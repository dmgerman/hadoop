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
name|Strings
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
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|tuple
operator|.
name|ImmutablePair
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
name|hdfs
operator|.
name|DFSUtil
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
name|helpers
operator|.
name|KsmKeyInfo
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
name|helpers
operator|.
name|KsmVolumeArgs
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
name|helpers
operator|.
name|KsmKeyLocationInfo
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
name|common
operator|.
name|BlockGroup
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
name|conf
operator|.
name|OzoneConfiguration
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
name|ksm
operator|.
name|exceptions
operator|.
name|KSMException
operator|.
name|ResultCodes
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
name|protocol
operator|.
name|proto
operator|.
name|KeySpaceManagerProtocolProtos
operator|.
name|KeyInfo
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
name|VolumeInfo
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
name|web
operator|.
name|utils
operator|.
name|OzoneUtils
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
name|BatchOperation
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
name|MetadataKeyFilters
operator|.
name|KeyPrefixFilter
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
name|MetadataKeyFilters
operator|.
name|MetadataKeyFilter
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
name|MetadataStore
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
name|MetadataStoreBuilder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|DELETING_KEY_PREFIX
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
name|KSM_DB_NAME
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
name|OPEN_KEY_ID_DELIMINATOR
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
name|OPEN_KEY_PREFIX
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
name|ksm
operator|.
name|KSMConfigKeys
operator|.
name|OZONE_KSM_DB_CACHE_SIZE_DEFAULT
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
name|ksm
operator|.
name|KSMConfigKeys
operator|.
name|OZONE_KSM_DB_CACHE_SIZE_MB
import|;
end_import

begin_comment
comment|/**  * KSM metadata manager interface.  */
end_comment

