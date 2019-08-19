begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.request.s3.multipart
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
operator|.
name|s3
operator|.
name|multipart
package|;
end_package

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
name|Optional
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
name|audit
operator|.
name|OMAction
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
name|OmKeyLocationInfoGroup
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
name|OzoneAclUtil
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
name|ratis
operator|.
name|utils
operator|.
name|OzoneManagerDoubleBufferHelper
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
name|request
operator|.
name|key
operator|.
name|OMKeyRequest
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
name|om
operator|.
name|response
operator|.
name|s3
operator|.
name|multipart
operator|.
name|S3InitiateMultipartUploadResponse
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
name|MultipartInfoInitiateRequest
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
name|MultipartInfoInitiateResponse
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
name|ozone
operator|.
name|security
operator|.
name|acl
operator|.
name|IAccessAuthorizer
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
name|security
operator|.
name|acl
operator|.
name|OzoneObj
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
name|util
operator|.
name|Time
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
name|UniqueId
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
name|cache
operator|.
name|CacheKey
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
name|cache
operator|.
name|CacheValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|om
operator|.
name|lock
operator|.
name|OzoneManagerLock
operator|.
name|Resource
operator|.
name|BUCKET_LOCK
import|;
end_import

begin_comment
comment|/**  * Handles initiate multipart upload request.  */
end_comment

