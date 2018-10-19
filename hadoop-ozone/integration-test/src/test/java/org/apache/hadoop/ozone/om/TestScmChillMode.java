begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.ââSee the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.ââThe ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.ââYou may obtain a copy of the License at  *  * ââââ http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
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
name|util
operator|.
name|concurrent
operator|.
name|AtomicDouble
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|RandomStringUtils
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
name|hdds
operator|.
name|HddsConfigKeys
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|LifeCycleEvent
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|ReplicationFactor
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|ReplicationType
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|SCMContainerManager
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|ContainerInfo
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
name|hdds
operator|.
name|scm
operator|.
name|events
operator|.
name|SCMEvents
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
name|hdds
operator|.
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
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
name|hdds
operator|.
name|scm
operator|.
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
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
name|hdds
operator|.
name|scm
operator|.
name|server
operator|.
name|SCMChillModeManager
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
name|hdds
operator|.
name|scm
operator|.
name|server
operator|.
name|SCMClientProtocolServer
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
name|hdds
operator|.
name|scm
operator|.
name|server
operator|.
name|StorageContainerManager
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
name|ozone
operator|.
name|HddsDatanodeService
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
name|ozone
operator|.
name|MiniOzoneCluster
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
name|ozone
operator|.
name|MiniOzoneClusterImpl
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|ozone
operator|.
name|TestStorageContainerManagerHelper
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
name|ozone
operator|.
name|om
operator|.
name|exceptions
operator|.
name|OMException
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmBucketInfo
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmKeyArgs
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmKeyInfo
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmVolumeArgs
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
name|GenericTestUtils
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
name|LambdaTestUtils
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
name|util
operator|.
name|Time
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
name|Assert
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
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|Timeout
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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

begin_comment
comment|/**  * Test Ozone Manager operation in distributed handler scenario.  */
end_comment

