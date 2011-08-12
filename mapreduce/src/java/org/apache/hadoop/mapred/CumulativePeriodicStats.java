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
comment|/**  *  * This class is a concrete PeriodicStatsAccumulator that deals with  *  measurements where the raw data are a measurement of an  *  accumulation.  The result in each bucket is the estimate   *  of the progress-weighted change in that quantity over the  *  progress range covered by the bucket.  *  *<p>An easy-to-understand example of this kind of quantity would be  *  a distance traveled.  It makes sense to consider that portion of  *  the total travel that can be apportioned to each bucket.  *  */
end_comment

begin_class
DECL|class|CumulativePeriodicStats
class|class
name|CumulativePeriodicStats
extends|extends
name|PeriodicStatsAccumulator
block|{
comment|// int's are acceptable here, even though times are normally
comment|// long's, because these are a difference and an int won't
comment|// overflow for 24 days.  Tasks can't run for more than about a
comment|// week for other reasons, and most jobs would be written
DECL|field|previousValue
name|int
name|previousValue
init|=
literal|0
decl_stmt|;
DECL|method|CumulativePeriodicStats (int count)
name|CumulativePeriodicStats
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
comment|/**    *    * accumulates a new reading by keeping a running account of the    *  value distance from the beginning of the bucket to the end of    *  this reading    */
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
name|state
operator|.
name|currentAccumulation
operator|+=
call|(
name|double
call|)
argument_list|(
name|newValue
operator|-
name|previousValue
argument_list|)
expr_stmt|;
name|previousValue
operator|=
name|newValue
expr_stmt|;
block|}
block|}
end_class

end_unit

