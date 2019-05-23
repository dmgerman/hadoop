begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.block
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|block
package|;
end_package

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
name|List
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
name|CompletableFuture
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
name|ExecutorService
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
name|Executors
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|HddsConfigKeys
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|hdds
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
name|hdds
operator|.
name|scm
operator|.
name|TestUtils
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
name|hdds
operator|.
name|scm
operator|.
name|safemode
operator|.
name|SCMSafeModeManager
operator|.
name|SafeModeStatus
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|CloseContainerEventHandler
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|ContainerID
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
name|hdds
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|SCMContainerManager
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
name|hdds
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ExcludeList
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
name|hdds
operator|.
name|scm
operator|.
name|events
operator|.
name|SCMEvents
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
name|hdds
operator|.
name|scm
operator|.
name|pipeline
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
name|hdds
operator|.
name|scm
operator|.
name|pipeline
operator|.
name|PipelineManager
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
name|hdds
operator|.
name|scm
operator|.
name|server
operator|.
name|SCMConfigurator
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
name|hdds
operator|.
name|scm
operator|.
name|server
operator|.
name|StorageContainerManager
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
name|hdds
operator|.
name|server
operator|.
name|events
operator|.
name|EventQueue
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
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
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
DECL|field|scm
specifier|private
name|StorageContainerManager
name|scm
decl_stmt|;
DECL|field|mapping
specifier|private
name|SCMContainerManager
name|mapping
decl_stmt|;
DECL|field|nodeManager
specifier|private
name|MockNodeManager
name|nodeManager
decl_stmt|;
DECL|field|pipelineManager
specifier|private
name|PipelineManager
name|pipelineManager
decl_stmt|;
DECL|field|blockManager
specifier|private
name|BlockManagerImpl
name|blockManager
decl_stmt|;
DECL|field|testDir
specifier|private
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
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
decl_stmt|;
DECL|field|type
specifier|private
specifier|static
name|HddsProtos
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
DECL|field|eventQueue
specifier|private
specifier|static
name|EventQueue
name|eventQueue
decl_stmt|;
DECL|field|numContainerPerOwnerInPipeline
specifier|private
name|int
name|numContainerPerOwnerInPipeline
decl_stmt|;
DECL|field|conf
specifier|private
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|safeModeStatus
specifier|private
name|SafeModeStatus
name|safeModeStatus
init|=
operator|new
name|SafeModeStatus
argument_list|(
literal|false
argument_list|)
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
name|Rule
DECL|field|folder
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|()
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
name|SCMTestUtils
operator|.
name|getConf
argument_list|()
expr_stmt|;
name|numContainerPerOwnerInPipeline
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_PIPELINE_OWNER_CONTAINER_COUNT
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_PIPELINE_OWNER_CONTAINER_COUNT_DEFAULT
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|folder
operator|.
name|newFolder
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Override the default Node Manager in SCM with this Mock Node Manager.
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
name|SCMConfigurator
name|configurator
init|=
operator|new
name|SCMConfigurator
argument_list|()
decl_stmt|;
name|configurator
operator|.
name|setScmNodeManager
argument_list|(
name|nodeManager
argument_list|)
expr_stmt|;
name|scm
operator|=
name|TestUtils
operator|.
name|getScm
argument_list|(
name|conf
argument_list|,
name|configurator
argument_list|)
expr_stmt|;
comment|// Initialize these fields so that the tests can pass.
name|mapping
operator|=
operator|(
name|SCMContainerManager
operator|)
name|scm
operator|.
name|getContainerManager
argument_list|()
expr_stmt|;
name|pipelineManager
operator|=
name|scm
operator|.
name|getPipelineManager
argument_list|()
expr_stmt|;
name|blockManager
operator|=
operator|(
name|BlockManagerImpl
operator|)
name|scm
operator|.
name|getScmBlockManager
argument_list|()
expr_stmt|;
name|eventQueue
operator|=
operator|new
name|EventQueue
argument_list|()
expr_stmt|;
name|eventQueue
operator|.
name|addHandler
argument_list|(
name|SCMEvents
operator|.
name|SAFE_MODE_STATUS
argument_list|,
name|scm
operator|.
name|getSafeModeHandler
argument_list|()
argument_list|)
expr_stmt|;
name|eventQueue
operator|.
name|addHandler
argument_list|(
name|SCMEvents
operator|.
name|SAFE_MODE_STATUS
argument_list|,
name|scm
operator|.
name|getSafeModeHandler
argument_list|()
argument_list|)
expr_stmt|;
name|CloseContainerEventHandler
name|closeContainerHandler
init|=
operator|new
name|CloseContainerEventHandler
argument_list|(
name|pipelineManager
argument_list|,
name|mapping
argument_list|)
decl_stmt|;
name|eventQueue
operator|.
name|addHandler
argument_list|(
name|SCMEvents
operator|.
name|CLOSE_CONTAINER
argument_list|,
name|closeContainerHandler
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
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
expr_stmt|;
name|type
operator|=
name|HddsProtos
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
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
expr_stmt|;
name|type
operator|=
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
expr_stmt|;
block|}
block|}
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
block|{
name|scm
operator|.
name|stop
argument_list|()
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
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|SAFE_MODE_STATUS
argument_list|,
name|safeModeStatus
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
operator|!
name|blockManager
operator|.
name|isScmInSafeMode
argument_list|()
return|;
block|}
argument_list|,
literal|10
argument_list|,
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
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
argument_list|,
operator|new
name|ExcludeList
argument_list|()
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
DECL|method|testAllocateBlockInParallel ()
specifier|public
name|void
name|testAllocateBlockInParallel
parameter_list|()
throws|throws
name|Exception
block|{
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|SAFE_MODE_STATUS
argument_list|,
name|safeModeStatus
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
operator|!
name|blockManager
operator|.
name|isScmInSafeMode
argument_list|()
return|;
block|}
argument_list|,
literal|10
argument_list|,
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
name|int
name|threadCount
init|=
literal|20
decl_stmt|;
name|List
argument_list|<
name|ExecutorService
argument_list|>
name|executors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|threadCount
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
name|threadCount
condition|;
name|i
operator|++
control|)
block|{
name|executors
operator|.
name|add
argument_list|(
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|CompletableFuture
argument_list|<
name|AllocatedBlock
argument_list|>
argument_list|>
name|futureList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|threadCount
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
name|threadCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|CompletableFuture
argument_list|<
name|AllocatedBlock
argument_list|>
name|future
init|=
operator|new
name|CompletableFuture
argument_list|<>
argument_list|()
decl_stmt|;
name|CompletableFuture
operator|.
name|supplyAsync
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|future
operator|.
name|complete
argument_list|(
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
argument_list|,
operator|new
name|ExcludeList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|future
operator|.
name|completeExceptionally
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|future
return|;
block|}
argument_list|,
name|executors
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|futureList
operator|.
name|add
argument_list|(
name|future
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|CompletableFuture
operator|.
name|allOf
argument_list|(
name|futureList
operator|.
name|toArray
argument_list|(
operator|new
name|CompletableFuture
index|[
name|futureList
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"testAllocateBlockInParallel failed"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAllocateOversizedBlock ()
specifier|public
name|void
name|testAllocateOversizedBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|SAFE_MODE_STATUS
argument_list|,
name|safeModeStatus
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
operator|!
name|blockManager
operator|.
name|isScmInSafeMode
argument_list|()
return|;
block|}
argument_list|,
literal|10
argument_list|,
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
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
argument_list|,
operator|new
name|ExcludeList
argument_list|()
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAllocateBlockFailureInSafeMode ()
specifier|public
name|void
name|testAllocateBlockFailureInSafeMode
parameter_list|()
throws|throws
name|Exception
block|{
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|SAFE_MODE_STATUS
argument_list|,
operator|new
name|SafeModeStatus
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
name|blockManager
operator|.
name|isScmInSafeMode
argument_list|()
return|;
block|}
argument_list|,
literal|10
argument_list|,
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
comment|// Test1: In safe mode expect an SCMException.
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"SafeModePrecheck failed for "
operator|+
literal|"allocateBlock"
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
argument_list|,
operator|new
name|ExcludeList
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAllocateBlockSucInSafeMode ()
specifier|public
name|void
name|testAllocateBlockSucInSafeMode
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test2: Exit safe mode and then try allocateBock again.
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|SAFE_MODE_STATUS
argument_list|,
name|safeModeStatus
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
return|return
operator|!
name|blockManager
operator|.
name|isScmInSafeMode
argument_list|()
return|;
block|}
argument_list|,
literal|10
argument_list|,
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
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
argument_list|,
operator|new
name|ExcludeList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testMultipleBlockAllocation ()
specifier|public
name|void
name|testMultipleBlockAllocation
parameter_list|()
throws|throws
name|IOException
throws|,
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|SAFE_MODE_STATUS
argument_list|,
name|safeModeStatus
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
operator|!
name|blockManager
operator|.
name|isScmInSafeMode
argument_list|()
argument_list|,
literal|10
argument_list|,
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
name|pipelineManager
operator|.
name|createPipeline
argument_list|(
name|type
argument_list|,
name|factor
argument_list|)
expr_stmt|;
name|pipelineManager
operator|.
name|createPipeline
argument_list|(
name|type
argument_list|,
name|factor
argument_list|)
expr_stmt|;
name|AllocatedBlock
name|allocatedBlock
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
argument_list|,
operator|new
name|ExcludeList
argument_list|()
argument_list|)
decl_stmt|;
comment|// block should be allocated in different pipelines
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
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
argument_list|,
operator|new
name|ExcludeList
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|!
name|block
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|allocatedBlock
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{       }
return|return
literal|false
return|;
block|}
argument_list|,
literal|100
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyNumberOfContainersInPipelines ( int numContainersPerPipeline)
specifier|private
name|boolean
name|verifyNumberOfContainersInPipelines
parameter_list|(
name|int
name|numContainersPerPipeline
parameter_list|)
block|{
try|try
block|{
for|for
control|(
name|Pipeline
name|pipeline
range|:
name|pipelineManager
operator|.
name|getPipelines
argument_list|(
name|type
argument_list|,
name|factor
argument_list|)
control|)
block|{
if|if
condition|(
name|pipelineManager
operator|.
name|getNumberOfContainers
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
operator|!=
name|numContainersPerPipeline
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testMultipleBlockAllocationWithClosedContainer ()
specifier|public
name|void
name|testMultipleBlockAllocationWithClosedContainer
parameter_list|()
throws|throws
name|IOException
throws|,
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|SAFE_MODE_STATUS
argument_list|,
name|safeModeStatus
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
operator|!
name|blockManager
operator|.
name|isScmInSafeMode
argument_list|()
argument_list|,
literal|10
argument_list|,
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
comment|// create pipelines
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodeManager
operator|.
name|getNodes
argument_list|(
name|HddsProtos
operator|.
name|NodeState
operator|.
name|HEALTHY
argument_list|)
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|pipelineManager
operator|.
name|createPipeline
argument_list|(
name|type
argument_list|,
name|factor
argument_list|)
expr_stmt|;
block|}
comment|// wait till each pipeline has the configured number of containers.
comment|// After this each pipeline has numContainerPerOwnerInPipeline containers
comment|// for each owner
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
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
argument_list|,
operator|new
name|ExcludeList
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{       }
return|return
name|verifyNumberOfContainersInPipelines
argument_list|(
name|numContainerPerOwnerInPipeline
argument_list|)
return|;
block|}
argument_list|,
literal|10
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
comment|// close all the containers in all the pipelines
for|for
control|(
name|Pipeline
name|pipeline
range|:
name|pipelineManager
operator|.
name|getPipelines
argument_list|(
name|type
argument_list|,
name|factor
argument_list|)
control|)
block|{
for|for
control|(
name|ContainerID
name|cid
range|:
name|pipelineManager
operator|.
name|getContainersInPipeline
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|)
control|)
block|{
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|CLOSE_CONTAINER
argument_list|,
name|cid
argument_list|)
expr_stmt|;
block|}
block|}
comment|// wait till no containers are left in the pipelines
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|verifyNumberOfContainersInPipelines
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|10
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
comment|// allocate block so that each pipeline has the configured number of
comment|// containers.
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
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
argument_list|,
operator|new
name|ExcludeList
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{       }
return|return
name|verifyNumberOfContainersInPipelines
argument_list|(
name|numContainerPerOwnerInPipeline
argument_list|)
return|;
block|}
argument_list|,
literal|10
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testBlockAllocationWithNoAvailablePipelines ()
specifier|public
name|void
name|testBlockAllocationWithNoAvailablePipelines
parameter_list|()
throws|throws
name|IOException
throws|,
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|SAFE_MODE_STATUS
argument_list|,
name|safeModeStatus
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
operator|!
name|blockManager
operator|.
name|isScmInSafeMode
argument_list|()
argument_list|,
literal|10
argument_list|,
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
for|for
control|(
name|Pipeline
name|pipeline
range|:
name|pipelineManager
operator|.
name|getPipelines
argument_list|()
control|)
block|{
name|pipelineManager
operator|.
name|finalizeAndDestroyPipeline
argument_list|(
name|pipeline
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|pipelineManager
operator|.
name|getPipelines
argument_list|(
name|type
argument_list|,
name|factor
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
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
argument_list|,
operator|new
name|ExcludeList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|pipelineManager
operator|.
name|getPipelines
argument_list|(
name|type
argument_list|,
name|factor
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

