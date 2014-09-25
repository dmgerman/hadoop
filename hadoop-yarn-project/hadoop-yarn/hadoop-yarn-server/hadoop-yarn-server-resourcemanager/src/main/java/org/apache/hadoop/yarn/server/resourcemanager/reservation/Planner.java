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
name|java
operator|.
name|util
operator|.
name|List
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

begin_interface
DECL|interface|Planner
specifier|public
interface|interface
name|Planner
block|{
comment|/**    * Update the existing {@link Plan}, by adding/removing/updating existing    * reservations, and adding a subset of the reservation requests in the    * contracts parameter.    *    * @param plan the {@link Plan} to replan    * @param contracts the list of reservation requests    * @throws PlanningException    */
DECL|method|plan (Plan plan, List<ReservationDefinition> contracts)
specifier|public
name|void
name|plan
parameter_list|(
name|Plan
name|plan
parameter_list|,
name|List
argument_list|<
name|ReservationDefinition
argument_list|>
name|contracts
parameter_list|)
throws|throws
name|PlanningException
function_decl|;
comment|/**    * Initialize the replanner    *    * @param planQueueName the name of the queue for this plan    * @param conf the scheduler configuration    */
DECL|method|init (String planQueueName, Configuration conf)
name|void
name|init
parameter_list|(
name|String
name|planQueueName
parameter_list|,
name|Configuration
name|conf
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

