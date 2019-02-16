begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
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
name|List
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
name|crypto
operator|.
name|CipherSuite
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
name|crypto
operator|.
name|CryptoProtocolVersion
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
name|crypto
operator|.
name|key
operator|.
name|KeyProvider
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
name|crypto
operator|.
name|key
operator|.
name|KeyProviderCryptoExtension
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
name|CommonConfigurationKeys
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
name|OzoneAcl
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
name|BucketEncryptionKeyInfo
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
name|OmBucketArgs
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
name|util
operator|.
name|Time
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

begin_comment
comment|/**  * OM bucket manager.  */
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
comment|/**    * OMMetadataManager is used for accessing OM MetadataDB and ReadWriteLock.    */
DECL|field|metadataManager
specifier|private
specifier|final
name|OMMetadataManager
name|metadataManager
decl_stmt|;
DECL|field|kmsProvider
specifier|private
specifier|final
name|KeyProviderCryptoExtension
name|kmsProvider
decl_stmt|;
comment|/**    * Constructs BucketManager.    *    * @param metadataManager    */
DECL|method|BucketManagerImpl (OMMetadataManager metadataManager)
specifier|public
name|BucketManagerImpl
parameter_list|(
name|OMMetadataManager
name|metadataManager
parameter_list|)
block|{
name|this
argument_list|(
name|metadataManager
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|BucketManagerImpl (OMMetadataManager metadataManager, KeyProviderCryptoExtension kmsProvider)
specifier|public
name|BucketManagerImpl
parameter_list|(
name|OMMetadataManager
name|metadataManager
parameter_list|,
name|KeyProviderCryptoExtension
name|kmsProvider
parameter_list|)
block|{
name|this
operator|.
name|metadataManager
operator|=
name|metadataManager
expr_stmt|;
name|this
operator|.
name|kmsProvider
operator|=
name|kmsProvider
expr_stmt|;
block|}
DECL|method|getKMSProvider ()
name|KeyProviderCryptoExtension
name|getKMSProvider
parameter_list|()
block|{
return|return
name|kmsProvider
return|;
block|}
comment|/**    * MetadataDB is maintained in MetadataManager and shared between    * BucketManager and VolumeManager. (and also by BlockManager)    *    * BucketManager uses MetadataDB to store bucket level information.    *    * Keys used in BucketManager for storing data into MetadataDB    * for BucketInfo:    * {volume/bucket} -> bucketInfo    *    * Work flow of create bucket:    *    * -> Check if the Volume exists in metadataDB, if not throw    * VolumeNotFoundException.    * -> Else check if the Bucket exists in metadataDB, if so throw    * BucketExistException    * -> Else update MetadataDB with VolumeInfo.    */
comment|/**    * Creates a bucket.    *    * @param bucketInfo - OmBucketInfo.    */
annotation|@
name|Override
DECL|method|createBucket (OmBucketInfo bucketInfo)
specifier|public
name|void
name|createBucket
parameter_list|(
name|OmBucketInfo
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
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireVolumeLock
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireBucketLock
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
try|try
block|{
name|String
name|volumeKey
init|=
name|metadataManager
operator|.
name|getVolumeKey
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|String
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
name|getVolumeTable
argument_list|()
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
name|debug
argument_list|(
literal|"volume: {} not found "
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Volume doesn't exist"
argument_list|,
name|OMException
operator|.
name|ResultCodes
operator|.
name|VOLUME_NOT_FOUND
argument_list|)
throw|;
block|}
comment|//Check if bucket already exists
if|if
condition|(
name|metadataManager
operator|.
name|getBucketTable
argument_list|()
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
name|debug
argument_list|(
literal|"bucket: {} already exists "
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Bucket already exist"
argument_list|,
name|OMException
operator|.
name|ResultCodes
operator|.
name|BUCKET_ALREADY_EXISTS
argument_list|)
throw|;
block|}
name|BucketEncryptionKeyInfo
name|bek
init|=
name|bucketInfo
operator|.
name|getEncryptionKeyInfo
argument_list|()
decl_stmt|;
name|BucketEncryptionKeyInfo
operator|.
name|Builder
name|bekb
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|bek
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|kmsProvider
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Invalid KMS provider, check configuration "
operator|+
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_KEY_PROVIDER_PATH
argument_list|,
name|OMException
operator|.
name|ResultCodes
operator|.
name|INVALID_KMS_PROVIDER
argument_list|)
throw|;
block|}
if|if
condition|(
name|bek
operator|.
name|getKeyName
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Bucket encryption key needed."
argument_list|,
name|OMException
operator|.
name|ResultCodes
operator|.
name|BUCKET_ENCRYPTION_KEY_NOT_FOUND
argument_list|)
throw|;
block|}
comment|// Talk to KMS to retrieve the bucket encryption key info.
name|KeyProvider
operator|.
name|Metadata
name|metadata
init|=
name|getKMSProvider
argument_list|()
operator|.
name|getMetadata
argument_list|(
name|bek
operator|.
name|getKeyName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|metadata
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Bucket encryption key "
operator|+
name|bek
operator|.
name|getKeyName
argument_list|()
operator|+
literal|" doesn't exist."
argument_list|,
name|OMException
operator|.
name|ResultCodes
operator|.
name|BUCKET_ENCRYPTION_KEY_NOT_FOUND
argument_list|)
throw|;
block|}
comment|// If the provider supports pool for EDEKs, this will fill in the pool
name|kmsProvider
operator|.
name|warmUpEncryptedKeys
argument_list|(
name|bek
operator|.
name|getKeyName
argument_list|()
argument_list|)
expr_stmt|;
name|bekb
operator|=
operator|new
name|BucketEncryptionKeyInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setKeyName
argument_list|(
name|bek
operator|.
name|getKeyName
argument_list|()
argument_list|)
operator|.
name|setVersion
argument_list|(
name|CryptoProtocolVersion
operator|.
name|ENCRYPTION_ZONES
argument_list|)
operator|.
name|setSuite
argument_list|(
name|CipherSuite
operator|.
name|convert
argument_list|(
name|metadata
operator|.
name|getCipher
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|OmBucketInfo
operator|.
name|Builder
name|omBucketInfoBuilder
init|=
name|OmBucketInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|bucketInfo
operator|.
name|getVolumeName
argument_list|()
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|bucketInfo
operator|.
name|getBucketName
argument_list|()
argument_list|)
operator|.
name|setAcls
argument_list|(
name|bucketInfo
operator|.
name|getAcls
argument_list|()
argument_list|)
operator|.
name|setStorageType
argument_list|(
name|bucketInfo
operator|.
name|getStorageType
argument_list|()
argument_list|)
operator|.
name|setIsVersionEnabled
argument_list|(
name|bucketInfo
operator|.
name|getIsVersionEnabled
argument_list|()
argument_list|)
operator|.
name|setCreationTime
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
operator|.
name|addAllMetadata
argument_list|(
name|bucketInfo
operator|.
name|getMetadata
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|bekb
operator|!=
literal|null
condition|)
block|{
name|omBucketInfoBuilder
operator|.
name|setBucketEncryptionKey
argument_list|(
name|bekb
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|metadataManager
operator|.
name|getBucketTable
argument_list|()
operator|.
name|put
argument_list|(
name|bucketKey
argument_list|,
name|omBucketInfoBuilder
operator|.
name|build
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
if|if
condition|(
operator|!
operator|(
name|ex
operator|instanceof
name|OMException
operator|)
condition|)
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
block|}
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseBucketLock
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseVolumeLock
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns Bucket Information.    *    * @param volumeName - Name of the Volume.    * @param bucketName - Name of the Bucket.    */
annotation|@
name|Override
DECL|method|getBucketInfo (String volumeName, String bucketName)
specifier|public
name|OmBucketInfo
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
name|getLock
argument_list|()
operator|.
name|acquireBucketLock
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
try|try
block|{
name|String
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
name|OmBucketInfo
name|value
init|=
name|metadataManager
operator|.
name|getBucketTable
argument_list|()
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
name|debug
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
name|OMException
argument_list|(
literal|"Bucket not found"
argument_list|,
name|OMException
operator|.
name|ResultCodes
operator|.
name|BUCKET_NOT_FOUND
argument_list|)
throw|;
block|}
return|return
name|value
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
if|if
condition|(
operator|!
operator|(
name|ex
operator|instanceof
name|OMException
operator|)
condition|)
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
block|}
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseBucketLock
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Sets bucket property from args.    *    * @param args - BucketArgs.    * @throws IOException - On Failure.    */
annotation|@
name|Override
DECL|method|setBucketProperty (OmBucketArgs args)
specifier|public
name|void
name|setBucketProperty
parameter_list|(
name|OmBucketArgs
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
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireBucketLock
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
try|try
block|{
name|String
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
name|OmBucketInfo
name|oldBucketInfo
init|=
name|metadataManager
operator|.
name|getBucketTable
argument_list|()
operator|.
name|get
argument_list|(
name|bucketKey
argument_list|)
decl_stmt|;
comment|//Check if bucket exist
if|if
condition|(
name|oldBucketInfo
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"bucket: {} not found "
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Bucket doesn't exist"
argument_list|,
name|OMException
operator|.
name|ResultCodes
operator|.
name|BUCKET_NOT_FOUND
argument_list|)
throw|;
block|}
name|OmBucketInfo
operator|.
name|Builder
name|bucketInfoBuilder
init|=
name|OmBucketInfo
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
name|bucketInfoBuilder
operator|.
name|addAllMetadata
argument_list|(
name|args
operator|.
name|getMetadata
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
name|bucketInfoBuilder
operator|.
name|setCreationTime
argument_list|(
name|oldBucketInfo
operator|.
name|getCreationTime
argument_list|()
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|getBucketTable
argument_list|()
operator|.
name|put
argument_list|(
name|bucketKey
argument_list|,
name|bucketInfoBuilder
operator|.
name|build
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
if|if
condition|(
operator|!
operator|(
name|ex
operator|instanceof
name|OMException
operator|)
condition|)
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
block|}
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseBucketLock
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
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
comment|/**    * Deletes an existing empty bucket from volume.    *    * @param volumeName - Name of the volume.    * @param bucketName - Name of the bucket.    * @throws IOException - on Failure.    */
annotation|@
name|Override
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
name|getLock
argument_list|()
operator|.
name|acquireBucketLock
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
try|try
block|{
comment|//Check if bucket exists
name|String
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
if|if
condition|(
name|metadataManager
operator|.
name|getBucketTable
argument_list|()
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
name|debug
argument_list|(
literal|"bucket: {} not found "
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Bucket doesn't exist"
argument_list|,
name|OMException
operator|.
name|ResultCodes
operator|.
name|BUCKET_NOT_FOUND
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
name|debug
argument_list|(
literal|"bucket: {} is not empty "
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Bucket is not empty"
argument_list|,
name|OMException
operator|.
name|ResultCodes
operator|.
name|BUCKET_NOT_EMPTY
argument_list|)
throw|;
block|}
name|metadataManager
operator|.
name|getBucketTable
argument_list|()
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
if|if
condition|(
operator|!
operator|(
name|ex
operator|instanceof
name|OMException
operator|)
condition|)
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
block|}
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseBucketLock
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|listBuckets (String volumeName, String startBucket, String bucketPrefix, int maxNumOfBuckets)
specifier|public
name|List
argument_list|<
name|OmBucketInfo
argument_list|>
name|listBuckets
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|startBucket
parameter_list|,
name|String
name|bucketPrefix
parameter_list|,
name|int
name|maxNumOfBuckets
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
return|return
name|metadataManager
operator|.
name|listBuckets
argument_list|(
name|volumeName
argument_list|,
name|startBucket
argument_list|,
name|bucketPrefix
argument_list|,
name|maxNumOfBuckets
argument_list|)
return|;
block|}
block|}
end_class

end_unit

