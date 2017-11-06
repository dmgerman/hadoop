begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.containerlaunch
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|containerlaunch
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
name|service
operator|.
name|AbstractService
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Component
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
name|service
operator|.
name|component
operator|.
name|instance
operator|.
name|ComponentInstance
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
name|service
operator|.
name|provider
operator|.
name|ProviderService
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
name|service
operator|.
name|provider
operator|.
name|ProviderFactory
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Service
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
name|service
operator|.
name|utils
operator|.
name|SliderFileSystem
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
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import

begin_class
DECL|class|ContainerLaunchService
specifier|public
class|class
name|ContainerLaunchService
extends|extends
name|AbstractService
block|{
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ContainerLaunchService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|executorService
specifier|private
name|ExecutorService
name|executorService
decl_stmt|;
DECL|field|fs
specifier|private
name|SliderFileSystem
name|fs
decl_stmt|;
DECL|method|ContainerLaunchService (SliderFileSystem fs)
specifier|public
name|ContainerLaunchService
parameter_list|(
name|SliderFileSystem
name|fs
parameter_list|)
block|{
name|super
argument_list|(
name|ContainerLaunchService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|executorService
operator|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|executorService
operator|!=
literal|null
condition|)
block|{
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|launchCompInstance (Service service, ComponentInstance instance, Container container)
specifier|public
name|void
name|launchCompInstance
parameter_list|(
name|Service
name|service
parameter_list|,
name|ComponentInstance
name|instance
parameter_list|,
name|Container
name|container
parameter_list|)
block|{
name|ContainerLauncher
name|launcher
init|=
operator|new
name|ContainerLauncher
argument_list|(
name|service
argument_list|,
name|instance
argument_list|,
name|container
argument_list|)
decl_stmt|;
name|executorService
operator|.
name|execute
argument_list|(
name|launcher
argument_list|)
expr_stmt|;
block|}
DECL|class|ContainerLauncher
specifier|private
class|class
name|ContainerLauncher
implements|implements
name|Runnable
block|{
DECL|field|container
specifier|public
specifier|final
name|Container
name|container
decl_stmt|;
DECL|field|service
specifier|public
specifier|final
name|Service
name|service
decl_stmt|;
DECL|field|instance
specifier|public
name|ComponentInstance
name|instance
decl_stmt|;
DECL|method|ContainerLauncher ( Service service, ComponentInstance instance, Container container)
specifier|public
name|ContainerLauncher
parameter_list|(
name|Service
name|service
parameter_list|,
name|ComponentInstance
name|instance
parameter_list|,
name|Container
name|container
parameter_list|)
block|{
name|this
operator|.
name|container
operator|=
name|container
expr_stmt|;
name|this
operator|.
name|service
operator|=
name|service
expr_stmt|;
name|this
operator|.
name|instance
operator|=
name|instance
expr_stmt|;
block|}
DECL|method|run ()
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Component
name|compSpec
init|=
name|instance
operator|.
name|getCompSpec
argument_list|()
decl_stmt|;
name|ProviderService
name|provider
init|=
name|ProviderFactory
operator|.
name|getProviderService
argument_list|(
name|compSpec
operator|.
name|getArtifact
argument_list|()
argument_list|)
decl_stmt|;
name|AbstractLauncher
name|launcher
init|=
operator|new
name|AbstractLauncher
argument_list|(
name|fs
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|provider
operator|.
name|buildContainerLaunchContext
argument_list|(
name|launcher
argument_list|,
name|service
argument_list|,
name|instance
argument_list|,
name|fs
argument_list|,
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|instance
operator|.
name|getComponent
argument_list|()
operator|.
name|getScheduler
argument_list|()
operator|.
name|getNmClient
argument_list|()
operator|.
name|startContainerAsync
argument_list|(
name|container
argument_list|,
name|launcher
operator|.
name|completeContainerLaunch
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|instance
operator|.
name|getCompInstanceId
argument_list|()
operator|+
literal|": Failed to launch container. "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

