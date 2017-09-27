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
name|ozone
operator|.
name|client
operator|.
name|protocol
operator|.
name|ClientProtocol
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
name|ozone
operator|.
name|client
operator|.
name|rest
operator|.
name|RestClient
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
name|ozone
operator|.
name|client
operator|.
name|rpc
operator|.
name|RpcClient
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
name|scm
operator|.
name|ScmConfigKeys
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
DECL|field|OZONE_LOCALSTORAGE_ROOT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_LOCALSTORAGE_ROOT
init|=
literal|"ozone.localstorage.root"
decl_stmt|;
DECL|field|OZONE_LOCALSTORAGE_ROOT_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_LOCALSTORAGE_ROOT_DEFAULT
init|=
literal|"/tmp/ozone"
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
DECL|field|OZONE_HANDLER_TYPE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_HANDLER_TYPE_KEY
init|=
literal|"ozone.handler.type"
decl_stmt|;
DECL|field|OZONE_HANDLER_TYPE_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_HANDLER_TYPE_DEFAULT
init|=
literal|"distributed"
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
DECL|field|OZONE_METADATA_DIRS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_METADATA_DIRS
init|=
literal|"ozone.metadata.dirs"
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
DECL|field|OZONE_KEY_CACHE
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KEY_CACHE
init|=
literal|"ozone.key.cache.size"
decl_stmt|;
DECL|field|OZONE_KEY_CACHE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_KEY_CACHE_DEFAULT
init|=
literal|1024
decl_stmt|;
DECL|field|OZONE_SCM_BLOCK_SIZE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_BLOCK_SIZE_KEY
init|=
literal|"ozone.scm.block.size"
decl_stmt|;
DECL|field|OZONE_SCM_BLOCK_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|OZONE_SCM_BLOCK_SIZE_DEFAULT
init|=
literal|256
operator|*
name|OzoneConsts
operator|.
name|MB
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
specifier|public
specifier|static
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|ClientProtocol
argument_list|>
DECL|field|OZONE_CLIENT_PROTOCOL_RPC
name|OZONE_CLIENT_PROTOCOL_RPC
init|=
name|RpcClient
operator|.
name|class
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|ClientProtocol
argument_list|>
DECL|field|OZONE_CLIENT_PROTOCOL_REST
name|OZONE_CLIENT_PROTOCOL_REST
init|=
name|RestClient
operator|.
name|class
decl_stmt|;
DECL|field|OZONE_CLIENT_SOCKET_TIMEOUT_MS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_SOCKET_TIMEOUT_MS
init|=
literal|"ozone.client.socket.timeout.ms"
decl_stmt|;
DECL|field|OZONE_CLIENT_SOCKET_TIMEOUT_MS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_CLIENT_SOCKET_TIMEOUT_MS_DEFAULT
init|=
literal|5000
decl_stmt|;
DECL|field|OZONE_CLIENT_CONNECTION_TIMEOUT_MS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_CLIENT_CONNECTION_TIMEOUT_MS
init|=
literal|"ozone.client.connection.timeout.ms"
decl_stmt|;
DECL|field|OZONE_CLIENT_CONNECTION_TIMEOUT_MS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_CLIENT_CONNECTION_TIMEOUT_MS_DEFAULT
init|=
literal|5000
decl_stmt|;
comment|/**    * Configuration properties for Ozone Block Deleting Service.    */
DECL|field|OZONE_BLOCK_DELETING_SERVICE_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL_MS
init|=
literal|"ozone.block.deleting.service.interval.ms"
decl_stmt|;
DECL|field|OZONE_BLOCK_DELETING_SERVICE_INTERVAL_MS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL_MS_DEFAULT
init|=
literal|60000
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
name|int
name|OZONE_BLOCK_DELETING_SERVICE_TIMEOUT_DEFAULT
init|=
literal|300000
decl_stmt|;
comment|// 300s for default
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
comment|/** A unique ID to identify a Ratis server. */
DECL|field|DFS_CONTAINER_RATIS_SERVER_ID
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_RATIS_SERVER_ID
init|=
literal|"dfs.container.ratis.server.id"
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
DECL|field|OZONE_SCM_WEB_AUTHENTICATION_KERBEROS_PRINCIPAL
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_WEB_AUTHENTICATION_KERBEROS_PRINCIPAL
init|=
literal|"ozone.web.authentication.kerberos.principal"
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

