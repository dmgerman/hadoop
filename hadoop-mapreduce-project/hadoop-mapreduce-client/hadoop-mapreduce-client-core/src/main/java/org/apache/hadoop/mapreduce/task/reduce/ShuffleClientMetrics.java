begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.task.reduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|task
operator|.
name|reduce
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
name|mapred
operator|.
name|JobConf
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
name|mapreduce
operator|.
name|TaskAttemptID
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
name|annotation
operator|.
name|Metrics
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
name|DefaultMetricsSystem
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
name|MutableCounterInt
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
name|MutableCounterLong
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
name|MutableGaugeInt
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
name|MetricsRegistry
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
name|ThreadLocalRandom
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
comment|/**  * Metric for Shuffle client.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"checkstyle:finalclass"
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
annotation|@
name|Metrics
argument_list|(
name|name
operator|=
literal|"ShuffleClientMetrics"
argument_list|,
name|context
operator|=
literal|"mapred"
argument_list|)
DECL|class|ShuffleClientMetrics
specifier|public
class|class
name|ShuffleClientMetrics
block|{
DECL|field|RECORD_INFO
specifier|private
specifier|static
specifier|final
name|MetricsInfo
name|RECORD_INFO
init|=
name|info
argument_list|(
literal|"ShuffleClientMetrics"
argument_list|,
literal|"Metrics for Shuffle client"
argument_list|)
decl_stmt|;
annotation|@
name|Metric
DECL|field|numFailedFetches
specifier|private
name|MutableCounterInt
name|numFailedFetches
decl_stmt|;
annotation|@
name|Metric
DECL|field|numSuccessFetches
specifier|private
name|MutableCounterInt
name|numSuccessFetches
decl_stmt|;
annotation|@
name|Metric
DECL|field|numBytes
specifier|private
name|MutableCounterLong
name|numBytes
decl_stmt|;
annotation|@
name|Metric
DECL|field|numThreadsBusy
specifier|private
name|MutableGaugeInt
name|numThreadsBusy
decl_stmt|;
DECL|field|metricsRegistry
specifier|private
specifier|final
name|MetricsRegistry
name|metricsRegistry
init|=
operator|new
name|MetricsRegistry
argument_list|(
name|RECORD_INFO
argument_list|)
decl_stmt|;
DECL|method|ShuffleClientMetrics ()
specifier|private
name|ShuffleClientMetrics
parameter_list|()
block|{   }
DECL|method|create ( TaskAttemptID reduceId, JobConf jobConf)
specifier|public
specifier|static
name|ShuffleClientMetrics
name|create
parameter_list|(
name|TaskAttemptID
name|reduceId
parameter_list|,
name|JobConf
name|jobConf
parameter_list|)
block|{
name|MetricsSystem
name|ms
init|=
name|DefaultMetricsSystem
operator|.
name|initialize
argument_list|(
literal|"JobTracker"
argument_list|)
decl_stmt|;
name|ShuffleClientMetrics
name|shuffleClientMetrics
init|=
operator|new
name|ShuffleClientMetrics
argument_list|()
decl_stmt|;
name|shuffleClientMetrics
operator|.
name|addTags
argument_list|(
name|reduceId
argument_list|,
name|jobConf
argument_list|)
expr_stmt|;
return|return
name|ms
operator|.
name|register
argument_list|(
literal|"ShuffleClientMetrics-"
operator|+
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|,
literal|null
argument_list|,
name|shuffleClientMetrics
argument_list|)
return|;
block|}
DECL|method|inputBytes (long bytes)
specifier|public
name|void
name|inputBytes
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
name|numBytes
operator|.
name|incr
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
DECL|method|failedFetch ()
specifier|public
name|void
name|failedFetch
parameter_list|()
block|{
name|numFailedFetches
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|successFetch ()
specifier|public
name|void
name|successFetch
parameter_list|()
block|{
name|numSuccessFetches
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|threadBusy ()
specifier|public
name|void
name|threadBusy
parameter_list|()
block|{
name|numThreadsBusy
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|threadFree ()
specifier|public
name|void
name|threadFree
parameter_list|()
block|{
name|numThreadsBusy
operator|.
name|decr
argument_list|()
expr_stmt|;
block|}
DECL|method|addTags (TaskAttemptID reduceId, JobConf jobConf)
specifier|private
name|void
name|addTags
parameter_list|(
name|TaskAttemptID
name|reduceId
parameter_list|,
name|JobConf
name|jobConf
parameter_list|)
block|{
name|metricsRegistry
operator|.
name|tag
argument_list|(
literal|"user"
argument_list|,
literal|""
argument_list|,
name|jobConf
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|tag
argument_list|(
literal|"jobName"
argument_list|,
literal|""
argument_list|,
name|jobConf
operator|.
name|getJobName
argument_list|()
argument_list|)
operator|.
name|tag
argument_list|(
literal|"jobId"
argument_list|,
literal|""
argument_list|,
name|reduceId
operator|.
name|getJobID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|tag
argument_list|(
literal|"taskId"
argument_list|,
literal|""
argument_list|,
name|reduceId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getMetricsRegistry ()
name|MetricsRegistry
name|getMetricsRegistry
parameter_list|()
block|{
return|return
name|metricsRegistry
return|;
block|}
block|}
end_class

end_unit

