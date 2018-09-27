begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.collector
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
name|timelineservice
operator|.
name|collector
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
name|server
operator|.
name|timelineservice
operator|.
name|metrics
operator|.
name|PerNodeAggTimelineCollectorMetrics
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
name|Assert
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
comment|/**  * Test PerNodeAggTimelineCollectorMetrics.  */
end_comment

begin_class
DECL|class|TestPerNodeAggTimelineCollectorMetrics
specifier|public
class|class
name|TestPerNodeAggTimelineCollectorMetrics
block|{
DECL|field|metrics
specifier|private
name|PerNodeAggTimelineCollectorMetrics
name|metrics
decl_stmt|;
annotation|@
name|Test
DECL|method|testTimelineCollectorMetrics ()
specifier|public
name|void
name|testTimelineCollectorMetrics
parameter_list|()
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|metrics
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|metrics
operator|.
name|getPutEntitiesSuccessLatency
argument_list|()
operator|.
name|getInterval
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|metrics
operator|.
name|getPutEntitiesFailureLatency
argument_list|()
operator|.
name|getInterval
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|metrics
operator|.
name|getAsyncPutEntitiesSuccessLatency
argument_list|()
operator|.
name|getInterval
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|metrics
operator|.
name|getAsyncPutEntitiesFailureLatency
argument_list|()
operator|.
name|getInterval
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|metrics
operator|=
name|PerNodeAggTimelineCollectorMetrics
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|PerNodeAggTimelineCollectorMetrics
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

