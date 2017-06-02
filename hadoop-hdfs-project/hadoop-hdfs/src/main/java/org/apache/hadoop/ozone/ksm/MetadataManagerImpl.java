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
name|LevelDBStore
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
name|DBIterator
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
name|Options
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
name|WriteBatch
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
DECL|class|MetadataManagerImpl
specifier|public
class|class
name|MetadataManagerImpl
implements|implements
name|MetadataManager
block|{
DECL|field|store
specifier|private
specifier|final
name|LevelDBStore
name|store
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|ReadWriteLock
name|lock
decl_stmt|;
DECL|method|MetadataManagerImpl (OzoneConfiguration conf)
specifier|public
name|MetadataManagerImpl
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
name|Options
name|options
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|options
operator|.
name|cacheSize
argument_list|(
name|cacheSize
operator|*
name|OzoneConsts
operator|.
name|MB
argument_list|)
expr_stmt|;
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
operator|new
name|LevelDBStore
argument_list|(
name|ksmDBFile
argument_list|,
name|options
argument_list|)
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
annotation|@
name|Override
DECL|method|getDBKeyForKey (String volume, String bucket, String key)
specifier|public
name|byte
index|[]
name|getDBKeyForKey
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
name|keyKeyString
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
operator|+
name|OzoneConsts
operator|.
name|KSM_KEY_PREFIX
operator|+
name|key
decl_stmt|;
return|return
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|keyKeyString
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
comment|/**    * Performs a batch Put and Delete from Metadata DB.    * Can be used to do multiple puts and deletes atomically.    * @param putList - list of key and value pairs to put to Metadata DB.    * @param delList - list of keys to delete from Metadata DB.    */
annotation|@
name|Override
DECL|method|batchPutDelete (List<Map.Entry<byte[], byte[]>> putList, List<byte[]> delList)
specifier|public
name|void
name|batchPutDelete
parameter_list|(
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
name|putList
parameter_list|,
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|delList
parameter_list|)
throws|throws
name|IOException
block|{
name|WriteBatch
name|batch
init|=
name|store
operator|.
name|createWriteBatch
argument_list|()
decl_stmt|;
name|putList
operator|.
name|forEach
argument_list|(
name|entry
lambda|->
name|batch
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|delList
operator|.
name|forEach
argument_list|(
name|entry
lambda|->
name|batch
operator|.
name|delete
argument_list|(
name|entry
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|store
operator|.
name|commitWriteBatch
argument_list|(
name|batch
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|store
operator|.
name|closeWriteBatch
argument_list|(
name|batch
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Performs a batch Put to Metadata DB.    * Can be used to do multiple puts atomically.    * @param list - list of Map.Entry    */
annotation|@
name|Override
DECL|method|batchPut (List<Map.Entry<byte[], byte[]>> list)
specifier|public
name|void
name|batchPut
parameter_list|(
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
name|list
parameter_list|)
throws|throws
name|IOException
block|{
name|WriteBatch
name|batch
init|=
name|store
operator|.
name|createWriteBatch
argument_list|()
decl_stmt|;
name|list
operator|.
name|forEach
argument_list|(
name|entry
lambda|->
name|batch
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|store
operator|.
name|commitWriteBatch
argument_list|(
name|batch
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|store
operator|.
name|closeWriteBatch
argument_list|(
name|batch
argument_list|)
expr_stmt|;
block|}
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
try|try
init|(
name|DBIterator
name|iterator
init|=
name|store
operator|.
name|getIterator
argument_list|()
init|)
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
comment|// Seek to the root of the volume and look for the next key
name|iterator
operator|.
name|seek
argument_list|(
name|dbVolumeRootKey
argument_list|)
expr_stmt|;
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|firstBucketKey
init|=
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
comment|// if the key starts with /<volume name>/
comment|// then there is at least one bucket
return|return
operator|!
name|firstBucketKey
operator|.
name|startsWith
argument_list|(
name|dbVolumeRootName
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

