begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.client
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
name|client
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
name|protocolrecords
operator|.
name|GetApplicationsRequest
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
name|ApplicationAttemptReport
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
name|ApplicationReport
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
name|YarnApplicationAttemptState
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
name|YarnApplicationState
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
name|client
operator|.
name|api
operator|.
name|YarnClient
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
name|ClientAMProtocol
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
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|CompInstancesUpgradeRequestProto
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
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|CompInstancesUpgradeResponseProto
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
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|UpgradeServiceRequestProto
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
name|proto
operator|.
name|ClientAMProtocol
operator|.
name|UpgradeServiceResponseProto
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
name|ServiceTestUtils
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
name|api
operator|.
name|records
operator|.
name|ServiceState
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
name|conf
operator|.
name|YarnServiceConf
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
name|exceptions
operator|.
name|ErrorStrings
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
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Matchers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * Tests for {@link ServiceClient}.  */
end_comment

begin_class
DECL|class|TestServiceClient
specifier|public
class|class
name|TestServiceClient
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
name|TestServiceClient
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|rule
specifier|public
name|ServiceTestUtils
operator|.
name|ServiceFSWatcher
name|rule
init|=
operator|new
name|ServiceTestUtils
operator|.
name|ServiceFSWatcher
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testUpgradeDisabledByDefault ()
specifier|public
name|void
name|testUpgradeDisabledByDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|Service
name|service
init|=
name|createService
argument_list|()
decl_stmt|;
name|ServiceClient
name|client
init|=
name|MockServiceClient
operator|.
name|create
argument_list|(
name|rule
argument_list|,
name|service
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|//upgrade the service
name|service
operator|.
name|setVersion
argument_list|(
literal|"v2"
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|initiateUpgrade
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ErrorStrings
operator|.
name|SERVICE_UPGRADE_DISABLED
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testActionServiceUpgrade ()
specifier|public
name|void
name|testActionServiceUpgrade
parameter_list|()
throws|throws
name|Exception
block|{
name|Service
name|service
init|=
name|createService
argument_list|()
decl_stmt|;
name|ServiceClient
name|client
init|=
name|MockServiceClient
operator|.
name|create
argument_list|(
name|rule
argument_list|,
name|service
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|//upgrade the service
name|service
operator|.
name|setVersion
argument_list|(
literal|"v2"
argument_list|)
expr_stmt|;
name|client
operator|.
name|initiateUpgrade
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|Service
name|fromFs
init|=
name|ServiceApiUtil
operator|.
name|loadServiceUpgrade
argument_list|(
name|rule
operator|.
name|getFs
argument_list|()
argument_list|,
name|service
operator|.
name|getName
argument_list|()
argument_list|,
name|service
operator|.
name|getVersion
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|service
operator|.
name|getName
argument_list|()
argument_list|,
name|fromFs
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|service
operator|.
name|getVersion
argument_list|()
argument_list|,
name|fromFs
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testActionCompInstanceUpgrade ()
specifier|public
name|void
name|testActionCompInstanceUpgrade
parameter_list|()
throws|throws
name|Exception
block|{
name|Service
name|service
init|=
name|createService
argument_list|()
decl_stmt|;
name|MockServiceClient
name|client
init|=
name|MockServiceClient
operator|.
name|create
argument_list|(
name|rule
argument_list|,
name|service
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|//upgrade the service
name|service
operator|.
name|setVersion
argument_list|(
literal|"v2"
argument_list|)
expr_stmt|;
name|client
operator|.
name|initiateUpgrade
argument_list|(
name|service
argument_list|)
expr_stmt|;
comment|//add containers to the component that needs to be upgraded.
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
name|ContainerId
name|containerId
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|client
operator|.
name|attemptId
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
name|comp
operator|.
name|addContainer
argument_list|(
operator|new
name|Container
argument_list|()
operator|.
name|id
argument_list|(
name|containerId
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|actionUpgrade
argument_list|(
name|service
argument_list|,
name|comp
operator|.
name|getContainers
argument_list|()
argument_list|)
expr_stmt|;
name|CompInstancesUpgradeResponseProto
name|response
init|=
name|client
operator|.
name|getLastProxyResponse
argument_list|(
name|CompInstancesUpgradeResponseProto
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"upgrade did not complete"
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|client
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|createService ()
specifier|private
name|Service
name|createService
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|Service
name|service
init|=
name|ServiceTestUtils
operator|.
name|createExampleApplication
argument_list|()
decl_stmt|;
name|service
operator|.
name|setVersion
argument_list|(
literal|"v1"
argument_list|)
expr_stmt|;
name|service
operator|.
name|setState
argument_list|(
name|ServiceState
operator|.
name|UPGRADING
argument_list|)
expr_stmt|;
return|return
name|service
return|;
block|}
DECL|class|MockServiceClient
specifier|private
specifier|static
specifier|final
class|class
name|MockServiceClient
extends|extends
name|ServiceClient
block|{
DECL|field|appId
specifier|private
specifier|final
name|ApplicationId
name|appId
decl_stmt|;
DECL|field|attemptId
specifier|private
specifier|final
name|ApplicationAttemptId
name|attemptId
decl_stmt|;
DECL|field|amProxy
specifier|private
specifier|final
name|ClientAMProtocol
name|amProxy
decl_stmt|;
DECL|field|proxyResponse
specifier|private
name|Object
name|proxyResponse
decl_stmt|;
DECL|field|service
specifier|private
name|Service
name|service
decl_stmt|;
DECL|method|MockServiceClient ()
specifier|private
name|MockServiceClient
parameter_list|()
block|{
name|amProxy
operator|=
name|mock
argument_list|(
name|ClientAMProtocol
operator|.
name|class
argument_list|)
expr_stmt|;
name|appId
operator|=
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
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"mocking service client for {}"
argument_list|,
name|appId
argument_list|)
expr_stmt|;
name|attemptId
operator|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|create (ServiceTestUtils.ServiceFSWatcher rule, Service service, boolean enableUpgrade)
specifier|static
name|MockServiceClient
name|create
parameter_list|(
name|ServiceTestUtils
operator|.
name|ServiceFSWatcher
name|rule
parameter_list|,
name|Service
name|service
parameter_list|,
name|boolean
name|enableUpgrade
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|MockServiceClient
name|client
init|=
operator|new
name|MockServiceClient
argument_list|()
decl_stmt|;
name|YarnClient
name|yarnClient
init|=
name|createMockYarnClient
argument_list|()
decl_stmt|;
name|ApplicationReport
name|appReport
init|=
name|mock
argument_list|(
name|ApplicationReport
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|appReport
operator|.
name|getHost
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"localhost"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|appReport
operator|.
name|getYarnApplicationState
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|YarnApplicationState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|ApplicationAttemptReport
name|attemptReport
init|=
name|ApplicationAttemptReport
operator|.
name|newInstance
argument_list|(
name|client
operator|.
name|attemptId
argument_list|,
literal|"localhost"
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|YarnApplicationAttemptState
operator|.
name|RUNNING
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|yarnClient
operator|.
name|getApplicationAttemptReport
argument_list|(
name|Matchers
operator|.
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attemptReport
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|yarnClient
operator|.
name|getApplicationReport
argument_list|(
name|client
operator|.
name|appId
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appReport
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|client
operator|.
name|amProxy
operator|.
name|upgrade
argument_list|(
name|Matchers
operator|.
name|any
argument_list|(
name|UpgradeServiceRequestProto
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|(
name|Answer
argument_list|<
name|UpgradeServiceResponseProto
argument_list|>
operator|)
name|invocation
lambda|->
block|{
name|UpgradeServiceResponseProto
name|response
init|=
name|UpgradeServiceResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|client
operator|.
name|proxyResponse
operator|=
name|response
expr_stmt|;
return|return
name|response
return|;
block|}
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|client
operator|.
name|amProxy
operator|.
name|upgrade
argument_list|(
name|Matchers
operator|.
name|any
argument_list|(
name|CompInstancesUpgradeRequestProto
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|(
name|Answer
argument_list|<
name|CompInstancesUpgradeResponseProto
argument_list|>
operator|)
name|invocation
lambda|->
block|{
name|CompInstancesUpgradeResponseProto
name|response
init|=
name|CompInstancesUpgradeResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|client
operator|.
name|proxyResponse
operator|=
name|response
expr_stmt|;
return|return
name|response
return|;
block|}
argument_list|)
expr_stmt|;
name|client
operator|.
name|setFileSystem
argument_list|(
name|rule
operator|.
name|getFs
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|setYarnClient
argument_list|(
name|yarnClient
argument_list|)
expr_stmt|;
name|client
operator|.
name|service
operator|=
name|service
expr_stmt|;
name|rule
operator|.
name|getConf
argument_list|()
operator|.
name|setBoolean
argument_list|(
name|YarnServiceConf
operator|.
name|YARN_SERVICE_UPGRADE_ENABLED
argument_list|,
name|enableUpgrade
argument_list|)
expr_stmt|;
name|client
operator|.
name|init
argument_list|(
name|rule
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
name|client
operator|.
name|actionCreate
argument_list|(
name|service
argument_list|)
expr_stmt|;
return|return
name|client
return|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration configuration)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|Exception
block|{     }
annotation|@
name|Override
DECL|method|createAMProxy (String serviceName, ApplicationReport appReport)
specifier|protected
name|ClientAMProtocol
name|createAMProxy
parameter_list|(
name|String
name|serviceName
parameter_list|,
name|ApplicationReport
name|appReport
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
return|return
name|amProxy
return|;
block|}
annotation|@
name|Override
DECL|method|submitApp (Service app)
name|ApplicationId
name|submitApp
parameter_list|(
name|Service
name|app
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
return|return
name|appId
return|;
block|}
annotation|@
name|Override
DECL|method|getStatus (String serviceName)
specifier|public
name|Service
name|getStatus
parameter_list|(
name|String
name|serviceName
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|service
operator|.
name|setState
argument_list|(
name|ServiceState
operator|.
name|STABLE
argument_list|)
expr_stmt|;
return|return
name|service
return|;
block|}
DECL|method|getLastProxyResponse (Class<T> clazz)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|T
name|getLastProxyResponse
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|.
name|isInstance
argument_list|(
name|proxyResponse
argument_list|)
condition|)
block|{
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|proxyResponse
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
DECL|method|createMockYarnClient ()
specifier|private
specifier|static
name|YarnClient
name|createMockYarnClient
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|YarnClient
name|yarnClient
init|=
name|mock
argument_list|(
name|YarnClient
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|yarnClient
operator|.
name|getApplications
argument_list|(
name|Matchers
operator|.
name|any
argument_list|(
name|GetApplicationsRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|yarnClient
return|;
block|}
block|}
end_class

end_unit

