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
name|HashMultimap
import|;
end_import

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
name|Multimap
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
name|records
operator|.
name|*
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
name|service
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
name|service
operator|.
name|client
operator|.
name|ServiceClient
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
name|exceptions
operator|.
name|SliderException
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
name|utils
operator|.
name|SliderFileSystem
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
name|TemporaryFolder
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
name|*
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|YarnApplicationState
operator|.
name|FINISHED
import|;
end_import

begin_comment
comment|/**  * End to end tests to test deploying services with MiniYarnCluster and a in-JVM  * ZK testing cluster.  */
end_comment

begin_class
DECL|class|TestYarnNativeServices
specifier|public
class|class
name|TestYarnNativeServices
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
name|TestYarnNativeServices
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|tmpFolder
specifier|public
name|TemporaryFolder
name|tmpFolder
init|=
operator|new
name|TemporaryFolder
argument_list|()
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
name|File
name|tmpYarnDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
literal|"tmp"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|tmpYarnDir
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
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|// End-to-end test to use ServiceClient to deploy a service.
comment|// 1. Create a service with 2 components, each of which has 2 containers
comment|// 2. Flex up each component to 3 containers and check the component instance names
comment|// 3. Flex down each component to 1 container and check the component instance names
comment|// 4. Flex up each component to 2 containers and check the component instance names
comment|// 5. Stop the service
comment|// 6. Destroy the service
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|200000
argument_list|)
DECL|method|testCreateFlexStopDestroyService ()
specifier|public
name|void
name|testCreateFlexStopDestroyService
parameter_list|()
throws|throws
name|Exception
block|{
name|setupInternal
argument_list|(
name|NUM_NMS
argument_list|)
expr_stmt|;
name|ServiceClient
name|client
init|=
name|createClient
argument_list|()
decl_stmt|;
name|Service
name|exampleApp
init|=
name|createExampleApplication
argument_list|()
decl_stmt|;
name|client
operator|.
name|actionCreate
argument_list|(
name|exampleApp
argument_list|)
expr_stmt|;
name|SliderFileSystem
name|fileSystem
init|=
operator|new
name|SliderFileSystem
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|appDir
init|=
name|fileSystem
operator|.
name|buildClusterDirPath
argument_list|(
name|exampleApp
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// check app.json is persisted.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|getFS
argument_list|()
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|appDir
argument_list|,
name|exampleApp
operator|.
name|getName
argument_list|()
operator|+
literal|".json"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|waitForAllCompToBeReady
argument_list|(
name|client
argument_list|,
name|exampleApp
argument_list|)
expr_stmt|;
comment|// Flex two components, each from 2 container to 3 containers.
name|flexComponents
argument_list|(
name|client
argument_list|,
name|exampleApp
argument_list|,
literal|3L
argument_list|)
expr_stmt|;
comment|// wait for flex to be completed, increase from 2 to 3 containers.
name|waitForAllCompToBeReady
argument_list|(
name|client
argument_list|,
name|exampleApp
argument_list|)
expr_stmt|;
comment|// check all instances name for each component are in sequential order.
name|checkCompInstancesInOrder
argument_list|(
name|client
argument_list|,
name|exampleApp
argument_list|)
expr_stmt|;
comment|// flex down to 1
name|flexComponents
argument_list|(
name|client
argument_list|,
name|exampleApp
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|waitForAllCompToBeReady
argument_list|(
name|client
argument_list|,
name|exampleApp
argument_list|)
expr_stmt|;
name|checkCompInstancesInOrder
argument_list|(
name|client
argument_list|,
name|exampleApp
argument_list|)
expr_stmt|;
comment|// check component dir and registry are cleaned up.
comment|// flex up again to 2
name|flexComponents
argument_list|(
name|client
argument_list|,
name|exampleApp
argument_list|,
literal|2L
argument_list|)
expr_stmt|;
name|waitForAllCompToBeReady
argument_list|(
name|client
argument_list|,
name|exampleApp
argument_list|)
expr_stmt|;
name|checkCompInstancesInOrder
argument_list|(
name|client
argument_list|,
name|exampleApp
argument_list|)
expr_stmt|;
comment|// stop the service
name|LOG
operator|.
name|info
argument_list|(
literal|"Stop the service"
argument_list|)
expr_stmt|;
name|client
operator|.
name|actionStop
argument_list|(
name|exampleApp
operator|.
name|getName
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ApplicationReport
name|report
init|=
name|client
operator|.
name|getYarnClient
argument_list|()
operator|.
name|getApplicationReport
argument_list|(
name|ApplicationId
operator|.
name|fromString
argument_list|(
name|exampleApp
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// AM unregisters with RM successfully
name|Assert
operator|.
name|assertEquals
argument_list|(
name|FINISHED
argument_list|,
name|report
operator|.
name|getYarnApplicationState
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|FinalApplicationStatus
operator|.
name|ENDED
argument_list|,
name|report
operator|.
name|getFinalApplicationStatus
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Destroy the service"
argument_list|)
expr_stmt|;
comment|//destroy the service and check the app dir is deleted from fs.
name|client
operator|.
name|actionDestroy
argument_list|(
name|exampleApp
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// check the service dir on hdfs (in this case, local fs) are deleted.
name|Assert
operator|.
name|assertFalse
argument_list|(
name|getFS
argument_list|()
operator|.
name|exists
argument_list|(
name|appDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Create compa with 2 containers
comment|// Create compb with 2 containers which depends on compa
comment|// Check containers for compa started before containers for compb
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|200000
argument_list|)
DECL|method|testComponentStartOrder ()
specifier|public
name|void
name|testComponentStartOrder
parameter_list|()
throws|throws
name|Exception
block|{
name|setupInternal
argument_list|(
name|NUM_NMS
argument_list|)
expr_stmt|;
name|ServiceClient
name|client
init|=
name|createClient
argument_list|()
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
name|setName
argument_list|(
literal|"teststartorder"
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
literal|2
argument_list|,
literal|"sleep 1000"
argument_list|)
argument_list|)
expr_stmt|;
name|Component
name|compb
init|=
name|createComponent
argument_list|(
literal|"compb"
argument_list|,
literal|2
argument_list|,
literal|"sleep 1000"
argument_list|)
decl_stmt|;
comment|// Let compb depedends on compa;
name|compb
operator|.
name|setDependencies
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"compa"
argument_list|)
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|addComponent
argument_list|(
name|compb
argument_list|)
expr_stmt|;
name|client
operator|.
name|actionCreate
argument_list|(
name|exampleApp
argument_list|)
expr_stmt|;
name|waitForAllCompToBeReady
argument_list|(
name|client
argument_list|,
name|exampleApp
argument_list|)
expr_stmt|;
comment|// check that containers for compa are launched before containers for compb
name|checkContainerLaunchDependencies
argument_list|(
name|client
argument_list|,
name|exampleApp
argument_list|,
literal|"compa"
argument_list|,
literal|"compb"
argument_list|)
expr_stmt|;
name|client
operator|.
name|actionStop
argument_list|(
name|exampleApp
operator|.
name|getName
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|client
operator|.
name|actionDestroy
argument_list|(
name|exampleApp
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Test to verify recovery of SeviceMaster after RM is restarted.
comment|// 1. Create an example service.
comment|// 2. Restart RM.
comment|// 3. Fail the application attempt.
comment|// 4. Verify ServiceMaster recovers.
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|200000
argument_list|)
DECL|method|testRecoverComponentsAfterRMRestart ()
specifier|public
name|void
name|testRecoverComponentsAfterRMRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RECOVERY_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WORK_PRESERVING_RECOVERY_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_RESOURCEMANAGER_CONNECT_RETRY_INTERVAL_MS
argument_list|,
literal|500L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_MINICLUSTER_FIXED_PORTS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_MINICLUSTER_USE_RPC
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|setupInternal
argument_list|(
name|NUM_NMS
argument_list|)
expr_stmt|;
name|ServiceClient
name|client
init|=
name|createClient
argument_list|()
decl_stmt|;
name|Service
name|exampleApp
init|=
name|createExampleApplication
argument_list|()
decl_stmt|;
name|client
operator|.
name|actionCreate
argument_list|(
name|exampleApp
argument_list|)
expr_stmt|;
name|waitForAllCompToBeReady
argument_list|(
name|client
argument_list|,
name|exampleApp
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Restart the resource manager"
argument_list|)
expr_stmt|;
name|getYarnCluster
argument_list|()
operator|.
name|restartResourceManager
argument_list|(
name|getYarnCluster
argument_list|()
operator|.
name|getActiveRMIndex
argument_list|()
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|getYarnCluster
argument_list|()
operator|.
name|getResourceManager
argument_list|()
operator|.
name|getServiceState
argument_list|()
operator|==
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|service
operator|.
name|Service
operator|.
name|STATE
operator|.
name|STARTED
argument_list|,
literal|2000
argument_list|,
literal|200000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"node managers connected"
argument_list|,
name|getYarnCluster
argument_list|()
operator|.
name|waitForNodeManagersToConnect
argument_list|(
literal|5000
argument_list|)
argument_list|)
expr_stmt|;
name|ApplicationId
name|exampleAppId
init|=
name|ApplicationId
operator|.
name|fromString
argument_list|(
name|exampleApp
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|client
operator|.
name|getYarnClient
argument_list|()
operator|.
name|getApplicationReport
argument_list|(
name|exampleAppId
argument_list|)
operator|.
name|getCurrentApplicationAttemptId
argument_list|()
decl_stmt|;
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|containersBeforeFailure
init|=
name|getContainersForAllComp
argument_list|(
name|client
argument_list|,
name|exampleApp
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Fail the application attempt {}"
argument_list|,
name|applicationAttemptId
argument_list|)
expr_stmt|;
name|client
operator|.
name|getYarnClient
argument_list|()
operator|.
name|failApplicationAttempt
argument_list|(
name|applicationAttemptId
argument_list|)
expr_stmt|;
comment|//wait until attempt 2 is running
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|ApplicationReport
name|ar
init|=
name|client
operator|.
name|getYarnClient
argument_list|()
operator|.
name|getApplicationReport
argument_list|(
name|exampleAppId
argument_list|)
decl_stmt|;
return|return
name|ar
operator|.
name|getCurrentApplicationAttemptId
argument_list|()
operator|.
name|getAttemptId
argument_list|()
operator|==
literal|2
operator|&&
name|ar
operator|.
name|getYarnApplicationState
argument_list|()
operator|==
name|YarnApplicationState
operator|.
name|RUNNING
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"while waiting"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|,
literal|2000
argument_list|,
literal|200000
argument_list|)
expr_stmt|;
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|containersAfterFailure
init|=
name|getContainersForAllComp
argument_list|(
name|client
argument_list|,
name|exampleApp
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"component container affected by restart"
argument_list|,
name|containersBeforeFailure
argument_list|,
name|containersAfterFailure
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stop/destroy service {}"
argument_list|,
name|exampleApp
argument_list|)
expr_stmt|;
name|client
operator|.
name|actionStop
argument_list|(
name|exampleApp
operator|.
name|getName
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|client
operator|.
name|actionDestroy
argument_list|(
name|exampleApp
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Check containers launched are in dependency order
comment|// Get all containers into a list and sort based on container launch time e.g.
comment|// compa-c1, compa-c2, compb-c1, compb-c2;
comment|// check that the container's launch time are align with the dependencies.
DECL|method|checkContainerLaunchDependencies (ServiceClient client, Service exampleApp, String... compOrder)
specifier|private
name|void
name|checkContainerLaunchDependencies
parameter_list|(
name|ServiceClient
name|client
parameter_list|,
name|Service
name|exampleApp
parameter_list|,
name|String
modifier|...
name|compOrder
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|Service
name|retrievedApp
init|=
name|client
operator|.
name|getStatus
argument_list|(
name|exampleApp
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Container
argument_list|>
name|containerList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Component
name|component
range|:
name|retrievedApp
operator|.
name|getComponents
argument_list|()
control|)
block|{
name|containerList
operator|.
name|addAll
argument_list|(
name|component
operator|.
name|getContainers
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// sort based on launchTime
name|containerList
operator|.
name|sort
argument_list|(
parameter_list|(
name|o1
parameter_list|,
name|o2
parameter_list|)
lambda|->
name|o1
operator|.
name|getLaunchTime
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getLaunchTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"containerList: "
operator|+
name|containerList
argument_list|)
expr_stmt|;
comment|// check the containers are in the dependency order.
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|comp
range|:
name|compOrder
control|)
block|{
name|long
name|num
init|=
name|retrievedApp
operator|.
name|getComponent
argument_list|(
name|comp
argument_list|)
operator|.
name|getNumberOfContainers
argument_list|()
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
name|i
operator|++
control|)
block|{
name|String
name|compInstanceName
init|=
name|containerList
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getComponentInstanceName
argument_list|()
decl_stmt|;
name|String
name|compName
init|=
name|compInstanceName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|compInstanceName
operator|.
name|lastIndexOf
argument_list|(
literal|'-'
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|comp
argument_list|,
name|compName
argument_list|)
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
block|}
block|}
DECL|method|flexComponents (ServiceClient client, Service exampleApp, long count)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|flexComponents
parameter_list|(
name|ServiceClient
name|client
parameter_list|,
name|Service
name|exampleApp
parameter_list|,
name|long
name|count
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|compCounts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|compCounts
operator|.
name|put
argument_list|(
literal|"compa"
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|compCounts
operator|.
name|put
argument_list|(
literal|"compb"
argument_list|,
name|count
argument_list|)
expr_stmt|;
comment|// flex will update the persisted conf to reflect latest number of containers.
name|exampleApp
operator|.
name|getComponent
argument_list|(
literal|"compa"
argument_list|)
operator|.
name|setNumberOfContainers
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|getComponent
argument_list|(
literal|"compb"
argument_list|)
operator|.
name|setNumberOfContainers
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|client
operator|.
name|flexByRestService
argument_list|(
name|exampleApp
operator|.
name|getName
argument_list|()
argument_list|,
name|compCounts
argument_list|)
expr_stmt|;
return|return
name|compCounts
return|;
block|}
comment|// Check each component's comp instances name are in sequential order.
comment|// E.g. If there are two instances compA-1 and compA-2
comment|// When flex up to 4 instances, it should be compA-1 , compA-2, compA-3, compA-4
comment|// When flex down to 3 instances,  it should be compA-1 , compA-2, compA-3.
DECL|method|checkCompInstancesInOrder (ServiceClient client, Service exampleApp)
specifier|private
name|void
name|checkCompInstancesInOrder
parameter_list|(
name|ServiceClient
name|client
parameter_list|,
name|Service
name|exampleApp
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|Service
name|service
init|=
name|client
operator|.
name|getStatus
argument_list|(
name|exampleApp
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Component
name|comp
range|:
name|service
operator|.
name|getComponents
argument_list|()
control|)
block|{
name|checkEachCompInstancesInOrder
argument_list|(
name|comp
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkRegistryAndCompDirDeleted ()
specifier|private
name|void
name|checkRegistryAndCompDirDeleted
parameter_list|()
block|{    }
DECL|method|checkEachCompInstancesInOrder (Component component)
specifier|private
name|void
name|checkEachCompInstancesInOrder
parameter_list|(
name|Component
name|component
parameter_list|)
block|{
name|long
name|expectedNumInstances
init|=
name|component
operator|.
name|getNumberOfContainers
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedNumInstances
argument_list|,
name|component
operator|.
name|getContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TreeSet
argument_list|<
name|String
argument_list|>
name|instances
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Container
name|container
range|:
name|component
operator|.
name|getContainers
argument_list|()
control|)
block|{
name|instances
operator|.
name|add
argument_list|(
name|container
operator|.
name|getComponentInstanceName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|instances
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|component
operator|.
name|getName
argument_list|()
operator|+
literal|"-"
operator|+
name|i
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
DECL|method|waitForOneCompToBeReady (ServiceClient client, Service exampleApp, String readyComp)
specifier|private
name|void
name|waitForOneCompToBeReady
parameter_list|(
name|ServiceClient
name|client
parameter_list|,
name|Service
name|exampleApp
parameter_list|,
name|String
name|readyComp
parameter_list|)
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|long
name|numExpectedContainers
init|=
name|exampleApp
operator|.
name|getComponent
argument_list|(
name|readyComp
argument_list|)
operator|.
name|getNumberOfContainers
argument_list|()
decl_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|Service
name|retrievedApp
init|=
name|client
operator|.
name|getStatus
argument_list|(
name|exampleApp
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Component
name|retrievedComp
init|=
name|retrievedApp
operator|.
name|getComponent
argument_list|(
name|readyComp
argument_list|)
decl_stmt|;
if|if
condition|(
name|retrievedComp
operator|.
name|getContainers
argument_list|()
operator|!=
literal|null
operator|&&
name|retrievedComp
operator|.
name|getContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|==
name|numExpectedContainers
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|readyComp
operator|+
literal|" found "
operator|+
name|numExpectedContainers
operator|+
literal|" containers running"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|" Waiting for "
operator|+
name|readyComp
operator|+
literal|"'s containers to be running"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
argument_list|,
literal|2000
argument_list|,
literal|200000
argument_list|)
expr_stmt|;
block|}
comment|// wait until all the containers for all components become ready state
DECL|method|waitForAllCompToBeReady (ServiceClient client, Service exampleApp)
specifier|private
name|void
name|waitForAllCompToBeReady
parameter_list|(
name|ServiceClient
name|client
parameter_list|,
name|Service
name|exampleApp
parameter_list|)
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|int
name|expectedTotalContainers
init|=
name|countTotalContainers
argument_list|(
name|exampleApp
argument_list|)
decl_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|Service
name|retrievedApp
init|=
name|client
operator|.
name|getStatus
argument_list|(
name|exampleApp
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|totalReadyContainers
init|=
literal|0
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Num Components "
operator|+
name|retrievedApp
operator|.
name|getComponents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Component
name|component
range|:
name|retrievedApp
operator|.
name|getComponents
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"looking for  "
operator|+
name|component
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|component
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|component
operator|.
name|getContainers
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|component
operator|.
name|getContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|==
name|exampleApp
operator|.
name|getComponent
argument_list|(
name|component
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getNumberOfContainers
argument_list|()
condition|)
block|{
for|for
control|(
name|Container
name|container
range|:
name|component
operator|.
name|getContainers
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Container state "
operator|+
name|container
operator|.
name|getState
argument_list|()
operator|+
literal|", component "
operator|+
name|component
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|container
operator|.
name|getState
argument_list|()
operator|==
name|ContainerState
operator|.
name|READY
condition|)
block|{
name|totalReadyContainers
operator|++
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Found 1 ready container "
operator|+
name|container
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
name|component
operator|.
name|getName
argument_list|()
operator|+
literal|" Expected number of containers "
operator|+
name|exampleApp
operator|.
name|getComponent
argument_list|(
name|component
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getNumberOfContainers
argument_list|()
operator|+
literal|", current = "
operator|+
name|component
operator|.
name|getContainers
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Exit loop, totalReadyContainers= "
operator|+
name|totalReadyContainers
operator|+
literal|" expected = "
operator|+
name|expectedTotalContainers
argument_list|)
expr_stmt|;
return|return
name|totalReadyContainers
operator|==
name|expectedTotalContainers
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
argument_list|,
literal|2000
argument_list|,
literal|200000
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get all containers of a service.    */
DECL|method|getContainersForAllComp (ServiceClient client, Service example)
specifier|private
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getContainersForAllComp
parameter_list|(
name|ServiceClient
name|client
parameter_list|,
name|Service
name|example
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|allContainers
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
name|Service
name|retrievedApp
init|=
name|client
operator|.
name|getStatus
argument_list|(
name|example
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|retrievedApp
operator|.
name|getComponents
argument_list|()
operator|.
name|forEach
argument_list|(
name|component
lambda|->
block|{
if|if
condition|(
name|component
operator|.
name|getContainers
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|component
operator|.
name|getContainers
argument_list|()
operator|.
name|forEach
argument_list|(
name|container
lambda|->
block|{
name|allContainers
operator|.
name|put
argument_list|(
name|component
operator|.
name|getName
argument_list|()
argument_list|,
name|container
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|allContainers
return|;
block|}
DECL|method|createClient ()
specifier|private
name|ServiceClient
name|createClient
parameter_list|()
throws|throws
name|Exception
block|{
name|ServiceClient
name|client
init|=
operator|new
name|ServiceClient
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Path
name|addJarResource
parameter_list|(
name|String
name|appName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|localResources
parameter_list|)
throws|throws
name|IOException
throws|,
name|SliderException
block|{
comment|// do nothing, the Unit test will use local jars
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
name|client
operator|.
name|init
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|client
return|;
block|}
DECL|method|countTotalContainers (Service service)
specifier|private
name|int
name|countTotalContainers
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
name|int
name|totalContainers
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Component
name|component
range|:
name|service
operator|.
name|getComponents
argument_list|()
control|)
block|{
name|totalContainers
operator|+=
name|component
operator|.
name|getNumberOfContainers
argument_list|()
expr_stmt|;
block|}
return|return
name|totalContainers
return|;
block|}
block|}
end_class

end_unit

