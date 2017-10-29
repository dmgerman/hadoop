begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.gpu
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
name|resourceplugin
operator|.
name|gpu
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|conf
operator|.
name|YarnConfiguration
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
name|container
operator|.
name|ResourceMappings
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
name|resources
operator|.
name|gpu
operator|.
name|GpuResourceAllocator
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
name|docker
operator|.
name|DockerRunCommand
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
name|docker
operator|.
name|DockerVolumeCommand
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
name|resourceplugin
operator|.
name|DockerCommandPlugin
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
name|runtime
operator|.
name|ContainerExecutionException
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
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|HashSet
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|docker
operator|.
name|DockerVolumeCommand
operator|.
name|VOLUME_NAME_PATTERN
import|;
end_import

begin_comment
comment|/**  * Implementation to use nvidia-docker v1 as GPU docker command plugin.  */
end_comment

begin_class
DECL|class|NvidiaDockerV1CommandPlugin
specifier|public
class|class
name|NvidiaDockerV1CommandPlugin
implements|implements
name|DockerCommandPlugin
block|{
DECL|field|LOG
specifier|final
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|NvidiaDockerV1CommandPlugin
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|additionalCommands
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|additionalCommands
init|=
literal|null
decl_stmt|;
DECL|field|volumeDriver
specifier|private
name|String
name|volumeDriver
init|=
literal|"local"
decl_stmt|;
comment|// Known option
DECL|field|DEVICE_OPTION
specifier|private
name|String
name|DEVICE_OPTION
init|=
literal|"--device"
decl_stmt|;
DECL|field|VOLUME_DRIVER_OPTION
specifier|private
name|String
name|VOLUME_DRIVER_OPTION
init|=
literal|"--volume-driver"
decl_stmt|;
DECL|field|MOUNT_RO_OPTION
specifier|private
name|String
name|MOUNT_RO_OPTION
init|=
literal|"--volume"
decl_stmt|;
DECL|method|NvidiaDockerV1CommandPlugin (Configuration conf)
specifier|public
name|NvidiaDockerV1CommandPlugin
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
comment|// Get value from key=value
comment|// Throw exception if '=' not found
DECL|method|getValue (String input)
specifier|private
name|String
name|getValue
parameter_list|(
name|String
name|input
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|int
name|index
init|=
name|input
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Failed to locate '=' from input="
operator|+
name|input
argument_list|)
throw|;
block|}
return|return
name|input
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
return|;
block|}
DECL|method|addToCommand (String key, String value)
specifier|private
name|void
name|addToCommand
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|additionalCommands
operator|==
literal|null
condition|)
block|{
name|additionalCommands
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|additionalCommands
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|additionalCommands
operator|.
name|put
argument_list|(
name|key
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|additionalCommands
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|init ()
specifier|private
name|void
name|init
parameter_list|()
throws|throws
name|ContainerExecutionException
block|{
name|String
name|endpoint
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NVIDIA_DOCKER_PLUGIN_V1_ENDPOINT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NVIDIA_DOCKER_PLUGIN_V1_ENDPOINT
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|endpoint
operator|||
name|endpoint
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|YarnConfiguration
operator|.
name|NVIDIA_DOCKER_PLUGIN_V1_ENDPOINT
operator|+
literal|" set to empty, skip init .."
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|cliOptions
decl_stmt|;
try|try
block|{
comment|// Talk to plugin server and get options
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|endpoint
argument_list|)
decl_stmt|;
name|URLConnection
name|uc
init|=
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|uc
operator|.
name|setRequestProperty
argument_list|(
literal|"X-Requested-With"
argument_list|,
literal|"Curl"
argument_list|)
expr_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|uc
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|writer
argument_list|,
literal|"utf-8"
argument_list|)
expr_stmt|;
name|cliOptions
operator|=
name|writer
operator|.
name|toString
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Additional docker CLI options from plugin to run GPU "
operator|+
literal|"containers:"
operator|+
name|cliOptions
argument_list|)
expr_stmt|;
comment|// Parse cli options
comment|// Examples like:
comment|// --device=/dev/nvidiactl --device=/dev/nvidia-uvm --device=/dev/nvidia0
comment|// --volume-driver=nvidia-docker
comment|// --volume=nvidia_driver_352.68:/usr/local/nvidia:ro
for|for
control|(
name|String
name|str
range|:
name|cliOptions
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
control|)
block|{
name|str
operator|=
name|str
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|str
operator|.
name|startsWith
argument_list|(
name|DEVICE_OPTION
argument_list|)
condition|)
block|{
name|addToCommand
argument_list|(
name|DEVICE_OPTION
argument_list|,
name|getValue
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|str
operator|.
name|startsWith
argument_list|(
name|VOLUME_DRIVER_OPTION
argument_list|)
condition|)
block|{
name|volumeDriver
operator|=
name|getValue
argument_list|(
name|str
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found volume-driver:"
operator|+
name|volumeDriver
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|str
operator|.
name|startsWith
argument_list|(
name|MOUNT_RO_OPTION
argument_list|)
condition|)
block|{
name|String
name|mount
init|=
name|getValue
argument_list|(
name|str
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|mount
operator|.
name|endsWith
argument_list|(
literal|":ro"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Should not have mount other than ro, command="
operator|+
name|str
argument_list|)
throw|;
block|}
name|addToCommand
argument_list|(
name|MOUNT_RO_OPTION
argument_list|,
name|mount
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|mount
operator|.
name|lastIndexOf
argument_list|(
literal|':'
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported option:"
operator|+
name|str
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"RuntimeException of "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" init:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"IOException of "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" init:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getGpuIndexFromDeviceName (String device)
specifier|private
name|int
name|getGpuIndexFromDeviceName
parameter_list|(
name|String
name|device
parameter_list|)
block|{
specifier|final
name|String
name|NVIDIA
init|=
literal|"nvidia"
decl_stmt|;
name|int
name|idx
init|=
name|device
operator|.
name|lastIndexOf
argument_list|(
name|NVIDIA
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|// Get last part
name|String
name|str
init|=
name|device
operator|.
name|substring
argument_list|(
name|idx
operator|+
name|NVIDIA
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|str
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|Character
operator|.
name|isDigit
argument_list|(
name|str
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
argument_list|)
return|;
block|}
DECL|method|getAssignedGpus (Container container)
specifier|private
name|Set
argument_list|<
name|GpuDevice
argument_list|>
name|getAssignedGpus
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
name|ResourceMappings
name|resourceMappings
init|=
name|container
operator|.
name|getResourceMappings
argument_list|()
decl_stmt|;
comment|// Copy of assigned Resources
name|Set
argument_list|<
name|GpuDevice
argument_list|>
name|assignedResources
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|resourceMappings
operator|!=
literal|null
condition|)
block|{
name|assignedResources
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|Serializable
name|s
range|:
name|resourceMappings
operator|.
name|getAssignedResources
argument_list|(
name|ResourceInformation
operator|.
name|GPU_URI
argument_list|)
control|)
block|{
name|assignedResources
operator|.
name|add
argument_list|(
operator|(
name|GpuDevice
operator|)
name|s
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|assignedResources
operator|==
literal|null
operator|||
name|assignedResources
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// When no GPU resource assigned, don't need to update docker command.
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
return|return
name|assignedResources
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|requestsGpu (Container container)
specifier|protected
name|boolean
name|requestsGpu
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
return|return
name|GpuResourceAllocator
operator|.
name|getRequestedGpus
argument_list|(
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
operator|>
literal|0
return|;
block|}
comment|/**    * Do initialize when GPU requested    * @param container nmContainer    * @return if #GPU-requested> 0    * @throws ContainerExecutionException when any issue happens    */
DECL|method|initializeWhenGpuRequested (Container container)
specifier|private
name|boolean
name|initializeWhenGpuRequested
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
if|if
condition|(
operator|!
name|requestsGpu
argument_list|(
name|container
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Do lazy initialization of gpu-docker plugin
if|if
condition|(
name|additionalCommands
operator|==
literal|null
condition|)
block|{
name|init
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|updateDockerRunCommand ( DockerRunCommand dockerRunCommand, Container container)
specifier|public
specifier|synchronized
name|void
name|updateDockerRunCommand
parameter_list|(
name|DockerRunCommand
name|dockerRunCommand
parameter_list|,
name|Container
name|container
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
if|if
condition|(
operator|!
name|initializeWhenGpuRequested
argument_list|(
name|container
argument_list|)
condition|)
block|{
return|return;
block|}
name|Set
argument_list|<
name|GpuDevice
argument_list|>
name|assignedResources
init|=
name|getAssignedGpus
argument_list|(
name|container
argument_list|)
decl_stmt|;
if|if
condition|(
name|assignedResources
operator|==
literal|null
operator|||
name|assignedResources
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// Write to dockerRunCommand
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|option
range|:
name|additionalCommands
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|option
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|values
init|=
name|option
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|DEVICE_OPTION
argument_list|)
condition|)
block|{
name|int
name|foundGpuDevices
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|deviceName
range|:
name|values
control|)
block|{
comment|// When specified is a GPU card (device name like /dev/nvidia[n]
comment|// Get index of the GPU (which is [n]).
name|Integer
name|gpuIdx
init|=
name|getGpuIndexFromDeviceName
argument_list|(
name|deviceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|gpuIdx
operator|>=
literal|0
condition|)
block|{
comment|// Use assignedResources to filter --device given by
comment|// nvidia-docker-plugin.
for|for
control|(
name|GpuDevice
name|gpuDevice
range|:
name|assignedResources
control|)
block|{
if|if
condition|(
name|gpuDevice
operator|.
name|getIndex
argument_list|()
operator|==
name|gpuIdx
condition|)
block|{
name|foundGpuDevices
operator|++
expr_stmt|;
name|dockerRunCommand
operator|.
name|addDevice
argument_list|(
name|deviceName
argument_list|,
name|deviceName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// When gpuIdx< 0, it is a controller device (such as
comment|// /dev/nvidiactl). In this case, add device directly.
name|dockerRunCommand
operator|.
name|addDevice
argument_list|(
name|deviceName
argument_list|,
name|deviceName
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Cannot get all assigned Gpu devices from docker plugin output
if|if
condition|(
name|foundGpuDevices
operator|<
name|assignedResources
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"Cannot get all assigned Gpu devices from docker plugin output"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|MOUNT_RO_OPTION
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|value
range|:
name|values
control|)
block|{
name|int
name|idx
init|=
name|value
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|String
name|source
init|=
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
decl_stmt|;
name|String
name|target
init|=
name|value
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|)
decl_stmt|;
name|dockerRunCommand
operator|.
name|addReadOnlyMountLocation
argument_list|(
name|source
argument_list|,
name|target
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"Unsupported option:"
operator|+
name|key
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getCreateDockerVolumeCommand (Container container)
specifier|public
name|DockerVolumeCommand
name|getCreateDockerVolumeCommand
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
if|if
condition|(
operator|!
name|initializeWhenGpuRequested
argument_list|(
name|container
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|newVolumeName
init|=
literal|null
decl_stmt|;
comment|// Get volume name
name|Set
argument_list|<
name|String
argument_list|>
name|mounts
init|=
name|additionalCommands
operator|.
name|get
argument_list|(
name|MOUNT_RO_OPTION
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|mount
range|:
name|mounts
control|)
block|{
name|int
name|idx
init|=
name|mount
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>=
literal|0
condition|)
block|{
name|String
name|mountSource
init|=
name|mount
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
decl_stmt|;
if|if
condition|(
name|VOLUME_NAME_PATTERN
operator|.
name|matcher
argument_list|(
name|mountSource
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
comment|// This is a valid named volume
name|newVolumeName
operator|=
name|mountSource
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found volume name for GPU:"
operator|+
name|newVolumeName
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
else|else
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failed to match "
operator|+
name|mountSource
operator|+
literal|" to named-volume regex pattern"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|newVolumeName
operator|!=
literal|null
condition|)
block|{
name|DockerVolumeCommand
name|command
init|=
operator|new
name|DockerVolumeCommand
argument_list|(
name|DockerVolumeCommand
operator|.
name|VOLUME_CREATE_COMMAND
argument_list|)
decl_stmt|;
name|command
operator|.
name|setDriverName
argument_list|(
name|volumeDriver
argument_list|)
expr_stmt|;
name|command
operator|.
name|setVolumeName
argument_list|(
name|newVolumeName
argument_list|)
expr_stmt|;
return|return
name|command
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getCleanupDockerVolumesCommand (Container container)
specifier|public
name|DockerVolumeCommand
name|getCleanupDockerVolumesCommand
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
comment|// No cleanup needed.
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

