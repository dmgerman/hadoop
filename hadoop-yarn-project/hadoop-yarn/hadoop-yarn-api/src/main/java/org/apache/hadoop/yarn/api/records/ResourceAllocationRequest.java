begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
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
operator|.
name|Private
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
name|Public
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
name|Unstable
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
name|Stable
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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  * {@code ResourceAllocationRequest} represents an allocation  * made for a reservation for the current state of the plan. This can be  * changed for reasons such as re-planning, but will always be subject to the  * constraints of the user contract as described by  * {@link ReservationDefinition}  * {@link Resource}  *  *<p>  * It includes:  *<ul>  *<li>StartTime of the allocation.</li>  *<li>EndTime of the allocation.</li>  *<li>{@link Resource} reserved for the allocation.</li>  *</ul>  *  * @see Resource  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|ResourceAllocationRequest
specifier|public
specifier|abstract
class|class
name|ResourceAllocationRequest
block|{
comment|/**    * @param startTime The start time that the capability is reserved for.    * @param endTime The end time that the capability is reserved for.    * @param capability {@link Resource} representing the capability of the    *                                   resource allocation.    * @return {ResourceAllocationRequest} which represents the capability of    * the resource allocation for a time interval.    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|newInstance (long startTime, long endTime, Resource capability)
specifier|public
specifier|static
name|ResourceAllocationRequest
name|newInstance
parameter_list|(
name|long
name|startTime
parameter_list|,
name|long
name|endTime
parameter_list|,
name|Resource
name|capability
parameter_list|)
block|{
name|ResourceAllocationRequest
name|ra
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ResourceAllocationRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|ra
operator|.
name|setEndTime
argument_list|(
name|endTime
argument_list|)
expr_stmt|;
name|ra
operator|.
name|setStartTime
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
name|ra
operator|.
name|setCapability
argument_list|(
name|capability
argument_list|)
expr_stmt|;
return|return
name|ra
return|;
block|}
comment|/**    * Get the start time that the resource is allocated.    *    * @return the start time that the resource is allocated.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getStartTime ()
specifier|public
specifier|abstract
name|long
name|getStartTime
parameter_list|()
function_decl|;
comment|/**    * Set the start time that the resource is allocated.    *    * @param startTime The start time that the capability is reserved for.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setStartTime (long startTime)
specifier|public
specifier|abstract
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
function_decl|;
comment|/**    * Get the end time that the resource is allocated.    *    * @return the end time that the resource is allocated.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getEndTime ()
specifier|public
specifier|abstract
name|long
name|getEndTime
parameter_list|()
function_decl|;
comment|/**    * Set the end time that the resource is allocated.    *    * @param endTime The end time that the capability is reserved for.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setEndTime (long endTime)
specifier|public
specifier|abstract
name|void
name|setEndTime
parameter_list|(
name|long
name|endTime
parameter_list|)
function_decl|;
comment|/**    * Get the allocated resource.    *    * @return the allocated resource.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getCapability ()
specifier|public
specifier|abstract
name|Resource
name|getCapability
parameter_list|()
function_decl|;
comment|/**    * Set the allocated resource.    *    * @param resource {@link Resource} representing the capability of the    *                                   resource allocation.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setCapability (Resource resource)
specifier|public
specifier|abstract
name|void
name|setCapability
parameter_list|(
name|Resource
name|resource
parameter_list|)
function_decl|;
block|}
end_class

end_unit

