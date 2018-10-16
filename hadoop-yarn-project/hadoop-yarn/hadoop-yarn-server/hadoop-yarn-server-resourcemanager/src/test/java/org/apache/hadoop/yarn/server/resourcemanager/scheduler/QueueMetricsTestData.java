begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|yarn
operator|.
name|resourcetypes
operator|.
name|ResourceTypesTestHelper
operator|.
name|extractCustomResources
import|;
end_import

begin_comment
comment|/**  * This class is to test standard and custom resource metrics for all types.  * Metrics types can be one of: allocated, pending, reserved  * and other resources.  */
end_comment

begin_class
DECL|class|QueueMetricsTestData
specifier|public
specifier|final
class|class
name|QueueMetricsTestData
block|{
DECL|class|Builder
specifier|public
specifier|static
specifier|final
class|class
name|Builder
block|{
DECL|field|containers
specifier|private
name|int
name|containers
decl_stmt|;
DECL|field|resource
specifier|private
name|Resource
name|resource
decl_stmt|;
DECL|field|resourceToDecrease
specifier|private
name|Resource
name|resourceToDecrease
decl_stmt|;
DECL|field|customResourceValues
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|customResourceValues
decl_stmt|;
DECL|field|containersToDecrease
specifier|private
name|int
name|containersToDecrease
decl_stmt|;
DECL|field|user
specifier|private
name|String
name|user
decl_stmt|;
DECL|field|partition
specifier|private
name|String
name|partition
decl_stmt|;
DECL|field|queueInfo
specifier|private
name|QueueInfo
name|queueInfo
decl_stmt|;
DECL|method|Builder ()
specifier|private
name|Builder
parameter_list|()
block|{     }
DECL|method|create ()
specifier|public
specifier|static
name|Builder
name|create
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
DECL|method|withContainers (int containers)
specifier|public
name|Builder
name|withContainers
parameter_list|(
name|int
name|containers
parameter_list|)
block|{
name|this
operator|.
name|containers
operator|=
name|containers
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withResourceToDecrease (Resource res, int containers)
specifier|public
name|Builder
name|withResourceToDecrease
parameter_list|(
name|Resource
name|res
parameter_list|,
name|int
name|containers
parameter_list|)
block|{
name|this
operator|.
name|resourceToDecrease
operator|=
name|res
expr_stmt|;
name|this
operator|.
name|containersToDecrease
operator|=
name|containers
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withResources (Resource res)
specifier|public
name|Builder
name|withResources
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|this
operator|.
name|resource
operator|=
name|res
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withUser (String user)
specifier|public
name|Builder
name|withUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withPartition (String partition)
specifier|public
name|Builder
name|withPartition
parameter_list|(
name|String
name|partition
parameter_list|)
block|{
name|this
operator|.
name|partition
operator|=
name|partition
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withLeafQueue (QueueInfo qInfo)
specifier|public
name|Builder
name|withLeafQueue
parameter_list|(
name|QueueInfo
name|qInfo
parameter_list|)
block|{
name|this
operator|.
name|queueInfo
operator|=
name|qInfo
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|QueueMetricsTestData
name|build
parameter_list|()
block|{
name|this
operator|.
name|customResourceValues
operator|=
name|extractCustomResources
argument_list|(
name|resource
argument_list|)
expr_stmt|;
return|return
operator|new
name|QueueMetricsTestData
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
DECL|field|customResourceValues
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|customResourceValues
decl_stmt|;
DECL|field|containers
specifier|final
name|int
name|containers
decl_stmt|;
DECL|field|resourceToDecrease
specifier|final
name|Resource
name|resourceToDecrease
decl_stmt|;
DECL|field|containersToDecrease
specifier|final
name|int
name|containersToDecrease
decl_stmt|;
DECL|field|resource
specifier|final
name|Resource
name|resource
decl_stmt|;
DECL|field|partition
specifier|final
name|String
name|partition
decl_stmt|;
DECL|field|leafQueue
specifier|final
name|QueueInfo
name|leafQueue
decl_stmt|;
DECL|field|user
specifier|final
name|String
name|user
decl_stmt|;
DECL|method|QueueMetricsTestData (Builder builder)
specifier|private
name|QueueMetricsTestData
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|customResourceValues
operator|=
name|builder
operator|.
name|customResourceValues
expr_stmt|;
name|this
operator|.
name|containers
operator|=
name|builder
operator|.
name|containers
expr_stmt|;
name|this
operator|.
name|resourceToDecrease
operator|=
name|builder
operator|.
name|resourceToDecrease
expr_stmt|;
name|this
operator|.
name|containersToDecrease
operator|=
name|builder
operator|.
name|containersToDecrease
expr_stmt|;
name|this
operator|.
name|resource
operator|=
name|builder
operator|.
name|resource
expr_stmt|;
name|this
operator|.
name|partition
operator|=
name|builder
operator|.
name|partition
expr_stmt|;
name|this
operator|.
name|leafQueue
operator|=
name|builder
operator|.
name|queueInfo
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|builder
operator|.
name|user
expr_stmt|;
block|}
block|}
end_class

end_unit

