begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.top.window
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|top
operator|.
name|window
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|AtomicLong
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
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * A class for exposing a rolling window view on the event that occur over time.  * Events are reported based on occurrence time. The total number of events in  * the last period covered by the rolling window can be retrieved by the  * {@link #getSum(long)} method.  *<p>  *  * Assumptions:  *<p>  *  * (1) Concurrent invocation of {@link #incAt} method are possible  *<p>  *  * (2) The time parameter of two consecutive invocation of {@link #incAt} could  * be in any given order  *<p>  *  * (3) The buffering delays are not more than the window length, i.e., after two  * consecutive invocation {@link #incAt(long time1, long)} and  * {@link #incAt(long time2, long)}, time1&lt; time2 || time1 - time2&lt;  * windowLenMs.  * This assumption helps avoiding unnecessary synchronizations.  *<p>  *  * Thread-safety is built in the {@link RollingWindow.Bucket}  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|RollingWindow
specifier|public
class|class
name|RollingWindow
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RollingWindow
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Each window is composed of buckets, which offer a trade-off between    * accuracy and space complexity: the lower the number of buckets, the less    * memory is required by the rolling window but more inaccuracy is possible in    * reading window total values.    */
DECL|field|buckets
name|Bucket
index|[]
name|buckets
decl_stmt|;
DECL|field|windowLenMs
specifier|final
name|int
name|windowLenMs
decl_stmt|;
DECL|field|bucketSize
specifier|final
name|int
name|bucketSize
decl_stmt|;
comment|/**    * @param windowLenMs The period that is covered by the window. This period must    *          be more than the buffering delays.    * @param numBuckets number of buckets in the window    */
DECL|method|RollingWindow (int windowLenMs, int numBuckets)
name|RollingWindow
parameter_list|(
name|int
name|windowLenMs
parameter_list|,
name|int
name|numBuckets
parameter_list|)
block|{
name|buckets
operator|=
operator|new
name|Bucket
index|[
name|numBuckets
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
name|numBuckets
condition|;
name|i
operator|++
control|)
block|{
name|buckets
index|[
name|i
index|]
operator|=
operator|new
name|Bucket
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|windowLenMs
operator|=
name|windowLenMs
expr_stmt|;
name|this
operator|.
name|bucketSize
operator|=
name|windowLenMs
operator|/
name|numBuckets
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|bucketSize
operator|%
name|bucketSize
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The bucket size in the rolling window is not integer: windowLenMs= "
operator|+
name|windowLenMs
operator|+
literal|" numBuckets= "
operator|+
name|numBuckets
argument_list|)
throw|;
block|}
block|}
comment|/**    * When an event occurs at the specified time, this method reflects that in    * the rolling window.    *<p>    *    * @param time the time at which the event occurred    * @param delta the delta that will be added to the window    */
DECL|method|incAt (long time, long delta)
specifier|public
name|void
name|incAt
parameter_list|(
name|long
name|time
parameter_list|,
name|long
name|delta
parameter_list|)
block|{
name|int
name|bi
init|=
name|computeBucketIndex
argument_list|(
name|time
argument_list|)
decl_stmt|;
name|Bucket
name|bucket
init|=
name|buckets
index|[
name|bi
index|]
decl_stmt|;
comment|// If the last time the bucket was updated is out of the scope of the
comment|// rolling window, reset the bucket.
if|if
condition|(
name|bucket
operator|.
name|isStaleNow
argument_list|(
name|time
argument_list|)
condition|)
block|{
name|bucket
operator|.
name|safeReset
argument_list|(
name|time
argument_list|)
expr_stmt|;
block|}
name|bucket
operator|.
name|inc
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
DECL|method|computeBucketIndex (long time)
specifier|private
name|int
name|computeBucketIndex
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|int
name|positionOnWindow
init|=
call|(
name|int
call|)
argument_list|(
name|time
operator|%
name|windowLenMs
argument_list|)
decl_stmt|;
name|int
name|bucketIndex
init|=
name|positionOnWindow
operator|*
name|buckets
operator|.
name|length
operator|/
name|windowLenMs
decl_stmt|;
return|return
name|bucketIndex
return|;
block|}
comment|/**    * Thread-safety is provided by synchronization when resetting the update time    * as well as atomic fields.    */
DECL|class|Bucket
specifier|private
class|class
name|Bucket
block|{
DECL|field|value
name|AtomicLong
name|value
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|updateTime
name|AtomicLong
name|updateTime
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/**      * Check whether the last time that the bucket was updated is no longer      * covered by rolling window.      *      * @param time the current time      * @return true if the bucket state is stale      */
DECL|method|isStaleNow (long time)
name|boolean
name|isStaleNow
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|long
name|utime
init|=
name|updateTime
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|time
operator|-
name|utime
operator|>=
name|windowLenMs
return|;
block|}
comment|/**      * Safely reset the bucket state considering concurrent updates (inc) and      * resets.      *      * @param time the current time      */
DECL|method|safeReset (long time)
name|void
name|safeReset
parameter_list|(
name|long
name|time
parameter_list|)
block|{
comment|// At any point in time, only one thread is allowed to reset the
comment|// bucket
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|isStaleNow
argument_list|(
name|time
argument_list|)
condition|)
block|{
comment|// reset the value before setting the time, it allows other
comment|// threads to safely assume that the value is updated if the
comment|// time is not stale
name|value
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|updateTime
operator|.
name|set
argument_list|(
name|time
argument_list|)
expr_stmt|;
block|}
comment|// else a concurrent thread has already reset it: do nothing
block|}
block|}
comment|/**      * Increment the bucket. It assumes that staleness check is already      * performed. We do not need to update the {@link #updateTime} because as      * long as the {@link #updateTime} belongs to the current view of the      * rolling window, the algorithm works fine.      * @param delta      */
DECL|method|inc (long delta)
name|void
name|inc
parameter_list|(
name|long
name|delta
parameter_list|)
block|{
name|value
operator|.
name|addAndGet
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get value represented by this window at the specified time    *<p>    *    * If time lags behind the latest update time, the new updates are still    * included in the sum    *    * @param time    * @return number of events occurred in the past period    */
DECL|method|getSum (long time)
specifier|public
name|long
name|getSum
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|long
name|sum
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Bucket
name|bucket
range|:
name|buckets
control|)
block|{
name|boolean
name|stale
init|=
name|bucket
operator|.
name|isStaleNow
argument_list|(
name|time
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|stale
condition|)
block|{
name|sum
operator|+=
name|bucket
operator|.
name|value
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|long
name|bucketTime
init|=
name|bucket
operator|.
name|updateTime
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|timeStr
init|=
operator|new
name|Date
argument_list|(
name|bucketTime
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sum: + "
operator|+
name|sum
operator|+
literal|" Bucket: updateTime: "
operator|+
name|timeStr
operator|+
literal|" ("
operator|+
name|bucketTime
operator|+
literal|") isStale "
operator|+
name|stale
operator|+
literal|" at "
operator|+
name|time
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sum
return|;
block|}
block|}
end_class

end_unit

