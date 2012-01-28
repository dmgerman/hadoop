begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.dao
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
operator|.
name|dao
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlTransient
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
name|RMNodeState
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
name|SchedulerNodeReport
import|;
end_import

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"node"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|NodeInfo
specifier|public
class|class
name|NodeInfo
block|{
DECL|field|rack
specifier|protected
name|String
name|rack
decl_stmt|;
DECL|field|state
specifier|protected
name|RMNodeState
name|state
decl_stmt|;
DECL|field|id
specifier|protected
name|String
name|id
decl_stmt|;
DECL|field|nodeHostName
specifier|protected
name|String
name|nodeHostName
decl_stmt|;
DECL|field|nodeHTTPAddress
specifier|protected
name|String
name|nodeHTTPAddress
decl_stmt|;
DECL|field|healthStatus
specifier|protected
name|String
name|healthStatus
decl_stmt|;
DECL|field|lastHealthUpdate
specifier|protected
name|long
name|lastHealthUpdate
decl_stmt|;
DECL|field|healthReport
specifier|protected
name|String
name|healthReport
decl_stmt|;
DECL|field|numContainers
specifier|protected
name|int
name|numContainers
decl_stmt|;
DECL|field|usedMemoryMB
specifier|protected
name|long
name|usedMemoryMB
decl_stmt|;
DECL|field|availMemoryMB
specifier|protected
name|long
name|availMemoryMB
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|healthy
specifier|protected
name|boolean
name|healthy
decl_stmt|;
DECL|method|NodeInfo ()
specifier|public
name|NodeInfo
parameter_list|()
block|{   }
comment|// JAXB needs this
DECL|method|NodeInfo (RMNode ni, ResourceScheduler sched)
specifier|public
name|NodeInfo
parameter_list|(
name|RMNode
name|ni
parameter_list|,
name|ResourceScheduler
name|sched
parameter_list|)
block|{
name|NodeId
name|id
init|=
name|ni
operator|.
name|getNodeID
argument_list|()
decl_stmt|;
name|SchedulerNodeReport
name|report
init|=
name|sched
operator|.
name|getNodeReport
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|NodeHealthStatus
name|health
init|=
name|ni
operator|.
name|getNodeHealthStatus
argument_list|()
decl_stmt|;
name|this
operator|.
name|numContainers
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|usedMemoryMB
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|availMemoryMB
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|report
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|numContainers
operator|=
name|report
operator|.
name|getNumContainers
argument_list|()
expr_stmt|;
name|this
operator|.
name|usedMemoryMB
operator|=
name|report
operator|.
name|getUsedResource
argument_list|()
operator|.
name|getMemory
argument_list|()
expr_stmt|;
name|this
operator|.
name|availMemoryMB
operator|=
name|report
operator|.
name|getAvailableResource
argument_list|()
operator|.
name|getMemory
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|id
operator|=
name|id
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|rack
operator|=
name|ni
operator|.
name|getRackName
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeHostName
operator|=
name|ni
operator|.
name|getHostName
argument_list|()
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|ni
operator|.
name|getState
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeHTTPAddress
operator|=
name|ni
operator|.
name|getHttpAddress
argument_list|()
expr_stmt|;
name|this
operator|.
name|healthy
operator|=
name|health
operator|.
name|getIsNodeHealthy
argument_list|()
expr_stmt|;
name|this
operator|.
name|healthStatus
operator|=
name|health
operator|.
name|getIsNodeHealthy
argument_list|()
condition|?
literal|"Healthy"
else|:
literal|"Unhealthy"
expr_stmt|;
name|this
operator|.
name|lastHealthUpdate
operator|=
name|health
operator|.
name|getLastHealthReportTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|healthReport
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|health
operator|.
name|getHealthReport
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|isHealthy ()
specifier|public
name|boolean
name|isHealthy
parameter_list|()
block|{
return|return
name|this
operator|.
name|healthy
return|;
block|}
DECL|method|getRack ()
specifier|public
name|String
name|getRack
parameter_list|()
block|{
return|return
name|this
operator|.
name|rack
return|;
block|}
DECL|method|getState ()
specifier|public
name|String
name|getState
parameter_list|()
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|this
operator|.
name|state
argument_list|)
return|;
block|}
DECL|method|getNodeId ()
specifier|public
name|String
name|getNodeId
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
DECL|method|getNodeHTTPAddress ()
specifier|public
name|String
name|getNodeHTTPAddress
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeHTTPAddress
return|;
block|}
DECL|method|setNodeHTTPAddress (String nodeHTTPAddress)
specifier|public
name|void
name|setNodeHTTPAddress
parameter_list|(
name|String
name|nodeHTTPAddress
parameter_list|)
block|{
name|this
operator|.
name|nodeHTTPAddress
operator|=
name|nodeHTTPAddress
expr_stmt|;
block|}
DECL|method|getHealthStatus ()
specifier|public
name|String
name|getHealthStatus
parameter_list|()
block|{
return|return
name|this
operator|.
name|healthStatus
return|;
block|}
DECL|method|getLastHealthUpdate ()
specifier|public
name|long
name|getLastHealthUpdate
parameter_list|()
block|{
return|return
name|this
operator|.
name|lastHealthUpdate
return|;
block|}
DECL|method|getHealthReport ()
specifier|public
name|String
name|getHealthReport
parameter_list|()
block|{
return|return
name|this
operator|.
name|healthReport
return|;
block|}
DECL|method|getNumContainers ()
specifier|public
name|int
name|getNumContainers
parameter_list|()
block|{
return|return
name|this
operator|.
name|numContainers
return|;
block|}
DECL|method|getUsedMemory ()
specifier|public
name|long
name|getUsedMemory
parameter_list|()
block|{
return|return
name|this
operator|.
name|usedMemoryMB
return|;
block|}
DECL|method|getAvailableMemory ()
specifier|public
name|long
name|getAvailableMemory
parameter_list|()
block|{
return|return
name|this
operator|.
name|availMemoryMB
return|;
block|}
block|}
end_class

end_unit

