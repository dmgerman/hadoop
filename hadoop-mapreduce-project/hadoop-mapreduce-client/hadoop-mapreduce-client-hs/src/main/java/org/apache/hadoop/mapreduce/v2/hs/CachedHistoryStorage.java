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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|JobReport
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
name|JobInfo
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
name|service
operator|.
name|AbstractService
import|;
end_import

begin_comment
comment|/**  * Manages an in memory cache of parsed Job History files.  */
end_comment

begin_class
DECL|class|CachedHistoryStorage
specifier|public
class|class
name|CachedHistoryStorage
extends|extends
name|AbstractService
implements|implements
name|HistoryStorage
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
name|CachedHistoryStorage
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|loadedJobCache
specifier|private
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|loadedJobCache
init|=
literal|null
decl_stmt|;
comment|// The number of loaded jobs.
DECL|field|loadedJobCacheSize
specifier|private
name|int
name|loadedJobCacheSize
decl_stmt|;
DECL|field|hsManager
specifier|private
name|HistoryFileManager
name|hsManager
decl_stmt|;
annotation|@
name|Override
DECL|method|setHistoryFileManager (HistoryFileManager hsManager)
specifier|public
name|void
name|setHistoryFileManager
parameter_list|(
name|HistoryFileManager
name|hsManager
parameter_list|)
block|{
name|this
operator|.
name|hsManager
operator|=
name|hsManager
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|YarnRuntimeException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"CachedHistoryStorage Init"
argument_list|)
expr_stmt|;
name|loadedJobCacheSize
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_LOADED_JOB_CACHE_SIZE
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_MR_HISTORY_LOADED_JOB_CACHE_SIZE
argument_list|)
expr_stmt|;
name|loadedJobCache
operator|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|LinkedHashMap
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
argument_list|(
name|loadedJobCacheSize
operator|+
literal|1
argument_list|,
literal|0.75f
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|removeEldestEntry
parameter_list|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|eldest
parameter_list|)
block|{
return|return
name|super
operator|.
name|size
argument_list|()
operator|>
name|loadedJobCacheSize
return|;
block|}
block|}
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
DECL|method|CachedHistoryStorage ()
specifier|public
name|CachedHistoryStorage
parameter_list|()
block|{
name|super
argument_list|(
name|CachedHistoryStorage
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|loadJob (HistoryFileInfo fileInfo)
specifier|private
name|Job
name|loadJob
parameter_list|(
name|HistoryFileInfo
name|fileInfo
parameter_list|)
block|{
try|try
block|{
name|Job
name|job
init|=
name|fileInfo
operator|.
name|loadJob
argument_list|()
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
literal|"Adding "
operator|+
name|job
operator|.
name|getID
argument_list|()
operator|+
literal|" to loaded job cache"
argument_list|)
expr_stmt|;
block|}
comment|// We can clobber results here, but that should be OK, because it only
comment|// means that we may have two identical copies of the same job floating
comment|// around for a while.
name|loadedJobCache
operator|.
name|put
argument_list|(
name|job
operator|.
name|getID
argument_list|()
argument_list|,
name|job
argument_list|)
expr_stmt|;
return|return
name|job
return|;
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
literal|"Could not find/load job: "
operator|+
name|fileInfo
operator|.
name|getJobId
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFullJob (JobId jobId)
specifier|public
name|Job
name|getFullJob
parameter_list|(
name|JobId
name|jobId
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
literal|"Looking for Job "
operator|+
name|jobId
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|HistoryFileInfo
name|fileInfo
init|=
name|hsManager
operator|.
name|getFileInfo
argument_list|(
name|jobId
argument_list|)
decl_stmt|;
name|Job
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|fileInfo
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|loadedJobCache
operator|.
name|get
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|loadJob
argument_list|(
name|fileInfo
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fileInfo
operator|.
name|isDeleted
argument_list|()
condition|)
block|{
name|loadedJobCache
operator|.
name|remove
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
name|result
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|loadedJobCache
operator|.
name|remove
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
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
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getAllPartialJobs ()
specifier|public
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|getAllPartialJobs
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Called getAllPartialJobs()"
argument_list|)
expr_stmt|;
name|SortedMap
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|result
init|=
operator|new
name|TreeMap
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|HistoryFileInfo
name|mi
range|:
name|hsManager
operator|.
name|getAllFileInfo
argument_list|()
control|)
block|{
if|if
condition|(
name|mi
operator|!=
literal|null
condition|)
block|{
name|JobId
name|id
init|=
name|mi
operator|.
name|getJobId
argument_list|()
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|PartialJob
argument_list|(
name|mi
operator|.
name|getJobIndexInfo
argument_list|()
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"Error trying to scan for all FileInfos"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
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
name|getPartialJobs
argument_list|(
name|getAllPartialJobs
argument_list|()
operator|.
name|values
argument_list|()
argument_list|,
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
DECL|method|getPartialJobs (Collection<Job> jobs, Long offset, Long count, String user, String queue, Long sBegin, Long sEnd, Long fBegin, Long fEnd, JobState jobState)
specifier|public
specifier|static
name|JobsInfo
name|getPartialJobs
parameter_list|(
name|Collection
argument_list|<
name|Job
argument_list|>
name|jobs
parameter_list|,
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
name|JobsInfo
name|allJobs
init|=
operator|new
name|JobsInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|sBegin
operator|==
literal|null
operator|||
name|sBegin
operator|<
literal|0
condition|)
name|sBegin
operator|=
literal|0l
expr_stmt|;
if|if
condition|(
name|sEnd
operator|==
literal|null
condition|)
name|sEnd
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
if|if
condition|(
name|fBegin
operator|==
literal|null
operator|||
name|fBegin
operator|<
literal|0
condition|)
name|fBegin
operator|=
literal|0l
expr_stmt|;
if|if
condition|(
name|fEnd
operator|==
literal|null
condition|)
name|fEnd
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
if|if
condition|(
name|offset
operator|==
literal|null
operator|||
name|offset
operator|<
literal|0
condition|)
name|offset
operator|=
literal|0l
expr_stmt|;
if|if
condition|(
name|count
operator|==
literal|null
condition|)
name|count
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
if|if
condition|(
name|offset
operator|>
name|jobs
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
name|allJobs
return|;
block|}
name|long
name|at
init|=
literal|0
decl_stmt|;
name|long
name|end
init|=
name|offset
operator|+
name|count
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|end
operator|<
literal|0
condition|)
block|{
comment|// due to overflow
name|end
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
for|for
control|(
name|Job
name|job
range|:
name|jobs
control|)
block|{
if|if
condition|(
name|at
operator|>
name|end
condition|)
block|{
break|break;
block|}
comment|// can't really validate queue is a valid one since queues could change
if|if
condition|(
name|queue
operator|!=
literal|null
operator|&&
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|job
operator|.
name|getQueueName
argument_list|()
operator|.
name|equals
argument_list|(
name|queue
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
if|if
condition|(
name|user
operator|!=
literal|null
operator|&&
operator|!
name|user
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|job
operator|.
name|getUserName
argument_list|()
operator|.
name|equals
argument_list|(
name|user
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
name|JobReport
name|report
init|=
name|job
operator|.
name|getReport
argument_list|()
decl_stmt|;
if|if
condition|(
name|report
operator|.
name|getStartTime
argument_list|()
operator|<
name|sBegin
operator|||
name|report
operator|.
name|getStartTime
argument_list|()
operator|>
name|sEnd
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|report
operator|.
name|getFinishTime
argument_list|()
operator|<
name|fBegin
operator|||
name|report
operator|.
name|getFinishTime
argument_list|()
operator|>
name|fEnd
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|jobState
operator|!=
literal|null
operator|&&
name|jobState
operator|!=
name|report
operator|.
name|getJobState
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|at
operator|++
expr_stmt|;
if|if
condition|(
operator|(
name|at
operator|-
literal|1
operator|)
operator|<
name|offset
condition|)
block|{
continue|continue;
block|}
name|JobInfo
name|jobInfo
init|=
operator|new
name|JobInfo
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|allJobs
operator|.
name|add
argument_list|(
name|jobInfo
argument_list|)
expr_stmt|;
block|}
return|return
name|allJobs
return|;
block|}
block|}
end_class

end_unit

