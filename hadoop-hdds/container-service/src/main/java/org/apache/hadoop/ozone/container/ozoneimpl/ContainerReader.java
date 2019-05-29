begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.ozoneimpl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|ozoneimpl
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Longs
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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|StorageContainerException
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
name|common
operator|.
name|Storage
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|BlockData
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
name|container
operator|.
name|common
operator|.
name|helpers
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerUtils
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
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ContainerData
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
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ContainerSet
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
name|container
operator|.
name|common
operator|.
name|volume
operator|.
name|HddsVolume
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
name|container
operator|.
name|common
operator|.
name|volume
operator|.
name|VolumeSet
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
name|container
operator|.
name|keyvalue
operator|.
name|KeyValueBlockIterator
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
name|container
operator|.
name|keyvalue
operator|.
name|KeyValueContainer
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
name|container
operator|.
name|keyvalue
operator|.
name|KeyValueContainerData
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
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ContainerDataYaml
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
name|container
operator|.
name|keyvalue
operator|.
name|helpers
operator|.
name|BlockUtils
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
name|container
operator|.
name|keyvalue
operator|.
name|helpers
operator|.
name|KeyValueContainerUtil
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
name|MetadataKeyFilters
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
name|container
operator|.
name|common
operator|.
name|utils
operator|.
name|ReferenceCountedDB
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileFilter
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
comment|/**  * Class used to read .container files from Volume and build container map.  *  * Layout of the container directory on disk is as follows:  *  *<p>../hdds/VERSION  *<p>{@literal ../hdds/<<scmUuid>>/current/<<containerDir>>/<<containerID  *>/metadata/<<containerID>>.container}  *<p>{@literal ../hdds/<<scmUuid>>/current/<<containerDir>>/<<containerID  *>/<<dataPath>>}  *<p>  * Some ContainerTypes will have extra metadata other than the .container  * file. For example, KeyValueContainer will have a .db file. This .db file  * will also be stored in the metadata folder along with the .container file.  *<p>  * {@literal ../hdds/<<scmUuid>>/current/<<containerDir>>/<<KVcontainerID  *>/metadata/<<KVcontainerID>>.db}  *<p>  * Note that the {@literal<<dataPath>>} is dependent on the ContainerType.  * For KeyValueContainers, the data is stored in a "chunks" folder. As such,  * the {@literal<<dataPath>>} layout for KeyValueContainers is:  *<p>{@literal ../hdds/<<scmUuid>>/current/<<containerDir>>/<<KVcontainerID  *>/chunks/<<chunksFile>>}  *  */
end_comment

