begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container
package|package
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
package|;
end_package

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
name|RandomUtils
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
name|conf
operator|.
name|StorageUnit
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
name|FileUtil
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
name|ContainerWithPipeline
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
name|pipeline
operator|.
name|PipelineManager
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
name|pipeline
operator|.
name|SCMPipelineManager
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
name|server
operator|.
name|events
operator|.
name|EventQueue
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
name|container
operator|.
name|common
operator|.
name|SCMTestUtils
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
name|File
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
import|import static
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
operator|.
name|CREATED
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE_DEFAULT
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE
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
name|hdds
operator|.
name|scm
operator|.
name|events
operator|.
name|SCMEvents
operator|.
name|CLOSE_CONTAINER
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
name|hdds
operator|.
name|scm
operator|.
name|events
operator|.
name|SCMEvents
operator|.
name|DATANODE_COMMAND
import|;
end_import

begin_comment
comment|/**  * Tests the closeContainerEventHandler class.  */
end_comment

begin_class
DECL|class|TestCloseContainerEventHandler
specifier|public
class|class
name|TestCloseContainerEventHandler
block|{
DECL|field|configuration
specifier|private
specifier|static
name|Configuration
name|configuration
decl_stmt|;
DECL|field|nodeManager
specifier|private
specifier|static
name|MockNodeManager
name|nodeManager
decl_stmt|;
DECL|field|containerManager
specifier|private
specifier|static
name|SCMContainerManager
name|containerManager
decl_stmt|;
DECL|field|size
specifier|private
specifier|static
name|long
name|size
decl_stmt|;
DECL|field|testDir
specifier|private
specifier|static
name|File
name|testDir
decl_stmt|;
DECL|field|eventQueue
specifier|private
specifier|static
name|EventQueue
name|eventQueue
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|configuration
operator|=
name|SCMTestUtils
operator|.
name|getConf
argument_list|()
expr_stmt|;
name|size
operator|=
operator|(
name|long
operator|)
name|configuration
operator|.
name|getStorageSize
argument_list|(
name|OZONE_SCM_CONTAINER_SIZE
argument_list|,
name|OZONE_SCM_CONTAINER_SIZE_DEFAULT
argument_list|,
name|StorageUnit
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|testDir
operator|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
name|TestCloseContainerEventHandler
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|testDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|nodeManager
operator|=
operator|new
name|MockNodeManager
argument_list|(
literal|true
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|PipelineManager
name|pipelineManager
init|=
operator|new
name|SCMPipelineManager
argument_list|(
name|configuration
argument_list|,
name|nodeManager
argument_list|,
name|eventQueue
argument_list|)
decl_stmt|;
name|containerManager
operator|=
operator|new
name|SCMContainerManager
argument_list|(
name|configuration
argument_list|,
name|nodeManager
argument_list|,
name|pipelineManager
argument_list|,
operator|new
name|EventQueue
argument_list|()
argument_list|)
expr_stmt|;
name|eventQueue
operator|=
operator|new
name|EventQueue
argument_list|()
expr_stmt|;
name|eventQueue
operator|.
name|addHandler
argument_list|(
name|CLOSE_CONTAINER
argument_list|,
operator|new
name|CloseContainerEventHandler
argument_list|(
name|containerManager
argument_list|)
argument_list|)
expr_stmt|;
name|eventQueue
operator|.
name|addHandler
argument_list|(
name|DATANODE_COMMAND
argument_list|,
name|nodeManager
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
throws|throws
name|Exception
block|{
if|if
condition|(
name|containerManager
operator|!=
literal|null
condition|)
block|{
name|containerManager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIfCloseContainerEventHadnlerInvoked ()
specifier|public
name|void
name|testIfCloseContainerEventHadnlerInvoked
parameter_list|()
block|{
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
name|CloseContainerEventHandler
operator|.
name|LOG
argument_list|)
decl_stmt|;
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|CLOSE_CONTAINER
argument_list|,
operator|new
name|ContainerID
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|RandomUtils
operator|.
name|nextInt
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|eventQueue
operator|.
name|processAll
argument_list|(
literal|1000
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
literal|"Close container Event triggered for container"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloseContainerEventWithInvalidContainer ()
specifier|public
name|void
name|testCloseContainerEventWithInvalidContainer
parameter_list|()
block|{
name|long
name|id
init|=
name|Math
operator|.
name|abs
argument_list|(
name|RandomUtils
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
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
name|CloseContainerEventHandler
operator|.
name|LOG
argument_list|)
decl_stmt|;
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|CLOSE_CONTAINER
argument_list|,
operator|new
name|ContainerID
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|eventQueue
operator|.
name|processAll
argument_list|(
literal|1000
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
literal|"Failed to update the container state"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloseContainerEventWithValidContainers ()
specifier|public
name|void
name|testCloseContainerEventWithValidContainers
parameter_list|()
throws|throws
name|IOException
block|{
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
name|CloseContainerEventHandler
operator|.
name|LOG
argument_list|)
decl_stmt|;
name|ContainerWithPipeline
name|containerWithPipeline
init|=
name|containerManager
operator|.
name|allocateContainer
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
argument_list|,
literal|"ozone"
argument_list|)
decl_stmt|;
name|ContainerID
name|id
init|=
operator|new
name|ContainerID
argument_list|(
name|containerWithPipeline
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|)
decl_stmt|;
name|DatanodeDetails
name|datanode
init|=
name|containerWithPipeline
operator|.
name|getPipeline
argument_list|()
operator|.
name|getFirstNode
argument_list|()
decl_stmt|;
name|int
name|closeCount
init|=
name|nodeManager
operator|.
name|getCommandCount
argument_list|(
name|datanode
argument_list|)
decl_stmt|;
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|CLOSE_CONTAINER
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|eventQueue
operator|.
name|processAll
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// At this point of time, the allocated container is not in open
comment|// state, so firing close container event should not queue CLOSE
comment|// command in the Datanode
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|nodeManager
operator|.
name|getCommandCount
argument_list|(
name|datanode
argument_list|)
argument_list|)
expr_stmt|;
comment|//Execute these state transitions so that we can close the container.
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|id
argument_list|,
name|CREATED
argument_list|)
expr_stmt|;
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|CLOSE_CONTAINER
argument_list|,
operator|new
name|ContainerID
argument_list|(
name|containerWithPipeline
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|eventQueue
operator|.
name|processAll
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|closeCount
operator|+
literal|1
argument_list|,
name|nodeManager
operator|.
name|getCommandCount
argument_list|(
name|datanode
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|CLOSING
argument_list|,
name|containerManager
operator|.
name|getContainer
argument_list|(
name|id
argument_list|)
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloseContainerEventWithRatis ()
specifier|public
name|void
name|testCloseContainerEventWithRatis
parameter_list|()
throws|throws
name|IOException
block|{
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
name|CloseContainerEventHandler
operator|.
name|LOG
argument_list|)
decl_stmt|;
name|ContainerWithPipeline
name|containerWithPipeline
init|=
name|containerManager
operator|.
name|allocateContainer
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
argument_list|,
literal|"ozone"
argument_list|)
decl_stmt|;
name|ContainerID
name|id
init|=
operator|new
name|ContainerID
argument_list|(
name|containerWithPipeline
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|)
decl_stmt|;
name|int
index|[]
name|closeCount
init|=
operator|new
name|int
index|[
literal|3
index|]
decl_stmt|;
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|CLOSE_CONTAINER
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|eventQueue
operator|.
name|processAll
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DatanodeDetails
name|details
range|:
name|containerWithPipeline
operator|.
name|getPipeline
argument_list|()
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|closeCount
index|[
name|i
index|]
operator|=
name|nodeManager
operator|.
name|getCommandCount
argument_list|(
name|details
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|i
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|DatanodeDetails
name|details
range|:
name|containerWithPipeline
operator|.
name|getPipeline
argument_list|()
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|closeCount
index|[
name|i
index|]
argument_list|,
name|nodeManager
operator|.
name|getCommandCount
argument_list|(
name|details
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
comment|//Execute these state transitions so that we can close the container.
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|id
argument_list|,
name|CREATED
argument_list|)
expr_stmt|;
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|CLOSE_CONTAINER
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|eventQueue
operator|.
name|processAll
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
comment|// Make sure close is queued for each datanode on the pipeline
for|for
control|(
name|DatanodeDetails
name|details
range|:
name|containerWithPipeline
operator|.
name|getPipeline
argument_list|()
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|closeCount
index|[
name|i
index|]
operator|+
literal|1
argument_list|,
name|nodeManager
operator|.
name|getCommandCount
argument_list|(
name|details
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|CLOSING
argument_list|,
name|containerManager
operator|.
name|getContainer
argument_list|(
name|id
argument_list|)
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

