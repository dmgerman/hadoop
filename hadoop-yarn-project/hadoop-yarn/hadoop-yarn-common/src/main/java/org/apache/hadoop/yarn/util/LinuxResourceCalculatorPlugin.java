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
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
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

begin_comment
comment|/**  * Plugin to calculate resource information on Linux systems.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|LinuxResourceCalculatorPlugin
specifier|public
class|class
name|LinuxResourceCalculatorPlugin
extends|extends
name|ResourceCalculatorPlugin
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
name|LinuxResourceCalculatorPlugin
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|UNAVAILABLE
specifier|public
specifier|static
specifier|final
name|int
name|UNAVAILABLE
init|=
operator|-
literal|1
decl_stmt|;
comment|/**    * proc's meminfo virtual file has keys-values in the format    * "key:[ \t]*value[ \t]kB".    */
DECL|field|PROCFS_MEMFILE
specifier|private
specifier|static
specifier|final
name|String
name|PROCFS_MEMFILE
init|=
literal|"/proc/meminfo"
decl_stmt|;
DECL|field|PROCFS_MEMFILE_FORMAT
specifier|private
specifier|static
specifier|final
name|Pattern
name|PROCFS_MEMFILE_FORMAT
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^([a-zA-Z]*):[ \t]*([0-9]*)[ \t]kB"
argument_list|)
decl_stmt|;
comment|// We need the values for the following keys in meminfo
DECL|field|MEMTOTAL_STRING
specifier|private
specifier|static
specifier|final
name|String
name|MEMTOTAL_STRING
init|=
literal|"MemTotal"
decl_stmt|;
DECL|field|SWAPTOTAL_STRING
specifier|private
specifier|static
specifier|final
name|String
name|SWAPTOTAL_STRING
init|=
literal|"SwapTotal"
decl_stmt|;
DECL|field|MEMFREE_STRING
specifier|private
specifier|static
specifier|final
name|String
name|MEMFREE_STRING
init|=
literal|"MemFree"
decl_stmt|;
DECL|field|SWAPFREE_STRING
specifier|private
specifier|static
specifier|final
name|String
name|SWAPFREE_STRING
init|=
literal|"SwapFree"
decl_stmt|;
DECL|field|INACTIVE_STRING
specifier|private
specifier|static
specifier|final
name|String
name|INACTIVE_STRING
init|=
literal|"Inactive"
decl_stmt|;
comment|/**    * Patterns for parsing /proc/cpuinfo    */
DECL|field|PROCFS_CPUINFO
specifier|private
specifier|static
specifier|final
name|String
name|PROCFS_CPUINFO
init|=
literal|"/proc/cpuinfo"
decl_stmt|;
DECL|field|PROCESSOR_FORMAT
specifier|private
specifier|static
specifier|final
name|Pattern
name|PROCESSOR_FORMAT
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^processor[ \t]:[ \t]*([0-9]*)"
argument_list|)
decl_stmt|;
DECL|field|FREQUENCY_FORMAT
specifier|private
specifier|static
specifier|final
name|Pattern
name|FREQUENCY_FORMAT
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^cpu MHz[ \t]*:[ \t]*([0-9.]*)"
argument_list|)
decl_stmt|;
comment|/**    * Pattern for parsing /proc/stat    */
DECL|field|PROCFS_STAT
specifier|private
specifier|static
specifier|final
name|String
name|PROCFS_STAT
init|=
literal|"/proc/stat"
decl_stmt|;
DECL|field|CPU_TIME_FORMAT
specifier|private
specifier|static
specifier|final
name|Pattern
name|CPU_TIME_FORMAT
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^cpu[ \t]*([0-9]*)"
operator|+
literal|"[ \t]*([0-9]*)[ \t]*([0-9]*)[ \t].*"
argument_list|)
decl_stmt|;
DECL|field|procfsMemFile
specifier|private
name|String
name|procfsMemFile
decl_stmt|;
DECL|field|procfsCpuFile
specifier|private
name|String
name|procfsCpuFile
decl_stmt|;
DECL|field|procfsStatFile
specifier|private
name|String
name|procfsStatFile
decl_stmt|;
DECL|field|jiffyLengthInMillis
name|long
name|jiffyLengthInMillis
decl_stmt|;
DECL|field|ramSize
specifier|private
name|long
name|ramSize
init|=
literal|0
decl_stmt|;
DECL|field|swapSize
specifier|private
name|long
name|swapSize
init|=
literal|0
decl_stmt|;
DECL|field|ramSizeFree
specifier|private
name|long
name|ramSizeFree
init|=
literal|0
decl_stmt|;
comment|// free ram space on the machine (kB)
DECL|field|swapSizeFree
specifier|private
name|long
name|swapSizeFree
init|=
literal|0
decl_stmt|;
comment|// free swap space on the machine (kB)
DECL|field|inactiveSize
specifier|private
name|long
name|inactiveSize
init|=
literal|0
decl_stmt|;
comment|// inactive cache memory (kB)
DECL|field|numProcessors
specifier|private
name|int
name|numProcessors
init|=
literal|0
decl_stmt|;
comment|// number of processors on the system
DECL|field|cpuFrequency
specifier|private
name|long
name|cpuFrequency
init|=
literal|0L
decl_stmt|;
comment|// CPU frequency on the system (kHz)
DECL|field|cumulativeCpuTime
specifier|private
name|long
name|cumulativeCpuTime
init|=
literal|0L
decl_stmt|;
comment|// CPU used time since system is on (ms)
DECL|field|lastCumulativeCpuTime
specifier|private
name|long
name|lastCumulativeCpuTime
init|=
literal|0L
decl_stmt|;
comment|// CPU used time read last time (ms)
comment|// Unix timestamp while reading the CPU time (ms)
DECL|field|cpuUsage
specifier|private
name|float
name|cpuUsage
init|=
name|UNAVAILABLE
decl_stmt|;
DECL|field|sampleTime
specifier|private
name|long
name|sampleTime
init|=
name|UNAVAILABLE
decl_stmt|;
DECL|field|lastSampleTime
specifier|private
name|long
name|lastSampleTime
init|=
name|UNAVAILABLE
decl_stmt|;
DECL|field|readMemInfoFile
name|boolean
name|readMemInfoFile
init|=
literal|false
decl_stmt|;
DECL|field|readCpuInfoFile
name|boolean
name|readCpuInfoFile
init|=
literal|false
decl_stmt|;
comment|/**    * Get current time    * @return Unix time stamp in millisecond    */
DECL|method|getCurrentTime ()
name|long
name|getCurrentTime
parameter_list|()
block|{
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
return|;
block|}
DECL|method|LinuxResourceCalculatorPlugin ()
specifier|public
name|LinuxResourceCalculatorPlugin
parameter_list|()
block|{
name|procfsMemFile
operator|=
name|PROCFS_MEMFILE
expr_stmt|;
name|procfsCpuFile
operator|=
name|PROCFS_CPUINFO
expr_stmt|;
name|procfsStatFile
operator|=
name|PROCFS_STAT
expr_stmt|;
name|jiffyLengthInMillis
operator|=
name|ProcfsBasedProcessTree
operator|.
name|JIFFY_LENGTH_IN_MILLIS
expr_stmt|;
block|}
comment|/**    * Constructor which allows assigning the /proc/ directories. This will be    * used only in unit tests    * @param procfsMemFile fake file for /proc/meminfo    * @param procfsCpuFile fake file for /proc/cpuinfo    * @param procfsStatFile fake file for /proc/stat    * @param jiffyLengthInMillis fake jiffy length value    */
DECL|method|LinuxResourceCalculatorPlugin (String procfsMemFile, String procfsCpuFile, String procfsStatFile, long jiffyLengthInMillis)
specifier|public
name|LinuxResourceCalculatorPlugin
parameter_list|(
name|String
name|procfsMemFile
parameter_list|,
name|String
name|procfsCpuFile
parameter_list|,
name|String
name|procfsStatFile
parameter_list|,
name|long
name|jiffyLengthInMillis
parameter_list|)
block|{
name|this
operator|.
name|procfsMemFile
operator|=
name|procfsMemFile
expr_stmt|;
name|this
operator|.
name|procfsCpuFile
operator|=
name|procfsCpuFile
expr_stmt|;
name|this
operator|.
name|procfsStatFile
operator|=
name|procfsStatFile
expr_stmt|;
name|this
operator|.
name|jiffyLengthInMillis
operator|=
name|jiffyLengthInMillis
expr_stmt|;
block|}
comment|/**    * Read /proc/meminfo, parse and compute memory information only once    */
DECL|method|readProcMemInfoFile ()
specifier|private
name|void
name|readProcMemInfoFile
parameter_list|()
block|{
name|readProcMemInfoFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Read /proc/meminfo, parse and compute memory information    * @param readAgain if false, read only on the first time    */
DECL|method|readProcMemInfoFile (boolean readAgain)
specifier|private
name|void
name|readProcMemInfoFile
parameter_list|(
name|boolean
name|readAgain
parameter_list|)
block|{
if|if
condition|(
name|readMemInfoFile
operator|&&
operator|!
name|readAgain
condition|)
block|{
return|return;
block|}
comment|// Read "/proc/memInfo" file
name|BufferedReader
name|in
init|=
literal|null
decl_stmt|;
name|FileReader
name|fReader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fReader
operator|=
operator|new
name|FileReader
argument_list|(
name|procfsMemFile
argument_list|)
expr_stmt|;
name|in
operator|=
operator|new
name|BufferedReader
argument_list|(
name|fReader
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|f
parameter_list|)
block|{
comment|// shouldn't happen....
return|return;
block|}
name|Matcher
name|mat
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|str
init|=
name|in
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|str
operator|!=
literal|null
condition|)
block|{
name|mat
operator|=
name|PROCFS_MEMFILE_FORMAT
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
expr_stmt|;
if|if
condition|(
name|mat
operator|.
name|find
argument_list|()
condition|)
block|{
if|if
condition|(
name|mat
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|equals
argument_list|(
name|MEMTOTAL_STRING
argument_list|)
condition|)
block|{
name|ramSize
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|mat
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mat
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|equals
argument_list|(
name|SWAPTOTAL_STRING
argument_list|)
condition|)
block|{
name|swapSize
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|mat
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mat
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|equals
argument_list|(
name|MEMFREE_STRING
argument_list|)
condition|)
block|{
name|ramSizeFree
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|mat
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mat
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|equals
argument_list|(
name|SWAPFREE_STRING
argument_list|)
condition|)
block|{
name|swapSizeFree
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|mat
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mat
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|equals
argument_list|(
name|INACTIVE_STRING
argument_list|)
condition|)
block|{
name|inactiveSize
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|mat
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|str
operator|=
name|in
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|io
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error reading the stream "
operator|+
name|io
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// Close the streams
try|try
block|{
name|fReader
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|i
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error closing the stream "
operator|+
name|in
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|i
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error closing the stream "
operator|+
name|fReader
argument_list|)
expr_stmt|;
block|}
block|}
name|readMemInfoFile
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Read /proc/cpuinfo, parse and calculate CPU information    */
DECL|method|readProcCpuInfoFile ()
specifier|private
name|void
name|readProcCpuInfoFile
parameter_list|()
block|{
comment|// This directory needs to be read only once
if|if
condition|(
name|readCpuInfoFile
condition|)
block|{
return|return;
block|}
comment|// Read "/proc/cpuinfo" file
name|BufferedReader
name|in
init|=
literal|null
decl_stmt|;
name|FileReader
name|fReader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fReader
operator|=
operator|new
name|FileReader
argument_list|(
name|procfsCpuFile
argument_list|)
expr_stmt|;
name|in
operator|=
operator|new
name|BufferedReader
argument_list|(
name|fReader
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|f
parameter_list|)
block|{
comment|// shouldn't happen....
return|return;
block|}
name|Matcher
name|mat
init|=
literal|null
decl_stmt|;
try|try
block|{
name|numProcessors
operator|=
literal|0
expr_stmt|;
name|String
name|str
init|=
name|in
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|str
operator|!=
literal|null
condition|)
block|{
name|mat
operator|=
name|PROCESSOR_FORMAT
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
expr_stmt|;
if|if
condition|(
name|mat
operator|.
name|find
argument_list|()
condition|)
block|{
name|numProcessors
operator|++
expr_stmt|;
block|}
name|mat
operator|=
name|FREQUENCY_FORMAT
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
expr_stmt|;
if|if
condition|(
name|mat
operator|.
name|find
argument_list|()
condition|)
block|{
name|cpuFrequency
operator|=
call|(
name|long
call|)
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|mat
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|// kHz
block|}
name|str
operator|=
name|in
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|io
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error reading the stream "
operator|+
name|io
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// Close the streams
try|try
block|{
name|fReader
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|i
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error closing the stream "
operator|+
name|in
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|i
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error closing the stream "
operator|+
name|fReader
argument_list|)
expr_stmt|;
block|}
block|}
name|readCpuInfoFile
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Read /proc/stat file, parse and calculate cumulative CPU    */
DECL|method|readProcStatFile ()
specifier|private
name|void
name|readProcStatFile
parameter_list|()
block|{
comment|// Read "/proc/stat" file
name|BufferedReader
name|in
init|=
literal|null
decl_stmt|;
name|FileReader
name|fReader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fReader
operator|=
operator|new
name|FileReader
argument_list|(
name|procfsStatFile
argument_list|)
expr_stmt|;
name|in
operator|=
operator|new
name|BufferedReader
argument_list|(
name|fReader
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|f
parameter_list|)
block|{
comment|// shouldn't happen....
return|return;
block|}
name|Matcher
name|mat
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|str
init|=
name|in
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|str
operator|!=
literal|null
condition|)
block|{
name|mat
operator|=
name|CPU_TIME_FORMAT
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
expr_stmt|;
if|if
condition|(
name|mat
operator|.
name|find
argument_list|()
condition|)
block|{
name|long
name|uTime
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|mat
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|nTime
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|mat
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|sTime
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|mat
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|cumulativeCpuTime
operator|=
name|uTime
operator|+
name|nTime
operator|+
name|sTime
expr_stmt|;
comment|// milliseconds
break|break;
block|}
name|str
operator|=
name|in
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
name|cumulativeCpuTime
operator|*=
name|jiffyLengthInMillis
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|io
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error reading the stream "
operator|+
name|io
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// Close the streams
try|try
block|{
name|fReader
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|i
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error closing the stream "
operator|+
name|in
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|i
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error closing the stream "
operator|+
name|fReader
argument_list|)
expr_stmt|;
block|}
block|}
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
name|readProcMemInfoFile
argument_list|()
expr_stmt|;
return|return
name|ramSize
operator|*
literal|1024
return|;
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
name|readProcMemInfoFile
argument_list|()
expr_stmt|;
return|return
operator|(
name|ramSize
operator|+
name|swapSize
operator|)
operator|*
literal|1024
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
name|readProcMemInfoFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|(
name|ramSizeFree
operator|+
name|inactiveSize
operator|)
operator|*
literal|1024
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
name|readProcMemInfoFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|(
name|ramSizeFree
operator|+
name|swapSizeFree
operator|+
name|inactiveSize
operator|)
operator|*
literal|1024
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
name|readProcCpuInfoFile
argument_list|()
expr_stmt|;
return|return
name|numProcessors
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
name|readProcCpuInfoFile
argument_list|()
expr_stmt|;
return|return
name|cpuFrequency
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
name|readProcStatFile
argument_list|()
expr_stmt|;
return|return
name|cumulativeCpuTime
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getCpuUsage ()
specifier|public
name|float
name|getCpuUsage
parameter_list|()
block|{
name|readProcStatFile
argument_list|()
expr_stmt|;
name|sampleTime
operator|=
name|getCurrentTime
argument_list|()
expr_stmt|;
if|if
condition|(
name|lastSampleTime
operator|==
name|UNAVAILABLE
operator|||
name|lastSampleTime
operator|>
name|sampleTime
condition|)
block|{
comment|// lastSampleTime> sampleTime may happen when the system time is changed
name|lastSampleTime
operator|=
name|sampleTime
expr_stmt|;
name|lastCumulativeCpuTime
operator|=
name|cumulativeCpuTime
expr_stmt|;
return|return
name|cpuUsage
return|;
block|}
comment|// When lastSampleTime is sufficiently old, update cpuUsage.
comment|// Also take a sample of the current time and cumulative CPU time for the
comment|// use of the next calculation.
specifier|final
name|long
name|MINIMUM_UPDATE_INTERVAL
init|=
literal|10
operator|*
name|jiffyLengthInMillis
decl_stmt|;
if|if
condition|(
name|sampleTime
operator|>
name|lastSampleTime
operator|+
name|MINIMUM_UPDATE_INTERVAL
condition|)
block|{
name|cpuUsage
operator|=
call|(
name|float
call|)
argument_list|(
name|cumulativeCpuTime
operator|-
name|lastCumulativeCpuTime
argument_list|)
operator|*
literal|100F
operator|/
operator|(
call|(
name|float
call|)
argument_list|(
name|sampleTime
operator|-
name|lastSampleTime
argument_list|)
operator|*
name|getNumProcessors
argument_list|()
operator|)
expr_stmt|;
name|lastSampleTime
operator|=
name|sampleTime
expr_stmt|;
name|lastCumulativeCpuTime
operator|=
name|cumulativeCpuTime
expr_stmt|;
block|}
return|return
name|cpuUsage
return|;
block|}
comment|/**    * Test the {@link LinuxResourceCalculatorPlugin}    *    * @param args    */
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|LinuxResourceCalculatorPlugin
name|plugin
init|=
operator|new
name|LinuxResourceCalculatorPlugin
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Physical memory Size (bytes) : "
operator|+
name|plugin
operator|.
name|getPhysicalMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Total Virtual memory Size (bytes) : "
operator|+
name|plugin
operator|.
name|getVirtualMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Available Physical memory Size (bytes) : "
operator|+
name|plugin
operator|.
name|getAvailablePhysicalMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Total Available Virtual memory Size (bytes) : "
operator|+
name|plugin
operator|.
name|getAvailableVirtualMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Number of Processors : "
operator|+
name|plugin
operator|.
name|getNumProcessors
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"CPU frequency (kHz) : "
operator|+
name|plugin
operator|.
name|getCpuFrequency
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Cumulative CPU time (ms) : "
operator|+
name|plugin
operator|.
name|getCumulativeCpuTime
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Sleep so we can compute the CPU usage
name|Thread
operator|.
name|sleep
argument_list|(
literal|500L
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// do nothing
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"CPU usage % : "
operator|+
name|plugin
operator|.
name|getCpuUsage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

