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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
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
name|Log
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
name|LogFactory
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
name|DatanodeInfo
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
name|HdfsConstants
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
name|LocatedBlock
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
name|LocatedBlocks
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
name|web
operator|.
name|ByteRangeInputStream
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|TimeoutException
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
name|atomic
operator|.
name|AtomicInteger
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
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
operator|.
name|BLOCK_STRIPED_CELL_SIZE
import|;
end_import

begin_class
DECL|class|StripedFileTestUtil
specifier|public
class|class
name|StripedFileTestUtil
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|StripedFileTestUtil
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dataBlocks
specifier|static
name|int
name|dataBlocks
init|=
name|HdfsConstants
operator|.
name|NUM_DATA_BLOCKS
decl_stmt|;
DECL|field|parityBlocks
specifier|static
name|int
name|parityBlocks
init|=
name|HdfsConstants
operator|.
name|NUM_PARITY_BLOCKS
decl_stmt|;
DECL|field|cellSize
specifier|static
specifier|final
name|int
name|cellSize
init|=
name|HdfsConstants
operator|.
name|BLOCK_STRIPED_CELL_SIZE
decl_stmt|;
DECL|field|stripesPerBlock
specifier|static
specifier|final
name|int
name|stripesPerBlock
init|=
literal|4
decl_stmt|;
DECL|field|blockSize
specifier|static
specifier|final
name|int
name|blockSize
init|=
name|cellSize
operator|*
name|stripesPerBlock
decl_stmt|;
DECL|field|numDNs
specifier|static
specifier|final
name|int
name|numDNs
init|=
name|dataBlocks
operator|+
name|parityBlocks
operator|+
literal|2
decl_stmt|;
DECL|field|random
specifier|static
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|method|generateBytes (int cnt)
specifier|static
name|byte
index|[]
name|generateBytes
parameter_list|(
name|int
name|cnt
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|cnt
index|]
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
name|cnt
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
name|i
index|]
operator|=
name|getByte
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
DECL|method|readAll (FSDataInputStream in, byte[] buf)
specifier|static
name|int
name|readAll
parameter_list|(
name|FSDataInputStream
name|in
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|readLen
init|=
literal|0
decl_stmt|;
name|int
name|ret
decl_stmt|;
while|while
condition|(
operator|(
name|ret
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|,
name|readLen
argument_list|,
name|buf
operator|.
name|length
operator|-
name|readLen
argument_list|)
operator|)
operator|>=
literal|0
operator|&&
name|readLen
operator|<=
name|buf
operator|.
name|length
condition|)
block|{
name|readLen
operator|+=
name|ret
expr_stmt|;
block|}
return|return
name|readLen
return|;
block|}
DECL|method|getByte (long pos)
specifier|static
name|byte
name|getByte
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
specifier|final
name|int
name|mod
init|=
literal|29
decl_stmt|;
return|return
call|(
name|byte
call|)
argument_list|(
name|pos
operator|%
name|mod
operator|+
literal|1
argument_list|)
return|;
block|}
DECL|method|verifyLength (FileSystem fs, Path srcPath, int fileLength)
specifier|static
name|void
name|verifyLength
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|srcPath
parameter_list|,
name|int
name|fileLength
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"File length should be the same"
argument_list|,
name|fileLength
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyPread (FileSystem fs, Path srcPath, int fileLength, byte[] expected, byte[] buf)
specifier|static
name|void
name|verifyPread
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|srcPath
parameter_list|,
name|int
name|fileLength
parameter_list|,
name|byte
index|[]
name|expected
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|srcPath
argument_list|)
init|)
block|{
name|int
index|[]
name|startOffsets
init|=
block|{
literal|0
block|,
literal|1
block|,
name|cellSize
operator|-
literal|102
block|,
name|cellSize
block|,
name|cellSize
operator|+
literal|102
block|,
name|cellSize
operator|*
operator|(
name|dataBlocks
operator|-
literal|1
operator|)
block|,
name|cellSize
operator|*
operator|(
name|dataBlocks
operator|-
literal|1
operator|)
operator|+
literal|102
block|,
name|cellSize
operator|*
name|dataBlocks
block|,
name|fileLength
operator|-
literal|102
block|,
name|fileLength
operator|-
literal|1
block|}
decl_stmt|;
for|for
control|(
name|int
name|startOffset
range|:
name|startOffsets
control|)
block|{
name|startOffset
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|startOffset
argument_list|,
name|fileLength
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|remaining
init|=
name|fileLength
operator|-
name|startOffset
decl_stmt|;
name|int
name|offset
init|=
name|startOffset
decl_stmt|;
specifier|final
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
name|remaining
index|]
decl_stmt|;
while|while
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
name|int
name|target
init|=
name|Math
operator|.
name|min
argument_list|(
name|remaining
argument_list|,
name|buf
operator|.
name|length
argument_list|)
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|offset
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
name|offset
operator|-
name|startOffset
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|remaining
operator|-=
name|target
expr_stmt|;
name|offset
operator|+=
name|target
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
name|fileLength
operator|-
name|startOffset
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Byte at "
operator|+
operator|(
name|startOffset
operator|+
name|i
operator|)
operator|+
literal|" is different, "
operator|+
literal|"the startOffset is "
operator|+
name|startOffset
argument_list|,
name|expected
index|[
name|startOffset
operator|+
name|i
index|]
argument_list|,
name|result
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|verifyStatefulRead (FileSystem fs, Path srcPath, int fileLength, byte[] expected, byte[] buf)
specifier|static
name|void
name|verifyStatefulRead
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|srcPath
parameter_list|,
name|int
name|fileLength
parameter_list|,
name|byte
index|[]
name|expected
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|srcPath
argument_list|)
init|)
block|{
specifier|final
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
name|fileLength
index|]
decl_stmt|;
name|int
name|readLen
init|=
literal|0
decl_stmt|;
name|int
name|ret
decl_stmt|;
while|while
condition|(
operator|(
name|ret
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
name|readLen
argument_list|,
name|ret
argument_list|)
expr_stmt|;
name|readLen
operator|+=
name|ret
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The length of file should be the same to write size"
argument_list|,
name|fileLength
argument_list|,
name|readLen
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyStatefulRead (FileSystem fs, Path srcPath, int fileLength, byte[] expected, ByteBuffer buf)
specifier|static
name|void
name|verifyStatefulRead
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|srcPath
parameter_list|,
name|int
name|fileLength
parameter_list|,
name|byte
index|[]
name|expected
parameter_list|,
name|ByteBuffer
name|buf
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|srcPath
argument_list|)
init|)
block|{
name|ByteBuffer
name|result
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|fileLength
argument_list|)
decl_stmt|;
name|int
name|readLen
init|=
literal|0
decl_stmt|;
name|int
name|ret
decl_stmt|;
while|while
condition|(
operator|(
name|ret
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|readLen
operator|+=
name|ret
expr_stmt|;
name|buf
operator|.
name|flip
argument_list|()
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|buf
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The length of file should be the same to write size"
argument_list|,
name|fileLength
argument_list|,
name|readLen
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|result
operator|.
name|array
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifySeek (FileSystem fs, Path srcPath, int fileLength)
specifier|static
name|void
name|verifySeek
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|srcPath
parameter_list|,
name|int
name|fileLength
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|srcPath
argument_list|)
init|)
block|{
comment|// seek to 1/2 of content
name|int
name|pos
init|=
name|fileLength
operator|/
literal|2
decl_stmt|;
name|assertSeekAndRead
argument_list|(
name|in
argument_list|,
name|pos
argument_list|,
name|fileLength
argument_list|)
expr_stmt|;
comment|// seek to 1/3 of content
name|pos
operator|=
name|fileLength
operator|/
literal|3
expr_stmt|;
name|assertSeekAndRead
argument_list|(
name|in
argument_list|,
name|pos
argument_list|,
name|fileLength
argument_list|)
expr_stmt|;
comment|// seek to 0 pos
name|pos
operator|=
literal|0
expr_stmt|;
name|assertSeekAndRead
argument_list|(
name|in
argument_list|,
name|pos
argument_list|,
name|fileLength
argument_list|)
expr_stmt|;
if|if
condition|(
name|fileLength
operator|>
name|cellSize
condition|)
block|{
comment|// seek to cellSize boundary
name|pos
operator|=
name|cellSize
operator|-
literal|1
expr_stmt|;
name|assertSeekAndRead
argument_list|(
name|in
argument_list|,
name|pos
argument_list|,
name|fileLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fileLength
operator|>
name|cellSize
operator|*
name|dataBlocks
condition|)
block|{
comment|// seek to striped cell group boundary
name|pos
operator|=
name|cellSize
operator|*
name|dataBlocks
operator|-
literal|1
expr_stmt|;
name|assertSeekAndRead
argument_list|(
name|in
argument_list|,
name|pos
argument_list|,
name|fileLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fileLength
operator|>
name|blockSize
operator|*
name|dataBlocks
condition|)
block|{
comment|// seek to striped block group boundary
name|pos
operator|=
name|blockSize
operator|*
name|dataBlocks
operator|-
literal|1
expr_stmt|;
name|assertSeekAndRead
argument_list|(
name|in
argument_list|,
name|pos
argument_list|,
name|fileLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
operator|(
name|in
operator|.
name|getWrappedStream
argument_list|()
operator|instanceof
name|ByteRangeInputStream
operator|)
condition|)
block|{
try|try
block|{
name|in
operator|.
name|seek
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Should be failed if seek to negative offset"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|in
operator|.
name|seek
argument_list|(
name|fileLength
operator|+
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Should be failed if seek after EOF"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
block|}
DECL|method|assertSeekAndRead (FSDataInputStream fsdis, int pos, int writeBytes)
specifier|static
name|void
name|assertSeekAndRead
parameter_list|(
name|FSDataInputStream
name|fsdis
parameter_list|,
name|int
name|pos
parameter_list|,
name|int
name|writeBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|fsdis
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|writeBytes
index|]
decl_stmt|;
name|int
name|readLen
init|=
name|StripedFileTestUtil
operator|.
name|readAll
argument_list|(
name|fsdis
argument_list|,
name|buf
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|readLen
argument_list|,
name|writeBytes
operator|-
name|pos
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
name|readLen
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Byte at "
operator|+
name|i
operator|+
literal|" should be the same"
argument_list|,
name|StripedFileTestUtil
operator|.
name|getByte
argument_list|(
name|pos
operator|+
name|i
argument_list|)
argument_list|,
name|buf
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|killDatanode (MiniDFSCluster cluster, DFSStripedOutputStream out, final int dnIndex, final AtomicInteger pos)
specifier|static
name|void
name|killDatanode
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|DFSStripedOutputStream
name|out
parameter_list|,
specifier|final
name|int
name|dnIndex
parameter_list|,
specifier|final
name|AtomicInteger
name|pos
parameter_list|)
block|{
specifier|final
name|StripedDataStreamer
name|s
init|=
name|out
operator|.
name|getStripedDataStreamer
argument_list|(
name|dnIndex
argument_list|)
decl_stmt|;
specifier|final
name|DatanodeInfo
name|datanode
init|=
name|getDatanodes
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"killDatanode "
operator|+
name|dnIndex
operator|+
literal|": "
operator|+
name|datanode
operator|+
literal|", pos="
operator|+
name|pos
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|datanode
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getDatanodes (StripedDataStreamer streamer)
specifier|static
name|DatanodeInfo
name|getDatanodes
parameter_list|(
name|StripedDataStreamer
name|streamer
parameter_list|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
specifier|final
name|DatanodeInfo
index|[]
name|datanodes
init|=
name|streamer
operator|.
name|getNodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|datanodes
operator|!=
literal|null
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|datanodes
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|datanodes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
return|return
name|datanodes
index|[
literal|0
index|]
return|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
comment|/**    * If the length of blockGroup is less than a full stripe, it returns the the    * number of actual data internal blocks. Otherwise returns NUM_DATA_BLOCKS.    */
DECL|method|getRealDataBlockNum (int numBytes)
specifier|public
specifier|static
name|short
name|getRealDataBlockNum
parameter_list|(
name|int
name|numBytes
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
name|Math
operator|.
name|min
argument_list|(
name|dataBlocks
argument_list|,
operator|(
name|numBytes
operator|-
literal|1
operator|)
operator|/
name|BLOCK_STRIPED_CELL_SIZE
operator|+
literal|1
argument_list|)
return|;
block|}
DECL|method|getRealTotalBlockNum (int numBytes)
specifier|public
specifier|static
name|short
name|getRealTotalBlockNum
parameter_list|(
name|int
name|numBytes
parameter_list|)
block|{
return|return
call|(
name|short
call|)
argument_list|(
name|getRealDataBlockNum
argument_list|(
name|numBytes
argument_list|)
operator|+
name|parityBlocks
argument_list|)
return|;
block|}
comment|/**    * Wait for all the internalBlocks of the blockGroups of the given file to be reported.    */
DECL|method|waitBlockGroupsReported (DistributedFileSystem fs, String src)
specifier|public
specifier|static
name|void
name|waitBlockGroupsReported
parameter_list|(
name|DistributedFileSystem
name|fs
parameter_list|,
name|String
name|src
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
block|{
name|boolean
name|success
decl_stmt|;
specifier|final
name|int
name|ATTEMPTS
init|=
literal|40
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
do|do
block|{
name|success
operator|=
literal|true
expr_stmt|;
name|count
operator|++
expr_stmt|;
name|LocatedBlocks
name|lbs
init|=
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|src
argument_list|,
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|lb
range|:
name|lbs
operator|.
name|getLocatedBlocks
argument_list|()
control|)
block|{
name|short
name|expected
init|=
name|getRealTotalBlockNum
argument_list|(
operator|(
name|int
operator|)
name|lb
operator|.
name|getBlockSize
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|reported
init|=
name|lb
operator|.
name|getLocations
argument_list|()
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|reported
operator|!=
name|expected
condition|)
block|{
name|success
operator|=
literal|false
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"blockGroup "
operator|+
name|lb
operator|.
name|getBlock
argument_list|()
operator|+
literal|" of file "
operator|+
name|src
operator|+
literal|" has reported internalBlocks "
operator|+
name|reported
operator|+
literal|" (desired "
operator|+
name|expected
operator|+
literal|"); locations "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|' '
argument_list|)
operator|.
name|join
argument_list|(
name|lb
operator|.
name|getLocations
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|success
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"All blockGroups of file "
operator|+
name|src
operator|+
literal|" verified to have all internalBlocks."
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
operator|!
name|success
operator|&&
name|count
operator|<
name|ATTEMPTS
condition|)
do|;
if|if
condition|(
name|count
operator|==
name|ATTEMPTS
condition|)
block|{
throw|throw
operator|new
name|TimeoutException
argument_list|(
literal|"Timed out waiting for "
operator|+
name|src
operator|+
literal|" to have all the internalBlocks"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Generate n random and different numbers within    * specified non-negative integer range    * @param min minimum of the range    * @param max maximum of the range    * @param n number to be generated    * @return    */
DECL|method|randomArray (int min, int max, int n)
specifier|public
specifier|static
name|int
index|[]
name|randomArray
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|,
name|int
name|n
parameter_list|)
block|{
if|if
condition|(
name|n
operator|>
operator|(
name|max
operator|-
name|min
operator|+
literal|1
operator|)
operator|||
name|max
operator|<
name|min
operator|||
name|min
operator|<
literal|0
operator|||
name|max
operator|<
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
name|n
index|]
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|count
operator|<
name|n
condition|)
block|{
name|int
name|num
init|=
call|(
name|int
call|)
argument_list|(
name|Math
operator|.
name|random
argument_list|()
operator|*
operator|(
name|max
operator|-
name|min
operator|)
argument_list|)
operator|+
name|min
decl_stmt|;
name|boolean
name|flag
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|n
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|num
operator|==
name|result
index|[
name|j
index|]
condition|)
block|{
name|flag
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|flag
condition|)
block|{
name|result
index|[
name|count
index|]
operator|=
name|num
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

