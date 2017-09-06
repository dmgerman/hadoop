begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.reservation.planning
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
operator|.
name|planning
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|conf
operator|.
name|Configuration
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
name|ReservationDefinition
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
name|InMemoryReservationAllocation
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
name|Plan
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
name|RLESparseResourceAllocation
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
name|ReservationAllocation
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
name|ReservationInterval
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
name|ContractValidationException
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

begin_comment
comment|/**  * An abstract class that follows the general behavior of planning algorithms.  */
end_comment

begin_class
DECL|class|PlanningAlgorithm
specifier|public
specifier|abstract
class|class
name|PlanningAlgorithm
implements|implements
name|ReservationAgent
block|{
comment|/**    * Performs the actual allocation for a ReservationDefinition within a Plan.    *    * @param reservationId the identifier of the reservation    * @param user the user who owns the reservation    * @param plan the Plan to which the reservation must be fitted    * @param contract encapsulates the resources required by the user for his    *          session    * @param oldReservation the existing reservation (null if none)    * @return whether the allocateUser function was successful or not    *    * @throws PlanningException if the session cannot be fitted into the plan    * @throws ContractValidationException if validation fails    */
DECL|method|allocateUser (ReservationId reservationId, String user, Plan plan, ReservationDefinition contract, ReservationAllocation oldReservation)
specifier|protected
name|boolean
name|allocateUser
parameter_list|(
name|ReservationId
name|reservationId
parameter_list|,
name|String
name|user
parameter_list|,
name|Plan
name|plan
parameter_list|,
name|ReservationDefinition
name|contract
parameter_list|,
name|ReservationAllocation
name|oldReservation
parameter_list|)
throws|throws
name|PlanningException
throws|,
name|ContractValidationException
block|{
comment|// Adjust the ResourceDefinition to account for system "imperfections"
comment|// (e.g., scheduling delays for large containers).
name|ReservationDefinition
name|adjustedContract
init|=
name|adjustContract
argument_list|(
name|plan
argument_list|,
name|contract
argument_list|)
decl_stmt|;
comment|// Compute the job allocation
name|RLESparseResourceAllocation
name|allocation
init|=
name|computeJobAllocation
argument_list|(
name|plan
argument_list|,
name|reservationId
argument_list|,
name|adjustedContract
argument_list|,
name|user
argument_list|)
decl_stmt|;
comment|// If no job allocation was found, fail
if|if
condition|(
name|allocation
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PlanningException
argument_list|(
literal|"The planning algorithm could not find a valid allocation"
operator|+
literal|" for your request"
argument_list|)
throw|;
block|}
comment|// Translate the allocation to a map (with zero paddings)
name|long
name|step
init|=
name|plan
operator|.
name|getStep
argument_list|()
decl_stmt|;
name|long
name|jobArrival
init|=
name|stepRoundUp
argument_list|(
name|adjustedContract
operator|.
name|getArrival
argument_list|()
argument_list|,
name|step
argument_list|)
decl_stmt|;
name|long
name|jobDeadline
init|=
name|stepRoundUp
argument_list|(
name|adjustedContract
operator|.
name|getDeadline
argument_list|()
argument_list|,
name|step
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|mapAllocations
init|=
name|allocationsToPaddedMap
argument_list|(
name|allocation
argument_list|,
name|jobArrival
argument_list|,
name|jobDeadline
argument_list|)
decl_stmt|;
comment|// Create the reservation
name|ReservationAllocation
name|capReservation
init|=
operator|new
name|InMemoryReservationAllocation
argument_list|(
name|reservationId
argument_list|,
comment|// ID
name|adjustedContract
argument_list|,
comment|// Contract
name|user
argument_list|,
comment|// User name
name|plan
operator|.
name|getQueueName
argument_list|()
argument_list|,
comment|// Queue name
name|findEarliestTime
argument_list|(
name|mapAllocations
argument_list|)
argument_list|,
comment|// Earliest start time
name|findLatestTime
argument_list|(
name|mapAllocations
argument_list|)
argument_list|,
comment|// Latest end time
name|mapAllocations
argument_list|,
comment|// Allocations
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
comment|// Resource calculator
name|plan
operator|.
name|getMinimumAllocation
argument_list|()
argument_list|)
decl_stmt|;
comment|// Minimum allocation
comment|// Add (or update) the reservation allocation
if|if
condition|(
name|oldReservation
operator|!=
literal|null
condition|)
block|{
return|return
name|plan
operator|.
name|updateReservation
argument_list|(
name|capReservation
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|plan
operator|.
name|addReservation
argument_list|(
name|capReservation
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
specifier|private
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
DECL|method|allocationsToPaddedMap (RLESparseResourceAllocation allocation, long jobArrival, long jobDeadline)
name|allocationsToPaddedMap
parameter_list|(
name|RLESparseResourceAllocation
name|allocation
parameter_list|,
name|long
name|jobArrival
parameter_list|,
name|long
name|jobDeadline
parameter_list|)
block|{
comment|// Allocate
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|mapAllocations
init|=
name|allocation
operator|.
name|toIntervalMap
argument_list|()
decl_stmt|;
comment|// Zero allocation
name|Resource
name|zeroResource
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
comment|// Pad at the beginning
name|long
name|earliestStart
init|=
name|findEarliestTime
argument_list|(
name|mapAllocations
argument_list|)
decl_stmt|;
if|if
condition|(
name|jobArrival
operator|<
name|earliestStart
condition|)
block|{
name|mapAllocations
operator|.
name|put
argument_list|(
operator|new
name|ReservationInterval
argument_list|(
name|jobArrival
argument_list|,
name|earliestStart
argument_list|)
argument_list|,
name|zeroResource
argument_list|)
expr_stmt|;
block|}
comment|// Pad at the beginning
name|long
name|latestEnd
init|=
name|findLatestTime
argument_list|(
name|mapAllocations
argument_list|)
decl_stmt|;
if|if
condition|(
name|latestEnd
operator|<
name|jobDeadline
condition|)
block|{
name|mapAllocations
operator|.
name|put
argument_list|(
operator|new
name|ReservationInterval
argument_list|(
name|latestEnd
argument_list|,
name|jobDeadline
argument_list|)
argument_list|,
name|zeroResource
argument_list|)
expr_stmt|;
block|}
return|return
name|mapAllocations
return|;
block|}
DECL|method|computeJobAllocation (Plan plan, ReservationId reservationId, ReservationDefinition reservation, String user)
specifier|public
specifier|abstract
name|RLESparseResourceAllocation
name|computeJobAllocation
parameter_list|(
name|Plan
name|plan
parameter_list|,
name|ReservationId
name|reservationId
parameter_list|,
name|ReservationDefinition
name|reservation
parameter_list|,
name|String
name|user
parameter_list|)
throws|throws
name|PlanningException
throws|,
name|ContractValidationException
function_decl|;
annotation|@
name|Override
DECL|method|createReservation (ReservationId reservationId, String user, Plan plan, ReservationDefinition contract)
specifier|public
name|boolean
name|createReservation
parameter_list|(
name|ReservationId
name|reservationId
parameter_list|,
name|String
name|user
parameter_list|,
name|Plan
name|plan
parameter_list|,
name|ReservationDefinition
name|contract
parameter_list|)
throws|throws
name|PlanningException
block|{
comment|// Allocate
return|return
name|allocateUser
argument_list|(
name|reservationId
argument_list|,
name|user
argument_list|,
name|plan
argument_list|,
name|contract
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|updateReservation (ReservationId reservationId, String user, Plan plan, ReservationDefinition contract)
specifier|public
name|boolean
name|updateReservation
parameter_list|(
name|ReservationId
name|reservationId
parameter_list|,
name|String
name|user
parameter_list|,
name|Plan
name|plan
parameter_list|,
name|ReservationDefinition
name|contract
parameter_list|)
throws|throws
name|PlanningException
block|{
comment|// Get the old allocation
name|ReservationAllocation
name|oldAlloc
init|=
name|plan
operator|.
name|getReservationById
argument_list|(
name|reservationId
argument_list|)
decl_stmt|;
comment|// Allocate (ignores the old allocation)
return|return
name|allocateUser
argument_list|(
name|reservationId
argument_list|,
name|user
argument_list|,
name|plan
argument_list|,
name|contract
argument_list|,
name|oldAlloc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|deleteReservation (ReservationId reservationId, String user, Plan plan)
specifier|public
name|boolean
name|deleteReservation
parameter_list|(
name|ReservationId
name|reservationId
parameter_list|,
name|String
name|user
parameter_list|,
name|Plan
name|plan
parameter_list|)
throws|throws
name|PlanningException
block|{
comment|// Delete the existing reservation
return|return
name|plan
operator|.
name|deleteReservation
argument_list|(
name|reservationId
argument_list|)
return|;
block|}
DECL|method|findEarliestTime ( Map<ReservationInterval, Resource> sesInt)
specifier|protected
specifier|static
name|long
name|findEarliestTime
parameter_list|(
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|sesInt
parameter_list|)
block|{
name|long
name|ret
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|s
range|:
name|sesInt
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|s
operator|.
name|getKey
argument_list|()
operator|.
name|getStartTime
argument_list|()
operator|<
name|ret
operator|&&
name|s
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ret
operator|=
name|s
operator|.
name|getKey
argument_list|()
operator|.
name|getStartTime
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
DECL|method|findLatestTime (Map<ReservationInterval, Resource> sesInt)
specifier|protected
specifier|static
name|long
name|findLatestTime
parameter_list|(
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|sesInt
parameter_list|)
block|{
name|long
name|ret
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|s
range|:
name|sesInt
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|s
operator|.
name|getKey
argument_list|()
operator|.
name|getEndTime
argument_list|()
operator|>
name|ret
operator|&&
name|s
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ret
operator|=
name|s
operator|.
name|getKey
argument_list|()
operator|.
name|getEndTime
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
DECL|method|stepRoundDown (long t, long step)
specifier|protected
specifier|static
name|long
name|stepRoundDown
parameter_list|(
name|long
name|t
parameter_list|,
name|long
name|step
parameter_list|)
block|{
return|return
operator|(
name|t
operator|/
name|step
operator|)
operator|*
name|step
return|;
block|}
DECL|method|stepRoundUp (long t, long step)
specifier|protected
specifier|static
name|long
name|stepRoundUp
parameter_list|(
name|long
name|t
parameter_list|,
name|long
name|step
parameter_list|)
block|{
return|return
operator|(
operator|(
name|t
operator|+
name|step
operator|-
literal|1
operator|)
operator|/
name|step
operator|)
operator|*
name|step
return|;
block|}
DECL|method|adjustContract (Plan plan, ReservationDefinition originalContract)
specifier|private
name|ReservationDefinition
name|adjustContract
parameter_list|(
name|Plan
name|plan
parameter_list|,
name|ReservationDefinition
name|originalContract
parameter_list|)
block|{
comment|// Place here adjustment. For example using QueueMetrics we can track
comment|// large container delays per YARN-YARN-1990
return|return
name|originalContract
return|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{   }
block|}
end_class

end_unit

