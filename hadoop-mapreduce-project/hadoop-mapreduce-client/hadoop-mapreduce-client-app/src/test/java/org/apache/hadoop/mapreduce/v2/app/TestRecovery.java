begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
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
name|jobhistory
operator|.
name|JobHistoryEvent
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
name|jobhistory
operator|.
name|JobHistoryEventHandler
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
name|jobhistory
operator|.
name|JobHistoryParser
operator|.
name|AMInfo
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
name|JobState
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
name|TaskAttemptState
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
name|TaskState
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
name|app
operator|.
name|job
operator|.
name|Task
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
name|TaskAttempt
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
name|TaskAttemptEvent
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
name|TaskAttemptEventType
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
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestRecovery
specifier|public
class|class
name|TestRecovery
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
name|TestRecovery
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testCrashed ()
specifier|public
name|void
name|testCrashed
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|runCount
init|=
literal|0
decl_stmt|;
name|long
name|am1StartTimeEst
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|MRApp
name|app
init|=
operator|new
name|MRAppWithHistory
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|true
argument_list|,
operator|++
name|runCount
argument_list|)
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
name|setBoolean
argument_list|(
name|MRJobConfig
operator|.
name|JOB_UBERTASK_ENABLE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Job
name|job
init|=
name|app
operator|.
name|submit
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|job
argument_list|,
name|JobState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|long
name|jobStartTime
init|=
name|job
operator|.
name|getReport
argument_list|()
operator|.
name|getStartTime
argument_list|()
decl_stmt|;
comment|//all maps would be running
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"No of tasks not correct"
argument_list|,
literal|3
argument_list|,
name|job
operator|.
name|getTasks
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Task
argument_list|>
name|it
init|=
name|job
operator|.
name|getTasks
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Task
name|mapTask1
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|Task
name|mapTask2
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|Task
name|reduceTask
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// all maps must be running
name|app
operator|.
name|waitForState
argument_list|(
name|mapTask1
argument_list|,
name|TaskState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|mapTask2
argument_list|,
name|TaskState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|TaskAttempt
name|task1Attempt1
init|=
name|mapTask1
operator|.
name|getAttempts
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|TaskAttempt
name|task2Attempt
init|=
name|mapTask2
operator|.
name|getAttempts
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
comment|//before sending the TA_DONE, event make sure attempt has come to
comment|//RUNNING state
name|app
operator|.
name|waitForState
argument_list|(
name|task1Attempt1
argument_list|,
name|TaskAttemptState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|task2Attempt
argument_list|,
name|TaskAttemptState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|// reduces must be in NEW state
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Reduce Task state not correct"
argument_list|,
name|TaskState
operator|.
name|RUNNING
argument_list|,
name|reduceTask
operator|.
name|getReport
argument_list|()
operator|.
name|getTaskState
argument_list|()
argument_list|)
expr_stmt|;
comment|//send the fail signal to the 1st map task attempt
name|app
operator|.
name|getContext
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|task1Attempt1
operator|.
name|getID
argument_list|()
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_FAILMSG
argument_list|)
argument_list|)
expr_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|task1Attempt1
argument_list|,
name|TaskAttemptState
operator|.
name|FAILED
argument_list|)
expr_stmt|;
while|while
condition|(
name|mapTask1
operator|.
name|getAttempts
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
literal|2
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for next attempt to start"
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|TaskAttempt
argument_list|>
name|itr
init|=
name|mapTask1
operator|.
name|getAttempts
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|itr
operator|.
name|next
argument_list|()
expr_stmt|;
name|TaskAttempt
name|task1Attempt2
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|task1Attempt2
argument_list|,
name|TaskAttemptState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|//send the kill signal to the 1st map 2nd attempt
name|app
operator|.
name|getContext
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|task1Attempt2
operator|.
name|getID
argument_list|()
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_KILL
argument_list|)
argument_list|)
expr_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|task1Attempt2
argument_list|,
name|TaskAttemptState
operator|.
name|KILLED
argument_list|)
expr_stmt|;
while|while
condition|(
name|mapTask1
operator|.
name|getAttempts
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
literal|3
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for next attempt to start"
argument_list|)
expr_stmt|;
block|}
name|itr
operator|=
name|mapTask1
operator|.
name|getAttempts
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|itr
operator|.
name|next
argument_list|()
expr_stmt|;
name|itr
operator|.
name|next
argument_list|()
expr_stmt|;
name|TaskAttempt
name|task1Attempt3
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|task1Attempt3
argument_list|,
name|TaskAttemptState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|//send the done signal to the 1st map 3rd attempt
name|app
operator|.
name|getContext
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|task1Attempt3
operator|.
name|getID
argument_list|()
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_DONE
argument_list|)
argument_list|)
expr_stmt|;
comment|//wait for first map task to complete
name|app
operator|.
name|waitForState
argument_list|(
name|mapTask1
argument_list|,
name|TaskState
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
name|long
name|task1StartTime
init|=
name|mapTask1
operator|.
name|getReport
argument_list|()
operator|.
name|getStartTime
argument_list|()
decl_stmt|;
name|long
name|task1FinishTime
init|=
name|mapTask1
operator|.
name|getReport
argument_list|()
operator|.
name|getFinishTime
argument_list|()
decl_stmt|;
comment|//stop the app
name|app
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|//rerun
comment|//in rerun the 1st map will be recovered from previous run
name|long
name|am2StartTimeEst
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|app
operator|=
operator|new
name|MRAppWithHistory
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|false
argument_list|,
operator|++
name|runCount
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_JOB_RECOVERY_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|MRJobConfig
operator|.
name|JOB_UBERTASK_ENABLE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|job
operator|=
name|app
operator|.
name|submit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|job
argument_list|,
name|JobState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|//all maps would be running
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"No of tasks not correct"
argument_list|,
literal|3
argument_list|,
name|job
operator|.
name|getTasks
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|it
operator|=
name|job
operator|.
name|getTasks
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|mapTask1
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|mapTask2
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|reduceTask
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// first map will be recovered, no need to send done
name|app
operator|.
name|waitForState
argument_list|(
name|mapTask1
argument_list|,
name|TaskState
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|mapTask2
argument_list|,
name|TaskState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|task2Attempt
operator|=
name|mapTask2
operator|.
name|getAttempts
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
comment|//before sending the TA_DONE, event make sure attempt has come to
comment|//RUNNING state
name|app
operator|.
name|waitForState
argument_list|(
name|task2Attempt
argument_list|,
name|TaskAttemptState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|//send the done signal to the 2nd map task
name|app
operator|.
name|getContext
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|mapTask2
operator|.
name|getAttempts
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_DONE
argument_list|)
argument_list|)
expr_stmt|;
comment|//wait to get it completed
name|app
operator|.
name|waitForState
argument_list|(
name|mapTask2
argument_list|,
name|TaskState
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
comment|//wait for reduce to be running before sending done
name|app
operator|.
name|waitForState
argument_list|(
name|reduceTask
argument_list|,
name|TaskState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|//send the done signal to the reduce
name|app
operator|.
name|getContext
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|reduceTask
operator|.
name|getAttempts
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_DONE
argument_list|)
argument_list|)
expr_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|job
argument_list|,
name|JobState
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
name|app
operator|.
name|verifyCompleted
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Job Start time not correct"
argument_list|,
name|jobStartTime
argument_list|,
name|job
operator|.
name|getReport
argument_list|()
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Task Start time not correct"
argument_list|,
name|task1StartTime
argument_list|,
name|mapTask1
operator|.
name|getReport
argument_list|()
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Task Finish time not correct"
argument_list|,
name|task1FinishTime
argument_list|,
name|mapTask1
operator|.
name|getReport
argument_list|()
operator|.
name|getFinishTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|job
operator|.
name|getAMInfos
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|attemptNum
init|=
literal|1
decl_stmt|;
comment|// Verify AMInfo
for|for
control|(
name|AMInfo
name|amInfo
range|:
name|job
operator|.
name|getAMInfos
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|attemptNum
operator|++
argument_list|,
name|amInfo
operator|.
name|getAppAttemptId
argument_list|()
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|amInfo
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|amInfo
operator|.
name|getContainerId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"testhost"
argument_list|,
name|amInfo
operator|.
name|getNodeManagerHost
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3333
argument_list|,
name|amInfo
operator|.
name|getNodeManagerHttpPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|long
name|am1StartTimeReal
init|=
name|job
operator|.
name|getAMInfos
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStartTime
argument_list|()
decl_stmt|;
name|long
name|am2StartTimeReal
init|=
name|job
operator|.
name|getAMInfos
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getStartTime
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|am1StartTimeReal
operator|>=
name|am1StartTimeEst
operator|&&
name|am1StartTimeReal
operator|<=
name|am2StartTimeEst
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|am2StartTimeReal
operator|>=
name|am2StartTimeEst
operator|&&
name|am2StartTimeReal
operator|<=
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO Add verification of additional data from jobHistory - whatever was
comment|// available in the failed attempt should be available here
block|}
DECL|class|MRAppWithHistory
class|class
name|MRAppWithHistory
extends|extends
name|MRApp
block|{
DECL|method|MRAppWithHistory (int maps, int reduces, boolean autoComplete, String testName, boolean cleanOnStart, int startCount)
specifier|public
name|MRAppWithHistory
parameter_list|(
name|int
name|maps
parameter_list|,
name|int
name|reduces
parameter_list|,
name|boolean
name|autoComplete
parameter_list|,
name|String
name|testName
parameter_list|,
name|boolean
name|cleanOnStart
parameter_list|,
name|int
name|startCount
parameter_list|)
block|{
name|super
argument_list|(
name|maps
argument_list|,
name|reduces
argument_list|,
name|autoComplete
argument_list|,
name|testName
argument_list|,
name|cleanOnStart
argument_list|,
name|startCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createJobHistoryHandler ( AppContext context)
specifier|protected
name|EventHandler
argument_list|<
name|JobHistoryEvent
argument_list|>
name|createJobHistoryHandler
parameter_list|(
name|AppContext
name|context
parameter_list|)
block|{
name|JobHistoryEventHandler
name|eventHandler
init|=
operator|new
name|JobHistoryEventHandler
argument_list|(
name|context
argument_list|,
name|getStartCount
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|eventHandler
return|;
block|}
block|}
DECL|method|main (String[] arg)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|arg
parameter_list|)
throws|throws
name|Exception
block|{
name|TestRecovery
name|test
init|=
operator|new
name|TestRecovery
argument_list|()
decl_stmt|;
name|test
operator|.
name|testCrashed
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

