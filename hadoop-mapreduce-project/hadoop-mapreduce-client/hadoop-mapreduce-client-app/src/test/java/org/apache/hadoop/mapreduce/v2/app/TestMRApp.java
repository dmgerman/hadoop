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
name|spy
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
name|TaskEvent
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
name|TaskEventType
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
name|JobImpl
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
comment|/**  * Tests the state machine of MR App.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|TestMRApp
specifier|public
class|class
name|TestMRApp
block|{
annotation|@
name|Test
DECL|method|testMapReduce ()
specifier|public
name|void
name|testMapReduce
parameter_list|()
throws|throws
name|Exception
block|{
name|MRApp
name|app
init|=
operator|new
name|MRApp
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|true
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
argument_list|)
decl_stmt|;
name|Job
name|job
init|=
name|app
operator|.
name|submit
argument_list|(
operator|new
name|Configuration
argument_list|()
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
name|app
operator|.
name|verifyCompleted
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|,
name|job
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testZeroMaps ()
specifier|public
name|void
name|testZeroMaps
parameter_list|()
throws|throws
name|Exception
block|{
name|MRApp
name|app
init|=
operator|new
name|MRApp
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|true
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
argument_list|)
decl_stmt|;
name|Job
name|job
init|=
name|app
operator|.
name|submit
argument_list|(
operator|new
name|Configuration
argument_list|()
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
name|app
operator|.
name|verifyCompleted
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testZeroMapReduces ()
specifier|public
name|void
name|testZeroMapReduces
parameter_list|()
throws|throws
name|Exception
block|{
name|MRApp
name|app
init|=
operator|new
name|MRApp
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|true
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
argument_list|)
decl_stmt|;
name|Job
name|job
init|=
name|app
operator|.
name|submit
argument_list|(
operator|new
name|Configuration
argument_list|()
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
block|}
annotation|@
name|Test
DECL|method|testCommitPending ()
specifier|public
name|void
name|testCommitPending
parameter_list|()
throws|throws
name|Exception
block|{
name|MRApp
name|app
init|=
operator|new
name|MRApp
argument_list|(
literal|1
argument_list|,
literal|0
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
argument_list|)
decl_stmt|;
name|Job
name|job
init|=
name|app
operator|.
name|submit
argument_list|(
operator|new
name|Configuration
argument_list|()
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
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Num tasks not correct"
argument_list|,
literal|1
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
name|task
init|=
name|it
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
name|RUNNING
argument_list|)
expr_stmt|;
name|TaskAttempt
name|attempt
init|=
name|task
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
name|app
operator|.
name|waitForState
argument_list|(
name|attempt
argument_list|,
name|TaskAttemptState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|//send the commit pending signal to the task
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
name|attempt
operator|.
name|getID
argument_list|()
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_COMMIT_PENDING
argument_list|)
argument_list|)
expr_stmt|;
comment|//wait for first attempt to commit pending
name|app
operator|.
name|waitForState
argument_list|(
name|attempt
argument_list|,
name|TaskAttemptState
operator|.
name|COMMIT_PENDING
argument_list|)
expr_stmt|;
comment|//send the done signal to the task
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
name|task
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
block|}
comment|//@Test
DECL|method|testCompletedMapsForReduceSlowstart ()
specifier|public
name|void
name|testCompletedMapsForReduceSlowstart
parameter_list|()
throws|throws
name|Exception
block|{
name|MRApp
name|app
init|=
operator|new
name|MRApp
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
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|//after half of the map completion, reduce will start
name|conf
operator|.
name|setFloat
argument_list|(
name|MRJobConfig
operator|.
name|COMPLETED_MAPS_FOR_REDUCE_SLOWSTART
argument_list|,
literal|0.5f
argument_list|)
expr_stmt|;
comment|//uberization forces full slowstart (1.0), so disable that
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
comment|//all maps would be running
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Num tasks not correct"
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
name|task1Attempt
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
name|task1Attempt
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
name|NEW
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
comment|//send the done signal to the 1st map task
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
comment|//Once the first map completes, it will schedule the reduces
comment|//now reduce must be running
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
comment|//send the done signal to 2nd map and the reduce to complete the job
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
block|}
annotation|@
name|Test
DECL|method|testJobError ()
specifier|public
name|void
name|testJobError
parameter_list|()
throws|throws
name|Exception
block|{
name|MRApp
name|app
init|=
operator|new
name|MRApp
argument_list|(
literal|1
argument_list|,
literal|0
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
argument_list|)
decl_stmt|;
name|Job
name|job
init|=
name|app
operator|.
name|submit
argument_list|(
operator|new
name|Configuration
argument_list|()
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
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Num tasks not correct"
argument_list|,
literal|1
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
name|task
init|=
name|it
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
name|RUNNING
argument_list|)
expr_stmt|;
comment|//send an invalid event on task at current state
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
name|TaskEvent
argument_list|(
name|task
operator|.
name|getID
argument_list|()
argument_list|,
name|TaskEventType
operator|.
name|T_SCHEDULE
argument_list|)
argument_list|)
expr_stmt|;
comment|//this must lead to job error
name|app
operator|.
name|waitForState
argument_list|(
name|job
argument_list|,
name|JobState
operator|.
name|ERROR
argument_list|)
expr_stmt|;
block|}
DECL|class|MRAppWithSpiedJob
specifier|private
specifier|final
class|class
name|MRAppWithSpiedJob
extends|extends
name|MRApp
block|{
DECL|field|spiedJob
specifier|private
name|JobImpl
name|spiedJob
decl_stmt|;
DECL|method|MRAppWithSpiedJob (int maps, int reduces, boolean autoComplete, String testName, boolean cleanOnStart)
specifier|private
name|MRAppWithSpiedJob
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createJob (Configuration conf)
specifier|protected
name|Job
name|createJob
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|spiedJob
operator|=
name|spy
argument_list|(
operator|(
name|JobImpl
operator|)
name|super
operator|.
name|createJob
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
operator|(
operator|(
name|AppContext
operator|)
name|getContext
argument_list|()
operator|)
operator|.
name|getAllJobs
argument_list|()
operator|.
name|put
argument_list|(
name|spiedJob
operator|.
name|getID
argument_list|()
argument_list|,
name|spiedJob
argument_list|)
expr_stmt|;
return|return
name|spiedJob
return|;
block|}
DECL|method|getSpiedJob ()
name|JobImpl
name|getSpiedJob
parameter_list|()
block|{
return|return
name|this
operator|.
name|spiedJob
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCountersOnJobFinish ()
specifier|public
name|void
name|testCountersOnJobFinish
parameter_list|()
throws|throws
name|Exception
block|{
name|MRAppWithSpiedJob
name|app
init|=
operator|new
name|MRAppWithSpiedJob
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|true
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
argument_list|)
decl_stmt|;
name|JobImpl
name|job
init|=
operator|(
name|JobImpl
operator|)
name|app
operator|.
name|submit
argument_list|(
operator|new
name|Configuration
argument_list|()
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
name|app
operator|.
name|verifyCompleted
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|job
operator|.
name|getAllCounters
argument_list|()
argument_list|)
expr_stmt|;
comment|// Just call getCounters
name|job
operator|.
name|getAllCounters
argument_list|()
expr_stmt|;
name|job
operator|.
name|getAllCounters
argument_list|()
expr_stmt|;
comment|// Should be called only once
name|verify
argument_list|(
name|job
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|constructFinalFullcounters
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|checkJobStateTypeConversion ()
specifier|public
name|void
name|checkJobStateTypeConversion
parameter_list|()
block|{
comment|//verify that all states can be converted without
comment|// throwing an exception
for|for
control|(
name|JobState
name|state
range|:
name|JobState
operator|.
name|values
argument_list|()
control|)
block|{
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|checkTaskStateTypeConversion ()
specifier|public
name|void
name|checkTaskStateTypeConversion
parameter_list|()
block|{
comment|//verify that all states can be converted without
comment|// throwing an exception
for|for
control|(
name|TaskState
name|state
range|:
name|TaskState
operator|.
name|values
argument_list|()
control|)
block|{
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|state
argument_list|)
expr_stmt|;
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
name|TestMRApp
name|t
init|=
operator|new
name|TestMRApp
argument_list|()
decl_stmt|;
name|t
operator|.
name|testMapReduce
argument_list|()
expr_stmt|;
name|t
operator|.
name|testZeroMapReduces
argument_list|()
expr_stmt|;
name|t
operator|.
name|testCommitPending
argument_list|()
expr_stmt|;
name|t
operator|.
name|testCompletedMapsForReduceSlowstart
argument_list|()
expr_stmt|;
name|t
operator|.
name|testJobError
argument_list|()
expr_stmt|;
name|t
operator|.
name|testCountersOnJobFinish
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

