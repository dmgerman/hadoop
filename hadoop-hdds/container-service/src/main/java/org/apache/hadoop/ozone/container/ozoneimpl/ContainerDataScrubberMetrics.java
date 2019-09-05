begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.ozoneimpl
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
name|ozoneimpl
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadLocalRandom
import|;
end_import

begin_comment
comment|/**  * This class captures the container data scrubber metrics on the data-node.  **/
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
literal|"DataNode container data scrubber metrics"
argument_list|,
name|context
operator|=
literal|"dfs"
argument_list|)
DECL|class|ContainerDataScrubberMetrics
specifier|public
specifier|final
class|class
name|ContainerDataScrubberMetrics
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|ms
specifier|private
specifier|final
name|MetricsSystem
name|ms
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"number of containers scanned in the current iteration"
argument_list|)
DECL|field|numContainersScanned
specifier|private
name|MutableGaugeInt
name|numContainersScanned
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"number of unhealthy containers found in the current iteration"
argument_list|)
DECL|field|numUnHealthyContainers
specifier|private
name|MutableGaugeInt
name|numUnHealthyContainers
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"number of iterations of scanner completed since the restart"
argument_list|)
DECL|field|numScanIterations
specifier|private
name|MutableCounterInt
name|numScanIterations
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"disk bandwidth used by the container data scrubber per volume"
argument_list|)
DECL|field|numBytesScanned
specifier|private
name|MutableRate
name|numBytesScanned
decl_stmt|;
DECL|method|getNumContainersScanned ()
specifier|public
name|int
name|getNumContainersScanned
parameter_list|()
block|{
return|return
name|numContainersScanned
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|incNumContainersScanned ()
specifier|public
name|void
name|incNumContainersScanned
parameter_list|()
block|{
name|numContainersScanned
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|resetNumContainersScanned ()
specifier|public
name|void
name|resetNumContainersScanned
parameter_list|()
block|{
name|numContainersScanned
operator|.
name|decr
argument_list|(
name|getNumContainersScanned
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getNumUnHealthyContainers ()
specifier|public
name|int
name|getNumUnHealthyContainers
parameter_list|()
block|{
return|return
name|numUnHealthyContainers
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|incNumUnHealthyContainers ()
specifier|public
name|void
name|incNumUnHealthyContainers
parameter_list|()
block|{
name|numUnHealthyContainers
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|resetNumUnhealthyContainers ()
specifier|public
name|void
name|resetNumUnhealthyContainers
parameter_list|()
block|{
name|numUnHealthyContainers
operator|.
name|decr
argument_list|(
name|getNumUnHealthyContainers
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getNumScanIterations ()
specifier|public
name|int
name|getNumScanIterations
parameter_list|()
block|{
return|return
name|numScanIterations
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|incNumScanIterations ()
specifier|public
name|void
name|incNumScanIterations
parameter_list|()
block|{
name|numScanIterations
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|getNumBytesScannedMean ()
specifier|public
name|double
name|getNumBytesScannedMean
parameter_list|()
block|{
return|return
name|numBytesScanned
operator|.
name|lastStat
argument_list|()
operator|.
name|mean
argument_list|()
return|;
block|}
DECL|method|getNumBytesScannedSampleCount ()
specifier|public
name|long
name|getNumBytesScannedSampleCount
parameter_list|()
block|{
return|return
name|numBytesScanned
operator|.
name|lastStat
argument_list|()
operator|.
name|numSamples
argument_list|()
return|;
block|}
DECL|method|getNumBytesScannedStdDev ()
specifier|public
name|double
name|getNumBytesScannedStdDev
parameter_list|()
block|{
return|return
name|numBytesScanned
operator|.
name|lastStat
argument_list|()
operator|.
name|stddev
argument_list|()
return|;
block|}
DECL|method|incNumBytesScanned (long bytes)
specifier|public
name|void
name|incNumBytesScanned
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
name|numBytesScanned
operator|.
name|add
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
DECL|method|unregister ()
specifier|public
name|void
name|unregister
parameter_list|()
block|{
name|ms
operator|.
name|unregisterSource
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|ContainerDataScrubberMetrics (String name, MetricsSystem ms)
specifier|private
name|ContainerDataScrubberMetrics
parameter_list|(
name|String
name|name
parameter_list|,
name|MetricsSystem
name|ms
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|ms
operator|=
name|ms
expr_stmt|;
block|}
DECL|method|create (final Configuration conf, final String volumeName)
specifier|public
specifier|static
name|ContainerDataScrubberMetrics
name|create
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|String
name|volumeName
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
name|String
name|name
init|=
literal|"ContainerDataScrubberMetrics-"
operator|+
operator|(
name|volumeName
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"UndefinedDataNodeVolume"
operator|+
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextInt
argument_list|()
else|:
name|volumeName
operator|.
name|replace
argument_list|(
literal|':'
argument_list|,
literal|'-'
argument_list|)
operator|)
decl_stmt|;
return|return
name|ms
operator|.
name|register
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
operator|new
name|ContainerDataScrubberMetrics
argument_list|(
name|name
argument_list|,
name|ms
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

