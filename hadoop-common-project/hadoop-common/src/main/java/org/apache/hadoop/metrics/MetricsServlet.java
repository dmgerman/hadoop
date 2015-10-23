begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics
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
name|PrintWriter
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServlet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|http
operator|.
name|HttpServer2
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
name|metrics
operator|.
name|spi
operator|.
name|OutputRecord
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
name|metrics
operator|.
name|spi
operator|.
name|AbstractMetricsContext
operator|.
name|MetricMap
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
name|metrics
operator|.
name|spi
operator|.
name|AbstractMetricsContext
operator|.
name|TagMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|util
operator|.
name|ajax
operator|.
name|JSON
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|util
operator|.
name|ajax
operator|.
name|JSON
operator|.
name|Output
import|;
end_import

begin_comment
comment|/**  * A servlet to print out metrics data.  By default, the servlet returns a   * textual representation (no promises are made for parseability), and  * users can use "?format=json" for parseable output.  *  * @deprecated Use org.apache.hadoop.metrics2 package instead.  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|MetricsServlet
specifier|public
class|class
name|MetricsServlet
extends|extends
name|HttpServlet
block|{
comment|/**    * A helper class to hold a TagMap and MetricMap.    */
DECL|class|TagsMetricsPair
specifier|static
class|class
name|TagsMetricsPair
implements|implements
name|JSON
operator|.
name|Convertible
block|{
DECL|field|tagMap
specifier|final
name|TagMap
name|tagMap
decl_stmt|;
DECL|field|metricMap
specifier|final
name|MetricMap
name|metricMap
decl_stmt|;
DECL|method|TagsMetricsPair (TagMap tagMap, MetricMap metricMap)
specifier|public
name|TagsMetricsPair
parameter_list|(
name|TagMap
name|tagMap
parameter_list|,
name|MetricMap
name|metricMap
parameter_list|)
block|{
name|this
operator|.
name|tagMap
operator|=
name|tagMap
expr_stmt|;
name|this
operator|.
name|metricMap
operator|=
name|metricMap
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|fromJSON (Map map)
specifier|public
name|void
name|fromJSON
parameter_list|(
name|Map
name|map
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** Converts to JSON by providing an array. */
annotation|@
name|Override
DECL|method|toJSON (Output out)
specifier|public
name|void
name|toJSON
parameter_list|(
name|Output
name|out
parameter_list|)
block|{
name|out
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|tagMap
block|,
name|metricMap
block|}
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Collects all metric data, and returns a map:    *   contextName -> recordName -> [ (tag->tagValue), (metric->metricValue) ].    * The values are either String or Number.  The final value is implemented    * as a list of TagsMetricsPair.    */
DECL|method|makeMap ( Collection<MetricsContext> contexts)
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|TagsMetricsPair
argument_list|>
argument_list|>
argument_list|>
name|makeMap
parameter_list|(
name|Collection
argument_list|<
name|MetricsContext
argument_list|>
name|contexts
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|TagsMetricsPair
argument_list|>
argument_list|>
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|TagsMetricsPair
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|MetricsContext
name|context
range|:
name|contexts
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|TagsMetricsPair
argument_list|>
argument_list|>
name|records
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|TagsMetricsPair
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|context
operator|.
name|getContextName
argument_list|()
argument_list|,
name|records
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|OutputRecord
argument_list|>
argument_list|>
name|r
range|:
name|context
operator|.
name|getAllRecords
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|TagsMetricsPair
argument_list|>
name|metricsAndTags
init|=
operator|new
name|ArrayList
argument_list|<
name|TagsMetricsPair
argument_list|>
argument_list|()
decl_stmt|;
name|records
operator|.
name|put
argument_list|(
name|r
operator|.
name|getKey
argument_list|()
argument_list|,
name|metricsAndTags
argument_list|)
expr_stmt|;
for|for
control|(
name|OutputRecord
name|outputRecord
range|:
name|r
operator|.
name|getValue
argument_list|()
control|)
block|{
name|TagMap
name|tagMap
init|=
name|outputRecord
operator|.
name|getTagsCopy
argument_list|()
decl_stmt|;
name|MetricMap
name|metricMap
init|=
name|outputRecord
operator|.
name|getMetricsCopy
argument_list|()
decl_stmt|;
name|metricsAndTags
operator|.
name|add
argument_list|(
operator|new
name|TagsMetricsPair
argument_list|(
name|tagMap
argument_list|,
name|metricMap
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|map
return|;
block|}
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest request, HttpServletResponse response)
specifier|public
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
if|if
condition|(
operator|!
name|HttpServer2
operator|.
name|isInstrumentationAccessAllowed
argument_list|(
name|getServletContext
argument_list|()
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
condition|)
block|{
return|return;
block|}
name|String
name|format
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"format"
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|MetricsContext
argument_list|>
name|allContexts
init|=
name|ContextFactory
operator|.
name|getFactory
argument_list|()
operator|.
name|getAllContexts
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"json"
operator|.
name|equals
argument_list|(
name|format
argument_list|)
condition|)
block|{
name|response
operator|.
name|setContentType
argument_list|(
literal|"application/json; charset=utf-8"
argument_list|)
expr_stmt|;
name|PrintWriter
name|out
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
try|try
block|{
comment|// Uses Jetty's built-in JSON support to convert the map into JSON.
name|out
operator|.
name|print
argument_list|(
operator|new
name|JSON
argument_list|()
operator|.
name|toJSON
argument_list|(
name|makeMap
argument_list|(
name|allContexts
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|PrintWriter
name|out
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
try|try
block|{
name|printMap
argument_list|(
name|out
argument_list|,
name|makeMap
argument_list|(
name|allContexts
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Prints metrics data in a multi-line text form.    */
DECL|method|printMap (PrintWriter out, Map<String, Map<String, List<TagsMetricsPair>>> map)
name|void
name|printMap
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|TagsMetricsPair
argument_list|>
argument_list|>
argument_list|>
name|map
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|TagsMetricsPair
argument_list|>
argument_list|>
argument_list|>
name|context
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|print
argument_list|(
name|context
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|TagsMetricsPair
argument_list|>
argument_list|>
name|record
range|:
name|context
operator|.
name|getValue
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|indent
argument_list|(
name|out
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|record
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|TagsMetricsPair
name|pair
range|:
name|record
operator|.
name|getValue
argument_list|()
control|)
block|{
name|indent
argument_list|(
name|out
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// Prints tag values in the form "{key=value,key=value}:"
name|out
operator|.
name|print
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|tagValue
range|:
name|pair
operator|.
name|tagMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|first
condition|)
block|{
name|first
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|print
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
name|tagValue
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|tagValue
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"}:\n"
argument_list|)
expr_stmt|;
comment|// Now print metric values, one per line
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Number
argument_list|>
name|metricValue
range|:
name|pair
operator|.
name|metricMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|indent
argument_list|(
name|out
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|metricValue
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|metricValue
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|indent (PrintWriter out, int indent)
specifier|private
name|void
name|indent
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|int
name|indent
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indent
condition|;
operator|++
name|i
control|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

