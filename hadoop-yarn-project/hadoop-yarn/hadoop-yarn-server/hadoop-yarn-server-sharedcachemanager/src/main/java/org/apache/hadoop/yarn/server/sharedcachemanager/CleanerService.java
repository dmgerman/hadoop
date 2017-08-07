begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.sharedcachemanager
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
name|sharedcachemanager
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
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
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
name|ScheduledExecutorService
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
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReentrantLock
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
name|HadoopIllegalArgumentException
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Evolving
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
name|FSDataOutputStream
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
name|service
operator|.
name|CompositeService
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
name|HadoopExecutors
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
name|exceptions
operator|.
name|YarnException
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
name|sharedcachemanager
operator|.
name|metrics
operator|.
name|CleanerMetrics
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
name|sharedcachemanager
operator|.
name|store
operator|.
name|SCMStore
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

begin_comment
comment|/**  * The cleaner service that maintains the shared cache area, and cleans up stale  * entries on a regular basis.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Evolving
DECL|class|CleanerService
specifier|public
class|class
name|CleanerService
extends|extends
name|CompositeService
block|{
comment|/**    * The name of the global cleaner lock that the cleaner creates to indicate    * that a cleaning process is in progress.    */
DECL|field|GLOBAL_CLEANER_PID
specifier|public
specifier|static
specifier|final
name|String
name|GLOBAL_CLEANER_PID
init|=
literal|".cleaner_pid"
decl_stmt|;
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
name|CleanerService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|metrics
specifier|private
name|CleanerMetrics
name|metrics
decl_stmt|;
DECL|field|scheduledExecutor
specifier|private
name|ScheduledExecutorService
name|scheduledExecutor
decl_stmt|;
DECL|field|store
specifier|private
specifier|final
name|SCMStore
name|store
decl_stmt|;
DECL|field|cleanerTaskLock
specifier|private
specifier|final
name|Lock
name|cleanerTaskLock
decl_stmt|;
DECL|method|CleanerService (SCMStore store)
specifier|public
name|CleanerService
parameter_list|(
name|SCMStore
name|store
parameter_list|)
block|{
name|super
argument_list|(
literal|"CleanerService"
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|cleanerTaskLock
operator|=
operator|new
name|ReentrantLock
argument_list|()
expr_stmt|;
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
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
comment|// create scheduler executor service that services the cleaner tasks
comment|// use 2 threads to accommodate the on-demand tasks and reduce the chance of
comment|// back-to-back runs
name|ThreadFactory
name|tf
init|=
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"Shared cache cleaner"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|scheduledExecutor
operator|=
name|HadoopExecutors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|2
argument_list|,
name|tf
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
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|writeGlobalCleanerPidFile
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"The global cleaner pid file already exists! "
operator|+
literal|"It appears there is another CleanerService running in the cluster"
argument_list|)
throw|;
block|}
name|this
operator|.
name|metrics
operator|=
name|CleanerMetrics
operator|.
name|getInstance
argument_list|()
expr_stmt|;
comment|// Start dependent services (i.e. AppChecker)
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
name|Runnable
name|task
init|=
name|CleanerTask
operator|.
name|create
argument_list|(
name|conf
argument_list|,
name|store
argument_list|,
name|metrics
argument_list|,
name|cleanerTaskLock
argument_list|)
decl_stmt|;
name|long
name|periodInMinutes
init|=
name|getPeriod
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|scheduledExecutor
operator|.
name|scheduleAtFixedRate
argument_list|(
name|task
argument_list|,
name|getInitialDelay
argument_list|(
name|conf
argument_list|)
argument_list|,
name|periodInMinutes
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Scheduled the shared cache cleaner task to run every "
operator|+
name|periodInMinutes
operator|+
literal|" minutes."
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down the background thread."
argument_list|)
expr_stmt|;
name|scheduledExecutor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|scheduledExecutor
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"The background thread stopped."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Gave up waiting for the cleaner task to shutdown."
argument_list|)
expr_stmt|;
block|}
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
literal|"The cleaner service was interrupted while shutting down the task."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|removeGlobalCleanerPidFile
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Execute an on-demand cleaner task.    */
DECL|method|runCleanerTask ()
specifier|protected
name|void
name|runCleanerTask
parameter_list|()
block|{
name|Runnable
name|task
init|=
name|CleanerTask
operator|.
name|create
argument_list|(
name|conf
argument_list|,
name|store
argument_list|,
name|metrics
argument_list|,
name|cleanerTaskLock
argument_list|)
decl_stmt|;
comment|// this is a non-blocking call (it simply submits the task to the executor
comment|// queue and returns)
name|this
operator|.
name|scheduledExecutor
operator|.
name|execute
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
comment|/**    * To ensure there are not multiple instances of the SCM running on a given    * cluster, a global pid file is used. This file contains the hostname of the    * machine that owns the pid file.    *    * @return true if the pid file was written, false otherwise    * @throws YarnException    */
DECL|method|writeGlobalCleanerPidFile ()
specifier|private
name|boolean
name|writeGlobalCleanerPidFile
parameter_list|()
throws|throws
name|YarnException
block|{
name|String
name|root
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|SHARED_CACHE_ROOT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_SHARED_CACHE_ROOT
argument_list|)
decl_stmt|;
name|Path
name|pidPath
init|=
operator|new
name|Path
argument_list|(
name|root
argument_list|,
name|GLOBAL_CLEANER_PID
argument_list|)
decl_stmt|;
try|try
block|{
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|this
operator|.
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|pidPath
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|FSDataOutputStream
name|os
init|=
name|fs
operator|.
name|create
argument_list|(
name|pidPath
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// write the hostname and the process id in the global cleaner pid file
specifier|final
name|String
name|ID
init|=
name|ManagementFactory
operator|.
name|getRuntimeMXBean
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|os
operator|.
name|writeUTF
argument_list|(
name|ID
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// add it to the delete-on-exit to ensure it gets deleted when the JVM
comment|// exits
name|fs
operator|.
name|deleteOnExit
argument_list|(
name|pidPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Created the global cleaner pid file at "
operator|+
name|pidPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|removeGlobalCleanerPidFile ()
specifier|private
name|void
name|removeGlobalCleanerPidFile
parameter_list|()
block|{
try|try
block|{
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|this
operator|.
name|conf
argument_list|)
decl_stmt|;
name|String
name|root
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|SHARED_CACHE_ROOT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_SHARED_CACHE_ROOT
argument_list|)
decl_stmt|;
name|Path
name|pidPath
init|=
operator|new
name|Path
argument_list|(
name|root
argument_list|,
name|GLOBAL_CLEANER_PID
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|pidPath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Removed the global cleaner pid file at "
operator|+
name|pidPath
operator|.
name|toString
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
literal|"Unable to remove the global cleaner pid file! The file may need "
operator|+
literal|"to be removed manually."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getInitialDelay (Configuration conf)
specifier|private
specifier|static
name|int
name|getInitialDelay
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|int
name|initialDelayInMinutes
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|SCM_CLEANER_INITIAL_DELAY_MINS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_SCM_CLEANER_INITIAL_DELAY_MINS
argument_list|)
decl_stmt|;
comment|// negative value is invalid; use the default
if|if
condition|(
name|initialDelayInMinutes
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Negative initial delay value: "
operator|+
name|initialDelayInMinutes
operator|+
literal|". The initial delay must be greater than zero."
argument_list|)
throw|;
block|}
return|return
name|initialDelayInMinutes
return|;
block|}
DECL|method|getPeriod (Configuration conf)
specifier|private
specifier|static
name|int
name|getPeriod
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|int
name|periodInMinutes
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|SCM_CLEANER_PERIOD_MINS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_SCM_CLEANER_PERIOD_MINS
argument_list|)
decl_stmt|;
comment|// non-positive value is invalid; use the default
if|if
condition|(
name|periodInMinutes
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Non-positive period value: "
operator|+
name|periodInMinutes
operator|+
literal|". The cleaner period must be greater than or equal to zero."
argument_list|)
throw|;
block|}
return|return
name|periodInMinutes
return|;
block|}
block|}
end_class

end_unit

