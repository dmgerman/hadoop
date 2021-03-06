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
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|CommonConfigurationKeys
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
name|ActiveStandbyElector
operator|.
name|ActiveStandbyElectorCallback
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
name|ActiveStandbyElector
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
name|test
operator|.
name|GenericTestUtils
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
name|util
operator|.
name|ZKUtil
operator|.
name|ZKAuthInfo
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
name|CreateMode
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
name|ZooDefs
operator|.
name|Ids
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
name|server
operator|.
name|ZooKeeperServer
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
name|AdditionalMatchers
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Ints
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|event
operator|.
name|Level
import|;
end_import

begin_comment
comment|/**  * Test for {@link ActiveStandbyElector} using real zookeeper.  */
end_comment

begin_class
DECL|class|TestActiveStandbyElectorRealZK
specifier|public
class|class
name|TestActiveStandbyElectorRealZK
extends|extends
name|ClientBaseWithFixes
block|{
DECL|field|NUM_ELECTORS
specifier|static
specifier|final
name|int
name|NUM_ELECTORS
init|=
literal|2
decl_stmt|;
static|static
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|ActiveStandbyElector
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|TRACE
argument_list|)
expr_stmt|;
block|}
DECL|field|PARENT_DIR
specifier|static
specifier|final
name|String
name|PARENT_DIR
init|=
literal|"/"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
DECL|field|electors
name|ActiveStandbyElector
index|[]
name|electors
init|=
operator|new
name|ActiveStandbyElector
index|[
name|NUM_ELECTORS
index|]
decl_stmt|;
DECL|field|appDatas
specifier|private
name|byte
index|[]
index|[]
name|appDatas
init|=
operator|new
name|byte
index|[
name|NUM_ELECTORS
index|]
index|[]
decl_stmt|;
DECL|field|cbs
specifier|private
name|ActiveStandbyElectorCallback
index|[]
name|cbs
init|=
operator|new
name|ActiveStandbyElectorCallback
index|[
name|NUM_ELECTORS
index|]
decl_stmt|;
DECL|field|zkServer
specifier|private
name|ZooKeeperServer
name|zkServer
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|zkServer
operator|=
name|getServer
argument_list|(
name|serverFactory
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_ELECTORS
condition|;
name|i
operator|++
control|)
block|{
name|cbs
index|[
name|i
index|]
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ActiveStandbyElectorCallback
operator|.
name|class
argument_list|)
expr_stmt|;
name|appDatas
index|[
name|i
index|]
operator|=
name|Ints
operator|.
name|toByteArray
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|electors
index|[
name|i
index|]
operator|=
operator|new
name|ActiveStandbyElector
argument_list|(
name|hostPort
argument_list|,
literal|5000
argument_list|,
name|PARENT_DIR
argument_list|,
name|Ids
operator|.
name|OPEN_ACL_UNSAFE
argument_list|,
name|Collections
operator|.
expr|<
name|ZKAuthInfo
operator|>
name|emptyList
argument_list|()
argument_list|,
name|cbs
index|[
name|i
index|]
argument_list|,
name|CommonConfigurationKeys
operator|.
name|HA_FC_ELECTOR_ZK_OP_RETRIES_DEFAULT
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkFatalsAndReset ()
specifier|private
name|void
name|checkFatalsAndReset
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_ELECTORS
condition|;
name|i
operator|++
control|)
block|{
name|Mockito
operator|.
name|verify
argument_list|(
name|cbs
index|[
name|i
index|]
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|notifyFatalError
argument_list|(
name|Mockito
operator|.
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|reset
argument_list|(
name|cbs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * the test creates 2 electors which try to become active using a real    * zookeeper server. It verifies that 1 becomes active and 1 becomes standby.    * Upon becoming active the leader quits election and the test verifies that    * the standby now becomes active.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testActiveStandbyTransition ()
specifier|public
name|void
name|testActiveStandbyTransition
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"starting test with parentDir:"
operator|+
name|PARENT_DIR
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|electors
index|[
literal|0
index|]
operator|.
name|parentZNodeExists
argument_list|()
argument_list|)
expr_stmt|;
name|electors
index|[
literal|0
index|]
operator|.
name|ensureParentZNode
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|electors
index|[
literal|0
index|]
operator|.
name|parentZNodeExists
argument_list|()
argument_list|)
expr_stmt|;
comment|// First elector joins election, becomes active.
name|electors
index|[
literal|0
index|]
operator|.
name|joinElection
argument_list|(
name|appDatas
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|ActiveStandbyElectorTestUtil
operator|.
name|waitForActiveLockData
argument_list|(
literal|null
argument_list|,
name|zkServer
argument_list|,
name|PARENT_DIR
argument_list|,
name|appDatas
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|cbs
index|[
literal|0
index|]
argument_list|,
name|Mockito
operator|.
name|timeout
argument_list|(
literal|1000
argument_list|)
argument_list|)
operator|.
name|becomeActive
argument_list|()
expr_stmt|;
name|checkFatalsAndReset
argument_list|()
expr_stmt|;
comment|// Second elector joins election, becomes standby.
name|electors
index|[
literal|1
index|]
operator|.
name|joinElection
argument_list|(
name|appDatas
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|cbs
index|[
literal|1
index|]
argument_list|,
name|Mockito
operator|.
name|timeout
argument_list|(
literal|1000
argument_list|)
argument_list|)
operator|.
name|becomeStandby
argument_list|()
expr_stmt|;
name|checkFatalsAndReset
argument_list|()
expr_stmt|;
comment|// First elector quits, second one should become active
name|electors
index|[
literal|0
index|]
operator|.
name|quitElection
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ActiveStandbyElectorTestUtil
operator|.
name|waitForActiveLockData
argument_list|(
literal|null
argument_list|,
name|zkServer
argument_list|,
name|PARENT_DIR
argument_list|,
name|appDatas
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|cbs
index|[
literal|1
index|]
argument_list|,
name|Mockito
operator|.
name|timeout
argument_list|(
literal|1000
argument_list|)
argument_list|)
operator|.
name|becomeActive
argument_list|()
expr_stmt|;
name|checkFatalsAndReset
argument_list|()
expr_stmt|;
comment|// First one rejoins, becomes standby, second one stays active
name|electors
index|[
literal|0
index|]
operator|.
name|joinElection
argument_list|(
name|appDatas
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|cbs
index|[
literal|0
index|]
argument_list|,
name|Mockito
operator|.
name|timeout
argument_list|(
literal|1000
argument_list|)
argument_list|)
operator|.
name|becomeStandby
argument_list|()
expr_stmt|;
name|checkFatalsAndReset
argument_list|()
expr_stmt|;
comment|// Second one expires, first one becomes active
name|electors
index|[
literal|1
index|]
operator|.
name|preventSessionReestablishmentForTests
argument_list|()
expr_stmt|;
try|try
block|{
name|zkServer
operator|.
name|closeSession
argument_list|(
name|electors
index|[
literal|1
index|]
operator|.
name|getZKSessionIdForTests
argument_list|()
argument_list|)
expr_stmt|;
name|ActiveStandbyElectorTestUtil
operator|.
name|waitForActiveLockData
argument_list|(
literal|null
argument_list|,
name|zkServer
argument_list|,
name|PARENT_DIR
argument_list|,
name|appDatas
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|cbs
index|[
literal|1
index|]
argument_list|,
name|Mockito
operator|.
name|timeout
argument_list|(
literal|1000
argument_list|)
argument_list|)
operator|.
name|enterNeutralMode
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|cbs
index|[
literal|0
index|]
argument_list|,
name|Mockito
operator|.
name|timeout
argument_list|(
literal|1000
argument_list|)
argument_list|)
operator|.
name|fenceOldActive
argument_list|(
name|AdditionalMatchers
operator|.
name|aryEq
argument_list|(
name|appDatas
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|cbs
index|[
literal|0
index|]
argument_list|,
name|Mockito
operator|.
name|timeout
argument_list|(
literal|1000
argument_list|)
argument_list|)
operator|.
name|becomeActive
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|electors
index|[
literal|1
index|]
operator|.
name|allowSessionReestablishmentForTests
argument_list|()
expr_stmt|;
block|}
comment|// Second one eventually reconnects and becomes standby
name|Mockito
operator|.
name|verify
argument_list|(
name|cbs
index|[
literal|1
index|]
argument_list|,
name|Mockito
operator|.
name|timeout
argument_list|(
literal|5000
argument_list|)
argument_list|)
operator|.
name|becomeStandby
argument_list|()
expr_stmt|;
name|checkFatalsAndReset
argument_list|()
expr_stmt|;
comment|// First one expires, second one should become active
name|electors
index|[
literal|0
index|]
operator|.
name|preventSessionReestablishmentForTests
argument_list|()
expr_stmt|;
try|try
block|{
name|zkServer
operator|.
name|closeSession
argument_list|(
name|electors
index|[
literal|0
index|]
operator|.
name|getZKSessionIdForTests
argument_list|()
argument_list|)
expr_stmt|;
name|ActiveStandbyElectorTestUtil
operator|.
name|waitForActiveLockData
argument_list|(
literal|null
argument_list|,
name|zkServer
argument_list|,
name|PARENT_DIR
argument_list|,
name|appDatas
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|cbs
index|[
literal|0
index|]
argument_list|,
name|Mockito
operator|.
name|timeout
argument_list|(
literal|1000
argument_list|)
argument_list|)
operator|.
name|enterNeutralMode
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|cbs
index|[
literal|1
index|]
argument_list|,
name|Mockito
operator|.
name|timeout
argument_list|(
literal|1000
argument_list|)
argument_list|)
operator|.
name|fenceOldActive
argument_list|(
name|AdditionalMatchers
operator|.
name|aryEq
argument_list|(
name|appDatas
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|cbs
index|[
literal|1
index|]
argument_list|,
name|Mockito
operator|.
name|timeout
argument_list|(
literal|1000
argument_list|)
argument_list|)
operator|.
name|becomeActive
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|electors
index|[
literal|0
index|]
operator|.
name|allowSessionReestablishmentForTests
argument_list|()
expr_stmt|;
block|}
name|checkFatalsAndReset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
DECL|method|testHandleSessionExpiration ()
specifier|public
name|void
name|testHandleSessionExpiration
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveStandbyElectorCallback
name|cb
init|=
name|cbs
index|[
literal|0
index|]
decl_stmt|;
name|byte
index|[]
name|appData
init|=
name|appDatas
index|[
literal|0
index|]
decl_stmt|;
name|ActiveStandbyElector
name|elector
init|=
name|electors
index|[
literal|0
index|]
decl_stmt|;
comment|// Let the first elector become active
name|elector
operator|.
name|ensureParentZNode
argument_list|()
expr_stmt|;
name|elector
operator|.
name|joinElection
argument_list|(
name|appData
argument_list|)
expr_stmt|;
name|ZooKeeperServer
name|zks
init|=
name|getServer
argument_list|(
name|serverFactory
argument_list|)
decl_stmt|;
name|ActiveStandbyElectorTestUtil
operator|.
name|waitForActiveLockData
argument_list|(
literal|null
argument_list|,
name|zks
argument_list|,
name|PARENT_DIR
argument_list|,
name|appData
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|cb
argument_list|,
name|Mockito
operator|.
name|timeout
argument_list|(
literal|1000
argument_list|)
argument_list|)
operator|.
name|becomeActive
argument_list|()
expr_stmt|;
name|checkFatalsAndReset
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"========================== Expiring session"
argument_list|)
expr_stmt|;
name|zks
operator|.
name|closeSession
argument_list|(
name|elector
operator|.
name|getZKSessionIdForTests
argument_list|()
argument_list|)
expr_stmt|;
comment|// Should enter neutral mode when disconnected
name|Mockito
operator|.
name|verify
argument_list|(
name|cb
argument_list|,
name|Mockito
operator|.
name|timeout
argument_list|(
literal|1000
argument_list|)
argument_list|)
operator|.
name|enterNeutralMode
argument_list|()
expr_stmt|;
comment|// Should re-join the election and regain active
name|ActiveStandbyElectorTestUtil
operator|.
name|waitForActiveLockData
argument_list|(
literal|null
argument_list|,
name|zks
argument_list|,
name|PARENT_DIR
argument_list|,
name|appData
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|cb
argument_list|,
name|Mockito
operator|.
name|timeout
argument_list|(
literal|1000
argument_list|)
argument_list|)
operator|.
name|becomeActive
argument_list|()
expr_stmt|;
name|checkFatalsAndReset
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"========================== Quitting election"
argument_list|)
expr_stmt|;
name|elector
operator|.
name|quitElection
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ActiveStandbyElectorTestUtil
operator|.
name|waitForActiveLockData
argument_list|(
literal|null
argument_list|,
name|zks
argument_list|,
name|PARENT_DIR
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Double check that we don't accidentally re-join the election
comment|// due to receiving the "expired" event.
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|cb
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|becomeActive
argument_list|()
expr_stmt|;
name|ActiveStandbyElectorTestUtil
operator|.
name|waitForActiveLockData
argument_list|(
literal|null
argument_list|,
name|zks
argument_list|,
name|PARENT_DIR
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkFatalsAndReset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
DECL|method|testHandleSessionExpirationOfStandby ()
specifier|public
name|void
name|testHandleSessionExpirationOfStandby
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Let elector 0 be active
name|electors
index|[
literal|0
index|]
operator|.
name|ensureParentZNode
argument_list|()
expr_stmt|;
name|electors
index|[
literal|0
index|]
operator|.
name|joinElection
argument_list|(
name|appDatas
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|ZooKeeperServer
name|zks
init|=
name|getServer
argument_list|(
name|serverFactory
argument_list|)
decl_stmt|;
name|ActiveStandbyElectorTestUtil
operator|.
name|waitForActiveLockData
argument_list|(
literal|null
argument_list|,
name|zks
argument_list|,
name|PARENT_DIR
argument_list|,
name|appDatas
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|cbs
index|[
literal|0
index|]
argument_list|,
name|Mockito
operator|.
name|timeout
argument_list|(
literal|1000
argument_list|)
argument_list|)
operator|.
name|becomeActive
argument_list|()
expr_stmt|;
name|checkFatalsAndReset
argument_list|()
expr_stmt|;
comment|// Let elector 1 be standby
name|electors
index|[
literal|1
index|]
operator|.
name|joinElection
argument_list|(
name|appDatas
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|ActiveStandbyElectorTestUtil
operator|.
name|waitForElectorState
argument_list|(
literal|null
argument_list|,
name|electors
index|[
literal|1
index|]
argument_list|,
name|State
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"========================== Expiring standby's session"
argument_list|)
expr_stmt|;
name|zks
operator|.
name|closeSession
argument_list|(
name|electors
index|[
literal|1
index|]
operator|.
name|getZKSessionIdForTests
argument_list|()
argument_list|)
expr_stmt|;
comment|// Should enter neutral mode when disconnected
name|Mockito
operator|.
name|verify
argument_list|(
name|cbs
index|[
literal|1
index|]
argument_list|,
name|Mockito
operator|.
name|timeout
argument_list|(
literal|1000
argument_list|)
argument_list|)
operator|.
name|enterNeutralMode
argument_list|()
expr_stmt|;
comment|// Should re-join the election and go back to STANDBY
name|ActiveStandbyElectorTestUtil
operator|.
name|waitForElectorState
argument_list|(
literal|null
argument_list|,
name|electors
index|[
literal|1
index|]
argument_list|,
name|State
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
name|checkFatalsAndReset
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"========================== Quitting election"
argument_list|)
expr_stmt|;
name|electors
index|[
literal|1
index|]
operator|.
name|quitElection
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Double check that we don't accidentally re-join the election
comment|// by quitting elector 0 and ensuring elector 1 doesn't become active
name|electors
index|[
literal|0
index|]
operator|.
name|quitElection
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// due to receiving the "expired" event.
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|cbs
index|[
literal|1
index|]
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|becomeActive
argument_list|()
expr_stmt|;
name|ActiveStandbyElectorTestUtil
operator|.
name|waitForActiveLockData
argument_list|(
literal|null
argument_list|,
name|zks
argument_list|,
name|PARENT_DIR
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkFatalsAndReset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
DECL|method|testDontJoinElectionOnDisconnectAndReconnect ()
specifier|public
name|void
name|testDontJoinElectionOnDisconnectAndReconnect
parameter_list|()
throws|throws
name|Exception
block|{
name|electors
index|[
literal|0
index|]
operator|.
name|ensureParentZNode
argument_list|()
expr_stmt|;
name|stopServer
argument_list|()
expr_stmt|;
name|ActiveStandbyElectorTestUtil
operator|.
name|waitForElectorState
argument_list|(
literal|null
argument_list|,
name|electors
index|[
literal|0
index|]
argument_list|,
name|State
operator|.
name|NEUTRAL
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
comment|// Have to sleep to allow time for the clients to reconnect.
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|cbs
index|[
literal|0
index|]
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|becomeActive
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|cbs
index|[
literal|1
index|]
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|becomeActive
argument_list|()
expr_stmt|;
name|checkFatalsAndReset
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test to verify that proper ZooKeeper ACLs can be updated on    * ActiveStandbyElector's parent znode.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
DECL|method|testSetZooKeeperACLsOnParentZnodeName ()
specifier|public
name|void
name|testSetZooKeeperACLsOnParentZnodeName
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveStandbyElectorCallback
name|cb
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ActiveStandbyElectorCallback
operator|.
name|class
argument_list|)
decl_stmt|;
name|ActiveStandbyElector
name|elector
init|=
operator|new
name|ActiveStandbyElector
argument_list|(
name|hostPort
argument_list|,
literal|5000
argument_list|,
name|PARENT_DIR
argument_list|,
name|Ids
operator|.
name|READ_ACL_UNSAFE
argument_list|,
name|Collections
operator|.
expr|<
name|ZKAuthInfo
operator|>
name|emptyList
argument_list|()
argument_list|,
name|cb
argument_list|,
name|CommonConfigurationKeys
operator|.
name|HA_FC_ELECTOR_ZK_OP_RETRIES_DEFAULT
argument_list|)
decl_stmt|;
comment|// Simulate the case by pre-creating znode 'parentZnodeName'. Then updates
comment|// znode's data so that data version will be increased to 1. Here znode's
comment|// aversion is 0.
name|ZooKeeper
name|otherClient
init|=
name|createClient
argument_list|()
decl_stmt|;
name|otherClient
operator|.
name|create
argument_list|(
name|PARENT_DIR
argument_list|,
literal|"sample1"
operator|.
name|getBytes
argument_list|()
argument_list|,
name|Ids
operator|.
name|OPEN_ACL_UNSAFE
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|otherClient
operator|.
name|setData
argument_list|(
name|PARENT_DIR
argument_list|,
literal|"sample2"
operator|.
name|getBytes
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|otherClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|elector
operator|.
name|ensureParentZNode
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

