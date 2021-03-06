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
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
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
name|FileUtil
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
name|assertIsFile
import|;
end_import

begin_comment
comment|/**  * Test copy operation.  */
end_comment

begin_class
DECL|class|ITestAzureBlobFileSystemCopy
specifier|public
class|class
name|ITestAzureBlobFileSystemCopy
extends|extends
name|AbstractAbfsIntegrationTest
block|{
DECL|method|ITestAzureBlobFileSystemCopy ()
specifier|public
name|ITestAzureBlobFileSystemCopy
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
DECL|method|testCopyFromLocalFileSystem ()
specifier|public
name|void
name|testCopyFromLocalFileSystem
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
name|Path
name|localFilePath
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"azure_test"
argument_list|)
argument_list|)
decl_stmt|;
name|FileSystem
name|localFs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|localFs
operator|.
name|delete
argument_list|(
name|localFilePath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|writeString
argument_list|(
name|localFs
argument_list|,
name|localFilePath
argument_list|,
literal|"Testing"
argument_list|)
expr_stmt|;
name|Path
name|dstPath
init|=
operator|new
name|Path
argument_list|(
literal|"copiedFromLocal"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|FileUtil
operator|.
name|copy
argument_list|(
name|localFs
argument_list|,
name|localFilePath
argument_list|,
name|fs
argument_list|,
name|dstPath
argument_list|,
literal|false
argument_list|,
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertIsFile
argument_list|(
name|fs
argument_list|,
name|dstPath
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Testing"
argument_list|,
name|readString
argument_list|(
name|fs
argument_list|,
name|dstPath
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|dstPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|localFs
operator|.
name|delete
argument_list|(
name|localFilePath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readString (FileSystem fs, Path testFile)
specifier|private
name|String
name|readString
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|testFile
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readString
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|testFile
argument_list|)
argument_list|)
return|;
block|}
DECL|method|readString (FSDataInputStream inputStream)
specifier|private
name|String
name|readString
parameter_list|(
name|FSDataInputStream
name|inputStream
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|inputStream
argument_list|)
argument_list|)
init|)
block|{
specifier|final
name|int
name|bufferSize
init|=
literal|1024
decl_stmt|;
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|bufferSize
index|]
decl_stmt|;
name|int
name|count
init|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|bufferSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|>
name|bufferSize
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Exceeded buffer size"
argument_list|)
throw|;
block|}
return|return
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
return|;
block|}
block|}
DECL|method|writeString (FileSystem fs, Path path, String value)
specifier|private
name|void
name|writeString
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|writeString
argument_list|(
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|writeString (FSDataOutputStream outputStream, String value)
specifier|private
name|void
name|writeString
parameter_list|(
name|FSDataOutputStream
name|outputStream
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|BufferedWriter
name|writer
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|outputStream
argument_list|)
argument_list|)
init|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

