begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.ratis.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|ratis
operator|.
name|utils
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
name|base
operator|.
name|Preconditions
import|;
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
name|om
operator|.
name|exceptions
operator|.
name|OMException
import|;
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
name|om
operator|.
name|request
operator|.
name|bucket
operator|.
name|OMBucketCreateRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|bucket
operator|.
name|OMBucketDeleteRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|bucket
operator|.
name|OMBucketSetPropertyRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|OMClientRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|bucket
operator|.
name|acl
operator|.
name|OMBucketAddAclRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|bucket
operator|.
name|acl
operator|.
name|OMBucketRemoveAclRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|bucket
operator|.
name|acl
operator|.
name|OMBucketSetAclRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|file
operator|.
name|OMDirectoryCreateRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|file
operator|.
name|OMFileCreateRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|key
operator|.
name|OMAllocateBlockRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|key
operator|.
name|OMKeyCommitRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|key
operator|.
name|OMKeyCreateRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|key
operator|.
name|OMKeyDeleteRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|key
operator|.
name|OMKeyPurgeRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|key
operator|.
name|OMKeyRenameRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|key
operator|.
name|acl
operator|.
name|OMKeyAddAclRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|key
operator|.
name|acl
operator|.
name|OMKeyRemoveAclRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|key
operator|.
name|acl
operator|.
name|OMKeySetAclRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|s3
operator|.
name|bucket
operator|.
name|S3BucketCreateRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|s3
operator|.
name|bucket
operator|.
name|S3BucketDeleteRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|s3
operator|.
name|multipart
operator|.
name|S3InitiateMultipartUploadRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|s3
operator|.
name|multipart
operator|.
name|S3MultipartUploadAbortRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|s3
operator|.
name|multipart
operator|.
name|S3MultipartUploadCommitPartRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|s3
operator|.
name|multipart
operator|.
name|S3MultipartUploadCompleteRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|volume
operator|.
name|OMVolumeCreateRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|volume
operator|.
name|OMVolumeDeleteRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|volume
operator|.
name|OMVolumeSetOwnerRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|volume
operator|.
name|OMVolumeSetQuotaRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|volume
operator|.
name|acl
operator|.
name|OMVolumeAddAclRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|volume
operator|.
name|acl
operator|.
name|OMVolumeRemoveAclRequest
import|;
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
name|om
operator|.
name|request
operator|.
name|volume
operator|.
name|acl
operator|.
name|OMVolumeSetAclRequest
import|;
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
name|OzoneManagerProtocolProtos
import|;
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
name|OzoneManagerProtocolProtos
operator|.
name|OMRequest
import|;
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
name|OzoneManagerProtocolProtos
operator|.
name|OzoneObj
operator|.
name|ObjectType
import|;
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
name|OzoneManagerProtocolProtos
operator|.
name|Status
import|;
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
name|OzoneManagerProtocolProtos
operator|.
name|Type
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
comment|/**  * Utility class used by OzoneManager HA.  */
end_comment

