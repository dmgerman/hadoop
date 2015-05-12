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
name|BlockLocation
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
name|server
operator|.
name|datanode
operator|.
name|DataNode
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
name|erasurecode
operator|.
name|rawcoder
operator|.
name|RSRawDecoder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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

begin_class
DECL|class|TestWriteReadStripedFile
specifier|public
class|class
name|TestWriteReadStripedFile
block|{
DECL|field|dataBlocks
specifier|private
specifier|static
name|int
name|dataBlocks
init|=
name|HdfsConstants
operator|.
name|NUM_DATA_BLOCKS
decl_stmt|;
DECL|field|parityBlocks
specifier|private
specifier|static
name|int
name|parityBlocks
init|=
name|HdfsConstants
operator|.
name|NUM_PARITY_BLOCKS
decl_stmt|;
DECL|field|fs
specifier|private
specifier|static
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|cellSize
specifier|private
specifier|final
specifier|static
name|int
name|cellSize
init|=
name|HdfsConstants
operator|.
name|BLOCK_STRIPED_CELL_SIZE
decl_stmt|;
DECL|field|stripesPerBlock
specifier|private
specifier|final
specifier|static
name|int
name|stripesPerBlock
init|=
literal|4
decl_stmt|;
DECL|field|blockSize
specifier|static
name|int
name|blockSize
init|=
name|cellSize
operator|*
name|stripesPerBlock
decl_stmt|;
DECL|field|numDNs
specifier|static
name|int
name|numDNs
init|=
name|dataBlocks
operator|+
name|parityBlocks
operator|+
literal|2
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
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
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|cluster
operator|=
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
name|numDNs
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getClient
argument_list|()
operator|.
name|createErasureCodingZone
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFileEmpty ()
specifier|public
name|void
name|testFileEmpty
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/EmptyFile"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileSmallerThanOneCell1 ()
specifier|public
name|void
name|testFileSmallerThanOneCell1
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/SmallerThanOneCell"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileSmallerThanOneCell2 ()
specifier|public
name|void
name|testFileSmallerThanOneCell2
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/SmallerThanOneCell"
argument_list|,
name|cellSize
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileEqualsWithOneCell ()
specifier|public
name|void
name|testFileEqualsWithOneCell
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/EqualsWithOneCell"
argument_list|,
name|cellSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileSmallerThanOneStripe1 ()
specifier|public
name|void
name|testFileSmallerThanOneStripe1
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/SmallerThanOneStripe"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileSmallerThanOneStripe2 ()
specifier|public
name|void
name|testFileSmallerThanOneStripe2
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/SmallerThanOneStripe"
argument_list|,
name|cellSize
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileEqualsWithOneStripe ()
specifier|public
name|void
name|testFileEqualsWithOneStripe
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/EqualsWithOneStripe"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileMoreThanOneStripe1 ()
specifier|public
name|void
name|testFileMoreThanOneStripe1
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/MoreThanOneStripe1"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileMoreThanOneStripe2 ()
specifier|public
name|void
name|testFileMoreThanOneStripe2
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/MoreThanOneStripe2"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|*
name|dataBlocks
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLessThanFullBlockGroup ()
specifier|public
name|void
name|testLessThanFullBlockGroup
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/LessThanFullBlockGroup"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|*
operator|(
name|stripesPerBlock
operator|-
literal|1
operator|)
operator|+
name|cellSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileFullBlockGroup ()
specifier|public
name|void
name|testFileFullBlockGroup
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/FullBlockGroup"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileMoreThanABlockGroup1 ()
specifier|public
name|void
name|testFileMoreThanABlockGroup1
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/MoreThanABlockGroup1"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileMoreThanABlockGroup2 ()
specifier|public
name|void
name|testFileMoreThanABlockGroup2
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/MoreThanABlockGroup2"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileMoreThanABlockGroup3 ()
specifier|public
name|void
name|testFileMoreThanABlockGroup3
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/MoreThanABlockGroup3"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
operator|*
literal|3
operator|+
name|cellSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
DECL|method|generateBytes (int cnt)
specifier|private
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
specifier|private
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
do|do
block|{
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
expr_stmt|;
if|if
condition|(
name|ret
operator|>
literal|0
condition|)
block|{
name|readLen
operator|+=
name|ret
expr_stmt|;
block|}
block|}
do|while
condition|(
name|ret
operator|>=
literal|0
operator|&&
name|readLen
operator|<
name|buf
operator|.
name|length
condition|)
do|;
return|return
name|readLen
return|;
block|}
DECL|method|getByte (long pos)
specifier|private
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
DECL|method|assertSeekAndRead (FSDataInputStream fsdis, int pos, int writeBytes)
specifier|private
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
DECL|method|testOneFileUsingDFSStripedInputStream (String src, int writeBytes)
specifier|private
name|void
name|testOneFileUsingDFSStripedInputStream
parameter_list|(
name|String
name|src
parameter_list|,
name|int
name|writeBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|testPath
init|=
operator|new
name|Path
argument_list|(
name|src
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
name|generateBytes
argument_list|(
name|writeBytes
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|testPath
argument_list|,
operator|new
name|String
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
comment|//check file length
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|testPath
argument_list|)
decl_stmt|;
name|long
name|fileLength
init|=
name|status
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"File length should be the same"
argument_list|,
name|writeBytes
argument_list|,
name|fileLength
argument_list|)
expr_stmt|;
comment|// pread
try|try
init|(
name|FSDataInputStream
name|fsdis
init|=
name|fs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|src
argument_list|)
argument_list|)
init|)
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|writeBytes
operator|+
literal|100
index|]
decl_stmt|;
name|int
name|readLen
init|=
name|fsdis
operator|.
name|read
argument_list|(
literal|0
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
decl_stmt|;
name|readLen
operator|=
name|readLen
operator|>=
literal|0
condition|?
name|readLen
else|:
literal|0
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The length of file should be the same to write size"
argument_list|,
name|writeBytes
argument_list|,
name|readLen
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
name|writeBytes
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
name|getByte
argument_list|(
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
comment|// stateful read with byte array
try|try
init|(
name|FSDataInputStream
name|fsdis
init|=
name|fs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|src
argument_list|)
argument_list|)
init|)
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|writeBytes
operator|+
literal|100
index|]
decl_stmt|;
name|int
name|readLen
init|=
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
literal|"The length of file should be the same to write size"
argument_list|,
name|writeBytes
argument_list|,
name|readLen
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
name|writeBytes
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
name|getByte
argument_list|(
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
comment|// seek and stateful read
try|try
init|(
name|FSDataInputStream
name|fsdis
init|=
name|fs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|src
argument_list|)
argument_list|)
init|)
block|{
comment|// seek to 1/2 of content
name|int
name|pos
init|=
name|writeBytes
operator|/
literal|2
decl_stmt|;
name|assertSeekAndRead
argument_list|(
name|fsdis
argument_list|,
name|pos
argument_list|,
name|writeBytes
argument_list|)
expr_stmt|;
comment|// seek to 1/3 of content
name|pos
operator|=
name|writeBytes
operator|/
literal|3
expr_stmt|;
name|assertSeekAndRead
argument_list|(
name|fsdis
argument_list|,
name|pos
argument_list|,
name|writeBytes
argument_list|)
expr_stmt|;
comment|// seek to 0 pos
name|pos
operator|=
literal|0
expr_stmt|;
name|assertSeekAndRead
argument_list|(
name|fsdis
argument_list|,
name|pos
argument_list|,
name|writeBytes
argument_list|)
expr_stmt|;
if|if
condition|(
name|writeBytes
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
name|fsdis
argument_list|,
name|pos
argument_list|,
name|writeBytes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|writeBytes
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
name|fsdis
argument_list|,
name|pos
argument_list|,
name|writeBytes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|writeBytes
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
name|fsdis
argument_list|,
name|pos
argument_list|,
name|writeBytes
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|fsdis
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
name|fsdis
operator|.
name|seek
argument_list|(
name|writeBytes
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
comment|// stateful read with ByteBuffer
try|try
init|(
name|FSDataInputStream
name|fsdis
init|=
name|fs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|src
argument_list|)
argument_list|)
init|)
block|{
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|writeBytes
operator|+
literal|100
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
do|do
block|{
name|ret
operator|=
name|fsdis
operator|.
name|read
argument_list|(
name|buf
argument_list|)
expr_stmt|;
if|if
condition|(
name|ret
operator|>
literal|0
condition|)
block|{
name|readLen
operator|+=
name|ret
expr_stmt|;
block|}
block|}
do|while
condition|(
name|ret
operator|>=
literal|0
condition|)
do|;
name|readLen
operator|=
name|readLen
operator|>=
literal|0
condition|?
name|readLen
else|:
literal|0
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The length of file should be the same to write size"
argument_list|,
name|writeBytes
argument_list|,
name|readLen
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
name|writeBytes
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
name|getByte
argument_list|(
name|i
argument_list|)
argument_list|,
name|buf
operator|.
name|array
argument_list|()
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// stateful read with 1KB size byte array
try|try
init|(
name|FSDataInputStream
name|fsdis
init|=
name|fs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|src
argument_list|)
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
name|writeBytes
index|]
decl_stmt|;
specifier|final
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1024
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
do|do
block|{
name|ret
operator|=
name|fsdis
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
expr_stmt|;
if|if
condition|(
name|ret
operator|>
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
block|}
do|while
condition|(
name|ret
operator|>=
literal|0
condition|)
do|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The length of file should be the same to write size"
argument_list|,
name|writeBytes
argument_list|,
name|readLen
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|bytes
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
comment|// stateful read using ByteBuffer with 1KB size
try|try
init|(
name|FSDataInputStream
name|fsdis
init|=
name|fs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|src
argument_list|)
argument_list|)
init|)
block|{
specifier|final
name|ByteBuffer
name|result
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|writeBytes
argument_list|)
decl_stmt|;
specifier|final
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|1024
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
do|do
block|{
name|ret
operator|=
name|fsdis
operator|.
name|read
argument_list|(
name|buf
argument_list|)
expr_stmt|;
if|if
condition|(
name|ret
operator|>
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
block|}
do|while
condition|(
name|ret
operator|>=
literal|0
condition|)
do|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The length of file should be the same to write size"
argument_list|,
name|writeBytes
argument_list|,
name|readLen
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|bytes
argument_list|,
name|result
operator|.
name|array
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWritePreadWithDNFailure ()
specifier|public
name|void
name|testWritePreadWithDNFailure
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|failedDNIdx
init|=
literal|2
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|cellSize
operator|*
operator|(
name|dataBlocks
operator|+
literal|2
operator|)
decl_stmt|;
name|Path
name|testPath
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
name|generateBytes
argument_list|(
name|length
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|testPath
argument_list|,
operator|new
name|String
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
comment|// shut down the DN that holds the last internal data block
name|BlockLocation
index|[]
name|locs
init|=
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|testPath
argument_list|,
name|cellSize
operator|*
literal|5
argument_list|,
name|cellSize
argument_list|)
decl_stmt|;
name|String
name|name
init|=
operator|(
name|locs
index|[
literal|0
index|]
operator|.
name|getNames
argument_list|()
operator|)
index|[
name|failedDNIdx
index|]
decl_stmt|;
for|for
control|(
name|DataNode
name|dn
range|:
name|cluster
operator|.
name|getDataNodes
argument_list|()
control|)
block|{
name|int
name|port
init|=
name|dn
operator|.
name|getXferPort
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|contains
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|port
argument_list|)
argument_list|)
condition|)
block|{
name|dn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
comment|// pread
name|int
name|startOffsetInFile
init|=
name|cellSize
operator|*
literal|5
decl_stmt|;
try|try
init|(
name|FSDataInputStream
name|fsdis
init|=
name|fs
operator|.
name|open
argument_list|(
name|testPath
argument_list|)
init|)
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|int
name|readLen
init|=
name|fsdis
operator|.
name|read
argument_list|(
name|startOffsetInFile
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"The length of file should be the same to write size"
argument_list|,
name|length
operator|-
name|startOffsetInFile
argument_list|,
name|readLen
argument_list|)
expr_stmt|;
name|RSRawDecoder
name|rsRawDecoder
init|=
operator|new
name|RSRawDecoder
argument_list|()
decl_stmt|;
name|rsRawDecoder
operator|.
name|initialize
argument_list|(
name|dataBlocks
argument_list|,
name|parityBlocks
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|byte
index|[]
name|expected
init|=
operator|new
name|byte
index|[
name|readLen
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|startOffsetInFile
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|//TODO: workaround (filling fixed bytes), to remove after HADOOP-11938
if|if
condition|(
operator|(
name|i
operator|/
name|cellSize
operator|)
operator|%
name|dataBlocks
operator|==
name|failedDNIdx
condition|)
block|{
name|expected
index|[
name|i
operator|-
name|startOffsetInFile
index|]
operator|=
operator|(
name|byte
operator|)
literal|7
expr_stmt|;
block|}
else|else
block|{
name|expected
index|[
name|i
operator|-
name|startOffsetInFile
index|]
operator|=
name|getByte
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
name|startOffsetInFile
init|;
name|i
operator|<
name|length
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
name|expected
index|[
name|i
operator|-
name|startOffsetInFile
index|]
argument_list|,
name|buf
index|[
name|i
operator|-
name|startOffsetInFile
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

