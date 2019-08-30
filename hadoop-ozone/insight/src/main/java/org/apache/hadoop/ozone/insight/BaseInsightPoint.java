begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.insight
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|insight
package|;
end_package

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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|StorageUnit
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
name|HddsUtils
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
name|conf
operator|.
name|OzoneConfiguration
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
name|scm
operator|.
name|ScmConfigKeys
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
name|scm
operator|.
name|XceiverClientManager
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
name|scm
operator|.
name|client
operator|.
name|ContainerOperationClient
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
name|scm
operator|.
name|client
operator|.
name|ScmClient
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
name|scm
operator|.
name|protocol
operator|.
name|StorageContainerLocationProtocol
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
name|scm
operator|.
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
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
name|scm
operator|.
name|protocolPB
operator|.
name|StorageContainerLocationProtocolPB
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
name|server
operator|.
name|PrometheusMetricsSink
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
name|tracing
operator|.
name|TracingUtil
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
name|Client
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
name|ProtobufRpcEngine
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
name|RPC
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
name|net
operator|.
name|NetUtils
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
name|ozone
operator|.
name|OzoneConsts
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
name|ozone
operator|.
name|insight
operator|.
name|LoggerSource
operator|.
name|Level
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
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ProtocolMessageEnum
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
name|hdds
operator|.
name|HddsUtils
operator|.
name|getScmAddressForClients
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE_DEFAULT
import|;
end_import

begin_comment
comment|/**  * Default implementation of Insight point logic.  */
end_comment

