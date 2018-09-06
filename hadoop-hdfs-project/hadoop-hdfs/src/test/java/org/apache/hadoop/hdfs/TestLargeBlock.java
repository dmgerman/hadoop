begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

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
name|Arrays
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
name|CommonConfigurationKeys
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

begin_comment
comment|/**  * This class tests that blocks can be larger than 2GB  */
end_comment

begin_class
DECL|class|TestLargeBlock
specifier|public
class|class
name|TestLargeBlock
block|{
comment|/**   {     GenericTestUtils.setLogLevel(DataNode.LOG, Level.ALL);     GenericTestUtils.setLogLevel(LeaseManager.LOG, Level.ALL);     GenericTestUtils.setLogLevel(FSNamesystem.LOG, Level.ALL);     GenericTestUtils.setLogLevel(DFSClient.LOG, Level.ALL);     GenericTestUtils.setLogLevel(TestLargeBlock.LOG, Level.ALL);   }  */
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
name|TestLargeBlock
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// should we verify the data read back from the file? (slow)
DECL|field|verifyData
specifier|static
specifier|final
name|boolean
name|verifyData
init|=
literal|true
decl_stmt|;
DECL|field|pattern
specifier|static
specifier|final
name|byte
index|[]
name|pattern
init|=
block|{
literal|'D'
block|,
literal|'E'
block|,
literal|'A'
block|,
literal|'D'
block|,
literal|'B'
block|,
literal|'E'
block|,
literal|'E'
block|,
literal|'F'
block|}
decl_stmt|;
DECL|field|numDatanodes
specifier|static
specifier|final
name|int
name|numDatanodes
init|=
literal|3
decl_stmt|;
comment|// creates a file
DECL|method|createFile (FileSystem fileSys, Path name, int repl, final long blockSize)
specifier|static
name|FSDataOutputStream
name|createFile
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|,
name|int
name|repl
parameter_list|,
specifier|final
name|long
name|blockSize
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|stm
init|=
name|fileSys
operator|.
name|create
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|fileSys
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
literal|4096
argument_list|)
argument_list|,
operator|(
name|short
operator|)
name|repl
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"createFile: Created "
operator|+
name|name
operator|+
literal|" with "
operator|+
name|repl
operator|+
literal|" replica."
argument_list|)
expr_stmt|;
return|return
name|stm
return|;
block|}
comment|/**    * Writes pattern to file    * @param stm FSDataOutputStream to write the file    * @param fileSize size of the file to be written    * @throws IOException in case of errors    */
DECL|method|writeFile (FSDataOutputStream stm, final long fileSize)
specifier|static
name|void
name|writeFile
parameter_list|(
name|FSDataOutputStream
name|stm
parameter_list|,
specifier|final
name|long
name|fileSize
parameter_list|)
throws|throws
name|IOException
block|{
comment|// write in chunks of 64 MB
specifier|final
name|int
name|writeSize
init|=
name|pattern
operator|.
name|length
operator|*
literal|8
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
if|if
condition|(
name|writeSize
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"A single write is too large "
operator|+
name|writeSize
argument_list|)
throw|;
block|}
name|long
name|bytesToWrite
init|=
name|fileSize
decl_stmt|;
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|writeSize
index|]
decl_stmt|;
comment|// initialize buffer
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|writeSize
condition|;
name|j
operator|++
control|)
block|{
name|b
index|[
name|j
index|]
operator|=
name|pattern
index|[
name|j
operator|%
name|pattern
operator|.
name|length
index|]
expr_stmt|;
block|}
while|while
condition|(
name|bytesToWrite
operator|>
literal|0
condition|)
block|{
comment|// how many bytes we are writing in this iteration
name|int
name|thiswrite
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|writeSize
argument_list|,
name|bytesToWrite
argument_list|)
decl_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|thiswrite
argument_list|)
expr_stmt|;
name|bytesToWrite
operator|-=
name|thiswrite
expr_stmt|;
block|}
block|}
comment|/**    * Reads from file and makes sure that it matches the pattern    * @param fs a reference to FileSystem    * @param name Path of a file    * @param fileSize size of the file    * @throws IOException in case of errors    */
DECL|method|checkFullFile (FileSystem fs, Path name, final long fileSize)
specifier|static
name|void
name|checkFullFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|name
parameter_list|,
specifier|final
name|long
name|fileSize
parameter_list|)
throws|throws
name|IOException
block|{
comment|// read in chunks of 128 MB
specifier|final
name|int
name|readSize
init|=
name|pattern
operator|.
name|length
operator|*
literal|16
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
if|if
condition|(
name|readSize
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"A single read is too large "
operator|+
name|readSize
argument_list|)
throw|;
block|}
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|readSize
index|]
decl_stmt|;
name|long
name|bytesToRead
init|=
name|fileSize
decl_stmt|;
name|byte
index|[]
name|compb
init|=
operator|new
name|byte
index|[
name|readSize
index|]
decl_stmt|;
comment|// buffer with correct data for comparison
if|if
condition|(
name|verifyData
condition|)
block|{
comment|// initialize compare buffer
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|readSize
condition|;
name|j
operator|++
control|)
block|{
name|compb
index|[
name|j
index|]
operator|=
name|pattern
index|[
name|j
operator|%
name|pattern
operator|.
name|length
index|]
expr_stmt|;
block|}
block|}
name|FSDataInputStream
name|stm
init|=
name|fs
operator|.
name|open
argument_list|(
name|name
argument_list|)
decl_stmt|;
while|while
condition|(
name|bytesToRead
operator|>
literal|0
condition|)
block|{
comment|// how many bytes we are reading in this iteration
name|int
name|thisread
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|readSize
argument_list|,
name|bytesToRead
argument_list|)
decl_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|thisread
argument_list|)
expr_stmt|;
if|if
condition|(
name|verifyData
condition|)
block|{
comment|// verify data read
if|if
condition|(
name|thisread
operator|==
name|readSize
condition|)
block|{
name|assertTrue
argument_list|(
literal|"file is corrupted at or after byte "
operator|+
operator|(
name|fileSize
operator|-
name|bytesToRead
operator|)
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|b
argument_list|,
name|compb
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// b was only partially filled by last read
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|thisread
condition|;
name|k
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"file is corrupted at or after byte "
operator|+
operator|(
name|fileSize
operator|-
name|bytesToRead
operator|)
argument_list|,
name|b
index|[
name|k
index|]
operator|==
name|compb
index|[
name|k
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Before update: to read: "
operator|+
name|bytesToRead
operator|+
literal|"; read already: "
operator|+
name|thisread
argument_list|)
expr_stmt|;
name|bytesToRead
operator|-=
name|thisread
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"After  update: to read: "
operator|+
name|bytesToRead
operator|+
literal|"; read already: "
operator|+
name|thisread
argument_list|)
expr_stmt|;
block|}
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test for block size of 2GB + 512B. This test can take a rather long time to    * complete on Windows (reading the file back can be slow) so we use a larger    * timeout here.    * @throws IOException in case of errors    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1800000
argument_list|)
DECL|method|testLargeBlockSize ()
specifier|public
name|void
name|testLargeBlockSize
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|long
name|blockSize
init|=
literal|2L
operator|*
literal|1024L
operator|*
literal|1024L
operator|*
literal|1024L
operator|+
literal|512L
decl_stmt|;
comment|// 2GB + 512B
name|runTest
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that we can write to and read from large blocks    * @param blockSize size of the block    * @throws IOException in case of errors    */
DECL|method|runTest (final long blockSize)
specifier|public
name|void
name|runTest
parameter_list|(
specifier|final
name|long
name|blockSize
parameter_list|)
throws|throws
name|IOException
block|{
comment|// write a file that is slightly larger than 1 block
specifier|final
name|long
name|fileSize
init|=
name|blockSize
operator|+
literal|1L
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numDatanodes
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
try|try
block|{
comment|// create a new file in test data directory
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/TestLargeBlock"
argument_list|,
name|blockSize
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|stm
init|=
name|createFile
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|,
literal|1
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"File "
operator|+
name|file1
operator|+
literal|" created with file size "
operator|+
name|fileSize
operator|+
literal|" blocksize "
operator|+
name|blockSize
argument_list|)
expr_stmt|;
comment|// verify that file exists in FS namespace
name|assertTrue
argument_list|(
name|file1
operator|+
literal|" should be a file"
argument_list|,
name|fs
operator|.
name|getFileStatus
argument_list|(
name|file1
argument_list|)
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
comment|// write to file
name|writeFile
argument_list|(
name|stm
argument_list|,
name|fileSize
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"File "
operator|+
name|file1
operator|+
literal|" written to."
argument_list|)
expr_stmt|;
comment|// close file
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"File "
operator|+
name|file1
operator|+
literal|" closed."
argument_list|)
expr_stmt|;
comment|// Make sure a client can read it
name|checkFullFile
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|,
name|fileSize
argument_list|)
expr_stmt|;
comment|// verify that file size has changed
name|long
name|len
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|file1
argument_list|)
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|file1
operator|+
literal|" should be of size "
operator|+
name|fileSize
operator|+
literal|" but found to be of size "
operator|+
name|len
argument_list|,
name|len
operator|==
name|fileSize
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

