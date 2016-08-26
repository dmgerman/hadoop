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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * This class is a {@link ContainerRuntime} implementation that delegates all  * operations to either a {@link DefaultLinuxContainerRuntime} instance or a  * {@link DockerLinuxContainerRuntime} instance, depending on whether the  * {@link DockerLinuxContainerRuntime} instance believes the operation to be  * requesting a Docker container.  *  * @see DockerLinuxContainerRuntime#isDockerContainerRequested  */
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
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
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
name|PrivilegedOperationExecutor
name|privilegedOperationExecutor
init|=
name|PrivilegedOperationExecutor
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|defaultLinuxContainerRuntime
operator|=
operator|new
name|DefaultLinuxContainerRuntime
argument_list|(
name|privilegedOperationExecutor
argument_list|)
expr_stmt|;
name|defaultLinuxContainerRuntime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|dockerLinuxContainerRuntime
operator|=
operator|new
name|DockerLinuxContainerRuntime
argument_list|(
name|privilegedOperationExecutor
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
DECL|method|pickContainerRuntime (Container container)
specifier|private
name|LinuxContainerRuntime
name|pickContainerRuntime
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
init|=
name|container
operator|.
name|getLaunchContext
argument_list|()
operator|.
name|getEnvironment
argument_list|()
decl_stmt|;
name|LinuxContainerRuntime
name|runtime
decl_stmt|;
if|if
condition|(
name|DockerLinuxContainerRuntime
operator|.
name|isDockerContainerRequested
argument_list|(
name|env
argument_list|)
condition|)
block|{
name|runtime
operator|=
name|dockerLinuxContainerRuntime
expr_stmt|;
block|}
else|else
block|{
name|runtime
operator|=
name|defaultLinuxContainerRuntime
expr_stmt|;
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
block|}
end_class

end_unit

