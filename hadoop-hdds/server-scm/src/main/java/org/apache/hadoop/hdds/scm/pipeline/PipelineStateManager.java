begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.pipeline
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
name|pipeline
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
name|Pipeline
operator|.
name|PipelineState
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Manages the state of pipelines in SCM. All write operations like pipeline  * creation, removal and updates should come via SCMPipelineManager.  * PipelineStateMap class holds the data structures related to pipeline and its  * state. All the read and write operations in PipelineStateMap are protected  * by a read write lock.  */
end_comment

begin_class
DECL|class|PipelineStateManager
class|class
name|PipelineStateManager
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
name|pipelines
operator|.
name|PipelineStateManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|pipelineStateMap
specifier|private
specifier|final
name|PipelineStateMap
name|pipelineStateMap
decl_stmt|;
DECL|method|PipelineStateManager (Configuration conf)
name|PipelineStateManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|pipelineStateMap
operator|=
operator|new
name|PipelineStateMap
argument_list|()
expr_stmt|;
block|}
DECL|method|addPipeline (Pipeline pipeline)
name|void
name|addPipeline
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
block|{
name|pipelineStateMap
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
DECL|method|addContainerToPipeline (PipelineID pipelineId, ContainerID containerID)
name|void
name|addContainerToPipeline
parameter_list|(
name|PipelineID
name|pipelineId
parameter_list|,
name|ContainerID
name|containerID
parameter_list|)
throws|throws
name|IOException
block|{
name|pipelineStateMap
operator|.
name|addContainerToPipeline
argument_list|(
name|pipelineId
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
block|}
DECL|method|getPipeline (PipelineID pipelineID)
name|Pipeline
name|getPipeline
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|pipelineStateMap
operator|.
name|getPipeline
argument_list|(
name|pipelineID
argument_list|)
return|;
block|}
DECL|method|getPipelinesByType (ReplicationType type)
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getPipelinesByType
parameter_list|(
name|ReplicationType
name|type
parameter_list|)
block|{
return|return
name|pipelineStateMap
operator|.
name|getPipelinesByType
argument_list|(
name|type
argument_list|)
return|;
block|}
DECL|method|getPipelinesByTypeAndFactor (ReplicationType type, ReplicationFactor factor)
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getPipelinesByTypeAndFactor
parameter_list|(
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|)
block|{
return|return
name|pipelineStateMap
operator|.
name|getPipelinesByTypeAndFactor
argument_list|(
name|type
argument_list|,
name|factor
argument_list|)
return|;
block|}
DECL|method|getContainers (PipelineID pipelineID)
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|getContainers
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|pipelineStateMap
operator|.
name|getContainers
argument_list|(
name|pipelineID
argument_list|)
return|;
block|}
DECL|method|getNumberOfContainers (PipelineID pipelineID)
name|int
name|getNumberOfContainers
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|pipelineStateMap
operator|.
name|getNumberOfContainers
argument_list|(
name|pipelineID
argument_list|)
return|;
block|}
DECL|method|removePipeline (PipelineID pipelineID)
name|void
name|removePipeline
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
name|pipelineStateMap
operator|.
name|removePipeline
argument_list|(
name|pipelineID
argument_list|)
expr_stmt|;
block|}
DECL|method|removeContainerFromPipeline (PipelineID pipelineID, ContainerID containerID)
name|void
name|removeContainerFromPipeline
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|,
name|ContainerID
name|containerID
parameter_list|)
throws|throws
name|IOException
block|{
name|pipelineStateMap
operator|.
name|removeContainerFromPipeline
argument_list|(
name|pipelineID
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
block|}
DECL|method|finalizePipeline (PipelineID pipelineId)
name|Pipeline
name|finalizePipeline
parameter_list|(
name|PipelineID
name|pipelineId
parameter_list|)
throws|throws
name|IOException
block|{
name|Pipeline
name|pipeline
init|=
name|pipelineStateMap
operator|.
name|getPipeline
argument_list|(
name|pipelineId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|pipeline
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|pipeline
operator|=
name|pipelineStateMap
operator|.
name|updatePipelineState
argument_list|(
name|pipelineId
argument_list|,
name|PipelineState
operator|.
name|CLOSED
argument_list|)
expr_stmt|;
block|}
return|return
name|pipeline
return|;
block|}
DECL|method|openPipeline (PipelineID pipelineId)
name|Pipeline
name|openPipeline
parameter_list|(
name|PipelineID
name|pipelineId
parameter_list|)
throws|throws
name|IOException
block|{
name|Pipeline
name|pipeline
init|=
name|pipelineStateMap
operator|.
name|getPipeline
argument_list|(
name|pipelineId
argument_list|)
decl_stmt|;
if|if
condition|(
name|pipeline
operator|.
name|isClosed
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Closed pipeline can not be opened"
argument_list|)
throw|;
block|}
if|if
condition|(
name|pipeline
operator|.
name|getPipelineState
argument_list|()
operator|==
name|PipelineState
operator|.
name|ALLOCATED
condition|)
block|{
name|pipeline
operator|=
name|pipelineStateMap
operator|.
name|updatePipelineState
argument_list|(
name|pipelineId
argument_list|,
name|PipelineState
operator|.
name|OPEN
argument_list|)
expr_stmt|;
block|}
return|return
name|pipeline
return|;
block|}
block|}
end_class

end_unit

