begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
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
name|StorageType
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
name|HdfsConstants
operator|.
name|StoragePolicySatisfierMode
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
name|balancer
operator|.
name|NameNodeConnector
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
name|common
operator|.
name|HdfsServerConstants
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
name|sps
operator|.
name|Context
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
name|sps
operator|.
name|StoragePolicySatisfier
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
name|sps
operator|.
name|ExternalSPSContext
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
comment|/**  * Test StoragePolicySatisfy admin commands.  */
end_comment

begin_class
DECL|class|TestStoragePolicySatisfyAdminCommands
specifier|public
class|class
name|TestStoragePolicySatisfyAdminCommands
block|{
DECL|field|REPL
specifier|private
specifier|static
specifier|final
name|short
name|REPL
init|=
literal|1
decl_stmt|;
DECL|field|SIZE
specifier|private
specifier|static
specifier|final
name|int
name|SIZE
init|=
literal|128
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|dfs
specifier|private
name|DistributedFileSystem
name|dfs
init|=
literal|null
decl_stmt|;
DECL|field|externalSps
specifier|private
name|StoragePolicySatisfier
name|externalSps
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
DECL|method|clusterSetUp ()
specifier|public
name|void
name|clusterSetUp
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_STORAGE_POLICY_SATISFIER_MODE_KEY
argument_list|,
name|StoragePolicySatisfierMode
operator|.
name|EXTERNAL
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Reduced refresh cycle to update latest datanodes.
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_SPS_DATANODE_CACHE_REFRESH_INTERVAL_MS
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|StorageType
index|[]
index|[]
name|newtypes
init|=
operator|new
name|StorageType
index|[]
index|[]
block|{
block|{
name|StorageType
operator|.
name|ARCHIVE
block|,
name|StorageType
operator|.
name|DISK
block|}
block|}
decl_stmt|;
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
name|REPL
argument_list|)
operator|.
name|storageTypes
argument_list|(
name|newtypes
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
name|dfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|NameNodeConnector
name|nnc
init|=
name|DFSTestUtil
operator|.
name|getNameNodeConnector
argument_list|(
name|conf
argument_list|,
name|HdfsServerConstants
operator|.
name|MOVER_ID_PATH
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|StoragePolicySatisfier
name|externalSps
init|=
operator|new
name|StoragePolicySatisfier
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Context
name|externalCtxt
init|=
operator|new
name|ExternalSPSContext
argument_list|(
name|externalSps
argument_list|,
name|nnc
argument_list|)
decl_stmt|;
name|externalSps
operator|.
name|init
argument_list|(
name|externalCtxt
argument_list|)
expr_stmt|;
name|externalSps
operator|.
name|start
argument_list|(
name|StoragePolicySatisfierMode
operator|.
name|EXTERNAL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|clusterShutdown ()
specifier|public
name|void
name|clusterShutdown
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|dfs
operator|!=
literal|null
condition|)
block|{
name|dfs
operator|.
name|close
argument_list|()
expr_stmt|;
name|dfs
operator|=
literal|null
expr_stmt|;
block|}
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
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|externalSps
operator|!=
literal|null
condition|)
block|{
name|externalSps
operator|.
name|stopGracefully
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testStoragePolicySatisfierCommand ()
specifier|public
name|void
name|testStoragePolicySatisfierCommand
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|file
init|=
literal|"/testStoragePolicySatisfierCommand"
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
operator|new
name|Path
argument_list|(
name|file
argument_list|)
argument_list|,
name|SIZE
argument_list|,
name|REPL
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|StoragePolicyAdmin
name|admin
init|=
operator|new
name|StoragePolicyAdmin
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-getStoragePolicy -path "
operator|+
name|file
argument_list|,
literal|0
argument_list|,
literal|"The storage policy of "
operator|+
name|file
operator|+
literal|" is unspecified"
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-setStoragePolicy -path "
operator|+
name|file
operator|+
literal|" -policy COLD"
argument_list|,
literal|0
argument_list|,
literal|"Set storage policy COLD on "
operator|+
name|file
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-satisfyStoragePolicy -path "
operator|+
name|file
argument_list|,
literal|0
argument_list|,
literal|"Scheduled blocks to move based on the current storage policy on "
operator|+
name|file
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitExpectedStorageType
argument_list|(
name|file
argument_list|,
name|StorageType
operator|.
name|ARCHIVE
argument_list|,
literal|1
argument_list|,
literal|30000
argument_list|,
name|dfs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testStoragePolicySatisfierCommandWithURI ()
specifier|public
name|void
name|testStoragePolicySatisfierCommandWithURI
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|file
init|=
literal|"/testStoragePolicySatisfierCommandURI"
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
operator|new
name|Path
argument_list|(
name|file
argument_list|)
argument_list|,
name|SIZE
argument_list|,
name|REPL
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|StoragePolicyAdmin
name|admin
init|=
operator|new
name|StoragePolicyAdmin
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-getStoragePolicy -path "
operator|+
name|file
argument_list|,
literal|0
argument_list|,
literal|"The storage policy of "
operator|+
name|file
operator|+
literal|" is unspecified"
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-setStoragePolicy -path "
operator|+
name|file
operator|+
literal|" -policy COLD"
argument_list|,
literal|0
argument_list|,
literal|"Set storage policy COLD on "
operator|+
name|file
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|toolRun
argument_list|(
name|admin
argument_list|,
literal|"-satisfyStoragePolicy -path "
operator|+
name|dfs
operator|.
name|getUri
argument_list|()
operator|+
name|file
argument_list|,
literal|0
argument_list|,
literal|"Scheduled blocks to move based on the current storage policy on "
operator|+
name|dfs
operator|.
name|getUri
argument_list|()
operator|+
name|file
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitExpectedStorageType
argument_list|(
name|file
argument_list|,
name|StorageType
operator|.
name|ARCHIVE
argument_list|,
literal|1
argument_list|,
literal|30000
argument_list|,
name|dfs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

