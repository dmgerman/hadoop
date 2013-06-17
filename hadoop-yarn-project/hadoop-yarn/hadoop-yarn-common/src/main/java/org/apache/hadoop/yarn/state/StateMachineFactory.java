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
name|java
operator|.
name|util
operator|.
name|EnumMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
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
name|Evolving
import|;
end_import

begin_comment
comment|/**  * State machine topology.  * This object is semantically immutable.  If you have a  * StateMachineFactory there's no operation in the API that changes  * its semantic properties.  *  * @param<OPERAND> The object type on which this state machine operates.  * @param<STATE> The state of the entity.  * @param<EVENTTYPE> The external eventType to be handled.  * @param<EVENT> The event object.  *  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|StateMachineFactory
specifier|final
specifier|public
class|class
name|StateMachineFactory
parameter_list|<
name|OPERAND
parameter_list|,
name|STATE
extends|extends
name|Enum
parameter_list|<
name|STATE
parameter_list|>
parameter_list|,
name|EVENTTYPE
extends|extends
name|Enum
parameter_list|<
name|EVENTTYPE
parameter_list|>
parameter_list|,
name|EVENT
parameter_list|>
block|{
DECL|field|transitionsListNode
specifier|private
specifier|final
name|TransitionsListNode
name|transitionsListNode
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|STATE
argument_list|,
name|Map
argument_list|<
name|EVENTTYPE
argument_list|,
DECL|field|stateMachineTable
name|Transition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
argument_list|>
argument_list|>
name|stateMachineTable
decl_stmt|;
DECL|field|defaultInitialState
specifier|private
name|STATE
name|defaultInitialState
decl_stmt|;
DECL|field|optimized
specifier|private
specifier|final
name|boolean
name|optimized
decl_stmt|;
comment|/**    * Constructor    *    * This is the only constructor in the API.    *    */
DECL|method|StateMachineFactory (STATE defaultInitialState)
specifier|public
name|StateMachineFactory
parameter_list|(
name|STATE
name|defaultInitialState
parameter_list|)
block|{
name|this
operator|.
name|transitionsListNode
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|defaultInitialState
operator|=
name|defaultInitialState
expr_stmt|;
name|this
operator|.
name|optimized
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|stateMachineTable
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|StateMachineFactory (StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> that, ApplicableTransition<OPERAND, STATE, EVENTTYPE, EVENT> t)
specifier|private
name|StateMachineFactory
parameter_list|(
name|StateMachineFactory
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
name|that
parameter_list|,
name|ApplicableTransition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
name|t
parameter_list|)
block|{
name|this
operator|.
name|defaultInitialState
operator|=
name|that
operator|.
name|defaultInitialState
expr_stmt|;
name|this
operator|.
name|transitionsListNode
operator|=
operator|new
name|TransitionsListNode
argument_list|(
name|t
argument_list|,
name|that
operator|.
name|transitionsListNode
argument_list|)
expr_stmt|;
name|this
operator|.
name|optimized
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|stateMachineTable
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|StateMachineFactory (StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> that, boolean optimized)
specifier|private
name|StateMachineFactory
parameter_list|(
name|StateMachineFactory
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
name|that
parameter_list|,
name|boolean
name|optimized
parameter_list|)
block|{
name|this
operator|.
name|defaultInitialState
operator|=
name|that
operator|.
name|defaultInitialState
expr_stmt|;
name|this
operator|.
name|transitionsListNode
operator|=
name|that
operator|.
name|transitionsListNode
expr_stmt|;
name|this
operator|.
name|optimized
operator|=
name|optimized
expr_stmt|;
if|if
condition|(
name|optimized
condition|)
block|{
name|makeStateMachineTable
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|stateMachineTable
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|interface|ApplicableTransition
specifier|private
interface|interface
name|ApplicableTransition
parameter_list|<
name|OPERAND
parameter_list|,
name|STATE
extends|extends
name|Enum
parameter_list|<
name|STATE
parameter_list|>
parameter_list|,
name|EVENTTYPE
extends|extends
name|Enum
parameter_list|<
name|EVENTTYPE
parameter_list|>
parameter_list|,
name|EVENT
parameter_list|>
block|{
DECL|method|apply (StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> subject)
name|void
name|apply
parameter_list|(
name|StateMachineFactory
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
name|subject
parameter_list|)
function_decl|;
block|}
DECL|class|TransitionsListNode
specifier|private
class|class
name|TransitionsListNode
block|{
DECL|field|transition
specifier|final
name|ApplicableTransition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
name|transition
decl_stmt|;
DECL|field|next
specifier|final
name|TransitionsListNode
name|next
decl_stmt|;
DECL|method|TransitionsListNode (ApplicableTransition<OPERAND, STATE, EVENTTYPE, EVENT> transition, TransitionsListNode next)
name|TransitionsListNode
parameter_list|(
name|ApplicableTransition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
name|transition
parameter_list|,
name|TransitionsListNode
name|next
parameter_list|)
block|{
name|this
operator|.
name|transition
operator|=
name|transition
expr_stmt|;
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
block|}
DECL|class|ApplicableSingleOrMultipleTransition
specifier|static
specifier|private
class|class
name|ApplicableSingleOrMultipleTransition
parameter_list|<
name|OPERAND
parameter_list|,
name|STATE
extends|extends
name|Enum
parameter_list|<
name|STATE
parameter_list|>
parameter_list|,
name|EVENTTYPE
extends|extends
name|Enum
parameter_list|<
name|EVENTTYPE
parameter_list|>
parameter_list|,
name|EVENT
parameter_list|>
implements|implements
name|ApplicableTransition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
block|{
DECL|field|preState
specifier|final
name|STATE
name|preState
decl_stmt|;
DECL|field|eventType
specifier|final
name|EVENTTYPE
name|eventType
decl_stmt|;
DECL|field|transition
specifier|final
name|Transition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
name|transition
decl_stmt|;
DECL|method|ApplicableSingleOrMultipleTransition (STATE preState, EVENTTYPE eventType, Transition<OPERAND, STATE, EVENTTYPE, EVENT> transition)
name|ApplicableSingleOrMultipleTransition
parameter_list|(
name|STATE
name|preState
parameter_list|,
name|EVENTTYPE
name|eventType
parameter_list|,
name|Transition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
name|transition
parameter_list|)
block|{
name|this
operator|.
name|preState
operator|=
name|preState
expr_stmt|;
name|this
operator|.
name|eventType
operator|=
name|eventType
expr_stmt|;
name|this
operator|.
name|transition
operator|=
name|transition
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (StateMachineFactory<OPERAND, STATE, EVENTTYPE, EVENT> subject)
specifier|public
name|void
name|apply
parameter_list|(
name|StateMachineFactory
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
name|subject
parameter_list|)
block|{
name|Map
argument_list|<
name|EVENTTYPE
argument_list|,
name|Transition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
argument_list|>
name|transitionMap
init|=
name|subject
operator|.
name|stateMachineTable
operator|.
name|get
argument_list|(
name|preState
argument_list|)
decl_stmt|;
if|if
condition|(
name|transitionMap
operator|==
literal|null
condition|)
block|{
comment|// I use HashMap here because I would expect most EVENTTYPE's to not
comment|//  apply out of a particular state, so FSM sizes would be
comment|//  quadratic if I use EnumMap's here as I do at the top level.
name|transitionMap
operator|=
operator|new
name|HashMap
argument_list|<
name|EVENTTYPE
argument_list|,
name|Transition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|subject
operator|.
name|stateMachineTable
operator|.
name|put
argument_list|(
name|preState
argument_list|,
name|transitionMap
argument_list|)
expr_stmt|;
block|}
name|transitionMap
operator|.
name|put
argument_list|(
name|eventType
argument_list|,
name|transition
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @return a NEW StateMachineFactory just like {@code this} with the current    *          transition added as a new legal transition.  This overload    *          has no hook object.    *    *         Note that the returned StateMachineFactory is a distinct    *         object.    *    *         This method is part of the API.    *    * @param preState pre-transition state    * @param postState post-transition state    * @param eventType stimulus for the transition    */
specifier|public
name|StateMachineFactory
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
DECL|method|addTransition (STATE preState, STATE postState, EVENTTYPE eventType)
name|addTransition
parameter_list|(
name|STATE
name|preState
parameter_list|,
name|STATE
name|postState
parameter_list|,
name|EVENTTYPE
name|eventType
parameter_list|)
block|{
return|return
name|addTransition
argument_list|(
name|preState
argument_list|,
name|postState
argument_list|,
name|eventType
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * @return a NEW StateMachineFactory just like {@code this} with the current    *          transition added as a new legal transition.  This overload    *          has no hook object.    *    *    *         Note that the returned StateMachineFactory is a distinct    *         object.    *    *         This method is part of the API.    *    * @param preState pre-transition state    * @param postState post-transition state    * @param eventTypes List of stimuli for the transitions    */
DECL|method|addTransition ( STATE preState, STATE postState, Set<EVENTTYPE> eventTypes)
specifier|public
name|StateMachineFactory
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
name|addTransition
parameter_list|(
name|STATE
name|preState
parameter_list|,
name|STATE
name|postState
parameter_list|,
name|Set
argument_list|<
name|EVENTTYPE
argument_list|>
name|eventTypes
parameter_list|)
block|{
return|return
name|addTransition
argument_list|(
name|preState
argument_list|,
name|postState
argument_list|,
name|eventTypes
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * @return a NEW StateMachineFactory just like {@code this} with the current    *          transition added as a new legal transition    *    *         Note that the returned StateMachineFactory is a distinct    *         object.    *    *         This method is part of the API.    *    * @param preState pre-transition state    * @param postState post-transition state    * @param eventTypes List of stimuli for the transitions    * @param hook transition hook    */
DECL|method|addTransition ( STATE preState, STATE postState, Set<EVENTTYPE> eventTypes, SingleArcTransition<OPERAND, EVENT> hook)
specifier|public
name|StateMachineFactory
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
name|addTransition
parameter_list|(
name|STATE
name|preState
parameter_list|,
name|STATE
name|postState
parameter_list|,
name|Set
argument_list|<
name|EVENTTYPE
argument_list|>
name|eventTypes
parameter_list|,
name|SingleArcTransition
argument_list|<
name|OPERAND
argument_list|,
name|EVENT
argument_list|>
name|hook
parameter_list|)
block|{
name|StateMachineFactory
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
name|factory
init|=
literal|null
decl_stmt|;
for|for
control|(
name|EVENTTYPE
name|event
range|:
name|eventTypes
control|)
block|{
if|if
condition|(
name|factory
operator|==
literal|null
condition|)
block|{
name|factory
operator|=
name|addTransition
argument_list|(
name|preState
argument_list|,
name|postState
argument_list|,
name|event
argument_list|,
name|hook
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|factory
operator|=
name|factory
operator|.
name|addTransition
argument_list|(
name|preState
argument_list|,
name|postState
argument_list|,
name|event
argument_list|,
name|hook
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|factory
return|;
block|}
comment|/**    * @return a NEW StateMachineFactory just like {@code this} with the current    *          transition added as a new legal transition    *    *         Note that the returned StateMachineFactory is a distinct object.    *    *         This method is part of the API.    *    * @param preState pre-transition state    * @param postState post-transition state    * @param eventType stimulus for the transition    * @param hook transition hook    */
specifier|public
name|StateMachineFactory
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
DECL|method|addTransition (STATE preState, STATE postState, EVENTTYPE eventType, SingleArcTransition<OPERAND, EVENT> hook)
name|addTransition
parameter_list|(
name|STATE
name|preState
parameter_list|,
name|STATE
name|postState
parameter_list|,
name|EVENTTYPE
name|eventType
parameter_list|,
name|SingleArcTransition
argument_list|<
name|OPERAND
argument_list|,
name|EVENT
argument_list|>
name|hook
parameter_list|)
block|{
return|return
operator|new
name|StateMachineFactory
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
argument_list|(
name|this
argument_list|,
operator|new
name|ApplicableSingleOrMultipleTransition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
argument_list|(
name|preState
argument_list|,
name|eventType
argument_list|,
operator|new
name|SingleInternalArc
argument_list|(
name|postState
argument_list|,
name|hook
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * @return a NEW StateMachineFactory just like {@code this} with the current    *          transition added as a new legal transition    *    *         Note that the returned StateMachineFactory is a distinct object.    *    *         This method is part of the API.    *    * @param preState pre-transition state    * @param postStates valid post-transition states    * @param eventType stimulus for the transition    * @param hook transition hook    */
specifier|public
name|StateMachineFactory
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
DECL|method|addTransition (STATE preState, Set<STATE> postStates, EVENTTYPE eventType, MultipleArcTransition<OPERAND, EVENT, STATE> hook)
name|addTransition
parameter_list|(
name|STATE
name|preState
parameter_list|,
name|Set
argument_list|<
name|STATE
argument_list|>
name|postStates
parameter_list|,
name|EVENTTYPE
name|eventType
parameter_list|,
name|MultipleArcTransition
argument_list|<
name|OPERAND
argument_list|,
name|EVENT
argument_list|,
name|STATE
argument_list|>
name|hook
parameter_list|)
block|{
return|return
operator|new
name|StateMachineFactory
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
argument_list|(
name|this
argument_list|,
operator|new
name|ApplicableSingleOrMultipleTransition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
argument_list|(
name|preState
argument_list|,
name|eventType
argument_list|,
operator|new
name|MultipleInternalArc
argument_list|(
name|postStates
argument_list|,
name|hook
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * @return a StateMachineFactory just like {@code this}, except that if    *         you won't need any synchronization to build a state machine    *    *         Note that the returned StateMachineFactory is a distinct object.    *    *         This method is part of the API.    *    *         The only way you could distinguish the returned    *         StateMachineFactory from {@code this} would be by    *         measuring the performance of the derived     *         {@code StateMachine} you can get from it.    *    * Calling this is optional.  It doesn't change the semantics of the factory,    *   if you call it then when you use the factory there is no synchronization.    */
specifier|public
name|StateMachineFactory
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
DECL|method|installTopology ()
name|installTopology
parameter_list|()
block|{
return|return
operator|new
name|StateMachineFactory
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
argument_list|(
name|this
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Effect a transition due to the effecting stimulus.    * @param state current state    * @param eventType trigger to initiate the transition    * @param cause causal eventType context    * @return transitioned state    */
DECL|method|doTransition (OPERAND operand, STATE oldState, EVENTTYPE eventType, EVENT event)
specifier|private
name|STATE
name|doTransition
parameter_list|(
name|OPERAND
name|operand
parameter_list|,
name|STATE
name|oldState
parameter_list|,
name|EVENTTYPE
name|eventType
parameter_list|,
name|EVENT
name|event
parameter_list|)
throws|throws
name|InvalidStateTransitonException
block|{
comment|// We can assume that stateMachineTable is non-null because we call
comment|//  maybeMakeStateMachineTable() when we build an InnerStateMachine ,
comment|//  and this code only gets called from inside a working InnerStateMachine .
name|Map
argument_list|<
name|EVENTTYPE
argument_list|,
name|Transition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
argument_list|>
name|transitionMap
init|=
name|stateMachineTable
operator|.
name|get
argument_list|(
name|oldState
argument_list|)
decl_stmt|;
if|if
condition|(
name|transitionMap
operator|!=
literal|null
condition|)
block|{
name|Transition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
name|transition
init|=
name|transitionMap
operator|.
name|get
argument_list|(
name|eventType
argument_list|)
decl_stmt|;
if|if
condition|(
name|transition
operator|!=
literal|null
condition|)
block|{
return|return
name|transition
operator|.
name|doTransition
argument_list|(
name|operand
argument_list|,
name|oldState
argument_list|,
name|event
argument_list|,
name|eventType
argument_list|)
return|;
block|}
block|}
throw|throw
operator|new
name|InvalidStateTransitonException
argument_list|(
name|oldState
argument_list|,
name|eventType
argument_list|)
throw|;
block|}
DECL|method|maybeMakeStateMachineTable ()
specifier|private
specifier|synchronized
name|void
name|maybeMakeStateMachineTable
parameter_list|()
block|{
if|if
condition|(
name|stateMachineTable
operator|==
literal|null
condition|)
block|{
name|makeStateMachineTable
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|makeStateMachineTable ()
specifier|private
name|void
name|makeStateMachineTable
parameter_list|()
block|{
name|Stack
argument_list|<
name|ApplicableTransition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
argument_list|>
name|stack
init|=
operator|new
name|Stack
argument_list|<
name|ApplicableTransition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|STATE
argument_list|,
name|Map
argument_list|<
name|EVENTTYPE
argument_list|,
name|Transition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
argument_list|>
argument_list|>
name|prototype
init|=
operator|new
name|HashMap
argument_list|<
name|STATE
argument_list|,
name|Map
argument_list|<
name|EVENTTYPE
argument_list|,
name|Transition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|prototype
operator|.
name|put
argument_list|(
name|defaultInitialState
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// I use EnumMap here because it'll be faster and denser.  I would
comment|//  expect most of the states to have at least one transition.
name|stateMachineTable
operator|=
operator|new
name|EnumMap
argument_list|<
name|STATE
argument_list|,
name|Map
argument_list|<
name|EVENTTYPE
argument_list|,
name|Transition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
argument_list|>
argument_list|>
argument_list|(
name|prototype
argument_list|)
expr_stmt|;
for|for
control|(
name|TransitionsListNode
name|cursor
init|=
name|transitionsListNode
init|;
name|cursor
operator|!=
literal|null
condition|;
name|cursor
operator|=
name|cursor
operator|.
name|next
control|)
block|{
name|stack
operator|.
name|push
argument_list|(
name|cursor
operator|.
name|transition
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
operator|!
name|stack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|stack
operator|.
name|pop
argument_list|()
operator|.
name|apply
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
DECL|interface|Transition
specifier|private
interface|interface
name|Transition
parameter_list|<
name|OPERAND
parameter_list|,
name|STATE
extends|extends
name|Enum
parameter_list|<
name|STATE
parameter_list|>
parameter_list|,
name|EVENTTYPE
extends|extends
name|Enum
parameter_list|<
name|EVENTTYPE
parameter_list|>
parameter_list|,
name|EVENT
parameter_list|>
block|{
DECL|method|doTransition (OPERAND operand, STATE oldState, EVENT event, EVENTTYPE eventType)
name|STATE
name|doTransition
parameter_list|(
name|OPERAND
name|operand
parameter_list|,
name|STATE
name|oldState
parameter_list|,
name|EVENT
name|event
parameter_list|,
name|EVENTTYPE
name|eventType
parameter_list|)
function_decl|;
block|}
DECL|class|SingleInternalArc
specifier|private
class|class
name|SingleInternalArc
implements|implements
name|Transition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
block|{
DECL|field|postState
specifier|private
name|STATE
name|postState
decl_stmt|;
DECL|field|hook
specifier|private
name|SingleArcTransition
argument_list|<
name|OPERAND
argument_list|,
name|EVENT
argument_list|>
name|hook
decl_stmt|;
comment|// transition hook
DECL|method|SingleInternalArc (STATE postState, SingleArcTransition<OPERAND, EVENT> hook)
name|SingleInternalArc
parameter_list|(
name|STATE
name|postState
parameter_list|,
name|SingleArcTransition
argument_list|<
name|OPERAND
argument_list|,
name|EVENT
argument_list|>
name|hook
parameter_list|)
block|{
name|this
operator|.
name|postState
operator|=
name|postState
expr_stmt|;
name|this
operator|.
name|hook
operator|=
name|hook
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doTransition (OPERAND operand, STATE oldState, EVENT event, EVENTTYPE eventType)
specifier|public
name|STATE
name|doTransition
parameter_list|(
name|OPERAND
name|operand
parameter_list|,
name|STATE
name|oldState
parameter_list|,
name|EVENT
name|event
parameter_list|,
name|EVENTTYPE
name|eventType
parameter_list|)
block|{
if|if
condition|(
name|hook
operator|!=
literal|null
condition|)
block|{
name|hook
operator|.
name|transition
argument_list|(
name|operand
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
return|return
name|postState
return|;
block|}
block|}
DECL|class|MultipleInternalArc
specifier|private
class|class
name|MultipleInternalArc
implements|implements
name|Transition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
block|{
comment|// Fields
DECL|field|validPostStates
specifier|private
name|Set
argument_list|<
name|STATE
argument_list|>
name|validPostStates
decl_stmt|;
DECL|field|hook
specifier|private
name|MultipleArcTransition
argument_list|<
name|OPERAND
argument_list|,
name|EVENT
argument_list|,
name|STATE
argument_list|>
name|hook
decl_stmt|;
comment|// transition hook
DECL|method|MultipleInternalArc (Set<STATE> postStates, MultipleArcTransition<OPERAND, EVENT, STATE> hook)
name|MultipleInternalArc
parameter_list|(
name|Set
argument_list|<
name|STATE
argument_list|>
name|postStates
parameter_list|,
name|MultipleArcTransition
argument_list|<
name|OPERAND
argument_list|,
name|EVENT
argument_list|,
name|STATE
argument_list|>
name|hook
parameter_list|)
block|{
name|this
operator|.
name|validPostStates
operator|=
name|postStates
expr_stmt|;
name|this
operator|.
name|hook
operator|=
name|hook
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doTransition (OPERAND operand, STATE oldState, EVENT event, EVENTTYPE eventType)
specifier|public
name|STATE
name|doTransition
parameter_list|(
name|OPERAND
name|operand
parameter_list|,
name|STATE
name|oldState
parameter_list|,
name|EVENT
name|event
parameter_list|,
name|EVENTTYPE
name|eventType
parameter_list|)
throws|throws
name|InvalidStateTransitonException
block|{
name|STATE
name|postState
init|=
name|hook
operator|.
name|transition
argument_list|(
name|operand
argument_list|,
name|event
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|validPostStates
operator|.
name|contains
argument_list|(
name|postState
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidStateTransitonException
argument_list|(
name|oldState
argument_list|,
name|eventType
argument_list|)
throw|;
block|}
return|return
name|postState
return|;
block|}
block|}
comment|/*     * @return a {@link StateMachine} that starts in     *         {@code initialState} and whose {@link Transition} s are    *         applied to {@code operand} .    *    *         This is part of the API.    *    * @param operand the object upon which the returned     *                {@link StateMachine} will operate.    * @param initialState the state in which the returned     *                {@link StateMachine} will start.    *                    */
specifier|public
name|StateMachine
argument_list|<
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
DECL|method|make (OPERAND operand, STATE initialState)
name|make
parameter_list|(
name|OPERAND
name|operand
parameter_list|,
name|STATE
name|initialState
parameter_list|)
block|{
return|return
operator|new
name|InternalStateMachine
argument_list|(
name|operand
argument_list|,
name|initialState
argument_list|)
return|;
block|}
comment|/*     * @return a {@link StateMachine} that starts in the default initial    *          state and whose {@link Transition} s are applied to    *          {@code operand} .     *    *         This is part of the API.    *    * @param operand the object upon which the returned     *                {@link StateMachine} will operate.    *                    */
DECL|method|make (OPERAND operand)
specifier|public
name|StateMachine
argument_list|<
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
name|make
parameter_list|(
name|OPERAND
name|operand
parameter_list|)
block|{
return|return
operator|new
name|InternalStateMachine
argument_list|(
name|operand
argument_list|,
name|defaultInitialState
argument_list|)
return|;
block|}
DECL|class|InternalStateMachine
specifier|private
class|class
name|InternalStateMachine
implements|implements
name|StateMachine
argument_list|<
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
block|{
DECL|field|operand
specifier|private
specifier|final
name|OPERAND
name|operand
decl_stmt|;
DECL|field|currentState
specifier|private
name|STATE
name|currentState
decl_stmt|;
DECL|method|InternalStateMachine (OPERAND operand, STATE initialState)
name|InternalStateMachine
parameter_list|(
name|OPERAND
name|operand
parameter_list|,
name|STATE
name|initialState
parameter_list|)
block|{
name|this
operator|.
name|operand
operator|=
name|operand
expr_stmt|;
name|this
operator|.
name|currentState
operator|=
name|initialState
expr_stmt|;
if|if
condition|(
operator|!
name|optimized
condition|)
block|{
name|maybeMakeStateMachineTable
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getCurrentState ()
specifier|public
specifier|synchronized
name|STATE
name|getCurrentState
parameter_list|()
block|{
return|return
name|currentState
return|;
block|}
annotation|@
name|Override
DECL|method|doTransition (EVENTTYPE eventType, EVENT event)
specifier|public
specifier|synchronized
name|STATE
name|doTransition
parameter_list|(
name|EVENTTYPE
name|eventType
parameter_list|,
name|EVENT
name|event
parameter_list|)
throws|throws
name|InvalidStateTransitonException
block|{
name|currentState
operator|=
name|StateMachineFactory
operator|.
name|this
operator|.
name|doTransition
argument_list|(
name|operand
argument_list|,
name|currentState
argument_list|,
name|eventType
argument_list|,
name|event
argument_list|)
expr_stmt|;
return|return
name|currentState
return|;
block|}
block|}
comment|/**    * Generate a graph represents the state graph of this StateMachine    * @param name graph name    * @return Graph object generated    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|generateStateGraph (String name)
specifier|public
name|Graph
name|generateStateGraph
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|maybeMakeStateMachineTable
argument_list|()
expr_stmt|;
name|Graph
name|g
init|=
operator|new
name|Graph
argument_list|(
name|name
argument_list|)
decl_stmt|;
for|for
control|(
name|STATE
name|startState
range|:
name|stateMachineTable
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|EVENTTYPE
argument_list|,
name|Transition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
argument_list|>
name|transitions
init|=
name|stateMachineTable
operator|.
name|get
argument_list|(
name|startState
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|EVENTTYPE
argument_list|,
name|Transition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
argument_list|>
name|entry
range|:
name|transitions
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Transition
argument_list|<
name|OPERAND
argument_list|,
name|STATE
argument_list|,
name|EVENTTYPE
argument_list|,
name|EVENT
argument_list|>
name|transition
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|transition
operator|instanceof
name|StateMachineFactory
operator|.
name|SingleInternalArc
condition|)
block|{
name|StateMachineFactory
operator|.
name|SingleInternalArc
name|sa
init|=
operator|(
name|StateMachineFactory
operator|.
name|SingleInternalArc
operator|)
name|transition
decl_stmt|;
name|Graph
operator|.
name|Node
name|fromNode
init|=
name|g
operator|.
name|getNode
argument_list|(
name|startState
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Graph
operator|.
name|Node
name|toNode
init|=
name|g
operator|.
name|getNode
argument_list|(
name|sa
operator|.
name|postState
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|fromNode
operator|.
name|addEdge
argument_list|(
name|toNode
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|transition
operator|instanceof
name|StateMachineFactory
operator|.
name|MultipleInternalArc
condition|)
block|{
name|StateMachineFactory
operator|.
name|MultipleInternalArc
name|ma
init|=
operator|(
name|StateMachineFactory
operator|.
name|MultipleInternalArc
operator|)
name|transition
decl_stmt|;
name|Iterator
name|iter
init|=
name|ma
operator|.
name|validPostStates
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Graph
operator|.
name|Node
name|fromNode
init|=
name|g
operator|.
name|getNode
argument_list|(
name|startState
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Graph
operator|.
name|Node
name|toNode
init|=
name|g
operator|.
name|getNode
argument_list|(
name|iter
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|fromNode
operator|.
name|addEdge
argument_list|(
name|toNode
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|g
return|;
block|}
block|}
end_class

end_unit

