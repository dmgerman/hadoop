begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.dao
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
name|webapp
operator|.
name|dao
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
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlTransient
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
name|nodelabels
operator|.
name|RMNodeLabelsManager
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
name|QueueResourceQuotas
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
name|ResourceUsage
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
name|capacity
operator|.
name|AutoCreatedLeafQueue
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
name|capacity
operator|.
name|LeafQueue
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
name|capacity
operator|.
name|QueueCapacities
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
name|capacity
operator|.
name|UserInfo
import|;
end_import

begin_class
annotation|@
name|XmlRootElement
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|CapacitySchedulerLeafQueueInfo
specifier|public
class|class
name|CapacitySchedulerLeafQueueInfo
extends|extends
name|CapacitySchedulerQueueInfo
block|{
DECL|field|numActiveApplications
specifier|protected
name|int
name|numActiveApplications
decl_stmt|;
DECL|field|numPendingApplications
specifier|protected
name|int
name|numPendingApplications
decl_stmt|;
DECL|field|numContainers
specifier|protected
name|int
name|numContainers
decl_stmt|;
DECL|field|maxApplications
specifier|protected
name|int
name|maxApplications
decl_stmt|;
DECL|field|maxApplicationsPerUser
specifier|protected
name|int
name|maxApplicationsPerUser
decl_stmt|;
DECL|field|userLimit
specifier|protected
name|int
name|userLimit
decl_stmt|;
DECL|field|users
specifier|protected
name|UsersInfo
name|users
decl_stmt|;
comment|// To add another level in the XML
DECL|field|userLimitFactor
specifier|protected
name|float
name|userLimitFactor
decl_stmt|;
DECL|field|AMResourceLimit
specifier|protected
name|ResourceInfo
name|AMResourceLimit
decl_stmt|;
DECL|field|usedAMResource
specifier|protected
name|ResourceInfo
name|usedAMResource
decl_stmt|;
DECL|field|userAMResourceLimit
specifier|protected
name|ResourceInfo
name|userAMResourceLimit
decl_stmt|;
DECL|field|preemptionDisabled
specifier|protected
name|boolean
name|preemptionDisabled
decl_stmt|;
DECL|field|intraQueuePreemptionDisabled
specifier|protected
name|boolean
name|intraQueuePreemptionDisabled
decl_stmt|;
DECL|field|defaultNodeLabelExpression
specifier|protected
name|String
name|defaultNodeLabelExpression
decl_stmt|;
DECL|field|defaultPriority
specifier|protected
name|int
name|defaultPriority
decl_stmt|;
DECL|field|isAutoCreatedLeafQueue
specifier|protected
name|boolean
name|isAutoCreatedLeafQueue
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|orderingPolicyInfo
specifier|protected
name|String
name|orderingPolicyInfo
decl_stmt|;
DECL|method|CapacitySchedulerLeafQueueInfo ()
name|CapacitySchedulerLeafQueueInfo
parameter_list|()
block|{   }
empty_stmt|;
DECL|method|CapacitySchedulerLeafQueueInfo (LeafQueue q)
name|CapacitySchedulerLeafQueueInfo
parameter_list|(
name|LeafQueue
name|q
parameter_list|)
block|{
name|super
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|numActiveApplications
operator|=
name|q
operator|.
name|getNumActiveApplications
argument_list|()
expr_stmt|;
name|numPendingApplications
operator|=
name|q
operator|.
name|getNumPendingApplications
argument_list|()
expr_stmt|;
name|numContainers
operator|=
name|q
operator|.
name|getNumContainers
argument_list|()
expr_stmt|;
name|maxApplications
operator|=
name|q
operator|.
name|getMaxApplications
argument_list|()
expr_stmt|;
name|maxApplicationsPerUser
operator|=
name|q
operator|.
name|getMaxApplicationsPerUser
argument_list|()
expr_stmt|;
name|userLimit
operator|=
name|q
operator|.
name|getUserLimit
argument_list|()
expr_stmt|;
name|users
operator|=
operator|new
name|UsersInfo
argument_list|(
name|q
operator|.
name|getUsersManager
argument_list|()
operator|.
name|getUsersInfo
argument_list|()
argument_list|)
expr_stmt|;
name|userLimitFactor
operator|=
name|q
operator|.
name|getUserLimitFactor
argument_list|()
expr_stmt|;
name|AMResourceLimit
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|q
operator|.
name|getAMResourceLimit
argument_list|()
argument_list|)
expr_stmt|;
name|usedAMResource
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|q
operator|.
name|getQueueResourceUsage
argument_list|()
operator|.
name|getAMUsed
argument_list|()
argument_list|)
expr_stmt|;
name|preemptionDisabled
operator|=
name|q
operator|.
name|getPreemptionDisabled
argument_list|()
expr_stmt|;
name|intraQueuePreemptionDisabled
operator|=
name|q
operator|.
name|getIntraQueuePreemptionDisabled
argument_list|()
expr_stmt|;
name|orderingPolicyInfo
operator|=
name|q
operator|.
name|getOrderingPolicy
argument_list|()
operator|.
name|getInfo
argument_list|()
expr_stmt|;
name|defaultNodeLabelExpression
operator|=
name|q
operator|.
name|getDefaultNodeLabelExpression
argument_list|()
expr_stmt|;
name|defaultPriority
operator|=
name|q
operator|.
name|getDefaultApplicationPriority
argument_list|()
operator|.
name|getPriority
argument_list|()
expr_stmt|;
name|ArrayList
argument_list|<
name|UserInfo
argument_list|>
name|usersList
init|=
name|users
operator|.
name|getUsersList
argument_list|()
decl_stmt|;
if|if
condition|(
name|usersList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// If no users are present, consider AM Limit for that queue.
name|userAMResourceLimit
operator|=
name|resources
operator|.
name|getPartitionResourceUsageInfo
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
operator|.
name|getAMLimit
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|userAMResourceLimit
operator|=
name|usersList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResourceUsageInfo
argument_list|()
operator|.
name|getPartitionResourceUsageInfo
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
operator|.
name|getAMLimit
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|q
operator|instanceof
name|AutoCreatedLeafQueue
condition|)
block|{
name|isAutoCreatedLeafQueue
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|populateQueueResourceUsage (ResourceUsage queueResourceUsage)
specifier|protected
name|void
name|populateQueueResourceUsage
parameter_list|(
name|ResourceUsage
name|queueResourceUsage
parameter_list|)
block|{
name|resources
operator|=
operator|new
name|ResourcesInfo
argument_list|(
name|queueResourceUsage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|populateQueueCapacities (QueueCapacities qCapacities, QueueResourceQuotas qResQuotas)
specifier|protected
name|void
name|populateQueueCapacities
parameter_list|(
name|QueueCapacities
name|qCapacities
parameter_list|,
name|QueueResourceQuotas
name|qResQuotas
parameter_list|)
block|{
name|capacities
operator|=
operator|new
name|QueueCapacitiesInfo
argument_list|(
name|qCapacities
argument_list|,
name|qResQuotas
argument_list|)
expr_stmt|;
block|}
DECL|method|getNumActiveApplications ()
specifier|public
name|int
name|getNumActiveApplications
parameter_list|()
block|{
return|return
name|numActiveApplications
return|;
block|}
DECL|method|getNumPendingApplications ()
specifier|public
name|int
name|getNumPendingApplications
parameter_list|()
block|{
return|return
name|numPendingApplications
return|;
block|}
DECL|method|getNumContainers ()
specifier|public
name|int
name|getNumContainers
parameter_list|()
block|{
return|return
name|numContainers
return|;
block|}
DECL|method|getMaxApplications ()
specifier|public
name|int
name|getMaxApplications
parameter_list|()
block|{
return|return
name|maxApplications
return|;
block|}
DECL|method|getMaxApplicationsPerUser ()
specifier|public
name|int
name|getMaxApplicationsPerUser
parameter_list|()
block|{
return|return
name|maxApplicationsPerUser
return|;
block|}
DECL|method|getUserLimit ()
specifier|public
name|int
name|getUserLimit
parameter_list|()
block|{
return|return
name|userLimit
return|;
block|}
comment|//Placing here because of JERSEY-1199
DECL|method|getUsers ()
specifier|public
name|UsersInfo
name|getUsers
parameter_list|()
block|{
return|return
name|users
return|;
block|}
DECL|method|getUserLimitFactor ()
specifier|public
name|float
name|getUserLimitFactor
parameter_list|()
block|{
return|return
name|userLimitFactor
return|;
block|}
DECL|method|getAMResourceLimit ()
specifier|public
name|ResourceInfo
name|getAMResourceLimit
parameter_list|()
block|{
return|return
name|AMResourceLimit
return|;
block|}
DECL|method|getUsedAMResource ()
specifier|public
name|ResourceInfo
name|getUsedAMResource
parameter_list|()
block|{
return|return
name|usedAMResource
return|;
block|}
DECL|method|getUserAMResourceLimit ()
specifier|public
name|ResourceInfo
name|getUserAMResourceLimit
parameter_list|()
block|{
return|return
name|userAMResourceLimit
return|;
block|}
DECL|method|getPreemptionDisabled ()
specifier|public
name|boolean
name|getPreemptionDisabled
parameter_list|()
block|{
return|return
name|preemptionDisabled
return|;
block|}
DECL|method|getIntraQueuePreemptionDisabled ()
specifier|public
name|boolean
name|getIntraQueuePreemptionDisabled
parameter_list|()
block|{
return|return
name|intraQueuePreemptionDisabled
return|;
block|}
DECL|method|getOrderingPolicyInfo ()
specifier|public
name|String
name|getOrderingPolicyInfo
parameter_list|()
block|{
return|return
name|orderingPolicyInfo
return|;
block|}
DECL|method|getDefaultNodeLabelExpression ()
specifier|public
name|String
name|getDefaultNodeLabelExpression
parameter_list|()
block|{
return|return
name|defaultNodeLabelExpression
return|;
block|}
DECL|method|getDefaultApplicationPriority ()
specifier|public
name|int
name|getDefaultApplicationPriority
parameter_list|()
block|{
return|return
name|defaultPriority
return|;
block|}
DECL|method|isAutoCreatedLeafQueue ()
specifier|public
name|boolean
name|isAutoCreatedLeafQueue
parameter_list|()
block|{
return|return
name|isAutoCreatedLeafQueue
return|;
block|}
block|}
end_class

end_unit

