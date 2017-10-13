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
name|BlockListAsLongs
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
name|RollingUpgradeStatus
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
name|CacheReportRequestProto
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
name|CacheReportResponseProto
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
name|StorageBlockReportProto
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
name|StorageReceivedDeletedBlocksProto
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
name|RollingUpgradeStatusProto
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
name|HdfsServerProtos
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
name|HdfsServerProtos
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
name|DatanodeStorage
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
name|HeartbeatResponse
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
name|StorageBlockReport
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
name|StorageReceivedDeletedBlocks
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
name|StorageReport
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
name|VolumeFailureSummary
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
name|base
operator|.
name|Preconditions
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
DECL|field|maxDataLength
specifier|private
specifier|final
name|int
name|maxDataLength
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ErrorReportResponseProto
DECL|field|VOID_ERROR_REPORT_RESPONSE_PROTO
name|VOID_ERROR_REPORT_RESPONSE_PROTO
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
DECL|field|VOID_BLOCK_RECEIVED_AND_DELETE_RESPONSE
name|VOID_BLOCK_RECEIVED_AND_DELETE_RESPONSE
init|=
name|BlockReceivedAndDeletedResponseProto
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
name|ReportBadBlocksResponseProto
DECL|field|VOID_REPORT_BAD_BLOCK_RESPONSE
name|VOID_REPORT_BAD_BLOCK_RESPONSE
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
DECL|field|VOID_COMMIT_BLOCK_SYNCHRONIZATION_RESPONSE_PROTO
name|VOID_COMMIT_BLOCK_SYNCHRONIZATION_RESPONSE_PROTO
init|=
name|CommitBlockSynchronizationResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|method|DatanodeProtocolServerSideTranslatorPB (DatanodeProtocol impl, int maxDataLength)
specifier|public
name|DatanodeProtocolServerSideTranslatorPB
parameter_list|(
name|DatanodeProtocol
name|impl
parameter_list|,
name|int
name|maxDataLength
parameter_list|)
block|{
name|this
operator|.
name|impl
operator|=
name|impl
expr_stmt|;
name|this
operator|.
name|maxDataLength
operator|=
name|maxDataLength
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
name|HeartbeatResponse
name|response
decl_stmt|;
try|try
block|{
specifier|final
name|StorageReport
index|[]
name|report
init|=
name|PBHelperClient
operator|.
name|convertStorageReports
argument_list|(
name|request
operator|.
name|getReportsList
argument_list|()
argument_list|)
decl_stmt|;
name|VolumeFailureSummary
name|volumeFailureSummary
init|=
name|request
operator|.
name|hasVolumeFailureSummary
argument_list|()
condition|?
name|PBHelper
operator|.
name|convertVolumeFailureSummary
argument_list|(
name|request
operator|.
name|getVolumeFailureSummary
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
name|response
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
argument_list|,
name|request
operator|.
name|getCacheCapacity
argument_list|()
argument_list|,
name|request
operator|.
name|getCacheUsed
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
argument_list|,
name|volumeFailureSummary
argument_list|,
name|request
operator|.
name|getRequestFullBlockReportLease
argument_list|()
argument_list|,
name|PBHelper
operator|.
name|convertSlowPeerInfo
argument_list|(
name|request
operator|.
name|getSlowPeersList
argument_list|()
argument_list|)
argument_list|,
name|PBHelper
operator|.
name|convertSlowDiskInfo
argument_list|(
name|request
operator|.
name|getSlowDisksList
argument_list|()
argument_list|)
argument_list|,
name|PBHelper
operator|.
name|convertBlksMovReport
argument_list|(
name|request
operator|.
name|getStorageMoveAttemptFinishedBlks
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
name|DatanodeCommand
index|[]
name|cmds
init|=
name|response
operator|.
name|getCommands
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
name|builder
operator|.
name|setHaStatus
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|response
operator|.
name|getNameNodeHaState
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|RollingUpgradeStatus
name|rollingUpdateStatus
init|=
name|response
operator|.
name|getRollingUpdateStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|rollingUpdateStatus
operator|!=
literal|null
condition|)
block|{
comment|// V2 is always set for newer datanodes.
comment|// To be compatible with older datanodes, V1 is set to null
comment|//  if the RU was finalized.
name|RollingUpgradeStatusProto
name|rus
init|=
name|PBHelperClient
operator|.
name|convertRollingUpgradeStatus
argument_list|(
name|rollingUpdateStatus
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setRollingUpgradeStatusV2
argument_list|(
name|rus
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|rollingUpdateStatus
operator|.
name|isFinalized
argument_list|()
condition|)
block|{
name|builder
operator|.
name|setRollingUpgradeStatus
argument_list|(
name|rus
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|setFullBlockReportLeaseId
argument_list|(
name|response
operator|.
name|getFullBlockReportLeaseId
argument_list|()
argument_list|)
expr_stmt|;
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
name|StorageBlockReport
index|[]
name|report
init|=
operator|new
name|StorageBlockReport
index|[
name|request
operator|.
name|getReportsCount
argument_list|()
index|]
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|StorageBlockReportProto
name|s
range|:
name|request
operator|.
name|getReportsList
argument_list|()
control|)
block|{
specifier|final
name|BlockListAsLongs
name|blocks
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|hasNumberOfBlocks
argument_list|()
condition|)
block|{
comment|// new style buffer based reports
name|int
name|num
init|=
operator|(
name|int
operator|)
name|s
operator|.
name|getNumberOfBlocks
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|s
operator|.
name|getBlocksCount
argument_list|()
operator|==
literal|0
argument_list|,
literal|"cannot send both blocks list and buffers"
argument_list|)
expr_stmt|;
name|blocks
operator|=
name|BlockListAsLongs
operator|.
name|decodeBuffers
argument_list|(
name|num
argument_list|,
name|s
operator|.
name|getBlocksBuffersList
argument_list|()
argument_list|,
name|maxDataLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|blocks
operator|=
name|BlockListAsLongs
operator|.
name|decodeLongs
argument_list|(
name|s
operator|.
name|getBlocksList
argument_list|()
argument_list|,
name|maxDataLength
argument_list|)
expr_stmt|;
block|}
name|report
index|[
name|index
operator|++
index|]
operator|=
operator|new
name|StorageBlockReport
argument_list|(
name|PBHelperClient
operator|.
name|convert
argument_list|(
name|s
operator|.
name|getStorage
argument_list|()
argument_list|)
argument_list|,
name|blocks
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
name|report
argument_list|,
name|request
operator|.
name|hasContext
argument_list|()
condition|?
name|PBHelper
operator|.
name|convert
argument_list|(
name|request
operator|.
name|getContext
argument_list|()
argument_list|)
else|:
literal|null
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
DECL|method|cacheReport (RpcController controller, CacheReportRequestProto request)
specifier|public
name|CacheReportResponseProto
name|cacheReport
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|CacheReportRequestProto
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
try|try
block|{
name|cmd
operator|=
name|impl
operator|.
name|cacheReport
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
name|request
operator|.
name|getBlocksList
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
name|CacheReportResponseProto
operator|.
name|Builder
name|builder
init|=
name|CacheReportResponseProto
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
name|StorageReceivedDeletedBlocksProto
argument_list|>
name|sBlocks
init|=
name|request
operator|.
name|getBlocksList
argument_list|()
decl_stmt|;
name|StorageReceivedDeletedBlocks
index|[]
name|info
init|=
operator|new
name|StorageReceivedDeletedBlocks
index|[
name|sBlocks
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
name|sBlocks
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|StorageReceivedDeletedBlocksProto
name|sBlock
init|=
name|sBlocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ReceivedDeletedBlockInfoProto
argument_list|>
name|list
init|=
name|sBlock
operator|.
name|getBlocksList
argument_list|()
decl_stmt|;
name|ReceivedDeletedBlockInfo
index|[]
name|rdBlocks
init|=
operator|new
name|ReceivedDeletedBlockInfo
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|rdBlocks
index|[
name|j
index|]
operator|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sBlock
operator|.
name|hasStorage
argument_list|()
condition|)
block|{
name|info
index|[
name|i
index|]
operator|=
operator|new
name|StorageReceivedDeletedBlocks
argument_list|(
name|PBHelperClient
operator|.
name|convert
argument_list|(
name|sBlock
operator|.
name|getStorage
argument_list|()
argument_list|)
argument_list|,
name|rdBlocks
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|info
index|[
name|i
index|]
operator|=
operator|new
name|StorageReceivedDeletedBlocks
argument_list|(
operator|new
name|DatanodeStorage
argument_list|(
name|sBlock
operator|.
name|getStorageUuid
argument_list|()
argument_list|)
argument_list|,
name|rdBlocks
argument_list|)
expr_stmt|;
block|}
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
name|VOID_BLOCK_RECEIVED_AND_DELETE_RESPONSE
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
name|VOID_ERROR_REPORT_RESPONSE_PROTO
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
name|PBHelperClient
operator|.
name|convertLocatedBlockProto
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
name|VOID_REPORT_BAD_BLOCK_RESPONSE
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
name|PBHelperClient
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
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|sidprotos
init|=
name|request
operator|.
name|getNewTargetStoragesList
argument_list|()
decl_stmt|;
specifier|final
name|String
index|[]
name|storageIDs
init|=
name|sidprotos
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|sidprotos
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
try|try
block|{
name|impl
operator|.
name|commitBlockSynchronization
argument_list|(
name|PBHelperClient
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
argument_list|,
name|storageIDs
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
name|VOID_COMMIT_BLOCK_SYNCHRONIZATION_RESPONSE_PROTO
return|;
block|}
block|}
end_class

end_unit

