begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime
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
name|runtime
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|conf
operator|.
name|YarnConfiguration
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
name|PrivilegedOperationExecutor
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
name|runtime
operator|.
name|ContainerExecutionException
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
name|runtime
operator|.
name|ContainerRuntime
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
name|runtime
operator|.
name|ContainerRuntimeContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * This class is a {@link ContainerRuntime} implementation that delegates all  * operations to a {@link DefaultLinuxContainerRuntime} instance, a  * {@link DockerLinuxContainerRuntime} instance, or a  * {@link JavaSandboxLinuxContainerRuntime} instance depending on whether  * each instance believes the operation to be within its scope.  *  * @see DockerLinuxContainerRuntime#isDockerContainerRequested  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|DelegatingLinuxContainerRuntime
specifier|public
class|class
name|DelegatingLinuxContainerRuntime
implements|implements
name|LinuxContainerRuntime
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DelegatingLinuxContainerRuntime
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|defaultLinuxContainerRuntime
specifier|private
name|DefaultLinuxContainerRuntime
name|defaultLinuxContainerRuntime
decl_stmt|;
DECL|field|dockerLinuxContainerRuntime
specifier|private
name|DockerLinuxContainerRuntime
name|dockerLinuxContainerRuntime
decl_stmt|;
DECL|field|javaSandboxLinuxContainerRuntime
specifier|private
name|JavaSandboxLinuxContainerRuntime
name|javaSandboxLinuxContainerRuntime
decl_stmt|;
DECL|field|allowedRuntimes
specifier|private
name|EnumSet
argument_list|<
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
argument_list|>
name|allowedRuntimes
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|initialize (Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|String
index|[]
name|configuredRuntimes
init|=
name|conf
operator|.
name|getTrimmedStrings
argument_list|(
name|YarnConfiguration
operator|.
name|LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|configuredRuntime
range|:
name|configuredRuntimes
control|)
block|{
try|try
block|{
name|allowedRuntimes
operator|.
name|add
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|valueOf
argument_list|(
name|configuredRuntime
operator|.
name|toUpperCase
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"Invalid runtime set in "
operator|+
name|YarnConfiguration
operator|.
name|LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES
operator|+
literal|" : "
operator|+
name|configuredRuntime
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|JAVASANDBOX
argument_list|)
condition|)
block|{
name|javaSandboxLinuxContainerRuntime
operator|=
operator|new
name|JavaSandboxLinuxContainerRuntime
argument_list|(
name|PrivilegedOperationExecutor
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|javaSandboxLinuxContainerRuntime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|DOCKER
argument_list|)
condition|)
block|{
name|dockerLinuxContainerRuntime
operator|=
operator|new
name|DockerLinuxContainerRuntime
argument_list|(
name|PrivilegedOperationExecutor
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|dockerLinuxContainerRuntime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isRuntimeAllowed
argument_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
operator|.
name|DEFAULT
argument_list|)
condition|)
block|{
name|defaultLinuxContainerRuntime
operator|=
operator|new
name|DefaultLinuxContainerRuntime
argument_list|(
name|PrivilegedOperationExecutor
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|defaultLinuxContainerRuntime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|useWhitelistEnv (Map<String, String> env)
specifier|public
name|boolean
name|useWhitelistEnv
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|)
block|{
try|try
block|{
name|LinuxContainerRuntime
name|runtime
init|=
name|pickContainerRuntime
argument_list|(
name|env
argument_list|)
decl_stmt|;
return|return
name|runtime
operator|.
name|useWhitelistEnv
argument_list|(
name|env
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ContainerExecutionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Unable to determine runtime"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|pickContainerRuntime ( Map<String, String> environment)
name|LinuxContainerRuntime
name|pickContainerRuntime
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|LinuxContainerRuntime
name|runtime
decl_stmt|;
comment|//Sandbox checked first to ensure DockerRuntime doesn't circumvent controls
if|if
condition|(
name|javaSandboxLinuxContainerRuntime
operator|!=
literal|null
operator|&&
name|javaSandboxLinuxContainerRuntime
operator|.
name|isSandboxContainerRequested
argument_list|()
condition|)
block|{
name|runtime
operator|=
name|javaSandboxLinuxContainerRuntime
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dockerLinuxContainerRuntime
operator|!=
literal|null
operator|&&
name|DockerLinuxContainerRuntime
operator|.
name|isDockerContainerRequested
argument_list|(
name|environment
argument_list|)
condition|)
block|{
name|runtime
operator|=
name|dockerLinuxContainerRuntime
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|defaultLinuxContainerRuntime
operator|!=
literal|null
operator|&&
operator|!
name|DockerLinuxContainerRuntime
operator|.
name|isDockerContainerRequested
argument_list|(
name|environment
argument_list|)
condition|)
block|{
name|runtime
operator|=
name|defaultLinuxContainerRuntime
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"Requested runtime not allowed."
argument_list|)
throw|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using container runtime: "
operator|+
name|runtime
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|runtime
return|;
block|}
DECL|method|pickContainerRuntime (Container container)
specifier|private
name|LinuxContainerRuntime
name|pickContainerRuntime
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
return|return
name|pickContainerRuntime
argument_list|(
name|container
operator|.
name|getLaunchContext
argument_list|()
operator|.
name|getEnvironment
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|prepareContainer (ContainerRuntimeContext ctx)
specifier|public
name|void
name|prepareContainer
parameter_list|(
name|ContainerRuntimeContext
name|ctx
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|LinuxContainerRuntime
name|runtime
init|=
name|pickContainerRuntime
argument_list|(
name|ctx
operator|.
name|getContainer
argument_list|()
argument_list|)
decl_stmt|;
name|runtime
operator|.
name|prepareContainer
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|launchContainer (ContainerRuntimeContext ctx)
specifier|public
name|void
name|launchContainer
parameter_list|(
name|ContainerRuntimeContext
name|ctx
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|Container
name|container
init|=
name|ctx
operator|.
name|getContainer
argument_list|()
decl_stmt|;
name|LinuxContainerRuntime
name|runtime
init|=
name|pickContainerRuntime
argument_list|(
name|container
argument_list|)
decl_stmt|;
name|runtime
operator|.
name|launchContainer
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|signalContainer (ContainerRuntimeContext ctx)
specifier|public
name|void
name|signalContainer
parameter_list|(
name|ContainerRuntimeContext
name|ctx
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|Container
name|container
init|=
name|ctx
operator|.
name|getContainer
argument_list|()
decl_stmt|;
name|LinuxContainerRuntime
name|runtime
init|=
name|pickContainerRuntime
argument_list|(
name|container
argument_list|)
decl_stmt|;
name|runtime
operator|.
name|signalContainer
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reapContainer (ContainerRuntimeContext ctx)
specifier|public
name|void
name|reapContainer
parameter_list|(
name|ContainerRuntimeContext
name|ctx
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|Container
name|container
init|=
name|ctx
operator|.
name|getContainer
argument_list|()
decl_stmt|;
name|LinuxContainerRuntime
name|runtime
init|=
name|pickContainerRuntime
argument_list|(
name|container
argument_list|)
decl_stmt|;
name|runtime
operator|.
name|reapContainer
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getIpAndHost (Container container)
specifier|public
name|String
index|[]
name|getIpAndHost
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|LinuxContainerRuntime
name|runtime
init|=
name|pickContainerRuntime
argument_list|(
name|container
argument_list|)
decl_stmt|;
return|return
name|runtime
operator|.
name|getIpAndHost
argument_list|(
name|container
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|isRuntimeAllowed ( LinuxContainerRuntimeConstants.RuntimeType runtimeType)
name|boolean
name|isRuntimeAllowed
parameter_list|(
name|LinuxContainerRuntimeConstants
operator|.
name|RuntimeType
name|runtimeType
parameter_list|)
block|{
return|return
name|allowedRuntimes
operator|.
name|contains
argument_list|(
name|runtimeType
argument_list|)
return|;
block|}
block|}
end_class

end_unit

