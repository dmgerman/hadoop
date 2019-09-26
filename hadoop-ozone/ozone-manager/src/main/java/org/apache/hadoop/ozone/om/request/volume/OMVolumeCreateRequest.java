begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.request.volume
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
name|request
operator|.
name|volume
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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

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
name|ratis
operator|.
name|utils
operator|.
name|OzoneManagerDoubleBufferHelper
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|audit
operator|.
name|OMAction
import|;
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
name|response
operator|.
name|volume
operator|.
name|OMVolumeCreateResponse
import|;
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
name|om
operator|.
name|OMMetrics
import|;
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
name|OMMetadataManager
import|;
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
name|OzoneManager
import|;
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
name|helpers
operator|.
name|OmVolumeArgs
import|;
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
name|response
operator|.
name|OMClientResponse
import|;
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
name|OzoneManagerProtocolProtos
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
name|OMResponse
import|;
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
name|VolumeInfo
import|;
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
name|VolumeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Time
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_ADMINISTRATORS_WILDCARD
import|;
end_import

begin_import
import|import static
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
name|lock
operator|.
name|OzoneManagerLock
operator|.
name|Resource
operator|.
name|VOLUME_LOCK
import|;
end_import

begin_import
import|import static
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
name|lock
operator|.
name|OzoneManagerLock
operator|.
name|Resource
operator|.
name|USER_LOCK
import|;
end_import

begin_comment
comment|/**  * Handles volume create request.  */
end_comment

