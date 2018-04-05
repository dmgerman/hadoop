begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.common.statemachine
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|common
operator|.
name|statemachine
package|;
end_package

begin_comment
comment|/**  * Class wraps invalid state transition exception.  */
end_comment

begin_class
DECL|class|InvalidStateTransitionException
specifier|public
class|class
name|InvalidStateTransitionException
extends|extends
name|Exception
block|{
DECL|field|currentState
specifier|private
name|Enum
argument_list|<
name|?
argument_list|>
name|currentState
decl_stmt|;
DECL|field|event
specifier|private
name|Enum
argument_list|<
name|?
argument_list|>
name|event
decl_stmt|;
DECL|method|InvalidStateTransitionException (Enum<?> currentState, Enum<?> event)
specifier|public
name|InvalidStateTransitionException
parameter_list|(
name|Enum
argument_list|<
name|?
argument_list|>
name|currentState
parameter_list|,
name|Enum
argument_list|<
name|?
argument_list|>
name|event
parameter_list|)
block|{
name|super
argument_list|(
literal|"Invalid event: "
operator|+
name|event
operator|+
literal|" at "
operator|+
name|currentState
operator|+
literal|" state."
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentState
operator|=
name|currentState
expr_stmt|;
name|this
operator|.
name|event
operator|=
name|event
expr_stmt|;
block|}
DECL|method|getCurrentState ()
specifier|public
name|Enum
argument_list|<
name|?
argument_list|>
name|getCurrentState
parameter_list|()
block|{
return|return
name|currentState
return|;
block|}
DECL|method|getEvent ()
specifier|public
name|Enum
argument_list|<
name|?
argument_list|>
name|getEvent
parameter_list|()
block|{
return|return
name|event
return|;
block|}
block|}
end_class

end_unit

