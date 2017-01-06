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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|capacity
operator|.
name|SchedulingMode
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
name|common
operator|.
name|PendingAsk
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
comment|/**  *<p>  * Comparing to {@link PlacementSet}, this also maintains  * pending ResourceRequests:  * - When new ResourceRequest(s) added to scheduler, or,  * - Or new container allocated, scheduler can notify corresponding  * PlacementSet.  *</p>  *  *<p>  * Different set of resource requests (E.g., resource requests with the  * same schedulerKey) can have one instance of PlacementSet, each PlacementSet  * can have different ways to order nodes depends on requests.  *</p>  */
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
DECL|method|updateResourceRequests ( Collection<ResourceRequest> requests, boolean recoverPreemptedRequestForAContainer)
name|ResourceRequestUpdateResult
name|updateResourceRequests
parameter_list|(
name|Collection
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
comment|/**    * Get pending ask for given resourceName. If there's no such pendingAsk,    * returns {@link PendingAsk#ZERO}    *    * @param resourceName resourceName    * @return PendingAsk    */
DECL|method|getPendingAsk (String resourceName)
name|PendingAsk
name|getPendingAsk
parameter_list|(
name|String
name|resourceName
parameter_list|)
function_decl|;
comment|/**    * Get #pending-allocations for given resourceName. If there's no such    * pendingAsk, returns 0    *    * @param resourceName resourceName    * @return #pending-allocations    */
DECL|method|getOutstandingAsksCount (String resourceName)
name|int
name|getOutstandingAsksCount
parameter_list|(
name|String
name|resourceName
parameter_list|)
function_decl|;
comment|/**    * Notify container allocated.    * @param schedulerKey SchedulerRequestKey for this ResourceRequest    * @param type Type of the allocation    * @param node Which node this container allocated on    * @return list of ResourceRequests deducted    */
DECL|method|allocate (SchedulerRequestKey schedulerKey, NodeType type, SchedulerNode node)
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|allocate
parameter_list|(
name|SchedulerRequestKey
name|schedulerKey
parameter_list|,
name|NodeType
name|type
parameter_list|,
name|SchedulerNode
name|node
parameter_list|)
function_decl|;
comment|/**    * Returns list of accepted resourceNames.    * @return Iterator of accepted resourceNames    */
DECL|method|getAcceptedResouceNames ()
name|Iterator
argument_list|<
name|String
argument_list|>
name|getAcceptedResouceNames
parameter_list|()
function_decl|;
comment|/**    * We can still have pending requirement for a given NodeType and node    * @param type Locality Type    * @param node which node we will allocate on    * @return true if we has pending requirement    */
DECL|method|canAllocate (NodeType type, SchedulerNode node)
name|boolean
name|canAllocate
parameter_list|(
name|NodeType
name|type
parameter_list|,
name|SchedulerNode
name|node
parameter_list|)
function_decl|;
comment|/**    * Can delay to give locality?    * TODO (wangda): This should be moved out of SchedulingPlacementSet    * and should belong to specific delay scheduling policy impl.    *    * @param resourceName resourceName    * @return can/cannot    */
DECL|method|canDelayTo (String resourceName)
name|boolean
name|canDelayTo
parameter_list|(
name|String
name|resourceName
parameter_list|)
function_decl|;
comment|/**    * Does this {@link SchedulingPlacementSet} accept resources on nodePartition?    *    * @param nodePartition nodePartition    * @param schedulingMode schedulingMode    * @return accepted/not    */
DECL|method|acceptNodePartition (String nodePartition, SchedulingMode schedulingMode)
name|boolean
name|acceptNodePartition
parameter_list|(
name|String
name|nodePartition
parameter_list|,
name|SchedulingMode
name|schedulingMode
parameter_list|)
function_decl|;
comment|/**    * It is possible that one request can accept multiple node partition,    * So this method returns primary node partition for pending resource /    * headroom calculation.    *    * @return primary requested node partition    */
DECL|method|getPrimaryRequestedNodePartition ()
name|String
name|getPrimaryRequestedNodePartition
parameter_list|()
function_decl|;
comment|/**    * @return number of unique location asks with #pending greater than 0,    * (like /rack1, host1, etc.).    *    * TODO (wangda): This should be moved out of SchedulingPlacementSet    * and should belong to specific delay scheduling policy impl.    */
DECL|method|getUniqueLocationAsks ()
name|int
name|getUniqueLocationAsks
parameter_list|()
function_decl|;
comment|/**    * Print human-readable requests to LOG debug.    */
DECL|method|showRequests ()
name|void
name|showRequests
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

