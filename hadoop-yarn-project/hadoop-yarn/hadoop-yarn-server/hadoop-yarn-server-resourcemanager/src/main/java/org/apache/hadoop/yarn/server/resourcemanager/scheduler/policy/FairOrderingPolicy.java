begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.policy
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|policy
package|;
end_package

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|rmcontainer
operator|.
name|RMContainer
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|*
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
name|yarn
operator|.
name|nodelabels
operator|.
name|CommonNodeLabelsManager
import|;
end_import

begin_comment
comment|/**  * An OrderingPolicy which orders SchedulableEntities for fairness (see  * FairScheduler  * FairSharePolicy), generally, processes with lesser usage are lesser. If  * sizedBasedWeight is set to true then an application with high demand  * may be prioritized ahead of an application with less usage.  This  * is to offset the tendency to favor small apps, which could result in  * starvation for large apps if many small ones enter and leave the queue  * continuously (optional, default false)  */
end_comment

begin_class
DECL|class|FairOrderingPolicy
specifier|public
class|class
name|FairOrderingPolicy
parameter_list|<
name|S
extends|extends
name|SchedulableEntity
parameter_list|>
extends|extends
name|AbstractComparatorOrderingPolicy
argument_list|<
name|S
argument_list|>
block|{
DECL|field|ENABLE_SIZE_BASED_WEIGHT
specifier|public
specifier|static
specifier|final
name|String
name|ENABLE_SIZE_BASED_WEIGHT
init|=
literal|"fair.enable-size-based-weight"
decl_stmt|;
DECL|class|FairComparator
specifier|protected
class|class
name|FairComparator
implements|implements
name|Comparator
argument_list|<
name|SchedulableEntity
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare (final SchedulableEntity r1, final SchedulableEntity r2)
specifier|public
name|int
name|compare
parameter_list|(
specifier|final
name|SchedulableEntity
name|r1
parameter_list|,
specifier|final
name|SchedulableEntity
name|r2
parameter_list|)
block|{
name|int
name|res
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|signum
argument_list|(
name|getMagnitude
argument_list|(
name|r1
argument_list|)
operator|-
name|getMagnitude
argument_list|(
name|r2
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|res
return|;
block|}
block|}
DECL|field|fairComparator
specifier|private
name|CompoundComparator
name|fairComparator
decl_stmt|;
DECL|field|sizeBasedWeight
specifier|private
name|boolean
name|sizeBasedWeight
init|=
literal|false
decl_stmt|;
DECL|method|FairOrderingPolicy ()
specifier|public
name|FairOrderingPolicy
parameter_list|()
block|{
name|List
argument_list|<
name|Comparator
argument_list|<
name|SchedulableEntity
argument_list|>
argument_list|>
name|comparators
init|=
operator|new
name|ArrayList
argument_list|<
name|Comparator
argument_list|<
name|SchedulableEntity
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|comparators
operator|.
name|add
argument_list|(
operator|new
name|FairComparator
argument_list|()
argument_list|)
expr_stmt|;
name|comparators
operator|.
name|add
argument_list|(
operator|new
name|FifoComparator
argument_list|()
argument_list|)
expr_stmt|;
name|fairComparator
operator|=
operator|new
name|CompoundComparator
argument_list|(
name|comparators
argument_list|)
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|fairComparator
expr_stmt|;
name|this
operator|.
name|schedulableEntities
operator|=
operator|new
name|TreeSet
argument_list|<
name|S
argument_list|>
argument_list|(
name|comparator
argument_list|)
expr_stmt|;
block|}
DECL|method|getMagnitude (SchedulableEntity r)
specifier|private
name|double
name|getMagnitude
parameter_list|(
name|SchedulableEntity
name|r
parameter_list|)
block|{
name|double
name|mag
init|=
name|r
operator|.
name|getSchedulingResourceUsage
argument_list|()
operator|.
name|getCachedUsed
argument_list|(
name|CommonNodeLabelsManager
operator|.
name|ANY
argument_list|)
operator|.
name|getMemory
argument_list|()
decl_stmt|;
if|if
condition|(
name|sizeBasedWeight
condition|)
block|{
name|double
name|weight
init|=
name|Math
operator|.
name|log1p
argument_list|(
name|r
operator|.
name|getSchedulingResourceUsage
argument_list|()
operator|.
name|getCachedDemand
argument_list|(
name|CommonNodeLabelsManager
operator|.
name|ANY
argument_list|)
operator|.
name|getMemory
argument_list|()
argument_list|)
operator|/
name|Math
operator|.
name|log
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|mag
operator|=
name|mag
operator|/
name|weight
expr_stmt|;
block|}
return|return
name|mag
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getSizeBasedWeight ()
specifier|public
name|boolean
name|getSizeBasedWeight
parameter_list|()
block|{
return|return
name|sizeBasedWeight
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setSizeBasedWeight (boolean sizeBasedWeight)
specifier|public
name|void
name|setSizeBasedWeight
parameter_list|(
name|boolean
name|sizeBasedWeight
parameter_list|)
block|{
name|this
operator|.
name|sizeBasedWeight
operator|=
name|sizeBasedWeight
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure (Map<String, String> conf)
specifier|public
name|void
name|configure
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|conf
parameter_list|)
block|{
if|if
condition|(
name|conf
operator|.
name|containsKey
argument_list|(
name|ENABLE_SIZE_BASED_WEIGHT
argument_list|)
condition|)
block|{
name|sizeBasedWeight
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|ENABLE_SIZE_BASED_WEIGHT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|containerAllocated (S schedulableEntity, RMContainer r)
specifier|public
name|void
name|containerAllocated
parameter_list|(
name|S
name|schedulableEntity
parameter_list|,
name|RMContainer
name|r
parameter_list|)
block|{
name|entityRequiresReordering
argument_list|(
name|schedulableEntity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|containerReleased (S schedulableEntity, RMContainer r)
specifier|public
name|void
name|containerReleased
parameter_list|(
name|S
name|schedulableEntity
parameter_list|,
name|RMContainer
name|r
parameter_list|)
block|{
name|entityRequiresReordering
argument_list|(
name|schedulableEntity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|demandUpdated (S schedulableEntity)
specifier|public
name|void
name|demandUpdated
parameter_list|(
name|S
name|schedulableEntity
parameter_list|)
block|{
if|if
condition|(
name|sizeBasedWeight
condition|)
block|{
name|entityRequiresReordering
argument_list|(
name|schedulableEntity
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getInfo ()
specifier|public
name|String
name|getInfo
parameter_list|()
block|{
name|String
name|sbw
init|=
name|sizeBasedWeight
condition|?
literal|" with sizeBasedWeight"
else|:
literal|""
decl_stmt|;
return|return
literal|"FairOrderingPolicy"
operator|+
name|sbw
return|;
block|}
block|}
end_class

end_unit

