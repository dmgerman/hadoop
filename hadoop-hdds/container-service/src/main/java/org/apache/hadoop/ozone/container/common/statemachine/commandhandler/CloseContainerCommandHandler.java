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
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|InvalidProtocolBufferException
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
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
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
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMCommandProto
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
name|StorageContainerDatanodeProtocolProtos
operator|.
name|CloseContainerCommandProto
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
name|SCMConnectionManager
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
name|ContainerController
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
name|SCMCommand
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
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|NotLeaderException
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
name|UUID
import|;
end_import

begin_comment
comment|/**  * Handler for close container command received from SCM.  */
end_comment

begin_class
DECL|class|CloseContainerCommandHandler
specifier|public
class|class
name|CloseContainerCommandHandler
implements|implements
name|CommandHandler
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CloseContainerCommandHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|invocationCount
specifier|private
name|int
name|invocationCount
decl_stmt|;
DECL|field|totalTime
specifier|private
name|long
name|totalTime
decl_stmt|;
comment|/**    * Constructs a ContainerReport handler.    */
DECL|method|CloseContainerCommandHandler ()
specifier|public
name|CloseContainerCommandHandler
parameter_list|()
block|{   }
comment|/**    * Handles a given SCM command.    *    * @param command           - SCM Command    * @param ozoneContainer         - Ozone Container.    * @param context           - Current Context.    * @param connectionManager - The SCMs that we are talking to.    */
annotation|@
name|Override
DECL|method|handle (SCMCommand command, OzoneContainer ozoneContainer, StateContext context, SCMConnectionManager connectionManager)
specifier|public
name|void
name|handle
parameter_list|(
name|SCMCommand
name|command
parameter_list|,
name|OzoneContainer
name|ozoneContainer
parameter_list|,
name|StateContext
name|context
parameter_list|,
name|SCMConnectionManager
name|connectionManager
parameter_list|)
block|{
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Processing Close Container command."
argument_list|)
expr_stmt|;
name|invocationCount
operator|++
expr_stmt|;
specifier|final
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
specifier|final
name|DatanodeDetails
name|datanodeDetails
init|=
name|context
operator|.
name|getParent
argument_list|()
operator|.
name|getDatanodeDetails
argument_list|()
decl_stmt|;
specifier|final
name|CloseContainerCommandProto
name|closeCommand
init|=
name|CloseContainerCommandProto
operator|.
name|parseFrom
argument_list|(
name|command
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ContainerController
name|controller
init|=
name|ozoneContainer
operator|.
name|getController
argument_list|()
decl_stmt|;
specifier|final
name|long
name|containerId
init|=
name|closeCommand
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|Container
name|container
init|=
name|controller
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|container
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Container #{} does not exist in datanode. "
operator|+
literal|"Container close failed."
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Move the container to CLOSING state
name|controller
operator|.
name|markContainerForClose
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
comment|// If the container is part of open pipeline, close it via write channel
if|if
condition|(
name|ozoneContainer
operator|.
name|getWriteChannel
argument_list|()
operator|.
name|isExist
argument_list|(
name|closeCommand
operator|.
name|getPipelineID
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|closeCommand
operator|.
name|getForce
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot force close a container when the container is"
operator|+
literal|" part of an active pipeline."
argument_list|)
expr_stmt|;
return|return;
block|}
name|ContainerCommandRequestProto
name|request
init|=
name|getContainerCommandRequestProto
argument_list|(
name|datanodeDetails
argument_list|,
name|closeCommand
operator|.
name|getContainerID
argument_list|()
argument_list|)
decl_stmt|;
name|ozoneContainer
operator|.
name|getWriteChannel
argument_list|()
operator|.
name|submitRequest
argument_list|(
name|request
argument_list|,
name|closeCommand
operator|.
name|getPipelineID
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// If we reach here, there is no active pipeline for this container.
if|if
condition|(
operator|!
name|closeCommand
operator|.
name|getForce
argument_list|()
condition|)
block|{
comment|// QUASI_CLOSE the container.
name|controller
operator|.
name|quasiCloseContainer
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// SCM told us to force close the container.
name|controller
operator|.
name|closeContainer
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NotLeaderException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Follower cannot close container #{}."
argument_list|,
name|containerId
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
name|error
argument_list|(
literal|"Can't close container #{}"
argument_list|,
name|containerId
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|long
name|endTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|totalTime
operator|+=
name|endTime
operator|-
name|startTime
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InvalidProtocolBufferException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while closing container"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getContainerCommandRequestProto ( final DatanodeDetails datanodeDetails, final long containerId)
specifier|private
name|ContainerCommandRequestProto
name|getContainerCommandRequestProto
parameter_list|(
specifier|final
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
specifier|final
name|long
name|containerId
parameter_list|)
block|{
specifier|final
name|ContainerCommandRequestProto
operator|.
name|Builder
name|command
init|=
name|ContainerCommandRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|command
operator|.
name|setCmdType
argument_list|(
name|ContainerProtos
operator|.
name|Type
operator|.
name|CloseContainer
argument_list|)
expr_stmt|;
name|command
operator|.
name|setContainerID
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|command
operator|.
name|setCloseContainer
argument_list|(
name|ContainerProtos
operator|.
name|CloseContainerRequestProto
operator|.
name|getDefaultInstance
argument_list|()
argument_list|)
expr_stmt|;
name|command
operator|.
name|setTraceID
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
name|command
operator|.
name|setDatanodeUuid
argument_list|(
name|datanodeDetails
operator|.
name|getUuidString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|command
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Returns the command type that this command handler handles.    *    * @return Type    */
annotation|@
name|Override
DECL|method|getCommandType ()
specifier|public
name|SCMCommandProto
operator|.
name|Type
name|getCommandType
parameter_list|()
block|{
return|return
name|SCMCommandProto
operator|.
name|Type
operator|.
name|closeContainerCommand
return|;
block|}
comment|/**    * Returns number of times this handler has been invoked.    *    * @return int    */
annotation|@
name|Override
DECL|method|getInvocationCount ()
specifier|public
name|int
name|getInvocationCount
parameter_list|()
block|{
return|return
name|invocationCount
return|;
block|}
comment|/**    * Returns the average time this function takes to run.    *    * @return long    */
annotation|@
name|Override
DECL|method|getAverageRunTime ()
specifier|public
name|long
name|getAverageRunTime
parameter_list|()
block|{
if|if
condition|(
name|invocationCount
operator|>
literal|0
condition|)
block|{
return|return
name|totalTime
operator|/
name|invocationCount
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

