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
comment|/**  * Plugin to calculate resource information on the system.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|SysInfo
specifier|public
specifier|abstract
class|class
name|SysInfo
block|{
comment|/**    * Return default OS instance.    * @throws UnsupportedOperationException If cannot determine OS.    * @return Default instance for the detected OS.    */
DECL|method|newInstance ()
specifier|public
specifier|static
name|SysInfo
name|newInstance
parameter_list|()
block|{
if|if
condition|(
name|Shell
operator|.
name|LINUX
condition|)
block|{
return|return
operator|new
name|SysInfoLinux
argument_list|()
return|;
block|}
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
return|return
operator|new
name|SysInfoWindows
argument_list|()
return|;
block|}
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Could not determine OS"
argument_list|)
throw|;
block|}
comment|/**    * Obtain the total size of the virtual memory present in the system.    *    * @return virtual memory size in bytes.    */
DECL|method|getVirtualMemorySize ()
specifier|public
specifier|abstract
name|long
name|getVirtualMemorySize
parameter_list|()
function_decl|;
comment|/**    * Obtain the total size of the physical memory present in the system.    *    * @return physical memory size bytes.    */
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
comment|/**    * Obtain the total number of logical processors present on the system.    *    * @return number of logical processors    */
DECL|method|getNumProcessors ()
specifier|public
specifier|abstract
name|int
name|getNumProcessors
parameter_list|()
function_decl|;
comment|/**    * Obtain total number of physical cores present on the system.    *    * @return number of physical cores    */
DECL|method|getNumCores ()
specifier|public
specifier|abstract
name|int
name|getNumCores
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
comment|/**    * Obtain the CPU usage % of the machine. Return -1 if it is unavailable    *    * @return CPU usage as a percentage of available cycles.    */
DECL|method|getCpuUsage ()
specifier|public
specifier|abstract
name|float
name|getCpuUsage
parameter_list|()
function_decl|;
comment|/**    * Obtain the aggregated number of bytes read over the network.    * @return total number of bytes read.    */
DECL|method|getNetworkBytesRead ()
specifier|public
specifier|abstract
name|long
name|getNetworkBytesRead
parameter_list|()
function_decl|;
comment|/**    * Obtain the aggregated number of bytes written to the network.    * @return total number of bytes written.    */
DECL|method|getNetworkBytesWritten ()
specifier|public
specifier|abstract
name|long
name|getNetworkBytesWritten
parameter_list|()
function_decl|;
block|}
end_class

end_unit

