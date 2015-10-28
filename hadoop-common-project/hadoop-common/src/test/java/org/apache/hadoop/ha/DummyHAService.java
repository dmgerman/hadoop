begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ha
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|BlockingService
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
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
name|ha
operator|.
name|protocolPB
operator|.
name|HAServiceProtocolPB
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
name|ha
operator|.
name|protocolPB
operator|.
name|HAServiceProtocolServerSideTranslatorPB
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
name|ha
operator|.
name|proto
operator|.
name|HAServiceProtocolProtos
operator|.
name|HAServiceProtocolService
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
name|security
operator|.
name|AccessControlException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|collect
operator|.
name|Lists
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
name|fs
operator|.
name|CommonConfigurationKeys
operator|.
name|HA_HM_RPC_TIMEOUT_DEFAULT
import|;
end_import

begin_comment
comment|/**  * Test-only implementation of {@link HAServiceTarget}, which returns  * a mock implementation.  */
end_comment

begin_class
DECL|class|DummyHAService
class|class
name|DummyHAService
extends|extends
name|HAServiceTarget
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DummyHAService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DUMMY_FENCE_KEY
specifier|private
specifier|static
specifier|final
name|String
name|DUMMY_FENCE_KEY
init|=
literal|"dummy.fence.key"
decl_stmt|;
DECL|field|state
specifier|volatile
name|HAServiceState
name|state
decl_stmt|;
DECL|field|proxy
DECL|field|healthMonitorProxy
name|HAServiceProtocol
name|proxy
decl_stmt|,
name|healthMonitorProxy
decl_stmt|;
DECL|field|zkfcProxy
name|ZKFCProtocol
name|zkfcProxy
init|=
literal|null
decl_stmt|;
DECL|field|fencer
name|NodeFencer
name|fencer
decl_stmt|;
DECL|field|address
DECL|field|healthMonitorAddress
name|InetSocketAddress
name|address
decl_stmt|,
name|healthMonitorAddress
decl_stmt|;
DECL|field|isHealthy
name|boolean
name|isHealthy
init|=
literal|true
decl_stmt|;
DECL|field|actUnreachable
name|boolean
name|actUnreachable
init|=
literal|false
decl_stmt|;
DECL|field|failToBecomeActive
DECL|field|failToBecomeStandby
DECL|field|failToFence
name|boolean
name|failToBecomeActive
decl_stmt|,
name|failToBecomeStandby
decl_stmt|,
name|failToFence
decl_stmt|;
DECL|field|sharedResource
name|DummySharedResource
name|sharedResource
decl_stmt|;
DECL|field|fenceCount
specifier|public
name|int
name|fenceCount
init|=
literal|0
decl_stmt|;
DECL|field|activeTransitionCount
specifier|public
name|int
name|activeTransitionCount
init|=
literal|0
decl_stmt|;
DECL|field|testWithProtoBufRPC
name|boolean
name|testWithProtoBufRPC
init|=
literal|false
decl_stmt|;
DECL|field|instances
specifier|static
name|ArrayList
argument_list|<
name|DummyHAService
argument_list|>
name|instances
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|index
name|int
name|index
decl_stmt|;
DECL|method|DummyHAService (HAServiceState state, InetSocketAddress address)
name|DummyHAService
parameter_list|(
name|HAServiceState
name|state
parameter_list|,
name|InetSocketAddress
name|address
parameter_list|)
block|{
name|this
argument_list|(
name|state
argument_list|,
name|address
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|DummyHAService (HAServiceState state, InetSocketAddress address, boolean testWithProtoBufRPC)
name|DummyHAService
parameter_list|(
name|HAServiceState
name|state
parameter_list|,
name|InetSocketAddress
name|address
parameter_list|,
name|boolean
name|testWithProtoBufRPC
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|testWithProtoBufRPC
operator|=
name|testWithProtoBufRPC
expr_stmt|;
if|if
condition|(
name|testWithProtoBufRPC
condition|)
block|{
name|this
operator|.
name|address
operator|=
name|startAndGetRPCServerAddress
argument_list|(
name|address
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|address
operator|=
name|address
expr_stmt|;
block|}
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|this
operator|.
name|proxy
operator|=
name|makeMock
argument_list|(
name|conf
argument_list|,
name|HA_HM_RPC_TIMEOUT_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|healthMonitorProxy
operator|=
name|makeHealthMonitorMock
argument_list|(
name|conf
argument_list|,
name|HA_HM_RPC_TIMEOUT_DEFAULT
argument_list|)
expr_stmt|;
try|try
block|{
name|conf
operator|.
name|set
argument_list|(
name|DUMMY_FENCE_KEY
argument_list|,
name|DummyFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|fencer
operator|=
name|Mockito
operator|.
name|spy
argument_list|(
name|NodeFencer
operator|.
name|create
argument_list|(
name|conf
argument_list|,
name|DUMMY_FENCE_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BadFencingConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
synchronized|synchronized
init|(
name|instances
init|)
block|{
name|instances
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|instances
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|DummyHAService (HAServiceState state, InetSocketAddress address, InetSocketAddress healthMonitorAddress, boolean testWithProtoBufRPC)
name|DummyHAService
parameter_list|(
name|HAServiceState
name|state
parameter_list|,
name|InetSocketAddress
name|address
parameter_list|,
name|InetSocketAddress
name|healthMonitorAddress
parameter_list|,
name|boolean
name|testWithProtoBufRPC
parameter_list|)
block|{
name|this
argument_list|(
name|state
argument_list|,
name|address
argument_list|,
name|testWithProtoBufRPC
argument_list|)
expr_stmt|;
if|if
condition|(
name|testWithProtoBufRPC
condition|)
block|{
name|this
operator|.
name|healthMonitorAddress
operator|=
name|startAndGetRPCServerAddress
argument_list|(
name|healthMonitorAddress
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|healthMonitorAddress
operator|=
name|healthMonitorAddress
expr_stmt|;
block|}
block|}
DECL|method|setSharedResource (DummySharedResource rsrc)
specifier|public
name|void
name|setSharedResource
parameter_list|(
name|DummySharedResource
name|rsrc
parameter_list|)
block|{
name|this
operator|.
name|sharedResource
operator|=
name|rsrc
expr_stmt|;
block|}
DECL|method|startAndGetRPCServerAddress (InetSocketAddress serverAddress)
specifier|private
name|InetSocketAddress
name|startAndGetRPCServerAddress
parameter_list|(
name|InetSocketAddress
name|serverAddress
parameter_list|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
try|try
block|{
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|HAServiceProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|HAServiceProtocolServerSideTranslatorPB
name|haServiceProtocolXlator
init|=
operator|new
name|HAServiceProtocolServerSideTranslatorPB
argument_list|(
operator|new
name|MockHAProtocolImpl
argument_list|()
argument_list|)
decl_stmt|;
name|BlockingService
name|haPbService
init|=
name|HAServiceProtocolService
operator|.
name|newReflectiveBlockingService
argument_list|(
name|haServiceProtocolXlator
argument_list|)
decl_stmt|;
name|Server
name|server
init|=
operator|new
name|RPC
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setProtocol
argument_list|(
name|HAServiceProtocolPB
operator|.
name|class
argument_list|)
operator|.
name|setInstance
argument_list|(
name|haPbService
argument_list|)
operator|.
name|setBindAddress
argument_list|(
name|serverAddress
operator|.
name|getHostName
argument_list|()
argument_list|)
operator|.
name|setPort
argument_list|(
name|serverAddress
operator|.
name|getPort
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|makeMock (Configuration conf, int timeoutMs)
specifier|private
name|HAServiceProtocol
name|makeMock
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|timeoutMs
parameter_list|)
block|{
name|HAServiceProtocol
name|service
decl_stmt|;
if|if
condition|(
operator|!
name|testWithProtoBufRPC
condition|)
block|{
name|service
operator|=
operator|new
name|MockHAProtocolImpl
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|service
operator|=
name|super
operator|.
name|getProxy
argument_list|(
name|conf
argument_list|,
name|timeoutMs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
name|Mockito
operator|.
name|spy
argument_list|(
name|service
argument_list|)
return|;
block|}
DECL|method|makeHealthMonitorMock (Configuration conf, int timeoutMs)
specifier|private
name|HAServiceProtocol
name|makeHealthMonitorMock
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|timeoutMs
parameter_list|)
block|{
name|HAServiceProtocol
name|service
decl_stmt|;
if|if
condition|(
operator|!
name|testWithProtoBufRPC
condition|)
block|{
name|service
operator|=
operator|new
name|MockHAProtocolImpl
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|service
operator|=
name|super
operator|.
name|getHealthMonitorProxy
argument_list|(
name|conf
argument_list|,
name|timeoutMs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
name|Mockito
operator|.
name|spy
argument_list|(
name|service
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAddress ()
specifier|public
name|InetSocketAddress
name|getAddress
parameter_list|()
block|{
return|return
name|address
return|;
block|}
annotation|@
name|Override
DECL|method|getHealthMonitorAddress ()
specifier|public
name|InetSocketAddress
name|getHealthMonitorAddress
parameter_list|()
block|{
return|return
name|healthMonitorAddress
return|;
block|}
annotation|@
name|Override
DECL|method|getZKFCAddress ()
specifier|public
name|InetSocketAddress
name|getZKFCAddress
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getProxy (Configuration conf, int timeout)
specifier|public
name|HAServiceProtocol
name|getProxy
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|testWithProtoBufRPC
condition|)
block|{
name|proxy
operator|=
name|makeMock
argument_list|(
name|conf
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
return|return
name|proxy
return|;
block|}
annotation|@
name|Override
DECL|method|getHealthMonitorProxy (Configuration conf, int timeout)
specifier|public
name|HAServiceProtocol
name|getHealthMonitorProxy
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|testWithProtoBufRPC
condition|)
block|{
name|proxy
operator|=
name|makeHealthMonitorMock
argument_list|(
name|conf
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
return|return
name|proxy
return|;
block|}
annotation|@
name|Override
DECL|method|getZKFCProxy (Configuration conf, int timeout)
specifier|public
name|ZKFCProtocol
name|getZKFCProxy
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|zkfcProxy
operator|!=
literal|null
assert|;
return|return
name|zkfcProxy
return|;
block|}
annotation|@
name|Override
DECL|method|getFencer ()
specifier|public
name|NodeFencer
name|getFencer
parameter_list|()
block|{
return|return
name|fencer
return|;
block|}
annotation|@
name|Override
DECL|method|checkFencingConfigured ()
specifier|public
name|void
name|checkFencingConfigured
parameter_list|()
throws|throws
name|BadFencingConfigurationException
block|{   }
annotation|@
name|Override
DECL|method|isAutoFailoverEnabled ()
specifier|public
name|boolean
name|isAutoFailoverEnabled
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DummyHAService #"
operator|+
name|index
return|;
block|}
DECL|method|getInstance (int serial)
specifier|public
specifier|static
name|HAServiceTarget
name|getInstance
parameter_list|(
name|int
name|serial
parameter_list|)
block|{
return|return
name|instances
operator|.
name|get
argument_list|(
name|serial
operator|-
literal|1
argument_list|)
return|;
block|}
DECL|class|MockHAProtocolImpl
specifier|private
class|class
name|MockHAProtocolImpl
implements|implements
name|HAServiceProtocol
implements|,
name|Closeable
block|{
annotation|@
name|Override
DECL|method|monitorHealth ()
specifier|public
name|void
name|monitorHealth
parameter_list|()
throws|throws
name|HealthCheckFailedException
throws|,
name|AccessControlException
throws|,
name|IOException
block|{
name|checkUnreachable
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isHealthy
condition|)
block|{
throw|throw
operator|new
name|HealthCheckFailedException
argument_list|(
literal|"not healthy"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|transitionToActive (StateChangeRequestInfo req)
specifier|public
name|void
name|transitionToActive
parameter_list|(
name|StateChangeRequestInfo
name|req
parameter_list|)
throws|throws
name|ServiceFailedException
throws|,
name|AccessControlException
throws|,
name|IOException
block|{
name|activeTransitionCount
operator|++
expr_stmt|;
name|checkUnreachable
argument_list|()
expr_stmt|;
if|if
condition|(
name|failToBecomeActive
condition|)
block|{
throw|throw
operator|new
name|ServiceFailedException
argument_list|(
literal|"injected failure"
argument_list|)
throw|;
block|}
if|if
condition|(
name|sharedResource
operator|!=
literal|null
condition|)
block|{
name|sharedResource
operator|.
name|take
argument_list|(
name|DummyHAService
operator|.
name|this
argument_list|)
expr_stmt|;
block|}
name|state
operator|=
name|HAServiceState
operator|.
name|ACTIVE
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|transitionToStandby (StateChangeRequestInfo req)
specifier|public
name|void
name|transitionToStandby
parameter_list|(
name|StateChangeRequestInfo
name|req
parameter_list|)
throws|throws
name|ServiceFailedException
throws|,
name|AccessControlException
throws|,
name|IOException
block|{
name|checkUnreachable
argument_list|()
expr_stmt|;
if|if
condition|(
name|failToBecomeStandby
condition|)
block|{
throw|throw
operator|new
name|ServiceFailedException
argument_list|(
literal|"injected failure"
argument_list|)
throw|;
block|}
if|if
condition|(
name|sharedResource
operator|!=
literal|null
condition|)
block|{
name|sharedResource
operator|.
name|release
argument_list|(
name|DummyHAService
operator|.
name|this
argument_list|)
expr_stmt|;
block|}
name|state
operator|=
name|HAServiceState
operator|.
name|STANDBY
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getServiceStatus ()
specifier|public
name|HAServiceStatus
name|getServiceStatus
parameter_list|()
throws|throws
name|IOException
block|{
name|checkUnreachable
argument_list|()
expr_stmt|;
name|HAServiceStatus
name|ret
init|=
operator|new
name|HAServiceStatus
argument_list|(
name|state
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|==
name|HAServiceState
operator|.
name|STANDBY
operator|||
name|state
operator|==
name|HAServiceState
operator|.
name|ACTIVE
condition|)
block|{
name|ret
operator|.
name|setReadyToBecomeActive
argument_list|()
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|checkUnreachable ()
specifier|private
name|void
name|checkUnreachable
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|actUnreachable
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Connection refused (fake)"
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
throws|throws
name|IOException
block|{     }
block|}
DECL|class|DummyFencer
specifier|public
specifier|static
class|class
name|DummyFencer
implements|implements
name|FenceMethod
block|{
annotation|@
name|Override
DECL|method|checkArgs (String args)
specifier|public
name|void
name|checkArgs
parameter_list|(
name|String
name|args
parameter_list|)
throws|throws
name|BadFencingConfigurationException
block|{     }
annotation|@
name|Override
DECL|method|tryFence (HAServiceTarget target, String args)
specifier|public
name|boolean
name|tryFence
parameter_list|(
name|HAServiceTarget
name|target
parameter_list|,
name|String
name|args
parameter_list|)
throws|throws
name|BadFencingConfigurationException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"tryFence("
operator|+
name|target
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|DummyHAService
name|svc
init|=
operator|(
name|DummyHAService
operator|)
name|target
decl_stmt|;
synchronized|synchronized
init|(
name|svc
init|)
block|{
name|svc
operator|.
name|fenceCount
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|svc
operator|.
name|failToFence
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Injected failure to fence"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|svc
operator|.
name|sharedResource
operator|.
name|release
argument_list|(
name|svc
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

