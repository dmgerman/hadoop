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
name|XmlSeeAlso
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
name|scheduler
operator|.
name|capacity
operator|.
name|CSQueue
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
annotation|@
name|XmlSeeAlso
argument_list|(
block|{
name|CapacitySchedulerLeafQueueInfo
operator|.
name|class
block|}
argument_list|)
DECL|class|CapacitySchedulerQueueInfo
specifier|public
class|class
name|CapacitySchedulerQueueInfo
block|{
annotation|@
name|XmlTransient
DECL|field|EPSILON
specifier|static
specifier|final
name|float
name|EPSILON
init|=
literal|1e-8f
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|queuePath
specifier|protected
name|String
name|queuePath
decl_stmt|;
DECL|field|capacity
specifier|protected
name|float
name|capacity
decl_stmt|;
DECL|field|usedCapacity
specifier|protected
name|float
name|usedCapacity
decl_stmt|;
DECL|field|maxCapacity
specifier|protected
name|float
name|maxCapacity
decl_stmt|;
DECL|field|absoluteCapacity
specifier|protected
name|float
name|absoluteCapacity
decl_stmt|;
DECL|field|absoluteMaxCapacity
specifier|protected
name|float
name|absoluteMaxCapacity
decl_stmt|;
DECL|field|utilization
specifier|protected
name|float
name|utilization
decl_stmt|;
DECL|field|numApplications
specifier|protected
name|int
name|numApplications
decl_stmt|;
DECL|field|usedResources
specifier|protected
name|String
name|usedResources
decl_stmt|;
DECL|field|queueName
specifier|protected
name|String
name|queueName
decl_stmt|;
DECL|field|state
specifier|protected
name|String
name|state
decl_stmt|;
DECL|field|subQueues
specifier|protected
name|ArrayList
argument_list|<
name|CapacitySchedulerQueueInfo
argument_list|>
name|subQueues
decl_stmt|;
DECL|method|CapacitySchedulerQueueInfo ()
name|CapacitySchedulerQueueInfo
parameter_list|()
block|{   }
empty_stmt|;
DECL|method|CapacitySchedulerQueueInfo (CSQueue q)
name|CapacitySchedulerQueueInfo
parameter_list|(
name|CSQueue
name|q
parameter_list|)
block|{
name|queuePath
operator|=
name|q
operator|.
name|getQueuePath
argument_list|()
expr_stmt|;
name|capacity
operator|=
name|q
operator|.
name|getCapacity
argument_list|()
operator|*
literal|100
expr_stmt|;
name|usedCapacity
operator|=
name|q
operator|.
name|getUsedCapacity
argument_list|()
operator|*
literal|100
expr_stmt|;
name|maxCapacity
operator|=
name|q
operator|.
name|getMaximumCapacity
argument_list|()
expr_stmt|;
if|if
condition|(
name|maxCapacity
argument_list|<
name|EPSILON
operator|||
name|maxCapacity
argument_list|>
literal|1f
condition|)
name|maxCapacity
operator|=
literal|1f
expr_stmt|;
name|maxCapacity
operator|*=
literal|100
expr_stmt|;
name|absoluteCapacity
operator|=
name|cap
argument_list|(
name|q
operator|.
name|getAbsoluteCapacity
argument_list|()
argument_list|,
literal|0f
argument_list|,
literal|1f
argument_list|)
operator|*
literal|100
expr_stmt|;
name|absoluteMaxCapacity
operator|=
name|cap
argument_list|(
name|q
operator|.
name|getAbsoluteMaximumCapacity
argument_list|()
argument_list|,
literal|0f
argument_list|,
literal|1f
argument_list|)
operator|*
literal|100
expr_stmt|;
name|utilization
operator|=
name|q
operator|.
name|getUtilization
argument_list|()
operator|*
literal|100
expr_stmt|;
name|numApplications
operator|=
name|q
operator|.
name|getNumApplications
argument_list|()
expr_stmt|;
name|usedResources
operator|=
name|q
operator|.
name|getUsedResources
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|queueName
operator|=
name|q
operator|.
name|getQueueName
argument_list|()
expr_stmt|;
name|state
operator|=
name|q
operator|.
name|getState
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
DECL|method|getCapacity ()
specifier|public
name|float
name|getCapacity
parameter_list|()
block|{
return|return
name|this
operator|.
name|capacity
return|;
block|}
DECL|method|getUsedCapacity ()
specifier|public
name|float
name|getUsedCapacity
parameter_list|()
block|{
return|return
name|this
operator|.
name|usedCapacity
return|;
block|}
DECL|method|getMaxCapacity ()
specifier|public
name|float
name|getMaxCapacity
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxCapacity
return|;
block|}
DECL|method|getAbsoluteCapacity ()
specifier|public
name|float
name|getAbsoluteCapacity
parameter_list|()
block|{
return|return
name|absoluteCapacity
return|;
block|}
DECL|method|getAbsoluteMaxCapacity ()
specifier|public
name|float
name|getAbsoluteMaxCapacity
parameter_list|()
block|{
return|return
name|absoluteMaxCapacity
return|;
block|}
DECL|method|getUtilization ()
specifier|public
name|float
name|getUtilization
parameter_list|()
block|{
return|return
name|utilization
return|;
block|}
DECL|method|getNumApplications ()
specifier|public
name|int
name|getNumApplications
parameter_list|()
block|{
return|return
name|numApplications
return|;
block|}
DECL|method|getUsedResources ()
specifier|public
name|String
name|getUsedResources
parameter_list|()
block|{
return|return
name|usedResources
return|;
block|}
DECL|method|getQueueName ()
specifier|public
name|String
name|getQueueName
parameter_list|()
block|{
return|return
name|this
operator|.
name|queueName
return|;
block|}
DECL|method|getQueueState ()
specifier|public
name|String
name|getQueueState
parameter_list|()
block|{
return|return
name|this
operator|.
name|state
return|;
block|}
DECL|method|getQueuePath ()
specifier|public
name|String
name|getQueuePath
parameter_list|()
block|{
return|return
name|this
operator|.
name|queuePath
return|;
block|}
DECL|method|getSubQueues ()
specifier|public
name|ArrayList
argument_list|<
name|CapacitySchedulerQueueInfo
argument_list|>
name|getSubQueues
parameter_list|()
block|{
return|return
name|this
operator|.
name|subQueues
return|;
block|}
comment|/**    * Limit a value to a specified range.    * @param val the value to be capped    * @param low the lower bound of the range (inclusive)    * @param hi the upper bound of the range (inclusive)    * @return the capped value    */
DECL|method|cap (float val, float low, float hi)
specifier|static
name|float
name|cap
parameter_list|(
name|float
name|val
parameter_list|,
name|float
name|low
parameter_list|,
name|float
name|hi
parameter_list|)
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|val
argument_list|,
name|low
argument_list|)
argument_list|,
name|hi
argument_list|)
return|;
block|}
block|}
end_class

end_unit

