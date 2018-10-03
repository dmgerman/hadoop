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
name|DFS_PERMISSIONS_ENABLED_DEFAULT
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
name|DFS_PERMISSIONS_ENABLED_KEY
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Set
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
name|base
operator|.
name|Preconditions
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
name|HdfsConstants
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
name|HdfsFileStatus
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
name|FederationNamespaceInfo
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
name|DisabledNameserviceStore
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
name|DisableNameserviceRequest
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
name|DisableNameserviceResponse
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
name|EnableNameserviceRequest
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
name|EnableNameserviceResponse
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
name|EnterSafeModeRequest
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
name|EnterSafeModeResponse
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
name|GetDisabledNameservicesRequest
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
name|GetDisabledNameservicesResponse
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
name|GetSafeModeRequest
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
name|GetSafeModeResponse
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
name|LeaveSafeModeRequest
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
name|LeaveSafeModeResponse
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|MountTable
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
name|namenode
operator|.
name|NameNode
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
name|security
operator|.
name|AccessControlException
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
name|UserGroupInformation
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
implements|,
name|RouterStateManager
implements|,
name|NameserviceManager
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
DECL|field|disabledStore
specifier|private
name|DisabledNameserviceStore
name|disabledStore
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
comment|/**    * Permission related info used for constructing new router permission    * checker instance.    */
DECL|field|routerOwner
specifier|private
specifier|static
name|String
name|routerOwner
decl_stmt|;
DECL|field|superGroup
specifier|private
specifier|static
name|String
name|superGroup
decl_stmt|;
DECL|field|isPermissionEnabled
specifier|private
specifier|static
name|boolean
name|isPermissionEnabled
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
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_ADMIN_HANDLER_COUNT_KEY
argument_list|,
name|RBFConfigKeys
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
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_ADMIN_BIND_HOST_KEY
argument_list|,
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_ADMIN_ADDRESS_KEY
argument_list|,
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_ADMIN_ADDRESS_DEFAULT
argument_list|,
name|RBFConfigKeys
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
name|RBFConfigKeys
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
name|initializePermissionSettings
argument_list|(
name|this
operator|.
name|conf
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
comment|/**    * Initialize permission related settings.    *    * @param routerConf    * @throws IOException    */
DECL|method|initializePermissionSettings (Configuration routerConf)
specifier|private
specifier|static
name|void
name|initializePermissionSettings
parameter_list|(
name|Configuration
name|routerConf
parameter_list|)
throws|throws
name|IOException
block|{
name|routerOwner
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
expr_stmt|;
name|superGroup
operator|=
name|routerConf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PERMISSIONS_SUPERUSERGROUP_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_PERMISSIONS_SUPERUSERGROUP_DEFAULT
argument_list|)
expr_stmt|;
name|isPermissionEnabled
operator|=
name|routerConf
operator|.
name|getBoolean
argument_list|(
name|DFS_PERMISSIONS_ENABLED_KEY
argument_list|,
name|DFS_PERMISSIONS_ENABLED_DEFAULT
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
DECL|method|getDisabledNameserviceStore ()
specifier|private
name|DisabledNameserviceStore
name|getDisabledNameserviceStore
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|disabledStore
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|disabledStore
operator|=
name|router
operator|.
name|getStateStore
argument_list|()
operator|.
name|getRegisteredRecordStore
argument_list|(
name|DisabledNameserviceStore
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|disabledStore
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Disabled Nameservice state store is not available."
argument_list|)
throw|;
block|}
block|}
return|return
name|this
operator|.
name|disabledStore
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
name|UpdateMountTableEntryResponse
name|response
init|=
name|getMountTableStore
argument_list|()
operator|.
name|updateMountTableEntry
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|MountTable
name|mountTable
init|=
name|request
operator|.
name|getEntry
argument_list|()
decl_stmt|;
if|if
condition|(
name|mountTable
operator|!=
literal|null
condition|)
block|{
name|synchronizeQuota
argument_list|(
name|mountTable
argument_list|)
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
comment|/**    * Synchronize the quota value across mount table and subclusters.    * @param mountTable Quota set in given mount table.    * @throws IOException    */
DECL|method|synchronizeQuota (MountTable mountTable)
specifier|private
name|void
name|synchronizeQuota
parameter_list|(
name|MountTable
name|mountTable
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|path
init|=
name|mountTable
operator|.
name|getSourcePath
argument_list|()
decl_stmt|;
name|long
name|nsQuota
init|=
name|mountTable
operator|.
name|getQuota
argument_list|()
operator|.
name|getQuota
argument_list|()
decl_stmt|;
name|long
name|ssQuota
init|=
name|mountTable
operator|.
name|getQuota
argument_list|()
operator|.
name|getSpaceQuota
argument_list|()
decl_stmt|;
if|if
condition|(
name|nsQuota
operator|!=
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
operator|||
name|ssQuota
operator|!=
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
condition|)
block|{
name|HdfsFileStatus
name|ret
init|=
name|this
operator|.
name|router
operator|.
name|getRpcServer
argument_list|()
operator|.
name|getFileInfo
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|router
operator|.
name|getRpcServer
argument_list|()
operator|.
name|getQuotaModule
argument_list|()
operator|.
name|setQuota
argument_list|(
name|path
argument_list|,
name|nsQuota
argument_list|,
name|ssQuota
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
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
annotation|@
name|Override
DECL|method|enterSafeMode (EnterSafeModeRequest request)
specifier|public
name|EnterSafeModeResponse
name|enterSafeMode
parameter_list|(
name|EnterSafeModeRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|RouterSafemodeService
name|safeModeService
init|=
name|this
operator|.
name|router
operator|.
name|getSafemodeService
argument_list|()
decl_stmt|;
if|if
condition|(
name|safeModeService
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|router
operator|.
name|updateRouterState
argument_list|(
name|RouterServiceState
operator|.
name|SAFEMODE
argument_list|)
expr_stmt|;
name|safeModeService
operator|.
name|setManualSafeMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|success
operator|=
name|verifySafeMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"STATE* Safe mode is ON.\n"
operator|+
literal|"It was turned on manually. "
operator|+
literal|"Use \"hdfs dfsrouteradmin -safemode leave\" to turn"
operator|+
literal|" safe mode off."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to enter safemode."
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|EnterSafeModeResponse
operator|.
name|newInstance
argument_list|(
name|success
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|leaveSafeMode (LeaveSafeModeRequest request)
specifier|public
name|LeaveSafeModeResponse
name|leaveSafeMode
parameter_list|(
name|LeaveSafeModeRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|RouterSafemodeService
name|safeModeService
init|=
name|this
operator|.
name|router
operator|.
name|getSafemodeService
argument_list|()
decl_stmt|;
if|if
condition|(
name|safeModeService
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|router
operator|.
name|updateRouterState
argument_list|(
name|RouterServiceState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|safeModeService
operator|.
name|setManualSafeMode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|success
operator|=
name|verifySafeMode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"STATE* Safe mode is OFF.\n"
operator|+
literal|"It was turned off manually."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to leave safemode."
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|LeaveSafeModeResponse
operator|.
name|newInstance
argument_list|(
name|success
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSafeMode (GetSafeModeRequest request)
specifier|public
name|GetSafeModeResponse
name|getSafeMode
parameter_list|(
name|GetSafeModeRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|isInSafeMode
init|=
literal|false
decl_stmt|;
name|RouterSafemodeService
name|safeModeService
init|=
name|this
operator|.
name|router
operator|.
name|getSafemodeService
argument_list|()
decl_stmt|;
if|if
condition|(
name|safeModeService
operator|!=
literal|null
condition|)
block|{
name|isInSafeMode
operator|=
name|safeModeService
operator|.
name|isInSafeMode
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Safemode status retrieved successfully."
argument_list|)
expr_stmt|;
block|}
return|return
name|GetSafeModeResponse
operator|.
name|newInstance
argument_list|(
name|isInSafeMode
argument_list|)
return|;
block|}
comment|/**    * Verify if Router set safe mode state correctly.    * @param isInSafeMode Expected state to be set.    * @return    */
DECL|method|verifySafeMode (boolean isInSafeMode)
specifier|private
name|boolean
name|verifySafeMode
parameter_list|(
name|boolean
name|isInSafeMode
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|this
operator|.
name|router
operator|.
name|getSafemodeService
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|serverInSafeMode
init|=
name|this
operator|.
name|router
operator|.
name|getSafemodeService
argument_list|()
operator|.
name|isInSafeMode
argument_list|()
decl_stmt|;
name|RouterServiceState
name|currentState
init|=
name|this
operator|.
name|router
operator|.
name|getRouterState
argument_list|()
decl_stmt|;
return|return
operator|(
name|isInSafeMode
operator|&&
name|currentState
operator|==
name|RouterServiceState
operator|.
name|SAFEMODE
operator|&&
name|serverInSafeMode
operator|)
operator|||
operator|(
operator|!
name|isInSafeMode
operator|&&
name|currentState
operator|!=
name|RouterServiceState
operator|.
name|SAFEMODE
operator|&&
operator|!
name|serverInSafeMode
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|disableNameservice ( DisableNameserviceRequest request)
specifier|public
name|DisableNameserviceResponse
name|disableNameservice
parameter_list|(
name|DisableNameserviceRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|RouterPermissionChecker
name|pc
init|=
name|getPermissionChecker
argument_list|()
decl_stmt|;
if|if
condition|(
name|pc
operator|!=
literal|null
condition|)
block|{
name|pc
operator|.
name|checkSuperuserPrivilege
argument_list|()
expr_stmt|;
block|}
name|String
name|nsId
init|=
name|request
operator|.
name|getNameServiceId
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|namespaceExists
argument_list|(
name|nsId
argument_list|)
condition|)
block|{
name|success
operator|=
name|getDisabledNameserviceStore
argument_list|()
operator|.
name|disableNameservice
argument_list|(
name|nsId
argument_list|)
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Nameservice {} disabled successfully."
argument_list|,
name|nsId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to disable Nameservice {}"
argument_list|,
name|nsId
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot disable {}, it does not exists"
argument_list|,
name|nsId
argument_list|)
expr_stmt|;
block|}
return|return
name|DisableNameserviceResponse
operator|.
name|newInstance
argument_list|(
name|success
argument_list|)
return|;
block|}
DECL|method|namespaceExists (final String nsId)
specifier|private
name|boolean
name|namespaceExists
parameter_list|(
specifier|final
name|String
name|nsId
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|ActiveNamenodeResolver
name|resolver
init|=
name|router
operator|.
name|getNamenodeResolver
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|FederationNamespaceInfo
argument_list|>
name|nss
init|=
name|resolver
operator|.
name|getNamespaces
argument_list|()
decl_stmt|;
for|for
control|(
name|FederationNamespaceInfo
name|ns
range|:
name|nss
control|)
block|{
if|if
condition|(
name|nsId
operator|.
name|equals
argument_list|(
name|ns
operator|.
name|getNameserviceId
argument_list|()
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
return|return
name|found
return|;
block|}
annotation|@
name|Override
DECL|method|enableNameservice ( EnableNameserviceRequest request)
specifier|public
name|EnableNameserviceResponse
name|enableNameservice
parameter_list|(
name|EnableNameserviceRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|RouterPermissionChecker
name|pc
init|=
name|getPermissionChecker
argument_list|()
decl_stmt|;
if|if
condition|(
name|pc
operator|!=
literal|null
condition|)
block|{
name|pc
operator|.
name|checkSuperuserPrivilege
argument_list|()
expr_stmt|;
block|}
name|String
name|nsId
init|=
name|request
operator|.
name|getNameServiceId
argument_list|()
decl_stmt|;
name|DisabledNameserviceStore
name|store
init|=
name|getDisabledNameserviceStore
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|disabled
init|=
name|store
operator|.
name|getDisabledNameservices
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|disabled
operator|.
name|contains
argument_list|(
name|nsId
argument_list|)
condition|)
block|{
name|success
operator|=
name|store
operator|.
name|enableNameservice
argument_list|(
name|nsId
argument_list|)
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Nameservice {} enabled successfully."
argument_list|,
name|nsId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to enable Nameservice {}"
argument_list|,
name|nsId
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot enable {}, it was not disabled"
argument_list|,
name|nsId
argument_list|)
expr_stmt|;
block|}
return|return
name|EnableNameserviceResponse
operator|.
name|newInstance
argument_list|(
name|success
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDisabledNameservices ( GetDisabledNameservicesRequest request)
specifier|public
name|GetDisabledNameservicesResponse
name|getDisabledNameservices
parameter_list|(
name|GetDisabledNameservicesRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|nsIds
init|=
name|getDisabledNameserviceStore
argument_list|()
operator|.
name|getDisabledNameservices
argument_list|()
decl_stmt|;
return|return
name|GetDisabledNameservicesResponse
operator|.
name|newInstance
argument_list|(
name|nsIds
argument_list|)
return|;
block|}
comment|/**    * Get a new permission checker used for making mount table access    * control. This method will be invoked during each RPC call in router    * admin server.    *    * @return Router permission checker.    * @throws AccessControlException If the user is not authorized.    */
DECL|method|getPermissionChecker ()
specifier|public
specifier|static
name|RouterPermissionChecker
name|getPermissionChecker
parameter_list|()
throws|throws
name|AccessControlException
block|{
if|if
condition|(
operator|!
name|isPermissionEnabled
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
return|return
operator|new
name|RouterPermissionChecker
argument_list|(
name|routerOwner
argument_list|,
name|superGroup
argument_list|,
name|NameNode
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Get super user name.    *    * @return String super user name.    */
DECL|method|getSuperUser ()
specifier|public
specifier|static
name|String
name|getSuperUser
parameter_list|()
block|{
return|return
name|routerOwner
return|;
block|}
comment|/**    * Get super group name.    *    * @return String super group name.    */
DECL|method|getSuperGroup ()
specifier|public
specifier|static
name|String
name|getSuperGroup
parameter_list|()
block|{
return|return
name|superGroup
return|;
block|}
block|}
end_class

end_unit

