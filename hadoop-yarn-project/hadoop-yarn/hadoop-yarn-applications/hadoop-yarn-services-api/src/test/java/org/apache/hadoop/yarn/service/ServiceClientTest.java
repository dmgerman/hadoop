begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|exceptions
operator|.
name|ApplicationNotFoundException
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
name|exceptions
operator|.
name|YarnException
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
name|Artifact
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
name|ContainerState
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
name|client
operator|.
name|ServiceClient
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
name|ServiceApiUtil
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
comment|/**  * A mock version of ServiceClient - This class is design  * to simulate various error conditions that will happen  * when a consumer class calls ServiceClient.  */
end_comment

begin_class
DECL|class|ServiceClientTest
specifier|public
class|class
name|ServiceClientTest
extends|extends
name|ServiceClient
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|goodServiceStatus
specifier|private
name|Service
name|goodServiceStatus
init|=
name|buildLiveGoodService
argument_list|()
decl_stmt|;
DECL|field|initialized
specifier|private
name|boolean
name|initialized
decl_stmt|;
DECL|method|ServiceClientTest ()
specifier|public
name|ServiceClientTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|initialized
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
comment|// This is needed for testing  API Server which use client to get status
comment|// and then perform an action.
block|}
DECL|method|forceStop ()
specifier|public
name|void
name|forceStop
parameter_list|()
block|{
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConfig ()
specifier|public
name|Configuration
name|getConfig
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|actionCreate (Service service)
specifier|public
name|ApplicationId
name|actionCreate
parameter_list|(
name|Service
name|service
parameter_list|)
throws|throws
name|IOException
block|{
name|ServiceApiUtil
operator|.
name|validateAndResolveService
argument_list|(
name|service
argument_list|,
operator|new
name|SliderFileSystem
argument_list|(
name|conf
argument_list|)
argument_list|,
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getStatus (String appName)
specifier|public
name|Service
name|getStatus
parameter_list|(
name|String
name|appName
parameter_list|)
block|{
if|if
condition|(
name|appName
operator|!=
literal|null
operator|&&
name|appName
operator|.
name|equals
argument_list|(
literal|"jenkins"
argument_list|)
condition|)
block|{
return|return
name|goodServiceStatus
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|actionStart (String serviceName)
specifier|public
name|int
name|actionStart
parameter_list|(
name|String
name|serviceName
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
if|if
condition|(
name|serviceName
operator|!=
literal|null
operator|&&
name|serviceName
operator|.
name|equals
argument_list|(
literal|"jenkins"
argument_list|)
condition|)
block|{
return|return
name|EXIT_SUCCESS
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ApplicationNotFoundException
argument_list|(
literal|""
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|actionStop (String serviceName, boolean waitForAppStopped)
specifier|public
name|int
name|actionStop
parameter_list|(
name|String
name|serviceName
parameter_list|,
name|boolean
name|waitForAppStopped
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
if|if
condition|(
name|serviceName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
if|if
condition|(
name|serviceName
operator|.
name|equals
argument_list|(
literal|"jenkins"
argument_list|)
condition|)
block|{
return|return
name|EXIT_SUCCESS
return|;
block|}
elseif|else
if|if
condition|(
name|serviceName
operator|.
name|equals
argument_list|(
literal|"jenkins-second-stop"
argument_list|)
condition|)
block|{
return|return
name|EXIT_COMMAND_ARGUMENT_ERROR
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ApplicationNotFoundException
argument_list|(
literal|""
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|actionDestroy (String serviceName)
specifier|public
name|int
name|actionDestroy
parameter_list|(
name|String
name|serviceName
parameter_list|)
block|{
if|if
condition|(
name|serviceName
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|serviceName
operator|.
name|equals
argument_list|(
literal|"jenkins"
argument_list|)
condition|)
block|{
return|return
name|EXIT_SUCCESS
return|;
block|}
elseif|else
if|if
condition|(
name|serviceName
operator|.
name|equals
argument_list|(
literal|"jenkins-already-stopped"
argument_list|)
condition|)
block|{
return|return
name|EXIT_SUCCESS
return|;
block|}
elseif|else
if|if
condition|(
name|serviceName
operator|.
name|equals
argument_list|(
literal|"jenkins-doesn't-exist"
argument_list|)
condition|)
block|{
return|return
name|EXIT_NOT_FOUND
return|;
block|}
elseif|else
if|if
condition|(
name|serviceName
operator|.
name|equals
argument_list|(
literal|"jenkins-error-cleaning-registry"
argument_list|)
condition|)
block|{
return|return
name|EXIT_OTHER_FAILURE
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|initiateUpgrade (Service service)
specifier|public
name|int
name|initiateUpgrade
parameter_list|(
name|Service
name|service
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
if|if
condition|(
name|service
operator|.
name|getName
argument_list|()
operator|!=
literal|null
operator|&&
name|service
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"jenkins"
argument_list|)
condition|)
block|{
return|return
name|EXIT_SUCCESS
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|actionUpgrade (Service service, List<Container> compInstances)
specifier|public
name|int
name|actionUpgrade
parameter_list|(
name|Service
name|service
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|compInstances
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
if|if
condition|(
name|service
operator|.
name|getName
argument_list|()
operator|!=
literal|null
operator|&&
name|service
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"jenkins"
argument_list|)
condition|)
block|{
return|return
name|EXIT_SUCCESS
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
block|}
DECL|method|getGoodServiceStatus ()
name|Service
name|getGoodServiceStatus
parameter_list|()
block|{
return|return
name|goodServiceStatus
return|;
block|}
DECL|method|buildGoodService ()
specifier|static
name|Service
name|buildGoodService
parameter_list|()
block|{
name|Service
name|service
init|=
operator|new
name|Service
argument_list|()
decl_stmt|;
name|service
operator|.
name|setName
argument_list|(
literal|"jenkins"
argument_list|)
expr_stmt|;
name|service
operator|.
name|setVersion
argument_list|(
literal|"v1"
argument_list|)
expr_stmt|;
name|Artifact
name|artifact
init|=
operator|new
name|Artifact
argument_list|()
decl_stmt|;
name|artifact
operator|.
name|setType
argument_list|(
name|Artifact
operator|.
name|TypeEnum
operator|.
name|DOCKER
argument_list|)
expr_stmt|;
name|artifact
operator|.
name|setId
argument_list|(
literal|"jenkins:latest"
argument_list|)
expr_stmt|;
name|Resource
name|resource
init|=
operator|new
name|Resource
argument_list|()
decl_stmt|;
name|resource
operator|.
name|setCpus
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setMemory
argument_list|(
literal|"2048"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Component
argument_list|>
name|components
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Component
name|c
init|=
operator|new
name|Component
argument_list|()
decl_stmt|;
name|c
operator|.
name|setName
argument_list|(
literal|"jenkins"
argument_list|)
expr_stmt|;
name|c
operator|.
name|setNumberOfContainers
argument_list|(
literal|2L
argument_list|)
expr_stmt|;
name|c
operator|.
name|setArtifact
argument_list|(
name|artifact
argument_list|)
expr_stmt|;
name|c
operator|.
name|setLaunchCommand
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|c
operator|.
name|setResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|components
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|service
operator|.
name|setComponents
argument_list|(
name|components
argument_list|)
expr_stmt|;
return|return
name|service
return|;
block|}
DECL|method|buildLiveGoodService ()
specifier|static
name|Service
name|buildLiveGoodService
parameter_list|()
block|{
name|Service
name|service
init|=
name|buildGoodService
argument_list|()
decl_stmt|;
name|Component
name|comp
init|=
name|service
operator|.
name|getComponents
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Container
argument_list|>
name|containers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|comp
operator|.
name|getNumberOfContainers
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Container
name|container
init|=
operator|new
name|Container
argument_list|()
decl_stmt|;
name|container
operator|.
name|setComponentInstanceName
argument_list|(
name|comp
operator|.
name|getName
argument_list|()
operator|+
literal|"-"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|container
operator|.
name|setState
argument_list|(
name|ContainerState
operator|.
name|READY
argument_list|)
expr_stmt|;
name|containers
operator|.
name|add
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
name|comp
operator|.
name|setContainers
argument_list|(
name|containers
argument_list|)
expr_stmt|;
return|return
name|service
return|;
block|}
block|}
end_class

end_unit

