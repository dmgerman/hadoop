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

begin_comment
comment|/**  * States used by the container state machine.  */
end_comment

begin_enum
DECL|enum|ContainerState
specifier|public
enum|enum
name|ContainerState
block|{
comment|// NOTE: In case of future additions / deletions / modifications to this
comment|//       enum, please ensure that the following are also correspondingly
comment|//       updated:
comment|//       1. ContainerImpl::getContainerSubState().
comment|//       2. the doc in the ContainerSubState class.
comment|//       3. the doc in the yarn_protos.proto file.
DECL|enumConstant|NEW
DECL|enumConstant|LOCALIZING
DECL|enumConstant|LOCALIZATION_FAILED
DECL|enumConstant|SCHEDULED
DECL|enumConstant|RUNNING
DECL|enumConstant|RELAUNCHING
name|NEW
block|,
name|LOCALIZING
block|,
name|LOCALIZATION_FAILED
block|,
name|SCHEDULED
block|,
name|RUNNING
block|,
name|RELAUNCHING
block|,
DECL|enumConstant|REINITIALIZING
DECL|enumConstant|REINITIALIZING_AWAITING_KILL
name|REINITIALIZING
block|,
name|REINITIALIZING_AWAITING_KILL
block|,
DECL|enumConstant|EXITED_WITH_SUCCESS
DECL|enumConstant|EXITED_WITH_FAILURE
DECL|enumConstant|KILLING
name|EXITED_WITH_SUCCESS
block|,
name|EXITED_WITH_FAILURE
block|,
name|KILLING
block|,
DECL|enumConstant|CONTAINER_CLEANEDUP_AFTER_KILL
DECL|enumConstant|CONTAINER_RESOURCES_CLEANINGUP
DECL|enumConstant|DONE
name|CONTAINER_CLEANEDUP_AFTER_KILL
block|,
name|CONTAINER_RESOURCES_CLEANINGUP
block|,
name|DONE
block|,
DECL|enumConstant|PAUSING
DECL|enumConstant|PAUSED
DECL|enumConstant|RESUMING
name|PAUSING
block|,
name|PAUSED
block|,
name|RESUMING
block|}
end_enum

end_unit

