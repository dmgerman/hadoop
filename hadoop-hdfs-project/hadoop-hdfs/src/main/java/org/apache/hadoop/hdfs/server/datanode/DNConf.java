begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INITIAL_DELAY_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INITIAL_DELAY_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INTERVAL_MSEC_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INTERVAL_MSEC_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_SPLIT_THRESHOLD_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_SPLIT_THRESHOLD_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_CACHEREPORT_INTERVAL_MSEC_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_CACHEREPORT_INTERVAL_MSEC_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_LIFELINE_INTERVAL_SECONDS_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_NON_LOCAL_LAZY_PERSIST
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_NON_LOCAL_LAZY_PERSIST_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_OUTLIERS_REPORT_INTERVAL_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_OUTLIERS_REPORT_INTERVAL_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_SOCKET_TIMEOUT_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MAX_LOCKED_MEMORY_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MAX_LOCKED_MEMORY_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SOCKET_WRITE_TIMEOUT_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SYNCONCLOSE_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SYNCONCLOSE_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_TRANSFERTO_ALLOWED_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_TRANSFERTO_ALLOWED_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_XCEIVER_STOP_TIMEOUT_MILLIS_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_XCEIVER_STOP_TIMEOUT_MILLIS_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MIN_SUPPORTED_NAMENODE_VERSION_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MIN_SUPPORTED_NAMENODE_VERSION_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_ENCRYPT_DATA_TRANSFER_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_ENCRYPT_DATA_TRANSFER_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATA_ENCRYPTION_ALGORITHM_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_RESTART_REPLICA_EXPIRY_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_RESTART_REPLICA_EXPIRY_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|IGNORE_SECURE_PORTS_FOR_TESTING_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|IGNORE_SECURE_PORTS_FOR_TESTING_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_BP_READY_TIMEOUT_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_BP_READY_TIMEOUT_DEFAULT
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
name|Configurable
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
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
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
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
name|hdfs
operator|.
name|protocol
operator|.
name|datatransfer
operator|.
name|TrustedChannelResolver
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
name|hdfs
operator|.
name|protocol
operator|.
name|datatransfer
operator|.
name|sasl
operator|.
name|DataTransferSaslUtil
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|Util
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
name|security
operator|.
name|SaslPropertiesResolver
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Simple class encapsulating all of the configuration that the DataNode  * loads at startup time.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DNConf
specifier|public
class|class
name|DNConf
block|{
DECL|field|socketTimeout
specifier|final
name|int
name|socketTimeout
decl_stmt|;
DECL|field|socketWriteTimeout
specifier|final
name|int
name|socketWriteTimeout
decl_stmt|;
DECL|field|socketKeepaliveTimeout
specifier|final
name|int
name|socketKeepaliveTimeout
decl_stmt|;
DECL|field|transferSocketSendBufferSize
specifier|private
specifier|final
name|int
name|transferSocketSendBufferSize
decl_stmt|;
DECL|field|transferSocketRecvBufferSize
specifier|private
specifier|final
name|int
name|transferSocketRecvBufferSize
decl_stmt|;
DECL|field|tcpNoDelay
specifier|private
specifier|final
name|boolean
name|tcpNoDelay
decl_stmt|;
DECL|field|transferToAllowed
specifier|final
name|boolean
name|transferToAllowed
decl_stmt|;
DECL|field|dropCacheBehindWrites
specifier|final
name|boolean
name|dropCacheBehindWrites
decl_stmt|;
DECL|field|syncBehindWrites
specifier|final
name|boolean
name|syncBehindWrites
decl_stmt|;
DECL|field|syncBehindWritesInBackground
specifier|final
name|boolean
name|syncBehindWritesInBackground
decl_stmt|;
DECL|field|dropCacheBehindReads
specifier|final
name|boolean
name|dropCacheBehindReads
decl_stmt|;
DECL|field|syncOnClose
specifier|final
name|boolean
name|syncOnClose
decl_stmt|;
DECL|field|encryptDataTransfer
specifier|final
name|boolean
name|encryptDataTransfer
decl_stmt|;
DECL|field|connectToDnViaHostname
specifier|final
name|boolean
name|connectToDnViaHostname
decl_stmt|;
DECL|field|readaheadLength
specifier|final
name|long
name|readaheadLength
decl_stmt|;
DECL|field|heartBeatInterval
specifier|final
name|long
name|heartBeatInterval
decl_stmt|;
DECL|field|lifelineIntervalMs
specifier|private
specifier|final
name|long
name|lifelineIntervalMs
decl_stmt|;
DECL|field|blockReportInterval
specifier|final
name|long
name|blockReportInterval
decl_stmt|;
DECL|field|blockReportSplitThreshold
specifier|final
name|long
name|blockReportSplitThreshold
decl_stmt|;
DECL|field|peerStatsEnabled
specifier|final
name|boolean
name|peerStatsEnabled
decl_stmt|;
DECL|field|diskStatsEnabled
specifier|final
name|boolean
name|diskStatsEnabled
decl_stmt|;
DECL|field|outliersReportIntervalMs
specifier|final
name|long
name|outliersReportIntervalMs
decl_stmt|;
DECL|field|ibrInterval
specifier|final
name|long
name|ibrInterval
decl_stmt|;
DECL|field|initialBlockReportDelayMs
specifier|final
name|long
name|initialBlockReportDelayMs
decl_stmt|;
DECL|field|cacheReportInterval
specifier|final
name|long
name|cacheReportInterval
decl_stmt|;
DECL|field|datanodeSlowIoWarningThresholdMs
specifier|final
name|long
name|datanodeSlowIoWarningThresholdMs
decl_stmt|;
DECL|field|minimumNameNodeVersion
specifier|final
name|String
name|minimumNameNodeVersion
decl_stmt|;
DECL|field|encryptionAlgorithm
specifier|final
name|String
name|encryptionAlgorithm
decl_stmt|;
DECL|field|saslPropsResolver
specifier|final
name|SaslPropertiesResolver
name|saslPropsResolver
decl_stmt|;
DECL|field|trustedChannelResolver
specifier|final
name|TrustedChannelResolver
name|trustedChannelResolver
decl_stmt|;
DECL|field|ignoreSecurePortsForTesting
specifier|private
specifier|final
name|boolean
name|ignoreSecurePortsForTesting
decl_stmt|;
DECL|field|xceiverStopTimeout
specifier|final
name|long
name|xceiverStopTimeout
decl_stmt|;
DECL|field|restartReplicaExpiry
specifier|final
name|long
name|restartReplicaExpiry
decl_stmt|;
DECL|field|maxLockedMemory
specifier|final
name|long
name|maxLockedMemory
decl_stmt|;
DECL|field|bpReadyTimeout
specifier|private
specifier|final
name|long
name|bpReadyTimeout
decl_stmt|;
comment|// Allow LAZY_PERSIST writes from non-local clients?
DECL|field|allowNonLocalLazyPersist
specifier|private
specifier|final
name|boolean
name|allowNonLocalLazyPersist
decl_stmt|;
DECL|field|volFailuresTolerated
specifier|private
specifier|final
name|int
name|volFailuresTolerated
decl_stmt|;
DECL|field|volsConfigured
specifier|private
specifier|final
name|int
name|volsConfigured
decl_stmt|;
DECL|field|maxDataLength
specifier|private
specifier|final
name|int
name|maxDataLength
decl_stmt|;
DECL|field|dn
specifier|private
name|Configurable
name|dn
decl_stmt|;
DECL|method|DNConf (final Configurable dn)
specifier|public
name|DNConf
parameter_list|(
specifier|final
name|Configurable
name|dn
parameter_list|)
block|{
name|this
operator|.
name|dn
operator|=
name|dn
expr_stmt|;
name|socketTimeout
operator|=
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|DFS_CLIENT_SOCKET_TIMEOUT_KEY
argument_list|,
name|HdfsConstants
operator|.
name|READ_TIMEOUT
argument_list|)
expr_stmt|;
name|socketWriteTimeout
operator|=
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|DFS_DATANODE_SOCKET_WRITE_TIMEOUT_KEY
argument_list|,
name|HdfsConstants
operator|.
name|WRITE_TIMEOUT
argument_list|)
expr_stmt|;
name|socketKeepaliveTimeout
operator|=
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SOCKET_REUSE_KEEPALIVE_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SOCKET_REUSE_KEEPALIVE_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|transferSocketSendBufferSize
operator|=
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_TRANSFER_SOCKET_SEND_BUFFER_SIZE_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_TRANSFER_SOCKET_SEND_BUFFER_SIZE_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|transferSocketRecvBufferSize
operator|=
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_TRANSFER_SOCKET_RECV_BUFFER_SIZE_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_TRANSFER_SOCKET_RECV_BUFFER_SIZE_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|tcpNoDelay
operator|=
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATA_TRANSFER_SERVER_TCPNODELAY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATA_TRANSFER_SERVER_TCPNODELAY_DEFAULT
argument_list|)
expr_stmt|;
comment|/* Based on results on different platforms, we might need set the default      * to false on some of them. */
name|transferToAllowed
operator|=
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|DFS_DATANODE_TRANSFERTO_ALLOWED_KEY
argument_list|,
name|DFS_DATANODE_TRANSFERTO_ALLOWED_DEFAULT
argument_list|)
expr_stmt|;
name|readaheadLength
operator|=
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_DATANODE_READAHEAD_BYTES_KEY
argument_list|,
name|HdfsClientConfigKeys
operator|.
name|DFS_DATANODE_READAHEAD_BYTES_DEFAULT
argument_list|)
expr_stmt|;
name|maxDataLength
operator|=
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|IPC_MAXIMUM_DATA_LENGTH
argument_list|,
name|DFSConfigKeys
operator|.
name|IPC_MAXIMUM_DATA_LENGTH_DEFAULT
argument_list|)
expr_stmt|;
name|dropCacheBehindWrites
operator|=
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DROP_CACHE_BEHIND_WRITES_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DROP_CACHE_BEHIND_WRITES_DEFAULT
argument_list|)
expr_stmt|;
name|syncBehindWrites
operator|=
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SYNC_BEHIND_WRITES_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SYNC_BEHIND_WRITES_DEFAULT
argument_list|)
expr_stmt|;
name|syncBehindWritesInBackground
operator|=
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SYNC_BEHIND_WRITES_IN_BACKGROUND_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SYNC_BEHIND_WRITES_IN_BACKGROUND_DEFAULT
argument_list|)
expr_stmt|;
name|dropCacheBehindReads
operator|=
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DROP_CACHE_BEHIND_READS_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DROP_CACHE_BEHIND_READS_DEFAULT
argument_list|)
expr_stmt|;
name|connectToDnViaHostname
operator|=
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_USE_DN_HOSTNAME
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_USE_DN_HOSTNAME_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockReportInterval
operator|=
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|DFS_BLOCKREPORT_INTERVAL_MSEC_KEY
argument_list|,
name|DFS_BLOCKREPORT_INTERVAL_MSEC_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|peerStatsEnabled
operator|=
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_PEER_STATS_ENABLED_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_PEER_STATS_ENABLED_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|diskStatsEnabled
operator|=
name|Util
operator|.
name|isDiskStatsEnabled
argument_list|(
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FILEIO_PROFILING_SAMPLING_PERCENTAGE_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FILEIO_PROFILING_SAMPLING_PERCENTAGE_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|outliersReportIntervalMs
operator|=
name|getConf
argument_list|()
operator|.
name|getTimeDuration
argument_list|(
name|DFS_DATANODE_OUTLIERS_REPORT_INTERVAL_KEY
argument_list|,
name|DFS_DATANODE_OUTLIERS_REPORT_INTERVAL_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|this
operator|.
name|ibrInterval
operator|=
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INCREMENTAL_INTERVAL_MSEC_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INCREMENTAL_INTERVAL_MSEC_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockReportSplitThreshold
operator|=
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|DFS_BLOCKREPORT_SPLIT_THRESHOLD_KEY
argument_list|,
name|DFS_BLOCKREPORT_SPLIT_THRESHOLD_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|cacheReportInterval
operator|=
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|DFS_CACHEREPORT_INTERVAL_MSEC_KEY
argument_list|,
name|DFS_CACHEREPORT_INTERVAL_MSEC_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|datanodeSlowIoWarningThresholdMs
operator|=
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SLOW_IO_WARNING_THRESHOLD_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SLOW_IO_WARNING_THRESHOLD_DEFAULT
argument_list|)
expr_stmt|;
name|long
name|initBRDelay
init|=
name|getConf
argument_list|()
operator|.
name|getTimeDuration
argument_list|(
name|DFS_BLOCKREPORT_INITIAL_DELAY_KEY
argument_list|,
name|DFS_BLOCKREPORT_INITIAL_DELAY_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
operator|*
literal|1000L
decl_stmt|;
if|if
condition|(
name|initBRDelay
operator|>=
name|blockReportInterval
condition|)
block|{
name|initBRDelay
operator|=
literal|0
expr_stmt|;
name|DataNode
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"dfs.blockreport.initialDelay is "
operator|+
literal|"greater than or equal to"
operator|+
literal|"dfs.blockreport.intervalMsec."
operator|+
literal|" Setting initial delay to 0 msec:"
argument_list|)
expr_stmt|;
block|}
name|initialBlockReportDelayMs
operator|=
name|initBRDelay
expr_stmt|;
name|heartBeatInterval
operator|=
name|getConf
argument_list|()
operator|.
name|getTimeDuration
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
name|DFS_HEARTBEAT_INTERVAL_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
operator|*
literal|1000L
expr_stmt|;
name|long
name|confLifelineIntervalMs
init|=
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|DFS_DATANODE_LIFELINE_INTERVAL_SECONDS_KEY
argument_list|,
literal|3
operator|*
name|getConf
argument_list|()
operator|.
name|getTimeDuration
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
name|DFS_HEARTBEAT_INTERVAL_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
operator|*
literal|1000L
decl_stmt|;
if|if
condition|(
name|confLifelineIntervalMs
operator|<=
name|heartBeatInterval
condition|)
block|{
name|confLifelineIntervalMs
operator|=
literal|3
operator|*
name|heartBeatInterval
expr_stmt|;
name|DataNode
operator|.
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s must be set to a value greater than %s.  "
operator|+
literal|"Resetting value to 3 * %s, which is %d milliseconds."
argument_list|,
name|DFS_DATANODE_LIFELINE_INTERVAL_SECONDS_KEY
argument_list|,
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
name|confLifelineIntervalMs
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|lifelineIntervalMs
operator|=
name|confLifelineIntervalMs
expr_stmt|;
comment|// do we need to sync block file contents to disk when blockfile is closed?
name|this
operator|.
name|syncOnClose
operator|=
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|DFS_DATANODE_SYNCONCLOSE_KEY
argument_list|,
name|DFS_DATANODE_SYNCONCLOSE_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|minimumNameNodeVersion
operator|=
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|DFS_DATANODE_MIN_SUPPORTED_NAMENODE_VERSION_KEY
argument_list|,
name|DFS_DATANODE_MIN_SUPPORTED_NAMENODE_VERSION_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|encryptDataTransfer
operator|=
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|DFS_ENCRYPT_DATA_TRANSFER_KEY
argument_list|,
name|DFS_ENCRYPT_DATA_TRANSFER_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|encryptionAlgorithm
operator|=
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|DFS_DATA_ENCRYPTION_ALGORITHM_KEY
argument_list|)
expr_stmt|;
name|this
operator|.
name|trustedChannelResolver
operator|=
name|TrustedChannelResolver
operator|.
name|getInstance
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|saslPropsResolver
operator|=
name|DataTransferSaslUtil
operator|.
name|getSaslPropertiesResolver
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|ignoreSecurePortsForTesting
operator|=
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|IGNORE_SECURE_PORTS_FOR_TESTING_KEY
argument_list|,
name|IGNORE_SECURE_PORTS_FOR_TESTING_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|xceiverStopTimeout
operator|=
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|DFS_DATANODE_XCEIVER_STOP_TIMEOUT_MILLIS_KEY
argument_list|,
name|DFS_DATANODE_XCEIVER_STOP_TIMEOUT_MILLIS_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxLockedMemory
operator|=
name|getConf
argument_list|()
operator|.
name|getLongBytes
argument_list|(
name|DFS_DATANODE_MAX_LOCKED_MEMORY_KEY
argument_list|,
name|DFS_DATANODE_MAX_LOCKED_MEMORY_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|restartReplicaExpiry
operator|=
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|DFS_DATANODE_RESTART_REPLICA_EXPIRY_KEY
argument_list|,
name|DFS_DATANODE_RESTART_REPLICA_EXPIRY_DEFAULT
argument_list|)
operator|*
literal|1000L
expr_stmt|;
name|this
operator|.
name|allowNonLocalLazyPersist
operator|=
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|DFS_DATANODE_NON_LOCAL_LAZY_PERSIST
argument_list|,
name|DFS_DATANODE_NON_LOCAL_LAZY_PERSIST_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|bpReadyTimeout
operator|=
name|getConf
argument_list|()
operator|.
name|getTimeDuration
argument_list|(
name|DFS_DATANODE_BP_READY_TIMEOUT_KEY
argument_list|,
name|DFS_DATANODE_BP_READY_TIMEOUT_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|this
operator|.
name|volFailuresTolerated
operator|=
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FAILED_VOLUMES_TOLERATED_DEFAULT
argument_list|)
expr_stmt|;
name|String
index|[]
name|dataDirs
init|=
name|getConf
argument_list|()
operator|.
name|getTrimmedStrings
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|)
decl_stmt|;
name|this
operator|.
name|volsConfigured
operator|=
operator|(
name|dataDirs
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|dataDirs
operator|.
name|length
expr_stmt|;
block|}
comment|// We get minimumNameNodeVersion via a method so it can be mocked out in tests.
DECL|method|getMinimumNameNodeVersion ()
name|String
name|getMinimumNameNodeVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|minimumNameNodeVersion
return|;
block|}
comment|/**    * Returns the configuration.    *     * @return Configuration the configuration    */
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|this
operator|.
name|dn
operator|.
name|getConf
argument_list|()
return|;
block|}
comment|/**    * Returns true if encryption enabled for DataTransferProtocol.    *    * @return boolean true if encryption enabled for DataTransferProtocol    */
DECL|method|getEncryptDataTransfer ()
specifier|public
name|boolean
name|getEncryptDataTransfer
parameter_list|()
block|{
return|return
name|encryptDataTransfer
return|;
block|}
comment|/**    * Returns encryption algorithm configured for DataTransferProtocol, or null    * if not configured.    *    * @return encryption algorithm configured for DataTransferProtocol    */
DECL|method|getEncryptionAlgorithm ()
specifier|public
name|String
name|getEncryptionAlgorithm
parameter_list|()
block|{
return|return
name|encryptionAlgorithm
return|;
block|}
DECL|method|getXceiverStopTimeout ()
specifier|public
name|long
name|getXceiverStopTimeout
parameter_list|()
block|{
return|return
name|xceiverStopTimeout
return|;
block|}
DECL|method|getMaxLockedMemory ()
specifier|public
name|long
name|getMaxLockedMemory
parameter_list|()
block|{
return|return
name|maxLockedMemory
return|;
block|}
comment|/**    * Returns true if connect to datanode via hostname    *     * @return boolean true if connect to datanode via hostname    */
DECL|method|getConnectToDnViaHostname ()
specifier|public
name|boolean
name|getConnectToDnViaHostname
parameter_list|()
block|{
return|return
name|connectToDnViaHostname
return|;
block|}
comment|/**    * Returns socket timeout    *     * @return int socket timeout    */
DECL|method|getSocketTimeout ()
specifier|public
name|int
name|getSocketTimeout
parameter_list|()
block|{
return|return
name|socketTimeout
return|;
block|}
comment|/**    * Returns socket write timeout    *     * @return int socket write timeout    */
DECL|method|getSocketWriteTimeout ()
specifier|public
name|int
name|getSocketWriteTimeout
parameter_list|()
block|{
return|return
name|socketWriteTimeout
return|;
block|}
comment|/**    * Returns the SaslPropertiesResolver configured for use with    * DataTransferProtocol, or null if not configured.    *    * @return SaslPropertiesResolver configured for use with DataTransferProtocol    */
DECL|method|getSaslPropsResolver ()
specifier|public
name|SaslPropertiesResolver
name|getSaslPropsResolver
parameter_list|()
block|{
return|return
name|saslPropsResolver
return|;
block|}
comment|/**    * Returns the TrustedChannelResolver configured for use with    * DataTransferProtocol, or null if not configured.    *    * @return TrustedChannelResolver configured for use with DataTransferProtocol    */
DECL|method|getTrustedChannelResolver ()
specifier|public
name|TrustedChannelResolver
name|getTrustedChannelResolver
parameter_list|()
block|{
return|return
name|trustedChannelResolver
return|;
block|}
comment|/**    * Returns true if configuration is set to skip checking for proper    * port configuration in a secured cluster.  This is only intended for use in    * dev testing.    *    * @return true if configured to skip checking secured port configuration    */
DECL|method|getIgnoreSecurePortsForTesting ()
specifier|public
name|boolean
name|getIgnoreSecurePortsForTesting
parameter_list|()
block|{
return|return
name|ignoreSecurePortsForTesting
return|;
block|}
DECL|method|getAllowNonLocalLazyPersist ()
specifier|public
name|boolean
name|getAllowNonLocalLazyPersist
parameter_list|()
block|{
return|return
name|allowNonLocalLazyPersist
return|;
block|}
DECL|method|getTransferSocketRecvBufferSize ()
specifier|public
name|int
name|getTransferSocketRecvBufferSize
parameter_list|()
block|{
return|return
name|transferSocketRecvBufferSize
return|;
block|}
DECL|method|getTransferSocketSendBufferSize ()
specifier|public
name|int
name|getTransferSocketSendBufferSize
parameter_list|()
block|{
return|return
name|transferSocketSendBufferSize
return|;
block|}
DECL|method|getDataTransferServerTcpNoDelay ()
specifier|public
name|boolean
name|getDataTransferServerTcpNoDelay
parameter_list|()
block|{
return|return
name|tcpNoDelay
return|;
block|}
DECL|method|getBpReadyTimeout ()
specifier|public
name|long
name|getBpReadyTimeout
parameter_list|()
block|{
return|return
name|bpReadyTimeout
return|;
block|}
comment|/**    * Returns the interval in milliseconds between sending lifeline messages.    *    * @return interval in milliseconds between sending lifeline messages    */
DECL|method|getLifelineIntervalMs ()
specifier|public
name|long
name|getLifelineIntervalMs
parameter_list|()
block|{
return|return
name|lifelineIntervalMs
return|;
block|}
DECL|method|getVolFailuresTolerated ()
specifier|public
name|int
name|getVolFailuresTolerated
parameter_list|()
block|{
return|return
name|volFailuresTolerated
return|;
block|}
DECL|method|getVolsConfigured ()
specifier|public
name|int
name|getVolsConfigured
parameter_list|()
block|{
return|return
name|volsConfigured
return|;
block|}
DECL|method|getSlowIoWarningThresholdMs ()
specifier|public
name|long
name|getSlowIoWarningThresholdMs
parameter_list|()
block|{
return|return
name|datanodeSlowIoWarningThresholdMs
return|;
block|}
DECL|method|getMaxDataLength ()
name|int
name|getMaxDataLength
parameter_list|()
block|{
return|return
name|maxDataLength
return|;
block|}
block|}
end_class

end_unit

