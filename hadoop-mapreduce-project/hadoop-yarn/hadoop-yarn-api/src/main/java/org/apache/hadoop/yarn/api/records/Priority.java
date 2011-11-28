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

begin_comment
comment|/**  * The priority assigned to a ResourceRequest or Application or Container   * allocation   *  */
end_comment

begin_interface
DECL|interface|Priority
specifier|public
interface|interface
name|Priority
extends|extends
name|Comparable
argument_list|<
name|Priority
argument_list|>
block|{
comment|/**    * Get the assigned priority    * @return the assigned priority    */
DECL|method|getPriority ()
specifier|public
specifier|abstract
name|int
name|getPriority
parameter_list|()
function_decl|;
comment|/**    * Set the assigned priority    * @param priority the assigned priority    */
DECL|method|setPriority (int priority)
specifier|public
specifier|abstract
name|void
name|setPriority
parameter_list|(
name|int
name|priority
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

