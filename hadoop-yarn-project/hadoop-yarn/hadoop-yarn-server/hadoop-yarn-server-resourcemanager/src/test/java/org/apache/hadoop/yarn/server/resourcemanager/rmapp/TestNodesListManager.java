begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmapp
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
operator|.
name|rmapp
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|argThat
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
name|doNothing
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
name|spy
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
name|ContainerState
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
name|Resource
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
name|event
operator|.
name|AbstractEvent
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
name|event
operator|.
name|Dispatcher
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
name|event
operator|.
name|DrainDispatcher
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
name|event
operator|.
name|Event
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
name|event
operator|.
name|EventHandler
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
name|MockAM
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
name|MockNM
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
name|MockNodes
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
name|MockRM
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
name|NodesListManager
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
name|NodesListManagerEvent
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
name|NodesListManagerEventType
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
name|attempt
operator|.
name|RMAppAttempt
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
name|attempt
operator|.
name|RMAppAttemptState
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
name|rmnode
operator|.
name|RMNode
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
name|ControlledClock
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
name|mockito
operator|.
name|ArgumentMatcher
import|;
end_import

begin_class
DECL|class|TestNodesListManager
specifier|public
class|class
name|TestNodesListManager
block|{
comment|// To hold list of application for which event was received
DECL|field|applist
name|ArrayList
argument_list|<
name|ApplicationId
argument_list|>
name|applist
init|=
operator|new
name|ArrayList
argument_list|<
name|ApplicationId
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testNodeUsableEvent ()
specifier|public
name|void
name|testNodeUsableEvent
parameter_list|()
throws|throws
name|Exception
block|{
name|GenericTestUtils
operator|.
name|setRootLogLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
specifier|final
name|Dispatcher
name|dispatcher
init|=
name|getDispatcher
argument_list|()
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Dispatcher
name|createDispatcher
parameter_list|()
block|{
return|return
name|dispatcher
return|;
block|}
block|}
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"h1:1234"
argument_list|,
literal|28000
argument_list|)
decl_stmt|;
name|NodesListManager
name|nodesListManager
init|=
name|rm
operator|.
name|getNodesListManager
argument_list|()
decl_stmt|;
name|Resource
name|clusterResource
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|28000
argument_list|,
literal|8
argument_list|)
decl_stmt|;
name|RMNode
name|rmnode
init|=
name|MockNodes
operator|.
name|newNodeInfo
argument_list|(
literal|1
argument_list|,
name|clusterResource
argument_list|)
decl_stmt|;
comment|// Create killing APP
name|RMApp
name|killrmApp
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|rm
operator|.
name|killApp
argument_list|(
name|killrmApp
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|rm
operator|.
name|waitForState
argument_list|(
name|killrmApp
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppState
operator|.
name|KILLED
argument_list|)
expr_stmt|;
comment|// Create finish APP
name|RMApp
name|finshrmApp
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|RMAppAttempt
name|attempt
init|=
name|finshrmApp
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
name|MockAM
name|am
init|=
name|rm
operator|.
name|sendAMLaunched
argument_list|(
name|attempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|am
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
name|am
operator|.
name|unregisterAppAttempt
argument_list|()
expr_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
name|attempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
literal|1
argument_list|,
name|ContainerState
operator|.
name|COMPLETE
argument_list|)
expr_stmt|;
name|rm
operator|.
name|waitForState
argument_list|(
name|am
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|RMAppAttemptState
operator|.
name|FINISHED
argument_list|)
expr_stmt|;
comment|// Create submitted App
name|RMApp
name|subrmApp
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|)
decl_stmt|;
comment|// Fire Event for NODE_USABLE
name|nodesListManager
operator|.
name|handle
argument_list|(
operator|new
name|NodesListManagerEvent
argument_list|(
name|NodesListManagerEventType
operator|.
name|NODE_USABLE
argument_list|,
name|rmnode
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|applist
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Event based on running app expected "
operator|+
name|subrmApp
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|applist
operator|.
name|contains
argument_list|(
name|subrmApp
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Event based on finish app not expected "
operator|+
name|finshrmApp
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|applist
operator|.
name|contains
argument_list|(
name|finshrmApp
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Event based on killed app not expected "
operator|+
name|killrmApp
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|applist
operator|.
name|contains
argument_list|(
name|killrmApp
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Events received should have beeen more than 1"
argument_list|)
expr_stmt|;
block|}
name|applist
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Fire Event for NODE_UNUSABLE
name|nodesListManager
operator|.
name|handle
argument_list|(
operator|new
name|NodesListManagerEvent
argument_list|(
name|NodesListManagerEventType
operator|.
name|NODE_UNUSABLE
argument_list|,
name|rmnode
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|applist
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Event based on running app expected "
operator|+
name|subrmApp
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|applist
operator|.
name|contains
argument_list|(
name|subrmApp
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Event based on finish app not expected "
operator|+
name|finshrmApp
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|applist
operator|.
name|contains
argument_list|(
name|finshrmApp
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Event based on killed app not expected "
operator|+
name|killrmApp
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|applist
operator|.
name|contains
argument_list|(
name|killrmApp
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Events received should have beeen more than 1"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCachedResolver ()
specifier|public
name|void
name|testCachedResolver
parameter_list|()
throws|throws
name|Exception
block|{
name|GenericTestUtils
operator|.
name|setRootLogLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
name|ControlledClock
name|clock
init|=
operator|new
name|ControlledClock
argument_list|()
decl_stmt|;
name|clock
operator|.
name|setTime
argument_list|(
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|int
name|CACHE_EXPIRY_INTERVAL_SECS
init|=
literal|30
decl_stmt|;
name|NodesListManager
operator|.
name|CachedResolver
name|resolver
init|=
operator|new
name|NodesListManager
operator|.
name|CachedResolver
argument_list|(
name|clock
argument_list|,
name|CACHE_EXPIRY_INTERVAL_SECS
argument_list|)
decl_stmt|;
name|resolver
operator|.
name|init
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|resolver
operator|.
name|start
argument_list|()
expr_stmt|;
name|resolver
operator|.
name|addToCache
argument_list|(
literal|"testCachedResolverHost1"
argument_list|,
literal|"1.1.1.1"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"1.1.1.1"
argument_list|,
name|resolver
operator|.
name|resolve
argument_list|(
literal|"testCachedResolverHost1"
argument_list|)
argument_list|)
expr_stmt|;
name|resolver
operator|.
name|addToCache
argument_list|(
literal|"testCachedResolverHost2"
argument_list|,
literal|"1.1.1.2"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"1.1.1.1"
argument_list|,
name|resolver
operator|.
name|resolve
argument_list|(
literal|"testCachedResolverHost1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"1.1.1.2"
argument_list|,
name|resolver
operator|.
name|resolve
argument_list|(
literal|"testCachedResolverHost2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test removeFromCache
name|resolver
operator|.
name|removeFromCache
argument_list|(
literal|"testCachedResolverHost1"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"1.1.1.1"
argument_list|,
name|resolver
operator|.
name|resolve
argument_list|(
literal|"testCachedResolverHost1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"1.1.1.2"
argument_list|,
name|resolver
operator|.
name|resolve
argument_list|(
literal|"testCachedResolverHost2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test expiry
name|clock
operator|.
name|tickMsec
argument_list|(
name|CACHE_EXPIRY_INTERVAL_SECS
operator|*
literal|1000
operator|+
literal|1
argument_list|)
expr_stmt|;
name|resolver
operator|.
name|getExpireChecker
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"1.1.1.1"
argument_list|,
name|resolver
operator|.
name|resolve
argument_list|(
literal|"testCachedResolverHost1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"1.1.1.2"
argument_list|,
name|resolver
operator|.
name|resolve
argument_list|(
literal|"testCachedResolverHost2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultResolver ()
specifier|public
name|void
name|testDefaultResolver
parameter_list|()
throws|throws
name|Exception
block|{
name|GenericTestUtils
operator|.
name|setRootLogLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|rm
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|NodesListManager
name|nodesListManager
init|=
name|rm
operator|.
name|getNodesListManager
argument_list|()
decl_stmt|;
name|NodesListManager
operator|.
name|Resolver
name|resolver
init|=
name|nodesListManager
operator|.
name|getResolver
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"default resolver should be DirectResolver"
argument_list|,
name|resolver
operator|instanceof
name|NodesListManager
operator|.
name|DirectResolver
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCachedResolverWithEvent ()
specifier|public
name|void
name|testCachedResolverWithEvent
parameter_list|()
throws|throws
name|Exception
block|{
name|GenericTestUtils
operator|.
name|setRootLogLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_NODE_IP_CACHE_EXPIRY_INTERVAL_SECS
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|rm
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|NodesListManager
name|nodesListManager
init|=
name|rm
operator|.
name|getNodesListManager
argument_list|()
decl_stmt|;
name|nodesListManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|nodesListManager
operator|.
name|start
argument_list|()
expr_stmt|;
name|NodesListManager
operator|.
name|CachedResolver
name|resolver
init|=
operator|(
name|NodesListManager
operator|.
name|CachedResolver
operator|)
name|nodesListManager
operator|.
name|getResolver
argument_list|()
decl_stmt|;
name|resolver
operator|.
name|addToCache
argument_list|(
literal|"testCachedResolverHost1"
argument_list|,
literal|"1.1.1.1"
argument_list|)
expr_stmt|;
name|resolver
operator|.
name|addToCache
argument_list|(
literal|"testCachedResolverHost2"
argument_list|,
literal|"1.1.1.2"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"1.1.1.1"
argument_list|,
name|resolver
operator|.
name|resolve
argument_list|(
literal|"testCachedResolverHost1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"1.1.1.2"
argument_list|,
name|resolver
operator|.
name|resolve
argument_list|(
literal|"testCachedResolverHost2"
argument_list|)
argument_list|)
expr_stmt|;
name|RMNode
name|rmnode1
init|=
name|MockNodes
operator|.
name|newNodeInfo
argument_list|(
literal|1
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|28000
argument_list|,
literal|8
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|"testCachedResolverHost1"
argument_list|,
literal|1234
argument_list|)
decl_stmt|;
name|RMNode
name|rmnode2
init|=
name|MockNodes
operator|.
name|newNodeInfo
argument_list|(
literal|1
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|28000
argument_list|,
literal|8
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|"testCachedResolverHost2"
argument_list|,
literal|1234
argument_list|)
decl_stmt|;
name|nodesListManager
operator|.
name|handle
argument_list|(
operator|new
name|NodesListManagerEvent
argument_list|(
name|NodesListManagerEventType
operator|.
name|NODE_USABLE
argument_list|,
name|rmnode1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"1.1.1.1"
argument_list|,
name|resolver
operator|.
name|resolve
argument_list|(
literal|"testCachedResolverHost1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"1.1.1.2"
argument_list|,
name|resolver
operator|.
name|resolve
argument_list|(
literal|"testCachedResolverHost2"
argument_list|)
argument_list|)
expr_stmt|;
name|nodesListManager
operator|.
name|handle
argument_list|(
operator|new
name|NodesListManagerEvent
argument_list|(
name|NodesListManagerEventType
operator|.
name|NODE_USABLE
argument_list|,
name|rmnode2
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"1.1.1.1"
argument_list|,
name|resolver
operator|.
name|resolve
argument_list|(
literal|"testCachedResolverHost1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"1.1.1.2"
argument_list|,
name|resolver
operator|.
name|resolve
argument_list|(
literal|"testCachedResolverHost2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*    * Create dispatcher object    */
DECL|method|getDispatcher ()
specifier|private
name|Dispatcher
name|getDispatcher
parameter_list|()
block|{
return|return
operator|new
name|DrainDispatcher
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
name|EventHandler
argument_list|<
name|Event
argument_list|>
name|getEventHandler
parameter_list|()
block|{
class|class
name|EventArgMatcher
implements|implements
name|ArgumentMatcher
argument_list|<
name|AbstractEvent
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|AbstractEvent
name|argument
parameter_list|)
block|{
if|if
condition|(
name|argument
operator|instanceof
name|RMAppNodeUpdateEvent
condition|)
block|{
name|ApplicationId
name|appid
init|=
operator|(
operator|(
name|RMAppNodeUpdateEvent
operator|)
name|argument
operator|)
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|applist
operator|.
name|add
argument_list|(
name|appid
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
name|EventHandler
name|handler
init|=
name|spy
argument_list|(
name|super
operator|.
name|getEventHandler
argument_list|()
argument_list|)
decl_stmt|;
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|handler
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|EventArgMatcher
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|handler
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

