begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.scm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
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
literal|50011
decl_stmt|;
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
name|int
name|SCM_CONTAINER_CLIENT_STALE_THRESHOLD_DEFAULT
init|=
literal|10000
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
comment|// 1 MB by default
DECL|field|OZONE_SCM_CHUNK_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_SCM_CHUNK_SIZE_DEFAULT
init|=
literal|1
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
literal|1
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
DECL|field|OZONE_SCM_HEARTBEAT_INTERVAL_SECONDS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_HEARTBEAT_INTERVAL_SECONDS
init|=
literal|"ozone.scm.heartbeat.interval.seconds"
decl_stmt|;
DECL|field|OZONE_SCM_HEARBEAT_INTERVAL_SECONDS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_SCM_HEARBEAT_INTERVAL_SECONDS_DEFAULT
init|=
literal|30
decl_stmt|;
DECL|field|OZONE_SCM_DEADNODE_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_DEADNODE_INTERVAL_MS
init|=
literal|"ozone.scm.dead.node.interval.ms"
decl_stmt|;
DECL|field|OZONE_SCM_DEADNODE_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|OZONE_SCM_DEADNODE_INTERVAL_DEFAULT
init|=
name|OZONE_SCM_HEARBEAT_INTERVAL_SECONDS_DEFAULT
operator|*
literal|1000L
operator|*
literal|20L
decl_stmt|;
DECL|field|OZONE_SCM_MAX_HB_COUNT_TO_PROCESS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_MAX_HB_COUNT_TO_PROCESS
init|=
literal|"ozone.scm.max.hb.count.to.process"
decl_stmt|;
DECL|field|OZONE_SCM_MAX_HB_COUNT_TO_PROCESS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_SCM_MAX_HB_COUNT_TO_PROCESS_DEFAULT
init|=
literal|5000
decl_stmt|;
DECL|field|OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL_MS
init|=
literal|"ozone.scm.heartbeat.thread.interval.ms"
decl_stmt|;
DECL|field|OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL_MS_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|OZONE_SCM_HEARTBEAT_PROCESS_INTERVAL_MS_DEFAULT
init|=
literal|3000
decl_stmt|;
DECL|field|OZONE_SCM_STALENODE_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_STALENODE_INTERVAL_MS
init|=
literal|"ozone.scm.stale.node.interval.ms"
decl_stmt|;
DECL|field|OZONE_SCM_STALENODE_INTERVAL_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|OZONE_SCM_STALENODE_INTERVAL_DEFAULT
init|=
name|OZONE_SCM_HEARBEAT_INTERVAL_SECONDS_DEFAULT
operator|*
literal|1000L
operator|*
literal|3L
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
literal|100
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
literal|9862
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
DECL|field|OZONE_SCM_CONTAINER_SIZE_GB
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_CONTAINER_SIZE_GB
init|=
literal|"ozone.scm.container.size.gb"
decl_stmt|;
DECL|field|OZONE_SCM_CONTAINER_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|OZONE_SCM_CONTAINER_SIZE_DEFAULT
init|=
literal|5
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
literal|10
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

