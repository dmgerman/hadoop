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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
operator|.
name|Unstable
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
name|SchedulerAppUtils
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
name|util
operator|.
name|resource
operator|.
name|Resources
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
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
DECL|field|runnableAppScheds
specifier|private
specifier|final
name|List
argument_list|<
name|AppSchedulable
argument_list|>
name|runnableAppScheds
init|=
comment|// apps that are runnable
operator|new
name|ArrayList
argument_list|<
name|AppSchedulable
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|nonRunnableAppScheds
specifier|private
specifier|final
name|List
argument_list|<
name|AppSchedulable
argument_list|>
name|nonRunnableAppScheds
init|=
operator|new
name|ArrayList
argument_list|<
name|AppSchedulable
argument_list|>
argument_list|()
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
DECL|method|FSLeafQueue (String name, FairScheduler scheduler, FSParentQueue parent)
specifier|public
name|FSLeafQueue
parameter_list|(
name|String
name|name
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
name|scheduler
argument_list|,
name|parent
argument_list|)
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
DECL|method|addApp (FSSchedulerApp app, boolean runnable)
specifier|public
name|void
name|addApp
parameter_list|(
name|FSSchedulerApp
name|app
parameter_list|,
name|boolean
name|runnable
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
if|if
condition|(
name|runnable
condition|)
block|{
name|runnableAppScheds
operator|.
name|add
argument_list|(
name|appSchedulable
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nonRunnableAppScheds
operator|.
name|add
argument_list|(
name|appSchedulable
argument_list|)
expr_stmt|;
block|}
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
name|runnableAppScheds
operator|.
name|add
argument_list|(
name|appSched
argument_list|)
expr_stmt|;
block|}
comment|/**    * Removes the given app from this queue.    * @return whether or not the app was runnable    */
DECL|method|removeApp (FSSchedulerApp app)
specifier|public
name|boolean
name|removeApp
parameter_list|(
name|FSSchedulerApp
name|app
parameter_list|)
block|{
if|if
condition|(
name|runnableAppScheds
operator|.
name|remove
argument_list|(
name|app
operator|.
name|getAppSchedulable
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|nonRunnableAppScheds
operator|.
name|remove
argument_list|(
name|app
operator|.
name|getAppSchedulable
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Given app to remove "
operator|+
name|app
operator|+
literal|" does not exist in queue "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
DECL|method|makeAppRunnable (AppSchedulable appSched)
specifier|public
name|void
name|makeAppRunnable
parameter_list|(
name|AppSchedulable
name|appSched
parameter_list|)
block|{
if|if
condition|(
operator|!
name|nonRunnableAppScheds
operator|.
name|remove
argument_list|(
name|appSched
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't make app runnable that does not "
operator|+
literal|"already exist in queue as non-runnable"
operator|+
name|appSched
argument_list|)
throw|;
block|}
name|runnableAppScheds
operator|.
name|add
argument_list|(
name|appSched
argument_list|)
expr_stmt|;
block|}
DECL|method|getRunnableAppSchedulables ()
specifier|public
name|Collection
argument_list|<
name|AppSchedulable
argument_list|>
name|getRunnableAppSchedulables
parameter_list|()
block|{
return|return
name|runnableAppScheds
return|;
block|}
DECL|method|getNonRunnableAppSchedulables ()
specifier|public
name|List
argument_list|<
name|AppSchedulable
argument_list|>
name|getNonRunnableAppSchedulables
parameter_list|()
block|{
return|return
name|nonRunnableAppScheds
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
if|if
condition|(
operator|!
name|SchedulingPolicy
operator|.
name|isApplicableTo
argument_list|(
name|policy
argument_list|,
name|SchedulingPolicy
operator|.
name|DEPTH_LEAF
argument_list|)
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
name|getRunnableAppSchedulables
argument_list|()
argument_list|,
name|getFairShare
argument_list|()
argument_list|)
expr_stmt|;
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
name|runnableAppScheds
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
for|for
control|(
name|AppSchedulable
name|app
range|:
name|nonRunnableAppScheds
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
name|scheduler
operator|.
name|getAllocationConfiguration
argument_list|()
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
name|runnableAppScheds
control|)
block|{
if|if
condition|(
name|Resources
operator|.
name|equals
argument_list|(
name|demand
argument_list|,
name|maxRes
argument_list|)
condition|)
block|{
break|break;
block|}
name|updateDemandForApp
argument_list|(
name|sched
argument_list|,
name|maxRes
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|AppSchedulable
name|sched
range|:
name|nonRunnableAppScheds
control|)
block|{
if|if
condition|(
name|Resources
operator|.
name|equals
argument_list|(
name|demand
argument_list|,
name|maxRes
argument_list|)
condition|)
block|{
break|break;
block|}
name|updateDemandForApp
argument_list|(
name|sched
argument_list|,
name|maxRes
argument_list|)
expr_stmt|;
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
DECL|method|updateDemandForApp (AppSchedulable sched, Resource maxRes)
specifier|private
name|void
name|updateDemandForApp
parameter_list|(
name|AppSchedulable
name|sched
parameter_list|,
name|Resource
name|maxRes
parameter_list|)
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
name|demand
operator|=
name|Resources
operator|.
name|componentwiseMin
argument_list|(
name|demand
argument_list|,
name|maxRes
argument_list|)
expr_stmt|;
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
literal|"Node "
operator|+
name|node
operator|.
name|getNodeName
argument_list|()
operator|+
literal|" offered to queue: "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|assignContainerPreCheck
argument_list|(
name|node
argument_list|)
condition|)
block|{
return|return
name|assigned
return|;
block|}
name|Comparator
argument_list|<
name|Schedulable
argument_list|>
name|comparator
init|=
name|policy
operator|.
name|getComparator
argument_list|()
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|runnableAppScheds
argument_list|,
name|comparator
argument_list|)
expr_stmt|;
for|for
control|(
name|AppSchedulable
name|sched
range|:
name|runnableAppScheds
control|)
block|{
if|if
condition|(
name|SchedulerAppUtils
operator|.
name|isBlacklisted
argument_list|(
name|sched
operator|.
name|getApp
argument_list|()
argument_list|,
name|node
argument_list|,
name|LOG
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|assigned
operator|=
name|sched
operator|.
name|assignContainer
argument_list|(
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|assigned
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
annotation|@
name|Override
DECL|method|getNumRunnableApps ()
specifier|public
name|int
name|getNumRunnableApps
parameter_list|()
block|{
return|return
name|runnableAppScheds
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

