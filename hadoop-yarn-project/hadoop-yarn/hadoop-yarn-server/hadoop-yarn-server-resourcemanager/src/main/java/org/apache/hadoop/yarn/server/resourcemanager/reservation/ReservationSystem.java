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
name|exceptions
operator|.
name|YarnException
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
name|RMContext
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
name|recovery
operator|.
name|Recoverable
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
name|Queue
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
name|ResourceScheduler
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
name|security
operator|.
name|ReservationsACLsManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * This interface is the one implemented by any system that wants to support  * Reservations i.e. make {@code Resource} allocations in future. Implementors  * need to bootstrap all configured {@link Plan}s in the active  * {@link ResourceScheduler} along with their corresponding  * {@code ReservationAgent} and {@link SharingPolicy}. It is also responsible  * for managing the {@link PlanFollower} to ensure the {@link Plan}s are in sync  * with the {@link ResourceScheduler}.  */
end_comment

begin_interface
annotation|@
name|LimitedPrivate
argument_list|(
literal|"yarn"
argument_list|)
annotation|@
name|Unstable
DECL|interface|ReservationSystem
specifier|public
interface|interface
name|ReservationSystem
extends|extends
name|Recoverable
block|{
comment|/**    * Set RMContext for {@link ReservationSystem}. This method should be called    * immediately after instantiating a reservation system once.    *     * @param rmContext created by {@code ResourceManager}    */
DECL|method|setRMContext (RMContext rmContext)
name|void
name|setRMContext
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
function_decl|;
comment|/**    * Re-initialize the {@link ReservationSystem}.    *     * @param conf configuration    * @param rmContext current context of the {@code ResourceManager}    * @throws YarnException    */
DECL|method|reinitialize (Configuration conf, RMContext rmContext)
name|void
name|reinitialize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Get an existing {@link Plan} that has been initialized.    *     * @param planName the name of the {@link Plan}    * @return the {@link Plan} identified by name    *     */
DECL|method|getPlan (String planName)
name|Plan
name|getPlan
parameter_list|(
name|String
name|planName
parameter_list|)
function_decl|;
comment|/**    * Return a map containing all the plans known to this ReservationSystem    * (useful for UI)    *     * @return a Map of Plan names and Plan objects    */
DECL|method|getAllPlans ()
name|Map
argument_list|<
name|String
argument_list|,
name|Plan
argument_list|>
name|getAllPlans
parameter_list|()
function_decl|;
comment|/**    * Invokes {@link PlanFollower} to synchronize the specified {@link Plan} with    * the {@link ResourceScheduler}    *     * @param planName the name of the {@link Plan} to be synchronized    * @param shouldReplan replan on reduction of plan capacity if true or    *          proportionally scale down reservations if false    */
DECL|method|synchronizePlan (String planName, boolean shouldReplan)
name|void
name|synchronizePlan
parameter_list|(
name|String
name|planName
parameter_list|,
name|boolean
name|shouldReplan
parameter_list|)
function_decl|;
comment|/**    * Return the time step (ms) at which the {@link PlanFollower} is invoked    *     * @return the time step (ms) at which the {@link PlanFollower} is invoked    */
DECL|method|getPlanFollowerTimeStep ()
name|long
name|getPlanFollowerTimeStep
parameter_list|()
function_decl|;
comment|/**    * Get a new unique {@link ReservationId}.    *     * @return a new unique {@link ReservationId}    *     */
DECL|method|getNewReservationId ()
name|ReservationId
name|getNewReservationId
parameter_list|()
function_decl|;
comment|/**    * Get the {@link Queue} that an existing {@link ReservationId} is associated    * with.    *     * @param reservationId the unique id of the reservation    * @return the name of the associated Queue    *     */
DECL|method|getQueueForReservation (ReservationId reservationId)
name|String
name|getQueueForReservation
parameter_list|(
name|ReservationId
name|reservationId
parameter_list|)
function_decl|;
comment|/**    * Set the {@link Queue} that an existing {@link ReservationId} should be    * associated with.    *     * @param reservationId the unique id of the reservation    * @param queueName the name of Queue to associate the reservation with    *     */
DECL|method|setQueueForReservation (ReservationId reservationId, String queueName)
name|void
name|setQueueForReservation
parameter_list|(
name|ReservationId
name|reservationId
parameter_list|,
name|String
name|queueName
parameter_list|)
function_decl|;
comment|/**    * Get the {@link ReservationsACLsManager} to use to check for the reservation    * access on a user.    *    * @return the reservation ACL manager to use to check reservation ACLs.    *    */
DECL|method|getReservationsACLsManager ()
name|ReservationsACLsManager
name|getReservationsACLsManager
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

