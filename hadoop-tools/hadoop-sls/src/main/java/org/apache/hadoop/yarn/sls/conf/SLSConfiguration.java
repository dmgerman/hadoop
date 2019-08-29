begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.sls.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|sls
operator|.
name|conf
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SLSConfiguration
specifier|public
class|class
name|SLSConfiguration
block|{
comment|// sls
DECL|field|PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"yarn.sls."
decl_stmt|;
comment|// runner
DECL|field|RUNNER_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|RUNNER_PREFIX
init|=
name|PREFIX
operator|+
literal|"runner."
decl_stmt|;
DECL|field|RUNNER_POOL_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|RUNNER_POOL_SIZE
init|=
name|RUNNER_PREFIX
operator|+
literal|"pool.size"
decl_stmt|;
DECL|field|RUNNER_POOL_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|RUNNER_POOL_SIZE_DEFAULT
init|=
literal|10
decl_stmt|;
comment|// scheduler
DECL|field|SCHEDULER_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|SCHEDULER_PREFIX
init|=
name|PREFIX
operator|+
literal|"scheduler."
decl_stmt|;
DECL|field|RM_SCHEDULER
specifier|public
specifier|static
specifier|final
name|String
name|RM_SCHEDULER
init|=
name|SCHEDULER_PREFIX
operator|+
literal|"class"
decl_stmt|;
comment|// metrics
DECL|field|METRICS_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|METRICS_PREFIX
init|=
name|PREFIX
operator|+
literal|"metrics."
decl_stmt|;
DECL|field|METRICS_SWITCH
specifier|public
specifier|static
specifier|final
name|String
name|METRICS_SWITCH
init|=
name|METRICS_PREFIX
operator|+
literal|"switch"
decl_stmt|;
DECL|field|METRICS_WEB_ADDRESS_PORT
specifier|public
specifier|static
specifier|final
name|String
name|METRICS_WEB_ADDRESS_PORT
init|=
name|METRICS_PREFIX
operator|+
literal|"web.address.port"
decl_stmt|;
DECL|field|METRICS_OUTPUT_DIR
specifier|public
specifier|static
specifier|final
name|String
name|METRICS_OUTPUT_DIR
init|=
name|METRICS_PREFIX
operator|+
literal|"output"
decl_stmt|;
DECL|field|METRICS_WEB_ADDRESS_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|METRICS_WEB_ADDRESS_PORT_DEFAULT
init|=
literal|10001
decl_stmt|;
DECL|field|METRICS_TIMER_WINDOW_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|METRICS_TIMER_WINDOW_SIZE
init|=
name|METRICS_PREFIX
operator|+
literal|"timer.window.size"
decl_stmt|;
DECL|field|METRICS_TIMER_WINDOW_SIZE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|METRICS_TIMER_WINDOW_SIZE_DEFAULT
init|=
literal|100
decl_stmt|;
DECL|field|METRICS_RECORD_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|METRICS_RECORD_INTERVAL_MS
init|=
name|METRICS_PREFIX
operator|+
literal|"record.interval.ms"
decl_stmt|;
DECL|field|METRICS_RECORD_INTERVAL_MS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|METRICS_RECORD_INTERVAL_MS_DEFAULT
init|=
literal|1000
decl_stmt|;
comment|// nm
DECL|field|NM_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|NM_PREFIX
init|=
name|PREFIX
operator|+
literal|"nm."
decl_stmt|;
DECL|field|NM_MEMORY_MB
specifier|public
specifier|static
specifier|final
name|String
name|NM_MEMORY_MB
init|=
name|NM_PREFIX
operator|+
literal|"memory.mb"
decl_stmt|;
DECL|field|NM_MEMORY_MB_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|NM_MEMORY_MB_DEFAULT
init|=
literal|10240
decl_stmt|;
DECL|field|NM_VCORES
specifier|public
specifier|static
specifier|final
name|String
name|NM_VCORES
init|=
name|NM_PREFIX
operator|+
literal|"vcores"
decl_stmt|;
DECL|field|NM_VCORES_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|NM_VCORES_DEFAULT
init|=
literal|10
decl_stmt|;
DECL|field|NM_RESOURCE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|NM_RESOURCE_DEFAULT
init|=
literal|0
decl_stmt|;
DECL|field|NM_HEARTBEAT_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|NM_HEARTBEAT_INTERVAL_MS
init|=
name|NM_PREFIX
operator|+
literal|"heartbeat.interval.ms"
decl_stmt|;
DECL|field|NM_HEARTBEAT_INTERVAL_MS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|NM_HEARTBEAT_INTERVAL_MS_DEFAULT
init|=
literal|1000
decl_stmt|;
comment|// am
DECL|field|AM_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|AM_PREFIX
init|=
name|PREFIX
operator|+
literal|"am."
decl_stmt|;
DECL|field|AM_HEARTBEAT_INTERVAL_MS
specifier|public
specifier|static
specifier|final
name|String
name|AM_HEARTBEAT_INTERVAL_MS
init|=
name|AM_PREFIX
operator|+
literal|"heartbeat.interval.ms"
decl_stmt|;
DECL|field|NM_RESOURCE_UTILIZATION_RATIO
specifier|public
specifier|static
specifier|final
name|String
name|NM_RESOURCE_UTILIZATION_RATIO
init|=
name|NM_PREFIX
operator|+
literal|"resource.utilization.ratio"
decl_stmt|;
DECL|field|AM_HEARTBEAT_INTERVAL_MS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|AM_HEARTBEAT_INTERVAL_MS_DEFAULT
init|=
literal|1000
decl_stmt|;
DECL|field|AM_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|AM_TYPE
init|=
name|AM_PREFIX
operator|+
literal|"type"
decl_stmt|;
DECL|field|AM_TYPE_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|AM_TYPE_PREFIX
init|=
name|AM_TYPE
operator|+
literal|"."
decl_stmt|;
DECL|field|AM_CONTAINER_MEMORY
specifier|public
specifier|static
specifier|final
name|String
name|AM_CONTAINER_MEMORY
init|=
name|AM_PREFIX
operator|+
literal|"container.memory"
decl_stmt|;
DECL|field|AM_CONTAINER_MEMORY_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|AM_CONTAINER_MEMORY_DEFAULT
init|=
literal|1024
decl_stmt|;
DECL|field|AM_CONTAINER_VCORES
specifier|public
specifier|static
specifier|final
name|String
name|AM_CONTAINER_VCORES
init|=
name|AM_PREFIX
operator|+
literal|"container.vcores"
decl_stmt|;
DECL|field|AM_CONTAINER_VCORES_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|AM_CONTAINER_VCORES_DEFAULT
init|=
literal|1
decl_stmt|;
DECL|field|NM_RESOURCE_UTILIZATION_RATIO_DEFAULT
specifier|public
specifier|static
specifier|final
name|float
name|NM_RESOURCE_UTILIZATION_RATIO_DEFAULT
init|=
operator|-
literal|1F
decl_stmt|;
comment|// container
DECL|field|CONTAINER_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_PREFIX
init|=
name|PREFIX
operator|+
literal|"container."
decl_stmt|;
DECL|field|CONTAINER_MEMORY_MB
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_MEMORY_MB
init|=
name|CONTAINER_PREFIX
operator|+
literal|"memory.mb"
decl_stmt|;
DECL|field|CONTAINER_MEMORY_MB_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|CONTAINER_MEMORY_MB_DEFAULT
init|=
literal|1024
decl_stmt|;
DECL|field|CONTAINER_VCORES
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_VCORES
init|=
name|CONTAINER_PREFIX
operator|+
literal|"vcores"
decl_stmt|;
DECL|field|CONTAINER_VCORES_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|CONTAINER_VCORES_DEFAULT
init|=
literal|1
decl_stmt|;
DECL|method|getAMContainerResource (Configuration conf)
specifier|public
specifier|static
name|Resource
name|getAMContainerResource
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|Resource
operator|.
name|newInstance
argument_list|(
name|conf
operator|.
name|getLong
argument_list|(
name|AM_CONTAINER_MEMORY
argument_list|,
name|AM_CONTAINER_MEMORY_DEFAULT
argument_list|)
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|AM_CONTAINER_VCORES
argument_list|,
name|AM_CONTAINER_VCORES_DEFAULT
argument_list|)
argument_list|)
return|;
block|}
comment|// input file
comment|// nodes
DECL|field|NUM_NODES
specifier|public
specifier|static
specifier|final
name|String
name|NUM_NODES
init|=
literal|"num.nodes"
decl_stmt|;
DECL|field|NUM_RACKS
specifier|public
specifier|static
specifier|final
name|String
name|NUM_RACKS
init|=
literal|"num.racks"
decl_stmt|;
comment|// job
DECL|field|JOB_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|JOB_PREFIX
init|=
literal|"job."
decl_stmt|;
DECL|field|JOB_ID
specifier|public
specifier|static
specifier|final
name|String
name|JOB_ID
init|=
name|JOB_PREFIX
operator|+
literal|"id"
decl_stmt|;
DECL|field|JOB_START_MS
specifier|public
specifier|static
specifier|final
name|String
name|JOB_START_MS
init|=
name|JOB_PREFIX
operator|+
literal|"start.ms"
decl_stmt|;
DECL|field|JOB_END_MS
specifier|public
specifier|static
specifier|final
name|String
name|JOB_END_MS
init|=
name|JOB_PREFIX
operator|+
literal|"end.ms"
decl_stmt|;
DECL|field|JOB_QUEUE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|JOB_QUEUE_NAME
init|=
name|JOB_PREFIX
operator|+
literal|"queue.name"
decl_stmt|;
DECL|field|JOB_LABEL_EXPR
specifier|public
specifier|static
specifier|final
name|String
name|JOB_LABEL_EXPR
init|=
name|JOB_PREFIX
operator|+
literal|"label.expression"
decl_stmt|;
DECL|field|JOB_USER
specifier|public
specifier|static
specifier|final
name|String
name|JOB_USER
init|=
name|JOB_PREFIX
operator|+
literal|"user"
decl_stmt|;
DECL|field|JOB_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|JOB_COUNT
init|=
name|JOB_PREFIX
operator|+
literal|"count"
decl_stmt|;
DECL|field|JOB_TASKS
specifier|public
specifier|static
specifier|final
name|String
name|JOB_TASKS
init|=
name|JOB_PREFIX
operator|+
literal|"tasks"
decl_stmt|;
DECL|field|JOB_AM_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|JOB_AM_PREFIX
init|=
literal|"am."
decl_stmt|;
comment|// task
DECL|field|TASK_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|TASK_PREFIX
init|=
literal|"container."
decl_stmt|;
DECL|field|COUNT
specifier|public
specifier|static
specifier|final
name|String
name|COUNT
init|=
literal|"count"
decl_stmt|;
DECL|field|TASK_CONTAINER
specifier|public
specifier|static
specifier|final
name|String
name|TASK_CONTAINER
init|=
literal|"container."
decl_stmt|;
DECL|field|TASK_HOST
specifier|public
specifier|static
specifier|final
name|String
name|TASK_HOST
init|=
name|TASK_CONTAINER
operator|+
literal|"host"
decl_stmt|;
DECL|field|TASK_START_MS
specifier|public
specifier|static
specifier|final
name|String
name|TASK_START_MS
init|=
name|TASK_CONTAINER
operator|+
literal|"start.ms"
decl_stmt|;
DECL|field|TASK_END_MS
specifier|public
specifier|static
specifier|final
name|String
name|TASK_END_MS
init|=
name|TASK_CONTAINER
operator|+
literal|"end.ms"
decl_stmt|;
DECL|field|DURATION_MS
specifier|public
specifier|static
specifier|final
name|String
name|DURATION_MS
init|=
literal|"duration.ms"
decl_stmt|;
DECL|field|TASK_DURATION_MS
specifier|public
specifier|static
specifier|final
name|String
name|TASK_DURATION_MS
init|=
name|TASK_CONTAINER
operator|+
name|DURATION_MS
decl_stmt|;
DECL|field|TASK_PRIORITY
specifier|public
specifier|static
specifier|final
name|String
name|TASK_PRIORITY
init|=
name|TASK_CONTAINER
operator|+
literal|"priority"
decl_stmt|;
DECL|field|TASK_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TASK_TYPE
init|=
name|TASK_CONTAINER
operator|+
literal|"type"
decl_stmt|;
DECL|field|TASK_EXECUTION_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TASK_EXECUTION_TYPE
init|=
name|TASK_CONTAINER
operator|+
literal|"execution.type"
decl_stmt|;
DECL|field|TASK_ALLOCATION_ID
specifier|public
specifier|static
specifier|final
name|String
name|TASK_ALLOCATION_ID
init|=
name|TASK_CONTAINER
operator|+
literal|"allocation.id"
decl_stmt|;
DECL|field|TASK_REQUEST_DELAY
specifier|public
specifier|static
specifier|final
name|String
name|TASK_REQUEST_DELAY
init|=
name|TASK_CONTAINER
operator|+
literal|"request.delay"
decl_stmt|;
block|}
end_class

end_unit

