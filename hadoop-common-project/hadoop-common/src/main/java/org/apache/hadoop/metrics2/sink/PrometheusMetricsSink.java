begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.sink
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
package|;
end_package

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
name|ConcurrentHashMap
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
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
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
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metricLines
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|SPLIT_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|SPLIT_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?<!(^|[A-Z_]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"
argument_list|)
decl_stmt|;
DECL|field|DELIMITERS
specifier|private
specifier|static
specifier|final
name|Pattern
name|DELIMITERS
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[^a-zA-Z0-9]+"
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
argument_list|)
operator|.
name|append
argument_list|(
name|key
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
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
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
operator|.
name|append
argument_list|(
name|key
argument_list|)
operator|.
name|append
argument_list|(
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
argument_list|)
operator|.
name|append
argument_list|(
name|tagName
argument_list|)
operator|.
name|append
argument_list|(
literal|"=\""
argument_list|)
operator|.
name|append
argument_list|(
name|tag
operator|.
name|value
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
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
name|builder
operator|.
name|append
argument_list|(
literal|"\n"
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
comment|/**    * Convert CamelCase based names to lower-case names where the separator    * is the underscore, to follow prometheus naming conventions.    */
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
name|StringUtils
operator|.
name|capitalize
argument_list|(
name|recordName
argument_list|)
operator|+
name|StringUtils
operator|.
name|capitalize
argument_list|(
name|metricName
argument_list|)
decl_stmt|;
name|String
index|[]
name|parts
init|=
name|SPLIT_PATTERN
operator|.
name|split
argument_list|(
name|baseName
argument_list|)
decl_stmt|;
name|String
name|joined
init|=
name|String
operator|.
name|join
argument_list|(
literal|"_"
argument_list|,
name|parts
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
return|return
name|DELIMITERS
operator|.
name|matcher
argument_list|(
name|joined
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"_"
argument_list|)
return|;
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
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

