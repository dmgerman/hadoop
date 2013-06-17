begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.state
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|state
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
name|Evolving
import|;
end_import

begin_comment
comment|/**  * Hook for Transition. This lead to state machine to move to   * the post state as registered in the state machine.  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Evolving
DECL|interface|SingleArcTransition
specifier|public
interface|interface
name|SingleArcTransition
parameter_list|<
name|OPERAND
parameter_list|,
name|EVENT
parameter_list|>
block|{
comment|/**    * Transition hook.    *     * @param operand the entity attached to the FSM, whose internal     *                state may change.    * @param event causal event    */
DECL|method|transition (OPERAND operand, EVENT event)
specifier|public
name|void
name|transition
parameter_list|(
name|OPERAND
name|operand
parameter_list|,
name|EVENT
name|event
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

