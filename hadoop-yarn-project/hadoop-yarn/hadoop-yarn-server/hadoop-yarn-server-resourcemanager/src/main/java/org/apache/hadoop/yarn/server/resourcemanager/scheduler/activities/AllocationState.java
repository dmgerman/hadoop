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
comment|/*  * Collection of allocation final states.  */
end_comment

begin_enum
DECL|enum|AllocationState
specifier|public
enum|enum
name|AllocationState
block|{
DECL|enumConstant|DEFAULT
name|DEFAULT
block|,
comment|// queue or application voluntarily give up to use the resource
comment|// OR nothing allocated
DECL|enumConstant|SKIPPED
name|SKIPPED
block|,
comment|// successfully allocate a new non-reserved container
DECL|enumConstant|ALLOCATED
name|ALLOCATED
block|,
comment|// successfully allocate a new container from an existing reserved container
DECL|enumConstant|ALLOCATED_FROM_RESERVED
name|ALLOCATED_FROM_RESERVED
block|,
comment|// successfully reserve a new container
DECL|enumConstant|RESERVED
name|RESERVED
block|}
end_enum

end_unit

