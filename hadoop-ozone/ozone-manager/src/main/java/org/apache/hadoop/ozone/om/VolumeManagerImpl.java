begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
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
name|hdds
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|OzoneAclInfo
import|;
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
operator|.
name|RocksDBStore
import|;
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
name|db
operator|.
name|BatchOperation
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
name|OMConfigKeys
operator|.
name|OZONE_OM_USER_MAX_VOLUME
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
name|OMConfigKeys
operator|.
name|OZONE_OM_USER_MAX_VOLUME_DEFAULT
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
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|RocksDBException
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

begin_comment
comment|/**  * OM volume management code.  */
end_comment

begin_class
DECL|class|VolumeManagerImpl
specifier|public
class|class
name|VolumeManagerImpl
implements|implements
name|VolumeManager
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
name|VolumeManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|metadataManager
specifier|private
specifier|final
name|OMMetadataManager
name|metadataManager
decl_stmt|;
DECL|field|maxUserVolumeCount
specifier|private
specifier|final
name|int
name|maxUserVolumeCount
decl_stmt|;
comment|/**    * Constructor.    * @param conf - Ozone configuration.    * @throws IOException    */
DECL|method|VolumeManagerImpl (OMMetadataManager metadataManager, OzoneConfiguration conf)
specifier|public
name|VolumeManagerImpl
parameter_list|(
name|OMMetadataManager
name|metadataManager
parameter_list|,
name|OzoneConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|metadataManager
operator|=
name|metadataManager
expr_stmt|;
name|this
operator|.
name|maxUserVolumeCount
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_OM_USER_MAX_VOLUME
argument_list|,
name|OZONE_OM_USER_MAX_VOLUME_DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|// Helpers to add and delete volume from user list
DECL|method|addVolumeToOwnerList (String volume, String owner, BatchOperation batchOperation)
specifier|private
name|void
name|addVolumeToOwnerList
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|owner
parameter_list|,
name|BatchOperation
name|batchOperation
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Get the volume list
name|byte
index|[]
name|dbUserKey
init|=
name|metadataManager
operator|.
name|getUserKey
argument_list|(
name|owner
argument_list|)
decl_stmt|;
name|byte
index|[]
name|volumeList
init|=
name|metadataManager
operator|.
name|getUserTable
argument_list|()
operator|.
name|get
argument_list|(
name|dbUserKey
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|prevVolList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|volumeList
operator|!=
literal|null
condition|)
block|{
name|VolumeList
name|vlist
init|=
name|VolumeList
operator|.
name|parseFrom
argument_list|(
name|volumeList
argument_list|)
decl_stmt|;
name|prevVolList
operator|.
name|addAll
argument_list|(
name|vlist
operator|.
name|getVolumeNamesList
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Check the volume count
if|if
condition|(
name|prevVolList
operator|.
name|size
argument_list|()
operator|>=
name|maxUserVolumeCount
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Too many volumes for user:{}"
argument_list|,
name|owner
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|OMException
argument_list|(
name|ResultCodes
operator|.
name|FAILED_TOO_MANY_USER_VOLUMES
argument_list|)
throw|;
block|}
comment|// Add the new volume to the list
name|prevVolList
operator|.
name|add
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|VolumeList
name|newVolList
init|=
name|VolumeList
operator|.
name|newBuilder
argument_list|()
operator|.
name|addAllVolumeNames
argument_list|(
name|prevVolList
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|metadataManager
operator|.
name|getUserTable
argument_list|()
operator|.
name|putWithBatch
argument_list|(
name|batchOperation
argument_list|,
name|dbUserKey
argument_list|,
name|newVolList
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|delVolumeFromOwnerList (String volume, String owner, BatchOperation batch)
specifier|private
name|void
name|delVolumeFromOwnerList
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|owner
parameter_list|,
name|BatchOperation
name|batch
parameter_list|)
throws|throws
name|RocksDBException
throws|,
name|IOException
block|{
comment|// Get the volume list
name|byte
index|[]
name|dbUserKey
init|=
name|metadataManager
operator|.
name|getUserKey
argument_list|(
name|owner
argument_list|)
decl_stmt|;
name|byte
index|[]
name|volumeList
init|=
name|metadataManager
operator|.
name|getUserTable
argument_list|()
operator|.
name|get
argument_list|(
name|dbUserKey
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|prevVolList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|volumeList
operator|!=
literal|null
condition|)
block|{
name|VolumeList
name|vlist
init|=
name|VolumeList
operator|.
name|parseFrom
argument_list|(
name|volumeList
argument_list|)
decl_stmt|;
name|prevVolList
operator|.
name|addAll
argument_list|(
name|vlist
operator|.
name|getVolumeNamesList
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
literal|"volume:{} not found for user:{}"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|OMException
argument_list|(
name|ResultCodes
operator|.
name|FAILED_USER_NOT_FOUND
argument_list|)
throw|;
block|}
comment|// Remove the volume from the list
name|prevVolList
operator|.
name|remove
argument_list|(
name|volume
argument_list|)
expr_stmt|;
if|if
condition|(
name|prevVolList
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|metadataManager
operator|.
name|getUserTable
argument_list|()
operator|.
name|deleteWithBatch
argument_list|(
name|batch
argument_list|,
name|dbUserKey
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|VolumeList
name|newVolList
init|=
name|VolumeList
operator|.
name|newBuilder
argument_list|()
operator|.
name|addAllVolumeNames
argument_list|(
name|prevVolList
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|metadataManager
operator|.
name|getUserTable
argument_list|()
operator|.
name|putWithBatch
argument_list|(
name|batch
argument_list|,
name|dbUserKey
argument_list|,
name|newVolList
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Creates a volume.    * @param args - OmVolumeArgs.    */
annotation|@
name|Override
DECL|method|createVolume (OmVolumeArgs args)
specifier|public
name|void
name|createVolume
parameter_list|(
name|OmVolumeArgs
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
name|getLock
argument_list|()
operator|.
name|acquireUserLock
argument_list|(
name|args
operator|.
name|getOwnerName
argument_list|()
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireVolumeLock
argument_list|(
name|args
operator|.
name|getVolume
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|dbVolumeKey
init|=
name|metadataManager
operator|.
name|getVolumeKey
argument_list|(
name|args
operator|.
name|getVolume
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|volumeInfo
init|=
name|metadataManager
operator|.
name|getVolumeTable
argument_list|()
operator|.
name|get
argument_list|(
name|dbVolumeKey
argument_list|)
decl_stmt|;
comment|// Check of the volume already exists
if|if
condition|(
name|volumeInfo
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"volume:{} already exists"
argument_list|,
name|args
operator|.
name|getVolume
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|OMException
argument_list|(
name|ResultCodes
operator|.
name|FAILED_VOLUME_ALREADY_EXISTS
argument_list|)
throw|;
block|}
try|try
init|(
name|BatchOperation
name|batch
init|=
name|metadataManager
operator|.
name|getStore
argument_list|()
operator|.
name|initBatchOperation
argument_list|()
init|)
block|{
comment|// Write the vol info
name|List
argument_list|<
name|HddsProtos
operator|.
name|KeyValue
argument_list|>
name|metadataList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|args
operator|.
name|getKeyValueMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|metadataList
operator|.
name|add
argument_list|(
name|HddsProtos
operator|.
name|KeyValue
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKey
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|setValue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|OzoneAclInfo
argument_list|>
name|aclList
init|=
name|args
operator|.
name|getAclMap
argument_list|()
operator|.
name|ozoneAclGetProtobuf
argument_list|()
decl_stmt|;
name|VolumeInfo
name|newVolumeInfo
init|=
name|VolumeInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setAdminName
argument_list|(
name|args
operator|.
name|getAdminName
argument_list|()
argument_list|)
operator|.
name|setOwnerName
argument_list|(
name|args
operator|.
name|getOwnerName
argument_list|()
argument_list|)
operator|.
name|setVolume
argument_list|(
name|args
operator|.
name|getVolume
argument_list|()
argument_list|)
operator|.
name|setQuotaInBytes
argument_list|(
name|args
operator|.
name|getQuotaInBytes
argument_list|()
argument_list|)
operator|.
name|addAllMetadata
argument_list|(
name|metadataList
argument_list|)
operator|.
name|addAllVolumeAcls
argument_list|(
name|aclList
argument_list|)
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
name|metadataManager
operator|.
name|getVolumeTable
argument_list|()
operator|.
name|putWithBatch
argument_list|(
name|batch
argument_list|,
name|dbVolumeKey
argument_list|,
name|newVolumeInfo
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add volume to user list
name|addVolumeToOwnerList
argument_list|(
name|args
operator|.
name|getVolume
argument_list|()
argument_list|,
name|args
operator|.
name|getOwnerName
argument_list|()
argument_list|,
name|batch
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|getStore
argument_list|()
operator|.
name|commitBatchOperation
argument_list|(
name|batch
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"created volume:{} user:{}"
argument_list|,
name|args
operator|.
name|getVolume
argument_list|()
argument_list|,
name|args
operator|.
name|getOwnerName
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
if|if
condition|(
operator|!
operator|(
name|ex
operator|instanceof
name|OMException
operator|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Volume creation failed for user:{} volume:{}"
argument_list|,
name|args
operator|.
name|getOwnerName
argument_list|()
argument_list|,
name|args
operator|.
name|getVolume
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|(
name|IOException
operator|)
name|ex
throw|;
block|}
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseVolumeLock
argument_list|(
name|args
operator|.
name|getVolume
argument_list|()
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseUserLock
argument_list|(
name|args
operator|.
name|getOwnerName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Changes the owner of a volume.    *    * @param volume - Name of the volume.    * @param owner - Name of the owner.    * @throws IOException    */
annotation|@
name|Override
DECL|method|setOwner (String volume, String owner)
specifier|public
name|void
name|setOwner
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|owner
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireUserLock
argument_list|(
name|owner
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireVolumeLock
argument_list|(
name|volume
argument_list|)
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|dbVolumeKey
init|=
name|metadataManager
operator|.
name|getVolumeKey
argument_list|(
name|volume
argument_list|)
decl_stmt|;
name|byte
index|[]
name|volInfo
init|=
name|metadataManager
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
name|volInfo
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
name|owner
argument_list|,
name|volume
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|OMException
argument_list|(
name|ResultCodes
operator|.
name|FAILED_VOLUME_NOT_FOUND
argument_list|)
throw|;
block|}
name|VolumeInfo
name|volumeInfo
init|=
name|VolumeInfo
operator|.
name|parseFrom
argument_list|(
name|volInfo
argument_list|)
decl_stmt|;
name|OmVolumeArgs
name|volumeArgs
init|=
name|OmVolumeArgs
operator|.
name|getFromProtobuf
argument_list|(
name|volumeInfo
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|volume
operator|.
name|equals
argument_list|(
name|volumeInfo
operator|.
name|getVolume
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|BatchOperation
name|batch
init|=
name|metadataManager
operator|.
name|getStore
argument_list|()
operator|.
name|initBatchOperation
argument_list|()
init|)
block|{
name|delVolumeFromOwnerList
argument_list|(
name|volume
argument_list|,
name|volumeArgs
operator|.
name|getOwnerName
argument_list|()
argument_list|,
name|batch
argument_list|)
expr_stmt|;
name|addVolumeToOwnerList
argument_list|(
name|volume
argument_list|,
name|owner
argument_list|,
name|batch
argument_list|)
expr_stmt|;
name|OmVolumeArgs
name|newVolumeArgs
init|=
name|OmVolumeArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolume
argument_list|(
name|volumeArgs
operator|.
name|getVolume
argument_list|()
argument_list|)
operator|.
name|setAdminName
argument_list|(
name|volumeArgs
operator|.
name|getAdminName
argument_list|()
argument_list|)
operator|.
name|setOwnerName
argument_list|(
name|owner
argument_list|)
operator|.
name|setQuotaInBytes
argument_list|(
name|volumeArgs
operator|.
name|getQuotaInBytes
argument_list|()
argument_list|)
operator|.
name|setCreationTime
argument_list|(
name|volumeArgs
operator|.
name|getCreationTime
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|VolumeInfo
name|newVolumeInfo
init|=
name|newVolumeArgs
operator|.
name|getProtobuf
argument_list|()
decl_stmt|;
name|metadataManager
operator|.
name|getVolumeTable
argument_list|()
operator|.
name|putWithBatch
argument_list|(
name|batch
argument_list|,
name|dbVolumeKey
argument_list|,
name|newVolumeInfo
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|getStore
argument_list|()
operator|.
name|commitBatchOperation
argument_list|(
name|batch
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RocksDBException
decl||
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
name|OMException
operator|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Changing volume ownership failed for user:{} volume:{}"
argument_list|,
name|owner
argument_list|,
name|volume
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ex
operator|instanceof
name|RocksDBException
condition|)
block|{
throw|throw
name|RocksDBStore
operator|.
name|toIOException
argument_list|(
literal|"Volume creation failed."
argument_list|,
operator|(
name|RocksDBException
operator|)
name|ex
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|(
name|IOException
operator|)
name|ex
throw|;
block|}
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseVolumeLock
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseUserLock
argument_list|(
name|owner
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Changes the Quota on a volume.    *    * @param volume - Name of the volume.    * @param quota - Quota in bytes.    * @throws IOException    */
annotation|@
name|Override
DECL|method|setQuota (String volume, long quota)
specifier|public
name|void
name|setQuota
parameter_list|(
name|String
name|volume
parameter_list|,
name|long
name|quota
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireVolumeLock
argument_list|(
name|volume
argument_list|)
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|dbVolumeKey
init|=
name|metadataManager
operator|.
name|getVolumeKey
argument_list|(
name|volume
argument_list|)
decl_stmt|;
name|byte
index|[]
name|volInfo
init|=
name|metadataManager
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
name|volInfo
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"volume:{} does not exist"
argument_list|,
name|volume
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|OMException
argument_list|(
name|ResultCodes
operator|.
name|FAILED_VOLUME_NOT_FOUND
argument_list|)
throw|;
block|}
name|VolumeInfo
name|volumeInfo
init|=
name|VolumeInfo
operator|.
name|parseFrom
argument_list|(
name|volInfo
argument_list|)
decl_stmt|;
name|OmVolumeArgs
name|volumeArgs
init|=
name|OmVolumeArgs
operator|.
name|getFromProtobuf
argument_list|(
name|volumeInfo
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|volume
operator|.
name|equals
argument_list|(
name|volumeInfo
operator|.
name|getVolume
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|OmVolumeArgs
name|newVolumeArgs
init|=
name|OmVolumeArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setVolume
argument_list|(
name|volumeArgs
operator|.
name|getVolume
argument_list|()
argument_list|)
operator|.
name|setAdminName
argument_list|(
name|volumeArgs
operator|.
name|getAdminName
argument_list|()
argument_list|)
operator|.
name|setOwnerName
argument_list|(
name|volumeArgs
operator|.
name|getOwnerName
argument_list|()
argument_list|)
operator|.
name|setQuotaInBytes
argument_list|(
name|quota
argument_list|)
operator|.
name|setCreationTime
argument_list|(
name|volumeArgs
operator|.
name|getCreationTime
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|VolumeInfo
name|newVolumeInfo
init|=
name|newVolumeArgs
operator|.
name|getProtobuf
argument_list|()
decl_stmt|;
name|metadataManager
operator|.
name|getVolumeTable
argument_list|()
operator|.
name|put
argument_list|(
name|dbVolumeKey
argument_list|,
name|newVolumeInfo
operator|.
name|toByteArray
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
if|if
condition|(
operator|!
operator|(
name|ex
operator|instanceof
name|OMException
operator|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Changing volume quota failed for volume:{} quota:{}"
argument_list|,
name|volume
argument_list|,
name|quota
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseVolumeLock
argument_list|(
name|volume
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Gets the volume information.    * @param volume - Volume name.    * @return VolumeArgs or exception is thrown.    * @throws IOException    */
annotation|@
name|Override
DECL|method|getVolumeInfo (String volume)
specifier|public
name|OmVolumeArgs
name|getVolumeInfo
parameter_list|(
name|String
name|volume
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireVolumeLock
argument_list|(
name|volume
argument_list|)
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|dbVolumeKey
init|=
name|metadataManager
operator|.
name|getVolumeKey
argument_list|(
name|volume
argument_list|)
decl_stmt|;
name|byte
index|[]
name|volInfo
init|=
name|metadataManager
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
name|volInfo
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"volume:{} does not exist"
argument_list|,
name|volume
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|OMException
argument_list|(
name|ResultCodes
operator|.
name|FAILED_VOLUME_NOT_FOUND
argument_list|)
throw|;
block|}
name|VolumeInfo
name|volumeInfo
init|=
name|VolumeInfo
operator|.
name|parseFrom
argument_list|(
name|volInfo
argument_list|)
decl_stmt|;
name|OmVolumeArgs
name|volumeArgs
init|=
name|OmVolumeArgs
operator|.
name|getFromProtobuf
argument_list|(
name|volumeInfo
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|volume
operator|.
name|equals
argument_list|(
name|volumeInfo
operator|.
name|getVolume
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|volumeArgs
return|;
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
name|OMException
operator|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Info volume failed for volume:{}"
argument_list|,
name|volume
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseVolumeLock
argument_list|(
name|volume
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Deletes an existing empty volume.    *    * @param volume - Name of the volume.    * @throws IOException    */
annotation|@
name|Override
DECL|method|deleteVolume (String volume)
specifier|public
name|void
name|deleteVolume
parameter_list|(
name|String
name|volume
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|String
name|owner
decl_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireVolumeLock
argument_list|(
name|volume
argument_list|)
expr_stmt|;
try|try
block|{
name|owner
operator|=
name|getVolumeInfo
argument_list|(
name|volume
argument_list|)
operator|.
name|getOwnerName
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseVolumeLock
argument_list|(
name|volume
argument_list|)
expr_stmt|;
block|}
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireUserLock
argument_list|(
name|owner
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireVolumeLock
argument_list|(
name|volume
argument_list|)
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|dbVolumeKey
init|=
name|metadataManager
operator|.
name|getVolumeKey
argument_list|(
name|volume
argument_list|)
decl_stmt|;
name|byte
index|[]
name|volInfo
init|=
name|metadataManager
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
name|volInfo
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"volume:{} does not exist"
argument_list|,
name|volume
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|OMException
argument_list|(
name|ResultCodes
operator|.
name|FAILED_VOLUME_NOT_FOUND
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|metadataManager
operator|.
name|isVolumeEmpty
argument_list|(
name|volume
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"volume:{} is not empty"
argument_list|,
name|volume
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|OMException
argument_list|(
name|ResultCodes
operator|.
name|FAILED_VOLUME_NOT_EMPTY
argument_list|)
throw|;
block|}
name|VolumeInfo
name|volumeInfo
init|=
name|VolumeInfo
operator|.
name|parseFrom
argument_list|(
name|volInfo
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|volume
operator|.
name|equals
argument_list|(
name|volumeInfo
operator|.
name|getVolume
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// delete the volume from the owner list
comment|// as well as delete the volume entry
try|try
init|(
name|BatchOperation
name|batch
init|=
name|metadataManager
operator|.
name|getStore
argument_list|()
operator|.
name|initBatchOperation
argument_list|()
init|)
block|{
name|delVolumeFromOwnerList
argument_list|(
name|volume
argument_list|,
name|volumeInfo
operator|.
name|getOwnerName
argument_list|()
argument_list|,
name|batch
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|getVolumeTable
argument_list|()
operator|.
name|deleteWithBatch
argument_list|(
name|batch
argument_list|,
name|dbVolumeKey
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|getStore
argument_list|()
operator|.
name|commitBatchOperation
argument_list|(
name|batch
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RocksDBException
decl||
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
name|OMException
operator|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Delete volume failed for volume:{}"
argument_list|,
name|volume
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ex
operator|instanceof
name|RocksDBException
condition|)
block|{
throw|throw
name|RocksDBStore
operator|.
name|toIOException
argument_list|(
literal|"Volume creation failed."
argument_list|,
operator|(
name|RocksDBException
operator|)
name|ex
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|(
name|IOException
operator|)
name|ex
throw|;
block|}
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseVolumeLock
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseUserLock
argument_list|(
name|owner
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Checks if the specified user with a role can access this volume.    *    * @param volume - volume    * @param userAcl - user acl which needs to be checked for access    * @return true if the user has access for the volume, false otherwise    * @throws IOException    */
annotation|@
name|Override
DECL|method|checkVolumeAccess (String volume, OzoneAclInfo userAcl)
specifier|public
name|boolean
name|checkVolumeAccess
parameter_list|(
name|String
name|volume
parameter_list|,
name|OzoneAclInfo
name|userAcl
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|userAcl
argument_list|)
expr_stmt|;
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireVolumeLock
argument_list|(
name|volume
argument_list|)
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|dbVolumeKey
init|=
name|metadataManager
operator|.
name|getVolumeKey
argument_list|(
name|volume
argument_list|)
decl_stmt|;
name|byte
index|[]
name|volInfo
init|=
name|metadataManager
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
name|volInfo
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"volume:{} does not exist"
argument_list|,
name|volume
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|OMException
argument_list|(
name|ResultCodes
operator|.
name|FAILED_VOLUME_NOT_FOUND
argument_list|)
throw|;
block|}
name|VolumeInfo
name|volumeInfo
init|=
name|VolumeInfo
operator|.
name|parseFrom
argument_list|(
name|volInfo
argument_list|)
decl_stmt|;
name|OmVolumeArgs
name|volumeArgs
init|=
name|OmVolumeArgs
operator|.
name|getFromProtobuf
argument_list|(
name|volumeInfo
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|volume
operator|.
name|equals
argument_list|(
name|volumeInfo
operator|.
name|getVolume
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|volumeArgs
operator|.
name|getAclMap
argument_list|()
operator|.
name|hasAccess
argument_list|(
name|userAcl
argument_list|)
return|;
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
name|OMException
operator|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Check volume access failed for volume:{} user:{} rights:{}"
argument_list|,
name|volume
argument_list|,
name|userAcl
operator|.
name|getName
argument_list|()
argument_list|,
name|userAcl
operator|.
name|getRights
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseVolumeLock
argument_list|(
name|volume
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|listVolumes (String userName, String prefix, String startKey, int maxKeys)
specifier|public
name|List
argument_list|<
name|OmVolumeArgs
argument_list|>
name|listVolumes
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|prefix
parameter_list|,
name|String
name|startKey
parameter_list|,
name|int
name|maxKeys
parameter_list|)
throws|throws
name|IOException
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|acquireUserLock
argument_list|(
name|userName
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|metadataManager
operator|.
name|listVolumes
argument_list|(
name|userName
argument_list|,
name|prefix
argument_list|,
name|startKey
argument_list|,
name|maxKeys
argument_list|)
return|;
block|}
finally|finally
block|{
name|metadataManager
operator|.
name|getLock
argument_list|()
operator|.
name|releaseUserLock
argument_list|(
name|userName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

