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
name|Public
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Evolving
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
import|;
end_import

begin_comment
comment|/**  * Interface class to obtain process resource usage  * NOTE: This class should not be used by external users, but only by external  * developers to extend and include their own process-tree implementation,   * especially for platforms other than Linux and Windows.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
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
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ResourceCalculatorProcessTree
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|UNAVAILABLE
specifier|public
specifier|static
specifier|final
name|int
name|UNAVAILABLE
init|=
operator|-
literal|1
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
comment|/**    * Initialize the object.    * @throws YarnException Throws an exception on error.    */
DECL|method|initialize ()
specifier|public
name|void
name|initialize
parameter_list|()
throws|throws
name|YarnException
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
comment|/**    * Get the virtual memory used by all the processes in the    * process-tree.    *    * @return virtual memory used by the process-tree in bytes,    * {@link #UNAVAILABLE} if it cannot be calculated.    */
DECL|method|getVirtualMemorySize ()
specifier|public
name|long
name|getVirtualMemorySize
parameter_list|()
block|{
return|return
name|getVirtualMemorySize
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**    * Get the resident set size (rss) memory used by all the processes    * in the process-tree.    *    * @return rss memory used by the process-tree in bytes,    * {@link #UNAVAILABLE} if it cannot be calculated.    */
DECL|method|getRssMemorySize ()
specifier|public
name|long
name|getRssMemorySize
parameter_list|()
block|{
return|return
name|getRssMemorySize
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**    * Get the virtual memory used by all the processes in the    * process-tree that are older than the passed in age.    *    * @param olderThanAge processes above this age are included in the    *                     memory addition    * @return virtual memory used by the process-tree in bytes for    * processes older than the specified age, {@link #UNAVAILABLE} if it    * cannot be calculated.    */
DECL|method|getVirtualMemorySize (int olderThanAge)
specifier|public
name|long
name|getVirtualMemorySize
parameter_list|(
name|int
name|olderThanAge
parameter_list|)
block|{
return|return
name|UNAVAILABLE
return|;
block|}
comment|/**    * Get the resident set size (rss) memory used by all the processes    * in the process-tree that are older than the passed in age.    *    * @param olderThanAge processes above this age are included in the    *                     memory addition    * @return rss memory used by the process-tree in bytes for    * processes older than specified age, {@link #UNAVAILABLE} if it cannot be    * calculated.    */
DECL|method|getRssMemorySize (int olderThanAge)
specifier|public
name|long
name|getRssMemorySize
parameter_list|(
name|int
name|olderThanAge
parameter_list|)
block|{
return|return
name|UNAVAILABLE
return|;
block|}
comment|/**    * Get the CPU time in millisecond used by all the processes in the    * process-tree since the process-tree was created    *    * @return cumulative CPU time in millisecond since the process-tree    * created, {@link #UNAVAILABLE} if it cannot be calculated.    */
DECL|method|getCumulativeCpuTime ()
specifier|public
name|long
name|getCumulativeCpuTime
parameter_list|()
block|{
return|return
name|UNAVAILABLE
return|;
block|}
comment|/**    * Get the CPU usage by all the processes in the process-tree based on    * average between samples as a ratio of overall CPU cycles similar to top.    * Thus, if 2 out of 4 cores are used this should return 200.0.    * Note: UNAVAILABLE will be returned in case when CPU usage is not    * available. It is NOT advised to return any other error code.    *    * @return percentage CPU usage since the process-tree was created,    * {@link #UNAVAILABLE} if CPU usage cannot be calculated or not available.    */
DECL|method|getCpuUsagePercent ()
specifier|public
name|float
name|getCpuUsagePercent
parameter_list|()
block|{
return|return
name|UNAVAILABLE
return|;
block|}
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
name|rctree
operator|.
name|initialize
argument_list|()
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

