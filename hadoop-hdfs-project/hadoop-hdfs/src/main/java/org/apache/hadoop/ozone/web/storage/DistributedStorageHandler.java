begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.storage
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
operator|.
name|storage
package|;
end_package

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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ChunkInfo
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|GetKeyResponseProto
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|KeyData
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
name|DatanodeInfo
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
name|protocolPB
operator|.
name|PBHelperClient
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
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|LengthInputStream
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
name|protocolPB
operator|.
name|KeySpaceManagerProtocolClientSideTranslatorPB
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
name|OzoneConsts
operator|.
name|Versioning
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
name|protocolPB
operator|.
name|KSMPBHelper
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
name|web
operator|.
name|request
operator|.
name|OzoneQuota
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
name|Pipeline
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
name|ScmConfigKeys
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
name|XceiverClientManager
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
name|LocatedContainer
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
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
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
name|web
operator|.
name|exceptions
operator|.
name|OzoneException
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
name|web
operator|.
name|handlers
operator|.
name|BucketArgs
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
name|web
operator|.
name|handlers
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
name|web
operator|.
name|handlers
operator|.
name|ListArgs
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
name|web
operator|.
name|handlers
operator|.
name|VolumeArgs
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
name|web
operator|.
name|interfaces
operator|.
name|StorageHandler
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
name|web
operator|.
name|response
operator|.
name|*
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
name|XceiverClientSpi
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
name|storage
operator|.
name|ChunkInputStream
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
name|storage
operator|.
name|ChunkOutputStream
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
name|storage
operator|.
name|ContainerProtocolCalls
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
name|StringUtils
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|stream
operator|.
name|Collectors
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
name|web
operator|.
name|storage
operator|.
name|OzoneContainerTranslation
operator|.
name|*
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
name|scm
operator|.
name|storage
operator|.
name|ContainerProtocolCalls
operator|.
name|getKey
import|;
end_import

begin_comment
comment|/**  * A {@link StorageHandler} implementation that distributes object storage  * across the nodes of an HDFS cluster.  */
end_comment

