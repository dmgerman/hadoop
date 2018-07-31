begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.sls.appmaster
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
name|appmaster
package|;
end_package

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricRegistry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|ReservationId
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
name|client
operator|.
name|cli
operator|.
name|RMAdminCLI
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
name|exceptions
operator|.
name|YarnException
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
name|rmapp
operator|.
name|RMApp
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
name|capacity
operator|.
name|CapacityScheduler
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
name|sls
operator|.
name|conf
operator|.
name|SLSConfiguration
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
name|sls
operator|.
name|scheduler
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestAMSimulator
specifier|public
class|class
name|TestAMSimulator
block|{
DECL|field|rm
specifier|private
name|ResourceManager
name|rm
decl_stmt|;
DECL|field|conf
specifier|private
name|YarnConfiguration
name|conf
decl_stmt|;
DECL|field|metricOutputDir
specifier|private
name|Path
name|metricOutputDir
decl_stmt|;
DECL|field|slsScheduler
specifier|private
name|Class
argument_list|<
name|?
argument_list|>
name|slsScheduler
decl_stmt|;
DECL|field|scheduler
specifier|private
name|Class
argument_list|<
name|?
argument_list|>
name|scheduler
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
DECL|method|params ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|params
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
name|SLSFairScheduler
operator|.
name|class
block|,
name|FairScheduler
operator|.
name|class
block|}
block|,
block|{
name|SLSCapacityScheduler
operator|.
name|class
block|,
name|CapacityScheduler
operator|.
name|class
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|TestAMSimulator (Class<?> slsScheduler, Class<?> scheduler)
specifier|public
name|TestAMSimulator
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|slsScheduler
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|scheduler
parameter_list|)
block|{
name|this
operator|.
name|slsScheduler
operator|=
name|slsScheduler
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|createMetricOutputDir
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SLSConfiguration
operator|.
name|METRICS_OUTPUT_DIR
argument_list|,
name|metricOutputDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|slsScheduler
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SLSConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|scheduler
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NODE_LABELS_ENABLED
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|SLSConfiguration
operator|.
name|METRICS_SWITCH
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|rm
operator|=
operator|new
name|ResourceManager
argument_list|()
expr_stmt|;
name|rm
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|class|MockAMSimulator
class|class
name|MockAMSimulator
extends|extends
name|AMSimulator
block|{
annotation|@
name|Override
DECL|method|processResponseQueue ()
specifier|protected
name|void
name|processResponseQueue
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|YarnException
throws|,
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|sendContainerRequest ()
specifier|protected
name|void
name|sendContainerRequest
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{     }
annotation|@
name|Override
DECL|method|initReservation (ReservationId id, long deadline, long now)
specifier|public
name|void
name|initReservation
parameter_list|(
name|ReservationId
name|id
parameter_list|,
name|long
name|deadline
parameter_list|,
name|long
name|now
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|checkStop ()
specifier|protected
name|void
name|checkStop
parameter_list|()
block|{     }
block|}
DECL|method|verifySchedulerMetrics (String appId)
specifier|private
name|void
name|verifySchedulerMetrics
parameter_list|(
name|String
name|appId
parameter_list|)
block|{
if|if
condition|(
name|scheduler
operator|.
name|equals
argument_list|(
name|FairScheduler
operator|.
name|class
argument_list|)
condition|)
block|{
name|SchedulerMetrics
name|schedulerMetrics
init|=
operator|(
operator|(
name|SchedulerWrapper
operator|)
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|)
operator|.
name|getSchedulerMetrics
argument_list|()
decl_stmt|;
name|MetricRegistry
name|metricRegistry
init|=
name|schedulerMetrics
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
for|for
control|(
name|FairSchedulerMetrics
operator|.
name|Metric
name|metric
range|:
name|FairSchedulerMetrics
operator|.
name|Metric
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|key
init|=
literal|"variable.app."
operator|+
name|appId
operator|+
literal|"."
operator|+
name|metric
operator|.
name|getValue
argument_list|()
operator|+
literal|".memory"
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|metricRegistry
operator|.
name|getGauges
argument_list|()
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|metricRegistry
operator|.
name|getGauges
argument_list|()
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|createMetricOutputDir ()
specifier|private
name|void
name|createMetricOutputDir
parameter_list|()
block|{
name|Path
name|testDir
init|=
name|Paths
operator|.
name|get
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"target/test-dir"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|metricOutputDir
operator|=
name|Files
operator|.
name|createTempDirectory
argument_list|(
name|testDir
argument_list|,
literal|"output"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|deleteMetricOutputDir ()
specifier|private
name|void
name|deleteMetricOutputDir
parameter_list|()
block|{
try|try
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|metricOutputDir
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAMSimulator ()
specifier|public
name|void
name|testAMSimulator
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Register one app
name|MockAMSimulator
name|app
init|=
operator|new
name|MockAMSimulator
argument_list|()
decl_stmt|;
name|String
name|appId
init|=
literal|"app1"
decl_stmt|;
name|String
name|queue
init|=
literal|"default"
decl_stmt|;
name|List
argument_list|<
name|ContainerSimulator
argument_list|>
name|containers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|app
operator|.
name|init
argument_list|(
literal|1000
argument_list|,
name|containers
argument_list|,
name|rm
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|1000000L
argument_list|,
literal|"user1"
argument_list|,
name|queue
argument_list|,
literal|true
argument_list|,
name|appId
argument_list|,
literal|0
argument_list|,
name|SLSConfiguration
operator|.
name|getAMContainerResource
argument_list|(
name|conf
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|app
operator|.
name|firstStep
argument_list|()
expr_stmt|;
name|verifySchedulerMetrics
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|app
operator|.
name|appId
argument_list|)
argument_list|)
expr_stmt|;
comment|// Finish this app
name|app
operator|.
name|lastStep
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAMSimulatorWithNodeLabels ()
specifier|public
name|void
name|testAMSimulatorWithNodeLabels
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|scheduler
operator|.
name|equals
argument_list|(
name|CapacityScheduler
operator|.
name|class
argument_list|)
condition|)
block|{
comment|// add label to the cluster
name|RMAdminCLI
name|rmAdminCLI
init|=
operator|new
name|RMAdminCLI
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
index|[]
name|args
init|=
block|{
literal|"-addToClusterNodeLabels"
block|,
literal|"label1"
block|}
decl_stmt|;
name|rmAdminCLI
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|MockAMSimulator
name|app
init|=
operator|new
name|MockAMSimulator
argument_list|()
decl_stmt|;
name|String
name|appId
init|=
literal|"app1"
decl_stmt|;
name|String
name|queue
init|=
literal|"default"
decl_stmt|;
name|List
argument_list|<
name|ContainerSimulator
argument_list|>
name|containers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|app
operator|.
name|init
argument_list|(
literal|1000
argument_list|,
name|containers
argument_list|,
name|rm
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|1000000L
argument_list|,
literal|"user1"
argument_list|,
name|queue
argument_list|,
literal|true
argument_list|,
name|appId
argument_list|,
literal|0
argument_list|,
name|SLSConfiguration
operator|.
name|getAMContainerResource
argument_list|(
name|conf
argument_list|)
argument_list|,
literal|"label1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|app
operator|.
name|firstStep
argument_list|()
expr_stmt|;
name|verifySchedulerMetrics
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|RMApp
argument_list|>
name|rmApps
init|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rmApps
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|RMApp
name|rmApp
init|=
name|rmApps
operator|.
name|get
argument_list|(
name|app
operator|.
name|appId
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|rmApp
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"label1"
argument_list|,
name|rmApp
operator|.
name|getAmNodeLabelExpression
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|rm
operator|!=
literal|null
condition|)
block|{
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|deleteMetricOutputDir
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

