begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.webapp.dao
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
name|Public
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
name|Evolving
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
name|util
operator|.
name|Times
import|;
end_import

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"container"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|ContainerInfo
specifier|public
class|class
name|ContainerInfo
block|{
DECL|field|containerId
specifier|protected
name|String
name|containerId
decl_stmt|;
DECL|field|allocatedMB
specifier|protected
name|int
name|allocatedMB
decl_stmt|;
DECL|field|allocatedVCores
specifier|protected
name|int
name|allocatedVCores
decl_stmt|;
DECL|field|assignedNodeId
specifier|protected
name|String
name|assignedNodeId
decl_stmt|;
DECL|field|priority
specifier|protected
name|int
name|priority
decl_stmt|;
DECL|field|startedTime
specifier|protected
name|long
name|startedTime
decl_stmt|;
DECL|field|finishedTime
specifier|protected
name|long
name|finishedTime
decl_stmt|;
DECL|field|elapsedTime
specifier|protected
name|long
name|elapsedTime
decl_stmt|;
DECL|field|diagnosticsInfo
specifier|protected
name|String
name|diagnosticsInfo
decl_stmt|;
DECL|field|logUrl
specifier|protected
name|String
name|logUrl
decl_stmt|;
DECL|field|containerExitStatus
specifier|protected
name|int
name|containerExitStatus
decl_stmt|;
DECL|field|containerState
specifier|protected
name|ContainerState
name|containerState
decl_stmt|;
DECL|field|nodeHttpAddress
specifier|protected
name|String
name|nodeHttpAddress
decl_stmt|;
DECL|method|ContainerInfo ()
specifier|public
name|ContainerInfo
parameter_list|()
block|{
comment|// JAXB needs this
block|}
DECL|method|ContainerInfo (ContainerReport container)
specifier|public
name|ContainerInfo
parameter_list|(
name|ContainerReport
name|container
parameter_list|)
block|{
name|containerId
operator|=
name|container
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
if|if
condition|(
name|container
operator|.
name|getAllocatedResource
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|allocatedMB
operator|=
name|container
operator|.
name|getAllocatedResource
argument_list|()
operator|.
name|getMemory
argument_list|()
expr_stmt|;
name|allocatedVCores
operator|=
name|container
operator|.
name|getAllocatedResource
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|container
operator|.
name|getAssignedNode
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|assignedNodeId
operator|=
name|container
operator|.
name|getAssignedNode
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|priority
operator|=
name|container
operator|.
name|getPriority
argument_list|()
operator|.
name|getPriority
argument_list|()
expr_stmt|;
name|startedTime
operator|=
name|container
operator|.
name|getCreationTime
argument_list|()
expr_stmt|;
name|finishedTime
operator|=
name|container
operator|.
name|getFinishTime
argument_list|()
expr_stmt|;
name|elapsedTime
operator|=
name|Times
operator|.
name|elapsed
argument_list|(
name|startedTime
argument_list|,
name|finishedTime
argument_list|)
expr_stmt|;
name|diagnosticsInfo
operator|=
name|container
operator|.
name|getDiagnosticsInfo
argument_list|()
expr_stmt|;
name|logUrl
operator|=
name|container
operator|.
name|getLogUrl
argument_list|()
expr_stmt|;
name|containerExitStatus
operator|=
name|container
operator|.
name|getContainerExitStatus
argument_list|()
expr_stmt|;
name|containerState
operator|=
name|container
operator|.
name|getContainerState
argument_list|()
expr_stmt|;
name|nodeHttpAddress
operator|=
name|container
operator|.
name|getNodeHttpAddress
argument_list|()
expr_stmt|;
block|}
DECL|method|getContainerId ()
specifier|public
name|String
name|getContainerId
parameter_list|()
block|{
return|return
name|containerId
return|;
block|}
DECL|method|getAllocatedMB ()
specifier|public
name|int
name|getAllocatedMB
parameter_list|()
block|{
return|return
name|allocatedMB
return|;
block|}
DECL|method|getAllocatedVCores ()
specifier|public
name|int
name|getAllocatedVCores
parameter_list|()
block|{
return|return
name|allocatedVCores
return|;
block|}
DECL|method|getAssignedNodeId ()
specifier|public
name|String
name|getAssignedNodeId
parameter_list|()
block|{
return|return
name|assignedNodeId
return|;
block|}
DECL|method|getPriority ()
specifier|public
name|int
name|getPriority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
DECL|method|getStartedTime ()
specifier|public
name|long
name|getStartedTime
parameter_list|()
block|{
return|return
name|startedTime
return|;
block|}
DECL|method|getFinishedTime ()
specifier|public
name|long
name|getFinishedTime
parameter_list|()
block|{
return|return
name|finishedTime
return|;
block|}
DECL|method|getElapsedTime ()
specifier|public
name|long
name|getElapsedTime
parameter_list|()
block|{
return|return
name|elapsedTime
return|;
block|}
DECL|method|getDiagnosticsInfo ()
specifier|public
name|String
name|getDiagnosticsInfo
parameter_list|()
block|{
return|return
name|diagnosticsInfo
return|;
block|}
DECL|method|getLogUrl ()
specifier|public
name|String
name|getLogUrl
parameter_list|()
block|{
return|return
name|logUrl
return|;
block|}
DECL|method|getContainerExitStatus ()
specifier|public
name|int
name|getContainerExitStatus
parameter_list|()
block|{
return|return
name|containerExitStatus
return|;
block|}
DECL|method|getContainerState ()
specifier|public
name|ContainerState
name|getContainerState
parameter_list|()
block|{
return|return
name|containerState
return|;
block|}
DECL|method|getNodeHttpAddress ()
specifier|public
name|String
name|getNodeHttpAddress
parameter_list|()
block|{
return|return
name|nodeHttpAddress
return|;
block|}
block|}
end_class

end_unit

