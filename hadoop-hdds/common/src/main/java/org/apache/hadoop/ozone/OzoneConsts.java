begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_comment
comment|/**  * Set of constants used in Ozone implementation.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|OzoneConsts
specifier|public
specifier|final
class|class
name|OzoneConsts
block|{
DECL|field|STORAGE_DIR
specifier|public
specifier|static
specifier|final
name|String
name|STORAGE_DIR
init|=
literal|"scm"
decl_stmt|;
DECL|field|SCM_ID
specifier|public
specifier|static
specifier|final
name|String
name|SCM_ID
init|=
literal|"scmUuid"
decl_stmt|;
DECL|field|OZONE_SIMPLE_ROOT_USER
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SIMPLE_ROOT_USER
init|=
literal|"root"
decl_stmt|;
DECL|field|OZONE_SIMPLE_HDFS_USER
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SIMPLE_HDFS_USER
init|=
literal|"hdfs"
decl_stmt|;
DECL|field|STORAGE_ID
specifier|public
specifier|static
specifier|final
name|String
name|STORAGE_ID
init|=
literal|"storageID"
decl_stmt|;
DECL|field|DATANODE_UUID
specifier|public
specifier|static
specifier|final
name|String
name|DATANODE_UUID
init|=
literal|"datanodeUuid"
decl_stmt|;
DECL|field|CLUSTER_ID
specifier|public
specifier|static
specifier|final
name|String
name|CLUSTER_ID
init|=
literal|"clusterID"
decl_stmt|;
DECL|field|LAYOUTVERSION
specifier|public
specifier|static
specifier|final
name|String
name|LAYOUTVERSION
init|=
literal|"layOutVersion"
decl_stmt|;
DECL|field|CTIME
specifier|public
specifier|static
specifier|final
name|String
name|CTIME
init|=
literal|"ctime"
decl_stmt|;
comment|/*    * BucketName length is used for both buckets and volume lengths    */
DECL|field|OZONE_MIN_BUCKET_NAME_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_MIN_BUCKET_NAME_LENGTH
init|=
literal|3
decl_stmt|;
DECL|field|OZONE_MAX_BUCKET_NAME_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_MAX_BUCKET_NAME_LENGTH
init|=
literal|63
decl_stmt|;
DECL|field|OZONE_ACL_USER_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ACL_USER_TYPE
init|=
literal|"user"
decl_stmt|;
DECL|field|OZONE_ACL_GROUP_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ACL_GROUP_TYPE
init|=
literal|"group"
decl_stmt|;
DECL|field|OZONE_ACL_WORLD_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ACL_WORLD_TYPE
init|=
literal|"world"
decl_stmt|;
DECL|field|OZONE_ACL_READ
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ACL_READ
init|=
literal|"r"
decl_stmt|;
DECL|field|OZONE_ACL_WRITE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ACL_WRITE
init|=
literal|"w"
decl_stmt|;
DECL|field|OZONE_ACL_READ_WRITE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ACL_READ_WRITE
init|=
literal|"rw"
decl_stmt|;
DECL|field|OZONE_ACL_WRITE_READ
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ACL_WRITE_READ
init|=
literal|"wr"
decl_stmt|;
DECL|field|OZONE_DATE_FORMAT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_DATE_FORMAT
init|=
literal|"EEE, dd MMM yyyy HH:mm:ss zzz"
decl_stmt|;
DECL|field|OZONE_TIME_ZONE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_TIME_ZONE
init|=
literal|"GMT"
decl_stmt|;
DECL|field|OZONE_COMPONENT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_COMPONENT
init|=
literal|"component"
decl_stmt|;
DECL|field|OZONE_FUNCTION
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_FUNCTION
init|=
literal|"function"
decl_stmt|;
DECL|field|OZONE_RESOURCE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RESOURCE
init|=
literal|"resource"
decl_stmt|;
DECL|field|OZONE_USER
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_USER
init|=
literal|"user"
decl_stmt|;
DECL|field|OZONE_REQUEST
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_REQUEST
init|=
literal|"request"
decl_stmt|;
DECL|field|OZONE_URI_SCHEME
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_URI_SCHEME
init|=
literal|"o3"
decl_stmt|;
DECL|field|OZONE_HTTP_SCHEME
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_HTTP_SCHEME
init|=
literal|"http"
decl_stmt|;
DECL|field|OZONE_URI_DELIMITER
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_URI_DELIMITER
init|=
literal|"/"
decl_stmt|;
DECL|field|CONTAINER_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_EXTENSION
init|=
literal|".container"
decl_stmt|;
DECL|field|CONTAINER_META
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_META
init|=
literal|".meta"
decl_stmt|;
comment|//  container storage is in the following format.
comment|//  Data Volume basePath/containers/<containerName>/metadata and
comment|//  Data Volume basePath/containers/<containerName>/data/...
DECL|field|CONTAINER_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_PREFIX
init|=
literal|"containers"
decl_stmt|;
DECL|field|CONTAINER_META_PATH
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_META_PATH
init|=
literal|"metadata"
decl_stmt|;
DECL|field|CONTAINER_DATA_PATH
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_DATA_PATH
init|=
literal|"data"
decl_stmt|;
DECL|field|CONTAINER_TEMPORARY_CHUNK_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_TEMPORARY_CHUNK_PREFIX
init|=
literal|"tmp"
decl_stmt|;
DECL|field|CONTAINER_CHUNK_NAME_DELIMITER
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_CHUNK_NAME_DELIMITER
init|=
literal|"."
decl_stmt|;
DECL|field|CONTAINER_ROOT_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_ROOT_PREFIX
init|=
literal|"repository"
decl_stmt|;
DECL|field|FILE_HASH
specifier|public
specifier|static
specifier|final
name|String
name|FILE_HASH
init|=
literal|"SHA-256"
decl_stmt|;
DECL|field|CHUNK_OVERWRITE
specifier|public
specifier|final
specifier|static
name|String
name|CHUNK_OVERWRITE
init|=
literal|"OverWriteRequested"
decl_stmt|;
DECL|field|CHUNK_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|CHUNK_SIZE
init|=
literal|1
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|// 1 MB
DECL|field|KB
specifier|public
specifier|static
specifier|final
name|long
name|KB
init|=
literal|1024L
decl_stmt|;
DECL|field|MB
specifier|public
specifier|static
specifier|final
name|long
name|MB
init|=
name|KB
operator|*
literal|1024L
decl_stmt|;
DECL|field|GB
specifier|public
specifier|static
specifier|final
name|long
name|GB
init|=
name|MB
operator|*
literal|1024L
decl_stmt|;
DECL|field|TB
specifier|public
specifier|static
specifier|final
name|long
name|TB
init|=
name|GB
operator|*
literal|1024L
decl_stmt|;
comment|/**    * level DB names used by SCM and data nodes.    */
DECL|field|CONTAINER_DB_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_DB_SUFFIX
init|=
literal|"container.db"
decl_stmt|;
DECL|field|SCM_CONTAINER_DB
specifier|public
specifier|static
specifier|final
name|String
name|SCM_CONTAINER_DB
init|=
literal|"scm-"
operator|+
name|CONTAINER_DB_SUFFIX
decl_stmt|;
DECL|field|DN_CONTAINER_DB
specifier|public
specifier|static
specifier|final
name|String
name|DN_CONTAINER_DB
init|=
literal|"-dn-"
operator|+
name|CONTAINER_DB_SUFFIX
decl_stmt|;
DECL|field|BLOCK_DB
specifier|public
specifier|static
specifier|final
name|String
name|BLOCK_DB
init|=
literal|"block.db"
decl_stmt|;
DECL|field|OPEN_CONTAINERS_DB
specifier|public
specifier|static
specifier|final
name|String
name|OPEN_CONTAINERS_DB
init|=
literal|"openContainers.db"
decl_stmt|;
DECL|field|DELETED_BLOCK_DB
specifier|public
specifier|static
specifier|final
name|String
name|DELETED_BLOCK_DB
init|=
literal|"deletedBlock.db"
decl_stmt|;
DECL|field|KSM_DB_NAME
specifier|public
specifier|static
specifier|final
name|String
name|KSM_DB_NAME
init|=
literal|"ksm.db"
decl_stmt|;
DECL|field|STORAGE_DIR_CHUNKS
specifier|public
specifier|static
specifier|final
name|String
name|STORAGE_DIR_CHUNKS
init|=
literal|"chunks"
decl_stmt|;
DECL|field|CONTAINER_FILE_CHECKSUM_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_FILE_CHECKSUM_EXTENSION
init|=
literal|".chksm"
decl_stmt|;
comment|/**    * Supports Bucket Versioning.    */
DECL|enum|Versioning
specifier|public
enum|enum
name|Versioning
block|{
DECL|enumConstant|NOT_DEFINED
DECL|enumConstant|ENABLED
DECL|enumConstant|DISABLED
name|NOT_DEFINED
block|,
name|ENABLED
block|,
name|DISABLED
block|;
DECL|method|getVersioning (boolean versioning)
specifier|public
specifier|static
name|Versioning
name|getVersioning
parameter_list|(
name|boolean
name|versioning
parameter_list|)
block|{
return|return
name|versioning
condition|?
name|ENABLED
else|:
name|DISABLED
return|;
block|}
block|}
comment|/**    * Ozone handler types.    */
DECL|field|OZONE_HANDLER_DISTRIBUTED
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_HANDLER_DISTRIBUTED
init|=
literal|"distributed"
decl_stmt|;
DECL|field|OZONE_HANDLER_LOCAL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_HANDLER_LOCAL
init|=
literal|"local"
decl_stmt|;
DECL|field|DELETING_KEY_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|DELETING_KEY_PREFIX
init|=
literal|"#deleting#"
decl_stmt|;
DECL|field|DELETED_KEY_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|DELETED_KEY_PREFIX
init|=
literal|"#deleted#"
decl_stmt|;
DECL|field|DELETE_TRANSACTION_KEY_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|DELETE_TRANSACTION_KEY_PREFIX
init|=
literal|"#delTX#"
decl_stmt|;
DECL|field|OPEN_KEY_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|OPEN_KEY_PREFIX
init|=
literal|"#open#"
decl_stmt|;
DECL|field|OPEN_KEY_ID_DELIMINATOR
specifier|public
specifier|static
specifier|final
name|String
name|OPEN_KEY_ID_DELIMINATOR
init|=
literal|"#"
decl_stmt|;
comment|/**    * KSM LevelDB prefixes.    *    * KSM DB stores metadata as KV pairs with certain prefixes,    * prefix is used to improve the performance to get related    * metadata.    *    * KSM DB Schema:    *  ----------------------------------------------------------    *  |  KEY                                     |     VALUE   |    *  ----------------------------------------------------------    *  | $userName                                |  VolumeList |    *  ----------------------------------------------------------    *  | /#volumeName                             |  VolumeInfo |    *  ----------------------------------------------------------    *  | /#volumeName/#bucketName                 |  BucketInfo |    *  ----------------------------------------------------------    *  | /volumeName/bucketName/keyName           |  KeyInfo    |    *  ----------------------------------------------------------    *  | #deleting#/volumeName/bucketName/keyName |  KeyInfo    |    *  ----------------------------------------------------------    */
DECL|field|KSM_VOLUME_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|KSM_VOLUME_PREFIX
init|=
literal|"/#"
decl_stmt|;
DECL|field|KSM_BUCKET_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|KSM_BUCKET_PREFIX
init|=
literal|"/#"
decl_stmt|;
DECL|field|KSM_KEY_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|KSM_KEY_PREFIX
init|=
literal|"/"
decl_stmt|;
DECL|field|KSM_USER_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|KSM_USER_PREFIX
init|=
literal|"$"
decl_stmt|;
comment|/**    * Max KSM Quota size of 1024 PB.    */
DECL|field|MAX_QUOTA_IN_BYTES
specifier|public
specifier|static
specifier|final
name|long
name|MAX_QUOTA_IN_BYTES
init|=
literal|1024L
operator|*
literal|1024
operator|*
name|TB
decl_stmt|;
comment|/**    * Max number of keys returned per list buckets operation.    */
DECL|field|MAX_LISTBUCKETS_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|MAX_LISTBUCKETS_SIZE
init|=
literal|1024
decl_stmt|;
comment|/**    * Max number of keys returned per list keys operation.    */
DECL|field|MAX_LISTKEYS_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|MAX_LISTKEYS_SIZE
init|=
literal|1024
decl_stmt|;
comment|/**    * Max number of volumes returned per list volumes operation.    */
DECL|field|MAX_LISTVOLUMES_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|MAX_LISTVOLUMES_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|INVALID_PORT
specifier|public
specifier|static
specifier|final
name|int
name|INVALID_PORT
init|=
operator|-
literal|1
decl_stmt|;
comment|// The ServiceListJSONServlet context attribute where KeySpaceManager
comment|// instance gets stored.
DECL|field|KSM_CONTEXT_ATTRIBUTE
specifier|public
specifier|static
specifier|final
name|String
name|KSM_CONTEXT_ATTRIBUTE
init|=
literal|"ozone.ksm"
decl_stmt|;
DECL|method|OzoneConsts ()
specifier|private
name|OzoneConsts
parameter_list|()
block|{
comment|// Never Constructed
block|}
comment|// YAML fields for .container files
DECL|field|CONTAINER_ID
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_ID
init|=
literal|"containerID"
decl_stmt|;
DECL|field|CONTAINER_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_TYPE
init|=
literal|"containerType"
decl_stmt|;
DECL|field|STATE
specifier|public
specifier|static
specifier|final
name|String
name|STATE
init|=
literal|"state"
decl_stmt|;
DECL|field|METADATA
specifier|public
specifier|static
specifier|final
name|String
name|METADATA
init|=
literal|"metadata"
decl_stmt|;
DECL|field|MAX_SIZE_GB
specifier|public
specifier|static
specifier|final
name|String
name|MAX_SIZE_GB
init|=
literal|"maxSizeGB"
decl_stmt|;
DECL|field|METADATA_PATH
specifier|public
specifier|static
specifier|final
name|String
name|METADATA_PATH
init|=
literal|"metadataPath"
decl_stmt|;
DECL|field|CHUNKS_PATH
specifier|public
specifier|static
specifier|final
name|String
name|CHUNKS_PATH
init|=
literal|"chunksPath"
decl_stmt|;
DECL|field|CONTAINER_DB_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_DB_TYPE
init|=
literal|"containerDBType"
decl_stmt|;
block|}
end_class

end_unit

