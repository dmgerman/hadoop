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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
name|proto
operator|.
name|RouterProtocolProtos
operator|.
name|RouterAdminProtocolService
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
name|protocolPB
operator|.
name|RouterAdminProtocolPB
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
name|protocolPB
operator|.
name|RouterAdminProtocolServerSideTranslatorPB
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
name|MountTableManager
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
name|MountTableStore
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
name|protocol
operator|.
name|AddMountTableEntryRequest
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
name|protocol
operator|.
name|AddMountTableEntryResponse
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
name|protocol
operator|.
name|GetMountTableEntriesRequest
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
name|protocol
operator|.
name|GetMountTableEntriesResponse
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
name|protocol
operator|.
name|RemoveMountTableEntryRequest
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
name|protocol
operator|.
name|RemoveMountTableEntryResponse
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
name|protocol
operator|.
name|UpdateMountTableEntryRequest
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
name|protocol
operator|.
name|UpdateMountTableEntryResponse
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
name|ipc
operator|.
name|ProtobufRpcEngine
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
name|ipc
operator|.
name|RPC
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
name|ipc
operator|.
name|RPC
operator|.
name|Server
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
name|service
operator|.
name|AbstractService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|BlockingService
import|;
end_import

begin_comment
comment|/**  * This class is responsible for handling all of the Admin calls to the HDFS  * router. It is created, started, and stopped by {@link Router}.  */
end_comment

begin_class
DECL|class|RouterAdminServer
specifier|public
class|class
name|RouterAdminServer
extends|extends
name|AbstractService
implements|implements
name|MountTableManager
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RouterAdminServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|router
specifier|private
specifier|final
name|Router
name|router
decl_stmt|;
DECL|field|mountTableStore
specifier|private
name|MountTableStore
name|mountTableStore
decl_stmt|;
comment|/** The Admin server that listens to requests from clients. */
DECL|field|adminServer
specifier|private
specifier|final
name|Server
name|adminServer
decl_stmt|;
DECL|field|adminAddress
specifier|private
specifier|final
name|InetSocketAddress
name|adminAddress
decl_stmt|;
DECL|method|RouterAdminServer (Configuration conf, Router router)
specifier|public
name|RouterAdminServer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Router
name|router
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|RouterAdminServer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|router
operator|=
name|router
expr_stmt|;
name|int
name|handlerCount
init|=
name|this
operator|.
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_ROUTER_ADMIN_HANDLER_COUNT_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_ROUTER_ADMIN_HANDLER_COUNT_DEFAULT
argument_list|)
decl_stmt|;
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|this
operator|.
name|conf
argument_list|,
name|RouterAdminProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|RouterAdminProtocolServerSideTranslatorPB
name|routerAdminProtocolTranslator
init|=
operator|new
name|RouterAdminProtocolServerSideTranslatorPB
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|BlockingService
name|clientNNPbService
init|=
name|RouterAdminProtocolService
operator|.
name|newReflectiveBlockingService
argument_list|(
name|routerAdminProtocolTranslator
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|confRpcAddress
init|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_ROUTER_ADMIN_BIND_HOST_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_ROUTER_ADMIN_ADDRESS_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_ROUTER_ADMIN_ADDRESS_DEFAULT
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_ROUTER_ADMIN_PORT_DEFAULT
argument_list|)
decl_stmt|;
name|String
name|bindHost
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_ROUTER_ADMIN_BIND_HOST_KEY
argument_list|,
name|confRpcAddress
operator|.
name|getHostName
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Admin server binding to {}:{}"
argument_list|,
name|bindHost
argument_list|,
name|confRpcAddress
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|adminServer
operator|=
operator|new
name|RPC
operator|.
name|Builder
argument_list|(
name|this
operator|.
name|conf
argument_list|)
operator|.
name|setProtocol
argument_list|(
name|RouterAdminProtocolPB
operator|.
name|class
argument_list|)
operator|.
name|setInstance
argument_list|(
name|clientNNPbService
argument_list|)
operator|.
name|setBindAddress
argument_list|(
name|bindHost
argument_list|)
operator|.
name|setPort
argument_list|(
name|confRpcAddress
operator|.
name|getPort
argument_list|()
argument_list|)
operator|.
name|setNumHandlers
argument_list|(
name|handlerCount
argument_list|)
operator|.
name|setVerbose
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// The RPC-server port can be ephemeral... ensure we have the correct info
name|InetSocketAddress
name|listenAddress
init|=
name|this
operator|.
name|adminServer
operator|.
name|getListenerAddress
argument_list|()
decl_stmt|;
name|this
operator|.
name|adminAddress
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|confRpcAddress
operator|.
name|getHostName
argument_list|()
argument_list|,
name|listenAddress
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|router
operator|.
name|setAdminServerAddress
argument_list|(
name|this
operator|.
name|adminAddress
argument_list|)
expr_stmt|;
block|}
comment|/** Allow access to the client RPC server for testing. */
annotation|@
name|VisibleForTesting
DECL|method|getAdminServer ()
name|Server
name|getAdminServer
parameter_list|()
block|{
return|return
name|this
operator|.
name|adminServer
return|;
block|}
DECL|method|getMountTableStore ()
specifier|private
name|MountTableStore
name|getMountTableStore
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|mountTableStore
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|mountTableStore
operator|=
name|router
operator|.
name|getStateStore
argument_list|()
operator|.
name|getRegisteredRecordStore
argument_list|(
name|MountTableStore
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|mountTableStore
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mount table state store is not available."
argument_list|)
throw|;
block|}
block|}
return|return
name|this
operator|.
name|mountTableStore
return|;
block|}
comment|/**    * Get the RPC address of the admin service.    * @return Administration service RPC address.    */
DECL|method|getRpcAddress ()
specifier|public
name|InetSocketAddress
name|getRpcAddress
parameter_list|()
block|{
return|return
name|this
operator|.
name|adminAddress
return|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration configuration)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|conf
operator|=
name|configuration
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|adminServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|this
operator|.
name|adminServer
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|adminServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addMountTableEntry ( AddMountTableEntryRequest request)
specifier|public
name|AddMountTableEntryResponse
name|addMountTableEntry
parameter_list|(
name|AddMountTableEntryRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getMountTableStore
argument_list|()
operator|.
name|addMountTableEntry
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|updateMountTableEntry ( UpdateMountTableEntryRequest request)
specifier|public
name|UpdateMountTableEntryResponse
name|updateMountTableEntry
parameter_list|(
name|UpdateMountTableEntryRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getMountTableStore
argument_list|()
operator|.
name|updateMountTableEntry
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|removeMountTableEntry ( RemoveMountTableEntryRequest request)
specifier|public
name|RemoveMountTableEntryResponse
name|removeMountTableEntry
parameter_list|(
name|RemoveMountTableEntryRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getMountTableStore
argument_list|()
operator|.
name|removeMountTableEntry
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMountTableEntries ( GetMountTableEntriesRequest request)
specifier|public
name|GetMountTableEntriesResponse
name|getMountTableEntries
parameter_list|(
name|GetMountTableEntriesRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getMountTableStore
argument_list|()
operator|.
name|getMountTableEntries
argument_list|(
name|request
argument_list|)
return|;
block|}
block|}
end_class

end_unit

