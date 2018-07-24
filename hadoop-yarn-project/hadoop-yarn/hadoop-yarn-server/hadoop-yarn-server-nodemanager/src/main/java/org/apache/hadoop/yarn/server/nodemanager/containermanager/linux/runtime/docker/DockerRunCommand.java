begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.docker
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
name|linux
operator|.
name|runtime
operator|.
name|docker
package|;
end_package

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
name|util
operator|.
name|LinkedHashMap
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|DockerRunCommand
specifier|public
class|class
name|DockerRunCommand
extends|extends
name|DockerCommand
block|{
DECL|field|RUN_COMMAND
specifier|private
specifier|static
specifier|final
name|String
name|RUN_COMMAND
init|=
literal|"run"
decl_stmt|;
DECL|field|userEnv
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|userEnv
decl_stmt|;
comment|/** The following are mandatory: */
DECL|method|DockerRunCommand (String containerId, String user, String image)
specifier|public
name|DockerRunCommand
parameter_list|(
name|String
name|containerId
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|image
parameter_list|)
block|{
name|super
argument_list|(
name|RUN_COMMAND
argument_list|)
expr_stmt|;
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"name"
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"user"
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"image"
argument_list|,
name|image
argument_list|)
expr_stmt|;
name|this
operator|.
name|userEnv
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|removeContainerOnExit ()
specifier|public
name|DockerRunCommand
name|removeContainerOnExit
parameter_list|()
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"rm"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|detachOnRun ()
specifier|public
name|DockerRunCommand
name|detachOnRun
parameter_list|()
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"detach"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setContainerWorkDir (String workdir)
specifier|public
name|DockerRunCommand
name|setContainerWorkDir
parameter_list|(
name|String
name|workdir
parameter_list|)
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"workdir"
argument_list|,
name|workdir
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setNetworkType (String type)
specifier|public
name|DockerRunCommand
name|setNetworkType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"net"
argument_list|,
name|type
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setPidNamespace (String type)
specifier|public
name|DockerRunCommand
name|setPidNamespace
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"pid"
argument_list|,
name|type
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addMountLocation (String sourcePath, String destinationPath, String mode)
specifier|public
name|DockerRunCommand
name|addMountLocation
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destinationPath
parameter_list|,
name|String
name|mode
parameter_list|)
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"mounts"
argument_list|,
name|sourcePath
operator|+
literal|":"
operator|+
name|destinationPath
operator|+
literal|":"
operator|+
name|mode
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addReadWriteMountLocation (String sourcePath, String destinationPath)
specifier|public
name|DockerRunCommand
name|addReadWriteMountLocation
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destinationPath
parameter_list|)
block|{
return|return
name|addMountLocation
argument_list|(
name|sourcePath
argument_list|,
name|destinationPath
argument_list|,
literal|"rw"
argument_list|)
return|;
block|}
DECL|method|addAllReadWriteMountLocations (List<String> paths)
specifier|public
name|DockerRunCommand
name|addAllReadWriteMountLocations
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
for|for
control|(
name|String
name|dir
range|:
name|paths
control|)
block|{
name|this
operator|.
name|addReadWriteMountLocation
argument_list|(
name|dir
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|addReadOnlyMountLocation (String sourcePath, String destinationPath, boolean createSource)
specifier|public
name|DockerRunCommand
name|addReadOnlyMountLocation
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destinationPath
parameter_list|,
name|boolean
name|createSource
parameter_list|)
block|{
name|boolean
name|sourceExists
init|=
operator|new
name|File
argument_list|(
name|sourcePath
argument_list|)
operator|.
name|exists
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|sourceExists
operator|&&
operator|!
name|createSource
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
name|addReadOnlyMountLocation
argument_list|(
name|sourcePath
argument_list|,
name|destinationPath
argument_list|)
return|;
block|}
DECL|method|addReadOnlyMountLocation (String sourcePath, String destinationPath)
specifier|public
name|DockerRunCommand
name|addReadOnlyMountLocation
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|destinationPath
parameter_list|)
block|{
return|return
name|addMountLocation
argument_list|(
name|sourcePath
argument_list|,
name|destinationPath
argument_list|,
literal|"ro"
argument_list|)
return|;
block|}
DECL|method|addAllReadOnlyMountLocations (List<String> paths)
specifier|public
name|DockerRunCommand
name|addAllReadOnlyMountLocations
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
for|for
control|(
name|String
name|dir
range|:
name|paths
control|)
block|{
name|this
operator|.
name|addReadOnlyMountLocation
argument_list|(
name|dir
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|setVolumeDriver (String volumeDriver)
specifier|public
name|DockerRunCommand
name|setVolumeDriver
parameter_list|(
name|String
name|volumeDriver
parameter_list|)
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"volume-driver"
argument_list|,
name|volumeDriver
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setCGroupParent (String parentPath)
specifier|public
name|DockerRunCommand
name|setCGroupParent
parameter_list|(
name|String
name|parentPath
parameter_list|)
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"cgroup-parent"
argument_list|,
name|parentPath
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/* Run a privileged container. Use with extreme care */
DECL|method|setPrivileged ()
specifier|public
name|DockerRunCommand
name|setPrivileged
parameter_list|()
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"privileged"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setCapabilities (Set<String> capabilties)
specifier|public
name|DockerRunCommand
name|setCapabilities
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|capabilties
parameter_list|)
block|{
comment|//first, drop all capabilities
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"cap-drop"
argument_list|,
literal|"ALL"
argument_list|)
expr_stmt|;
comment|//now, add the capabilities supplied
for|for
control|(
name|String
name|capability
range|:
name|capabilties
control|)
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"cap-add"
argument_list|,
name|capability
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|setHostname (String hostname)
specifier|public
name|DockerRunCommand
name|setHostname
parameter_list|(
name|String
name|hostname
parameter_list|)
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"hostname"
argument_list|,
name|hostname
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addDevice (String sourceDevice, String destinationDevice)
specifier|public
name|DockerRunCommand
name|addDevice
parameter_list|(
name|String
name|sourceDevice
parameter_list|,
name|String
name|destinationDevice
parameter_list|)
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"devices"
argument_list|,
name|sourceDevice
operator|+
literal|":"
operator|+
name|destinationDevice
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|enableDetach ()
specifier|public
name|DockerRunCommand
name|enableDetach
parameter_list|()
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"detach"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|disableDetach ()
specifier|public
name|DockerRunCommand
name|disableDetach
parameter_list|()
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"detach"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|groupAdd (String[] groups)
specifier|public
name|DockerRunCommand
name|groupAdd
parameter_list|(
name|String
index|[]
name|groups
parameter_list|)
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"group-add"
argument_list|,
name|String
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|groups
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setOverrideCommandWithArgs ( List<String> overrideCommandWithArgs)
specifier|public
name|DockerRunCommand
name|setOverrideCommandWithArgs
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|overrideCommandWithArgs
parameter_list|)
block|{
for|for
control|(
name|String
name|override
range|:
name|overrideCommandWithArgs
control|)
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"launch-command"
argument_list|,
name|override
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getDockerCommandWithArguments ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getDockerCommandWithArguments
parameter_list|()
block|{
return|return
name|super
operator|.
name|getDockerCommandWithArguments
argument_list|()
return|;
block|}
DECL|method|setOverrideDisabled (boolean toggle)
specifier|public
name|DockerRunCommand
name|setOverrideDisabled
parameter_list|(
name|boolean
name|toggle
parameter_list|)
block|{
name|String
name|value
init|=
name|Boolean
operator|.
name|toString
argument_list|(
name|toggle
argument_list|)
decl_stmt|;
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"use-entry-point"
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setLogDir (String logDir)
specifier|public
name|DockerRunCommand
name|setLogDir
parameter_list|(
name|String
name|logDir
parameter_list|)
block|{
name|super
operator|.
name|addCommandArguments
argument_list|(
literal|"log-dir"
argument_list|,
name|logDir
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Check if user defined environment variables are empty.    *    * @return true if user defined environment variables are not empty.    */
DECL|method|containsEnv ()
specifier|public
name|boolean
name|containsEnv
parameter_list|()
block|{
if|if
condition|(
name|userEnv
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Get user defined environment variables.    *    * @return a map of user defined environment variables    */
DECL|method|getEnv ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getEnv
parameter_list|()
block|{
return|return
name|userEnv
return|;
block|}
comment|/**    * Add user defined environment variables.    *    * @param environment A map of user defined environment variables    */
DECL|method|addEnv (Map<String, String> environment)
specifier|public
specifier|final
name|void
name|addEnv
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
parameter_list|)
block|{
name|userEnv
operator|.
name|putAll
argument_list|(
name|environment
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

