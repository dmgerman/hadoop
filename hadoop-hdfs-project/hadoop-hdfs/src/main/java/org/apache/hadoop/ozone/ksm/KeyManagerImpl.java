begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.ksm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|ksm
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
name|hdfs
operator|.
name|DFSUtil
import|;
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
name|ozone
operator|.
name|common
operator|.
name|BlockGroup
import|;
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
name|ozone
operator|.
name|ksm
operator|.
name|helpers
operator|.
name|KsmKeyLocationInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|OzoneConfiguration
import|;
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
name|ksm
operator|.
name|helpers
operator|.
name|OpenKeySession
import|;
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
name|KeyInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
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
name|scm
operator|.
name|protocol
operator|.
name|ScmBlockLocationProtocol
import|;
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
operator|.
name|BackgroundService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
operator|.
name|BatchOperation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|DBException
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
name|ArrayList
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
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|DFS_CONTAINER_RATIS_ENABLED_DEFAULT
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
name|DFS_CONTAINER_RATIS_ENABLED_KEY
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
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL_MS
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
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL_MS_DEFAULT
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
name|OZONE_BLOCK_DELETING_SERVICE_TIMEOUT
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
name|OZONE_BLOCK_DELETING_SERVICE_TIMEOUT_DEFAULT
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
name|OZONE_KEY_PREALLOCATION_MAXSIZE
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
name|OZONE_KEY_PREALLOCATION_MAXSIZE_DEFAULT
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
name|OZONE_SCM_BLOCK_SIZE_DEFAULT
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
name|OZONE_SCM_BLOCK_SIZE_IN_MB
import|;
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
name|OzoneProtos
operator|.
name|ReplicationType
import|;
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
name|OzoneProtos
operator|.
name|ReplicationFactor
import|;
end_import

begin_comment
comment|/**  * Implementation of keyManager.  */
end_comment

