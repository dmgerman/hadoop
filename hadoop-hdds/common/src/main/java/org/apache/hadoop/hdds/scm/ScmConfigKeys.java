begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
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
name|ratis
operator|.
name|proto
operator|.
name|RaftProtos
operator|.
name|ReplicationLevel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|util
operator|.
name|TimeDuration
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
comment|/**  * This class contains constants for configuration keys used in SCM.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|ScmConfigKeys
specifier|public
specifier|final
class|class
name|ScmConfigKeys
block|{
DECL|field|SCM_CONTAINER_CLIENT_STALE_THRESHOLD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SCM_CONTAINER_CLIENT_STALE_THRESHOLD_KEY
init|=
literal|"scm.container.client.idle.threshold"
decl_stmt|;
DECL|field|SCM_CONTAINER_CLIENT_STALE_THRESHOLD_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|SCM_CONTAINER_CLIENT_STALE_THRESHOLD_DEFAULT
init|=
literal|"10s"
decl_stmt|;
DECL|field|SCM_CONTAINER_CLIENT_MAX_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SCM_CONTAINER_CLIENT_MAX_SIZE_KEY
init|=
literal|"scm.container.client.max.size"
decl_stmt|;
DECL|field|SCM_CONTAINER_CLIENT_MAX_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|SCM_CONTAINER_CLIENT_MAX_SIZE_DEFAULT
init|=
literal|256
decl_stmt|;
DECL|field|SCM_CONTAINER_CLIENT_MAX_OUTSTANDING_REQUESTS
specifier|public
specifier|static
specifier|final
name|String
name|SCM_CONTAINER_CLIENT_MAX_OUTSTANDING_REQUESTS
init|=
literal|"scm.container.client.max.outstanding.requests"
decl_stmt|;
DECL|field|SCM_CONTAINER_CLIENT_MAX_OUTSTANDING_REQUESTS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|SCM_CONTAINER_CLIENT_MAX_OUTSTANDING_REQUESTS_DEFAULT
init|=
literal|100
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_ENABLED_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_ENABLED_KEY
init|=
literal|"dfs.container.ratis.enabled"
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_ENABLED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|DFS_CONTAINER_RATIS_ENABLED_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_RPC_TYPE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_RPC_TYPE_KEY
init|=
literal|"dfs.container.ratis.rpc.type"
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_RPC_TYPE_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_RPC_TYPE_DEFAULT
init|=
literal|"GRPC"
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_NUM_WRITE_CHUNK_THREADS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_NUM_WRITE_CHUNK_THREADS_KEY
init|=
literal|"dfs.container.ratis.num.write.chunk.threads"
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_NUM_WRITE_CHUNK_THREADS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CONTAINER_RATIS_NUM_WRITE_CHUNK_THREADS_DEFAULT
init|=
literal|60
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_REPLICATION_LEVEL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_REPLICATION_LEVEL_KEY
init|=
literal|"dfs.container.ratis.replication.level"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ReplicationLevel
DECL|field|DFS_CONTAINER_RATIS_REPLICATION_LEVEL_DEFAULT
name|DFS_CONTAINER_RATIS_REPLICATION_LEVEL_DEFAULT
init|=
name|ReplicationLevel
operator|.
name|MAJORITY
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_NUM_CONTAINER_OP_EXECUTORS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_NUM_CONTAINER_OP_EXECUTORS_KEY
init|=
literal|"dfs.container.ratis.num.container.op.threads"
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_NUM_CONTAINER_OP_EXECUTORS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CONTAINER_RATIS_NUM_CONTAINER_OP_EXECUTORS_DEFAULT
init|=
literal|10
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_SEGMENT_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_SEGMENT_SIZE_KEY
init|=
literal|"dfs.container.ratis.segment.size"
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_SEGMENT_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CONTAINER_RATIS_SEGMENT_SIZE_DEFAULT
init|=
literal|1
operator|*
literal|1024
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_SEGMENT_PREALLOCATED_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_SEGMENT_PREALLOCATED_SIZE_KEY
init|=
literal|"dfs.container.ratis.segment.preallocated.size"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
DECL|field|DFS_CONTAINER_RATIS_SEGMENT_PREALLOCATED_SIZE_DEFAULT
name|DFS_CONTAINER_RATIS_SEGMENT_PREALLOCATED_SIZE_DEFAULT
init|=
literal|128
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|DFS_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_KEY
init|=
literal|"dfs.ratis.client.request.timeout.duration"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|DFS_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_DEFAULT
name|DFS_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_DEFAULT
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
literal|3000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
DECL|field|DFS_RATIS_CLIENT_REQUEST_MAX_RETRIES_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_RATIS_CLIENT_REQUEST_MAX_RETRIES_KEY
init|=
literal|"dfs.ratis.client.request.max.retries"
decl_stmt|;
DECL|field|DFS_RATIS_CLIENT_REQUEST_MAX_RETRIES_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_RATIS_CLIENT_REQUEST_MAX_RETRIES_DEFAULT
init|=
literal|180
decl_stmt|;
DECL|field|DFS_RATIS_CLIENT_REQUEST_RETRY_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_RATIS_CLIENT_REQUEST_RETRY_INTERVAL_KEY
init|=
literal|"dfs.ratis.client.request.retry.interval"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|DFS_RATIS_CLIENT_REQUEST_RETRY_INTERVAL_DEFAULT
name|DFS_RATIS_CLIENT_REQUEST_RETRY_INTERVAL_DEFAULT
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
DECL|field|DFS_RATIS_SERVER_RETRY_CACHE_TIMEOUT_DURATION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_RATIS_SERVER_RETRY_CACHE_TIMEOUT_DURATION_KEY
init|=
literal|"dfs.ratis.server.retry-cache.timeout.duration"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|DFS_RATIS_SERVER_RETRY_CACHE_TIMEOUT_DURATION_DEFAULT
name|DFS_RATIS_SERVER_RETRY_CACHE_TIMEOUT_DURATION_DEFAULT
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
literal|600000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
DECL|field|DFS_RATIS_SERVER_REQUEST_TIMEOUT_DURATION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_RATIS_SERVER_REQUEST_TIMEOUT_DURATION_KEY
init|=
literal|"dfs.ratis.server.request.timeout.duration"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|DFS_RATIS_SERVER_REQUEST_TIMEOUT_DURATION_DEFAULT
name|DFS_RATIS_SERVER_REQUEST_TIMEOUT_DURATION_DEFAULT
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
literal|3000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|DFS_RATIS_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_KEY
name|DFS_RATIS_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_KEY
init|=
literal|"dfs.ratis.leader.election.minimum.timeout.duration"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|DFS_RATIS_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_DEFAULT
name|DFS_RATIS_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_DEFAULT
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
DECL|field|DFS_RATIS_SERVER_FAILURE_DURATION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_RATIS_SERVER_FAILURE_DURATION_KEY
init|=
literal|"dfs.ratis.server.failure.duration"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|DFS_RATIS_SERVER_FAILURE_DURATION_DEFAULT
name|DFS_RATIS_SERVER_FAILURE_DURATION_DEFAULT
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
literal|120
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
comment|// TODO : this is copied from OzoneConsts, may need to move to a better place
DECL|field|OZONE_SCM_CHUNK_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_CHUNK_SIZE_KEY
init|=
literal|"ozone.scm.chunk.size"
decl_stmt|;
comment|// 16 MB by default
DECL|field|OZONE_SCM_CHUNK_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_SCM_CHUNK_SIZE_DEFAULT
init|=
literal|16
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|OZONE_SCM_CHUNK_MAX_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_SCM_CHUNK_MAX_SIZE
init|=
literal|32
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|OZONE_SCM_CLIENT_PORT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_CLIENT_PORT_KEY
init|=
literal|"ozone.scm.client.port"
decl_stmt|;
DECL|field|OZONE_SCM_CLIENT_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_SCM_CLIENT_PORT_DEFAULT
init|=
literal|9860
decl_stmt|;
DECL|field|OZONE_SCM_DATANODE_PORT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_DATANODE_PORT_KEY
init|=
literal|"ozone.scm.datanode.port"
decl_stmt|;
DECL|field|OZONE_SCM_DATANODE_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_SCM_DATANODE_PORT_DEFAULT
init|=
literal|9861
decl_stmt|;
comment|// OZONE_OM_PORT_DEFAULT = 9862
DECL|field|OZONE_SCM_BLOCK_CLIENT_PORT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_BLOCK_CLIENT_PORT_KEY
init|=
literal|"ozone.scm.block.client.port"
decl_stmt|;
DECL|field|OZONE_SCM_BLOCK_CLIENT_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_SCM_BLOCK_CLIENT_PORT_DEFAULT
init|=
literal|9863
decl_stmt|;
comment|// Container service client
DECL|field|OZONE_SCM_CLIENT_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_CLIENT_ADDRESS_KEY
init|=
literal|"ozone.scm.client.address"
decl_stmt|;
DECL|field|OZONE_SCM_CLIENT_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_CLIENT_BIND_HOST_KEY
init|=
literal|"ozone.scm.client.bind.host"
decl_stmt|;
DECL|field|OZONE_SCM_CLIENT_BIND_HOST_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_CLIENT_BIND_HOST_DEFAULT
init|=
literal|"0.0.0.0"
decl_stmt|;
comment|// Block service client
DECL|field|OZONE_SCM_BLOCK_CLIENT_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_BLOCK_CLIENT_ADDRESS_KEY
init|=
literal|"ozone.scm.block.client.address"
decl_stmt|;
DECL|field|OZONE_SCM_BLOCK_CLIENT_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_BLOCK_CLIENT_BIND_HOST_KEY
init|=
literal|"ozone.scm.block.client.bind.host"
decl_stmt|;
DECL|field|OZONE_SCM_BLOCK_CLIENT_BIND_HOST_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_BLOCK_CLIENT_BIND_HOST_DEFAULT
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|OZONE_SCM_DATANODE_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_DATANODE_ADDRESS_KEY
init|=
literal|"ozone.scm.datanode.address"
decl_stmt|;
DECL|field|OZONE_SCM_DATANODE_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_DATANODE_BIND_HOST_KEY
init|=
literal|"ozone.scm.datanode.bind.host"
decl_stmt|;
DECL|field|OZONE_SCM_DATANODE_BIND_HOST_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_DATANODE_BIND_HOST_DEFAULT
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|OZONE_SCM_HTTP_ENABLED_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_HTTP_ENABLED_KEY
init|=
literal|"ozone.scm.http.enabled"
decl_stmt|;
DECL|field|OZONE_SCM_HTTP_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_HTTP_BIND_HOST_KEY
init|=
literal|"ozone.scm.http-bind-host"
decl_stmt|;
DECL|field|OZONE_SCM_HTTPS_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_HTTPS_BIND_HOST_KEY
init|=
literal|"ozone.scm.https-bind-host"
decl_stmt|;
DECL|field|OZONE_SCM_HTTP_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_HTTP_ADDRESS_KEY
init|=
literal|"ozone.scm.http-address"
decl_stmt|;
DECL|field|OZONE_SCM_HTTPS_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_HTTPS_ADDRESS_KEY
init|=
literal|"ozone.scm.https-address"
decl_stmt|;
DECL|field|OZONE_SCM_KEYTAB_FILE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_KEYTAB_FILE
init|=
literal|"ozone.scm.keytab.file"
decl_stmt|;
DECL|field|OZONE_SCM_HTTP_BIND_HOST_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_HTTP_BIND_HOST_DEFAULT
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|OZONE_SCM_HTTP_BIND_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_SCM_HTTP_BIND_PORT_DEFAULT
init|=
literal|9876
decl_stmt|;
DECL|field|OZONE_SCM_HTTPS_BIND_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_SCM_HTTPS_BIND_PORT_DEFAULT
init|=
literal|9877
decl_stmt|;
DECL|field|HDDS_REST_HTTP_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_REST_HTTP_ADDRESS_KEY
init|=
literal|"hdds.rest.http-address"
decl_stmt|;
DECL|field|HDDS_REST_HTTP_ADDRESS_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_REST_HTTP_ADDRESS_DEFAULT
init|=
literal|"0.0.0.0:9880"
decl_stmt|;
DECL|field|HDDS_DATANODE_DIR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_DATANODE_DIR_KEY
init|=
literal|"hdds.datanode.dir"
decl_stmt|;
DECL|field|HDDS_REST_CSRF_ENABLED_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_REST_CSRF_ENABLED_KEY
init|=
literal|"hdds.rest.rest-csrf.enabled"
decl_stmt|;
DECL|field|HDDS_REST_CSRF_ENABLED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|HDDS_REST_CSRF_ENABLED_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|HDDS_REST_NETTY_HIGH_WATERMARK
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_REST_NETTY_HIGH_WATERMARK
init|=
literal|"hdds.rest.netty.high.watermark"
decl_stmt|;
DECL|field|HDDS_REST_NETTY_HIGH_WATERMARK_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|HDDS_REST_NETTY_HIGH_WATERMARK_DEFAULT
init|=
literal|65536
decl_stmt|;
DECL|field|HDDS_REST_NETTY_LOW_WATERMARK_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|HDDS_REST_NETTY_LOW_WATERMARK_DEFAULT
init|=
literal|32768
decl_stmt|;
DECL|field|HDDS_REST_NETTY_LOW_WATERMARK
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_REST_NETTY_LOW_WATERMARK
init|=
literal|"hdds.rest.netty.low.watermark"
decl_stmt|;
DECL|field|OZONE_SCM_HANDLER_COUNT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_HANDLER_COUNT_KEY
init|=
literal|"ozone.scm.handler.count.key"
decl_stmt|;
DECL|field|OZONE_SCM_HANDLER_COUNT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_SCM_HANDLER_COUNT_DEFAULT
init|=
literal|10
decl_stmt|;
DECL|field|OZONE_SCM_DEADNODE_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_DEADNODE_INTERVAL
init|=
literal|"ozone.scm.dead.node.interval"
decl_stmt|;
DECL|field|OZONE_SCM_DEADNODE_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_DEADNODE_INTERVAL_DEFAULT
init|=
literal|"10m"
decl_stmt|;
DECL|field|OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL
init|=
literal|"ozone.scm.heartbeat.thread.interval"
decl_stmt|;
DECL|field|OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL_DEFAULT
init|=
literal|"3s"
decl_stmt|;
DECL|field|OZONE_SCM_STALENODE_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_STALENODE_INTERVAL
init|=
literal|"ozone.scm.stale.node.interval"
decl_stmt|;
DECL|field|OZONE_SCM_STALENODE_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_STALENODE_INTERVAL_DEFAULT
init|=
literal|"90s"
decl_stmt|;
DECL|field|OZONE_SCM_HEARTBEAT_RPC_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_HEARTBEAT_RPC_TIMEOUT
init|=
literal|"ozone.scm.heartbeat.rpc-timeout"
decl_stmt|;
DECL|field|OZONE_SCM_HEARTBEAT_RPC_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|OZONE_SCM_HEARTBEAT_RPC_TIMEOUT_DEFAULT
init|=
literal|1000
decl_stmt|;
comment|/**    * Defines how frequently we will log the missing of heartbeat to a specific    * SCM. In the default case we will write a warning message for each 10    * sequential heart beats that we miss to a specific SCM. This is to avoid    * overrunning the log with lots of HB missed Log statements.    */
DECL|field|OZONE_SCM_HEARTBEAT_LOG_WARN_INTERVAL_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_HEARTBEAT_LOG_WARN_INTERVAL_COUNT
init|=
literal|"ozone.scm.heartbeat.log.warn.interval.count"
decl_stmt|;
DECL|field|OZONE_SCM_HEARTBEAT_LOG_WARN_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_SCM_HEARTBEAT_LOG_WARN_DEFAULT
init|=
literal|10
decl_stmt|;
comment|// ozone.scm.names key is a set of DNS | DNS:PORT | IP Address | IP:PORT.
comment|// Written as a comma separated string. e.g. scm1, scm2:8020, 7.7.7.7:7777
comment|//
comment|// If this key is not specified datanodes will not be able to find
comment|// SCM. The SCM membership can be dynamic, so this key should contain
comment|// all possible SCM names. Once the SCM leader is discovered datanodes will
comment|// get the right list of SCMs to heartbeat to from the leader.
comment|// While it is good for the datanodes to know the names of all SCM nodes,
comment|// it is sufficient to actually know the name of on working SCM. That SCM
comment|// will be able to return the information about other SCMs that are part of
comment|// the SCM replicated Log.
comment|//
comment|//In case of a membership change, any one of the SCM machines will be
comment|// able to send back a new list to the datanodes.
DECL|field|OZONE_SCM_NAMES
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_NAMES
init|=
literal|"ozone.scm.names"
decl_stmt|;
DECL|field|OZONE_SCM_DEFAULT_PORT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_SCM_DEFAULT_PORT
init|=
name|OZONE_SCM_DATANODE_PORT_DEFAULT
decl_stmt|;
comment|// File Name and path where datanode ID is to written to.
comment|// if this value is not set then container startup will fail.
DECL|field|OZONE_SCM_DATANODE_ID
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_DATANODE_ID
init|=
literal|"ozone.scm.datanode.id"
decl_stmt|;
DECL|field|OZONE_SCM_DATANODE_ID_PATH_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_DATANODE_ID_PATH_DEFAULT
init|=
literal|"datanode.id"
decl_stmt|;
DECL|field|OZONE_SCM_DB_CACHE_SIZE_MB
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_DB_CACHE_SIZE_MB
init|=
literal|"ozone.scm.db.cache.size.mb"
decl_stmt|;
DECL|field|OZONE_SCM_DB_CACHE_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_SCM_DB_CACHE_SIZE_DEFAULT
init|=
literal|128
decl_stmt|;
DECL|field|OZONE_SCM_CONTAINER_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_CONTAINER_SIZE
init|=
literal|"ozone.scm.container.size"
decl_stmt|;
DECL|field|OZONE_SCM_CONTAINER_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_CONTAINER_SIZE_DEFAULT
init|=
literal|"5GB"
decl_stmt|;
DECL|field|OZONE_SCM_CONTAINER_PLACEMENT_IMPL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_CONTAINER_PLACEMENT_IMPL_KEY
init|=
literal|"ozone.scm.container.placement.impl"
decl_stmt|;
DECL|field|OZONE_SCM_CONTAINER_PROVISION_BATCH_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_CONTAINER_PROVISION_BATCH_SIZE
init|=
literal|"ozone.scm.container.provision_batch_size"
decl_stmt|;
DECL|field|OZONE_SCM_CONTAINER_PROVISION_BATCH_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_SCM_CONTAINER_PROVISION_BATCH_SIZE_DEFAULT
init|=
literal|20
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|OZONE_SCM_KEY_VALUE_CONTAINER_DELETION_CHOOSING_POLICY
name|OZONE_SCM_KEY_VALUE_CONTAINER_DELETION_CHOOSING_POLICY
init|=
literal|"ozone.scm.keyvalue.container.deletion-choosing.policy"
decl_stmt|;
DECL|field|OZONE_SCM_CONTAINER_CREATION_LEASE_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_CONTAINER_CREATION_LEASE_TIMEOUT
init|=
literal|"ozone.scm.container.creation.lease.timeout"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|OZONE_SCM_CONTAINER_CREATION_LEASE_TIMEOUT_DEFAULT
name|OZONE_SCM_CONTAINER_CREATION_LEASE_TIMEOUT_DEFAULT
init|=
literal|"60s"
decl_stmt|;
DECL|field|OZONE_SCM_PIPELINE_CREATION_LEASE_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_PIPELINE_CREATION_LEASE_TIMEOUT
init|=
literal|"ozone.scm.pipeline.creation.lease.timeout"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|OZONE_SCM_PIPELINE_CREATION_LEASE_TIMEOUT_DEFAULT
name|OZONE_SCM_PIPELINE_CREATION_LEASE_TIMEOUT_DEFAULT
init|=
literal|"60s"
decl_stmt|;
DECL|field|OZONE_SCM_BLOCK_DELETION_MAX_RETRY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_BLOCK_DELETION_MAX_RETRY
init|=
literal|"ozone.scm.block.deletion.max.retry"
decl_stmt|;
DECL|field|OZONE_SCM_BLOCK_DELETION_MAX_RETRY_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_SCM_BLOCK_DELETION_MAX_RETRY_DEFAULT
init|=
literal|4096
decl_stmt|;
DECL|field|HDDS_SCM_WATCHER_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_SCM_WATCHER_TIMEOUT
init|=
literal|"hdds.scm.watcher.timeout"
decl_stmt|;
DECL|field|HDDS_SCM_WATCHER_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_SCM_WATCHER_TIMEOUT_DEFAULT
init|=
literal|"10m"
decl_stmt|;
comment|/**    * Never constructed.    */
DECL|method|ScmConfigKeys ()
specifier|private
name|ScmConfigKeys
parameter_list|()
block|{    }
block|}
end_class

end_unit

