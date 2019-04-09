begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container
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
name|container
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
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|LifeCycleEvent
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
operator|.
name|LifeCycleState
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
name|XceiverClientManager
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
name|protocol
operator|.
name|DatanodeDetails
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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReplicaProto
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
name|pipeline
operator|.
name|SCMPipelineManager
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
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|IntStream
import|;
end_import

begin_comment
comment|/**  * Tests for Container ContainerManager.  */
end_comment

begin_class
DECL|class|TestSCMContainerManager
specifier|public
class|class
name|TestSCMContainerManager
block|{
DECL|field|containerManager
specifier|private
specifier|static
name|SCMContainerManager
name|containerManager
decl_stmt|;
DECL|field|nodeManager
specifier|private
specifier|static
name|MockNodeManager
name|nodeManager
decl_stmt|;
DECL|field|pipelineManager
specifier|private
specifier|static
name|PipelineManager
name|pipelineManager
decl_stmt|;
DECL|field|testDir
specifier|private
specifier|static
name|File
name|testDir
decl_stmt|;
DECL|field|xceiverClientManager
specifier|private
specifier|static
name|XceiverClientManager
name|xceiverClientManager
decl_stmt|;
DECL|field|containerOwner
specifier|private
specifier|static
name|String
name|containerOwner
init|=
literal|"OZONE"
decl_stmt|;
DECL|field|random
specifier|private
specifier|static
name|Random
name|random
decl_stmt|;
DECL|field|TIMEOUT
specifier|private
specifier|static
specifier|final
name|long
name|TIMEOUT
init|=
literal|10000
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
name|testDir
operator|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
name|TestSCMContainerManager
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
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
name|testDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_CREATION_LEASE_TIMEOUT
argument_list|,
name|TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
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
name|pipelineManager
operator|=
operator|new
name|SCMPipelineManager
argument_list|(
name|conf
argument_list|,
name|nodeManager
argument_list|,
operator|new
name|EventQueue
argument_list|()
argument_list|)
expr_stmt|;
name|containerManager
operator|=
operator|new
name|SCMContainerManager
argument_list|(
name|conf
argument_list|,
name|nodeManager
argument_list|,
name|pipelineManager
argument_list|,
operator|new
name|EventQueue
argument_list|()
argument_list|)
expr_stmt|;
name|xceiverClientManager
operator|=
operator|new
name|XceiverClientManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|random
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
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
if|if
condition|(
name|containerManager
operator|!=
literal|null
condition|)
block|{
name|containerManager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|pipelineManager
operator|!=
literal|null
condition|)
block|{
name|pipelineManager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
DECL|method|testallocateContainer ()
specifier|public
name|void
name|testallocateContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|ContainerInfo
name|containerInfo
init|=
name|containerManager
operator|.
name|allocateContainer
argument_list|(
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|xceiverClientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|containerOwner
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|containerInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testallocateContainerDistributesAllocation ()
specifier|public
name|void
name|testallocateContainerDistributesAllocation
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* This is a lame test, we should really be testing something like     z-score or make sure that we don't have 3sigma kind of events. Too lazy     to write all that code. This test very lamely tests if we have more than     5 separate nodes  from the list of 10 datanodes that got allocated a     container.      */
name|Set
argument_list|<
name|UUID
argument_list|>
name|pipelineList
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|30
condition|;
name|x
operator|++
control|)
block|{
name|ContainerInfo
name|containerInfo
init|=
name|containerManager
operator|.
name|allocateContainer
argument_list|(
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|xceiverClientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|containerOwner
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|containerInfo
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|containerInfo
operator|.
name|getPipelineID
argument_list|()
argument_list|)
expr_stmt|;
name|pipelineList
operator|.
name|add
argument_list|(
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|containerInfo
operator|.
name|getPipelineID
argument_list|()
argument_list|)
operator|.
name|getFirstNode
argument_list|()
operator|.
name|getUuid
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|pipelineList
operator|.
name|size
argument_list|()
operator|>
literal|5
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetContainer ()
specifier|public
name|void
name|testGetContainer
parameter_list|()
throws|throws
name|IOException
block|{
name|ContainerInfo
name|containerInfo
init|=
name|containerManager
operator|.
name|allocateContainer
argument_list|(
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|xceiverClientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|containerOwner
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|containerInfo
argument_list|)
expr_stmt|;
name|Pipeline
name|pipeline
init|=
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|containerInfo
operator|.
name|getPipelineID
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerInfo
argument_list|,
name|containerManager
operator|.
name|getContainer
argument_list|(
name|containerInfo
operator|.
name|containerID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetContainerWithPipeline ()
specifier|public
name|void
name|testGetContainerWithPipeline
parameter_list|()
throws|throws
name|Exception
block|{
name|ContainerInfo
name|contInfo
init|=
name|containerManager
operator|.
name|allocateContainer
argument_list|(
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|xceiverClientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|containerOwner
argument_list|)
decl_stmt|;
comment|// Add dummy replicas for container.
name|Iterator
argument_list|<
name|DatanodeDetails
argument_list|>
name|nodes
init|=
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|contInfo
operator|.
name|getPipelineID
argument_list|()
argument_list|)
operator|.
name|getNodes
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|DatanodeDetails
name|dn1
init|=
name|nodes
operator|.
name|next
argument_list|()
decl_stmt|;
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|contInfo
operator|.
name|containerID
argument_list|()
argument_list|,
name|LifeCycleEvent
operator|.
name|FINALIZE
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|contInfo
operator|.
name|containerID
argument_list|()
argument_list|,
name|LifeCycleEvent
operator|.
name|CLOSE
argument_list|)
expr_stmt|;
name|ContainerInfo
name|finalContInfo
init|=
name|contInfo
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|containerManager
operator|.
name|getContainerReplicas
argument_list|(
name|finalContInfo
operator|.
name|containerID
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|updateContainerReplica
argument_list|(
name|contInfo
operator|.
name|containerID
argument_list|()
argument_list|,
name|ContainerReplica
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerID
argument_list|(
name|contInfo
operator|.
name|containerID
argument_list|()
argument_list|)
operator|.
name|setContainerState
argument_list|(
name|ContainerReplicaProto
operator|.
name|State
operator|.
name|CLOSED
argument_list|)
operator|.
name|setDatanodeDetails
argument_list|(
name|dn1
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|containerManager
operator|.
name|getContainerReplicas
argument_list|(
name|finalContInfo
operator|.
name|containerID
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|contInfo
operator|=
name|containerManager
operator|.
name|getContainer
argument_list|(
name|contInfo
operator|.
name|containerID
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|contInfo
operator|.
name|getState
argument_list|()
argument_list|,
name|LifeCycleState
operator|.
name|CLOSED
argument_list|)
expr_stmt|;
comment|// After closing the container, we should get the replica and construct
comment|// standalone pipeline. No more ratis pipeline.
name|Set
argument_list|<
name|DatanodeDetails
argument_list|>
name|replicaNodes
init|=
name|containerManager
operator|.
name|getContainerReplicas
argument_list|(
name|contInfo
operator|.
name|containerID
argument_list|()
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|ContainerReplica
operator|::
name|getDatanodeDetails
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|replicaNodes
operator|.
name|contains
argument_list|(
name|dn1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetContainerReplicaWithParallelUpdate ()
specifier|public
name|void
name|testGetContainerReplicaWithParallelUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|testGetContainerWithPipeline
argument_list|()
expr_stmt|;
specifier|final
name|Optional
argument_list|<
name|ContainerID
argument_list|>
name|id
init|=
name|containerManager
operator|.
name|getContainerIDs
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|findFirst
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|id
operator|.
name|isPresent
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ContainerID
name|cId
init|=
name|id
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|ContainerReplica
argument_list|>
name|replica
init|=
name|containerManager
operator|.
name|getContainerReplicas
argument_list|(
name|cId
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|findFirst
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|replica
operator|.
name|isPresent
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ContainerReplica
name|cReplica
init|=
name|replica
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|AtomicBoolean
name|runUpdaterThread
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Thread
name|updaterThread
init|=
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
while|while
condition|(
name|runUpdaterThread
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
name|containerManager
operator|.
name|removeContainerReplica
argument_list|(
name|cId
argument_list|,
name|cReplica
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|updateContainerReplica
argument_list|(
name|cId
argument_list|,
name|cReplica
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ContainerException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Container Exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|updaterThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|updaterThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|IntStream
operator|.
name|range
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
operator|.
name|forEach
argument_list|(
name|i
lambda|->
block|{
try|try
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|containerManager
operator|.
name|getContainerReplicas
argument_list|(
name|cId
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|ContainerReplica
operator|::
name|getDatanodeDetails
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ContainerNotFoundException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Missing Container "
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|runUpdaterThread
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testgetNoneExistentContainer ()
specifier|public
name|void
name|testgetNoneExistentContainer
parameter_list|()
block|{
try|try
block|{
name|containerManager
operator|.
name|getContainer
argument_list|(
name|ContainerID
operator|.
name|valueof
argument_list|(
name|random
operator|.
name|nextInt
argument_list|()
operator|&
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ContainerNotFoundException
name|ex
parameter_list|)
block|{
comment|// Success!
block|}
block|}
annotation|@
name|Test
DECL|method|testCloseContainer ()
specifier|public
name|void
name|testCloseContainer
parameter_list|()
throws|throws
name|IOException
block|{
name|ContainerID
name|id
init|=
name|createContainer
argument_list|()
operator|.
name|containerID
argument_list|()
decl_stmt|;
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|id
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|FINALIZE
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|id
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CLOSE
argument_list|)
expr_stmt|;
name|ContainerInfo
name|closedContainer
init|=
name|containerManager
operator|.
name|getContainer
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|LifeCycleState
operator|.
name|CLOSED
argument_list|,
name|closedContainer
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a container with the given name in SCMContainerManager.    * @throws IOException    */
DECL|method|createContainer ()
specifier|private
name|ContainerInfo
name|createContainer
parameter_list|()
throws|throws
name|IOException
block|{
name|nodeManager
operator|.
name|setChillmode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|containerManager
operator|.
name|allocateContainer
argument_list|(
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|xceiverClientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|containerOwner
argument_list|)
return|;
block|}
block|}
end_class

end_unit

