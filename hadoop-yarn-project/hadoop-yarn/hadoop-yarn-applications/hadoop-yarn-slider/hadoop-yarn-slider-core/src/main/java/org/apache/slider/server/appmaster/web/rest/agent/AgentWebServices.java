begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.web.rest.agent
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
name|web
operator|.
name|rest
operator|.
name|agent
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
name|web
operator|.
name|WebAppApi
import|;
end_import

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
name|web
operator|.
name|rest
operator|.
name|RestPaths
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Context
import|;
end_import

begin_comment
comment|/** The available agent REST services exposed by a slider AM. */
end_comment

begin_class
annotation|@
name|Path
argument_list|(
name|RestPaths
operator|.
name|SLIDER_AGENT_CONTEXT_ROOT
argument_list|)
DECL|class|AgentWebServices
specifier|public
class|class
name|AgentWebServices
block|{
comment|/** AM/WebApp info object */
annotation|@
name|Context
DECL|field|slider
specifier|private
name|WebAppApi
name|slider
decl_stmt|;
DECL|method|AgentWebServices ()
specifier|public
name|AgentWebServices
parameter_list|()
block|{   }
annotation|@
name|Path
argument_list|(
name|RestPaths
operator|.
name|SLIDER_SUBPATH_AGENTS
argument_list|)
DECL|method|getAgentResource ()
specifier|public
name|AgentResource
name|getAgentResource
parameter_list|()
block|{
return|return
operator|new
name|AgentResource
argument_list|(
name|slider
argument_list|)
return|;
block|}
block|}
end_class

end_unit

