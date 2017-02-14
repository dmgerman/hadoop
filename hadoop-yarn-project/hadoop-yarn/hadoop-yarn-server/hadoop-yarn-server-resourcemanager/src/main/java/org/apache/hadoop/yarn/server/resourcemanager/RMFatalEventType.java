begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|enum|RMFatalEventType
specifier|public
enum|enum
name|RMFatalEventType
block|{
comment|// Source<- Store
DECL|enumConstant|STATE_STORE_OP_FAILED
name|STATE_STORE_OP_FAILED
block|,
comment|// Source<- Embedded Elector
DECL|enumConstant|EMBEDDED_ELECTOR_FAILED
name|EMBEDDED_ELECTOR_FAILED
block|,
comment|// Source<- Admin Service
DECL|enumConstant|TRANSITION_TO_ACTIVE_FAILED
name|TRANSITION_TO_ACTIVE_FAILED
block|,
comment|// Source<- Critical Thread Crash
DECL|enumConstant|CRITICAL_THREAD_CRASH
name|CRITICAL_THREAD_CRASH
block|}
end_enum

end_unit

