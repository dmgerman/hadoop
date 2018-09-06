begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
package|;
end_package

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

begin_import
import|import
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
name|datanode
operator|.
name|BPServiceActor
operator|.
name|Scheduler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|abs
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doReturn
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|spy
import|;
end_import

begin_comment
comment|/**  * Verify the block report and heartbeat scheduling logic of BPServiceActor  * using a few different values .  */
end_comment

begin_class
DECL|class|TestBpServiceActorScheduler
specifier|public
class|class
name|TestBpServiceActorScheduler
block|{
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestBpServiceActorScheduler
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|timeout
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
decl_stmt|;
DECL|field|HEARTBEAT_INTERVAL_MS
specifier|private
specifier|static
specifier|final
name|long
name|HEARTBEAT_INTERVAL_MS
init|=
literal|5000
decl_stmt|;
comment|// 5 seconds
DECL|field|LIFELINE_INTERVAL_MS
specifier|private
specifier|static
specifier|final
name|long
name|LIFELINE_INTERVAL_MS
init|=
literal|3
operator|*
name|HEARTBEAT_INTERVAL_MS
decl_stmt|;
DECL|field|BLOCK_REPORT_INTERVAL_MS
specifier|private
specifier|static
specifier|final
name|long
name|BLOCK_REPORT_INTERVAL_MS
init|=
literal|10000
decl_stmt|;
comment|// 10 seconds
DECL|field|OUTLIER_REPORT_INTERVAL_MS
specifier|private
specifier|static
specifier|final
name|long
name|OUTLIER_REPORT_INTERVAL_MS
init|=
literal|10000
decl_stmt|;
comment|// 10 seconds
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testInit ()
specifier|public
name|void
name|testInit
parameter_list|()
block|{
for|for
control|(
specifier|final
name|long
name|now
range|:
name|getTimestamps
argument_list|()
control|)
block|{
name|Scheduler
name|scheduler
init|=
name|makeMockScheduler
argument_list|(
name|now
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|scheduler
operator|.
name|isHeartbeatDue
argument_list|(
name|now
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scheduler
operator|.
name|isBlockReportDue
argument_list|(
name|scheduler
operator|.
name|monotonicNow
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testScheduleBlockReportImmediate ()
specifier|public
name|void
name|testScheduleBlockReportImmediate
parameter_list|()
block|{
for|for
control|(
specifier|final
name|long
name|now
range|:
name|getTimestamps
argument_list|()
control|)
block|{
name|Scheduler
name|scheduler
init|=
name|makeMockScheduler
argument_list|(
name|now
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|scheduleBlockReport
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scheduler
operator|.
name|resetBlockReportTime
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|scheduler
operator|.
name|nextBlockReportTime
argument_list|,
name|is
argument_list|(
name|now
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testScheduleBlockReportDelayed ()
specifier|public
name|void
name|testScheduleBlockReportDelayed
parameter_list|()
block|{
for|for
control|(
specifier|final
name|long
name|now
range|:
name|getTimestamps
argument_list|()
control|)
block|{
name|Scheduler
name|scheduler
init|=
name|makeMockScheduler
argument_list|(
name|now
argument_list|)
decl_stmt|;
specifier|final
name|long
name|delayMs
init|=
literal|10
decl_stmt|;
name|scheduler
operator|.
name|scheduleBlockReport
argument_list|(
name|delayMs
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scheduler
operator|.
name|resetBlockReportTime
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scheduler
operator|.
name|nextBlockReportTime
operator|-
name|now
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scheduler
operator|.
name|nextBlockReportTime
operator|-
operator|(
name|now
operator|+
name|delayMs
operator|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * If resetBlockReportTime is true then the next block report must be scheduled    * in the range [now, now + BLOCK_REPORT_INTERVAL_SEC).    */
annotation|@
name|Test
DECL|method|testScheduleNextBlockReport ()
specifier|public
name|void
name|testScheduleNextBlockReport
parameter_list|()
block|{
for|for
control|(
specifier|final
name|long
name|now
range|:
name|getTimestamps
argument_list|()
control|)
block|{
name|Scheduler
name|scheduler
init|=
name|makeMockScheduler
argument_list|(
name|now
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|scheduler
operator|.
name|resetBlockReportTime
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|scheduleNextBlockReport
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|scheduler
operator|.
name|nextBlockReportTime
operator|-
operator|(
name|now
operator|+
name|BLOCK_REPORT_INTERVAL_MS
operator|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * If resetBlockReportTime is false then the next block report must be scheduled    * exactly at (now + BLOCK_REPORT_INTERVAL_SEC).    */
annotation|@
name|Test
DECL|method|testScheduleNextBlockReport2 ()
specifier|public
name|void
name|testScheduleNextBlockReport2
parameter_list|()
block|{
for|for
control|(
specifier|final
name|long
name|now
range|:
name|getTimestamps
argument_list|()
control|)
block|{
name|Scheduler
name|scheduler
init|=
name|makeMockScheduler
argument_list|(
name|now
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|resetBlockReportTime
operator|=
literal|false
expr_stmt|;
name|scheduler
operator|.
name|scheduleNextBlockReport
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|scheduler
operator|.
name|nextBlockReportTime
argument_list|,
name|is
argument_list|(
name|now
operator|+
name|BLOCK_REPORT_INTERVAL_MS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Tests the case when a block report was delayed past its scheduled time.    * In that case the next block report should not be delayed for a full interval.    */
annotation|@
name|Test
DECL|method|testScheduleNextBlockReport3 ()
specifier|public
name|void
name|testScheduleNextBlockReport3
parameter_list|()
block|{
for|for
control|(
specifier|final
name|long
name|now
range|:
name|getTimestamps
argument_list|()
control|)
block|{
name|Scheduler
name|scheduler
init|=
name|makeMockScheduler
argument_list|(
name|now
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|resetBlockReportTime
operator|=
literal|false
expr_stmt|;
comment|// Make it look like the block report was scheduled to be sent between 1-3
comment|// intervals ago but sent just now.
specifier|final
name|long
name|blockReportDelay
init|=
name|BLOCK_REPORT_INTERVAL_MS
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|2
operator|*
operator|(
name|int
operator|)
name|BLOCK_REPORT_INTERVAL_MS
argument_list|)
decl_stmt|;
specifier|final
name|long
name|origBlockReportTime
init|=
name|now
operator|-
name|blockReportDelay
decl_stmt|;
name|scheduler
operator|.
name|nextBlockReportTime
operator|=
name|origBlockReportTime
expr_stmt|;
name|scheduler
operator|.
name|scheduleNextBlockReport
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|scheduler
operator|.
name|nextBlockReportTime
operator|-
name|now
operator|<
name|BLOCK_REPORT_INTERVAL_MS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|scheduler
operator|.
name|nextBlockReportTime
operator|-
name|origBlockReportTime
operator|)
operator|%
name|BLOCK_REPORT_INTERVAL_MS
operator|)
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testScheduleHeartbeat ()
specifier|public
name|void
name|testScheduleHeartbeat
parameter_list|()
block|{
for|for
control|(
specifier|final
name|long
name|now
range|:
name|getTimestamps
argument_list|()
control|)
block|{
name|Scheduler
name|scheduler
init|=
name|makeMockScheduler
argument_list|(
name|now
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|scheduleNextHeartbeat
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|scheduler
operator|.
name|isHeartbeatDue
argument_list|(
name|now
argument_list|)
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|scheduleHeartbeat
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|scheduler
operator|.
name|isHeartbeatDue
argument_list|(
name|now
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Regression test for HDFS-9305.    * Delayed processing of a heartbeat can cause a subsequent heartbeat    * storm.    */
annotation|@
name|Test
DECL|method|testScheduleDelayedHeartbeat ()
specifier|public
name|void
name|testScheduleDelayedHeartbeat
parameter_list|()
block|{
for|for
control|(
specifier|final
name|long
name|now
range|:
name|getTimestamps
argument_list|()
control|)
block|{
name|Scheduler
name|scheduler
init|=
name|makeMockScheduler
argument_list|(
name|now
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|scheduleNextHeartbeat
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|scheduler
operator|.
name|isHeartbeatDue
argument_list|(
name|now
argument_list|)
argument_list|)
expr_stmt|;
comment|// Simulate a delayed heartbeat e.g. due to slow processing by NN.
name|scheduler
operator|.
name|nextHeartbeatTime
operator|=
name|now
operator|-
operator|(
name|HEARTBEAT_INTERVAL_MS
operator|*
literal|10
operator|)
expr_stmt|;
name|scheduler
operator|.
name|scheduleNextHeartbeat
argument_list|()
expr_stmt|;
comment|// Ensure that the next heartbeat is not due immediately.
name|assertFalse
argument_list|(
name|scheduler
operator|.
name|isHeartbeatDue
argument_list|(
name|now
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testScheduleLifeline ()
specifier|public
name|void
name|testScheduleLifeline
parameter_list|()
block|{
for|for
control|(
specifier|final
name|long
name|now
range|:
name|getTimestamps
argument_list|()
control|)
block|{
name|Scheduler
name|scheduler
init|=
name|makeMockScheduler
argument_list|(
name|now
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|scheduleNextLifeline
argument_list|(
name|now
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|scheduler
operator|.
name|isLifelineDue
argument_list|(
name|now
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|scheduler
operator|.
name|getLifelineWaitTime
argument_list|()
argument_list|,
name|is
argument_list|(
name|LIFELINE_INTERVAL_MS
argument_list|)
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|scheduleNextLifeline
argument_list|(
name|now
operator|-
name|LIFELINE_INTERVAL_MS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scheduler
operator|.
name|isLifelineDue
argument_list|(
name|now
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|scheduler
operator|.
name|getLifelineWaitTime
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testOutlierReportScheduling ()
specifier|public
name|void
name|testOutlierReportScheduling
parameter_list|()
block|{
for|for
control|(
specifier|final
name|long
name|now
range|:
name|getTimestamps
argument_list|()
control|)
block|{
name|Scheduler
name|scheduler
init|=
name|makeMockScheduler
argument_list|(
name|now
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|scheduler
operator|.
name|isOutliersReportDue
argument_list|(
name|now
argument_list|)
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|scheduleNextOutlierReport
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|scheduler
operator|.
name|isOutliersReportDue
argument_list|(
name|now
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|scheduler
operator|.
name|isOutliersReportDue
argument_list|(
name|now
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scheduler
operator|.
name|isOutliersReportDue
argument_list|(
name|now
operator|+
name|OUTLIER_REPORT_INTERVAL_MS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|makeMockScheduler (long now)
specifier|private
name|Scheduler
name|makeMockScheduler
parameter_list|(
name|long
name|now
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Using now = "
operator|+
name|now
argument_list|)
expr_stmt|;
name|Scheduler
name|mockScheduler
init|=
name|spy
argument_list|(
operator|new
name|Scheduler
argument_list|(
name|HEARTBEAT_INTERVAL_MS
argument_list|,
name|LIFELINE_INTERVAL_MS
argument_list|,
name|BLOCK_REPORT_INTERVAL_MS
argument_list|,
name|OUTLIER_REPORT_INTERVAL_MS
argument_list|)
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|now
argument_list|)
operator|.
name|when
argument_list|(
name|mockScheduler
argument_list|)
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
name|mockScheduler
operator|.
name|nextBlockReportTime
operator|=
name|now
expr_stmt|;
name|mockScheduler
operator|.
name|nextHeartbeatTime
operator|=
name|now
expr_stmt|;
name|mockScheduler
operator|.
name|nextOutliersReportTime
operator|=
name|now
expr_stmt|;
return|return
name|mockScheduler
return|;
block|}
DECL|method|getTimestamps ()
name|List
argument_list|<
name|Long
argument_list|>
name|getTimestamps
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
literal|0L
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
comment|// test boundaries
name|Long
operator|.
name|MAX_VALUE
operator|-
literal|1
argument_list|,
comment|// test integer overflow
name|abs
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|,
comment|// positive random
operator|-
name|abs
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
return|;
comment|// negative random
block|}
block|}
end_class

end_unit

