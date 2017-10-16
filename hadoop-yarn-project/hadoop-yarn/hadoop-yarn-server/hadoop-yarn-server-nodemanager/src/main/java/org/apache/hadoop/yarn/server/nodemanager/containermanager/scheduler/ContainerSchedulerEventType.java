begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.scheduler
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
name|scheduler
package|;
end_package

begin_comment
comment|/**  * Event types associated with {@link ContainerSchedulerEvent}.  */
end_comment

begin_enum
DECL|enum|ContainerSchedulerEventType
specifier|public
enum|enum
name|ContainerSchedulerEventType
block|{
DECL|enumConstant|SCHEDULE_CONTAINER
name|SCHEDULE_CONTAINER
block|,
DECL|enumConstant|CONTAINER_COMPLETED
name|CONTAINER_COMPLETED
block|,
DECL|enumConstant|UPDATE_CONTAINER
name|UPDATE_CONTAINER
block|,
comment|// Producer: Node HB response - RM has asked to shed the queue
DECL|enumConstant|SHED_QUEUED_CONTAINERS
name|SHED_QUEUED_CONTAINERS
block|,
DECL|enumConstant|CONTAINER_PAUSED
name|CONTAINER_PAUSED
block|,
DECL|enumConstant|RECOVERY_COMPLETED
name|RECOVERY_COMPLETED
block|}
end_enum

end_unit

