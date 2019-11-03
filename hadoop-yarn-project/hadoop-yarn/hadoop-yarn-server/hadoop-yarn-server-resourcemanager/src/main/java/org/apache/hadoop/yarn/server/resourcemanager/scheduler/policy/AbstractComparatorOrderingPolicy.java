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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentSkipListSet
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
comment|/**  * An OrderingPolicy which can serve as a baseclass for policies which can be  * expressed in terms of comparators  */
end_comment

begin_class
DECL|class|AbstractComparatorOrderingPolicy
specifier|public
specifier|abstract
class|class
name|AbstractComparatorOrderingPolicy
parameter_list|<
name|S
extends|extends
name|SchedulableEntity
parameter_list|>
implements|implements
name|OrderingPolicy
argument_list|<
name|S
argument_list|>
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
name|OrderingPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|schedulableEntities
specifier|protected
name|ConcurrentSkipListSet
argument_list|<
name|S
argument_list|>
name|schedulableEntities
decl_stmt|;
DECL|field|comparator
specifier|protected
name|Comparator
argument_list|<
name|SchedulableEntity
argument_list|>
name|comparator
decl_stmt|;
DECL|field|entitiesToReorder
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|S
argument_list|>
name|entitiesToReorder
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|S
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|AbstractComparatorOrderingPolicy ()
specifier|public
name|AbstractComparatorOrderingPolicy
parameter_list|()
block|{ }
annotation|@
name|Override
DECL|method|getSchedulableEntities ()
specifier|public
name|Collection
argument_list|<
name|S
argument_list|>
name|getSchedulableEntities
parameter_list|()
block|{
return|return
name|schedulableEntities
return|;
block|}
annotation|@
name|Override
DECL|method|getAssignmentIterator (IteratorSelector sel)
specifier|public
name|Iterator
argument_list|<
name|S
argument_list|>
name|getAssignmentIterator
parameter_list|(
name|IteratorSelector
name|sel
parameter_list|)
block|{
name|reorderScheduleEntities
argument_list|()
expr_stmt|;
return|return
name|schedulableEntities
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPreemptionIterator ()
specifier|public
name|Iterator
argument_list|<
name|S
argument_list|>
name|getPreemptionIterator
parameter_list|()
block|{
name|reorderScheduleEntities
argument_list|()
expr_stmt|;
return|return
name|schedulableEntities
operator|.
name|descendingIterator
argument_list|()
return|;
block|}
DECL|method|updateSchedulingResourceUsage (ResourceUsage ru)
specifier|public
specifier|static
name|void
name|updateSchedulingResourceUsage
parameter_list|(
name|ResourceUsage
name|ru
parameter_list|)
block|{
name|ru
operator|.
name|setCachedUsed
argument_list|(
name|CommonNodeLabelsManager
operator|.
name|ANY
argument_list|,
name|ru
operator|.
name|getAllUsed
argument_list|()
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setCachedPending
argument_list|(
name|CommonNodeLabelsManager
operator|.
name|ANY
argument_list|,
name|ru
operator|.
name|getAllPending
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|reorderSchedulableEntity (S schedulableEntity)
specifier|protected
name|void
name|reorderSchedulableEntity
parameter_list|(
name|S
name|schedulableEntity
parameter_list|)
block|{
comment|//remove, update comparable data, and reinsert to update position in order
name|schedulableEntities
operator|.
name|remove
argument_list|(
name|schedulableEntity
argument_list|)
expr_stmt|;
name|updateSchedulingResourceUsage
argument_list|(
name|schedulableEntity
operator|.
name|getSchedulingResourceUsage
argument_list|()
argument_list|)
expr_stmt|;
name|schedulableEntities
operator|.
name|add
argument_list|(
name|schedulableEntity
argument_list|)
expr_stmt|;
block|}
DECL|method|reorderScheduleEntities ()
specifier|protected
name|void
name|reorderScheduleEntities
parameter_list|()
block|{
synchronized|synchronized
init|(
name|entitiesToReorder
init|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|S
argument_list|>
name|entry
range|:
name|entitiesToReorder
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|reorderSchedulableEntity
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|entitiesToReorder
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|entityRequiresReordering (S schedulableEntity)
specifier|protected
name|void
name|entityRequiresReordering
parameter_list|(
name|S
name|schedulableEntity
parameter_list|)
block|{
synchronized|synchronized
init|(
name|entitiesToReorder
init|)
block|{
name|entitiesToReorder
operator|.
name|put
argument_list|(
name|schedulableEntity
operator|.
name|getId
argument_list|()
argument_list|,
name|schedulableEntity
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getComparator ()
specifier|public
name|Comparator
argument_list|<
name|SchedulableEntity
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|comparator
return|;
block|}
annotation|@
name|Override
DECL|method|addSchedulableEntity (S s)
specifier|public
name|void
name|addSchedulableEntity
parameter_list|(
name|S
name|s
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|s
condition|)
block|{
return|return;
block|}
name|schedulableEntities
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeSchedulableEntity (S s)
specifier|public
name|boolean
name|removeSchedulableEntity
parameter_list|(
name|S
name|s
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|s
condition|)
block|{
return|return
literal|false
return|;
block|}
synchronized|synchronized
init|(
name|entitiesToReorder
init|)
block|{
name|entitiesToReorder
operator|.
name|remove
argument_list|(
name|s
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|schedulableEntities
operator|.
name|remove
argument_list|(
name|s
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|addAllSchedulableEntities (Collection<S> sc)
specifier|public
name|void
name|addAllSchedulableEntities
parameter_list|(
name|Collection
argument_list|<
name|S
argument_list|>
name|sc
parameter_list|)
block|{
name|schedulableEntities
operator|.
name|addAll
argument_list|(
name|sc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumSchedulableEntities ()
specifier|public
name|int
name|getNumSchedulableEntities
parameter_list|()
block|{
return|return
name|schedulableEntities
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|configure (Map<String, String> conf)
specifier|public
specifier|abstract
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
function_decl|;
annotation|@
name|Override
DECL|method|containerAllocated (S schedulableEntity, RMContainer r)
specifier|public
specifier|abstract
name|void
name|containerAllocated
parameter_list|(
name|S
name|schedulableEntity
parameter_list|,
name|RMContainer
name|r
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|containerReleased (S schedulableEntity, RMContainer r)
specifier|public
specifier|abstract
name|void
name|containerReleased
parameter_list|(
name|S
name|schedulableEntity
parameter_list|,
name|RMContainer
name|r
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|demandUpdated (S schedulableEntity)
specifier|public
specifier|abstract
name|void
name|demandUpdated
parameter_list|(
name|S
name|schedulableEntity
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|getInfo ()
specifier|public
specifier|abstract
name|String
name|getInfo
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|getConfigName ()
specifier|public
specifier|abstract
name|String
name|getConfigName
parameter_list|()
function_decl|;
block|}
end_class

end_unit

