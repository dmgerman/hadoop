begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.uam
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
name|uam
package|;
end_package

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
name|security
operator|.
name|PrivilegedExceptionAction
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|AllocateRequest
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
name|AllocateResponse
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
name|FinishApplicationMasterRequest
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
name|FinishApplicationMasterResponse
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
name|RegisterApplicationMasterRequest
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
name|RegisterApplicationMasterResponse
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
name|security
operator|.
name|AMRMTokenIdentifier
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
name|AMHeartbeatRequestHandler
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
name|AMRMClientRelayer
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
name|MockResourceManagerFacade
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
name|util
operator|.
name|AsyncCallback
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

begin_comment
comment|/**  * Unit test for UnmanagedApplicationManager.  */
end_comment

begin_class
DECL|class|TestUnmanagedApplicationManager
specifier|public
class|class
name|TestUnmanagedApplicationManager
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
name|TestUnmanagedApplicationManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|uam
specifier|private
name|TestableUnmanagedApplicationManager
name|uam
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
DECL|field|callback
specifier|private
name|CountingCallback
name|callback
decl_stmt|;
DECL|field|attemptId
specifier|private
name|ApplicationAttemptId
name|attemptId
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CLUSTER_ID
argument_list|,
literal|"subclusterId"
argument_list|)
expr_stmt|;
name|callback
operator|=
operator|new
name|CountingCallback
argument_list|()
expr_stmt|;
name|attemptId
operator|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|uam
operator|=
operator|new
name|TestableUnmanagedApplicationManager
argument_list|(
name|conf
argument_list|,
name|attemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|"submitter"
argument_list|,
literal|"appNameSuffix"
argument_list|,
literal|true
argument_list|,
literal|"rm"
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForCallBackCountAndCheckZeroPending ( CountingCallback callBack, int expectCallBackCount)
specifier|protected
name|void
name|waitForCallBackCountAndCheckZeroPending
parameter_list|(
name|CountingCallback
name|callBack
parameter_list|,
name|int
name|expectCallBackCount
parameter_list|)
block|{
synchronized|synchronized
init|(
name|callBack
init|)
block|{
while|while
condition|(
name|callBack
operator|.
name|callBackCount
operator|!=
name|expectCallBackCount
condition|)
block|{
try|try
block|{
name|callBack
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{         }
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Non zero pending requests when number of allocate callbacks reaches "
operator|+
name|expectCallBackCount
argument_list|,
literal|0
argument_list|,
name|callBack
operator|.
name|requestQueueSize
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testBasicUsage ()
specifier|public
name|void
name|testBasicUsage
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|launchUAM
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
name|registerApplicationMaster
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
name|allocateAsync
argument_list|(
name|AllocateRequest
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|callback
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
comment|// Wait for outstanding async allocate callback
name|waitForCallBackCountAndCheckZeroPending
argument_list|(
name|callback
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|finishApplicationMaster
argument_list|(
name|FinishApplicationMasterRequest
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
while|while
condition|(
name|uam
operator|.
name|isHeartbeatThreadAlive
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"waiting for heartbeat thread to finish"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * Test re-attaching of an existing UAM. This is for HA of UAM client.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testUAMReAttach ()
specifier|public
name|void
name|testUAMReAttach
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|launchUAM
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
name|registerApplicationMaster
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
name|allocateAsync
argument_list|(
name|AllocateRequest
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|callback
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
comment|// Wait for outstanding async allocate callback
name|waitForCallBackCountAndCheckZeroPending
argument_list|(
name|callback
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|MockResourceManagerFacade
name|rmProxy
init|=
name|uam
operator|.
name|getRMProxy
argument_list|()
decl_stmt|;
name|uam
operator|=
operator|new
name|TestableUnmanagedApplicationManager
argument_list|(
name|conf
argument_list|,
name|attemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|"submitter"
argument_list|,
literal|"appNameSuffix"
argument_list|,
literal|true
argument_list|,
literal|"rm"
argument_list|)
expr_stmt|;
name|uam
operator|.
name|setRMProxy
argument_list|(
name|rmProxy
argument_list|)
expr_stmt|;
name|reAttachUAM
argument_list|(
literal|null
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
name|registerApplicationMaster
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
name|allocateAsync
argument_list|(
name|AllocateRequest
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|callback
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
comment|// Wait for outstanding async allocate callback
name|waitForCallBackCountAndCheckZeroPending
argument_list|(
name|callback
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|finishApplicationMaster
argument_list|(
name|FinishApplicationMasterRequest
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testReRegister ()
specifier|public
name|void
name|testReRegister
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|launchUAM
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
name|registerApplicationMaster
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
name|uam
operator|.
name|setShouldReRegisterNext
argument_list|()
expr_stmt|;
name|allocateAsync
argument_list|(
name|AllocateRequest
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|callback
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
comment|// Wait for outstanding async allocate callback
name|waitForCallBackCountAndCheckZeroPending
argument_list|(
name|callback
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|uam
operator|.
name|setShouldReRegisterNext
argument_list|()
expr_stmt|;
name|finishApplicationMaster
argument_list|(
name|FinishApplicationMasterRequest
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
block|}
comment|/**    * If register is slow, async allocate requests in the meanwhile should not    * throw or be dropped.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testSlowRegisterCall ()
specifier|public
name|void
name|testSlowRegisterCall
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
comment|// Register with wait() in RM in a separate thread
name|Thread
name|registerAMThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|launchUAM
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
name|registerApplicationMaster
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
literal|1001
argument_list|,
literal|null
argument_list|)
argument_list|,
name|attemptId
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
name|info
argument_list|(
literal|"Register thread exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
comment|// Sync obj from mock RM
name|Object
name|syncObj
init|=
name|MockResourceManagerFacade
operator|.
name|getRegisterSyncObj
argument_list|()
decl_stmt|;
comment|// Wait for register call in the thread get into RM and then wake us
synchronized|synchronized
init|(
name|syncObj
init|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting register thread"
argument_list|)
expr_stmt|;
name|registerAMThread
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test main starts waiting"
argument_list|)
expr_stmt|;
name|syncObj
operator|.
name|wait
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test main wait finished"
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
name|info
argument_list|(
literal|"Test main wait interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// First allocate before register succeeds
name|allocateAsync
argument_list|(
name|AllocateRequest
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|callback
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
comment|// Notify the register thread
synchronized|synchronized
init|(
name|syncObj
init|)
block|{
name|syncObj
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Test main wait for register thread to finish"
argument_list|)
expr_stmt|;
name|registerAMThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Register thread finished"
argument_list|)
expr_stmt|;
comment|// Second allocate, normal case
name|allocateAsync
argument_list|(
name|AllocateRequest
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|callback
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
comment|// Both allocate before should respond
name|waitForCallBackCountAndCheckZeroPending
argument_list|(
name|callback
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|finishApplicationMaster
argument_list|(
name|FinishApplicationMasterRequest
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
comment|// Allocates after finishAM should be ignored
name|allocateAsync
argument_list|(
name|AllocateRequest
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|callback
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
name|allocateAsync
argument_list|(
name|AllocateRequest
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|callback
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|callback
operator|.
name|requestQueueSize
argument_list|)
expr_stmt|;
comment|// A short wait just in case the allocates get executed
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{     }
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|callback
operator|.
name|callBackCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|Exception
operator|.
name|class
argument_list|)
DECL|method|testAllocateWithoutRegister ()
specifier|public
name|void
name|testAllocateWithoutRegister
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|allocateAsync
argument_list|(
name|AllocateRequest
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|callback
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|Exception
operator|.
name|class
argument_list|)
DECL|method|testFinishWithoutRegister ()
specifier|public
name|void
name|testFinishWithoutRegister
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|finishApplicationMaster
argument_list|(
name|FinishApplicationMasterRequest
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testForceKill ()
specifier|public
name|void
name|testForceKill
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|launchUAM
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
name|registerApplicationMaster
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
name|uam
operator|.
name|forceKillApplication
argument_list|()
expr_stmt|;
while|while
condition|(
name|uam
operator|.
name|isHeartbeatThreadAlive
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"waiting for heartbeat thread to finish"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|uam
operator|.
name|forceKillApplication
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Should fail because application is already killed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|t
parameter_list|)
block|{     }
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testShutDownConnections ()
specifier|public
name|void
name|testShutDownConnections
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|launchUAM
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
name|registerApplicationMaster
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|,
name|attemptId
argument_list|)
expr_stmt|;
name|uam
operator|.
name|shutDownConnections
argument_list|()
expr_stmt|;
while|while
condition|(
name|uam
operator|.
name|isHeartbeatThreadAlive
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"waiting for heartbeat thread to finish"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getUGIWithToken ( ApplicationAttemptId appAttemptId)
specifier|protected
name|UserGroupInformation
name|getUGIWithToken
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|appAttemptId
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|AMRMTokenIdentifier
name|token
init|=
operator|new
name|AMRMTokenIdentifier
argument_list|(
name|appAttemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|addTokenIdentifier
argument_list|(
name|token
argument_list|)
expr_stmt|;
return|return
name|ugi
return|;
block|}
DECL|method|launchUAM ( ApplicationAttemptId appAttemptId)
specifier|protected
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|launchUAM
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|getUGIWithToken
argument_list|(
name|appAttemptId
argument_list|)
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|uam
operator|.
name|launchUAM
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|reAttachUAM (final Token<AMRMTokenIdentifier> uamToken, ApplicationAttemptId appAttemptId)
specifier|protected
name|void
name|reAttachUAM
parameter_list|(
specifier|final
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|uamToken
parameter_list|,
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|getUGIWithToken
argument_list|(
name|appAttemptId
argument_list|)
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|uam
operator|.
name|reAttachUAM
argument_list|(
name|uamToken
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|registerApplicationMaster ( final RegisterApplicationMasterRequest request, ApplicationAttemptId appAttemptId)
specifier|protected
name|RegisterApplicationMasterResponse
name|registerApplicationMaster
parameter_list|(
specifier|final
name|RegisterApplicationMasterRequest
name|request
parameter_list|,
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|getUGIWithToken
argument_list|(
name|appAttemptId
argument_list|)
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|RegisterApplicationMasterResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RegisterApplicationMasterResponse
name|run
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|uam
operator|.
name|registerApplicationMaster
argument_list|(
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|allocateAsync (final AllocateRequest request, final AsyncCallback<AllocateResponse> callBack, ApplicationAttemptId appAttemptId)
specifier|protected
name|void
name|allocateAsync
parameter_list|(
specifier|final
name|AllocateRequest
name|request
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|AllocateResponse
argument_list|>
name|callBack
parameter_list|,
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|getUGIWithToken
argument_list|(
name|appAttemptId
argument_list|)
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|YarnException
block|{
name|uam
operator|.
name|allocateAsync
argument_list|(
name|request
argument_list|,
name|callBack
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|finishApplicationMaster ( final FinishApplicationMasterRequest request, ApplicationAttemptId appAttemptId)
specifier|protected
name|FinishApplicationMasterResponse
name|finishApplicationMaster
parameter_list|(
specifier|final
name|FinishApplicationMasterRequest
name|request
parameter_list|,
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|getUGIWithToken
argument_list|(
name|appAttemptId
argument_list|)
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|FinishApplicationMasterResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FinishApplicationMasterResponse
name|run
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|FinishApplicationMasterResponse
name|response
init|=
name|uam
operator|.
name|finishApplicationMaster
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|class|CountingCallback
specifier|protected
class|class
name|CountingCallback
implements|implements
name|AsyncCallback
argument_list|<
name|AllocateResponse
argument_list|>
block|{
DECL|field|callBackCount
specifier|private
name|int
name|callBackCount
decl_stmt|;
DECL|field|requestQueueSize
specifier|private
name|int
name|requestQueueSize
decl_stmt|;
annotation|@
name|Override
DECL|method|callback (AllocateResponse response)
specifier|public
name|void
name|callback
parameter_list|(
name|AllocateResponse
name|response
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|callBackCount
operator|++
expr_stmt|;
name|requestQueueSize
operator|=
name|uam
operator|.
name|getRequestQueueSize
argument_list|()
expr_stmt|;
name|this
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Testable UnmanagedApplicationManager that talks to a mock RM.    */
DECL|class|TestableUnmanagedApplicationManager
specifier|public
class|class
name|TestableUnmanagedApplicationManager
extends|extends
name|UnmanagedApplicationManager
block|{
DECL|field|rmProxy
specifier|private
name|MockResourceManagerFacade
name|rmProxy
decl_stmt|;
DECL|method|TestableUnmanagedApplicationManager (Configuration conf, ApplicationId appId, String queueName, String submitter, String appNameSuffix, boolean keepContainersAcrossApplicationAttempts, String rmName)
specifier|public
name|TestableUnmanagedApplicationManager
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|String
name|queueName
parameter_list|,
name|String
name|submitter
parameter_list|,
name|String
name|appNameSuffix
parameter_list|,
name|boolean
name|keepContainersAcrossApplicationAttempts
parameter_list|,
name|String
name|rmName
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|appId
argument_list|,
name|queueName
argument_list|,
name|submitter
argument_list|,
name|appNameSuffix
argument_list|,
name|keepContainersAcrossApplicationAttempts
argument_list|,
name|rmName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createAMHeartbeatRequestHandler ( Configuration config, ApplicationId appId, AMRMClientRelayer rmProxyRelayer)
specifier|protected
name|AMHeartbeatRequestHandler
name|createAMHeartbeatRequestHandler
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|AMRMClientRelayer
name|rmProxyRelayer
parameter_list|)
block|{
return|return
operator|new
name|TestableAMRequestHandlerThread
argument_list|(
name|config
argument_list|,
name|appId
argument_list|,
name|rmProxyRelayer
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|createRMProxy (final Class<T> protocol, Configuration config, UserGroupInformation user, Token<AMRMTokenIdentifier> token)
specifier|protected
parameter_list|<
name|T
parameter_list|>
name|T
name|createRMProxy
parameter_list|(
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|protocol
parameter_list|,
name|Configuration
name|config
parameter_list|,
name|UserGroupInformation
name|user
parameter_list|,
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|token
parameter_list|)
block|{
if|if
condition|(
name|rmProxy
operator|==
literal|null
condition|)
block|{
name|rmProxy
operator|=
operator|new
name|MockResourceManagerFacade
argument_list|(
name|config
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|T
operator|)
name|rmProxy
return|;
block|}
DECL|method|setShouldReRegisterNext ()
specifier|public
name|void
name|setShouldReRegisterNext
parameter_list|()
block|{
if|if
condition|(
name|rmProxy
operator|!=
literal|null
condition|)
block|{
name|rmProxy
operator|.
name|setShouldReRegisterNext
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getRMProxy ()
specifier|public
name|MockResourceManagerFacade
name|getRMProxy
parameter_list|()
block|{
return|return
name|rmProxy
return|;
block|}
DECL|method|setRMProxy (MockResourceManagerFacade proxy)
specifier|public
name|void
name|setRMProxy
parameter_list|(
name|MockResourceManagerFacade
name|proxy
parameter_list|)
block|{
name|this
operator|.
name|rmProxy
operator|=
name|proxy
expr_stmt|;
block|}
block|}
comment|/**    * Wrap the handler thread so it calls from the same user.    */
DECL|class|TestableAMRequestHandlerThread
specifier|public
class|class
name|TestableAMRequestHandlerThread
extends|extends
name|AMHeartbeatRequestHandler
block|{
DECL|method|TestableAMRequestHandlerThread (Configuration conf, ApplicationId applicationId, AMRMClientRelayer rmProxyRelayer)
specifier|public
name|TestableAMRequestHandlerThread
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|,
name|AMRMClientRelayer
name|rmProxyRelayer
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|applicationId
argument_list|,
name|rmProxyRelayer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|getUGIWithToken
argument_list|(
name|attemptId
argument_list|)
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
block|{
name|TestableAMRequestHandlerThread
operator|.
name|super
operator|.
name|run
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
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
literal|"Exception running TestableAMRequestHandlerThread"
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

