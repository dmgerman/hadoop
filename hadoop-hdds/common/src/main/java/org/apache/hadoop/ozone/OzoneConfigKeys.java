begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|hdds
operator|.
name|client
operator|.
name|ReplicationFactor
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
name|hdds
operator|.
name|client
operator|.
name|ReplicationType
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
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
comment|/**  * This class contains constants for configuration keys used in Ozone.  */
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
DECL|class|OzoneConfigKeys
specifier|public
specifier|final
class|class
name|OzoneConfigKeys
block|{
DECL|field|OZONE_TAGS_SYSTEM_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_TAGS_SYSTEM_KEY
init|=
literal|"ozone.tags.system"
decl_stmt|;
DECL|field|DFS_CONTAINER_IPC_PORT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_IPC_PORT
init|=
literal|"dfs.container.ipc"
decl_stmt|;
DECL|field|DFS_CONTAINER_IPC_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CONTAINER_IPC_PORT_DEFAULT
init|=
literal|9859
decl_stmt|;
comment|/**    *    * When set to true, allocate a random free port for ozone container,    * so that a mini cluster is able to launch multiple containers on a node.    *    * When set to false (default), container port is fixed as specified by    * DFS_CONTAINER_IPC_PORT_DEFAULT.    */
DECL|field|DFS_CONTAINER_IPC_RANDOM_PORT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_IPC_RANDOM_PORT
init|=
literal|"dfs.container.ipc.random.port"
decl_stmt|;
DECL|field|DFS_CONTAINER_IPC_RANDOM_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|DFS_CONTAINER_IPC_RANDOM_PORT_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|DFS_CONTAINER_CHUNK_WRITE_SYNC_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_CHUNK_WRITE_SYNC_KEY
init|=
literal|"dfs.container.chunk.write.sync"
decl_stmt|;
DECL|field|DFS_CONTAINER_CHUNK_WRITE_SYNC_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|DFS_CONTAINER_CHUNK_WRITE_SYNC_DEFAULT
init|=
literal|false
decl_stmt|;
comment|/**    * Ratis Port where containers listen to.    */
DECL|field|DFS_CONTAINER_RATIS_IPC_PORT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_IPC_PORT
init|=
literal|"dfs.container.ratis.ipc"
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_IPC_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CONTAINER_RATIS_IPC_PORT_DEFAULT
init|=
literal|9858
decl_stmt|;
comment|/**    * When set to true, allocate a random free port for ozone container, so that    * a mini cluster is able to launch multiple containers on a node.    */
DECL|field|DFS_CONTAINER_RATIS_IPC_RANDOM_PORT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_IPC_RANDOM_PORT
init|=
literal|"dfs.container.ratis.ipc.random.port"
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_IPC_RANDOM_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|DFS_CONTAINER_RATIS_IPC_RANDOM_PORT_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|OZONE_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ENABLED
init|=
literal|"ozone.enabled"
decl_stmt|;
DECL|field|OZONE_ENABLED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|OZONE_ENABLED_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|OZONE_TRACE_ENABLED_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_TRACE_ENABLED_KEY
init|=
literal|"ozone.trace.enabled"
decl_stmt|;
DECL|field|OZONE_TRACE_ENABLED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|OZONE_TRACE_ENABLED_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|OZONE_METADATA_STORE_IMPL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_METADATA_STORE_IMPL
init|=
literal|"ozone.metastore.impl"
decl_stmt|;
DECL|field|OZONE_METADATA_STORE_IMPL_LEVELDB
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_METADATA_STORE_IMPL_LEVELDB
init|=
literal|"LevelDB"
decl_stmt|;
DECL|field|OZONE_METADATA_STORE_IMPL_ROCKSDB
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_METADATA_STORE_IMPL_ROCKSDB
init|=
literal|"RocksDB"
decl_stmt|;
DECL|field|OZONE_METADATA_STORE_IMPL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_METADATA_STORE_IMPL_DEFAULT
init|=
name|OZONE_METADATA_STORE_IMPL_ROCKSDB
decl_stmt|;
DECL|field|OZONE_METADATA_STORE_ROCKSDB_STATISTICS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_METADATA_STORE_ROCKSDB_STATISTICS
init|=
literal|"ozone.metastore.rocksdb.statistics"
decl_stmt|;
DECL|field|OZONE_METADATA_STORE_ROCKSDB_STATISTICS_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_METADATA_STORE_ROCKSDB_STATISTICS_DEFAULT
init|=
literal|"OFF"
decl_stmt|;
DECL|field|OZONE_METADATA_STORE_ROCKSDB_STATISTICS_OFF
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_METADATA_STORE_ROCKSDB_STATISTICS_OFF
init|=
literal|"OFF"
decl_stmt|;
DECL|field|OZONE_UNSAFEBYTEOPERATIONS_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_UNSAFEBYTEOPERATIONS_ENABLED
init|=
literal|"ozone.UnsafeByteOperations.enabled"
decl_stmt|;
DECL|field|OZONE_UNSAFEBYTEOPERATIONS_ENABLED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|OZONE_UNSAFEBYTEOPERATIONS_ENABLED_DEFAULT
init|=
literal|true
decl_stmt|;
DECL|field|OZONE_CONTAINER_CACHE_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CONTAINER_CACHE_SIZE
init|=
literal|"ozone.container.cache.size"
decl_stmt|;
DECL|field|OZONE_CONTAINER_CACHE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_CONTAINER_CACHE_DEFAULT
init|=
literal|1024
decl_stmt|;
DECL|field|OZONE_SCM_BLOCK_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_BLOCK_SIZE
init|=
literal|"ozone.scm.block.size"
decl_stmt|;
DECL|field|OZONE_SCM_BLOCK_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_BLOCK_SIZE_DEFAULT
init|=
literal|"256MB"
decl_stmt|;
comment|/**    * Ozone administrator users delimited by comma.    * If not set, only the user who launches an ozone service will be the    * admin user. This property must be set if ozone services are started by    * different users. Otherwise the RPC layer will reject calls from    * other servers which are started by users not in the list.    * */
DECL|field|OZONE_ADMINISTRATORS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ADMINISTRATORS
init|=
literal|"ozone.administrators"
decl_stmt|;
DECL|field|OZONE_CLIENT_PROTOCOL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_PROTOCOL
init|=
literal|"ozone.client.protocol"
decl_stmt|;
DECL|field|OZONE_CLIENT_STREAM_BUFFER_FLUSH_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_STREAM_BUFFER_FLUSH_SIZE
init|=
literal|"ozone.client.stream.buffer.flush.size"
decl_stmt|;
DECL|field|OZONE_CLIENT_STREAM_BUFFER_FLUSH_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_STREAM_BUFFER_FLUSH_SIZE_DEFAULT
init|=
literal|"64MB"
decl_stmt|;
DECL|field|OZONE_CLIENT_STREAM_BUFFER_MAX_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_STREAM_BUFFER_MAX_SIZE
init|=
literal|"ozone.client.stream.buffer.max.size"
decl_stmt|;
DECL|field|OZONE_CLIENT_STREAM_BUFFER_MAX_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_STREAM_BUFFER_MAX_SIZE_DEFAULT
init|=
literal|"128MB"
decl_stmt|;
DECL|field|OZONE_CLIENT_WATCH_REQUEST_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_WATCH_REQUEST_TIMEOUT
init|=
literal|"ozone.client.watch.request.timeout"
decl_stmt|;
DECL|field|OZONE_CLIENT_WATCH_REQUEST_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_WATCH_REQUEST_TIMEOUT_DEFAULT
init|=
literal|"30s"
decl_stmt|;
DECL|field|OZONE_CLIENT_MAX_RETRIES
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_MAX_RETRIES
init|=
literal|"ozone.client.max.retries"
decl_stmt|;
DECL|field|OZONE_CLIENT_MAX_RETRIES_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_CLIENT_MAX_RETRIES_DEFAULT
init|=
literal|100
decl_stmt|;
DECL|field|OZONE_CLIENT_RETRY_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_RETRY_INTERVAL
init|=
literal|"ozone.client.retry.interval"
decl_stmt|;
DECL|field|OZONE_CLIENT_RETRY_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|TimeDuration
name|OZONE_CLIENT_RETRY_INTERVAL_DEFAULT
init|=
name|TimeDuration
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
comment|// This defines the overall connection limit for the connection pool used in
comment|// RestClient.
DECL|field|OZONE_REST_CLIENT_HTTP_CONNECTION_MAX
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_REST_CLIENT_HTTP_CONNECTION_MAX
init|=
literal|"ozone.rest.client.http.connection.max"
decl_stmt|;
DECL|field|OZONE_REST_CLIENT_HTTP_CONNECTION_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_REST_CLIENT_HTTP_CONNECTION_DEFAULT
init|=
literal|100
decl_stmt|;
comment|// This defines the connection limit per one HTTP route/host.
DECL|field|OZONE_REST_CLIENT_HTTP_CONNECTION_PER_ROUTE_MAX
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_REST_CLIENT_HTTP_CONNECTION_PER_ROUTE_MAX
init|=
literal|"ozone.rest.client.http.connection.per-route.max"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
DECL|field|OZONE_REST_CLIENT_HTTP_CONNECTION_PER_ROUTE_MAX_DEFAULT
name|OZONE_REST_CLIENT_HTTP_CONNECTION_PER_ROUTE_MAX_DEFAULT
init|=
literal|20
decl_stmt|;
DECL|field|OZONE_CLIENT_SOCKET_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_SOCKET_TIMEOUT
init|=
literal|"ozone.client.socket.timeout"
decl_stmt|;
DECL|field|OZONE_CLIENT_SOCKET_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_CLIENT_SOCKET_TIMEOUT_DEFAULT
init|=
literal|5000
decl_stmt|;
DECL|field|OZONE_CLIENT_CONNECTION_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_CONNECTION_TIMEOUT
init|=
literal|"ozone.client.connection.timeout"
decl_stmt|;
DECL|field|OZONE_CLIENT_CONNECTION_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_CLIENT_CONNECTION_TIMEOUT_DEFAULT
init|=
literal|5000
decl_stmt|;
DECL|field|OZONE_REPLICATION
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_REPLICATION
init|=
literal|"ozone.replication"
decl_stmt|;
DECL|field|OZONE_REPLICATION_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_REPLICATION_DEFAULT
init|=
name|ReplicationFactor
operator|.
name|THREE
operator|.
name|getValue
argument_list|()
decl_stmt|;
DECL|field|OZONE_REPLICATION_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_REPLICATION_TYPE
init|=
literal|"ozone.replication.type"
decl_stmt|;
DECL|field|OZONE_REPLICATION_TYPE_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_REPLICATION_TYPE_DEFAULT
init|=
name|ReplicationType
operator|.
name|RATIS
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|/**    * Configuration property to configure the cache size of client list calls.    */
DECL|field|OZONE_CLIENT_LIST_CACHE_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_LIST_CACHE_SIZE
init|=
literal|"ozone.client.list.cache"
decl_stmt|;
DECL|field|OZONE_CLIENT_LIST_CACHE_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_CLIENT_LIST_CACHE_SIZE_DEFAULT
init|=
literal|1000
decl_stmt|;
comment|/**    * Configuration properties for Ozone Block Deleting Service.    */
DECL|field|OZONE_BLOCK_DELETING_SERVICE_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL
init|=
literal|"ozone.block.deleting.service.interval"
decl_stmt|;
DECL|field|OZONE_BLOCK_DELETING_SERVICE_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL_DEFAULT
init|=
literal|"60s"
decl_stmt|;
comment|/**    * The interval of open key clean service.    */
DECL|field|OZONE_OPEN_KEY_CLEANUP_SERVICE_INTERVAL_SECONDS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OPEN_KEY_CLEANUP_SERVICE_INTERVAL_SECONDS
init|=
literal|"ozone.open.key.cleanup.service.interval.seconds"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
DECL|field|OZONE_OPEN_KEY_CLEANUP_SERVICE_INTERVAL_SECONDS_DEFAULT
name|OZONE_OPEN_KEY_CLEANUP_SERVICE_INTERVAL_SECONDS_DEFAULT
init|=
literal|24
operator|*
literal|3600
decl_stmt|;
comment|// a total of 24 hour
comment|/**    * An open key gets cleaned up when it is being in open state for too long.    */
DECL|field|OZONE_OPEN_KEY_EXPIRE_THRESHOLD_SECONDS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_OPEN_KEY_EXPIRE_THRESHOLD_SECONDS
init|=
literal|"ozone.open.key.expire.threshold"
decl_stmt|;
DECL|field|OZONE_OPEN_KEY_EXPIRE_THRESHOLD_SECONDS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_OPEN_KEY_EXPIRE_THRESHOLD_SECONDS_DEFAULT
init|=
literal|24
operator|*
literal|3600
decl_stmt|;
DECL|field|OZONE_BLOCK_DELETING_SERVICE_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_BLOCK_DELETING_SERVICE_TIMEOUT
init|=
literal|"ozone.block.deleting.service.timeout"
decl_stmt|;
DECL|field|OZONE_BLOCK_DELETING_SERVICE_TIMEOUT_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_BLOCK_DELETING_SERVICE_TIMEOUT_DEFAULT
init|=
literal|"300s"
decl_stmt|;
comment|// 300s for default
DECL|field|OZONE_KEY_PREALLOCATION_BLOCKS_MAX
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KEY_PREALLOCATION_BLOCKS_MAX
init|=
literal|"ozone.key.preallocation.max.blocks"
decl_stmt|;
DECL|field|OZONE_KEY_PREALLOCATION_BLOCKS_MAX_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_KEY_PREALLOCATION_BLOCKS_MAX_DEFAULT
init|=
literal|64
decl_stmt|;
DECL|field|OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER
init|=
literal|"ozone.block.deleting.limit.per.task"
decl_stmt|;
DECL|field|OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER_DEFAULT
init|=
literal|1000
decl_stmt|;
DECL|field|OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL
init|=
literal|"ozone.block.deleting.container.limit.per.interval"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
DECL|field|OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL_DEFAULT
name|OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL_DEFAULT
init|=
literal|10
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_ENABLED_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_ENABLED_KEY
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_ENABLED_KEY
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_ENABLED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|DFS_CONTAINER_RATIS_ENABLED_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_ENABLED_DEFAULT
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_RPC_TYPE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_RPC_TYPE_KEY
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_RPC_TYPE_KEY
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_RPC_TYPE_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_RPC_TYPE_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_RPC_TYPE_DEFAULT
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_NUM_WRITE_CHUNK_THREADS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_NUM_WRITE_CHUNK_THREADS_KEY
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_NUM_WRITE_CHUNK_THREADS_KEY
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_NUM_WRITE_CHUNK_THREADS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CONTAINER_RATIS_NUM_WRITE_CHUNK_THREADS_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_NUM_WRITE_CHUNK_THREADS_DEFAULT
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_REPLICATION_LEVEL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_REPLICATION_LEVEL_KEY
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_REPLICATION_LEVEL_KEY
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ReplicationLevel
DECL|field|DFS_CONTAINER_RATIS_REPLICATION_LEVEL_DEFAULT
name|DFS_CONTAINER_RATIS_REPLICATION_LEVEL_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_REPLICATION_LEVEL_DEFAULT
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_NUM_CONTAINER_OP_EXECUTORS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_NUM_CONTAINER_OP_EXECUTORS_KEY
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_NUM_CONTAINER_OP_EXECUTORS_KEY
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_NUM_CONTAINER_OP_EXECUTORS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CONTAINER_RATIS_NUM_CONTAINER_OP_EXECUTORS_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_NUM_CONTAINER_OP_EXECUTORS_DEFAULT
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_SEGMENT_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_SEGMENT_SIZE_KEY
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_SEGMENT_SIZE_KEY
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_SEGMENT_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_SEGMENT_SIZE_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_SEGMENT_SIZE_DEFAULT
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_SEGMENT_PREALLOCATED_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_SEGMENT_PREALLOCATED_SIZE_KEY
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_SEGMENT_PREALLOCATED_SIZE_KEY
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|DFS_CONTAINER_RATIS_SEGMENT_PREALLOCATED_SIZE_DEFAULT
name|DFS_CONTAINER_RATIS_SEGMENT_PREALLOCATED_SIZE_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_SEGMENT_PREALLOCATED_SIZE_DEFAULT
decl_stmt|;
comment|// config settings to enable stateMachineData write timeout
specifier|public
specifier|static
specifier|final
name|String
DECL|field|DFS_CONTAINER_RATIS_STATEMACHINEDATA_SYNC_TIMEOUT
name|DFS_CONTAINER_RATIS_STATEMACHINEDATA_SYNC_TIMEOUT
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_STATEMACHINEDATA_SYNC_TIMEOUT
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|DFS_CONTAINER_RATIS_STATEMACHINEDATA_SYNC_TIMEOUT_DEFAULT
name|DFS_CONTAINER_RATIS_STATEMACHINEDATA_SYNC_TIMEOUT_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_STATEMACHINEDATA_SYNC_TIMEOUT_DEFAULT
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|DFS_CONTAINER_RATIS_STATEMACHINEDATA_CACHE_EXPIRY_INTERVAL
name|DFS_CONTAINER_RATIS_STATEMACHINEDATA_CACHE_EXPIRY_INTERVAL
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_STATEMACHINEDATA_CACHE_EXPIRY_INTERVAL
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|DFS_CONTAINER_RATIS_STATEMACHINEDATA_CACHE_EXPIRY_INTERVAL_DEFAULT
name|DFS_CONTAINER_RATIS_STATEMACHINEDATA_CACHE_EXPIRY_INTERVAL_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_STATEMACHINEDATA_CACHE_EXPIRY_INTERVAL_DEFAULT
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_DATANODE_STORAGE_DIR
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_DATANODE_STORAGE_DIR
init|=
literal|"dfs.container.ratis.datanode.storage.dir"
decl_stmt|;
DECL|field|DFS_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_KEY
init|=
name|ScmConfigKeys
operator|.
name|DFS_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_KEY
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|DFS_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_DEFAULT
name|DFS_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_RATIS_CLIENT_REQUEST_TIMEOUT_DURATION_DEFAULT
decl_stmt|;
DECL|field|DFS_RATIS_CLIENT_REQUEST_MAX_RETRIES_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_RATIS_CLIENT_REQUEST_MAX_RETRIES_KEY
init|=
name|ScmConfigKeys
operator|.
name|DFS_RATIS_CLIENT_REQUEST_MAX_RETRIES_KEY
decl_stmt|;
DECL|field|DFS_RATIS_CLIENT_REQUEST_MAX_RETRIES_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_RATIS_CLIENT_REQUEST_MAX_RETRIES_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_RATIS_CLIENT_REQUEST_MAX_RETRIES_DEFAULT
decl_stmt|;
DECL|field|DFS_RATIS_CLIENT_REQUEST_RETRY_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_RATIS_CLIENT_REQUEST_RETRY_INTERVAL_KEY
init|=
name|ScmConfigKeys
operator|.
name|DFS_RATIS_CLIENT_REQUEST_RETRY_INTERVAL_KEY
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|DFS_RATIS_CLIENT_REQUEST_RETRY_INTERVAL_DEFAULT
name|DFS_RATIS_CLIENT_REQUEST_RETRY_INTERVAL_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_RATIS_CLIENT_REQUEST_RETRY_INTERVAL_DEFAULT
decl_stmt|;
DECL|field|DFS_RATIS_SERVER_RETRY_CACHE_TIMEOUT_DURATION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_RATIS_SERVER_RETRY_CACHE_TIMEOUT_DURATION_KEY
init|=
name|ScmConfigKeys
operator|.
name|DFS_RATIS_SERVER_RETRY_CACHE_TIMEOUT_DURATION_KEY
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|DFS_RATIS_SERVER_RETRY_CACHE_TIMEOUT_DURATION_DEFAULT
name|DFS_RATIS_SERVER_RETRY_CACHE_TIMEOUT_DURATION_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_RATIS_SERVER_RETRY_CACHE_TIMEOUT_DURATION_DEFAULT
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|DFS_CONTAINER_RATIS_STATEMACHINEDATA_SYNC_RETRIES
name|DFS_CONTAINER_RATIS_STATEMACHINEDATA_SYNC_RETRIES
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_STATEMACHINEDATA_SYNC_RETRIES
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
DECL|field|DFS_CONTAINER_RATIS_STATEMACHINEDATA_SYNC_RETRIES_DEFAULT
name|DFS_CONTAINER_RATIS_STATEMACHINEDATA_SYNC_RETRIES_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_STATEMACHINEDATA_SYNC_RETRIES_DEFAULT
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_LOG_QUEUE_NUM_ELEMENTS
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_LOG_QUEUE_NUM_ELEMENTS
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_LOG_QUEUE_NUM_ELEMENTS
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_LOG_QUEUE_NUM_ELEMENTS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CONTAINER_RATIS_LOG_QUEUE_NUM_ELEMENTS_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_LOG_QUEUE_NUM_ELEMENTS_DEFAULT
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_LOG_QUEUE_BYTE_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_LOG_QUEUE_BYTE_LIMIT
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_LOG_QUEUE_BYTE_LIMIT
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_LOG_QUEUE_BYTE_LIMIT_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_LOG_QUEUE_BYTE_LIMIT_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_LOG_QUEUE_BYTE_LIMIT_DEFAULT
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|DFS_CONTAINER_RATIS_LOG_APPENDER_QUEUE_NUM_ELEMENTS
name|DFS_CONTAINER_RATIS_LOG_APPENDER_QUEUE_NUM_ELEMENTS
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_LOG_APPENDER_QUEUE_NUM_ELEMENTS
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
DECL|field|DFS_CONTAINER_RATIS_LOG_APPENDER_QUEUE_NUM_ELEMENTS_DEFAULT
name|DFS_CONTAINER_RATIS_LOG_APPENDER_QUEUE_NUM_ELEMENTS_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_LOG_APPENDER_QUEUE_NUM_ELEMENTS_DEFAULT
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_LOG_APPENDER_QUEUE_BYTE_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_LOG_APPENDER_QUEUE_BYTE_LIMIT
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_LOG_APPENDER_QUEUE_BYTE_LIMIT
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|DFS_CONTAINER_RATIS_LOG_APPENDER_QUEUE_BYTE_LIMIT_DEFAULT
name|DFS_CONTAINER_RATIS_LOG_APPENDER_QUEUE_BYTE_LIMIT_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_LOG_APPENDER_QUEUE_BYTE_LIMIT_DEFAULT
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_LOG_PURGE_GAP
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_LOG_PURGE_GAP
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_LOG_PURGE_GAP
decl_stmt|;
DECL|field|DFS_CONTAINER_RATIS_LOG_PURGE_GAP_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CONTAINER_RATIS_LOG_PURGE_GAP_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_LOG_PURGE_GAP_DEFAULT
decl_stmt|;
DECL|field|DFS_RATIS_SERVER_REQUEST_TIMEOUT_DURATION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_RATIS_SERVER_REQUEST_TIMEOUT_DURATION_KEY
init|=
name|ScmConfigKeys
operator|.
name|DFS_RATIS_SERVER_REQUEST_TIMEOUT_DURATION_KEY
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|DFS_RATIS_SERVER_REQUEST_TIMEOUT_DURATION_DEFAULT
name|DFS_RATIS_SERVER_REQUEST_TIMEOUT_DURATION_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_RATIS_SERVER_REQUEST_TIMEOUT_DURATION_DEFAULT
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|DFS_RATIS_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_KEY
name|DFS_RATIS_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_KEY
init|=
name|ScmConfigKeys
operator|.
name|DFS_RATIS_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_KEY
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|DFS_RATIS_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_DEFAULT
name|DFS_RATIS_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_RATIS_LEADER_ELECTION_MINIMUM_TIMEOUT_DURATION_DEFAULT
decl_stmt|;
DECL|field|DFS_RATIS_SNAPSHOT_THRESHOLD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_RATIS_SNAPSHOT_THRESHOLD_KEY
init|=
name|ScmConfigKeys
operator|.
name|DFS_RATIS_SNAPSHOT_THRESHOLD_KEY
decl_stmt|;
DECL|field|DFS_RATIS_SNAPSHOT_THRESHOLD_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|DFS_RATIS_SNAPSHOT_THRESHOLD_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_RATIS_SNAPSHOT_THRESHOLD_DEFAULT
decl_stmt|;
DECL|field|DFS_RATIS_SERVER_FAILURE_DURATION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_RATIS_SERVER_FAILURE_DURATION_KEY
init|=
name|ScmConfigKeys
operator|.
name|DFS_RATIS_SERVER_FAILURE_DURATION_KEY
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|TimeDuration
DECL|field|DFS_RATIS_SERVER_FAILURE_DURATION_DEFAULT
name|DFS_RATIS_SERVER_FAILURE_DURATION_DEFAULT
init|=
name|ScmConfigKeys
operator|.
name|DFS_RATIS_SERVER_FAILURE_DURATION_DEFAULT
decl_stmt|;
DECL|field|HDDS_DATANODE_PLUGINS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|HDDS_DATANODE_PLUGINS_KEY
init|=
literal|"hdds.datanode.plugins"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|HDDS_DATANODE_STORAGE_UTILIZATION_WARNING_THRESHOLD
name|HDDS_DATANODE_STORAGE_UTILIZATION_WARNING_THRESHOLD
init|=
literal|"hdds.datanode.storage.utilization.warning.threshold"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|double
DECL|field|HDDS_DATANODE_STORAGE_UTILIZATION_WARNING_THRESHOLD_DEFAULT
name|HDDS_DATANODE_STORAGE_UTILIZATION_WARNING_THRESHOLD_DEFAULT
init|=
literal|0.95
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|HDDS_DATANODE_STORAGE_UTILIZATION_CRITICAL_THRESHOLD
name|HDDS_DATANODE_STORAGE_UTILIZATION_CRITICAL_THRESHOLD
init|=
literal|"hdds.datanode.storage.utilization.critical.threshold"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|double
DECL|field|HDDS_DATANODE_STORAGE_UTILIZATION_CRITICAL_THRESHOLD_DEFAULT
name|HDDS_DATANODE_STORAGE_UTILIZATION_CRITICAL_THRESHOLD_DEFAULT
init|=
literal|0.75
decl_stmt|;
DECL|field|OZONE_SECURITY_ENABLED_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SECURITY_ENABLED_KEY
init|=
literal|"ozone.security.enabled"
decl_stmt|;
DECL|field|OZONE_SECURITY_ENABLED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|OZONE_SECURITY_ENABLED_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|OZONE_CONTAINER_COPY_WORKDIR
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CONTAINER_COPY_WORKDIR
init|=
literal|"hdds.datanode.replication.work.dir"
decl_stmt|;
comment|/**    * Config properties to set client side checksum properties.    */
DECL|field|OZONE_CLIENT_CHECKSUM_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_CHECKSUM_TYPE
init|=
literal|"ozone.client.checksum.type"
decl_stmt|;
DECL|field|OZONE_CLIENT_CHECKSUM_TYPE_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_CHECKSUM_TYPE_DEFAULT
init|=
literal|"SHA256"
decl_stmt|;
DECL|field|OZONE_CLIENT_BYTES_PER_CHECKSUM
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_BYTES_PER_CHECKSUM
init|=
literal|"ozone.client.bytes.per.checksum"
decl_stmt|;
DECL|field|OZONE_CLIENT_BYTES_PER_CHECKSUM_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_BYTES_PER_CHECKSUM_DEFAULT
init|=
literal|"1MB"
decl_stmt|;
DECL|field|OZONE_CLIENT_BYTES_PER_CHECKSUM_DEFAULT_BYTES
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_CLIENT_BYTES_PER_CHECKSUM_DEFAULT_BYTES
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|OZONE_CLIENT_BYTES_PER_CHECKSUM_MIN_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_CLIENT_BYTES_PER_CHECKSUM_MIN_SIZE
init|=
literal|256
operator|*
literal|1024
decl_stmt|;
DECL|field|OZONE_CLIENT_VERIFY_CHECKSUM
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_VERIFY_CHECKSUM
init|=
literal|"ozone.client.verify.checksum"
decl_stmt|;
DECL|field|OZONE_CLIENT_VERIFY_CHECKSUM_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|OZONE_CLIENT_VERIFY_CHECKSUM_DEFAULT
init|=
literal|true
decl_stmt|;
DECL|field|OZONE_ACL_AUTHORIZER_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ACL_AUTHORIZER_CLASS
init|=
literal|"ozone.acl.authorizer.class"
decl_stmt|;
DECL|field|OZONE_ACL_AUTHORIZER_CLASS_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ACL_AUTHORIZER_CLASS_DEFAULT
init|=
literal|"org.apache.hadoop.ozone.security.acl.OzoneAccessAuthorizer"
decl_stmt|;
DECL|field|OZONE_ACL_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_ACL_ENABLED
init|=
literal|"ozone.acl.enabled"
decl_stmt|;
DECL|field|OZONE_ACL_ENABLED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|OZONE_ACL_ENABLED_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|OZONE_S3_TOKEN_MAX_LIFETIME_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_S3_TOKEN_MAX_LIFETIME_KEY
init|=
literal|"ozone.s3.token.max.lifetime"
decl_stmt|;
DECL|field|OZONE_S3_TOKEN_MAX_LIFETIME_KEY_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_S3_TOKEN_MAX_LIFETIME_KEY_DEFAULT
init|=
literal|"3m"
decl_stmt|;
comment|//For technical reasons this is unused and hardcoded to the
comment|// OzoneFileSystem.initialize.
DECL|field|OZONE_FS_ISOLATED_CLASSLOADER
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_FS_ISOLATED_CLASSLOADER
init|=
literal|"ozone.fs.isolated-classloader"
decl_stmt|;
comment|// Ozone Client Retry and Failover configurations
DECL|field|OZONE_CLIENT_RETRY_MAX_ATTEMPTS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_RETRY_MAX_ATTEMPTS_KEY
init|=
literal|"ozone.client.retry.max.attempts"
decl_stmt|;
DECL|field|OZONE_CLIENT_RETRY_MAX_ATTEMPTS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_CLIENT_RETRY_MAX_ATTEMPTS_DEFAULT
init|=
literal|10
decl_stmt|;
DECL|field|OZONE_CLIENT_FAILOVER_MAX_ATTEMPTS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_FAILOVER_MAX_ATTEMPTS_KEY
init|=
literal|"ozone.client.failover.max.attempts"
decl_stmt|;
DECL|field|OZONE_CLIENT_FAILOVER_MAX_ATTEMPTS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_CLIENT_FAILOVER_MAX_ATTEMPTS_DEFAULT
init|=
literal|15
decl_stmt|;
DECL|field|OZONE_CLIENT_FAILOVER_SLEEP_BASE_MILLIS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_FAILOVER_SLEEP_BASE_MILLIS_KEY
init|=
literal|"ozone.client.failover.sleep.base.millis"
decl_stmt|;
DECL|field|OZONE_CLIENT_FAILOVER_SLEEP_BASE_MILLIS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_CLIENT_FAILOVER_SLEEP_BASE_MILLIS_DEFAULT
init|=
literal|500
decl_stmt|;
DECL|field|OZONE_CLIENT_FAILOVER_SLEEP_MAX_MILLIS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_FAILOVER_SLEEP_MAX_MILLIS_KEY
init|=
literal|"ozone.client.failover.sleep.max.millis"
decl_stmt|;
DECL|field|OZONE_CLIENT_FAILOVER_SLEEP_MAX_MILLIS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_CLIENT_FAILOVER_SLEEP_MAX_MILLIS_DEFAULT
init|=
literal|15000
decl_stmt|;
DECL|field|OZONE_FREON_HTTP_ENABLED_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_FREON_HTTP_ENABLED_KEY
init|=
literal|"ozone.freon.http.enabled"
decl_stmt|;
DECL|field|OZONE_FREON_HTTP_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_FREON_HTTP_BIND_HOST_KEY
init|=
literal|"ozone.freon.http-bind-host"
decl_stmt|;
DECL|field|OZONE_FREON_HTTPS_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_FREON_HTTPS_BIND_HOST_KEY
init|=
literal|"ozone.freon.https-bind-host"
decl_stmt|;
DECL|field|OZONE_FREON_HTTP_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_FREON_HTTP_ADDRESS_KEY
init|=
literal|"ozone.freon.http-address"
decl_stmt|;
DECL|field|OZONE_FREON_HTTPS_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_FREON_HTTPS_ADDRESS_KEY
init|=
literal|"ozone.freon.https-address"
decl_stmt|;
DECL|field|OZONE_FREON_HTTP_BIND_HOST_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_FREON_HTTP_BIND_HOST_DEFAULT
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|OZONE_FREON_HTTP_BIND_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_FREON_HTTP_BIND_PORT_DEFAULT
init|=
literal|9884
decl_stmt|;
DECL|field|OZONE_FREON_HTTPS_BIND_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_FREON_HTTPS_BIND_PORT_DEFAULT
init|=
literal|9885
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|OZONE_FREON_HTTP_KERBEROS_PRINCIPAL_KEY
name|OZONE_FREON_HTTP_KERBEROS_PRINCIPAL_KEY
init|=
literal|"ozone.freon.http.kerberos.principal"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
DECL|field|OZONE_FREON_HTTP_KERBEROS_KEYTAB_FILE_KEY
name|OZONE_FREON_HTTP_KERBEROS_KEYTAB_FILE_KEY
init|=
literal|"ozone.freon.http.kerberos.keytab"
decl_stmt|;
comment|/**    * There is no need to instantiate this class.    */
DECL|method|OzoneConfigKeys ()
specifier|private
name|OzoneConfigKeys
parameter_list|()
block|{   }
block|}
end_class

end_unit

