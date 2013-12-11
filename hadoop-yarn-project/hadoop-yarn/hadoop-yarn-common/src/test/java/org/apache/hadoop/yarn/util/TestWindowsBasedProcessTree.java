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
name|util
operator|.
name|Shell
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
name|assertTrue
import|;
end_import

begin_class
DECL|class|TestWindowsBasedProcessTree
specifier|public
class|class
name|TestWindowsBasedProcessTree
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
name|TestWindowsBasedProcessTree
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|WindowsBasedProcessTreeTester
class|class
name|WindowsBasedProcessTreeTester
extends|extends
name|WindowsBasedProcessTree
block|{
DECL|field|infoStr
name|String
name|infoStr
init|=
literal|null
decl_stmt|;
DECL|method|WindowsBasedProcessTreeTester (String pid)
specifier|public
name|WindowsBasedProcessTreeTester
parameter_list|(
name|String
name|pid
parameter_list|)
block|{
name|super
argument_list|(
name|pid
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAllProcessInfoFromShell ()
name|String
name|getAllProcessInfoFromShell
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
DECL|method|tree ()
specifier|public
name|void
name|tree
parameter_list|()
block|{
if|if
condition|(
operator|!
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Platform not Windows. Not testing"
argument_list|)
expr_stmt|;
return|return;
block|}
name|assertTrue
argument_list|(
literal|"WindowsBasedProcessTree should be available on Windows"
argument_list|,
name|WindowsBasedProcessTree
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
name|WindowsBasedProcessTreeTester
name|pTree
init|=
operator|new
name|WindowsBasedProcessTreeTester
argument_list|(
literal|"-1"
argument_list|)
decl_stmt|;
name|pTree
operator|.
name|infoStr
operator|=
literal|"3524,1024,1024,500\r\n2844,1024,1024,500\r\n"
expr_stmt|;
name|pTree
operator|.
name|updateProcessTree
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|pTree
operator|.
name|getCumulativeVmem
argument_list|()
operator|==
literal|2048
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pTree
operator|.
name|getCumulativeVmem
argument_list|(
literal|0
argument_list|)
operator|==
literal|2048
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pTree
operator|.
name|getCumulativeRssmem
argument_list|()
operator|==
literal|2048
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pTree
operator|.
name|getCumulativeRssmem
argument_list|(
literal|0
argument_list|)
operator|==
literal|2048
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pTree
operator|.
name|getCumulativeCpuTime
argument_list|()
operator|==
literal|1000
argument_list|)
expr_stmt|;
name|pTree
operator|.
name|infoStr
operator|=
literal|"3524,1024,1024,1000\r\n2844,1024,1024,1000\r\n1234,1024,1024,1000\r\n"
expr_stmt|;
name|pTree
operator|.
name|updateProcessTree
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|pTree
operator|.
name|getCumulativeVmem
argument_list|()
operator|==
literal|3072
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pTree
operator|.
name|getCumulativeVmem
argument_list|(
literal|1
argument_list|)
operator|==
literal|2048
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pTree
operator|.
name|getCumulativeRssmem
argument_list|()
operator|==
literal|3072
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pTree
operator|.
name|getCumulativeRssmem
argument_list|(
literal|1
argument_list|)
operator|==
literal|2048
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pTree
operator|.
name|getCumulativeCpuTime
argument_list|()
operator|==
literal|3000
argument_list|)
expr_stmt|;
name|pTree
operator|.
name|infoStr
operator|=
literal|"3524,1024,1024,1500\r\n2844,1024,1024,1500\r\n"
expr_stmt|;
name|pTree
operator|.
name|updateProcessTree
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|pTree
operator|.
name|getCumulativeVmem
argument_list|()
operator|==
literal|2048
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pTree
operator|.
name|getCumulativeVmem
argument_list|(
literal|2
argument_list|)
operator|==
literal|2048
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pTree
operator|.
name|getCumulativeRssmem
argument_list|()
operator|==
literal|2048
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pTree
operator|.
name|getCumulativeRssmem
argument_list|(
literal|2
argument_list|)
operator|==
literal|2048
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pTree
operator|.
name|getCumulativeCpuTime
argument_list|()
operator|==
literal|4000
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

