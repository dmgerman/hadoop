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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Evolving
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
name|security
operator|.
name|UserGroupInformation
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
name|QueueACL
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
name|QueueInfo
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
name|QueueUserACLInfo
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
name|rmcontainer
operator|.
name|RMContainer
import|;
end_import

begin_interface
annotation|@
name|Evolving
annotation|@
name|LimitedPrivate
argument_list|(
literal|"yarn"
argument_list|)
DECL|interface|Queue
specifier|public
interface|interface
name|Queue
block|{
comment|/**    * Get the queue name    * @return queue name    */
DECL|method|getQueueName ()
name|String
name|getQueueName
parameter_list|()
function_decl|;
comment|/**    * Get the queue metrics    * @return the queue metrics    */
DECL|method|getMetrics ()
name|QueueMetrics
name|getMetrics
parameter_list|()
function_decl|;
comment|/**    * Get queue information    * @param includeChildQueues include child queues?    * @param recursive recursively get child queue information?    * @return queue information    */
DECL|method|getQueueInfo (boolean includeChildQueues, boolean recursive)
name|QueueInfo
name|getQueueInfo
parameter_list|(
name|boolean
name|includeChildQueues
parameter_list|,
name|boolean
name|recursive
parameter_list|)
function_decl|;
comment|/**    * Get queue ACLs for given<code>user</code>.    * @param user username    * @return queue ACLs for user    */
DECL|method|getQueueUserAclInfo (UserGroupInformation user)
name|List
argument_list|<
name|QueueUserACLInfo
argument_list|>
name|getQueueUserAclInfo
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|)
function_decl|;
DECL|method|hasAccess (QueueACL acl, UserGroupInformation user)
name|boolean
name|hasAccess
parameter_list|(
name|QueueACL
name|acl
parameter_list|,
name|UserGroupInformation
name|user
parameter_list|)
function_decl|;
DECL|method|getActiveUsersManager ()
specifier|public
name|ActiveUsersManager
name|getActiveUsersManager
parameter_list|()
function_decl|;
comment|/**    * Recover the state of the queue for a given container.    * @param clusterResource the resource of the cluster    * @param schedulerAttempt the application for which the container was allocated    * @param rmContainer the container that was recovered.    */
DECL|method|recoverContainer (Resource clusterResource, SchedulerApplicationAttempt schedulerAttempt, RMContainer rmContainer)
specifier|public
name|void
name|recoverContainer
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|SchedulerApplicationAttempt
name|schedulerAttempt
parameter_list|,
name|RMContainer
name|rmContainer
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

