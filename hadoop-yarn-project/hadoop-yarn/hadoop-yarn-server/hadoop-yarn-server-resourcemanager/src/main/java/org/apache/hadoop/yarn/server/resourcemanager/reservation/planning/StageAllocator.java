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
name|ReservationRequest
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
name|PlanningException
import|;
end_import

begin_comment
comment|/**  * Interface for allocating a single stage in IterativePlanner.  */
end_comment

begin_interface
DECL|interface|StageAllocator
specifier|public
interface|interface
name|StageAllocator
block|{
comment|/**    * Computes the allocation of a stage inside a defined time interval.    *    * @param plan the Plan to which the reservation must be fitted    * @param planLoads a 'dirty' read of the plan loads at each time    * @param planModifications the allocations performed by the planning    *          algorithm which are not yet reflected by plan    * @param rr the stage    * @param stageArrival the arrival time (earliest starting time) set for    *          the stage by the two phase planning algorithm    * @param stageDeadline the deadline of the stage set by the two phase    *          planning algorithm    * @param user name of the user    * @param oldId identifier of the old reservation    *    * @return The computed allocation (or null if the stage could not be    *         allocated)    * @throws PlanningException    */
DECL|method|computeStageAllocation (Plan plan, RLESparseResourceAllocation planLoads, RLESparseResourceAllocation planModifications, ReservationRequest rr, long stageArrival, long stageDeadline, String user, ReservationId oldId)
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|computeStageAllocation
parameter_list|(
name|Plan
name|plan
parameter_list|,
name|RLESparseResourceAllocation
name|planLoads
parameter_list|,
name|RLESparseResourceAllocation
name|planModifications
parameter_list|,
name|ReservationRequest
name|rr
parameter_list|,
name|long
name|stageArrival
parameter_list|,
name|long
name|stageDeadline
parameter_list|,
name|String
name|user
parameter_list|,
name|ReservationId
name|oldId
parameter_list|)
throws|throws
name|PlanningException
function_decl|;
block|}
end_interface

end_unit

