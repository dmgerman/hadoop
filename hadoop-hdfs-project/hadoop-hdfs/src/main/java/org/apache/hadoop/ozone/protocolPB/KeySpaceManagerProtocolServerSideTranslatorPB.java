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
name|KsmBucketInfo
import|;
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
name|KsmKeyArgs
import|;
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
name|KsmKeyInfo
import|;
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
name|KeySpaceManagerProtocol
import|;
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
name|protocol
operator|.
name|proto
operator|.
name|KeySpaceManagerProtocolProtos
operator|.
name|CreateBucketRequest
import|;
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
name|CreateBucketResponse
import|;
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
name|InfoBucketRequest
import|;
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
name|InfoBucketResponse
import|;
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
name|CreateKeyRequest
import|;
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
name|CreateKeyResponse
import|;
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
name|KeyArgs
import|;
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
comment|/**  * This class is the server-side translator that forwards requests received on  * {@link org.apache.hadoop.ksm.protocolPB.KeySpaceManagerProtocolPB} to the  * KeySpaceManagerService server implementation.  */
end_comment

begin_class
DECL|class|KeySpaceManagerProtocolServerSideTranslatorPB
specifier|public
class|class
name|KeySpaceManagerProtocolServerSideTranslatorPB
implements|implements
name|KeySpaceManagerProtocolPB
block|{
DECL|field|impl
specifier|private
specifier|final
name|KeySpaceManagerProtocol
name|impl
decl_stmt|;
comment|/**    * Constructs an instance of the server handler.    *    * @param impl KeySpaceManagerProtocolPB    */
DECL|method|KeySpaceManagerProtocolServerSideTranslatorPB ( KeySpaceManagerProtocol impl)
specifier|public
name|KeySpaceManagerProtocolServerSideTranslatorPB
parameter_list|(
name|KeySpaceManagerProtocol
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
comment|// Convert and exception to corresponding status code
DECL|method|exceptionToResponseStatus (IOException ex)
specifier|private
name|Status
name|exceptionToResponseStatus
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
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
name|ex
decl_stmt|;
switch|switch
condition|(
name|ksmException
operator|.
name|getResult
argument_list|()
condition|)
block|{
case|case
name|FAILED_VOLUME_ALREADY_EXISTS
case|:
return|return
name|Status
operator|.
name|VOLUME_ALREADY_EXISTS
return|;
case|case
name|FAILED_TOO_MANY_USER_VOLUMES
case|:
return|return
name|Status
operator|.
name|USER_TOO_MANY_VOLUMES
return|;
case|case
name|FAILED_VOLUME_NOT_FOUND
case|:
return|return
name|Status
operator|.
name|VOLUME_NOT_FOUND
return|;
case|case
name|FAILED_USER_NOT_FOUND
case|:
return|return
name|Status
operator|.
name|USER_NOT_FOUND
return|;
case|case
name|FAILED_BUCKET_ALREADY_EXISTS
case|:
return|return
name|Status
operator|.
name|BUCKET_ALREADY_EXISTS
return|;
case|case
name|FAILED_BUCKET_NOT_FOUND
case|:
return|return
name|Status
operator|.
name|BUCKET_NOT_FOUND
return|;
default|default:
return|return
name|Status
operator|.
name|INTERNAL_ERROR
return|;
block|}
block|}
else|else
block|{
return|return
name|Status
operator|.
name|INTERNAL_ERROR
return|;
block|}
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
name|resp
operator|.
name|setStatus
argument_list|(
name|exceptionToResponseStatus
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
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
name|SetVolumePropertyResponse
operator|.
name|Builder
name|resp
init|=
name|SetVolumePropertyResponse
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
name|String
name|volume
init|=
name|request
operator|.
name|getVolumeName
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|request
operator|.
name|hasQuotaInBytes
argument_list|()
condition|)
block|{
name|long
name|quota
init|=
name|request
operator|.
name|getQuotaInBytes
argument_list|()
decl_stmt|;
name|impl
operator|.
name|setQuota
argument_list|(
name|volume
argument_list|,
name|quota
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|owner
init|=
name|request
operator|.
name|getOwnerName
argument_list|()
decl_stmt|;
name|impl
operator|.
name|setOwner
argument_list|(
name|volume
argument_list|,
name|owner
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|resp
operator|.
name|setStatus
argument_list|(
name|exceptionToResponseStatus
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
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
name|InfoVolumeResponse
operator|.
name|Builder
name|resp
init|=
name|InfoVolumeResponse
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
name|String
name|volume
init|=
name|request
operator|.
name|getVolumeName
argument_list|()
decl_stmt|;
try|try
block|{
name|KsmVolumeArgs
name|ret
init|=
name|impl
operator|.
name|getVolumeInfo
argument_list|(
name|volume
argument_list|)
decl_stmt|;
name|resp
operator|.
name|setVolumeInfo
argument_list|(
name|ret
operator|.
name|getProtobuf
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
name|resp
operator|.
name|setStatus
argument_list|(
name|exceptionToResponseStatus
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
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
annotation|@
name|Override
DECL|method|createBucket ( RpcController controller, CreateBucketRequest request)
specifier|public
name|CreateBucketResponse
name|createBucket
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|CreateBucketRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|CreateBucketResponse
operator|.
name|Builder
name|resp
init|=
name|CreateBucketResponse
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|impl
operator|.
name|createBucket
argument_list|(
name|KsmBucketInfo
operator|.
name|getFromProtobuf
argument_list|(
name|request
operator|.
name|getBucketInfo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|resp
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|resp
operator|.
name|setStatus
argument_list|(
name|exceptionToResponseStatus
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
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
DECL|method|infoBucket ( RpcController controller, InfoBucketRequest request)
specifier|public
name|InfoBucketResponse
name|infoBucket
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|InfoBucketRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|InfoBucketResponse
operator|.
name|Builder
name|resp
init|=
name|InfoBucketResponse
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|KsmBucketInfo
name|ksmBucketInfo
init|=
name|impl
operator|.
name|getBucketInfo
argument_list|(
name|request
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|request
operator|.
name|getBucketName
argument_list|()
argument_list|)
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
name|resp
operator|.
name|setBucketInfo
argument_list|(
name|ksmBucketInfo
operator|.
name|getProtobuf
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
name|resp
operator|.
name|setStatus
argument_list|(
name|exceptionToResponseStatus
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
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
DECL|method|createKey ( RpcController controller, CreateKeyRequest request )
specifier|public
name|CreateKeyResponse
name|createKey
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|CreateKeyRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|CreateKeyResponse
operator|.
name|Builder
name|resp
init|=
name|CreateKeyResponse
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|KeyArgs
name|keyArgs
init|=
name|request
operator|.
name|getKeyArgs
argument_list|()
decl_stmt|;
name|KsmKeyArgs
name|ksmKeyArgs
init|=
operator|new
name|KsmKeyArgs
operator|.
name|Builder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|keyArgs
operator|.
name|getVolumeName
argument_list|()
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|keyArgs
operator|.
name|getBucketName
argument_list|()
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|keyArgs
operator|.
name|getKeyName
argument_list|()
argument_list|)
operator|.
name|setDataSize
argument_list|(
name|keyArgs
operator|.
name|getDataSize
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|KsmKeyInfo
name|keyInfo
init|=
name|impl
operator|.
name|allocateKey
argument_list|(
name|ksmKeyArgs
argument_list|)
decl_stmt|;
name|resp
operator|.
name|setKeyInfo
argument_list|(
name|keyInfo
operator|.
name|getProtobuf
argument_list|()
argument_list|)
expr_stmt|;
name|resp
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|resp
operator|.
name|setStatus
argument_list|(
name|exceptionToResponseStatus
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|resp
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

