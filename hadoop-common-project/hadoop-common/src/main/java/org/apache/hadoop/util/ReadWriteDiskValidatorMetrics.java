begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
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
name|annotations
operator|.
name|VisibleForTesting
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
name|MetricsSystem
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
name|annotation
operator|.
name|Metric
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
name|lib
operator|.
name|*
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|lib
operator|.
name|Interns
operator|.
name|info
import|;
end_import

begin_comment
comment|/**  * The metrics for a directory generated by {@link ReadWriteDiskValidator}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ReadWriteDiskValidatorMetrics
specifier|public
class|class
name|ReadWriteDiskValidatorMetrics
block|{
DECL|field|failureCount
annotation|@
name|Metric
argument_list|(
literal|"# of disk failure"
argument_list|)
name|MutableCounterInt
name|failureCount
decl_stmt|;
DECL|field|lastFailureTime
annotation|@
name|Metric
argument_list|(
literal|"Time of last failure"
argument_list|)
name|MutableGaugeLong
name|lastFailureTime
decl_stmt|;
DECL|field|registry
specifier|private
specifier|final
name|MetricsRegistry
name|registry
decl_stmt|;
DECL|field|RECORD_INFO
specifier|private
specifier|static
specifier|final
name|MetricsInfo
name|RECORD_INFO
init|=
name|info
argument_list|(
literal|"ReadWriteDiskValidatorMetrics"
argument_list|,
literal|"Metrics for the DiskValidator"
argument_list|)
decl_stmt|;
DECL|field|quantileIntervals
specifier|private
specifier|final
name|int
index|[]
name|quantileIntervals
init|=
operator|new
name|int
index|[]
block|{
literal|60
operator|*
literal|60
block|,
comment|// 1h
literal|24
operator|*
literal|60
operator|*
literal|60
block|,
comment|//1 day
literal|10
operator|*
literal|24
operator|*
literal|60
operator|*
literal|60
comment|//10 day
block|}
decl_stmt|;
DECL|field|fileReadQuantiles
specifier|private
specifier|final
name|MutableQuantiles
index|[]
name|fileReadQuantiles
decl_stmt|;
DECL|field|fileWriteQuantiles
specifier|private
specifier|final
name|MutableQuantiles
index|[]
name|fileWriteQuantiles
decl_stmt|;
DECL|method|ReadWriteDiskValidatorMetrics ()
specifier|public
name|ReadWriteDiskValidatorMetrics
parameter_list|()
block|{
name|registry
operator|=
operator|new
name|MetricsRegistry
argument_list|(
name|RECORD_INFO
argument_list|)
expr_stmt|;
name|fileReadQuantiles
operator|=
operator|new
name|MutableQuantiles
index|[
name|quantileIntervals
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fileReadQuantiles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|interval
init|=
name|quantileIntervals
index|[
name|i
index|]
decl_stmt|;
name|fileReadQuantiles
index|[
name|i
index|]
operator|=
name|registry
operator|.
name|newQuantiles
argument_list|(
literal|"readLatency"
operator|+
name|interval
operator|+
literal|"s"
argument_list|,
literal|"File read latency"
argument_list|,
literal|"Ops"
argument_list|,
literal|"latencyMicros"
argument_list|,
name|interval
argument_list|)
expr_stmt|;
block|}
name|fileWriteQuantiles
operator|=
operator|new
name|MutableQuantiles
index|[
name|quantileIntervals
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fileWriteQuantiles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|interval
init|=
name|quantileIntervals
index|[
name|i
index|]
decl_stmt|;
name|fileWriteQuantiles
index|[
name|i
index|]
operator|=
name|registry
operator|.
name|newQuantiles
argument_list|(
literal|"writeLatency"
operator|+
name|interval
operator|+
literal|"s"
argument_list|,
literal|"File write latency"
argument_list|,
literal|"Ops"
argument_list|,
literal|"latencyMicros"
argument_list|,
name|interval
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Simple metrics cache to help prevent re-registrations and help to access    * metrics.    */
DECL|field|DIR_METRICS
specifier|protected
specifier|final
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|ReadWriteDiskValidatorMetrics
argument_list|>
name|DIR_METRICS
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Get a metric by given directory name.    *    * @param dirName directory name    * @return the metric    */
DECL|method|getMetric ( String dirName)
specifier|public
specifier|synchronized
specifier|static
name|ReadWriteDiskValidatorMetrics
name|getMetric
parameter_list|(
name|String
name|dirName
parameter_list|)
block|{
name|MetricsSystem
name|ms
init|=
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
decl_stmt|;
name|ReadWriteDiskValidatorMetrics
name|metrics
init|=
name|DIR_METRICS
operator|.
name|get
argument_list|(
name|dirName
argument_list|)
decl_stmt|;
if|if
condition|(
name|metrics
operator|==
literal|null
condition|)
block|{
name|metrics
operator|=
operator|new
name|ReadWriteDiskValidatorMetrics
argument_list|()
expr_stmt|;
comment|// Register with the MetricsSystems
if|if
condition|(
name|ms
operator|!=
literal|null
condition|)
block|{
name|metrics
operator|=
name|ms
operator|.
name|register
argument_list|(
name|sourceName
argument_list|(
name|dirName
argument_list|)
argument_list|,
literal|"Metrics for directory: "
operator|+
name|dirName
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
block|}
name|DIR_METRICS
operator|.
name|put
argument_list|(
name|dirName
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
block|}
return|return
name|metrics
return|;
block|}
comment|/**    * Add the file write latency to {@link MutableQuantiles} metrics.    *    * @param writeLatency file write latency in microseconds    */
DECL|method|addWriteFileLatency (long writeLatency)
specifier|public
name|void
name|addWriteFileLatency
parameter_list|(
name|long
name|writeLatency
parameter_list|)
block|{
if|if
condition|(
name|fileWriteQuantiles
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|MutableQuantiles
name|q
range|:
name|fileWriteQuantiles
control|)
block|{
name|q
operator|.
name|add
argument_list|(
name|writeLatency
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Add the file read latency to {@link MutableQuantiles} metrics.    *    * @param readLatency file read latency in microseconds    */
DECL|method|addReadFileLatency (long readLatency)
specifier|public
name|void
name|addReadFileLatency
parameter_list|(
name|long
name|readLatency
parameter_list|)
block|{
if|if
condition|(
name|fileReadQuantiles
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|MutableQuantiles
name|q
range|:
name|fileReadQuantiles
control|)
block|{
name|q
operator|.
name|add
argument_list|(
name|readLatency
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Get a source name by given directory name.    *    * @param dirName directory name    * @return the source name    */
DECL|method|sourceName (String dirName)
specifier|protected
specifier|static
name|String
name|sourceName
parameter_list|(
name|String
name|dirName
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|RECORD_INFO
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",dir="
argument_list|)
operator|.
name|append
argument_list|(
name|dirName
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Increase the failure count and update the last failure timestamp.    */
DECL|method|diskCheckFailed ()
specifier|public
name|void
name|diskCheckFailed
parameter_list|()
block|{
name|failureCount
operator|.
name|incr
argument_list|()
expr_stmt|;
name|lastFailureTime
operator|.
name|set
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get {@link MutableQuantiles} metrics for the file read time.    *    * @return {@link MutableQuantiles} metrics for the file read time    */
annotation|@
name|VisibleForTesting
DECL|method|getFileReadQuantiles ()
specifier|protected
name|MutableQuantiles
index|[]
name|getFileReadQuantiles
parameter_list|()
block|{
return|return
name|fileReadQuantiles
return|;
block|}
comment|/**    * Get {@link MutableQuantiles} metrics for the file write time.    *    * @return {@link MutableQuantiles} metrics for the file write time    */
annotation|@
name|VisibleForTesting
DECL|method|getFileWriteQuantiles ()
specifier|protected
name|MutableQuantiles
index|[]
name|getFileWriteQuantiles
parameter_list|()
block|{
return|return
name|fileWriteQuantiles
return|;
block|}
block|}
end_class

end_unit

