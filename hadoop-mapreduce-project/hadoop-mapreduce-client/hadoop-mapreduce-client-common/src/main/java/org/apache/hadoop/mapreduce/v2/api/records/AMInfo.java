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
name|ContainerId
import|;
end_import

begin_interface
DECL|interface|AMInfo
specifier|public
interface|interface
name|AMInfo
block|{
DECL|method|getAppAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getAppAttemptId
parameter_list|()
function_decl|;
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
function_decl|;
DECL|method|getContainerId ()
specifier|public
name|ContainerId
name|getContainerId
parameter_list|()
function_decl|;
DECL|method|getNodeManagerHost ()
specifier|public
name|String
name|getNodeManagerHost
parameter_list|()
function_decl|;
DECL|method|getNodeManagerPort ()
specifier|public
name|int
name|getNodeManagerPort
parameter_list|()
function_decl|;
DECL|method|getNodeManagerHttpPort ()
specifier|public
name|int
name|getNodeManagerHttpPort
parameter_list|()
function_decl|;
DECL|method|setAppAttemptId (ApplicationAttemptId appAttemptId)
specifier|public
name|void
name|setAppAttemptId
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
function_decl|;
DECL|method|setStartTime (long startTime)
specifier|public
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
function_decl|;
DECL|method|setContainerId (ContainerId containerId)
specifier|public
name|void
name|setContainerId
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
function_decl|;
DECL|method|setNodeManagerHost (String nmHost)
specifier|public
name|void
name|setNodeManagerHost
parameter_list|(
name|String
name|nmHost
parameter_list|)
function_decl|;
DECL|method|setNodeManagerPort (int nmPort)
specifier|public
name|void
name|setNodeManagerPort
parameter_list|(
name|int
name|nmPort
parameter_list|)
function_decl|;
DECL|method|setNodeManagerHttpPort (int mnHttpPort)
specifier|public
name|void
name|setNodeManagerHttpPort
parameter_list|(
name|int
name|mnHttpPort
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

