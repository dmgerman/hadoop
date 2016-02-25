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
name|*
import|;
end_import

begin_class
DECL|class|TestSysInfoWindows
specifier|public
class|class
name|TestSysInfoWindows
block|{
DECL|class|SysInfoWindowsMock
specifier|static
class|class
name|SysInfoWindowsMock
extends|extends
name|SysInfoWindows
block|{
DECL|field|time
specifier|private
name|long
name|time
init|=
name|SysInfoWindows
operator|.
name|REFRESH_INTERVAL_MS
operator|+
literal|1
decl_stmt|;
DECL|field|infoStr
specifier|private
name|String
name|infoStr
init|=
literal|null
decl_stmt|;
DECL|method|setSysinfoString (String infoStr)
name|void
name|setSysinfoString
parameter_list|(
name|String
name|infoStr
parameter_list|)
block|{
name|this
operator|.
name|infoStr
operator|=
name|infoStr
expr_stmt|;
block|}
DECL|method|advance (long dur)
name|void
name|advance
parameter_list|(
name|long
name|dur
parameter_list|)
block|{
name|time
operator|+=
name|dur
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSystemInfoInfoFromShell ()
name|String
name|getSystemInfoInfoFromShell
parameter_list|()
block|{
return|return
name|infoStr
return|;
block|}
annotation|@
name|Override
DECL|method|now ()
name|long
name|now
parameter_list|()
block|{
return|return
name|time
return|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|parseSystemInfoString ()
specifier|public
name|void
name|parseSystemInfoString
parameter_list|()
block|{
name|SysInfoWindowsMock
name|tester
init|=
operator|new
name|SysInfoWindowsMock
argument_list|()
decl_stmt|;
name|tester
operator|.
name|setSysinfoString
argument_list|(
literal|"17177038848,8589467648,15232745472,6400417792,1,2805000,6261812,"
operator|+
literal|"1234567,2345678,3456789,4567890\r\n"
argument_list|)
expr_stmt|;
comment|// info str derived from windows shell command has \r\n termination
name|assertEquals
argument_list|(
literal|17177038848L
argument_list|,
name|tester
operator|.
name|getVirtualMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8589467648L
argument_list|,
name|tester
operator|.
name|getPhysicalMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|15232745472L
argument_list|,
name|tester
operator|.
name|getAvailableVirtualMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6400417792L
argument_list|,
name|tester
operator|.
name|getAvailablePhysicalMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tester
operator|.
name|getNumProcessors
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tester
operator|.
name|getNumCores
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2805000L
argument_list|,
name|tester
operator|.
name|getCpuFrequency
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6261812L
argument_list|,
name|tester
operator|.
name|getCumulativeCpuTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1234567L
argument_list|,
name|tester
operator|.
name|getStorageBytesRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2345678L
argument_list|,
name|tester
operator|.
name|getStorageBytesWritten
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3456789L
argument_list|,
name|tester
operator|.
name|getNetworkBytesRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4567890L
argument_list|,
name|tester
operator|.
name|getNetworkBytesWritten
argument_list|()
argument_list|)
expr_stmt|;
comment|// undef on first call
name|assertEquals
argument_list|(
operator|(
name|float
operator|)
name|CpuTimeTracker
operator|.
name|UNAVAILABLE
argument_list|,
name|tester
operator|.
name|getCpuUsagePercentage
argument_list|()
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|float
operator|)
name|CpuTimeTracker
operator|.
name|UNAVAILABLE
argument_list|,
name|tester
operator|.
name|getNumVCoresUsed
argument_list|()
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|refreshAndCpuUsage ()
specifier|public
name|void
name|refreshAndCpuUsage
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|SysInfoWindowsMock
name|tester
init|=
operator|new
name|SysInfoWindowsMock
argument_list|()
decl_stmt|;
name|tester
operator|.
name|setSysinfoString
argument_list|(
literal|"17177038848,8589467648,15232745472,6400417792,1,2805000,6261812,"
operator|+
literal|"1234567,2345678,3456789,4567890\r\n"
argument_list|)
expr_stmt|;
comment|// info str derived from windows shell command has \r\n termination
name|tester
operator|.
name|getAvailablePhysicalMemorySize
argument_list|()
expr_stmt|;
comment|// verify information has been refreshed
name|assertEquals
argument_list|(
literal|6400417792L
argument_list|,
name|tester
operator|.
name|getAvailablePhysicalMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|float
operator|)
name|CpuTimeTracker
operator|.
name|UNAVAILABLE
argument_list|,
name|tester
operator|.
name|getCpuUsagePercentage
argument_list|()
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|float
operator|)
name|CpuTimeTracker
operator|.
name|UNAVAILABLE
argument_list|,
name|tester
operator|.
name|getNumVCoresUsed
argument_list|()
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|tester
operator|.
name|setSysinfoString
argument_list|(
literal|"17177038848,8589467648,15232745472,5400417792,1,2805000,6263012,"
operator|+
literal|"1234567,2345678,3456789,4567890\r\n"
argument_list|)
expr_stmt|;
name|tester
operator|.
name|getAvailablePhysicalMemorySize
argument_list|()
expr_stmt|;
comment|// verify information has not been refreshed
name|assertEquals
argument_list|(
literal|6400417792L
argument_list|,
name|tester
operator|.
name|getAvailablePhysicalMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|float
operator|)
name|CpuTimeTracker
operator|.
name|UNAVAILABLE
argument_list|,
name|tester
operator|.
name|getCpuUsagePercentage
argument_list|()
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|float
operator|)
name|CpuTimeTracker
operator|.
name|UNAVAILABLE
argument_list|,
name|tester
operator|.
name|getNumVCoresUsed
argument_list|()
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
comment|// advance clock
name|tester
operator|.
name|advance
argument_list|(
name|SysInfoWindows
operator|.
name|REFRESH_INTERVAL_MS
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// verify information has been refreshed
name|assertEquals
argument_list|(
literal|5400417792L
argument_list|,
name|tester
operator|.
name|getAvailablePhysicalMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|6263012
operator|-
literal|6261812
operator|)
operator|*
literal|100F
operator|/
operator|(
name|SysInfoWindows
operator|.
name|REFRESH_INTERVAL_MS
operator|+
literal|1f
operator|)
operator|/
literal|1
argument_list|,
name|tester
operator|.
name|getCpuUsagePercentage
argument_list|()
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|6263012
operator|-
literal|6261812
operator|)
operator|/
operator|(
name|SysInfoWindows
operator|.
name|REFRESH_INTERVAL_MS
operator|+
literal|1f
operator|)
operator|/
literal|1
argument_list|,
name|tester
operator|.
name|getNumVCoresUsed
argument_list|()
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|refreshAndCpuUsageMulticore ()
specifier|public
name|void
name|refreshAndCpuUsageMulticore
parameter_list|()
throws|throws
name|InterruptedException
block|{
comment|// test with 12 cores
name|SysInfoWindowsMock
name|tester
init|=
operator|new
name|SysInfoWindowsMock
argument_list|()
decl_stmt|;
name|tester
operator|.
name|setSysinfoString
argument_list|(
literal|"17177038848,8589467648,15232745472,6400417792,12,2805000,6261812,"
operator|+
literal|"1234567,2345678,3456789,4567890\r\n"
argument_list|)
expr_stmt|;
comment|// verify information has been refreshed
name|assertEquals
argument_list|(
literal|6400417792L
argument_list|,
name|tester
operator|.
name|getAvailablePhysicalMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|tester
operator|.
name|setSysinfoString
argument_list|(
literal|"17177038848,8589467648,15232745472,5400417792,12,2805000,6263012,"
operator|+
literal|"1234567,2345678,3456789,4567890\r\n"
argument_list|)
expr_stmt|;
comment|// verify information has not been refreshed
name|assertEquals
argument_list|(
literal|6400417792L
argument_list|,
name|tester
operator|.
name|getAvailablePhysicalMemorySize
argument_list|()
argument_list|)
expr_stmt|;
comment|// advance clock
name|tester
operator|.
name|advance
argument_list|(
name|SysInfoWindows
operator|.
name|REFRESH_INTERVAL_MS
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// verify information has been refreshed
name|assertEquals
argument_list|(
literal|5400417792L
argument_list|,
name|tester
operator|.
name|getAvailablePhysicalMemorySize
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify information has been refreshed
name|assertEquals
argument_list|(
operator|(
literal|6263012
operator|-
literal|6261812
operator|)
operator|*
literal|100F
operator|/
operator|(
name|SysInfoWindows
operator|.
name|REFRESH_INTERVAL_MS
operator|+
literal|1f
operator|)
operator|/
literal|12
argument_list|,
name|tester
operator|.
name|getCpuUsagePercentage
argument_list|()
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
literal|6263012
operator|-
literal|6261812
operator|)
operator|/
operator|(
name|SysInfoWindows
operator|.
name|REFRESH_INTERVAL_MS
operator|+
literal|1f
operator|)
argument_list|,
name|tester
operator|.
name|getNumVCoresUsed
argument_list|()
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|errorInGetSystemInfo ()
specifier|public
name|void
name|errorInGetSystemInfo
parameter_list|()
block|{
name|SysInfoWindowsMock
name|tester
init|=
operator|new
name|SysInfoWindowsMock
argument_list|()
decl_stmt|;
comment|// info str derived from windows shell command has \r\n termination
name|tester
operator|.
name|setSysinfoString
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// call a method to refresh values
name|tester
operator|.
name|getAvailablePhysicalMemorySize
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

