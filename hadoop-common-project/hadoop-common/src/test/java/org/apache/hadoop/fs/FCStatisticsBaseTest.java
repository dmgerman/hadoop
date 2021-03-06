begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

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
name|FileContextTestHelper
operator|.
name|createFile
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|hadoop
operator|.
name|fs
operator|.
name|FileSystem
operator|.
name|Statistics
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
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
name|Uninterruptibles
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
comment|/**  *<p>  *   Base class to test {@link FileContext} Statistics.  *</p>  */
end_comment

begin_class
DECL|class|FCStatisticsBaseTest
specifier|public
specifier|abstract
class|class
name|FCStatisticsBaseTest
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
name|FCStatisticsBaseTest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|blockSize
specifier|static
specifier|protected
name|int
name|blockSize
init|=
literal|512
decl_stmt|;
DECL|field|numBlocks
specifier|static
specifier|protected
name|int
name|numBlocks
init|=
literal|1
decl_stmt|;
DECL|field|fileContextTestHelper
specifier|protected
specifier|final
name|FileContextTestHelper
name|fileContextTestHelper
init|=
operator|new
name|FileContextTestHelper
argument_list|()
decl_stmt|;
comment|//fc should be set appropriately by the deriving test.
DECL|field|fc
specifier|protected
specifier|static
name|FileContext
name|fc
init|=
literal|null
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testStatisticsOperations ()
specifier|public
name|void
name|testStatisticsOperations
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Statistics
name|stats
init|=
operator|new
name|Statistics
argument_list|(
literal|"file"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|stats
operator|.
name|getBytesRead
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|stats
operator|.
name|getBytesWritten
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|getWriteOps
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|incrementBytesWritten
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1000L
argument_list|,
name|stats
operator|.
name|getBytesWritten
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|getWriteOps
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|incrementWriteOps
argument_list|(
literal|123
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|123
argument_list|,
name|stats
operator|.
name|getWriteOps
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|stats
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|Uninterruptibles
operator|.
name|joinUninterruptibly
argument_list|(
name|thread
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|124
argument_list|,
name|stats
operator|.
name|getWriteOps
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test copy constructor and reset function
name|Statistics
name|stats2
init|=
operator|new
name|Statistics
argument_list|(
name|stats
argument_list|)
decl_stmt|;
name|stats
operator|.
name|reset
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|getWriteOps
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|stats
operator|.
name|getBytesWritten
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|stats
operator|.
name|getBytesRead
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|124
argument_list|,
name|stats2
operator|.
name|getWriteOps
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1000L
argument_list|,
name|stats2
operator|.
name|getBytesWritten
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|stats2
operator|.
name|getBytesRead
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStatistics ()
specifier|public
name|void
name|testStatistics
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|URI
name|fsUri
init|=
name|getFsUri
argument_list|()
decl_stmt|;
name|Statistics
name|stats
init|=
name|FileContext
operator|.
name|getStatistics
argument_list|(
name|fsUri
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|getBytesRead
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|filePath
init|=
name|fileContextTestHelper
operator|.
name|getTestRootPath
argument_list|(
name|fc
argument_list|,
literal|"file1"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|fc
argument_list|,
name|filePath
argument_list|,
name|numBlocks
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|getBytesRead
argument_list|()
argument_list|)
expr_stmt|;
name|verifyWrittenBytes
argument_list|(
name|stats
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|fstr
init|=
name|fc
operator|.
name|open
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|blockSize
index|]
decl_stmt|;
name|int
name|bytesRead
init|=
name|fstr
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|fstr
operator|.
name|read
argument_list|(
literal|0
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|blockSize
argument_list|,
name|bytesRead
argument_list|)
expr_stmt|;
name|verifyReadBytes
argument_list|(
name|stats
argument_list|)
expr_stmt|;
name|verifyWrittenBytes
argument_list|(
name|stats
argument_list|)
expr_stmt|;
name|verifyReadBytes
argument_list|(
name|FileContext
operator|.
name|getStatistics
argument_list|(
name|getFsUri
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|URI
argument_list|,
name|Statistics
argument_list|>
name|statsMap
init|=
name|FileContext
operator|.
name|getAllStatistics
argument_list|()
decl_stmt|;
name|URI
name|exactUri
init|=
name|getSchemeAuthorityUri
argument_list|()
decl_stmt|;
name|verifyWrittenBytes
argument_list|(
name|statsMap
operator|.
name|get
argument_list|(
name|exactUri
argument_list|)
argument_list|)
expr_stmt|;
name|fc
operator|.
name|delete
argument_list|(
name|filePath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|70000
argument_list|)
DECL|method|testStatisticsThreadLocalDataCleanUp ()
specifier|public
name|void
name|testStatisticsThreadLocalDataCleanUp
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Statistics
name|stats
init|=
operator|new
name|Statistics
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
comment|// create a small thread pool to test the statistics
specifier|final
name|int
name|size
init|=
literal|2
decl_stmt|;
name|ExecutorService
name|es
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|>
name|tasks
init|=
operator|new
name|ArrayList
argument_list|<
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|>
argument_list|(
name|size
argument_list|)
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|tasks
operator|.
name|add
argument_list|(
operator|new
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
specifier|public
name|Boolean
name|call
parameter_list|()
block|{
comment|// this populates the data set in statistics
name|stats
operator|.
name|incrementReadOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|// run the threads
name|es
operator|.
name|invokeAll
argument_list|(
name|tasks
argument_list|)
expr_stmt|;
comment|// assert that the data size is exactly the number of threads
specifier|final
name|AtomicInteger
name|allDataSize
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|allDataSize
operator|.
name|set
argument_list|(
name|stats
operator|.
name|getAllThreadLocalDataSize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|size
argument_list|,
name|allDataSize
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|size
argument_list|,
name|stats
operator|.
name|getReadOps
argument_list|()
argument_list|)
expr_stmt|;
comment|// force the GC to collect the threads by shutting down the thread pool
name|es
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|es
operator|.
name|awaitTermination
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
name|es
operator|=
literal|null
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
comment|// force GC to garbage collect threads
comment|// wait for up to 60 seconds
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
name|int
name|size
init|=
name|stats
operator|.
name|getAllThreadLocalDataSize
argument_list|()
decl_stmt|;
name|allDataSize
operator|.
name|set
argument_list|(
name|size
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"not all references have been cleaned up; still "
operator|+
name|allDataSize
operator|.
name|get
argument_list|()
operator|+
literal|" references left"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"triggering another GC"
argument_list|)
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
argument_list|,
literal|500
argument_list|,
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|allDataSize
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|size
argument_list|,
name|stats
operator|.
name|getReadOps
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Bytes read may be different for different file systems. This method should    * throw assertion error if bytes read are incorrect.    *     * @param stats    */
DECL|method|verifyReadBytes (Statistics stats)
specifier|protected
specifier|abstract
name|void
name|verifyReadBytes
parameter_list|(
name|Statistics
name|stats
parameter_list|)
function_decl|;
comment|/**    * Bytes written may be different for different file systems. This method should    * throw assertion error if bytes written are incorrect.    *     * @param stats    */
DECL|method|verifyWrittenBytes (Statistics stats)
specifier|protected
specifier|abstract
name|void
name|verifyWrittenBytes
parameter_list|(
name|Statistics
name|stats
parameter_list|)
function_decl|;
comment|/**    * Returns the filesystem uri. Should be set    * @return URI    */
DECL|method|getFsUri ()
specifier|protected
specifier|abstract
name|URI
name|getFsUri
parameter_list|()
function_decl|;
DECL|method|getSchemeAuthorityUri ()
specifier|protected
name|URI
name|getSchemeAuthorityUri
parameter_list|()
block|{
name|URI
name|uri
init|=
name|getFsUri
argument_list|()
decl_stmt|;
name|String
name|SchemeAuthString
init|=
name|uri
operator|.
name|getScheme
argument_list|()
operator|+
literal|"://"
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|getAuthority
argument_list|()
operator|==
literal|null
condition|)
block|{
name|SchemeAuthString
operator|+=
literal|"/"
expr_stmt|;
block|}
else|else
block|{
name|SchemeAuthString
operator|+=
name|uri
operator|.
name|getAuthority
argument_list|()
expr_stmt|;
block|}
return|return
name|URI
operator|.
name|create
argument_list|(
name|SchemeAuthString
argument_list|)
return|;
block|}
block|}
end_class

end_unit

