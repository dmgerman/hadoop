begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.com.nec
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
name|com
operator|.
name|nec
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|UncheckedIOException
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
name|Files
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
name|Path
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|lang3
operator|.
name|mutable
operator|.
name|MutableInt
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
name|util
operator|.
name|Shell
operator|.
name|CommandExecutor
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
name|api
operator|.
name|deviceplugin
operator|.
name|Device
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

begin_class
DECL|class|VEDeviceDiscoverer
class|class
name|VEDeviceDiscoverer
block|{
DECL|field|STATE_TERMINATING
specifier|private
specifier|static
specifier|final
name|String
name|STATE_TERMINATING
init|=
literal|"TERMINATING"
decl_stmt|;
DECL|field|STATE_INITIALIZING
specifier|private
specifier|static
specifier|final
name|String
name|STATE_INITIALIZING
init|=
literal|"INITIALIZING"
decl_stmt|;
DECL|field|STATE_OFFLINE
specifier|private
specifier|static
specifier|final
name|String
name|STATE_OFFLINE
init|=
literal|"OFFLINE"
decl_stmt|;
DECL|field|STATE_ONLINE
specifier|private
specifier|static
specifier|final
name|String
name|STATE_ONLINE
init|=
literal|"ONLINE"
decl_stmt|;
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
name|VEDeviceDiscoverer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DEVICE_STATE
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|DEVICE_STATE
init|=
block|{
name|STATE_ONLINE
block|,
name|STATE_OFFLINE
block|,
name|STATE_INITIALIZING
block|,
name|STATE_TERMINATING
block|}
decl_stmt|;
DECL|field|udev
specifier|private
name|UdevUtil
name|udev
decl_stmt|;
specifier|private
name|Function
argument_list|<
name|String
index|[]
argument_list|,
name|CommandExecutor
argument_list|>
DECL|field|commandExecutorProvider
name|commandExecutorProvider
init|=
name|this
operator|::
name|createCommandExecutor
decl_stmt|;
DECL|method|VEDeviceDiscoverer (UdevUtil udevUtil)
name|VEDeviceDiscoverer
parameter_list|(
name|UdevUtil
name|udevUtil
parameter_list|)
block|{
name|udev
operator|=
name|udevUtil
expr_stmt|;
block|}
DECL|method|getDevicesFromPath (String path)
specifier|public
name|Set
argument_list|<
name|Device
argument_list|>
name|getDevicesFromPath
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|MutableInt
name|counter
init|=
operator|new
name|MutableInt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|Files
operator|.
name|walk
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|filter
argument_list|(
name|p
lambda|->
name|p
operator|.
name|toFile
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"veslot"
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|p
lambda|->
name|toDevice
argument_list|(
name|p
argument_list|,
name|counter
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toDevice (Path p, MutableInt counter)
specifier|private
name|Device
name|toDevice
parameter_list|(
name|Path
name|p
parameter_list|,
name|MutableInt
name|counter
parameter_list|)
block|{
name|CommandExecutor
name|executor
init|=
name|commandExecutorProvider
operator|.
name|apply
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"stat"
block|,
literal|"-L"
block|,
literal|"-c"
block|,
literal|"%t:%T:%F"
block|,
name|p
operator|.
name|toString
argument_list|()
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Checking device file: {}"
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|()
expr_stmt|;
name|String
name|statOutput
init|=
name|executor
operator|.
name|getOutput
argument_list|()
decl_stmt|;
name|String
index|[]
name|stat
init|=
name|statOutput
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|int
name|major
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|stat
index|[
literal|0
index|]
argument_list|,
literal|16
argument_list|)
decl_stmt|;
name|int
name|minor
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|stat
index|[
literal|1
index|]
argument_list|,
literal|16
argument_list|)
decl_stmt|;
name|char
name|devType
init|=
name|getDevType
argument_list|(
name|p
argument_list|,
name|stat
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
name|int
name|deviceNumber
init|=
name|makeDev
argument_list|(
name|major
argument_list|,
name|minor
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Device: major: {}, minor: {}, devNo: {}, type: {}"
argument_list|,
name|major
argument_list|,
name|minor
argument_list|,
name|deviceNumber
argument_list|,
name|devType
argument_list|)
expr_stmt|;
name|String
name|sysPath
init|=
name|udev
operator|.
name|getSysPath
argument_list|(
name|deviceNumber
argument_list|,
name|devType
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Device syspath: {}"
argument_list|,
name|sysPath
argument_list|)
expr_stmt|;
name|String
name|deviceState
init|=
name|getDeviceState
argument_list|(
name|sysPath
argument_list|)
decl_stmt|;
name|Device
operator|.
name|Builder
name|builder
init|=
name|Device
operator|.
name|Builder
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setId
argument_list|(
name|counter
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
operator|.
name|setMajorNumber
argument_list|(
name|major
argument_list|)
operator|.
name|setMinorNumber
argument_list|(
name|minor
argument_list|)
operator|.
name|setHealthy
argument_list|(
name|STATE_ONLINE
operator|.
name|equalsIgnoreCase
argument_list|(
name|deviceState
argument_list|)
argument_list|)
operator|.
name|setStatus
argument_list|(
name|deviceState
argument_list|)
operator|.
name|setDevPath
argument_list|(
name|p
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
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
name|UncheckedIOException
argument_list|(
literal|"Cannot execute stat command"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|makeDev (int major, int minor)
specifier|private
name|int
name|makeDev
parameter_list|(
name|int
name|major
parameter_list|,
name|int
name|minor
parameter_list|)
block|{
return|return
name|major
operator|*
literal|256
operator|+
name|minor
return|;
block|}
DECL|method|getDevType (Path p, String fromStat)
specifier|private
name|char
name|getDevType
parameter_list|(
name|Path
name|p
parameter_list|,
name|String
name|fromStat
parameter_list|)
block|{
if|if
condition|(
name|fromStat
operator|.
name|contains
argument_list|(
literal|"character"
argument_list|)
condition|)
block|{
return|return
literal|'c'
return|;
block|}
elseif|else
if|if
condition|(
name|fromStat
operator|.
name|contains
argument_list|(
literal|"block"
argument_list|)
condition|)
block|{
return|return
literal|'b'
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"File is neither a char nor block device: "
operator|+
name|p
argument_list|)
throw|;
block|}
block|}
DECL|method|getDeviceState (String sysPath)
specifier|private
name|String
name|getDeviceState
parameter_list|(
name|String
name|sysPath
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|statePath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|sysPath
argument_list|,
literal|"os_state"
argument_list|)
decl_stmt|;
try|try
init|(
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|statePath
operator|.
name|toString
argument_list|()
argument_list|)
init|)
block|{
name|byte
name|state
init|=
operator|(
name|byte
operator|)
name|fis
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|<
literal|0
operator|||
name|DEVICE_STATE
operator|.
name|length
operator|<=
name|state
condition|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"Unknown (%d)"
argument_list|,
name|state
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|DEVICE_STATE
index|[
name|state
index|]
return|;
block|}
block|}
block|}
DECL|method|createCommandExecutor (String[] command)
specifier|private
name|CommandExecutor
name|createCommandExecutor
parameter_list|(
name|String
index|[]
name|command
parameter_list|)
block|{
return|return
operator|new
name|Shell
operator|.
name|ShellCommandExecutor
argument_list|(
name|command
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setCommandExecutorProvider ( Function<String[], CommandExecutor> provider)
name|void
name|setCommandExecutorProvider
parameter_list|(
name|Function
argument_list|<
name|String
index|[]
argument_list|,
name|CommandExecutor
argument_list|>
name|provider
parameter_list|)
block|{
name|this
operator|.
name|commandExecutorProvider
operator|=
name|provider
expr_stmt|;
block|}
block|}
end_class

end_unit

