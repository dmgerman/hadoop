begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmnode
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
name|rmnode
package|;
end_package

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
name|server
operator|.
name|api
operator|.
name|records
operator|.
name|HeartbeatResponse
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

begin_class
DECL|class|RMNodeStatusEvent
specifier|public
class|class
name|RMNodeStatusEvent
extends|extends
name|RMNodeEvent
block|{
DECL|field|nodeHealthStatus
specifier|private
specifier|final
name|NodeHealthStatus
name|nodeHealthStatus
decl_stmt|;
DECL|field|containersCollection
specifier|private
specifier|final
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|containersCollection
decl_stmt|;
DECL|field|latestResponse
specifier|private
specifier|final
name|HeartbeatResponse
name|latestResponse
decl_stmt|;
DECL|field|keepAliveAppIds
specifier|private
specifier|final
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|keepAliveAppIds
decl_stmt|;
DECL|field|currentMasterKey
specifier|private
specifier|final
name|MasterKey
name|currentMasterKey
decl_stmt|;
DECL|method|RMNodeStatusEvent (NodeId nodeId, NodeHealthStatus nodeHealthStatus, List<ContainerStatus> collection, List<ApplicationId> keepAliveAppIds, HeartbeatResponse latestResponse, MasterKey currentMasterKey)
specifier|public
name|RMNodeStatusEvent
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|NodeHealthStatus
name|nodeHealthStatus
parameter_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|collection
parameter_list|,
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|keepAliveAppIds
parameter_list|,
name|HeartbeatResponse
name|latestResponse
parameter_list|,
name|MasterKey
name|currentMasterKey
parameter_list|)
block|{
name|super
argument_list|(
name|nodeId
argument_list|,
name|RMNodeEventType
operator|.
name|STATUS_UPDATE
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeHealthStatus
operator|=
name|nodeHealthStatus
expr_stmt|;
name|this
operator|.
name|containersCollection
operator|=
name|collection
expr_stmt|;
name|this
operator|.
name|keepAliveAppIds
operator|=
name|keepAliveAppIds
expr_stmt|;
name|this
operator|.
name|latestResponse
operator|=
name|latestResponse
expr_stmt|;
name|this
operator|.
name|currentMasterKey
operator|=
name|currentMasterKey
expr_stmt|;
block|}
DECL|method|getNodeHealthStatus ()
specifier|public
name|NodeHealthStatus
name|getNodeHealthStatus
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeHealthStatus
return|;
block|}
DECL|method|getContainers ()
specifier|public
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|getContainers
parameter_list|()
block|{
return|return
name|this
operator|.
name|containersCollection
return|;
block|}
DECL|method|getLatestResponse ()
specifier|public
name|HeartbeatResponse
name|getLatestResponse
parameter_list|()
block|{
return|return
name|this
operator|.
name|latestResponse
return|;
block|}
DECL|method|getKeepAliveAppIds ()
specifier|public
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|getKeepAliveAppIds
parameter_list|()
block|{
return|return
name|this
operator|.
name|keepAliveAppIds
return|;
block|}
DECL|method|getCurrentMasterKey ()
specifier|public
name|MasterKey
name|getCurrentMasterKey
parameter_list|()
block|{
return|return
name|this
operator|.
name|currentMasterKey
return|;
block|}
block|}
end_class

end_unit

