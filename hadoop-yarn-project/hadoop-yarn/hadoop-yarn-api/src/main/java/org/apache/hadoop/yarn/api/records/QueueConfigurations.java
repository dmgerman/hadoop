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
name|yarn
operator|.
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  * QueueConfigurations contain information about the configuration percentages  * of a queue.  *<p>  * It includes information such as:  *<ul>  *<li>Capacity of the queue.</li>  *<li>Absolute capacity of the queue.</li>  *<li>Maximum capacity of the queue.</li>  *<li>Absolute maximum capacity of the queue.</li>  *<li>Maximum ApplicationMaster resource percentage of the queue.</li>  *</ul>  */
end_comment

begin_class
DECL|class|QueueConfigurations
specifier|public
specifier|abstract
class|class
name|QueueConfigurations
block|{
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|newInstance (float capacity, float absoluteCapacity, float maxCapacity, float absoluteMaxCapacity, float maxAMPercentage)
specifier|public
specifier|static
name|QueueConfigurations
name|newInstance
parameter_list|(
name|float
name|capacity
parameter_list|,
name|float
name|absoluteCapacity
parameter_list|,
name|float
name|maxCapacity
parameter_list|,
name|float
name|absoluteMaxCapacity
parameter_list|,
name|float
name|maxAMPercentage
parameter_list|)
block|{
name|QueueConfigurations
name|queueConfigurations
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|QueueConfigurations
operator|.
name|class
argument_list|)
decl_stmt|;
name|queueConfigurations
operator|.
name|setCapacity
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
name|queueConfigurations
operator|.
name|setAbsoluteCapacity
argument_list|(
name|absoluteCapacity
argument_list|)
expr_stmt|;
name|queueConfigurations
operator|.
name|setMaxCapacity
argument_list|(
name|maxCapacity
argument_list|)
expr_stmt|;
name|queueConfigurations
operator|.
name|setAbsoluteMaxCapacity
argument_list|(
name|absoluteMaxCapacity
argument_list|)
expr_stmt|;
name|queueConfigurations
operator|.
name|setMaxAMPercentage
argument_list|(
name|maxAMPercentage
argument_list|)
expr_stmt|;
return|return
name|queueConfigurations
return|;
block|}
comment|/**    * Get the queue capacity.    *    * @return the queue capacity    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getCapacity ()
specifier|public
specifier|abstract
name|float
name|getCapacity
parameter_list|()
function_decl|;
comment|/**    * Set the queue capacity.    *    * @param capacity    *          the queue capacity.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setCapacity (float capacity)
specifier|public
specifier|abstract
name|void
name|setCapacity
parameter_list|(
name|float
name|capacity
parameter_list|)
function_decl|;
comment|/**    * Get the absolute capacity.    *    * @return the absolute capacity    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getAbsoluteCapacity ()
specifier|public
specifier|abstract
name|float
name|getAbsoluteCapacity
parameter_list|()
function_decl|;
comment|/**    * Set the absolute capacity.    *    * @param absoluteCapacity    *          the absolute capacity    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setAbsoluteCapacity (float absoluteCapacity)
specifier|public
specifier|abstract
name|void
name|setAbsoluteCapacity
parameter_list|(
name|float
name|absoluteCapacity
parameter_list|)
function_decl|;
comment|/**    * Get the maximum capacity.    *    * @return the maximum capacity    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getMaxCapacity ()
specifier|public
specifier|abstract
name|float
name|getMaxCapacity
parameter_list|()
function_decl|;
comment|/**    * Set the maximum capacity.    *    * @param maxCapacity    *          the maximum capacity    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setMaxCapacity (float maxCapacity)
specifier|public
specifier|abstract
name|void
name|setMaxCapacity
parameter_list|(
name|float
name|maxCapacity
parameter_list|)
function_decl|;
comment|/**    * Get the absolute maximum capacity.    *    * @return the absolute maximum capacity    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getAbsoluteMaxCapacity ()
specifier|public
specifier|abstract
name|float
name|getAbsoluteMaxCapacity
parameter_list|()
function_decl|;
comment|/**    * Set the absolute maximum capacity.    *    * @param absoluteMaxCapacity    *          the absolute maximum capacity    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setAbsoluteMaxCapacity (float absoluteMaxCapacity)
specifier|public
specifier|abstract
name|void
name|setAbsoluteMaxCapacity
parameter_list|(
name|float
name|absoluteMaxCapacity
parameter_list|)
function_decl|;
comment|/**    * Get the maximum AM resource percentage.    *    * @return the maximum AM resource percentage    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getMaxAMPercentage ()
specifier|public
specifier|abstract
name|float
name|getMaxAMPercentage
parameter_list|()
function_decl|;
comment|/**    * Set the maximum AM resource percentage.    *    * @param maxAMPercentage    *          the maximum AM resource percentage    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setMaxAMPercentage (float maxAMPercentage)
specifier|public
specifier|abstract
name|void
name|setMaxAMPercentage
parameter_list|(
name|float
name|maxAMPercentage
parameter_list|)
function_decl|;
block|}
end_class

end_unit

