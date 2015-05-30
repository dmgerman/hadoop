begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice
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
name|applicationhistoryservice
package|;
end_package

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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|GetApplicationAttemptReportRequest
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
name|GetApplicationAttemptReportResponse
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
name|GetApplicationAttemptsRequest
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
name|GetApplicationAttemptsResponse
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
name|GetApplicationReportRequest
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
name|GetApplicationReportResponse
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
name|GetApplicationsRequest
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
name|GetApplicationsResponse
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
name|GetContainerReportRequest
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
name|GetContainerReportResponse
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
name|GetContainersRequest
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
name|GetContainersResponse
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
name|ApplicationAttemptReport
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
name|ContainerReport
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
name|security
operator|.
name|ApplicationACLsManager
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
name|timeline
operator|.
name|TimelineDataManager
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
name|timeline
operator|.
name|TimelineStore
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
name|timeline
operator|.
name|security
operator|.
name|TimelineACLsManager
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
name|BeforeClass
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
DECL|class|TestApplicationHistoryClientService
specifier|public
class|class
name|TestApplicationHistoryClientService
block|{
DECL|field|clientService
specifier|private
specifier|static
name|ApplicationHistoryClientService
name|clientService
decl_stmt|;
DECL|field|dataManager
specifier|private
specifier|static
name|TimelineDataManager
name|dataManager
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|TimelineStore
name|store
init|=
name|TestApplicationHistoryManagerOnTimelineStore
operator|.
name|createStore
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|TimelineACLsManager
name|aclsManager
init|=
operator|new
name|TimelineACLsManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|dataManager
operator|=
operator|new
name|TimelineDataManager
argument_list|(
name|store
argument_list|,
name|aclsManager
argument_list|)
expr_stmt|;
name|ApplicationACLsManager
name|appAclsManager
init|=
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|ApplicationHistoryManagerOnTimelineStore
name|historyManager
init|=
operator|new
name|ApplicationHistoryManagerOnTimelineStore
argument_list|(
name|dataManager
argument_list|,
name|appAclsManager
argument_list|)
decl_stmt|;
name|historyManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|historyManager
operator|.
name|start
argument_list|()
expr_stmt|;
name|clientService
operator|=
operator|new
name|ApplicationHistoryClientService
argument_list|(
name|historyManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testApplicationReport ()
specifier|public
name|void
name|testApplicationReport
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|ApplicationId
name|appId
init|=
literal|null
decl_stmt|;
name|appId
operator|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|GetApplicationReportRequest
name|request
init|=
name|GetApplicationReportRequest
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|)
decl_stmt|;
name|GetApplicationReportResponse
name|response
init|=
name|clientService
operator|.
name|getApplicationReport
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|ApplicationReport
name|appReport
init|=
name|response
operator|.
name|getApplicationReport
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|appReport
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|123
argument_list|,
name|appReport
operator|.
name|getApplicationResourceUsageReport
argument_list|()
operator|.
name|getMemorySeconds
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|345
argument_list|,
name|appReport
operator|.
name|getApplicationResourceUsageReport
argument_list|()
operator|.
name|getVcoreSeconds
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"application_0_0001"
argument_list|,
name|appReport
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test app type"
argument_list|,
name|appReport
operator|.
name|getApplicationType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test queue"
argument_list|,
name|appReport
operator|.
name|getQueue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testApplications ()
specifier|public
name|void
name|testApplications
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|ApplicationId
name|appId
init|=
literal|null
decl_stmt|;
name|appId
operator|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId1
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|GetApplicationsRequest
name|request
init|=
name|GetApplicationsRequest
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|GetApplicationsResponse
name|response
init|=
name|clientService
operator|.
name|getApplications
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|appReport
init|=
name|response
operator|.
name|getApplicationList
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|appReport
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appId
argument_list|,
name|appReport
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appId1
argument_list|,
name|appReport
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Create a historyManager, and set the max_apps can be loaded
comment|// as 1.
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|APPLICATION_HISTORY_MAX_APPS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ApplicationHistoryManagerOnTimelineStore
name|historyManager2
init|=
operator|new
name|ApplicationHistoryManagerOnTimelineStore
argument_list|(
name|dataManager
argument_list|,
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
argument_list|)
decl_stmt|;
name|historyManager2
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|historyManager2
operator|.
name|start
argument_list|()
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
name|ApplicationHistoryClientService
name|clientService2
init|=
operator|new
name|ApplicationHistoryClientService
argument_list|(
name|historyManager2
argument_list|)
decl_stmt|;
name|response
operator|=
name|clientService2
operator|.
name|getApplications
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|appReport
operator|=
name|response
operator|.
name|getApplicationList
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|appReport
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|appReport
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
comment|// Expected to get the appReport for application with appId1
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appId1
argument_list|,
name|appReport
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testApplicationAttemptReport ()
specifier|public
name|void
name|testApplicationAttemptReport
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|GetApplicationAttemptReportRequest
name|request
init|=
name|GetApplicationAttemptReportRequest
operator|.
name|newInstance
argument_list|(
name|appAttemptId
argument_list|)
decl_stmt|;
name|GetApplicationAttemptReportResponse
name|response
init|=
name|clientService
operator|.
name|getApplicationAttemptReport
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|ApplicationAttemptReport
name|attemptReport
init|=
name|response
operator|.
name|getApplicationAttemptReport
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|attemptReport
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"appattempt_0_0001_000001"
argument_list|,
name|attemptReport
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testApplicationAttempts ()
specifier|public
name|void
name|testApplicationAttempts
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId1
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|GetApplicationAttemptsRequest
name|request
init|=
name|GetApplicationAttemptsRequest
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|)
decl_stmt|;
name|GetApplicationAttemptsResponse
name|response
init|=
name|clientService
operator|.
name|getApplicationAttempts
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ApplicationAttemptReport
argument_list|>
name|attemptReports
init|=
name|response
operator|.
name|getApplicationAttemptList
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|attemptReports
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appAttemptId
argument_list|,
name|attemptReports
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appAttemptId1
argument_list|,
name|attemptReports
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainerReport ()
specifier|public
name|void
name|testContainerReport
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|GetContainerReportRequest
name|request
init|=
name|GetContainerReportRequest
operator|.
name|newInstance
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|GetContainerReportResponse
name|response
init|=
name|clientService
operator|.
name|getContainerReport
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|ContainerReport
name|container
init|=
name|response
operator|.
name|getContainerReport
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerId
argument_list|,
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"http://0.0.0.0:8188/applicationhistory/logs/"
operator|+
literal|"test host:100/container_0_0001_01_000001/"
operator|+
literal|"container_0_0001_01_000001/user1"
argument_list|,
name|container
operator|.
name|getLogUrl
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainers ()
specifier|public
name|void
name|testContainers
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId1
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|GetContainersRequest
name|request
init|=
name|GetContainersRequest
operator|.
name|newInstance
argument_list|(
name|appAttemptId
argument_list|)
decl_stmt|;
name|GetContainersResponse
name|response
init|=
name|clientService
operator|.
name|getContainers
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ContainerReport
argument_list|>
name|containers
init|=
name|response
operator|.
name|getContainerList
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|containers
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerId
argument_list|,
name|containers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerId1
argument_list|,
name|containers
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

