begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
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
name|blockmanagement
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
name|Supplier
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
name|FileUtil
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
name|DistributedFileSystem
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
name|DatanodeID
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
name|datanode
operator|.
name|fsdataset
operator|.
name|FsVolumeSpi
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
name|StorageReport
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
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|assertEquals
import|;
end_import

begin_class
DECL|class|TestNameNodePrunesMissingStorages
specifier|public
class|class
name|TestNameNodePrunesMissingStorages
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
name|TestNameNodePrunesMissingStorages
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|runTest (final String testCaseName, final boolean createFiles, final int numInitialStorages, final int expectedStoragesAfterTest)
specifier|private
specifier|static
name|void
name|runTest
parameter_list|(
specifier|final
name|String
name|testCaseName
parameter_list|,
specifier|final
name|boolean
name|createFiles
parameter_list|,
specifier|final
name|int
name|numInitialStorages
parameter_list|,
specifier|final
name|int
name|expectedStoragesAfterTest
parameter_list|)
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
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
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
name|storagesPerDatanode
argument_list|(
name|numInitialStorages
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
specifier|final
name|DataNode
name|dn0
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// Ensure NN knows about the storage.
specifier|final
name|DatanodeID
name|dnId
init|=
name|dn0
operator|.
name|getDatanodeId
argument_list|()
decl_stmt|;
specifier|final
name|DatanodeDescriptor
name|dnDescriptor
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getDatanode
argument_list|(
name|dnId
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|dnDescriptor
operator|.
name|getStorageInfos
argument_list|()
operator|.
name|length
argument_list|,
name|is
argument_list|(
name|numInitialStorages
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|bpid
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
specifier|final
name|DatanodeRegistration
name|dnReg
init|=
name|dn0
operator|.
name|getDNRegistrationForBP
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
name|DataNodeTestUtils
operator|.
name|triggerBlockReport
argument_list|(
name|dn0
argument_list|)
expr_stmt|;
if|if
condition|(
name|createFiles
condition|)
block|{
specifier|final
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|,
name|testCaseName
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
argument_list|,
name|path
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0x1BAD5EED
argument_list|)
expr_stmt|;
name|DataNodeTestUtils
operator|.
name|triggerBlockReport
argument_list|(
name|dn0
argument_list|)
expr_stmt|;
block|}
comment|// Generate a fake StorageReport that is missing one storage.
specifier|final
name|StorageReport
name|reports
index|[]
init|=
name|dn0
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getStorageReports
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
specifier|final
name|StorageReport
name|prunedReports
index|[]
init|=
operator|new
name|StorageReport
index|[
name|numInitialStorages
operator|-
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|reports
argument_list|,
literal|0
argument_list|,
name|prunedReports
argument_list|,
literal|0
argument_list|,
name|prunedReports
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Stop the DataNode and send fake heartbeat with missing storage.
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|sendHeartbeat
argument_list|(
name|dnReg
argument_list|,
name|prunedReports
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Check that the missing storage was pruned.
name|assertThat
argument_list|(
name|dnDescriptor
operator|.
name|getStorageInfos
argument_list|()
operator|.
name|length
argument_list|,
name|is
argument_list|(
name|expectedStoragesAfterTest
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
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
block|}
comment|/**    * Test that the NameNode prunes empty storage volumes that are no longer    * reported by the DataNode.    * @throws IOException    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testUnusedStorageIsPruned ()
specifier|public
name|void
name|testUnusedStorageIsPruned
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Run the test with 1 storage, after the text expect 0 storages.
name|runTest
argument_list|(
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that the NameNode does not prune storages with blocks    * simply as a result of a heartbeat being sent missing that storage.    *    * @throws IOException    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testStorageWithBlocksIsNotPruned ()
specifier|public
name|void
name|testStorageWithBlocksIsNotPruned
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Run the test with 1 storage, after the text still expect 1 storage.
name|runTest
argument_list|(
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Regression test for HDFS-7960.<p/>    *    * Shutting down a datanode, removing a storage directory, and restarting    * the DataNode should not produce zombie storages.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testRemovingStorageDoesNotProduceZombies ()
specifier|public
name|void
name|testRemovingStorageDoesNotProduceZombies
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
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|int
name|NUM_STORAGES_PER_DN
init|=
literal|2
decl_stmt|;
specifier|final
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
name|storagesPerDatanode
argument_list|(
name|NUM_STORAGES_PER_DN
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
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
name|assertEquals
argument_list|(
name|NUM_STORAGES_PER_DN
argument_list|,
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getDatanode
argument_list|(
name|dn
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
operator|.
name|getStorageInfos
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|// Create a file which will end up on all 3 datanodes.
specifier|final
name|Path
name|TEST_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/foo1"
argument_list|)
decl_stmt|;
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|TEST_PATH
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|0xcafecafe
argument_list|)
expr_stmt|;
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
name|triggerBlockReport
argument_list|(
name|dn
argument_list|)
expr_stmt|;
block|}
name|ExtendedBlock
name|block
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/foo1"
argument_list|)
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|writeLock
argument_list|()
expr_stmt|;
specifier|final
name|String
name|storageIdToRemove
decl_stmt|;
name|String
name|datanodeUuid
decl_stmt|;
comment|// Find the first storage which this block is in.
try|try
block|{
name|Iterator
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|storageInfoIter
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getStorages
argument_list|(
name|block
operator|.
name|getLocalBlock
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|storageInfoIter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|DatanodeStorageInfo
name|info
init|=
name|storageInfoIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|storageIdToRemove
operator|=
name|info
operator|.
name|getStorageID
argument_list|()
expr_stmt|;
name|datanodeUuid
operator|=
name|info
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|.
name|getDatanodeUuid
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
comment|// Find the DataNode which holds that first storage.
specifier|final
name|DataNode
name|datanodeToRemoveStorageFrom
decl_stmt|;
name|int
name|datanodeToRemoveStorageFromIdx
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|datanodeToRemoveStorageFromIdx
operator|>=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"failed to find datanode with uuid "
operator|+
name|datanodeUuid
argument_list|)
expr_stmt|;
name|datanodeToRemoveStorageFrom
operator|=
literal|null
expr_stmt|;
break|break;
block|}
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
name|datanodeToRemoveStorageFromIdx
argument_list|)
decl_stmt|;
if|if
condition|(
name|dn
operator|.
name|getDatanodeUuid
argument_list|()
operator|.
name|equals
argument_list|(
name|datanodeUuid
argument_list|)
condition|)
block|{
name|datanodeToRemoveStorageFrom
operator|=
name|dn
expr_stmt|;
break|break;
block|}
name|datanodeToRemoveStorageFromIdx
operator|++
expr_stmt|;
block|}
comment|// Find the volume within the datanode which holds that first storage.
name|List
argument_list|<
name|?
extends|extends
name|FsVolumeSpi
argument_list|>
name|volumes
init|=
name|datanodeToRemoveStorageFrom
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getVolumes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|NUM_STORAGES_PER_DN
argument_list|,
name|volumes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|volumeDirectoryToRemove
init|=
literal|null
decl_stmt|;
for|for
control|(
name|FsVolumeSpi
name|volume
range|:
name|volumes
control|)
block|{
if|if
condition|(
name|volume
operator|.
name|getStorageID
argument_list|()
operator|.
name|equals
argument_list|(
name|storageIdToRemove
argument_list|)
condition|)
block|{
name|volumeDirectoryToRemove
operator|=
name|volume
operator|.
name|getBasePath
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Shut down the datanode and remove the volume.
comment|// Replace the volume directory with a regular file, which will
comment|// cause a volume failure.  (If we merely removed the directory,
comment|// it would be re-initialized with a new storage ID.)
name|assertNotNull
argument_list|(
name|volumeDirectoryToRemove
argument_list|)
expr_stmt|;
name|datanodeToRemoveStorageFrom
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|volumeDirectoryToRemove
argument_list|)
argument_list|)
expr_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|volumeDirectoryToRemove
argument_list|)
decl_stmt|;
try|try
block|{
name|fos
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|datanodeToRemoveStorageFromIdx
argument_list|)
expr_stmt|;
comment|// Wait for the NameNode to remove the storage.
name|LOG
operator|.
name|info
argument_list|(
literal|"waiting for the datanode to remove "
operator|+
name|storageIdToRemove
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
specifier|final
name|DatanodeDescriptor
name|dnDescriptor
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getDatanode
argument_list|(
name|datanodeToRemoveStorageFrom
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|dnDescriptor
argument_list|)
expr_stmt|;
name|DatanodeStorageInfo
index|[]
name|infos
init|=
name|dnDescriptor
operator|.
name|getStorageInfos
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeStorageInfo
name|info
range|:
name|infos
control|)
block|{
if|if
condition|(
name|info
operator|.
name|getStorageID
argument_list|()
operator|.
name|equals
argument_list|(
name|storageIdToRemove
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Still found storage "
operator|+
name|storageIdToRemove
operator|+
literal|" on "
operator|+
name|info
operator|+
literal|"."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
name|assertEquals
argument_list|(
name|NUM_STORAGES_PER_DN
operator|-
literal|1
argument_list|,
name|infos
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|,
literal|10
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
block|}
finally|finally
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
block|}
block|}
end_class

end_unit

