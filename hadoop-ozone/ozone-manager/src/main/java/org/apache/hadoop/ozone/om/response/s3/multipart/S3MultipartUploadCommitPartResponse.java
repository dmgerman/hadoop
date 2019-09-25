begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OmUtils
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
name|helpers
operator|.
name|OmMultipartKeyInfo
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
name|RepeatedOmKeyInfo
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
name|hdds
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import static
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
name|Status
operator|.
name|NO_SUCH_MULTIPART_UPLOAD_ERROR
import|;
end_import

begin_import
import|import static
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
name|Status
operator|.
name|OK
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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * Response for S3MultipartUploadCommitPart request.  */
end_comment

begin_class
DECL|class|S3MultipartUploadCommitPartResponse
specifier|public
class|class
name|S3MultipartUploadCommitPartResponse
extends|extends
name|OMClientResponse
block|{
DECL|field|multipartKey
specifier|private
name|String
name|multipartKey
decl_stmt|;
DECL|field|openKey
specifier|private
name|String
name|openKey
decl_stmt|;
DECL|field|deletePartKeyInfo
specifier|private
name|OmKeyInfo
name|deletePartKeyInfo
decl_stmt|;
DECL|field|omMultipartKeyInfo
specifier|private
name|OmMultipartKeyInfo
name|omMultipartKeyInfo
decl_stmt|;
DECL|field|oldMultipartKeyInfo
specifier|private
name|OzoneManagerProtocolProtos
operator|.
name|PartKeyInfo
name|oldMultipartKeyInfo
decl_stmt|;
DECL|method|S3MultipartUploadCommitPartResponse (String multipartKey, String openKey, @Nullable OmKeyInfo deletePartKeyInfo, @Nullable OmMultipartKeyInfo omMultipartKeyInfo, @Nullable OzoneManagerProtocolProtos.PartKeyInfo oldPartKeyInfo, @Nonnull OMResponse omResponse)
specifier|public
name|S3MultipartUploadCommitPartResponse
parameter_list|(
name|String
name|multipartKey
parameter_list|,
name|String
name|openKey
parameter_list|,
annotation|@
name|Nullable
name|OmKeyInfo
name|deletePartKeyInfo
parameter_list|,
annotation|@
name|Nullable
name|OmMultipartKeyInfo
name|omMultipartKeyInfo
parameter_list|,
annotation|@
name|Nullable
name|OzoneManagerProtocolProtos
operator|.
name|PartKeyInfo
name|oldPartKeyInfo
parameter_list|,
annotation|@
name|Nonnull
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
name|openKey
operator|=
name|openKey
expr_stmt|;
name|this
operator|.
name|deletePartKeyInfo
operator|=
name|deletePartKeyInfo
expr_stmt|;
name|this
operator|.
name|omMultipartKeyInfo
operator|=
name|omMultipartKeyInfo
expr_stmt|;
name|this
operator|.
name|oldMultipartKeyInfo
operator|=
name|oldPartKeyInfo
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
name|NO_SUCH_MULTIPART_UPLOAD_ERROR
condition|)
block|{
comment|// Means by the time we try to commit part, some one has aborted this
comment|// multipart upload. So, delete this part information.
name|RepeatedOmKeyInfo
name|repeatedOmKeyInfo
init|=
name|omMetadataManager
operator|.
name|getDeletedTable
argument_list|()
operator|.
name|get
argument_list|(
name|openKey
argument_list|)
decl_stmt|;
name|repeatedOmKeyInfo
operator|=
name|OmUtils
operator|.
name|prepareKeyForDelete
argument_list|(
name|deletePartKeyInfo
argument_list|,
name|repeatedOmKeyInfo
argument_list|)
expr_stmt|;
name|omMetadataManager
operator|.
name|getDeletedTable
argument_list|()
operator|.
name|putWithBatch
argument_list|(
name|batchOperation
argument_list|,
name|openKey
argument_list|,
name|repeatedOmKeyInfo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getOMResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|==
name|OK
condition|)
block|{
comment|// If we have old part info:
comment|// Need to do 3 steps:
comment|//   0. Strip GDPR related metadata from multipart info
comment|//   1. add old part to delete table
comment|//   2. Commit multipart info which has information about this new part.
comment|//   3. delete this new part entry from open key table.
comment|// This means for this multipart upload part upload, we have an old
comment|// part information, so delete it.
if|if
condition|(
name|oldMultipartKeyInfo
operator|!=
literal|null
condition|)
block|{
name|OmKeyInfo
name|partKey
init|=
name|OmKeyInfo
operator|.
name|getFromProtobuf
argument_list|(
name|oldMultipartKeyInfo
operator|.
name|getPartKeyInfo
argument_list|()
argument_list|)
decl_stmt|;
name|RepeatedOmKeyInfo
name|repeatedOmKeyInfo
init|=
name|omMetadataManager
operator|.
name|getDeletedTable
argument_list|()
operator|.
name|get
argument_list|(
name|oldMultipartKeyInfo
operator|.
name|getPartName
argument_list|()
argument_list|)
decl_stmt|;
name|repeatedOmKeyInfo
operator|=
name|OmUtils
operator|.
name|prepareKeyForDelete
argument_list|(
name|partKey
argument_list|,
name|repeatedOmKeyInfo
argument_list|)
expr_stmt|;
name|omMetadataManager
operator|.
name|getDeletedTable
argument_list|()
operator|.
name|putWithBatch
argument_list|(
name|batchOperation
argument_list|,
name|oldMultipartKeyInfo
operator|.
name|getPartName
argument_list|()
argument_list|,
name|repeatedOmKeyInfo
argument_list|)
expr_stmt|;
block|}
name|omMetadataManager
operator|.
name|getMultipartInfoTable
argument_list|()
operator|.
name|putWithBatch
argument_list|(
name|batchOperation
argument_list|,
name|multipartKey
argument_list|,
name|omMultipartKeyInfo
argument_list|)
expr_stmt|;
comment|//  This information has been added to multipartKeyInfo. So, we can
comment|//  safely delete part key info from open key table.
name|omMetadataManager
operator|.
name|getOpenKeyTable
argument_list|()
operator|.
name|deleteWithBatch
argument_list|(
name|batchOperation
argument_list|,
name|openKey
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