begin_class
DECL|class|KSMMetadataManagerImpl
specifier|public
class|class
name|KSMMetadataManagerImpl
implements|implements
name|KSMMetadataManager
block|{
DECL|field|store
specifier|private
specifier|final
name|MetadataStore
name|store
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|ReadWriteLock
name|lock
decl_stmt|;
DECL|method|KSMMetadataManagerImpl (OzoneConfiguration conf)
specifier|public
name|KSMMetadataManagerImpl
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|metaDir
init|=
name|OzoneUtils
operator|.
name|getScmMetadirPath
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|int
name|cacheSize
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_KSM_DB_CACHE_SIZE_MB
argument_list|,
name|OZONE_KSM_DB_CACHE_SIZE_DEFAULT
argument_list|)
decl_stmt|;
name|File
name|ksmDBFile
init|=
operator|new
name|File
argument_list|(
name|metaDir
operator|.
name|getPath
argument_list|()
argument_list|,
name|KSM_DB_NAME
argument_list|)
decl_stmt|;
name|this
operator|.
name|store
operator|=
name|MetadataStoreBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
operator|.
name|setDbFile
argument_list|(
name|ksmDBFile
argument_list|)
operator|.
name|setCacheSize
argument_list|(
name|cacheSize
operator|*
name|OzoneConsts
operator|.
name|MB
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|lock
operator|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
expr_stmt|;
block|}
comment|/**    * Start metadata manager.    */
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{    }
comment|/**    * Stop metadata manager.    */
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get metadata store.    * @return store - metadata store.    */
annotation|@
name|VisibleForTesting
annotation|@
name|Override
DECL|method|getStore ()
specifier|public
name|MetadataStore
name|getStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
comment|/**    * Given a volume return the corresponding DB key.    * @param volume - Volume name    */
DECL|method|getVolumeKey (String volume)
specifier|public
name|byte
index|[]
name|getVolumeKey
parameter_list|(
name|String
name|volume
parameter_list|)
block|{
name|String
name|dbVolumeName
init|=
name|OzoneConsts
operator|.
name|KSM_VOLUME_PREFIX
operator|+
name|volume
decl_stmt|;
return|return
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|dbVolumeName
argument_list|)
return|;
block|}
comment|/**    * Given a user return the corresponding DB key.    * @param user - User name    */
DECL|method|getUserKey (String user)
specifier|public
name|byte
index|[]
name|getUserKey
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|String
name|dbUserName
init|=
name|OzoneConsts
operator|.
name|KSM_USER_PREFIX
operator|+
name|user
decl_stmt|;
return|return
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|dbUserName
argument_list|)
return|;
block|}
comment|/**    * Given a volume and bucket, return the corresponding DB key.    * @param volume - User name    * @param bucket - Bucket name    */
DECL|method|getBucketKey (String volume, String bucket)
specifier|public
name|byte
index|[]
name|getBucketKey
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|)
block|{
name|String
name|bucketKeyString
init|=
name|OzoneConsts
operator|.
name|KSM_VOLUME_PREFIX
operator|+
name|volume
operator|+
name|OzoneConsts
operator|.
name|KSM_BUCKET_PREFIX
operator|+
name|bucket
decl_stmt|;
return|return
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|bucketKeyString
argument_list|)
return|;
block|}
comment|/**    * @param volume    * @param bucket    * @return    */
DECL|method|getBucketWithDBPrefix (String volume, String bucket)
specifier|private
name|String
name|getBucketWithDBPrefix
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|)
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|OzoneConsts
operator|.
name|KSM_VOLUME_PREFIX
argument_list|)
operator|.
name|append
argument_list|(
name|volume
argument_list|)
operator|.
name|append
argument_list|(
name|OzoneConsts
operator|.
name|KSM_BUCKET_PREFIX
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|bucket
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getKeyWithDBPrefix (String volume, String bucket, String key)
specifier|public
name|String
name|getKeyWithDBPrefix
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|String
name|keyVB
init|=
name|OzoneConsts
operator|.
name|KSM_KEY_PREFIX
operator|+
name|volume
operator|+
name|OzoneConsts
operator|.
name|KSM_KEY_PREFIX
operator|+
name|bucket
operator|+
name|OzoneConsts
operator|.
name|KSM_KEY_PREFIX
decl_stmt|;
return|return
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|key
argument_list|)
condition|?
name|keyVB
else|:
name|keyVB
operator|+
name|key
return|;
block|}
annotation|@
name|Override
DECL|method|getDBKeyBytes (String volume, String bucket, String key)
specifier|public
name|byte
index|[]
name|getDBKeyBytes
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|,
name|String
name|key
parameter_list|)
block|{
return|return
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|getKeyWithDBPrefix
argument_list|(
name|volume
argument_list|,
name|bucket
argument_list|,
name|key
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDeletedKeyName (byte[] keyName)
specifier|public
name|byte
index|[]
name|getDeletedKeyName
parameter_list|(
name|byte
index|[]
name|keyName
parameter_list|)
block|{
return|return
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|DELETING_KEY_PREFIX
operator|+
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|keyName
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getOpenKeyNameBytes (String keyName, int id)
specifier|public
name|byte
index|[]
name|getOpenKeyNameBytes
parameter_list|(
name|String
name|keyName
parameter_list|,
name|int
name|id
parameter_list|)
block|{
return|return
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|OPEN_KEY_PREFIX
operator|+
name|id
operator|+
name|OPEN_KEY_ID_DELIMINATOR
operator|+
name|keyName
argument_list|)
return|;
block|}
comment|/**    * Returns the read lock used on Metadata DB.    * @return readLock    */
annotation|@
name|Override
DECL|method|readLock ()
specifier|public
name|Lock
name|readLock
parameter_list|()
block|{
return|return
name|lock
operator|.
name|readLock
argument_list|()
return|;
block|}
comment|/**    * Returns the write lock used on Metadata DB.    * @return writeLock    */
annotation|@
name|Override
DECL|method|writeLock ()
specifier|public
name|Lock
name|writeLock
parameter_list|()
block|{
return|return
name|lock
operator|.
name|writeLock
argument_list|()
return|;
block|}
comment|/**    * Returns the value associated with this key.    * @param key - key    * @return value    */
annotation|@
name|Override
DECL|method|get (byte[] key)
specifier|public
name|byte
index|[]
name|get
parameter_list|(
name|byte
index|[]
name|key
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|store
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**    * Puts a Key into Metadata DB.    * @param key   - key    * @param value - value    */
annotation|@
name|Override
DECL|method|put (byte[] key, byte[] value)
specifier|public
name|void
name|put
parameter_list|(
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|store
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deletes a Key from Metadata DB.    * @param key   - key    */
DECL|method|delete (byte[] key)
specifier|public
name|void
name|delete
parameter_list|(
name|byte
index|[]
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|store
operator|.
name|delete
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBatch (BatchOperation batch)
specifier|public
name|void
name|writeBatch
parameter_list|(
name|BatchOperation
name|batch
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|store
operator|.
name|writeBatch
argument_list|(
name|batch
argument_list|)
expr_stmt|;
block|}
comment|/**    * Given a volume, check if it is empty, i.e there are no buckets inside it.    * @param volume - Volume name    * @return true if the volume is empty    */
DECL|method|isVolumeEmpty (String volume)
specifier|public
name|boolean
name|isVolumeEmpty
parameter_list|(
name|String
name|volume
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|dbVolumeRootName
init|=
name|OzoneConsts
operator|.
name|KSM_VOLUME_PREFIX
operator|+
name|volume
operator|+
name|OzoneConsts
operator|.
name|KSM_BUCKET_PREFIX
decl_stmt|;
name|byte
index|[]
name|dbVolumeRootKey
init|=
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|dbVolumeRootName
argument_list|)
decl_stmt|;
name|ImmutablePair
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|volumeRoot
init|=
name|store
operator|.
name|peekAround
argument_list|(
literal|0
argument_list|,
name|dbVolumeRootKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|volumeRoot
operator|!=
literal|null
condition|)
block|{
return|return
operator|!
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|volumeRoot
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|startsWith
argument_list|(
name|dbVolumeRootName
argument_list|)
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Given a volume/bucket, check if it is empty,    * i.e there are no keys inside it.    * @param volume - Volume name    * @param bucket - Bucket name    * @return true if the bucket is empty    */
DECL|method|isBucketEmpty (String volume, String bucket)
specifier|public
name|boolean
name|isBucketEmpty
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|keyRootName
init|=
name|getKeyWithDBPrefix
argument_list|(
name|volume
argument_list|,
name|bucket
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|byte
index|[]
name|keyRoot
init|=
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|keyRootName
argument_list|)
decl_stmt|;
name|ImmutablePair
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|firstKey
init|=
name|store
operator|.
name|peekAround
argument_list|(
literal|0
argument_list|,
name|keyRoot
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstKey
operator|!=
literal|null
condition|)
block|{
return|return
operator|!
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|firstKey
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|startsWith
argument_list|(
name|keyRootName
argument_list|)
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|listBuckets (final String volumeName, final String startBucket, final String bucketPrefix, final int maxNumOfBuckets)
specifier|public
name|List
argument_list|<
name|KsmBucketInfo
argument_list|>
name|listBuckets
parameter_list|(
specifier|final
name|String
name|volumeName
parameter_list|,
specifier|final
name|String
name|startBucket
parameter_list|,
specifier|final
name|String
name|bucketPrefix
parameter_list|,
specifier|final
name|int
name|maxNumOfBuckets
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|KsmBucketInfo
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|volumeName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Volume name is required."
argument_list|,
name|ResultCodes
operator|.
name|FAILED_VOLUME_NOT_FOUND
argument_list|)
throw|;
block|}
name|byte
index|[]
name|volumeNameBytes
init|=
name|getVolumeKey
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|store
operator|.
name|get
argument_list|(
name|volumeNameBytes
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Volume "
operator|+
name|volumeName
operator|+
literal|" not found."
argument_list|,
name|ResultCodes
operator|.
name|FAILED_VOLUME_NOT_FOUND
argument_list|)
throw|;
block|}
comment|// A bucket starts with /#volume/#bucket_prefix
name|MetadataKeyFilter
name|filter
init|=
parameter_list|(
name|preKey
parameter_list|,
name|currentKey
parameter_list|,
name|nextKey
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|currentKey
operator|!=
literal|null
condition|)
block|{
name|String
name|bucketNamePrefix
init|=
name|getBucketWithDBPrefix
argument_list|(
name|volumeName
argument_list|,
name|bucketPrefix
argument_list|)
decl_stmt|;
name|String
name|bucket
init|=
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|currentKey
argument_list|)
decl_stmt|;
return|return
name|bucket
operator|.
name|startsWith
argument_list|(
name|bucketNamePrefix
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
decl_stmt|;
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|rangeResult
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|startBucket
argument_list|)
condition|)
block|{
comment|// Since we are excluding start key from the result,
comment|// the maxNumOfBuckets is incremented.
name|rangeResult
operator|=
name|store
operator|.
name|getSequentialRangeKVs
argument_list|(
name|getBucketKey
argument_list|(
name|volumeName
argument_list|,
name|startBucket
argument_list|)
argument_list|,
name|maxNumOfBuckets
operator|+
literal|1
argument_list|,
name|filter
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|rangeResult
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|//Remove start key from result.
name|rangeResult
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|rangeResult
operator|=
name|store
operator|.
name|getSequentialRangeKVs
argument_list|(
literal|null
argument_list|,
name|maxNumOfBuckets
argument_list|,
name|filter
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
range|:
name|rangeResult
control|)
block|{
name|KsmBucketInfo
name|info
init|=
name|KsmBucketInfo
operator|.
name|getFromProtobuf
argument_list|(
name|BucketInfo
operator|.
name|parseFrom
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|listKeys (String volumeName, String bucketName, String startKey, String keyPrefix, int maxKeys)
specifier|public
name|List
argument_list|<
name|KsmKeyInfo
argument_list|>
name|listKeys
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|startKey
parameter_list|,
name|String
name|keyPrefix
parameter_list|,
name|int
name|maxKeys
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|KsmKeyInfo
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|volumeName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Volume name is required."
argument_list|,
name|ResultCodes
operator|.
name|FAILED_VOLUME_NOT_FOUND
argument_list|)
throw|;
block|}
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|bucketName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Bucket name is required."
argument_list|,
name|ResultCodes
operator|.
name|FAILED_BUCKET_NOT_FOUND
argument_list|)
throw|;
block|}
name|byte
index|[]
name|bucketNameBytes
init|=
name|getBucketKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
if|if
condition|(
name|store
operator|.
name|get
argument_list|(
name|bucketNameBytes
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Bucket "
operator|+
name|bucketName
operator|+
literal|" not found."
argument_list|,
name|ResultCodes
operator|.
name|FAILED_BUCKET_NOT_FOUND
argument_list|)
throw|;
block|}
name|MetadataKeyFilter
name|filter
init|=
operator|new
name|KeyPrefixFilter
argument_list|(
name|getKeyWithDBPrefix
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyPrefix
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|rangeResult
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|startKey
argument_list|)
condition|)
block|{
comment|//Since we are excluding start key from the result,
comment|// the maxNumOfBuckets is incremented.
name|rangeResult
operator|=
name|store
operator|.
name|getSequentialRangeKVs
argument_list|(
name|getDBKeyBytes
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|startKey
argument_list|)
argument_list|,
name|maxKeys
operator|+
literal|1
argument_list|,
name|filter
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|rangeResult
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|//Remove start key from result.
name|rangeResult
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|rangeResult
operator|=
name|store
operator|.
name|getSequentialRangeKVs
argument_list|(
literal|null
argument_list|,
name|maxKeys
argument_list|,
name|filter
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
range|:
name|rangeResult
control|)
block|{
name|KsmKeyInfo
name|info
init|=
name|KsmKeyInfo
operator|.
name|getFromProtobuf
argument_list|(
name|KeyInfo
operator|.
name|parseFrom
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|listVolumes (String userName, String prefix, String startKey, int maxKeys)
specifier|public
name|List
argument_list|<
name|KsmVolumeArgs
argument_list|>
name|listVolumes
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|prefix
parameter_list|,
name|String
name|startKey
parameter_list|,
name|int
name|maxKeys
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|KsmVolumeArgs
argument_list|>
name|result
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|VolumeList
name|volumes
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|userName
argument_list|)
condition|)
block|{
name|volumes
operator|=
name|getAllVolumes
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|volumes
operator|=
name|getVolumesByUser
argument_list|(
name|userName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|volumes
operator|==
literal|null
operator|||
name|volumes
operator|.
name|getVolumeNamesCount
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|result
return|;
block|}
name|boolean
name|startKeyFound
init|=
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|startKey
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|volumeName
range|:
name|volumes
operator|.
name|getVolumeNamesList
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|volumeName
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
if|if
condition|(
operator|!
name|startKeyFound
operator|&&
name|volumeName
operator|.
name|equals
argument_list|(
name|startKey
argument_list|)
condition|)
block|{
name|startKeyFound
operator|=
literal|true
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|startKeyFound
operator|&&
name|result
operator|.
name|size
argument_list|()
operator|<
name|maxKeys
condition|)
block|{
name|byte
index|[]
name|volumeInfo
init|=
name|store
operator|.
name|get
argument_list|(
name|this
operator|.
name|getVolumeKey
argument_list|(
name|volumeName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|volumeInfo
operator|==
literal|null
condition|)
block|{
comment|// Could not get volume info by given volume name,
comment|// since the volume name is loaded from db,
comment|// this probably means ksm db is corrupted or some entries are
comment|// accidentally removed.
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Volume info not found for "
operator|+
name|volumeName
argument_list|,
name|ResultCodes
operator|.
name|FAILED_VOLUME_NOT_FOUND
argument_list|)
throw|;
block|}
name|VolumeInfo
name|info
init|=
name|VolumeInfo
operator|.
name|parseFrom
argument_list|(
name|volumeInfo
argument_list|)
decl_stmt|;
name|KsmVolumeArgs
name|volumeArgs
init|=
name|KsmVolumeArgs
operator|.
name|getFromProtobuf
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|volumeArgs
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|getVolumesByUser (String userName)
specifier|private
name|VolumeList
name|getVolumesByUser
parameter_list|(
name|String
name|userName
parameter_list|)
throws|throws
name|KSMException
block|{
return|return
name|getVolumesByUser
argument_list|(
name|getUserKey
argument_list|(
name|userName
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getVolumesByUser (byte[] userNameKey)
specifier|private
name|VolumeList
name|getVolumesByUser
parameter_list|(
name|byte
index|[]
name|userNameKey
parameter_list|)
throws|throws
name|KSMException
block|{
name|VolumeList
name|volumes
init|=
literal|null
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|volumesInBytes
init|=
name|store
operator|.
name|get
argument_list|(
name|userNameKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|volumesInBytes
operator|==
literal|null
condition|)
block|{
comment|// No volume found for this user, return an empty list
return|return
name|VolumeList
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
name|volumes
operator|=
name|VolumeList
operator|.
name|parseFrom
argument_list|(
name|volumesInBytes
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Unable to get volumes info by the given user, "
operator|+
literal|"metadata might be corrupted"
argument_list|,
name|e
argument_list|,
name|ResultCodes
operator|.
name|FAILED_METADATA_ERROR
argument_list|)
throw|;
block|}
return|return
name|volumes
return|;
block|}
DECL|method|getAllVolumes ()
specifier|private
name|VolumeList
name|getAllVolumes
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Scan all users in database
name|KeyPrefixFilter
name|filter
init|=
operator|new
name|KeyPrefixFilter
argument_list|(
name|OzoneConsts
operator|.
name|KSM_USER_PREFIX
argument_list|)
decl_stmt|;
comment|// We are not expecting a huge number of users per cluster,
comment|// it should be fine to scan all users in db and return us a
comment|// list of volume names in string per user.
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|rangeKVs
init|=
name|store
operator|.
name|getSequentialRangeKVs
argument_list|(
literal|null
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|filter
argument_list|)
decl_stmt|;
name|VolumeList
operator|.
name|Builder
name|builder
init|=
name|VolumeList
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
range|:
name|rangeKVs
control|)
block|{
name|VolumeList
name|volumes
init|=
name|this
operator|.
name|getVolumesByUser
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addAllVolumeNames
argument_list|(
name|volumes
operator|.
name|getVolumeNamesList
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPendingDeletionKeys (final int count)
specifier|public
name|List
argument_list|<
name|BlockGroup
argument_list|>
name|getPendingDeletionKeys
parameter_list|(
specifier|final
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|BlockGroup
argument_list|>
name|keyBlocksList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|final
name|MetadataKeyFilter
name|deletingKeyFilter
init|=
operator|new
name|KeyPrefixFilter
argument_list|(
name|DELETING_KEY_PREFIX
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|rangeResult
init|=
name|store
operator|.
name|getSequentialRangeKVs
argument_list|(
literal|null
argument_list|,
name|count
argument_list|,
name|deletingKeyFilter
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
range|:
name|rangeResult
control|)
block|{
name|KsmKeyInfo
name|info
init|=
name|KsmKeyInfo
operator|.
name|getFromProtobuf
argument_list|(
name|KeyInfo
operator|.
name|parseFrom
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// Get block keys as a list.
name|List
argument_list|<
name|String
argument_list|>
name|item
init|=
name|info
operator|.
name|getKeyLocationList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|KsmKeyLocationInfo
operator|::
name|getBlockID
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|BlockGroup
name|keyBlocks
init|=
name|BlockGroup
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKeyName
argument_list|(
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
operator|.
name|addAllBlockIDs
argument_list|(
name|item
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|keyBlocksList
operator|.
name|add
argument_list|(
name|keyBlocks
argument_list|)
expr_stmt|;
block|}
return|return
name|keyBlocksList
return|;
block|}
block|}
end_class

end_unit

