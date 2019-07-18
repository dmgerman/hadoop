begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.request.s3.bucket
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
name|bucket
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|hdds
operator|.
name|protocol
operator|.
name|StorageType
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
name|OzoneConsts
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
name|exceptions
operator|.
name|OMException
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
name|OmVolumeArgs
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
name|volume
operator|.
name|OMVolumeRequest
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
name|bucket
operator|.
name|OMBucketCreateResponse
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
name|bucket
operator|.
name|S3BucketCreateResponse
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
name|volume
operator|.
name|OMVolumeCreateResponse
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|S3CreateBucketRequest
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
name|S3CreateBucketResponse
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
name|S3CreateVolumeInfo
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
name|VolumeList
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConsts
operator|.
name|OM_S3_VOLUME_PREFIX
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
name|OzoneConsts
operator|.
name|S3_BUCKET_MAX_LENGTH
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
name|OzoneConsts
operator|.
name|S3_BUCKET_MIN_LENGTH
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
name|S3_BUCKET_LOCK
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
name|USER_LOCK
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
name|VOLUME_LOCK
import|;
end_import

begin_comment
comment|/**  * Handles S3 Bucket create request.  */
end_comment

begin_class
DECL|class|S3BucketCreateRequest
specifier|public
class|class
name|S3BucketCreateRequest
extends|extends
name|OMVolumeRequest
block|{
DECL|field|S3_ADMIN_NAME
specifier|private
specifier|static
specifier|final
name|String
name|S3_ADMIN_NAME
init|=
literal|"OzoneS3Manager"
decl_stmt|;
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
name|S3CreateBucketRequest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|S3BucketCreateRequest (OMRequest omRequest)
specifier|public
name|S3BucketCreateRequest
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
name|S3CreateBucketRequest
name|s3CreateBucketRequest
init|=
name|getOmRequest
argument_list|()
operator|.
name|getCreateS3BucketRequest
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|s3CreateBucketRequest
argument_list|)
expr_stmt|;
name|S3CreateBucketRequest
operator|.
name|Builder
name|newS3CreateBucketRequest
init|=
name|s3CreateBucketRequest
operator|.
name|toBuilder
argument_list|()
operator|.
name|setS3CreateVolumeInfo
argument_list|(
name|S3CreateVolumeInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCreationTime
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// TODO: Do we need to enforce the bucket rules in this code path?
comment|// https://docs.aws.amazon.com/AmazonS3/latest/dev/BucketRestrictions.html
comment|// For now only checked the length.
name|int
name|bucketLength
init|=
name|s3CreateBucketRequest
operator|.
name|getS3Bucketname
argument_list|()
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|bucketLength
operator|<
name|S3_BUCKET_MIN_LENGTH
operator|||
name|bucketLength
operator|>=
name|S3_BUCKET_MAX_LENGTH
condition|)
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|"S3BucketName must be at least 3 and not more "
operator|+
literal|"than 63 characters long"
argument_list|,
name|OMException
operator|.
name|ResultCodes
operator|.
name|S3_BUCKET_INVALID_LENGTH
argument_list|)
throw|;
block|}
return|return
name|getOmRequest
argument_list|()
operator|.
name|toBuilder
argument_list|()
operator|.
name|setCreateS3BucketRequest
argument_list|(
name|newS3CreateBucketRequest
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
DECL|method|validateAndUpdateCache (OzoneManager ozoneManager, long transactionLogIndex)
specifier|public
name|OMClientResponse
name|validateAndUpdateCache
parameter_list|(
name|OzoneManager
name|ozoneManager
parameter_list|,
name|long
name|transactionLogIndex
parameter_list|)
block|{
name|S3CreateBucketRequest
name|s3CreateBucketRequest
init|=
name|getOmRequest
argument_list|()
operator|.
name|getCreateS3BucketRequest
argument_list|()
decl_stmt|;
name|String
name|userName
init|=
name|s3CreateBucketRequest
operator|.
name|getUserName
argument_list|()
decl_stmt|;
name|String
name|s3BucketName
init|=
name|s3CreateBucketRequest
operator|.
name|getS3Bucketname
argument_list|()
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
name|CreateS3Bucket
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
name|incNumS3BucketCreates
argument_list|()
expr_stmt|;
comment|// When s3 Bucket is created, we internally create ozone volume/ozone
comment|// bucket.
comment|// ozone volume name is generated from userName by calling
comment|// formatOzoneVolumeName.
comment|// ozone bucket name is same as s3 bucket name.
comment|// In S3 buckets are unique, so we create a mapping like s3BucketName ->
comment|// ozoneVolume/ozoneBucket and add it to s3 mapping table. If
comment|// s3BucketName exists in mapping table, bucket already exist or we go
comment|// ahead and create a bucket.
name|OMMetadataManager
name|omMetadataManager
init|=
name|ozoneManager
operator|.
name|getMetadataManager
argument_list|()
decl_stmt|;
name|IOException
name|exception
init|=
literal|null
decl_stmt|;
name|VolumeList
name|volumeList
init|=
literal|null
decl_stmt|;
name|OmVolumeArgs
name|omVolumeArgs
init|=
literal|null
decl_stmt|;
name|OmBucketInfo
name|omBucketInfo
init|=
literal|null
decl_stmt|;
name|boolean
name|volumeCreated
init|=
literal|false
decl_stmt|;
name|boolean
name|acquiredVolumeLock
init|=
literal|false
decl_stmt|;
name|boolean
name|acquiredUserLock
init|=
literal|false
decl_stmt|;
name|boolean
name|acquiredS3Lock
init|=
literal|false
decl_stmt|;
name|String
name|volumeName
init|=
name|formatOzoneVolumeName
argument_list|(
name|userName
argument_list|)
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
name|BUCKET
argument_list|,
name|OzoneObj
operator|.
name|StoreType
operator|.
name|S3
argument_list|,
name|IAccessAuthorizer
operator|.
name|ACLType
operator|.
name|CREATE
argument_list|,
literal|null
argument_list|,
name|s3BucketName
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|acquiredS3Lock
operator|=
name|omMetadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireLock
argument_list|(
name|S3_BUCKET_LOCK
argument_list|,
name|s3BucketName
argument_list|)
expr_stmt|;
comment|// First check if this s3Bucket exists
if|if
condition|(
name|omMetadataManager
operator|.
name|getS3Table
argument_list|()
operator|.
name|isExist
argument_list|(
name|s3BucketName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|"S3Bucket "
operator|+
name|s3BucketName
operator|+
literal|" already exists"
argument_list|,
name|OMException
operator|.
name|ResultCodes
operator|.
name|S3_BUCKET_ALREADY_EXISTS
argument_list|)
throw|;
block|}
try|try
block|{
name|acquiredVolumeLock
operator|=
name|omMetadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireLock
argument_list|(
name|VOLUME_LOCK
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
name|acquiredUserLock
operator|=
name|omMetadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireLock
argument_list|(
name|USER_LOCK
argument_list|,
name|userName
argument_list|)
expr_stmt|;
comment|// Check if volume exists, if it does not exist create
comment|// ozone volume.
name|String
name|volumeKey
init|=
name|omMetadataManager
operator|.
name|getVolumeKey
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|omMetadataManager
operator|.
name|getVolumeTable
argument_list|()
operator|.
name|isExist
argument_list|(
name|volumeKey
argument_list|)
condition|)
block|{
name|omVolumeArgs
operator|=
name|createOmVolumeArgs
argument_list|(
name|volumeName
argument_list|,
name|userName
argument_list|,
name|s3CreateBucketRequest
operator|.
name|getS3CreateVolumeInfo
argument_list|()
operator|.
name|getCreationTime
argument_list|()
argument_list|)
expr_stmt|;
name|volumeList
operator|=
name|omMetadataManager
operator|.
name|getUserTable
argument_list|()
operator|.
name|get
argument_list|(
name|omMetadataManager
operator|.
name|getUserKey
argument_list|(
name|userName
argument_list|)
argument_list|)
expr_stmt|;
name|volumeList
operator|=
name|addVolumeToOwnerList
argument_list|(
name|volumeList
argument_list|,
name|volumeName
argument_list|,
name|userName
argument_list|,
name|ozoneManager
operator|.
name|getMaxUserVolumeCount
argument_list|()
argument_list|)
expr_stmt|;
name|createVolume
argument_list|(
name|omMetadataManager
argument_list|,
name|omVolumeArgs
argument_list|,
name|volumeList
argument_list|,
name|volumeKey
argument_list|,
name|omMetadataManager
operator|.
name|getUserKey
argument_list|(
name|userName
argument_list|)
argument_list|,
name|transactionLogIndex
argument_list|)
expr_stmt|;
name|volumeCreated
operator|=
literal|true
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|acquiredUserLock
condition|)
block|{
name|omMetadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseLock
argument_list|(
name|USER_LOCK
argument_list|,
name|userName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|acquiredVolumeLock
condition|)
block|{
name|omMetadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseLock
argument_list|(
name|VOLUME_LOCK
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
block|}
block|}
comment|// check if ozone bucket exists, if it does not exist create ozone
comment|// bucket
name|omBucketInfo
operator|=
name|createBucket
argument_list|(
name|omMetadataManager
argument_list|,
name|volumeName
argument_list|,
name|s3BucketName
argument_list|,
name|s3CreateBucketRequest
operator|.
name|getS3CreateVolumeInfo
argument_list|()
operator|.
name|getCreationTime
argument_list|()
argument_list|,
name|transactionLogIndex
argument_list|)
expr_stmt|;
comment|// Now finally add it to s3 table cache.
name|omMetadataManager
operator|.
name|getS3Table
argument_list|()
operator|.
name|addCacheEntry
argument_list|(
operator|new
name|CacheKey
argument_list|<>
argument_list|(
name|s3BucketName
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
name|formatS3MappingName
argument_list|(
name|volumeName
argument_list|,
name|s3BucketName
argument_list|)
argument_list|)
argument_list|,
name|transactionLogIndex
argument_list|)
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
block|}
finally|finally
block|{
if|if
condition|(
name|acquiredS3Lock
condition|)
block|{
name|omMetadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseLock
argument_list|(
name|S3_BUCKET_LOCK
argument_list|,
name|s3BucketName
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Performing audit logging outside of the lock.
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
name|CREATE_S3_BUCKET
argument_list|,
name|buildAuditMap
argument_list|(
name|userName
argument_list|,
name|s3BucketName
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
literal|"S3Bucket is successfully created for userName: {}, "
operator|+
literal|"s3BucketName {}, volumeName {}"
argument_list|,
name|userName
argument_list|,
name|s3BucketName
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
name|OMVolumeCreateResponse
name|omVolumeCreateResponse
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|volumeCreated
condition|)
block|{
name|omMetrics
operator|.
name|incNumVolumes
argument_list|()
expr_stmt|;
name|omVolumeCreateResponse
operator|=
operator|new
name|OMVolumeCreateResponse
argument_list|(
name|omVolumeArgs
argument_list|,
name|volumeList
argument_list|,
name|omResponse
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|omMetrics
operator|.
name|incNumBuckets
argument_list|()
expr_stmt|;
name|OMBucketCreateResponse
name|omBucketCreateResponse
init|=
operator|new
name|OMBucketCreateResponse
argument_list|(
name|omBucketInfo
argument_list|,
name|omResponse
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|omMetrics
operator|.
name|incNumS3Buckets
argument_list|()
expr_stmt|;
return|return
operator|new
name|S3BucketCreateResponse
argument_list|(
name|omVolumeCreateResponse
argument_list|,
name|omBucketCreateResponse
argument_list|,
name|s3BucketName
argument_list|,
name|formatS3MappingName
argument_list|(
name|volumeName
argument_list|,
name|s3BucketName
argument_list|)
argument_list|,
name|omResponse
operator|.
name|setCreateS3BucketResponse
argument_list|(
name|S3CreateBucketResponse
operator|.
name|newBuilder
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"S3Bucket Creation Failed for userName: {}, s3BucketName {}, "
operator|+
literal|"VolumeName {}"
argument_list|,
name|userName
argument_list|,
name|s3BucketName
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
name|omMetrics
operator|.
name|incNumS3BucketCreateFails
argument_list|()
expr_stmt|;
return|return
operator|new
name|S3BucketCreateResponse
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
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
return|;
block|}
block|}
DECL|method|createBucket (OMMetadataManager omMetadataManager, String volumeName, String s3BucketName, long creationTime, long transactionLogIndex)
specifier|private
name|OmBucketInfo
name|createBucket
parameter_list|(
name|OMMetadataManager
name|omMetadataManager
parameter_list|,
name|String
name|volumeName
parameter_list|,
name|String
name|s3BucketName
parameter_list|,
name|long
name|creationTime
parameter_list|,
name|long
name|transactionLogIndex
parameter_list|)
throws|throws
name|IOException
block|{
comment|// check if ozone bucket exists, if it does not exist create ozone
comment|// bucket
name|boolean
name|acquireBucketLock
init|=
literal|false
decl_stmt|;
name|OmBucketInfo
name|omBucketInfo
init|=
literal|null
decl_stmt|;
try|try
block|{
name|acquireBucketLock
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
name|s3BucketName
argument_list|)
expr_stmt|;
name|String
name|bucketKey
init|=
name|omMetadataManager
operator|.
name|getBucketKey
argument_list|(
name|volumeName
argument_list|,
name|s3BucketName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|omMetadataManager
operator|.
name|getBucketTable
argument_list|()
operator|.
name|isExist
argument_list|(
name|bucketKey
argument_list|)
condition|)
block|{
name|omBucketInfo
operator|=
name|createOmBucketInfo
argument_list|(
name|volumeName
argument_list|,
name|s3BucketName
argument_list|,
name|creationTime
argument_list|)
expr_stmt|;
comment|// Add to bucket table cache.
name|omMetadataManager
operator|.
name|getBucketTable
argument_list|()
operator|.
name|addCacheEntry
argument_list|(
operator|new
name|CacheKey
argument_list|<>
argument_list|(
name|bucketKey
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
name|omBucketInfo
argument_list|)
argument_list|,
name|transactionLogIndex
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// This can happen when a ozone bucket exists already in the
comment|// volume, but this is not a s3 bucket.
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Bucket "
operator|+
name|s3BucketName
operator|+
literal|" already exists"
argument_list|,
name|OMException
operator|.
name|ResultCodes
operator|.
name|BUCKET_ALREADY_EXISTS
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|acquireBucketLock
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
name|s3BucketName
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|omBucketInfo
return|;
block|}
comment|/**    * Generate Ozone volume name from userName.    * @param userName    * @return volume name    */
annotation|@
name|VisibleForTesting
DECL|method|formatOzoneVolumeName (String userName)
specifier|public
specifier|static
name|String
name|formatOzoneVolumeName
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|OM_S3_VOLUME_PREFIX
operator|+
literal|"%s"
argument_list|,
name|userName
argument_list|)
return|;
block|}
comment|/**    * Generate S3Mapping for provided volume and bucket. This information will    * be persisted in s3 table in OM DB.    * @param volumeName    * @param bucketName    * @return s3Mapping    */
annotation|@
name|VisibleForTesting
DECL|method|formatS3MappingName (String volumeName, String bucketName)
specifier|public
specifier|static
name|String
name|formatS3MappingName
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s"
operator|+
name|OzoneConsts
operator|.
name|OM_KEY_PREFIX
operator|+
literal|"%s"
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|)
return|;
block|}
comment|/**    * Create {@link OmVolumeArgs} which needs to be persisted in volume table    * in OM DB.    * @param volumeName    * @param userName    * @param creationTime    * @return {@link OmVolumeArgs}    */
DECL|method|createOmVolumeArgs (String volumeName, String userName, long creationTime)
specifier|private
name|OmVolumeArgs
name|createOmVolumeArgs
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|userName
parameter_list|,
name|long
name|creationTime
parameter_list|)
block|{
return|return
name|OmVolumeArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setAdminName
argument_list|(
name|S3_ADMIN_NAME
argument_list|)
operator|.
name|setVolume
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setQuotaInBytes
argument_list|(
name|OzoneConsts
operator|.
name|MAX_QUOTA_IN_BYTES
argument_list|)
operator|.
name|setOwnerName
argument_list|(
name|userName
argument_list|)
operator|.
name|setCreationTime
argument_list|(
name|creationTime
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Create {@link OmBucketInfo} which needs to be persisted in to bucket table    * in OM DB.    * @param volumeName    * @param s3BucketName    * @param creationTime    * @return {@link OmBucketInfo}    */
DECL|method|createOmBucketInfo (String volumeName, String s3BucketName, long creationTime)
specifier|private
name|OmBucketInfo
name|createOmBucketInfo
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|s3BucketName
parameter_list|,
name|long
name|creationTime
parameter_list|)
block|{
comment|//TODO: Now S3Bucket API takes only bucketName as param. In future if we
comment|// support some configurable options we need to fix this.
return|return
name|OmBucketInfo
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
name|s3BucketName
argument_list|)
operator|.
name|setIsVersionEnabled
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
operator|.
name|setStorageType
argument_list|(
name|StorageType
operator|.
name|DEFAULT
argument_list|)
operator|.
name|setCreationTime
argument_list|(
name|creationTime
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Build auditMap.    * @param userName    * @param s3BucketName    * @return auditMap    */
DECL|method|buildAuditMap (String userName, String s3BucketName)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|buildAuditMap
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|s3BucketName
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|auditMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|auditMap
operator|.
name|put
argument_list|(
name|userName
argument_list|,
name|OzoneConsts
operator|.
name|USERNAME
argument_list|)
expr_stmt|;
name|auditMap
operator|.
name|put
argument_list|(
name|s3BucketName
argument_list|,
name|OzoneConsts
operator|.
name|S3_BUCKET
argument_list|)
expr_stmt|;
return|return
name|auditMap
return|;
block|}
block|}
end_class

end_unit

