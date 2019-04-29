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
name|MutableRatesWithAggregation
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
comment|/**  * This class is for maintaining RPC method related statistics  * and publishing them through the metrics interfaces.  */
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
literal|"Per method RPC metrics"
argument_list|,
name|context
operator|=
literal|"rpcdetailed"
argument_list|)
DECL|class|RpcDetailedMetrics
specifier|public
class|class
name|RpcDetailedMetrics
block|{
DECL|field|rates
annotation|@
name|Metric
name|MutableRatesWithAggregation
name|rates
decl_stmt|;
DECL|field|deferredRpcRates
annotation|@
name|Metric
name|MutableRatesWithAggregation
name|deferredRpcRates
decl_stmt|;
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
name|RpcDetailedMetrics
operator|.
name|class
argument_list|)
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
DECL|method|RpcDetailedMetrics (int port)
name|RpcDetailedMetrics
parameter_list|(
name|int
name|port
parameter_list|)
block|{
name|name
operator|=
literal|"RpcDetailedActivityForPort"
operator|+
name|port
expr_stmt|;
name|registry
operator|=
operator|new
name|MetricsRegistry
argument_list|(
literal|"rpcdetailed"
argument_list|)
operator|.
name|tag
argument_list|(
literal|"port"
argument_list|,
literal|"RPC port"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|port
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|registry
operator|.
name|info
argument_list|()
operator|.
name|toString
argument_list|()
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
DECL|method|create (int port)
specifier|public
specifier|static
name|RpcDetailedMetrics
name|create
parameter_list|(
name|int
name|port
parameter_list|)
block|{
name|RpcDetailedMetrics
name|m
init|=
operator|new
name|RpcDetailedMetrics
argument_list|(
name|port
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
comment|/**    * Initialize the metrics for JMX with protocol methods    * @param protocol the protocol class    */
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
name|rates
operator|.
name|init
argument_list|(
name|protocol
argument_list|)
expr_stmt|;
name|deferredRpcRates
operator|.
name|init
argument_list|(
name|protocol
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add an RPC processing time sample    * @param rpcCallName of the RPC call    * @param processingTime  the processing time    */
comment|//@Override // some instrumentation interface
DECL|method|addProcessingTime (String rpcCallName, long processingTime)
specifier|public
name|void
name|addProcessingTime
parameter_list|(
name|String
name|rpcCallName
parameter_list|,
name|long
name|processingTime
parameter_list|)
block|{
name|rates
operator|.
name|add
argument_list|(
name|rpcCallName
argument_list|,
name|processingTime
argument_list|)
expr_stmt|;
block|}
DECL|method|addDeferredProcessingTime (String name, long processingTime)
specifier|public
name|void
name|addDeferredProcessingTime
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|processingTime
parameter_list|)
block|{
name|deferredRpcRates
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|processingTime
argument_list|)
expr_stmt|;
block|}
comment|/**    * Shutdown the instrumentation for the process    */
comment|//@Override // some instrumentation interface
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{}
block|}
end_class

end_unit

