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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  * Node Manager's unregister request.  */
end_comment

begin_class
DECL|class|UnRegisterNodeManagerRequest
specifier|public
specifier|abstract
class|class
name|UnRegisterNodeManagerRequest
block|{
DECL|method|newInstance (NodeId nodeId)
specifier|public
specifier|static
name|UnRegisterNodeManagerRequest
name|newInstance
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
name|UnRegisterNodeManagerRequest
name|nodeHeartbeatRequest
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|UnRegisterNodeManagerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|nodeHeartbeatRequest
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
return|return
name|nodeHeartbeatRequest
return|;
block|}
DECL|method|getNodeId ()
specifier|public
specifier|abstract
name|NodeId
name|getNodeId
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
block|}
end_class

end_unit

