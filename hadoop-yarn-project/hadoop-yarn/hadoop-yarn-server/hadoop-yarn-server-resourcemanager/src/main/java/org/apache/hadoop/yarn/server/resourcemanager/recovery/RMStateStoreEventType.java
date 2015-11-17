begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.recovery
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
name|recovery
package|;
end_package

begin_enum
DECL|enum|RMStateStoreEventType
specifier|public
enum|enum
name|RMStateStoreEventType
block|{
DECL|enumConstant|STORE_APP_ATTEMPT
name|STORE_APP_ATTEMPT
block|,
DECL|enumConstant|STORE_APP
name|STORE_APP
block|,
DECL|enumConstant|UPDATE_APP
name|UPDATE_APP
block|,
DECL|enumConstant|UPDATE_APP_ATTEMPT
name|UPDATE_APP_ATTEMPT
block|,
DECL|enumConstant|REMOVE_APP
name|REMOVE_APP
block|,
DECL|enumConstant|FENCED
name|FENCED
block|,
comment|// Below events should be called synchronously
DECL|enumConstant|STORE_MASTERKEY
name|STORE_MASTERKEY
block|,
DECL|enumConstant|REMOVE_MASTERKEY
name|REMOVE_MASTERKEY
block|,
DECL|enumConstant|STORE_DELEGATION_TOKEN
name|STORE_DELEGATION_TOKEN
block|,
DECL|enumConstant|REMOVE_DELEGATION_TOKEN
name|REMOVE_DELEGATION_TOKEN
block|,
DECL|enumConstant|UPDATE_DELEGATION_TOKEN
name|UPDATE_DELEGATION_TOKEN
block|,
DECL|enumConstant|UPDATE_AMRM_TOKEN
name|UPDATE_AMRM_TOKEN
block|,
DECL|enumConstant|STORE_RESERVATION
name|STORE_RESERVATION
block|,
DECL|enumConstant|REMOVE_RESERVATION
name|REMOVE_RESERVATION
block|, }
end_enum

end_unit

