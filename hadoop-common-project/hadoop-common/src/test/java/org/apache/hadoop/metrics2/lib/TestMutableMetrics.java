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
name|assertCounter
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
name|assertGauge
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
name|mockMetricsRecordBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|AdditionalMatchers
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
name|AdditionalMatchers
operator|.
name|geq
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|AdditionalMatchers
operator|.
name|leq
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
name|anyLong
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
name|times
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
name|verify
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
name|Map
operator|.
name|Entry
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
name|util
operator|.
name|Quantile
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

begin_comment
comment|/**  * Test metrics record builder interface and mutable metrics  */
end_comment

begin_class
DECL|class|TestMutableMetrics
specifier|public
class|class
name|TestMutableMetrics
block|{
DECL|field|EPSILON
specifier|private
specifier|final
name|double
name|EPSILON
init|=
literal|1e-42
decl_stmt|;
comment|/**    * Test the snapshot method    */
DECL|method|testSnapshot ()
annotation|@
name|Test
specifier|public
name|void
name|testSnapshot
parameter_list|()
block|{
name|MetricsRecordBuilder
name|mb
init|=
name|mockMetricsRecordBuilder
argument_list|()
decl_stmt|;
name|MetricsRegistry
name|registry
init|=
operator|new
name|MetricsRegistry
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|registry
operator|.
name|newCounter
argument_list|(
literal|"c1"
argument_list|,
literal|"int counter"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|registry
operator|.
name|newCounter
argument_list|(
literal|"c2"
argument_list|,
literal|"long counter"
argument_list|,
literal|2L
argument_list|)
expr_stmt|;
name|registry
operator|.
name|newGauge
argument_list|(
literal|"g1"
argument_list|,
literal|"int gauge"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|registry
operator|.
name|newGauge
argument_list|(
literal|"g2"
argument_list|,
literal|"long gauge"
argument_list|,
literal|4L
argument_list|)
expr_stmt|;
name|registry
operator|.
name|newStat
argument_list|(
literal|"s1"
argument_list|,
literal|"stat"
argument_list|,
literal|"Ops"
argument_list|,
literal|"Time"
argument_list|,
literal|true
argument_list|)
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|registry
operator|.
name|newRate
argument_list|(
literal|"s2"
argument_list|,
literal|"stat"
argument_list|,
literal|false
argument_list|)
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|registry
operator|.
name|snapshot
argument_list|(
name|mb
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|MutableStat
name|s2
init|=
operator|(
name|MutableStat
operator|)
name|registry
operator|.
name|get
argument_list|(
literal|"s2"
argument_list|)
decl_stmt|;
name|s2
operator|.
name|snapshot
argument_list|(
name|mb
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// should get the same back.
name|s2
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|s2
operator|.
name|snapshot
argument_list|(
name|mb
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// should get new interval values back
name|verify
argument_list|(
name|mb
argument_list|)
operator|.
name|addCounter
argument_list|(
name|info
argument_list|(
literal|"c1"
argument_list|,
literal|"int counter"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mb
argument_list|)
operator|.
name|addCounter
argument_list|(
name|info
argument_list|(
literal|"c2"
argument_list|,
literal|"long counter"
argument_list|)
argument_list|,
literal|2L
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|info
argument_list|(
literal|"g1"
argument_list|,
literal|"int gauge"
argument_list|)
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|info
argument_list|(
literal|"g2"
argument_list|,
literal|"long gauge"
argument_list|)
argument_list|,
literal|4L
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mb
argument_list|)
operator|.
name|addCounter
argument_list|(
name|info
argument_list|(
literal|"S1NumOps"
argument_list|,
literal|"Number of ops for stat"
argument_list|)
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eq
argument_list|(
name|info
argument_list|(
literal|"S1AvgTime"
argument_list|,
literal|"Average time for stat"
argument_list|)
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|0.0
argument_list|,
name|EPSILON
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eq
argument_list|(
name|info
argument_list|(
literal|"S1StdevTime"
argument_list|,
literal|"Standard deviation of time for stat"
argument_list|)
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|0.0
argument_list|,
name|EPSILON
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eq
argument_list|(
name|info
argument_list|(
literal|"S1IMinTime"
argument_list|,
literal|"Interval min time for stat"
argument_list|)
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|0.0
argument_list|,
name|EPSILON
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eq
argument_list|(
name|info
argument_list|(
literal|"S1IMaxTime"
argument_list|,
literal|"Interval max time for stat"
argument_list|)
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|0.0
argument_list|,
name|EPSILON
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eq
argument_list|(
name|info
argument_list|(
literal|"S1MinTime"
argument_list|,
literal|"Min time for stat"
argument_list|)
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|0.0
argument_list|,
name|EPSILON
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eq
argument_list|(
name|info
argument_list|(
literal|"S1MaxTime"
argument_list|,
literal|"Max time for stat"
argument_list|)
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|0.0
argument_list|,
name|EPSILON
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mb
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|addCounter
argument_list|(
name|info
argument_list|(
literal|"S2NumOps"
argument_list|,
literal|"Number of ops for stat"
argument_list|)
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mb
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eq
argument_list|(
name|info
argument_list|(
literal|"S2AvgTime"
argument_list|,
literal|"Average time for stat"
argument_list|)
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|0.0
argument_list|,
name|EPSILON
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mb
argument_list|)
operator|.
name|addCounter
argument_list|(
name|info
argument_list|(
literal|"S2NumOps"
argument_list|,
literal|"Number of ops for stat"
argument_list|)
argument_list|,
literal|2L
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eq
argument_list|(
name|info
argument_list|(
literal|"S2AvgTime"
argument_list|,
literal|"Average time for stat"
argument_list|)
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|1.0
argument_list|,
name|EPSILON
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|interface|TestProtocol
interface|interface
name|TestProtocol
block|{
DECL|method|foo ()
name|void
name|foo
parameter_list|()
function_decl|;
DECL|method|bar ()
name|void
name|bar
parameter_list|()
function_decl|;
block|}
DECL|method|testMutableRates ()
annotation|@
name|Test
specifier|public
name|void
name|testMutableRates
parameter_list|()
block|{
name|MetricsRecordBuilder
name|rb
init|=
name|mockMetricsRecordBuilder
argument_list|()
decl_stmt|;
name|MetricsRegistry
name|registry
init|=
operator|new
name|MetricsRegistry
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|MutableRates
name|rates
init|=
operator|new
name|MutableRates
argument_list|(
name|registry
argument_list|)
decl_stmt|;
name|rates
operator|.
name|init
argument_list|(
name|TestProtocol
operator|.
name|class
argument_list|)
expr_stmt|;
name|registry
operator|.
name|snapshot
argument_list|(
name|rb
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"FooNumOps"
argument_list|,
literal|0L
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"FooAvgTime"
argument_list|,
literal|0.0
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"BarNumOps"
argument_list|,
literal|0L
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"BarAvgTime"
argument_list|,
literal|0.0
argument_list|,
name|rb
argument_list|)
expr_stmt|;
block|}
comment|/**    * Ensure that quantile estimates from {@link MutableQuantiles} are within    * specified error bounds.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testMutableQuantilesError ()
specifier|public
name|void
name|testMutableQuantilesError
parameter_list|()
throws|throws
name|Exception
block|{
name|MetricsRecordBuilder
name|mb
init|=
name|mockMetricsRecordBuilder
argument_list|()
decl_stmt|;
name|MetricsRegistry
name|registry
init|=
operator|new
name|MetricsRegistry
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
comment|// Use a 5s rollover period
name|MutableQuantiles
name|quantiles
init|=
name|registry
operator|.
name|newQuantiles
argument_list|(
literal|"foo"
argument_list|,
literal|"stat"
argument_list|,
literal|"Ops"
argument_list|,
literal|"Latency"
argument_list|,
literal|5
argument_list|)
decl_stmt|;
comment|// Push some values in and wait for it to publish
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|/
literal|1000000
decl_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|quantiles
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|quantiles
operator|.
name|add
argument_list|(
literal|1001
operator|-
name|i
argument_list|)
expr_stmt|;
block|}
name|long
name|end
init|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|/
literal|1000000
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|6000
operator|-
operator|(
name|end
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
name|registry
operator|.
name|snapshot
argument_list|(
name|mb
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Print out the snapshot
name|Map
argument_list|<
name|Quantile
argument_list|,
name|Long
argument_list|>
name|previousSnapshot
init|=
name|quantiles
operator|.
name|previousSnapshot
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|Quantile
argument_list|,
name|Long
argument_list|>
name|item
range|:
name|previousSnapshot
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Quantile %.2f has value %d"
argument_list|,
name|item
operator|.
name|getKey
argument_list|()
operator|.
name|quantile
argument_list|,
name|item
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Verify the results are within our requirements
name|verify
argument_list|(
name|mb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|info
argument_list|(
literal|"FooNumOps"
argument_list|,
literal|"Number of ops for stat with 5s interval"
argument_list|)
argument_list|,
operator|(
name|long
operator|)
literal|2000
argument_list|)
expr_stmt|;
name|Quantile
index|[]
name|quants
init|=
name|MutableQuantiles
operator|.
name|quantiles
decl_stmt|;
name|String
name|name
init|=
literal|"Foo%dthPercentileLatency"
decl_stmt|;
name|String
name|desc
init|=
literal|"%d percentile latency with 5 second interval for stat"
decl_stmt|;
for|for
control|(
name|Quantile
name|q
range|:
name|quants
control|)
block|{
name|int
name|percentile
init|=
call|(
name|int
call|)
argument_list|(
literal|100
operator|*
name|q
operator|.
name|quantile
argument_list|)
decl_stmt|;
name|int
name|error
init|=
call|(
name|int
call|)
argument_list|(
literal|1000
operator|*
name|q
operator|.
name|error
argument_list|)
decl_stmt|;
name|String
name|n
init|=
name|String
operator|.
name|format
argument_list|(
name|name
argument_list|,
name|percentile
argument_list|)
decl_stmt|;
name|String
name|d
init|=
name|String
operator|.
name|format
argument_list|(
name|desc
argument_list|,
name|percentile
argument_list|)
decl_stmt|;
name|long
name|expected
init|=
call|(
name|long
call|)
argument_list|(
name|q
operator|.
name|quantile
operator|*
literal|1000
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|mb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eq
argument_list|(
name|info
argument_list|(
name|n
argument_list|,
name|d
argument_list|)
argument_list|)
argument_list|,
name|leq
argument_list|(
name|expected
operator|+
name|error
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eq
argument_list|(
name|info
argument_list|(
name|n
argument_list|,
name|d
argument_list|)
argument_list|)
argument_list|,
name|geq
argument_list|(
name|expected
operator|-
name|error
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that {@link MutableQuantiles} rolls the window over at the specified    * interval.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testMutableQuantilesRollover ()
specifier|public
name|void
name|testMutableQuantilesRollover
parameter_list|()
throws|throws
name|Exception
block|{
name|MetricsRecordBuilder
name|mb
init|=
name|mockMetricsRecordBuilder
argument_list|()
decl_stmt|;
name|MetricsRegistry
name|registry
init|=
operator|new
name|MetricsRegistry
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
comment|// Use a 5s rollover period
name|MutableQuantiles
name|quantiles
init|=
name|registry
operator|.
name|newQuantiles
argument_list|(
literal|"foo"
argument_list|,
literal|"stat"
argument_list|,
literal|"Ops"
argument_list|,
literal|"Latency"
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|Quantile
index|[]
name|quants
init|=
name|MutableQuantiles
operator|.
name|quantiles
decl_stmt|;
name|String
name|name
init|=
literal|"Foo%dthPercentileLatency"
decl_stmt|;
name|String
name|desc
init|=
literal|"%d percentile latency with 5 second interval for stat"
decl_stmt|;
comment|// Push values for three intervals
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|/
literal|1000000
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
comment|// Insert the values
for|for
control|(
name|long
name|j
init|=
literal|1
init|;
name|j
operator|<=
literal|1000
condition|;
name|j
operator|++
control|)
block|{
name|quantiles
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|// Sleep until 1s after the next 5s interval, to let the metrics
comment|// roll over
name|long
name|sleep
init|=
operator|(
name|start
operator|+
operator|(
literal|5000
operator|*
name|i
operator|)
operator|+
literal|1000
operator|)
operator|-
operator|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|/
literal|1000000
operator|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleep
argument_list|)
expr_stmt|;
comment|// Verify that the window reset, check it has the values we pushed in
name|registry
operator|.
name|snapshot
argument_list|(
name|mb
argument_list|,
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|Quantile
name|q
range|:
name|quants
control|)
block|{
name|int
name|percentile
init|=
call|(
name|int
call|)
argument_list|(
literal|100
operator|*
name|q
operator|.
name|quantile
argument_list|)
decl_stmt|;
name|String
name|n
init|=
name|String
operator|.
name|format
argument_list|(
name|name
argument_list|,
name|percentile
argument_list|)
decl_stmt|;
name|String
name|d
init|=
name|String
operator|.
name|format
argument_list|(
name|desc
argument_list|,
name|percentile
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|mb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|info
argument_list|(
name|n
argument_list|,
name|d
argument_list|)
argument_list|,
operator|(
name|long
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Verify the metrics were added the right number of times
name|verify
argument_list|(
name|mb
argument_list|,
name|times
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|addGauge
argument_list|(
name|info
argument_list|(
literal|"FooNumOps"
argument_list|,
literal|"Number of ops for stat with 5s interval"
argument_list|)
argument_list|,
operator|(
name|long
operator|)
literal|1000
argument_list|)
expr_stmt|;
for|for
control|(
name|Quantile
name|q
range|:
name|quants
control|)
block|{
name|int
name|percentile
init|=
call|(
name|int
call|)
argument_list|(
literal|100
operator|*
name|q
operator|.
name|quantile
argument_list|)
decl_stmt|;
name|String
name|n
init|=
name|String
operator|.
name|format
argument_list|(
name|name
argument_list|,
name|percentile
argument_list|)
decl_stmt|;
name|String
name|d
init|=
name|String
operator|.
name|format
argument_list|(
name|desc
argument_list|,
name|percentile
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|mb
argument_list|,
name|times
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eq
argument_list|(
name|info
argument_list|(
name|n
argument_list|,
name|d
argument_list|)
argument_list|)
argument_list|,
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that {@link MutableQuantiles} rolls over correctly even if no items    * have been added to the window    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testMutableQuantilesEmptyRollover ()
specifier|public
name|void
name|testMutableQuantilesEmptyRollover
parameter_list|()
throws|throws
name|Exception
block|{
name|MetricsRecordBuilder
name|mb
init|=
name|mockMetricsRecordBuilder
argument_list|()
decl_stmt|;
name|MetricsRegistry
name|registry
init|=
operator|new
name|MetricsRegistry
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
comment|// Use a 5s rollover period
name|MutableQuantiles
name|quantiles
init|=
name|registry
operator|.
name|newQuantiles
argument_list|(
literal|"foo"
argument_list|,
literal|"stat"
argument_list|,
literal|"Ops"
argument_list|,
literal|"Latency"
argument_list|,
literal|5
argument_list|)
decl_stmt|;
comment|// Check it initially
name|quantiles
operator|.
name|snapshot
argument_list|(
name|mb
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|info
argument_list|(
literal|"FooNumOps"
argument_list|,
literal|"Number of ops for stat with 5s interval"
argument_list|)
argument_list|,
operator|(
name|long
operator|)
literal|0
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|6000
argument_list|)
expr_stmt|;
name|quantiles
operator|.
name|snapshot
argument_list|(
name|mb
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mb
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|addGauge
argument_list|(
name|info
argument_list|(
literal|"FooNumOps"
argument_list|,
literal|"Number of ops for stat with 5s interval"
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
end_class

end_unit

