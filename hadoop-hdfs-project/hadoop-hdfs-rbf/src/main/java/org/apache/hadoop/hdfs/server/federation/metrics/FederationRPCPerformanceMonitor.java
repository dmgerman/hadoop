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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
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
name|Executors
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
name|ThreadFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|NotCompliantMBeanException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|StandardMBean
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
name|server
operator|.
name|federation
operator|.
name|router
operator|.
name|RouterRpcMonitor
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
name|server
operator|.
name|federation
operator|.
name|router
operator|.
name|RouterRpcServer
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|StateStoreService
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
name|MBeans
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
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
name|util
operator|.
name|Time
operator|.
name|monotonicNow
import|;
end_import

begin_comment
comment|/**  * Customizable RPC performance monitor. Receives events from the RPC server  * and aggregates them via JMX.  */
end_comment

begin_class
DECL|class|FederationRPCPerformanceMonitor
specifier|public
class|class
name|FederationRPCPerformanceMonitor
implements|implements
name|RouterRpcMonitor
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FederationRPCPerformanceMonitor
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Time for an operation to be received in the Router. */
DECL|field|START_TIME
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|Long
argument_list|>
name|START_TIME
init|=
operator|new
name|ThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Time for an operation to be send to the Namenode. */
DECL|field|PROXY_TIME
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|Long
argument_list|>
name|PROXY_TIME
init|=
operator|new
name|ThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Configuration for the performance monitor. */
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
comment|/** RPC server for the Router. */
DECL|field|server
specifier|private
name|RouterRpcServer
name|server
decl_stmt|;
comment|/** State Store. */
DECL|field|store
specifier|private
name|StateStoreService
name|store
decl_stmt|;
comment|/** JMX interface to monitor the RPC metrics. */
DECL|field|metrics
specifier|private
name|FederationRPCMetrics
name|metrics
decl_stmt|;
DECL|field|registeredBean
specifier|private
name|ObjectName
name|registeredBean
decl_stmt|;
comment|/** Thread pool for logging stats. */
DECL|field|executor
specifier|private
name|ExecutorService
name|executor
decl_stmt|;
annotation|@
name|Override
DECL|method|init (Configuration configuration, RouterRpcServer rpcServer, StateStoreService stateStore)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|RouterRpcServer
name|rpcServer
parameter_list|,
name|StateStoreService
name|stateStore
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|configuration
expr_stmt|;
name|this
operator|.
name|server
operator|=
name|rpcServer
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|stateStore
expr_stmt|;
comment|// Create metrics
name|this
operator|.
name|metrics
operator|=
name|FederationRPCMetrics
operator|.
name|create
argument_list|(
name|conf
argument_list|,
name|server
argument_list|)
expr_stmt|;
comment|// Create thread pool
name|ThreadFactory
name|threadFactory
init|=
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"Federation RPC Performance Monitor-%d"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|this
operator|.
name|executor
operator|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|1
argument_list|,
name|threadFactory
argument_list|)
expr_stmt|;
comment|// Adding JMX interface
try|try
block|{
name|StandardMBean
name|bean
init|=
operator|new
name|StandardMBean
argument_list|(
name|this
operator|.
name|metrics
argument_list|,
name|FederationRPCMBean
operator|.
name|class
argument_list|)
decl_stmt|;
name|registeredBean
operator|=
name|MBeans
operator|.
name|register
argument_list|(
literal|"Router"
argument_list|,
literal|"FederationRPC"
argument_list|,
name|bean
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Registered FederationRPCMBean: {}"
argument_list|,
name|registeredBean
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotCompliantMBeanException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Bad FederationRPCMBean setup"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|registeredBean
operator|!=
literal|null
condition|)
block|{
name|MBeans
operator|.
name|unregister
argument_list|(
name|registeredBean
argument_list|)
expr_stmt|;
name|registeredBean
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|executor
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Resets all RPC service performance counters to their defaults.    */
DECL|method|resetPerfCounters ()
specifier|public
name|void
name|resetPerfCounters
parameter_list|()
block|{
if|if
condition|(
name|registeredBean
operator|!=
literal|null
condition|)
block|{
name|MBeans
operator|.
name|unregister
argument_list|(
name|registeredBean
argument_list|)
expr_stmt|;
name|registeredBean
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|metrics
operator|!=
literal|null
condition|)
block|{
name|FederationRPCMetrics
operator|.
name|reset
argument_list|()
expr_stmt|;
name|metrics
operator|=
literal|null
expr_stmt|;
block|}
name|init
argument_list|(
name|conf
argument_list|,
name|server
argument_list|,
name|store
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startOp ()
specifier|public
name|void
name|startOp
parameter_list|()
block|{
name|START_TIME
operator|.
name|set
argument_list|(
name|monotonicNow
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|proxyOp ()
specifier|public
name|long
name|proxyOp
parameter_list|()
block|{
name|PROXY_TIME
operator|.
name|set
argument_list|(
name|monotonicNow
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|processingTime
init|=
name|getProcessingTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|processingTime
operator|>=
literal|0
condition|)
block|{
name|metrics
operator|.
name|addProcessingTime
argument_list|(
name|processingTime
argument_list|)
expr_stmt|;
block|}
return|return
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getId
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|proxyOpComplete (boolean success)
specifier|public
name|void
name|proxyOpComplete
parameter_list|(
name|boolean
name|success
parameter_list|)
block|{
if|if
condition|(
name|success
condition|)
block|{
name|long
name|proxyTime
init|=
name|getProxyTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|proxyTime
operator|>=
literal|0
condition|)
block|{
name|metrics
operator|.
name|addProxyTime
argument_list|(
name|proxyTime
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|proxyOpFailureStandby ()
specifier|public
name|void
name|proxyOpFailureStandby
parameter_list|()
block|{
name|metrics
operator|.
name|incrProxyOpFailureStandby
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|proxyOpFailureCommunicate ()
specifier|public
name|void
name|proxyOpFailureCommunicate
parameter_list|()
block|{
name|metrics
operator|.
name|incrProxyOpFailureCommunicate
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|proxyOpFailureClientOverloaded ()
specifier|public
name|void
name|proxyOpFailureClientOverloaded
parameter_list|()
block|{
name|metrics
operator|.
name|incrProxyOpFailureClientOverloaded
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|proxyOpNotImplemented ()
specifier|public
name|void
name|proxyOpNotImplemented
parameter_list|()
block|{
name|metrics
operator|.
name|incrProxyOpNotImplemented
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|proxyOpRetries ()
specifier|public
name|void
name|proxyOpRetries
parameter_list|()
block|{
name|metrics
operator|.
name|incrProxyOpRetries
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|proxyOpNoNamenodes ()
specifier|public
name|void
name|proxyOpNoNamenodes
parameter_list|()
block|{
name|metrics
operator|.
name|incrProxyOpNoNamenodes
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|routerFailureStateStore ()
specifier|public
name|void
name|routerFailureStateStore
parameter_list|()
block|{
name|metrics
operator|.
name|incrRouterFailureStateStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|routerFailureSafemode ()
specifier|public
name|void
name|routerFailureSafemode
parameter_list|()
block|{
name|metrics
operator|.
name|incrRouterFailureSafemode
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|routerFailureReadOnly ()
specifier|public
name|void
name|routerFailureReadOnly
parameter_list|()
block|{
name|metrics
operator|.
name|incrRouterFailureReadOnly
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|routerFailureLocked ()
specifier|public
name|void
name|routerFailureLocked
parameter_list|()
block|{
name|metrics
operator|.
name|incrRouterFailureLocked
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get time between we receiving the operation and sending it to the Namenode.    * @return Processing time in nanoseconds.    */
DECL|method|getProcessingTime ()
specifier|private
name|long
name|getProcessingTime
parameter_list|()
block|{
if|if
condition|(
name|START_TIME
operator|.
name|get
argument_list|()
operator|!=
literal|null
operator|&&
name|START_TIME
operator|.
name|get
argument_list|()
operator|>
literal|0
operator|&&
name|PROXY_TIME
operator|.
name|get
argument_list|()
operator|!=
literal|null
operator|&&
name|PROXY_TIME
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|PROXY_TIME
operator|.
name|get
argument_list|()
operator|-
name|START_TIME
operator|.
name|get
argument_list|()
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/**    * Get time between now and when the operation was forwarded to the Namenode.    * @return Current proxy time in nanoseconds.    */
DECL|method|getProxyTime ()
specifier|private
name|long
name|getProxyTime
parameter_list|()
block|{
if|if
condition|(
name|PROXY_TIME
operator|.
name|get
argument_list|()
operator|!=
literal|null
operator|&&
name|PROXY_TIME
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|monotonicNow
argument_list|()
operator|-
name|PROXY_TIME
operator|.
name|get
argument_list|()
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getRPCMetrics ()
specifier|public
name|FederationRPCMetrics
name|getRPCMetrics
parameter_list|()
block|{
return|return
name|this
operator|.
name|metrics
return|;
block|}
block|}
end_class

end_unit

