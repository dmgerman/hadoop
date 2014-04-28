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
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
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
name|ExecutionException
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
name|ExecutorService
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
name|Executors
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
name|Future
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
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|datatransfer
operator|.
name|DataTransferProtocol
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|SimulatedFSDataset
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

begin_comment
comment|/**  * This class tests the DFS positional read functionality in a single node  * mini-cluster.  */
end_comment

begin_class
DECL|class|TestPread
specifier|public
class|class
name|TestPread
block|{
DECL|field|seed
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0xDEADBEEFL
decl_stmt|;
DECL|field|blockSize
specifier|static
specifier|final
name|int
name|blockSize
init|=
literal|4096
decl_stmt|;
DECL|field|simulatedStorage
name|boolean
name|simulatedStorage
init|=
literal|false
decl_stmt|;
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
name|int
name|replication
init|=
literal|3
decl_stmt|;
comment|// We need> 1 blocks to test out the hedged reads.
comment|// test empty file open and read
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fileSys
argument_list|,
name|name
argument_list|,
literal|12
operator|*
name|blockSize
argument_list|,
literal|0
argument_list|,
name|blockSize
argument_list|,
operator|(
name|short
operator|)
name|replication
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|in
init|=
name|fileSys
operator|.
name|open
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|12
operator|*
name|blockSize
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
literal|0
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|IOException
name|res
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// read beyond the end of the file
name|in
operator|.
name|readFully
argument_list|(
literal|0
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// should throw an exception
name|res
operator|=
name|e
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Error reading beyond file boundary."
argument_list|,
name|res
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|fileSys
operator|.
name|delete
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
condition|)
name|assertTrue
argument_list|(
literal|"Cannot delete file"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// now create the real file
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fileSys
argument_list|,
name|name
argument_list|,
literal|12
operator|*
name|blockSize
argument_list|,
literal|12
operator|*
name|blockSize
argument_list|,
name|blockSize
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|seed
argument_list|)
expr_stmt|;
block|}
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
DECL|method|doPread (FSDataInputStream stm, long position, byte[] buffer, int offset, int length)
specifier|private
name|void
name|doPread
parameter_list|(
name|FSDataInputStream
name|stm
parameter_list|,
name|long
name|position
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|nread
init|=
literal|0
decl_stmt|;
name|long
name|totalRead
init|=
literal|0
decl_stmt|;
name|DFSInputStream
name|dfstm
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|stm
operator|.
name|getWrappedStream
argument_list|()
operator|instanceof
name|DFSInputStream
condition|)
block|{
name|dfstm
operator|=
call|(
name|DFSInputStream
call|)
argument_list|(
name|stm
operator|.
name|getWrappedStream
argument_list|()
argument_list|)
expr_stmt|;
name|totalRead
operator|=
name|dfstm
operator|.
name|getReadStatistics
argument_list|()
operator|.
name|getTotalBytesRead
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
name|nread
operator|<
name|length
condition|)
block|{
name|int
name|nbytes
init|=
name|stm
operator|.
name|read
argument_list|(
name|position
operator|+
name|nread
argument_list|,
name|buffer
argument_list|,
name|offset
operator|+
name|nread
argument_list|,
name|length
operator|-
name|nread
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Error in pread"
argument_list|,
name|nbytes
operator|>
literal|0
argument_list|)
expr_stmt|;
name|nread
operator|+=
name|nbytes
expr_stmt|;
block|}
if|if
condition|(
name|dfstm
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
literal|"Expected read statistic to be incremented"
argument_list|,
name|length
argument_list|,
name|dfstm
operator|.
name|getReadStatistics
argument_list|()
operator|.
name|getTotalBytesRead
argument_list|()
operator|-
name|totalRead
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|pReadFile (FileSystem fileSys, Path name)
specifier|private
name|void
name|pReadFile
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
name|FSDataInputStream
name|stm
init|=
name|fileSys
operator|.
name|open
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|byte
index|[]
name|expected
init|=
operator|new
name|byte
index|[
literal|12
operator|*
name|blockSize
index|]
decl_stmt|;
if|if
condition|(
name|simulatedStorage
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expected
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|expected
index|[
name|i
index|]
operator|=
name|SimulatedFSDataset
operator|.
name|DEFAULT_DATABYTE
expr_stmt|;
block|}
block|}
else|else
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|rand
operator|.
name|nextBytes
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|// do a sanity check. Read first 4K bytes
name|byte
index|[]
name|actual
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
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
literal|0
argument_list|,
name|expected
argument_list|,
literal|"Read Sanity Test"
argument_list|)
expr_stmt|;
comment|// now do a pread for the first 8K bytes
name|actual
operator|=
operator|new
name|byte
index|[
literal|8192
index|]
expr_stmt|;
name|doPread
argument_list|(
name|stm
argument_list|,
literal|0L
argument_list|,
name|actual
argument_list|,
literal|0
argument_list|,
literal|8192
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
literal|"Pread Test 1"
argument_list|)
expr_stmt|;
comment|// Now check to see if the normal read returns 4K-8K byte range
name|actual
operator|=
operator|new
name|byte
index|[
literal|4096
index|]
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
literal|4096
argument_list|,
name|expected
argument_list|,
literal|"Pread Test 2"
argument_list|)
expr_stmt|;
comment|// Now see if we can cross a single block boundary successfully
comment|// read 4K bytes from blockSize - 2K offset
name|stm
operator|.
name|readFully
argument_list|(
name|blockSize
operator|-
literal|2048
argument_list|,
name|actual
argument_list|,
literal|0
argument_list|,
literal|4096
argument_list|)
expr_stmt|;
name|checkAndEraseData
argument_list|(
name|actual
argument_list|,
operator|(
name|blockSize
operator|-
literal|2048
operator|)
argument_list|,
name|expected
argument_list|,
literal|"Pread Test 3"
argument_list|)
expr_stmt|;
comment|// now see if we can cross two block boundaries successfully
comment|// read blockSize + 4K bytes from blockSize - 2K offset
name|actual
operator|=
operator|new
name|byte
index|[
name|blockSize
operator|+
literal|4096
index|]
expr_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
name|blockSize
operator|-
literal|2048
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|checkAndEraseData
argument_list|(
name|actual
argument_list|,
operator|(
name|blockSize
operator|-
literal|2048
operator|)
argument_list|,
name|expected
argument_list|,
literal|"Pread Test 4"
argument_list|)
expr_stmt|;
comment|// now see if we can cross two block boundaries that are not cached
comment|// read blockSize + 4K bytes from 10*blockSize - 2K offset
name|actual
operator|=
operator|new
name|byte
index|[
name|blockSize
operator|+
literal|4096
index|]
expr_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
literal|10
operator|*
name|blockSize
operator|-
literal|2048
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|checkAndEraseData
argument_list|(
name|actual
argument_list|,
operator|(
literal|10
operator|*
name|blockSize
operator|-
literal|2048
operator|)
argument_list|,
name|expected
argument_list|,
literal|"Pread Test 5"
argument_list|)
expr_stmt|;
comment|// now check that even after all these preads, we can still read
comment|// bytes 8K-12K
name|actual
operator|=
operator|new
name|byte
index|[
literal|4096
index|]
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
literal|8192
argument_list|,
name|expected
argument_list|,
literal|"Pread Test 6"
argument_list|)
expr_stmt|;
comment|// done
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// check block location caching
name|stm
operator|=
name|fileSys
operator|.
name|open
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
literal|1
argument_list|,
name|actual
argument_list|,
literal|0
argument_list|,
literal|4096
argument_list|)
expr_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
literal|4
operator|*
name|blockSize
argument_list|,
name|actual
argument_list|,
literal|0
argument_list|,
literal|4096
argument_list|)
expr_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
literal|7
operator|*
name|blockSize
argument_list|,
name|actual
argument_list|,
literal|0
argument_list|,
literal|4096
argument_list|)
expr_stmt|;
name|actual
operator|=
operator|new
name|byte
index|[
literal|3
operator|*
literal|4096
index|]
expr_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
literal|0
operator|*
name|blockSize
argument_list|,
name|actual
argument_list|,
literal|0
argument_list|,
literal|3
operator|*
literal|4096
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
literal|"Pread Test 7"
argument_list|)
expr_stmt|;
name|actual
operator|=
operator|new
name|byte
index|[
literal|8
operator|*
literal|4096
index|]
expr_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
literal|3
operator|*
name|blockSize
argument_list|,
name|actual
argument_list|,
literal|0
argument_list|,
literal|8
operator|*
literal|4096
argument_list|)
expr_stmt|;
name|checkAndEraseData
argument_list|(
name|actual
argument_list|,
literal|3
operator|*
name|blockSize
argument_list|,
name|expected
argument_list|,
literal|"Pread Test 8"
argument_list|)
expr_stmt|;
comment|// read the tail
name|stm
operator|.
name|readFully
argument_list|(
literal|11
operator|*
name|blockSize
operator|+
name|blockSize
operator|/
literal|2
argument_list|,
name|actual
argument_list|,
literal|0
argument_list|,
name|blockSize
operator|/
literal|2
argument_list|)
expr_stmt|;
name|IOException
name|res
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// read beyond the end of the file
name|stm
operator|.
name|readFully
argument_list|(
literal|11
operator|*
name|blockSize
operator|+
name|blockSize
operator|/
literal|2
argument_list|,
name|actual
argument_list|,
literal|0
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// should throw an exception
name|res
operator|=
name|e
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Error reading beyond file boundary."
argument_list|,
name|res
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// test pread can survive datanode restarts
DECL|method|datanodeRestartTest (MiniDFSCluster cluster, FileSystem fileSys, Path name)
specifier|private
name|void
name|datanodeRestartTest
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|)
throws|throws
name|IOException
block|{
comment|// skip this test if using simulated storage since simulated blocks
comment|// don't survive datanode restarts.
if|if
condition|(
name|simulatedStorage
condition|)
block|{
return|return;
block|}
name|int
name|numBlocks
init|=
literal|1
decl_stmt|;
name|assertTrue
argument_list|(
name|numBlocks
operator|<=
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_MAX_BLOCK_ACQUIRE_FAILURES_DEFAULT
argument_list|)
expr_stmt|;
name|byte
index|[]
name|expected
init|=
operator|new
name|byte
index|[
name|numBlocks
operator|*
name|blockSize
index|]
decl_stmt|;
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|rand
operator|.
name|nextBytes
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|byte
index|[]
name|actual
init|=
operator|new
name|byte
index|[
name|numBlocks
operator|*
name|blockSize
index|]
decl_stmt|;
name|FSDataInputStream
name|stm
init|=
name|fileSys
operator|.
name|open
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|// read a block and get block locations cached as a result
name|stm
operator|.
name|readFully
argument_list|(
literal|0
argument_list|,
name|actual
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
literal|"Pread Datanode Restart Setup"
argument_list|)
expr_stmt|;
comment|// restart all datanodes. it is expected that they will
comment|// restart on different ports, hence, cached block locations
comment|// will no longer work.
name|assertTrue
argument_list|(
name|cluster
operator|.
name|restartDataNodes
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// verify the block can be read again using the same InputStream
comment|// (via re-fetching of block locations from namenode). there is a
comment|// 3 sec sleep in chooseDataNode(), which can be shortened for
comment|// this test if configurable.
name|stm
operator|.
name|readFully
argument_list|(
literal|0
argument_list|,
name|actual
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
literal|"Pread Datanode Restart Test"
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
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|delete
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
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
DECL|method|getPReadFileCallable (final FileSystem fileSys, final Path file)
specifier|private
name|Callable
argument_list|<
name|Void
argument_list|>
name|getPReadFileCallable
parameter_list|(
specifier|final
name|FileSystem
name|fileSys
parameter_list|,
specifier|final
name|Path
name|file
parameter_list|)
block|{
return|return
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|IOException
block|{
name|pReadFile
argument_list|(
name|fileSys
argument_list|,
name|file
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
comment|/**    * Tests positional read in DFS.    */
annotation|@
name|Test
DECL|method|testPreadDFS ()
specifier|public
name|void
name|testPreadDFS
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|dfsPreadTest
argument_list|(
name|conf
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// normal pread
name|dfsPreadTest
argument_list|(
name|conf
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// trigger read code path without
comment|// transferTo.
block|}
annotation|@
name|Test
DECL|method|testPreadDFSNoChecksum ()
specifier|public
name|void
name|testPreadDFSNoChecksum
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|DataTransferProtocol
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|dfsPreadTest
argument_list|(
name|conf
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dfsPreadTest
argument_list|(
name|conf
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests positional read in DFS, with hedged reads enabled.    */
annotation|@
name|Test
DECL|method|testHedgedPreadDFSBasic ()
specifier|public
name|void
name|testHedgedPreadDFSBasic
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DFSCLIENT_HEDGED_READ_THREADPOOL_SIZE
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DFSCLIENT_HEDGED_READ_THRESHOLD_MILLIS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|dfsPreadTest
argument_list|(
name|conf
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// normal pread
name|dfsPreadTest
argument_list|(
name|conf
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// trigger read code path without
comment|// transferTo.
block|}
annotation|@
name|Test
DECL|method|testMaxOutHedgedReadPool ()
specifier|public
name|void
name|testMaxOutHedgedReadPool
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|ExecutionException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|int
name|numHedgedReadPoolThreads
init|=
literal|5
decl_stmt|;
specifier|final
name|int
name|initialHedgedReadTimeoutMillis
init|=
literal|50000
decl_stmt|;
specifier|final
name|int
name|fixedSleepIntervalMillis
init|=
literal|50
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DFSCLIENT_HEDGED_READ_THREADPOOL_SIZE
argument_list|,
name|numHedgedReadPoolThreads
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DFSCLIENT_HEDGED_READ_THRESHOLD_MILLIS
argument_list|,
name|initialHedgedReadTimeoutMillis
argument_list|)
expr_stmt|;
comment|// Set up the InjectionHandler
name|DFSClientFaultInjector
operator|.
name|instance
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DFSClientFaultInjector
operator|.
name|class
argument_list|)
expr_stmt|;
name|DFSClientFaultInjector
name|injector
init|=
name|DFSClientFaultInjector
operator|.
name|instance
decl_stmt|;
comment|// make preads sleep for 50ms
name|Mockito
operator|.
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|fixedSleepIntervalMillis
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|injector
argument_list|)
operator|.
name|startFetchFromDatanode
argument_list|()
expr_stmt|;
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
literal|3
argument_list|)
operator|.
name|format
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|DistributedFileSystem
name|fileSys
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|DFSClient
name|dfsClient
init|=
name|fileSys
operator|.
name|getClient
argument_list|()
decl_stmt|;
name|DFSHedgedReadMetrics
name|metrics
init|=
name|dfsClient
operator|.
name|getHedgedReadMetrics
argument_list|()
decl_stmt|;
comment|// Metrics instance is static, so we need to reset counts from prior tests.
name|metrics
operator|.
name|hedgedReadOps
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|hedgedReadOpsWin
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|hedgedReadOpsInCurThread
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
try|try
block|{
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
literal|"hedgedReadMaxOut.dat"
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|)
expr_stmt|;
comment|// Basic test. Reads complete within timeout. Assert that there were no
comment|// hedged reads.
name|pReadFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|)
expr_stmt|;
comment|// assert that there were no hedged reads. 50ms + delta< 500ms
name|assertTrue
argument_list|(
name|metrics
operator|.
name|getHedgedReadOps
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metrics
operator|.
name|getHedgedReadOpsInCurThread
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
comment|/*        * Reads take longer than timeout. But, only one thread reading. Assert        * that there were hedged reads. But, none of the reads had to run in the        * current thread.        */
name|dfsClient
operator|.
name|setHedgedReadTimeout
argument_list|(
literal|50
argument_list|)
expr_stmt|;
comment|// 50ms
name|pReadFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|)
expr_stmt|;
comment|// assert that there were hedged reads
name|assertTrue
argument_list|(
name|metrics
operator|.
name|getHedgedReadOps
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metrics
operator|.
name|getHedgedReadOpsInCurThread
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
comment|/*        * Multiple threads reading. Reads take longer than timeout. Assert that        * there were hedged reads. And that reads had to run in the current        * thread.        */
name|int
name|factor
init|=
literal|10
decl_stmt|;
name|int
name|numHedgedReads
init|=
name|numHedgedReadPoolThreads
operator|*
name|factor
decl_stmt|;
name|long
name|initialReadOpsValue
init|=
name|metrics
operator|.
name|getHedgedReadOps
argument_list|()
decl_stmt|;
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|numHedgedReads
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|Future
argument_list|<
name|Void
argument_list|>
argument_list|>
name|futures
init|=
operator|new
name|ArrayList
argument_list|<
name|Future
argument_list|<
name|Void
argument_list|>
argument_list|>
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
name|numHedgedReads
condition|;
name|i
operator|++
control|)
block|{
name|futures
operator|.
name|add
argument_list|(
name|executor
operator|.
name|submit
argument_list|(
name|getPReadFileCallable
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numHedgedReads
condition|;
name|i
operator|++
control|)
block|{
name|futures
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|metrics
operator|.
name|getHedgedReadOps
argument_list|()
operator|>
name|initialReadOpsValue
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|metrics
operator|.
name|getHedgedReadOpsInCurThread
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
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
name|Mockito
operator|.
name|reset
argument_list|(
name|injector
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|dfsPreadTest (Configuration conf, boolean disableTransferTo, boolean verifyChecksum)
specifier|private
name|void
name|dfsPreadTest
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|boolean
name|disableTransferTo
parameter_list|,
name|boolean
name|verifyChecksum
parameter_list|)
throws|throws
name|IOException
block|{
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
literal|4096
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_READ_PREFETCH_SIZE_KEY
argument_list|,
literal|4096
argument_list|)
expr_stmt|;
if|if
condition|(
name|simulatedStorage
condition|)
block|{
name|SimulatedFSDataset
operator|.
name|setFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|disableTransferTo
condition|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"dfs.datanode.transferTo.allowed"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
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
literal|3
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
name|fileSys
operator|.
name|setVerifyChecksum
argument_list|(
name|verifyChecksum
argument_list|)
expr_stmt|;
try|try
block|{
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
literal|"preadtest.dat"
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|)
expr_stmt|;
name|pReadFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|)
expr_stmt|;
name|datanodeRestartTest
argument_list|(
name|cluster
argument_list|,
name|fileSys
argument_list|,
name|file1
argument_list|)
expr_stmt|;
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|file1
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
block|}
annotation|@
name|Test
DECL|method|testPreadDFSSimulated ()
specifier|public
name|void
name|testPreadDFSSimulated
parameter_list|()
throws|throws
name|IOException
block|{
name|simulatedStorage
operator|=
literal|true
expr_stmt|;
name|testPreadDFS
argument_list|()
expr_stmt|;
name|simulatedStorage
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * Tests positional read in LocalFS.    */
annotation|@
name|Test
DECL|method|testPreadLocalFS ()
specifier|public
name|void
name|testPreadLocalFS
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|FileSystem
name|fileSys
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
literal|"build/test/data"
argument_list|,
literal|"preadtest.dat"
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|)
expr_stmt|;
name|pReadFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|)
expr_stmt|;
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|file1
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
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
operator|new
name|TestPread
argument_list|()
operator|.
name|testPreadDFS
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

