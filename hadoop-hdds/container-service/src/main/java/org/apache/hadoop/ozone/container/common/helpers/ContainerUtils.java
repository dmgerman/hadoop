begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.helpers
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
name|common
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|impl
operator|.
name|ContainerManagerImpl
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
name|FileOutputStream
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
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FilenameUtils
operator|.
name|removeExtension
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
name|INVALID_ARGUMENT
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
name|UNABLE_TO_FIND_DATA_DIR
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
name|OzoneConsts
operator|.
name|CONTAINER_EXTENSION
import|;
end_import

begin_comment
comment|/**  * A set of helper functions to create proper responses.  */
end_comment

begin_class
DECL|class|ContainerUtils
specifier|public
specifier|final
class|class
name|ContainerUtils
block|{
DECL|method|ContainerUtils ()
specifier|private
name|ContainerUtils
parameter_list|()
block|{
comment|//never constructed.
block|}
comment|/**    * Returns a CreateContainer Response. This call is used by create and delete    * containers which have null success responses.    *    * @param msg Request    * @return Response.    */
specifier|public
specifier|static
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
DECL|method|getContainerResponse (ContainerProtos.ContainerCommandRequestProto msg)
name|getContainerResponse
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|msg
parameter_list|)
block|{
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
operator|.
name|Builder
name|builder
init|=
name|getContainerResponse
argument_list|(
name|msg
argument_list|,
name|ContainerProtos
operator|.
name|Result
operator|.
name|SUCCESS
argument_list|,
literal|""
argument_list|)
decl_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Returns a ReadContainer Response.    *    * @param msg Request    * @param containerData - data    * @return Response.    */
specifier|public
specifier|static
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
DECL|method|getReadContainerResponse (ContainerProtos.ContainerCommandRequestProto msg, ContainerData containerData)
name|getReadContainerResponse
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|msg
parameter_list|,
name|ContainerData
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
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
operator|.
name|Builder
name|builder
init|=
name|getContainerResponse
argument_list|(
name|msg
argument_list|,
name|ContainerProtos
operator|.
name|Result
operator|.
name|SUCCESS
argument_list|,
literal|""
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
comment|/**    * We found a command type but no associated payload for the command. Hence    * return malformed Command as response.    *    * @param msg - Protobuf message.    * @param result - result    * @param message - Error message.    * @return ContainerCommandResponseProto - MALFORMED_REQUEST.    */
specifier|public
specifier|static
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
operator|.
name|Builder
DECL|method|getContainerResponse (ContainerProtos.ContainerCommandRequestProto msg, ContainerProtos.Result result, String message)
name|getContainerResponse
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|msg
parameter_list|,
name|ContainerProtos
operator|.
name|Result
name|result
parameter_list|,
name|String
name|message
parameter_list|)
block|{
return|return
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|msg
operator|.
name|getCmdType
argument_list|()
argument_list|)
operator|.
name|setTraceID
argument_list|(
name|msg
operator|.
name|getTraceID
argument_list|()
argument_list|)
operator|.
name|setResult
argument_list|(
name|result
argument_list|)
operator|.
name|setMessage
argument_list|(
name|message
argument_list|)
return|;
block|}
comment|/**    * Logs the error and returns a response to the caller.    *    * @param log - Logger    * @param ex - Exception    * @param msg - Request Object    * @return Response    */
DECL|method|logAndReturnError ( Logger log, StorageContainerException ex, ContainerProtos.ContainerCommandRequestProto msg)
specifier|public
specifier|static
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
name|logAndReturnError
parameter_list|(
name|Logger
name|log
parameter_list|,
name|StorageContainerException
name|ex
parameter_list|,
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|msg
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Operation: {} : Trace ID: {} : Message: {} : Result: {}"
argument_list|,
name|msg
operator|.
name|getCmdType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|msg
operator|.
name|getTraceID
argument_list|()
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
operator|.
name|getResult
argument_list|()
operator|.
name|getValueDescriptor
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|getContainerResponse
argument_list|(
name|msg
argument_list|,
name|ex
operator|.
name|getResult
argument_list|()
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Logs the error and returns a response to the caller.    *    * @param log - Logger    * @param ex - Exception    * @param msg - Request Object    * @return Response    */
DECL|method|logAndReturnError ( Logger log, RuntimeException ex, ContainerProtos.ContainerCommandRequestProto msg)
specifier|public
specifier|static
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
name|logAndReturnError
parameter_list|(
name|Logger
name|log
parameter_list|,
name|RuntimeException
name|ex
parameter_list|,
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|msg
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Operation: {} : Trace ID: {} : Message: {} "
argument_list|,
name|msg
operator|.
name|getCmdType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|msg
operator|.
name|getTraceID
argument_list|()
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|getContainerResponse
argument_list|(
name|msg
argument_list|,
name|INVALID_ARGUMENT
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * We found a command type but no associated payload for the command. Hence    * return malformed Command as response.    *    * @param msg - Protobuf message.    * @return ContainerCommandResponseProto - MALFORMED_REQUEST.    */
specifier|public
specifier|static
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
DECL|method|malformedRequest (ContainerProtos.ContainerCommandRequestProto msg)
name|malformedRequest
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|msg
parameter_list|)
block|{
return|return
name|getContainerResponse
argument_list|(
name|msg
argument_list|,
name|ContainerProtos
operator|.
name|Result
operator|.
name|MALFORMED_REQUEST
argument_list|,
literal|"Cmd type does not match the payload."
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * We found a command type that is not supported yet.    *    * @param msg - Protobuf message.    * @return ContainerCommandResponseProto - MALFORMED_REQUEST.    */
specifier|public
specifier|static
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
DECL|method|unsupportedRequest (ContainerProtos.ContainerCommandRequestProto msg)
name|unsupportedRequest
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|msg
parameter_list|)
block|{
return|return
name|getContainerResponse
argument_list|(
name|msg
argument_list|,
name|ContainerProtos
operator|.
name|Result
operator|.
name|UNSUPPORTED_REQUEST
argument_list|,
literal|"Server does not support this command yet."
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * get containerName from a container file.    *    * @param containerFile - File    * @return Name of the container.    */
DECL|method|getContainerNameFromFile (File containerFile)
specifier|public
specifier|static
name|String
name|getContainerNameFromFile
parameter_list|(
name|File
name|containerFile
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerFile
argument_list|)
expr_stmt|;
return|return
name|Paths
operator|.
name|get
argument_list|(
name|containerFile
operator|.
name|getParent
argument_list|()
argument_list|)
operator|.
name|resolve
argument_list|(
name|removeExtension
argument_list|(
name|containerFile
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getContainerIDFromFile (File containerFile)
specifier|public
specifier|static
name|long
name|getContainerIDFromFile
parameter_list|(
name|File
name|containerFile
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerFile
argument_list|)
expr_stmt|;
name|String
name|containerID
init|=
name|getContainerNameFromFile
argument_list|(
name|containerFile
argument_list|)
decl_stmt|;
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|containerID
argument_list|)
return|;
block|}
comment|/**    * Verifies that this in indeed a new container.    *    * @param containerFile - Container File to verify    * @throws IOException    */
DECL|method|verifyIsNewContainer (File containerFile)
specifier|public
specifier|static
name|void
name|verifyIsNewContainer
parameter_list|(
name|File
name|containerFile
parameter_list|)
throws|throws
name|IOException
block|{
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ContainerManagerImpl
operator|.
name|class
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
name|log
operator|.
name|error
argument_list|(
literal|"container already exists on disk. File: {}"
argument_list|,
name|containerFile
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FileAlreadyExistsException
argument_list|(
literal|"container already exists on "
operator|+
literal|"disk."
argument_list|)
throw|;
block|}
name|File
name|parentPath
init|=
operator|new
name|File
argument_list|(
name|containerFile
operator|.
name|getParent
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|parentPath
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|parentPath
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unable to create parent path. Path: {}"
argument_list|,
name|parentPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create container directory."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|containerFile
operator|.
name|createNewFile
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"creation of a new container file failed. File: {}"
argument_list|,
name|containerFile
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"creation of a new container file failed."
argument_list|)
throw|;
block|}
block|}
DECL|method|getContainerDbFileName (String containerName)
specifier|public
specifier|static
name|String
name|getContainerDbFileName
parameter_list|(
name|String
name|containerName
parameter_list|)
block|{
return|return
name|containerName
operator|+
name|OzoneConsts
operator|.
name|DN_CONTAINER_DB
return|;
block|}
comment|/**    * creates a Metadata DB for the specified container.    *    * @param containerPath - Container Path.    * @throws IOException    */
DECL|method|createMetadata (Path containerPath, String containerName, Configuration conf)
specifier|public
specifier|static
name|Path
name|createMetadata
parameter_list|(
name|Path
name|containerPath
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
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ContainerManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerPath
argument_list|)
expr_stmt|;
name|Path
name|metadataPath
init|=
name|containerPath
operator|.
name|resolve
argument_list|(
name|OzoneConsts
operator|.
name|CONTAINER_META_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|metadataPath
operator|.
name|toFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unable to create directory for metadata storage. Path: {}"
argument_list|,
name|metadataPath
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
name|metadataPath
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
name|metadataPath
operator|.
name|resolve
argument_list|(
name|getContainerDbFileName
argument_list|(
name|containerName
argument_list|)
argument_list|)
operator|.
name|toFile
argument_list|()
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
name|Path
name|dataPath
init|=
name|containerPath
operator|.
name|resolve
argument_list|(
name|OzoneConsts
operator|.
name|CONTAINER_DATA_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dataPath
operator|.
name|toFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
comment|// If we failed to create data directory, we cleanup the
comment|// metadata directory completely. That is, we will delete the
comment|// whole directory including LevelDB file.
name|log
operator|.
name|error
argument_list|(
literal|"Unable to create directory for data storage. cleaning up the"
operator|+
literal|" container path: {} dataPath: {}"
argument_list|,
name|containerPath
argument_list|,
name|dataPath
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|containerPath
operator|.
name|toFile
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
name|dataPath
argument_list|)
throw|;
block|}
return|return
name|metadataPath
return|;
block|}
comment|/**    * Returns container file location.    *    * @param containerData - Data    * @param location - Root path    * @return Path    */
DECL|method|getContainerFile (ContainerData containerData, Path location)
specifier|public
specifier|static
name|File
name|getContainerFile
parameter_list|(
name|ContainerData
name|containerData
parameter_list|,
name|Path
name|location
parameter_list|)
block|{
return|return
name|location
operator|.
name|resolve
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|containerData
operator|.
name|getContainerID
argument_list|()
argument_list|)
operator|.
name|concat
argument_list|(
name|CONTAINER_EXTENSION
argument_list|)
argument_list|)
operator|.
name|toFile
argument_list|()
return|;
block|}
comment|/**    * Container metadata directory -- here is where the level DB lives.    *    * @param cData - cData.    * @return Path to the parent directory where the DB lives.    */
DECL|method|getMetadataDirectory (ContainerData cData)
specifier|public
specifier|static
name|Path
name|getMetadataDirectory
parameter_list|(
name|ContainerData
name|cData
parameter_list|)
block|{
name|Path
name|dbPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|cData
operator|.
name|getDBPath
argument_list|()
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|dbPath
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|dbPath
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
return|return
name|dbPath
operator|.
name|getParent
argument_list|()
return|;
block|}
comment|/**    * Returns the path where data or chunks live for a given container.    *    * @param cData - cData container    * @return - Path    * @throws StorageContainerException    */
DECL|method|getDataDirectory (ContainerData cData)
specifier|public
specifier|static
name|Path
name|getDataDirectory
parameter_list|(
name|ContainerData
name|cData
parameter_list|)
throws|throws
name|StorageContainerException
block|{
name|Path
name|path
init|=
name|getMetadataDirectory
argument_list|(
name|cData
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|Path
name|parentPath
init|=
name|path
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentPath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Unable to get Data directory."
operator|+
name|path
argument_list|,
name|UNABLE_TO_FIND_DATA_DIR
argument_list|)
throw|;
block|}
return|return
name|parentPath
operator|.
name|resolve
argument_list|(
name|OzoneConsts
operator|.
name|CONTAINER_DATA_PATH
argument_list|)
return|;
block|}
comment|/**    * remove Container if it is empty.    *<p/>    * There are three things we need to delete.    *<p/>    * 1. Container file and metadata file. 2. The Level DB file 3. The path that    * we created on the data location.    *    * @param containerData - Data of the container to remove.    * @param conf - configuration of the cluster.    * @param forceDelete - whether this container should be deleted forcibly.    * @throws IOException    */
DECL|method|removeContainer (ContainerData containerData, Configuration conf, boolean forceDelete)
specifier|public
specifier|static
name|void
name|removeContainer
parameter_list|(
name|ContainerData
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
name|Path
name|dbPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|containerData
operator|.
name|getDBPath
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
comment|// Delete the DB File.
name|FileUtils
operator|.
name|forceDelete
argument_list|(
name|dbPath
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
name|dbPath
operator|=
name|dbPath
operator|.
name|getParent
argument_list|()
expr_stmt|;
comment|// Delete all Metadata in the Data directories for this containers.
if|if
condition|(
name|dbPath
operator|!=
literal|null
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|dbPath
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
name|dbPath
operator|=
name|dbPath
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
comment|// now delete the container directory, this means that all key data dirs
comment|// will be removed too.
if|if
condition|(
name|dbPath
operator|!=
literal|null
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|dbPath
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Delete the container metadata from the metadata locations.
name|String
name|rootPath
init|=
name|getContainerNameFromFile
argument_list|(
operator|new
name|File
argument_list|(
name|containerData
operator|.
name|getContainerPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Path
name|containerPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|rootPath
operator|.
name|concat
argument_list|(
name|CONTAINER_EXTENSION
argument_list|)
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|forceDelete
argument_list|(
name|containerPath
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Persistent a {@link DatanodeDetails} to a local file.    *    * @throws IOException when read/write error occurs    */
DECL|method|writeDatanodeDetailsTo ( DatanodeDetails datanodeDetails, File path)
specifier|public
specifier|synchronized
specifier|static
name|void
name|writeDatanodeDetailsTo
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|File
name|path
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|path
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|path
operator|.
name|delete
argument_list|()
operator|||
operator|!
name|path
operator|.
name|createNewFile
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to overwrite the datanode ID file."
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|path
operator|.
name|getParentFile
argument_list|()
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|path
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create datanode ID directories."
argument_list|)
throw|;
block|}
block|}
try|try
init|(
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|path
argument_list|)
init|)
block|{
name|HddsProtos
operator|.
name|DatanodeDetailsProto
name|proto
init|=
name|datanodeDetails
operator|.
name|getProtoBufMessage
argument_list|()
decl_stmt|;
name|proto
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Read {@link DatanodeDetails} from a local ID file.    *    * @param path ID file local path    * @return {@link DatanodeDetails}    * @throws IOException If the id file is malformed or other I/O exceptions    */
DECL|method|readDatanodeDetailsFrom (File path)
specifier|public
specifier|synchronized
specifier|static
name|DatanodeDetails
name|readDatanodeDetailsFrom
parameter_list|(
name|File
name|path
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|path
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Datanode ID file not found."
argument_list|)
throw|;
block|}
try|try
init|(
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|path
argument_list|)
init|)
block|{
return|return
name|DatanodeDetails
operator|.
name|getFromProtoBuf
argument_list|(
name|HddsProtos
operator|.
name|DatanodeDetailsProto
operator|.
name|parseFrom
argument_list|(
name|in
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to parse DatanodeDetails from "
operator|+
name|path
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

