begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.monitor
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
name|monitor
package|;
end_package

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
name|util
operator|.
name|ResourceCalculatorProcessTree
import|;
end_import

begin_comment
comment|/**  * Mock class to obtain resource usage (CPU).  */
end_comment

begin_class
DECL|class|MockCPUResourceCalculatorProcessTree
specifier|public
class|class
name|MockCPUResourceCalculatorProcessTree
extends|extends
name|ResourceCalculatorProcessTree
block|{
DECL|field|cpuPercentage
specifier|private
name|long
name|cpuPercentage
init|=
name|ResourceCalculatorProcessTree
operator|.
name|UNAVAILABLE
decl_stmt|;
comment|/**    * Constructor for MockCPUResourceCalculatorProcessTree with specified root    * process.    * @param root    */
DECL|method|MockCPUResourceCalculatorProcessTree (String root)
specifier|public
name|MockCPUResourceCalculatorProcessTree
parameter_list|(
name|String
name|root
parameter_list|)
block|{
name|super
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateProcessTree ()
specifier|public
name|void
name|updateProcessTree
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|getProcessTreeDump ()
specifier|public
name|String
name|getProcessTreeDump
parameter_list|()
block|{
return|return
literal|""
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
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|checkPidPgrpidForMatch ()
specifier|public
name|boolean
name|checkPidPgrpidForMatch
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getCpuUsagePercent ()
specifier|public
name|float
name|getCpuUsagePercent
parameter_list|()
block|{
name|long
name|cpu
init|=
name|this
operator|.
name|cpuPercentage
decl_stmt|;
comment|// First getter call will be returned with -1, and other calls will
comment|// return non-zero value as defined below.
if|if
condition|(
name|cpu
operator|==
name|ResourceCalculatorProcessTree
operator|.
name|UNAVAILABLE
condition|)
block|{
comment|// Set a default value other than 0 for test.
name|this
operator|.
name|cpuPercentage
operator|=
literal|50
expr_stmt|;
block|}
return|return
name|cpu
return|;
block|}
block|}
end_class

end_unit

