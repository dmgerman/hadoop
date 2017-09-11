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
name|MetadataStore
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
name|concurrent
operator|.
name|locks
operator|.
name|Lock
import|;
end_import

begin_comment
comment|/**  * KSM metadata manager interface.  */
end_comment

begin_interface
DECL|interface|KSMMetadataManager
specifier|public
interface|interface
name|KSMMetadataManager
block|{
comment|/**    * Start metadata manager.    */
DECL|method|start ()
name|void
name|start
parameter_list|()
function_decl|;
comment|/**    * Stop metadata manager.    */
DECL|method|stop ()
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get metadata store.    * @return metadata store.    */
annotation|@
name|VisibleForTesting
DECL|method|getStore ()
name|MetadataStore
name|getStore
parameter_list|()
function_decl|;
comment|/**    * Returns the read lock used on Metadata DB.    * @return readLock    */
DECL|method|readLock ()
name|Lock
name|readLock
parameter_list|()
function_decl|;
comment|/**    * Returns the write lock used on Metadata DB.    * @return writeLock    */
DECL|method|writeLock ()
name|Lock
name|writeLock
parameter_list|()
function_decl|;
comment|/**    * Returns the value associated with this key.    * @param key - key    * @return value    */
DECL|method|get (byte[] key)
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
function_decl|;
comment|/**    * Puts a Key into Metadata DB.    * @param key   - key    * @param value - value    */
DECL|method|put (byte[] key, byte[] value)
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
function_decl|;
comment|/**    * Deletes a Key from Metadata DB.    * @param key   - key    */
DECL|method|delete (byte[] key)
name|void
name|delete
parameter_list|(
name|byte
index|[]
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Atomic write a batch of operations.    * @param batch    * @throws IOException    */
DECL|method|writeBatch (BatchOperation batch)
name|void
name|writeBatch
parameter_list|(
name|BatchOperation
name|batch
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Given a volume return the corresponding DB key.    * @param volume - Volume name    */
DECL|method|getVolumeKey (String volume)
name|byte
index|[]
name|getVolumeKey
parameter_list|(
name|String
name|volume
parameter_list|)
function_decl|;
comment|/**    * Given a user return the corresponding DB key.    * @param user - User name    */
DECL|method|getUserKey (String user)
name|byte
index|[]
name|getUserKey
parameter_list|(
name|String
name|user
parameter_list|)
function_decl|;
comment|/**    * Given a volume and bucket, return the corresponding DB key.    * @param volume - User name    * @param bucket - Bucket name    */
DECL|method|getBucketKey (String volume, String bucket)
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
function_decl|;
comment|/**    * Given a volume, bucket and a key, return the corresponding DB key.    * @param volume - volume name    * @param bucket - bucket name    * @param key - key name    * @return bytes of DB key.    */
DECL|method|getDBKeyForKey (String volume, String bucket, String key)
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
function_decl|;
comment|/**    * Returns the DB key name of a deleted key in KSM metadata store.    * The name for a deleted key has prefix #deleting# followed by    * the actual key name.    * @param keyName - key name    * @return bytes of DB key.    */
DECL|method|getDeletedKeyName (byte[] keyName)
name|byte
index|[]
name|getDeletedKeyName
parameter_list|(
name|byte
index|[]
name|keyName
parameter_list|)
function_decl|;
comment|/**    * Given a volume, check if it is empty,    * i.e there are no buckets inside it.    * @param volume - Volume name    */
DECL|method|isVolumeEmpty (String volume)
name|boolean
name|isVolumeEmpty
parameter_list|(
name|String
name|volume
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Given a volume/bucket, check if it is empty,    * i.e there are no keys inside it.    * @param volume - Volume name    * @param  bucket - Bucket name    * @return true if the bucket is empty    */
DECL|method|isBucketEmpty (String volume, String bucket)
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
function_decl|;
comment|/**    * Returns a list of buckets represented by {@link KsmBucketInfo}    * in the given volume.    *    * @param volumeName    *   the name of the volume. This argument is required,    *   this method returns buckets in this given volume.    * @param startBucket    *   the start bucket name. Only the buckets whose name is    *   after this value will be included in the result.    *   This key is excluded from the result.    * @param bucketPrefix    *   bucket name prefix. Only the buckets whose name has    *   this prefix will be included in the result.    * @param maxNumOfBuckets    *   the maximum number of buckets to return. It ensures    *   the size of the result will not exceed this limit.    * @return a list of buckets.    * @throws IOException    */
DECL|method|listBuckets (String volumeName, String startBucket, String bucketPrefix, int maxNumOfBuckets)
name|List
argument_list|<
name|KsmBucketInfo
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
function_decl|;
comment|/**    * Returns a list of keys represented by {@link KsmKeyInfo}    * in the given bucket.    *    * @param volumeName    *   the name of the volume.    * @param bucketName    *   the name of the bucket.    * @param startKey    *   the start key name, only the keys whose name is    *   after this value will be included in the result.    *   This key is excluded from the result.    * @param keyPrefix    *   key name prefix, only the keys whose name has    *   this prefix will be included in the result.    * @param maxKeys    *   the maximum number of keys to return. It ensures    *   the size of the result will not exceed this limit.    * @return a list of keys.    * @throws IOException    */
DECL|method|listKeys (String volumeName, String bucketName, String startKey, String keyPrefix, int maxKeys)
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
function_decl|;
comment|/**    * Returns a list of volumes owned by a given user; if user is null,    * returns all volumes.    *    * @param userName    *   volume owner    * @param prefix    *   the volume prefix used to filter the listing result.    * @param startKey    *   the start volume name determines where to start listing from,    *   this key is excluded from the result.    * @param maxKeys    *   the maximum number of volumes to return.    * @return a list of {@link KsmVolumeArgs}    * @throws IOException    */
DECL|method|listVolumes (String userName, String prefix, String startKey, int maxKeys)
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
function_decl|;
comment|/**    * Returns a list of pending deletion key info that ups to the given count.    * Each entry is a {@link BlockGroup}, which contains the info about the    * key name and all its associated block IDs. A pending deletion key is    * stored with #deleting# prefix in KSM DB.    *    * @param count max number of keys to return.    * @return a list of {@link BlockGroup} represent keys and blocks.    * @throws IOException    */
DECL|method|getPendingDeletionKeys (int count)
name|List
argument_list|<
name|BlockGroup
argument_list|>
name|getPendingDeletionKeys
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

