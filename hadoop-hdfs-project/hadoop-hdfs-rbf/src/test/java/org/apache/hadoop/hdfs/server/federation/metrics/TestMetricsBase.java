begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|metrics
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|FederationTestUtils
operator|.
name|NAMENODES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|FederationTestUtils
operator|.
name|NAMESERVICES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|FederationTestUtils
operator|.
name|ROUTERS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|FederationStateStoreTestUtils
operator|.
name|clearAllRecords
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|FederationStateStoreTestUtils
operator|.
name|createMockMountTable
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|FederationStateStoreTestUtils
operator|.
name|createMockRegistrationForNamenode
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|FederationStateStoreTestUtils
operator|.
name|synchronizeRecords
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|FederationStateStoreTestUtils
operator|.
name|waitStateStore
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|RouterConfigBuilder
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|FederationNamenodeServiceState
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|MembershipNamenodeResolver
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|router
operator|.
name|Router
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|router
operator|.
name|RouterServiceState
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|MembershipStore
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|RouterStore
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|StateStoreService
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|GetRouterRegistrationRequest
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|GetRouterRegistrationResponse
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|NamenodeHeartbeatRequest
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|NamenodeHeartbeatResponse
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|RouterHeartbeatRequest
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|MembershipState
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|MountTable
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|RouterState
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|StateStoreVersion
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
name|Time
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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

begin_comment
comment|/**  * Test the basic metrics functionality.  */
end_comment

