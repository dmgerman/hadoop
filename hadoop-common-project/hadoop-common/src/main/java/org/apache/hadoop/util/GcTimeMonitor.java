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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|GarbageCollectorMXBean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * This class monitors the percentage of time the JVM is paused in GC within  * the specified observation window, say 1 minute. The user can provide a  * hook which will be called whenever this percentage exceeds the specified  * threshold.  */
end_comment

begin_class
DECL|class|GcTimeMonitor
specifier|public
class|class
name|GcTimeMonitor
extends|extends
name|Thread
block|{
DECL|field|maxGcTimePercentage
specifier|private
specifier|final
name|long
name|maxGcTimePercentage
decl_stmt|;
DECL|field|observationWindowMs
DECL|field|sleepIntervalMs
specifier|private
specifier|final
name|long
name|observationWindowMs
decl_stmt|,
name|sleepIntervalMs
decl_stmt|;
DECL|field|alertHandler
specifier|private
specifier|final
name|GcTimeAlertHandler
name|alertHandler
decl_stmt|;
DECL|field|gcBeans
specifier|private
specifier|final
name|List
argument_list|<
name|GarbageCollectorMXBean
argument_list|>
name|gcBeans
init|=
name|ManagementFactory
operator|.
name|getGarbageCollectorMXBeans
argument_list|()
decl_stmt|;
comment|// Ring buffers containing GC timings and timestamps when timings were taken
DECL|field|gcDataBuf
specifier|private
specifier|final
name|TsAndData
index|[]
name|gcDataBuf
decl_stmt|;
DECL|field|bufSize
DECL|field|startIdx
DECL|field|endIdx
specifier|private
name|int
name|bufSize
decl_stmt|,
name|startIdx
decl_stmt|,
name|endIdx
decl_stmt|;
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|curData
specifier|private
specifier|final
name|GcData
name|curData
init|=
operator|new
name|GcData
argument_list|()
decl_stmt|;
DECL|field|shouldRun
specifier|private
specifier|volatile
name|boolean
name|shouldRun
init|=
literal|true
decl_stmt|;
comment|/**    * Create an instance of GCTimeMonitor. Once it's started, it will stay alive    * and monitor GC time percentage until shutdown() is called. If you don't    * put a limit on the number of GCTimeMonitor instances that you create, and    * alertHandler != null, you should necessarily call shutdown() once the given    * instance is not needed. Otherwise, you may create a memory leak, because    * each running GCTimeMonitor will keep its alertHandler object in memory,    * which in turn may reference and keep in memory many more other objects.    *    * @param observationWindowMs the interval over which the percentage    *   of GC time should be calculated. A practical value would be somewhere    *   between 30 sec and several minutes.    * @param sleepIntervalMs how frequently this thread should wake up to check    *   GC timings. This is also a frequency with which alertHandler will be    *   invoked if GC time percentage exceeds the specified limit. A practical    *   value would likely be 500..1000 ms.    * @param maxGcTimePercentage A GC time percentage limit (0..100) within    *   observationWindowMs. Once this is exceeded, alertHandler will be    *   invoked every sleepIntervalMs milliseconds until GC time percentage    *   falls below this limit.    * @param alertHandler a single method in this interface is invoked when GC    *   time percentage exceeds the specified limit.    */
DECL|method|GcTimeMonitor (long observationWindowMs, long sleepIntervalMs, int maxGcTimePercentage, GcTimeAlertHandler alertHandler)
specifier|public
name|GcTimeMonitor
parameter_list|(
name|long
name|observationWindowMs
parameter_list|,
name|long
name|sleepIntervalMs
parameter_list|,
name|int
name|maxGcTimePercentage
parameter_list|,
name|GcTimeAlertHandler
name|alertHandler
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|observationWindowMs
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|sleepIntervalMs
operator|>
literal|0
operator|&&
name|sleepIntervalMs
operator|<
name|observationWindowMs
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|maxGcTimePercentage
operator|>=
literal|0
operator|&&
name|maxGcTimePercentage
operator|<=
literal|100
argument_list|)
expr_stmt|;
name|this
operator|.
name|observationWindowMs
operator|=
name|observationWindowMs
expr_stmt|;
name|this
operator|.
name|sleepIntervalMs
operator|=
name|sleepIntervalMs
expr_stmt|;
name|this
operator|.
name|maxGcTimePercentage
operator|=
name|maxGcTimePercentage
expr_stmt|;
name|this
operator|.
name|alertHandler
operator|=
name|alertHandler
expr_stmt|;
name|bufSize
operator|=
call|(
name|int
call|)
argument_list|(
name|observationWindowMs
operator|/
name|sleepIntervalMs
operator|+
literal|2
argument_list|)
expr_stmt|;
comment|// Prevent the user from accidentally creating an abnormally big buffer,
comment|// which will result in slow calculations and likely inaccuracy.
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|bufSize
operator|<=
literal|128
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|gcDataBuf
operator|=
operator|new
name|TsAndData
index|[
name|bufSize
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bufSize
condition|;
name|i
operator|++
control|)
block|{
name|gcDataBuf
index|[
name|i
index|]
operator|=
operator|new
name|TsAndData
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|setName
argument_list|(
literal|"GcTimeMonitor obsWindow = "
operator|+
name|observationWindowMs
operator|+
literal|", sleepInterval = "
operator|+
name|sleepIntervalMs
operator|+
literal|", maxGcTimePerc = "
operator|+
name|maxGcTimePercentage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|curData
operator|.
name|timestamp
operator|=
name|startTime
expr_stmt|;
name|gcDataBuf
index|[
name|startIdx
index|]
operator|.
name|setValues
argument_list|(
name|startTime
argument_list|,
literal|0
argument_list|)
expr_stmt|;
while|while
condition|(
name|shouldRun
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepIntervalMs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
return|return;
block|}
name|calculateGCTimePercentageWithinObservedInterval
argument_list|()
expr_stmt|;
if|if
condition|(
name|alertHandler
operator|!=
literal|null
operator|&&
name|curData
operator|.
name|gcTimePercentage
operator|>
name|maxGcTimePercentage
condition|)
block|{
name|alertHandler
operator|.
name|alert
argument_list|(
name|curData
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|shouldRun
operator|=
literal|false
expr_stmt|;
block|}
comment|/** Returns a copy of the most recent data measured by this monitor. */
DECL|method|getLatestGcData ()
specifier|public
name|GcData
name|getLatestGcData
parameter_list|()
block|{
return|return
name|curData
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|method|calculateGCTimePercentageWithinObservedInterval ()
specifier|private
name|void
name|calculateGCTimePercentageWithinObservedInterval
parameter_list|()
block|{
name|long
name|prevTotalGcTime
init|=
name|curData
operator|.
name|totalGcTime
decl_stmt|;
name|long
name|totalGcTime
init|=
literal|0
decl_stmt|;
name|long
name|totalGcCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|GarbageCollectorMXBean
name|gcBean
range|:
name|gcBeans
control|)
block|{
name|totalGcTime
operator|+=
name|gcBean
operator|.
name|getCollectionTime
argument_list|()
expr_stmt|;
name|totalGcCount
operator|+=
name|gcBean
operator|.
name|getCollectionCount
argument_list|()
expr_stmt|;
block|}
name|long
name|gcTimeWithinSleepInterval
init|=
name|totalGcTime
operator|-
name|prevTotalGcTime
decl_stmt|;
name|long
name|ts
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|gcMonitorRunTime
init|=
name|ts
operator|-
name|startTime
decl_stmt|;
name|endIdx
operator|=
operator|(
name|endIdx
operator|+
literal|1
operator|)
operator|%
name|bufSize
expr_stmt|;
name|gcDataBuf
index|[
name|endIdx
index|]
operator|.
name|setValues
argument_list|(
name|ts
argument_list|,
name|gcTimeWithinSleepInterval
argument_list|)
expr_stmt|;
comment|// Move startIdx forward until we reach the first buffer entry with
comment|// timestamp within the observation window.
name|long
name|startObsWindowTs
init|=
name|ts
operator|-
name|observationWindowMs
decl_stmt|;
while|while
condition|(
name|gcDataBuf
index|[
name|startIdx
index|]
operator|.
name|ts
operator|<
name|startObsWindowTs
operator|&&
name|startIdx
operator|!=
name|endIdx
condition|)
block|{
name|startIdx
operator|=
operator|(
name|startIdx
operator|+
literal|1
operator|)
operator|%
name|bufSize
expr_stmt|;
block|}
comment|// Calculate total GC time within observationWindowMs.
comment|// We should be careful about GC time that passed before the first timestamp
comment|// in our observation window.
name|long
name|gcTimeWithinObservationWindow
init|=
name|Math
operator|.
name|min
argument_list|(
name|gcDataBuf
index|[
name|startIdx
index|]
operator|.
name|gcPause
argument_list|,
name|gcDataBuf
index|[
name|startIdx
index|]
operator|.
name|ts
operator|-
name|startObsWindowTs
argument_list|)
decl_stmt|;
if|if
condition|(
name|startIdx
operator|!=
name|endIdx
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
operator|(
name|startIdx
operator|+
literal|1
operator|)
operator|%
name|bufSize
init|;
name|i
operator|!=
name|endIdx
condition|;
name|i
operator|=
operator|(
name|i
operator|+
literal|1
operator|)
operator|%
name|bufSize
control|)
block|{
name|gcTimeWithinObservationWindow
operator|+=
name|gcDataBuf
index|[
name|i
index|]
operator|.
name|gcPause
expr_stmt|;
block|}
block|}
name|curData
operator|.
name|update
argument_list|(
name|ts
argument_list|,
name|gcMonitorRunTime
argument_list|,
name|totalGcTime
argument_list|,
name|totalGcCount
argument_list|,
call|(
name|int
call|)
argument_list|(
name|gcTimeWithinObservationWindow
operator|*
literal|100
operator|/
name|Math
operator|.
name|min
argument_list|(
name|observationWindowMs
argument_list|,
name|gcMonitorRunTime
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * The user can provide an instance of a class implementing this interface    * when initializing a GcTimeMonitor to receive alerts when GC time    * percentage exceeds the specified threshold.    */
DECL|interface|GcTimeAlertHandler
specifier|public
interface|interface
name|GcTimeAlertHandler
block|{
DECL|method|alert (GcData gcData)
name|void
name|alert
parameter_list|(
name|GcData
name|gcData
parameter_list|)
function_decl|;
block|}
comment|/** Encapsulates data about GC pauses measured at the specific timestamp. */
DECL|class|GcData
specifier|public
specifier|static
class|class
name|GcData
implements|implements
name|Cloneable
block|{
DECL|field|timestamp
specifier|private
name|long
name|timestamp
decl_stmt|;
DECL|field|gcMonitorRunTime
DECL|field|totalGcTime
DECL|field|totalGcCount
specifier|private
name|long
name|gcMonitorRunTime
decl_stmt|,
name|totalGcTime
decl_stmt|,
name|totalGcCount
decl_stmt|;
DECL|field|gcTimePercentage
specifier|private
name|int
name|gcTimePercentage
decl_stmt|;
comment|/** Returns the absolute timestamp when this measurement was taken. */
DECL|method|getTimestamp ()
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
comment|/** Returns the time since the start of the associated GcTimeMonitor. */
DECL|method|getGcMonitorRunTime ()
specifier|public
name|long
name|getGcMonitorRunTime
parameter_list|()
block|{
return|return
name|gcMonitorRunTime
return|;
block|}
comment|/** Returns accumulated GC time since this JVM started. */
DECL|method|getAccumulatedGcTime ()
specifier|public
name|long
name|getAccumulatedGcTime
parameter_list|()
block|{
return|return
name|totalGcTime
return|;
block|}
comment|/** Returns the accumulated number of GC pauses since this JVM started. */
DECL|method|getAccumulatedGcCount ()
specifier|public
name|long
name|getAccumulatedGcCount
parameter_list|()
block|{
return|return
name|totalGcCount
return|;
block|}
comment|/**      * Returns the percentage (0..100) of time that the JVM spent in GC pauses      * within the observation window of the associated GcTimeMonitor.      */
DECL|method|getGcTimePercentage ()
specifier|public
name|int
name|getGcTimePercentage
parameter_list|()
block|{
return|return
name|gcTimePercentage
return|;
block|}
DECL|method|update (long inTimestamp, long inGcMonitorRunTime, long inTotalGcTime, long inTotalGcCount, int inGcTimePercentage)
specifier|private
specifier|synchronized
name|void
name|update
parameter_list|(
name|long
name|inTimestamp
parameter_list|,
name|long
name|inGcMonitorRunTime
parameter_list|,
name|long
name|inTotalGcTime
parameter_list|,
name|long
name|inTotalGcCount
parameter_list|,
name|int
name|inGcTimePercentage
parameter_list|)
block|{
name|this
operator|.
name|timestamp
operator|=
name|inTimestamp
expr_stmt|;
name|this
operator|.
name|gcMonitorRunTime
operator|=
name|inGcMonitorRunTime
expr_stmt|;
name|this
operator|.
name|totalGcTime
operator|=
name|inTotalGcTime
expr_stmt|;
name|this
operator|.
name|totalGcCount
operator|=
name|inTotalGcCount
expr_stmt|;
name|this
operator|.
name|gcTimePercentage
operator|=
name|inGcTimePercentage
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone ()
specifier|public
specifier|synchronized
name|GcData
name|clone
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|GcData
operator|)
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
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
block|}
DECL|class|TsAndData
specifier|private
specifier|static
class|class
name|TsAndData
block|{
DECL|field|ts
specifier|private
name|long
name|ts
decl_stmt|;
comment|// Timestamp when this measurement was taken
DECL|field|gcPause
specifier|private
name|long
name|gcPause
decl_stmt|;
comment|// Total GC pause time within the interval between ts
comment|// and the timestamp of the previous measurement.
DECL|method|setValues (long inTs, long inGcPause)
name|void
name|setValues
parameter_list|(
name|long
name|inTs
parameter_list|,
name|long
name|inGcPause
parameter_list|)
block|{
name|this
operator|.
name|ts
operator|=
name|inTs
expr_stmt|;
name|this
operator|.
name|gcPause
operator|=
name|inGcPause
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

