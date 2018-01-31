begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.constraint.api
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
name|constraint
operator|.
name|api
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|SchedulingRequest
import|;
end_import

begin_comment
comment|/**  * This class encapsulates the response received from the ResourceScheduler's  * attemptAllocateOnNode method.  */
end_comment

begin_class
DECL|class|SchedulingResponse
specifier|public
class|class
name|SchedulingResponse
block|{
DECL|field|isSuccess
specifier|private
specifier|final
name|boolean
name|isSuccess
decl_stmt|;
DECL|field|applicationId
specifier|private
specifier|final
name|ApplicationId
name|applicationId
decl_stmt|;
DECL|field|schedulingRequest
specifier|private
specifier|final
name|SchedulingRequest
name|schedulingRequest
decl_stmt|;
comment|/**    * Create a SchedulingResponse.    * @param isSuccess did scheduler accept.    * @param applicationId Application Id.    * @param schedulingRequest Scheduling Request.    */
DECL|method|SchedulingResponse (boolean isSuccess, ApplicationId applicationId, SchedulingRequest schedulingRequest)
specifier|public
name|SchedulingResponse
parameter_list|(
name|boolean
name|isSuccess
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|,
name|SchedulingRequest
name|schedulingRequest
parameter_list|)
block|{
name|this
operator|.
name|isSuccess
operator|=
name|isSuccess
expr_stmt|;
name|this
operator|.
name|applicationId
operator|=
name|applicationId
expr_stmt|;
name|this
operator|.
name|schedulingRequest
operator|=
name|schedulingRequest
expr_stmt|;
block|}
comment|/**    * Returns true if Scheduler was able to accept and commit this request.    * @return isSuccessful.    */
DECL|method|isSuccess ()
specifier|public
name|boolean
name|isSuccess
parameter_list|()
block|{
return|return
name|this
operator|.
name|isSuccess
return|;
block|}
comment|/**    * Get Application Id.    * @return Application Id.    */
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
return|return
name|this
operator|.
name|applicationId
return|;
block|}
comment|/**    * Get Scheduling Request.    * @return Scheduling Request.    */
DECL|method|getSchedulingRequest ()
specifier|public
name|SchedulingRequest
name|getSchedulingRequest
parameter_list|()
block|{
return|return
name|this
operator|.
name|schedulingRequest
return|;
block|}
block|}
end_class

end_unit

