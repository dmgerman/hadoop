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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Map
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
name|mapred
operator|.
name|TaskAttemptListenerImpl
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
name|TaskAttemptId
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
name|TaskId
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
name|TaskAttemptStateInternal
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|impl
operator|.
name|TaskAttemptImpl
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
name|launcher
operator|.
name|ContainerLauncher
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
name|launcher
operator|.
name|ContainerLauncherEvent
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
name|launcher
operator|.
name|ContainerLauncherImpl
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
name|net
operator|.
name|NetUtils
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
name|ContainerManager
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
name|api
operator|.
name|records
operator|.
name|Token
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

begin_comment
comment|/**  * Tests the state machine with respect to Job/Task/TaskAttempt failure   * scenarios.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|TestFail
specifier|public
class|class
name|TestFail
block|{
annotation|@
name|Test
comment|//First attempt is failed and second attempt is passed
comment|//The job succeeds.
DECL|method|testFailTask ()
specifier|public
name|void
name|testFailTask
parameter_list|()
throws|throws
name|Exception
block|{
name|MRApp
name|app
init|=
operator|new
name|MockFirstFailingAttemptMRApp
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// this test requires two task attempts, but uberization overrides max to 1
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
name|SUCCEEDED
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|tasks
init|=
name|job
operator|.
name|getTasks
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Num tasks is not correct"
argument_list|,
literal|1
argument_list|,
name|tasks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Task
name|task
init|=
name|tasks
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
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Task state not correct"
argument_list|,
name|TaskState
operator|.
name|SUCCEEDED
argument_list|,
name|task
operator|.
name|getReport
argument_list|()
operator|.
name|getTaskState
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|TaskAttemptId
argument_list|,
name|TaskAttempt
argument_list|>
name|attempts
init|=
name|tasks
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
name|getAttempts
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Num attempts is not correct"
argument_list|,
literal|2
argument_list|,
name|attempts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//one attempt must be failed
comment|//and another must have succeeded
name|Iterator
argument_list|<
name|TaskAttempt
argument_list|>
name|it
init|=
name|attempts
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Attempt state not correct"
argument_list|,
name|TaskAttemptState
operator|.
name|FAILED
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|.
name|getReport
argument_list|()
operator|.
name|getTaskAttemptState
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Attempt state not correct"
argument_list|,
name|TaskAttemptState
operator|.
name|SUCCEEDED
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|.
name|getReport
argument_list|()
operator|.
name|getTaskAttemptState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMapFailureMaxPercent ()
specifier|public
name|void
name|testMapFailureMaxPercent
parameter_list|()
throws|throws
name|Exception
block|{
name|MRApp
name|app
init|=
operator|new
name|MockFirstFailingTaskMRApp
argument_list|(
literal|4
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|//reduce the no of attempts so test run faster
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MAP_MAX_ATTEMPTS
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|REDUCE_MAX_ATTEMPTS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MAP_FAILURES_MAX_PERCENT
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MAP_MAX_ATTEMPTS
argument_list|,
literal|1
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
name|FAILED
argument_list|)
expr_stmt|;
comment|//setting the failure percentage to 25% (1/4 is 25) will
comment|//make the Job successful
name|app
operator|=
operator|new
name|MockFirstFailingTaskMRApp
argument_list|(
literal|4
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
comment|//reduce the no of attempts so test run faster
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MAP_MAX_ATTEMPTS
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|REDUCE_MAX_ATTEMPTS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MAP_FAILURES_MAX_PERCENT
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MAP_MAX_ATTEMPTS
argument_list|,
literal|1
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
name|SUCCEEDED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReduceFailureMaxPercent ()
specifier|public
name|void
name|testReduceFailureMaxPercent
parameter_list|()
throws|throws
name|Exception
block|{
name|MRApp
name|app
init|=
operator|new
name|MockFirstFailingTaskMRApp
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|//reduce the no of attempts so test run faster
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MAP_MAX_ATTEMPTS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|REDUCE_MAX_ATTEMPTS
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MAP_FAILURES_MAX_PERCENT
argument_list|,
literal|50
argument_list|)
expr_stmt|;
comment|//no failure due to Map
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MAP_MAX_ATTEMPTS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|REDUCE_FAILURES_MAXPERCENT
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|REDUCE_MAX_ATTEMPTS
argument_list|,
literal|1
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
name|FAILED
argument_list|)
expr_stmt|;
comment|//setting the failure percentage to 25% (1/4 is 25) will
comment|//make the Job successful
name|app
operator|=
operator|new
name|MockFirstFailingTaskMRApp
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
comment|//reduce the no of attempts so test run faster
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MAP_MAX_ATTEMPTS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|REDUCE_MAX_ATTEMPTS
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MAP_FAILURES_MAX_PERCENT
argument_list|,
literal|50
argument_list|)
expr_stmt|;
comment|//no failure due to Map
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MAP_MAX_ATTEMPTS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|REDUCE_FAILURES_MAXPERCENT
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|REDUCE_MAX_ATTEMPTS
argument_list|,
literal|1
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
name|SUCCEEDED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|//All Task attempts are timed out, leading to Job failure
DECL|method|testTimedOutTask ()
specifier|public
name|void
name|testTimedOutTask
parameter_list|()
throws|throws
name|Exception
block|{
name|MRApp
name|app
init|=
operator|new
name|TimeOutTaskMRApp
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|int
name|maxAttempts
init|=
literal|2
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MAP_MAX_ATTEMPTS
argument_list|,
name|maxAttempts
argument_list|)
expr_stmt|;
comment|// disable uberization (requires entire job to be reattempted, so max for
comment|// subtask attempts is overridden to 1)
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
name|FAILED
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|tasks
init|=
name|job
operator|.
name|getTasks
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Num tasks is not correct"
argument_list|,
literal|1
argument_list|,
name|tasks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Task
name|task
init|=
name|tasks
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
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Task state not correct"
argument_list|,
name|TaskState
operator|.
name|FAILED
argument_list|,
name|task
operator|.
name|getReport
argument_list|()
operator|.
name|getTaskState
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|TaskAttemptId
argument_list|,
name|TaskAttempt
argument_list|>
name|attempts
init|=
name|tasks
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
name|getAttempts
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Num attempts is not correct"
argument_list|,
name|maxAttempts
argument_list|,
name|attempts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|TaskAttempt
name|attempt
range|:
name|attempts
operator|.
name|values
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Attempt state not correct"
argument_list|,
name|TaskAttemptState
operator|.
name|FAILED
argument_list|,
name|attempt
operator|.
name|getReport
argument_list|()
operator|.
name|getTaskAttemptState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testTaskFailWithUnusedContainer ()
specifier|public
name|void
name|testTaskFailWithUnusedContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|MRApp
name|app
init|=
operator|new
name|MRAppWithFailingTaskAndUnusedContainer
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|int
name|maxAttempts
init|=
literal|1
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MAP_MAX_ATTEMPTS
argument_list|,
name|maxAttempts
argument_list|)
expr_stmt|;
comment|// disable uberization (requires entire job to be reattempted, so max for
comment|// subtask attempts is overridden to 1)
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
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|tasks
init|=
name|job
operator|.
name|getTasks
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Num tasks is not correct"
argument_list|,
literal|1
argument_list|,
name|tasks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Task
name|task
init|=
name|tasks
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
name|app
operator|.
name|waitForState
argument_list|(
name|task
argument_list|,
name|TaskState
operator|.
name|SCHEDULED
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|TaskAttemptId
argument_list|,
name|TaskAttempt
argument_list|>
name|attempts
init|=
name|tasks
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
name|getAttempts
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Num attempts is not correct"
argument_list|,
name|maxAttempts
argument_list|,
name|attempts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TaskAttempt
name|attempt
init|=
name|attempts
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
name|app
operator|.
name|waitForInternalState
argument_list|(
operator|(
name|TaskAttemptImpl
operator|)
name|attempt
argument_list|,
name|TaskAttemptStateInternal
operator|.
name|ASSIGNED
argument_list|)
expr_stmt|;
name|app
operator|.
name|getDispatcher
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
name|attempt
operator|.
name|getID
argument_list|()
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_CONTAINER_COMPLETED
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
name|FAILED
argument_list|)
expr_stmt|;
block|}
DECL|class|MRAppWithFailingTaskAndUnusedContainer
specifier|static
class|class
name|MRAppWithFailingTaskAndUnusedContainer
extends|extends
name|MRApp
block|{
DECL|method|MRAppWithFailingTaskAndUnusedContainer ()
specifier|public
name|MRAppWithFailingTaskAndUnusedContainer
parameter_list|()
block|{
name|super
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|"TaskFailWithUnsedContainer"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createContainerLauncher (AppContext context)
specifier|protected
name|ContainerLauncher
name|createContainerLauncher
parameter_list|(
name|AppContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|ContainerLauncherImpl
argument_list|(
name|context
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|ContainerLauncherEvent
name|event
parameter_list|)
block|{
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|CONTAINER_REMOTE_LAUNCH
case|:
name|super
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
comment|// Unused event and container.
break|break;
case|case
name|CONTAINER_REMOTE_CLEANUP
case|:
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
name|event
operator|.
name|getTaskAttemptID
argument_list|()
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_CONTAINER_CLEANED
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|ContainerManager
name|getCMProxy
parameter_list|(
name|ContainerId
name|contianerID
parameter_list|,
name|String
name|containerManagerBindAddr
parameter_list|,
name|Token
name|containerToken
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|wait
argument_list|()
expr_stmt|;
comment|// Just hang the thread simulating a very slow NM.
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
empty_stmt|;
block|}
DECL|class|TimeOutTaskMRApp
specifier|static
class|class
name|TimeOutTaskMRApp
extends|extends
name|MRApp
block|{
DECL|method|TimeOutTaskMRApp (int maps, int reduces)
name|TimeOutTaskMRApp
parameter_list|(
name|int
name|maps
parameter_list|,
name|int
name|reduces
parameter_list|)
block|{
name|super
argument_list|(
name|maps
argument_list|,
name|reduces
argument_list|,
literal|false
argument_list|,
literal|"TimeOutTaskMRApp"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createTaskAttemptListener (AppContext context)
specifier|protected
name|TaskAttemptListener
name|createTaskAttemptListener
parameter_list|(
name|AppContext
name|context
parameter_list|)
block|{
comment|//This will create the TaskAttemptListener with TaskHeartbeatHandler
comment|//RPC servers are not started
comment|//task time out is reduced
comment|//when attempt times out, heartbeat handler will send the lost event
comment|//leading to Attempt failure
return|return
operator|new
name|TaskAttemptListenerImpl
argument_list|(
name|getContext
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|startRpcServer
parameter_list|()
block|{}
empty_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|stopRpcServer
parameter_list|()
block|{}
empty_stmt|;
annotation|@
name|Override
specifier|public
name|InetSocketAddress
name|getAddress
parameter_list|()
block|{
return|return
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
literal|"localhost"
argument_list|,
literal|1234
argument_list|)
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|TASK_TIMEOUT
argument_list|,
literal|1
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|//reduce timeout
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|TASK_TIMEOUT_CHECK_INTERVAL_MS
argument_list|,
literal|1
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
comment|//Attempts of first Task are failed
DECL|class|MockFirstFailingTaskMRApp
specifier|static
class|class
name|MockFirstFailingTaskMRApp
extends|extends
name|MRApp
block|{
DECL|method|MockFirstFailingTaskMRApp (int maps, int reduces)
name|MockFirstFailingTaskMRApp
parameter_list|(
name|int
name|maps
parameter_list|,
name|int
name|reduces
parameter_list|)
block|{
name|super
argument_list|(
name|maps
argument_list|,
name|reduces
argument_list|,
literal|true
argument_list|,
literal|"MockFirstFailingTaskMRApp"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|attemptLaunched (TaskAttemptId attemptID)
specifier|protected
name|void
name|attemptLaunched
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
block|{
if|if
condition|(
name|attemptID
operator|.
name|getTaskId
argument_list|()
operator|.
name|getId
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|//check if it is first task
comment|// send the Fail event
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
name|attemptID
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_FAILMSG
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
name|attemptID
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_DONE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//First attempt is failed
DECL|class|MockFirstFailingAttemptMRApp
specifier|static
class|class
name|MockFirstFailingAttemptMRApp
extends|extends
name|MRApp
block|{
DECL|method|MockFirstFailingAttemptMRApp (int maps, int reduces)
name|MockFirstFailingAttemptMRApp
parameter_list|(
name|int
name|maps
parameter_list|,
name|int
name|reduces
parameter_list|)
block|{
name|super
argument_list|(
name|maps
argument_list|,
name|reduces
argument_list|,
literal|true
argument_list|,
literal|"MockFirstFailingAttemptMRApp"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|attemptLaunched (TaskAttemptId attemptID)
specifier|protected
name|void
name|attemptLaunched
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
block|{
if|if
condition|(
name|attemptID
operator|.
name|getTaskId
argument_list|()
operator|.
name|getId
argument_list|()
operator|==
literal|0
operator|&&
name|attemptID
operator|.
name|getId
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|//check if it is first task's first attempt
comment|// send the Fail event
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
name|attemptID
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_FAILMSG
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
name|attemptID
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_DONE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|TestFail
name|t
init|=
operator|new
name|TestFail
argument_list|()
decl_stmt|;
name|t
operator|.
name|testFailTask
argument_list|()
expr_stmt|;
name|t
operator|.
name|testTimedOutTask
argument_list|()
expr_stmt|;
name|t
operator|.
name|testMapFailureMaxPercent
argument_list|()
expr_stmt|;
name|t
operator|.
name|testReduceFailureMaxPercent
argument_list|()
expr_stmt|;
name|t
operator|.
name|testTaskFailWithUnusedContainer
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

