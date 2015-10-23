begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics
operator|.
name|util
package|;
end_package

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
name|concurrent
operator|.
name|ConcurrentHashMap
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

begin_comment
comment|/**  *   * This is the registry for metrics.  * Related set of metrics should be declared in a holding class and registered  * in a registry for those metrics which is also stored in the the holding class.  *  * @deprecated Use org.apache.hadoop.metrics2 package instead.  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
DECL|class|MetricsRegistry
specifier|public
class|class
name|MetricsRegistry
block|{
DECL|field|metricsList
specifier|private
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|MetricsBase
argument_list|>
name|metricsList
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|MetricsBase
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|MetricsRegistry ()
specifier|public
name|MetricsRegistry
parameter_list|()
block|{   }
comment|/**    *     * @return number of metrics in the registry    */
DECL|method|size ()
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|metricsList
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Add a new metrics to the registry    * @param metricsName - the name    * @param theMetricsObj - the metrics    * @throws IllegalArgumentException if a name is already registered    */
DECL|method|add (final String metricsName, final MetricsBase theMetricsObj)
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|String
name|metricsName
parameter_list|,
specifier|final
name|MetricsBase
name|theMetricsObj
parameter_list|)
block|{
if|if
condition|(
name|metricsList
operator|.
name|putIfAbsent
argument_list|(
name|metricsName
argument_list|,
name|theMetricsObj
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Duplicate metricsName:"
operator|+
name|metricsName
argument_list|)
throw|;
block|}
block|}
comment|/**    *     * @param metricsName    * @return the metrics if there is one registered by the supplied name.    *         Returns null if none is registered    */
DECL|method|get (final String metricsName)
specifier|public
name|MetricsBase
name|get
parameter_list|(
specifier|final
name|String
name|metricsName
parameter_list|)
block|{
return|return
name|metricsList
operator|.
name|get
argument_list|(
name|metricsName
argument_list|)
return|;
block|}
comment|/**    *     * @return the list of metrics names    */
DECL|method|getKeyList ()
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getKeyList
parameter_list|()
block|{
return|return
name|metricsList
operator|.
name|keySet
argument_list|()
return|;
block|}
comment|/**    *     * @return the list of metrics    */
DECL|method|getMetricsList ()
specifier|public
name|Collection
argument_list|<
name|MetricsBase
argument_list|>
name|getMetricsList
parameter_list|()
block|{
return|return
name|metricsList
operator|.
name|values
argument_list|()
return|;
block|}
block|}
end_class

end_unit

