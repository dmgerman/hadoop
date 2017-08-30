begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmapp
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
package|;
end_package

begin_enum
DECL|enum|RMAppEventType
specifier|public
enum|enum
name|RMAppEventType
block|{
comment|// Source: ClientRMService
DECL|enumConstant|START
name|START
block|,
DECL|enumConstant|RECOVER
name|RECOVER
block|,
DECL|enumConstant|KILL
name|KILL
block|,
comment|// Source: Scheduler and RMAppManager
DECL|enumConstant|APP_REJECTED
name|APP_REJECTED
block|,
comment|// Source: Scheduler
DECL|enumConstant|APP_ACCEPTED
name|APP_ACCEPTED
block|,
comment|// Source: RMAppAttempt
DECL|enumConstant|ATTEMPT_REGISTERED
name|ATTEMPT_REGISTERED
block|,
DECL|enumConstant|ATTEMPT_UNREGISTERED
name|ATTEMPT_UNREGISTERED
block|,
DECL|enumConstant|ATTEMPT_FINISHED
name|ATTEMPT_FINISHED
block|,
comment|// Will send the final state
DECL|enumConstant|ATTEMPT_FAILED
name|ATTEMPT_FAILED
block|,
DECL|enumConstant|ATTEMPT_KILLED
name|ATTEMPT_KILLED
block|,
DECL|enumConstant|NODE_UPDATE
name|NODE_UPDATE
block|,
comment|// Source: Container and ResourceTracker
DECL|enumConstant|APP_RUNNING_ON_NODE
name|APP_RUNNING_ON_NODE
block|,
comment|// Source: RMStateStore
DECL|enumConstant|APP_NEW_SAVED
name|APP_NEW_SAVED
block|,
DECL|enumConstant|APP_UPDATE_SAVED
name|APP_UPDATE_SAVED
block|,
DECL|enumConstant|APP_SAVE_FAILED
name|APP_SAVE_FAILED
block|, }
end_enum

end_unit

