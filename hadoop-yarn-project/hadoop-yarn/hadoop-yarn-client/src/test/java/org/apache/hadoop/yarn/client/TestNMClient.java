begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
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
name|assertFalse
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
name|assertNotNull
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
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|ByteBuffer
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
name|io
operator|.
name|DataOutputBuffer
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
name|Credentials
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|AllocateResponse
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|GetNewApplicationResponse
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|SubmitApplicationRequest
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationReport
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationSubmissionContext
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Container
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerLaunchContext
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerState
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerStatus
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|FinalApplicationStatus
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeReport
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Priority
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Resource
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ResourceRequest
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|YarnApplicationState
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
name|yarn
operator|.
name|client
operator|.
name|AMRMClient
operator|.
name|ContainerRequest
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
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
name|yarn
operator|.
name|server
operator|.
name|MiniYARNCluster
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
name|yarn
operator|.
name|service
operator|.
name|Service
operator|.
name|STATE
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
name|yarn
operator|.
name|util
operator|.
name|Records
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

begin_class
DECL|class|TestNMClient
specifier|public
class|class
name|TestNMClient
block|{
DECL|field|conf
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
DECL|field|yarnCluster
name|MiniYARNCluster
name|yarnCluster
init|=
literal|null
decl_stmt|;
DECL|field|yarnClient
name|YarnClientImpl
name|yarnClient
init|=
literal|null
decl_stmt|;
DECL|field|rmClient
name|AMRMClientImpl
argument_list|<
name|ContainerRequest
argument_list|>
name|rmClient
init|=
literal|null
decl_stmt|;
DECL|field|nmClient
name|NMClientImpl
name|nmClient
init|=
literal|null
decl_stmt|;
DECL|field|nodeReports
name|List
argument_list|<
name|NodeReport
argument_list|>
name|nodeReports
init|=
literal|null
decl_stmt|;
DECL|field|attemptId
name|ApplicationAttemptId
name|attemptId
init|=
literal|null
decl_stmt|;
DECL|field|nodeCount
name|int
name|nodeCount
init|=
literal|3
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
comment|// start minicluster
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|yarnCluster
operator|=
operator|new
name|MiniYARNCluster
argument_list|(
name|TestAMRMClient
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|nodeCount
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|yarnCluster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|yarnCluster
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|yarnCluster
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|,
name|yarnCluster
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
comment|// start rm client
name|yarnClient
operator|=
operator|new
name|YarnClientImpl
argument_list|()
expr_stmt|;
name|yarnClient
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|yarnClient
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|yarnClient
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|,
name|yarnClient
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
comment|// get node info
name|nodeReports
operator|=
name|yarnClient
operator|.
name|getNodeReports
argument_list|()
expr_stmt|;
comment|// submit new app
name|GetNewApplicationResponse
name|newApp
init|=
name|yarnClient
operator|.
name|getNewApplication
argument_list|()
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|newApp
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|ApplicationSubmissionContext
name|appContext
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ApplicationSubmissionContext
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// set the application id
name|appContext
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
comment|// set the application name
name|appContext
operator|.
name|setApplicationName
argument_list|(
literal|"Test"
argument_list|)
expr_stmt|;
comment|// Set the priority for the application master
name|Priority
name|pri
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|appContext
operator|.
name|setPriority
argument_list|(
name|pri
argument_list|)
expr_stmt|;
comment|// Set the queue to which this application is to be submitted in the RM
name|appContext
operator|.
name|setQueue
argument_list|(
literal|"default"
argument_list|)
expr_stmt|;
comment|// Set up the container launch context for the application master
name|ContainerLaunchContext
name|amContainer
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|appContext
operator|.
name|setAMContainerSpec
argument_list|(
name|amContainer
argument_list|)
expr_stmt|;
comment|// unmanaged AM
name|appContext
operator|.
name|setUnmanagedAM
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Create the request to send to the applications manager
name|SubmitApplicationRequest
name|appRequest
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|SubmitApplicationRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|appRequest
operator|.
name|setApplicationSubmissionContext
argument_list|(
name|appContext
argument_list|)
expr_stmt|;
comment|// Submit the application to the applications manager
name|yarnClient
operator|.
name|submitApplication
argument_list|(
name|appContext
argument_list|)
expr_stmt|;
comment|// wait for app to start
name|int
name|iterationsLeft
init|=
literal|30
decl_stmt|;
while|while
condition|(
name|iterationsLeft
operator|>
literal|0
condition|)
block|{
name|ApplicationReport
name|appReport
init|=
name|yarnClient
operator|.
name|getApplicationReport
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|appReport
operator|.
name|getYarnApplicationState
argument_list|()
operator|==
name|YarnApplicationState
operator|.
name|ACCEPTED
condition|)
block|{
name|attemptId
operator|=
name|appReport
operator|.
name|getCurrentApplicationAttemptId
argument_list|()
expr_stmt|;
break|break;
block|}
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
operator|--
name|iterationsLeft
expr_stmt|;
block|}
if|if
condition|(
name|iterationsLeft
operator|==
literal|0
condition|)
block|{
name|fail
argument_list|(
literal|"Application hasn't bee started"
argument_list|)
expr_stmt|;
block|}
comment|// start am rm client
name|rmClient
operator|=
operator|new
name|AMRMClientImpl
argument_list|<
name|ContainerRequest
argument_list|>
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
name|rmClient
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|rmClient
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|rmClient
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|,
name|rmClient
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
comment|// start am nm client
name|nmClient
operator|=
operator|new
name|NMClientImpl
argument_list|()
expr_stmt|;
name|nmClient
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|nmClient
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|nmClient
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|,
name|nmClient
operator|.
name|getServiceState
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
name|rmClient
operator|.
name|stop
argument_list|()
expr_stmt|;
name|yarnClient
operator|.
name|stop
argument_list|()
expr_stmt|;
name|yarnCluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|stopNmClient (boolean stopContainers)
specifier|private
name|void
name|stopNmClient
parameter_list|(
name|boolean
name|stopContainers
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"Null nmClient"
argument_list|,
name|nmClient
argument_list|)
expr_stmt|;
comment|// leave one unclosed
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nmClient
operator|.
name|startedContainers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// default true
name|assertTrue
argument_list|(
name|nmClient
operator|.
name|cleanupRunningContainers
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|nmClient
operator|.
name|cleanupRunningContainersOnStop
argument_list|(
name|stopContainers
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stopContainers
argument_list|,
name|nmClient
operator|.
name|cleanupRunningContainers
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|nmClient
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testNMClientNoCleanupOnStop ()
specifier|public
name|void
name|testNMClientNoCleanupOnStop
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|rmClient
operator|.
name|registerApplicationMaster
argument_list|(
literal|"Host"
argument_list|,
literal|10000
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|testContainerManagement
argument_list|(
name|nmClient
argument_list|,
name|allocateContainers
argument_list|(
name|rmClient
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|rmClient
operator|.
name|unregisterApplicationMaster
argument_list|(
name|FinalApplicationStatus
operator|.
name|SUCCEEDED
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// don't stop the running containers
name|stopNmClient
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|nmClient
operator|.
name|startedContainers
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|//now cleanup
name|nmClient
operator|.
name|cleanupRunningContainers
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|nmClient
operator|.
name|startedContainers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testNMClient ()
specifier|public
name|void
name|testNMClient
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|rmClient
operator|.
name|registerApplicationMaster
argument_list|(
literal|"Host"
argument_list|,
literal|10000
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|testContainerManagement
argument_list|(
name|nmClient
argument_list|,
name|allocateContainers
argument_list|(
name|rmClient
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|rmClient
operator|.
name|unregisterApplicationMaster
argument_list|(
name|FinalApplicationStatus
operator|.
name|SUCCEEDED
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// stop the running containers on close
name|assertFalse
argument_list|(
name|nmClient
operator|.
name|startedContainers
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|nmClient
operator|.
name|cleanupRunningContainersOnStop
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nmClient
operator|.
name|cleanupRunningContainers
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|nmClient
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|allocateContainers ( AMRMClientImpl<ContainerRequest> rmClient, int num)
specifier|private
name|Set
argument_list|<
name|Container
argument_list|>
name|allocateContainers
parameter_list|(
name|AMRMClientImpl
argument_list|<
name|ContainerRequest
argument_list|>
name|rmClient
parameter_list|,
name|int
name|num
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
comment|// setup container request
name|Resource
name|capability
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Priority
name|priority
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|node
init|=
name|nodeReports
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeId
argument_list|()
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|String
name|rack
init|=
name|nodeReports
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getRackName
argument_list|()
decl_stmt|;
name|String
index|[]
name|nodes
init|=
operator|new
name|String
index|[]
block|{
name|node
block|}
decl_stmt|;
name|String
index|[]
name|racks
init|=
operator|new
name|String
index|[]
block|{
name|rack
block|}
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
name|num
condition|;
operator|++
name|i
control|)
block|{
name|rmClient
operator|.
name|addContainerRequest
argument_list|(
operator|new
name|ContainerRequest
argument_list|(
name|capability
argument_list|,
name|nodes
argument_list|,
name|racks
argument_list|,
name|priority
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|containersRequestedAny
init|=
name|rmClient
operator|.
name|remoteRequestsTable
operator|.
name|get
argument_list|(
name|priority
argument_list|)
operator|.
name|get
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
operator|.
name|get
argument_list|(
name|capability
argument_list|)
operator|.
name|remoteRequest
operator|.
name|getNumContainers
argument_list|()
decl_stmt|;
comment|// RM should allocate container within 2 calls to allocate()
name|int
name|allocatedContainerCount
init|=
literal|0
decl_stmt|;
name|int
name|iterationsLeft
init|=
literal|2
decl_stmt|;
name|Set
argument_list|<
name|Container
argument_list|>
name|containers
init|=
operator|new
name|TreeSet
argument_list|<
name|Container
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|allocatedContainerCount
argument_list|<
name|containersRequestedAny
operator|&&
name|iterationsLeft
argument_list|>
literal|0
condition|)
block|{
name|AllocateResponse
name|allocResponse
init|=
name|rmClient
operator|.
name|allocate
argument_list|(
literal|0.1f
argument_list|)
decl_stmt|;
name|allocatedContainerCount
operator|+=
name|allocResponse
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
for|for
control|(
name|Container
name|container
range|:
name|allocResponse
operator|.
name|getAllocatedContainers
argument_list|()
control|)
block|{
name|containers
operator|.
name|add
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allocatedContainerCount
operator|<
name|containersRequestedAny
condition|)
block|{
comment|// sleep to let NM's heartbeat to RM and trigger allocations
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
operator|--
name|iterationsLeft
expr_stmt|;
block|}
return|return
name|containers
return|;
block|}
DECL|method|testContainerManagement (NMClientImpl nmClient, Set<Container> containers)
specifier|private
name|void
name|testContainerManagement
parameter_list|(
name|NMClientImpl
name|nmClient
parameter_list|,
name|Set
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|int
name|size
init|=
name|containers
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Container
name|container
range|:
name|containers
control|)
block|{
comment|// getContainerStatus shouldn't be called before startContainer,
comment|// otherwise, NodeManager cannot find the container
try|try
block|{
name|nmClient
operator|.
name|getContainerStatus
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|container
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|container
operator|.
name|getContainerToken
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception is expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"The thrown exception is not expected"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is not handled by this NodeManager"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// stopContainer shouldn't be called before startContainer,
comment|// otherwise, an exception will be thrown
try|try
block|{
name|nmClient
operator|.
name|stopContainer
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|container
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|container
operator|.
name|getContainerToken
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception is expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is either not started yet or already stopped"
argument_list|)
condition|)
block|{
throw|throw
call|(
name|AssertionError
call|)
argument_list|(
operator|new
name|AssertionError
argument_list|(
literal|"Exception is not expected: "
operator|+
name|e
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
argument_list|)
throw|;
block|}
block|}
name|Credentials
name|ts
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|DataOutputBuffer
name|dob
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|ts
operator|.
name|writeTokenStorageToStream
argument_list|(
name|dob
argument_list|)
expr_stmt|;
name|ByteBuffer
name|securityTokens
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|dob
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|dob
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerLaunchContext
name|clc
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|clc
operator|.
name|setTokens
argument_list|(
name|securityTokens
argument_list|)
expr_stmt|;
try|try
block|{
name|nmClient
operator|.
name|startContainer
argument_list|(
name|container
argument_list|,
name|clc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
call|(
name|AssertionError
call|)
argument_list|(
operator|new
name|AssertionError
argument_list|(
literal|"Exception is not expected: "
operator|+
name|e
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
argument_list|)
throw|;
block|}
comment|// leave one container unclosed
if|if
condition|(
operator|++
name|i
operator|<
name|size
condition|)
block|{
comment|// NodeManager may still need some time to make the container started
name|testGetContainerStatus
argument_list|(
name|container
argument_list|,
name|i
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|,
literal|""
argument_list|,
operator|-
literal|1000
argument_list|)
expr_stmt|;
try|try
block|{
name|nmClient
operator|.
name|stopContainer
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|container
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|container
operator|.
name|getContainerToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
call|(
name|AssertionError
call|)
argument_list|(
operator|new
name|AssertionError
argument_list|(
literal|"Exception is not expected: "
operator|+
name|e
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
argument_list|)
throw|;
block|}
comment|// getContainerStatus can be called after stopContainer
name|testGetContainerStatus
argument_list|(
name|container
argument_list|,
name|i
argument_list|,
name|ContainerState
operator|.
name|COMPLETE
argument_list|,
literal|"Container killed by the ApplicationMaster."
argument_list|,
literal|143
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|sleep (int sleepTime)
specifier|private
name|void
name|sleep
parameter_list|(
name|int
name|sleepTime
parameter_list|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testGetContainerStatus (Container container, int index, ContainerState state, String diagnostics, int exitStatus)
specifier|private
name|void
name|testGetContainerStatus
parameter_list|(
name|Container
name|container
parameter_list|,
name|int
name|index
parameter_list|,
name|ContainerState
name|state
parameter_list|,
name|String
name|diagnostics
parameter_list|,
name|int
name|exitStatus
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|ContainerStatus
name|status
init|=
name|nmClient
operator|.
name|getContainerStatus
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|container
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|container
operator|.
name|getContainerToken
argument_list|()
argument_list|)
decl_stmt|;
comment|// NodeManager may still need some time to get the stable
comment|// container status
if|if
condition|(
name|status
operator|.
name|getState
argument_list|()
operator|==
name|state
condition|)
block|{
name|assertEquals
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|status
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|""
operator|+
name|index
operator|+
literal|": "
operator|+
name|status
operator|.
name|getDiagnostics
argument_list|()
argument_list|,
name|status
operator|.
name|getDiagnostics
argument_list|()
operator|.
name|contains
argument_list|(
name|diagnostics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|exitStatus
argument_list|,
name|status
operator|.
name|getExitStatus
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

