begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.monitor.invariants
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
name|monitor
operator|.
name|invariants
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
name|QueueMetrics
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|TestCase
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * This class tests the {@code MetricsInvariantChecker} by running it multiple  * time and reporting the time it takes to execute, as well as verifying that  * the invariant throws in case the invariants are not respected.  */
end_comment

begin_class
DECL|class|TestMetricsInvariantChecker
specifier|public
class|class
name|TestMetricsInvariantChecker
block|{
DECL|field|LOG
specifier|public
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|TestMetricsInvariantChecker
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|metricsSystem
specifier|private
name|MetricsSystem
name|metricsSystem
decl_stmt|;
DECL|field|ic
specifier|private
name|MetricsInvariantChecker
name|ic
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|this
operator|.
name|metricsSystem
operator|=
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
expr_stmt|;
name|JvmMetrics
operator|.
name|initSingleton
argument_list|(
literal|"ResourceManager"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|ic
operator|=
operator|new
name|MetricsInvariantChecker
argument_list|()
expr_stmt|;
name|this
operator|.
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MetricsInvariantChecker
operator|.
name|INVARIANTS_FILE
argument_list|,
literal|"src/test/resources/invariants.txt"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|MetricsInvariantChecker
operator|.
name|THROW_ON_VIOLATION
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ic
operator|.
name|init
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testManyRuns ()
specifier|public
name|void
name|testManyRuns
parameter_list|()
block|{
name|QueueMetrics
name|qm
init|=
name|QueueMetrics
operator|.
name|forQueue
argument_list|(
name|metricsSystem
argument_list|,
literal|"root"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|qm
operator|.
name|setAvailableResourcesToQueue
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
name|int
name|numIterations
init|=
literal|1000
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
name|ic
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Runtime per iteration (avg of "
operator|+
name|numIterations
operator|+
literal|" iterations): "
operator|+
operator|(
name|end
operator|-
name|start
operator|)
operator|+
literal|" tot time"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testViolation ()
specifier|public
name|void
name|testViolation
parameter_list|()
block|{
comment|// create a "wrong" condition in which the invariants are not respected
name|QueueMetrics
name|qm
init|=
name|QueueMetrics
operator|.
name|forQueue
argument_list|(
name|metricsSystem
argument_list|,
literal|"root"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|qm
operator|.
name|setAvailableResourcesToQueue
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// test with throwing exception turned on
try|try
block|{
name|ic
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvariantViolationException
name|i
parameter_list|)
block|{
comment|// expected
block|}
comment|// test log-only mode
name|conf
operator|.
name|setBoolean
argument_list|(
name|MetricsInvariantChecker
operator|.
name|THROW_ON_VIOLATION
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ic
operator|.
name|init
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ic
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