begin_class
DECL|class|KeyManagerImpl
specifier|public
class|class
name|KeyManagerImpl
implements|implements
name|KeyManager
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
name|KeyManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * A SCM block client, used to talk to SCM to allocate block during putKey.    */
DECL|field|scmBlockClient
specifier|private
specifier|final
name|ScmBlockLocationProtocol
name|scmBlockClient
decl_stmt|;
DECL|field|metadataManager
specifier|private
specifier|final
name|KSMMetadataManager
name|metadataManager
decl_stmt|;
DECL|field|scmBlockSize
specifier|private
specifier|final
name|long
name|scmBlockSize
decl_stmt|;
DECL|field|useRatis
specifier|private
specifier|final
name|boolean
name|useRatis
decl_stmt|;
DECL|field|keyDeletingService
specifier|private
specifier|final
name|BackgroundService
name|keyDeletingService
decl_stmt|;
DECL|field|preallocateMax
specifier|private
specifier|final
name|long
name|preallocateMax
decl_stmt|;
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|method|KeyManagerImpl (ScmBlockLocationProtocol scmBlockClient, KSMMetadataManager metadataManager, OzoneConfiguration conf)
specifier|public
name|KeyManagerImpl
parameter_list|(
name|ScmBlockLocationProtocol
name|scmBlockClient
parameter_list|,
name|KSMMetadataManager
name|metadataManager
parameter_list|,
name|OzoneConfiguration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|scmBlockClient
operator|=
name|scmBlockClient
expr_stmt|;
name|this
operator|.
name|metadataManager
operator|=
name|metadataManager
expr_stmt|;
name|this
operator|.
name|scmBlockSize
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|OZONE_SCM_BLOCK_SIZE_IN_MB
argument_list|,
name|OZONE_SCM_BLOCK_SIZE_DEFAULT
argument_list|)
operator|*
name|OzoneConsts
operator|.
name|MB
expr_stmt|;
name|this
operator|.
name|useRatis
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|DFS_CONTAINER_RATIS_ENABLED_KEY
argument_list|,
name|DFS_CONTAINER_RATIS_ENABLED_DEFAULT
argument_list|)
expr_stmt|;
name|int
name|svcInterval
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL_MS
argument_list|,
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL_MS_DEFAULT
argument_list|)
decl_stmt|;
name|long
name|serviceTimeout
init|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|OZONE_BLOCK_DELETING_SERVICE_TIMEOUT
argument_list|,
name|OZONE_BLOCK_DELETING_SERVICE_TIMEOUT_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|this
operator|.
name|preallocateMax
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|OZONE_KEY_PREALLOCATION_MAXSIZE
argument_list|,
name|OZONE_KEY_PREALLOCATION_MAXSIZE_DEFAULT
argument_list|)
expr_stmt|;
name|keyDeletingService
operator|=
operator|new
name|KeyDeletingService
argument_list|(
name|scmBlockClient
argument_list|,
name|this
argument_list|,
name|svcInterval
argument_list|,
name|serviceTimeout
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|random
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|keyDeletingService
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
block|{
name|keyDeletingService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|validateBucket (String volumeName, String bucketName)
specifier|private
name|void
name|validateBucket
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|volumeKey
init|=
name|metadataManager
operator|.
name|getVolumeKey
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bucketKey
init|=
name|metadataManager
operator|.
name|getBucketKey
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
comment|//Check if the volume exists
if|if
condition|(
name|metadataManager
operator|.
name|get
argument_list|(
name|volumeKey
argument_list|)
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"volume not found: {}"
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Volume not found"
argument_list|,
name|KSMException
operator|.
name|ResultCodes
operator|.
name|FAILED_VOLUME_NOT_FOUND
argument_list|)
throw|;
block|}
comment|//Check if bucket already exists
if|if
condition|(
name|metadataManager
operator|.
name|get
argument_list|(
name|bucketKey
argument_list|)
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"bucket not found: {}/{} "
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Bucket not found"
argument_list|,
name|KSMException
operator|.
name|ResultCodes
operator|.
name|FAILED_BUCKET_NOT_FOUND
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|allocateBlock (KsmKeyArgs args, int clientID)
specifier|public
name|KsmKeyLocationInfo
name|allocateBlock
parameter_list|(
name|KsmKeyArgs
name|args
parameter_list|,
name|int
name|clientID
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|String
name|volumeName
init|=
name|args
operator|.
name|getVolumeName
argument_list|()
decl_stmt|;
name|String
name|bucketName
init|=
name|args
operator|.
name|getBucketName
argument_list|()
decl_stmt|;
name|String
name|keyName
init|=
name|args
operator|.
name|getKeyName
argument_list|()
decl_stmt|;
name|ReplicationFactor
name|factor
init|=
name|args
operator|.
name|getFactor
argument_list|()
decl_stmt|;
name|ReplicationType
name|type
init|=
name|args
operator|.
name|getType
argument_list|()
decl_stmt|;
comment|// If user does not specify a replication strategy or
comment|// replication factor, KSM will use defaults.
if|if
condition|(
name|factor
operator|==
literal|null
condition|)
block|{
name|factor
operator|=
name|useRatis
condition|?
name|ReplicationFactor
operator|.
name|THREE
else|:
name|ReplicationFactor
operator|.
name|ONE
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|useRatis
condition|?
name|ReplicationType
operator|.
name|RATIS
else|:
name|ReplicationType
operator|.
name|STAND_ALONE
expr_stmt|;
block|}
try|try
block|{
name|validateBucket
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
name|String
name|objectKey
init|=
name|metadataManager
operator|.
name|getKeyWithDBPrefix
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|)
decl_stmt|;
name|byte
index|[]
name|openKey
init|=
name|metadataManager
operator|.
name|getOpenKeyNameBytes
argument_list|(
name|objectKey
argument_list|,
name|clientID
argument_list|)
decl_stmt|;
name|byte
index|[]
name|keyData
init|=
name|metadataManager
operator|.
name|get
argument_list|(
name|openKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyData
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Allocate block for a key not in open status in meta store "
operator|+
name|objectKey
operator|+
literal|" with ID "
operator|+
name|clientID
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Open Key not found"
argument_list|,
name|KSMException
operator|.
name|ResultCodes
operator|.
name|FAILED_KEY_NOT_FOUND
argument_list|)
throw|;
block|}
name|AllocatedBlock
name|allocatedBlock
init|=
name|scmBlockClient
operator|.
name|allocateBlock
argument_list|(
name|scmBlockSize
argument_list|,
name|type
argument_list|,
name|factor
argument_list|)
decl_stmt|;
name|KsmKeyInfo
name|keyInfo
init|=
name|KsmKeyInfo
operator|.
name|getFromProtobuf
argument_list|(
name|KeyInfo
operator|.
name|parseFrom
argument_list|(
name|keyData
argument_list|)
argument_list|)
decl_stmt|;
name|KsmKeyLocationInfo
name|info
init|=
operator|new
name|KsmKeyLocationInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setContainerName
argument_list|(
name|allocatedBlock
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
argument_list|()
argument_list|)
operator|.
name|setBlockID
argument_list|(
name|allocatedBlock
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|setShouldCreateContainer
argument_list|(
name|allocatedBlock
operator|.
name|getCreateContainer
argument_list|()
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
name|setIndex
argument_list|(
name|keyInfo
operator|.
name|getKeyLocationList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|keyInfo
operator|.
name|appendKeyLocation
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|put
argument_list|(
name|openKey
argument_list|,
name|keyInfo
operator|.
name|getProtobuf
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|openKey (KsmKeyArgs args)
specifier|public
name|OpenKeySession
name|openKey
parameter_list|(
name|KsmKeyArgs
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|String
name|volumeName
init|=
name|args
operator|.
name|getVolumeName
argument_list|()
decl_stmt|;
name|String
name|bucketName
init|=
name|args
operator|.
name|getBucketName
argument_list|()
decl_stmt|;
name|String
name|keyName
init|=
name|args
operator|.
name|getKeyName
argument_list|()
decl_stmt|;
name|ReplicationFactor
name|factor
init|=
name|args
operator|.
name|getFactor
argument_list|()
decl_stmt|;
name|ReplicationType
name|type
init|=
name|args
operator|.
name|getType
argument_list|()
decl_stmt|;
comment|// If user does not specify a replication strategy or
comment|// replication factor, KSM will use defaults.
if|if
condition|(
name|factor
operator|==
literal|null
condition|)
block|{
name|factor
operator|=
name|useRatis
condition|?
name|ReplicationFactor
operator|.
name|THREE
else|:
name|ReplicationFactor
operator|.
name|ONE
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|useRatis
condition|?
name|ReplicationType
operator|.
name|RATIS
else|:
name|ReplicationType
operator|.
name|STAND_ALONE
expr_stmt|;
block|}
try|try
block|{
name|validateBucket
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
name|long
name|requestedSize
init|=
name|Math
operator|.
name|min
argument_list|(
name|preallocateMax
argument_list|,
name|args
operator|.
name|getDataSize
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|KsmKeyLocationInfo
argument_list|>
name|locations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|objectKey
init|=
name|metadataManager
operator|.
name|getKeyWithDBPrefix
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|)
decl_stmt|;
comment|// requested size is not required but more like a optimization:
comment|// SCM looks at the requested, if it 0, no block will be allocated at
comment|// the point, if client needs more blocks, client can always call
comment|// allocateBlock. But if requested size is not 0, KSM will preallocate
comment|// some blocks and piggyback to client, to save RPC calls.
name|int
name|idx
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|requestedSize
operator|>
literal|0
condition|)
block|{
name|long
name|allocateSize
init|=
name|Math
operator|.
name|min
argument_list|(
name|scmBlockSize
argument_list|,
name|requestedSize
argument_list|)
decl_stmt|;
name|AllocatedBlock
name|allocatedBlock
init|=
name|scmBlockClient
operator|.
name|allocateBlock
argument_list|(
name|allocateSize
argument_list|,
name|type
argument_list|,
name|factor
argument_list|)
decl_stmt|;
name|KsmKeyLocationInfo
name|subKeyInfo
init|=
operator|new
name|KsmKeyLocationInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setContainerName
argument_list|(
name|allocatedBlock
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
argument_list|()
argument_list|)
operator|.
name|setBlockID
argument_list|(
name|allocatedBlock
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|setShouldCreateContainer
argument_list|(
name|allocatedBlock
operator|.
name|getCreateContainer
argument_list|()
argument_list|)
operator|.
name|setIndex
argument_list|(
name|idx
operator|++
argument_list|)
operator|.
name|setLength
argument_list|(
name|allocateSize
argument_list|)
operator|.
name|setOffset
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|locations
operator|.
name|add
argument_list|(
name|subKeyInfo
argument_list|)
expr_stmt|;
name|requestedSize
operator|-=
name|allocateSize
expr_stmt|;
block|}
name|long
name|currentTime
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
comment|// NOTE size of a key is not a hard limit on anything, it is a value that
comment|// client should expect, in terms of current size of key. If client sets a
comment|// value, then this value is used, otherwise, we allocate a single block
comment|// which is the current size, if read by the client.
name|long
name|size
init|=
name|args
operator|.
name|getDataSize
argument_list|()
operator|>=
literal|0
condition|?
name|args
operator|.
name|getDataSize
argument_list|()
else|:
name|scmBlockSize
decl_stmt|;
name|KsmKeyInfo
name|keyInfo
init|=
operator|new
name|KsmKeyInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setVolumeName
argument_list|(
name|args
operator|.
name|getVolumeName
argument_list|()
argument_list|)
operator|.
name|setBucketName
argument_list|(
name|args
operator|.
name|getBucketName
argument_list|()
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|args
operator|.
name|getKeyName
argument_list|()
argument_list|)
operator|.
name|setKsmKeyLocationInfos
argument_list|(
name|locations
argument_list|)
operator|.
name|setCreationTime
argument_list|(
name|currentTime
argument_list|)
operator|.
name|setModificationTime
argument_list|(
name|currentTime
argument_list|)
operator|.
name|setDataSize
argument_list|(
name|size
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Generate a random ID which is not already in meta db.
name|int
name|id
init|=
operator|-
literal|1
decl_stmt|;
comment|// in general this should finish in a couple times at most. putting some
comment|// arbitrary large number here to avoid dead loop.
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10000
condition|;
name|j
operator|++
control|)
block|{
name|id
operator|=
name|random
operator|.
name|nextInt
argument_list|()
expr_stmt|;
name|byte
index|[]
name|openKey
init|=
name|metadataManager
operator|.
name|getOpenKeyNameBytes
argument_list|(
name|objectKey
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|metadataManager
operator|.
name|get
argument_list|(
name|openKey
argument_list|)
operator|==
literal|null
condition|)
block|{
name|metadataManager
operator|.
name|put
argument_list|(
name|openKey
argument_list|,
name|keyInfo
operator|.
name|getProtobuf
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|id
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to find a usable id for "
operator|+
name|objectKey
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Key {} allocated in volume {} bucket {}"
argument_list|,
name|keyName
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
return|return
operator|new
name|OpenKeySession
argument_list|(
name|id
argument_list|,
name|keyInfo
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|KSMException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|ex
operator|instanceof
name|KSMException
operator|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Key open failed for volume:{} bucket:{} key:{}"
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|KSMException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|KSMException
operator|.
name|ResultCodes
operator|.
name|FAILED_KEY_ALLOCATION
argument_list|)
throw|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|commitKey (KsmKeyArgs args, int clientID)
specifier|public
name|void
name|commitKey
parameter_list|(
name|KsmKeyArgs
name|args
parameter_list|,
name|int
name|clientID
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|String
name|volumeName
init|=
name|args
operator|.
name|getVolumeName
argument_list|()
decl_stmt|;
name|String
name|bucketName
init|=
name|args
operator|.
name|getBucketName
argument_list|()
decl_stmt|;
name|String
name|keyName
init|=
name|args
operator|.
name|getKeyName
argument_list|()
decl_stmt|;
try|try
block|{
name|validateBucket
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
name|String
name|objectKey
init|=
name|metadataManager
operator|.
name|getKeyWithDBPrefix
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|)
decl_stmt|;
name|byte
index|[]
name|objectKeyBytes
init|=
name|metadataManager
operator|.
name|getDBKeyBytes
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|)
decl_stmt|;
name|byte
index|[]
name|openKey
init|=
name|metadataManager
operator|.
name|getOpenKeyNameBytes
argument_list|(
name|objectKey
argument_list|,
name|clientID
argument_list|)
decl_stmt|;
name|byte
index|[]
name|openKeyData
init|=
name|metadataManager
operator|.
name|get
argument_list|(
name|openKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|openKeyData
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Commit a key without corresponding entry "
operator|+
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|openKey
argument_list|)
argument_list|,
name|ResultCodes
operator|.
name|FAILED_KEY_NOT_FOUND
argument_list|)
throw|;
block|}
name|KsmKeyInfo
name|keyInfo
init|=
name|KsmKeyInfo
operator|.
name|getFromProtobuf
argument_list|(
name|KeyInfo
operator|.
name|parseFrom
argument_list|(
name|openKeyData
argument_list|)
argument_list|)
decl_stmt|;
name|keyInfo
operator|.
name|setDataSize
argument_list|(
name|args
operator|.
name|getDataSize
argument_list|()
argument_list|)
expr_stmt|;
name|BatchOperation
name|batch
init|=
operator|new
name|BatchOperation
argument_list|()
decl_stmt|;
name|batch
operator|.
name|delete
argument_list|(
name|openKey
argument_list|)
expr_stmt|;
name|batch
operator|.
name|put
argument_list|(
name|objectKeyBytes
argument_list|,
name|keyInfo
operator|.
name|getProtobuf
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|writeBatch
argument_list|(
name|batch
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KSMException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|ex
operator|instanceof
name|KSMException
operator|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Key commit failed for volume:{} bucket:{} key:{}"
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|KSMException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|KSMException
operator|.
name|ResultCodes
operator|.
name|FAILED_KEY_ALLOCATION
argument_list|)
throw|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|lookupKey (KsmKeyArgs args)
specifier|public
name|KsmKeyInfo
name|lookupKey
parameter_list|(
name|KsmKeyArgs
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|String
name|volumeName
init|=
name|args
operator|.
name|getVolumeName
argument_list|()
decl_stmt|;
name|String
name|bucketName
init|=
name|args
operator|.
name|getBucketName
argument_list|()
decl_stmt|;
name|String
name|keyName
init|=
name|args
operator|.
name|getKeyName
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|keyKey
init|=
name|metadataManager
operator|.
name|getDBKeyBytes
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|)
decl_stmt|;
name|byte
index|[]
name|value
init|=
name|metadataManager
operator|.
name|get
argument_list|(
name|keyKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"volume:{} bucket:{} Key:{} not found"
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Key not found"
argument_list|,
name|KSMException
operator|.
name|ResultCodes
operator|.
name|FAILED_KEY_NOT_FOUND
argument_list|)
throw|;
block|}
return|return
name|KsmKeyInfo
operator|.
name|getFromProtobuf
argument_list|(
name|KeyInfo
operator|.
name|parseFrom
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|DBException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Get key failed for volume:{} bucket:{} key:{}"
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|KSMException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|KSMException
operator|.
name|ResultCodes
operator|.
name|FAILED_KEY_NOT_FOUND
argument_list|)
throw|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|deleteKey (KsmKeyArgs args)
specifier|public
name|void
name|deleteKey
parameter_list|(
name|KsmKeyArgs
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|String
name|volumeName
init|=
name|args
operator|.
name|getVolumeName
argument_list|()
decl_stmt|;
name|String
name|bucketName
init|=
name|args
operator|.
name|getBucketName
argument_list|()
decl_stmt|;
name|String
name|keyName
init|=
name|args
operator|.
name|getKeyName
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|objectKey
init|=
name|metadataManager
operator|.
name|getDBKeyBytes
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|)
decl_stmt|;
name|byte
index|[]
name|objectValue
init|=
name|metadataManager
operator|.
name|get
argument_list|(
name|objectKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|objectValue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|KSMException
argument_list|(
literal|"Key not found"
argument_list|,
name|KSMException
operator|.
name|ResultCodes
operator|.
name|FAILED_KEY_NOT_FOUND
argument_list|)
throw|;
block|}
name|byte
index|[]
name|deletingKey
init|=
name|metadataManager
operator|.
name|getDeletedKeyName
argument_list|(
name|objectKey
argument_list|)
decl_stmt|;
name|BatchOperation
name|batch
init|=
operator|new
name|BatchOperation
argument_list|()
decl_stmt|;
name|batch
operator|.
name|put
argument_list|(
name|deletingKey
argument_list|,
name|objectValue
argument_list|)
expr_stmt|;
name|batch
operator|.
name|delete
argument_list|(
name|objectKey
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|writeBatch
argument_list|(
name|batch
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DBException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Delete key failed for volume:%s "
operator|+
literal|"bucket:%s key:%s"
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|)
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|KSMException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|,
name|ResultCodes
operator|.
name|FAILED_KEY_DELETION
argument_list|)
throw|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|listKeys (String volumeName, String bucketName, String startKey, String keyPrefix, int maxKeys)
specifier|public
name|List
argument_list|<
name|KsmKeyInfo
argument_list|>
name|listKeys
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|startKey
parameter_list|,
name|String
name|keyPrefix
parameter_list|,
name|int
name|maxKeys
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|metadataManager
operator|.
name|listKeys
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|startKey
argument_list|,
name|keyPrefix
argument_list|,
name|maxKeys
argument_list|)
return|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getPendingDeletionKeys (final int count)
specifier|public
name|List
argument_list|<
name|BlockGroup
argument_list|>
name|getPendingDeletionKeys
parameter_list|(
specifier|final
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|metadataManager
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|metadataManager
operator|.
name|getPendingDeletionKeys
argument_list|(
name|count
argument_list|)
return|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|deletePendingDeletionKey (String objectKeyName)
specifier|public
name|void
name|deletePendingDeletionKey
parameter_list|(
name|String
name|objectKeyName
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|objectKeyName
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|objectKeyName
operator|.
name|startsWith
argument_list|(
name|OzoneConsts
operator|.
name|DELETING_KEY_PREFIX
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid key name,"
operator|+
literal|" the name should be the key name with deleting prefix"
argument_list|)
throw|;
block|}
comment|// Simply removes the entry from KSM DB.
name|metadataManager
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|pendingDelKey
init|=
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|objectKeyName
argument_list|)
decl_stmt|;
name|byte
index|[]
name|delKeyValue
init|=
name|metadataManager
operator|.
name|get
argument_list|(
name|pendingDelKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|delKeyValue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete key "
operator|+
name|objectKeyName
operator|+
literal|" because it is not found in DB"
argument_list|)
throw|;
block|}
name|metadataManager
operator|.
name|delete
argument_list|(
name|pendingDelKey
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

