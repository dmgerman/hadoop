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
name|assertArrayEquals
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
name|net
operator|.
name|InetSocketAddress
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
name|security
operator|.
name|AccessControlException
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
name|authorize
operator|.
name|PolicyProvider
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
name|MultithreadedTestUtil
operator|.
name|TestContext
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
name|MultithreadedTestUtil
operator|.
name|TestingThread
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
operator|.
name|NoNodeException
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
name|ZooKeeperServer
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
name|base
operator|.
name|Preconditions
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

begin_comment
comment|/**  * Harness for starting two dummy ZK FailoverControllers, associated with  * DummyHAServices. This harness starts two such ZKFCs, designated by  * indexes 0 and 1, and provides utilities for building tests around them.  */
end_comment

begin_class
DECL|class|MiniZKFCCluster
specifier|public
class|class
name|MiniZKFCCluster
block|{
DECL|field|ctx
specifier|private
specifier|final
name|TestContext
name|ctx
decl_stmt|;
DECL|field|zks
specifier|private
specifier|final
name|ZooKeeperServer
name|zks
decl_stmt|;
DECL|field|svcs
specifier|private
name|DummyHAService
name|svcs
index|[]
decl_stmt|;
DECL|field|thrs
specifier|private
name|DummyZKFCThread
name|thrs
index|[]
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|sharedResource
specifier|private
name|DummySharedResource
name|sharedResource
init|=
operator|new
name|DummySharedResource
argument_list|()
decl_stmt|;
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
name|MiniZKFCCluster
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|MiniZKFCCluster (Configuration conf, ZooKeeperServer zks)
specifier|public
name|MiniZKFCCluster
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ZooKeeperServer
name|zks
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
comment|// Fast check interval so tests run faster
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HA_HM_CHECK_INTERVAL_KEY
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HA_HM_CONNECT_RETRY_INTERVAL_KEY
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HA_HM_SLEEP_AFTER_DISCONNECT_KEY
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|svcs
operator|=
operator|new
name|DummyHAService
index|[
literal|2
index|]
expr_stmt|;
name|svcs
index|[
literal|0
index|]
operator|=
operator|new
name|DummyHAService
argument_list|(
name|HAServiceState
operator|.
name|INITIALIZING
argument_list|,
operator|new
name|InetSocketAddress
argument_list|(
literal|"svc1"
argument_list|,
literal|1234
argument_list|)
argument_list|)
expr_stmt|;
name|svcs
index|[
literal|0
index|]
operator|.
name|setSharedResource
argument_list|(
name|sharedResource
argument_list|)
expr_stmt|;
name|svcs
index|[
literal|1
index|]
operator|=
operator|new
name|DummyHAService
argument_list|(
name|HAServiceState
operator|.
name|INITIALIZING
argument_list|,
operator|new
name|InetSocketAddress
argument_list|(
literal|"svc2"
argument_list|,
literal|1234
argument_list|)
argument_list|)
expr_stmt|;
name|svcs
index|[
literal|1
index|]
operator|.
name|setSharedResource
argument_list|(
name|sharedResource
argument_list|)
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
operator|new
name|TestContext
argument_list|()
expr_stmt|;
name|this
operator|.
name|zks
operator|=
name|zks
expr_stmt|;
block|}
comment|/**    * Set up two services and their failover controllers. svc1 is started    * first, so that it enters ACTIVE state, and then svc2 is started,    * which enters STANDBY    */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Format the base dir, should succeed
name|thrs
operator|=
operator|new
name|DummyZKFCThread
index|[
literal|2
index|]
expr_stmt|;
name|thrs
index|[
literal|0
index|]
operator|=
operator|new
name|DummyZKFCThread
argument_list|(
name|ctx
argument_list|,
name|svcs
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|thrs
index|[
literal|0
index|]
operator|.
name|zkfc
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-formatZK"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|addThread
argument_list|(
name|thrs
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|thrs
index|[
literal|0
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for svc0 to enter active state"
argument_list|)
expr_stmt|;
name|waitForHAState
argument_list|(
literal|0
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
literal|"Adding svc1"
argument_list|)
expr_stmt|;
name|thrs
index|[
literal|1
index|]
operator|=
operator|new
name|DummyZKFCThread
argument_list|(
name|ctx
argument_list|,
name|svcs
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|thrs
index|[
literal|1
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
name|waitForHAState
argument_list|(
literal|1
argument_list|,
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
block|}
comment|/**    * Stop the services.    * @throws Exception if either of the services had encountered a fatal error    */
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|DummyZKFCThread
name|thr
range|:
name|thrs
control|)
block|{
if|if
condition|(
name|thr
operator|!=
literal|null
condition|)
block|{
name|thr
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ctx
operator|!=
literal|null
condition|)
block|{
name|ctx
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|sharedResource
operator|.
name|assertNoViolations
argument_list|()
expr_stmt|;
block|}
comment|/**    * @return the TestContext implementation used internally. This allows more    * threads to be added to the context, etc.    */
DECL|method|getTestContext ()
specifier|public
name|TestContext
name|getTestContext
parameter_list|()
block|{
return|return
name|ctx
return|;
block|}
DECL|method|getService (int i)
specifier|public
name|DummyHAService
name|getService
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|svcs
index|[
name|i
index|]
return|;
block|}
DECL|method|getElector (int i)
specifier|public
name|ActiveStandbyElector
name|getElector
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|thrs
index|[
name|i
index|]
operator|.
name|zkfc
operator|.
name|getElectorForTests
argument_list|()
return|;
block|}
DECL|method|getZkfc (int i)
specifier|public
name|DummyZKFC
name|getZkfc
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|thrs
index|[
name|i
index|]
operator|.
name|zkfc
return|;
block|}
DECL|method|setHealthy (int idx, boolean healthy)
specifier|public
name|void
name|setHealthy
parameter_list|(
name|int
name|idx
parameter_list|,
name|boolean
name|healthy
parameter_list|)
block|{
name|svcs
index|[
name|idx
index|]
operator|.
name|isHealthy
operator|=
name|healthy
expr_stmt|;
block|}
DECL|method|setFailToBecomeActive (int idx, boolean doFail)
specifier|public
name|void
name|setFailToBecomeActive
parameter_list|(
name|int
name|idx
parameter_list|,
name|boolean
name|doFail
parameter_list|)
block|{
name|svcs
index|[
name|idx
index|]
operator|.
name|failToBecomeActive
operator|=
name|doFail
expr_stmt|;
block|}
DECL|method|setFailToBecomeStandby (int idx, boolean doFail)
specifier|public
name|void
name|setFailToBecomeStandby
parameter_list|(
name|int
name|idx
parameter_list|,
name|boolean
name|doFail
parameter_list|)
block|{
name|svcs
index|[
name|idx
index|]
operator|.
name|failToBecomeStandby
operator|=
name|doFail
expr_stmt|;
block|}
DECL|method|setFailToFence (int idx, boolean doFail)
specifier|public
name|void
name|setFailToFence
parameter_list|(
name|int
name|idx
parameter_list|,
name|boolean
name|doFail
parameter_list|)
block|{
name|svcs
index|[
name|idx
index|]
operator|.
name|failToFence
operator|=
name|doFail
expr_stmt|;
block|}
DECL|method|setUnreachable (int idx, boolean unreachable)
specifier|public
name|void
name|setUnreachable
parameter_list|(
name|int
name|idx
parameter_list|,
name|boolean
name|unreachable
parameter_list|)
block|{
name|svcs
index|[
name|idx
index|]
operator|.
name|actUnreachable
operator|=
name|unreachable
expr_stmt|;
block|}
comment|/**    * Wait for the given HA service to enter the given HA state.    */
DECL|method|waitForHAState (int idx, HAServiceState state)
specifier|public
name|void
name|waitForHAState
parameter_list|(
name|int
name|idx
parameter_list|,
name|HAServiceState
name|state
parameter_list|)
throws|throws
name|Exception
block|{
name|DummyHAService
name|svc
init|=
name|getService
argument_list|(
name|idx
argument_list|)
decl_stmt|;
while|while
condition|(
name|svc
operator|.
name|state
operator|!=
name|state
condition|)
block|{
name|ctx
operator|.
name|checkException
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Wait for the ZKFC to be notified of a change in health state.    */
DECL|method|waitForHealthState (int idx, State state)
specifier|public
name|void
name|waitForHealthState
parameter_list|(
name|int
name|idx
parameter_list|,
name|State
name|state
parameter_list|)
throws|throws
name|Exception
block|{
name|ZKFailoverController
name|zkfc
init|=
name|thrs
index|[
name|idx
index|]
operator|.
name|zkfc
decl_stmt|;
while|while
condition|(
name|zkfc
operator|.
name|getLastHealthState
argument_list|()
operator|!=
name|state
condition|)
block|{
name|ctx
operator|.
name|checkException
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Wait for the given elector to enter the given elector state.    * @param idx the service index (0 or 1)    * @param state the state to wait for    * @throws Exception if it times out, or an exception occurs on one    * of the ZKFC threads while waiting.    */
DECL|method|waitForElectorState (int idx, ActiveStandbyElector.State state)
specifier|public
name|void
name|waitForElectorState
parameter_list|(
name|int
name|idx
parameter_list|,
name|ActiveStandbyElector
operator|.
name|State
name|state
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveStandbyElectorTestUtil
operator|.
name|waitForElectorState
argument_list|(
name|ctx
argument_list|,
name|getElector
argument_list|(
name|idx
argument_list|)
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expire the ZK session of the given service. This requires    * (and asserts) that the given service be the current active.    * @throws NoNodeException if no service holds the lock    */
DECL|method|expireActiveLockHolder (int idx)
specifier|public
name|void
name|expireActiveLockHolder
parameter_list|(
name|int
name|idx
parameter_list|)
throws|throws
name|NoNodeException
block|{
name|Stat
name|stat
init|=
operator|new
name|Stat
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|zks
operator|.
name|getZKDatabase
argument_list|()
operator|.
name|getData
argument_list|(
name|DummyZKFC
operator|.
name|LOCK_ZNODE
argument_list|,
name|stat
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|Ints
operator|.
name|toByteArray
argument_list|(
name|svcs
index|[
name|idx
index|]
operator|.
name|index
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|long
name|session
init|=
name|stat
operator|.
name|getEphemeralOwner
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Expiring svc "
operator|+
name|idx
operator|+
literal|"'s zookeeper session "
operator|+
name|session
argument_list|)
expr_stmt|;
name|zks
operator|.
name|closeSession
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
comment|/**    * Wait for the given HA service to become the active lock holder.    * If the passed svc is null, waits for there to be no active    * lock holder.    */
DECL|method|waitForActiveLockHolder (Integer idx)
specifier|public
name|void
name|waitForActiveLockHolder
parameter_list|(
name|Integer
name|idx
parameter_list|)
throws|throws
name|Exception
block|{
name|DummyHAService
name|svc
init|=
name|idx
operator|==
literal|null
condition|?
literal|null
else|:
name|svcs
index|[
name|idx
index|]
decl_stmt|;
name|ActiveStandbyElectorTestUtil
operator|.
name|waitForActiveLockData
argument_list|(
name|ctx
argument_list|,
name|zks
argument_list|,
name|DummyZKFC
operator|.
name|SCOPED_PARENT_ZNODE
argument_list|,
operator|(
name|idx
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|Ints
operator|.
name|toByteArray
argument_list|(
name|svc
operator|.
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expires the ZK session associated with service 'fromIdx', and waits    * until service 'toIdx' takes over.    * @throws Exception if the target service does not become active    */
DECL|method|expireAndVerifyFailover (int fromIdx, int toIdx)
specifier|public
name|void
name|expireAndVerifyFailover
parameter_list|(
name|int
name|fromIdx
parameter_list|,
name|int
name|toIdx
parameter_list|)
throws|throws
name|Exception
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|fromIdx
operator|!=
name|toIdx
argument_list|)
expr_stmt|;
name|getElector
argument_list|(
name|fromIdx
argument_list|)
operator|.
name|preventSessionReestablishmentForTests
argument_list|()
expr_stmt|;
try|try
block|{
name|expireActiveLockHolder
argument_list|(
name|fromIdx
argument_list|)
expr_stmt|;
name|waitForHAState
argument_list|(
name|fromIdx
argument_list|,
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
name|waitForHAState
argument_list|(
name|toIdx
argument_list|,
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|getElector
argument_list|(
name|fromIdx
argument_list|)
operator|.
name|allowSessionReestablishmentForTests
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test-thread which runs a ZK Failover Controller corresponding    * to a given dummy service.    */
DECL|class|DummyZKFCThread
specifier|private
class|class
name|DummyZKFCThread
extends|extends
name|TestingThread
block|{
DECL|field|zkfc
specifier|private
specifier|final
name|DummyZKFC
name|zkfc
decl_stmt|;
DECL|method|DummyZKFCThread (TestContext ctx, DummyHAService svc)
specifier|public
name|DummyZKFCThread
parameter_list|(
name|TestContext
name|ctx
parameter_list|,
name|DummyHAService
name|svc
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|this
operator|.
name|zkfc
operator|=
operator|new
name|DummyZKFC
argument_list|(
name|conf
argument_list|,
name|svc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWork ()
specifier|public
name|void
name|doWork
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|zkfc
operator|.
name|run
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// Interrupted by main thread, that's OK.
block|}
block|}
block|}
DECL|class|DummyZKFC
specifier|static
class|class
name|DummyZKFC
extends|extends
name|ZKFailoverController
block|{
DECL|field|DUMMY_CLUSTER
specifier|private
specifier|static
specifier|final
name|String
name|DUMMY_CLUSTER
init|=
literal|"dummy-cluster"
decl_stmt|;
DECL|field|SCOPED_PARENT_ZNODE
specifier|public
specifier|static
specifier|final
name|String
name|SCOPED_PARENT_ZNODE
init|=
name|ZKFailoverController
operator|.
name|ZK_PARENT_ZNODE_DEFAULT
operator|+
literal|"/"
operator|+
name|DUMMY_CLUSTER
decl_stmt|;
DECL|field|LOCK_ZNODE
specifier|private
specifier|static
specifier|final
name|String
name|LOCK_ZNODE
init|=
name|SCOPED_PARENT_ZNODE
operator|+
literal|"/"
operator|+
name|ActiveStandbyElector
operator|.
name|LOCK_FILENAME
decl_stmt|;
DECL|field|localTarget
specifier|private
specifier|final
name|DummyHAService
name|localTarget
decl_stmt|;
DECL|method|DummyZKFC (Configuration conf, DummyHAService localTarget)
specifier|public
name|DummyZKFC
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|DummyHAService
name|localTarget
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|localTarget
argument_list|)
expr_stmt|;
name|this
operator|.
name|localTarget
operator|=
name|localTarget
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|targetToData (HAServiceTarget target)
specifier|protected
name|byte
index|[]
name|targetToData
parameter_list|(
name|HAServiceTarget
name|target
parameter_list|)
block|{
return|return
name|Ints
operator|.
name|toByteArray
argument_list|(
operator|(
operator|(
name|DummyHAService
operator|)
name|target
operator|)
operator|.
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|dataToTarget (byte[] data)
specifier|protected
name|HAServiceTarget
name|dataToTarget
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|int
name|index
init|=
name|Ints
operator|.
name|fromByteArray
argument_list|(
name|data
argument_list|)
decl_stmt|;
return|return
name|DummyHAService
operator|.
name|getInstance
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|loginAsFCUser ()
specifier|protected
name|void
name|loginAsFCUser
parameter_list|()
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|getScopeInsideParentNode ()
specifier|protected
name|String
name|getScopeInsideParentNode
parameter_list|()
block|{
return|return
name|DUMMY_CLUSTER
return|;
block|}
annotation|@
name|Override
DECL|method|checkRpcAdminAccess ()
specifier|protected
name|void
name|checkRpcAdminAccess
parameter_list|()
throws|throws
name|AccessControlException
block|{     }
annotation|@
name|Override
DECL|method|getRpcAddressToBindTo ()
specifier|protected
name|InetSocketAddress
name|getRpcAddressToBindTo
parameter_list|()
block|{
return|return
operator|new
name|InetSocketAddress
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|initRPC ()
specifier|protected
name|void
name|initRPC
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|initRPC
argument_list|()
expr_stmt|;
name|localTarget
operator|.
name|zkfcProxy
operator|=
name|this
operator|.
name|getRpcServerForTests
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPolicyProvider ()
specifier|protected
name|PolicyProvider
name|getPolicyProvider
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

