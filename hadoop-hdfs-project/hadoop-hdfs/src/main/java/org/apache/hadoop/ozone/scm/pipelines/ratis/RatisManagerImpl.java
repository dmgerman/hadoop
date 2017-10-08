begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.pipelines.ratis
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|pipelines
operator|.
name|ratis
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
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
name|proto
operator|.
name|OzoneProtos
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
name|scm
operator|.
name|container
operator|.
name|placement
operator|.
name|algorithms
operator|.
name|ContainerPlacementPolicy
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
name|scm
operator|.
name|node
operator|.
name|NodeManager
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
name|scm
operator|.
name|pipelines
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
name|ozone
operator|.
name|scm
operator|.
name|pipelines
operator|.
name|PipelineSelector
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
name|scm
operator|.
name|XceiverClientRatis
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|atomic
operator|.
name|AtomicInteger
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
operator|.
name|LifeCycleState
operator|.
name|ALLOCATED
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link PipelineManager}.  *  * TODO : Introduce a state machine.  */
end_comment

begin_class
DECL|class|RatisManagerImpl
specifier|public
class|class
name|RatisManagerImpl
implements|implements
name|PipelineManager
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
name|RatisManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|nodeManager
specifier|private
specifier|final
name|NodeManager
name|nodeManager
decl_stmt|;
DECL|field|placementPolicy
specifier|private
specifier|final
name|ContainerPlacementPolicy
name|placementPolicy
decl_stmt|;
DECL|field|containerSize
specifier|private
specifier|final
name|long
name|containerSize
decl_stmt|;
DECL|field|ratisMembers
specifier|private
specifier|final
name|Set
argument_list|<
name|DatanodeID
argument_list|>
name|ratisMembers
decl_stmt|;
DECL|field|activePipelines
specifier|private
specifier|final
name|List
argument_list|<
name|Pipeline
argument_list|>
name|activePipelines
decl_stmt|;
DECL|field|pipelineIndex
specifier|private
specifier|final
name|AtomicInteger
name|pipelineIndex
decl_stmt|;
DECL|field|PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"Ratis-"
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
comment|/**    * Constructs a Ratis Pipeline Manager.    *    * @param nodeManager    */
DECL|method|RatisManagerImpl (NodeManager nodeManager, ContainerPlacementPolicy placementPolicy, long size, Configuration conf)
specifier|public
name|RatisManagerImpl
parameter_list|(
name|NodeManager
name|nodeManager
parameter_list|,
name|ContainerPlacementPolicy
name|placementPolicy
parameter_list|,
name|long
name|size
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|nodeManager
operator|=
name|nodeManager
expr_stmt|;
name|this
operator|.
name|placementPolicy
operator|=
name|placementPolicy
expr_stmt|;
name|this
operator|.
name|containerSize
operator|=
name|size
expr_stmt|;
name|ratisMembers
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|activePipelines
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|pipelineIndex
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
comment|/**    * This function is called by the Container Manager while allocation a new    * container. The client specifies what kind of replication pipeline is needed    * and based on the replication type in the request appropriate Interface is    * invoked.    *    * @param containerName Name of the container    * @param replicationFactor - Replication Factor    * @return a Pipeline.    *<p>    * TODO: Evaulate if we really need this lock. Right now favoring safety over    * speed.    */
annotation|@
name|Override
DECL|method|getPipeline (String containerName, OzoneProtos.ReplicationFactor replicationFactor)
specifier|public
specifier|synchronized
name|Pipeline
name|getPipeline
parameter_list|(
name|String
name|containerName
parameter_list|,
name|OzoneProtos
operator|.
name|ReplicationFactor
name|replicationFactor
parameter_list|)
throws|throws
name|IOException
block|{
comment|/**      * In the ratis world, we have a very simple policy.      *      * 1. Try to create a pipeline if there are enough free nodes.      *      * 2. This allows all nodes to part of a pipeline quickly.      *      * 3. if there are not enough free nodes, return pipelines in a      * round-robin fashion.      *      * TODO: Might have to come up with a better algorithm than this.      * Create a new placement policy that returns pipelines in round robin      * fashion.      */
name|Pipeline
name|pipeline
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|newNodes
init|=
name|allocatePipelineNodes
argument_list|(
name|replicationFactor
argument_list|)
decl_stmt|;
if|if
condition|(
name|newNodes
operator|!=
literal|null
condition|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|newNodes
operator|.
name|size
argument_list|()
operator|==
name|getReplicationCount
argument_list|(
name|replicationFactor
argument_list|)
argument_list|,
literal|"Replication factor "
operator|+
literal|"does not match the expected node count."
argument_list|)
expr_stmt|;
name|pipeline
operator|=
name|allocateRatisPipeline
argument_list|(
name|newNodes
argument_list|,
name|containerName
argument_list|,
name|replicationFactor
argument_list|)
expr_stmt|;
try|try
init|(
name|XceiverClientRatis
name|client
init|=
name|XceiverClientRatis
operator|.
name|newXceiverClientRatis
argument_list|(
name|pipeline
argument_list|,
name|conf
argument_list|)
init|)
block|{
name|client
operator|.
name|createPipeline
argument_list|(
name|pipeline
operator|.
name|getPipelineName
argument_list|()
argument_list|,
name|pipeline
operator|.
name|getMachines
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|pipeline
operator|=
name|findOpenPipeline
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|pipeline
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Get pipeline call failed. We are not able to find free nodes"
operator|+
literal|" or operational pipeline."
argument_list|)
expr_stmt|;
block|}
return|return
name|pipeline
return|;
block|}
comment|/**    * Find a pipeline that is operational.    *    * @return - Pipeline or null    */
DECL|method|findOpenPipeline ()
name|Pipeline
name|findOpenPipeline
parameter_list|()
block|{
name|Pipeline
name|pipeline
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|sentinal
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|activePipelines
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"No Operational pipelines found. Returning null."
argument_list|)
expr_stmt|;
return|return
name|pipeline
return|;
block|}
name|int
name|startIndex
init|=
name|getNextIndex
argument_list|()
decl_stmt|;
name|int
name|nextIndex
init|=
name|sentinal
decl_stmt|;
for|for
control|(
init|;
name|startIndex
operator|!=
name|nextIndex
condition|;
name|nextIndex
operator|=
name|getNextIndex
argument_list|()
control|)
block|{
comment|// Just walk the list in a circular way.
name|Pipeline
name|temp
init|=
name|activePipelines
operator|.
name|get
argument_list|(
name|nextIndex
operator|!=
name|sentinal
condition|?
name|nextIndex
else|:
name|startIndex
argument_list|)
decl_stmt|;
comment|// if we find an operational pipeline just return that.
if|if
condition|(
name|temp
operator|.
name|getLifeCycleState
argument_list|()
operator|==
name|OPEN
condition|)
block|{
name|pipeline
operator|=
name|temp
expr_stmt|;
break|break;
block|}
block|}
return|return
name|pipeline
return|;
block|}
comment|/**    * Allocate a new Ratis pipeline from the existing nodes.    *    * @param nodes - list of Nodes.    * @param containerName - container Name    * @return - Pipeline.    */
DECL|method|allocateRatisPipeline (List<DatanodeID> nodes, String containerName, OzoneProtos.ReplicationFactor factor)
name|Pipeline
name|allocateRatisPipeline
parameter_list|(
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|nodes
parameter_list|,
name|String
name|containerName
parameter_list|,
name|OzoneProtos
operator|.
name|ReplicationFactor
name|factor
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
name|Pipeline
name|pipeline
init|=
name|PipelineSelector
operator|.
name|newPipelineFromNodes
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
if|if
condition|(
name|pipeline
operator|!=
literal|null
condition|)
block|{
comment|// Start all pipeline names with "Ratis", easy to grep the logs.
name|String
name|pipelineName
init|=
name|PREFIX
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
name|PREFIX
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|pipeline
operator|.
name|setType
argument_list|(
name|OzoneProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|)
expr_stmt|;
name|pipeline
operator|.
name|setLifeCycleState
argument_list|(
name|ALLOCATED
argument_list|)
expr_stmt|;
name|pipeline
operator|.
name|setFactor
argument_list|(
name|factor
argument_list|)
expr_stmt|;
name|pipeline
operator|.
name|setPipelineName
argument_list|(
name|pipelineName
argument_list|)
expr_stmt|;
name|pipeline
operator|.
name|setContainerName
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating new ratis pipeline: {}"
argument_list|,
name|pipeline
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|activePipelines
operator|.
name|add
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
return|return
name|pipeline
return|;
block|}
comment|/**    * gets the next index of in the pipelines to get.    *    * @return index in the link list to get.    */
DECL|method|getNextIndex ()
specifier|private
name|int
name|getNextIndex
parameter_list|()
block|{
return|return
name|pipelineIndex
operator|.
name|incrementAndGet
argument_list|()
operator|%
name|activePipelines
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Allocates a set of new nodes for the Ratis pipeline.    *    * @param replicationFactor - One or Three    * @return List of Datanodes.    */
DECL|method|allocatePipelineNodes (OzoneProtos.ReplicationFactor replicationFactor)
specifier|private
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|allocatePipelineNodes
parameter_list|(
name|OzoneProtos
operator|.
name|ReplicationFactor
name|replicationFactor
parameter_list|)
block|{
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|newNodesList
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|datanodes
init|=
name|nodeManager
operator|.
name|getNodes
argument_list|(
name|OzoneProtos
operator|.
name|NodeState
operator|.
name|HEALTHY
argument_list|)
decl_stmt|;
name|int
name|count
init|=
name|getReplicationCount
argument_list|(
name|replicationFactor
argument_list|)
decl_stmt|;
comment|//TODO: Add Raft State to the Nodes, so we can query and skip nodes from
comment|// data from datanode instead of maintaining a set.
for|for
control|(
name|DatanodeID
name|datanode
range|:
name|datanodes
control|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|datanode
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|ratisMembers
operator|.
name|contains
argument_list|(
name|datanode
argument_list|)
condition|)
block|{
name|newNodesList
operator|.
name|add
argument_list|(
name|datanode
argument_list|)
expr_stmt|;
comment|// once a datanode has been added to a pipeline, exclude it from
comment|// further allocations
name|ratisMembers
operator|.
name|add
argument_list|(
name|datanode
argument_list|)
expr_stmt|;
if|if
condition|(
name|newNodesList
operator|.
name|size
argument_list|()
operator|==
name|count
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Allocating a new pipeline of size: {}"
argument_list|,
name|count
argument_list|)
expr_stmt|;
return|return
name|newNodesList
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getReplicationCount (OzoneProtos.ReplicationFactor factor)
specifier|private
name|int
name|getReplicationCount
parameter_list|(
name|OzoneProtos
operator|.
name|ReplicationFactor
name|factor
parameter_list|)
block|{
switch|switch
condition|(
name|factor
condition|)
block|{
case|case
name|ONE
case|:
return|return
literal|1
return|;
case|case
name|THREE
case|:
return|return
literal|3
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected replication count"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Creates a pipeline from a specified set of Nodes.    *    * @param pipelineID - Name of the pipeline    * @param datanodes - The list of datanodes that make this pipeline.    */
annotation|@
name|Override
DECL|method|createPipeline (String pipelineID, List<DatanodeID> datanodes)
specifier|public
name|void
name|createPipeline
parameter_list|(
name|String
name|pipelineID
parameter_list|,
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|datanodes
parameter_list|)
block|{    }
comment|/**    * Close the  pipeline with the given clusterId.    *    * @param pipelineID    */
annotation|@
name|Override
DECL|method|closePipeline (String pipelineID)
specifier|public
name|void
name|closePipeline
parameter_list|(
name|String
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{    }
comment|/**    * list members in the pipeline .    *    * @param pipelineID    * @return the datanode    */
annotation|@
name|Override
DECL|method|getMembers (String pipelineID)
specifier|public
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|getMembers
parameter_list|(
name|String
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Update the datanode list of the pipeline.    *    * @param pipelineID    * @param newDatanodes    */
annotation|@
name|Override
DECL|method|updatePipeline (String pipelineID, List<DatanodeID> newDatanodes)
specifier|public
name|void
name|updatePipeline
parameter_list|(
name|String
name|pipelineID
parameter_list|,
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|newDatanodes
parameter_list|)
throws|throws
name|IOException
block|{    }
block|}
end_class

end_unit

