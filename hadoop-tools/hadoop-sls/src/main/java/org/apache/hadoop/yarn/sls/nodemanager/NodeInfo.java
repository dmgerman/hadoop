begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.sls.nodemanager
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|sls
operator|.
name|nodemanager
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
name|net
operator|.
name|Node
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
name|ContainerExitStatus
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
name|NodeState
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
name|ResourceOption
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
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNode
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
name|rmnode
operator|.
name|UpdatedContainerInfo
import|;
end_import

begin_class
DECL|class|NodeInfo
specifier|public
class|class
name|NodeInfo
block|{
DECL|field|NODE_ID
specifier|private
specifier|static
name|int
name|NODE_ID
init|=
literal|0
decl_stmt|;
DECL|method|newNodeID (String host, int port)
specifier|public
specifier|static
name|NodeId
name|newNodeID
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|)
block|{
return|return
name|NodeId
operator|.
name|newInstance
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
return|;
block|}
DECL|class|FakeRMNodeImpl
specifier|private
specifier|static
class|class
name|FakeRMNodeImpl
implements|implements
name|RMNode
block|{
DECL|field|nodeId
specifier|private
name|NodeId
name|nodeId
decl_stmt|;
DECL|field|hostName
specifier|private
name|String
name|hostName
decl_stmt|;
DECL|field|nodeAddr
specifier|private
name|String
name|nodeAddr
decl_stmt|;
DECL|field|httpAddress
specifier|private
name|String
name|httpAddress
decl_stmt|;
DECL|field|cmdPort
specifier|private
name|int
name|cmdPort
decl_stmt|;
DECL|field|perNode
specifier|private
specifier|volatile
name|ResourceOption
name|perNode
decl_stmt|;
DECL|field|rackName
specifier|private
name|String
name|rackName
decl_stmt|;
DECL|field|healthReport
specifier|private
name|String
name|healthReport
decl_stmt|;
DECL|field|state
specifier|private
name|NodeState
name|state
decl_stmt|;
DECL|field|toCleanUpContainers
specifier|private
name|List
argument_list|<
name|ContainerId
argument_list|>
name|toCleanUpContainers
decl_stmt|;
DECL|field|toCleanUpApplications
specifier|private
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|toCleanUpApplications
decl_stmt|;
DECL|method|FakeRMNodeImpl (NodeId nodeId, String nodeAddr, String httpAddress, ResourceOption perNode, String rackName, String healthReport, int cmdPort, String hostName, NodeState state)
specifier|public
name|FakeRMNodeImpl
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|String
name|nodeAddr
parameter_list|,
name|String
name|httpAddress
parameter_list|,
name|ResourceOption
name|perNode
parameter_list|,
name|String
name|rackName
parameter_list|,
name|String
name|healthReport
parameter_list|,
name|int
name|cmdPort
parameter_list|,
name|String
name|hostName
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
name|this
operator|.
name|nodeId
operator|=
name|nodeId
expr_stmt|;
name|this
operator|.
name|nodeAddr
operator|=
name|nodeAddr
expr_stmt|;
name|this
operator|.
name|httpAddress
operator|=
name|httpAddress
expr_stmt|;
name|this
operator|.
name|perNode
operator|=
name|perNode
expr_stmt|;
name|this
operator|.
name|rackName
operator|=
name|rackName
expr_stmt|;
name|this
operator|.
name|healthReport
operator|=
name|healthReport
expr_stmt|;
name|this
operator|.
name|cmdPort
operator|=
name|cmdPort
expr_stmt|;
name|this
operator|.
name|hostName
operator|=
name|hostName
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|toCleanUpApplications
operator|=
operator|new
name|ArrayList
argument_list|<
name|ApplicationId
argument_list|>
argument_list|()
expr_stmt|;
name|toCleanUpContainers
operator|=
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|getNodeID ()
specifier|public
name|NodeId
name|getNodeID
parameter_list|()
block|{
return|return
name|nodeId
return|;
block|}
DECL|method|getHostName ()
specifier|public
name|String
name|getHostName
parameter_list|()
block|{
return|return
name|hostName
return|;
block|}
DECL|method|getCommandPort ()
specifier|public
name|int
name|getCommandPort
parameter_list|()
block|{
return|return
name|cmdPort
return|;
block|}
DECL|method|getHttpPort ()
specifier|public
name|int
name|getHttpPort
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|getNodeAddress ()
specifier|public
name|String
name|getNodeAddress
parameter_list|()
block|{
return|return
name|nodeAddr
return|;
block|}
DECL|method|getHttpAddress ()
specifier|public
name|String
name|getHttpAddress
parameter_list|()
block|{
return|return
name|httpAddress
return|;
block|}
DECL|method|getHealthReport ()
specifier|public
name|String
name|getHealthReport
parameter_list|()
block|{
return|return
name|healthReport
return|;
block|}
DECL|method|getLastHealthReportTime ()
specifier|public
name|long
name|getLastHealthReportTime
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|getTotalCapability ()
specifier|public
name|Resource
name|getTotalCapability
parameter_list|()
block|{
return|return
name|perNode
operator|.
name|getResource
argument_list|()
return|;
block|}
DECL|method|getResourceOption ()
specifier|public
name|ResourceOption
name|getResourceOption
parameter_list|()
block|{
return|return
name|perNode
return|;
block|}
DECL|method|getRackName ()
specifier|public
name|String
name|getRackName
parameter_list|()
block|{
return|return
name|rackName
return|;
block|}
DECL|method|getNode ()
specifier|public
name|Node
name|getNode
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
DECL|method|getState ()
specifier|public
name|NodeState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|getContainersToCleanUp ()
specifier|public
name|List
argument_list|<
name|ContainerId
argument_list|>
name|getContainersToCleanUp
parameter_list|()
block|{
return|return
name|toCleanUpContainers
return|;
block|}
DECL|method|getAppsToCleanup ()
specifier|public
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|getAppsToCleanup
parameter_list|()
block|{
return|return
name|toCleanUpApplications
return|;
block|}
DECL|method|updateNodeHeartbeatResponseForCleanup ( NodeHeartbeatResponse response)
specifier|public
name|void
name|updateNodeHeartbeatResponseForCleanup
parameter_list|(
name|NodeHeartbeatResponse
name|response
parameter_list|)
block|{     }
DECL|method|getLastNodeHeartBeatResponse ()
specifier|public
name|NodeHeartbeatResponse
name|getLastNodeHeartBeatResponse
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|pullContainerUpdates ()
specifier|public
name|List
argument_list|<
name|UpdatedContainerInfo
argument_list|>
name|pullContainerUpdates
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|UpdatedContainerInfo
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|UpdatedContainerInfo
argument_list|>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
name|list2
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
name|ContainerId
name|cId
range|:
name|this
operator|.
name|toCleanUpContainers
control|)
block|{
name|list2
operator|.
name|add
argument_list|(
name|ContainerStatus
operator|.
name|newInstance
argument_list|(
name|cId
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|,
literal|""
argument_list|,
name|ContainerExitStatus
operator|.
name|SUCCESS
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
operator|new
name|UpdatedContainerInfo
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|()
argument_list|,
name|list2
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|list
return|;
block|}
annotation|@
name|Override
DECL|method|getNodeManagerVersion ()
specifier|public
name|String
name|getNodeManagerVersion
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setResourceOption (ResourceOption resourceOption)
specifier|public
name|void
name|setResourceOption
parameter_list|(
name|ResourceOption
name|resourceOption
parameter_list|)
block|{
name|perNode
operator|=
name|resourceOption
expr_stmt|;
block|}
block|}
DECL|method|newNodeInfo (String rackName, String hostName, final ResourceOption resourceOption, int port)
specifier|public
specifier|static
name|RMNode
name|newNodeInfo
parameter_list|(
name|String
name|rackName
parameter_list|,
name|String
name|hostName
parameter_list|,
specifier|final
name|ResourceOption
name|resourceOption
parameter_list|,
name|int
name|port
parameter_list|)
block|{
specifier|final
name|NodeId
name|nodeId
init|=
name|newNodeID
argument_list|(
name|hostName
argument_list|,
name|port
argument_list|)
decl_stmt|;
specifier|final
name|String
name|nodeAddr
init|=
name|hostName
operator|+
literal|":"
operator|+
name|port
decl_stmt|;
specifier|final
name|String
name|httpAddress
init|=
name|hostName
decl_stmt|;
return|return
operator|new
name|FakeRMNodeImpl
argument_list|(
name|nodeId
argument_list|,
name|nodeAddr
argument_list|,
name|httpAddress
argument_list|,
name|resourceOption
argument_list|,
name|rackName
argument_list|,
literal|"Me good"
argument_list|,
name|port
argument_list|,
name|hostName
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|newNodeInfo (String rackName, String hostName, final Resource resource)
specifier|public
specifier|static
name|RMNode
name|newNodeInfo
parameter_list|(
name|String
name|rackName
parameter_list|,
name|String
name|hostName
parameter_list|,
specifier|final
name|Resource
name|resource
parameter_list|)
block|{
return|return
name|newNodeInfo
argument_list|(
name|rackName
argument_list|,
name|hostName
argument_list|,
name|ResourceOption
operator|.
name|newInstance
argument_list|(
name|resource
argument_list|,
name|RMNode
operator|.
name|OVER_COMMIT_TIMEOUT_MILLIS_DEFAULT
argument_list|)
argument_list|,
name|NODE_ID
operator|++
argument_list|)
return|;
block|}
block|}
end_class

end_unit

