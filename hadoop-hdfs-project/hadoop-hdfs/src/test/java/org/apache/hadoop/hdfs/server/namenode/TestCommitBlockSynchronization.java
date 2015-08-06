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
name|BlockInfoContiguousUnderConstruction
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
name|common
operator|.
name|HdfsServerConstants
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
name|io
operator|.
name|IOException
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
name|fail
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
name|any
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
name|*
import|;
end_import

begin_comment
comment|/**  * Verify that TestCommitBlockSynchronization is idempotent.  */
end_comment

begin_class
DECL|class|TestCommitBlockSynchronization
specifier|public
class|class
name|TestCommitBlockSynchronization
block|{
DECL|field|blockId
specifier|private
specifier|static
specifier|final
name|long
name|blockId
init|=
literal|100
decl_stmt|;
DECL|field|length
specifier|private
specifier|static
specifier|final
name|long
name|length
init|=
literal|200
decl_stmt|;
DECL|field|genStamp
specifier|private
specifier|static
specifier|final
name|long
name|genStamp
init|=
literal|300
decl_stmt|;
DECL|method|makeNameSystemSpy (Block block, INodeFile file)
specifier|private
name|FSNamesystem
name|makeNameSystemSpy
parameter_list|(
name|Block
name|block
parameter_list|,
name|INodeFile
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FSEditLog
name|editlog
init|=
name|mock
argument_list|(
name|FSEditLog
operator|.
name|class
argument_list|)
decl_stmt|;
name|FSImage
name|image
init|=
operator|new
name|FSImage
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|image
argument_list|,
literal|"editLog"
argument_list|,
name|editlog
argument_list|)
expr_stmt|;
specifier|final
name|DatanodeStorageInfo
index|[]
name|targets
init|=
block|{}
decl_stmt|;
name|FSNamesystem
name|namesystem
init|=
operator|new
name|FSNamesystem
argument_list|(
name|conf
argument_list|,
name|image
argument_list|)
decl_stmt|;
name|namesystem
operator|.
name|setImageLoaded
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// set file's parent as root and put the file to inodeMap, so
comment|// FSNamesystem's isFileDeleted() method will return false on this file
if|if
condition|(
name|file
operator|.
name|getParent
argument_list|()
operator|==
literal|null
condition|)
block|{
name|INodeDirectory
name|mparent
init|=
name|mock
argument_list|(
name|INodeDirectory
operator|.
name|class
argument_list|)
decl_stmt|;
name|INodeDirectory
name|parent
init|=
operator|new
name|INodeDirectory
argument_list|(
name|mparent
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
name|mparent
operator|.
name|getPermissionStatus
argument_list|()
argument_list|,
name|mparent
operator|.
name|getAccessTime
argument_list|()
argument_list|)
decl_stmt|;
name|parent
operator|.
name|setLocalName
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addChild
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|file
operator|.
name|setParent
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
name|namesystem
operator|.
name|dir
operator|.
name|getINodeMap
argument_list|()
operator|.
name|put
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|FSNamesystem
name|namesystemSpy
init|=
name|spy
argument_list|(
name|namesystem
argument_list|)
decl_stmt|;
name|BlockInfoContiguousUnderConstruction
name|blockInfo
init|=
operator|new
name|BlockInfoContiguousUnderConstruction
argument_list|(
name|block
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|HdfsServerConstants
operator|.
name|BlockUCState
operator|.
name|UNDER_CONSTRUCTION
argument_list|,
name|targets
argument_list|)
decl_stmt|;
name|blockInfo
operator|.
name|setBlockCollection
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|blockInfo
operator|.
name|setGenerationStamp
argument_list|(
name|genStamp
argument_list|)
expr_stmt|;
name|blockInfo
operator|.
name|initializeBlockRecovery
argument_list|(
name|genStamp
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
name|blockInfo
argument_list|)
operator|.
name|when
argument_list|(
name|file
argument_list|)
operator|.
name|removeLastBlock
argument_list|(
name|any
argument_list|(
name|Block
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|file
argument_list|)
operator|.
name|isUnderConstruction
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
operator|new
name|BlockInfoContiguous
index|[
literal|1
index|]
argument_list|)
operator|.
name|when
argument_list|(
name|file
argument_list|)
operator|.
name|getBlocks
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|blockInfo
argument_list|)
operator|.
name|when
argument_list|(
name|namesystemSpy
argument_list|)
operator|.
name|getStoredBlock
argument_list|(
name|any
argument_list|(
name|Block
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
name|blockInfo
argument_list|)
operator|.
name|when
argument_list|(
name|file
argument_list|)
operator|.
name|getLastBlock
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
literal|""
argument_list|)
operator|.
name|when
argument_list|(
name|namesystemSpy
argument_list|)
operator|.
name|closeFileCommitBlocks
argument_list|(
name|any
argument_list|(
name|INodeFile
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|BlockInfo
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
name|mock
argument_list|(
name|FSEditLog
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|namesystemSpy
argument_list|)
operator|.
name|getEditLog
argument_list|()
expr_stmt|;
return|return
name|namesystemSpy
return|;
block|}
DECL|method|mockFileUnderConstruction ()
specifier|private
name|INodeFile
name|mockFileUnderConstruction
parameter_list|()
block|{
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
return|return
name|file
return|;
block|}
annotation|@
name|Test
DECL|method|testCommitBlockSynchronization ()
specifier|public
name|void
name|testCommitBlockSynchronization
parameter_list|()
throws|throws
name|IOException
block|{
name|INodeFile
name|file
init|=
name|mockFileUnderConstruction
argument_list|()
decl_stmt|;
name|Block
name|block
init|=
operator|new
name|Block
argument_list|(
name|blockId
argument_list|,
name|length
argument_list|,
name|genStamp
argument_list|)
decl_stmt|;
name|FSNamesystem
name|namesystemSpy
init|=
name|makeNameSystemSpy
argument_list|(
name|block
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|DatanodeID
index|[]
name|newTargets
init|=
operator|new
name|DatanodeID
index|[
literal|0
index|]
decl_stmt|;
name|ExtendedBlock
name|lastBlock
init|=
operator|new
name|ExtendedBlock
argument_list|()
decl_stmt|;
name|namesystemSpy
operator|.
name|commitBlockSynchronization
argument_list|(
name|lastBlock
argument_list|,
name|genStamp
argument_list|,
name|length
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|newTargets
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Repeat the call to make sure it does not throw
name|namesystemSpy
operator|.
name|commitBlockSynchronization
argument_list|(
name|lastBlock
argument_list|,
name|genStamp
argument_list|,
name|length
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|newTargets
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Simulate 'completing' the block.
name|BlockInfo
name|completedBlockInfo
init|=
operator|new
name|BlockInfoContiguous
argument_list|(
name|block
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
decl_stmt|;
name|completedBlockInfo
operator|.
name|setBlockCollection
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|completedBlockInfo
operator|.
name|setGenerationStamp
argument_list|(
name|genStamp
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
name|completedBlockInfo
argument_list|)
operator|.
name|when
argument_list|(
name|namesystemSpy
argument_list|)
operator|.
name|getStoredBlock
argument_list|(
name|any
argument_list|(
name|Block
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
name|completedBlockInfo
argument_list|)
operator|.
name|when
argument_list|(
name|file
argument_list|)
operator|.
name|getLastBlock
argument_list|()
expr_stmt|;
comment|// Repeat the call to make sure it does not throw
name|namesystemSpy
operator|.
name|commitBlockSynchronization
argument_list|(
name|lastBlock
argument_list|,
name|genStamp
argument_list|,
name|length
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|newTargets
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommitBlockSynchronization2 ()
specifier|public
name|void
name|testCommitBlockSynchronization2
parameter_list|()
throws|throws
name|IOException
block|{
name|INodeFile
name|file
init|=
name|mockFileUnderConstruction
argument_list|()
decl_stmt|;
name|Block
name|block
init|=
operator|new
name|Block
argument_list|(
name|blockId
argument_list|,
name|length
argument_list|,
name|genStamp
argument_list|)
decl_stmt|;
name|FSNamesystem
name|namesystemSpy
init|=
name|makeNameSystemSpy
argument_list|(
name|block
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|DatanodeID
index|[]
name|newTargets
init|=
operator|new
name|DatanodeID
index|[
literal|0
index|]
decl_stmt|;
name|ExtendedBlock
name|lastBlock
init|=
operator|new
name|ExtendedBlock
argument_list|()
decl_stmt|;
name|namesystemSpy
operator|.
name|commitBlockSynchronization
argument_list|(
name|lastBlock
argument_list|,
name|genStamp
argument_list|,
name|length
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|newTargets
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Make sure the call fails if the generation stamp does not match
comment|// the block recovery ID.
try|try
block|{
name|namesystemSpy
operator|.
name|commitBlockSynchronization
argument_list|(
name|lastBlock
argument_list|,
name|genStamp
operator|-
literal|1
argument_list|,
name|length
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|newTargets
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failed to get expected IOException on generation stamp/"
operator|+
literal|"recovery ID mismatch"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// Expected exception.
block|}
block|}
annotation|@
name|Test
DECL|method|testCommitBlockSynchronizationWithDelete ()
specifier|public
name|void
name|testCommitBlockSynchronizationWithDelete
parameter_list|()
throws|throws
name|IOException
block|{
name|INodeFile
name|file
init|=
name|mockFileUnderConstruction
argument_list|()
decl_stmt|;
name|Block
name|block
init|=
operator|new
name|Block
argument_list|(
name|blockId
argument_list|,
name|length
argument_list|,
name|genStamp
argument_list|)
decl_stmt|;
name|FSNamesystem
name|namesystemSpy
init|=
name|makeNameSystemSpy
argument_list|(
name|block
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|DatanodeID
index|[]
name|newTargets
init|=
operator|new
name|DatanodeID
index|[
literal|0
index|]
decl_stmt|;
name|ExtendedBlock
name|lastBlock
init|=
operator|new
name|ExtendedBlock
argument_list|()
decl_stmt|;
name|namesystemSpy
operator|.
name|commitBlockSynchronization
argument_list|(
name|lastBlock
argument_list|,
name|genStamp
argument_list|,
name|length
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|newTargets
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Simulate removing the last block from the file.
name|doReturn
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|file
argument_list|)
operator|.
name|removeLastBlock
argument_list|(
name|any
argument_list|(
name|Block
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// Repeat the call to make sure it does not throw
name|namesystemSpy
operator|.
name|commitBlockSynchronization
argument_list|(
name|lastBlock
argument_list|,
name|genStamp
argument_list|,
name|length
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|newTargets
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommitBlockSynchronizationWithClose ()
specifier|public
name|void
name|testCommitBlockSynchronizationWithClose
parameter_list|()
throws|throws
name|IOException
block|{
name|INodeFile
name|file
init|=
name|mockFileUnderConstruction
argument_list|()
decl_stmt|;
name|Block
name|block
init|=
operator|new
name|Block
argument_list|(
name|blockId
argument_list|,
name|length
argument_list|,
name|genStamp
argument_list|)
decl_stmt|;
name|FSNamesystem
name|namesystemSpy
init|=
name|makeNameSystemSpy
argument_list|(
name|block
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|DatanodeID
index|[]
name|newTargets
init|=
operator|new
name|DatanodeID
index|[
literal|0
index|]
decl_stmt|;
name|ExtendedBlock
name|lastBlock
init|=
operator|new
name|ExtendedBlock
argument_list|()
decl_stmt|;
name|namesystemSpy
operator|.
name|commitBlockSynchronization
argument_list|(
name|lastBlock
argument_list|,
name|genStamp
argument_list|,
name|length
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|newTargets
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Repeat the call to make sure it returns true
name|namesystemSpy
operator|.
name|commitBlockSynchronization
argument_list|(
name|lastBlock
argument_list|,
name|genStamp
argument_list|,
name|length
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|newTargets
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|BlockInfo
name|completedBlockInfo
init|=
operator|new
name|BlockInfoContiguous
argument_list|(
name|block
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
decl_stmt|;
name|completedBlockInfo
operator|.
name|setBlockCollection
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|completedBlockInfo
operator|.
name|setGenerationStamp
argument_list|(
name|genStamp
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
name|completedBlockInfo
argument_list|)
operator|.
name|when
argument_list|(
name|namesystemSpy
argument_list|)
operator|.
name|getStoredBlock
argument_list|(
name|any
argument_list|(
name|Block
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
name|completedBlockInfo
argument_list|)
operator|.
name|when
argument_list|(
name|file
argument_list|)
operator|.
name|getLastBlock
argument_list|()
expr_stmt|;
name|namesystemSpy
operator|.
name|commitBlockSynchronization
argument_list|(
name|lastBlock
argument_list|,
name|genStamp
argument_list|,
name|length
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|newTargets
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommitBlockSynchronizationWithCloseAndNonExistantTarget ()
specifier|public
name|void
name|testCommitBlockSynchronizationWithCloseAndNonExistantTarget
parameter_list|()
throws|throws
name|IOException
block|{
name|INodeFile
name|file
init|=
name|mockFileUnderConstruction
argument_list|()
decl_stmt|;
name|Block
name|block
init|=
operator|new
name|Block
argument_list|(
name|blockId
argument_list|,
name|length
argument_list|,
name|genStamp
argument_list|)
decl_stmt|;
name|FSNamesystem
name|namesystemSpy
init|=
name|makeNameSystemSpy
argument_list|(
name|block
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|DatanodeID
index|[]
name|newTargets
init|=
operator|new
name|DatanodeID
index|[]
block|{
operator|new
name|DatanodeID
argument_list|(
literal|"0.0.0.0"
argument_list|,
literal|"nonexistantHost"
argument_list|,
literal|"1"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
block|}
decl_stmt|;
name|ExtendedBlock
name|lastBlock
init|=
operator|new
name|ExtendedBlock
argument_list|()
decl_stmt|;
name|namesystemSpy
operator|.
name|commitBlockSynchronization
argument_list|(
name|lastBlock
argument_list|,
name|genStamp
argument_list|,
name|length
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|newTargets
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Repeat the call to make sure it returns true
name|namesystemSpy
operator|.
name|commitBlockSynchronization
argument_list|(
name|lastBlock
argument_list|,
name|genStamp
argument_list|,
name|length
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|newTargets
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

