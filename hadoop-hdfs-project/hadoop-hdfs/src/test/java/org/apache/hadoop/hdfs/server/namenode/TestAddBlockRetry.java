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
name|assertTrue
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
name|doAnswer
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
name|spy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|CreateFlag
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
name|permission
operator|.
name|FsPermission
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
name|blockmanagement
operator|.
name|DatanodeStorageInfo
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
name|NamenodeProtocols
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
name|EnumSetWritable
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
name|net
operator|.
name|Node
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
name|Mockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

begin_comment
comment|/**  * Race between two threads simultaneously calling  * FSNamesystem.getAdditionalBlock().  */
end_comment

begin_class
DECL|class|TestAddBlockRetry
specifier|public
class|class
name|TestAddBlockRetry
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
name|TestAddBlockRetry
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|REPLICATION
specifier|private
specifier|static
specifier|final
name|short
name|REPLICATION
init|=
literal|3
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|field|lb1
specifier|private
name|LocatedBlock
name|lb1
decl_stmt|;
DECL|field|lb2
specifier|private
name|LocatedBlock
name|lb2
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
name|Configuration
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
name|REPLICATION
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
comment|/**    * Retry addBlock() while another thread is in chooseTarget().    * See HDFS-4452.    */
annotation|@
name|Test
DECL|method|testRetryAddBlockWhileInChooseTarget ()
specifier|public
name|void
name|testRetryAddBlockWhileInChooseTarget
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|src
init|=
literal|"/testRetryAddBlockWhileInChooseTarget"
decl_stmt|;
name|FSNamesystem
name|ns
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|BlockManager
name|spyBM
init|=
name|spy
argument_list|(
name|ns
operator|.
name|getBlockManager
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|NamenodeProtocols
name|nn
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
decl_stmt|;
comment|// substitute mocked BlockManager into FSNamesystem
name|Class
argument_list|<
name|?
extends|extends
name|FSNamesystem
argument_list|>
name|nsClass
init|=
name|ns
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|Field
name|bmField
init|=
name|nsClass
operator|.
name|getDeclaredField
argument_list|(
literal|"blockManager"
argument_list|)
decl_stmt|;
name|bmField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|bmField
operator|.
name|set
argument_list|(
name|ns
argument_list|,
name|spyBM
argument_list|)
expr_stmt|;
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|DatanodeStorageInfo
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DatanodeStorageInfo
index|[]
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"chooseTarget for "
operator|+
name|src
argument_list|)
expr_stmt|;
name|DatanodeStorageInfo
index|[]
name|ret
init|=
operator|(
name|DatanodeStorageInfo
index|[]
operator|)
name|invocation
operator|.
name|callRealMethod
argument_list|()
decl_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|==
literal|1
condition|)
block|{
comment|// run second addBlock()
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting second addBlock for "
operator|+
name|src
argument_list|)
expr_stmt|;
name|nn
operator|.
name|addBlock
argument_list|(
name|src
argument_list|,
literal|"clientName"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|INodeId
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|LocatedBlocks
name|lbs
init|=
name|nn
operator|.
name|getBlockLocations
argument_list|(
name|src
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Must be one block"
argument_list|,
literal|1
argument_list|,
name|lbs
operator|.
name|getLocatedBlocks
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|lb2
operator|=
name|lbs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong replication"
argument_list|,
name|REPLICATION
argument_list|,
name|lb2
operator|.
name|getLocations
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|spyBM
argument_list|)
operator|.
name|chooseTarget4NewBlock
argument_list|(
name|Mockito
operator|.
name|anyString
argument_list|()
argument_list|,
name|Mockito
operator|.
name|anyInt
argument_list|()
argument_list|,
name|Mockito
operator|.
expr|<
name|DatanodeDescriptor
operator|>
name|any
argument_list|()
argument_list|,
name|Mockito
operator|.
expr|<
name|HashSet
argument_list|<
name|Node
argument_list|>
operator|>
name|any
argument_list|()
argument_list|,
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|,
name|Mockito
operator|.
expr|<
name|List
argument_list|<
name|String
argument_list|>
operator|>
name|any
argument_list|()
argument_list|,
name|Mockito
operator|.
name|anyByte
argument_list|()
argument_list|)
expr_stmt|;
comment|// create file
name|nn
operator|.
name|create
argument_list|(
name|src
argument_list|,
name|FsPermission
operator|.
name|getFileDefault
argument_list|()
argument_list|,
literal|"clientName"
argument_list|,
operator|new
name|EnumSetWritable
argument_list|<
name|CreateFlag
argument_list|>
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
comment|// start first addBlock()
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting first addBlock for "
operator|+
name|src
argument_list|)
expr_stmt|;
name|nn
operator|.
name|addBlock
argument_list|(
name|src
argument_list|,
literal|"clientName"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|INodeId
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// check locations
name|LocatedBlocks
name|lbs
init|=
name|nn
operator|.
name|getBlockLocations
argument_list|(
name|src
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Must be one block"
argument_list|,
literal|1
argument_list|,
name|lbs
operator|.
name|getLocatedBlocks
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|lb1
operator|=
name|lbs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong replication"
argument_list|,
name|REPLICATION
argument_list|,
name|lb1
operator|.
name|getLocations
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Blocks are not equal"
argument_list|,
name|lb1
operator|.
name|getBlock
argument_list|()
argument_list|,
name|lb2
operator|.
name|getBlock
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*    * Since NameNode will not persist any locations of the block, addBlock()    * retry call after restart NN should re-select the locations and return to    * client. refer HDFS-5257    */
annotation|@
name|Test
DECL|method|testAddBlockRetryShouldReturnBlockWithLocations ()
specifier|public
name|void
name|testAddBlockRetryShouldReturnBlockWithLocations
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|src
init|=
literal|"/testAddBlockRetryShouldReturnBlockWithLocations"
decl_stmt|;
name|NamenodeProtocols
name|nameNodeRpc
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
decl_stmt|;
comment|// create file
name|nameNodeRpc
operator|.
name|create
argument_list|(
name|src
argument_list|,
name|FsPermission
operator|.
name|getFileDefault
argument_list|()
argument_list|,
literal|"clientName"
argument_list|,
operator|new
name|EnumSetWritable
argument_list|<
name|CreateFlag
argument_list|>
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
comment|// start first addBlock()
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting first addBlock for "
operator|+
name|src
argument_list|)
expr_stmt|;
name|LocatedBlock
name|lb1
init|=
name|nameNodeRpc
operator|.
name|addBlock
argument_list|(
name|src
argument_list|,
literal|"clientName"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|INodeId
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Block locations should be present"
argument_list|,
name|lb1
operator|.
name|getLocations
argument_list|()
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
name|nameNodeRpc
operator|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
expr_stmt|;
name|LocatedBlock
name|lb2
init|=
name|nameNodeRpc
operator|.
name|addBlock
argument_list|(
name|src
argument_list|,
literal|"clientName"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|INodeId
operator|.
name|GRANDFATHER_INODE_ID
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Blocks are not equal"
argument_list|,
name|lb1
operator|.
name|getBlock
argument_list|()
argument_list|,
name|lb2
operator|.
name|getBlock
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Wrong locations with retry"
argument_list|,
name|lb2
operator|.
name|getLocations
argument_list|()
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

