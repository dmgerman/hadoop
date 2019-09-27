begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.cosn
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|cosn
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
name|*
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|Random
import|;
end_import

begin_comment
comment|/**  * CosNInputStream Tester.  */
end_comment

begin_class
DECL|class|TestCosNInputStream
specifier|public
class|class
name|TestCosNInputStream
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
name|TestCosNInputStream
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|testRootDir
specifier|private
name|Path
name|testRootDir
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|this
operator|.
name|fs
operator|=
name|CosNTestUtils
operator|.
name|createTestFileSystem
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|this
operator|.
name|testRootDir
operator|=
name|CosNTestUtils
operator|.
name|createTestPath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"test root dir: "
operator|+
name|this
operator|.
name|testRootDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
literal|null
operator|!=
name|this
operator|.
name|fs
condition|)
block|{
name|this
operator|.
name|fs
operator|.
name|delete
argument_list|(
name|this
operator|.
name|testRootDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Method: seek(long pos).    */
annotation|@
name|Test
DECL|method|testSeek ()
specifier|public
name|void
name|testSeek
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|seekTestFilePath
init|=
operator|new
name|Path
argument_list|(
name|this
operator|.
name|testRootDir
operator|+
literal|"/"
operator|+
literal|"seekTestFile"
argument_list|)
decl_stmt|;
name|long
name|fileSize
init|=
literal|5
operator|*
name|Unit
operator|.
name|MB
decl_stmt|;
name|ContractTestUtils
operator|.
name|generateTestFile
argument_list|(
name|this
operator|.
name|fs
argument_list|,
name|seekTestFilePath
argument_list|,
name|fileSize
argument_list|,
literal|256
argument_list|,
literal|255
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"5MB file for seek test has created."
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|inputStream
init|=
name|this
operator|.
name|fs
operator|.
name|open
argument_list|(
name|seekTestFilePath
argument_list|)
decl_stmt|;
name|int
name|seekTimes
init|=
literal|5
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|!=
name|seekTimes
condition|;
name|i
operator|++
control|)
block|{
name|long
name|pos
init|=
name|fileSize
operator|/
operator|(
name|seekTimes
operator|-
name|i
operator|)
operator|-
literal|1
decl_stmt|;
name|inputStream
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"expected position at: "
operator|+
name|pos
operator|+
literal|", but got: "
operator|+
name|inputStream
operator|.
name|getPos
argument_list|()
argument_list|,
name|inputStream
operator|.
name|getPos
argument_list|()
operator|==
name|pos
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"completed seeking at pos: "
operator|+
name|inputStream
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"begin to random position seeking test..."
argument_list|)
expr_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
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
name|seekTimes
condition|;
name|i
operator|++
control|)
block|{
name|long
name|pos
init|=
name|Math
operator|.
name|abs
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
operator|%
name|fileSize
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"seeking for pos: "
operator|+
name|pos
argument_list|)
expr_stmt|;
name|inputStream
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"expected position at: "
operator|+
name|pos
operator|+
literal|", but got: "
operator|+
name|inputStream
operator|.
name|getPos
argument_list|()
argument_list|,
name|inputStream
operator|.
name|getPos
argument_list|()
operator|==
name|pos
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"completed seeking at pos: "
operator|+
name|inputStream
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Method: getPos().    */
annotation|@
name|Test
DECL|method|testGetPos ()
specifier|public
name|void
name|testGetPos
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|seekTestFilePath
init|=
operator|new
name|Path
argument_list|(
name|this
operator|.
name|testRootDir
operator|+
literal|"/"
operator|+
literal|"seekTestFile"
argument_list|)
decl_stmt|;
name|long
name|fileSize
init|=
literal|5
operator|*
name|Unit
operator|.
name|MB
decl_stmt|;
name|ContractTestUtils
operator|.
name|generateTestFile
argument_list|(
name|this
operator|.
name|fs
argument_list|,
name|seekTestFilePath
argument_list|,
name|fileSize
argument_list|,
literal|256
argument_list|,
literal|255
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"5MB file for getPos test has created."
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|inputStream
init|=
name|this
operator|.
name|fs
operator|.
name|open
argument_list|(
name|seekTestFilePath
argument_list|)
decl_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|long
name|pos
init|=
name|Math
operator|.
name|abs
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
operator|%
name|fileSize
decl_stmt|;
name|inputStream
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"expected position at: "
operator|+
name|pos
operator|+
literal|", but got: "
operator|+
name|inputStream
operator|.
name|getPos
argument_list|()
argument_list|,
name|inputStream
operator|.
name|getPos
argument_list|()
operator|==
name|pos
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"completed get pos tests."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Method: seekToNewSource(long targetPos).    */
annotation|@
name|Ignore
argument_list|(
literal|"Not ready yet"
argument_list|)
DECL|method|testSeekToNewSource ()
specifier|public
name|void
name|testSeekToNewSource
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Currently it is not supported to "
operator|+
literal|"seek the offset in a new source."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Method: read().    */
annotation|@
name|Test
DECL|method|testRead ()
specifier|public
name|void
name|testRead
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|bufLen
init|=
literal|256
decl_stmt|;
name|Path
name|readTestFilePath
init|=
operator|new
name|Path
argument_list|(
name|this
operator|.
name|testRootDir
operator|+
literal|"/"
operator|+
literal|"testReadSmallFile.txt"
argument_list|)
decl_stmt|;
name|long
name|fileSize
init|=
literal|5
operator|*
name|Unit
operator|.
name|MB
decl_stmt|;
name|ContractTestUtils
operator|.
name|generateTestFile
argument_list|(
name|this
operator|.
name|fs
argument_list|,
name|readTestFilePath
argument_list|,
name|fileSize
argument_list|,
literal|256
argument_list|,
literal|255
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"read test file: "
operator|+
name|readTestFilePath
operator|+
literal|" has created."
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|inputStream
init|=
name|this
operator|.
name|fs
operator|.
name|open
argument_list|(
name|readTestFilePath
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|bufLen
index|]
decl_stmt|;
name|long
name|bytesRead
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|bytesRead
operator|<
name|fileSize
condition|)
block|{
name|int
name|bytes
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|fileSize
operator|-
name|bytesRead
operator|<
name|bufLen
condition|)
block|{
name|int
name|remaining
init|=
call|(
name|int
call|)
argument_list|(
name|fileSize
operator|-
name|bytesRead
argument_list|)
decl_stmt|;
name|bytes
operator|=
name|inputStream
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|remaining
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bytes
operator|=
name|inputStream
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|bufLen
argument_list|)
expr_stmt|;
block|}
name|bytesRead
operator|+=
name|bytes
expr_stmt|;
if|if
condition|(
name|bytesRead
operator|%
operator|(
literal|1
operator|*
name|Unit
operator|.
name|MB
operator|)
operator|==
literal|0
condition|)
block|{
name|int
name|available
init|=
name|inputStream
operator|.
name|available
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"expected remaining: "
operator|+
operator|(
name|fileSize
operator|-
name|bytesRead
operator|)
operator|+
literal|" but got: "
operator|+
name|available
argument_list|,
operator|(
name|fileSize
operator|-
name|bytesRead
operator|)
operator|==
name|available
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Bytes read: "
operator|+
name|Math
operator|.
name|round
argument_list|(
operator|(
name|double
operator|)
name|bytesRead
operator|/
name|Unit
operator|.
name|MB
argument_list|)
operator|+
literal|"MB"
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|inputStream
operator|.
name|available
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|inputStream
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

