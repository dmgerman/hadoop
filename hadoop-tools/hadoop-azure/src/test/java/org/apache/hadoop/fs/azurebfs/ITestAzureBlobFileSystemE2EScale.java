begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
package|;
end_package

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
name|Random
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
name|Future
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
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FSDataInputStream
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
name|Path
import|;
end_import

begin_comment
comment|/**  * Test end to end between ABFS client and ABFS server with heavy traffic.  */
end_comment

begin_class
DECL|class|ITestAzureBlobFileSystemE2EScale
specifier|public
class|class
name|ITestAzureBlobFileSystemE2EScale
extends|extends
name|AbstractAbfsScaleTest
block|{
DECL|field|TEN
specifier|private
specifier|static
specifier|final
name|int
name|TEN
init|=
literal|10
decl_stmt|;
DECL|field|ONE_THOUSAND
specifier|private
specifier|static
specifier|final
name|int
name|ONE_THOUSAND
init|=
literal|1000
decl_stmt|;
DECL|field|BASE_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BASE_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|ONE_MB
specifier|private
specifier|static
specifier|final
name|int
name|ONE_MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|DEFAULT_WRITE_TIMES
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_WRITE_TIMES
init|=
literal|100
decl_stmt|;
DECL|field|TEST_FILE
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_FILE
init|=
operator|new
name|Path
argument_list|(
literal|"ITestAzureBlobFileSystemE2EScale"
argument_list|)
decl_stmt|;
DECL|method|ITestAzureBlobFileSystemE2EScale ()
specifier|public
name|ITestAzureBlobFileSystemE2EScale
parameter_list|()
block|{   }
annotation|@
name|Test
DECL|method|testWriteHeavyBytesToFileAcrossThreads ()
specifier|public
name|void
name|testWriteHeavyBytesToFileAcrossThreads
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|FSDataOutputStream
name|stream
init|=
name|fs
operator|.
name|create
argument_list|(
name|TEST_FILE
argument_list|)
decl_stmt|;
name|ExecutorService
name|es
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|TEN
argument_list|)
decl_stmt|;
name|int
name|testWriteBufferSize
init|=
literal|2
operator|*
name|TEN
operator|*
name|ONE_THOUSAND
operator|*
name|BASE_SIZE
decl_stmt|;
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|testWriteBufferSize
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|Void
argument_list|>
argument_list|>
name|tasks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|operationCount
init|=
name|DEFAULT_WRITE_TIMES
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
name|operationCount
condition|;
name|i
operator|++
control|)
block|{
name|Callable
argument_list|<
name|Void
argument_list|>
name|callable
init|=
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|stream
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
name|tasks
operator|.
name|add
argument_list|(
name|es
operator|.
name|submit
argument_list|(
name|callable
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Future
argument_list|<
name|Void
argument_list|>
name|task
range|:
name|tasks
control|)
block|{
name|task
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|tasks
operator|.
name|clear
argument_list|()
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|es
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|FileStatus
name|fileStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|TEST_FILE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|testWriteBufferSize
operator|*
name|operationCount
argument_list|,
name|fileStatus
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadWriteHeavyBytesToFileWithStatistics ()
specifier|public
name|void
name|testReadWriteHeavyBytesToFileWithStatistics
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|FileSystem
operator|.
name|Statistics
name|abfsStatistics
decl_stmt|;
name|int
name|testBufferSize
decl_stmt|;
specifier|final
name|byte
index|[]
name|sourceData
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|stream
init|=
name|fs
operator|.
name|create
argument_list|(
name|TEST_FILE
argument_list|)
init|)
block|{
name|abfsStatistics
operator|=
name|fs
operator|.
name|getFsStatistics
argument_list|()
expr_stmt|;
name|abfsStatistics
operator|.
name|reset
argument_list|()
expr_stmt|;
name|testBufferSize
operator|=
literal|5
operator|*
name|TEN
operator|*
name|ONE_THOUSAND
operator|*
name|BASE_SIZE
expr_stmt|;
name|sourceData
operator|=
operator|new
name|byte
index|[
name|testBufferSize
index|]
expr_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|sourceData
argument_list|)
expr_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|sourceData
argument_list|)
expr_stmt|;
block|}
specifier|final
name|byte
index|[]
name|remoteData
init|=
operator|new
name|byte
index|[
name|testBufferSize
index|]
decl_stmt|;
name|int
name|bytesRead
decl_stmt|;
try|try
init|(
name|FSDataInputStream
name|inputStream
init|=
name|fs
operator|.
name|open
argument_list|(
name|TEST_FILE
argument_list|,
literal|4
operator|*
name|ONE_MB
argument_list|)
init|)
block|{
name|bytesRead
operator|=
name|inputStream
operator|.
name|read
argument_list|(
name|remoteData
argument_list|)
expr_stmt|;
block|}
name|String
name|stats
init|=
name|abfsStatistics
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Bytes read in "
operator|+
name|stats
argument_list|,
name|remoteData
operator|.
name|length
argument_list|,
name|abfsStatistics
operator|.
name|getBytesRead
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bytes written in "
operator|+
name|stats
argument_list|,
name|sourceData
operator|.
name|length
argument_list|,
name|abfsStatistics
operator|.
name|getBytesWritten
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bytesRead from read() call"
argument_list|,
name|testBufferSize
argument_list|,
name|bytesRead
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
literal|"round tripped data"
argument_list|,
name|sourceData
argument_list|,
name|remoteData
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

