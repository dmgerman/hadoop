begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|concurrent
operator|.
name|BlockingQueue
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
name|LinkedBlockingQueue
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
name|fs
operator|.
name|FSError
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
name|FileStatus
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
name|fs
operator|.
name|UnsupportedFileSystemException
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
name|MRConfig
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
name|event
operator|.
name|JobCounterUpdateEvent
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
name|TaskAttemptContainerLaunchedEvent
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
name|ContainerRemoteLaunchEvent
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
name|yarn
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
name|api
operator|.
name|ApplicationConstants
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
name|ApplicationConstants
operator|.
name|Environment
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
name|service
operator|.
name|AbstractService
import|;
end_import

begin_comment
comment|/**  * Runs the container task locally in a thread.  * Since all (sub)tasks share the same local directory, they must be executed  * sequentially in order to avoid creating/deleting the same files/dirs.  */
end_comment

begin_class
DECL|class|LocalContainerLauncher
specifier|public
class|class
name|LocalContainerLauncher
extends|extends
name|AbstractService
implements|implements
name|ContainerLauncher
block|{
DECL|field|curDir
specifier|private
specifier|static
specifier|final
name|File
name|curDir
init|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
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
name|LocalContainerLauncher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|curFC
specifier|private
name|FileContext
name|curFC
init|=
literal|null
decl_stmt|;
DECL|field|localizedFiles
specifier|private
specifier|final
name|HashSet
argument_list|<
name|File
argument_list|>
name|localizedFiles
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|AppContext
name|context
decl_stmt|;
DECL|field|umbilical
specifier|private
specifier|final
name|TaskUmbilicalProtocol
name|umbilical
decl_stmt|;
DECL|field|eventHandlingThread
specifier|private
name|Thread
name|eventHandlingThread
decl_stmt|;
DECL|field|eventQueue
specifier|private
name|BlockingQueue
argument_list|<
name|ContainerLauncherEvent
argument_list|>
name|eventQueue
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|ContainerLauncherEvent
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|LocalContainerLauncher (AppContext context, TaskUmbilicalProtocol umbilical)
specifier|public
name|LocalContainerLauncher
parameter_list|(
name|AppContext
name|context
parameter_list|,
name|TaskUmbilicalProtocol
name|umbilical
parameter_list|)
block|{
name|super
argument_list|(
name|LocalContainerLauncher
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|umbilical
operator|=
name|umbilical
expr_stmt|;
comment|// umbilical:  MRAppMaster creates (taskAttemptListener), passes to us
comment|// (TODO/FIXME:  pointless to use RPC to talk to self; should create
comment|// LocalTaskAttemptListener or similar:  implement umbilical protocol
comment|// but skip RPC stuff)
try|try
block|{
name|curFC
operator|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|curDir
operator|.
name|toURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedFileSystemException
name|ufse
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Local filesystem "
operator|+
name|curDir
operator|.
name|toURI
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" is unsupported?? (should never happen)"
argument_list|)
expr_stmt|;
block|}
comment|// Save list of files/dirs that are supposed to be present so can delete
comment|// any extras created by one task before starting subsequent task.  Note
comment|// that there's no protection against deleted or renamed localization;
comment|// users who do that get what they deserve (and will have to disable
comment|// uberization in order to run correctly).
name|File
index|[]
name|curLocalFiles
init|=
name|curDir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
name|localizedFiles
operator|=
operator|new
name|HashSet
argument_list|<
name|File
argument_list|>
argument_list|(
name|curLocalFiles
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|curLocalFiles
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|localizedFiles
operator|.
name|add
argument_list|(
name|curLocalFiles
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Relocalization note/future FIXME (per chrisdo, 20110315):  At moment,
comment|// full localization info is in AppSubmissionContext passed from client to
comment|// RM and then to NM for AM-container launch:  no difference between AM-
comment|// localization and MapTask- or ReduceTask-localization, so can assume all
comment|// OK.  Longer-term, will need to override uber-AM container-localization
comment|// request ("needed resources") with union of regular-AM-resources + task-
comment|// resources (and, if maps and reduces ever differ, then union of all three
comment|// types), OR will need localizer service/API that uber-AM can request
comment|// after running (e.g., "localizeForTask()" or "localizeForMapTask()").
block|}
DECL|method|serviceStart ()
specifier|public
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|eventHandlingThread
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|SubtaskRunner
argument_list|()
argument_list|,
literal|"uber-SubtaskRunner"
argument_list|)
expr_stmt|;
name|eventHandlingThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
DECL|method|serviceStop ()
specifier|public
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|eventHandlingThread
operator|!=
literal|null
condition|)
block|{
name|eventHandlingThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (ContainerLauncherEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|ContainerLauncherEvent
name|event
parameter_list|)
block|{
try|try
block|{
name|eventQueue
operator|.
name|put
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
comment|// FIXME? YarnRuntimeException is "for runtime exceptions only"
block|}
block|}
comment|/*    * Uber-AM lifecycle/ordering ("normal" case):    *    * - [somebody] sends TA_ASSIGNED    *   - handled by ContainerAssignedTransition (TaskAttemptImpl.java)    *     - creates "remoteTask" for us == real Task    *     - sends CONTAINER_REMOTE_LAUNCH    *     - TA: UNASSIGNED -> ASSIGNED    * - CONTAINER_REMOTE_LAUNCH handled by LocalContainerLauncher (us)    *   - sucks "remoteTask" out of TaskAttemptImpl via getRemoteTask()    *   - sends TA_CONTAINER_LAUNCHED    *     [[ elsewhere...    *       - TA_CONTAINER_LAUNCHED handled by LaunchedContainerTransition    *         - registers "remoteTask" with TaskAttemptListener (== umbilical)    *         - NUKES "remoteTask"    *         - sends T_ATTEMPT_LAUNCHED (Task: SCHEDULED -> RUNNING)    *         - TA: ASSIGNED -> RUNNING    *     ]]    *   - runs Task (runSubMap() or runSubReduce())    *     - TA can safely send TA_UPDATE since in RUNNING state    */
DECL|class|SubtaskRunner
specifier|private
class|class
name|SubtaskRunner
implements|implements
name|Runnable
block|{
DECL|field|doneWithMaps
specifier|private
name|boolean
name|doneWithMaps
init|=
literal|false
decl_stmt|;
DECL|field|finishedSubMaps
specifier|private
name|int
name|finishedSubMaps
init|=
literal|0
decl_stmt|;
DECL|method|SubtaskRunner ()
name|SubtaskRunner
parameter_list|()
block|{     }
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|ContainerLauncherEvent
name|event
init|=
literal|null
decl_stmt|;
comment|// _must_ either run subtasks sequentially or accept expense of new JVMs
comment|// (i.e., fork()), else will get weird failures when maps try to create/
comment|// write same dirname or filename:  no chdir() in Java
while|while
condition|(
operator|!
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
try|try
block|{
name|event
operator|=
name|eventQueue
operator|.
name|take
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// mostly via T_KILL? JOB_KILL?
name|LOG
operator|.
name|error
argument_list|(
literal|"Returning, interrupted : "
operator|+
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Processing the event "
operator|+
name|event
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|EventType
operator|.
name|CONTAINER_REMOTE_LAUNCH
condition|)
block|{
name|ContainerRemoteLaunchEvent
name|launchEv
init|=
operator|(
name|ContainerRemoteLaunchEvent
operator|)
name|event
decl_stmt|;
name|TaskAttemptId
name|attemptID
init|=
name|launchEv
operator|.
name|getTaskAttemptID
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
name|context
operator|.
name|getAllJobs
argument_list|()
operator|.
name|get
argument_list|(
name|attemptID
operator|.
name|getTaskId
argument_list|()
operator|.
name|getJobId
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numMapTasks
init|=
name|job
operator|.
name|getTotalMaps
argument_list|()
decl_stmt|;
name|int
name|numReduceTasks
init|=
name|job
operator|.
name|getTotalReduces
argument_list|()
decl_stmt|;
comment|// YARN (tracking) Task:
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
name|ytask
init|=
name|job
operator|.
name|getTask
argument_list|(
name|attemptID
operator|.
name|getTaskId
argument_list|()
argument_list|)
decl_stmt|;
comment|// classic mapred Task:
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|Task
name|remoteTask
init|=
name|launchEv
operator|.
name|getRemoteTask
argument_list|()
decl_stmt|;
comment|// after "launching," send launched event to task attempt to move
comment|// state from ASSIGNED to RUNNING (also nukes "remoteTask", so must
comment|// do getRemoteTask() call first)
comment|//There is no port number because we are not really talking to a task
comment|// tracker.  The shuffle is just done through local files.  So the
comment|// port number is set to -1 in this case.
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptContainerLaunchedEvent
argument_list|(
name|attemptID
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|numMapTasks
operator|==
literal|0
condition|)
block|{
name|doneWithMaps
operator|=
literal|true
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|remoteTask
operator|.
name|isMapOrReduce
argument_list|()
condition|)
block|{
name|JobCounterUpdateEvent
name|jce
init|=
operator|new
name|JobCounterUpdateEvent
argument_list|(
name|attemptID
operator|.
name|getTaskId
argument_list|()
operator|.
name|getJobId
argument_list|()
argument_list|)
decl_stmt|;
name|jce
operator|.
name|addCounterUpdate
argument_list|(
name|JobCounter
operator|.
name|TOTAL_LAUNCHED_UBERTASKS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|remoteTask
operator|.
name|isMapTask
argument_list|()
condition|)
block|{
name|jce
operator|.
name|addCounterUpdate
argument_list|(
name|JobCounter
operator|.
name|NUM_UBER_SUBMAPS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|jce
operator|.
name|addCounterUpdate
argument_list|(
name|JobCounter
operator|.
name|NUM_UBER_SUBREDUCES
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
name|jce
argument_list|)
expr_stmt|;
block|}
name|runSubtask
argument_list|(
name|remoteTask
argument_list|,
name|ytask
operator|.
name|getType
argument_list|()
argument_list|,
name|attemptID
argument_list|,
name|numMapTasks
argument_list|,
operator|(
name|numReduceTasks
operator|>
literal|0
operator|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|re
parameter_list|)
block|{
name|JobCounterUpdateEvent
name|jce
init|=
operator|new
name|JobCounterUpdateEvent
argument_list|(
name|attemptID
operator|.
name|getTaskId
argument_list|()
operator|.
name|getJobId
argument_list|()
argument_list|)
decl_stmt|;
name|jce
operator|.
name|addCounterUpdate
argument_list|(
name|JobCounter
operator|.
name|NUM_FAILED_UBERTASKS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
name|jce
argument_list|)
expr_stmt|;
comment|// this is our signal that the subtask failed in some way, so
comment|// simulate a failed JVM/container and send a container-completed
comment|// event to task attempt (i.e., move state machine from RUNNING
comment|// to FAIL_CONTAINER_CLEANUP [and ultimately to FAILED])
name|context
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
name|TA_CONTAINER_COMPLETED
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// if umbilical itself barfs (in error-handler of runSubMap()),
comment|// we're pretty much hosed, so do what YarnChild main() does
comment|// (i.e., exit clumsily--but can never happen, so no worries!)
name|LOG
operator|.
name|fatal
argument_list|(
literal|"oopsie...  this can never happen: "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|ioe
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|EventType
operator|.
name|CONTAINER_REMOTE_CLEANUP
condition|)
block|{
comment|// no container to kill, so just send "cleaned" event to task attempt
comment|// to move us from SUCCESS_CONTAINER_CLEANUP to SUCCEEDED state
comment|// (or {FAIL|KILL}_CONTAINER_CLEANUP to {FAIL|KILL}_TASK_CLEANUP)
name|context
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
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ignoring unexpected event "
operator|+
name|event
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|runSubtask (org.apache.hadoop.mapred.Task task, final TaskType taskType, TaskAttemptId attemptID, final int numMapTasks, boolean renameOutputs)
specifier|private
name|void
name|runSubtask
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|Task
name|task
parameter_list|,
specifier|final
name|TaskType
name|taskType
parameter_list|,
name|TaskAttemptId
name|attemptID
parameter_list|,
specifier|final
name|int
name|numMapTasks
parameter_list|,
name|boolean
name|renameOutputs
parameter_list|)
throws|throws
name|RuntimeException
throws|,
name|IOException
block|{
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|TaskAttemptID
name|classicAttemptID
init|=
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|attemptID
argument_list|)
decl_stmt|;
try|try
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|(
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|TASK_ID
argument_list|,
name|task
operator|.
name|getTaskID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|TASK_ATTEMPT_ID
argument_list|,
name|classicAttemptID
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|JobContext
operator|.
name|TASK_ISMAP
argument_list|,
operator|(
name|taskType
operator|==
name|TaskType
operator|.
name|MAP
operator|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|JobContext
operator|.
name|TASK_PARTITION
argument_list|,
name|task
operator|.
name|getPartition
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|ID
argument_list|,
name|task
operator|.
name|getJobID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Use the AM's local dir env to generate the intermediate step
comment|// output files
name|String
index|[]
name|localSysDirs
init|=
name|StringUtils
operator|.
name|getTrimmedStrings
argument_list|(
name|System
operator|.
name|getenv
argument_list|(
name|Environment
operator|.
name|LOCAL_DIRS
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setStrings
argument_list|(
name|MRConfig
operator|.
name|LOCAL_DIR
argument_list|,
name|localSysDirs
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|MRConfig
operator|.
name|LOCAL_DIR
operator|+
literal|" for uber task: "
operator|+
name|conf
operator|.
name|get
argument_list|(
name|MRConfig
operator|.
name|LOCAL_DIR
argument_list|)
argument_list|)
expr_stmt|;
comment|// mark this as an uberized subtask so it can set task counter
comment|// (longer-term/FIXME:  could redefine as job counter and send
comment|// "JobCounterEvent" to JobImpl on [successful] completion of subtask;
comment|// will need new Job state-machine transition and JobImpl jobCounters
comment|// map to handle)
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"mapreduce.task.uberized"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// META-FIXME: do we want the extra sanity-checking (doneWithMaps,
comment|// etc.), or just assume/hope the state machine(s) and uber-AM work
comment|// as expected?
if|if
condition|(
name|taskType
operator|==
name|TaskType
operator|.
name|MAP
condition|)
block|{
if|if
condition|(
name|doneWithMaps
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"CONTAINER_REMOTE_LAUNCH contains a map task ("
operator|+
name|attemptID
operator|+
literal|"), but should be finished with maps"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
name|MapTask
name|map
init|=
operator|(
name|MapTask
operator|)
name|task
decl_stmt|;
name|map
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|map
operator|.
name|run
argument_list|(
name|conf
argument_list|,
name|umbilical
argument_list|)
expr_stmt|;
if|if
condition|(
name|renameOutputs
condition|)
block|{
name|renameMapOutputForReduce
argument_list|(
name|conf
argument_list|,
name|attemptID
argument_list|,
name|map
operator|.
name|getMapOutputFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|relocalize
argument_list|()
expr_stmt|;
if|if
condition|(
operator|++
name|finishedSubMaps
operator|==
name|numMapTasks
condition|)
block|{
name|doneWithMaps
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
comment|/* TaskType.REDUCE */
block|{
if|if
condition|(
operator|!
name|doneWithMaps
condition|)
block|{
comment|// check if event-queue empty?  whole idea of counting maps vs.
comment|// checking event queue is a tad wacky...but could enforce ordering
comment|// (assuming no "lost events") at LocalMRAppMaster [CURRENT BUG(?):
comment|// doesn't send reduce event until maps all done]
name|LOG
operator|.
name|error
argument_list|(
literal|"CONTAINER_REMOTE_LAUNCH contains a reduce task ("
operator|+
name|attemptID
operator|+
literal|"), but not yet finished with maps"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
comment|// a.k.a. "mapreduce.jobtracker.address" in LocalJobRunner:
comment|// set framework name to local to make task local
name|conf
operator|.
name|set
argument_list|(
name|MRConfig
operator|.
name|FRAMEWORK_NAME
argument_list|,
name|MRConfig
operator|.
name|LOCAL_FRAMEWORK_NAME
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRConfig
operator|.
name|MASTER_ADDRESS
argument_list|,
literal|"local"
argument_list|)
expr_stmt|;
comment|// bypass shuffle
name|ReduceTask
name|reduce
init|=
operator|(
name|ReduceTask
operator|)
name|task
decl_stmt|;
name|reduce
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|reduce
operator|.
name|run
argument_list|(
name|conf
argument_list|,
name|umbilical
argument_list|)
expr_stmt|;
comment|//relocalize();  // needed only if more than one reducer supported (is MAPREDUCE-434 fixed yet?)
block|}
block|}
catch|catch
parameter_list|(
name|FSError
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"FSError from child"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// umbilical:  MRAppMaster creates (taskAttemptListener), passes to us
name|umbilical
operator|.
name|fsError
argument_list|(
name|classicAttemptID
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exception
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception running local (uberized) 'child' : "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|exception
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|task
operator|!=
literal|null
condition|)
block|{
comment|// do cleanup for the task
name|task
operator|.
name|taskCleanup
argument_list|(
name|umbilical
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception cleaning up: "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Report back any failures, for diagnostic purposes
name|umbilical
operator|.
name|reportDiagnosticInfo
argument_list|(
name|classicAttemptID
argument_list|,
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|exception
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Error running local (uberized) 'child' : "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|throwable
argument_list|)
argument_list|)
expr_stmt|;
name|Throwable
name|tCause
init|=
name|throwable
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|String
name|cause
init|=
operator|(
name|tCause
operator|==
literal|null
operator|)
condition|?
name|throwable
operator|.
name|getMessage
argument_list|()
else|:
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|tCause
argument_list|)
decl_stmt|;
name|umbilical
operator|.
name|fatalError
argument_list|(
name|classicAttemptID
argument_list|,
name|cause
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
block|}
comment|/**      * Within the _local_ filesystem (not HDFS), all activity takes place within      * a single subdir (${local.dir}/usercache/$user/appcache/$appId/$contId/),      * and all sub-MapTasks create the same filename ("file.out").  Rename that      * to something unique (e.g., "map_0.out") to avoid collisions.      *      * Longer-term, we'll modify [something] to use TaskAttemptID-based      * filenames instead of "file.out". (All of this is entirely internal,      * so there are no particular compatibility issues.)      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|renameMapOutputForReduce (JobConf conf, TaskAttemptId mapId, MapOutputFile subMapOutputFile)
specifier|private
name|void
name|renameMapOutputForReduce
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|TaskAttemptId
name|mapId
parameter_list|,
name|MapOutputFile
name|subMapOutputFile
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|localFs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// move map output to reduce input
name|Path
name|mapOut
init|=
name|subMapOutputFile
operator|.
name|getOutputFile
argument_list|()
decl_stmt|;
name|FileStatus
name|mStatus
init|=
name|localFs
operator|.
name|getFileStatus
argument_list|(
name|mapOut
argument_list|)
decl_stmt|;
name|Path
name|reduceIn
init|=
name|subMapOutputFile
operator|.
name|getInputFileForWrite
argument_list|(
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|mapId
argument_list|)
operator|.
name|getTaskID
argument_list|()
argument_list|,
name|mStatus
operator|.
name|getLen
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Renaming map output file for task attempt "
operator|+
name|mapId
operator|.
name|toString
argument_list|()
operator|+
literal|" from original location "
operator|+
name|mapOut
operator|.
name|toString
argument_list|()
operator|+
literal|" to destination "
operator|+
name|reduceIn
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|localFs
operator|.
name|mkdirs
argument_list|(
name|reduceIn
operator|.
name|getParent
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mkdirs failed to create "
operator|+
name|reduceIn
operator|.
name|getParent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|localFs
operator|.
name|rename
argument_list|(
name|mapOut
argument_list|,
name|reduceIn
argument_list|)
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Couldn't rename "
operator|+
name|mapOut
argument_list|)
throw|;
block|}
comment|/**      * Also within the local filesystem, we need to restore the initial state      * of the directory as much as possible.  Compare current contents against      * the saved original state and nuke everything that doesn't belong, with      * the exception of the renamed map outputs.      *      * Any jobs that go out of their way to rename or delete things from the      * local directory are considered broken and deserve what they get...      */
DECL|method|relocalize ()
specifier|private
name|void
name|relocalize
parameter_list|()
block|{
name|File
index|[]
name|curLocalFiles
init|=
name|curDir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|curLocalFiles
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
if|if
condition|(
operator|!
name|localizedFiles
operator|.
name|contains
argument_list|(
name|curLocalFiles
index|[
name|j
index|]
argument_list|)
condition|)
block|{
comment|// found one that wasn't there before:  delete it
name|boolean
name|deleted
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
name|curFC
operator|!=
literal|null
condition|)
block|{
comment|// this is recursive, unlike File delete():
name|deleted
operator|=
name|curFC
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|curLocalFiles
index|[
name|j
index|]
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|deleted
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|deleted
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to delete unexpected local file/dir "
operator|+
name|curLocalFiles
index|[
name|j
index|]
operator|.
name|getName
argument_list|()
operator|+
literal|": insufficient permissions?"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|// end SubtaskRunner
block|}
end_class

end_unit

