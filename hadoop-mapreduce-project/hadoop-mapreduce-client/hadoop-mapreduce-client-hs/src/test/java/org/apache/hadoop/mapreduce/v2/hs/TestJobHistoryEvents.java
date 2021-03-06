begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs
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
name|hs
package|;
end_package

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
name|api
operator|.
name|records
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
name|MRApp
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
name|service
operator|.
name|Service
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
import|import static
name|org
operator|.
name|assertj
operator|.
name|core
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertThat
import|;
end_import

begin_class
DECL|class|TestJobHistoryEvents
specifier|public
class|class
name|TestJobHistoryEvents
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
name|TestJobHistoryEvents
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testHistoryEvents ()
specifier|public
name|void
name|testHistoryEvents
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
name|app
operator|.
name|submit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Job
name|job
init|=
name|app
operator|.
name|getContext
argument_list|()
operator|.
name|getAllJobs
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
name|JobId
name|jobId
init|=
name|job
operator|.
name|getID
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"JOBID is "
operator|+
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|jobId
argument_list|)
operator|.
name|toString
argument_list|()
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
comment|//make sure all events are flushed
name|app
operator|.
name|waitForState
argument_list|(
name|Service
operator|.
name|STATE
operator|.
name|STOPPED
argument_list|)
expr_stmt|;
comment|/*      * Use HistoryContext to read logged events and verify the number of       * completed maps      */
name|HistoryContext
name|context
init|=
operator|new
name|JobHistory
argument_list|()
decl_stmt|;
comment|// test start and stop states
operator|(
operator|(
name|JobHistory
operator|)
name|context
operator|)
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
operator|(
operator|(
name|JobHistory
operator|)
name|context
operator|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|context
operator|.
name|getStartTime
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|(
operator|(
name|JobHistory
operator|)
name|context
operator|)
operator|.
name|getServiceState
argument_list|()
argument_list|,
name|Service
operator|.
name|STATE
operator|.
name|STARTED
argument_list|)
expr_stmt|;
comment|// get job before stopping JobHistory
name|Job
name|parsedJob
init|=
name|context
operator|.
name|getJob
argument_list|(
name|jobId
argument_list|)
decl_stmt|;
comment|// stop JobHistory
operator|(
operator|(
name|JobHistory
operator|)
name|context
operator|)
operator|.
name|stop
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|(
operator|(
name|JobHistory
operator|)
name|context
operator|)
operator|.
name|getServiceState
argument_list|()
argument_list|,
name|Service
operator|.
name|STATE
operator|.
name|STOPPED
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"CompletedMaps not correct"
argument_list|,
literal|2
argument_list|,
name|parsedJob
operator|.
name|getCompletedMaps
argument_list|()
argument_list|)
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
name|parsedJob
operator|.
name|getUserName
argument_list|()
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
name|parsedJob
operator|.
name|getTasks
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"No of tasks not correct"
argument_list|,
literal|3
argument_list|,
name|tasks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Task
name|task
range|:
name|tasks
operator|.
name|values
argument_list|()
control|)
block|{
name|verifyTask
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|maps
init|=
name|parsedJob
operator|.
name|getTasks
argument_list|(
name|TaskType
operator|.
name|MAP
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"No of maps not correct"
argument_list|,
literal|2
argument_list|,
name|maps
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|reduces
init|=
name|parsedJob
operator|.
name|getTasks
argument_list|(
name|TaskType
operator|.
name|REDUCE
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"No of reduces not correct"
argument_list|,
literal|1
argument_list|,
name|reduces
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"CompletedReduce not correct"
argument_list|,
literal|1
argument_list|,
name|parsedJob
operator|.
name|getCompletedReduces
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Job state not currect"
argument_list|,
name|JobState
operator|.
name|SUCCEEDED
argument_list|,
name|parsedJob
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that all the events are flushed on stopping the HistoryHandler    * @throws Exception    */
annotation|@
name|Test
DECL|method|testEventsFlushOnStop ()
specifier|public
name|void
name|testEventsFlushOnStop
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
name|MRApp
name|app
init|=
operator|new
name|MRAppWithSpecialHistoryHandler
argument_list|(
literal|1
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
name|app
operator|.
name|submit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Job
name|job
init|=
name|app
operator|.
name|getContext
argument_list|()
operator|.
name|getAllJobs
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
name|JobId
name|jobId
init|=
name|job
operator|.
name|getID
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"JOBID is "
operator|+
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|jobId
argument_list|)
operator|.
name|toString
argument_list|()
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
comment|// make sure all events are flushed
name|app
operator|.
name|waitForState
argument_list|(
name|Service
operator|.
name|STATE
operator|.
name|STOPPED
argument_list|)
expr_stmt|;
comment|/*      * Use HistoryContext to read logged events and verify the number of      * completed maps      */
name|HistoryContext
name|context
init|=
operator|new
name|JobHistory
argument_list|()
decl_stmt|;
operator|(
operator|(
name|JobHistory
operator|)
name|context
operator|)
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Job
name|parsedJob
init|=
name|context
operator|.
name|getJob
argument_list|(
name|jobId
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"CompletedMaps not correct"
argument_list|,
literal|1
argument_list|,
name|parsedJob
operator|.
name|getCompletedMaps
argument_list|()
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
name|parsedJob
operator|.
name|getTasks
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"No of tasks not correct"
argument_list|,
literal|1
argument_list|,
name|tasks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|verifyTask
argument_list|(
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
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|maps
init|=
name|parsedJob
operator|.
name|getTasks
argument_list|(
name|TaskType
operator|.
name|MAP
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"No of maps not correct"
argument_list|,
literal|1
argument_list|,
name|maps
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Job state not currect"
argument_list|,
name|JobState
operator|.
name|SUCCEEDED
argument_list|,
name|parsedJob
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJobHistoryEventHandlerIsFirstServiceToStop ()
specifier|public
name|void
name|testJobHistoryEventHandlerIsFirstServiceToStop
parameter_list|()
block|{
name|MRApp
name|app
init|=
operator|new
name|MRAppWithSpecialHistoryHandler
argument_list|(
literal|1
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
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|app
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Service
index|[]
name|services
init|=
name|app
operator|.
name|getServices
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|Service
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
comment|// Verifying that it is the last to be added is same as verifying that it is
comment|// the first to be stopped. CompositeService related tests already validate
comment|// this.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"JobHistoryEventHandler"
argument_list|,
name|services
index|[
name|services
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAssignedQueue ()
specifier|public
name|void
name|testAssignedQueue
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
argument_list|,
literal|"assignedQueue"
argument_list|)
decl_stmt|;
name|app
operator|.
name|submit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Job
name|job
init|=
name|app
operator|.
name|getContext
argument_list|()
operator|.
name|getAllJobs
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
name|JobId
name|jobId
init|=
name|job
operator|.
name|getID
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"JOBID is "
operator|+
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|jobId
argument_list|)
operator|.
name|toString
argument_list|()
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
comment|//make sure all events are flushed
name|app
operator|.
name|waitForState
argument_list|(
name|Service
operator|.
name|STATE
operator|.
name|STOPPED
argument_list|)
expr_stmt|;
comment|/*      * Use HistoryContext to read logged events and verify the number of       * completed maps      */
name|HistoryContext
name|context
init|=
operator|new
name|JobHistory
argument_list|()
decl_stmt|;
comment|// test start and stop states
operator|(
operator|(
name|JobHistory
operator|)
name|context
operator|)
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
operator|(
operator|(
name|JobHistory
operator|)
name|context
operator|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|context
operator|.
name|getStartTime
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|JobHistory
operator|)
name|context
operator|)
operator|.
name|getServiceState
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|Service
operator|.
name|STATE
operator|.
name|STARTED
argument_list|)
expr_stmt|;
comment|// get job before stopping JobHistory
name|Job
name|parsedJob
init|=
name|context
operator|.
name|getJob
argument_list|(
name|jobId
argument_list|)
decl_stmt|;
comment|// stop JobHistory
operator|(
operator|(
name|JobHistory
operator|)
name|context
operator|)
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|JobHistory
operator|)
name|context
operator|)
operator|.
name|getServiceState
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|Service
operator|.
name|STATE
operator|.
name|STOPPED
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"QueueName not correct"
argument_list|,
literal|"assignedQueue"
argument_list|,
name|parsedJob
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyTask (Task task)
specifier|private
name|void
name|verifyTask
parameter_list|(
name|Task
name|task
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Task state not currect"
argument_list|,
name|TaskState
operator|.
name|SUCCEEDED
argument_list|,
name|task
operator|.
name|getState
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
name|task
operator|.
name|getAttempts
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"No of attempts not correct"
argument_list|,
literal|1
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
name|verifyAttempt
argument_list|(
name|attempt
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyAttempt (TaskAttempt attempt)
specifier|private
name|void
name|verifyAttempt
parameter_list|(
name|TaskAttempt
name|attempt
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"TaskAttempt state not currect"
argument_list|,
name|TaskAttemptState
operator|.
name|SUCCEEDED
argument_list|,
name|attempt
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|attempt
operator|.
name|getAssignedContainerID
argument_list|()
argument_list|)
expr_stmt|;
comment|//Verify the wrong ctor is not being used. Remove after mrv1 is removed.
name|ContainerId
name|fakeCid
init|=
name|MRApp
operator|.
name|newContainerId
argument_list|(
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|attempt
operator|.
name|getAssignedContainerID
argument_list|()
operator|.
name|equals
argument_list|(
name|fakeCid
argument_list|)
argument_list|)
expr_stmt|;
comment|//Verify complete contianerManagerAddress
name|Assert
operator|.
name|assertEquals
argument_list|(
name|MRApp
operator|.
name|NM_HOST
operator|+
literal|":"
operator|+
name|MRApp
operator|.
name|NM_PORT
argument_list|,
name|attempt
operator|.
name|getAssignedContainerMgrAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|MRAppWithHistory
specifier|static
class|class
name|MRAppWithHistory
extends|extends
name|MRApp
block|{
DECL|method|MRAppWithHistory (int maps, int reduces, boolean autoComplete, String testName, boolean cleanOnStart)
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
DECL|method|MRAppWithHistory (int maps, int reduces, boolean autoComplete, String testName, boolean cleanOnStart, String assignedQueue)
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
name|String
name|assignedQueue
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
name|assignedQueue
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
return|return
operator|new
name|JobHistoryEventHandler
argument_list|(
name|context
argument_list|,
name|getStartCount
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * MRapp with special HistoryEventHandler that writes events only during stop.    * This is to simulate events that don't get written by the eventHandling    * thread due to say a slow DFS and verify that they are flushed during stop.    */
DECL|class|MRAppWithSpecialHistoryHandler
specifier|private
specifier|static
class|class
name|MRAppWithSpecialHistoryHandler
extends|extends
name|MRApp
block|{
DECL|method|MRAppWithSpecialHistoryHandler (int maps, int reduces, boolean autoComplete, String testName, boolean cleanOnStart)
specifier|public
name|MRAppWithSpecialHistoryHandler
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
return|return
operator|new
name|JobHistoryEventHandler
argument_list|(
name|context
argument_list|,
name|getStartCount
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|serviceStart
parameter_list|()
block|{
comment|// Don't start any event draining thread.
name|super
operator|.
name|eventHandlingThread
operator|=
operator|new
name|Thread
argument_list|()
expr_stmt|;
name|super
operator|.
name|eventHandlingThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
return|;
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
name|TestJobHistoryEvents
name|t
init|=
operator|new
name|TestJobHistoryEvents
argument_list|()
decl_stmt|;
name|t
operator|.
name|testHistoryEvents
argument_list|()
expr_stmt|;
name|t
operator|.
name|testEventsFlushOnStop
argument_list|()
expr_stmt|;
name|t
operator|.
name|testJobHistoryEventHandlerIsFirstServiceToStop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

