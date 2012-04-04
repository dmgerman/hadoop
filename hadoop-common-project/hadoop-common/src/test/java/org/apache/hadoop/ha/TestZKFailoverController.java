begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ha
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
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
name|impl
operator|.
name|Log4JLogger
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
name|HealthMonitor
operator|.
name|State
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
name|MiniZKFCCluster
operator|.
name|DummyZKFC
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|ZooKeeper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|server
operator|.
name|auth
operator|.
name|DigestAuthenticationProvider
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestZKFailoverController
specifier|public
class|class
name|TestZKFailoverController
extends|extends
name|ClientBaseWithFixes
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniZKFCCluster
name|cluster
decl_stmt|;
comment|// Set up ZK digest-based credentials for the purposes of the tests,
comment|// to make sure all of our functionality works with auth and ACLs
comment|// present.
DECL|field|DIGEST_USER_PASS
specifier|private
specifier|static
specifier|final
name|String
name|DIGEST_USER_PASS
init|=
literal|"test-user:test-password"
decl_stmt|;
DECL|field|TEST_AUTH_GOOD
specifier|private
specifier|static
specifier|final
name|String
name|TEST_AUTH_GOOD
init|=
literal|"digest:"
operator|+
name|DIGEST_USER_PASS
decl_stmt|;
DECL|field|DIGEST_USER_HASH
specifier|private
specifier|static
specifier|final
name|String
name|DIGEST_USER_HASH
decl_stmt|;
static|static
block|{
try|try
block|{
name|DIGEST_USER_HASH
operator|=
name|DigestAuthenticationProvider
operator|.
name|generateDigest
argument_list|(
name|DIGEST_USER_PASS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|field|TEST_ACL
specifier|private
specifier|static
specifier|final
name|String
name|TEST_ACL
init|=
literal|"digest:"
operator|+
name|DIGEST_USER_HASH
operator|+
literal|":rwcda"
decl_stmt|;
static|static
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|ActiveStandbyElector
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setupConfAndServices ()
specifier|public
name|void
name|setupConfAndServices
parameter_list|()
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ZKFailoverController
operator|.
name|ZK_ACL_KEY
argument_list|,
name|TEST_ACL
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ZKFailoverController
operator|.
name|ZK_AUTH_KEY
argument_list|,
name|TEST_AUTH_GOOD
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ZKFailoverController
operator|.
name|ZK_QUORUM_KEY
argument_list|,
name|hostPort
argument_list|)
expr_stmt|;
name|this
operator|.
name|cluster
operator|=
operator|new
name|MiniZKFCCluster
argument_list|(
name|conf
argument_list|,
name|getServer
argument_list|(
name|serverFactory
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that the various command lines for formatting the ZK directory    * function correctly.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
DECL|method|testFormatZK ()
specifier|public
name|void
name|testFormatZK
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyHAService
name|svc
init|=
name|cluster
operator|.
name|getService
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Run without formatting the base dir,
comment|// should barf
name|assertEquals
argument_list|(
name|ZKFailoverController
operator|.
name|ERR_CODE_NO_PARENT_ZNODE
argument_list|,
name|runFC
argument_list|(
name|svc
argument_list|)
argument_list|)
expr_stmt|;
comment|// Format the base dir, should succeed
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runFC
argument_list|(
name|svc
argument_list|,
literal|"-formatZK"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Should fail to format if already formatted
name|assertEquals
argument_list|(
name|ZKFailoverController
operator|.
name|ERR_CODE_FORMAT_DENIED
argument_list|,
name|runFC
argument_list|(
name|svc
argument_list|,
literal|"-formatZK"
argument_list|,
literal|"-nonInteractive"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Unless '-force' is on
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runFC
argument_list|(
name|svc
argument_list|,
literal|"-formatZK"
argument_list|,
literal|"-force"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that, if ACLs are specified in the configuration, that    * it sets the ACLs when formatting the parent node.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
DECL|method|testFormatSetsAcls ()
specifier|public
name|void
name|testFormatSetsAcls
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Format the base dir, should succeed
name|DummyHAService
name|svc
init|=
name|cluster
operator|.
name|getService
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runFC
argument_list|(
name|svc
argument_list|,
literal|"-formatZK"
argument_list|)
argument_list|)
expr_stmt|;
name|ZooKeeper
name|otherClient
init|=
name|createClient
argument_list|()
decl_stmt|;
try|try
block|{
comment|// client without auth should not be able to read it
name|Stat
name|stat
init|=
operator|new
name|Stat
argument_list|()
decl_stmt|;
name|otherClient
operator|.
name|getData
argument_list|(
name|ZKFailoverController
operator|.
name|ZK_PARENT_ZNODE_DEFAULT
argument_list|,
literal|false
argument_list|,
name|stat
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Was able to read data without authenticating!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoAuthException
name|nae
parameter_list|)
block|{
comment|// expected
block|}
block|}
comment|/**    * Test that the ZKFC won't run if fencing is not configured for the    * local service.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
DECL|method|testFencingMustBeConfigured ()
specifier|public
name|void
name|testFencingMustBeConfigured
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyHAService
name|svc
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|cluster
operator|.
name|getService
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doThrow
argument_list|(
operator|new
name|BadFencingConfigurationException
argument_list|(
literal|"no fencing"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|svc
argument_list|)
operator|.
name|checkFencingConfigured
argument_list|()
expr_stmt|;
comment|// Format the base dir, should succeed
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runFC
argument_list|(
name|svc
argument_list|,
literal|"-formatZK"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Try to run the actual FC, should fail without a fencer
name|assertEquals
argument_list|(
name|ZKFailoverController
operator|.
name|ERR_CODE_NO_FENCER
argument_list|,
name|runFC
argument_list|(
name|svc
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that, when the health monitor indicates bad health status,    * failover is triggered. Also ensures that graceful active->standby    * transition is used when possible, falling back to fencing when    * the graceful approach fails.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
DECL|method|testAutoFailoverOnBadHealth ()
specifier|public
name|void
name|testAutoFailoverOnBadHealth
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|cluster
operator|.
name|start
argument_list|()
expr_stmt|;
name|DummyHAService
name|svc1
init|=
name|cluster
operator|.
name|getService
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Faking svc0 unhealthy, should failover to svc1"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|setHealthy
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for svc0 to enter standby state"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitForHAState
argument_list|(
literal|0
argument_list|,
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitForHAState
argument_list|(
literal|1
argument_list|,
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Allowing svc0 to be healthy again, making svc1 unreachable "
operator|+
literal|"and fail to gracefully go to standby"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|setUnreachable
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|setHealthy
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Should fail back to svc0 at this point
name|cluster
operator|.
name|waitForHAState
argument_list|(
literal|0
argument_list|,
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
expr_stmt|;
comment|// and fence svc1
name|Mockito
operator|.
name|verify
argument_list|(
name|svc1
operator|.
name|fencer
argument_list|)
operator|.
name|fence
argument_list|(
name|Mockito
operator|.
name|same
argument_list|(
name|svc1
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
DECL|method|testAutoFailoverOnLostZKSession ()
specifier|public
name|void
name|testAutoFailoverOnLostZKSession
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|cluster
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Expire svc0, it should fail over to svc1
name|cluster
operator|.
name|expireAndVerifyFailover
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Expire svc1, it should fail back to svc0
name|cluster
operator|.
name|expireAndVerifyFailover
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"======= Running test cases second time to test "
operator|+
literal|"re-establishment ========="
argument_list|)
expr_stmt|;
comment|// Expire svc0, it should fail over to svc1
name|cluster
operator|.
name|expireAndVerifyFailover
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Expire svc1, it should fail back to svc0
name|cluster
operator|.
name|expireAndVerifyFailover
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test that, if the standby node is unhealthy, it doesn't try to become    * active    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
DECL|method|testDontFailoverToUnhealthyNode ()
specifier|public
name|void
name|testDontFailoverToUnhealthyNode
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|cluster
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Make svc1 unhealthy, and wait for its FC to notice the bad health.
name|cluster
operator|.
name|setHealthy
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitForHealthState
argument_list|(
literal|1
argument_list|,
name|HealthMonitor
operator|.
name|State
operator|.
name|SERVICE_UNHEALTHY
argument_list|)
expr_stmt|;
comment|// Expire svc0
name|cluster
operator|.
name|getElector
argument_list|(
literal|0
argument_list|)
operator|.
name|preventSessionReestablishmentForTests
argument_list|()
expr_stmt|;
try|try
block|{
name|cluster
operator|.
name|expireActiveLockHolder
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Expired svc0's ZK session. Waiting a second to give svc1"
operator|+
literal|" a chance to take the lock, if it is ever going to."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// Ensure that no one holds the lock.
name|cluster
operator|.
name|waitForActiveLockHolder
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Allowing svc0's elector to re-establish its connection"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getElector
argument_list|(
literal|0
argument_list|)
operator|.
name|allowSessionReestablishmentForTests
argument_list|()
expr_stmt|;
block|}
comment|// svc0 should get the lock again
name|cluster
operator|.
name|waitForActiveLockHolder
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test that the ZKFC successfully quits the election when it fails to    * become active. This allows the old node to successfully fail back.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
DECL|method|testBecomingActiveFails ()
specifier|public
name|void
name|testBecomingActiveFails
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|cluster
operator|.
name|start
argument_list|()
expr_stmt|;
name|DummyHAService
name|svc1
init|=
name|cluster
operator|.
name|getService
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Making svc1 fail to become active"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|setFailToBecomeActive
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Faking svc0 unhealthy, should NOT successfully "
operator|+
literal|"failover to svc1"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|setHealthy
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitForHealthState
argument_list|(
literal|0
argument_list|,
name|State
operator|.
name|SERVICE_UNHEALTHY
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitForActiveLockHolder
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|svc1
operator|.
name|proxy
argument_list|,
name|Mockito
operator|.
name|timeout
argument_list|(
literal|2000
argument_list|)
operator|.
name|atLeastOnce
argument_list|()
argument_list|)
operator|.
name|transitionToActive
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitForHAState
argument_list|(
literal|0
argument_list|,
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitForHAState
argument_list|(
literal|1
argument_list|,
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Faking svc0 healthy again, should go back to svc0"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|setHealthy
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitForHAState
argument_list|(
literal|0
argument_list|,
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitForHAState
argument_list|(
literal|1
argument_list|,
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitForActiveLockHolder
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Ensure that we can fail back to svc1  once it it is able
comment|// to become active (e.g the admin has restarted it)
name|LOG
operator|.
name|info
argument_list|(
literal|"Allowing svc1 to become active, expiring svc0"
argument_list|)
expr_stmt|;
name|svc1
operator|.
name|failToBecomeActive
operator|=
literal|false
expr_stmt|;
name|cluster
operator|.
name|expireAndVerifyFailover
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test that, when ZooKeeper fails, the system remains in its    * current state, without triggering any failovers, and without    * causing the active node to enter standby state.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
DECL|method|testZooKeeperFailure ()
specifier|public
name|void
name|testZooKeeperFailure
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|cluster
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Record initial ZK sessions
name|long
name|session0
init|=
name|cluster
operator|.
name|getElector
argument_list|(
literal|0
argument_list|)
operator|.
name|getZKSessionIdForTests
argument_list|()
decl_stmt|;
name|long
name|session1
init|=
name|cluster
operator|.
name|getElector
argument_list|(
literal|1
argument_list|)
operator|.
name|getZKSessionIdForTests
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"====== Stopping ZK server"
argument_list|)
expr_stmt|;
name|stopServer
argument_list|()
expr_stmt|;
name|waitForServerDown
argument_list|(
name|hostPort
argument_list|,
name|CONNECTION_TIMEOUT
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"====== Waiting for services to enter NEUTRAL mode"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitForElectorState
argument_list|(
literal|0
argument_list|,
name|ActiveStandbyElector
operator|.
name|State
operator|.
name|NEUTRAL
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitForElectorState
argument_list|(
literal|1
argument_list|,
name|ActiveStandbyElector
operator|.
name|State
operator|.
name|NEUTRAL
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"====== Checking that the services didn't change HA state"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
name|cluster
operator|.
name|getService
argument_list|(
literal|0
argument_list|)
operator|.
name|state
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|,
name|cluster
operator|.
name|getService
argument_list|(
literal|1
argument_list|)
operator|.
name|state
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"====== Restarting server"
argument_list|)
expr_stmt|;
name|startServer
argument_list|()
expr_stmt|;
name|waitForServerUp
argument_list|(
name|hostPort
argument_list|,
name|CONNECTION_TIMEOUT
argument_list|)
expr_stmt|;
comment|// Nodes should go back to their original states, since they re-obtain
comment|// the same sessions.
name|cluster
operator|.
name|waitForElectorState
argument_list|(
literal|0
argument_list|,
name|ActiveStandbyElector
operator|.
name|State
operator|.
name|ACTIVE
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitForElectorState
argument_list|(
literal|1
argument_list|,
name|ActiveStandbyElector
operator|.
name|State
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
comment|// Check HA states didn't change.
name|cluster
operator|.
name|waitForHAState
argument_list|(
literal|0
argument_list|,
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitForHAState
argument_list|(
literal|1
argument_list|,
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
comment|// Check they re-used the same sessions and didn't spuriously reconnect
name|assertEquals
argument_list|(
name|session0
argument_list|,
name|cluster
operator|.
name|getElector
argument_list|(
literal|0
argument_list|)
operator|.
name|getZKSessionIdForTests
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|session1
argument_list|,
name|cluster
operator|.
name|getElector
argument_list|(
literal|1
argument_list|)
operator|.
name|getZKSessionIdForTests
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|runFC (DummyHAService target, String ... args)
specifier|private
name|int
name|runFC
parameter_list|(
name|DummyHAService
name|target
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|DummyZKFC
name|zkfc
init|=
operator|new
name|DummyZKFC
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|zkfc
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|zkfc
operator|.
name|run
argument_list|(
name|args
argument_list|)
return|;
block|}
block|}
end_class

end_unit

