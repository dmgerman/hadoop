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

begin_comment
comment|/**  * This class is a main entry-point for any kind of metrics for  * custom resources.  * It provides increase and decrease methods for all types of metrics.  */
end_comment

begin_class
DECL|class|QueueMetricsForCustomResources
specifier|public
class|class
name|QueueMetricsForCustomResources
block|{
DECL|field|aggregatePreemptedSeconds
specifier|private
specifier|final
name|QueueMetricsCustomResource
name|aggregatePreemptedSeconds
init|=
operator|new
name|QueueMetricsCustomResource
argument_list|()
decl_stmt|;
DECL|field|allocated
specifier|private
specifier|final
name|QueueMetricsCustomResource
name|allocated
init|=
operator|new
name|QueueMetricsCustomResource
argument_list|()
decl_stmt|;
DECL|field|available
specifier|private
specifier|final
name|QueueMetricsCustomResource
name|available
init|=
operator|new
name|QueueMetricsCustomResource
argument_list|()
decl_stmt|;
DECL|field|pending
specifier|private
specifier|final
name|QueueMetricsCustomResource
name|pending
init|=
operator|new
name|QueueMetricsCustomResource
argument_list|()
decl_stmt|;
DECL|field|reserved
specifier|private
specifier|final
name|QueueMetricsCustomResource
name|reserved
init|=
operator|new
name|QueueMetricsCustomResource
argument_list|()
decl_stmt|;
DECL|method|increaseReserved (Resource res)
specifier|public
name|void
name|increaseReserved
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|reserved
operator|.
name|increase
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|decreaseReserved (Resource res)
specifier|public
name|void
name|decreaseReserved
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|reserved
operator|.
name|decrease
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|setAvailable (Resource res)
specifier|public
name|void
name|setAvailable
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|available
operator|.
name|set
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|increasePending (Resource res, int containers)
specifier|public
name|void
name|increasePending
parameter_list|(
name|Resource
name|res
parameter_list|,
name|int
name|containers
parameter_list|)
block|{
name|pending
operator|.
name|increaseWithMultiplier
argument_list|(
name|res
argument_list|,
name|containers
argument_list|)
expr_stmt|;
block|}
DECL|method|decreasePending (Resource res)
specifier|public
name|void
name|decreasePending
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|pending
operator|.
name|decrease
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|decreasePending (Resource res, int containers)
specifier|public
name|void
name|decreasePending
parameter_list|(
name|Resource
name|res
parameter_list|,
name|int
name|containers
parameter_list|)
block|{
name|pending
operator|.
name|decreaseWithMultiplier
argument_list|(
name|res
argument_list|,
name|containers
argument_list|)
expr_stmt|;
block|}
DECL|method|increaseAllocated (Resource res)
specifier|public
name|void
name|increaseAllocated
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|allocated
operator|.
name|increase
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|increaseAllocated (Resource res, int containers)
specifier|public
name|void
name|increaseAllocated
parameter_list|(
name|Resource
name|res
parameter_list|,
name|int
name|containers
parameter_list|)
block|{
name|allocated
operator|.
name|increaseWithMultiplier
argument_list|(
name|res
argument_list|,
name|containers
argument_list|)
expr_stmt|;
block|}
DECL|method|decreaseAllocated (Resource res)
specifier|public
name|void
name|decreaseAllocated
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|allocated
operator|.
name|decrease
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|decreaseAllocated (Resource res, int containers)
specifier|public
name|void
name|decreaseAllocated
parameter_list|(
name|Resource
name|res
parameter_list|,
name|int
name|containers
parameter_list|)
block|{
name|allocated
operator|.
name|decreaseWithMultiplier
argument_list|(
name|res
argument_list|,
name|containers
argument_list|)
expr_stmt|;
block|}
DECL|method|increaseAggregatedPreemptedSeconds (Resource res, long seconds)
specifier|public
name|void
name|increaseAggregatedPreemptedSeconds
parameter_list|(
name|Resource
name|res
parameter_list|,
name|long
name|seconds
parameter_list|)
block|{
name|aggregatePreemptedSeconds
operator|.
name|increaseWithMultiplier
argument_list|(
name|res
argument_list|,
name|seconds
argument_list|)
expr_stmt|;
block|}
DECL|method|getAllocatedValues ()
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getAllocatedValues
parameter_list|()
block|{
return|return
name|allocated
operator|.
name|getValues
argument_list|()
return|;
block|}
DECL|method|getAvailableValues ()
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getAvailableValues
parameter_list|()
block|{
return|return
name|available
operator|.
name|getValues
argument_list|()
return|;
block|}
DECL|method|getPendingValues ()
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getPendingValues
parameter_list|()
block|{
return|return
name|pending
operator|.
name|getValues
argument_list|()
return|;
block|}
DECL|method|getReservedValues ()
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getReservedValues
parameter_list|()
block|{
return|return
name|reserved
operator|.
name|getValues
argument_list|()
return|;
block|}
DECL|method|getAggregatePreemptedSeconds ()
name|QueueMetricsCustomResource
name|getAggregatePreemptedSeconds
parameter_list|()
block|{
return|return
name|aggregatePreemptedSeconds
return|;
block|}
DECL|method|getAvailable ()
specifier|public
name|QueueMetricsCustomResource
name|getAvailable
parameter_list|()
block|{
return|return
name|available
return|;
block|}
block|}
end_class

end_unit

