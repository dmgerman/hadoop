begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.aliyun.oss
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|aliyun
operator|.
name|oss
package|;
end_package

begin_comment
comment|/**  * ALL configuration constants for OSS filesystem.  */
end_comment

begin_class
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
comment|// Class of credential provider
DECL|field|ALIYUN_OSS_CREDENTIALS_PROVIDER_KEY
specifier|public
specifier|static
specifier|final
name|String
name|ALIYUN_OSS_CREDENTIALS_PROVIDER_KEY
init|=
literal|"fs.oss.credentials.provider"
decl_stmt|;
comment|// OSS access verification
DECL|field|ACCESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|ACCESS_KEY
init|=
literal|"fs.oss.accessKeyId"
decl_stmt|;
DECL|field|SECRET_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SECRET_KEY
init|=
literal|"fs.oss.accessKeySecret"
decl_stmt|;
DECL|field|SECURITY_TOKEN
specifier|public
specifier|static
specifier|final
name|String
name|SECURITY_TOKEN
init|=
literal|"fs.oss.securityToken"
decl_stmt|;
comment|// Number of simultaneous connections to oss
DECL|field|MAXIMUM_CONNECTIONS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|MAXIMUM_CONNECTIONS_KEY
init|=
literal|"fs.oss.connection.maximum"
decl_stmt|;
DECL|field|MAXIMUM_CONNECTIONS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|MAXIMUM_CONNECTIONS_DEFAULT
init|=
literal|32
decl_stmt|;
comment|// Connect to oss over ssl
DECL|field|SECURE_CONNECTIONS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SECURE_CONNECTIONS_KEY
init|=
literal|"fs.oss.connection.secure.enabled"
decl_stmt|;
DECL|field|SECURE_CONNECTIONS_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|SECURE_CONNECTIONS_DEFAULT
init|=
literal|true
decl_stmt|;
comment|// Use a custom endpoint
DECL|field|ENDPOINT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|ENDPOINT_KEY
init|=
literal|"fs.oss.endpoint"
decl_stmt|;
comment|// Connect to oss through a proxy server
DECL|field|PROXY_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_HOST_KEY
init|=
literal|"fs.oss.proxy.host"
decl_stmt|;
DECL|field|PROXY_PORT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_PORT_KEY
init|=
literal|"fs.oss.proxy.port"
decl_stmt|;
DECL|field|PROXY_USERNAME_KEY
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_USERNAME_KEY
init|=
literal|"fs.oss.proxy.username"
decl_stmt|;
DECL|field|PROXY_PASSWORD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_PASSWORD_KEY
init|=
literal|"fs.oss.proxy.password"
decl_stmt|;
DECL|field|PROXY_DOMAIN_KEY
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_DOMAIN_KEY
init|=
literal|"fs.oss.proxy.domain"
decl_stmt|;
DECL|field|PROXY_WORKSTATION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_WORKSTATION_KEY
init|=
literal|"fs.oss.proxy.workstation"
decl_stmt|;
comment|// Number of times we should retry errors
DECL|field|MAX_ERROR_RETRIES_KEY
specifier|public
specifier|static
specifier|final
name|String
name|MAX_ERROR_RETRIES_KEY
init|=
literal|"fs.oss.attempts.maximum"
decl_stmt|;
DECL|field|MAX_ERROR_RETRIES_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|MAX_ERROR_RETRIES_DEFAULT
init|=
literal|20
decl_stmt|;
comment|// Time until we give up trying to establish a connection to oss
DECL|field|ESTABLISH_TIMEOUT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|ESTABLISH_TIMEOUT_KEY
init|=
literal|"fs.oss.connection.establish.timeout"
decl_stmt|;
DECL|field|ESTABLISH_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|ESTABLISH_TIMEOUT_DEFAULT
init|=
literal|50000
decl_stmt|;
comment|// Time until we give up on a connection to oss
DECL|field|SOCKET_TIMEOUT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SOCKET_TIMEOUT_KEY
init|=
literal|"fs.oss.connection.timeout"
decl_stmt|;
DECL|field|SOCKET_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|SOCKET_TIMEOUT_DEFAULT
init|=
literal|200000
decl_stmt|;
comment|// Number of records to get while paging through a directory listing
DECL|field|MAX_PAGING_KEYS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|MAX_PAGING_KEYS_KEY
init|=
literal|"fs.oss.paging.maximum"
decl_stmt|;
DECL|field|MAX_PAGING_KEYS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|MAX_PAGING_KEYS_DEFAULT
init|=
literal|1000
decl_stmt|;
comment|// Size of each of or multipart pieces in bytes
DECL|field|MULTIPART_UPLOAD_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|MULTIPART_UPLOAD_SIZE_KEY
init|=
literal|"fs.oss.multipart.upload.size"
decl_stmt|;
DECL|field|MULTIPART_UPLOAD_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|MULTIPART_UPLOAD_SIZE_DEFAULT
init|=
literal|10
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|MULTIPART_UPLOAD_PART_NUM_LIMIT
specifier|public
specifier|static
specifier|final
name|int
name|MULTIPART_UPLOAD_PART_NUM_LIMIT
init|=
literal|10000
decl_stmt|;
comment|// Minimum size in bytes before we start a multipart uploads or copy
DECL|field|MIN_MULTIPART_UPLOAD_THRESHOLD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|MIN_MULTIPART_UPLOAD_THRESHOLD_KEY
init|=
literal|"fs.oss.multipart.upload.threshold"
decl_stmt|;
DECL|field|MIN_MULTIPART_UPLOAD_THRESHOLD_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|MIN_MULTIPART_UPLOAD_THRESHOLD_DEFAULT
init|=
literal|20
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|MULTIPART_DOWNLOAD_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|MULTIPART_DOWNLOAD_SIZE_KEY
init|=
literal|"fs.oss.multipart.download.size"
decl_stmt|;
DECL|field|MULTIPART_DOWNLOAD_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|MULTIPART_DOWNLOAD_SIZE_DEFAULT
init|=
literal|100
operator|*
literal|1024
decl_stmt|;
comment|// Comma separated list of directories
DECL|field|BUFFER_DIR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|BUFFER_DIR_KEY
init|=
literal|"fs.oss.buffer.dir"
decl_stmt|;
comment|// private | public-read | public-read-write | authenticated-read |
comment|// log-delivery-write | bucket-owner-read | bucket-owner-full-control
DECL|field|CANNED_ACL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|CANNED_ACL_KEY
init|=
literal|"fs.oss.acl.default"
decl_stmt|;
DECL|field|CANNED_ACL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|CANNED_ACL_DEFAULT
init|=
literal|""
decl_stmt|;
comment|// OSS server-side encryption
DECL|field|SERVER_SIDE_ENCRYPTION_ALGORITHM_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SERVER_SIDE_ENCRYPTION_ALGORITHM_KEY
init|=
literal|"fs.oss.server-side-encryption-algorithm"
decl_stmt|;
DECL|field|FS_OSS_BLOCK_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_OSS_BLOCK_SIZE_KEY
init|=
literal|"fs.oss.block.size"
decl_stmt|;
DECL|field|FS_OSS_BLOCK_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|FS_OSS_BLOCK_SIZE_DEFAULT
init|=
literal|64
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|FS_OSS
specifier|public
specifier|static
specifier|final
name|String
name|FS_OSS
init|=
literal|"oss"
decl_stmt|;
DECL|field|MIN_MULTIPART_UPLOAD_PART_SIZE
specifier|public
specifier|static
specifier|final
name|long
name|MIN_MULTIPART_UPLOAD_PART_SIZE
init|=
literal|100
operator|*
literal|1024L
decl_stmt|;
DECL|field|MAX_RETRIES
specifier|public
specifier|static
specifier|final
name|int
name|MAX_RETRIES
init|=
literal|10
decl_stmt|;
block|}
end_class

end_unit

