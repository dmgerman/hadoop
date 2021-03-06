begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.client.impl.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
operator|.
name|impl
operator|.
name|metrics
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
name|hdfs
operator|.
name|client
operator|.
name|impl
operator|.
name|DfsClientConf
operator|.
name|ShortCircuitConf
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
name|Timer
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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
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
comment|/**  * Profiles {@link org.apache.hadoop.hdfs.client.impl.BlockReaderLocal} short  * circuit read latencies when ShortCircuit read metrics is enabled through  * {@link ShortCircuitConf#scrMetricsEnabled}.  */
end_comment

begin_class
DECL|class|BlockReaderIoProvider
specifier|public
class|class
name|BlockReaderIoProvider
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BlockReaderIoProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|metrics
specifier|private
specifier|final
name|BlockReaderLocalMetrics
name|metrics
decl_stmt|;
DECL|field|isEnabled
specifier|private
specifier|final
name|boolean
name|isEnabled
decl_stmt|;
DECL|field|sampleRangeMax
specifier|private
specifier|final
name|int
name|sampleRangeMax
decl_stmt|;
DECL|field|timer
specifier|private
specifier|final
name|Timer
name|timer
decl_stmt|;
comment|// Threshold in milliseconds above which a warning should be flagged.
DECL|field|SLOW_READ_WARNING_THRESHOLD_MS
specifier|private
specifier|static
specifier|final
name|long
name|SLOW_READ_WARNING_THRESHOLD_MS
init|=
literal|1000
decl_stmt|;
DECL|field|isWarningLogged
specifier|private
name|boolean
name|isWarningLogged
init|=
literal|false
decl_stmt|;
DECL|method|BlockReaderIoProvider (@ullable ShortCircuitConf conf, BlockReaderLocalMetrics metrics, Timer timer)
specifier|public
name|BlockReaderIoProvider
parameter_list|(
annotation|@
name|Nullable
name|ShortCircuitConf
name|conf
parameter_list|,
name|BlockReaderLocalMetrics
name|metrics
parameter_list|,
name|Timer
name|timer
parameter_list|)
block|{
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
name|isEnabled
operator|=
name|conf
operator|.
name|isScrMetricsEnabled
argument_list|()
expr_stmt|;
name|sampleRangeMax
operator|=
operator|(
name|Integer
operator|.
name|MAX_VALUE
operator|/
literal|100
operator|)
operator|*
name|conf
operator|.
name|getScrMetricsSamplingPercentage
argument_list|()
expr_stmt|;
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
name|this
operator|.
name|timer
operator|=
name|timer
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|isEnabled
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|sampleRangeMax
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|metrics
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|timer
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|read (FileChannel dataIn, ByteBuffer dst, long position)
specifier|public
name|int
name|read
parameter_list|(
name|FileChannel
name|dataIn
parameter_list|,
name|ByteBuffer
name|dst
parameter_list|,
name|long
name|position
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|nRead
decl_stmt|;
if|if
condition|(
name|isEnabled
operator|&&
operator|(
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|<
name|sampleRangeMax
operator|)
condition|)
block|{
name|long
name|begin
init|=
name|timer
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|nRead
operator|=
name|dataIn
operator|.
name|read
argument_list|(
name|dst
argument_list|,
name|position
argument_list|)
expr_stmt|;
name|long
name|latency
init|=
name|timer
operator|.
name|monotonicNow
argument_list|()
operator|-
name|begin
decl_stmt|;
name|addLatency
argument_list|(
name|latency
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nRead
operator|=
name|dataIn
operator|.
name|read
argument_list|(
name|dst
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
return|return
name|nRead
return|;
block|}
DECL|method|addLatency (long latency)
specifier|private
name|void
name|addLatency
parameter_list|(
name|long
name|latency
parameter_list|)
block|{
name|metrics
operator|.
name|addShortCircuitReadLatency
argument_list|(
name|latency
argument_list|)
expr_stmt|;
if|if
condition|(
name|latency
operator|>
name|SLOW_READ_WARNING_THRESHOLD_MS
operator|&&
operator|!
name|isWarningLogged
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"The Short Circuit Local Read latency, %d ms, "
operator|+
literal|"is higher then the threshold (%d ms). Suppressing further warnings"
operator|+
literal|" for this BlockReaderLocal."
argument_list|,
name|latency
argument_list|,
name|SLOW_READ_WARNING_THRESHOLD_MS
argument_list|)
argument_list|)
expr_stmt|;
name|isWarningLogged
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

