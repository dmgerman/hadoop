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
name|collect
operator|.
name|Lists
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
name|ErasureCodingPolicy
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
name|server
operator|.
name|namenode
operator|.
name|ErasureCodingPolicyManager
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
name|namenode
operator|.
name|NameNodeAdapter
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
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|Timeout
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
name|List
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

begin_class
DECL|class|TestSafeModeWithStripedFile
specifier|public
class|class
name|TestSafeModeWithStripedFile
block|{
DECL|field|ecPolicy
specifier|private
specifier|final
name|ErasureCodingPolicy
name|ecPolicy
init|=
name|ErasureCodingPolicyManager
operator|.
name|getSystemDefaultPolicy
argument_list|()
decl_stmt|;
DECL|field|dataBlocks
specifier|private
specifier|final
name|short
name|dataBlocks
init|=
operator|(
name|short
operator|)
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
decl_stmt|;
DECL|field|parityBlocks
specifier|private
specifier|final
name|short
name|parityBlocks
init|=
operator|(
name|short
operator|)
name|ecPolicy
operator|.
name|getNumParityUnits
argument_list|()
decl_stmt|;
DECL|field|numDNs
specifier|private
specifier|final
name|int
name|numDNs
init|=
name|dataBlocks
operator|+
name|parityBlocks
decl_stmt|;
DECL|field|cellSize
specifier|private
specifier|final
name|int
name|cellSize
init|=
name|ecPolicy
operator|.
name|getCellSize
argument_list|()
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|final
name|int
name|blockSize
init|=
name|cellSize
operator|*
literal|2
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Rule
DECL|field|globalTimeout
specifier|public
name|Timeout
name|globalTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
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
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INTERVAL_MSEC_KEY
argument_list|,
literal|100
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
name|setErasureCodingPolicy
argument_list|(
literal|"/"
argument_list|,
name|ErasureCodingPolicyManager
operator|.
name|getSystemDefaultPolicy
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
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
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testStripedFile0 ()
specifier|public
name|void
name|testStripedFile0
parameter_list|()
throws|throws
name|IOException
block|{
name|doTest
argument_list|(
name|cellSize
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStripedFile1 ()
specifier|public
name|void
name|testStripedFile1
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|numCell
init|=
name|dataBlocks
operator|-
literal|1
decl_stmt|;
name|doTest
argument_list|(
name|cellSize
operator|*
name|numCell
argument_list|,
name|numCell
argument_list|)
expr_stmt|;
block|}
comment|/**    * This util writes a small block group whose size is given by caller.    * Then write another 2 full stripe blocks.    * Then shutdown all DNs and start again one by one. and verify the safemode    * status accordingly.    *    * @param smallSize file size of the small block group    * @param minStorages minimum replicas needed by the block so it can be safe    */
DECL|method|doTest (int smallSize, int minStorages)
specifier|private
name|void
name|doTest
parameter_list|(
name|int
name|smallSize
parameter_list|,
name|int
name|minStorages
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// add 1 block
name|byte
index|[]
name|data
init|=
name|StripedFileTestUtil
operator|.
name|generateBytes
argument_list|(
name|smallSize
argument_list|)
decl_stmt|;
name|Path
name|smallFilePath
init|=
operator|new
name|Path
argument_list|(
literal|"/testStripedFile_"
operator|+
name|smallSize
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|smallFilePath
argument_list|,
name|data
argument_list|)
expr_stmt|;
comment|// If we only have 1 block, NN won't enter safemode in the first place
comment|// because the threshold is 0 blocks.
comment|// So we need to add another 2 blocks.
name|int
name|bigSize
init|=
name|blockSize
operator|*
name|dataBlocks
operator|*
literal|2
decl_stmt|;
name|Path
name|bigFilePath
init|=
operator|new
name|Path
argument_list|(
literal|"/testStripedFile_"
operator|+
name|bigSize
argument_list|)
decl_stmt|;
name|data
operator|=
name|StripedFileTestUtil
operator|.
name|generateBytes
argument_list|(
name|bigSize
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|bigFilePath
argument_list|,
name|data
argument_list|)
expr_stmt|;
comment|// now we have 3 blocks. NN needs 2 blocks to reach the threshold 0.9 of
comment|// total blocks 3.
comment|// stopping all DNs
name|List
argument_list|<
name|MiniDFSCluster
operator|.
name|DataNodeProperties
argument_list|>
name|dnprops
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|LocatedBlocks
name|lbs
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|smallFilePath
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|smallSize
argument_list|)
decl_stmt|;
name|DatanodeInfo
index|[]
name|locations
init|=
name|lbs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLocations
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeInfo
name|loc
range|:
name|locations
control|)
block|{
comment|// keep the DNs that have smallFile in the head of dnprops
name|dnprops
operator|.
name|add
argument_list|(
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|loc
operator|.
name|getName
argument_list|()
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
name|numDNs
operator|-
name|locations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|dnprops
operator|.
name|add
argument_list|(
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|NameNode
name|nn
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|NameNodeAdapter
operator|.
name|getSafeModeSafeBlocks
argument_list|(
name|nn
argument_list|)
argument_list|)
expr_stmt|;
comment|// the block of smallFile doesn't reach minStorages,
comment|// so the safe blocks count doesn't increment.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|minStorages
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dnprops
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|NameNodeAdapter
operator|.
name|getSafeModeSafeBlocks
argument_list|(
name|nn
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// the block of smallFile reaches minStorages,
comment|// so the safe blocks count increment.
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dnprops
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|NameNodeAdapter
operator|.
name|getSafeModeSafeBlocks
argument_list|(
name|nn
argument_list|)
argument_list|)
expr_stmt|;
comment|// the 2 blocks of bigFile need DATA_BLK_NUM storages to be safe
for|for
control|(
name|int
name|i
init|=
name|minStorages
init|;
name|i
operator|<
name|dataBlocks
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dnprops
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|nn
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dnprops
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|nn
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

