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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|container
operator|.
name|ContainerID
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
name|*
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

begin_comment
comment|/**  * Holds the data structures which maintain the information about pipeline and  * its state. All the read write operations in this class are protected by a  * lock.  * Invariant: If a pipeline exists in PipelineStateMap, both pipelineMap and  * pipeline2container would have a non-null mapping for it.  */
end_comment

begin_class
DECL|class|PipelineStateMap
class|class
name|PipelineStateMap
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
name|PipelineStateMap
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|pipelineMap
specifier|private
specifier|final
name|Map
argument_list|<
name|PipelineID
argument_list|,
name|Pipeline
argument_list|>
name|pipelineMap
decl_stmt|;
DECL|field|pipeline2container
specifier|private
specifier|final
name|Map
argument_list|<
name|PipelineID
argument_list|,
name|Set
argument_list|<
name|ContainerID
argument_list|>
argument_list|>
name|pipeline2container
decl_stmt|;
DECL|method|PipelineStateMap ()
name|PipelineStateMap
parameter_list|()
block|{
name|this
operator|.
name|pipelineMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|pipeline2container
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Adds provided pipeline in the data structures.    *    * @param pipeline - Pipeline to add    * @throws IOException if pipeline with provided pipelineID already exists    */
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
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipeline
argument_list|,
literal|"Pipeline cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|pipeline
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|==
name|pipeline
operator|.
name|getFactor
argument_list|()
operator|.
name|getNumber
argument_list|()
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"Nodes size=%d, replication factor=%d do not match "
argument_list|,
name|pipeline
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|pipeline
operator|.
name|getFactor
argument_list|()
operator|.
name|getNumber
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|pipelineMap
operator|.
name|putIfAbsent
argument_list|(
name|pipeline
operator|.
name|getID
argument_list|()
argument_list|,
name|pipeline
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Duplicate pipeline ID detected. {}"
argument_list|,
name|pipeline
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Duplicate pipeline ID %s detected."
argument_list|,
name|pipeline
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|pipeline2container
operator|.
name|put
argument_list|(
name|pipeline
operator|.
name|getID
argument_list|()
argument_list|,
operator|new
name|TreeSet
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add container to an existing pipeline.    *    * @param pipelineID - PipelineID of the pipeline to which container is added    * @param containerID - ContainerID of the container to add    * @throws IOException if pipeline is not in open state or does not exist    */
DECL|method|addContainerToPipeline (PipelineID pipelineID, ContainerID containerID)
name|void
name|addContainerToPipeline
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
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipelineID
argument_list|,
literal|"Pipeline Id cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerID
argument_list|,
literal|"container Id cannot be null"
argument_list|)
expr_stmt|;
name|Pipeline
name|pipeline
init|=
name|getPipeline
argument_list|(
name|pipelineID
argument_list|)
decl_stmt|;
comment|// TODO: verify the state we need the pipeline to be in
if|if
condition|(
operator|!
name|isOpen
argument_list|(
name|pipeline
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s is not in open state"
argument_list|,
name|pipelineID
argument_list|)
argument_list|)
throw|;
block|}
name|pipeline2container
operator|.
name|get
argument_list|(
name|pipelineID
argument_list|)
operator|.
name|add
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get pipeline corresponding to specified pipelineID.    *    * @param pipelineID - PipelineID of the pipeline to be retrieved    * @return Pipeline    * @throws IOException if pipeline is not found    */
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
name|Pipeline
name|pipeline
init|=
name|pipelineMap
operator|.
name|get
argument_list|(
name|pipelineID
argument_list|)
decl_stmt|;
if|if
condition|(
name|pipeline
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s not found"
argument_list|,
name|pipelineID
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|pipeline
return|;
block|}
comment|/**    * Get pipeline corresponding to specified replication type.    *    * @param type - ReplicationType    * @return List of pipelines which have the specified replication type    */
DECL|method|getPipelines (ReplicationType type)
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getPipelines
parameter_list|(
name|ReplicationType
name|type
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|type
argument_list|,
literal|"Replication type cannot be null"
argument_list|)
expr_stmt|;
return|return
name|pipelineMap
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|p
lambda|->
name|p
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|type
argument_list|)
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
comment|/**    * Get set of containers corresponding to a pipeline.    *    * @param pipelineID - PipelineID    * @return Set of Containers belonging to the pipeline    * @throws IOException if pipeline is not found    */
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
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|containerIDs
init|=
name|pipeline2container
operator|.
name|get
argument_list|(
name|pipelineID
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerIDs
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s not found"
argument_list|,
name|pipelineID
argument_list|)
argument_list|)
throw|;
block|}
return|return
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|containerIDs
argument_list|)
return|;
block|}
comment|/**    * Remove pipeline from the data structures.    *    * @param pipelineID - PipelineID of the pipeline to be removed    * @throws IOException if the pipeline is not empty or does not exist    */
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
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipelineID
argument_list|,
literal|"Pipeline Id cannot be null"
argument_list|)
expr_stmt|;
comment|//TODO: Add a flag which suppresses exception if pipeline does not exist?
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|containerIDs
init|=
name|getContainers
argument_list|(
name|pipelineID
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerIDs
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Pipeline with %s is not empty"
argument_list|,
name|pipelineID
argument_list|)
argument_list|)
throw|;
block|}
name|pipelineMap
operator|.
name|remove
argument_list|(
name|pipelineID
argument_list|)
expr_stmt|;
name|pipeline2container
operator|.
name|remove
argument_list|(
name|pipelineID
argument_list|)
expr_stmt|;
block|}
comment|/**    * Remove container from a pipeline.    *    * @param pipelineID - PipelineID of the pipeline from which container needs    *                   to be removed    * @param containerID - ContainerID of the container to remove    * @throws IOException if pipeline does not exist    */
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
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipelineID
argument_list|,
literal|"Pipeline Id cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerID
argument_list|,
literal|"container Id cannot be null"
argument_list|)
expr_stmt|;
name|Pipeline
name|pipeline
init|=
name|getPipeline
argument_list|(
name|pipelineID
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|containerIDs
init|=
name|pipeline2container
operator|.
name|get
argument_list|(
name|pipelineID
argument_list|)
decl_stmt|;
name|containerIDs
operator|.
name|remove
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
if|if
condition|(
name|containerIDs
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|&&
name|isClosingOrClosed
argument_list|(
name|pipeline
argument_list|)
condition|)
block|{
name|removePipeline
argument_list|(
name|pipelineID
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Updates the state of pipeline.    *    * @param pipelineID - PipelineID of the pipeline whose state needs    *                   to be updated    * @param state - new state of the pipeline    * @return Pipeline with the updated state    * @throws IOException if pipeline does not exist    */
DECL|method|updatePipelineState (PipelineID pipelineID, LifeCycleState state)
name|Pipeline
name|updatePipelineState
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|,
name|LifeCycleState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipelineID
argument_list|,
literal|"Pipeline Id cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|state
argument_list|,
literal|"Pipeline LifeCycleState cannot be null"
argument_list|)
expr_stmt|;
name|Pipeline
name|pipeline
init|=
name|getPipeline
argument_list|(
name|pipelineID
argument_list|)
decl_stmt|;
name|pipeline
operator|=
name|pipelineMap
operator|.
name|put
argument_list|(
name|pipelineID
argument_list|,
name|Pipeline
operator|.
name|newBuilder
argument_list|(
name|pipeline
argument_list|)
operator|.
name|setState
argument_list|(
name|state
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO: Verify if need to throw exception for non-existent pipeline
return|return
name|pipeline
return|;
block|}
DECL|method|isClosingOrClosed (Pipeline pipeline)
specifier|private
name|boolean
name|isClosingOrClosed
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
block|{
name|LifeCycleState
name|state
init|=
name|pipeline
operator|.
name|getLifeCycleState
argument_list|()
decl_stmt|;
return|return
name|state
operator|==
name|LifeCycleState
operator|.
name|CLOSING
operator|||
name|state
operator|==
name|LifeCycleState
operator|.
name|CLOSED
return|;
block|}
DECL|method|isOpen (Pipeline pipeline)
specifier|private
name|boolean
name|isOpen
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
block|{
return|return
name|pipeline
operator|.
name|getLifeCycleState
argument_list|()
operator|==
name|LifeCycleState
operator|.
name|OPEN
return|;
block|}
block|}
end_class

end_unit

