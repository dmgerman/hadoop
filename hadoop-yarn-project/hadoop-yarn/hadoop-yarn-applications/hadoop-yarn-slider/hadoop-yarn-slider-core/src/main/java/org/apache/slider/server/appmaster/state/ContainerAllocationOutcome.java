begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.state
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
package|;
end_package

begin_comment
comment|/**  * Outcome of the assignment  */
end_comment

begin_enum
DECL|enum|ContainerAllocationOutcome
specifier|public
enum|enum
name|ContainerAllocationOutcome
block|{
comment|/**    * There wasn't a request for this    */
DECL|enumConstant|Unallocated
name|Unallocated
block|,
comment|/**    * Open placement    */
DECL|enumConstant|Open
name|Open
block|,
comment|/**    * Allocated explicitly  where requested    */
DECL|enumConstant|Placed
name|Placed
block|,
comment|/**    * This was an escalated placement    */
DECL|enumConstant|Escalated
name|Escalated
block|}
end_enum

end_unit