begin_class
DECL|class|ContainerReader
specifier|public
class|class
name|ContainerReader
implements|implements
name|Runnable
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
name|ContainerReader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|hddsVolume
specifier|private
name|HddsVolume
name|hddsVolume
decl_stmt|;
DECL|field|containerSet
specifier|private
specifier|final
name|ContainerSet
name|containerSet
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|OzoneConfiguration
name|config
decl_stmt|;
DECL|field|hddsVolumeDir
specifier|private
specifier|final
name|File
name|hddsVolumeDir
decl_stmt|;
DECL|field|volumeSet
specifier|private
specifier|final
name|VolumeSet
name|volumeSet
decl_stmt|;
DECL|method|ContainerReader (VolumeSet volSet, HddsVolume volume, ContainerSet cset, OzoneConfiguration conf)
name|ContainerReader
parameter_list|(
name|VolumeSet
name|volSet
parameter_list|,
name|HddsVolume
name|volume
parameter_list|,
name|ContainerSet
name|cset
parameter_list|,
name|OzoneConfiguration
name|conf
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|this
operator|.
name|hddsVolume
operator|=
name|volume
expr_stmt|;
name|this
operator|.
name|hddsVolumeDir
operator|=
name|hddsVolume
operator|.
name|getHddsRootDir
argument_list|()
expr_stmt|;
name|this
operator|.
name|containerSet
operator|=
name|cset
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|volumeSet
operator|=
name|volSet
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|readVolume
argument_list|(
name|hddsVolumeDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Caught a Run time exception during reading container files"
operator|+
literal|" from Volume {} {}"
argument_list|,
name|hddsVolumeDir
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readVolume (File hddsVolumeRootDir)
specifier|public
name|void
name|readVolume
parameter_list|(
name|File
name|hddsVolumeRootDir
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|hddsVolumeRootDir
argument_list|,
literal|"hddsVolumeRootDir"
operator|+
literal|"cannot be null"
argument_list|)
expr_stmt|;
comment|//filtering scm directory
name|File
index|[]
name|scmDir
init|=
name|hddsVolumeRootDir
operator|.
name|listFiles
argument_list|(
operator|new
name|FileFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|pathname
parameter_list|)
block|{
return|return
name|pathname
operator|.
name|isDirectory
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|scmDir
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"IO error for the volume {}, skipped loading"
argument_list|,
name|hddsVolumeRootDir
argument_list|)
expr_stmt|;
name|volumeSet
operator|.
name|failVolume
argument_list|(
name|hddsVolumeRootDir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|scmDir
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Volume {} is in Inconsistent state"
argument_list|,
name|hddsVolumeRootDir
argument_list|)
expr_stmt|;
name|volumeSet
operator|.
name|failVolume
argument_list|(
name|hddsVolumeRootDir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
name|File
name|scmLoc
range|:
name|scmDir
control|)
block|{
name|File
name|currentDir
init|=
operator|new
name|File
argument_list|(
name|scmLoc
argument_list|,
name|Storage
operator|.
name|STORAGE_DIR_CURRENT
argument_list|)
decl_stmt|;
name|File
index|[]
name|containerTopDirs
init|=
name|currentDir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|containerTopDirs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|containerTopDir
range|:
name|containerTopDirs
control|)
block|{
if|if
condition|(
name|containerTopDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|File
index|[]
name|containerDirs
init|=
name|containerTopDir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|containerDirs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|containerDir
range|:
name|containerDirs
control|)
block|{
name|File
name|containerFile
init|=
name|ContainerUtils
operator|.
name|getContainerFile
argument_list|(
name|containerDir
argument_list|)
decl_stmt|;
name|long
name|containerID
init|=
name|ContainerUtils
operator|.
name|getContainerID
argument_list|(
name|containerDir
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|verifyContainerFile
argument_list|(
name|containerID
argument_list|,
name|containerFile
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Missing .container file for ContainerID: {}"
argument_list|,
name|containerDir
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
block|}
DECL|method|verifyContainerFile (long containerID, File containerFile)
specifier|private
name|void
name|verifyContainerFile
parameter_list|(
name|long
name|containerID
parameter_list|,
name|File
name|containerFile
parameter_list|)
block|{
try|try
block|{
name|ContainerData
name|containerData
init|=
name|ContainerDataYaml
operator|.
name|readContainerFile
argument_list|(
name|containerFile
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerID
operator|!=
name|containerData
operator|.
name|getContainerID
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Invalid ContainerID in file {}. "
operator|+
literal|"Skipping loading of this container."
argument_list|,
name|containerFile
argument_list|)
expr_stmt|;
return|return;
block|}
name|verifyAndFixupContainerData
argument_list|(
name|containerData
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to parse ContainerFile for ContainerID: {}"
argument_list|,
name|containerID
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * verify ContainerData loaded from disk and fix-up stale members.    * Specifically blockCommitSequenceId, delete related metadata    * and bytesUsed    * @param containerData    * @throws IOException    */
DECL|method|verifyAndFixupContainerData (ContainerData containerData)
specifier|public
name|void
name|verifyAndFixupContainerData
parameter_list|(
name|ContainerData
name|containerData
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|containerData
operator|.
name|getContainerType
argument_list|()
condition|)
block|{
case|case
name|KeyValueContainer
case|:
if|if
condition|(
name|containerData
operator|instanceof
name|KeyValueContainerData
condition|)
block|{
name|KeyValueContainerData
name|kvContainerData
init|=
operator|(
name|KeyValueContainerData
operator|)
name|containerData
decl_stmt|;
name|containerData
operator|.
name|setVolume
argument_list|(
name|hddsVolume
argument_list|)
expr_stmt|;
name|KeyValueContainerUtil
operator|.
name|parseKVContainerData
argument_list|(
name|kvContainerData
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|KeyValueContainer
name|kvContainer
init|=
operator|new
name|KeyValueContainer
argument_list|(
name|kvContainerData
argument_list|,
name|config
argument_list|)
decl_stmt|;
try|try
init|(
name|ReferenceCountedDB
name|containerDB
init|=
name|BlockUtils
operator|.
name|getDB
argument_list|(
name|kvContainerData
argument_list|,
name|config
argument_list|)
init|)
block|{
name|MetadataKeyFilters
operator|.
name|KeyPrefixFilter
name|filter
init|=
operator|new
name|MetadataKeyFilters
operator|.
name|KeyPrefixFilter
argument_list|()
operator|.
name|addFilter
argument_list|(
name|OzoneConsts
operator|.
name|DELETING_KEY_PREFIX
argument_list|)
decl_stmt|;
name|int
name|numPendingDeletionBlocks
init|=
name|containerDB
operator|.
name|getStore
argument_list|()
operator|.
name|getSequentialRangeKVs
argument_list|(
literal|null
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|filter
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
name|kvContainerData
operator|.
name|incrPendingDeletionBlocks
argument_list|(
name|numPendingDeletionBlocks
argument_list|)
expr_stmt|;
name|byte
index|[]
name|delTxnId
init|=
name|containerDB
operator|.
name|getStore
argument_list|()
operator|.
name|get
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|OzoneConsts
operator|.
name|DELETE_TRANSACTION_KEY_PREFIX
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|delTxnId
operator|!=
literal|null
condition|)
block|{
name|kvContainerData
operator|.
name|updateDeleteTransactionId
argument_list|(
name|Longs
operator|.
name|fromByteArray
argument_list|(
name|delTxnId
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// sets the BlockCommitSequenceId.
name|byte
index|[]
name|bcsId
init|=
name|containerDB
operator|.
name|getStore
argument_list|()
operator|.
name|get
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|OzoneConsts
operator|.
name|BLOCK_COMMIT_SEQUENCE_ID_PREFIX
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|bcsId
operator|!=
literal|null
condition|)
block|{
name|kvContainerData
operator|.
name|updateBlockCommitSequenceId
argument_list|(
name|Longs
operator|.
name|fromByteArray
argument_list|(
name|bcsId
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|kvContainer
operator|.
name|getContainerState
argument_list|()
operator|==
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|OPEN
condition|)
block|{
comment|// commitSpace for Open Containers relies on usedBytes
name|initializeUsedBytes
argument_list|(
name|kvContainer
argument_list|)
expr_stmt|;
block|}
name|containerSet
operator|.
name|addContainer
argument_list|(
name|kvContainer
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Container File is corrupted. "
operator|+
literal|"ContainerType is KeyValueContainer but cast to "
operator|+
literal|"KeyValueContainerData failed. "
argument_list|,
name|ContainerProtos
operator|.
name|Result
operator|.
name|CONTAINER_METADATA_ERROR
argument_list|)
throw|;
block|}
break|break;
default|default:
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Unrecognized ContainerType "
operator|+
name|containerData
operator|.
name|getContainerType
argument_list|()
argument_list|,
name|ContainerProtos
operator|.
name|Result
operator|.
name|UNKNOWN_CONTAINER_TYPE
argument_list|)
throw|;
block|}
block|}
DECL|method|initializeUsedBytes (KeyValueContainer container)
specifier|private
name|void
name|initializeUsedBytes
parameter_list|(
name|KeyValueContainer
name|container
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|KeyValueBlockIterator
name|blockIter
init|=
operator|new
name|KeyValueBlockIterator
argument_list|(
name|container
operator|.
name|getContainerData
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|,
operator|new
name|File
argument_list|(
name|container
operator|.
name|getContainerData
argument_list|()
operator|.
name|getContainerPath
argument_list|()
argument_list|)
argument_list|)
init|)
block|{
name|long
name|usedBytes
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|blockIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|BlockData
name|block
init|=
name|blockIter
operator|.
name|nextBlock
argument_list|()
decl_stmt|;
name|long
name|blockLen
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|ContainerProtos
operator|.
name|ChunkInfo
argument_list|>
name|chunkInfoList
init|=
name|block
operator|.
name|getChunks
argument_list|()
decl_stmt|;
for|for
control|(
name|ContainerProtos
operator|.
name|ChunkInfo
name|chunk
range|:
name|chunkInfoList
control|)
block|{
name|ChunkInfo
name|info
init|=
name|ChunkInfo
operator|.
name|getFromProtoBuf
argument_list|(
name|chunk
argument_list|)
decl_stmt|;
name|blockLen
operator|+=
name|info
operator|.
name|getLen
argument_list|()
expr_stmt|;
block|}
name|usedBytes
operator|+=
name|blockLen
expr_stmt|;
block|}
name|container
operator|.
name|getContainerData
argument_list|()
operator|.
name|setBytesUsed
argument_list|(
name|usedBytes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

