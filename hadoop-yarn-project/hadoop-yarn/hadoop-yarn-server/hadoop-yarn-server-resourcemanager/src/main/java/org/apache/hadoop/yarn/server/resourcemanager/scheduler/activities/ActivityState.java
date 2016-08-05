begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.activities
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
name|activities
package|;
end_package

begin_comment
comment|/*  * Collection of activity operation states.  */
end_comment

begin_enum
DECL|enum|ActivityState
specifier|public
enum|enum
name|ActivityState
block|{
comment|// default state when adding a new activity in node allocation
DECL|enumConstant|DEFAULT
name|DEFAULT
block|,
comment|// container is allocated to sub-queues/applications or this queue/application
DECL|enumConstant|ACCEPTED
name|ACCEPTED
block|,
comment|// queue or application voluntarily give up to use the resource OR
comment|// nothing allocated
DECL|enumConstant|SKIPPED
name|SKIPPED
block|,
comment|// container could not be allocated to sub-queues or this application
DECL|enumConstant|REJECTED
name|REJECTED
block|,
DECL|enumConstant|ALLOCATED
name|ALLOCATED
block|,
comment|// successfully allocate a new non-reserved container
DECL|enumConstant|RESERVED
name|RESERVED
block|,
comment|// successfully reserve a new container
DECL|enumConstant|RE_RESERVED
name|RE_RESERVED
comment|// successfully reserve a new container
block|}
end_enum

end_unit

