begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|rmapp
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
name|ApplicationId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationReport
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
name|yarn
operator|.
name|event
operator|.
name|EventHandler
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
operator|.
name|ApplicationsStore
operator|.
name|ApplicationStore
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttempt
import|;
end_import

begin_interface
DECL|interface|RMApp
specifier|public
interface|interface
name|RMApp
extends|extends
name|EventHandler
argument_list|<
name|RMAppEvent
argument_list|>
block|{
DECL|method|getApplicationId ()
name|ApplicationId
name|getApplicationId
parameter_list|()
function_decl|;
DECL|method|getState ()
name|RMAppState
name|getState
parameter_list|()
function_decl|;
DECL|method|getUser ()
name|String
name|getUser
parameter_list|()
function_decl|;
DECL|method|getProgress ()
name|float
name|getProgress
parameter_list|()
function_decl|;
DECL|method|getRMAppAttempt (ApplicationAttemptId appAttemptId)
name|RMAppAttempt
name|getRMAppAttempt
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
function_decl|;
DECL|method|getQueue ()
name|String
name|getQueue
parameter_list|()
function_decl|;
DECL|method|getName ()
name|String
name|getName
parameter_list|()
function_decl|;
DECL|method|getCurrentAppAttempt ()
name|RMAppAttempt
name|getCurrentAppAttempt
parameter_list|()
function_decl|;
DECL|method|createAndGetApplicationReport ()
name|ApplicationReport
name|createAndGetApplicationReport
parameter_list|()
function_decl|;
DECL|method|getApplicationStore ()
name|ApplicationStore
name|getApplicationStore
parameter_list|()
function_decl|;
DECL|method|getFinishTime ()
name|long
name|getFinishTime
parameter_list|()
function_decl|;
DECL|method|getStartTime ()
name|long
name|getStartTime
parameter_list|()
function_decl|;
DECL|method|getTrackingUrl ()
name|String
name|getTrackingUrl
parameter_list|()
function_decl|;
DECL|method|getDiagnostics ()
name|StringBuilder
name|getDiagnostics
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

