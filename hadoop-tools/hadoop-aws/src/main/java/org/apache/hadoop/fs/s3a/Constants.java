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
comment|/**  * All the constants used with the {@link S3AFileSystem}.  */
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
comment|// should we upload directly from memory rather than using a file buffer
DECL|field|FAST_UPLOAD
specifier|public
specifier|static
specifier|final
name|String
name|FAST_UPLOAD
init|=
literal|"fs.s3a.fast.upload"
decl_stmt|;
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
comment|// private | public-read | public-read-write | authenticated-read |
comment|// log-delivery-write | bucket-owner-read | bucket-owner-full-control
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
literal|14400
decl_stmt|;
comment|// s3 server-side encryption
DECL|field|SERVER_SIDE_ENCRYPTION_ALGORITHM
specifier|public
specifier|static
specifier|final
name|String
name|SERVER_SIDE_ENCRYPTION_ALGORITHM
init|=
literal|"fs.s3a.server-side-encryption-algorithm"
decl_stmt|;
comment|/**    * The standard encryption algorithm AWS supports.    * Different implementations may support others (or none).    */
DECL|field|SERVER_SIDE_ENCRYPTION_AES256
specifier|public
specifier|static
specifier|final
name|String
name|SERVER_SIDE_ENCRYPTION_AES256
init|=
literal|"AES256"
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
block|}
end_class

end_unit

