begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.statemachine.commandhandler
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|commandhandler
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
name|hdds
operator|.
name|client
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
name|client
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
name|DatanodeDetails
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
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
name|client
operator|.
name|ObjectStore
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
name|client
operator|.
name|OzoneClient
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
name|client
operator|.
name|OzoneClientFactory
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
name|client
operator|.
name|io
operator|.
name|OzoneOutputStream
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
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ContainerData
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
name|container
operator|.
name|ozoneimpl
operator|.
name|OzoneContainer
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
name|OmKeyLocationInfo
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
name|protocol
operator|.
name|commands
operator|.
name|CloseContainerCommand
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
name|Assert
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
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_class
DECL|class|TestCloseContainerByPipeline
specifier|public
class|class
name|TestCloseContainerByPipeline
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|client
specifier|private
specifier|static
name|OzoneClient
name|client
decl_stmt|;
DECL|field|objectStore
specifier|private
specifier|static
name|ObjectStore
name|objectStore
decl_stmt|;
comment|/**    * Create a MiniDFSCluster for testing.    *<p>    * Ozone is made active by setting OZONE_ENABLED = true    *    * @throws IOException    */
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
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
name|cluster
operator|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|setNumDatanodes
argument_list|(
literal|3
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
comment|//the easiest way to create an open container is creating a key
name|client
operator|=
name|OzoneClientFactory
operator|.
name|getClient
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|objectStore
operator|=
name|client
operator|.
name|getObjectStore
argument_list|()
expr_stmt|;
name|objectStore
operator|.
name|createVolume
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|objectStore
operator|.
name|getVolume
argument_list|(
literal|"test"
argument_list|)
operator|.
name|createBucket
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Shutdown MiniDFSCluster.    */
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
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
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testIfCloseContainerCommandHandlerIsInvoked ()
specifier|public
name|void
name|testIfCloseContainerCommandHandlerIsInvoked
parameter_list|()
throws|throws
name|Exception
block|{
name|OzoneOutputStream
name|key
init|=
name|objectStore
operator|.
name|getVolume
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getBucket
argument_list|(
literal|"test"
argument_list|)
operator|.
name|createKey
argument_list|(
literal|"testCloseContainer"
argument_list|,
literal|1024
argument_list|,
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|,
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
decl_stmt|;
name|key
operator|.
name|write
argument_list|(
literal|"standalone"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|key
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//get the name of a valid container
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
literal|"test"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setType
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|)
operator|.
name|setFactor
argument_list|(
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
operator|.
name|setDataSize
argument_list|(
literal|1024
argument_list|)
operator|.
name|setKeyName
argument_list|(
literal|"testCloseContainer"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OmKeyLocationInfo
name|omKeyLocationInfo
init|=
name|cluster
operator|.
name|getOzoneManager
argument_list|()
operator|.
name|lookupKey
argument_list|(
name|keyArgs
argument_list|)
operator|.
name|getKeyLocationVersions
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlocksLatestVersionOnly
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|containerID
init|=
name|omKeyLocationInfo
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmContainerManager
argument_list|()
operator|.
name|getContainerWithPipeline
argument_list|(
name|containerID
argument_list|)
operator|.
name|getPipeline
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|datanodes
init|=
name|pipeline
operator|.
name|getMachines
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanodes
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|DatanodeDetails
name|datanodeDetails
init|=
name|datanodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|HddsDatanodeService
name|datanodeService
init|=
literal|null
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|isContainerClosed
argument_list|(
name|cluster
argument_list|,
name|containerID
argument_list|,
name|datanodeDetails
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|HddsDatanodeService
name|datanodeServiceItr
range|:
name|cluster
operator|.
name|getHddsDatanodes
argument_list|()
control|)
block|{
if|if
condition|(
name|datanodeDetails
operator|.
name|equals
argument_list|(
name|datanodeServiceItr
operator|.
name|getDatanodeDetails
argument_list|()
argument_list|)
condition|)
block|{
name|datanodeService
operator|=
name|datanodeServiceItr
expr_stmt|;
break|break;
block|}
block|}
name|CommandHandler
name|closeContainerHandler
init|=
name|datanodeService
operator|.
name|getDatanodeStateMachine
argument_list|()
operator|.
name|getCommandDispatcher
argument_list|()
operator|.
name|getCloseContainerHandler
argument_list|()
decl_stmt|;
name|int
name|lastInvocationCount
init|=
name|closeContainerHandler
operator|.
name|getInvocationCount
argument_list|()
decl_stmt|;
comment|//send the order to close the container
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmNodeManager
argument_list|()
operator|.
name|addDatanodeCommand
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|,
operator|new
name|CloseContainerCommand
argument_list|(
name|containerID
argument_list|,
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|,
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|isContainerClosed
argument_list|(
name|cluster
argument_list|,
name|containerID
argument_list|,
name|datanodeDetails
argument_list|)
argument_list|,
literal|500
argument_list|,
literal|5
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|// Make sure the closeContainerCommandHandler is Invoked
name|Assert
operator|.
name|assertTrue
argument_list|(
name|closeContainerHandler
operator|.
name|getInvocationCount
argument_list|()
operator|>
name|lastInvocationCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloseContainerViaStandAlone ()
specifier|public
name|void
name|testCloseContainerViaStandAlone
parameter_list|()
throws|throws
name|IOException
throws|,
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|OzoneOutputStream
name|key
init|=
name|objectStore
operator|.
name|getVolume
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getBucket
argument_list|(
literal|"test"
argument_list|)
operator|.
name|createKey
argument_list|(
literal|"standalone"
argument_list|,
literal|1024
argument_list|,
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|,
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
decl_stmt|;
name|key
operator|.
name|write
argument_list|(
literal|"standalone"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|key
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//get the name of a valid container
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
literal|"test"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setType
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|)
operator|.
name|setFactor
argument_list|(
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
operator|.
name|setDataSize
argument_list|(
literal|1024
argument_list|)
operator|.
name|setKeyName
argument_list|(
literal|"standalone"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OmKeyLocationInfo
name|omKeyLocationInfo
init|=
name|cluster
operator|.
name|getOzoneManager
argument_list|()
operator|.
name|lookupKey
argument_list|(
name|keyArgs
argument_list|)
operator|.
name|getKeyLocationVersions
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlocksLatestVersionOnly
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|containerID
init|=
name|omKeyLocationInfo
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmContainerManager
argument_list|()
operator|.
name|getContainerWithPipeline
argument_list|(
name|containerID
argument_list|)
operator|.
name|getPipeline
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|datanodes
init|=
name|pipeline
operator|.
name|getMachines
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanodes
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|DatanodeDetails
name|datanodeDetails
init|=
name|datanodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|isContainerClosed
argument_list|(
name|cluster
argument_list|,
name|containerID
argument_list|,
name|datanodeDetails
argument_list|)
argument_list|)
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
name|OzoneContainer
operator|.
name|LOG
argument_list|)
decl_stmt|;
comment|//send the order to close the container
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmNodeManager
argument_list|()
operator|.
name|addDatanodeCommand
argument_list|(
name|datanodeDetails
operator|.
name|getUuid
argument_list|()
argument_list|,
operator|new
name|CloseContainerCommand
argument_list|(
name|containerID
argument_list|,
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|,
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// The log will appear after the state changed to closed in standalone,
comment|// wait for the log to ensure the operation has been done.
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"submitting CloseContainer request over STAND_ALONE server for"
operator|+
literal|" container "
operator|+
name|containerID
argument_list|)
argument_list|,
literal|500
argument_list|,
literal|5
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|//double check if it's really closed (waitFor also throws an exception)
name|Assert
operator|.
name|assertTrue
argument_list|(
name|isContainerClosed
argument_list|(
name|cluster
argument_list|,
name|containerID
argument_list|,
name|datanodeDetails
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"submitting CloseContainer request over STAND_ALONE server for"
operator|+
literal|" container "
operator|+
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure it was really closed via StandAlone not Ratis server
name|Assert
operator|.
name|assertFalse
argument_list|(
operator|(
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"submitting CloseContainer request over RATIS server for container "
operator|+
name|containerID
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|logCapturer
operator|.
name|stopCapturing
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloseContainerViaRatis ()
specifier|public
name|void
name|testCloseContainerViaRatis
parameter_list|()
throws|throws
name|IOException
throws|,
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|OzoneOutputStream
name|key
init|=
name|objectStore
operator|.
name|getVolume
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getBucket
argument_list|(
literal|"test"
argument_list|)
operator|.
name|createKey
argument_list|(
literal|"ratis"
argument_list|,
literal|1024
argument_list|,
name|ReplicationType
operator|.
name|RATIS
argument_list|,
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
decl_stmt|;
name|key
operator|.
name|write
argument_list|(
literal|"ratis"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|key
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//get the name of a valid container
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
literal|"test"
argument_list|)
operator|.
name|setBucketName
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setType
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|)
operator|.
name|setFactor
argument_list|(
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
operator|.
name|setDataSize
argument_list|(
literal|1024
argument_list|)
operator|.
name|setKeyName
argument_list|(
literal|"ratis"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|OmKeyLocationInfo
name|omKeyLocationInfo
init|=
name|cluster
operator|.
name|getOzoneManager
argument_list|()
operator|.
name|lookupKey
argument_list|(
name|keyArgs
argument_list|)
operator|.
name|getKeyLocationVersions
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlocksLatestVersionOnly
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|containerID
init|=
name|omKeyLocationInfo
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmContainerManager
argument_list|()
operator|.
name|getContainerWithPipeline
argument_list|(
name|containerID
argument_list|)
operator|.
name|getPipeline
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|datanodes
init|=
name|pipeline
operator|.
name|getMachines
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|datanodes
operator|.
name|size
argument_list|()
argument_list|)
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
name|OzoneContainer
operator|.
name|LOG
argument_list|)
decl_stmt|;
for|for
control|(
name|DatanodeDetails
name|details
range|:
name|datanodes
control|)
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
name|isContainerClosed
argument_list|(
name|cluster
argument_list|,
name|containerID
argument_list|,
name|details
argument_list|)
argument_list|)
expr_stmt|;
comment|//send the order to close the container
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmNodeManager
argument_list|()
operator|.
name|addDatanodeCommand
argument_list|(
name|details
operator|.
name|getUuid
argument_list|()
argument_list|,
operator|new
name|CloseContainerCommand
argument_list|(
name|containerID
argument_list|,
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|,
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|DatanodeDetails
name|datanodeDetails
range|:
name|datanodes
control|)
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|isContainerClosed
argument_list|(
name|cluster
argument_list|,
name|containerID
argument_list|,
name|datanodeDetails
argument_list|)
argument_list|,
literal|500
argument_list|,
literal|15
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|//double check if it's really closed (waitFor also throws an exception)
name|Assert
operator|.
name|assertTrue
argument_list|(
name|isContainerClosed
argument_list|(
name|cluster
argument_list|,
name|containerID
argument_list|,
name|datanodeDetails
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Make sure it was really closed via Ratis not STAND_ALONE server
name|Assert
operator|.
name|assertFalse
argument_list|(
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"submitting CloseContainer request over STAND_ALONE "
operator|+
literal|"server for container "
operator|+
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|(
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"submitting CloseContainer request over RATIS server for container "
operator|+
name|containerID
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|logCapturer
operator|.
name|stopCapturing
argument_list|()
expr_stmt|;
block|}
DECL|method|isContainerClosed (MiniOzoneCluster cluster, long containerID, DatanodeDetails datanode)
specifier|private
name|Boolean
name|isContainerClosed
parameter_list|(
name|MiniOzoneCluster
name|cluster
parameter_list|,
name|long
name|containerID
parameter_list|,
name|DatanodeDetails
name|datanode
parameter_list|)
block|{
name|ContainerData
name|containerData
decl_stmt|;
for|for
control|(
name|HddsDatanodeService
name|datanodeService
range|:
name|cluster
operator|.
name|getHddsDatanodes
argument_list|()
control|)
block|{
if|if
condition|(
name|datanode
operator|.
name|equals
argument_list|(
name|datanodeService
operator|.
name|getDatanodeDetails
argument_list|()
argument_list|)
condition|)
block|{
name|containerData
operator|=
name|datanodeService
operator|.
name|getDatanodeStateMachine
argument_list|()
operator|.
name|getContainer
argument_list|()
operator|.
name|getContainerSet
argument_list|()
operator|.
name|getContainer
argument_list|(
name|containerID
argument_list|)
operator|.
name|getContainerData
argument_list|()
expr_stmt|;
return|return
name|containerData
operator|.
name|isClosed
argument_list|()
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

