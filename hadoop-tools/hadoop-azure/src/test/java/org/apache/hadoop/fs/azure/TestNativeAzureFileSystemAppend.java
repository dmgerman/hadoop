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
name|Arrays
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
name|lang
operator|.
name|RandomStringUtils
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
name|Before
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

begin_class
DECL|class|TestNativeAzureFileSystemAppend
specifier|public
class|class
name|TestNativeAzureFileSystemAppend
extends|extends
name|NativeAzureFileSystemBaseTest
block|{
DECL|field|TEST_FILE
specifier|private
specifier|static
specifier|final
name|String
name|TEST_FILE
init|=
literal|"test.dat"
decl_stmt|;
DECL|field|TEST_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_PATH
init|=
operator|new
name|Path
argument_list|(
name|TEST_FILE
argument_list|)
decl_stmt|;
DECL|field|testAccount
specifier|private
name|AzureBlobStorageTestAccount
name|testAccount
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|testAccount
operator|=
name|createTestAccount
argument_list|()
expr_stmt|;
name|fs
operator|=
name|testAccount
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
name|fs
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|NativeAzureFileSystem
operator|.
name|APPEND_SUPPORT_ENABLE_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
name|fs
operator|.
name|getUri
argument_list|()
decl_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/*    * Helper method that creates test data of size provided by the    * "size" parameter.    */
DECL|method|getTestData (int size)
specifier|private
specifier|static
name|byte
index|[]
name|getTestData
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|byte
index|[]
name|testData
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
name|size
argument_list|)
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|testData
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
return|return
name|testData
return|;
block|}
comment|// Helper method to create file and write fileSize bytes of data on it.
DECL|method|createBaseFileWithData (int fileSize, Path testPath)
specifier|private
name|byte
index|[]
name|createBaseFileWithData
parameter_list|(
name|int
name|fileSize
parameter_list|,
name|Path
name|testPath
parameter_list|)
throws|throws
name|Throwable
block|{
name|FSDataOutputStream
name|createStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|createStream
operator|=
name|fs
operator|.
name|create
argument_list|(
name|testPath
argument_list|)
expr_stmt|;
name|byte
index|[]
name|fileData
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|fileSize
operator|!=
literal|0
condition|)
block|{
name|fileData
operator|=
name|getTestData
argument_list|(
name|fileSize
argument_list|)
expr_stmt|;
name|createStream
operator|.
name|write
argument_list|(
name|fileData
argument_list|)
expr_stmt|;
block|}
return|return
name|fileData
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|createStream
operator|!=
literal|null
condition|)
block|{
name|createStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/*    * Helper method to verify a file data equal to "dataLength" parameter    */
DECL|method|verifyFileData (int dataLength, byte[] testData, int testDataIndex, FSDataInputStream srcStream)
specifier|private
name|boolean
name|verifyFileData
parameter_list|(
name|int
name|dataLength
parameter_list|,
name|byte
index|[]
name|testData
parameter_list|,
name|int
name|testDataIndex
parameter_list|,
name|FSDataInputStream
name|srcStream
parameter_list|)
block|{
try|try
block|{
name|byte
index|[]
name|fileBuffer
init|=
operator|new
name|byte
index|[
name|dataLength
index|]
decl_stmt|;
name|byte
index|[]
name|testDataBuffer
init|=
operator|new
name|byte
index|[
name|dataLength
index|]
decl_stmt|;
name|int
name|fileBytesRead
init|=
name|srcStream
operator|.
name|read
argument_list|(
name|fileBuffer
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileBytesRead
operator|<
name|dataLength
condition|)
block|{
return|return
literal|false
return|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|testData
argument_list|,
name|testDataIndex
argument_list|,
name|testDataBuffer
argument_list|,
literal|0
argument_list|,
name|dataLength
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|fileBuffer
argument_list|,
name|testDataBuffer
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/*    * Helper method to verify Append on a testFile.    */
DECL|method|verifyAppend (byte[] testData, Path testFile)
specifier|private
name|boolean
name|verifyAppend
parameter_list|(
name|byte
index|[]
name|testData
parameter_list|,
name|Path
name|testFile
parameter_list|)
block|{
name|FSDataInputStream
name|srcStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|srcStream
operator|=
name|fs
operator|.
name|open
argument_list|(
name|testFile
argument_list|)
expr_stmt|;
name|int
name|baseBufferSize
init|=
literal|2048
decl_stmt|;
name|int
name|testDataSize
init|=
name|testData
operator|.
name|length
decl_stmt|;
name|int
name|testDataIndex
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|testDataSize
operator|>
name|baseBufferSize
condition|)
block|{
if|if
condition|(
operator|!
name|verifyFileData
argument_list|(
name|baseBufferSize
argument_list|,
name|testData
argument_list|,
name|testDataIndex
argument_list|,
name|srcStream
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|testDataIndex
operator|+=
name|baseBufferSize
expr_stmt|;
name|testDataSize
operator|-=
name|baseBufferSize
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|verifyFileData
argument_list|(
name|testDataSize
argument_list|,
name|testData
argument_list|,
name|testDataIndex
argument_list|,
name|srcStream
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|srcStream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|srcStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// Swallowing
block|}
block|}
block|}
block|}
comment|/*    * Test case to verify if an append on small size data works. This tests    * append E2E    */
annotation|@
name|Test
DECL|method|testSingleAppend ()
specifier|public
name|void
name|testSingleAppend
parameter_list|()
throws|throws
name|Throwable
block|{
name|FSDataOutputStream
name|appendStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|int
name|baseDataSize
init|=
literal|50
decl_stmt|;
name|byte
index|[]
name|baseDataBuffer
init|=
name|createBaseFileWithData
argument_list|(
name|baseDataSize
argument_list|,
name|TEST_PATH
argument_list|)
decl_stmt|;
name|int
name|appendDataSize
init|=
literal|20
decl_stmt|;
name|byte
index|[]
name|appendDataBuffer
init|=
name|getTestData
argument_list|(
name|appendDataSize
argument_list|)
decl_stmt|;
name|appendStream
operator|=
name|fs
operator|.
name|append
argument_list|(
name|TEST_PATH
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|appendStream
operator|.
name|write
argument_list|(
name|appendDataBuffer
argument_list|)
expr_stmt|;
name|appendStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|byte
index|[]
name|testData
init|=
operator|new
name|byte
index|[
name|baseDataSize
operator|+
name|appendDataSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|baseDataBuffer
argument_list|,
literal|0
argument_list|,
name|testData
argument_list|,
literal|0
argument_list|,
name|baseDataSize
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|appendDataBuffer
argument_list|,
literal|0
argument_list|,
name|testData
argument_list|,
name|baseDataSize
argument_list|,
name|appendDataSize
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|verifyAppend
argument_list|(
name|testData
argument_list|,
name|TEST_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|appendStream
operator|!=
literal|null
condition|)
block|{
name|appendStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/*    * Test case to verify append to an empty file.    */
annotation|@
name|Test
DECL|method|testSingleAppendOnEmptyFile ()
specifier|public
name|void
name|testSingleAppendOnEmptyFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|FSDataOutputStream
name|appendStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|createBaseFileWithData
argument_list|(
literal|0
argument_list|,
name|TEST_PATH
argument_list|)
expr_stmt|;
name|int
name|appendDataSize
init|=
literal|20
decl_stmt|;
name|byte
index|[]
name|appendDataBuffer
init|=
name|getTestData
argument_list|(
name|appendDataSize
argument_list|)
decl_stmt|;
name|appendStream
operator|=
name|fs
operator|.
name|append
argument_list|(
name|TEST_PATH
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|appendStream
operator|.
name|write
argument_list|(
name|appendDataBuffer
argument_list|)
expr_stmt|;
name|appendStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|verifyAppend
argument_list|(
name|appendDataBuffer
argument_list|,
name|TEST_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|appendStream
operator|!=
literal|null
condition|)
block|{
name|appendStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/*    * Test to verify that we can open only one Append stream on a File.    */
annotation|@
name|Test
DECL|method|testSingleAppenderScenario ()
specifier|public
name|void
name|testSingleAppenderScenario
parameter_list|()
throws|throws
name|Throwable
block|{
name|FSDataOutputStream
name|appendStream1
init|=
literal|null
decl_stmt|;
name|FSDataOutputStream
name|appendStream2
init|=
literal|null
decl_stmt|;
name|IOException
name|ioe
init|=
literal|null
decl_stmt|;
try|try
block|{
name|createBaseFileWithData
argument_list|(
literal|0
argument_list|,
name|TEST_PATH
argument_list|)
expr_stmt|;
name|appendStream1
operator|=
name|fs
operator|.
name|append
argument_list|(
name|TEST_PATH
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|boolean
name|encounteredException
init|=
literal|false
decl_stmt|;
try|try
block|{
name|appendStream2
operator|=
name|fs
operator|.
name|append
argument_list|(
name|TEST_PATH
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|encounteredException
operator|=
literal|true
expr_stmt|;
name|ioe
operator|=
name|ex
expr_stmt|;
block|}
name|appendStream1
operator|.
name|close
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|encounteredException
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Unable to set Append lease on the Blob"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|appendStream1
operator|!=
literal|null
condition|)
block|{
name|appendStream1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|appendStream2
operator|!=
literal|null
condition|)
block|{
name|appendStream2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/*    * Tests to verify multiple appends on a Blob.    */
annotation|@
name|Test
DECL|method|testMultipleAppends ()
specifier|public
name|void
name|testMultipleAppends
parameter_list|()
throws|throws
name|Throwable
block|{
name|int
name|baseDataSize
init|=
literal|50
decl_stmt|;
name|byte
index|[]
name|baseDataBuffer
init|=
name|createBaseFileWithData
argument_list|(
name|baseDataSize
argument_list|,
name|TEST_PATH
argument_list|)
decl_stmt|;
name|int
name|appendDataSize
init|=
literal|100
decl_stmt|;
name|int
name|targetAppendCount
init|=
literal|50
decl_stmt|;
name|byte
index|[]
name|testData
init|=
operator|new
name|byte
index|[
name|baseDataSize
operator|+
operator|(
name|appendDataSize
operator|*
name|targetAppendCount
operator|)
index|]
decl_stmt|;
name|int
name|testDataIndex
init|=
literal|0
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|baseDataBuffer
argument_list|,
literal|0
argument_list|,
name|testData
argument_list|,
name|testDataIndex
argument_list|,
name|baseDataSize
argument_list|)
expr_stmt|;
name|testDataIndex
operator|+=
name|baseDataSize
expr_stmt|;
name|int
name|appendCount
init|=
literal|0
decl_stmt|;
name|FSDataOutputStream
name|appendStream
init|=
literal|null
decl_stmt|;
try|try
block|{
while|while
condition|(
name|appendCount
operator|<
name|targetAppendCount
condition|)
block|{
name|byte
index|[]
name|appendDataBuffer
init|=
name|getTestData
argument_list|(
name|appendDataSize
argument_list|)
decl_stmt|;
name|appendStream
operator|=
name|fs
operator|.
name|append
argument_list|(
name|TEST_PATH
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|appendStream
operator|.
name|write
argument_list|(
name|appendDataBuffer
argument_list|)
expr_stmt|;
name|appendStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|appendDataBuffer
argument_list|,
literal|0
argument_list|,
name|testData
argument_list|,
name|testDataIndex
argument_list|,
name|appendDataSize
argument_list|)
expr_stmt|;
name|testDataIndex
operator|+=
name|appendDataSize
expr_stmt|;
name|appendCount
operator|++
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|verifyAppend
argument_list|(
name|testData
argument_list|,
name|TEST_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|appendStream
operator|!=
literal|null
condition|)
block|{
name|appendStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/*    * Test to verify we multiple appends on the same stream.    */
annotation|@
name|Test
DECL|method|testMultipleAppendsOnSameStream ()
specifier|public
name|void
name|testMultipleAppendsOnSameStream
parameter_list|()
throws|throws
name|Throwable
block|{
name|int
name|baseDataSize
init|=
literal|50
decl_stmt|;
name|byte
index|[]
name|baseDataBuffer
init|=
name|createBaseFileWithData
argument_list|(
name|baseDataSize
argument_list|,
name|TEST_PATH
argument_list|)
decl_stmt|;
name|int
name|appendDataSize
init|=
literal|100
decl_stmt|;
name|int
name|targetAppendCount
init|=
literal|50
decl_stmt|;
name|byte
index|[]
name|testData
init|=
operator|new
name|byte
index|[
name|baseDataSize
operator|+
operator|(
name|appendDataSize
operator|*
name|targetAppendCount
operator|)
index|]
decl_stmt|;
name|int
name|testDataIndex
init|=
literal|0
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|baseDataBuffer
argument_list|,
literal|0
argument_list|,
name|testData
argument_list|,
name|testDataIndex
argument_list|,
name|baseDataSize
argument_list|)
expr_stmt|;
name|testDataIndex
operator|+=
name|baseDataSize
expr_stmt|;
name|int
name|appendCount
init|=
literal|0
decl_stmt|;
name|FSDataOutputStream
name|appendStream
init|=
literal|null
decl_stmt|;
try|try
block|{
while|while
condition|(
name|appendCount
operator|<
name|targetAppendCount
condition|)
block|{
name|appendStream
operator|=
name|fs
operator|.
name|append
argument_list|(
name|TEST_PATH
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|int
name|singleAppendChunkSize
init|=
literal|20
decl_stmt|;
name|int
name|appendRunSize
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|appendRunSize
operator|<
name|appendDataSize
condition|)
block|{
name|byte
index|[]
name|appendDataBuffer
init|=
name|getTestData
argument_list|(
name|singleAppendChunkSize
argument_list|)
decl_stmt|;
name|appendStream
operator|.
name|write
argument_list|(
name|appendDataBuffer
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|appendDataBuffer
argument_list|,
literal|0
argument_list|,
name|testData
argument_list|,
name|testDataIndex
operator|+
name|appendRunSize
argument_list|,
name|singleAppendChunkSize
argument_list|)
expr_stmt|;
name|appendRunSize
operator|+=
name|singleAppendChunkSize
expr_stmt|;
block|}
name|appendStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|testDataIndex
operator|+=
name|appendDataSize
expr_stmt|;
name|appendCount
operator|++
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|verifyAppend
argument_list|(
name|testData
argument_list|,
name|TEST_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|appendStream
operator|!=
literal|null
condition|)
block|{
name|appendStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|UnsupportedOperationException
operator|.
name|class
argument_list|)
comment|/*    * Test to verify the behavior when Append Support configuration flag is set to false    */
DECL|method|testFalseConfigurationFlagBehavior ()
specifier|public
name|void
name|testFalseConfigurationFlagBehavior
parameter_list|()
throws|throws
name|Throwable
block|{
name|fs
operator|=
name|testAccount
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
name|fs
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|NativeAzureFileSystem
operator|.
name|APPEND_SUPPORT_ENABLE_PROPERTY_NAME
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
name|fs
operator|.
name|getUri
argument_list|()
decl_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|appendStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|createBaseFileWithData
argument_list|(
literal|0
argument_list|,
name|TEST_PATH
argument_list|)
expr_stmt|;
name|appendStream
operator|=
name|fs
operator|.
name|append
argument_list|(
name|TEST_PATH
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|appendStream
operator|!=
literal|null
condition|)
block|{
name|appendStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
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
name|create
argument_list|()
return|;
block|}
block|}
end_class

end_unit

