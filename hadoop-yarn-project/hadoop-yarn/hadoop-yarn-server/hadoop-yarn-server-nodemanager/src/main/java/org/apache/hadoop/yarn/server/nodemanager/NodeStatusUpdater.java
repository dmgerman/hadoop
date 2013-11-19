begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
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
name|nodemanager
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
name|service
operator|.
name|Service
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
name|server
operator|.
name|api
operator|.
name|records
operator|.
name|NodeStatus
import|;
end_import

begin_interface
DECL|interface|NodeStatusUpdater
specifier|public
interface|interface
name|NodeStatusUpdater
extends|extends
name|Service
block|{
DECL|method|sendOutofBandHeartBeat ()
name|void
name|sendOutofBandHeartBeat
parameter_list|()
function_decl|;
DECL|method|getNodeStatusAndUpdateContainersInContext (int responseId)
name|NodeStatus
name|getNodeStatusAndUpdateContainersInContext
parameter_list|(
name|int
name|responseId
parameter_list|)
function_decl|;
DECL|method|getRMIdentifier ()
name|long
name|getRMIdentifier
parameter_list|()
function_decl|;
DECL|method|isContainerRecentlyStopped (ContainerId containerId)
specifier|public
name|boolean
name|isContainerRecentlyStopped
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
function_decl|;
DECL|method|clearFinishedContainersFromCache ()
specifier|public
name|void
name|clearFinishedContainersFromCache
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

