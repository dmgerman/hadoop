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
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Map
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
name|security
operator|.
name|authorize
operator|.
name|AccessControlList
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
DECL|class|FSLeafQueue
specifier|public
class|class
name|FSLeafQueue
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
name|FSLeafQueue
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|appScheds
specifier|private
specifier|final
name|List
argument_list|<
name|AppSchedulable
argument_list|>
name|appScheds
init|=
operator|new
name|ArrayList
argument_list|<
name|AppSchedulable
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Scheduling mode for jobs inside the queue (fair or FIFO) */
DECL|field|schedulingMode
specifier|private
name|SchedulingMode
name|schedulingMode
decl_stmt|;
DECL|field|scheduler
specifier|private
specifier|final
name|FairScheduler
name|scheduler
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
comment|// Variables used for preemption
DECL|field|lastTimeAtMinShare
specifier|private
name|long
name|lastTimeAtMinShare
decl_stmt|;
DECL|field|lastTimeAtHalfFairShare
specifier|private
name|long
name|lastTimeAtHalfFairShare
decl_stmt|;
DECL|method|FSLeafQueue (String name, QueueManager queueMgr, FairScheduler scheduler, FSParentQueue parent)
specifier|public
name|FSLeafQueue
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
name|scheduler
operator|=
name|scheduler
expr_stmt|;
name|this
operator|.
name|queueMgr
operator|=
name|queueMgr
expr_stmt|;
name|this
operator|.
name|lastTimeAtMinShare
operator|=
name|scheduler
operator|.
name|getClock
argument_list|()
operator|.
name|getTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastTimeAtHalfFairShare
operator|=
name|scheduler
operator|.
name|getClock
argument_list|()
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
DECL|method|addApp (FSSchedulerApp app)
specifier|public
name|void
name|addApp
parameter_list|(
name|FSSchedulerApp
name|app
parameter_list|)
block|{
name|AppSchedulable
name|appSchedulable
init|=
operator|new
name|AppSchedulable
argument_list|(
name|scheduler
argument_list|,
name|app
argument_list|,
name|this
argument_list|)
decl_stmt|;
name|app
operator|.
name|setAppSchedulable
argument_list|(
name|appSchedulable
argument_list|)
expr_stmt|;
name|appScheds
operator|.
name|add
argument_list|(
name|appSchedulable
argument_list|)
expr_stmt|;
block|}
comment|// for testing
DECL|method|addAppSchedulable (AppSchedulable appSched)
name|void
name|addAppSchedulable
parameter_list|(
name|AppSchedulable
name|appSched
parameter_list|)
block|{
name|appScheds
operator|.
name|add
argument_list|(
name|appSched
argument_list|)
expr_stmt|;
block|}
DECL|method|removeApp (FSSchedulerApp app)
specifier|public
name|void
name|removeApp
parameter_list|(
name|FSSchedulerApp
name|app
parameter_list|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|AppSchedulable
argument_list|>
name|it
init|=
name|appScheds
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|AppSchedulable
name|appSched
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|appSched
operator|.
name|getApp
argument_list|()
operator|==
name|app
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|method|getAppSchedulables ()
specifier|public
name|Collection
argument_list|<
name|AppSchedulable
argument_list|>
name|getAppSchedulables
parameter_list|()
block|{
return|return
name|appScheds
return|;
block|}
DECL|method|setSchedulingMode (SchedulingMode mode)
specifier|public
name|void
name|setSchedulingMode
parameter_list|(
name|SchedulingMode
name|mode
parameter_list|)
block|{
name|this
operator|.
name|schedulingMode
operator|=
name|mode
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|recomputeFairShares ()
specifier|public
name|void
name|recomputeFairShares
parameter_list|()
block|{
if|if
condition|(
name|schedulingMode
operator|==
name|SchedulingMode
operator|.
name|FAIR
condition|)
block|{
name|SchedulingAlgorithms
operator|.
name|computeFairShares
argument_list|(
name|appScheds
argument_list|,
name|getFairShare
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|AppSchedulable
name|sched
range|:
name|appScheds
control|)
block|{
name|sched
operator|.
name|setFairShare
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|AppSchedulable
name|app
range|:
name|appScheds
control|)
block|{
name|Resources
operator|.
name|addTo
argument_list|(
name|usage
argument_list|,
name|app
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
name|AppSchedulable
name|sched
range|:
name|appScheds
control|)
block|{
name|sched
operator|.
name|updateDemand
argument_list|()
expr_stmt|;
name|Resource
name|toAdd
init|=
name|sched
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
name|sched
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
annotation|@
name|Override
DECL|method|assignContainer (FSSchedulerNode node, boolean reserved)
specifier|public
name|Resource
name|assignContainer
parameter_list|(
name|FSSchedulerNode
name|node
parameter_list|,
name|boolean
name|reserved
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Node offered to queue: "
operator|+
name|getName
argument_list|()
operator|+
literal|" reserved: "
operator|+
name|reserved
argument_list|)
expr_stmt|;
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
name|Resources
operator|.
name|none
argument_list|()
return|;
block|}
comment|// If this node already has reserved resources for an app, first try to
comment|// finish allocating resources for that app.
if|if
condition|(
name|reserved
condition|)
block|{
for|for
control|(
name|AppSchedulable
name|sched
range|:
name|appScheds
control|)
block|{
if|if
condition|(
name|sched
operator|.
name|getApp
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
operator|==
name|node
operator|.
name|getReservedContainer
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
condition|)
block|{
return|return
name|sched
operator|.
name|assignContainer
argument_list|(
name|node
argument_list|,
name|reserved
argument_list|)
return|;
block|}
block|}
return|return
name|Resources
operator|.
name|none
argument_list|()
return|;
comment|// We should never get here
block|}
comment|// Otherwise, chose app to schedule based on given policy (fair vs fifo).
else|else
block|{
name|Comparator
argument_list|<
name|Schedulable
argument_list|>
name|comparator
decl_stmt|;
if|if
condition|(
name|schedulingMode
operator|==
name|SchedulingMode
operator|.
name|FIFO
condition|)
block|{
name|comparator
operator|=
operator|new
name|SchedulingAlgorithms
operator|.
name|FifoComparator
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|schedulingMode
operator|==
name|SchedulingMode
operator|.
name|FAIR
condition|)
block|{
name|comparator
operator|=
operator|new
name|SchedulingAlgorithms
operator|.
name|FairShareComparator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unsupported queue scheduling mode "
operator|+
name|schedulingMode
argument_list|)
throw|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|appScheds
argument_list|,
name|comparator
argument_list|)
expr_stmt|;
for|for
control|(
name|AppSchedulable
name|sched
range|:
name|appScheds
control|)
block|{
if|if
condition|(
name|sched
operator|.
name|getRunnable
argument_list|()
condition|)
block|{
name|Resource
name|assignedResource
init|=
name|sched
operator|.
name|assignContainer
argument_list|(
name|node
argument_list|,
name|reserved
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|assignedResource
operator|.
name|equals
argument_list|(
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|assignedResource
return|;
block|}
block|}
block|}
return|return
name|Resources
operator|.
name|none
argument_list|()
return|;
block|}
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
operator|new
name|ArrayList
argument_list|<
name|FSQueue
argument_list|>
argument_list|(
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getQueueUserAclInfo (UserGroupInformation user)
specifier|public
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
name|Map
argument_list|<
name|QueueACL
argument_list|,
name|AccessControlList
argument_list|>
name|acls
init|=
name|queueMgr
operator|.
name|getQueueAcls
argument_list|(
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|acls
operator|.
name|get
argument_list|(
name|operation
argument_list|)
operator|.
name|isUserAllowed
argument_list|(
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
name|Collections
operator|.
name|singletonList
argument_list|(
name|userAclInfo
argument_list|)
return|;
block|}
DECL|method|getLastTimeAtMinShare ()
specifier|public
name|long
name|getLastTimeAtMinShare
parameter_list|()
block|{
return|return
name|lastTimeAtMinShare
return|;
block|}
DECL|method|setLastTimeAtMinShare (long lastTimeAtMinShare)
specifier|public
name|void
name|setLastTimeAtMinShare
parameter_list|(
name|long
name|lastTimeAtMinShare
parameter_list|)
block|{
name|this
operator|.
name|lastTimeAtMinShare
operator|=
name|lastTimeAtMinShare
expr_stmt|;
block|}
DECL|method|getLastTimeAtHalfFairShare ()
specifier|public
name|long
name|getLastTimeAtHalfFairShare
parameter_list|()
block|{
return|return
name|lastTimeAtHalfFairShare
return|;
block|}
DECL|method|setLastTimeAtHalfFairShare (long lastTimeAtHalfFairShare)
specifier|public
name|void
name|setLastTimeAtHalfFairShare
parameter_list|(
name|long
name|lastTimeAtHalfFairShare
parameter_list|)
block|{
name|this
operator|.
name|lastTimeAtHalfFairShare
operator|=
name|lastTimeAtHalfFairShare
expr_stmt|;
block|}
block|}
end_class

end_unit

