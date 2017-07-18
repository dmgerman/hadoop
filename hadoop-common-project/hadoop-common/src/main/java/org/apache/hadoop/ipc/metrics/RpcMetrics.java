begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|ipc
operator|.
name|Server
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
comment|/**  * This class is for maintaining  the various RPC statistics  * and publishing them through the metrics interfaces.  */
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
literal|"Aggregate RPC metrics"
argument_list|,
name|context
operator|=
literal|"rpc"
argument_list|)
DECL|class|RpcMetrics
specifier|public
class|class
name|RpcMetrics
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
name|RpcMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|server
specifier|final
name|Server
name|server
decl_stmt|;
DECL|field|registry
specifier|final
name|MetricsRegistry
name|registry
decl_stmt|;
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|rpcQuantileEnable
specifier|final
name|boolean
name|rpcQuantileEnable
decl_stmt|;
DECL|method|RpcMetrics (Server server, Configuration conf)
name|RpcMetrics
parameter_list|(
name|Server
name|server
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|port
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|server
operator|.
name|getListenerAddress
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
name|name
operator|=
literal|"RpcActivityForPort"
operator|+
name|port
expr_stmt|;
name|this
operator|.
name|server
operator|=
name|server
expr_stmt|;
name|registry
operator|=
operator|new
name|MetricsRegistry
argument_list|(
literal|"rpc"
argument_list|)
operator|.
name|tag
argument_list|(
literal|"port"
argument_list|,
literal|"RPC port"
argument_list|,
name|port
argument_list|)
expr_stmt|;
name|int
index|[]
name|intervals
init|=
name|conf
operator|.
name|getInts
argument_list|(
name|CommonConfigurationKeys
operator|.
name|RPC_METRICS_PERCENTILES_INTERVALS_KEY
argument_list|)
decl_stmt|;
name|rpcQuantileEnable
operator|=
operator|(
name|intervals
operator|.
name|length
operator|>
literal|0
operator|)
operator|&&
name|conf
operator|.
name|getBoolean
argument_list|(
name|CommonConfigurationKeys
operator|.
name|RPC_METRICS_QUANTILE_ENABLE
argument_list|,
name|CommonConfigurationKeys
operator|.
name|RPC_METRICS_QUANTILE_ENABLE_DEFAULT
argument_list|)
expr_stmt|;
if|if
condition|(
name|rpcQuantileEnable
condition|)
block|{
name|rpcQueueTimeMillisQuantiles
operator|=
operator|new
name|MutableQuantiles
index|[
name|intervals
operator|.
name|length
index|]
expr_stmt|;
name|rpcProcessingTimeMillisQuantiles
operator|=
operator|new
name|MutableQuantiles
index|[
name|intervals
operator|.
name|length
index|]
expr_stmt|;
name|deferredRpcProcessingTimeMillisQuantiles
operator|=
operator|new
name|MutableQuantiles
index|[
name|intervals
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
name|intervals
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
name|intervals
index|[
name|i
index|]
decl_stmt|;
name|rpcQueueTimeMillisQuantiles
index|[
name|i
index|]
operator|=
name|registry
operator|.
name|newQuantiles
argument_list|(
literal|"rpcQueueTime"
operator|+
name|interval
operator|+
literal|"s"
argument_list|,
literal|"rpc queue time in milli second"
argument_list|,
literal|"ops"
argument_list|,
literal|"latency"
argument_list|,
name|interval
argument_list|)
expr_stmt|;
name|rpcProcessingTimeMillisQuantiles
index|[
name|i
index|]
operator|=
name|registry
operator|.
name|newQuantiles
argument_list|(
literal|"rpcProcessingTime"
operator|+
name|interval
operator|+
literal|"s"
argument_list|,
literal|"rpc processing time in milli second"
argument_list|,
literal|"ops"
argument_list|,
literal|"latency"
argument_list|,
name|interval
argument_list|)
expr_stmt|;
name|deferredRpcProcessingTimeMillisQuantiles
index|[
name|i
index|]
operator|=
name|registry
operator|.
name|newQuantiles
argument_list|(
literal|"deferredRpcProcessingTime"
operator|+
name|interval
operator|+
literal|"s"
argument_list|,
literal|"deferred rpc processing time in milli seconds"
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Initialized "
operator|+
name|registry
argument_list|)
expr_stmt|;
block|}
DECL|method|name ()
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|create (Server server, Configuration conf)
specifier|public
specifier|static
name|RpcMetrics
name|create
parameter_list|(
name|Server
name|server
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|RpcMetrics
name|m
init|=
operator|new
name|RpcMetrics
argument_list|(
name|server
argument_list|,
name|conf
argument_list|)
decl_stmt|;
return|return
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
operator|.
name|register
argument_list|(
name|m
operator|.
name|name
argument_list|,
literal|null
argument_list|,
name|m
argument_list|)
return|;
block|}
DECL|field|receivedBytes
annotation|@
name|Metric
argument_list|(
literal|"Number of received bytes"
argument_list|)
name|MutableCounterLong
name|receivedBytes
decl_stmt|;
DECL|field|sentBytes
annotation|@
name|Metric
argument_list|(
literal|"Number of sent bytes"
argument_list|)
name|MutableCounterLong
name|sentBytes
decl_stmt|;
DECL|field|rpcQueueTime
annotation|@
name|Metric
argument_list|(
literal|"Queue time"
argument_list|)
name|MutableRate
name|rpcQueueTime
decl_stmt|;
DECL|field|rpcQueueTimeMillisQuantiles
name|MutableQuantiles
index|[]
name|rpcQueueTimeMillisQuantiles
decl_stmt|;
DECL|field|rpcProcessingTime
annotation|@
name|Metric
argument_list|(
literal|"Processing time"
argument_list|)
name|MutableRate
name|rpcProcessingTime
decl_stmt|;
DECL|field|rpcProcessingTimeMillisQuantiles
name|MutableQuantiles
index|[]
name|rpcProcessingTimeMillisQuantiles
decl_stmt|;
DECL|field|deferredRpcProcessingTime
annotation|@
name|Metric
argument_list|(
literal|"Deferred Processing time"
argument_list|)
name|MutableRate
name|deferredRpcProcessingTime
decl_stmt|;
DECL|field|deferredRpcProcessingTimeMillisQuantiles
name|MutableQuantiles
index|[]
name|deferredRpcProcessingTimeMillisQuantiles
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Number of authentication failures"
argument_list|)
DECL|field|rpcAuthenticationFailures
name|MutableCounterLong
name|rpcAuthenticationFailures
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Number of authentication successes"
argument_list|)
DECL|field|rpcAuthenticationSuccesses
name|MutableCounterLong
name|rpcAuthenticationSuccesses
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Number of authorization failures"
argument_list|)
DECL|field|rpcAuthorizationFailures
name|MutableCounterLong
name|rpcAuthorizationFailures
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Number of authorization successes"
argument_list|)
DECL|field|rpcAuthorizationSuccesses
name|MutableCounterLong
name|rpcAuthorizationSuccesses
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Number of client backoff requests"
argument_list|)
DECL|field|rpcClientBackoff
name|MutableCounterLong
name|rpcClientBackoff
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Number of Slow RPC calls"
argument_list|)
DECL|field|rpcSlowCalls
name|MutableCounterLong
name|rpcSlowCalls
decl_stmt|;
DECL|method|numOpenConnections ()
annotation|@
name|Metric
argument_list|(
literal|"Number of open connections"
argument_list|)
specifier|public
name|int
name|numOpenConnections
parameter_list|()
block|{
return|return
name|server
operator|.
name|getNumOpenConnections
argument_list|()
return|;
block|}
annotation|@
name|Metric
argument_list|(
literal|"Number of open connections per user"
argument_list|)
DECL|method|numOpenConnectionsPerUser ()
specifier|public
name|String
name|numOpenConnectionsPerUser
parameter_list|()
block|{
return|return
name|server
operator|.
name|getNumOpenConnectionsPerUser
argument_list|()
return|;
block|}
DECL|method|callQueueLength ()
annotation|@
name|Metric
argument_list|(
literal|"Length of the call queue"
argument_list|)
specifier|public
name|int
name|callQueueLength
parameter_list|()
block|{
return|return
name|server
operator|.
name|getCallQueueLen
argument_list|()
return|;
block|}
DECL|method|numDroppedConnections ()
annotation|@
name|Metric
argument_list|(
literal|"Number of dropped connections"
argument_list|)
specifier|public
name|long
name|numDroppedConnections
parameter_list|()
block|{
return|return
name|server
operator|.
name|getNumDroppedConnections
argument_list|()
return|;
block|}
comment|// Public instrumentation methods that could be extracted to an
comment|// abstract class if we decide to do custom instrumentation classes a la
comment|// JobTrackerInstrumentation. The methods with //@Override comment are
comment|// candidates for abstract methods in a abstract instrumentation class.
comment|/**    * One authentication failure event    */
comment|//@Override
DECL|method|incrAuthenticationFailures ()
specifier|public
name|void
name|incrAuthenticationFailures
parameter_list|()
block|{
name|rpcAuthenticationFailures
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * One authentication success event    */
comment|//@Override
DECL|method|incrAuthenticationSuccesses ()
specifier|public
name|void
name|incrAuthenticationSuccesses
parameter_list|()
block|{
name|rpcAuthenticationSuccesses
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * One authorization success event    */
comment|//@Override
DECL|method|incrAuthorizationSuccesses ()
specifier|public
name|void
name|incrAuthorizationSuccesses
parameter_list|()
block|{
name|rpcAuthorizationSuccesses
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * One authorization failure event    */
comment|//@Override
DECL|method|incrAuthorizationFailures ()
specifier|public
name|void
name|incrAuthorizationFailures
parameter_list|()
block|{
name|rpcAuthorizationFailures
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * Shutdown the instrumentation for the process    */
comment|//@Override
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{}
comment|/**    * Increment sent bytes by count    * @param count to increment    */
comment|//@Override
DECL|method|incrSentBytes (int count)
specifier|public
name|void
name|incrSentBytes
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|sentBytes
operator|.
name|incr
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
comment|/**    * Increment received bytes by count    * @param count to increment    */
comment|//@Override
DECL|method|incrReceivedBytes (int count)
specifier|public
name|void
name|incrReceivedBytes
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|receivedBytes
operator|.
name|incr
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add an RPC queue time sample    * @param qTime the queue time    */
comment|//@Override
DECL|method|addRpcQueueTime (int qTime)
specifier|public
name|void
name|addRpcQueueTime
parameter_list|(
name|int
name|qTime
parameter_list|)
block|{
name|rpcQueueTime
operator|.
name|add
argument_list|(
name|qTime
argument_list|)
expr_stmt|;
if|if
condition|(
name|rpcQuantileEnable
condition|)
block|{
for|for
control|(
name|MutableQuantiles
name|q
range|:
name|rpcQueueTimeMillisQuantiles
control|)
block|{
name|q
operator|.
name|add
argument_list|(
name|qTime
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Add an RPC processing time sample    * @param processingTime the processing time    */
comment|//@Override
DECL|method|addRpcProcessingTime (int processingTime)
specifier|public
name|void
name|addRpcProcessingTime
parameter_list|(
name|int
name|processingTime
parameter_list|)
block|{
name|rpcProcessingTime
operator|.
name|add
argument_list|(
name|processingTime
argument_list|)
expr_stmt|;
if|if
condition|(
name|rpcQuantileEnable
condition|)
block|{
for|for
control|(
name|MutableQuantiles
name|q
range|:
name|rpcProcessingTimeMillisQuantiles
control|)
block|{
name|q
operator|.
name|add
argument_list|(
name|processingTime
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addDeferredRpcProcessingTime (long processingTime)
specifier|public
name|void
name|addDeferredRpcProcessingTime
parameter_list|(
name|long
name|processingTime
parameter_list|)
block|{
name|deferredRpcProcessingTime
operator|.
name|add
argument_list|(
name|processingTime
argument_list|)
expr_stmt|;
if|if
condition|(
name|rpcQuantileEnable
condition|)
block|{
for|for
control|(
name|MutableQuantiles
name|q
range|:
name|deferredRpcProcessingTimeMillisQuantiles
control|)
block|{
name|q
operator|.
name|add
argument_list|(
name|processingTime
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * One client backoff event    */
comment|//@Override
DECL|method|incrClientBackoff ()
specifier|public
name|void
name|incrClientBackoff
parameter_list|()
block|{
name|rpcClientBackoff
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * Increments the Slow RPC counter.    */
DECL|method|incrSlowRpc ()
specifier|public
name|void
name|incrSlowRpc
parameter_list|()
block|{
name|rpcSlowCalls
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns a MutableRate Counter.    * @return Mutable Rate    */
DECL|method|getRpcProcessingTime ()
specifier|public
name|MutableRate
name|getRpcProcessingTime
parameter_list|()
block|{
return|return
name|rpcProcessingTime
return|;
block|}
comment|/**    * Returns the number of samples that we have seen so far.    * @return long    */
DECL|method|getProcessingSampleCount ()
specifier|public
name|long
name|getProcessingSampleCount
parameter_list|()
block|{
return|return
name|rpcProcessingTime
operator|.
name|lastStat
argument_list|()
operator|.
name|numSamples
argument_list|()
return|;
block|}
comment|/**    * Returns mean of RPC Processing Times.    * @return double    */
DECL|method|getProcessingMean ()
specifier|public
name|double
name|getProcessingMean
parameter_list|()
block|{
return|return
name|rpcProcessingTime
operator|.
name|lastStat
argument_list|()
operator|.
name|mean
argument_list|()
return|;
block|}
comment|/**    * Return Standard Deviation of the Processing Time.    * @return  double    */
DECL|method|getProcessingStdDev ()
specifier|public
name|double
name|getProcessingStdDev
parameter_list|()
block|{
return|return
name|rpcProcessingTime
operator|.
name|lastStat
argument_list|()
operator|.
name|stddev
argument_list|()
return|;
block|}
comment|/**    * Returns the number of slow calls.    * @return long    */
DECL|method|getRpcSlowCalls ()
specifier|public
name|long
name|getRpcSlowCalls
parameter_list|()
block|{
return|return
name|rpcSlowCalls
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getDeferredRpcProcessingTime ()
specifier|public
name|MutableRate
name|getDeferredRpcProcessingTime
parameter_list|()
block|{
return|return
name|deferredRpcProcessingTime
return|;
block|}
DECL|method|getDeferredRpcProcessingSampleCount ()
specifier|public
name|long
name|getDeferredRpcProcessingSampleCount
parameter_list|()
block|{
return|return
name|deferredRpcProcessingTime
operator|.
name|lastStat
argument_list|()
operator|.
name|numSamples
argument_list|()
return|;
block|}
DECL|method|getDeferredRpcProcessingMean ()
specifier|public
name|double
name|getDeferredRpcProcessingMean
parameter_list|()
block|{
return|return
name|deferredRpcProcessingTime
operator|.
name|lastStat
argument_list|()
operator|.
name|mean
argument_list|()
return|;
block|}
DECL|method|getDeferredRpcProcessingStdDev ()
specifier|public
name|double
name|getDeferredRpcProcessingStdDev
parameter_list|()
block|{
return|return
name|deferredRpcProcessingTime
operator|.
name|lastStat
argument_list|()
operator|.
name|stddev
argument_list|()
return|;
block|}
block|}
end_class

end_unit

