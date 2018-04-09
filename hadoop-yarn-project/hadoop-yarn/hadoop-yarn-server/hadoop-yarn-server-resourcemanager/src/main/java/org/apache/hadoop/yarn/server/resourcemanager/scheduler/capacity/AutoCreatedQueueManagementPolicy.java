begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity
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
name|capacity
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|SchedulerDynamicEditException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_interface
DECL|interface|AutoCreatedQueueManagementPolicy
specifier|public
interface|interface
name|AutoCreatedQueueManagementPolicy
block|{
comment|/**    * Initialize policy    * @param schedulerContext Capacity Scheduler context    */
DECL|method|init (CapacitySchedulerContext schedulerContext, ParentQueue parentQueue)
name|void
name|init
parameter_list|(
name|CapacitySchedulerContext
name|schedulerContext
parameter_list|,
name|ParentQueue
name|parentQueue
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Reinitialize policy state ( if required )    * @param schedulerContext Capacity Scheduler context    */
DECL|method|reinitialize (CapacitySchedulerContext schedulerContext, ParentQueue parentQueue)
name|void
name|reinitialize
parameter_list|(
name|CapacitySchedulerContext
name|schedulerContext
parameter_list|,
name|ParentQueue
name|parentQueue
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get initial template for the specified leaf queue    * @param leafQueue the leaf queue    * @return initial leaf queue template configurations and capacities for    * auto created queue    */
DECL|method|getInitialLeafQueueConfiguration ( AbstractAutoCreatedLeafQueue leafQueue)
name|AutoCreatedLeafQueueConfig
name|getInitialLeafQueueConfiguration
parameter_list|(
name|AbstractAutoCreatedLeafQueue
name|leafQueue
parameter_list|)
throws|throws
name|SchedulerDynamicEditException
function_decl|;
comment|/**    * Compute/Adjust child queue capacities    * for auto created leaf queues    * This computes queue entitlements but does not update LeafQueueState or    * queue capacities. Scheduler calls commitQueueManagemetChanges after    * validation after applying queue changes and commits to LeafQueueState    * are done in commitQueueManagementChanges.    *    * @return returns a list of suggested QueueEntitlementChange(s) which may    * or may not be be enforced by the scheduler    */
DECL|method|computeQueueManagementChanges ()
name|List
argument_list|<
name|QueueManagementChange
argument_list|>
name|computeQueueManagementChanges
parameter_list|()
throws|throws
name|SchedulerDynamicEditException
function_decl|;
comment|/**    * Commit/Update state for the specified queue management changes.    */
DECL|method|commitQueueManagementChanges ( List<QueueManagementChange> queueManagementChanges)
name|void
name|commitQueueManagementChanges
parameter_list|(
name|List
argument_list|<
name|QueueManagementChange
argument_list|>
name|queueManagementChanges
parameter_list|)
throws|throws
name|SchedulerDynamicEditException
function_decl|;
block|}
end_interface

end_unit

