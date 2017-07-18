begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.ams
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|ams
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
name|protocolrecords
operator|.
name|AllocateRequest
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
name|protocolrecords
operator|.
name|AllocateResponse
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
name|protocolrecords
operator|.
name|FinishApplicationMasterRequest
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
name|protocolrecords
operator|.
name|FinishApplicationMasterResponse
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
name|protocolrecords
operator|.
name|RegisterApplicationMasterRequest
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
name|protocolrecords
operator|.
name|RegisterApplicationMasterResponse
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
name|exceptions
operator|.
name|YarnException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Interface to abstract out the the actual processing logic of the  * Application Master Service.  */
end_comment

begin_interface
DECL|interface|ApplicationMasterServiceProcessor
specifier|public
interface|interface
name|ApplicationMasterServiceProcessor
block|{
comment|/**    * Initialize with and ApplicationMasterService Context as well as the    * next processor in the chain.    * @param amsContext AMSContext.    * @param nextProcessor next ApplicationMasterServiceProcessor    */
DECL|method|init (ApplicationMasterServiceContext amsContext, ApplicationMasterServiceProcessor nextProcessor)
name|void
name|init
parameter_list|(
name|ApplicationMasterServiceContext
name|amsContext
parameter_list|,
name|ApplicationMasterServiceProcessor
name|nextProcessor
parameter_list|)
function_decl|;
comment|/**    * Register AM attempt.    * @param applicationAttemptId applicationAttemptId.    * @param request Register Request.    * @param response Register Response.    * @throws IOException IOException.    */
DECL|method|registerApplicationMaster ( ApplicationAttemptId applicationAttemptId, RegisterApplicationMasterRequest request, RegisterApplicationMasterResponse response)
name|void
name|registerApplicationMaster
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|,
name|RegisterApplicationMasterRequest
name|request
parameter_list|,
name|RegisterApplicationMasterResponse
name|response
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Allocate call.    * @param appAttemptId appAttemptId.    * @param request Allocate Request.    * @param response Allocate Response.    * @throws YarnException YarnException.    */
DECL|method|allocate (ApplicationAttemptId appAttemptId, AllocateRequest request, AllocateResponse response)
name|void
name|allocate
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|AllocateRequest
name|request
parameter_list|,
name|AllocateResponse
name|response
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Finish AM.    * @param applicationAttemptId applicationAttemptId.    * @param request Finish AM Request.    * @param response Finish AM Response.    */
DECL|method|finishApplicationMaster ( ApplicationAttemptId applicationAttemptId, FinishApplicationMasterRequest request, FinishApplicationMasterResponse response)
name|void
name|finishApplicationMaster
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|,
name|FinishApplicationMasterRequest
name|request
parameter_list|,
name|FinishApplicationMasterResponse
name|response
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

