begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.response
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
name|response
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
name|OMMetadataManager
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
name|OMResponse
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
name|utils
operator|.
name|db
operator|.
name|BatchOperation
import|;
end_import

begin_comment
comment|/**  * Interface for OM Responses, each OM response should implement this interface.  */
end_comment

begin_class
DECL|class|OMClientResponse
specifier|public
specifier|abstract
class|class
name|OMClientResponse
block|{
DECL|field|omResponse
specifier|private
name|OMResponse
name|omResponse
decl_stmt|;
DECL|method|OMClientResponse (OMResponse omResponse)
specifier|public
name|OMClientResponse
parameter_list|(
name|OMResponse
name|omResponse
parameter_list|)
block|{
name|this
operator|.
name|omResponse
operator|=
name|omResponse
expr_stmt|;
block|}
comment|/**    * Implement logic to add the response to batch.    * @param omMetadataManager    * @param batchOperation    * @throws IOException    */
DECL|method|addToDBBatch (OMMetadataManager omMetadataManager, BatchOperation batchOperation)
specifier|public
specifier|abstract
name|void
name|addToDBBatch
parameter_list|(
name|OMMetadataManager
name|omMetadataManager
parameter_list|,
name|BatchOperation
name|batchOperation
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Return OMResponse.    * @return OMResponse    */
DECL|method|getOMResponse ()
specifier|public
name|OMResponse
name|getOMResponse
parameter_list|()
block|{
return|return
name|omResponse
return|;
block|}
block|}
end_class

end_unit

