begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.metrics
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
name|metrics
package|;
end_package

begin_enum
DECL|enum|SystemMetricsEventType
specifier|public
enum|enum
name|SystemMetricsEventType
block|{
comment|// app events
DECL|enumConstant|APP_CREATED
name|APP_CREATED
block|,
DECL|enumConstant|APP_FINISHED
name|APP_FINISHED
block|,
DECL|enumConstant|APP_ACLS_UPDATED
name|APP_ACLS_UPDATED
block|,
DECL|enumConstant|APP_UPDATED
name|APP_UPDATED
block|,
comment|// app attempt events
DECL|enumConstant|APP_ATTEMPT_REGISTERED
name|APP_ATTEMPT_REGISTERED
block|,
DECL|enumConstant|APP_ATTEMPT_FINISHED
name|APP_ATTEMPT_FINISHED
block|,
comment|// container events
DECL|enumConstant|CONTAINER_CREATED
name|CONTAINER_CREATED
block|,
DECL|enumConstant|CONTAINER_FINISHED
name|CONTAINER_FINISHED
block|}
end_enum

end_unit

