begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.scheduler
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
name|scheduler
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
name|Container
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
name|ContainerId
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
name|Priority
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
name|ResourceRequest
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
name|UpdateContainerRequest
import|;
end_import

begin_comment
comment|/**  * Composite key for outstanding scheduler requests for any schedulable entity.  * Currently it includes {@link Priority}.  */
end_comment

begin_class
DECL|class|SchedulerRequestKey
specifier|public
specifier|final
class|class
name|SchedulerRequestKey
implements|implements
name|Comparable
argument_list|<
name|SchedulerRequestKey
argument_list|>
block|{
DECL|field|priority
specifier|private
specifier|final
name|Priority
name|priority
decl_stmt|;
DECL|field|allocationRequestId
specifier|private
specifier|final
name|long
name|allocationRequestId
decl_stmt|;
DECL|field|containerToUpdate
specifier|private
specifier|final
name|ContainerId
name|containerToUpdate
decl_stmt|;
comment|/**    * Factory method to generate a SchedulerRequestKey from a ResourceRequest.    * @param req ResourceRequest    * @return SchedulerRequestKey    */
DECL|method|create (ResourceRequest req)
specifier|public
specifier|static
name|SchedulerRequestKey
name|create
parameter_list|(
name|ResourceRequest
name|req
parameter_list|)
block|{
return|return
operator|new
name|SchedulerRequestKey
argument_list|(
name|req
operator|.
name|getPriority
argument_list|()
argument_list|,
name|req
operator|.
name|getAllocationRequestId
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Factory method to generate a SchedulerRequestKey from a SchedulingRequest.    * @param req SchedulingRequest    * @return SchedulerRequestKey    */
DECL|method|create (SchedulingRequest req)
specifier|public
specifier|static
name|SchedulerRequestKey
name|create
parameter_list|(
name|SchedulingRequest
name|req
parameter_list|)
block|{
return|return
operator|new
name|SchedulerRequestKey
argument_list|(
name|req
operator|.
name|getPriority
argument_list|()
argument_list|,
name|req
operator|.
name|getAllocationRequestId
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|create (UpdateContainerRequest req, SchedulerRequestKey schedulerRequestKey)
specifier|public
specifier|static
name|SchedulerRequestKey
name|create
parameter_list|(
name|UpdateContainerRequest
name|req
parameter_list|,
name|SchedulerRequestKey
name|schedulerRequestKey
parameter_list|)
block|{
return|return
operator|new
name|SchedulerRequestKey
argument_list|(
name|schedulerRequestKey
operator|.
name|getPriority
argument_list|()
argument_list|,
name|schedulerRequestKey
operator|.
name|getAllocationRequestId
argument_list|()
argument_list|,
name|req
operator|.
name|getContainerId
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Convenience method to extract the SchedulerRequestKey used to schedule the    * Container.    * @param container Container    * @return SchedulerRequestKey    */
DECL|method|extractFrom (Container container)
specifier|public
specifier|static
name|SchedulerRequestKey
name|extractFrom
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
return|return
operator|new
name|SchedulerRequestKey
argument_list|(
name|container
operator|.
name|getPriority
argument_list|()
argument_list|,
name|container
operator|.
name|getAllocationRequestId
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|SchedulerRequestKey (Priority priority, long allocationRequestId, ContainerId containerToUpdate)
specifier|public
name|SchedulerRequestKey
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|long
name|allocationRequestId
parameter_list|,
name|ContainerId
name|containerToUpdate
parameter_list|)
block|{
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
name|this
operator|.
name|allocationRequestId
operator|=
name|allocationRequestId
expr_stmt|;
name|this
operator|.
name|containerToUpdate
operator|=
name|containerToUpdate
expr_stmt|;
block|}
comment|/**    * Get the {@link Priority} of the request.    *    * @return the {@link Priority} of the request    */
DECL|method|getPriority ()
specifier|public
name|Priority
name|getPriority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
comment|/**    * Get the Id of the associated {@link ResourceRequest}.    *    * @return the Id of the associated {@link ResourceRequest}    */
DECL|method|getAllocationRequestId ()
specifier|public
name|long
name|getAllocationRequestId
parameter_list|()
block|{
return|return
name|allocationRequestId
return|;
block|}
DECL|method|getContainerToUpdate ()
specifier|public
name|ContainerId
name|getContainerToUpdate
parameter_list|()
block|{
return|return
name|containerToUpdate
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (SchedulerRequestKey o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|SchedulerRequestKey
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
operator|(
name|priority
operator|!=
literal|null
operator|)
condition|?
operator|-
literal|1
else|:
literal|0
return|;
block|}
else|else
block|{
if|if
condition|(
name|priority
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
block|}
comment|// Ensure updates are ranked higher
if|if
condition|(
name|this
operator|.
name|containerToUpdate
operator|==
literal|null
operator|&&
name|o
operator|.
name|containerToUpdate
operator|!=
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|this
operator|.
name|containerToUpdate
operator|!=
literal|null
operator|&&
name|o
operator|.
name|containerToUpdate
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
name|int
name|priorityCompare
init|=
name|o
operator|.
name|getPriority
argument_list|()
operator|.
name|compareTo
argument_list|(
name|priority
argument_list|)
decl_stmt|;
comment|// we first sort by priority and then by allocationRequestId
if|if
condition|(
name|priorityCompare
operator|!=
literal|0
condition|)
block|{
return|return
name|priorityCompare
return|;
block|}
name|int
name|allocReqCompare
init|=
name|Long
operator|.
name|compare
argument_list|(
name|allocationRequestId
argument_list|,
name|o
operator|.
name|getAllocationRequestId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|allocReqCompare
operator|!=
literal|0
condition|)
block|{
return|return
name|allocReqCompare
return|;
block|}
if|if
condition|(
name|this
operator|.
name|containerToUpdate
operator|!=
literal|null
operator|&&
name|o
operator|.
name|containerToUpdate
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
name|this
operator|.
name|containerToUpdate
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|containerToUpdate
argument_list|)
operator|)
return|;
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|SchedulerRequestKey
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SchedulerRequestKey
name|that
init|=
operator|(
name|SchedulerRequestKey
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|getAllocationRequestId
argument_list|()
operator|!=
name|that
operator|.
name|getAllocationRequestId
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|getPriority
argument_list|()
operator|.
name|equals
argument_list|(
name|that
operator|.
name|getPriority
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|containerToUpdate
operator|!=
literal|null
condition|?
name|containerToUpdate
operator|.
name|equals
argument_list|(
name|that
operator|.
name|containerToUpdate
argument_list|)
else|:
name|that
operator|.
name|containerToUpdate
operator|==
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|priority
operator|!=
literal|null
condition|?
name|priority
operator|.
name|hashCode
argument_list|()
else|:
literal|0
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|allocationRequestId
operator|^
operator|(
name|allocationRequestId
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|containerToUpdate
operator|!=
literal|null
condition|?
name|containerToUpdate
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SchedulerRequestKey{"
operator|+
literal|"priority="
operator|+
name|priority
operator|+
literal|", allocationRequestId="
operator|+
name|allocationRequestId
operator|+
literal|", containerToUpdate="
operator|+
name|containerToUpdate
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

