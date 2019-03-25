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
name|Collections
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

begin_class
DECL|class|AoclDiagnosticOutputParser
specifier|final
class|class
name|AoclDiagnosticOutputParser
block|{
DECL|method|AoclDiagnosticOutputParser ()
specifier|private
name|AoclDiagnosticOutputParser
parameter_list|()
block|{
comment|// no instances
block|}
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
name|AoclDiagnosticOutputParser
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * One real sample output of Intel FPGA SDK 17.0's "aocl diagnose" is as below:    * "    * aocl diagnose: Running diagnose from /home/fpga/intelFPGA_pro/17.0/hld/board/nalla_pcie/linux64/libexec    *    * ------------------------- acl0 -------------------------    * Vendor: Nallatech ltd    *    * Phys Dev Name  Status   Information    *    * aclnalla_pcie0Passed   nalla_pcie (aclnalla_pcie0)    *                        PCIe dev_id = 2494, bus:slot.func = 02:00.00, Gen3 x8    *                        FPGA temperature = 54.4 degrees C.    *                        Total Card Power Usage = 31.7 Watts.    *                        Device Power Usage = 0.0 Watts.    *    * DIAGNOSTIC_PASSED    * ---------------------------------------------------------    * "    *    * While per Intel's guide, the output(should be outdated or prior SDK version's) is as below:    *    * "    * aocl diagnose: Running diagnostic from ALTERAOCLSDKROOT/board/&lt;board_name&gt;/    *&lt;platform&gt;/libexec    * Verified that the kernel mode driver is installed on the host machine.    * Using board package from vendor:&lt;board_vendor_name&gt;    * Querying information for all supported devices that are installed on the host    * machine ...    *    * device_name Status Information    *    * acl0 Passed&lt;descriptive_board_name&gt;    *             PCIe dev_id =&lt;device_ID&gt;, bus:slot.func = 02:00.00,    *               at Gen 2 with 8 lanes.    *             FPGA temperature=43.0 degrees C.    * acl1 Passed&lt;descriptive_board_name&gt;    *             PCIe dev_id =&lt;device_ID&gt;, bus:slot.func = 03:00.00,    *               at Gen 2 with 8 lanes.    *             FPGA temperature = 35.0 degrees C.    *    * Found 2 active device(s) installed on the host machine, to perform a full    * diagnostic on a specific device, please run aocl diagnose&lt;device_name&gt;    *    * DIAGNOSTIC_PASSED    * "    * But this method only support the first output    * */
DECL|method|parseDiagnosticOutput ( String output, InnerShellExecutor shellExecutor, String fpgaType)
specifier|public
specifier|static
name|List
argument_list|<
name|FpgaDevice
argument_list|>
name|parseDiagnosticOutput
parameter_list|(
name|String
name|output
parameter_list|,
name|InnerShellExecutor
name|shellExecutor
parameter_list|,
name|String
name|fpgaType
parameter_list|)
block|{
if|if
condition|(
name|output
operator|.
name|contains
argument_list|(
literal|"DIAGNOSTIC_PASSED"
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|FpgaDevice
argument_list|>
name|devices
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Matcher
name|headerStartMatcher
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"acl[0-31]"
argument_list|)
operator|.
name|matcher
argument_list|(
name|output
argument_list|)
decl_stmt|;
name|Matcher
name|headerEndMatcher
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?i)DIAGNOSTIC_PASSED"
argument_list|)
operator|.
name|matcher
argument_list|(
name|output
argument_list|)
decl_stmt|;
name|int
name|sectionStartIndex
decl_stmt|;
name|int
name|sectionEndIndex
decl_stmt|;
name|String
name|aliasName
decl_stmt|;
while|while
condition|(
name|headerStartMatcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|sectionStartIndex
operator|=
name|headerStartMatcher
operator|.
name|end
argument_list|()
expr_stmt|;
name|String
name|section
init|=
literal|null
decl_stmt|;
name|aliasName
operator|=
name|headerStartMatcher
operator|.
name|group
argument_list|()
expr_stmt|;
while|while
condition|(
name|headerEndMatcher
operator|.
name|find
argument_list|(
name|sectionStartIndex
argument_list|)
condition|)
block|{
name|sectionEndIndex
operator|=
name|headerEndMatcher
operator|.
name|start
argument_list|()
expr_stmt|;
name|section
operator|=
name|output
operator|.
name|substring
argument_list|(
name|sectionStartIndex
argument_list|,
name|sectionEndIndex
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|section
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unsupported diagnose output"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"aocl output is: "
operator|+
name|output
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|// devName, \(.*\)
comment|// busNum, bus:slot.func\s=\s.*,
comment|// FPGA temperature\s=\s.*
comment|// Total\sCard\sPower\sUsage\s=\s.*
name|String
index|[]
name|fieldRegexes
init|=
operator|new
name|String
index|[]
block|{
literal|"\\(.*\\)\n"
block|,
literal|"(?i)bus:slot.func\\s=\\s.*,"
block|,
literal|"(?i)FPGA temperature\\s=\\s.*"
block|,
literal|"(?i)Total\\sCard\\sPower\\sUsage\\s=\\s.*"
block|}
decl_stmt|;
name|String
index|[]
name|fields
init|=
operator|new
name|String
index|[
literal|4
index|]
decl_stmt|;
name|String
name|tempFieldValue
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
name|fieldRegexes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Matcher
name|fieldMatcher
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|fieldRegexes
index|[
name|i
index|]
argument_list|)
operator|.
name|matcher
argument_list|(
name|section
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fieldMatcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Couldn't find "
operator|+
name|fieldRegexes
index|[
name|i
index|]
operator|+
literal|" pattern"
argument_list|)
expr_stmt|;
name|fields
index|[
name|i
index|]
operator|=
literal|""
expr_stmt|;
continue|continue;
block|}
name|tempFieldValue
operator|=
name|fieldMatcher
operator|.
name|group
argument_list|()
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
comment|// special case for Device name
name|fields
index|[
name|i
index|]
operator|=
name|tempFieldValue
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|tempFieldValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|ss
init|=
name|tempFieldValue
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
index|[
literal|1
index|]
operator|.
name|trim
argument_list|()
decl_stmt|;
name|fields
index|[
name|i
index|]
operator|=
name|ss
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|ss
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|majorMinorNumber
init|=
name|shellExecutor
operator|.
name|getMajorAndMinorNumber
argument_list|(
name|fields
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|majorMinorNumber
condition|)
block|{
name|String
index|[]
name|mmn
init|=
name|majorMinorNumber
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|devices
operator|.
name|add
argument_list|(
operator|new
name|FpgaDevice
argument_list|(
name|fpgaType
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|mmn
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|mmn
index|[
literal|1
index|]
argument_list|)
argument_list|,
name|aliasName
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to retrieve major/minor number for device"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|devices
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The diagnostic has failed"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Output of aocl is: "
operator|+
name|output
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