begin_class
DECL|class|DistributedStorageHandler
specifier|public
specifier|final
class|class
name|DistributedStorageHandler
implements|implements
name|StorageHandler
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
name|DistributedStorageHandler
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|StorageContainerLocationProtocolClientSideTranslatorPB
DECL|field|storageContainerLocationClient
name|storageContainerLocationClient
decl_stmt|;
specifier|private
specifier|final
name|KeySpaceManagerProtocolClientSideTranslatorPB
DECL|field|keySpaceManagerClient
name|keySpaceManagerClient
decl_stmt|;
DECL|field|xceiverClientManager
specifier|private
specifier|final
name|XceiverClientManager
name|xceiverClientManager
decl_stmt|;
DECL|field|chunkSize
specifier|private
name|int
name|chunkSize
decl_stmt|;
comment|/**    * Creates a new DistributedStorageHandler.    *    * @param conf configuration    * @param storageContainerLocation StorageContainerLocationProtocol proxy    * @param keySpaceManagerClient KeySpaceManager proxy    */
DECL|method|DistributedStorageHandler (OzoneConfiguration conf, StorageContainerLocationProtocolClientSideTranslatorPB storageContainerLocation, KeySpaceManagerProtocolClientSideTranslatorPB keySpaceManagerClient)
specifier|public
name|DistributedStorageHandler
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|StorageContainerLocationProtocolClientSideTranslatorPB
name|storageContainerLocation
parameter_list|,
name|KeySpaceManagerProtocolClientSideTranslatorPB
name|keySpaceManagerClient
parameter_list|)
block|{
name|this
operator|.
name|keySpaceManagerClient
operator|=
name|keySpaceManagerClient
expr_stmt|;
name|this
operator|.
name|storageContainerLocationClient
operator|=
name|storageContainerLocation
expr_stmt|;
name|this
operator|.
name|xceiverClientManager
operator|=
operator|new
name|XceiverClientManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|chunkSize
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CHUNK_SIZE_KEY
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CHUNK_SIZE_DEFAULT
argument_list|)
expr_stmt|;
if|if
condition|(
name|chunkSize
operator|>
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CHUNK_MAX_SIZE
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The chunk size ({}) is not allowed to be more than"
operator|+
literal|" the maximum size ({}),"
operator|+
literal|" resetting to the maximum size."
argument_list|,
name|chunkSize
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CHUNK_MAX_SIZE
argument_list|)
expr_stmt|;
name|chunkSize
operator|=
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CHUNK_MAX_SIZE
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|createVolume (VolumeArgs args)
specifier|public
name|void
name|createVolume
parameter_list|(
name|VolumeArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|long
name|quota
init|=
name|args
operator|.
name|getQuota
argument_list|()
operator|==
literal|null
condition|?
name|OzoneConsts
operator|.
name|MAX_QUOTA_IN_BYTES
else|:
name|args
operator|.
name|getQuota
argument_list|()
operator|.
name|sizeInBytes
argument_list|()
decl_stmt|;
name|KsmVolumeArgs
name|volumeArgs
init|=
name|KsmVolumeArgs
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
name|getUserName
argument_list|()
argument_list|)
operator|.
name|setVolume
argument_list|(
name|args
operator|.
name|getVolumeName
argument_list|()
argument_list|)
operator|.
name|setQuotaInBytes
argument_list|(
name|quota
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|keySpaceManagerClient
operator|.
name|createVolume
argument_list|(
name|volumeArgs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setVolumeOwner (VolumeArgs args)
specifier|public
name|void
name|setVolumeOwner
parameter_list|(
name|VolumeArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|keySpaceManagerClient
operator|.
name|setOwner
argument_list|(
name|args
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|args
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setVolumeQuota (VolumeArgs args, boolean remove)
specifier|public
name|void
name|setVolumeQuota
parameter_list|(
name|VolumeArgs
name|args
parameter_list|,
name|boolean
name|remove
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|long
name|quota
init|=
name|remove
condition|?
name|OzoneConsts
operator|.
name|MAX_QUOTA_IN_BYTES
else|:
name|args
operator|.
name|getQuota
argument_list|()
operator|.
name|sizeInBytes
argument_list|()
decl_stmt|;
name|keySpaceManagerClient
operator|.
name|setQuota
argument_list|(
name|args
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|quota
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|checkVolumeAccess (VolumeArgs args)
specifier|public
name|boolean
name|checkVolumeAccess
parameter_list|(
name|VolumeArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"checkVolumeAccessnot implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|listVolumes (ListArgs args)
specifier|public
name|ListVolumes
name|listVolumes
parameter_list|(
name|ListArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"listVolumes not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|deleteVolume (VolumeArgs args)
specifier|public
name|void
name|deleteVolume
parameter_list|(
name|VolumeArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"deleteVolume not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getVolumeInfo (VolumeArgs args)
specifier|public
name|VolumeInfo
name|getVolumeInfo
parameter_list|(
name|VolumeArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|KsmVolumeArgs
name|volumeArgs
init|=
name|keySpaceManagerClient
operator|.
name|getVolumeInfo
argument_list|(
name|args
operator|.
name|getVolumeName
argument_list|()
argument_list|)
decl_stmt|;
comment|//TODO: add support for createdOn and other fields in getVolumeInfo
name|VolumeInfo
name|volInfo
init|=
operator|new
name|VolumeInfo
argument_list|(
name|volumeArgs
operator|.
name|getVolume
argument_list|()
argument_list|,
literal|null
argument_list|,
name|volumeArgs
operator|.
name|getAdminName
argument_list|()
argument_list|)
decl_stmt|;
name|volInfo
operator|.
name|setOwner
argument_list|(
operator|new
name|VolumeOwner
argument_list|(
name|volumeArgs
operator|.
name|getOwnerName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|volInfo
operator|.
name|setQuota
argument_list|(
name|OzoneQuota
operator|.
name|getOzoneQuota
argument_list|(
name|volumeArgs
operator|.
name|getQuotaInBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|volInfo
return|;
block|}
annotation|@
name|Override
DECL|method|createBucket (final BucketArgs args)
specifier|public
name|void
name|createBucket
parameter_list|(
specifier|final
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|KsmBucketInfo
operator|.
name|Builder
name|builder
init|=
name|KsmBucketInfo
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
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
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|getAddAcls
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setAcls
argument_list|(
name|args
operator|.
name|getAddAcls
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|KSMPBHelper
operator|::
name|convertOzoneAcl
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|getStorageType
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setStorageType
argument_list|(
name|PBHelperClient
operator|.
name|convertStorageType
argument_list|(
name|args
operator|.
name|getStorageType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|getVersioning
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setIsVersionEnabled
argument_list|(
name|getBucketVersioningProtobuf
argument_list|(
name|args
operator|.
name|getVersioning
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|keySpaceManagerClient
operator|.
name|createBucket
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Converts OzoneConts.Versioning enum to boolean.    *    * @param version    * @return corresponding boolean value    */
DECL|method|getBucketVersioningProtobuf ( Versioning version)
specifier|private
name|boolean
name|getBucketVersioningProtobuf
parameter_list|(
name|Versioning
name|version
parameter_list|)
block|{
if|if
condition|(
name|version
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|version
condition|)
block|{
case|case
name|ENABLED
case|:
return|return
literal|true
return|;
case|case
name|NOT_DEFINED
case|:
case|case
name|DISABLED
case|:
default|default:
return|return
literal|false
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|setBucketAcls (BucketArgs args)
specifier|public
name|void
name|setBucketAcls
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"setBucketAcls not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|setBucketVersioning (BucketArgs args)
specifier|public
name|void
name|setBucketVersioning
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"setBucketVersioning not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|setBucketStorageClass (BucketArgs args)
specifier|public
name|void
name|setBucketStorageClass
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"setBucketStorageClass not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|deleteBucket (BucketArgs args)
specifier|public
name|void
name|deleteBucket
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"deleteBucket not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|checkBucketAccess (BucketArgs args)
specifier|public
name|void
name|checkBucketAccess
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"checkBucketAccess not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|listBuckets (ListArgs args)
specifier|public
name|ListBuckets
name|listBuckets
parameter_list|(
name|ListArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"listBuckets not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getBucketInfo (BucketArgs args)
specifier|public
name|BucketInfo
name|getBucketInfo
parameter_list|(
name|BucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
block|{
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
name|KsmBucketInfo
name|ksmBucketInfo
init|=
name|keySpaceManagerClient
operator|.
name|getBucketInfo
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
name|BucketInfo
name|bucketInfo
init|=
operator|new
name|BucketInfo
argument_list|(
name|ksmBucketInfo
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|ksmBucketInfo
operator|.
name|getBucketName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ksmBucketInfo
operator|.
name|getIsVersionEnabled
argument_list|()
condition|)
block|{
name|bucketInfo
operator|.
name|setVersioning
argument_list|(
name|Versioning
operator|.
name|ENABLED
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bucketInfo
operator|.
name|setVersioning
argument_list|(
name|Versioning
operator|.
name|DISABLED
argument_list|)
expr_stmt|;
block|}
name|bucketInfo
operator|.
name|setStorageType
argument_list|(
name|PBHelperClient
operator|.
name|convertStorageType
argument_list|(
name|ksmBucketInfo
operator|.
name|getStorageType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|bucketInfo
operator|.
name|setAcls
argument_list|(
name|ksmBucketInfo
operator|.
name|getAcls
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|KSMPBHelper
operator|::
name|convertOzoneAcl
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|bucketInfo
return|;
block|}
annotation|@
name|Override
DECL|method|newKeyWriter (KeyArgs args)
specifier|public
name|OutputStream
name|newKeyWriter
parameter_list|(
name|KeyArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|KsmKeyArgs
name|keyArgs
init|=
operator|new
name|KsmKeyArgs
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
name|setDataSize
argument_list|(
name|args
operator|.
name|getSize
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// contact KSM to allocate a block for key.
name|String
name|containerKey
init|=
name|buildContainerKey
argument_list|(
name|args
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|args
operator|.
name|getBucketName
argument_list|()
argument_list|,
name|args
operator|.
name|getKeyName
argument_list|()
argument_list|)
decl_stmt|;
name|KsmKeyInfo
name|keyInfo
init|=
name|keySpaceManagerClient
operator|.
name|allocateKey
argument_list|(
name|keyArgs
argument_list|)
decl_stmt|;
comment|// TODO the following createContainer and key writes may fail, in which
comment|// case we should revert the above allocateKey to KSM.
name|String
name|containerName
init|=
name|keyInfo
operator|.
name|getContainerName
argument_list|()
decl_stmt|;
name|XceiverClientSpi
name|xceiverClient
init|=
name|getContainer
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyInfo
operator|.
name|getShouldCreateContainer
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Need to create container {} for key: {}/{}/{}"
argument_list|,
name|containerName
argument_list|,
name|args
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|args
operator|.
name|getBucketName
argument_list|()
argument_list|,
name|args
operator|.
name|getKeyName
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerProtocolCalls
operator|.
name|createContainer
argument_list|(
name|xceiverClient
argument_list|,
name|args
operator|.
name|getRequestID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// establish a connection to the container to write the key
return|return
operator|new
name|ChunkOutputStream
argument_list|(
name|containerKey
argument_list|,
name|args
operator|.
name|getKeyName
argument_list|()
argument_list|,
name|xceiverClientManager
argument_list|,
name|xceiverClient
argument_list|,
name|args
operator|.
name|getRequestID
argument_list|()
argument_list|,
name|chunkSize
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|commitKey (KeyArgs args, OutputStream stream)
specifier|public
name|void
name|commitKey
parameter_list|(
name|KeyArgs
name|args
parameter_list|,
name|OutputStream
name|stream
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newKeyReader (KeyArgs args)
specifier|public
name|LengthInputStream
name|newKeyReader
parameter_list|(
name|KeyArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|String
name|containerKey
init|=
name|buildContainerKey
argument_list|(
name|args
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|args
operator|.
name|getBucketName
argument_list|()
argument_list|,
name|args
operator|.
name|getKeyName
argument_list|()
argument_list|)
decl_stmt|;
name|XceiverClientSpi
name|xceiverClient
init|=
name|acquireXceiverClient
argument_list|(
name|containerKey
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|KeyData
name|containerKeyData
init|=
name|containerKeyDataForRead
argument_list|(
name|xceiverClient
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
argument_list|()
argument_list|,
name|containerKey
argument_list|)
decl_stmt|;
name|GetKeyResponseProto
name|response
init|=
name|getKey
argument_list|(
name|xceiverClient
argument_list|,
name|containerKeyData
argument_list|,
name|args
operator|.
name|getRequestID
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|length
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|ChunkInfo
argument_list|>
name|chunks
init|=
name|response
operator|.
name|getKeyData
argument_list|()
operator|.
name|getChunksList
argument_list|()
decl_stmt|;
for|for
control|(
name|ChunkInfo
name|chunk
range|:
name|chunks
control|)
block|{
name|length
operator|+=
name|chunk
operator|.
name|getLen
argument_list|()
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
return|return
operator|new
name|LengthInputStream
argument_list|(
operator|new
name|ChunkInputStream
argument_list|(
name|containerKey
argument_list|,
name|xceiverClientManager
argument_list|,
name|xceiverClient
argument_list|,
name|chunks
argument_list|,
name|args
operator|.
name|getRequestID
argument_list|()
argument_list|)
argument_list|,
name|length
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|xceiverClientManager
operator|.
name|releaseClient
argument_list|(
name|xceiverClient
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|deleteKey (KeyArgs args)
specifier|public
name|void
name|deleteKey
parameter_list|(
name|KeyArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"deleteKey not implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|listKeys (ListArgs args)
specifier|public
name|ListKeys
name|listKeys
parameter_list|(
name|ListArgs
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"listKeys not implemented"
argument_list|)
throw|;
block|}
DECL|method|getContainer (String containerName)
specifier|private
name|XceiverClientSpi
name|getContainer
parameter_list|(
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
block|{
name|Pipeline
name|pipeline
init|=
name|storageContainerLocationClient
operator|.
name|getContainer
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
return|return
name|xceiverClientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline
argument_list|)
return|;
block|}
comment|/**    * Acquires an {@link XceiverClientSpi} connected to a {@link Pipeline}    * of nodes capable of serving container protocol operations.    * The container is selected based on the specified container key.    *    * @param containerKey container key    * @return XceiverClient connected to a container    * @throws IOException if an XceiverClient cannot be acquired    */
DECL|method|acquireXceiverClient (String containerKey)
specifier|private
name|XceiverClientSpi
name|acquireXceiverClient
parameter_list|(
name|String
name|containerKey
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|LocatedContainer
argument_list|>
name|locatedContainers
init|=
name|storageContainerLocationClient
operator|.
name|getStorageContainerLocations
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|containerKey
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|newPipelineFromLocatedContainer
argument_list|(
name|locatedContainers
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|xceiverClientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline
argument_list|)
return|;
block|}
comment|/**    * Creates a container key from any number of components by combining all    * components with a delimiter.    *    * @param parts container key components    * @return container key    */
DECL|method|buildContainerKey (String... parts)
specifier|private
specifier|static
name|String
name|buildContainerKey
parameter_list|(
name|String
modifier|...
name|parts
parameter_list|)
block|{
return|return
literal|'/'
operator|+
name|StringUtils
operator|.
name|join
argument_list|(
literal|'/'
argument_list|,
name|parts
argument_list|)
return|;
block|}
comment|/**    * Formats a date in the expected string format.    *    * @param date the date to format    * @return formatted string representation of date    */
DECL|method|dateToString (Date date)
specifier|private
specifier|static
name|String
name|dateToString
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
name|SimpleDateFormat
name|sdf
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_DATE_FORMAT
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|sdf
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_TIME_ZONE
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sdf
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
block|}
comment|/**    * Translates a set of container locations, ordered such that the first is the    * leader, into a corresponding {@link Pipeline} object.    *    * @param locatedContainer container location    * @return pipeline corresponding to container locations    */
DECL|method|newPipelineFromLocatedContainer ( LocatedContainer locatedContainer)
specifier|private
specifier|static
name|Pipeline
name|newPipelineFromLocatedContainer
parameter_list|(
name|LocatedContainer
name|locatedContainer
parameter_list|)
block|{
name|Set
argument_list|<
name|DatanodeInfo
argument_list|>
name|locations
init|=
name|locatedContainer
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|String
name|leaderId
init|=
name|locations
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getDatanodeUuid
argument_list|()
decl_stmt|;
name|Pipeline
name|pipeline
init|=
operator|new
name|Pipeline
argument_list|(
name|leaderId
argument_list|)
decl_stmt|;
for|for
control|(
name|DatanodeInfo
name|location
range|:
name|locations
control|)
block|{
name|pipeline
operator|.
name|addMember
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
name|pipeline
operator|.
name|setContainerName
argument_list|(
name|locatedContainer
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|pipeline
return|;
block|}
block|}
end_class

end_unit

