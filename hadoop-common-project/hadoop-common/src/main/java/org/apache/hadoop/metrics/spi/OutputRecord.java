begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * OutputRecord.java  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics.spi
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics
operator|.
name|spi
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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

begin_comment
comment|/**  * Represents a record of metric data to be sent to a metrics system.  *  * @deprecated Use org.apache.hadoop.metrics2 package instead.  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|OutputRecord
specifier|public
class|class
name|OutputRecord
block|{
DECL|field|tagMap
specifier|private
name|TagMap
name|tagMap
decl_stmt|;
DECL|field|metricMap
specifier|private
name|MetricMap
name|metricMap
decl_stmt|;
comment|/** Creates a new instance of OutputRecord */
DECL|method|OutputRecord (TagMap tagMap, MetricMap metricMap)
name|OutputRecord
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
comment|/**    * Returns the set of tag names    */
DECL|method|getTagNames ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getTagNames
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|tagMap
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns a tag object which is can be a String, Integer, Short or Byte.    *    * @return the tag value, or null if there is no such tag    */
DECL|method|getTag (String name)
specifier|public
name|Object
name|getTag
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|tagMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * Returns the set of metric names.    */
DECL|method|getMetricNames ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getMetricNames
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|metricMap
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns the metric object which can be a Float, Integer, Short or Byte.    */
DECL|method|getMetric (String name)
specifier|public
name|Number
name|getMetric
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|metricMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * Returns a copy of this record's tags.    */
DECL|method|getTagsCopy ()
specifier|public
name|TagMap
name|getTagsCopy
parameter_list|()
block|{
return|return
operator|new
name|TagMap
argument_list|(
name|tagMap
argument_list|)
return|;
block|}
comment|/**    * Returns a copy of this record's metrics.    */
DECL|method|getMetricsCopy ()
specifier|public
name|MetricMap
name|getMetricsCopy
parameter_list|()
block|{
return|return
operator|new
name|MetricMap
argument_list|(
name|metricMap
argument_list|)
return|;
block|}
block|}
end_class

end_unit

