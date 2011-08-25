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
block|}
end_interface

end_unit

