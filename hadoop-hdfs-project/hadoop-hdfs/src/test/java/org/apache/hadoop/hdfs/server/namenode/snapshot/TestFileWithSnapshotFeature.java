begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.snapshot
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
operator|.
name|snapshot
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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|BlockStoragePolicy
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
name|BlockInfoContiguous
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
name|BlockStoragePolicySuite
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
name|INode
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
name|INodeFile
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
name|QuotaCounts
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
name|DISK
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
name|SSD
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
name|anyByte
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
name|mock
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
name|when
import|;
end_import

begin_class
DECL|class|TestFileWithSnapshotFeature
specifier|public
class|class
name|TestFileWithSnapshotFeature
block|{
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|REPL_3
specifier|private
specifier|static
specifier|final
name|short
name|REPL_3
init|=
literal|3
decl_stmt|;
DECL|field|REPL_1
specifier|private
specifier|static
specifier|final
name|short
name|REPL_1
init|=
literal|1
decl_stmt|;
annotation|@
name|Test
DECL|method|testUpdateQuotaAndCollectBlocks ()
specifier|public
name|void
name|testUpdateQuotaAndCollectBlocks
parameter_list|()
block|{
name|FileDiffList
name|diffs
init|=
operator|new
name|FileDiffList
argument_list|()
decl_stmt|;
name|FileWithSnapshotFeature
name|sf
init|=
operator|new
name|FileWithSnapshotFeature
argument_list|(
name|diffs
argument_list|)
decl_stmt|;
name|FileDiff
name|diff
init|=
name|mock
argument_list|(
name|FileDiff
operator|.
name|class
argument_list|)
decl_stmt|;
name|BlockStoragePolicySuite
name|bsps
init|=
name|mock
argument_list|(
name|BlockStoragePolicySuite
operator|.
name|class
argument_list|)
decl_stmt|;
name|BlockStoragePolicy
name|bsp
init|=
name|mock
argument_list|(
name|BlockStoragePolicy
operator|.
name|class
argument_list|)
decl_stmt|;
name|BlockInfoContiguous
index|[]
name|blocks
init|=
operator|new
name|BlockInfoContiguous
index|[]
block|{
operator|new
name|BlockInfoContiguous
argument_list|(
operator|new
name|Block
argument_list|(
literal|1
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|1
argument_list|)
argument_list|,
name|REPL_1
argument_list|)
block|}
decl_stmt|;
comment|// No snapshot
name|INodeFile
name|file
init|=
name|mock
argument_list|(
name|INodeFile
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|file
operator|.
name|getFileWithSnapshotFeature
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|sf
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|file
operator|.
name|getBlocks
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|blocks
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|file
operator|.
name|getStoragePolicyID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|bsps
operator|.
name|getPolicy
argument_list|(
name|anyByte
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|bsp
argument_list|)
expr_stmt|;
name|INode
operator|.
name|BlocksMapUpdateInfo
name|collectedBlocks
init|=
name|mock
argument_list|(
name|INode
operator|.
name|BlocksMapUpdateInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|INode
argument_list|>
name|removedINodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|QuotaCounts
name|counts
init|=
name|sf
operator|.
name|updateQuotaAndCollectBlocks
argument_list|(
name|bsps
argument_list|,
name|file
argument_list|,
name|diff
argument_list|,
name|collectedBlocks
argument_list|,
name|removedINodes
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|counts
operator|.
name|getStorageSpace
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|counts
operator|.
name|getTypeSpaces
argument_list|()
operator|.
name|allLessOrEqual
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// INode only exists in the snapshot
name|INodeFile
name|snapshotINode
init|=
name|mock
argument_list|(
name|INodeFile
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|file
operator|.
name|getBlockReplication
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|REPL_1
argument_list|)
expr_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|snapshotINode
argument_list|,
literal|"header"
argument_list|,
operator|(
name|long
operator|)
name|REPL_3
operator|<<
literal|48
argument_list|)
expr_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|diff
argument_list|,
literal|"snapshotINode"
argument_list|,
name|snapshotINode
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|diff
operator|.
name|getSnapshotINode
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|snapshotINode
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|bsp
operator|.
name|chooseStorageTypes
argument_list|(
name|REPL_1
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|SSD
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|bsp
operator|.
name|chooseStorageTypes
argument_list|(
name|REPL_3
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|DISK
argument_list|)
argument_list|)
expr_stmt|;
name|counts
operator|=
name|sf
operator|.
name|updateQuotaAndCollectBlocks
argument_list|(
name|bsps
argument_list|,
name|file
argument_list|,
name|diff
argument_list|,
name|collectedBlocks
argument_list|,
name|removedINodes
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|(
name|REPL_3
operator|-
name|REPL_1
operator|)
operator|*
name|BLOCK_SIZE
argument_list|,
name|counts
operator|.
name|getStorageSpace
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|BLOCK_SIZE
argument_list|,
name|counts
operator|.
name|getTypeSpaces
argument_list|()
operator|.
name|get
argument_list|(
name|DISK
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|-
name|BLOCK_SIZE
argument_list|,
name|counts
operator|.
name|getTypeSpaces
argument_list|()
operator|.
name|get
argument_list|(
name|SSD
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

