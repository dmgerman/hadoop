begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.monitor
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
name|containermanager
operator|.
name|monitor
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
name|AbstractMetric
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
name|MetricsRecord
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
name|MetricsCollectorImpl
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
name|MetricsRecords
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
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|ApplicationId
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
name|ContainerId
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|assertEquals
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
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyString
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
name|doReturn
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

begin_class
DECL|class|TestContainerMetrics
specifier|public
class|class
name|TestContainerMetrics
block|{
annotation|@
name|Test
DECL|method|testContainerMetricsFlow ()
specifier|public
name|void
name|testContainerMetricsFlow
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|String
name|ERR
init|=
literal|"Error in number of records"
decl_stmt|;
comment|// Create a dummy MetricsSystem
name|MetricsSystem
name|system
init|=
name|mock
argument_list|(
name|MetricsSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|this
argument_list|)
operator|.
name|when
argument_list|(
name|system
argument_list|)
operator|.
name|register
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|MetricsCollectorImpl
name|collector
init|=
operator|new
name|MetricsCollectorImpl
argument_list|()
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|mock
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerMetrics
name|metrics
init|=
name|ContainerMetrics
operator|.
name|forContainer
argument_list|(
name|containerId
argument_list|,
literal|100
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|metrics
operator|.
name|recordMemoryUsage
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|getMetrics
argument_list|(
name|collector
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ERR
argument_list|,
literal|0
argument_list|,
name|collector
operator|.
name|getRecords
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|110
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|getMetrics
argument_list|(
name|collector
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ERR
argument_list|,
literal|1
argument_list|,
name|collector
operator|.
name|getRecords
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|collector
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|110
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|getMetrics
argument_list|(
name|collector
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ERR
argument_list|,
literal|1
argument_list|,
name|collector
operator|.
name|getRecords
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|collector
operator|.
name|clear
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|finished
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|getMetrics
argument_list|(
name|collector
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ERR
argument_list|,
literal|1
argument_list|,
name|collector
operator|.
name|getRecords
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|collector
operator|.
name|clear
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|getMetrics
argument_list|(
name|collector
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ERR
argument_list|,
literal|1
argument_list|,
name|collector
operator|.
name|getRecords
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|collector
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|110
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|getMetrics
argument_list|(
name|collector
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ERR
argument_list|,
literal|1
argument_list|,
name|collector
operator|.
name|getRecords
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainerMetricsLimit ()
specifier|public
name|void
name|testContainerMetricsLimit
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|String
name|ERR
init|=
literal|"Error in number of records"
decl_stmt|;
name|MetricsSystem
name|system
init|=
name|mock
argument_list|(
name|MetricsSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|this
argument_list|)
operator|.
name|when
argument_list|(
name|system
argument_list|)
operator|.
name|register
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|()
argument_list|)
expr_stmt|;
name|MetricsCollectorImpl
name|collector
init|=
operator|new
name|MetricsCollectorImpl
argument_list|()
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|mock
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerMetrics
name|metrics
init|=
name|ContainerMetrics
operator|.
name|forContainer
argument_list|(
name|containerId
argument_list|,
literal|100
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|int
name|anyPmemLimit
init|=
literal|1024
decl_stmt|;
name|int
name|anyVmemLimit
init|=
literal|2048
decl_stmt|;
name|int
name|anyVcores
init|=
literal|10
decl_stmt|;
name|long
name|anyLaunchDuration
init|=
literal|20L
decl_stmt|;
name|long
name|anyLocalizationDuration
init|=
literal|1000L
decl_stmt|;
name|String
name|anyProcessId
init|=
literal|"1234"
decl_stmt|;
name|metrics
operator|.
name|recordResourceLimit
argument_list|(
name|anyVmemLimit
argument_list|,
name|anyPmemLimit
argument_list|,
name|anyVcores
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|recordProcessId
argument_list|(
name|anyProcessId
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|recordStateChangeDurations
argument_list|(
name|anyLaunchDuration
argument_list|,
name|anyLocalizationDuration
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|110
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|getMetrics
argument_list|(
name|collector
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ERR
argument_list|,
literal|1
argument_list|,
name|collector
operator|.
name|getRecords
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|MetricsRecord
name|record
init|=
name|collector
operator|.
name|getRecords
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|MetricsRecords
operator|.
name|assertTag
argument_list|(
name|record
argument_list|,
name|ContainerMetrics
operator|.
name|PROCESSID_INFO
operator|.
name|name
argument_list|()
argument_list|,
name|anyProcessId
argument_list|)
expr_stmt|;
name|MetricsRecords
operator|.
name|assertMetric
argument_list|(
name|record
argument_list|,
name|ContainerMetrics
operator|.
name|PMEM_LIMIT_METRIC_NAME
argument_list|,
name|anyPmemLimit
argument_list|)
expr_stmt|;
name|MetricsRecords
operator|.
name|assertMetric
argument_list|(
name|record
argument_list|,
name|ContainerMetrics
operator|.
name|VMEM_LIMIT_METRIC_NAME
argument_list|,
name|anyVmemLimit
argument_list|)
expr_stmt|;
name|MetricsRecords
operator|.
name|assertMetric
argument_list|(
name|record
argument_list|,
name|ContainerMetrics
operator|.
name|VCORE_LIMIT_METRIC_NAME
argument_list|,
name|anyVcores
argument_list|)
expr_stmt|;
name|MetricsRecords
operator|.
name|assertMetric
argument_list|(
name|record
argument_list|,
name|ContainerMetrics
operator|.
name|LAUNCH_DURATION_METRIC_NAME
argument_list|,
name|anyLaunchDuration
argument_list|)
expr_stmt|;
name|MetricsRecords
operator|.
name|assertMetric
argument_list|(
name|record
argument_list|,
name|ContainerMetrics
operator|.
name|LOCALIZATION_DURATION_METRIC_NAME
argument_list|,
name|anyLocalizationDuration
argument_list|)
expr_stmt|;
name|collector
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainerMetricsFinished ()
specifier|public
name|void
name|testContainerMetricsFinished
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|MetricsSystemImpl
name|system
init|=
operator|new
name|MetricsSystemImpl
argument_list|()
decl_stmt|;
name|system
operator|.
name|init
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1234
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId1
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerMetrics
name|metrics1
init|=
name|ContainerMetrics
operator|.
name|forContainer
argument_list|(
name|system
argument_list|,
name|containerId1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId2
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|ContainerMetrics
name|metrics2
init|=
name|ContainerMetrics
operator|.
name|forContainer
argument_list|(
name|system
argument_list|,
name|containerId2
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId3
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|ContainerMetrics
name|metrics3
init|=
name|ContainerMetrics
operator|.
name|forContainer
argument_list|(
name|system
argument_list|,
name|containerId3
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|metrics1
operator|.
name|finished
argument_list|()
expr_stmt|;
name|metrics2
operator|.
name|finished
argument_list|()
expr_stmt|;
name|system
operator|.
name|sampleMetrics
argument_list|()
expr_stmt|;
name|system
operator|.
name|sampleMetrics
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|system
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// verify metrics1 is unregistered
name|assertTrue
argument_list|(
name|metrics1
operator|!=
name|ContainerMetrics
operator|.
name|forContainer
argument_list|(
name|system
argument_list|,
name|containerId1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify metrics2 is unregistered
name|assertTrue
argument_list|(
name|metrics2
operator|!=
name|ContainerMetrics
operator|.
name|forContainer
argument_list|(
name|system
argument_list|,
name|containerId2
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify metrics3 is still registered
name|assertTrue
argument_list|(
name|metrics3
operator|==
name|ContainerMetrics
operator|.
name|forContainer
argument_list|(
name|system
argument_list|,
name|containerId3
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|system
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Run a test to submit values for actual memory usage and see if the    * histogram comes out correctly.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testContainerMetricsHistogram ()
specifier|public
name|void
name|testContainerMetricsHistogram
parameter_list|()
throws|throws
name|Exception
block|{
comment|// submit 2 values - 1024 and 2048. 75th, 90th, 95th and 99th percentiles
comment|// will be 2048. 50th percentile will be 1536((1024+2048)/2)
comment|// if we keep recording 1024 and 2048 in a loop, the 50th percentile
comment|// will tend closer to 2048
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|expectedValues
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|expectedValues
operator|.
name|put
argument_list|(
literal|"PMemUsageMBHistogram50thPercentileMBs"
argument_list|,
literal|1536L
argument_list|)
expr_stmt|;
name|expectedValues
operator|.
name|put
argument_list|(
literal|"PMemUsageMBHistogram75thPercentileMBs"
argument_list|,
literal|2048L
argument_list|)
expr_stmt|;
name|expectedValues
operator|.
name|put
argument_list|(
literal|"PMemUsageMBHistogram90thPercentileMBs"
argument_list|,
literal|2048L
argument_list|)
expr_stmt|;
name|expectedValues
operator|.
name|put
argument_list|(
literal|"PMemUsageMBHistogram95thPercentileMBs"
argument_list|,
literal|2048L
argument_list|)
expr_stmt|;
name|expectedValues
operator|.
name|put
argument_list|(
literal|"PMemUsageMBHistogram99thPercentileMBs"
argument_list|,
literal|2048L
argument_list|)
expr_stmt|;
name|expectedValues
operator|.
name|put
argument_list|(
literal|"PCpuUsagePercentHistogram50thPercentilePercents"
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|expectedValues
operator|.
name|put
argument_list|(
literal|"PCpuUsagePercentHistogram75thPercentilePercents"
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|expectedValues
operator|.
name|put
argument_list|(
literal|"PCpuUsagePercentHistogram90thPercentilePercents"
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|expectedValues
operator|.
name|put
argument_list|(
literal|"PCpuUsagePercentHistogram95thPercentilePercents"
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|expectedValues
operator|.
name|put
argument_list|(
literal|"PCpuUsagePercentHistogram99thPercentilePercents"
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|testResults
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|delay
init|=
literal|10
decl_stmt|;
name|int
name|rolloverDelay
init|=
literal|1000
decl_stmt|;
name|MetricsCollectorImpl
name|collector
init|=
operator|new
name|MetricsCollectorImpl
argument_list|()
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|mock
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerMetrics
name|metrics
init|=
name|ContainerMetrics
operator|.
name|forContainer
argument_list|(
name|containerId
argument_list|,
name|delay
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|metrics
operator|.
name|recordMemoryUsage
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|recordMemoryUsage
argument_list|(
literal|2048
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|rolloverDelay
operator|+
literal|10
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|getMetrics
argument_list|(
name|collector
argument_list|,
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|MetricsRecord
name|record
range|:
name|collector
operator|.
name|getRecords
argument_list|()
control|)
block|{
for|for
control|(
name|AbstractMetric
name|metric
range|:
name|record
operator|.
name|metrics
argument_list|()
control|)
block|{
name|String
name|metricName
init|=
name|metric
operator|.
name|name
argument_list|()
decl_stmt|;
if|if
condition|(
name|expectedValues
operator|.
name|containsKey
argument_list|(
name|metricName
argument_list|)
condition|)
block|{
name|Long
name|expectedValue
init|=
name|expectedValues
operator|.
name|get
argument_list|(
name|metricName
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Metric "
operator|+
name|metricName
operator|+
literal|" doesn't have expected value"
argument_list|,
name|expectedValue
argument_list|,
name|metric
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|testResults
operator|.
name|add
argument_list|(
name|metricName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedValues
operator|.
name|keySet
argument_list|()
argument_list|,
name|testResults
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

