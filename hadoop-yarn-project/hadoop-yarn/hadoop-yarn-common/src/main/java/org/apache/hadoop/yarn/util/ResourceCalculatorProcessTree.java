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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
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
operator|.
name|Private
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

begin_comment
comment|/**  * Interface class to obtain process resource usage  *  */
end_comment

begin_class
annotation|@
name|Private
DECL|class|ResourceCalculatorProcessTree
specifier|public
specifier|abstract
class|class
name|ResourceCalculatorProcessTree
extends|extends
name|Configured
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ResourceCalculatorProcessTree
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Create process-tree instance with specified root process.    *    * Subclass must override this.    * @param root process-tree root-process    */
DECL|method|ResourceCalculatorProcessTree (String root)
specifier|public
name|ResourceCalculatorProcessTree
parameter_list|(
name|String
name|root
parameter_list|)
block|{   }
comment|/**    * Update the process-tree with latest state.    *    * Each call to this function should increment the age of the running    * processes that already exist in the process tree. Age is used other API's    * of the interface.    *    */
DECL|method|updateProcessTree ()
specifier|public
specifier|abstract
name|void
name|updateProcessTree
parameter_list|()
function_decl|;
comment|/**    * Get a dump of the process-tree.    *    * @return a string concatenating the dump of information of all the processes    *         in the process-tree    */
DECL|method|getProcessTreeDump ()
specifier|public
specifier|abstract
name|String
name|getProcessTreeDump
parameter_list|()
function_decl|;
comment|/**    * Get the cumulative virtual memory used by all the processes in the    * process-tree.    *    * @return cumulative virtual memory used by the process-tree in bytes.    */
DECL|method|getCumulativeVmem ()
specifier|public
name|long
name|getCumulativeVmem
parameter_list|()
block|{
return|return
name|getCumulativeVmem
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**    * Get the cumulative resident set size (rss) memory used by all the processes    * in the process-tree.    *    * @return cumulative rss memory used by the process-tree in bytes. return 0    *         if it cannot be calculated    */
DECL|method|getCumulativeRssmem ()
specifier|public
name|long
name|getCumulativeRssmem
parameter_list|()
block|{
return|return
name|getCumulativeRssmem
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**    * Get the cumulative virtual memory used by all the processes in the    * process-tree that are older than the passed in age.    *    * @param olderThanAge processes above this age are included in the    *                      memory addition    * @return cumulative virtual memory used by the process-tree in bytes,    *          for processes older than this age.    */
DECL|method|getCumulativeVmem (int olderThanAge)
specifier|public
specifier|abstract
name|long
name|getCumulativeVmem
parameter_list|(
name|int
name|olderThanAge
parameter_list|)
function_decl|;
comment|/**    * Get the cumulative resident set size (rss) memory used by all the processes    * in the process-tree that are older than the passed in age.    *    * @param olderThanAge processes above this age are included in the    *                      memory addition    * @return cumulative rss memory used by the process-tree in bytes,    *          for processes older than this age. return 0 if it cannot be    *          calculated    */
DECL|method|getCumulativeRssmem (int olderThanAge)
specifier|public
specifier|abstract
name|long
name|getCumulativeRssmem
parameter_list|(
name|int
name|olderThanAge
parameter_list|)
function_decl|;
comment|/**    * Get the CPU time in millisecond used by all the processes in the    * process-tree since the process-tree was created    *    * @return cumulative CPU time in millisecond since the process-tree created    *         return 0 if it cannot be calculated    */
DECL|method|getCumulativeCpuTime ()
specifier|public
specifier|abstract
name|long
name|getCumulativeCpuTime
parameter_list|()
function_decl|;
comment|/**    * Get the CPU usage by all the processes in the process-tree based on    * average between samples as a ratio of overall CPU cycles similar to top.    * Thus, if 2 out of 4 cores are used this should return 200.0.    *    * @return percentage CPU usage since the process-tree was created    *         return {@link CpuTimeTracker#UNAVAILABLE} if it cannot be calculated    */
DECL|method|getCpuUsagePercent ()
specifier|public
specifier|abstract
name|float
name|getCpuUsagePercent
parameter_list|()
function_decl|;
comment|/** Verify that the tree process id is same as its process group id.    * @return true if the process id matches else return false.    */
DECL|method|checkPidPgrpidForMatch ()
specifier|public
specifier|abstract
name|boolean
name|checkPidPgrpidForMatch
parameter_list|()
function_decl|;
comment|/**    * Create the ResourceCalculatorProcessTree rooted to specified process     * from the class name and configure it. If class name is null, this method    * will try and return a process tree plugin available for this system.    *    * @param pid process pid of the root of the process tree    * @param clazz class-name    * @param conf configure the plugin with this.    *    * @return ResourceCalculatorProcessTree or null if ResourceCalculatorPluginTree    *         is not available for this system.    */
DECL|method|getResourceCalculatorProcessTree ( String pid, Class<? extends ResourceCalculatorProcessTree> clazz, Configuration conf)
specifier|public
specifier|static
name|ResourceCalculatorProcessTree
name|getResourceCalculatorProcessTree
parameter_list|(
name|String
name|pid
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|ResourceCalculatorProcessTree
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
try|try
block|{
name|Constructor
argument_list|<
name|?
extends|extends
name|ResourceCalculatorProcessTree
argument_list|>
name|c
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|ResourceCalculatorProcessTree
name|rctree
init|=
name|c
operator|.
name|newInstance
argument_list|(
name|pid
argument_list|)
decl_stmt|;
name|rctree
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|rctree
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|// No class given, try a os specific class
if|if
condition|(
name|ProcfsBasedProcessTree
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
return|return
operator|new
name|ProcfsBasedProcessTree
argument_list|(
name|pid
argument_list|)
return|;
block|}
if|if
condition|(
name|WindowsBasedProcessTree
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
return|return
operator|new
name|WindowsBasedProcessTree
argument_list|(
name|pid
argument_list|)
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

