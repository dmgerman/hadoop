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
name|Random
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
name|mapreduce
operator|.
name|util
operator|.
name|ResourceCalculatorPlugin
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

begin_comment
comment|/**  *<p>A {@link ResourceUsageEmulatorPlugin} that emulates the cumulative CPU   * usage by performing certain CPU intensive operations. Performing such CPU   * intensive operations essentially uses up some CPU. Every   * {@link ResourceUsageEmulatorPlugin} is configured with a feedback module i.e   * a {@link ResourceCalculatorPlugin}, to monitor the resource usage.</p>  *   *<p>{@link CumulativeCpuUsageEmulatorPlugin} emulates the CPU usage in steps.   * The frequency of emulation can be configured via   * {@link #CPU_EMULATION_FREQUENCY}.  * CPU usage values are matched via emulation only on the interval boundaries.  *</p>  *    * {@link CumulativeCpuUsageEmulatorPlugin} is a wrapper program for managing   * the CPU usage emulation feature. It internally uses an emulation algorithm   * (called as core and described using {@link CpuUsageEmulatorCore}) for   * performing the actual emulation. Multiple calls to this core engine should   * use up some amount of CPU.<br>  *   *<p>{@link CumulativeCpuUsageEmulatorPlugin} provides a calibration feature   * via {@link #initialize(Configuration, ResourceUsageMetrics,   *                        ResourceCalculatorPlugin, Progressive)} to calibrate   *  the plugin and its core for the underlying hardware. As a result of   *  calibration, every call to the emulation engine's core should roughly use up  *  1% of the total usage value to be emulated. This makes sure that the   *  underlying hardware is profiled before use and that the plugin doesn't   *  accidently overuse the CPU. With 1% as the unit emulation target value for   *  the core engine, there will be roughly 100 calls to the engine resulting in   *  roughly 100 calls to the feedback (resource usage monitor) module.   *  Excessive usage of the feedback module is discouraged as   *  it might result into excess CPU usage resulting into no real CPU emulation.  *</p>  */
end_comment

