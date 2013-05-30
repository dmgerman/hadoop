begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.viewfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|viewfs
package|;
end_package

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
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
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
name|FileSystemTestHelper
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
name|MiniDFSNNTopology
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
name|After
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

begin_class
DECL|class|TestViewFileSystemHdfs
specifier|public
class|class
name|TestViewFileSystemHdfs
extends|extends
name|ViewFileSystemBaseTest
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|defaultWorkingDirectory
specifier|private
specifier|static
name|Path
name|defaultWorkingDirectory
decl_stmt|;
DECL|field|defaultWorkingDirectory2
specifier|private
specifier|static
name|Path
name|defaultWorkingDirectory2
decl_stmt|;
DECL|field|CONF
specifier|private
specifier|static
name|Configuration
name|CONF
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|fHdfs
specifier|private
specifier|static
name|FileSystem
name|fHdfs
decl_stmt|;
DECL|field|fHdfs2
specifier|private
specifier|static
name|FileSystem
name|fHdfs2
decl_stmt|;
DECL|field|fsTarget2
specifier|private
name|FileSystem
name|fsTarget2
decl_stmt|;
DECL|field|targetTestRoot2
name|Path
name|targetTestRoot2
decl_stmt|;
annotation|@
name|Override
DECL|method|createFileSystemHelper ()
specifier|protected
name|FileSystemTestHelper
name|createFileSystemHelper
parameter_list|()
block|{
return|return
operator|new
name|FileSystemTestHelper
argument_list|(
literal|"/tmp/TestViewFileSystemHdfs"
argument_list|)
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|clusterSetupAtBegining ()
specifier|public
specifier|static
name|void
name|clusterSetupAtBegining
parameter_list|()
throws|throws
name|IOException
throws|,
name|LoginException
throws|,
name|URISyntaxException
block|{
name|SupportsBlocks
operator|=
literal|true
expr_stmt|;
name|CONF
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DELEGATION_TOKEN_ALWAYS_USE_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|CONF
argument_list|)
operator|.
name|nnTopology
argument_list|(
name|MiniDFSNNTopology
operator|.
name|simpleFederatedTopology
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|2
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
name|fHdfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fHdfs2
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|defaultWorkingDirectory
operator|=
name|fHdfs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/user/"
operator|+
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|defaultWorkingDirectory2
operator|=
name|fHdfs2
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/user/"
operator|+
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fHdfs
operator|.
name|mkdirs
argument_list|(
name|defaultWorkingDirectory
argument_list|)
expr_stmt|;
name|fHdfs2
operator|.
name|mkdirs
argument_list|(
name|defaultWorkingDirectory2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|ClusterShutdownAtEnd ()
specifier|public
specifier|static
name|void
name|ClusterShutdownAtEnd
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create the test root on local_fs
name|fsTarget
operator|=
name|fHdfs
expr_stmt|;
name|fsTarget2
operator|=
name|fHdfs2
expr_stmt|;
name|targetTestRoot2
operator|=
operator|new
name|FileSystemTestHelper
argument_list|()
operator|.
name|getAbsoluteTestRootPath
argument_list|(
name|fsTarget2
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setupMountPoints ()
name|void
name|setupMountPoints
parameter_list|()
block|{
name|super
operator|.
name|setupMountPoints
argument_list|()
expr_stmt|;
name|ConfigUtil
operator|.
name|addLink
argument_list|(
name|conf
argument_list|,
literal|"/mountOnNn2"
argument_list|,
operator|new
name|Path
argument_list|(
name|targetTestRoot2
argument_list|,
literal|"mountOnNn2"
argument_list|)
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Overriden test helper methods - changed values based on hdfs and the
comment|// additional mount.
annotation|@
name|Override
DECL|method|getExpectedDirPaths ()
name|int
name|getExpectedDirPaths
parameter_list|()
block|{
return|return
literal|8
return|;
block|}
annotation|@
name|Override
DECL|method|getExpectedMountPoints ()
name|int
name|getExpectedMountPoints
parameter_list|()
block|{
return|return
literal|9
return|;
block|}
annotation|@
name|Override
DECL|method|getExpectedDelegationTokenCount ()
name|int
name|getExpectedDelegationTokenCount
parameter_list|()
block|{
return|return
literal|2
return|;
comment|// Mount points to 2 unique hdfs
block|}
annotation|@
name|Override
DECL|method|getExpectedDelegationTokenCountWithCredentials ()
name|int
name|getExpectedDelegationTokenCountWithCredentials
parameter_list|()
block|{
return|return
literal|2
return|;
block|}
block|}
end_class

end_unit

