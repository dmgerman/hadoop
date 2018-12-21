begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|NodeAction
import|;
end_import

begin_comment
comment|/**  * Node Manager's register response.  */
end_comment

begin_class
DECL|class|RegisterNodeManagerResponse
specifier|public
specifier|abstract
class|class
name|RegisterNodeManagerResponse
block|{
DECL|method|getContainerTokenMasterKey ()
specifier|public
specifier|abstract
name|MasterKey
name|getContainerTokenMasterKey
parameter_list|()
function_decl|;
DECL|method|setContainerTokenMasterKey (MasterKey secretKey)
specifier|public
specifier|abstract
name|void
name|setContainerTokenMasterKey
parameter_list|(
name|MasterKey
name|secretKey
parameter_list|)
function_decl|;
DECL|method|getNMTokenMasterKey ()
specifier|public
specifier|abstract
name|MasterKey
name|getNMTokenMasterKey
parameter_list|()
function_decl|;
DECL|method|setNMTokenMasterKey (MasterKey secretKey)
specifier|public
specifier|abstract
name|void
name|setNMTokenMasterKey
parameter_list|(
name|MasterKey
name|secretKey
parameter_list|)
function_decl|;
DECL|method|getNodeAction ()
specifier|public
specifier|abstract
name|NodeAction
name|getNodeAction
parameter_list|()
function_decl|;
DECL|method|setNodeAction (NodeAction nodeAction)
specifier|public
specifier|abstract
name|void
name|setNodeAction
parameter_list|(
name|NodeAction
name|nodeAction
parameter_list|)
function_decl|;
DECL|method|getRMIdentifier ()
specifier|public
specifier|abstract
name|long
name|getRMIdentifier
parameter_list|()
function_decl|;
DECL|method|setRMIdentifier (long rmIdentifier)
specifier|public
specifier|abstract
name|void
name|setRMIdentifier
parameter_list|(
name|long
name|rmIdentifier
parameter_list|)
function_decl|;
DECL|method|getDiagnosticsMessage ()
specifier|public
specifier|abstract
name|String
name|getDiagnosticsMessage
parameter_list|()
function_decl|;
DECL|method|setDiagnosticsMessage (String diagnosticsMessage)
specifier|public
specifier|abstract
name|void
name|setDiagnosticsMessage
parameter_list|(
name|String
name|diagnosticsMessage
parameter_list|)
function_decl|;
DECL|method|setRMVersion (String version)
specifier|public
specifier|abstract
name|void
name|setRMVersion
parameter_list|(
name|String
name|version
parameter_list|)
function_decl|;
DECL|method|getRMVersion ()
specifier|public
specifier|abstract
name|String
name|getRMVersion
parameter_list|()
function_decl|;
DECL|method|getResource ()
specifier|public
specifier|abstract
name|Resource
name|getResource
parameter_list|()
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
DECL|method|getAreNodeLabelsAcceptedByRM ()
specifier|public
specifier|abstract
name|boolean
name|getAreNodeLabelsAcceptedByRM
parameter_list|()
function_decl|;
DECL|method|setAreNodeLabelsAcceptedByRM ( boolean areNodeLabelsAcceptedByRM)
specifier|public
specifier|abstract
name|void
name|setAreNodeLabelsAcceptedByRM
parameter_list|(
name|boolean
name|areNodeLabelsAcceptedByRM
parameter_list|)
function_decl|;
DECL|method|getAreNodeAttributesAcceptedByRM ()
specifier|public
specifier|abstract
name|boolean
name|getAreNodeAttributesAcceptedByRM
parameter_list|()
function_decl|;
DECL|method|setAreNodeAttributesAcceptedByRM ( boolean areNodeAttributesAcceptedByRM)
specifier|public
specifier|abstract
name|void
name|setAreNodeAttributesAcceptedByRM
parameter_list|(
name|boolean
name|areNodeAttributesAcceptedByRM
parameter_list|)
function_decl|;
block|}
end_class

end_unit

