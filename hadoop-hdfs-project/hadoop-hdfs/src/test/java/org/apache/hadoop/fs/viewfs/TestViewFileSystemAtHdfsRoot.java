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

begin_comment
comment|/**  * Make sure that ViewFileSystem works when the root of an FS is mounted to a  * ViewFileSystem mount point.  */
end_comment

begin_class
DECL|class|TestViewFileSystemAtHdfsRoot
specifier|public
class|class
name|TestViewFileSystemAtHdfsRoot
extends|extends
name|ViewFileSystemBaseTest
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
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
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|clusterShutdownAtEnd ()
specifier|public
specifier|static
name|void
name|clusterShutdownAtEnd
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
name|fsTarget
operator|=
name|fHdfs
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
comment|/**    * Override this so that we don't set the targetTestRoot to any path under the    * root of the FS, and so that we don't try to delete the test dir, but rather    * only its contents.    */
annotation|@
name|Override
DECL|method|initializeTargetTestRoot ()
name|void
name|initializeTargetTestRoot
parameter_list|()
throws|throws
name|IOException
block|{
name|targetTestRoot
operator|=
name|fHdfs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|FileStatus
name|status
range|:
name|fHdfs
operator|.
name|listStatus
argument_list|(
name|targetTestRoot
argument_list|)
control|)
block|{
name|fHdfs
operator|.
name|delete
argument_list|(
name|status
operator|.
name|getPath
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getExpectedDelegationTokenCount ()
name|int
name|getExpectedDelegationTokenCount
parameter_list|()
block|{
return|return
literal|8
return|;
block|}
annotation|@
name|Override
DECL|method|getExpectedDelegationTokenCountWithCredentials ()
name|int
name|getExpectedDelegationTokenCountWithCredentials
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
block|}
end_class

end_unit

