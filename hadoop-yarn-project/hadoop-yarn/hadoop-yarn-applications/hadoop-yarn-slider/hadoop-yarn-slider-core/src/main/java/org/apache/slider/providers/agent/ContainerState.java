begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.providers.agent
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
operator|.
name|agent
package|;
end_package

begin_comment
comment|/** The states a component instance can be. */
end_comment

begin_enum
DECL|enum|ContainerState
specifier|public
enum|enum
name|ContainerState
block|{
DECL|enumConstant|INIT
name|INIT
block|,
comment|// Container is not net activated
DECL|enumConstant|HEALTHY
name|HEALTHY
block|,
comment|// Agent is heartbeating
DECL|enumConstant|UNHEALTHY
name|UNHEALTHY
block|,
comment|// Container is unhealthy - no heartbeat for some interval
DECL|enumConstant|HEARTBEAT_LOST
name|HEARTBEAT_LOST
block|;
comment|// Container is lost - request a new instance
comment|/**    * Indicates whether or not it is a valid state to produce a command.    *    * @return true if command can be issued for this state.    */
DECL|method|canIssueCommands ()
specifier|public
name|boolean
name|canIssueCommands
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|HEALTHY
case|:
return|return
literal|true
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
block|}
end_enum

end_unit

