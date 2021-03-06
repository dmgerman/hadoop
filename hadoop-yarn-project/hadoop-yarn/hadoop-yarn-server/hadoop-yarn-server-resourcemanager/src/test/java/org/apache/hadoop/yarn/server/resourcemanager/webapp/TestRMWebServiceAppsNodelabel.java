begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp
package|package
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
name|resourcemanager
operator|.
name|webapp
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
name|fail
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
name|HashSet
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
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
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
name|NodeId
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
name|NodeLabel
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
name|server
operator|.
name|resourcemanager
operator|.
name|MockAM
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
name|resourcemanager
operator|.
name|MockNM
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
name|resourcemanager
operator|.
name|MockRM
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
name|resourcemanager
operator|.
name|MockRMAppSubmissionData
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
name|resourcemanager
operator|.
name|MockRMAppSubmitter
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
name|resourcemanager
operator|.
name|ResourceManager
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
name|resourcemanager
operator|.
name|nodelabels
operator|.
name|RMNodeLabelsManager
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMApp
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|ResourceScheduler
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|capacity
operator|.
name|CapacityScheduler
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|capacity
operator|.
name|CapacitySchedulerConfiguration
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
name|webapp
operator|.
name|GenericExceptionHandler
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
name|webapp
operator|.
name|GuiceServletConfig
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
name|webapp
operator|.
name|JerseyTestBase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONArray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONObject
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Guice
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|servlet
operator|.
name|ServletModule
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|ClientResponse
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|WebResource
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|guice
operator|.
name|spi
operator|.
name|container
operator|.
name|servlet
operator|.
name|GuiceContainer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|test
operator|.
name|framework
operator|.
name|WebAppDescriptor
import|;
end_import

begin_comment
comment|/**  * Tests partition resource usage per application.  *  */
end_comment

