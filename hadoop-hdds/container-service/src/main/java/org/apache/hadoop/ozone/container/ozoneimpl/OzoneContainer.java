begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.ozoneimpl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|ozoneimpl
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsProto
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
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|StorageLocation
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
name|protocol
operator|.
name|DatanodeDetails
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
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|NodeReportProto
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerData
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
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ChunkManagerImpl
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
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ContainerManagerImpl
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
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|Dispatcher
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
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|KeyManagerImpl
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
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|ChunkManager
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
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|ContainerDispatcher
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
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|ContainerManager
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
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|KeyManager
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
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|background
operator|.
name|BlockDeletingService
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
name|container
operator|.
name|common
operator|.
name|transport
operator|.
name|server
operator|.
name|XceiverServer
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
name|container
operator|.
name|common
operator|.
name|transport
operator|.
name|server
operator|.
name|XceiverServerGrpc
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
name|container
operator|.
name|common
operator|.
name|transport
operator|.
name|server
operator|.
name|XceiverServerSpi
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
name|container
operator|.
name|common
operator|.
name|transport
operator|.
name|server
operator|.
name|ratis
operator|.
name|XceiverServerRatis
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
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|concurrent
operator|.
name|TimeUnit
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
name|DFS_DATANODE_DATA_DIR_KEY
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
name|OzoneConfigKeys
operator|.
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL
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
name|OzoneConfigKeys
operator|.
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL_DEFAULT
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
name|OzoneConfigKeys
operator|.
name|OZONE_BLOCK_DELETING_SERVICE_TIMEOUT
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
name|OzoneConfigKeys
operator|.
name|OZONE_BLOCK_DELETING_SERVICE_TIMEOUT_DEFAULT
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
name|OzoneConsts
operator|.
name|CONTAINER_ROOT_PREFIX
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
name|OzoneConsts
operator|.
name|INVALID_PORT
import|;
end_import

begin_comment
comment|/**  * Ozone main class sets up the network server and initializes the container  * layer.  */
end_comment

