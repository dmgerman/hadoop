begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
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
name|federation
operator|.
name|router
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|federation
operator|.
name|metrics
operator|.
name|FederationRPCPerformanceMonitor
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
name|federation
operator|.
name|resolver
operator|.
name|ActiveNamenodeResolver
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
name|federation
operator|.
name|resolver
operator|.
name|FileSubclusterResolver
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
name|federation
operator|.
name|resolver
operator|.
name|MembershipNamenodeResolver
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
name|federation
operator|.
name|resolver
operator|.
name|MountTableResolver
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
name|federation
operator|.
name|store
operator|.
name|driver
operator|.
name|StateStoreDriver
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
name|federation
operator|.
name|store
operator|.
name|driver
operator|.
name|impl
operator|.
name|StateStoreSerializerPBImpl
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
name|federation
operator|.
name|store
operator|.
name|driver
operator|.
name|impl
operator|.
name|StateStoreZooKeeperImpl
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
comment|/**  * Config fields for router-based hdfs federation.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|RBFConfigKeys
specifier|public
class|class
name|RBFConfigKeys
extends|extends
name|CommonConfigurationKeysPublic
block|{
comment|// HDFS Router-based federation
DECL|field|FEDERATION_ROUTER_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|FEDERATION_ROUTER_PREFIX
init|=
literal|"dfs.federation.router."
decl_stmt|;
DECL|field|DFS_ROUTER_DEFAULT_NAMESERVICE
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_DEFAULT_NAMESERVICE
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"default.nameserviceId"
decl_stmt|;
DECL|field|DFS_ROUTER_HANDLER_COUNT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_HANDLER_COUNT_KEY
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"handler.count"
decl_stmt|;
DECL|field|DFS_ROUTER_HANDLER_COUNT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_ROUTER_HANDLER_COUNT_DEFAULT
init|=
literal|10
decl_stmt|;
DECL|field|DFS_ROUTER_READER_QUEUE_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_READER_QUEUE_SIZE_KEY
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"reader.queue.size"
decl_stmt|;
DECL|field|DFS_ROUTER_READER_QUEUE_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_ROUTER_READER_QUEUE_SIZE_DEFAULT
init|=
literal|100
decl_stmt|;
DECL|field|DFS_ROUTER_READER_COUNT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_READER_COUNT_KEY
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"reader.count"
decl_stmt|;
DECL|field|DFS_ROUTER_READER_COUNT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_ROUTER_READER_COUNT_DEFAULT
init|=
literal|1
decl_stmt|;
DECL|field|DFS_ROUTER_HANDLER_QUEUE_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_HANDLER_QUEUE_SIZE_KEY
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"handler.queue.size"
decl_stmt|;
DECL|field|DFS_ROUTER_HANDLER_QUEUE_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_ROUTER_HANDLER_QUEUE_SIZE_DEFAULT
init|=
literal|100
decl_stmt|;
DECL|field|DFS_ROUTER_RPC_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_RPC_BIND_HOST_KEY
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"rpc-bind-host"
decl_stmt|;
DECL|field|DFS_ROUTER_RPC_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_ROUTER_RPC_PORT_DEFAULT
init|=
literal|8888
decl_stmt|;
DECL|field|DFS_ROUTER_RPC_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_RPC_ADDRESS_KEY
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"rpc-address"
decl_stmt|;
DECL|field|DFS_ROUTER_RPC_ADDRESS_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_RPC_ADDRESS_DEFAULT
init|=
literal|"0.0.0.0:"
operator|+
name|DFS_ROUTER_RPC_PORT_DEFAULT
decl_stmt|;
DECL|field|DFS_ROUTER_RPC_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_RPC_ENABLE
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"rpc.enable"
decl_stmt|;
DECL|field|DFS_ROUTER_RPC_ENABLE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|DFS_ROUTER_RPC_ENABLE_DEFAULT
init|=
literal|true
decl_stmt|;
DECL|field|DFS_ROUTER_METRICS_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_METRICS_ENABLE
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"metrics.enable"
decl_stmt|;
DECL|field|DFS_ROUTER_METRICS_ENABLE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|DFS_ROUTER_METRICS_ENABLE_DEFAULT
init|=
literal|true
decl_stmt|;
DECL|field|DFS_ROUTER_METRICS_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_METRICS_CLASS
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"metrics.class"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|RouterRpcMonitor
argument_list|>
DECL|field|DFS_ROUTER_METRICS_CLASS_DEFAULT
name|DFS_ROUTER_METRICS_CLASS_DEFAULT
init|=
name|FederationRPCPerformanceMonitor
operator|.
name|class
decl_stmt|;
comment|// HDFS Router heartbeat
DECL|field|DFS_ROUTER_HEARTBEAT_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_HEARTBEAT_ENABLE
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"heartbeat.enable"
decl_stmt|;
DECL|field|DFS_ROUTER_HEARTBEAT_ENABLE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|DFS_ROUTER_HEARTBEAT_ENABLE_DEFAULT
init|=
literal|true
decl_stmt|;
DECL|field|DFS_ROUTER_HEARTBEAT_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_HEARTBEAT_INTERVAL_MS
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"heartbeat.interval"
decl_stmt|;
DECL|field|DFS_ROUTER_HEARTBEAT_INTERVAL_MS_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|DFS_ROUTER_HEARTBEAT_INTERVAL_MS_DEFAULT
init|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|5
argument_list|)
decl_stmt|;
DECL|field|DFS_ROUTER_MONITOR_NAMENODE
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_MONITOR_NAMENODE
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"monitor.namenode"
decl_stmt|;
DECL|field|DFS_ROUTER_MONITOR_LOCAL_NAMENODE
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_MONITOR_LOCAL_NAMENODE
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"monitor.localnamenode.enable"
decl_stmt|;
DECL|field|DFS_ROUTER_MONITOR_LOCAL_NAMENODE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|DFS_ROUTER_MONITOR_LOCAL_NAMENODE_DEFAULT
init|=
literal|true
decl_stmt|;
DECL|field|DFS_ROUTER_HEARTBEAT_STATE_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_HEARTBEAT_STATE_INTERVAL_MS
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"heartbeat-state.interval"
decl_stmt|;
DECL|field|DFS_ROUTER_HEARTBEAT_STATE_INTERVAL_MS_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|DFS_ROUTER_HEARTBEAT_STATE_INTERVAL_MS_DEFAULT
init|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|5
argument_list|)
decl_stmt|;
comment|// HDFS Router NN client
DECL|field|DFS_ROUTER_NAMENODE_CONNECTION_POOL_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_NAMENODE_CONNECTION_POOL_SIZE
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"connection.pool-size"
decl_stmt|;
DECL|field|DFS_ROUTER_NAMENODE_CONNECTION_POOL_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_ROUTER_NAMENODE_CONNECTION_POOL_SIZE_DEFAULT
init|=
literal|64
decl_stmt|;
DECL|field|DFS_ROUTER_NAMENODE_CONNECTION_POOL_CLEAN
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_NAMENODE_CONNECTION_POOL_CLEAN
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"connection.pool.clean.ms"
decl_stmt|;
DECL|field|DFS_ROUTER_NAMENODE_CONNECTION_POOL_CLEAN_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|DFS_ROUTER_NAMENODE_CONNECTION_POOL_CLEAN_DEFAULT
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|DFS_ROUTER_NAMENODE_CONNECTION_CLEAN_MS
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_NAMENODE_CONNECTION_CLEAN_MS
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"connection.clean.ms"
decl_stmt|;
DECL|field|DFS_ROUTER_NAMENODE_CONNECTION_CLEAN_MS_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|DFS_ROUTER_NAMENODE_CONNECTION_CLEAN_MS_DEFAULT
init|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|// HDFS Router RPC client
DECL|field|DFS_ROUTER_CLIENT_THREADS_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_CLIENT_THREADS_SIZE
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"client.thread-size"
decl_stmt|;
DECL|field|DFS_ROUTER_CLIENT_THREADS_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_ROUTER_CLIENT_THREADS_SIZE_DEFAULT
init|=
literal|32
decl_stmt|;
DECL|field|DFS_ROUTER_CLIENT_MAX_ATTEMPTS
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_CLIENT_MAX_ATTEMPTS
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"client.retry.max.attempts"
decl_stmt|;
DECL|field|DFS_ROUTER_CLIENT_MAX_ATTEMPTS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_ROUTER_CLIENT_MAX_ATTEMPTS_DEFAULT
init|=
literal|3
decl_stmt|;
DECL|field|DFS_ROUTER_CLIENT_REJECT_OVERLOAD
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_CLIENT_REJECT_OVERLOAD
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"client.reject.overload"
decl_stmt|;
DECL|field|DFS_ROUTER_CLIENT_REJECT_OVERLOAD_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|DFS_ROUTER_CLIENT_REJECT_OVERLOAD_DEFAULT
init|=
literal|false
decl_stmt|;
comment|// HDFS Router State Store connection
DECL|field|FEDERATION_FILE_RESOLVER_CLIENT_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|FEDERATION_FILE_RESOLVER_CLIENT_CLASS
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"file.resolver.client.class"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|FileSubclusterResolver
argument_list|>
DECL|field|FEDERATION_FILE_RESOLVER_CLIENT_CLASS_DEFAULT
name|FEDERATION_FILE_RESOLVER_CLIENT_CLASS_DEFAULT
init|=
name|MountTableResolver
operator|.
name|class
decl_stmt|;
DECL|field|FEDERATION_NAMENODE_RESOLVER_CLIENT_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|FEDERATION_NAMENODE_RESOLVER_CLIENT_CLASS
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"namenode.resolver.client.class"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|ActiveNamenodeResolver
argument_list|>
DECL|field|FEDERATION_NAMENODE_RESOLVER_CLIENT_CLASS_DEFAULT
name|FEDERATION_NAMENODE_RESOLVER_CLIENT_CLASS_DEFAULT
init|=
name|MembershipNamenodeResolver
operator|.
name|class
decl_stmt|;
comment|// HDFS Router-based federation State Store
DECL|field|FEDERATION_STORE_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|FEDERATION_STORE_PREFIX
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"store."
decl_stmt|;
DECL|field|DFS_ROUTER_STORE_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_STORE_ENABLE
init|=
name|FEDERATION_STORE_PREFIX
operator|+
literal|"enable"
decl_stmt|;
DECL|field|DFS_ROUTER_STORE_ENABLE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|DFS_ROUTER_STORE_ENABLE_DEFAULT
init|=
literal|true
decl_stmt|;
DECL|field|FEDERATION_STORE_SERIALIZER_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|FEDERATION_STORE_SERIALIZER_CLASS
init|=
name|FEDERATION_STORE_PREFIX
operator|+
literal|"serializer"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Class
argument_list|<
name|StateStoreSerializerPBImpl
argument_list|>
DECL|field|FEDERATION_STORE_SERIALIZER_CLASS_DEFAULT
name|FEDERATION_STORE_SERIALIZER_CLASS_DEFAULT
init|=
name|StateStoreSerializerPBImpl
operator|.
name|class
decl_stmt|;
DECL|field|FEDERATION_STORE_DRIVER_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|FEDERATION_STORE_DRIVER_CLASS
init|=
name|FEDERATION_STORE_PREFIX
operator|+
literal|"driver.class"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|StateStoreDriver
argument_list|>
DECL|field|FEDERATION_STORE_DRIVER_CLASS_DEFAULT
name|FEDERATION_STORE_DRIVER_CLASS_DEFAULT
init|=
name|StateStoreZooKeeperImpl
operator|.
name|class
decl_stmt|;
DECL|field|FEDERATION_STORE_CONNECTION_TEST_MS
specifier|public
specifier|static
specifier|final
name|String
name|FEDERATION_STORE_CONNECTION_TEST_MS
init|=
name|FEDERATION_STORE_PREFIX
operator|+
literal|"connection.test"
decl_stmt|;
DECL|field|FEDERATION_STORE_CONNECTION_TEST_MS_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|FEDERATION_STORE_CONNECTION_TEST_MS_DEFAULT
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|DFS_ROUTER_CACHE_TIME_TO_LIVE_MS
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_CACHE_TIME_TO_LIVE_MS
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"cache.ttl"
decl_stmt|;
DECL|field|DFS_ROUTER_CACHE_TIME_TO_LIVE_MS_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|DFS_ROUTER_CACHE_TIME_TO_LIVE_MS_DEFAULT
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|FEDERATION_STORE_MEMBERSHIP_EXPIRATION_MS
specifier|public
specifier|static
specifier|final
name|String
name|FEDERATION_STORE_MEMBERSHIP_EXPIRATION_MS
init|=
name|FEDERATION_STORE_PREFIX
operator|+
literal|"membership.expiration"
decl_stmt|;
DECL|field|FEDERATION_STORE_MEMBERSHIP_EXPIRATION_MS_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|FEDERATION_STORE_MEMBERSHIP_EXPIRATION_MS_DEFAULT
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|5
argument_list|)
decl_stmt|;
DECL|field|FEDERATION_STORE_ROUTER_EXPIRATION_MS
specifier|public
specifier|static
specifier|final
name|String
name|FEDERATION_STORE_ROUTER_EXPIRATION_MS
init|=
name|FEDERATION_STORE_PREFIX
operator|+
literal|"router.expiration"
decl_stmt|;
DECL|field|FEDERATION_STORE_ROUTER_EXPIRATION_MS_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|FEDERATION_STORE_ROUTER_EXPIRATION_MS_DEFAULT
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|5
argument_list|)
decl_stmt|;
comment|// HDFS Router safe mode
DECL|field|DFS_ROUTER_SAFEMODE_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_SAFEMODE_ENABLE
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"safemode.enable"
decl_stmt|;
DECL|field|DFS_ROUTER_SAFEMODE_ENABLE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|DFS_ROUTER_SAFEMODE_ENABLE_DEFAULT
init|=
literal|true
decl_stmt|;
DECL|field|DFS_ROUTER_SAFEMODE_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_SAFEMODE_EXTENSION
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"safemode.extension"
decl_stmt|;
DECL|field|DFS_ROUTER_SAFEMODE_EXTENSION_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|DFS_ROUTER_SAFEMODE_EXTENSION_DEFAULT
init|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|30
argument_list|)
decl_stmt|;
DECL|field|DFS_ROUTER_SAFEMODE_EXPIRATION
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_SAFEMODE_EXPIRATION
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"safemode.expiration"
decl_stmt|;
DECL|field|DFS_ROUTER_SAFEMODE_EXPIRATION_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|DFS_ROUTER_SAFEMODE_EXPIRATION_DEFAULT
init|=
literal|3
operator|*
name|DFS_ROUTER_CACHE_TIME_TO_LIVE_MS_DEFAULT
decl_stmt|;
comment|// HDFS Router-based federation mount table entries
comment|/** Maximum number of cache entries to have. */
DECL|field|FEDERATION_MOUNT_TABLE_MAX_CACHE_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|FEDERATION_MOUNT_TABLE_MAX_CACHE_SIZE
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"mount-table.max-cache-size"
decl_stmt|;
comment|/** Remove cache entries if we have more than 10k. */
DECL|field|FEDERATION_MOUNT_TABLE_MAX_CACHE_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|FEDERATION_MOUNT_TABLE_MAX_CACHE_SIZE_DEFAULT
init|=
literal|10000
decl_stmt|;
DECL|field|FEDERATION_MOUNT_TABLE_CACHE_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|FEDERATION_MOUNT_TABLE_CACHE_ENABLE
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"mount-table.cache.enable"
decl_stmt|;
DECL|field|FEDERATION_MOUNT_TABLE_CACHE_ENABLE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|FEDERATION_MOUNT_TABLE_CACHE_ENABLE_DEFAULT
init|=
literal|true
decl_stmt|;
comment|// HDFS Router-based federation admin
DECL|field|DFS_ROUTER_ADMIN_HANDLER_COUNT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_ADMIN_HANDLER_COUNT_KEY
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"admin.handler.count"
decl_stmt|;
DECL|field|DFS_ROUTER_ADMIN_HANDLER_COUNT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_ROUTER_ADMIN_HANDLER_COUNT_DEFAULT
init|=
literal|1
decl_stmt|;
DECL|field|DFS_ROUTER_ADMIN_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_ROUTER_ADMIN_PORT_DEFAULT
init|=
literal|8111
decl_stmt|;
DECL|field|DFS_ROUTER_ADMIN_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_ADMIN_ADDRESS_KEY
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"admin-address"
decl_stmt|;
DECL|field|DFS_ROUTER_ADMIN_ADDRESS_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_ADMIN_ADDRESS_DEFAULT
init|=
literal|"0.0.0.0:"
operator|+
name|DFS_ROUTER_ADMIN_PORT_DEFAULT
decl_stmt|;
DECL|field|DFS_ROUTER_ADMIN_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_ADMIN_BIND_HOST_KEY
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"admin-bind-host"
decl_stmt|;
DECL|field|DFS_ROUTER_ADMIN_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_ADMIN_ENABLE
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"admin.enable"
decl_stmt|;
DECL|field|DFS_ROUTER_ADMIN_ENABLE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|DFS_ROUTER_ADMIN_ENABLE_DEFAULT
init|=
literal|true
decl_stmt|;
comment|// HDFS Router-based federation web
DECL|field|DFS_ROUTER_HTTP_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_HTTP_ENABLE
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"http.enable"
decl_stmt|;
DECL|field|DFS_ROUTER_HTTP_ENABLE_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|DFS_ROUTER_HTTP_ENABLE_DEFAULT
init|=
literal|true
decl_stmt|;
DECL|field|DFS_ROUTER_HTTP_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_HTTP_ADDRESS_KEY
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"http-address"
decl_stmt|;
DECL|field|DFS_ROUTER_HTTP_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_ROUTER_HTTP_PORT_DEFAULT
init|=
literal|50071
decl_stmt|;
DECL|field|DFS_ROUTER_HTTP_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_HTTP_BIND_HOST_KEY
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"http-bind-host"
decl_stmt|;
DECL|field|DFS_ROUTER_HTTP_ADDRESS_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_HTTP_ADDRESS_DEFAULT
init|=
literal|"0.0.0.0:"
operator|+
name|DFS_ROUTER_HTTP_PORT_DEFAULT
decl_stmt|;
DECL|field|DFS_ROUTER_HTTPS_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_HTTPS_ADDRESS_KEY
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"https-address"
decl_stmt|;
DECL|field|DFS_ROUTER_HTTPS_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_ROUTER_HTTPS_PORT_DEFAULT
init|=
literal|50072
decl_stmt|;
DECL|field|DFS_ROUTER_HTTPS_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_HTTPS_BIND_HOST_KEY
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"https-bind-host"
decl_stmt|;
DECL|field|DFS_ROUTER_HTTPS_ADDRESS_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_HTTPS_ADDRESS_DEFAULT
init|=
literal|"0.0.0.0:"
operator|+
name|DFS_ROUTER_HTTPS_PORT_DEFAULT
decl_stmt|;
comment|// HDFS Router-based federation quota
DECL|field|DFS_ROUTER_QUOTA_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_QUOTA_ENABLE
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"quota.enable"
decl_stmt|;
DECL|field|DFS_ROUTER_QUOTA_ENABLED_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|DFS_ROUTER_QUOTA_ENABLED_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|DFS_ROUTER_QUOTA_CACHE_UPATE_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|DFS_ROUTER_QUOTA_CACHE_UPATE_INTERVAL
init|=
name|FEDERATION_ROUTER_PREFIX
operator|+
literal|"quota-cache.update.interval"
decl_stmt|;
DECL|field|DFS_ROUTER_QUOTA_CACHE_UPATE_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|DFS_ROUTER_QUOTA_CACHE_UPATE_INTERVAL_DEFAULT
init|=
literal|60000
decl_stmt|;
block|}
end_class

end_unit

