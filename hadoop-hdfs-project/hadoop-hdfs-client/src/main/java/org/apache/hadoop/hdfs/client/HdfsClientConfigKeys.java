begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
package|;
end_package

begin_comment
comment|/** Client configuration properties */
end_comment

begin_interface
DECL|interface|HdfsClientConfigKeys
specifier|public
interface|interface
name|HdfsClientConfigKeys
block|{
DECL|field|SECOND
name|long
name|SECOND
init|=
literal|1000L
decl_stmt|;
DECL|field|MINUTE
name|long
name|MINUTE
init|=
literal|60
operator|*
name|SECOND
decl_stmt|;
DECL|field|DFS_BLOCK_SIZE_KEY
name|String
name|DFS_BLOCK_SIZE_KEY
init|=
literal|"dfs.blocksize"
decl_stmt|;
DECL|field|DFS_BLOCK_SIZE_DEFAULT
name|long
name|DFS_BLOCK_SIZE_DEFAULT
init|=
literal|128
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|DFS_REPLICATION_KEY
name|String
name|DFS_REPLICATION_KEY
init|=
literal|"dfs.replication"
decl_stmt|;
DECL|field|DFS_REPLICATION_DEFAULT
name|short
name|DFS_REPLICATION_DEFAULT
init|=
literal|3
decl_stmt|;
DECL|field|DFS_WEBHDFS_USER_PATTERN_KEY
name|String
name|DFS_WEBHDFS_USER_PATTERN_KEY
init|=
literal|"dfs.webhdfs.user.provider.user.pattern"
decl_stmt|;
DECL|field|DFS_WEBHDFS_USER_PATTERN_DEFAULT
name|String
name|DFS_WEBHDFS_USER_PATTERN_DEFAULT
init|=
literal|"^[A-Za-z_][A-Za-z0-9._-]*[$]?$"
decl_stmt|;
DECL|field|DFS_WEBHDFS_ACL_PERMISSION_PATTERN_DEFAULT
name|String
name|DFS_WEBHDFS_ACL_PERMISSION_PATTERN_DEFAULT
init|=
literal|"^(default:)?(user|group|mask|other):[[A-Za-z_][A-Za-z0-9._-]]*:([rwx-]{3})?(,(default:)?(user|group|mask|other):[[A-Za-z_][A-Za-z0-9._-]]*:([rwx-]{3})?)*$"
decl_stmt|;
DECL|field|PREFIX
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"dfs.client."
decl_stmt|;
DECL|field|DFS_NAMESERVICES
name|String
name|DFS_NAMESERVICES
init|=
literal|"dfs.nameservices"
decl_stmt|;
DECL|field|DFS_NAMENODE_HTTP_PORT_DEFAULT
name|int
name|DFS_NAMENODE_HTTP_PORT_DEFAULT
init|=
literal|50070
decl_stmt|;
DECL|field|DFS_NAMENODE_HTTP_ADDRESS_KEY
name|String
name|DFS_NAMENODE_HTTP_ADDRESS_KEY
init|=
literal|"dfs.namenode.http-address"
decl_stmt|;
DECL|field|DFS_NAMENODE_HTTPS_PORT_DEFAULT
name|int
name|DFS_NAMENODE_HTTPS_PORT_DEFAULT
init|=
literal|50470
decl_stmt|;
DECL|field|DFS_NAMENODE_HTTPS_ADDRESS_KEY
name|String
name|DFS_NAMENODE_HTTPS_ADDRESS_KEY
init|=
literal|"dfs.namenode.https-address"
decl_stmt|;
DECL|field|DFS_HA_NAMENODES_KEY_PREFIX
name|String
name|DFS_HA_NAMENODES_KEY_PREFIX
init|=
literal|"dfs.ha.namenodes"
decl_stmt|;
DECL|field|DFS_NAMENODE_RPC_PORT_DEFAULT
name|int
name|DFS_NAMENODE_RPC_PORT_DEFAULT
init|=
literal|8020
decl_stmt|;
comment|/** dfs.client.retry configuration properties */
DECL|interface|Retry
interface|interface
name|Retry
block|{
DECL|field|PREFIX
name|String
name|PREFIX
init|=
name|HdfsClientConfigKeys
operator|.
name|PREFIX
operator|+
literal|"retry."
decl_stmt|;
DECL|field|POLICY_ENABLED_KEY
name|String
name|POLICY_ENABLED_KEY
init|=
name|PREFIX
operator|+
literal|"policy.enabled"
decl_stmt|;
DECL|field|POLICY_ENABLED_DEFAULT
name|boolean
name|POLICY_ENABLED_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|POLICY_SPEC_KEY
name|String
name|POLICY_SPEC_KEY
init|=
name|PREFIX
operator|+
literal|"policy.spec"
decl_stmt|;
DECL|field|POLICY_SPEC_DEFAULT
name|String
name|POLICY_SPEC_DEFAULT
init|=
literal|"10000,6,60000,10"
decl_stmt|;
comment|//t1,n1,t2,n2,...
DECL|field|TIMES_GET_LAST_BLOCK_LENGTH_KEY
name|String
name|TIMES_GET_LAST_BLOCK_LENGTH_KEY
init|=
name|PREFIX
operator|+
literal|"times.get-last-block-length"
decl_stmt|;
DECL|field|TIMES_GET_LAST_BLOCK_LENGTH_DEFAULT
name|int
name|TIMES_GET_LAST_BLOCK_LENGTH_DEFAULT
init|=
literal|3
decl_stmt|;
DECL|field|INTERVAL_GET_LAST_BLOCK_LENGTH_KEY
name|String
name|INTERVAL_GET_LAST_BLOCK_LENGTH_KEY
init|=
name|PREFIX
operator|+
literal|"interval-ms.get-last-block-length"
decl_stmt|;
DECL|field|INTERVAL_GET_LAST_BLOCK_LENGTH_DEFAULT
name|int
name|INTERVAL_GET_LAST_BLOCK_LENGTH_DEFAULT
init|=
literal|4000
decl_stmt|;
DECL|field|MAX_ATTEMPTS_KEY
name|String
name|MAX_ATTEMPTS_KEY
init|=
name|PREFIX
operator|+
literal|"max.attempts"
decl_stmt|;
DECL|field|MAX_ATTEMPTS_DEFAULT
name|int
name|MAX_ATTEMPTS_DEFAULT
init|=
literal|10
decl_stmt|;
DECL|field|WINDOW_BASE_KEY
name|String
name|WINDOW_BASE_KEY
init|=
name|PREFIX
operator|+
literal|"window.base"
decl_stmt|;
DECL|field|WINDOW_BASE_DEFAULT
name|int
name|WINDOW_BASE_DEFAULT
init|=
literal|3000
decl_stmt|;
block|}
comment|/** dfs.client.failover configuration properties */
DECL|interface|Failover
interface|interface
name|Failover
block|{
DECL|field|PREFIX
name|String
name|PREFIX
init|=
name|HdfsClientConfigKeys
operator|.
name|PREFIX
operator|+
literal|"failover."
decl_stmt|;
DECL|field|PROXY_PROVIDER_KEY_PREFIX
name|String
name|PROXY_PROVIDER_KEY_PREFIX
init|=
name|PREFIX
operator|+
literal|"proxy.provider"
decl_stmt|;
DECL|field|MAX_ATTEMPTS_KEY
name|String
name|MAX_ATTEMPTS_KEY
init|=
name|PREFIX
operator|+
literal|"max.attempts"
decl_stmt|;
DECL|field|MAX_ATTEMPTS_DEFAULT
name|int
name|MAX_ATTEMPTS_DEFAULT
init|=
literal|15
decl_stmt|;
DECL|field|SLEEPTIME_BASE_KEY
name|String
name|SLEEPTIME_BASE_KEY
init|=
name|PREFIX
operator|+
literal|"sleep.base.millis"
decl_stmt|;
DECL|field|SLEEPTIME_BASE_DEFAULT
name|int
name|SLEEPTIME_BASE_DEFAULT
init|=
literal|500
decl_stmt|;
DECL|field|SLEEPTIME_MAX_KEY
name|String
name|SLEEPTIME_MAX_KEY
init|=
name|PREFIX
operator|+
literal|"sleep.max.millis"
decl_stmt|;
DECL|field|SLEEPTIME_MAX_DEFAULT
name|int
name|SLEEPTIME_MAX_DEFAULT
init|=
literal|15000
decl_stmt|;
DECL|field|CONNECTION_RETRIES_KEY
name|String
name|CONNECTION_RETRIES_KEY
init|=
name|PREFIX
operator|+
literal|"connection.retries"
decl_stmt|;
DECL|field|CONNECTION_RETRIES_DEFAULT
name|int
name|CONNECTION_RETRIES_DEFAULT
init|=
literal|0
decl_stmt|;
DECL|field|CONNECTION_RETRIES_ON_SOCKET_TIMEOUTS_KEY
name|String
name|CONNECTION_RETRIES_ON_SOCKET_TIMEOUTS_KEY
init|=
name|PREFIX
operator|+
literal|"connection.retries.on.timeouts"
decl_stmt|;
DECL|field|CONNECTION_RETRIES_ON_SOCKET_TIMEOUTS_DEFAULT
name|int
name|CONNECTION_RETRIES_ON_SOCKET_TIMEOUTS_DEFAULT
init|=
literal|0
decl_stmt|;
block|}
comment|/** dfs.client.write configuration properties */
DECL|interface|Write
interface|interface
name|Write
block|{
DECL|field|PREFIX
name|String
name|PREFIX
init|=
name|HdfsClientConfigKeys
operator|.
name|PREFIX
operator|+
literal|"write."
decl_stmt|;
DECL|field|MAX_PACKETS_IN_FLIGHT_KEY
name|String
name|MAX_PACKETS_IN_FLIGHT_KEY
init|=
name|PREFIX
operator|+
literal|"max-packets-in-flight"
decl_stmt|;
DECL|field|MAX_PACKETS_IN_FLIGHT_DEFAULT
name|int
name|MAX_PACKETS_IN_FLIGHT_DEFAULT
init|=
literal|80
decl_stmt|;
DECL|field|EXCLUDE_NODES_CACHE_EXPIRY_INTERVAL_KEY
name|String
name|EXCLUDE_NODES_CACHE_EXPIRY_INTERVAL_KEY
init|=
name|PREFIX
operator|+
literal|"exclude.nodes.cache.expiry.interval.millis"
decl_stmt|;
DECL|field|EXCLUDE_NODES_CACHE_EXPIRY_INTERVAL_DEFAULT
name|long
name|EXCLUDE_NODES_CACHE_EXPIRY_INTERVAL_DEFAULT
init|=
literal|10
operator|*
name|MINUTE
decl_stmt|;
DECL|interface|ByteArrayManager
interface|interface
name|ByteArrayManager
block|{
DECL|field|PREFIX
name|String
name|PREFIX
init|=
name|Write
operator|.
name|PREFIX
operator|+
literal|"byte-array-manager."
decl_stmt|;
DECL|field|ENABLED_KEY
name|String
name|ENABLED_KEY
init|=
name|PREFIX
operator|+
literal|"enabled"
decl_stmt|;
DECL|field|ENABLED_DEFAULT
name|boolean
name|ENABLED_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|COUNT_THRESHOLD_KEY
name|String
name|COUNT_THRESHOLD_KEY
init|=
name|PREFIX
operator|+
literal|"count-threshold"
decl_stmt|;
DECL|field|COUNT_THRESHOLD_DEFAULT
name|int
name|COUNT_THRESHOLD_DEFAULT
init|=
literal|128
decl_stmt|;
DECL|field|COUNT_LIMIT_KEY
name|String
name|COUNT_LIMIT_KEY
init|=
name|PREFIX
operator|+
literal|"count-limit"
decl_stmt|;
DECL|field|COUNT_LIMIT_DEFAULT
name|int
name|COUNT_LIMIT_DEFAULT
init|=
literal|2048
decl_stmt|;
DECL|field|COUNT_RESET_TIME_PERIOD_MS_KEY
name|String
name|COUNT_RESET_TIME_PERIOD_MS_KEY
init|=
name|PREFIX
operator|+
literal|"count-reset-time-period-ms"
decl_stmt|;
DECL|field|COUNT_RESET_TIME_PERIOD_MS_DEFAULT
name|long
name|COUNT_RESET_TIME_PERIOD_MS_DEFAULT
init|=
literal|10
operator|*
name|SECOND
decl_stmt|;
block|}
block|}
comment|/** dfs.client.block.write configuration properties */
DECL|interface|BlockWrite
interface|interface
name|BlockWrite
block|{
DECL|field|PREFIX
name|String
name|PREFIX
init|=
name|HdfsClientConfigKeys
operator|.
name|PREFIX
operator|+
literal|"block.write."
decl_stmt|;
DECL|field|RETRIES_KEY
name|String
name|RETRIES_KEY
init|=
name|PREFIX
operator|+
literal|"retries"
decl_stmt|;
DECL|field|RETRIES_DEFAULT
name|int
name|RETRIES_DEFAULT
init|=
literal|3
decl_stmt|;
DECL|field|LOCATEFOLLOWINGBLOCK_RETRIES_KEY
name|String
name|LOCATEFOLLOWINGBLOCK_RETRIES_KEY
init|=
name|PREFIX
operator|+
literal|"locateFollowingBlock.retries"
decl_stmt|;
DECL|field|LOCATEFOLLOWINGBLOCK_RETRIES_DEFAULT
name|int
name|LOCATEFOLLOWINGBLOCK_RETRIES_DEFAULT
init|=
literal|5
decl_stmt|;
DECL|field|LOCATEFOLLOWINGBLOCK_INITIAL_DELAY_MS_KEY
name|String
name|LOCATEFOLLOWINGBLOCK_INITIAL_DELAY_MS_KEY
init|=
name|PREFIX
operator|+
literal|"locateFollowingBlock.initial.delay.ms"
decl_stmt|;
DECL|field|LOCATEFOLLOWINGBLOCK_INITIAL_DELAY_MS_DEFAULT
name|int
name|LOCATEFOLLOWINGBLOCK_INITIAL_DELAY_MS_DEFAULT
init|=
literal|400
decl_stmt|;
DECL|interface|ReplaceDatanodeOnFailure
interface|interface
name|ReplaceDatanodeOnFailure
block|{
DECL|field|PREFIX
name|String
name|PREFIX
init|=
name|BlockWrite
operator|.
name|PREFIX
operator|+
literal|"replace-datanode-on-failure."
decl_stmt|;
DECL|field|ENABLE_KEY
name|String
name|ENABLE_KEY
init|=
name|PREFIX
operator|+
literal|"enable"
decl_stmt|;
DECL|field|ENABLE_DEFAULT
name|boolean
name|ENABLE_DEFAULT
init|=
literal|true
decl_stmt|;
DECL|field|POLICY_KEY
name|String
name|POLICY_KEY
init|=
name|PREFIX
operator|+
literal|"policy"
decl_stmt|;
DECL|field|POLICY_DEFAULT
name|String
name|POLICY_DEFAULT
init|=
literal|"DEFAULT"
decl_stmt|;
DECL|field|BEST_EFFORT_KEY
name|String
name|BEST_EFFORT_KEY
init|=
name|PREFIX
operator|+
literal|"best-effort"
decl_stmt|;
DECL|field|BEST_EFFORT_DEFAULT
name|boolean
name|BEST_EFFORT_DEFAULT
init|=
literal|false
decl_stmt|;
block|}
block|}
comment|/** dfs.client.read configuration properties */
DECL|interface|Read
interface|interface
name|Read
block|{
DECL|field|PREFIX
name|String
name|PREFIX
init|=
name|HdfsClientConfigKeys
operator|.
name|PREFIX
operator|+
literal|"read."
decl_stmt|;
DECL|field|PREFETCH_SIZE_KEY
name|String
name|PREFETCH_SIZE_KEY
init|=
name|PREFIX
operator|+
literal|"prefetch.size"
decl_stmt|;
DECL|interface|ShortCircuit
interface|interface
name|ShortCircuit
block|{
DECL|field|PREFIX
name|String
name|PREFIX
init|=
name|Read
operator|.
name|PREFIX
operator|+
literal|"shortcircuit."
decl_stmt|;
DECL|field|KEY
name|String
name|KEY
init|=
name|PREFIX
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|PREFIX
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
DECL|field|DEFAULT
name|boolean
name|DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|SKIP_CHECKSUM_KEY
name|String
name|SKIP_CHECKSUM_KEY
init|=
name|PREFIX
operator|+
literal|"skip.checksum"
decl_stmt|;
DECL|field|SKIP_CHECKSUM_DEFAULT
name|boolean
name|SKIP_CHECKSUM_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|BUFFER_SIZE_KEY
name|String
name|BUFFER_SIZE_KEY
init|=
name|PREFIX
operator|+
literal|"buffer.size"
decl_stmt|;
DECL|field|BUFFER_SIZE_DEFAULT
name|int
name|BUFFER_SIZE_DEFAULT
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|STREAMS_CACHE_SIZE_KEY
name|String
name|STREAMS_CACHE_SIZE_KEY
init|=
name|PREFIX
operator|+
literal|"streams.cache.size"
decl_stmt|;
DECL|field|STREAMS_CACHE_SIZE_DEFAULT
name|int
name|STREAMS_CACHE_SIZE_DEFAULT
init|=
literal|256
decl_stmt|;
DECL|field|STREAMS_CACHE_EXPIRY_MS_KEY
name|String
name|STREAMS_CACHE_EXPIRY_MS_KEY
init|=
name|PREFIX
operator|+
literal|"streams.cache.expiry.ms"
decl_stmt|;
DECL|field|STREAMS_CACHE_EXPIRY_MS_DEFAULT
name|long
name|STREAMS_CACHE_EXPIRY_MS_DEFAULT
init|=
literal|5
operator|*
name|MINUTE
decl_stmt|;
block|}
block|}
comment|/** dfs.client.short.circuit configuration properties */
DECL|interface|ShortCircuit
interface|interface
name|ShortCircuit
block|{
DECL|field|PREFIX
name|String
name|PREFIX
init|=
name|Read
operator|.
name|PREFIX
operator|+
literal|"short.circuit."
decl_stmt|;
DECL|field|REPLICA_STALE_THRESHOLD_MS_KEY
name|String
name|REPLICA_STALE_THRESHOLD_MS_KEY
init|=
name|PREFIX
operator|+
literal|"replica.stale.threshold.ms"
decl_stmt|;
DECL|field|REPLICA_STALE_THRESHOLD_MS_DEFAULT
name|long
name|REPLICA_STALE_THRESHOLD_MS_DEFAULT
init|=
literal|30
operator|*
name|MINUTE
decl_stmt|;
block|}
comment|/** dfs.client.mmap configuration properties */
DECL|interface|Mmap
interface|interface
name|Mmap
block|{
DECL|field|PREFIX
name|String
name|PREFIX
init|=
name|HdfsClientConfigKeys
operator|.
name|PREFIX
operator|+
literal|"mmap."
decl_stmt|;
DECL|field|ENABLED_KEY
name|String
name|ENABLED_KEY
init|=
name|PREFIX
operator|+
literal|"enabled"
decl_stmt|;
DECL|field|ENABLED_DEFAULT
name|boolean
name|ENABLED_DEFAULT
init|=
literal|true
decl_stmt|;
DECL|field|CACHE_SIZE_KEY
name|String
name|CACHE_SIZE_KEY
init|=
name|PREFIX
operator|+
literal|"cache.size"
decl_stmt|;
DECL|field|CACHE_SIZE_DEFAULT
name|int
name|CACHE_SIZE_DEFAULT
init|=
literal|256
decl_stmt|;
DECL|field|CACHE_TIMEOUT_MS_KEY
name|String
name|CACHE_TIMEOUT_MS_KEY
init|=
name|PREFIX
operator|+
literal|"cache.timeout.ms"
decl_stmt|;
DECL|field|CACHE_TIMEOUT_MS_DEFAULT
name|long
name|CACHE_TIMEOUT_MS_DEFAULT
init|=
literal|60
operator|*
name|MINUTE
decl_stmt|;
DECL|field|RETRY_TIMEOUT_MS_KEY
name|String
name|RETRY_TIMEOUT_MS_KEY
init|=
name|PREFIX
operator|+
literal|"retry.timeout.ms"
decl_stmt|;
DECL|field|RETRY_TIMEOUT_MS_DEFAULT
name|long
name|RETRY_TIMEOUT_MS_DEFAULT
init|=
literal|5
operator|*
name|MINUTE
decl_stmt|;
block|}
comment|/** dfs.client.hedged.read configuration properties */
DECL|interface|HedgedRead
interface|interface
name|HedgedRead
block|{
DECL|field|THRESHOLD_MILLIS_KEY
name|String
name|THRESHOLD_MILLIS_KEY
init|=
name|PREFIX
operator|+
literal|"threshold.millis"
decl_stmt|;
DECL|field|THRESHOLD_MILLIS_DEFAULT
name|long
name|THRESHOLD_MILLIS_DEFAULT
init|=
literal|500
decl_stmt|;
DECL|field|THREADPOOL_SIZE_KEY
name|String
name|THREADPOOL_SIZE_KEY
init|=
name|PREFIX
operator|+
literal|"threadpool.size"
decl_stmt|;
DECL|field|THREADPOOL_SIZE_DEFAULT
name|int
name|THREADPOOL_SIZE_DEFAULT
init|=
literal|0
decl_stmt|;
block|}
comment|/** dfs.http.client configuration properties */
DECL|interface|HttpClient
interface|interface
name|HttpClient
block|{
DECL|field|PREFIX
name|String
name|PREFIX
init|=
literal|"dfs.http.client."
decl_stmt|;
comment|// retry
DECL|field|RETRY_POLICY_ENABLED_KEY
name|String
name|RETRY_POLICY_ENABLED_KEY
init|=
name|PREFIX
operator|+
literal|"retry.policy.enabled"
decl_stmt|;
DECL|field|RETRY_POLICY_ENABLED_DEFAULT
name|boolean
name|RETRY_POLICY_ENABLED_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|RETRY_POLICY_SPEC_KEY
name|String
name|RETRY_POLICY_SPEC_KEY
init|=
name|PREFIX
operator|+
literal|"retry.policy.spec"
decl_stmt|;
DECL|field|RETRY_POLICY_SPEC_DEFAULT
name|String
name|RETRY_POLICY_SPEC_DEFAULT
init|=
literal|"10000,6,60000,10"
decl_stmt|;
comment|//t1,n1,t2,n2,...
DECL|field|RETRY_MAX_ATTEMPTS_KEY
name|String
name|RETRY_MAX_ATTEMPTS_KEY
init|=
name|PREFIX
operator|+
literal|"retry.max.attempts"
decl_stmt|;
DECL|field|RETRY_MAX_ATTEMPTS_DEFAULT
name|int
name|RETRY_MAX_ATTEMPTS_DEFAULT
init|=
literal|10
decl_stmt|;
comment|// failover
DECL|field|FAILOVER_MAX_ATTEMPTS_KEY
name|String
name|FAILOVER_MAX_ATTEMPTS_KEY
init|=
name|PREFIX
operator|+
literal|"failover.max.attempts"
decl_stmt|;
DECL|field|FAILOVER_MAX_ATTEMPTS_DEFAULT
name|int
name|FAILOVER_MAX_ATTEMPTS_DEFAULT
init|=
literal|15
decl_stmt|;
DECL|field|FAILOVER_SLEEPTIME_BASE_KEY
name|String
name|FAILOVER_SLEEPTIME_BASE_KEY
init|=
name|PREFIX
operator|+
literal|"failover.sleep.base.millis"
decl_stmt|;
DECL|field|FAILOVER_SLEEPTIME_BASE_DEFAULT
name|int
name|FAILOVER_SLEEPTIME_BASE_DEFAULT
init|=
literal|500
decl_stmt|;
DECL|field|FAILOVER_SLEEPTIME_MAX_KEY
name|String
name|FAILOVER_SLEEPTIME_MAX_KEY
init|=
name|PREFIX
operator|+
literal|"failover.sleep.max.millis"
decl_stmt|;
DECL|field|FAILOVER_SLEEPTIME_MAX_DEFAULT
name|int
name|FAILOVER_SLEEPTIME_MAX_DEFAULT
init|=
literal|15000
decl_stmt|;
block|}
block|}
end_interface

end_unit

