begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.nfs.nfs3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|nfs
operator|.
name|nfs3
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
name|InetSocketAddress
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
name|DFSTestUtil
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
name|DistributedFileSystem
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
name|nfs
operator|.
name|conf
operator|.
name|NfsConfiguration
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NameNode
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
name|nfs
operator|.
name|nfs3
operator|.
name|FileHandle
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
name|nfs
operator|.
name|nfs3
operator|.
name|Nfs3Status
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
name|nfs
operator|.
name|nfs3
operator|.
name|response
operator|.
name|REMOVE3Response
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
name|oncrpc
operator|.
name|XDR
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
name|oncrpc
operator|.
name|security
operator|.
name|SecurityHandler
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
name|DefaultImpersonationProvider
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
name|AfterClass
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
name|BeforeClass
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

begin_class
DECL|class|TestClientAccessPrivilege
specifier|public
class|class
name|TestClientAccessPrivilege
block|{
DECL|field|cluster
specifier|static
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|config
specifier|static
name|NfsConfiguration
name|config
init|=
operator|new
name|NfsConfiguration
argument_list|()
decl_stmt|;
DECL|field|hdfs
specifier|static
name|DistributedFileSystem
name|hdfs
decl_stmt|;
DECL|field|nn
specifier|static
name|NameNode
name|nn
decl_stmt|;
DECL|field|testdir
specifier|static
name|String
name|testdir
init|=
literal|"/tmp"
decl_stmt|;
DECL|field|securityHandler
specifier|static
name|SecurityHandler
name|securityHandler
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|currentUser
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
decl_stmt|;
name|config
operator|.
name|set
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getTestProvider
argument_list|()
operator|.
name|getProxySuperuserGroupConfKey
argument_list|(
name|currentUser
argument_list|)
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getTestProvider
argument_list|()
operator|.
name|getProxySuperuserIpConfKey
argument_list|(
name|currentUser
argument_list|)
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|ProxyUsers
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|config
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|hdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|nn
operator|=
name|cluster
operator|.
name|getNameNode
argument_list|()
expr_stmt|;
comment|// Use ephemeral port in case tests are running in parallel
name|config
operator|.
name|setInt
argument_list|(
literal|"nfs3.mountd.port"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|config
operator|.
name|setInt
argument_list|(
literal|"nfs3.server.port"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|securityHandler
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|SecurityHandler
operator|.
name|class
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|securityHandler
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
throws|throws
name|Exception
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
annotation|@
name|Before
DECL|method|createFiles ()
specifier|public
name|void
name|createFiles
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IOException
block|{
name|hdfs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|testdir
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|hdfs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|testdir
argument_list|)
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
operator|new
name|Path
argument_list|(
name|testdir
operator|+
literal|"/f1"
argument_list|)
argument_list|,
literal|0
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testClientAccessPrivilegeForRemove ()
specifier|public
name|void
name|testClientAccessPrivilegeForRemove
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Configure ro access for nfs1 service
name|config
operator|.
name|set
argument_list|(
literal|"dfs.nfs.exports.allowed.hosts"
argument_list|,
literal|"* ro"
argument_list|)
expr_stmt|;
comment|// Start nfs
name|Nfs3
name|nfs
init|=
operator|new
name|Nfs3
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|nfs
operator|.
name|startServiceInternal
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|RpcProgramNfs3
name|nfsd
init|=
operator|(
name|RpcProgramNfs3
operator|)
name|nfs
operator|.
name|getRpcProgram
argument_list|()
decl_stmt|;
comment|// Create a remove request
name|HdfsFileStatus
name|status
init|=
name|nn
operator|.
name|getRpcServer
argument_list|()
operator|.
name|getFileInfo
argument_list|(
name|testdir
argument_list|)
decl_stmt|;
name|long
name|dirId
init|=
name|status
operator|.
name|getFileId
argument_list|()
decl_stmt|;
name|int
name|namenodeId
init|=
name|Nfs3Utils
operator|.
name|getNamenodeId
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|XDR
name|xdr_req
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|FileHandle
name|handle
init|=
operator|new
name|FileHandle
argument_list|(
name|dirId
argument_list|,
name|namenodeId
argument_list|)
decl_stmt|;
name|handle
operator|.
name|serialize
argument_list|(
name|xdr_req
argument_list|)
expr_stmt|;
name|xdr_req
operator|.
name|writeString
argument_list|(
literal|"f1"
argument_list|)
expr_stmt|;
comment|// Remove operation
name|REMOVE3Response
name|response
init|=
name|nfsd
operator|.
name|remove
argument_list|(
name|xdr_req
operator|.
name|asReadOnlyWrap
argument_list|()
argument_list|,
name|securityHandler
argument_list|,
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
literal|1234
argument_list|)
argument_list|)
decl_stmt|;
comment|// Assert on return code
name|assertEquals
argument_list|(
literal|"Incorrect return code"
argument_list|,
name|Nfs3Status
operator|.
name|NFS3ERR_ACCES
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

