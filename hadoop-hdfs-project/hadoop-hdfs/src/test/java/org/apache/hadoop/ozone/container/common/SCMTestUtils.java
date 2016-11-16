begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
package|;
end_package

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
name|DFSUtil
import|;
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|EndpointStateMachine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|StorageContainerDatanodeProtocol
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|RegisteredCommand
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|StorageContainerDatanodeProtocolService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocolPB
operator|.
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocolPB
operator|.
name|StorageContainerDatanodeProtocolPB
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocolPB
operator|.
name|StorageContainerDatanodeProtocolServerSideTranslatorPB
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|node
operator|.
name|SCMNodeManager
import|;
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
name|InetAddress
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
name|net
operator|.
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  * Test Endpoint class.  */
end_comment

begin_class
DECL|class|SCMTestUtils
specifier|public
specifier|final
class|class
name|SCMTestUtils
block|{
comment|/**    * Never constructed.    */
DECL|method|SCMTestUtils ()
specifier|private
name|SCMTestUtils
parameter_list|()
block|{   }
comment|/**    * Starts an RPC server, if configured.    *    * @param conf configuration    * @param addr configured address of RPC server    * @param protocol RPC protocol provided by RPC server    * @param instance RPC protocol implementation instance    * @param handlerCount RPC server handler count    * @return RPC server    * @throws IOException if there is an I/O error while creating RPC server    */
DECL|method|startRpcServer (Configuration conf, InetSocketAddress addr, Class<?> protocol, BlockingService instance, int handlerCount)
specifier|private
specifier|static
name|RPC
operator|.
name|Server
name|startRpcServer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|BlockingService
name|instance
parameter_list|,
name|int
name|handlerCount
parameter_list|)
throws|throws
name|IOException
block|{
name|RPC
operator|.
name|Server
name|rpcServer
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
name|protocol
argument_list|)
operator|.
name|setInstance
argument_list|(
name|instance
argument_list|)
operator|.
name|setBindAddress
argument_list|(
name|addr
operator|.
name|getHostString
argument_list|()
argument_list|)
operator|.
name|setPort
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|)
operator|.
name|setNumHandlers
argument_list|(
name|handlerCount
argument_list|)
operator|.
name|setVerbose
argument_list|(
literal|false
argument_list|)
operator|.
name|setSecretManager
argument_list|(
literal|null
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|DFSUtil
operator|.
name|addPBProtocol
argument_list|(
name|conf
argument_list|,
name|protocol
argument_list|,
name|instance
argument_list|,
name|rpcServer
argument_list|)
expr_stmt|;
return|return
name|rpcServer
return|;
block|}
comment|/**    * Creates an Endpoint class for testing purpose.    *    * @param conf - Conf    * @param address - InetAddres    * @param rpcTimeout - rpcTimeOut    * @return EndPoint    * @throws Exception    */
DECL|method|createEndpoint (Configuration conf, InetSocketAddress address, int rpcTimeout)
specifier|public
specifier|static
name|EndpointStateMachine
name|createEndpoint
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|InetSocketAddress
name|address
parameter_list|,
name|int
name|rpcTimeout
parameter_list|)
throws|throws
name|Exception
block|{
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|StorageContainerDatanodeProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|long
name|version
init|=
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|StorageContainerDatanodeProtocolPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|StorageContainerDatanodeProtocolPB
name|rpcProxy
init|=
name|RPC
operator|.
name|getProtocolProxy
argument_list|(
name|StorageContainerDatanodeProtocolPB
operator|.
name|class
argument_list|,
name|version
argument_list|,
name|address
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
name|rpcTimeout
argument_list|,
name|RetryPolicies
operator|.
name|TRY_ONCE_THEN_FAIL
argument_list|)
operator|.
name|getProxy
argument_list|()
decl_stmt|;
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
name|rpcClient
init|=
operator|new
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
argument_list|(
name|rpcProxy
argument_list|)
decl_stmt|;
return|return
operator|new
name|EndpointStateMachine
argument_list|(
name|address
argument_list|,
name|rpcClient
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/**    * Start Datanode RPC server.    */
DECL|method|startScmRpcServer (Configuration configuration, StorageContainerDatanodeProtocol server, InetSocketAddress rpcServerAddresss, int handlerCount)
specifier|public
specifier|static
name|RPC
operator|.
name|Server
name|startScmRpcServer
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|StorageContainerDatanodeProtocol
name|server
parameter_list|,
name|InetSocketAddress
name|rpcServerAddresss
parameter_list|,
name|int
name|handlerCount
parameter_list|)
throws|throws
name|IOException
block|{
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|configuration
argument_list|,
name|StorageContainerDatanodeProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|BlockingService
name|scmDatanodeService
init|=
name|StorageContainerDatanodeProtocolService
operator|.
name|newReflectiveBlockingService
argument_list|(
operator|new
name|StorageContainerDatanodeProtocolServerSideTranslatorPB
argument_list|(
name|server
argument_list|)
argument_list|)
decl_stmt|;
name|RPC
operator|.
name|Server
name|scmServer
init|=
name|startRpcServer
argument_list|(
name|configuration
argument_list|,
name|rpcServerAddresss
argument_list|,
name|StorageContainerDatanodeProtocolPB
operator|.
name|class
argument_list|,
name|scmDatanodeService
argument_list|,
name|handlerCount
argument_list|)
decl_stmt|;
name|scmServer
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|scmServer
return|;
block|}
DECL|method|getReuseableAddress ()
specifier|public
specifier|static
name|InetSocketAddress
name|getReuseableAddress
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|ServerSocket
name|socket
init|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|)
init|)
block|{
name|socket
operator|.
name|setReuseAddress
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|int
name|port
init|=
name|socket
operator|.
name|getLocalPort
argument_list|()
decl_stmt|;
name|String
name|addr
init|=
name|InetAddress
operator|.
name|getLoopbackAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
operator|new
name|InetSocketAddress
argument_list|(
name|addr
argument_list|,
name|port
argument_list|)
return|;
block|}
block|}
DECL|method|getConf ()
specifier|public
specifier|static
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
operator|new
name|Configuration
argument_list|()
return|;
block|}
DECL|method|getDatanodeID (SCMNodeManager nodeManager)
specifier|public
specifier|static
name|DatanodeID
name|getDatanodeID
parameter_list|(
name|SCMNodeManager
name|nodeManager
parameter_list|)
block|{
return|return
name|getDatanodeID
argument_list|(
name|nodeManager
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Create a new DatanodeID with NodeID set to the string.    *    * @param uuid - node ID, it is generally UUID.    * @return DatanodeID.    */
DECL|method|getDatanodeID (SCMNodeManager nodeManager, String uuid)
specifier|public
specifier|static
name|DatanodeID
name|getDatanodeID
parameter_list|(
name|SCMNodeManager
name|nodeManager
parameter_list|,
name|String
name|uuid
parameter_list|)
block|{
name|DatanodeID
name|tempDataNode
init|=
name|getDatanodeID
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
name|RegisteredCommand
name|command
init|=
operator|(
name|RegisteredCommand
operator|)
name|nodeManager
operator|.
name|register
argument_list|(
name|tempDataNode
argument_list|)
decl_stmt|;
return|return
operator|new
name|DatanodeID
argument_list|(
name|command
operator|.
name|getDatanodeUUID
argument_list|()
argument_list|,
name|tempDataNode
argument_list|)
return|;
block|}
comment|/**    * Get a datanode ID.    *    * @return DatanodeID    */
DECL|method|getDatanodeID ()
specifier|public
specifier|static
name|DatanodeID
name|getDatanodeID
parameter_list|()
block|{
return|return
name|getDatanodeID
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getDatanodeID (String uuid)
specifier|private
specifier|static
name|DatanodeID
name|getDatanodeID
parameter_list|(
name|String
name|uuid
parameter_list|)
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|String
name|ipAddress
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
operator|+
literal|"."
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
operator|+
literal|"."
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
operator|+
literal|"."
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
decl_stmt|;
name|String
name|hostName
init|=
name|uuid
decl_stmt|;
return|return
operator|new
name|DatanodeID
argument_list|(
name|ipAddress
argument_list|,
name|hostName
argument_list|,
name|uuid
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
block|}
end_class

end_unit

