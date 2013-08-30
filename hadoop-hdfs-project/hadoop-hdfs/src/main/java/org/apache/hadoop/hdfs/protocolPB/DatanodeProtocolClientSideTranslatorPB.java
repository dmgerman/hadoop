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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|AlreadyBeingCreatedException
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
name|HdfsConstants
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
name|DatanodeCommandProto
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
name|server
operator|.
name|namenode
operator|.
name|NameNode
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
name|CacheReport
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
name|io
operator|.
name|retry
operator|.
name|RetryPolicies
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
name|io
operator|.
name|retry
operator|.
name|RetryPolicy
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
name|io
operator|.
name|retry
operator|.
name|RetryProxy
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
name|RemoteException
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
comment|/**  * This class is the client side translator to translate the requests made on  * {@link DatanodeProtocol} interfaces to the RPC server implementing  * {@link DatanodeProtocolPB}.  */
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
DECL|class|DatanodeProtocolClientSideTranslatorPB
specifier|public
class|class
name|DatanodeProtocolClientSideTranslatorPB
implements|implements
name|ProtocolMetaInterface
implements|,
name|DatanodeProtocol
implements|,
name|Closeable
block|{
comment|/** RpcController is not used and hence is set to null */
DECL|field|rpcProxy
specifier|private
specifier|final
name|DatanodeProtocolPB
name|rpcProxy
decl_stmt|;
DECL|field|VOID_VERSION_REQUEST
specifier|private
specifier|static
specifier|final
name|VersionRequestProto
name|VOID_VERSION_REQUEST
init|=
name|VersionRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|NULL_CONTROLLER
specifier|private
specifier|final
specifier|static
name|RpcController
name|NULL_CONTROLLER
init|=
literal|null
decl_stmt|;
DECL|method|DatanodeProtocolClientSideTranslatorPB (InetSocketAddress nameNodeAddr, Configuration conf)
specifier|public
name|DatanodeProtocolClientSideTranslatorPB
parameter_list|(
name|InetSocketAddress
name|nameNodeAddr
parameter_list|,
name|Configuration
name|conf
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
name|DatanodeProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|rpcProxy
operator|=
name|createNamenodeWithRetry
argument_list|(
name|createNamenode
argument_list|(
name|nameNodeAddr
argument_list|,
name|conf
argument_list|,
name|ugi
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createNamenode ( InetSocketAddress nameNodeAddr, Configuration conf, UserGroupInformation ugi)
specifier|private
specifier|static
name|DatanodeProtocolPB
name|createNamenode
parameter_list|(
name|InetSocketAddress
name|nameNodeAddr
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|RPC
operator|.
name|getProxy
argument_list|(
name|DatanodeProtocolPB
operator|.
name|class
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
name|nameNodeAddr
argument_list|,
name|ugi
argument_list|,
name|conf
argument_list|,
name|NetUtils
operator|.
name|getSocketFactory
argument_list|(
name|conf
argument_list|,
name|DatanodeProtocolPB
operator|.
name|class
argument_list|)
argument_list|)
return|;
block|}
comment|/** Create a {@link NameNode} proxy */
DECL|method|createNamenodeWithRetry ( DatanodeProtocolPB rpcNamenode)
specifier|static
name|DatanodeProtocolPB
name|createNamenodeWithRetry
parameter_list|(
name|DatanodeProtocolPB
name|rpcNamenode
parameter_list|)
block|{
name|RetryPolicy
name|createPolicy
init|=
name|RetryPolicies
operator|.
name|retryUpToMaximumCountWithFixedSleep
argument_list|(
literal|5
argument_list|,
name|HdfsConstants
operator|.
name|LEASE_SOFTLIMIT_PERIOD
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
argument_list|,
name|RetryPolicy
argument_list|>
name|remoteExceptionToPolicyMap
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
argument_list|,
name|RetryPolicy
argument_list|>
argument_list|()
decl_stmt|;
name|remoteExceptionToPolicyMap
operator|.
name|put
argument_list|(
name|AlreadyBeingCreatedException
operator|.
name|class
argument_list|,
name|createPolicy
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
argument_list|,
name|RetryPolicy
argument_list|>
name|exceptionToPolicyMap
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
argument_list|,
name|RetryPolicy
argument_list|>
argument_list|()
decl_stmt|;
name|exceptionToPolicyMap
operator|.
name|put
argument_list|(
name|RemoteException
operator|.
name|class
argument_list|,
name|RetryPolicies
operator|.
name|retryByRemoteException
argument_list|(
name|RetryPolicies
operator|.
name|TRY_ONCE_THEN_FAIL
argument_list|,
name|remoteExceptionToPolicyMap
argument_list|)
argument_list|)
expr_stmt|;
name|RetryPolicy
name|methodPolicy
init|=
name|RetryPolicies
operator|.
name|retryByException
argument_list|(
name|RetryPolicies
operator|.
name|TRY_ONCE_THEN_FAIL
argument_list|,
name|exceptionToPolicyMap
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|RetryPolicy
argument_list|>
name|methodNameToPolicyMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|RetryPolicy
argument_list|>
argument_list|()
decl_stmt|;
name|methodNameToPolicyMap
operator|.
name|put
argument_list|(
literal|"create"
argument_list|,
name|methodPolicy
argument_list|)
expr_stmt|;
return|return
operator|(
name|DatanodeProtocolPB
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|DatanodeProtocolPB
operator|.
name|class
argument_list|,
name|rpcNamenode
argument_list|,
name|methodNameToPolicyMap
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
DECL|method|registerDatanode (DatanodeRegistration registration )
specifier|public
name|DatanodeRegistration
name|registerDatanode
parameter_list|(
name|DatanodeRegistration
name|registration
parameter_list|)
throws|throws
name|IOException
block|{
name|RegisterDatanodeRequestProto
operator|.
name|Builder
name|builder
init|=
name|RegisterDatanodeRequestProto
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
decl_stmt|;
name|RegisterDatanodeResponseProto
name|resp
decl_stmt|;
try|try
block|{
name|resp
operator|=
name|rpcProxy
operator|.
name|registerDatanode
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|se
parameter_list|)
block|{
throw|throw
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|se
argument_list|)
throw|;
block|}
return|return
name|PBHelper
operator|.
name|convert
argument_list|(
name|resp
operator|.
name|getRegistration
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|sendHeartbeat (DatanodeRegistration registration, StorageReport[] reports, CacheReport[] cacheReports, int xmitsInProgress, int xceiverCount, int failedVolumes)
specifier|public
name|HeartbeatResponse
name|sendHeartbeat
parameter_list|(
name|DatanodeRegistration
name|registration
parameter_list|,
name|StorageReport
index|[]
name|reports
parameter_list|,
name|CacheReport
index|[]
name|cacheReports
parameter_list|,
name|int
name|xmitsInProgress
parameter_list|,
name|int
name|xceiverCount
parameter_list|,
name|int
name|failedVolumes
parameter_list|)
throws|throws
name|IOException
block|{
name|HeartbeatRequestProto
operator|.
name|Builder
name|builder
init|=
name|HeartbeatRequestProto
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
name|setXmitsInProgress
argument_list|(
name|xmitsInProgress
argument_list|)
operator|.
name|setXceiverCount
argument_list|(
name|xceiverCount
argument_list|)
operator|.
name|setFailedVolumes
argument_list|(
name|failedVolumes
argument_list|)
decl_stmt|;
for|for
control|(
name|StorageReport
name|r
range|:
name|reports
control|)
block|{
name|builder
operator|.
name|addReports
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|CacheReport
name|r
range|:
name|cacheReports
control|)
block|{
name|builder
operator|.
name|addCacheReports
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|HeartbeatResponseProto
name|resp
decl_stmt|;
try|try
block|{
name|resp
operator|=
name|rpcProxy
operator|.
name|sendHeartbeat
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|se
parameter_list|)
block|{
throw|throw
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|se
argument_list|)
throw|;
block|}
name|DatanodeCommand
index|[]
name|cmds
init|=
operator|new
name|DatanodeCommand
index|[
name|resp
operator|.
name|getCmdsList
argument_list|()
operator|.
name|size
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
name|DatanodeCommandProto
name|p
range|:
name|resp
operator|.
name|getCmdsList
argument_list|()
control|)
block|{
name|cmds
index|[
name|index
index|]
operator|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
return|return
operator|new
name|HeartbeatResponse
argument_list|(
name|cmds
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|resp
operator|.
name|getHaStatus
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|blockReport (DatanodeRegistration registration, String poolId, StorageBlockReport[] reports)
specifier|public
name|DatanodeCommand
name|blockReport
parameter_list|(
name|DatanodeRegistration
name|registration
parameter_list|,
name|String
name|poolId
parameter_list|,
name|StorageBlockReport
index|[]
name|reports
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockReportRequestProto
operator|.
name|Builder
name|builder
init|=
name|BlockReportRequestProto
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
name|setBlockPoolId
argument_list|(
name|poolId
argument_list|)
decl_stmt|;
for|for
control|(
name|StorageBlockReport
name|r
range|:
name|reports
control|)
block|{
name|StorageBlockReportProto
operator|.
name|Builder
name|reportBuilder
init|=
name|StorageBlockReportProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setStorage
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|r
operator|.
name|getStorage
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|long
index|[]
name|blocks
init|=
name|r
operator|.
name|getBlocks
argument_list|()
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
name|blocks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|reportBuilder
operator|.
name|addBlocks
argument_list|(
name|blocks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|addReports
argument_list|(
name|reportBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|BlockReportResponseProto
name|resp
decl_stmt|;
try|try
block|{
name|resp
operator|=
name|rpcProxy
operator|.
name|blockReport
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|se
parameter_list|)
block|{
throw|throw
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|se
argument_list|)
throw|;
block|}
return|return
name|resp
operator|.
name|hasCmd
argument_list|()
condition|?
name|PBHelper
operator|.
name|convert
argument_list|(
name|resp
operator|.
name|getCmd
argument_list|()
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|cacheReport (DatanodeRegistration registration, String poolId, long[] blocks)
specifier|public
name|DatanodeCommand
name|cacheReport
parameter_list|(
name|DatanodeRegistration
name|registration
parameter_list|,
name|String
name|poolId
parameter_list|,
name|long
index|[]
name|blocks
parameter_list|)
throws|throws
name|IOException
block|{
name|CacheReportRequestProto
operator|.
name|Builder
name|builder
init|=
name|CacheReportRequestProto
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
name|setBlockPoolId
argument_list|(
name|poolId
argument_list|)
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
name|blocks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|addBlocks
argument_list|(
name|blocks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|CacheReportResponseProto
name|resp
decl_stmt|;
try|try
block|{
name|resp
operator|=
name|rpcProxy
operator|.
name|cacheReport
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|se
parameter_list|)
block|{
throw|throw
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|se
argument_list|)
throw|;
block|}
if|if
condition|(
name|resp
operator|.
name|hasCmd
argument_list|()
condition|)
block|{
return|return
name|PBHelper
operator|.
name|convert
argument_list|(
name|resp
operator|.
name|getCmd
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|blockReceivedAndDeleted (DatanodeRegistration registration, String poolId, StorageReceivedDeletedBlocks[] receivedAndDeletedBlocks)
specifier|public
name|void
name|blockReceivedAndDeleted
parameter_list|(
name|DatanodeRegistration
name|registration
parameter_list|,
name|String
name|poolId
parameter_list|,
name|StorageReceivedDeletedBlocks
index|[]
name|receivedAndDeletedBlocks
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockReceivedAndDeletedRequestProto
operator|.
name|Builder
name|builder
init|=
name|BlockReceivedAndDeletedRequestProto
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
name|setBlockPoolId
argument_list|(
name|poolId
argument_list|)
decl_stmt|;
for|for
control|(
name|StorageReceivedDeletedBlocks
name|storageBlock
range|:
name|receivedAndDeletedBlocks
control|)
block|{
name|StorageReceivedDeletedBlocksProto
operator|.
name|Builder
name|repBuilder
init|=
name|StorageReceivedDeletedBlocksProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|repBuilder
operator|.
name|setStorageID
argument_list|(
name|storageBlock
operator|.
name|getStorageID
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ReceivedDeletedBlockInfo
name|rdBlock
range|:
name|storageBlock
operator|.
name|getBlocks
argument_list|()
control|)
block|{
name|repBuilder
operator|.
name|addBlocks
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|rdBlock
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|addBlocks
argument_list|(
name|repBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|rpcProxy
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|se
parameter_list|)
block|{
throw|throw
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|se
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|errorReport (DatanodeRegistration registration, int errorCode, String msg)
specifier|public
name|void
name|errorReport
parameter_list|(
name|DatanodeRegistration
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
name|setRegistartion
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|registration
argument_list|)
argument_list|)
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
name|se
parameter_list|)
block|{
throw|throw
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|se
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
name|VOID_VERSION_REQUEST
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
DECL|method|reportBadBlocks (LocatedBlock[] blocks)
specifier|public
name|void
name|reportBadBlocks
parameter_list|(
name|LocatedBlock
index|[]
name|blocks
parameter_list|)
throws|throws
name|IOException
block|{
name|ReportBadBlocksRequestProto
operator|.
name|Builder
name|builder
init|=
name|ReportBadBlocksRequestProto
operator|.
name|newBuilder
argument_list|()
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
name|blocks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|addBlocks
argument_list|(
name|i
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|blocks
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ReportBadBlocksRequestProto
name|req
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|rpcProxy
operator|.
name|reportBadBlocks
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
name|se
parameter_list|)
block|{
throw|throw
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|se
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|commitBlockSynchronization (ExtendedBlock block, long newgenerationstamp, long newlength, boolean closeFile, boolean deleteblock, DatanodeID[] newtargets, String[] newtargetstorages )
specifier|public
name|void
name|commitBlockSynchronization
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|,
name|long
name|newgenerationstamp
parameter_list|,
name|long
name|newlength
parameter_list|,
name|boolean
name|closeFile
parameter_list|,
name|boolean
name|deleteblock
parameter_list|,
name|DatanodeID
index|[]
name|newtargets
parameter_list|,
name|String
index|[]
name|newtargetstorages
parameter_list|)
throws|throws
name|IOException
block|{
name|CommitBlockSynchronizationRequestProto
operator|.
name|Builder
name|builder
init|=
name|CommitBlockSynchronizationRequestProto
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
name|block
argument_list|)
argument_list|)
operator|.
name|setNewGenStamp
argument_list|(
name|newgenerationstamp
argument_list|)
operator|.
name|setNewLength
argument_list|(
name|newlength
argument_list|)
operator|.
name|setCloseFile
argument_list|(
name|closeFile
argument_list|)
operator|.
name|setDeleteBlock
argument_list|(
name|deleteblock
argument_list|)
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
name|newtargets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|addNewTaragets
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|newtargets
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addNewTargetStorages
argument_list|(
name|newtargetstorages
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|CommitBlockSynchronizationRequestProto
name|req
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|rpcProxy
operator|.
name|commitBlockSynchronization
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
name|se
parameter_list|)
block|{
throw|throw
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|se
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
comment|// ProtocolMetaInterface
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
name|DatanodeProtocolPB
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
name|DatanodeProtocolPB
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

