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
name|java
operator|.
name|util
operator|.
name|List
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
name|LocatedBlock
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
name|DatanodeProtocolProtos
operator|.
name|BlockReceivedAndDeletedRequestProto
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
name|DatanodeProtocolProtos
operator|.
name|BlockReceivedAndDeletedResponseProto
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
name|DatanodeProtocolProtos
operator|.
name|BlockReportRequestProto
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
name|DatanodeProtocolProtos
operator|.
name|BlockReportResponseProto
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
name|DatanodeProtocolProtos
operator|.
name|CommitBlockSynchronizationRequestProto
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
name|DatanodeProtocolProtos
operator|.
name|CommitBlockSynchronizationResponseProto
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
name|DatanodeProtocolProtos
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
name|DatanodeProtocolProtos
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
name|DatanodeProtocolProtos
operator|.
name|HeartbeatRequestProto
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
name|DatanodeProtocolProtos
operator|.
name|HeartbeatResponseProto
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
name|DatanodeProtocolProtos
operator|.
name|ProcessUpgradeRequestProto
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
name|DatanodeProtocolProtos
operator|.
name|ProcessUpgradeResponseProto
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
name|DatanodeProtocolProtos
operator|.
name|ReceivedDeletedBlockInfoProto
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
name|DatanodeProtocolProtos
operator|.
name|RegisterDatanodeRequestProto
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
name|DatanodeProtocolProtos
operator|.
name|RegisterDatanodeResponseProto
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
name|DatanodeProtocolProtos
operator|.
name|ReportBadBlocksRequestProto
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
name|DatanodeProtocolProtos
operator|.
name|ReportBadBlocksResponseProto
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
name|DatanodeProtocolProtos
operator|.
name|StorageReportProto
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
name|DatanodeIDProto
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
name|LocatedBlockProto
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
name|protocolR23Compatible
operator|.
name|ProtocolSignatureWritable
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
name|DatanodeCommand
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
name|DatanodeProtocol
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
name|DatanodeRegistration
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
name|ReceivedDeletedBlockInfo
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
name|UpgradeCommand
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
name|ProtocolSignature
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

