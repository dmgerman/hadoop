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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Simplest release selector simply returns the list  */
end_comment

begin_class
DECL|class|SimpleReleaseSelector
specifier|public
class|class
name|SimpleReleaseSelector
implements|implements
name|ContainerReleaseSelector
block|{
annotation|@
name|Override
DECL|method|sortCandidates (int roleId, List<RoleInstance> candidates)
specifier|public
name|List
argument_list|<
name|RoleInstance
argument_list|>
name|sortCandidates
parameter_list|(
name|int
name|roleId
parameter_list|,
name|List
argument_list|<
name|RoleInstance
argument_list|>
name|candidates
parameter_list|)
block|{
return|return
name|candidates
return|;
block|}
block|}
end_class

end_unit

