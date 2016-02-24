begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.api.records
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
name|api
operator|.
name|records
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
name|Priority
import|;
end_import

begin_interface
DECL|interface|JobReport
specifier|public
interface|interface
name|JobReport
block|{
DECL|method|getJobId ()
specifier|public
specifier|abstract
name|JobId
name|getJobId
parameter_list|()
function_decl|;
DECL|method|getJobState ()
specifier|public
specifier|abstract
name|JobState
name|getJobState
parameter_list|()
function_decl|;
DECL|method|getMapProgress ()
specifier|public
specifier|abstract
name|float
name|getMapProgress
parameter_list|()
function_decl|;
DECL|method|getReduceProgress ()
specifier|public
specifier|abstract
name|float
name|getReduceProgress
parameter_list|()
function_decl|;
DECL|method|getCleanupProgress ()
specifier|public
specifier|abstract
name|float
name|getCleanupProgress
parameter_list|()
function_decl|;
DECL|method|getSetupProgress ()
specifier|public
specifier|abstract
name|float
name|getSetupProgress
parameter_list|()
function_decl|;
DECL|method|getSubmitTime ()
specifier|public
specifier|abstract
name|long
name|getSubmitTime
parameter_list|()
function_decl|;
DECL|method|getStartTime ()
specifier|public
specifier|abstract
name|long
name|getStartTime
parameter_list|()
function_decl|;
DECL|method|getFinishTime ()
specifier|public
specifier|abstract
name|long
name|getFinishTime
parameter_list|()
function_decl|;
DECL|method|getUser ()
specifier|public
specifier|abstract
name|String
name|getUser
parameter_list|()
function_decl|;
DECL|method|getJobName ()
specifier|public
specifier|abstract
name|String
name|getJobName
parameter_list|()
function_decl|;
DECL|method|getTrackingUrl ()
specifier|public
specifier|abstract
name|String
name|getTrackingUrl
parameter_list|()
function_decl|;
DECL|method|getDiagnostics ()
specifier|public
specifier|abstract
name|String
name|getDiagnostics
parameter_list|()
function_decl|;
DECL|method|getJobFile ()
specifier|public
specifier|abstract
name|String
name|getJobFile
parameter_list|()
function_decl|;
DECL|method|getAMInfos ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|AMInfo
argument_list|>
name|getAMInfos
parameter_list|()
function_decl|;
DECL|method|isUber ()
specifier|public
specifier|abstract
name|boolean
name|isUber
parameter_list|()
function_decl|;
DECL|method|getJobPriority ()
specifier|public
specifier|abstract
name|Priority
name|getJobPriority
parameter_list|()
function_decl|;
DECL|method|getHistoryFile ()
specifier|public
specifier|abstract
name|String
name|getHistoryFile
parameter_list|()
function_decl|;
DECL|method|setJobId (JobId jobId)
specifier|public
specifier|abstract
name|void
name|setJobId
parameter_list|(
name|JobId
name|jobId
parameter_list|)
function_decl|;
DECL|method|setJobState (JobState jobState)
specifier|public
specifier|abstract
name|void
name|setJobState
parameter_list|(
name|JobState
name|jobState
parameter_list|)
function_decl|;
DECL|method|setMapProgress (float progress)
specifier|public
specifier|abstract
name|void
name|setMapProgress
parameter_list|(
name|float
name|progress
parameter_list|)
function_decl|;
DECL|method|setReduceProgress (float progress)
specifier|public
specifier|abstract
name|void
name|setReduceProgress
parameter_list|(
name|float
name|progress
parameter_list|)
function_decl|;
DECL|method|setCleanupProgress (float progress)
specifier|public
specifier|abstract
name|void
name|setCleanupProgress
parameter_list|(
name|float
name|progress
parameter_list|)
function_decl|;
DECL|method|setSetupProgress (float progress)
specifier|public
specifier|abstract
name|void
name|setSetupProgress
parameter_list|(
name|float
name|progress
parameter_list|)
function_decl|;
DECL|method|setSubmitTime (long submitTime)
specifier|public
specifier|abstract
name|void
name|setSubmitTime
parameter_list|(
name|long
name|submitTime
parameter_list|)
function_decl|;
DECL|method|setStartTime (long startTime)
specifier|public
specifier|abstract
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
function_decl|;
DECL|method|setFinishTime (long finishTime)
specifier|public
specifier|abstract
name|void
name|setFinishTime
parameter_list|(
name|long
name|finishTime
parameter_list|)
function_decl|;
DECL|method|setUser (String user)
specifier|public
specifier|abstract
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
function_decl|;
DECL|method|setJobName (String jobName)
specifier|public
specifier|abstract
name|void
name|setJobName
parameter_list|(
name|String
name|jobName
parameter_list|)
function_decl|;
DECL|method|setTrackingUrl (String trackingUrl)
specifier|public
specifier|abstract
name|void
name|setTrackingUrl
parameter_list|(
name|String
name|trackingUrl
parameter_list|)
function_decl|;
DECL|method|setDiagnostics (String diagnostics)
specifier|public
specifier|abstract
name|void
name|setDiagnostics
parameter_list|(
name|String
name|diagnostics
parameter_list|)
function_decl|;
DECL|method|setJobFile (String jobFile)
specifier|public
specifier|abstract
name|void
name|setJobFile
parameter_list|(
name|String
name|jobFile
parameter_list|)
function_decl|;
DECL|method|setAMInfos (List<AMInfo> amInfos)
specifier|public
specifier|abstract
name|void
name|setAMInfos
parameter_list|(
name|List
argument_list|<
name|AMInfo
argument_list|>
name|amInfos
parameter_list|)
function_decl|;
DECL|method|setIsUber (boolean isUber)
specifier|public
specifier|abstract
name|void
name|setIsUber
parameter_list|(
name|boolean
name|isUber
parameter_list|)
function_decl|;
DECL|method|setJobPriority (Priority priority)
specifier|public
specifier|abstract
name|void
name|setJobPriority
parameter_list|(
name|Priority
name|priority
parameter_list|)
function_decl|;
DECL|method|setHistoryFile (String historyFile)
specifier|public
specifier|abstract
name|void
name|setHistoryFile
parameter_list|(
name|String
name|historyFile
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

