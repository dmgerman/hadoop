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
name|classification
operator|.
name|InterfaceAudience
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
name|NodeId
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
name|SchedulerNode
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

begin_comment
comment|/**  * A group of nodes which can be allocated by scheduler.  *  * It will have following part:  *  * 1) A map of nodes which can be schedule-able.  * 2) Version of the node set, version should be updated if any node added or  *    removed from the node set. This will be used by  *    {@link AppPlacementAllocator} or other class to check if it's required to  *    invalidate local caches, etc.  * 3) Node partition of the candidate set.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|CandidateNodeSet
specifier|public
interface|interface
name|CandidateNodeSet
parameter_list|<
name|N
extends|extends
name|SchedulerNode
parameter_list|>
block|{
comment|/**    * Get all nodes for this CandidateNodeSet    * @return all nodes for this CandidateNodeSet    */
DECL|method|getAllNodes ()
name|Map
argument_list|<
name|NodeId
argument_list|,
name|N
argument_list|>
name|getAllNodes
parameter_list|()
function_decl|;
comment|/**    * Version of the CandidateNodeSet, can help {@link AppPlacementAllocator} to    * decide if update is required    * @return version    */
DECL|method|getVersion ()
name|long
name|getVersion
parameter_list|()
function_decl|;
comment|/**    * Node partition of the node set.    * @return node partition    */
DECL|method|getPartition ()
name|String
name|getPartition
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

