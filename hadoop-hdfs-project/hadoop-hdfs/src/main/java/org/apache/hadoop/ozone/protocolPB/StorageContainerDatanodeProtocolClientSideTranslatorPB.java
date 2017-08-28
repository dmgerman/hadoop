begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.protocolPB
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocolPB
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
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsProto
import|;
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
name|ReportState
import|;
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
name|SCMHeartbeatRequestProto
import|;
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
name|SCMHeartbeatResponseProto
import|;
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
name|SCMNodeReport
import|;
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
name|SCMRegisterRequestProto
import|;
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
name|SCMRegisteredCmdResponseProto
import|;
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
name|SCMVersionRequestProto
import|;
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
name|SCMVersionResponseProto
import|;
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
name|ContainerBlocksDeletionACKProto
import|;
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
name|ContainerBlocksDeletionACKResponseProto
import|;
end_import

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

begin_comment
comment|/**  * This class is the client-side translator to translate the requests made on  * the {@link StorageContainerDatanodeProtocol} interface to the RPC server  * implementing {@link StorageContainerDatanodeProtocolPB}.  */
end_comment

begin_class
DECL|class|StorageContainerDatanodeProtocolClientSideTranslatorPB
specifier|public
class|class
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
implements|implements
name|StorageContainerDatanodeProtocol
implements|,
name|ProtocolTranslator
implements|,
name|Closeable
block|{
comment|/**    * RpcController is not used and hence is set to null.    */
DECL|field|NULL_RPC_CONTROLLER
specifier|private
specifier|static
specifier|final
name|RpcController
name|NULL_RPC_CONTROLLER
init|=
literal|null
decl_stmt|;
DECL|field|rpcProxy
specifier|private
specifier|final
name|StorageContainerDatanodeProtocolPB
name|rpcProxy
decl_stmt|;
comment|/**    * Constructs a Client side interface that calls into SCM datanode protocol.    *    * @param rpcProxy - Proxy for RPC.    */
DECL|method|StorageContainerDatanodeProtocolClientSideTranslatorPB ( StorageContainerDatanodeProtocolPB rpcProxy)
specifier|public
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
parameter_list|(
name|StorageContainerDatanodeProtocolPB
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
comment|/**    * Closes this stream and releases any system resources associated with it. If    * the stream is already closed then invoking this method has no effect.    *<p>    *<p> As noted in {@link AutoCloseable#close()}, cases where the close may    * fail require careful attention. It is strongly advised to relinquish the    * underlying resources and to internally<em>mark</em> the {@code Closeable}    * as closed, prior to throwing the {@code IOException}.    *    * @throws IOException if an I/O error occurs    */
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
comment|/**    * Return the proxy object underlying this protocol translator.    *    * @return the proxy object underlying this protocol translator.    */
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
comment|/**    * Returns SCM version.    *    * @param unused - set to null and unused.    * @return Version info.    */
annotation|@
name|Override
DECL|method|getVersion (SCMVersionRequestProto unused)
specifier|public
name|SCMVersionResponseProto
name|getVersion
parameter_list|(
name|SCMVersionRequestProto
name|unused
parameter_list|)
throws|throws
name|IOException
block|{
name|SCMVersionRequestProto
name|request
init|=
name|SCMVersionRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|SCMVersionResponseProto
name|response
decl_stmt|;
try|try
block|{
name|response
operator|=
name|rpcProxy
operator|.
name|getVersion
argument_list|(
name|NULL_RPC_CONTROLLER
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|ex
parameter_list|)
block|{
throw|throw
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
return|return
name|response
return|;
block|}
comment|/**    * Send by datanode to SCM.    *    * @param datanodeID - DatanodeID    * @param nodeReport - node report    * @throws IOException    */
annotation|@
name|Override
DECL|method|sendHeartbeat (DatanodeID datanodeID, SCMNodeReport nodeReport, ReportState reportState)
specifier|public
name|SCMHeartbeatResponseProto
name|sendHeartbeat
parameter_list|(
name|DatanodeID
name|datanodeID
parameter_list|,
name|SCMNodeReport
name|nodeReport
parameter_list|,
name|ReportState
name|reportState
parameter_list|)
throws|throws
name|IOException
block|{
name|SCMHeartbeatRequestProto
operator|.
name|Builder
name|req
init|=
name|SCMHeartbeatRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|req
operator|.
name|setDatanodeID
argument_list|(
name|datanodeID
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|setNodeReport
argument_list|(
name|nodeReport
argument_list|)
expr_stmt|;
name|req
operator|.
name|setContainerReportState
argument_list|(
name|reportState
argument_list|)
expr_stmt|;
specifier|final
name|SCMHeartbeatResponseProto
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
name|NULL_RPC_CONTROLLER
argument_list|,
name|req
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
return|return
name|resp
return|;
block|}
comment|/**    * Register Datanode.    *    * @param datanodeID - DatanodID.    * @return SCM Command.    */
annotation|@
name|Override
DECL|method|register (DatanodeID datanodeID, String[] scmAddresses)
specifier|public
name|SCMRegisteredCmdResponseProto
name|register
parameter_list|(
name|DatanodeID
name|datanodeID
parameter_list|,
name|String
index|[]
name|scmAddresses
parameter_list|)
throws|throws
name|IOException
block|{
name|SCMRegisterRequestProto
operator|.
name|Builder
name|req
init|=
name|SCMRegisterRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|req
operator|.
name|setDatanodeID
argument_list|(
name|datanodeID
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|SCMRegisteredCmdResponseProto
name|response
decl_stmt|;
try|try
block|{
name|response
operator|=
name|rpcProxy
operator|.
name|register
argument_list|(
name|NULL_RPC_CONTROLLER
argument_list|,
name|req
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
return|return
name|response
return|;
block|}
comment|/**    * Send a container report.    *    * @param reports -- Container report    * @return HeartbeatRespose.nullcommand.    * @throws IOException    */
annotation|@
name|Override
DECL|method|sendContainerReport ( ContainerReportsProto reports)
specifier|public
name|SCMHeartbeatResponseProto
name|sendContainerReport
parameter_list|(
name|ContainerReportsProto
name|reports
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SCMHeartbeatResponseProto
name|resp
decl_stmt|;
try|try
block|{
name|resp
operator|=
name|rpcProxy
operator|.
name|sendContainerReport
argument_list|(
name|NULL_RPC_CONTROLLER
argument_list|,
name|reports
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
name|resp
return|;
block|}
annotation|@
name|Override
DECL|method|sendContainerBlocksDeletionACK ( ContainerBlocksDeletionACKProto deletedBlocks)
specifier|public
name|ContainerBlocksDeletionACKResponseProto
name|sendContainerBlocksDeletionACK
parameter_list|(
name|ContainerBlocksDeletionACKProto
name|deletedBlocks
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ContainerBlocksDeletionACKResponseProto
name|resp
decl_stmt|;
try|try
block|{
name|resp
operator|=
name|rpcProxy
operator|.
name|sendContainerBlocksDeletionACK
argument_list|(
name|NULL_RPC_CONTROLLER
argument_list|,
name|deletedBlocks
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
name|resp
return|;
block|}
block|}
end_class

end_unit

