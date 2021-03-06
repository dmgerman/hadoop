begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Callable
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
name|atomic
operator|.
name|AtomicInteger
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
name|LocatedFileStatus
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
name|PathFilter
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
name|RemoteIterator
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
name|lib
operator|.
name|input
operator|.
name|FileInputFormat
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
name|collect
operator|.
name|Iterables
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
name|FutureCallback
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
name|Futures
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
name|ListenableFuture
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
name|ListeningExecutorService
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
name|MoreExecutors
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

begin_comment
comment|/**  * Utility class to fetch block locations for specified Input paths using a  * configured number of threads.  * The thread count is determined from the value of  * "mapreduce.input.fileinputformat.list-status.num-threads" in the  * configuration.  */
end_comment

begin_class
annotation|@
name|Private
DECL|class|LocatedFileStatusFetcher
specifier|public
class|class
name|LocatedFileStatusFetcher
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LocatedFileStatusFetcher
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|inputDirs
specifier|private
specifier|final
name|Path
index|[]
name|inputDirs
decl_stmt|;
DECL|field|inputFilter
specifier|private
specifier|final
name|PathFilter
name|inputFilter
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|recursive
specifier|private
specifier|final
name|boolean
name|recursive
decl_stmt|;
DECL|field|newApi
specifier|private
specifier|final
name|boolean
name|newApi
decl_stmt|;
DECL|field|rawExec
specifier|private
specifier|final
name|ExecutorService
name|rawExec
decl_stmt|;
DECL|field|exec
specifier|private
specifier|final
name|ListeningExecutorService
name|exec
decl_stmt|;
DECL|field|resultQueue
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|List
argument_list|<
name|FileStatus
argument_list|>
argument_list|>
name|resultQueue
decl_stmt|;
DECL|field|invalidInputErrors
specifier|private
specifier|final
name|List
argument_list|<
name|IOException
argument_list|>
name|invalidInputErrors
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|processInitialInputPathCallback
specifier|private
specifier|final
name|ProcessInitialInputPathCallback
name|processInitialInputPathCallback
init|=
operator|new
name|ProcessInitialInputPathCallback
argument_list|()
decl_stmt|;
DECL|field|processInputDirCallback
specifier|private
specifier|final
name|ProcessInputDirCallback
name|processInputDirCallback
init|=
operator|new
name|ProcessInputDirCallback
argument_list|()
decl_stmt|;
DECL|field|runningTasks
specifier|private
specifier|final
name|AtomicInteger
name|runningTasks
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|ReentrantLock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
DECL|field|condition
specifier|private
specifier|final
name|Condition
name|condition
init|=
name|lock
operator|.
name|newCondition
argument_list|()
decl_stmt|;
DECL|field|unknownError
specifier|private
specifier|volatile
name|Throwable
name|unknownError
decl_stmt|;
comment|/**    * Instantiate.    * The newApi switch is only used to configure what exception is raised    * on failure of {@link #getFileStatuses()}, it does not change the algorithm.    * @param conf configuration for the job    * @param dirs the initial list of paths    * @param recursive whether to traverse the paths recursively    * @param inputFilter inputFilter to apply to the resulting paths    * @param newApi whether using the mapred or mapreduce API    * @throws InterruptedException    * @throws IOException    */
DECL|method|LocatedFileStatusFetcher (Configuration conf, Path[] dirs, boolean recursive, PathFilter inputFilter, boolean newApi)
specifier|public
name|LocatedFileStatusFetcher
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Path
index|[]
name|dirs
parameter_list|,
name|boolean
name|recursive
parameter_list|,
name|PathFilter
name|inputFilter
parameter_list|,
name|boolean
name|newApi
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
name|int
name|numThreads
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|FileInputFormat
operator|.
name|LIST_STATUS_NUM_THREADS
argument_list|,
name|FileInputFormat
operator|.
name|DEFAULT_LIST_STATUS_NUM_THREADS
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Instantiated LocatedFileStatusFetcher with {} threads"
argument_list|,
name|numThreads
argument_list|)
expr_stmt|;
name|rawExec
operator|=
name|HadoopExecutors
operator|.
name|newFixedThreadPool
argument_list|(
name|numThreads
argument_list|,
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|setNameFormat
argument_list|(
literal|"GetFileInfo #%d"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|exec
operator|=
name|MoreExecutors
operator|.
name|listeningDecorator
argument_list|(
name|rawExec
argument_list|)
expr_stmt|;
name|resultQueue
operator|=
operator|new
name|LinkedBlockingQueue
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|inputDirs
operator|=
name|dirs
expr_stmt|;
name|this
operator|.
name|recursive
operator|=
name|recursive
expr_stmt|;
name|this
operator|.
name|inputFilter
operator|=
name|inputFilter
expr_stmt|;
name|this
operator|.
name|newApi
operator|=
name|newApi
expr_stmt|;
block|}
comment|/**    * Start executing and return FileStatuses based on the parameters specified.    * @return fetched file statuses    * @throws InterruptedException interruption waiting for results.    * @throws IOException IO failure or other error.    * @throws InvalidInputException on an invalid input and the old API    * @throws org.apache.hadoop.mapreduce.lib.input.InvalidInputException on an    *         invalid input and the new API.    */
DECL|method|getFileStatuses ()
specifier|public
name|Iterable
argument_list|<
name|FileStatus
argument_list|>
name|getFileStatuses
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
comment|// Increment to make sure a race between the first thread completing and the
comment|// rest being scheduled does not lead to a termination.
name|runningTasks
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
for|for
control|(
name|Path
name|p
range|:
name|inputDirs
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Queuing scan of directory {}"
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|runningTasks
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|ListenableFuture
argument_list|<
name|ProcessInitialInputPathCallable
operator|.
name|Result
argument_list|>
name|future
init|=
name|exec
operator|.
name|submit
argument_list|(
operator|new
name|ProcessInitialInputPathCallable
argument_list|(
name|p
argument_list|,
name|conf
argument_list|,
name|inputFilter
argument_list|)
argument_list|)
decl_stmt|;
name|Futures
operator|.
name|addCallback
argument_list|(
name|future
argument_list|,
name|processInitialInputPathCallback
argument_list|,
name|MoreExecutors
operator|.
name|directExecutor
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|runningTasks
operator|.
name|decrementAndGet
argument_list|()
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
name|debug
argument_list|(
literal|"Waiting scan completion"
argument_list|)
expr_stmt|;
while|while
condition|(
name|runningTasks
operator|.
name|get
argument_list|()
operator|!=
literal|0
operator|&&
name|unknownError
operator|==
literal|null
condition|)
block|{
name|condition
operator|.
name|await
argument_list|()
expr_stmt|;
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
comment|// either the scan completed or an error was raised.
comment|// in the case of an error shutting down the executor will interrupt all
comment|// active threads, which can add noise to the logs.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Scan complete: shutting down"
argument_list|)
expr_stmt|;
name|this
operator|.
name|exec
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|unknownError
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Scan failed"
argument_list|,
name|this
operator|.
name|unknownError
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|unknownError
operator|instanceof
name|Error
condition|)
block|{
throw|throw
operator|(
name|Error
operator|)
name|this
operator|.
name|unknownError
throw|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|unknownError
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|this
operator|.
name|unknownError
throw|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|unknownError
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|this
operator|.
name|unknownError
throw|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|unknownError
operator|instanceof
name|InterruptedException
condition|)
block|{
throw|throw
operator|(
name|InterruptedException
operator|)
name|this
operator|.
name|unknownError
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|this
operator|.
name|unknownError
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|this
operator|.
name|invalidInputErrors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Invalid Input Errors raised"
argument_list|)
expr_stmt|;
for|for
control|(
name|IOException
name|error
range|:
name|invalidInputErrors
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Error"
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|newApi
condition|)
block|{
throw|throw
operator|new
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|input
operator|.
name|InvalidInputException
argument_list|(
name|invalidInputErrors
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|InvalidInputException
argument_list|(
name|invalidInputErrors
argument_list|)
throw|;
block|}
block|}
return|return
name|Iterables
operator|.
name|concat
argument_list|(
name|resultQueue
argument_list|)
return|;
block|}
comment|/**    * Collect misconfigured Input errors. Errors while actually reading file info    * are reported immediately.    */
DECL|method|registerInvalidInputError (List<IOException> errors)
specifier|private
name|void
name|registerInvalidInputError
parameter_list|(
name|List
argument_list|<
name|IOException
argument_list|>
name|errors
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|this
operator|.
name|invalidInputErrors
operator|.
name|addAll
argument_list|(
name|errors
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Register fatal errors - example an IOException while accessing a file or a    * full execution queue.    */
DECL|method|registerError (Throwable t)
specifier|private
name|void
name|registerError
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Error"
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|unknownError
operator|==
literal|null
condition|)
block|{
name|unknownError
operator|=
name|t
expr_stmt|;
name|condition
operator|.
name|signal
argument_list|()
expr_stmt|;
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
block|}
DECL|method|decrementRunningAndCheckCompletion ()
specifier|private
name|void
name|decrementRunningAndCheckCompletion
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|runningTasks
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|condition
operator|.
name|signal
argument_list|()
expr_stmt|;
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
block|}
comment|/**    * Retrieves block locations for the given @link {@link FileStatus}, and adds    * additional paths to the process queue if required.    */
DECL|class|ProcessInputDirCallable
specifier|private
specifier|static
class|class
name|ProcessInputDirCallable
implements|implements
name|Callable
argument_list|<
name|ProcessInputDirCallable
operator|.
name|Result
argument_list|>
block|{
DECL|field|fs
specifier|private
specifier|final
name|FileSystem
name|fs
decl_stmt|;
DECL|field|fileStatus
specifier|private
specifier|final
name|FileStatus
name|fileStatus
decl_stmt|;
DECL|field|recursive
specifier|private
specifier|final
name|boolean
name|recursive
decl_stmt|;
DECL|field|inputFilter
specifier|private
specifier|final
name|PathFilter
name|inputFilter
decl_stmt|;
DECL|method|ProcessInputDirCallable (FileSystem fs, FileStatus fileStatus, boolean recursive, PathFilter inputFilter)
name|ProcessInputDirCallable
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|FileStatus
name|fileStatus
parameter_list|,
name|boolean
name|recursive
parameter_list|,
name|PathFilter
name|inputFilter
parameter_list|)
block|{
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|fileStatus
operator|=
name|fileStatus
expr_stmt|;
name|this
operator|.
name|recursive
operator|=
name|recursive
expr_stmt|;
name|this
operator|.
name|inputFilter
operator|=
name|inputFilter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Result
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|result
init|=
operator|new
name|Result
argument_list|()
decl_stmt|;
name|result
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"ProcessInputDirCallable {}"
argument_list|,
name|fileStatus
argument_list|)
expr_stmt|;
if|if
condition|(
name|fileStatus
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|RemoteIterator
argument_list|<
name|LocatedFileStatus
argument_list|>
name|iter
init|=
name|fs
operator|.
name|listLocatedStatus
argument_list|(
name|fileStatus
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|LocatedFileStatus
name|stat
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|inputFilter
operator|.
name|accept
argument_list|(
name|stat
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|recursive
operator|&&
name|stat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|result
operator|.
name|dirsNeedingRecursiveCalls
operator|.
name|add
argument_list|(
name|stat
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|locatedFileStatuses
operator|.
name|add
argument_list|(
name|stat
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
name|result
operator|.
name|locatedFileStatuses
operator|.
name|add
argument_list|(
name|fileStatus
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|class|Result
specifier|private
specifier|static
class|class
name|Result
block|{
DECL|field|locatedFileStatuses
specifier|private
name|List
argument_list|<
name|FileStatus
argument_list|>
name|locatedFileStatuses
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|dirsNeedingRecursiveCalls
specifier|private
name|List
argument_list|<
name|FileStatus
argument_list|>
name|dirsNeedingRecursiveCalls
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
block|}
block|}
comment|/**    * The callback handler to handle results generated by    * {@link ProcessInputDirCallable}. This populates the final result set.    *     */
DECL|class|ProcessInputDirCallback
specifier|private
class|class
name|ProcessInputDirCallback
implements|implements
name|FutureCallback
argument_list|<
name|ProcessInputDirCallable
operator|.
name|Result
argument_list|>
block|{
annotation|@
name|Override
DECL|method|onSuccess (ProcessInputDirCallable.Result result)
specifier|public
name|void
name|onSuccess
parameter_list|(
name|ProcessInputDirCallable
operator|.
name|Result
name|result
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|result
operator|.
name|locatedFileStatuses
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|resultQueue
operator|.
name|add
argument_list|(
name|result
operator|.
name|locatedFileStatuses
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|result
operator|.
name|dirsNeedingRecursiveCalls
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|FileStatus
name|fileStatus
range|:
name|result
operator|.
name|dirsNeedingRecursiveCalls
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Queueing directory scan {}"
argument_list|,
name|fileStatus
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|runningTasks
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|ListenableFuture
argument_list|<
name|ProcessInputDirCallable
operator|.
name|Result
argument_list|>
name|future
init|=
name|exec
operator|.
name|submit
argument_list|(
operator|new
name|ProcessInputDirCallable
argument_list|(
name|result
operator|.
name|fs
argument_list|,
name|fileStatus
argument_list|,
name|recursive
argument_list|,
name|inputFilter
argument_list|)
argument_list|)
decl_stmt|;
name|Futures
operator|.
name|addCallback
argument_list|(
name|future
argument_list|,
name|processInputDirCallback
argument_list|,
name|MoreExecutors
operator|.
name|directExecutor
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|decrementRunningAndCheckCompletion
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Error within the callback itself.
name|registerError
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onFailure (Throwable t)
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Any generated exceptions. Leads to immediate termination.
name|registerError
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Processes an initial Input Path pattern through the globber and PathFilter    * to generate a list of files which need further processing.    */
DECL|class|ProcessInitialInputPathCallable
specifier|private
specifier|static
class|class
name|ProcessInitialInputPathCallable
implements|implements
name|Callable
argument_list|<
name|ProcessInitialInputPathCallable
operator|.
name|Result
argument_list|>
block|{
DECL|field|path
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|inputFilter
specifier|private
specifier|final
name|PathFilter
name|inputFilter
decl_stmt|;
DECL|method|ProcessInitialInputPathCallable (Path path, Configuration conf, PathFilter pathFilter)
specifier|public
name|ProcessInitialInputPathCallable
parameter_list|(
name|Path
name|path
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|PathFilter
name|pathFilter
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|inputFilter
operator|=
name|pathFilter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Result
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|Result
name|result
init|=
operator|new
name|Result
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|path
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|result
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"ProcessInitialInputPathCallable path {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|FileStatus
index|[]
name|matches
init|=
name|fs
operator|.
name|globStatus
argument_list|(
name|path
argument_list|,
name|inputFilter
argument_list|)
decl_stmt|;
if|if
condition|(
name|matches
operator|==
literal|null
condition|)
block|{
name|result
operator|.
name|addError
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Input path does not exist: "
operator|+
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|matches
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|result
operator|.
name|addError
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Input Pattern "
operator|+
name|path
operator|+
literal|" matches 0 files"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|matchedFileStatuses
operator|=
name|matches
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|class|Result
specifier|private
specifier|static
class|class
name|Result
block|{
DECL|field|errors
specifier|private
name|List
argument_list|<
name|IOException
argument_list|>
name|errors
decl_stmt|;
DECL|field|matchedFileStatuses
specifier|private
name|FileStatus
index|[]
name|matchedFileStatuses
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|method|addError (IOException ioe)
name|void
name|addError
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
if|if
condition|(
name|errors
operator|==
literal|null
condition|)
block|{
name|errors
operator|=
operator|new
name|LinkedList
argument_list|<
name|IOException
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|errors
operator|.
name|add
argument_list|(
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * The callback handler to handle results generated by    * {@link ProcessInitialInputPathCallable}.    *     */
DECL|class|ProcessInitialInputPathCallback
specifier|private
class|class
name|ProcessInitialInputPathCallback
implements|implements
name|FutureCallback
argument_list|<
name|ProcessInitialInputPathCallable
operator|.
name|Result
argument_list|>
block|{
annotation|@
name|Override
DECL|method|onSuccess (ProcessInitialInputPathCallable.Result result)
specifier|public
name|void
name|onSuccess
parameter_list|(
name|ProcessInitialInputPathCallable
operator|.
name|Result
name|result
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|result
operator|.
name|errors
operator|!=
literal|null
condition|)
block|{
name|registerInvalidInputError
argument_list|(
name|result
operator|.
name|errors
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|.
name|matchedFileStatuses
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|FileStatus
name|matched
range|:
name|result
operator|.
name|matchedFileStatuses
control|)
block|{
name|runningTasks
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|ListenableFuture
argument_list|<
name|ProcessInputDirCallable
operator|.
name|Result
argument_list|>
name|future
init|=
name|exec
operator|.
name|submit
argument_list|(
operator|new
name|ProcessInputDirCallable
argument_list|(
name|result
operator|.
name|fs
argument_list|,
name|matched
argument_list|,
name|recursive
argument_list|,
name|inputFilter
argument_list|)
argument_list|)
decl_stmt|;
name|Futures
operator|.
name|addCallback
argument_list|(
name|future
argument_list|,
name|processInputDirCallback
argument_list|,
name|MoreExecutors
operator|.
name|directExecutor
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|decrementRunningAndCheckCompletion
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Exception within the callback
name|registerError
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onFailure (Throwable t)
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Any generated exceptions. Leads to immediate termination.
name|registerError
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

