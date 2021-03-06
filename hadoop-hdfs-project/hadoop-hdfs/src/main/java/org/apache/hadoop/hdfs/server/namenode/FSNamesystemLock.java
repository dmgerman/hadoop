begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|TimeUnit
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
name|AtomicInteger
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Condition
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
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|ipc
operator|.
name|Server
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
name|log
operator|.
name|LogThrottlingHelper
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
name|metrics2
operator|.
name|lib
operator|.
name|MutableRatesWithAggregation
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
name|util
operator|.
name|StringUtils
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
name|util
operator|.
name|Time
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
name|util
operator|.
name|Timer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_LOCK_SUPPRESS_WARNING_INTERVAL_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_LOCK_SUPPRESS_WARNING_INTERVAL_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_FSLOCK_FAIR_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_FSLOCK_FAIR_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_LOCK_DETAILED_METRICS_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_LOCK_DETAILED_METRICS_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_READ_LOCK_REPORTING_THRESHOLD_MS_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_READ_LOCK_REPORTING_THRESHOLD_MS_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_WRITE_LOCK_REPORTING_THRESHOLD_MS_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_WRITE_LOCK_REPORTING_THRESHOLD_MS_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
operator|.
name|ProcessingDetails
operator|.
name|Timing
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|log
operator|.
name|LogThrottlingHelper
operator|.
name|LogAction
import|;
end_import

begin_comment
comment|/**  * Mimics a ReentrantReadWriteLock but does not directly implement the interface  * so more sophisticated locking capabilities and logging/metrics are possible.  * {@link org.apache.hadoop.hdfs.DFSConfigKeys#DFS_NAMENODE_LOCK_DETAILED_METRICS_KEY}  * to be true, metrics will be emitted into the FSNamesystem metrics registry  * for each operation which acquires this lock indicating how long the operation  * held the lock for. These metrics have names of the form  * FSN(Read|Write)LockNanosOperationName, where OperationName denotes the name  * of the operation that initiated the lock hold (this will be OTHER for certain  * uncategorized operations) and they export the hold time values in  * nanoseconds. Note that if a thread dies, metrics produced after the  * most recent snapshot will be lost due to the use of  * {@link MutableRatesWithAggregation}. However since threads are re-used  * between operations this should not generally be an issue.  */
end_comment

