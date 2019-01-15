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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|http
operator|.
name|lib
operator|.
name|StaticUserWebFilter
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
name|net
operator|.
name|DomainNameResolver
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
name|net
operator|.
name|DNSDomainNameResolver
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
comment|/** Timeout value for RPC client on waiting for response. */
DECL|field|IPC_CLIENT_RPC_TIMEOUT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_RPC_TIMEOUT_KEY
init|=
literal|"ipc.client.rpc-timeout.ms"
decl_stmt|;
comment|/** Default value for IPC_CLIENT_RPC_TIMEOUT_KEY. */
DECL|field|IPC_CLIENT_RPC_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_CLIENT_RPC_TIMEOUT_DEFAULT
init|=
literal|0
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
comment|/** Number of pending connections that may be queued per socket reader */
DECL|field|IPC_SERVER_RPC_READ_CONNECTION_QUEUE_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_SERVER_RPC_READ_CONNECTION_QUEUE_SIZE_KEY
init|=
literal|"ipc.server.read.connection-queue.size"
decl_stmt|;
comment|/** Default value for IPC_SERVER_RPC_READ_CONNECTION_QUEUE_SIZE */
DECL|field|IPC_SERVER_RPC_READ_CONNECTION_QUEUE_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_SERVER_RPC_READ_CONNECTION_QUEUE_SIZE_DEFAULT
init|=
literal|100
decl_stmt|;
comment|/** Max request size a server will accept. */
DECL|field|IPC_MAXIMUM_DATA_LENGTH
specifier|public
specifier|static
specifier|final
name|String
name|IPC_MAXIMUM_DATA_LENGTH
init|=
literal|"ipc.maximum.data.length"
decl_stmt|;
comment|/** Default value for IPC_MAXIMUM_DATA_LENGTH. */
DECL|field|IPC_MAXIMUM_DATA_LENGTH_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_MAXIMUM_DATA_LENGTH_DEFAULT
init|=
literal|64
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|/** Max response size a client will accept. */
DECL|field|IPC_MAXIMUM_RESPONSE_LENGTH
specifier|public
specifier|static
specifier|final
name|String
name|IPC_MAXIMUM_RESPONSE_LENGTH
init|=
literal|"ipc.maximum.response.length"
decl_stmt|;
comment|/** Default value for IPC_MAXIMUM_RESPONSE_LENGTH. */
DECL|field|IPC_MAXIMUM_RESPONSE_LENGTH_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_MAXIMUM_RESPONSE_LENGTH_DEFAULT
init|=
literal|128
operator|*
literal|1024
operator|*
literal|1024
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
comment|/**    * CallQueue related settings. These are not used directly, but rather    * combined with a namespace and port. For instance:    * IPC_NAMESPACE + ".8020." + IPC_CALLQUEUE_IMPL_KEY    */
DECL|field|IPC_NAMESPACE
specifier|public
specifier|static
specifier|final
name|String
name|IPC_NAMESPACE
init|=
literal|"ipc"
decl_stmt|;
DECL|field|IPC_CALLQUEUE_IMPL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CALLQUEUE_IMPL_KEY
init|=
literal|"callqueue.impl"
decl_stmt|;
DECL|field|IPC_SCHEDULER_IMPL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_SCHEDULER_IMPL_KEY
init|=
literal|"scheduler.impl"
decl_stmt|;
DECL|field|IPC_IDENTITY_PROVIDER_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_IDENTITY_PROVIDER_KEY
init|=
literal|"identity-provider.impl"
decl_stmt|;
DECL|field|IPC_BACKOFF_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|IPC_BACKOFF_ENABLE
init|=
literal|"backoff.enable"
decl_stmt|;
DECL|field|IPC_BACKOFF_ENABLE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|IPC_BACKOFF_ENABLE_DEFAULT
init|=
literal|false
decl_stmt|;
comment|/**    * IPC scheduler priority levels.    */
DECL|field|IPC_SCHEDULER_PRIORITY_LEVELS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_SCHEDULER_PRIORITY_LEVELS_KEY
init|=
literal|"scheduler.priority.levels"
decl_stmt|;
DECL|field|IPC_SCHEDULER_PRIORITY_LEVELS_DEFAULT_KEY
specifier|public
specifier|static
specifier|final
name|int
name|IPC_SCHEDULER_PRIORITY_LEVELS_DEFAULT_KEY
init|=
literal|4
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
comment|/**    * Supported compression codec classes    */
DECL|field|IO_COMPRESSION_CODECS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_COMPRESSION_CODECS_KEY
init|=
literal|"io.compression.codecs"
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
comment|/** ZStandard compression level. */
DECL|field|IO_COMPRESSION_CODEC_ZSTD_LEVEL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_COMPRESSION_CODEC_ZSTD_LEVEL_KEY
init|=
literal|"io.compression.codec.zstd.level"
decl_stmt|;
comment|/** Default value for IO_COMPRESSION_CODEC_ZSTD_LEVEL_KEY. */
DECL|field|IO_COMPRESSION_CODEC_ZSTD_LEVEL_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IO_COMPRESSION_CODEC_ZSTD_LEVEL_DEFAULT
init|=
literal|3
decl_stmt|;
comment|/** ZStandard buffer size. */
DECL|field|IO_COMPRESSION_CODEC_ZSTD_BUFFER_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_COMPRESSION_CODEC_ZSTD_BUFFER_SIZE_KEY
init|=
literal|"io.compression.codec.zstd.buffersize"
decl_stmt|;
comment|/** ZStandard buffer size a value of 0 means use the recommended zstd    * buffer size that the library recommends. */
specifier|public
specifier|static
specifier|final
name|int
DECL|field|IO_COMPRESSION_CODEC_ZSTD_BUFFER_SIZE_DEFAULT
name|IO_COMPRESSION_CODEC_ZSTD_BUFFER_SIZE_DEFAULT
init|=
literal|0
decl_stmt|;
comment|/** Internal buffer size for Lz4 compressor/decompressors */
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
comment|/** Use lz4hc(slow but with high compression ratio) for lz4 compression */
DECL|field|IO_COMPRESSION_CODEC_LZ4_USELZ4HC_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_COMPRESSION_CODEC_LZ4_USELZ4HC_KEY
init|=
literal|"io.compression.codec.lz4.use.lz4hc"
decl_stmt|;
comment|/** Default value for IO_COMPRESSION_CODEC_USELZ4HC_KEY */
DECL|field|IO_COMPRESSION_CODEC_LZ4_USELZ4HC_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|IO_COMPRESSION_CODEC_LZ4_USELZ4HC_DEFAULT
init|=
literal|false
decl_stmt|;
comment|/**    * Service Authorization    */
specifier|public
specifier|static
specifier|final
name|String
DECL|field|HADOOP_SECURITY_SERVICE_AUTHORIZATION_DEFAULT_ACL
name|HADOOP_SECURITY_SERVICE_AUTHORIZATION_DEFAULT_ACL
init|=
literal|"security.service.authorization.default.acl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|HADOOP_SECURITY_SERVICE_AUTHORIZATION_DEFAULT_BLOCKED_ACL
name|HADOOP_SECURITY_SERVICE_AUTHORIZATION_DEFAULT_BLOCKED_ACL
init|=
literal|"security.service.authorization.default.acl.blocked"
decl_stmt|;
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
specifier|public
specifier|static
specifier|final
name|String
DECL|field|HADOOP_SECURITY_SERVICE_AUTHORIZATION_REFRESH_CALLQUEUE
name|HADOOP_SECURITY_SERVICE_AUTHORIZATION_REFRESH_CALLQUEUE
init|=
literal|"security.refresh.callqueue.protocol.acl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|HADOOP_SECURITY_SERVICE_AUTHORIZATION_GENERIC_REFRESH
name|HADOOP_SECURITY_SERVICE_AUTHORIZATION_GENERIC_REFRESH
init|=
literal|"security.refresh.generic.protocol.acl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|HADOOP_SECURITY_SERVICE_AUTHORIZATION_TRACING
name|HADOOP_SECURITY_SERVICE_AUTHORIZATION_TRACING
init|=
literal|"security.trace.protocol.acl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|HADOOP_SECURITY_SERVICE_AUTHORIZATION_DATANODE_LIFELINE
name|HADOOP_SECURITY_SERVICE_AUTHORIZATION_DATANODE_LIFELINE
init|=
literal|"security.datanode.lifeline.protocol.acl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|HADOOP_SECURITY_SERVICE_AUTHORIZATION_RECONFIGURATION
name|HADOOP_SECURITY_SERVICE_AUTHORIZATION_RECONFIGURATION
init|=
literal|"security.reconfiguration.protocol.acl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|SECURITY_HA_SERVICE_PROTOCOL_ACL
name|SECURITY_HA_SERVICE_PROTOCOL_ACL
init|=
literal|"security.ha.service.protocol.acl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|SECURITY_ZKFC_PROTOCOL_ACL
name|SECURITY_ZKFC_PROTOCOL_ACL
init|=
literal|"security.zkfc.protocol.acl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|SECURITY_CLIENT_PROTOCOL_ACL
name|SECURITY_CLIENT_PROTOCOL_ACL
init|=
literal|"security.client.protocol.acl"
decl_stmt|;
DECL|field|SECURITY_CLIENT_DATANODE_PROTOCOL_ACL
specifier|public
specifier|static
specifier|final
name|String
name|SECURITY_CLIENT_DATANODE_PROTOCOL_ACL
init|=
literal|"security.client.datanode.protocol.acl"
decl_stmt|;
DECL|field|SECURITY_ROUTER_ADMIN_PROTOCOL_ACL
specifier|public
specifier|static
specifier|final
name|String
name|SECURITY_ROUTER_ADMIN_PROTOCOL_ACL
init|=
literal|"security.router.admin.protocol.acl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|SECURITY_DATANODE_PROTOCOL_ACL
name|SECURITY_DATANODE_PROTOCOL_ACL
init|=
literal|"security.datanode.protocol.acl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|SECURITY_INTER_DATANODE_PROTOCOL_ACL
name|SECURITY_INTER_DATANODE_PROTOCOL_ACL
init|=
literal|"security.inter.datanode.protocol.acl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|SECURITY_NAMENODE_PROTOCOL_ACL
name|SECURITY_NAMENODE_PROTOCOL_ACL
init|=
literal|"security.namenode.protocol.acl"
decl_stmt|;
DECL|field|SECURITY_QJOURNAL_SERVICE_PROTOCOL_ACL
specifier|public
specifier|static
specifier|final
name|String
name|SECURITY_QJOURNAL_SERVICE_PROTOCOL_ACL
init|=
literal|"security.qjournal.service.protocol.acl"
decl_stmt|;
DECL|field|SECURITY_INTERQJOURNAL_SERVICE_PROTOCOL_ACL
specifier|public
specifier|static
specifier|final
name|String
name|SECURITY_INTERQJOURNAL_SERVICE_PROTOCOL_ACL
init|=
literal|"security.interqjournal.service.protocol.acl"
decl_stmt|;
DECL|field|HADOOP_SECURITY_TOKEN_SERVICE_USE_IP
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_SECURITY_TOKEN_SERVICE_USE_IP
init|=
literal|"hadoop.security.token.service.use_ip"
decl_stmt|;
DECL|field|HADOOP_SECURITY_TOKEN_SERVICE_USE_IP_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|HADOOP_SECURITY_TOKEN_SERVICE_USE_IP_DEFAULT
init|=
literal|true
decl_stmt|;
comment|/**    * @see    *<a href="{@docRoot}/../hadoop-project-dist/hadoop-common/core-default.xml">    * core-default.xml</a>    */
DECL|field|HADOOP_SECURITY_DNS_LOG_SLOW_LOOKUPS_ENABLED_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_SECURITY_DNS_LOG_SLOW_LOOKUPS_ENABLED_KEY
init|=
literal|"hadoop.security.dns.log-slow-lookups.enabled"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
DECL|field|HADOOP_SECURITY_DNS_LOG_SLOW_LOOKUPS_ENABLED_DEFAULT
name|HADOOP_SECURITY_DNS_LOG_SLOW_LOOKUPS_ENABLED_DEFAULT
init|=
literal|false
decl_stmt|;
comment|/**    * @see    *<a href="{@docRoot}/../hadoop-project-dist/hadoop-common/core-default.xml">    * core-default.xml</a>    */
specifier|public
specifier|static
specifier|final
name|String
DECL|field|HADOOP_SECURITY_DNS_LOG_SLOW_LOOKUPS_THRESHOLD_MS_KEY
name|HADOOP_SECURITY_DNS_LOG_SLOW_LOOKUPS_THRESHOLD_MS_KEY
init|=
literal|"hadoop.security.dns.log-slow-lookups.threshold.ms"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
DECL|field|HADOOP_SECURITY_DNS_LOG_SLOW_LOOKUPS_THRESHOLD_MS_DEFAULT
name|HADOOP_SECURITY_DNS_LOG_SLOW_LOOKUPS_THRESHOLD_MS_DEFAULT
init|=
literal|1000
decl_stmt|;
comment|/**    * HA health monitor and failover controller.    */
comment|/** How often to retry connecting to the service. */
DECL|field|HA_HM_CONNECT_RETRY_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HA_HM_CONNECT_RETRY_INTERVAL_KEY
init|=
literal|"ha.health-monitor.connect-retry-interval.ms"
decl_stmt|;
DECL|field|HA_HM_CONNECT_RETRY_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|HA_HM_CONNECT_RETRY_INTERVAL_DEFAULT
init|=
literal|1000
decl_stmt|;
comment|/* How often to check the service. */
DECL|field|HA_HM_CHECK_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HA_HM_CHECK_INTERVAL_KEY
init|=
literal|"ha.health-monitor.check-interval.ms"
decl_stmt|;
DECL|field|HA_HM_CHECK_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|HA_HM_CHECK_INTERVAL_DEFAULT
init|=
literal|1000
decl_stmt|;
comment|/* How long to sleep after an unexpected RPC error. */
DECL|field|HA_HM_SLEEP_AFTER_DISCONNECT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HA_HM_SLEEP_AFTER_DISCONNECT_KEY
init|=
literal|"ha.health-monitor.sleep-after-disconnect.ms"
decl_stmt|;
DECL|field|HA_HM_SLEEP_AFTER_DISCONNECT_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|HA_HM_SLEEP_AFTER_DISCONNECT_DEFAULT
init|=
literal|1000
decl_stmt|;
comment|/* Timeout for the actual monitorHealth() calls. */
DECL|field|HA_HM_RPC_TIMEOUT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HA_HM_RPC_TIMEOUT_KEY
init|=
literal|"ha.health-monitor.rpc-timeout.ms"
decl_stmt|;
DECL|field|HA_HM_RPC_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|HA_HM_RPC_TIMEOUT_DEFAULT
init|=
literal|45000
decl_stmt|;
comment|/* Timeout that the FC waits for the new active to become active */
DECL|field|HA_FC_NEW_ACTIVE_TIMEOUT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HA_FC_NEW_ACTIVE_TIMEOUT_KEY
init|=
literal|"ha.failover-controller.new-active.rpc-timeout.ms"
decl_stmt|;
DECL|field|HA_FC_NEW_ACTIVE_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|HA_FC_NEW_ACTIVE_TIMEOUT_DEFAULT
init|=
literal|60000
decl_stmt|;
comment|/* Timeout that the FC waits for the old active to go to standby */
DECL|field|HA_FC_GRACEFUL_FENCE_TIMEOUT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HA_FC_GRACEFUL_FENCE_TIMEOUT_KEY
init|=
literal|"ha.failover-controller.graceful-fence.rpc-timeout.ms"
decl_stmt|;
DECL|field|HA_FC_GRACEFUL_FENCE_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|HA_FC_GRACEFUL_FENCE_TIMEOUT_DEFAULT
init|=
literal|5000
decl_stmt|;
comment|/* FC connection retries for graceful fencing */
DECL|field|HA_FC_GRACEFUL_FENCE_CONNECTION_RETRIES
specifier|public
specifier|static
specifier|final
name|String
name|HA_FC_GRACEFUL_FENCE_CONNECTION_RETRIES
init|=
literal|"ha.failover-controller.graceful-fence.connection.retries"
decl_stmt|;
DECL|field|HA_FC_GRACEFUL_FENCE_CONNECTION_RETRIES_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|HA_FC_GRACEFUL_FENCE_CONNECTION_RETRIES_DEFAULT
init|=
literal|1
decl_stmt|;
comment|/** number of zookeeper operation retry times in ActiveStandbyElector */
DECL|field|HA_FC_ELECTOR_ZK_OP_RETRIES_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HA_FC_ELECTOR_ZK_OP_RETRIES_KEY
init|=
literal|"ha.failover-controller.active-standby-elector.zk.op.retries"
decl_stmt|;
DECL|field|HA_FC_ELECTOR_ZK_OP_RETRIES_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|HA_FC_ELECTOR_ZK_OP_RETRIES_DEFAULT
init|=
literal|3
decl_stmt|;
comment|/* Timeout that the CLI (manual) FC waits for monitorHealth, getServiceState */
DECL|field|HA_FC_CLI_CHECK_TIMEOUT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HA_FC_CLI_CHECK_TIMEOUT_KEY
init|=
literal|"ha.failover-controller.cli-check.rpc-timeout.ms"
decl_stmt|;
DECL|field|HA_FC_CLI_CHECK_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|HA_FC_CLI_CHECK_TIMEOUT_DEFAULT
init|=
literal|20000
decl_stmt|;
comment|/** Static user web-filter properties.    * See {@link StaticUserWebFilter}.    */
DECL|field|HADOOP_HTTP_STATIC_USER
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_HTTP_STATIC_USER
init|=
literal|"hadoop.http.staticuser.user"
decl_stmt|;
DECL|field|DEFAULT_HADOOP_HTTP_STATIC_USER
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_HADOOP_HTTP_STATIC_USER
init|=
literal|"dr.who"
decl_stmt|;
comment|/**    * User{@literal ->}groups static mapping to override the groups lookup    */
DECL|field|HADOOP_USER_GROUP_STATIC_OVERRIDES
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_USER_GROUP_STATIC_OVERRIDES
init|=
literal|"hadoop.user.group.static.mapping.overrides"
decl_stmt|;
DECL|field|HADOOP_USER_GROUP_STATIC_OVERRIDES_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_USER_GROUP_STATIC_OVERRIDES_DEFAULT
init|=
literal|"dr.who=;"
decl_stmt|;
comment|/** Enable/Disable aliases serving from jetty */
DECL|field|HADOOP_JETTY_LOGS_SERVE_ALIASES
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_JETTY_LOGS_SERVE_ALIASES
init|=
literal|"hadoop.jetty.logs.serve.aliases"
decl_stmt|;
DECL|field|DEFAULT_HADOOP_JETTY_LOGS_SERVE_ALIASES
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_HADOOP_JETTY_LOGS_SERVE_ALIASES
init|=
literal|true
decl_stmt|;
comment|/* Path to the Kerberos ticket cache.  Setting this will force    * UserGroupInformation to use only this ticket cache file when creating a    * FileSystem instance.    */
DECL|field|KERBEROS_TICKET_CACHE_PATH
specifier|public
specifier|static
specifier|final
name|String
name|KERBEROS_TICKET_CACHE_PATH
init|=
literal|"hadoop.security.kerberos.ticket.cache.path"
decl_stmt|;
DECL|field|HADOOP_SECURITY_UID_NAME_CACHE_TIMEOUT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_SECURITY_UID_NAME_CACHE_TIMEOUT_KEY
init|=
literal|"hadoop.security.uid.cache.secs"
decl_stmt|;
DECL|field|HADOOP_SECURITY_UID_NAME_CACHE_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|HADOOP_SECURITY_UID_NAME_CACHE_TIMEOUT_DEFAULT
init|=
literal|4
operator|*
literal|60
operator|*
literal|60
decl_stmt|;
comment|// 4 hours
DECL|field|IPC_CLIENT_ASYNC_CALLS_MAX_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_ASYNC_CALLS_MAX_KEY
init|=
literal|"ipc.client.async.calls.max"
decl_stmt|;
DECL|field|IPC_CLIENT_ASYNC_CALLS_MAX_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_CLIENT_ASYNC_CALLS_MAX_DEFAULT
init|=
literal|100
decl_stmt|;
DECL|field|IPC_CLIENT_FALLBACK_TO_SIMPLE_AUTH_ALLOWED_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_FALLBACK_TO_SIMPLE_AUTH_ALLOWED_KEY
init|=
literal|"ipc.client.fallback-to-simple-auth-allowed"
decl_stmt|;
DECL|field|IPC_CLIENT_FALLBACK_TO_SIMPLE_AUTH_ALLOWED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|IPC_CLIENT_FALLBACK_TO_SIMPLE_AUTH_ALLOWED_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|IPC_CLIENT_BIND_WILDCARD_ADDR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_BIND_WILDCARD_ADDR_KEY
init|=
literal|"ipc.client"
operator|+
literal|".bind.wildcard.addr"
decl_stmt|;
DECL|field|IPC_CLIENT_BIND_WILDCARD_ADDR_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|IPC_CLIENT_BIND_WILDCARD_ADDR_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SASL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SASL_KEY
init|=
literal|"ipc.client.connect.max.retries.on.sasl"
decl_stmt|;
DECL|field|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SASL_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SASL_DEFAULT
init|=
literal|5
decl_stmt|;
comment|/** How often the server scans for idle connections */
DECL|field|IPC_CLIENT_CONNECTION_IDLESCANINTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CLIENT_CONNECTION_IDLESCANINTERVAL_KEY
init|=
literal|"ipc.client.connection.idle-scan-interval.ms"
decl_stmt|;
comment|/** Default value for IPC_SERVER_CONNECTION_IDLE_SCAN_INTERVAL_KEY */
DECL|field|IPC_CLIENT_CONNECTION_IDLESCANINTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_CLIENT_CONNECTION_IDLESCANINTERVAL_DEFAULT
init|=
literal|10000
decl_stmt|;
DECL|field|HADOOP_USER_GROUP_METRICS_PERCENTILES_INTERVALS
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_USER_GROUP_METRICS_PERCENTILES_INTERVALS
init|=
literal|"hadoop.user.group.metrics.percentiles.intervals"
decl_stmt|;
DECL|field|RPC_METRICS_QUANTILE_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|RPC_METRICS_QUANTILE_ENABLE
init|=
literal|"rpc.metrics.quantile.enable"
decl_stmt|;
DECL|field|RPC_METRICS_QUANTILE_ENABLE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|RPC_METRICS_QUANTILE_ENABLE_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|RPC_METRICS_PERCENTILES_INTERVALS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|RPC_METRICS_PERCENTILES_INTERVALS_KEY
init|=
literal|"rpc.metrics.percentiles.intervals"
decl_stmt|;
comment|/** Allowed hosts for nfs exports */
DECL|field|NFS_EXPORTS_ALLOWED_HOSTS_SEPARATOR
specifier|public
specifier|static
specifier|final
name|String
name|NFS_EXPORTS_ALLOWED_HOSTS_SEPARATOR
init|=
literal|";"
decl_stmt|;
DECL|field|NFS_EXPORTS_ALLOWED_HOSTS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NFS_EXPORTS_ALLOWED_HOSTS_KEY
init|=
literal|"nfs.exports.allowed.hosts"
decl_stmt|;
DECL|field|NFS_EXPORTS_ALLOWED_HOSTS_KEY_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|NFS_EXPORTS_ALLOWED_HOSTS_KEY_DEFAULT
init|=
literal|"* rw"
decl_stmt|;
comment|// HDFS client HTrace configuration.
DECL|field|FS_CLIENT_HTRACE_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|FS_CLIENT_HTRACE_PREFIX
init|=
literal|"fs.client.htrace."
decl_stmt|;
comment|// Global ZooKeeper configuration keys
DECL|field|ZK_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|ZK_PREFIX
init|=
literal|"hadoop.zk."
decl_stmt|;
comment|/** ACL for the ZooKeeper ensemble. */
DECL|field|ZK_ACL
specifier|public
specifier|static
specifier|final
name|String
name|ZK_ACL
init|=
name|ZK_PREFIX
operator|+
literal|"acl"
decl_stmt|;
DECL|field|ZK_ACL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|ZK_ACL_DEFAULT
init|=
literal|"world:anyone:rwcda"
decl_stmt|;
comment|/** Authentication for the ZooKeeper ensemble. */
DECL|field|ZK_AUTH
specifier|public
specifier|static
specifier|final
name|String
name|ZK_AUTH
init|=
name|ZK_PREFIX
operator|+
literal|"auth"
decl_stmt|;
comment|/** Address of the ZooKeeper ensemble. */
DECL|field|ZK_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|ZK_ADDRESS
init|=
name|ZK_PREFIX
operator|+
literal|"address"
decl_stmt|;
comment|/** Maximum number of retries for a ZooKeeper operation. */
DECL|field|ZK_NUM_RETRIES
specifier|public
specifier|static
specifier|final
name|String
name|ZK_NUM_RETRIES
init|=
name|ZK_PREFIX
operator|+
literal|"num-retries"
decl_stmt|;
DECL|field|ZK_NUM_RETRIES_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|ZK_NUM_RETRIES_DEFAULT
init|=
literal|1000
decl_stmt|;
comment|/** Timeout for a ZooKeeper operation in ZooKeeper in milliseconds. */
DECL|field|ZK_TIMEOUT_MS
specifier|public
specifier|static
specifier|final
name|String
name|ZK_TIMEOUT_MS
init|=
name|ZK_PREFIX
operator|+
literal|"timeout-ms"
decl_stmt|;
DECL|field|ZK_TIMEOUT_MS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|ZK_TIMEOUT_MS_DEFAULT
init|=
literal|10000
decl_stmt|;
comment|/** How often to retry a ZooKeeper operation  in milliseconds. */
DECL|field|ZK_RETRY_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|ZK_RETRY_INTERVAL_MS
init|=
name|ZK_PREFIX
operator|+
literal|"retry-interval-ms"
decl_stmt|;
DECL|field|ZK_RETRY_INTERVAL_MS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|ZK_RETRY_INTERVAL_MS_DEFAULT
init|=
literal|1000
decl_stmt|;
comment|/** Default domain name resolver for hadoop to use. */
DECL|field|HADOOP_DOMAINNAME_RESOLVER_IMPL
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_DOMAINNAME_RESOLVER_IMPL
init|=
literal|"hadoop.domainname.resolver.impl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|DomainNameResolver
argument_list|>
DECL|field|HADOOP_DOMAINNAME_RESOLVER_IMPL_DEFAULT
name|HADOOP_DOMAINNAME_RESOLVER_IMPL_DEFAULT
init|=
name|DNSDomainNameResolver
operator|.
name|class
decl_stmt|;
block|}
end_class

end_unit

