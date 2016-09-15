begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.container
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
operator|.
name|containermanager
operator|.
name|container
package|;
end_package

begin_enum
DECL|enum|ContainerEventType
specifier|public
enum|enum
name|ContainerEventType
block|{
comment|// Producer: ContainerManager
DECL|enumConstant|INIT_CONTAINER
name|INIT_CONTAINER
block|,
DECL|enumConstant|KILL_CONTAINER
name|KILL_CONTAINER
block|,
DECL|enumConstant|UPDATE_DIAGNOSTICS_MSG
name|UPDATE_DIAGNOSTICS_MSG
block|,
DECL|enumConstant|CONTAINER_DONE
name|CONTAINER_DONE
block|,
DECL|enumConstant|REINITIALIZE_CONTAINER
name|REINITIALIZE_CONTAINER
block|,
comment|// DownloadManager
DECL|enumConstant|CONTAINER_INITED
name|CONTAINER_INITED
block|,
DECL|enumConstant|RESOURCE_LOCALIZED
name|RESOURCE_LOCALIZED
block|,
DECL|enumConstant|RESOURCE_FAILED
name|RESOURCE_FAILED
block|,
DECL|enumConstant|CONTAINER_RESOURCES_CLEANEDUP
name|CONTAINER_RESOURCES_CLEANEDUP
block|,
comment|// Producer: ContainersLauncher
DECL|enumConstant|CONTAINER_LAUNCHED
name|CONTAINER_LAUNCHED
block|,
DECL|enumConstant|CONTAINER_EXITED_WITH_SUCCESS
name|CONTAINER_EXITED_WITH_SUCCESS
block|,
DECL|enumConstant|CONTAINER_EXITED_WITH_FAILURE
name|CONTAINER_EXITED_WITH_FAILURE
block|,
DECL|enumConstant|CONTAINER_KILLED_ON_REQUEST
name|CONTAINER_KILLED_ON_REQUEST
block|}
end_enum

end_unit

