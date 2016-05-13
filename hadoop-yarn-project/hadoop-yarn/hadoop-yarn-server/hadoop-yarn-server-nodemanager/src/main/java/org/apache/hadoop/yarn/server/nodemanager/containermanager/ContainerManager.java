begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager
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
name|containermanager
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
name|service
operator|.
name|ServiceStateChangeListener
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
name|ContainerManagementProtocol
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
name|event
operator|.
name|EventHandler
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
name|api
operator|.
name|records
operator|.
name|ContainerQueuingLimit
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
name|ContainerManagerEvent
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
name|containermanager
operator|.
name|monitor
operator|.
name|ContainersMonitor
import|;
end_import

begin_comment
comment|/**  * The ContainerManager is an entity that manages the life cycle of Containers.  */
end_comment

begin_interface
DECL|interface|ContainerManager
specifier|public
interface|interface
name|ContainerManager
extends|extends
name|ServiceStateChangeListener
extends|,
name|ContainerManagementProtocol
extends|,
name|EventHandler
argument_list|<
name|ContainerManagerEvent
argument_list|>
block|{
DECL|method|getContainersMonitor ()
name|ContainersMonitor
name|getContainersMonitor
parameter_list|()
function_decl|;
DECL|method|updateQueuingLimit (ContainerQueuingLimit queuingLimit)
name|void
name|updateQueuingLimit
parameter_list|(
name|ContainerQueuingLimit
name|queuingLimit
parameter_list|)
function_decl|;
DECL|method|setBlockNewContainerRequests (boolean blockNewContainerRequests)
name|void
name|setBlockNewContainerRequests
parameter_list|(
name|boolean
name|blockNewContainerRequests
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

