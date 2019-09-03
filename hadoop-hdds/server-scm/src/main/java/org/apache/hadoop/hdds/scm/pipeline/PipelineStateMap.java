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
name|edu
operator|.
name|umd
operator|.
name|cs
operator|.
name|findbugs
operator|.
name|annotations
operator|.
name|SuppressFBWarnings
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
name|builder
operator|.
name|HashCodeBuilder
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
name|*
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
name|CopyOnWriteArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
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
comment|/**  * Holds the data structures which maintain the information about pipeline and  * its state.  * Invariant: If a pipeline exists in PipelineStateMap, both pipelineMap and  * pipeline2container would have a non-null mapping for it.  */
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
name|NavigableSet
argument_list|<
name|ContainerID
argument_list|>
argument_list|>
name|pipeline2container
decl_stmt|;
DECL|field|query2OpenPipelines
specifier|private
specifier|final
name|Map
argument_list|<
name|PipelineQuery
argument_list|,
name|List
argument_list|<
name|Pipeline
argument_list|>
argument_list|>
name|query2OpenPipelines
decl_stmt|;
DECL|method|PipelineStateMap ()
name|PipelineStateMap
parameter_list|()
block|{
comment|// TODO: Use TreeMap for range operations?
name|pipelineMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|pipeline2container
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|query2OpenPipelines
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|initializeQueryMap
argument_list|()
expr_stmt|;
block|}
DECL|method|initializeQueryMap ()
specifier|private
name|void
name|initializeQueryMap
parameter_list|()
block|{
for|for
control|(
name|ReplicationType
name|type
range|:
name|ReplicationType
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|ReplicationFactor
name|factor
range|:
name|ReplicationFactor
operator|.
name|values
argument_list|()
control|)
block|{
name|query2OpenPipelines
operator|.
name|put
argument_list|(
operator|new
name|PipelineQuery
argument_list|(
name|type
argument_list|,
name|factor
argument_list|)
argument_list|,
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|getId
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
name|getId
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
name|getId
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
name|getId
argument_list|()
argument_list|,
operator|new
name|TreeSet
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|pipeline
operator|.
name|getPipelineState
argument_list|()
operator|==
name|PipelineState
operator|.
name|OPEN
condition|)
block|{
name|query2OpenPipelines
operator|.
name|get
argument_list|(
operator|new
name|PipelineQuery
argument_list|(
name|pipeline
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
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
literal|"Container Id cannot be null"
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
name|String
operator|.
name|format
argument_list|(
literal|"Cannot add container to pipeline=%s in closed state"
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
name|PipelineNotFoundException
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
name|PipelineNotFoundException
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
comment|/**    * Get list of pipelines in SCM.    * @return List of pipelines    */
DECL|method|getPipelines ()
specifier|public
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getPipelines
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|pipelineMap
operator|.
name|values
argument_list|()
argument_list|)
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
comment|/**    * Get pipeline corresponding to specified replication type and factor.    *    * @param type - ReplicationType    * @param factor - ReplicationFactor    * @return List of pipelines with specified replication type and factor    */
DECL|method|getPipelines (ReplicationType type, ReplicationFactor factor)
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getPipelines
parameter_list|(
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
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
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|factor
argument_list|,
literal|"Replication factor cannot be null"
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
name|pipeline
lambda|->
name|pipeline
operator|.
name|getType
argument_list|()
operator|==
name|type
operator|&&
name|pipeline
operator|.
name|getFactor
argument_list|()
operator|==
name|factor
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
comment|/**    * Get list of pipeline corresponding to specified replication type and    * pipeline states.    *    * @param type - ReplicationType    * @param states - Array of required PipelineState    * @return List of pipelines with specified replication type and states    */
DECL|method|getPipelines (ReplicationType type, PipelineState... states)
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getPipelines
parameter_list|(
name|ReplicationType
name|type
parameter_list|,
name|PipelineState
modifier|...
name|states
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
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|states
argument_list|,
literal|"Pipeline state cannot be null"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|PipelineState
argument_list|>
name|pipelineStates
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|pipelineStates
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|states
argument_list|)
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
name|pipeline
lambda|->
name|pipeline
operator|.
name|getType
argument_list|()
operator|==
name|type
operator|&&
name|pipelineStates
operator|.
name|contains
argument_list|(
name|pipeline
operator|.
name|getPipelineState
argument_list|()
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
comment|/**    * Get list of pipeline corresponding to specified replication type,    * replication factor and pipeline state.    *    * @param type - ReplicationType    * @param state - Required PipelineState    * @return List of pipelines with specified replication type,    * replication factor and pipeline state    */
DECL|method|getPipelines (ReplicationType type, ReplicationFactor factor, PipelineState state)
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getPipelines
parameter_list|(
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|,
name|PipelineState
name|state
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
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|factor
argument_list|,
literal|"Replication factor cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|state
argument_list|,
literal|"Pipeline state cannot be null"
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|==
name|PipelineState
operator|.
name|OPEN
condition|)
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|query2OpenPipelines
operator|.
name|get
argument_list|(
operator|new
name|PipelineQuery
argument_list|(
name|type
argument_list|,
name|factor
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
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
name|pipeline
lambda|->
name|pipeline
operator|.
name|getType
argument_list|()
operator|==
name|type
operator|&&
name|pipeline
operator|.
name|getPipelineState
argument_list|()
operator|==
name|state
operator|&&
name|pipeline
operator|.
name|getFactor
argument_list|()
operator|==
name|factor
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
comment|/**    * Get list of pipeline corresponding to specified replication type,    * replication factor and pipeline state.    *    * @param type - ReplicationType    * @param state - Required PipelineState    * @param excludeDns list of dns to exclude    * @param excludePipelines pipelines to exclude    * @return List of pipelines with specified replication type,    * replication factor and pipeline state    */
DECL|method|getPipelines (ReplicationType type, ReplicationFactor factor, PipelineState state, Collection<DatanodeDetails> excludeDns, Collection<PipelineID> excludePipelines)
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getPipelines
parameter_list|(
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|,
name|PipelineState
name|state
parameter_list|,
name|Collection
argument_list|<
name|DatanodeDetails
argument_list|>
name|excludeDns
parameter_list|,
name|Collection
argument_list|<
name|PipelineID
argument_list|>
name|excludePipelines
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
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|factor
argument_list|,
literal|"Replication factor cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|state
argument_list|,
literal|"Pipeline state cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|excludeDns
argument_list|,
literal|"Datanode exclude list cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|excludeDns
argument_list|,
literal|"Pipeline exclude list cannot be null"
argument_list|)
expr_stmt|;
return|return
name|getPipelines
argument_list|(
name|type
argument_list|,
name|factor
argument_list|,
name|state
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|pipeline
lambda|->
operator|!
name|discardPipeline
argument_list|(
name|pipeline
argument_list|,
name|excludePipelines
argument_list|)
operator|&&
operator|!
name|discardDatanode
argument_list|(
name|pipeline
argument_list|,
name|excludeDns
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
DECL|method|discardPipeline (Pipeline pipeline, Collection<PipelineID> excludePipelines)
specifier|private
name|boolean
name|discardPipeline
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|Collection
argument_list|<
name|PipelineID
argument_list|>
name|excludePipelines
parameter_list|)
block|{
if|if
condition|(
name|excludePipelines
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Predicate
argument_list|<
name|PipelineID
argument_list|>
name|predicate
init|=
name|p
lambda|->
name|p
operator|.
name|equals
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|excludePipelines
operator|.
name|parallelStream
argument_list|()
operator|.
name|anyMatch
argument_list|(
name|predicate
argument_list|)
return|;
block|}
DECL|method|discardDatanode (Pipeline pipeline, Collection<DatanodeDetails> excludeDns)
specifier|private
name|boolean
name|discardDatanode
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|Collection
argument_list|<
name|DatanodeDetails
argument_list|>
name|excludeDns
parameter_list|)
block|{
if|if
condition|(
name|excludeDns
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|discard
init|=
literal|false
decl_stmt|;
for|for
control|(
name|DatanodeDetails
name|dn
range|:
name|pipeline
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|Predicate
argument_list|<
name|DatanodeDetails
argument_list|>
name|predicate
init|=
name|p
lambda|->
name|p
operator|.
name|equals
argument_list|(
name|dn
argument_list|)
decl_stmt|;
name|discard
operator|=
name|excludeDns
operator|.
name|parallelStream
argument_list|()
operator|.
name|anyMatch
argument_list|(
name|predicate
argument_list|)
expr_stmt|;
if|if
condition|(
name|discard
condition|)
block|{
break|break;
block|}
block|}
return|return
name|discard
return|;
block|}
comment|/**    * Get set of containerIDs corresponding to a pipeline.    *    * @param pipelineID - PipelineID    * @return Set of containerIDs belonging to the pipeline    * @throws IOException if pipeline is not found    */
DECL|method|getContainers (PipelineID pipelineID)
name|NavigableSet
argument_list|<
name|ContainerID
argument_list|>
name|getContainers
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|PipelineNotFoundException
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
name|NavigableSet
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
name|PipelineNotFoundException
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
name|TreeSet
argument_list|<>
argument_list|(
name|containerIDs
argument_list|)
return|;
block|}
comment|/**    * Get number of containers corresponding to a pipeline.    *    * @param pipelineID - PipelineID    * @return Number of containers belonging to the pipeline    * @throws IOException if pipeline is not found    */
DECL|method|getNumberOfContainers (PipelineID pipelineID)
name|int
name|getNumberOfContainers
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|PipelineNotFoundException
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
name|PipelineNotFoundException
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
name|containerIDs
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Remove pipeline from the data structures.    *    * @param pipelineID - PipelineID of the pipeline to be removed    * @throws IOException if the pipeline is not empty or does not exist    */
DECL|method|removePipeline (PipelineID pipelineID)
name|Pipeline
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
name|Pipeline
name|pipeline
init|=
name|getPipeline
argument_list|(
name|pipelineID
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
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Pipeline with %s is not yet closed"
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
return|return
name|pipeline
return|;
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
name|PipelineNotFoundException
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
name|containerIDs
operator|.
name|remove
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
block|}
comment|/**    * Updates the state of pipeline.    *    * @param pipelineID - PipelineID of the pipeline whose state needs    *                   to be updated    * @param state - new state of the pipeline    * @return Pipeline with the updated state    * @throws IOException if pipeline does not exist    */
DECL|method|updatePipelineState (PipelineID pipelineID, PipelineState state)
name|Pipeline
name|updatePipelineState
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|,
name|PipelineState
name|state
parameter_list|)
throws|throws
name|PipelineNotFoundException
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
specifier|final
name|Pipeline
name|pipeline
init|=
name|getPipeline
argument_list|(
name|pipelineID
argument_list|)
decl_stmt|;
name|Pipeline
name|updatedPipeline
init|=
name|pipelineMap
operator|.
name|compute
argument_list|(
name|pipelineID
argument_list|,
parameter_list|(
name|id
parameter_list|,
name|p
parameter_list|)
lambda|->
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
decl_stmt|;
name|PipelineQuery
name|query
init|=
operator|new
name|PipelineQuery
argument_list|(
name|pipeline
argument_list|)
decl_stmt|;
if|if
condition|(
name|updatedPipeline
operator|.
name|getPipelineState
argument_list|()
operator|==
name|PipelineState
operator|.
name|OPEN
condition|)
block|{
comment|// for transition to OPEN state add pipeline to query2OpenPipelines
name|query2OpenPipelines
operator|.
name|get
argument_list|(
name|query
argument_list|)
operator|.
name|add
argument_list|(
name|updatedPipeline
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// for transition from OPEN to CLOSED state remove pipeline from
comment|// query2OpenPipelines
name|query2OpenPipelines
operator|.
name|get
argument_list|(
name|query
argument_list|)
operator|.
name|remove
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
return|return
name|updatedPipeline
return|;
block|}
DECL|class|PipelineQuery
specifier|private
specifier|static
class|class
name|PipelineQuery
block|{
DECL|field|type
specifier|private
name|ReplicationType
name|type
decl_stmt|;
DECL|field|factor
specifier|private
name|ReplicationFactor
name|factor
decl_stmt|;
DECL|method|PipelineQuery (ReplicationType type, ReplicationFactor factor)
name|PipelineQuery
parameter_list|(
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|factor
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|factor
argument_list|)
expr_stmt|;
block|}
DECL|method|PipelineQuery (Pipeline pipeline)
name|PipelineQuery
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
block|{
name|type
operator|=
name|pipeline
operator|.
name|getType
argument_list|()
expr_stmt|;
name|factor
operator|=
name|pipeline
operator|.
name|getFactor
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressFBWarnings
argument_list|(
literal|"NP_EQUALS_SHOULD_HANDLE_NULL_ARGUMENT"
argument_list|)
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|PipelineQuery
name|otherQuery
init|=
operator|(
name|PipelineQuery
operator|)
name|other
decl_stmt|;
return|return
name|type
operator|==
name|otherQuery
operator|.
name|type
operator|&&
name|factor
operator|==
name|otherQuery
operator|.
name|factor
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|new
name|HashCodeBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|type
argument_list|)
operator|.
name|append
argument_list|(
name|factor
argument_list|)
operator|.
name|toHashCode
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

