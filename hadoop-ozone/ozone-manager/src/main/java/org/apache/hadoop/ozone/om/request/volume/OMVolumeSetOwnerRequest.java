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
name|Map
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Optional
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
name|audit
operator|.
name|AuditLogger
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
name|OzoneConsts
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
name|om
operator|.
name|response
operator|.
name|volume
operator|.
name|OMVolumeSetOwnerResponse
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
name|OzoneManagerProtocolProtos
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
name|security
operator|.
name|acl
operator|.
name|IAccessAuthorizer
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
name|security
operator|.
name|acl
operator|.
name|OzoneObj
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
name|hdds
operator|.
name|utils
operator|.
name|db
operator|.
name|cache
operator|.
name|CacheKey
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
name|hdds
operator|.
name|utils
operator|.
name|db
operator|.
name|cache
operator|.
name|CacheValue
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

begin_comment
comment|/**  * Handle set owner request for volume.  */
end_comment

begin_class
DECL|class|OMVolumeSetOwnerRequest
specifier|public
class|class
name|OMVolumeSetOwnerRequest
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
name|OMVolumeSetOwnerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|OMVolumeSetOwnerRequest (OMRequest omRequest)
specifier|public
name|OMVolumeSetOwnerRequest
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
name|SetVolumePropertyRequest
name|setVolumePropertyRequest
init|=
name|getOmRequest
argument_list|()
operator|.
name|getSetVolumePropertyRequest
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|setVolumePropertyRequest
argument_list|)
expr_stmt|;
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
name|SetVolumeProperty
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
comment|// In production this will never happen, this request will be called only
comment|// when we have ownerName in setVolumePropertyRequest.
if|if
condition|(
operator|!
name|setVolumePropertyRequest
operator|.
name|hasOwnerName
argument_list|()
condition|)
block|{
name|omResponse
operator|.
name|setStatus
argument_list|(
name|OzoneManagerProtocolProtos
operator|.
name|Status
operator|.
name|INVALID_REQUEST
argument_list|)
operator|.
name|setSuccess
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
operator|new
name|OMVolumeSetOwnerResponse
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|omResponse
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
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
name|incNumVolumeUpdates
argument_list|()
expr_stmt|;
name|String
name|volume
init|=
name|setVolumePropertyRequest
operator|.
name|getVolumeName
argument_list|()
decl_stmt|;
name|String
name|newOwner
init|=
name|setVolumePropertyRequest
operator|.
name|getOwnerName
argument_list|()
decl_stmt|;
name|AuditLogger
name|auditLogger
init|=
name|ozoneManager
operator|.
name|getAuditLogger
argument_list|()
decl_stmt|;
name|OzoneManagerProtocolProtos
operator|.
name|UserInfo
name|userInfo
init|=
name|getOmRequest
argument_list|()
operator|.
name|getUserInfo
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|auditMap
init|=
name|buildVolumeAuditMap
argument_list|(
name|volume
argument_list|)
decl_stmt|;
name|auditMap
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|OWNER
argument_list|,
name|newOwner
argument_list|)
expr_stmt|;
name|boolean
name|acquiredUserLocks
init|=
literal|false
decl_stmt|;
name|boolean
name|acquiredVolumeLock
init|=
literal|false
decl_stmt|;
name|IOException
name|exception
init|=
literal|null
decl_stmt|;
name|OMMetadataManager
name|omMetadataManager
init|=
name|ozoneManager
operator|.
name|getMetadataManager
argument_list|()
decl_stmt|;
name|String
name|oldOwner
init|=
literal|null
decl_stmt|;
name|OMClientResponse
name|omClientResponse
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// check Acl
if|if
condition|(
name|ozoneManager
operator|.
name|getAclsEnabled
argument_list|()
condition|)
block|{
name|checkAcls
argument_list|(
name|ozoneManager
argument_list|,
name|OzoneObj
operator|.
name|ResourceType
operator|.
name|VOLUME
argument_list|,
name|OzoneObj
operator|.
name|StoreType
operator|.
name|OZONE
argument_list|,
name|IAccessAuthorizer
operator|.
name|ACLType
operator|.
name|WRITE_ACL
argument_list|,
name|volume
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|long
name|maxUserVolumeCount
init|=
name|ozoneManager
operator|.
name|getMaxUserVolumeCount
argument_list|()
decl_stmt|;
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
name|OzoneManagerProtocolProtos
operator|.
name|UserVolumeInfo
name|oldOwnerVolumeList
init|=
literal|null
decl_stmt|;
name|OzoneManagerProtocolProtos
operator|.
name|UserVolumeInfo
name|newOwnerVolumeList
init|=
literal|null
decl_stmt|;
name|OmVolumeArgs
name|omVolumeArgs
init|=
literal|null
decl_stmt|;
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
name|omVolumeArgs
operator|=
name|omMetadataManager
operator|.
name|getVolumeTable
argument_list|()
operator|.
name|get
argument_list|(
name|dbVolumeKey
argument_list|)
expr_stmt|;
if|if
condition|(
name|omVolumeArgs
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Changing volume ownership failed for user:{} volume:{}"
argument_list|,
name|newOwner
argument_list|,
name|volume
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Volume "
operator|+
name|volume
operator|+
literal|" is not found"
argument_list|,
name|OMException
operator|.
name|ResultCodes
operator|.
name|VOLUME_NOT_FOUND
argument_list|)
throw|;
block|}
name|oldOwner
operator|=
name|omVolumeArgs
operator|.
name|getOwnerName
argument_list|()
expr_stmt|;
name|acquiredUserLocks
operator|=
name|omMetadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireMultiUserLock
argument_list|(
name|newOwner
argument_list|,
name|oldOwner
argument_list|)
expr_stmt|;
name|oldOwnerVolumeList
operator|=
name|omMetadataManager
operator|.
name|getUserTable
argument_list|()
operator|.
name|get
argument_list|(
name|oldOwner
argument_list|)
expr_stmt|;
name|oldOwnerVolumeList
operator|=
name|delVolumeFromOwnerList
argument_list|(
name|oldOwnerVolumeList
argument_list|,
name|volume
argument_list|,
name|oldOwner
argument_list|,
name|transactionLogIndex
argument_list|)
expr_stmt|;
name|newOwnerVolumeList
operator|=
name|omMetadataManager
operator|.
name|getUserTable
argument_list|()
operator|.
name|get
argument_list|(
name|newOwner
argument_list|)
expr_stmt|;
name|newOwnerVolumeList
operator|=
name|addVolumeToOwnerList
argument_list|(
name|newOwnerVolumeList
argument_list|,
name|volume
argument_list|,
name|newOwner
argument_list|,
name|maxUserVolumeCount
argument_list|,
name|transactionLogIndex
argument_list|)
expr_stmt|;
comment|// Set owner with new owner name.
name|omVolumeArgs
operator|.
name|setOwnerName
argument_list|(
name|newOwner
argument_list|)
expr_stmt|;
name|omVolumeArgs
operator|.
name|setUpdateID
argument_list|(
name|transactionLogIndex
argument_list|)
expr_stmt|;
comment|// Update cache.
name|omMetadataManager
operator|.
name|getUserTable
argument_list|()
operator|.
name|addCacheEntry
argument_list|(
operator|new
name|CacheKey
argument_list|<>
argument_list|(
name|omMetadataManager
operator|.
name|getUserKey
argument_list|(
name|newOwner
argument_list|)
argument_list|)
argument_list|,
operator|new
name|CacheValue
argument_list|<>
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|newOwnerVolumeList
argument_list|)
argument_list|,
name|transactionLogIndex
argument_list|)
argument_list|)
expr_stmt|;
name|omMetadataManager
operator|.
name|getUserTable
argument_list|()
operator|.
name|addCacheEntry
argument_list|(
operator|new
name|CacheKey
argument_list|<>
argument_list|(
name|omMetadataManager
operator|.
name|getUserKey
argument_list|(
name|oldOwner
argument_list|)
argument_list|)
argument_list|,
operator|new
name|CacheValue
argument_list|<>
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|oldOwnerVolumeList
argument_list|)
argument_list|,
name|transactionLogIndex
argument_list|)
argument_list|)
expr_stmt|;
name|omMetadataManager
operator|.
name|getVolumeTable
argument_list|()
operator|.
name|addCacheEntry
argument_list|(
operator|new
name|CacheKey
argument_list|<>
argument_list|(
name|dbVolumeKey
argument_list|)
argument_list|,
operator|new
name|CacheValue
argument_list|<>
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|omVolumeArgs
argument_list|)
argument_list|,
name|transactionLogIndex
argument_list|)
argument_list|)
expr_stmt|;
name|omResponse
operator|.
name|setSetVolumePropertyResponse
argument_list|(
name|SetVolumePropertyResponse
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
name|OMVolumeSetOwnerResponse
argument_list|(
name|oldOwner
argument_list|,
name|oldOwnerVolumeList
argument_list|,
name|newOwnerVolumeList
argument_list|,
name|omVolumeArgs
argument_list|,
name|omResponse
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
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
name|OMVolumeSetOwnerResponse
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
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
name|acquiredUserLocks
condition|)
block|{
name|omMetadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseMultiUserLock
argument_list|(
name|newOwner
argument_list|,
name|oldOwner
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
name|auditLogger
argument_list|,
name|buildAuditMessage
argument_list|(
name|OMAction
operator|.
name|SET_OWNER
argument_list|,
name|auditMap
argument_list|,
name|exception
argument_list|,
name|userInfo
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
name|debug
argument_list|(
literal|"Successfully changed Owner of Volume {} from {} -> {}"
argument_list|,
name|volume
argument_list|,
name|oldOwner
argument_list|,
name|newOwner
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Changing volume ownership failed for user:{} volume:{}"
argument_list|,
name|newOwner
argument_list|,
name|volume
argument_list|,
name|exception
argument_list|)
expr_stmt|;
name|omMetrics
operator|.
name|incNumVolumeUpdateFails
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

