begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|swift
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Build ongoing statistics from duration data  */
end_comment

begin_class
DECL|class|DurationStats
specifier|public
class|class
name|DurationStats
block|{
DECL|field|operation
specifier|final
name|String
name|operation
decl_stmt|;
DECL|field|n
name|int
name|n
decl_stmt|;
DECL|field|sum
name|long
name|sum
decl_stmt|;
DECL|field|min
name|long
name|min
decl_stmt|;
DECL|field|max
name|long
name|max
decl_stmt|;
DECL|field|mean
DECL|field|m2
name|double
name|mean
decl_stmt|,
name|m2
decl_stmt|;
comment|/**    * Construct statistics for a given operation.    * @param operation operation    */
DECL|method|DurationStats (String operation)
specifier|public
name|DurationStats
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
DECL|method|DurationStats (DurationStats that)
specifier|public
name|DurationStats
parameter_list|(
name|DurationStats
name|that
parameter_list|)
block|{
name|operation
operator|=
name|that
operator|.
name|operation
expr_stmt|;
name|n
operator|=
name|that
operator|.
name|n
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
comment|/**    * Add a duration    * @param duration the new duration    */
DECL|method|add (Duration duration)
specifier|public
name|void
name|add
parameter_list|(
name|Duration
name|duration
parameter_list|)
block|{
name|add
argument_list|(
name|duration
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a number    * @param x the number    */
DECL|method|add (long x)
specifier|public
name|void
name|add
parameter_list|(
name|long
name|x
parameter_list|)
block|{
name|n
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
name|n
expr_stmt|;
name|m2
operator|+=
name|delta
operator|*
operator|(
name|x
operator|-
name|mean
operator|)
expr_stmt|;
if|if
condition|(
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
comment|/**    * Reset the data    */
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|n
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
literal|10000000
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
comment|/**    * Get the number of entries sampled    * @return the number of durations added    */
DECL|method|getCount ()
specifier|public
name|int
name|getCount
parameter_list|()
block|{
return|return
name|n
return|;
block|}
comment|/**    * Get the sum of all durations    * @return all the durations    */
DECL|method|getSum ()
specifier|public
name|long
name|getSum
parameter_list|()
block|{
return|return
name|sum
return|;
block|}
comment|/**    * Get the arithmetic mean of the aggregate statistics    * @return the arithmetic mean    */
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
comment|/**    * Variance, sigma^2    * @return variance, or, if no samples are there, 0.    */
DECL|method|getVariance ()
specifier|public
name|double
name|getVariance
parameter_list|()
block|{
return|return
name|n
operator|>
literal|0
condition|?
operator|(
name|m2
operator|/
operator|(
name|n
operator|-
literal|1
operator|)
operator|)
else|:
literal|0
return|;
block|}
comment|/**    * Get the std deviation, sigma    * @return the stddev, 0 may mean there are no samples.    */
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
comment|/**    * Covert to a useful string    * @return a human readable summary    */
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
name|n
argument_list|,
name|sum
operator|/
literal|1000.0
argument_list|,
name|mean
operator|/
literal|1000.0
argument_list|,
name|getDeviation
argument_list|()
operator|/
literal|1000000.0
argument_list|,
name|min
operator|/
literal|1000.0
argument_list|,
name|max
operator|/
literal|1000.0
argument_list|)
return|;
block|}
block|}
end_class

end_unit

