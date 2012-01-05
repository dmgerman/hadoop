begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
comment|/**   * This class contains constants for configuration keys used  * in the common code.  *  * It inherits all the publicly documented configuration keys  * and adds unsupported keys.  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|CommonConfigurationKeys
specifier|public
class|class
name|CommonConfigurationKeys
extends|extends
name|CommonConfigurationKeysPublic
block|{
comment|/** Default location for user home directories */
DECL|field|FS_HOME_DIR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_HOME_DIR_KEY
init|=
literal|"fs.homeDir"
decl_stmt|;
comment|/** Default value for FS_HOME_DIR_KEY */
DECL|field|FS_HOME_DIR_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|FS_HOME_DIR_DEFAULT
init|=
literal|"/user"
decl_stmt|;
comment|/** Default umask for files created in HDFS */
DECL|field|FS_PERMISSIONS_UMASK_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_PERMISSIONS_UMASK_KEY
init|=
literal|"fs.permissions.umask-mode"
decl_stmt|;
comment|/** Default value for FS_PERMISSIONS_UMASK_KEY */
DECL|field|FS_PERMISSIONS_UMASK_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|FS_PERMISSIONS_UMASK_DEFAULT
init|=
literal|0022
decl_stmt|;
comment|/** @deprecated not used, jira was created to remove this constant:    * https://issues.apache.org/jira/browse/HADOOP-6802    */
DECL|field|FS_CLIENT_BUFFER_DIR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_CLIENT_BUFFER_DIR_KEY
init|=
literal|"fs.client.buffer.dir"
decl_stmt|;
comment|/** How often does RPC client send pings to RPC server */
DECL|field|IPC_PING_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_PING_INTERVAL_KEY
init|=
literal|"ipc.ping.interval"
decl_stmt|;
comment|/** Default value for IPC_PING_INTERVAL_KEY */
DECL|field|IPC_PING_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_PING_INTERVAL_DEFAULT
init|=
literal|60000
decl_stmt|;
comment|// 1 min
comment|/** Enables pings from RPC client to the server */
DECL|field|IPC_CLIENT_PING_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_PING_KEY
init|=
literal|"ipc.client.ping"
decl_stmt|;
comment|/** Default value of IPC_CLIENT_PING_KEY */
DECL|field|IPC_CLIENT_PING_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|IPC_CLIENT_PING_DEFAULT
init|=
literal|true
decl_stmt|;
comment|/** Responses larger than this will be logged */
DECL|field|IPC_SERVER_RPC_MAX_RESPONSE_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_SERVER_RPC_MAX_RESPONSE_SIZE_KEY
init|=
literal|"ipc.server.max.response.size"
decl_stmt|;
comment|/** Default value for IPC_SERVER_RPC_MAX_RESPONSE_SIZE_KEY */
DECL|field|IPC_SERVER_RPC_MAX_RESPONSE_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_SERVER_RPC_MAX_RESPONSE_SIZE_DEFAULT
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|/** Number of threads in RPC server reading from the socket */
DECL|field|IPC_SERVER_RPC_READ_THREADS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_SERVER_RPC_READ_THREADS_KEY
init|=
literal|"ipc.server.read.threadpool.size"
decl_stmt|;
comment|/** Default value for IPC_SERVER_RPC_READ_THREADS_KEY */
DECL|field|IPC_SERVER_RPC_READ_THREADS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_SERVER_RPC_READ_THREADS_DEFAULT
init|=
literal|1
decl_stmt|;
comment|/** How many calls per handler are allowed in the queue. */
DECL|field|IPC_SERVER_HANDLER_QUEUE_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_SERVER_HANDLER_QUEUE_SIZE_KEY
init|=
literal|"ipc.server.handler.queue.size"
decl_stmt|;
comment|/** Default value for IPC_SERVER_HANDLER_QUEUE_SIZE_KEY */
DECL|field|IPC_SERVER_HANDLER_QUEUE_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_SERVER_HANDLER_QUEUE_SIZE_DEFAULT
init|=
literal|100
decl_stmt|;
comment|/** Internal buffer size for Lzo compressor/decompressors */
DECL|field|IO_COMPRESSION_CODEC_LZO_BUFFERSIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_COMPRESSION_CODEC_LZO_BUFFERSIZE_KEY
init|=
literal|"io.compression.codec.lzo.buffersize"
decl_stmt|;
comment|/** Default value for IO_COMPRESSION_CODEC_LZO_BUFFERSIZE_KEY */
DECL|field|IO_COMPRESSION_CODEC_LZO_BUFFERSIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IO_COMPRESSION_CODEC_LZO_BUFFERSIZE_DEFAULT
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
comment|/** This is for specifying the implementation for the mappings from    * hostnames to the racks they belong to    */
DECL|field|NET_TOPOLOGY_CONFIGURED_NODE_MAPPING_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NET_TOPOLOGY_CONFIGURED_NODE_MAPPING_KEY
init|=
literal|"net.topology.configured.node.mapping"
decl_stmt|;
comment|/** Internal buffer size for Snappy compressor/decompressors */
DECL|field|IO_COMPRESSION_CODEC_SNAPPY_BUFFERSIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_COMPRESSION_CODEC_SNAPPY_BUFFERSIZE_KEY
init|=
literal|"io.compression.codec.snappy.buffersize"
decl_stmt|;
comment|/** Default value for IO_COMPRESSION_CODEC_SNAPPY_BUFFERSIZE_KEY */
DECL|field|IO_COMPRESSION_CODEC_SNAPPY_BUFFERSIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IO_COMPRESSION_CODEC_SNAPPY_BUFFERSIZE_DEFAULT
init|=
literal|256
operator|*
literal|1024
decl_stmt|;
comment|/** Internal buffer size for Snappy compressor/decompressors */
DECL|field|IO_COMPRESSION_CODEC_LZ4_BUFFERSIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_COMPRESSION_CODEC_LZ4_BUFFERSIZE_KEY
init|=
literal|"io.compression.codec.lz4.buffersize"
decl_stmt|;
comment|/** Default value for IO_COMPRESSION_CODEC_SNAPPY_BUFFERSIZE_KEY */
DECL|field|IO_COMPRESSION_CODEC_LZ4_BUFFERSIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IO_COMPRESSION_CODEC_LZ4_BUFFERSIZE_DEFAULT
init|=
literal|256
operator|*
literal|1024
decl_stmt|;
comment|/**    * Service Authorization    */
specifier|public
specifier|static
specifier|final
name|String
DECL|field|HADOOP_SECURITY_SERVICE_AUTHORIZATION_REFRESH_POLICY
name|HADOOP_SECURITY_SERVICE_AUTHORIZATION_REFRESH_POLICY
init|=
literal|"security.refresh.policy.protocol.acl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|HADOOP_SECURITY_SERVICE_AUTHORIZATION_GET_USER_MAPPINGS
name|HADOOP_SECURITY_SERVICE_AUTHORIZATION_GET_USER_MAPPINGS
init|=
literal|"security.get.user.mappings.protocol.acl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|HADOOP_SECURITY_SERVICE_AUTHORIZATION_REFRESH_USER_MAPPINGS
name|HADOOP_SECURITY_SERVICE_AUTHORIZATION_REFRESH_USER_MAPPINGS
init|=
literal|"security.refresh.user.mappings.protocol.acl"
decl_stmt|;
block|}
end_class

end_unit

