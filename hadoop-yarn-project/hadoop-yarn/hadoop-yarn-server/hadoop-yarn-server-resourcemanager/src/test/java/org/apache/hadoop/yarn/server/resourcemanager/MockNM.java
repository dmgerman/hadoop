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
name|util
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
specifier|final
name|int
name|memory
decl_stmt|;
DECL|field|resourceTracker
specifier|private
name|ResourceTrackerService
name|resourceTracker
decl_stmt|;
DECL|field|httpPort
specifier|private
specifier|final
name|int
name|httpPort
init|=
literal|2
decl_stmt|;
DECL|field|currentMasterKey
specifier|private
name|MasterKey
name|currentMasterKey
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
name|this
operator|.
name|memory
operator|=
name|memory
expr_stmt|;
name|this
operator|.
name|resourceTracker
operator|=
name|resourceTracker
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
name|Records
operator|.
name|newRecord
argument_list|(
name|NodeId
operator|.
name|class
argument_list|)
expr_stmt|;
name|nodeId
operator|.
name|setHost
argument_list|(
name|splits
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|nodeId
operator|.
name|setPort
argument_list|(
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
DECL|method|setResourceTrackerService (ResourceTrackerService resourceTracker)
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
DECL|method|registerNode ()
specifier|public
name|RegisterNodeManagerResponse
name|registerNode
parameter_list|()
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
name|Records
operator|.
name|newRecord
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setMemory
argument_list|(
name|memory
argument_list|)
expr_stmt|;
name|req
operator|.
name|setResource
argument_list|(
name|resource
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
name|currentMasterKey
operator|=
name|registrationResponse
operator|.
name|getMasterKey
argument_list|()
expr_stmt|;
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
argument_list|,
name|isHealthy
argument_list|,
operator|++
name|responseId
argument_list|)
return|;
block|}
DECL|method|nodeHeartbeat (ApplicationAttemptId attemptId, int containerId, ContainerState containerState)
specifier|public
name|NodeHeartbeatResponse
name|nodeHeartbeat
parameter_list|(
name|ApplicationAttemptId
name|attemptId
parameter_list|,
name|int
name|containerId
parameter_list|,
name|ContainerState
name|containerState
parameter_list|)
throws|throws
name|Exception
block|{
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|>
name|nodeUpdate
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
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|ContainerStatus
name|amContainerStatus
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
literal|1
argument_list|)
argument_list|,
name|ContainerState
operator|.
name|COMPLETE
argument_list|,
literal|"Success"
argument_list|,
literal|0
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
name|amContainerStatus
argument_list|)
expr_stmt|;
name|nodeUpdate
operator|.
name|put
argument_list|(
name|attemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|containerStatusList
argument_list|)
expr_stmt|;
return|return
name|nodeHeartbeat
argument_list|(
name|nodeUpdate
argument_list|,
literal|true
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
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|>
name|entry
range|:
name|conts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|status
operator|.
name|setContainersStatuses
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|setLastKnownMasterKey
argument_list|(
name|this
operator|.
name|currentMasterKey
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
name|getMasterKey
argument_list|()
decl_stmt|;
name|this
operator|.
name|currentMasterKey
operator|=
operator|(
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
name|currentMasterKey
operator|.
name|getKeyId
argument_list|()
condition|?
name|masterKeyFromRM
else|:
name|this
operator|.
name|currentMasterKey
operator|)
expr_stmt|;
return|return
name|heartbeatResponse
return|;
block|}
block|}
end_class

end_unit

