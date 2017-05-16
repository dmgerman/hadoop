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
name|ksm
operator|.
name|helpers
operator|.
name|KsmVolumeArgs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ksm
operator|.
name|protocol
operator|.
name|KeyspaceManagerProtocol
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ksm
operator|.
name|protocolPB
operator|.
name|KeySpaceManagerProtocolPB
import|;
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
name|ksm
operator|.
name|exceptions
operator|.
name|KSMException
import|;
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
name|ksm
operator|.
name|exceptions
operator|.
name|KSMException
operator|.
name|ResultCodes
import|;
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
name|KeySpaceManagerProtocolProtos
operator|.
name|CreateVolumeRequest
import|;
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
name|KeySpaceManagerProtocolProtos
operator|.
name|CreateVolumeResponse
import|;
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
name|KeySpaceManagerProtocolProtos
operator|.
name|SetVolumePropertyRequest
import|;
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
name|KeySpaceManagerProtocolProtos
operator|.
name|SetVolumePropertyResponse
import|;
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
name|KeySpaceManagerProtocolProtos
operator|.
name|CheckVolumeAccessRequest
import|;
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
name|KeySpaceManagerProtocolProtos
operator|.
name|CheckVolumeAccessResponse
import|;
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
name|KeySpaceManagerProtocolProtos
operator|.
name|InfoVolumeRequest
import|;
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
name|KeySpaceManagerProtocolProtos
operator|.
name|InfoVolumeResponse
import|;
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
name|KeySpaceManagerProtocolProtos
operator|.
name|DeleteVolumeRequest
import|;
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
name|KeySpaceManagerProtocolProtos
operator|.
name|DeleteVolumeResponse
import|;
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
name|KeySpaceManagerProtocolProtos
operator|.
name|ListVolumeRequest
import|;
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
name|KeySpaceManagerProtocolProtos
operator|.
name|ListVolumeResponse
import|;
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
name|KeySpaceManagerProtocolProtos
operator|.
name|Status
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
comment|/**  * This class is the server-side translator that forwards requests received on  * {@link org.apache.hadoop.ksm.protocolPB.KeySpaceManagerProtocolPB} to the  * KeyspaceManagerService server implementation.  */
end_comment

begin_class
DECL|class|KeyspaceManagerProtocolServerSideTranslatorPB
specifier|public
class|class
name|KeyspaceManagerProtocolServerSideTranslatorPB
implements|implements
name|KeySpaceManagerProtocolPB
block|{
DECL|field|impl
specifier|private
specifier|final
name|KeyspaceManagerProtocol
name|impl
decl_stmt|;
comment|/**    * Constructs an instance of the server handler.    *    * @param impl KeySpaceManagerProtocolPB    */
DECL|method|KeyspaceManagerProtocolServerSideTranslatorPB ( KeyspaceManagerProtocol impl)
specifier|public
name|KeyspaceManagerProtocolServerSideTranslatorPB
parameter_list|(
name|KeyspaceManagerProtocol
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
DECL|method|createVolume ( RpcController controller, CreateVolumeRequest request)
specifier|public
name|CreateVolumeResponse
name|createVolume
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|CreateVolumeRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|CreateVolumeResponse
operator|.
name|Builder
name|resp
init|=
name|CreateVolumeResponse
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|resp
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
expr_stmt|;
try|try
block|{
name|impl
operator|.
name|createVolume
argument_list|(
name|KsmVolumeArgs
operator|.
name|getFromProtobuf
argument_list|(
name|request
operator|.
name|getVolumeInfo
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
if|if
condition|(
name|e
operator|instanceof
name|KSMException
condition|)
block|{
name|KSMException
name|ksmException
init|=
operator|(
name|KSMException
operator|)
name|e
decl_stmt|;
if|if
condition|(
name|ksmException
operator|.
name|getResult
argument_list|()
operator|==
name|ResultCodes
operator|.
name|FAILED_VOLUME_ALREADY_EXISTS
condition|)
block|{
name|resp
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|VOLUME_ALREADY_EXISTS
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ksmException
operator|.
name|getResult
argument_list|()
operator|==
name|ResultCodes
operator|.
name|FAILED_TOO_MANY_USER_VOLUMES
condition|)
block|{
name|resp
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|USER_TOO_MANY_VOLUMES
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|resp
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|INTERNAL_ERROR
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|resp
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setVolumeProperty ( RpcController controller, SetVolumePropertyRequest request)
specifier|public
name|SetVolumePropertyResponse
name|setVolumeProperty
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|SetVolumePropertyRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|checkVolumeAccess ( RpcController controller, CheckVolumeAccessRequest request)
specifier|public
name|CheckVolumeAccessResponse
name|checkVolumeAccess
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|CheckVolumeAccessRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|infoVolume ( RpcController controller, InfoVolumeRequest request)
specifier|public
name|InfoVolumeResponse
name|infoVolume
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|InfoVolumeRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|deleteVolume ( RpcController controller, DeleteVolumeRequest request)
specifier|public
name|DeleteVolumeResponse
name|deleteVolume
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|DeleteVolumeRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|listVolumes ( RpcController controller, ListVolumeRequest request)
specifier|public
name|ListVolumeResponse
name|listVolumes
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|ListVolumeRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

