begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|UUID
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
name|lang3
operator|.
name|RandomUtils
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerWithPipeline
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
name|MiniOzoneCluster
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
name|OzoneConsts
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
name|scm
operator|.
name|XceiverClientManager
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
import|import
name|java
operator|.
name|util
operator|.
name|NavigableSet
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

begin_comment
comment|/**  * Tests for ContainerStateManager.  */
end_comment

begin_class
DECL|class|TestContainerStateManagerIntegration
specifier|public
class|class
name|TestContainerStateManagerIntegration
block|{
DECL|field|conf
specifier|private
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniOzoneCluster
name|cluster
decl_stmt|;
DECL|field|xceiverClientManager
specifier|private
name|XceiverClientManager
name|xceiverClientManager
decl_stmt|;
DECL|field|scm
specifier|private
name|StorageContainerManager
name|scm
decl_stmt|;
DECL|field|containerManager
specifier|private
name|ContainerManager
name|containerManager
decl_stmt|;
DECL|field|containerStateManager
specifier|private
name|ContainerStateManager
name|containerStateManager
decl_stmt|;
DECL|field|containerOwner
specifier|private
name|String
name|containerOwner
init|=
literal|"OZONE"
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
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|cluster
operator|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|setNumDatanodes
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitTobeOutOfChillMode
argument_list|()
expr_stmt|;
name|xceiverClientManager
operator|=
operator|new
name|XceiverClientManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|scm
operator|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
expr_stmt|;
name|containerManager
operator|=
name|scm
operator|.
name|getContainerManager
argument_list|()
expr_stmt|;
name|containerStateManager
operator|=
operator|(
operator|(
name|SCMContainerManager
operator|)
name|containerManager
operator|)
operator|.
name|getContainerStateManager
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanUp ()
specifier|public
name|void
name|cleanUp
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
block|}
block|}
annotation|@
name|Test
DECL|method|testAllocateContainer ()
specifier|public
name|void
name|testAllocateContainer
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Allocate a container and verify the container info
name|ContainerWithPipeline
name|container1
init|=
name|scm
operator|.
name|getClientProtocolServer
argument_list|()
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
name|ContainerInfo
name|info
init|=
name|containerStateManager
operator|.
name|getMatchingContainer
argument_list|(
name|OzoneConsts
operator|.
name|GB
operator|*
literal|3
argument_list|,
name|containerOwner
argument_list|,
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
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|container1
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|info
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerOwner
argument_list|,
name|info
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|info
operator|.
name|getReplicationType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|xceiverClientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|info
operator|.
name|getReplicationFactor
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
argument_list|,
name|info
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check there are two containers in ALLOCATED state after allocation
name|ContainerWithPipeline
name|container2
init|=
name|scm
operator|.
name|getClientProtocolServer
argument_list|()
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
name|int
name|numContainers
init|=
name|containerStateManager
operator|.
name|getMatchingContainerIDs
argument_list|(
name|containerOwner
argument_list|,
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
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
name|container1
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|container2
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|numContainers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainerStateManagerRestart ()
specifier|public
name|void
name|testContainerStateManagerRestart
parameter_list|()
throws|throws
name|IOException
throws|,
name|TimeoutException
throws|,
name|InterruptedException
block|{
comment|// Allocate 5 containers in ALLOCATED state and 5 in CREATING state
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|ContainerWithPipeline
name|container
init|=
name|scm
operator|.
name|getClientProtocolServer
argument_list|()
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
if|if
condition|(
name|i
operator|>=
literal|5
condition|)
block|{
name|scm
operator|.
name|getContainerManager
argument_list|()
operator|.
name|updateContainerState
argument_list|(
name|container
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|containerID
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|FINALIZE
argument_list|)
expr_stmt|;
block|}
block|}
name|cluster
operator|.
name|restartStorageContainerManager
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|result
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getContainerManager
argument_list|()
operator|.
name|listContainer
argument_list|(
literal|null
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|long
name|matchCount
init|=
name|result
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|info
lambda|->
name|info
operator|.
name|getOwner
argument_list|()
operator|.
name|equals
argument_list|(
name|containerOwner
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|info
lambda|->
name|info
operator|.
name|getReplicationType
argument_list|()
operator|==
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|info
lambda|->
name|info
operator|.
name|getReplicationFactor
argument_list|()
operator|==
name|xceiverClientManager
operator|.
name|getFactor
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|info
lambda|->
name|info
operator|.
name|getState
argument_list|()
operator|==
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
argument_list|)
operator|.
name|count
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|matchCount
argument_list|)
expr_stmt|;
name|matchCount
operator|=
name|result
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|info
lambda|->
name|info
operator|.
name|getOwner
argument_list|()
operator|.
name|equals
argument_list|(
name|containerOwner
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|info
lambda|->
name|info
operator|.
name|getReplicationType
argument_list|()
operator|==
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|info
lambda|->
name|info
operator|.
name|getReplicationFactor
argument_list|()
operator|==
name|xceiverClientManager
operator|.
name|getFactor
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|info
lambda|->
name|info
operator|.
name|getState
argument_list|()
operator|==
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|CLOSING
argument_list|)
operator|.
name|count
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|matchCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetMatchingContainer ()
specifier|public
name|void
name|testGetMatchingContainer
parameter_list|()
throws|throws
name|IOException
block|{
name|ContainerWithPipeline
name|container1
init|=
name|scm
operator|.
name|getClientProtocolServer
argument_list|()
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
name|ContainerInfo
name|info
init|=
name|containerStateManager
operator|.
name|getMatchingContainer
argument_list|(
name|OzoneConsts
operator|.
name|GB
operator|*
literal|3
argument_list|,
name|containerOwner
argument_list|,
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
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|container1
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|info
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerWithPipeline
name|container2
init|=
name|scm
operator|.
name|getClientProtocolServer
argument_list|()
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
name|info
operator|=
name|containerStateManager
operator|.
name|getMatchingContainer
argument_list|(
name|OzoneConsts
operator|.
name|GB
operator|*
literal|3
argument_list|,
name|containerOwner
argument_list|,
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
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
argument_list|)
expr_stmt|;
comment|// space has already been allocated in container1, now container 2 should
comment|// be chosen.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|container2
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|info
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
comment|// now we have to get container1
name|info
operator|=
name|containerStateManager
operator|.
name|getMatchingContainer
argument_list|(
name|OzoneConsts
operator|.
name|GB
operator|*
literal|3
argument_list|,
name|containerOwner
argument_list|,
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
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|container1
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|info
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpdateContainerState ()
specifier|public
name|void
name|testUpdateContainerState
parameter_list|()
throws|throws
name|IOException
block|{
name|NavigableSet
argument_list|<
name|ContainerID
argument_list|>
name|containerList
init|=
name|containerStateManager
operator|.
name|getMatchingContainerIDs
argument_list|(
name|containerOwner
argument_list|,
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
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
argument_list|)
decl_stmt|;
name|int
name|containers
init|=
name|containerList
operator|==
literal|null
condition|?
literal|0
else|:
name|containerList
operator|.
name|size
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|containers
argument_list|)
expr_stmt|;
comment|// Allocate container1 and update its state from
comment|// OPEN -> CLOSING -> CLOSED -> DELETING -> DELETED
name|ContainerWithPipeline
name|container1
init|=
name|scm
operator|.
name|getClientProtocolServer
argument_list|()
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
name|containers
operator|=
name|containerStateManager
operator|.
name|getMatchingContainerIDs
argument_list|(
name|containerOwner
argument_list|,
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
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|containers
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|container1
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|containerID
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|FINALIZE
argument_list|)
expr_stmt|;
name|containers
operator|=
name|containerStateManager
operator|.
name|getMatchingContainerIDs
argument_list|(
name|containerOwner
argument_list|,
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
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|CLOSING
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|containers
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|container1
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|containerID
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CLOSE
argument_list|)
expr_stmt|;
name|containers
operator|=
name|containerStateManager
operator|.
name|getMatchingContainerIDs
argument_list|(
name|containerOwner
argument_list|,
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
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|CLOSED
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|containers
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|container1
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|containerID
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|DELETE
argument_list|)
expr_stmt|;
name|containers
operator|=
name|containerStateManager
operator|.
name|getMatchingContainerIDs
argument_list|(
name|containerOwner
argument_list|,
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
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|DELETING
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|containers
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|container1
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|containerID
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CLEANUP
argument_list|)
expr_stmt|;
name|containers
operator|=
name|containerStateManager
operator|.
name|getMatchingContainerIDs
argument_list|(
name|containerOwner
argument_list|,
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
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|DELETED
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|containers
argument_list|)
expr_stmt|;
comment|// Allocate container1 and update its state from
comment|// OPEN -> CLOSING -> CLOSED
name|ContainerWithPipeline
name|container3
init|=
name|scm
operator|.
name|getClientProtocolServer
argument_list|()
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
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|container3
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|containerID
argument_list|()
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
name|container3
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|containerID
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CLOSE
argument_list|)
expr_stmt|;
name|containers
operator|=
name|containerStateManager
operator|.
name|getMatchingContainerIDs
argument_list|(
name|containerOwner
argument_list|,
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
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|CLOSED
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|containers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReplicaMap ()
specifier|public
name|void
name|testReplicaMap
parameter_list|()
throws|throws
name|Exception
block|{
name|DatanodeDetails
name|dn1
init|=
name|DatanodeDetails
operator|.
name|newBuilder
argument_list|()
operator|.
name|setHostName
argument_list|(
literal|"host1"
argument_list|)
operator|.
name|setIpAddress
argument_list|(
literal|"1.1.1.1"
argument_list|)
operator|.
name|setUuid
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|DatanodeDetails
name|dn2
init|=
name|DatanodeDetails
operator|.
name|newBuilder
argument_list|()
operator|.
name|setHostName
argument_list|(
literal|"host2"
argument_list|)
operator|.
name|setIpAddress
argument_list|(
literal|"2.2.2.2"
argument_list|)
operator|.
name|setUuid
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Test 1: no replica's exist
name|ContainerID
name|containerID
init|=
name|ContainerID
operator|.
name|valueof
argument_list|(
name|RandomUtils
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ContainerReplica
argument_list|>
name|replicaSet
decl_stmt|;
try|try
block|{
name|containerStateManager
operator|.
name|getContainerReplicas
argument_list|(
name|containerID
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
comment|// expected.
block|}
name|ContainerWithPipeline
name|container
init|=
name|scm
operator|.
name|getClientProtocolServer
argument_list|()
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
name|ContainerID
name|id
init|=
name|container
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|containerID
argument_list|()
decl_stmt|;
comment|// Test 2: Add replica nodes and then test
name|ContainerReplica
name|replicaOne
init|=
name|ContainerReplica
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerID
argument_list|(
name|id
argument_list|)
operator|.
name|setContainerState
argument_list|(
name|ContainerReplicaProto
operator|.
name|State
operator|.
name|OPEN
argument_list|)
operator|.
name|setDatanodeDetails
argument_list|(
name|dn1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ContainerReplica
name|replicaTwo
init|=
name|ContainerReplica
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerID
argument_list|(
name|id
argument_list|)
operator|.
name|setContainerState
argument_list|(
name|ContainerReplicaProto
operator|.
name|State
operator|.
name|OPEN
argument_list|)
operator|.
name|setDatanodeDetails
argument_list|(
name|dn2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|containerStateManager
operator|.
name|updateContainerReplica
argument_list|(
name|id
argument_list|,
name|replicaOne
argument_list|)
expr_stmt|;
name|containerStateManager
operator|.
name|updateContainerReplica
argument_list|(
name|id
argument_list|,
name|replicaTwo
argument_list|)
expr_stmt|;
name|replicaSet
operator|=
name|containerStateManager
operator|.
name|getContainerReplicas
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|replicaSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|replicaSet
operator|.
name|contains
argument_list|(
name|replicaOne
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|replicaSet
operator|.
name|contains
argument_list|(
name|replicaTwo
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test 3: Remove one replica node and then test
name|containerStateManager
operator|.
name|removeContainerReplica
argument_list|(
name|id
argument_list|,
name|replicaOne
argument_list|)
expr_stmt|;
name|replicaSet
operator|=
name|containerStateManager
operator|.
name|getContainerReplicas
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|replicaSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|replicaSet
operator|.
name|contains
argument_list|(
name|replicaOne
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|replicaSet
operator|.
name|contains
argument_list|(
name|replicaTwo
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test 3: Remove second replica node and then test
name|containerStateManager
operator|.
name|removeContainerReplica
argument_list|(
name|id
argument_list|,
name|replicaTwo
argument_list|)
expr_stmt|;
name|replicaSet
operator|=
name|containerStateManager
operator|.
name|getContainerReplicas
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|replicaSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|replicaSet
operator|.
name|contains
argument_list|(
name|replicaOne
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|replicaSet
operator|.
name|contains
argument_list|(
name|replicaTwo
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test 4: Re-insert dn1
name|containerStateManager
operator|.
name|updateContainerReplica
argument_list|(
name|id
argument_list|,
name|replicaOne
argument_list|)
expr_stmt|;
name|replicaSet
operator|=
name|containerStateManager
operator|.
name|getContainerReplicas
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|replicaSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|replicaSet
operator|.
name|contains
argument_list|(
name|replicaOne
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|replicaSet
operator|.
name|contains
argument_list|(
name|replicaTwo
argument_list|)
argument_list|)
expr_stmt|;
comment|// Re-insert dn2
name|containerStateManager
operator|.
name|updateContainerReplica
argument_list|(
name|id
argument_list|,
name|replicaTwo
argument_list|)
expr_stmt|;
name|replicaSet
operator|=
name|containerStateManager
operator|.
name|getContainerReplicas
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|replicaSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|replicaSet
operator|.
name|contains
argument_list|(
name|replicaOne
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|replicaSet
operator|.
name|contains
argument_list|(
name|replicaTwo
argument_list|)
argument_list|)
expr_stmt|;
comment|// Re-insert dn1
name|containerStateManager
operator|.
name|updateContainerReplica
argument_list|(
name|id
argument_list|,
name|replicaOne
argument_list|)
expr_stmt|;
name|replicaSet
operator|=
name|containerStateManager
operator|.
name|getContainerReplicas
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|replicaSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|replicaSet
operator|.
name|contains
argument_list|(
name|replicaOne
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|replicaSet
operator|.
name|contains
argument_list|(
name|replicaTwo
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

