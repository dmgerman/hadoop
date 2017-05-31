begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|SECONDS
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
name|HashMap
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
name|List
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ScheduledThreadPoolExecutor
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
name|ThreadFactory
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|service
operator|.
name|AbstractService
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
name|concurrent
operator|.
name|HadoopScheduledThreadPoolExecutor
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
name|proto
operator|.
name|YarnServerNodemanagerRecoveryProtos
operator|.
name|DeletionServiceDeleteTaskProto
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
name|server
operator|.
name|nodemanager
operator|.
name|api
operator|.
name|impl
operator|.
name|pb
operator|.
name|NMProtoUtils
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|deletion
operator|.
name|recovery
operator|.
name|DeletionTaskRecoveryInfo
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|deletion
operator|.
name|task
operator|.
name|DeletionTask
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
name|server
operator|.
name|nodemanager
operator|.
name|recovery
operator|.
name|NMNullStateStoreService
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
name|server
operator|.
name|nodemanager
operator|.
name|recovery
operator|.
name|NMStateStoreService
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
import|;
end_import

begin_class
DECL|class|DeletionService
specifier|public
class|class
name|DeletionService
extends|extends
name|AbstractService
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
name|DeletionService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|debugDelay
specifier|private
name|int
name|debugDelay
decl_stmt|;
DECL|field|containerExecutor
specifier|private
specifier|final
name|ContainerExecutor
name|containerExecutor
decl_stmt|;
DECL|field|stateStore
specifier|private
specifier|final
name|NMStateStoreService
name|stateStore
decl_stmt|;
DECL|field|sched
specifier|private
name|ScheduledThreadPoolExecutor
name|sched
decl_stmt|;
DECL|field|nextTaskId
specifier|private
name|AtomicInteger
name|nextTaskId
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|DeletionService (ContainerExecutor exec)
specifier|public
name|DeletionService
parameter_list|(
name|ContainerExecutor
name|exec
parameter_list|)
block|{
name|this
argument_list|(
name|exec
argument_list|,
operator|new
name|NMNullStateStoreService
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|DeletionService (ContainerExecutor containerExecutor, NMStateStoreService stateStore)
specifier|public
name|DeletionService
parameter_list|(
name|ContainerExecutor
name|containerExecutor
parameter_list|,
name|NMStateStoreService
name|stateStore
parameter_list|)
block|{
name|super
argument_list|(
name|DeletionService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerExecutor
operator|=
name|containerExecutor
expr_stmt|;
name|this
operator|.
name|debugDelay
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|stateStore
operator|=
name|stateStore
expr_stmt|;
block|}
DECL|method|getDebugDelay ()
specifier|public
name|int
name|getDebugDelay
parameter_list|()
block|{
return|return
name|debugDelay
return|;
block|}
DECL|method|getContainerExecutor ()
specifier|public
name|ContainerExecutor
name|getContainerExecutor
parameter_list|()
block|{
return|return
name|containerExecutor
return|;
block|}
DECL|method|getStateStore ()
specifier|public
name|NMStateStoreService
name|getStateStore
parameter_list|()
block|{
return|return
name|stateStore
return|;
block|}
DECL|method|delete (DeletionTask deletionTask)
specifier|public
name|void
name|delete
parameter_list|(
name|DeletionTask
name|deletionTask
parameter_list|)
block|{
if|if
condition|(
name|debugDelay
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Scheduling DeletionTask (delay %d) : %s"
argument_list|,
name|debugDelay
argument_list|,
name|deletionTask
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|recordDeletionTaskInStateStore
argument_list|(
name|deletionTask
argument_list|)
expr_stmt|;
name|sched
operator|.
name|schedule
argument_list|(
name|deletionTask
argument_list|,
name|debugDelay
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|recover (NMStateStoreService.RecoveredDeletionServiceState state)
specifier|private
name|void
name|recover
parameter_list|(
name|NMStateStoreService
operator|.
name|RecoveredDeletionServiceState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|DeletionServiceDeleteTaskProto
argument_list|>
name|taskProtos
init|=
name|state
operator|.
name|getTasks
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|DeletionTaskRecoveryInfo
argument_list|>
name|idToInfoMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|taskProtos
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Integer
argument_list|>
name|successorTasks
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DeletionServiceDeleteTaskProto
name|proto
range|:
name|taskProtos
control|)
block|{
name|DeletionTaskRecoveryInfo
name|info
init|=
name|NMProtoUtils
operator|.
name|convertProtoToDeletionTaskRecoveryInfo
argument_list|(
name|proto
argument_list|,
name|this
argument_list|)
decl_stmt|;
name|idToInfoMap
operator|.
name|put
argument_list|(
name|info
operator|.
name|getTask
argument_list|()
operator|.
name|getTaskId
argument_list|()
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|nextTaskId
operator|.
name|set
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|nextTaskId
operator|.
name|get
argument_list|()
argument_list|,
name|info
operator|.
name|getTask
argument_list|()
operator|.
name|getTaskId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|successorTasks
operator|.
name|addAll
argument_list|(
name|info
operator|.
name|getSuccessorTaskIds
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// restore the task dependencies and schedule the deletion tasks that
comment|// have no predecessors
specifier|final
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|DeletionTaskRecoveryInfo
name|info
range|:
name|idToInfoMap
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|Integer
name|successorId
range|:
name|info
operator|.
name|getSuccessorTaskIds
argument_list|()
control|)
block|{
name|DeletionTaskRecoveryInfo
name|successor
init|=
name|idToInfoMap
operator|.
name|get
argument_list|(
name|successorId
argument_list|)
decl_stmt|;
if|if
condition|(
name|successor
operator|!=
literal|null
condition|)
block|{
name|info
operator|.
name|getTask
argument_list|()
operator|.
name|addDeletionTaskDependency
argument_list|(
name|successor
operator|.
name|getTask
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to locate dependency task for deletion task "
operator|+
name|info
operator|.
name|getTask
argument_list|()
operator|.
name|getTaskId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|successorTasks
operator|.
name|contains
argument_list|(
name|info
operator|.
name|getTask
argument_list|()
operator|.
name|getTaskId
argument_list|()
argument_list|)
condition|)
block|{
name|long
name|msecTilDeletion
init|=
name|info
operator|.
name|getDeletionTimestamp
argument_list|()
operator|-
name|now
decl_stmt|;
name|sched
operator|.
name|schedule
argument_list|(
name|info
operator|.
name|getTask
argument_list|()
argument_list|,
name|msecTilDeletion
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|generateTaskId ()
specifier|private
name|int
name|generateTaskId
parameter_list|()
block|{
comment|// get the next ID but avoid an invalid ID
name|int
name|taskId
init|=
name|nextTaskId
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
while|while
condition|(
name|taskId
operator|==
name|DeletionTask
operator|.
name|INVALID_TASK_ID
condition|)
block|{
name|taskId
operator|=
name|nextTaskId
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
return|return
name|taskId
return|;
block|}
DECL|method|recordDeletionTaskInStateStore (DeletionTask task)
specifier|private
name|void
name|recordDeletionTaskInStateStore
parameter_list|(
name|DeletionTask
name|task
parameter_list|)
block|{
if|if
condition|(
operator|!
name|stateStore
operator|.
name|canRecover
argument_list|()
condition|)
block|{
comment|// optimize the case where we aren't really recording
return|return;
block|}
if|if
condition|(
name|task
operator|.
name|getTaskId
argument_list|()
operator|!=
name|DeletionTask
operator|.
name|INVALID_TASK_ID
condition|)
block|{
return|return;
comment|// task already recorded
block|}
name|task
operator|.
name|setTaskId
argument_list|(
name|generateTaskId
argument_list|()
argument_list|)
expr_stmt|;
comment|// store successors first to ensure task IDs have been generated for them
name|DeletionTask
index|[]
name|successors
init|=
name|task
operator|.
name|getSuccessorTasks
argument_list|()
decl_stmt|;
for|for
control|(
name|DeletionTask
name|successor
range|:
name|successors
control|)
block|{
name|recordDeletionTaskInStateStore
argument_list|(
name|successor
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|stateStore
operator|.
name|storeDeletionTask
argument_list|(
name|task
operator|.
name|getTaskId
argument_list|()
argument_list|,
name|task
operator|.
name|convertDeletionTaskToProto
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to store deletion task "
operator|+
name|task
operator|.
name|getTaskId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|ThreadFactory
name|tf
init|=
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"DeletionService #%d"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
name|sched
operator|=
operator|new
name|HadoopScheduledThreadPoolExecutor
argument_list|(
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_DELETE_THREAD_COUNT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_DELETE_THREAD_COUNT
argument_list|)
argument_list|,
name|tf
argument_list|)
expr_stmt|;
name|debugDelay
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|DEBUG_NM_DELETE_DELAY_SEC
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sched
operator|=
operator|new
name|HadoopScheduledThreadPoolExecutor
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_NM_DELETE_THREAD_COUNT
argument_list|,
name|tf
argument_list|)
expr_stmt|;
block|}
name|sched
operator|.
name|setExecuteExistingDelayedTasksAfterShutdownPolicy
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|sched
operator|.
name|setKeepAliveTime
argument_list|(
literal|60L
argument_list|,
name|SECONDS
argument_list|)
expr_stmt|;
if|if
condition|(
name|stateStore
operator|.
name|canRecover
argument_list|()
condition|)
block|{
name|recover
argument_list|(
name|stateStore
operator|.
name|loadDeletionServiceState
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|sched
operator|!=
literal|null
condition|)
block|{
name|sched
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|boolean
name|terminated
init|=
literal|false
decl_stmt|;
try|try
block|{
name|terminated
operator|=
name|sched
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{ }
if|if
condition|(
operator|!
name|terminated
condition|)
block|{
name|sched
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Determine if the service has completely stopped.    * Used only by unit tests    * @return true if service has completely stopped    */
annotation|@
name|Private
DECL|method|isTerminated ()
specifier|public
name|boolean
name|isTerminated
parameter_list|()
block|{
return|return
name|getServiceState
argument_list|()
operator|==
name|STATE
operator|.
name|STOPPED
operator|&&
name|sched
operator|.
name|isTerminated
argument_list|()
return|;
block|}
block|}
end_class

end_unit

