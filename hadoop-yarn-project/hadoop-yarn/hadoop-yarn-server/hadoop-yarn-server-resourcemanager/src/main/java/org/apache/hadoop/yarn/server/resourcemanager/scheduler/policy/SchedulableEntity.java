begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.policy
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
name|api
operator|.
name|records
operator|.
name|Priority
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
name|ResourceUsage
import|;
end_import

begin_comment
comment|/**  * A SchedulableEntity is a process to be scheduled.  * for example, an application / application attempt  */
end_comment

begin_interface
DECL|interface|SchedulableEntity
specifier|public
interface|interface
name|SchedulableEntity
block|{
comment|/**    * Id - each entity must have a unique id    */
DECL|method|getId ()
specifier|public
name|String
name|getId
parameter_list|()
function_decl|;
comment|/**    * Compare the passed SchedulableEntity to this one for input order.    * Input order is implementation defined and should reflect the     * correct ordering for first-in first-out processing    */
DECL|method|compareInputOrderTo (SchedulableEntity other)
specifier|public
name|int
name|compareInputOrderTo
parameter_list|(
name|SchedulableEntity
name|other
parameter_list|)
function_decl|;
comment|/**    * View of Resources wanted and consumed by the entity    */
DECL|method|getSchedulingResourceUsage ()
specifier|public
name|ResourceUsage
name|getSchedulingResourceUsage
parameter_list|()
function_decl|;
comment|/**    * Get the priority of the application    */
DECL|method|getPriority ()
specifier|public
name|Priority
name|getPriority
parameter_list|()
function_decl|;
comment|/**    * Whether application was running before RM restart.    */
DECL|method|isRecovering ()
specifier|public
name|boolean
name|isRecovering
parameter_list|()
function_decl|;
comment|/**    * Get partition corresponding to this entity.    * @return partition    */
DECL|method|getPartition ()
name|String
name|getPartition
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

