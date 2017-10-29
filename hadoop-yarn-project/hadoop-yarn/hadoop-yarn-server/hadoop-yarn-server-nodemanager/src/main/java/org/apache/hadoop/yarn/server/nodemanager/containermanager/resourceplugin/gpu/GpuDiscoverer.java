begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|classification
operator|.
name|InterfaceStability
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
name|util
operator|.
name|Shell
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
name|webapp
operator|.
name|dao
operator|.
name|gpu
operator|.
name|GpuDeviceInformation
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
name|webapp
operator|.
name|dao
operator|.
name|gpu
operator|.
name|GpuDeviceInformationParser
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
name|webapp
operator|.
name|dao
operator|.
name|gpu
operator|.
name|PerGpuDeviceInformation
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
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|GpuDiscoverer
specifier|public
class|class
name|GpuDiscoverer
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
name|GpuDiscoverer
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|DEFAULT_BINARY_NAME
specifier|protected
specifier|static
specifier|final
name|String
name|DEFAULT_BINARY_NAME
init|=
literal|"nvidia-smi"
decl_stmt|;
comment|// When executable path not set, try to search default dirs
comment|// By default search /usr/bin, /bin, and /usr/local/nvidia/bin (when
comment|// launched by nvidia-docker.
DECL|field|DEFAULT_BINARY_SEARCH_DIRS
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|DEFAULT_BINARY_SEARCH_DIRS
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"/usr/bin"
argument_list|,
literal|"/bin"
argument_list|,
literal|"/usr/local/nvidia/bin"
argument_list|)
decl_stmt|;
comment|// command should not run more than 10 sec.
DECL|field|MAX_EXEC_TIMEOUT_MS
specifier|private
specifier|static
specifier|final
name|int
name|MAX_EXEC_TIMEOUT_MS
init|=
literal|10
operator|*
literal|1000
decl_stmt|;
DECL|field|MAX_REPEATED_ERROR_ALLOWED
specifier|private
specifier|static
specifier|final
name|int
name|MAX_REPEATED_ERROR_ALLOWED
init|=
literal|10
decl_stmt|;
DECL|field|instance
specifier|private
specifier|static
name|GpuDiscoverer
name|instance
decl_stmt|;
static|static
block|{
name|instance
operator|=
operator|new
name|GpuDiscoverer
argument_list|()
expr_stmt|;
block|}
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
DECL|field|pathOfGpuBinary
specifier|private
name|String
name|pathOfGpuBinary
init|=
literal|null
decl_stmt|;
DECL|field|environment
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|parser
specifier|private
name|GpuDeviceInformationParser
name|parser
init|=
operator|new
name|GpuDeviceInformationParser
argument_list|()
decl_stmt|;
DECL|field|numOfErrorExecutionSinceLastSucceed
specifier|private
name|int
name|numOfErrorExecutionSinceLastSucceed
init|=
literal|0
decl_stmt|;
DECL|field|lastDiscoveredGpuInformation
name|GpuDeviceInformation
name|lastDiscoveredGpuInformation
init|=
literal|null
decl_stmt|;
DECL|method|validateConfOrThrowException ()
specifier|private
name|void
name|validateConfOrThrowException
parameter_list|()
throws|throws
name|YarnException
block|{
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Please initialize (call initialize) before use "
operator|+
name|GpuDiscoverer
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Get GPU device information from system.    * This need to be called after initialize.    *    * Please note that this only works on *NIX platform, so external caller    * need to make sure this.    *    * @return GpuDeviceInformation    * @throws YarnException when any error happens    */
DECL|method|getGpuDeviceInformation ()
specifier|public
specifier|synchronized
name|GpuDeviceInformation
name|getGpuDeviceInformation
parameter_list|()
throws|throws
name|YarnException
block|{
name|validateConfOrThrowException
argument_list|()
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|pathOfGpuBinary
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Failed to find GPU discovery executable, please double check "
operator|+
name|YarnConfiguration
operator|.
name|NM_GPU_PATH_TO_EXEC
operator|+
literal|" setting."
argument_list|)
throw|;
block|}
if|if
condition|(
name|numOfErrorExecutionSinceLastSucceed
operator|==
name|MAX_REPEATED_ERROR_ALLOWED
condition|)
block|{
name|String
name|msg
init|=
literal|"Failed to execute GPU device information detection script for "
operator|+
name|MAX_REPEATED_ERROR_ALLOWED
operator|+
literal|" times, skip following executions."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|String
name|output
decl_stmt|;
try|try
block|{
name|output
operator|=
name|Shell
operator|.
name|execCommand
argument_list|(
name|environment
argument_list|,
operator|new
name|String
index|[]
block|{
name|pathOfGpuBinary
block|,
literal|"-x"
block|,
literal|"-q"
block|}
argument_list|,
name|MAX_EXEC_TIMEOUT_MS
argument_list|)
expr_stmt|;
name|GpuDeviceInformation
name|info
init|=
name|parser
operator|.
name|parseXml
argument_list|(
name|output
argument_list|)
decl_stmt|;
name|numOfErrorExecutionSinceLastSucceed
operator|=
literal|0
expr_stmt|;
name|lastDiscoveredGpuInformation
operator|=
name|info
expr_stmt|;
return|return
name|info
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|numOfErrorExecutionSinceLastSucceed
operator|++
expr_stmt|;
name|String
name|msg
init|=
literal|"Failed to execute "
operator|+
name|pathOfGpuBinary
operator|+
literal|" exception message:"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|", continue ..."
decl_stmt|;
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
name|msg
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|numOfErrorExecutionSinceLastSucceed
operator|++
expr_stmt|;
name|String
name|msg
init|=
literal|"Failed to parse xml output"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
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
name|warn
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
block|}
comment|/**    * Get list of GPU devices usable by YARN.    *    * @return List of GPU devices    * @throws YarnException when any issue happens    */
DECL|method|getGpusUsableByYarn ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|GpuDevice
argument_list|>
name|getGpusUsableByYarn
parameter_list|()
throws|throws
name|YarnException
block|{
name|validateConfOrThrowException
argument_list|()
expr_stmt|;
name|String
name|allowedDevicesStr
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_GPU_ALLOWED_DEVICES
argument_list|,
name|YarnConfiguration
operator|.
name|AUTOMATICALLY_DISCOVER_GPU_DEVICES
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|GpuDevice
argument_list|>
name|gpuDevices
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|allowedDevicesStr
operator|.
name|equals
argument_list|(
name|YarnConfiguration
operator|.
name|AUTOMATICALLY_DISCOVER_GPU_DEVICES
argument_list|)
condition|)
block|{
comment|// Get gpu device information from system.
if|if
condition|(
literal|null
operator|==
name|lastDiscoveredGpuInformation
condition|)
block|{
name|String
name|msg
init|=
name|YarnConfiguration
operator|.
name|NM_GPU_ALLOWED_DEVICES
operator|+
literal|" is set to "
operator|+
name|YarnConfiguration
operator|.
name|AUTOMATICALLY_DISCOVER_GPU_DEVICES
operator|+
literal|", however automatically discovering "
operator|+
literal|"GPU information failed, please check NodeManager log for more"
operator|+
literal|" details, as an alternative, admin can specify "
operator|+
name|YarnConfiguration
operator|.
name|NM_GPU_ALLOWED_DEVICES
operator|+
literal|" manually to enable GPU isolation."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
if|if
condition|(
name|lastDiscoveredGpuInformation
operator|.
name|getGpus
argument_list|()
operator|!=
literal|null
condition|)
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
name|lastDiscoveredGpuInformation
operator|.
name|getGpus
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|PerGpuDeviceInformation
argument_list|>
name|gpuInfos
init|=
name|lastDiscoveredGpuInformation
operator|.
name|getGpus
argument_list|()
decl_stmt|;
name|gpuDevices
operator|.
name|add
argument_list|(
operator|new
name|GpuDevice
argument_list|(
name|i
argument_list|,
name|gpuInfos
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getMinorNumber
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
for|for
control|(
name|String
name|s
range|:
name|allowedDevicesStr
operator|.
name|split
argument_list|(
literal|","
argument_list|)
control|)
block|{
if|if
condition|(
name|s
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
index|[]
name|kv
init|=
name|s
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
name|kv
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Illegal format, it should be index:minor_number format, now it="
operator|+
name|s
argument_list|)
throw|;
block|}
name|gpuDevices
operator|.
name|add
argument_list|(
operator|new
name|GpuDevice
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|kv
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|kv
index|[
literal|1
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Allowed GPU devices:"
operator|+
name|gpuDevices
argument_list|)
expr_stmt|;
block|}
return|return
name|gpuDevices
return|;
block|}
DECL|method|initialize (Configuration conf)
specifier|public
specifier|synchronized
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|YarnException
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|numOfErrorExecutionSinceLastSucceed
operator|=
literal|0
expr_stmt|;
name|String
name|pathToExecutable
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_GPU_PATH_TO_EXEC
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_GPU_PATH_TO_EXEC
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathToExecutable
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|pathToExecutable
operator|=
name|DEFAULT_BINARY_NAME
expr_stmt|;
block|}
comment|// Validate file existence
name|File
name|binaryPath
init|=
operator|new
name|File
argument_list|(
name|pathToExecutable
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|binaryPath
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// When binary not exist, use default setting.
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|dir
range|:
name|DEFAULT_BINARY_SEARCH_DIRS
control|)
block|{
name|binaryPath
operator|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|DEFAULT_BINARY_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|binaryPath
operator|.
name|exists
argument_list|()
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
name|pathOfGpuBinary
operator|=
name|binaryPath
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to locate binary at:"
operator|+
name|binaryPath
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|", please double check ["
operator|+
name|YarnConfiguration
operator|.
name|NM_GPU_PATH_TO_EXEC
operator|+
literal|"] setting. Now use "
operator|+
literal|"default binary:"
operator|+
name|DEFAULT_BINARY_NAME
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// If path specified by user is a directory, use
if|if
condition|(
name|binaryPath
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|binaryPath
operator|=
operator|new
name|File
argument_list|(
name|binaryPath
argument_list|,
name|DEFAULT_BINARY_NAME
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Specified path is a directory, use "
operator|+
name|DEFAULT_BINARY_NAME
operator|+
literal|" under the directory, updated path-to-executable:"
operator|+
name|binaryPath
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Validated
name|pathOfGpuBinary
operator|=
name|binaryPath
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
comment|// Try to discover GPU information once and print
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Trying to discover GPU information ..."
argument_list|)
expr_stmt|;
name|GpuDeviceInformation
name|info
init|=
name|getGpuDeviceInformation
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|info
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Failed to discover GPU information from system, exception message:"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|" continue..."
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getEnvironmentToRunCommand ()
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getEnvironmentToRunCommand
parameter_list|()
block|{
return|return
name|environment
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getPathOfGpuBinary ()
specifier|protected
name|String
name|getPathOfGpuBinary
parameter_list|()
block|{
return|return
name|pathOfGpuBinary
return|;
block|}
DECL|method|getInstance ()
specifier|public
specifier|static
name|GpuDiscoverer
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
block|}
end_class

end_unit

