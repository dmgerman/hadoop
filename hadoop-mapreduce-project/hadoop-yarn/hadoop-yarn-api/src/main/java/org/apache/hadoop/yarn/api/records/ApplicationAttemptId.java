begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_interface
DECL|interface|ApplicationAttemptId
specifier|public
interface|interface
name|ApplicationAttemptId
extends|extends
name|Comparable
argument_list|<
name|ApplicationAttemptId
argument_list|>
block|{
DECL|method|getApplicationId ()
specifier|public
specifier|abstract
name|ApplicationId
name|getApplicationId
parameter_list|()
function_decl|;
DECL|method|getAttemptId ()
specifier|public
specifier|abstract
name|int
name|getAttemptId
parameter_list|()
function_decl|;
DECL|method|setApplicationId (ApplicationId appID)
specifier|public
specifier|abstract
name|void
name|setApplicationId
parameter_list|(
name|ApplicationId
name|appID
parameter_list|)
function_decl|;
DECL|method|setAttemptId (int attemptId)
specifier|public
specifier|abstract
name|void
name|setAttemptId
parameter_list|(
name|int
name|attemptId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

