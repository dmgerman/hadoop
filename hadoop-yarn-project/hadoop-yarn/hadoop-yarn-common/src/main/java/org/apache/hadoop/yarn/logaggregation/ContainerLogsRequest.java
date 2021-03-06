begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.logaggregation
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|logaggregation
package|;
end_package

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

begin_class
DECL|class|ContainerLogsRequest
specifier|public
class|class
name|ContainerLogsRequest
block|{
DECL|field|appId
specifier|private
name|ApplicationId
name|appId
decl_stmt|;
DECL|field|containerId
specifier|private
name|String
name|containerId
decl_stmt|;
DECL|field|nodeId
specifier|private
name|String
name|nodeId
decl_stmt|;
DECL|field|nodeHttpAddress
specifier|private
name|String
name|nodeHttpAddress
decl_stmt|;
DECL|field|appOwner
specifier|private
name|String
name|appOwner
decl_stmt|;
DECL|field|appFinished
specifier|private
name|boolean
name|appFinished
decl_stmt|;
DECL|field|outputLocalDir
specifier|private
name|String
name|outputLocalDir
decl_stmt|;
DECL|field|logTypes
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|logTypes
decl_stmt|;
DECL|field|bytes
specifier|private
name|long
name|bytes
decl_stmt|;
DECL|field|containerState
specifier|private
name|ContainerState
name|containerState
decl_stmt|;
DECL|method|ContainerLogsRequest ()
specifier|public
name|ContainerLogsRequest
parameter_list|()
block|{}
DECL|method|ContainerLogsRequest (ContainerLogsRequest request)
specifier|public
name|ContainerLogsRequest
parameter_list|(
name|ContainerLogsRequest
name|request
parameter_list|)
block|{
name|this
operator|.
name|setAppId
argument_list|(
name|request
operator|.
name|getAppId
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|setAppFinished
argument_list|(
name|request
operator|.
name|isAppFinished
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|setAppOwner
argument_list|(
name|request
operator|.
name|getAppOwner
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|setNodeId
argument_list|(
name|request
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|setNodeHttpAddress
argument_list|(
name|request
operator|.
name|getNodeHttpAddress
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|setContainerId
argument_list|(
name|request
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|setOutputLocalDir
argument_list|(
name|request
operator|.
name|getOutputLocalDir
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|setLogTypes
argument_list|(
name|request
operator|.
name|getLogTypes
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|setBytes
argument_list|(
name|request
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|setContainerState
argument_list|(
name|request
operator|.
name|getContainerState
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|ContainerLogsRequest (ApplicationId applicationId, boolean isAppFinished, String owner, String address, String httpAddress, String container, String localDir, Set<String> logs, long bytes, ContainerState containerState)
specifier|public
name|ContainerLogsRequest
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|boolean
name|isAppFinished
parameter_list|,
name|String
name|owner
parameter_list|,
name|String
name|address
parameter_list|,
name|String
name|httpAddress
parameter_list|,
name|String
name|container
parameter_list|,
name|String
name|localDir
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|logs
parameter_list|,
name|long
name|bytes
parameter_list|,
name|ContainerState
name|containerState
parameter_list|)
block|{
name|this
operator|.
name|setAppId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|this
operator|.
name|setAppFinished
argument_list|(
name|isAppFinished
argument_list|)
expr_stmt|;
name|this
operator|.
name|setAppOwner
argument_list|(
name|owner
argument_list|)
expr_stmt|;
name|this
operator|.
name|setNodeId
argument_list|(
name|address
argument_list|)
expr_stmt|;
name|this
operator|.
name|setNodeHttpAddress
argument_list|(
name|httpAddress
argument_list|)
expr_stmt|;
name|this
operator|.
name|setContainerId
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|this
operator|.
name|setOutputLocalDir
argument_list|(
name|localDir
argument_list|)
expr_stmt|;
name|this
operator|.
name|setLogTypes
argument_list|(
name|logs
argument_list|)
expr_stmt|;
name|this
operator|.
name|setBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|this
operator|.
name|setContainerState
argument_list|(
name|containerState
argument_list|)
expr_stmt|;
block|}
DECL|method|getAppId ()
specifier|public
name|ApplicationId
name|getAppId
parameter_list|()
block|{
return|return
name|appId
return|;
block|}
DECL|method|setAppId (ApplicationId appId)
specifier|public
name|void
name|setAppId
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
name|this
operator|.
name|appId
operator|=
name|appId
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
DECL|method|setContainerId (String containerId)
specifier|public
name|void
name|setContainerId
parameter_list|(
name|String
name|containerId
parameter_list|)
block|{
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
block|}
DECL|method|getNodeId ()
specifier|public
name|String
name|getNodeId
parameter_list|()
block|{
return|return
name|nodeId
return|;
block|}
DECL|method|setNodeId (String nodeAddress)
specifier|public
name|void
name|setNodeId
parameter_list|(
name|String
name|nodeAddress
parameter_list|)
block|{
name|this
operator|.
name|nodeId
operator|=
name|nodeAddress
expr_stmt|;
block|}
DECL|method|getAppOwner ()
specifier|public
name|String
name|getAppOwner
parameter_list|()
block|{
return|return
name|appOwner
return|;
block|}
DECL|method|setAppOwner (String appOwner)
specifier|public
name|void
name|setAppOwner
parameter_list|(
name|String
name|appOwner
parameter_list|)
block|{
name|this
operator|.
name|appOwner
operator|=
name|appOwner
expr_stmt|;
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
DECL|method|setNodeHttpAddress (String nodeHttpAddress)
specifier|public
name|void
name|setNodeHttpAddress
parameter_list|(
name|String
name|nodeHttpAddress
parameter_list|)
block|{
name|this
operator|.
name|nodeHttpAddress
operator|=
name|nodeHttpAddress
expr_stmt|;
block|}
DECL|method|isAppFinished ()
specifier|public
name|boolean
name|isAppFinished
parameter_list|()
block|{
return|return
name|appFinished
return|;
block|}
DECL|method|setAppFinished (boolean appFinished)
specifier|public
name|void
name|setAppFinished
parameter_list|(
name|boolean
name|appFinished
parameter_list|)
block|{
name|this
operator|.
name|appFinished
operator|=
name|appFinished
expr_stmt|;
block|}
DECL|method|getOutputLocalDir ()
specifier|public
name|String
name|getOutputLocalDir
parameter_list|()
block|{
return|return
name|outputLocalDir
return|;
block|}
DECL|method|setOutputLocalDir (String outputLocalDir)
specifier|public
name|void
name|setOutputLocalDir
parameter_list|(
name|String
name|outputLocalDir
parameter_list|)
block|{
name|this
operator|.
name|outputLocalDir
operator|=
name|outputLocalDir
expr_stmt|;
block|}
DECL|method|getLogTypes ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getLogTypes
parameter_list|()
block|{
return|return
name|logTypes
return|;
block|}
DECL|method|setLogTypes (Set<String> logTypes)
specifier|public
name|void
name|setLogTypes
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|logTypes
parameter_list|)
block|{
name|this
operator|.
name|logTypes
operator|=
name|logTypes
expr_stmt|;
block|}
DECL|method|getBytes ()
specifier|public
name|long
name|getBytes
parameter_list|()
block|{
return|return
name|bytes
return|;
block|}
DECL|method|setBytes (long bytes)
specifier|public
name|void
name|setBytes
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
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
DECL|method|setContainerState (ContainerState containerState)
specifier|public
name|void
name|setContainerState
parameter_list|(
name|ContainerState
name|containerState
parameter_list|)
block|{
name|this
operator|.
name|containerState
operator|=
name|containerState
expr_stmt|;
block|}
block|}
end_class

end_unit

