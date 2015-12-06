begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.reservation
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
name|reservation
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|LimitedPrivate
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ReservationId
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
name|reservation
operator|.
name|exceptions
operator|.
name|MismatchedUserException
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
name|reservation
operator|.
name|exceptions
operator|.
name|PlanningException
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
name|reservation
operator|.
name|exceptions
operator|.
name|ResourceOverCommitException
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

begin_comment
comment|/**  * This policy enforce a simple physical cluster capacity constraints, by  * validating that the allocation proposed fits in the current plan. This  * validation is compatible with "updates" and in verifying the capacity  * constraints it conceptually remove the prior version of the reservation.  */
end_comment

begin_class
annotation|@
name|LimitedPrivate
argument_list|(
literal|"yarn"
argument_list|)
annotation|@
name|Unstable
DECL|class|NoOverCommitPolicy
specifier|public
class|class
name|NoOverCommitPolicy
implements|implements
name|SharingPolicy
block|{
annotation|@
name|Override
DECL|method|validate (Plan plan, ReservationAllocation reservation)
specifier|public
name|void
name|validate
parameter_list|(
name|Plan
name|plan
parameter_list|,
name|ReservationAllocation
name|reservation
parameter_list|)
throws|throws
name|PlanningException
block|{
name|ReservationAllocation
name|oldReservation
init|=
name|plan
operator|.
name|getReservationById
argument_list|(
name|reservation
operator|.
name|getReservationId
argument_list|()
argument_list|)
decl_stmt|;
comment|// check updates are using same name
if|if
condition|(
name|oldReservation
operator|!=
literal|null
operator|&&
operator|!
name|oldReservation
operator|.
name|getUser
argument_list|()
operator|.
name|equals
argument_list|(
name|reservation
operator|.
name|getUser
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|MismatchedUserException
argument_list|(
literal|"Updating an existing reservation with mismatching user:"
operator|+
name|oldReservation
operator|.
name|getUser
argument_list|()
operator|+
literal|" != "
operator|+
name|reservation
operator|.
name|getUser
argument_list|()
argument_list|)
throw|;
block|}
name|long
name|startTime
init|=
name|reservation
operator|.
name|getStartTime
argument_list|()
decl_stmt|;
name|long
name|endTime
init|=
name|reservation
operator|.
name|getEndTime
argument_list|()
decl_stmt|;
name|long
name|step
init|=
name|plan
operator|.
name|getStep
argument_list|()
decl_stmt|;
comment|// for every instant in time, check we are respecting cluster capacity
for|for
control|(
name|long
name|t
init|=
name|startTime
init|;
name|t
operator|<
name|endTime
condition|;
name|t
operator|+=
name|step
control|)
block|{
name|Resource
name|currExistingAllocTot
init|=
name|plan
operator|.
name|getTotalCommittedResources
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|Resource
name|currNewAlloc
init|=
name|reservation
operator|.
name|getResourcesAtTime
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|Resource
name|currOldAlloc
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldReservation
operator|!=
literal|null
condition|)
block|{
name|oldReservation
operator|.
name|getResourcesAtTime
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
comment|// check the cluster is never over committed
comment|// currExistingAllocTot + currNewAlloc - currOldAlloc>
comment|// capPlan.getTotalCapacity()
if|if
condition|(
name|Resources
operator|.
name|greaterThan
argument_list|(
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
name|plan
operator|.
name|getTotalCapacity
argument_list|()
argument_list|,
name|Resources
operator|.
name|subtract
argument_list|(
name|Resources
operator|.
name|add
argument_list|(
name|currExistingAllocTot
argument_list|,
name|currNewAlloc
argument_list|)
argument_list|,
name|currOldAlloc
argument_list|)
argument_list|,
name|plan
operator|.
name|getTotalCapacity
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceOverCommitException
argument_list|(
literal|"Resources at time "
operator|+
name|t
operator|+
literal|" would be overcommitted by "
operator|+
literal|"accepting reservation: "
operator|+
name|reservation
operator|.
name|getReservationId
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getValidWindow ()
specifier|public
name|long
name|getValidWindow
parameter_list|()
block|{
comment|// this policy has no "memory" so the valid window is set to zero
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|init (String planQueuePath, ReservationSchedulerConfiguration conf)
specifier|public
name|void
name|init
parameter_list|(
name|String
name|planQueuePath
parameter_list|,
name|ReservationSchedulerConfiguration
name|conf
parameter_list|)
block|{
comment|// nothing to do for this policy
block|}
annotation|@
name|Override
DECL|method|availableResources ( RLESparseResourceAllocation available, Plan plan, String user, ReservationId oldId, long start, long end)
specifier|public
name|RLESparseResourceAllocation
name|availableResources
parameter_list|(
name|RLESparseResourceAllocation
name|available
parameter_list|,
name|Plan
name|plan
parameter_list|,
name|String
name|user
parameter_list|,
name|ReservationId
name|oldId
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|)
throws|throws
name|PlanningException
block|{
return|return
name|available
return|;
block|}
block|}
end_class

end_unit

