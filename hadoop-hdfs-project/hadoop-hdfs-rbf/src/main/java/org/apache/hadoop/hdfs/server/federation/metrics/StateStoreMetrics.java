begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|metrics
package|;
end_package

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
name|impl
operator|.
name|MsInfo
operator|.
name|ProcessName
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
name|impl
operator|.
name|MsInfo
operator|.
name|SessionId
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|MetricsRegistry
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
name|MutableRate
import|;
end_import

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

begin_comment
comment|/**  * Implementations of the JMX interface for the State Store metrics.  */
end_comment

begin_class
annotation|@
name|Metrics
argument_list|(
name|name
operator|=
literal|"StateStoreActivity"
argument_list|,
name|about
operator|=
literal|"Router metrics"
argument_list|,
name|context
operator|=
literal|"dfs"
argument_list|)
DECL|class|StateStoreMetrics
specifier|public
class|class
name|StateStoreMetrics
implements|implements
name|StateStoreMBean
block|{
DECL|field|registry
specifier|private
specifier|final
name|MetricsRegistry
name|registry
init|=
operator|new
name|MetricsRegistry
argument_list|(
literal|"router"
argument_list|)
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"GET transactions"
argument_list|)
DECL|field|reads
specifier|private
name|MutableRate
name|reads
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"PUT transactions"
argument_list|)
DECL|field|writes
specifier|private
name|MutableRate
name|writes
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"REMOVE transactions"
argument_list|)
DECL|field|removes
specifier|private
name|MutableRate
name|removes
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Failed transactions"
argument_list|)
DECL|field|failures
specifier|private
name|MutableRate
name|failures
decl_stmt|;
DECL|field|cacheSizes
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|MutableGaugeInt
argument_list|>
name|cacheSizes
decl_stmt|;
DECL|method|StateStoreMetrics ()
specifier|protected
name|StateStoreMetrics
parameter_list|()
block|{}
DECL|method|StateStoreMetrics (Configuration conf)
specifier|private
name|StateStoreMetrics
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|registry
operator|.
name|tag
argument_list|(
name|SessionId
argument_list|,
literal|"RouterSession"
argument_list|)
expr_stmt|;
name|registry
operator|.
name|tag
argument_list|(
name|ProcessName
argument_list|,
literal|"Router"
argument_list|)
expr_stmt|;
name|cacheSizes
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|create (Configuration conf)
specifier|public
specifier|static
name|StateStoreMetrics
name|create
parameter_list|(
name|Configuration
name|conf
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
return|return
name|ms
operator|.
name|register
argument_list|(
operator|new
name|StateStoreMetrics
argument_list|(
name|conf
argument_list|)
argument_list|)
return|;
block|}
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|DefaultMetricsSystem
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|addRead (long latency)
specifier|public
name|void
name|addRead
parameter_list|(
name|long
name|latency
parameter_list|)
block|{
name|reads
operator|.
name|add
argument_list|(
name|latency
argument_list|)
expr_stmt|;
block|}
DECL|method|getReadOps ()
specifier|public
name|long
name|getReadOps
parameter_list|()
block|{
return|return
name|reads
operator|.
name|lastStat
argument_list|()
operator|.
name|numSamples
argument_list|()
return|;
block|}
DECL|method|getReadAvg ()
specifier|public
name|double
name|getReadAvg
parameter_list|()
block|{
return|return
name|reads
operator|.
name|lastStat
argument_list|()
operator|.
name|mean
argument_list|()
return|;
block|}
DECL|method|addWrite (long latency)
specifier|public
name|void
name|addWrite
parameter_list|(
name|long
name|latency
parameter_list|)
block|{
name|writes
operator|.
name|add
argument_list|(
name|latency
argument_list|)
expr_stmt|;
block|}
DECL|method|getWriteOps ()
specifier|public
name|long
name|getWriteOps
parameter_list|()
block|{
return|return
name|writes
operator|.
name|lastStat
argument_list|()
operator|.
name|numSamples
argument_list|()
return|;
block|}
DECL|method|getWriteAvg ()
specifier|public
name|double
name|getWriteAvg
parameter_list|()
block|{
return|return
name|writes
operator|.
name|lastStat
argument_list|()
operator|.
name|mean
argument_list|()
return|;
block|}
DECL|method|addFailure (long latency)
specifier|public
name|void
name|addFailure
parameter_list|(
name|long
name|latency
parameter_list|)
block|{
name|failures
operator|.
name|add
argument_list|(
name|latency
argument_list|)
expr_stmt|;
block|}
DECL|method|getFailureOps ()
specifier|public
name|long
name|getFailureOps
parameter_list|()
block|{
return|return
name|failures
operator|.
name|lastStat
argument_list|()
operator|.
name|numSamples
argument_list|()
return|;
block|}
DECL|method|getFailureAvg ()
specifier|public
name|double
name|getFailureAvg
parameter_list|()
block|{
return|return
name|failures
operator|.
name|lastStat
argument_list|()
operator|.
name|mean
argument_list|()
return|;
block|}
DECL|method|addRemove (long latency)
specifier|public
name|void
name|addRemove
parameter_list|(
name|long
name|latency
parameter_list|)
block|{
name|removes
operator|.
name|add
argument_list|(
name|latency
argument_list|)
expr_stmt|;
block|}
DECL|method|getRemoveOps ()
specifier|public
name|long
name|getRemoveOps
parameter_list|()
block|{
return|return
name|removes
operator|.
name|lastStat
argument_list|()
operator|.
name|numSamples
argument_list|()
return|;
block|}
DECL|method|getRemoveAvg ()
specifier|public
name|double
name|getRemoveAvg
parameter_list|()
block|{
return|return
name|removes
operator|.
name|lastStat
argument_list|()
operator|.
name|mean
argument_list|()
return|;
block|}
comment|/**    * Set the size of the cache for a State Store interface.    *    * @param name Name of the record to cache.    * @param size Number of records.    */
DECL|method|setCacheSize (String name, int size)
specifier|public
name|void
name|setCacheSize
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|String
name|counterName
init|=
literal|"Cache"
operator|+
name|name
operator|+
literal|"Size"
decl_stmt|;
name|MutableGaugeInt
name|counter
init|=
name|cacheSizes
operator|.
name|get
argument_list|(
name|counterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|counter
operator|==
literal|null
condition|)
block|{
name|counter
operator|=
name|registry
operator|.
name|newGauge
argument_list|(
name|counterName
argument_list|,
name|name
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|cacheSizes
operator|.
name|put
argument_list|(
name|counterName
argument_list|,
name|counter
argument_list|)
expr_stmt|;
block|}
name|counter
operator|.
name|set
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|reads
operator|.
name|resetMinMax
argument_list|()
expr_stmt|;
name|writes
operator|.
name|resetMinMax
argument_list|()
expr_stmt|;
name|removes
operator|.
name|resetMinMax
argument_list|()
expr_stmt|;
name|failures
operator|.
name|resetMinMax
argument_list|()
expr_stmt|;
name|reads
operator|.
name|lastStat
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
name|writes
operator|.
name|lastStat
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
name|removes
operator|.
name|lastStat
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
name|failures
operator|.
name|lastStat
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

