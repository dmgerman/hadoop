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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
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
name|Random
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
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * A JUnit test to test {@link LinuxResourceCalculatorPlugin}  * Create the fake /proc/ information and verify the parsing and calculation  */
end_comment

begin_class
DECL|class|TestLinuxResourceCalculatorPlugin
specifier|public
class|class
name|TestLinuxResourceCalculatorPlugin
block|{
comment|/**    * LinuxResourceCalculatorPlugin with a fake timer    */
DECL|class|FakeLinuxResourceCalculatorPlugin
specifier|static
class|class
name|FakeLinuxResourceCalculatorPlugin
extends|extends
name|LinuxResourceCalculatorPlugin
block|{
DECL|field|currentTime
name|long
name|currentTime
init|=
literal|0
decl_stmt|;
DECL|method|FakeLinuxResourceCalculatorPlugin (String procfsMemFile, String procfsCpuFile, String procfsStatFile, long jiffyLengthInMillis)
specifier|public
name|FakeLinuxResourceCalculatorPlugin
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
name|super
argument_list|(
name|procfsMemFile
argument_list|,
name|procfsCpuFile
argument_list|,
name|procfsStatFile
argument_list|,
name|jiffyLengthInMillis
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCurrentTime ()
name|long
name|getCurrentTime
parameter_list|()
block|{
return|return
name|currentTime
return|;
block|}
DECL|method|advanceTime (long adv)
specifier|public
name|void
name|advanceTime
parameter_list|(
name|long
name|adv
parameter_list|)
block|{
name|currentTime
operator|+=
name|adv
operator|*
name|jiffyLengthInMillis
expr_stmt|;
block|}
block|}
DECL|field|plugin
specifier|private
specifier|static
specifier|final
name|FakeLinuxResourceCalculatorPlugin
name|plugin
decl_stmt|;
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
name|String
name|TEST_ROOT_DIR
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|' '
argument_list|,
literal|'+'
argument_list|)
decl_stmt|;
DECL|field|FAKE_MEMFILE
specifier|private
specifier|static
specifier|final
name|String
name|FAKE_MEMFILE
decl_stmt|;
DECL|field|FAKE_CPUFILE
specifier|private
specifier|static
specifier|final
name|String
name|FAKE_CPUFILE
decl_stmt|;
DECL|field|FAKE_STATFILE
specifier|private
specifier|static
specifier|final
name|String
name|FAKE_STATFILE
decl_stmt|;
DECL|field|FAKE_JIFFY_LENGTH
specifier|private
specifier|static
specifier|final
name|long
name|FAKE_JIFFY_LENGTH
init|=
literal|10L
decl_stmt|;
static|static
block|{
name|int
name|randomNum
init|=
operator|(
operator|new
name|Random
argument_list|()
operator|)
operator|.
name|nextInt
argument_list|(
literal|1000000000
argument_list|)
decl_stmt|;
name|FAKE_MEMFILE
operator|=
name|TEST_ROOT_DIR
operator|+
name|File
operator|.
name|separator
operator|+
literal|"MEMINFO_"
operator|+
name|randomNum
expr_stmt|;
name|FAKE_CPUFILE
operator|=
name|TEST_ROOT_DIR
operator|+
name|File
operator|.
name|separator
operator|+
literal|"CPUINFO_"
operator|+
name|randomNum
expr_stmt|;
name|FAKE_STATFILE
operator|=
name|TEST_ROOT_DIR
operator|+
name|File
operator|.
name|separator
operator|+
literal|"STATINFO_"
operator|+
name|randomNum
expr_stmt|;
name|plugin
operator|=
operator|new
name|FakeLinuxResourceCalculatorPlugin
argument_list|(
name|FAKE_MEMFILE
argument_list|,
name|FAKE_CPUFILE
argument_list|,
name|FAKE_STATFILE
argument_list|,
name|FAKE_JIFFY_LENGTH
argument_list|)
expr_stmt|;
block|}
DECL|field|MEMINFO_FORMAT
specifier|static
specifier|final
name|String
name|MEMINFO_FORMAT
init|=
literal|"MemTotal:      %d kB\n"
operator|+
literal|"MemFree:         %d kB\n"
operator|+
literal|"Buffers:        138244 kB\n"
operator|+
literal|"Cached:         947780 kB\n"
operator|+
literal|"SwapCached:     142880 kB\n"
operator|+
literal|"Active:        3229888 kB\n"
operator|+
literal|"Inactive:       %d kB\n"
operator|+
literal|"SwapTotal:     %d kB\n"
operator|+
literal|"SwapFree:      %d kB\n"
operator|+
literal|"Dirty:          122012 kB\n"
operator|+
literal|"Writeback:           0 kB\n"
operator|+
literal|"AnonPages:     2710792 kB\n"
operator|+
literal|"Mapped:          24740 kB\n"
operator|+
literal|"Slab:           132528 kB\n"
operator|+
literal|"SReclaimable:   105096 kB\n"
operator|+
literal|"SUnreclaim:      27432 kB\n"
operator|+
literal|"PageTables:      11448 kB\n"
operator|+
literal|"NFS_Unstable:        0 kB\n"
operator|+
literal|"Bounce:              0 kB\n"
operator|+
literal|"CommitLimit:   4125904 kB\n"
operator|+
literal|"Committed_AS:  4143556 kB\n"
operator|+
literal|"VmallocTotal: 34359738367 kB\n"
operator|+
literal|"VmallocUsed:      1632 kB\n"
operator|+
literal|"VmallocChunk: 34359736375 kB\n"
operator|+
literal|"HugePages_Total:     0\n"
operator|+
literal|"HugePages_Free:      0\n"
operator|+
literal|"HugePages_Rsvd:      0\n"
operator|+
literal|"Hugepagesize:     2048 kB"
decl_stmt|;
DECL|field|CPUINFO_FORMAT
specifier|static
specifier|final
name|String
name|CPUINFO_FORMAT
init|=
literal|"processor : %s\n"
operator|+
literal|"vendor_id : AuthenticAMD\n"
operator|+
literal|"cpu family  : 15\n"
operator|+
literal|"model   : 33\n"
operator|+
literal|"model name  : Dual Core AMD Opteron(tm) Processor 280\n"
operator|+
literal|"stepping  : 2\n"
operator|+
literal|"cpu MHz   : %f\n"
operator|+
literal|"cache size  : 1024 KB\n"
operator|+
literal|"physical id : 0\n"
operator|+
literal|"siblings  : 2\n"
operator|+
literal|"core id   : 0\n"
operator|+
literal|"cpu cores : 2\n"
operator|+
literal|"fpu   : yes\n"
operator|+
literal|"fpu_exception : yes\n"
operator|+
literal|"cpuid level : 1\n"
operator|+
literal|"wp    : yes\n"
operator|+
literal|"flags   : fpu vme de pse tsc msr pae mce cx8 apic sep mtrr pge mca cmov "
operator|+
literal|"pat pse36 clflush mmx fxsr sse sse2 ht syscall nx mmxext fxsr_opt lm "
operator|+
literal|"3dnowext 3dnow pni lahf_lm cmp_legacy\n"
operator|+
literal|"bogomips  : 4792.41\n"
operator|+
literal|"TLB size  : 1024 4K pages\n"
operator|+
literal|"clflush size  : 64\n"
operator|+
literal|"cache_alignment : 64\n"
operator|+
literal|"address sizes : 40 bits physical, 48 bits virtual\n"
operator|+
literal|"power management: ts fid vid ttp"
decl_stmt|;
DECL|field|STAT_FILE_FORMAT
specifier|static
specifier|final
name|String
name|STAT_FILE_FORMAT
init|=
literal|"cpu  %d %d %d 1646495089 831319 48713 164346 0\n"
operator|+
literal|"cpu0 15096055 30805 3823005 411456015 206027 13 14269 0\n"
operator|+
literal|"cpu1 14760561 89890 6432036 408707910 456857 48074 130857 0\n"
operator|+
literal|"cpu2 12761169 20842 3758639 413976772 98028 411 10288 0\n"
operator|+
literal|"cpu3 12355207 47322 5789691 412354390 70406 213 8931 0\n"
operator|+
literal|"intr 114648668 20010764 2 0 945665 2 0 0 0 0 0 0 0 4 0 0 0 0 0 0\n"
operator|+
literal|"ctxt 242017731764\n"
operator|+
literal|"btime 1257808753\n"
operator|+
literal|"processes 26414943\n"
operator|+
literal|"procs_running 1\n"
operator|+
literal|"procs_blocked 0\n"
decl_stmt|;
comment|/**    * Test parsing /proc/stat and /proc/cpuinfo    * @throws IOException    */
annotation|@
name|Test
DECL|method|parsingProcStatAndCpuFile ()
specifier|public
name|void
name|parsingProcStatAndCpuFile
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Write fake /proc/cpuinfo file.
name|long
name|numProcessors
init|=
literal|8
decl_stmt|;
name|long
name|cpuFrequencyKHz
init|=
literal|2392781
decl_stmt|;
name|String
name|fileContent
init|=
literal|""
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
name|numProcessors
condition|;
name|i
operator|++
control|)
block|{
name|fileContent
operator|+=
name|String
operator|.
name|format
argument_list|(
name|CPUINFO_FORMAT
argument_list|,
name|i
argument_list|,
name|cpuFrequencyKHz
operator|/
literal|1000D
argument_list|)
operator|+
literal|"\n"
expr_stmt|;
block|}
name|File
name|tempFile
init|=
operator|new
name|File
argument_list|(
name|FAKE_CPUFILE
argument_list|)
decl_stmt|;
name|tempFile
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|FileWriter
name|fWriter
init|=
operator|new
name|FileWriter
argument_list|(
name|FAKE_CPUFILE
argument_list|)
decl_stmt|;
name|fWriter
operator|.
name|write
argument_list|(
name|fileContent
argument_list|)
expr_stmt|;
name|fWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|plugin
operator|.
name|getNumProcessors
argument_list|()
argument_list|,
name|numProcessors
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|plugin
operator|.
name|getCpuFrequency
argument_list|()
argument_list|,
name|cpuFrequencyKHz
argument_list|)
expr_stmt|;
comment|// Write fake /proc/stat file.
name|long
name|uTime
init|=
literal|54972994
decl_stmt|;
name|long
name|nTime
init|=
literal|188860
decl_stmt|;
name|long
name|sTime
init|=
literal|19803373
decl_stmt|;
name|tempFile
operator|=
operator|new
name|File
argument_list|(
name|FAKE_STATFILE
argument_list|)
expr_stmt|;
name|tempFile
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|updateStatFile
argument_list|(
name|uTime
argument_list|,
name|nTime
argument_list|,
name|sTime
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|plugin
operator|.
name|getCumulativeCpuTime
argument_list|()
argument_list|,
name|FAKE_JIFFY_LENGTH
operator|*
operator|(
name|uTime
operator|+
name|nTime
operator|+
name|sTime
operator|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|plugin
operator|.
name|getCpuUsage
argument_list|()
argument_list|,
call|(
name|float
call|)
argument_list|(
name|LinuxResourceCalculatorPlugin
operator|.
name|UNAVAILABLE
argument_list|)
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
comment|// Advance the time and sample again to test the CPU usage calculation
name|uTime
operator|+=
literal|100L
expr_stmt|;
name|plugin
operator|.
name|advanceTime
argument_list|(
literal|200L
argument_list|)
expr_stmt|;
name|updateStatFile
argument_list|(
name|uTime
argument_list|,
name|nTime
argument_list|,
name|sTime
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|plugin
operator|.
name|getCumulativeCpuTime
argument_list|()
argument_list|,
name|FAKE_JIFFY_LENGTH
operator|*
operator|(
name|uTime
operator|+
name|nTime
operator|+
name|sTime
operator|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|plugin
operator|.
name|getCpuUsage
argument_list|()
argument_list|,
literal|6.25F
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
comment|// Advance the time and sample again. This time, we call getCpuUsage() only.
name|uTime
operator|+=
literal|600L
expr_stmt|;
name|plugin
operator|.
name|advanceTime
argument_list|(
literal|300L
argument_list|)
expr_stmt|;
name|updateStatFile
argument_list|(
name|uTime
argument_list|,
name|nTime
argument_list|,
name|sTime
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|plugin
operator|.
name|getCpuUsage
argument_list|()
argument_list|,
literal|25F
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
comment|// Advance very short period of time (one jiffy length).
comment|// In this case, CPU usage should not be updated.
name|uTime
operator|+=
literal|1L
expr_stmt|;
name|plugin
operator|.
name|advanceTime
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|updateStatFile
argument_list|(
name|uTime
argument_list|,
name|nTime
argument_list|,
name|sTime
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|plugin
operator|.
name|getCumulativeCpuTime
argument_list|()
argument_list|,
name|FAKE_JIFFY_LENGTH
operator|*
operator|(
name|uTime
operator|+
name|nTime
operator|+
name|sTime
operator|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|plugin
operator|.
name|getCpuUsage
argument_list|()
argument_list|,
literal|25F
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
comment|// CPU usage is not updated.
block|}
comment|/**    * Write information to fake /proc/stat file    */
DECL|method|updateStatFile (long uTime, long nTime, long sTime)
specifier|private
name|void
name|updateStatFile
parameter_list|(
name|long
name|uTime
parameter_list|,
name|long
name|nTime
parameter_list|,
name|long
name|sTime
parameter_list|)
throws|throws
name|IOException
block|{
name|FileWriter
name|fWriter
init|=
operator|new
name|FileWriter
argument_list|(
name|FAKE_STATFILE
argument_list|)
decl_stmt|;
name|fWriter
operator|.
name|write
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|STAT_FILE_FORMAT
argument_list|,
name|uTime
argument_list|,
name|nTime
argument_list|,
name|sTime
argument_list|)
argument_list|)
expr_stmt|;
name|fWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test parsing /proc/meminfo    * @throws IOException    */
annotation|@
name|Test
DECL|method|parsingProcMemFile ()
specifier|public
name|void
name|parsingProcMemFile
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|memTotal
init|=
literal|4058864L
decl_stmt|;
name|long
name|memFree
init|=
literal|99632L
decl_stmt|;
name|long
name|inactive
init|=
literal|567732L
decl_stmt|;
name|long
name|swapTotal
init|=
literal|2096472L
decl_stmt|;
name|long
name|swapFree
init|=
literal|1818480L
decl_stmt|;
name|File
name|tempFile
init|=
operator|new
name|File
argument_list|(
name|FAKE_MEMFILE
argument_list|)
decl_stmt|;
name|tempFile
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|FileWriter
name|fWriter
init|=
operator|new
name|FileWriter
argument_list|(
name|FAKE_MEMFILE
argument_list|)
decl_stmt|;
name|fWriter
operator|.
name|write
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|MEMINFO_FORMAT
argument_list|,
name|memTotal
argument_list|,
name|memFree
argument_list|,
name|inactive
argument_list|,
name|swapTotal
argument_list|,
name|swapFree
argument_list|)
argument_list|)
expr_stmt|;
name|fWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|plugin
operator|.
name|getAvailablePhysicalMemorySize
argument_list|()
argument_list|,
literal|1024L
operator|*
operator|(
name|memFree
operator|+
name|inactive
operator|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|plugin
operator|.
name|getAvailableVirtualMemorySize
argument_list|()
argument_list|,
literal|1024L
operator|*
operator|(
name|memFree
operator|+
name|inactive
operator|+
name|swapFree
operator|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|plugin
operator|.
name|getPhysicalMemorySize
argument_list|()
argument_list|,
literal|1024L
operator|*
name|memTotal
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|plugin
operator|.
name|getVirtualMemorySize
argument_list|()
argument_list|,
literal|1024L
operator|*
operator|(
name|memTotal
operator|+
name|swapTotal
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

