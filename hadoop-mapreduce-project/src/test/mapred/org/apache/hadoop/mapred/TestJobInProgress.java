begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * TestJobInProgress is a unit test to test consistency of JobInProgress class  * data structures under different conditions (speculation/locality) and at  * different stages (tasks are running/pending/killed)  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|BeforeClass
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
name|mapred
operator|.
name|FakeObjectUtilities
operator|.
name|FakeJobInProgress
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
name|FakeObjectUtilities
operator|.
name|FakeJobTracker
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
name|TaskStatus
operator|.
name|Phase
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
name|UtilsForTests
operator|.
name|FakeClock
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
name|Cluster
operator|.
name|JobTrackerStatus
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
name|JobCounter
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
name|server
operator|.
name|jobtracker
operator|.
name|JTConfig
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
name|split
operator|.
name|JobSplit
operator|.
name|TaskSplitMetaInfo
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
name|DNSToSwitchMapping
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
name|NetworkTopology
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
name|Node
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
name|NodeBase
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
name|StaticMapping
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|class|TestJobInProgress
specifier|public
class|class
name|TestJobInProgress
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestJobInProgress
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|jobTracker
specifier|static
name|FakeJobTracker
name|jobTracker
decl_stmt|;
DECL|field|trackers
specifier|static
name|String
name|trackers
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"tracker_tracker1.r1.com:1000"
block|,
literal|"tracker_tracker2.r1.com:1000"
block|,
literal|"tracker_tracker3.r2.com:1000"
block|,
literal|"tracker_tracker4.r3.com:1000"
block|}
decl_stmt|;
DECL|field|hosts
specifier|static
name|String
index|[]
name|hosts
init|=
operator|new
name|String
index|[]
block|{
literal|"tracker1.r1.com"
block|,
literal|"tracker2.r1.com"
block|,
literal|"tracker3.r2.com"
block|,
literal|"tracker4.r3.com"
block|}
decl_stmt|;
DECL|field|racks
specifier|static
name|String
index|[]
name|racks
init|=
operator|new
name|String
index|[]
block|{
literal|"/r1"
block|,
literal|"/r1"
block|,
literal|"/r2"
block|,
literal|"/r3"
block|}
decl_stmt|;
DECL|field|numUniqueHosts
specifier|static
name|int
name|numUniqueHosts
init|=
name|hosts
operator|.
name|length
decl_stmt|;
DECL|field|clusterSize
specifier|static
name|int
name|clusterSize
init|=
name|trackers
operator|.
name|length
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JTConfig
operator|.
name|JT_IPC_ADDRESS
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JTConfig
operator|.
name|JT_HTTP_ADDRESS
argument_list|,
literal|"0.0.0.0:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
literal|"topology.node.switch.mapping.impl"
argument_list|,
name|StaticMapping
operator|.
name|class
argument_list|,
name|DNSToSwitchMapping
operator|.
name|class
argument_list|)
expr_stmt|;
name|jobTracker
operator|=
operator|new
name|FakeJobTracker
argument_list|(
name|conf
argument_list|,
operator|new
name|FakeClock
argument_list|()
argument_list|,
name|trackers
argument_list|)
expr_stmt|;
comment|// Set up the Topology Information
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hosts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|StaticMapping
operator|.
name|addNodeToRack
argument_list|(
name|hosts
index|[
name|i
index|]
argument_list|,
name|racks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|s
range|:
name|trackers
control|)
block|{
name|FakeObjectUtilities
operator|.
name|establishFirstContact
argument_list|(
name|jobTracker
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MyFakeJobInProgress
specifier|static
class|class
name|MyFakeJobInProgress
extends|extends
name|FakeJobInProgress
block|{
DECL|method|MyFakeJobInProgress (JobConf jc, JobTracker jt)
name|MyFakeJobInProgress
parameter_list|(
name|JobConf
name|jc
parameter_list|,
name|JobTracker
name|jt
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|jc
argument_list|,
name|jt
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createSplits (org.apache.hadoop.mapreduce.JobID jobId)
name|TaskSplitMetaInfo
index|[]
name|createSplits
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|JobID
name|jobId
parameter_list|)
block|{
comment|// Set all splits to reside on one host. This will ensure that
comment|// one tracker gets data local, one gets rack local and two others
comment|// get non-local maps
name|TaskSplitMetaInfo
index|[]
name|splits
init|=
operator|new
name|TaskSplitMetaInfo
index|[
name|numMapTasks
index|]
decl_stmt|;
name|String
index|[]
name|splitHosts0
init|=
operator|new
name|String
index|[]
block|{
name|hosts
index|[
literal|0
index|]
block|}
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numMapTasks
condition|;
name|i
operator|++
control|)
block|{
name|splits
index|[
name|i
index|]
operator|=
operator|new
name|TaskSplitMetaInfo
argument_list|(
name|splitHosts0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|splits
return|;
block|}
DECL|method|makeRunning (TaskAttemptID taskId, TaskInProgress tip, String taskTracker)
specifier|private
name|void
name|makeRunning
parameter_list|(
name|TaskAttemptID
name|taskId
parameter_list|,
name|TaskInProgress
name|tip
parameter_list|,
name|String
name|taskTracker
parameter_list|)
block|{
name|TaskStatus
name|status
init|=
name|TaskStatus
operator|.
name|createTaskStatus
argument_list|(
name|tip
operator|.
name|isMapTask
argument_list|()
argument_list|,
name|taskId
argument_list|,
literal|0.0f
argument_list|,
literal|1
argument_list|,
name|TaskStatus
operator|.
name|State
operator|.
name|RUNNING
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
name|taskTracker
argument_list|,
name|tip
operator|.
name|isMapTask
argument_list|()
condition|?
name|Phase
operator|.
name|MAP
else|:
name|Phase
operator|.
name|REDUCE
argument_list|,
operator|new
name|Counters
argument_list|()
argument_list|)
decl_stmt|;
name|updateTaskStatus
argument_list|(
name|tip
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
DECL|method|getTipForTaskID (TaskAttemptID tid, boolean isMap)
specifier|private
name|TaskInProgress
name|getTipForTaskID
parameter_list|(
name|TaskAttemptID
name|tid
parameter_list|,
name|boolean
name|isMap
parameter_list|)
block|{
name|TaskInProgress
name|result
init|=
literal|null
decl_stmt|;
name|TaskID
name|id
init|=
name|tid
operator|.
name|getTaskID
argument_list|()
decl_stmt|;
name|TaskInProgress
index|[]
name|arrayToLook
init|=
name|isMap
condition|?
name|maps
else|:
name|reduces
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|arrayToLook
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TaskInProgress
name|tip
init|=
name|arrayToLook
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|tip
operator|.
name|getTIPId
argument_list|()
operator|==
name|id
condition|)
block|{
name|result
operator|=
name|tip
expr_stmt|;
break|break;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * Find a new Map or a reduce task and mark it as running on the specified      * tracker      */
DECL|method|findAndRunNewTask (boolean isMap, String tt, String host, int clusterSize, int numUniqueHosts)
specifier|public
name|TaskAttemptID
name|findAndRunNewTask
parameter_list|(
name|boolean
name|isMap
parameter_list|,
name|String
name|tt
parameter_list|,
name|String
name|host
parameter_list|,
name|int
name|clusterSize
parameter_list|,
name|int
name|numUniqueHosts
parameter_list|)
throws|throws
name|IOException
block|{
name|TaskTrackerStatus
name|tts
init|=
operator|new
name|TaskTrackerStatus
argument_list|(
name|tt
argument_list|,
name|host
argument_list|)
decl_stmt|;
name|Task
name|task
init|=
name|isMap
condition|?
name|obtainNewMapTask
argument_list|(
name|tts
argument_list|,
name|clusterSize
argument_list|,
name|numUniqueHosts
argument_list|)
else|:
name|obtainNewReduceTask
argument_list|(
name|tts
argument_list|,
name|clusterSize
argument_list|,
name|numUniqueHosts
argument_list|)
decl_stmt|;
name|TaskAttemptID
name|tid
init|=
name|task
operator|.
name|getTaskID
argument_list|()
decl_stmt|;
name|makeRunning
argument_list|(
name|task
operator|.
name|getTaskID
argument_list|()
argument_list|,
name|getTipForTaskID
argument_list|(
name|tid
argument_list|,
name|isMap
argument_list|)
argument_list|,
name|tt
argument_list|)
expr_stmt|;
return|return
name|tid
return|;
block|}
block|}
comment|//@Test
DECL|method|testPendingMapTaskCount ()
specifier|public
name|void
name|testPendingMapTaskCount
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numMaps
init|=
literal|4
decl_stmt|;
name|int
name|numReds
init|=
literal|4
decl_stmt|;
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setNumMapTasks
argument_list|(
name|numMaps
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setNumReduceTasks
argument_list|(
name|numReds
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setSpeculativeExecution
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|JobContext
operator|.
name|SETUP_CLEANUP_NEEDED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|MyFakeJobInProgress
name|job1
init|=
operator|new
name|MyFakeJobInProgress
argument_list|(
name|conf
argument_list|,
name|jobTracker
argument_list|)
decl_stmt|;
name|job1
operator|.
name|initTasks
argument_list|()
expr_stmt|;
name|TaskAttemptID
index|[]
name|tid
init|=
operator|new
name|TaskAttemptID
index|[
name|numMaps
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numMaps
condition|;
name|i
operator|++
control|)
block|{
name|tid
index|[
name|i
index|]
operator|=
name|job1
operator|.
name|findAndRunNewTask
argument_list|(
literal|true
argument_list|,
name|trackers
index|[
name|i
index|]
argument_list|,
name|hosts
index|[
name|i
index|]
argument_list|,
name|clusterSize
argument_list|,
name|numUniqueHosts
argument_list|)
expr_stmt|;
block|}
comment|// Fail all maps
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numMaps
condition|;
name|i
operator|++
control|)
block|{
name|job1
operator|.
name|failTask
argument_list|(
name|tid
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|MyFakeJobInProgress
name|job2
init|=
operator|new
name|MyFakeJobInProgress
argument_list|(
name|conf
argument_list|,
name|jobTracker
argument_list|)
decl_stmt|;
name|job2
operator|.
name|initTasks
argument_list|()
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
name|numMaps
condition|;
name|i
operator|++
control|)
block|{
name|tid
index|[
name|i
index|]
operator|=
name|job2
operator|.
name|findAndRunNewTask
argument_list|(
literal|true
argument_list|,
name|trackers
index|[
name|i
index|]
argument_list|,
name|hosts
index|[
name|i
index|]
argument_list|,
name|clusterSize
argument_list|,
name|numUniqueHosts
argument_list|)
expr_stmt|;
name|job2
operator|.
name|finishTask
argument_list|(
name|tid
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numReds
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|tid
index|[
name|i
index|]
operator|=
name|job2
operator|.
name|findAndRunNewTask
argument_list|(
literal|false
argument_list|,
name|trackers
index|[
name|i
index|]
argument_list|,
name|hosts
index|[
name|i
index|]
argument_list|,
name|clusterSize
argument_list|,
name|numUniqueHosts
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numReds
operator|/
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|job2
operator|.
name|finishTask
argument_list|(
name|tid
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|numReds
operator|/
literal|4
init|;
name|i
operator|<
name|numReds
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|job2
operator|.
name|failTask
argument_list|(
name|tid
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Job1. All Maps have failed, no reduces have been scheduled
name|checkTaskCounts
argument_list|(
name|job1
argument_list|,
literal|0
argument_list|,
name|numMaps
argument_list|,
literal|0
argument_list|,
name|numReds
argument_list|)
expr_stmt|;
comment|// Job2. All Maps have completed. One reducer has completed, one has
comment|// failed and two others have not been scheduled
name|checkTaskCounts
argument_list|(
name|job2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|3
operator|*
name|numReds
operator|/
literal|4
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test if running tasks are correctly maintained for various types of jobs    */
DECL|method|testRunningTaskCount (boolean speculation)
specifier|static
name|void
name|testRunningTaskCount
parameter_list|(
name|boolean
name|speculation
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Testing running jobs with speculation : "
operator|+
name|speculation
argument_list|)
expr_stmt|;
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setNumMapTasks
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setNumReduceTasks
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setSpeculativeExecution
argument_list|(
name|speculation
argument_list|)
expr_stmt|;
name|MyFakeJobInProgress
name|jip
init|=
operator|new
name|MyFakeJobInProgress
argument_list|(
name|conf
argument_list|,
name|jobTracker
argument_list|)
decl_stmt|;
name|jip
operator|.
name|initTasks
argument_list|()
expr_stmt|;
name|TaskAttemptID
index|[]
name|tid
init|=
operator|new
name|TaskAttemptID
index|[
literal|4
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|tid
index|[
name|i
index|]
operator|=
name|jip
operator|.
name|findAndRunNewTask
argument_list|(
literal|true
argument_list|,
name|trackers
index|[
name|i
index|]
argument_list|,
name|hosts
index|[
name|i
index|]
argument_list|,
name|clusterSize
argument_list|,
name|numUniqueHosts
argument_list|)
expr_stmt|;
block|}
comment|// check if the running structures are populated
name|Set
argument_list|<
name|TaskInProgress
argument_list|>
name|uniqueTasks
init|=
operator|new
name|HashSet
argument_list|<
name|TaskInProgress
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Node
argument_list|,
name|Set
argument_list|<
name|TaskInProgress
argument_list|>
argument_list|>
name|s
range|:
name|jip
operator|.
name|getRunningMapCache
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|uniqueTasks
operator|.
name|addAll
argument_list|(
name|s
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// add non local map tasks
name|uniqueTasks
operator|.
name|addAll
argument_list|(
name|jip
operator|.
name|getNonLocalRunningMaps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Running map count doesnt match for jobs with speculation "
operator|+
name|speculation
argument_list|,
name|jip
operator|.
name|runningMaps
argument_list|()
argument_list|,
name|uniqueTasks
operator|.
name|size
argument_list|()
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|tid
index|[
name|i
index|]
operator|=
name|jip
operator|.
name|findAndRunNewTask
argument_list|(
literal|false
argument_list|,
name|trackers
index|[
name|i
index|]
argument_list|,
name|hosts
index|[
name|i
index|]
argument_list|,
name|clusterSize
argument_list|,
name|numUniqueHosts
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Running reducer count doesnt match for"
operator|+
literal|" jobs with speculation "
operator|+
name|speculation
argument_list|,
name|jip
operator|.
name|runningReduces
argument_list|()
argument_list|,
name|jip
operator|.
name|getRunningReduces
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testRunningTaskCount ()
specifier|public
name|void
name|testRunningTaskCount
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test with spec = false
name|testRunningTaskCount
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// test with spec = true
name|testRunningTaskCount
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|checkTaskCounts (JobInProgress jip, int runningMaps, int pendingMaps, int runningReduces, int pendingReduces)
specifier|static
name|void
name|checkTaskCounts
parameter_list|(
name|JobInProgress
name|jip
parameter_list|,
name|int
name|runningMaps
parameter_list|,
name|int
name|pendingMaps
parameter_list|,
name|int
name|runningReduces
parameter_list|,
name|int
name|pendingReduces
parameter_list|)
block|{
name|Counters
name|counter
init|=
name|jip
operator|.
name|getJobCounters
argument_list|()
decl_stmt|;
name|long
name|totalTaskCount
init|=
name|counter
operator|.
name|getCounter
argument_list|(
name|JobCounter
operator|.
name|TOTAL_LAUNCHED_MAPS
argument_list|)
operator|+
name|counter
operator|.
name|getCounter
argument_list|(
name|JobCounter
operator|.
name|TOTAL_LAUNCHED_REDUCES
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"totalTaskCount is "
operator|+
name|totalTaskCount
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|" Running Maps:"
operator|+
name|jip
operator|.
name|runningMaps
argument_list|()
operator|+
literal|" Pending Maps:"
operator|+
name|jip
operator|.
name|pendingMaps
argument_list|()
operator|+
literal|" Running Reds:"
operator|+
name|jip
operator|.
name|runningReduces
argument_list|()
operator|+
literal|" Pending Reds:"
operator|+
name|jip
operator|.
name|pendingReduces
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|jip
operator|.
name|getNumTaskCompletionEvents
argument_list|()
argument_list|,
name|totalTaskCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|runningMaps
argument_list|,
name|jip
operator|.
name|runningMaps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pendingMaps
argument_list|,
name|jip
operator|.
name|pendingMaps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|runningReduces
argument_list|,
name|jip
operator|.
name|runningReduces
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pendingReduces
argument_list|,
name|jip
operator|.
name|pendingReduces
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testJobSummary ()
specifier|public
name|void
name|testJobSummary
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numMaps
init|=
literal|2
decl_stmt|;
name|int
name|numReds
init|=
literal|2
decl_stmt|;
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setNumMapTasks
argument_list|(
name|numMaps
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setNumReduceTasks
argument_list|(
name|numReds
argument_list|)
expr_stmt|;
comment|// Spying a fake is easier than mocking here
name|MyFakeJobInProgress
name|jspy
init|=
name|spy
argument_list|(
operator|new
name|MyFakeJobInProgress
argument_list|(
name|conf
argument_list|,
name|jobTracker
argument_list|)
argument_list|)
decl_stmt|;
name|jspy
operator|.
name|initTasks
argument_list|()
expr_stmt|;
name|TaskAttemptID
name|tid
decl_stmt|;
comment|// Launch some map tasks
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numMaps
condition|;
name|i
operator|++
control|)
block|{
name|jspy
operator|.
name|maps
index|[
name|i
index|]
operator|.
name|setExecStartTime
argument_list|(
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|tid
operator|=
name|jspy
operator|.
name|findAndRunNewTask
argument_list|(
literal|true
argument_list|,
name|trackers
index|[
name|i
index|]
argument_list|,
name|hosts
index|[
name|i
index|]
argument_list|,
name|clusterSize
argument_list|,
name|numUniqueHosts
argument_list|)
expr_stmt|;
name|jspy
operator|.
name|finishTask
argument_list|(
name|tid
argument_list|)
expr_stmt|;
block|}
comment|// Launch some reduce tasks
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numReds
condition|;
name|i
operator|++
control|)
block|{
name|jspy
operator|.
name|reduces
index|[
name|i
index|]
operator|.
name|setExecStartTime
argument_list|(
name|i
operator|+
name|numMaps
operator|+
literal|1
argument_list|)
expr_stmt|;
name|tid
operator|=
name|jspy
operator|.
name|findAndRunNewTask
argument_list|(
literal|false
argument_list|,
name|trackers
index|[
name|i
index|]
argument_list|,
name|hosts
index|[
name|i
index|]
argument_list|,
name|clusterSize
argument_list|,
name|numUniqueHosts
argument_list|)
expr_stmt|;
name|jspy
operator|.
name|finishTask
argument_list|(
name|tid
argument_list|)
expr_stmt|;
block|}
comment|// Should be invoked numMaps + numReds times by different TIP objects
name|verify
argument_list|(
name|jspy
argument_list|,
name|times
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|setFirstTaskLaunchTime
argument_list|(
name|any
argument_list|(
name|TaskInProgress
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|ClusterStatus
name|cspy
init|=
name|spy
argument_list|(
operator|new
name|ClusterStatus
argument_list|(
literal|4
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|,
literal|4
argument_list|,
name|JobTrackerStatus
operator|.
name|RUNNING
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|JobInProgress
operator|.
name|JobSummary
operator|.
name|logJobSummary
argument_list|(
name|jspy
argument_list|,
name|cspy
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|jspy
argument_list|)
operator|.
name|getStatus
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|jspy
argument_list|)
operator|.
name|getProfile
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|jspy
argument_list|,
name|atLeastOnce
argument_list|()
argument_list|)
operator|.
name|getJobCounters
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|jspy
argument_list|,
name|atLeastOnce
argument_list|()
argument_list|)
operator|.
name|getJobID
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|jspy
argument_list|)
operator|.
name|getStartTime
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|jspy
argument_list|)
operator|.
name|getFirstTaskLaunchTimes
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|jspy
argument_list|)
operator|.
name|getFinishTime
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|jspy
argument_list|)
operator|.
name|getTasks
argument_list|(
name|TaskType
operator|.
name|MAP
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|jspy
argument_list|)
operator|.
name|getTasks
argument_list|(
name|TaskType
operator|.
name|REDUCE
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|jspy
argument_list|)
operator|.
name|getNumSlotsPerMap
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|jspy
argument_list|)
operator|.
name|getNumSlotsPerReduce
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|cspy
argument_list|)
operator|.
name|getMaxMapTasks
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|cspy
argument_list|)
operator|.
name|getMaxReduceTasks
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"firstMapTaskLaunchTime"
argument_list|,
literal|1
argument_list|,
name|jspy
operator|.
name|getFirstTaskLaunchTimes
argument_list|()
operator|.
name|get
argument_list|(
name|TaskType
operator|.
name|MAP
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"firstReduceTaskLaunchTime"
argument_list|,
literal|3
argument_list|,
name|jspy
operator|.
name|getFirstTaskLaunchTimes
argument_list|()
operator|.
name|get
argument_list|(
name|TaskType
operator|.
name|REDUCE
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLocality ()
specifier|public
name|void
name|testLocality
parameter_list|()
throws|throws
name|Exception
block|{
name|NetworkTopology
name|nt
init|=
operator|new
name|NetworkTopology
argument_list|()
decl_stmt|;
name|Node
name|r1n1
init|=
operator|new
name|NodeBase
argument_list|(
literal|"/default/rack1/node1"
argument_list|)
decl_stmt|;
name|nt
operator|.
name|add
argument_list|(
name|r1n1
argument_list|)
expr_stmt|;
name|Node
name|r1n2
init|=
operator|new
name|NodeBase
argument_list|(
literal|"/default/rack1/node2"
argument_list|)
decl_stmt|;
name|nt
operator|.
name|add
argument_list|(
name|r1n2
argument_list|)
expr_stmt|;
name|Node
name|r2n3
init|=
operator|new
name|NodeBase
argument_list|(
literal|"/default/rack2/node3"
argument_list|)
decl_stmt|;
name|nt
operator|.
name|add
argument_list|(
name|r2n3
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"r1n1 parent: "
operator|+
name|r1n1
operator|.
name|getParent
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"r1n2 parent: "
operator|+
name|r1n2
operator|.
name|getParent
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"r2n3 parent: "
operator|+
name|r2n3
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
comment|// Same host
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|JobInProgress
operator|.
name|getMatchingLevelForNodes
argument_list|(
name|r1n1
argument_list|,
name|r1n1
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// Same rack
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|JobInProgress
operator|.
name|getMatchingLevelForNodes
argument_list|(
name|r1n1
argument_list|,
name|r1n2
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// Different rack
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|JobInProgress
operator|.
name|getMatchingLevelForNodes
argument_list|(
name|r1n1
argument_list|,
name|r2n3
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

