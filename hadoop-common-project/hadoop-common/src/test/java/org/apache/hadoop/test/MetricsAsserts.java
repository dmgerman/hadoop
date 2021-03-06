begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|Assert
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
name|Mockito
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentCaptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatcher
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
name|MetricsInfo
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
name|MetricsSource
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
name|lib
operator|.
name|MutableQuantiles
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|*
import|;
end_import

begin_comment
comment|/**  * Helpers for metrics source tests  */
end_comment

begin_class
DECL|class|MetricsAsserts
specifier|public
class|class
name|MetricsAsserts
block|{
DECL|field|LOG
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MetricsAsserts
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|EPSILON
specifier|private
specifier|static
specifier|final
name|double
name|EPSILON
init|=
literal|0.00001
decl_stmt|;
DECL|method|mockMetricsSystem ()
specifier|public
specifier|static
name|MetricsSystem
name|mockMetricsSystem
parameter_list|()
block|{
name|MetricsSystem
name|ms
init|=
name|mock
argument_list|(
name|MetricsSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|DefaultMetricsSystem
operator|.
name|setInstance
argument_list|(
name|ms
argument_list|)
expr_stmt|;
return|return
name|ms
return|;
block|}
DECL|method|mockMetricsRecordBuilder ()
specifier|public
specifier|static
name|MetricsRecordBuilder
name|mockMetricsRecordBuilder
parameter_list|()
block|{
specifier|final
name|MetricsCollector
name|mc
init|=
name|mock
argument_list|(
name|MetricsCollector
operator|.
name|class
argument_list|)
decl_stmt|;
name|MetricsRecordBuilder
name|rb
init|=
name|mock
argument_list|(
name|MetricsRecordBuilder
operator|.
name|class
argument_list|,
operator|new
name|Answer
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
block|{
name|Object
index|[]
name|args
init|=
name|invocation
operator|.
name|getArguments
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|args
control|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|methodName
init|=
name|invocation
operator|.
name|getMethod
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|methodName
operator|+
literal|": "
operator|+
name|sb
argument_list|)
expr_stmt|;
return|return
name|methodName
operator|.
name|equals
argument_list|(
literal|"parent"
argument_list|)
operator|||
name|methodName
operator|.
name|equals
argument_list|(
literal|"endRecord"
argument_list|)
condition|?
name|mc
else|:
name|invocation
operator|.
name|getMock
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mc
operator|.
name|addRecord
argument_list|(
name|anyString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rb
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mc
operator|.
name|addRecord
argument_list|(
name|anyInfo
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rb
argument_list|)
expr_stmt|;
return|return
name|rb
return|;
block|}
comment|/**    * Call getMetrics on source and get a record builder mock to verify    * @param source  the metrics source    * @param all     if true, return all metrics even if not changed    * @return the record builder mock to verifyÃ    */
DECL|method|getMetrics (MetricsSource source, boolean all)
specifier|public
specifier|static
name|MetricsRecordBuilder
name|getMetrics
parameter_list|(
name|MetricsSource
name|source
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|MetricsRecordBuilder
name|rb
init|=
name|mockMetricsRecordBuilder
argument_list|()
decl_stmt|;
name|MetricsCollector
name|mc
init|=
name|rb
operator|.
name|parent
argument_list|()
decl_stmt|;
name|source
operator|.
name|getMetrics
argument_list|(
name|mc
argument_list|,
name|all
argument_list|)
expr_stmt|;
return|return
name|rb
return|;
block|}
DECL|method|getMetrics (String name)
specifier|public
specifier|static
name|MetricsRecordBuilder
name|getMetrics
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getMetrics
argument_list|(
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
operator|.
name|getSource
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getMetrics (MetricsSource source)
specifier|public
specifier|static
name|MetricsRecordBuilder
name|getMetrics
parameter_list|(
name|MetricsSource
name|source
parameter_list|)
block|{
return|return
name|getMetrics
argument_list|(
name|source
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|class|InfoWithSameName
specifier|private
specifier|static
class|class
name|InfoWithSameName
implements|implements
name|ArgumentMatcher
argument_list|<
name|MetricsInfo
argument_list|>
block|{
DECL|field|expected
specifier|private
specifier|final
name|String
name|expected
decl_stmt|;
DECL|method|InfoWithSameName (MetricsInfo info)
name|InfoWithSameName
parameter_list|(
name|MetricsInfo
name|info
parameter_list|)
block|{
name|expected
operator|=
name|checkNotNull
argument_list|(
name|info
operator|.
name|name
argument_list|()
argument_list|,
literal|"info name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|matches (MetricsInfo info)
specifier|public
name|boolean
name|matches
parameter_list|(
name|MetricsInfo
name|info
parameter_list|)
block|{
return|return
name|expected
operator|.
name|equals
argument_list|(
name|info
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Info with name="
operator|+
name|expected
return|;
block|}
block|}
comment|/**    * MetricInfo with the same name    * @param info to match    * @return<code>null</code>    */
DECL|method|eqName (MetricsInfo info)
specifier|public
specifier|static
name|MetricsInfo
name|eqName
parameter_list|(
name|MetricsInfo
name|info
parameter_list|)
block|{
return|return
name|argThat
argument_list|(
operator|new
name|InfoWithSameName
argument_list|(
name|info
argument_list|)
argument_list|)
return|;
block|}
DECL|class|AnyInfo
specifier|private
specifier|static
class|class
name|AnyInfo
implements|implements
name|ArgumentMatcher
argument_list|<
name|MetricsInfo
argument_list|>
block|{
annotation|@
name|Override
DECL|method|matches (MetricsInfo info)
specifier|public
name|boolean
name|matches
parameter_list|(
name|MetricsInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|!=
literal|null
return|;
block|}
block|}
DECL|method|anyInfo ()
specifier|public
specifier|static
name|MetricsInfo
name|anyInfo
parameter_list|()
block|{
return|return
name|argThat
argument_list|(
operator|new
name|AnyInfo
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Assert an int gauge metric as expected    * @param name  of the metric    * @param expected  value of the metric    * @param rb  the record builder mock used to getMetrics    */
DECL|method|assertGauge (String name, int expected, MetricsRecordBuilder rb)
specifier|public
specifier|static
name|void
name|assertGauge
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|expected
parameter_list|,
name|MetricsRecordBuilder
name|rb
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Bad value for metric "
operator|+
name|name
argument_list|,
name|expected
argument_list|,
name|getIntGauge
argument_list|(
name|name
argument_list|,
name|rb
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getIntGauge (String name, MetricsRecordBuilder rb)
specifier|public
specifier|static
name|int
name|getIntGauge
parameter_list|(
name|String
name|name
parameter_list|,
name|MetricsRecordBuilder
name|rb
parameter_list|)
block|{
name|ArgumentCaptor
argument_list|<
name|Integer
argument_list|>
name|captor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|rb
argument_list|,
name|atLeast
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eqName
argument_list|(
name|info
argument_list|(
name|name
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|,
name|captor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|checkCaptured
argument_list|(
name|captor
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|captor
operator|.
name|getValue
argument_list|()
return|;
block|}
comment|/**    * Assert an int counter metric as expected    * @param name  of the metric    * @param expected  value of the metric    * @param rb  the record builder mock used to getMetrics    */
DECL|method|assertCounter (String name, int expected, MetricsRecordBuilder rb)
specifier|public
specifier|static
name|void
name|assertCounter
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|expected
parameter_list|,
name|MetricsRecordBuilder
name|rb
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Bad value for metric "
operator|+
name|name
argument_list|,
name|expected
argument_list|,
name|getIntCounter
argument_list|(
name|name
argument_list|,
name|rb
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getIntCounter (String name, MetricsRecordBuilder rb)
specifier|public
specifier|static
name|int
name|getIntCounter
parameter_list|(
name|String
name|name
parameter_list|,
name|MetricsRecordBuilder
name|rb
parameter_list|)
block|{
name|ArgumentCaptor
argument_list|<
name|Integer
argument_list|>
name|captor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|rb
argument_list|,
name|atLeast
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|addCounter
argument_list|(
name|eqName
argument_list|(
name|info
argument_list|(
name|name
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|,
name|captor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|checkCaptured
argument_list|(
name|captor
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|captor
operator|.
name|getValue
argument_list|()
return|;
block|}
comment|/**    * Assert a long gauge metric as expected    * @param name  of the metric    * @param expected  value of the metric    * @param rb  the record builder mock used to getMetrics    */
DECL|method|assertGauge (String name, long expected, MetricsRecordBuilder rb)
specifier|public
specifier|static
name|void
name|assertGauge
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|expected
parameter_list|,
name|MetricsRecordBuilder
name|rb
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Bad value for metric "
operator|+
name|name
argument_list|,
name|expected
argument_list|,
name|getLongGauge
argument_list|(
name|name
argument_list|,
name|rb
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getLongGauge (String name, MetricsRecordBuilder rb)
specifier|public
specifier|static
name|long
name|getLongGauge
parameter_list|(
name|String
name|name
parameter_list|,
name|MetricsRecordBuilder
name|rb
parameter_list|)
block|{
name|ArgumentCaptor
argument_list|<
name|Long
argument_list|>
name|captor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|Long
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|rb
argument_list|,
name|atLeast
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eqName
argument_list|(
name|info
argument_list|(
name|name
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|,
name|captor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|checkCaptured
argument_list|(
name|captor
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|captor
operator|.
name|getValue
argument_list|()
return|;
block|}
comment|/**    * Assert a double gauge metric as expected    * @param name  of the metric    * @param expected  value of the metric    * @param rb  the record builder mock used to getMetrics    */
DECL|method|assertGauge (String name, double expected, MetricsRecordBuilder rb)
specifier|public
specifier|static
name|void
name|assertGauge
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|expected
parameter_list|,
name|MetricsRecordBuilder
name|rb
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Bad value for metric "
operator|+
name|name
argument_list|,
name|expected
argument_list|,
name|getDoubleGauge
argument_list|(
name|name
argument_list|,
name|rb
argument_list|)
argument_list|,
name|EPSILON
argument_list|)
expr_stmt|;
block|}
DECL|method|getDoubleGauge (String name, MetricsRecordBuilder rb)
specifier|public
specifier|static
name|double
name|getDoubleGauge
parameter_list|(
name|String
name|name
parameter_list|,
name|MetricsRecordBuilder
name|rb
parameter_list|)
block|{
name|ArgumentCaptor
argument_list|<
name|Double
argument_list|>
name|captor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|Double
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|rb
argument_list|,
name|atLeast
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eqName
argument_list|(
name|info
argument_list|(
name|name
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|,
name|captor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|checkCaptured
argument_list|(
name|captor
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|captor
operator|.
name|getValue
argument_list|()
return|;
block|}
comment|/**    * Assert a long counter metric as expected    * @param name  of the metric    * @param expected  value of the metric    * @param rb  the record builder mock used to getMetrics    */
DECL|method|assertCounter (String name, long expected, MetricsRecordBuilder rb)
specifier|public
specifier|static
name|void
name|assertCounter
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|expected
parameter_list|,
name|MetricsRecordBuilder
name|rb
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Bad value for metric "
operator|+
name|name
argument_list|,
name|expected
argument_list|,
name|getLongCounter
argument_list|(
name|name
argument_list|,
name|rb
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getLongCounter (String name, MetricsRecordBuilder rb)
specifier|public
specifier|static
name|long
name|getLongCounter
parameter_list|(
name|String
name|name
parameter_list|,
name|MetricsRecordBuilder
name|rb
parameter_list|)
block|{
name|ArgumentCaptor
argument_list|<
name|Long
argument_list|>
name|captor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|Long
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|rb
argument_list|,
name|atLeast
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|addCounter
argument_list|(
name|eqName
argument_list|(
name|info
argument_list|(
name|name
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|,
name|captor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|checkCaptured
argument_list|(
name|captor
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|captor
operator|.
name|getValue
argument_list|()
return|;
block|}
DECL|method|getLongCounterWithoutCheck (String name, MetricsRecordBuilder rb)
specifier|public
specifier|static
name|long
name|getLongCounterWithoutCheck
parameter_list|(
name|String
name|name
parameter_list|,
name|MetricsRecordBuilder
name|rb
parameter_list|)
block|{
name|ArgumentCaptor
argument_list|<
name|Long
argument_list|>
name|captor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|Long
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|rb
argument_list|,
name|atLeast
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|addCounter
argument_list|(
name|eqName
argument_list|(
name|info
argument_list|(
name|name
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|,
name|captor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|captor
operator|.
name|getValue
argument_list|()
return|;
block|}
DECL|method|getStringMetric (String name, MetricsRecordBuilder rb)
specifier|public
specifier|static
name|String
name|getStringMetric
parameter_list|(
name|String
name|name
parameter_list|,
name|MetricsRecordBuilder
name|rb
parameter_list|)
block|{
name|ArgumentCaptor
argument_list|<
name|String
argument_list|>
name|captor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|rb
argument_list|,
name|atLeast
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|tag
argument_list|(
name|eqName
argument_list|(
name|info
argument_list|(
name|name
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|,
name|captor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|checkCaptured
argument_list|(
name|captor
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|captor
operator|.
name|getValue
argument_list|()
return|;
block|}
comment|/**    * Assert a float gauge metric as expected    * @param name  of the metric    * @param expected  value of the metric    * @param rb  the record builder mock used to getMetrics    */
DECL|method|assertGauge (String name, float expected, MetricsRecordBuilder rb)
specifier|public
specifier|static
name|void
name|assertGauge
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|expected
parameter_list|,
name|MetricsRecordBuilder
name|rb
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Bad value for metric "
operator|+
name|name
argument_list|,
name|expected
argument_list|,
name|getFloatGauge
argument_list|(
name|name
argument_list|,
name|rb
argument_list|)
argument_list|,
name|EPSILON
argument_list|)
expr_stmt|;
block|}
DECL|method|getFloatGauge (String name, MetricsRecordBuilder rb)
specifier|public
specifier|static
name|float
name|getFloatGauge
parameter_list|(
name|String
name|name
parameter_list|,
name|MetricsRecordBuilder
name|rb
parameter_list|)
block|{
name|ArgumentCaptor
argument_list|<
name|Float
argument_list|>
name|captor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|Float
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|rb
argument_list|,
name|atLeast
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eqName
argument_list|(
name|info
argument_list|(
name|name
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|,
name|captor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|checkCaptured
argument_list|(
name|captor
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|captor
operator|.
name|getValue
argument_list|()
return|;
block|}
comment|/**    * Check that this metric was captured exactly once.    */
DECL|method|checkCaptured (ArgumentCaptor<?> captor, String name)
specifier|private
specifier|static
name|void
name|checkCaptured
parameter_list|(
name|ArgumentCaptor
argument_list|<
name|?
argument_list|>
name|captor
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Expected exactly one metric for name "
operator|+
name|name
argument_list|,
literal|1
argument_list|,
name|captor
operator|.
name|getAllValues
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert an int gauge metric as expected    * @param name  of the metric    * @param expected  value of the metric    * @param source  to get metrics from    */
DECL|method|assertGauge (String name, int expected, MetricsSource source)
specifier|public
specifier|static
name|void
name|assertGauge
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|expected
parameter_list|,
name|MetricsSource
name|source
parameter_list|)
block|{
name|assertGauge
argument_list|(
name|name
argument_list|,
name|expected
argument_list|,
name|getMetrics
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert an int counter metric as expected    * @param name  of the metric    * @param expected  value of the metric    * @param source  to get metrics from    */
DECL|method|assertCounter (String name, int expected, MetricsSource source)
specifier|public
specifier|static
name|void
name|assertCounter
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|expected
parameter_list|,
name|MetricsSource
name|source
parameter_list|)
block|{
name|assertCounter
argument_list|(
name|name
argument_list|,
name|expected
argument_list|,
name|getMetrics
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert a long gauge metric as expected    * @param name  of the metric    * @param expected  value of the metric    * @param source  to get metrics from    */
DECL|method|assertGauge (String name, long expected, MetricsSource source)
specifier|public
specifier|static
name|void
name|assertGauge
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|expected
parameter_list|,
name|MetricsSource
name|source
parameter_list|)
block|{
name|assertGauge
argument_list|(
name|name
argument_list|,
name|expected
argument_list|,
name|getMetrics
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert a long counter metric as expected    * @param name  of the metric    * @param expected  value of the metric    * @param source  to get metrics from    */
DECL|method|assertCounter (String name, long expected, MetricsSource source)
specifier|public
specifier|static
name|void
name|assertCounter
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|expected
parameter_list|,
name|MetricsSource
name|source
parameter_list|)
block|{
name|assertCounter
argument_list|(
name|name
argument_list|,
name|expected
argument_list|,
name|getMetrics
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that a long counter metric is greater than a value    * @param name  of the metric    * @param greater value of the metric should be greater than this    * @param rb  the record builder mock used to getMetrics    */
DECL|method|assertCounterGt (String name, long greater, MetricsRecordBuilder rb)
specifier|public
specifier|static
name|void
name|assertCounterGt
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|greater
parameter_list|,
name|MetricsRecordBuilder
name|rb
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Bad value for metric "
operator|+
name|name
argument_list|,
name|getLongCounter
argument_list|(
name|name
argument_list|,
name|rb
argument_list|)
operator|>
name|greater
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that a long counter metric is greater than a value    * @param name  of the metric    * @param greater value of the metric should be greater than this    * @param source  the metrics source    */
DECL|method|assertCounterGt (String name, long greater, MetricsSource source)
specifier|public
specifier|static
name|void
name|assertCounterGt
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|greater
parameter_list|,
name|MetricsSource
name|source
parameter_list|)
block|{
name|assertCounterGt
argument_list|(
name|name
argument_list|,
name|greater
argument_list|,
name|getMetrics
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that a double gauge metric is greater than a value    * @param name  of the metric    * @param greater value of the metric should be greater than this    * @param rb  the record builder mock used to getMetrics    */
DECL|method|assertGaugeGt (String name, double greater, MetricsRecordBuilder rb)
specifier|public
specifier|static
name|void
name|assertGaugeGt
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|greater
parameter_list|,
name|MetricsRecordBuilder
name|rb
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Bad value for metric "
operator|+
name|name
argument_list|,
name|getDoubleGauge
argument_list|(
name|name
argument_list|,
name|rb
argument_list|)
operator|>
name|greater
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that a double gauge metric is greater than a value    * @param name  of the metric    * @param greater value of the metric should be greater than this    * @param source  the metrics source    */
DECL|method|assertGaugeGt (String name, double greater, MetricsSource source)
specifier|public
specifier|static
name|void
name|assertGaugeGt
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|greater
parameter_list|,
name|MetricsSource
name|source
parameter_list|)
block|{
name|assertGaugeGt
argument_list|(
name|name
argument_list|,
name|greater
argument_list|,
name|getMetrics
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Asserts that the NumOps and quantiles for a metric with value name    * "Latency" have been changed at some point to a non-zero value.    *     * @param prefix of the metric    * @param rb MetricsRecordBuilder with the metric    */
DECL|method|assertQuantileGauges (String prefix, MetricsRecordBuilder rb)
specifier|public
specifier|static
name|void
name|assertQuantileGauges
parameter_list|(
name|String
name|prefix
parameter_list|,
name|MetricsRecordBuilder
name|rb
parameter_list|)
block|{
name|assertQuantileGauges
argument_list|(
name|prefix
argument_list|,
name|rb
argument_list|,
literal|"Latency"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Asserts that the NumOps and quantiles for a metric have been changed at    * some point to a non-zero value, for the specified value name of the    * metrics (e.g., "Latency", "Count").    *    * @param prefix of the metric    * @param rb MetricsRecordBuilder with the metric    * @param valueName the value name for the metric    */
DECL|method|assertQuantileGauges (String prefix, MetricsRecordBuilder rb, String valueName)
specifier|public
specifier|static
name|void
name|assertQuantileGauges
parameter_list|(
name|String
name|prefix
parameter_list|,
name|MetricsRecordBuilder
name|rb
parameter_list|,
name|String
name|valueName
parameter_list|)
block|{
name|verify
argument_list|(
name|rb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eqName
argument_list|(
name|info
argument_list|(
name|prefix
operator|+
literal|"NumOps"
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|,
name|geq
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Quantile
name|q
range|:
name|MutableQuantiles
operator|.
name|quantiles
control|)
block|{
name|String
name|nameTemplate
init|=
name|prefix
operator|+
literal|"%dthPercentile"
operator|+
name|valueName
decl_stmt|;
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
name|verify
argument_list|(
name|rb
argument_list|)
operator|.
name|addGauge
argument_list|(
name|eqName
argument_list|(
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|nameTemplate
argument_list|,
name|percentile
argument_list|)
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|,
name|geq
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

