begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|RegistryOperations
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
name|ComponentState
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
name|exceptions
operator|.
name|SliderException
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
name|registry
operator|.
name|YarnRegistryViewForProviders
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
name|Map
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

begin_comment
comment|/**  * Tests for {@link ServiceManager}.  */
end_comment

begin_class
DECL|class|TestServiceManager
specifier|public
class|class
name|TestServiceManager
block|{
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
DECL|method|testUpgrade ()
specifier|public
name|void
name|testUpgrade
parameter_list|()
throws|throws
name|IOException
throws|,
name|SliderException
block|{
name|ServiceManager
name|serviceManager
init|=
name|createTestServiceManager
argument_list|(
literal|"testUpgrade"
argument_list|)
decl_stmt|;
name|upgrade
argument_list|(
name|serviceManager
argument_list|,
literal|"v2"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"service not upgraded"
argument_list|,
name|ServiceState
operator|.
name|UPGRADING
argument_list|,
name|serviceManager
operator|.
name|getServiceSpec
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRestartNothingToUpgrade ()
specifier|public
name|void
name|testRestartNothingToUpgrade
parameter_list|()
throws|throws
name|IOException
throws|,
name|SliderException
block|{
name|ServiceManager
name|serviceManager
init|=
name|createTestServiceManager
argument_list|(
literal|"testRestartNothingToUpgrade"
argument_list|)
decl_stmt|;
name|upgrade
argument_list|(
name|serviceManager
argument_list|,
literal|"v2"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//make components stable
name|serviceManager
operator|.
name|getServiceSpec
argument_list|()
operator|.
name|getComponents
argument_list|()
operator|.
name|forEach
argument_list|(
name|comp
lambda|->
block|{
name|comp
operator|.
name|setState
argument_list|(
name|ComponentState
operator|.
name|STABLE
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|serviceManager
operator|.
name|handle
argument_list|(
operator|new
name|ServiceEvent
argument_list|(
name|ServiceEventType
operator|.
name|START
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"service not re-started"
argument_list|,
name|ServiceState
operator|.
name|STABLE
argument_list|,
name|serviceManager
operator|.
name|getServiceSpec
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAutoFinalizeNothingToUpgrade ()
specifier|public
name|void
name|testAutoFinalizeNothingToUpgrade
parameter_list|()
throws|throws
name|IOException
throws|,
name|SliderException
block|{
name|ServiceManager
name|serviceManager
init|=
name|createTestServiceManager
argument_list|(
literal|"testAutoFinalizeNothingToUpgrade"
argument_list|)
decl_stmt|;
name|upgrade
argument_list|(
name|serviceManager
argument_list|,
literal|"v2"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//make components stable
name|serviceManager
operator|.
name|getServiceSpec
argument_list|()
operator|.
name|getComponents
argument_list|()
operator|.
name|forEach
argument_list|(
name|comp
lambda|->
name|comp
operator|.
name|setState
argument_list|(
name|ComponentState
operator|.
name|STABLE
argument_list|)
argument_list|)
expr_stmt|;
name|serviceManager
operator|.
name|handle
argument_list|(
operator|new
name|ServiceEvent
argument_list|(
name|ServiceEventType
operator|.
name|CHECK_STABLE
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"service stable"
argument_list|,
name|ServiceState
operator|.
name|STABLE
argument_list|,
name|serviceManager
operator|.
name|getServiceSpec
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRestartWithPendingUpgrade ()
specifier|public
name|void
name|testRestartWithPendingUpgrade
parameter_list|()
throws|throws
name|IOException
throws|,
name|SliderException
block|{
name|ServiceManager
name|serviceManager
init|=
name|createTestServiceManager
argument_list|(
literal|"testRestart"
argument_list|)
decl_stmt|;
name|upgrade
argument_list|(
name|serviceManager
argument_list|,
literal|"v2"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|serviceManager
operator|.
name|handle
argument_list|(
operator|new
name|ServiceEvent
argument_list|(
name|ServiceEventType
operator|.
name|START
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"service should still be upgrading"
argument_list|,
name|ServiceState
operator|.
name|UPGRADING
argument_list|,
name|serviceManager
operator|.
name|getServiceSpec
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCheckState ()
specifier|public
name|void
name|testCheckState
parameter_list|()
throws|throws
name|IOException
throws|,
name|SliderException
block|{
name|ServiceManager
name|serviceManager
init|=
name|createTestServiceManager
argument_list|(
literal|"testCheckState"
argument_list|)
decl_stmt|;
name|upgrade
argument_list|(
name|serviceManager
argument_list|,
literal|"v2"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"service not upgrading"
argument_list|,
name|ServiceState
operator|.
name|UPGRADING
argument_list|,
name|serviceManager
operator|.
name|getServiceSpec
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
comment|// make components stable
name|serviceManager
operator|.
name|getServiceSpec
argument_list|()
operator|.
name|getComponents
argument_list|()
operator|.
name|forEach
argument_list|(
name|comp
lambda|->
block|{
name|comp
operator|.
name|setState
argument_list|(
name|ComponentState
operator|.
name|STABLE
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|ServiceEvent
name|checkStable
init|=
operator|new
name|ServiceEvent
argument_list|(
name|ServiceEventType
operator|.
name|CHECK_STABLE
argument_list|)
decl_stmt|;
name|serviceManager
operator|.
name|handle
argument_list|(
name|checkStable
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"service should still be upgrading"
argument_list|,
name|ServiceState
operator|.
name|UPGRADING
argument_list|,
name|serviceManager
operator|.
name|getServiceSpec
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
comment|// finalize service
name|ServiceEvent
name|restart
init|=
operator|new
name|ServiceEvent
argument_list|(
name|ServiceEventType
operator|.
name|START
argument_list|)
decl_stmt|;
name|serviceManager
operator|.
name|handle
argument_list|(
name|restart
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"service not stable"
argument_list|,
name|ServiceState
operator|.
name|STABLE
argument_list|,
name|serviceManager
operator|.
name|getServiceSpec
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|validateUpgradeFinalization
argument_list|(
name|serviceManager
operator|.
name|getName
argument_list|()
argument_list|,
literal|"v2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCheckStateAutoFinalize ()
specifier|public
name|void
name|testCheckStateAutoFinalize
parameter_list|()
throws|throws
name|IOException
throws|,
name|SliderException
block|{
name|ServiceManager
name|serviceManager
init|=
name|createTestServiceManager
argument_list|(
literal|"testCheckState"
argument_list|)
decl_stmt|;
name|serviceManager
operator|.
name|getServiceSpec
argument_list|()
operator|.
name|setState
argument_list|(
name|ServiceState
operator|.
name|UPGRADING_AUTO_FINALIZE
argument_list|)
expr_stmt|;
name|upgrade
argument_list|(
name|serviceManager
argument_list|,
literal|"v2"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"service not upgrading"
argument_list|,
name|ServiceState
operator|.
name|UPGRADING_AUTO_FINALIZE
argument_list|,
name|serviceManager
operator|.
name|getServiceSpec
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
comment|// make components stable
name|serviceManager
operator|.
name|getServiceSpec
argument_list|()
operator|.
name|getComponents
argument_list|()
operator|.
name|forEach
argument_list|(
name|comp
lambda|->
name|comp
operator|.
name|setState
argument_list|(
name|ComponentState
operator|.
name|STABLE
argument_list|)
argument_list|)
expr_stmt|;
name|ServiceEvent
name|checkStable
init|=
operator|new
name|ServiceEvent
argument_list|(
name|ServiceEventType
operator|.
name|CHECK_STABLE
argument_list|)
decl_stmt|;
name|serviceManager
operator|.
name|handle
argument_list|(
name|checkStable
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"service not stable"
argument_list|,
name|ServiceState
operator|.
name|STABLE
argument_list|,
name|serviceManager
operator|.
name|getServiceSpec
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|validateUpgradeFinalization
argument_list|(
name|serviceManager
operator|.
name|getName
argument_list|()
argument_list|,
literal|"v2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidUpgrade ()
specifier|public
name|void
name|testInvalidUpgrade
parameter_list|()
throws|throws
name|IOException
throws|,
name|SliderException
block|{
name|ServiceManager
name|serviceManager
init|=
name|createTestServiceManager
argument_list|(
literal|"testInvalidUpgrade"
argument_list|)
decl_stmt|;
name|serviceManager
operator|.
name|getServiceSpec
argument_list|()
operator|.
name|setState
argument_list|(
name|ServiceState
operator|.
name|UPGRADING_AUTO_FINALIZE
argument_list|)
expr_stmt|;
name|Service
name|upgradedDef
init|=
name|ServiceTestUtils
operator|.
name|createExampleApplication
argument_list|()
decl_stmt|;
name|upgradedDef
operator|.
name|setName
argument_list|(
name|serviceManager
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|upgradedDef
operator|.
name|setVersion
argument_list|(
literal|"v2"
argument_list|)
expr_stmt|;
name|upgradedDef
operator|.
name|setLifetime
argument_list|(
literal|2L
argument_list|)
expr_stmt|;
name|writeUpgradedDef
argument_list|(
name|upgradedDef
argument_list|)
expr_stmt|;
try|try
block|{
name|serviceManager
operator|.
name|processUpgradeRequest
argument_list|(
literal|"v2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ex
operator|instanceof
name|UnsupportedOperationException
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
DECL|method|validateUpgradeFinalization (String serviceName, String expectedVersion)
specifier|private
name|void
name|validateUpgradeFinalization
parameter_list|(
name|String
name|serviceName
parameter_list|,
name|String
name|expectedVersion
parameter_list|)
throws|throws
name|IOException
block|{
name|Service
name|savedSpec
init|=
name|ServiceApiUtil
operator|.
name|loadService
argument_list|(
name|rule
operator|.
name|getFs
argument_list|()
argument_list|,
name|serviceName
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"service def not re-written"
argument_list|,
name|expectedVersion
argument_list|,
name|savedSpec
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"app id not present"
argument_list|,
name|savedSpec
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"state not stable"
argument_list|,
name|ServiceState
operator|.
name|STABLE
argument_list|,
name|savedSpec
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|savedSpec
operator|.
name|getComponents
argument_list|()
operator|.
name|forEach
argument_list|(
name|compSpec
lambda|->
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"comp not stable"
argument_list|,
name|ComponentState
operator|.
name|STABLE
argument_list|,
name|compSpec
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|upgrade (ServiceManager serviceManager, String version, boolean upgradeArtifact, boolean autoFinalize)
specifier|private
name|void
name|upgrade
parameter_list|(
name|ServiceManager
name|serviceManager
parameter_list|,
name|String
name|version
parameter_list|,
name|boolean
name|upgradeArtifact
parameter_list|,
name|boolean
name|autoFinalize
parameter_list|)
throws|throws
name|IOException
throws|,
name|SliderException
block|{
name|Service
name|upgradedDef
init|=
name|ServiceTestUtils
operator|.
name|createExampleApplication
argument_list|()
decl_stmt|;
name|upgradedDef
operator|.
name|setName
argument_list|(
name|serviceManager
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|upgradedDef
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
if|if
condition|(
name|upgradeArtifact
condition|)
block|{
name|Artifact
name|upgradedArtifact
init|=
name|createTestArtifact
argument_list|(
literal|"2"
argument_list|)
decl_stmt|;
name|upgradedDef
operator|.
name|getComponents
argument_list|()
operator|.
name|forEach
argument_list|(
name|component
lambda|->
block|{
name|component
operator|.
name|setArtifact
argument_list|(
name|upgradedArtifact
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
name|writeUpgradedDef
argument_list|(
name|upgradedDef
argument_list|)
expr_stmt|;
name|serviceManager
operator|.
name|processUpgradeRequest
argument_list|(
name|version
argument_list|,
name|autoFinalize
argument_list|)
expr_stmt|;
name|ServiceEvent
name|upgradeEvent
init|=
operator|new
name|ServiceEvent
argument_list|(
name|ServiceEventType
operator|.
name|UPGRADE
argument_list|)
decl_stmt|;
name|upgradeEvent
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
if|if
condition|(
name|autoFinalize
condition|)
block|{
name|upgradeEvent
operator|.
name|setAutoFinalize
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|serviceManager
operator|.
name|handle
argument_list|(
name|upgradeEvent
argument_list|)
expr_stmt|;
block|}
DECL|method|createTestServiceManager (String name)
specifier|private
name|ServiceManager
name|createTestServiceManager
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ServiceContext
name|context
init|=
operator|new
name|ServiceContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|service
operator|=
name|createBaseDef
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|context
operator|.
name|fs
operator|=
name|rule
operator|.
name|getFs
argument_list|()
expr_stmt|;
name|context
operator|.
name|scheduler
operator|=
operator|new
name|ServiceScheduler
argument_list|(
name|context
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|YarnRegistryViewForProviders
name|createYarnRegistryOperations
parameter_list|(
name|ServiceContext
name|context
parameter_list|,
name|RegistryOperations
name|registryClient
parameter_list|)
block|{
return|return
name|mock
argument_list|(
name|YarnRegistryViewForProviders
operator|.
name|class
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|context
operator|.
name|scheduler
operator|.
name|init
argument_list|(
name|rule
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
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
name|Component
argument_list|>
name|componentState
init|=
name|context
operator|.
name|scheduler
operator|.
name|getAllComponents
argument_list|()
decl_stmt|;
name|context
operator|.
name|service
operator|.
name|getComponents
argument_list|()
operator|.
name|forEach
argument_list|(
name|component
lambda|->
block|{
name|componentState
operator|.
name|put
argument_list|(
name|component
operator|.
name|getName
argument_list|()
argument_list|,
operator|new
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
name|Component
argument_list|(
name|component
argument_list|,
literal|1L
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
return|return
operator|new
name|ServiceManager
argument_list|(
name|context
argument_list|)
return|;
block|}
DECL|method|createBaseDef (String name)
specifier|public
specifier|static
name|Service
name|createBaseDef
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|ApplicationId
name|applicationId
init|=
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
decl_stmt|;
name|Service
name|serviceDef
init|=
name|ServiceTestUtils
operator|.
name|createExampleApplication
argument_list|()
decl_stmt|;
name|serviceDef
operator|.
name|setId
argument_list|(
name|applicationId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|serviceDef
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|serviceDef
operator|.
name|setState
argument_list|(
name|ServiceState
operator|.
name|STARTED
argument_list|)
expr_stmt|;
name|Artifact
name|artifact
init|=
name|createTestArtifact
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
name|serviceDef
operator|.
name|getComponents
argument_list|()
operator|.
name|forEach
argument_list|(
name|component
lambda|->
name|component
operator|.
name|setArtifact
argument_list|(
name|artifact
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|serviceDef
return|;
block|}
DECL|method|createTestArtifact (String artifactId)
specifier|static
name|Artifact
name|createTestArtifact
parameter_list|(
name|String
name|artifactId
parameter_list|)
block|{
name|Artifact
name|artifact
init|=
operator|new
name|Artifact
argument_list|()
decl_stmt|;
name|artifact
operator|.
name|setId
argument_list|(
name|artifactId
argument_list|)
expr_stmt|;
name|artifact
operator|.
name|setType
argument_list|(
name|Artifact
operator|.
name|TypeEnum
operator|.
name|TARBALL
argument_list|)
expr_stmt|;
return|return
name|artifact
return|;
block|}
DECL|method|writeUpgradedDef (Service upgradedDef)
specifier|private
name|void
name|writeUpgradedDef
parameter_list|(
name|Service
name|upgradedDef
parameter_list|)
throws|throws
name|IOException
throws|,
name|SliderException
block|{
name|Path
name|upgradePath
init|=
name|rule
operator|.
name|getFs
argument_list|()
operator|.
name|buildClusterUpgradeDirPath
argument_list|(
name|upgradedDef
operator|.
name|getName
argument_list|()
argument_list|,
name|upgradedDef
operator|.
name|getVersion
argument_list|()
argument_list|)
decl_stmt|;
name|ServiceApiUtil
operator|.
name|createDirAndPersistApp
argument_list|(
name|rule
operator|.
name|getFs
argument_list|()
argument_list|,
name|upgradePath
argument_list|,
name|upgradedDef
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

