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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|QueueState
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

begin_comment
comment|/**  *  * Represents a queue in Scheduler.  *  */
end_comment

begin_interface
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|LimitedPrivate
argument_list|(
literal|"yarn"
argument_list|)
DECL|interface|SchedulerQueue
specifier|public
interface|interface
name|SchedulerQueue
parameter_list|<
name|T
extends|extends
name|SchedulerQueue
parameter_list|>
extends|extends
name|Queue
block|{
comment|/**    * Get list of child queues.    * @return a list of child queues    */
DECL|method|getChildQueues ()
name|List
argument_list|<
name|T
argument_list|>
name|getChildQueues
parameter_list|()
function_decl|;
comment|/**    * Get the parent queue.    * @return the parent queue    */
DECL|method|getParent ()
name|T
name|getParent
parameter_list|()
function_decl|;
comment|/**    * Get current queue state.    * @return the queue state    */
DECL|method|getState ()
name|QueueState
name|getState
parameter_list|()
function_decl|;
comment|/**    * Update the queue state.    * @param state the queue state    */
DECL|method|updateQueueState (QueueState state)
name|void
name|updateQueueState
parameter_list|(
name|QueueState
name|state
parameter_list|)
function_decl|;
comment|/**    * Stop the queue.    */
DECL|method|stopQueue ()
name|void
name|stopQueue
parameter_list|()
function_decl|;
comment|/**    * Active the queue.    * @throws YarnException if the queue can not be activated.    */
DECL|method|activeQueue ()
name|void
name|activeQueue
parameter_list|()
throws|throws
name|YarnException
function_decl|;
block|}
end_interface

end_unit

