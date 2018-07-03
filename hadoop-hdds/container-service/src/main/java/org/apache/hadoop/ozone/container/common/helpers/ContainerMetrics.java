begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.helpers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|MutableQuantiles
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

begin_comment
comment|/**  *  * This class is for maintaining  the various Storage Container  * DataNode statistics and publishing them through the metrics interfaces.  * This also registers the JMX MBean for RPC.  *<p>  * This class has a number of metrics variables that are publicly accessible;  * these variables (objects) have methods to update their values;  *  for example:  *<p> {@link #numOps}.inc()  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|Metrics
argument_list|(
name|about
operator|=
literal|"Storage Container DataNode Metrics"
argument_list|,
name|context
operator|=
literal|"dfs"
argument_list|)
DECL|class|ContainerMetrics
specifier|public
class|class
name|ContainerMetrics
block|{
DECL|field|numOps
annotation|@
name|Metric
specifier|private
name|MutableCounterLong
name|numOps
decl_stmt|;
DECL|field|numOpsArray
specifier|private
name|MutableCounterLong
index|[]
name|numOpsArray
decl_stmt|;
DECL|field|opsBytesArray
specifier|private
name|MutableCounterLong
index|[]
name|opsBytesArray
decl_stmt|;
DECL|field|opsLatency
specifier|private
name|MutableRate
index|[]
name|opsLatency
decl_stmt|;
DECL|field|opsLatQuantiles
specifier|private
name|MutableQuantiles
index|[]
index|[]
name|opsLatQuantiles
decl_stmt|;
DECL|field|registry
specifier|private
name|MetricsRegistry
name|registry
init|=
literal|null
decl_stmt|;
DECL|method|ContainerMetrics (int[] intervals)
specifier|public
name|ContainerMetrics
parameter_list|(
name|int
index|[]
name|intervals
parameter_list|)
block|{
name|int
name|numEnumEntries
init|=
name|ContainerProtos
operator|.
name|Type
operator|.
name|values
argument_list|()
operator|.
name|length
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|intervals
operator|.
name|length
decl_stmt|;
name|this
operator|.
name|numOpsArray
operator|=
operator|new
name|MutableCounterLong
index|[
name|numEnumEntries
index|]
expr_stmt|;
name|this
operator|.
name|opsBytesArray
operator|=
operator|new
name|MutableCounterLong
index|[
name|numEnumEntries
index|]
expr_stmt|;
name|this
operator|.
name|opsLatency
operator|=
operator|new
name|MutableRate
index|[
name|numEnumEntries
index|]
expr_stmt|;
name|this
operator|.
name|opsLatQuantiles
operator|=
operator|new
name|MutableQuantiles
index|[
name|numEnumEntries
index|]
index|[
name|len
index|]
expr_stmt|;
name|this
operator|.
name|registry
operator|=
operator|new
name|MetricsRegistry
argument_list|(
literal|"StorageContainerMetrics"
argument_list|)
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
name|numEnumEntries
condition|;
name|i
operator|++
control|)
block|{
name|numOpsArray
index|[
name|i
index|]
operator|=
name|registry
operator|.
name|newCounter
argument_list|(
literal|"num"
operator|+
name|ContainerProtos
operator|.
name|Type
operator|.
name|forNumber
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|,
literal|"number of "
operator|+
name|ContainerProtos
operator|.
name|Type
operator|.
name|forNumber
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|+
literal|" ops"
argument_list|,
operator|(
name|long
operator|)
literal|0
argument_list|)
expr_stmt|;
name|opsBytesArray
index|[
name|i
index|]
operator|=
name|registry
operator|.
name|newCounter
argument_list|(
literal|"bytes"
operator|+
name|ContainerProtos
operator|.
name|Type
operator|.
name|forNumber
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|,
literal|"bytes used by "
operator|+
name|ContainerProtos
operator|.
name|Type
operator|.
name|forNumber
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|+
literal|"op"
argument_list|,
operator|(
name|long
operator|)
literal|0
argument_list|)
expr_stmt|;
name|opsLatency
index|[
name|i
index|]
operator|=
name|registry
operator|.
name|newRate
argument_list|(
literal|"latency"
operator|+
name|ContainerProtos
operator|.
name|Type
operator|.
name|forNumber
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|,
name|ContainerProtos
operator|.
name|Type
operator|.
name|forNumber
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|+
literal|" op"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|len
condition|;
name|j
operator|++
control|)
block|{
name|int
name|interval
init|=
name|intervals
index|[
name|j
index|]
decl_stmt|;
name|String
name|quantileName
init|=
name|ContainerProtos
operator|.
name|Type
operator|.
name|forNumber
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|+
literal|"Nanos"
operator|+
name|interval
operator|+
literal|"s"
decl_stmt|;
name|opsLatQuantiles
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|registry
operator|.
name|newQuantiles
argument_list|(
name|quantileName
argument_list|,
literal|"latency of Container ops"
argument_list|,
literal|"ops"
argument_list|,
literal|"latency"
argument_list|,
name|interval
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|create (Configuration conf)
specifier|public
specifier|static
name|ContainerMetrics
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
comment|// Percentile measurement is off by default, by watching no intervals
name|int
index|[]
name|intervals
init|=
name|conf
operator|.
name|getInts
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_METRICS_PERCENTILES_INTERVALS_KEY
argument_list|)
decl_stmt|;
return|return
name|ms
operator|.
name|register
argument_list|(
literal|"StorageContainerMetrics"
argument_list|,
literal|"Storage Container Node Metrics"
argument_list|,
operator|new
name|ContainerMetrics
argument_list|(
name|intervals
argument_list|)
argument_list|)
return|;
block|}
DECL|method|incContainerOpsMetrics (ContainerProtos.Type type)
specifier|public
name|void
name|incContainerOpsMetrics
parameter_list|(
name|ContainerProtos
operator|.
name|Type
name|type
parameter_list|)
block|{
name|numOps
operator|.
name|incr
argument_list|()
expr_stmt|;
name|numOpsArray
index|[
name|type
operator|.
name|ordinal
argument_list|()
index|]
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|getContainerOpsMetrics (ContainerProtos.Type type)
specifier|public
name|long
name|getContainerOpsMetrics
parameter_list|(
name|ContainerProtos
operator|.
name|Type
name|type
parameter_list|)
block|{
return|return
name|numOpsArray
index|[
name|type
operator|.
name|ordinal
argument_list|()
index|]
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|incContainerOpsLatencies (ContainerProtos.Type type, long latencyNanos)
specifier|public
name|void
name|incContainerOpsLatencies
parameter_list|(
name|ContainerProtos
operator|.
name|Type
name|type
parameter_list|,
name|long
name|latencyNanos
parameter_list|)
block|{
name|opsLatency
index|[
name|type
operator|.
name|ordinal
argument_list|()
index|]
operator|.
name|add
argument_list|(
name|latencyNanos
argument_list|)
expr_stmt|;
for|for
control|(
name|MutableQuantiles
name|q
range|:
name|opsLatQuantiles
index|[
name|type
operator|.
name|ordinal
argument_list|()
index|]
control|)
block|{
name|q
operator|.
name|add
argument_list|(
name|latencyNanos
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|incContainerBytesStats (ContainerProtos.Type type, long bytes)
specifier|public
name|void
name|incContainerBytesStats
parameter_list|(
name|ContainerProtos
operator|.
name|Type
name|type
parameter_list|,
name|long
name|bytes
parameter_list|)
block|{
name|opsBytesArray
index|[
name|type
operator|.
name|ordinal
argument_list|()
index|]
operator|.
name|incr
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
DECL|method|getContainerBytesMetrics (ContainerProtos.Type type)
specifier|public
name|long
name|getContainerBytesMetrics
parameter_list|(
name|ContainerProtos
operator|.
name|Type
name|type
parameter_list|)
block|{
return|return
name|opsBytesArray
index|[
name|type
operator|.
name|ordinal
argument_list|()
index|]
operator|.
name|value
argument_list|()
return|;
block|}
block|}
end_class

end_unit

