begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cblock
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Thread
operator|.
name|NORM_PRIORITY
import|;
end_import

begin_comment
comment|/**  * This class contains constants for configuration keys used in CBlock.  */
end_comment

begin_class
DECL|class|CBlockConfigKeys
specifier|public
specifier|final
class|class
name|CBlockConfigKeys
block|{
DECL|field|DFS_CBLOCK_SERVICERPC_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_SERVICERPC_ADDRESS_KEY
init|=
literal|"dfs.cblock.servicerpc-address"
decl_stmt|;
DECL|field|DFS_CBLOCK_SERVICERPC_PORT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_SERVICERPC_PORT_KEY
init|=
literal|"dfs.cblock.servicerpc.port"
decl_stmt|;
DECL|field|DFS_CBLOCK_SERVICERPC_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CBLOCK_SERVICERPC_PORT_DEFAULT
init|=
literal|9810
decl_stmt|;
DECL|field|DFS_CBLOCK_SERVICERPC_HOSTNAME_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_SERVICERPC_HOSTNAME_KEY
init|=
literal|"dfs.cblock.servicerpc.hostname"
decl_stmt|;
DECL|field|DFS_CBLOCK_SERVICERPC_HOSTNAME_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_SERVICERPC_HOSTNAME_DEFAULT
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|DFS_CBLOCK_SERVICERPC_ADDRESS_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_SERVICERPC_ADDRESS_DEFAULT
init|=
name|DFS_CBLOCK_SERVICERPC_HOSTNAME_DEFAULT
operator|+
literal|":"
operator|+
name|DFS_CBLOCK_SERVICERPC_PORT_DEFAULT
decl_stmt|;
DECL|field|DFS_CBLOCK_JSCSIRPC_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_JSCSIRPC_ADDRESS_KEY
init|=
literal|"dfs.cblock.jscsi-address"
decl_stmt|;
comment|//The port on CBlockManager node for jSCSI to ask
DECL|field|DFS_CBLOCK_JSCSI_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CBLOCK_JSCSI_PORT_DEFAULT
init|=
literal|9811
decl_stmt|;
DECL|field|DFS_CBLOCK_JSCSIRPC_ADDRESS_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_JSCSIRPC_ADDRESS_DEFAULT
init|=
name|DFS_CBLOCK_SERVICERPC_HOSTNAME_DEFAULT
operator|+
literal|":"
operator|+
name|DFS_CBLOCK_JSCSI_PORT_DEFAULT
decl_stmt|;
DECL|field|DFS_CBLOCK_SERVICERPC_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_SERVICERPC_BIND_HOST_KEY
init|=
literal|"dfs.cblock.service.rpc-bind-host"
decl_stmt|;
DECL|field|DFS_CBLOCK_JSCSIRPC_BIND_HOST_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_JSCSIRPC_BIND_HOST_KEY
init|=
literal|"dfs.cblock.jscsi.rpc-bind-host"
decl_stmt|;
comment|// default block size is 4KB
DECL|field|DFS_CBLOCK_SERVICE_BLOCK_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CBLOCK_SERVICE_BLOCK_SIZE_DEFAULT
init|=
literal|4096
decl_stmt|;
DECL|field|DFS_CBLOCK_SERVICERPC_HANDLER_COUNT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_SERVICERPC_HANDLER_COUNT_KEY
init|=
literal|"dfs.storage.service.handler.count"
decl_stmt|;
DECL|field|DFS_CBLOCK_SERVICERPC_HANDLER_COUNT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CBLOCK_SERVICERPC_HANDLER_COUNT_DEFAULT
init|=
literal|10
decl_stmt|;
DECL|field|DFS_CBLOCK_SERVICE_LEVELDB_PATH_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_SERVICE_LEVELDB_PATH_KEY
init|=
literal|"dfs.cblock.service.leveldb.path"
decl_stmt|;
comment|//TODO : find a better place
DECL|field|DFS_CBLOCK_SERVICE_LEVELDB_PATH_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_SERVICE_LEVELDB_PATH_DEFAULT
init|=
literal|"/tmp/cblock_levelDB.dat"
decl_stmt|;
DECL|field|DFS_CBLOCK_DISK_CACHE_PATH_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_DISK_CACHE_PATH_KEY
init|=
literal|"dfs.cblock.disk.cache.path"
decl_stmt|;
DECL|field|DFS_CBLOCK_DISK_CACHE_PATH_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_DISK_CACHE_PATH_DEFAULT
init|=
literal|"/tmp/cblockCacheDB"
decl_stmt|;
comment|/**    * Setting this flag to true makes the block layer compute a sha256 hash of    * the data and log that information along with block ID. This is very    * useful for doing trace based simulation of various workloads. Since it is    * computing a hash for each block this could be expensive, hence default    * is false.    */
DECL|field|DFS_CBLOCK_TRACE_IO
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_TRACE_IO
init|=
literal|"dfs.cblock.trace.io"
decl_stmt|;
DECL|field|DFS_CBLOCK_TRACE_IO_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|DFS_CBLOCK_TRACE_IO_DEFAULT
init|=
literal|false
decl_stmt|;
DECL|field|DFS_CBLOCK_ENABLE_SHORT_CIRCUIT_IO
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_ENABLE_SHORT_CIRCUIT_IO
init|=
literal|"dfs.cblock.short.circuit.io"
decl_stmt|;
DECL|field|DFS_CBLOCK_ENABLE_SHORT_CIRCUIT_IO_DEFAULT
specifier|public
specifier|static
specifier|final
name|boolean
name|DFS_CBLOCK_ENABLE_SHORT_CIRCUIT_IO_DEFAULT
init|=
literal|false
decl_stmt|;
comment|/**    * Cache size in 1000s of entries. 256 indicates 256 * 1024.    */
DECL|field|DFS_CBLOCK_CACHE_QUEUE_SIZE_KB
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_CACHE_QUEUE_SIZE_KB
init|=
literal|"dfs.cblock.cache.cache.size.in.kb"
decl_stmt|;
DECL|field|DFS_CBLOCK_CACHE_QUEUE_SIZE_KB_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CBLOCK_CACHE_QUEUE_SIZE_KB_DEFAULT
init|=
literal|256
decl_stmt|;
comment|/**    *  Minimum Number of threads that cache pool will use for background I/O.    */
DECL|field|DFS_CBLOCK_CACHE_CORE_POOL_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_CACHE_CORE_POOL_SIZE
init|=
literal|"dfs.cblock.cache.core.pool.size"
decl_stmt|;
DECL|field|DFS_CBLOCK_CACHE_CORE_POOL_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CBLOCK_CACHE_CORE_POOL_SIZE_DEFAULT
init|=
literal|16
decl_stmt|;
comment|/**    *  Maximum Number of threads that cache pool will use for background I/O.    */
DECL|field|DFS_CBLOCK_CACHE_MAX_POOL_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_CACHE_MAX_POOL_SIZE
init|=
literal|"dfs.cblock.cache.max.pool.size"
decl_stmt|;
DECL|field|DFS_CBLOCK_CACHE_MAX_POOL_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CBLOCK_CACHE_MAX_POOL_SIZE_DEFAULT
init|=
literal|256
decl_stmt|;
comment|/**    * Number of seconds to keep the Thread alive when it is idle.    */
DECL|field|DFS_CBLOCK_CACHE_KEEP_ALIVE_SECONDS
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_CACHE_KEEP_ALIVE_SECONDS
init|=
literal|"dfs.cblock.cache.keep.alive.seconds"
decl_stmt|;
DECL|field|DFS_CBLOCK_CACHE_KEEP_ALIVE_SECONDS_DEFAULT
specifier|public
specifier|static
specifier|final
name|long
name|DFS_CBLOCK_CACHE_KEEP_ALIVE_SECONDS_DEFAULT
init|=
literal|60
decl_stmt|;
comment|/**    * Priority of cache flusher thread, affecting the relative performance of    * write and read.    */
DECL|field|DFS_CBLOCK_CACHE_THREAD_PRIORITY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_CACHE_THREAD_PRIORITY
init|=
literal|"dfs.cblock.cache.thread.priority"
decl_stmt|;
DECL|field|DFS_CBLOCK_CACHE_THREAD_PRIORITY_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CBLOCK_CACHE_THREAD_PRIORITY_DEFAULT
init|=
name|NORM_PRIORITY
decl_stmt|;
comment|/**    * Block Buffer size in terms of blockID entries, 512 means 512 blockIDs.    */
DECL|field|DFS_CBLOCK_CACHE_BLOCK_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_CACHE_BLOCK_BUFFER_SIZE
init|=
literal|"dfs.cblock.cache.block.buffer.size"
decl_stmt|;
DECL|field|DFS_CBLOCK_CACHE_BLOCK_BUFFER_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CBLOCK_CACHE_BLOCK_BUFFER_SIZE_DEFAULT
init|=
literal|512
decl_stmt|;
comment|// jscsi server settings
DECL|field|DFS_CBLOCK_JSCSI_SERVER_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_JSCSI_SERVER_ADDRESS_KEY
init|=
literal|"dfs.cblock.jscsi.server.address"
decl_stmt|;
DECL|field|DFS_CBLOCK_JSCSI_SERVER_ADDRESS_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_JSCSI_SERVER_ADDRESS_DEFAULT
init|=
literal|"127.0.0.1"
decl_stmt|;
DECL|field|DFS_CBLOCK_JSCSI_CBLOCK_SERVER_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_JSCSI_CBLOCK_SERVER_ADDRESS_KEY
init|=
literal|"dfs.cblock.jscsi.cblock.server.address"
decl_stmt|;
DECL|field|DFS_CBLOCK_JSCSI_CBLOCK_SERVER_ADDRESS_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_JSCSI_CBLOCK_SERVER_ADDRESS_DEFAULT
init|=
literal|"127.0.0.1"
decl_stmt|;
DECL|field|DFS_CBLOCK_CONTAINER_SIZE_GB_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_CONTAINER_SIZE_GB_KEY
init|=
literal|"dfs.cblock.container.size.gb"
decl_stmt|;
DECL|field|DFS_CBLOCK_CONTAINER_SIZE_GB_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CBLOCK_CONTAINER_SIZE_GB_DEFAULT
init|=
literal|5
decl_stmt|;
DECL|method|CBlockConfigKeys ()
specifier|private
name|CBlockConfigKeys
parameter_list|()
block|{    }
block|}
end_class

end_unit

