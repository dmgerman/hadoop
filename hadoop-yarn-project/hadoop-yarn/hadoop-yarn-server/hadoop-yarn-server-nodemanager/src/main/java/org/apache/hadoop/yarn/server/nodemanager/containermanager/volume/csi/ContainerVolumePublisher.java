begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.volume.csi
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|volume
operator|.
name|csi
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
name|collect
operator|.
name|ImmutableList
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
name|collect
operator|.
name|ImmutableMap
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
name|yarn
operator|.
name|api
operator|.
name|CsiAdaptorProtocol
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|NodePublishVolumeRequest
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|NodeUnpublishVolumeRequest
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|ValidateVolumeCapabilitiesRequest
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Resource
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ResourceInformation
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|Container
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|linux
operator|.
name|runtime
operator|.
name|OCIContainerRuntime
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
name|yarn
operator|.
name|server
operator|.
name|volume
operator|.
name|csi
operator|.
name|CsiConstants
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
name|yarn
operator|.
name|server
operator|.
name|volume
operator|.
name|csi
operator|.
name|VolumeMetaData
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
name|yarn
operator|.
name|server
operator|.
name|volume
operator|.
name|csi
operator|.
name|exception
operator|.
name|InvalidVolumeException
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
name|HashMap
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

begin_comment
comment|/**  * Publish/un-publish CSI volumes on node manager.  */
end_comment

