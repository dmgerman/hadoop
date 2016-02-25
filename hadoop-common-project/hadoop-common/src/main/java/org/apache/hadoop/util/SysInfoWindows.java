begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
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
name|IOException
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
name|util
operator|.
name|Shell
operator|.
name|ShellCommandExecutor
import|;
end_import

begin_comment
comment|/**  * Plugin to calculate resource information on Windows systems.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|SysInfoWindows
specifier|public
class|class
name|SysInfoWindows
extends|extends
name|SysInfo
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SysInfoWindows
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|vmemSize
specifier|private
name|long
name|vmemSize
decl_stmt|;
DECL|field|memSize
specifier|private
name|long
name|memSize
decl_stmt|;
DECL|field|vmemAvailable
specifier|private
name|long
name|vmemAvailable
decl_stmt|;
DECL|field|memAvailable
specifier|private
name|long
name|memAvailable
decl_stmt|;
DECL|field|numProcessors
specifier|private
name|int
name|numProcessors
decl_stmt|;
DECL|field|cpuFrequencyKhz
specifier|private
name|long
name|cpuFrequencyKhz
decl_stmt|;
DECL|field|cumulativeCpuTimeMs
specifier|private
name|long
name|cumulativeCpuTimeMs
decl_stmt|;
DECL|field|cpuUsage
specifier|private
name|float
name|cpuUsage
decl_stmt|;
DECL|field|storageBytesRead
specifier|private
name|long
name|storageBytesRead
decl_stmt|;
DECL|field|storageBytesWritten
specifier|private
name|long
name|storageBytesWritten
decl_stmt|;
DECL|field|netBytesRead
specifier|private
name|long
name|netBytesRead
decl_stmt|;
DECL|field|netBytesWritten
specifier|private
name|long
name|netBytesWritten
decl_stmt|;
DECL|field|lastRefreshTime
specifier|private
name|long
name|lastRefreshTime
decl_stmt|;
DECL|field|REFRESH_INTERVAL_MS
specifier|static
specifier|final
name|int
name|REFRESH_INTERVAL_MS
init|=
literal|1000
decl_stmt|;
DECL|method|SysInfoWindows ()
specifier|public
name|SysInfoWindows
parameter_list|()
block|{
name|lastRefreshTime
operator|=
literal|0
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|now ()
name|long
name|now
parameter_list|()
block|{
return|return
name|System
operator|.
name|nanoTime
argument_list|()
return|;
block|}
DECL|method|reset ()
name|void
name|reset
parameter_list|()
block|{
name|vmemSize
operator|=
operator|-
literal|1
expr_stmt|;
name|memSize
operator|=
operator|-
literal|1
expr_stmt|;
name|vmemAvailable
operator|=
operator|-
literal|1
expr_stmt|;
name|memAvailable
operator|=
operator|-
literal|1
expr_stmt|;
name|numProcessors
operator|=
operator|-
literal|1
expr_stmt|;
name|cpuFrequencyKhz
operator|=
operator|-
literal|1
expr_stmt|;
name|cumulativeCpuTimeMs
operator|=
operator|-
literal|1
expr_stmt|;
name|cpuUsage
operator|=
operator|-
literal|1
expr_stmt|;
name|storageBytesRead
operator|=
operator|-
literal|1
expr_stmt|;
name|storageBytesWritten
operator|=
operator|-
literal|1
expr_stmt|;
name|netBytesRead
operator|=
operator|-
literal|1
expr_stmt|;
name|netBytesWritten
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|getSystemInfoInfoFromShell ()
name|String
name|getSystemInfoInfoFromShell
parameter_list|()
block|{
try|try
block|{
name|ShellCommandExecutor
name|shellExecutor
init|=
operator|new
name|ShellCommandExecutor
argument_list|(
operator|new
name|String
index|[]
block|{
name|Shell
operator|.
name|getWinUtilsFile
argument_list|()
operator|.
name|getCanonicalPath
argument_list|()
block|,
literal|"systeminfo"
block|}
argument_list|)
decl_stmt|;
name|shellExecutor
operator|.
name|execute
argument_list|()
expr_stmt|;
return|return
name|shellExecutor
operator|.
name|getOutput
argument_list|()
return|;
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
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|refreshIfNeeded ()
name|void
name|refreshIfNeeded
parameter_list|()
block|{
name|long
name|now
init|=
name|now
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|-
name|lastRefreshTime
operator|>
name|REFRESH_INTERVAL_MS
condition|)
block|{
name|long
name|refreshInterval
init|=
name|now
operator|-
name|lastRefreshTime
decl_stmt|;
name|lastRefreshTime
operator|=
name|now
expr_stmt|;
name|long
name|lastCumCpuTimeMs
init|=
name|cumulativeCpuTimeMs
decl_stmt|;
name|reset
argument_list|()
expr_stmt|;
name|String
name|sysInfoStr
init|=
name|getSystemInfoInfoFromShell
argument_list|()
decl_stmt|;
if|if
condition|(
name|sysInfoStr
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|sysInfoSplitCount
init|=
literal|11
decl_stmt|;
name|String
index|[]
name|sysInfo
init|=
name|sysInfoStr
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|sysInfoStr
operator|.
name|indexOf
argument_list|(
literal|"\r\n"
argument_list|)
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
if|if
condition|(
name|sysInfo
operator|.
name|length
operator|==
name|sysInfoSplitCount
condition|)
block|{
try|try
block|{
name|vmemSize
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|sysInfo
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|memSize
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|sysInfo
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|vmemAvailable
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|sysInfo
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|memAvailable
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|sysInfo
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
name|numProcessors
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|sysInfo
index|[
literal|4
index|]
argument_list|)
expr_stmt|;
name|cpuFrequencyKhz
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|sysInfo
index|[
literal|5
index|]
argument_list|)
expr_stmt|;
name|cumulativeCpuTimeMs
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|sysInfo
index|[
literal|6
index|]
argument_list|)
expr_stmt|;
name|storageBytesRead
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|sysInfo
index|[
literal|7
index|]
argument_list|)
expr_stmt|;
name|storageBytesWritten
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|sysInfo
index|[
literal|8
index|]
argument_list|)
expr_stmt|;
name|netBytesRead
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|sysInfo
index|[
literal|9
index|]
argument_list|)
expr_stmt|;
name|netBytesWritten
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|sysInfo
index|[
literal|10
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastCumCpuTimeMs
operator|!=
operator|-
literal|1
condition|)
block|{
comment|/**                * This number will be the aggregated usage across all cores in                * [0.0, 100.0]. For example, it will be 400.0 if there are 8                * cores and each of them is running at 50% utilization.                */
name|cpuUsage
operator|=
operator|(
name|cumulativeCpuTimeMs
operator|-
name|lastCumCpuTimeMs
operator|)
operator|*
literal|100F
operator|/
name|refreshInterval
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error parsing sysInfo"
argument_list|,
name|nfe
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Expected split length of sysInfo to be "
operator|+
name|sysInfoSplitCount
operator|+
literal|". Got "
operator|+
name|sysInfo
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getVirtualMemorySize ()
specifier|public
name|long
name|getVirtualMemorySize
parameter_list|()
block|{
name|refreshIfNeeded
argument_list|()
expr_stmt|;
return|return
name|vmemSize
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getPhysicalMemorySize ()
specifier|public
name|long
name|getPhysicalMemorySize
parameter_list|()
block|{
name|refreshIfNeeded
argument_list|()
expr_stmt|;
return|return
name|memSize
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getAvailableVirtualMemorySize ()
specifier|public
name|long
name|getAvailableVirtualMemorySize
parameter_list|()
block|{
name|refreshIfNeeded
argument_list|()
expr_stmt|;
return|return
name|vmemAvailable
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getAvailablePhysicalMemorySize ()
specifier|public
name|long
name|getAvailablePhysicalMemorySize
parameter_list|()
block|{
name|refreshIfNeeded
argument_list|()
expr_stmt|;
return|return
name|memAvailable
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getNumProcessors ()
specifier|public
name|int
name|getNumProcessors
parameter_list|()
block|{
name|refreshIfNeeded
argument_list|()
expr_stmt|;
return|return
name|numProcessors
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getNumCores ()
specifier|public
name|int
name|getNumCores
parameter_list|()
block|{
return|return
name|getNumProcessors
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getCpuFrequency ()
specifier|public
name|long
name|getCpuFrequency
parameter_list|()
block|{
name|refreshIfNeeded
argument_list|()
expr_stmt|;
return|return
name|cpuFrequencyKhz
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getCumulativeCpuTime ()
specifier|public
name|long
name|getCumulativeCpuTime
parameter_list|()
block|{
name|refreshIfNeeded
argument_list|()
expr_stmt|;
return|return
name|cumulativeCpuTimeMs
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getCpuUsagePercentage ()
specifier|public
name|float
name|getCpuUsagePercentage
parameter_list|()
block|{
name|refreshIfNeeded
argument_list|()
expr_stmt|;
name|float
name|ret
init|=
name|cpuUsage
decl_stmt|;
if|if
condition|(
name|ret
operator|!=
operator|-
literal|1
condition|)
block|{
name|ret
operator|=
name|ret
operator|/
name|numProcessors
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getNumVCoresUsed ()
specifier|public
name|float
name|getNumVCoresUsed
parameter_list|()
block|{
name|refreshIfNeeded
argument_list|()
expr_stmt|;
name|float
name|ret
init|=
name|cpuUsage
decl_stmt|;
if|if
condition|(
name|ret
operator|!=
operator|-
literal|1
condition|)
block|{
name|ret
operator|=
name|ret
operator|/
literal|100F
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getNetworkBytesRead ()
specifier|public
name|long
name|getNetworkBytesRead
parameter_list|()
block|{
name|refreshIfNeeded
argument_list|()
expr_stmt|;
return|return
name|netBytesRead
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getNetworkBytesWritten ()
specifier|public
name|long
name|getNetworkBytesWritten
parameter_list|()
block|{
name|refreshIfNeeded
argument_list|()
expr_stmt|;
return|return
name|netBytesWritten
return|;
block|}
annotation|@
name|Override
DECL|method|getStorageBytesRead ()
specifier|public
name|long
name|getStorageBytesRead
parameter_list|()
block|{
name|refreshIfNeeded
argument_list|()
expr_stmt|;
return|return
name|storageBytesRead
return|;
block|}
annotation|@
name|Override
DECL|method|getStorageBytesWritten ()
specifier|public
name|long
name|getStorageBytesWritten
parameter_list|()
block|{
name|refreshIfNeeded
argument_list|()
expr_stmt|;
return|return
name|storageBytesWritten
return|;
block|}
block|}
end_class

end_unit

