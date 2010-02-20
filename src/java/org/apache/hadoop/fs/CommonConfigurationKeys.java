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

begin_comment
comment|/**   * This class contains constants for configuration keys used  * in the common code.  *  */
end_comment

begin_class
DECL|class|CommonConfigurationKeys
specifier|public
class|class
name|CommonConfigurationKeys
block|{
comment|// The Keys
DECL|field|IO_NATIVE_LIB_AVAILABLE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_NATIVE_LIB_AVAILABLE_KEY
init|=
literal|"io.native.lib.available"
decl_stmt|;
DECL|field|IO_NATIVE_LIB_AVAILABLE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|IO_NATIVE_LIB_AVAILABLE_DEFAULT
init|=
literal|true
decl_stmt|;
DECL|field|NET_TOPOLOGY_SCRIPT_NUMBER_ARGS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NET_TOPOLOGY_SCRIPT_NUMBER_ARGS_KEY
init|=
literal|"net.topology.script.number.args"
decl_stmt|;
DECL|field|NET_TOPOLOGY_SCRIPT_NUMBER_ARGS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|NET_TOPOLOGY_SCRIPT_NUMBER_ARGS_DEFAULT
init|=
literal|100
decl_stmt|;
comment|//FS keys
DECL|field|FS_HOME_DIR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_HOME_DIR_KEY
init|=
literal|"fs.homeDir"
decl_stmt|;
DECL|field|FS_HOME_DIR_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|FS_HOME_DIR_DEFAULT
init|=
literal|"/user"
decl_stmt|;
DECL|field|FS_DEFAULT_NAME_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_DEFAULT_NAME_KEY
init|=
literal|"fs.defaultFS"
decl_stmt|;
DECL|field|FS_DEFAULT_NAME_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|FS_DEFAULT_NAME_DEFAULT
init|=
literal|"file:///"
decl_stmt|;
DECL|field|FS_PERMISSIONS_UMASK_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_PERMISSIONS_UMASK_KEY
init|=
literal|"fs.permissions.umask-mode"
decl_stmt|;
DECL|field|FS_PERMISSIONS_UMASK_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|FS_PERMISSIONS_UMASK_DEFAULT
init|=
literal|0022
decl_stmt|;
DECL|field|FS_DF_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_DF_INTERVAL_KEY
init|=
literal|"fs.df.interval"
decl_stmt|;
DECL|field|FS_DF_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|FS_DF_INTERVAL_DEFAULT
init|=
literal|60000
decl_stmt|;
comment|//Defaults are not specified for following keys
DECL|field|NET_TOPOLOGY_SCRIPT_FILE_NAME_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NET_TOPOLOGY_SCRIPT_FILE_NAME_KEY
init|=
literal|"net.topology.script.file.name"
decl_stmt|;
DECL|field|NET_TOPOLOGY_CONFIGURED_NODE_MAPPING_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NET_TOPOLOGY_CONFIGURED_NODE_MAPPING_KEY
init|=
literal|"net.topology.configured.node.mapping"
decl_stmt|;
DECL|field|NET_TOPOLOGY_NODE_SWITCH_MAPPING_IMPL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NET_TOPOLOGY_NODE_SWITCH_MAPPING_IMPL_KEY
init|=
literal|"net.topology.node.switch.mapping.impl"
decl_stmt|;
DECL|field|FS_CLIENT_BUFFER_DIR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_CLIENT_BUFFER_DIR_KEY
init|=
literal|"fs.client.buffer.dir"
decl_stmt|;
comment|//TBD: Code is not updated to use following keys.
comment|//These keys will be used in later versions
comment|//
DECL|field|FS_LOCAL_BLOCK_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|FS_LOCAL_BLOCK_SIZE_DEFAULT
init|=
literal|32
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|FS_AUTOMATIC_CLOSE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_AUTOMATIC_CLOSE_KEY
init|=
literal|"fs.automatic.close"
decl_stmt|;
DECL|field|FS_AUTOMATIC_CLOSE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|FS_AUTOMATIC_CLOSE_DEFAULT
init|=
literal|true
decl_stmt|;
DECL|field|FS_FILE_IMPL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_FILE_IMPL_KEY
init|=
literal|"fs.file.impl"
decl_stmt|;
DECL|field|FS_FTP_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_FTP_HOST_KEY
init|=
literal|"fs.ftp.host"
decl_stmt|;
DECL|field|FS_FTP_HOST_PORT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_FTP_HOST_PORT_KEY
init|=
literal|"fs.ftp.host.port"
decl_stmt|;
DECL|field|FS_TRASH_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_TRASH_INTERVAL_KEY
init|=
literal|"fs.trash.interval"
decl_stmt|;
DECL|field|FS_TRASH_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|FS_TRASH_INTERVAL_DEFAULT
init|=
literal|0
decl_stmt|;
DECL|field|IO_MAPFILE_BLOOM_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_MAPFILE_BLOOM_SIZE_KEY
init|=
literal|"io.mapfile.bloom.size"
decl_stmt|;
DECL|field|IO_MAPFILE_BLOOM_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IO_MAPFILE_BLOOM_SIZE_DEFAULT
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|IO_MAPFILE_BLOOM_ERROR_RATE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_MAPFILE_BLOOM_ERROR_RATE_KEY
init|=
literal|"io.mapfile.bloom.error.rate"
decl_stmt|;
DECL|field|IO_MAPFILE_BLOOM_ERROR_RATE_DEFAULT
specifier|public
specifier|static
specifier|final
name|float
name|IO_MAPFILE_BLOOM_ERROR_RATE_DEFAULT
init|=
literal|0.005f
decl_stmt|;
DECL|field|IO_COMPRESSION_CODEC_LZO_CLASS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_COMPRESSION_CODEC_LZO_CLASS_KEY
init|=
literal|"io.compression.codec.lzo.class"
decl_stmt|;
DECL|field|IO_COMPRESSION_CODEC_LZO_BUFFERSIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_COMPRESSION_CODEC_LZO_BUFFERSIZE_KEY
init|=
literal|"io.compression.codec.lzo.buffersize"
decl_stmt|;
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
DECL|field|IO_MAP_INDEX_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_MAP_INDEX_INTERVAL_KEY
init|=
literal|"io.map.index.interval"
decl_stmt|;
DECL|field|IO_MAP_INDEX_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IO_MAP_INDEX_INTERVAL_DEFAULT
init|=
literal|128
decl_stmt|;
DECL|field|IO_MAP_INDEX_SKIP_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_MAP_INDEX_SKIP_KEY
init|=
literal|"io.map.index.skip"
decl_stmt|;
DECL|field|IO_MAP_INDEX_SKIP_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IO_MAP_INDEX_SKIP_DEFAULT
init|=
literal|0
decl_stmt|;
DECL|field|IO_SEQFILE_COMPRESS_BLOCKSIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_SEQFILE_COMPRESS_BLOCKSIZE_KEY
init|=
literal|"io.seqfile.compress.blocksize"
decl_stmt|;
DECL|field|IO_SEQFILE_COMPRESS_BLOCKSIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IO_SEQFILE_COMPRESS_BLOCKSIZE_DEFAULT
init|=
literal|1000000
decl_stmt|;
DECL|field|IO_SKIP_CHECKSUM_ERRORS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_SKIP_CHECKSUM_ERRORS_KEY
init|=
literal|"io.skip.checksum.errors"
decl_stmt|;
DECL|field|IO_SKIP_CHECKSUM_ERRORS_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|IO_SKIP_CHECKSUM_ERRORS_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|IO_SORT_MB_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_SORT_MB_KEY
init|=
literal|"io.sort.mb"
decl_stmt|;
DECL|field|IO_SORT_MB_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IO_SORT_MB_DEFAULT
init|=
literal|100
decl_stmt|;
DECL|field|IO_SORT_FACTOR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_SORT_FACTOR_KEY
init|=
literal|"io.sort.factor"
decl_stmt|;
DECL|field|IO_SORT_FACTOR_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IO_SORT_FACTOR_DEFAULT
init|=
literal|100
decl_stmt|;
DECL|field|IO_SERIALIZATIONS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_SERIALIZATIONS_KEY
init|=
literal|"io.serializations"
decl_stmt|;
DECL|field|TFILE_IO_CHUNK_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TFILE_IO_CHUNK_SIZE_KEY
init|=
literal|"tfile.io.chunk.size"
decl_stmt|;
DECL|field|TFILE_IO_CHUNK_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|TFILE_IO_CHUNK_SIZE_DEFAULT
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|TFILE_FS_INPUT_BUFFER_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TFILE_FS_INPUT_BUFFER_SIZE_KEY
init|=
literal|"tfile.fs.input.buffer.size"
decl_stmt|;
DECL|field|TFILE_FS_INPUT_BUFFER_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|TFILE_FS_INPUT_BUFFER_SIZE_DEFAULT
init|=
literal|256
operator|*
literal|1024
decl_stmt|;
DECL|field|TFILE_FS_OUTPUT_BUFFER_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TFILE_FS_OUTPUT_BUFFER_SIZE_KEY
init|=
literal|"tfile.fs.output.buffer.size"
decl_stmt|;
DECL|field|TFILE_FS_OUTPUT_BUFFER_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|TFILE_FS_OUTPUT_BUFFER_SIZE_DEFAULT
init|=
literal|256
operator|*
literal|1024
decl_stmt|;
DECL|field|IPC_PING_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_PING_INTERVAL_KEY
init|=
literal|"ipc.ping.interval"
decl_stmt|;
DECL|field|IPC_PING_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_PING_INTERVAL_DEFAULT
init|=
literal|60000
decl_stmt|;
DECL|field|IPC_CLIENT_PING_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_PING_KEY
init|=
literal|"ipc.client.ping"
decl_stmt|;
DECL|field|IPC_CLIENT_PING_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|IPC_CLIENT_PING_DEFAULT
init|=
literal|true
decl_stmt|;
DECL|field|IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY
init|=
literal|"ipc.client.connection.maxidletime"
decl_stmt|;
DECL|field|IPC_CLIENT_CONNECTION_MAXIDLETIME_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_CLIENT_CONNECTION_MAXIDLETIME_DEFAULT
init|=
literal|10000
decl_stmt|;
DECL|field|IPC_CLIENT_CONNECT_MAX_RETRIES_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_CONNECT_MAX_RETRIES_KEY
init|=
literal|"ipc.client.connect.max.retries"
decl_stmt|;
DECL|field|IPC_CLIENT_CONNECT_MAX_RETRIES_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_CLIENT_CONNECT_MAX_RETRIES_DEFAULT
init|=
literal|10
decl_stmt|;
DECL|field|IPC_CLIENT_TCPNODELAY_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_TCPNODELAY_KEY
init|=
literal|"ipc.client.tcpnodelay"
decl_stmt|;
DECL|field|IPC_CLIENT_TCPNODELAY_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|IPC_CLIENT_TCPNODELAY_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|IPC_SERVER_LISTEN_QUEUE_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_SERVER_LISTEN_QUEUE_SIZE_KEY
init|=
literal|"ipc.server.listen.queue.size"
decl_stmt|;
DECL|field|IPC_SERVER_LISTEN_QUEUE_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_SERVER_LISTEN_QUEUE_SIZE_DEFAULT
init|=
literal|128
decl_stmt|;
DECL|field|IPC_CLIENT_KILL_MAX_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_KILL_MAX_KEY
init|=
literal|"ipc.client.kill.max"
decl_stmt|;
DECL|field|IPC_CLIENT_KILL_MAX_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_CLIENT_KILL_MAX_DEFAULT
init|=
literal|10
decl_stmt|;
DECL|field|IPC_CLIENT_IDLETHRESHOLD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_IDLETHRESHOLD_KEY
init|=
literal|"ipc.client.idlethreshold"
decl_stmt|;
DECL|field|IPC_CLIENT_IDLETHRESHOLD_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_CLIENT_IDLETHRESHOLD_DEFAULT
init|=
literal|4000
decl_stmt|;
DECL|field|IPC_SERVER_TCPNODELAY_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_SERVER_TCPNODELAY_KEY
init|=
literal|"ipc.server.tcpnodelay"
decl_stmt|;
DECL|field|IPC_SERVER_TCPNODELAY_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|IPC_SERVER_TCPNODELAY_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|IPC_SERVER_RPC_MAX_RESPONSE_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_SERVER_RPC_MAX_RESPONSE_SIZE_KEY
init|=
literal|"ipc.server.max.response.size"
decl_stmt|;
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
DECL|field|HADOOP_RPC_SOCKET_FACTORY_CLASS_DEFAULT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_RPC_SOCKET_FACTORY_CLASS_DEFAULT_KEY
init|=
literal|"hadoop.rpc.socket.factory.class.default"
decl_stmt|;
DECL|field|HADOOP_SOCKS_SERVER_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_SOCKS_SERVER_KEY
init|=
literal|"hadoop.socks.server"
decl_stmt|;
DECL|field|HADOOP_JOB_UGI_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_JOB_UGI_KEY
init|=
literal|"hadoop.job.ugi"
decl_stmt|;
DECL|field|HADOOP_UTIL_HASH_TYPE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_UTIL_HASH_TYPE_KEY
init|=
literal|"hadoop.util.hash.type"
decl_stmt|;
DECL|field|HADOOP_UTIL_HASH_TYPE_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_UTIL_HASH_TYPE_DEFAULT
init|=
literal|"murmur"
decl_stmt|;
DECL|field|HADOOP_SECURITY_GROUP_MAPPING
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_SECURITY_GROUP_MAPPING
init|=
literal|"hadoop.security.group.mapping"
decl_stmt|;
DECL|field|HADOOP_SECURITY_GROUPS_CACHE_SECS
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_SECURITY_GROUPS_CACHE_SECS
init|=
literal|"hadoop.security.groups.cache.secs"
decl_stmt|;
DECL|field|HADOOP_SECURITY_AUTHENTICATION
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_SECURITY_AUTHENTICATION
init|=
literal|"hadoop.security.authentication"
decl_stmt|;
block|}
end_class

end_unit