begin_class
DECL|class|OzoneContainer
specifier|public
class|class
name|OzoneContainer
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OzoneContainer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ozoneConfig
specifier|private
specifier|final
name|Configuration
name|ozoneConfig
decl_stmt|;
DECL|field|dispatcher
specifier|private
specifier|final
name|ContainerDispatcher
name|dispatcher
decl_stmt|;
DECL|field|manager
specifier|private
specifier|final
name|ContainerManager
name|manager
decl_stmt|;
DECL|field|server
specifier|private
specifier|final
name|XceiverServerSpi
index|[]
name|server
decl_stmt|;
DECL|field|chunkManager
specifier|private
specifier|final
name|ChunkManager
name|chunkManager
decl_stmt|;
DECL|field|keyManager
specifier|private
specifier|final
name|KeyManager
name|keyManager
decl_stmt|;
DECL|field|blockDeletingService
specifier|private
specifier|final
name|BlockDeletingService
name|blockDeletingService
decl_stmt|;
comment|/**    * Creates a network endpoint and enables Ozone container.    *    * @param ozoneConfig - Config    * @throws IOException    */
DECL|method|OzoneContainer ( DatanodeDetails datanodeDetails, Configuration ozoneConfig)
specifier|public
name|OzoneContainer
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|Configuration
name|ozoneConfig
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|ozoneConfig
operator|=
name|ozoneConfig
expr_stmt|;
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|locations
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
index|[]
name|paths
init|=
name|ozoneConfig
operator|.
name|getStrings
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|)
decl_stmt|;
if|if
condition|(
name|paths
operator|!=
literal|null
operator|&&
name|paths
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|p
range|:
name|paths
control|)
block|{
name|locations
operator|.
name|add
argument_list|(
name|StorageLocation
operator|.
name|parse
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|p
argument_list|)
operator|.
name|resolve
argument_list|(
name|CONTAINER_ROOT_PREFIX
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|getDataDir
argument_list|(
name|locations
argument_list|)
expr_stmt|;
block|}
name|manager
operator|=
operator|new
name|ContainerManagerImpl
argument_list|()
expr_stmt|;
name|manager
operator|.
name|init
argument_list|(
name|this
operator|.
name|ozoneConfig
argument_list|,
name|locations
argument_list|,
name|datanodeDetails
argument_list|)
expr_stmt|;
name|this
operator|.
name|chunkManager
operator|=
operator|new
name|ChunkManagerImpl
argument_list|(
name|manager
argument_list|)
expr_stmt|;
name|manager
operator|.
name|setChunkManager
argument_list|(
name|this
operator|.
name|chunkManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyManager
operator|=
operator|new
name|KeyManagerImpl
argument_list|(
name|manager
argument_list|,
name|ozoneConfig
argument_list|)
expr_stmt|;
name|manager
operator|.
name|setKeyManager
argument_list|(
name|this
operator|.
name|keyManager
argument_list|)
expr_stmt|;
name|long
name|svcInterval
init|=
name|ozoneConfig
operator|.
name|getTimeDuration
argument_list|(
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL
argument_list|,
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|long
name|serviceTimeout
init|=
name|ozoneConfig
operator|.
name|getTimeDuration
argument_list|(
name|OZONE_BLOCK_DELETING_SERVICE_TIMEOUT
argument_list|,
name|OZONE_BLOCK_DELETING_SERVICE_TIMEOUT_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|this
operator|.
name|blockDeletingService
operator|=
operator|new
name|BlockDeletingService
argument_list|(
name|manager
argument_list|,
name|svcInterval
argument_list|,
name|serviceTimeout
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|ozoneConfig
argument_list|)
expr_stmt|;
name|this
operator|.
name|dispatcher
operator|=
operator|new
name|Dispatcher
argument_list|(
name|manager
argument_list|,
name|this
operator|.
name|ozoneConfig
argument_list|)
expr_stmt|;
name|boolean
name|useGrpc
init|=
name|this
operator|.
name|ozoneConfig
operator|.
name|getBoolean
argument_list|(
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_GRPC_ENABLED_KEY
argument_list|,
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_GRPC_ENABLED_DEFAULT
argument_list|)
decl_stmt|;
name|server
operator|=
operator|new
name|XceiverServerSpi
index|[]
block|{
name|useGrpc
condition|?
operator|new
name|XceiverServerGrpc
argument_list|(
name|datanodeDetails
argument_list|,
name|this
operator|.
name|ozoneConfig
argument_list|,
name|this
operator|.
name|dispatcher
argument_list|)
else|:
operator|new
name|XceiverServer
argument_list|(
name|datanodeDetails
argument_list|,
name|this
operator|.
name|ozoneConfig
argument_list|,
name|this
operator|.
name|dispatcher
argument_list|)
block|,
name|XceiverServerRatis
operator|.
name|newXceiverServerRatis
argument_list|(
name|datanodeDetails
argument_list|,
name|this
operator|.
name|ozoneConfig
argument_list|,
name|dispatcher
argument_list|)
block|}
expr_stmt|;
block|}
comment|/**    * Starts serving requests to ozone container.    *    * @throws IOException    */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|XceiverServerSpi
name|serverinstance
range|:
name|server
control|)
block|{
name|serverinstance
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|blockDeletingService
operator|.
name|start
argument_list|()
expr_stmt|;
name|dispatcher
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
comment|/**    * Stops the ozone container.    *<p>    * Shutdown logic is not very obvious from the following code. if you need to    * modify the logic, please keep these comments in mind. Here is the shutdown    * sequence.    *<p>    * 1. We shutdown the network ports.    *<p>    * 2. Now we need to wait for all requests in-flight to finish.    *<p>    * 3. The container manager lock is a read-write lock with "Fairness"    * enabled.    *<p>    * 4. This means that the waiting threads are served in a "first-come-first    * -served" manner. Please note that this applies to waiting threads only.    *<p>    * 5. Since write locks are exclusive, if we are waiting to get a lock it    * implies that we are waiting for in-flight operations to complete.    *<p>    * 6. if there are other write operations waiting on the reader-writer lock,    * fairness guarantees that they will proceed before the shutdown lock    * request.    *<p>    * 7. Since all operations either take a reader or writer lock of container    * manager, we are guaranteed that we are the last operation since we have    * closed the network port, and we wait until close is successful.    *<p>    * 8. We take the writer lock and call shutdown on each of the managers in    * reverse order. That is chunkManager, keyManager and containerManager is    * shutdown.    */
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempting to stop container services."
argument_list|)
expr_stmt|;
for|for
control|(
name|XceiverServerSpi
name|serverinstance
range|:
name|server
control|)
block|{
name|serverinstance
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|dispatcher
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|manager
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|this
operator|.
name|chunkManager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|this
operator|.
name|keyManager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|this
operator|.
name|manager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|this
operator|.
name|blockDeletingService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"container services shutdown complete."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"container service shutdown error:"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|this
operator|.
name|manager
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns a paths to data dirs.    *    * @param pathList - List of paths.    * @throws IOException    */
DECL|method|getDataDir (List<StorageLocation> pathList)
specifier|private
name|void
name|getDataDir
parameter_list|(
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|pathList
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|dir
range|:
name|ozoneConfig
operator|.
name|getStrings
argument_list|(
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|)
control|)
block|{
name|StorageLocation
name|location
init|=
name|StorageLocation
operator|.
name|parse
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|pathList
operator|.
name|add
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns node report of container storage usage.    */
DECL|method|getNodeReport ()
specifier|public
name|NodeReportProto
name|getNodeReport
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|manager
operator|.
name|getNodeReport
argument_list|()
return|;
block|}
DECL|method|getPortbyType (HddsProtos.ReplicationType replicationType)
specifier|private
name|int
name|getPortbyType
parameter_list|(
name|HddsProtos
operator|.
name|ReplicationType
name|replicationType
parameter_list|)
block|{
for|for
control|(
name|XceiverServerSpi
name|serverinstance
range|:
name|server
control|)
block|{
if|if
condition|(
name|serverinstance
operator|.
name|getServerType
argument_list|()
operator|==
name|replicationType
condition|)
block|{
return|return
name|serverinstance
operator|.
name|getIPCPort
argument_list|()
return|;
block|}
block|}
return|return
name|INVALID_PORT
return|;
block|}
comment|/**    * Returns the container server IPC port.    *    * @return Container server IPC port.    */
DECL|method|getContainerServerPort ()
specifier|public
name|int
name|getContainerServerPort
parameter_list|()
block|{
return|return
name|getPortbyType
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|)
return|;
block|}
comment|/**    * Returns the Ratis container Server IPC port.    *    * @return Ratis port.    */
DECL|method|getRatisContainerServerPort ()
specifier|public
name|int
name|getRatisContainerServerPort
parameter_list|()
block|{
return|return
name|getPortbyType
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|)
return|;
block|}
comment|/**    * Returns container report.    * @return - container report.    * @throws IOException    */
DECL|method|getContainerReport ()
specifier|public
name|ContainerReportsProto
name|getContainerReport
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|manager
operator|.
name|getContainerReport
argument_list|()
return|;
block|}
comment|// TODO: remove getContainerReports
comment|/**    * Returns the list of closed containers.    * @return - List of closed containers.    * @throws IOException    */
DECL|method|getClosedContainerReports ()
specifier|public
name|List
argument_list|<
name|ContainerData
argument_list|>
name|getClosedContainerReports
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|manager
operator|.
name|getClosedContainerReports
argument_list|()
return|;
block|}
DECL|method|getRatisSerer ()
specifier|private
name|XceiverServerSpi
name|getRatisSerer
parameter_list|()
block|{
for|for
control|(
name|XceiverServerSpi
name|serverInstance
range|:
name|server
control|)
block|{
if|if
condition|(
name|serverInstance
operator|instanceof
name|XceiverServerRatis
condition|)
block|{
return|return
name|serverInstance
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getStandaAloneSerer ()
specifier|private
name|XceiverServerSpi
name|getStandaAloneSerer
parameter_list|()
block|{
for|for
control|(
name|XceiverServerSpi
name|serverInstance
range|:
name|server
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|serverInstance
operator|instanceof
name|XceiverServerRatis
operator|)
condition|)
block|{
return|return
name|serverInstance
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getContainerManager ()
specifier|public
name|ContainerManager
name|getContainerManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|manager
return|;
block|}
DECL|method|submitContainerRequest ( ContainerProtos.ContainerCommandRequestProto request, HddsProtos.ReplicationType replicationType)
specifier|public
name|void
name|submitContainerRequest
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|request
parameter_list|,
name|HddsProtos
operator|.
name|ReplicationType
name|replicationType
parameter_list|)
throws|throws
name|IOException
block|{
name|XceiverServerSpi
name|serverInstance
decl_stmt|;
name|long
name|containerId
init|=
name|getContainerIdForCmd
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|replicationType
operator|==
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
condition|)
block|{
name|serverInstance
operator|=
name|getRatisSerer
argument_list|()
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|serverInstance
argument_list|)
expr_stmt|;
name|serverInstance
operator|.
name|submitRequest
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"submitting {} request over RATIS server for container {}"
argument_list|,
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|serverInstance
operator|=
name|getStandaAloneSerer
argument_list|()
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|serverInstance
argument_list|)
expr_stmt|;
name|getStandaAloneSerer
argument_list|()
operator|.
name|submitRequest
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"submitting {} request over STAND_ALONE server for container {}"
argument_list|,
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getContainerIdForCmd ( ContainerProtos.ContainerCommandRequestProto request)
specifier|private
name|long
name|getContainerIdForCmd
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|request
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|ContainerProtos
operator|.
name|Type
name|type
init|=
name|request
operator|.
name|getCmdType
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|CloseContainer
case|:
return|return
name|request
operator|.
name|getCloseContainer
argument_list|()
operator|.
name|getContainerID
argument_list|()
return|;
comment|// Right now, we handle only closeContainer via queuing it over the
comment|// over the XceiVerServer. For all other commands we throw Illegal
comment|// argument exception here. Will need to extend the switch cases
comment|// in case we want add another commands here.
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cmd "
operator|+
name|request
operator|.
name|getCmdType
argument_list|()
operator|+
literal|" not supported over HearBeat Response"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

