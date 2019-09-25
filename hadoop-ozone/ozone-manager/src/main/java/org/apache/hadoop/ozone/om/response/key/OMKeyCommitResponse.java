begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.response.key
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
name|key
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
comment|/**  * Response for CommitKey request.  */
end_comment

begin_class
DECL|class|OMKeyCommitResponse
specifier|public
class|class
name|OMKeyCommitResponse
extends|extends
name|OMClientResponse
block|{
DECL|field|omKeyInfo
specifier|private
name|OmKeyInfo
name|omKeyInfo
decl_stmt|;
DECL|field|openKeySessionID
specifier|private
name|long
name|openKeySessionID
decl_stmt|;
DECL|method|OMKeyCommitResponse (@ullable OmKeyInfo omKeyInfo, long openKeySessionID, @Nonnull OzoneManagerProtocolProtos.OMResponse omResponse)
specifier|public
name|OMKeyCommitResponse
parameter_list|(
annotation|@
name|Nullable
name|OmKeyInfo
name|omKeyInfo
parameter_list|,
name|long
name|openKeySessionID
parameter_list|,
annotation|@
name|Nonnull
name|OzoneManagerProtocolProtos
operator|.
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
name|omKeyInfo
operator|=
name|omKeyInfo
expr_stmt|;
name|this
operator|.
name|openKeySessionID
operator|=
name|openKeySessionID
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
comment|// For OmResponse with failure, this should do nothing. This method is
comment|// not called in failure scenario in OM code.
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
name|String
name|volumeName
init|=
name|omKeyInfo
operator|.
name|getVolumeName
argument_list|()
decl_stmt|;
name|String
name|bucketName
init|=
name|omKeyInfo
operator|.
name|getBucketName
argument_list|()
decl_stmt|;
name|String
name|keyName
init|=
name|omKeyInfo
operator|.
name|getKeyName
argument_list|()
decl_stmt|;
name|String
name|openKey
init|=
name|omMetadataManager
operator|.
name|getOpenKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|openKeySessionID
argument_list|)
decl_stmt|;
name|String
name|ozoneKey
init|=
name|omMetadataManager
operator|.
name|getOzoneKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|)
decl_stmt|;
comment|// Delete from open key table and add entry to key table.
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
name|omMetadataManager
operator|.
name|getKeyTable
argument_list|()
operator|.
name|putWithBatch
argument_list|(
name|batchOperation
argument_list|,
name|ozoneKey
argument_list|,
name|omKeyInfo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

