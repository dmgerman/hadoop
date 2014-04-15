begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|util
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Time
operator|.
name|monotonicNow
import|;
end_import

begin_comment
comment|/**   * a class to throttle the data transfers.  * This class is thread safe. It can be shared by multiple threads.  * The parameter bandwidthPerSec specifies the total bandwidth shared by  * threads.  */
end_comment

begin_class
DECL|class|DataTransferThrottler
specifier|public
class|class
name|DataTransferThrottler
block|{
DECL|field|period
specifier|private
specifier|final
name|long
name|period
decl_stmt|;
comment|// period over which bw is imposed
DECL|field|periodExtension
specifier|private
specifier|final
name|long
name|periodExtension
decl_stmt|;
comment|// Max period over which bw accumulates.
DECL|field|bytesPerPeriod
specifier|private
name|long
name|bytesPerPeriod
decl_stmt|;
comment|// total number of bytes can be sent in each period
DECL|field|curPeriodStart
specifier|private
name|long
name|curPeriodStart
decl_stmt|;
comment|// current period starting time
DECL|field|curReserve
specifier|private
name|long
name|curReserve
decl_stmt|;
comment|// remaining bytes can be sent in the period
DECL|field|bytesAlreadyUsed
specifier|private
name|long
name|bytesAlreadyUsed
decl_stmt|;
comment|/** Constructor     * @param bandwidthPerSec bandwidth allowed in bytes per second.     */
DECL|method|DataTransferThrottler (long bandwidthPerSec)
specifier|public
name|DataTransferThrottler
parameter_list|(
name|long
name|bandwidthPerSec
parameter_list|)
block|{
name|this
argument_list|(
literal|500
argument_list|,
name|bandwidthPerSec
argument_list|)
expr_stmt|;
comment|// by default throttling period is 500ms
block|}
comment|/**    * Constructor    * @param period in milliseconds. Bandwidth is enforced over this    *        period.    * @param bandwidthPerSec bandwidth allowed in bytes per second.     */
DECL|method|DataTransferThrottler (long period, long bandwidthPerSec)
specifier|public
name|DataTransferThrottler
parameter_list|(
name|long
name|period
parameter_list|,
name|long
name|bandwidthPerSec
parameter_list|)
block|{
name|this
operator|.
name|curPeriodStart
operator|=
name|monotonicNow
argument_list|()
expr_stmt|;
name|this
operator|.
name|period
operator|=
name|period
expr_stmt|;
name|this
operator|.
name|curReserve
operator|=
name|this
operator|.
name|bytesPerPeriod
operator|=
name|bandwidthPerSec
operator|*
name|period
operator|/
literal|1000
expr_stmt|;
name|this
operator|.
name|periodExtension
operator|=
name|period
operator|*
literal|3
expr_stmt|;
block|}
comment|/**    * @return current throttle bandwidth in bytes per second.    */
DECL|method|getBandwidth ()
specifier|public
specifier|synchronized
name|long
name|getBandwidth
parameter_list|()
block|{
return|return
name|bytesPerPeriod
operator|*
literal|1000
operator|/
name|period
return|;
block|}
comment|/**    * Sets throttle bandwidth. This takes affect latest by the end of current    * period.    *     * @param bytesPerSecond     */
DECL|method|setBandwidth (long bytesPerSecond)
specifier|public
specifier|synchronized
name|void
name|setBandwidth
parameter_list|(
name|long
name|bytesPerSecond
parameter_list|)
block|{
if|if
condition|(
name|bytesPerSecond
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|""
operator|+
name|bytesPerSecond
argument_list|)
throw|;
block|}
name|bytesPerPeriod
operator|=
name|bytesPerSecond
operator|*
name|period
operator|/
literal|1000
expr_stmt|;
block|}
comment|/** Given the numOfBytes sent/received since last time throttle was called,    * make the current thread sleep if I/O rate is too fast    * compared to the given bandwidth.    *    * @param numOfBytes    *     number of bytes sent/received since last time throttle was called    */
DECL|method|throttle (long numOfBytes)
specifier|public
specifier|synchronized
name|void
name|throttle
parameter_list|(
name|long
name|numOfBytes
parameter_list|)
block|{
name|throttle
argument_list|(
name|numOfBytes
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Given the numOfBytes sent/received since last time throttle was called,    * make the current thread sleep if I/O rate is too fast    * compared to the given bandwidth.  Allows for optional external cancelation.    *    * @param numOfBytes    *     number of bytes sent/received since last time throttle was called    * @param canceler    *     optional canceler to check for abort of throttle    */
DECL|method|throttle (long numOfBytes, Canceler canceler)
specifier|public
specifier|synchronized
name|void
name|throttle
parameter_list|(
name|long
name|numOfBytes
parameter_list|,
name|Canceler
name|canceler
parameter_list|)
block|{
if|if
condition|(
name|numOfBytes
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
name|curReserve
operator|-=
name|numOfBytes
expr_stmt|;
name|bytesAlreadyUsed
operator|+=
name|numOfBytes
expr_stmt|;
while|while
condition|(
name|curReserve
operator|<=
literal|0
condition|)
block|{
if|if
condition|(
name|canceler
operator|!=
literal|null
operator|&&
name|canceler
operator|.
name|isCancelled
argument_list|()
condition|)
block|{
return|return;
block|}
name|long
name|now
init|=
name|monotonicNow
argument_list|()
decl_stmt|;
name|long
name|curPeriodEnd
init|=
name|curPeriodStart
operator|+
name|period
decl_stmt|;
if|if
condition|(
name|now
operator|<
name|curPeriodEnd
condition|)
block|{
comment|// Wait for next period so that curReserve can be increased.
try|try
block|{
name|wait
argument_list|(
name|curPeriodEnd
operator|-
name|now
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Abort throttle and reset interrupted status to make sure other
comment|// interrupt handling higher in the call stack executes.
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
elseif|else
if|if
condition|(
name|now
operator|<
operator|(
name|curPeriodStart
operator|+
name|periodExtension
operator|)
condition|)
block|{
name|curPeriodStart
operator|=
name|curPeriodEnd
expr_stmt|;
name|curReserve
operator|+=
name|bytesPerPeriod
expr_stmt|;
block|}
else|else
block|{
comment|// discard the prev period. Throttler might not have
comment|// been used for a long time.
name|curPeriodStart
operator|=
name|now
expr_stmt|;
name|curReserve
operator|=
name|bytesPerPeriod
operator|-
name|bytesAlreadyUsed
expr_stmt|;
block|}
block|}
name|bytesAlreadyUsed
operator|-=
name|numOfBytes
expr_stmt|;
block|}
block|}
end_class

end_unit

