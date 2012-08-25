begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|/**  * The MetricsTimeVaryingLong class is for a metric that naturally  * varies over time (e.g. number of files created). The metrics is accumulated  * over an interval (set in the metrics config file); the metrics is  *  published at the end of each interval and then   * reset to zero. Hence the counter has the value in the current interval.   *   * Note if one wants a time associated with the metric then use  * @see org.apache.hadoop.metrics.util.MetricsTimeVaryingRate  *  */
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
DECL|class|MetricsTimeVaryingLong
specifier|public
class|class
name|MetricsTimeVaryingLong
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
DECL|field|currentValue
specifier|private
name|long
name|currentValue
decl_stmt|;
DECL|field|previousIntervalValue
specifier|private
name|long
name|previousIntervalValue
decl_stmt|;
comment|/**    * Constructor - create a new metric    * @param nam the name of the metrics to be used to publish the metric    * @param registry - where the metrics object will be registered    */
DECL|method|MetricsTimeVaryingLong (final String nam, MetricsRegistry registry, final String description)
specifier|public
name|MetricsTimeVaryingLong
parameter_list|(
specifier|final
name|String
name|nam
parameter_list|,
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
name|currentValue
operator|=
literal|0
expr_stmt|;
name|previousIntervalValue
operator|=
literal|0
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
DECL|method|MetricsTimeVaryingLong (final String nam, MetricsRegistry registry)
specifier|public
name|MetricsTimeVaryingLong
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
comment|/**    * Inc metrics for incr vlaue    * @param incr - number of operations    */
DECL|method|inc (final long incr)
specifier|public
specifier|synchronized
name|void
name|inc
parameter_list|(
specifier|final
name|long
name|incr
parameter_list|)
block|{
name|currentValue
operator|+=
name|incr
expr_stmt|;
block|}
comment|/**    * Inc metrics by one    */
DECL|method|inc ()
specifier|public
specifier|synchronized
name|void
name|inc
parameter_list|()
block|{
name|currentValue
operator|++
expr_stmt|;
block|}
DECL|method|intervalHeartBeat ()
specifier|private
specifier|synchronized
name|void
name|intervalHeartBeat
parameter_list|()
block|{
name|previousIntervalValue
operator|=
name|currentValue
expr_stmt|;
name|currentValue
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Push the delta  metrics to the mr.    * The delta is since the last push/interval.    *     * Note this does NOT push to JMX    * (JMX gets the info via {@link #previousIntervalValue}    *    * @param mr    */
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
name|intervalHeartBeat
argument_list|()
expr_stmt|;
try|try
block|{
name|mr
operator|.
name|incrMetric
argument_list|(
name|getName
argument_list|()
argument_list|,
name|getPreviousIntervalValue
argument_list|()
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
comment|/**    * The Value at the Previous interval    * @return prev interval value    */
DECL|method|getPreviousIntervalValue ()
specifier|public
specifier|synchronized
name|long
name|getPreviousIntervalValue
parameter_list|()
block|{
return|return
name|previousIntervalValue
return|;
block|}
comment|/**    * The Value at the current interval    * @return prev interval value    */
DECL|method|getCurrentIntervalValue ()
specifier|public
specifier|synchronized
name|long
name|getCurrentIntervalValue
parameter_list|()
block|{
return|return
name|currentValue
return|;
block|}
block|}
end_class

end_unit

