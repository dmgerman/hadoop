begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Context
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
name|ContainerRuntimeConstants
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|MockLinuxContainerRuntime
specifier|public
class|class
name|MockLinuxContainerRuntime
implements|implements
name|LinuxContainerRuntime
block|{
annotation|@
name|Override
DECL|method|initialize (Configuration conf, Context nmContext)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Context
name|nmContext
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|isRuntimeRequested (Map<String, String> env)
specifier|public
name|boolean
name|isRuntimeRequested
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
if|if
condition|(
name|env
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|type
init|=
name|env
operator|.
name|get
argument_list|(
name|ContainerRuntimeConstants
operator|.
name|ENV_CONTAINER_TYPE
argument_list|)
decl_stmt|;
return|return
name|type
operator|!=
literal|null
operator|&&
name|type
operator|.
name|equals
argument_list|(
literal|"mock"
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
block|{}
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
block|{}
annotation|@
name|Override
DECL|method|relaunchContainer (ContainerRuntimeContext ctx)
specifier|public
name|void
name|relaunchContainer
parameter_list|(
name|ContainerRuntimeContext
name|ctx
parameter_list|)
block|{}
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
block|{}
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
block|{}
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
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|execContainer (ContainerExecContext ctx)
specifier|public
name|IOStreamPair
name|execContainer
parameter_list|(
name|ContainerExecContext
name|ctx
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

