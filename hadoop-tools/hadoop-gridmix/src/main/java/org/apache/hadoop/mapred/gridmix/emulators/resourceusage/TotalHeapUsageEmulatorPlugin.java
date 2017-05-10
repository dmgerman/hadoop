begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix.emulators.resourceusage
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
operator|.
name|emulators
operator|.
name|resourceusage
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|mapred
operator|.
name|gridmix
operator|.
name|Progressive
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
name|tools
operator|.
name|rumen
operator|.
name|ResourceUsageMetrics
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
name|util
operator|.
name|ResourceCalculatorPlugin
import|;
end_import

begin_comment
comment|/**  *<p>A {@link ResourceUsageEmulatorPlugin} that emulates the total heap   * usage by loading the JVM heap memory. Adding smaller chunks of data to the   * heap will essentially use up some heap space thus forcing the JVM to expand   * its heap and thus resulting into increase in the heap usage.</p>  *   *<p>{@link TotalHeapUsageEmulatorPlugin} emulates the heap usage in steps.   * The frequency of emulation can be configured via   * {@link #HEAP_EMULATION_PROGRESS_INTERVAL}.  * Heap usage values are matched via emulation only at specific interval   * boundaries.  *</p>  *    * {@link TotalHeapUsageEmulatorPlugin} is a wrapper program for managing   * the heap usage emulation feature. It internally uses an emulation algorithm   * (called as core and described using {@link HeapUsageEmulatorCore}) for   * performing the actual emulation. Multiple calls to this core engine should   * use up some amount of heap.  */
end_comment

