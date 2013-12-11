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
name|assertTrue
import|;
end_import

begin_class
DECL|class|TestWindowsResourceCalculatorPlugin
specifier|public
class|class
name|TestWindowsResourceCalculatorPlugin
block|{
DECL|class|WindowsResourceCalculatorPluginTester
class|class
name|WindowsResourceCalculatorPluginTester
extends|extends
name|WindowsResourceCalculatorPlugin
block|{
DECL|field|infoStr
specifier|private
name|String
name|infoStr
init|=
literal|null
decl_stmt|;
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
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|parseSystemInfoString ()
specifier|public
name|void
name|parseSystemInfoString
parameter_list|()
block|{
name|WindowsResourceCalculatorPluginTester
name|tester
init|=
operator|new
name|WindowsResourceCalculatorPluginTester
argument_list|()
decl_stmt|;
comment|// info str derived from windows shell command has \r\n termination
name|tester
operator|.
name|infoStr
operator|=
literal|"17177038848,8589467648,15232745472,6400417792,1,2805000,6261812\r\n"
expr_stmt|;
comment|// call a method to refresh values
name|tester
operator|.
name|getAvailablePhysicalMemorySize
argument_list|()
expr_stmt|;
comment|// verify information has been refreshed
name|assertTrue
argument_list|(
name|tester
operator|.
name|vmemSize
operator|==
literal|17177038848L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tester
operator|.
name|memSize
operator|==
literal|8589467648L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tester
operator|.
name|vmemAvailable
operator|==
literal|15232745472L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tester
operator|.
name|memAvailable
operator|==
literal|6400417792L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tester
operator|.
name|numProcessors
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tester
operator|.
name|cpuFrequencyKhz
operator|==
literal|2805000L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tester
operator|.
name|cumulativeCpuTimeMs
operator|==
literal|6261812L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tester
operator|.
name|cpuUsage
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|refreshAndCpuUsage ()
specifier|public
name|void
name|refreshAndCpuUsage
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|WindowsResourceCalculatorPluginTester
name|tester
init|=
operator|new
name|WindowsResourceCalculatorPluginTester
argument_list|()
decl_stmt|;
comment|// info str derived from windows shell command has \r\n termination
name|tester
operator|.
name|infoStr
operator|=
literal|"17177038848,8589467648,15232745472,6400417792,1,2805000,6261812\r\n"
expr_stmt|;
name|tester
operator|.
name|getAvailablePhysicalMemorySize
argument_list|()
expr_stmt|;
comment|// verify information has been refreshed
name|assertTrue
argument_list|(
name|tester
operator|.
name|memAvailable
operator|==
literal|6400417792L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tester
operator|.
name|cpuUsage
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
name|tester
operator|.
name|infoStr
operator|=
literal|"17177038848,8589467648,15232745472,5400417792,1,2805000,6261812\r\n"
expr_stmt|;
name|tester
operator|.
name|getAvailablePhysicalMemorySize
argument_list|()
expr_stmt|;
comment|// verify information has not been refreshed
name|assertTrue
argument_list|(
name|tester
operator|.
name|memAvailable
operator|==
literal|6400417792L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tester
operator|.
name|cpuUsage
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
name|tester
operator|.
name|infoStr
operator|=
literal|"17177038848,8589467648,15232745472,5400417792,1,2805000,6286812\r\n"
expr_stmt|;
name|tester
operator|.
name|getAvailablePhysicalMemorySize
argument_list|()
expr_stmt|;
comment|// verify information has been refreshed
name|assertTrue
argument_list|(
name|tester
operator|.
name|memAvailable
operator|==
literal|5400417792L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tester
operator|.
name|cpuUsage
operator|>=
literal|0.1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|errorInGetSystemInfo ()
specifier|public
name|void
name|errorInGetSystemInfo
parameter_list|()
block|{
name|WindowsResourceCalculatorPluginTester
name|tester
init|=
operator|new
name|WindowsResourceCalculatorPluginTester
argument_list|()
decl_stmt|;
comment|// info str derived from windows shell command has \r\n termination
name|tester
operator|.
name|infoStr
operator|=
literal|null
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

