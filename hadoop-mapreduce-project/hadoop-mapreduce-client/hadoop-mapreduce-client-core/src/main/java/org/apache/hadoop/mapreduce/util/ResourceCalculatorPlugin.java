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
name|ReflectionUtils
import|;
end_import

begin_comment
comment|/**  * Plugin to calculate resource information on the system.  *   */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|ResourceCalculatorPlugin
specifier|public
specifier|abstract
class|class
name|ResourceCalculatorPlugin
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
comment|/**    * Obtain the total size of the available virtual memory present    * in the system.    *    * @return available virtual memory size in bytes.    */
DECL|method|getAvailableVirtualMemorySize ()
specifier|public
specifier|abstract
name|long
name|getAvailableVirtualMemorySize
parameter_list|()
function_decl|;
comment|/**    * Obtain the total size of the available physical memory present    * in the system.    *    * @return available physical memory size bytes.    */
DECL|method|getAvailablePhysicalMemorySize ()
specifier|public
specifier|abstract
name|long
name|getAvailablePhysicalMemorySize
parameter_list|()
function_decl|;
comment|/**    * Obtain the total number of processors present on the system.    *    * @return number of processors    */
DECL|method|getNumProcessors ()
specifier|public
specifier|abstract
name|int
name|getNumProcessors
parameter_list|()
function_decl|;
comment|/**    * Obtain the CPU frequency of on the system.    *    * @return CPU frequency in kHz    */
DECL|method|getCpuFrequency ()
specifier|public
specifier|abstract
name|long
name|getCpuFrequency
parameter_list|()
function_decl|;
comment|/**    * Obtain the cumulative CPU time since the system is on.    *    * @return cumulative CPU time in milliseconds    */
DECL|method|getCumulativeCpuTime ()
specifier|public
specifier|abstract
name|long
name|getCumulativeCpuTime
parameter_list|()
function_decl|;
comment|/**    * Obtain the CPU usage % of the machine. Return -1 if it is unavailable    *    * @return CPU usage in %    */
DECL|method|getCpuUsage ()
specifier|public
specifier|abstract
name|float
name|getCpuUsage
parameter_list|()
function_decl|;
comment|/**    * Obtain resource status used by current process tree.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|getProcResourceValues ()
specifier|public
specifier|abstract
name|ProcResourceValues
name|getProcResourceValues
parameter_list|()
function_decl|;
DECL|class|ProcResourceValues
specifier|public
specifier|static
class|class
name|ProcResourceValues
block|{
DECL|field|cumulativeCpuTime
specifier|private
specifier|final
name|long
name|cumulativeCpuTime
decl_stmt|;
DECL|field|physicalMemorySize
specifier|private
specifier|final
name|long
name|physicalMemorySize
decl_stmt|;
DECL|field|virtualMemorySize
specifier|private
specifier|final
name|long
name|virtualMemorySize
decl_stmt|;
DECL|method|ProcResourceValues (long cumulativeCpuTime, long physicalMemorySize, long virtualMemorySize)
specifier|public
name|ProcResourceValues
parameter_list|(
name|long
name|cumulativeCpuTime
parameter_list|,
name|long
name|physicalMemorySize
parameter_list|,
name|long
name|virtualMemorySize
parameter_list|)
block|{
name|this
operator|.
name|cumulativeCpuTime
operator|=
name|cumulativeCpuTime
expr_stmt|;
name|this
operator|.
name|physicalMemorySize
operator|=
name|physicalMemorySize
expr_stmt|;
name|this
operator|.
name|virtualMemorySize
operator|=
name|virtualMemorySize
expr_stmt|;
block|}
comment|/**      * Obtain the physical memory size used by current process tree.      * @return physical memory size in bytes.      */
DECL|method|getPhysicalMemorySize ()
specifier|public
name|long
name|getPhysicalMemorySize
parameter_list|()
block|{
return|return
name|physicalMemorySize
return|;
block|}
comment|/**      * Obtain the virtual memory size used by a current process tree.      * @return virtual memory size in bytes.      */
DECL|method|getVirtualMemorySize ()
specifier|public
name|long
name|getVirtualMemorySize
parameter_list|()
block|{
return|return
name|virtualMemorySize
return|;
block|}
comment|/**      * Obtain the cumulative CPU time used by a current process tree.      * @return cumulative CPU time in milliseconds      */
DECL|method|getCumulativeCpuTime ()
specifier|public
name|long
name|getCumulativeCpuTime
parameter_list|()
block|{
return|return
name|cumulativeCpuTime
return|;
block|}
block|}
comment|/**    * Get the ResourceCalculatorPlugin from the class name and configure it. If    * class name is null, this method will try and return a memory calculator    * plugin available for this system.    *     * @param clazz class-name    * @param conf configure the plugin with this.    * @return ResourceCalculatorPlugin    */
DECL|method|getResourceCalculatorPlugin ( Class<? extends ResourceCalculatorPlugin> clazz, Configuration conf)
specifier|public
specifier|static
name|ResourceCalculatorPlugin
name|getResourceCalculatorPlugin
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|ResourceCalculatorPlugin
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
name|LinuxResourceCalculatorPlugin
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

