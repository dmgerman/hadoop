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
name|io
operator|.
name|InputStream
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
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|StreamCapabilities
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
name|azurebfs
operator|.
name|services
operator|.
name|AbfsOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|IsEqual
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|IsNot
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
comment|/**  * Test flush operation.  * This class cannot be run in parallel test mode--check comments in  * testWriteHeavyBytesToFileSyncFlush().  */
end_comment

begin_class
DECL|class|ITestAzureBlobFileSystemFlush
specifier|public
class|class
name|ITestAzureBlobFileSystemFlush
extends|extends
name|AbstractAbfsScaleTest
block|{
DECL|field|BASE_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BASE_SIZE
init|=
literal|1024
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
DECL|field|TEST_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|TEST_BUFFER_SIZE
init|=
literal|5
operator|*
name|ONE_THOUSAND
operator|*
name|BASE_SIZE
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
DECL|field|FLUSH_TIMES
specifier|private
specifier|static
specifier|final
name|int
name|FLUSH_TIMES
init|=
literal|200
decl_stmt|;
DECL|field|THREAD_SLEEP_TIME
specifier|private
specifier|static
specifier|final
name|int
name|THREAD_SLEEP_TIME
init|=
literal|1000
decl_stmt|;
DECL|field|TEST_FILE_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|TEST_FILE_LENGTH
init|=
literal|1024
operator|*
literal|1024
operator|*
literal|8
decl_stmt|;
DECL|field|WAITING_TIME
specifier|private
specifier|static
specifier|final
name|int
name|WAITING_TIME
init|=
literal|1000
decl_stmt|;
DECL|method|ITestAzureBlobFileSystemFlush ()
specifier|public
name|ITestAzureBlobFileSystemFlush
parameter_list|()
throws|throws
name|Exception
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAbfsOutputStreamAsyncFlushWithRetainUncommittedData ()
specifier|public
name|void
name|testAbfsOutputStreamAsyncFlushWithRetainUncommittedData
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
name|Path
name|testFilePath
init|=
name|path
argument_list|(
name|methodName
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|b
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
name|testFilePath
argument_list|)
init|)
block|{
name|b
operator|=
operator|new
name|byte
index|[
name|TEST_BUFFER_SIZE
index|]
expr_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|b
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|stream
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|FLUSH_TIMES
condition|;
name|j
operator|++
control|)
block|{
name|stream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|byte
index|[]
name|r
init|=
operator|new
name|byte
index|[
name|TEST_BUFFER_SIZE
index|]
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
name|testFilePath
argument_list|,
literal|4
operator|*
name|ONE_MB
argument_list|)
init|)
block|{
while|while
condition|(
name|inputStream
operator|.
name|available
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|int
name|result
init|=
name|inputStream
operator|.
name|read
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
literal|"read returned -1"
argument_list|,
operator|-
literal|1
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
literal|"buffer read from stream"
argument_list|,
name|r
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testAbfsOutputStreamSyncFlush ()
specifier|public
name|void
name|testAbfsOutputStreamSyncFlush
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
name|Path
name|testFilePath
init|=
name|path
argument_list|(
name|methodName
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|b
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
name|testFilePath
argument_list|)
init|)
block|{
name|b
operator|=
operator|new
name|byte
index|[
name|TEST_BUFFER_SIZE
index|]
expr_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|b
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
name|FLUSH_TIMES
condition|;
name|i
operator|++
control|)
block|{
name|stream
operator|.
name|hsync
argument_list|()
expr_stmt|;
name|stream
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|byte
index|[]
name|r
init|=
operator|new
name|byte
index|[
name|TEST_BUFFER_SIZE
index|]
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
name|testFilePath
argument_list|,
literal|4
operator|*
name|ONE_MB
argument_list|)
init|)
block|{
name|int
name|result
init|=
name|inputStream
operator|.
name|read
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|r
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWriteHeavyBytesToFileSyncFlush ()
specifier|public
name|void
name|testWriteHeavyBytesToFileSyncFlush
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
name|Path
name|testFilePath
init|=
name|path
argument_list|(
name|methodName
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
name|ExecutorService
name|es
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
name|testFilePath
argument_list|)
init|)
block|{
name|es
operator|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|10
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|TEST_BUFFER_SIZE
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|FLUSH_TIMES
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
name|boolean
name|shouldStop
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|shouldStop
condition|)
block|{
name|shouldStop
operator|=
literal|true
expr_stmt|;
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
if|if
condition|(
operator|!
name|task
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|stream
operator|.
name|hsync
argument_list|()
expr_stmt|;
name|shouldStop
operator|=
literal|false
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|THREAD_SLEEP_TIME
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|tasks
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
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
name|testFilePath
argument_list|)
decl_stmt|;
name|long
name|expectedWrites
init|=
operator|(
name|long
operator|)
name|TEST_BUFFER_SIZE
operator|*
name|FLUSH_TIMES
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong file length in "
operator|+
name|testFilePath
argument_list|,
name|expectedWrites
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
DECL|method|testWriteHeavyBytesToFileAsyncFlush ()
specifier|public
name|void
name|testWriteHeavyBytesToFileAsyncFlush
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
name|ExecutorService
name|es
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|testFilePath
init|=
name|path
argument_list|(
name|methodName
operator|.
name|getMethodName
argument_list|()
argument_list|)
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
name|testFilePath
argument_list|)
init|)
block|{
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|TEST_BUFFER_SIZE
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|FLUSH_TIMES
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
name|boolean
name|shouldStop
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|shouldStop
condition|)
block|{
name|shouldStop
operator|=
literal|true
expr_stmt|;
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
if|if
condition|(
operator|!
name|task
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|stream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|shouldStop
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|THREAD_SLEEP_TIME
argument_list|)
expr_stmt|;
name|tasks
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
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
name|testFilePath
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|long
operator|)
name|TEST_BUFFER_SIZE
operator|*
name|FLUSH_TIMES
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
DECL|method|testFlushWithOutputStreamFlushEnabled ()
specifier|public
name|void
name|testFlushWithOutputStreamFlushEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|testFlush
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFlushWithOutputStreamFlushDisabled ()
specifier|public
name|void
name|testFlushWithOutputStreamFlushDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|testFlush
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testFlush (boolean disableOutputStreamFlush)
specifier|private
name|void
name|testFlush
parameter_list|(
name|boolean
name|disableOutputStreamFlush
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
operator|(
name|AzureBlobFileSystem
operator|)
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// Simulate setting "fs.azure.disable.outputstream.flush" to true or false
name|fs
operator|.
name|getAbfsStore
argument_list|()
operator|.
name|getAbfsConfiguration
argument_list|()
operator|.
name|setDisableOutputStreamFlush
argument_list|(
name|disableOutputStreamFlush
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|testFilePath
init|=
name|path
argument_list|(
name|methodName
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
name|getRandomBytesArray
argument_list|()
decl_stmt|;
comment|// The test case must write "fs.azure.write.request.size" bytes
comment|// to the stream in order for the data to be uploaded to storage.
name|assertEquals
argument_list|(
name|fs
operator|.
name|getAbfsStore
argument_list|()
operator|.
name|getAbfsConfiguration
argument_list|()
operator|.
name|getWriteBufferSize
argument_list|()
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
try|try
init|(
name|FSDataOutputStream
name|stream
init|=
name|fs
operator|.
name|create
argument_list|(
name|testFilePath
argument_list|)
init|)
block|{
name|stream
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
comment|// Write asynchronously uploads data, so we must wait for completion
name|AbfsOutputStream
name|abfsStream
init|=
operator|(
name|AbfsOutputStream
operator|)
name|stream
operator|.
name|getWrappedStream
argument_list|()
decl_stmt|;
name|abfsStream
operator|.
name|waitForPendingUploads
argument_list|()
expr_stmt|;
comment|// Flush commits the data so it can be read.
name|stream
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// Verify that the data can be read if disableOutputStreamFlush is
comment|// false; and otherwise cannot be read.
name|validate
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|testFilePath
argument_list|)
argument_list|,
name|buffer
argument_list|,
operator|!
name|disableOutputStreamFlush
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testHflushWithFlushEnabled ()
specifier|public
name|void
name|testHflushWithFlushEnabled
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|this
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
name|getRandomBytesArray
argument_list|()
decl_stmt|;
name|String
name|fileName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|testFilePath
init|=
name|path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|stream
init|=
name|getStreamAfterWrite
argument_list|(
name|fs
argument_list|,
name|testFilePath
argument_list|,
name|buffer
argument_list|,
literal|true
argument_list|)
init|)
block|{
name|stream
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|validate
argument_list|(
name|fs
argument_list|,
name|testFilePath
argument_list|,
name|buffer
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testHflushWithFlushDisabled ()
specifier|public
name|void
name|testHflushWithFlushDisabled
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|this
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
name|getRandomBytesArray
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|testFilePath
init|=
name|path
argument_list|(
name|methodName
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|stream
init|=
name|getStreamAfterWrite
argument_list|(
name|fs
argument_list|,
name|testFilePath
argument_list|,
name|buffer
argument_list|,
literal|false
argument_list|)
init|)
block|{
name|stream
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|validate
argument_list|(
name|fs
argument_list|,
name|testFilePath
argument_list|,
name|buffer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testHsyncWithFlushEnabled ()
specifier|public
name|void
name|testHsyncWithFlushEnabled
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|this
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
name|getRandomBytesArray
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|testFilePath
init|=
name|path
argument_list|(
name|methodName
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|stream
init|=
name|getStreamAfterWrite
argument_list|(
name|fs
argument_list|,
name|testFilePath
argument_list|,
name|buffer
argument_list|,
literal|true
argument_list|)
init|)
block|{
name|stream
operator|.
name|hsync
argument_list|()
expr_stmt|;
name|validate
argument_list|(
name|fs
argument_list|,
name|testFilePath
argument_list|,
name|buffer
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testStreamCapabilitiesWithFlushDisabled ()
specifier|public
name|void
name|testStreamCapabilitiesWithFlushDisabled
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|this
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
name|getRandomBytesArray
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|testFilePath
init|=
name|path
argument_list|(
name|methodName
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|stream
init|=
name|getStreamAfterWrite
argument_list|(
name|fs
argument_list|,
name|testFilePath
argument_list|,
name|buffer
argument_list|,
literal|false
argument_list|)
init|)
block|{
name|assertFalse
argument_list|(
name|stream
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|HFLUSH
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|stream
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|HSYNC
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|stream
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|DROPBEHIND
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|stream
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|READAHEAD
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|stream
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|UNBUFFER
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testStreamCapabilitiesWithFlushEnabled ()
specifier|public
name|void
name|testStreamCapabilitiesWithFlushEnabled
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|this
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
name|getRandomBytesArray
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|testFilePath
init|=
name|path
argument_list|(
name|methodName
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|stream
init|=
name|getStreamAfterWrite
argument_list|(
name|fs
argument_list|,
name|testFilePath
argument_list|,
name|buffer
argument_list|,
literal|true
argument_list|)
init|)
block|{
name|assertTrue
argument_list|(
name|stream
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|HFLUSH
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stream
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|HSYNC
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|stream
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|DROPBEHIND
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|stream
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|READAHEAD
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|stream
operator|.
name|hasCapability
argument_list|(
name|StreamCapabilities
operator|.
name|UNBUFFER
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testHsyncWithFlushDisabled ()
specifier|public
name|void
name|testHsyncWithFlushDisabled
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|this
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
name|getRandomBytesArray
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|testFilePath
init|=
name|path
argument_list|(
name|methodName
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|stream
init|=
name|getStreamAfterWrite
argument_list|(
name|fs
argument_list|,
name|testFilePath
argument_list|,
name|buffer
argument_list|,
literal|false
argument_list|)
init|)
block|{
name|stream
operator|.
name|hsync
argument_list|()
expr_stmt|;
name|validate
argument_list|(
name|fs
argument_list|,
name|testFilePath
argument_list|,
name|buffer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getRandomBytesArray ()
specifier|private
name|byte
index|[]
name|getRandomBytesArray
parameter_list|()
block|{
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|TEST_FILE_LENGTH
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
return|return
name|b
return|;
block|}
DECL|method|getStreamAfterWrite (AzureBlobFileSystem fs, Path path, byte[] buffer, boolean enableFlush)
specifier|private
name|FSDataOutputStream
name|getStreamAfterWrite
parameter_list|(
name|AzureBlobFileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|boolean
name|enableFlush
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|getAbfsStore
argument_list|()
operator|.
name|getAbfsConfiguration
argument_list|()
operator|.
name|setEnableFlush
argument_list|(
name|enableFlush
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|stream
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
return|return
name|stream
return|;
block|}
DECL|method|validate (InputStream stream, byte[] writeBuffer, boolean isEqual)
specifier|private
name|void
name|validate
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|byte
index|[]
name|writeBuffer
parameter_list|,
name|boolean
name|isEqual
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|byte
index|[]
name|readBuffer
init|=
operator|new
name|byte
index|[
name|writeBuffer
operator|.
name|length
index|]
decl_stmt|;
name|int
name|numBytesRead
init|=
name|stream
operator|.
name|read
argument_list|(
name|readBuffer
argument_list|,
literal|0
argument_list|,
name|readBuffer
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|isEqual
condition|)
block|{
name|assertArrayEquals
argument_list|(
literal|"Bytes read do not match bytes written."
argument_list|,
name|writeBuffer
argument_list|,
name|readBuffer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
literal|"Bytes read unexpectedly match bytes written."
argument_list|,
name|readBuffer
argument_list|,
name|IsNot
operator|.
name|not
argument_list|(
name|IsEqual
operator|.
name|equalTo
argument_list|(
name|writeBuffer
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|validate (FileSystem fs, Path path, byte[] writeBuffer, boolean isEqual)
specifier|private
name|void
name|validate
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|byte
index|[]
name|writeBuffer
parameter_list|,
name|boolean
name|isEqual
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|filePath
init|=
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
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
name|path
argument_list|)
init|)
block|{
name|byte
index|[]
name|readBuffer
init|=
operator|new
name|byte
index|[
name|TEST_FILE_LENGTH
index|]
decl_stmt|;
name|int
name|numBytesRead
init|=
name|inputStream
operator|.
name|read
argument_list|(
name|readBuffer
argument_list|,
literal|0
argument_list|,
name|readBuffer
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|isEqual
condition|)
block|{
name|assertArrayEquals
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Bytes read do not match bytes written to %1$s"
argument_list|,
name|filePath
argument_list|)
argument_list|,
name|writeBuffer
argument_list|,
name|readBuffer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Bytes read unexpectedly match bytes written to %1$s"
argument_list|,
name|filePath
argument_list|)
argument_list|,
name|readBuffer
argument_list|,
name|IsNot
operator|.
name|not
argument_list|(
name|IsEqual
operator|.
name|equalTo
argument_list|(
name|writeBuffer
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

