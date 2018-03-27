begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.block
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|block
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|SCMTestUtils
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
name|hdsl
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdslProtos
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
name|ozone
operator|.
name|scm
operator|.
name|container
operator|.
name|ContainerMapping
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
name|ozone
operator|.
name|scm
operator|.
name|container
operator|.
name|MockNodeManager
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
name|scm
operator|.
name|ScmConfigKeys
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|AllocatedBlock
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
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
name|AfterClass
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
name|BeforeClass
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
name|ExpectedException
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|UUID
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
name|ozone
operator|.
name|OzoneConsts
operator|.
name|GB
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
name|ozone
operator|.
name|OzoneConsts
operator|.
name|MB
import|;
end_import

begin_comment
comment|/**  * Tests for SCM Block Manager.  */
end_comment

begin_class
DECL|class|TestBlockManager
specifier|public
class|class
name|TestBlockManager
block|{
DECL|field|mapping
specifier|private
specifier|static
name|ContainerMapping
name|mapping
decl_stmt|;
DECL|field|nodeManager
specifier|private
specifier|static
name|MockNodeManager
name|nodeManager
decl_stmt|;
DECL|field|blockManager
specifier|private
specifier|static
name|BlockManagerImpl
name|blockManager
decl_stmt|;
DECL|field|testDir
specifier|private
specifier|static
name|File
name|testDir
decl_stmt|;
DECL|field|DEFAULT_BLOCK_SIZE
specifier|private
specifier|final
specifier|static
name|long
name|DEFAULT_BLOCK_SIZE
init|=
literal|128
operator|*
name|MB
decl_stmt|;
DECL|field|factor
specifier|private
specifier|static
name|HdslProtos
operator|.
name|ReplicationFactor
name|factor
decl_stmt|;
DECL|field|type
specifier|private
specifier|static
name|HdslProtos
operator|.
name|ReplicationType
name|type
decl_stmt|;
DECL|field|containerOwner
specifier|private
specifier|static
name|String
name|containerOwner
init|=
literal|"OZONE"
decl_stmt|;
annotation|@
name|Rule
DECL|field|thrown
specifier|public
name|ExpectedException
name|thrown
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|SCMTestUtils
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestBlockManager
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|testDir
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|)
operator|.
name|toFile
argument_list|()
expr_stmt|;
name|boolean
name|folderExisted
init|=
name|testDir
operator|.
name|exists
argument_list|()
operator|||
name|testDir
operator|.
name|mkdirs
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|folderExisted
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create test directory path"
argument_list|)
throw|;
block|}
name|nodeManager
operator|=
operator|new
name|MockNodeManager
argument_list|(
literal|true
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|mapping
operator|=
operator|new
name|ContainerMapping
argument_list|(
name|conf
argument_list|,
name|nodeManager
argument_list|,
literal|128
argument_list|)
expr_stmt|;
name|blockManager
operator|=
operator|new
name|BlockManagerImpl
argument_list|(
name|conf
argument_list|,
name|nodeManager
argument_list|,
name|mapping
argument_list|,
literal|128
argument_list|)
expr_stmt|;
if|if
condition|(
name|conf
operator|.
name|getBoolean
argument_list|(
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_ENABLED_KEY
argument_list|,
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_ENABLED_DEFAULT
argument_list|)
condition|)
block|{
name|factor
operator|=
name|HdslProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
expr_stmt|;
name|type
operator|=
name|HdslProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
expr_stmt|;
block|}
else|else
block|{
name|factor
operator|=
name|HdslProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
expr_stmt|;
name|type
operator|=
name|HdslProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|cleanup ()
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
block|{
name|blockManager
operator|.
name|close
argument_list|()
expr_stmt|;
name|mapping
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|clearChillMode ()
specifier|public
name|void
name|clearChillMode
parameter_list|()
block|{
name|nodeManager
operator|.
name|setChillmode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAllocateBlock ()
specifier|public
name|void
name|testAllocateBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|AllocatedBlock
name|block
init|=
name|blockManager
operator|.
name|allocateBlock
argument_list|(
name|DEFAULT_BLOCK_SIZE
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|containerOwner
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetAllocatedBlock ()
specifier|public
name|void
name|testGetAllocatedBlock
parameter_list|()
throws|throws
name|IOException
block|{
name|AllocatedBlock
name|block
init|=
name|blockManager
operator|.
name|allocateBlock
argument_list|(
name|DEFAULT_BLOCK_SIZE
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|containerOwner
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|Pipeline
name|pipeline
init|=
name|blockManager
operator|.
name|getBlock
argument_list|(
name|block
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getLeader
argument_list|()
operator|.
name|getUuid
argument_list|()
argument_list|,
name|block
operator|.
name|getPipeline
argument_list|()
operator|.
name|getLeader
argument_list|()
operator|.
name|getUuid
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteBlock ()
specifier|public
name|void
name|testDeleteBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|AllocatedBlock
name|block
init|=
name|blockManager
operator|.
name|allocateBlock
argument_list|(
name|DEFAULT_BLOCK_SIZE
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|containerOwner
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|blockManager
operator|.
name|deleteBlocks
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|block
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Deleted block can not be retrieved
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"Specified block key does not exist."
argument_list|)
expr_stmt|;
name|blockManager
operator|.
name|getBlock
argument_list|(
name|block
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
comment|// Tombstone of the deleted block can be retrieved if it has not been
comment|// cleaned yet.
name|String
name|deletedKeyName
init|=
name|blockManager
operator|.
name|getDeletedKeyName
argument_list|(
name|block
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|blockManager
operator|.
name|getBlock
argument_list|(
name|deletedKeyName
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pipeline
operator|.
name|getLeader
argument_list|()
operator|.
name|getUuid
argument_list|()
argument_list|,
name|block
operator|.
name|getPipeline
argument_list|()
operator|.
name|getLeader
argument_list|()
operator|.
name|getUuid
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAllocateOversizedBlock ()
specifier|public
name|void
name|testAllocateOversizedBlock
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|size
init|=
literal|6
operator|*
name|GB
decl_stmt|;
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"Unsupported block size"
argument_list|)
expr_stmt|;
name|AllocatedBlock
name|block
init|=
name|blockManager
operator|.
name|allocateBlock
argument_list|(
name|size
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|containerOwner
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNoneExistentContainer ()
specifier|public
name|void
name|testGetNoneExistentContainer
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|nonExistBlockKey
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"Specified block key does not exist."
argument_list|)
expr_stmt|;
name|blockManager
operator|.
name|getBlock
argument_list|(
name|nonExistBlockKey
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChillModeAllocateBlockFails ()
specifier|public
name|void
name|testChillModeAllocateBlockFails
parameter_list|()
throws|throws
name|IOException
block|{
name|nodeManager
operator|.
name|setChillmode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"Unable to create block while in chill mode"
argument_list|)
expr_stmt|;
name|blockManager
operator|.
name|allocateBlock
argument_list|(
name|DEFAULT_BLOCK_SIZE
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|containerOwner
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

