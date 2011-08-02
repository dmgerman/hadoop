begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.sink.ganglia
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|sink
operator|.
name|ganglia
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
name|MetricsVisitor
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
name|sink
operator|.
name|ganglia
operator|.
name|AbstractGangliaSink
operator|.
name|GangliaSlope
import|;
end_import

begin_comment
comment|/**  * Since implementations of Metric are not public, hence use a visitor to figure  * out the type and slope of the metric. Counters have "positive" slope.  */
end_comment

begin_class
DECL|class|GangliaMetricVisitor
class|class
name|GangliaMetricVisitor
implements|implements
name|MetricsVisitor
block|{
DECL|field|INT32
specifier|private
specifier|static
specifier|final
name|String
name|INT32
init|=
literal|"int32"
decl_stmt|;
DECL|field|FLOAT
specifier|private
specifier|static
specifier|final
name|String
name|FLOAT
init|=
literal|"float"
decl_stmt|;
DECL|field|DOUBLE
specifier|private
specifier|static
specifier|final
name|String
name|DOUBLE
init|=
literal|"double"
decl_stmt|;
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
DECL|field|slope
specifier|private
name|GangliaSlope
name|slope
decl_stmt|;
comment|/**    * @return the type of a visited metric    */
DECL|method|getType ()
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**    * @return the slope of a visited metric. Slope is positive for counters and    *         null for others    */
DECL|method|getSlope ()
name|GangliaSlope
name|getSlope
parameter_list|()
block|{
return|return
name|slope
return|;
block|}
annotation|@
name|Override
DECL|method|gauge (MetricsInfo info, int value)
specifier|public
name|void
name|gauge
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|int
name|value
parameter_list|)
block|{
comment|// MetricGaugeInt.class ==> "int32"
name|type
operator|=
name|INT32
expr_stmt|;
name|slope
operator|=
literal|null
expr_stmt|;
comment|// set to null as cannot figure out from Metric
block|}
annotation|@
name|Override
DECL|method|gauge (MetricsInfo info, long value)
specifier|public
name|void
name|gauge
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|long
name|value
parameter_list|)
block|{
comment|// MetricGaugeLong.class ==> "float"
name|type
operator|=
name|FLOAT
expr_stmt|;
name|slope
operator|=
literal|null
expr_stmt|;
comment|// set to null as cannot figure out from Metric
block|}
annotation|@
name|Override
DECL|method|gauge (MetricsInfo info, float value)
specifier|public
name|void
name|gauge
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|float
name|value
parameter_list|)
block|{
comment|// MetricGaugeFloat.class ==> "float"
name|type
operator|=
name|FLOAT
expr_stmt|;
name|slope
operator|=
literal|null
expr_stmt|;
comment|// set to null as cannot figure out from Metric
block|}
annotation|@
name|Override
DECL|method|gauge (MetricsInfo info, double value)
specifier|public
name|void
name|gauge
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|double
name|value
parameter_list|)
block|{
comment|// MetricGaugeDouble.class ==> "double"
name|type
operator|=
name|DOUBLE
expr_stmt|;
name|slope
operator|=
literal|null
expr_stmt|;
comment|// set to null as cannot figure out from Metric
block|}
annotation|@
name|Override
DECL|method|counter (MetricsInfo info, int value)
specifier|public
name|void
name|counter
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|int
name|value
parameter_list|)
block|{
comment|// MetricCounterInt.class ==> "int32"
name|type
operator|=
name|INT32
expr_stmt|;
comment|// counters have positive slope
name|slope
operator|=
name|GangliaSlope
operator|.
name|positive
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|counter (MetricsInfo info, long value)
specifier|public
name|void
name|counter
parameter_list|(
name|MetricsInfo
name|info
parameter_list|,
name|long
name|value
parameter_list|)
block|{
comment|// MetricCounterLong.class ==> "float"
name|type
operator|=
name|FLOAT
expr_stmt|;
comment|// counters have positive slope
name|slope
operator|=
name|GangliaSlope
operator|.
name|positive
expr_stmt|;
block|}
block|}
end_class

end_unit

