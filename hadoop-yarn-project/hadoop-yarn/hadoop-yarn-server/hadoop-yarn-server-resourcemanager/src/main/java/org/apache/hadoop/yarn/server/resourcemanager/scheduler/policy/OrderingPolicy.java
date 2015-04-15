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
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|*
import|;
end_import

begin_comment
comment|/**  * OrderingPolicy is used by the scheduler to order SchedulableEntities for  * container assignment and preemption  */
end_comment

begin_interface
DECL|interface|OrderingPolicy
specifier|public
interface|interface
name|OrderingPolicy
parameter_list|<
name|S
extends|extends
name|SchedulableEntity
parameter_list|>
block|{
comment|/*    * Note: OrderingPolicy depends upon external    * synchronization of all use of the SchedulableEntity Collection and    * Iterators for correctness and to avoid concurrent modification issues    */
comment|/**    * Get the collection of SchedulableEntities which are managed by this    * OrderingPolicy - should include processes returned by the Assignment and    * Preemption iterator with no guarantees regarding order    */
DECL|method|getSchedulableEntities ()
specifier|public
name|Collection
argument_list|<
name|S
argument_list|>
name|getSchedulableEntities
parameter_list|()
function_decl|;
comment|/**    * Return an iterator over the collection of SchedulableEntities which orders    * them for container assignment    */
DECL|method|getAssignmentIterator ()
specifier|public
name|Iterator
argument_list|<
name|S
argument_list|>
name|getAssignmentIterator
parameter_list|()
function_decl|;
comment|/**    * Return an iterator over the collection of SchedulableEntities which orders    * them for preemption    */
DECL|method|getPreemptionIterator ()
specifier|public
name|Iterator
argument_list|<
name|S
argument_list|>
name|getPreemptionIterator
parameter_list|()
function_decl|;
comment|/**    * Add a SchedulableEntity to be managed for allocation and preemption     * ordering    */
DECL|method|addSchedulableEntity (S s)
specifier|public
name|void
name|addSchedulableEntity
parameter_list|(
name|S
name|s
parameter_list|)
function_decl|;
comment|/**    * Remove a SchedulableEntity from management for allocation and preemption     * ordering    */
DECL|method|removeSchedulableEntity (S s)
specifier|public
name|boolean
name|removeSchedulableEntity
parameter_list|(
name|S
name|s
parameter_list|)
function_decl|;
comment|/**    * Add a collection of SchedulableEntities to be managed for allocation     * and preemption ordering    */
DECL|method|addAllSchedulableEntities (Collection<S> sc)
specifier|public
name|void
name|addAllSchedulableEntities
parameter_list|(
name|Collection
argument_list|<
name|S
argument_list|>
name|sc
parameter_list|)
function_decl|;
comment|/**    * Get the number of SchedulableEntities managed for allocation and    * preemption ordering    */
DECL|method|getNumSchedulableEntities ()
specifier|public
name|int
name|getNumSchedulableEntities
parameter_list|()
function_decl|;
comment|/**    * Provides configuration information for the policy from the scheduler    * configuration    */
DECL|method|configure (String conf)
specifier|public
name|void
name|configure
parameter_list|(
name|String
name|conf
parameter_list|)
function_decl|;
comment|/**    * The passed SchedulableEntity has been allocated the passed Container,    * take appropriate action (depending on comparator, a reordering of the    * SchedulableEntity may be required)    */
DECL|method|containerAllocated (S schedulableEntity, RMContainer r)
specifier|public
name|void
name|containerAllocated
parameter_list|(
name|S
name|schedulableEntity
parameter_list|,
name|RMContainer
name|r
parameter_list|)
function_decl|;
comment|/**    * The passed SchedulableEntity has released the passed Container,    * take appropriate action (depending on comparator, a reordering of the    * SchedulableEntity may be required)    */
DECL|method|containerReleased (S schedulableEntity, RMContainer r)
specifier|public
name|void
name|containerReleased
parameter_list|(
name|S
name|schedulableEntity
parameter_list|,
name|RMContainer
name|r
parameter_list|)
function_decl|;
comment|/**    * Display information regarding configuration& status    */
DECL|method|getStatusMessage ()
specifier|public
name|String
name|getStatusMessage
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

