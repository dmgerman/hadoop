begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocolPB
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocolPB
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
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|SocketFactory
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
name|classification
operator|.
name|InterfaceStability
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
name|conf
operator|.
name|ReconfigurationTaskStatus
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
name|protocol
operator|.
name|ReconfigurationProtocol
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
name|protocol
operator|.
name|proto
operator|.
name|ReconfigurationProtocolProtos
operator|.
name|GetReconfigurationStatusRequestProto
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
name|protocol
operator|.
name|proto
operator|.
name|ReconfigurationProtocolProtos
operator|.
name|ListReconfigurablePropertiesRequestProto
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
name|protocol
operator|.
name|proto
operator|.
name|ReconfigurationProtocolProtos
operator|.
name|ListReconfigurablePropertiesResponseProto
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
name|protocol
operator|.
name|proto
operator|.
name|ReconfigurationProtocolProtos
operator|.
name|StartReconfigurationRequestProto
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
name|ProtobufHelper
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
name|ProtocolMetaInterface
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
name|ProtocolTranslator
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
name|RpcClientUtil
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
name|protobuf
operator|.
name|RpcController
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
name|ServiceException
import|;
end_import

begin_comment
comment|/**  * This class is the client side translator to translate the requests made on  * {@link ReconfigurationProtocol} interfaces to the RPC server implementing  * {@link ReconfigurationProtocolPB}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|ReconfigurationProtocolTranslatorPB
specifier|public
class|class
name|ReconfigurationProtocolTranslatorPB
implements|implements
name|ProtocolMetaInterface
implements|,
name|ReconfigurationProtocol
implements|,
name|ProtocolTranslator
implements|,
name|Closeable
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
name|ReconfigurationProtocolTranslatorPB
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NULL_CONTROLLER
specifier|private
specifier|static
specifier|final
name|RpcController
name|NULL_CONTROLLER
init|=
literal|null
decl_stmt|;
DECL|field|VOID_START_RECONFIG
specifier|private
specifier|static
specifier|final
name|StartReconfigurationRequestProto
name|VOID_START_RECONFIG
init|=
name|StartReconfigurationRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ListReconfigurablePropertiesRequestProto
DECL|field|VOID_LIST_RECONFIGURABLE_PROPERTIES
name|VOID_LIST_RECONFIGURABLE_PROPERTIES
init|=
name|ListReconfigurablePropertiesRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|GetReconfigurationStatusRequestProto
DECL|field|VOID_GET_RECONFIG_STATUS
name|VOID_GET_RECONFIG_STATUS
init|=
name|GetReconfigurationStatusRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|rpcProxy
specifier|private
specifier|final
name|ReconfigurationProtocolPB
name|rpcProxy
decl_stmt|;
DECL|method|ReconfigurationProtocolTranslatorPB (InetSocketAddress addr, UserGroupInformation ticket, Configuration conf, SocketFactory factory)
specifier|public
name|ReconfigurationProtocolTranslatorPB
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|,
name|UserGroupInformation
name|ticket
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|SocketFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|rpcProxy
operator|=
name|createReconfigurationProtocolProxy
argument_list|(
name|addr
argument_list|,
name|ticket
argument_list|,
name|conf
argument_list|,
name|factory
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|createReconfigurationProtocolProxy ( InetSocketAddress addr, UserGroupInformation ticket, Configuration conf, SocketFactory factory, int socketTimeout)
specifier|static
name|ReconfigurationProtocolPB
name|createReconfigurationProtocolProxy
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|,
name|UserGroupInformation
name|ticket
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|SocketFactory
name|factory
parameter_list|,
name|int
name|socketTimeout
parameter_list|)
throws|throws
name|IOException
block|{
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|ReconfigurationProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|RPC
operator|.
name|getProxy
argument_list|(
name|ReconfigurationProtocolPB
operator|.
name|class
argument_list|,
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|ReconfigurationProtocolPB
operator|.
name|class
argument_list|)
argument_list|,
name|addr
argument_list|,
name|ticket
argument_list|,
name|conf
argument_list|,
name|factory
argument_list|,
name|socketTimeout
argument_list|)
return|;
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
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|rpcProxy
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUnderlyingProxyObject ()
specifier|public
name|Object
name|getUnderlyingProxyObject
parameter_list|()
block|{
return|return
name|rpcProxy
return|;
block|}
annotation|@
name|Override
DECL|method|startReconfiguration ()
specifier|public
name|void
name|startReconfiguration
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|rpcProxy
operator|.
name|startReconfiguration
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|VOID_START_RECONFIG
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
throw|throw
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getReconfigurationStatus ()
specifier|public
name|ReconfigurationTaskStatus
name|getReconfigurationStatus
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|ReconfigurationProtocolUtils
operator|.
name|getReconfigurationStatus
argument_list|(
name|rpcProxy
operator|.
name|getReconfigurationStatus
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|VOID_GET_RECONFIG_STATUS
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
throw|throw
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|listReconfigurableProperties ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|listReconfigurableProperties
parameter_list|()
throws|throws
name|IOException
block|{
name|ListReconfigurablePropertiesResponseProto
name|response
decl_stmt|;
try|try
block|{
name|response
operator|=
name|rpcProxy
operator|.
name|listReconfigurableProperties
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|VOID_LIST_RECONFIGURABLE_PROPERTIES
argument_list|)
expr_stmt|;
return|return
name|response
operator|.
name|getNameList
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
throw|throw
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|isMethodSupported (String methodName)
specifier|public
name|boolean
name|isMethodSupported
parameter_list|(
name|String
name|methodName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|RpcClientUtil
operator|.
name|isMethodSupported
argument_list|(
name|rpcProxy
argument_list|,
name|ReconfigurationProtocolPB
operator|.
name|class
argument_list|,
name|RPC
operator|.
name|RpcKind
operator|.
name|RPC_PROTOCOL_BUFFER
argument_list|,
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|ReconfigurationProtocolPB
operator|.
name|class
argument_list|)
argument_list|,
name|methodName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

