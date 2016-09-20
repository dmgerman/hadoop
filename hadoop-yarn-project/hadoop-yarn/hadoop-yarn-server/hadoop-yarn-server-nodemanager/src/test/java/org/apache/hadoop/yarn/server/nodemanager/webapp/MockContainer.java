begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.webapp
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
name|webapp
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
name|fs
operator|.
name|Path
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
name|security
operator|.
name|Credentials
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
name|ApplicationAttemptId
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
name|ContainerLaunchContext
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
name|ContainerStatus
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
name|Priority
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
name|event
operator|.
name|Dispatcher
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|security
operator|.
name|ContainerTokenIdentifier
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
name|protocolrecords
operator|.
name|NMContainerStatus
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
name|container
operator|.
name|ContainerEvent
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|ResourceSet
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
name|utils
operator|.
name|BuilderUtils
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
name|HashMap
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
DECL|class|MockContainer
specifier|public
class|class
name|MockContainer
implements|implements
name|Container
block|{
DECL|field|id
specifier|private
name|ContainerId
name|id
decl_stmt|;
DECL|field|state
specifier|private
name|ContainerState
name|state
decl_stmt|;
DECL|field|user
specifier|private
name|String
name|user
decl_stmt|;
DECL|field|launchContext
specifier|private
name|ContainerLaunchContext
name|launchContext
decl_stmt|;
DECL|field|resource
specifier|private
specifier|final
name|Map
argument_list|<
name|Path
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|resource
init|=
operator|new
name|HashMap
argument_list|<
name|Path
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|recordFactory
specifier|private
name|RecordFactory
name|recordFactory
decl_stmt|;
DECL|field|containerTokenIdentifier
specifier|private
specifier|final
name|ContainerTokenIdentifier
name|containerTokenIdentifier
decl_stmt|;
DECL|method|MockContainer (ApplicationAttemptId appAttemptId, Dispatcher dispatcher, Configuration conf, String user, ApplicationId appId, int uniqId)
specifier|public
name|MockContainer
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|Dispatcher
name|dispatcher
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|String
name|user
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|int
name|uniqId
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|recordFactory
operator|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|recordFactory
argument_list|,
name|appId
argument_list|,
name|appAttemptId
argument_list|,
name|uniqId
argument_list|)
expr_stmt|;
name|this
operator|.
name|launchContext
operator|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
expr_stmt|;
name|long
name|currentTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|this
operator|.
name|containerTokenIdentifier
operator|=
name|BuilderUtils
operator|.
name|newContainerTokenIdentifier
argument_list|(
name|BuilderUtils
operator|.
name|newContainerToken
argument_list|(
name|id
argument_list|,
literal|0
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|1234
argument_list|,
name|user
argument_list|,
name|BuilderUtils
operator|.
name|newResource
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
name|currentTime
operator|+
literal|10000
argument_list|,
literal|123
argument_list|,
literal|"password"
operator|.
name|getBytes
argument_list|()
argument_list|,
name|currentTime
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|ContainerState
operator|.
name|NEW
expr_stmt|;
block|}
DECL|method|setState (ContainerState state)
specifier|public
name|void
name|setState
parameter_list|(
name|ContainerState
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
annotation|@
name|Override
DECL|method|getContainerState ()
specifier|public
name|ContainerState
name|getContainerState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
annotation|@
name|Override
DECL|method|getLaunchContext ()
specifier|public
name|ContainerLaunchContext
name|getLaunchContext
parameter_list|()
block|{
return|return
name|launchContext
return|;
block|}
annotation|@
name|Override
DECL|method|getCredentials ()
specifier|public
name|Credentials
name|getCredentials
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getLocalizedResources ()
specifier|public
name|Map
argument_list|<
name|Path
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getLocalizedResources
parameter_list|()
block|{
return|return
name|resource
return|;
block|}
annotation|@
name|Override
DECL|method|cloneAndGetContainerStatus ()
specifier|public
name|ContainerStatus
name|cloneAndGetContainerStatus
parameter_list|()
block|{
name|ContainerStatus
name|containerStatus
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|containerStatus
operator|.
name|setState
argument_list|(
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
name|ContainerState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|containerStatus
operator|.
name|setDiagnostics
argument_list|(
literal|"testing"
argument_list|)
expr_stmt|;
name|containerStatus
operator|.
name|setExitStatus
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|containerStatus
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
annotation|@
name|Override
DECL|method|getResourceSet ()
specifier|public
name|ResourceSet
name|getResourceSet
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|handle (ContainerEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|ContainerEvent
name|event
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|getContainerId ()
specifier|public
name|ContainerId
name|getContainerId
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
annotation|@
name|Override
DECL|method|getResource ()
specifier|public
name|Resource
name|getResource
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerTokenIdentifier
operator|.
name|getResource
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setResource (Resource targetResource)
specifier|public
name|void
name|setResource
parameter_list|(
name|Resource
name|targetResource
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|getContainerTokenIdentifier ()
specifier|public
name|ContainerTokenIdentifier
name|getContainerTokenIdentifier
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerTokenIdentifier
return|;
block|}
annotation|@
name|Override
DECL|method|getNMContainerStatus ()
specifier|public
name|NMContainerStatus
name|getNMContainerStatus
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|isRetryContextSet ()
specifier|public
name|boolean
name|isRetryContextSet
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|shouldRetry (int errorCode)
specifier|public
name|boolean
name|shouldRetry
parameter_list|(
name|int
name|errorCode
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getWorkDir ()
specifier|public
name|String
name|getWorkDir
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setWorkDir (String workDir)
specifier|public
name|void
name|setWorkDir
parameter_list|(
name|String
name|workDir
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|getLogDir ()
specifier|public
name|String
name|getLogDir
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setLogDir (String logDir)
specifier|public
name|void
name|setLogDir
parameter_list|(
name|String
name|logDir
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|getPriority ()
specifier|public
name|Priority
name|getPriority
parameter_list|()
block|{
return|return
name|Priority
operator|.
name|UNDEFINED
return|;
block|}
annotation|@
name|Override
DECL|method|setIpAndHost (String[] ipAndHost)
specifier|public
name|void
name|setIpAndHost
parameter_list|(
name|String
index|[]
name|ipAndHost
parameter_list|)
block|{    }
annotation|@
name|Override
DECL|method|isRunning ()
specifier|public
name|boolean
name|isRunning
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|setIsReInitializing (boolean isReInitializing)
specifier|public
name|void
name|setIsReInitializing
parameter_list|(
name|boolean
name|isReInitializing
parameter_list|)
block|{    }
annotation|@
name|Override
DECL|method|isReInitializing ()
specifier|public
name|boolean
name|isReInitializing
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|canRollback ()
specifier|public
name|boolean
name|canRollback
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|commitUpgrade ()
specifier|public
name|void
name|commitUpgrade
parameter_list|()
block|{    }
block|}
end_class

end_unit

