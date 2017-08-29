begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.router.webapp
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
name|router
operator|.
name|webapp
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
name|Arrays
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
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
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
name|server
operator|.
name|federation
operator|.
name|policies
operator|.
name|FederationPolicyUtils
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
name|federation
operator|.
name|policies
operator|.
name|manager
operator|.
name|UniformBroadcastPolicyManager
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
name|federation
operator|.
name|store
operator|.
name|impl
operator|.
name|MemoryFederationStateStore
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
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|GetApplicationHomeSubClusterRequest
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
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|SubClusterId
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
name|federation
operator|.
name|utils
operator|.
name|FederationStateStoreFacade
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
name|federation
operator|.
name|utils
operator|.
name|FederationStateStoreTestUtil
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
name|webapp
operator|.
name|dao
operator|.
name|ApplicationSubmissionContextInfo
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
name|webapp
operator|.
name|dao
operator|.
name|AppsInfo
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
name|webapp
operator|.
name|dao
operator|.
name|NewApplication
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
name|router
operator|.
name|clientrm
operator|.
name|PassThroughClientRequestInterceptor
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
name|router
operator|.
name|clientrm
operator|.
name|TestableFederationClientInterceptor
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
comment|/**  * Extends the {@code BaseRouterWebServicesTest} and overrides methods in order  * to use the {@code RouterWebServices} pipeline test cases for testing the  * {@code FederationInterceptorREST} class. The tests for  * {@code RouterWebServices} has been written cleverly so that it can be reused  * to validate different request interceptor chains.  *<p>  * It tests the case with SubClusters down and the Router logic of retries. We  * have 1 good SubCluster and 2 bad ones for all the tests.  */
end_comment

