begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
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
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|net
operator|.
name|URI
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
comment|/**  * Tests small file upload functionality for  * {@link S3ABlockOutputStream} with the blocks buffered in byte arrays.  *  * File sizes are kept small to reduce test duration on slow connections;  * multipart tests are kept in scale tests.  */
end_comment

begin_class
DECL|class|ITestS3ABlockOutputArray
specifier|public
class|class
name|ITestS3ABlockOutputArray
extends|extends
name|AbstractS3ATestBase
block|{
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|256
operator|*
literal|1024
decl_stmt|;
DECL|field|dataset
specifier|private
specifier|static
name|byte
index|[]
name|dataset
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupDataset ()
specifier|public
specifier|static
name|void
name|setupDataset
parameter_list|()
block|{
name|dataset
operator|=
name|ContractTestUtils
operator|.
name|dataset
argument_list|(
name|BLOCK_SIZE
argument_list|,
literal|0
argument_list|,
literal|256
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createConfiguration ()
specifier|protected
name|Configuration
name|createConfiguration
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|super
operator|.
name|createConfiguration
argument_list|()
decl_stmt|;
name|S3ATestUtils
operator|.
name|disableFilesystemCaching
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|MIN_MULTIPART_THRESHOLD
argument_list|,
name|MULTIPART_MIN_SIZE
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MULTIPART_SIZE
argument_list|,
name|MULTIPART_MIN_SIZE
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
DECL|method|getBlockOutputBufferName ()
specifier|protected
name|String
name|getBlockOutputBufferName
parameter_list|()
block|{
return|return
name|FAST_UPLOAD_BUFFER_ARRAY
return|;
block|}
annotation|@
name|Test
DECL|method|testZeroByteUpload ()
specifier|public
name|void
name|testZeroByteUpload
parameter_list|()
throws|throws
name|IOException
block|{
name|verifyUpload
argument_list|(
literal|"0"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRegularUpload ()
specifier|public
name|void
name|testRegularUpload
parameter_list|()
throws|throws
name|IOException
block|{
name|verifyUpload
argument_list|(
literal|"regular"
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|testWriteAfterStreamClose ()
specifier|public
name|void
name|testWriteAfterStreamClose
parameter_list|()
throws|throws
name|Throwable
block|{
name|Path
name|dest
init|=
name|path
argument_list|(
literal|"testWriteAfterStreamClose"
argument_list|)
decl_stmt|;
name|describe
argument_list|(
literal|" testWriteAfterStreamClose"
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|stream
init|=
name|getFileSystem
argument_list|()
operator|.
name|create
argument_list|(
name|dest
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|ContractTestUtils
operator|.
name|dataset
argument_list|(
literal|16
argument_list|,
literal|'a'
argument_list|,
literal|26
argument_list|)
decl_stmt|;
try|try
block|{
name|stream
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBlocksClosed ()
specifier|public
name|void
name|testBlocksClosed
parameter_list|()
throws|throws
name|Throwable
block|{
name|Path
name|dest
init|=
name|path
argument_list|(
literal|"testBlocksClosed"
argument_list|)
decl_stmt|;
name|describe
argument_list|(
literal|" testBlocksClosed"
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|stream
init|=
name|getFileSystem
argument_list|()
operator|.
name|create
argument_list|(
name|dest
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|S3AInstrumentation
operator|.
name|OutputStreamStatistics
name|statistics
init|=
name|S3ATestUtils
operator|.
name|getOutputStreamStatistics
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|ContractTestUtils
operator|.
name|dataset
argument_list|(
literal|16
argument_list|,
literal|'a'
argument_list|,
literal|26
argument_list|)
decl_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"closing output stream"
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"total allocated blocks in "
operator|+
name|statistics
argument_list|,
literal|1
argument_list|,
name|statistics
operator|.
name|blocksAllocated
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"actively allocated blocks in "
operator|+
name|statistics
argument_list|,
literal|0
argument_list|,
name|statistics
operator|.
name|blocksActivelyAllocated
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"end of test case"
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyUpload (String name, int fileSize)
specifier|private
name|void
name|verifyUpload
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|fileSize
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|dest
init|=
name|path
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|describe
argument_list|(
name|name
operator|+
literal|" upload to "
operator|+
name|dest
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|createAndVerifyFile
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|dest
argument_list|,
name|fileSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a factory for used in mark/reset tests.    * @param fileSystem source FS    * @return the factory    */
DECL|method|createFactory (S3AFileSystem fileSystem)
specifier|protected
name|S3ADataBlocks
operator|.
name|BlockFactory
name|createFactory
parameter_list|(
name|S3AFileSystem
name|fileSystem
parameter_list|)
block|{
return|return
operator|new
name|S3ADataBlocks
operator|.
name|ArrayBlockFactory
argument_list|(
name|fileSystem
argument_list|)
return|;
block|}
DECL|method|markAndResetDatablock (S3ADataBlocks.BlockFactory factory)
specifier|private
name|void
name|markAndResetDatablock
parameter_list|(
name|S3ADataBlocks
operator|.
name|BlockFactory
name|factory
parameter_list|)
throws|throws
name|Exception
block|{
name|S3AInstrumentation
name|instrumentation
init|=
operator|new
name|S3AInstrumentation
argument_list|(
operator|new
name|URI
argument_list|(
literal|"s3a://example"
argument_list|)
argument_list|)
decl_stmt|;
name|S3AInstrumentation
operator|.
name|OutputStreamStatistics
name|outstats
init|=
name|instrumentation
operator|.
name|newOutputStreamStatistics
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|S3ADataBlocks
operator|.
name|DataBlock
name|block
init|=
name|factory
operator|.
name|create
argument_list|(
literal|1
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|outstats
argument_list|)
decl_stmt|;
name|block
operator|.
name|write
argument_list|(
name|dataset
argument_list|,
literal|0
argument_list|,
name|dataset
operator|.
name|length
argument_list|)
expr_stmt|;
name|S3ADataBlocks
operator|.
name|BlockUploadData
name|uploadData
init|=
name|block
operator|.
name|startUpload
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|uploadData
operator|.
name|getUploadStream
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Mark not supported in "
operator|+
name|stream
argument_list|,
name|stream
operator|.
name|markSupported
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|stream
operator|.
name|mark
argument_list|(
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
comment|// read a lot
name|long
name|l
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|stream
operator|.
name|read
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// do nothing
name|l
operator|++
expr_stmt|;
block|}
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMarkReset ()
specifier|public
name|void
name|testMarkReset
parameter_list|()
throws|throws
name|Throwable
block|{
name|markAndResetDatablock
argument_list|(
name|createFactory
argument_list|(
name|getFileSystem
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

