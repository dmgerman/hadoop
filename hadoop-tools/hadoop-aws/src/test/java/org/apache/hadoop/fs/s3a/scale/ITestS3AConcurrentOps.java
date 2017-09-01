begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.scale
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|scale
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
name|net
operator|.
name|URI
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
name|ExecutionException
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
name|Executors
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
name|Future
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
name|contract
operator|.
name|ContractTestUtils
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
name|contract
operator|.
name|ContractTestUtils
operator|.
name|NanoTimer
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
name|s3a
operator|.
name|S3AFileSystem
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
name|s3a
operator|.
name|S3ATestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|Constants
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Tests concurrent operations on a single S3AFileSystem instance.  */
end_comment

begin_class
DECL|class|ITestS3AConcurrentOps
specifier|public
class|class
name|ITestS3AConcurrentOps
extends|extends
name|S3AScaleTestBase
block|{
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
name|ITestS3AConcurrentOps
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|concurrentRenames
specifier|private
specifier|final
name|int
name|concurrentRenames
init|=
literal|10
decl_stmt|;
DECL|field|testRoot
specifier|private
name|Path
name|testRoot
decl_stmt|;
DECL|field|source
specifier|private
name|Path
index|[]
name|source
init|=
operator|new
name|Path
index|[
name|concurrentRenames
index|]
decl_stmt|;
DECL|field|target
specifier|private
name|Path
index|[]
name|target
init|=
operator|new
name|Path
index|[
name|concurrentRenames
index|]
decl_stmt|;
DECL|field|fs
specifier|private
name|S3AFileSystem
name|fs
decl_stmt|;
DECL|field|auxFs
specifier|private
name|S3AFileSystem
name|auxFs
decl_stmt|;
annotation|@
name|Override
DECL|method|getTestTimeoutSeconds ()
specifier|protected
name|int
name|getTestTimeoutSeconds
parameter_list|()
block|{
return|return
literal|16
operator|*
literal|60
return|;
block|}
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|fs
operator|=
name|getRestrictedFileSystem
argument_list|()
expr_stmt|;
name|auxFs
operator|=
name|getNormalFileSystem
argument_list|()
expr_stmt|;
name|testRoot
operator|=
name|path
argument_list|(
literal|"/ITestS3AConcurrentOps"
argument_list|)
expr_stmt|;
name|testRoot
operator|=
name|S3ATestUtils
operator|.
name|createTestPath
argument_list|(
name|testRoot
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|concurrentRenames
condition|;
name|i
operator|++
control|)
block|{
name|source
index|[
name|i
index|]
operator|=
operator|new
name|Path
argument_list|(
name|testRoot
argument_list|,
literal|"source"
operator|+
name|i
argument_list|)
expr_stmt|;
name|target
index|[
name|i
index|]
operator|=
operator|new
name|Path
argument_list|(
name|testRoot
argument_list|,
literal|"target"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Generating data..."
argument_list|)
expr_stmt|;
name|auxFs
operator|.
name|mkdirs
argument_list|(
name|testRoot
argument_list|)
expr_stmt|;
name|byte
index|[]
name|zeroes
init|=
name|ContractTestUtils
operator|.
name|dataset
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
for|for
control|(
name|Path
name|aSource
range|:
name|source
control|)
block|{
try|try
init|(
name|FSDataOutputStream
name|out
init|=
name|auxFs
operator|.
name|create
argument_list|(
name|aSource
argument_list|)
init|)
block|{
for|for
control|(
name|int
name|mb
init|=
literal|0
init|;
name|mb
operator|<
literal|20
condition|;
name|mb
operator|++
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"{}: Block {}..."
argument_list|,
name|aSource
argument_list|,
name|mb
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|zeroes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Data generated..."
argument_list|)
expr_stmt|;
block|}
DECL|method|getRestrictedFileSystem ()
specifier|private
name|S3AFileSystem
name|getRestrictedFileSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|getConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MAX_THREADS
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MAX_TOTAL_TASKS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MIN_MULTIPART_THRESHOLD
argument_list|,
literal|"10M"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MULTIPART_SIZE
argument_list|,
literal|"5M"
argument_list|)
expr_stmt|;
name|S3AFileSystem
name|s3a
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|URI
name|rootURI
init|=
operator|new
name|URI
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|TEST_FS_S3A_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|s3a
operator|.
name|initialize
argument_list|(
name|rootURI
argument_list|,
name|conf
argument_list|)
expr_stmt|;
return|return
name|s3a
return|;
block|}
DECL|method|getNormalFileSystem ()
specifier|private
name|S3AFileSystem
name|getNormalFileSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|S3AFileSystem
name|s3a
init|=
operator|new
name|S3AFileSystem
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
name|createScaleConfiguration
argument_list|()
decl_stmt|;
name|URI
name|rootURI
init|=
operator|new
name|URI
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|TEST_FS_S3A_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|s3a
operator|.
name|initialize
argument_list|(
name|rootURI
argument_list|,
name|conf
argument_list|)
expr_stmt|;
return|return
name|s3a
return|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|teardown
argument_list|()
expr_stmt|;
if|if
condition|(
name|auxFs
operator|!=
literal|null
condition|)
block|{
name|auxFs
operator|.
name|delete
argument_list|(
name|testRoot
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Attempts to trigger a deadlock that would happen if any bounded resource    * pool became saturated with control tasks that depended on other tasks    * that now can't enter the resource pool to get completed.    */
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testParallelRename ()
specifier|public
name|void
name|testParallelRename
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
throws|,
name|IOException
block|{
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|concurrentRenames
argument_list|,
operator|new
name|ThreadFactory
argument_list|()
block|{
specifier|private
name|AtomicInteger
name|count
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
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
name|r
argument_list|,
literal|"testParallelRename"
operator|+
name|count
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
operator|(
operator|(
name|ThreadPoolExecutor
operator|)
name|executor
operator|)
operator|.
name|prestartAllCoreThreads
argument_list|()
expr_stmt|;
name|Future
argument_list|<
name|Boolean
argument_list|>
index|[]
name|futures
init|=
operator|new
name|Future
index|[
name|concurrentRenames
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|concurrentRenames
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|index
init|=
name|i
decl_stmt|;
name|futures
index|[
name|i
index|]
operator|=
name|executor
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|NanoTimer
name|timer
init|=
operator|new
name|NanoTimer
argument_list|()
decl_stmt|;
name|boolean
name|result
init|=
name|fs
operator|.
name|rename
argument_list|(
name|source
index|[
name|index
index|]
argument_list|,
name|target
index|[
name|index
index|]
argument_list|)
decl_stmt|;
name|timer
operator|.
name|end
argument_list|(
literal|"parallel rename %d"
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Rename {} ran from {} to {}"
argument_list|,
name|index
argument_list|,
name|timer
operator|.
name|getStartTime
argument_list|()
argument_list|,
name|timer
operator|.
name|getEndTime
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for tasks to complete..."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Deadlock may have occurred if nothing else is logged"
operator|+
literal|" or the test times out"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|concurrentRenames
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"No future "
operator|+
name|i
argument_list|,
name|futures
index|[
name|i
index|]
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertPathExists
argument_list|(
literal|"target path"
argument_list|,
name|target
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
literal|"source path"
argument_list|,
name|source
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"All tasks have completed successfully"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

