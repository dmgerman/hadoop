begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
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
name|FSDataOutputStream
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
name|fs
operator|.
name|StorageType
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
name|io
operator|.
name|IOUtils
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

begin_comment
comment|/**  * Make sure we correctly update the quota usage with the striped blocks.  */
end_comment

begin_class
DECL|class|TestQuotaWithStripedBlocks
specifier|public
class|class
name|TestQuotaWithStripedBlocks
block|{
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|DISK_QUOTA
specifier|private
specifier|static
specifier|final
name|long
name|DISK_QUOTA
init|=
name|BLOCK_SIZE
operator|*
literal|10
decl_stmt|;
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
name|int
name|dataBlocks
init|=
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
decl_stmt|;
DECL|field|parityBlocsk
specifier|private
specifier|final
name|int
name|parityBlocsk
init|=
name|ecPolicy
operator|.
name|getNumParityUnits
argument_list|()
decl_stmt|;
DECL|field|groupSize
specifier|private
specifier|final
name|int
name|groupSize
init|=
name|dataBlocks
operator|+
name|parityBlocsk
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
DECL|field|ecDir
specifier|private
specifier|static
specifier|final
name|Path
name|ecDir
init|=
operator|new
name|Path
argument_list|(
literal|"/ec"
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|dir
specifier|private
name|FSDirectory
name|dir
decl_stmt|;
DECL|field|dfs
specifier|private
name|DistributedFileSystem
name|dfs
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
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
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
name|BLOCK_SIZE
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
name|groupSize
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
name|dir
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSDirectory
argument_list|()
expr_stmt|;
name|dfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|ecDir
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|setErasureCodingPolicy
argument_list|(
name|ecDir
operator|.
name|toString
argument_list|()
argument_list|,
name|ecPolicy
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|setQuota
argument_list|(
name|ecDir
argument_list|,
name|Long
operator|.
name|MAX_VALUE
operator|-
literal|1
argument_list|,
name|DISK_QUOTA
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|setQuotaByStorageType
argument_list|(
name|ecDir
argument_list|,
name|StorageType
operator|.
name|DISK
argument_list|,
name|DISK_QUOTA
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|setStoragePolicy
argument_list|(
name|ecDir
argument_list|,
name|HdfsConstants
operator|.
name|HOT_STORAGE_POLICY_NAME
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
DECL|method|testUpdatingQuotaCount ()
specifier|public
name|void
name|testUpdatingQuotaCount
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|ecDir
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|out
operator|=
name|dfs
operator|.
name|create
argument_list|(
name|file
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|INodeFile
name|fileNode
init|=
name|dir
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
decl_stmt|;
name|ExtendedBlock
name|previous
init|=
literal|null
decl_stmt|;
comment|// Create striped blocks which have a cell in each block.
name|Block
name|newBlock
init|=
name|DFSTestUtil
operator|.
name|addBlockToFile
argument_list|(
literal|true
argument_list|,
name|cluster
operator|.
name|getDataNodes
argument_list|()
argument_list|,
name|dfs
argument_list|,
name|cluster
operator|.
name|getNamesystem
argument_list|()
argument_list|,
name|file
operator|.
name|toString
argument_list|()
argument_list|,
name|fileNode
argument_list|,
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getClientName
argument_list|()
argument_list|,
name|previous
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|previous
operator|=
operator|new
name|ExtendedBlock
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|newBlock
argument_list|)
expr_stmt|;
specifier|final
name|INodeDirectory
name|dirNode
init|=
name|dir
operator|.
name|getINode4Write
argument_list|(
name|ecDir
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asDirectory
argument_list|()
decl_stmt|;
specifier|final
name|long
name|spaceUsed
init|=
name|dirNode
operator|.
name|getDirectoryWithQuotaFeature
argument_list|()
operator|.
name|getSpaceConsumed
argument_list|()
operator|.
name|getStorageSpace
argument_list|()
decl_stmt|;
specifier|final
name|long
name|diskUsed
init|=
name|dirNode
operator|.
name|getDirectoryWithQuotaFeature
argument_list|()
operator|.
name|getSpaceConsumed
argument_list|()
operator|.
name|getTypeSpaces
argument_list|()
operator|.
name|get
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
decl_stmt|;
comment|// When we add a new block we update the quota using the full block size.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|BLOCK_SIZE
operator|*
name|groupSize
argument_list|,
name|spaceUsed
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|BLOCK_SIZE
operator|*
name|groupSize
argument_list|,
name|diskUsed
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getNamenode
argument_list|()
operator|.
name|complete
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|,
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getClientName
argument_list|()
argument_list|,
name|previous
argument_list|,
name|fileNode
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|long
name|actualSpaceUsed
init|=
name|dirNode
operator|.
name|getDirectoryWithQuotaFeature
argument_list|()
operator|.
name|getSpaceConsumed
argument_list|()
operator|.
name|getStorageSpace
argument_list|()
decl_stmt|;
specifier|final
name|long
name|actualDiskUsed
init|=
name|dirNode
operator|.
name|getDirectoryWithQuotaFeature
argument_list|()
operator|.
name|getSpaceConsumed
argument_list|()
operator|.
name|getTypeSpaces
argument_list|()
operator|.
name|get
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
decl_stmt|;
comment|// In this case the file's real size is cell size * block group size.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|cellSize
operator|*
name|groupSize
argument_list|,
name|actualSpaceUsed
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|cellSize
operator|*
name|groupSize
argument_list|,
name|actualDiskUsed
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