begin_class
DECL|class|DatanodeProtocolServerSideTranslatorPB
specifier|public
class|class
name|DatanodeProtocolServerSideTranslatorPB
implements|implements
name|DatanodeProtocolPB
block|{
DECL|field|impl
specifier|private
specifier|final
name|DatanodeProtocol
name|impl
decl_stmt|;
DECL|field|ERROR_REPORT_RESPONSE_PROTO
specifier|private
specifier|static
specifier|final
name|ErrorReportResponseProto
name|ERROR_REPORT_RESPONSE_PROTO
init|=
name|ErrorReportResponseProto
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
name|BlockReceivedAndDeletedResponseProto
DECL|field|BLOCK_RECEIVED_AND_DELETE_RESPONSE
name|BLOCK_RECEIVED_AND_DELETE_RESPONSE
init|=
name|BlockReceivedAndDeletedResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|REPORT_BAD_BLOCK_RESPONSE
specifier|private
specifier|static
specifier|final
name|ReportBadBlocksResponseProto
name|REPORT_BAD_BLOCK_RESPONSE
init|=
name|ReportBadBlocksResponseProto
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
name|CommitBlockSynchronizationResponseProto
DECL|field|COMMIT_BLOCK_SYNCHRONIZATION_RESPONSE_PROTO
name|COMMIT_BLOCK_SYNCHRONIZATION_RESPONSE_PROTO
init|=
name|CommitBlockSynchronizationResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|method|DatanodeProtocolServerSideTranslatorPB (DatanodeProtocol impl)
specifier|public
name|DatanodeProtocolServerSideTranslatorPB
parameter_list|(
name|DatanodeProtocol
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
DECL|method|registerDatanode ( RpcController controller, RegisterDatanodeRequestProto request)
specifier|public
name|RegisterDatanodeResponseProto
name|registerDatanode
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|RegisterDatanodeRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|DatanodeRegistration
name|registration
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|request
operator|.
name|getRegistration
argument_list|()
argument_list|)
decl_stmt|;
name|DatanodeRegistration
name|registrationResp
decl_stmt|;
try|try
block|{
name|registrationResp
operator|=
name|impl
operator|.
name|registerDatanode
argument_list|(
name|registration
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
name|RegisterDatanodeResponseProto
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
name|registrationResp
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|sendHeartbeat (RpcController controller, HeartbeatRequestProto request)
specifier|public
name|HeartbeatResponseProto
name|sendHeartbeat
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|HeartbeatRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|DatanodeCommand
index|[]
name|cmds
init|=
literal|null
decl_stmt|;
try|try
block|{
name|StorageReportProto
name|report
init|=
name|request
operator|.
name|getReports
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|cmds
operator|=
name|impl
operator|.
name|sendHeartbeat
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
name|report
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|report
operator|.
name|getDfsUsed
argument_list|()
argument_list|,
name|report
operator|.
name|getRemaining
argument_list|()
argument_list|,
name|report
operator|.
name|getBlockPoolUsed
argument_list|()
argument_list|,
name|request
operator|.
name|getXmitsInProgress
argument_list|()
argument_list|,
name|request
operator|.
name|getXceiverCount
argument_list|()
argument_list|,
name|request
operator|.
name|getFailedVolumes
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
name|HeartbeatResponseProto
operator|.
name|Builder
name|builder
init|=
name|HeartbeatResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|cmds
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cmds
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|cmds
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|addCmds
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|cmds
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
DECL|method|blockReport (RpcController controller, BlockReportRequestProto request)
specifier|public
name|BlockReportResponseProto
name|blockReport
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|BlockReportRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|DatanodeCommand
name|cmd
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|blockIds
init|=
name|request
operator|.
name|getReports
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlocksList
argument_list|()
decl_stmt|;
name|long
index|[]
name|blocks
init|=
operator|new
name|long
index|[
name|blockIds
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|blockIds
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|blocks
index|[
name|i
index|]
operator|=
name|blockIds
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|cmd
operator|=
name|impl
operator|.
name|blockReport
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
name|getBlockPoolId
argument_list|()
argument_list|,
name|blocks
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
name|BlockReportResponseProto
operator|.
name|Builder
name|builder
init|=
name|BlockReportResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|cmd
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setCmd
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|cmd
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
DECL|method|blockReceivedAndDeleted ( RpcController controller, BlockReceivedAndDeletedRequestProto request)
specifier|public
name|BlockReceivedAndDeletedResponseProto
name|blockReceivedAndDeleted
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|BlockReceivedAndDeletedRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|List
argument_list|<
name|ReceivedDeletedBlockInfoProto
argument_list|>
name|rdbip
init|=
name|request
operator|.
name|getBlocks
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlocksList
argument_list|()
decl_stmt|;
name|ReceivedDeletedBlockInfo
index|[]
name|info
init|=
operator|new
name|ReceivedDeletedBlockInfo
index|[
name|rdbip
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rdbip
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|info
index|[
name|i
index|]
operator|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|rdbip
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|impl
operator|.
name|blockReceivedAndDeleted
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
name|getBlockPoolId
argument_list|()
argument_list|,
name|info
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
name|BLOCK_RECEIVED_AND_DELETE_RESPONSE
return|;
block|}
annotation|@
name|Override
DECL|method|errorReport (RpcController controller, ErrorReportRequestProto request)
specifier|public
name|ErrorReportResponseProto
name|errorReport
parameter_list|(
name|RpcController
name|controller
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
name|getRegistartion
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
name|ERROR_REPORT_RESPONSE_PROTO
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
annotation|@
name|Override
DECL|method|processUpgrade (RpcController controller, ProcessUpgradeRequestProto request)
specifier|public
name|ProcessUpgradeResponseProto
name|processUpgrade
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|ProcessUpgradeRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|UpgradeCommand
name|ret
decl_stmt|;
try|try
block|{
name|UpgradeCommand
name|cmd
init|=
name|request
operator|.
name|hasCmd
argument_list|()
condition|?
name|PBHelper
operator|.
name|convert
argument_list|(
name|request
operator|.
name|getCmd
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
name|ret
operator|=
name|impl
operator|.
name|processUpgradeCommand
argument_list|(
name|cmd
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
name|ProcessUpgradeResponseProto
operator|.
name|Builder
name|builder
init|=
name|ProcessUpgradeResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|ret
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setCmd
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|ret
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
DECL|method|reportBadBlocks (RpcController controller, ReportBadBlocksRequestProto request)
specifier|public
name|ReportBadBlocksResponseProto
name|reportBadBlocks
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|ReportBadBlocksRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|List
argument_list|<
name|LocatedBlockProto
argument_list|>
name|lbps
init|=
name|request
operator|.
name|getBlocksList
argument_list|()
decl_stmt|;
name|LocatedBlock
index|[]
name|blocks
init|=
operator|new
name|LocatedBlock
index|[
name|lbps
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|lbps
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|blocks
index|[
name|i
index|]
operator|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|lbps
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|impl
operator|.
name|reportBadBlocks
argument_list|(
name|blocks
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
name|REPORT_BAD_BLOCK_RESPONSE
return|;
block|}
annotation|@
name|Override
DECL|method|commitBlockSynchronization ( RpcController controller, CommitBlockSynchronizationRequestProto request)
specifier|public
name|CommitBlockSynchronizationResponseProto
name|commitBlockSynchronization
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|CommitBlockSynchronizationRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|List
argument_list|<
name|DatanodeIDProto
argument_list|>
name|dnprotos
init|=
name|request
operator|.
name|getNewTaragetsList
argument_list|()
decl_stmt|;
name|DatanodeID
index|[]
name|dns
init|=
operator|new
name|DatanodeID
index|[
name|dnprotos
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dnprotos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|dns
index|[
name|i
index|]
operator|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|dnprotos
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|impl
operator|.
name|commitBlockSynchronization
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|request
operator|.
name|getBlock
argument_list|()
argument_list|)
argument_list|,
name|request
operator|.
name|getNewGenStamp
argument_list|()
argument_list|,
name|request
operator|.
name|getNewLength
argument_list|()
argument_list|,
name|request
operator|.
name|getCloseFile
argument_list|()
argument_list|,
name|request
operator|.
name|getDeleteBlock
argument_list|()
argument_list|,
name|dns
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
name|COMMIT_BLOCK_SYNCHRONIZATION_RESPONSE_PROTO
return|;
block|}
annotation|@
name|Override
DECL|method|getProtocolVersion (String protocol, long clientVersion)
specifier|public
name|long
name|getProtocolVersion
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|DatanodeProtocolPB
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getProtocolSignature (String protocol, long clientVersion, int clientMethodsHash)
specifier|public
name|ProtocolSignature
name|getProtocolSignature
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|,
name|int
name|clientMethodsHash
parameter_list|)
throws|throws
name|IOException
block|{
comment|/**      * Don't forward this to the server. The protocol version and signature is      * that of {@link DatanodeProtocol}      */
if|if
condition|(
operator|!
name|protocol
operator|.
name|equals
argument_list|(
name|RPC
operator|.
name|getProtocolName
argument_list|(
name|DatanodeProtocolPB
operator|.
name|class
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Namenode Serverside implements "
operator|+
name|RPC
operator|.
name|getProtocolName
argument_list|(
name|DatanodeProtocolPB
operator|.
name|class
argument_list|)
operator|+
literal|". The following requested protocol is unknown: "
operator|+
name|protocol
argument_list|)
throw|;
block|}
return|return
name|ProtocolSignature
operator|.
name|getProtocolSignature
argument_list|(
name|clientMethodsHash
argument_list|,
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|DatanodeProtocolPB
operator|.
name|class
argument_list|)
argument_list|,
name|DatanodeProtocolPB
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getProtocolSignature2 (String protocol, long clientVersion, int clientMethodsHash)
specifier|public
name|ProtocolSignatureWritable
name|getProtocolSignature2
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|,
name|int
name|clientMethodsHash
parameter_list|)
throws|throws
name|IOException
block|{
comment|/**      * Don't forward this to the server. The protocol version and signature is      * that of {@link DatanodeProtocolPB}      */
return|return
name|ProtocolSignatureWritable
operator|.
name|convert
argument_list|(
name|this
operator|.
name|getProtocolSignature
argument_list|(
name|protocol
argument_list|,
name|clientVersion
argument_list|,
name|clientMethodsHash
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

