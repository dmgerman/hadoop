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
name|DFS_CLIENT_WRITE_PACKET_SIZE_DEFAULT
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
name|DFS_CLIENT_WRITE_PACKET_SIZE_KEY
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
name|HdfsServerConstants
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
DECL|field|conf
specifier|final
name|Configuration
name|conf
decl_stmt|;
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
DECL|field|initialBlockReportDelay
specifier|final
name|long
name|initialBlockReportDelay
decl_stmt|;
DECL|field|cacheReportInterval
specifier|final
name|long
name|cacheReportInterval
decl_stmt|;
DECL|field|dfsclientSlowIoWarningThresholdMs
specifier|final
name|long
name|dfsclientSlowIoWarningThresholdMs
decl_stmt|;
DECL|field|datanodeSlowIoWarningThresholdMs
specifier|final
name|long
name|datanodeSlowIoWarningThresholdMs
decl_stmt|;
DECL|field|writePacketSize
specifier|final
name|int
name|writePacketSize
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
DECL|method|DNConf (Configuration conf)
specifier|public
name|DNConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|socketTimeout
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFS_CLIENT_SOCKET_TIMEOUT_KEY
argument_list|,
name|HdfsServerConstants
operator|.
name|READ_TIMEOUT
argument_list|)
expr_stmt|;
name|socketWriteTimeout
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFS_DATANODE_SOCKET_WRITE_TIMEOUT_KEY
argument_list|,
name|HdfsServerConstants
operator|.
name|WRITE_TIMEOUT
argument_list|)
expr_stmt|;
name|socketKeepaliveTimeout
operator|=
name|conf
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
comment|/* Based on results on different platforms, we might need set the default       * to false on some of them. */
name|transferToAllowed
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|DFS_DATANODE_TRANSFERTO_ALLOWED_KEY
argument_list|,
name|DFS_DATANODE_TRANSFERTO_ALLOWED_DEFAULT
argument_list|)
expr_stmt|;
name|writePacketSize
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFS_CLIENT_WRITE_PACKET_SIZE_KEY
argument_list|,
name|DFS_CLIENT_WRITE_PACKET_SIZE_DEFAULT
argument_list|)
expr_stmt|;
name|readaheadLength
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_READAHEAD_BYTES_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_READAHEAD_BYTES_DEFAULT
argument_list|)
expr_stmt|;
name|dropCacheBehindWrites
operator|=
name|conf
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
name|conf
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
name|conf
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
name|conf
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
name|conf
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
name|conf
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
name|blockReportSplitThreshold
operator|=
name|conf
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
name|conf
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
name|dfsclientSlowIoWarningThresholdMs
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_SLOW_IO_WARNING_THRESHOLD_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_SLOW_IO_WARNING_THRESHOLD_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|datanodeSlowIoWarningThresholdMs
operator|=
name|conf
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
name|conf
operator|.
name|getLong
argument_list|(
name|DFS_BLOCKREPORT_INITIAL_DELAY_KEY
argument_list|,
name|DFS_BLOCKREPORT_INITIAL_DELAY_DEFAULT
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
literal|"dfs.blockreport.initialDelay is greater than "
operator|+
literal|"dfs.blockreport.intervalMsec."
operator|+
literal|" Setting initial delay to 0 msec:"
argument_list|)
expr_stmt|;
block|}
name|initialBlockReportDelay
operator|=
name|initBRDelay
expr_stmt|;
name|heartBeatInterval
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
name|DFS_HEARTBEAT_INTERVAL_DEFAULT
argument_list|)
operator|*
literal|1000L
expr_stmt|;
comment|// do we need to sync block file contents to disk when blockfile is closed?
name|this
operator|.
name|syncOnClose
operator|=
name|conf
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
name|conf
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
name|conf
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
name|conf
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
name|conf
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
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|ignoreSecurePortsForTesting
operator|=
name|conf
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
name|conf
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
name|conf
operator|.
name|getLong
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
name|conf
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
name|conf
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
block|}
end_class

end_unit

