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

begin_enum
DECL|enum|RMNodeEventType
specifier|public
enum|enum
name|RMNodeEventType
block|{
DECL|enumConstant|STARTED
name|STARTED
block|,
comment|// Source: AdminService
DECL|enumConstant|DECOMMISSION
name|DECOMMISSION
block|,
DECL|enumConstant|GRACEFUL_DECOMMISSION
name|GRACEFUL_DECOMMISSION
block|,
DECL|enumConstant|RECOMMISSION
name|RECOMMISSION
block|,
comment|// Source: AdminService, ResourceTrackerService
DECL|enumConstant|RESOURCE_UPDATE
name|RESOURCE_UPDATE
block|,
comment|// ResourceTrackerService
DECL|enumConstant|STATUS_UPDATE
name|STATUS_UPDATE
block|,
DECL|enumConstant|REBOOTING
name|REBOOTING
block|,
DECL|enumConstant|RECONNECTED
name|RECONNECTED
block|,
DECL|enumConstant|SHUTDOWN
name|SHUTDOWN
block|,
comment|// Source: Application
DECL|enumConstant|CLEANUP_APP
name|CLEANUP_APP
block|,
comment|// Source: Container
DECL|enumConstant|CONTAINER_ALLOCATED
name|CONTAINER_ALLOCATED
block|,
DECL|enumConstant|CLEANUP_CONTAINER
name|CLEANUP_CONTAINER
block|,
DECL|enumConstant|UPDATE_CONTAINER
name|UPDATE_CONTAINER
block|,
comment|// Source: ClientRMService
DECL|enumConstant|SIGNAL_CONTAINER
name|SIGNAL_CONTAINER
block|,
comment|// Source: RMAppAttempt
DECL|enumConstant|FINISHED_CONTAINERS_PULLED_BY_AM
name|FINISHED_CONTAINERS_PULLED_BY_AM
block|,
comment|// Source: NMLivelinessMonitor
DECL|enumConstant|EXPIRE
name|EXPIRE
block|}
end_enum

end_unit

