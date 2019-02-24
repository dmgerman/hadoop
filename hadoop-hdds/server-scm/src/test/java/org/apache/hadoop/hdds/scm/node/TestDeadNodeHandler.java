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
name|HddsTestUtils
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
name|node
operator|.
name|states
operator|.
name|NodeNotFoundException
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
name|security
operator|.
name|authentication
operator|.
name|client
operator|.
name|AuthenticationException
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|event
operator|.
name|Level
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
DECL|field|scm
specifier|private
name|StorageContainerManager
name|scm
decl_stmt|;
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
throws|,
name|AuthenticationException
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
name|scm
operator|=
name|HddsTestUtils
operator|.
name|getScm
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|nodeManager
operator|=
operator|(
name|SCMNodeManager
operator|)
name|scm
operator|.
name|getScmNodeManager
argument_list|()
expr_stmt|;
name|containerManager
operator|=
name|scm
operator|.
name|getContainerManager
argument_list|()
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
name|scm
operator|.
name|stop
argument_list|()
expr_stmt|;
name|scm
operator|.
name|join
argument_list|()
expr_stmt|;
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
throws|,
name|NodeNotFoundException
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
name|ContainerInfo
name|container4
init|=
name|TestUtils
operator|.
name|allocateContainer
argument_list|(
name|containerManager
argument_list|)
decl_stmt|;
name|registerContainers
argument_list|(
name|datanode1
argument_list|,
name|container1
argument_list|,
name|container2
argument_list|,
name|container4
argument_list|)
expr_stmt|;
name|registerContainers
argument_list|(
name|datanode2
argument_list|,
name|container1
argument_list|,
name|container2
argument_list|)
expr_stmt|;
name|registerContainers
argument_list|(
name|datanode3
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
argument_list|,
name|datanode2
argument_list|)
expr_stmt|;
name|registerReplicas
argument_list|(
name|containerManager
argument_list|,
name|container3
argument_list|,
name|datanode3
argument_list|)
expr_stmt|;
name|registerReplicas
argument_list|(
name|containerManager
argument_list|,
name|container4
argument_list|,
name|datanode1
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
name|quasiCloseContainer
argument_list|(
name|containerManager
argument_list|,
name|container3
operator|.
name|containerID
argument_list|()
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|DeadNodeHandler
operator|.
name|getLogger
argument_list|()
argument_list|,
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
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
literal|1
argument_list|,
name|container2Replicas
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
name|container2Replicas
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
name|datanode3
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
comment|// Replicate should be fired for container 1 and container 2 as now
comment|// datanode 1 is dead, these 2 will not match with expected replica count
comment|// and their state is one of CLOSED/QUASI_CLOSE.
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
literal|"Replicate Request fired for container "
operator|+
name|container1
operator|.
name|getContainerID
argument_list|()
argument_list|)
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
literal|"Replicate Request fired for container "
operator|+
name|container2
operator|.
name|getContainerID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// as container4 is still in open state, replicate event should not have
comment|// fired for this.
name|Assert
operator|.
name|assertFalse
argument_list|(
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Replicate Request fired for container "
operator|+
name|container4
operator|.
name|getContainerID
argument_list|()
argument_list|)
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
literal|"DeadNode event for a unregistered node"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|registerReplicas (ContainerManager contManager, ContainerInfo container, DatanodeDetails... datanodes)
specifier|private
name|void
name|registerReplicas
parameter_list|(
name|ContainerManager
name|contManager
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
name|contManager
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
name|setContainerState
argument_list|(
name|ContainerReplicaProto
operator|.
name|State
operator|.
name|OPEN
argument_list|)
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
comment|/**    * Update containers available on the datanode.    * @param datanode    * @param containers    * @throws NodeNotFoundException    */
DECL|method|registerContainers (DatanodeDetails datanode, ContainerInfo... containers)
specifier|private
name|void
name|registerContainers
parameter_list|(
name|DatanodeDetails
name|datanode
parameter_list|,
name|ContainerInfo
modifier|...
name|containers
parameter_list|)
throws|throws
name|NodeNotFoundException
block|{
name|nodeManager
operator|.
name|setContainers
argument_list|(
name|datanode
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

