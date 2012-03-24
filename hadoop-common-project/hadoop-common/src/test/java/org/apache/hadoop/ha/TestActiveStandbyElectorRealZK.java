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
name|io
operator|.
name|File
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
name|UUID
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
name|CountDownLatch
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
name|TimeUnit
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
name|Test
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
name|test
operator|.
name|ClientBase
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
name|ClientBase
block|{
DECL|field|NUM_ELECTORS
specifier|static
specifier|final
name|int
name|NUM_ELECTORS
init|=
literal|2
decl_stmt|;
DECL|field|zkClient
specifier|static
name|ZooKeeper
index|[]
name|zkClient
init|=
operator|new
name|ZooKeeper
index|[
name|NUM_ELECTORS
index|]
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
DECL|field|activeIndex
name|int
name|activeIndex
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|standbyIndex
name|int
name|standbyIndex
init|=
operator|-
literal|1
decl_stmt|;
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
comment|// build.test.dir is used by zookeeper
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"build.test.dir"
argument_list|,
literal|"build"
argument_list|)
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
comment|/**    * The class object runs on a thread and waits for a signal to start from the     * test object. On getting the signal it joins the election and thus by doing     * this on multiple threads we can test simultaneous attempts at leader lock     * creation. after joining the election, the object waits on a signal to exit.    * this signal comes when the object's elector has become a leader or there is     * an unexpected fatal error. this lets another thread object to become a     * leader.    */
DECL|class|ThreadRunner
class|class
name|ThreadRunner
extends|extends
name|TestingThread
implements|implements
name|ActiveStandbyElectorCallback
block|{
DECL|field|index
name|int
name|index
decl_stmt|;
DECL|field|hasBecomeActive
name|CountDownLatch
name|hasBecomeActive
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|method|ThreadRunner (TestContext ctx, int idx)
name|ThreadRunner
parameter_list|(
name|TestContext
name|ctx
parameter_list|,
name|int
name|idx
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|index
operator|=
name|idx
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
name|LOG
operator|.
name|info
argument_list|(
literal|"starting "
operator|+
name|index
argument_list|)
expr_stmt|;
comment|// join election
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
name|data
index|[
literal|0
index|]
operator|=
operator|(
name|byte
operator|)
name|index
expr_stmt|;
name|ActiveStandbyElector
name|elector
init|=
name|electors
index|[
name|index
index|]
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"joining "
operator|+
name|index
argument_list|)
expr_stmt|;
name|elector
operator|.
name|joinElection
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|hasBecomeActive
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// quit election to allow other elector to become active
name|elector
operator|.
name|quitElection
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"ending "
operator|+
name|index
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|becomeActive ()
specifier|public
specifier|synchronized
name|void
name|becomeActive
parameter_list|()
block|{
name|reportActive
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"active "
operator|+
name|index
argument_list|)
expr_stmt|;
name|hasBecomeActive
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|becomeStandby ()
specifier|public
specifier|synchronized
name|void
name|becomeStandby
parameter_list|()
block|{
name|reportStandby
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"standby "
operator|+
name|index
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|enterNeutralMode ()
specifier|public
specifier|synchronized
name|void
name|enterNeutralMode
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"neutral "
operator|+
name|index
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|notifyFatalError (String errorMessage)
specifier|public
specifier|synchronized
name|void
name|notifyFatalError
parameter_list|(
name|String
name|errorMessage
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"fatal "
operator|+
name|index
operator|+
literal|" .Error message:"
operator|+
name|errorMessage
argument_list|)
expr_stmt|;
name|this
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fenceOldActive (byte[] data)
specifier|public
name|void
name|fenceOldActive
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"fenceOldActive "
operator|+
name|index
argument_list|)
expr_stmt|;
comment|// should not fence itself
name|Assert
operator|.
name|assertTrue
argument_list|(
name|index
operator|!=
name|data
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|reportActive (int index)
specifier|synchronized
name|void
name|reportActive
parameter_list|(
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|activeIndex
operator|==
operator|-
literal|1
condition|)
block|{
name|activeIndex
operator|=
name|index
expr_stmt|;
block|}
else|else
block|{
comment|// standby should become active
name|Assert
operator|.
name|assertEquals
argument_list|(
name|standbyIndex
argument_list|,
name|index
argument_list|)
expr_stmt|;
comment|// old active should not become active
name|Assert
operator|.
name|assertFalse
argument_list|(
name|activeIndex
operator|==
name|index
argument_list|)
expr_stmt|;
block|}
name|activeIndex
operator|=
name|index
expr_stmt|;
block|}
DECL|method|reportStandby (int index)
specifier|synchronized
name|void
name|reportStandby
parameter_list|(
name|int
name|index
parameter_list|)
block|{
comment|// only 1 standby should be reported and it should not be the same as active
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|standbyIndex
argument_list|)
expr_stmt|;
name|standbyIndex
operator|=
name|index
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|activeIndex
operator|==
name|standbyIndex
argument_list|)
expr_stmt|;
block|}
comment|/**    * the test creates 2 electors which try to become active using a real    * zookeeper server. It verifies that 1 becomes active and 1 becomes standby.    * Upon becoming active the leader quits election and the test verifies that    * the standby now becomes active. these electors run on different threads and     * callback to the test class to report active and standby where the outcome     * is verified    * @throws Exception     */
annotation|@
name|Test
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
name|TestContext
name|ctx
init|=
operator|new
name|TestContext
argument_list|()
decl_stmt|;
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
name|LOG
operator|.
name|info
argument_list|(
literal|"creating "
operator|+
name|i
argument_list|)
expr_stmt|;
specifier|final
name|ZooKeeper
name|zk
init|=
name|createClient
argument_list|()
decl_stmt|;
assert|assert
name|zk
operator|!=
literal|null
assert|;
name|ThreadRunner
name|tr
init|=
operator|new
name|ThreadRunner
argument_list|(
name|ctx
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|electors
index|[
name|i
index|]
operator|=
operator|new
name|ActiveStandbyElector
argument_list|(
literal|"hostPort"
argument_list|,
literal|1000
argument_list|,
name|PARENT_DIR
argument_list|,
name|Ids
operator|.
name|OPEN_ACL_UNSAFE
argument_list|,
name|tr
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
specifier|synchronized
name|ZooKeeper
name|getNewZooKeeper
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|zk
return|;
block|}
block|}
expr_stmt|;
name|ctx
operator|.
name|addThread
argument_list|(
name|tr
argument_list|)
expr_stmt|;
block|}
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
name|ctx
operator|.
name|startThreads
argument_list|()
expr_stmt|;
name|ctx
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