begin_class
DECL|class|FSNamesystemLock
class|class
name|FSNamesystemLock
block|{
annotation|@
name|VisibleForTesting
DECL|field|coarseLock
specifier|protected
name|ReentrantReadWriteLock
name|coarseLock
decl_stmt|;
DECL|field|metricsEnabled
specifier|private
specifier|final
name|boolean
name|metricsEnabled
decl_stmt|;
DECL|field|detailedHoldTimeMetrics
specifier|private
specifier|final
name|MutableRatesWithAggregation
name|detailedHoldTimeMetrics
decl_stmt|;
DECL|field|timer
specifier|private
specifier|final
name|Timer
name|timer
decl_stmt|;
comment|/**    * Log statements about long lock hold times will not be produced more    * frequently than this interval.    */
DECL|field|lockSuppressWarningIntervalMs
specifier|private
specifier|final
name|long
name|lockSuppressWarningIntervalMs
decl_stmt|;
comment|/** Threshold (ms) for long holding write lock report. */
DECL|field|writeLockReportingThresholdMs
specifier|private
specifier|final
name|long
name|writeLockReportingThresholdMs
decl_stmt|;
comment|/** Last time stamp for write lock. Keep the longest one for multi-entrance.*/
DECL|field|writeLockHeldTimeStampNanos
specifier|private
name|long
name|writeLockHeldTimeStampNanos
decl_stmt|;
comment|/** Frequency limiter used for reporting long write lock hold times. */
DECL|field|writeLockReportLogger
specifier|private
specifier|final
name|LogThrottlingHelper
name|writeLockReportLogger
decl_stmt|;
comment|/** Threshold (ms) for long holding read lock report. */
DECL|field|readLockReportingThresholdMs
specifier|private
specifier|final
name|long
name|readLockReportingThresholdMs
decl_stmt|;
comment|/**    * Last time stamp for read lock. Keep the longest one for    * multi-entrance. This is ThreadLocal since there could be    * many read locks held simultaneously.    */
DECL|field|readLockHeldTimeStampNanos
specifier|private
specifier|final
name|ThreadLocal
argument_list|<
name|Long
argument_list|>
name|readLockHeldTimeStampNanos
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|initialValue
parameter_list|()
block|{
return|return
name|Long
operator|.
name|MAX_VALUE
return|;
block|}
block|}
decl_stmt|;
DECL|field|numReadLockWarningsSuppressed
specifier|private
specifier|final
name|AtomicInteger
name|numReadLockWarningsSuppressed
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/** Time stamp (ms) of the last time a read lock report was written. */
DECL|field|timeStampOfLastReadLockReportMs
specifier|private
specifier|final
name|AtomicLong
name|timeStampOfLastReadLockReportMs
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/**    * The info (lock held time and stack trace) when longest time (ms) a read    * lock was held since the last report.    */
DECL|field|longestReadLockHeldInfo
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|LockHeldInfo
argument_list|>
name|longestReadLockHeldInfo
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|(
operator|new
name|LockHeldInfo
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|longestWriteLockHeldInfo
specifier|private
name|LockHeldInfo
name|longestWriteLockHeldInfo
init|=
operator|new
name|LockHeldInfo
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|OP_NAME_OTHER
specifier|static
specifier|final
name|String
name|OP_NAME_OTHER
init|=
literal|"OTHER"
decl_stmt|;
DECL|field|READ_LOCK_METRIC_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|READ_LOCK_METRIC_PREFIX
init|=
literal|"FSNReadLock"
decl_stmt|;
DECL|field|WRITE_LOCK_METRIC_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|WRITE_LOCK_METRIC_PREFIX
init|=
literal|"FSNWriteLock"
decl_stmt|;
DECL|field|LOCK_METRIC_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|LOCK_METRIC_SUFFIX
init|=
literal|"Nanos"
decl_stmt|;
DECL|field|OVERALL_METRIC_NAME
specifier|private
specifier|static
specifier|final
name|String
name|OVERALL_METRIC_NAME
init|=
literal|"Overall"
decl_stmt|;
DECL|method|FSNamesystemLock (Configuration conf, MutableRatesWithAggregation detailedHoldTimeMetrics)
name|FSNamesystemLock
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|MutableRatesWithAggregation
name|detailedHoldTimeMetrics
parameter_list|)
block|{
name|this
argument_list|(
name|conf
argument_list|,
name|detailedHoldTimeMetrics
argument_list|,
operator|new
name|Timer
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|FSNamesystemLock (Configuration conf, MutableRatesWithAggregation detailedHoldTimeMetrics, Timer timer)
name|FSNamesystemLock
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|MutableRatesWithAggregation
name|detailedHoldTimeMetrics
parameter_list|,
name|Timer
name|timer
parameter_list|)
block|{
name|boolean
name|fair
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|DFS_NAMENODE_FSLOCK_FAIR_KEY
argument_list|,
name|DFS_NAMENODE_FSLOCK_FAIR_DEFAULT
argument_list|)
decl_stmt|;
name|FSNamesystem
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"fsLock is fair: "
operator|+
name|fair
argument_list|)
expr_stmt|;
name|this
operator|.
name|coarseLock
operator|=
operator|new
name|ReentrantReadWriteLock
argument_list|(
name|fair
argument_list|)
expr_stmt|;
name|this
operator|.
name|timer
operator|=
name|timer
expr_stmt|;
name|this
operator|.
name|writeLockReportingThresholdMs
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|DFS_NAMENODE_WRITE_LOCK_REPORTING_THRESHOLD_MS_KEY
argument_list|,
name|DFS_NAMENODE_WRITE_LOCK_REPORTING_THRESHOLD_MS_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|readLockReportingThresholdMs
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|DFS_NAMENODE_READ_LOCK_REPORTING_THRESHOLD_MS_KEY
argument_list|,
name|DFS_NAMENODE_READ_LOCK_REPORTING_THRESHOLD_MS_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|lockSuppressWarningIntervalMs
operator|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|DFS_LOCK_SUPPRESS_WARNING_INTERVAL_KEY
argument_list|,
name|DFS_LOCK_SUPPRESS_WARNING_INTERVAL_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|this
operator|.
name|writeLockReportLogger
operator|=
operator|new
name|LogThrottlingHelper
argument_list|(
name|lockSuppressWarningIntervalMs
argument_list|)
expr_stmt|;
name|this
operator|.
name|metricsEnabled
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|DFS_NAMENODE_LOCK_DETAILED_METRICS_KEY
argument_list|,
name|DFS_NAMENODE_LOCK_DETAILED_METRICS_DEFAULT
argument_list|)
expr_stmt|;
name|FSNamesystem
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Detailed lock hold time metrics enabled: "
operator|+
name|this
operator|.
name|metricsEnabled
argument_list|)
expr_stmt|;
name|this
operator|.
name|detailedHoldTimeMetrics
operator|=
name|detailedHoldTimeMetrics
expr_stmt|;
block|}
DECL|method|readLock ()
specifier|public
name|void
name|readLock
parameter_list|()
block|{
name|doLock
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|readLockInterruptibly ()
specifier|public
name|void
name|readLockInterruptibly
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|doLockInterruptibly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|readUnlock ()
specifier|public
name|void
name|readUnlock
parameter_list|()
block|{
name|readUnlock
argument_list|(
name|OP_NAME_OTHER
argument_list|)
expr_stmt|;
block|}
DECL|method|readUnlock (String opName)
specifier|public
name|void
name|readUnlock
parameter_list|(
name|String
name|opName
parameter_list|)
block|{
specifier|final
name|boolean
name|needReport
init|=
name|coarseLock
operator|.
name|getReadHoldCount
argument_list|()
operator|==
literal|1
decl_stmt|;
specifier|final
name|long
name|currentTimeStampNanos
init|=
name|timer
operator|.
name|monotonicNowNanos
argument_list|()
decl_stmt|;
specifier|final
name|long
name|readLockIntervalNanos
init|=
name|currentTimeStampNanos
operator|-
name|readLockHeldTimeStampNanos
operator|.
name|get
argument_list|()
decl_stmt|;
name|coarseLock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
if|if
condition|(
name|needReport
condition|)
block|{
name|addMetric
argument_list|(
name|opName
argument_list|,
name|readLockIntervalNanos
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|readLockHeldTimeStampNanos
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
specifier|final
name|long
name|readLockIntervalMs
init|=
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|readLockIntervalNanos
argument_list|)
decl_stmt|;
specifier|final
name|long
name|currentTimeMs
init|=
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|currentTimeStampNanos
argument_list|)
decl_stmt|;
if|if
condition|(
name|needReport
operator|&&
name|readLockIntervalMs
operator|>=
name|this
operator|.
name|readLockReportingThresholdMs
condition|)
block|{
name|LockHeldInfo
name|localLockHeldInfo
decl_stmt|;
do|do
block|{
name|localLockHeldInfo
operator|=
name|longestReadLockHeldInfo
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|localLockHeldInfo
operator|.
name|getIntervalMs
argument_list|()
operator|-
name|readLockIntervalMs
operator|<
literal|0
operator|&&
operator|!
name|longestReadLockHeldInfo
operator|.
name|compareAndSet
argument_list|(
name|localLockHeldInfo
argument_list|,
operator|new
name|LockHeldInfo
argument_list|(
name|currentTimeMs
argument_list|,
name|readLockIntervalMs
argument_list|,
name|StringUtils
operator|.
name|getStackTrace
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
argument_list|)
argument_list|)
condition|)
do|;
name|long
name|localTimeStampOfLastReadLockReport
decl_stmt|;
name|long
name|nowMs
decl_stmt|;
do|do
block|{
name|nowMs
operator|=
name|timer
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
name|localTimeStampOfLastReadLockReport
operator|=
name|timeStampOfLastReadLockReportMs
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|nowMs
operator|-
name|localTimeStampOfLastReadLockReport
operator|<
name|lockSuppressWarningIntervalMs
condition|)
block|{
name|numReadLockWarningsSuppressed
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
do|while
condition|(
operator|!
name|timeStampOfLastReadLockReportMs
operator|.
name|compareAndSet
argument_list|(
name|localTimeStampOfLastReadLockReport
argument_list|,
name|nowMs
argument_list|)
condition|)
do|;
name|int
name|numSuppressedWarnings
init|=
name|numReadLockWarningsSuppressed
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|LockHeldInfo
name|lockHeldInfo
init|=
name|longestReadLockHeldInfo
operator|.
name|getAndSet
argument_list|(
operator|new
name|LockHeldInfo
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|FSNamesystem
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"\tNumber of suppressed read-lock reports: {}"
operator|+
literal|"\n\tLongest read-lock held at {} for {}ms via {}"
argument_list|,
name|numSuppressedWarnings
argument_list|,
name|Time
operator|.
name|formatTime
argument_list|(
name|lockHeldInfo
operator|.
name|getStartTimeMs
argument_list|()
argument_list|)
argument_list|,
name|lockHeldInfo
operator|.
name|getIntervalMs
argument_list|()
argument_list|,
name|lockHeldInfo
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeLock ()
specifier|public
name|void
name|writeLock
parameter_list|()
block|{
name|doLock
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|writeLockInterruptibly ()
specifier|public
name|void
name|writeLockInterruptibly
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|doLockInterruptibly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Unlocks FSNameSystem write lock. This internally calls {@link    * FSNamesystemLock#writeUnlock(String, boolean)}    */
DECL|method|writeUnlock ()
specifier|public
name|void
name|writeUnlock
parameter_list|()
block|{
name|writeUnlock
argument_list|(
name|OP_NAME_OTHER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Unlocks FSNameSystem write lock. This internally calls {@link    * FSNamesystemLock#writeUnlock(String, boolean)}    *    * @param opName Operation name.    */
DECL|method|writeUnlock (String opName)
specifier|public
name|void
name|writeUnlock
parameter_list|(
name|String
name|opName
parameter_list|)
block|{
name|writeUnlock
argument_list|(
name|opName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Unlocks FSNameSystem write lock.    *    * @param opName Operation name    * @param suppressWriteLockReport When false, event of write lock being held    * for long time will be logged in logs and metrics.    */
DECL|method|writeUnlock (String opName, boolean suppressWriteLockReport)
specifier|public
name|void
name|writeUnlock
parameter_list|(
name|String
name|opName
parameter_list|,
name|boolean
name|suppressWriteLockReport
parameter_list|)
block|{
specifier|final
name|boolean
name|needReport
init|=
operator|!
name|suppressWriteLockReport
operator|&&
name|coarseLock
operator|.
name|getWriteHoldCount
argument_list|()
operator|==
literal|1
operator|&&
name|coarseLock
operator|.
name|isWriteLockedByCurrentThread
argument_list|()
decl_stmt|;
specifier|final
name|long
name|currentTimeNanos
init|=
name|timer
operator|.
name|monotonicNowNanos
argument_list|()
decl_stmt|;
specifier|final
name|long
name|writeLockIntervalNanos
init|=
name|currentTimeNanos
operator|-
name|writeLockHeldTimeStampNanos
decl_stmt|;
specifier|final
name|long
name|currentTimeMs
init|=
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|currentTimeNanos
argument_list|)
decl_stmt|;
specifier|final
name|long
name|writeLockIntervalMs
init|=
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|writeLockIntervalNanos
argument_list|)
decl_stmt|;
name|LogAction
name|logAction
init|=
name|LogThrottlingHelper
operator|.
name|DO_NOT_LOG
decl_stmt|;
if|if
condition|(
name|needReport
operator|&&
name|writeLockIntervalMs
operator|>=
name|this
operator|.
name|writeLockReportingThresholdMs
condition|)
block|{
if|if
condition|(
name|longestWriteLockHeldInfo
operator|.
name|getIntervalMs
argument_list|()
operator|<
name|writeLockIntervalMs
condition|)
block|{
name|longestWriteLockHeldInfo
operator|=
operator|new
name|LockHeldInfo
argument_list|(
name|currentTimeMs
argument_list|,
name|writeLockIntervalMs
argument_list|,
name|StringUtils
operator|.
name|getStackTrace
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|logAction
operator|=
name|writeLockReportLogger
operator|.
name|record
argument_list|(
literal|"write"
argument_list|,
name|currentTimeMs
argument_list|,
name|writeLockIntervalMs
argument_list|)
expr_stmt|;
block|}
name|LockHeldInfo
name|lockHeldInfo
init|=
name|longestWriteLockHeldInfo
decl_stmt|;
if|if
condition|(
name|logAction
operator|.
name|shouldLog
argument_list|()
condition|)
block|{
name|longestWriteLockHeldInfo
operator|=
operator|new
name|LockHeldInfo
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|coarseLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
if|if
condition|(
name|needReport
condition|)
block|{
name|addMetric
argument_list|(
name|opName
argument_list|,
name|writeLockIntervalNanos
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|logAction
operator|.
name|shouldLog
argument_list|()
condition|)
block|{
name|FSNamesystem
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"\tNumber of suppressed write-lock reports: {}"
operator|+
literal|"\n\tLongest write-lock held at {} for {}ms via {}"
operator|+
literal|"\n\tTotal suppressed write-lock held time: {}"
argument_list|,
name|logAction
operator|.
name|getCount
argument_list|()
operator|-
literal|1
argument_list|,
name|Time
operator|.
name|formatTime
argument_list|(
name|lockHeldInfo
operator|.
name|getStartTimeMs
argument_list|()
argument_list|)
argument_list|,
name|lockHeldInfo
operator|.
name|getIntervalMs
argument_list|()
argument_list|,
name|lockHeldInfo
operator|.
name|getStackTrace
argument_list|()
argument_list|,
name|logAction
operator|.
name|getStats
argument_list|(
literal|0
argument_list|)
operator|.
name|getSum
argument_list|()
operator|-
name|lockHeldInfo
operator|.
name|getIntervalMs
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getReadHoldCount ()
specifier|public
name|int
name|getReadHoldCount
parameter_list|()
block|{
return|return
name|coarseLock
operator|.
name|getReadHoldCount
argument_list|()
return|;
block|}
DECL|method|getWriteHoldCount ()
specifier|public
name|int
name|getWriteHoldCount
parameter_list|()
block|{
return|return
name|coarseLock
operator|.
name|getWriteHoldCount
argument_list|()
return|;
block|}
DECL|method|isWriteLockedByCurrentThread ()
specifier|public
name|boolean
name|isWriteLockedByCurrentThread
parameter_list|()
block|{
return|return
name|coarseLock
operator|.
name|isWriteLockedByCurrentThread
argument_list|()
return|;
block|}
DECL|method|newWriteLockCondition ()
specifier|public
name|Condition
name|newWriteLockCondition
parameter_list|()
block|{
return|return
name|coarseLock
operator|.
name|writeLock
argument_list|()
operator|.
name|newCondition
argument_list|()
return|;
block|}
comment|/**    * Returns the QueueLength of waiting threads.    *    * A larger number indicates greater lock contention.    *    * @return int - Number of threads waiting on this lock    */
DECL|method|getQueueLength ()
specifier|public
name|int
name|getQueueLength
parameter_list|()
block|{
return|return
name|coarseLock
operator|.
name|getQueueLength
argument_list|()
return|;
block|}
comment|/**    * Add the lock hold time for a recent operation to the metrics.    * @param operationName Name of the operation for which to record the time    * @param value Length of time the lock was held (nanoseconds)    */
DECL|method|addMetric (String operationName, long value, boolean isWrite)
specifier|private
name|void
name|addMetric
parameter_list|(
name|String
name|operationName
parameter_list|,
name|long
name|value
parameter_list|,
name|boolean
name|isWrite
parameter_list|)
block|{
if|if
condition|(
name|metricsEnabled
condition|)
block|{
name|String
name|opMetric
init|=
name|getMetricName
argument_list|(
name|operationName
argument_list|,
name|isWrite
argument_list|)
decl_stmt|;
name|detailedHoldTimeMetrics
operator|.
name|add
argument_list|(
name|opMetric
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|String
name|overallMetric
init|=
name|getMetricName
argument_list|(
name|OVERALL_METRIC_NAME
argument_list|,
name|isWrite
argument_list|)
decl_stmt|;
name|detailedHoldTimeMetrics
operator|.
name|add
argument_list|(
name|overallMetric
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|updateProcessingDetails
argument_list|(
name|isWrite
condition|?
name|Timing
operator|.
name|LOCKEXCLUSIVE
else|:
name|Timing
operator|.
name|LOCKSHARED
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|doLock (boolean isWrite)
specifier|private
name|void
name|doLock
parameter_list|(
name|boolean
name|isWrite
parameter_list|)
block|{
name|long
name|startNanos
init|=
name|timer
operator|.
name|monotonicNowNanos
argument_list|()
decl_stmt|;
if|if
condition|(
name|isWrite
condition|)
block|{
name|coarseLock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|coarseLock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
name|updateLockWait
argument_list|(
name|startNanos
argument_list|,
name|isWrite
argument_list|)
expr_stmt|;
block|}
DECL|method|doLockInterruptibly (boolean isWrite)
specifier|private
name|void
name|doLockInterruptibly
parameter_list|(
name|boolean
name|isWrite
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|long
name|startNanos
init|=
name|timer
operator|.
name|monotonicNowNanos
argument_list|()
decl_stmt|;
if|if
condition|(
name|isWrite
condition|)
block|{
name|coarseLock
operator|.
name|writeLock
argument_list|()
operator|.
name|lockInterruptibly
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|coarseLock
operator|.
name|readLock
argument_list|()
operator|.
name|lockInterruptibly
argument_list|()
expr_stmt|;
block|}
name|updateLockWait
argument_list|(
name|startNanos
argument_list|,
name|isWrite
argument_list|)
expr_stmt|;
block|}
DECL|method|updateLockWait (long startNanos, boolean isWrite)
specifier|private
name|void
name|updateLockWait
parameter_list|(
name|long
name|startNanos
parameter_list|,
name|boolean
name|isWrite
parameter_list|)
block|{
name|long
name|now
init|=
name|timer
operator|.
name|monotonicNowNanos
argument_list|()
decl_stmt|;
name|updateProcessingDetails
argument_list|(
name|Timing
operator|.
name|LOCKWAIT
argument_list|,
name|now
operator|-
name|startNanos
argument_list|)
expr_stmt|;
if|if
condition|(
name|isWrite
condition|)
block|{
if|if
condition|(
name|coarseLock
operator|.
name|getWriteHoldCount
argument_list|()
operator|==
literal|1
condition|)
block|{
name|writeLockHeldTimeStampNanos
operator|=
name|now
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|coarseLock
operator|.
name|getReadHoldCount
argument_list|()
operator|==
literal|1
condition|)
block|{
name|readLockHeldTimeStampNanos
operator|.
name|set
argument_list|(
name|now
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|updateProcessingDetails (Timing type, long deltaNanos)
specifier|private
specifier|static
name|void
name|updateProcessingDetails
parameter_list|(
name|Timing
name|type
parameter_list|,
name|long
name|deltaNanos
parameter_list|)
block|{
name|Server
operator|.
name|Call
name|call
init|=
name|Server
operator|.
name|getCurCall
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|call
operator|!=
literal|null
condition|)
block|{
name|call
operator|.
name|getProcessingDetails
argument_list|()
operator|.
name|add
argument_list|(
name|type
argument_list|,
name|deltaNanos
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getMetricName (String operationName, boolean isWrite)
specifier|private
specifier|static
name|String
name|getMetricName
parameter_list|(
name|String
name|operationName
parameter_list|,
name|boolean
name|isWrite
parameter_list|)
block|{
return|return
operator|(
name|isWrite
condition|?
name|WRITE_LOCK_METRIC_PREFIX
else|:
name|READ_LOCK_METRIC_PREFIX
operator|)
operator|+
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
operator|.
name|capitalize
argument_list|(
name|operationName
argument_list|)
operator|+
name|LOCK_METRIC_SUFFIX
return|;
block|}
comment|/**    * Read lock Held Info.    */
DECL|class|LockHeldInfo
specifier|private
specifier|static
class|class
name|LockHeldInfo
block|{
comment|/** Lock held start time. */
DECL|field|startTimeMs
specifier|private
name|Long
name|startTimeMs
decl_stmt|;
comment|/** Lock held time. */
DECL|field|intervalMs
specifier|private
name|Long
name|intervalMs
decl_stmt|;
comment|/** The stack trace lock was held. */
DECL|field|stackTrace
specifier|private
name|String
name|stackTrace
decl_stmt|;
DECL|method|LockHeldInfo (long startTimeMs, long intervalMs, String stackTrace)
name|LockHeldInfo
parameter_list|(
name|long
name|startTimeMs
parameter_list|,
name|long
name|intervalMs
parameter_list|,
name|String
name|stackTrace
parameter_list|)
block|{
name|this
operator|.
name|startTimeMs
operator|=
name|startTimeMs
expr_stmt|;
name|this
operator|.
name|intervalMs
operator|=
name|intervalMs
expr_stmt|;
name|this
operator|.
name|stackTrace
operator|=
name|stackTrace
expr_stmt|;
block|}
DECL|method|getStartTimeMs ()
specifier|public
name|Long
name|getStartTimeMs
parameter_list|()
block|{
return|return
name|this
operator|.
name|startTimeMs
return|;
block|}
DECL|method|getIntervalMs ()
specifier|public
name|Long
name|getIntervalMs
parameter_list|()
block|{
return|return
name|this
operator|.
name|intervalMs
return|;
block|}
DECL|method|getStackTrace ()
specifier|public
name|String
name|getStackTrace
parameter_list|()
block|{
return|return
name|this
operator|.
name|stackTrace
return|;
block|}
block|}
block|}
end_class

end_unit

