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

begin_comment
comment|/**  *  * This class is a concrete PeriodicStatsAccumulator that deals with  *  measurements where the raw data are a measurement of a  *  time-varying quantity.  The result in each bucket is the estimate  *  of the progress-weighted mean value of that quantity over the  *  progress range covered by the bucket.  *  *<p>An easy-to-understand example of this kind of quantity would be  *  a temperature.  It makes sense to consider the mean temperature  *  over a progress range.  *  */
end_comment

begin_class
DECL|class|StatePeriodicStats
class|class
name|StatePeriodicStats
extends|extends
name|PeriodicStatsAccumulator
block|{
DECL|method|StatePeriodicStats (int count)
name|StatePeriodicStats
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|super
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
comment|/**    *    * accumulates a new reading by keeping a running account of the    *  area under the piecewise linear curve marked by pairs of    *  {@code newProgress, newValue} .    */
annotation|@
name|Override
DECL|method|extendInternal (double newProgress, int newValue)
specifier|protected
name|void
name|extendInternal
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
condition|)
block|{
return|return;
block|}
comment|// the effective height of this trapezoid if rectangularized
name|double
name|mean
init|=
operator|(
operator|(
name|double
operator|)
name|newValue
operator|+
operator|(
name|double
operator|)
name|state
operator|.
name|oldValue
operator|)
operator|/
literal|2.0D
decl_stmt|;
comment|// conceptually mean *  (newProgress - state.oldProgress) / (1 / count)
name|state
operator|.
name|currentAccumulation
operator|+=
name|mean
operator|*
operator|(
name|newProgress
operator|-
name|state
operator|.
name|oldProgress
operator|)
operator|*
name|count
expr_stmt|;
block|}
block|}
end_class

end_unit

