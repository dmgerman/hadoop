begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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

begin_interface
DECL|interface|ApplicationReport
specifier|public
interface|interface
name|ApplicationReport
block|{
DECL|method|getApplicationId ()
name|ApplicationId
name|getApplicationId
parameter_list|()
function_decl|;
DECL|method|setApplicationId (ApplicationId applicationId)
name|void
name|setApplicationId
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
function_decl|;
DECL|method|getUser ()
name|String
name|getUser
parameter_list|()
function_decl|;
DECL|method|setUser (String user)
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
function_decl|;
DECL|method|getQueue ()
name|String
name|getQueue
parameter_list|()
function_decl|;
DECL|method|setQueue (String queue)
name|void
name|setQueue
parameter_list|(
name|String
name|queue
parameter_list|)
function_decl|;
DECL|method|getName ()
name|String
name|getName
parameter_list|()
function_decl|;
DECL|method|setName (String name)
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|getHost ()
name|String
name|getHost
parameter_list|()
function_decl|;
DECL|method|setHost (String host)
name|void
name|setHost
parameter_list|(
name|String
name|host
parameter_list|)
function_decl|;
DECL|method|getRpcPort ()
name|int
name|getRpcPort
parameter_list|()
function_decl|;
DECL|method|setRpcPort (int rpcPort)
name|void
name|setRpcPort
parameter_list|(
name|int
name|rpcPort
parameter_list|)
function_decl|;
DECL|method|getClientToken ()
name|String
name|getClientToken
parameter_list|()
function_decl|;
DECL|method|setClientToken (String clientToken)
name|void
name|setClientToken
parameter_list|(
name|String
name|clientToken
parameter_list|)
function_decl|;
DECL|method|getState ()
name|ApplicationState
name|getState
parameter_list|()
function_decl|;
DECL|method|setState (ApplicationState state)
name|void
name|setState
parameter_list|(
name|ApplicationState
name|state
parameter_list|)
function_decl|;
DECL|method|getDiagnostics ()
name|String
name|getDiagnostics
parameter_list|()
function_decl|;
DECL|method|setDiagnostics (String diagnostics)
name|void
name|setDiagnostics
parameter_list|(
name|String
name|diagnostics
parameter_list|)
function_decl|;
DECL|method|getTrackingUrl ()
name|String
name|getTrackingUrl
parameter_list|()
function_decl|;
DECL|method|setTrackingUrl (String url)
name|void
name|setTrackingUrl
parameter_list|(
name|String
name|url
parameter_list|)
function_decl|;
DECL|method|getStartTime ()
name|long
name|getStartTime
parameter_list|()
function_decl|;
DECL|method|setStartTime (long startTime)
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

