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
name|Private
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
name|Unstable
import|;
end_import

begin_comment
comment|/**  *<em>For internal use only...</em>  */
end_comment

begin_interface
annotation|@
name|Private
annotation|@
name|Unstable
DECL|interface|ApplicationMaster
specifier|public
interface|interface
name|ApplicationMaster
block|{
DECL|method|getApplicationId ()
name|ApplicationId
name|getApplicationId
parameter_list|()
function_decl|;
DECL|method|setApplicationId (ApplicationId appId)
name|void
name|setApplicationId
parameter_list|(
name|ApplicationId
name|appId
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
DECL|method|getStatus ()
name|ApplicationStatus
name|getStatus
parameter_list|()
function_decl|;
DECL|method|setStatus (ApplicationStatus status)
name|void
name|setStatus
parameter_list|(
name|ApplicationStatus
name|status
parameter_list|)
function_decl|;
DECL|method|getState ()
name|YarnApplicationState
name|getState
parameter_list|()
function_decl|;
DECL|method|setState (YarnApplicationState state)
name|void
name|setState
parameter_list|(
name|YarnApplicationState
name|state
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
DECL|method|getAMFailCount ()
name|int
name|getAMFailCount
parameter_list|()
function_decl|;
DECL|method|setAMFailCount (int amFailCount)
name|void
name|setAMFailCount
parameter_list|(
name|int
name|amFailCount
parameter_list|)
function_decl|;
DECL|method|getContainerCount ()
name|int
name|getContainerCount
parameter_list|()
function_decl|;
DECL|method|setContainerCount (int containerCount)
name|void
name|setContainerCount
parameter_list|(
name|int
name|containerCount
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
block|}
end_interface

end_unit