begin_class
DECL|class|OMVolumeCreateRequest
specifier|public
class|class
name|OMVolumeCreateRequest
extends|extends
name|OMVolumeRequest
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
name|OMVolumeCreateRequest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|OMVolumeCreateRequest (OMRequest omRequest)
specifier|public
name|OMVolumeCreateRequest
parameter_list|(
name|OMRequest
name|omRequest
parameter_list|)
block|{
name|super
argument_list|(
name|omRequest
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|preExecute (OzoneManager ozoneManager)
specifier|public
name|OMRequest
name|preExecute
parameter_list|(
name|OzoneManager
name|ozoneManager
parameter_list|)
throws|throws
name|IOException
block|{
name|VolumeInfo
name|volumeInfo
init|=
name|getOmRequest
argument_list|()
operator|.
name|getCreateVolumeRequest
argument_list|()
operator|.
name|getVolumeInfo
argument_list|()
decl_stmt|;
comment|// Set creation time
name|VolumeInfo
name|updatedVolumeInfo
init|=
name|volumeInfo
operator|.
name|toBuilder
argument_list|()
operator|.
name|setCreationTime
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|getOmRequest
argument_list|()
operator|.
name|toBuilder
argument_list|()
operator|.
name|setCreateVolumeRequest
argument_list|(
name|CreateVolumeRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolumeInfo
argument_list|(
name|updatedVolumeInfo
argument_list|)
argument_list|)
operator|.
name|setUserInfo
argument_list|(
name|getUserInfo
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|validateAndUpdateCache (OzoneManager ozoneManager, long transactionLogIndex, OzoneManagerDoubleBufferHelper ozoneManagerDoubleBufferHelper)
specifier|public
name|OMClientResponse
name|validateAndUpdateCache
parameter_list|(
name|OzoneManager
name|ozoneManager
parameter_list|,
name|long
name|transactionLogIndex
parameter_list|,
name|OzoneManagerDoubleBufferHelper
name|ozoneManagerDoubleBufferHelper
parameter_list|)
block|{
name|CreateVolumeRequest
name|createVolumeRequest
init|=
name|getOmRequest
argument_list|()
operator|.
name|getCreateVolumeRequest
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|createVolumeRequest
argument_list|)
expr_stmt|;
name|VolumeInfo
name|volumeInfo
init|=
name|createVolumeRequest
operator|.
name|getVolumeInfo
argument_list|()
decl_stmt|;
name|OMMetrics
name|omMetrics
init|=
name|ozoneManager
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|omMetrics
operator|.
name|incNumVolumeCreates
argument_list|()
expr_stmt|;
name|String
name|volume
init|=
name|volumeInfo
operator|.
name|getVolume
argument_list|()
decl_stmt|;
name|String
name|owner
init|=
name|volumeInfo
operator|.
name|getOwnerName
argument_list|()
decl_stmt|;
name|OMResponse
operator|.
name|Builder
name|omResponse
init|=
name|OMResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Type
operator|.
name|CreateVolume
argument_list|)
operator|.
name|setStatus
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setSuccess
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|OMMetadataManager
name|omMetadataManager
init|=
name|ozoneManager
operator|.
name|getMetadataManager
argument_list|()
decl_stmt|;
comment|// Doing this here, so we can do protobuf conversion outside of lock.
name|boolean
name|acquiredVolumeLock
init|=
literal|false
decl_stmt|;
name|boolean
name|acquiredUserLock
init|=
literal|false
decl_stmt|;
name|IOException
name|exception
init|=
literal|null
decl_stmt|;
name|OMClientResponse
name|omClientResponse
init|=
literal|null
decl_stmt|;
name|OmVolumeArgs
name|omVolumeArgs
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|auditMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|ozAdmins
init|=
name|ozoneManager
operator|.
name|getOzoneAdmins
argument_list|()
decl_stmt|;
try|try
block|{
name|omVolumeArgs
operator|=
name|OmVolumeArgs
operator|.
name|getFromProtobuf
argument_list|(
name|volumeInfo
argument_list|)
expr_stmt|;
comment|// when you create a volume, we set both Object ID and update ID to the
comment|// same ratis transaction ID. The Object ID will never change, but update
comment|// ID will be set to transactionID each time we update the object.
name|omVolumeArgs
operator|.
name|setUpdateID
argument_list|(
name|transactionLogIndex
argument_list|)
expr_stmt|;
name|omVolumeArgs
operator|.
name|setObjectID
argument_list|(
name|transactionLogIndex
argument_list|)
expr_stmt|;
name|auditMap
operator|=
name|omVolumeArgs
operator|.
name|toAuditMap
argument_list|()
expr_stmt|;
comment|// check Acl
if|if
condition|(
name|ozoneManager
operator|.
name|getAclsEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|ozAdmins
operator|.
name|contains
argument_list|(
name|OZONE_ADMINISTRATORS_WILDCARD
argument_list|)
operator|&&
operator|!
name|ozAdmins
operator|.
name|contains
argument_list|(
name|getUserInfo
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Only admin users are authorized to create "
operator|+
literal|"Ozone volumes. User: "
operator|+
name|getUserInfo
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|,
name|OMException
operator|.
name|ResultCodes
operator|.
name|PERMISSION_DENIED
argument_list|)
throw|;
block|}
block|}
name|VolumeList
name|volumeList
init|=
literal|null
decl_stmt|;
comment|// acquire lock.
name|acquiredVolumeLock
operator|=
name|omMetadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireLock
argument_list|(
name|VOLUME_LOCK
argument_list|,
name|volume
argument_list|)
expr_stmt|;
name|acquiredUserLock
operator|=
name|omMetadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireLock
argument_list|(
name|USER_LOCK
argument_list|,
name|owner
argument_list|)
expr_stmt|;
name|String
name|dbVolumeKey
init|=
name|omMetadataManager
operator|.
name|getVolumeKey
argument_list|(
name|volume
argument_list|)
decl_stmt|;
name|OmVolumeArgs
name|dbVolumeArgs
init|=
name|omMetadataManager
operator|.
name|getVolumeTable
argument_list|()
operator|.
name|get
argument_list|(
name|dbVolumeKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|dbVolumeArgs
operator|==
literal|null
condition|)
block|{
name|String
name|dbUserKey
init|=
name|omMetadataManager
operator|.
name|getUserKey
argument_list|(
name|owner
argument_list|)
decl_stmt|;
name|volumeList
operator|=
name|omMetadataManager
operator|.
name|getUserTable
argument_list|()
operator|.
name|get
argument_list|(
name|dbUserKey
argument_list|)
expr_stmt|;
name|volumeList
operator|=
name|addVolumeToOwnerList
argument_list|(
name|volumeList
argument_list|,
name|volume
argument_list|,
name|owner
argument_list|,
name|ozoneManager
operator|.
name|getMaxUserVolumeCount
argument_list|()
argument_list|,
name|transactionLogIndex
argument_list|)
expr_stmt|;
name|createVolume
argument_list|(
name|omMetadataManager
argument_list|,
name|omVolumeArgs
argument_list|,
name|volumeList
argument_list|,
name|dbVolumeKey
argument_list|,
name|dbUserKey
argument_list|,
name|transactionLogIndex
argument_list|)
expr_stmt|;
name|omResponse
operator|.
name|setCreateVolumeResponse
argument_list|(
name|CreateVolumeResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|omClientResponse
operator|=
operator|new
name|OMVolumeCreateResponse
argument_list|(
name|omVolumeArgs
argument_list|,
name|volumeList
argument_list|,
name|omResponse
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"volume:{} successfully created"
argument_list|,
name|omVolumeArgs
operator|.
name|getVolume
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"volume:{} already exists"
argument_list|,
name|omVolumeArgs
operator|.
name|getVolume
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Volume already exists"
argument_list|,
name|OMException
operator|.
name|ResultCodes
operator|.
name|VOLUME_ALREADY_EXISTS
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|exception
operator|=
name|ex
expr_stmt|;
name|omClientResponse
operator|=
operator|new
name|OMVolumeCreateResponse
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|createErrorOMResponse
argument_list|(
name|omResponse
argument_list|,
name|exception
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|omClientResponse
operator|!=
literal|null
condition|)
block|{
name|omClientResponse
operator|.
name|setFlushFuture
argument_list|(
name|ozoneManagerDoubleBufferHelper
operator|.
name|add
argument_list|(
name|omClientResponse
argument_list|,
name|transactionLogIndex
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|acquiredUserLock
condition|)
block|{
name|omMetadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseLock
argument_list|(
name|USER_LOCK
argument_list|,
name|owner
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|acquiredVolumeLock
condition|)
block|{
name|omMetadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseLock
argument_list|(
name|VOLUME_LOCK
argument_list|,
name|volume
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Performing audit logging outside of the lock.
name|auditLog
argument_list|(
name|ozoneManager
operator|.
name|getAuditLogger
argument_list|()
argument_list|,
name|buildAuditMessage
argument_list|(
name|OMAction
operator|.
name|CREATE_VOLUME
argument_list|,
name|auditMap
argument_list|,
name|exception
argument_list|,
name|getOmRequest
argument_list|()
operator|.
name|getUserInfo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// return response after releasing lock.
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"created volume:{} for user:{}"
argument_list|,
name|volume
argument_list|,
name|owner
argument_list|)
expr_stmt|;
name|omMetrics
operator|.
name|incNumVolumes
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Volume creation failed for user:{} volume:{}"
argument_list|,
name|owner
argument_list|,
name|volume
argument_list|,
name|exception
argument_list|)
expr_stmt|;
name|omMetrics
operator|.
name|incNumVolumeCreateFails
argument_list|()
expr_stmt|;
block|}
return|return
name|omClientResponse
return|;
block|}
block|}
end_class

end_unit

