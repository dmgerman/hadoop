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
name|java
operator|.
name|util
operator|.
name|UUID
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|ExportedBlockKeys
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
name|common
operator|.
name|GenerationStamp
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
name|common
operator|.
name|HdfsServerConstants
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
name|common
operator|.
name|StorageInfo
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
name|util
operator|.
name|VersionInfo
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|internal
operator|.
name|util
operator|.
name|reflection
operator|.
name|Whitebox
import|;
end_import

begin_comment
comment|/**  * Test if FSNamesystem handles heartbeat right  */
end_comment

begin_class
DECL|class|TestComputeInvalidateWork
specifier|public
class|class
name|TestComputeInvalidateWork
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|NUM_OF_DATANODES
specifier|private
specifier|final
name|int
name|NUM_OF_DATANODES
init|=
literal|3
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|namesystem
specifier|private
name|FSNamesystem
name|namesystem
decl_stmt|;
DECL|field|bm
specifier|private
name|BlockManager
name|bm
decl_stmt|;
DECL|field|nodes
specifier|private
name|DatanodeDescriptor
index|[]
name|nodes
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
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
name|NUM_OF_DATANODES
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
name|namesystem
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
name|bm
operator|=
name|namesystem
operator|.
name|getBlockManager
argument_list|()
expr_stmt|;
name|nodes
operator|=
name|bm
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getHeartbeatManager
argument_list|()
operator|.
name|getDatanodes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|nodes
operator|.
name|length
argument_list|,
name|NUM_OF_DATANODES
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
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
comment|/**    * Test if {@link BlockManager#computeInvalidateWork(int)}    * can schedule invalidate work correctly     */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testCompInvalidate ()
specifier|public
name|void
name|testCompInvalidate
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|blockInvalidateLimit
init|=
name|bm
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|blockInvalidateLimit
decl_stmt|;
name|namesystem
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
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
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|3
operator|*
name|blockInvalidateLimit
operator|+
literal|1
condition|;
name|j
operator|++
control|)
block|{
name|Block
name|block
init|=
operator|new
name|Block
argument_list|(
name|i
operator|*
operator|(
name|blockInvalidateLimit
operator|+
literal|1
operator|)
operator|+
name|j
argument_list|,
literal|0
argument_list|,
name|GenerationStamp
operator|.
name|LAST_RESERVED_STAMP
argument_list|)
decl_stmt|;
name|bm
operator|.
name|addToInvalidates
argument_list|(
name|block
argument_list|,
name|nodes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|blockInvalidateLimit
operator|*
name|NUM_OF_DATANODES
argument_list|,
name|bm
operator|.
name|computeInvalidateWork
argument_list|(
name|NUM_OF_DATANODES
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockInvalidateLimit
operator|*
name|NUM_OF_DATANODES
argument_list|,
name|bm
operator|.
name|computeInvalidateWork
argument_list|(
name|NUM_OF_DATANODES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockInvalidateLimit
operator|*
operator|(
name|NUM_OF_DATANODES
operator|-
literal|1
operator|)
argument_list|,
name|bm
operator|.
name|computeInvalidateWork
argument_list|(
name|NUM_OF_DATANODES
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|workCount
init|=
name|bm
operator|.
name|computeInvalidateWork
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|workCount
operator|==
literal|1
condition|)
block|{
name|assertEquals
argument_list|(
name|blockInvalidateLimit
operator|+
literal|1
argument_list|,
name|bm
operator|.
name|computeInvalidateWork
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|workCount
argument_list|,
name|blockInvalidateLimit
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|bm
operator|.
name|computeInvalidateWork
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|namesystem
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Reformatted DataNodes will replace the original UUID in the    * {@link DatanodeManager#datanodeMap}. This tests if block    * invalidation work on the original DataNode can be skipped.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testDatanodeReformat ()
specifier|public
name|void
name|testDatanodeReformat
parameter_list|()
throws|throws
name|Exception
block|{
name|namesystem
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// Change the datanode UUID to emulate a reformat
name|String
name|poolId
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|DatanodeRegistration
name|dnr
init|=
name|cluster
operator|.
name|getDataNode
argument_list|(
name|nodes
index|[
literal|0
index|]
operator|.
name|getIpcPort
argument_list|()
argument_list|)
operator|.
name|getDNRegistrationForBP
argument_list|(
name|poolId
argument_list|)
decl_stmt|;
name|dnr
operator|=
operator|new
name|DatanodeRegistration
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|dnr
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|nodes
index|[
literal|0
index|]
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
name|Block
name|block
init|=
operator|new
name|Block
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|GenerationStamp
operator|.
name|LAST_RESERVED_STAMP
argument_list|)
decl_stmt|;
name|bm
operator|.
name|addToInvalidates
argument_list|(
name|block
argument_list|,
name|nodes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|bm
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|registerDatanode
argument_list|(
name|dnr
argument_list|)
expr_stmt|;
comment|// Since UUID has changed, the invalidation work should be skipped
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bm
operator|.
name|computeInvalidateWork
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bm
operator|.
name|getPendingDeletionBlocksCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|namesystem
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|12000
argument_list|)
DECL|method|testDatanodeReRegistration ()
specifier|public
name|void
name|testDatanodeReRegistration
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create a test file
specifier|final
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/testRR"
argument_list|)
decl_stmt|;
comment|// Create a file and shutdown the DNs, which populates InvalidateBlocks
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|path
argument_list|,
name|dfs
operator|.
name|getDefaultBlockSize
argument_list|()
argument_list|,
operator|(
name|short
operator|)
name|NUM_OF_DATANODES
argument_list|,
literal|0xED0ED0
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
name|dn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|dfs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|namesystem
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|InvalidateBlocks
name|invalidateBlocks
decl_stmt|;
name|int
name|expected
init|=
name|NUM_OF_DATANODES
decl_stmt|;
try|try
block|{
name|invalidateBlocks
operator|=
operator|(
name|InvalidateBlocks
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
argument_list|,
literal|"invalidateBlocks"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected invalidate blocks to be the number of DNs"
argument_list|,
operator|(
name|long
operator|)
name|expected
argument_list|,
name|invalidateBlocks
operator|.
name|numBlocks
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|namesystem
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
comment|// Re-register each DN and see that it wipes the invalidation work
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
name|DatanodeID
name|did
init|=
name|dn
operator|.
name|getDatanodeId
argument_list|()
decl_stmt|;
name|DatanodeRegistration
name|reg
init|=
operator|new
name|DatanodeRegistration
argument_list|(
operator|new
name|DatanodeID
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|did
argument_list|)
argument_list|,
operator|new
name|StorageInfo
argument_list|(
name|HdfsServerConstants
operator|.
name|NodeType
operator|.
name|DATA_NODE
argument_list|)
argument_list|,
operator|new
name|ExportedBlockKeys
argument_list|()
argument_list|,
name|VersionInfo
operator|.
name|getVersion
argument_list|()
argument_list|)
decl_stmt|;
name|namesystem
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|bm
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|registerDatanode
argument_list|(
name|reg
argument_list|)
expr_stmt|;
name|expected
operator|--
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected number of invalidate blocks to decrease"
argument_list|,
operator|(
name|long
operator|)
name|expected
argument_list|,
name|invalidateBlocks
operator|.
name|numBlocks
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|namesystem
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

