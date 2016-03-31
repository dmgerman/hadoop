begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
package|;
end_package

begin_comment
comment|/**  * Provides metrics for Decay scheduler.  */
end_comment

begin_interface
DECL|interface|DecayRpcSchedulerMXBean
specifier|public
interface|interface
name|DecayRpcSchedulerMXBean
block|{
comment|// Get an overview of the requests in history.
DECL|method|getSchedulingDecisionSummary ()
name|String
name|getSchedulingDecisionSummary
parameter_list|()
function_decl|;
DECL|method|getCallVolumeSummary ()
name|String
name|getCallVolumeSummary
parameter_list|()
function_decl|;
DECL|method|getUniqueIdentityCount ()
name|int
name|getUniqueIdentityCount
parameter_list|()
function_decl|;
DECL|method|getTotalCallVolume ()
name|long
name|getTotalCallVolume
parameter_list|()
function_decl|;
DECL|method|getAverageResponseTime ()
name|double
index|[]
name|getAverageResponseTime
parameter_list|()
function_decl|;
DECL|method|getResponseTimeCountInLastWindow ()
name|long
index|[]
name|getResponseTimeCountInLastWindow
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

