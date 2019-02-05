begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|DeleteObjectsRequest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|MultiObjectDeleteException
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
name|java
operator|.
name|io
operator|.
name|EOFException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|S3ATestUtils
operator|.
name|getLandsatCSVPath
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
name|test
operator|.
name|LambdaTestUtils
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Test S3A Failure translation, including a functional test  * generating errors during stream IO.  */
end_comment

begin_class
DECL|class|ITestS3AFailureHandling
specifier|public
class|class
name|ITestS3AFailureHandling
extends|extends
name|AbstractS3ATestBase
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
name|ITestS3AFailureHandling
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|setBoolean
argument_list|(
name|Constants
operator|.
name|ENABLE_MULTI_DELETE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Test
DECL|method|testReadFileChanged ()
specifier|public
name|void
name|testReadFileChanged
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"overwrite a file with a shorter one during a read, seek"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|fullLength
init|=
literal|8192
decl_stmt|;
specifier|final
name|byte
index|[]
name|fullDataset
init|=
name|dataset
argument_list|(
name|fullLength
argument_list|,
literal|'a'
argument_list|,
literal|32
argument_list|)
decl_stmt|;
specifier|final
name|int
name|shortLen
init|=
literal|4096
decl_stmt|;
specifier|final
name|byte
index|[]
name|shortDataset
init|=
name|dataset
argument_list|(
name|shortLen
argument_list|,
literal|'A'
argument_list|,
literal|32
argument_list|)
decl_stmt|;
specifier|final
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|testpath
init|=
name|path
argument_list|(
literal|"readFileToChange.txt"
argument_list|)
decl_stmt|;
comment|// initial write
name|writeDataset
argument_list|(
name|fs
argument_list|,
name|testpath
argument_list|,
name|fullDataset
argument_list|,
name|fullDataset
operator|.
name|length
argument_list|,
literal|1024
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
init|(
name|FSDataInputStream
name|instream
init|=
name|fs
operator|.
name|open
argument_list|(
name|testpath
argument_list|)
init|)
block|{
name|instream
operator|.
name|seek
argument_list|(
name|fullLength
operator|-
literal|16
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no data to read"
argument_list|,
name|instream
operator|.
name|read
argument_list|()
operator|>=
literal|0
argument_list|)
expr_stmt|;
comment|// overwrite
name|writeDataset
argument_list|(
name|fs
argument_list|,
name|testpath
argument_list|,
name|shortDataset
argument_list|,
name|shortDataset
operator|.
name|length
argument_list|,
literal|1024
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// here the file length is less. Probe the file to see if this is true,
comment|// with a spin and wait
name|eventually
argument_list|(
literal|30
operator|*
literal|1000
argument_list|,
literal|1000
argument_list|,
parameter_list|()
lambda|->
block|{
name|assertEquals
argument_list|(
name|shortLen
argument_list|,
name|fs
operator|.
name|getFileStatus
argument_list|(
name|testpath
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
comment|// here length is shorter. Assuming it has propagated to all replicas,
comment|// the position of the input stream is now beyond the EOF.
comment|// An attempt to seek backwards to a position greater than the
comment|// short length will raise an exception from AWS S3, which must be
comment|// translated into an EOF
name|instream
operator|.
name|seek
argument_list|(
name|shortLen
operator|+
literal|1024
argument_list|)
expr_stmt|;
name|int
name|c
init|=
name|instream
operator|.
name|read
argument_list|()
decl_stmt|;
name|assertIsEOF
argument_list|(
literal|"read()"
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|256
index|]
decl_stmt|;
name|assertIsEOF
argument_list|(
literal|"read(buffer)"
argument_list|,
name|instream
operator|.
name|read
argument_list|(
name|buf
argument_list|)
argument_list|)
expr_stmt|;
name|assertIsEOF
argument_list|(
literal|"read(offset)"
argument_list|,
name|instream
operator|.
name|read
argument_list|(
name|instream
operator|.
name|getPos
argument_list|()
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
comment|// now do a block read fully, again, backwards from the current pos
name|intercept
argument_list|(
name|EOFException
operator|.
name|class
argument_list|,
literal|""
argument_list|,
literal|"readfully"
argument_list|,
parameter_list|()
lambda|->
name|instream
operator|.
name|readFully
argument_list|(
name|shortLen
operator|+
literal|512
argument_list|,
name|buf
argument_list|)
argument_list|)
expr_stmt|;
name|assertIsEOF
argument_list|(
literal|"read(offset)"
argument_list|,
name|instream
operator|.
name|read
argument_list|(
name|shortLen
operator|+
literal|510
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
comment|// seek somewhere useful
name|instream
operator|.
name|seek
argument_list|(
name|shortLen
operator|-
literal|256
argument_list|)
expr_stmt|;
comment|// delete the file. Reads must fail
name|fs
operator|.
name|delete
argument_list|(
name|testpath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|intercept
argument_list|(
name|FileNotFoundException
operator|.
name|class
argument_list|,
literal|""
argument_list|,
literal|"read()"
argument_list|,
parameter_list|()
lambda|->
name|instream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|intercept
argument_list|(
name|FileNotFoundException
operator|.
name|class
argument_list|,
literal|""
argument_list|,
literal|"readfully"
argument_list|,
parameter_list|()
lambda|->
name|instream
operator|.
name|readFully
argument_list|(
literal|2048
argument_list|,
name|buf
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Assert that a read operation returned an EOF value.    * @param operation specific operation    * @param readResult result    */
DECL|method|assertIsEOF (String operation, int readResult)
specifier|private
name|void
name|assertIsEOF
parameter_list|(
name|String
name|operation
parameter_list|,
name|int
name|readResult
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Expected EOF from "
operator|+
name|operation
operator|+
literal|"; got char "
operator|+
operator|(
name|char
operator|)
name|readResult
argument_list|,
operator|-
literal|1
argument_list|,
name|readResult
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiObjectDeleteNoFile ()
specifier|public
name|void
name|testMultiObjectDeleteNoFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Deleting a missing object"
argument_list|)
expr_stmt|;
name|removeKeys
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
literal|"ITestS3AFailureHandling/missingFile"
argument_list|)
expr_stmt|;
block|}
DECL|method|removeKeys (S3AFileSystem fileSystem, String... keys)
specifier|private
name|void
name|removeKeys
parameter_list|(
name|S3AFileSystem
name|fileSystem
parameter_list|,
name|String
modifier|...
name|keys
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|DeleteObjectsRequest
operator|.
name|KeyVersion
argument_list|>
name|request
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|keys
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|keys
control|)
block|{
name|request
operator|.
name|add
argument_list|(
operator|new
name|DeleteObjectsRequest
operator|.
name|KeyVersion
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|fileSystem
operator|.
name|removeKeys
argument_list|(
name|request
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiObjectDeleteSomeFiles ()
specifier|public
name|void
name|testMultiObjectDeleteSomeFiles
parameter_list|()
throws|throws
name|Throwable
block|{
name|Path
name|valid
init|=
name|path
argument_list|(
literal|"ITestS3AFailureHandling/validFile"
argument_list|)
decl_stmt|;
name|touch
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|valid
argument_list|)
expr_stmt|;
name|NanoTimer
name|timer
init|=
operator|new
name|NanoTimer
argument_list|()
decl_stmt|;
name|removeKeys
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|getFileSystem
argument_list|()
operator|.
name|pathToKey
argument_list|(
name|valid
argument_list|)
argument_list|,
literal|"ITestS3AFailureHandling/missingFile"
argument_list|)
expr_stmt|;
name|timer
operator|.
name|end
argument_list|(
literal|"removeKeys"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiObjectDeleteNoPermissions ()
specifier|public
name|void
name|testMultiObjectDeleteNoPermissions
parameter_list|()
throws|throws
name|Throwable
block|{
name|Path
name|testFile
init|=
name|getLandsatCSVPath
argument_list|(
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|S3AFileSystem
name|fs
init|=
operator|(
name|S3AFileSystem
operator|)
name|testFile
operator|.
name|getFileSystem
argument_list|(
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|intercept
argument_list|(
name|MultiObjectDeleteException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|removeKeys
argument_list|(
name|fs
argument_list|,
name|fs
operator|.
name|pathToKey
argument_list|(
name|testFile
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

