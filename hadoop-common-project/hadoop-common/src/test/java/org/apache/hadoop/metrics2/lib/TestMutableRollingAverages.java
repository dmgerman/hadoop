begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.lib
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|lib
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
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
name|Time
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
name|Map
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
name|TimeUnit
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
name|lib
operator|.
name|Interns
operator|.
name|info
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyDouble
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|eq
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

begin_comment
comment|/**  * This class tests various cases of the algorithms implemented in  * {@link MutableRollingAverages}.  */
end_comment

begin_class
DECL|class|TestMutableRollingAverages
specifier|public
class|class
name|TestMutableRollingAverages
block|{
comment|/**    * Tests if the results are correct if no samples are inserted, dry run of    * empty roll over.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testRollingAveragesEmptyRollover ()
specifier|public
name|void
name|testRollingAveragesEmptyRollover
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|MetricsRecordBuilder
name|rb
init|=
name|mockMetricsRecordBuilder
argument_list|()
decl_stmt|;
comment|/* 5s interval and 2 windows */
try|try
init|(
name|MutableRollingAverages
name|rollingAverages
init|=
operator|new
name|MutableRollingAverages
argument_list|(
literal|"Time"
argument_list|)
init|)
block|{
name|rollingAverages
operator|.
name|replaceScheduledTask
argument_list|(
literal|2
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|/* Check it initially */
name|rollingAverages
operator|.
name|snapshot
argument_list|(
name|rb
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|rb
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|addGauge
argument_list|(
name|info
argument_list|(
literal|"FooRollingAvgTime"
argument_list|,
literal|"Rolling average time for foo"
argument_list|)
argument_list|,
operator|(
name|long
operator|)
literal|0
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|rb
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|addGauge
argument_list|(
name|info
argument_list|(
literal|"BarAvgTime"
argument_list|,
literal|"Rolling average time for bar"
argument_list|)
argument_list|,
operator|(
name|long
operator|)
literal|0
argument_list|)
expr_stmt|;
comment|/* sleep 6s longer than 5s interval to wait for rollover done */
name|Thread
operator|.
name|sleep
argument_list|(
literal|6000
argument_list|)
expr_stmt|;
name|rollingAverages
operator|.
name|snapshot
argument_list|(
name|rb
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|rb
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|addGauge
argument_list|(
name|info
argument_list|(
literal|"FooRollingAvgTime"
argument_list|,
literal|"Rolling average time for foo"
argument_list|)
argument_list|,
operator|(
name|long
operator|)
literal|0
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|rb
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|addGauge
argument_list|(
name|info
argument_list|(
literal|"BarAvgTime"
argument_list|,
literal|"Rolling average time for bar"
argument_list|)
argument_list|,
operator|(
name|long
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Tests the case:    *<p>    * 5s interval and 2 sliding windows    *</p>    *<p>    * sample stream: 1000 times 1, 2, and 3, respectively, e.g. [1, 1...1], [2,    * 2...2] and [3, 3...3]    *</p>    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testRollingAveragesRollover ()
specifier|public
name|void
name|testRollingAveragesRollover
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|MetricsRecordBuilder
name|rb
init|=
name|mockMetricsRecordBuilder
argument_list|()
decl_stmt|;
specifier|final
name|String
name|name
init|=
literal|"foo2"
decl_stmt|;
specifier|final
name|int
name|windowSizeMs
init|=
literal|5000
decl_stmt|;
comment|// 5s roll over interval
specifier|final
name|int
name|numWindows
init|=
literal|2
decl_stmt|;
specifier|final
name|int
name|numOpsPerIteration
init|=
literal|1000
decl_stmt|;
try|try
init|(
name|MutableRollingAverages
name|rollingAverages
init|=
operator|new
name|MutableRollingAverages
argument_list|(
literal|"Time"
argument_list|)
init|)
block|{
name|rollingAverages
operator|.
name|replaceScheduledTask
argument_list|(
literal|2
argument_list|,
literal|5000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
comment|/* Push values for three intervals */
specifier|final
name|long
name|start
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|3
condition|;
name|i
operator|++
control|)
block|{
comment|/* insert value */
for|for
control|(
name|long
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|numOpsPerIteration
condition|;
name|j
operator|++
control|)
block|{
name|rollingAverages
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
comment|/**          * Sleep until 1s after the next windowSize seconds interval, to let the          * metrics roll over          */
specifier|final
name|long
name|sleep
init|=
operator|(
name|start
operator|+
operator|(
name|windowSizeMs
operator|*
name|i
operator|)
operator|+
literal|1000
operator|)
operator|-
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleep
argument_list|)
expr_stmt|;
comment|/* Verify that the window reset, check it has the values we pushed in */
name|rollingAverages
operator|.
name|snapshot
argument_list|(
name|rb
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|/*          * #1 window with a series of 1 1000          * times, e.g. [1, 1...1], similarly, #2 window, e.g. [2, 2...2],          * #3 window, e.g. [3, 3...3]          */
specifier|final
name|double
name|rollingSum
init|=
name|numOpsPerIteration
operator|*
operator|(
name|i
operator|>
literal|1
condition|?
operator|(
name|i
operator|-
literal|1
operator|)
else|:
literal|0
operator|)
operator|+
name|numOpsPerIteration
operator|*
name|i
decl_stmt|;
comment|/* one empty window or all 2 windows full */
specifier|final
name|long
name|rollingTotal
init|=
name|i
operator|>
literal|1
condition|?
literal|2
operator|*
name|numOpsPerIteration
else|:
name|numOpsPerIteration
decl_stmt|;
name|verify
argument_list|(
name|rb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|info
argument_list|(
literal|"[Foo2]RollingAvgTime"
argument_list|,
literal|"Rolling average time for foo2"
argument_list|)
argument_list|,
name|rollingSum
operator|/
name|rollingTotal
argument_list|)
expr_stmt|;
comment|/* Verify the metrics were added the right number of times */
name|verify
argument_list|(
name|rb
argument_list|,
name|times
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eq
argument_list|(
name|info
argument_list|(
literal|"[Foo2]RollingAvgTime"
argument_list|,
literal|"Rolling average time for foo2"
argument_list|)
argument_list|)
argument_list|,
name|anyDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Test that MutableRollingAverages gives expected results after    * initialization.    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testMutableRollingAveragesMetric ()
specifier|public
name|void
name|testMutableRollingAveragesMetric
parameter_list|()
throws|throws
name|Exception
block|{
name|DummyTestMetric
name|testMetric
init|=
operator|new
name|DummyTestMetric
argument_list|()
decl_stmt|;
name|testMetric
operator|.
name|create
argument_list|()
expr_stmt|;
name|testMetric
operator|.
name|add
argument_list|(
literal|"metric1"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|testMetric
operator|.
name|add
argument_list|(
literal|"metric1"
argument_list|,
literal|900
argument_list|)
expr_stmt|;
name|testMetric
operator|.
name|add
argument_list|(
literal|"metric2"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|testMetric
operator|.
name|add
argument_list|(
literal|"metric2"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
name|testMetric
operator|.
name|collectThreadLocalStates
argument_list|()
expr_stmt|;
return|return
name|testMetric
operator|.
name|getStats
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
return|;
block|}
block|}
argument_list|,
literal|500
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
name|MetricsRecordBuilder
name|rb
init|=
name|getMetrics
argument_list|(
name|DummyTestMetric
operator|.
name|METRIC_NAME
argument_list|)
decl_stmt|;
name|double
name|metric1Avg
init|=
name|getDoubleGauge
argument_list|(
literal|"[Metric1]RollingAvgTesting"
argument_list|,
name|rb
argument_list|)
decl_stmt|;
name|double
name|metric2Avg
init|=
name|getDoubleGauge
argument_list|(
literal|"[Metric2]RollingAvgTesting"
argument_list|,
name|rb
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The rolling average of metric1 is not as expected"
argument_list|,
name|metric1Avg
operator|==
literal|500.0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The rolling average of metric2 is not as expected"
argument_list|,
name|metric2Avg
operator|==
literal|1000.0
argument_list|)
expr_stmt|;
block|}
DECL|class|DummyTestMetric
class|class
name|DummyTestMetric
block|{
annotation|@
name|Metric
argument_list|(
name|valueName
operator|=
literal|"testing"
argument_list|)
DECL|field|rollingAverages
specifier|private
name|MutableRollingAverages
name|rollingAverages
decl_stmt|;
DECL|field|METRIC_NAME
specifier|static
specifier|final
name|String
name|METRIC_NAME
init|=
literal|"RollingAveragesTestMetric"
decl_stmt|;
DECL|method|create ()
specifier|protected
name|void
name|create
parameter_list|()
block|{
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
operator|.
name|register
argument_list|(
name|METRIC_NAME
argument_list|,
literal|"mutable rolling averages test"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|rollingAverages
operator|.
name|replaceScheduledTask
argument_list|(
literal|10
argument_list|,
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
DECL|method|add (String name, long latency)
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|latency
parameter_list|)
block|{
name|rollingAverages
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|latency
argument_list|)
expr_stmt|;
block|}
DECL|method|collectThreadLocalStates ()
name|void
name|collectThreadLocalStates
parameter_list|()
block|{
name|rollingAverages
operator|.
name|collectThreadLocalStates
argument_list|()
expr_stmt|;
block|}
DECL|method|getStats ()
name|Map
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|getStats
parameter_list|()
block|{
return|return
name|rollingAverages
operator|.
name|getStats
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

