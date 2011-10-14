begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler
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
name|scheduler
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|assertCounter
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|assertGauge
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|getMetrics
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
name|test
operator|.
name|MockitoMaker
operator|.
name|make
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
name|test
operator|.
name|MockitoMaker
operator|.
name|stub
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
name|assertNull
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
name|mock
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
name|when
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
name|metrics2
operator|.
name|MetricsRecordBuilder
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
name|metrics2
operator|.
name|MetricsSource
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
name|metrics2
operator|.
name|MetricsSystem
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
name|metrics2
operator|.
name|impl
operator|.
name|MetricsSystemImpl
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
name|resource
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
name|server
operator|.
name|resourcemanager
operator|.
name|resource
operator|.
name|Resources
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestQueueMetrics
specifier|public
class|class
name|TestQueueMetrics
block|{
DECL|field|GB
specifier|static
specifier|final
name|int
name|GB
init|=
literal|1024
decl_stmt|;
comment|// MB
DECL|field|ms
specifier|final
name|MetricsSystem
name|ms
init|=
operator|new
name|MetricsSystemImpl
argument_list|()
decl_stmt|;
DECL|method|testDefaultSingleQueueMetrics ()
annotation|@
name|Test
specifier|public
name|void
name|testDefaultSingleQueueMetrics
parameter_list|()
block|{
name|String
name|queueName
init|=
literal|"single"
decl_stmt|;
name|String
name|user
init|=
literal|"alice"
decl_stmt|;
name|QueueMetrics
name|metrics
init|=
name|QueueMetrics
operator|.
name|forQueue
argument_list|(
name|ms
argument_list|,
name|queueName
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|MetricsSource
name|queueSource
init|=
name|queueSource
argument_list|(
name|ms
argument_list|,
name|queueName
argument_list|)
decl_stmt|;
name|AppSchedulingInfo
name|app
init|=
name|mockApp
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|metrics
operator|.
name|submitApp
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|MetricsSource
name|userSource
init|=
name|userSource
argument_list|(
name|ms
argument_list|,
name|queueName
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|checkApps
argument_list|(
name|queueSource
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|setAvailableResourcesToQueue
argument_list|(
name|Resource
operator|.
name|createResource
argument_list|(
literal|100
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|incrPendingResources
argument_list|(
name|user
argument_list|,
literal|5
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|15
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
comment|// Available resources is set externally, as it depends on dynamic
comment|// configurable cluster/queue resources
name|checkResources
argument_list|(
name|queueSource
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|,
literal|15
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|incrAppsRunning
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|checkApps
argument_list|(
name|queueSource
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|allocateResources
argument_list|(
name|user
argument_list|,
literal|3
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|2
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|checkResources
argument_list|(
name|queueSource
argument_list|,
literal|6
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|,
literal|9
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|releaseResources
argument_list|(
name|user
argument_list|,
literal|1
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|2
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|checkResources
argument_list|(
name|queueSource
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|,
literal|9
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|finishApp
argument_list|(
name|app
argument_list|,
name|RMAppAttemptState
operator|.
name|FINISHED
argument_list|)
expr_stmt|;
name|checkApps
argument_list|(
name|queueSource
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|userSource
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleQueueWithUserMetrics ()
annotation|@
name|Test
specifier|public
name|void
name|testSingleQueueWithUserMetrics
parameter_list|()
block|{
name|String
name|queueName
init|=
literal|"single2"
decl_stmt|;
name|String
name|user
init|=
literal|"dodo"
decl_stmt|;
name|QueueMetrics
name|metrics
init|=
name|QueueMetrics
operator|.
name|forQueue
argument_list|(
name|ms
argument_list|,
name|queueName
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|MetricsSource
name|queueSource
init|=
name|queueSource
argument_list|(
name|ms
argument_list|,
name|queueName
argument_list|)
decl_stmt|;
name|AppSchedulingInfo
name|app
init|=
name|mockApp
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|metrics
operator|.
name|submitApp
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|MetricsSource
name|userSource
init|=
name|userSource
argument_list|(
name|ms
argument_list|,
name|queueName
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|checkApps
argument_list|(
name|queueSource
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkApps
argument_list|(
name|userSource
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|setAvailableResourcesToQueue
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|100
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|setAvailableResourcesToUser
argument_list|(
name|user
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|10
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|incrPendingResources
argument_list|(
name|user
argument_list|,
literal|5
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|15
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
comment|// Available resources is set externally, as it depends on dynamic
comment|// configurable cluster/queue resources
name|checkResources
argument_list|(
name|queueSource
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|,
literal|15
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkResources
argument_list|(
name|userSource
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|15
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|incrAppsRunning
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|checkApps
argument_list|(
name|queueSource
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkApps
argument_list|(
name|userSource
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|allocateResources
argument_list|(
name|user
argument_list|,
literal|3
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|2
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|checkResources
argument_list|(
name|queueSource
argument_list|,
literal|6
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|,
literal|9
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkResources
argument_list|(
name|userSource
argument_list|,
literal|6
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|9
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|releaseResources
argument_list|(
name|user
argument_list|,
literal|1
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|2
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|checkResources
argument_list|(
name|queueSource
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|,
literal|9
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkResources
argument_list|(
name|userSource
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|,
literal|9
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|finishApp
argument_list|(
name|app
argument_list|,
name|RMAppAttemptState
operator|.
name|FINISHED
argument_list|)
expr_stmt|;
name|checkApps
argument_list|(
name|queueSource
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkApps
argument_list|(
name|userSource
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoLevelWithUserMetrics ()
annotation|@
name|Test
specifier|public
name|void
name|testTwoLevelWithUserMetrics
parameter_list|()
block|{
name|String
name|parentQueueName
init|=
literal|"root"
decl_stmt|;
name|String
name|leafQueueName
init|=
literal|"root.leaf"
decl_stmt|;
name|String
name|user
init|=
literal|"alice"
decl_stmt|;
name|QueueMetrics
name|parentMetrics
init|=
name|QueueMetrics
operator|.
name|forQueue
argument_list|(
name|ms
argument_list|,
name|parentQueueName
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Queue
name|parentQueue
init|=
name|make
argument_list|(
name|stub
argument_list|(
name|Queue
operator|.
name|class
argument_list|)
operator|.
name|returning
argument_list|(
name|parentMetrics
argument_list|)
operator|.
name|from
operator|.
name|getMetrics
argument_list|()
argument_list|)
decl_stmt|;
name|QueueMetrics
name|metrics
init|=
name|QueueMetrics
operator|.
name|forQueue
argument_list|(
name|ms
argument_list|,
name|leafQueueName
argument_list|,
name|parentQueue
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|MetricsSource
name|parentQueueSource
init|=
name|queueSource
argument_list|(
name|ms
argument_list|,
name|parentQueueName
argument_list|)
decl_stmt|;
name|MetricsSource
name|queueSource
init|=
name|queueSource
argument_list|(
name|ms
argument_list|,
name|leafQueueName
argument_list|)
decl_stmt|;
name|AppSchedulingInfo
name|app
init|=
name|mockApp
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|metrics
operator|.
name|submitApp
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|MetricsSource
name|userSource
init|=
name|userSource
argument_list|(
name|ms
argument_list|,
name|leafQueueName
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|MetricsSource
name|parentUserSource
init|=
name|userSource
argument_list|(
name|ms
argument_list|,
name|parentQueueName
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|checkApps
argument_list|(
name|queueSource
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkApps
argument_list|(
name|parentQueueSource
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkApps
argument_list|(
name|userSource
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkApps
argument_list|(
name|parentUserSource
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|parentMetrics
operator|.
name|setAvailableResourcesToQueue
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|100
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|setAvailableResourcesToQueue
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
literal|100
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|parentMetrics
operator|.
name|setAvailableResourcesToUser
argument_list|(
name|user
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|10
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|setAvailableResourcesToUser
argument_list|(
name|user
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|10
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|incrPendingResources
argument_list|(
name|user
argument_list|,
literal|5
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|15
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|checkResources
argument_list|(
name|queueSource
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|,
literal|15
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkResources
argument_list|(
name|parentQueueSource
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|,
literal|15
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkResources
argument_list|(
name|userSource
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|15
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkResources
argument_list|(
name|parentUserSource
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|15
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|incrAppsRunning
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|checkApps
argument_list|(
name|queueSource
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkApps
argument_list|(
name|userSource
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|allocateResources
argument_list|(
name|user
argument_list|,
literal|3
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|2
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|reserveResource
argument_list|(
name|user
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|3
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
comment|// Available resources is set externally, as it depends on dynamic
comment|// configurable cluster/queue resources
name|checkResources
argument_list|(
name|queueSource
argument_list|,
literal|6
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|,
literal|9
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|checkResources
argument_list|(
name|parentQueueSource
argument_list|,
literal|6
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|,
literal|9
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|checkResources
argument_list|(
name|userSource
argument_list|,
literal|6
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|9
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|checkResources
argument_list|(
name|parentUserSource
argument_list|,
literal|6
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|9
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|releaseResources
argument_list|(
name|user
argument_list|,
literal|1
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|2
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|unreserveResource
argument_list|(
name|user
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|3
operator|*
name|GB
argument_list|)
argument_list|)
expr_stmt|;
name|checkResources
argument_list|(
name|queueSource
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|,
literal|9
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkResources
argument_list|(
name|parentQueueSource
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|,
literal|9
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkResources
argument_list|(
name|userSource
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|,
literal|9
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkResources
argument_list|(
name|parentUserSource
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|,
literal|9
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|finishApp
argument_list|(
name|app
argument_list|,
name|RMAppAttemptState
operator|.
name|FINISHED
argument_list|)
expr_stmt|;
name|checkApps
argument_list|(
name|queueSource
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkApps
argument_list|(
name|parentQueueSource
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkApps
argument_list|(
name|userSource
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkApps
argument_list|(
name|parentUserSource
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|checkApps (MetricsSource source, int submitted, int pending, int running, int completed, int failed, int killed)
specifier|public
specifier|static
name|void
name|checkApps
parameter_list|(
name|MetricsSource
name|source
parameter_list|,
name|int
name|submitted
parameter_list|,
name|int
name|pending
parameter_list|,
name|int
name|running
parameter_list|,
name|int
name|completed
parameter_list|,
name|int
name|failed
parameter_list|,
name|int
name|killed
parameter_list|)
block|{
name|MetricsRecordBuilder
name|rb
init|=
name|getMetrics
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|assertCounter
argument_list|(
literal|"AppsSubmitted"
argument_list|,
name|submitted
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"AppsPending"
argument_list|,
name|pending
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"AppsRunning"
argument_list|,
name|running
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"AppsCompleted"
argument_list|,
name|completed
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"AppsFailed"
argument_list|,
name|failed
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"AppsKilled"
argument_list|,
name|killed
argument_list|,
name|rb
argument_list|)
expr_stmt|;
block|}
DECL|method|checkResources (MetricsSource source, int allocGB, int allocCtnrs, long aggreAllocCtnrs, long aggreReleasedCtnrs, int availGB, int pendingGB, int pendingCtnrs, int reservedGB, int reservedCtnrs)
specifier|public
specifier|static
name|void
name|checkResources
parameter_list|(
name|MetricsSource
name|source
parameter_list|,
name|int
name|allocGB
parameter_list|,
name|int
name|allocCtnrs
parameter_list|,
name|long
name|aggreAllocCtnrs
parameter_list|,
name|long
name|aggreReleasedCtnrs
parameter_list|,
name|int
name|availGB
parameter_list|,
name|int
name|pendingGB
parameter_list|,
name|int
name|pendingCtnrs
parameter_list|,
name|int
name|reservedGB
parameter_list|,
name|int
name|reservedCtnrs
parameter_list|)
block|{
name|MetricsRecordBuilder
name|rb
init|=
name|getMetrics
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|assertGauge
argument_list|(
literal|"AllocatedGB"
argument_list|,
name|allocGB
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"AllocatedContainers"
argument_list|,
name|allocCtnrs
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"AggregateContainersAllocated"
argument_list|,
name|aggreAllocCtnrs
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"AggregateContainersReleased"
argument_list|,
name|aggreReleasedCtnrs
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"AvailableGB"
argument_list|,
name|availGB
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"PendingGB"
argument_list|,
name|pendingGB
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"PendingContainers"
argument_list|,
name|pendingCtnrs
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"ReservedGB"
argument_list|,
name|reservedGB
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"ReservedContainers"
argument_list|,
name|reservedCtnrs
argument_list|,
name|rb
argument_list|)
expr_stmt|;
block|}
DECL|method|mockApp (String user)
specifier|private
specifier|static
name|AppSchedulingInfo
name|mockApp
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|AppSchedulingInfo
name|app
init|=
name|mock
argument_list|(
name|AppSchedulingInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|app
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|user
argument_list|)
expr_stmt|;
return|return
name|app
return|;
block|}
DECL|method|queueSource (MetricsSystem ms, String queue)
specifier|public
specifier|static
name|MetricsSource
name|queueSource
parameter_list|(
name|MetricsSystem
name|ms
parameter_list|,
name|String
name|queue
parameter_list|)
block|{
name|MetricsSource
name|s
init|=
name|ms
operator|.
name|getSource
argument_list|(
name|QueueMetrics
operator|.
name|sourceName
argument_list|(
name|queue
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|s
return|;
block|}
DECL|method|userSource (MetricsSystem ms, String queue, String user)
specifier|public
specifier|static
name|MetricsSource
name|userSource
parameter_list|(
name|MetricsSystem
name|ms
parameter_list|,
name|String
name|queue
parameter_list|,
name|String
name|user
parameter_list|)
block|{
name|MetricsSource
name|s
init|=
name|ms
operator|.
name|getSource
argument_list|(
name|QueueMetrics
operator|.
name|sourceName
argument_list|(
name|queue
argument_list|)
operator|.
name|append
argument_list|(
literal|",user="
argument_list|)
operator|.
name|append
argument_list|(
name|user
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|s
return|;
block|}
block|}
end_class

end_unit

