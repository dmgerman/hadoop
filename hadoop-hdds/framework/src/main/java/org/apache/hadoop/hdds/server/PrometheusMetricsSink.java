begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|server
package|;
end_package

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
name|io
operator|.
name|Writer
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|MetricType
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
name|MetricsSink
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
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|configuration2
operator|.
name|SubsetConfiguration
import|;
end_import

begin_comment
comment|/**  * Metrics sink for prometheus exporter.  *<p>  * Stores the metric data in-memory and return with it on request.  */
end_comment

begin_class
DECL|class|PrometheusMetricsSink
specifier|public
class|class
name|PrometheusMetricsSink
implements|implements
name|MetricsSink
block|{
comment|/**    * Cached output lines for each metrics.    */
DECL|field|metricLines
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metricLines
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|UPPER_CASE_SEQ
specifier|private
specifier|static
specifier|final
name|Pattern
name|UPPER_CASE_SEQ
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"([A-Z]*)([A-Z])"
argument_list|)
decl_stmt|;
DECL|method|PrometheusMetricsSink ()
specifier|public
name|PrometheusMetricsSink
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|putMetrics (MetricsRecord metricsRecord)
specifier|public
name|void
name|putMetrics
parameter_list|(
name|MetricsRecord
name|metricsRecord
parameter_list|)
block|{
for|for
control|(
name|AbstractMetric
name|metrics
range|:
name|metricsRecord
operator|.
name|metrics
argument_list|()
control|)
block|{
if|if
condition|(
name|metrics
operator|.
name|type
argument_list|()
operator|==
name|MetricType
operator|.
name|COUNTER
operator|||
name|metrics
operator|.
name|type
argument_list|()
operator|==
name|MetricType
operator|.
name|GAUGE
condition|)
block|{
name|String
name|key
init|=
name|prometheusName
argument_list|(
name|metricsRecord
operator|.
name|name
argument_list|()
argument_list|,
name|metrics
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"# TYPE "
operator|+
name|key
operator|+
literal|" "
operator|+
name|metrics
operator|.
name|type
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|key
operator|+
literal|"{"
argument_list|)
expr_stmt|;
name|String
name|sep
init|=
literal|""
decl_stmt|;
comment|//add tags
for|for
control|(
name|MetricsTag
name|tag
range|:
name|metricsRecord
operator|.
name|tags
argument_list|()
control|)
block|{
name|String
name|tagName
init|=
name|tag
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
comment|//ignore specific tag which includes sub-hierarchy
if|if
condition|(
operator|!
name|tagName
operator|.
name|equals
argument_list|(
literal|"numopenconnectionsperuser"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|sep
operator|+
name|tagName
operator|+
literal|"=\""
operator|+
name|tag
operator|.
name|value
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|sep
operator|=
literal|","
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"} "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|metrics
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|metricLines
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Convert CamelCase based namess to lower-case names where the separator    * is the underscore, to follow prometheus naming conventions.    */
DECL|method|prometheusName (String recordName, String metricName)
specifier|public
name|String
name|prometheusName
parameter_list|(
name|String
name|recordName
parameter_list|,
name|String
name|metricName
parameter_list|)
block|{
name|String
name|baseName
init|=
name|upperFirst
argument_list|(
name|recordName
argument_list|)
operator|+
name|upperFirst
argument_list|(
name|metricName
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|UPPER_CASE_SEQ
operator|.
name|matcher
argument_list|(
name|baseName
argument_list|)
decl_stmt|;
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|String
name|replacement
init|=
literal|"_"
operator|+
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|replacement
operator|=
literal|"_"
operator|+
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|toLowerCase
argument_list|()
operator|+
name|replacement
expr_stmt|;
block|}
name|m
operator|.
name|appendReplacement
argument_list|(
name|sb
argument_list|,
name|replacement
argument_list|)
expr_stmt|;
block|}
name|m
operator|.
name|appendTail
argument_list|(
name|sb
argument_list|)
expr_stmt|;
comment|//always prefixed with "_"
return|return
name|sb
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
return|;
block|}
DECL|method|upperFirst (String name)
specifier|private
name|String
name|upperFirst
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|Character
operator|.
name|isLowerCase
argument_list|(
name|name
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|Character
operator|.
name|toUpperCase
argument_list|(
name|name
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|+
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|name
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
block|{    }
annotation|@
name|Override
DECL|method|init (SubsetConfiguration subsetConfiguration)
specifier|public
name|void
name|init
parameter_list|(
name|SubsetConfiguration
name|subsetConfiguration
parameter_list|)
block|{    }
DECL|method|writeMetrics (Writer writer)
specifier|public
name|void
name|writeMetrics
parameter_list|(
name|Writer
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|line
range|:
name|metricLines
operator|.
name|values
argument_list|()
control|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|line
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

