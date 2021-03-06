begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
operator|.
name|integration
operator|.
name|AzureTestUtils
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
name|permission
operator|.
name|FsPermission
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
name|permission
operator|.
name|PermissionStatus
import|;
end_import

begin_comment
comment|/**  * Handle OOB IO into a shared container.  */
end_comment

begin_class
DECL|class|ITestAzureConcurrentOutOfBandIo
specifier|public
class|class
name|ITestAzureConcurrentOutOfBandIo
extends|extends
name|AbstractWasbTestBase
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
name|ITestAzureConcurrentOutOfBandIo
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Class constants.
DECL|field|DOWNLOAD_BLOCK_SIZE
specifier|static
specifier|final
name|int
name|DOWNLOAD_BLOCK_SIZE
init|=
literal|8
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|UPLOAD_BLOCK_SIZE
specifier|static
specifier|final
name|int
name|UPLOAD_BLOCK_SIZE
init|=
literal|4
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|BLOB_SIZE
specifier|static
specifier|final
name|int
name|BLOB_SIZE
init|=
literal|32
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|// Number of blocks to be written before flush.
DECL|field|NUMBER_OF_BLOCKS
specifier|static
specifier|final
name|int
name|NUMBER_OF_BLOCKS
init|=
literal|2
decl_stmt|;
annotation|@
name|Override
DECL|method|createTestAccount ()
specifier|protected
name|AzureBlobStorageTestAccount
name|createTestAccount
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|AzureBlobStorageTestAccount
operator|.
name|createOutOfBandStore
argument_list|(
name|UPLOAD_BLOCK_SIZE
argument_list|,
name|DOWNLOAD_BLOCK_SIZE
argument_list|)
return|;
block|}
DECL|class|DataBlockWriter
class|class
name|DataBlockWriter
implements|implements
name|Runnable
block|{
DECL|field|runner
name|Thread
name|runner
decl_stmt|;
DECL|field|writerStorageAccount
name|AzureBlobStorageTestAccount
name|writerStorageAccount
decl_stmt|;
DECL|field|key
name|String
name|key
decl_stmt|;
DECL|field|done
name|boolean
name|done
init|=
literal|false
decl_stmt|;
comment|/**      * Constructor captures the test account.      *       * @param testAccount      */
DECL|method|DataBlockWriter (AzureBlobStorageTestAccount testAccount, String key)
specifier|public
name|DataBlockWriter
parameter_list|(
name|AzureBlobStorageTestAccount
name|testAccount
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|writerStorageAccount
operator|=
name|testAccount
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
comment|/**      * Start writing blocks to Azure storage.      */
DECL|method|startWriting ()
specifier|public
name|void
name|startWriting
parameter_list|()
block|{
name|runner
operator|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// Create the block writer thread.
name|runner
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Start the block writer thread.
block|}
comment|/**      * Stop writing blocks to Azure storage.      */
DECL|method|stopWriting ()
specifier|public
name|void
name|stopWriting
parameter_list|()
block|{
name|done
operator|=
literal|true
expr_stmt|;
block|}
comment|/**      * Implementation of the runnable interface. The run method is a tight loop      * which repeatedly updates the blob with a 4 MB block.      */
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|byte
index|[]
name|dataBlockWrite
init|=
operator|new
name|byte
index|[
name|UPLOAD_BLOCK_SIZE
index|]
decl_stmt|;
name|OutputStream
name|outputStream
init|=
literal|null
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
operator|!
name|done
condition|;
name|i
operator|++
control|)
block|{
comment|// Write two 4 MB blocks to the blob.
comment|//
name|outputStream
operator|=
name|writerStorageAccount
operator|.
name|getStore
argument_list|()
operator|.
name|storefile
argument_list|(
name|key
argument_list|,
operator|new
name|PermissionStatus
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|dataBlockWrite
argument_list|,
call|(
name|byte
call|)
argument_list|(
name|i
operator|%
literal|256
argument_list|)
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
name|NUMBER_OF_BLOCKS
condition|;
name|j
operator|++
control|)
block|{
name|outputStream
operator|.
name|write
argument_list|(
name|dataBlockWrite
argument_list|)
expr_stmt|;
block|}
name|outputStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|outputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|AzureException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"DatablockWriter thread encountered a storage exception."
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
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
name|error
argument_list|(
literal|"DatablockWriter thread encountered an I/O exception."
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testReadOOBWrites ()
specifier|public
name|void
name|testReadOOBWrites
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|dataBlockWrite
init|=
operator|new
name|byte
index|[
name|UPLOAD_BLOCK_SIZE
index|]
decl_stmt|;
name|byte
index|[]
name|dataBlockRead
init|=
operator|new
name|byte
index|[
name|UPLOAD_BLOCK_SIZE
index|]
decl_stmt|;
comment|// Write to blob to make sure it exists.
comment|//
comment|// Write five 4 MB blocks to the blob. To ensure there is data in the blob before
comment|// reading.  This eliminates the race between the reader and writer threads.
name|String
name|key
init|=
literal|"WASB_String"
operator|+
name|AzureTestUtils
operator|.
name|getForkID
argument_list|()
operator|+
literal|".txt"
decl_stmt|;
name|OutputStream
name|outputStream
init|=
name|testAccount
operator|.
name|getStore
argument_list|()
operator|.
name|storefile
argument_list|(
name|key
argument_list|,
operator|new
name|PermissionStatus
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
argument_list|,
name|key
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|dataBlockWrite
argument_list|,
operator|(
name|byte
operator|)
literal|255
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
name|NUMBER_OF_BLOCKS
condition|;
name|i
operator|++
control|)
block|{
name|outputStream
operator|.
name|write
argument_list|(
name|dataBlockWrite
argument_list|)
expr_stmt|;
block|}
name|outputStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|outputStream
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Start writing blocks to Azure store using the DataBlockWriter thread.
name|DataBlockWriter
name|writeBlockTask
init|=
operator|new
name|DataBlockWriter
argument_list|(
name|testAccount
argument_list|,
name|key
argument_list|)
decl_stmt|;
name|writeBlockTask
operator|.
name|startWriting
argument_list|()
expr_stmt|;
name|int
name|count
init|=
literal|0
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
try|try
init|(
name|InputStream
name|inputStream
init|=
name|testAccount
operator|.
name|getStore
argument_list|()
operator|.
name|retrieve
argument_list|(
name|key
argument_list|)
init|)
block|{
name|count
operator|=
literal|0
expr_stmt|;
name|int
name|c
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|c
operator|>=
literal|0
condition|)
block|{
name|c
operator|=
name|inputStream
operator|.
name|read
argument_list|(
name|dataBlockRead
argument_list|,
literal|0
argument_list|,
name|UPLOAD_BLOCK_SIZE
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|<
literal|0
condition|)
block|{
break|break;
block|}
comment|// Counting the number of bytes.
name|count
operator|+=
name|c
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Stop writing blocks.
name|writeBlockTask
operator|.
name|stopWriting
argument_list|()
expr_stmt|;
comment|// Validate that a block was read.
name|assertEquals
argument_list|(
name|NUMBER_OF_BLOCKS
operator|*
name|UPLOAD_BLOCK_SIZE
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

