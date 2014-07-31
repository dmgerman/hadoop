begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ScheduledFuture
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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|JobID
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
name|app
operator|.
name|ClusterInfo
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
name|hs
operator|.
name|HistoryFileManager
operator|.
name|HistoryFileInfo
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
name|hs
operator|.
name|webapp
operator|.
name|dao
operator|.
name|JobsInfo
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
name|jobhistory
operator|.
name|JHAdminConfig
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
name|util
operator|.
name|ReflectionUtils
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
name|ApplicationAttemptId
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
name|ApplicationId
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
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|exceptions
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|security
operator|.
name|client
operator|.
name|ClientToAMTokenSecretManager
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
name|util
operator|.
name|Clock
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

begin_comment
comment|/**  * Loads and manages the Job history cache.  */
end_comment

begin_class
DECL|class|JobHistory
specifier|public
class|class
name|JobHistory
extends|extends
name|AbstractService
implements|implements
name|HistoryContext
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
name|JobHistory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CONF_FILENAME_REGEX
specifier|public
specifier|static
specifier|final
name|Pattern
name|CONF_FILENAME_REGEX
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"("
operator|+
name|JobID
operator|.
name|JOBID_REGEX
operator|+
literal|")_conf.xml(?:\\.[0-9]+\\.old)?"
argument_list|)
decl_stmt|;
DECL|field|OLD_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|OLD_SUFFIX
init|=
literal|".old"
decl_stmt|;
comment|// Time interval for the move thread.
DECL|field|moveThreadInterval
specifier|private
name|long
name|moveThreadInterval
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|scheduledExecutor
specifier|private
name|ScheduledThreadPoolExecutor
name|scheduledExecutor
init|=
literal|null
decl_stmt|;
DECL|field|storage
specifier|private
name|HistoryStorage
name|storage
init|=
literal|null
decl_stmt|;
DECL|field|hsManager
specifier|private
name|HistoryFileManager
name|hsManager
init|=
literal|null
decl_stmt|;
DECL|field|futureHistoryCleaner
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|futureHistoryCleaner
init|=
literal|null
decl_stmt|;
comment|//History job cleaner interval
DECL|field|cleanerInterval
specifier|private
name|long
name|cleanerInterval
decl_stmt|;
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
name|LOG
operator|.
name|info
argument_list|(
literal|"JobHistory Init"
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|appID
operator|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|appAttemptID
operator|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
name|conf
argument_list|)
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
expr_stmt|;
name|moveThreadInterval
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_MOVE_INTERVAL_MS
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_MR_HISTORY_MOVE_INTERVAL_MS
argument_list|)
expr_stmt|;
name|hsManager
operator|=
name|createHistoryFileManager
argument_list|()
expr_stmt|;
name|hsManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
try|try
block|{
name|hsManager
operator|.
name|initExisting
argument_list|()
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
name|YarnRuntimeException
argument_list|(
literal|"Failed to intialize existing directories"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|storage
operator|=
name|createHistoryStorage
argument_list|()
expr_stmt|;
if|if
condition|(
name|storage
operator|instanceof
name|Service
condition|)
block|{
operator|(
operator|(
name|Service
operator|)
name|storage
operator|)
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
name|storage
operator|.
name|setHistoryFileManager
argument_list|(
name|hsManager
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
DECL|method|createHistoryStorage ()
specifier|protected
name|HistoryStorage
name|createHistoryStorage
parameter_list|()
block|{
return|return
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|conf
operator|.
name|getClass
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_STORAGE
argument_list|,
name|CachedHistoryStorage
operator|.
name|class
argument_list|,
name|HistoryStorage
operator|.
name|class
argument_list|)
argument_list|,
name|conf
argument_list|)
return|;
block|}
DECL|method|createHistoryFileManager ()
specifier|protected
name|HistoryFileManager
name|createHistoryFileManager
parameter_list|()
block|{
return|return
operator|new
name|HistoryFileManager
argument_list|()
return|;
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
name|hsManager
operator|.
name|start
argument_list|()
expr_stmt|;
if|if
condition|(
name|storage
operator|instanceof
name|Service
condition|)
block|{
operator|(
operator|(
name|Service
operator|)
name|storage
operator|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|scheduledExecutor
operator|=
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
literal|2
argument_list|,
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"Log Scanner/Cleaner #%d"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|scheduledExecutor
operator|.
name|scheduleAtFixedRate
argument_list|(
operator|new
name|MoveIntermediateToDoneRunnable
argument_list|()
argument_list|,
name|moveThreadInterval
argument_list|,
name|moveThreadInterval
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
comment|// Start historyCleaner
name|scheduleHistoryCleaner
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
DECL|method|getInitDelaySecs ()
specifier|protected
name|int
name|getInitDelaySecs
parameter_list|()
block|{
return|return
literal|30
return|;
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
literal|"Stopping JobHistory"
argument_list|)
expr_stmt|;
if|if
condition|(
name|scheduledExecutor
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping History Cleaner/Move To Done"
argument_list|)
expr_stmt|;
name|scheduledExecutor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|boolean
name|interrupted
init|=
literal|false
decl_stmt|;
name|long
name|currentTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|scheduledExecutor
operator|.
name|isShutdown
argument_list|()
operator|&&
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|>
name|currentTime
operator|+
literal|1000l
operator|&&
operator|!
name|interrupted
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|20
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|interrupted
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|scheduledExecutor
operator|.
name|isShutdown
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"HistoryCleanerService/move to done shutdown may not have "
operator|+
literal|"succeeded, Forcing a shutdown"
argument_list|)
expr_stmt|;
name|scheduledExecutor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|storage
operator|!=
literal|null
operator|&&
name|storage
operator|instanceof
name|Service
condition|)
block|{
operator|(
operator|(
name|Service
operator|)
name|storage
operator|)
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|hsManager
operator|!=
literal|null
condition|)
block|{
name|hsManager
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|JobHistory ()
specifier|public
name|JobHistory
parameter_list|()
block|{
name|super
argument_list|(
name|JobHistory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicationName ()
specifier|public
name|String
name|getApplicationName
parameter_list|()
block|{
return|return
literal|"Job History Server"
return|;
block|}
DECL|class|MoveIntermediateToDoneRunnable
specifier|private
class|class
name|MoveIntermediateToDoneRunnable
implements|implements
name|Runnable
block|{
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting scan to move intermediate done files"
argument_list|)
expr_stmt|;
name|hsManager
operator|.
name|scanIntermediateDirectory
argument_list|()
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
literal|"Error while scanning intermediate done dir "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|HistoryCleaner
specifier|private
class|class
name|HistoryCleaner
implements|implements
name|Runnable
block|{
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"History Cleaner started"
argument_list|)
expr_stmt|;
try|try
block|{
name|hsManager
operator|.
name|clean
argument_list|()
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
literal|"Error trying to clean up "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"History Cleaner complete"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Helper method for test cases.    */
DECL|method|getJobFileInfo (JobId jobId)
name|HistoryFileInfo
name|getJobFileInfo
parameter_list|(
name|JobId
name|jobId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|hsManager
operator|.
name|getFileInfo
argument_list|(
name|jobId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getJob (JobId jobId)
specifier|public
name|Job
name|getJob
parameter_list|(
name|JobId
name|jobId
parameter_list|)
block|{
return|return
name|storage
operator|.
name|getFullJob
argument_list|(
name|jobId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAllJobs (ApplicationId appID)
specifier|public
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|getAllJobs
parameter_list|(
name|ApplicationId
name|appID
parameter_list|)
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
literal|"Called getAllJobs(AppId): "
operator|+
name|appID
argument_list|)
expr_stmt|;
block|}
comment|// currently there is 1 to 1 mapping between app and job id
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|JobID
name|oldJobID
init|=
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|appID
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|jobs
init|=
operator|new
name|HashMap
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
argument_list|()
decl_stmt|;
name|JobId
name|jobID
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|oldJobID
argument_list|)
decl_stmt|;
name|jobs
operator|.
name|put
argument_list|(
name|jobID
argument_list|,
name|getJob
argument_list|(
name|jobID
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|jobs
return|;
block|}
annotation|@
name|Override
DECL|method|getAllJobs ()
specifier|public
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|getAllJobs
parameter_list|()
block|{
return|return
name|storage
operator|.
name|getAllPartialJobs
argument_list|()
return|;
block|}
DECL|method|refreshLoadedJobCache ()
specifier|public
name|void
name|refreshLoadedJobCache
parameter_list|()
block|{
if|if
condition|(
name|getServiceState
argument_list|()
operator|==
name|STATE
operator|.
name|STARTED
condition|)
block|{
if|if
condition|(
name|storage
operator|instanceof
name|CachedHistoryStorage
condition|)
block|{
operator|(
operator|(
name|CachedHistoryStorage
operator|)
name|storage
operator|)
operator|.
name|refreshLoadedJobCache
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|storage
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" is expected to be an instance of "
operator|+
name|CachedHistoryStorage
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to execute refreshLoadedJobCache: JobHistory service is not started"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getHistoryStorage ()
name|HistoryStorage
name|getHistoryStorage
parameter_list|()
block|{
return|return
name|storage
return|;
block|}
comment|/**    * Look for a set of partial jobs.    *     * @param offset    *          the offset into the list of jobs.    * @param count    *          the maximum number of jobs to return.    * @param user    *          only return jobs for the given user.    * @param queue    *          only return jobs for in the given queue.    * @param sBegin    *          only return Jobs that started on or after the given time.    * @param sEnd    *          only return Jobs that started on or before the given time.    * @param fBegin    *          only return Jobs that ended on or after the given time.    * @param fEnd    *          only return Jobs that ended on or before the given time.    * @param jobState    *          only return jobs that are in the give job state.    * @return The list of filtered jobs.    */
annotation|@
name|Override
DECL|method|getPartialJobs (Long offset, Long count, String user, String queue, Long sBegin, Long sEnd, Long fBegin, Long fEnd, JobState jobState)
specifier|public
name|JobsInfo
name|getPartialJobs
parameter_list|(
name|Long
name|offset
parameter_list|,
name|Long
name|count
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|queue
parameter_list|,
name|Long
name|sBegin
parameter_list|,
name|Long
name|sEnd
parameter_list|,
name|Long
name|fBegin
parameter_list|,
name|Long
name|fEnd
parameter_list|,
name|JobState
name|jobState
parameter_list|)
block|{
return|return
name|storage
operator|.
name|getPartialJobs
argument_list|(
name|offset
argument_list|,
name|count
argument_list|,
name|user
argument_list|,
name|queue
argument_list|,
name|sBegin
argument_list|,
name|sEnd
argument_list|,
name|fBegin
argument_list|,
name|fEnd
argument_list|,
name|jobState
argument_list|)
return|;
block|}
DECL|method|refreshJobRetentionSettings ()
specifier|public
name|void
name|refreshJobRetentionSettings
parameter_list|()
block|{
if|if
condition|(
name|getServiceState
argument_list|()
operator|==
name|STATE
operator|.
name|STARTED
condition|)
block|{
name|conf
operator|=
name|createConf
argument_list|()
expr_stmt|;
name|long
name|maxHistoryAge
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_MAX_AGE_MS
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_MR_HISTORY_MAX_AGE
argument_list|)
decl_stmt|;
name|hsManager
operator|.
name|setMaxHistoryAge
argument_list|(
name|maxHistoryAge
argument_list|)
expr_stmt|;
if|if
condition|(
name|futureHistoryCleaner
operator|!=
literal|null
condition|)
block|{
name|futureHistoryCleaner
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|futureHistoryCleaner
operator|=
literal|null
expr_stmt|;
name|scheduleHistoryCleaner
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to execute refreshJobRetentionSettings : Job History service is not started"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|scheduleHistoryCleaner ()
specifier|private
name|void
name|scheduleHistoryCleaner
parameter_list|()
block|{
name|boolean
name|startCleanerService
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_CLEANER_ENABLE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|startCleanerService
condition|)
block|{
name|cleanerInterval
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_CLEANER_INTERVAL_MS
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_MR_HISTORY_CLEANER_INTERVAL_MS
argument_list|)
expr_stmt|;
name|futureHistoryCleaner
operator|=
name|scheduledExecutor
operator|.
name|scheduleAtFixedRate
argument_list|(
operator|new
name|HistoryCleaner
argument_list|()
argument_list|,
name|getInitDelaySecs
argument_list|()
operator|*
literal|1000l
argument_list|,
name|cleanerInterval
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createConf ()
specifier|protected
name|Configuration
name|createConf
parameter_list|()
block|{
return|return
operator|new
name|Configuration
argument_list|()
return|;
block|}
DECL|method|getCleanerInterval ()
specifier|public
name|long
name|getCleanerInterval
parameter_list|()
block|{
return|return
name|cleanerInterval
return|;
block|}
comment|// TODO AppContext - Not Required
DECL|field|appAttemptID
specifier|private
name|ApplicationAttemptId
name|appAttemptID
decl_stmt|;
annotation|@
name|Override
DECL|method|getApplicationAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
block|{
comment|// TODO fixme - bogus appAttemptID for now
return|return
name|appAttemptID
return|;
block|}
comment|// TODO AppContext - Not Required
DECL|field|appID
specifier|private
name|ApplicationId
name|appID
decl_stmt|;
annotation|@
name|Override
DECL|method|getApplicationID ()
specifier|public
name|ApplicationId
name|getApplicationID
parameter_list|()
block|{
comment|// TODO fixme - bogus appID for now
return|return
name|appID
return|;
block|}
comment|// TODO AppContext - Not Required
annotation|@
name|Override
DECL|method|getEventHandler ()
specifier|public
name|EventHandler
name|getEventHandler
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|// TODO AppContext - Not Required
DECL|field|userName
specifier|private
name|String
name|userName
decl_stmt|;
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|CharSequence
name|getUser
parameter_list|()
block|{
if|if
condition|(
name|userName
operator|!=
literal|null
condition|)
block|{
name|userName
operator|=
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|USER_NAME
argument_list|,
literal|"history-user"
argument_list|)
expr_stmt|;
block|}
return|return
name|userName
return|;
block|}
comment|// TODO AppContext - Not Required
annotation|@
name|Override
DECL|method|getClock ()
specifier|public
name|Clock
name|getClock
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|// TODO AppContext - Not Required
annotation|@
name|Override
DECL|method|getClusterInfo ()
specifier|public
name|ClusterInfo
name|getClusterInfo
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|// TODO AppContext - Not Required
annotation|@
name|Override
DECL|method|getBlacklistedNodes ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getBlacklistedNodes
parameter_list|()
block|{
comment|// Not Implemented
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getClientToAMTokenSecretManager ()
specifier|public
name|ClientToAMTokenSecretManager
name|getClientToAMTokenSecretManager
parameter_list|()
block|{
comment|// Not implemented.
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|isLastAMRetry ()
specifier|public
name|boolean
name|isLastAMRetry
parameter_list|()
block|{
comment|// bogus - Not Required
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hasSuccessfullyUnregistered ()
specifier|public
name|boolean
name|hasSuccessfullyUnregistered
parameter_list|()
block|{
comment|// bogus - Not Required
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getNMHostname ()
specifier|public
name|String
name|getNMHostname
parameter_list|()
block|{
comment|// bogus - Not Required
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

