begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.web.rest.agent
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|rest
operator|.
name|agent
package|;
end_package

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|AgentRestOperations
specifier|public
interface|interface
name|AgentRestOperations
block|{
DECL|method|handleRegistration (Register registration)
name|RegistrationResponse
name|handleRegistration
parameter_list|(
name|Register
name|registration
parameter_list|)
function_decl|;
DECL|method|handleHeartBeat (HeartBeat heartBeat)
name|HeartBeatResponse
name|handleHeartBeat
parameter_list|(
name|HeartBeat
name|heartBeat
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

