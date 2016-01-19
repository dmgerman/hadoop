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
name|ResourceCalculatorPlugin
import|;
end_import

begin_class
DECL|class|MockResourceCalculatorPlugin
specifier|public
class|class
name|MockResourceCalculatorPlugin
extends|extends
name|ResourceCalculatorPlugin
block|{
annotation|@
name|Override
DECL|method|getVirtualMemorySize ()
specifier|public
name|long
name|getVirtualMemorySize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getPhysicalMemorySize ()
specifier|public
name|long
name|getPhysicalMemorySize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getAvailableVirtualMemorySize ()
specifier|public
name|long
name|getAvailableVirtualMemorySize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getAvailablePhysicalMemorySize ()
specifier|public
name|long
name|getAvailablePhysicalMemorySize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getNumProcessors ()
specifier|public
name|int
name|getNumProcessors
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getNumCores ()
specifier|public
name|int
name|getNumCores
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getCpuFrequency ()
specifier|public
name|long
name|getCpuFrequency
parameter_list|()
block|{
return|return
literal|0
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
DECL|method|getCpuUsagePercentage ()
specifier|public
name|float
name|getCpuUsagePercentage
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

