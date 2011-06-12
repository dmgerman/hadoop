begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
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
name|nio
operator|.
name|channels
operator|.
name|ClosedByInterruptException
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
name|ExecutorService
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|RejectedExecutionException
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
name|Semaphore
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
name|ThreadPoolExecutor
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

begin_comment
comment|/**  * Component accepting deserialized job traces, computing split data, and  * submitting to the cluster on deadline. Each job added from an upstream  * factory must be submitted to the cluster by the deadline recorded on it.  * Once submitted, jobs must be added to a downstream component for  * monitoring.  */
end_comment

begin_class
DECL|class|JobSubmitter
class|class
name|JobSubmitter
implements|implements
name|Gridmix
operator|.
name|Component
argument_list|<
name|GridmixJob
argument_list|>
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JobSubmitter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|sem
specifier|private
specifier|final
name|Semaphore
name|sem
decl_stmt|;
DECL|field|statistics
specifier|private
specifier|final
name|Statistics
name|statistics
decl_stmt|;
DECL|field|inputDir
specifier|private
specifier|final
name|FilePool
name|inputDir
decl_stmt|;
DECL|field|monitor
specifier|private
specifier|final
name|JobMonitor
name|monitor
decl_stmt|;
DECL|field|sched
specifier|private
specifier|final
name|ExecutorService
name|sched
decl_stmt|;
DECL|field|shutdown
specifier|private
specifier|volatile
name|boolean
name|shutdown
init|=
literal|false
decl_stmt|;
comment|/**    * Initialize the submission component with downstream monitor and pool of    * files from which split data may be read.    * @param monitor Monitor component to which jobs should be passed    * @param threads Number of submission threads    *   See {@link Gridmix#GRIDMIX_SUB_THR}.    * @param queueDepth Max depth of pending work queue    *   See {@link Gridmix#GRIDMIX_QUE_DEP}.    * @param inputDir Set of files from which split data may be mined for    *   synthetic jobs.    * @param statistics    */
DECL|method|JobSubmitter (JobMonitor monitor, int threads, int queueDepth, FilePool inputDir, Statistics statistics)
specifier|public
name|JobSubmitter
parameter_list|(
name|JobMonitor
name|monitor
parameter_list|,
name|int
name|threads
parameter_list|,
name|int
name|queueDepth
parameter_list|,
name|FilePool
name|inputDir
parameter_list|,
name|Statistics
name|statistics
parameter_list|)
block|{
name|sem
operator|=
operator|new
name|Semaphore
argument_list|(
name|queueDepth
argument_list|)
expr_stmt|;
name|sched
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|threads
argument_list|,
name|threads
argument_list|,
literal|0L
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|inputDir
operator|=
name|inputDir
expr_stmt|;
name|this
operator|.
name|monitor
operator|=
name|monitor
expr_stmt|;
name|this
operator|.
name|statistics
operator|=
name|statistics
expr_stmt|;
block|}
comment|/**    * Runnable wrapping a job to be submitted to the cluster.    */
DECL|class|SubmitTask
specifier|private
class|class
name|SubmitTask
implements|implements
name|Runnable
block|{
DECL|field|job
specifier|final
name|GridmixJob
name|job
decl_stmt|;
DECL|method|SubmitTask (GridmixJob job)
specifier|public
name|SubmitTask
parameter_list|(
name|GridmixJob
name|job
parameter_list|)
block|{
name|this
operator|.
name|job
operator|=
name|job
expr_stmt|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
comment|// pre-compute split information
try|try
block|{
name|job
operator|.
name|buildSplits
argument_list|(
name|inputDir
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
literal|"Failed to submit "
operator|+
name|job
operator|.
name|getJob
argument_list|()
operator|.
name|getJobName
argument_list|()
operator|+
literal|" as "
operator|+
name|job
operator|.
name|getUgi
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|submissionFailed
argument_list|(
name|job
operator|.
name|getJob
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to submit "
operator|+
name|job
operator|.
name|getJob
argument_list|()
operator|.
name|getJobName
argument_list|()
operator|+
literal|" as "
operator|+
name|job
operator|.
name|getUgi
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|submissionFailed
argument_list|(
name|job
operator|.
name|getJob
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Sleep until deadline
name|long
name|nsDelay
init|=
name|job
operator|.
name|getDelay
argument_list|(
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
while|while
condition|(
name|nsDelay
operator|>
literal|0
condition|)
block|{
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|sleep
argument_list|(
name|nsDelay
argument_list|)
expr_stmt|;
name|nsDelay
operator|=
name|job
operator|.
name|getDelay
argument_list|(
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// submit job
name|monitor
operator|.
name|add
argument_list|(
name|job
operator|.
name|call
argument_list|()
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|addJobStats
argument_list|(
name|job
operator|.
name|getJob
argument_list|()
argument_list|,
name|job
operator|.
name|getJobDesc
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"SUBMIT "
operator|+
name|job
operator|+
literal|"@"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|" ("
operator|+
name|job
operator|.
name|getJob
argument_list|()
operator|.
name|getJobID
argument_list|()
operator|+
literal|")"
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
literal|"Failed to submit "
operator|+
name|job
operator|.
name|getJob
argument_list|()
operator|.
name|getJobName
argument_list|()
operator|+
literal|" as "
operator|+
name|job
operator|.
name|getUgi
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|ClosedByInterruptException
condition|)
block|{
throw|throw
operator|new
name|InterruptedException
argument_list|(
literal|"Failed to submit "
operator|+
name|job
operator|.
name|getJob
argument_list|()
operator|.
name|getJobName
argument_list|()
argument_list|)
throw|;
block|}
name|monitor
operator|.
name|submissionFailed
argument_list|(
name|job
operator|.
name|getJob
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to submit "
operator|+
name|job
operator|.
name|getJob
argument_list|()
operator|.
name|getJobName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|submissionFailed
argument_list|(
name|job
operator|.
name|getJob
argument_list|()
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
comment|// abort execution, remove splits if nesc
comment|// TODO release ThdLoc
name|GridmixJob
operator|.
name|pullDescription
argument_list|(
name|job
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|monitor
operator|.
name|submissionFailed
argument_list|(
name|job
operator|.
name|getJob
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//Due to some exception job wasnt submitted.
name|LOG
operator|.
name|info
argument_list|(
literal|" Job "
operator|+
name|job
operator|.
name|getJob
argument_list|()
operator|+
literal|" submission failed "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|monitor
operator|.
name|submissionFailed
argument_list|(
name|job
operator|.
name|getJob
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|sem
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Enqueue the job to be submitted per the deadline associated with it.    */
DECL|method|add (final GridmixJob job)
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|GridmixJob
name|job
parameter_list|)
throws|throws
name|InterruptedException
block|{
specifier|final
name|boolean
name|addToQueue
init|=
operator|!
name|shutdown
decl_stmt|;
if|if
condition|(
name|addToQueue
condition|)
block|{
specifier|final
name|SubmitTask
name|task
init|=
operator|new
name|SubmitTask
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|sem
operator|.
name|acquire
argument_list|()
expr_stmt|;
try|try
block|{
name|sched
operator|.
name|execute
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RejectedExecutionException
name|e
parameter_list|)
block|{
name|sem
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * (Re)scan the set of input files from which splits are derived.    * @throws java.io.IOException    */
DECL|method|refreshFilePool ()
specifier|public
name|void
name|refreshFilePool
parameter_list|()
throws|throws
name|IOException
block|{
name|inputDir
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
comment|/**    * Does nothing, as the threadpool is already initialized and waiting for    * work from the upstream factory.    */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{ }
comment|/**    * Continue running until all queued jobs have been submitted to the    * cluster.    */
DECL|method|join (long millis)
specifier|public
name|void
name|join
parameter_list|(
name|long
name|millis
parameter_list|)
throws|throws
name|InterruptedException
block|{
if|if
condition|(
operator|!
name|shutdown
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot wait for active submit thread"
argument_list|)
throw|;
block|}
name|sched
operator|.
name|awaitTermination
argument_list|(
name|millis
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
comment|/**    * Finish all jobs pending submission, but do not accept new work.    */
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
comment|// complete pending tasks, but accept no new tasks
name|shutdown
operator|=
literal|true
expr_stmt|;
name|sched
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Discard pending work, including precomputed work waiting to be    * submitted.    */
DECL|method|abort ()
specifier|public
name|void
name|abort
parameter_list|()
block|{
comment|//pendingJobs.clear();
name|shutdown
operator|=
literal|true
expr_stmt|;
name|sched
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

