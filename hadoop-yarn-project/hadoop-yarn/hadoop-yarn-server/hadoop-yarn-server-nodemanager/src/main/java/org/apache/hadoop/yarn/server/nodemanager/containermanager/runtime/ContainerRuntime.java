begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.runtime
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
name|runtime
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
name|hdfs
operator|.
name|protocol
operator|.
name|datatransfer
operator|.
name|IOStreamPair
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
name|executor
operator|.
name|ContainerExecContext
import|;
end_import

begin_comment
comment|/**  * An abstraction for various container runtime implementations. Examples  * include Process Tree, Docker, Appc runtimes etc. These implementations  * are meant for low-level OS container support - dependencies on  * higher-level node mananger constructs should be avoided.  */
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
DECL|interface|ContainerRuntime
specifier|public
interface|interface
name|ContainerRuntime
block|{
comment|/**    * Prepare a container to be ready for launch.    *    * @param ctx the {@link ContainerRuntimeContext}    * @throws ContainerExecutionException if an error occurs while preparing    * the container    */
DECL|method|prepareContainer (ContainerRuntimeContext ctx)
name|void
name|prepareContainer
parameter_list|(
name|ContainerRuntimeContext
name|ctx
parameter_list|)
throws|throws
name|ContainerExecutionException
function_decl|;
comment|/**    * Launch a container.    *    * @param ctx the {@link ContainerRuntimeContext}    * @throws ContainerExecutionException if an error occurs while launching    * the container    */
DECL|method|launchContainer (ContainerRuntimeContext ctx)
name|void
name|launchContainer
parameter_list|(
name|ContainerRuntimeContext
name|ctx
parameter_list|)
throws|throws
name|ContainerExecutionException
function_decl|;
comment|/**    * Relaunch a container.    *    * @param ctx the {@link ContainerRuntimeContext}    * @throws ContainerExecutionException if an error occurs while relaunching    * the container    */
DECL|method|relaunchContainer (ContainerRuntimeContext ctx)
name|void
name|relaunchContainer
parameter_list|(
name|ContainerRuntimeContext
name|ctx
parameter_list|)
throws|throws
name|ContainerExecutionException
function_decl|;
comment|/**    * Signal a container. Signals may be a request to terminate, a status check,    * etc.    *    * @param ctx the {@link ContainerRuntimeContext}    * @throws ContainerExecutionException if an error occurs while signaling    * the container    */
DECL|method|signalContainer (ContainerRuntimeContext ctx)
name|void
name|signalContainer
parameter_list|(
name|ContainerRuntimeContext
name|ctx
parameter_list|)
throws|throws
name|ContainerExecutionException
function_decl|;
comment|/**    * Perform any container cleanup that may be required.    *    * @param ctx the {@link ContainerRuntimeContext}    * @throws ContainerExecutionException if an error occurs while reaping    * the container    */
DECL|method|reapContainer (ContainerRuntimeContext ctx)
name|void
name|reapContainer
parameter_list|(
name|ContainerRuntimeContext
name|ctx
parameter_list|)
throws|throws
name|ContainerExecutionException
function_decl|;
comment|/**    * Run a program in container.    *    * @param ctx the {@link ContainerExecContext}    * @return stdin and stdout of container exec    * @throws ContainerExecutionException    */
DECL|method|execContainer (ContainerExecContext ctx)
name|IOStreamPair
name|execContainer
parameter_list|(
name|ContainerExecContext
name|ctx
parameter_list|)
throws|throws
name|ContainerExecutionException
function_decl|;
comment|/**    * Return the host and ip of the container.    *    * @param container the {@link Container}    * @throws ContainerExecutionException if an error occurs while getting the ip    * and hostname    */
DECL|method|getIpAndHost (Container container)
name|String
index|[]
name|getIpAndHost
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|ContainerExecutionException
function_decl|;
block|}
end_interface

end_unit

