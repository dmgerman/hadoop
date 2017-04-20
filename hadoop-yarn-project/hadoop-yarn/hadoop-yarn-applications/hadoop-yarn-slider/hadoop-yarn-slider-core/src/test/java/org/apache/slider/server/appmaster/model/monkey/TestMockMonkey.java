begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.model.monkey
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|model
operator|.
name|monkey
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
name|slider
operator|.
name|api
operator|.
name|InternalKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|actions
operator|.
name|ActionHalt
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|actions
operator|.
name|ActionKillContainer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|actions
operator|.
name|AsyncAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|actions
operator|.
name|QueueService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|model
operator|.
name|mock
operator|.
name|BaseMockAppStateTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|model
operator|.
name|mock
operator|.
name|MockRMOperationHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|monkey
operator|.
name|ChaosKillAM
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|monkey
operator|.
name|ChaosKillContainer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|monkey
operator|.
name|ChaosMonkeyService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|monkey
operator|.
name|ChaosTarget
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|operations
operator|.
name|ContainerReleaseOperation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|RoleInstance
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
name|Ignore
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_comment
comment|/**  * Test chaos monkey.  */
end_comment

begin_class
DECL|class|TestMockMonkey
specifier|public
class|class
name|TestMockMonkey
extends|extends
name|BaseMockAppStateTest
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
name|TestMockMonkey
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * This queue service is NOT started; tests need to poll the queue    * rather than expect them to execute.    */
DECL|field|queues
specifier|private
name|QueueService
name|queues
decl_stmt|;
DECL|field|monkey
specifier|private
name|ChaosMonkeyService
name|monkey
decl_stmt|;
annotation|@
name|Before
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
block|{
name|YarnConfiguration
name|configuration
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|queues
operator|=
operator|new
name|QueueService
argument_list|()
expr_stmt|;
name|queues
operator|.
name|init
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|monkey
operator|=
operator|new
name|ChaosMonkeyService
argument_list|(
name|METRICS
operator|.
name|getMetrics
argument_list|()
argument_list|,
name|queues
argument_list|)
expr_stmt|;
name|monkey
operator|.
name|init
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMonkeyStart ()
specifier|public
name|void
name|testMonkeyStart
parameter_list|()
throws|throws
name|Throwable
block|{
name|monkey
operator|.
name|start
argument_list|()
expr_stmt|;
name|monkey
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMonkeyPlay ()
specifier|public
name|void
name|testMonkeyPlay
parameter_list|()
throws|throws
name|Throwable
block|{
name|ChaosCounter
name|counter
init|=
operator|new
name|ChaosCounter
argument_list|()
decl_stmt|;
name|monkey
operator|.
name|addTarget
argument_list|(
literal|"target"
argument_list|,
name|counter
argument_list|,
name|InternalKeys
operator|.
name|PROBABILITY_PERCENT_100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|monkey
operator|.
name|getTargetCount
argument_list|()
argument_list|)
expr_stmt|;
name|monkey
operator|.
name|play
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|counter
operator|.
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMonkeySchedule ()
specifier|public
name|void
name|testMonkeySchedule
parameter_list|()
throws|throws
name|Throwable
block|{
name|ChaosCounter
name|counter
init|=
operator|new
name|ChaosCounter
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|monkey
operator|.
name|getTargetCount
argument_list|()
argument_list|)
expr_stmt|;
name|monkey
operator|.
name|addTarget
argument_list|(
literal|"target"
argument_list|,
name|counter
argument_list|,
name|InternalKeys
operator|.
name|PROBABILITY_PERCENT_100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|monkey
operator|.
name|getTargetCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|monkey
operator|.
name|schedule
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queues
operator|.
name|scheduledActions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMonkeyDoesntAddProb0Actions ()
specifier|public
name|void
name|testMonkeyDoesntAddProb0Actions
parameter_list|()
throws|throws
name|Throwable
block|{
name|ChaosCounter
name|counter
init|=
operator|new
name|ChaosCounter
argument_list|()
decl_stmt|;
name|monkey
operator|.
name|addTarget
argument_list|(
literal|"target"
argument_list|,
name|counter
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|monkey
operator|.
name|getTargetCount
argument_list|()
argument_list|)
expr_stmt|;
name|monkey
operator|.
name|play
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|counter
operator|.
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMonkeyScheduleProb0Actions ()
specifier|public
name|void
name|testMonkeyScheduleProb0Actions
parameter_list|()
throws|throws
name|Throwable
block|{
name|ChaosCounter
name|counter
init|=
operator|new
name|ChaosCounter
argument_list|()
decl_stmt|;
name|monkey
operator|.
name|addTarget
argument_list|(
literal|"target"
argument_list|,
name|counter
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|monkey
operator|.
name|schedule
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queues
operator|.
name|scheduledActions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMonkeyPlaySometimes ()
specifier|public
name|void
name|testMonkeyPlaySometimes
parameter_list|()
throws|throws
name|Throwable
block|{
name|ChaosCounter
name|counter
init|=
operator|new
name|ChaosCounter
argument_list|()
decl_stmt|;
name|ChaosCounter
name|counter2
init|=
operator|new
name|ChaosCounter
argument_list|()
decl_stmt|;
name|monkey
operator|.
name|addTarget
argument_list|(
literal|"target1"
argument_list|,
name|counter
argument_list|,
name|InternalKeys
operator|.
name|PROBABILITY_PERCENT_1
operator|*
literal|50
argument_list|)
expr_stmt|;
name|monkey
operator|.
name|addTarget
argument_list|(
literal|"target2"
argument_list|,
name|counter2
argument_list|,
name|InternalKeys
operator|.
name|PROBABILITY_PERCENT_1
operator|*
literal|25
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|monkey
operator|.
name|play
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Counter1 = {} counter2 = {}"
argument_list|,
name|counter
operator|.
name|count
argument_list|,
name|counter2
operator|.
name|count
argument_list|)
expr_stmt|;
comment|/*      * Relying on probability here to give approximate answers      */
name|assertTrue
argument_list|(
name|counter
operator|.
name|count
operator|>
literal|25
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|counter
operator|.
name|count
operator|<
literal|75
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|counter2
operator|.
name|count
operator|<
name|counter
operator|.
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAMKiller ()
specifier|public
name|void
name|testAMKiller
parameter_list|()
throws|throws
name|Throwable
block|{
name|ChaosKillAM
name|chaos
init|=
operator|new
name|ChaosKillAM
argument_list|(
name|queues
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|chaos
operator|.
name|chaosAction
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queues
operator|.
name|scheduledActions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|AsyncAction
name|action
init|=
name|queues
operator|.
name|scheduledActions
operator|.
name|take
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|action
operator|instanceof
name|ActionHalt
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainerKillerEmptyApp ()
specifier|public
name|void
name|testContainerKillerEmptyApp
parameter_list|()
throws|throws
name|Throwable
block|{
name|ChaosKillContainer
name|chaos
init|=
operator|new
name|ChaosKillContainer
argument_list|(
name|appState
argument_list|,
name|queues
argument_list|,
operator|new
name|MockRMOperationHandler
argument_list|()
argument_list|)
decl_stmt|;
name|chaos
operator|.
name|chaosAction
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queues
operator|.
name|scheduledActions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
DECL|method|testContainerKillerIgnoresAM ()
specifier|public
name|void
name|testContainerKillerIgnoresAM
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// TODO: AM needed in live container list?
name|addAppMastertoAppState
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appState
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ChaosKillContainer
name|chaos
init|=
operator|new
name|ChaosKillContainer
argument_list|(
name|appState
argument_list|,
name|queues
argument_list|,
operator|new
name|MockRMOperationHandler
argument_list|()
argument_list|)
decl_stmt|;
name|chaos
operator|.
name|chaosAction
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queues
operator|.
name|scheduledActions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainerKiller ()
specifier|public
name|void
name|testContainerKiller
parameter_list|()
throws|throws
name|Throwable
block|{
name|MockRMOperationHandler
name|ops
init|=
operator|new
name|MockRMOperationHandler
argument_list|()
decl_stmt|;
name|getRole0Status
argument_list|()
operator|.
name|setDesired
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RoleInstance
argument_list|>
name|instances
init|=
name|createAndStartNodes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|instances
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|RoleInstance
name|instance
init|=
name|instances
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ChaosKillContainer
name|chaos
init|=
operator|new
name|ChaosKillContainer
argument_list|(
name|appState
argument_list|,
name|queues
argument_list|,
name|ops
argument_list|)
decl_stmt|;
name|chaos
operator|.
name|chaosAction
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queues
operator|.
name|scheduledActions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|AsyncAction
name|action
init|=
name|queues
operator|.
name|scheduledActions
operator|.
name|take
argument_list|()
decl_stmt|;
name|ActionKillContainer
name|killer
init|=
operator|(
name|ActionKillContainer
operator|)
name|action
decl_stmt|;
name|assertEquals
argument_list|(
name|killer
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|instance
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
name|killer
operator|.
name|execute
argument_list|(
literal|null
argument_list|,
name|queues
argument_list|,
name|appState
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ops
operator|.
name|getNumReleases
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerReleaseOperation
name|operation
init|=
operator|(
name|ContainerReleaseOperation
operator|)
name|ops
operator|.
name|getFirstOp
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|operation
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|instance
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Chaos target that just implements a counter.    */
DECL|class|ChaosCounter
specifier|private
specifier|static
class|class
name|ChaosCounter
implements|implements
name|ChaosTarget
block|{
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
annotation|@
name|Override
DECL|method|chaosAction ()
specifier|public
name|void
name|chaosAction
parameter_list|()
block|{
name|count
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"ChaosCounter{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"count="
argument_list|)
operator|.
name|append
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

