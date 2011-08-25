begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords
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
name|protocolrecords
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
import|;
end_import

begin_interface
DECL|interface|FinishApplicationMasterRequest
specifier|public
interface|interface
name|FinishApplicationMasterRequest
block|{
DECL|method|getApplicationAttemptId ()
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
function_decl|;
DECL|method|setAppAttemptId (ApplicationAttemptId applicationAttemptId)
name|void
name|setAppAttemptId
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|)
function_decl|;
DECL|method|getFinalState ()
name|String
name|getFinalState
parameter_list|()
function_decl|;
DECL|method|setFinalState (String string)
name|void
name|setFinalState
parameter_list|(
name|String
name|string
parameter_list|)
function_decl|;
DECL|method|getDiagnostics ()
name|String
name|getDiagnostics
parameter_list|()
function_decl|;
DECL|method|setDiagnostics (String string)
name|void
name|setDiagnostics
parameter_list|(
name|String
name|string
parameter_list|)
function_decl|;
DECL|method|getTrackingUrl ()
name|String
name|getTrackingUrl
parameter_list|()
function_decl|;
DECL|method|setTrackingUrl (String historyUrl)
name|void
name|setTrackingUrl
parameter_list|(
name|String
name|historyUrl
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

