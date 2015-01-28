begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.nfs.nfs3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|nfs
operator|.
name|nfs3
package|;
end_package

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
comment|/**  * This class is a thread pool to easily schedule async data operations. Current  * async data operation is write back operation. In the future, we could use it  * for readahead operations too.  */
end_comment

begin_class
DECL|class|AsyncDataService
specifier|public
class|class
name|AsyncDataService
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
name|AsyncDataService
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// ThreadPool core pool size
DECL|field|CORE_THREADS_PER_VOLUME
specifier|private
specifier|static
specifier|final
name|int
name|CORE_THREADS_PER_VOLUME
init|=
literal|1
decl_stmt|;
comment|// ThreadPool maximum pool size
DECL|field|MAXIMUM_THREADS_PER_VOLUME
specifier|private
specifier|static
specifier|final
name|int
name|MAXIMUM_THREADS_PER_VOLUME
init|=
literal|4
decl_stmt|;
comment|// ThreadPool keep-alive time for threads over core pool size
DECL|field|THREADS_KEEP_ALIVE_SECONDS
specifier|private
specifier|static
specifier|final
name|long
name|THREADS_KEEP_ALIVE_SECONDS
init|=
literal|60
decl_stmt|;
DECL|field|threadGroup
specifier|private
specifier|final
name|ThreadGroup
name|threadGroup
init|=
operator|new
name|ThreadGroup
argument_list|(
literal|"async data service"
argument_list|)
decl_stmt|;
DECL|field|threadFactory
specifier|private
name|ThreadFactory
name|threadFactory
init|=
literal|null
decl_stmt|;
DECL|field|executor
specifier|private
name|ThreadPoolExecutor
name|executor
init|=
literal|null
decl_stmt|;
DECL|method|AsyncDataService ()
specifier|public
name|AsyncDataService
parameter_list|()
block|{
name|threadFactory
operator|=
operator|new
name|ThreadFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|r
parameter_list|)
block|{
return|return
operator|new
name|Thread
argument_list|(
name|threadGroup
argument_list|,
name|r
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|executor
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|CORE_THREADS_PER_VOLUME
argument_list|,
name|MAXIMUM_THREADS_PER_VOLUME
argument_list|,
name|THREADS_KEEP_ALIVE_SECONDS
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
name|threadFactory
argument_list|)
expr_stmt|;
comment|// This can reduce the number of running threads
name|executor
operator|.
name|allowCoreThreadTimeOut
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Execute the task sometime in the future.    */
DECL|method|execute (Runnable task)
specifier|synchronized
name|void
name|execute
parameter_list|(
name|Runnable
name|task
parameter_list|)
block|{
if|if
condition|(
name|executor
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"AsyncDataService is already shutdown"
argument_list|)
throw|;
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
literal|"Current active thread number: "
operator|+
name|executor
operator|.
name|getActiveCount
argument_list|()
operator|+
literal|" queue size: "
operator|+
name|executor
operator|.
name|getQueue
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" scheduled task number: "
operator|+
name|executor
operator|.
name|getTaskCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|executor
operator|.
name|execute
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gracefully shut down the ThreadPool. Will wait for all data tasks to    * finish.    */
DECL|method|shutdown ()
specifier|synchronized
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|executor
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"AsyncDataService has already shut down."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down all async data service threads..."
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// clear the executor so that calling execute again will fail.
name|executor
operator|=
literal|null
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"All async data service threads have been shut down"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Write the data to HDFS asynchronously    */
DECL|method|writeAsync (OpenFileCtx openFileCtx)
name|void
name|writeAsync
parameter_list|(
name|OpenFileCtx
name|openFileCtx
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
literal|"Scheduling write back task for fileId: "
operator|+
name|openFileCtx
operator|.
name|getLatestAttr
argument_list|()
operator|.
name|getFileId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|WriteBackTask
name|wbTask
init|=
operator|new
name|WriteBackTask
argument_list|(
name|openFileCtx
argument_list|)
decl_stmt|;
name|execute
argument_list|(
name|wbTask
argument_list|)
expr_stmt|;
block|}
comment|/**    * A task to write data back to HDFS for a file. Since only one thread can    * write to a file, there should only be one task at any time for a file    * (in queue or executing), and this should be guaranteed by the caller.    */
DECL|class|WriteBackTask
specifier|static
class|class
name|WriteBackTask
implements|implements
name|Runnable
block|{
DECL|field|openFileCtx
name|OpenFileCtx
name|openFileCtx
decl_stmt|;
DECL|method|WriteBackTask (OpenFileCtx openFileCtx)
name|WriteBackTask
parameter_list|(
name|OpenFileCtx
name|openFileCtx
parameter_list|)
block|{
name|this
operator|.
name|openFileCtx
operator|=
name|openFileCtx
expr_stmt|;
block|}
DECL|method|getOpenFileCtx ()
name|OpenFileCtx
name|getOpenFileCtx
parameter_list|()
block|{
return|return
name|openFileCtx
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|// Called in AsyncDataService.execute for displaying error messages.
return|return
literal|"write back data for fileId"
operator|+
name|openFileCtx
operator|.
name|getLatestAttr
argument_list|()
operator|.
name|getFileId
argument_list|()
operator|+
literal|" with nextOffset "
operator|+
name|openFileCtx
operator|.
name|getNextOffset
argument_list|()
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
try|try
block|{
name|openFileCtx
operator|.
name|executeWriteBack
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Async data service got error: "
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

