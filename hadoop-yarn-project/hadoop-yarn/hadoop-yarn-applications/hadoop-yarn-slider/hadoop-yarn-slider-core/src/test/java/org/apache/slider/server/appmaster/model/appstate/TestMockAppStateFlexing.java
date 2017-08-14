begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.model.appstate
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
name|appstate
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
name|api
operator|.
name|records
operator|.
name|Container
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
name|resource
operator|.
name|Application
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
name|types
operator|.
name|ApplicationLivenessInformation
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
name|core
operator|.
name|exceptions
operator|.
name|TriggerClusterTeardownException
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
name|MockRoles
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
name|AbstractRMOperation
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
name|CancelSingleRequest
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
name|AppState
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
name|ContainerAssignment
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
name|ArrayList
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

begin_comment
comment|/**  * Test app state flexing.  */
end_comment

begin_class
DECL|class|TestMockAppStateFlexing
specifier|public
class|class
name|TestMockAppStateFlexing
extends|extends
name|BaseMockAppStateTest
implements|implements
name|MockRoles
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
name|BaseMockAppStateTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|getTestName ()
specifier|public
name|String
name|getTestName
parameter_list|()
block|{
return|return
literal|"TestMockAppStateFlexing"
return|;
block|}
comment|//@Test
DECL|method|testFlexDuringLaunchPhase ()
specifier|public
name|void
name|testFlexDuringLaunchPhase
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// ask for one instance of role0
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
name|AbstractRMOperation
argument_list|>
name|ops
init|=
name|appState
operator|.
name|reviewRequestAndReleaseNodes
argument_list|()
decl_stmt|;
comment|// at this point there's now one request in the list
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ops
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// and in a liveness check, one outstanding
name|ApplicationLivenessInformation
name|liveness
init|=
name|appState
operator|.
name|getApplicationLivenessInformation
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|liveness
operator|.
name|requestsOutstanding
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|liveness
operator|.
name|allRequestsSatisfied
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Container
argument_list|>
name|allocations
init|=
name|engine
operator|.
name|execute
argument_list|(
name|ops
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ContainerAssignment
argument_list|>
name|assignments
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AbstractRMOperation
argument_list|>
name|releases
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|appState
operator|.
name|onContainersAllocated
argument_list|(
name|allocations
argument_list|,
name|assignments
argument_list|,
name|releases
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|assignments
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerAssignment
name|assigned
init|=
name|assignments
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Container
name|target
init|=
name|assigned
operator|.
name|container
decl_stmt|;
name|RoleInstance
name|ri
init|=
name|roleInstance
argument_list|(
name|assigned
argument_list|)
decl_stmt|;
name|ops
operator|=
name|appState
operator|.
name|reviewRequestAndReleaseNodes
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|ops
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|liveness
operator|=
name|appState
operator|.
name|getApplicationLivenessInformation
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|liveness
operator|.
name|requestsOutstanding
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|liveness
operator|.
name|allRequestsSatisfied
argument_list|)
expr_stmt|;
comment|//now this is the start point.
name|appState
operator|.
name|containerStartSubmitted
argument_list|(
name|target
argument_list|,
name|ri
argument_list|)
expr_stmt|;
name|ops
operator|=
name|appState
operator|.
name|reviewRequestAndReleaseNodes
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|ops
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|appState
operator|.
name|innerOnNodeManagerContainerStarted
argument_list|(
name|target
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testFlexBeforeAllocationPhase ()
specifier|public
name|void
name|testFlexBeforeAllocationPhase
parameter_list|()
throws|throws
name|Throwable
block|{
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
name|AbstractRMOperation
argument_list|>
name|ops
init|=
name|appState
operator|.
name|reviewRequestAndReleaseNodes
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|ops
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// second scan will find the first run outstanding, so not re-issue
comment|// any more container requests
name|List
argument_list|<
name|AbstractRMOperation
argument_list|>
name|ops2
init|=
name|appState
operator|.
name|reviewRequestAndReleaseNodes
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|ops2
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// and in a liveness check, one outstanding
name|ApplicationLivenessInformation
name|liveness
init|=
name|appState
operator|.
name|getApplicationLivenessInformation
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|liveness
operator|.
name|requestsOutstanding
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|liveness
operator|.
name|allRequestsSatisfied
argument_list|)
expr_stmt|;
name|appState
operator|.
name|refreshClusterStatus
argument_list|()
expr_stmt|;
name|Application
name|application
init|=
name|appState
operator|.
name|getClusterStatus
argument_list|()
decl_stmt|;
comment|// TODO cluster status returns liveness info
comment|//    assertEquals(1, cd.liveness.requestsOutstanding);
block|}
comment|//@Test
DECL|method|testFlexDownTwice ()
specifier|public
name|void
name|testFlexDownTwice
parameter_list|()
throws|throws
name|Throwable
block|{
name|int
name|r0
init|=
literal|6
decl_stmt|;
name|int
name|r1
init|=
literal|0
decl_stmt|;
name|int
name|r2
init|=
literal|0
decl_stmt|;
name|getRole0Status
argument_list|()
operator|.
name|setDesired
argument_list|(
name|r0
argument_list|)
expr_stmt|;
name|getRole1Status
argument_list|()
operator|.
name|setDesired
argument_list|(
name|r1
argument_list|)
expr_stmt|;
name|getRole2Status
argument_list|()
operator|.
name|setDesired
argument_list|(
name|r2
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
name|int
name|clusterSize
init|=
name|r0
operator|+
name|r1
operator|+
name|r2
decl_stmt|;
name|assertEquals
argument_list|(
name|instances
operator|.
name|size
argument_list|()
argument_list|,
name|clusterSize
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"shrinking cluster"
argument_list|)
expr_stmt|;
name|r0
operator|=
literal|4
expr_stmt|;
name|getRole0Status
argument_list|()
operator|.
name|setDesired
argument_list|(
name|r0
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AppState
operator|.
name|NodeCompletionResult
argument_list|>
name|completionResults
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|instances
operator|=
name|createStartAndStopNodes
argument_list|(
name|completionResults
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|instances
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// assert two nodes were released
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|completionResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// no-op review
name|completionResults
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|instances
operator|=
name|createStartAndStopNodes
argument_list|(
name|completionResults
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|instances
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// assert two nodes were released
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|completionResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// now shrink again
name|getRole0Status
argument_list|()
operator|.
name|setDesired
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|completionResults
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|instances
operator|=
name|createStartAndStopNodes
argument_list|(
name|completionResults
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|instances
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// assert two nodes were released
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|completionResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testFlexNegative ()
specifier|public
name|void
name|testFlexNegative
parameter_list|()
throws|throws
name|Throwable
block|{
name|int
name|r0
init|=
literal|6
decl_stmt|;
name|int
name|r1
init|=
literal|0
decl_stmt|;
name|int
name|r2
init|=
literal|0
decl_stmt|;
name|getRole0Status
argument_list|()
operator|.
name|setDesired
argument_list|(
name|r0
argument_list|)
expr_stmt|;
name|getRole1Status
argument_list|()
operator|.
name|setDesired
argument_list|(
name|r1
argument_list|)
expr_stmt|;
name|getRole2Status
argument_list|()
operator|.
name|setDesired
argument_list|(
name|r2
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
name|int
name|clusterSize
init|=
name|r0
operator|+
name|r1
operator|+
name|r2
decl_stmt|;
name|assertEquals
argument_list|(
name|instances
operator|.
name|size
argument_list|()
argument_list|,
name|clusterSize
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"shrinking cluster"
argument_list|)
expr_stmt|;
name|getRole0Status
argument_list|()
operator|.
name|setDesired
argument_list|(
operator|-
literal|2
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AppState
operator|.
name|NodeCompletionResult
argument_list|>
name|completionResults
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|createStartAndStopNodes
argument_list|(
name|completionResults
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TriggerClusterTeardownException
name|e
parameter_list|)
block|{     }
block|}
comment|//@Test
DECL|method|testCancelWithRequestsOutstanding ()
specifier|public
name|void
name|testCancelWithRequestsOutstanding
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// flex cluster size before the original set were allocated
name|getRole0Status
argument_list|()
operator|.
name|setDesired
argument_list|(
literal|6
argument_list|)
expr_stmt|;
comment|// build the ops
name|List
argument_list|<
name|AbstractRMOperation
argument_list|>
name|ops
init|=
name|appState
operator|.
name|reviewRequestAndReleaseNodes
argument_list|()
decl_stmt|;
comment|// here the data structures exist
comment|// go down
name|getRole0Status
argument_list|()
operator|.
name|setDesired
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AbstractRMOperation
argument_list|>
name|ops2
init|=
name|appState
operator|.
name|reviewRequestAndReleaseNodes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|ops2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AbstractRMOperation
name|op
range|:
name|ops2
control|)
block|{
name|assertTrue
argument_list|(
name|op
operator|instanceof
name|CancelSingleRequest
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

