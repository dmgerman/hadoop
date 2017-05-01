begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*******************************************************************************  *   Licensed to the Apache Software Foundation (ASF) under one  *   or more contributor license agreements.  See the NOTICE file  *   distributed with this work for additional information  *   regarding copyright ownership.  The ASF licenses this file  *   to you under the Apache License, Version 2.0 (the  *   "License"); you may not use this file except in compliance  *   with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *   Unless required by applicable law or agreed to in writing, software  *   distributed under the License is distributed on an "AS IS" BASIS,  *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *   See the License for the specific language governing permissions and  *   limitations under the License.  *******************************************************************************/
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
name|exceptions
operator|.
name|PlanningException
import|;
end_import

begin_comment
comment|/**  * An entity that seeks to acquire resources to satisfy an user's contract  */
end_comment

begin_interface
DECL|interface|ReservationAgent
specifier|public
interface|interface
name|ReservationAgent
block|{
comment|/**    * Constant defining the preferential treatment of time for equally valid    * allocations.    */
DECL|field|FAVOR_EARLY_ALLOCATION
specifier|final
specifier|static
name|String
name|FAVOR_EARLY_ALLOCATION
init|=
literal|"yarn.resourcemanager.reservation-system.favor-early-allocation"
decl_stmt|;
comment|/**    * By default favor early allocations.    */
DECL|field|DEFAULT_GREEDY_FAVOR_EARLY_ALLOCATION
specifier|final
specifier|static
name|boolean
name|DEFAULT_GREEDY_FAVOR_EARLY_ALLOCATION
init|=
literal|true
decl_stmt|;
comment|/**    * Create a reservation for the user that abides by the specified contract    *    * @param reservationId the identifier of the reservation to be created.    * @param user the user who wants to create the reservation    * @param plan the Plan to which the reservation must be fitted    * @param contract encapsulates the resources the user requires for his    *          session    *    * @return whether the create operation was successful or not    * @throws PlanningException if the session cannot be fitted into the plan    */
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
function_decl|;
comment|/**    * Update a reservation for the user that abides by the specified contract    *    * @param reservationId the identifier of the reservation to be updated    * @param user the user who wants to create the session    * @param plan the Plan to which the reservation must be fitted    * @param contract encapsulates the resources the user requires for his    *          reservation    *    * @return whether the update operation was successful or not    * @throws PlanningException if the reservation cannot be fitted into the plan    */
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
function_decl|;
comment|/**    * Delete an user reservation    *    * @param reservationId the identifier of the reservation to be deleted    * @param user the user who wants to create the reservation    * @param plan the Plan to which the session must be fitted    *    * @return whether the delete operation was successful or not    * @throws PlanningException if the reservation cannot be fitted into the plan    */
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
function_decl|;
comment|/**    * Init configuration.    *    * @param conf Configuration    */
DECL|method|init (Configuration conf)
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

