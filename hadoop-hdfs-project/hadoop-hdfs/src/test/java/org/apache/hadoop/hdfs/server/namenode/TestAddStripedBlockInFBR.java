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
name|StripedFileTestUtil
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
name|server
operator|.
name|blockmanagement
operator|.
name|BlockInfoStriped
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
name|NumberReplicas
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
name|test
operator|.
name|GenericTestUtils
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
name|Whitebox
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
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|TestAddStripedBlockInFBR
specifier|public
class|class
name|TestAddStripedBlockInFBR
block|{
DECL|field|ecPolicy
specifier|private
specifier|final
name|ErasureCodingPolicy
name|ecPolicy
init|=
name|StripedFileTestUtil
operator|.
name|getDefaultECPolicy
argument_list|()
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
DECL|field|groupSize
specifier|private
specifier|final
name|short
name|groupSize
init|=
call|(
name|short
call|)
argument_list|(
name|dataBlocks
operator|+
name|parityBlocks
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
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
DECL|method|setup ()
specifier|public
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
name|HdfsConfiguration
argument_list|()
decl_stmt|;
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
name|dfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|dfs
operator|.
name|enableErasureCodingPolicy
argument_list|(
name|StripedFileTestUtil
operator|.
name|getDefaultECPolicy
argument_list|()
operator|.
name|getName
argument_list|()
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
DECL|method|testAddBlockInFullBlockReport ()
specifier|public
name|void
name|testAddBlockInFullBlockReport
parameter_list|()
throws|throws
name|Exception
block|{
name|BlockManager
name|spy
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
argument_list|)
decl_stmt|;
comment|// let NN ignore one DataNode's IBR
specifier|final
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
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|DatanodeID
name|datanodeID
init|=
name|dn
operator|.
name|getDatanodeId
argument_list|()
decl_stmt|;
name|Mockito
operator|.
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|spy
argument_list|)
operator|.
name|processIncrementalBlockReport
argument_list|(
name|Mockito
operator|.
name|eq
argument_list|(
name|datanodeID
argument_list|)
argument_list|,
name|Mockito
operator|.
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|()
argument_list|,
literal|"blockManager"
argument_list|,
name|spy
argument_list|)
expr_stmt|;
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
specifier|final
name|Path
name|repDir
init|=
operator|new
name|Path
argument_list|(
literal|"/rep"
argument_list|)
decl_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|ecDir
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|repDir
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
name|StripedFileTestUtil
operator|.
name|getDefaultECPolicy
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// create several non-EC files and one EC file
specifier|final
name|Path
index|[]
name|repFiles
init|=
operator|new
name|Path
index|[
name|groupSize
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
name|groupSize
condition|;
name|i
operator|++
control|)
block|{
name|repFiles
index|[
name|i
index|]
operator|=
operator|new
name|Path
argument_list|(
name|repDir
argument_list|,
literal|"f"
operator|+
name|i
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|repFiles
index|[
name|i
index|]
argument_list|,
literal|1L
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Path
name|ecFile
init|=
operator|new
name|Path
argument_list|(
name|ecDir
argument_list|,
literal|"f"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|ecFile
argument_list|,
name|cellSize
operator|*
name|dataBlocks
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0L
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
try|try
block|{
comment|// trigger dn's FBR. The FBR will add block-dn mapping.
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
comment|// make sure NN has correct block-dn mapping
name|BlockInfoStriped
name|blockInfo
init|=
operator|(
name|BlockInfoStriped
operator|)
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|getINode
argument_list|(
name|ecFile
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asFile
argument_list|()
operator|.
name|getLastBlock
argument_list|()
decl_stmt|;
name|NumberReplicas
name|nr
init|=
name|spy
operator|.
name|countNodes
argument_list|(
name|blockInfo
argument_list|)
decl_stmt|;
return|return
name|nr
operator|.
name|excessReplicas
argument_list|()
operator|==
literal|0
operator|&&
name|nr
operator|.
name|liveReplicas
argument_list|()
operator|==
name|groupSize
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{
comment|// Ignore the exception
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|,
literal|3000
argument_list|,
literal|60000
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

