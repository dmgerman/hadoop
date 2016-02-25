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
name|hdfs
operator|.
name|client
operator|.
name|BlockReportOptions
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
name|BlockLocalPathInfo
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
name|ClientDatanodeProtocol
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
name|ClientDatanodeProtocolProtos
operator|.
name|DeleteBlockPoolRequestProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|DeleteBlockPoolResponseProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|EvictWritersRequestProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|EvictWritersResponseProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|GetBalancerBandwidthRequestProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|GetBalancerBandwidthResponseProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|GetBlockLocalPathInfoRequestProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|GetBlockLocalPathInfoResponseProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|GetDatanodeInfoRequestProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|GetDatanodeInfoResponseProto
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
name|GetReconfigurationStatusResponseProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|GetReplicaVisibleLengthRequestProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|GetReplicaVisibleLengthResponseProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|RefreshNamenodesRequestProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|RefreshNamenodesResponseProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|ShutdownDatanodeRequestProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|ShutdownDatanodeResponseProto
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
name|hdfs
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ReconfigurationProtocolProtos
operator|.
name|StartReconfigurationResponseProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|TriggerBlockReportRequestProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|TriggerBlockReportResponseProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|SubmitDiskBalancerPlanRequestProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|SubmitDiskBalancerPlanResponseProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|CancelPlanRequestProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|CancelPlanResponseProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|QueryPlanStatusRequestProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|QueryPlanStatusResponseProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|DiskBalancerSettingRequestProto
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
name|ClientDatanodeProtocolProtos
operator|.
name|DiskBalancerSettingResponseProto
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
name|datanode
operator|.
name|DiskBalancerWorkStatus
import|;
end_import

