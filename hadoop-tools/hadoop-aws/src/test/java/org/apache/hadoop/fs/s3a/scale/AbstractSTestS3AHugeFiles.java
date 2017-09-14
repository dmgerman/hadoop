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
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|event
operator|.
name|ProgressEvent
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|event
operator|.
name|ProgressEventType
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|event
operator|.
name|ProgressListener
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
name|junit
operator|.
name|FixMethodOrder
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
name|junit
operator|.
name|runners
operator|.
name|MethodSorters
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
name|StorageStatistics
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
name|S3AInstrumentation
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
name|Statistic
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
name|Progressable
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
name|contract
operator|.
name|ContractTestUtils
operator|.
name|*
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
name|S3ATestUtils
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Scale test which creates a huge file.  *  *<b>Important:</b> the order in which these tests execute is fixed to  * alphabetical order. Test cases are numbered {@code test_123_} to impose  * an ordering based on the numbers.  *  * Having this ordering allows the tests to assume that the huge file  * exists. Even so: they should all have a {@link #assumeHugeFileExists()}  * check at the start, in case an individual test is executed.  */
end_comment

begin_class
annotation|@
name|FixMethodOrder
argument_list|(
name|MethodSorters
operator|.
name|NAME_ASCENDING
argument_list|)
DECL|class|AbstractSTestS3AHugeFiles
specifier|public
specifier|abstract
class|class
name|AbstractSTestS3AHugeFiles
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
name|AbstractSTestS3AHugeFiles
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_UPLOAD_BLOCKSIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_UPLOAD_BLOCKSIZE
init|=
literal|64
operator|*
name|_1KB
decl_stmt|;
DECL|field|DEFAULT_PARTITION_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_PARTITION_SIZE
init|=
literal|"8M"
decl_stmt|;
DECL|field|scaleTestDir
specifier|private
name|Path
name|scaleTestDir
decl_stmt|;
DECL|field|hugefile
specifier|private
name|Path
name|hugefile
decl_stmt|;
DECL|field|hugefileRenamed
specifier|private
name|Path
name|hugefileRenamed
decl_stmt|;
DECL|field|uploadBlockSize
specifier|private
name|int
name|uploadBlockSize
init|=
name|DEFAULT_UPLOAD_BLOCKSIZE
decl_stmt|;
DECL|field|partitionSize
specifier|private
name|int
name|partitionSize
decl_stmt|;
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
specifier|final
name|Path
name|testPath
init|=
name|getTestPath
argument_list|()
decl_stmt|;
name|scaleTestDir
operator|=
operator|new
name|Path
argument_list|(
name|testPath
argument_list|,
literal|"scale"
argument_list|)
expr_stmt|;
name|hugefile
operator|=
operator|new
name|Path
argument_list|(
name|scaleTestDir
argument_list|,
literal|"hugefile"
argument_list|)
expr_stmt|;
name|hugefileRenamed
operator|=
operator|new
name|Path
argument_list|(
name|scaleTestDir
argument_list|,
literal|"hugefileRenamed"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Note that this can get called before test setup.    * @return the configuration to use.    */
annotation|@
name|Override
DECL|method|createScaleConfiguration ()
specifier|protected
name|Configuration
name|createScaleConfiguration
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|super
operator|.
name|createScaleConfiguration
argument_list|()
decl_stmt|;
name|partitionSize
operator|=
operator|(
name|int
operator|)
name|getTestPropertyBytes
argument_list|(
name|conf
argument_list|,
name|KEY_HUGE_PARTITION_SIZE
argument_list|,
name|DEFAULT_PARTITION_SIZE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Partition size too small: "
operator|+
name|partitionSize
argument_list|,
name|partitionSize
operator|>
name|MULTIPART_MIN_SIZE
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|SOCKET_SEND_BUFFER
argument_list|,
name|_1MB
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|SOCKET_RECV_BUFFER
argument_list|,
name|_1MB
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|MIN_MULTIPART_THRESHOLD
argument_list|,
name|partitionSize
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MULTIPART_SIZE
argument_list|,
name|partitionSize
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|USER_AGENT_PREFIX
argument_list|,
literal|"STestS3AHugeFileCreate"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FAST_UPLOAD_BUFFER
argument_list|,
name|getBlockOutputBufferName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
comment|/**    * The name of the buffering mechanism to use.    * @return a buffering mechanism    */
DECL|method|getBlockOutputBufferName ()
specifier|protected
specifier|abstract
name|String
name|getBlockOutputBufferName
parameter_list|()
function_decl|;
annotation|@
name|Test
DECL|method|test_010_CreateHugeFile ()
specifier|public
name|void
name|test_010_CreateHugeFile
parameter_list|()
throws|throws
name|IOException
block|{
name|assertFalse
argument_list|(
literal|"Please run this test sequentially to avoid timeouts"
operator|+
literal|" and bandwidth problems"
argument_list|,
name|isParallelExecution
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|filesize
init|=
name|getTestPropertyBytes
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|KEY_HUGE_FILESIZE
argument_list|,
name|DEFAULT_HUGE_FILESIZE
argument_list|)
decl_stmt|;
name|long
name|filesizeMB
init|=
name|filesize
operator|/
name|_1MB
decl_stmt|;
comment|// clean up from any previous attempts
name|deleteHugeFile
argument_list|()
expr_stmt|;
name|describe
argument_list|(
literal|"Creating file %s of size %d MB"
operator|+
literal|" with partition size %d buffered by %s"
argument_list|,
name|hugefile
argument_list|,
name|filesizeMB
argument_list|,
name|partitionSize
argument_list|,
name|getBlockOutputBufferName
argument_list|()
argument_list|)
expr_stmt|;
comment|// now do a check of available upload time, with a pessimistic bandwidth
comment|// (that of remote upload tests). If the test times out then not only is
comment|// the test outcome lost, as the follow-on tests continue, they will
comment|// overlap with the ongoing upload test, for much confusion.
name|int
name|timeout
init|=
name|getTestTimeoutSeconds
argument_list|()
decl_stmt|;
comment|// assume 1 MB/s upload bandwidth
name|int
name|bandwidth
init|=
name|_1MB
decl_stmt|;
name|long
name|uploadTime
init|=
name|filesize
operator|/
name|bandwidth
decl_stmt|;
name|assertTrue
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Timeout set in %s seconds is too low;"
operator|+
literal|" estimating upload time of %d seconds at 1 MB/s."
operator|+
literal|" Rerun tests with -D%s=%d"
argument_list|,
name|timeout
argument_list|,
name|uploadTime
argument_list|,
name|KEY_TEST_TIMEOUT
argument_list|,
name|uploadTime
operator|*
literal|2
argument_list|)
argument_list|,
name|uploadTime
operator|<
name|timeout
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"File size set in "
operator|+
name|KEY_HUGE_FILESIZE
operator|+
literal|" = "
operator|+
name|filesize
operator|+
literal|" is not a multiple of "
operator|+
name|uploadBlockSize
argument_list|,
literal|0
argument_list|,
name|filesize
operator|%
name|uploadBlockSize
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|uploadBlockSize
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
name|uploadBlockSize
condition|;
name|i
operator|++
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|i
operator|%
literal|256
argument_list|)
expr_stmt|;
block|}
name|long
name|blocks
init|=
name|filesize
operator|/
name|uploadBlockSize
decl_stmt|;
name|long
name|blocksPerMB
init|=
name|_1MB
operator|/
name|uploadBlockSize
decl_stmt|;
comment|// perform the upload.
comment|// there's lots of logging here, so that a tail -f on the output log
comment|// can give a view of what is happening.
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|StorageStatistics
name|storageStatistics
init|=
name|fs
operator|.
name|getStorageStatistics
argument_list|()
decl_stmt|;
name|String
name|putRequests
init|=
name|Statistic
operator|.
name|OBJECT_PUT_REQUESTS
operator|.
name|getSymbol
argument_list|()
decl_stmt|;
name|String
name|putBytes
init|=
name|Statistic
operator|.
name|OBJECT_PUT_BYTES
operator|.
name|getSymbol
argument_list|()
decl_stmt|;
name|Statistic
name|putRequestsActive
init|=
name|Statistic
operator|.
name|OBJECT_PUT_REQUESTS_ACTIVE
decl_stmt|;
name|Statistic
name|putBytesPending
init|=
name|Statistic
operator|.
name|OBJECT_PUT_BYTES_PENDING
decl_stmt|;
name|ContractTestUtils
operator|.
name|NanoTimer
name|timer
init|=
operator|new
name|ContractTestUtils
operator|.
name|NanoTimer
argument_list|()
decl_stmt|;
name|S3AInstrumentation
operator|.
name|OutputStreamStatistics
name|streamStatistics
decl_stmt|;
name|long
name|blocksPer10MB
init|=
name|blocksPerMB
operator|*
literal|10
decl_stmt|;
name|ProgressCallback
name|progress
init|=
operator|new
name|ProgressCallback
argument_list|(
name|timer
argument_list|)
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|hugefile
argument_list|,
literal|true
argument_list|,
name|uploadBlockSize
argument_list|,
name|progress
argument_list|)
init|)
block|{
try|try
block|{
name|streamStatistics
operator|=
name|getOutputStreamStatistics
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Wrapped output stream is not block stream: {}"
argument_list|,
name|out
operator|.
name|getWrappedStream
argument_list|()
argument_list|)
expr_stmt|;
name|streamStatistics
operator|=
literal|null
expr_stmt|;
block|}
for|for
control|(
name|long
name|block
init|=
literal|1
init|;
name|block
operator|<=
name|blocks
condition|;
name|block
operator|++
control|)
block|{
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|long
name|written
init|=
name|block
operator|*
name|uploadBlockSize
decl_stmt|;
comment|// every 10 MB and on file upload @ 100%, print some stats
if|if
condition|(
name|block
operator|%
name|blocksPer10MB
operator|==
literal|0
operator|||
name|written
operator|==
name|filesize
condition|)
block|{
name|long
name|percentage
init|=
name|written
operator|*
literal|100
operator|/
name|filesize
decl_stmt|;
name|double
name|elapsedTime
init|=
name|timer
operator|.
name|elapsedTime
argument_list|()
operator|/
literal|1.0e9
decl_stmt|;
name|double
name|writtenMB
init|=
literal|1.0
operator|*
name|written
operator|/
name|_1MB
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"[%02d%%] Buffered %.2f MB out of %d MB;"
operator|+
literal|" PUT %d bytes (%d pending) in %d operations (%d active);"
operator|+
literal|" elapsedTime=%.2fs; write to buffer bandwidth=%.2f MB/s"
argument_list|,
name|percentage
argument_list|,
name|writtenMB
argument_list|,
name|filesizeMB
argument_list|,
name|storageStatistics
operator|.
name|getLong
argument_list|(
name|putBytes
argument_list|)
argument_list|,
name|gaugeValue
argument_list|(
name|putBytesPending
argument_list|)
argument_list|,
name|storageStatistics
operator|.
name|getLong
argument_list|(
name|putRequests
argument_list|)
argument_list|,
name|gaugeValue
argument_list|(
name|putRequestsActive
argument_list|)
argument_list|,
name|elapsedTime
argument_list|,
name|writtenMB
operator|/
name|elapsedTime
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// now close the file
name|LOG
operator|.
name|info
argument_list|(
literal|"Closing stream {}"
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Statistics : {}"
argument_list|,
name|streamStatistics
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|NanoTimer
name|closeTimer
init|=
operator|new
name|ContractTestUtils
operator|.
name|NanoTimer
argument_list|()
decl_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|closeTimer
operator|.
name|end
argument_list|(
literal|"time to close() output stream"
argument_list|)
expr_stmt|;
block|}
name|timer
operator|.
name|end
argument_list|(
literal|"time to write %d MB in blocks of %d"
argument_list|,
name|filesizeMB
argument_list|,
name|uploadBlockSize
argument_list|)
expr_stmt|;
name|logFSState
argument_list|()
expr_stmt|;
name|bandwidth
argument_list|(
name|timer
argument_list|,
name|filesize
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Statistics after stream closed: {}"
argument_list|,
name|streamStatistics
argument_list|)
expr_stmt|;
name|long
name|putRequestCount
init|=
name|storageStatistics
operator|.
name|getLong
argument_list|(
name|putRequests
argument_list|)
decl_stmt|;
name|Long
name|putByteCount
init|=
name|storageStatistics
operator|.
name|getLong
argument_list|(
name|putBytes
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"PUT {} bytes in {} operations; {} MB/operation"
argument_list|,
name|putByteCount
argument_list|,
name|putRequestCount
argument_list|,
name|putByteCount
operator|/
operator|(
name|putRequestCount
operator|*
name|_1MB
operator|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Time per PUT {} nS"
argument_list|,
name|toHuman
argument_list|(
name|timer
operator|.
name|nanosPerOperation
argument_list|(
name|putRequestCount
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"active put requests in \n"
operator|+
name|fs
argument_list|,
literal|0
argument_list|,
name|gaugeValue
argument_list|(
name|putRequestsActive
argument_list|)
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|assertPathExists
argument_list|(
name|fs
argument_list|,
literal|"Huge file"
argument_list|,
name|hugefile
argument_list|)
expr_stmt|;
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|hugefile
argument_list|)
decl_stmt|;
name|ContractTestUtils
operator|.
name|assertIsFile
argument_list|(
name|hugefile
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"File size in "
operator|+
name|status
argument_list|,
name|filesize
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|progress
operator|!=
literal|null
condition|)
block|{
name|progress
operator|.
name|verifyNoFailures
argument_list|(
literal|"Put file "
operator|+
name|hugefile
operator|+
literal|" of size "
operator|+
name|filesize
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|streamStatistics
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
literal|"actively allocated blocks in "
operator|+
name|streamStatistics
argument_list|,
literal|0
argument_list|,
name|streamStatistics
operator|.
name|blocksActivelyAllocated
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Progress callback from AWS. Likely to come in on a different thread.    */
DECL|class|ProgressCallback
specifier|private
specifier|final
class|class
name|ProgressCallback
implements|implements
name|Progressable
implements|,
name|ProgressListener
block|{
DECL|field|bytesTransferred
specifier|private
name|AtomicLong
name|bytesTransferred
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|failures
specifier|private
name|AtomicInteger
name|failures
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|timer
specifier|private
specifier|final
name|ContractTestUtils
operator|.
name|NanoTimer
name|timer
decl_stmt|;
DECL|method|ProgressCallback (NanoTimer timer)
specifier|private
name|ProgressCallback
parameter_list|(
name|NanoTimer
name|timer
parameter_list|)
block|{
name|this
operator|.
name|timer
operator|=
name|timer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|progress ()
specifier|public
name|void
name|progress
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|progressChanged (ProgressEvent progressEvent)
specifier|public
name|void
name|progressChanged
parameter_list|(
name|ProgressEvent
name|progressEvent
parameter_list|)
block|{
name|ProgressEventType
name|eventType
init|=
name|progressEvent
operator|.
name|getEventType
argument_list|()
decl_stmt|;
if|if
condition|(
name|eventType
operator|.
name|isByteCountEvent
argument_list|()
condition|)
block|{
name|bytesTransferred
operator|.
name|addAndGet
argument_list|(
name|progressEvent
operator|.
name|getBytesTransferred
argument_list|()
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|eventType
condition|)
block|{
case|case
name|TRANSFER_PART_FAILED_EVENT
case|:
comment|// failure
name|failures
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Transfer failure"
argument_list|)
expr_stmt|;
break|break;
case|case
name|TRANSFER_PART_COMPLETED_EVENT
case|:
comment|// completion
name|long
name|elapsedTime
init|=
name|timer
operator|.
name|elapsedTime
argument_list|()
decl_stmt|;
name|double
name|elapsedTimeS
init|=
name|elapsedTime
operator|/
literal|1.0e9
decl_stmt|;
name|long
name|written
init|=
name|bytesTransferred
operator|.
name|get
argument_list|()
decl_stmt|;
name|long
name|writtenMB
init|=
name|written
operator|/
name|_1MB
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Event %s; total uploaded=%d MB in %.1fs;"
operator|+
literal|" effective upload bandwidth = %.2f MB/s"
argument_list|,
name|progressEvent
argument_list|,
name|writtenMB
argument_list|,
name|elapsedTimeS
argument_list|,
name|writtenMB
operator|/
name|elapsedTimeS
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
if|if
condition|(
name|eventType
operator|.
name|isByteCountEvent
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Event {}"
argument_list|,
name|progressEvent
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Event {}"
argument_list|,
name|progressEvent
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|sb
init|=
literal|"ProgressCallback{"
operator|+
literal|"bytesTransferred="
operator|+
name|bytesTransferred
operator|+
literal|", failures="
operator|+
name|failures
operator|+
literal|'}'
decl_stmt|;
return|return
name|sb
return|;
block|}
DECL|method|verifyNoFailures (String operation)
specifier|private
name|void
name|verifyNoFailures
parameter_list|(
name|String
name|operation
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Failures in "
operator|+
name|operation
operator|+
literal|": "
operator|+
name|this
argument_list|,
literal|0
argument_list|,
name|failures
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assumeHugeFileExists ()
name|void
name|assumeHugeFileExists
parameter_list|()
throws|throws
name|IOException
block|{
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|ContractTestUtils
operator|.
name|assertPathExists
argument_list|(
name|fs
argument_list|,
literal|"huge file not created"
argument_list|,
name|hugefile
argument_list|)
expr_stmt|;
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|hugefile
argument_list|)
decl_stmt|;
name|ContractTestUtils
operator|.
name|assertIsFile
argument_list|(
name|hugefile
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"File "
operator|+
name|hugefile
operator|+
literal|" is empty"
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|logFSState ()
specifier|private
name|void
name|logFSState
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"File System state after operation:\n{}"
argument_list|,
name|getFileSystem
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test_040_PositionedReadHugeFile ()
specifier|public
name|void
name|test_040_PositionedReadHugeFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|assumeHugeFileExists
argument_list|()
expr_stmt|;
specifier|final
name|String
name|encryption
init|=
name|getConf
argument_list|()
operator|.
name|getTrimmed
argument_list|(
name|SERVER_SIDE_ENCRYPTION_ALGORITHM
argument_list|)
decl_stmt|;
name|boolean
name|encrypted
init|=
name|encryption
operator|!=
literal|null
decl_stmt|;
if|if
condition|(
name|encrypted
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"File is encrypted with algorithm {}"
argument_list|,
name|encryption
argument_list|)
expr_stmt|;
block|}
name|String
name|filetype
init|=
name|encrypted
condition|?
literal|"encrypted file"
else|:
literal|"file"
decl_stmt|;
name|describe
argument_list|(
literal|"Positioned reads of %s %s"
argument_list|,
name|filetype
argument_list|,
name|hugefile
argument_list|)
expr_stmt|;
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|hugefile
argument_list|)
decl_stmt|;
name|long
name|filesize
init|=
name|status
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|int
name|ops
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|bufferSize
init|=
literal|8192
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|bufferSize
index|]
decl_stmt|;
name|long
name|eof
init|=
name|filesize
operator|-
literal|1
decl_stmt|;
name|ContractTestUtils
operator|.
name|NanoTimer
name|timer
init|=
operator|new
name|ContractTestUtils
operator|.
name|NanoTimer
argument_list|()
decl_stmt|;
name|ContractTestUtils
operator|.
name|NanoTimer
name|readAtByte0
decl_stmt|,
name|readAtByte0Again
decl_stmt|,
name|readAtEOF
decl_stmt|;
try|try
init|(
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|hugefile
argument_list|,
name|uploadBlockSize
argument_list|)
init|)
block|{
name|readAtByte0
operator|=
operator|new
name|ContractTestUtils
operator|.
name|NanoTimer
argument_list|()
expr_stmt|;
name|in
operator|.
name|readFully
argument_list|(
literal|0
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|readAtByte0
operator|.
name|end
argument_list|(
literal|"time to read data at start of file"
argument_list|)
expr_stmt|;
name|ops
operator|++
expr_stmt|;
name|readAtEOF
operator|=
operator|new
name|ContractTestUtils
operator|.
name|NanoTimer
argument_list|()
expr_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|eof
operator|-
name|bufferSize
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|readAtEOF
operator|.
name|end
argument_list|(
literal|"time to read data at end of file"
argument_list|)
expr_stmt|;
name|ops
operator|++
expr_stmt|;
name|readAtByte0Again
operator|=
operator|new
name|ContractTestUtils
operator|.
name|NanoTimer
argument_list|()
expr_stmt|;
name|in
operator|.
name|readFully
argument_list|(
literal|0
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|readAtByte0Again
operator|.
name|end
argument_list|(
literal|"time to read data at start of file again"
argument_list|)
expr_stmt|;
name|ops
operator|++
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Final stream state: {}"
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
name|long
name|mb
init|=
name|Math
operator|.
name|max
argument_list|(
name|filesize
operator|/
name|_1MB
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|logFSState
argument_list|()
expr_stmt|;
name|timer
operator|.
name|end
argument_list|(
literal|"time to performed positioned reads of %s of %d MB "
argument_list|,
name|filetype
argument_list|,
name|mb
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Time per positioned read = {} nS"
argument_list|,
name|toHuman
argument_list|(
name|timer
operator|.
name|nanosPerOperation
argument_list|(
name|ops
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test_050_readHugeFile ()
specifier|public
name|void
name|test_050_readHugeFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|assumeHugeFileExists
argument_list|()
expr_stmt|;
name|describe
argument_list|(
literal|"Reading %s"
argument_list|,
name|hugefile
argument_list|)
expr_stmt|;
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|hugefile
argument_list|)
decl_stmt|;
name|long
name|filesize
init|=
name|status
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|long
name|blocks
init|=
name|filesize
operator|/
name|uploadBlockSize
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|uploadBlockSize
index|]
decl_stmt|;
name|ContractTestUtils
operator|.
name|NanoTimer
name|timer
init|=
operator|new
name|ContractTestUtils
operator|.
name|NanoTimer
argument_list|()
decl_stmt|;
try|try
init|(
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|hugefile
argument_list|,
name|uploadBlockSize
argument_list|)
init|)
block|{
for|for
control|(
name|long
name|block
init|=
literal|0
init|;
name|block
operator|<
name|blocks
condition|;
name|block
operator|++
control|)
block|{
name|in
operator|.
name|readFully
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Final stream state: {}"
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
name|long
name|mb
init|=
name|Math
operator|.
name|max
argument_list|(
name|filesize
operator|/
name|_1MB
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|timer
operator|.
name|end
argument_list|(
literal|"time to read file of %d MB "
argument_list|,
name|mb
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Time per MB to read = {} nS"
argument_list|,
name|toHuman
argument_list|(
name|timer
operator|.
name|nanosPerOperation
argument_list|(
name|mb
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|bandwidth
argument_list|(
name|timer
argument_list|,
name|filesize
argument_list|)
expr_stmt|;
name|logFSState
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test_100_renameHugeFile ()
specifier|public
name|void
name|test_100_renameHugeFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|assumeHugeFileExists
argument_list|()
expr_stmt|;
name|describe
argument_list|(
literal|"renaming %s to %s"
argument_list|,
name|hugefile
argument_list|,
name|hugefileRenamed
argument_list|)
expr_stmt|;
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|hugefile
argument_list|)
decl_stmt|;
name|long
name|filesize
init|=
name|status
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|hugefileRenamed
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|NanoTimer
name|timer
init|=
operator|new
name|ContractTestUtils
operator|.
name|NanoTimer
argument_list|()
decl_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|hugefile
argument_list|,
name|hugefileRenamed
argument_list|)
expr_stmt|;
name|long
name|mb
init|=
name|Math
operator|.
name|max
argument_list|(
name|filesize
operator|/
name|_1MB
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|timer
operator|.
name|end
argument_list|(
literal|"time to rename file of %d MB"
argument_list|,
name|mb
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Time per MB to rename = {} nS"
argument_list|,
name|toHuman
argument_list|(
name|timer
operator|.
name|nanosPerOperation
argument_list|(
name|mb
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|bandwidth
argument_list|(
name|timer
argument_list|,
name|filesize
argument_list|)
expr_stmt|;
name|logFSState
argument_list|()
expr_stmt|;
name|FileStatus
name|destFileStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|hugefileRenamed
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|filesize
argument_list|,
name|destFileStatus
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
comment|// rename back
name|ContractTestUtils
operator|.
name|NanoTimer
name|timer2
init|=
operator|new
name|ContractTestUtils
operator|.
name|NanoTimer
argument_list|()
decl_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|hugefileRenamed
argument_list|,
name|hugefile
argument_list|)
expr_stmt|;
name|timer2
operator|.
name|end
argument_list|(
literal|"Renaming back"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Time per MB to rename = {} nS"
argument_list|,
name|toHuman
argument_list|(
name|timer2
operator|.
name|nanosPerOperation
argument_list|(
name|mb
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|bandwidth
argument_list|(
name|timer2
argument_list|,
name|filesize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test_999_DeleteHugeFiles ()
specifier|public
name|void
name|test_999_DeleteHugeFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|deleteHugeFile
argument_list|()
expr_stmt|;
name|ContractTestUtils
operator|.
name|NanoTimer
name|timer2
init|=
operator|new
name|ContractTestUtils
operator|.
name|NanoTimer
argument_list|()
decl_stmt|;
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|hugefileRenamed
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|timer2
operator|.
name|end
argument_list|(
literal|"time to delete %s"
argument_list|,
name|hugefileRenamed
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|rm
argument_list|(
name|fs
argument_list|,
name|getTestPath
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteHugeFile ()
specifier|protected
name|void
name|deleteHugeFile
parameter_list|()
throws|throws
name|IOException
block|{
name|describe
argument_list|(
literal|"Deleting %s"
argument_list|,
name|hugefile
argument_list|)
expr_stmt|;
name|NanoTimer
name|timer
init|=
operator|new
name|NanoTimer
argument_list|()
decl_stmt|;
name|getFileSystem
argument_list|()
operator|.
name|delete
argument_list|(
name|hugefile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|timer
operator|.
name|end
argument_list|(
literal|"time to delete %s"
argument_list|,
name|hugefile
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

