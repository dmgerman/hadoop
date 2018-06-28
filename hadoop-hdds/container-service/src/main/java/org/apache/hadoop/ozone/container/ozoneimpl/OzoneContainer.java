begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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
name|hdds
operator|.
name|conf
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ContainerSet
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
name|HddsDispatcher
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
name|volume
operator|.
name|HddsVolume
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
name|volume
operator|.
name|VolumeSet
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
name|DiskChecker
operator|.
name|DiskOutOfSpaceException
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
name|*
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
name|Iterator
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
DECL|field|hddsDispatcher
specifier|private
specifier|final
name|HddsDispatcher
name|hddsDispatcher
decl_stmt|;
DECL|field|dnDetails
specifier|private
specifier|final
name|DatanodeDetails
name|dnDetails
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|OzoneConfiguration
name|config
decl_stmt|;
DECL|field|volumeSet
specifier|private
specifier|final
name|VolumeSet
name|volumeSet
decl_stmt|;
DECL|field|containerSet
specifier|private
specifier|final
name|ContainerSet
name|containerSet
decl_stmt|;
DECL|field|server
specifier|private
specifier|final
name|XceiverServerSpi
index|[]
name|server
decl_stmt|;
comment|/**    * Construct OzoneContainer object.    * @param datanodeDetails    * @param conf    * @throws DiskOutOfSpaceException    * @throws IOException    */
DECL|method|OzoneContainer (DatanodeDetails datanodeDetails, OzoneConfiguration conf)
specifier|public
name|OzoneContainer
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|OzoneConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|dnDetails
operator|=
name|datanodeDetails
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|volumeSet
operator|=
operator|new
name|VolumeSet
argument_list|(
name|datanodeDetails
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerSet
operator|=
operator|new
name|ContainerSet
argument_list|()
expr_stmt|;
name|boolean
name|useGrpc
init|=
name|this
operator|.
name|config
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
name|buildContainerSet
argument_list|()
expr_stmt|;
name|hddsDispatcher
operator|=
operator|new
name|HddsDispatcher
argument_list|(
name|config
argument_list|,
name|containerSet
argument_list|,
name|volumeSet
argument_list|)
expr_stmt|;
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
name|config
argument_list|,
name|this
operator|.
name|hddsDispatcher
argument_list|)
else|:
operator|new
name|XceiverServer
argument_list|(
name|datanodeDetails
argument_list|,
name|this
operator|.
name|config
argument_list|,
name|this
operator|.
name|hddsDispatcher
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
name|config
argument_list|,
name|hddsDispatcher
argument_list|)
block|}
expr_stmt|;
block|}
comment|/**    * Build's container map.    */
DECL|method|buildContainerSet ()
specifier|public
name|void
name|buildContainerSet
parameter_list|()
block|{
name|Iterator
argument_list|<
name|HddsVolume
argument_list|>
name|volumeSetIterator
init|=
name|volumeSet
operator|.
name|getVolumesList
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|Thread
argument_list|>
name|volumeThreads
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
comment|//TODO: diskchecker should be run before this, to see how disks are.
comment|// And also handle disk failure tolerance need to be added
while|while
condition|(
name|volumeSetIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|HddsVolume
name|volume
init|=
name|volumeSetIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|File
name|hddsVolumeRootDir
init|=
name|volume
operator|.
name|getHddsRootDir
argument_list|()
decl_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|ContainerReader
argument_list|(
name|hddsVolumeRootDir
argument_list|,
name|containerSet
argument_list|,
name|config
argument_list|)
argument_list|)
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|volumeThreads
operator|.
name|add
argument_list|(
name|thread
argument_list|)
expr_stmt|;
block|}
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|volumeThreads
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|volumeThreads
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Volume Threads Interrupted exception"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempting to start container services."
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
name|start
argument_list|()
expr_stmt|;
block|}
name|hddsDispatcher
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
comment|/**    * Stop Container Service on the datanode.    */
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
comment|//TODO: at end of container IO integration work.
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
name|hddsDispatcher
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getContainerSet ()
specifier|public
name|ContainerSet
name|getContainerSet
parameter_list|()
block|{
return|return
name|containerSet
return|;
block|}
comment|/**    * Returns container report.    * @return - container report.    * @throws IOException    */
specifier|public
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsProto
DECL|method|getContainerReport ()
name|getContainerReport
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|containerSet
operator|.
name|getContainerReport
argument_list|()
return|;
block|}
comment|/**    * Submit ContainerRequest.    * @param request    * @param replicationType    * @throws IOException    */
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
comment|/**    * Returns node report of container storage usage.    */
DECL|method|getNodeReport ()
specifier|public
name|StorageContainerDatanodeProtocolProtos
operator|.
name|NodeReportProto
name|getNodeReport
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|volumeSet
operator|.
name|getNodeReport
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getDispatcher ()
specifier|public
name|ContainerDispatcher
name|getDispatcher
parameter_list|()
block|{
return|return
name|this
operator|.
name|hddsDispatcher
return|;
block|}
DECL|method|getVolumeSet ()
specifier|public
name|VolumeSet
name|getVolumeSet
parameter_list|()
block|{
return|return
name|volumeSet
return|;
block|}
block|}
end_class

end_unit

