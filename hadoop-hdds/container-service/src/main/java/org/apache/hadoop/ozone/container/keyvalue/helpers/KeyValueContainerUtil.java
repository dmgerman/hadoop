begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.keyvalue.helpers
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
name|keyvalue
operator|.
name|helpers
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
name|commons
operator|.
name|codec
operator|.
name|digest
operator|.
name|DigestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|Configuration
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
name|FileAlreadyExistsException
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
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
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
operator|.
name|ContainerCommandResponseProto
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
name|io
operator|.
name|IOUtils
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
name|helpers
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
name|utils
operator|.
name|MetadataStore
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
name|MetadataStoreBuilder
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
name|FileInputStream
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
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
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
import|import static
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
operator|.
name|Result
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Class which defines utility methods for KeyValueContainer.  */
end_comment

begin_class
DECL|class|KeyValueContainerUtil
specifier|public
specifier|final
class|class
name|KeyValueContainerUtil
block|{
comment|/* Never constructed. */
DECL|method|KeyValueContainerUtil ()
specifier|private
name|KeyValueContainerUtil
parameter_list|()
block|{    }
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
name|KeyValueContainerUtil
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * creates metadata path, chunks path and  metadata DB for the specified    * container.    *    * @param containerMetaDataPath    * @throws IOException    */
DECL|method|createContainerMetaData (File containerMetaDataPath, File chunksPath, File dbFile, String containerName, Configuration conf)
specifier|public
specifier|static
name|void
name|createContainerMetaData
parameter_list|(
name|File
name|containerMetaDataPath
parameter_list|,
name|File
name|chunksPath
parameter_list|,
name|File
name|dbFile
parameter_list|,
name|String
name|containerName
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerMetaDataPath
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|containerMetaDataPath
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to create directory for metadata storage. Path: {}"
argument_list|,
name|containerMetaDataPath
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create directory for metadata storage."
operator|+
literal|" Path: "
operator|+
name|containerMetaDataPath
argument_list|)
throw|;
block|}
name|MetadataStore
name|store
init|=
name|MetadataStoreBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
operator|.
name|setCreateIfMissing
argument_list|(
literal|true
argument_list|)
operator|.
name|setDbFile
argument_list|(
name|dbFile
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// we close since the SCM pre-creates containers.
comment|// we will open and put Db handle into a cache when keys are being created
comment|// in a container.
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|chunksPath
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to create chunks directory Container {}"
argument_list|,
name|chunksPath
argument_list|)
expr_stmt|;
comment|//clean up container metadata path and metadata db
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|containerMetaDataPath
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|containerMetaDataPath
operator|.
name|getParentFile
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create directory for data storage."
operator|+
literal|" Path: "
operator|+
name|chunksPath
argument_list|)
throw|;
block|}
block|}
comment|/**    * remove Container if it is empty.    *<p/>    * There are three things we need to delete.    *<p/>    * 1. Container file and metadata file. 2. The Level DB file 3. The path that    * we created on the data location.    *    * @param containerData - Data of the container to remove.    * @param conf - configuration of the cluster.    * @param forceDelete - whether this container should be deleted forcibly.    * @throws IOException    */
DECL|method|removeContainer (KeyValueContainerData containerData, Configuration conf, boolean forceDelete)
specifier|public
specifier|static
name|void
name|removeContainer
parameter_list|(
name|KeyValueContainerData
name|containerData
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|boolean
name|forceDelete
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerData
argument_list|)
expr_stmt|;
name|File
name|containerMetaDataPath
init|=
operator|new
name|File
argument_list|(
name|containerData
operator|.
name|getMetadataPath
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|chunksPath
init|=
operator|new
name|File
argument_list|(
name|containerData
operator|.
name|getChunksPath
argument_list|()
argument_list|)
decl_stmt|;
name|MetadataStore
name|db
init|=
name|KeyUtils
operator|.
name|getDB
argument_list|(
name|containerData
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// If the container is not empty and cannot be deleted forcibly,
comment|// then throw a SCE to stop deleting.
if|if
condition|(
operator|!
name|forceDelete
operator|&&
operator|!
name|db
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Container cannot be deleted because it is not empty."
argument_list|,
name|ContainerProtos
operator|.
name|Result
operator|.
name|ERROR_CONTAINER_NOT_EMPTY
argument_list|)
throw|;
block|}
comment|// Close the DB connection and remove the DB handler from cache
name|KeyUtils
operator|.
name|removeDB
argument_list|(
name|containerData
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// Delete the Container MetaData path.
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|containerMetaDataPath
argument_list|)
expr_stmt|;
comment|//Delete the Container Chunks Path.
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|chunksPath
argument_list|)
expr_stmt|;
comment|//Delete Container directory
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|containerMetaDataPath
operator|.
name|getParentFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a ReadContainer Response.    *    * @param request Request    * @param containerData - data    * @return Response.    */
DECL|method|getReadContainerResponse ( ContainerCommandRequestProto request, KeyValueContainerData containerData)
specifier|public
specifier|static
name|ContainerCommandResponseProto
name|getReadContainerResponse
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|,
name|KeyValueContainerData
name|containerData
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerData
argument_list|)
expr_stmt|;
name|ContainerProtos
operator|.
name|ReadContainerResponseProto
operator|.
name|Builder
name|response
init|=
name|ContainerProtos
operator|.
name|ReadContainerResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|response
operator|.
name|setContainerData
argument_list|(
name|containerData
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerCommandResponseProto
operator|.
name|Builder
name|builder
init|=
name|ContainerUtils
operator|.
name|getSuccessResponseBuilder
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setReadContainer
argument_list|(
name|response
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Compute checksum of the .container file.    * @param containerId    * @param containerFile    * @throws StorageContainerException    */
DECL|method|computeCheckSum (long containerId, File containerFile)
specifier|public
specifier|static
name|String
name|computeCheckSum
parameter_list|(
name|long
name|containerId
parameter_list|,
name|File
name|containerFile
parameter_list|)
throws|throws
name|StorageContainerException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerFile
argument_list|,
literal|"containerFile cannot be null"
argument_list|)
expr_stmt|;
name|MessageDigest
name|sha
decl_stmt|;
name|FileInputStream
name|containerFileStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|sha
operator|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
name|OzoneConsts
operator|.
name|FILE_HASH
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Unable to create Message Digest, "
operator|+
literal|"usually this is a java configuration issue."
argument_list|,
name|NO_SUCH_ALGORITHM
argument_list|)
throw|;
block|}
try|try
block|{
name|containerFileStream
operator|=
operator|new
name|FileInputStream
argument_list|(
name|containerFile
argument_list|)
expr_stmt|;
name|byte
index|[]
name|byteArray
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|bytesCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|bytesCount
operator|=
name|containerFileStream
operator|.
name|read
argument_list|(
name|byteArray
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|sha
operator|.
name|update
argument_list|(
name|byteArray
argument_list|,
literal|0
argument_list|,
name|bytesCount
argument_list|)
expr_stmt|;
block|}
name|String
name|checksum
init|=
name|DigestUtils
operator|.
name|sha256Hex
argument_list|(
name|sha
operator|.
name|digest
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|checksum
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Error during computing checksum: "
operator|+
literal|"for container "
operator|+
name|containerId
argument_list|,
name|ex
argument_list|,
name|CONTAINER_CHECKSUM_ERROR
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|containerFileStream
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Verify checksum of the container.    * @param containerId    * @param checksumFile    * @param checksum    * @throws StorageContainerException    */
DECL|method|verifyCheckSum (long containerId, File checksumFile, String checksum)
specifier|public
specifier|static
name|void
name|verifyCheckSum
parameter_list|(
name|long
name|containerId
parameter_list|,
name|File
name|checksumFile
parameter_list|,
name|String
name|checksum
parameter_list|)
throws|throws
name|StorageContainerException
block|{
try|try
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|checksum
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|checksumFile
argument_list|)
expr_stmt|;
name|Path
name|path
init|=
name|Paths
operator|.
name|get
argument_list|(
name|checksumFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fileCheckSum
init|=
name|Files
operator|.
name|readAllLines
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|fileCheckSum
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|,
literal|"checksum "
operator|+
literal|"should be 32 byte string"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|checksum
operator|.
name|equals
argument_list|(
name|fileCheckSum
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Checksum mismatch for the container {}"
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Checksum mismatch for "
operator|+
literal|"the container "
operator|+
name|containerId
argument_list|,
name|CHECKSUM_MISMATCH
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|StorageContainerException
name|ex
parameter_list|)
block|{
throw|throw
name|ex
throw|;
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
literal|"Error during verify checksum for container {}"
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Error during verify checksum"
operator|+
literal|" for container "
operator|+
name|containerId
argument_list|,
name|IO_EXCEPTION
argument_list|)
throw|;
block|}
block|}
comment|/**    * Parse KeyValueContainerData and verify checksum.    * @param containerData    * @param containerFile    * @param checksumFile    * @param dbFile    * @param config    * @throws IOException    */
DECL|method|parseKeyValueContainerData ( KeyValueContainerData containerData, File containerFile, File checksumFile, File dbFile, OzoneConfiguration config)
specifier|public
specifier|static
name|void
name|parseKeyValueContainerData
parameter_list|(
name|KeyValueContainerData
name|containerData
parameter_list|,
name|File
name|containerFile
parameter_list|,
name|File
name|checksumFile
parameter_list|,
name|File
name|dbFile
parameter_list|,
name|OzoneConfiguration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerData
argument_list|,
literal|"containerData cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerFile
argument_list|,
literal|"containerFile cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|checksumFile
argument_list|,
literal|"checksumFile cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|dbFile
argument_list|,
literal|"dbFile cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|config
argument_list|,
literal|"ozone config cannot be null"
argument_list|)
expr_stmt|;
name|long
name|containerId
init|=
name|containerData
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|String
name|containerName
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|File
name|metadataPath
init|=
operator|new
name|File
argument_list|(
name|containerData
operator|.
name|getMetadataPath
argument_list|()
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerName
argument_list|,
literal|"container Name cannot be "
operator|+
literal|"null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|metadataPath
argument_list|,
literal|"metadata path cannot be "
operator|+
literal|"null"
argument_list|)
expr_stmt|;
comment|// Verify Checksum
name|String
name|checksum
init|=
name|KeyValueContainerUtil
operator|.
name|computeCheckSum
argument_list|(
name|containerData
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|containerFile
argument_list|)
decl_stmt|;
name|KeyValueContainerUtil
operator|.
name|verifyCheckSum
argument_list|(
name|containerId
argument_list|,
name|checksumFile
argument_list|,
name|checksum
argument_list|)
expr_stmt|;
name|containerData
operator|.
name|setDbFile
argument_list|(
name|dbFile
argument_list|)
expr_stmt|;
name|MetadataStore
name|metadata
init|=
name|KeyUtils
operator|.
name|getDB
argument_list|(
name|containerData
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|long
name|bytesUsed
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|liveKeys
init|=
name|metadata
operator|.
name|getRangeKVs
argument_list|(
literal|null
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|MetadataKeyFilters
operator|.
name|getNormalKeyFilter
argument_list|()
argument_list|)
decl_stmt|;
name|bytesUsed
operator|=
name|liveKeys
operator|.
name|parallelStream
argument_list|()
operator|.
name|mapToLong
argument_list|(
name|e
lambda|->
block|{
name|KeyData
name|keyData
decl_stmt|;
try|try
block|{
name|keyData
operator|=
name|KeyUtils
operator|.
name|getKeyData
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|keyData
operator|.
name|getSize
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
return|return
literal|0L
return|;
block|}
block|}
argument_list|)
operator|.
name|sum
argument_list|()
expr_stmt|;
name|containerData
operator|.
name|setBytesUsed
argument_list|(
name|bytesUsed
argument_list|)
expr_stmt|;
name|containerData
operator|.
name|setKeyCount
argument_list|(
name|liveKeys
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the path where data or chunks live for a given container.    *    * @param kvContainerData - KeyValueContainerData    * @return - Path to the chunks directory    */
DECL|method|getDataDirectory (KeyValueContainerData kvContainerData)
specifier|public
specifier|static
name|Path
name|getDataDirectory
parameter_list|(
name|KeyValueContainerData
name|kvContainerData
parameter_list|)
block|{
name|String
name|chunksPath
init|=
name|kvContainerData
operator|.
name|getChunksPath
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|chunksPath
argument_list|)
expr_stmt|;
return|return
name|Paths
operator|.
name|get
argument_list|(
name|chunksPath
argument_list|)
return|;
block|}
comment|/**    * Container metadata directory -- here is where the level DB and    * .container file lives.    *    * @param kvContainerData - KeyValueContainerData    * @return Path to the metadata directory    */
DECL|method|getMetadataDirectory ( KeyValueContainerData kvContainerData)
specifier|public
specifier|static
name|Path
name|getMetadataDirectory
parameter_list|(
name|KeyValueContainerData
name|kvContainerData
parameter_list|)
block|{
name|String
name|metadataPath
init|=
name|kvContainerData
operator|.
name|getMetadataPath
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|metadataPath
argument_list|)
expr_stmt|;
return|return
name|Paths
operator|.
name|get
argument_list|(
name|metadataPath
argument_list|)
return|;
block|}
block|}
end_class

end_unit

