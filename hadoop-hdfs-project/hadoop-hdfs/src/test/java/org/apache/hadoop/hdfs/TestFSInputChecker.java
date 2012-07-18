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
name|assertFalse
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
name|File
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
name|io
operator|.
name|RandomAccessFile
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
name|ChecksumException
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
name|LocalFileSystem
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
name|Test
import|;
end_import

begin_comment
comment|/**  * This class tests if FSInputChecker works correctly.  */
end_comment

begin_class
DECL|class|TestFSInputChecker
specifier|public
class|class
name|TestFSInputChecker
block|{
DECL|field|seed
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0xDEADBEEFL
decl_stmt|;
DECL|field|BYTES_PER_SUM
specifier|static
specifier|final
name|int
name|BYTES_PER_SUM
init|=
literal|10
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|2
operator|*
name|BYTES_PER_SUM
decl_stmt|;
DECL|field|HALF_CHUNK_SIZE
specifier|static
specifier|final
name|int
name|HALF_CHUNK_SIZE
init|=
name|BYTES_PER_SUM
operator|/
literal|2
decl_stmt|;
DECL|field|FILE_SIZE
specifier|static
specifier|final
name|int
name|FILE_SIZE
init|=
literal|2
operator|*
name|BLOCK_SIZE
operator|-
literal|1
decl_stmt|;
DECL|field|NUM_OF_DATANODES
specifier|static
specifier|final
name|short
name|NUM_OF_DATANODES
init|=
literal|2
decl_stmt|;
DECL|field|expected
name|byte
index|[]
name|expected
init|=
operator|new
name|byte
index|[
name|FILE_SIZE
index|]
decl_stmt|;
DECL|field|actual
name|byte
index|[]
name|actual
decl_stmt|;
DECL|field|stm
name|FSDataInputStream
name|stm
decl_stmt|;
DECL|field|rand
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
comment|/* create a file */
DECL|method|writeFile (FileSystem fileSys, Path name)
specifier|private
name|void
name|writeFile
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|)
throws|throws
name|IOException
block|{
comment|// create and write a file that contains three blocks of data
name|FSDataOutputStream
name|stm
init|=
name|fileSys
operator|.
name|create
argument_list|(
name|name
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
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
name|NUM_OF_DATANODES
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/*validate data*/
DECL|method|checkAndEraseData (byte[] actual, int from, byte[] expected, String message)
specifier|private
name|void
name|checkAndEraseData
parameter_list|(
name|byte
index|[]
name|actual
parameter_list|,
name|int
name|from
parameter_list|,
name|byte
index|[]
name|expected
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|actual
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|message
operator|+
literal|" byte "
operator|+
operator|(
name|from
operator|+
name|idx
operator|)
operator|+
literal|" differs. expected "
operator|+
name|expected
index|[
name|from
operator|+
name|idx
index|]
operator|+
literal|" actual "
operator|+
name|actual
index|[
name|idx
index|]
argument_list|,
name|actual
index|[
name|idx
index|]
argument_list|,
name|expected
index|[
name|from
operator|+
name|idx
index|]
argument_list|)
expr_stmt|;
name|actual
index|[
name|idx
index|]
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/* test read and getPos */
DECL|method|checkReadAndGetPos ()
specifier|private
name|void
name|checkReadAndGetPos
parameter_list|()
throws|throws
name|Exception
block|{
name|actual
operator|=
operator|new
name|byte
index|[
name|FILE_SIZE
index|]
expr_stmt|;
comment|// test reads that do not cross checksum boundary
name|stm
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|int
name|offset
decl_stmt|;
for|for
control|(
name|offset
operator|=
literal|0
init|;
name|offset
operator|<
name|BLOCK_SIZE
operator|+
name|BYTES_PER_SUM
condition|;
name|offset
operator|+=
name|BYTES_PER_SUM
control|)
block|{
name|assertEquals
argument_list|(
name|stm
operator|.
name|getPos
argument_list|()
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
name|actual
argument_list|,
name|offset
argument_list|,
name|BYTES_PER_SUM
argument_list|)
expr_stmt|;
block|}
name|stm
operator|.
name|readFully
argument_list|(
name|actual
argument_list|,
name|offset
argument_list|,
name|FILE_SIZE
operator|-
name|BLOCK_SIZE
operator|-
name|BYTES_PER_SUM
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stm
operator|.
name|getPos
argument_list|()
argument_list|,
name|FILE_SIZE
argument_list|)
expr_stmt|;
name|checkAndEraseData
argument_list|(
name|actual
argument_list|,
literal|0
argument_list|,
name|expected
argument_list|,
literal|"Read Sanity Test"
argument_list|)
expr_stmt|;
comment|// test reads that cross checksum boundary
name|stm
operator|.
name|seek
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stm
operator|.
name|getPos
argument_list|()
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
name|actual
argument_list|,
literal|0
argument_list|,
name|HALF_CHUNK_SIZE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stm
operator|.
name|getPos
argument_list|()
argument_list|,
name|HALF_CHUNK_SIZE
argument_list|)
expr_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
name|actual
argument_list|,
name|HALF_CHUNK_SIZE
argument_list|,
name|BLOCK_SIZE
operator|-
name|HALF_CHUNK_SIZE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stm
operator|.
name|getPos
argument_list|()
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
name|actual
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|BYTES_PER_SUM
operator|+
name|HALF_CHUNK_SIZE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stm
operator|.
name|getPos
argument_list|()
argument_list|,
name|BLOCK_SIZE
operator|+
name|BYTES_PER_SUM
operator|+
name|HALF_CHUNK_SIZE
argument_list|)
expr_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
name|actual
argument_list|,
literal|2
operator|*
name|BLOCK_SIZE
operator|-
name|HALF_CHUNK_SIZE
argument_list|,
name|FILE_SIZE
operator|-
operator|(
literal|2
operator|*
name|BLOCK_SIZE
operator|-
name|HALF_CHUNK_SIZE
operator|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stm
operator|.
name|getPos
argument_list|()
argument_list|,
name|FILE_SIZE
argument_list|)
expr_stmt|;
name|checkAndEraseData
argument_list|(
name|actual
argument_list|,
literal|0
argument_list|,
name|expected
argument_list|,
literal|"Read Sanity Test"
argument_list|)
expr_stmt|;
comment|// test read that cross block boundary
name|stm
operator|.
name|seek
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
name|actual
argument_list|,
literal|0
argument_list|,
name|BYTES_PER_SUM
operator|+
name|HALF_CHUNK_SIZE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stm
operator|.
name|getPos
argument_list|()
argument_list|,
name|BYTES_PER_SUM
operator|+
name|HALF_CHUNK_SIZE
argument_list|)
expr_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
name|actual
argument_list|,
name|BYTES_PER_SUM
operator|+
name|HALF_CHUNK_SIZE
argument_list|,
name|BYTES_PER_SUM
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stm
operator|.
name|getPos
argument_list|()
argument_list|,
name|BLOCK_SIZE
operator|+
name|HALF_CHUNK_SIZE
argument_list|)
expr_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
name|actual
argument_list|,
name|BLOCK_SIZE
operator|+
name|HALF_CHUNK_SIZE
argument_list|,
name|FILE_SIZE
operator|-
name|BLOCK_SIZE
operator|-
name|HALF_CHUNK_SIZE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stm
operator|.
name|getPos
argument_list|()
argument_list|,
name|FILE_SIZE
argument_list|)
expr_stmt|;
name|checkAndEraseData
argument_list|(
name|actual
argument_list|,
literal|0
argument_list|,
name|expected
argument_list|,
literal|"Read Sanity Test"
argument_list|)
expr_stmt|;
block|}
comment|/* test if one seek is correct */
DECL|method|testSeek1 (int offset)
specifier|private
name|void
name|testSeek1
parameter_list|(
name|int
name|offset
parameter_list|)
throws|throws
name|Exception
block|{
name|stm
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|offset
argument_list|,
name|stm
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
name|actual
argument_list|)
expr_stmt|;
name|checkAndEraseData
argument_list|(
name|actual
argument_list|,
name|offset
argument_list|,
name|expected
argument_list|,
literal|"Read Sanity Test"
argument_list|)
expr_stmt|;
block|}
comment|/* test seek() */
DECL|method|checkSeek ( )
specifier|private
name|void
name|checkSeek
parameter_list|( )
throws|throws
name|Exception
block|{
name|actual
operator|=
operator|new
name|byte
index|[
name|HALF_CHUNK_SIZE
index|]
expr_stmt|;
comment|// test seeks to checksum boundary
name|testSeek1
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|testSeek1
argument_list|(
name|BYTES_PER_SUM
argument_list|)
expr_stmt|;
name|testSeek1
argument_list|(
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
comment|// test seek to non-checksum-boundary pos
name|testSeek1
argument_list|(
name|BLOCK_SIZE
operator|+
name|HALF_CHUNK_SIZE
argument_list|)
expr_stmt|;
name|testSeek1
argument_list|(
name|HALF_CHUNK_SIZE
argument_list|)
expr_stmt|;
comment|// test seek to a position at the same checksum chunk
name|testSeek1
argument_list|(
name|HALF_CHUNK_SIZE
operator|/
literal|2
argument_list|)
expr_stmt|;
name|testSeek1
argument_list|(
name|HALF_CHUNK_SIZE
operator|*
literal|3
operator|/
literal|2
argument_list|)
expr_stmt|;
comment|// test end of file
name|actual
operator|=
operator|new
name|byte
index|[
literal|1
index|]
expr_stmt|;
name|testSeek1
argument_list|(
name|FILE_SIZE
operator|-
literal|1
argument_list|)
expr_stmt|;
name|String
name|errMsg
init|=
literal|null
decl_stmt|;
try|try
block|{
name|stm
operator|.
name|seek
argument_list|(
name|FILE_SIZE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|errMsg
operator|=
name|e
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|errMsg
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/* test if one skip is correct */
DECL|method|testSkip1 (int skippedBytes)
specifier|private
name|void
name|testSkip1
parameter_list|(
name|int
name|skippedBytes
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|oldPos
init|=
name|stm
operator|.
name|getPos
argument_list|()
decl_stmt|;
name|long
name|nSkipped
init|=
name|stm
operator|.
name|skip
argument_list|(
name|skippedBytes
argument_list|)
decl_stmt|;
name|long
name|newPos
init|=
name|oldPos
operator|+
name|nSkipped
decl_stmt|;
name|assertEquals
argument_list|(
name|stm
operator|.
name|getPos
argument_list|()
argument_list|,
name|newPos
argument_list|)
expr_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
name|actual
argument_list|)
expr_stmt|;
name|checkAndEraseData
argument_list|(
name|actual
argument_list|,
operator|(
name|int
operator|)
name|newPos
argument_list|,
name|expected
argument_list|,
literal|"Read Sanity Test"
argument_list|)
expr_stmt|;
block|}
comment|/* test skip() */
DECL|method|checkSkip ( )
specifier|private
name|void
name|checkSkip
parameter_list|( )
throws|throws
name|Exception
block|{
name|actual
operator|=
operator|new
name|byte
index|[
name|HALF_CHUNK_SIZE
index|]
expr_stmt|;
comment|// test skip to a checksum boundary
name|stm
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|testSkip1
argument_list|(
name|BYTES_PER_SUM
argument_list|)
expr_stmt|;
name|testSkip1
argument_list|(
name|HALF_CHUNK_SIZE
argument_list|)
expr_stmt|;
name|testSkip1
argument_list|(
name|HALF_CHUNK_SIZE
argument_list|)
expr_stmt|;
comment|// test skip to non-checksum-boundary pos
name|stm
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|testSkip1
argument_list|(
name|HALF_CHUNK_SIZE
operator|+
literal|1
argument_list|)
expr_stmt|;
name|testSkip1
argument_list|(
name|BYTES_PER_SUM
argument_list|)
expr_stmt|;
name|testSkip1
argument_list|(
name|HALF_CHUNK_SIZE
argument_list|)
expr_stmt|;
comment|// test skip to a position at the same checksum chunk
name|stm
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|testSkip1
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|testSkip1
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// test skip to end of file
name|stm
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|actual
operator|=
operator|new
name|byte
index|[
literal|1
index|]
expr_stmt|;
name|testSkip1
argument_list|(
name|FILE_SIZE
operator|-
literal|1
argument_list|)
expr_stmt|;
name|stm
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stm
operator|.
name|skip
argument_list|(
name|FILE_SIZE
argument_list|)
argument_list|,
name|FILE_SIZE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stm
operator|.
name|skip
argument_list|(
literal|10
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|stm
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stm
operator|.
name|skip
argument_list|(
name|FILE_SIZE
operator|+
literal|10
argument_list|)
argument_list|,
name|FILE_SIZE
argument_list|)
expr_stmt|;
name|stm
operator|.
name|seek
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stm
operator|.
name|skip
argument_list|(
name|FILE_SIZE
argument_list|)
argument_list|,
name|FILE_SIZE
operator|-
literal|10
argument_list|)
expr_stmt|;
block|}
DECL|method|cleanupFile (FileSystem fileSys, Path name)
specifier|private
name|void
name|cleanupFile
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|exists
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|fileSys
operator|.
name|delete
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|fileSys
operator|.
name|exists
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests read/seek/getPos/skipped opeation for input stream.    */
DECL|method|testChecker (FileSystem fileSys, boolean readCS)
specifier|private
name|void
name|testChecker
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|boolean
name|readCS
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"try.dat"
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|fileSys
argument_list|,
name|file
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|readCS
condition|)
block|{
name|fileSys
operator|.
name|setVerifyChecksum
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|stm
operator|=
name|fileSys
operator|.
name|open
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|checkReadAndGetPos
argument_list|()
expr_stmt|;
name|checkSeek
argument_list|()
expr_stmt|;
name|checkSkip
argument_list|()
expr_stmt|;
comment|//checkMark
name|assertFalse
argument_list|(
name|stm
operator|.
name|markSupported
argument_list|()
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|readCS
condition|)
block|{
name|fileSys
operator|.
name|setVerifyChecksum
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFileCorruption (LocalFileSystem fileSys)
specifier|private
name|void
name|testFileCorruption
parameter_list|(
name|LocalFileSystem
name|fileSys
parameter_list|)
throws|throws
name|IOException
block|{
comment|// create a file and verify that checksum corruption results in
comment|// a checksum exception on LocalFS
name|String
name|dir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"."
argument_list|)
decl_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|dir
operator|+
literal|"/corruption-test.dat"
argument_list|)
decl_stmt|;
name|Path
name|crcFile
init|=
operator|new
name|Path
argument_list|(
name|dir
operator|+
literal|"/.corruption-test.dat.crc"
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|fileSys
argument_list|,
name|file
argument_list|)
expr_stmt|;
name|int
name|fileLen
init|=
operator|(
name|int
operator|)
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|fileLen
index|]
decl_stmt|;
name|InputStream
name|in
init|=
name|fileSys
operator|.
name|open
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// check .crc corruption
name|checkFileCorruption
argument_list|(
name|fileSys
argument_list|,
name|file
argument_list|,
name|crcFile
argument_list|)
expr_stmt|;
name|fileSys
operator|.
name|delete
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|fileSys
argument_list|,
name|file
argument_list|)
expr_stmt|;
comment|// check data corrutpion
name|checkFileCorruption
argument_list|(
name|fileSys
argument_list|,
name|file
argument_list|,
name|file
argument_list|)
expr_stmt|;
name|fileSys
operator|.
name|delete
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|checkFileCorruption (LocalFileSystem fileSys, Path file, Path fileToCorrupt)
specifier|private
name|void
name|checkFileCorruption
parameter_list|(
name|LocalFileSystem
name|fileSys
parameter_list|,
name|Path
name|file
parameter_list|,
name|Path
name|fileToCorrupt
parameter_list|)
throws|throws
name|IOException
block|{
comment|// corrupt the file
name|RandomAccessFile
name|out
init|=
operator|new
name|RandomAccessFile
argument_list|(
operator|new
name|File
argument_list|(
name|fileToCorrupt
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|file
argument_list|)
operator|.
name|getLen
argument_list|()
index|]
decl_stmt|;
name|int
name|corruptFileLen
init|=
operator|(
name|int
operator|)
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|fileToCorrupt
argument_list|)
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|buf
operator|.
name|length
operator|>=
name|corruptFileLen
argument_list|)
expr_stmt|;
name|rand
operator|.
name|nextBytes
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|out
operator|.
name|seek
argument_list|(
name|corruptFileLen
operator|/
literal|2
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|corruptFileLen
operator|/
literal|4
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|boolean
name|gotException
init|=
literal|false
decl_stmt|;
name|InputStream
name|in
init|=
name|fileSys
operator|.
name|open
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ChecksumException
name|e
parameter_list|)
block|{
name|gotException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|gotException
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFSInputChecker ()
specifier|public
name|void
name|testFSInputChecker
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
name|BYTES_PER_SUM
argument_list|)
expr_stmt|;
name|rand
operator|.
name|nextBytes
argument_list|(
name|expected
argument_list|)
expr_stmt|;
comment|// test DFS
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
name|build
argument_list|()
decl_stmt|;
name|FileSystem
name|fileSys
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
try|try
block|{
name|testChecker
argument_list|(
name|fileSys
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|testChecker
argument_list|(
name|fileSys
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testSeekAndRead
argument_list|(
name|fileSys
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fileSys
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|// test Local FS
name|fileSys
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
expr_stmt|;
try|try
block|{
name|testChecker
argument_list|(
name|fileSys
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|testChecker
argument_list|(
name|fileSys
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testFileCorruption
argument_list|(
operator|(
name|LocalFileSystem
operator|)
name|fileSys
argument_list|)
expr_stmt|;
name|testSeekAndRead
argument_list|(
name|fileSys
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fileSys
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testSeekAndRead (FileSystem fileSys)
specifier|private
name|void
name|testSeekAndRead
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"try.dat"
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|fileSys
argument_list|,
name|file
argument_list|)
expr_stmt|;
name|stm
operator|=
name|fileSys
operator|.
name|open
argument_list|(
name|file
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
argument_list|)
expr_stmt|;
name|checkSeekAndRead
argument_list|()
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
DECL|method|checkSeekAndRead ()
specifier|private
name|void
name|checkSeekAndRead
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|position
init|=
literal|1
decl_stmt|;
name|int
name|len
init|=
literal|2
operator|*
name|BYTES_PER_SUM
operator|-
name|position
decl_stmt|;
name|readAndCompare
argument_list|(
name|stm
argument_list|,
name|position
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|position
operator|=
name|BYTES_PER_SUM
expr_stmt|;
name|len
operator|=
name|BYTES_PER_SUM
expr_stmt|;
name|readAndCompare
argument_list|(
name|stm
argument_list|,
name|position
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|readAndCompare (FSDataInputStream in, int position, int len)
specifier|private
name|void
name|readAndCompare
parameter_list|(
name|FSDataInputStream
name|in
parameter_list|,
name|int
name|position
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
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
name|b
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|expected
index|[
name|position
operator|+
name|i
index|]
argument_list|,
name|b
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