begin_class
DECL|class|TotalHeapUsageEmulatorPlugin
specifier|public
class|class
name|TotalHeapUsageEmulatorPlugin
implements|implements
name|ResourceUsageEmulatorPlugin
block|{
comment|// Configuration parameters
comment|//  the core engine to emulate heap usage
DECL|field|emulatorCore
specifier|protected
name|HeapUsageEmulatorCore
name|emulatorCore
decl_stmt|;
comment|//  the progress bar
DECL|field|progress
specifier|private
name|Progressive
name|progress
decl_stmt|;
comment|//  decides if this plugin can emulate heap usage or not
DECL|field|enabled
specifier|private
name|boolean
name|enabled
init|=
literal|true
decl_stmt|;
comment|//  the progress boundaries/interval where emulation should be done
DECL|field|emulationInterval
specifier|private
name|float
name|emulationInterval
decl_stmt|;
comment|//  target heap usage to emulate
DECL|field|targetHeapUsageInMB
specifier|private
name|long
name|targetHeapUsageInMB
init|=
literal|0
decl_stmt|;
comment|/**    * The frequency (based on task progress) with which memory-emulation code is    * run. If the value is set to 0.1 then the emulation will happen at 10% of     * the task's progress. The default value of this parameter is     * {@link #DEFAULT_EMULATION_PROGRESS_INTERVAL}.    */
DECL|field|HEAP_EMULATION_PROGRESS_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|HEAP_EMULATION_PROGRESS_INTERVAL
init|=
literal|"gridmix.emulators.resource-usage.heap.emulation-interval"
decl_stmt|;
comment|// Default value for emulation interval
DECL|field|DEFAULT_EMULATION_PROGRESS_INTERVAL
specifier|private
specifier|static
specifier|final
name|float
name|DEFAULT_EMULATION_PROGRESS_INTERVAL
init|=
literal|0.1F
decl_stmt|;
comment|// 10 %
DECL|field|prevEmulationProgress
specifier|private
name|float
name|prevEmulationProgress
init|=
literal|0F
decl_stmt|;
comment|/**    * The minimum buffer reserved for other non-emulation activities.    */
DECL|field|MIN_HEAP_FREE_RATIO
specifier|public
specifier|static
specifier|final
name|String
name|MIN_HEAP_FREE_RATIO
init|=
literal|"gridmix.emulators.resource-usage.heap.min-free-ratio"
decl_stmt|;
DECL|field|minFreeHeapRatio
specifier|private
name|float
name|minFreeHeapRatio
decl_stmt|;
DECL|field|DEFAULT_MIN_FREE_HEAP_RATIO
specifier|private
specifier|static
specifier|final
name|float
name|DEFAULT_MIN_FREE_HEAP_RATIO
init|=
literal|0.3F
decl_stmt|;
comment|/**    * Determines the unit increase per call to the core engine's load API. This    * is expressed as a percentage of the difference between the expected total     * heap usage and the current usage.     */
DECL|field|HEAP_LOAD_RATIO
specifier|public
specifier|static
specifier|final
name|String
name|HEAP_LOAD_RATIO
init|=
literal|"gridmix.emulators.resource-usage.heap.load-ratio"
decl_stmt|;
DECL|field|heapLoadRatio
specifier|private
name|float
name|heapLoadRatio
decl_stmt|;
DECL|field|DEFAULT_HEAP_LOAD_RATIO
specifier|private
specifier|static
specifier|final
name|float
name|DEFAULT_HEAP_LOAD_RATIO
init|=
literal|0.1F
decl_stmt|;
DECL|field|ONE_MB
specifier|public
specifier|static
specifier|final
name|int
name|ONE_MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|/**    * Defines the core heap usage emulation algorithm. This engine is expected    * to perform certain memory intensive operations to consume some    * amount of heap. {@link #load(long)} should load the current heap and     * increase the heap usage by the specified value. This core engine can be     * initialized using the {@link #initialize(ResourceCalculatorPlugin, long)}     * API to suit the underlying hardware better.    */
DECL|interface|HeapUsageEmulatorCore
specifier|public
interface|interface
name|HeapUsageEmulatorCore
block|{
comment|/**      * Performs some memory intensive operations to use up some heap.      */
DECL|method|load (long sizeInMB)
specifier|public
name|void
name|load
parameter_list|(
name|long
name|sizeInMB
parameter_list|)
function_decl|;
comment|/**      * Initialize the core.      */
DECL|method|initialize (ResourceCalculatorPlugin monitor, long totalHeapUsageInMB)
specifier|public
name|void
name|initialize
parameter_list|(
name|ResourceCalculatorPlugin
name|monitor
parameter_list|,
name|long
name|totalHeapUsageInMB
parameter_list|)
function_decl|;
comment|/**      * Reset the resource usage      */
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
function_decl|;
block|}
comment|/**    * This is the core engine to emulate the heap usage. The only responsibility     * of this class is to perform certain memory intensive operations to make     * sure that some desired value of heap is used.    */
DECL|class|DefaultHeapUsageEmulator
specifier|public
specifier|static
class|class
name|DefaultHeapUsageEmulator
implements|implements
name|HeapUsageEmulatorCore
block|{
comment|// store the unit loads in a list
DECL|field|heapSpace
specifier|private
specifier|static
specifier|final
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|heapSpace
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Increase heap usage by current process by the given amount.      * This is done by creating objects each of size 1MB.      */
DECL|method|load (long sizeInMB)
specifier|public
name|void
name|load
parameter_list|(
name|long
name|sizeInMB
parameter_list|)
block|{
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sizeInMB
condition|;
operator|++
name|i
control|)
block|{
comment|// Create another String object of size 1MB
name|heapSpace
operator|.
name|add
argument_list|(
operator|(
name|Object
operator|)
operator|new
name|byte
index|[
name|ONE_MB
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Gets the total number of 1mb objects stored in the emulator.      *      * @return total number of 1mb objects.      */
annotation|@
name|VisibleForTesting
DECL|method|getHeapSpaceSize ()
specifier|public
name|int
name|getHeapSpaceSize
parameter_list|()
block|{
return|return
name|heapSpace
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * This will initialize the core and check if the core can emulate the       * desired target on the underlying hardware.      */
DECL|method|initialize (ResourceCalculatorPlugin monitor, long totalHeapUsageInMB)
specifier|public
name|void
name|initialize
parameter_list|(
name|ResourceCalculatorPlugin
name|monitor
parameter_list|,
name|long
name|totalHeapUsageInMB
parameter_list|)
block|{
name|long
name|maxPhysicalMemoryInMB
init|=
name|monitor
operator|.
name|getPhysicalMemorySize
argument_list|()
operator|/
name|ONE_MB
decl_stmt|;
if|if
condition|(
name|maxPhysicalMemoryInMB
operator|<
name|totalHeapUsageInMB
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Total heap the can be used is "
operator|+
name|maxPhysicalMemoryInMB
operator|+
literal|" bytes while the emulator is configured to emulate a total of "
operator|+
name|totalHeapUsageInMB
operator|+
literal|" bytes"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Clear references to all the GridMix-allocated special objects so that       * heap usage is reduced.      */
annotation|@
name|Override
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|heapSpace
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|TotalHeapUsageEmulatorPlugin ()
specifier|public
name|TotalHeapUsageEmulatorPlugin
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|DefaultHeapUsageEmulator
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * For testing.    */
DECL|method|TotalHeapUsageEmulatorPlugin (HeapUsageEmulatorCore core)
specifier|public
name|TotalHeapUsageEmulatorPlugin
parameter_list|(
name|HeapUsageEmulatorCore
name|core
parameter_list|)
block|{
name|emulatorCore
operator|=
name|core
expr_stmt|;
block|}
DECL|method|getTotalHeapUsageInMB ()
specifier|protected
name|long
name|getTotalHeapUsageInMB
parameter_list|()
block|{
return|return
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|totalMemory
argument_list|()
operator|/
name|ONE_MB
return|;
block|}
DECL|method|getMaxHeapUsageInMB ()
specifier|protected
name|long
name|getMaxHeapUsageInMB
parameter_list|()
block|{
return|return
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|maxMemory
argument_list|()
operator|/
name|ONE_MB
return|;
block|}
annotation|@
name|Override
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
return|return
name|enabled
condition|?
name|Math
operator|.
name|min
argument_list|(
literal|1f
argument_list|,
operator|(
operator|(
name|float
operator|)
name|getTotalHeapUsageInMB
argument_list|()
operator|)
operator|/
name|targetHeapUsageInMB
argument_list|)
else|:
literal|1.0f
return|;
block|}
annotation|@
name|Override
DECL|method|emulate ()
specifier|public
name|void
name|emulate
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|enabled
condition|)
block|{
name|float
name|currentProgress
init|=
name|progress
operator|.
name|getProgress
argument_list|()
decl_stmt|;
if|if
condition|(
name|prevEmulationProgress
operator|<
name|currentProgress
operator|&&
operator|(
operator|(
name|currentProgress
operator|-
name|prevEmulationProgress
operator|)
operator|>=
name|emulationInterval
operator|||
name|currentProgress
operator|==
literal|1
operator|)
condition|)
block|{
name|long
name|maxHeapSizeInMB
init|=
name|getMaxHeapUsageInMB
argument_list|()
decl_stmt|;
name|long
name|committedHeapSizeInMB
init|=
name|getTotalHeapUsageInMB
argument_list|()
decl_stmt|;
comment|// Increase committed heap usage, if needed
comment|// Using a linear weighing function for computing the expected usage
name|long
name|expectedHeapUsageInMB
init|=
name|Math
operator|.
name|min
argument_list|(
name|maxHeapSizeInMB
argument_list|,
call|(
name|long
call|)
argument_list|(
name|targetHeapUsageInMB
operator|*
name|currentProgress
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|expectedHeapUsageInMB
operator|<
name|maxHeapSizeInMB
operator|&&
name|committedHeapSizeInMB
operator|<
name|expectedHeapUsageInMB
condition|)
block|{
name|long
name|bufferInMB
init|=
call|(
name|long
call|)
argument_list|(
name|minFreeHeapRatio
operator|*
name|expectedHeapUsageInMB
argument_list|)
decl_stmt|;
name|long
name|currentDifferenceInMB
init|=
name|expectedHeapUsageInMB
operator|-
name|committedHeapSizeInMB
decl_stmt|;
name|long
name|currentIncrementLoadSizeInMB
init|=
call|(
name|long
call|)
argument_list|(
name|currentDifferenceInMB
operator|*
name|heapLoadRatio
argument_list|)
decl_stmt|;
comment|// Make sure that at least 1 MB is incremented.
name|currentIncrementLoadSizeInMB
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|currentIncrementLoadSizeInMB
argument_list|)
expr_stmt|;
while|while
condition|(
name|committedHeapSizeInMB
operator|+
name|bufferInMB
operator|<
name|expectedHeapUsageInMB
condition|)
block|{
comment|// add blocks in order of X% of the difference, X = 10% by default
name|emulatorCore
operator|.
name|load
argument_list|(
name|currentIncrementLoadSizeInMB
argument_list|)
expr_stmt|;
name|committedHeapSizeInMB
operator|=
name|getTotalHeapUsageInMB
argument_list|()
expr_stmt|;
block|}
block|}
comment|// store the emulation progress boundary
name|prevEmulationProgress
operator|=
name|currentProgress
expr_stmt|;
block|}
comment|// reset the core so that the garbage is reclaimed
name|emulatorCore
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|initialize (Configuration conf, ResourceUsageMetrics metrics, ResourceCalculatorPlugin monitor, Progressive progress)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ResourceUsageMetrics
name|metrics
parameter_list|,
name|ResourceCalculatorPlugin
name|monitor
parameter_list|,
name|Progressive
name|progress
parameter_list|)
block|{
name|this
operator|.
name|progress
operator|=
name|progress
expr_stmt|;
comment|// get the target heap usage
name|targetHeapUsageInMB
operator|=
name|metrics
operator|.
name|getHeapUsage
argument_list|()
operator|/
name|ONE_MB
expr_stmt|;
if|if
condition|(
name|targetHeapUsageInMB
operator|<=
literal|0
condition|)
block|{
name|enabled
operator|=
literal|false
expr_stmt|;
return|return;
block|}
else|else
block|{
comment|// calibrate the core heap-usage utility
name|emulatorCore
operator|.
name|initialize
argument_list|(
name|monitor
argument_list|,
name|targetHeapUsageInMB
argument_list|)
expr_stmt|;
name|enabled
operator|=
literal|true
expr_stmt|;
block|}
name|emulationInterval
operator|=
name|conf
operator|.
name|getFloat
argument_list|(
name|HEAP_EMULATION_PROGRESS_INTERVAL
argument_list|,
name|DEFAULT_EMULATION_PROGRESS_INTERVAL
argument_list|)
expr_stmt|;
name|minFreeHeapRatio
operator|=
name|conf
operator|.
name|getFloat
argument_list|(
name|MIN_HEAP_FREE_RATIO
argument_list|,
name|DEFAULT_MIN_FREE_HEAP_RATIO
argument_list|)
expr_stmt|;
name|heapLoadRatio
operator|=
name|conf
operator|.
name|getFloat
argument_list|(
name|HEAP_LOAD_RATIO
argument_list|,
name|DEFAULT_HEAP_LOAD_RATIO
argument_list|)
expr_stmt|;
name|prevEmulationProgress
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class

end_unit

