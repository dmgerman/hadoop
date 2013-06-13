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
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
specifier|final
name|FileContext
name|lfs
init|=
name|getLfs
argument_list|()
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
block|}
comment|/**    *    /**    * Delete the path(s) as this user.    * @param user The user to delete as, or the JVM user if null    * @param subDir the sub directory name    * @param baseDirs the base directories which contains the subDir's    */
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
name|sched
operator|.
name|schedule
argument_list|(
operator|new
name|FileDeletion
argument_list|(
name|user
argument_list|,
name|subDir
argument_list|,
name|baseDirs
argument_list|)
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
name|ScheduledThreadPoolExecutor
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
name|ScheduledThreadPoolExecutor
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
DECL|class|FileDeletion
specifier|private
class|class
name|FileDeletion
implements|implements
name|Runnable
block|{
DECL|field|user
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|subDir
specifier|final
name|Path
name|subDir
decl_stmt|;
DECL|field|baseDirs
specifier|final
name|Path
index|[]
name|baseDirs
decl_stmt|;
DECL|method|FileDeletion (String user, Path subDir, Path[] baseDirs)
name|FileDeletion
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
name|length
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
return|return;
block|}
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
name|exec
operator|.
name|deleteAsUser
argument_list|(
name|user
argument_list|,
name|subDir
argument_list|,
name|baseDirs
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
block|}
block|}
block|}
end_class

end_unit

