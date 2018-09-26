begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.component
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|component
package|;
end_package

begin_enum
DECL|enum|ComponentEventType
specifier|public
enum|enum
name|ComponentEventType
block|{
DECL|enumConstant|FLEX
name|FLEX
block|,
DECL|enumConstant|CONTAINER_ALLOCATED
name|CONTAINER_ALLOCATED
block|,
DECL|enumConstant|CONTAINER_RECOVERED
name|CONTAINER_RECOVERED
block|,
DECL|enumConstant|CONTAINER_STARTED
name|CONTAINER_STARTED
block|,
DECL|enumConstant|CONTAINER_COMPLETED
name|CONTAINER_COMPLETED
block|,
DECL|enumConstant|CANCEL_UPGRADE
name|CANCEL_UPGRADE
block|,
DECL|enumConstant|UPGRADE
name|UPGRADE
block|,
DECL|enumConstant|CHECK_STABLE
name|CHECK_STABLE
block|}
end_enum

end_unit