begin_class
DECL|class|TestMetricsBase
specifier|public
class|class
name|TestMetricsBase
block|{
DECL|field|stateStore
specifier|private
name|StateStoreService
name|stateStore
decl_stmt|;
DECL|field|membershipStore
specifier|private
name|MembershipStore
name|membershipStore
decl_stmt|;
DECL|field|routerStore
specifier|private
name|RouterStore
name|routerStore
decl_stmt|;
DECL|field|router
specifier|private
name|Router
name|router
decl_stmt|;
DECL|field|routerConfig
specifier|private
name|Configuration
name|routerConfig
decl_stmt|;
DECL|field|activeMemberships
specifier|private
name|List
argument_list|<
name|MembershipState
argument_list|>
name|activeMemberships
decl_stmt|;
DECL|field|standbyMemberships
specifier|private
name|List
argument_list|<
name|MembershipState
argument_list|>
name|standbyMemberships
decl_stmt|;
DECL|field|mockMountTable
specifier|private
name|List
argument_list|<
name|MountTable
argument_list|>
name|mockMountTable
decl_stmt|;
DECL|field|mockRouters
specifier|private
name|List
argument_list|<
name|RouterState
argument_list|>
name|mockRouters
decl_stmt|;
DECL|field|nameservices
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|nameservices
decl_stmt|;
annotation|@
name|Before
DECL|method|setupBase ()
specifier|public
name|void
name|setupBase
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|router
operator|==
literal|null
condition|)
block|{
name|routerConfig
operator|=
operator|new
name|RouterConfigBuilder
argument_list|()
operator|.
name|stateStore
argument_list|()
operator|.
name|metrics
argument_list|()
operator|.
name|http
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
name|router
operator|=
operator|new
name|Router
argument_list|()
expr_stmt|;
name|router
operator|.
name|init
argument_list|(
name|routerConfig
argument_list|)
expr_stmt|;
name|router
operator|.
name|setRouterId
argument_list|(
literal|"routerId"
argument_list|)
expr_stmt|;
name|router
operator|.
name|start
argument_list|()
expr_stmt|;
name|stateStore
operator|=
name|router
operator|.
name|getStateStore
argument_list|()
expr_stmt|;
name|membershipStore
operator|=
name|stateStore
operator|.
name|getRegisteredRecordStore
argument_list|(
name|MembershipStore
operator|.
name|class
argument_list|)
expr_stmt|;
name|routerStore
operator|=
name|stateStore
operator|.
name|getRegisteredRecordStore
argument_list|(
name|RouterStore
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Read all data and load all caches
name|waitStateStore
argument_list|(
name|stateStore
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|createFixtures
argument_list|()
expr_stmt|;
name|stateStore
operator|.
name|refreshCaches
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|After
DECL|method|tearDownBase ()
specifier|public
name|void
name|tearDownBase
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|router
operator|!=
literal|null
condition|)
block|{
name|router
operator|.
name|stop
argument_list|()
expr_stmt|;
name|router
operator|.
name|close
argument_list|()
expr_stmt|;
name|router
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|createFixtures ()
specifier|private
name|void
name|createFixtures
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Clear all records
name|clearAllRecords
argument_list|(
name|stateStore
argument_list|)
expr_stmt|;
name|nameservices
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|nameservices
operator|.
name|add
argument_list|(
name|NAMESERVICES
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|nameservices
operator|.
name|add
argument_list|(
name|NAMESERVICES
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
comment|// 2 NNs per NS
name|activeMemberships
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|standbyMemberships
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|nameservice
range|:
name|nameservices
control|)
block|{
name|MembershipState
name|namenode1
init|=
name|createMockRegistrationForNamenode
argument_list|(
name|nameservice
argument_list|,
name|NAMENODES
index|[
literal|0
index|]
argument_list|,
name|FederationNamenodeServiceState
operator|.
name|ACTIVE
argument_list|)
decl_stmt|;
name|NamenodeHeartbeatRequest
name|request1
init|=
name|NamenodeHeartbeatRequest
operator|.
name|newInstance
argument_list|(
name|namenode1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|membershipStore
operator|.
name|namenodeHeartbeat
argument_list|(
name|request1
argument_list|)
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
name|activeMemberships
operator|.
name|add
argument_list|(
name|namenode1
argument_list|)
expr_stmt|;
name|MembershipState
name|namenode2
init|=
name|createMockRegistrationForNamenode
argument_list|(
name|nameservice
argument_list|,
name|NAMENODES
index|[
literal|1
index|]
argument_list|,
name|FederationNamenodeServiceState
operator|.
name|STANDBY
argument_list|)
decl_stmt|;
name|NamenodeHeartbeatRequest
name|request2
init|=
name|NamenodeHeartbeatRequest
operator|.
name|newInstance
argument_list|(
name|namenode2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|membershipStore
operator|.
name|namenodeHeartbeat
argument_list|(
name|request2
argument_list|)
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
name|standbyMemberships
operator|.
name|add
argument_list|(
name|namenode2
argument_list|)
expr_stmt|;
block|}
comment|// Add 2 mount table memberships
name|mockMountTable
operator|=
name|createMockMountTable
argument_list|(
name|nameservices
argument_list|)
expr_stmt|;
name|synchronizeRecords
argument_list|(
name|stateStore
argument_list|,
name|mockMountTable
argument_list|,
name|MountTable
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Add 2 router memberships in addition to the running router.
name|long
name|t1
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|mockRouters
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|RouterState
name|router1
init|=
name|RouterState
operator|.
name|newInstance
argument_list|(
literal|"router1"
argument_list|,
name|t1
argument_list|,
name|RouterServiceState
operator|.
name|RUNNING
argument_list|)
decl_stmt|;
name|router1
operator|.
name|setStateStoreVersion
argument_list|(
name|StateStoreVersion
operator|.
name|newInstance
argument_list|(
name|t1
operator|-
literal|1000
argument_list|,
name|t1
operator|-
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
name|RouterHeartbeatRequest
name|heartbeatRequest
init|=
name|RouterHeartbeatRequest
operator|.
name|newInstance
argument_list|(
name|router1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|routerStore
operator|.
name|routerHeartbeat
argument_list|(
name|heartbeatRequest
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|GetRouterRegistrationRequest
name|getRequest
init|=
name|GetRouterRegistrationRequest
operator|.
name|newInstance
argument_list|(
literal|"router1"
argument_list|)
decl_stmt|;
name|GetRouterRegistrationResponse
name|getResponse
init|=
name|routerStore
operator|.
name|getRouterRegistration
argument_list|(
name|getRequest
argument_list|)
decl_stmt|;
name|RouterState
name|routerState1
init|=
name|getResponse
operator|.
name|getRouter
argument_list|()
decl_stmt|;
name|mockRouters
operator|.
name|add
argument_list|(
name|routerState1
argument_list|)
expr_stmt|;
name|long
name|t2
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|RouterState
name|router2
init|=
name|RouterState
operator|.
name|newInstance
argument_list|(
literal|"router2"
argument_list|,
name|t2
argument_list|,
name|RouterServiceState
operator|.
name|RUNNING
argument_list|)
decl_stmt|;
name|router2
operator|.
name|setStateStoreVersion
argument_list|(
name|StateStoreVersion
operator|.
name|newInstance
argument_list|(
name|t2
operator|-
literal|6000
argument_list|,
name|t2
operator|-
literal|7000
argument_list|)
argument_list|)
expr_stmt|;
name|heartbeatRequest
operator|.
name|setRouter
argument_list|(
name|router2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|routerStore
operator|.
name|routerHeartbeat
argument_list|(
name|heartbeatRequest
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|getRequest
operator|.
name|setRouterId
argument_list|(
literal|"router2"
argument_list|)
expr_stmt|;
name|getResponse
operator|=
name|routerStore
operator|.
name|getRouterRegistration
argument_list|(
name|getRequest
argument_list|)
expr_stmt|;
name|RouterState
name|routerState2
init|=
name|getResponse
operator|.
name|getRouter
argument_list|()
decl_stmt|;
name|mockRouters
operator|.
name|add
argument_list|(
name|routerState2
argument_list|)
expr_stmt|;
block|}
DECL|method|getRouter ()
specifier|protected
name|Router
name|getRouter
parameter_list|()
block|{
return|return
name|router
return|;
block|}
DECL|method|getMockMountTable ()
specifier|protected
name|List
argument_list|<
name|MountTable
argument_list|>
name|getMockMountTable
parameter_list|()
block|{
return|return
name|mockMountTable
return|;
block|}
DECL|method|getActiveMemberships ()
specifier|protected
name|List
argument_list|<
name|MembershipState
argument_list|>
name|getActiveMemberships
parameter_list|()
block|{
return|return
name|activeMemberships
return|;
block|}
DECL|method|getStandbyMemberships ()
specifier|protected
name|List
argument_list|<
name|MembershipState
argument_list|>
name|getStandbyMemberships
parameter_list|()
block|{
return|return
name|standbyMemberships
return|;
block|}
DECL|method|getNameservices ()
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|getNameservices
parameter_list|()
block|{
return|return
name|nameservices
return|;
block|}
DECL|method|getMockRouters ()
specifier|protected
name|List
argument_list|<
name|RouterState
argument_list|>
name|getMockRouters
parameter_list|()
block|{
return|return
name|mockRouters
return|;
block|}
DECL|method|getStateStore ()
specifier|protected
name|StateStoreService
name|getStateStore
parameter_list|()
block|{
return|return
name|stateStore
return|;
block|}
annotation|@
name|Test
DECL|method|testObserverMetrics ()
specifier|public
name|void
name|testObserverMetrics
parameter_list|()
throws|throws
name|Exception
block|{
name|mockObserver
argument_list|()
expr_stmt|;
name|RBFMetrics
name|metrics
init|=
name|router
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|String
name|jsonString
init|=
name|metrics
operator|.
name|getNameservices
argument_list|()
decl_stmt|;
name|JSONObject
name|jsonObject
init|=
operator|new
name|JSONObject
argument_list|(
name|jsonString
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|getNameserviceStateMap
argument_list|(
name|jsonObject
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Cannot find ns0 in: "
operator|+
name|jsonString
argument_list|,
name|map
operator|.
name|containsKey
argument_list|(
literal|"ns0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"OBSERVER"
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"ns0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getNameserviceStateMap ( JSONObject jsonObject)
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getNameserviceStateMap
parameter_list|(
name|JSONObject
name|jsonObject
parameter_list|)
throws|throws
name|JSONException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|?
argument_list|>
name|keys
init|=
name|jsonObject
operator|.
name|keys
argument_list|()
decl_stmt|;
while|while
condition|(
name|keys
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|keys
operator|.
name|next
argument_list|()
decl_stmt|;
name|JSONObject
name|json
init|=
name|jsonObject
operator|.
name|getJSONObject
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|String
name|nsId
init|=
name|json
operator|.
name|getString
argument_list|(
literal|"nameserviceId"
argument_list|)
decl_stmt|;
name|String
name|state
init|=
name|json
operator|.
name|getString
argument_list|(
literal|"state"
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|nsId
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
DECL|method|mockObserver ()
specifier|private
name|void
name|mockObserver
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|ns
init|=
literal|"ns0"
decl_stmt|;
name|String
name|nn
init|=
literal|"nn0"
decl_stmt|;
name|createRegistration
argument_list|(
name|ns
argument_list|,
name|nn
argument_list|,
name|ROUTERS
index|[
literal|1
index|]
argument_list|,
name|FederationNamenodeServiceState
operator|.
name|OBSERVER
argument_list|)
expr_stmt|;
comment|// Load data into cache and calculate quorum
name|assertTrue
argument_list|(
name|stateStore
operator|.
name|loadCache
argument_list|(
name|MembershipStore
operator|.
name|class
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|membershipStore
operator|.
name|loadCache
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|MembershipNamenodeResolver
name|resolver
init|=
operator|(
name|MembershipNamenodeResolver
operator|)
name|router
operator|.
name|getNamenodeResolver
argument_list|()
decl_stmt|;
name|resolver
operator|.
name|loadCache
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|createRegistration (String ns, String nn, String routerId, FederationNamenodeServiceState state)
specifier|private
name|MembershipState
name|createRegistration
parameter_list|(
name|String
name|ns
parameter_list|,
name|String
name|nn
parameter_list|,
name|String
name|routerId
parameter_list|,
name|FederationNamenodeServiceState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|MembershipState
name|record
init|=
name|MembershipState
operator|.
name|newInstance
argument_list|(
name|routerId
argument_list|,
name|ns
argument_list|,
name|nn
argument_list|,
literal|"testcluster"
argument_list|,
literal|"testblock-"
operator|+
name|ns
argument_list|,
literal|"testrpc-"
operator|+
name|ns
operator|+
name|nn
argument_list|,
literal|"testservice-"
operator|+
name|ns
operator|+
name|nn
argument_list|,
literal|"testlifeline-"
operator|+
name|ns
operator|+
name|nn
argument_list|,
literal|"http"
argument_list|,
literal|"testweb-"
operator|+
name|ns
operator|+
name|nn
argument_list|,
name|state
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|NamenodeHeartbeatRequest
name|request
init|=
name|NamenodeHeartbeatRequest
operator|.
name|newInstance
argument_list|(
name|record
argument_list|)
decl_stmt|;
name|NamenodeHeartbeatResponse
name|response
init|=
name|membershipStore
operator|.
name|namenodeHeartbeat
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|record
return|;
block|}
block|}
end_class

end_unit

