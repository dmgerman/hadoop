begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|junit
operator|.
name|Assert
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
name|security
operator|.
name|UserGroupInformation
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
name|server
operator|.
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMApp
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

begin_class
DECL|class|TestSubmitApplicationWithRMHA
specifier|public
class|class
name|TestSubmitApplicationWithRMHA
extends|extends
name|RMHATestBase
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestSubmitApplicationWithRMHA
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
DECL|method|testHandleRMHABeforeSubmitApplicationCallWithSavedApplicationState ()
name|testHandleRMHABeforeSubmitApplicationCallWithSavedApplicationState
parameter_list|()
throws|throws
name|Exception
block|{
comment|// start two RMs, and transit rm1 to active, rm2 to standby
name|startRMs
argument_list|()
expr_stmt|;
comment|// get a new applicationId from rm1
name|ApplicationId
name|appId
init|=
name|rm1
operator|.
name|getNewAppId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
comment|// Do the failover
name|explicitFailover
argument_list|()
expr_stmt|;
comment|// submit the application with previous assigned applicationId
comment|// to current active rm: rm2
name|RMApp
name|app1
init|=
name|rm2
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|,
literal|""
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|configuration
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AM_MAX_ATTEMPTS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_AM_MAX_ATTEMPTS
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|appId
argument_list|)
decl_stmt|;
comment|// verify application submission
name|verifySubmitApp
argument_list|(
name|rm2
argument_list|,
name|app1
argument_list|,
name|appId
argument_list|)
expr_stmt|;
block|}
DECL|method|verifySubmitApp (MockRM rm, RMApp app, ApplicationId expectedAppId)
specifier|private
name|void
name|verifySubmitApp
parameter_list|(
name|MockRM
name|rm
parameter_list|,
name|RMApp
name|app
parameter_list|,
name|ApplicationId
name|expectedAppId
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|maxWaittingTimes
init|=
literal|20
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|YarnApplicationState
name|state
init|=
name|rm
operator|.
name|getApplicationReport
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|getYarnApplicationState
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|state
operator|.
name|equals
argument_list|(
name|YarnApplicationState
operator|.
name|NEW
argument_list|)
operator|&&
operator|!
name|state
operator|.
name|equals
argument_list|(
name|YarnApplicationState
operator|.
name|NEW_SAVING
argument_list|)
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|count
operator|>
name|maxWaittingTimes
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
comment|// Verify submittion is successful
name|YarnApplicationState
name|state
init|=
name|rm
operator|.
name|getApplicationReport
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|getYarnApplicationState
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|state
operator|==
name|YarnApplicationState
operator|.
name|ACCEPTED
operator|||
name|state
operator|==
name|YarnApplicationState
operator|.
name|SUBMITTED
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedAppId
argument_list|,
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// There are two scenarios when RM failover happens
comment|// after SubmitApplication Call:
comment|// 1) RMStateStore already saved the ApplicationState when failover happens
comment|// 2) RMStateStore did not save the ApplicationState when failover happens
annotation|@
name|Test
specifier|public
name|void
DECL|method|testHandleRMHAafterSubmitApplicationCallWithSavedApplicationState ()
name|testHandleRMHAafterSubmitApplicationCallWithSavedApplicationState
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test scenario 1 when RM failover happens
comment|// after SubmitApplication Call:
comment|// RMStateStore already saved the ApplicationState when failover happens
name|startRMs
argument_list|()
expr_stmt|;
comment|// Submit Application
comment|// After submission, the applicationState will be saved in RMStateStore.
name|RMApp
name|app0
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|)
decl_stmt|;
comment|// Do the failover
name|explicitFailover
argument_list|()
expr_stmt|;
comment|// Since the applicationState has already been saved in RMStateStore
comment|// before failover happens, the current active rm can load the previous
comment|// applicationState.
name|ApplicationReport
name|appReport
init|=
name|rm2
operator|.
name|getApplicationReport
argument_list|(
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
comment|// verify previous submission is successful.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|appReport
operator|.
name|getYarnApplicationState
argument_list|()
operator|==
name|YarnApplicationState
operator|.
name|ACCEPTED
operator|||
name|appReport
operator|.
name|getYarnApplicationState
argument_list|()
operator|==
name|YarnApplicationState
operator|.
name|SUBMITTED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
DECL|method|testHandleRMHAafterSubmitApplicationCallWithoutSavedApplicationState ()
name|testHandleRMHAafterSubmitApplicationCallWithoutSavedApplicationState
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test scenario 2 when RM failover happens
comment|// after SubmitApplication Call:
comment|// RMStateStore did not save the ApplicationState when failover happens.
comment|// Using customized RMAppManager.
name|startRMsWithCustomizedRMAppManager
argument_list|()
expr_stmt|;
comment|// Submit Application
comment|// After submission, the applicationState will
comment|// not be saved in RMStateStore
name|RMApp
name|app0
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|,
literal|""
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|configuration
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AM_MAX_ATTEMPTS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_AM_MAX_ATTEMPTS
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Do the failover
name|explicitFailover
argument_list|()
expr_stmt|;
comment|// Since the applicationState is not saved in RMStateStore
comment|// when failover happens. The current active RM can not load
comment|// previous applicationState.
comment|// Expect ApplicationNotFoundException by calling getApplicationReport().
try|try
block|{
name|rm2
operator|.
name|getApplicationReport
argument_list|(
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Should get ApplicationNotFoundException here"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ApplicationNotFoundException
name|ex
parameter_list|)
block|{
comment|// expected ApplicationNotFoundException
block|}
comment|// Submit the application with previous ApplicationId to current active RM
comment|// This will mimic the similar behavior of YarnClient which will re-submit
comment|// Application with previous applicationId
comment|// when catches the ApplicationNotFoundException
name|RMApp
name|app1
init|=
name|rm2
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|,
literal|""
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|configuration
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AM_MAX_ATTEMPTS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_AM_MAX_ATTEMPTS
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|verifySubmitApp
argument_list|(
name|rm2
argument_list|,
name|app1
argument_list|,
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test multiple calls of getApplicationReport, to make sure    * it is idempotent    */
annotation|@
name|Test
DECL|method|testGetApplicationReportIdempotent ()
specifier|public
name|void
name|testGetApplicationReportIdempotent
parameter_list|()
throws|throws
name|Exception
block|{
comment|// start two RMs, and transit rm1 to active, rm2 to standby
name|startRMs
argument_list|()
expr_stmt|;
comment|// Submit Application
comment|// After submission, the applicationState will be saved in RMStateStore.
name|RMApp
name|app
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|ApplicationReport
name|appReport1
init|=
name|rm1
operator|.
name|getApplicationReport
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|appReport1
operator|.
name|getYarnApplicationState
argument_list|()
operator|==
name|YarnApplicationState
operator|.
name|ACCEPTED
operator|||
name|appReport1
operator|.
name|getYarnApplicationState
argument_list|()
operator|==
name|YarnApplicationState
operator|.
name|SUBMITTED
argument_list|)
expr_stmt|;
comment|// call getApplicationReport again
name|ApplicationReport
name|appReport2
init|=
name|rm1
operator|.
name|getApplicationReport
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appReport1
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|appReport2
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appReport1
operator|.
name|getYarnApplicationState
argument_list|()
argument_list|,
name|appReport2
operator|.
name|getYarnApplicationState
argument_list|()
argument_list|)
expr_stmt|;
comment|// Do the failover
name|explicitFailover
argument_list|()
expr_stmt|;
comment|// call getApplicationReport
name|ApplicationReport
name|appReport3
init|=
name|rm2
operator|.
name|getApplicationReport
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appReport1
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|appReport3
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appReport1
operator|.
name|getYarnApplicationState
argument_list|()
argument_list|,
name|appReport3
operator|.
name|getYarnApplicationState
argument_list|()
argument_list|)
expr_stmt|;
comment|// call getApplicationReport again
name|ApplicationReport
name|appReport4
init|=
name|rm2
operator|.
name|getApplicationReport
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appReport3
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|appReport4
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appReport3
operator|.
name|getYarnApplicationState
argument_list|()
argument_list|,
name|appReport4
operator|.
name|getYarnApplicationState
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// There are two scenarios when RM failover happens
comment|// during SubmitApplication Call:
comment|// 1) RMStateStore already saved the ApplicationState when failover happens
comment|// 2) RMStateStore did not save the ApplicationState when failover happens
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|50000
argument_list|)
specifier|public
name|void
DECL|method|testHandleRMHADuringSubmitApplicationCallWithSavedApplicationState ()
name|testHandleRMHADuringSubmitApplicationCallWithSavedApplicationState
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test scenario 1 when RM failover happens
comment|// druing SubmitApplication Call:
comment|// RMStateStore already saved the ApplicationState when failover happens
name|startRMs
argument_list|()
expr_stmt|;
comment|// Submit Application
comment|// After submission, the applicationState will be saved in RMStateStore.
name|RMApp
name|app0
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|)
decl_stmt|;
comment|// Do the failover
name|explicitFailover
argument_list|()
expr_stmt|;
comment|// Since the applicationState has already been saved in RMStateStore
comment|// before failover happens, the current active rm can load the previous
comment|// applicationState.
comment|// This RMApp should exist in the RMContext of current active RM
name|Assert
operator|.
name|assertTrue
argument_list|(
name|rm2
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|containsKey
argument_list|(
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// When we re-submit the application with same applicationId, it will
comment|// check whether this application has been exist. If yes, just simply
comment|// return submitApplicationResponse.
name|RMApp
name|app1
init|=
name|rm2
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|,
literal|""
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|configuration
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AM_MAX_ATTEMPTS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_AM_MAX_ATTEMPTS
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|app1
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|50000
argument_list|)
specifier|public
name|void
DECL|method|testHandleRMHADuringSubmitApplicationCallWithoutSavedApplicationState ()
name|testHandleRMHADuringSubmitApplicationCallWithoutSavedApplicationState
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test scenario 2 when RM failover happens
comment|// during SubmitApplication Call:
comment|// RMStateStore did not save the ApplicationState when failover happens.
comment|// Using customized RMAppManager.
name|startRMsWithCustomizedRMAppManager
argument_list|()
expr_stmt|;
comment|// Submit Application
comment|// After submission, the applicationState will
comment|// not be saved in RMStateStore
name|RMApp
name|app0
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|,
literal|""
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|configuration
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AM_MAX_ATTEMPTS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_AM_MAX_ATTEMPTS
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Do the failover
name|explicitFailover
argument_list|()
expr_stmt|;
comment|// When failover happens, the RMStateStore has not saved applicationState.
comment|// The applicationState of this RMApp is lost.
comment|// We should not find the RMApp in the RMContext of current active rm.
name|Assert
operator|.
name|assertFalse
argument_list|(
name|rm2
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|containsKey
argument_list|(
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Submit the application with previous ApplicationId to current active RM
comment|// This will mimic the similar behavior of ApplicationClientProtocol#
comment|// submitApplication() when failover happens during the submission process
comment|// because the submitApplication api is marked as idempotent
name|RMApp
name|app1
init|=
name|rm2
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|,
literal|""
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|configuration
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AM_MAX_ATTEMPTS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_AM_MAX_ATTEMPTS
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|verifySubmitApp
argument_list|(
name|rm2
argument_list|,
name|app1
argument_list|,
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|rm2
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|containsKey
argument_list|(
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

