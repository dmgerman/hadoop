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
name|IOException
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
name|java
operator|.
name|util
operator|.
name|Random
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
name|AbfsServiceProviderImpl
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
name|azurebfs
operator|.
name|contracts
operator|.
name|services
operator|.
name|ConfigurationService
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
name|azurebfs
operator|.
name|constants
operator|.
name|ConfigurationKeys
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
import|;
end_import

begin_comment
comment|/**  * Test end to end between ABFS client and ABFS server.  */
end_comment

begin_class
DECL|class|ITestAzureBlobFileSystemE2E
specifier|public
class|class
name|ITestAzureBlobFileSystemE2E
extends|extends
name|DependencyInjectedTest
block|{
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
literal|"testfile"
argument_list|)
decl_stmt|;
DECL|field|TEST_BYTE
specifier|private
specifier|static
specifier|final
name|int
name|TEST_BYTE
init|=
literal|100
decl_stmt|;
DECL|field|TEST_OFFSET
specifier|private
specifier|static
specifier|final
name|int
name|TEST_OFFSET
init|=
literal|100
decl_stmt|;
DECL|field|TEST_DEFAULT_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|TEST_DEFAULT_BUFFER_SIZE
init|=
literal|4
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|TEST_DEFAULT_READ_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|TEST_DEFAULT_READ_BUFFER_SIZE
init|=
literal|1023900
decl_stmt|;
DECL|method|ITestAzureBlobFileSystemE2E ()
specifier|public
name|ITestAzureBlobFileSystemE2E
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|Configuration
name|configuration
init|=
name|this
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|FS_AZURE_READ_AHEAD_QUEUE_DEPTH
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|this
operator|.
name|getMockServiceInjector
argument_list|()
operator|.
name|replaceInstance
argument_list|(
name|Configuration
operator|.
name|class
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWriteOneByteToFile ()
specifier|public
name|void
name|testWriteOneByteToFile
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
name|stream
operator|.
name|write
argument_list|(
name|TEST_BYTE
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
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
literal|1
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
DECL|method|testReadWriteBytesToFile ()
specifier|public
name|void
name|testReadWriteBytesToFile
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
name|testWriteOneByteToFile
argument_list|()
expr_stmt|;
name|FSDataInputStream
name|inputStream
init|=
name|fs
operator|.
name|open
argument_list|(
name|TEST_FILE
argument_list|,
name|TEST_DEFAULT_BUFFER_SIZE
argument_list|)
decl_stmt|;
name|int
name|i
init|=
name|inputStream
operator|.
name|read
argument_list|()
decl_stmt|;
name|inputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|TEST_BYTE
argument_list|,
name|i
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
DECL|method|testOOBWrites ()
specifier|public
name|void
name|testOOBWrites
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
name|int
name|readBufferSize
init|=
name|AbfsServiceProviderImpl
operator|.
name|instance
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationService
operator|.
name|class
argument_list|)
operator|.
name|getReadBufferSize
argument_list|()
decl_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_FILE
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|writeStream
init|=
name|fs
operator|.
name|create
argument_list|(
name|TEST_FILE
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytesToRead
init|=
operator|new
name|byte
index|[
name|readBufferSize
index|]
decl_stmt|;
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|2
operator|*
name|readBufferSize
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
name|writeStream
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|writeStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writeStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|FSDataInputStream
name|readStream
init|=
name|fs
operator|.
name|open
argument_list|(
name|TEST_FILE
argument_list|)
decl_stmt|;
name|readStream
operator|.
name|read
argument_list|(
name|bytesToRead
argument_list|,
literal|0
argument_list|,
name|readBufferSize
argument_list|)
expr_stmt|;
name|writeStream
operator|=
name|fs
operator|.
name|create
argument_list|(
name|TEST_FILE
argument_list|)
expr_stmt|;
name|writeStream
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|writeStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writeStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|readStream
operator|.
name|read
argument_list|(
name|bytesToRead
argument_list|,
literal|0
argument_list|,
name|readBufferSize
argument_list|)
expr_stmt|;
name|readStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWriteWithBufferOffset ()
specifier|public
name|void
name|testWriteWithBufferOffset
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
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|1024
operator|*
literal|1000
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
name|stream
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|TEST_OFFSET
argument_list|,
name|b
operator|.
name|length
operator|-
name|TEST_OFFSET
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|byte
index|[]
name|r
init|=
operator|new
name|byte
index|[
name|TEST_DEFAULT_READ_BUFFER_SIZE
index|]
decl_stmt|;
name|FSDataInputStream
name|inputStream
init|=
name|fs
operator|.
name|open
argument_list|(
name|TEST_FILE
argument_list|,
name|TEST_DEFAULT_BUFFER_SIZE
argument_list|)
decl_stmt|;
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
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|b
argument_list|,
name|TEST_OFFSET
argument_list|,
name|b
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|inputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadWriteHeavyBytesToFileWithSmallerChunks ()
specifier|public
name|void
name|testReadWriteHeavyBytesToFileWithSmallerChunks
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
specifier|final
name|byte
index|[]
name|writeBuffer
init|=
operator|new
name|byte
index|[
literal|5
operator|*
literal|1000
operator|*
literal|1024
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|writeBuffer
argument_list|)
expr_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|writeBuffer
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|byte
index|[]
name|readBuffer
init|=
operator|new
name|byte
index|[
literal|5
operator|*
literal|1000
operator|*
literal|1024
index|]
decl_stmt|;
name|FSDataInputStream
name|inputStream
init|=
name|fs
operator|.
name|open
argument_list|(
name|TEST_FILE
argument_list|,
name|TEST_DEFAULT_BUFFER_SIZE
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|inputStream
operator|.
name|read
argument_list|(
name|readBuffer
argument_list|,
name|offset
argument_list|,
name|TEST_OFFSET
argument_list|)
operator|>
literal|0
condition|)
block|{
name|offset
operator|+=
name|TEST_OFFSET
expr_stmt|;
block|}
name|assertArrayEquals
argument_list|(
name|readBuffer
argument_list|,
name|writeBuffer
argument_list|)
expr_stmt|;
name|inputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

