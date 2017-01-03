begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|impl
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
name|Predicate
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|MetricsTag
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

begin_comment
comment|/**  * Utility class mainly for tests  */
end_comment

begin_class
DECL|class|MetricsRecords
specifier|public
class|class
name|MetricsRecords
block|{
DECL|method|assertTag (MetricsRecord record, String tagName, String expectedValue)
specifier|public
specifier|static
name|void
name|assertTag
parameter_list|(
name|MetricsRecord
name|record
parameter_list|,
name|String
name|tagName
parameter_list|,
name|String
name|expectedValue
parameter_list|)
block|{
name|MetricsTag
name|processIdTag
init|=
name|getFirstTagByName
argument_list|(
name|record
argument_list|,
name|tagName
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|processIdTag
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedValue
argument_list|,
name|processIdTag
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertMetric (MetricsRecord record, String metricName, Number expectedValue)
specifier|public
specifier|static
name|void
name|assertMetric
parameter_list|(
name|MetricsRecord
name|record
parameter_list|,
name|String
name|metricName
parameter_list|,
name|Number
name|expectedValue
parameter_list|)
block|{
name|AbstractMetric
name|resourceLimitMetric
init|=
name|getFirstMetricByName
argument_list|(
name|record
argument_list|,
name|metricName
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|resourceLimitMetric
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedValue
argument_list|,
name|resourceLimitMetric
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getMetricValueByName (MetricsRecord record, String metricName)
specifier|public
specifier|static
name|Number
name|getMetricValueByName
parameter_list|(
name|MetricsRecord
name|record
parameter_list|,
name|String
name|metricName
parameter_list|)
block|{
name|AbstractMetric
name|resourceLimitMetric
init|=
name|getFirstMetricByName
argument_list|(
name|record
argument_list|,
name|metricName
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|resourceLimitMetric
argument_list|)
expr_stmt|;
return|return
name|resourceLimitMetric
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|assertMetricNotNull (MetricsRecord record, String metricName)
specifier|public
specifier|static
name|void
name|assertMetricNotNull
parameter_list|(
name|MetricsRecord
name|record
parameter_list|,
name|String
name|metricName
parameter_list|)
block|{
name|AbstractMetric
name|resourceLimitMetric
init|=
name|getFirstMetricByName
argument_list|(
name|record
argument_list|,
name|metricName
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Metric "
operator|+
name|metricName
operator|+
literal|" doesn't exist"
argument_list|,
name|resourceLimitMetric
argument_list|)
expr_stmt|;
block|}
DECL|method|getFirstTagByName (MetricsRecord record, String name)
specifier|private
specifier|static
name|MetricsTag
name|getFirstTagByName
parameter_list|(
name|MetricsRecord
name|record
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|Iterables
operator|.
name|getFirst
argument_list|(
name|Iterables
operator|.
name|filter
argument_list|(
name|record
operator|.
name|tags
argument_list|()
argument_list|,
operator|new
name|MetricsTagPredicate
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getFirstMetricByName ( MetricsRecord record, String name)
specifier|private
specifier|static
name|AbstractMetric
name|getFirstMetricByName
parameter_list|(
name|MetricsRecord
name|record
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|Iterables
operator|.
name|getFirst
argument_list|(
name|Iterables
operator|.
name|filter
argument_list|(
name|record
operator|.
name|metrics
argument_list|()
argument_list|,
operator|new
name|AbstractMetricPredicate
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|class|MetricsTagPredicate
specifier|private
specifier|static
class|class
name|MetricsTagPredicate
implements|implements
name|Predicate
argument_list|<
name|MetricsTag
argument_list|>
block|{
DECL|field|tagName
specifier|private
name|String
name|tagName
decl_stmt|;
DECL|method|MetricsTagPredicate (String tagName)
specifier|public
name|MetricsTagPredicate
parameter_list|(
name|String
name|tagName
parameter_list|)
block|{
name|this
operator|.
name|tagName
operator|=
name|tagName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (MetricsTag input)
specifier|public
name|boolean
name|apply
parameter_list|(
name|MetricsTag
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|tagName
argument_list|)
return|;
block|}
block|}
DECL|class|AbstractMetricPredicate
specifier|private
specifier|static
class|class
name|AbstractMetricPredicate
implements|implements
name|Predicate
argument_list|<
name|AbstractMetric
argument_list|>
block|{
DECL|field|metricName
specifier|private
name|String
name|metricName
decl_stmt|;
DECL|method|AbstractMetricPredicate ( String metricName)
specifier|public
name|AbstractMetricPredicate
parameter_list|(
name|String
name|metricName
parameter_list|)
block|{
name|this
operator|.
name|metricName
operator|=
name|metricName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (AbstractMetric input)
specifier|public
name|boolean
name|apply
parameter_list|(
name|AbstractMetric
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|metricName
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

