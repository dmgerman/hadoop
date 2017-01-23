begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.policy
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
operator|.
name|policy
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
name|capacity
operator|.
name|CSQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_comment
comment|/**  * This will be used by  * {@link org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.ParentQueue}  * to decide allocation ordering of child queues.  */
end_comment

begin_interface
DECL|interface|QueueOrderingPolicy
specifier|public
interface|interface
name|QueueOrderingPolicy
block|{
DECL|method|setQueues (List<CSQueue> queues)
name|void
name|setQueues
parameter_list|(
name|List
argument_list|<
name|CSQueue
argument_list|>
name|queues
parameter_list|)
function_decl|;
comment|/**    * Return an iterator over the collection of CSQueues which orders    * them for container assignment.    *    * Please note that, to avoid queue's set updated during sorting / iterating.    * Caller need to make sure parent queue's read lock is properly acquired.    *    * @param partition nodePartition    *    * @return iterator of queues to allocate    */
DECL|method|getAssignmentIterator (String partition)
name|Iterator
argument_list|<
name|CSQueue
argument_list|>
name|getAssignmentIterator
parameter_list|(
name|String
name|partition
parameter_list|)
function_decl|;
comment|/**    * Returns configuration name (which will be used to set ordering policy    * @return configuration name    */
DECL|method|getConfigName ()
name|String
name|getConfigName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

