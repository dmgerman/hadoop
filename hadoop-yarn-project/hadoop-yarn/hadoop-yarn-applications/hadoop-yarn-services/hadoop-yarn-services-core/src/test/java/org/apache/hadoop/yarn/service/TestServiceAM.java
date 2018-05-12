begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
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
name|ImmutableMap
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
name|io
operator|.
name|FileUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|curator
operator|.
name|test
operator|.
name|TestingCluster
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
name|Path
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|token
operator|.
name|TokenIdentifier
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
name|ResourceTypes
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
name|ContainerId
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
name|ResourceTypeInfo
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
name|api
operator|.
name|AMRMClient
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
name|api
operator|.
name|async
operator|.
name|AMRMClientAsync
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
name|security
operator|.
name|DockerCredentialTokenIdentifier
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
name|api
operator|.
name|records
operator|.
name|Artifact
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
name|api
operator|.
name|records
operator|.
name|Component
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
name|api
operator|.
name|records
operator|.
name|ResourceInformation
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
name|api
operator|.
name|records
operator|.
name|Service
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
name|component
operator|.
name|ComponentState
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
name|component
operator|.
name|instance
operator|.
name|ComponentInstance
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
name|component
operator|.
name|instance
operator|.
name|ComponentInstanceState
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
name|conf
operator|.
name|YarnServiceConf
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
name|DockerClientConfigHandler
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
name|resource
operator|.
name|ResourceUtils
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|FileWriter
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|TimeoutException
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
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|RegistryConstants
operator|.
name|KEY_REGISTRY_ZK_QUORUM
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
name|assertEquals
import|;
end_import

