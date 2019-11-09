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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|BlockCommand
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
name|net
operator|.
name|Node
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

begin_class
DECL|class|ReplicationWork
class|class
name|ReplicationWork
extends|extends
name|BlockReconstructionWork
block|{
DECL|method|ReplicationWork (BlockInfo block, BlockCollection bc, DatanodeDescriptor[] srcNodes, List<DatanodeDescriptor> containingNodes, List<DatanodeStorageInfo> liveReplicaStorages, int additionalReplRequired, int priority)
specifier|public
name|ReplicationWork
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
name|super
argument_list|(
name|block
argument_list|,
name|bc
argument_list|,
name|srcNodes
argument_list|,
name|containingNodes
argument_list|,
name|liveReplicaStorages
argument_list|,
name|additionalReplRequired
argument_list|,
name|priority
argument_list|)
expr_stmt|;
assert|assert
name|getSrcNodes
argument_list|()
operator|.
name|length
operator|==
literal|1
operator|:
literal|"There should be exactly 1 source node that have been selected"
assert|;
name|getSrcNodes
argument_list|()
index|[
literal|0
index|]
operator|.
name|incrementPendingReplicationWithoutTargets
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating a ReplicationWork to reconstruct "
operator|+
name|block
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|chooseTargets (BlockPlacementPolicy blockplacement, BlockStoragePolicySuite storagePolicySuite, Set<Node> excludedNodes)
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
block|{
assert|assert
name|getSrcNodes
argument_list|()
operator|.
name|length
operator|>
literal|0
operator|:
literal|"At least 1 source node should have been selected"
assert|;
try|try
block|{
name|DatanodeStorageInfo
index|[]
name|chosenTargets
init|=
literal|null
decl_stmt|;
comment|// HDFS-14720 If the block is deleted, the block size will become
comment|// BlockCommand.NO_ACK (LONG.MAX_VALUE) . This kind of block we don't need
comment|// to send for replication or reconstruction
if|if
condition|(
name|getBlock
argument_list|()
operator|.
name|getNumBytes
argument_list|()
operator|!=
name|BlockCommand
operator|.
name|NO_ACK
condition|)
block|{
name|chosenTargets
operator|=
name|blockplacement
operator|.
name|chooseTarget
argument_list|(
name|getSrcPath
argument_list|()
argument_list|,
name|getAdditionalReplRequired
argument_list|()
argument_list|,
name|getSrcNodes
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|getLiveReplicaStorages
argument_list|()
argument_list|,
literal|false
argument_list|,
name|excludedNodes
argument_list|,
name|getBlockSize
argument_list|()
argument_list|,
name|storagePolicySuite
operator|.
name|getPolicy
argument_list|(
name|getStoragePolicyID
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|setTargets
argument_list|(
name|chosenTargets
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|getSrcNodes
argument_list|()
index|[
literal|0
index|]
operator|.
name|decrementPendingReplicationWithoutTargets
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addTaskToDatanode (NumberReplicas numberReplicas)
name|void
name|addTaskToDatanode
parameter_list|(
name|NumberReplicas
name|numberReplicas
parameter_list|)
block|{
name|getSrcNodes
argument_list|()
index|[
literal|0
index|]
operator|.
name|addBlockToBeReplicated
argument_list|(
name|getBlock
argument_list|()
argument_list|,
name|getTargets
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

