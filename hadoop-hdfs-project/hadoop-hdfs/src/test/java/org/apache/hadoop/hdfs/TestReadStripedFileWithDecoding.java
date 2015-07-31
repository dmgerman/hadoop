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
name|Block
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
name|ExtendedBlock
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
name|protocol
operator|.
name|LocatedStripedBlock
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
name|blockmanagement
operator|.
name|BlockInfo
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
name|blockmanagement
operator|.
name|BlockManager
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
name|blockmanagement
operator|.
name|DatanodeDescriptor
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
name|server
operator|.
name|datanode
operator|.
name|DataNodeTestUtils
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
name|namenode
operator|.
name|FSNamesystem
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
name|namenode
operator|.
name|NameNodeAdapter
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
name|util
operator|.
name|StripedBlockUtil
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|parityBlocks
import|;
end_import

begin_class
DECL|class|TestReadStripedFileWithDecoding
specifier|public
class|class
name|TestReadStripedFileWithDecoding
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestReadStripedFileWithDecoding
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|smallFileLength
specifier|private
specifier|final
name|int
name|smallFileLength
init|=
name|blockSize
operator|*
name|dataBlocks
operator|-
literal|123
decl_stmt|;
DECL|field|largeFileLength
specifier|private
specifier|final
name|int
name|largeFileLength
init|=
name|blockSize
operator|*
name|dataBlocks
operator|+
literal|123
decl_stmt|;
DECL|field|fileLengths
specifier|private
specifier|final
name|int
index|[]
name|fileLengths
init|=
block|{
name|smallFileLength
block|,
name|largeFileLength
block|}
decl_stmt|;
DECL|field|dnFailureNums
specifier|private
specifier|final
name|int
index|[]
name|dnFailureNums
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|}
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
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
operator|new
name|HdfsConfiguration
argument_list|()
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
comment|/**    * Shutdown tolerable number of Datanode before reading.    * Verify the decoding works correctly.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testReadWithDNFailure ()
specifier|public
name|void
name|testReadWithDNFailure
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|fileLength
range|:
name|fileLengths
control|)
block|{
for|for
control|(
name|int
name|dnFailureNum
range|:
name|dnFailureNums
control|)
block|{
try|try
block|{
comment|// setup a new cluster with no dead datanode
name|setup
argument_list|()
expr_stmt|;
name|testReadWithDNFailure
argument_list|(
name|fileLength
argument_list|,
name|dnFailureNum
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|String
name|fileType
init|=
name|fileLength
operator|<
operator|(
name|blockSize
operator|*
name|dataBlocks
operator|)
condition|?
literal|"smallFile"
else|:
literal|"largeFile"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to read file with DN failure:"
operator|+
literal|" fileType = "
operator|+
name|fileType
operator|+
literal|", dnFailureNum = "
operator|+
name|dnFailureNum
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// tear down the cluster
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Corrupt tolerable number of block before reading.    * Verify the decoding works correctly.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testReadCorruptedData ()
specifier|public
name|void
name|testReadCorruptedData
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|fileLength
range|:
name|fileLengths
control|)
block|{
for|for
control|(
name|int
name|dataDelNum
init|=
literal|1
init|;
name|dataDelNum
operator|<
literal|4
condition|;
name|dataDelNum
operator|++
control|)
block|{
for|for
control|(
name|int
name|parityDelNum
init|=
literal|0
init|;
operator|(
name|dataDelNum
operator|+
name|parityDelNum
operator|)
operator|<
literal|4
condition|;
name|parityDelNum
operator|++
control|)
block|{
name|String
name|src
init|=
literal|"/corrupted_"
operator|+
name|dataDelNum
operator|+
literal|"_"
operator|+
name|parityDelNum
decl_stmt|;
name|testReadWithBlockCorrupted
argument_list|(
name|src
argument_list|,
name|fileLength
argument_list|,
name|dataDelNum
argument_list|,
name|parityDelNum
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Delete tolerable number of block before reading.    * Verify the decoding works correctly.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testReadCorruptedDataByDeleting ()
specifier|public
name|void
name|testReadCorruptedDataByDeleting
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|fileLength
range|:
name|fileLengths
control|)
block|{
for|for
control|(
name|int
name|dataDelNum
init|=
literal|1
init|;
name|dataDelNum
operator|<
literal|4
condition|;
name|dataDelNum
operator|++
control|)
block|{
for|for
control|(
name|int
name|parityDelNum
init|=
literal|0
init|;
operator|(
name|dataDelNum
operator|+
name|parityDelNum
operator|)
operator|<
literal|4
condition|;
name|parityDelNum
operator|++
control|)
block|{
name|String
name|src
init|=
literal|"/deleted_"
operator|+
name|dataDelNum
operator|+
literal|"_"
operator|+
name|parityDelNum
decl_stmt|;
name|testReadWithBlockCorrupted
argument_list|(
name|src
argument_list|,
name|fileLength
argument_list|,
name|dataDelNum
argument_list|,
name|parityDelNum
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|findFirstDataNode (Path file, long length)
specifier|private
name|int
name|findFirstDataNode
parameter_list|(
name|Path
name|file
parameter_list|,
name|long
name|length
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
name|file
argument_list|,
literal|0
argument_list|,
name|length
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
literal|0
index|]
decl_stmt|;
name|int
name|dnIndex
init|=
literal|0
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
return|return
name|dnIndex
return|;
block|}
name|dnIndex
operator|++
expr_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|verifyRead (Path testPath, int length, byte[] expected)
specifier|private
name|void
name|verifyRead
parameter_list|(
name|Path
name|testPath
parameter_list|,
name|int
name|length
parameter_list|,
name|byte
index|[]
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|length
operator|+
literal|100
index|]
decl_stmt|;
name|StripedFileTestUtil
operator|.
name|verifyLength
argument_list|(
name|fs
argument_list|,
name|testPath
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|verifyPread
argument_list|(
name|fs
argument_list|,
name|testPath
argument_list|,
name|length
argument_list|,
name|expected
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|verifyStatefulRead
argument_list|(
name|fs
argument_list|,
name|testPath
argument_list|,
name|length
argument_list|,
name|expected
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|verifyStatefulRead
argument_list|(
name|fs
argument_list|,
name|testPath
argument_list|,
name|length
argument_list|,
name|expected
argument_list|,
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|length
operator|+
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|verifySeek
argument_list|(
name|fs
argument_list|,
name|testPath
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testReadWithDNFailure (int fileLength, int dnFailureNum)
specifier|private
name|void
name|testReadWithDNFailure
parameter_list|(
name|int
name|fileLength
parameter_list|,
name|int
name|dnFailureNum
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fileType
init|=
name|fileLength
operator|<
operator|(
name|blockSize
operator|*
name|dataBlocks
operator|)
condition|?
literal|"smallFile"
else|:
literal|"largeFile"
decl_stmt|;
name|String
name|src
init|=
literal|"/dnFailure_"
operator|+
name|dnFailureNum
operator|+
literal|"_"
operator|+
name|fileType
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"testReadWithDNFailure: file = "
operator|+
name|src
operator|+
literal|", fileSize = "
operator|+
name|fileLength
operator|+
literal|", dnFailureNum = "
operator|+
name|dnFailureNum
argument_list|)
expr_stmt|;
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
name|StripedFileTestUtil
operator|.
name|generateBytes
argument_list|(
name|fileLength
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
name|bytes
argument_list|)
expr_stmt|;
comment|// shut down the DN that holds an internal data block
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
for|for
control|(
name|int
name|failedDnIdx
init|=
literal|0
init|;
name|failedDnIdx
operator|<
name|dnFailureNum
condition|;
name|failedDnIdx
operator|++
control|)
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
name|failedDnIdx
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
block|}
block|}
block|}
comment|// check file length, pread, stateful read and seek
name|verifyRead
argument_list|(
name|testPath
argument_list|,
name|fileLength
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
comment|/**    * After reading a corrupted block, make sure the client can correctly report    * the corruption to the NameNode.    */
annotation|@
name|Test
DECL|method|testReportBadBlock ()
specifier|public
name|void
name|testReportBadBlock
parameter_list|()
throws|throws
name|IOException
block|{
comment|// create file
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"/corrupted"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|length
init|=
literal|10
decl_stmt|;
comment|// length of "corruption"
specifier|final
name|byte
index|[]
name|bytes
init|=
name|StripedFileTestUtil
operator|.
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
name|file
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
comment|// corrupt the first data block
name|int
name|dnIndex
init|=
name|findFirstDataNode
argument_list|(
name|file
argument_list|,
name|cellSize
operator|*
name|dataBlocks
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|dnIndex
argument_list|)
expr_stmt|;
name|LocatedStripedBlock
name|slb
init|=
operator|(
name|LocatedStripedBlock
operator|)
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|cellSize
operator|*
name|dataBlocks
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|LocatedBlock
index|[]
name|blks
init|=
name|StripedBlockUtil
operator|.
name|parseStripedBlockGroup
argument_list|(
name|slb
argument_list|,
name|cellSize
argument_list|,
name|dataBlocks
argument_list|,
name|parityBlocks
argument_list|)
decl_stmt|;
comment|// find the first block file
name|File
name|storageDir
init|=
name|cluster
operator|.
name|getInstanceStorageDir
argument_list|(
name|dnIndex
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|File
name|blkFile
init|=
name|MiniDFSCluster
operator|.
name|getBlockFile
argument_list|(
name|storageDir
argument_list|,
name|blks
index|[
literal|0
index|]
operator|.
name|getBlock
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Block file does not exist"
argument_list|,
name|blkFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// corrupt the block file
name|LOG
operator|.
name|info
argument_list|(
literal|"Deliberately corrupting file "
operator|+
name|blkFile
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|blkFile
argument_list|)
init|)
block|{
name|out
operator|.
name|write
argument_list|(
literal|"corruption"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// disable the heartbeat from DN so that the corrupted block record is kept
comment|// in NameNode
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
name|DataNodeTestUtils
operator|.
name|setHeartbeatsDisabledForTests
argument_list|(
name|dn
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// do stateful read
name|StripedFileTestUtil
operator|.
name|verifyStatefulRead
argument_list|(
name|fs
argument_list|,
name|file
argument_list|,
name|length
argument_list|,
name|bytes
argument_list|,
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
comment|// check whether the corruption has been reported to the NameNode
specifier|final
name|FSNamesystem
name|ns
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
specifier|final
name|BlockManager
name|bm
init|=
name|ns
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
name|BlockInfo
name|blockInfo
init|=
operator|(
name|ns
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|getINode4Write
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asFile
argument_list|()
operator|.
name|getBlocks
argument_list|()
operator|)
index|[
literal|0
index|]
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bm
operator|.
name|getCorruptReplicas
argument_list|(
name|blockInfo
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
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
name|DataNodeTestUtils
operator|.
name|setHeartbeatsDisabledForTests
argument_list|(
name|dn
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testInvalidateBlock ()
specifier|public
name|void
name|testInvalidateBlock
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"/invalidate"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|length
init|=
literal|10
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
name|StripedFileTestUtil
operator|.
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
name|file
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|int
name|dnIndex
init|=
name|findFirstDataNode
argument_list|(
name|file
argument_list|,
name|cellSize
operator|*
name|dataBlocks
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|dnIndex
argument_list|)
expr_stmt|;
name|LocatedStripedBlock
name|slb
init|=
operator|(
name|LocatedStripedBlock
operator|)
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|cellSize
operator|*
name|dataBlocks
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|LocatedBlock
index|[]
name|blks
init|=
name|StripedBlockUtil
operator|.
name|parseStripedBlockGroup
argument_list|(
name|slb
argument_list|,
name|cellSize
argument_list|,
name|dataBlocks
argument_list|,
name|parityBlocks
argument_list|)
decl_stmt|;
specifier|final
name|Block
name|b
init|=
name|blks
index|[
literal|0
index|]
operator|.
name|getBlock
argument_list|()
operator|.
name|getLocalBlock
argument_list|()
decl_stmt|;
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|dnIndex
argument_list|)
decl_stmt|;
comment|// disable the heartbeat from DN so that the invalidated block record is kept
comment|// in NameNode until heartbeat expires and NN mark the dn as dead
name|DataNodeTestUtils
operator|.
name|setHeartbeatsDisabledForTests
argument_list|(
name|dn
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
comment|// delete the file
name|fs
operator|.
name|delete
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// check the block is added to invalidateBlocks
specifier|final
name|FSNamesystem
name|fsn
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
specifier|final
name|BlockManager
name|bm
init|=
name|fsn
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
name|DatanodeDescriptor
name|dnd
init|=
name|NameNodeAdapter
operator|.
name|getDatanode
argument_list|(
name|fsn
argument_list|,
name|dn
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|bm
operator|.
name|containsInvalidateBlock
argument_list|(
name|blks
index|[
literal|0
index|]
operator|.
name|getLocations
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|b
argument_list|)
operator|||
name|dnd
operator|.
name|containsInvalidateBlock
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|DataNodeTestUtils
operator|.
name|setHeartbeatsDisabledForTests
argument_list|(
name|dn
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test reading a file with some blocks(data blocks or parity blocks or both)    * deleted or corrupted.    * @param src file path    * @param fileLength file length    * @param dataBlkDelNum the deleted or corrupted number of data blocks.    * @param parityBlkDelNum the deleted or corrupted number of parity blocks.    * @param deleteBlockFile whether block file is deleted or corrupted.    *                        true is to delete the block file.    *                        false is to corrupt the content of the block file.    * @throws IOException    */
DECL|method|testReadWithBlockCorrupted (String src, int fileLength, int dataBlkDelNum, int parityBlkDelNum, boolean deleteBlockFile)
specifier|private
name|void
name|testReadWithBlockCorrupted
parameter_list|(
name|String
name|src
parameter_list|,
name|int
name|fileLength
parameter_list|,
name|int
name|dataBlkDelNum
parameter_list|,
name|int
name|parityBlkDelNum
parameter_list|,
name|boolean
name|deleteBlockFile
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"testReadWithBlockCorrupted: file = "
operator|+
name|src
operator|+
literal|", dataBlkDelNum = "
operator|+
name|dataBlkDelNum
operator|+
literal|", parityBlkDelNum = "
operator|+
name|parityBlkDelNum
operator|+
literal|", deleteBlockFile? "
operator|+
name|deleteBlockFile
argument_list|)
expr_stmt|;
name|int
name|recoverBlkNum
init|=
name|dataBlkDelNum
operator|+
name|parityBlkDelNum
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"dataBlkDelNum and parityBlkDelNum should be positive"
argument_list|,
name|dataBlkDelNum
operator|>=
literal|0
operator|&&
name|parityBlkDelNum
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The sum of dataBlkDelNum and parityBlkDelNum "
operator|+
literal|"should be between 1 ~ "
operator|+
name|parityBlocks
argument_list|,
name|recoverBlkNum
operator|<=
name|parityBlocks
argument_list|)
expr_stmt|;
comment|// write a file with the length of writeLen
name|Path
name|srcPath
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
name|StripedFileTestUtil
operator|.
name|generateBytes
argument_list|(
name|fileLength
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
name|bytes
argument_list|)
expr_stmt|;
comment|// delete or corrupt some blocks
name|corruptBlocks
argument_list|(
name|srcPath
argument_list|,
name|dataBlkDelNum
argument_list|,
name|parityBlkDelNum
argument_list|,
name|deleteBlockFile
argument_list|)
expr_stmt|;
comment|// check the file can be read after some blocks were deleted
name|verifyRead
argument_list|(
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
DECL|method|corruptBlocks (Path srcPath, int dataBlkDelNum, int parityBlkDelNum, boolean deleteBlockFile)
specifier|private
name|void
name|corruptBlocks
parameter_list|(
name|Path
name|srcPath
parameter_list|,
name|int
name|dataBlkDelNum
parameter_list|,
name|int
name|parityBlkDelNum
parameter_list|,
name|boolean
name|deleteBlockFile
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|recoverBlkNum
init|=
name|dataBlkDelNum
operator|+
name|parityBlkDelNum
decl_stmt|;
name|LocatedBlocks
name|locatedBlocks
init|=
name|getLocatedBlocks
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
name|LocatedStripedBlock
name|lastBlock
init|=
operator|(
name|LocatedStripedBlock
operator|)
name|locatedBlocks
operator|.
name|getLastLocatedBlock
argument_list|()
decl_stmt|;
name|int
index|[]
name|delDataBlkIndices
init|=
name|StripedFileTestUtil
operator|.
name|randomArray
argument_list|(
literal|0
argument_list|,
name|dataBlocks
argument_list|,
name|dataBlkDelNum
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|delDataBlkIndices
argument_list|)
expr_stmt|;
name|int
index|[]
name|delParityBlkIndices
init|=
name|StripedFileTestUtil
operator|.
name|randomArray
argument_list|(
name|dataBlocks
argument_list|,
name|dataBlocks
operator|+
name|parityBlocks
argument_list|,
name|parityBlkDelNum
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|delParityBlkIndices
argument_list|)
expr_stmt|;
name|int
index|[]
name|delBlkIndices
init|=
operator|new
name|int
index|[
name|recoverBlkNum
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|delDataBlkIndices
argument_list|,
literal|0
argument_list|,
name|delBlkIndices
argument_list|,
literal|0
argument_list|,
name|delDataBlkIndices
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|delParityBlkIndices
argument_list|,
literal|0
argument_list|,
name|delBlkIndices
argument_list|,
name|delDataBlkIndices
operator|.
name|length
argument_list|,
name|delParityBlkIndices
operator|.
name|length
argument_list|)
expr_stmt|;
name|ExtendedBlock
index|[]
name|delBlocks
init|=
operator|new
name|ExtendedBlock
index|[
name|recoverBlkNum
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
name|recoverBlkNum
condition|;
name|i
operator|++
control|)
block|{
name|delBlocks
index|[
name|i
index|]
operator|=
name|StripedBlockUtil
operator|.
name|constructInternalBlock
argument_list|(
name|lastBlock
operator|.
name|getBlock
argument_list|()
argument_list|,
name|cellSize
argument_list|,
name|dataBlocks
argument_list|,
name|delBlkIndices
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|deleteBlockFile
condition|)
block|{
comment|// delete the block file
name|cluster
operator|.
name|corruptBlockOnDataNodesByDeletingBlockFile
argument_list|(
name|delBlocks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// corrupt the block file
name|cluster
operator|.
name|corruptBlockOnDataNodes
argument_list|(
name|delBlocks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getLocatedBlocks (Path filePath)
specifier|private
name|LocatedBlocks
name|getLocatedBlocks
parameter_list|(
name|Path
name|filePath
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|filePath
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
block|}
end_class

end_unit

