begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.pipelines.standalone
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
name|pipelines
operator|.
name|standalone
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
name|hdds
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
name|hdds
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
name|hdds
operator|.
name|scm
operator|.
name|pipelines
operator|.
name|Node2PipelineMap
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
name|hdds
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
name|NodeState
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

begin_comment
comment|/**  * Standalone Manager Impl to prove that pluggable interface  * works with current tests.  */
end_comment

begin_class
DECL|class|StandaloneManagerImpl
specifier|public
class|class
name|StandaloneManagerImpl
extends|extends
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
name|StandaloneManagerImpl
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
DECL|field|standAloneMembers
specifier|private
specifier|final
name|Set
argument_list|<
name|DatanodeDetails
argument_list|>
name|standAloneMembers
decl_stmt|;
comment|/**    * Constructor for Standalone Node Manager Impl.    * @param nodeManager - Node Manager.    * @param placementPolicy - Placement Policy    * @param containerSize - Container Size.    */
DECL|method|StandaloneManagerImpl (NodeManager nodeManager, ContainerPlacementPolicy placementPolicy, long containerSize, Node2PipelineMap map)
specifier|public
name|StandaloneManagerImpl
parameter_list|(
name|NodeManager
name|nodeManager
parameter_list|,
name|ContainerPlacementPolicy
name|placementPolicy
parameter_list|,
name|long
name|containerSize
parameter_list|,
name|Node2PipelineMap
name|map
parameter_list|)
block|{
name|super
argument_list|(
name|map
argument_list|)
expr_stmt|;
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
name|containerSize
expr_stmt|;
name|this
operator|.
name|standAloneMembers
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Allocates a new standalone Pipeline from the free nodes.    *    * @param factor - One    * @return Pipeline.    */
DECL|method|allocatePipeline (ReplicationFactor factor)
specifier|public
name|Pipeline
name|allocatePipeline
parameter_list|(
name|ReplicationFactor
name|factor
parameter_list|)
block|{
name|List
argument_list|<
name|DatanodeDetails
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
name|DatanodeDetails
argument_list|>
name|datanodes
init|=
name|nodeManager
operator|.
name|getNodes
argument_list|(
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
name|factor
argument_list|)
decl_stmt|;
for|for
control|(
name|DatanodeDetails
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
name|standAloneMembers
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
comment|// once a datanode has been added to a pipeline, exclude it from
comment|// further allocations
name|standAloneMembers
operator|.
name|addAll
argument_list|(
name|newNodesList
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Allocating a new standalone pipeline of size: {}"
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|String
name|pipelineName
init|=
literal|"SA-"
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
literal|3
argument_list|)
decl_stmt|;
return|return
name|PipelineSelector
operator|.
name|newPipelineFromNodes
argument_list|(
name|newNodesList
argument_list|,
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|,
name|ReplicationFactor
operator|.
name|ONE
argument_list|,
name|pipelineName
argument_list|)
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|initializePipeline (Pipeline pipeline)
specifier|public
name|void
name|initializePipeline
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
block|{
comment|// Nothing to be done for standalone pipeline
block|}
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
name|DatanodeDetails
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
DECL|method|updatePipeline (String pipelineID, List<DatanodeDetails> newDatanodes)
specifier|public
name|void
name|updatePipeline
parameter_list|(
name|String
name|pipelineID
parameter_list|,
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|newDatanodes
parameter_list|)
throws|throws
name|IOException
block|{    }
block|}
end_class

end_unit

