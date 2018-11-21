begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
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
name|federation
operator|.
name|router
package|;
end_package

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
name|protocol
operator|.
name|BlockStoragePolicy
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
name|ClientProtocol
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
name|federation
operator|.
name|MiniRouterDFSCluster
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
name|federation
operator|.
name|RouterConfigBuilder
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
name|federation
operator|.
name|metrics
operator|.
name|NamenodeBeanMetrics
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
name|AfterClass
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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

begin_comment
comment|/**  * Test StoragePolicySatisfy through router rpc calls.  */
end_comment

begin_class
DECL|class|TestRouterRpcStoragePolicySatisfier
specifier|public
class|class
name|TestRouterRpcStoragePolicySatisfier
block|{
comment|/** Federated HDFS cluster. */
DECL|field|cluster
specifier|private
specifier|static
name|MiniRouterDFSCluster
name|cluster
decl_stmt|;
comment|/** Client interface to the Router. */
DECL|field|routerProtocol
specifier|private
specifier|static
name|ClientProtocol
name|routerProtocol
decl_stmt|;
comment|/** Filesystem interface to the Router. */
DECL|field|routerFS
specifier|private
specifier|static
name|FileSystem
name|routerFS
decl_stmt|;
comment|/** Filesystem interface to the Namenode. */
DECL|field|nnFS
specifier|private
specifier|static
name|FileSystem
name|nnFS
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|globalSetUp ()
specifier|public
specifier|static
name|void
name|globalSetUp
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|=
operator|new
name|MiniRouterDFSCluster
argument_list|(
literal|false
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Set storage types for the cluster
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
operator|.
name|setStorageTypes
argument_list|(
name|newtypes
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
name|cluster
operator|.
name|getNamenodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_STORAGE_POLICY_SATISFIER_MODE_KEY
argument_list|,
name|HdfsConstants
operator|.
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
name|cluster
operator|.
name|addNamenodeOverrides
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|setNumDatanodesPerNameservice
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// Start NNs and DNs and wait until ready
name|cluster
operator|.
name|startCluster
argument_list|()
expr_stmt|;
comment|// Start routers with only an RPC service
name|Configuration
name|routerConf
init|=
operator|new
name|RouterConfigBuilder
argument_list|()
operator|.
name|metrics
argument_list|()
operator|.
name|rpc
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// We decrease the DN cache times to make the test faster
name|routerConf
operator|.
name|setTimeDuration
argument_list|(
name|RBFConfigKeys
operator|.
name|DN_REPORT_CACHE_EXPIRE
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|addRouterOverrides
argument_list|(
name|routerConf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|startRouters
argument_list|()
expr_stmt|;
comment|// Register and verify all NNs with all routers
name|cluster
operator|.
name|registerNamenodes
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitNamenodeRegistration
argument_list|()
expr_stmt|;
comment|// Create mock locations
name|cluster
operator|.
name|installMockLocations
argument_list|()
expr_stmt|;
comment|// Random router for this test
name|MiniRouterDFSCluster
operator|.
name|RouterContext
name|rndRouter
init|=
name|cluster
operator|.
name|getRandomRouter
argument_list|()
decl_stmt|;
name|routerProtocol
operator|=
name|rndRouter
operator|.
name|getClient
argument_list|()
operator|.
name|getNamenode
argument_list|()
expr_stmt|;
name|routerFS
operator|=
name|rndRouter
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|nnFS
operator|=
name|cluster
operator|.
name|getNamenodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
name|HdfsConstants
operator|.
name|StoragePolicySatisfierMode
operator|.
name|EXTERNAL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStoragePolicySatisfier ()
specifier|public
name|void
name|testStoragePolicySatisfier
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
name|short
name|repl
init|=
literal|1
decl_stmt|;
name|int
name|size
init|=
literal|32
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|routerFS
argument_list|,
operator|new
name|Path
argument_list|(
name|file
argument_list|)
argument_list|,
name|size
argument_list|,
name|repl
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Varify storage type is DISK
name|DFSTestUtil
operator|.
name|waitExpectedStorageType
argument_list|(
name|file
argument_list|,
name|StorageType
operator|.
name|DISK
argument_list|,
literal|1
argument_list|,
literal|20000
argument_list|,
operator|(
name|DistributedFileSystem
operator|)
name|routerFS
argument_list|)
expr_stmt|;
comment|// Set storage policy as COLD
name|routerProtocol
operator|.
name|setStoragePolicy
argument_list|(
name|file
argument_list|,
name|HdfsConstants
operator|.
name|COLD_STORAGE_POLICY_NAME
argument_list|)
expr_stmt|;
comment|// Verify storage policy is set properly
name|BlockStoragePolicy
name|storagePolicy
init|=
name|routerProtocol
operator|.
name|getStoragePolicy
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HdfsConstants
operator|.
name|COLD_STORAGE_POLICY_NAME
argument_list|,
name|storagePolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Invoke satisfy storage policy
name|routerProtocol
operator|.
name|satisfyStoragePolicy
argument_list|(
name|file
argument_list|)
expr_stmt|;
comment|// Verify storage type is ARCHIVE
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
literal|20000
argument_list|,
operator|(
name|DistributedFileSystem
operator|)
name|routerFS
argument_list|)
expr_stmt|;
comment|// Verify storage type via NN
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
literal|20000
argument_list|,
operator|(
name|DistributedFileSystem
operator|)
name|nnFS
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

