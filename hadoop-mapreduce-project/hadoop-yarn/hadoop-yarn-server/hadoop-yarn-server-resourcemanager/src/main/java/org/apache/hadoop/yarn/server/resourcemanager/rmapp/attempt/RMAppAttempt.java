begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt
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
operator|.
name|attempt
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|ApplicationSubmissionContext
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
name|Container
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
name|NodeId
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

begin_interface
DECL|interface|RMAppAttempt
specifier|public
interface|interface
name|RMAppAttempt
extends|extends
name|EventHandler
argument_list|<
name|RMAppAttemptEvent
argument_list|>
block|{
DECL|method|getAppAttemptId ()
name|ApplicationAttemptId
name|getAppAttemptId
parameter_list|()
function_decl|;
DECL|method|getAppAttemptState ()
name|RMAppAttemptState
name|getAppAttemptState
parameter_list|()
function_decl|;
DECL|method|getHost ()
name|String
name|getHost
parameter_list|()
function_decl|;
DECL|method|getRpcPort ()
name|int
name|getRpcPort
parameter_list|()
function_decl|;
DECL|method|getTrackingUrl ()
name|String
name|getTrackingUrl
parameter_list|()
function_decl|;
DECL|method|getClientToken ()
name|String
name|getClientToken
parameter_list|()
function_decl|;
DECL|method|getDiagnostics ()
name|StringBuilder
name|getDiagnostics
parameter_list|()
function_decl|;
DECL|method|getProgress ()
name|float
name|getProgress
parameter_list|()
function_decl|;
DECL|method|getRanNodes ()
name|Set
argument_list|<
name|NodeId
argument_list|>
name|getRanNodes
parameter_list|()
function_decl|;
DECL|method|pullJustFinishedContainers ()
name|List
argument_list|<
name|Container
argument_list|>
name|pullJustFinishedContainers
parameter_list|()
function_decl|;
DECL|method|getJustFinishedContainers ()
name|List
argument_list|<
name|Container
argument_list|>
name|getJustFinishedContainers
parameter_list|()
function_decl|;
DECL|method|getMasterContainer ()
name|Container
name|getMasterContainer
parameter_list|()
function_decl|;
DECL|method|getSubmissionContext ()
name|ApplicationSubmissionContext
name|getSubmissionContext
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

