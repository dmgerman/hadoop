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
DECL|field|OZONE_ACL_IP_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ACL_IP_TYPE
init|=
literal|"ip"
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
DECL|field|OZONE_ACL_DELETE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ACL_DELETE
init|=
literal|"d"
decl_stmt|;
DECL|field|OZONE_ACL_LIST
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ACL_LIST
init|=
literal|"l"
decl_stmt|;
DECL|field|OZONE_ACL_ALL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ACL_ALL
init|=
literal|"a"
decl_stmt|;
DECL|field|OZONE_ACL_NONE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ACL_NONE
init|=
literal|"n"
decl_stmt|;
DECL|field|OZONE_ACL_CREATE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ACL_CREATE
init|=
literal|"c"
decl_stmt|;
DECL|field|OZONE_ACL_READ_ACL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ACL_READ_ACL
init|=
literal|"x"
decl_stmt|;
DECL|field|OZONE_ACL_WRITE_ACL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ACL_WRITE_ACL
init|=
literal|"y"
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
comment|// Ozone File System scheme
DECL|field|OZONE_URI_SCHEME
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_URI_SCHEME
init|=
literal|"o3fs"
decl_stmt|;
DECL|field|OZONE_RPC_SCHEME
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_RPC_SCHEME
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
comment|// Refer to {@link ContainerReader} for container storage layout on disk.
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
DECL|field|PIPELINE_DB_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|PIPELINE_DB_SUFFIX
init|=
literal|"pipeline.db"
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
DECL|field|SCM_PIPELINE_DB
specifier|public
specifier|static
specifier|final
name|String
name|SCM_PIPELINE_DB
init|=
literal|"scm-"
operator|+
name|PIPELINE_DB_SUFFIX
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
DECL|field|DELETED_BLOCK_DB
specifier|public
specifier|static
specifier|final
name|String
name|DELETED_BLOCK_DB
init|=
literal|"deletedBlock.db"
decl_stmt|;
DECL|field|OM_DB_NAME
specifier|public
specifier|static
specifier|final
name|String
name|OM_DB_NAME
init|=
literal|"om.db"
decl_stmt|;
DECL|field|OZONE_MANAGER_TOKEN_DB_NAME
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_MANAGER_TOKEN_DB_NAME
init|=
literal|"om-token.db"
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
DECL|field|BLOCK_COMMIT_SEQUENCE_ID_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|BLOCK_COMMIT_SEQUENCE_ID_PREFIX
init|=
literal|"#BCSID"
decl_stmt|;
comment|/**    * OM LevelDB prefixes.    *    * OM DB stores metadata as KV pairs with certain prefixes,    * prefix is used to improve the performance to get related    * metadata.    *    * OM DB Schema:    *  ----------------------------------------------------------    *  |  KEY                                     |     VALUE   |    *  ----------------------------------------------------------    *  | $userName                                |  VolumeList |    *  ----------------------------------------------------------    *  | /#volumeName                             |  VolumeInfo |    *  ----------------------------------------------------------    *  | /#volumeName/#bucketName                 |  BucketInfo |    *  ----------------------------------------------------------    *  | /volumeName/bucketName/keyName           |  KeyInfo    |    *  ----------------------------------------------------------    *  | #deleting#/volumeName/bucketName/keyName |  KeyInfo    |    *  ----------------------------------------------------------    */
DECL|field|OM_KEY_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|OM_KEY_PREFIX
init|=
literal|"/"
decl_stmt|;
DECL|field|OM_USER_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|OM_USER_PREFIX
init|=
literal|"$"
decl_stmt|;
DECL|field|OM_S3_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|OM_S3_PREFIX
init|=
literal|"S3:"
decl_stmt|;
DECL|field|OM_S3_VOLUME_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|OM_S3_VOLUME_PREFIX
init|=
literal|"s3"
decl_stmt|;
comment|/**    *   Max chunk size limit.    */
DECL|field|OZONE_SCM_CHUNK_MAX_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_SCM_CHUNK_MAX_SIZE
init|=
literal|32
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|/**    * Max OM Quota size of 1024 PB.    */
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
comment|// The ServiceListJSONServlet context attribute where OzoneManager
comment|// instance gets stored.
DECL|field|OM_CONTEXT_ATTRIBUTE
specifier|public
specifier|static
specifier|final
name|String
name|OM_CONTEXT_ATTRIBUTE
init|=
literal|"ozone.om"
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
DECL|field|MAX_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|MAX_SIZE
init|=
literal|"maxSize"
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
DECL|field|CHECKSUM
specifier|public
specifier|static
specifier|final
name|String
name|CHECKSUM
init|=
literal|"checksum"
decl_stmt|;
DECL|field|ORIGIN_PIPELINE_ID
specifier|public
specifier|static
specifier|final
name|String
name|ORIGIN_PIPELINE_ID
init|=
literal|"originPipelineId"
decl_stmt|;
DECL|field|ORIGIN_NODE_ID
specifier|public
specifier|static
specifier|final
name|String
name|ORIGIN_NODE_ID
init|=
literal|"originNodeId"
decl_stmt|;
comment|// Supported store types.
DECL|field|OZONE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE
init|=
literal|"ozone"
decl_stmt|;
DECL|field|S3
specifier|public
specifier|static
specifier|final
name|String
name|S3
init|=
literal|"s3"
decl_stmt|;
comment|// For OM Audit usage
DECL|field|VOLUME
specifier|public
specifier|static
specifier|final
name|String
name|VOLUME
init|=
literal|"volume"
decl_stmt|;
DECL|field|BUCKET
specifier|public
specifier|static
specifier|final
name|String
name|BUCKET
init|=
literal|"bucket"
decl_stmt|;
DECL|field|KEY
specifier|public
specifier|static
specifier|final
name|String
name|KEY
init|=
literal|"key"
decl_stmt|;
DECL|field|QUOTA
specifier|public
specifier|static
specifier|final
name|String
name|QUOTA
init|=
literal|"quota"
decl_stmt|;
DECL|field|QUOTA_IN_BYTES
specifier|public
specifier|static
specifier|final
name|String
name|QUOTA_IN_BYTES
init|=
literal|"quotaInBytes"
decl_stmt|;
DECL|field|CLIENT_ID
specifier|public
specifier|static
specifier|final
name|String
name|CLIENT_ID
init|=
literal|"clientID"
decl_stmt|;
DECL|field|OWNER
specifier|public
specifier|static
specifier|final
name|String
name|OWNER
init|=
literal|"owner"
decl_stmt|;
DECL|field|ADMIN
specifier|public
specifier|static
specifier|final
name|String
name|ADMIN
init|=
literal|"admin"
decl_stmt|;
DECL|field|USERNAME
specifier|public
specifier|static
specifier|final
name|String
name|USERNAME
init|=
literal|"username"
decl_stmt|;
DECL|field|PREV_KEY
specifier|public
specifier|static
specifier|final
name|String
name|PREV_KEY
init|=
literal|"prevKey"
decl_stmt|;
DECL|field|START_KEY
specifier|public
specifier|static
specifier|final
name|String
name|START_KEY
init|=
literal|"startKey"
decl_stmt|;
DECL|field|MAX_KEYS
specifier|public
specifier|static
specifier|final
name|String
name|MAX_KEYS
init|=
literal|"maxKeys"
decl_stmt|;
DECL|field|PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"prefix"
decl_stmt|;
DECL|field|KEY_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|KEY_PREFIX
init|=
literal|"keyPrefix"
decl_stmt|;
DECL|field|ACLS
specifier|public
specifier|static
specifier|final
name|String
name|ACLS
init|=
literal|"acls"
decl_stmt|;
DECL|field|USER_ACL
specifier|public
specifier|static
specifier|final
name|String
name|USER_ACL
init|=
literal|"userAcl"
decl_stmt|;
DECL|field|ADD_ACLS
specifier|public
specifier|static
specifier|final
name|String
name|ADD_ACLS
init|=
literal|"addAcls"
decl_stmt|;
DECL|field|REMOVE_ACLS
specifier|public
specifier|static
specifier|final
name|String
name|REMOVE_ACLS
init|=
literal|"removeAcls"
decl_stmt|;
DECL|field|MAX_NUM_OF_BUCKETS
specifier|public
specifier|static
specifier|final
name|String
name|MAX_NUM_OF_BUCKETS
init|=
literal|"maxNumOfBuckets"
decl_stmt|;
DECL|field|TO_KEY_NAME
specifier|public
specifier|static
specifier|final
name|String
name|TO_KEY_NAME
init|=
literal|"toKeyName"
decl_stmt|;
DECL|field|STORAGE_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|STORAGE_TYPE
init|=
literal|"storageType"
decl_stmt|;
DECL|field|IS_VERSION_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|IS_VERSION_ENABLED
init|=
literal|"isVersionEnabled"
decl_stmt|;
DECL|field|CREATION_TIME
specifier|public
specifier|static
specifier|final
name|String
name|CREATION_TIME
init|=
literal|"creationTime"
decl_stmt|;
DECL|field|DATA_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|DATA_SIZE
init|=
literal|"dataSize"
decl_stmt|;
DECL|field|REPLICATION_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|REPLICATION_TYPE
init|=
literal|"replicationType"
decl_stmt|;
DECL|field|REPLICATION_FACTOR
specifier|public
specifier|static
specifier|final
name|String
name|REPLICATION_FACTOR
init|=
literal|"replicationFactor"
decl_stmt|;
DECL|field|KEY_LOCATION_INFO
specifier|public
specifier|static
specifier|final
name|String
name|KEY_LOCATION_INFO
init|=
literal|"keyLocationInfo"
decl_stmt|;
DECL|field|MULTIPART_LIST
specifier|public
specifier|static
specifier|final
name|String
name|MULTIPART_LIST
init|=
literal|"multipartList"
decl_stmt|;
comment|// For OM metrics saving to a file
DECL|field|OM_METRICS_FILE
specifier|public
specifier|static
specifier|final
name|String
name|OM_METRICS_FILE
init|=
literal|"omMetrics"
decl_stmt|;
DECL|field|OM_METRICS_TEMP_FILE
specifier|public
specifier|static
specifier|final
name|String
name|OM_METRICS_TEMP_FILE
init|=
name|OM_METRICS_FILE
operator|+
literal|".tmp"
decl_stmt|;
comment|// For Multipart upload
DECL|field|OM_MULTIPART_MIN_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|OM_MULTIPART_MIN_SIZE
init|=
literal|5
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
block|}
end_class

end_unit