begin_class
DECL|class|TestScmChillMode
specifier|public
class|class
name|TestScmChillMode
block|{
DECL|field|LOG
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestScmChillMode
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|builder
specifier|private
specifier|static
name|MiniOzoneCluster
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|om
specifier|private
specifier|static
name|OzoneManager
name|om
decl_stmt|;
specifier|private
specifier|static
name|StorageContainerLocationProtocolClientSideTranslatorPB
DECL|field|storageContainerLocationClient
name|storageContainerLocationClient
decl_stmt|;
annotation|@
name|Rule
DECL|field|timeout
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|1000
operator|*
literal|200
argument_list|)
decl_stmt|;
comment|/**    * Create a MiniDFSCluster for testing.    *<p>    * Ozone is made active by setting OZONE_ENABLED = true and    * OZONE_HANDLER_TYPE_KEY = "distributed"    *    * @throws IOException    */
annotation|@
name|Before
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|builder
operator|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|setHbInterval
argument_list|(
literal|1000
argument_list|)
operator|.
name|setHbProcessorInterval
argument_list|(
literal|500
argument_list|)
operator|.
name|setStartDataNodes
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|cluster
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|startHddsDatanodes
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
name|om
operator|=
name|cluster
operator|.
name|getOzoneManager
argument_list|()
expr_stmt|;
name|storageContainerLocationClient
operator|=
name|cluster
operator|.
name|getStorageContainerLocationClient
argument_list|()
expr_stmt|;
block|}
comment|/**    * Shutdown MiniDFSCluster.    */
annotation|@
name|After
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// do nothing.
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testChillModeOperations ()
specifier|public
name|void
name|testChillModeOperations
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicReference
argument_list|<
name|MiniOzoneCluster
argument_list|>
name|miniCluster
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Create {numKeys} random names keys.
name|TestStorageContainerManagerHelper
name|helper
init|=
operator|new
name|TestStorageContainerManagerHelper
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|OmKeyInfo
argument_list|>
name|keyLocations
init|=
name|helper
operator|.
name|createKeys
argument_list|(
literal|100
argument_list|,
literal|4096
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|containers
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getContainerManager
argument_list|()
operator|.
name|getContainers
argument_list|()
decl_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
name|containers
operator|.
name|size
argument_list|()
operator|>
literal|10
return|;
block|}
argument_list|,
literal|100
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|String
name|volumeName
init|=
literal|"volume"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|bucketName
init|=
literal|"bucket"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|keyName
init|=
literal|"key"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|userName
init|=
literal|"user"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|String
name|adminName
init|=
literal|"admin"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|OmKeyArgs
name|keyArgs
init|=
operator|new
name|OmKeyArgs
operator|.
name|Builder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|bucketName
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|keyName
argument_list|)
operator|.
name|setDataSize
argument_list|(
literal|1000
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OmVolumeArgs
name|volArgs
init|=
operator|new
name|OmVolumeArgs
operator|.
name|Builder
argument_list|()
operator|.
name|setAdminName
argument_list|(
name|adminName
argument_list|)
operator|.
name|setCreationTime
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|)
operator|.
name|setQuotaInBytes
argument_list|(
literal|10000
argument_list|)
operator|.
name|setVolume
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setOwnerName
argument_list|(
name|userName
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OmBucketInfo
name|bucketInfo
init|=
operator|new
name|OmBucketInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setBucketName
argument_list|(
name|bucketName
argument_list|)
operator|.
name|setIsVersionEnabled
argument_list|(
literal|false
argument_list|)
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|om
operator|.
name|createVolume
argument_list|(
name|volArgs
argument_list|)
expr_stmt|;
name|om
operator|.
name|createBucket
argument_list|(
name|bucketInfo
argument_list|)
expr_stmt|;
name|om
operator|.
name|openKey
argument_list|(
name|keyArgs
argument_list|)
expr_stmt|;
comment|//om.commitKey(keyArgs, 1);
name|cluster
operator|.
name|stop
argument_list|()
expr_stmt|;
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|miniCluster
operator|.
name|set
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"failed"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|StorageContainerManager
name|scm
decl_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
name|miniCluster
operator|.
name|get
argument_list|()
operator|!=
literal|null
return|;
block|}
argument_list|,
literal|100
argument_list|,
literal|1000
operator|*
literal|3
argument_list|)
expr_stmt|;
name|cluster
operator|=
name|miniCluster
operator|.
name|get
argument_list|()
expr_stmt|;
name|scm
operator|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|scm
operator|.
name|isInChillMode
argument_list|()
argument_list|)
expr_stmt|;
name|om
operator|=
name|miniCluster
operator|.
name|get
argument_list|()
operator|.
name|getOzoneManager
argument_list|()
expr_stmt|;
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|OMException
operator|.
name|class
argument_list|,
literal|"ChillModePrecheck failed for allocateBlock"
argument_list|,
parameter_list|()
lambda|->
name|om
operator|.
name|openKey
argument_list|(
name|keyArgs
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests inChillMode& forceExitChillMode api calls.    */
annotation|@
name|Test
DECL|method|testIsScmInChillModeAndForceExit ()
specifier|public
name|void
name|testIsScmInChillModeAndForceExit
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicReference
argument_list|<
name|MiniOzoneCluster
argument_list|>
name|miniCluster
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Test 1: SCM should be out of chill mode.
name|Assert
operator|.
name|assertFalse
argument_list|(
name|storageContainerLocationClient
operator|.
name|inChillMode
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// Restart the cluster with same metadata dir.
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|miniCluster
operator|.
name|set
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Cluster startup failed."
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
name|miniCluster
operator|.
name|get
argument_list|()
operator|!=
literal|null
return|;
block|}
argument_list|,
literal|10
argument_list|,
literal|1000
operator|*
literal|3
argument_list|)
expr_stmt|;
name|cluster
operator|=
name|miniCluster
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// Test 2: Scm should be in chill mode as datanodes are not started yet.
name|storageContainerLocationClient
operator|=
name|cluster
operator|.
name|getStorageContainerLocationClient
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|storageContainerLocationClient
operator|.
name|inChillMode
argument_list|()
argument_list|)
expr_stmt|;
comment|// Force scm out of chill mode.
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getClientProtocolServer
argument_list|()
operator|.
name|forceExitChillMode
argument_list|()
expr_stmt|;
comment|// Test 3: SCM should be out of chill mode.
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
return|return
operator|!
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getClientProtocolServer
argument_list|()
operator|.
name|inChillMode
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Cluster"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
argument_list|,
literal|10
argument_list|,
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300_000
argument_list|)
DECL|method|testSCMChillMode ()
specifier|public
name|void
name|testSCMChillMode
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniOzoneCluster
operator|.
name|Builder
name|clusterBuilder
init|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|setHbInterval
argument_list|(
literal|1000
argument_list|)
operator|.
name|setNumDatanodes
argument_list|(
literal|3
argument_list|)
operator|.
name|setStartDataNodes
argument_list|(
literal|false
argument_list|)
operator|.
name|setHbProcessorInterval
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|MiniOzoneClusterImpl
name|miniCluster
init|=
operator|(
name|MiniOzoneClusterImpl
operator|)
name|clusterBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Test1: Test chill mode  when there are no containers in system.
name|assertTrue
argument_list|(
name|miniCluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|isInChillMode
argument_list|()
argument_list|)
expr_stmt|;
name|miniCluster
operator|.
name|startHddsDatanodes
argument_list|()
expr_stmt|;
name|miniCluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|miniCluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|isInChillMode
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test2: Test chill mode  when containers are there in system.
comment|// Create {numKeys} random names keys.
name|TestStorageContainerManagerHelper
name|helper
init|=
operator|new
name|TestStorageContainerManagerHelper
argument_list|(
name|miniCluster
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|OmKeyInfo
argument_list|>
name|keyLocations
init|=
name|helper
operator|.
name|createKeys
argument_list|(
literal|100
operator|*
literal|2
argument_list|,
literal|4096
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|containers
init|=
name|miniCluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getContainerManager
argument_list|()
operator|.
name|getContainers
argument_list|()
decl_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
name|containers
operator|.
name|size
argument_list|()
operator|>
literal|10
return|;
block|}
argument_list|,
literal|100
argument_list|,
literal|1000
operator|*
literal|2
argument_list|)
expr_stmt|;
comment|// Removing some container to keep them open.
name|containers
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|containers
operator|.
name|remove
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|containers
operator|.
name|remove
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|containers
operator|.
name|remove
argument_list|(
literal|3
argument_list|)
expr_stmt|;
comment|// Close remaining containers
name|SCMContainerManager
name|mapping
init|=
operator|(
name|SCMContainerManager
operator|)
name|miniCluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getContainerManager
argument_list|()
decl_stmt|;
name|containers
operator|.
name|forEach
argument_list|(
name|c
lambda|->
block|{
try|try
block|{
name|mapping
operator|.
name|updateContainerState
argument_list|(
name|c
operator|.
name|containerID
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|FINALIZE
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|updateContainerState
argument_list|(
name|c
operator|.
name|containerID
argument_list|()
argument_list|,
name|LifeCycleEvent
operator|.
name|CLOSE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Failed to change state of open containers."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|miniCluster
operator|.
name|stop
argument_list|()
expr_stmt|;
name|GenericTestUtils
operator|.
name|LogCapturer
name|logCapturer
init|=
name|GenericTestUtils
operator|.
name|LogCapturer
operator|.
name|captureLogs
argument_list|(
name|SCMChillModeManager
operator|.
name|getLogger
argument_list|()
argument_list|)
decl_stmt|;
name|logCapturer
operator|.
name|clearOutput
argument_list|()
expr_stmt|;
name|AtomicReference
argument_list|<
name|MiniOzoneCluster
argument_list|>
name|miniClusterOzone
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|miniClusterOzone
operator|.
name|set
argument_list|(
name|clusterBuilder
operator|.
name|setStartDataNodes
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"failed"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|StorageContainerManager
name|scm
decl_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
name|miniClusterOzone
operator|.
name|get
argument_list|()
operator|!=
literal|null
return|;
block|}
argument_list|,
literal|100
argument_list|,
literal|1000
operator|*
literal|3
argument_list|)
expr_stmt|;
name|miniCluster
operator|=
operator|(
name|MiniOzoneClusterImpl
operator|)
name|miniClusterOzone
operator|.
name|get
argument_list|()
expr_stmt|;
name|scm
operator|=
name|miniCluster
operator|.
name|getStorageContainerManager
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|scm
operator|.
name|isInChillMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"SCM exiting chill mode."
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scm
operator|.
name|getCurrentContainerThreshold
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|AtomicDouble
name|curThreshold
init|=
operator|new
name|AtomicDouble
argument_list|()
decl_stmt|;
name|AtomicDouble
name|lastReportedThreshold
init|=
operator|new
name|AtomicDouble
argument_list|()
decl_stmt|;
for|for
control|(
name|HddsDatanodeService
name|dn
range|:
name|miniCluster
operator|.
name|getHddsDatanodes
argument_list|()
control|)
block|{
name|dn
operator|.
name|start
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
name|curThreshold
operator|.
name|set
argument_list|(
name|scm
operator|.
name|getCurrentContainerThreshold
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|curThreshold
operator|.
name|get
argument_list|()
operator|>
name|lastReportedThreshold
operator|.
name|get
argument_list|()
return|;
block|}
argument_list|,
literal|100
argument_list|,
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
name|lastReportedThreshold
operator|.
name|set
argument_list|(
name|curThreshold
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|=
name|miniCluster
expr_stmt|;
name|double
name|chillModeCutoff
init|=
name|conf
operator|.
name|getDouble
argument_list|(
name|HddsConfigKeys
operator|.
name|HDDS_SCM_CHILLMODE_THRESHOLD_PCT
argument_list|,
name|HddsConfigKeys
operator|.
name|HDDS_SCM_CHILLMODE_THRESHOLD_PCT_DEFAULT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|scm
operator|.
name|getCurrentContainerThreshold
argument_list|()
operator|>=
name|chillModeCutoff
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"SCM exiting chill mode."
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|scm
operator|.
name|isInChillMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSCMChillModeRestrictedOp ()
specifier|public
name|void
name|testSCMChillModeRestrictedOp
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_STORE_IMPL
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_STORE_IMPL_LEVELDB
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|stop
argument_list|()
expr_stmt|;
name|cluster
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|StorageContainerManager
name|scm
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|scm
operator|.
name|isInChillMode
argument_list|()
argument_list|)
expr_stmt|;
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|SCMException
operator|.
name|class
argument_list|,
literal|"ChillModePrecheck failed for allocateContainer"
argument_list|,
parameter_list|()
lambda|->
block|{
name|scm
operator|.
name|getClientProtocolServer
argument_list|()
operator|.
name|allocateContainer
argument_list|(
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|,
name|ReplicationFactor
operator|.
name|ONE
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|startHddsDatanodes
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|scm
operator|.
name|isInChillMode
argument_list|()
argument_list|)
expr_stmt|;
name|TestStorageContainerManagerHelper
name|helper
init|=
operator|new
name|TestStorageContainerManagerHelper
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|helper
operator|.
name|createKeys
argument_list|(
literal|10
argument_list|,
literal|4096
argument_list|)
expr_stmt|;
name|SCMClientProtocolServer
name|clientProtocolServer
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getClientProtocolServer
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
operator|(
name|scm
operator|.
name|getClientProtocolServer
argument_list|()
operator|)
operator|.
name|getChillModeStatus
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|containers
init|=
name|scm
operator|.
name|getContainerManager
argument_list|()
operator|.
name|getContainers
argument_list|()
decl_stmt|;
name|scm
operator|.
name|getEventQueue
argument_list|()
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|CHILL_MODE_STATUS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
name|clientProtocolServer
operator|.
name|getChillModeStatus
argument_list|()
return|;
block|}
argument_list|,
literal|50
argument_list|,
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|clientProtocolServer
operator|.
name|getChillModeStatus
argument_list|()
argument_list|)
expr_stmt|;
name|LambdaTestUtils
operator|.
name|intercept
argument_list|(
name|SCMException
operator|.
name|class
argument_list|,
literal|"Open container "
operator|+
name|containers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getContainerID
argument_list|()
operator|+
literal|" "
operator|+
literal|"doesn't have enough replicas to service this operation in Chill"
operator|+
literal|" mode."
argument_list|,
parameter_list|()
lambda|->
name|clientProtocolServer
operator|.
name|getContainerWithPipeline
argument_list|(
name|containers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getContainerID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300_000
argument_list|)
DECL|method|testSCMChillModeDisabled ()
specifier|public
name|void
name|testSCMChillModeDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// If chill mode is disabled, cluster should not be in chill mode even if
comment|// min number of datanodes are not started.
name|conf
operator|.
name|setBoolean
argument_list|(
name|HddsConfigKeys
operator|.
name|HDDS_SCM_CHILLMODE_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|HddsConfigKeys
operator|.
name|HDDS_SCM_CHILLMODE_MIN_DATANODE
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|builder
operator|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|setHbInterval
argument_list|(
literal|1000
argument_list|)
operator|.
name|setHbProcessorInterval
argument_list|(
literal|500
argument_list|)
operator|.
name|setNumDatanodes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|cluster
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|StorageContainerManager
name|scm
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|scm
operator|.
name|isInChillMode
argument_list|()
argument_list|)
expr_stmt|;
comment|// Even on SCM restart, cluster should be out of chill mode immediately.
name|cluster
operator|.
name|restartStorageContainerManager
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|scm
operator|.
name|isInChillMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

