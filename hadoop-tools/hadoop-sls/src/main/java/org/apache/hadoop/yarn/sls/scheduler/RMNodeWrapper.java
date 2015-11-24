begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.sls.scheduler
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
name|scheduler
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|records
operator|.
name|ResourceUtilization
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

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|RMNodeWrapper
specifier|public
class|class
name|RMNodeWrapper
implements|implements
name|RMNode
block|{
DECL|field|node
specifier|private
name|RMNode
name|node
decl_stmt|;
DECL|field|updates
specifier|private
name|List
argument_list|<
name|UpdatedContainerInfo
argument_list|>
name|updates
decl_stmt|;
DECL|field|pulled
specifier|private
name|boolean
name|pulled
init|=
literal|false
decl_stmt|;
DECL|method|RMNodeWrapper (RMNode node)
specifier|public
name|RMNodeWrapper
parameter_list|(
name|RMNode
name|node
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|updates
operator|=
name|node
operator|.
name|pullContainerUpdates
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNodeID ()
specifier|public
name|NodeId
name|getNodeID
parameter_list|()
block|{
return|return
name|node
operator|.
name|getNodeID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getHostName ()
specifier|public
name|String
name|getHostName
parameter_list|()
block|{
return|return
name|node
operator|.
name|getHostName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCommandPort ()
specifier|public
name|int
name|getCommandPort
parameter_list|()
block|{
return|return
name|node
operator|.
name|getCommandPort
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getHttpPort ()
specifier|public
name|int
name|getHttpPort
parameter_list|()
block|{
return|return
name|node
operator|.
name|getHttpPort
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNodeAddress ()
specifier|public
name|String
name|getNodeAddress
parameter_list|()
block|{
return|return
name|node
operator|.
name|getNodeAddress
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getHttpAddress ()
specifier|public
name|String
name|getHttpAddress
parameter_list|()
block|{
return|return
name|node
operator|.
name|getHttpAddress
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getHealthReport ()
specifier|public
name|String
name|getHealthReport
parameter_list|()
block|{
return|return
name|node
operator|.
name|getHealthReport
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLastHealthReportTime ()
specifier|public
name|long
name|getLastHealthReportTime
parameter_list|()
block|{
return|return
name|node
operator|.
name|getLastHealthReportTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTotalCapability ()
specifier|public
name|Resource
name|getTotalCapability
parameter_list|()
block|{
return|return
name|node
operator|.
name|getTotalCapability
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getRackName ()
specifier|public
name|String
name|getRackName
parameter_list|()
block|{
return|return
name|node
operator|.
name|getRackName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNode ()
specifier|public
name|Node
name|getNode
parameter_list|()
block|{
return|return
name|node
operator|.
name|getNode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getState ()
specifier|public
name|NodeState
name|getState
parameter_list|()
block|{
return|return
name|node
operator|.
name|getState
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|node
operator|.
name|getContainersToCleanUp
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|node
operator|.
name|getAppsToCleanup
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getRunningApps ()
specifier|public
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|getRunningApps
parameter_list|()
block|{
return|return
name|node
operator|.
name|getRunningApps
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|updateNodeHeartbeatResponseForCleanup ( NodeHeartbeatResponse nodeHeartbeatResponse)
specifier|public
name|void
name|updateNodeHeartbeatResponseForCleanup
parameter_list|(
name|NodeHeartbeatResponse
name|nodeHeartbeatResponse
parameter_list|)
block|{
name|node
operator|.
name|updateNodeHeartbeatResponseForCleanup
argument_list|(
name|nodeHeartbeatResponse
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLastNodeHeartBeatResponse ()
specifier|public
name|NodeHeartbeatResponse
name|getLastNodeHeartBeatResponse
parameter_list|()
block|{
return|return
name|node
operator|.
name|getLastNodeHeartBeatResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|resetLastNodeHeartBeatResponse ()
specifier|public
name|void
name|resetLastNodeHeartBeatResponse
parameter_list|()
block|{
name|node
operator|.
name|getLastNodeHeartBeatResponse
argument_list|()
operator|.
name|setResponseId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|pullContainerUpdates ()
specifier|public
name|List
argument_list|<
name|UpdatedContainerInfo
argument_list|>
name|pullContainerUpdates
parameter_list|()
block|{
name|List
argument_list|<
name|UpdatedContainerInfo
argument_list|>
name|list
init|=
name|Collections
operator|.
name|EMPTY_LIST
decl_stmt|;
if|if
condition|(
operator|!
name|pulled
condition|)
block|{
name|list
operator|=
name|updates
expr_stmt|;
name|pulled
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
DECL|method|getContainerUpdates ()
name|List
argument_list|<
name|UpdatedContainerInfo
argument_list|>
name|getContainerUpdates
parameter_list|()
block|{
return|return
name|updates
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
return|return
name|node
operator|.
name|getNodeManagerVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNodeLabels ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getNodeLabels
parameter_list|()
block|{
return|return
name|RMNodeLabelsManager
operator|.
name|EMPTY_STRING_SET
return|;
block|}
annotation|@
name|Override
DECL|method|updateNodeHeartbeatResponseForContainersDecreasing ( NodeHeartbeatResponse response)
specifier|public
name|void
name|updateNodeHeartbeatResponseForContainersDecreasing
parameter_list|(
name|NodeHeartbeatResponse
name|response
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
annotation|@
name|Override
DECL|method|pullNewlyIncreasedContainers ()
specifier|public
name|List
argument_list|<
name|Container
argument_list|>
name|pullNewlyIncreasedContainers
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getAggregatedContainersUtilization ()
specifier|public
name|ResourceUtilization
name|getAggregatedContainersUtilization
parameter_list|()
block|{
return|return
name|node
operator|.
name|getAggregatedContainersUtilization
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNodeUtilization ()
specifier|public
name|ResourceUtilization
name|getNodeUtilization
parameter_list|()
block|{
return|return
name|node
operator|.
name|getNodeUtilization
argument_list|()
return|;
block|}
block|}
end_class

end_unit

