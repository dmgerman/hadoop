begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.fpga
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
name|fpga
package|;
end_package

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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
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
name|resources
operator|.
name|fpga
operator|.
name|FpgaResourceAllocator
operator|.
name|FpgaDevice
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
name|resourceplugin
operator|.
name|fpga
operator|.
name|IntelFpgaOpenclPlugin
operator|.
name|InnerShellExecutor
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

begin_comment
comment|/**  * Tests for AoclDiagnosticOutputParser.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"checkstyle:linelength"
argument_list|)
DECL|class|TestAoclOutputParser
specifier|public
class|class
name|TestAoclOutputParser
block|{
annotation|@
name|Test
DECL|method|testParsing ()
specifier|public
name|void
name|testParsing
parameter_list|()
block|{
name|String
name|output
init|=
literal|"------------------------- acl0 -------------------------\n"
operator|+
literal|"Vendor: Nallatech ltd\n"
operator|+
literal|"Phys Dev Name  Status   Information\n"
operator|+
literal|"aclnalla_pcie0Passed   nalla_pcie (aclnalla_pcie0)\n"
operator|+
literal|"                       PCIe dev_id = 2494, bus:slot.func = 02:00.00, Gen3 x8\n"
operator|+
literal|"                       FPGA temperature = 53.1 degrees C.\n"
operator|+
literal|"                       Total Card Power Usage = 31.7 Watts.\n"
operator|+
literal|"                       Device Power Usage = 0.0 Watts.\n"
operator|+
literal|"DIAGNOSTIC_PASSED"
operator|+
literal|"---------------------------------------------------------\n"
decl_stmt|;
name|output
operator|=
name|output
operator|+
literal|"------------------------- acl1 -------------------------\n"
operator|+
literal|"Vendor: Nallatech ltd\n"
operator|+
literal|"Phys Dev Name  Status   Information\n"
operator|+
literal|"aclnalla_pcie1Passed   nalla_pcie (aclnalla_pcie1)\n"
operator|+
literal|"                       PCIe dev_id = 2495, bus:slot.func = 03:00.00, Gen3 x8\n"
operator|+
literal|"                       FPGA temperature = 43.1 degrees C.\n"
operator|+
literal|"                       Total Card Power Usage = 11.7 Watts.\n"
operator|+
literal|"                       Device Power Usage = 0.0 Watts.\n"
operator|+
literal|"DIAGNOSTIC_PASSED"
operator|+
literal|"---------------------------------------------------------\n"
expr_stmt|;
name|output
operator|=
name|output
operator|+
literal|"------------------------- acl2 -------------------------\n"
operator|+
literal|"Vendor: Intel(R) Corporation\n"
operator|+
literal|"\n"
operator|+
literal|"Phys Dev Name  Status   Information\n"
operator|+
literal|"\n"
operator|+
literal|"acla10_ref0   Passed   Arria 10 Reference Platform (acla10_ref0)\n"
operator|+
literal|"                       PCIe dev_id = 2494, bus:slot.func = 09:00.00, Gen2 x8\n"
operator|+
literal|"                       FPGA temperature = 50.5781 degrees C.\n"
operator|+
literal|"\n"
operator|+
literal|"DIAGNOSTIC_PASSED\n"
operator|+
literal|"---------------------------------------------------------\n"
expr_stmt|;
name|InnerShellExecutor
name|shellExecutor
init|=
name|mock
argument_list|(
name|InnerShellExecutor
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|shellExecutor
operator|.
name|getMajorAndMinorNumber
argument_list|(
literal|"aclnalla_pcie0"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"247:0"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|shellExecutor
operator|.
name|getMajorAndMinorNumber
argument_list|(
literal|"aclnalla_pcie1"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"247:1"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|shellExecutor
operator|.
name|getMajorAndMinorNumber
argument_list|(
literal|"acla10_ref0"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"246:0"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FpgaDevice
argument_list|>
name|devices
init|=
name|AoclDiagnosticOutputParser
operator|.
name|parseDiagnosticOutput
argument_list|(
name|output
argument_list|,
name|shellExecutor
argument_list|,
literal|"IntelOpenCL"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|devices
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"IntelOpenCL"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"247"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMajor
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMinor
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"acl0"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAliasDevName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"aclnalla_pcie0"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDevName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"02:00.00"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBusNum
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"53.1 degrees C"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTemperature
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"31.7 Watts"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getCardPowerUsage
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"IntelOpenCL"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"247"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getMajor
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getMinor
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"acl1"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getAliasDevName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"aclnalla_pcie1"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getDevName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"03:00.00"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getBusNum
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"43.1 degrees C"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getTemperature
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"11.7 Watts"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getCardPowerUsage
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"IntelOpenCL"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"246"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getMajor
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getMinor
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"acl2"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getAliasDevName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"acla10_ref0"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getDevName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"09:00.00"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getBusNum
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"50.5781 degrees C"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getTemperature
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getCardPowerUsage
argument_list|()
argument_list|)
expr_stmt|;
comment|// Case 2. check alias map
name|assertEquals
argument_list|(
literal|"acl0"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAliasDevName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"acl1"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getAliasDevName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"acl2"
argument_list|,
name|devices
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getAliasDevName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

