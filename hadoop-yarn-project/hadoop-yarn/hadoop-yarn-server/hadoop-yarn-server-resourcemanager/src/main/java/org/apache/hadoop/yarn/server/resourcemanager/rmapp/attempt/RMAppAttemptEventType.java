begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt
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
name|rmapp
operator|.
name|attempt
package|;
end_package

begin_enum
DECL|enum|RMAppAttemptEventType
specifier|public
enum|enum
name|RMAppAttemptEventType
block|{
comment|// Source: RMApp
DECL|enumConstant|START
name|START
block|,
DECL|enumConstant|KILL
name|KILL
block|,
DECL|enumConstant|FAIL
name|FAIL
block|,
comment|// Source: AMLauncher
DECL|enumConstant|LAUNCHED
name|LAUNCHED
block|,
DECL|enumConstant|LAUNCH_FAILED
name|LAUNCH_FAILED
block|,
comment|// Source: AMLivelinessMonitor
DECL|enumConstant|EXPIRE
name|EXPIRE
block|,
comment|// Source: ApplicationMasterService
DECL|enumConstant|REGISTERED
name|REGISTERED
block|,
DECL|enumConstant|STATUS_UPDATE
name|STATUS_UPDATE
block|,
DECL|enumConstant|UNREGISTERED
name|UNREGISTERED
block|,
comment|// Source: Containers
DECL|enumConstant|CONTAINER_ALLOCATED
name|CONTAINER_ALLOCATED
block|,
DECL|enumConstant|CONTAINER_FINISHED
name|CONTAINER_FINISHED
block|,
comment|// Source: RMStateStore
DECL|enumConstant|ATTEMPT_NEW_SAVED
name|ATTEMPT_NEW_SAVED
block|,
DECL|enumConstant|ATTEMPT_UPDATE_SAVED
name|ATTEMPT_UPDATE_SAVED
block|,
comment|// Source: Scheduler
DECL|enumConstant|ATTEMPT_ADDED
name|ATTEMPT_ADDED
block|,
comment|// Source: RMAttemptImpl.recover
DECL|enumConstant|RECOVER
name|RECOVER
block|}
end_enum

end_unit

