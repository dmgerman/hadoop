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
comment|/**  * A {@link StateTransitionListener} that dispatches the pre and post  * state transitions to multiple registered listeners.  * NOTE: The registered listeners are called in a for loop. Clients should  *       know that a listener configured earlier might prevent a later listener  *       from being called, if for instance it throws an un-caught Exception.  */
end_comment

begin_class
DECL|class|MultiStateTransitionListener
specifier|public
specifier|abstract
class|class
name|MultiStateTransitionListener
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
implements|implements
name|StateTransitionListener
argument_list|<
name|OPERAND
argument_list|,
name|EVENT
argument_list|,
name|STATE
argument_list|>
block|{
DECL|field|listeners
specifier|private
specifier|final
name|List
argument_list|<
name|StateTransitionListener
argument_list|<
name|OPERAND
argument_list|,
name|EVENT
argument_list|,
name|STATE
argument_list|>
argument_list|>
name|listeners
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Add a listener to the list of listeners.    * @param listener A listener.    */
DECL|method|addListener (StateTransitionListener<OPERAND, EVENT, STATE> listener)
specifier|public
name|void
name|addListener
parameter_list|(
name|StateTransitionListener
argument_list|<
name|OPERAND
argument_list|,
name|EVENT
argument_list|,
name|STATE
argument_list|>
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|preTransition (OPERAND op, STATE beforeState, EVENT eventToBeProcessed)
specifier|public
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
block|{
for|for
control|(
name|StateTransitionListener
argument_list|<
name|OPERAND
argument_list|,
name|EVENT
argument_list|,
name|STATE
argument_list|>
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|preTransition
argument_list|(
name|op
argument_list|,
name|beforeState
argument_list|,
name|eventToBeProcessed
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|postTransition (OPERAND op, STATE beforeState, STATE afterState, EVENT processedEvent)
specifier|public
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
block|{
for|for
control|(
name|StateTransitionListener
argument_list|<
name|OPERAND
argument_list|,
name|EVENT
argument_list|,
name|STATE
argument_list|>
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|postTransition
argument_list|(
name|op
argument_list|,
name|beforeState
argument_list|,
name|afterState
argument_list|,
name|processedEvent
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

