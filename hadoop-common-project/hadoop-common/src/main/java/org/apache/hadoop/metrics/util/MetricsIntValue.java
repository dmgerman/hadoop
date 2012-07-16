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
name|metrics
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
name|util
operator|.
name|StringUtils
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * The MetricsIntValue class is for a metric that is not time varied  * but changes only when it is set.   * Each time its value is set, it is published only *once* at the next update  * call.  *  */
end_comment

begin_class
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
DECL|class|MetricsIntValue
specifier|public
class|class
name|MetricsIntValue
extends|extends
name|MetricsBase
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
literal|"org.apache.hadoop.metrics.util"
argument_list|)
decl_stmt|;
DECL|field|value
specifier|private
name|int
name|value
decl_stmt|;
DECL|field|changed
specifier|private
name|boolean
name|changed
decl_stmt|;
comment|/**    * Constructor - create a new metric    * @param nam the name of the metrics to be used to publish the metric    * @param registry - where the metrics object will be registered    */
DECL|method|MetricsIntValue (final String nam, final MetricsRegistry registry, final String description)
specifier|public
name|MetricsIntValue
parameter_list|(
specifier|final
name|String
name|nam
parameter_list|,
specifier|final
name|MetricsRegistry
name|registry
parameter_list|,
specifier|final
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|nam
argument_list|,
name|description
argument_list|)
expr_stmt|;
name|value
operator|=
literal|0
expr_stmt|;
name|changed
operator|=
literal|false
expr_stmt|;
name|registry
operator|.
name|add
argument_list|(
name|nam
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor - create a new metric    * @param nam the name of the metrics to be used to publish the metric    * @param registry - where the metrics object will be registered    * A description of {@link #NO_DESCRIPTION} is used    */
DECL|method|MetricsIntValue (final String nam, MetricsRegistry registry)
specifier|public
name|MetricsIntValue
parameter_list|(
specifier|final
name|String
name|nam
parameter_list|,
name|MetricsRegistry
name|registry
parameter_list|)
block|{
name|this
argument_list|(
name|nam
argument_list|,
name|registry
argument_list|,
name|NO_DESCRIPTION
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the value    * @param newValue    */
DECL|method|set (final int newValue)
specifier|public
specifier|synchronized
name|void
name|set
parameter_list|(
specifier|final
name|int
name|newValue
parameter_list|)
block|{
name|value
operator|=
name|newValue
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Get value    * @return the value last set    */
DECL|method|get ()
specifier|public
specifier|synchronized
name|int
name|get
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**    * Push the metric to the mr.    * The metric is pushed only if it was updated since last push    *     * Note this does NOT push to JMX    * (JMX gets the info via {@link #get()}    *    * @param mr    */
annotation|@
name|Override
DECL|method|pushMetric (final MetricsRecord mr)
specifier|public
specifier|synchronized
name|void
name|pushMetric
parameter_list|(
specifier|final
name|MetricsRecord
name|mr
parameter_list|)
block|{
if|if
condition|(
name|changed
condition|)
block|{
try|try
block|{
name|mr
operator|.
name|setMetric
argument_list|(
name|getName
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"pushMetric failed for "
operator|+
name|getName
argument_list|()
operator|+
literal|"\n"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|changed
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class

end_unit