begin_class
DECL|class|TestRMWebServiceAppsNodelabel
specifier|public
class|class
name|TestRMWebServiceAppsNodelabel
extends|extends
name|JerseyTestBase
block|{
DECL|field|AM_CONTAINER_MB
specifier|private
specifier|static
specifier|final
name|int
name|AM_CONTAINER_MB
init|=
literal|1024
decl_stmt|;
DECL|field|nodeLabelManager
specifier|private
specifier|static
name|RMNodeLabelsManager
name|nodeLabelManager
decl_stmt|;
DECL|field|rm
specifier|private
specifier|static
name|MockRM
name|rm
decl_stmt|;
DECL|field|csConf
specifier|private
specifier|static
name|CapacitySchedulerConfiguration
name|csConf
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|YarnConfiguration
name|conf
decl_stmt|;
DECL|class|WebServletModule
specifier|private
specifier|static
class|class
name|WebServletModule
extends|extends
name|ServletModule
block|{
DECL|field|LABEL_X
specifier|private
specifier|static
specifier|final
name|String
name|LABEL_X
init|=
literal|"X"
decl_stmt|;
annotation|@
name|Override
DECL|method|configureServlets ()
specifier|protected
name|void
name|configureServlets
parameter_list|()
block|{
name|bind
argument_list|(
name|JAXBContextResolver
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|RMWebServices
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|GenericExceptionHandler
operator|.
name|class
argument_list|)
expr_stmt|;
name|csConf
operator|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
expr_stmt|;
name|setupQueueConfiguration
argument_list|(
name|csConf
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|(
name|csConf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|CapacityScheduler
operator|.
name|class
argument_list|,
name|ResourceScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
name|rm
operator|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|NodeLabel
argument_list|>
name|labels
init|=
operator|new
name|HashSet
argument_list|<
name|NodeLabel
argument_list|>
argument_list|()
decl_stmt|;
name|labels
operator|.
name|add
argument_list|(
name|NodeLabel
operator|.
name|newInstance
argument_list|(
name|LABEL_X
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|nodeLabelManager
operator|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getNodeLabelManager
argument_list|()
expr_stmt|;
name|nodeLabelManager
operator|.
name|addToCluserNodeLabels
argument_list|(
name|labels
argument_list|)
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
argument_list|()
expr_stmt|;
block|}
name|bind
argument_list|(
name|ResourceManager
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|rm
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/*"
argument_list|)
operator|.
name|with
argument_list|(
name|GuiceContainer
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
DECL|method|TestRMWebServiceAppsNodelabel ()
specifier|public
name|TestRMWebServiceAppsNodelabel
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|WebAppDescriptor
operator|.
name|Builder
argument_list|(
literal|"org.apache.hadoop.yarn.server.resourcemanager.webapp"
argument_list|)
operator|.
name|contextListenerClass
argument_list|(
name|GuiceServletConfig
operator|.
name|class
argument_list|)
operator|.
name|filterClass
argument_list|(
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|servlet
operator|.
name|GuiceFilter
operator|.
name|class
argument_list|)
operator|.
name|contextPath
argument_list|(
literal|"jersey-guice-filter"
argument_list|)
operator|.
name|servletPath
argument_list|(
literal|"/"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setupQueueConfiguration ( CapacitySchedulerConfiguration config)
specifier|private
specifier|static
name|void
name|setupQueueConfiguration
parameter_list|(
name|CapacitySchedulerConfiguration
name|config
parameter_list|)
block|{
comment|// Define top-level queues
name|config
operator|.
name|setQueues
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"default"
block|}
argument_list|)
expr_stmt|;
specifier|final
name|String
name|queueA
init|=
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".a"
decl_stmt|;
name|config
operator|.
name|setCapacity
argument_list|(
name|queueA
argument_list|,
literal|50f
argument_list|)
expr_stmt|;
name|config
operator|.
name|setMaximumCapacity
argument_list|(
name|queueA
argument_list|,
literal|50
argument_list|)
expr_stmt|;
specifier|final
name|String
name|defaultQueue
init|=
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".default"
decl_stmt|;
name|config
operator|.
name|setCapacity
argument_list|(
name|defaultQueue
argument_list|,
literal|50f
argument_list|)
expr_stmt|;
name|config
operator|.
name|setCapacityByLabel
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
literal|"X"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|config
operator|.
name|setMaximumCapacityByLabel
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
literal|"X"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
comment|// set for default queue
name|config
operator|.
name|setCapacityByLabel
argument_list|(
name|defaultQueue
argument_list|,
literal|"X"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|config
operator|.
name|setMaximumCapacityByLabel
argument_list|(
name|defaultQueue
argument_list|,
literal|"X"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|GuiceServletConfig
operator|.
name|setInjector
argument_list|(
name|Guice
operator|.
name|createInjector
argument_list|(
operator|new
name|WebServletModule
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppsFinished ()
specifier|public
name|void
name|testAppsFinished
parameter_list|()
throws|throws
name|JSONException
throws|,
name|Exception
block|{
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|amNodeManager
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|2048
argument_list|)
decl_stmt|;
name|amNodeManager
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|RMApp
name|killedApp
init|=
name|MockRMAppSubmitter
operator|.
name|submitWithMemory
argument_list|(
name|AM_CONTAINER_MB
argument_list|,
name|rm
argument_list|)
decl_stmt|;
name|rm
operator|.
name|killApp
argument_list|(
name|killedApp
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|WebResource
name|r
init|=
name|resource
argument_list|()
decl_stmt|;
name|ClientResponse
name|response
init|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"apps"
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|JSONObject
name|json
init|=
name|response
operator|.
name|getEntity
argument_list|(
name|JSONObject
operator|.
name|class
argument_list|)
decl_stmt|;
name|JSONObject
name|apps
init|=
name|json
operator|.
name|getJSONObject
argument_list|(
literal|"apps"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"incorrect number of elements"
argument_list|,
literal|1
argument_list|,
name|apps
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|apps
operator|.
name|getJSONArray
argument_list|(
literal|"app"
argument_list|)
operator|.
name|getJSONObject
argument_list|(
literal|0
argument_list|)
operator|.
name|getJSONObject
argument_list|(
literal|"resourceInfo"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"resourceInfo object shouldn't be available for finished apps"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"resourceInfo shouldn't be available for finished apps"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppsRunning ()
specifier|public
name|void
name|testAppsRunning
parameter_list|()
throws|throws
name|JSONException
throws|,
name|Exception
block|{
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"h1:1234"
argument_list|,
literal|2048
argument_list|)
decl_stmt|;
name|MockNM
name|nm2
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"h2:1235"
argument_list|,
literal|2048
argument_list|)
decl_stmt|;
name|nodeLabelManager
operator|.
name|addLabelsToNode
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"h2"
argument_list|,
literal|1235
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"X"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|MockRMAppSubmissionData
name|data
init|=
name|MockRMAppSubmissionData
operator|.
name|Builder
operator|.
name|createWithMemory
argument_list|(
name|AM_CONTAINER_MB
argument_list|,
name|rm
argument_list|)
operator|.
name|withAppName
argument_list|(
literal|"app"
argument_list|)
operator|.
name|withUser
argument_list|(
literal|"user"
argument_list|)
operator|.
name|withAcls
argument_list|(
literal|null
argument_list|)
operator|.
name|withQueue
argument_list|(
literal|"default"
argument_list|)
operator|.
name|withUnmanagedAM
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RMApp
name|app1
init|=
name|MockRMAppSubmitter
operator|.
name|submit
argument_list|(
name|rm
argument_list|,
name|data
argument_list|)
decl_stmt|;
name|MockAM
name|am1
init|=
name|MockRM
operator|.
name|launchAndRegisterAM
argument_list|(
name|app1
argument_list|,
name|rm
argument_list|,
name|nm1
argument_list|)
decl_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// AM request for resource in partition X
name|am1
operator|.
name|allocate
argument_list|(
literal|"*"
argument_list|,
literal|1024
argument_list|,
literal|1
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|,
literal|"X"
argument_list|)
expr_stmt|;
name|nm2
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|WebResource
name|r
init|=
name|resource
argument_list|()
decl_stmt|;
name|ClientResponse
name|response
init|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"apps"
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|JSONObject
name|json
init|=
name|response
operator|.
name|getEntity
argument_list|(
name|JSONObject
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Verify apps resource
name|JSONObject
name|apps
init|=
name|json
operator|.
name|getJSONObject
argument_list|(
literal|"apps"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"incorrect number of elements"
argument_list|,
literal|1
argument_list|,
name|apps
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|JSONObject
name|jsonObject
init|=
name|apps
operator|.
name|getJSONArray
argument_list|(
literal|"app"
argument_list|)
operator|.
name|getJSONObject
argument_list|(
literal|0
argument_list|)
operator|.
name|getJSONObject
argument_list|(
literal|"resourceInfo"
argument_list|)
decl_stmt|;
name|JSONArray
name|jsonArray
init|=
name|jsonObject
operator|.
name|getJSONArray
argument_list|(
literal|"resourceUsagesByPartition"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Partition expected is 2"
argument_list|,
literal|2
argument_list|,
name|jsonArray
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// Default partition resource
name|JSONObject
name|defaultPartition
init|=
name|jsonArray
operator|.
name|getJSONObject
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|verifyResource
argument_list|(
name|defaultPartition
argument_list|,
literal|""
argument_list|,
name|getResource
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
name|getResource
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
name|getResource
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify resource used for parition x
name|JSONObject
name|paritionX
init|=
name|jsonArray
operator|.
name|getJSONObject
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|verifyResource
argument_list|(
name|paritionX
argument_list|,
literal|"X"
argument_list|,
name|getResource
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
name|getResource
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
name|getResource
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|getResource (int memory, int vcore)
specifier|private
name|String
name|getResource
parameter_list|(
name|int
name|memory
parameter_list|,
name|int
name|vcore
parameter_list|)
block|{
return|return
literal|"{\"memory\":"
operator|+
name|memory
operator|+
literal|",\"vCores\":"
operator|+
name|vcore
operator|+
literal|"}"
return|;
block|}
DECL|method|verifyResource (JSONObject partition, String partitionName, String amused, String used, String reserved)
specifier|private
name|void
name|verifyResource
parameter_list|(
name|JSONObject
name|partition
parameter_list|,
name|String
name|partitionName
parameter_list|,
name|String
name|amused
parameter_list|,
name|String
name|used
parameter_list|,
name|String
name|reserved
parameter_list|)
throws|throws
name|JSONException
block|{
name|JSONObject
name|amusedObject
init|=
operator|(
name|JSONObject
operator|)
name|partition
operator|.
name|get
argument_list|(
literal|"amUsed"
argument_list|)
decl_stmt|;
name|JSONObject
name|usedObject
init|=
operator|(
name|JSONObject
operator|)
name|partition
operator|.
name|get
argument_list|(
literal|"used"
argument_list|)
decl_stmt|;
name|JSONObject
name|reservedObject
init|=
operator|(
name|JSONObject
operator|)
name|partition
operator|.
name|get
argument_list|(
literal|"reserved"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Partition expected"
argument_list|,
name|partitionName
argument_list|,
name|partition
operator|.
name|get
argument_list|(
literal|"partitionName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"partition amused"
argument_list|,
name|amused
argument_list|,
name|getResource
argument_list|(
operator|(
name|int
operator|)
name|amusedObject
operator|.
name|get
argument_list|(
literal|"memory"
argument_list|)
argument_list|,
operator|(
name|int
operator|)
name|amusedObject
operator|.
name|get
argument_list|(
literal|"vCores"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"partition used"
argument_list|,
name|used
argument_list|,
name|getResource
argument_list|(
operator|(
name|int
operator|)
name|usedObject
operator|.
name|get
argument_list|(
literal|"memory"
argument_list|)
argument_list|,
operator|(
name|int
operator|)
name|usedObject
operator|.
name|get
argument_list|(
literal|"vCores"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"partition reserved"
argument_list|,
name|reserved
argument_list|,
name|getResource
argument_list|(
operator|(
name|int
operator|)
name|reservedObject
operator|.
name|get
argument_list|(
literal|"memory"
argument_list|)
argument_list|,
operator|(
name|int
operator|)
name|reservedObject
operator|.
name|get
argument_list|(
literal|"vCores"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|toSet (E... elements)
specifier|private
parameter_list|<
name|E
parameter_list|>
name|Set
argument_list|<
name|E
argument_list|>
name|toSet
parameter_list|(
name|E
modifier|...
name|elements
parameter_list|)
block|{
name|Set
argument_list|<
name|E
argument_list|>
name|set
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|elements
argument_list|)
decl_stmt|;
return|return
name|set
return|;
block|}
block|}
end_class

end_unit

