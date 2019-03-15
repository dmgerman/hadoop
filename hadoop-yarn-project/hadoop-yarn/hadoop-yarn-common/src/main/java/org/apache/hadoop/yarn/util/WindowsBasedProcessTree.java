begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
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
name|java
operator|.
name|math
operator|.
name|BigInteger
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
name|Map
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|CpuTimeTracker
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
name|ShellCommandExecutor
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

begin_class
annotation|@
name|Private
DECL|class|WindowsBasedProcessTree
specifier|public
class|class
name|WindowsBasedProcessTree
extends|extends
name|ResourceCalculatorProcessTree
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
name|WindowsBasedProcessTree
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|ProcessInfo
specifier|static
class|class
name|ProcessInfo
block|{
DECL|field|pid
name|String
name|pid
decl_stmt|;
comment|// process pid
DECL|field|vmem
name|long
name|vmem
decl_stmt|;
comment|// virtual memory
DECL|field|workingSet
name|long
name|workingSet
decl_stmt|;
comment|// working set, RAM used
DECL|field|cpuTimeMs
name|long
name|cpuTimeMs
decl_stmt|;
comment|// total cpuTime in millisec
DECL|field|cpuTimeMsDelta
name|long
name|cpuTimeMsDelta
decl_stmt|;
comment|// delta of cpuTime since last update
DECL|field|age
name|int
name|age
init|=
literal|1
decl_stmt|;
block|}
DECL|field|taskProcessId
specifier|private
name|String
name|taskProcessId
init|=
literal|null
decl_stmt|;
DECL|field|cpuTimeMs
specifier|private
name|long
name|cpuTimeMs
init|=
name|UNAVAILABLE
decl_stmt|;
DECL|field|processTree
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ProcessInfo
argument_list|>
name|processTree
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ProcessInfo
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Track CPU utilization. */
DECL|field|cpuTimeTracker
specifier|private
specifier|final
name|CpuTimeTracker
name|cpuTimeTracker
decl_stmt|;
comment|/** Clock to account for CPU utilization. */
DECL|field|clock
specifier|private
name|Clock
name|clock
decl_stmt|;
DECL|method|isAvailable ()
specifier|public
specifier|static
name|boolean
name|isAvailable
parameter_list|()
block|{
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
if|if
condition|(
operator|!
name|Shell
operator|.
name|hasWinutilsPath
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
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
name|getWinUtilsPath
argument_list|()
block|,
literal|"help"
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|shellExecutor
operator|.
name|execute
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
finally|finally
block|{
name|String
name|output
init|=
name|shellExecutor
operator|.
name|getOutput
argument_list|()
decl_stmt|;
if|if
condition|(
name|output
operator|!=
literal|null
operator|&&
name|output
operator|.
name|contains
argument_list|(
literal|"Prints to stdout a list of processes in the task"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Create a monitor for a Windows process tree.    * @param pid Identifier of the job object.    */
DECL|method|WindowsBasedProcessTree (final String pid)
specifier|public
name|WindowsBasedProcessTree
parameter_list|(
specifier|final
name|String
name|pid
parameter_list|)
block|{
name|this
argument_list|(
name|pid
argument_list|,
name|SystemClock
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a monitor for a Windows process tree.    * @param pid Identifier of the job object.    * @param pClock Clock to keep track of time for CPU utilization.    */
DECL|method|WindowsBasedProcessTree (final String pid, final Clock pClock)
specifier|public
name|WindowsBasedProcessTree
parameter_list|(
specifier|final
name|String
name|pid
parameter_list|,
specifier|final
name|Clock
name|pClock
parameter_list|)
block|{
name|super
argument_list|(
name|pid
argument_list|)
expr_stmt|;
name|this
operator|.
name|taskProcessId
operator|=
name|pid
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|pClock
expr_stmt|;
comment|// Instead of jiffies, Windows uses milliseconds directly; 1ms = 1 jiffy
name|this
operator|.
name|cpuTimeTracker
operator|=
operator|new
name|CpuTimeTracker
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
block|}
comment|// helper method to override while testing
DECL|method|getAllProcessInfoFromShell ()
name|String
name|getAllProcessInfoFromShell
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
literal|"task"
block|,
literal|"processList"
block|,
name|taskProcessId
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
comment|/**    * Parses string of process info lines into ProcessInfo objects    * @param processesInfoStr    * @return Map of pid string to ProcessInfo objects    */
DECL|method|createProcessInfo (String processesInfoStr)
name|Map
argument_list|<
name|String
argument_list|,
name|ProcessInfo
argument_list|>
name|createProcessInfo
parameter_list|(
name|String
name|processesInfoStr
parameter_list|)
block|{
name|String
index|[]
name|processesStr
init|=
name|processesInfoStr
operator|.
name|split
argument_list|(
literal|"\r\n"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ProcessInfo
argument_list|>
name|allProcs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ProcessInfo
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|procInfoSplitCount
init|=
literal|4
decl_stmt|;
for|for
control|(
name|String
name|processStr
range|:
name|processesStr
control|)
block|{
if|if
condition|(
name|processStr
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|procInfo
init|=
name|processStr
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
if|if
condition|(
name|procInfo
operator|.
name|length
operator|==
name|procInfoSplitCount
condition|)
block|{
try|try
block|{
name|ProcessInfo
name|pInfo
init|=
operator|new
name|ProcessInfo
argument_list|()
decl_stmt|;
name|pInfo
operator|.
name|pid
operator|=
name|procInfo
index|[
literal|0
index|]
expr_stmt|;
name|pInfo
operator|.
name|vmem
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|procInfo
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|pInfo
operator|.
name|workingSet
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|procInfo
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|pInfo
operator|.
name|cpuTimeMs
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|procInfo
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
name|allProcs
operator|.
name|put
argument_list|(
name|pInfo
operator|.
name|pid
argument_list|,
name|pInfo
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Error parsing procInfo."
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
name|debug
argument_list|(
literal|"Expected split length of proc info to be {}. Got {}"
argument_list|,
name|procInfoSplitCount
argument_list|,
name|procInfo
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|allProcs
return|;
block|}
annotation|@
name|Override
DECL|method|updateProcessTree ()
specifier|public
name|void
name|updateProcessTree
parameter_list|()
block|{
if|if
condition|(
name|taskProcessId
operator|!=
literal|null
condition|)
block|{
comment|// taskProcessId can be null in some tests
name|String
name|processesInfoStr
init|=
name|getAllProcessInfoFromShell
argument_list|()
decl_stmt|;
if|if
condition|(
name|processesInfoStr
operator|!=
literal|null
operator|&&
name|processesInfoStr
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ProcessInfo
argument_list|>
name|allProcessInfo
init|=
name|createProcessInfo
argument_list|(
name|processesInfoStr
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ProcessInfo
argument_list|>
name|entry
range|:
name|allProcessInfo
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|pid
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|ProcessInfo
name|pInfo
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|ProcessInfo
name|oldInfo
init|=
name|processTree
operator|.
name|get
argument_list|(
name|pid
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldInfo
operator|!=
literal|null
condition|)
block|{
comment|// existing process, update age and replace value
name|pInfo
operator|.
name|age
operator|+=
name|oldInfo
operator|.
name|age
expr_stmt|;
comment|// calculate the delta since the last refresh. totals are being kept
comment|// in the WindowsBasedProcessTree object
name|pInfo
operator|.
name|cpuTimeMsDelta
operator|=
name|pInfo
operator|.
name|cpuTimeMs
operator|-
name|oldInfo
operator|.
name|cpuTimeMs
expr_stmt|;
block|}
else|else
block|{
comment|// new process. delta cpu == total cpu
name|pInfo
operator|.
name|cpuTimeMsDelta
operator|=
name|pInfo
operator|.
name|cpuTimeMs
expr_stmt|;
block|}
block|}
name|processTree
operator|.
name|clear
argument_list|()
expr_stmt|;
name|processTree
operator|=
name|allProcessInfo
expr_stmt|;
block|}
else|else
block|{
comment|// clearing process tree to mimic semantics of existing Procfs impl
name|processTree
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|checkPidPgrpidForMatch ()
specifier|public
name|boolean
name|checkPidPgrpidForMatch
parameter_list|()
block|{
comment|// This is always true on Windows, because the pid doubles as a job object
comment|// name for task management.
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getProcessTreeDump ()
specifier|public
name|String
name|getProcessTreeDump
parameter_list|()
block|{
name|StringBuilder
name|ret
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// The header.
name|ret
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"\t|- PID "
operator|+
literal|"CPU_TIME(MILLIS) "
operator|+
literal|"VMEM(BYTES) WORKING_SET(BYTES)%n"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|ProcessInfo
name|p
range|:
name|processTree
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|ret
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"\t|- %s %d %d %d%n"
argument_list|,
name|p
operator|.
name|pid
argument_list|,
name|p
operator|.
name|cpuTimeMs
argument_list|,
name|p
operator|.
name|vmem
argument_list|,
name|p
operator|.
name|workingSet
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getVirtualMemorySize (int olderThanAge)
specifier|public
name|long
name|getVirtualMemorySize
parameter_list|(
name|int
name|olderThanAge
parameter_list|)
block|{
name|long
name|total
init|=
name|UNAVAILABLE
decl_stmt|;
for|for
control|(
name|ProcessInfo
name|p
range|:
name|processTree
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|total
operator|==
name|UNAVAILABLE
condition|)
block|{
name|total
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|p
operator|.
name|age
operator|>
name|olderThanAge
condition|)
block|{
name|total
operator|+=
name|p
operator|.
name|vmem
expr_stmt|;
block|}
block|}
block|}
return|return
name|total
return|;
block|}
annotation|@
name|Override
DECL|method|getRssMemorySize (int olderThanAge)
specifier|public
name|long
name|getRssMemorySize
parameter_list|(
name|int
name|olderThanAge
parameter_list|)
block|{
name|long
name|total
init|=
name|UNAVAILABLE
decl_stmt|;
for|for
control|(
name|ProcessInfo
name|p
range|:
name|processTree
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|total
operator|==
name|UNAVAILABLE
condition|)
block|{
name|total
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|p
operator|.
name|age
operator|>
name|olderThanAge
condition|)
block|{
name|total
operator|+=
name|p
operator|.
name|workingSet
expr_stmt|;
block|}
block|}
block|}
return|return
name|total
return|;
block|}
annotation|@
name|Override
DECL|method|getCumulativeCpuTime ()
specifier|public
name|long
name|getCumulativeCpuTime
parameter_list|()
block|{
for|for
control|(
name|ProcessInfo
name|p
range|:
name|processTree
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|cpuTimeMs
operator|==
name|UNAVAILABLE
condition|)
block|{
name|cpuTimeMs
operator|=
literal|0
expr_stmt|;
block|}
name|cpuTimeMs
operator|+=
name|p
operator|.
name|cpuTimeMsDelta
expr_stmt|;
block|}
return|return
name|cpuTimeMs
return|;
block|}
comment|/**    * Get the number of used ms for all the processes under the monitored job    * object.    * @return Total consumed milliseconds by all processes in the job object.    */
DECL|method|getTotalProcessMs ()
specifier|private
name|BigInteger
name|getTotalProcessMs
parameter_list|()
block|{
name|long
name|totalMs
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ProcessInfo
name|p
range|:
name|processTree
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|totalMs
operator|+=
name|p
operator|.
name|cpuTimeMs
expr_stmt|;
block|}
block|}
return|return
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|totalMs
argument_list|)
return|;
block|}
comment|/**    * Get the CPU usage by all the processes in the process-tree in Windows.    * Note: UNAVAILABLE will be returned in case when CPU usage is not    * available. It is NOT advised to return any other error code.    *    * @return percentage CPU usage since the process-tree was created,    * {@link #UNAVAILABLE} if CPU usage cannot be calculated or not available.    */
annotation|@
name|Override
DECL|method|getCpuUsagePercent ()
specifier|public
name|float
name|getCpuUsagePercent
parameter_list|()
block|{
name|BigInteger
name|processTotalMs
init|=
name|getTotalProcessMs
argument_list|()
decl_stmt|;
name|cpuTimeTracker
operator|.
name|updateElapsedJiffies
argument_list|(
name|processTotalMs
argument_list|,
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|cpuTimeTracker
operator|.
name|getCpuTrackerUsagePercent
argument_list|()
return|;
block|}
block|}
end_class

end_unit

