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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|AddMountTableEntryRequestProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|AddMountTableEntryResponseProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|DisableNameserviceRequestProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|DisableNameserviceResponseProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|EnableNameserviceRequestProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|EnableNameserviceResponseProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|EnterSafeModeRequestProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|EnterSafeModeResponseProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|GetDisabledNameservicesRequestProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|GetDisabledNameservicesResponseProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|GetMountTableEntriesRequestProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|GetMountTableEntriesResponseProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|GetSafeModeRequestProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|GetSafeModeResponseProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|LeaveSafeModeRequestProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|LeaveSafeModeResponseProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|RefreshMountTableEntriesRequestProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|RefreshMountTableEntriesResponseProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|RemoveMountTableEntryRequestProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|RemoveMountTableEntryResponseProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|UpdateMountTableEntryRequestProto
import|;
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|UpdateMountTableEntryResponseProto
import|;
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
name|federation
operator|.
name|resolver
operator|.
name|MountTableManager
import|;
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
name|federation
operator|.
name|router
operator|.
name|NameserviceManager
import|;
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
name|federation
operator|.
name|router
operator|.
name|RouterStateManager
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|AddMountTableEntryRequest
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|AddMountTableEntryResponse
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|DisableNameserviceRequest
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|DisableNameserviceResponse
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|EnableNameserviceRequest
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|EnableNameserviceResponse
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|EnterSafeModeRequest
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|EnterSafeModeResponse
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|GetDisabledNameservicesRequest
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|GetDisabledNameservicesResponse
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|GetMountTableEntriesRequest
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|GetMountTableEntriesResponse
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|GetSafeModeRequest
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|GetSafeModeResponse
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|LeaveSafeModeRequest
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|LeaveSafeModeResponse
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|RefreshMountTableEntriesRequest
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|RefreshMountTableEntriesResponse
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|RemoveMountTableEntryRequest
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|RemoveMountTableEntryResponse
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|UpdateMountTableEntryRequest
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|UpdateMountTableEntryResponse
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|AddMountTableEntryRequestPBImpl
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|AddMountTableEntryResponsePBImpl
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|DisableNameserviceRequestPBImpl
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|DisableNameserviceResponsePBImpl
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|EnableNameserviceRequestPBImpl
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|EnableNameserviceResponsePBImpl
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|EnterSafeModeResponsePBImpl
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetDisabledNameservicesResponsePBImpl
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetMountTableEntriesRequestPBImpl
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetMountTableEntriesResponsePBImpl
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetSafeModeResponsePBImpl
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|LeaveSafeModeResponsePBImpl
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|RefreshMountTableEntriesRequestPBImpl
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|RefreshMountTableEntriesResponsePBImpl
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|RemoveMountTableEntryRequestPBImpl
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|RemoveMountTableEntryResponsePBImpl
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|UpdateMountTableEntryRequestPBImpl
import|;
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
operator|.
name|UpdateMountTableEntryResponsePBImpl
import|;
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
comment|/**  * This class forwards NN's ClientProtocol calls as RPC calls to the NN server  * while translating from the parameter types used in ClientProtocol to the  * new PB types.  */
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
DECL|class|RouterAdminProtocolTranslatorPB
specifier|public
class|class
name|RouterAdminProtocolTranslatorPB
implements|implements
name|ProtocolMetaInterface
implements|,
name|MountTableManager
implements|,
name|Closeable
implements|,
name|ProtocolTranslator
implements|,
name|RouterStateManager
implements|,
name|NameserviceManager
block|{
DECL|field|rpcProxy
specifier|final
specifier|private
name|RouterAdminProtocolPB
name|rpcProxy
decl_stmt|;
DECL|method|RouterAdminProtocolTranslatorPB (RouterAdminProtocolPB proxy)
specifier|public
name|RouterAdminProtocolTranslatorPB
parameter_list|(
name|RouterAdminProtocolPB
name|proxy
parameter_list|)
block|{
name|rpcProxy
operator|=
name|proxy
expr_stmt|;
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
name|RouterAdminProtocolPB
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
name|RouterAdminProtocolPB
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
DECL|method|addMountTableEntry ( AddMountTableEntryRequest request)
specifier|public
name|AddMountTableEntryResponse
name|addMountTableEntry
parameter_list|(
name|AddMountTableEntryRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|AddMountTableEntryRequestPBImpl
name|requestPB
init|=
operator|(
name|AddMountTableEntryRequestPBImpl
operator|)
name|request
decl_stmt|;
name|AddMountTableEntryRequestProto
name|proto
init|=
name|requestPB
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
name|AddMountTableEntryResponseProto
name|response
init|=
name|rpcProxy
operator|.
name|addMountTableEntry
argument_list|(
literal|null
argument_list|,
name|proto
argument_list|)
decl_stmt|;
return|return
operator|new
name|AddMountTableEntryResponsePBImpl
argument_list|(
name|response
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
operator|new
name|IOException
argument_list|(
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|e
argument_list|)
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|updateMountTableEntry ( UpdateMountTableEntryRequest request)
specifier|public
name|UpdateMountTableEntryResponse
name|updateMountTableEntry
parameter_list|(
name|UpdateMountTableEntryRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|UpdateMountTableEntryRequestPBImpl
name|requestPB
init|=
operator|(
name|UpdateMountTableEntryRequestPBImpl
operator|)
name|request
decl_stmt|;
name|UpdateMountTableEntryRequestProto
name|proto
init|=
name|requestPB
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
name|UpdateMountTableEntryResponseProto
name|response
init|=
name|rpcProxy
operator|.
name|updateMountTableEntry
argument_list|(
literal|null
argument_list|,
name|proto
argument_list|)
decl_stmt|;
return|return
operator|new
name|UpdateMountTableEntryResponsePBImpl
argument_list|(
name|response
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
operator|new
name|IOException
argument_list|(
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|e
argument_list|)
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|removeMountTableEntry ( RemoveMountTableEntryRequest request)
specifier|public
name|RemoveMountTableEntryResponse
name|removeMountTableEntry
parameter_list|(
name|RemoveMountTableEntryRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|RemoveMountTableEntryRequestPBImpl
name|requestPB
init|=
operator|(
name|RemoveMountTableEntryRequestPBImpl
operator|)
name|request
decl_stmt|;
name|RemoveMountTableEntryRequestProto
name|proto
init|=
name|requestPB
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
name|RemoveMountTableEntryResponseProto
name|responseProto
init|=
name|rpcProxy
operator|.
name|removeMountTableEntry
argument_list|(
literal|null
argument_list|,
name|proto
argument_list|)
decl_stmt|;
return|return
operator|new
name|RemoveMountTableEntryResponsePBImpl
argument_list|(
name|responseProto
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
operator|new
name|IOException
argument_list|(
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|e
argument_list|)
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getMountTableEntries ( GetMountTableEntriesRequest request)
specifier|public
name|GetMountTableEntriesResponse
name|getMountTableEntries
parameter_list|(
name|GetMountTableEntriesRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|GetMountTableEntriesRequestPBImpl
name|requestPB
init|=
operator|(
name|GetMountTableEntriesRequestPBImpl
operator|)
name|request
decl_stmt|;
name|GetMountTableEntriesRequestProto
name|proto
init|=
name|requestPB
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
name|GetMountTableEntriesResponseProto
name|response
init|=
name|rpcProxy
operator|.
name|getMountTableEntries
argument_list|(
literal|null
argument_list|,
name|proto
argument_list|)
decl_stmt|;
return|return
operator|new
name|GetMountTableEntriesResponsePBImpl
argument_list|(
name|response
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
operator|new
name|IOException
argument_list|(
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|e
argument_list|)
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|enterSafeMode (EnterSafeModeRequest request)
specifier|public
name|EnterSafeModeResponse
name|enterSafeMode
parameter_list|(
name|EnterSafeModeRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|EnterSafeModeRequestProto
name|proto
init|=
name|EnterSafeModeRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|EnterSafeModeResponseProto
name|response
init|=
name|rpcProxy
operator|.
name|enterSafeMode
argument_list|(
literal|null
argument_list|,
name|proto
argument_list|)
decl_stmt|;
return|return
operator|new
name|EnterSafeModeResponsePBImpl
argument_list|(
name|response
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
operator|new
name|IOException
argument_list|(
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|e
argument_list|)
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|leaveSafeMode (LeaveSafeModeRequest request)
specifier|public
name|LeaveSafeModeResponse
name|leaveSafeMode
parameter_list|(
name|LeaveSafeModeRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|LeaveSafeModeRequestProto
name|proto
init|=
name|LeaveSafeModeRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|LeaveSafeModeResponseProto
name|response
init|=
name|rpcProxy
operator|.
name|leaveSafeMode
argument_list|(
literal|null
argument_list|,
name|proto
argument_list|)
decl_stmt|;
return|return
operator|new
name|LeaveSafeModeResponsePBImpl
argument_list|(
name|response
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
operator|new
name|IOException
argument_list|(
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|e
argument_list|)
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSafeMode (GetSafeModeRequest request)
specifier|public
name|GetSafeModeResponse
name|getSafeMode
parameter_list|(
name|GetSafeModeRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|GetSafeModeRequestProto
name|proto
init|=
name|GetSafeModeRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|GetSafeModeResponseProto
name|response
init|=
name|rpcProxy
operator|.
name|getSafeMode
argument_list|(
literal|null
argument_list|,
name|proto
argument_list|)
decl_stmt|;
return|return
operator|new
name|GetSafeModeResponsePBImpl
argument_list|(
name|response
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
operator|new
name|IOException
argument_list|(
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|e
argument_list|)
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|disableNameservice ( DisableNameserviceRequest request)
specifier|public
name|DisableNameserviceResponse
name|disableNameservice
parameter_list|(
name|DisableNameserviceRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|DisableNameserviceRequestPBImpl
name|requestPB
init|=
operator|(
name|DisableNameserviceRequestPBImpl
operator|)
name|request
decl_stmt|;
name|DisableNameserviceRequestProto
name|proto
init|=
name|requestPB
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
name|DisableNameserviceResponseProto
name|response
init|=
name|rpcProxy
operator|.
name|disableNameservice
argument_list|(
literal|null
argument_list|,
name|proto
argument_list|)
decl_stmt|;
return|return
operator|new
name|DisableNameserviceResponsePBImpl
argument_list|(
name|response
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
operator|new
name|IOException
argument_list|(
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|e
argument_list|)
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|enableNameservice ( EnableNameserviceRequest request)
specifier|public
name|EnableNameserviceResponse
name|enableNameservice
parameter_list|(
name|EnableNameserviceRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|EnableNameserviceRequestPBImpl
name|requestPB
init|=
operator|(
name|EnableNameserviceRequestPBImpl
operator|)
name|request
decl_stmt|;
name|EnableNameserviceRequestProto
name|proto
init|=
name|requestPB
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
name|EnableNameserviceResponseProto
name|response
init|=
name|rpcProxy
operator|.
name|enableNameservice
argument_list|(
literal|null
argument_list|,
name|proto
argument_list|)
decl_stmt|;
return|return
operator|new
name|EnableNameserviceResponsePBImpl
argument_list|(
name|response
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
operator|new
name|IOException
argument_list|(
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|e
argument_list|)
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDisabledNameservices ( GetDisabledNameservicesRequest request)
specifier|public
name|GetDisabledNameservicesResponse
name|getDisabledNameservices
parameter_list|(
name|GetDisabledNameservicesRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|GetDisabledNameservicesRequestProto
name|proto
init|=
name|GetDisabledNameservicesRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|GetDisabledNameservicesResponseProto
name|response
init|=
name|rpcProxy
operator|.
name|getDisabledNameservices
argument_list|(
literal|null
argument_list|,
name|proto
argument_list|)
decl_stmt|;
return|return
operator|new
name|GetDisabledNameservicesResponsePBImpl
argument_list|(
name|response
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
operator|new
name|IOException
argument_list|(
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|e
argument_list|)
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|refreshMountTableEntries ( RefreshMountTableEntriesRequest request)
specifier|public
name|RefreshMountTableEntriesResponse
name|refreshMountTableEntries
parameter_list|(
name|RefreshMountTableEntriesRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|RefreshMountTableEntriesRequestPBImpl
name|requestPB
init|=
operator|(
name|RefreshMountTableEntriesRequestPBImpl
operator|)
name|request
decl_stmt|;
name|RefreshMountTableEntriesRequestProto
name|proto
init|=
name|requestPB
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
name|RefreshMountTableEntriesResponseProto
name|response
init|=
name|rpcProxy
operator|.
name|refreshMountTableEntries
argument_list|(
literal|null
argument_list|,
name|proto
argument_list|)
decl_stmt|;
return|return
operator|new
name|RefreshMountTableEntriesResponsePBImpl
argument_list|(
name|response
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
operator|new
name|IOException
argument_list|(
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|e
argument_list|)
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

