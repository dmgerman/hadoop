begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.response.s3.multipart
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
operator|.
name|s3
operator|.
name|multipart
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
name|om
operator|.
name|helpers
operator|.
name|OmKeyInfo
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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Response for Multipart Upload Complete request.  */
end_comment

begin_class
DECL|class|S3MultipartUploadCompleteResponse
specifier|public
class|class
name|S3MultipartUploadCompleteResponse
extends|extends
name|OMClientResponse
block|{
DECL|field|multipartKey
specifier|private
name|String
name|multipartKey
decl_stmt|;
DECL|field|omKeyInfo
specifier|private
name|OmKeyInfo
name|omKeyInfo
decl_stmt|;
DECL|method|S3MultipartUploadCompleteResponse (@ullable String multipartKey, @Nullable OmKeyInfo omKeyInfo, OMResponse omResponse)
specifier|public
name|S3MultipartUploadCompleteResponse
parameter_list|(
annotation|@
name|Nullable
name|String
name|multipartKey
parameter_list|,
annotation|@
name|Nullable
name|OmKeyInfo
name|omKeyInfo
parameter_list|,
name|OMResponse
name|omResponse
parameter_list|)
block|{
name|super
argument_list|(
name|omResponse
argument_list|)
expr_stmt|;
name|this
operator|.
name|multipartKey
operator|=
name|multipartKey
expr_stmt|;
name|this
operator|.
name|omKeyInfo
operator|=
name|omKeyInfo
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addToDBBatch (OMMetadataManager omMetadataManager, BatchOperation batchOperation)
specifier|public
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
block|{
if|if
condition|(
name|getOMResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|==
name|OzoneManagerProtocolProtos
operator|.
name|Status
operator|.
name|OK
condition|)
block|{
name|omMetadataManager
operator|.
name|getKeyTable
argument_list|()
operator|.
name|putWithBatch
argument_list|(
name|batchOperation
argument_list|,
name|omMetadataManager
operator|.
name|getOzoneKey
argument_list|(
name|omKeyInfo
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|omKeyInfo
operator|.
name|getBucketName
argument_list|()
argument_list|,
name|omKeyInfo
operator|.
name|getKeyName
argument_list|()
argument_list|)
argument_list|,
name|omKeyInfo
argument_list|)
expr_stmt|;
name|omMetadataManager
operator|.
name|getOpenKeyTable
argument_list|()
operator|.
name|deleteWithBatch
argument_list|(
name|batchOperation
argument_list|,
name|multipartKey
argument_list|)
expr_stmt|;
name|omMetadataManager
operator|.
name|getMultipartInfoTable
argument_list|()
operator|.
name|deleteWithBatch
argument_list|(
name|batchOperation
argument_list|,
name|multipartKey
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

