begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
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
name|net
operator|.
name|Node
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
comment|/**  * This class is used internally by  * {@link BlockManager#computeReconstructionWorkForBlocks} to represent a  * task to reconstruct a block through replication or erasure coding.  * Reconstruction is done by transferring data from srcNodes to targets  */
end_comment

begin_class
DECL|class|BlockReconstructionWork
specifier|abstract
class|class
name|BlockReconstructionWork
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
name|BlockReconstructionWork
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|block
specifier|private
specifier|final
name|BlockInfo
name|block
decl_stmt|;
DECL|field|srcPath
specifier|private
specifier|final
name|String
name|srcPath
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|final
name|long
name|blockSize
decl_stmt|;
DECL|field|storagePolicyID
specifier|private
specifier|final
name|byte
name|storagePolicyID
decl_stmt|;
comment|/**    * An erasure coding reconstruction task has multiple source nodes.    * A replication task only has 1 source node, stored on top of the array    */
DECL|field|srcNodes
specifier|private
specifier|final
name|DatanodeDescriptor
index|[]
name|srcNodes
decl_stmt|;
comment|/** Nodes containing the block; avoid them in choosing new targets */
DECL|field|containingNodes
specifier|private
specifier|final
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|containingNodes
decl_stmt|;
comment|/** Required by {@link BlockPlacementPolicy#chooseTarget} */
DECL|field|liveReplicaStorages
specifier|private
specifier|final
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|liveReplicaStorages
decl_stmt|;
DECL|field|additionalReplRequired
specifier|private
specifier|final
name|int
name|additionalReplRequired
decl_stmt|;
DECL|field|targets
specifier|private
name|DatanodeStorageInfo
index|[]
name|targets
decl_stmt|;
DECL|field|priority
specifier|private
specifier|final
name|int
name|priority
decl_stmt|;
DECL|field|notEnoughRack
specifier|private
name|boolean
name|notEnoughRack
init|=
literal|false
decl_stmt|;
DECL|method|BlockReconstructionWork (BlockInfo block, BlockCollection bc, DatanodeDescriptor[] srcNodes, List<DatanodeDescriptor> containingNodes, List<DatanodeStorageInfo> liveReplicaStorages, int additionalReplRequired, int priority)
specifier|public
name|BlockReconstructionWork
parameter_list|(
name|BlockInfo
name|block
parameter_list|,
name|BlockCollection
name|bc
parameter_list|,
name|DatanodeDescriptor
index|[]
name|srcNodes
parameter_list|,
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|containingNodes
parameter_list|,
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|liveReplicaStorages
parameter_list|,
name|int
name|additionalReplRequired
parameter_list|,
name|int
name|priority
parameter_list|)
block|{
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
name|this
operator|.
name|srcPath
operator|=
name|bc
operator|.
name|getName
argument_list|()
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
name|block
operator|.
name|getNumBytes
argument_list|()
expr_stmt|;
name|this
operator|.
name|storagePolicyID
operator|=
name|bc
operator|.
name|getStoragePolicyID
argument_list|()
expr_stmt|;
name|this
operator|.
name|srcNodes
operator|=
name|srcNodes
expr_stmt|;
name|this
operator|.
name|containingNodes
operator|=
name|containingNodes
expr_stmt|;
name|this
operator|.
name|liveReplicaStorages
operator|=
name|liveReplicaStorages
expr_stmt|;
name|this
operator|.
name|additionalReplRequired
operator|=
name|additionalReplRequired
expr_stmt|;
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
name|this
operator|.
name|targets
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getTargets ()
name|DatanodeStorageInfo
index|[]
name|getTargets
parameter_list|()
block|{
return|return
name|targets
return|;
block|}
DECL|method|resetTargets ()
name|void
name|resetTargets
parameter_list|()
block|{
name|this
operator|.
name|targets
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|setTargets (DatanodeStorageInfo[] targets)
name|void
name|setTargets
parameter_list|(
name|DatanodeStorageInfo
index|[]
name|targets
parameter_list|)
block|{
name|this
operator|.
name|targets
operator|=
name|targets
expr_stmt|;
block|}
DECL|method|getContainingNodes ()
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|getContainingNodes
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|containingNodes
argument_list|)
return|;
block|}
DECL|method|getPriority ()
specifier|public
name|int
name|getPriority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
DECL|method|getBlock ()
specifier|public
name|BlockInfo
name|getBlock
parameter_list|()
block|{
return|return
name|block
return|;
block|}
DECL|method|getSrcNodes ()
specifier|public
name|DatanodeDescriptor
index|[]
name|getSrcNodes
parameter_list|()
block|{
return|return
name|srcNodes
return|;
block|}
DECL|method|getSrcPath ()
specifier|public
name|String
name|getSrcPath
parameter_list|()
block|{
return|return
name|srcPath
return|;
block|}
DECL|method|getBlockSize ()
specifier|public
name|long
name|getBlockSize
parameter_list|()
block|{
return|return
name|blockSize
return|;
block|}
DECL|method|getStoragePolicyID ()
specifier|public
name|byte
name|getStoragePolicyID
parameter_list|()
block|{
return|return
name|storagePolicyID
return|;
block|}
DECL|method|getLiveReplicaStorages ()
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|getLiveReplicaStorages
parameter_list|()
block|{
return|return
name|liveReplicaStorages
return|;
block|}
DECL|method|getAdditionalReplRequired ()
specifier|public
name|int
name|getAdditionalReplRequired
parameter_list|()
block|{
return|return
name|additionalReplRequired
return|;
block|}
comment|/**    * Mark that the reconstruction work is to replicate internal block to a new    * rack.    */
DECL|method|setNotEnoughRack ()
name|void
name|setNotEnoughRack
parameter_list|()
block|{
name|notEnoughRack
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|hasNotEnoughRack ()
name|boolean
name|hasNotEnoughRack
parameter_list|()
block|{
return|return
name|notEnoughRack
return|;
block|}
DECL|method|chooseTargets (BlockPlacementPolicy blockplacement, BlockStoragePolicySuite storagePolicySuite, Set<Node> excludedNodes)
specifier|abstract
name|void
name|chooseTargets
parameter_list|(
name|BlockPlacementPolicy
name|blockplacement
parameter_list|,
name|BlockStoragePolicySuite
name|storagePolicySuite
parameter_list|,
name|Set
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|)
function_decl|;
comment|/**    * Add reconstruction task into a source datanode.    *    * @param numberReplicas replica details    */
DECL|method|addTaskToDatanode (NumberReplicas numberReplicas)
specifier|abstract
name|void
name|addTaskToDatanode
parameter_list|(
name|NumberReplicas
name|numberReplicas
parameter_list|)
function_decl|;
block|}
end_class

end_unit

