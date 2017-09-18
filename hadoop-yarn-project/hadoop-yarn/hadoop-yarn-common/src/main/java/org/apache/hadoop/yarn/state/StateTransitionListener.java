begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * A State Transition Listener.  * It exposes a pre and post transition hook called before and  * after the transition.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|StateTransitionListener
specifier|public
interface|interface
name|StateTransitionListener
parameter_list|<
name|OPERAND
parameter_list|,
name|EVENT
parameter_list|,
name|STATE
extends|extends
name|Enum
parameter_list|<
name|STATE
parameter_list|>
parameter_list|>
block|{
comment|/**    * Pre Transition Hook. This will be called before transition.    * @param op Operand.    * @param beforeState State before transition.    * @param eventToBeProcessed Incoming Event.    */
DECL|method|preTransition (OPERAND op, STATE beforeState, EVENT eventToBeProcessed)
name|void
name|preTransition
parameter_list|(
name|OPERAND
name|op
parameter_list|,
name|STATE
name|beforeState
parameter_list|,
name|EVENT
name|eventToBeProcessed
parameter_list|)
function_decl|;
comment|/**    * Post Transition Hook. This will be called after the transition.    * @param op Operand.    * @param beforeState State before transition.    * @param afterState State after transition.    * @param processedEvent Processed Event.    */
DECL|method|postTransition (OPERAND op, STATE beforeState, STATE afterState, EVENT processedEvent)
name|void
name|postTransition
parameter_list|(
name|OPERAND
name|op
parameter_list|,
name|STATE
name|beforeState
parameter_list|,
name|STATE
name|afterState
parameter_list|,
name|EVENT
name|processedEvent
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

