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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
operator|.
name|meta
operator|.
name|VolumeDescriptor
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
name|cblock
operator|.
name|meta
operator|.
name|VolumeInfo
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
name|cblock
operator|.
name|proto
operator|.
name|CBlockClientProtocol
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
name|cblock
operator|.
name|proto
operator|.
name|CBlockServiceProtocol
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
name|cblock
operator|.
name|proto
operator|.
name|MountVolumeResponse
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
name|cblock
operator|.
name|protocol
operator|.
name|proto
operator|.
name|CBlockClientServerProtocolProtos
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
name|cblock
operator|.
name|protocol
operator|.
name|proto
operator|.
name|CBlockServiceProtocolProtos
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
name|cblock
operator|.
name|protocolPB
operator|.
name|CBlockClientServerProtocolPB
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
name|cblock
operator|.
name|protocolPB
operator|.
name|CBlockClientServerProtocolServerSideTranslatorPB
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
name|cblock
operator|.
name|protocolPB
operator|.
name|CBlockServiceProtocolPB
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
name|cblock
operator|.
name|protocolPB
operator|.
name|CBlockServiceProtocolServerSideTranslatorPB
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
name|Client
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
name|OzoneConfigKeys
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
name|OzoneConsts
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
name|XceiverClientManager
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
name|client
operator|.
name|ContainerOperationClient
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
name|client
operator|.
name|ScmClient
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
name|cblock
operator|.
name|storage
operator|.
name|StorageManager
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
name|cblock
operator|.
name|util
operator|.
name|KeyUtil
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
name|net
operator|.
name|NetUtils
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
name|OzoneConfiguration
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
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
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
name|protocolPB
operator|.
name|StorageContainerLocationProtocolPB
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
name|utils
operator|.
name|LevelDBStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|DBIterator
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
name|java
operator|.
name|io
operator|.
name|File
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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_CONTAINER_SIZE_GB_DEFAULT
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_CONTAINER_SIZE_GB_KEY
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_JSCSIRPC_ADDRESS_DEFAULT
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_JSCSIRPC_ADDRESS_KEY
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_JSCSIRPC_BIND_HOST_KEY
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_SCM_IPADDRESS_DEFAULT
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_SCM_IPADDRESS_KEY
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_SCM_PORT_DEFAULT
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_SCM_PORT_KEY
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_SERVICERPC_ADDRESS_DEFAULT
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_SERVICERPC_ADDRESS_KEY
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_SERVICERPC_BIND_HOST_KEY
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_SERVICERPC_HANDLER_COUNT_DEFAULT
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_SERVICERPC_HANDLER_COUNT_KEY
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_SERVICE_LEVELDB_PATH_DEFAULT
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_SERVICE_LEVELDB_PATH_KEY
import|;
end_import

begin_comment
comment|/**  * The main entry point of CBlock operations, ALL the CBlock operations  * will go through this class. But NOTE that:  *  * volume operations (create/  * delete/info) are:  *    client -> CBlockManager -> StorageManager -> CBlock client  *  * IO operations (put/get block) are;  *    client -> CBlock client -> container  *  */
end_comment

