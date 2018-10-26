begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  *<p>http://www.apache.org/licenses/LICENSE-2.0  *<p>  *<p>Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|hdds
operator|.
name|server
operator|.
name|events
operator|.
name|EventHandler
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
name|EventPublisher
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
name|IdentifiableEventPayload
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|CommandForDatanode
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
name|CLOSE_CONTAINER_RETRYABLE_REQ
import|;
end_import

begin_comment
comment|/**  * In case of a node failure, volume failure, volume out of spapce, node  * out of space etc, CLOSE_CONTAINER will be triggered.  * CloseContainerEventHandler is the handler for CLOSE_CONTAINER.  * When a close container event is fired, a close command for the container  * should be sent to all the datanodes in the pipeline and containerStateManager  * needs to update the container state to Closing.  */
end_comment

begin_class
DECL|class|CloseContainerEventHandler
specifier|public
class|class
name|CloseContainerEventHandler
implements|implements
name|EventHandler
argument_list|<
name|ContainerID
argument_list|>
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CloseContainerEventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|containerManager
specifier|private
specifier|final
name|ContainerManager
name|containerManager
decl_stmt|;
DECL|method|CloseContainerEventHandler (ContainerManager containerManager)
specifier|public
name|CloseContainerEventHandler
parameter_list|(
name|ContainerManager
name|containerManager
parameter_list|)
block|{
name|this
operator|.
name|containerManager
operator|=
name|containerManager
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onMessage (ContainerID containerID, EventPublisher publisher)
specifier|public
name|void
name|onMessage
parameter_list|(
name|ContainerID
name|containerID
parameter_list|,
name|EventPublisher
name|publisher
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Close container Event triggered for container : {}"
argument_list|,
name|containerID
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerWithPipeline
name|containerWithPipeline
decl_stmt|;
name|ContainerInfo
name|info
decl_stmt|;
try|try
block|{
name|containerWithPipeline
operator|=
name|containerManager
operator|.
name|getContainerWithPipeline
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
name|info
operator|=
name|containerWithPipeline
operator|.
name|getContainerInfo
argument_list|()
expr_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to update the container state. Container with id : {}"
operator|+
literal|" does not exist"
argument_list|,
name|containerID
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
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
literal|"Failed to update the container state. Container with id : {} "
operator|+
literal|"does not exist"
argument_list|,
name|containerID
operator|.
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|HddsProtos
operator|.
name|LifeCycleState
name|state
init|=
name|info
operator|.
name|getState
argument_list|()
decl_stmt|;
try|try
block|{
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|ALLOCATED
case|:
comment|// We cannot close a container in ALLOCATED state, moving the
comment|// container to CREATING state, this should eventually
comment|// timeout and the container will be moved to DELETING state.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Closing container #{} in {} state"
argument_list|,
name|containerID
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|containerID
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CREATE
argument_list|)
expr_stmt|;
break|break;
case|case
name|CREATING
case|:
comment|// We cannot close a container in CREATING state, it will eventually
comment|// timeout and moved to DELETING state.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Closing container {} in {} state"
argument_list|,
name|containerID
argument_list|,
name|state
argument_list|)
expr_stmt|;
break|break;
case|case
name|OPEN
case|:
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|containerID
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|FINALIZE
argument_list|)
expr_stmt|;
name|fireCloseContainerEvents
argument_list|(
name|containerWithPipeline
argument_list|,
name|info
argument_list|,
name|publisher
argument_list|)
expr_stmt|;
break|break;
case|case
name|CLOSING
case|:
name|fireCloseContainerEvents
argument_list|(
name|containerWithPipeline
argument_list|,
name|info
argument_list|,
name|publisher
argument_list|)
expr_stmt|;
break|break;
case|case
name|CLOSED
case|:
case|case
name|DELETING
case|:
case|case
name|DELETED
case|:
name|LOG
operator|.
name|info
argument_list|(
literal|"Cannot close container #{}, it is already in {} state."
argument_list|,
name|containerID
operator|.
name|getId
argument_list|()
argument_list|,
name|state
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid container state for container #"
operator|+
name|containerID
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to update the container state for container #{}"
operator|+
name|containerID
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fireCloseContainerEvents ( ContainerWithPipeline containerWithPipeline, ContainerInfo info, EventPublisher publisher)
specifier|private
name|void
name|fireCloseContainerEvents
parameter_list|(
name|ContainerWithPipeline
name|containerWithPipeline
parameter_list|,
name|ContainerInfo
name|info
parameter_list|,
name|EventPublisher
name|publisher
parameter_list|)
block|{
name|ContainerID
name|containerID
init|=
name|info
operator|.
name|containerID
argument_list|()
decl_stmt|;
comment|// fire events.
name|CloseContainerCommand
name|closeContainerCommand
init|=
operator|new
name|CloseContainerCommand
argument_list|(
name|containerID
operator|.
name|getId
argument_list|()
argument_list|,
name|info
operator|.
name|getReplicationType
argument_list|()
argument_list|,
name|info
operator|.
name|getPipelineID
argument_list|()
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|containerWithPipeline
operator|.
name|getPipeline
argument_list|()
decl_stmt|;
name|pipeline
operator|.
name|getNodes
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|node
lambda|->
operator|new
name|CommandForDatanode
argument_list|<>
argument_list|(
name|node
operator|.
name|getUuid
argument_list|()
argument_list|,
name|closeContainerCommand
argument_list|)
argument_list|)
operator|.
name|forEach
argument_list|(
name|command
lambda|->
name|publisher
operator|.
name|fireEvent
argument_list|(
name|DATANODE_COMMAND
argument_list|,
name|command
argument_list|)
argument_list|)
expr_stmt|;
name|publisher
operator|.
name|fireEvent
argument_list|(
name|CLOSE_CONTAINER_RETRYABLE_REQ
argument_list|,
operator|new
name|CloseContainerRetryableReq
argument_list|(
name|containerID
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Issuing {} on Pipeline {} for container"
argument_list|,
name|closeContainerCommand
argument_list|,
name|pipeline
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
block|}
comment|/**    * Class to create retryable event. Prevents redundant requests for same    * container Id.    */
DECL|class|CloseContainerRetryableReq
specifier|public
specifier|static
class|class
name|CloseContainerRetryableReq
implements|implements
name|IdentifiableEventPayload
block|{
DECL|field|containerID
specifier|private
name|ContainerID
name|containerID
decl_stmt|;
DECL|method|CloseContainerRetryableReq (ContainerID containerID)
specifier|public
name|CloseContainerRetryableReq
parameter_list|(
name|ContainerID
name|containerID
parameter_list|)
block|{
name|this
operator|.
name|containerID
operator|=
name|containerID
expr_stmt|;
block|}
DECL|method|getContainerID ()
specifier|public
name|ContainerID
name|getContainerID
parameter_list|()
block|{
return|return
name|containerID
return|;
block|}
annotation|@
name|Override
DECL|method|getId ()
specifier|public
name|long
name|getId
parameter_list|()
block|{
return|return
name|containerID
operator|.
name|getId
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

