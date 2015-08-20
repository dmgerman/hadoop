begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements. See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.util
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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|Arrays
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|fs
operator|.
name|FileUtil
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
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|LinuxContainerExecutor
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
name|privileged
operator|.
name|PrivilegedOperation
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
name|util
operator|.
name|Clock
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
name|util
operator|.
name|ResourceCalculatorPlugin
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
name|util
operator|.
name|SystemClock
import|;
end_import

begin_class
DECL|class|CgroupsLCEResourcesHandler
specifier|public
class|class
name|CgroupsLCEResourcesHandler
implements|implements
name|LCEResourcesHandler
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
name|CgroupsLCEResourcesHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|cgroupPrefix
specifier|private
name|String
name|cgroupPrefix
decl_stmt|;
DECL|field|cgroupMount
specifier|private
name|boolean
name|cgroupMount
decl_stmt|;
DECL|field|cgroupMountPath
specifier|private
name|String
name|cgroupMountPath
decl_stmt|;
DECL|field|cpuWeightEnabled
specifier|private
name|boolean
name|cpuWeightEnabled
init|=
literal|true
decl_stmt|;
DECL|field|strictResourceUsageMode
specifier|private
name|boolean
name|strictResourceUsageMode
init|=
literal|false
decl_stmt|;
DECL|field|MTAB_FILE
specifier|private
specifier|final
name|String
name|MTAB_FILE
init|=
literal|"/proc/mounts"
decl_stmt|;
DECL|field|CGROUPS_FSTYPE
specifier|private
specifier|final
name|String
name|CGROUPS_FSTYPE
init|=
literal|"cgroup"
decl_stmt|;
DECL|field|CONTROLLER_CPU
specifier|private
specifier|final
name|String
name|CONTROLLER_CPU
init|=
literal|"cpu"
decl_stmt|;
DECL|field|CPU_PERIOD_US
specifier|private
specifier|final
name|String
name|CPU_PERIOD_US
init|=
literal|"cfs_period_us"
decl_stmt|;
DECL|field|CPU_QUOTA_US
specifier|private
specifier|final
name|String
name|CPU_QUOTA_US
init|=
literal|"cfs_quota_us"
decl_stmt|;
DECL|field|CPU_DEFAULT_WEIGHT
specifier|private
specifier|final
name|int
name|CPU_DEFAULT_WEIGHT
init|=
literal|1024
decl_stmt|;
comment|// set by kernel
DECL|field|MAX_QUOTA_US
specifier|private
specifier|final
name|int
name|MAX_QUOTA_US
init|=
literal|1000
operator|*
literal|1000
decl_stmt|;
DECL|field|MIN_PERIOD_US
specifier|private
specifier|final
name|int
name|MIN_PERIOD_US
init|=
literal|1000
decl_stmt|;
DECL|field|controllerPaths
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|controllerPaths
decl_stmt|;
comment|// Controller -> path
DECL|field|deleteCgroupTimeout
specifier|private
name|long
name|deleteCgroupTimeout
decl_stmt|;
DECL|field|deleteCgroupDelay
specifier|private
name|long
name|deleteCgroupDelay
decl_stmt|;
comment|// package private for testing purposes
DECL|field|clock
name|Clock
name|clock
decl_stmt|;
DECL|field|yarnProcessors
specifier|private
name|float
name|yarnProcessors
decl_stmt|;
DECL|field|nodeVCores
name|int
name|nodeVCores
decl_stmt|;
DECL|method|CgroupsLCEResourcesHandler ()
specifier|public
name|CgroupsLCEResourcesHandler
parameter_list|()
block|{
name|this
operator|.
name|controllerPaths
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|clock
operator|=
operator|new
name|SystemClock
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
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
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|initConfig ()
name|void
name|initConfig
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|cgroupPrefix
operator|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_HIERARCHY
argument_list|,
literal|"/hadoop-yarn"
argument_list|)
expr_stmt|;
name|this
operator|.
name|cgroupMount
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_MOUNT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|cgroupMountPath
operator|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_MOUNT_PATH
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|deleteCgroupTimeout
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_DELETE_TIMEOUT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LINUX_CONTAINER_CGROUPS_DELETE_TIMEOUT
argument_list|)
expr_stmt|;
name|this
operator|.
name|deleteCgroupDelay
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_DELETE_DELAY
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LINUX_CONTAINER_CGROUPS_DELETE_DELAY
argument_list|)
expr_stmt|;
comment|// remove extra /'s at end or start of cgroupPrefix
if|if
condition|(
name|cgroupPrefix
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
condition|)
block|{
name|cgroupPrefix
operator|=
name|cgroupPrefix
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|strictResourceUsageMode
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_STRICT_RESOURCE_USAGE
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LINUX_CONTAINER_CGROUPS_STRICT_RESOURCE_USAGE
argument_list|)
expr_stmt|;
name|int
name|len
init|=
name|cgroupPrefix
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|cgroupPrefix
operator|.
name|charAt
argument_list|(
name|len
operator|-
literal|1
argument_list|)
operator|==
literal|'/'
condition|)
block|{
name|cgroupPrefix
operator|=
name|cgroupPrefix
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|len
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|init (LinuxContainerExecutor lce)
specifier|public
name|void
name|init
parameter_list|(
name|LinuxContainerExecutor
name|lce
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|init
argument_list|(
name|lce
argument_list|,
name|ResourceCalculatorPlugin
operator|.
name|getResourceCalculatorPlugin
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|init (LinuxContainerExecutor lce, ResourceCalculatorPlugin plugin)
name|void
name|init
parameter_list|(
name|LinuxContainerExecutor
name|lce
parameter_list|,
name|ResourceCalculatorPlugin
name|plugin
parameter_list|)
throws|throws
name|IOException
block|{
name|initConfig
argument_list|()
expr_stmt|;
comment|// mount cgroups if requested
if|if
condition|(
name|cgroupMount
operator|&&
name|cgroupMountPath
operator|!=
literal|null
condition|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|cgroupKVs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|cgroupKVs
operator|.
name|add
argument_list|(
name|CONTROLLER_CPU
operator|+
literal|"="
operator|+
name|cgroupMountPath
operator|+
literal|"/"
operator|+
name|CONTROLLER_CPU
argument_list|)
expr_stmt|;
name|lce
operator|.
name|mountCgroups
argument_list|(
name|cgroupKVs
argument_list|,
name|cgroupPrefix
argument_list|)
expr_stmt|;
block|}
name|initializeControllerPaths
argument_list|()
expr_stmt|;
name|nodeVCores
operator|=
name|NodeManagerHardwareUtils
operator|.
name|getVCores
argument_list|(
name|plugin
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// cap overall usage to the number of cores allocated to YARN
name|yarnProcessors
operator|=
name|NodeManagerHardwareUtils
operator|.
name|getContainersCPUs
argument_list|(
name|plugin
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|int
name|systemProcessors
init|=
name|NodeManagerHardwareUtils
operator|.
name|getNodeCPUs
argument_list|(
name|plugin
argument_list|,
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|systemProcessors
operator|!=
operator|(
name|int
operator|)
name|yarnProcessors
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"YARN containers restricted to "
operator|+
name|yarnProcessors
operator|+
literal|" cores"
argument_list|)
expr_stmt|;
name|int
index|[]
name|limits
init|=
name|getOverallLimits
argument_list|(
name|yarnProcessors
argument_list|)
decl_stmt|;
name|updateCgroup
argument_list|(
name|CONTROLLER_CPU
argument_list|,
literal|""
argument_list|,
name|CPU_PERIOD_US
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|limits
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|updateCgroup
argument_list|(
name|CONTROLLER_CPU
argument_list|,
literal|""
argument_list|,
name|CPU_QUOTA_US
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|limits
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cpuLimitsExist
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Removing CPU constraints for YARN containers."
argument_list|)
expr_stmt|;
name|updateCgroup
argument_list|(
name|CONTROLLER_CPU
argument_list|,
literal|""
argument_list|,
name|CPU_QUOTA_US
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|cpuLimitsExist ()
name|boolean
name|cpuLimitsExist
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|path
init|=
name|pathForCgroup
argument_list|(
name|CONTROLLER_CPU
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|File
name|quotaFile
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|,
name|CONTROLLER_CPU
operator|+
literal|"."
operator|+
name|CPU_QUOTA_US
argument_list|)
decl_stmt|;
if|if
condition|(
name|quotaFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|String
name|contents
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
name|quotaFile
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|int
name|quotaUS
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|contents
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|quotaUS
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getOverallLimits (float yarnProcessors)
name|int
index|[]
name|getOverallLimits
parameter_list|(
name|float
name|yarnProcessors
parameter_list|)
block|{
name|int
index|[]
name|ret
init|=
operator|new
name|int
index|[
literal|2
index|]
decl_stmt|;
if|if
condition|(
name|yarnProcessors
operator|<
literal|0.01f
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Number of processors can't be<= 0."
argument_list|)
throw|;
block|}
name|int
name|quotaUS
init|=
name|MAX_QUOTA_US
decl_stmt|;
name|int
name|periodUS
init|=
call|(
name|int
call|)
argument_list|(
name|MAX_QUOTA_US
operator|/
name|yarnProcessors
argument_list|)
decl_stmt|;
if|if
condition|(
name|yarnProcessors
operator|<
literal|1.0f
condition|)
block|{
name|periodUS
operator|=
name|MAX_QUOTA_US
expr_stmt|;
name|quotaUS
operator|=
call|(
name|int
call|)
argument_list|(
name|periodUS
operator|*
name|yarnProcessors
argument_list|)
expr_stmt|;
if|if
condition|(
name|quotaUS
operator|<
name|MIN_PERIOD_US
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The quota calculated for the cgroup was too low. The minimum value is "
operator|+
name|MIN_PERIOD_US
operator|+
literal|", calculated value is "
operator|+
name|quotaUS
operator|+
literal|". Setting quota to minimum value."
argument_list|)
expr_stmt|;
name|quotaUS
operator|=
name|MIN_PERIOD_US
expr_stmt|;
block|}
block|}
comment|// cfs_period_us can't be less than 1000 microseconds
comment|// if the value of periodUS is less than 1000, we can't really use cgroups
comment|// to limit cpu
if|if
condition|(
name|periodUS
operator|<
name|MIN_PERIOD_US
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The period calculated for the cgroup was too low. The minimum value is "
operator|+
name|MIN_PERIOD_US
operator|+
literal|", calculated value is "
operator|+
name|periodUS
operator|+
literal|". Using all available CPU."
argument_list|)
expr_stmt|;
name|periodUS
operator|=
name|MAX_QUOTA_US
expr_stmt|;
name|quotaUS
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|ret
index|[
literal|0
index|]
operator|=
name|periodUS
expr_stmt|;
name|ret
index|[
literal|1
index|]
operator|=
name|quotaUS
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|isCpuWeightEnabled ()
name|boolean
name|isCpuWeightEnabled
parameter_list|()
block|{
return|return
name|this
operator|.
name|cpuWeightEnabled
return|;
block|}
comment|/*    * Next four functions are for an individual cgroup.    */
DECL|method|pathForCgroup (String controller, String groupName)
specifier|private
name|String
name|pathForCgroup
parameter_list|(
name|String
name|controller
parameter_list|,
name|String
name|groupName
parameter_list|)
block|{
name|String
name|controllerPath
init|=
name|controllerPaths
operator|.
name|get
argument_list|(
name|controller
argument_list|)
decl_stmt|;
return|return
name|controllerPath
operator|+
literal|"/"
operator|+
name|cgroupPrefix
operator|+
literal|"/"
operator|+
name|groupName
return|;
block|}
DECL|method|createCgroup (String controller, String groupName)
specifier|private
name|void
name|createCgroup
parameter_list|(
name|String
name|controller
parameter_list|,
name|String
name|groupName
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|path
init|=
name|pathForCgroup
argument_list|(
name|controller
argument_list|,
name|groupName
argument_list|)
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
literal|"createCgroup: "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
operator|new
name|File
argument_list|(
name|path
argument_list|)
operator|.
name|mkdir
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to create cgroup at "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
DECL|method|updateCgroup (String controller, String groupName, String param, String value)
specifier|private
name|void
name|updateCgroup
parameter_list|(
name|String
name|controller
parameter_list|,
name|String
name|groupName
parameter_list|,
name|String
name|param
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|path
init|=
name|pathForCgroup
argument_list|(
name|controller
argument_list|,
name|groupName
argument_list|)
decl_stmt|;
name|param
operator|=
name|controller
operator|+
literal|"."
operator|+
name|param
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
literal|"updateCgroup: "
operator|+
name|path
operator|+
literal|": "
operator|+
name|param
operator|+
literal|"="
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
name|PrintWriter
name|pw
init|=
literal|null
decl_stmt|;
try|try
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|path
operator|+
literal|"/"
operator|+
name|param
argument_list|)
decl_stmt|;
name|Writer
name|w
init|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|pw
operator|=
operator|new
name|PrintWriter
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|pw
operator|.
name|write
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to set "
operator|+
name|param
operator|+
literal|"="
operator|+
name|value
operator|+
literal|" for cgroup at: "
operator|+
name|path
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|pw
operator|!=
literal|null
condition|)
block|{
name|boolean
name|hasError
init|=
name|pw
operator|.
name|checkError
argument_list|()
decl_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasError
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to set "
operator|+
name|param
operator|+
literal|"="
operator|+
name|value
operator|+
literal|" for cgroup at: "
operator|+
name|path
argument_list|)
throw|;
block|}
if|if
condition|(
name|pw
operator|.
name|checkError
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error while closing cgroup file "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|/*    * Utility routine to print first line from cgroup tasks file    */
DECL|method|logLineFromTasksFile (File cgf)
specifier|private
name|void
name|logLineFromTasksFile
parameter_list|(
name|File
name|cgf
parameter_list|)
block|{
name|String
name|str
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
try|try
init|(
name|BufferedReader
name|inl
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|cgf
operator|+
literal|"/tasks"
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
init|)
block|{
if|if
condition|(
operator|(
name|str
operator|=
name|inl
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"First line in cgroup tasks file: "
operator|+
name|cgf
operator|+
literal|" "
operator|+
name|str
argument_list|)
expr_stmt|;
block|}
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
literal|"Failed to read cgroup tasks file. "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * If tasks file is empty, delete the cgroup.    *    * @param file object referring to the cgroup to be deleted    * @return Boolean indicating whether cgroup was deleted    */
annotation|@
name|VisibleForTesting
DECL|method|checkAndDeleteCgroup (File cgf)
name|boolean
name|checkAndDeleteCgroup
parameter_list|(
name|File
name|cgf
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|boolean
name|deleted
init|=
literal|false
decl_stmt|;
comment|// FileInputStream in = null;
try|try
init|(
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|cgf
operator|+
literal|"/tasks"
argument_list|)
init|)
block|{
if|if
condition|(
name|in
operator|.
name|read
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
comment|/*          * "tasks" file is empty, sleep a bit more and then try to delete the          * cgroup. Some versions of linux will occasionally panic due to a race          * condition in this area, hence the paranoia.          */
name|Thread
operator|.
name|sleep
argument_list|(
name|deleteCgroupDelay
argument_list|)
expr_stmt|;
name|deleted
operator|=
name|cgf
operator|.
name|delete
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|deleted
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed attempt to delete cgroup: "
operator|+
name|cgf
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|logLineFromTasksFile
argument_list|(
name|cgf
argument_list|)
expr_stmt|;
block|}
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
literal|"Failed to read cgroup tasks file. "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|deleted
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|deleteCgroup (String cgroupPath)
name|boolean
name|deleteCgroup
parameter_list|(
name|String
name|cgroupPath
parameter_list|)
block|{
name|boolean
name|deleted
init|=
literal|false
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
literal|"deleteCgroup: "
operator|+
name|cgroupPath
argument_list|)
expr_stmt|;
block|}
name|long
name|start
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
do|do
block|{
try|try
block|{
name|deleted
operator|=
name|checkAndDeleteCgroup
argument_list|(
operator|new
name|File
argument_list|(
name|cgroupPath
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|deleted
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|deleteCgroupDelay
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
comment|// NOP
block|}
block|}
do|while
condition|(
operator|!
name|deleted
operator|&&
operator|(
name|clock
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|)
operator|<
name|deleteCgroupTimeout
condition|)
do|;
if|if
condition|(
operator|!
name|deleted
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to delete cgroup at: "
operator|+
name|cgroupPath
operator|+
literal|", tried to delete for "
operator|+
name|deleteCgroupTimeout
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
return|return
name|deleted
return|;
block|}
comment|/*    * Next three functions operate on all the resources we are enforcing.    */
DECL|method|setupLimits (ContainerId containerId, Resource containerResource)
specifier|private
name|void
name|setupLimits
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|Resource
name|containerResource
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|containerName
init|=
name|containerId
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|isCpuWeightEnabled
argument_list|()
condition|)
block|{
name|int
name|containerVCores
init|=
name|containerResource
operator|.
name|getVirtualCores
argument_list|()
decl_stmt|;
name|createCgroup
argument_list|(
name|CONTROLLER_CPU
argument_list|,
name|containerName
argument_list|)
expr_stmt|;
name|int
name|cpuShares
init|=
name|CPU_DEFAULT_WEIGHT
operator|*
name|containerVCores
decl_stmt|;
name|updateCgroup
argument_list|(
name|CONTROLLER_CPU
argument_list|,
name|containerName
argument_list|,
literal|"shares"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|cpuShares
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|strictResourceUsageMode
condition|)
block|{
if|if
condition|(
name|nodeVCores
operator|!=
name|containerVCores
condition|)
block|{
name|float
name|containerCPU
init|=
operator|(
name|containerVCores
operator|*
name|yarnProcessors
operator|)
operator|/
operator|(
name|float
operator|)
name|nodeVCores
decl_stmt|;
name|int
index|[]
name|limits
init|=
name|getOverallLimits
argument_list|(
name|containerCPU
argument_list|)
decl_stmt|;
name|updateCgroup
argument_list|(
name|CONTROLLER_CPU
argument_list|,
name|containerName
argument_list|,
name|CPU_PERIOD_US
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|limits
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|updateCgroup
argument_list|(
name|CONTROLLER_CPU
argument_list|,
name|containerName
argument_list|,
name|CPU_QUOTA_US
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|limits
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|clearLimits (ContainerId containerId)
specifier|private
name|void
name|clearLimits
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
if|if
condition|(
name|isCpuWeightEnabled
argument_list|()
condition|)
block|{
name|deleteCgroup
argument_list|(
name|pathForCgroup
argument_list|(
name|CONTROLLER_CPU
argument_list|,
name|containerId
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * LCE Resources Handler interface    */
DECL|method|preExecute (ContainerId containerId, Resource containerResource)
specifier|public
name|void
name|preExecute
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|Resource
name|containerResource
parameter_list|)
throws|throws
name|IOException
block|{
name|setupLimits
argument_list|(
name|containerId
argument_list|,
name|containerResource
argument_list|)
expr_stmt|;
block|}
DECL|method|postExecute (ContainerId containerId)
specifier|public
name|void
name|postExecute
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
name|clearLimits
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
DECL|method|getResourcesOption (ContainerId containerId)
specifier|public
name|String
name|getResourcesOption
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
name|String
name|containerName
init|=
name|containerId
operator|.
name|toString
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"cgroups="
argument_list|)
decl_stmt|;
if|if
condition|(
name|isCpuWeightEnabled
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|pathForCgroup
argument_list|(
name|CONTROLLER_CPU
argument_list|,
name|containerName
argument_list|)
operator|+
literal|"/tasks"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|PrivilegedOperation
operator|.
name|LINUX_FILE_PATH_SEPARATOR
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sb
operator|.
name|charAt
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
name|PrivilegedOperation
operator|.
name|LINUX_FILE_PATH_SEPARATOR
condition|)
block|{
name|sb
operator|.
name|deleteCharAt
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* We are looking for entries of the form:    * none /cgroup/path/mem cgroup rw,memory 0 0    *    * Use a simple pattern that splits on the five spaces, and    * grabs the 2, 3, and 4th fields.    */
DECL|field|MTAB_FILE_FORMAT
specifier|private
specifier|static
specifier|final
name|Pattern
name|MTAB_FILE_FORMAT
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^[^\\s]+\\s([^\\s]+)\\s([^\\s]+)\\s([^\\s]+)\\s[^\\s]+\\s[^\\s]+$"
argument_list|)
decl_stmt|;
comment|/*    * Returns a map: path -> mount options    * for mounts with type "cgroup". Cgroup controllers will    * appear in the list of options for a path.    */
DECL|method|parseMtab ()
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|parseMtab
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|ret
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|BufferedReader
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|getMtabFileName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|in
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|fis
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|str
init|=
name|in
operator|.
name|readLine
argument_list|()
init|;
name|str
operator|!=
literal|null
condition|;
name|str
operator|=
name|in
operator|.
name|readLine
argument_list|()
control|)
block|{
name|Matcher
name|m
init|=
name|MTAB_FILE_FORMAT
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|boolean
name|mat
init|=
name|m
operator|.
name|find
argument_list|()
decl_stmt|;
if|if
condition|(
name|mat
condition|)
block|{
name|String
name|path
init|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|String
name|options
init|=
name|m
operator|.
name|group
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|CGROUPS_FSTYPE
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|value
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|options
operator|.
name|split
argument_list|(
literal|","
argument_list|)
argument_list|)
decl_stmt|;
name|ret
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error while reading "
operator|+
name|getMtabFileName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|findControllerInMtab (String controller, Map<String, List<String>> entries)
specifier|private
name|String
name|findControllerInMtab
parameter_list|(
name|String
name|controller
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|entries
parameter_list|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|e
range|:
name|entries
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|contains
argument_list|(
name|controller
argument_list|)
condition|)
return|return
name|e
operator|.
name|getKey
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|initializeControllerPaths ()
specifier|private
name|void
name|initializeControllerPaths
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|controllerPath
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|parsedMtab
init|=
name|parseMtab
argument_list|()
decl_stmt|;
comment|// CPU
name|controllerPath
operator|=
name|findControllerInMtab
argument_list|(
name|CONTROLLER_CPU
argument_list|,
name|parsedMtab
argument_list|)
expr_stmt|;
if|if
condition|(
name|controllerPath
operator|!=
literal|null
condition|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|controllerPath
operator|+
literal|"/"
operator|+
name|this
operator|.
name|cgroupPrefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|FileUtil
operator|.
name|canWrite
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|controllerPaths
operator|.
name|put
argument_list|(
name|CONTROLLER_CPU
argument_list|,
name|controllerPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not able to enforce cpu weights; cannot write "
operator|+
literal|"to cgroup at: "
operator|+
name|controllerPath
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not able to enforce cpu weights; cannot find "
operator|+
literal|"cgroup for cpu controller in "
operator|+
name|getMtabFileName
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getMtabFileName ()
name|String
name|getMtabFileName
parameter_list|()
block|{
return|return
name|MTAB_FILE
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getControllerPaths ()
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getControllerPaths
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|controllerPaths
argument_list|)
return|;
block|}
block|}
end_class

end_unit

