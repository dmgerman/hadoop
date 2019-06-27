begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.request.key
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
name|key
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
name|crypto
operator|.
name|key
operator|.
name|KeyProviderCryptoExtension
operator|.
name|EncryptedKeyVersion
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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|fs
operator|.
name|FileEncryptionInfo
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
name|client
operator|.
name|BlockID
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
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|AllocatedBlock
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ExcludeList
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
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
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
name|Server
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
name|ScmClient
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
name|BucketEncryptionKeyInfo
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
name|OmBucketInfo
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
name|OmKeyLocationInfo
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
name|OzoneBlockTokenSecretManager
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
name|security
operator|.
name|SecurityUtil
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
name|security
operator|.
name|UserGroupInformation
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
name|security
operator|.
name|GeneralSecurityException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|exceptions
operator|.
name|OMException
operator|.
name|ResultCodes
operator|.
name|BUCKET_NOT_FOUND
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
name|exceptions
operator|.
name|OMException
operator|.
name|ResultCodes
operator|.
name|VOLUME_NOT_FOUND
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
name|util
operator|.
name|Time
operator|.
name|monotonicNow
import|;
end_import

begin_comment
comment|/**  * Interface for key write requests.  */
end_comment

begin_interface
DECL|interface|OMKeyRequest
specifier|public
interface|interface
name|OMKeyRequest
block|{
DECL|field|LOG
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OMKeyRequest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * This methods avoids multiple rpc calls to SCM by allocating multiple blocks    * in one rpc call.    * @throws IOException    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"parameternumber"
argument_list|)
DECL|method|allocateBlock (ScmClient scmClient, OzoneBlockTokenSecretManager secretManager, HddsProtos.ReplicationType replicationType, HddsProtos.ReplicationFactor replicationFactor, ExcludeList excludeList, long requestedSize, long scmBlockSize, int preallocateBlocksMax, boolean grpcBlockTokenEnabled, String omID)
specifier|default
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|allocateBlock
parameter_list|(
name|ScmClient
name|scmClient
parameter_list|,
name|OzoneBlockTokenSecretManager
name|secretManager
parameter_list|,
name|HddsProtos
operator|.
name|ReplicationType
name|replicationType
parameter_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
name|replicationFactor
parameter_list|,
name|ExcludeList
name|excludeList
parameter_list|,
name|long
name|requestedSize
parameter_list|,
name|long
name|scmBlockSize
parameter_list|,
name|int
name|preallocateBlocksMax
parameter_list|,
name|boolean
name|grpcBlockTokenEnabled
parameter_list|,
name|String
name|omID
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numBlocks
init|=
name|Math
operator|.
name|min
argument_list|(
call|(
name|int
call|)
argument_list|(
operator|(
name|requestedSize
operator|-
literal|1
operator|)
operator|/
name|scmBlockSize
operator|+
literal|1
argument_list|)
argument_list|,
name|preallocateBlocksMax
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|locationInfos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numBlocks
argument_list|)
decl_stmt|;
name|String
name|remoteUser
init|=
name|getRemoteUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AllocatedBlock
argument_list|>
name|allocatedBlocks
decl_stmt|;
try|try
block|{
name|allocatedBlocks
operator|=
name|scmClient
operator|.
name|getBlockClient
argument_list|()
operator|.
name|allocateBlock
argument_list|(
name|scmBlockSize
argument_list|,
name|numBlocks
argument_list|,
name|replicationType
argument_list|,
name|replicationFactor
argument_list|,
name|omID
argument_list|,
name|excludeList
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SCMException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|.
name|getResult
argument_list|()
operator|.
name|equals
argument_list|(
name|SCMException
operator|.
name|ResultCodes
operator|.
name|SAFE_MODE_EXCEPTION
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|OMException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|OMException
operator|.
name|ResultCodes
operator|.
name|SCM_IN_SAFE_MODE
argument_list|)
throw|;
block|}
throw|throw
name|ex
throw|;
block|}
for|for
control|(
name|AllocatedBlock
name|allocatedBlock
range|:
name|allocatedBlocks
control|)
block|{
name|OmKeyLocationInfo
operator|.
name|Builder
name|builder
init|=
operator|new
name|OmKeyLocationInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setBlockID
argument_list|(
operator|new
name|BlockID
argument_list|(
name|allocatedBlock
operator|.
name|getBlockID
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setLength
argument_list|(
name|scmBlockSize
argument_list|)
operator|.
name|setOffset
argument_list|(
literal|0
argument_list|)
operator|.
name|setPipeline
argument_list|(
name|allocatedBlock
operator|.
name|getPipeline
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|grpcBlockTokenEnabled
condition|)
block|{
name|builder
operator|.
name|setToken
argument_list|(
name|secretManager
operator|.
name|generateToken
argument_list|(
name|remoteUser
argument_list|,
name|allocatedBlock
operator|.
name|getBlockID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|getAclForUser
argument_list|(
name|remoteUser
argument_list|)
argument_list|,
name|scmBlockSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|locationInfos
operator|.
name|add
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|locationInfos
return|;
block|}
comment|/* Optimize ugi lookup for RPC operations to avoid a trip through    * UGI.getCurrentUser which is synch'ed.    */
DECL|method|getRemoteUser ()
specifier|default
name|UserGroupInformation
name|getRemoteUser
parameter_list|()
throws|throws
name|IOException
block|{
name|UserGroupInformation
name|ugi
init|=
name|Server
operator|.
name|getRemoteUser
argument_list|()
decl_stmt|;
return|return
operator|(
name|ugi
operator|!=
literal|null
operator|)
condition|?
name|ugi
else|:
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
return|;
block|}
comment|/**    * Return acl for user.    * @param user    *    * */
specifier|default
name|EnumSet
argument_list|<
name|HddsProtos
operator|.
name|BlockTokenSecretProto
operator|.
name|AccessModeProto
argument_list|>
DECL|method|getAclForUser (String user)
name|getAclForUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
comment|// TODO: Return correct acl for user.
return|return
name|EnumSet
operator|.
name|allOf
argument_list|(
name|HddsProtos
operator|.
name|BlockTokenSecretProto
operator|.
name|AccessModeProto
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**    * Validate bucket and volume exists or not.    * @param omMetadataManager    * @param volumeName    * @param bucketName    * @throws IOException    */
DECL|method|validateBucketAndVolume (OMMetadataManager omMetadataManager, String volumeName, String bucketName)
specifier|default
name|void
name|validateBucketAndVolume
parameter_list|(
name|OMMetadataManager
name|omMetadataManager
parameter_list|,
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|bucketKey
init|=
name|omMetadataManager
operator|.
name|getBucketKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
comment|// Check if bucket exists
if|if
condition|(
operator|!
name|omMetadataManager
operator|.
name|getBucketTable
argument_list|()
operator|.
name|isExist
argument_list|(
name|bucketKey
argument_list|)
condition|)
block|{
name|String
name|volumeKey
init|=
name|omMetadataManager
operator|.
name|getVolumeKey
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
comment|// If the volume also does not exist, we should throw volume not found
comment|// exception
if|if
condition|(
operator|!
name|omMetadataManager
operator|.
name|getVolumeTable
argument_list|()
operator|.
name|isExist
argument_list|(
name|volumeKey
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Volume not found "
operator|+
name|volumeName
argument_list|,
name|VOLUME_NOT_FOUND
argument_list|)
throw|;
block|}
comment|// if the volume exists but bucket does not exist, throw bucket not found
comment|// exception
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Bucket not found "
operator|+
name|bucketName
argument_list|,
name|BUCKET_NOT_FOUND
argument_list|)
throw|;
block|}
block|}
DECL|method|getFileEncryptionInfo ( OzoneManager ozoneManager, OmBucketInfo bucketInfo)
specifier|default
name|FileEncryptionInfo
name|getFileEncryptionInfo
parameter_list|(
name|OzoneManager
name|ozoneManager
parameter_list|,
name|OmBucketInfo
name|bucketInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|FileEncryptionInfo
name|encInfo
init|=
literal|null
decl_stmt|;
name|BucketEncryptionKeyInfo
name|ezInfo
init|=
name|bucketInfo
operator|.
name|getEncryptionKeyInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|ezInfo
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|ozoneManager
operator|.
name|getKmsProvider
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|"Invalid KMS provider, check configuration "
operator|+
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_KEY_PROVIDER_PATH
argument_list|,
name|OMException
operator|.
name|ResultCodes
operator|.
name|INVALID_KMS_PROVIDER
argument_list|)
throw|;
block|}
specifier|final
name|String
name|ezKeyName
init|=
name|ezInfo
operator|.
name|getKeyName
argument_list|()
decl_stmt|;
name|EncryptedKeyVersion
name|edek
init|=
name|generateEDEK
argument_list|(
name|ozoneManager
argument_list|,
name|ezKeyName
argument_list|)
decl_stmt|;
name|encInfo
operator|=
operator|new
name|FileEncryptionInfo
argument_list|(
name|ezInfo
operator|.
name|getSuite
argument_list|()
argument_list|,
name|ezInfo
operator|.
name|getVersion
argument_list|()
argument_list|,
name|edek
operator|.
name|getEncryptedKeyVersion
argument_list|()
operator|.
name|getMaterial
argument_list|()
argument_list|,
name|edek
operator|.
name|getEncryptedKeyIv
argument_list|()
argument_list|,
name|ezKeyName
argument_list|,
name|edek
operator|.
name|getEncryptionKeyVersionName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|encInfo
return|;
block|}
DECL|method|generateEDEK (OzoneManager ozoneManager, String ezKeyName)
specifier|default
name|EncryptedKeyVersion
name|generateEDEK
parameter_list|(
name|OzoneManager
name|ozoneManager
parameter_list|,
name|String
name|ezKeyName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|ezKeyName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
name|generateEDEKStartTime
init|=
name|monotonicNow
argument_list|()
decl_stmt|;
name|EncryptedKeyVersion
name|edek
init|=
name|SecurityUtil
operator|.
name|doAsLoginUser
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|EncryptedKeyVersion
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|EncryptedKeyVersion
name|run
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|ozoneManager
operator|.
name|getKmsProvider
argument_list|()
operator|.
name|generateEncryptedKey
argument_list|(
name|ezKeyName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|GeneralSecurityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|long
name|generateEDEKTime
init|=
name|monotonicNow
argument_list|()
operator|-
name|generateEDEKStartTime
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"generateEDEK takes {} ms"
argument_list|,
name|generateEDEKTime
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|edek
argument_list|)
expr_stmt|;
return|return
name|edek
return|;
block|}
block|}
end_interface

end_unit

