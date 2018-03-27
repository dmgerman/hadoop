begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.pipelines
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
name|hdsl
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
name|hdsl
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdslProtos
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
name|hdsl
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdslProtos
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
name|hdsl
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdslProtos
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|PipelineChannel
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * Manage Ozone pipelines.  */
end_comment

begin_class
DECL|class|PipelineManager
specifier|public
specifier|abstract
class|class
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
name|PipelineManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|activePipelineChannels
specifier|private
specifier|final
name|List
argument_list|<
name|PipelineChannel
argument_list|>
name|activePipelineChannels
decl_stmt|;
DECL|field|conduitsIndex
specifier|private
specifier|final
name|AtomicInteger
name|conduitsIndex
decl_stmt|;
DECL|method|PipelineManager ()
specifier|public
name|PipelineManager
parameter_list|()
block|{
name|activePipelineChannels
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|conduitsIndex
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * This function is called by the Container Manager while allocating a new    * container. The client specifies what kind of replication pipeline is    * needed and based on the replication type in the request appropriate    * Interface is invoked.    *    * @param containerName Name of the container    * @param replicationFactor - Replication Factor    * @return a Pipeline.    */
DECL|method|getPipeline (String containerName, ReplicationFactor replicationFactor, ReplicationType replicationType)
specifier|public
specifier|synchronized
specifier|final
name|Pipeline
name|getPipeline
parameter_list|(
name|String
name|containerName
parameter_list|,
name|ReplicationFactor
name|replicationFactor
parameter_list|,
name|ReplicationType
name|replicationType
parameter_list|)
throws|throws
name|IOException
block|{
comment|/**      * In the Ozone world, we have a very simple policy.      *      * 1. Try to create a pipelineChannel if there are enough free nodes.      *      * 2. This allows all nodes to part of a pipelineChannel quickly.      *      * 3. if there are not enough free nodes, return conduits in a      * round-robin fashion.      *      * TODO: Might have to come up with a better algorithm than this.      * Create a new placement policy that returns conduits in round robin      * fashion.      */
name|PipelineChannel
name|pipelineChannel
init|=
name|allocatePipelineChannel
argument_list|(
name|replicationFactor
argument_list|)
decl_stmt|;
if|if
condition|(
name|pipelineChannel
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"created new pipelineChannel:{} for container:{}"
argument_list|,
name|pipelineChannel
operator|.
name|getName
argument_list|()
argument_list|,
name|containerName
argument_list|)
expr_stmt|;
name|activePipelineChannels
operator|.
name|add
argument_list|(
name|pipelineChannel
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pipelineChannel
operator|=
name|findOpenPipelineChannel
argument_list|(
name|replicationType
argument_list|,
name|replicationFactor
argument_list|)
expr_stmt|;
if|if
condition|(
name|pipelineChannel
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"re-used pipelineChannel:{} for container:{}"
argument_list|,
name|pipelineChannel
operator|.
name|getName
argument_list|()
argument_list|,
name|containerName
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|pipelineChannel
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Get pipelineChannel call failed. We are not able to find"
operator|+
literal|"free nodes or operational pipelineChannel."
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|Pipeline
argument_list|(
name|containerName
argument_list|,
name|pipelineChannel
argument_list|)
return|;
block|}
block|}
DECL|method|getReplicationCount (ReplicationFactor factor)
specifier|protected
name|int
name|getReplicationCount
parameter_list|(
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
DECL|method|allocatePipelineChannel ( ReplicationFactor replicationFactor)
specifier|public
specifier|abstract
name|PipelineChannel
name|allocatePipelineChannel
parameter_list|(
name|ReplicationFactor
name|replicationFactor
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Find a PipelineChannel that is operational.    *    * @return - Pipeline or null    */
DECL|method|findOpenPipelineChannel ( ReplicationType type, ReplicationFactor factor)
specifier|private
name|PipelineChannel
name|findOpenPipelineChannel
parameter_list|(
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|)
block|{
name|PipelineChannel
name|pipelineChannel
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
name|activePipelineChannels
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
literal|"No Operational conduits found. Returning null."
argument_list|)
expr_stmt|;
return|return
literal|null
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
name|PipelineChannel
name|temp
init|=
name|activePipelineChannels
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
comment|// if we find an operational pipelineChannel just return that.
if|if
condition|(
operator|(
name|temp
operator|.
name|getLifeCycleState
argument_list|()
operator|==
name|LifeCycleState
operator|.
name|OPEN
operator|)
operator|&&
operator|(
name|temp
operator|.
name|getFactor
argument_list|()
operator|==
name|factor
operator|)
operator|&&
operator|(
name|temp
operator|.
name|getType
argument_list|()
operator|==
name|type
operator|)
condition|)
block|{
name|pipelineChannel
operator|=
name|temp
expr_stmt|;
break|break;
block|}
block|}
return|return
name|pipelineChannel
return|;
block|}
comment|/**    * gets the next index of the PipelineChannel to get.    *    * @return index in the link list to get.    */
DECL|method|getNextIndex ()
specifier|private
name|int
name|getNextIndex
parameter_list|()
block|{
return|return
name|conduitsIndex
operator|.
name|incrementAndGet
argument_list|()
operator|%
name|activePipelineChannels
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Creates a pipeline from a specified set of Nodes.    * @param pipelineID - Name of the pipeline    * @param datanodes - The list of datanodes that make this pipeline.    */
DECL|method|createPipeline (String pipelineID, List<DatanodeDetails> datanodes)
specifier|public
specifier|abstract
name|void
name|createPipeline
parameter_list|(
name|String
name|pipelineID
parameter_list|,
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|datanodes
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Close the  pipeline with the given clusterId.    */
DECL|method|closePipeline (String pipelineID)
specifier|public
specifier|abstract
name|void
name|closePipeline
parameter_list|(
name|String
name|pipelineID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * list members in the pipeline .    * @return the datanode    */
DECL|method|getMembers (String pipelineID)
specifier|public
specifier|abstract
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
function_decl|;
comment|/**    * Update the datanode list of the pipeline.    */
DECL|method|updatePipeline (String pipelineID, List<DatanodeDetails> newDatanodes)
specifier|public
specifier|abstract
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
function_decl|;
block|}
end_class

end_unit

