begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|IOUtils
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
name|tools
operator|.
name|rumen
operator|.
name|JobStory
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
name|tools
operator|.
name|rumen
operator|.
name|JobStoryProducer
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
name|gridmix
operator|.
name|Statistics
operator|.
name|JobStats
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
name|security
operator|.
name|UserGroupInformation
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
name|CountDownLatch
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
name|Condition
import|;
end_import

begin_class
DECL|class|SerialJobFactory
specifier|public
class|class
name|SerialJobFactory
extends|extends
name|JobFactory
argument_list|<
name|JobStats
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
name|SerialJobFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|jobCompleted
specifier|private
specifier|final
name|Condition
name|jobCompleted
init|=
name|lock
operator|.
name|newCondition
argument_list|()
decl_stmt|;
comment|/**    * Creating a new instance does not start the thread.    *    * @param submitter   Component to which deserialized jobs are passed    * @param jobProducer Job story producer    *                    {@link org.apache.hadoop.tools.rumen.ZombieJobProducer}    * @param scratch     Directory into which to write output from simulated jobs    * @param conf        Config passed to all jobs to be submitted    * @param startFlag   Latch released from main to start pipeline    * @throws java.io.IOException    */
DECL|method|SerialJobFactory ( JobSubmitter submitter, JobStoryProducer jobProducer, Path scratch, Configuration conf, CountDownLatch startFlag, UserResolver resolver)
specifier|public
name|SerialJobFactory
parameter_list|(
name|JobSubmitter
name|submitter
parameter_list|,
name|JobStoryProducer
name|jobProducer
parameter_list|,
name|Path
name|scratch
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|CountDownLatch
name|startFlag
parameter_list|,
name|UserResolver
name|resolver
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|submitter
argument_list|,
name|jobProducer
argument_list|,
name|scratch
argument_list|,
name|conf
argument_list|,
name|startFlag
argument_list|,
name|resolver
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createReaderThread ()
specifier|public
name|Thread
name|createReaderThread
parameter_list|()
block|{
return|return
operator|new
name|SerialReaderThread
argument_list|(
literal|"SerialJobFactory"
argument_list|)
return|;
block|}
DECL|class|SerialReaderThread
specifier|private
class|class
name|SerialReaderThread
extends|extends
name|Thread
block|{
DECL|method|SerialReaderThread (String threadName)
specifier|public
name|SerialReaderThread
parameter_list|(
name|String
name|threadName
parameter_list|)
block|{
name|super
argument_list|(
name|threadName
argument_list|)
expr_stmt|;
block|}
comment|/**      * SERIAL : In this scenario .  method waits on notification ,      * that a submitted job is actually completed. Logic is simple.      * ===      * while(true) {      * wait till previousjob is completed.      * break;      * }      * submit newJob.      * previousJob = newJob;      * ==      */
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
name|startFlag
operator|.
name|await
argument_list|()
expr_stmt|;
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"START SERIAL @ "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|GridmixJob
name|prevJob
decl_stmt|;
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
specifier|final
name|JobStory
name|job
decl_stmt|;
try|try
block|{
name|job
operator|=
name|getNextJobFiltered
argument_list|()
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|job
condition|)
block|{
return|return;
block|}
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
literal|"Serial mode submitting job "
operator|+
name|job
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|prevJob
operator|=
name|jobCreator
operator|.
name|createGridmixJob
argument_list|(
name|conf
argument_list|,
literal|0L
argument_list|,
name|job
argument_list|,
name|scratch
argument_list|,
name|userResolver
operator|.
name|getTargetUgi
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|job
operator|.
name|getUser
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|sequence
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|" Submitted the job "
operator|+
name|prevJob
argument_list|)
expr_stmt|;
name|submitter
operator|.
name|add
argument_list|(
name|prevJob
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
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
name|e
expr_stmt|;
comment|//If submission of current job fails , try to submit the next job.
return|return;
block|}
if|if
condition|(
name|prevJob
operator|!=
literal|null
condition|)
block|{
comment|//Wait till previous job submitted is completed.
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|jobCompleted
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|" Error in SerialJobFactory while waiting for job completion "
argument_list|,
name|ie
argument_list|)
expr_stmt|;
return|return;
block|}
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
name|info
argument_list|(
literal|" job "
operator|+
name|job
operator|.
name|getName
argument_list|()
operator|+
literal|" completed "
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|prevJob
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
return|return;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|jobProducer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * SERIAL. Once you get notification from StatsCollector about the job    * completion ,simply notify the waiting thread.    *    * @param item    */
annotation|@
name|Override
DECL|method|update (Statistics.JobStats item)
specifier|public
name|void
name|update
parameter_list|(
name|Statistics
operator|.
name|JobStats
name|item
parameter_list|)
block|{
comment|//simply notify in case of serial submissions. We are just bothered
comment|//if submitted job is completed or not.
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|jobCompleted
operator|.
name|signalAll
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Start the reader thread, wait for latch if necessary.    */
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|" Starting Serial submission "
argument_list|)
expr_stmt|;
name|this
operator|.
name|rThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// it is need for test
DECL|method|setDistCacheEmulator (DistributedCacheEmulator e)
name|void
name|setDistCacheEmulator
parameter_list|(
name|DistributedCacheEmulator
name|e
parameter_list|)
block|{
name|jobCreator
operator|.
name|setDistCacheEmulator
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

