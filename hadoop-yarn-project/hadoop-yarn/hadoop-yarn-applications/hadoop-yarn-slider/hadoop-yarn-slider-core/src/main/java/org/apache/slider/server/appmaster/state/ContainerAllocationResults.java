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

begin_import
import|import
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
name|operations
operator|.
name|AbstractRMOperation
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * This is just a tuple of the outcome of a container allocation  */
end_comment

begin_class
DECL|class|ContainerAllocationResults
specifier|public
class|class
name|ContainerAllocationResults
block|{
comment|/**    * What was the outcome of this allocation: placed, escalated, ...    */
DECL|field|outcome
specifier|public
name|ContainerAllocationOutcome
name|outcome
decl_stmt|;
comment|/**    * The outstanding request which originated this.    * This will be null if the outcome is {@link ContainerAllocationOutcome#Unallocated}    * as it wasn't expected.    */
DECL|field|origin
specifier|public
name|OutstandingRequest
name|origin
decl_stmt|;
comment|/**    * A possibly empty list of requests to add to the follow-up actions    */
DECL|field|operations
specifier|public
name|List
argument_list|<
name|AbstractRMOperation
argument_list|>
name|operations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|ContainerAllocationResults ()
specifier|public
name|ContainerAllocationResults
parameter_list|()
block|{   }
block|}
end_class

end_unit

