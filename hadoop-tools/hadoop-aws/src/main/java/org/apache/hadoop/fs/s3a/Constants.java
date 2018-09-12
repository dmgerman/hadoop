begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
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
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * All the constants used with the {@link S3AFileSystem}.  *  * Some of the strings are marked as {@code Unstable}. This means  * that they may be unsupported in future; at which point they will be marked  * as deprecated and simply ignored.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|Constants
specifier|public
specifier|final
class|class
name|Constants
block|{
DECL|method|Constants ()
specifier|private
name|Constants
parameter_list|()
block|{   }
comment|/**    * default hadoop temp dir on local system: {@value}.    */
DECL|field|HADOOP_TMP_DIR
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_TMP_DIR
init|=
literal|"hadoop.tmp.dir"
decl_stmt|;
comment|/** The minimum multipart size which S3 supports. */
DECL|field|MULTIPART_MIN_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|MULTIPART_MIN_SIZE
init|=
literal|5
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|// s3 access key
DECL|field|ACCESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|ACCESS_KEY
init|=
literal|"fs.s3a.access.key"
decl_stmt|;
comment|// s3 secret key
DECL|field|SECRET_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SECRET_KEY
init|=
literal|"fs.s3a.secret.key"
decl_stmt|;
comment|// aws credentials provider
DECL|field|AWS_CREDENTIALS_PROVIDER
specifier|public
specifier|static
specifier|final
name|String
name|AWS_CREDENTIALS_PROVIDER
init|=
literal|"fs.s3a.aws.credentials.provider"
decl_stmt|;
comment|/**    * Extra set of security credentials which will be prepended to that    * set in {@code "hadoop.security.credential.provider.path"}.    * This extra option allows for per-bucket overrides.    */
DECL|field|S3A_SECURITY_CREDENTIAL_PROVIDER_PATH
specifier|public
specifier|static
specifier|final
name|String
name|S3A_SECURITY_CREDENTIAL_PROVIDER_PATH
init|=
literal|"fs.s3a.security.credential.provider.path"
decl_stmt|;
comment|// session token for when using TemporaryAWSCredentialsProvider
DECL|field|SESSION_TOKEN
specifier|public
specifier|static
specifier|final
name|String
name|SESSION_TOKEN
init|=
literal|"fs.s3a.session.token"
decl_stmt|;
comment|/**    * AWS Role to request.    */
DECL|field|ASSUMED_ROLE_ARN
specifier|public
specifier|static
specifier|final
name|String
name|ASSUMED_ROLE_ARN
init|=
literal|"fs.s3a.assumed.role.arn"
decl_stmt|;
comment|/**    * Session name for the assumed role, must be valid characters according    * to the AWS APIs.    * If not set, one is generated from the current Hadoop/Kerberos username.    */
DECL|field|ASSUMED_ROLE_SESSION_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ASSUMED_ROLE_SESSION_NAME
init|=
literal|"fs.s3a.assumed.role.session.name"
decl_stmt|;
comment|/**    * Duration of assumed roles before a refresh is attempted.    */
DECL|field|ASSUMED_ROLE_SESSION_DURATION
specifier|public
specifier|static
specifier|final
name|String
name|ASSUMED_ROLE_SESSION_DURATION
init|=
literal|"fs.s3a.assumed.role.session.duration"
decl_stmt|;
comment|/** Security Token Service Endpoint. If unset, uses the default endpoint. */
DECL|field|ASSUMED_ROLE_STS_ENDPOINT
specifier|public
specifier|static
specifier|final
name|String
name|ASSUMED_ROLE_STS_ENDPOINT
init|=
literal|"fs.s3a.assumed.role.sts.endpoint"
decl_stmt|;
comment|/**    * Region for the STS endpoint; only relevant if the endpoint    * is set.    */
DECL|field|ASSUMED_ROLE_STS_ENDPOINT_REGION
specifier|public
specifier|static
specifier|final
name|String
name|ASSUMED_ROLE_STS_ENDPOINT_REGION
init|=
literal|"fs.s3a.assumed.role.sts.endpoint.region"
decl_stmt|;
comment|/**    * Default value for the STS endpoint region; needed for    * v4 signing.    */
DECL|field|ASSUMED_ROLE_STS_ENDPOINT_REGION_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|ASSUMED_ROLE_STS_ENDPOINT_REGION_DEFAULT
init|=
literal|"us-west-1"
decl_stmt|;
comment|/**    * Default duration of an assumed role.    */
DECL|field|ASSUMED_ROLE_SESSION_DURATION_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|ASSUMED_ROLE_SESSION_DURATION_DEFAULT
init|=
literal|"30m"
decl_stmt|;
comment|/** list of providers to authenticate for the assumed role. */
DECL|field|ASSUMED_ROLE_CREDENTIALS_PROVIDER
specifier|public
specifier|static
specifier|final
name|String
name|ASSUMED_ROLE_CREDENTIALS_PROVIDER
init|=
literal|"fs.s3a.assumed.role.credentials.provider"
decl_stmt|;
comment|/** JSON policy containing the policy to apply to the role. */
DECL|field|ASSUMED_ROLE_POLICY
specifier|public
specifier|static
specifier|final
name|String
name|ASSUMED_ROLE_POLICY
init|=
literal|"fs.s3a.assumed.role.policy"
decl_stmt|;
DECL|field|ASSUMED_ROLE_CREDENTIALS_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|ASSUMED_ROLE_CREDENTIALS_DEFAULT
init|=
name|SimpleAWSCredentialsProvider
operator|.
name|NAME
decl_stmt|;
comment|// number of simultaneous connections to s3
DECL|field|MAXIMUM_CONNECTIONS
specifier|public
specifier|static
specifier|final
name|String
name|MAXIMUM_CONNECTIONS
init|=
literal|"fs.s3a.connection.maximum"
decl_stmt|;
DECL|field|DEFAULT_MAXIMUM_CONNECTIONS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAXIMUM_CONNECTIONS
init|=
literal|15
decl_stmt|;
comment|// connect to s3 over ssl?
DECL|field|SECURE_CONNECTIONS
specifier|public
specifier|static
specifier|final
name|String
name|SECURE_CONNECTIONS
init|=
literal|"fs.s3a.connection.ssl.enabled"
decl_stmt|;
DECL|field|DEFAULT_SECURE_CONNECTIONS
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_SECURE_CONNECTIONS
init|=
literal|true
decl_stmt|;
comment|//use a custom endpoint?
DECL|field|ENDPOINT
specifier|public
specifier|static
specifier|final
name|String
name|ENDPOINT
init|=
literal|"fs.s3a.endpoint"
decl_stmt|;
comment|//Enable path style access? Overrides default virtual hosting
DECL|field|PATH_STYLE_ACCESS
specifier|public
specifier|static
specifier|final
name|String
name|PATH_STYLE_ACCESS
init|=
literal|"fs.s3a.path.style.access"
decl_stmt|;
comment|//connect to s3 through a proxy server?
DECL|field|PROXY_HOST
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_HOST
init|=
literal|"fs.s3a.proxy.host"
decl_stmt|;
DECL|field|PROXY_PORT
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_PORT
init|=
literal|"fs.s3a.proxy.port"
decl_stmt|;
DECL|field|PROXY_USERNAME
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_USERNAME
init|=
literal|"fs.s3a.proxy.username"
decl_stmt|;
DECL|field|PROXY_PASSWORD
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_PASSWORD
init|=
literal|"fs.s3a.proxy.password"
decl_stmt|;
DECL|field|PROXY_DOMAIN
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_DOMAIN
init|=
literal|"fs.s3a.proxy.domain"
decl_stmt|;
DECL|field|PROXY_WORKSTATION
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_WORKSTATION
init|=
literal|"fs.s3a.proxy.workstation"
decl_stmt|;
comment|// number of times we should retry errors
DECL|field|MAX_ERROR_RETRIES
specifier|public
specifier|static
specifier|final
name|String
name|MAX_ERROR_RETRIES
init|=
literal|"fs.s3a.attempts.maximum"
decl_stmt|;
DECL|field|DEFAULT_MAX_ERROR_RETRIES
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_ERROR_RETRIES
init|=
literal|20
decl_stmt|;
comment|// seconds until we give up trying to establish a connection to s3
DECL|field|ESTABLISH_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|ESTABLISH_TIMEOUT
init|=
literal|"fs.s3a.connection.establish.timeout"
decl_stmt|;
DECL|field|DEFAULT_ESTABLISH_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_ESTABLISH_TIMEOUT
init|=
literal|50000
decl_stmt|;
comment|// seconds until we give up on a connection to s3
DECL|field|SOCKET_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|SOCKET_TIMEOUT
init|=
literal|"fs.s3a.connection.timeout"
decl_stmt|;
DECL|field|DEFAULT_SOCKET_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_SOCKET_TIMEOUT
init|=
literal|200000
decl_stmt|;
comment|// socket send buffer to be used in Amazon client
DECL|field|SOCKET_SEND_BUFFER
specifier|public
specifier|static
specifier|final
name|String
name|SOCKET_SEND_BUFFER
init|=
literal|"fs.s3a.socket.send.buffer"
decl_stmt|;
DECL|field|DEFAULT_SOCKET_SEND_BUFFER
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_SOCKET_SEND_BUFFER
init|=
literal|8
operator|*
literal|1024
decl_stmt|;
comment|// socket send buffer to be used in Amazon client
DECL|field|SOCKET_RECV_BUFFER
specifier|public
specifier|static
specifier|final
name|String
name|SOCKET_RECV_BUFFER
init|=
literal|"fs.s3a.socket.recv.buffer"
decl_stmt|;
DECL|field|DEFAULT_SOCKET_RECV_BUFFER
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_SOCKET_RECV_BUFFER
init|=
literal|8
operator|*
literal|1024
decl_stmt|;
comment|// number of records to get while paging through a directory listing
DECL|field|MAX_PAGING_KEYS
specifier|public
specifier|static
specifier|final
name|String
name|MAX_PAGING_KEYS
init|=
literal|"fs.s3a.paging.maximum"
decl_stmt|;
DECL|field|DEFAULT_MAX_PAGING_KEYS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_PAGING_KEYS
init|=
literal|5000
decl_stmt|;
comment|// the maximum number of threads to allow in the pool used by TransferManager
DECL|field|MAX_THREADS
specifier|public
specifier|static
specifier|final
name|String
name|MAX_THREADS
init|=
literal|"fs.s3a.threads.max"
decl_stmt|;
DECL|field|DEFAULT_MAX_THREADS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_THREADS
init|=
literal|10
decl_stmt|;
comment|// the time an idle thread waits before terminating
DECL|field|KEEPALIVE_TIME
specifier|public
specifier|static
specifier|final
name|String
name|KEEPALIVE_TIME
init|=
literal|"fs.s3a.threads.keepalivetime"
decl_stmt|;
DECL|field|DEFAULT_KEEPALIVE_TIME
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_KEEPALIVE_TIME
init|=
literal|60
decl_stmt|;
comment|// the maximum number of tasks cached if all threads are already uploading
DECL|field|MAX_TOTAL_TASKS
specifier|public
specifier|static
specifier|final
name|String
name|MAX_TOTAL_TASKS
init|=
literal|"fs.s3a.max.total.tasks"
decl_stmt|;
DECL|field|DEFAULT_MAX_TOTAL_TASKS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_TOTAL_TASKS
init|=
literal|5
decl_stmt|;
comment|// size of each of or multipart pieces in bytes
DECL|field|MULTIPART_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|MULTIPART_SIZE
init|=
literal|"fs.s3a.multipart.size"
decl_stmt|;
DECL|field|DEFAULT_MULTIPART_SIZE
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_MULTIPART_SIZE
init|=
literal|104857600
decl_stmt|;
comment|// 100 MB
comment|// minimum size in bytes before we start a multipart uploads or copy
DECL|field|MIN_MULTIPART_THRESHOLD
specifier|public
specifier|static
specifier|final
name|String
name|MIN_MULTIPART_THRESHOLD
init|=
literal|"fs.s3a.multipart.threshold"
decl_stmt|;
DECL|field|DEFAULT_MIN_MULTIPART_THRESHOLD
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_MIN_MULTIPART_THRESHOLD
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
comment|//enable multiobject-delete calls?
DECL|field|ENABLE_MULTI_DELETE
specifier|public
specifier|static
specifier|final
name|String
name|ENABLE_MULTI_DELETE
init|=
literal|"fs.s3a.multiobjectdelete.enable"
decl_stmt|;
comment|// comma separated list of directories
DECL|field|BUFFER_DIR
specifier|public
specifier|static
specifier|final
name|String
name|BUFFER_DIR
init|=
literal|"fs.s3a.buffer.dir"
decl_stmt|;
comment|// switch to the fast block-by-block upload mechanism
comment|// this is the only supported upload mechanism
annotation|@
name|Deprecated
DECL|field|FAST_UPLOAD
specifier|public
specifier|static
specifier|final
name|String
name|FAST_UPLOAD
init|=
literal|"fs.s3a.fast.upload"
decl_stmt|;
annotation|@
name|Deprecated
DECL|field|DEFAULT_FAST_UPLOAD
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_FAST_UPLOAD
init|=
literal|false
decl_stmt|;
comment|//initial size of memory buffer for a fast upload
annotation|@
name|Deprecated
DECL|field|FAST_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|FAST_BUFFER_SIZE
init|=
literal|"fs.s3a.fast.buffer.size"
decl_stmt|;
DECL|field|DEFAULT_FAST_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_FAST_BUFFER_SIZE
init|=
literal|1048576
decl_stmt|;
comment|//1MB
comment|/**    * What buffer to use.    * Default is {@link #FAST_UPLOAD_BUFFER_DISK}    * Value: {@value}    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|FAST_UPLOAD_BUFFER
specifier|public
specifier|static
specifier|final
name|String
name|FAST_UPLOAD_BUFFER
init|=
literal|"fs.s3a.fast.upload.buffer"
decl_stmt|;
comment|/**    * Buffer blocks to disk: {@value}.    * Capacity is limited to available disk space.    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|FAST_UPLOAD_BUFFER_DISK
specifier|public
specifier|static
specifier|final
name|String
name|FAST_UPLOAD_BUFFER_DISK
init|=
literal|"disk"
decl_stmt|;
comment|/**    * Use an in-memory array. Fast but will run of heap rapidly: {@value}.    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|FAST_UPLOAD_BUFFER_ARRAY
specifier|public
specifier|static
specifier|final
name|String
name|FAST_UPLOAD_BUFFER_ARRAY
init|=
literal|"array"
decl_stmt|;
comment|/**    * Use a byte buffer. May be more memory efficient than the    * {@link #FAST_UPLOAD_BUFFER_ARRAY}: {@value}.    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|FAST_UPLOAD_BYTEBUFFER
specifier|public
specifier|static
specifier|final
name|String
name|FAST_UPLOAD_BYTEBUFFER
init|=
literal|"bytebuffer"
decl_stmt|;
comment|/**    * Default buffer option: {@value}.    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|DEFAULT_FAST_UPLOAD_BUFFER
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_FAST_UPLOAD_BUFFER
init|=
name|FAST_UPLOAD_BUFFER_DISK
decl_stmt|;
comment|/**    * Maximum Number of blocks a single output stream can have    * active (uploading, or queued to the central FileSystem    * instance's pool of queued operations.    * This stops a single stream overloading the shared thread pool.    * {@value}    *<p>    * Default is {@link #DEFAULT_FAST_UPLOAD_ACTIVE_BLOCKS}    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|FAST_UPLOAD_ACTIVE_BLOCKS
specifier|public
specifier|static
specifier|final
name|String
name|FAST_UPLOAD_ACTIVE_BLOCKS
init|=
literal|"fs.s3a.fast.upload.active.blocks"
decl_stmt|;
comment|/**    * Limit of queued block upload operations before writes    * block. Value: {@value}    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|DEFAULT_FAST_UPLOAD_ACTIVE_BLOCKS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_FAST_UPLOAD_ACTIVE_BLOCKS
init|=
literal|4
decl_stmt|;
comment|// Private | PublicRead | PublicReadWrite | AuthenticatedRead |
comment|// LogDeliveryWrite | BucketOwnerRead | BucketOwnerFullControl
DECL|field|CANNED_ACL
specifier|public
specifier|static
specifier|final
name|String
name|CANNED_ACL
init|=
literal|"fs.s3a.acl.default"
decl_stmt|;
DECL|field|DEFAULT_CANNED_ACL
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_CANNED_ACL
init|=
literal|""
decl_stmt|;
comment|// should we try to purge old multipart uploads when starting up
DECL|field|PURGE_EXISTING_MULTIPART
specifier|public
specifier|static
specifier|final
name|String
name|PURGE_EXISTING_MULTIPART
init|=
literal|"fs.s3a.multipart.purge"
decl_stmt|;
DECL|field|DEFAULT_PURGE_EXISTING_MULTIPART
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_PURGE_EXISTING_MULTIPART
init|=
literal|false
decl_stmt|;
comment|// purge any multipart uploads older than this number of seconds
DECL|field|PURGE_EXISTING_MULTIPART_AGE
specifier|public
specifier|static
specifier|final
name|String
name|PURGE_EXISTING_MULTIPART_AGE
init|=
literal|"fs.s3a.multipart.purge.age"
decl_stmt|;
DECL|field|DEFAULT_PURGE_EXISTING_MULTIPART_AGE
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_PURGE_EXISTING_MULTIPART_AGE
init|=
literal|86400
decl_stmt|;
comment|// s3 server-side encryption, see S3AEncryptionMethods for valid options
DECL|field|SERVER_SIDE_ENCRYPTION_ALGORITHM
specifier|public
specifier|static
specifier|final
name|String
name|SERVER_SIDE_ENCRYPTION_ALGORITHM
init|=
literal|"fs.s3a.server-side-encryption-algorithm"
decl_stmt|;
comment|/**    * The standard encryption algorithm AWS supports.    * Different implementations may support others (or none).    * Use the S3AEncryptionMethods instead when configuring    * which Server Side Encryption to use.    * Value: "{@value}".    */
annotation|@
name|Deprecated
DECL|field|SERVER_SIDE_ENCRYPTION_AES256
specifier|public
specifier|static
specifier|final
name|String
name|SERVER_SIDE_ENCRYPTION_AES256
init|=
literal|"AES256"
decl_stmt|;
comment|/**    * Used to specify which AWS KMS key to use if    * {@link #SERVER_SIDE_ENCRYPTION_ALGORITHM} is    * {@code SSE-KMS} (will default to aws/s3    * master key if left blank).    * With with {@code SSE_C}, the base-64 encoded AES 256 key.    * May be set within a JCEKS file.    * Value: "{@value}".    */
DECL|field|SERVER_SIDE_ENCRYPTION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SERVER_SIDE_ENCRYPTION_KEY
init|=
literal|"fs.s3a.server-side-encryption.key"
decl_stmt|;
comment|//override signature algorithm used for signing requests
DECL|field|SIGNING_ALGORITHM
specifier|public
specifier|static
specifier|final
name|String
name|SIGNING_ALGORITHM
init|=
literal|"fs.s3a.signing-algorithm"
decl_stmt|;
DECL|field|S3N_FOLDER_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|S3N_FOLDER_SUFFIX
init|=
literal|"_$folder$"
decl_stmt|;
DECL|field|FS_S3A_BLOCK_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|FS_S3A_BLOCK_SIZE
init|=
literal|"fs.s3a.block.size"
decl_stmt|;
DECL|field|FS_S3A
specifier|public
specifier|static
specifier|final
name|String
name|FS_S3A
init|=
literal|"s3a"
decl_stmt|;
comment|/** Prefix for all S3A properties: {@value}. */
DECL|field|FS_S3A_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|FS_S3A_PREFIX
init|=
literal|"fs.s3a."
decl_stmt|;
comment|/** Prefix for S3A bucket-specific properties: {@value}. */
DECL|field|FS_S3A_BUCKET_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|FS_S3A_BUCKET_PREFIX
init|=
literal|"fs.s3a.bucket."
decl_stmt|;
DECL|field|S3A_DEFAULT_PORT
specifier|public
specifier|static
specifier|final
name|int
name|S3A_DEFAULT_PORT
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|USER_AGENT_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|USER_AGENT_PREFIX
init|=
literal|"fs.s3a.user.agent.prefix"
decl_stmt|;
comment|/** Whether or not to allow MetadataStore to be source of truth. */
DECL|field|METADATASTORE_AUTHORITATIVE
specifier|public
specifier|static
specifier|final
name|String
name|METADATASTORE_AUTHORITATIVE
init|=
literal|"fs.s3a.metadatastore.authoritative"
decl_stmt|;
DECL|field|DEFAULT_METADATASTORE_AUTHORITATIVE
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_METADATASTORE_AUTHORITATIVE
init|=
literal|false
decl_stmt|;
comment|/** read ahead buffer size to prevent connection re-establishments. */
DECL|field|READAHEAD_RANGE
specifier|public
specifier|static
specifier|final
name|String
name|READAHEAD_RANGE
init|=
literal|"fs.s3a.readahead.range"
decl_stmt|;
DECL|field|DEFAULT_READAHEAD_RANGE
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_READAHEAD_RANGE
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
comment|/**    * Which input strategy to use for buffering, seeking and similar when    * reading data.    * Value: {@value}    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|INPUT_FADVISE
specifier|public
specifier|static
specifier|final
name|String
name|INPUT_FADVISE
init|=
literal|"fs.s3a.experimental.input.fadvise"
decl_stmt|;
comment|/**    * General input. Some seeks, some reads.    * Value: {@value}    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|INPUT_FADV_NORMAL
specifier|public
specifier|static
specifier|final
name|String
name|INPUT_FADV_NORMAL
init|=
literal|"normal"
decl_stmt|;
comment|/**    * Optimized for sequential access.    * Value: {@value}    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|INPUT_FADV_SEQUENTIAL
specifier|public
specifier|static
specifier|final
name|String
name|INPUT_FADV_SEQUENTIAL
init|=
literal|"sequential"
decl_stmt|;
comment|/**    * Optimized purely for random seek+read/positionedRead operations;    * The performance of sequential IO may be reduced in exchange for    * more efficient {@code seek()} operations.    * Value: {@value}    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|INPUT_FADV_RANDOM
specifier|public
specifier|static
specifier|final
name|String
name|INPUT_FADV_RANDOM
init|=
literal|"random"
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|S3_CLIENT_FACTORY_IMPL
specifier|public
specifier|static
specifier|final
name|String
name|S3_CLIENT_FACTORY_IMPL
init|=
literal|"fs.s3a.s3.client.factory.impl"
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
specifier|public
specifier|static
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|S3ClientFactory
argument_list|>
DECL|field|DEFAULT_S3_CLIENT_FACTORY_IMPL
name|DEFAULT_S3_CLIENT_FACTORY_IMPL
init|=
name|DefaultS3ClientFactory
operator|.
name|class
decl_stmt|;
comment|/**    * Maximum number of partitions in a multipart upload: {@value}.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|MAX_MULTIPART_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|MAX_MULTIPART_COUNT
init|=
literal|10000
decl_stmt|;
comment|/* Constants. */
DECL|field|S3_METADATA_STORE_IMPL
specifier|public
specifier|static
specifier|final
name|String
name|S3_METADATA_STORE_IMPL
init|=
literal|"fs.s3a.metadatastore.impl"
decl_stmt|;
comment|/** Minimum period of time (in milliseconds) to keep metadata (may only be    * applied when a prune command is manually run).    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|S3GUARD_CLI_PRUNE_AGE
specifier|public
specifier|static
specifier|final
name|String
name|S3GUARD_CLI_PRUNE_AGE
init|=
literal|"fs.s3a.s3guard.cli.prune.age"
decl_stmt|;
comment|/**    * The region of the DynamoDB service.    *    * This config has no default value. If the user does not set this, the    * S3Guard will operate table in the associated S3 bucket region.    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|S3GUARD_DDB_REGION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|S3GUARD_DDB_REGION_KEY
init|=
literal|"fs.s3a.s3guard.ddb.region"
decl_stmt|;
comment|/**    * The DynamoDB table name to use.    *    * This config has no default value. If the user does not set this, the    * S3Guard implementation will use the respective S3 bucket name.    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|S3GUARD_DDB_TABLE_NAME_KEY
specifier|public
specifier|static
specifier|final
name|String
name|S3GUARD_DDB_TABLE_NAME_KEY
init|=
literal|"fs.s3a.s3guard.ddb.table"
decl_stmt|;
comment|/**    * Test table name to use during DynamoDB integration test.    *    * The table will be modified, and deleted in the end of the tests.    * If this value is not set, the integration tests that would be destructive    * won't run.    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|S3GUARD_DDB_TEST_TABLE_NAME_KEY
specifier|public
specifier|static
specifier|final
name|String
name|S3GUARD_DDB_TEST_TABLE_NAME_KEY
init|=
literal|"fs.s3a.s3guard.ddb.test.table"
decl_stmt|;
comment|/**    * Whether to create the DynamoDB table if the table does not exist.    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|S3GUARD_DDB_TABLE_CREATE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|S3GUARD_DDB_TABLE_CREATE_KEY
init|=
literal|"fs.s3a.s3guard.ddb.table.create"
decl_stmt|;
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|S3GUARD_DDB_TABLE_CAPACITY_READ_KEY
specifier|public
specifier|static
specifier|final
name|String
name|S3GUARD_DDB_TABLE_CAPACITY_READ_KEY
init|=
literal|"fs.s3a.s3guard.ddb.table.capacity.read"
decl_stmt|;
DECL|field|S3GUARD_DDB_TABLE_CAPACITY_READ_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|S3GUARD_DDB_TABLE_CAPACITY_READ_DEFAULT
init|=
literal|500
decl_stmt|;
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|S3GUARD_DDB_TABLE_CAPACITY_WRITE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|S3GUARD_DDB_TABLE_CAPACITY_WRITE_KEY
init|=
literal|"fs.s3a.s3guard.ddb.table.capacity.write"
decl_stmt|;
DECL|field|S3GUARD_DDB_TABLE_CAPACITY_WRITE_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|S3GUARD_DDB_TABLE_CAPACITY_WRITE_DEFAULT
init|=
literal|100
decl_stmt|;
comment|/**    * The maximum put or delete requests per BatchWriteItem request.    *    * Refer to Amazon API reference for this limit.    */
DECL|field|S3GUARD_DDB_BATCH_WRITE_REQUEST_LIMIT
specifier|public
specifier|static
specifier|final
name|int
name|S3GUARD_DDB_BATCH_WRITE_REQUEST_LIMIT
init|=
literal|25
decl_stmt|;
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|S3GUARD_DDB_MAX_RETRIES
specifier|public
specifier|static
specifier|final
name|String
name|S3GUARD_DDB_MAX_RETRIES
init|=
literal|"fs.s3a.s3guard.ddb.max.retries"
decl_stmt|;
comment|/**    * Max retries on batched DynamoDB operations before giving up and    * throwing an IOException.  Default is {@value}. See core-default.xml for    * more detail.    */
DECL|field|S3GUARD_DDB_MAX_RETRIES_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|S3GUARD_DDB_MAX_RETRIES_DEFAULT
init|=
literal|9
decl_stmt|;
comment|/**    * Period of time (in milliseconds) to sleep between batches of writes.    * Currently only applies to prune operations, as they are naturally a    * lower priority than other operations.    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|S3GUARD_DDB_BACKGROUND_SLEEP_MSEC_KEY
specifier|public
specifier|static
specifier|final
name|String
name|S3GUARD_DDB_BACKGROUND_SLEEP_MSEC_KEY
init|=
literal|"fs.s3a.s3guard.ddb.background.sleep"
decl_stmt|;
DECL|field|S3GUARD_DDB_BACKGROUND_SLEEP_MSEC_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|S3GUARD_DDB_BACKGROUND_SLEEP_MSEC_DEFAULT
init|=
literal|25
decl_stmt|;
comment|/**    * The default "Null" metadata store: {@value}.    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|S3GUARD_METASTORE_NULL
specifier|public
specifier|static
specifier|final
name|String
name|S3GUARD_METASTORE_NULL
init|=
literal|"org.apache.hadoop.fs.s3a.s3guard.NullMetadataStore"
decl_stmt|;
comment|/**    * Use Local memory for the metadata: {@value}.    * This is not coherent across processes and must be used for testing only.    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|S3GUARD_METASTORE_LOCAL
specifier|public
specifier|static
specifier|final
name|String
name|S3GUARD_METASTORE_LOCAL
init|=
literal|"org.apache.hadoop.fs.s3a.s3guard.LocalMetadataStore"
decl_stmt|;
comment|/**    * Maximum number of records in LocalMetadataStore.    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|S3GUARD_METASTORE_LOCAL_MAX_RECORDS
specifier|public
specifier|static
specifier|final
name|String
name|S3GUARD_METASTORE_LOCAL_MAX_RECORDS
init|=
literal|"fs.s3a.s3guard.local.max_records"
decl_stmt|;
DECL|field|DEFAULT_S3GUARD_METASTORE_LOCAL_MAX_RECORDS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_S3GUARD_METASTORE_LOCAL_MAX_RECORDS
init|=
literal|256
decl_stmt|;
comment|/**    * Time to live in milliseconds in LocalMetadataStore.    * If zero, time-based expiration is disabled.    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|S3GUARD_METASTORE_LOCAL_ENTRY_TTL
specifier|public
specifier|static
specifier|final
name|String
name|S3GUARD_METASTORE_LOCAL_ENTRY_TTL
init|=
literal|"fs.s3a.s3guard.local.ttl"
decl_stmt|;
DECL|field|DEFAULT_S3GUARD_METASTORE_LOCAL_ENTRY_TTL
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_S3GUARD_METASTORE_LOCAL_ENTRY_TTL
init|=
literal|10
operator|*
literal|1000
decl_stmt|;
comment|/**    * Use DynamoDB for the metadata: {@value}.    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|S3GUARD_METASTORE_DYNAMO
specifier|public
specifier|static
specifier|final
name|String
name|S3GUARD_METASTORE_DYNAMO
init|=
literal|"org.apache.hadoop.fs.s3a.s3guard.DynamoDBMetadataStore"
decl_stmt|;
comment|/**    * Inconsistency (visibility delay) injection settings.    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|FAIL_INJECT_INCONSISTENCY_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FAIL_INJECT_INCONSISTENCY_KEY
init|=
literal|"fs.s3a.failinject.inconsistency.key.substring"
decl_stmt|;
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|FAIL_INJECT_INCONSISTENCY_MSEC
specifier|public
specifier|static
specifier|final
name|String
name|FAIL_INJECT_INCONSISTENCY_MSEC
init|=
literal|"fs.s3a.failinject.inconsistency.msec"
decl_stmt|;
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|FAIL_INJECT_INCONSISTENCY_PROBABILITY
specifier|public
specifier|static
specifier|final
name|String
name|FAIL_INJECT_INCONSISTENCY_PROBABILITY
init|=
literal|"fs.s3a.failinject.inconsistency.probability"
decl_stmt|;
comment|/**    * S3 API level parameters.    */
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|LIST_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|LIST_VERSION
init|=
literal|"fs.s3a.list.version"
decl_stmt|;
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|DEFAULT_LIST_VERSION
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_LIST_VERSION
init|=
literal|2
decl_stmt|;
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|FAIL_INJECT_THROTTLE_PROBABILITY
specifier|public
specifier|static
specifier|final
name|String
name|FAIL_INJECT_THROTTLE_PROBABILITY
init|=
literal|"fs.s3a.failinject.throttle.probability"
decl_stmt|;
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|field|FAIL_INJECT_CLIENT_FACTORY
specifier|public
specifier|static
specifier|final
name|String
name|FAIL_INJECT_CLIENT_FACTORY
init|=
literal|"org.apache.hadoop.fs.s3a.InconsistentS3ClientFactory"
decl_stmt|;
comment|/**    * Number of times to retry any repeatable S3 client request on failure,    * excluding throttling requests: {@value}.    */
DECL|field|RETRY_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|RETRY_LIMIT
init|=
literal|"fs.s3a.retry.limit"
decl_stmt|;
comment|/**    * Default retry limit: {@value}.    */
DECL|field|RETRY_LIMIT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|RETRY_LIMIT_DEFAULT
init|=
name|DEFAULT_MAX_ERROR_RETRIES
decl_stmt|;
comment|/**    * Interval between retry attempts.: {@value}.    */
DECL|field|RETRY_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|RETRY_INTERVAL
init|=
literal|"fs.s3a.retry.interval"
decl_stmt|;
comment|/**    * Default retry interval: {@value}.    */
DECL|field|RETRY_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|RETRY_INTERVAL_DEFAULT
init|=
literal|"500ms"
decl_stmt|;
comment|/**    * Number of times to retry any throttled request: {@value}.    */
DECL|field|RETRY_THROTTLE_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|RETRY_THROTTLE_LIMIT
init|=
literal|"fs.s3a.retry.throttle.limit"
decl_stmt|;
comment|/**    * Default throttled retry limit: {@value}.    */
DECL|field|RETRY_THROTTLE_LIMIT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|RETRY_THROTTLE_LIMIT_DEFAULT
init|=
name|DEFAULT_MAX_ERROR_RETRIES
decl_stmt|;
comment|/**    * Interval between retry attempts on throttled requests: {@value}.    */
DECL|field|RETRY_THROTTLE_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|RETRY_THROTTLE_INTERVAL
init|=
literal|"fs.s3a.retry.throttle.interval"
decl_stmt|;
comment|/**    * Default throttled retry interval: {@value}.    */
DECL|field|RETRY_THROTTLE_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|RETRY_THROTTLE_INTERVAL_DEFAULT
init|=
literal|"500ms"
decl_stmt|;
comment|/**    * Should etags be exposed as checksums?    */
DECL|field|ETAG_CHECKSUM_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|ETAG_CHECKSUM_ENABLED
init|=
literal|"fs.s3a.etag.checksum.enabled"
decl_stmt|;
comment|/**    * Default value: false.    */
DECL|field|ETAG_CHECKSUM_ENABLED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|ETAG_CHECKSUM_ENABLED_DEFAULT
init|=
literal|false
decl_stmt|;
block|}
end_class

end_unit

