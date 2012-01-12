begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.jobhistory
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|jobhistory
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
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
name|mock
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
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
name|verify
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
name|when
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|fs
operator|.
name|FileContext
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
name|fs
operator|.
name|Path
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
name|Counters
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
name|MRJobConfig
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
name|TaskID
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
name|TaskType
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
name|TypeConverter
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
name|JobId
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
name|Job
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
name|util
operator|.
name|MRBuilderUtils
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
name|yarn
operator|.
name|YarnException
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|yarn
operator|.
name|util
operator|.
name|BuilderUtils
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

begin_class
DECL|class|TestJobHistoryEventHandler
specifier|public
class|class
name|TestJobHistoryEventHandler
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestJobHistoryEventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testFirstFlushOnCompletionEvent ()
specifier|public
name|void
name|testFirstFlushOnCompletionEvent
parameter_list|()
throws|throws
name|Exception
block|{
name|TestParams
name|t
init|=
operator|new
name|TestParams
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_STAGING_DIR
argument_list|,
name|t
operator|.
name|workDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_HISTORY_COMPLETE_EVENT_FLUSH_TIMEOUT_MS
argument_list|,
literal|60
operator|*
literal|1000l
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_HISTORY_JOB_COMPLETE_UNFLUSHED_MULTIPLIER
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_HISTORY_MAX_UNFLUSHED_COMPLETE_EVENTS
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_HISTORY_USE_BATCHED_FLUSH_QUEUE_SIZE_THRESHOLD
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|JHEvenHandlerForTest
name|realJheh
init|=
operator|new
name|JHEvenHandlerForTest
argument_list|(
name|t
operator|.
name|mockAppContext
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|JHEvenHandlerForTest
name|jheh
init|=
name|spy
argument_list|(
name|realJheh
argument_list|)
decl_stmt|;
name|jheh
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|EventWriter
name|mockWriter
init|=
literal|null
decl_stmt|;
try|try
block|{
name|jheh
operator|.
name|start
argument_list|()
expr_stmt|;
name|handleEvent
argument_list|(
name|jheh
argument_list|,
operator|new
name|JobHistoryEvent
argument_list|(
name|t
operator|.
name|jobId
argument_list|,
operator|new
name|AMStartedEvent
argument_list|(
name|t
operator|.
name|appAttemptId
argument_list|,
literal|200
argument_list|,
name|t
operator|.
name|containerId
argument_list|,
literal|"nmhost"
argument_list|,
literal|3000
argument_list|,
literal|4000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mockWriter
operator|=
name|jheh
operator|.
name|getEventWriter
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockWriter
argument_list|)
operator|.
name|write
argument_list|(
name|any
argument_list|(
name|HistoryEvent
operator|.
name|class
argument_list|)
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|queueEvent
argument_list|(
name|jheh
argument_list|,
operator|new
name|JobHistoryEvent
argument_list|(
name|t
operator|.
name|jobId
argument_list|,
operator|new
name|TaskStartedEvent
argument_list|(
name|t
operator|.
name|taskID
argument_list|,
literal|0
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|handleNextNEvents
argument_list|(
name|jheh
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockWriter
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// First completion event, but min-queue-size for batching flushes is 10
name|handleEvent
argument_list|(
name|jheh
argument_list|,
operator|new
name|JobHistoryEvent
argument_list|(
name|t
operator|.
name|jobId
argument_list|,
operator|new
name|TaskFinishedEvent
argument_list|(
name|t
operator|.
name|taskID
argument_list|,
literal|0
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockWriter
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|jheh
operator|.
name|stop
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockWriter
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMaxUnflushedCompletionEvents ()
specifier|public
name|void
name|testMaxUnflushedCompletionEvents
parameter_list|()
throws|throws
name|Exception
block|{
name|TestParams
name|t
init|=
operator|new
name|TestParams
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_STAGING_DIR
argument_list|,
name|t
operator|.
name|workDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_HISTORY_COMPLETE_EVENT_FLUSH_TIMEOUT_MS
argument_list|,
literal|60
operator|*
literal|1000l
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_HISTORY_JOB_COMPLETE_UNFLUSHED_MULTIPLIER
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_HISTORY_MAX_UNFLUSHED_COMPLETE_EVENTS
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_HISTORY_USE_BATCHED_FLUSH_QUEUE_SIZE_THRESHOLD
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|JHEvenHandlerForTest
name|realJheh
init|=
operator|new
name|JHEvenHandlerForTest
argument_list|(
name|t
operator|.
name|mockAppContext
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|JHEvenHandlerForTest
name|jheh
init|=
name|spy
argument_list|(
name|realJheh
argument_list|)
decl_stmt|;
name|jheh
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|EventWriter
name|mockWriter
init|=
literal|null
decl_stmt|;
try|try
block|{
name|jheh
operator|.
name|start
argument_list|()
expr_stmt|;
name|handleEvent
argument_list|(
name|jheh
argument_list|,
operator|new
name|JobHistoryEvent
argument_list|(
name|t
operator|.
name|jobId
argument_list|,
operator|new
name|AMStartedEvent
argument_list|(
name|t
operator|.
name|appAttemptId
argument_list|,
literal|200
argument_list|,
name|t
operator|.
name|containerId
argument_list|,
literal|"nmhost"
argument_list|,
literal|3000
argument_list|,
literal|4000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mockWriter
operator|=
name|jheh
operator|.
name|getEventWriter
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockWriter
argument_list|)
operator|.
name|write
argument_list|(
name|any
argument_list|(
name|HistoryEvent
operator|.
name|class
argument_list|)
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|queueEvent
argument_list|(
name|jheh
argument_list|,
operator|new
name|JobHistoryEvent
argument_list|(
name|t
operator|.
name|jobId
argument_list|,
operator|new
name|TaskFinishedEvent
argument_list|(
name|t
operator|.
name|taskID
argument_list|,
literal|0
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|handleNextNEvents
argument_list|(
name|jheh
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockWriter
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
name|handleNextNEvents
argument_list|(
name|jheh
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockWriter
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
name|handleNextNEvents
argument_list|(
name|jheh
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockWriter
argument_list|,
name|times
argument_list|(
literal|6
argument_list|)
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|jheh
operator|.
name|stop
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockWriter
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUnflushedTimer ()
specifier|public
name|void
name|testUnflushedTimer
parameter_list|()
throws|throws
name|Exception
block|{
name|TestParams
name|t
init|=
operator|new
name|TestParams
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_STAGING_DIR
argument_list|,
name|t
operator|.
name|workDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_HISTORY_COMPLETE_EVENT_FLUSH_TIMEOUT_MS
argument_list|,
literal|2
operator|*
literal|1000l
argument_list|)
expr_stmt|;
comment|//2 seconds.
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_HISTORY_JOB_COMPLETE_UNFLUSHED_MULTIPLIER
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_HISTORY_MAX_UNFLUSHED_COMPLETE_EVENTS
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_HISTORY_USE_BATCHED_FLUSH_QUEUE_SIZE_THRESHOLD
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|JHEvenHandlerForTest
name|realJheh
init|=
operator|new
name|JHEvenHandlerForTest
argument_list|(
name|t
operator|.
name|mockAppContext
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|JHEvenHandlerForTest
name|jheh
init|=
name|spy
argument_list|(
name|realJheh
argument_list|)
decl_stmt|;
name|jheh
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|EventWriter
name|mockWriter
init|=
literal|null
decl_stmt|;
try|try
block|{
name|jheh
operator|.
name|start
argument_list|()
expr_stmt|;
name|handleEvent
argument_list|(
name|jheh
argument_list|,
operator|new
name|JobHistoryEvent
argument_list|(
name|t
operator|.
name|jobId
argument_list|,
operator|new
name|AMStartedEvent
argument_list|(
name|t
operator|.
name|appAttemptId
argument_list|,
literal|200
argument_list|,
name|t
operator|.
name|containerId
argument_list|,
literal|"nmhost"
argument_list|,
literal|3000
argument_list|,
literal|4000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mockWriter
operator|=
name|jheh
operator|.
name|getEventWriter
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockWriter
argument_list|)
operator|.
name|write
argument_list|(
name|any
argument_list|(
name|HistoryEvent
operator|.
name|class
argument_list|)
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|queueEvent
argument_list|(
name|jheh
argument_list|,
operator|new
name|JobHistoryEvent
argument_list|(
name|t
operator|.
name|jobId
argument_list|,
operator|new
name|TaskFinishedEvent
argument_list|(
name|t
operator|.
name|taskID
argument_list|,
literal|0
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|handleNextNEvents
argument_list|(
name|jheh
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockWriter
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2
operator|*
literal|4
operator|*
literal|1000l
argument_list|)
expr_stmt|;
comment|// 4 seconds should be enough. Just be safe.
name|verify
argument_list|(
name|mockWriter
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|jheh
operator|.
name|stop
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockWriter
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBatchedFlushJobEndMultiplier ()
specifier|public
name|void
name|testBatchedFlushJobEndMultiplier
parameter_list|()
throws|throws
name|Exception
block|{
name|TestParams
name|t
init|=
operator|new
name|TestParams
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_STAGING_DIR
argument_list|,
name|t
operator|.
name|workDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_HISTORY_COMPLETE_EVENT_FLUSH_TIMEOUT_MS
argument_list|,
literal|60
operator|*
literal|1000l
argument_list|)
expr_stmt|;
comment|//2 seconds.
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_HISTORY_JOB_COMPLETE_UNFLUSHED_MULTIPLIER
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_HISTORY_MAX_UNFLUSHED_COMPLETE_EVENTS
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_HISTORY_USE_BATCHED_FLUSH_QUEUE_SIZE_THRESHOLD
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|JHEvenHandlerForTest
name|realJheh
init|=
operator|new
name|JHEvenHandlerForTest
argument_list|(
name|t
operator|.
name|mockAppContext
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|JHEvenHandlerForTest
name|jheh
init|=
name|spy
argument_list|(
name|realJheh
argument_list|)
decl_stmt|;
name|jheh
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|EventWriter
name|mockWriter
init|=
literal|null
decl_stmt|;
try|try
block|{
name|jheh
operator|.
name|start
argument_list|()
expr_stmt|;
name|handleEvent
argument_list|(
name|jheh
argument_list|,
operator|new
name|JobHistoryEvent
argument_list|(
name|t
operator|.
name|jobId
argument_list|,
operator|new
name|AMStartedEvent
argument_list|(
name|t
operator|.
name|appAttemptId
argument_list|,
literal|200
argument_list|,
name|t
operator|.
name|containerId
argument_list|,
literal|"nmhost"
argument_list|,
literal|3000
argument_list|,
literal|4000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mockWriter
operator|=
name|jheh
operator|.
name|getEventWriter
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockWriter
argument_list|)
operator|.
name|write
argument_list|(
name|any
argument_list|(
name|HistoryEvent
operator|.
name|class
argument_list|)
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|queueEvent
argument_list|(
name|jheh
argument_list|,
operator|new
name|JobHistoryEvent
argument_list|(
name|t
operator|.
name|jobId
argument_list|,
operator|new
name|TaskFinishedEvent
argument_list|(
name|t
operator|.
name|taskID
argument_list|,
literal|0
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|queueEvent
argument_list|(
name|jheh
argument_list|,
operator|new
name|JobHistoryEvent
argument_list|(
name|t
operator|.
name|jobId
argument_list|,
operator|new
name|JobFinishedEvent
argument_list|(
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|t
operator|.
name|jobId
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|Counters
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|handleNextNEvents
argument_list|(
name|jheh
argument_list|,
literal|29
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockWriter
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
name|handleNextNEvents
argument_list|(
name|jheh
argument_list|,
literal|72
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockWriter
argument_list|,
name|times
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|//3 * 30 + 1 for JobFinished
block|}
finally|finally
block|{
name|jheh
operator|.
name|stop
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockWriter
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|queueEvent (JHEvenHandlerForTest jheh, JobHistoryEvent event)
specifier|private
name|void
name|queueEvent
parameter_list|(
name|JHEvenHandlerForTest
name|jheh
parameter_list|,
name|JobHistoryEvent
name|event
parameter_list|)
block|{
name|jheh
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
DECL|method|handleEvent (JHEvenHandlerForTest jheh, JobHistoryEvent event)
specifier|private
name|void
name|handleEvent
parameter_list|(
name|JHEvenHandlerForTest
name|jheh
parameter_list|,
name|JobHistoryEvent
name|event
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|jheh
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|jheh
operator|.
name|handleEvent
argument_list|(
name|jheh
operator|.
name|eventQueue
operator|.
name|take
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|handleNextNEvents (JHEvenHandlerForTest jheh, int numEvents)
specifier|private
name|void
name|handleNextNEvents
parameter_list|(
name|JHEvenHandlerForTest
name|jheh
parameter_list|,
name|int
name|numEvents
parameter_list|)
throws|throws
name|InterruptedException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numEvents
condition|;
name|i
operator|++
control|)
block|{
name|jheh
operator|.
name|handleEvent
argument_list|(
name|jheh
operator|.
name|eventQueue
operator|.
name|take
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setupTestWorkDir ()
specifier|private
name|String
name|setupTestWorkDir
parameter_list|()
block|{
name|File
name|testWorkDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|testWorkDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|testWorkDir
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not cleanup"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"could not cleanup test dir"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|mockAppContext (JobId jobId)
specifier|private
name|AppContext
name|mockAppContext
parameter_list|(
name|JobId
name|jobId
parameter_list|)
block|{
name|AppContext
name|mockContext
init|=
name|mock
argument_list|(
name|AppContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|Job
name|mockJob
init|=
name|mock
argument_list|(
name|Job
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|getTotalMaps
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|getTotalReduces
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"mockjob"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockContext
operator|.
name|getJob
argument_list|(
name|jobId
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockJob
argument_list|)
expr_stmt|;
return|return
name|mockContext
return|;
block|}
DECL|class|TestParams
specifier|private
class|class
name|TestParams
block|{
DECL|field|workDir
name|String
name|workDir
init|=
name|setupTestWorkDir
argument_list|()
decl_stmt|;
DECL|field|appId
name|ApplicationId
name|appId
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|200
argument_list|,
literal|1
argument_list|)
decl_stmt|;
DECL|field|appAttemptId
name|ApplicationAttemptId
name|appAttemptId
init|=
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
DECL|field|containerId
name|ContainerId
name|containerId
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
DECL|field|taskID
name|TaskID
name|taskID
init|=
name|TaskID
operator|.
name|forName
argument_list|(
literal|"task_200707121733_0003_m_000005"
argument_list|)
decl_stmt|;
DECL|field|jobId
name|JobId
name|jobId
init|=
name|MRBuilderUtils
operator|.
name|newJobId
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
DECL|field|mockAppContext
name|AppContext
name|mockAppContext
init|=
name|mockAppContext
argument_list|(
name|jobId
argument_list|)
decl_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|JHEvenHandlerForTest
class|class
name|JHEvenHandlerForTest
extends|extends
name|JobHistoryEventHandler
block|{
DECL|field|eventWriter
specifier|private
name|EventWriter
name|eventWriter
decl_stmt|;
DECL|field|handleEventCompleteCalls
specifier|volatile
name|int
name|handleEventCompleteCalls
init|=
literal|0
decl_stmt|;
DECL|field|handleEventStartedCalls
specifier|volatile
name|int
name|handleEventStartedCalls
init|=
literal|0
decl_stmt|;
DECL|method|JHEvenHandlerForTest (AppContext context, int startCount)
specifier|public
name|JHEvenHandlerForTest
parameter_list|(
name|AppContext
name|context
parameter_list|,
name|int
name|startCount
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|startCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|createEventWriter (Path historyFilePath)
specifier|protected
name|EventWriter
name|createEventWriter
parameter_list|(
name|Path
name|historyFilePath
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|eventWriter
operator|=
name|mock
argument_list|(
name|EventWriter
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|eventWriter
return|;
block|}
annotation|@
name|Override
DECL|method|closeEventWriter (JobId jobId)
specifier|protected
name|void
name|closeEventWriter
parameter_list|(
name|JobId
name|jobId
parameter_list|)
block|{   }
DECL|method|getEventWriter ()
specifier|public
name|EventWriter
name|getEventWriter
parameter_list|()
block|{
return|return
name|this
operator|.
name|eventWriter
return|;
block|}
block|}
end_class

end_unit

