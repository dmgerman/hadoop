begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements. See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership. The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License. You may obtain a copy of the License at  *  *  http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources
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
operator|.
name|linux
operator|.
name|resources
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
name|InterfaceStability
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
name|Configuration
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|Container
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
name|linux
operator|.
name|privileged
operator|.
name|PrivilegedOperation
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
comment|/**  * Handler interface for resource subsystems' isolation and enforcement. e.g cpu, memory, network, disks etc  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|ResourceHandler
specifier|public
interface|interface
name|ResourceHandler
block|{
comment|/**    * Bootstrap resource susbsystem.    *    * @return (possibly empty) list of operations that require elevated    * privileges    */
DECL|method|bootstrap (Configuration configuration)
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|bootstrap
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|ResourceHandlerException
function_decl|;
comment|/**    * Prepare a resource environment for container launch    *    * @param container Container being launched    * @return (possibly empty) list of operations that require elevated    * privileges e.g a) create a custom cgroup b) add pid for container to tasks    * file for a cgroup.    * @throws ResourceHandlerException    */
DECL|method|preStart (Container container)
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|preStart
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|ResourceHandlerException
function_decl|;
comment|/**    * Require state for container that was already launched    *    * @param containerId id of the container being reacquired.    * @return (possibly empty) list of operations that require elevated    * privileges    * @throws ResourceHandlerException    */
DECL|method|reacquireContainer (ContainerId containerId)
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|reacquireContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|ResourceHandlerException
function_decl|;
comment|/**    * Update state for container that was already launched    *    * @param container the container being updated.    * @return (possibly empty) list of operations that require elevated    * privileges    * @throws ResourceHandlerException    */
DECL|method|updateContainer (Container container)
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|updateContainer
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|ResourceHandlerException
function_decl|;
comment|/**    * Perform any tasks necessary after container completion.    * @param containerId of the container that was completed.    * @return (possibly empty) list of operations that require elevated    * privileges    * @throws ResourceHandlerException    */
DECL|method|postComplete (ContainerId containerId)
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|postComplete
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|ResourceHandlerException
function_decl|;
comment|/**    * Teardown environment for resource subsystem if requested. This method    * needs to be used with care since it could impact running containers.    *    * @return (possibly empty) list of operations that require elevated    * privileges    */
DECL|method|teardown ()
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|teardown
parameter_list|()
throws|throws
name|ResourceHandlerException
function_decl|;
block|}
end_interface

end_unit

