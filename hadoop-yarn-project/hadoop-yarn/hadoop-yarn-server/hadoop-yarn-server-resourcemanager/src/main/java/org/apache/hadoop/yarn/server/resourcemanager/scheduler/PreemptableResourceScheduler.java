begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler
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
name|ApplicationAttemptId
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
name|rmcontainer
operator|.
name|RMContainer
import|;
end_import

begin_comment
comment|/**  * Interface for a scheduler that supports preemption/killing  *  */
end_comment

begin_interface
DECL|interface|PreemptableResourceScheduler
specifier|public
interface|interface
name|PreemptableResourceScheduler
extends|extends
name|ResourceScheduler
block|{
comment|/**    * If the scheduler support container reservations, this method is used to    * ask the scheduler to drop the reservation for the given container.    * @param container Reference to reserved container allocation.    */
DECL|method|killReservedContainer (RMContainer container)
name|void
name|killReservedContainer
parameter_list|(
name|RMContainer
name|container
parameter_list|)
function_decl|;
comment|/**    * Ask the scheduler to obtain back the container from a specific application    * by issuing a preemption request    * @param aid the application from which we want to get a container back    * @param container the container we want back    */
DECL|method|markContainerForPreemption (ApplicationAttemptId aid, RMContainer container)
name|void
name|markContainerForPreemption
parameter_list|(
name|ApplicationAttemptId
name|aid
parameter_list|,
name|RMContainer
name|container
parameter_list|)
function_decl|;
comment|/**    * Ask the scheduler to forcibly interrupt the container given as input    * @param container    */
DECL|method|markContainerForKillable (RMContainer container)
name|void
name|markContainerForKillable
parameter_list|(
name|RMContainer
name|container
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

