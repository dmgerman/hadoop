begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements. See the NOTICE file * distributed with this work for additional information * regarding copyright ownership. The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License. You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.util
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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|conf
operator|.
name|Configurable
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
name|ContainerId
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
name|Resource
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
name|server
operator|.
name|nodemanager
operator|.
name|LinuxContainerExecutor
import|;
end_import

begin_interface
annotation|@
name|Deprecated
DECL|interface|LCEResourcesHandler
specifier|public
interface|interface
name|LCEResourcesHandler
extends|extends
name|Configurable
block|{
DECL|method|init (LinuxContainerExecutor lce)
name|void
name|init
parameter_list|(
name|LinuxContainerExecutor
name|lce
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Called by the LinuxContainerExecutor before launching the executable    * inside the container.    * @param containerId the id of the container being launched    * @param containerResource the node resources the container will be using    */
DECL|method|preExecute (ContainerId containerId, Resource containerResource)
name|void
name|preExecute
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|Resource
name|containerResource
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Called by the LinuxContainerExecutor after the executable inside the    * container has exited (successfully or not).    * @param containerId the id of the container which was launched    */
DECL|method|postExecute (ContainerId containerId)
name|void
name|postExecute
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
function_decl|;
DECL|method|getResourcesOption (ContainerId containerId)
name|String
name|getResourcesOption
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

