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
DECL|field|DFS_CBLOCK_ENABLED_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_ENABLED_KEY
init|=
literal|"dfs.cblock.enabled"
decl_stmt|;
DECL|field|DFS_CBLOCK_SERVICERPC_ADDRESS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_SERVICERPC_ADDRESS_KEY
init|=
literal|"dfs.cblock.servicerpc-address"
decl_stmt|;
DECL|field|DFS_CBLOCK_RPCSERVICE_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CBLOCK_RPCSERVICE_PORT_DEFAULT
init|=
literal|9810
decl_stmt|;
DECL|field|DFS_CBLOCK_RPCSERVICE_IP_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CBLOCK_RPCSERVICE_IP_DEFAULT
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
name|DFS_CBLOCK_RPCSERVICE_IP_DEFAULT
operator|+
literal|":"
operator|+
name|DFS_CBLOCK_RPCSERVICE_PORT_DEFAULT
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
name|DFS_CBLOCK_RPCSERVICE_IP_DEFAULT
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
DECL|method|CBlockConfigKeys ()
specifier|private
name|CBlockConfigKeys
parameter_list|()
block|{    }
block|}
end_class

end_unit

