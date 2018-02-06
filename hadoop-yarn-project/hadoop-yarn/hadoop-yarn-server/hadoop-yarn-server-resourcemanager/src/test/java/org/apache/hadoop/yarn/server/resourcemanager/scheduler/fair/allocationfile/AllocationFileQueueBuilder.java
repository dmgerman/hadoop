begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.allocationfile
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
operator|.
name|allocationfile
package|;
end_package

begin_comment
comment|/**  * Abstract base class for building simple queues and subqueues for testcases.  * Currently there are two concrete types subclassed from this class:  * {@link AllocationFileSimpleQueueBuilder} and  * {@link AllocationFileSubQueueBuilder}.  * The intention of having this class to group the common properties of  * simple queues and subqueues by methods delegating calls to a  * queuePropertiesBuilder instance.  */
end_comment

begin_class
DECL|class|AllocationFileQueueBuilder
specifier|public
specifier|abstract
class|class
name|AllocationFileQueueBuilder
block|{
DECL|field|queuePropertiesBuilder
specifier|final
name|AllocationFileQueueProperties
operator|.
name|Builder
name|queuePropertiesBuilder
decl_stmt|;
DECL|method|AllocationFileQueueBuilder ()
name|AllocationFileQueueBuilder
parameter_list|()
block|{
name|this
operator|.
name|queuePropertiesBuilder
operator|=
name|AllocationFileQueueProperties
operator|.
name|Builder
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
DECL|method|parent (boolean parent)
specifier|public
name|AllocationFileQueueBuilder
name|parent
parameter_list|(
name|boolean
name|parent
parameter_list|)
block|{
name|this
operator|.
name|queuePropertiesBuilder
operator|.
name|parent
argument_list|(
name|parent
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|minResources (String value)
specifier|public
name|AllocationFileQueueBuilder
name|minResources
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|queuePropertiesBuilder
operator|.
name|minResources
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|maxResources (String value)
specifier|public
name|AllocationFileQueueBuilder
name|maxResources
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|queuePropertiesBuilder
operator|.
name|maxResources
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|aclAdministerApps (String value)
specifier|public
name|AllocationFileQueueBuilder
name|aclAdministerApps
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|queuePropertiesBuilder
operator|.
name|aclAdministerApps
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|aclSubmitApps (String value)
specifier|public
name|AllocationFileQueueBuilder
name|aclSubmitApps
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|queuePropertiesBuilder
operator|.
name|aclSubmitApps
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|schedulingPolicy (String value)
specifier|public
name|AllocationFileQueueBuilder
name|schedulingPolicy
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|queuePropertiesBuilder
operator|.
name|schedulingPolicy
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|maxRunningApps (int value)
specifier|public
name|AllocationFileQueueBuilder
name|maxRunningApps
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|queuePropertiesBuilder
operator|.
name|maxRunningApps
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|maxAMShare (double value)
specifier|public
name|AllocationFileQueueBuilder
name|maxAMShare
parameter_list|(
name|double
name|value
parameter_list|)
block|{
name|this
operator|.
name|queuePropertiesBuilder
operator|.
name|maxAMShare
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|minSharePreemptionTimeout (int value)
specifier|public
name|AllocationFileQueueBuilder
name|minSharePreemptionTimeout
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|queuePropertiesBuilder
operator|.
name|minSharePreemptionTimeout
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|maxChildResources (String value)
specifier|public
name|AllocationFileQueueBuilder
name|maxChildResources
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|queuePropertiesBuilder
operator|.
name|maxChildResources
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|fairSharePreemptionTimeout (Integer value)
specifier|public
name|AllocationFileQueueBuilder
name|fairSharePreemptionTimeout
parameter_list|(
name|Integer
name|value
parameter_list|)
block|{
name|this
operator|.
name|queuePropertiesBuilder
operator|.
name|fairSharePreemptionTimeout
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|fairSharePreemptionThreshold ( double value)
specifier|public
name|AllocationFileQueueBuilder
name|fairSharePreemptionThreshold
parameter_list|(
name|double
name|value
parameter_list|)
block|{
name|this
operator|.
name|queuePropertiesBuilder
operator|.
name|fairSharePreemptionThreshold
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|subQueue (String queueName)
specifier|public
name|AllocationFileQueueBuilder
name|subQueue
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
if|if
condition|(
name|this
operator|instanceof
name|AllocationFileSimpleQueueBuilder
condition|)
block|{
return|return
operator|new
name|AllocationFileSubQueueBuilder
argument_list|(
operator|(
name|AllocationFileSimpleQueueBuilder
operator|)
name|this
argument_list|,
name|queueName
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"subQueue can only be invoked on instances of "
operator|+
name|AllocationFileSimpleQueueBuilder
operator|.
name|class
argument_list|)
throw|;
block|}
block|}
DECL|method|buildQueue ()
specifier|public
specifier|abstract
name|AllocationFileWriter
name|buildQueue
parameter_list|()
function_decl|;
DECL|method|buildSubQueue ()
specifier|public
specifier|abstract
name|AllocationFileSimpleQueueBuilder
name|buildSubQueue
parameter_list|()
function_decl|;
DECL|method|getqueuePropertiesBuilder ()
name|AllocationFileQueueProperties
operator|.
name|Builder
name|getqueuePropertiesBuilder
parameter_list|()
block|{
return|return
name|queuePropertiesBuilder
return|;
block|}
block|}
end_class

end_unit

