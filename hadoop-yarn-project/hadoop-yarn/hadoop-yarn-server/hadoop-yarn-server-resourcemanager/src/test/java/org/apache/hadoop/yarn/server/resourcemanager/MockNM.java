begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
package|;
end_package

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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|api
operator|.
name|protocolrecords
operator|.
name|NMContainerStatus
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
name|api
operator|.
name|protocolrecords
operator|.
name|NodeHeartbeatRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|NodeHeartbeatResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|RegisterNodeManagerRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|RegisterNodeManagerResponse
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
name|api
operator|.
name|records
operator|.
name|MasterKey
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
name|api
operator|.
name|records
operator|.
name|NodeHealthStatus
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
name|api
operator|.
name|records
operator|.
name|NodeStatus
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
name|utils
operator|.
name|BuilderUtils
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
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
operator|.
name|YarnVersionInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|util
operator|.
name|log
operator|.
name|Log
import|;
end_import

begin_class
DECL|class|MockNM
specifier|public
class|class
name|MockNM
block|{
DECL|field|responseId
specifier|private
name|int
name|responseId
decl_stmt|;
DECL|field|nodeId
specifier|private
name|NodeId
name|nodeId
decl_stmt|;
DECL|field|memory
specifier|private
name|long
name|memory
decl_stmt|;
DECL|field|vCores
specifier|private
name|int
name|vCores
decl_stmt|;
DECL|field|resourceTracker
specifier|private
name|ResourceTrackerService
name|resourceTracker
decl_stmt|;
DECL|field|httpPort
specifier|private
name|int
name|httpPort
init|=
literal|2
decl_stmt|;
DECL|field|currentContainerTokenMasterKey
specifier|private
name|MasterKey
name|currentContainerTokenMasterKey
decl_stmt|;
DECL|field|currentNMTokenMasterKey
specifier|private
name|MasterKey
name|currentNMTokenMasterKey
decl_stmt|;
DECL|field|version
specifier|private
name|String
name|version
decl_stmt|;
DECL|field|containerStats
specifier|private
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerStatus
argument_list|>
name|containerStats
init|=
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|ContainerStatus
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|MockNM (String nodeIdStr, int memory, ResourceTrackerService resourceTracker)
specifier|public
name|MockNM
parameter_list|(
name|String
name|nodeIdStr
parameter_list|,
name|int
name|memory
parameter_list|,
name|ResourceTrackerService
name|resourceTracker
parameter_list|)
block|{
comment|// scale vcores based on the requested memory
name|this
argument_list|(
name|nodeIdStr
argument_list|,
name|memory
argument_list|,
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
operator|(
name|memory
operator|*
name|YarnConfiguration
operator|.
name|DEFAULT_NM_VCORES
operator|)
operator|/
name|YarnConfiguration
operator|.
name|DEFAULT_NM_PMEM_MB
argument_list|)
argument_list|,
name|resourceTracker
argument_list|)
expr_stmt|;
block|}
DECL|method|MockNM (String nodeIdStr, int memory, int vcores, ResourceTrackerService resourceTracker)
specifier|public
name|MockNM
parameter_list|(
name|String
name|nodeIdStr
parameter_list|,
name|int
name|memory
parameter_list|,
name|int
name|vcores
parameter_list|,
name|ResourceTrackerService
name|resourceTracker
parameter_list|)
block|{
name|this
argument_list|(
name|nodeIdStr
argument_list|,
name|memory
argument_list|,
name|vcores
argument_list|,
name|resourceTracker
argument_list|,
name|YarnVersionInfo
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|MockNM (String nodeIdStr, int memory, int vcores, ResourceTrackerService resourceTracker, String version)
specifier|public
name|MockNM
parameter_list|(
name|String
name|nodeIdStr
parameter_list|,
name|int
name|memory
parameter_list|,
name|int
name|vcores
parameter_list|,
name|ResourceTrackerService
name|resourceTracker
parameter_list|,
name|String
name|version
parameter_list|)
block|{
name|this
operator|.
name|memory
operator|=
name|memory
expr_stmt|;
name|this
operator|.
name|vCores
operator|=
name|vcores
expr_stmt|;
name|this
operator|.
name|resourceTracker
operator|=
name|resourceTracker
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|String
index|[]
name|splits
init|=
name|nodeIdStr
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|nodeId
operator|=
name|BuilderUtils
operator|.
name|newNodeId
argument_list|(
name|splits
index|[
literal|0
index|]
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|splits
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getNodeId ()
specifier|public
name|NodeId
name|getNodeId
parameter_list|()
block|{
return|return
name|nodeId
return|;
block|}
DECL|method|getHttpPort ()
specifier|public
name|int
name|getHttpPort
parameter_list|()
block|{
return|return
name|httpPort
return|;
block|}
DECL|method|setHttpPort (int port)
specifier|public
name|void
name|setHttpPort
parameter_list|(
name|int
name|port
parameter_list|)
block|{
name|httpPort
operator|=
name|port
expr_stmt|;
block|}
DECL|method|setResourceTrackerService (ResourceTrackerService resourceTracker)
specifier|public
name|void
name|setResourceTrackerService
parameter_list|(
name|ResourceTrackerService
name|resourceTracker
parameter_list|)
block|{
name|this
operator|.
name|resourceTracker
operator|=
name|resourceTracker
expr_stmt|;
block|}
DECL|method|containerStatus (ContainerStatus containerStatus)
specifier|public
name|void
name|containerStatus
parameter_list|(
name|ContainerStatus
name|containerStatus
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|>
name|conts
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|conts
operator|.
name|put
argument_list|(
name|containerStatus
operator|.
name|getContainerId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|ContainerStatus
index|[]
block|{
name|containerStatus
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|nodeHeartbeat
argument_list|(
name|conts
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|containerIncreaseStatus (Container container)
specifier|public
name|void
name|containerIncreaseStatus
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|Exception
block|{
name|ContainerStatus
name|containerStatus
init|=
name|BuilderUtils
operator|.
name|newContainerStatus
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|,
literal|"Success"
argument_list|,
literal|0
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Container
argument_list|>
name|increasedConts
init|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|container
argument_list|)
decl_stmt|;
name|nodeHeartbeat
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|containerStatus
argument_list|)
argument_list|,
name|increasedConts
argument_list|,
literal|true
argument_list|,
operator|++
name|responseId
argument_list|)
expr_stmt|;
block|}
DECL|method|registerNode ()
specifier|public
name|RegisterNodeManagerResponse
name|registerNode
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|registerNode
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|registerNode ( List<ApplicationId> runningApplications)
specifier|public
name|RegisterNodeManagerResponse
name|registerNode
parameter_list|(
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|runningApplications
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|registerNode
argument_list|(
literal|null
argument_list|,
name|runningApplications
argument_list|)
return|;
block|}
DECL|method|registerNode ( List<NMContainerStatus> containerReports, List<ApplicationId> runningApplications)
specifier|public
name|RegisterNodeManagerResponse
name|registerNode
parameter_list|(
name|List
argument_list|<
name|NMContainerStatus
argument_list|>
name|containerReports
parameter_list|,
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|runningApplications
parameter_list|)
throws|throws
name|Exception
block|{
name|RegisterNodeManagerRequest
name|req
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|RegisterNodeManagerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|req
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|req
operator|.
name|setHttpPort
argument_list|(
name|httpPort
argument_list|)
expr_stmt|;
name|Resource
name|resource
init|=
name|BuilderUtils
operator|.
name|newResource
argument_list|(
name|memory
argument_list|,
name|vCores
argument_list|)
decl_stmt|;
name|req
operator|.
name|setResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|req
operator|.
name|setContainerStatuses
argument_list|(
name|containerReports
argument_list|)
expr_stmt|;
name|req
operator|.
name|setNMVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|req
operator|.
name|setRunningApplications
argument_list|(
name|runningApplications
argument_list|)
expr_stmt|;
name|RegisterNodeManagerResponse
name|registrationResponse
init|=
name|resourceTracker
operator|.
name|registerNodeManager
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|this
operator|.
name|currentContainerTokenMasterKey
operator|=
name|registrationResponse
operator|.
name|getContainerTokenMasterKey
argument_list|()
expr_stmt|;
name|this
operator|.
name|currentNMTokenMasterKey
operator|=
name|registrationResponse
operator|.
name|getNMTokenMasterKey
argument_list|()
expr_stmt|;
name|Resource
name|newResource
init|=
name|registrationResponse
operator|.
name|getResource
argument_list|()
decl_stmt|;
if|if
condition|(
name|newResource
operator|!=
literal|null
condition|)
block|{
name|memory
operator|=
operator|(
name|int
operator|)
name|newResource
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|vCores
operator|=
name|newResource
operator|.
name|getVirtualCores
argument_list|()
expr_stmt|;
block|}
name|containerStats
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|containerReports
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|NMContainerStatus
name|report
range|:
name|containerReports
control|)
block|{
if|if
condition|(
name|report
operator|.
name|getContainerState
argument_list|()
operator|!=
name|ContainerState
operator|.
name|COMPLETE
condition|)
block|{
name|containerStats
operator|.
name|put
argument_list|(
name|report
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|ContainerStatus
operator|.
name|newInstance
argument_list|(
name|report
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|report
operator|.
name|getContainerState
argument_list|()
argument_list|,
name|report
operator|.
name|getDiagnostics
argument_list|()
argument_list|,
name|report
operator|.
name|getContainerExitStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|registrationResponse
return|;
block|}
DECL|method|nodeHeartbeat (boolean isHealthy)
specifier|public
name|NodeHeartbeatResponse
name|nodeHeartbeat
parameter_list|(
name|boolean
name|isHealthy
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|nodeHeartbeat
argument_list|(
name|Collections
operator|.
expr|<
name|ContainerStatus
operator|>
name|emptyList
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|Container
operator|>
name|emptyList
argument_list|()
argument_list|,
name|isHealthy
argument_list|,
operator|++
name|responseId
argument_list|)
return|;
block|}
DECL|method|nodeHeartbeat (ApplicationAttemptId attemptId, long containerId, ContainerState containerState)
specifier|public
name|NodeHeartbeatResponse
name|nodeHeartbeat
parameter_list|(
name|ApplicationAttemptId
name|attemptId
parameter_list|,
name|long
name|containerId
parameter_list|,
name|ContainerState
name|containerState
parameter_list|)
throws|throws
name|Exception
block|{
name|ContainerStatus
name|containerStatus
init|=
name|BuilderUtils
operator|.
name|newContainerStatus
argument_list|(
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|attemptId
argument_list|,
name|containerId
argument_list|)
argument_list|,
name|containerState
argument_list|,
literal|"Success"
argument_list|,
literal|0
argument_list|,
name|BuilderUtils
operator|.
name|newResource
argument_list|(
name|memory
argument_list|,
name|vCores
argument_list|)
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
name|containerStatusList
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|containerStatusList
operator|.
name|add
argument_list|(
name|containerStatus
argument_list|)
expr_stmt|;
name|Log
operator|.
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"ContainerStatus: "
operator|+
name|containerStatus
argument_list|)
expr_stmt|;
return|return
name|nodeHeartbeat
argument_list|(
name|containerStatusList
argument_list|,
name|Collections
operator|.
expr|<
name|Container
operator|>
name|emptyList
argument_list|()
argument_list|,
literal|true
argument_list|,
operator|++
name|responseId
argument_list|)
return|;
block|}
DECL|method|nodeHeartbeat (Map<ApplicationId, List<ContainerStatus>> conts, boolean isHealthy)
specifier|public
name|NodeHeartbeatResponse
name|nodeHeartbeat
parameter_list|(
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|>
name|conts
parameter_list|,
name|boolean
name|isHealthy
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|nodeHeartbeat
argument_list|(
name|conts
argument_list|,
name|isHealthy
argument_list|,
operator|++
name|responseId
argument_list|)
return|;
block|}
DECL|method|nodeHeartbeat (Map<ApplicationId, List<ContainerStatus>> conts, boolean isHealthy, int resId)
specifier|public
name|NodeHeartbeatResponse
name|nodeHeartbeat
parameter_list|(
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|>
name|conts
parameter_list|,
name|boolean
name|isHealthy
parameter_list|,
name|int
name|resId
parameter_list|)
throws|throws
name|Exception
block|{
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
name|updatedStats
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|stats
range|:
name|conts
operator|.
name|values
argument_list|()
control|)
block|{
name|updatedStats
operator|.
name|addAll
argument_list|(
name|stats
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeHeartbeat
argument_list|(
name|updatedStats
argument_list|,
name|Collections
operator|.
expr|<
name|Container
operator|>
name|emptyList
argument_list|()
argument_list|,
name|isHealthy
argument_list|,
name|resId
argument_list|)
return|;
block|}
DECL|method|nodeHeartbeat ( List<ContainerStatus> updatedStats, boolean isHealthy)
specifier|public
name|NodeHeartbeatResponse
name|nodeHeartbeat
parameter_list|(
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|updatedStats
parameter_list|,
name|boolean
name|isHealthy
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|nodeHeartbeat
argument_list|(
name|updatedStats
argument_list|,
name|Collections
operator|.
expr|<
name|Container
operator|>
name|emptyList
argument_list|()
argument_list|,
name|isHealthy
argument_list|,
operator|++
name|responseId
argument_list|)
return|;
block|}
DECL|method|nodeHeartbeat (List<ContainerStatus> updatedStats, List<Container> increasedConts, boolean isHealthy, int resId)
specifier|public
name|NodeHeartbeatResponse
name|nodeHeartbeat
parameter_list|(
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|updatedStats
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|increasedConts
parameter_list|,
name|boolean
name|isHealthy
parameter_list|,
name|int
name|resId
parameter_list|)
throws|throws
name|Exception
block|{
name|NodeHeartbeatRequest
name|req
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|NodeHeartbeatRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|NodeStatus
name|status
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|NodeStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|status
operator|.
name|setResponseId
argument_list|(
name|resId
argument_list|)
expr_stmt|;
name|status
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
name|completedContainers
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ContainerStatus
name|stat
range|:
name|updatedStats
control|)
block|{
if|if
condition|(
name|stat
operator|.
name|getState
argument_list|()
operator|==
name|ContainerState
operator|.
name|COMPLETE
condition|)
block|{
name|completedContainers
operator|.
name|add
argument_list|(
name|stat
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|containerStats
operator|.
name|put
argument_list|(
name|stat
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|stat
argument_list|)
expr_stmt|;
block|}
name|status
operator|.
name|setContainersStatuses
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|(
name|containerStats
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|ContainerId
name|cid
range|:
name|completedContainers
control|)
block|{
name|containerStats
operator|.
name|remove
argument_list|(
name|cid
argument_list|)
expr_stmt|;
block|}
name|status
operator|.
name|setIncreasedContainers
argument_list|(
name|increasedConts
argument_list|)
expr_stmt|;
name|NodeHealthStatus
name|healthStatus
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|NodeHealthStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|healthStatus
operator|.
name|setHealthReport
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|healthStatus
operator|.
name|setIsNodeHealthy
argument_list|(
name|isHealthy
argument_list|)
expr_stmt|;
name|healthStatus
operator|.
name|setLastHealthReportTime
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|status
operator|.
name|setNodeHealthStatus
argument_list|(
name|healthStatus
argument_list|)
expr_stmt|;
name|req
operator|.
name|setNodeStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|req
operator|.
name|setLastKnownContainerTokenMasterKey
argument_list|(
name|this
operator|.
name|currentContainerTokenMasterKey
argument_list|)
expr_stmt|;
name|req
operator|.
name|setLastKnownNMTokenMasterKey
argument_list|(
name|this
operator|.
name|currentNMTokenMasterKey
argument_list|)
expr_stmt|;
name|NodeHeartbeatResponse
name|heartbeatResponse
init|=
name|resourceTracker
operator|.
name|nodeHeartbeat
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|MasterKey
name|masterKeyFromRM
init|=
name|heartbeatResponse
operator|.
name|getContainerTokenMasterKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|masterKeyFromRM
operator|!=
literal|null
operator|&&
name|masterKeyFromRM
operator|.
name|getKeyId
argument_list|()
operator|!=
name|this
operator|.
name|currentContainerTokenMasterKey
operator|.
name|getKeyId
argument_list|()
condition|)
block|{
name|this
operator|.
name|currentContainerTokenMasterKey
operator|=
name|masterKeyFromRM
expr_stmt|;
block|}
name|masterKeyFromRM
operator|=
name|heartbeatResponse
operator|.
name|getNMTokenMasterKey
argument_list|()
expr_stmt|;
if|if
condition|(
name|masterKeyFromRM
operator|!=
literal|null
operator|&&
name|masterKeyFromRM
operator|.
name|getKeyId
argument_list|()
operator|!=
name|this
operator|.
name|currentNMTokenMasterKey
operator|.
name|getKeyId
argument_list|()
condition|)
block|{
name|this
operator|.
name|currentNMTokenMasterKey
operator|=
name|masterKeyFromRM
expr_stmt|;
block|}
name|Resource
name|newResource
init|=
name|heartbeatResponse
operator|.
name|getResource
argument_list|()
decl_stmt|;
if|if
condition|(
name|newResource
operator|!=
literal|null
condition|)
block|{
name|memory
operator|=
name|newResource
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|vCores
operator|=
name|newResource
operator|.
name|getVirtualCores
argument_list|()
expr_stmt|;
block|}
return|return
name|heartbeatResponse
return|;
block|}
DECL|method|getMemory ()
specifier|public
name|long
name|getMemory
parameter_list|()
block|{
return|return
name|memory
return|;
block|}
DECL|method|getvCores ()
specifier|public
name|int
name|getvCores
parameter_list|()
block|{
return|return
name|vCores
return|;
block|}
DECL|method|getVersion ()
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
block|}
end_class

end_unit

