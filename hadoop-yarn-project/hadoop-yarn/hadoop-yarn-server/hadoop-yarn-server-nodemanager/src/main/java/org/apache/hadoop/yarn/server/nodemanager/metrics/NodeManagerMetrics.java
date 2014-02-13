begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.metrics
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
name|nodemanager
operator|.
name|metrics
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
name|annotation
operator|.
name|Metric
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
name|annotation
operator|.
name|Metrics
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
name|lib
operator|.
name|DefaultMetricsSystem
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
name|lib
operator|.
name|MutableCounterInt
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
name|lib
operator|.
name|MutableGaugeInt
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
name|source
operator|.
name|JvmMetrics
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

begin_class
annotation|@
name|Metrics
argument_list|(
name|about
operator|=
literal|"Metrics for node manager"
argument_list|,
name|context
operator|=
literal|"yarn"
argument_list|)
DECL|class|NodeManagerMetrics
specifier|public
class|class
name|NodeManagerMetrics
block|{
DECL|field|containersLaunched
annotation|@
name|Metric
name|MutableCounterInt
name|containersLaunched
decl_stmt|;
DECL|field|containersCompleted
annotation|@
name|Metric
name|MutableCounterInt
name|containersCompleted
decl_stmt|;
DECL|field|containersFailed
annotation|@
name|Metric
name|MutableCounterInt
name|containersFailed
decl_stmt|;
DECL|field|containersKilled
annotation|@
name|Metric
name|MutableCounterInt
name|containersKilled
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"# of initializing containers"
argument_list|)
DECL|field|containersIniting
name|MutableGaugeInt
name|containersIniting
decl_stmt|;
DECL|field|containersRunning
annotation|@
name|Metric
name|MutableGaugeInt
name|containersRunning
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Current allocated memory in GB"
argument_list|)
DECL|field|allocatedGB
name|MutableGaugeInt
name|allocatedGB
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Current # of allocated containers"
argument_list|)
DECL|field|allocatedContainers
name|MutableGaugeInt
name|allocatedContainers
decl_stmt|;
DECL|field|availableGB
annotation|@
name|Metric
name|MutableGaugeInt
name|availableGB
decl_stmt|;
DECL|method|create ()
specifier|public
specifier|static
name|NodeManagerMetrics
name|create
parameter_list|()
block|{
return|return
name|create
argument_list|(
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
argument_list|)
return|;
block|}
DECL|method|create (MetricsSystem ms)
specifier|static
name|NodeManagerMetrics
name|create
parameter_list|(
name|MetricsSystem
name|ms
parameter_list|)
block|{
name|JvmMetrics
operator|.
name|create
argument_list|(
literal|"NodeManager"
argument_list|,
literal|null
argument_list|,
name|ms
argument_list|)
expr_stmt|;
return|return
name|ms
operator|.
name|register
argument_list|(
operator|new
name|NodeManagerMetrics
argument_list|()
argument_list|)
return|;
block|}
comment|// Potential instrumentation interface methods
DECL|method|launchedContainer ()
specifier|public
name|void
name|launchedContainer
parameter_list|()
block|{
name|containersLaunched
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|completedContainer ()
specifier|public
name|void
name|completedContainer
parameter_list|()
block|{
name|containersCompleted
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|failedContainer ()
specifier|public
name|void
name|failedContainer
parameter_list|()
block|{
name|containersFailed
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|killedContainer ()
specifier|public
name|void
name|killedContainer
parameter_list|()
block|{
name|containersKilled
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|initingContainer ()
specifier|public
name|void
name|initingContainer
parameter_list|()
block|{
name|containersIniting
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|endInitingContainer ()
specifier|public
name|void
name|endInitingContainer
parameter_list|()
block|{
name|containersIniting
operator|.
name|decr
argument_list|()
expr_stmt|;
block|}
DECL|method|runningContainer ()
specifier|public
name|void
name|runningContainer
parameter_list|()
block|{
name|containersRunning
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|endRunningContainer ()
specifier|public
name|void
name|endRunningContainer
parameter_list|()
block|{
name|containersRunning
operator|.
name|decr
argument_list|()
expr_stmt|;
block|}
DECL|method|allocateContainer (Resource res)
specifier|public
name|void
name|allocateContainer
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|allocatedContainers
operator|.
name|incr
argument_list|()
expr_stmt|;
name|allocatedGB
operator|.
name|incr
argument_list|(
name|res
operator|.
name|getMemory
argument_list|()
operator|/
literal|1024
argument_list|)
expr_stmt|;
name|availableGB
operator|.
name|decr
argument_list|(
name|res
operator|.
name|getMemory
argument_list|()
operator|/
literal|1024
argument_list|)
expr_stmt|;
block|}
DECL|method|releaseContainer (Resource res)
specifier|public
name|void
name|releaseContainer
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|allocatedContainers
operator|.
name|decr
argument_list|()
expr_stmt|;
name|allocatedGB
operator|.
name|decr
argument_list|(
name|res
operator|.
name|getMemory
argument_list|()
operator|/
literal|1024
argument_list|)
expr_stmt|;
name|availableGB
operator|.
name|incr
argument_list|(
name|res
operator|.
name|getMemory
argument_list|()
operator|/
literal|1024
argument_list|)
expr_stmt|;
block|}
DECL|method|addResource (Resource res)
specifier|public
name|void
name|addResource
parameter_list|(
name|Resource
name|res
parameter_list|)
block|{
name|availableGB
operator|.
name|incr
argument_list|(
name|res
operator|.
name|getMemory
argument_list|()
operator|/
literal|1024
argument_list|)
expr_stmt|;
block|}
DECL|method|getRunningContainers ()
specifier|public
name|int
name|getRunningContainers
parameter_list|()
block|{
return|return
name|containersRunning
operator|.
name|value
argument_list|()
return|;
block|}
block|}
end_class

end_unit

