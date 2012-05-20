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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
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
name|DatanodeInfo
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
name|NamenodeCommandProto
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
name|VersionRequestProto
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
name|NamenodeProtocolProtos
operator|.
name|EndCheckpointRequestProto
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
name|NamenodeProtocolProtos
operator|.
name|ErrorReportRequestProto
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
name|NamenodeProtocolProtos
operator|.
name|GetBlockKeysRequestProto
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
name|NamenodeProtocolProtos
operator|.
name|GetBlocksRequestProto
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
name|NamenodeProtocolProtos
operator|.
name|GetEditLogManifestRequestProto
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
name|NamenodeProtocolProtos
operator|.
name|GetMostRecentCheckpointTxIdRequestProto
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
name|NamenodeProtocolProtos
operator|.
name|GetTransactionIdRequestProto
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
name|NamenodeProtocolProtos
operator|.
name|RegisterRequestProto
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
name|NamenodeProtocolProtos
operator|.
name|RollEditLogRequestProto
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
name|NamenodeProtocolProtos
operator|.
name|StartCheckpointRequestProto
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|ExportedBlockKeys
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
name|namenode
operator|.
name|CheckpointSignature
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
name|BlocksWithLocations
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
name|NamenodeCommand
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
name|NamenodeProtocol
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
name|NamenodeRegistration
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
name|NamespaceInfo
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
name|RemoteEditLogManifest
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
comment|/**  * This class is the client side translator to translate the requests made on  * {@link NamenodeProtocol} interfaces to the RPC server implementing  * {@link NamenodeProtocolPB}.  */
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
DECL|class|NamenodeProtocolTranslatorPB
specifier|public
class|class
name|NamenodeProtocolTranslatorPB
implements|implements
name|NamenodeProtocol
implements|,
name|ProtocolMetaInterface
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
comment|/*    * Protobuf requests with no parameters instantiated only once    */
DECL|field|GET_BLOCKKEYS
specifier|private
specifier|static
specifier|final
name|GetBlockKeysRequestProto
name|GET_BLOCKKEYS
init|=
name|GetBlockKeysRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|GET_TRANSACTIONID
specifier|private
specifier|static
specifier|final
name|GetTransactionIdRequestProto
name|GET_TRANSACTIONID
init|=
name|GetTransactionIdRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|ROLL_EDITLOG
specifier|private
specifier|static
specifier|final
name|RollEditLogRequestProto
name|ROLL_EDITLOG
init|=
name|RollEditLogRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|VERSION_REQUEST
specifier|private
specifier|static
specifier|final
name|VersionRequestProto
name|VERSION_REQUEST
init|=
name|VersionRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|rpcProxy
specifier|final
specifier|private
name|NamenodeProtocolPB
name|rpcProxy
decl_stmt|;
DECL|method|NamenodeProtocolTranslatorPB (NamenodeProtocolPB rpcProxy)
specifier|public
name|NamenodeProtocolTranslatorPB
parameter_list|(
name|NamenodeProtocolPB
name|rpcProxy
parameter_list|)
block|{
name|this
operator|.
name|rpcProxy
operator|=
name|rpcProxy
expr_stmt|;
block|}
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
DECL|method|getBlocks (DatanodeInfo datanode, long size)
specifier|public
name|BlocksWithLocations
name|getBlocks
parameter_list|(
name|DatanodeInfo
name|datanode
parameter_list|,
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|GetBlocksRequestProto
name|req
init|=
name|GetBlocksRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDatanode
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
operator|(
name|DatanodeID
operator|)
name|datanode
argument_list|)
argument_list|)
operator|.
name|setSize
argument_list|(
name|size
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|PBHelper
operator|.
name|convert
argument_list|(
name|rpcProxy
operator|.
name|getBlocks
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|req
argument_list|)
operator|.
name|getBlocks
argument_list|()
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
DECL|method|getBlockKeys ()
specifier|public
name|ExportedBlockKeys
name|getBlockKeys
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|PBHelper
operator|.
name|convert
argument_list|(
name|rpcProxy
operator|.
name|getBlockKeys
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|GET_BLOCKKEYS
argument_list|)
operator|.
name|getKeys
argument_list|()
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
DECL|method|getTransactionID ()
specifier|public
name|long
name|getTransactionID
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|rpcProxy
operator|.
name|getTransactionId
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|GET_TRANSACTIONID
argument_list|)
operator|.
name|getTxId
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
DECL|method|getMostRecentCheckpointTxId ()
specifier|public
name|long
name|getMostRecentCheckpointTxId
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|rpcProxy
operator|.
name|getMostRecentCheckpointTxId
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|GetMostRecentCheckpointTxIdRequestProto
operator|.
name|getDefaultInstance
argument_list|()
argument_list|)
operator|.
name|getTxId
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
DECL|method|rollEditLog ()
specifier|public
name|CheckpointSignature
name|rollEditLog
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|PBHelper
operator|.
name|convert
argument_list|(
name|rpcProxy
operator|.
name|rollEditLog
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|ROLL_EDITLOG
argument_list|)
operator|.
name|getSignature
argument_list|()
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
DECL|method|versionRequest ()
specifier|public
name|NamespaceInfo
name|versionRequest
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|PBHelper
operator|.
name|convert
argument_list|(
name|rpcProxy
operator|.
name|versionRequest
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|VERSION_REQUEST
argument_list|)
operator|.
name|getInfo
argument_list|()
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
DECL|method|errorReport (NamenodeRegistration registration, int errorCode, String msg)
specifier|public
name|void
name|errorReport
parameter_list|(
name|NamenodeRegistration
name|registration
parameter_list|,
name|int
name|errorCode
parameter_list|,
name|String
name|msg
parameter_list|)
throws|throws
name|IOException
block|{
name|ErrorReportRequestProto
name|req
init|=
name|ErrorReportRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setErrorCode
argument_list|(
name|errorCode
argument_list|)
operator|.
name|setMsg
argument_list|(
name|msg
argument_list|)
operator|.
name|setRegistration
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|registration
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|rpcProxy
operator|.
name|errorReport
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
block|}
annotation|@
name|Override
DECL|method|register (NamenodeRegistration registration)
specifier|public
name|NamenodeRegistration
name|register
parameter_list|(
name|NamenodeRegistration
name|registration
parameter_list|)
throws|throws
name|IOException
block|{
name|RegisterRequestProto
name|req
init|=
name|RegisterRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setRegistration
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|registration
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|PBHelper
operator|.
name|convert
argument_list|(
name|rpcProxy
operator|.
name|register
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|req
argument_list|)
operator|.
name|getRegistration
argument_list|()
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
DECL|method|startCheckpoint (NamenodeRegistration registration)
specifier|public
name|NamenodeCommand
name|startCheckpoint
parameter_list|(
name|NamenodeRegistration
name|registration
parameter_list|)
throws|throws
name|IOException
block|{
name|StartCheckpointRequestProto
name|req
init|=
name|StartCheckpointRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setRegistration
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|registration
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NamenodeCommandProto
name|cmd
decl_stmt|;
try|try
block|{
name|cmd
operator|=
name|rpcProxy
operator|.
name|startCheckpoint
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|req
argument_list|)
operator|.
name|getCommand
argument_list|()
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
return|return
name|PBHelper
operator|.
name|convert
argument_list|(
name|cmd
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|endCheckpoint (NamenodeRegistration registration, CheckpointSignature sig)
specifier|public
name|void
name|endCheckpoint
parameter_list|(
name|NamenodeRegistration
name|registration
parameter_list|,
name|CheckpointSignature
name|sig
parameter_list|)
throws|throws
name|IOException
block|{
name|EndCheckpointRequestProto
name|req
init|=
name|EndCheckpointRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setRegistration
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|registration
argument_list|)
argument_list|)
operator|.
name|setSignature
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|sig
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|rpcProxy
operator|.
name|endCheckpoint
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
block|}
annotation|@
name|Override
DECL|method|getEditLogManifest (long sinceTxId)
specifier|public
name|RemoteEditLogManifest
name|getEditLogManifest
parameter_list|(
name|long
name|sinceTxId
parameter_list|)
throws|throws
name|IOException
block|{
name|GetEditLogManifestRequestProto
name|req
init|=
name|GetEditLogManifestRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setSinceTxId
argument_list|(
name|sinceTxId
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|PBHelper
operator|.
name|convert
argument_list|(
name|rpcProxy
operator|.
name|getEditLogManifest
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|req
argument_list|)
operator|.
name|getManifest
argument_list|()
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
name|NamenodeProtocolPB
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
name|NamenodeProtocolPB
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

