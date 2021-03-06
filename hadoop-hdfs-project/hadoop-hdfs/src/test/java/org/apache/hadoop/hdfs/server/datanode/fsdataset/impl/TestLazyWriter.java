begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset.impl
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
operator|.
name|fsdataset
operator|.
name|impl
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
name|test
operator|.
name|GenericTestUtils
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
name|Test
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
name|Collections
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|StorageType
operator|.
name|DEFAULT
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
name|StorageType
operator|.
name|RAM_DISK
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
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
name|assertThat
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

begin_class
DECL|class|TestLazyWriter
specifier|public
class|class
name|TestLazyWriter
extends|extends
name|LazyPersistTestCase
block|{
annotation|@
name|Test
DECL|method|testLazyPersistBlocksAreSaved ()
specifier|public
name|void
name|testLazyPersistBlocksAreSaved
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
block|{
name|getClusterBuilder
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
specifier|final
name|int
name|NUM_BLOCKS
init|=
literal|10
decl_stmt|;
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
comment|// Create a test file
name|makeTestFile
argument_list|(
name|path
argument_list|,
name|BLOCK_SIZE
operator|*
name|NUM_BLOCKS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LocatedBlocks
name|locatedBlocks
init|=
name|ensureFileReplicasOnStorageType
argument_list|(
name|path
argument_list|,
name|RAM_DISK
argument_list|)
decl_stmt|;
name|waitForMetric
argument_list|(
literal|"RamDiskBlocksLazyPersisted"
argument_list|,
name|NUM_BLOCKS
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Verifying copy was saved to lazyPersist/"
argument_list|)
expr_stmt|;
comment|// Make sure that there is a saved copy of the replica on persistent
comment|// storage.
name|ensureLazyPersistBlocksAreSaved
argument_list|(
name|locatedBlocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSynchronousEviction ()
specifier|public
name|void
name|testSynchronousEviction
parameter_list|()
throws|throws
name|Exception
block|{
name|getClusterBuilder
argument_list|()
operator|.
name|setMaxLockedMemory
argument_list|(
name|BLOCK_SIZE
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|path1
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".01.dat"
argument_list|)
decl_stmt|;
name|makeTestFile
argument_list|(
name|path1
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ensureFileReplicasOnStorageType
argument_list|(
name|path1
argument_list|,
name|RAM_DISK
argument_list|)
expr_stmt|;
comment|// Wait until the replica is written to persistent storage.
name|waitForMetric
argument_list|(
literal|"RamDiskBlocksLazyPersisted"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Ensure that writing a new file to RAM DISK evicts the block
comment|// for the previous one.
name|Path
name|path2
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".02.dat"
argument_list|)
decl_stmt|;
name|makeTestFile
argument_list|(
name|path2
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|waitForMetric
argument_list|(
literal|"RamDiskBlocksEvicted"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|verifyRamDiskJMXMetric
argument_list|(
literal|"RamDiskBlocksEvicted"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|verifyRamDiskJMXMetric
argument_list|(
literal|"RamDiskBlocksEvictedWithoutRead"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * RamDisk eviction should not happen on blocks that are not yet    * persisted on disk.    * @throws IOException    * @throws InterruptedException    */
annotation|@
name|Test
DECL|method|testRamDiskEvictionBeforePersist ()
specifier|public
name|void
name|testRamDiskEvictionBeforePersist
parameter_list|()
throws|throws
name|Exception
block|{
name|getClusterBuilder
argument_list|()
operator|.
name|setMaxLockedMemory
argument_list|(
name|BLOCK_SIZE
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|Path
name|path1
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".01.dat"
argument_list|)
decl_stmt|;
name|Path
name|path2
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".02.dat"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|SEED
init|=
literal|0XFADED
decl_stmt|;
comment|// Stop lazy writer to ensure block for path1 is not persisted to disk.
name|FsDatasetTestUtil
operator|.
name|stopLazyWriter
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|makeRandomTestFile
argument_list|(
name|path1
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|true
argument_list|,
name|SEED
argument_list|)
expr_stmt|;
name|ensureFileReplicasOnStorageType
argument_list|(
name|path1
argument_list|,
name|RAM_DISK
argument_list|)
expr_stmt|;
comment|// Create second file with a replica on RAM_DISK.
name|makeTestFile
argument_list|(
name|path2
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Eviction should not happen for block of the first file that is not
comment|// persisted yet.
name|verifyRamDiskJMXMetric
argument_list|(
literal|"RamDiskBlocksEvicted"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|ensureFileReplicasOnStorageType
argument_list|(
name|path1
argument_list|,
name|RAM_DISK
argument_list|)
expr_stmt|;
name|ensureFileReplicasOnStorageType
argument_list|(
name|path2
argument_list|,
name|DEFAULT
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|fs
operator|.
name|exists
argument_list|(
name|path1
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|fs
operator|.
name|exists
argument_list|(
name|path2
argument_list|)
operator|)
assert|;
name|assertTrue
argument_list|(
name|verifyReadRandomFile
argument_list|(
name|path1
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|SEED
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validates lazy persisted blocks are evicted from RAM_DISK based on LRU.    * @throws IOException    * @throws InterruptedException    */
annotation|@
name|Test
DECL|method|testRamDiskEvictionIsLru ()
specifier|public
name|void
name|testRamDiskEvictionIsLru
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|NUM_PATHS
init|=
literal|5
decl_stmt|;
name|getClusterBuilder
argument_list|()
operator|.
name|setMaxLockedMemory
argument_list|(
name|NUM_PATHS
operator|*
name|BLOCK_SIZE
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|Path
name|paths
index|[]
init|=
operator|new
name|Path
index|[
name|NUM_PATHS
operator|*
literal|2
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
name|paths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|paths
index|[
name|i
index|]
operator|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|"."
operator|+
name|i
operator|+
literal|".dat"
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
name|NUM_PATHS
condition|;
name|i
operator|++
control|)
block|{
name|makeTestFile
argument_list|(
name|paths
index|[
name|i
index|]
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|waitForMetric
argument_list|(
literal|"RamDiskBlocksLazyPersisted"
argument_list|,
name|NUM_PATHS
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
name|NUM_PATHS
condition|;
operator|++
name|i
control|)
block|{
name|ensureFileReplicasOnStorageType
argument_list|(
name|paths
index|[
name|i
index|]
argument_list|,
name|RAM_DISK
argument_list|)
expr_stmt|;
block|}
comment|// Open the files for read in a random order.
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|indexes
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|NUM_PATHS
argument_list|)
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
name|NUM_PATHS
condition|;
operator|++
name|i
control|)
block|{
name|indexes
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|indexes
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
name|NUM_PATHS
condition|;
operator|++
name|i
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Touching file "
operator|+
name|paths
index|[
name|indexes
operator|.
name|get
argument_list|(
name|i
argument_list|)
index|]
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
name|paths
index|[
name|indexes
operator|.
name|get
argument_list|(
name|i
argument_list|)
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Create an equal number of new files ensuring that the previous
comment|// files are evicted in the same order they were read.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_PATHS
condition|;
operator|++
name|i
control|)
block|{
name|makeTestFile
argument_list|(
name|paths
index|[
name|i
operator|+
name|NUM_PATHS
index|]
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|triggerBlockReport
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|ensureFileReplicasOnStorageType
argument_list|(
name|paths
index|[
name|i
operator|+
name|NUM_PATHS
index|]
argument_list|,
name|RAM_DISK
argument_list|)
expr_stmt|;
name|ensureFileReplicasOnStorageType
argument_list|(
name|paths
index|[
name|indexes
operator|.
name|get
argument_list|(
name|i
argument_list|)
index|]
argument_list|,
name|DEFAULT
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
operator|+
literal|1
init|;
name|j
operator|<
name|NUM_PATHS
condition|;
operator|++
name|j
control|)
block|{
name|ensureFileReplicasOnStorageType
argument_list|(
name|paths
index|[
name|indexes
operator|.
name|get
argument_list|(
name|j
argument_list|)
index|]
argument_list|,
name|RAM_DISK
argument_list|)
expr_stmt|;
block|}
block|}
name|verifyRamDiskJMXMetric
argument_list|(
literal|"RamDiskBlocksWrite"
argument_list|,
name|NUM_PATHS
operator|*
literal|2
argument_list|)
expr_stmt|;
name|verifyRamDiskJMXMetric
argument_list|(
literal|"RamDiskBlocksWriteFallback"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|verifyRamDiskJMXMetric
argument_list|(
literal|"RamDiskBytesWrite"
argument_list|,
name|BLOCK_SIZE
operator|*
name|NUM_PATHS
operator|*
literal|2
argument_list|)
expr_stmt|;
name|verifyRamDiskJMXMetric
argument_list|(
literal|"RamDiskBlocksReadHits"
argument_list|,
name|NUM_PATHS
argument_list|)
expr_stmt|;
name|verifyRamDiskJMXMetric
argument_list|(
literal|"RamDiskBlocksEvicted"
argument_list|,
name|NUM_PATHS
argument_list|)
expr_stmt|;
name|verifyRamDiskJMXMetric
argument_list|(
literal|"RamDiskBlocksEvictedWithoutRead"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|verifyRamDiskJMXMetric
argument_list|(
literal|"RamDiskBlocksDeletedBeforeLazyPersisted"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Delete lazy-persist file that has not been persisted to disk.    * Memory is freed up and file is gone.    * @throws IOException    */
annotation|@
name|Test
DECL|method|testDeleteBeforePersist ()
specifier|public
name|void
name|testDeleteBeforePersist
parameter_list|()
throws|throws
name|Exception
block|{
name|getClusterBuilder
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|FsDatasetTestUtil
operator|.
name|stopLazyWriter
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
name|makeTestFile
argument_list|(
name|path
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LocatedBlocks
name|locatedBlocks
init|=
name|ensureFileReplicasOnStorageType
argument_list|(
name|path
argument_list|,
name|RAM_DISK
argument_list|)
decl_stmt|;
comment|// Delete before persist
name|client
operator|.
name|delete
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|verifyDeletedBlocks
argument_list|(
name|locatedBlocks
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|verifyRamDiskJMXMetric
argument_list|(
literal|"RamDiskBlocksDeletedBeforeLazyPersisted"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Delete lazy-persist file that has been persisted to disk    * Both memory blocks and disk blocks are deleted.    * @throws IOException    * @throws InterruptedException    */
annotation|@
name|Test
DECL|method|testDeleteAfterPersist ()
specifier|public
name|void
name|testDeleteAfterPersist
parameter_list|()
throws|throws
name|Exception
block|{
name|getClusterBuilder
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
name|makeTestFile
argument_list|(
name|path
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LocatedBlocks
name|locatedBlocks
init|=
name|ensureFileReplicasOnStorageType
argument_list|(
name|path
argument_list|,
name|RAM_DISK
argument_list|)
decl_stmt|;
name|waitForMetric
argument_list|(
literal|"RamDiskBlocksLazyPersisted"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Delete after persist
name|client
operator|.
name|delete
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|verifyDeletedBlocks
argument_list|(
name|locatedBlocks
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|verifyRamDiskJMXMetric
argument_list|(
literal|"RamDiskBlocksLazyPersisted"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|verifyRamDiskJMXMetric
argument_list|(
literal|"RamDiskBytesLazyPersisted"
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/**    * RAM_DISK used/free space    * @throws IOException    * @throws InterruptedException    */
annotation|@
name|Test
DECL|method|testDfsUsageCreateDelete ()
specifier|public
name|void
name|testDfsUsageCreateDelete
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
block|{
name|getClusterBuilder
argument_list|()
operator|.
name|setRamDiskReplicaCapacity
argument_list|(
literal|4
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
comment|// Get the usage before write BLOCK_SIZE
name|long
name|usedBeforeCreate
init|=
name|fs
operator|.
name|getUsed
argument_list|()
decl_stmt|;
name|makeTestFile
argument_list|(
name|path
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|long
name|usedAfterCreate
init|=
name|fs
operator|.
name|getUsed
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|usedAfterCreate
argument_list|,
name|is
argument_list|(
operator|(
name|long
operator|)
name|BLOCK_SIZE
argument_list|)
argument_list|)
expr_stmt|;
name|waitForMetric
argument_list|(
literal|"RamDiskBlocksLazyPersisted"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|long
name|usedAfterPersist
init|=
name|fs
operator|.
name|getUsed
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|usedAfterPersist
argument_list|,
name|is
argument_list|(
operator|(
name|long
operator|)
name|BLOCK_SIZE
argument_list|)
argument_list|)
expr_stmt|;
comment|// Delete after persist
name|client
operator|.
name|delete
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|long
name|usedAfterDelete
init|=
name|fs
operator|.
name|getUsed
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|usedBeforeCreate
argument_list|,
name|is
argument_list|(
name|usedAfterDelete
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

