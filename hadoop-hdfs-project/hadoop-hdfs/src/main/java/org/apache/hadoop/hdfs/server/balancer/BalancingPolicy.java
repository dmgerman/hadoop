begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.balancer
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
name|balancer
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
name|hdfs
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
name|server
operator|.
name|protocol
operator|.
name|DatanodeStorageReport
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
name|protocol
operator|.
name|StorageReport
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
name|util
operator|.
name|EnumCounters
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
name|util
operator|.
name|EnumDoubles
import|;
end_import

begin_comment
comment|/**  * Balancing policy.  * Since a datanode may contain multiple block pools,  * {@link Pool} implies {@link Node}  * but NOT the other way around  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BalancingPolicy
specifier|abstract
class|class
name|BalancingPolicy
block|{
DECL|field|totalCapacities
specifier|final
name|EnumCounters
argument_list|<
name|StorageType
argument_list|>
name|totalCapacities
init|=
operator|new
name|EnumCounters
argument_list|<
name|StorageType
argument_list|>
argument_list|(
name|StorageType
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|totalUsedSpaces
specifier|final
name|EnumCounters
argument_list|<
name|StorageType
argument_list|>
name|totalUsedSpaces
init|=
operator|new
name|EnumCounters
argument_list|<
name|StorageType
argument_list|>
argument_list|(
name|StorageType
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|avgUtilizations
specifier|final
name|EnumDoubles
argument_list|<
name|StorageType
argument_list|>
name|avgUtilizations
init|=
operator|new
name|EnumDoubles
argument_list|<
name|StorageType
argument_list|>
argument_list|(
name|StorageType
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|reset ()
name|void
name|reset
parameter_list|()
block|{
name|totalCapacities
operator|.
name|reset
argument_list|()
expr_stmt|;
name|totalUsedSpaces
operator|.
name|reset
argument_list|()
expr_stmt|;
name|avgUtilizations
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/** Get the policy name. */
DECL|method|getName ()
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
comment|/** Accumulate used space and capacity. */
DECL|method|accumulateSpaces (DatanodeStorageReport r)
specifier|abstract
name|void
name|accumulateSpaces
parameter_list|(
name|DatanodeStorageReport
name|r
parameter_list|)
function_decl|;
DECL|method|initAvgUtilization ()
name|void
name|initAvgUtilization
parameter_list|()
block|{
for|for
control|(
name|StorageType
name|t
range|:
name|StorageType
operator|.
name|asList
argument_list|()
control|)
block|{
specifier|final
name|long
name|capacity
init|=
name|totalCapacities
operator|.
name|get
argument_list|(
name|t
argument_list|)
decl_stmt|;
if|if
condition|(
name|capacity
operator|>
literal|0L
condition|)
block|{
specifier|final
name|double
name|avg
init|=
name|totalUsedSpaces
operator|.
name|get
argument_list|(
name|t
argument_list|)
operator|*
literal|100.0
operator|/
name|capacity
decl_stmt|;
name|avgUtilizations
operator|.
name|set
argument_list|(
name|t
argument_list|,
name|avg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getAvgUtilization (StorageType t)
name|double
name|getAvgUtilization
parameter_list|(
name|StorageType
name|t
parameter_list|)
block|{
return|return
name|avgUtilizations
operator|.
name|get
argument_list|(
name|t
argument_list|)
return|;
block|}
comment|/** @return the utilization of a particular storage type of a datanode;    *          or return null if the datanode does not have such storage type.    */
DECL|method|getUtilization (DatanodeStorageReport r, StorageType t)
specifier|abstract
name|Double
name|getUtilization
parameter_list|(
name|DatanodeStorageReport
name|r
parameter_list|,
name|StorageType
name|t
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|BalancingPolicy
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"."
operator|+
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
return|;
block|}
comment|/** Get all {@link BalancingPolicy} instances*/
DECL|method|parse (String s)
specifier|static
name|BalancingPolicy
name|parse
parameter_list|(
name|String
name|s
parameter_list|)
block|{
specifier|final
name|BalancingPolicy
index|[]
name|all
init|=
block|{
name|BalancingPolicy
operator|.
name|Node
operator|.
name|INSTANCE
block|,
name|BalancingPolicy
operator|.
name|Pool
operator|.
name|INSTANCE
block|}
decl_stmt|;
for|for
control|(
name|BalancingPolicy
name|p
range|:
name|all
control|)
block|{
if|if
condition|(
name|p
operator|.
name|getName
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|s
argument_list|)
condition|)
return|return
name|p
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot parse string \""
operator|+
name|s
operator|+
literal|"\""
argument_list|)
throw|;
block|}
comment|/**    * Cluster is balanced if each node is balanced.    */
DECL|class|Node
specifier|static
class|class
name|Node
extends|extends
name|BalancingPolicy
block|{
DECL|field|INSTANCE
specifier|static
specifier|final
name|Node
name|INSTANCE
init|=
operator|new
name|Node
argument_list|()
decl_stmt|;
DECL|method|Node ()
specifier|private
name|Node
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|getName ()
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"datanode"
return|;
block|}
annotation|@
name|Override
DECL|method|accumulateSpaces (DatanodeStorageReport r)
name|void
name|accumulateSpaces
parameter_list|(
name|DatanodeStorageReport
name|r
parameter_list|)
block|{
for|for
control|(
name|StorageReport
name|s
range|:
name|r
operator|.
name|getStorageReports
argument_list|()
control|)
block|{
specifier|final
name|StorageType
name|t
init|=
name|s
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageType
argument_list|()
decl_stmt|;
name|totalCapacities
operator|.
name|add
argument_list|(
name|t
argument_list|,
name|s
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|totalUsedSpaces
operator|.
name|add
argument_list|(
name|t
argument_list|,
name|s
operator|.
name|getDfsUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getUtilization (DatanodeStorageReport r, final StorageType t)
name|Double
name|getUtilization
parameter_list|(
name|DatanodeStorageReport
name|r
parameter_list|,
specifier|final
name|StorageType
name|t
parameter_list|)
block|{
name|long
name|capacity
init|=
literal|0L
decl_stmt|;
name|long
name|dfsUsed
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|StorageReport
name|s
range|:
name|r
operator|.
name|getStorageReports
argument_list|()
control|)
block|{
if|if
condition|(
name|s
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageType
argument_list|()
operator|==
name|t
condition|)
block|{
name|capacity
operator|+=
name|s
operator|.
name|getCapacity
argument_list|()
expr_stmt|;
name|dfsUsed
operator|+=
name|s
operator|.
name|getDfsUsed
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|capacity
operator|==
literal|0L
condition|?
literal|null
else|:
name|dfsUsed
operator|*
literal|100.0
operator|/
name|capacity
return|;
block|}
block|}
comment|/**    * Cluster is balanced if each pool in each node is balanced.    */
DECL|class|Pool
specifier|static
class|class
name|Pool
extends|extends
name|BalancingPolicy
block|{
DECL|field|INSTANCE
specifier|static
specifier|final
name|Pool
name|INSTANCE
init|=
operator|new
name|Pool
argument_list|()
decl_stmt|;
DECL|method|Pool ()
specifier|private
name|Pool
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|getName ()
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"blockpool"
return|;
block|}
annotation|@
name|Override
DECL|method|accumulateSpaces (DatanodeStorageReport r)
name|void
name|accumulateSpaces
parameter_list|(
name|DatanodeStorageReport
name|r
parameter_list|)
block|{
for|for
control|(
name|StorageReport
name|s
range|:
name|r
operator|.
name|getStorageReports
argument_list|()
control|)
block|{
specifier|final
name|StorageType
name|t
init|=
name|s
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageType
argument_list|()
decl_stmt|;
name|totalCapacities
operator|.
name|add
argument_list|(
name|t
argument_list|,
name|s
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|totalUsedSpaces
operator|.
name|add
argument_list|(
name|t
argument_list|,
name|s
operator|.
name|getBlockPoolUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getUtilization (DatanodeStorageReport r, final StorageType t)
name|Double
name|getUtilization
parameter_list|(
name|DatanodeStorageReport
name|r
parameter_list|,
specifier|final
name|StorageType
name|t
parameter_list|)
block|{
name|long
name|capacity
init|=
literal|0L
decl_stmt|;
name|long
name|blockPoolUsed
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|StorageReport
name|s
range|:
name|r
operator|.
name|getStorageReports
argument_list|()
control|)
block|{
if|if
condition|(
name|s
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageType
argument_list|()
operator|==
name|t
condition|)
block|{
name|capacity
operator|+=
name|s
operator|.
name|getCapacity
argument_list|()
expr_stmt|;
name|blockPoolUsed
operator|+=
name|s
operator|.
name|getBlockPoolUsed
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|capacity
operator|==
literal|0L
condition|?
literal|null
else|:
name|blockPoolUsed
operator|*
literal|100.0
operator|/
name|capacity
return|;
block|}
block|}
block|}
end_class

end_unit

