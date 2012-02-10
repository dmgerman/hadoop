begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|Unstable
import|;
end_import

begin_comment
comment|/**  *  * This abstract class that represents a bucketed series of  *  measurements of a quantity being measured in a running task  *  attempt.   *  *<p>The sole constructor is called with a count, which is the  *  number of buckets into which we evenly divide the spectrum of  *  progress from 0.0D to 1.0D .  In the future we may provide for  *  custom split points that don't have to be uniform.  *  *<p>A subclass determines how we fold readings for portions of a  *  bucket and how we interpret the readings by overriding  *  {@code extendInternal(...)} and {@code initializeInterval()}  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|PeriodicStatsAccumulator
specifier|public
specifier|abstract
class|class
name|PeriodicStatsAccumulator
block|{
comment|// The range of progress from 0.0D through 1.0D is divided into
comment|//  count "progress segments".  This object accumulates an
comment|//  estimate of the effective value of a time-varying value during
comment|//  the zero-based i'th progress segment, ranging from i/count
comment|//  through (i+1)/count .
comment|// This is an abstract class.  We have two implementations: one
comment|//  for monotonically increasing time-dependent variables
comment|//  [currently, CPU time in milliseconds and wallclock time in
comment|//  milliseconds] and one for quantities that can vary arbitrarily
comment|//  over time, currently virtual and physical memory used, in
comment|//  kilobytes.
comment|// We carry int's here.  This saves a lot of JVM heap space in the
comment|//  job tracker per running task attempt [200 bytes per] but it
comment|//  has a small downside.
comment|// No task attempt can run for more than 57 days nor occupy more
comment|//  than two terabytes of virtual memory.
DECL|field|count
specifier|protected
specifier|final
name|int
name|count
decl_stmt|;
DECL|field|values
specifier|protected
specifier|final
name|int
index|[]
name|values
decl_stmt|;
DECL|class|StatsetState
specifier|static
class|class
name|StatsetState
block|{
DECL|field|oldValue
name|int
name|oldValue
init|=
literal|0
decl_stmt|;
DECL|field|oldProgress
name|double
name|oldProgress
init|=
literal|0.0D
decl_stmt|;
DECL|field|currentAccumulation
name|double
name|currentAccumulation
init|=
literal|0.0D
decl_stmt|;
block|}
comment|// We provide this level of indirection to reduce the memory
comment|//  footprint of done task attempts.  When a task's progress
comment|//  reaches 1.0D, we delete this objecte StatsetState.
DECL|field|state
name|StatsetState
name|state
init|=
operator|new
name|StatsetState
argument_list|()
decl_stmt|;
DECL|method|PeriodicStatsAccumulator (int count)
name|PeriodicStatsAccumulator
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|values
operator|=
operator|new
name|int
index|[
name|count
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
name|count
condition|;
operator|++
name|i
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
DECL|method|getValues ()
specifier|protected
name|int
index|[]
name|getValues
parameter_list|()
block|{
return|return
name|values
return|;
block|}
comment|// The concrete implementation of this abstract function
comment|//  accumulates more data into the current progress segment.
comment|//  newProgress [from the call] and oldProgress [from the object]
comment|//  must be in [or at the border of] a single progress segment.
comment|/**    *    * adds a new reading to the current bucket.    *    * @param newProgress the endpoint of the interval this new    *                      reading covers    * @param newValue the value of the reading at {@code newProgress}     *    * The class has three instance variables, {@code oldProgress} and    *  {@code oldValue} and {@code currentAccumulation}.     *    * {@code extendInternal} can count on three things:     *    *   1: The first time it's called in a particular instance, both    *      oldXXX's will be zero.    *    *   2: oldXXX for a later call is the value of newXXX of the    *      previous call.  This ensures continuity in accumulation from    *      one call to the next.    *    *   3: {@code currentAccumulation} is owned by     *      {@code initializeInterval} and {@code extendInternal}.    */
DECL|method|extendInternal (double newProgress, int newValue)
specifier|protected
specifier|abstract
name|void
name|extendInternal
parameter_list|(
name|double
name|newProgress
parameter_list|,
name|int
name|newValue
parameter_list|)
function_decl|;
comment|// What has to be done when you open a new interval
comment|/**    * initializes the state variables to be ready for a new interval    */
DECL|method|initializeInterval ()
specifier|protected
name|void
name|initializeInterval
parameter_list|()
block|{
name|state
operator|.
name|currentAccumulation
operator|=
literal|0.0D
expr_stmt|;
block|}
comment|// called for each new reading
comment|/**    * This method calls {@code extendInternal} at least once.  It    *  divides the current progress interval [from the last call's    *  {@code newProgress}  to this call's {@code newProgress} ]    *  into one or more subintervals by splitting at any point which    *  is an interval boundary if there are any such points.  It    *  then calls {@code extendInternal} for each subinterval, or the    *  whole interval if there are no splitting points.    *     *<p>For example, if the value was {@code 300} last time with    *  {@code 0.3}  progress, and count is {@code 5}, and you get a    *  new reading with the variable at {@code 700} and progress at    *  {@code 0.7}, you get three calls to {@code extendInternal}:    *  one extending from progress {@code 0.3} to {@code 0.4} [the    *  next boundary] with a value of {@code 400}, the next one    *  through {@code 0.6} with a  value of {@code 600}, and finally    *  one at {@code 700} with a progress of {@code 0.7} .     *    * @param newProgress the endpoint of the progress range this new    *                      reading covers    * @param newValue the value of the reading at {@code newProgress}     */
DECL|method|extend (double newProgress, int newValue)
specifier|protected
name|void
name|extend
parameter_list|(
name|double
name|newProgress
parameter_list|,
name|int
name|newValue
parameter_list|)
block|{
if|if
condition|(
name|state
operator|==
literal|null
operator|||
name|newProgress
operator|<
name|state
operator|.
name|oldProgress
condition|)
block|{
return|return;
block|}
comment|// This correctness of this code depends on 100% * count = count.
name|int
name|oldIndex
init|=
call|(
name|int
call|)
argument_list|(
name|state
operator|.
name|oldProgress
operator|*
name|count
argument_list|)
decl_stmt|;
name|int
name|newIndex
init|=
call|(
name|int
call|)
argument_list|(
name|newProgress
operator|*
name|count
argument_list|)
decl_stmt|;
name|int
name|originalOldValue
init|=
name|state
operator|.
name|oldValue
decl_stmt|;
name|double
name|fullValueDistance
init|=
operator|(
name|double
operator|)
name|newValue
operator|-
name|state
operator|.
name|oldValue
decl_stmt|;
name|double
name|fullProgressDistance
init|=
name|newProgress
operator|-
name|state
operator|.
name|oldProgress
decl_stmt|;
name|double
name|originalOldProgress
init|=
name|state
operator|.
name|oldProgress
decl_stmt|;
comment|// In this loop we detect each subinterval boundary within the
comment|//  range from the old progress to the new one.  Then we
comment|//  interpolate the value from the old value to the new one to
comment|//  infer what its value might have been at each such boundary.
comment|//  Lastly we make the necessary calls to extendInternal to fold
comment|//  in the data for each trapazoid where no such trapazoid
comment|//  crosses a boundary.
for|for
control|(
name|int
name|closee
init|=
name|oldIndex
init|;
name|closee
operator|<
name|newIndex
condition|;
operator|++
name|closee
control|)
block|{
name|double
name|interpolationProgress
init|=
call|(
name|double
call|)
argument_list|(
name|closee
operator|+
literal|1
argument_list|)
operator|/
name|count
decl_stmt|;
comment|// In floats, x * y / y might not equal y.
name|interpolationProgress
operator|=
name|Math
operator|.
name|min
argument_list|(
name|interpolationProgress
argument_list|,
name|newProgress
argument_list|)
expr_stmt|;
name|double
name|progressLength
init|=
operator|(
name|interpolationProgress
operator|-
name|originalOldProgress
operator|)
decl_stmt|;
name|double
name|interpolationProportion
init|=
name|progressLength
operator|/
name|fullProgressDistance
decl_stmt|;
name|double
name|interpolationValueDistance
init|=
name|fullValueDistance
operator|*
name|interpolationProportion
decl_stmt|;
comment|// estimates the value at the next [interpolated] subsegment boundary
name|int
name|interpolationValue
init|=
operator|(
name|int
operator|)
name|interpolationValueDistance
operator|+
name|originalOldValue
decl_stmt|;
name|extendInternal
argument_list|(
name|interpolationProgress
argument_list|,
name|interpolationValue
argument_list|)
expr_stmt|;
name|advanceState
argument_list|(
name|interpolationProgress
argument_list|,
name|interpolationValue
argument_list|)
expr_stmt|;
name|values
index|[
name|closee
index|]
operator|=
operator|(
name|int
operator|)
name|state
operator|.
name|currentAccumulation
expr_stmt|;
name|initializeInterval
argument_list|()
expr_stmt|;
block|}
name|extendInternal
argument_list|(
name|newProgress
argument_list|,
name|newValue
argument_list|)
expr_stmt|;
name|advanceState
argument_list|(
name|newProgress
argument_list|,
name|newValue
argument_list|)
expr_stmt|;
if|if
condition|(
name|newIndex
operator|==
name|count
condition|)
block|{
name|state
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|advanceState (double newProgress, int newValue)
specifier|protected
name|void
name|advanceState
parameter_list|(
name|double
name|newProgress
parameter_list|,
name|int
name|newValue
parameter_list|)
block|{
name|state
operator|.
name|oldValue
operator|=
name|newValue
expr_stmt|;
name|state
operator|.
name|oldProgress
operator|=
name|newProgress
expr_stmt|;
block|}
DECL|method|getCount ()
name|int
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
DECL|method|get (int index)
name|int
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|values
index|[
name|index
index|]
return|;
block|}
block|}
end_class

end_unit

