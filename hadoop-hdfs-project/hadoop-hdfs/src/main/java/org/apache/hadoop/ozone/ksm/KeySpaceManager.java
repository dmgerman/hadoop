begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.ksm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|ksm
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
name|hdfs
operator|.
name|DFSUtil
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmBucketArgs
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmVolumeArgs
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
name|ksm
operator|.
name|protocol
operator|.
name|KeySpaceManagerProtocol
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
name|ksm
operator|.
name|protocolPB
operator|.
name|KeySpaceManagerProtocolPB
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
name|OzoneClientUtils
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
name|ozone
operator|.
name|protocolPB
operator|.
name|KeySpaceManagerProtocolServerSideTranslatorPB
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
name|scm
operator|.
name|StorageContainerManager
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
name|util
operator|.
name|StringUtils
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
name|List
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
name|ozone
operator|.
name|ksm
operator|.
name|KSMConfigKeys
operator|.
name|OZONE_KSM_ADDRESS_KEY
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
name|ozone
operator|.
name|ksm
operator|.
name|KSMConfigKeys
operator|.
name|OZONE_KSM_HANDLER_COUNT_DEFAULT
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
name|ozone
operator|.
name|ksm
operator|.
name|KSMConfigKeys
operator|.
name|OZONE_KSM_HANDLER_COUNT_KEY
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|KeySpaceManagerProtocolProtos
operator|.
name|KeySpaceManagerService
operator|.
name|newReflectiveBlockingService
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
name|util
operator|.
name|ExitUtil
operator|.
name|terminate
import|;
end_import

