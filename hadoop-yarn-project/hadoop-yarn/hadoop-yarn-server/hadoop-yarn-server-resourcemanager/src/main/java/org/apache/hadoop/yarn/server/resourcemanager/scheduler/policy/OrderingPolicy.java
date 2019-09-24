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
comment|/**  * OrderingPolicy is used by the scheduler to order SchedulableEntities for  * container assignment and preemption.  * @param<S> the type of {@link SchedulableEntity} that will be compared  */
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
comment|/**    * Get the collection of {@link SchedulableEntity} Objects which are managed    * by this OrderingPolicy - should include processes returned by the    * Assignment and Preemption iterator with no guarantees regarding order.    * @return a collection of {@link SchedulableEntity} objects    */
DECL|method|getSchedulableEntities ()
specifier|public
name|Collection
argument_list|<
name|S
argument_list|>
name|getSchedulableEntities
parameter_list|()
function_decl|;
comment|/**    * Return an iterator over the collection of {@link SchedulableEntity}    * objects which orders them for container assignment.    * @param sel the {@link IteratorSelector} to filter with    * @return an iterator over the collection of {@link SchedulableEntity}    * objects    */
DECL|method|getAssignmentIterator (IteratorSelector sel)
name|Iterator
argument_list|<
name|S
argument_list|>
name|getAssignmentIterator
parameter_list|(
name|IteratorSelector
name|sel
parameter_list|)
function_decl|;
comment|/**    * Return an iterator over the collection of {@link SchedulableEntity}    * objects which orders them for preemption.    * @return an iterator over the collection of {@link SchedulableEntity}    */
DECL|method|getPreemptionIterator ()
specifier|public
name|Iterator
argument_list|<
name|S
argument_list|>
name|getPreemptionIterator
parameter_list|()
function_decl|;
comment|/**    * Add a {@link SchedulableEntity} to be managed for allocation and preemption    * ordering.    * @param s the {@link SchedulableEntity} to add    */
DECL|method|addSchedulableEntity (S s)
specifier|public
name|void
name|addSchedulableEntity
parameter_list|(
name|S
name|s
parameter_list|)
function_decl|;
comment|/**    * Remove a {@link SchedulableEntity} from management for allocation and    * preemption ordering.    * @param s the {@link SchedulableEntity} to remove    * @return whether the {@link SchedulableEntity} was present before this    * operation    */
DECL|method|removeSchedulableEntity (S s)
specifier|public
name|boolean
name|removeSchedulableEntity
parameter_list|(
name|S
name|s
parameter_list|)
function_decl|;
comment|/**    * Add a collection of {@link SchedulableEntity} objects to be managed for    * allocation and preemption ordering.    * @param sc the collection of {@link SchedulableEntity} objects to add    */
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
comment|/**    * Get the number of {@link SchedulableEntity} objects managed for allocation    * and preemption ordering.    * @return the number of {@link SchedulableEntity} objects    */
DECL|method|getNumSchedulableEntities ()
specifier|public
name|int
name|getNumSchedulableEntities
parameter_list|()
function_decl|;
comment|/**    * Provides configuration information for the policy from the scheduler    * configuration.    * @param conf a map of scheduler configuration properties and values    */
DECL|method|configure (Map<String, String> conf)
specifier|public
name|void
name|configure
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|conf
parameter_list|)
function_decl|;
comment|/**    * Notify the {@code OrderingPolicy} that the {@link SchedulableEntity}    * has been allocated the given {@link RMContainer}, enabling the    * {@code OrderingPolicy} to take appropriate action. Depending on the    * comparator, a reordering of the {@link SchedulableEntity} may be required.    * @param schedulableEntity the {@link SchedulableEntity}    * @param r the allocated {@link RMContainer}    */
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
comment|/**    * Notify the {@code OrderingPolicy} that the {@link SchedulableEntity}    * has released the given {@link RMContainer}, enabling the    * {@code OrderingPolicy} to take appropriate action. Depending on the    * comparator, a reordering of the {@link SchedulableEntity} may be required.    * @param schedulableEntity the {@link SchedulableEntity}    * @param r the released {@link RMContainer}    */
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
comment|/**    * Notify the {@code OrderingPolicy} that the demand for the    * {@link SchedulableEntity} has been updated, enabling the    * {@code OrderingPolicy} to reorder the {@link SchedulableEntity} if needed.    * @param schedulableEntity the updated {@link SchedulableEntity}    */
DECL|method|demandUpdated (S schedulableEntity)
name|void
name|demandUpdated
parameter_list|(
name|S
name|schedulableEntity
parameter_list|)
function_decl|;
comment|/**    * Return information regarding configuration and status.    * @return configuration and status information    */
DECL|method|getInfo ()
specifier|public
name|String
name|getInfo
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

