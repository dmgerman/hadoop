begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
name|resourcemanager
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
name|ha
operator|.
name|HAServiceProtocol
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|StateChangeRequestInfo
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
name|ha
operator|.
name|HealthCheckFailedException
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
name|conf
operator|.
name|HAUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
DECL|class|TestRMHA
specifier|public
class|class
name|TestRMHA
block|{
DECL|field|LOG
specifier|private
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestRMHA
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rm
specifier|private
name|MockRM
name|rm
init|=
literal|null
decl_stmt|;
DECL|field|STATE_ERR
specifier|private
specifier|static
specifier|final
name|String
name|STATE_ERR
init|=
literal|"ResourceManager is in wrong HA state"
decl_stmt|;
DECL|field|RM1_ADDRESS
specifier|private
specifier|static
specifier|final
name|String
name|RM1_ADDRESS
init|=
literal|"0.0.0.0:0"
decl_stmt|;
DECL|field|RM1_NODE_ID
specifier|private
specifier|static
specifier|final
name|String
name|RM1_NODE_ID
init|=
literal|"rm1"
decl_stmt|;
DECL|field|RM2_ADDRESS
specifier|private
specifier|static
specifier|final
name|String
name|RM2_ADDRESS
init|=
literal|"1.1.1.1:1"
decl_stmt|;
DECL|field|RM2_NODE_ID
specifier|private
specifier|static
specifier|final
name|String
name|RM2_NODE_ID
init|=
literal|"rm2"
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_IDS
argument_list|,
name|RM1_NODE_ID
operator|+
literal|","
operator|+
name|RM2_NODE_ID
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|confKey
range|:
name|YarnConfiguration
operator|.
name|RM_SERVICES_ADDRESS_CONF_KEYS
control|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|HAUtil
operator|.
name|addSuffix
argument_list|(
name|confKey
argument_list|,
name|RM1_NODE_ID
argument_list|)
argument_list|,
name|RM1_ADDRESS
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HAUtil
operator|.
name|addSuffix
argument_list|(
name|confKey
argument_list|,
name|RM2_NODE_ID
argument_list|)
argument_list|,
name|RM2_ADDRESS
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ID
argument_list|,
name|RM1_NODE_ID
argument_list|)
expr_stmt|;
name|rm
operator|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|rm
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|checkMonitorHealth ()
specifier|private
name|void
name|checkMonitorHealth
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|rm
operator|.
name|adminService
operator|.
name|monitorHealth
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HealthCheckFailedException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"The RM is in bad health: it is Active, but the active services "
operator|+
literal|"are not running"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkStandbyRMFunctionality ()
specifier|private
name|void
name|checkStandbyRMFunctionality
parameter_list|()
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|STATE_ERR
argument_list|,
name|HAServiceState
operator|.
name|STANDBY
argument_list|,
name|rm
operator|.
name|adminService
operator|.
name|getServiceStatus
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Active RM services are started"
argument_list|,
name|rm
operator|.
name|areActiveServicesRunning
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"RM is not ready to become active"
argument_list|,
name|rm
operator|.
name|adminService
operator|.
name|getServiceStatus
argument_list|()
operator|.
name|isReadyToBecomeActive
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkActiveRMFunctionality ()
specifier|private
name|void
name|checkActiveRMFunctionality
parameter_list|()
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|STATE_ERR
argument_list|,
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
name|rm
operator|.
name|adminService
operator|.
name|getServiceStatus
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Active RM services aren't started"
argument_list|,
name|rm
operator|.
name|areActiveServicesRunning
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"RM is not ready to become active"
argument_list|,
name|rm
operator|.
name|adminService
operator|.
name|getServiceStatus
argument_list|()
operator|.
name|isReadyToBecomeActive
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|rm
operator|.
name|getNewAppId
argument_list|()
expr_stmt|;
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:0"
argument_list|,
literal|2048
argument_list|)
expr_stmt|;
name|rm
operator|.
name|submitApp
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Unable to perform Active RM functions"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"ActiveRM check failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test to verify the following RM HA transitions to the following states.    * 1. Standby: Should be a no-op    * 2. Active: Active services should start    * 3. Active: Should be a no-op.    *    While active, submit a couple of jobs    * 4. Standby: Active services should stop    * 5. Active: Active services should start    * 6. Stop the RM: All services should stop and RM should not be ready to    * become Active    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testStartAndTransitions ()
specifier|public
name|void
name|testStartAndTransitions
parameter_list|()
throws|throws
name|IOException
block|{
name|StateChangeRequestInfo
name|requestInfo
init|=
operator|new
name|StateChangeRequestInfo
argument_list|(
name|HAServiceProtocol
operator|.
name|RequestSource
operator|.
name|REQUEST_BY_USER
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|STATE_ERR
argument_list|,
name|HAServiceState
operator|.
name|INITIALIZING
argument_list|,
name|rm
operator|.
name|adminService
operator|.
name|getServiceStatus
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"RM is ready to become active before being started"
argument_list|,
name|rm
operator|.
name|adminService
operator|.
name|getServiceStatus
argument_list|()
operator|.
name|isReadyToBecomeActive
argument_list|()
argument_list|)
expr_stmt|;
name|checkMonitorHealth
argument_list|()
expr_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|checkMonitorHealth
argument_list|()
expr_stmt|;
name|checkStandbyRMFunctionality
argument_list|()
expr_stmt|;
comment|// 1. Transition to Standby - must be a no-op
name|rm
operator|.
name|adminService
operator|.
name|transitionToStandby
argument_list|(
name|requestInfo
argument_list|)
expr_stmt|;
name|checkMonitorHealth
argument_list|()
expr_stmt|;
name|checkStandbyRMFunctionality
argument_list|()
expr_stmt|;
comment|// 2. Transition to active
name|rm
operator|.
name|adminService
operator|.
name|transitionToActive
argument_list|(
name|requestInfo
argument_list|)
expr_stmt|;
name|checkMonitorHealth
argument_list|()
expr_stmt|;
name|checkActiveRMFunctionality
argument_list|()
expr_stmt|;
comment|// 3. Transition to active - no-op
name|rm
operator|.
name|adminService
operator|.
name|transitionToActive
argument_list|(
name|requestInfo
argument_list|)
expr_stmt|;
name|checkMonitorHealth
argument_list|()
expr_stmt|;
name|checkActiveRMFunctionality
argument_list|()
expr_stmt|;
comment|// 4. Transition to standby
name|rm
operator|.
name|adminService
operator|.
name|transitionToStandby
argument_list|(
name|requestInfo
argument_list|)
expr_stmt|;
name|checkMonitorHealth
argument_list|()
expr_stmt|;
name|checkStandbyRMFunctionality
argument_list|()
expr_stmt|;
comment|// 5. Transition to active to check Active->Standby->Active works
name|rm
operator|.
name|adminService
operator|.
name|transitionToActive
argument_list|(
name|requestInfo
argument_list|)
expr_stmt|;
name|checkMonitorHealth
argument_list|()
expr_stmt|;
name|checkActiveRMFunctionality
argument_list|()
expr_stmt|;
comment|// 6. Stop the RM. All services should stop and RM should not be ready to
comment|// become active
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE_ERR
argument_list|,
name|HAServiceState
operator|.
name|STOPPING
argument_list|,
name|rm
operator|.
name|adminService
operator|.
name|getServiceStatus
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"RM is ready to become active even after it is stopped"
argument_list|,
name|rm
operator|.
name|adminService
operator|.
name|getServiceStatus
argument_list|()
operator|.
name|isReadyToBecomeActive
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Active RM services are started"
argument_list|,
name|rm
operator|.
name|areActiveServicesRunning
argument_list|()
argument_list|)
expr_stmt|;
name|checkMonitorHealth
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