begin_comment
comment|/**  * Ozone Keyspace manager is the metadata manager of ozone.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"CBLOCK"
block|,
literal|"OZONE"
block|,
literal|"HBASE"
block|}
argument_list|)
DECL|class|KeySpaceManager
specifier|public
class|class
name|KeySpaceManager
implements|implements
name|KeySpaceManagerProtocol
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
name|KeySpaceManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ksmRpcServer
specifier|private
specifier|final
name|RPC
operator|.
name|Server
name|ksmRpcServer
decl_stmt|;
DECL|field|ksmRpcAddress
specifier|private
specifier|final
name|InetSocketAddress
name|ksmRpcAddress
decl_stmt|;
DECL|field|metadataManager
specifier|private
specifier|final
name|MetadataManager
name|metadataManager
decl_stmt|;
DECL|field|volumeManager
specifier|private
specifier|final
name|VolumeManager
name|volumeManager
decl_stmt|;
DECL|field|bucketManager
specifier|private
specifier|final
name|BucketManager
name|bucketManager
decl_stmt|;
DECL|field|metrics
specifier|private
specifier|final
name|KSMMetrics
name|metrics
decl_stmt|;
DECL|method|KeySpaceManager (OzoneConfiguration conf)
specifier|public
name|KeySpaceManager
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|handlerCount
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_KSM_HANDLER_COUNT_KEY
argument_list|,
name|OZONE_KSM_HANDLER_COUNT_DEFAULT
argument_list|)
decl_stmt|;
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|KeySpaceManagerProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|BlockingService
name|ksmService
init|=
name|newReflectiveBlockingService
argument_list|(
operator|new
name|KeySpaceManagerProtocolServerSideTranslatorPB
argument_list|(
name|this
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|InetSocketAddress
name|ksmNodeRpcAddr
init|=
name|OzoneClientUtils
operator|.
name|getKsmAddress
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|ksmRpcServer
operator|=
name|startRpcServer
argument_list|(
name|conf
argument_list|,
name|ksmNodeRpcAddr
argument_list|,
name|KeySpaceManagerProtocolPB
operator|.
name|class
argument_list|,
name|ksmService
argument_list|,
name|handlerCount
argument_list|)
expr_stmt|;
name|ksmRpcAddress
operator|=
name|updateListenAddress
argument_list|(
name|conf
argument_list|,
name|OZONE_KSM_ADDRESS_KEY
argument_list|,
name|ksmNodeRpcAddr
argument_list|,
name|ksmRpcServer
argument_list|)
expr_stmt|;
name|metadataManager
operator|=
operator|new
name|MetadataManagerImpl
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|volumeManager
operator|=
operator|new
name|VolumeManagerImpl
argument_list|(
name|metadataManager
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|bucketManager
operator|=
operator|new
name|BucketManagerImpl
argument_list|(
name|metadataManager
argument_list|)
expr_stmt|;
name|metrics
operator|=
name|KSMMetrics
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
comment|/**    * Starts an RPC server, if configured.    *    * @param conf configuration    * @param addr configured address of RPC server    * @param protocol RPC protocol provided by RPC server    * @param instance RPC protocol implementation instance    * @param handlerCount RPC server handler count    *    * @return RPC server    * @throws IOException if there is an I/O error while creating RPC server    */
DECL|method|startRpcServer (OzoneConfiguration conf, InetSocketAddress addr, Class<?> protocol, BlockingService instance, int handlerCount)
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
name|InetSocketAddress
name|addr
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
name|int
name|handlerCount
parameter_list|)
throws|throws
name|IOException
block|{
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
name|addr
operator|.
name|getHostString
argument_list|()
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
name|handlerCount
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
name|DFSUtil
operator|.
name|addPBProtocol
argument_list|(
name|conf
argument_list|,
name|protocol
argument_list|,
name|instance
argument_list|,
name|rpcServer
argument_list|)
expr_stmt|;
return|return
name|rpcServer
return|;
block|}
DECL|method|getMetrics ()
specifier|public
name|KSMMetrics
name|getMetrics
parameter_list|()
block|{
return|return
name|metrics
return|;
block|}
comment|/**    * Returns listening address of Key Space Manager RPC server.    *    * @return listen address of Key Space Manager RPC server    */
annotation|@
name|VisibleForTesting
DECL|method|getClientRpcAddress ()
specifier|public
name|InetSocketAddress
name|getClientRpcAddress
parameter_list|()
block|{
return|return
name|ksmRpcAddress
return|;
block|}
comment|/**    * Main entry point for starting KeySpaceManager.    *    * @param argv arguments    * @throws IOException if startup fails due to I/O error    */
DECL|method|main (String[] argv)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|IOException
block|{
name|StringUtils
operator|.
name|startupShutdownMessage
argument_list|(
name|StorageContainerManager
operator|.
name|class
argument_list|,
name|argv
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
try|try
block|{
name|KeySpaceManager
name|ksm
init|=
operator|new
name|KeySpaceManager
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|ksm
operator|.
name|start
argument_list|()
expr_stmt|;
name|ksm
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to start the KeyspaceManager."
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|terminate
argument_list|(
literal|1
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Builds a message for logging startup information about an RPC server.    *    * @param description RPC server description    * @param addr RPC server listening address    * @return server startup message    */
DECL|method|buildRpcServerStartMessage (String description, InetSocketAddress addr)
specifier|private
specifier|static
name|String
name|buildRpcServerStartMessage
parameter_list|(
name|String
name|description
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|)
block|{
return|return
name|addr
operator|!=
literal|null
condition|?
name|String
operator|.
name|format
argument_list|(
literal|"%s is listening at %s"
argument_list|,
name|description
argument_list|,
name|addr
operator|.
name|toString
argument_list|()
argument_list|)
else|:
name|String
operator|.
name|format
argument_list|(
literal|"%s not started"
argument_list|,
name|description
argument_list|)
return|;
block|}
comment|/**    * After starting an RPC server, updates configuration with the actual    * listening address of that server. The listening address may be different    * from the configured address if, for example, the configured address uses    * port 0 to request use of an ephemeral port.    *    * @param conf configuration to update    * @param rpcAddressKey configuration key for RPC server address    * @param addr configured address    * @param rpcServer started RPC server.    */
DECL|method|updateListenAddress (OzoneConfiguration conf, String rpcAddressKey, InetSocketAddress addr, RPC.Server rpcServer)
specifier|private
specifier|static
name|InetSocketAddress
name|updateListenAddress
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|String
name|rpcAddressKey
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|RPC
operator|.
name|Server
name|rpcServer
parameter_list|)
block|{
name|InetSocketAddress
name|listenAddr
init|=
name|rpcServer
operator|.
name|getListenerAddress
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|updatedAddr
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|addr
operator|.
name|getHostString
argument_list|()
argument_list|,
name|listenAddr
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|rpcAddressKey
argument_list|,
name|listenAddr
operator|.
name|getHostString
argument_list|()
operator|+
literal|":"
operator|+
name|listenAddr
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|updatedAddr
return|;
block|}
comment|/**    * Start service.    */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
name|buildRpcServerStartMessage
argument_list|(
literal|"KeyspaceManager RPC server"
argument_list|,
name|ksmRpcAddress
argument_list|)
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|start
argument_list|()
expr_stmt|;
name|ksmRpcServer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * Stop service.    */
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
try|try
block|{
name|ksmRpcServer
operator|.
name|stop
argument_list|()
expr_stmt|;
name|metadataManager
operator|.
name|stop
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
name|info
argument_list|(
literal|"Key Space Manager stop failed."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Wait until service has completed shutdown.    */
DECL|method|join ()
specifier|public
name|void
name|join
parameter_list|()
block|{
try|try
block|{
name|ksmRpcServer
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
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Interrupted during KeyspaceManager join."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Creates a volume.    *    * @param args - Arguments to create Volume.    * @throws IOException    */
annotation|@
name|Override
DECL|method|createVolume (KsmVolumeArgs args)
specifier|public
name|void
name|createVolume
parameter_list|(
name|KsmVolumeArgs
name|args
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|metrics
operator|.
name|incNumVolumeCreates
argument_list|()
expr_stmt|;
name|volumeManager
operator|.
name|createVolume
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|metrics
operator|.
name|incNumVolumeCreateFails
argument_list|()
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
comment|/**    * Changes the owner of a volume.    *    * @param volume - Name of the volume.    * @param owner - Name of the owner.    * @throws IOException    */
annotation|@
name|Override
DECL|method|setOwner (String volume, String owner)
specifier|public
name|void
name|setOwner
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
block|{    }
comment|/**    * Changes the Quota on a volume.    *    * @param volume - Name of the volume.    * @param quota - Quota in bytes.    * @throws IOException    */
annotation|@
name|Override
DECL|method|setQuota (String volume, long quota)
specifier|public
name|void
name|setQuota
parameter_list|(
name|String
name|volume
parameter_list|,
name|long
name|quota
parameter_list|)
throws|throws
name|IOException
block|{    }
comment|/**    * Checks if the specified user can access this volume.    *    * @param volume - volume    * @param userName - user name    * @throws IOException    */
annotation|@
name|Override
DECL|method|checkVolumeAccess (String volume, String userName)
specifier|public
name|void
name|checkVolumeAccess
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|userName
parameter_list|)
throws|throws
name|IOException
block|{    }
comment|/**    * Gets the volume information.    *    * @param volume - Volume name.s    * @return VolumeArgs or exception is thrown.    * @throws IOException    */
annotation|@
name|Override
DECL|method|getVolumeInfo (String volume)
specifier|public
name|KsmVolumeArgs
name|getVolumeInfo
parameter_list|(
name|String
name|volume
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Deletes the an exisiting empty volume.    *    * @param volume - Name of the volume.    * @throws IOException    */
annotation|@
name|Override
DECL|method|deleteVolume (String volume)
specifier|public
name|void
name|deleteVolume
parameter_list|(
name|String
name|volume
parameter_list|)
throws|throws
name|IOException
block|{    }
comment|/**    * Lists volume owned by a specific user.    *    * @param userName - user name    * @param prefix - Filter prefix -- Return only entries that match this.    * @param prevKey - Previous key -- List starts from the next from the    * prevkey    * @param maxKeys - Max number of keys to return.    * @return List of Volumes.    * @throws IOException    */
annotation|@
name|Override
DECL|method|listVolumeByUser (String userName, String prefix, String prevKey, long maxKeys)
specifier|public
name|List
argument_list|<
name|KsmVolumeArgs
argument_list|>
name|listVolumeByUser
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|prefix
parameter_list|,
name|String
name|prevKey
parameter_list|,
name|long
name|maxKeys
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Lists volume all volumes in the cluster.    *    * @param prefix - Filter prefix -- Return only entries that match this.    * @param prevKey - Previous key -- List starts from the next from the    * prevkey    * @param maxKeys - Max number of keys to return.    * @return List of Volumes.    * @throws IOException    */
annotation|@
name|Override
DECL|method|listAllVolumes (String prefix, String prevKey, long maxKeys)
specifier|public
name|List
argument_list|<
name|KsmVolumeArgs
argument_list|>
name|listAllVolumes
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|prevKey
parameter_list|,
name|long
name|maxKeys
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Creates a bucket.    *    * @param args - Arguments to create Bucket.    * @throws IOException    */
annotation|@
name|Override
DECL|method|createBucket (KsmBucketArgs args)
specifier|public
name|void
name|createBucket
parameter_list|(
name|KsmBucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|metrics
operator|.
name|incNumBucketCreates
argument_list|()
expr_stmt|;
name|bucketManager
operator|.
name|createBucket
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|metrics
operator|.
name|incNumBucketCreateFails
argument_list|()
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
block|}
end_class

end_unit