begin_class
DECL|class|OzoneManagerRatisUtils
specifier|public
specifier|final
class|class
name|OzoneManagerRatisUtils
block|{
DECL|method|OzoneManagerRatisUtils ()
specifier|private
name|OzoneManagerRatisUtils
parameter_list|()
block|{   }
comment|/**    * Create OMClientRequest which enacpsulates the OMRequest.    * @param omRequest    * @return OMClientRequest    * @throws IOException    */
DECL|method|createClientRequest (OMRequest omRequest)
specifier|public
specifier|static
name|OMClientRequest
name|createClientRequest
parameter_list|(
name|OMRequest
name|omRequest
parameter_list|)
block|{
name|Type
name|cmdType
init|=
name|omRequest
operator|.
name|getCmdType
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|cmdType
condition|)
block|{
case|case
name|CreateVolume
case|:
return|return
operator|new
name|OMVolumeCreateRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|SetVolumeProperty
case|:
name|boolean
name|hasQuota
init|=
name|omRequest
operator|.
name|getSetVolumePropertyRequest
argument_list|()
operator|.
name|hasQuotaInBytes
argument_list|()
decl_stmt|;
name|boolean
name|hasOwner
init|=
name|omRequest
operator|.
name|getSetVolumePropertyRequest
argument_list|()
operator|.
name|hasOwnerName
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|hasOwner
operator|||
name|hasQuota
argument_list|,
literal|"Either Quota or owner "
operator|+
literal|"should be set in the SetVolumeProperty request"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
operator|(
name|hasOwner
operator|&&
name|hasQuota
operator|)
argument_list|,
literal|"Either Quota or "
operator|+
literal|"owner should be set in the SetVolumeProperty request. Should not "
operator|+
literal|"set both"
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasQuota
condition|)
block|{
return|return
operator|new
name|OMVolumeSetQuotaRequest
argument_list|(
name|omRequest
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|OMVolumeSetOwnerRequest
argument_list|(
name|omRequest
argument_list|)
return|;
block|}
case|case
name|DeleteVolume
case|:
return|return
operator|new
name|OMVolumeDeleteRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|CreateBucket
case|:
return|return
operator|new
name|OMBucketCreateRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|DeleteBucket
case|:
return|return
operator|new
name|OMBucketDeleteRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|SetBucketProperty
case|:
return|return
operator|new
name|OMBucketSetPropertyRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|AllocateBlock
case|:
return|return
operator|new
name|OMAllocateBlockRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|CreateKey
case|:
return|return
operator|new
name|OMKeyCreateRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|CommitKey
case|:
return|return
operator|new
name|OMKeyCommitRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|DeleteKey
case|:
return|return
operator|new
name|OMKeyDeleteRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|RenameKey
case|:
return|return
operator|new
name|OMKeyRenameRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|CreateDirectory
case|:
return|return
operator|new
name|OMDirectoryCreateRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|CreateFile
case|:
return|return
operator|new
name|OMFileCreateRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|PurgeKeys
case|:
return|return
operator|new
name|OMKeyPurgeRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|CreateS3Bucket
case|:
return|return
operator|new
name|S3BucketCreateRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|DeleteS3Bucket
case|:
return|return
operator|new
name|S3BucketDeleteRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|InitiateMultiPartUpload
case|:
return|return
operator|new
name|S3InitiateMultipartUploadRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|CommitMultiPartUpload
case|:
return|return
operator|new
name|S3MultipartUploadCommitPartRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|AbortMultiPartUpload
case|:
return|return
operator|new
name|S3MultipartUploadAbortRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|CompleteMultiPartUpload
case|:
return|return
operator|new
name|S3MultipartUploadCompleteRequest
argument_list|(
name|omRequest
argument_list|)
return|;
case|case
name|AddAcl
case|:
case|case
name|RemoveAcl
case|:
case|case
name|SetAcl
case|:
return|return
name|getOMAclRequest
argument_list|(
name|omRequest
argument_list|)
return|;
default|default:
comment|// TODO: will update once all request types are implemented.
return|return
literal|null
return|;
block|}
block|}
DECL|method|getOMAclRequest (OMRequest omRequest)
specifier|private
specifier|static
name|OMClientRequest
name|getOMAclRequest
parameter_list|(
name|OMRequest
name|omRequest
parameter_list|)
block|{
name|Type
name|cmdType
init|=
name|omRequest
operator|.
name|getCmdType
argument_list|()
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|AddAcl
operator|==
name|cmdType
condition|)
block|{
name|ObjectType
name|type
init|=
name|omRequest
operator|.
name|getAddAclRequest
argument_list|()
operator|.
name|getObj
argument_list|()
operator|.
name|getResType
argument_list|()
decl_stmt|;
if|if
condition|(
name|ObjectType
operator|.
name|VOLUME
operator|==
name|type
condition|)
block|{
return|return
operator|new
name|OMVolumeAddAclRequest
argument_list|(
name|omRequest
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|ObjectType
operator|.
name|BUCKET
operator|==
name|type
condition|)
block|{
return|return
operator|new
name|OMBucketAddAclRequest
argument_list|(
name|omRequest
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|ObjectType
operator|.
name|KEY
operator|==
name|type
condition|)
block|{
return|return
operator|new
name|OMKeyAddAclRequest
argument_list|(
name|omRequest
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|Type
operator|.
name|RemoveAcl
operator|==
name|cmdType
condition|)
block|{
name|ObjectType
name|type
init|=
name|omRequest
operator|.
name|getRemoveAclRequest
argument_list|()
operator|.
name|getObj
argument_list|()
operator|.
name|getResType
argument_list|()
decl_stmt|;
if|if
condition|(
name|ObjectType
operator|.
name|VOLUME
operator|==
name|type
condition|)
block|{
return|return
operator|new
name|OMVolumeRemoveAclRequest
argument_list|(
name|omRequest
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|ObjectType
operator|.
name|BUCKET
operator|==
name|type
condition|)
block|{
return|return
operator|new
name|OMBucketRemoveAclRequest
argument_list|(
name|omRequest
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|ObjectType
operator|.
name|KEY
operator|==
name|type
condition|)
block|{
return|return
operator|new
name|OMKeyRemoveAclRequest
argument_list|(
name|omRequest
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|Type
operator|.
name|SetAcl
operator|==
name|cmdType
condition|)
block|{
name|ObjectType
name|type
init|=
name|omRequest
operator|.
name|getSetAclRequest
argument_list|()
operator|.
name|getObj
argument_list|()
operator|.
name|getResType
argument_list|()
decl_stmt|;
if|if
condition|(
name|ObjectType
operator|.
name|VOLUME
operator|==
name|type
condition|)
block|{
return|return
operator|new
name|OMVolumeSetAclRequest
argument_list|(
name|omRequest
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|ObjectType
operator|.
name|BUCKET
operator|==
name|type
condition|)
block|{
return|return
operator|new
name|OMBucketSetAclRequest
argument_list|(
name|omRequest
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|ObjectType
operator|.
name|KEY
operator|==
name|type
condition|)
block|{
return|return
operator|new
name|OMKeySetAclRequest
argument_list|(
name|omRequest
argument_list|)
return|;
block|}
block|}
comment|//TODO: handle key and prefix AddAcl
return|return
literal|null
return|;
block|}
comment|/**    * Convert exception result to {@link OzoneManagerProtocolProtos.Status}.    * @param exception    * @return OzoneManagerProtocolProtos.Status    */
DECL|method|exceptionToResponseStatus (IOException exception)
specifier|public
specifier|static
name|Status
name|exceptionToResponseStatus
parameter_list|(
name|IOException
name|exception
parameter_list|)
block|{
if|if
condition|(
name|exception
operator|instanceof
name|OMException
condition|)
block|{
return|return
name|Status
operator|.
name|values
argument_list|()
index|[
operator|(
operator|(
name|OMException
operator|)
name|exception
operator|)
operator|.
name|getResult
argument_list|()
operator|.
name|ordinal
argument_list|()
index|]
return|;
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
block|}
end_class

end_unit

