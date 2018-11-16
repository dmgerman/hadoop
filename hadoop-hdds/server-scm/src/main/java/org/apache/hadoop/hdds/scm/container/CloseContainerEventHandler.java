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
name|stream
operator|.
name|Collectors
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
name|LifeCycleState
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
name|PipelineNotFoundException
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
DECL|field|pipelineManager
specifier|private
specifier|final
name|PipelineManager
name|pipelineManager
decl_stmt|;
DECL|field|containerManager
specifier|private
specifier|final
name|ContainerManager
name|containerManager
decl_stmt|;
DECL|method|CloseContainerEventHandler (final PipelineManager pipelineManager, final ContainerManager containerManager)
specifier|public
name|CloseContainerEventHandler
parameter_list|(
specifier|final
name|PipelineManager
name|pipelineManager
parameter_list|,
specifier|final
name|ContainerManager
name|containerManager
parameter_list|)
block|{
name|this
operator|.
name|pipelineManager
operator|=
name|pipelineManager
expr_stmt|;
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
argument_list|)
expr_stmt|;
try|try
block|{
comment|// If the container is in OPEN state, FINALIZE it.
if|if
condition|(
name|containerManager
operator|.
name|getContainer
argument_list|(
name|containerID
argument_list|)
operator|.
name|getState
argument_list|()
operator|==
name|LifeCycleState
operator|.
name|OPEN
condition|)
block|{
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|containerID
argument_list|,
name|LifeCycleEvent
operator|.
name|FINALIZE
argument_list|)
expr_stmt|;
block|}
comment|// ContainerInfo has to read again after the above state change.
specifier|final
name|ContainerInfo
name|container
init|=
name|containerManager
operator|.
name|getContainer
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
comment|// Send close command to datanodes, if the container is in CLOSING state
if|if
condition|(
name|container
operator|.
name|getState
argument_list|()
operator|==
name|LifeCycleState
operator|.
name|CLOSING
condition|)
block|{
specifier|final
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
name|container
operator|.
name|getPipelineID
argument_list|()
argument_list|)
decl_stmt|;
name|getNodes
argument_list|(
name|container
argument_list|)
operator|.
name|forEach
argument_list|(
name|node
lambda|->
name|publisher
operator|.
name|fireEvent
argument_list|(
name|DATANODE_COMMAND
argument_list|,
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
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot close container {}, which is in {} state."
argument_list|,
name|containerID
argument_list|,
name|container
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"Failed to close the container {}."
argument_list|,
name|containerID
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns the list of Datanodes where this container lives.    *    * @param container ContainerInfo    * @return list of DatanodeDetails    * @throws ContainerNotFoundException    */
DECL|method|getNodes (final ContainerInfo container)
specifier|private
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|getNodes
parameter_list|(
specifier|final
name|ContainerInfo
name|container
parameter_list|)
throws|throws
name|ContainerNotFoundException
block|{
try|try
block|{
return|return
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|container
operator|.
name|getPipelineID
argument_list|()
argument_list|)
operator|.
name|getNodes
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|PipelineNotFoundException
name|ex
parameter_list|)
block|{
comment|// Use container replica if the pipeline is not available.
return|return
name|containerManager
operator|.
name|getContainerReplicas
argument_list|(
name|container
operator|.
name|containerID
argument_list|()
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|ContainerReplica
operator|::
name|getDatanodeDetails
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