begin_class
DECL|class|TestServiceAM
specifier|public
class|class
name|TestServiceAM
extends|extends
name|ServiceTestUtils
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestServiceAM
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|basedir
specifier|private
name|File
name|basedir
decl_stmt|;
DECL|field|conf
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
DECL|field|zkCluster
name|TestingCluster
name|zkCluster
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
name|basedir
operator|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
literal|"apps"
argument_list|)
expr_stmt|;
if|if
condition|(
name|basedir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|basedir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|basedir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
name|zkCluster
operator|=
operator|new
name|TestingCluster
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|zkCluster
operator|.
name|start
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KEY_REGISTRY_ZK_QUORUM
argument_list|,
name|zkCluster
operator|.
name|getConnectString
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"ZK cluster: {}"
argument_list|,
name|zkCluster
operator|.
name|getConnectString
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
throws|throws
name|IOException
block|{
if|if
condition|(
name|basedir
operator|!=
literal|null
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|basedir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|zkCluster
operator|!=
literal|null
condition|)
block|{
name|zkCluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Race condition YARN-7486
comment|// 1. Allocate 1 container to compa and wait it to be started
comment|// 2. Fail this container, and in the meanwhile allocate the 2nd container.
comment|// 3. The 2nd container should not be assigned to compa-0 instance, because
comment|//   the compa-0 instance is not stopped yet.
comment|// 4. check compa still has the instance in the pending list.
annotation|@
name|Test
DECL|method|testContainerCompleted ()
specifier|public
name|void
name|testContainerCompleted
parameter_list|()
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|123456
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Service
name|exampleApp
init|=
operator|new
name|Service
argument_list|()
decl_stmt|;
name|exampleApp
operator|.
name|setId
argument_list|(
name|applicationId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|setVersion
argument_list|(
literal|"v1"
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|setName
argument_list|(
literal|"testContainerCompleted"
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|addComponent
argument_list|(
name|createComponent
argument_list|(
literal|"compa"
argument_list|,
literal|1
argument_list|,
literal|"pwd"
argument_list|)
argument_list|)
expr_stmt|;
name|MockServiceAM
name|am
init|=
operator|new
name|MockServiceAM
argument_list|(
name|exampleApp
argument_list|)
decl_stmt|;
name|am
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|am
operator|.
name|start
argument_list|()
expr_stmt|;
name|ComponentInstance
name|compa0
init|=
name|am
operator|.
name|getCompInstance
argument_list|(
literal|"compa"
argument_list|,
literal|"compa-0"
argument_list|)
decl_stmt|;
comment|// allocate a container
name|am
operator|.
name|feedContainerToComp
argument_list|(
name|exampleApp
argument_list|,
literal|1
argument_list|,
literal|"compa"
argument_list|)
expr_stmt|;
name|am
operator|.
name|waitForCompInstanceState
argument_list|(
name|compa0
argument_list|,
name|ComponentInstanceState
operator|.
name|STARTED
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Fail the container 1"
argument_list|)
expr_stmt|;
comment|// fail the container
name|am
operator|.
name|feedFailedContainerToComp
argument_list|(
name|exampleApp
argument_list|,
literal|1
argument_list|,
literal|"compa"
argument_list|)
expr_stmt|;
comment|// allocate the second container immediately, this container will not be
comment|// assigned to comp instance
comment|// because the instance is not yet added to the pending list.
name|am
operator|.
name|feedContainerToComp
argument_list|(
name|exampleApp
argument_list|,
literal|2
argument_list|,
literal|"compa"
argument_list|)
expr_stmt|;
name|am
operator|.
name|waitForCompInstanceState
argument_list|(
name|compa0
argument_list|,
name|ComponentInstanceState
operator|.
name|INIT
argument_list|)
expr_stmt|;
comment|// still 1 pending instance
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|am
operator|.
name|getComponent
argument_list|(
literal|"compa"
argument_list|)
operator|.
name|getPendingInstances
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|am
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|// Test to verify that the containers of previous attempt are not prematurely
comment|// released. These containers are sent by the RM to the AM in the
comment|// heartbeat response.
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|200000
argument_list|)
DECL|method|testContainersFromPreviousAttemptsWithRMRestart ()
specifier|public
name|void
name|testContainersFromPreviousAttemptsWithRMRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Service
name|exampleApp
init|=
operator|new
name|Service
argument_list|()
decl_stmt|;
name|exampleApp
operator|.
name|setId
argument_list|(
name|applicationId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|setVersion
argument_list|(
literal|"v1"
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|setName
argument_list|(
literal|"testContainersRecovers"
argument_list|)
expr_stmt|;
name|String
name|comp1Name
init|=
literal|"comp1"
decl_stmt|;
name|String
name|comp1InstName
init|=
literal|"comp1-0"
decl_stmt|;
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
name|api
operator|.
name|records
operator|.
name|Component
name|compA
init|=
name|createComponent
argument_list|(
name|comp1Name
argument_list|,
literal|1
argument_list|,
literal|"sleep"
argument_list|)
decl_stmt|;
name|exampleApp
operator|.
name|addComponent
argument_list|(
name|compA
argument_list|)
expr_stmt|;
name|MockServiceAM
name|am
init|=
operator|new
name|MockServiceAM
argument_list|(
name|exampleApp
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|am
operator|.
name|createContainerId
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|am
operator|.
name|feedRegistryComponent
argument_list|(
name|containerId
argument_list|,
name|comp1Name
argument_list|,
name|comp1InstName
argument_list|)
expr_stmt|;
name|am
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|am
operator|.
name|start
argument_list|()
expr_stmt|;
name|ComponentInstance
name|comp10
init|=
name|am
operator|.
name|getCompInstance
argument_list|(
name|comp1Name
argument_list|,
name|comp1InstName
argument_list|)
decl_stmt|;
name|am
operator|.
name|feedRecoveredContainer
argument_list|(
name|containerId
argument_list|,
name|comp1Name
argument_list|)
expr_stmt|;
name|am
operator|.
name|waitForCompInstanceState
argument_list|(
name|comp10
argument_list|,
name|ComponentInstanceState
operator|.
name|STARTED
argument_list|)
expr_stmt|;
comment|// 0 pending instance
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|am
operator|.
name|getComponent
argument_list|(
name|comp1Name
argument_list|)
operator|.
name|getPendingInstances
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|am
operator|.
name|getCompInstance
argument_list|(
name|comp1Name
argument_list|,
name|comp1InstName
argument_list|)
operator|.
name|getContainerStatus
argument_list|()
operator|!=
literal|null
argument_list|,
literal|2000
argument_list|,
literal|200000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"container state"
argument_list|,
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
operator|.
name|RUNNING
argument_list|,
name|am
operator|.
name|getCompInstance
argument_list|(
name|comp1Name
argument_list|,
name|comp1InstName
argument_list|)
operator|.
name|getContainerStatus
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|am
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|// Test to verify that the containers of previous attempt are released and the
comment|// component instance is added to the pending queue when the recovery wait
comment|// time interval elapses.
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|200000
argument_list|)
DECL|method|testContainersReleasedWhenExpired ()
specifier|public
name|void
name|testContainersReleasedWhenExpired
parameter_list|()
throws|throws
name|Exception
block|{
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Service
name|exampleApp
init|=
operator|new
name|Service
argument_list|()
decl_stmt|;
name|exampleApp
operator|.
name|setId
argument_list|(
name|applicationId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|setName
argument_list|(
literal|"testContainersRecovers"
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|setVersion
argument_list|(
literal|"v1"
argument_list|)
expr_stmt|;
name|String
name|comp1Name
init|=
literal|"comp1"
decl_stmt|;
name|String
name|comp1InstName
init|=
literal|"comp1-0"
decl_stmt|;
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
name|api
operator|.
name|records
operator|.
name|Component
name|compA
init|=
name|createComponent
argument_list|(
name|comp1Name
argument_list|,
literal|1
argument_list|,
literal|"sleep"
argument_list|)
decl_stmt|;
name|exampleApp
operator|.
name|addComponent
argument_list|(
name|compA
argument_list|)
expr_stmt|;
name|MockServiceAM
name|am
init|=
operator|new
name|MockServiceAM
argument_list|(
name|exampleApp
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|am
operator|.
name|createContainerId
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|am
operator|.
name|feedRegistryComponent
argument_list|(
name|containerId
argument_list|,
name|comp1Name
argument_list|,
name|comp1InstName
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnServiceConf
operator|.
name|CONTAINER_RECOVERY_TIMEOUT_MS
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|am
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|am
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|am
operator|.
name|getComponent
argument_list|(
name|comp1Name
argument_list|)
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|ComponentState
operator|.
name|FLEXING
argument_list|)
argument_list|,
literal|100
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
comment|// 1 pending instance
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|am
operator|.
name|getComponent
argument_list|(
name|comp1Name
argument_list|)
operator|.
name|getPendingInstances
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|am
operator|.
name|feedContainerToComp
argument_list|(
name|exampleApp
argument_list|,
literal|2
argument_list|,
name|comp1Name
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|am
operator|.
name|getCompInstance
argument_list|(
name|comp1Name
argument_list|,
name|comp1InstName
argument_list|)
operator|.
name|getContainerStatus
argument_list|()
operator|!=
literal|null
argument_list|,
literal|2000
argument_list|,
literal|200000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"container state"
argument_list|,
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
operator|.
name|RUNNING
argument_list|,
name|am
operator|.
name|getCompInstance
argument_list|(
name|comp1Name
argument_list|,
name|comp1InstName
argument_list|)
operator|.
name|getContainerStatus
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Test to verify that the AM doesn't wait for containers of a different app
comment|// even though it corresponds to the same service.
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|200000
argument_list|)
DECL|method|testContainersFromDifferentApp ()
specifier|public
name|void
name|testContainersFromDifferentApp
parameter_list|()
throws|throws
name|Exception
block|{
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Service
name|exampleApp
init|=
operator|new
name|Service
argument_list|()
decl_stmt|;
name|exampleApp
operator|.
name|setId
argument_list|(
name|applicationId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|setName
argument_list|(
literal|"testContainersFromDifferentApp"
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|setVersion
argument_list|(
literal|"v1"
argument_list|)
expr_stmt|;
name|String
name|comp1Name
init|=
literal|"comp1"
decl_stmt|;
name|String
name|comp1InstName
init|=
literal|"comp1-0"
decl_stmt|;
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
name|api
operator|.
name|records
operator|.
name|Component
name|compA
init|=
name|createComponent
argument_list|(
name|comp1Name
argument_list|,
literal|1
argument_list|,
literal|"sleep"
argument_list|)
decl_stmt|;
name|exampleApp
operator|.
name|addComponent
argument_list|(
name|compA
argument_list|)
expr_stmt|;
name|MockServiceAM
name|am
init|=
operator|new
name|MockServiceAM
argument_list|(
name|exampleApp
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|am
operator|.
name|createContainerId
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// saves the container in the registry
name|am
operator|.
name|feedRegistryComponent
argument_list|(
name|containerId
argument_list|,
name|comp1Name
argument_list|,
name|comp1InstName
argument_list|)
expr_stmt|;
name|ApplicationId
name|changedAppId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|exampleApp
operator|.
name|setId
argument_list|(
name|changedAppId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|am
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|am
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// 1 pending instance since the container in registry belongs to a different
comment|// app.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|am
operator|.
name|getComponent
argument_list|(
name|comp1Name
argument_list|)
operator|.
name|getPendingInstances
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|am
operator|.
name|feedContainerToComp
argument_list|(
name|exampleApp
argument_list|,
literal|1
argument_list|,
name|comp1Name
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|am
operator|.
name|getCompInstance
argument_list|(
name|comp1Name
argument_list|,
name|comp1InstName
argument_list|)
operator|.
name|getContainerStatus
argument_list|()
operator|!=
literal|null
argument_list|,
literal|2000
argument_list|,
literal|200000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"container state"
argument_list|,
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
operator|.
name|RUNNING
argument_list|,
name|am
operator|.
name|getCompInstance
argument_list|(
name|comp1Name
argument_list|,
name|comp1InstName
argument_list|)
operator|.
name|getContainerStatus
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|am
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testScheduleWithMultipleResourceTypes ()
specifier|public
name|void
name|testScheduleWithMultipleResourceTypes
parameter_list|()
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|123456
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Service
name|exampleApp
init|=
operator|new
name|Service
argument_list|()
decl_stmt|;
name|exampleApp
operator|.
name|setId
argument_list|(
name|applicationId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|setName
argument_list|(
literal|"testScheduleWithMultipleResourceTypes"
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|setVersion
argument_list|(
literal|"v1"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ResourceTypeInfo
argument_list|>
name|resourceTypeInfos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|ResourceUtils
operator|.
name|getResourcesTypeInfo
argument_list|()
argument_list|)
decl_stmt|;
comment|// Add 3rd resource type.
name|resourceTypeInfos
operator|.
name|add
argument_list|(
name|ResourceTypeInfo
operator|.
name|newInstance
argument_list|(
literal|"resource-1"
argument_list|,
literal|""
argument_list|,
name|ResourceTypes
operator|.
name|COUNTABLE
argument_list|)
argument_list|)
expr_stmt|;
comment|// Reinitialize resource types
name|ResourceUtils
operator|.
name|reinitializeResources
argument_list|(
name|resourceTypeInfos
argument_list|)
expr_stmt|;
name|Component
name|serviceCompoent
init|=
name|createComponent
argument_list|(
literal|"compa"
argument_list|,
literal|1
argument_list|,
literal|"pwd"
argument_list|)
decl_stmt|;
name|serviceCompoent
operator|.
name|getResource
argument_list|()
operator|.
name|setResourceInformations
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"resource-1"
argument_list|,
operator|new
name|ResourceInformation
argument_list|()
operator|.
name|value
argument_list|(
literal|3333L
argument_list|)
operator|.
name|unit
argument_list|(
literal|"Gi"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|addComponent
argument_list|(
name|serviceCompoent
argument_list|)
expr_stmt|;
name|MockServiceAM
name|am
init|=
operator|new
name|MockServiceAM
argument_list|(
name|exampleApp
argument_list|)
decl_stmt|;
name|am
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|am
operator|.
name|start
argument_list|()
expr_stmt|;
name|ServiceScheduler
name|serviceScheduler
init|=
name|am
operator|.
name|context
operator|.
name|scheduler
decl_stmt|;
name|AMRMClientAsync
argument_list|<
name|AMRMClient
operator|.
name|ContainerRequest
argument_list|>
name|amrmClientAsync
init|=
name|serviceScheduler
operator|.
name|getAmRMClient
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|AMRMClient
operator|.
name|ContainerRequest
argument_list|>
name|rr
init|=
name|amrmClientAsync
operator|.
name|getMatchingRequests
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rr
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
name|capability
init|=
name|rr
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getCapability
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3333L
argument_list|,
name|capability
operator|.
name|getResourceValue
argument_list|(
literal|"resource-1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Gi"
argument_list|,
name|capability
operator|.
name|getResourceInformation
argument_list|(
literal|"resource-1"
argument_list|)
operator|.
name|getUnits
argument_list|()
argument_list|)
expr_stmt|;
name|am
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRecordTokensForContainers ()
specifier|public
name|void
name|testRecordTokensForContainers
parameter_list|()
throws|throws
name|Exception
block|{
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|123456
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Service
name|exampleApp
init|=
operator|new
name|Service
argument_list|()
decl_stmt|;
name|exampleApp
operator|.
name|setId
argument_list|(
name|applicationId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|setName
argument_list|(
literal|"testContainerCompleted"
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|addComponent
argument_list|(
name|createComponent
argument_list|(
literal|"compa"
argument_list|,
literal|1
argument_list|,
literal|"pwd"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|json
init|=
literal|"{\"auths\": "
operator|+
literal|"{\"https://index.docker.io/v1/\": "
operator|+
literal|"{\"auth\": \"foobarbaz\"},"
operator|+
literal|"\"registry.example.com\": "
operator|+
literal|"{\"auth\": \"bazbarfoo\"}}}"
decl_stmt|;
name|File
name|dockerTmpDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
literal|"docker-tmp"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|dockerTmpDir
argument_list|)
expr_stmt|;
name|dockerTmpDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|String
name|dockerConfig
init|=
name|dockerTmpDir
operator|+
literal|"/config.json"
decl_stmt|;
name|BufferedWriter
name|bw
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|dockerConfig
argument_list|)
argument_list|)
decl_stmt|;
name|bw
operator|.
name|write
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|bw
operator|.
name|close
argument_list|()
expr_stmt|;
name|Credentials
name|dockerCred
init|=
name|DockerClientConfigHandler
operator|.
name|readCredentialsFromConfigFile
argument_list|(
operator|new
name|Path
argument_list|(
name|dockerConfig
argument_list|)
argument_list|,
name|conf
argument_list|,
name|applicationId
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|MockServiceAM
name|am
init|=
operator|new
name|MockServiceAM
argument_list|(
name|exampleApp
argument_list|,
name|dockerCred
argument_list|)
decl_stmt|;
name|ByteBuffer
name|amCredBuffer
init|=
name|am
operator|.
name|recordTokensForContainers
argument_list|()
decl_stmt|;
name|Credentials
name|amCreds
init|=
name|DockerClientConfigHandler
operator|.
name|getCredentialsFromTokensByteBuffer
argument_list|(
name|amCredBuffer
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|amCreds
operator|.
name|numberOfTokens
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|tk
range|:
name|amCreds
operator|.
name|getAllTokens
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|tk
operator|.
name|getKind
argument_list|()
operator|.
name|equals
argument_list|(
name|DockerCredentialTokenIdentifier
operator|.
name|KIND
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|am
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIPChange ()
specifier|public
name|void
name|testIPChange
parameter_list|()
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|123456
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|String
name|comp1Name
init|=
literal|"comp1"
decl_stmt|;
name|String
name|comp1InstName
init|=
literal|"comp1-0"
decl_stmt|;
name|Service
name|exampleApp
init|=
operator|new
name|Service
argument_list|()
decl_stmt|;
name|exampleApp
operator|.
name|setId
argument_list|(
name|applicationId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|setVersion
argument_list|(
literal|"v1"
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|setName
argument_list|(
literal|"testIPChange"
argument_list|)
expr_stmt|;
name|Component
name|comp1
init|=
name|createComponent
argument_list|(
name|comp1Name
argument_list|,
literal|1
argument_list|,
literal|"sleep 60"
argument_list|)
decl_stmt|;
name|comp1
operator|.
name|setArtifact
argument_list|(
operator|new
name|Artifact
argument_list|()
operator|.
name|type
argument_list|(
name|Artifact
operator|.
name|TypeEnum
operator|.
name|DOCKER
argument_list|)
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|addComponent
argument_list|(
name|comp1
argument_list|)
expr_stmt|;
name|MockServiceAM
name|am
init|=
operator|new
name|MockServiceAM
argument_list|(
name|exampleApp
argument_list|)
decl_stmt|;
name|am
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|am
operator|.
name|start
argument_list|()
expr_stmt|;
name|ComponentInstance
name|comp1inst0
init|=
name|am
operator|.
name|getCompInstance
argument_list|(
name|comp1Name
argument_list|,
name|comp1InstName
argument_list|)
decl_stmt|;
comment|// allocate a container
name|am
operator|.
name|feedContainerToComp
argument_list|(
name|exampleApp
argument_list|,
literal|1
argument_list|,
name|comp1Name
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|comp1inst0
operator|.
name|getContainerStatus
argument_list|()
operator|!=
literal|null
argument_list|,
literal|2000
argument_list|,
literal|200000
argument_list|)
expr_stmt|;
comment|// first host status will match the container nodeId
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"localhost"
argument_list|,
name|comp1inst0
operator|.
name|getContainerStatus
argument_list|()
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Change the IP and host"
argument_list|)
expr_stmt|;
comment|// change the container status
name|am
operator|.
name|updateContainerStatus
argument_list|(
name|exampleApp
argument_list|,
literal|1
argument_list|,
name|comp1Name
argument_list|,
literal|"new.host"
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|comp1inst0
operator|.
name|getContainerStatus
argument_list|()
operator|.
name|getHost
argument_list|()
operator|.
name|equals
argument_list|(
literal|"new.host"
argument_list|)
argument_list|,
literal|2000
argument_list|,
literal|200000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Change the IP and host again"
argument_list|)
expr_stmt|;
comment|// change the container status
name|am
operator|.
name|updateContainerStatus
argument_list|(
name|exampleApp
argument_list|,
literal|1
argument_list|,
name|comp1Name
argument_list|,
literal|"newer.host"
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|comp1inst0
operator|.
name|getContainerStatus
argument_list|()
operator|.
name|getHost
argument_list|()
operator|.
name|equals
argument_list|(
literal|"newer.host"
argument_list|)
argument_list|,
literal|2000
argument_list|,
literal|200000
argument_list|)
expr_stmt|;
name|am
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

