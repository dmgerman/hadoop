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
name|Records
import|;
end_import

begin_class
DECL|class|NodeHeartbeatRequest
specifier|public
specifier|abstract
class|class
name|NodeHeartbeatRequest
block|{
DECL|method|newInstance (NodeStatus nodeStatus, MasterKey lastKnownContainerTokenMasterKey, MasterKey lastKnownNMTokenMasterKey, Set<String> nodeLabels)
specifier|public
specifier|static
name|NodeHeartbeatRequest
name|newInstance
parameter_list|(
name|NodeStatus
name|nodeStatus
parameter_list|,
name|MasterKey
name|lastKnownContainerTokenMasterKey
parameter_list|,
name|MasterKey
name|lastKnownNMTokenMasterKey
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|nodeLabels
parameter_list|)
block|{
name|NodeHeartbeatRequest
name|nodeHeartbeatRequest
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
name|nodeHeartbeatRequest
operator|.
name|setNodeStatus
argument_list|(
name|nodeStatus
argument_list|)
expr_stmt|;
name|nodeHeartbeatRequest
operator|.
name|setLastKnownContainerTokenMasterKey
argument_list|(
name|lastKnownContainerTokenMasterKey
argument_list|)
expr_stmt|;
name|nodeHeartbeatRequest
operator|.
name|setLastKnownNMTokenMasterKey
argument_list|(
name|lastKnownNMTokenMasterKey
argument_list|)
expr_stmt|;
name|nodeHeartbeatRequest
operator|.
name|setNodeLabels
argument_list|(
name|nodeLabels
argument_list|)
expr_stmt|;
return|return
name|nodeHeartbeatRequest
return|;
block|}
DECL|method|getNodeStatus ()
specifier|public
specifier|abstract
name|NodeStatus
name|getNodeStatus
parameter_list|()
function_decl|;
DECL|method|setNodeStatus (NodeStatus status)
specifier|public
specifier|abstract
name|void
name|setNodeStatus
parameter_list|(
name|NodeStatus
name|status
parameter_list|)
function_decl|;
DECL|method|getLastKnownContainerTokenMasterKey ()
specifier|public
specifier|abstract
name|MasterKey
name|getLastKnownContainerTokenMasterKey
parameter_list|()
function_decl|;
DECL|method|setLastKnownContainerTokenMasterKey (MasterKey secretKey)
specifier|public
specifier|abstract
name|void
name|setLastKnownContainerTokenMasterKey
parameter_list|(
name|MasterKey
name|secretKey
parameter_list|)
function_decl|;
DECL|method|getLastKnownNMTokenMasterKey ()
specifier|public
specifier|abstract
name|MasterKey
name|getLastKnownNMTokenMasterKey
parameter_list|()
function_decl|;
DECL|method|setLastKnownNMTokenMasterKey (MasterKey secretKey)
specifier|public
specifier|abstract
name|void
name|setLastKnownNMTokenMasterKey
parameter_list|(
name|MasterKey
name|secretKey
parameter_list|)
function_decl|;
DECL|method|getNodeLabels ()
specifier|public
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getNodeLabels
parameter_list|()
function_decl|;
DECL|method|setNodeLabels (Set<String> nodeLabels)
specifier|public
specifier|abstract
name|void
name|setNodeLabels
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|nodeLabels
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|List
argument_list|<
name|LogAggregationReport
argument_list|>
DECL|method|getLogAggregationReportsForApps ()
name|getLogAggregationReportsForApps
parameter_list|()
function_decl|;
DECL|method|setLogAggregationReportsForApps ( List<LogAggregationReport> logAggregationReportsForApps)
specifier|public
specifier|abstract
name|void
name|setLogAggregationReportsForApps
parameter_list|(
name|List
argument_list|<
name|LogAggregationReport
argument_list|>
name|logAggregationReportsForApps
parameter_list|)
function_decl|;
block|}
end_class

end_unit

