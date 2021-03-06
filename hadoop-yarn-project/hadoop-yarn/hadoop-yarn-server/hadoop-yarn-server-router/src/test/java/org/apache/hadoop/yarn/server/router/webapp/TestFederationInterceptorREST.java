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
name|api
operator|.
name|records
operator|.
name|ResourceOption
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
name|AppInfo
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
name|AppState
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
name|ClusterMetricsInfo
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
name|resourcemanager
operator|.
name|webapp
operator|.
name|dao
operator|.
name|NodeInfo
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
name|NodesInfo
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
name|ResourceInfo
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
name|ResourceOptionInfo
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
comment|/**  * Extends the {@code BaseRouterClientRMTest} and overrides methods in order to  * use the {@code RouterClientRMService} pipeline test cases for testing the  * {@code FederationInterceptor} class. The tests for  * {@code RouterClientRMService} has been written cleverly so that it can be  * reused to validate different request intercepter chains.  */
end_comment

begin_class
DECL|class|TestFederationInterceptorREST
specifier|public
class|class
name|TestFederationInterceptorREST
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
name|TestFederationInterceptorREST
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NUM_SUBCLUSTER
specifier|private
specifier|final
specifier|static
name|int
name|NUM_SUBCLUSTER
init|=
literal|4
decl_stmt|;
DECL|field|BAD_REQUEST
specifier|private
specifier|static
specifier|final
name|int
name|BAD_REQUEST
init|=
literal|400
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
DECL|field|user
specifier|private
specifier|static
name|String
name|user
init|=
literal|"test-user"
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
DECL|field|subClusters
specifier|private
name|List
argument_list|<
name|SubClusterId
argument_list|>
name|subClusters
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
name|this
operator|.
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
name|subClusters
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
try|try
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
name|NUM_SUBCLUSTER
condition|;
name|i
operator|++
control|)
block|{
name|SubClusterId
name|sc
init|=
name|SubClusterId
operator|.
name|newInstance
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|stateStoreUtil
operator|.
name|registerSubCluster
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|subClusters
operator|.
name|add
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
name|PassThroughRESTRequestInterceptor
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
name|TestableFederationInterceptorREST
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
comment|/**    * This test validates the correctness of GetNewApplication. The return    * ApplicationId has to belong to one of the SubCluster in the cluster.    */
annotation|@
name|Test
DECL|method|testGetNewApplication ()
specifier|public
name|void
name|testGetNewApplication
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
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
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|NewApplication
name|ci
init|=
operator|(
name|NewApplication
operator|)
name|response
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|ci
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|fromString
argument_list|(
name|ci
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|appId
operator|.
name|getClusterTimestamp
argument_list|()
operator|<
name|NUM_SUBCLUSTER
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|appId
operator|.
name|getClusterTimestamp
argument_list|()
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of SubmitApplication. The application    * has to be submitted to one of the SubCluster in the cluster.    */
annotation|@
name|Test
DECL|method|testSubmitApplication ()
specifier|public
name|void
name|testSubmitApplication
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
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
name|SubClusterId
name|ci
init|=
operator|(
name|SubClusterId
operator|)
name|response
operator|.
name|getEntity
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|SubClusterId
name|scIdResult
init|=
name|stateStoreUtil
operator|.
name|queryApplicationHomeSC
argument_list|(
name|appId
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|scIdResult
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|subClusters
operator|.
name|contains
argument_list|(
name|scIdResult
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ci
argument_list|,
name|scIdResult
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of SubmitApplication in case of    * multiple submission. The first retry has to be submitted to the same    * SubCluster of the first attempt.    */
annotation|@
name|Test
DECL|method|testSubmitApplicationMultipleSubmission ()
specifier|public
name|void
name|testSubmitApplicationMultipleSubmission
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
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
comment|// First attempt
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
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
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
name|SubClusterId
name|scIdResult
init|=
name|stateStoreUtil
operator|.
name|queryApplicationHomeSC
argument_list|(
name|appId
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|scIdResult
argument_list|)
expr_stmt|;
comment|// First retry
name|response
operator|=
name|interceptor
operator|.
name|submitApplication
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
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
name|ACCEPTED
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|SubClusterId
name|scIdResult2
init|=
name|stateStoreUtil
operator|.
name|queryApplicationHomeSC
argument_list|(
name|appId
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|scIdResult2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|scIdResult
argument_list|,
name|scIdResult2
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of SubmitApplication in case of empty    * request.    */
annotation|@
name|Test
DECL|method|testSubmitApplicationEmptyRequest ()
specifier|public
name|void
name|testSubmitApplicationEmptyRequest
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
comment|// ApplicationSubmissionContextInfo null
name|Response
name|response
init|=
name|interceptor
operator|.
name|submitApplication
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|BAD_REQUEST
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
comment|// ApplicationSubmissionContextInfo empty
name|response
operator|=
name|interceptor
operator|.
name|submitApplication
argument_list|(
operator|new
name|ApplicationSubmissionContextInfo
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|BAD_REQUEST
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|ApplicationSubmissionContextInfo
name|context
init|=
operator|new
name|ApplicationSubmissionContextInfo
argument_list|()
decl_stmt|;
name|response
operator|=
name|interceptor
operator|.
name|submitApplication
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|BAD_REQUEST
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of SubmitApplication in case of of    * application in wrong format.    */
annotation|@
name|Test
DECL|method|testSubmitApplicationWrongFormat ()
specifier|public
name|void
name|testSubmitApplicationWrongFormat
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
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
literal|"Application_wrong_id"
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
name|BAD_REQUEST
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of ForceKillApplication in case the    * application exists in the cluster.    */
annotation|@
name|Test
DECL|method|testForceKillApplication ()
specifier|public
name|void
name|testForceKillApplication
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
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
comment|// Submit the application we are going to kill later
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
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|stateStoreUtil
operator|.
name|queryApplicationHomeSC
argument_list|(
name|appId
argument_list|)
argument_list|)
expr_stmt|;
name|AppState
name|appState
init|=
operator|new
name|AppState
argument_list|(
literal|"KILLED"
argument_list|)
decl_stmt|;
name|Response
name|responseKill
init|=
name|interceptor
operator|.
name|updateAppState
argument_list|(
name|appState
argument_list|,
literal|null
argument_list|,
name|appId
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|responseKill
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of ForceKillApplication in case of    * application does not exist in StateStore.    */
annotation|@
name|Test
DECL|method|testForceKillApplicationNotExists ()
specifier|public
name|void
name|testForceKillApplicationNotExists
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
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
name|AppState
name|appState
init|=
operator|new
name|AppState
argument_list|(
literal|"KILLED"
argument_list|)
decl_stmt|;
name|Response
name|response
init|=
name|interceptor
operator|.
name|updateAppState
argument_list|(
name|appState
argument_list|,
literal|null
argument_list|,
name|appId
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|BAD_REQUEST
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of ForceKillApplication in case of    * application in wrong format.    */
annotation|@
name|Test
DECL|method|testForceKillApplicationWrongFormat ()
specifier|public
name|void
name|testForceKillApplicationWrongFormat
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|AppState
name|appState
init|=
operator|new
name|AppState
argument_list|(
literal|"KILLED"
argument_list|)
decl_stmt|;
name|Response
name|response
init|=
name|interceptor
operator|.
name|updateAppState
argument_list|(
name|appState
argument_list|,
literal|null
argument_list|,
literal|"Application_wrong_id"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|BAD_REQUEST
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of ForceKillApplication in case of    * empty request.    */
annotation|@
name|Test
DECL|method|testForceKillApplicationEmptyRequest ()
specifier|public
name|void
name|testForceKillApplicationEmptyRequest
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
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
comment|// Submit the application we are going to kill later
name|interceptor
operator|.
name|submitApplication
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Response
name|response
init|=
name|interceptor
operator|.
name|updateAppState
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|appId
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|BAD_REQUEST
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of GetApplicationReport in case the    * application exists in the cluster.    */
annotation|@
name|Test
DECL|method|testGetApplicationReport ()
specifier|public
name|void
name|testGetApplicationReport
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
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
comment|// Submit the application we want the report later
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
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|stateStoreUtil
operator|.
name|queryApplicationHomeSC
argument_list|(
name|appId
argument_list|)
argument_list|)
expr_stmt|;
name|AppInfo
name|responseGet
init|=
name|interceptor
operator|.
name|getApp
argument_list|(
literal|null
argument_list|,
name|appId
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|responseGet
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of GetApplicationReport in case the    * application does not exist in StateStore.    */
annotation|@
name|Test
DECL|method|testGetApplicationNotExists ()
specifier|public
name|void
name|testGetApplicationNotExists
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
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
name|AppInfo
name|response
init|=
name|interceptor
operator|.
name|getApp
argument_list|(
literal|null
argument_list|,
name|appId
operator|.
name|toString
argument_list|()
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
comment|/**    * This test validates the correctness of GetApplicationReport in case of    * application in wrong format.    */
annotation|@
name|Test
DECL|method|testGetApplicationWrongFormat ()
specifier|public
name|void
name|testGetApplicationWrongFormat
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|AppInfo
name|response
init|=
name|interceptor
operator|.
name|getApp
argument_list|(
literal|null
argument_list|,
literal|"Application_wrong_id"
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
comment|/**    * This test validates the correctness of GetApplicationsReport in case each    * subcluster provided one application.    */
annotation|@
name|Test
DECL|method|testGetApplicationsReport ()
specifier|public
name|void
name|testGetApplicationsReport
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|AppsInfo
name|responseGet
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
name|responseGet
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NUM_SUBCLUSTER
argument_list|,
name|responseGet
operator|.
name|getApps
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// The merged operations is tested in TestRouterWebServiceUtil
block|}
comment|/**    * This test validates the correctness of GetNodes in case each subcluster    * provided one node with the LastHealthUpdate set to the SubClusterId. The    * expected result would be the NodeInfo from the last SubCluster that has    * LastHealthUpdate equal to Num_SubCluster -1.    */
annotation|@
name|Test
DECL|method|testGetNode ()
specifier|public
name|void
name|testGetNode
parameter_list|()
block|{
name|NodeInfo
name|responseGet
init|=
name|interceptor
operator|.
name|getNode
argument_list|(
literal|"testGetNode"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|responseGet
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NUM_SUBCLUSTER
operator|-
literal|1
argument_list|,
name|responseGet
operator|.
name|getLastHealthUpdate
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of GetNodes in case each subcluster    * provided one node.    */
annotation|@
name|Test
DECL|method|testGetNodes ()
specifier|public
name|void
name|testGetNodes
parameter_list|()
block|{
name|NodesInfo
name|responseGet
init|=
name|interceptor
operator|.
name|getNodes
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|responseGet
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NUM_SUBCLUSTER
argument_list|,
name|responseGet
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// The remove duplicate operations is tested in TestRouterWebServiceUtil
block|}
comment|/**    * This test validates the correctness of updateNodeResource().    */
annotation|@
name|Test
DECL|method|testUpdateNodeResource ()
specifier|public
name|void
name|testUpdateNodeResource
parameter_list|()
block|{
name|List
argument_list|<
name|NodeInfo
argument_list|>
name|nodes
init|=
name|interceptor
operator|.
name|getNodes
argument_list|(
literal|null
argument_list|)
operator|.
name|getNodes
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|nodes
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|nodeId
init|=
name|nodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeId
argument_list|()
decl_stmt|;
name|ResourceOptionInfo
name|resourceOption
init|=
operator|new
name|ResourceOptionInfo
argument_list|(
name|ResourceOption
operator|.
name|newInstance
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|2048
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|1000
argument_list|)
argument_list|)
decl_stmt|;
name|ResourceInfo
name|resource
init|=
name|interceptor
operator|.
name|updateNodeResource
argument_list|(
literal|null
argument_list|,
name|nodeId
argument_list|,
name|resourceOption
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2048
argument_list|,
name|resource
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|resource
operator|.
name|getvCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of getClusterMetricsInfo in case each    * SubCluster provided a ClusterMetricsInfo with appsSubmitted set to the    * SubClusterId. The expected result would be appSubmitted equals to the sum    * of SubClusterId. SubClusterId in this case is an integer.    */
annotation|@
name|Test
DECL|method|testGetClusterMetrics ()
specifier|public
name|void
name|testGetClusterMetrics
parameter_list|()
block|{
name|ClusterMetricsInfo
name|responseGet
init|=
name|interceptor
operator|.
name|getClusterMetricsInfo
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|responseGet
argument_list|)
expr_stmt|;
name|int
name|expectedAppSubmitted
init|=
literal|0
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
name|NUM_SUBCLUSTER
condition|;
name|i
operator|++
control|)
block|{
name|expectedAppSubmitted
operator|+=
name|i
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedAppSubmitted
argument_list|,
name|responseGet
operator|.
name|getAppsSubmitted
argument_list|()
argument_list|)
expr_stmt|;
comment|// The merge operations is tested in TestRouterWebServiceUtil
block|}
comment|/**    * This test validates the correctness of GetApplicationState in case the    * application exists in the cluster.    */
annotation|@
name|Test
DECL|method|testGetApplicationState ()
specifier|public
name|void
name|testGetApplicationState
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
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
comment|// Submit the application we want the report later
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
name|assertNotNull
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|stateStoreUtil
operator|.
name|queryApplicationHomeSC
argument_list|(
name|appId
argument_list|)
argument_list|)
expr_stmt|;
name|AppState
name|responseGet
init|=
name|interceptor
operator|.
name|getAppState
argument_list|(
literal|null
argument_list|,
name|appId
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|responseGet
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|MockDefaultRequestInterceptorREST
operator|.
name|APP_STATE_RUNNING
argument_list|,
name|responseGet
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test validates the correctness of GetApplicationState in case the    * application does not exist in StateStore.    */
annotation|@
name|Test
DECL|method|testGetApplicationStateNotExists ()
specifier|public
name|void
name|testGetApplicationStateNotExists
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
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
name|AppState
name|response
init|=
name|interceptor
operator|.
name|getAppState
argument_list|(
literal|null
argument_list|,
name|appId
operator|.
name|toString
argument_list|()
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
comment|/**    * This test validates the correctness of GetApplicationState in case of    * application in wrong format.    */
annotation|@
name|Test
DECL|method|testGetApplicationStateWrongFormat ()
specifier|public
name|void
name|testGetApplicationStateWrongFormat
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|AppState
name|response
init|=
name|interceptor
operator|.
name|getAppState
argument_list|(
literal|null
argument_list|,
literal|"Application_wrong_id"
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
block|}
end_class

end_unit

