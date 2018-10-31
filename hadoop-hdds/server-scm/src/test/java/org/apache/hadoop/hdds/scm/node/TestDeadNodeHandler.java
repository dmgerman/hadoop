begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.node
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
name|node
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
name|Arrays
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
name|UUID
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
name|NodeReportProto
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
name|StorageReportProto
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
name|ContainerManager
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
name|ContainerNotFoundException
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
name|ContainerReplica
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
name|ContainerInfo
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
name|placement
operator|.
name|metrics
operator|.
name|SCMNodeMetric
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
name|container
operator|.
name|placement
operator|.
name|metrics
operator|.
name|SCMNodeStat
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
name|exceptions
operator|.
name|SCMException
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
name|scm
operator|.
name|server
operator|.
name|SCMDatanodeHeartbeatDispatcher
operator|.
name|NodeReportFromDatanode
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
name|EventPublisher
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

begin_comment
comment|/**  * Test DeadNodeHandler.  */
end_comment

begin_class
DECL|class|TestDeadNodeHandler
specifier|public
class|class
name|TestDeadNodeHandler
block|{
DECL|field|nodeManager
specifier|private
name|SCMNodeManager
name|nodeManager
decl_stmt|;
DECL|field|containerManager
specifier|private
name|ContainerManager
name|containerManager
decl_stmt|;
DECL|field|nodeReportHandler
specifier|private
name|NodeReportHandler
name|nodeReportHandler
decl_stmt|;
DECL|field|deadNodeHandler
specifier|private
name|DeadNodeHandler
name|deadNodeHandler
decl_stmt|;
DECL|field|publisher
specifier|private
name|EventPublisher
name|publisher
decl_stmt|;
DECL|field|eventQueue
specifier|private
name|EventQueue
name|eventQueue
decl_stmt|;
DECL|field|storageDir
specifier|private
name|String
name|storageDir
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
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|storageDir
operator|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestDeadNodeHandler
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
name|UUID
operator|.
name|randomUUID
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
name|storageDir
argument_list|)
expr_stmt|;
name|eventQueue
operator|=
operator|new
name|EventQueue
argument_list|()
expr_stmt|;
name|nodeManager
operator|=
operator|new
name|SCMNodeManager
argument_list|(
name|conf
argument_list|,
literal|"cluster1"
argument_list|,
literal|null
argument_list|,
name|eventQueue
argument_list|)
expr_stmt|;
name|PipelineManager
name|pipelineManager
init|=
operator|new
name|SCMPipelineManager
argument_list|(
name|conf
argument_list|,
name|nodeManager
argument_list|,
name|eventQueue
argument_list|)
decl_stmt|;
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
name|eventQueue
argument_list|)
expr_stmt|;
name|deadNodeHandler
operator|=
operator|new
name|DeadNodeHandler
argument_list|(
name|nodeManager
argument_list|,
name|containerManager
argument_list|)
expr_stmt|;
name|eventQueue
operator|.
name|addHandler
argument_list|(
name|SCMEvents
operator|.
name|DEAD_NODE
argument_list|,
name|deadNodeHandler
argument_list|)
expr_stmt|;
name|publisher
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|EventPublisher
operator|.
name|class
argument_list|)
expr_stmt|;
name|nodeReportHandler
operator|=
operator|new
name|NodeReportHandler
argument_list|(
name|nodeManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|storageDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOnMessage ()
specifier|public
name|void
name|testOnMessage
parameter_list|()
throws|throws
name|IOException
block|{
comment|//GIVEN
name|DatanodeDetails
name|datanode1
init|=
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
name|DatanodeDetails
name|datanode2
init|=
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
name|DatanodeDetails
name|datanode3
init|=
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
name|String
name|storagePath
init|=
name|GenericTestUtils
operator|.
name|getRandomizedTempPath
argument_list|()
operator|.
name|concat
argument_list|(
literal|"/"
operator|+
name|datanode1
operator|.
name|getUuidString
argument_list|()
argument_list|)
decl_stmt|;
name|StorageReportProto
name|storageOne
init|=
name|TestUtils
operator|.
name|createStorageReport
argument_list|(
name|datanode1
operator|.
name|getUuid
argument_list|()
argument_list|,
name|storagePath
argument_list|,
literal|100
argument_list|,
literal|10
argument_list|,
literal|90
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// Standalone pipeline now excludes the nodes which are already used,
comment|// is the a proper behavior. Adding 9 datanodes for now to make the
comment|// test case happy.
name|nodeManager
operator|.
name|register
argument_list|(
name|datanode1
argument_list|,
name|TestUtils
operator|.
name|createNodeReport
argument_list|(
name|storageOne
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|nodeManager
operator|.
name|register
argument_list|(
name|datanode2
argument_list|,
name|TestUtils
operator|.
name|createNodeReport
argument_list|(
name|storageOne
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|nodeManager
operator|.
name|register
argument_list|(
name|datanode3
argument_list|,
name|TestUtils
operator|.
name|createNodeReport
argument_list|(
name|storageOne
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|nodeManager
operator|.
name|register
argument_list|(
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
argument_list|,
name|TestUtils
operator|.
name|createNodeReport
argument_list|(
name|storageOne
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|nodeManager
operator|.
name|register
argument_list|(
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
argument_list|,
name|TestUtils
operator|.
name|createNodeReport
argument_list|(
name|storageOne
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|nodeManager
operator|.
name|register
argument_list|(
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
argument_list|,
name|TestUtils
operator|.
name|createNodeReport
argument_list|(
name|storageOne
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|nodeManager
operator|.
name|register
argument_list|(
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
argument_list|,
name|TestUtils
operator|.
name|createNodeReport
argument_list|(
name|storageOne
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|nodeManager
operator|.
name|register
argument_list|(
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
argument_list|,
name|TestUtils
operator|.
name|createNodeReport
argument_list|(
name|storageOne
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|nodeManager
operator|.
name|register
argument_list|(
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
argument_list|,
name|TestUtils
operator|.
name|createNodeReport
argument_list|(
name|storageOne
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ContainerInfo
name|container1
init|=
name|TestUtils
operator|.
name|allocateContainer
argument_list|(
name|containerManager
argument_list|)
decl_stmt|;
name|ContainerInfo
name|container2
init|=
name|TestUtils
operator|.
name|allocateContainer
argument_list|(
name|containerManager
argument_list|)
decl_stmt|;
name|ContainerInfo
name|container3
init|=
name|TestUtils
operator|.
name|allocateContainer
argument_list|(
name|containerManager
argument_list|)
decl_stmt|;
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|container1
operator|.
name|containerID
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CREATE
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|container1
operator|.
name|containerID
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CREATED
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|container2
operator|.
name|containerID
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CREATE
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|container2
operator|.
name|containerID
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CREATED
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|container3
operator|.
name|containerID
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CREATE
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|container3
operator|.
name|containerID
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CREATED
argument_list|)
expr_stmt|;
name|registerReplicas
argument_list|(
name|datanode1
argument_list|,
name|container1
argument_list|,
name|container2
argument_list|)
expr_stmt|;
name|registerReplicas
argument_list|(
name|datanode2
argument_list|,
name|container1
argument_list|,
name|container3
argument_list|)
expr_stmt|;
name|registerReplicas
argument_list|(
name|containerManager
argument_list|,
name|container1
argument_list|,
name|datanode1
argument_list|,
name|datanode2
argument_list|)
expr_stmt|;
name|registerReplicas
argument_list|(
name|containerManager
argument_list|,
name|container2
argument_list|,
name|datanode1
argument_list|)
expr_stmt|;
name|registerReplicas
argument_list|(
name|containerManager
argument_list|,
name|container3
argument_list|,
name|datanode2
argument_list|)
expr_stmt|;
name|TestUtils
operator|.
name|closeContainer
argument_list|(
name|containerManager
argument_list|,
name|container1
operator|.
name|containerID
argument_list|()
argument_list|)
expr_stmt|;
name|TestUtils
operator|.
name|closeContainer
argument_list|(
name|containerManager
argument_list|,
name|container2
operator|.
name|containerID
argument_list|()
argument_list|)
expr_stmt|;
name|TestUtils
operator|.
name|closeContainer
argument_list|(
name|containerManager
argument_list|,
name|container3
operator|.
name|containerID
argument_list|()
argument_list|)
expr_stmt|;
name|deadNodeHandler
operator|.
name|onMessage
argument_list|(
name|datanode1
argument_list|,
name|publisher
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ContainerReplica
argument_list|>
name|container1Replicas
init|=
name|containerManager
operator|.
name|getContainerReplicas
argument_list|(
operator|new
name|ContainerID
argument_list|(
name|container1
operator|.
name|getContainerID
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|container1Replicas
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanode2
argument_list|,
name|container1Replicas
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getDatanodeDetails
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ContainerReplica
argument_list|>
name|container2Replicas
init|=
name|containerManager
operator|.
name|getContainerReplicas
argument_list|(
operator|new
name|ContainerID
argument_list|(
name|container2
operator|.
name|getContainerID
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|container2Replicas
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ContainerReplica
argument_list|>
name|container3Replicas
init|=
name|containerManager
operator|.
name|getContainerReplicas
argument_list|(
operator|new
name|ContainerID
argument_list|(
name|container3
operator|.
name|getContainerID
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|container3Replicas
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|datanode2
argument_list|,
name|container3Replicas
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getDatanodeDetails
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStatisticsUpdate ()
specifier|public
name|void
name|testStatisticsUpdate
parameter_list|()
throws|throws
name|Exception
block|{
comment|//GIVEN
name|DatanodeDetails
name|datanode1
init|=
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
name|DatanodeDetails
name|datanode2
init|=
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
name|String
name|storagePath1
init|=
name|GenericTestUtils
operator|.
name|getRandomizedTempPath
argument_list|()
operator|.
name|concat
argument_list|(
literal|"/"
operator|+
name|datanode1
operator|.
name|getUuidString
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|storagePath2
init|=
name|GenericTestUtils
operator|.
name|getRandomizedTempPath
argument_list|()
operator|.
name|concat
argument_list|(
literal|"/"
operator|+
name|datanode2
operator|.
name|getUuidString
argument_list|()
argument_list|)
decl_stmt|;
name|StorageReportProto
name|storageOne
init|=
name|TestUtils
operator|.
name|createStorageReport
argument_list|(
name|datanode1
operator|.
name|getUuid
argument_list|()
argument_list|,
name|storagePath1
argument_list|,
literal|100
argument_list|,
literal|10
argument_list|,
literal|90
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|StorageReportProto
name|storageTwo
init|=
name|TestUtils
operator|.
name|createStorageReport
argument_list|(
name|datanode2
operator|.
name|getUuid
argument_list|()
argument_list|,
name|storagePath2
argument_list|,
literal|200
argument_list|,
literal|20
argument_list|,
literal|180
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|nodeManager
operator|.
name|register
argument_list|(
name|datanode1
argument_list|,
name|TestUtils
operator|.
name|createNodeReport
argument_list|(
name|storageOne
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|nodeManager
operator|.
name|register
argument_list|(
name|datanode2
argument_list|,
name|TestUtils
operator|.
name|createNodeReport
argument_list|(
name|storageTwo
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|nodeReportHandler
operator|.
name|onMessage
argument_list|(
name|getNodeReport
argument_list|(
name|datanode1
argument_list|,
name|storageOne
argument_list|)
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|EventPublisher
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|nodeReportHandler
operator|.
name|onMessage
argument_list|(
name|getNodeReport
argument_list|(
name|datanode2
argument_list|,
name|storageTwo
argument_list|)
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|EventPublisher
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|SCMNodeStat
name|stat
init|=
name|nodeManager
operator|.
name|getStats
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|stat
operator|.
name|getCapacity
argument_list|()
operator|.
name|get
argument_list|()
operator|==
literal|300
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|stat
operator|.
name|getRemaining
argument_list|()
operator|.
name|get
argument_list|()
operator|==
literal|270
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|stat
operator|.
name|getScmUsed
argument_list|()
operator|.
name|get
argument_list|()
operator|==
literal|30
argument_list|)
expr_stmt|;
name|SCMNodeMetric
name|nodeStat
init|=
name|nodeManager
operator|.
name|getNodeStat
argument_list|(
name|datanode1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|nodeStat
operator|.
name|get
argument_list|()
operator|.
name|getCapacity
argument_list|()
operator|.
name|get
argument_list|()
operator|==
literal|100
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|nodeStat
operator|.
name|get
argument_list|()
operator|.
name|getRemaining
argument_list|()
operator|.
name|get
argument_list|()
operator|==
literal|90
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|nodeStat
operator|.
name|get
argument_list|()
operator|.
name|getScmUsed
argument_list|()
operator|.
name|get
argument_list|()
operator|==
literal|10
argument_list|)
expr_stmt|;
comment|//WHEN datanode1 is dead.
name|eventQueue
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|DEAD_NODE
argument_list|,
name|datanode1
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|//THEN statistics in SCM should changed.
name|stat
operator|=
name|nodeManager
operator|.
name|getStats
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|stat
operator|.
name|getCapacity
argument_list|()
operator|.
name|get
argument_list|()
operator|==
literal|200
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|stat
operator|.
name|getRemaining
argument_list|()
operator|.
name|get
argument_list|()
operator|==
literal|180
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|stat
operator|.
name|getScmUsed
argument_list|()
operator|.
name|get
argument_list|()
operator|==
literal|20
argument_list|)
expr_stmt|;
name|nodeStat
operator|=
name|nodeManager
operator|.
name|getNodeStat
argument_list|(
name|datanode1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|nodeStat
operator|.
name|get
argument_list|()
operator|.
name|getCapacity
argument_list|()
operator|.
name|get
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|nodeStat
operator|.
name|get
argument_list|()
operator|.
name|getRemaining
argument_list|()
operator|.
name|get
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|nodeStat
operator|.
name|get
argument_list|()
operator|.
name|getScmUsed
argument_list|()
operator|.
name|get
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOnMessageReplicaFailure ()
specifier|public
name|void
name|testOnMessageReplicaFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|DatanodeDetails
name|datanode1
init|=
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
name|DatanodeDetails
name|datanode2
init|=
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
name|DatanodeDetails
name|datanode3
init|=
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
name|String
name|storagePath
init|=
name|GenericTestUtils
operator|.
name|getRandomizedTempPath
argument_list|()
operator|.
name|concat
argument_list|(
literal|"/"
operator|+
name|datanode1
operator|.
name|getUuidString
argument_list|()
argument_list|)
decl_stmt|;
name|StorageReportProto
name|storageOne
init|=
name|TestUtils
operator|.
name|createStorageReport
argument_list|(
name|datanode1
operator|.
name|getUuid
argument_list|()
argument_list|,
name|storagePath
argument_list|,
literal|100
argument_list|,
literal|10
argument_list|,
literal|90
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|nodeManager
operator|.
name|register
argument_list|(
name|datanode1
argument_list|,
name|TestUtils
operator|.
name|createNodeReport
argument_list|(
name|storageOne
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|nodeManager
operator|.
name|register
argument_list|(
name|datanode2
argument_list|,
name|TestUtils
operator|.
name|createNodeReport
argument_list|(
name|storageOne
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|nodeManager
operator|.
name|register
argument_list|(
name|datanode3
argument_list|,
name|TestUtils
operator|.
name|createNodeReport
argument_list|(
name|storageOne
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|DatanodeDetails
name|dn1
init|=
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
name|GenericTestUtils
operator|.
name|LogCapturer
name|logCapturer
init|=
name|GenericTestUtils
operator|.
name|LogCapturer
operator|.
name|captureLogs
argument_list|(
name|DeadNodeHandler
operator|.
name|getLogger
argument_list|()
argument_list|)
decl_stmt|;
name|nodeReportHandler
operator|.
name|onMessage
argument_list|(
name|getNodeReport
argument_list|(
name|dn1
argument_list|,
name|storageOne
argument_list|)
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|EventPublisher
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|ContainerInfo
name|container1
init|=
name|TestUtils
operator|.
name|allocateContainer
argument_list|(
name|containerManager
argument_list|)
decl_stmt|;
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|container1
operator|.
name|containerID
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CREATE
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|container1
operator|.
name|containerID
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CREATED
argument_list|)
expr_stmt|;
name|TestUtils
operator|.
name|closeContainer
argument_list|(
name|containerManager
argument_list|,
name|container1
operator|.
name|containerID
argument_list|()
argument_list|)
expr_stmt|;
name|registerReplicas
argument_list|(
name|dn1
argument_list|,
name|container1
argument_list|)
expr_stmt|;
name|deadNodeHandler
operator|.
name|onMessage
argument_list|(
name|dn1
argument_list|,
name|eventQueue
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Exception while removing container replica "
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|registerReplicas (ContainerManager containerManager, ContainerInfo container, DatanodeDetails... datanodes)
specifier|private
name|void
name|registerReplicas
parameter_list|(
name|ContainerManager
name|containerManager
parameter_list|,
name|ContainerInfo
name|container
parameter_list|,
name|DatanodeDetails
modifier|...
name|datanodes
parameter_list|)
throws|throws
name|ContainerNotFoundException
block|{
for|for
control|(
name|DatanodeDetails
name|datanode
range|:
name|datanodes
control|)
block|{
name|containerManager
operator|.
name|updateContainerReplica
argument_list|(
operator|new
name|ContainerID
argument_list|(
name|container
operator|.
name|getContainerID
argument_list|()
argument_list|)
argument_list|,
name|ContainerReplica
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerID
argument_list|(
name|container
operator|.
name|containerID
argument_list|()
argument_list|)
operator|.
name|setDatanodeDetails
argument_list|(
name|datanode
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|registerReplicas (DatanodeDetails datanode, ContainerInfo... containers)
specifier|private
name|void
name|registerReplicas
parameter_list|(
name|DatanodeDetails
name|datanode
parameter_list|,
name|ContainerInfo
modifier|...
name|containers
parameter_list|)
throws|throws
name|SCMException
block|{
name|nodeManager
operator|.
name|addDatanodeInContainerMap
argument_list|(
name|datanode
operator|.
name|getUuid
argument_list|()
argument_list|,
name|Arrays
operator|.
name|stream
argument_list|(
name|containers
argument_list|)
operator|.
name|map
argument_list|(
name|container
lambda|->
operator|new
name|ContainerID
argument_list|(
name|container
operator|.
name|getContainerID
argument_list|()
argument_list|)
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
DECL|method|getNodeReport (DatanodeDetails dn, StorageReportProto... reports)
specifier|private
name|NodeReportFromDatanode
name|getNodeReport
parameter_list|(
name|DatanodeDetails
name|dn
parameter_list|,
name|StorageReportProto
modifier|...
name|reports
parameter_list|)
block|{
name|NodeReportProto
name|nodeReportProto
init|=
name|TestUtils
operator|.
name|createNodeReport
argument_list|(
name|reports
argument_list|)
decl_stmt|;
return|return
operator|new
name|NodeReportFromDatanode
argument_list|(
name|dn
argument_list|,
name|nodeReportProto
argument_list|)
return|;
block|}
block|}
end_class

end_unit

