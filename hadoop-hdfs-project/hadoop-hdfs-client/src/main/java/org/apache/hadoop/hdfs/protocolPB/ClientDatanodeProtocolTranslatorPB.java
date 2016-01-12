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
name|fs
operator|.
name|CommonConfigurationKeysPublic
import|;
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
name|DatanodeLocalInfo
import|;
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenIdentifier
import|;
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|token
operator|.
name|Token
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

begin_comment
comment|/**  * This class is the client side translator to translate the requests made on  * {@link ClientDatanodeProtocol} interfaces to the RPC server implementing  * {@link ClientDatanodeProtocolPB}.  */
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
DECL|class|ClientDatanodeProtocolTranslatorPB
specifier|public
class|class
name|ClientDatanodeProtocolTranslatorPB
implements|implements
name|ProtocolMetaInterface
implements|,
name|ClientDatanodeProtocol
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
name|ClientDatanodeProtocolTranslatorPB
operator|.
name|class
argument_list|)
decl_stmt|;
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
specifier|private
specifier|final
name|ClientDatanodeProtocolPB
name|rpcProxy
decl_stmt|;
DECL|field|VOID_REFRESH_NAMENODES
specifier|private
specifier|final
specifier|static
name|RefreshNamenodesRequestProto
name|VOID_REFRESH_NAMENODES
init|=
name|RefreshNamenodesRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|VOID_GET_DATANODE_INFO
specifier|private
specifier|final
specifier|static
name|GetDatanodeInfoRequestProto
name|VOID_GET_DATANODE_INFO
init|=
name|GetDatanodeInfoRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|VOID_GET_RECONFIG_STATUS
specifier|private
specifier|final
specifier|static
name|GetReconfigurationStatusRequestProto
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
DECL|field|VOID_START_RECONFIG
specifier|private
specifier|final
specifier|static
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
name|GetBalancerBandwidthRequestProto
DECL|field|VOID_GET_BALANCER_BANDWIDTH
name|VOID_GET_BALANCER_BANDWIDTH
init|=
name|GetBalancerBandwidthRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|VOID_EVICT_WRITERS
specifier|private
specifier|final
specifier|static
name|EvictWritersRequestProto
name|VOID_EVICT_WRITERS
init|=
name|EvictWritersRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|method|ClientDatanodeProtocolTranslatorPB (DatanodeID datanodeid, Configuration conf, int socketTimeout, boolean connectToDnViaHostname, LocatedBlock locatedBlock)
specifier|public
name|ClientDatanodeProtocolTranslatorPB
parameter_list|(
name|DatanodeID
name|datanodeid
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|int
name|socketTimeout
parameter_list|,
name|boolean
name|connectToDnViaHostname
parameter_list|,
name|LocatedBlock
name|locatedBlock
parameter_list|)
throws|throws
name|IOException
block|{
name|rpcProxy
operator|=
name|createClientDatanodeProtocolProxy
argument_list|(
name|datanodeid
argument_list|,
name|conf
argument_list|,
name|socketTimeout
argument_list|,
name|connectToDnViaHostname
argument_list|,
name|locatedBlock
argument_list|)
expr_stmt|;
block|}
DECL|method|ClientDatanodeProtocolTranslatorPB (InetSocketAddress addr, UserGroupInformation ticket, Configuration conf, SocketFactory factory)
specifier|public
name|ClientDatanodeProtocolTranslatorPB
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
name|createClientDatanodeProtocolProxy
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
comment|/**    * Constructor.    * @param datanodeid Datanode to connect to.    * @param conf Configuration.    * @param socketTimeout Socket timeout to use.    * @param connectToDnViaHostname connect to the Datanode using its hostname    * @throws IOException    */
DECL|method|ClientDatanodeProtocolTranslatorPB (DatanodeID datanodeid, Configuration conf, int socketTimeout, boolean connectToDnViaHostname)
specifier|public
name|ClientDatanodeProtocolTranslatorPB
parameter_list|(
name|DatanodeID
name|datanodeid
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|int
name|socketTimeout
parameter_list|,
name|boolean
name|connectToDnViaHostname
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|dnAddr
init|=
name|datanodeid
operator|.
name|getIpcAddr
argument_list|(
name|connectToDnViaHostname
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|dnAddr
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connecting to datanode {} addr={}"
argument_list|,
name|dnAddr
argument_list|,
name|addr
argument_list|)
expr_stmt|;
name|rpcProxy
operator|=
name|createClientDatanodeProtocolProxy
argument_list|(
name|addr
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
name|conf
argument_list|,
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|conf
argument_list|)
argument_list|,
name|socketTimeout
argument_list|)
expr_stmt|;
block|}
DECL|method|createClientDatanodeProtocolProxy ( DatanodeID datanodeid, Configuration conf, int socketTimeout, boolean connectToDnViaHostname, LocatedBlock locatedBlock)
specifier|static
name|ClientDatanodeProtocolPB
name|createClientDatanodeProtocolProxy
parameter_list|(
name|DatanodeID
name|datanodeid
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|int
name|socketTimeout
parameter_list|,
name|boolean
name|connectToDnViaHostname
parameter_list|,
name|LocatedBlock
name|locatedBlock
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|dnAddr
init|=
name|datanodeid
operator|.
name|getIpcAddr
argument_list|(
name|connectToDnViaHostname
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|dnAddr
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connecting to datanode {} addr={}"
argument_list|,
name|dnAddr
argument_list|,
name|addr
argument_list|)
expr_stmt|;
comment|// Since we're creating a new UserGroupInformation here, we know that no
comment|// future RPC proxies will be able to re-use the same connection. And
comment|// usages of this proxy tend to be one-off calls.
comment|//
comment|// This is a temporary fix: callers should really achieve this by using
comment|// RPC.stopProxy() on the resulting object, but this is currently not
comment|// working in trunk. See the discussion on HDFS-1965.
name|Configuration
name|confWithNoIpcIdle
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|confWithNoIpcIdle
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|ticket
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|locatedBlock
operator|.
name|getBlock
argument_list|()
operator|.
name|getLocalBlock
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|ticket
operator|.
name|addToken
argument_list|(
name|locatedBlock
operator|.
name|getBlockToken
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|createClientDatanodeProtocolProxy
argument_list|(
name|addr
argument_list|,
name|ticket
argument_list|,
name|confWithNoIpcIdle
argument_list|,
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|conf
argument_list|)
argument_list|,
name|socketTimeout
argument_list|)
return|;
block|}
DECL|method|createClientDatanodeProtocolProxy ( InetSocketAddress addr, UserGroupInformation ticket, Configuration conf, SocketFactory factory, int socketTimeout)
specifier|static
name|ClientDatanodeProtocolPB
name|createClientDatanodeProtocolProxy
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
name|ClientDatanodeProtocolPB
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
name|ClientDatanodeProtocolPB
operator|.
name|class
argument_list|,
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|ClientDatanodeProtocolPB
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
DECL|method|getReplicaVisibleLength (ExtendedBlock b)
specifier|public
name|long
name|getReplicaVisibleLength
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|GetReplicaVisibleLengthRequestProto
name|req
init|=
name|GetReplicaVisibleLengthRequestProto
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
name|b
argument_list|)
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
name|getReplicaVisibleLength
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|req
argument_list|)
operator|.
name|getLength
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
DECL|method|refreshNamenodes ()
specifier|public
name|void
name|refreshNamenodes
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|rpcProxy
operator|.
name|refreshNamenodes
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|VOID_REFRESH_NAMENODES
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
DECL|method|deleteBlockPool (String bpid, boolean force)
specifier|public
name|void
name|deleteBlockPool
parameter_list|(
name|String
name|bpid
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
block|{
name|DeleteBlockPoolRequestProto
name|req
init|=
name|DeleteBlockPoolRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBlockPool
argument_list|(
name|bpid
argument_list|)
operator|.
name|setForce
argument_list|(
name|force
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|rpcProxy
operator|.
name|deleteBlockPool
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
DECL|method|getBlockLocalPathInfo (ExtendedBlock block, Token<BlockTokenIdentifier> token)
specifier|public
name|BlockLocalPathInfo
name|getBlockLocalPathInfo
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|,
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
name|GetBlockLocalPathInfoRequestProto
name|req
init|=
name|GetBlockLocalPathInfoRequestProto
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
name|block
argument_list|)
argument_list|)
operator|.
name|setToken
argument_list|(
name|PBHelperClient
operator|.
name|convert
argument_list|(
name|token
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|GetBlockLocalPathInfoResponseProto
name|resp
decl_stmt|;
try|try
block|{
name|resp
operator|=
name|rpcProxy
operator|.
name|getBlockLocalPathInfo
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
return|return
operator|new
name|BlockLocalPathInfo
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
argument_list|,
name|resp
operator|.
name|getLocalPath
argument_list|()
argument_list|,
name|resp
operator|.
name|getLocalMetaPath
argument_list|()
argument_list|)
return|;
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
name|ClientDatanodeProtocolPB
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
name|ClientDatanodeProtocolPB
operator|.
name|class
argument_list|)
argument_list|,
name|methodName
argument_list|)
return|;
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
DECL|method|shutdownDatanode (boolean forUpgrade)
specifier|public
name|void
name|shutdownDatanode
parameter_list|(
name|boolean
name|forUpgrade
parameter_list|)
throws|throws
name|IOException
block|{
name|ShutdownDatanodeRequestProto
name|request
init|=
name|ShutdownDatanodeRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setForUpgrade
argument_list|(
name|forUpgrade
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|rpcProxy
operator|.
name|shutdownDatanode
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
DECL|method|evictWriters ()
specifier|public
name|void
name|evictWriters
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|rpcProxy
operator|.
name|evictWriters
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|VOID_EVICT_WRITERS
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
DECL|method|getDatanodeInfo ()
specifier|public
name|DatanodeLocalInfo
name|getDatanodeInfo
parameter_list|()
throws|throws
name|IOException
block|{
name|GetDatanodeInfoResponseProto
name|response
decl_stmt|;
try|try
block|{
name|response
operator|=
name|rpcProxy
operator|.
name|getDatanodeInfo
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|VOID_GET_DATANODE_INFO
argument_list|)
expr_stmt|;
return|return
name|PBHelperClient
operator|.
name|convert
argument_list|(
name|response
operator|.
name|getLocalInfo
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
DECL|method|triggerBlockReport (BlockReportOptions options)
specifier|public
name|void
name|triggerBlockReport
parameter_list|(
name|BlockReportOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|rpcProxy
operator|.
name|triggerBlockReport
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|TriggerBlockReportRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setIncremental
argument_list|(
name|options
operator|.
name|isIncremental
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
DECL|method|getBalancerBandwidth ()
specifier|public
name|long
name|getBalancerBandwidth
parameter_list|()
throws|throws
name|IOException
block|{
name|GetBalancerBandwidthResponseProto
name|response
decl_stmt|;
try|try
block|{
name|response
operator|=
name|rpcProxy
operator|.
name|getBalancerBandwidth
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|VOID_GET_BALANCER_BANDWIDTH
argument_list|)
expr_stmt|;
return|return
name|response
operator|.
name|getBandwidth
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
comment|/**    * Submits a disk balancer plan to the datanode.    * @param planID - Plan ID is the hash512 string of the plan that is    *               submitted. This is used by clients when they want to find    *               local copies of these plans.    * @param planVersion - The data format of the plans - for future , not    *                    used now.    * @param bandwidth - Maximum disk bandwidth to consume, setting this value    *                  to zero allows datanode to use the value defined in    *                  configration.    * @param plan - Actual plan.    * @return Success or throws Exception.    * @throws Exception    */
annotation|@
name|Override
DECL|method|submitDiskBalancerPlan (String planID, long planVersion, long bandwidth, String plan)
specifier|public
name|void
name|submitDiskBalancerPlan
parameter_list|(
name|String
name|planID
parameter_list|,
name|long
name|planVersion
parameter_list|,
name|long
name|bandwidth
parameter_list|,
name|String
name|plan
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|SubmitDiskBalancerPlanRequestProto
name|request
init|=
name|SubmitDiskBalancerPlanRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setPlanID
argument_list|(
name|planID
argument_list|)
operator|.
name|setPlanVersion
argument_list|(
name|planVersion
argument_list|)
operator|.
name|setMaxDiskBandwidth
argument_list|(
name|bandwidth
argument_list|)
operator|.
name|setPlan
argument_list|(
name|plan
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|rpcProxy
operator|.
name|submitDiskBalancerPlan
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
block|}
end_class

end_unit

