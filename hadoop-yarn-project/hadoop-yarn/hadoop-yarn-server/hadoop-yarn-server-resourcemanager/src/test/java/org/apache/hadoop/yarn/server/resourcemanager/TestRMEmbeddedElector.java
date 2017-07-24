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
name|ClientBaseWithFixes
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
name|ServiceFailedException
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
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
name|atLeast
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
name|atMost
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
name|never
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
name|verify
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

begin_class
DECL|class|TestRMEmbeddedElector
specifier|public
class|class
name|TestRMEmbeddedElector
extends|extends
name|ClientBaseWithFixes
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
name|TestRMEmbeddedElector
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
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
DECL|field|RM1_PORT_BASE
specifier|private
specifier|static
specifier|final
name|int
name|RM1_PORT_BASE
init|=
literal|10000
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
DECL|field|RM2_PORT_BASE
specifier|private
specifier|static
specifier|final
name|int
name|RM2_PORT_BASE
init|=
literal|20000
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|callbackCalled
specifier|private
name|AtomicBoolean
name|callbackCalled
decl_stmt|;
DECL|enum|SyncTestType
specifier|private
enum|enum
name|SyncTestType
block|{
DECL|enumConstant|ACTIVE
name|ACTIVE
block|,
DECL|enumConstant|STANDBY
name|STANDBY
block|,
DECL|enumConstant|NEUTRAL
name|NEUTRAL
block|,
DECL|enumConstant|ACTIVE_TIMING
name|ACTIVE_TIMING
block|,
DECL|enumConstant|STANDBY_TIMING
name|STANDBY_TIMING
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
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
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|AUTO_FAILOVER_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|AUTO_FAILOVER_EMBEDDED
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
name|RM_CLUSTER_ID
argument_list|,
literal|"yarn-test-cluster"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_ADDRESS
argument_list|,
name|hostPort
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_TIMEOUT_MS
argument_list|,
literal|2000
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
name|HATestUtil
operator|.
name|setRpcAddressForRM
argument_list|(
name|RM1_NODE_ID
argument_list|,
name|RM1_PORT_BASE
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|HATestUtil
operator|.
name|setRpcAddressForRM
argument_list|(
name|RM2_NODE_ID
argument_list|,
name|RM2_PORT_BASE
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_FAILOVER_SLEEPTIME_BASE_MS
argument_list|,
literal|100L
argument_list|)
expr_stmt|;
name|callbackCalled
operator|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that tries to see if there is a deadlock between    * (a) the thread stopping the RM    * (b) thread processing the ZK event asking RM to transition to active    *    * The test times out if there is a deadlock.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testDeadlockShutdownBecomeActive ()
specifier|public
name|void
name|testDeadlockShutdownBecomeActive
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|MockRM
name|rm
init|=
operator|new
name|MockRMWithElector
argument_list|(
name|conf
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for callback"
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|callbackCalled
operator|.
name|get
argument_list|()
condition|)
empty_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping RM"
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopped RM"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that neutral mode plays well with all other transitions.    *    * @throws IOException if there's an issue transitioning    * @throws InterruptedException if interrupted    */
annotation|@
name|Test
DECL|method|testCallbackSynchronization ()
specifier|public
name|void
name|testCallbackSynchronization
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|testCallbackSynchronization
argument_list|(
name|SyncTestType
operator|.
name|ACTIVE
argument_list|)
expr_stmt|;
name|testCallbackSynchronization
argument_list|(
name|SyncTestType
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
name|testCallbackSynchronization
argument_list|(
name|SyncTestType
operator|.
name|NEUTRAL
argument_list|)
expr_stmt|;
name|testCallbackSynchronization
argument_list|(
name|SyncTestType
operator|.
name|ACTIVE_TIMING
argument_list|)
expr_stmt|;
name|testCallbackSynchronization
argument_list|(
name|SyncTestType
operator|.
name|STANDBY_TIMING
argument_list|)
expr_stmt|;
block|}
comment|/**    * Helper method to test that neutral mode plays well with other transitions.    *    * @param type the type of test to run    * @throws IOException if there's an issue transitioning    * @throws InterruptedException if interrupted    */
DECL|method|testCallbackSynchronization (SyncTestType type)
specifier|private
name|void
name|testCallbackSynchronization
parameter_list|(
name|SyncTestType
name|type
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|AdminService
name|as
init|=
name|mock
argument_list|(
name|AdminService
operator|.
name|class
argument_list|)
decl_stmt|;
name|RMContext
name|rc
init|=
name|mock
argument_list|(
name|RMContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ResourceManager
name|rm
init|=
name|mock
argument_list|(
name|ResourceManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|Configuration
name|myConf
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|myConf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_TIMEOUT_MS
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rm
operator|.
name|getRMContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rc
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rc
operator|.
name|getRMAdminService
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|as
argument_list|)
expr_stmt|;
name|ActiveStandbyElectorBasedElectorService
name|ees
init|=
operator|new
name|ActiveStandbyElectorBasedElectorService
argument_list|(
name|rm
argument_list|)
decl_stmt|;
name|ees
operator|.
name|init
argument_list|(
name|myConf
argument_list|)
expr_stmt|;
name|ees
operator|.
name|enterNeutralMode
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|ACTIVE
case|:
name|testCallbackSynchronizationActive
argument_list|(
name|as
argument_list|,
name|ees
argument_list|)
expr_stmt|;
break|break;
case|case
name|STANDBY
case|:
name|testCallbackSynchronizationStandby
argument_list|(
name|as
argument_list|,
name|ees
argument_list|)
expr_stmt|;
break|break;
case|case
name|NEUTRAL
case|:
name|testCallbackSynchronizationNeutral
argument_list|(
name|as
argument_list|,
name|ees
argument_list|)
expr_stmt|;
break|break;
case|case
name|ACTIVE_TIMING
case|:
name|testCallbackSynchronizationTimingActive
argument_list|(
name|as
argument_list|,
name|ees
argument_list|)
expr_stmt|;
break|break;
case|case
name|STANDBY_TIMING
case|:
name|testCallbackSynchronizationTimingStandby
argument_list|(
name|as
argument_list|,
name|ees
argument_list|)
expr_stmt|;
break|break;
default|default:
name|fail
argument_list|(
literal|"Unknown test type: "
operator|+
name|type
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|/**    * Helper method to test that neutral mode plays well with an active    * transition.    *    * @param as the admin service    * @param ees the embedded elector service    * @throws IOException if there's an issue transitioning    * @throws InterruptedException if interrupted    */
DECL|method|testCallbackSynchronizationActive (AdminService as, ActiveStandbyElectorBasedElectorService ees)
specifier|private
name|void
name|testCallbackSynchronizationActive
parameter_list|(
name|AdminService
name|as
parameter_list|,
name|ActiveStandbyElectorBasedElectorService
name|ees
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|ees
operator|.
name|becomeActive
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|as
argument_list|)
operator|.
name|transitionToActive
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|as
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|transitionToStandby
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Helper method to test that neutral mode plays well with a standby    * transition.    *    * @param as the admin service    * @param ees the embedded elector service    * @throws IOException if there's an issue transitioning    * @throws InterruptedException if interrupted    */
DECL|method|testCallbackSynchronizationStandby (AdminService as, ActiveStandbyElectorBasedElectorService ees)
specifier|private
name|void
name|testCallbackSynchronizationStandby
parameter_list|(
name|AdminService
name|as
parameter_list|,
name|ActiveStandbyElectorBasedElectorService
name|ees
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|ees
operator|.
name|becomeStandby
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|as
argument_list|,
name|atLeast
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|transitionToStandby
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|as
argument_list|,
name|atMost
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|transitionToStandby
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Helper method to test that neutral mode plays well with itself.    *    * @param as the admin service    * @param ees the embedded elector service    * @throws IOException if there's an issue transitioning    * @throws InterruptedException if interrupted    */
DECL|method|testCallbackSynchronizationNeutral (AdminService as, ActiveStandbyElectorBasedElectorService ees)
specifier|private
name|void
name|testCallbackSynchronizationNeutral
parameter_list|(
name|AdminService
name|as
parameter_list|,
name|ActiveStandbyElectorBasedElectorService
name|ees
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|ees
operator|.
name|enterNeutralMode
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|as
argument_list|,
name|atLeast
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|transitionToStandby
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|as
argument_list|,
name|atMost
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|transitionToStandby
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Helper method to test that neutral mode does not race with an active    * transition.    *    * @param as the admin service    * @param ees the embedded elector service    * @throws IOException if there's an issue transitioning    * @throws InterruptedException if interrupted    */
DECL|method|testCallbackSynchronizationTimingActive (AdminService as, ActiveStandbyElectorBasedElectorService ees)
specifier|private
name|void
name|testCallbackSynchronizationTimingActive
parameter_list|(
name|AdminService
name|as
parameter_list|,
name|ActiveStandbyElectorBasedElectorService
name|ees
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
synchronized|synchronized
init|(
name|ees
operator|.
name|zkDisconnectLock
init|)
block|{
comment|// Sleep while holding the lock so that the timer thread can't do
comment|// anything when it runs.  Sleep until we're pretty sure the timer thread
comment|// has tried to run.
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// While still holding the lock cancel the timer by transitioning. This
comment|// simulates a race where the callback goes to cancel the timer while the
comment|// timer is trying to run.
name|ees
operator|.
name|becomeActive
argument_list|()
expr_stmt|;
block|}
comment|// Sleep just a little more so that the timer thread can do whatever it's
comment|// going to do, hopefully nothing.
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|as
argument_list|)
operator|.
name|transitionToActive
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|as
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|transitionToStandby
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Helper method to test that neutral mode does not race with an active    * transition.    *    * @param as the admin service    * @param ees the embedded elector service    * @throws IOException if there's an issue transitioning    * @throws InterruptedException if interrupted    */
DECL|method|testCallbackSynchronizationTimingStandby (AdminService as, ActiveStandbyElectorBasedElectorService ees)
specifier|private
name|void
name|testCallbackSynchronizationTimingStandby
parameter_list|(
name|AdminService
name|as
parameter_list|,
name|ActiveStandbyElectorBasedElectorService
name|ees
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
synchronized|synchronized
init|(
name|ees
operator|.
name|zkDisconnectLock
init|)
block|{
comment|// Sleep while holding the lock so that the timer thread can't do
comment|// anything when it runs.  Sleep until we're pretty sure the timer thread
comment|// has tried to run.
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// While still holding the lock cancel the timer by transitioning. This
comment|// simulates a race where the callback goes to cancel the timer while the
comment|// timer is trying to run.
name|ees
operator|.
name|becomeStandby
argument_list|()
expr_stmt|;
block|}
comment|// Sleep just a little more so that the timer thread can do whatever it's
comment|// going to do, hopefully nothing.
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|as
argument_list|,
name|atLeast
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|transitionToStandby
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|as
argument_list|,
name|atMost
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|transitionToStandby
argument_list|(
name|any
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|MockRMWithElector
specifier|private
class|class
name|MockRMWithElector
extends|extends
name|MockRM
block|{
DECL|field|delayMs
specifier|private
name|long
name|delayMs
init|=
literal|0
decl_stmt|;
DECL|method|MockRMWithElector (Configuration conf)
name|MockRMWithElector
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|MockRMWithElector (Configuration conf, long delayMs)
name|MockRMWithElector
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|long
name|delayMs
parameter_list|)
block|{
name|this
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|delayMs
operator|=
name|delayMs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createEmbeddedElector ()
specifier|protected
name|EmbeddedElector
name|createEmbeddedElector
parameter_list|()
block|{
return|return
operator|new
name|ActiveStandbyElectorBasedElectorService
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|becomeActive
parameter_list|()
throws|throws
name|ServiceFailedException
block|{
try|try
block|{
name|callbackCalled
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TestRMEmbeddedElector
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Callback called. Sleeping now"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|delayMs
argument_list|)
expr_stmt|;
name|TestRMEmbeddedElector
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Sleep done"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|becomeActive
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
block|}
end_class

end_unit

