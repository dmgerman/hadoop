begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|Job
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|Task
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

begin_class
annotation|@
name|Metrics
argument_list|(
name|about
operator|=
literal|"MR App Metrics"
argument_list|,
name|context
operator|=
literal|"mapred"
argument_list|)
DECL|class|MRAppMetrics
specifier|public
class|class
name|MRAppMetrics
block|{
DECL|field|jobsSubmitted
annotation|@
name|Metric
name|MutableCounterInt
name|jobsSubmitted
decl_stmt|;
DECL|field|jobsCompleted
annotation|@
name|Metric
name|MutableCounterInt
name|jobsCompleted
decl_stmt|;
DECL|field|jobsFailed
annotation|@
name|Metric
name|MutableCounterInt
name|jobsFailed
decl_stmt|;
DECL|field|jobsKilled
annotation|@
name|Metric
name|MutableCounterInt
name|jobsKilled
decl_stmt|;
DECL|field|jobsPreparing
annotation|@
name|Metric
name|MutableGaugeInt
name|jobsPreparing
decl_stmt|;
DECL|field|jobsRunning
annotation|@
name|Metric
name|MutableGaugeInt
name|jobsRunning
decl_stmt|;
DECL|field|mapsLaunched
annotation|@
name|Metric
name|MutableCounterInt
name|mapsLaunched
decl_stmt|;
DECL|field|mapsCompleted
annotation|@
name|Metric
name|MutableCounterInt
name|mapsCompleted
decl_stmt|;
DECL|field|mapsFailed
annotation|@
name|Metric
name|MutableCounterInt
name|mapsFailed
decl_stmt|;
DECL|field|mapsKilled
annotation|@
name|Metric
name|MutableCounterInt
name|mapsKilled
decl_stmt|;
DECL|field|mapsRunning
annotation|@
name|Metric
name|MutableGaugeInt
name|mapsRunning
decl_stmt|;
DECL|field|mapsWaiting
annotation|@
name|Metric
name|MutableGaugeInt
name|mapsWaiting
decl_stmt|;
DECL|field|reducesLaunched
annotation|@
name|Metric
name|MutableCounterInt
name|reducesLaunched
decl_stmt|;
DECL|field|reducesCompleted
annotation|@
name|Metric
name|MutableCounterInt
name|reducesCompleted
decl_stmt|;
DECL|field|reducesFailed
annotation|@
name|Metric
name|MutableCounterInt
name|reducesFailed
decl_stmt|;
DECL|field|reducesKilled
annotation|@
name|Metric
name|MutableCounterInt
name|reducesKilled
decl_stmt|;
DECL|field|reducesRunning
annotation|@
name|Metric
name|MutableGaugeInt
name|reducesRunning
decl_stmt|;
DECL|field|reducesWaiting
annotation|@
name|Metric
name|MutableGaugeInt
name|reducesWaiting
decl_stmt|;
DECL|method|create ()
specifier|public
specifier|static
name|MRAppMetrics
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
specifier|public
specifier|static
name|MRAppMetrics
name|create
parameter_list|(
name|MetricsSystem
name|ms
parameter_list|)
block|{
name|JvmMetrics
operator|.
name|initSingleton
argument_list|(
literal|"MRAppMaster"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|ms
operator|.
name|register
argument_list|(
operator|new
name|MRAppMetrics
argument_list|()
argument_list|)
return|;
block|}
comment|// potential instrumentation interface methods
DECL|method|submittedJob (Job job)
specifier|public
name|void
name|submittedJob
parameter_list|(
name|Job
name|job
parameter_list|)
block|{
name|jobsSubmitted
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|completedJob (Job job)
specifier|public
name|void
name|completedJob
parameter_list|(
name|Job
name|job
parameter_list|)
block|{
name|jobsCompleted
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|failedJob (Job job)
specifier|public
name|void
name|failedJob
parameter_list|(
name|Job
name|job
parameter_list|)
block|{
name|jobsFailed
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|killedJob (Job job)
specifier|public
name|void
name|killedJob
parameter_list|(
name|Job
name|job
parameter_list|)
block|{
name|jobsKilled
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|preparingJob (Job job)
specifier|public
name|void
name|preparingJob
parameter_list|(
name|Job
name|job
parameter_list|)
block|{
name|jobsPreparing
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|endPreparingJob (Job job)
specifier|public
name|void
name|endPreparingJob
parameter_list|(
name|Job
name|job
parameter_list|)
block|{
name|jobsPreparing
operator|.
name|decr
argument_list|()
expr_stmt|;
block|}
DECL|method|runningJob (Job job)
specifier|public
name|void
name|runningJob
parameter_list|(
name|Job
name|job
parameter_list|)
block|{
name|jobsRunning
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|endRunningJob (Job job)
specifier|public
name|void
name|endRunningJob
parameter_list|(
name|Job
name|job
parameter_list|)
block|{
name|jobsRunning
operator|.
name|decr
argument_list|()
expr_stmt|;
block|}
DECL|method|launchedTask (Task task)
specifier|public
name|void
name|launchedTask
parameter_list|(
name|Task
name|task
parameter_list|)
block|{
switch|switch
condition|(
name|task
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|MAP
case|:
name|mapsLaunched
operator|.
name|incr
argument_list|()
expr_stmt|;
break|break;
case|case
name|REDUCE
case|:
name|reducesLaunched
operator|.
name|incr
argument_list|()
expr_stmt|;
break|break;
block|}
name|endWaitingTask
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
DECL|method|completedTask (Task task)
specifier|public
name|void
name|completedTask
parameter_list|(
name|Task
name|task
parameter_list|)
block|{
switch|switch
condition|(
name|task
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|MAP
case|:
name|mapsCompleted
operator|.
name|incr
argument_list|()
expr_stmt|;
break|break;
case|case
name|REDUCE
case|:
name|reducesCompleted
operator|.
name|incr
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
DECL|method|failedTask (Task task)
specifier|public
name|void
name|failedTask
parameter_list|(
name|Task
name|task
parameter_list|)
block|{
switch|switch
condition|(
name|task
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|MAP
case|:
name|mapsFailed
operator|.
name|incr
argument_list|()
expr_stmt|;
break|break;
case|case
name|REDUCE
case|:
name|reducesFailed
operator|.
name|incr
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
DECL|method|killedTask (Task task)
specifier|public
name|void
name|killedTask
parameter_list|(
name|Task
name|task
parameter_list|)
block|{
switch|switch
condition|(
name|task
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|MAP
case|:
name|mapsKilled
operator|.
name|incr
argument_list|()
expr_stmt|;
break|break;
case|case
name|REDUCE
case|:
name|reducesKilled
operator|.
name|incr
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
DECL|method|runningTask (Task task)
specifier|public
name|void
name|runningTask
parameter_list|(
name|Task
name|task
parameter_list|)
block|{
switch|switch
condition|(
name|task
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|MAP
case|:
name|mapsRunning
operator|.
name|incr
argument_list|()
expr_stmt|;
break|break;
case|case
name|REDUCE
case|:
name|reducesRunning
operator|.
name|incr
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
DECL|method|endRunningTask (Task task)
specifier|public
name|void
name|endRunningTask
parameter_list|(
name|Task
name|task
parameter_list|)
block|{
switch|switch
condition|(
name|task
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|MAP
case|:
name|mapsRunning
operator|.
name|decr
argument_list|()
expr_stmt|;
break|break;
case|case
name|REDUCE
case|:
name|reducesRunning
operator|.
name|decr
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
DECL|method|waitingTask (Task task)
specifier|public
name|void
name|waitingTask
parameter_list|(
name|Task
name|task
parameter_list|)
block|{
switch|switch
condition|(
name|task
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|MAP
case|:
name|mapsWaiting
operator|.
name|incr
argument_list|()
expr_stmt|;
break|break;
case|case
name|REDUCE
case|:
name|reducesWaiting
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|endWaitingTask (Task task)
specifier|public
name|void
name|endWaitingTask
parameter_list|(
name|Task
name|task
parameter_list|)
block|{
switch|switch
condition|(
name|task
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|MAP
case|:
name|mapsWaiting
operator|.
name|decr
argument_list|()
expr_stmt|;
break|break;
case|case
name|REDUCE
case|:
name|reducesWaiting
operator|.
name|decr
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
end_class

end_unit

