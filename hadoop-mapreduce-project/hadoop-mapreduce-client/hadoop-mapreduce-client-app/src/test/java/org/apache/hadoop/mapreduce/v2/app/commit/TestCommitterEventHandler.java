begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.commit
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
name|commit
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|never
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
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentLinkedQueue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|JobContext
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
name|OutputCommitter
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
name|JobEvent
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
name|JobEventType
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
name|rm
operator|.
name|RMHeartbeatHandler
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
name|event
operator|.
name|AsyncDispatcher
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
name|event
operator|.
name|EventHandler
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
name|*
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
name|*
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
name|io
operator|.
name|FileUtils
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
name|FileSystem
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
name|job
operator|.
name|event
operator|.
name|JobCommitCompletedEvent
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
name|JobCommitFailedEvent
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
name|MRApps
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
name|security
operator|.
name|UserGroupInformation
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
name|conf
operator|.
name|YarnConfiguration
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
name|event
operator|.
name|Event
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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|Clock
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
name|SystemClock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
DECL|class|TestCommitterEventHandler
specifier|public
class|class
name|TestCommitterEventHandler
block|{
DECL|class|WaitForItHandler
specifier|public
specifier|static
class|class
name|WaitForItHandler
implements|implements
name|EventHandler
argument_list|<
name|Event
argument_list|>
block|{
DECL|field|event
specifier|private
name|Event
name|event
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|handle (Event event)
specifier|public
specifier|synchronized
name|void
name|handle
parameter_list|(
name|Event
name|event
parameter_list|)
block|{
name|this
operator|.
name|event
operator|=
name|event
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
DECL|method|getAndClearEvent ()
specifier|public
specifier|synchronized
name|Event
name|getAndClearEvent
parameter_list|()
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|event
operator|==
literal|null
condition|)
block|{
specifier|final
name|long
name|waitTime
init|=
literal|5000
decl_stmt|;
name|long
name|waitStartTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
while|while
condition|(
name|event
operator|==
literal|null
operator|&&
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|waitStartTime
operator|<
name|waitTime
condition|)
block|{
comment|//Wait for at most 5 sec
name|wait
argument_list|(
name|waitTime
argument_list|)
expr_stmt|;
block|}
block|}
name|Event
name|e
init|=
name|event
decl_stmt|;
name|event
operator|=
literal|null
expr_stmt|;
return|return
name|e
return|;
block|}
block|}
DECL|field|stagingDir
specifier|static
name|String
name|stagingDir
init|=
literal|"target/test-staging/"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|stagingDir
argument_list|)
decl_stmt|;
name|stagingDir
operator|=
name|dir
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|stagingDir
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
name|dir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommitWindow ()
specifier|public
name|void
name|testCommitWindow
parameter_list|()
throws|throws
name|Exception
block|{
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
name|stagingDir
argument_list|)
expr_stmt|;
name|AsyncDispatcher
name|dispatcher
init|=
operator|new
name|AsyncDispatcher
argument_list|()
decl_stmt|;
name|dispatcher
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|start
argument_list|()
expr_stmt|;
name|TestingJobEventHandler
name|jeh
init|=
operator|new
name|TestingJobEventHandler
argument_list|()
decl_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|JobEventType
operator|.
name|class
argument_list|,
name|jeh
argument_list|)
expr_stmt|;
name|SystemClock
name|clock
init|=
name|SystemClock
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|AppContext
name|appContext
init|=
name|mock
argument_list|(
name|AppContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|attemptid
init|=
name|ApplicationAttemptId
operator|.
name|fromString
argument_list|(
literal|"appattempt_1234567890000_0001_0"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|appContext
operator|.
name|getApplicationID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attemptid
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|appContext
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attemptid
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|appContext
operator|.
name|getEventHandler
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|appContext
operator|.
name|getClock
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|OutputCommitter
name|committer
init|=
name|mock
argument_list|(
name|OutputCommitter
operator|.
name|class
argument_list|)
decl_stmt|;
name|TestingRMHeartbeatHandler
name|rmhh
init|=
operator|new
name|TestingRMHeartbeatHandler
argument_list|()
decl_stmt|;
name|CommitterEventHandler
name|ceh
init|=
operator|new
name|CommitterEventHandler
argument_list|(
name|appContext
argument_list|,
name|committer
argument_list|,
name|rmhh
argument_list|)
decl_stmt|;
name|ceh
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|ceh
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// verify trying to commit when RM heartbeats are stale does not commit
name|ceh
operator|.
name|handle
argument_list|(
operator|new
name|CommitterJobCommitEvent
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|timeToWaitMs
init|=
literal|5000
decl_stmt|;
while|while
condition|(
name|rmhh
operator|.
name|getNumCallbacks
argument_list|()
operator|!=
literal|1
operator|&&
name|timeToWaitMs
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|timeToWaitMs
operator|-=
literal|10
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"committer did not register a heartbeat callback"
argument_list|,
literal|1
argument_list|,
name|rmhh
operator|.
name|getNumCallbacks
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|committer
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|commitJob
argument_list|(
name|any
argument_list|(
name|JobContext
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"committer should not have committed"
argument_list|,
literal|0
argument_list|,
name|jeh
operator|.
name|numCommitCompletedEvents
argument_list|)
expr_stmt|;
comment|// set a fresh heartbeat and verify commit completes
name|rmhh
operator|.
name|setLastHeartbeatTime
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|timeToWaitMs
operator|=
literal|5000
expr_stmt|;
while|while
condition|(
name|jeh
operator|.
name|numCommitCompletedEvents
operator|!=
literal|1
operator|&&
name|timeToWaitMs
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|timeToWaitMs
operator|-=
literal|10
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"committer did not complete commit after RM hearbeat"
argument_list|,
literal|1
argument_list|,
name|jeh
operator|.
name|numCommitCompletedEvents
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|committer
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|commitJob
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
comment|//Clean up so we can try to commit again (Don't do this at home)
name|cleanup
argument_list|()
expr_stmt|;
comment|// try to commit again and verify it goes through since the heartbeat
comment|// is still fresh
name|ceh
operator|.
name|handle
argument_list|(
operator|new
name|CommitterJobCommitEvent
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|timeToWaitMs
operator|=
literal|5000
expr_stmt|;
while|while
condition|(
name|jeh
operator|.
name|numCommitCompletedEvents
operator|!=
literal|2
operator|&&
name|timeToWaitMs
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|timeToWaitMs
operator|-=
literal|10
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"committer did not commit"
argument_list|,
literal|2
argument_list|,
name|jeh
operator|.
name|numCommitCompletedEvents
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|committer
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|commitJob
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|ceh
operator|.
name|stop
argument_list|()
expr_stmt|;
name|dispatcher
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|class|TestingRMHeartbeatHandler
specifier|private
specifier|static
class|class
name|TestingRMHeartbeatHandler
implements|implements
name|RMHeartbeatHandler
block|{
DECL|field|lastHeartbeatTime
specifier|private
name|long
name|lastHeartbeatTime
init|=
literal|0
decl_stmt|;
DECL|field|callbacks
specifier|private
name|ConcurrentLinkedQueue
argument_list|<
name|Runnable
argument_list|>
name|callbacks
init|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getLastHeartbeatTime ()
specifier|public
name|long
name|getLastHeartbeatTime
parameter_list|()
block|{
return|return
name|lastHeartbeatTime
return|;
block|}
annotation|@
name|Override
DECL|method|runOnNextHeartbeat (Runnable callback)
specifier|public
name|void
name|runOnNextHeartbeat
parameter_list|(
name|Runnable
name|callback
parameter_list|)
block|{
name|callbacks
operator|.
name|add
argument_list|(
name|callback
argument_list|)
expr_stmt|;
block|}
DECL|method|setLastHeartbeatTime (long timestamp)
specifier|public
name|void
name|setLastHeartbeatTime
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
name|lastHeartbeatTime
operator|=
name|timestamp
expr_stmt|;
name|Runnable
name|callback
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|callback
operator|=
name|callbacks
operator|.
name|poll
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|callback
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getNumCallbacks ()
specifier|public
name|int
name|getNumCallbacks
parameter_list|()
block|{
return|return
name|callbacks
operator|.
name|size
argument_list|()
return|;
block|}
block|}
DECL|class|TestingJobEventHandler
specifier|private
specifier|static
class|class
name|TestingJobEventHandler
implements|implements
name|EventHandler
argument_list|<
name|JobEvent
argument_list|>
block|{
DECL|field|numCommitCompletedEvents
name|int
name|numCommitCompletedEvents
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|handle (JobEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|JobEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|JobEventType
operator|.
name|JOB_COMMIT_COMPLETED
condition|)
block|{
operator|++
name|numCommitCompletedEvents
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testBasic ()
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
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
name|OutputCommitter
name|mockCommitter
init|=
name|mock
argument_list|(
name|OutputCommitter
operator|.
name|class
argument_list|)
decl_stmt|;
name|Clock
name|mockClock
init|=
name|mock
argument_list|(
name|Clock
operator|.
name|class
argument_list|)
decl_stmt|;
name|CommitterEventHandler
name|handler
init|=
operator|new
name|CommitterEventHandler
argument_list|(
name|mockContext
argument_list|,
name|mockCommitter
argument_list|,
operator|new
name|TestingRMHeartbeatHandler
argument_list|()
argument_list|)
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
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
name|stagingDir
argument_list|)
expr_stmt|;
name|JobContext
name|mockJobContext
init|=
name|mock
argument_list|(
name|JobContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|attemptid
init|=
name|ApplicationAttemptId
operator|.
name|fromString
argument_list|(
literal|"appattempt_1234567890000_0001_0"
argument_list|)
decl_stmt|;
name|JobId
name|jobId
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|attemptid
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|WaitForItHandler
name|waitForItHandler
init|=
operator|new
name|WaitForItHandler
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|mockContext
operator|.
name|getApplicationID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attemptid
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockContext
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attemptid
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockContext
operator|.
name|getEventHandler
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|waitForItHandler
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockContext
operator|.
name|getClock
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockClock
argument_list|)
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|handler
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|handler
operator|.
name|handle
argument_list|(
operator|new
name|CommitterJobCommitEvent
argument_list|(
name|jobId
argument_list|,
name|mockJobContext
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|Path
name|startCommitFile
init|=
name|MRApps
operator|.
name|getStartJobCommitFile
argument_list|(
name|conf
argument_list|,
name|user
argument_list|,
name|jobId
argument_list|)
decl_stmt|;
name|Path
name|endCommitSuccessFile
init|=
name|MRApps
operator|.
name|getEndJobCommitSuccessFile
argument_list|(
name|conf
argument_list|,
name|user
argument_list|,
name|jobId
argument_list|)
decl_stmt|;
name|Path
name|endCommitFailureFile
init|=
name|MRApps
operator|.
name|getEndJobCommitFailureFile
argument_list|(
name|conf
argument_list|,
name|user
argument_list|,
name|jobId
argument_list|)
decl_stmt|;
name|Event
name|e
init|=
name|waitForItHandler
operator|.
name|getAndClearEvent
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|JobCommitCompletedEvent
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|startCommitFile
operator|.
name|toString
argument_list|()
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|startCommitFile
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|endCommitSuccessFile
operator|.
name|toString
argument_list|()
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|endCommitSuccessFile
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|endCommitFailureFile
operator|.
name|toString
argument_list|()
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|endCommitFailureFile
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockCommitter
argument_list|)
operator|.
name|commitJob
argument_list|(
name|any
argument_list|(
name|JobContext
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|handler
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFailure ()
specifier|public
name|void
name|testFailure
parameter_list|()
throws|throws
name|Exception
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
name|OutputCommitter
name|mockCommitter
init|=
name|mock
argument_list|(
name|OutputCommitter
operator|.
name|class
argument_list|)
decl_stmt|;
name|Clock
name|mockClock
init|=
name|mock
argument_list|(
name|Clock
operator|.
name|class
argument_list|)
decl_stmt|;
name|CommitterEventHandler
name|handler
init|=
operator|new
name|CommitterEventHandler
argument_list|(
name|mockContext
argument_list|,
name|mockCommitter
argument_list|,
operator|new
name|TestingRMHeartbeatHandler
argument_list|()
argument_list|)
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
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
name|stagingDir
argument_list|)
expr_stmt|;
name|JobContext
name|mockJobContext
init|=
name|mock
argument_list|(
name|JobContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|attemptid
init|=
name|ApplicationAttemptId
operator|.
name|fromString
argument_list|(
literal|"appattempt_1234567890000_0001_0"
argument_list|)
decl_stmt|;
name|JobId
name|jobId
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|attemptid
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|WaitForItHandler
name|waitForItHandler
init|=
operator|new
name|WaitForItHandler
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|mockContext
operator|.
name|getApplicationID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attemptid
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockContext
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attemptid
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockContext
operator|.
name|getEventHandler
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|waitForItHandler
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockContext
operator|.
name|getClock
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockClock
argument_list|)
expr_stmt|;
name|doThrow
argument_list|(
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Intentional Failure"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|mockCommitter
argument_list|)
operator|.
name|commitJob
argument_list|(
name|any
argument_list|(
name|JobContext
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|handler
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|handler
operator|.
name|handle
argument_list|(
operator|new
name|CommitterJobCommitEvent
argument_list|(
name|jobId
argument_list|,
name|mockJobContext
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|Path
name|startCommitFile
init|=
name|MRApps
operator|.
name|getStartJobCommitFile
argument_list|(
name|conf
argument_list|,
name|user
argument_list|,
name|jobId
argument_list|)
decl_stmt|;
name|Path
name|endCommitSuccessFile
init|=
name|MRApps
operator|.
name|getEndJobCommitSuccessFile
argument_list|(
name|conf
argument_list|,
name|user
argument_list|,
name|jobId
argument_list|)
decl_stmt|;
name|Path
name|endCommitFailureFile
init|=
name|MRApps
operator|.
name|getEndJobCommitFailureFile
argument_list|(
name|conf
argument_list|,
name|user
argument_list|,
name|jobId
argument_list|)
decl_stmt|;
name|Event
name|e
init|=
name|waitForItHandler
operator|.
name|getAndClearEvent
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|JobCommitFailedEvent
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|startCommitFile
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|endCommitSuccessFile
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|endCommitFailureFile
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockCommitter
argument_list|)
operator|.
name|commitJob
argument_list|(
name|any
argument_list|(
name|JobContext
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|handler
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

