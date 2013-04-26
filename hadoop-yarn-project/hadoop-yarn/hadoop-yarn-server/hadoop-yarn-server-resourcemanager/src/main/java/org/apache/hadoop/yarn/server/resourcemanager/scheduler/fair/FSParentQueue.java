begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
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
name|fair
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|security
operator|.
name|UserGroupInformation
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
name|api
operator|.
name|records
operator|.
name|QueueACL
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
name|api
operator|.
name|records
operator|.
name|QueueUserACLInfo
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
name|api
operator|.
name|records
operator|.
name|Resource
import|;
end_import

begin_class
DECL|class|FSParentQueue
specifier|public
class|class
name|FSParentQueue
extends|extends
name|FSQueue
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FSParentQueue
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|childQueues
specifier|private
specifier|final
name|List
argument_list|<
name|FSQueue
argument_list|>
name|childQueues
init|=
operator|new
name|ArrayList
argument_list|<
name|FSQueue
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|queueMgr
specifier|private
specifier|final
name|QueueManager
name|queueMgr
decl_stmt|;
DECL|field|demand
specifier|private
name|Resource
name|demand
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|FSParentQueue (String name, QueueManager queueMgr, FairScheduler scheduler, FSParentQueue parent)
specifier|public
name|FSParentQueue
parameter_list|(
name|String
name|name
parameter_list|,
name|QueueManager
name|queueMgr
parameter_list|,
name|FairScheduler
name|scheduler
parameter_list|,
name|FSParentQueue
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|queueMgr
argument_list|,
name|scheduler
argument_list|,
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|queueMgr
operator|=
name|queueMgr
expr_stmt|;
block|}
DECL|method|addChildQueue (FSQueue child)
specifier|public
name|void
name|addChildQueue
parameter_list|(
name|FSQueue
name|child
parameter_list|)
block|{
name|childQueues
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|recomputeShares ()
specifier|public
name|void
name|recomputeShares
parameter_list|()
block|{
name|policy
operator|.
name|computeShares
argument_list|(
name|childQueues
argument_list|,
name|getFairShare
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|FSQueue
name|childQueue
range|:
name|childQueues
control|)
block|{
name|childQueue
operator|.
name|getMetrics
argument_list|()
operator|.
name|setAvailableResourcesToQueue
argument_list|(
name|childQueue
operator|.
name|getFairShare
argument_list|()
argument_list|)
expr_stmt|;
name|childQueue
operator|.
name|recomputeShares
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDemand ()
specifier|public
name|Resource
name|getDemand
parameter_list|()
block|{
return|return
name|demand
return|;
block|}
annotation|@
name|Override
DECL|method|getResourceUsage ()
specifier|public
name|Resource
name|getResourceUsage
parameter_list|()
block|{
name|Resource
name|usage
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|FSQueue
name|child
range|:
name|childQueues
control|)
block|{
name|Resources
operator|.
name|addTo
argument_list|(
name|usage
argument_list|,
name|child
operator|.
name|getResourceUsage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|usage
return|;
block|}
annotation|@
name|Override
DECL|method|updateDemand ()
specifier|public
name|void
name|updateDemand
parameter_list|()
block|{
comment|// Compute demand by iterating through apps in the queue
comment|// Limit demand to maxResources
name|Resource
name|maxRes
init|=
name|queueMgr
operator|.
name|getMaxResources
argument_list|(
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|demand
operator|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|FSQueue
name|childQueue
range|:
name|childQueues
control|)
block|{
name|childQueue
operator|.
name|updateDemand
argument_list|()
expr_stmt|;
name|Resource
name|toAdd
init|=
name|childQueue
operator|.
name|getDemand
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Counting resource from "
operator|+
name|childQueue
operator|.
name|getName
argument_list|()
operator|+
literal|" "
operator|+
name|toAdd
operator|+
literal|"; Total resource consumption for "
operator|+
name|getName
argument_list|()
operator|+
literal|" now "
operator|+
name|demand
argument_list|)
expr_stmt|;
block|}
name|demand
operator|=
name|Resources
operator|.
name|add
argument_list|(
name|demand
argument_list|,
name|toAdd
argument_list|)
expr_stmt|;
if|if
condition|(
name|Resources
operator|.
name|greaterThanOrEqual
argument_list|(
name|demand
argument_list|,
name|maxRes
argument_list|)
condition|)
block|{
name|demand
operator|=
name|maxRes
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"The updated demand for "
operator|+
name|getName
argument_list|()
operator|+
literal|" is "
operator|+
name|demand
operator|+
literal|"; the max is "
operator|+
name|maxRes
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getUserAclInfo ( UserGroupInformation user)
specifier|private
specifier|synchronized
name|QueueUserACLInfo
name|getUserAclInfo
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|)
block|{
name|QueueUserACLInfo
name|userAclInfo
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|QueueUserACLInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|QueueACL
argument_list|>
name|operations
init|=
operator|new
name|ArrayList
argument_list|<
name|QueueACL
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|QueueACL
name|operation
range|:
name|QueueACL
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|hasAccess
argument_list|(
name|operation
argument_list|,
name|user
argument_list|)
condition|)
block|{
name|operations
operator|.
name|add
argument_list|(
name|operation
argument_list|)
expr_stmt|;
block|}
block|}
name|userAclInfo
operator|.
name|setQueueName
argument_list|(
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|userAclInfo
operator|.
name|setUserAcls
argument_list|(
name|operations
argument_list|)
expr_stmt|;
return|return
name|userAclInfo
return|;
block|}
annotation|@
name|Override
DECL|method|getQueueUserAclInfo ( UserGroupInformation user)
specifier|public
specifier|synchronized
name|List
argument_list|<
name|QueueUserACLInfo
argument_list|>
name|getQueueUserAclInfo
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|)
block|{
name|List
argument_list|<
name|QueueUserACLInfo
argument_list|>
name|userAcls
init|=
operator|new
name|ArrayList
argument_list|<
name|QueueUserACLInfo
argument_list|>
argument_list|()
decl_stmt|;
comment|// Add queue acls
name|userAcls
operator|.
name|add
argument_list|(
name|getUserAclInfo
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add children queue acls
for|for
control|(
name|FSQueue
name|child
range|:
name|childQueues
control|)
block|{
name|userAcls
operator|.
name|addAll
argument_list|(
name|child
operator|.
name|getQueueUserAclInfo
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|userAcls
return|;
block|}
annotation|@
name|Override
DECL|method|assignContainer (FSSchedulerNode node)
specifier|public
name|Resource
name|assignContainer
parameter_list|(
name|FSSchedulerNode
name|node
parameter_list|)
block|{
name|Resource
name|assigned
init|=
name|Resources
operator|.
name|none
argument_list|()
decl_stmt|;
comment|// If this queue is over its limit, reject
if|if
condition|(
name|Resources
operator|.
name|greaterThan
argument_list|(
name|getResourceUsage
argument_list|()
argument_list|,
name|queueMgr
operator|.
name|getMaxResources
argument_list|(
name|getName
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|assigned
return|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|childQueues
argument_list|,
name|policy
operator|.
name|getComparator
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|FSQueue
name|child
range|:
name|childQueues
control|)
block|{
name|assigned
operator|=
name|child
operator|.
name|assignContainer
argument_list|(
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getReservedContainer
argument_list|()
operator|!=
literal|null
operator|||
name|Resources
operator|.
name|greaterThan
argument_list|(
name|assigned
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
return|return
name|assigned
return|;
block|}
annotation|@
name|Override
DECL|method|getChildQueues ()
specifier|public
name|Collection
argument_list|<
name|FSQueue
argument_list|>
name|getChildQueues
parameter_list|()
block|{
return|return
name|childQueues
return|;
block|}
annotation|@
name|Override
DECL|method|setPolicy (SchedulingPolicy policy)
specifier|public
name|void
name|setPolicy
parameter_list|(
name|SchedulingPolicy
name|policy
parameter_list|)
throws|throws
name|AllocationConfigurationException
block|{
name|boolean
name|allowed
init|=
name|SchedulingPolicy
operator|.
name|isApplicableTo
argument_list|(
name|policy
argument_list|,
operator|(
name|this
operator|==
name|queueMgr
operator|.
name|getRootQueue
argument_list|()
operator|)
condition|?
name|SchedulingPolicy
operator|.
name|DEPTH_ROOT
else|:
name|SchedulingPolicy
operator|.
name|DEPTH_INTERMEDIATE
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|allowed
condition|)
block|{
name|throwPolicyDoesnotApplyException
argument_list|(
name|policy
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|policy
operator|=
name|policy
expr_stmt|;
block|}
block|}
end_class

end_unit

