begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.request.key
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
name|key
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileEncryptionInfo
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
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ExcludeList
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
name|OMMetrics
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
name|OmBucketInfo
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
name|OmKeyLocationInfo
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
name|CreateKeyRequest
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
name|KeyArgs
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
comment|/**  * Handles CreateKey request.  */
end_comment

begin_class
DECL|class|OMKeyCreateRequest
specifier|public
class|class
name|OMKeyCreateRequest
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
name|OMKeyCreateRequest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|OMKeyCreateRequest (OMRequest omRequest)
specifier|public
name|OMKeyCreateRequest
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
throws|throws
name|IOException
block|{
name|CreateKeyRequest
name|createKeyRequest
init|=
name|getOmRequest
argument_list|()
operator|.
name|getCreateKeyRequest
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|createKeyRequest
argument_list|)
expr_stmt|;
name|KeyArgs
name|keyArgs
init|=
name|createKeyRequest
operator|.
name|getKeyArgs
argument_list|()
decl_stmt|;
comment|// We cannot allocate block for multipart upload part when
comment|// createMultipartKey is called, as we will not know type and factor with
comment|// which initiateMultipartUpload has started for this key. When
comment|// allocateBlock call happen's we shall know type and factor, as we set
comment|// the type and factor read from multipart table, and set the KeyInfo in
comment|// validateAndUpdateCache and return to the client. TODO: See if we can fix
comment|//  this. We do not call allocateBlock in openKey for multipart upload.
name|CreateKeyRequest
operator|.
name|Builder
name|newCreateKeyRequest
init|=
literal|null
decl_stmt|;
name|KeyArgs
operator|.
name|Builder
name|newKeyArgs
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|keyArgs
operator|.
name|getIsMultipartKey
argument_list|()
condition|)
block|{
name|long
name|scmBlockSize
init|=
name|ozoneManager
operator|.
name|getScmBlockSize
argument_list|()
decl_stmt|;
comment|// NOTE size of a key is not a hard limit on anything, it is a value that
comment|// client should expect, in terms of current size of key. If client sets
comment|// a value, then this value is used, otherwise, we allocate a single
comment|// block which is the current size, if read by the client.
specifier|final
name|long
name|requestedSize
init|=
name|keyArgs
operator|.
name|getDataSize
argument_list|()
operator|>
literal|0
condition|?
name|keyArgs
operator|.
name|getDataSize
argument_list|()
else|:
name|scmBlockSize
decl_stmt|;
name|boolean
name|useRatis
init|=
name|ozoneManager
operator|.
name|shouldUseRatis
argument_list|()
decl_stmt|;
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
init|=
name|keyArgs
operator|.
name|getFactor
argument_list|()
decl_stmt|;
if|if
condition|(
name|factor
operator|==
literal|null
condition|)
block|{
name|factor
operator|=
name|useRatis
condition|?
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
else|:
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
expr_stmt|;
block|}
name|HddsProtos
operator|.
name|ReplicationType
name|type
init|=
name|keyArgs
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|useRatis
condition|?
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
else|:
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
expr_stmt|;
block|}
comment|// TODO: Here we are allocating block with out any check for
comment|//  bucket/key/volume or not and also with out any authorization checks.
comment|//  As for a client for the first time this can be executed on any OM,
comment|//  till leader is identified.
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|omKeyLocationInfoList
init|=
name|allocateBlock
argument_list|(
name|ozoneManager
operator|.
name|getScmClient
argument_list|()
argument_list|,
name|ozoneManager
operator|.
name|getBlockTokenSecretManager
argument_list|()
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
operator|new
name|ExcludeList
argument_list|()
argument_list|,
name|requestedSize
argument_list|,
name|scmBlockSize
argument_list|,
name|ozoneManager
operator|.
name|getPreallocateBlocksMax
argument_list|()
argument_list|,
name|ozoneManager
operator|.
name|isGrpcBlockTokenEnabled
argument_list|()
argument_list|,
name|ozoneManager
operator|.
name|getOMNodeId
argument_list|()
argument_list|)
decl_stmt|;
name|newKeyArgs
operator|=
name|keyArgs
operator|.
name|toBuilder
argument_list|()
operator|.
name|setModificationTime
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
operator|.
name|setType
argument_list|(
name|type
argument_list|)
operator|.
name|setFactor
argument_list|(
name|factor
argument_list|)
operator|.
name|setDataSize
argument_list|(
name|requestedSize
argument_list|)
expr_stmt|;
name|newKeyArgs
operator|.
name|addAllKeyLocations
argument_list|(
name|omKeyLocationInfoList
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|OmKeyLocationInfo
operator|::
name|getProtobuf
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newKeyArgs
operator|=
name|keyArgs
operator|.
name|toBuilder
argument_list|()
operator|.
name|setModificationTime
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|newCreateKeyRequest
operator|=
name|createKeyRequest
operator|.
name|toBuilder
argument_list|()
operator|.
name|setKeyArgs
argument_list|(
name|newKeyArgs
argument_list|)
operator|.
name|setClientID
argument_list|(
name|UniqueId
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|getOmRequest
argument_list|()
operator|.
name|toBuilder
argument_list|()
operator|.
name|setCreateKeyRequest
argument_list|(
name|newCreateKeyRequest
argument_list|)
operator|.
name|setUserInfo
argument_list|(
name|getUserInfo
argument_list|()
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
name|CreateKeyRequest
name|createKeyRequest
init|=
name|getOmRequest
argument_list|()
operator|.
name|getCreateKeyRequest
argument_list|()
decl_stmt|;
name|KeyArgs
name|keyArgs
init|=
name|createKeyRequest
operator|.
name|getKeyArgs
argument_list|()
decl_stmt|;
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
name|OMMetrics
name|omMetrics
init|=
name|ozoneManager
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|omMetrics
operator|.
name|incNumKeyAllocates
argument_list|()
expr_stmt|;
name|OMMetadataManager
name|omMetadataManager
init|=
name|ozoneManager
operator|.
name|getMetadataManager
argument_list|()
decl_stmt|;
name|OmKeyInfo
name|omKeyInfo
init|=
literal|null
decl_stmt|;
specifier|final
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|locations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Optional
argument_list|<
name|FileEncryptionInfo
argument_list|>
name|encryptionInfo
init|=
name|Optional
operator|.
name|absent
argument_list|()
decl_stmt|;
name|IOException
name|exception
init|=
literal|null
decl_stmt|;
name|boolean
name|acquireLock
init|=
literal|false
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
name|acquireLock
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
comment|//TODO: We can optimize this get here, if getKmsProvider is null, then
comment|// bucket encryptionInfo will be not set. If this assumption holds
comment|// true, we can avoid get from bucket table.
name|OmBucketInfo
name|bucketInfo
init|=
name|omMetadataManager
operator|.
name|getBucketTable
argument_list|()
operator|.
name|get
argument_list|(
name|omMetadataManager
operator|.
name|getBucketKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
argument_list|)
decl_stmt|;
name|encryptionInfo
operator|=
name|getFileEncryptionInfo
argument_list|(
name|ozoneManager
argument_list|,
name|bucketInfo
argument_list|)
expr_stmt|;
name|omKeyInfo
operator|=
name|prepareKeyInfo
argument_list|(
name|omMetadataManager
argument_list|,
name|keyArgs
argument_list|,
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
argument_list|,
name|keyArgs
operator|.
name|getDataSize
argument_list|()
argument_list|,
name|locations
argument_list|,
name|encryptionInfo
operator|.
name|orNull
argument_list|()
argument_list|)
expr_stmt|;
name|omClientResponse
operator|=
name|prepareCreateKeyResponse
argument_list|(
name|keyArgs
argument_list|,
name|omKeyInfo
argument_list|,
name|locations
argument_list|,
name|encryptionInfo
operator|.
name|orNull
argument_list|()
argument_list|,
name|exception
argument_list|,
name|createKeyRequest
operator|.
name|getClientID
argument_list|()
argument_list|,
name|transactionLogIndex
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|ozoneManager
argument_list|,
name|OMAction
operator|.
name|ALLOCATE_KEY
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
name|prepareCreateKeyResponse
argument_list|(
name|keyArgs
argument_list|,
name|omKeyInfo
argument_list|,
name|locations
argument_list|,
name|encryptionInfo
operator|.
name|orNull
argument_list|()
argument_list|,
name|exception
argument_list|,
name|createKeyRequest
operator|.
name|getClientID
argument_list|()
argument_list|,
name|transactionLogIndex
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|ozoneManager
argument_list|,
name|OMAction
operator|.
name|ALLOCATE_KEY
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
name|acquireLock
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
return|return
name|omClientResponse
return|;
block|}
block|}
end_class

end_unit

