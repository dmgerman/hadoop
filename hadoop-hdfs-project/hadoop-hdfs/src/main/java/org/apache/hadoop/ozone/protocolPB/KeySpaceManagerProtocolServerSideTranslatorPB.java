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
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmBucketArgs
import|;
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
name|SetBucketPropertyRequest
import|;
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
name|SetBucketPropertyResponse
import|;
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
name|DeleteBucketRequest
import|;
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
name|DeleteBucketResponse
import|;
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
name|LocateKeyRequest
import|;
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
name|LocateKeyResponse
import|;
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
name|ListBucketsRequest
import|;
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
name|ListBucketsResponse
import|;
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
name|ListKeysRequest
import|;
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
name|ListKeysResponse
import|;
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
name|util
operator|.
name|List
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
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|KeySpaceManagerProtocolServerSideTranslatorPB
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|FAILED_VOLUME_NOT_EMPTY
case|:
return|return
name|Status
operator|.
name|VOLUME_NOT_EMPTY
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
case|case
name|FAILED_BUCKET_NOT_EMPTY
case|:
return|return
name|Status
operator|.
name|BUCKET_NOT_EMPTY
return|;
case|case
name|FAILED_KEY_ALREADY_EXISTS
case|:
return|return
name|Status
operator|.
name|KEY_ALREADY_EXISTS
return|;
case|case
name|FAILED_KEY_NOT_FOUND
case|:
return|return
name|Status
operator|.
name|KEY_NOT_FOUND
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
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Unknown error occurs"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
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
name|CheckVolumeAccessResponse
operator|.
name|Builder
name|resp
init|=
name|CheckVolumeAccessResponse
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
name|boolean
name|access
init|=
name|impl
operator|.
name|checkVolumeAccess
argument_list|(
name|request
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|request
operator|.
name|getUserAcl
argument_list|()
argument_list|)
decl_stmt|;
comment|// if no access, set the response status as access denied
if|if
condition|(
operator|!
name|access
condition|)
block|{
name|resp
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|ACCESS_DENIED
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
name|DeleteVolumeResponse
operator|.
name|Builder
name|resp
init|=
name|DeleteVolumeResponse
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
name|deleteVolume
argument_list|(
name|request
operator|.
name|getVolumeName
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
name|ListVolumeResponse
operator|.
name|Builder
name|resp
init|=
name|ListVolumeResponse
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|KsmVolumeArgs
argument_list|>
name|result
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|request
operator|.
name|getScope
argument_list|()
operator|==
name|ListVolumeRequest
operator|.
name|Scope
operator|.
name|VOLUMES_BY_USER
condition|)
block|{
name|result
operator|=
name|impl
operator|.
name|listVolumeByUser
argument_list|(
name|request
operator|.
name|getUserName
argument_list|()
argument_list|,
name|request
operator|.
name|getPrefix
argument_list|()
argument_list|,
name|request
operator|.
name|getPrevKey
argument_list|()
argument_list|,
name|request
operator|.
name|getMaxKeys
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|getScope
argument_list|()
operator|==
name|ListVolumeRequest
operator|.
name|Scope
operator|.
name|VOLUMES_BY_CLUSTER
condition|)
block|{
name|result
operator|=
name|impl
operator|.
name|listAllVolumes
argument_list|(
name|request
operator|.
name|getPrefix
argument_list|()
argument_list|,
name|request
operator|.
name|getPrevKey
argument_list|()
argument_list|,
name|request
operator|.
name|getMaxKeys
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Failed to get volumes for given scope "
operator|+
name|request
operator|.
name|getScope
argument_list|()
argument_list|)
throw|;
block|}
name|result
operator|.
name|forEach
argument_list|(
name|item
lambda|->
name|resp
operator|.
name|addVolumeInfo
argument_list|(
name|item
operator|.
name|getProtobuf
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
DECL|method|createKey ( RpcController controller, LocateKeyRequest request )
specifier|public
name|LocateKeyResponse
name|createKey
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|LocateKeyRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|LocateKeyResponse
operator|.
name|Builder
name|resp
init|=
name|LocateKeyResponse
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
annotation|@
name|Override
DECL|method|lookupKey ( RpcController controller, LocateKeyRequest request )
specifier|public
name|LocateKeyResponse
name|lookupKey
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|LocateKeyRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|LocateKeyResponse
operator|.
name|Builder
name|resp
init|=
name|LocateKeyResponse
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
name|lookupKey
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
annotation|@
name|Override
DECL|method|setBucketProperty ( RpcController controller, SetBucketPropertyRequest request)
specifier|public
name|SetBucketPropertyResponse
name|setBucketProperty
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|SetBucketPropertyRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|SetBucketPropertyResponse
operator|.
name|Builder
name|resp
init|=
name|SetBucketPropertyResponse
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|impl
operator|.
name|setBucketProperty
argument_list|(
name|KsmBucketArgs
operator|.
name|getFromProtobuf
argument_list|(
name|request
operator|.
name|getBucketArgs
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
DECL|method|deleteKey (RpcController controller, LocateKeyRequest request)
specifier|public
name|LocateKeyResponse
name|deleteKey
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|LocateKeyRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|LocateKeyResponse
operator|.
name|Builder
name|resp
init|=
name|LocateKeyResponse
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
name|build
argument_list|()
decl_stmt|;
name|impl
operator|.
name|deleteKey
argument_list|(
name|ksmKeyArgs
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
DECL|method|deleteBucket ( RpcController controller, DeleteBucketRequest request)
specifier|public
name|DeleteBucketResponse
name|deleteBucket
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|DeleteBucketRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|DeleteBucketResponse
operator|.
name|Builder
name|resp
init|=
name|DeleteBucketResponse
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
name|deleteBucket
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
DECL|method|listBuckets ( RpcController controller, ListBucketsRequest request)
specifier|public
name|ListBucketsResponse
name|listBuckets
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|ListBucketsRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|ListBucketsResponse
operator|.
name|Builder
name|resp
init|=
name|ListBucketsResponse
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|KsmBucketInfo
argument_list|>
name|buckets
init|=
name|impl
operator|.
name|listBuckets
argument_list|(
name|request
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|request
operator|.
name|getStartKey
argument_list|()
argument_list|,
name|request
operator|.
name|getPrefix
argument_list|()
argument_list|,
name|request
operator|.
name|getCount
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|KsmBucketInfo
name|bucket
range|:
name|buckets
control|)
block|{
name|resp
operator|.
name|addBucketInfo
argument_list|(
name|bucket
operator|.
name|getProtobuf
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
DECL|method|listKeys (RpcController controller, ListKeysRequest request)
specifier|public
name|ListKeysResponse
name|listKeys
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|ListKeysRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|ListKeysResponse
operator|.
name|Builder
name|resp
init|=
name|ListKeysResponse
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|KsmKeyInfo
argument_list|>
name|keys
init|=
name|impl
operator|.
name|listKeys
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
argument_list|,
name|request
operator|.
name|getStartKey
argument_list|()
argument_list|,
name|request
operator|.
name|getPrefix
argument_list|()
argument_list|,
name|request
operator|.
name|getCount
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|KsmKeyInfo
name|key
range|:
name|keys
control|)
block|{
name|resp
operator|.
name|addKeyInfo
argument_list|(
name|key
operator|.
name|getProtobuf
argument_list|()
argument_list|)
expr_stmt|;
block|}
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