begin_class
DECL|class|ContainerVolumePublisher
specifier|public
class|class
name|ContainerVolumePublisher
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
name|ContainerVolumePublisher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|container
specifier|private
specifier|final
name|Container
name|container
decl_stmt|;
DECL|field|localMountRoot
specifier|private
specifier|final
name|String
name|localMountRoot
decl_stmt|;
DECL|field|runtime
specifier|private
specifier|final
name|OCIContainerRuntime
name|runtime
decl_stmt|;
DECL|method|ContainerVolumePublisher (Container container, String localMountRoot, OCIContainerRuntime runtime)
specifier|public
name|ContainerVolumePublisher
parameter_list|(
name|Container
name|container
parameter_list|,
name|String
name|localMountRoot
parameter_list|,
name|OCIContainerRuntime
name|runtime
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Initiate container volume publisher, containerID={},"
operator|+
literal|" volume local mount rootDir={}"
argument_list|,
name|container
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|localMountRoot
argument_list|)
expr_stmt|;
name|this
operator|.
name|container
operator|=
name|container
expr_stmt|;
name|this
operator|.
name|localMountRoot
operator|=
name|localMountRoot
expr_stmt|;
name|this
operator|.
name|runtime
operator|=
name|runtime
expr_stmt|;
block|}
comment|/**    * It first discovers the volume info from container resource;    * then negotiates with CSI driver adaptor to publish the volume on this    * node manager, on a specific directory under container's work dir;    * and then map the local mounted directory to volume target mount in    * the docker container.    *    * CSI volume publish is a two phase work, by reaching up here    * we can assume the 1st phase is done on the RM side, which means    * YARN is already called the controller service of csi-driver    * to publish the volume; here we only need to call the node service of    * csi-driver to publish the volume on this local node manager.    *    * @return a map where each key is the local mounted path on current node,    *   and value is the remote mount path on the container.    * @throws YarnException    * @throws IOException    */
DECL|method|publishVolumes ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|publishVolumes
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"publishing volumes"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|volumeMounts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|VolumeMetaData
argument_list|>
name|volumes
init|=
name|getVolumes
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Found {} volumes to be published on this node"
argument_list|,
name|volumes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|VolumeMetaData
name|volume
range|:
name|volumes
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|bindings
init|=
name|publishVolume
argument_list|(
name|volume
argument_list|)
decl_stmt|;
if|if
condition|(
name|bindings
operator|!=
literal|null
operator|&&
operator|!
name|bindings
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|volumeMounts
operator|.
name|putAll
argument_list|(
name|bindings
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|volumeMounts
return|;
block|}
DECL|method|unpublishVolumes ()
specifier|public
name|void
name|unpublishVolumes
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Un-publishing Volumes"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|VolumeMetaData
argument_list|>
name|volumes
init|=
name|getVolumes
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Volumes to un-publish {}"
argument_list|,
name|volumes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|VolumeMetaData
name|volume
range|:
name|volumes
control|)
block|{
name|this
operator|.
name|unpublishVolume
argument_list|(
name|volume
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getLocalVolumeMountPath ( String containerWorkDir, String volumeId)
specifier|private
name|File
name|getLocalVolumeMountPath
parameter_list|(
name|String
name|containerWorkDir
parameter_list|,
name|String
name|volumeId
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|containerWorkDir
argument_list|,
name|volumeId
operator|+
literal|"_mount"
argument_list|)
return|;
block|}
DECL|method|getLocalVolumeStagingPath ( String containerWorkDir, String volumeId)
specifier|private
name|File
name|getLocalVolumeStagingPath
parameter_list|(
name|String
name|containerWorkDir
parameter_list|,
name|String
name|volumeId
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|containerWorkDir
argument_list|,
name|volumeId
operator|+
literal|"_staging"
argument_list|)
return|;
block|}
DECL|method|getVolumes ()
specifier|private
name|List
argument_list|<
name|VolumeMetaData
argument_list|>
name|getVolumes
parameter_list|()
throws|throws
name|InvalidVolumeException
block|{
name|List
argument_list|<
name|VolumeMetaData
argument_list|>
name|volumes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Resource
name|containerResource
init|=
name|container
operator|.
name|getResource
argument_list|()
decl_stmt|;
if|if
condition|(
name|containerResource
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ResourceInformation
name|resourceInformation
range|:
name|containerResource
operator|.
name|getAllResourcesListCopy
argument_list|()
control|)
block|{
if|if
condition|(
name|resourceInformation
operator|.
name|getTags
argument_list|()
operator|.
name|contains
argument_list|(
name|CsiConstants
operator|.
name|CSI_VOLUME_RESOURCE_TAG
argument_list|)
condition|)
block|{
name|volumes
operator|.
name|addAll
argument_list|(
name|VolumeMetaData
operator|.
name|fromResource
argument_list|(
name|resourceInformation
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|volumes
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Total number of volumes require provisioning is {}"
argument_list|,
name|volumes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|volumes
return|;
block|}
DECL|method|publishVolume (VolumeMetaData volume)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|publishVolume
parameter_list|(
name|VolumeMetaData
name|volume
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|bindVolumes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// compose a local mount for CSI volume with the container ID
name|File
name|localMount
init|=
name|getLocalVolumeMountPath
argument_list|(
name|localMountRoot
argument_list|,
name|volume
operator|.
name|getVolumeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|localStaging
init|=
name|getLocalVolumeStagingPath
argument_list|(
name|localMountRoot
argument_list|,
name|volume
operator|.
name|getVolumeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Volume {}, local mount path: {}, local staging path {}"
argument_list|,
name|volume
operator|.
name|getVolumeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|localMount
argument_list|,
name|localStaging
argument_list|)
expr_stmt|;
name|NodePublishVolumeRequest
name|publishRequest
init|=
name|NodePublishVolumeRequest
operator|.
name|newInstance
argument_list|(
name|volume
operator|.
name|getVolumeId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
comment|// volume Id
literal|false
argument_list|,
comment|// read only flag
name|localMount
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
comment|// target path
name|localStaging
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
comment|// staging path
operator|new
name|ValidateVolumeCapabilitiesRequest
operator|.
name|VolumeCapability
argument_list|(
name|ValidateVolumeCapabilitiesRequest
operator|.
name|AccessMode
operator|.
name|SINGLE_NODE_WRITER
argument_list|,
name|ValidateVolumeCapabilitiesRequest
operator|.
name|VolumeType
operator|.
name|FILE_SYSTEM
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
argument_list|,
comment|// capability
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|,
comment|// publish context
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
comment|// secrets
comment|// make sure the volume is a known type
if|if
condition|(
name|runtime
operator|.
name|getCsiClients
argument_list|()
operator|.
name|get
argument_list|(
name|volume
operator|.
name|getDriverName
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"No csi-adaptor is found that can talk"
operator|+
literal|" to csi-driver "
operator|+
name|volume
operator|.
name|getDriverName
argument_list|()
argument_list|)
throw|;
block|}
comment|// publish volume to node
name|LOG
operator|.
name|info
argument_list|(
literal|"Publish volume on NM, request {}"
argument_list|,
name|publishRequest
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|runtime
operator|.
name|getCsiClients
argument_list|()
operator|.
name|get
argument_list|(
name|volume
operator|.
name|getDriverName
argument_list|()
argument_list|)
operator|.
name|nodePublishVolume
argument_list|(
name|publishRequest
argument_list|)
expr_stmt|;
comment|// once succeed, bind the container to this mount
name|String
name|containerMountPath
init|=
name|volume
operator|.
name|getMountPoint
argument_list|()
decl_stmt|;
name|bindVolumes
operator|.
name|put
argument_list|(
name|localMount
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|containerMountPath
argument_list|)
expr_stmt|;
return|return
name|bindVolumes
return|;
block|}
DECL|method|unpublishVolume (VolumeMetaData volume)
specifier|private
name|void
name|unpublishVolume
parameter_list|(
name|VolumeMetaData
name|volume
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|CsiAdaptorProtocol
name|csiClient
init|=
name|runtime
operator|.
name|getCsiClients
argument_list|()
operator|.
name|get
argument_list|(
name|volume
operator|.
name|getDriverName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|csiClient
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"No csi-adaptor is found that can talk"
operator|+
literal|" to csi-driver "
operator|+
name|volume
operator|.
name|getDriverName
argument_list|()
argument_list|)
throw|;
block|}
comment|// When container is launched, the container work dir is memorized,
comment|// and that is also the dir we mount the volume to.
name|File
name|localMount
init|=
name|getLocalVolumeMountPath
argument_list|(
name|container
operator|.
name|getCsiVolumesRootDir
argument_list|()
argument_list|,
name|volume
operator|.
name|getVolumeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|localMount
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Local mount {} no longer exist, skipping cleaning"
operator|+
literal|" up the volume"
argument_list|,
name|localMount
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|NodeUnpublishVolumeRequest
name|unpublishRequest
init|=
name|NodeUnpublishVolumeRequest
operator|.
name|newInstance
argument_list|(
name|volume
operator|.
name|getVolumeId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
comment|// volume id
name|localMount
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
comment|// target path
comment|// un-publish volume from node
name|LOG
operator|.
name|info
argument_list|(
literal|"Un-publish volume {}, request {}"
argument_list|,
name|volume
operator|.
name|getVolumeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|unpublishRequest
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|csiClient
operator|.
name|nodeUnpublishVolume
argument_list|(
name|unpublishRequest
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

