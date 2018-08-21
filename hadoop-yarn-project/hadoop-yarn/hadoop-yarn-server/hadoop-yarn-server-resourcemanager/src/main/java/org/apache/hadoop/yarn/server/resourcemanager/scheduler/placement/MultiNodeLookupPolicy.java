begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.placement
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
name|placement
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
name|SchedulerNode
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Set
import|;
end_import

begin_comment
comment|/**  *<p>  * This class has the following functionality.  *  *<p>  * Provide an interface for MultiNodeLookupPolicy so that different placement  * allocator can choose nodes based on need.  *</p>  */
end_comment

begin_interface
DECL|interface|MultiNodeLookupPolicy
specifier|public
interface|interface
name|MultiNodeLookupPolicy
parameter_list|<
name|N
extends|extends
name|SchedulerNode
parameter_list|>
block|{
comment|/**    * Get iterator of preferred node depends on requirement and/or availability.    *    * @param nodes    *          List of Nodes    * @param partition    *          node label    *    * @return iterator of preferred node    */
DECL|method|getPreferredNodeIterator (Collection<N> nodes, String partition)
name|Iterator
argument_list|<
name|N
argument_list|>
name|getPreferredNodeIterator
parameter_list|(
name|Collection
argument_list|<
name|N
argument_list|>
name|nodes
parameter_list|,
name|String
name|partition
parameter_list|)
function_decl|;
comment|/**    * Refresh working nodes set for re-ordering based on the algorithm selected.    *    * @param nodes    *          a collection working nm's.    */
DECL|method|addAndRefreshNodesSet (Collection<N> nodes, String partition)
name|void
name|addAndRefreshNodesSet
parameter_list|(
name|Collection
argument_list|<
name|N
argument_list|>
name|nodes
parameter_list|,
name|String
name|partition
parameter_list|)
function_decl|;
comment|/**    * Get sorted nodes per partition.    *    * @param partition    *          node label    *    * @return collection of sorted nodes    */
DECL|method|getNodesPerPartition (String partition)
name|Set
argument_list|<
name|N
argument_list|>
name|getNodesPerPartition
parameter_list|(
name|String
name|partition
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