begin_class
DECL|class|CBlockManager
specifier|public
class|class
name|CBlockManager
implements|implements
name|CBlockServiceProtocol
implements|,
name|CBlockClientProtocol
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
name|CBlockManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cblockService
specifier|private
specifier|final
name|RPC
operator|.
name|Server
name|cblockService
decl_stmt|;
DECL|field|cblockServer
specifier|private
specifier|final
name|RPC
operator|.
name|Server
name|cblockServer
decl_stmt|;
DECL|field|storageManager
specifier|private
specifier|final
name|StorageManager
name|storageManager
decl_stmt|;
DECL|field|levelDBStore
specifier|private
specifier|final
name|LevelDBStore
name|levelDBStore
decl_stmt|;
DECL|field|dbPath
specifier|private
specifier|final
name|String
name|dbPath
decl_stmt|;
DECL|field|encoding
specifier|private
name|Charset
name|encoding
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
DECL|method|CBlockManager (OzoneConfiguration conf, ScmClient storageClient)
specifier|public
name|CBlockManager
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|ScmClient
name|storageClient
parameter_list|)
throws|throws
name|IOException
block|{
name|storageManager
operator|=
operator|new
name|StorageManager
argument_list|(
name|storageClient
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|dbPath
operator|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|DFS_CBLOCK_SERVICE_LEVELDB_PATH_KEY
argument_list|,
name|DFS_CBLOCK_SERVICE_LEVELDB_PATH_DEFAULT
argument_list|)
expr_stmt|;
name|levelDBStore
operator|=
operator|new
name|LevelDBStore
argument_list|(
operator|new
name|File
argument_list|(
name|dbPath
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Try to load exising volume information"
argument_list|)
expr_stmt|;
name|readFromPersistentStore
argument_list|()
expr_stmt|;
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|CBlockServiceProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|CBlockClientServerProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// start service for client command-to-cblock server service
name|InetSocketAddress
name|serviceRpcAddr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|conf
operator|.
name|getTrimmed
argument_list|(
name|DFS_CBLOCK_SERVICERPC_ADDRESS_KEY
argument_list|,
name|DFS_CBLOCK_SERVICERPC_ADDRESS_DEFAULT
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|,
name|DFS_CBLOCK_SERVICERPC_ADDRESS_KEY
argument_list|)
decl_stmt|;
name|BlockingService
name|cblockProto
init|=
name|CBlockServiceProtocolProtos
operator|.
name|CBlockServiceProtocolService
operator|.
name|newReflectiveBlockingService
argument_list|(
operator|new
name|CBlockServiceProtocolServerSideTranslatorPB
argument_list|(
name|this
argument_list|)
argument_list|)
decl_stmt|;
name|cblockService
operator|=
name|startRpcServer
argument_list|(
name|conf
argument_list|,
name|CBlockServiceProtocolPB
operator|.
name|class
argument_list|,
name|cblockProto
argument_list|,
name|serviceRpcAddr
argument_list|,
name|DFS_CBLOCK_SERVICERPC_BIND_HOST_KEY
argument_list|,
name|DFS_CBLOCK_SERVICERPC_HANDLER_COUNT_KEY
argument_list|,
name|DFS_CBLOCK_SERVICERPC_HANDLER_COUNT_DEFAULT
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"CBlock manager listening for client commands on: {}"
argument_list|,
name|serviceRpcAddr
argument_list|)
expr_stmt|;
comment|// now start service for cblock client-to-cblock server communication
name|InetSocketAddress
name|serverRpcAddr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|DFS_CBLOCK_JSCSIRPC_ADDRESS_KEY
argument_list|,
name|DFS_CBLOCK_JSCSIRPC_ADDRESS_DEFAULT
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|,
name|DFS_CBLOCK_JSCSIRPC_ADDRESS_KEY
argument_list|)
decl_stmt|;
name|BlockingService
name|serverProto
init|=
name|CBlockClientServerProtocolProtos
operator|.
name|CBlockClientServerProtocolService
operator|.
name|newReflectiveBlockingService
argument_list|(
operator|new
name|CBlockClientServerProtocolServerSideTranslatorPB
argument_list|(
name|this
argument_list|)
argument_list|)
decl_stmt|;
name|cblockServer
operator|=
name|startRpcServer
argument_list|(
name|conf
argument_list|,
name|CBlockClientServerProtocolPB
operator|.
name|class
argument_list|,
name|serverProto
argument_list|,
name|serverRpcAddr
argument_list|,
name|DFS_CBLOCK_JSCSIRPC_BIND_HOST_KEY
argument_list|,
name|DFS_CBLOCK_SERVICERPC_HANDLER_COUNT_KEY
argument_list|,
name|DFS_CBLOCK_SERVICERPC_HANDLER_COUNT_DEFAULT
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"CBlock server listening for client commands on: {}"
argument_list|,
name|serverRpcAddr
argument_list|)
expr_stmt|;
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|cblockService
operator|.
name|start
argument_list|()
expr_stmt|;
name|cblockServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"CBlock manager started!"
argument_list|)
expr_stmt|;
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|cblockService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|cblockServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|join ()
specifier|public
name|void
name|join
parameter_list|()
block|{
try|try
block|{
name|cblockService
operator|.
name|join
argument_list|()
expr_stmt|;
name|cblockServer
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Interrupted during join"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Starts an RPC server, if configured.    *    * @param conf configuration    * @param protocol RPC protocol provided by RPC server    * @param instance RPC protocol implementation instance    * @param addr configured address of RPC server    * @param bindHostKey configuration key for setting explicit bind host.  If    *     the property is not configured, then the bind host is taken from addr.    * @param handlerCountKey configuration key for RPC server handler count    * @param handlerCountDefault default RPC server handler count if unconfigured    * @return RPC server, or null if addr is null    * @throws IOException if there is an I/O error while creating RPC server    */
DECL|method|startRpcServer (OzoneConfiguration conf, Class<?> protocol, BlockingService instance, InetSocketAddress addr, String bindHostKey, String handlerCountKey, int handlerCountDefault)
specifier|private
specifier|static
name|RPC
operator|.
name|Server
name|startRpcServer
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|BlockingService
name|instance
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|String
name|bindHostKey
parameter_list|,
name|String
name|handlerCountKey
parameter_list|,
name|int
name|handlerCountDefault
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|addr
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|bindHost
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|bindHostKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|bindHost
operator|==
literal|null
operator|||
name|bindHost
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|bindHost
operator|=
name|addr
operator|.
name|getHostName
argument_list|()
expr_stmt|;
block|}
name|int
name|numHandlers
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|handlerCountKey
argument_list|,
name|handlerCountDefault
argument_list|)
decl_stmt|;
name|RPC
operator|.
name|Server
name|rpcServer
init|=
operator|new
name|RPC
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setProtocol
argument_list|(
name|protocol
argument_list|)
operator|.
name|setInstance
argument_list|(
name|instance
argument_list|)
operator|.
name|setBindAddress
argument_list|(
name|bindHost
argument_list|)
operator|.
name|setPort
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|)
operator|.
name|setNumHandlers
argument_list|(
name|numHandlers
argument_list|)
operator|.
name|setVerbose
argument_list|(
literal|false
argument_list|)
operator|.
name|setSecretManager
argument_list|(
literal|null
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|rpcServer
return|;
block|}
annotation|@
name|Override
DECL|method|mountVolume ( String userName, String volumeName)
specifier|public
specifier|synchronized
name|MountVolumeResponse
name|mountVolume
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|volumeName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|storageManager
operator|.
name|isVolumeValid
argument_list|(
name|userName
argument_list|,
name|volumeName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createVolume (String userName, String volumeName, long volumeSize, int blockSize)
specifier|public
specifier|synchronized
name|void
name|createVolume
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|volumeName
parameter_list|,
name|long
name|volumeSize
parameter_list|,
name|int
name|blockSize
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Create volume received: userName: {} volumeName: {} "
operator|+
literal|"volumeSize: {} blockSize: {}"
argument_list|,
name|userName
argument_list|,
name|volumeName
argument_list|,
name|volumeSize
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
comment|// It is important to create in-memory representation of the
comment|// volume first, then writes to persistent storage (levelDB)
comment|// such that it is guaranteed that when there is an entry in
comment|// levelDB, the volume is allocated. (more like a UNDO log fashion)
comment|// TODO: what if creation failed? we allocated containers but lost
comment|// the reference to the volume and all it's containers. How to release
comment|// the containers?
name|storageManager
operator|.
name|createVolume
argument_list|(
name|userName
argument_list|,
name|volumeName
argument_list|,
name|volumeSize
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|VolumeDescriptor
name|volume
init|=
name|storageManager
operator|.
name|getVolume
argument_list|(
name|userName
argument_list|,
name|volumeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|volume
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Volume creation failed!"
argument_list|)
throw|;
block|}
name|String
name|volumeKey
init|=
name|KeyUtil
operator|.
name|getVolumeKey
argument_list|(
name|userName
argument_list|,
name|volumeName
argument_list|)
decl_stmt|;
name|writeToPersistentStore
argument_list|(
name|volumeKey
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|,
name|volume
operator|.
name|toProtobuf
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteVolume (String userName, String volumeName, boolean force)
specifier|public
specifier|synchronized
name|void
name|deleteVolume
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|volumeName
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Delete volume received: volume: {} {} "
argument_list|,
name|volumeName
argument_list|,
name|force
argument_list|)
expr_stmt|;
name|storageManager
operator|.
name|deleteVolume
argument_list|(
name|userName
argument_list|,
name|volumeName
argument_list|,
name|force
argument_list|)
expr_stmt|;
comment|// being here means volume is successfully deleted now
name|String
name|volumeKey
init|=
name|KeyUtil
operator|.
name|getVolumeKey
argument_list|(
name|userName
argument_list|,
name|volumeName
argument_list|)
decl_stmt|;
name|removeFromPersistentStore
argument_list|(
name|volumeKey
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// No need to synchronize on the following three methods, since write and
comment|// remove's caller are synchronized. read's caller is the constructor and
comment|// no other method call can happen at that time.
annotation|@
name|VisibleForTesting
DECL|method|writeToPersistentStore (byte[] key, byte[] value)
specifier|public
name|void
name|writeToPersistentStore
parameter_list|(
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
block|{
name|levelDBStore
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|removeFromPersistentStore (byte[] key)
specifier|public
name|void
name|removeFromPersistentStore
parameter_list|(
name|byte
index|[]
name|key
parameter_list|)
block|{
name|levelDBStore
operator|.
name|delete
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
DECL|method|readFromPersistentStore ()
specifier|public
name|void
name|readFromPersistentStore
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|DBIterator
name|iter
init|=
name|levelDBStore
operator|.
name|getIterator
argument_list|()
init|)
block|{
name|iter
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|volumeKey
init|=
operator|new
name|String
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|encoding
argument_list|)
decl_stmt|;
try|try
block|{
name|VolumeDescriptor
name|volumeDescriptor
init|=
name|VolumeDescriptor
operator|.
name|fromProtobuf
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|storageManager
operator|.
name|addVolume
argument_list|(
name|volumeDescriptor
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Loading volume "
operator|+
name|volumeKey
operator|+
literal|" error "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|infoVolume (String userName, String volumeName )
specifier|public
specifier|synchronized
name|VolumeInfo
name|infoVolume
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|volumeName
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Info volume received: volume: {}"
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
return|return
name|storageManager
operator|.
name|infoVolume
argument_list|(
name|userName
argument_list|,
name|volumeName
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getAllVolumes ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|VolumeDescriptor
argument_list|>
name|getAllVolumes
parameter_list|()
block|{
return|return
name|storageManager
operator|.
name|getAllVolume
argument_list|(
literal|null
argument_list|)
return|;
block|}
DECL|method|getAllVolumes (String userName)
specifier|public
specifier|synchronized
name|List
argument_list|<
name|VolumeDescriptor
argument_list|>
name|getAllVolumes
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
return|return
name|storageManager
operator|.
name|getAllVolume
argument_list|(
name|userName
argument_list|)
return|;
block|}
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|levelDBStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error when closing levelDB "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|clean ()
specifier|public
specifier|synchronized
name|void
name|clean
parameter_list|()
block|{
try|try
block|{
name|levelDBStore
operator|.
name|close
argument_list|()
expr_stmt|;
name|levelDBStore
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error when deleting levelDB "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|listVolume (String userName)
specifier|public
specifier|synchronized
name|List
argument_list|<
name|VolumeInfo
argument_list|>
name|listVolume
parameter_list|(
name|String
name|userName
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|VolumeInfo
argument_list|>
name|response
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|VolumeDescriptor
argument_list|>
name|allVolumes
init|=
name|storageManager
operator|.
name|getAllVolume
argument_list|(
name|userName
argument_list|)
decl_stmt|;
for|for
control|(
name|VolumeDescriptor
name|volume
range|:
name|allVolumes
control|)
block|{
name|VolumeInfo
name|info
init|=
operator|new
name|VolumeInfo
argument_list|(
name|volume
operator|.
name|getUserName
argument_list|()
argument_list|,
name|volume
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|volume
operator|.
name|getVolumeSize
argument_list|()
argument_list|,
name|volume
operator|.
name|getBlockSize
argument_list|()
argument_list|)
decl_stmt|;
name|response
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|version
init|=
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|StorageContainerLocationProtocolPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|OzoneConfiguration
name|ozoneConf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|String
name|scmAddress
init|=
name|ozoneConf
operator|.
name|get
argument_list|(
name|DFS_CBLOCK_SCM_IPADDRESS_KEY
argument_list|,
name|DFS_CBLOCK_SCM_IPADDRESS_DEFAULT
argument_list|)
decl_stmt|;
name|int
name|scmPort
init|=
name|ozoneConf
operator|.
name|getInt
argument_list|(
name|DFS_CBLOCK_SCM_PORT_KEY
argument_list|,
name|DFS_CBLOCK_SCM_PORT_DEFAULT
argument_list|)
decl_stmt|;
name|int
name|containerSizeGB
init|=
name|ozoneConf
operator|.
name|getInt
argument_list|(
name|DFS_CBLOCK_CONTAINER_SIZE_GB_KEY
argument_list|,
name|DFS_CBLOCK_CONTAINER_SIZE_GB_DEFAULT
argument_list|)
decl_stmt|;
name|ContainerOperationClient
operator|.
name|setContainerSizeB
argument_list|(
name|containerSizeGB
operator|*
name|OzoneConsts
operator|.
name|GB
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|address
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|scmAddress
argument_list|,
name|scmPort
argument_list|)
decl_stmt|;
name|ozoneConf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT
argument_list|,
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT_DEFAULT
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating StorageContainerLocationProtocol RPC client with address {}"
argument_list|,
name|address
argument_list|)
expr_stmt|;
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|ozoneConf
argument_list|,
name|StorageContainerLocationProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|StorageContainerLocationProtocolClientSideTranslatorPB
name|client
init|=
operator|new
name|StorageContainerLocationProtocolClientSideTranslatorPB
argument_list|(
name|RPC
operator|.
name|getProxy
argument_list|(
name|StorageContainerLocationProtocolPB
operator|.
name|class
argument_list|,
name|version
argument_list|,
name|address
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
name|ozoneConf
argument_list|,
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|ozoneConf
argument_list|)
argument_list|,
name|Client
operator|.
name|getRpcTimeout
argument_list|(
name|ozoneConf
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|ScmClient
name|storageClient
init|=
operator|new
name|ContainerOperationClient
argument_list|(
name|client
argument_list|,
operator|new
name|XceiverClientManager
argument_list|(
name|ozoneConf
argument_list|)
argument_list|)
decl_stmt|;
name|CBlockManager
name|cbm
init|=
operator|new
name|CBlockManager
argument_list|(
name|ozoneConf
argument_list|,
name|storageClient
argument_list|)
decl_stmt|;
name|cbm
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

