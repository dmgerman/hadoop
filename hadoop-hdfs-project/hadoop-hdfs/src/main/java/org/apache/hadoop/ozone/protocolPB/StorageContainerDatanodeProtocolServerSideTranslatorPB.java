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
import|;
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * This class is the server-side translator that forwards requests received on  * {@link StorageContainerDatanodeProtocolPB} to the {@link  * StorageContainerDatanodeProtocol} server implementation.  */
end_comment

begin_class
DECL|class|StorageContainerDatanodeProtocolServerSideTranslatorPB
specifier|public
class|class
name|StorageContainerDatanodeProtocolServerSideTranslatorPB
implements|implements
name|StorageContainerDatanodeProtocolPB
block|{
DECL|field|impl
specifier|private
specifier|final
name|StorageContainerDatanodeProtocol
name|impl
decl_stmt|;
DECL|method|StorageContainerDatanodeProtocolServerSideTranslatorPB ( StorageContainerDatanodeProtocol impl)
specifier|public
name|StorageContainerDatanodeProtocolServerSideTranslatorPB
parameter_list|(
name|StorageContainerDatanodeProtocol
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
specifier|public
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMVersionResponseProto
DECL|method|getVersion (RpcController controller, StorageContainerDatanodeProtocolProtos.SCMVersionRequestProto request)
name|getVersion
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMVersionRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|impl
operator|.
name|getVersion
argument_list|()
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
specifier|public
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMRegisteredCmdResponseProto
DECL|method|register (RpcController controller, StorageContainerDatanodeProtocolProtos .SCMRegisterRequestProto request)
name|register
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMRegisterRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|String
index|[]
name|addressArray
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|hasAddressList
argument_list|()
condition|)
block|{
name|addressArray
operator|=
name|request
operator|.
name|getAddressList
argument_list|()
operator|.
name|getAddressListList
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
try|try
block|{
return|return
name|impl
operator|.
name|register
argument_list|(
name|DatanodeID
operator|.
name|getFromProtoBuf
argument_list|(
name|request
operator|.
name|getDatanodeID
argument_list|()
argument_list|)
argument_list|,
name|addressArray
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
specifier|public
name|SCMHeartbeatResponseProto
DECL|method|sendHeartbeat (RpcController controller, SCMHeartbeatRequestProto request)
name|sendHeartbeat
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|SCMHeartbeatRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|impl
operator|.
name|sendHeartbeat
argument_list|(
name|DatanodeID
operator|.
name|getFromProtoBuf
argument_list|(
name|request
operator|.
name|getDatanodeID
argument_list|()
argument_list|)
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
block|}
end_class

end_unit

