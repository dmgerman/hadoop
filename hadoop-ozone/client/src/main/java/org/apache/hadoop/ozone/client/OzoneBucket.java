begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
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
name|conf
operator|.
name|Configuration
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
name|hdds
operator|.
name|client
operator|.
name|ReplicationFactor
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
name|client
operator|.
name|ReplicationType
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
name|client
operator|.
name|HddsClientUtils
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
name|OzoneConfigKeys
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
name|client
operator|.
name|io
operator|.
name|OzoneInputStream
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
name|client
operator|.
name|io
operator|.
name|OzoneOutputStream
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
name|client
operator|.
name|protocol
operator|.
name|ClientProtocol
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
name|helpers
operator|.
name|OmMultipartInfo
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
name|OmMultipartUploadCompleteInfo
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
name|WithMetadata
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|NoSuchElementException
import|;
end_import

begin_comment
comment|/**  * A class that encapsulates OzoneBucket.  */
end_comment

begin_class
DECL|class|OzoneBucket
specifier|public
class|class
name|OzoneBucket
extends|extends
name|WithMetadata
block|{
comment|/**    * The proxy used for connecting to the cluster and perform    * client operations.    */
DECL|field|proxy
specifier|private
specifier|final
name|ClientProtocol
name|proxy
decl_stmt|;
comment|/**    * Name of the volume in which the bucket belongs to.    */
DECL|field|volumeName
specifier|private
specifier|final
name|String
name|volumeName
decl_stmt|;
comment|/**    * Name of the bucket.    */
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**    * Default replication factor to be used while creating keys.    */
DECL|field|defaultReplication
specifier|private
specifier|final
name|ReplicationFactor
name|defaultReplication
decl_stmt|;
comment|/**    * Default replication type to be used while creating keys.    */
DECL|field|defaultReplicationType
specifier|private
specifier|final
name|ReplicationType
name|defaultReplicationType
decl_stmt|;
comment|/**    * Bucket ACLs.    */
DECL|field|acls
specifier|private
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
decl_stmt|;
comment|/**    * Type of storage to be used for this bucket.    * [RAM_DISK, SSD, DISK, ARCHIVE]    */
DECL|field|storageType
specifier|private
name|StorageType
name|storageType
decl_stmt|;
comment|/**    * Bucket Version flag.    */
DECL|field|versioning
specifier|private
name|Boolean
name|versioning
decl_stmt|;
comment|/**    * Cache size to be used for listKey calls.    */
DECL|field|listCacheSize
specifier|private
name|int
name|listCacheSize
decl_stmt|;
comment|/**    * Creation time of the bucket.    */
DECL|field|creationTime
specifier|private
name|long
name|creationTime
decl_stmt|;
comment|/**    * Constructs OzoneBucket instance.    * @param conf Configuration object.    * @param proxy ClientProtocol proxy.    * @param volumeName Name of the volume the bucket belongs to.    * @param bucketName Name of the bucket.    * @param acls ACLs associated with the bucket.    * @param storageType StorageType of the bucket.    * @param versioning versioning status of the bucket.    * @param creationTime creation time of the bucket.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"parameternumber"
argument_list|)
DECL|method|OzoneBucket (Configuration conf, ClientProtocol proxy, String volumeName, String bucketName, List<OzoneAcl> acls, StorageType storageType, Boolean versioning, long creationTime, Map<String, String> metadata)
specifier|public
name|OzoneBucket
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ClientProtocol
name|proxy
parameter_list|,
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
parameter_list|,
name|StorageType
name|storageType
parameter_list|,
name|Boolean
name|versioning
parameter_list|,
name|long
name|creationTime
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|proxy
argument_list|,
literal|"Client proxy is not set."
argument_list|)
expr_stmt|;
name|this
operator|.
name|proxy
operator|=
name|proxy
expr_stmt|;
name|this
operator|.
name|volumeName
operator|=
name|volumeName
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|bucketName
expr_stmt|;
name|this
operator|.
name|acls
operator|=
name|acls
expr_stmt|;
name|this
operator|.
name|storageType
operator|=
name|storageType
expr_stmt|;
name|this
operator|.
name|versioning
operator|=
name|versioning
expr_stmt|;
name|this
operator|.
name|listCacheSize
operator|=
name|HddsClientUtils
operator|.
name|getListCacheSize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|creationTime
operator|=
name|creationTime
expr_stmt|;
name|this
operator|.
name|defaultReplication
operator|=
name|ReplicationFactor
operator|.
name|valueOf
argument_list|(
name|conf
operator|.
name|getInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_REPLICATION
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_REPLICATION_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultReplicationType
operator|=
name|ReplicationType
operator|.
name|valueOf
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_REPLICATION_TYPE
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_REPLICATION_TYPE_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
annotation|@
name|SuppressWarnings
argument_list|(
literal|"parameternumber"
argument_list|)
DECL|method|OzoneBucket (String volumeName, String name, ReplicationFactor defaultReplication, ReplicationType defaultReplicationType, List<OzoneAcl> acls, StorageType storageType, Boolean versioning, long creationTime)
name|OzoneBucket
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|name
parameter_list|,
name|ReplicationFactor
name|defaultReplication
parameter_list|,
name|ReplicationType
name|defaultReplicationType
parameter_list|,
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
parameter_list|,
name|StorageType
name|storageType
parameter_list|,
name|Boolean
name|versioning
parameter_list|,
name|long
name|creationTime
parameter_list|)
block|{
name|this
operator|.
name|proxy
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|volumeName
operator|=
name|volumeName
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|defaultReplication
operator|=
name|defaultReplication
expr_stmt|;
name|this
operator|.
name|defaultReplicationType
operator|=
name|defaultReplicationType
expr_stmt|;
name|this
operator|.
name|acls
operator|=
name|acls
expr_stmt|;
name|this
operator|.
name|storageType
operator|=
name|storageType
expr_stmt|;
name|this
operator|.
name|versioning
operator|=
name|versioning
expr_stmt|;
name|this
operator|.
name|creationTime
operator|=
name|creationTime
expr_stmt|;
block|}
comment|/**    * Returns Volume Name.    *    * @return volumeName    */
DECL|method|getVolumeName ()
specifier|public
name|String
name|getVolumeName
parameter_list|()
block|{
return|return
name|volumeName
return|;
block|}
comment|/**    * Returns Bucket Name.    *    * @return bucketName    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**    * Returns ACL's associated with the Bucket.    *    * @return acls    */
DECL|method|getAcls ()
specifier|public
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|getAcls
parameter_list|()
block|{
return|return
name|acls
return|;
block|}
comment|/**    * Returns StorageType of the Bucket.    *    * @return storageType    */
DECL|method|getStorageType ()
specifier|public
name|StorageType
name|getStorageType
parameter_list|()
block|{
return|return
name|storageType
return|;
block|}
comment|/**    * Returns Versioning associated with the Bucket.    *    * @return versioning    */
DECL|method|getVersioning ()
specifier|public
name|Boolean
name|getVersioning
parameter_list|()
block|{
return|return
name|versioning
return|;
block|}
comment|/**    * Returns creation time of the Bucket.    *    * @return creation time of the bucket    */
DECL|method|getCreationTime ()
specifier|public
name|long
name|getCreationTime
parameter_list|()
block|{
return|return
name|creationTime
return|;
block|}
comment|/**    * Adds ACLs to the Bucket.    * @param addAcls ACLs to be added    * @throws IOException    */
DECL|method|addAcls (List<OzoneAcl> addAcls)
specifier|public
name|void
name|addAcls
parameter_list|(
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|addAcls
parameter_list|)
throws|throws
name|IOException
block|{
name|proxy
operator|.
name|addBucketAcls
argument_list|(
name|volumeName
argument_list|,
name|name
argument_list|,
name|addAcls
argument_list|)
expr_stmt|;
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
name|acls
operator|.
name|contains
argument_list|(
name|acl
argument_list|)
argument_list|)
operator|.
name|forEach
argument_list|(
name|acls
operator|::
name|add
argument_list|)
expr_stmt|;
block|}
comment|/**    * Removes ACLs from the bucket.    * @param removeAcls ACLs to be removed    * @throws IOException    */
DECL|method|removeAcls (List<OzoneAcl> removeAcls)
specifier|public
name|void
name|removeAcls
parameter_list|(
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|removeAcls
parameter_list|)
throws|throws
name|IOException
block|{
name|proxy
operator|.
name|removeBucketAcls
argument_list|(
name|volumeName
argument_list|,
name|name
argument_list|,
name|removeAcls
argument_list|)
expr_stmt|;
name|acls
operator|.
name|removeAll
argument_list|(
name|removeAcls
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets/Changes the storage type of the bucket.    * @param newStorageType Storage type to be set    * @throws IOException    */
DECL|method|setStorageType (StorageType newStorageType)
specifier|public
name|void
name|setStorageType
parameter_list|(
name|StorageType
name|newStorageType
parameter_list|)
throws|throws
name|IOException
block|{
name|proxy
operator|.
name|setBucketStorageType
argument_list|(
name|volumeName
argument_list|,
name|name
argument_list|,
name|newStorageType
argument_list|)
expr_stmt|;
name|storageType
operator|=
name|newStorageType
expr_stmt|;
block|}
comment|/**    * Enable/Disable versioning of the bucket.    * @param newVersioning    * @throws IOException    */
DECL|method|setVersioning (Boolean newVersioning)
specifier|public
name|void
name|setVersioning
parameter_list|(
name|Boolean
name|newVersioning
parameter_list|)
throws|throws
name|IOException
block|{
name|proxy
operator|.
name|setBucketVersioning
argument_list|(
name|volumeName
argument_list|,
name|name
argument_list|,
name|newVersioning
argument_list|)
expr_stmt|;
name|versioning
operator|=
name|newVersioning
expr_stmt|;
block|}
comment|/**    * Creates a new key in the bucket, with default replication type RATIS and    * with replication factor THREE.    * @param key Name of the key to be created.    * @param size Size of the data the key will point to.    * @return OzoneOutputStream to which the data has to be written.    * @throws IOException    */
DECL|method|createKey (String key, long size)
specifier|public
name|OzoneOutputStream
name|createKey
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createKey
argument_list|(
name|key
argument_list|,
name|size
argument_list|,
name|defaultReplicationType
argument_list|,
name|defaultReplication
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Creates a new key in the bucket.    * @param key Name of the key to be created.    * @param size Size of the data the key will point to.    * @param type Replication type to be used.    * @param factor Replication factor of the key.    * @return OzoneOutputStream to which the data has to be written.    * @throws IOException    */
DECL|method|createKey (String key, long size, ReplicationType type, ReplicationFactor factor, Map<String, String> keyMetadata)
specifier|public
name|OzoneOutputStream
name|createKey
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|size
parameter_list|,
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|keyMetadata
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|proxy
operator|.
name|createKey
argument_list|(
name|volumeName
argument_list|,
name|name
argument_list|,
name|key
argument_list|,
name|size
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|keyMetadata
argument_list|)
return|;
block|}
comment|/**    * Reads an existing key from the bucket.    * @param key Name of the key to be read.    * @return OzoneInputStream the stream using which the data can be read.    * @throws IOException    */
DECL|method|readKey (String key)
specifier|public
name|OzoneInputStream
name|readKey
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|proxy
operator|.
name|getKey
argument_list|(
name|volumeName
argument_list|,
name|name
argument_list|,
name|key
argument_list|)
return|;
block|}
comment|/**    * Returns information about the key.    * @param key Name of the key.    * @return OzoneKeyDetails Information about the key.    * @throws IOException    */
DECL|method|getKey (String key)
specifier|public
name|OzoneKeyDetails
name|getKey
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|proxy
operator|.
name|getKeyDetails
argument_list|(
name|volumeName
argument_list|,
name|name
argument_list|,
name|key
argument_list|)
return|;
block|}
comment|/**    * Returns Iterator to iterate over all keys in the bucket.    * The result can be restricted using key prefix, will return all    * keys if key prefix is null.    *    * @param keyPrefix Bucket prefix to match    * @return {@code Iterator<OzoneKey>}    */
DECL|method|listKeys (String keyPrefix)
specifier|public
name|Iterator
argument_list|<
name|?
extends|extends
name|OzoneKey
argument_list|>
name|listKeys
parameter_list|(
name|String
name|keyPrefix
parameter_list|)
block|{
return|return
name|listKeys
argument_list|(
name|keyPrefix
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Returns Iterator to iterate over all keys after prevKey in the bucket.    * If prevKey is null it iterates from the first key in the bucket.    * The result can be restricted using key prefix, will return all    * keys if key prefix is null.    *    * @param keyPrefix Bucket prefix to match    * @param prevKey Keys will be listed after this key name    * @return {@code Iterator<OzoneKey>}    */
DECL|method|listKeys (String keyPrefix, String prevKey)
specifier|public
name|Iterator
argument_list|<
name|?
extends|extends
name|OzoneKey
argument_list|>
name|listKeys
parameter_list|(
name|String
name|keyPrefix
parameter_list|,
name|String
name|prevKey
parameter_list|)
block|{
return|return
operator|new
name|KeyIterator
argument_list|(
name|keyPrefix
argument_list|,
name|prevKey
argument_list|)
return|;
block|}
comment|/**    * Deletes key from the bucket.    * @param key Name of the key to be deleted.    * @throws IOException    */
DECL|method|deleteKey (String key)
specifier|public
name|void
name|deleteKey
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|proxy
operator|.
name|deleteKey
argument_list|(
name|volumeName
argument_list|,
name|name
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
DECL|method|renameKey (String fromKeyName, String toKeyName)
specifier|public
name|void
name|renameKey
parameter_list|(
name|String
name|fromKeyName
parameter_list|,
name|String
name|toKeyName
parameter_list|)
throws|throws
name|IOException
block|{
name|proxy
operator|.
name|renameKey
argument_list|(
name|volumeName
argument_list|,
name|name
argument_list|,
name|fromKeyName
argument_list|,
name|toKeyName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initiate multipart upload for a specified key.    * @param keyName    * @param type    * @param factor    * @return OmMultipartInfo    * @throws IOException    */
DECL|method|initiateMultipartUpload (String keyName, ReplicationType type, ReplicationFactor factor)
specifier|public
name|OmMultipartInfo
name|initiateMultipartUpload
parameter_list|(
name|String
name|keyName
parameter_list|,
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|proxy
operator|.
name|initiateMultipartUpload
argument_list|(
name|volumeName
argument_list|,
name|name
argument_list|,
name|keyName
argument_list|,
name|type
argument_list|,
name|factor
argument_list|)
return|;
block|}
comment|/**    * Initiate multipart upload for a specified key, with default replication    * type RATIS and with replication factor THREE.    * @param key Name of the key to be created.    * @return OmMultipartInfo.    * @throws IOException    */
DECL|method|initiateMultipartUpload (String key)
specifier|public
name|OmMultipartInfo
name|initiateMultipartUpload
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|initiateMultipartUpload
argument_list|(
name|key
argument_list|,
name|defaultReplicationType
argument_list|,
name|defaultReplication
argument_list|)
return|;
block|}
comment|/**    * Create a part key for a multipart upload key.    * @param key    * @param size    * @param partNumber    * @param uploadID    * @return OzoneOutputStream    * @throws IOException    */
DECL|method|createMultipartKey (String key, long size, int partNumber, String uploadID)
specifier|public
name|OzoneOutputStream
name|createMultipartKey
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|size
parameter_list|,
name|int
name|partNumber
parameter_list|,
name|String
name|uploadID
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|proxy
operator|.
name|createMultipartKey
argument_list|(
name|volumeName
argument_list|,
name|name
argument_list|,
name|key
argument_list|,
name|size
argument_list|,
name|partNumber
argument_list|,
name|uploadID
argument_list|)
return|;
block|}
comment|/**    * Complete Multipart upload. This will combine all the parts and make the    * key visible in ozone.    * @param key    * @param uploadID    * @param partsMap    * @return OmMultipartUploadCompleteInfo    * @throws IOException    */
DECL|method|completeMultipartUpload (String key, String uploadID, Map<Integer, String> partsMap)
specifier|public
name|OmMultipartUploadCompleteInfo
name|completeMultipartUpload
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|uploadID
parameter_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|partsMap
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|proxy
operator|.
name|completeMultipartUpload
argument_list|(
name|volumeName
argument_list|,
name|name
argument_list|,
name|key
argument_list|,
name|uploadID
argument_list|,
name|partsMap
argument_list|)
return|;
block|}
comment|/**    * Abort multipart upload request.    * @param keyName    * @param uploadID    * @throws IOException    */
DECL|method|abortMultipartUpload (String keyName, String uploadID)
specifier|public
name|void
name|abortMultipartUpload
parameter_list|(
name|String
name|keyName
parameter_list|,
name|String
name|uploadID
parameter_list|)
throws|throws
name|IOException
block|{
name|proxy
operator|.
name|abortMultipartUpload
argument_list|(
name|volumeName
argument_list|,
name|name
argument_list|,
name|keyName
argument_list|,
name|uploadID
argument_list|)
expr_stmt|;
block|}
comment|/**    * An Iterator to iterate over {@link OzoneKey} list.    */
DECL|class|KeyIterator
specifier|private
class|class
name|KeyIterator
implements|implements
name|Iterator
argument_list|<
name|OzoneKey
argument_list|>
block|{
DECL|field|keyPrefix
specifier|private
name|String
name|keyPrefix
init|=
literal|null
decl_stmt|;
DECL|field|currentIterator
specifier|private
name|Iterator
argument_list|<
name|OzoneKey
argument_list|>
name|currentIterator
decl_stmt|;
DECL|field|currentValue
specifier|private
name|OzoneKey
name|currentValue
decl_stmt|;
comment|/**      * Creates an Iterator to iterate over all keys after prevKey in the bucket.      * If prevKey is null it iterates from the first key in the bucket.      * The returned keys match key prefix.      * @param keyPrefix      */
DECL|method|KeyIterator (String keyPrefix, String prevKey)
name|KeyIterator
parameter_list|(
name|String
name|keyPrefix
parameter_list|,
name|String
name|prevKey
parameter_list|)
block|{
name|this
operator|.
name|keyPrefix
operator|=
name|keyPrefix
expr_stmt|;
name|this
operator|.
name|currentValue
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|currentIterator
operator|=
name|getNextListOfKeys
argument_list|(
name|prevKey
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
operator|!
name|currentIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|currentIterator
operator|=
name|getNextListOfKeys
argument_list|(
name|currentValue
operator|!=
literal|null
condition|?
name|currentValue
operator|.
name|getName
argument_list|()
else|:
literal|null
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
return|return
name|currentIterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|OzoneKey
name|next
parameter_list|()
block|{
if|if
condition|(
name|hasNext
argument_list|()
condition|)
block|{
name|currentValue
operator|=
name|currentIterator
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
name|currentValue
return|;
block|}
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
comment|/**      * Gets the next set of key list using proxy.      * @param prevKey      * @return {@code List<OzoneVolume>}      */
DECL|method|getNextListOfKeys (String prevKey)
specifier|private
name|List
argument_list|<
name|OzoneKey
argument_list|>
name|getNextListOfKeys
parameter_list|(
name|String
name|prevKey
parameter_list|)
block|{
try|try
block|{
return|return
name|proxy
operator|.
name|listKeys
argument_list|(
name|volumeName
argument_list|,
name|name
argument_list|,
name|keyPrefix
argument_list|,
name|prevKey
argument_list|,
name|listCacheSize
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