begin_class
DECL|class|S3InitiateMultipartUploadRequest
specifier|public
class|class
name|S3InitiateMultipartUploadRequest
extends|extends
name|OMKeyRequest
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|S3InitiateMultipartUploadRequest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|S3InitiateMultipartUploadRequest (OMRequest omRequest)
specifier|public
name|S3InitiateMultipartUploadRequest
parameter_list|(
name|OMRequest
name|omRequest
parameter_list|)
block|{
name|super
argument_list|(
name|omRequest
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|preExecute (OzoneManager ozoneManager)
specifier|public
name|OMRequest
name|preExecute
parameter_list|(
name|OzoneManager
name|ozoneManager
parameter_list|)
block|{
name|MultipartInfoInitiateRequest
name|multipartInfoInitiateRequest
init|=
name|getOmRequest
argument_list|()
operator|.
name|getInitiateMultiPartUploadRequest
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|multipartInfoInitiateRequest
argument_list|)
expr_stmt|;
name|OzoneManagerProtocolProtos
operator|.
name|KeyArgs
operator|.
name|Builder
name|newKeyArgs
init|=
name|multipartInfoInitiateRequest
operator|.
name|getKeyArgs
argument_list|()
operator|.
name|toBuilder
argument_list|()
operator|.
name|setMultipartUploadID
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"-"
operator|+
name|UniqueId
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|setModificationTime
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|getOmRequest
argument_list|()
operator|.
name|toBuilder
argument_list|()
operator|.
name|setUserInfo
argument_list|(
name|getUserInfo
argument_list|()
argument_list|)
operator|.
name|setInitiateMultiPartUploadRequest
argument_list|(
name|multipartInfoInitiateRequest
operator|.
name|toBuilder
argument_list|()
operator|.
name|setKeyArgs
argument_list|(
name|newKeyArgs
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|validateAndUpdateCache (OzoneManager ozoneManager, long transactionLogIndex, OzoneManagerDoubleBufferHelper ozoneManagerDoubleBufferHelper)
specifier|public
name|OMClientResponse
name|validateAndUpdateCache
parameter_list|(
name|OzoneManager
name|ozoneManager
parameter_list|,
name|long
name|transactionLogIndex
parameter_list|,
name|OzoneManagerDoubleBufferHelper
name|ozoneManagerDoubleBufferHelper
parameter_list|)
block|{
name|MultipartInfoInitiateRequest
name|multipartInfoInitiateRequest
init|=
name|getOmRequest
argument_list|()
operator|.
name|getInitiateMultiPartUploadRequest
argument_list|()
decl_stmt|;
name|OzoneManagerProtocolProtos
operator|.
name|KeyArgs
name|keyArgs
init|=
name|multipartInfoInitiateRequest
operator|.
name|getKeyArgs
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|keyArgs
operator|.
name|getMultipartUploadID
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|volumeName
init|=
name|keyArgs
operator|.
name|getVolumeName
argument_list|()
decl_stmt|;
name|String
name|bucketName
init|=
name|keyArgs
operator|.
name|getBucketName
argument_list|()
decl_stmt|;
name|String
name|keyName
init|=
name|keyArgs
operator|.
name|getKeyName
argument_list|()
decl_stmt|;
name|OMMetadataManager
name|omMetadataManager
init|=
name|ozoneManager
operator|.
name|getMetadataManager
argument_list|()
decl_stmt|;
name|ozoneManager
operator|.
name|getMetrics
argument_list|()
operator|.
name|incNumInitiateMultipartUploads
argument_list|()
expr_stmt|;
name|boolean
name|acquiredBucketLock
init|=
literal|false
decl_stmt|;
name|IOException
name|exception
init|=
literal|null
decl_stmt|;
name|OmMultipartKeyInfo
name|multipartKeyInfo
init|=
literal|null
decl_stmt|;
name|OmKeyInfo
name|omKeyInfo
init|=
literal|null
decl_stmt|;
name|OMResponse
operator|.
name|Builder
name|omResponse
init|=
name|OMResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Type
operator|.
name|InitiateMultiPartUpload
argument_list|)
operator|.
name|setStatus
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setSuccess
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|OMClientResponse
name|omClientResponse
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// check Acl
if|if
condition|(
name|ozoneManager
operator|.
name|getAclsEnabled
argument_list|()
condition|)
block|{
name|checkAcls
argument_list|(
name|ozoneManager
argument_list|,
name|OzoneObj
operator|.
name|ResourceType
operator|.
name|KEY
argument_list|,
name|OzoneObj
operator|.
name|StoreType
operator|.
name|OZONE
argument_list|,
name|IAccessAuthorizer
operator|.
name|ACLType
operator|.
name|WRITE
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|)
expr_stmt|;
block|}
name|acquiredBucketLock
operator|=
name|omMetadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireLock
argument_list|(
name|BUCKET_LOCK
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
name|validateBucketAndVolume
argument_list|(
name|omMetadataManager
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
comment|// We are adding uploadId to key, because if multiple users try to
comment|// perform multipart upload on the same key, each will try to upload, who
comment|// ever finally commit the key, we see that key in ozone. Suppose if we
comment|// don't add id, and use the same key /volume/bucket/key, when multiple
comment|// users try to upload the key, we update the parts of the key's from
comment|// multiple users to same key, and the key output can be a mix of the
comment|// parts from multiple users.
comment|// So on same key if multiple time multipart upload is initiated we
comment|// store multiple entries in the openKey Table.
comment|// Checked AWS S3, when we try to run multipart upload, each time a
comment|// new uploadId is returned. And also even if a key exist when initiate
comment|// multipart upload request is received, it returns multipart upload id
comment|// for the key.
name|String
name|multipartKey
init|=
name|omMetadataManager
operator|.
name|getMultipartKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|keyArgs
operator|.
name|getMultipartUploadID
argument_list|()
argument_list|)
decl_stmt|;
comment|// Not checking if there is an already key for this in the keyTable, as
comment|// during final complete multipart upload we take care of this. AWS S3
comment|// behavior is also like this, even when key exists in a bucket, user
comment|// can still initiate MPU.
name|multipartKeyInfo
operator|=
operator|new
name|OmMultipartKeyInfo
argument_list|(
name|keyArgs
operator|.
name|getMultipartUploadID
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|omKeyInfo
operator|=
operator|new
name|OmKeyInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|keyArgs
operator|.
name|getVolumeName
argument_list|()
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|keyArgs
operator|.
name|getBucketName
argument_list|()
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|keyArgs
operator|.
name|getKeyName
argument_list|()
argument_list|)
operator|.
name|setCreationTime
argument_list|(
name|keyArgs
operator|.
name|getModificationTime
argument_list|()
argument_list|)
operator|.
name|setModificationTime
argument_list|(
name|keyArgs
operator|.
name|getModificationTime
argument_list|()
argument_list|)
operator|.
name|setReplicationType
argument_list|(
name|keyArgs
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
name|keyArgs
operator|.
name|getFactor
argument_list|()
argument_list|)
operator|.
name|setOmKeyLocationInfos
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|OmKeyLocationInfoGroup
argument_list|(
literal|0
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|setAcls
argument_list|(
name|OzoneAclUtil
operator|.
name|fromProtobuf
argument_list|(
name|keyArgs
operator|.
name|getAclsList
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// Add to cache
name|omMetadataManager
operator|.
name|getOpenKeyTable
argument_list|()
operator|.
name|addCacheEntry
argument_list|(
operator|new
name|CacheKey
argument_list|<>
argument_list|(
name|multipartKey
argument_list|)
argument_list|,
operator|new
name|CacheValue
argument_list|<>
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|omKeyInfo
argument_list|)
argument_list|,
name|transactionLogIndex
argument_list|)
argument_list|)
expr_stmt|;
name|omMetadataManager
operator|.
name|getMultipartInfoTable
argument_list|()
operator|.
name|addCacheEntry
argument_list|(
operator|new
name|CacheKey
argument_list|<>
argument_list|(
name|multipartKey
argument_list|)
argument_list|,
operator|new
name|CacheValue
argument_list|<>
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|multipartKeyInfo
argument_list|)
argument_list|,
name|transactionLogIndex
argument_list|)
argument_list|)
expr_stmt|;
name|omClientResponse
operator|=
operator|new
name|S3InitiateMultipartUploadResponse
argument_list|(
name|multipartKeyInfo
argument_list|,
name|omKeyInfo
argument_list|,
name|omResponse
operator|.
name|setInitiateMultiPartUploadResponse
argument_list|(
name|MultipartInfoInitiateResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|bucketName
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|keyName
argument_list|)
operator|.
name|setMultipartUploadID
argument_list|(
name|keyArgs
operator|.
name|getMultipartUploadID
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|exception
operator|=
name|ex
expr_stmt|;
name|omClientResponse
operator|=
operator|new
name|S3InitiateMultipartUploadResponse
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|createErrorOMResponse
argument_list|(
name|omResponse
argument_list|,
name|exception
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|omClientResponse
operator|!=
literal|null
condition|)
block|{
name|omClientResponse
operator|.
name|setFlushFuture
argument_list|(
name|ozoneManagerDoubleBufferHelper
operator|.
name|add
argument_list|(
name|omClientResponse
argument_list|,
name|transactionLogIndex
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|acquiredBucketLock
condition|)
block|{
name|omMetadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseLock
argument_list|(
name|BUCKET_LOCK
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
block|}
block|}
comment|// audit log
name|auditLog
argument_list|(
name|ozoneManager
operator|.
name|getAuditLogger
argument_list|()
argument_list|,
name|buildAuditMessage
argument_list|(
name|OMAction
operator|.
name|INITIATE_MULTIPART_UPLOAD
argument_list|,
name|buildKeyArgsAuditMap
argument_list|(
name|keyArgs
argument_list|)
argument_list|,
name|exception
argument_list|,
name|getOmRequest
argument_list|()
operator|.
name|getUserInfo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"S3 InitiateMultipart Upload request for Key {} in "
operator|+
literal|"Volume/Bucket {}/{} is successfully completed"
argument_list|,
name|keyName
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
return|return
name|omClientResponse
return|;
block|}
else|else
block|{
name|ozoneManager
operator|.
name|getMetrics
argument_list|()
operator|.
name|incNumInitiateMultipartUploadFails
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"S3 InitiateMultipart Upload request for Key {} in "
operator|+
literal|"Volume/Bucket {}/{} is failed"
argument_list|,
name|keyName
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|exception
argument_list|)
expr_stmt|;
return|return
name|omClientResponse
return|;
block|}
block|}
block|}
end_class

end_unit

