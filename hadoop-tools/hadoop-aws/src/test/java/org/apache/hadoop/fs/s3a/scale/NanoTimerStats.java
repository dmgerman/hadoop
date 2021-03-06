begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.scale
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|scale
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
name|fs
operator|.
name|contract
operator|.
name|ContractTestUtils
import|;
end_import

begin_comment
comment|/**  * Collect statistics from duration data from  * {@link ContractTestUtils.NanoTimer} values.  *  * The mean and standard deviation is built up as the stats are collected,  * using "Welford's Online algorithm" for the variance.  * Trends in statistics (e.g. slowing down) are not tracked.  * Not synchronized.  */
end_comment

begin_class
DECL|class|NanoTimerStats
specifier|public
class|class
name|NanoTimerStats
block|{
DECL|field|ONE_NS
specifier|private
specifier|static
specifier|final
name|double
name|ONE_NS
init|=
literal|1.0e9
decl_stmt|;
DECL|field|operation
specifier|private
specifier|final
name|String
name|operation
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
DECL|field|sum
specifier|private
name|double
name|sum
decl_stmt|;
DECL|field|min
specifier|private
name|double
name|min
decl_stmt|;
DECL|field|max
specifier|private
name|double
name|max
decl_stmt|;
DECL|field|mean
specifier|private
name|double
name|mean
decl_stmt|;
DECL|field|m2
specifier|private
name|double
name|m2
decl_stmt|;
comment|/**    * Construct statistics for a given operation.    * @param operation operation    */
DECL|method|NanoTimerStats (String operation)
specifier|public
name|NanoTimerStats
parameter_list|(
name|String
name|operation
parameter_list|)
block|{
name|this
operator|.
name|operation
operator|=
name|operation
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**    * construct from another stats entry;    * all value are copied.    * @param that the source statistics    */
DECL|method|NanoTimerStats (NanoTimerStats that)
specifier|public
name|NanoTimerStats
parameter_list|(
name|NanoTimerStats
name|that
parameter_list|)
block|{
name|operation
operator|=
name|that
operator|.
name|operation
expr_stmt|;
name|count
operator|=
name|that
operator|.
name|count
expr_stmt|;
name|sum
operator|=
name|that
operator|.
name|sum
expr_stmt|;
name|min
operator|=
name|that
operator|.
name|min
expr_stmt|;
name|max
operator|=
name|that
operator|.
name|max
expr_stmt|;
name|mean
operator|=
name|that
operator|.
name|mean
expr_stmt|;
name|m2
operator|=
name|that
operator|.
name|m2
expr_stmt|;
block|}
comment|/**    * Add a duration.    * @param duration the new duration    */
DECL|method|add (ContractTestUtils.NanoTimer duration)
specifier|public
name|void
name|add
parameter_list|(
name|ContractTestUtils
operator|.
name|NanoTimer
name|duration
parameter_list|)
block|{
name|add
argument_list|(
name|duration
operator|.
name|elapsedTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a number.    * @param x the number    */
DECL|method|add (long x)
specifier|public
name|void
name|add
parameter_list|(
name|long
name|x
parameter_list|)
block|{
name|count
operator|++
expr_stmt|;
name|sum
operator|+=
name|x
expr_stmt|;
name|double
name|delta
init|=
name|x
operator|-
name|mean
decl_stmt|;
name|mean
operator|+=
name|delta
operator|/
name|count
expr_stmt|;
name|double
name|delta2
init|=
name|x
operator|-
name|mean
decl_stmt|;
name|m2
operator|+=
name|delta
operator|*
name|delta2
expr_stmt|;
if|if
condition|(
name|min
operator|<
literal|0
operator|||
name|x
operator|<
name|min
condition|)
block|{
name|min
operator|=
name|x
expr_stmt|;
block|}
if|if
condition|(
name|x
operator|>
name|max
condition|)
block|{
name|max
operator|=
name|x
expr_stmt|;
block|}
block|}
comment|/**    * Reset the data.    */
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|count
operator|=
literal|0
expr_stmt|;
name|sum
operator|=
literal|0
expr_stmt|;
name|sum
operator|=
literal|0
expr_stmt|;
name|min
operator|=
operator|-
literal|1
expr_stmt|;
name|max
operator|=
literal|0
expr_stmt|;
name|mean
operator|=
literal|0
expr_stmt|;
name|m2
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Get the number of entries sampled.    * @return the number of durations added    */
DECL|method|getCount ()
specifier|public
name|int
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
comment|/**    * Get the sum of all durations.    * @return all the durations    */
DECL|method|getSum ()
specifier|public
name|double
name|getSum
parameter_list|()
block|{
return|return
name|sum
return|;
block|}
comment|/**    * Get the arithmetic mean of the aggregate statistics.    * @return the arithmetic mean    */
DECL|method|getArithmeticMean ()
specifier|public
name|double
name|getArithmeticMean
parameter_list|()
block|{
return|return
name|mean
return|;
block|}
comment|/**    * Variance, {@code sigma^2}.    * @return variance, or, if no samples are there, 0.    */
DECL|method|getVariance ()
specifier|public
name|double
name|getVariance
parameter_list|()
block|{
return|return
name|count
operator|>
literal|0
condition|?
operator|(
name|m2
operator|/
operator|(
name|count
operator|-
literal|1
operator|)
operator|)
else|:
name|Double
operator|.
name|NaN
return|;
block|}
comment|/**    * Get the std deviation, sigma.    * @return the stddev, 0 may mean there are no samples.    */
DECL|method|getDeviation ()
specifier|public
name|double
name|getDeviation
parameter_list|()
block|{
name|double
name|variance
init|=
name|getVariance
argument_list|()
decl_stmt|;
return|return
operator|(
operator|!
name|Double
operator|.
name|isNaN
argument_list|(
name|variance
argument_list|)
operator|&&
name|variance
operator|>
literal|0
operator|)
condition|?
name|Math
operator|.
name|sqrt
argument_list|(
name|variance
argument_list|)
else|:
literal|0
return|;
block|}
DECL|method|toSeconds (double nano)
specifier|private
name|double
name|toSeconds
parameter_list|(
name|double
name|nano
parameter_list|)
block|{
return|return
name|nano
operator|/
name|ONE_NS
return|;
block|}
comment|/**    * Covert to a useful string.    * @return a human readable summary    */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s count=%d total=%.3fs mean=%.3fs stddev=%.3fs min=%.3fs max=%.3fs"
argument_list|,
name|operation
argument_list|,
name|count
argument_list|,
name|toSeconds
argument_list|(
name|sum
argument_list|)
argument_list|,
name|toSeconds
argument_list|(
name|mean
argument_list|)
argument_list|,
name|getDeviation
argument_list|()
operator|/
name|ONE_NS
argument_list|,
name|toSeconds
argument_list|(
name|min
argument_list|)
argument_list|,
name|toSeconds
argument_list|(
name|max
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getOperation ()
specifier|public
name|String
name|getOperation
parameter_list|()
block|{
return|return
name|operation
return|;
block|}
DECL|method|getMin ()
specifier|public
name|double
name|getMin
parameter_list|()
block|{
return|return
name|min
return|;
block|}
DECL|method|getMax ()
specifier|public
name|double
name|getMax
parameter_list|()
block|{
return|return
name|max
return|;
block|}
DECL|method|getMean ()
specifier|public
name|double
name|getMean
parameter_list|()
block|{
return|return
name|mean
return|;
block|}
block|}
end_class

end_unit

