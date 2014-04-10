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
name|InetAddress
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
name|protocol
operator|.
name|HdfsFileStatus
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
name|security
operator|.
name|UserGroupInformation
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
DECL|field|foundPermission
specifier|static
name|short
name|foundPermission
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
name|logCount
operator|++
expr_stmt|;
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

