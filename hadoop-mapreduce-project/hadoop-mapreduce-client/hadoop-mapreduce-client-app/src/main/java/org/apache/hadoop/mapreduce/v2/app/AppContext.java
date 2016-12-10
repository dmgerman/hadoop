begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|classification
operator|.
name|InterfaceAudience
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobId
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|Job
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
name|event
operator|.
name|Event
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
name|security
operator|.
name|client
operator|.
name|ClientToAMTokenSecretManager
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
name|util
operator|.
name|Clock
import|;
end_import

begin_comment
comment|/**  * Context interface for sharing information across components in YARN App.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|AppContext
specifier|public
interface|interface
name|AppContext
block|{
DECL|method|getApplicationID ()
name|ApplicationId
name|getApplicationID
parameter_list|()
function_decl|;
DECL|method|getApplicationAttemptId ()
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
function_decl|;
DECL|method|getApplicationName ()
name|String
name|getApplicationName
parameter_list|()
function_decl|;
DECL|method|getStartTime ()
name|long
name|getStartTime
parameter_list|()
function_decl|;
DECL|method|getUser ()
name|CharSequence
name|getUser
parameter_list|()
function_decl|;
DECL|method|getJob (JobId jobID)
name|Job
name|getJob
parameter_list|(
name|JobId
name|jobID
parameter_list|)
function_decl|;
DECL|method|getAllJobs ()
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|getAllJobs
parameter_list|()
function_decl|;
DECL|method|getEventHandler ()
name|EventHandler
argument_list|<
name|Event
argument_list|>
name|getEventHandler
parameter_list|()
function_decl|;
DECL|method|getClock ()
name|Clock
name|getClock
parameter_list|()
function_decl|;
DECL|method|getClusterInfo ()
name|ClusterInfo
name|getClusterInfo
parameter_list|()
function_decl|;
DECL|method|getBlacklistedNodes ()
name|Set
argument_list|<
name|String
argument_list|>
name|getBlacklistedNodes
parameter_list|()
function_decl|;
DECL|method|getClientToAMTokenSecretManager ()
name|ClientToAMTokenSecretManager
name|getClientToAMTokenSecretManager
parameter_list|()
function_decl|;
DECL|method|isLastAMRetry ()
name|boolean
name|isLastAMRetry
parameter_list|()
function_decl|;
DECL|method|hasSuccessfullyUnregistered ()
name|boolean
name|hasSuccessfullyUnregistered
parameter_list|()
function_decl|;
DECL|method|getNMHostname ()
name|String
name|getNMHostname
parameter_list|()
function_decl|;
DECL|method|getTaskAttemptFinishingMonitor ()
name|TaskAttemptFinishingMonitor
name|getTaskAttemptFinishingMonitor
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

