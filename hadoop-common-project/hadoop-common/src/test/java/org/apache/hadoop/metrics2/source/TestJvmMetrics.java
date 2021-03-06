begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.source
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|source
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
name|util
operator|.
name|GcTimeMonitor
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
name|Rule
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
name|rules
operator|.
name|Timeout
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
name|*
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|*
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
name|metrics2
operator|.
name|MetricsCollector
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
name|MetricsRecordBuilder
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
name|service
operator|.
name|ServiceOperations
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
name|service
operator|.
name|ServiceStateException
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
name|test
operator|.
name|GenericTestUtils
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
name|util
operator|.
name|JvmPauseMonitor
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
name|List
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
name|metrics2
operator|.
name|source
operator|.
name|JvmMetricsInfo
operator|.
name|*
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
name|metrics2
operator|.
name|impl
operator|.
name|MsInfo
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestJvmMetrics
specifier|public
class|class
name|TestJvmMetrics
block|{
annotation|@
name|Rule
DECL|field|timeout
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|30000
argument_list|)
decl_stmt|;
DECL|field|pauseMonitor
specifier|private
name|JvmPauseMonitor
name|pauseMonitor
decl_stmt|;
DECL|field|gcTimeMonitor
specifier|private
name|GcTimeMonitor
name|gcTimeMonitor
decl_stmt|;
comment|/**    * Robust shutdown of the monitors if they haven't been stopped already.    */
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|ServiceOperations
operator|.
name|stop
argument_list|(
name|pauseMonitor
argument_list|)
expr_stmt|;
if|if
condition|(
name|gcTimeMonitor
operator|!=
literal|null
condition|)
block|{
name|gcTimeMonitor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testJvmPauseMonitorPresence ()
specifier|public
name|void
name|testJvmPauseMonitorPresence
parameter_list|()
block|{
name|pauseMonitor
operator|=
operator|new
name|JvmPauseMonitor
argument_list|()
expr_stmt|;
name|pauseMonitor
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|pauseMonitor
operator|.
name|start
argument_list|()
expr_stmt|;
name|JvmMetrics
name|jvmMetrics
init|=
operator|new
name|JvmMetrics
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
name|jvmMetrics
operator|.
name|setPauseMonitor
argument_list|(
name|pauseMonitor
argument_list|)
expr_stmt|;
name|MetricsRecordBuilder
name|rb
init|=
name|getMetrics
argument_list|(
name|jvmMetrics
argument_list|)
decl_stmt|;
name|MetricsCollector
name|mc
init|=
name|rb
operator|.
name|parent
argument_list|()
decl_stmt|;
name|verify
argument_list|(
name|mc
argument_list|)
operator|.
name|addRecord
argument_list|(
name|JvmMetrics
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|rb
argument_list|)
operator|.
name|tag
argument_list|(
name|ProcessName
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|rb
argument_list|)
operator|.
name|tag
argument_list|(
name|SessionId
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
for|for
control|(
name|JvmMetricsInfo
name|info
range|:
name|JvmMetricsInfo
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|info
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Mem"
argument_list|)
condition|)
block|{
name|verify
argument_list|(
name|rb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eq
argument_list|(
name|info
argument_list|)
argument_list|,
name|anyFloat
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|info
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Gc"
argument_list|)
operator|&&
operator|!
name|info
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
literal|"GcTimePercentage"
argument_list|)
condition|)
block|{
name|verify
argument_list|(
name|rb
argument_list|)
operator|.
name|addCounter
argument_list|(
name|eq
argument_list|(
name|info
argument_list|)
argument_list|,
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|info
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Threads"
argument_list|)
condition|)
block|{
name|verify
argument_list|(
name|rb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eq
argument_list|(
name|info
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|info
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Log"
argument_list|)
condition|)
block|{
name|verify
argument_list|(
name|rb
argument_list|)
operator|.
name|addCounter
argument_list|(
name|eq
argument_list|(
name|info
argument_list|)
argument_list|,
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testGcTimeMonitorPresence ()
specifier|public
name|void
name|testGcTimeMonitorPresence
parameter_list|()
block|{
name|gcTimeMonitor
operator|=
operator|new
name|GcTimeMonitor
argument_list|(
literal|60000
argument_list|,
literal|1000
argument_list|,
literal|70
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|gcTimeMonitor
operator|.
name|start
argument_list|()
expr_stmt|;
name|JvmMetrics
name|jvmMetrics
init|=
operator|new
name|JvmMetrics
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
name|jvmMetrics
operator|.
name|setGcTimeMonitor
argument_list|(
name|gcTimeMonitor
argument_list|)
expr_stmt|;
name|MetricsRecordBuilder
name|rb
init|=
name|getMetrics
argument_list|(
name|jvmMetrics
argument_list|)
decl_stmt|;
name|MetricsCollector
name|mc
init|=
name|rb
operator|.
name|parent
argument_list|()
decl_stmt|;
name|verify
argument_list|(
name|mc
argument_list|)
operator|.
name|addRecord
argument_list|(
name|JvmMetrics
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|rb
argument_list|)
operator|.
name|tag
argument_list|(
name|ProcessName
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|rb
argument_list|)
operator|.
name|tag
argument_list|(
name|SessionId
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
for|for
control|(
name|JvmMetricsInfo
name|info
range|:
name|JvmMetricsInfo
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|info
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
literal|"GcTimePercentage"
argument_list|)
condition|)
block|{
name|verify
argument_list|(
name|rb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eq
argument_list|(
name|info
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testDoubleStop ()
specifier|public
name|void
name|testDoubleStop
parameter_list|()
throws|throws
name|Throwable
block|{
name|pauseMonitor
operator|=
operator|new
name|JvmPauseMonitor
argument_list|()
expr_stmt|;
name|pauseMonitor
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|pauseMonitor
operator|.
name|start
argument_list|()
expr_stmt|;
name|pauseMonitor
operator|.
name|stop
argument_list|()
expr_stmt|;
name|pauseMonitor
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDoubleStart ()
specifier|public
name|void
name|testDoubleStart
parameter_list|()
throws|throws
name|Throwable
block|{
name|pauseMonitor
operator|=
operator|new
name|JvmPauseMonitor
argument_list|()
expr_stmt|;
name|pauseMonitor
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|pauseMonitor
operator|.
name|start
argument_list|()
expr_stmt|;
name|pauseMonitor
operator|.
name|start
argument_list|()
expr_stmt|;
name|pauseMonitor
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStopBeforeStart ()
specifier|public
name|void
name|testStopBeforeStart
parameter_list|()
throws|throws
name|Throwable
block|{
name|pauseMonitor
operator|=
operator|new
name|JvmPauseMonitor
argument_list|()
expr_stmt|;
try|try
block|{
name|pauseMonitor
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|pauseMonitor
operator|.
name|stop
argument_list|()
expr_stmt|;
name|pauseMonitor
operator|.
name|start
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected an exception, got "
operator|+
name|pauseMonitor
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceStateException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"cannot enter state"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testStopBeforeInit ()
specifier|public
name|void
name|testStopBeforeInit
parameter_list|()
throws|throws
name|Throwable
block|{
name|pauseMonitor
operator|=
operator|new
name|JvmPauseMonitor
argument_list|()
expr_stmt|;
try|try
block|{
name|pauseMonitor
operator|.
name|stop
argument_list|()
expr_stmt|;
name|pauseMonitor
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected an exception, got "
operator|+
name|pauseMonitor
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceStateException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"cannot enter state"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGcTimeMonitor ()
specifier|public
name|void
name|testGcTimeMonitor
parameter_list|()
block|{
class|class
name|Alerter
implements|implements
name|GcTimeMonitor
operator|.
name|GcTimeAlertHandler
block|{
specifier|private
specifier|volatile
name|int
name|numAlerts
decl_stmt|;
specifier|private
specifier|volatile
name|int
name|maxGcTimePercentage
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|alert
parameter_list|(
name|GcTimeMonitor
operator|.
name|GcData
name|gcData
parameter_list|)
block|{
name|numAlerts
operator|++
expr_stmt|;
if|if
condition|(
name|gcData
operator|.
name|getGcTimePercentage
argument_list|()
operator|>
name|maxGcTimePercentage
condition|)
block|{
name|maxGcTimePercentage
operator|=
name|gcData
operator|.
name|getGcTimePercentage
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|Alerter
name|alerter
init|=
operator|new
name|Alerter
argument_list|()
decl_stmt|;
name|int
name|alertGcPerc
init|=
literal|10
decl_stmt|;
comment|// Alerter should be called if GC takes>= 10%
name|gcTimeMonitor
operator|=
operator|new
name|GcTimeMonitor
argument_list|(
literal|60
operator|*
literal|1000
argument_list|,
literal|100
argument_list|,
name|alertGcPerc
argument_list|,
name|alerter
argument_list|)
expr_stmt|;
name|gcTimeMonitor
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|maxGcTimePercentage
init|=
literal|0
decl_stmt|;
name|long
name|gcCount
init|=
literal|0
decl_stmt|;
comment|// Generate a lot of garbage for some time and verify that the monitor
comment|// reports at least some percentage of time in GC pauses, and that the
comment|// alerter is invoked at least once.
name|List
argument_list|<
name|String
argument_list|>
name|garbageStrings
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// Run this for at least 1 sec for our monitor to collect enough data
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|<
literal|1000
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100000
condition|;
name|j
operator|++
control|)
block|{
name|garbageStrings
operator|.
name|add
argument_list|(
literal|"Long string prefix just to fill memory with garbage "
operator|+
name|j
argument_list|)
expr_stmt|;
block|}
name|garbageStrings
operator|.
name|clear
argument_list|()
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|GcTimeMonitor
operator|.
name|GcData
name|gcData
init|=
name|gcTimeMonitor
operator|.
name|getLatestGcData
argument_list|()
decl_stmt|;
name|int
name|gcTimePercentage
init|=
name|gcData
operator|.
name|getGcTimePercentage
argument_list|()
decl_stmt|;
if|if
condition|(
name|gcTimePercentage
operator|>
name|maxGcTimePercentage
condition|)
block|{
name|maxGcTimePercentage
operator|=
name|gcTimePercentage
expr_stmt|;
block|}
name|gcCount
operator|=
name|gcData
operator|.
name|getAccumulatedGcCount
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|maxGcTimePercentage
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|gcCount
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|alerter
operator|.
name|numAlerts
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|alerter
operator|.
name|maxGcTimePercentage
operator|>=
name|alertGcPerc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJvmMetricsSingletonWithSameProcessName ()
specifier|public
name|void
name|testJvmMetricsSingletonWithSameProcessName
parameter_list|()
block|{
name|JvmMetrics
name|jvmMetrics1
init|=
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
operator|.
name|initSingleton
argument_list|(
literal|"test"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|JvmMetrics
name|jvmMetrics2
init|=
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
operator|.
name|initSingleton
argument_list|(
literal|"test"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"initSingleton should return the singleton instance"
argument_list|,
name|jvmMetrics1
argument_list|,
name|jvmMetrics2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJvmMetricsSingletonWithDifferentProcessNames ()
specifier|public
name|void
name|testJvmMetricsSingletonWithDifferentProcessNames
parameter_list|()
block|{
specifier|final
name|String
name|process1Name
init|=
literal|"process1"
decl_stmt|;
name|JvmMetrics
name|jvmMetrics1
init|=
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
operator|.
name|initSingleton
argument_list|(
name|process1Name
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|String
name|process2Name
init|=
literal|"process2"
decl_stmt|;
name|JvmMetrics
name|jvmMetrics2
init|=
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
operator|.
name|initSingleton
argument_list|(
name|process2Name
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"initSingleton should return the singleton instance"
argument_list|,
name|jvmMetrics1
argument_list|,
name|jvmMetrics2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"unexpected process name of the singleton instance"
argument_list|,
name|process1Name
argument_list|,
name|jvmMetrics1
operator|.
name|processName
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"unexpected process name of the singleton instance"
argument_list|,
name|process1Name
argument_list|,
name|jvmMetrics2
operator|.
name|processName
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

