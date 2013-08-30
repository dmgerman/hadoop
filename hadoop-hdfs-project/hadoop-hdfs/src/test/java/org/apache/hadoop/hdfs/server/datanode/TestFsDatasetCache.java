begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
package|package
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
name|mockito
operator|.
name|Matchers
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doReturn
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|channels
operator|.
name|FileChannel
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
name|HdfsBlockLocation
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
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
name|DFSConfigKeys
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
name|DFSTestUtil
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
name|HdfsConfiguration
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
name|LogVerificationAppender
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
name|MiniDFSCluster
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
name|protocolPB
operator|.
name|DatanodeProtocolClientSideTranslatorPB
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
name|fsdataset
operator|.
name|FsDatasetSpi
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
name|FSImage
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
name|NameNode
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
name|protocol
operator|.
name|BlockCommand
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
name|protocol
operator|.
name|CacheReport
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
name|protocol
operator|.
name|DatanodeCommand
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
name|protocol
operator|.
name|DatanodeProtocol
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
name|protocol
operator|.
name|DatanodeRegistration
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
name|protocol
operator|.
name|HeartbeatResponse
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
name|protocol
operator|.
name|NNHAStatusHeartbeat
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
name|protocol
operator|.
name|StorageReport
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
name|Logger
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

begin_class
DECL|class|TestFsDatasetCache
specifier|public
class|class
name|TestFsDatasetCache
block|{
comment|// Most Linux installs allow a default of 64KB locked memory
DECL|field|CACHE_CAPACITY
specifier|private
specifier|static
specifier|final
name|long
name|CACHE_CAPACITY
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|long
name|BLOCK_SIZE
init|=
literal|4096
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|fs
specifier|private
specifier|static
name|FileSystem
name|fs
decl_stmt|;
DECL|field|nn
specifier|private
specifier|static
name|NameNode
name|nn
decl_stmt|;
DECL|field|fsImage
specifier|private
specifier|static
name|FSImage
name|fsImage
decl_stmt|;
DECL|field|dn
specifier|private
specifier|static
name|DataNode
name|dn
decl_stmt|;
DECL|field|fsd
specifier|private
specifier|static
name|FsDatasetSpi
argument_list|<
name|?
argument_list|>
name|fsd
decl_stmt|;
DECL|field|spyNN
specifier|private
specifier|static
name|DatanodeProtocolClientSideTranslatorPB
name|spyNN
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
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
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MAX_LOCKED_MEMORY_KEY
argument_list|,
name|CACHE_CAPACITY
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1
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
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|nn
operator|=
name|cluster
operator|.
name|getNameNode
argument_list|()
expr_stmt|;
name|fsImage
operator|=
name|nn
operator|.
name|getFSImage
argument_list|()
expr_stmt|;
name|dn
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fsd
operator|=
name|dn
operator|.
name|getFSDataset
argument_list|()
expr_stmt|;
name|spyNN
operator|=
name|DataNodeTestUtils
operator|.
name|spyOnBposToNN
argument_list|(
name|dn
argument_list|,
name|nn
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
name|Exception
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
DECL|method|setHeartbeatResponse (DatanodeCommand[] cmds)
specifier|private
specifier|static
name|void
name|setHeartbeatResponse
parameter_list|(
name|DatanodeCommand
index|[]
name|cmds
parameter_list|)
throws|throws
name|IOException
block|{
name|HeartbeatResponse
name|response
init|=
operator|new
name|HeartbeatResponse
argument_list|(
name|cmds
argument_list|,
operator|new
name|NNHAStatusHeartbeat
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
name|fsImage
operator|.
name|getLastAppliedOrWrittenTxId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|response
argument_list|)
operator|.
name|when
argument_list|(
name|spyNN
argument_list|)
operator|.
name|sendHeartbeat
argument_list|(
operator|(
name|DatanodeRegistration
operator|)
name|any
argument_list|()
argument_list|,
operator|(
name|StorageReport
index|[]
operator|)
name|any
argument_list|()
argument_list|,
operator|(
name|CacheReport
index|[]
operator|)
name|any
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|cacheBlock (HdfsBlockLocation loc)
specifier|private
specifier|static
name|DatanodeCommand
index|[]
name|cacheBlock
parameter_list|(
name|HdfsBlockLocation
name|loc
parameter_list|)
block|{
return|return
name|cacheBlocks
argument_list|(
operator|new
name|HdfsBlockLocation
index|[]
block|{
name|loc
block|}
argument_list|)
return|;
block|}
DECL|method|cacheBlocks (HdfsBlockLocation[] locs)
specifier|private
specifier|static
name|DatanodeCommand
index|[]
name|cacheBlocks
parameter_list|(
name|HdfsBlockLocation
index|[]
name|locs
parameter_list|)
block|{
return|return
operator|new
name|DatanodeCommand
index|[]
block|{
name|getResponse
argument_list|(
name|locs
argument_list|,
name|DatanodeProtocol
operator|.
name|DNA_CACHE
argument_list|)
block|}
return|;
block|}
DECL|method|uncacheBlock (HdfsBlockLocation loc)
specifier|private
specifier|static
name|DatanodeCommand
index|[]
name|uncacheBlock
parameter_list|(
name|HdfsBlockLocation
name|loc
parameter_list|)
block|{
return|return
name|uncacheBlocks
argument_list|(
operator|new
name|HdfsBlockLocation
index|[]
block|{
name|loc
block|}
argument_list|)
return|;
block|}
DECL|method|uncacheBlocks (HdfsBlockLocation[] locs)
specifier|private
specifier|static
name|DatanodeCommand
index|[]
name|uncacheBlocks
parameter_list|(
name|HdfsBlockLocation
index|[]
name|locs
parameter_list|)
block|{
return|return
operator|new
name|DatanodeCommand
index|[]
block|{
name|getResponse
argument_list|(
name|locs
argument_list|,
name|DatanodeProtocol
operator|.
name|DNA_UNCACHE
argument_list|)
block|}
return|;
block|}
comment|/**    * Creates a cache or uncache DatanodeCommand from an array of locations    */
DECL|method|getResponse (HdfsBlockLocation[] locs, int action)
specifier|private
specifier|static
name|DatanodeCommand
name|getResponse
parameter_list|(
name|HdfsBlockLocation
index|[]
name|locs
parameter_list|,
name|int
name|action
parameter_list|)
block|{
name|String
name|bpid
init|=
name|locs
index|[
literal|0
index|]
operator|.
name|getLocatedBlock
argument_list|()
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|Block
index|[]
name|blocks
init|=
operator|new
name|Block
index|[
name|locs
operator|.
name|length
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
name|locs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|blocks
index|[
name|i
index|]
operator|=
name|locs
index|[
name|i
index|]
operator|.
name|getLocatedBlock
argument_list|()
operator|.
name|getBlock
argument_list|()
operator|.
name|getLocalBlock
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|BlockCommand
argument_list|(
name|action
argument_list|,
name|bpid
argument_list|,
name|blocks
argument_list|)
return|;
block|}
DECL|method|getBlockSizes (HdfsBlockLocation[] locs)
specifier|private
specifier|static
name|long
index|[]
name|getBlockSizes
parameter_list|(
name|HdfsBlockLocation
index|[]
name|locs
parameter_list|)
throws|throws
name|Exception
block|{
name|long
index|[]
name|sizes
init|=
operator|new
name|long
index|[
name|locs
operator|.
name|length
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
name|locs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|HdfsBlockLocation
name|loc
init|=
name|locs
index|[
name|i
index|]
decl_stmt|;
name|String
name|bpid
init|=
name|loc
operator|.
name|getLocatedBlock
argument_list|()
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|Block
name|block
init|=
name|loc
operator|.
name|getLocatedBlock
argument_list|()
operator|.
name|getBlock
argument_list|()
operator|.
name|getLocalBlock
argument_list|()
decl_stmt|;
name|ExtendedBlock
name|extBlock
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|bpid
argument_list|,
name|block
argument_list|)
decl_stmt|;
name|FileChannel
name|blockChannel
init|=
operator|(
operator|(
name|FileInputStream
operator|)
name|fsd
operator|.
name|getBlockInputStream
argument_list|(
name|extBlock
argument_list|,
literal|0
argument_list|)
operator|)
operator|.
name|getChannel
argument_list|()
decl_stmt|;
name|sizes
index|[
name|i
index|]
operator|=
name|blockChannel
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|sizes
return|;
block|}
comment|/**    * Blocks until cache usage changes from the current value, then verifies    * against the expected new value.    */
DECL|method|verifyExpectedCacheUsage (final long current, final long expected)
specifier|private
name|long
name|verifyExpectedCacheUsage
parameter_list|(
specifier|final
name|long
name|current
parameter_list|,
specifier|final
name|long
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|cacheUsed
init|=
name|fsd
operator|.
name|getCacheUsed
argument_list|()
decl_stmt|;
while|while
condition|(
name|cacheUsed
operator|==
name|current
condition|)
block|{
name|cacheUsed
operator|=
name|fsd
operator|.
name|getCacheUsed
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|long
name|cacheCapacity
init|=
name|fsd
operator|.
name|getCacheCapacity
argument_list|()
decl_stmt|;
name|long
name|cacheRemaining
init|=
name|fsd
operator|.
name|getCacheRemaining
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Sum of used and remaining cache does not equal total"
argument_list|,
name|cacheCapacity
argument_list|,
name|cacheUsed
operator|+
name|cacheRemaining
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected amount of cache used"
argument_list|,
name|expected
argument_list|,
name|cacheUsed
argument_list|)
expr_stmt|;
return|return
name|cacheUsed
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testCacheAndUncacheBlock ()
specifier|public
name|void
name|testCacheAndUncacheBlock
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|NUM_BLOCKS
init|=
literal|5
decl_stmt|;
comment|// Write a test file
specifier|final
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
literal|"/testCacheBlock"
argument_list|)
decl_stmt|;
specifier|final
name|long
name|testFileLen
init|=
name|BLOCK_SIZE
operator|*
name|NUM_BLOCKS
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|testFile
argument_list|,
name|testFileLen
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xABBAl
argument_list|)
expr_stmt|;
comment|// Get the details of the written file
name|HdfsBlockLocation
index|[]
name|locs
init|=
operator|(
name|HdfsBlockLocation
index|[]
operator|)
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|testFile
argument_list|,
literal|0
argument_list|,
name|testFileLen
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected number of blocks"
argument_list|,
name|NUM_BLOCKS
argument_list|,
name|locs
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|long
index|[]
name|blockSizes
init|=
name|getBlockSizes
argument_list|(
name|locs
argument_list|)
decl_stmt|;
comment|// Check initial state
specifier|final
name|long
name|cacheCapacity
init|=
name|fsd
operator|.
name|getCacheCapacity
argument_list|()
decl_stmt|;
name|long
name|cacheUsed
init|=
name|fsd
operator|.
name|getCacheUsed
argument_list|()
decl_stmt|;
name|long
name|current
init|=
literal|0
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected cache capacity"
argument_list|,
name|CACHE_CAPACITY
argument_list|,
name|cacheCapacity
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected amount of cache used"
argument_list|,
name|current
argument_list|,
name|cacheUsed
argument_list|)
expr_stmt|;
comment|// Cache each block in succession, checking each time
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_BLOCKS
condition|;
name|i
operator|++
control|)
block|{
name|setHeartbeatResponse
argument_list|(
name|cacheBlock
argument_list|(
name|locs
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|current
operator|=
name|verifyExpectedCacheUsage
argument_list|(
name|current
argument_list|,
name|current
operator|+
name|blockSizes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Uncache each block in succession, again checking each time
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_BLOCKS
condition|;
name|i
operator|++
control|)
block|{
name|setHeartbeatResponse
argument_list|(
name|uncacheBlock
argument_list|(
name|locs
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|current
operator|=
name|verifyExpectedCacheUsage
argument_list|(
name|current
argument_list|,
name|current
operator|-
name|blockSizes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testFilesExceedMaxLockedMemory ()
specifier|public
name|void
name|testFilesExceedMaxLockedMemory
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create some test files that will exceed total cache capacity
comment|// Don't forget that meta files take up space too!
specifier|final
name|int
name|numFiles
init|=
literal|4
decl_stmt|;
specifier|final
name|long
name|fileSize
init|=
name|CACHE_CAPACITY
operator|/
name|numFiles
decl_stmt|;
specifier|final
name|Path
index|[]
name|testFiles
init|=
operator|new
name|Path
index|[
literal|4
index|]
decl_stmt|;
specifier|final
name|HdfsBlockLocation
index|[]
index|[]
name|fileLocs
init|=
operator|new
name|HdfsBlockLocation
index|[
name|numFiles
index|]
index|[]
decl_stmt|;
specifier|final
name|long
index|[]
name|fileSizes
init|=
operator|new
name|long
index|[
name|numFiles
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
name|numFiles
condition|;
name|i
operator|++
control|)
block|{
name|testFiles
index|[
name|i
index|]
operator|=
operator|new
name|Path
argument_list|(
literal|"/testFilesExceedMaxLockedMemory-"
operator|+
name|i
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|testFiles
index|[
name|i
index|]
argument_list|,
name|fileSize
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xDFAl
argument_list|)
expr_stmt|;
name|fileLocs
index|[
name|i
index|]
operator|=
operator|(
name|HdfsBlockLocation
index|[]
operator|)
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|testFiles
index|[
name|i
index|]
argument_list|,
literal|0
argument_list|,
name|fileSize
argument_list|)
expr_stmt|;
comment|// Get the file size (sum of blocks)
name|long
index|[]
name|sizes
init|=
name|getBlockSizes
argument_list|(
name|fileLocs
index|[
name|i
index|]
argument_list|)
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
name|sizes
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|fileSizes
index|[
name|i
index|]
operator|+=
name|sizes
index|[
name|j
index|]
expr_stmt|;
block|}
block|}
comment|// Cache the first n-1 files
name|long
name|current
init|=
literal|0
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
name|numFiles
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|setHeartbeatResponse
argument_list|(
name|cacheBlocks
argument_list|(
name|fileLocs
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|current
operator|=
name|verifyExpectedCacheUsage
argument_list|(
name|current
argument_list|,
name|current
operator|+
name|fileSizes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
name|oldCurrent
init|=
name|current
decl_stmt|;
comment|// nth file should hit a capacity exception
specifier|final
name|LogVerificationAppender
name|appender
init|=
operator|new
name|LogVerificationAppender
argument_list|()
decl_stmt|;
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getRootLogger
argument_list|()
decl_stmt|;
name|logger
operator|.
name|addAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
name|setHeartbeatResponse
argument_list|(
name|cacheBlocks
argument_list|(
name|fileLocs
index|[
name|numFiles
operator|-
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|lines
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|lines
operator|==
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|lines
operator|=
name|appender
operator|.
name|countLinesWithMessage
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MAX_LOCKED_MEMORY_KEY
operator|+
literal|" exceeded"
argument_list|)
expr_stmt|;
block|}
comment|// Uncache the cached part of the nth file
name|setHeartbeatResponse
argument_list|(
name|uncacheBlocks
argument_list|(
name|fileLocs
index|[
name|numFiles
operator|-
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
name|fsd
operator|.
name|getCacheUsed
argument_list|()
operator|!=
name|oldCurrent
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
comment|// Uncache the n-1 files
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numFiles
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|setHeartbeatResponse
argument_list|(
name|uncacheBlocks
argument_list|(
name|fileLocs
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|current
operator|=
name|verifyExpectedCacheUsage
argument_list|(
name|current
argument_list|,
name|current
operator|-
name|fileSizes
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

