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
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|ScmConfigKeys
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
name|ContainerID
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
name|PipelineID
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
name|interfaces
operator|.
name|Container
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
name|statemachine
operator|.
name|DatanodeStateMachine
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
name|statemachine
operator|.
name|StateContext
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
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|ratis
operator|.
name|RatisHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|client
operator|.
name|RaftClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftGroup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftGroupId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftPeer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|retry
operator|.
name|RetryPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|rpc
operator|.
name|SupportedRpcType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|util
operator|.
name|TimeDuration
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
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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

begin_comment
comment|/**  * Test cases to verify CloseContainerCommandHandler in datanode.  */
end_comment

begin_class
DECL|class|TestCloseContainerCommandHandler
specifier|public
class|class
name|TestCloseContainerCommandHandler
block|{
DECL|field|context
specifier|private
specifier|final
name|StateContext
name|context
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StateContext
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|testDir
specifier|private
specifier|static
name|File
name|testDir
decl_stmt|;
annotation|@
name|Test
DECL|method|testCloseContainerViaRatis ()
specifier|public
name|void
name|testCloseContainerViaRatis
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|DatanodeDetails
name|datanodeDetails
init|=
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
specifier|final
name|OzoneContainer
name|ozoneContainer
init|=
name|getOzoneContainer
argument_list|(
name|conf
argument_list|,
name|datanodeDetails
argument_list|)
decl_stmt|;
name|ozoneContainer
operator|.
name|start
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|Container
name|container
init|=
name|createContainer
argument_list|(
name|conf
argument_list|,
name|datanodeDetails
argument_list|,
name|ozoneContainer
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|context
operator|.
name|getParent
argument_list|()
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|triggerHeartbeat
argument_list|()
expr_stmt|;
specifier|final
name|long
name|containerId
init|=
name|container
operator|.
name|getContainerData
argument_list|()
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
specifier|final
name|PipelineID
name|pipelineId
init|=
name|PipelineID
operator|.
name|valueOf
argument_list|(
name|UUID
operator|.
name|fromString
argument_list|(
name|container
operator|.
name|getContainerData
argument_list|()
operator|.
name|getOriginPipelineId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// We have created a container via ratis.
comment|// Now close the container on ratis.
specifier|final
name|CloseContainerCommandHandler
name|closeHandler
init|=
operator|new
name|CloseContainerCommandHandler
argument_list|()
decl_stmt|;
specifier|final
name|CloseContainerCommand
name|command
init|=
operator|new
name|CloseContainerCommand
argument_list|(
name|containerId
argument_list|,
name|pipelineId
argument_list|)
decl_stmt|;
name|closeHandler
operator|.
name|handle
argument_list|(
name|command
argument_list|,
name|ozoneContainer
argument_list|,
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|CLOSED
argument_list|,
name|ozoneContainer
operator|.
name|getContainerSet
argument_list|()
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
operator|.
name|getContainerState
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|context
operator|.
name|getParent
argument_list|()
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|triggerHeartbeat
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|ozoneContainer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCloseContainerViaStandalone ()
specifier|public
name|void
name|testCloseContainerViaStandalone
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|DatanodeDetails
name|datanodeDetails
init|=
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
specifier|final
name|OzoneContainer
name|ozoneContainer
init|=
name|getOzoneContainer
argument_list|(
name|conf
argument_list|,
name|datanodeDetails
argument_list|)
decl_stmt|;
name|ozoneContainer
operator|.
name|start
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|Container
name|container
init|=
name|createContainer
argument_list|(
name|conf
argument_list|,
name|datanodeDetails
argument_list|,
name|ozoneContainer
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|context
operator|.
name|getParent
argument_list|()
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|triggerHeartbeat
argument_list|()
expr_stmt|;
specifier|final
name|long
name|containerId
init|=
name|container
operator|.
name|getContainerData
argument_list|()
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
comment|// To quasi close specify a pipeline which doesn't exist in the datanode.
specifier|final
name|PipelineID
name|pipelineId
init|=
name|PipelineID
operator|.
name|randomId
argument_list|()
decl_stmt|;
comment|// We have created a container via ratis. Now quasi close it.
specifier|final
name|CloseContainerCommandHandler
name|closeHandler
init|=
operator|new
name|CloseContainerCommandHandler
argument_list|()
decl_stmt|;
specifier|final
name|CloseContainerCommand
name|command
init|=
operator|new
name|CloseContainerCommand
argument_list|(
name|containerId
argument_list|,
name|pipelineId
argument_list|)
decl_stmt|;
name|closeHandler
operator|.
name|handle
argument_list|(
name|command
argument_list|,
name|ozoneContainer
argument_list|,
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|QUASI_CLOSED
argument_list|,
name|ozoneContainer
operator|.
name|getContainerSet
argument_list|()
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
operator|.
name|getContainerState
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|context
operator|.
name|getParent
argument_list|()
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|triggerHeartbeat
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|ozoneContainer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testQuasiCloseToClose ()
specifier|public
name|void
name|testQuasiCloseToClose
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|DatanodeDetails
name|datanodeDetails
init|=
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
specifier|final
name|OzoneContainer
name|ozoneContainer
init|=
name|getOzoneContainer
argument_list|(
name|conf
argument_list|,
name|datanodeDetails
argument_list|)
decl_stmt|;
name|ozoneContainer
operator|.
name|start
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|Container
name|container
init|=
name|createContainer
argument_list|(
name|conf
argument_list|,
name|datanodeDetails
argument_list|,
name|ozoneContainer
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|context
operator|.
name|getParent
argument_list|()
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|triggerHeartbeat
argument_list|()
expr_stmt|;
specifier|final
name|long
name|containerId
init|=
name|container
operator|.
name|getContainerData
argument_list|()
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
comment|// A pipeline which doesn't exist in the datanode.
specifier|final
name|PipelineID
name|pipelineId
init|=
name|PipelineID
operator|.
name|randomId
argument_list|()
decl_stmt|;
comment|// We have created a container via ratis. Now quasi close it.
specifier|final
name|CloseContainerCommandHandler
name|closeHandler
init|=
operator|new
name|CloseContainerCommandHandler
argument_list|()
decl_stmt|;
specifier|final
name|CloseContainerCommand
name|command
init|=
operator|new
name|CloseContainerCommand
argument_list|(
name|containerId
argument_list|,
name|pipelineId
argument_list|)
decl_stmt|;
name|closeHandler
operator|.
name|handle
argument_list|(
name|command
argument_list|,
name|ozoneContainer
argument_list|,
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|QUASI_CLOSED
argument_list|,
name|ozoneContainer
operator|.
name|getContainerSet
argument_list|()
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
operator|.
name|getContainerState
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|context
operator|.
name|getParent
argument_list|()
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|triggerHeartbeat
argument_list|()
expr_stmt|;
comment|// The container is quasi closed. Force close the container now.
specifier|final
name|CloseContainerCommand
name|closeCommand
init|=
operator|new
name|CloseContainerCommand
argument_list|(
name|containerId
argument_list|,
name|pipelineId
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|closeHandler
operator|.
name|handle
argument_list|(
name|closeCommand
argument_list|,
name|ozoneContainer
argument_list|,
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|CLOSED
argument_list|,
name|ozoneContainer
operator|.
name|getContainerSet
argument_list|()
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
operator|.
name|getContainerState
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|context
operator|.
name|getParent
argument_list|()
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|triggerHeartbeat
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|ozoneContainer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testForceCloseOpenContainer ()
specifier|public
name|void
name|testForceCloseOpenContainer
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|DatanodeDetails
name|datanodeDetails
init|=
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
specifier|final
name|OzoneContainer
name|ozoneContainer
init|=
name|getOzoneContainer
argument_list|(
name|conf
argument_list|,
name|datanodeDetails
argument_list|)
decl_stmt|;
name|ozoneContainer
operator|.
name|start
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|Container
name|container
init|=
name|createContainer
argument_list|(
name|conf
argument_list|,
name|datanodeDetails
argument_list|,
name|ozoneContainer
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|context
operator|.
name|getParent
argument_list|()
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|triggerHeartbeat
argument_list|()
expr_stmt|;
specifier|final
name|long
name|containerId
init|=
name|container
operator|.
name|getContainerData
argument_list|()
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
comment|// A pipeline which doesn't exist in the datanode.
specifier|final
name|PipelineID
name|pipelineId
init|=
name|PipelineID
operator|.
name|randomId
argument_list|()
decl_stmt|;
specifier|final
name|CloseContainerCommandHandler
name|closeHandler
init|=
operator|new
name|CloseContainerCommandHandler
argument_list|()
decl_stmt|;
specifier|final
name|CloseContainerCommand
name|closeCommand
init|=
operator|new
name|CloseContainerCommand
argument_list|(
name|containerId
argument_list|,
name|pipelineId
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|closeHandler
operator|.
name|handle
argument_list|(
name|closeCommand
argument_list|,
name|ozoneContainer
argument_list|,
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|CLOSED
argument_list|,
name|ozoneContainer
operator|.
name|getContainerSet
argument_list|()
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
operator|.
name|getContainerState
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|context
operator|.
name|getParent
argument_list|()
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|triggerHeartbeat
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|ozoneContainer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testQuasiCloseClosedContainer ()
specifier|public
name|void
name|testQuasiCloseClosedContainer
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|DatanodeDetails
name|datanodeDetails
init|=
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
specifier|final
name|OzoneContainer
name|ozoneContainer
init|=
name|getOzoneContainer
argument_list|(
name|conf
argument_list|,
name|datanodeDetails
argument_list|)
decl_stmt|;
name|ozoneContainer
operator|.
name|start
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|Container
name|container
init|=
name|createContainer
argument_list|(
name|conf
argument_list|,
name|datanodeDetails
argument_list|,
name|ozoneContainer
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|context
operator|.
name|getParent
argument_list|()
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|triggerHeartbeat
argument_list|()
expr_stmt|;
specifier|final
name|long
name|containerId
init|=
name|container
operator|.
name|getContainerData
argument_list|()
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
specifier|final
name|PipelineID
name|pipelineId
init|=
name|PipelineID
operator|.
name|valueOf
argument_list|(
name|UUID
operator|.
name|fromString
argument_list|(
name|container
operator|.
name|getContainerData
argument_list|()
operator|.
name|getOriginPipelineId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|CloseContainerCommandHandler
name|closeHandler
init|=
operator|new
name|CloseContainerCommandHandler
argument_list|()
decl_stmt|;
specifier|final
name|CloseContainerCommand
name|closeCommand
init|=
operator|new
name|CloseContainerCommand
argument_list|(
name|containerId
argument_list|,
name|pipelineId
argument_list|)
decl_stmt|;
name|closeHandler
operator|.
name|handle
argument_list|(
name|closeCommand
argument_list|,
name|ozoneContainer
argument_list|,
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|CLOSED
argument_list|,
name|ozoneContainer
operator|.
name|getContainerSet
argument_list|()
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
operator|.
name|getContainerState
argument_list|()
argument_list|)
expr_stmt|;
comment|// The container is closed, now we send close command with
comment|// pipeline id which doesn't exist.
comment|// This should cause the datanode to trigger quasi close, since the
comment|// container is already closed, this should do nothing.
comment|// The command should not fail either.
specifier|final
name|PipelineID
name|randomPipeline
init|=
name|PipelineID
operator|.
name|randomId
argument_list|()
decl_stmt|;
specifier|final
name|CloseContainerCommand
name|quasiCloseCommand
init|=
operator|new
name|CloseContainerCommand
argument_list|(
name|containerId
argument_list|,
name|randomPipeline
argument_list|)
decl_stmt|;
name|closeHandler
operator|.
name|handle
argument_list|(
name|quasiCloseCommand
argument_list|,
name|ozoneContainer
argument_list|,
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|CLOSED
argument_list|,
name|ozoneContainer
operator|.
name|getContainerSet
argument_list|()
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
operator|.
name|getContainerState
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|ozoneContainer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getOzoneContainer (final OzoneConfiguration conf, final DatanodeDetails datanodeDetails)
specifier|private
name|OzoneContainer
name|getOzoneContainer
parameter_list|(
specifier|final
name|OzoneConfiguration
name|conf
parameter_list|,
specifier|final
name|DatanodeDetails
name|datanodeDetails
parameter_list|)
throws|throws
name|IOException
block|{
name|testDir
operator|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
name|TestCloseContainerCommandHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|testDir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ScmConfigKeys
operator|.
name|HDDS_DATANODE_DIR_KEY
argument_list|,
name|testDir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_IPC_RANDOM_PORT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_RANDOM_PORT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|DatanodeStateMachine
name|datanodeStateMachine
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DatanodeStateMachine
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|datanodeStateMachine
operator|.
name|getDatanodeDetails
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|datanodeDetails
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|context
operator|.
name|getParent
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|datanodeStateMachine
argument_list|)
expr_stmt|;
specifier|final
name|OzoneContainer
name|ozoneContainer
init|=
operator|new
name|OzoneContainer
argument_list|(
name|datanodeDetails
argument_list|,
name|conf
argument_list|,
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|ozoneContainer
return|;
block|}
DECL|method|createContainer (final Configuration conf, final DatanodeDetails datanodeDetails, final OzoneContainer ozoneContainer)
specifier|private
name|Container
name|createContainer
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
specifier|final
name|OzoneContainer
name|ozoneContainer
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|PipelineID
name|pipelineID
init|=
name|PipelineID
operator|.
name|randomId
argument_list|()
decl_stmt|;
specifier|final
name|RaftGroupId
name|raftGroupId
init|=
name|RaftGroupId
operator|.
name|valueOf
argument_list|(
name|pipelineID
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RetryPolicy
name|retryPolicy
init|=
name|RatisHelper
operator|.
name|createRetryPolicy
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|RaftPeer
name|peer
init|=
name|RatisHelper
operator|.
name|toRaftPeer
argument_list|(
name|datanodeDetails
argument_list|)
decl_stmt|;
specifier|final
name|RaftGroup
name|group
init|=
name|RatisHelper
operator|.
name|newRaftGroup
argument_list|(
name|raftGroupId
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|datanodeDetails
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxOutstandingRequests
init|=
literal|100
decl_stmt|;
specifier|final
name|RaftClient
name|client
init|=
name|RatisHelper
operator|.
name|newRaftClient
argument_list|(
name|SupportedRpcType
operator|.
name|GRPC
argument_list|,
name|peer
argument_list|,
name|retryPolicy
argument_list|,
name|maxOutstandingRequests
argument_list|,
name|TimeDuration
operator|.
name|valueOf
argument_list|(
literal|3
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|client
operator|.
name|groupAdd
argument_list|(
name|group
argument_list|,
name|peer
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
specifier|final
name|ContainerID
name|containerId
init|=
name|ContainerID
operator|.
name|valueof
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
operator|&
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
operator|.
name|Builder
name|request
init|=
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|request
operator|.
name|setCmdType
argument_list|(
name|ContainerProtos
operator|.
name|Type
operator|.
name|CreateContainer
argument_list|)
expr_stmt|;
name|request
operator|.
name|setContainerID
argument_list|(
name|containerId
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|setCreateContainer
argument_list|(
name|ContainerProtos
operator|.
name|CreateContainerRequestProto
operator|.
name|getDefaultInstance
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|setDatanodeUuid
argument_list|(
name|datanodeDetails
operator|.
name|getUuidString
argument_list|()
argument_list|)
expr_stmt|;
name|ozoneContainer
operator|.
name|getWriteChannel
argument_list|()
operator|.
name|submitRequest
argument_list|(
name|request
operator|.
name|build
argument_list|()
argument_list|,
name|pipelineID
operator|.
name|getProtobuf
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Container
name|container
init|=
name|ozoneContainer
operator|.
name|getContainerSet
argument_list|()
operator|.
name|getContainer
argument_list|(
name|containerId
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|OPEN
argument_list|,
name|container
operator|.
name|getContainerState
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|container
return|;
block|}
comment|/**    * Creates a random DatanodeDetails.    * @return DatanodeDetails    */
DECL|method|randomDatanodeDetails ()
specifier|private
specifier|static
name|DatanodeDetails
name|randomDatanodeDetails
parameter_list|()
block|{
name|String
name|ipAddress
init|=
literal|"127.0.0.1"
decl_stmt|;
name|DatanodeDetails
operator|.
name|Port
name|containerPort
init|=
name|DatanodeDetails
operator|.
name|newPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|STANDALONE
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DatanodeDetails
operator|.
name|Port
name|ratisPort
init|=
name|DatanodeDetails
operator|.
name|newPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|RATIS
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DatanodeDetails
operator|.
name|Port
name|restPort
init|=
name|DatanodeDetails
operator|.
name|newPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|REST
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DatanodeDetails
operator|.
name|Builder
name|builder
init|=
name|DatanodeDetails
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setUuid
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setHostName
argument_list|(
literal|"localhost"
argument_list|)
operator|.
name|setIpAddress
argument_list|(
name|ipAddress
argument_list|)
operator|.
name|addPort
argument_list|(
name|containerPort
argument_list|)
operator|.
name|addPort
argument_list|(
name|ratisPort
argument_list|)
operator|.
name|addPort
argument_list|(
name|restPort
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|AfterClass
DECL|method|teardown ()
specifier|public
specifier|static
name|void
name|teardown
parameter_list|()
throws|throws
name|IOException
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

