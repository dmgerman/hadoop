begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.constraint.api
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
name|constraint
operator|.
name|api
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
name|SchedulingRequest
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
name|ArrayList
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
comment|/**  * Class to encapsulate a Placed scheduling Request.  * It has the original Scheduling Request and a list of SchedulerNodes (one  * for each 'numAllocation' field in the corresponding ResourceSizing object.  *  * NOTE: Clients of this class SHOULD NOT rely on the value of  *       resourceSizing.numAllocations and instead should use the  *       size of collection returned by getNodes() instead.  */
end_comment

begin_class
DECL|class|PlacedSchedulingRequest
specifier|public
class|class
name|PlacedSchedulingRequest
block|{
comment|// The number of times the Algorithm tried to place the SchedulingRequest
comment|// after it was rejected by the commit phase of the Scheduler (due to some
comment|// transient state of the cluster. For eg: no space on Node / user limit etc.)
comment|// The Algorithm can then try to probably place on a different node.
DECL|field|placementAttempt
specifier|private
name|int
name|placementAttempt
init|=
literal|0
decl_stmt|;
DECL|field|request
specifier|private
specifier|final
name|SchedulingRequest
name|request
decl_stmt|;
comment|// One Node per numContainers in the SchedulingRequest;
DECL|field|nodes
specifier|private
specifier|final
name|List
argument_list|<
name|SchedulerNode
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|PlacedSchedulingRequest (SchedulingRequest request)
specifier|public
name|PlacedSchedulingRequest
parameter_list|(
name|SchedulingRequest
name|request
parameter_list|)
block|{
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
block|}
DECL|method|getSchedulingRequest ()
specifier|public
name|SchedulingRequest
name|getSchedulingRequest
parameter_list|()
block|{
return|return
name|request
return|;
block|}
comment|/**    * List of Node locations on which this Scheduling Request can be placed.    * The size of this list = schedulingRequest.resourceSizing.numAllocations.    * @return List of Scheduler nodes.    */
DECL|method|getNodes ()
specifier|public
name|List
argument_list|<
name|SchedulerNode
argument_list|>
name|getNodes
parameter_list|()
block|{
return|return
name|nodes
return|;
block|}
DECL|method|getPlacementAttempt ()
specifier|public
name|int
name|getPlacementAttempt
parameter_list|()
block|{
return|return
name|placementAttempt
return|;
block|}
DECL|method|setPlacementAttempt (int attempt)
specifier|public
name|void
name|setPlacementAttempt
parameter_list|(
name|int
name|attempt
parameter_list|)
block|{
name|this
operator|.
name|placementAttempt
operator|=
name|attempt
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"PlacedSchedulingRequest{"
operator|+
literal|"placementAttempt="
operator|+
name|placementAttempt
operator|+
literal|", request="
operator|+
name|request
operator|+
literal|", nodes="
operator|+
name|nodes
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

