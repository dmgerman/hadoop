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
name|java
operator|.
name|beans
operator|.
name|ConstructorProperties
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
name|classification
operator|.
name|InterfaceStability
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

begin_comment
comment|/**  * Statistics per StorageType.  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|StorageTypeStats
specifier|public
class|class
name|StorageTypeStats
block|{
DECL|field|capacityTotal
specifier|private
name|long
name|capacityTotal
init|=
literal|0L
decl_stmt|;
DECL|field|capacityUsed
specifier|private
name|long
name|capacityUsed
init|=
literal|0L
decl_stmt|;
DECL|field|capacityNonDfsUsed
specifier|private
name|long
name|capacityNonDfsUsed
init|=
literal|0L
decl_stmt|;
DECL|field|capacityRemaining
specifier|private
name|long
name|capacityRemaining
init|=
literal|0L
decl_stmt|;
DECL|field|blockPoolUsed
specifier|private
name|long
name|blockPoolUsed
init|=
literal|0L
decl_stmt|;
DECL|field|nodesInService
specifier|private
name|int
name|nodesInService
init|=
literal|0
decl_stmt|;
DECL|field|storageType
specifier|private
name|StorageType
name|storageType
decl_stmt|;
annotation|@
name|ConstructorProperties
argument_list|(
block|{
literal|"capacityTotal"
block|,
literal|"capacityUsed"
block|,
literal|"capacityNonDfsUsed"
block|,
literal|"capacityRemaining"
block|,
literal|"blockPoolUsed"
block|,
literal|"nodesInService"
block|}
argument_list|)
DECL|method|StorageTypeStats ( long capacityTotal, long capacityUsed, long capacityNonDfsUsedUsed, long capacityRemaining, long blockPoolUsed, int nodesInService)
specifier|public
name|StorageTypeStats
parameter_list|(
name|long
name|capacityTotal
parameter_list|,
name|long
name|capacityUsed
parameter_list|,
name|long
name|capacityNonDfsUsedUsed
parameter_list|,
name|long
name|capacityRemaining
parameter_list|,
name|long
name|blockPoolUsed
parameter_list|,
name|int
name|nodesInService
parameter_list|)
block|{
name|this
operator|.
name|capacityTotal
operator|=
name|capacityTotal
expr_stmt|;
name|this
operator|.
name|capacityUsed
operator|=
name|capacityUsed
expr_stmt|;
name|this
operator|.
name|capacityNonDfsUsed
operator|=
name|capacityNonDfsUsedUsed
expr_stmt|;
name|this
operator|.
name|capacityRemaining
operator|=
name|capacityRemaining
expr_stmt|;
name|this
operator|.
name|blockPoolUsed
operator|=
name|blockPoolUsed
expr_stmt|;
name|this
operator|.
name|nodesInService
operator|=
name|nodesInService
expr_stmt|;
block|}
DECL|method|getCapacityTotal ()
specifier|public
name|long
name|getCapacityTotal
parameter_list|()
block|{
comment|// for PROVIDED storage, avoid counting the same storage
comment|// across multiple datanodes
if|if
condition|(
name|storageType
operator|==
name|StorageType
operator|.
name|PROVIDED
operator|&&
name|nodesInService
operator|>
literal|0
condition|)
block|{
return|return
name|capacityTotal
operator|/
name|nodesInService
return|;
block|}
return|return
name|capacityTotal
return|;
block|}
DECL|method|getCapacityUsed ()
specifier|public
name|long
name|getCapacityUsed
parameter_list|()
block|{
comment|// for PROVIDED storage, avoid counting the same storage
comment|// across multiple datanodes
if|if
condition|(
name|storageType
operator|==
name|StorageType
operator|.
name|PROVIDED
operator|&&
name|nodesInService
operator|>
literal|0
condition|)
block|{
return|return
name|capacityUsed
operator|/
name|nodesInService
return|;
block|}
return|return
name|capacityUsed
return|;
block|}
DECL|method|getCapacityNonDfsUsed ()
specifier|public
name|long
name|getCapacityNonDfsUsed
parameter_list|()
block|{
comment|// for PROVIDED storage, avoid counting the same storage
comment|// across multiple datanodes
if|if
condition|(
name|storageType
operator|==
name|StorageType
operator|.
name|PROVIDED
operator|&&
name|nodesInService
operator|>
literal|0
condition|)
block|{
return|return
name|capacityNonDfsUsed
operator|/
name|nodesInService
return|;
block|}
return|return
name|capacityNonDfsUsed
return|;
block|}
DECL|method|getCapacityRemaining ()
specifier|public
name|long
name|getCapacityRemaining
parameter_list|()
block|{
comment|// for PROVIDED storage, avoid counting the same storage
comment|// across multiple datanodes
if|if
condition|(
name|storageType
operator|==
name|StorageType
operator|.
name|PROVIDED
operator|&&
name|nodesInService
operator|>
literal|0
condition|)
block|{
return|return
name|capacityRemaining
operator|/
name|nodesInService
return|;
block|}
return|return
name|capacityRemaining
return|;
block|}
DECL|method|getBlockPoolUsed ()
specifier|public
name|long
name|getBlockPoolUsed
parameter_list|()
block|{
comment|// for PROVIDED storage, avoid counting the same storage
comment|// across multiple datanodes
if|if
condition|(
name|storageType
operator|==
name|StorageType
operator|.
name|PROVIDED
operator|&&
name|nodesInService
operator|>
literal|0
condition|)
block|{
return|return
name|blockPoolUsed
operator|/
name|nodesInService
return|;
block|}
return|return
name|blockPoolUsed
return|;
block|}
DECL|method|getNodesInService ()
specifier|public
name|int
name|getNodesInService
parameter_list|()
block|{
return|return
name|nodesInService
return|;
block|}
DECL|method|StorageTypeStats (StorageType storageType)
name|StorageTypeStats
parameter_list|(
name|StorageType
name|storageType
parameter_list|)
block|{
name|this
operator|.
name|storageType
operator|=
name|storageType
expr_stmt|;
block|}
DECL|method|StorageTypeStats (StorageTypeStats other)
name|StorageTypeStats
parameter_list|(
name|StorageTypeStats
name|other
parameter_list|)
block|{
name|capacityTotal
operator|=
name|other
operator|.
name|capacityTotal
expr_stmt|;
name|capacityUsed
operator|=
name|other
operator|.
name|capacityUsed
expr_stmt|;
name|capacityNonDfsUsed
operator|=
name|other
operator|.
name|capacityNonDfsUsed
expr_stmt|;
name|capacityRemaining
operator|=
name|other
operator|.
name|capacityRemaining
expr_stmt|;
name|blockPoolUsed
operator|=
name|other
operator|.
name|blockPoolUsed
expr_stmt|;
name|nodesInService
operator|=
name|other
operator|.
name|nodesInService
expr_stmt|;
block|}
DECL|method|addStorage (final DatanodeStorageInfo info, final DatanodeDescriptor node)
name|void
name|addStorage
parameter_list|(
specifier|final
name|DatanodeStorageInfo
name|info
parameter_list|,
specifier|final
name|DatanodeDescriptor
name|node
parameter_list|)
block|{
assert|assert
name|storageType
operator|==
name|info
operator|.
name|getStorageType
argument_list|()
assert|;
name|capacityUsed
operator|+=
name|info
operator|.
name|getDfsUsed
argument_list|()
expr_stmt|;
name|capacityNonDfsUsed
operator|+=
name|info
operator|.
name|getNonDfsUsed
argument_list|()
expr_stmt|;
name|blockPoolUsed
operator|+=
name|info
operator|.
name|getBlockPoolUsed
argument_list|()
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|isInService
argument_list|()
condition|)
block|{
name|capacityTotal
operator|+=
name|info
operator|.
name|getCapacity
argument_list|()
expr_stmt|;
name|capacityRemaining
operator|+=
name|info
operator|.
name|getRemaining
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|capacityTotal
operator|+=
name|info
operator|.
name|getDfsUsed
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addNode (final DatanodeDescriptor node)
name|void
name|addNode
parameter_list|(
specifier|final
name|DatanodeDescriptor
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|isInService
argument_list|()
condition|)
block|{
name|nodesInService
operator|++
expr_stmt|;
block|}
block|}
DECL|method|subtractStorage (final DatanodeStorageInfo info, final DatanodeDescriptor node)
name|void
name|subtractStorage
parameter_list|(
specifier|final
name|DatanodeStorageInfo
name|info
parameter_list|,
specifier|final
name|DatanodeDescriptor
name|node
parameter_list|)
block|{
assert|assert
name|storageType
operator|==
name|info
operator|.
name|getStorageType
argument_list|()
assert|;
name|capacityUsed
operator|-=
name|info
operator|.
name|getDfsUsed
argument_list|()
expr_stmt|;
name|capacityNonDfsUsed
operator|-=
name|info
operator|.
name|getNonDfsUsed
argument_list|()
expr_stmt|;
name|blockPoolUsed
operator|-=
name|info
operator|.
name|getBlockPoolUsed
argument_list|()
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|isInService
argument_list|()
condition|)
block|{
name|capacityTotal
operator|-=
name|info
operator|.
name|getCapacity
argument_list|()
expr_stmt|;
name|capacityRemaining
operator|-=
name|info
operator|.
name|getRemaining
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|capacityTotal
operator|-=
name|info
operator|.
name|getDfsUsed
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|subtractNode (final DatanodeDescriptor node)
name|void
name|subtractNode
parameter_list|(
specifier|final
name|DatanodeDescriptor
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|isInService
argument_list|()
condition|)
block|{
name|nodesInService
operator|--
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