begin_class
DECL|class|TestFederationInterceptorRESTRetry
specifier|public
class|class
name|TestFederationInterceptorRESTRetry
extends|extends
name|BaseRouterWebServicesTest
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
name|TestFederationInterceptorRESTRetry
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SERVICE_UNAVAILABLE
specifier|private
specifier|static
specifier|final
name|int
name|SERVICE_UNAVAILABLE
init|=
literal|503
decl_stmt|;
DECL|field|ACCEPTED
specifier|private
specifier|static
specifier|final
name|int
name|ACCEPTED
init|=
literal|202
decl_stmt|;
DECL|field|OK
specifier|private
specifier|static
specifier|final
name|int
name|OK
init|=
literal|200
decl_stmt|;
comment|// running and registered
DECL|field|good
specifier|private
specifier|static
name|SubClusterId
name|good
decl_stmt|;
comment|// registered but not running
DECL|field|bad1
specifier|private
specifier|static
name|SubClusterId
name|bad1
decl_stmt|;
DECL|field|bad2
specifier|private
specifier|static
name|SubClusterId
name|bad2
decl_stmt|;
DECL|field|scs
specifier|private
specifier|static
name|List
argument_list|<
name|SubClusterId
argument_list|>
name|scs
init|=
operator|new
name|ArrayList
argument_list|<
name|SubClusterId
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|interceptor
specifier|private
name|TestableFederationInterceptorREST
name|interceptor
decl_stmt|;
DECL|field|stateStore
specifier|private
name|MemoryFederationStateStore
name|stateStore
decl_stmt|;
DECL|field|stateStoreUtil
specifier|private
name|FederationStateStoreTestUtil
name|stateStoreUtil
decl_stmt|;
DECL|field|user
specifier|private
name|String
name|user
init|=
literal|"test-user"
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|super
operator|.
name|setUpConfig
argument_list|()
expr_stmt|;
name|interceptor
operator|=
operator|new
name|TestableFederationInterceptorREST
argument_list|()
expr_stmt|;
name|stateStore
operator|=
operator|new
name|MemoryFederationStateStore
argument_list|()
expr_stmt|;
name|stateStore
operator|.
name|init
argument_list|(
name|this
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|FederationStateStoreFacade
operator|.
name|getInstance
argument_list|()
operator|.
name|reinitialize
argument_list|(
name|stateStore
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|stateStoreUtil
operator|=
operator|new
name|FederationStateStoreTestUtil
argument_list|(
name|stateStore
argument_list|)
expr_stmt|;
name|interceptor
operator|.
name|setConf
argument_list|(
name|this
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|interceptor
operator|.
name|init
argument_list|(
name|user
argument_list|)
expr_stmt|;
comment|// Create SubClusters
name|good
operator|=
name|SubClusterId
operator|.
name|newInstance
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
name|bad1
operator|=
name|SubClusterId
operator|.
name|newInstance
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|bad2
operator|=
name|SubClusterId
operator|.
name|newInstance
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|scs
operator|.
name|add
argument_list|(
name|good
argument_list|)
expr_stmt|;
name|scs
operator|.
name|add
argument_list|(
name|bad1
argument_list|)
expr_stmt|;
name|scs
operator|.
name|add
argument_list|(
name|bad2
argument_list|)
expr_stmt|;
comment|// The mock RM will not start in these SubClusters, this is done to simulate
comment|// a SubCluster down
name|interceptor
operator|.
name|registerBadSubCluster
argument_list|(
name|bad1
argument_list|)
expr_stmt|;
name|interceptor
operator|.
name|registerBadSubCluster
argument_list|(
name|bad2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|interceptor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|setupCluster (List<SubClusterId> scsToRegister)
specifier|private
name|void
name|setupCluster
parameter_list|(
name|List
argument_list|<
name|SubClusterId
argument_list|>
name|scsToRegister
parameter_list|)
throws|throws
name|YarnException
block|{
try|try
block|{
comment|// Clean up the StateStore before every test
name|stateStoreUtil
operator|.
name|deregisterAllSubClusters
argument_list|()
expr_stmt|;
for|for
control|(
name|SubClusterId
name|sc
range|:
name|scsToRegister
control|)
block|{
name|stateStoreUtil
operator|.
name|registerSubCluster
argument_list|(
name|sc
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|createConfiguration ()
specifier|protected
name|YarnConfiguration
name|createConfiguration
parameter_list|()
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_ENABLED
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
name|ROUTER_WEBAPP_DEFAULT_INTERCEPTOR_CLASS
argument_list|,
name|MockDefaultRequestInterceptorREST
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|mockPassThroughInterceptorClass
init|=
name|PassThroughClientRequestInterceptor
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// Create a request intercepter pipeline for testing. The last one in the
comment|// chain is the federation intercepter that calls the mock resource manager.
comment|// The others in the chain will simply forward it to the next one in the
comment|// chain
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|ROUTER_CLIENTRM_INTERCEPTOR_CLASS_PIPELINE
argument_list|,
name|mockPassThroughInterceptorClass
operator|+
literal|","
operator|+
name|TestableFederationClientInterceptor
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_POLICY_MANAGER
argument_list|,
name|UniformBroadcastPolicyManager
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Disable StateStoreFacade cache
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_CACHE_TIME_TO_LIVE_SECS
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
comment|/**    * This test validates the correctness of GetNewApplication in case the    * cluster is composed of only 1 bad SubCluster.    */
annotation|@
name|Test
DECL|method|testGetNewApplicationOneBadSC ()
specifier|public
name|void
name|testGetNewApplicationOneBadSC
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|setupCluster
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|bad2
argument_list|)
argument_list|)
expr_stmt|;
name|Response
name|response
init|=
name|interceptor
operator|.
name|createNewApplication
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|SERVICE_UNAVAILABLE
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|FederationPolicyUtils
operator|.
name|NO_ACTIVE_SUBCLUSTER_AVAILABLE
argument_list|,
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of GetNewApplication in case the    * cluster is composed of only 2 bad SubClusters.    */
annotation|@
name|Test
DECL|method|testGetNewApplicationTwoBadSCs ()
specifier|public
name|void
name|testGetNewApplicationTwoBadSCs
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|setupCluster
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|bad1
argument_list|,
name|bad2
argument_list|)
argument_list|)
expr_stmt|;
name|Response
name|response
init|=
name|interceptor
operator|.
name|createNewApplication
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|SERVICE_UNAVAILABLE
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|FederationPolicyUtils
operator|.
name|NO_ACTIVE_SUBCLUSTER_AVAILABLE
argument_list|,
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of GetNewApplication in case the    * cluster is composed of only 1 bad SubCluster and 1 good one.    */
annotation|@
name|Test
DECL|method|testGetNewApplicationOneBadOneGood ()
specifier|public
name|void
name|testGetNewApplicationOneBadOneGood
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test getNewApplication with one bad, one good SC"
argument_list|)
expr_stmt|;
name|setupCluster
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|good
argument_list|,
name|bad2
argument_list|)
argument_list|)
expr_stmt|;
name|Response
name|response
init|=
name|interceptor
operator|.
name|createNewApplication
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|OK
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|NewApplication
name|newApp
init|=
operator|(
name|NewApplication
operator|)
name|response
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|fromString
argument_list|(
name|newApp
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|good
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|appId
operator|.
name|getClusterTimestamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of SubmitApplication in case the    * cluster is composed of only 1 bad SubCluster.    */
annotation|@
name|Test
DECL|method|testSubmitApplicationOneBadSC ()
specifier|public
name|void
name|testSubmitApplicationOneBadSC
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|setupCluster
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|bad2
argument_list|)
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationSubmissionContextInfo
name|context
init|=
operator|new
name|ApplicationSubmissionContextInfo
argument_list|()
decl_stmt|;
name|context
operator|.
name|setApplicationId
argument_list|(
name|appId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Response
name|response
init|=
name|interceptor
operator|.
name|submitApplication
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|SERVICE_UNAVAILABLE
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|FederationPolicyUtils
operator|.
name|NO_ACTIVE_SUBCLUSTER_AVAILABLE
argument_list|,
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of SubmitApplication in case the    * cluster is composed of only 2 bad SubClusters.    */
annotation|@
name|Test
DECL|method|testSubmitApplicationTwoBadSCs ()
specifier|public
name|void
name|testSubmitApplicationTwoBadSCs
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|setupCluster
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|bad1
argument_list|,
name|bad2
argument_list|)
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationSubmissionContextInfo
name|context
init|=
operator|new
name|ApplicationSubmissionContextInfo
argument_list|()
decl_stmt|;
name|context
operator|.
name|setApplicationId
argument_list|(
name|appId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Response
name|response
init|=
name|interceptor
operator|.
name|submitApplication
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|SERVICE_UNAVAILABLE
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|FederationPolicyUtils
operator|.
name|NO_ACTIVE_SUBCLUSTER_AVAILABLE
argument_list|,
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of SubmitApplication in case the    * cluster is composed of only 1 bad SubCluster and a good one.    */
annotation|@
name|Test
DECL|method|testSubmitApplicationOneBadOneGood ()
specifier|public
name|void
name|testSubmitApplicationOneBadOneGood
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test submitApplication with one bad, one good SC"
argument_list|)
expr_stmt|;
name|setupCluster
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|good
argument_list|,
name|bad2
argument_list|)
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationSubmissionContextInfo
name|context
init|=
operator|new
name|ApplicationSubmissionContextInfo
argument_list|()
decl_stmt|;
name|context
operator|.
name|setApplicationId
argument_list|(
name|appId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Response
name|response
init|=
name|interceptor
operator|.
name|submitApplication
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ACCEPTED
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|good
argument_list|,
name|stateStore
operator|.
name|getApplicationHomeSubCluster
argument_list|(
name|GetApplicationHomeSubClusterRequest
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|)
argument_list|)
operator|.
name|getApplicationHomeSubCluster
argument_list|()
operator|.
name|getHomeSubCluster
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of GetApps in case the cluster is    * composed of only 1 bad SubCluster.    */
annotation|@
name|Test
DECL|method|testGetAppsOneBadSC ()
specifier|public
name|void
name|testGetAppsOneBadSC
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|setupCluster
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|bad2
argument_list|)
argument_list|)
expr_stmt|;
name|AppsInfo
name|response
init|=
name|interceptor
operator|.
name|getApps
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of GetApps in case the cluster is    * composed of only 2 bad SubClusters.    */
annotation|@
name|Test
DECL|method|testGetAppsTwoBadSCs ()
specifier|public
name|void
name|testGetAppsTwoBadSCs
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|setupCluster
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|bad1
argument_list|,
name|bad2
argument_list|)
argument_list|)
expr_stmt|;
name|AppsInfo
name|response
init|=
name|interceptor
operator|.
name|getApps
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of GetApps in case the cluster is    * composed of only 1 bad SubCluster and a good one.    */
annotation|@
name|Test
DECL|method|testGetAppsOneBadOneGood ()
specifier|public
name|void
name|testGetAppsOneBadOneGood
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|setupCluster
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|good
argument_list|,
name|bad2
argument_list|)
argument_list|)
expr_stmt|;
name|AppsInfo
name|response
init|=
name|interceptor
operator|.
name|getApps
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|response
operator|.
name|getApps
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

