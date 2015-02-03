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
name|Test
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
name|assertNotNull
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
name|collector
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

