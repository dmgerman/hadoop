begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.request
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|request
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|ozone
operator|.
name|om
operator|.
name|OzoneManager
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
name|ozone
operator|.
name|om
operator|.
name|response
operator|.
name|OMClientResponse
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|OMRequest
import|;
end_import

begin_comment
comment|/**  * OMClientRequest provides methods which every write OM request should  * implement.  */
end_comment

begin_class
DECL|class|OMClientRequest
specifier|public
specifier|abstract
class|class
name|OMClientRequest
block|{
DECL|field|omRequest
specifier|private
specifier|final
name|OMRequest
name|omRequest
decl_stmt|;
DECL|method|OMClientRequest (OMRequest omRequest)
specifier|public
name|OMClientRequest
parameter_list|(
name|OMRequest
name|omRequest
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|omRequest
argument_list|)
expr_stmt|;
name|this
operator|.
name|omRequest
operator|=
name|omRequest
expr_stmt|;
block|}
comment|/**    * Perform pre-execute steps on a OMRequest.    *    * Called from the RPC context, and generates a OMRequest object which has    * all the information that will be either persisted    * in RocksDB or returned to the caller once this operation    * is executed.    *    * @return OMRequest that will be serialized and handed off to Ratis for    *         consensus.    */
DECL|method|preExecute (OzoneManager ozoneManager)
specifier|public
specifier|abstract
name|OMRequest
name|preExecute
parameter_list|(
name|OzoneManager
name|ozoneManager
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Validate the OMRequest and update the cache.    * This step should verify that the request can be executed, perform    * any authorization steps and update the in-memory cache.     * This step does not persist the changes to the database.    *    * @return the response that will be returned to the client.    */
DECL|method|validateAndUpdateCache ( OzoneManager ozoneManager, long transactionLogIndex)
specifier|public
specifier|abstract
name|OMClientResponse
name|validateAndUpdateCache
parameter_list|(
name|OzoneManager
name|ozoneManager
parameter_list|,
name|long
name|transactionLogIndex
parameter_list|)
function_decl|;
annotation|@
name|VisibleForTesting
DECL|method|getOmRequest ()
specifier|public
name|OMRequest
name|getOmRequest
parameter_list|()
block|{
return|return
name|omRequest
return|;
block|}
block|}
end_class

end_unit

