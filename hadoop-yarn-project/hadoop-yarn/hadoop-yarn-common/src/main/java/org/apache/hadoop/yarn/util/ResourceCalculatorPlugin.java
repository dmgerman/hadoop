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
name|SysInfo
import|;
end_import

begin_comment
comment|/**  * Plugin to calculate resource information on the system.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"YARN"
block|,
literal|"MAPREDUCE"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|ResourceCalculatorPlugin
specifier|public
class|class
name|ResourceCalculatorPlugin
extends|extends
name|Configured
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
name|ResourceCalculatorPlugin
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|sys
specifier|private
specifier|final
name|SysInfo
name|sys
decl_stmt|;
DECL|method|ResourceCalculatorPlugin ()
specifier|protected
name|ResourceCalculatorPlugin
parameter_list|()
block|{
name|this
argument_list|(
name|SysInfo
operator|.
name|newInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|ResourceCalculatorPlugin (SysInfo sys)
specifier|public
name|ResourceCalculatorPlugin
parameter_list|(
name|SysInfo
name|sys
parameter_list|)
block|{
name|this
operator|.
name|sys
operator|=
name|sys
expr_stmt|;
block|}
comment|/**    * Obtain the total size of the virtual memory present in the system.    *    * @return virtual memory size in bytes.    */
DECL|method|getVirtualMemorySize ()
specifier|public
name|long
name|getVirtualMemorySize
parameter_list|()
block|{
return|return
name|sys
operator|.
name|getVirtualMemorySize
argument_list|()
return|;
block|}
comment|/**    * Obtain the total size of the physical memory present in the system.    *    * @return physical memory size bytes.    */
DECL|method|getPhysicalMemorySize ()
specifier|public
name|long
name|getPhysicalMemorySize
parameter_list|()
block|{
return|return
name|sys
operator|.
name|getPhysicalMemorySize
argument_list|()
return|;
block|}
comment|/**    * Obtain the total size of the available virtual memory present    * in the system.    *    * @return available virtual memory size in bytes.    */
DECL|method|getAvailableVirtualMemorySize ()
specifier|public
name|long
name|getAvailableVirtualMemorySize
parameter_list|()
block|{
return|return
name|sys
operator|.
name|getAvailableVirtualMemorySize
argument_list|()
return|;
block|}
comment|/**    * Obtain the total size of the available physical memory present    * in the system.    *    * @return available physical memory size bytes.    */
DECL|method|getAvailablePhysicalMemorySize ()
specifier|public
name|long
name|getAvailablePhysicalMemorySize
parameter_list|()
block|{
return|return
name|sys
operator|.
name|getAvailablePhysicalMemorySize
argument_list|()
return|;
block|}
comment|/**    * Obtain the total number of logical processors present on the system.    *    * @return number of logical processors    */
DECL|method|getNumProcessors ()
specifier|public
name|int
name|getNumProcessors
parameter_list|()
block|{
return|return
name|sys
operator|.
name|getNumProcessors
argument_list|()
return|;
block|}
comment|/**    * Obtain total number of physical cores present on the system.    *    * @return number of physical cores    */
DECL|method|getNumCores ()
specifier|public
name|int
name|getNumCores
parameter_list|()
block|{
return|return
name|sys
operator|.
name|getNumCores
argument_list|()
return|;
block|}
comment|/**    * Obtain the CPU frequency of on the system.    *    * @return CPU frequency in kHz    */
DECL|method|getCpuFrequency ()
specifier|public
name|long
name|getCpuFrequency
parameter_list|()
block|{
return|return
name|sys
operator|.
name|getCpuFrequency
argument_list|()
return|;
block|}
comment|/**    * Obtain the cumulative CPU time since the system is on.    *    * @return cumulative CPU time in milliseconds    */
DECL|method|getCumulativeCpuTime ()
specifier|public
name|long
name|getCumulativeCpuTime
parameter_list|()
block|{
return|return
name|sys
operator|.
name|getCumulativeCpuTime
argument_list|()
return|;
block|}
comment|/**    * Obtain the CPU usage % of the machine. Return -1 if it is unavailable    *    * @return CPU usage in %    */
DECL|method|getCpuUsage ()
specifier|public
name|float
name|getCpuUsage
parameter_list|()
block|{
return|return
name|sys
operator|.
name|getCpuUsage
argument_list|()
return|;
block|}
comment|/**    * Obtain the aggregated number of bytes read over the network.    * @return total number of bytes read.    */
DECL|method|getNetworkBytesRead ()
specifier|public
name|long
name|getNetworkBytesRead
parameter_list|()
block|{
return|return
name|sys
operator|.
name|getNetworkBytesRead
argument_list|()
return|;
block|}
comment|/**    * Obtain the aggregated number of bytes written to the network.    * @return total number of bytes written.    */
DECL|method|getNetworkBytesWritten ()
specifier|public
name|long
name|getNetworkBytesWritten
parameter_list|()
block|{
return|return
name|sys
operator|.
name|getNetworkBytesWritten
argument_list|()
return|;
block|}
comment|/**    * Obtain the aggregated number of bytes read from disks.    *    * @return total number of bytes read.    */
DECL|method|getStorageBytesRead ()
specifier|public
name|long
name|getStorageBytesRead
parameter_list|()
block|{
return|return
name|sys
operator|.
name|getStorageBytesRead
argument_list|()
return|;
block|}
comment|/**    * Obtain the aggregated number of bytes written to disks.    *    * @return total number of bytes written.    */
DECL|method|getStorageBytesWritten ()
specifier|public
name|long
name|getStorageBytesWritten
parameter_list|()
block|{
return|return
name|sys
operator|.
name|getStorageBytesWritten
argument_list|()
return|;
block|}
comment|/**    * Create the ResourceCalculatorPlugin from the class name and configure it. If    * class name is null, this method will try and return a memory calculator    * plugin available for this system.    *    * @param clazz ResourceCalculator plugin class-name    * @param conf configure the plugin with this.    * @return ResourceCalculatorPlugin or null if ResourceCalculatorPlugin is not    * 		 available for current system    */
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
try|try
block|{
return|return
operator|new
name|ResourceCalculatorPlugin
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|t
operator|+
literal|": Failed to instantiate default resource calculator."
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

