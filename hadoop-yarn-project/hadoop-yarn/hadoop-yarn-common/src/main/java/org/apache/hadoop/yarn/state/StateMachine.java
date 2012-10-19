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

begin_interface
DECL|interface|StateMachine
specifier|public
interface|interface
name|StateMachine
parameter_list|<
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
DECL|method|getCurrentState ()
specifier|public
name|STATE
name|getCurrentState
parameter_list|()
function_decl|;
DECL|method|doTransition (EVENTTYPE eventType, EVENT event)
specifier|public
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
function_decl|;
block|}
end_interface

end_unit

