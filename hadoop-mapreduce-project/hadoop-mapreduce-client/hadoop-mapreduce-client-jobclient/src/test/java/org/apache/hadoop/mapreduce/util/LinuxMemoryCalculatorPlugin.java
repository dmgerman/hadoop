begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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
comment|/**  * Plugin to calculate virtual and physical memories on Linux systems.  * @deprecated   * Use {@link org.apache.hadoop.mapreduce.util.LinuxResourceCalculatorPlugin}  * instead  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|LinuxMemoryCalculatorPlugin
specifier|public
class|class
name|LinuxMemoryCalculatorPlugin
extends|extends
name|MemoryCalculatorPlugin
block|{
DECL|field|resourceCalculatorPlugin
specifier|private
name|LinuxResourceCalculatorPlugin
name|resourceCalculatorPlugin
decl_stmt|;
comment|// Use everything from LinuxResourceCalculatorPlugin
DECL|method|LinuxMemoryCalculatorPlugin ()
specifier|public
name|LinuxMemoryCalculatorPlugin
parameter_list|()
block|{
name|resourceCalculatorPlugin
operator|=
operator|new
name|LinuxResourceCalculatorPlugin
argument_list|()
expr_stmt|;
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
return|return
name|resourceCalculatorPlugin
operator|.
name|getPhysicalMemorySize
argument_list|()
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
return|return
name|resourceCalculatorPlugin
operator|.
name|getVirtualMemorySize
argument_list|()
return|;
block|}
block|}
end_class

end_unit

