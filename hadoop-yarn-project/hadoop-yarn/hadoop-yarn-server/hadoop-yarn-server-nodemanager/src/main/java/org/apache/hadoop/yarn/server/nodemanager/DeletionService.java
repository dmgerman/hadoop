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
name|ArrayList
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
name|Iterator
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
name|executor
operator|.
name|DeletionAsUserContext
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
operator|.
name|RecoveredDeletionServiceState
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
name|annotations
operator|.
name|VisibleForTesting
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
DECL|field|exec
specifier|private
specifier|final
name|ContainerExecutor
name|exec
decl_stmt|;
DECL|field|sched
specifier|private
name|ScheduledThreadPoolExecutor
name|sched
decl_stmt|;
DECL|field|lfs
specifier|private
specifier|static
specifier|final
name|FileContext
name|lfs
init|=
name|getLfs
argument_list|()
decl_stmt|;
DECL|field|stateStore
specifier|private
specifier|final
name|NMStateStoreService
name|stateStore
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
DECL|method|getLfs ()
specifier|static
specifier|final
name|FileContext
name|getLfs
parameter_list|()
block|{
try|try
block|{
return|return
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedFileSystemException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
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
DECL|method|DeletionService (ContainerExecutor exec, NMStateStoreService stateStore)
specifier|public
name|DeletionService
parameter_list|(
name|ContainerExecutor
name|exec
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
name|exec
operator|=
name|exec
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
comment|/**    * Delete the path(s) as this user.    * @param user The user to delete as, or the JVM user if null    * @param subDir the sub directory name    * @param baseDirs the base directories which contains the subDir's    */
DECL|method|delete (String user, Path subDir, Path... baseDirs)
specifier|public
name|void
name|delete
parameter_list|(
name|String
name|user
parameter_list|,
name|Path
name|subDir
parameter_list|,
name|Path
modifier|...
name|baseDirs
parameter_list|)
block|{
comment|// TODO if parent owned by NM, rename within parent inline
if|if
condition|(
name|debugDelay
operator|!=
operator|-
literal|1
condition|)
block|{
name|List
argument_list|<
name|Path
argument_list|>
name|baseDirList
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|baseDirs
operator|!=
literal|null
operator|&&
name|baseDirs
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|baseDirList
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|baseDirs
argument_list|)
expr_stmt|;
block|}
name|FileDeletionTask
name|task
init|=
operator|new
name|FileDeletionTask
argument_list|(
name|this
argument_list|,
name|user
argument_list|,
name|subDir
argument_list|,
name|baseDirList
argument_list|)
decl_stmt|;
name|recordDeletionTaskInStateStore
argument_list|(
name|task
argument_list|)
expr_stmt|;
name|sched
operator|.
name|schedule
argument_list|(
name|task
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
DECL|method|scheduleFileDeletionTask (FileDeletionTask fileDeletionTask)
specifier|public
name|void
name|scheduleFileDeletionTask
parameter_list|(
name|FileDeletionTask
name|fileDeletionTask
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
name|recordDeletionTaskInStateStore
argument_list|(
name|fileDeletionTask
argument_list|)
expr_stmt|;
name|sched
operator|.
name|schedule
argument_list|(
name|fileDeletionTask
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
specifier|protected
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
block|{       }
if|if
condition|(
name|terminated
operator|!=
literal|true
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
DECL|class|FileDeletionTask
specifier|public
specifier|static
class|class
name|FileDeletionTask
implements|implements
name|Runnable
block|{
DECL|field|INVALID_TASK_ID
specifier|public
specifier|static
specifier|final
name|int
name|INVALID_TASK_ID
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|taskId
specifier|private
name|int
name|taskId
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|subDir
specifier|private
specifier|final
name|Path
name|subDir
decl_stmt|;
DECL|field|baseDirs
specifier|private
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|baseDirs
decl_stmt|;
DECL|field|numberOfPendingPredecessorTasks
specifier|private
specifier|final
name|AtomicInteger
name|numberOfPendingPredecessorTasks
decl_stmt|;
DECL|field|successorTaskSet
specifier|private
specifier|final
name|Set
argument_list|<
name|FileDeletionTask
argument_list|>
name|successorTaskSet
decl_stmt|;
DECL|field|delService
specifier|private
specifier|final
name|DeletionService
name|delService
decl_stmt|;
comment|// By default all tasks will start as success=true; however if any of
comment|// the dependent task fails then it will be marked as false in
comment|// fileDeletionTaskFinished().
DECL|field|success
specifier|private
name|boolean
name|success
decl_stmt|;
DECL|method|FileDeletionTask (DeletionService delService, String user, Path subDir, List<Path> baseDirs)
specifier|private
name|FileDeletionTask
parameter_list|(
name|DeletionService
name|delService
parameter_list|,
name|String
name|user
parameter_list|,
name|Path
name|subDir
parameter_list|,
name|List
argument_list|<
name|Path
argument_list|>
name|baseDirs
parameter_list|)
block|{
name|this
argument_list|(
name|INVALID_TASK_ID
argument_list|,
name|delService
argument_list|,
name|user
argument_list|,
name|subDir
argument_list|,
name|baseDirs
argument_list|)
expr_stmt|;
block|}
DECL|method|FileDeletionTask (int taskId, DeletionService delService, String user, Path subDir, List<Path> baseDirs)
specifier|private
name|FileDeletionTask
parameter_list|(
name|int
name|taskId
parameter_list|,
name|DeletionService
name|delService
parameter_list|,
name|String
name|user
parameter_list|,
name|Path
name|subDir
parameter_list|,
name|List
argument_list|<
name|Path
argument_list|>
name|baseDirs
parameter_list|)
block|{
name|this
operator|.
name|taskId
operator|=
name|taskId
expr_stmt|;
name|this
operator|.
name|delService
operator|=
name|delService
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|subDir
operator|=
name|subDir
expr_stmt|;
name|this
operator|.
name|baseDirs
operator|=
name|baseDirs
expr_stmt|;
name|this
operator|.
name|successorTaskSet
operator|=
operator|new
name|HashSet
argument_list|<
name|FileDeletionTask
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|numberOfPendingPredecessorTasks
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
comment|/**      * increments and returns pending predecessor task count      */
DECL|method|incrementAndGetPendingPredecessorTasks ()
specifier|public
name|int
name|incrementAndGetPendingPredecessorTasks
parameter_list|()
block|{
return|return
name|numberOfPendingPredecessorTasks
operator|.
name|incrementAndGet
argument_list|()
return|;
block|}
comment|/**      * decrements and returns pending predecessor task count      */
DECL|method|decrementAndGetPendingPredecessorTasks ()
specifier|public
name|int
name|decrementAndGetPendingPredecessorTasks
parameter_list|()
block|{
return|return
name|numberOfPendingPredecessorTasks
operator|.
name|decrementAndGet
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|this
operator|.
name|user
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getSubDir ()
specifier|public
name|Path
name|getSubDir
parameter_list|()
block|{
return|return
name|this
operator|.
name|subDir
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getBaseDirs ()
specifier|public
name|List
argument_list|<
name|Path
argument_list|>
name|getBaseDirs
parameter_list|()
block|{
return|return
name|this
operator|.
name|baseDirs
return|;
block|}
DECL|method|setSuccess (boolean success)
specifier|public
specifier|synchronized
name|void
name|setSuccess
parameter_list|(
name|boolean
name|success
parameter_list|)
block|{
name|this
operator|.
name|success
operator|=
name|success
expr_stmt|;
block|}
DECL|method|getSucess ()
specifier|public
specifier|synchronized
name|boolean
name|getSucess
parameter_list|()
block|{
return|return
name|this
operator|.
name|success
return|;
block|}
DECL|method|getSuccessorTasks ()
specifier|public
specifier|synchronized
name|FileDeletionTask
index|[]
name|getSuccessorTasks
parameter_list|()
block|{
name|FileDeletionTask
index|[]
name|successors
init|=
operator|new
name|FileDeletionTask
index|[
name|successorTaskSet
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
name|successorTaskSet
operator|.
name|toArray
argument_list|(
name|successors
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
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
name|this
argument_list|)
expr_stmt|;
block|}
name|boolean
name|error
init|=
literal|false
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|user
condition|)
block|{
if|if
condition|(
name|baseDirs
operator|==
literal|null
operator|||
name|baseDirs
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"NM deleting absolute path : "
operator|+
name|subDir
argument_list|)
expr_stmt|;
try|try
block|{
name|lfs
operator|.
name|delete
argument_list|(
name|subDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|error
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to delete "
operator|+
name|subDir
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|Path
name|baseDir
range|:
name|baseDirs
control|)
block|{
name|Path
name|del
init|=
name|subDir
operator|==
literal|null
condition|?
name|baseDir
else|:
operator|new
name|Path
argument_list|(
name|baseDir
argument_list|,
name|subDir
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"NM deleting path : "
operator|+
name|del
argument_list|)
expr_stmt|;
try|try
block|{
name|lfs
operator|.
name|delete
argument_list|(
name|del
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|error
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to delete "
operator|+
name|subDir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Deleting path: ["
operator|+
name|subDir
operator|+
literal|"] as user: ["
operator|+
name|user
operator|+
literal|"]"
argument_list|)
expr_stmt|;
if|if
condition|(
name|baseDirs
operator|==
literal|null
operator|||
name|baseDirs
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|delService
operator|.
name|exec
operator|.
name|deleteAsUser
argument_list|(
operator|new
name|DeletionAsUserContext
operator|.
name|Builder
argument_list|()
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
operator|.
name|setSubDir
argument_list|(
name|subDir
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|delService
operator|.
name|exec
operator|.
name|deleteAsUser
argument_list|(
operator|new
name|DeletionAsUserContext
operator|.
name|Builder
argument_list|()
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
operator|.
name|setSubDir
argument_list|(
name|subDir
argument_list|)
operator|.
name|setBasedirs
argument_list|(
name|baseDirs
operator|.
name|toArray
argument_list|(
operator|new
name|Path
index|[
literal|0
index|]
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
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
name|error
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to delete as user "
operator|+
name|user
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|error
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to delete as user "
operator|+
name|user
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|error
condition|)
block|{
name|setSuccess
argument_list|(
operator|!
name|error
argument_list|)
expr_stmt|;
block|}
name|fileDeletionTaskFinished
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"\nFileDeletionTask : "
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"  user : "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|user
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"  subDir : "
argument_list|)
operator|.
name|append
argument_list|(
name|subDir
operator|==
literal|null
condition|?
literal|"null"
else|:
name|subDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"  baseDir : "
argument_list|)
expr_stmt|;
if|if
condition|(
name|baseDirs
operator|==
literal|null
operator|||
name|baseDirs
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|Path
name|baseDir
range|:
name|baseDirs
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|baseDir
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * If there is a task dependency between say tasks 1,2,3 such that      * task2 and task3 can be started only after task1 then we should define      * task2 and task3 as successor tasks for task1.      * Note:- Task dependency should be defined prior to      * @param successorTask      */
DECL|method|addFileDeletionTaskDependency ( FileDeletionTask successorTask)
specifier|public
specifier|synchronized
name|void
name|addFileDeletionTaskDependency
parameter_list|(
name|FileDeletionTask
name|successorTask
parameter_list|)
block|{
if|if
condition|(
name|successorTaskSet
operator|.
name|add
argument_list|(
name|successorTask
argument_list|)
condition|)
block|{
name|successorTask
operator|.
name|incrementAndGetPendingPredecessorTasks
argument_list|()
expr_stmt|;
block|}
block|}
comment|/*      * This is called when      * 1) Current file deletion task ran and finished.      * 2) This can be even directly called by predecessor task if one of the      * dependent tasks of it has failed marking its success = false.        */
DECL|method|fileDeletionTaskFinished ()
specifier|private
specifier|synchronized
name|void
name|fileDeletionTaskFinished
parameter_list|()
block|{
try|try
block|{
name|delService
operator|.
name|stateStore
operator|.
name|removeDeletionTask
argument_list|(
name|taskId
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
literal|"Unable to remove deletion task "
operator|+
name|taskId
operator|+
literal|" from state store"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|FileDeletionTask
argument_list|>
name|successorTaskI
init|=
name|this
operator|.
name|successorTaskSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|successorTaskI
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|FileDeletionTask
name|successorTask
init|=
name|successorTaskI
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|successorTask
operator|.
name|setSuccess
argument_list|(
name|success
argument_list|)
expr_stmt|;
block|}
name|int
name|count
init|=
name|successorTask
operator|.
name|decrementAndGetPendingPredecessorTasks
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|successorTask
operator|.
name|getSucess
argument_list|()
condition|)
block|{
name|successorTask
operator|.
name|delService
operator|.
name|scheduleFileDeletionTask
argument_list|(
name|successorTask
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|successorTask
operator|.
name|fileDeletionTaskFinished
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**    * Helper method to create file deletion task. To be used only if we need    * a way to define dependencies between deletion tasks.    * @param user user on whose behalf this task is suppose to run    * @param subDir sub directory as required in     * {@link DeletionService#delete(String, Path, Path...)}    * @param baseDirs base directories as required in    * {@link DeletionService#delete(String, Path, Path...)}    */
DECL|method|createFileDeletionTask (String user, Path subDir, Path[] baseDirs)
specifier|public
name|FileDeletionTask
name|createFileDeletionTask
parameter_list|(
name|String
name|user
parameter_list|,
name|Path
name|subDir
parameter_list|,
name|Path
index|[]
name|baseDirs
parameter_list|)
block|{
return|return
operator|new
name|FileDeletionTask
argument_list|(
name|this
argument_list|,
name|user
argument_list|,
name|subDir
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|baseDirs
argument_list|)
argument_list|)
return|;
block|}
DECL|method|recover (RecoveredDeletionServiceState state)
specifier|private
name|void
name|recover
parameter_list|(
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
argument_list|<
name|Integer
argument_list|,
name|DeletionTaskRecoveryInfo
argument_list|>
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
argument_list|<
name|Integer
argument_list|>
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
name|parseTaskProto
argument_list|(
name|proto
argument_list|)
decl_stmt|;
name|idToInfoMap
operator|.
name|put
argument_list|(
name|info
operator|.
name|task
operator|.
name|taskId
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
name|task
operator|.
name|taskId
argument_list|)
argument_list|)
expr_stmt|;
name|successorTasks
operator|.
name|addAll
argument_list|(
name|info
operator|.
name|successorTaskIds
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
name|successorTaskIds
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
name|task
operator|.
name|addFileDeletionTaskDependency
argument_list|(
name|successor
operator|.
name|task
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
name|task
operator|.
name|taskId
operator|+
literal|" at "
operator|+
name|info
operator|.
name|task
operator|.
name|getSubDir
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
name|task
operator|.
name|taskId
argument_list|)
condition|)
block|{
name|long
name|msecTilDeletion
init|=
name|info
operator|.
name|deletionTimestamp
operator|-
name|now
decl_stmt|;
name|sched
operator|.
name|schedule
argument_list|(
name|info
operator|.
name|task
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
DECL|method|parseTaskProto ( DeletionServiceDeleteTaskProto proto)
specifier|private
name|DeletionTaskRecoveryInfo
name|parseTaskProto
parameter_list|(
name|DeletionServiceDeleteTaskProto
name|proto
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|taskId
init|=
name|proto
operator|.
name|getId
argument_list|()
decl_stmt|;
name|String
name|user
init|=
name|proto
operator|.
name|hasUser
argument_list|()
condition|?
name|proto
operator|.
name|getUser
argument_list|()
else|:
literal|null
decl_stmt|;
name|Path
name|subdir
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|basePaths
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|proto
operator|.
name|hasSubdir
argument_list|()
condition|)
block|{
name|subdir
operator|=
operator|new
name|Path
argument_list|(
name|proto
operator|.
name|getSubdir
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|basedirs
init|=
name|proto
operator|.
name|getBasedirsList
argument_list|()
decl_stmt|;
if|if
condition|(
name|basedirs
operator|!=
literal|null
operator|&&
name|basedirs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|basePaths
operator|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|(
name|basedirs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|basedir
range|:
name|basedirs
control|)
block|{
name|basePaths
operator|.
name|add
argument_list|(
operator|new
name|Path
argument_list|(
name|basedir
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|FileDeletionTask
name|task
init|=
operator|new
name|FileDeletionTask
argument_list|(
name|taskId
argument_list|,
name|this
argument_list|,
name|user
argument_list|,
name|subdir
argument_list|,
name|basePaths
argument_list|)
decl_stmt|;
return|return
operator|new
name|DeletionTaskRecoveryInfo
argument_list|(
name|task
argument_list|,
name|proto
operator|.
name|getSuccessorIdsList
argument_list|()
argument_list|,
name|proto
operator|.
name|getDeletionTime
argument_list|()
argument_list|)
return|;
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
name|FileDeletionTask
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
DECL|method|recordDeletionTaskInStateStore (FileDeletionTask task)
specifier|private
name|void
name|recordDeletionTaskInStateStore
parameter_list|(
name|FileDeletionTask
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
name|taskId
operator|!=
name|FileDeletionTask
operator|.
name|INVALID_TASK_ID
condition|)
block|{
return|return;
comment|// task already recorded
block|}
name|task
operator|.
name|taskId
operator|=
name|generateTaskId
argument_list|()
expr_stmt|;
name|FileDeletionTask
index|[]
name|successors
init|=
name|task
operator|.
name|getSuccessorTasks
argument_list|()
decl_stmt|;
comment|// store successors first to ensure task IDs have been generated for them
for|for
control|(
name|FileDeletionTask
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
name|DeletionServiceDeleteTaskProto
operator|.
name|Builder
name|builder
init|=
name|DeletionServiceDeleteTaskProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setId
argument_list|(
name|task
operator|.
name|taskId
argument_list|)
expr_stmt|;
if|if
condition|(
name|task
operator|.
name|getUser
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setUser
argument_list|(
name|task
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|task
operator|.
name|getSubDir
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setSubdir
argument_list|(
name|task
operator|.
name|getSubDir
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setDeletionTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|debugDelay
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|task
operator|.
name|getBaseDirs
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Path
name|dir
range|:
name|task
operator|.
name|getBaseDirs
argument_list|()
control|)
block|{
name|builder
operator|.
name|addBasedirs
argument_list|(
name|dir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|FileDeletionTask
name|successor
range|:
name|successors
control|)
block|{
name|builder
operator|.
name|addSuccessorIds
argument_list|(
name|successor
operator|.
name|taskId
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
name|taskId
argument_list|,
name|builder
operator|.
name|build
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
name|taskId
operator|+
literal|" for "
operator|+
name|task
operator|.
name|getSubDir
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|DeletionTaskRecoveryInfo
specifier|private
specifier|static
class|class
name|DeletionTaskRecoveryInfo
block|{
DECL|field|task
name|FileDeletionTask
name|task
decl_stmt|;
DECL|field|successorTaskIds
name|List
argument_list|<
name|Integer
argument_list|>
name|successorTaskIds
decl_stmt|;
DECL|field|deletionTimestamp
name|long
name|deletionTimestamp
decl_stmt|;
DECL|method|DeletionTaskRecoveryInfo (FileDeletionTask task, List<Integer> successorTaskIds, long deletionTimestamp)
specifier|public
name|DeletionTaskRecoveryInfo
parameter_list|(
name|FileDeletionTask
name|task
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|successorTaskIds
parameter_list|,
name|long
name|deletionTimestamp
parameter_list|)
block|{
name|this
operator|.
name|task
operator|=
name|task
expr_stmt|;
name|this
operator|.
name|successorTaskIds
operator|=
name|successorTaskIds
expr_stmt|;
name|this
operator|.
name|deletionTimestamp
operator|=
name|deletionTimestamp
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

