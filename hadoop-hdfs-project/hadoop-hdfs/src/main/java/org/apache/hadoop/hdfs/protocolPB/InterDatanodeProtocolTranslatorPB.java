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
name|hdfs
operator|.
name|protocol
operator|.
name|ExtendedBlock
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
name|HdfsProtos
operator|.
name|BlockProto
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
name|InterDatanodeProtocolProtos
operator|.
name|InitReplicaRecoveryRequestProto
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
name|InterDatanodeProtocolProtos
operator|.
name|InitReplicaRecoveryResponseProto
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
name|InterDatanodeProtocolProtos
operator|.
name|UpdateReplicaUnderRecoveryRequestProto
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
name|protocol
operator|.
name|BlockRecoveryCommand
operator|.
name|RecoveringBlock
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
name|protocol
operator|.
name|InterDatanodeProtocol
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
name|protocol
operator|.
name|ReplicaRecoveryInfo
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
comment|/**  * This class is the client side translator to translate the requests made on  * {@link InterDatanodeProtocol} interfaces to the RPC server implementing  * {@link InterDatanodeProtocolPB}.  */
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
DECL|class|InterDatanodeProtocolTranslatorPB
specifier|public
class|class
name|InterDatanodeProtocolTranslatorPB
implements|implements
name|ProtocolMetaInterface
implements|,
name|InterDatanodeProtocol
implements|,
name|Closeable
block|{
comment|/** RpcController is not used and hence is set to null */
DECL|field|NULL_CONTROLLER
specifier|private
specifier|final
specifier|static
name|RpcController
name|NULL_CONTROLLER
init|=
literal|null
decl_stmt|;
DECL|field|rpcProxy
specifier|final
specifier|private
name|InterDatanodeProtocolPB
name|rpcProxy
decl_stmt|;
DECL|method|InterDatanodeProtocolTranslatorPB (InetSocketAddress addr, UserGroupInformation ugi, Configuration conf, SocketFactory factory, int socketTimeout)
specifier|public
name|InterDatanodeProtocolTranslatorPB
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|,
name|UserGroupInformation
name|ugi
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
name|InterDatanodeProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|rpcProxy
operator|=
name|RPC
operator|.
name|getProxy
argument_list|(
name|InterDatanodeProtocolPB
operator|.
name|class
argument_list|,
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|InterDatanodeProtocolPB
operator|.
name|class
argument_list|)
argument_list|,
name|addr
argument_list|,
name|ugi
argument_list|,
name|conf
argument_list|,
name|factory
argument_list|,
name|socketTimeout
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
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
DECL|method|initReplicaRecovery (RecoveringBlock rBlock)
specifier|public
name|ReplicaRecoveryInfo
name|initReplicaRecovery
parameter_list|(
name|RecoveringBlock
name|rBlock
parameter_list|)
throws|throws
name|IOException
block|{
name|InitReplicaRecoveryRequestProto
name|req
init|=
name|InitReplicaRecoveryRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBlock
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|rBlock
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|InitReplicaRecoveryResponseProto
name|resp
decl_stmt|;
try|try
block|{
name|resp
operator|=
name|rpcProxy
operator|.
name|initReplicaRecovery
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|req
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
if|if
condition|(
operator|!
name|resp
operator|.
name|getReplicaFound
argument_list|()
condition|)
block|{
comment|// No replica found on the remote node.
return|return
literal|null
return|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|resp
operator|.
name|hasBlock
argument_list|()
operator|||
operator|!
name|resp
operator|.
name|hasState
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Replica was found but missing fields. "
operator|+
literal|"Req: "
operator|+
name|req
operator|+
literal|"\n"
operator|+
literal|"Resp: "
operator|+
name|resp
argument_list|)
throw|;
block|}
block|}
name|BlockProto
name|b
init|=
name|resp
operator|.
name|getBlock
argument_list|()
decl_stmt|;
return|return
operator|new
name|ReplicaRecoveryInfo
argument_list|(
name|b
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|b
operator|.
name|getNumBytes
argument_list|()
argument_list|,
name|b
operator|.
name|getGenStamp
argument_list|()
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|resp
operator|.
name|getState
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|updateReplicaUnderRecovery (ExtendedBlock oldBlock, long recoveryId, long newLength)
specifier|public
name|String
name|updateReplicaUnderRecovery
parameter_list|(
name|ExtendedBlock
name|oldBlock
parameter_list|,
name|long
name|recoveryId
parameter_list|,
name|long
name|newLength
parameter_list|)
throws|throws
name|IOException
block|{
name|UpdateReplicaUnderRecoveryRequestProto
name|req
init|=
name|UpdateReplicaUnderRecoveryRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBlock
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|oldBlock
argument_list|)
argument_list|)
operator|.
name|setNewLength
argument_list|(
name|newLength
argument_list|)
operator|.
name|setRecoveryId
argument_list|(
name|recoveryId
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|rpcProxy
operator|.
name|updateReplicaUnderRecovery
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|req
argument_list|)
operator|.
name|getStorageUuid
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
name|InterDatanodeProtocolPB
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
name|InterDatanodeProtocolPB
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

