begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|permission
operator|.
name|AclEntry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|permission
operator|.
name|FsPermission
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|HdfsConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|MiniDFSCluster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|top
operator|.
name|TopAuditLogger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
operator|.
name|resources
operator|.
name|GetOpParam
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
operator|.
name|RemoteException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
operator|.
name|NetUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|AccessControlException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|authorize
operator|.
name|ProxyServers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|authorize
operator|.
name|ProxyUsers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_ACLS_ENABLED_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_AUDIT_LOGGERS_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|NNTOP_ENABLED_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doThrow
import|;
end_import

begin_comment
comment|/**  * Tests for the {@link AuditLogger} custom audit logging interface.  */
end_comment

begin_class
DECL|class|TestAuditLogger
specifier|public
class|class
name|TestAuditLogger
block|{
DECL|field|TEST_PERMISSION
specifier|private
specifier|static
specifier|final
name|short
name|TEST_PERMISSION
init|=
operator|(
name|short
operator|)
literal|0654
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|DummyAuditLogger
operator|.
name|initialized
operator|=
literal|false
expr_stmt|;
name|DummyAuditLogger
operator|.
name|logCount
operator|=
literal|0
expr_stmt|;
name|DummyAuditLogger
operator|.
name|remoteAddr
operator|=
literal|null
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|ProxyUsers
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that AuditLogger works as expected.    */
annotation|@
name|Test
DECL|method|testAuditLogger ()
specifier|public
name|void
name|testAuditLogger
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_AUDIT_LOGGERS_KEY
argument_list|,
name|DummyAuditLogger
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|DummyAuditLogger
operator|.
name|initialized
argument_list|)
expr_stmt|;
name|DummyAuditLogger
operator|.
name|resetLogCount
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|fs
operator|.
name|setTimes
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|time
argument_list|,
name|time
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DummyAuditLogger
operator|.
name|logCount
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Tests that TopAuditLogger can be disabled    */
annotation|@
name|Test
DECL|method|testDisableTopAuditLogger ()
specifier|public
name|void
name|testDisableTopAuditLogger
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|NNTOP_ENABLED_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|AuditLogger
argument_list|>
name|auditLoggers
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getAuditLoggers
argument_list|()
decl_stmt|;
for|for
control|(
name|AuditLogger
name|auditLogger
range|:
name|auditLoggers
control|)
block|{
name|assertFalse
argument_list|(
literal|"top audit logger is still hooked in after it is disabled"
argument_list|,
name|auditLogger
operator|instanceof
name|TopAuditLogger
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWebHdfsAuditLogger ()
specifier|public
name|void
name|testWebHdfsAuditLogger
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_AUDIT_LOGGERS_KEY
argument_list|,
name|DummyAuditLogger
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|GetOpParam
operator|.
name|Op
name|op
init|=
name|GetOpParam
operator|.
name|Op
operator|.
name|GETFILESTATUS
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|DummyAuditLogger
operator|.
name|initialized
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"http"
argument_list|,
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getHttpAddress
argument_list|()
argument_list|)
argument_list|,
literal|"/webhdfs/v1/"
argument_list|,
name|op
operator|.
name|toQueryString
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// non-proxy request
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|uri
operator|.
name|toURL
argument_list|()
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setRequestMethod
argument_list|(
name|op
operator|.
name|getType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|conn
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DummyAuditLogger
operator|.
name|logCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|DummyAuditLogger
operator|.
name|remoteAddr
argument_list|)
expr_stmt|;
comment|// non-trusted proxied request
name|conn
operator|=
operator|(
name|HttpURLConnection
operator|)
name|uri
operator|.
name|toURL
argument_list|()
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|conn
operator|.
name|setRequestMethod
argument_list|(
name|op
operator|.
name|getType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setRequestProperty
argument_list|(
literal|"X-Forwarded-For"
argument_list|,
literal|"1.1.1.1"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|conn
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|DummyAuditLogger
operator|.
name|logCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|DummyAuditLogger
operator|.
name|remoteAddr
argument_list|)
expr_stmt|;
comment|// trusted proxied request
name|conf
operator|.
name|set
argument_list|(
name|ProxyServers
operator|.
name|CONF_HADOOP_PROXYSERVERS
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|ProxyUsers
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conn
operator|=
operator|(
name|HttpURLConnection
operator|)
name|uri
operator|.
name|toURL
argument_list|()
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|conn
operator|.
name|setRequestMethod
argument_list|(
name|op
operator|.
name|getType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setRequestProperty
argument_list|(
literal|"X-Forwarded-For"
argument_list|,
literal|"1.1.1.1"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|conn
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|DummyAuditLogger
operator|.
name|logCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.1.1.1"
argument_list|,
name|DummyAuditLogger
operator|.
name|remoteAddr
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Minor test related to HADOOP-9155. Verify that during a    * FileSystem.setPermission() operation, the stat passed in during the    * logAuditEvent() call returns the new permission rather than the old    * permission.    */
annotation|@
name|Test
DECL|method|testAuditLoggerWithSetPermission ()
specifier|public
name|void
name|testAuditLoggerWithSetPermission
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_AUDIT_LOGGERS_KEY
argument_list|,
name|DummyAuditLogger
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|DummyAuditLogger
operator|.
name|initialized
argument_list|)
expr_stmt|;
name|DummyAuditLogger
operator|.
name|resetLogCount
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|setTimes
argument_list|(
name|p
argument_list|,
name|time
argument_list|,
name|time
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setPermission
argument_list|(
name|p
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|TEST_PERMISSION
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TEST_PERMISSION
argument_list|,
name|DummyAuditLogger
operator|.
name|foundPermission
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|DummyAuditLogger
operator|.
name|logCount
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAuditLogWithAclFailure ()
specifier|public
name|void
name|testAuditLogWithAclFailure
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFS_NAMENODE_ACLS_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_AUDIT_LOGGERS_KEY
argument_list|,
name|DummyAuditLogger
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
specifier|final
name|FSDirectory
name|dir
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
specifier|final
name|FSDirectory
name|mockedDir
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|AccessControlException
name|ex
init|=
operator|new
name|AccessControlException
argument_list|()
decl_stmt|;
name|doThrow
argument_list|(
name|ex
argument_list|)
operator|.
name|when
argument_list|(
name|mockedDir
argument_list|)
operator|.
name|getPermissionChecker
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|setFSDirectory
argument_list|(
name|mockedDir
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DummyAuditLogger
operator|.
name|initialized
argument_list|)
expr_stmt|;
name|DummyAuditLogger
operator|.
name|resetLogCount
argument_list|()
expr_stmt|;
specifier|final
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|AclEntry
argument_list|>
name|acls
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|getAclStatus
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|ignored
parameter_list|)
block|{}
try|try
block|{
name|fs
operator|.
name|setAcl
argument_list|(
name|p
argument_list|,
name|acls
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|ignored
parameter_list|)
block|{}
try|try
block|{
name|fs
operator|.
name|removeAcl
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|ignored
parameter_list|)
block|{}
try|try
block|{
name|fs
operator|.
name|removeDefaultAcl
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|ignored
parameter_list|)
block|{}
try|try
block|{
name|fs
operator|.
name|removeAclEntries
argument_list|(
name|p
argument_list|,
name|acls
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|ignored
parameter_list|)
block|{}
try|try
block|{
name|fs
operator|.
name|modifyAclEntries
argument_list|(
name|p
argument_list|,
name|acls
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|ignored
parameter_list|)
block|{}
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|DummyAuditLogger
operator|.
name|logCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|DummyAuditLogger
operator|.
name|unsuccessfulCount
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Tests that a broken audit logger causes requests to fail.    */
annotation|@
name|Test
DECL|method|testBrokenLogger ()
specifier|public
name|void
name|testBrokenLogger
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_AUDIT_LOGGERS_KEY
argument_list|,
name|BrokenAuditLogger
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|fs
operator|.
name|setTimes
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|time
argument_list|,
name|time
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception due to broken audit logger."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|re
parameter_list|)
block|{
comment|// Expected.
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|DummyAuditLogger
specifier|public
specifier|static
class|class
name|DummyAuditLogger
implements|implements
name|AuditLogger
block|{
DECL|field|initialized
specifier|static
name|boolean
name|initialized
decl_stmt|;
DECL|field|logCount
specifier|static
name|int
name|logCount
decl_stmt|;
DECL|field|unsuccessfulCount
specifier|static
name|int
name|unsuccessfulCount
decl_stmt|;
DECL|field|foundPermission
specifier|static
name|short
name|foundPermission
decl_stmt|;
DECL|field|remoteAddr
specifier|static
name|String
name|remoteAddr
decl_stmt|;
DECL|method|initialize (Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|initialized
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|resetLogCount ()
specifier|public
specifier|static
name|void
name|resetLogCount
parameter_list|()
block|{
name|logCount
operator|=
literal|0
expr_stmt|;
name|unsuccessfulCount
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|logAuditEvent (boolean succeeded, String userName, InetAddress addr, String cmd, String src, String dst, FileStatus stat)
specifier|public
name|void
name|logAuditEvent
parameter_list|(
name|boolean
name|succeeded
parameter_list|,
name|String
name|userName
parameter_list|,
name|InetAddress
name|addr
parameter_list|,
name|String
name|cmd
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dst
parameter_list|,
name|FileStatus
name|stat
parameter_list|)
block|{
name|remoteAddr
operator|=
name|addr
operator|.
name|getHostAddress
argument_list|()
expr_stmt|;
name|logCount
operator|++
expr_stmt|;
if|if
condition|(
operator|!
name|succeeded
condition|)
block|{
name|unsuccessfulCount
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|stat
operator|!=
literal|null
condition|)
block|{
name|foundPermission
operator|=
name|stat
operator|.
name|getPermission
argument_list|()
operator|.
name|toShort
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|class|BrokenAuditLogger
specifier|public
specifier|static
class|class
name|BrokenAuditLogger
implements|implements
name|AuditLogger
block|{
DECL|method|initialize (Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// No op.
block|}
DECL|method|logAuditEvent (boolean succeeded, String userName, InetAddress addr, String cmd, String src, String dst, FileStatus stat)
specifier|public
name|void
name|logAuditEvent
parameter_list|(
name|boolean
name|succeeded
parameter_list|,
name|String
name|userName
parameter_list|,
name|InetAddress
name|addr
parameter_list|,
name|String
name|cmd
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dst
parameter_list|,
name|FileStatus
name|stat
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"uh oh"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

