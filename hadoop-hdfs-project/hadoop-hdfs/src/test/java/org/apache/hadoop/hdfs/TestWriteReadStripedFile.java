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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
operator|.
name|WebHdfsConstants
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
name|WebHdfsTestUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|StripedFileTestUtil
operator|.
name|blockSize
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
name|StripedFileTestUtil
operator|.
name|cellSize
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
name|StripedFileTestUtil
operator|.
name|dataBlocks
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
name|StripedFileTestUtil
operator|.
name|numDNs
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
name|StripedFileTestUtil
operator|.
name|stripesPerBlock
import|;
end_import

begin_class
DECL|class|TestWriteReadStripedFile
specifier|public
class|class
name|TestWriteReadStripedFile
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
name|TestWriteReadStripedFile
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
specifier|static
name|FileSystem
name|fs
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
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
argument_list|,
name|cellSize
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/EmptyFile2"
argument_list|,
literal|0
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/SmallerThanOneCell2"
argument_list|,
literal|1
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/SmallerThanOneCell2"
argument_list|,
name|cellSize
operator|-
literal|1
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/EqualsWithOneCell2"
argument_list|,
name|cellSize
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/SmallerThanOneStripe2"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|-
literal|1
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/SmallerThanOneStripe2"
argument_list|,
name|cellSize
operator|+
literal|123
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/EqualsWithOneStripe2"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/MoreThanOneStripe12"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|+
literal|123
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/MoreThanOneStripe22"
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
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/LessThanFullBlockGroup2"
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
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/FullBlockGroup2"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/MoreThanABlockGroup12"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
operator|+
literal|123
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/MoreThanABlockGroup22"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|+
literal|123
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/MoreThanABlockGroup32"
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
argument_list|,
literal|true
argument_list|)
expr_stmt|;
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
DECL|method|testOneFileUsingDFSStripedInputStream (String src, int fileLength)
specifier|private
name|void
name|testOneFileUsingDFSStripedInputStream
parameter_list|(
name|String
name|src
parameter_list|,
name|int
name|fileLength
parameter_list|)
throws|throws
name|IOException
block|{
name|testOneFileUsingDFSStripedInputStream
argument_list|(
name|src
argument_list|,
name|fileLength
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testOneFileUsingDFSStripedInputStream (String src, int fileLength, boolean withDataNodeFailure)
specifier|private
name|void
name|testOneFileUsingDFSStripedInputStream
parameter_list|(
name|String
name|src
parameter_list|,
name|int
name|fileLength
parameter_list|,
name|boolean
name|withDataNodeFailure
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|byte
index|[]
name|expected
init|=
name|StripedFileTestUtil
operator|.
name|generateBytes
argument_list|(
name|fileLength
argument_list|)
decl_stmt|;
name|Path
name|srcPath
init|=
operator|new
name|Path
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
operator|new
name|String
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|verifyLength
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|)
expr_stmt|;
if|if
condition|(
name|withDataNodeFailure
condition|)
block|{
name|int
name|dnIndex
init|=
literal|1
decl_stmt|;
comment|// TODO: StripedFileTestUtil.random.nextInt(dataBlocks);
name|LOG
operator|.
name|info
argument_list|(
literal|"stop DataNode "
operator|+
name|dnIndex
argument_list|)
expr_stmt|;
name|stopDataNode
argument_list|(
name|srcPath
argument_list|,
name|dnIndex
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|smallBuf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|byte
index|[]
name|largeBuf
init|=
operator|new
name|byte
index|[
name|fileLength
operator|+
literal|100
index|]
decl_stmt|;
name|verifyPread
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|expected
argument_list|,
name|largeBuf
argument_list|)
expr_stmt|;
name|verifyStatefulRead
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|expected
argument_list|,
name|largeBuf
argument_list|)
expr_stmt|;
name|verifySeek
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|)
expr_stmt|;
name|verifyStatefulRead
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|expected
argument_list|,
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|fileLength
operator|+
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|verifyStatefulRead
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|expected
argument_list|,
name|smallBuf
argument_list|)
expr_stmt|;
name|verifyStatefulRead
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|expected
argument_list|,
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|stopDataNode (Path path, int failedDNIdx)
specifier|private
name|void
name|stopDataNode
parameter_list|(
name|Path
name|path
parameter_list|,
name|int
name|failedDNIdx
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockLocation
index|[]
name|locs
init|=
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|path
argument_list|,
literal|0
argument_list|,
name|cellSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|locs
operator|!=
literal|null
operator|&&
name|locs
operator|.
name|length
operator|>
literal|0
condition|)
block|{
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
block|}
block|}
annotation|@
name|Test
DECL|method|testWriteReadUsingWebHdfs ()
specifier|public
name|void
name|testWriteReadUsingWebHdfs
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|fileLength
init|=
name|blockSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|+
literal|123
decl_stmt|;
specifier|final
name|byte
index|[]
name|expected
init|=
name|StripedFileTestUtil
operator|.
name|generateBytes
argument_list|(
name|fileLength
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|WebHdfsTestUtil
operator|.
name|getWebHdfsFileSystem
argument_list|(
name|conf
argument_list|,
name|WebHdfsConstants
operator|.
name|WEBHDFS_SCHEME
argument_list|)
decl_stmt|;
name|Path
name|srcPath
init|=
operator|new
name|Path
argument_list|(
literal|"/testWriteReadUsingWebHdfs_stripe"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
operator|new
name|String
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|verifyLength
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|)
expr_stmt|;
name|byte
index|[]
name|smallBuf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|byte
index|[]
name|largeBuf
init|=
operator|new
name|byte
index|[
name|fileLength
operator|+
literal|100
index|]
decl_stmt|;
name|verifyPread
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|expected
argument_list|,
name|largeBuf
argument_list|)
expr_stmt|;
name|verifyStatefulRead
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|expected
argument_list|,
name|largeBuf
argument_list|)
expr_stmt|;
name|verifySeek
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|)
expr_stmt|;
name|verifyStatefulRead
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|expected
argument_list|,
name|smallBuf
argument_list|)
expr_stmt|;
comment|//webhdfs doesn't support bytebuffer read
block|}
DECL|method|verifyLength (FileSystem fs, Path srcPath, int fileLength)
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
name|in
operator|.
name|readFully
argument_list|(
name|startOffset
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|remaining
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
name|remaining
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
literal|" should be the "
operator|+
literal|"same"
argument_list|,
name|expected
index|[
name|startOffset
operator|+
name|i
index|]
argument_list|,
name|buf
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
block|}
end_class

end_unit

