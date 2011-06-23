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
comment|/**  * The MetricsTimeVaryingRate class is for a rate based metric that  * naturally varies over time (e.g. time taken to create a file).  * The rate is averaged at each interval heart beat (the interval  * is set in the metrics config file).  * This class also keeps track of the min and max rates along with   * a method to reset the min-max.  *  */
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
DECL|class|MetricsTimeVaryingRate
specifier|public
class|class
name|MetricsTimeVaryingRate
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
DECL|class|Metrics
specifier|static
class|class
name|Metrics
block|{
DECL|field|numOperations
name|int
name|numOperations
init|=
literal|0
decl_stmt|;
DECL|field|time
name|long
name|time
init|=
literal|0
decl_stmt|;
comment|// total time or average time
DECL|method|set (final Metrics resetTo)
name|void
name|set
parameter_list|(
specifier|final
name|Metrics
name|resetTo
parameter_list|)
block|{
name|numOperations
operator|=
name|resetTo
operator|.
name|numOperations
expr_stmt|;
name|time
operator|=
name|resetTo
operator|.
name|time
expr_stmt|;
block|}
DECL|method|reset ()
name|void
name|reset
parameter_list|()
block|{
name|numOperations
operator|=
literal|0
expr_stmt|;
name|time
operator|=
literal|0
expr_stmt|;
block|}
block|}
DECL|class|MinMax
specifier|static
class|class
name|MinMax
block|{
DECL|field|minTime
name|long
name|minTime
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|maxTime
name|long
name|maxTime
init|=
literal|0
decl_stmt|;
DECL|method|set (final MinMax newVal)
name|void
name|set
parameter_list|(
specifier|final
name|MinMax
name|newVal
parameter_list|)
block|{
name|minTime
operator|=
name|newVal
operator|.
name|minTime
expr_stmt|;
name|maxTime
operator|=
name|newVal
operator|.
name|maxTime
expr_stmt|;
block|}
DECL|method|reset ()
name|void
name|reset
parameter_list|()
block|{
name|minTime
operator|=
operator|-
literal|1
expr_stmt|;
name|maxTime
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|update (final long time)
name|void
name|update
parameter_list|(
specifier|final
name|long
name|time
parameter_list|)
block|{
comment|// update min max
name|minTime
operator|=
operator|(
name|minTime
operator|==
operator|-
literal|1
operator|)
condition|?
name|time
else|:
name|Math
operator|.
name|min
argument_list|(
name|minTime
argument_list|,
name|time
argument_list|)
expr_stmt|;
name|minTime
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minTime
argument_list|,
name|time
argument_list|)
expr_stmt|;
name|maxTime
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxTime
argument_list|,
name|time
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|currentData
specifier|private
name|Metrics
name|currentData
decl_stmt|;
DECL|field|previousIntervalData
specifier|private
name|Metrics
name|previousIntervalData
decl_stmt|;
DECL|field|minMax
specifier|private
name|MinMax
name|minMax
decl_stmt|;
comment|/**    * Constructor - create a new metric    * @param nam the name of the metrics to be used to publish the metric    * @param registry - where the metrics object will be registered    */
DECL|method|MetricsTimeVaryingRate (final String nam, final MetricsRegistry registry, final String description)
specifier|public
name|MetricsTimeVaryingRate
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
name|currentData
operator|=
operator|new
name|Metrics
argument_list|()
expr_stmt|;
name|previousIntervalData
operator|=
operator|new
name|Metrics
argument_list|()
expr_stmt|;
name|minMax
operator|=
operator|new
name|MinMax
argument_list|()
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
DECL|method|MetricsTimeVaryingRate (final String nam, MetricsRegistry registry)
specifier|public
name|MetricsTimeVaryingRate
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
comment|/**    * Increment the metrics for numOps operations    * @param numOps - number of operations    * @param time - time for numOps operations    */
DECL|method|inc (final int numOps, final long time)
specifier|public
specifier|synchronized
name|void
name|inc
parameter_list|(
specifier|final
name|int
name|numOps
parameter_list|,
specifier|final
name|long
name|time
parameter_list|)
block|{
name|currentData
operator|.
name|numOperations
operator|+=
name|numOps
expr_stmt|;
name|currentData
operator|.
name|time
operator|+=
name|time
expr_stmt|;
name|long
name|timePerOps
init|=
name|time
operator|/
name|numOps
decl_stmt|;
name|minMax
operator|.
name|update
argument_list|(
name|timePerOps
argument_list|)
expr_stmt|;
block|}
comment|/**    * Increment the metrics for one operation    * @param time for one operation    */
DECL|method|inc (final long time)
specifier|public
specifier|synchronized
name|void
name|inc
parameter_list|(
specifier|final
name|long
name|time
parameter_list|)
block|{
name|currentData
operator|.
name|numOperations
operator|++
expr_stmt|;
name|currentData
operator|.
name|time
operator|+=
name|time
expr_stmt|;
name|minMax
operator|.
name|update
argument_list|(
name|time
argument_list|)
expr_stmt|;
block|}
DECL|method|intervalHeartBeat ()
specifier|private
specifier|synchronized
name|void
name|intervalHeartBeat
parameter_list|()
block|{
name|previousIntervalData
operator|.
name|numOperations
operator|=
name|currentData
operator|.
name|numOperations
expr_stmt|;
name|previousIntervalData
operator|.
name|time
operator|=
operator|(
name|currentData
operator|.
name|numOperations
operator|==
literal|0
operator|)
condition|?
literal|0
else|:
name|currentData
operator|.
name|time
operator|/
name|currentData
operator|.
name|numOperations
expr_stmt|;
name|currentData
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**    * Push the delta  metrics to the mr.    * The delta is since the last push/interval.    *     * Note this does NOT push to JMX    * (JMX gets the info via {@link #getPreviousIntervalAverageTime()} and    * {@link #getPreviousIntervalNumOps()}    *    * @param mr    */
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
operator|+
literal|"_num_ops"
argument_list|,
name|getPreviousIntervalNumOps
argument_list|()
argument_list|)
expr_stmt|;
name|mr
operator|.
name|setMetric
argument_list|(
name|getName
argument_list|()
operator|+
literal|"_avg_time"
argument_list|,
name|getPreviousIntervalAverageTime
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
comment|/**    * The number of operations in the previous interval    * @return - ops in prev interval    */
DECL|method|getPreviousIntervalNumOps ()
specifier|public
specifier|synchronized
name|int
name|getPreviousIntervalNumOps
parameter_list|()
block|{
return|return
name|previousIntervalData
operator|.
name|numOperations
return|;
block|}
comment|/**    * The average rate of an operation in the previous interval    * @return - the average rate.    */
DECL|method|getPreviousIntervalAverageTime ()
specifier|public
specifier|synchronized
name|long
name|getPreviousIntervalAverageTime
parameter_list|()
block|{
return|return
name|previousIntervalData
operator|.
name|time
return|;
block|}
comment|/**    * The min time for a single operation since the last reset    *  {@link #resetMinMax()}    * @return min time for an operation    */
DECL|method|getMinTime ()
specifier|public
specifier|synchronized
name|long
name|getMinTime
parameter_list|()
block|{
return|return
name|minMax
operator|.
name|minTime
return|;
block|}
comment|/**    * The max time for a single operation since the last reset    *  {@link #resetMinMax()}    * @return max time for an operation    */
DECL|method|getMaxTime ()
specifier|public
specifier|synchronized
name|long
name|getMaxTime
parameter_list|()
block|{
return|return
name|minMax
operator|.
name|maxTime
return|;
block|}
comment|/**    * Reset the min max values    */
DECL|method|resetMinMax ()
specifier|public
specifier|synchronized
name|void
name|resetMinMax
parameter_list|()
block|{
name|minMax
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

