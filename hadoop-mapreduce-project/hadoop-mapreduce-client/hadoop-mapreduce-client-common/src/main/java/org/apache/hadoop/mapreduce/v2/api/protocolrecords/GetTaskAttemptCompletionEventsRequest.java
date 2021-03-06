begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.api.protocolrecords
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

begin_interface
DECL|interface|GetTaskAttemptCompletionEventsRequest
specifier|public
interface|interface
name|GetTaskAttemptCompletionEventsRequest
block|{
DECL|method|getJobId ()
specifier|public
specifier|abstract
name|JobId
name|getJobId
parameter_list|()
function_decl|;
DECL|method|getFromEventId ()
specifier|public
specifier|abstract
name|int
name|getFromEventId
parameter_list|()
function_decl|;
DECL|method|getMaxEvents ()
specifier|public
specifier|abstract
name|int
name|getMaxEvents
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
DECL|method|setFromEventId (int id)
specifier|public
specifier|abstract
name|void
name|setFromEventId
parameter_list|(
name|int
name|id
parameter_list|)
function_decl|;
DECL|method|setMaxEvents (int maxEvents)
specifier|public
specifier|abstract
name|void
name|setMaxEvents
parameter_list|(
name|int
name|maxEvents
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

