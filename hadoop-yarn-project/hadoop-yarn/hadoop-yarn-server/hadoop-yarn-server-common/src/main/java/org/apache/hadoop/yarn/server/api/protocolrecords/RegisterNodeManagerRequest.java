begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.protocolrecords
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
name|api
operator|.
name|protocolrecords
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
name|util
operator|.
name|Records
import|;
end_import

begin_class
DECL|class|RegisterNodeManagerRequest
specifier|public
specifier|abstract
class|class
name|RegisterNodeManagerRequest
block|{
DECL|method|newInstance (NodeId nodeId, int httpPort, Resource resource, String nodeManagerVersionId, List<NMContainerStatus> containerStatuses)
specifier|public
specifier|static
name|RegisterNodeManagerRequest
name|newInstance
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|int
name|httpPort
parameter_list|,
name|Resource
name|resource
parameter_list|,
name|String
name|nodeManagerVersionId
parameter_list|,
name|List
argument_list|<
name|NMContainerStatus
argument_list|>
name|containerStatuses
parameter_list|)
block|{
name|RegisterNodeManagerRequest
name|request
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
name|request
operator|.
name|setHttpPort
argument_list|(
name|httpPort
argument_list|)
expr_stmt|;
name|request
operator|.
name|setResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|request
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|request
operator|.
name|setNMVersion
argument_list|(
name|nodeManagerVersionId
argument_list|)
expr_stmt|;
name|request
operator|.
name|setContainerStatuses
argument_list|(
name|containerStatuses
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
DECL|method|getNodeId ()
specifier|public
specifier|abstract
name|NodeId
name|getNodeId
parameter_list|()
function_decl|;
DECL|method|getHttpPort ()
specifier|public
specifier|abstract
name|int
name|getHttpPort
parameter_list|()
function_decl|;
DECL|method|getResource ()
specifier|public
specifier|abstract
name|Resource
name|getResource
parameter_list|()
function_decl|;
DECL|method|getNMVersion ()
specifier|public
specifier|abstract
name|String
name|getNMVersion
parameter_list|()
function_decl|;
DECL|method|getNMContainerStatuses ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|NMContainerStatus
argument_list|>
name|getNMContainerStatuses
parameter_list|()
function_decl|;
DECL|method|setNodeId (NodeId nodeId)
specifier|public
specifier|abstract
name|void
name|setNodeId
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
function_decl|;
DECL|method|setHttpPort (int port)
specifier|public
specifier|abstract
name|void
name|setHttpPort
parameter_list|(
name|int
name|port
parameter_list|)
function_decl|;
DECL|method|setResource (Resource resource)
specifier|public
specifier|abstract
name|void
name|setResource
parameter_list|(
name|Resource
name|resource
parameter_list|)
function_decl|;
DECL|method|setNMVersion (String version)
specifier|public
specifier|abstract
name|void
name|setNMVersion
parameter_list|(
name|String
name|version
parameter_list|)
function_decl|;
DECL|method|setContainerStatuses ( List<NMContainerStatus> containerStatuses)
specifier|public
specifier|abstract
name|void
name|setContainerStatuses
parameter_list|(
name|List
argument_list|<
name|NMContainerStatus
argument_list|>
name|containerStatuses
parameter_list|)
function_decl|;
block|}
end_class

end_unit

