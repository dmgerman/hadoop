begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|server
operator|.
name|resourcemanager
operator|.
name|reservation
operator|.
name|ReservationSchedulerConfiguration
import|;
end_import

begin_comment
comment|/**  *  * Context of the Queues in Scheduler.  *  */
end_comment

begin_interface
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Private
annotation|@
name|Unstable
DECL|interface|SchedulerQueueManager
specifier|public
interface|interface
name|SchedulerQueueManager
parameter_list|<
name|T
extends|extends
name|SchedulerQueue
parameter_list|,
name|E
extends|extends
name|ReservationSchedulerConfiguration
parameter_list|>
block|{
comment|/**    * Get the root queue.    * @return root queue    */
DECL|method|getRootQueue ()
name|T
name|getRootQueue
parameter_list|()
function_decl|;
comment|/**    * Get all the queues.    * @return a map contains all the queues as well as related queue names    */
DECL|method|getQueues ()
name|Map
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|getQueues
parameter_list|()
function_decl|;
comment|/**    * Remove the queue from the existing queue.    * @param queueName the queue name    */
DECL|method|removeQueue (String queueName)
name|void
name|removeQueue
parameter_list|(
name|String
name|queueName
parameter_list|)
function_decl|;
comment|/**    * Add a new queue to the existing queues.    * @param queueName the queue name    * @param queue the queue object    */
DECL|method|addQueue (String queueName, T queue)
name|void
name|addQueue
parameter_list|(
name|String
name|queueName
parameter_list|,
name|T
name|queue
parameter_list|)
function_decl|;
comment|/**    * Get a queue matching the specified queue name.    * @param queueName the queue name    * @return a queue object    */
DECL|method|getQueue (String queueName)
name|T
name|getQueue
parameter_list|(
name|String
name|queueName
parameter_list|)
function_decl|;
comment|/**    * Reinitialize the queues.    * @param newConf the configuration    * @throws IOException if fails to re-initialize queues    */
DECL|method|reinitializeQueues (E newConf)
name|void
name|reinitializeQueues
parameter_list|(
name|E
name|newConf
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