begin_class
DECL|class|CumulativeCpuUsageEmulatorPlugin
specifier|public
class|class
name|CumulativeCpuUsageEmulatorPlugin
implements|implements
name|ResourceUsageEmulatorPlugin
block|{
DECL|field|emulatorCore
specifier|protected
name|CpuUsageEmulatorCore
name|emulatorCore
decl_stmt|;
DECL|field|monitor
specifier|private
name|ResourceCalculatorPlugin
name|monitor
decl_stmt|;
DECL|field|progress
specifier|private
name|Progressive
name|progress
decl_stmt|;
DECL|field|enabled
specifier|private
name|boolean
name|enabled
init|=
literal|true
decl_stmt|;
DECL|field|emulationInterval
specifier|private
name|float
name|emulationInterval
decl_stmt|;
comment|// emulation interval
DECL|field|targetCpuUsage
specifier|private
name|long
name|targetCpuUsage
init|=
literal|0
decl_stmt|;
DECL|field|lastSeenProgress
specifier|private
name|float
name|lastSeenProgress
init|=
literal|0
decl_stmt|;
DECL|field|lastSeenCpuUsageCpuUsage
specifier|private
name|long
name|lastSeenCpuUsageCpuUsage
init|=
literal|0
decl_stmt|;
comment|// Configuration parameters
DECL|field|CPU_EMULATION_FREQUENCY
specifier|public
specifier|static
specifier|final
name|String
name|CPU_EMULATION_FREQUENCY
init|=
literal|"gridmix.emulators.resource-usage.cpu.frequency"
decl_stmt|;
DECL|field|DEFAULT_EMULATION_FREQUENCY
specifier|private
specifier|static
specifier|final
name|float
name|DEFAULT_EMULATION_FREQUENCY
init|=
literal|0.1F
decl_stmt|;
comment|// 10 times
comment|/**    * This is the core CPU usage emulation algorithm. This is the core engine    * which actually performs some CPU intensive operations to consume some    * amount of CPU. Multiple calls of {@link #compute()} should help the     * plugin emulate the desired level of CPU usage. This core engine can be    * calibrated using the {@link #calibrate(ResourceCalculatorPlugin, long)}    * API to suit the underlying hardware better. It also can be used to optimize    * the emulation cycle.    */
DECL|interface|CpuUsageEmulatorCore
specifier|public
interface|interface
name|CpuUsageEmulatorCore
block|{
comment|/**      * Performs some computation to use up some CPU.      */
DECL|method|compute ()
specifier|public
name|void
name|compute
parameter_list|()
function_decl|;
comment|/**      * Allows the core to calibrate itself.      */
DECL|method|calibrate (ResourceCalculatorPlugin monitor, long totalCpuUsage)
specifier|public
name|void
name|calibrate
parameter_list|(
name|ResourceCalculatorPlugin
name|monitor
parameter_list|,
name|long
name|totalCpuUsage
parameter_list|)
function_decl|;
block|}
comment|/**    * This is the core engine to emulate the CPU usage. The only responsibility     * of this class is to perform certain math intensive operations to make sure     * that some desired value of CPU is used.    */
DECL|class|DefaultCpuUsageEmulator
specifier|public
specifier|static
class|class
name|DefaultCpuUsageEmulator
implements|implements
name|CpuUsageEmulatorCore
block|{
comment|// number of times to loop for performing the basic unit computation
DECL|field|numIterations
specifier|private
name|int
name|numIterations
decl_stmt|;
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
comment|/**      * This is to fool the JVM and make it think that we need the value       * stored in the unit computation i.e {@link #compute()}. This will prevent      * the JVM from optimizing the code.      */
DECL|field|returnValue
specifier|protected
name|double
name|returnValue
decl_stmt|;
comment|/**      * Initialized the {@link DefaultCpuUsageEmulator} with default values.       * Note that the {@link DefaultCpuUsageEmulator} should be calibrated       * (see {@link #calibrate(ResourceCalculatorPlugin, long)}) when initialized      * using this constructor.      */
DECL|method|DefaultCpuUsageEmulator ()
specifier|public
name|DefaultCpuUsageEmulator
parameter_list|()
block|{
name|this
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|DefaultCpuUsageEmulator (int numIterations)
name|DefaultCpuUsageEmulator
parameter_list|(
name|int
name|numIterations
parameter_list|)
block|{
name|this
operator|.
name|numIterations
operator|=
name|numIterations
expr_stmt|;
name|random
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
block|}
comment|/**      * This will consume some desired level of CPU. This API will try to use up      * 'X' percent of the target cumulative CPU usage. Currently X is set to       * 10%.      */
DECL|method|compute ()
specifier|public
name|void
name|compute
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numIterations
condition|;
operator|++
name|i
control|)
block|{
name|performUnitComputation
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Perform unit computation. The complete CPU emulation will be based on
comment|// multiple invocations to this unit computation module.
DECL|method|performUnitComputation ()
specifier|protected
name|void
name|performUnitComputation
parameter_list|()
block|{
comment|//TODO can this be configurable too. Users/emulators should be able to
comment|// pick and choose what MATH operations to run.
comment|// Example :
comment|//           BASIC : ADD, SUB, MUL, DIV
comment|//           ADV   : SQRT, SIN, COSIN..
comment|//           COMPO : (BASIC/ADV)*
comment|// Also define input generator. For now we can use the random number
comment|// generator. Later this can be changed to accept multiple sources.
name|int
name|randomData
init|=
name|random
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|int
name|randomDataCube
init|=
name|randomData
operator|*
name|randomData
operator|*
name|randomData
decl_stmt|;
name|double
name|randomDataCubeRoot
init|=
name|Math
operator|.
name|cbrt
argument_list|(
name|randomData
argument_list|)
decl_stmt|;
name|returnValue
operator|=
name|Math
operator|.
name|log
argument_list|(
name|Math
operator|.
name|tan
argument_list|(
name|randomDataCubeRoot
operator|*
name|Math
operator|.
name|exp
argument_list|(
name|randomDataCube
argument_list|)
argument_list|)
operator|*
name|Math
operator|.
name|sqrt
argument_list|(
name|randomData
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * This will calibrate the algorithm such that a single invocation of      * {@link #compute()} emulates roughly 1% of the total desired resource       * usage value.      */
DECL|method|calibrate (ResourceCalculatorPlugin monitor, long totalCpuUsage)
specifier|public
name|void
name|calibrate
parameter_list|(
name|ResourceCalculatorPlugin
name|monitor
parameter_list|,
name|long
name|totalCpuUsage
parameter_list|)
block|{
name|long
name|initTime
init|=
name|monitor
operator|.
name|getProcResourceValues
argument_list|()
operator|.
name|getCumulativeCpuTime
argument_list|()
decl_stmt|;
name|long
name|defaultLoopSize
init|=
literal|0
decl_stmt|;
name|long
name|finalTime
init|=
name|initTime
decl_stmt|;
comment|//TODO Make this configurable
while|while
condition|(
name|finalTime
operator|-
name|initTime
operator|<
literal|100
condition|)
block|{
comment|// 100 ms
operator|++
name|defaultLoopSize
expr_stmt|;
name|performUnitComputation
argument_list|()
expr_stmt|;
comment|//perform unit computation
name|finalTime
operator|=
name|monitor
operator|.
name|getProcResourceValues
argument_list|()
operator|.
name|getCumulativeCpuTime
argument_list|()
expr_stmt|;
block|}
name|long
name|referenceRuntime
init|=
name|finalTime
operator|-
name|initTime
decl_stmt|;
comment|// time for one loop = (final-time - init-time) / total-loops
name|float
name|timePerLoop
init|=
operator|(
operator|(
name|float
operator|)
name|referenceRuntime
operator|)
operator|/
name|defaultLoopSize
decl_stmt|;
comment|// compute the 1% of the total CPU usage desired
comment|//TODO Make this configurable
name|long
name|onePercent
init|=
name|totalCpuUsage
operator|/
literal|100
decl_stmt|;
comment|// num-iterations for 1% = (total-desired-usage / 100) / time-for-one-loop
name|numIterations
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
call|(
name|int
call|)
argument_list|(
operator|(
name|float
operator|)
name|onePercent
operator|/
name|timePerLoop
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Calibration done. Basic computation runtime : "
operator|+
name|timePerLoop
operator|+
literal|" milliseconds. Optimal number of iterations (1%): "
operator|+
name|numIterations
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|CumulativeCpuUsageEmulatorPlugin ()
specifier|public
name|CumulativeCpuUsageEmulatorPlugin
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|DefaultCpuUsageEmulator
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * For testing.    */
DECL|method|CumulativeCpuUsageEmulatorPlugin (CpuUsageEmulatorCore core)
specifier|public
name|CumulativeCpuUsageEmulatorPlugin
parameter_list|(
name|CpuUsageEmulatorCore
name|core
parameter_list|)
block|{
name|emulatorCore
operator|=
name|core
expr_stmt|;
block|}
comment|// Note that this weighing function uses only the current progress. In future,
comment|// this might depend on progress, emulation-interval and expected target.
DECL|method|getWeightForProgressInterval (float progress)
specifier|private
name|float
name|getWeightForProgressInterval
parameter_list|(
name|float
name|progress
parameter_list|)
block|{
comment|// we want some kind of exponential growth function that gives less weight
comment|// on lower progress boundaries but high (exact emulation) near progress
comment|// value of 1.
comment|// so here is how the current growth function looks like
comment|//    progress    weight
comment|//      0.1       0.0001
comment|//      0.2       0.0016
comment|//      0.3       0.0081
comment|//      0.4       0.0256
comment|//      0.5       0.0625
comment|//      0.6       0.1296
comment|//      0.7       0.2401
comment|//      0.8       0.4096
comment|//      0.9       0.6561
comment|//      1.0       1.000
return|return
name|progress
operator|*
name|progress
operator|*
name|progress
operator|*
name|progress
return|;
block|}
annotation|@
name|Override
comment|//TODO Multi-threading for speedup?
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
name|lastSeenProgress
operator|<
name|currentProgress
operator|&&
operator|(
operator|(
name|currentProgress
operator|-
name|lastSeenProgress
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
comment|// Estimate the final cpu usage
comment|//
comment|//   Consider the following
comment|//     Cl/Cc/Cp : Last/Current/Projected Cpu usage
comment|//     Pl/Pc/Pp : Last/Current/Projected progress
comment|//   Then
comment|//     (Cp-Cc)/(Pp-Pc) = (Cc-Cl)/(Pc-Pl)
comment|//   Solving this for Cp, we get
comment|//     Cp = Cc + (1-Pc)*(Cc-Cl)/Pc-Pl)
comment|//   Note that (Cc-Cl)/(Pc-Pl) is termed as 'rate' in the following
comment|//   section
name|long
name|currentCpuUsage
init|=
name|monitor
operator|.
name|getProcResourceValues
argument_list|()
operator|.
name|getCumulativeCpuTime
argument_list|()
decl_stmt|;
comment|// estimate the cpu usage rate
name|float
name|rate
init|=
operator|(
name|currentCpuUsage
operator|-
name|lastSeenCpuUsageCpuUsage
operator|)
operator|/
operator|(
name|currentProgress
operator|-
name|lastSeenProgress
operator|)
decl_stmt|;
name|long
name|projectedUsage
init|=
name|currentCpuUsage
operator|+
call|(
name|long
call|)
argument_list|(
operator|(
literal|1
operator|-
name|currentProgress
operator|)
operator|*
name|rate
argument_list|)
decl_stmt|;
if|if
condition|(
name|projectedUsage
operator|<
name|targetCpuUsage
condition|)
block|{
comment|// determine the correction factor between the current usage and the
comment|// expected usage and add some weight to the target
name|long
name|currentWeighedTarget
init|=
call|(
name|long
call|)
argument_list|(
name|targetCpuUsage
operator|*
name|getWeightForProgressInterval
argument_list|(
name|currentProgress
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
name|monitor
operator|.
name|getProcResourceValues
argument_list|()
operator|.
name|getCumulativeCpuTime
argument_list|()
operator|<
name|currentWeighedTarget
condition|)
block|{
name|emulatorCore
operator|.
name|compute
argument_list|()
expr_stmt|;
comment|// sleep for 100ms
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|String
name|message
init|=
literal|"CumulativeCpuUsageEmulatorPlugin got interrupted. Exiting."
decl_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
block|}
comment|// set the last seen progress
name|lastSeenProgress
operator|=
name|progress
operator|.
name|getProgress
argument_list|()
expr_stmt|;
comment|// set the last seen usage
name|lastSeenCpuUsageCpuUsage
operator|=
name|monitor
operator|.
name|getProcResourceValues
argument_list|()
operator|.
name|getCumulativeCpuTime
argument_list|()
expr_stmt|;
block|}
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
comment|// get the target CPU usage
name|targetCpuUsage
operator|=
name|metrics
operator|.
name|getCumulativeCpuUsage
argument_list|()
expr_stmt|;
if|if
condition|(
name|targetCpuUsage
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
name|enabled
operator|=
literal|true
expr_stmt|;
block|}
name|this
operator|.
name|monitor
operator|=
name|monitor
expr_stmt|;
name|this
operator|.
name|progress
operator|=
name|progress
expr_stmt|;
name|emulationInterval
operator|=
name|conf
operator|.
name|getFloat
argument_list|(
name|CPU_EMULATION_FREQUENCY
argument_list|,
name|DEFAULT_EMULATION_FREQUENCY
argument_list|)
expr_stmt|;
comment|// calibrate the core cpu-usage utility
name|emulatorCore
operator|.
name|calibrate
argument_list|(
name|monitor
argument_list|,
name|targetCpuUsage
argument_list|)
expr_stmt|;
comment|// initialize the states
name|lastSeenProgress
operator|=
literal|0
expr_stmt|;
name|lastSeenCpuUsageCpuUsage
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class

end_unit

