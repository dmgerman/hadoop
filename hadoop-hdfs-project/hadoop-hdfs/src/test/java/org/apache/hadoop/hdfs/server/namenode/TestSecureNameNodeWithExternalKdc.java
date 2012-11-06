begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|assertNotNull
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
name|security
operator|.
name|PrivilegedExceptionAction
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
name|CommonConfigurationKeys
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
name|DFSConfigKeys
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
name|security
operator|.
name|UserGroupInformation
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
operator|.
name|AuthenticationMethod
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
name|security
operator|.
name|SecurityUtilTestHelper
operator|.
name|isExternalKdcRunning
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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

begin_comment
comment|/**  * This test brings up a MiniDFSCluster with 1 NameNode and 0  * DataNodes with kerberos authentication enabled using user-specified  * KDC, principals, and keytabs.  *  * To run, users must specify the following system properties:  *   externalKdc=true  *   java.security.krb5.conf  *   dfs.namenode.kerberos.principal  *   dfs.namenode.kerberos.internal.spnego.principal  *   dfs.namenode.keytab.file  *   user.principal (do not specify superuser!)  *   user.keytab  */
end_comment

begin_class
DECL|class|TestSecureNameNodeWithExternalKdc
specifier|public
class|class
name|TestSecureNameNodeWithExternalKdc
block|{
DECL|field|NUM_OF_DATANODES
specifier|final
specifier|static
specifier|private
name|int
name|NUM_OF_DATANODES
init|=
literal|0
decl_stmt|;
annotation|@
name|Before
DECL|method|testExternalKdcRunning ()
specifier|public
name|void
name|testExternalKdcRunning
parameter_list|()
block|{
comment|// Tests are skipped if external KDC is not running.
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|isExternalKdcRunning
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSecureNameNode ()
specifier|public
name|void
name|testSecureNameNode
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|nnPrincipal
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"dfs.namenode.kerberos.principal"
argument_list|)
decl_stmt|;
name|String
name|nnSpnegoPrincipal
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"dfs.namenode.kerberos.internal.spnego.principal"
argument_list|)
decl_stmt|;
name|String
name|nnKeyTab
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"dfs.namenode.keytab.file"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"NameNode principal was not specified"
argument_list|,
name|nnPrincipal
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"NameNode SPNEGO principal was not specified"
argument_list|,
name|nnSpnegoPrincipal
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"NameNode keytab was not specified"
argument_list|,
name|nnKeyTab
argument_list|)
expr_stmt|;
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
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_USER_NAME_KEY
argument_list|,
name|nnPrincipal
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_INTERNAL_SPNEGO_USER_NAME_KEY
argument_list|,
name|nnSpnegoPrincipal
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_KEYTAB_FILE_KEY
argument_list|,
name|nnKeyTab
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|NUM_OF_DATANODES
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
specifier|final
name|MiniDFSCluster
name|clusterRef
init|=
name|cluster
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|FileSystem
name|fsForCurrentUser
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|fsForCurrentUser
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp"
argument_list|)
argument_list|)
expr_stmt|;
name|fsForCurrentUser
operator|.
name|setPermission
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp"
argument_list|)
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|511
argument_list|)
argument_list|)
expr_stmt|;
comment|// The user specified should not be a superuser
name|String
name|userPrincipal
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.principal"
argument_list|)
decl_stmt|;
name|String
name|userKeyTab
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.keytab"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"User principal was not specified"
argument_list|,
name|userPrincipal
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"User keytab was not specified"
argument_list|,
name|userKeyTab
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|loginUserFromKeytabAndReturnUGI
argument_list|(
name|userPrincipal
argument_list|,
name|userKeyTab
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|FileSystem
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FileSystem
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|clusterRef
operator|.
name|getFileSystem
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/users"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"User must not be allowed to write in /"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|expected
parameter_list|)
block|{       }
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/alpha"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|fs
operator|.
name|listStatus
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AuthenticationMethod
operator|.
name|KERBEROS
argument_list|,
name|ugi
operator|.
name|getAuthenticationMethod
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

