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
comment|/**  * A JUnit test to test {@link SysInfoLinux}  * Create the fake /proc/ information and verify the parsing and calculation  */
end_comment

begin_class
DECL|class|TestSysInfoLinux
specifier|public
class|class
name|TestSysInfoLinux
block|{
comment|/**    * LinuxResourceCalculatorPlugin with a fake timer    */
DECL|class|FakeLinuxResourceCalculatorPlugin
specifier|static
class|class
name|FakeLinuxResourceCalculatorPlugin
extends|extends
name|SysInfoLinux
block|{
DECL|field|SECTORSIZE
specifier|static
specifier|final
name|int
name|SECTORSIZE
init|=
literal|4096
decl_stmt|;
DECL|field|currentTime
name|long
name|currentTime
init|=
literal|0
decl_stmt|;
DECL|method|FakeLinuxResourceCalculatorPlugin (String procfsMemFile, String procfsCpuFile, String procfsStatFile, String procfsNetFile, String procfsDisksFile, long jiffyLengthInMillis)
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
name|String
name|procfsNetFile
parameter_list|,
name|String
name|procfsDisksFile
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
name|procfsNetFile
argument_list|,
name|procfsDisksFile
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
name|this
operator|.
name|getJiffyLengthInMillis
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readDiskBlockInformation (String diskName, int defSector)
name|int
name|readDiskBlockInformation
parameter_list|(
name|String
name|diskName
parameter_list|,
name|int
name|defSector
parameter_list|)
block|{
return|return
name|SECTORSIZE
return|;
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
DECL|field|FAKE_NETFILE
specifier|private
specifier|static
specifier|final
name|String
name|FAKE_NETFILE
decl_stmt|;
DECL|field|FAKE_DISKSFILE
specifier|private
specifier|static
specifier|final
name|String
name|FAKE_DISKSFILE
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
name|FAKE_NETFILE
operator|=
name|TEST_ROOT_DIR
operator|+
name|File
operator|.
name|separator
operator|+
literal|"NETINFO_"
operator|+
name|randomNum
expr_stmt|;
name|FAKE_DISKSFILE
operator|=
name|TEST_ROOT_DIR
operator|+
name|File
operator|.
name|separator
operator|+
literal|"DISKSINFO_"
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
name|FAKE_NETFILE
argument_list|,
name|FAKE_DISKSFILE
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
literal|"physical id : %s\n"
operator|+
literal|"siblings  : 2\n"
operator|+
literal|"core id   : %s\n"
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
DECL|field|NETINFO_FORMAT
specifier|static
specifier|final
name|String
name|NETINFO_FORMAT
init|=
literal|"Inter-|   Receive                                                |  Transmit\n"
operator|+
literal|"face |bytes    packets errs drop fifo frame compressed multicast|bytes    packets"
operator|+
literal|"errs drop fifo colls carrier compressed\n"
operator|+
literal|"   lo: 42236310  563003    0    0    0     0          0         0 42236310  563003    "
operator|+
literal|"0    0    0     0       0          0\n"
operator|+
literal|" eth0: %d 3452527    0    0    0     0          0    299787 %d 1866280    0    0    "
operator|+
literal|"0     0       0          0\n"
operator|+
literal|" eth1: %d 3152521    0    0    0     0          0    219781 %d 1866290    0    0    "
operator|+
literal|"0     0       0          0\n"
decl_stmt|;
DECL|field|DISKSINFO_FORMAT
specifier|static
specifier|final
name|String
name|DISKSINFO_FORMAT
init|=
literal|"1       0 ram0 0 0 0 0 0 0 0 0 0 0 0\n"
operator|+
literal|"1       1 ram1 0 0 0 0 0 0 0 0 0 0 0\n"
operator|+
literal|"1       2 ram2 0 0 0 0 0 0 0 0 0 0 0\n"
operator|+
literal|"1       3 ram3 0 0 0 0 0 0 0 0 0 0 0\n"
operator|+
literal|"1       4 ram4 0 0 0 0 0 0 0 0 0 0 0\n"
operator|+
literal|"1       5 ram5 0 0 0 0 0 0 0 0 0 0 0\n"
operator|+
literal|"1       6 ram6 0 0 0 0 0 0 0 0 0 0 0\n"
operator|+
literal|"7       0 loop0 0 0 0 0 0 0 0 0 0 0 0\n"
operator|+
literal|"7       1 loop1 0 0 0 0 0 0 0 0 0 0 0\n"
operator|+
literal|"8       0 sda 82575678 2486518 %d 59876600 3225402 19761924 %d "
operator|+
literal|"6407705 4 48803346 66227952\n"
operator|+
literal|"8       1 sda1 732 289 21354 787 7 3 32 4 0 769 791"
operator|+
literal|"8       2 sda2 744272 2206315 23605200 6742762 336830 2979630 "
operator|+
literal|"26539520 1424776 4 1820130 8165444\n"
operator|+
literal|"8       3 sda3 81830497 279914 17881852954 53132969 2888558 16782291 "
operator|+
literal|"157367552 4982925 0 47077660 58061635\n"
operator|+
literal|"8      32 sdc 10148118 693255 %d 122125461 6090515 401630172 %d 2696685590 "
operator|+
literal|"0 26848216 2818793840\n"
operator|+
literal|"8      33 sdc1 10147917 693230 2054138426 122125426 6090506 401630172 "
operator|+
literal|"3261765880 2696685589 0 26848181 2818793804\n"
operator|+
literal|"8      64 sde 9989771 553047 %d 93407551 5978572 391997273 %d 2388274325 "
operator|+
literal|"0 24396646 2481664818\n"
operator|+
literal|"8      65 sde1 9989570 553022 1943973346 93407489 5978563 391997273 3183807264 "
operator|+
literal|"2388274325 0 24396584 2481666274\n"
operator|+
literal|"8      80 sdf 10197163 693995 %d 144374395 6216644 408395438 %d 2669389056 0 "
operator|+
literal|"26164759 2813746348\n"
operator|+
literal|"8      81 sdf1 10196962 693970 2033452794 144374355 6216635 408395438 3316897064 "
operator|+
literal|"2669389056 0 26164719 2813746308\n"
operator|+
literal|"8     129 sdi1 10078602 657936 2056552626 108362198 6134036 403851153 3279882064 "
operator|+
literal|"2639256086 0 26260432 2747601085\n"
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
argument_list|,
literal|0
argument_list|,
literal|0
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
name|CpuTimeTracker
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
annotation|@
name|Test
DECL|method|testCoreCounts ()
specifier|public
name|void
name|testCoreCounts
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|fileContent
init|=
literal|""
decl_stmt|;
comment|// single core, hyper threading
name|long
name|numProcessors
init|=
literal|2
decl_stmt|;
name|long
name|cpuFrequencyKHz
init|=
literal|2392781
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
operator|=
name|fileContent
operator|.
name|concat
argument_list|(
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
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|fileContent
operator|=
name|fileContent
operator|.
name|concat
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|writeFakeCPUInfoFile
argument_list|(
name|fileContent
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|setReadCpuInfoFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numProcessors
argument_list|,
name|plugin
operator|.
name|getNumProcessors
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|plugin
operator|.
name|getNumCores
argument_list|()
argument_list|)
expr_stmt|;
comment|// single socket quad core, no hyper threading
name|fileContent
operator|=
literal|""
expr_stmt|;
name|numProcessors
operator|=
literal|4
expr_stmt|;
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
operator|=
name|fileContent
operator|.
name|concat
argument_list|(
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
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|fileContent
operator|=
name|fileContent
operator|.
name|concat
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|writeFakeCPUInfoFile
argument_list|(
name|fileContent
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|setReadCpuInfoFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numProcessors
argument_list|,
name|plugin
operator|.
name|getNumProcessors
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|plugin
operator|.
name|getNumCores
argument_list|()
argument_list|)
expr_stmt|;
comment|// dual socket single core, hyper threading
name|fileContent
operator|=
literal|""
expr_stmt|;
name|numProcessors
operator|=
literal|4
expr_stmt|;
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
operator|=
name|fileContent
operator|.
name|concat
argument_list|(
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
argument_list|,
name|i
operator|/
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|fileContent
operator|=
name|fileContent
operator|.
name|concat
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|writeFakeCPUInfoFile
argument_list|(
name|fileContent
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|setReadCpuInfoFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numProcessors
argument_list|,
name|plugin
operator|.
name|getNumProcessors
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|plugin
operator|.
name|getNumCores
argument_list|()
argument_list|)
expr_stmt|;
comment|// dual socket, dual core, no hyper threading
name|fileContent
operator|=
literal|""
expr_stmt|;
name|numProcessors
operator|=
literal|4
expr_stmt|;
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
operator|=
name|fileContent
operator|.
name|concat
argument_list|(
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
argument_list|,
name|i
operator|/
literal|2
argument_list|,
name|i
operator|%
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|fileContent
operator|=
name|fileContent
operator|.
name|concat
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|writeFakeCPUInfoFile
argument_list|(
name|fileContent
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|setReadCpuInfoFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numProcessors
argument_list|,
name|plugin
operator|.
name|getNumProcessors
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|plugin
operator|.
name|getNumCores
argument_list|()
argument_list|)
expr_stmt|;
comment|// dual socket, dual core, hyper threading
name|fileContent
operator|=
literal|""
expr_stmt|;
name|numProcessors
operator|=
literal|8
expr_stmt|;
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
operator|=
name|fileContent
operator|.
name|concat
argument_list|(
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
argument_list|,
name|i
operator|/
literal|4
argument_list|,
operator|(
name|i
operator|%
literal|4
operator|)
operator|/
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|fileContent
operator|=
name|fileContent
operator|.
name|concat
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|writeFakeCPUInfoFile
argument_list|(
name|fileContent
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|setReadCpuInfoFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numProcessors
argument_list|,
name|plugin
operator|.
name|getNumProcessors
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|plugin
operator|.
name|getNumCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|writeFakeCPUInfoFile (String content)
specifier|private
name|void
name|writeFakeCPUInfoFile
parameter_list|(
name|String
name|content
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|tempFile
init|=
operator|new
name|File
argument_list|(
name|FAKE_CPUFILE
argument_list|)
decl_stmt|;
name|FileWriter
name|fWriter
init|=
operator|new
name|FileWriter
argument_list|(
name|FAKE_CPUFILE
argument_list|)
decl_stmt|;
name|tempFile
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
try|try
block|{
name|fWriter
operator|.
name|write
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|fWriter
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test parsing /proc/net/dev    * @throws IOException    */
annotation|@
name|Test
DECL|method|parsingProcNetFile ()
specifier|public
name|void
name|parsingProcNetFile
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|numBytesReadIntf1
init|=
literal|2097172468L
decl_stmt|;
name|long
name|numBytesWrittenIntf1
init|=
literal|1355620114L
decl_stmt|;
name|long
name|numBytesReadIntf2
init|=
literal|1097172460L
decl_stmt|;
name|long
name|numBytesWrittenIntf2
init|=
literal|1055620110L
decl_stmt|;
name|File
name|tempFile
init|=
operator|new
name|File
argument_list|(
name|FAKE_NETFILE
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
name|FAKE_NETFILE
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
name|NETINFO_FORMAT
argument_list|,
name|numBytesReadIntf1
argument_list|,
name|numBytesWrittenIntf1
argument_list|,
name|numBytesReadIntf2
argument_list|,
name|numBytesWrittenIntf2
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
name|getNetworkBytesRead
argument_list|()
argument_list|,
name|numBytesReadIntf1
operator|+
name|numBytesReadIntf2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|plugin
operator|.
name|getNetworkBytesWritten
argument_list|()
argument_list|,
name|numBytesWrittenIntf1
operator|+
name|numBytesWrittenIntf2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test parsing /proc/diskstats    * @throws IOException    */
annotation|@
name|Test
DECL|method|parsingProcDisksFile ()
specifier|public
name|void
name|parsingProcDisksFile
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|numSectorsReadsda
init|=
literal|1790549L
decl_stmt|;
name|long
name|numSectorsWrittensda
init|=
literal|1839071L
decl_stmt|;
name|long
name|numSectorsReadsdc
init|=
literal|20541402L
decl_stmt|;
name|long
name|numSectorsWrittensdc
init|=
literal|32617658L
decl_stmt|;
name|long
name|numSectorsReadsde
init|=
literal|19439751L
decl_stmt|;
name|long
name|numSectorsWrittensde
init|=
literal|31838072L
decl_stmt|;
name|long
name|numSectorsReadsdf
init|=
literal|20334546L
decl_stmt|;
name|long
name|numSectorsWrittensdf
init|=
literal|33168970L
decl_stmt|;
name|File
name|tempFile
init|=
operator|new
name|File
argument_list|(
name|FAKE_DISKSFILE
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
name|FAKE_DISKSFILE
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
name|DISKSINFO_FORMAT
argument_list|,
name|numSectorsReadsda
argument_list|,
name|numSectorsWrittensda
argument_list|,
name|numSectorsReadsdc
argument_list|,
name|numSectorsWrittensdc
argument_list|,
name|numSectorsReadsde
argument_list|,
name|numSectorsWrittensde
argument_list|,
name|numSectorsReadsdf
argument_list|,
name|numSectorsWrittensdf
argument_list|)
argument_list|)
expr_stmt|;
name|fWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|long
name|expectedNumSectorsRead
init|=
name|numSectorsReadsda
operator|+
name|numSectorsReadsdc
operator|+
name|numSectorsReadsde
operator|+
name|numSectorsReadsdf
decl_stmt|;
name|long
name|expectedNumSectorsWritten
init|=
name|numSectorsWrittensda
operator|+
name|numSectorsWrittensdc
operator|+
name|numSectorsWrittensde
operator|+
name|numSectorsWrittensdf
decl_stmt|;
comment|// use non-default sector size
name|int
name|diskSectorSize
init|=
name|FakeLinuxResourceCalculatorPlugin
operator|.
name|SECTORSIZE
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedNumSectorsRead
operator|*
name|diskSectorSize
argument_list|,
name|plugin
operator|.
name|getStorageBytesRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedNumSectorsWritten
operator|*
name|diskSectorSize
argument_list|,
name|plugin
operator|.
name|getStorageBytesWritten
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

