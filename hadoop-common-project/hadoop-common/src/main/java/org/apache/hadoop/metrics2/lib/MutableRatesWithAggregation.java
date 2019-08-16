begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.lib
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|lib
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
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|WeakReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Set
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
name|concurrent
operator|.
name|ConcurrentLinkedDeque
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
name|ConcurrentMap
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
name|metrics2
operator|.
name|MetricsRecordBuilder
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
name|util
operator|.
name|SampleStat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Helper class to manage a group of mutable rate metrics.  *  * Each thread will maintain a local rate count, and upon snapshot,  * these values will be aggregated into a global rate. This class  * should only be used for long running threads, as any metrics  * produced between the last snapshot and the death of a thread  * will be lost. This allows for significantly higher concurrency  * than {@link MutableRates}. See HADOOP-24420.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|MutableRatesWithAggregation
specifier|public
class|class
name|MutableRatesWithAggregation
extends|extends
name|MutableMetric
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MutableRatesWithAggregation
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|globalMetrics
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|MutableRate
argument_list|>
name|globalMetrics
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|protocolCache
specifier|private
specifier|final
name|Set
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|protocolCache
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentLinkedDeque
argument_list|<
name|WeakReference
argument_list|<
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|ThreadSafeSampleStat
argument_list|>
argument_list|>
argument_list|>
DECL|field|weakReferenceQueue
name|weakReferenceQueue
init|=
operator|new
name|ConcurrentLinkedDeque
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ThreadLocal
argument_list|<
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|ThreadSafeSampleStat
argument_list|>
argument_list|>
DECL|field|threadLocalMetricsMap
name|threadLocalMetricsMap
init|=
operator|new
name|ThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
comment|// prefix for metric name
DECL|field|typePrefix
specifier|private
name|String
name|typePrefix
init|=
literal|""
decl_stmt|;
comment|/**    * Initialize the registry with all the methods in a protocol    * so they all show up in the first snapshot.    * Convenient for JMX implementations.    * @param protocol the protocol class    */
DECL|method|init (Class<?> protocol)
specifier|public
name|void
name|init
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|)
block|{
if|if
condition|(
name|protocolCache
operator|.
name|contains
argument_list|(
name|protocol
argument_list|)
condition|)
block|{
return|return;
block|}
name|protocolCache
operator|.
name|add
argument_list|(
name|protocol
argument_list|)
expr_stmt|;
for|for
control|(
name|Method
name|method
range|:
name|protocol
operator|.
name|getDeclaredMethods
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|method
operator|.
name|getName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|addMetricIfNotExists
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Add a rate sample for a rate metric.    * @param name of the rate metric    * @param elapsed time    */
DECL|method|add (String name, long elapsed)
specifier|public
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|elapsed
parameter_list|)
block|{
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|ThreadSafeSampleStat
argument_list|>
name|localStats
init|=
name|threadLocalMetricsMap
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|localStats
operator|==
literal|null
condition|)
block|{
name|localStats
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|threadLocalMetricsMap
operator|.
name|set
argument_list|(
name|localStats
argument_list|)
expr_stmt|;
name|weakReferenceQueue
operator|.
name|add
argument_list|(
operator|new
name|WeakReference
argument_list|<>
argument_list|(
name|localStats
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ThreadSafeSampleStat
name|stat
init|=
name|localStats
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|stat
operator|==
literal|null
condition|)
block|{
name|stat
operator|=
operator|new
name|ThreadSafeSampleStat
argument_list|()
expr_stmt|;
name|localStats
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|stat
argument_list|)
expr_stmt|;
block|}
name|stat
operator|.
name|add
argument_list|(
name|elapsed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|snapshot (MetricsRecordBuilder rb, boolean all)
specifier|public
specifier|synchronized
name|void
name|snapshot
parameter_list|(
name|MetricsRecordBuilder
name|rb
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|Iterator
argument_list|<
name|WeakReference
argument_list|<
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|ThreadSafeSampleStat
argument_list|>
argument_list|>
argument_list|>
name|iter
init|=
name|weakReferenceQueue
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|ThreadSafeSampleStat
argument_list|>
name|map
init|=
name|iter
operator|.
name|next
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
comment|// Thread has died; clean up its state
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|aggregateLocalStatesToGlobalMetrics
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|MutableRate
name|globalMetric
range|:
name|globalMetrics
operator|.
name|values
argument_list|()
control|)
block|{
name|globalMetric
operator|.
name|snapshot
argument_list|(
name|rb
argument_list|,
name|all
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Collects states maintained in {@link ThreadLocal}, if any.    */
DECL|method|collectThreadLocalStates ()
specifier|synchronized
name|void
name|collectThreadLocalStates
parameter_list|()
block|{
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|ThreadSafeSampleStat
argument_list|>
name|localStats
init|=
name|threadLocalMetricsMap
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|localStats
operator|!=
literal|null
condition|)
block|{
name|aggregateLocalStatesToGlobalMetrics
argument_list|(
name|localStats
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Aggregates the thread's local samples into the global metrics. The caller    * should ensure its thread safety.    */
DECL|method|aggregateLocalStatesToGlobalMetrics ( final ConcurrentMap<String, ThreadSafeSampleStat> localStats)
specifier|private
name|void
name|aggregateLocalStatesToGlobalMetrics
parameter_list|(
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|ThreadSafeSampleStat
argument_list|>
name|localStats
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
name|ThreadSafeSampleStat
argument_list|>
name|entry
range|:
name|localStats
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|MutableRate
name|globalMetric
init|=
name|addMetricIfNotExists
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|snapshotInto
argument_list|(
name|globalMetric
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getGlobalMetrics ()
name|Map
argument_list|<
name|String
argument_list|,
name|MutableRate
argument_list|>
name|getGlobalMetrics
parameter_list|()
block|{
return|return
name|globalMetrics
return|;
block|}
DECL|method|addMetricIfNotExists (String name)
specifier|private
specifier|synchronized
name|MutableRate
name|addMetricIfNotExists
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|MutableRate
name|metric
init|=
name|globalMetrics
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|metric
operator|==
literal|null
condition|)
block|{
name|metric
operator|=
operator|new
name|MutableRate
argument_list|(
name|name
operator|+
name|typePrefix
argument_list|,
name|name
operator|+
name|typePrefix
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|globalMetrics
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|metric
argument_list|)
expr_stmt|;
block|}
return|return
name|metric
return|;
block|}
DECL|class|ThreadSafeSampleStat
specifier|private
specifier|static
class|class
name|ThreadSafeSampleStat
block|{
DECL|field|stat
specifier|private
name|SampleStat
name|stat
init|=
operator|new
name|SampleStat
argument_list|()
decl_stmt|;
DECL|method|add (double x)
specifier|synchronized
name|void
name|add
parameter_list|(
name|double
name|x
parameter_list|)
block|{
name|stat
operator|.
name|add
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
DECL|method|snapshotInto (MutableRate metric)
specifier|synchronized
name|void
name|snapshotInto
parameter_list|(
name|MutableRate
name|metric
parameter_list|)
block|{
if|if
condition|(
name|stat
operator|.
name|numSamples
argument_list|()
operator|>
literal|0
condition|)
block|{
name|metric
operator|.
name|add
argument_list|(
name|stat
operator|.
name|numSamples
argument_list|()
argument_list|,
name|Math
operator|.
name|round
argument_list|(
name|stat
operator|.
name|total
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|stat
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|init (Class<?> protocol, String prefix)
specifier|public
name|void
name|init
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|typePrefix
operator|=
name|prefix
expr_stmt|;
name|init
argument_list|(
name|protocol
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

