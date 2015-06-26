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
name|hdfs
operator|.
name|protocol
operator|.
name|Block
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
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * Subclass of {@link BlockInfoUnderConstruction}, representing a block under  * the contiguous (instead of striped) layout.  */
end_comment

begin_class
DECL|class|BlockInfoUnderConstructionContiguous
specifier|public
class|class
name|BlockInfoUnderConstructionContiguous
extends|extends
name|BlockInfoUnderConstruction
block|{
comment|/**    * Create block and set its state to    * {@link HdfsServerConstants.BlockUCState#UNDER_CONSTRUCTION}.    */
DECL|method|BlockInfoUnderConstructionContiguous (Block blk, short replication)
specifier|public
name|BlockInfoUnderConstructionContiguous
parameter_list|(
name|Block
name|blk
parameter_list|,
name|short
name|replication
parameter_list|)
block|{
name|this
argument_list|(
name|blk
argument_list|,
name|replication
argument_list|,
name|HdfsServerConstants
operator|.
name|BlockUCState
operator|.
name|UNDER_CONSTRUCTION
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a block that is currently being constructed.    */
DECL|method|BlockInfoUnderConstructionContiguous (Block blk, short replication, HdfsServerConstants.BlockUCState state, DatanodeStorageInfo[] targets)
specifier|public
name|BlockInfoUnderConstructionContiguous
parameter_list|(
name|Block
name|blk
parameter_list|,
name|short
name|replication
parameter_list|,
name|HdfsServerConstants
operator|.
name|BlockUCState
name|state
parameter_list|,
name|DatanodeStorageInfo
index|[]
name|targets
parameter_list|)
block|{
name|super
argument_list|(
name|blk
argument_list|,
name|replication
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|getBlockUCState
argument_list|()
operator|!=
name|HdfsServerConstants
operator|.
name|BlockUCState
operator|.
name|COMPLETE
argument_list|,
literal|"BlockInfoUnderConstructionContiguous cannot be in COMPLETE state"
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockUCState
operator|=
name|state
expr_stmt|;
name|setExpectedLocations
argument_list|(
name|targets
argument_list|)
expr_stmt|;
block|}
comment|/**    * Convert an under construction block to a complete block.    *    * @return BlockInfo - a complete block.    * @throws IOException if the state of the block    * (the generation stamp and the length) has not been committed by    * the client or it does not have at least a minimal number of replicas    * reported from data-nodes.    */
annotation|@
name|Override
DECL|method|convertToCompleteBlock ()
specifier|public
name|BlockInfoContiguous
name|convertToCompleteBlock
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|getBlockUCState
argument_list|()
operator|!=
name|HdfsServerConstants
operator|.
name|BlockUCState
operator|.
name|COMPLETE
argument_list|,
literal|"Trying to convert a COMPLETE block"
argument_list|)
expr_stmt|;
return|return
operator|new
name|BlockInfoContiguous
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|addStorage (DatanodeStorageInfo storage, Block reportedBlock)
name|boolean
name|addStorage
parameter_list|(
name|DatanodeStorageInfo
name|storage
parameter_list|,
name|Block
name|reportedBlock
parameter_list|)
block|{
return|return
name|ContiguousBlockStorageOp
operator|.
name|addStorage
argument_list|(
name|this
argument_list|,
name|storage
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|removeStorage (DatanodeStorageInfo storage)
name|boolean
name|removeStorage
parameter_list|(
name|DatanodeStorageInfo
name|storage
parameter_list|)
block|{
return|return
name|ContiguousBlockStorageOp
operator|.
name|removeStorage
argument_list|(
name|this
argument_list|,
name|storage
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|numNodes ()
specifier|public
name|int
name|numNodes
parameter_list|()
block|{
return|return
name|ContiguousBlockStorageOp
operator|.
name|numNodes
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|replaceBlock (BlockInfo newBlock)
name|void
name|replaceBlock
parameter_list|(
name|BlockInfo
name|newBlock
parameter_list|)
block|{
name|ContiguousBlockStorageOp
operator|.
name|replaceBlock
argument_list|(
name|this
argument_list|,
name|newBlock
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setExpectedLocations (DatanodeStorageInfo[] targets)
specifier|public
name|void
name|setExpectedLocations
parameter_list|(
name|DatanodeStorageInfo
index|[]
name|targets
parameter_list|)
block|{
name|int
name|numLocations
init|=
name|targets
operator|==
literal|null
condition|?
literal|0
else|:
name|targets
operator|.
name|length
decl_stmt|;
name|this
operator|.
name|replicas
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numLocations
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numLocations
condition|;
name|i
operator|++
control|)
block|{
name|replicas
operator|.
name|add
argument_list|(
operator|new
name|ReplicaUnderConstruction
argument_list|(
name|this
argument_list|,
name|targets
index|[
name|i
index|]
argument_list|,
name|HdfsServerConstants
operator|.
name|ReplicaState
operator|.
name|RBW
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getTruncateBlock ()
specifier|public
name|Block
name|getTruncateBlock
parameter_list|()
block|{
return|return
name|truncateBlock
return|;
block|}
annotation|@
name|Override
DECL|method|setTruncateBlock (Block recoveryBlock)
specifier|public
name|void
name|setTruncateBlock
parameter_list|(
name|Block
name|recoveryBlock
parameter_list|)
block|{
name|this
operator|.
name|truncateBlock
operator|=
name|recoveryBlock
expr_stmt|;
block|}
block|}
end_class

end_unit

