begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.api.protocolrecords
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
name|nodemanager
operator|.
name|api
operator|.
name|protocolrecords
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|LocalResource
import|;
end_import

begin_interface
DECL|interface|LocalizerHeartbeatResponse
specifier|public
interface|interface
name|LocalizerHeartbeatResponse
block|{
DECL|method|getLocalizerAction ()
specifier|public
name|LocalizerAction
name|getLocalizerAction
parameter_list|()
function_decl|;
DECL|method|getAllResources ()
specifier|public
name|List
argument_list|<
name|LocalResource
argument_list|>
name|getAllResources
parameter_list|()
function_decl|;
DECL|method|getLocalResource (int i)
specifier|public
name|LocalResource
name|getLocalResource
parameter_list|(
name|int
name|i
parameter_list|)
function_decl|;
DECL|method|setLocalizerAction (LocalizerAction action)
specifier|public
name|void
name|setLocalizerAction
parameter_list|(
name|LocalizerAction
name|action
parameter_list|)
function_decl|;
DECL|method|addAllResources (List<LocalResource> resources)
specifier|public
name|void
name|addAllResources
parameter_list|(
name|List
argument_list|<
name|LocalResource
argument_list|>
name|resources
parameter_list|)
function_decl|;
DECL|method|addResource (LocalResource resource)
specifier|public
name|void
name|addResource
parameter_list|(
name|LocalResource
name|resource
parameter_list|)
function_decl|;
DECL|method|removeResource (int index)
specifier|public
name|void
name|removeResource
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|clearResources ()
specifier|public
name|void
name|clearResources
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

