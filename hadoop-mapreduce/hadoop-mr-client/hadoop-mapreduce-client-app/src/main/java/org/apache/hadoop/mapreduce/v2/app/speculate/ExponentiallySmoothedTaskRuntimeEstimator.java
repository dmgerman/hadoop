begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.speculate
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|speculate
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
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
name|v2
operator|.
name|app
operator|.
name|AMConstants
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
name|v2
operator|.
name|app
operator|.
name|AppContext
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
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptStatusUpdateEvent
operator|.
name|TaskAttemptStatus
import|;
end_import

begin_comment
comment|/**  * This estimator exponentially smooths the rate of progress versus wallclock  * time.  Conceivably we could write an estimator that smooths time per  * unit progress, and get different results.  */
end_comment

begin_class
DECL|class|ExponentiallySmoothedTaskRuntimeEstimator
specifier|public
class|class
name|ExponentiallySmoothedTaskRuntimeEstimator
extends|extends
name|StartEndTimesBase
block|{
DECL|field|estimates
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|TaskAttemptId
argument_list|,
name|AtomicReference
argument_list|<
name|EstimateVector
argument_list|>
argument_list|>
name|estimates
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|TaskAttemptId
argument_list|,
name|AtomicReference
argument_list|<
name|EstimateVector
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|smoothedValue
specifier|private
name|SmoothedValue
name|smoothedValue
decl_stmt|;
DECL|field|lambda
specifier|private
name|long
name|lambda
decl_stmt|;
DECL|enum|SmoothedValue
specifier|public
enum|enum
name|SmoothedValue
block|{
DECL|enumConstant|RATE
DECL|enumConstant|TIME_PER_UNIT_PROGRESS
name|RATE
block|,
name|TIME_PER_UNIT_PROGRESS
block|}
DECL|method|ExponentiallySmoothedTaskRuntimeEstimator (long lambda, SmoothedValue smoothedValue)
name|ExponentiallySmoothedTaskRuntimeEstimator
parameter_list|(
name|long
name|lambda
parameter_list|,
name|SmoothedValue
name|smoothedValue
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|smoothedValue
operator|=
name|smoothedValue
expr_stmt|;
name|this
operator|.
name|lambda
operator|=
name|lambda
expr_stmt|;
block|}
DECL|method|ExponentiallySmoothedTaskRuntimeEstimator ()
specifier|public
name|ExponentiallySmoothedTaskRuntimeEstimator
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|// immutable
DECL|class|EstimateVector
specifier|private
class|class
name|EstimateVector
block|{
DECL|field|value
specifier|final
name|double
name|value
decl_stmt|;
DECL|field|basedOnProgress
specifier|final
name|float
name|basedOnProgress
decl_stmt|;
DECL|field|atTime
specifier|final
name|long
name|atTime
decl_stmt|;
DECL|method|EstimateVector (double value, float basedOnProgress, long atTime)
name|EstimateVector
parameter_list|(
name|double
name|value
parameter_list|,
name|float
name|basedOnProgress
parameter_list|,
name|long
name|atTime
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|basedOnProgress
operator|=
name|basedOnProgress
expr_stmt|;
name|this
operator|.
name|atTime
operator|=
name|atTime
expr_stmt|;
block|}
DECL|method|incorporate (float newProgress, long newAtTime)
name|EstimateVector
name|incorporate
parameter_list|(
name|float
name|newProgress
parameter_list|,
name|long
name|newAtTime
parameter_list|)
block|{
if|if
condition|(
name|newAtTime
operator|<=
name|atTime
operator|||
name|newProgress
operator|<
name|basedOnProgress
condition|)
block|{
return|return
name|this
return|;
block|}
name|double
name|oldWeighting
init|=
name|value
operator|<
literal|0.0
condition|?
literal|0.0
else|:
name|Math
operator|.
name|exp
argument_list|(
operator|(
call|(
name|double
call|)
argument_list|(
name|newAtTime
operator|-
name|atTime
argument_list|)
operator|)
operator|/
name|lambda
argument_list|)
decl_stmt|;
name|double
name|newRead
init|=
operator|(
name|newProgress
operator|-
name|basedOnProgress
operator|)
operator|/
operator|(
name|newAtTime
operator|-
name|atTime
operator|)
decl_stmt|;
if|if
condition|(
name|smoothedValue
operator|==
name|SmoothedValue
operator|.
name|TIME_PER_UNIT_PROGRESS
condition|)
block|{
name|newRead
operator|=
literal|1.0
operator|/
name|newRead
expr_stmt|;
block|}
return|return
operator|new
name|EstimateVector
argument_list|(
name|value
operator|*
name|oldWeighting
operator|+
name|newRead
operator|*
operator|(
literal|1.0
operator|-
name|oldWeighting
operator|)
argument_list|,
name|newProgress
argument_list|,
name|newAtTime
argument_list|)
return|;
block|}
block|}
DECL|method|incorporateReading (TaskAttemptId attemptID, float newProgress, long newTime)
specifier|private
name|void
name|incorporateReading
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|,
name|float
name|newProgress
parameter_list|,
name|long
name|newTime
parameter_list|)
block|{
comment|//TODO: Refactor this method, it seems more complicated than necessary.
name|AtomicReference
argument_list|<
name|EstimateVector
argument_list|>
name|vectorRef
init|=
name|estimates
operator|.
name|get
argument_list|(
name|attemptID
argument_list|)
decl_stmt|;
if|if
condition|(
name|vectorRef
operator|==
literal|null
condition|)
block|{
name|estimates
operator|.
name|putIfAbsent
argument_list|(
name|attemptID
argument_list|,
operator|new
name|AtomicReference
argument_list|<
name|EstimateVector
argument_list|>
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|incorporateReading
argument_list|(
name|attemptID
argument_list|,
name|newProgress
argument_list|,
name|newTime
argument_list|)
expr_stmt|;
return|return;
block|}
name|EstimateVector
name|oldVector
init|=
name|vectorRef
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldVector
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|vectorRef
operator|.
name|compareAndSet
argument_list|(
literal|null
argument_list|,
operator|new
name|EstimateVector
argument_list|(
operator|-
literal|1.0
argument_list|,
literal|0.0F
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|)
argument_list|)
condition|)
block|{
return|return;
block|}
name|incorporateReading
argument_list|(
name|attemptID
argument_list|,
name|newProgress
argument_list|,
name|newTime
argument_list|)
expr_stmt|;
return|return;
block|}
while|while
condition|(
operator|!
name|vectorRef
operator|.
name|compareAndSet
argument_list|(
name|oldVector
argument_list|,
name|oldVector
operator|.
name|incorporate
argument_list|(
name|newProgress
argument_list|,
name|newTime
argument_list|)
argument_list|)
condition|)
block|{
name|oldVector
operator|=
name|vectorRef
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getEstimateVector (TaskAttemptId attemptID)
specifier|private
name|EstimateVector
name|getEstimateVector
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
block|{
name|AtomicReference
argument_list|<
name|EstimateVector
argument_list|>
name|vectorRef
init|=
name|estimates
operator|.
name|get
argument_list|(
name|attemptID
argument_list|)
decl_stmt|;
if|if
condition|(
name|vectorRef
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|vectorRef
operator|.
name|get
argument_list|()
return|;
block|}
DECL|field|DEFAULT_EXPONENTIAL_SMOOTHING_LAMBDA_MILLISECONDS
specifier|private
specifier|static
specifier|final
name|long
name|DEFAULT_EXPONENTIAL_SMOOTHING_LAMBDA_MILLISECONDS
init|=
literal|1000L
operator|*
literal|60
decl_stmt|;
annotation|@
name|Override
DECL|method|contextualize (Configuration conf, AppContext context)
specifier|public
name|void
name|contextualize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|AppContext
name|context
parameter_list|)
block|{
name|super
operator|.
name|contextualize
argument_list|(
name|conf
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|lambda
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|AMConstants
operator|.
name|EXPONENTIAL_SMOOTHING_LAMBDA_MILLISECONDS
argument_list|,
name|DEFAULT_EXPONENTIAL_SMOOTHING_LAMBDA_MILLISECONDS
argument_list|)
expr_stmt|;
name|smoothedValue
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|AMConstants
operator|.
name|EXPONENTIAL_SMOOTHING_SMOOTH_RATE
argument_list|,
literal|true
argument_list|)
condition|?
name|SmoothedValue
operator|.
name|RATE
else|:
name|SmoothedValue
operator|.
name|TIME_PER_UNIT_PROGRESS
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|estimatedRuntime (TaskAttemptId id)
specifier|public
name|long
name|estimatedRuntime
parameter_list|(
name|TaskAttemptId
name|id
parameter_list|)
block|{
name|Long
name|startTime
init|=
name|startTimes
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|startTime
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1L
return|;
block|}
name|EstimateVector
name|vector
init|=
name|getEstimateVector
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|vector
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1L
return|;
block|}
name|long
name|sunkTime
init|=
name|vector
operator|.
name|atTime
operator|-
name|startTime
decl_stmt|;
name|double
name|value
init|=
name|vector
operator|.
name|value
decl_stmt|;
name|float
name|progress
init|=
name|vector
operator|.
name|basedOnProgress
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1L
return|;
block|}
name|double
name|rate
init|=
name|smoothedValue
operator|==
name|SmoothedValue
operator|.
name|RATE
condition|?
name|value
else|:
literal|1.0
operator|/
name|value
decl_stmt|;
if|if
condition|(
name|rate
operator|==
literal|0.0
condition|)
block|{
return|return
operator|-
literal|1L
return|;
block|}
name|double
name|remainingTime
init|=
operator|(
literal|1.0
operator|-
name|progress
operator|)
operator|/
name|rate
decl_stmt|;
return|return
name|sunkTime
operator|+
operator|(
name|long
operator|)
name|remainingTime
return|;
block|}
annotation|@
name|Override
DECL|method|runtimeEstimateVariance (TaskAttemptId id)
specifier|public
name|long
name|runtimeEstimateVariance
parameter_list|(
name|TaskAttemptId
name|id
parameter_list|)
block|{
return|return
operator|-
literal|1L
return|;
block|}
annotation|@
name|Override
DECL|method|updateAttempt (TaskAttemptStatus status, long timestamp)
specifier|public
name|void
name|updateAttempt
parameter_list|(
name|TaskAttemptStatus
name|status
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
name|super
operator|.
name|updateAttempt
argument_list|(
name|status
argument_list|,
name|timestamp
argument_list|)
expr_stmt|;
name|TaskAttemptId
name|attemptID
init|=
name|status
operator|.
name|id
decl_stmt|;
name|float
name|progress
init|=
name|status
operator|.
name|progress
decl_stmt|;
name|incorporateReading
argument_list|(
name|attemptID
argument_list|,
name|progress
argument_list|,
name|timestamp
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

