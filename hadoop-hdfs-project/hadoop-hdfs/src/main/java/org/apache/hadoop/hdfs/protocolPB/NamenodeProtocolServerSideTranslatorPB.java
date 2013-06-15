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
name|HdfsProtos
operator|.
name|VersionResponseProto
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
name|EndCheckpointResponseProto
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
name|ErrorReportResponseProto
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
name|GetBlockKeysResponseProto
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
name|GetBlocksResponseProto
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
name|GetEditLogManifestResponseProto
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
name|GetMostRecentCheckpointTxIdResponseProto
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
name|GetTransactionIdResponseProto
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
name|RegisterResponseProto
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
name|RollEditLogResponseProto
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
name|protocol
operator|.
name|proto
operator|.
name|NamenodeProtocolProtos
operator|.
name|StartCheckpointResponseProto
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
comment|/**  * Implementation for protobuf service that forwards requests  * received on {@link NamenodeProtocolPB} to the  * {@link NamenodeProtocol} server implementation.  */
end_comment

begin_class
DECL|class|NamenodeProtocolServerSideTranslatorPB
specifier|public
class|class
name|NamenodeProtocolServerSideTranslatorPB
implements|implements
name|NamenodeProtocolPB
block|{
DECL|field|impl
specifier|private
specifier|final
name|NamenodeProtocol
name|impl
decl_stmt|;
DECL|field|VOID_ERROR_REPORT_RESPONSE
specifier|private
specifier|final
specifier|static
name|ErrorReportResponseProto
name|VOID_ERROR_REPORT_RESPONSE
init|=
name|ErrorReportResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|VOID_END_CHECKPOINT_RESPONSE
specifier|private
specifier|final
specifier|static
name|EndCheckpointResponseProto
name|VOID_END_CHECKPOINT_RESPONSE
init|=
name|EndCheckpointResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|method|NamenodeProtocolServerSideTranslatorPB (NamenodeProtocol impl)
specifier|public
name|NamenodeProtocolServerSideTranslatorPB
parameter_list|(
name|NamenodeProtocol
name|impl
parameter_list|)
block|{
name|this
operator|.
name|impl
operator|=
name|impl
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBlocks (RpcController unused, GetBlocksRequestProto request)
specifier|public
name|GetBlocksResponseProto
name|getBlocks
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|GetBlocksRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|DatanodeInfo
name|dnInfo
init|=
operator|new
name|DatanodeInfo
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|request
operator|.
name|getDatanode
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|BlocksWithLocations
name|blocks
decl_stmt|;
try|try
block|{
name|blocks
operator|=
name|impl
operator|.
name|getBlocks
argument_list|(
name|dnInfo
argument_list|,
name|request
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|GetBlocksResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBlocks
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|blocks
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getBlockKeys (RpcController unused, GetBlockKeysRequestProto request)
specifier|public
name|GetBlockKeysResponseProto
name|getBlockKeys
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|GetBlockKeysRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|ExportedBlockKeys
name|keys
decl_stmt|;
try|try
block|{
name|keys
operator|=
name|impl
operator|.
name|getBlockKeys
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|GetBlockKeysResponseProto
operator|.
name|Builder
name|builder
init|=
name|GetBlockKeysResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|keys
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setKeys
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|keys
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTransactionId (RpcController unused, GetTransactionIdRequestProto request)
specifier|public
name|GetTransactionIdResponseProto
name|getTransactionId
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|GetTransactionIdRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|long
name|txid
decl_stmt|;
try|try
block|{
name|txid
operator|=
name|impl
operator|.
name|getTransactionID
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|GetTransactionIdResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setTxId
argument_list|(
name|txid
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMostRecentCheckpointTxId ( RpcController unused, GetMostRecentCheckpointTxIdRequestProto request)
specifier|public
name|GetMostRecentCheckpointTxIdResponseProto
name|getMostRecentCheckpointTxId
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|GetMostRecentCheckpointTxIdRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|long
name|txid
decl_stmt|;
try|try
block|{
name|txid
operator|=
name|impl
operator|.
name|getMostRecentCheckpointTxId
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|GetMostRecentCheckpointTxIdResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setTxId
argument_list|(
name|txid
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|rollEditLog (RpcController unused, RollEditLogRequestProto request)
specifier|public
name|RollEditLogResponseProto
name|rollEditLog
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|RollEditLogRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|CheckpointSignature
name|signature
decl_stmt|;
try|try
block|{
name|signature
operator|=
name|impl
operator|.
name|rollEditLog
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|RollEditLogResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setSignature
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|signature
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|errorReport (RpcController unused, ErrorReportRequestProto request)
specifier|public
name|ErrorReportResponseProto
name|errorReport
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|ErrorReportRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|impl
operator|.
name|errorReport
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|request
operator|.
name|getRegistration
argument_list|()
argument_list|)
argument_list|,
name|request
operator|.
name|getErrorCode
argument_list|()
argument_list|,
name|request
operator|.
name|getMsg
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|VOID_ERROR_REPORT_RESPONSE
return|;
block|}
annotation|@
name|Override
DECL|method|registerSubordinateNamenode ( RpcController unused, RegisterRequestProto request)
specifier|public
name|RegisterResponseProto
name|registerSubordinateNamenode
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|RegisterRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|NamenodeRegistration
name|reg
decl_stmt|;
try|try
block|{
name|reg
operator|=
name|impl
operator|.
name|registerSubordinateNamenode
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|request
operator|.
name|getRegistration
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|RegisterResponseProto
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
name|reg
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|startCheckpoint (RpcController unused, StartCheckpointRequestProto request)
specifier|public
name|StartCheckpointResponseProto
name|startCheckpoint
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|StartCheckpointRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|NamenodeCommand
name|cmd
decl_stmt|;
try|try
block|{
name|cmd
operator|=
name|impl
operator|.
name|startCheckpoint
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|request
operator|.
name|getRegistration
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|StartCheckpointResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCommand
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|cmd
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|endCheckpoint (RpcController unused, EndCheckpointRequestProto request)
specifier|public
name|EndCheckpointResponseProto
name|endCheckpoint
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|EndCheckpointRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|impl
operator|.
name|endCheckpoint
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|request
operator|.
name|getRegistration
argument_list|()
argument_list|)
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|request
operator|.
name|getSignature
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|VOID_END_CHECKPOINT_RESPONSE
return|;
block|}
annotation|@
name|Override
DECL|method|getEditLogManifest ( RpcController unused, GetEditLogManifestRequestProto request)
specifier|public
name|GetEditLogManifestResponseProto
name|getEditLogManifest
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|GetEditLogManifestRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|RemoteEditLogManifest
name|manifest
decl_stmt|;
try|try
block|{
name|manifest
operator|=
name|impl
operator|.
name|getEditLogManifest
argument_list|(
name|request
operator|.
name|getSinceTxId
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|GetEditLogManifestResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setManifest
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|manifest
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|versionRequest (RpcController controller, VersionRequestProto request)
specifier|public
name|VersionResponseProto
name|versionRequest
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|VersionRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|NamespaceInfo
name|info
decl_stmt|;
try|try
block|{
name|info
operator|=
name|impl
operator|.
name|versionRequest
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|VersionResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setInfo
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|info
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