begin_class
DECL|class|BaseInsightPoint
specifier|public
specifier|abstract
class|class
name|BaseInsightPoint
implements|implements
name|InsightPoint
block|{
comment|/**    * List the related metrics.    */
annotation|@
name|Override
DECL|method|getMetrics ()
specifier|public
name|List
argument_list|<
name|MetricGroupDisplay
argument_list|>
name|getMetrics
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|()
return|;
block|}
comment|/**    * List the related configuration.    */
annotation|@
name|Override
DECL|method|getConfigurationClasses ()
specifier|public
name|List
argument_list|<
name|Class
argument_list|>
name|getConfigurationClasses
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|()
return|;
block|}
comment|/**    * List the related loggers.    *    * @param verbose true if verbose logging is requested.    */
annotation|@
name|Override
DECL|method|getRelatedLoggers (boolean verbose)
specifier|public
name|List
argument_list|<
name|LoggerSource
argument_list|>
name|getRelatedLoggers
parameter_list|(
name|boolean
name|verbose
parameter_list|)
block|{
name|List
argument_list|<
name|LoggerSource
argument_list|>
name|loggers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
return|return
name|loggers
return|;
block|}
comment|/**    * Create scm client.    */
DECL|method|createScmClient (OzoneConfiguration ozoneConf)
specifier|public
name|ScmClient
name|createScmClient
parameter_list|(
name|OzoneConfiguration
name|ozoneConf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|HddsUtils
operator|.
name|getHostNameFromConfigKeys
argument_list|(
name|ozoneConf
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|)
operator|.
name|isPresent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_ADDRESS_KEY
operator|+
literal|" should be set in ozone-site.xml"
argument_list|)
throw|;
block|}
name|long
name|version
init|=
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|StorageContainerLocationProtocolPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|scmAddress
init|=
name|getScmAddressForClients
argument_list|(
name|ozoneConf
argument_list|)
decl_stmt|;
name|int
name|containerSizeGB
init|=
operator|(
name|int
operator|)
name|ozoneConf
operator|.
name|getStorageSize
argument_list|(
name|OZONE_SCM_CONTAINER_SIZE
argument_list|,
name|OZONE_SCM_CONTAINER_SIZE_DEFAULT
argument_list|,
name|StorageUnit
operator|.
name|GB
argument_list|)
decl_stmt|;
name|ContainerOperationClient
operator|.
name|setContainerSizeB
argument_list|(
name|containerSizeGB
operator|*
name|OzoneConsts
operator|.
name|GB
argument_list|)
expr_stmt|;
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|ozoneConf
argument_list|,
name|StorageContainerLocationProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|StorageContainerLocationProtocol
name|client
init|=
name|TracingUtil
operator|.
name|createProxy
argument_list|(
operator|new
name|StorageContainerLocationProtocolClientSideTranslatorPB
argument_list|(
name|RPC
operator|.
name|getProxy
argument_list|(
name|StorageContainerLocationProtocolPB
operator|.
name|class
argument_list|,
name|version
argument_list|,
name|scmAddress
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
name|ozoneConf
argument_list|,
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|ozoneConf
argument_list|)
argument_list|,
name|Client
operator|.
name|getRpcTimeout
argument_list|(
name|ozoneConf
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|StorageContainerLocationProtocol
operator|.
name|class
argument_list|,
name|ozoneConf
argument_list|)
decl_stmt|;
return|return
operator|new
name|ContainerOperationClient
argument_list|(
name|client
argument_list|,
operator|new
name|XceiverClientManager
argument_list|(
name|ozoneConf
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Convenient method to define default log levels.    */
DECL|method|defaultLevel (boolean verbose)
specifier|public
name|Level
name|defaultLevel
parameter_list|(
name|boolean
name|verbose
parameter_list|)
block|{
return|return
name|verbose
condition|?
name|Level
operator|.
name|TRACE
else|:
name|Level
operator|.
name|DEBUG
return|;
block|}
comment|/**    * Default metrics for any message type based RPC ServerSide translators.    */
DECL|method|addProtocolMessageMetrics (List<MetricGroupDisplay> metrics, String prefix, Component.Type component, ProtocolMessageEnum[] types)
specifier|public
name|void
name|addProtocolMessageMetrics
parameter_list|(
name|List
argument_list|<
name|MetricGroupDisplay
argument_list|>
name|metrics
parameter_list|,
name|String
name|prefix
parameter_list|,
name|Component
operator|.
name|Type
name|component
parameter_list|,
name|ProtocolMessageEnum
index|[]
name|types
parameter_list|)
block|{
name|MetricGroupDisplay
name|messageTypeCounters
init|=
operator|new
name|MetricGroupDisplay
argument_list|(
name|component
argument_list|,
literal|"Message type counters"
argument_list|)
decl_stmt|;
for|for
control|(
name|ProtocolMessageEnum
name|type
range|:
name|types
control|)
block|{
name|String
name|typeName
init|=
name|type
operator|.
name|toString
argument_list|()
decl_stmt|;
name|MetricDisplay
name|metricDisplay
init|=
operator|new
name|MetricDisplay
argument_list|(
literal|"Number of "
operator|+
name|typeName
argument_list|,
name|prefix
operator|+
literal|"_"
operator|+
name|PrometheusMetricsSink
operator|.
name|normalizeName
argument_list|(
name|typeName
argument_list|)
argument_list|)
decl_stmt|;
name|messageTypeCounters
operator|.
name|addMetrics
argument_list|(
name|metricDisplay
argument_list|)
expr_stmt|;
block|}
name|metrics
operator|.
name|add
argument_list|(
name|messageTypeCounters
argument_list|)
expr_stmt|;
block|}
comment|/**    * Rpc metrics for any hadoop rpc endpoint.    */
DECL|method|addRpcMetrics (List<MetricGroupDisplay> metrics, Component.Type component, Map<String, String> filter)
specifier|public
name|void
name|addRpcMetrics
parameter_list|(
name|List
argument_list|<
name|MetricGroupDisplay
argument_list|>
name|metrics
parameter_list|,
name|Component
operator|.
name|Type
name|component
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|filter
parameter_list|)
block|{
name|MetricGroupDisplay
name|connection
init|=
operator|new
name|MetricGroupDisplay
argument_list|(
name|component
argument_list|,
literal|"RPC connections"
argument_list|)
decl_stmt|;
name|connection
operator|.
name|addMetrics
argument_list|(
operator|new
name|MetricDisplay
argument_list|(
literal|"Open connections"
argument_list|,
literal|"rpc_num_open_connections"
argument_list|,
name|filter
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|addMetrics
argument_list|(
operator|new
name|MetricDisplay
argument_list|(
literal|"Dropped connections"
argument_list|,
literal|"rpc_num_dropped_connections"
argument_list|,
name|filter
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|addMetrics
argument_list|(
operator|new
name|MetricDisplay
argument_list|(
literal|"Received bytes"
argument_list|,
literal|"rpc_received_bytes"
argument_list|,
name|filter
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|addMetrics
argument_list|(
operator|new
name|MetricDisplay
argument_list|(
literal|"Sent bytes"
argument_list|,
literal|"rpc_sent_bytes"
argument_list|,
name|filter
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|MetricGroupDisplay
name|queue
init|=
operator|new
name|MetricGroupDisplay
argument_list|(
name|component
argument_list|,
literal|"RPC queue"
argument_list|)
decl_stmt|;
name|queue
operator|.
name|addMetrics
argument_list|(
operator|new
name|MetricDisplay
argument_list|(
literal|"RPC average queue time"
argument_list|,
literal|"rpc_rpc_queue_time_avg_time"
argument_list|,
name|filter
argument_list|)
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addMetrics
argument_list|(
operator|new
name|MetricDisplay
argument_list|(
literal|"RPC call queue length"
argument_list|,
literal|"rpc_call_queue_length"
argument_list|,
name|filter
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|add
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|MetricGroupDisplay
name|performance
init|=
operator|new
name|MetricGroupDisplay
argument_list|(
name|component
argument_list|,
literal|"RPC performance"
argument_list|)
decl_stmt|;
name|performance
operator|.
name|addMetrics
argument_list|(
operator|new
name|MetricDisplay
argument_list|(
literal|"RPC processing time average"
argument_list|,
literal|"rpc_rpc_processing_time_avg_time"
argument_list|,
name|filter
argument_list|)
argument_list|)
expr_stmt|;
name|performance
operator|.
name|addMetrics
argument_list|(
operator|new
name|MetricDisplay
argument_list|(
literal|"Number of slow calls"
argument_list|,
literal|"rpc_rpc_slow_calls"
argument_list|,
name|filter
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|add
argument_list|(
name|performance
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

