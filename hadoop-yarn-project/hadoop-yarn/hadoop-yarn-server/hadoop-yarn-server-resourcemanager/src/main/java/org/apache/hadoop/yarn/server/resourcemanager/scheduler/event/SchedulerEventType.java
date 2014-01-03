begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.event
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
name|scheduler
operator|.
name|event
package|;
end_package

begin_enum
DECL|enum|SchedulerEventType
specifier|public
enum|enum
name|SchedulerEventType
block|{
comment|// Source: Node
DECL|enumConstant|NODE_ADDED
name|NODE_ADDED
block|,
DECL|enumConstant|NODE_REMOVED
name|NODE_REMOVED
block|,
DECL|enumConstant|NODE_UPDATE
name|NODE_UPDATE
block|,
comment|// Source: RMApp
DECL|enumConstant|APP_ADDED
name|APP_ADDED
block|,
DECL|enumConstant|APP_REMOVED
name|APP_REMOVED
block|,
comment|// Source: RMAppAttempt
DECL|enumConstant|APP_ATTEMPT_ADDED
name|APP_ATTEMPT_ADDED
block|,
DECL|enumConstant|APP_ATTEMPT_REMOVED
name|APP_ATTEMPT_REMOVED
block|,
comment|// Source: ContainerAllocationExpirer
DECL|enumConstant|CONTAINER_EXPIRED
name|CONTAINER_EXPIRED
block|}
end_enum

end_unit

