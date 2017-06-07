begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.ksm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|ksm
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
name|fs
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmBucketArgs
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmBucketInfo
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
name|ksm
operator|.
name|exceptions
operator|.
name|KSMException
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
name|KeySpaceManagerProtocolProtos
operator|.
name|BucketInfo
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
name|OzoneAcl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|DBException
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
name|List
import|;
end_import

begin_comment
comment|/**  * KSM bucket manager.  */
end_comment

begin_class
DECL|class|BucketManagerImpl
specifier|public
class|class
name|BucketManagerImpl
implements|implements
name|BucketManager
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
name|BucketManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * MetadataManager is used for accessing KSM MetadataDB and ReadWriteLock.    */
DECL|field|metadataManager
specifier|private
specifier|final
name|MetadataManager
name|metadataManager
decl_stmt|;
comment|/**    * Constructs BucketManager.    * @param metadataManager    */
DECL|method|BucketManagerImpl (MetadataManager metadataManager)
specifier|public
name|BucketManagerImpl
parameter_list|(
name|MetadataManager
name|metadataManager
parameter_list|)
block|{
name|this
operator|.
name|metadataManager
operator|=
name|metadataManager
expr_stmt|;
block|}
comment|/**    * MetadataDB is maintained in MetadataManager and shared between    * BucketManager and VolumeManager. (and also by KeyManager)    *    * BucketManager uses MetadataDB to store bucket level information.    *    * Keys used in BucketManager for storing data into MetadataDB    * for BucketInfo:    * {volume/bucket} -> bucketInfo    *    * Work flow of create bucket:    *    * -> Check if the Volume exists in metadataDB, if not throw    * VolumeNotFoundException.    * -> Else check if the Bucket exists in metadataDB, if so throw    * BucketExistException    * -> Else update MetadataDB with VolumeInfo.    */
comment|/**    * Creates a bucket.    * @param bucketInfo - KsmBucketInfo.    */
annotation|@
name|Override
DECL|method|createBucket (KsmBucketInfo bucketInfo)
specifier|public
name|void
name|createBucket
parameter_list|(
name|KsmBucketInfo
name|bucketInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|bucketInfo
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|String
name|volumeName
init|=
name|bucketInfo
operator|.
name|getVolumeName
argument_list|()
decl_stmt|;
name|String
name|bucketName
init|=
name|bucketInfo
operator|.
name|getBucketName
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|volumeKey
init|=
name|metadataManager
operator|.
name|getVolumeKey
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bucketKey
init|=
name|metadataManager
operator|.
name|getBucketKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
comment|//Check if the volume exists
if|if
condition|(
name|metadataManager
operator|.
name|get
argument_list|(
name|volumeKey
argument_list|)
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"volume: {} not found "
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Volume doesn't exist"
argument_list|,
name|KSMException
operator|.
name|ResultCodes
operator|.
name|FAILED_VOLUME_NOT_FOUND
argument_list|)
throw|;
block|}
comment|//Check if bucket already exists
if|if
condition|(
name|metadataManager
operator|.
name|get
argument_list|(
name|bucketKey
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"bucket: {} already exists "
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Bucket already exist"
argument_list|,
name|KSMException
operator|.
name|ResultCodes
operator|.
name|FAILED_BUCKET_ALREADY_EXISTS
argument_list|)
throw|;
block|}
name|metadataManager
operator|.
name|put
argument_list|(
name|bucketKey
argument_list|,
name|bucketInfo
operator|.
name|getProtobuf
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"created bucket: {} in volume: {}"
argument_list|,
name|bucketName
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|DBException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Bucket creation failed for bucket:{} in volume:{}"
argument_list|,
name|bucketName
argument_list|,
name|volumeName
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns Bucket Information.    *    * @param volumeName - Name of the Volume.    * @param bucketName - Name of the Bucket.    */
annotation|@
name|Override
DECL|method|getBucketInfo (String volumeName, String bucketName)
specifier|public
name|KsmBucketInfo
name|getBucketInfo
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|bucketKey
init|=
name|metadataManager
operator|.
name|getBucketKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
name|byte
index|[]
name|value
init|=
name|metadataManager
operator|.
name|get
argument_list|(
name|bucketKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"bucket: {} not found in volume: {}."
argument_list|,
name|bucketName
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Bucket not found"
argument_list|,
name|KSMException
operator|.
name|ResultCodes
operator|.
name|FAILED_BUCKET_NOT_FOUND
argument_list|)
throw|;
block|}
return|return
name|KsmBucketInfo
operator|.
name|getFromProtobuf
argument_list|(
name|BucketInfo
operator|.
name|parseFrom
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|DBException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while getting bucket info for bucket: {}"
argument_list|,
name|bucketName
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Sets bucket property from args.    * @param args - BucketArgs.    * @throws IOException    */
annotation|@
name|Override
DECL|method|setBucketProperty (KsmBucketArgs args)
specifier|public
name|void
name|setBucketProperty
parameter_list|(
name|KsmBucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|String
name|volumeName
init|=
name|args
operator|.
name|getVolumeName
argument_list|()
decl_stmt|;
name|String
name|bucketName
init|=
name|args
operator|.
name|getBucketName
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|bucketKey
init|=
name|metadataManager
operator|.
name|getBucketKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
comment|//Check if volume exists
if|if
condition|(
name|metadataManager
operator|.
name|get
argument_list|(
name|metadataManager
operator|.
name|getVolumeKey
argument_list|(
name|volumeName
argument_list|)
argument_list|)
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"volume: {} not found "
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Volume doesn't exist"
argument_list|,
name|KSMException
operator|.
name|ResultCodes
operator|.
name|FAILED_VOLUME_NOT_FOUND
argument_list|)
throw|;
block|}
name|byte
index|[]
name|value
init|=
name|metadataManager
operator|.
name|get
argument_list|(
name|bucketKey
argument_list|)
decl_stmt|;
comment|//Check if bucket exist
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"bucket: {} not found "
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Bucket doesn't exist"
argument_list|,
name|KSMException
operator|.
name|ResultCodes
operator|.
name|FAILED_BUCKET_NOT_FOUND
argument_list|)
throw|;
block|}
name|KsmBucketInfo
name|oldBucketInfo
init|=
name|KsmBucketInfo
operator|.
name|getFromProtobuf
argument_list|(
name|BucketInfo
operator|.
name|parseFrom
argument_list|(
name|value
argument_list|)
argument_list|)
decl_stmt|;
name|KsmBucketInfo
operator|.
name|Builder
name|bucketInfoBuilder
init|=
name|KsmBucketInfo
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|bucketInfoBuilder
operator|.
name|setVolumeName
argument_list|(
name|oldBucketInfo
operator|.
name|getVolumeName
argument_list|()
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|oldBucketInfo
operator|.
name|getBucketName
argument_list|()
argument_list|)
expr_stmt|;
comment|//Check ACLs to update
if|if
condition|(
name|args
operator|.
name|getAddAcls
argument_list|()
operator|!=
literal|null
operator|||
name|args
operator|.
name|getRemoveAcls
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|bucketInfoBuilder
operator|.
name|setAcls
argument_list|(
name|getUpdatedAclList
argument_list|(
name|oldBucketInfo
operator|.
name|getAcls
argument_list|()
argument_list|,
name|args
operator|.
name|getRemoveAcls
argument_list|()
argument_list|,
name|args
operator|.
name|getAddAcls
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Updating ACLs for bucket: {} in volume: {}"
argument_list|,
name|bucketName
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bucketInfoBuilder
operator|.
name|setAcls
argument_list|(
name|oldBucketInfo
operator|.
name|getAcls
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//Check StorageType to update
name|StorageType
name|storageType
init|=
name|args
operator|.
name|getStorageType
argument_list|()
decl_stmt|;
if|if
condition|(
name|storageType
operator|!=
literal|null
condition|)
block|{
name|bucketInfoBuilder
operator|.
name|setStorageType
argument_list|(
name|storageType
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Updating bucket storage type for bucket: {} in volume: {}"
argument_list|,
name|bucketName
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bucketInfoBuilder
operator|.
name|setStorageType
argument_list|(
name|oldBucketInfo
operator|.
name|getStorageType
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//Check Versioning to update
name|Boolean
name|versioning
init|=
name|args
operator|.
name|getIsVersionEnabled
argument_list|()
decl_stmt|;
if|if
condition|(
name|versioning
operator|!=
literal|null
condition|)
block|{
name|bucketInfoBuilder
operator|.
name|setIsVersionEnabled
argument_list|(
name|versioning
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Updating bucket versioning for bucket: {} in volume: {}"
argument_list|,
name|bucketName
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bucketInfoBuilder
operator|.
name|setIsVersionEnabled
argument_list|(
name|oldBucketInfo
operator|.
name|getIsVersionEnabled
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|metadataManager
operator|.
name|put
argument_list|(
name|bucketKey
argument_list|,
name|bucketInfoBuilder
operator|.
name|build
argument_list|()
operator|.
name|getProtobuf
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|DBException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Setting bucket property failed for bucket:{} in volume:{}"
argument_list|,
name|bucketName
argument_list|,
name|volumeName
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Updates the existing ACL list with remove and add ACLs that are passed.    * Remove is done before Add.    *    * @param existingAcls - old ACL list.    * @param removeAcls - ACLs to be removed.    * @param addAcls - ACLs to be added.    * @return updated ACL list.    */
DECL|method|getUpdatedAclList (List<OzoneAcl> existingAcls, List<OzoneAcl> removeAcls, List<OzoneAcl> addAcls)
specifier|private
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|getUpdatedAclList
parameter_list|(
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|existingAcls
parameter_list|,
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|removeAcls
parameter_list|,
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|addAcls
parameter_list|)
block|{
if|if
condition|(
name|removeAcls
operator|!=
literal|null
operator|&&
operator|!
name|removeAcls
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|existingAcls
operator|.
name|removeAll
argument_list|(
name|removeAcls
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|addAcls
operator|!=
literal|null
operator|&&
operator|!
name|addAcls
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|addAcls
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|acl
lambda|->
operator|!
name|existingAcls
operator|.
name|contains
argument_list|(
name|acl
argument_list|)
argument_list|)
operator|.
name|forEach
argument_list|(
name|existingAcls
operator|::
name|add
argument_list|)
expr_stmt|;
block|}
return|return
name|existingAcls
return|;
block|}
comment|/**    * Deletes an existing empty bucket from volume.    * @param volumeName - Name of the volume.    * @param bucketName - Name of the bucket.    * @throws IOException    */
DECL|method|deleteBucket (String volumeName, String bucketName)
specifier|public
name|void
name|deleteBucket
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|bucketKey
init|=
name|metadataManager
operator|.
name|getBucketKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
comment|//Check if volume exists
if|if
condition|(
name|metadataManager
operator|.
name|get
argument_list|(
name|metadataManager
operator|.
name|getVolumeKey
argument_list|(
name|volumeName
argument_list|)
argument_list|)
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"volume: {} not found "
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Volume doesn't exist"
argument_list|,
name|KSMException
operator|.
name|ResultCodes
operator|.
name|FAILED_VOLUME_NOT_FOUND
argument_list|)
throw|;
block|}
comment|//Check if bucket exist
if|if
condition|(
name|metadataManager
operator|.
name|get
argument_list|(
name|bucketKey
argument_list|)
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"bucket: {} not found "
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Bucket doesn't exist"
argument_list|,
name|KSMException
operator|.
name|ResultCodes
operator|.
name|FAILED_BUCKET_NOT_FOUND
argument_list|)
throw|;
block|}
comment|//Check if bucket is empty
if|if
condition|(
operator|!
name|metadataManager
operator|.
name|isBucketEmpty
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"bucket: {} is not empty "
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Bucket is not empty"
argument_list|,
name|KSMException
operator|.
name|ResultCodes
operator|.
name|FAILED_BUCKET_NOT_EMPTY
argument_list|)
throw|;
block|}
name|metadataManager
operator|.
name|delete
argument_list|(
name|bucketKey
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Delete bucket failed for bucket:{} in volume:{}"
argument_list|,
name|bucketName
argument_list|,
name|volumeName
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

