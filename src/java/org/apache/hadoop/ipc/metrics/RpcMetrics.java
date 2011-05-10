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
name|MutableRate
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
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
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
DECL|method|RpcMetrics (Server server)
name|RpcMetrics
parameter_list|(
name|Server
name|server
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
DECL|method|create (Server server)
specifier|public
specifier|static
name|RpcMetrics
name|create
parameter_list|(
name|Server
name|server
parameter_list|)
block|{
name|RpcMetrics
name|m
init|=
operator|new
name|RpcMetrics
argument_list|(
name|server
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
DECL|field|rpcProcessingTime
annotation|@
name|Metric
argument_list|(
literal|"Processsing time"
argument_list|)
name|MutableRate
name|rpcProcessingTime
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Number of authentication failures"
argument_list|)
DECL|field|rpcAuthenticationFailures
name|MutableCounterInt
name|rpcAuthenticationFailures
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Number of authentication successes"
argument_list|)
DECL|field|rpcAuthenticationSuccesses
name|MutableCounterInt
name|rpcAuthenticationSuccesses
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Number of authorization failures"
argument_list|)
DECL|field|rpcAuthorizationFailures
name|MutableCounterInt
name|rpcAuthorizationFailures
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Number of authorization sucesses"
argument_list|)
DECL|field|rpcAuthorizationSuccesses
name|MutableCounterInt
name|rpcAuthorizationSuccesses
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
comment|// Public instrumentation methods that could be extracted to an
comment|// abstract class if we decide to do custom instrumentation classes a la
comment|// JobTrackerInstrumenation. The methods with //@Override comment are
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
block|}
block|}
end_class

end_unit

