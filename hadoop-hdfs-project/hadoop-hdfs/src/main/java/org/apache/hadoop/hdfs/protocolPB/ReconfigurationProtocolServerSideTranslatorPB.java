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
name|ReconfigurationProtocol
import|;
end_import

begin_import
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
comment|/**  * This class is used on the server side. Calls come across the wire for the  * for protocol {@link ReconfigurationProtocolPB}.  * This class translates the PB data types  * to the native data types used inside the NN/DN as specified in the generic  * ReconfigurationProtocol.  */
end_comment

begin_class
DECL|class|ReconfigurationProtocolServerSideTranslatorPB
specifier|public
class|class
name|ReconfigurationProtocolServerSideTranslatorPB
implements|implements
name|ReconfigurationProtocolPB
block|{
DECL|field|impl
specifier|private
specifier|final
name|ReconfigurationProtocol
name|impl
decl_stmt|;
DECL|field|START_RECONFIG_RESP
specifier|private
specifier|static
specifier|final
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
DECL|method|ReconfigurationProtocolServerSideTranslatorPB ( ReconfigurationProtocol impl)
specifier|public
name|ReconfigurationProtocolServerSideTranslatorPB
parameter_list|(
name|ReconfigurationProtocol
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
DECL|method|startReconfiguration ( RpcController controller, StartReconfigurationRequestProto request)
specifier|public
name|StartReconfigurationResponseProto
name|startReconfiguration
parameter_list|(
name|RpcController
name|controller
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
block|}
end_class

end_unit

