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

begin_comment
comment|/**  * An auxiliary class used to compute the time interval in which the stage can  * be allocated resources by {@link IterativePlanner}.  */
end_comment

begin_interface
DECL|interface|StageExecutionInterval
specifier|public
interface|interface
name|StageExecutionInterval
block|{
comment|/**    * Computes the earliest allowed starting time for a given stage.    *    * @param plan the Plan to which the reservation must be fitted    * @param reservation the job contract    * @param currentReservationStage the stage    * @param allocateLeft is the job allocated from left to right    * @param allocations Existing resource assignments for the job    * @return the time interval in which the stage can get resources.    */
DECL|method|computeExecutionInterval (Plan plan, ReservationDefinition reservation, ReservationRequest currentReservationStage, boolean allocateLeft, RLESparseResourceAllocation allocations)
name|ReservationInterval
name|computeExecutionInterval
parameter_list|(
name|Plan
name|plan
parameter_list|,
name|ReservationDefinition
name|reservation
parameter_list|,
name|ReservationRequest
name|currentReservationStage
parameter_list|,
name|boolean
name|allocateLeft
parameter_list|,
name|RLESparseResourceAllocation
name|allocations
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

