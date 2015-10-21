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
name|classification
operator|.
name|InterfaceAudience
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
name|fs
operator|.
name|StorageType
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
name|DatanodeInfo
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
operator|.
name|NodeBase
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

begin_comment
comment|/**  * The class is responsible for choosing the desired number of targets  * for placing block replicas.  * The strategy is that it tries its best to place the replicas to most racks.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockPlacementPolicyRackFaultTolerant
specifier|public
class|class
name|BlockPlacementPolicyRackFaultTolerant
extends|extends
name|BlockPlacementPolicyDefault
block|{
annotation|@
name|Override
DECL|method|getMaxNodesPerRack (int numOfChosen, int numOfReplicas)
specifier|protected
name|int
index|[]
name|getMaxNodesPerRack
parameter_list|(
name|int
name|numOfChosen
parameter_list|,
name|int
name|numOfReplicas
parameter_list|)
block|{
name|int
name|clusterSize
init|=
name|clusterMap
operator|.
name|getNumOfLeaves
argument_list|()
decl_stmt|;
name|int
name|totalNumOfReplicas
init|=
name|numOfChosen
operator|+
name|numOfReplicas
decl_stmt|;
if|if
condition|(
name|totalNumOfReplicas
operator|>
name|clusterSize
condition|)
block|{
name|numOfReplicas
operator|-=
operator|(
name|totalNumOfReplicas
operator|-
name|clusterSize
operator|)
expr_stmt|;
name|totalNumOfReplicas
operator|=
name|clusterSize
expr_stmt|;
block|}
comment|// No calculation needed when there is only one rack or picking one node.
name|int
name|numOfRacks
init|=
name|clusterMap
operator|.
name|getNumOfRacks
argument_list|()
decl_stmt|;
if|if
condition|(
name|numOfRacks
operator|==
literal|1
operator|||
name|totalNumOfReplicas
operator|<=
literal|1
condition|)
block|{
return|return
operator|new
name|int
index|[]
block|{
name|numOfReplicas
block|,
name|totalNumOfReplicas
block|}
return|;
block|}
if|if
condition|(
name|totalNumOfReplicas
operator|<
name|numOfRacks
condition|)
block|{
return|return
operator|new
name|int
index|[]
block|{
name|numOfReplicas
block|,
literal|1
block|}
return|;
block|}
name|int
name|maxNodesPerRack
init|=
operator|(
name|totalNumOfReplicas
operator|-
literal|1
operator|)
operator|/
name|numOfRacks
operator|+
literal|1
decl_stmt|;
return|return
operator|new
name|int
index|[]
block|{
name|numOfReplicas
block|,
name|maxNodesPerRack
block|}
return|;
block|}
comment|/**    * Choose numOfReplicas in order:    * 1. If total replica expected is less than numOfRacks in cluster, it choose    * randomly.    * 2. If total replica expected is bigger than numOfRacks, it choose:    *  2a. Fill each rack exactly (maxNodesPerRack-1) replicas.    *  2b. For some random racks, place one more replica to each one of them, until    *  numOfReplicas have been chosen.<br>    * In the end, the difference of the numbers of replicas for each two racks    * is no more than 1.    * Either way it always prefer local storage.    * @return local node of writer    */
annotation|@
name|Override
DECL|method|chooseTargetInOrder (int numOfReplicas, Node writer, final Set<Node> excludedNodes, final long blocksize, final int maxNodesPerRack, final List<DatanodeStorageInfo> results, final boolean avoidStaleNodes, final boolean newBlock, EnumMap<StorageType, Integer> storageTypes)
specifier|protected
name|Node
name|chooseTargetInOrder
parameter_list|(
name|int
name|numOfReplicas
parameter_list|,
name|Node
name|writer
parameter_list|,
specifier|final
name|Set
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
specifier|final
name|long
name|blocksize
parameter_list|,
specifier|final
name|int
name|maxNodesPerRack
parameter_list|,
specifier|final
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|results
parameter_list|,
specifier|final
name|boolean
name|avoidStaleNodes
parameter_list|,
specifier|final
name|boolean
name|newBlock
parameter_list|,
name|EnumMap
argument_list|<
name|StorageType
argument_list|,
name|Integer
argument_list|>
name|storageTypes
parameter_list|)
throws|throws
name|NotEnoughReplicasException
block|{
name|int
name|totalReplicaExpected
init|=
name|results
operator|.
name|size
argument_list|()
operator|+
name|numOfReplicas
decl_stmt|;
name|int
name|numOfRacks
init|=
name|clusterMap
operator|.
name|getNumOfRacks
argument_list|()
decl_stmt|;
if|if
condition|(
name|totalReplicaExpected
operator|<
name|numOfRacks
operator|||
name|totalReplicaExpected
operator|%
name|numOfRacks
operator|==
literal|0
condition|)
block|{
name|writer
operator|=
name|chooseOnce
argument_list|(
name|numOfReplicas
argument_list|,
name|writer
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxNodesPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
expr_stmt|;
return|return
name|writer
return|;
block|}
assert|assert
name|totalReplicaExpected
operator|>
operator|(
name|maxNodesPerRack
operator|-
literal|1
operator|)
operator|*
name|numOfRacks
assert|;
comment|// Calculate numOfReplicas for filling each rack exactly (maxNodesPerRack-1)
comment|// replicas.
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|rackCounts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeStorageInfo
name|dsInfo
range|:
name|results
control|)
block|{
name|String
name|rack
init|=
name|dsInfo
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|.
name|getNetworkLocation
argument_list|()
decl_stmt|;
name|Integer
name|count
init|=
name|rackCounts
operator|.
name|get
argument_list|(
name|rack
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
name|rackCounts
operator|.
name|put
argument_list|(
name|rack
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rackCounts
operator|.
name|put
argument_list|(
name|rack
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|excess
init|=
literal|0
decl_stmt|;
comment|// Sum of the above (maxNodesPerRack-1) part of nodes in results
for|for
control|(
name|int
name|count
range|:
name|rackCounts
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|count
operator|>
name|maxNodesPerRack
operator|-
literal|1
condition|)
block|{
name|excess
operator|+=
name|count
operator|-
operator|(
name|maxNodesPerRack
operator|-
literal|1
operator|)
expr_stmt|;
block|}
block|}
name|numOfReplicas
operator|=
name|Math
operator|.
name|min
argument_list|(
name|totalReplicaExpected
operator|-
name|results
operator|.
name|size
argument_list|()
argument_list|,
operator|(
name|maxNodesPerRack
operator|-
literal|1
operator|)
operator|*
name|numOfRacks
operator|-
operator|(
name|results
operator|.
name|size
argument_list|()
operator|-
name|excess
operator|)
argument_list|)
expr_stmt|;
comment|// Fill each rack exactly (maxNodesPerRack-1) replicas.
name|writer
operator|=
name|chooseOnce
argument_list|(
name|numOfReplicas
argument_list|,
name|writer
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|excludedNodes
argument_list|)
argument_list|,
name|blocksize
argument_list|,
name|maxNodesPerRack
operator|-
literal|1
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
expr_stmt|;
for|for
control|(
name|DatanodeStorageInfo
name|resultStorage
range|:
name|results
control|)
block|{
name|addToExcludedNodes
argument_list|(
name|resultStorage
operator|.
name|getDatanodeDescriptor
argument_list|()
argument_list|,
name|excludedNodes
argument_list|)
expr_stmt|;
block|}
comment|// For some racks, place one more replica to each one of them.
name|numOfReplicas
operator|=
name|totalReplicaExpected
operator|-
name|results
operator|.
name|size
argument_list|()
expr_stmt|;
name|chooseOnce
argument_list|(
name|numOfReplicas
argument_list|,
name|writer
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxNodesPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
expr_stmt|;
return|return
name|writer
return|;
block|}
comment|/**    * Randomly choose<i>numOfReplicas</i> targets from the given<i>scope</i>.    * Except that 1st replica prefer local storage.    * @return local node of writer.    */
DECL|method|chooseOnce (int numOfReplicas, Node writer, final Set<Node> excludedNodes, final long blocksize, final int maxNodesPerRack, final List<DatanodeStorageInfo> results, final boolean avoidStaleNodes, EnumMap<StorageType, Integer> storageTypes)
specifier|private
name|Node
name|chooseOnce
parameter_list|(
name|int
name|numOfReplicas
parameter_list|,
name|Node
name|writer
parameter_list|,
specifier|final
name|Set
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
specifier|final
name|long
name|blocksize
parameter_list|,
specifier|final
name|int
name|maxNodesPerRack
parameter_list|,
specifier|final
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|results
parameter_list|,
specifier|final
name|boolean
name|avoidStaleNodes
parameter_list|,
name|EnumMap
argument_list|<
name|StorageType
argument_list|,
name|Integer
argument_list|>
name|storageTypes
parameter_list|)
throws|throws
name|NotEnoughReplicasException
block|{
if|if
condition|(
name|numOfReplicas
operator|==
literal|0
condition|)
block|{
return|return
name|writer
return|;
block|}
name|writer
operator|=
name|chooseLocalStorage
argument_list|(
name|writer
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxNodesPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|,
literal|true
argument_list|)
operator|.
name|getDatanodeDescriptor
argument_list|()
expr_stmt|;
if|if
condition|(
operator|--
name|numOfReplicas
operator|==
literal|0
condition|)
block|{
return|return
name|writer
return|;
block|}
name|chooseRandom
argument_list|(
name|numOfReplicas
argument_list|,
name|NodeBase
operator|.
name|ROOT
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxNodesPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
expr_stmt|;
return|return
name|writer
return|;
block|}
annotation|@
name|Override
DECL|method|verifyBlockPlacement (DatanodeInfo[] locs, int numberOfReplicas)
specifier|public
name|BlockPlacementStatus
name|verifyBlockPlacement
parameter_list|(
name|DatanodeInfo
index|[]
name|locs
parameter_list|,
name|int
name|numberOfReplicas
parameter_list|)
block|{
if|if
condition|(
name|locs
operator|==
literal|null
condition|)
name|locs
operator|=
name|DatanodeDescriptor
operator|.
name|EMPTY_ARRAY
expr_stmt|;
if|if
condition|(
operator|!
name|clusterMap
operator|.
name|hasClusterEverBeenMultiRack
argument_list|()
condition|)
block|{
comment|// only one rack
return|return
operator|new
name|BlockPlacementStatusDefault
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
return|;
block|}
comment|// 1. Check that all locations are different.
comment|// 2. Count locations on different racks.
name|Set
argument_list|<
name|String
argument_list|>
name|racks
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeInfo
name|dn
range|:
name|locs
control|)
name|racks
operator|.
name|add
argument_list|(
name|dn
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|BlockPlacementStatusDefault
argument_list|(
name|racks
operator|.
name|size
argument_list|()
argument_list|,
name|numberOfReplicas
argument_list|)
return|;
block|}
block|}
end_class

end_unit

