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
name|hdds
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
name|OmPrefixInfo
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
name|helpers
operator|.
name|S3SecretValue
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
name|lock
operator|.
name|OzoneManagerLock
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
name|OzoneTokenIdentifier
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
name|DBStore
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
name|Table
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

begin_comment
comment|/**  * OM metadata manager interface.  */
end_comment

begin_interface
DECL|interface|OMMetadataManager
specifier|public
interface|interface
name|OMMetadataManager
block|{
comment|/**    * Start metadata manager.    *    * @param configuration    * @throws IOException    */
DECL|method|start (OzoneConfiguration configuration)
name|void
name|start
parameter_list|(
name|OzoneConfiguration
name|configuration
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Stop metadata manager.    */
DECL|method|stop ()
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * Get metadata store.    *    * @return metadata store.    */
annotation|@
name|VisibleForTesting
DECL|method|getStore ()
name|DBStore
name|getStore
parameter_list|()
function_decl|;
comment|/**    * Returns the OzoneManagerLock used on Metadata DB.    *    * @return OzoneManagerLock    */
DECL|method|getLock ()
name|OzoneManagerLock
name|getLock
parameter_list|()
function_decl|;
comment|/**    * Given a volume return the corresponding DB key.    *    * @param volume - Volume name    */
DECL|method|getVolumeKey (String volume)
name|String
name|getVolumeKey
parameter_list|(
name|String
name|volume
parameter_list|)
function_decl|;
comment|/**    * Given a user return the corresponding DB key.    *    * @param user - User name    */
DECL|method|getUserKey (String user)
name|String
name|getUserKey
parameter_list|(
name|String
name|user
parameter_list|)
function_decl|;
comment|/**    * Given a volume and bucket, return the corresponding DB key.    *    * @param volume - User name    * @param bucket - Bucket name    */
DECL|method|getBucketKey (String volume, String bucket)
name|String
name|getBucketKey
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|)
function_decl|;
comment|/**    * Given a volume, bucket and a key, return the corresponding DB key.    *    * @param volume - volume name    * @param bucket - bucket name    * @param key    - key name    * @return DB key as String.    */
DECL|method|getOzoneKey (String volume, String bucket, String key)
name|String
name|getOzoneKey
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
comment|/**    * Given a volume, bucket and a key, return the corresponding DB directory    * key.    *    * @param volume - volume name    * @param bucket - bucket name    * @param key    - key name    * @return DB directory key as String.    */
DECL|method|getOzoneDirKey (String volume, String bucket, String key)
name|String
name|getOzoneDirKey
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
comment|/**    * Returns the DB key name of a open key in OM metadata store. Should be    * #open# prefix followed by actual key name.    *    * @param volume - volume name    * @param bucket - bucket name    * @param key - key name    * @param id - the id for this open    * @return bytes of DB key.    */
DECL|method|getOpenKey (String volume, String bucket, String key, long id)
name|String
name|getOpenKey
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|,
name|String
name|key
parameter_list|,
name|long
name|id
parameter_list|)
function_decl|;
comment|/**    * Given a volume, check if it is empty, i.e there are no buckets inside it.    *    * @param volume - Volume name    */
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
comment|/**    * Given a volume/bucket, check if it is empty, i.e there are no keys inside    * it.    *    * @param volume - Volume name    * @param bucket - Bucket name    * @return true if the bucket is empty    */
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
comment|/**    * Returns a list of buckets represented by {@link OmBucketInfo} in the given    * volume.    *    * @param volumeName the name of the volume. This argument is required, this    * method returns buckets in this given volume.    * @param startBucket the start bucket name. Only the buckets whose name is    * after this value will be included in the result. This key is excluded from    * the result.    * @param bucketPrefix bucket name prefix. Only the buckets whose name has    * this prefix will be included in the result.    * @param maxNumOfBuckets the maximum number of buckets to return. It ensures    * the size of the result will not exceed this limit.    * @return a list of buckets.    * @throws IOException    */
DECL|method|listBuckets (String volumeName, String startBucket, String bucketPrefix, int maxNumOfBuckets)
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
function_decl|;
comment|/**    * Returns a list of keys represented by {@link OmKeyInfo} in the given    * bucket.    *    * @param volumeName the name of the volume.    * @param bucketName the name of the bucket.    * @param startKey the start key name, only the keys whose name is after this    * value will be included in the result. This key is excluded from the    * result.    * @param keyPrefix key name prefix, only the keys whose name has this prefix    * will be included in the result.    * @param maxKeys the maximum number of keys to return. It ensures the size of    * the result will not exceed this limit.    * @return a list of keys.    * @throws IOException    */
DECL|method|listKeys (String volumeName, String bucketName, String startKey, String keyPrefix, int maxKeys)
name|List
argument_list|<
name|OmKeyInfo
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
comment|/**    * Returns a list of volumes owned by a given user; if user is null, returns    * all volumes.    *    * @param userName volume owner    * @param prefix the volume prefix used to filter the listing result.    * @param startKey the start volume name determines where to start listing    * from, this key is excluded from the result.    * @param maxKeys the maximum number of volumes to return.    * @return a list of {@link OmVolumeArgs}    * @throws IOException    */
DECL|method|listVolumes (String userName, String prefix, String startKey, int maxKeys)
name|List
argument_list|<
name|OmVolumeArgs
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
comment|/**    * Returns a list of pending deletion key info that ups to the given count.    * Each entry is a {@link BlockGroup}, which contains the info about the key    * name and all its associated block IDs. A pending deletion key is stored    * with #deleting# prefix in OM DB.    *    * @param count max number of keys to return.    * @return a list of {@link BlockGroup} represent keys and blocks.    * @throws IOException    */
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
comment|/**    * Returns a list of all still open key info. Which contains the info about    * the key name and all its associated block IDs. A pending open key has    * prefix #open# in OM DB.    *    * @return a list of {@link BlockGroup} representing keys and blocks.    * @throws IOException    */
DECL|method|getExpiredOpenKeys ()
name|List
argument_list|<
name|BlockGroup
argument_list|>
name|getExpiredOpenKeys
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the user Table.    *    * @return UserTable.    */
DECL|method|getUserTable ()
name|Table
argument_list|<
name|String
argument_list|,
name|VolumeList
argument_list|>
name|getUserTable
parameter_list|()
function_decl|;
comment|/**    * Returns the Volume Table.    *    * @return VolumeTable.    */
DECL|method|getVolumeTable ()
name|Table
argument_list|<
name|String
argument_list|,
name|OmVolumeArgs
argument_list|>
name|getVolumeTable
parameter_list|()
function_decl|;
comment|/**    * Returns the BucketTable.    *    * @return BucketTable.    */
DECL|method|getBucketTable ()
name|Table
argument_list|<
name|String
argument_list|,
name|OmBucketInfo
argument_list|>
name|getBucketTable
parameter_list|()
function_decl|;
comment|/**    * Returns the KeyTable.    *    * @return KeyTable.    */
DECL|method|getKeyTable ()
name|Table
argument_list|<
name|String
argument_list|,
name|OmKeyInfo
argument_list|>
name|getKeyTable
parameter_list|()
function_decl|;
comment|/**    * Get Deleted Table.    *    * @return Deleted Table.    */
DECL|method|getDeletedTable ()
name|Table
argument_list|<
name|String
argument_list|,
name|RepeatedOmKeyInfo
argument_list|>
name|getDeletedTable
parameter_list|()
function_decl|;
comment|/**    * Gets the OpenKeyTable.    *    * @return Table.    */
DECL|method|getOpenKeyTable ()
name|Table
argument_list|<
name|String
argument_list|,
name|OmKeyInfo
argument_list|>
name|getOpenKeyTable
parameter_list|()
function_decl|;
comment|/**    * Gets the DelegationTokenTable.    *    * @return Table.    */
DECL|method|getDelegationTokenTable ()
name|Table
argument_list|<
name|OzoneTokenIdentifier
argument_list|,
name|Long
argument_list|>
name|getDelegationTokenTable
parameter_list|()
function_decl|;
comment|/**    * Gets the S3Bucket to Ozone Volume/bucket mapping table.    *    * @return Table.    */
DECL|method|getS3Table ()
name|Table
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getS3Table
parameter_list|()
function_decl|;
comment|/**    * Gets the Ozone prefix path to its acl mapping table.    * @return Table.    */
DECL|method|getPrefixTable ()
name|Table
argument_list|<
name|String
argument_list|,
name|OmPrefixInfo
argument_list|>
name|getPrefixTable
parameter_list|()
function_decl|;
comment|/**    * Returns the DB key name of a multipart upload key in OM metadata store.    *    * @param volume - volume name    * @param bucket - bucket name    * @param key - key name    * @param uploadId - the upload id for this key    * @return bytes of DB key.    */
DECL|method|getMultipartKey (String volume, String bucket, String key, String uploadId)
name|String
name|getMultipartKey
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|uploadId
parameter_list|)
function_decl|;
comment|/**    * Gets the multipart info table which holds the information about    * multipart upload information of the keys.    * @return Table    */
DECL|method|getMultipartInfoTable ()
name|Table
argument_list|<
name|String
argument_list|,
name|OmMultipartKeyInfo
argument_list|>
name|getMultipartInfoTable
parameter_list|()
function_decl|;
comment|/**    * Gets the S3 Secrets table.    * @return Table    */
DECL|method|getS3SecretTable ()
name|Table
argument_list|<
name|String
argument_list|,
name|S3SecretValue
argument_list|>
name|getS3SecretTable
parameter_list|()
function_decl|;
comment|/**    * Returns number of rows in a table.  This should not be used for very    * large tables.    * @param table    * @return long    * @throws IOException    */
DECL|method|countRowsInTable (Table<KEY, VALUE> table)
parameter_list|<
name|KEY
parameter_list|,
name|VALUE
parameter_list|>
name|long
name|countRowsInTable
parameter_list|(
name|Table
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
name|table
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns an estimated number of rows in a table.  This is much quicker    * than {@link OMMetadataManager#countRowsInTable} but the result can be    * inaccurate.    * @param table Table    * @return long Estimated number of rows in the table.    * @throws IOException    */
DECL|method|countEstimatedRowsInTable (Table<KEY, VALUE> table)
parameter_list|<
name|KEY
parameter_list|,
name|VALUE
parameter_list|>
name|long
name|countEstimatedRowsInTable
parameter_list|(
name|Table
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
name|table
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Return the existing upload keys which includes volumeName, bucketName,    * keyName.    */
DECL|method|getMultipartUploadKeys (String volumeName, String bucketName, String prefix)
name|List
argument_list|<
name|String
argument_list|>
name|getMultipartUploadKeys
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|prefix
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

