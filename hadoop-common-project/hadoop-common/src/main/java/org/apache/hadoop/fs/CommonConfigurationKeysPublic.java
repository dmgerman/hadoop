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

begin_comment
comment|/**   * This class contains constants for configuration keys used  * in the common code.  *  * It includes all publicly documented configuration keys. In general  * this class should not be used directly (use CommonConfigurationKeys  * instead)  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
DECL|class|CommonConfigurationKeysPublic
specifier|public
class|class
name|CommonConfigurationKeysPublic
block|{
comment|// The Keys
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|IO_NATIVE_LIB_AVAILABLE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_NATIVE_LIB_AVAILABLE_KEY
init|=
literal|"io.native.lib.available"
decl_stmt|;
comment|/** Default value for IO_NATIVE_LIB_AVAILABLE_KEY */
DECL|field|IO_NATIVE_LIB_AVAILABLE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|IO_NATIVE_LIB_AVAILABLE_DEFAULT
init|=
literal|true
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|NET_TOPOLOGY_SCRIPT_NUMBER_ARGS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NET_TOPOLOGY_SCRIPT_NUMBER_ARGS_KEY
init|=
literal|"net.topology.script.number.args"
decl_stmt|;
comment|/** Default value for NET_TOPOLOGY_SCRIPT_NUMBER_ARGS_KEY */
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
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|FS_DEFAULT_NAME_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_DEFAULT_NAME_KEY
init|=
literal|"fs.defaultFS"
decl_stmt|;
comment|/** Default value for FS_DEFAULT_NAME_KEY */
DECL|field|FS_DEFAULT_NAME_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|FS_DEFAULT_NAME_DEFAULT
init|=
literal|"file:///"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|FS_DF_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_DF_INTERVAL_KEY
init|=
literal|"fs.df.interval"
decl_stmt|;
comment|/** Default value for FS_DF_INTERVAL_KEY */
DECL|field|FS_DF_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|FS_DF_INTERVAL_DEFAULT
init|=
literal|60000
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|FS_DU_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_DU_INTERVAL_KEY
init|=
literal|"fs.du.interval"
decl_stmt|;
comment|/** Default value for FS_DU_INTERVAL_KEY */
DECL|field|FS_DU_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|FS_DU_INTERVAL_DEFAULT
init|=
literal|60000
decl_stmt|;
comment|//Defaults are not specified for following keys
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|NET_TOPOLOGY_SCRIPT_FILE_NAME_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NET_TOPOLOGY_SCRIPT_FILE_NAME_KEY
init|=
literal|"net.topology.script.file.name"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|NET_TOPOLOGY_NODE_SWITCH_MAPPING_IMPL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NET_TOPOLOGY_NODE_SWITCH_MAPPING_IMPL_KEY
init|=
literal|"net.topology.node.switch.mapping.impl"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|NET_TOPOLOGY_IMPL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NET_TOPOLOGY_IMPL_KEY
init|=
literal|"net.topology.impl"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|NET_TOPOLOGY_TABLE_MAPPING_FILE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NET_TOPOLOGY_TABLE_MAPPING_FILE_KEY
init|=
literal|"net.topology.table.file.name"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|FS_TRASH_CHECKPOINT_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_TRASH_CHECKPOINT_INTERVAL_KEY
init|=
literal|"fs.trash.checkpoint.interval"
decl_stmt|;
comment|/** Default value for FS_TRASH_CHECKPOINT_INTERVAL_KEY */
DECL|field|FS_TRASH_CHECKPOINT_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|FS_TRASH_CHECKPOINT_INTERVAL_DEFAULT
init|=
literal|0
decl_stmt|;
comment|// TBD: Code is still using hardcoded values (e.g. "fs.automatic.close")
comment|// instead of constant (e.g. FS_AUTOMATIC_CLOSE_KEY)
comment|//
comment|/** Not used anywhere, looks like default value for FS_LOCAL_BLOCK_SIZE */
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
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|FS_AUTOMATIC_CLOSE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_AUTOMATIC_CLOSE_KEY
init|=
literal|"fs.automatic.close"
decl_stmt|;
comment|/** Default value for FS_AUTOMATIC_CLOSE_KEY */
DECL|field|FS_AUTOMATIC_CLOSE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|FS_AUTOMATIC_CLOSE_DEFAULT
init|=
literal|true
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|FS_FILE_IMPL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_FILE_IMPL_KEY
init|=
literal|"fs.file.impl"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|FS_FTP_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_FTP_HOST_KEY
init|=
literal|"fs.ftp.host"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|FS_FTP_HOST_PORT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_FTP_HOST_PORT_KEY
init|=
literal|"fs.ftp.host.port"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|FS_TRASH_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FS_TRASH_INTERVAL_KEY
init|=
literal|"fs.trash.interval"
decl_stmt|;
comment|/** Default value for FS_TRASH_INTERVAL_KEY */
DECL|field|FS_TRASH_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|FS_TRASH_INTERVAL_DEFAULT
init|=
literal|0
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|IO_MAPFILE_BLOOM_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_MAPFILE_BLOOM_SIZE_KEY
init|=
literal|"io.mapfile.bloom.size"
decl_stmt|;
comment|/** Default value for IO_MAPFILE_BLOOM_SIZE_KEY */
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
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|IO_MAPFILE_BLOOM_ERROR_RATE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_MAPFILE_BLOOM_ERROR_RATE_KEY
init|=
literal|"io.mapfile.bloom.error.rate"
decl_stmt|;
comment|/** Default value for IO_MAPFILE_BLOOM_ERROR_RATE_KEY */
DECL|field|IO_MAPFILE_BLOOM_ERROR_RATE_DEFAULT
specifier|public
specifier|static
specifier|final
name|float
name|IO_MAPFILE_BLOOM_ERROR_RATE_DEFAULT
init|=
literal|0.005f
decl_stmt|;
comment|/** Codec class that implements Lzo compression algorithm */
DECL|field|IO_COMPRESSION_CODEC_LZO_CLASS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_COMPRESSION_CODEC_LZO_CLASS_KEY
init|=
literal|"io.compression.codec.lzo.class"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|IO_MAP_INDEX_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_MAP_INDEX_INTERVAL_KEY
init|=
literal|"io.map.index.interval"
decl_stmt|;
comment|/** Default value for IO_MAP_INDEX_INTERVAL_DEFAULT */
DECL|field|IO_MAP_INDEX_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IO_MAP_INDEX_INTERVAL_DEFAULT
init|=
literal|128
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|IO_MAP_INDEX_SKIP_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_MAP_INDEX_SKIP_KEY
init|=
literal|"io.map.index.skip"
decl_stmt|;
comment|/** Default value for IO_MAP_INDEX_SKIP_KEY */
DECL|field|IO_MAP_INDEX_SKIP_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IO_MAP_INDEX_SKIP_DEFAULT
init|=
literal|0
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|IO_SEQFILE_COMPRESS_BLOCKSIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_SEQFILE_COMPRESS_BLOCKSIZE_KEY
init|=
literal|"io.seqfile.compress.blocksize"
decl_stmt|;
comment|/** Default value for IO_SEQFILE_COMPRESS_BLOCKSIZE_KEY */
DECL|field|IO_SEQFILE_COMPRESS_BLOCKSIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IO_SEQFILE_COMPRESS_BLOCKSIZE_DEFAULT
init|=
literal|1000000
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|IO_FILE_BUFFER_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_FILE_BUFFER_SIZE_KEY
init|=
literal|"io.file.buffer.size"
decl_stmt|;
comment|/** Default value for IO_FILE_BUFFER_SIZE_KEY */
DECL|field|IO_FILE_BUFFER_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IO_FILE_BUFFER_SIZE_DEFAULT
init|=
literal|4096
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|IO_SKIP_CHECKSUM_ERRORS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_SKIP_CHECKSUM_ERRORS_KEY
init|=
literal|"io.skip.checksum.errors"
decl_stmt|;
comment|/** Default value for IO_SKIP_CHECKSUM_ERRORS_KEY */
DECL|field|IO_SKIP_CHECKSUM_ERRORS_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|IO_SKIP_CHECKSUM_ERRORS_DEFAULT
init|=
literal|false
decl_stmt|;
comment|/**    * @deprecated Moved to mapreduce, see mapreduce.task.io.sort.mb    * in mapred-default.xml    * See https://issues.apache.org/jira/browse/HADOOP-6801    */
DECL|field|IO_SORT_MB_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_SORT_MB_KEY
init|=
literal|"io.sort.mb"
decl_stmt|;
comment|/** Default value for IO_SORT_MB_DEFAULT */
DECL|field|IO_SORT_MB_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IO_SORT_MB_DEFAULT
init|=
literal|100
decl_stmt|;
comment|/**    * @deprecated Moved to mapreduce, see mapreduce.task.io.sort.factor    * in mapred-default.xml    * See https://issues.apache.org/jira/browse/HADOOP-6801    */
DECL|field|IO_SORT_FACTOR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_SORT_FACTOR_KEY
init|=
literal|"io.sort.factor"
decl_stmt|;
comment|/** Default value for IO_SORT_FACTOR_DEFAULT */
DECL|field|IO_SORT_FACTOR_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IO_SORT_FACTOR_DEFAULT
init|=
literal|100
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|IO_SERIALIZATIONS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_SERIALIZATIONS_KEY
init|=
literal|"io.serializations"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|TFILE_IO_CHUNK_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TFILE_IO_CHUNK_SIZE_KEY
init|=
literal|"tfile.io.chunk.size"
decl_stmt|;
comment|/** Default value for TFILE_IO_CHUNK_SIZE_DEFAULT */
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
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|TFILE_FS_INPUT_BUFFER_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TFILE_FS_INPUT_BUFFER_SIZE_KEY
init|=
literal|"tfile.fs.input.buffer.size"
decl_stmt|;
comment|/** Default value for TFILE_FS_INPUT_BUFFER_SIZE_KEY */
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
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|TFILE_FS_OUTPUT_BUFFER_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TFILE_FS_OUTPUT_BUFFER_SIZE_KEY
init|=
literal|"tfile.fs.output.buffer.size"
decl_stmt|;
comment|/** Default value for TFILE_FS_OUTPUT_BUFFER_SIZE_KEY */
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
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY
init|=
literal|"ipc.client.connection.maxidletime"
decl_stmt|;
comment|/** Default value for IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY */
DECL|field|IPC_CLIENT_CONNECTION_MAXIDLETIME_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_CLIENT_CONNECTION_MAXIDLETIME_DEFAULT
init|=
literal|10000
decl_stmt|;
comment|// 10s
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|IPC_CLIENT_CONNECT_TIMEOUT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_CONNECT_TIMEOUT_KEY
init|=
literal|"ipc.client.connect.timeout"
decl_stmt|;
comment|/** Default value for IPC_CLIENT_CONNECT_TIMEOUT_KEY */
DECL|field|IPC_CLIENT_CONNECT_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_CLIENT_CONNECT_TIMEOUT_DEFAULT
init|=
literal|20000
decl_stmt|;
comment|// 20s
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|IPC_CLIENT_CONNECT_MAX_RETRIES_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_CONNECT_MAX_RETRIES_KEY
init|=
literal|"ipc.client.connect.max.retries"
decl_stmt|;
comment|/** Default value for IPC_CLIENT_CONNECT_MAX_RETRIES_KEY */
DECL|field|IPC_CLIENT_CONNECT_MAX_RETRIES_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_CLIENT_CONNECT_MAX_RETRIES_DEFAULT
init|=
literal|10
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SOCKET_TIMEOUTS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SOCKET_TIMEOUTS_KEY
init|=
literal|"ipc.client.connect.max.retries.on.timeouts"
decl_stmt|;
comment|/** Default value for IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SOCKET_TIMEOUTS_KEY */
DECL|field|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SOCKET_TIMEOUTS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SOCKET_TIMEOUTS_DEFAULT
init|=
literal|45
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|IPC_CLIENT_TCPNODELAY_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_TCPNODELAY_KEY
init|=
literal|"ipc.client.tcpnodelay"
decl_stmt|;
comment|/** Defalt value for IPC_CLIENT_TCPNODELAY_KEY */
DECL|field|IPC_CLIENT_TCPNODELAY_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|IPC_CLIENT_TCPNODELAY_DEFAULT
init|=
literal|false
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|IPC_SERVER_LISTEN_QUEUE_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_SERVER_LISTEN_QUEUE_SIZE_KEY
init|=
literal|"ipc.server.listen.queue.size"
decl_stmt|;
comment|/** Default value for IPC_SERVER_LISTEN_QUEUE_SIZE_KEY */
DECL|field|IPC_SERVER_LISTEN_QUEUE_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_SERVER_LISTEN_QUEUE_SIZE_DEFAULT
init|=
literal|128
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|IPC_CLIENT_KILL_MAX_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_KILL_MAX_KEY
init|=
literal|"ipc.client.kill.max"
decl_stmt|;
comment|/** Default value for IPC_CLIENT_KILL_MAX_KEY */
DECL|field|IPC_CLIENT_KILL_MAX_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_CLIENT_KILL_MAX_DEFAULT
init|=
literal|10
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|IPC_CLIENT_IDLETHRESHOLD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_IDLETHRESHOLD_KEY
init|=
literal|"ipc.client.idlethreshold"
decl_stmt|;
comment|/** Default value for IPC_CLIENT_IDLETHRESHOLD_DEFAULT */
DECL|field|IPC_CLIENT_IDLETHRESHOLD_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_CLIENT_IDLETHRESHOLD_DEFAULT
init|=
literal|4000
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|IPC_SERVER_TCPNODELAY_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_SERVER_TCPNODELAY_KEY
init|=
literal|"ipc.server.tcpnodelay"
decl_stmt|;
comment|/** Default value for IPC_SERVER_TCPNODELAY_KEY */
DECL|field|IPC_SERVER_TCPNODELAY_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|IPC_SERVER_TCPNODELAY_DEFAULT
init|=
literal|false
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|HADOOP_RPC_SOCKET_FACTORY_CLASS_DEFAULT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_RPC_SOCKET_FACTORY_CLASS_DEFAULT_KEY
init|=
literal|"hadoop.rpc.socket.factory.class.default"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|HADOOP_SOCKS_SERVER_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_SOCKS_SERVER_KEY
init|=
literal|"hadoop.socks.server"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|HADOOP_UTIL_HASH_TYPE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_UTIL_HASH_TYPE_KEY
init|=
literal|"hadoop.util.hash.type"
decl_stmt|;
comment|/** Default value for HADOOP_UTIL_HASH_TYPE_KEY */
DECL|field|HADOOP_UTIL_HASH_TYPE_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_UTIL_HASH_TYPE_DEFAULT
init|=
literal|"murmur"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|HADOOP_SECURITY_GROUP_MAPPING
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_SECURITY_GROUP_MAPPING
init|=
literal|"hadoop.security.group.mapping"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|HADOOP_SECURITY_GROUPS_CACHE_SECS
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_SECURITY_GROUPS_CACHE_SECS
init|=
literal|"hadoop.security.groups.cache.secs"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|HADOOP_SECURITY_AUTHENTICATION
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_SECURITY_AUTHENTICATION
init|=
literal|"hadoop.security.authentication"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|HADOOP_SECURITY_AUTHORIZATION
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_SECURITY_AUTHORIZATION
init|=
literal|"hadoop.security.authorization"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|HADOOP_SECURITY_INSTRUMENTATION_REQUIRES_ADMIN
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_SECURITY_INSTRUMENTATION_REQUIRES_ADMIN
init|=
literal|"hadoop.security.instrumentation.requires.admin"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|HADOOP_SECURITY_SERVICE_USER_NAME_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_SECURITY_SERVICE_USER_NAME_KEY
init|=
literal|"hadoop.security.service.user.name.key"
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|HADOOP_SECURITY_AUTH_TO_LOCAL
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_SECURITY_AUTH_TO_LOCAL
init|=
literal|"hadoop.security.auth_to_local"
decl_stmt|;
DECL|field|HADOOP_SSL_ENABLED_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_SSL_ENABLED_KEY
init|=
literal|"hadoop.ssl.enabled"
decl_stmt|;
DECL|field|HADOOP_SSL_ENABLED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|HADOOP_SSL_ENABLED_DEFAULT
init|=
literal|false
decl_stmt|;
comment|/** See<a href="{@docRoot}/../core-default.html">core-default.xml</a> */
DECL|field|HADOOP_KERBEROS_MIN_SECONDS_BEFORE_RELOGIN
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_KERBEROS_MIN_SECONDS_BEFORE_RELOGIN
init|=
literal|"hadoop.kerberos.min.seconds.before.relogin"
decl_stmt|;
comment|/** Default value for HADOOP_KERBEROS_MIN_SECONDS_BEFORE_RELOGIN */
DECL|field|HADOOP_KERBEROS_MIN_SECONDS_BEFORE_RELOGIN_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|HADOOP_KERBEROS_MIN_SECONDS_BEFORE_RELOGIN_DEFAULT
init|=
literal|60
decl_stmt|;
block|}
end_class

end_unit

