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
name|api
operator|.
name|records
operator|.
name|ResourceRequest
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
name|NodeType
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
name|scheduler
operator|.
name|SchedulerRequestKey
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
comment|/**  *<p>  * In addition to {@link PlacementSet}, this also maintains  * pending ResourceRequests:  * - When new ResourceRequest(s) added to scheduler, or,  * - Or new container allocated, scheduler can notify corresponding  * PlacementSet.  *</p>  *  *<p>  * Different set of resource requests (E.g., resource requests with the  * same schedulerKey) can have one instance of PlacementSet, each PlacementSet  * can have different ways to order nodes depends on requests.  *</p>  */
end_comment

begin_interface
DECL|interface|SchedulingPlacementSet
specifier|public
interface|interface
name|SchedulingPlacementSet
parameter_list|<
name|N
extends|extends
name|SchedulerNode
parameter_list|>
extends|extends
name|PlacementSet
argument_list|<
name|N
argument_list|>
block|{
comment|/**    * Get iterator of preferred node depends on requirement and/or availability    * @param clusterPlacementSet input cluster PlacementSet    * @return iterator of preferred node    */
DECL|method|getPreferredNodeIterator (PlacementSet<N> clusterPlacementSet)
name|Iterator
argument_list|<
name|N
argument_list|>
name|getPreferredNodeIterator
parameter_list|(
name|PlacementSet
argument_list|<
name|N
argument_list|>
name|clusterPlacementSet
parameter_list|)
function_decl|;
comment|/**    * Replace existing ResourceRequest by the new requests    *    * @param requests new ResourceRequests    * @param recoverPreemptedRequestForAContainer if we're recovering resource    * requests for preempted container    * @return true if total pending resource changed    */
DECL|method|updateResourceRequests ( List<ResourceRequest> requests, boolean recoverPreemptedRequestForAContainer)
name|ResourceRequestUpdateResult
name|updateResourceRequests
parameter_list|(
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|requests
parameter_list|,
name|boolean
name|recoverPreemptedRequestForAContainer
parameter_list|)
function_decl|;
comment|/**    * Get pending ResourceRequests by given schedulerRequestKey    * @return Map of resourceName to ResourceRequest    */
DECL|method|getResourceRequests ()
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|getResourceRequests
parameter_list|()
function_decl|;
comment|/**    * Get ResourceRequest by given schedulerKey and resourceName    * @param resourceName resourceName    * @param schedulerRequestKey schedulerRequestKey    * @return ResourceRequest    */
DECL|method|getResourceRequest (String resourceName, SchedulerRequestKey schedulerRequestKey)
name|ResourceRequest
name|getResourceRequest
parameter_list|(
name|String
name|resourceName
parameter_list|,
name|SchedulerRequestKey
name|schedulerRequestKey
parameter_list|)
function_decl|;
comment|/**    * Notify container allocated.    * @param type Type of the allocation    * @param node Which node this container allocated on    * @param request resource request    * @return list of ResourceRequests deducted    */
DECL|method|allocate (NodeType type, SchedulerNode node, ResourceRequest request)
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|allocate
parameter_list|(
name|NodeType
name|type
parameter_list|,
name|SchedulerNode
name|node
parameter_list|,
name|ResourceRequest
name|request
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

