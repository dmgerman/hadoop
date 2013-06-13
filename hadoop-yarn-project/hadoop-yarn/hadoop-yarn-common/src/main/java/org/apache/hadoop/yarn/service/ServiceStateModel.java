begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
package|;
end_package

begin_comment
comment|/**  * Implements the service state model for YARN.  */
end_comment

begin_class
DECL|class|ServiceStateModel
specifier|public
class|class
name|ServiceStateModel
block|{
comment|/**    * Map of all valid state transitions    * [current] [proposed1, proposed2, ...]    */
DECL|field|statemap
specifier|private
specifier|static
specifier|final
name|boolean
index|[]
index|[]
name|statemap
init|=
block|{
comment|//                uninited inited started stopped
comment|/* uninited  */
block|{
literal|false
block|,
literal|true
block|,
literal|false
block|,
literal|true
block|}
block|,
comment|/* inited    */
block|{
literal|false
block|,
literal|true
block|,
literal|true
block|,
literal|true
block|}
block|,
comment|/* started   */
block|{
literal|false
block|,
literal|false
block|,
literal|true
block|,
literal|true
block|}
block|,
comment|/* stopped   */
block|{
literal|false
block|,
literal|false
block|,
literal|false
block|,
literal|true
block|}
block|,     }
decl_stmt|;
comment|/**    * The state of the service    */
DECL|field|state
specifier|private
specifier|volatile
name|Service
operator|.
name|STATE
name|state
decl_stmt|;
comment|/**    * The name of the service: used in exceptions    */
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
comment|/**    * Create the service state model in the {@link Service.STATE#NOTINITED}    * state.    */
DECL|method|ServiceStateModel (String name)
specifier|public
name|ServiceStateModel
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|Service
operator|.
name|STATE
operator|.
name|NOTINITED
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a service state model instance in the chosen state    * @param state the starting state    */
DECL|method|ServiceStateModel (String name, Service.STATE state)
specifier|public
name|ServiceStateModel
parameter_list|(
name|String
name|name
parameter_list|,
name|Service
operator|.
name|STATE
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**    * Query the service state. This is a non-blocking operation.    * @return the state    */
DECL|method|getState ()
specifier|public
name|Service
operator|.
name|STATE
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
comment|/**    * Query that the state is in a specific state    * @param proposed proposed new state    * @return the state    */
DECL|method|isInState (Service.STATE proposed)
specifier|public
name|boolean
name|isInState
parameter_list|(
name|Service
operator|.
name|STATE
name|proposed
parameter_list|)
block|{
return|return
name|state
operator|.
name|equals
argument_list|(
name|proposed
argument_list|)
return|;
block|}
comment|/**    * Verify that that a service is in a given state.    * @param expectedState the desired state    * @throws ServiceStateException if the service state is different from    * the desired state    */
DECL|method|ensureCurrentState (Service.STATE expectedState)
specifier|public
name|void
name|ensureCurrentState
parameter_list|(
name|Service
operator|.
name|STATE
name|expectedState
parameter_list|)
block|{
if|if
condition|(
name|state
operator|!=
name|expectedState
condition|)
block|{
throw|throw
operator|new
name|ServiceStateException
argument_list|(
name|name
operator|+
literal|": for this operation, the "
operator|+
literal|"current service state must be "
operator|+
name|expectedState
operator|+
literal|" instead of "
operator|+
name|state
argument_list|)
throw|;
block|}
block|}
comment|/**    * Enter a state -thread safe.    *    * @param proposed proposed new state    * @return the original state    * @throws ServiceStateException if the transition is not permitted    */
DECL|method|enterState (Service.STATE proposed)
specifier|public
specifier|synchronized
name|Service
operator|.
name|STATE
name|enterState
parameter_list|(
name|Service
operator|.
name|STATE
name|proposed
parameter_list|)
block|{
name|checkStateTransition
argument_list|(
name|name
argument_list|,
name|state
argument_list|,
name|proposed
argument_list|)
expr_stmt|;
name|Service
operator|.
name|STATE
name|oldState
init|=
name|state
decl_stmt|;
comment|//atomic write of the new state
name|state
operator|=
name|proposed
expr_stmt|;
return|return
name|oldState
return|;
block|}
comment|/**    * Check that a state tansition is valid and    * throw an exception if not    * @param name name of the service (can be null)    * @param state current state    * @param proposed proposed new state    */
DECL|method|checkStateTransition (String name, Service.STATE state, Service.STATE proposed)
specifier|public
specifier|static
name|void
name|checkStateTransition
parameter_list|(
name|String
name|name
parameter_list|,
name|Service
operator|.
name|STATE
name|state
parameter_list|,
name|Service
operator|.
name|STATE
name|proposed
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isValidStateTransition
argument_list|(
name|state
argument_list|,
name|proposed
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ServiceStateException
argument_list|(
name|name
operator|+
literal|" cannot enter state "
operator|+
name|proposed
operator|+
literal|" from state "
operator|+
name|state
argument_list|)
throw|;
block|}
block|}
comment|/**    * Is a state transition valid?    * There are no checks for current==proposed    * as that is considered a non-transition.    *    * using an array kills off all branch misprediction costs, at the expense    * of cache line misses.    *    * @param current current state    * @param proposed proposed new state    * @return true if the transition to a new state is valid    */
DECL|method|isValidStateTransition (Service.STATE current, Service.STATE proposed)
specifier|public
specifier|static
name|boolean
name|isValidStateTransition
parameter_list|(
name|Service
operator|.
name|STATE
name|current
parameter_list|,
name|Service
operator|.
name|STATE
name|proposed
parameter_list|)
block|{
name|boolean
index|[]
name|row
init|=
name|statemap
index|[
name|current
operator|.
name|getValue
argument_list|()
index|]
decl_stmt|;
return|return
name|row
index|[
name|proposed
operator|.
name|getValue
argument_list|()
index|]
return|;
block|}
comment|/**    * return the state text as the toString() value    * @return the current state's description    */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|(
name|name
operator|.
name|isEmpty
argument_list|()
condition|?
literal|""
else|:
operator|(
operator|(
name|name
operator|)
operator|+
literal|": "
operator|)
operator|)
operator|+
name|state
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

