begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.store.metrics
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
name|federation
operator|.
name|store
operator|.
name|metrics
package|;
end_package

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
comment|/**  * Unittests for {@link FederationStateStoreClientMetrics}.  *  */
end_comment

begin_class
DECL|class|TestFederationStateStoreClientMetrics
specifier|public
class|class
name|TestFederationStateStoreClientMetrics
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestFederationStateStoreClientMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|badStateStore
specifier|private
name|MockBadFederationStateStore
name|badStateStore
init|=
operator|new
name|MockBadFederationStateStore
argument_list|()
decl_stmt|;
DECL|field|goodStateStore
specifier|private
name|MockGoodFederationStateStore
name|goodStateStore
init|=
operator|new
name|MockGoodFederationStateStore
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testAggregateMetricInit ()
specifier|public
name|void
name|testAggregateMetricInit
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test: aggregate metrics are initialized correctly"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|FederationStateStoreClientMetrics
operator|.
name|getNumSucceededCalls
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|FederationStateStoreClientMetrics
operator|.
name|getNumFailedCalls
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test: aggregate metrics are updated correctly"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSuccessfulCalls ()
specifier|public
name|void
name|testSuccessfulCalls
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test: Aggregate and method successful calls updated correctly"
argument_list|)
expr_stmt|;
name|long
name|totalGoodBefore
init|=
name|FederationStateStoreClientMetrics
operator|.
name|getNumSucceededCalls
argument_list|()
decl_stmt|;
name|long
name|apiGoodBefore
init|=
name|FederationStateStoreClientMetrics
operator|.
name|getNumSucceessfulCallsForMethod
argument_list|(
literal|"registerSubCluster"
argument_list|)
decl_stmt|;
name|goodStateStore
operator|.
name|registerSubCluster
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|totalGoodBefore
operator|+
literal|1
argument_list|,
name|FederationStateStoreClientMetrics
operator|.
name|getNumSucceededCalls
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|FederationStateStoreClientMetrics
operator|.
name|getLatencySucceededCalls
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|apiGoodBefore
operator|+
literal|1
argument_list|,
name|FederationStateStoreClientMetrics
operator|.
name|getNumSucceededCalls
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|FederationStateStoreClientMetrics
operator|.
name|getLatencySucceessfulCallsForMethod
argument_list|(
literal|"registerSubCluster"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test: Running stats correctly calculated for 2 metrics"
argument_list|)
expr_stmt|;
name|goodStateStore
operator|.
name|registerSubCluster
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|totalGoodBefore
operator|+
literal|2
argument_list|,
name|FederationStateStoreClientMetrics
operator|.
name|getNumSucceededCalls
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|150
argument_list|,
name|FederationStateStoreClientMetrics
operator|.
name|getLatencySucceededCalls
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|apiGoodBefore
operator|+
literal|2
argument_list|,
name|FederationStateStoreClientMetrics
operator|.
name|getNumSucceededCalls
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|150
argument_list|,
name|FederationStateStoreClientMetrics
operator|.
name|getLatencySucceessfulCallsForMethod
argument_list|(
literal|"registerSubCluster"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailedCalls ()
specifier|public
name|void
name|testFailedCalls
parameter_list|()
block|{
name|long
name|totalBadbefore
init|=
name|FederationStateStoreClientMetrics
operator|.
name|getNumFailedCalls
argument_list|()
decl_stmt|;
name|long
name|apiBadBefore
init|=
name|FederationStateStoreClientMetrics
operator|.
name|getNumFailedCallsForMethod
argument_list|(
literal|"registerSubCluster"
argument_list|)
decl_stmt|;
name|badStateStore
operator|.
name|registerSubCluster
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test: Aggregate and method failed calls updated correctly"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|totalBadbefore
operator|+
literal|1
argument_list|,
name|FederationStateStoreClientMetrics
operator|.
name|getNumFailedCalls
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|apiBadBefore
operator|+
literal|1
argument_list|,
name|FederationStateStoreClientMetrics
operator|.
name|getNumFailedCallsForMethod
argument_list|(
literal|"registerSubCluster"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCallsUnknownMethod ()
specifier|public
name|void
name|testCallsUnknownMethod
parameter_list|()
block|{
name|long
name|totalBadbefore
init|=
name|FederationStateStoreClientMetrics
operator|.
name|getNumFailedCalls
argument_list|()
decl_stmt|;
name|long
name|apiBadBefore
init|=
name|FederationStateStoreClientMetrics
operator|.
name|getNumFailedCallsForMethod
argument_list|(
literal|"registerSubCluster"
argument_list|)
decl_stmt|;
name|long
name|totalGoodBefore
init|=
name|FederationStateStoreClientMetrics
operator|.
name|getNumSucceededCalls
argument_list|()
decl_stmt|;
name|long
name|apiGoodBefore
init|=
name|FederationStateStoreClientMetrics
operator|.
name|getNumSucceessfulCallsForMethod
argument_list|(
literal|"registerSubCluster"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Calling Metrics class directly"
argument_list|)
expr_stmt|;
name|FederationStateStoreClientMetrics
operator|.
name|failedStateStoreCall
argument_list|()
expr_stmt|;
name|FederationStateStoreClientMetrics
operator|.
name|succeededStateStoreCall
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test: Aggregate and method calls did not update"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|totalBadbefore
argument_list|,
name|FederationStateStoreClientMetrics
operator|.
name|getNumFailedCalls
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|apiBadBefore
argument_list|,
name|FederationStateStoreClientMetrics
operator|.
name|getNumFailedCallsForMethod
argument_list|(
literal|"registerSubCluster"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|totalGoodBefore
argument_list|,
name|FederationStateStoreClientMetrics
operator|.
name|getNumSucceededCalls
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|apiGoodBefore
argument_list|,
name|FederationStateStoreClientMetrics
operator|.
name|getNumSucceessfulCallsForMethod
argument_list|(
literal|"registerSubCluster"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Records failures for all calls
DECL|class|MockBadFederationStateStore
specifier|private
class|class
name|MockBadFederationStateStore
block|{
DECL|method|registerSubCluster ()
specifier|public
name|void
name|registerSubCluster
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Mocked: failed registerSubCluster call"
argument_list|)
expr_stmt|;
name|FederationStateStoreClientMetrics
operator|.
name|failedStateStoreCall
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Records successes for all calls
DECL|class|MockGoodFederationStateStore
specifier|private
class|class
name|MockGoodFederationStateStore
block|{
DECL|method|registerSubCluster (long duration)
specifier|public
name|void
name|registerSubCluster
parameter_list|(
name|long
name|duration
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Mocked: successful registerSubCluster call with duration {}"
argument_list|,
name|duration
argument_list|)
expr_stmt|;
name|FederationStateStoreClientMetrics
operator|.
name|succeededStateStoreCall
argument_list|(
name|duration
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

