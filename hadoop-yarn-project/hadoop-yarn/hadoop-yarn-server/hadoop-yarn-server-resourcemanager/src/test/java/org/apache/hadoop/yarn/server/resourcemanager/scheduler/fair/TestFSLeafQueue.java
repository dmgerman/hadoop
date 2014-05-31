begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
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
operator|.
name|fair
package|;
end_package

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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|AsyncDispatcher
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
name|ResourceManager
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
name|ResourceScheduler
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
name|Resources
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestFSLeafQueue
specifier|public
class|class
name|TestFSLeafQueue
block|{
DECL|field|schedulable
specifier|private
name|FSLeafQueue
name|schedulable
init|=
literal|null
decl_stmt|;
DECL|field|maxResource
specifier|private
name|Resource
name|maxResource
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|10
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|FairScheduler
name|scheduler
init|=
operator|new
name|FairScheduler
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
name|createConfiguration
argument_list|()
decl_stmt|;
comment|// All tests assume only one assignment per node update
name|conf
operator|.
name|set
argument_list|(
name|FairSchedulerConfiguration
operator|.
name|ASSIGN_MULTIPLE
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|ResourceManager
name|resourceManager
init|=
operator|new
name|ResourceManager
argument_list|()
decl_stmt|;
name|resourceManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
operator|(
operator|(
name|AsyncDispatcher
operator|)
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getDispatcher
argument_list|()
operator|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|scheduler
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|start
argument_list|()
expr_stmt|;
name|scheduler
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
name|resourceManager
operator|.
name|getRMContext
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|queueName
init|=
literal|"root.queue1"
decl_stmt|;
name|scheduler
operator|.
name|allocConf
operator|=
name|mock
argument_list|(
name|AllocationConfiguration
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|scheduler
operator|.
name|allocConf
operator|.
name|getMaxResources
argument_list|(
name|queueName
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|maxResource
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|scheduler
operator|.
name|allocConf
operator|.
name|getMinResources
argument_list|(
name|queueName
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
expr_stmt|;
name|schedulable
operator|=
operator|new
name|FSLeafQueue
argument_list|(
name|queueName
argument_list|,
name|scheduler
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpdateDemand ()
specifier|public
name|void
name|testUpdateDemand
parameter_list|()
block|{
name|AppSchedulable
name|app
init|=
name|mock
argument_list|(
name|AppSchedulable
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|app
operator|.
name|getDemand
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|maxResource
argument_list|)
expr_stmt|;
name|schedulable
operator|.
name|addAppSchedulable
argument_list|(
name|app
argument_list|)
expr_stmt|;
name|schedulable
operator|.
name|addAppSchedulable
argument_list|(
name|app
argument_list|)
expr_stmt|;
name|schedulable
operator|.
name|updateDemand
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Demand is greater than max allowed "
argument_list|,
name|Resources
operator|.
name|equals
argument_list|(
name|schedulable
operator|.
name|getDemand
argument_list|()
argument_list|,
name|maxResource
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createConfiguration ()
specifier|private
name|Configuration
name|createConfiguration
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|FairScheduler
operator|.
name|class
argument_list|,
name|ResourceScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
block|}
end_class

end_unit