begin_comment
comment|/**  * Implementation for protobuf service that forwards requests  * received on {@link ClientDatanodeProtocolPB} to the  * {@link ClientDatanodeProtocol} server implementation.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ClientDatanodeProtocolServerSideTranslatorPB
specifier|public
class|class
name|ClientDatanodeProtocolServerSideTranslatorPB
implements|implements
name|ClientDatanodeProtocolPB
block|{
DECL|field|REFRESH_NAMENODE_RESP
specifier|private
specifier|final
specifier|static
name|RefreshNamenodesResponseProto
name|REFRESH_NAMENODE_RESP
init|=
name|RefreshNamenodesResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|DELETE_BLOCKPOOL_RESP
specifier|private
specifier|final
specifier|static
name|DeleteBlockPoolResponseProto
name|DELETE_BLOCKPOOL_RESP
init|=
name|DeleteBlockPoolResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|SHUTDOWN_DATANODE_RESP
specifier|private
specifier|final
specifier|static
name|ShutdownDatanodeResponseProto
name|SHUTDOWN_DATANODE_RESP
init|=
name|ShutdownDatanodeResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|START_RECONFIG_RESP
specifier|private
specifier|final
specifier|static
name|StartReconfigurationResponseProto
name|START_RECONFIG_RESP
init|=
name|StartReconfigurationResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|TRIGGER_BLOCK_REPORT_RESP
specifier|private
specifier|final
specifier|static
name|TriggerBlockReportResponseProto
name|TRIGGER_BLOCK_REPORT_RESP
init|=
name|TriggerBlockReportResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|EVICT_WRITERS_RESP
specifier|private
specifier|final
specifier|static
name|EvictWritersResponseProto
name|EVICT_WRITERS_RESP
init|=
name|EvictWritersResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|impl
specifier|private
specifier|final
name|ClientDatanodeProtocol
name|impl
decl_stmt|;
DECL|method|ClientDatanodeProtocolServerSideTranslatorPB ( ClientDatanodeProtocol impl)
specifier|public
name|ClientDatanodeProtocolServerSideTranslatorPB
parameter_list|(
name|ClientDatanodeProtocol
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
DECL|method|getReplicaVisibleLength ( RpcController unused, GetReplicaVisibleLengthRequestProto request)
specifier|public
name|GetReplicaVisibleLengthResponseProto
name|getReplicaVisibleLength
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|GetReplicaVisibleLengthRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|long
name|len
decl_stmt|;
try|try
block|{
name|len
operator|=
name|impl
operator|.
name|getReplicaVisibleLength
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
name|GetReplicaVisibleLengthResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setLength
argument_list|(
name|len
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|refreshNamenodes ( RpcController unused, RefreshNamenodesRequestProto request)
specifier|public
name|RefreshNamenodesResponseProto
name|refreshNamenodes
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|RefreshNamenodesRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|impl
operator|.
name|refreshNamenodes
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
name|REFRESH_NAMENODE_RESP
return|;
block|}
annotation|@
name|Override
DECL|method|deleteBlockPool (RpcController unused, DeleteBlockPoolRequestProto request)
specifier|public
name|DeleteBlockPoolResponseProto
name|deleteBlockPool
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|DeleteBlockPoolRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|impl
operator|.
name|deleteBlockPool
argument_list|(
name|request
operator|.
name|getBlockPool
argument_list|()
argument_list|,
name|request
operator|.
name|getForce
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
name|DELETE_BLOCKPOOL_RESP
return|;
block|}
annotation|@
name|Override
DECL|method|getBlockLocalPathInfo ( RpcController unused, GetBlockLocalPathInfoRequestProto request)
specifier|public
name|GetBlockLocalPathInfoResponseProto
name|getBlockLocalPathInfo
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|GetBlockLocalPathInfoRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|BlockLocalPathInfo
name|resp
decl_stmt|;
try|try
block|{
name|resp
operator|=
name|impl
operator|.
name|getBlockLocalPathInfo
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
name|PBHelperClient
operator|.
name|convert
argument_list|(
name|request
operator|.
name|getToken
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
name|GetBlockLocalPathInfoResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBlock
argument_list|(
name|PBHelperClient
operator|.
name|convert
argument_list|(
name|resp
operator|.
name|getBlock
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setLocalPath
argument_list|(
name|resp
operator|.
name|getBlockPath
argument_list|()
argument_list|)
operator|.
name|setLocalMetaPath
argument_list|(
name|resp
operator|.
name|getMetaPath
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|shutdownDatanode ( RpcController unused, ShutdownDatanodeRequestProto request)
specifier|public
name|ShutdownDatanodeResponseProto
name|shutdownDatanode
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|ShutdownDatanodeRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|impl
operator|.
name|shutdownDatanode
argument_list|(
name|request
operator|.
name|getForUpgrade
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
name|SHUTDOWN_DATANODE_RESP
return|;
block|}
annotation|@
name|Override
DECL|method|evictWriters (RpcController unused, EvictWritersRequestProto request)
specifier|public
name|EvictWritersResponseProto
name|evictWriters
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|EvictWritersRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|impl
operator|.
name|evictWriters
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
name|EVICT_WRITERS_RESP
return|;
block|}
DECL|method|getDatanodeInfo (RpcController unused, GetDatanodeInfoRequestProto request)
specifier|public
name|GetDatanodeInfoResponseProto
name|getDatanodeInfo
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|GetDatanodeInfoRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|GetDatanodeInfoResponseProto
name|res
decl_stmt|;
try|try
block|{
name|res
operator|=
name|GetDatanodeInfoResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setLocalInfo
argument_list|(
name|PBHelperClient
operator|.
name|convert
argument_list|(
name|impl
operator|.
name|getDatanodeInfo
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
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
name|res
return|;
block|}
annotation|@
name|Override
DECL|method|startReconfiguration ( RpcController unused, StartReconfigurationRequestProto request)
specifier|public
name|StartReconfigurationResponseProto
name|startReconfiguration
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|StartReconfigurationRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|impl
operator|.
name|startReconfiguration
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
name|START_RECONFIG_RESP
return|;
block|}
annotation|@
name|Override
DECL|method|listReconfigurableProperties ( RpcController controller, ListReconfigurablePropertiesRequestProto request)
specifier|public
name|ListReconfigurablePropertiesResponseProto
name|listReconfigurableProperties
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|ListReconfigurablePropertiesRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|ReconfigurationProtocolServerSideUtils
operator|.
name|listReconfigurableProperties
argument_list|(
name|impl
operator|.
name|listReconfigurableProperties
argument_list|()
argument_list|)
return|;
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
block|}
annotation|@
name|Override
DECL|method|getReconfigurationStatus ( RpcController unused, GetReconfigurationStatusRequestProto request)
specifier|public
name|GetReconfigurationStatusResponseProto
name|getReconfigurationStatus
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|GetReconfigurationStatusRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|ReconfigurationProtocolServerSideUtils
operator|.
name|getReconfigurationStatus
argument_list|(
name|impl
operator|.
name|getReconfigurationStatus
argument_list|()
argument_list|)
return|;
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
block|}
annotation|@
name|Override
DECL|method|triggerBlockReport ( RpcController unused, TriggerBlockReportRequestProto request)
specifier|public
name|TriggerBlockReportResponseProto
name|triggerBlockReport
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|TriggerBlockReportRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|impl
operator|.
name|triggerBlockReport
argument_list|(
operator|new
name|BlockReportOptions
operator|.
name|Factory
argument_list|()
operator|.
name|setIncremental
argument_list|(
name|request
operator|.
name|getIncremental
argument_list|()
argument_list|)
operator|.
name|build
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
name|TRIGGER_BLOCK_REPORT_RESP
return|;
block|}
annotation|@
name|Override
DECL|method|getBalancerBandwidth ( RpcController controller, GetBalancerBandwidthRequestProto request)
specifier|public
name|GetBalancerBandwidthResponseProto
name|getBalancerBandwidth
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|GetBalancerBandwidthRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|long
name|bandwidth
decl_stmt|;
try|try
block|{
name|bandwidth
operator|=
name|impl
operator|.
name|getBalancerBandwidth
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
name|GetBalancerBandwidthResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBandwidth
argument_list|(
name|bandwidth
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Submit a disk balancer plan for execution.    * @param controller  - RpcController    * @param request   - Request    * @return   Response    * @throws ServiceException    */
annotation|@
name|Override
DECL|method|submitDiskBalancerPlan ( RpcController controller, SubmitDiskBalancerPlanRequestProto request)
specifier|public
name|SubmitDiskBalancerPlanResponseProto
name|submitDiskBalancerPlan
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|SubmitDiskBalancerPlanRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|impl
operator|.
name|submitDiskBalancerPlan
argument_list|(
name|request
operator|.
name|getPlanID
argument_list|()
argument_list|,
name|request
operator|.
name|hasPlanVersion
argument_list|()
condition|?
name|request
operator|.
name|getPlanVersion
argument_list|()
else|:
literal|0
argument_list|,
name|request
operator|.
name|hasMaxDiskBandwidth
argument_list|()
condition|?
name|request
operator|.
name|getMaxDiskBandwidth
argument_list|()
else|:
literal|0
argument_list|,
name|request
operator|.
name|getPlan
argument_list|()
argument_list|)
expr_stmt|;
name|SubmitDiskBalancerPlanResponseProto
name|response
init|=
name|SubmitDiskBalancerPlanResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|response
return|;
block|}
catch|catch
parameter_list|(
name|Exception
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
block|}
comment|/**    * Cancel an executing plan.    * @param controller - RpcController    * @param request  - Request    * @return Response.    * @throws ServiceException    */
annotation|@
name|Override
DECL|method|cancelDiskBalancerPlan ( RpcController controller, CancelPlanRequestProto request)
specifier|public
name|CancelPlanResponseProto
name|cancelDiskBalancerPlan
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|CancelPlanRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|impl
operator|.
name|cancelDiskBalancePlan
argument_list|(
name|request
operator|.
name|getPlanID
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|CancelPlanResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
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
block|}
comment|/**    * Gets the status of an executing Plan.    */
annotation|@
name|Override
DECL|method|queryDiskBalancerPlan ( RpcController controller, QueryPlanStatusRequestProto request)
specifier|public
name|QueryPlanStatusResponseProto
name|queryDiskBalancerPlan
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|QueryPlanStatusRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|DiskBalancerWorkStatus
name|result
init|=
name|impl
operator|.
name|queryDiskBalancerPlan
argument_list|()
decl_stmt|;
return|return
name|QueryPlanStatusResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setResult
argument_list|(
name|result
operator|.
name|getResult
argument_list|()
operator|.
name|getIntResult
argument_list|()
argument_list|)
operator|.
name|setPlanID
argument_list|(
name|result
operator|.
name|getPlanID
argument_list|()
argument_list|)
operator|.
name|setCurrentStatus
argument_list|(
name|result
operator|.
name|getCurrentStateString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
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
block|}
comment|/**    * Returns a run-time setting from diskbalancer like Bandwidth.    */
annotation|@
name|Override
DECL|method|getDiskBalancerSetting ( RpcController controller, DiskBalancerSettingRequestProto request)
specifier|public
name|DiskBalancerSettingResponseProto
name|getDiskBalancerSetting
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|DiskBalancerSettingRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|String
name|val
init|=
name|impl
operator|.
name|getDiskBalancerSetting
argument_list|(
name|request
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|DiskBalancerSettingResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setValue
argument_list|(
name|val
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
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
block|}
block|}
end_class

end_unit

