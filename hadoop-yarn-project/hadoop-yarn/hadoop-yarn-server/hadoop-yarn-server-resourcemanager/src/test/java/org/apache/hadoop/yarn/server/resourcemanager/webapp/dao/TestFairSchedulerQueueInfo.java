begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.dao
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
name|webapp
operator|.
name|dao
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
name|RMContext
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
name|placement
operator|.
name|PlacementManager
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
name|scheduler
operator|.
name|fair
operator|.
name|AllocationConfiguration
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
name|scheduler
operator|.
name|fair
operator|.
name|FSQueue
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
name|scheduler
operator|.
name|fair
operator|.
name|FairScheduler
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
name|scheduler
operator|.
name|fair
operator|.
name|FairSchedulerConfiguration
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
name|scheduler
operator|.
name|fair
operator|.
name|QueueManager
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
name|SystemClock
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
name|resource
operator|.
name|DefaultResourceCalculator
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
name|util
operator|.
name|Collection
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

begin_class
DECL|class|TestFairSchedulerQueueInfo
specifier|public
class|class
name|TestFairSchedulerQueueInfo
block|{
annotation|@
name|Test
DECL|method|testEmptyChildQueues ()
specifier|public
name|void
name|testEmptyChildQueues
parameter_list|()
block|{
name|FairSchedulerConfiguration
name|fsConf
init|=
operator|new
name|FairSchedulerConfiguration
argument_list|()
decl_stmt|;
name|RMContext
name|rmContext
init|=
name|mock
argument_list|(
name|RMContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|PlacementManager
name|placementManager
init|=
operator|new
name|PlacementManager
argument_list|()
decl_stmt|;
name|SystemClock
name|clock
init|=
name|SystemClock
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|FairScheduler
name|scheduler
init|=
name|mock
argument_list|(
name|FairScheduler
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|scheduler
operator|.
name|getConf
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fsConf
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|scheduler
operator|.
name|getConfig
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fsConf
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|scheduler
operator|.
name|getRMContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rmContext
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rmContext
operator|.
name|getQueuePlacementManager
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|placementManager
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|scheduler
operator|.
name|getClusterResource
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|scheduler
operator|.
name|getResourceCalculator
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|DefaultResourceCalculator
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|scheduler
operator|.
name|getClock
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|AllocationConfiguration
name|allocConf
init|=
operator|new
name|AllocationConfiguration
argument_list|(
name|scheduler
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|scheduler
operator|.
name|getAllocationConfiguration
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|allocConf
argument_list|)
expr_stmt|;
name|QueueManager
name|queueManager
init|=
operator|new
name|QueueManager
argument_list|(
name|scheduler
argument_list|)
decl_stmt|;
name|queueManager
operator|.
name|initialize
argument_list|()
expr_stmt|;
name|FSQueue
name|testQueue
init|=
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"test"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FairSchedulerQueueInfo
name|queueInfo
init|=
operator|new
name|FairSchedulerQueueInfo
argument_list|(
name|testQueue
argument_list|,
name|scheduler
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|FairSchedulerQueueInfo
argument_list|>
name|childQueues
init|=
name|queueInfo
operator|.
name|getChildQueues
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|childQueues
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Child QueueInfo was not empty"
argument_list|,
literal|0
argument_list|,
name|childQueues
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

