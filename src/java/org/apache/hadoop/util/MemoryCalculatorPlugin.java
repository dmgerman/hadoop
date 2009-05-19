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
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|conf
operator|.
name|Configured
import|;
end_import

begin_comment
comment|/**  * Plugin to calculate virtual and physical memories on the system.  *   */
end_comment

begin_class
DECL|class|MemoryCalculatorPlugin
specifier|public
specifier|abstract
class|class
name|MemoryCalculatorPlugin
extends|extends
name|Configured
block|{
comment|/**    * Obtain the total size of the virtual memory present in the system.    *     * @return virtual memory size in bytes.    */
DECL|method|getVirtualMemorySize ()
specifier|public
specifier|abstract
name|long
name|getVirtualMemorySize
parameter_list|()
function_decl|;
comment|/**    * Obtain the total size of the physical memory present in the system.    *     * @return physical memory size bytes.    */
DECL|method|getPhysicalMemorySize ()
specifier|public
specifier|abstract
name|long
name|getPhysicalMemorySize
parameter_list|()
function_decl|;
comment|/**    * Get the MemoryCalculatorPlugin from the class name and configure it. If    * class name is null, this method will try and return a memory calculator    * plugin available for this system.    *     * @param clazz class-name    * @param conf configure the plugin with this.    * @return MemoryCalculatorPlugin    */
DECL|method|getMemoryCalculatorPlugin ( Class<? extends MemoryCalculatorPlugin> clazz, Configuration conf)
specifier|public
specifier|static
name|MemoryCalculatorPlugin
name|getMemoryCalculatorPlugin
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|MemoryCalculatorPlugin
argument_list|>
name|clazz
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|!=
literal|null
condition|)
block|{
return|return
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|clazz
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|// No class given, try a os specific class
try|try
block|{
name|String
name|osName
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|osName
operator|.
name|startsWith
argument_list|(
literal|"Linux"
argument_list|)
condition|)
block|{
return|return
operator|new
name|LinuxMemoryCalculatorPlugin
argument_list|()
return|;
block|}
block|}
catch|catch
parameter_list|(
name|SecurityException
name|se
parameter_list|)
block|{
comment|// Failed to get Operating System name.
return|return
literal|null
return|;
block|}
comment|// Not supported on this system.
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

