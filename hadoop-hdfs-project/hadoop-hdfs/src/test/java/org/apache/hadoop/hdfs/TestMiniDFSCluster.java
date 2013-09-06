begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
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
name|test
operator|.
name|PathUtils
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
comment|/**  * Tests MiniDFS cluster setup/teardown and isolation.  * Every instance is brought up with a new data dir, to ensure that  * shutdown work in background threads don't interfere with bringing up  * the new cluster.  */
end_comment

begin_class
DECL|class|TestMiniDFSCluster
specifier|public
class|class
name|TestMiniDFSCluster
block|{
DECL|field|CLUSTER_1
specifier|private
specifier|static
specifier|final
name|String
name|CLUSTER_1
init|=
literal|"cluster1"
decl_stmt|;
DECL|field|CLUSTER_2
specifier|private
specifier|static
specifier|final
name|String
name|CLUSTER_2
init|=
literal|"cluster2"
decl_stmt|;
DECL|field|CLUSTER_3
specifier|private
specifier|static
specifier|final
name|String
name|CLUSTER_3
init|=
literal|"cluster3"
decl_stmt|;
DECL|field|CLUSTER_4
specifier|private
specifier|static
specifier|final
name|String
name|CLUSTER_4
init|=
literal|"cluster4"
decl_stmt|;
DECL|field|CLUSTER_5
specifier|private
specifier|static
specifier|final
name|String
name|CLUSTER_5
init|=
literal|"cluster5"
decl_stmt|;
DECL|field|testDataPath
specifier|protected
name|File
name|testDataPath
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|testDataPath
operator|=
operator|new
name|File
argument_list|(
name|PathUtils
operator|.
name|getTestDir
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|,
literal|"miniclusters"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that without system properties the cluster still comes up, provided    * the configuration is set    *    * @throws Throwable on a failure    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|100000
argument_list|)
DECL|method|testClusterWithoutSystemProperties ()
specifier|public
name|void
name|testClusterWithoutSystemProperties
parameter_list|()
throws|throws
name|Throwable
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|MiniDFSCluster
operator|.
name|PROP_TEST_BUILD_DATA
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|File
name|testDataCluster1
init|=
operator|new
name|File
argument_list|(
name|testDataPath
argument_list|,
name|CLUSTER_1
argument_list|)
decl_stmt|;
name|String
name|c1Path
init|=
name|testDataCluster1
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MiniDFSCluster
operator|.
name|HDFS_MINIDFS_BASEDIR
argument_list|,
name|c1Path
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
name|assertEquals
argument_list|(
operator|new
name|File
argument_list|(
name|c1Path
operator|+
literal|"/data"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|cluster
operator|.
name|getDataDirectory
argument_list|()
argument_list|)
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
comment|/**    * Bring up two clusters and assert that they are in different directories.    * @throws Throwable on a failure    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|100000
argument_list|)
DECL|method|testDualClusters ()
specifier|public
name|void
name|testDualClusters
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|testDataCluster2
init|=
operator|new
name|File
argument_list|(
name|testDataPath
argument_list|,
name|CLUSTER_2
argument_list|)
decl_stmt|;
name|File
name|testDataCluster3
init|=
operator|new
name|File
argument_list|(
name|testDataPath
argument_list|,
name|CLUSTER_3
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|String
name|c2Path
init|=
name|testDataCluster2
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MiniDFSCluster
operator|.
name|HDFS_MINIDFS_BASEDIR
argument_list|,
name|c2Path
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster2
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
name|MiniDFSCluster
name|cluster3
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|dataDir2
init|=
name|cluster2
operator|.
name|getDataDirectory
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|File
argument_list|(
name|c2Path
operator|+
literal|"/data"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|dataDir2
argument_list|)
argument_list|)
expr_stmt|;
comment|//change the data dir
name|conf
operator|.
name|set
argument_list|(
name|MiniDFSCluster
operator|.
name|HDFS_MINIDFS_BASEDIR
argument_list|,
name|testDataCluster3
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|MiniDFSCluster
operator|.
name|Builder
name|builder
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|cluster3
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|String
name|dataDir3
init|=
name|cluster3
operator|.
name|getDataDirectory
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Clusters are bound to the same directory: "
operator|+
name|dataDir2
argument_list|,
operator|!
name|dataDir2
operator|.
name|equals
argument_list|(
name|dataDir3
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|MiniDFSCluster
operator|.
name|shutdownCluster
argument_list|(
name|cluster3
argument_list|)
expr_stmt|;
name|MiniDFSCluster
operator|.
name|shutdownCluster
argument_list|(
name|cluster2
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|100000
argument_list|)
DECL|method|testIsClusterUpAfterShutdown ()
specifier|public
name|void
name|testIsClusterUpAfterShutdown
parameter_list|()
throws|throws
name|Throwable
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|File
name|testDataCluster4
init|=
operator|new
name|File
argument_list|(
name|testDataPath
argument_list|,
name|CLUSTER_4
argument_list|)
decl_stmt|;
name|String
name|c4Path
init|=
name|testDataCluster4
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MiniDFSCluster
operator|.
name|HDFS_MINIDFS_BASEDIR
argument_list|,
name|c4Path
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster4
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
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster4
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|dfs
operator|.
name|setSafeMode
argument_list|(
name|HdfsConstants
operator|.
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|)
expr_stmt|;
name|cluster4
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
while|while
condition|(
name|cluster4
operator|.
name|isClusterUp
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** MiniDFSCluster should not clobber dfs.datanode.hostname if requested */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|100000
argument_list|)
DECL|method|testClusterSetDatanodeHostname ()
specifier|public
name|void
name|testClusterSetDatanodeHostname
parameter_list|()
throws|throws
name|Throwable
block|{
name|assumeTrue
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Linux"
argument_list|)
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
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HOST_NAME_KEY
argument_list|,
literal|"MYHOST"
argument_list|)
expr_stmt|;
name|File
name|testDataCluster5
init|=
operator|new
name|File
argument_list|(
name|testDataPath
argument_list|,
name|CLUSTER_5
argument_list|)
decl_stmt|;
name|String
name|c5Path
init|=
name|testDataCluster5
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MiniDFSCluster
operator|.
name|HDFS_MINIDFS_BASEDIR
argument_list|,
name|c5Path
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster5
init|=
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
literal|1
argument_list|)
operator|.
name|checkDataNodeHostConfig
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"DataNode hostname config not respected"
argument_list|,
literal|"MYHOST"
argument_list|,
name|cluster5
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDatanodeId
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|MiniDFSCluster
operator|.
name|shutdownCluster
argument_list|(
name|cluster5
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

