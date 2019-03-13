begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.documentstore.collection
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
name|documentstore
operator|.
name|collection
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
name|timelineservice
operator|.
name|TimelineEntityType
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
name|timelineservice
operator|.
name|TimelineMetric
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
name|timelineservice
operator|.
name|TimelineMetricOperation
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
name|timelineservice
operator|.
name|documentstore
operator|.
name|DocumentStoreTestUtils
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
name|timelineservice
operator|.
name|documentstore
operator|.
name|collection
operator|.
name|document
operator|.
name|entity
operator|.
name|TimelineEntityDocument
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
name|timelineservice
operator|.
name|documentstore
operator|.
name|collection
operator|.
name|document
operator|.
name|entity
operator|.
name|TimelineMetricSubDoc
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
name|timelineservice
operator|.
name|documentstore
operator|.
name|collection
operator|.
name|document
operator|.
name|flowactivity
operator|.
name|FlowActivityDocument
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
name|timelineservice
operator|.
name|documentstore
operator|.
name|collection
operator|.
name|document
operator|.
name|flowrun
operator|.
name|FlowRunDocument
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Timeline Entity Document merge and aggregation test.  */
end_comment

begin_class
DECL|class|TestDocumentOperations
specifier|public
class|class
name|TestDocumentOperations
block|{
DECL|field|MEMORY_ID
specifier|private
specifier|static
specifier|final
name|String
name|MEMORY_ID
init|=
literal|"MEMORY"
decl_stmt|;
DECL|field|FLOW_NAME
specifier|private
specifier|static
specifier|final
name|String
name|FLOW_NAME
init|=
literal|"DistributedShell"
decl_stmt|;
DECL|field|FLOW_VERSION
specifier|private
specifier|static
specifier|final
name|String
name|FLOW_VERSION
init|=
literal|"1"
decl_stmt|;
annotation|@
name|Test
DECL|method|testTimelineEntityDocMergeOperation ()
specifier|public
name|void
name|testTimelineEntityDocMergeOperation
parameter_list|()
throws|throws
name|IOException
block|{
name|TimelineEntityDocument
name|actualEntityDoc
init|=
operator|new
name|TimelineEntityDocument
argument_list|()
decl_stmt|;
name|TimelineEntityDocument
name|expectedEntityDoc
init|=
name|DocumentStoreTestUtils
operator|.
name|bakeTimelineEntityDoc
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|actualEntityDoc
operator|.
name|getInfo
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|actualEntityDoc
operator|.
name|getMetrics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|actualEntityDoc
operator|.
name|getEvents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|actualEntityDoc
operator|.
name|getConfigs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|actualEntityDoc
operator|.
name|getIsRelatedToEntities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|actualEntityDoc
operator|.
name|getRelatesToEntities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|actualEntityDoc
operator|.
name|merge
argument_list|(
name|expectedEntityDoc
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedEntityDoc
operator|.
name|getInfo
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|actualEntityDoc
operator|.
name|getInfo
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedEntityDoc
operator|.
name|getMetrics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|actualEntityDoc
operator|.
name|getMetrics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedEntityDoc
operator|.
name|getEvents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|actualEntityDoc
operator|.
name|getEvents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedEntityDoc
operator|.
name|getConfigs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|actualEntityDoc
operator|.
name|getConfigs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedEntityDoc
operator|.
name|getRelatesToEntities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|actualEntityDoc
operator|.
name|getIsRelatedToEntities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedEntityDoc
operator|.
name|getRelatesToEntities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|actualEntityDoc
operator|.
name|getRelatesToEntities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFlowActivityDocMergeOperation ()
specifier|public
name|void
name|testFlowActivityDocMergeOperation
parameter_list|()
throws|throws
name|IOException
block|{
name|FlowActivityDocument
name|actualFlowActivityDoc
init|=
operator|new
name|FlowActivityDocument
argument_list|()
decl_stmt|;
name|FlowActivityDocument
name|expectedFlowActivityDoc
init|=
name|DocumentStoreTestUtils
operator|.
name|bakeFlowActivityDoc
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|actualFlowActivityDoc
operator|.
name|getDayTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|actualFlowActivityDoc
operator|.
name|getFlowActivities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|actualFlowActivityDoc
operator|.
name|getFlowName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_FLOW_ACTIVITY
operator|.
name|toString
argument_list|()
argument_list|,
name|actualFlowActivityDoc
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|actualFlowActivityDoc
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|actualFlowActivityDoc
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|actualFlowActivityDoc
operator|.
name|merge
argument_list|(
name|expectedFlowActivityDoc
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowActivityDoc
operator|.
name|getDayTimestamp
argument_list|()
argument_list|,
name|actualFlowActivityDoc
operator|.
name|getDayTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowActivityDoc
operator|.
name|getFlowActivities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|actualFlowActivityDoc
operator|.
name|getFlowActivities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowActivityDoc
operator|.
name|getFlowName
argument_list|()
argument_list|,
name|actualFlowActivityDoc
operator|.
name|getFlowName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowActivityDoc
operator|.
name|getType
argument_list|()
argument_list|,
name|actualFlowActivityDoc
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowActivityDoc
operator|.
name|getUser
argument_list|()
argument_list|,
name|actualFlowActivityDoc
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowActivityDoc
operator|.
name|getId
argument_list|()
argument_list|,
name|actualFlowActivityDoc
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|expectedFlowActivityDoc
operator|.
name|addFlowActivity
argument_list|(
name|FLOW_NAME
argument_list|,
name|FLOW_VERSION
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|actualFlowActivityDoc
operator|.
name|merge
argument_list|(
name|expectedFlowActivityDoc
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowActivityDoc
operator|.
name|getDayTimestamp
argument_list|()
argument_list|,
name|actualFlowActivityDoc
operator|.
name|getDayTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowActivityDoc
operator|.
name|getFlowActivities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|actualFlowActivityDoc
operator|.
name|getFlowActivities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowActivityDoc
operator|.
name|getFlowName
argument_list|()
argument_list|,
name|actualFlowActivityDoc
operator|.
name|getFlowName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowActivityDoc
operator|.
name|getType
argument_list|()
argument_list|,
name|actualFlowActivityDoc
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowActivityDoc
operator|.
name|getUser
argument_list|()
argument_list|,
name|actualFlowActivityDoc
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowActivityDoc
operator|.
name|getId
argument_list|()
argument_list|,
name|actualFlowActivityDoc
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFlowRunDocMergeAndAggOperation ()
specifier|public
name|void
name|testFlowRunDocMergeAndAggOperation
parameter_list|()
throws|throws
name|IOException
block|{
name|FlowRunDocument
name|actualFlowRunDoc
init|=
operator|new
name|FlowRunDocument
argument_list|()
decl_stmt|;
name|FlowRunDocument
name|expectedFlowRunDoc
init|=
name|DocumentStoreTestUtils
operator|.
name|bakeFlowRunDoc
argument_list|()
decl_stmt|;
specifier|final
name|long
name|timestamp
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|long
name|value
init|=
literal|98586624
decl_stmt|;
name|TimelineMetric
name|timelineMetric
init|=
operator|new
name|TimelineMetric
argument_list|()
decl_stmt|;
name|timelineMetric
operator|.
name|setId
argument_list|(
name|MEMORY_ID
argument_list|)
expr_stmt|;
name|timelineMetric
operator|.
name|setType
argument_list|(
name|TimelineMetric
operator|.
name|Type
operator|.
name|SINGLE_VALUE
argument_list|)
expr_stmt|;
name|timelineMetric
operator|.
name|setRealtimeAggregationOp
argument_list|(
name|TimelineMetricOperation
operator|.
name|SUM
argument_list|)
expr_stmt|;
name|timelineMetric
operator|.
name|addValue
argument_list|(
name|timestamp
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|TimelineMetricSubDoc
name|metricSubDoc
init|=
operator|new
name|TimelineMetricSubDoc
argument_list|(
name|timelineMetric
argument_list|)
decl_stmt|;
name|expectedFlowRunDoc
operator|.
name|getMetrics
argument_list|()
operator|.
name|put
argument_list|(
name|MEMORY_ID
argument_list|,
name|metricSubDoc
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|actualFlowRunDoc
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|actualFlowRunDoc
operator|.
name|getFlowName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|actualFlowRunDoc
operator|.
name|getFlowRunId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|actualFlowRunDoc
operator|.
name|getFlowVersion
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|actualFlowRunDoc
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|actualFlowRunDoc
operator|.
name|getUsername
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|actualFlowRunDoc
operator|.
name|getType
argument_list|()
argument_list|,
name|TimelineEntityType
operator|.
name|YARN_FLOW_RUN
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|actualFlowRunDoc
operator|.
name|getMinStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|actualFlowRunDoc
operator|.
name|getMaxEndTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|actualFlowRunDoc
operator|.
name|getMetrics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|actualFlowRunDoc
operator|.
name|merge
argument_list|(
name|expectedFlowRunDoc
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowRunDoc
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|actualFlowRunDoc
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowRunDoc
operator|.
name|getFlowName
argument_list|()
argument_list|,
name|actualFlowRunDoc
operator|.
name|getFlowName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowRunDoc
operator|.
name|getFlowRunId
argument_list|()
argument_list|,
name|actualFlowRunDoc
operator|.
name|getFlowRunId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowRunDoc
operator|.
name|getFlowVersion
argument_list|()
argument_list|,
name|actualFlowRunDoc
operator|.
name|getFlowVersion
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowRunDoc
operator|.
name|getId
argument_list|()
argument_list|,
name|actualFlowRunDoc
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowRunDoc
operator|.
name|getUsername
argument_list|()
argument_list|,
name|actualFlowRunDoc
operator|.
name|getUsername
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowRunDoc
operator|.
name|getType
argument_list|()
argument_list|,
name|actualFlowRunDoc
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowRunDoc
operator|.
name|getMinStartTime
argument_list|()
argument_list|,
name|actualFlowRunDoc
operator|.
name|getMinStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowRunDoc
operator|.
name|getMaxEndTime
argument_list|()
argument_list|,
name|actualFlowRunDoc
operator|.
name|getMaxEndTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedFlowRunDoc
operator|.
name|getMetrics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|actualFlowRunDoc
operator|.
name|getMetrics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|actualFlowRunDoc
operator|.
name|merge
argument_list|(
name|expectedFlowRunDoc
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|value
operator|+
name|value
argument_list|,
name|actualFlowRunDoc
operator|.
name|getMetrics
argument_list|()
operator|.
name|get
argument_list|(
name|MEMORY_ID
argument_list|)
operator|.
name|getSingleDataValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

