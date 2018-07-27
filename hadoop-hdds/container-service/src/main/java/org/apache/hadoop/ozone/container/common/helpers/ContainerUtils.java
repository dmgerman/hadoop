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
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|Result
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
import|import
name|org
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|Yaml
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
name|CONTAINER_CHECKSUM_ERROR
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
name|NO_SUCH_ALGORITHM
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
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ContainerData
operator|.
name|CHARSET_ENCODING
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
comment|/**    * Returns a Container Command Response Builder with the specified result    * and message.    * @param request requestProto message.    * @param result result of the command.    * @param message response message.    * @return ContainerCommand Response Builder.    */
specifier|public
specifier|static
name|ContainerCommandResponseProto
operator|.
name|Builder
DECL|method|getContainerCommandResponse ( ContainerCommandRequestProto request, Result result, String message)
name|getContainerCommandResponse
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|,
name|Result
name|result
parameter_list|,
name|String
name|message
parameter_list|)
block|{
return|return
name|ContainerCommandResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
operator|.
name|setTraceID
argument_list|(
name|request
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
comment|/**    * Returns a Container Command Response Builder. This call is used to build    * success responses. Calling function can add other fields to the response    * as required.    * @param request requestProto message.    * @return ContainerCommand Response Builder with result as SUCCESS.    */
DECL|method|getSuccessResponseBuilder ( ContainerCommandRequestProto request)
specifier|public
specifier|static
name|ContainerCommandResponseProto
operator|.
name|Builder
name|getSuccessResponseBuilder
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|)
block|{
return|return
name|ContainerCommandResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
operator|.
name|setTraceID
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
operator|.
name|setResult
argument_list|(
name|Result
operator|.
name|SUCCESS
argument_list|)
return|;
block|}
comment|/**    * Returns a Container Command Response. This call is used for creating null    * success responses.    * @param request requestProto message.    * @return ContainerCommand Response with result as SUCCESS.    */
DECL|method|getSuccessResponse ( ContainerCommandRequestProto request)
specifier|public
specifier|static
name|ContainerCommandResponseProto
name|getSuccessResponse
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|)
block|{
name|ContainerCommandResponseProto
operator|.
name|Builder
name|builder
init|=
name|getContainerCommandResponse
argument_list|(
name|request
argument_list|,
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
comment|/**    * We found a command type but no associated payload for the command. Hence    * return malformed Command as response.    *    * @param request - Protobuf message.    * @return ContainerCommandResponseProto - MALFORMED_REQUEST.    */
DECL|method|malformedRequest ( ContainerCommandRequestProto request)
specifier|public
specifier|static
name|ContainerCommandResponseProto
name|malformedRequest
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|)
block|{
return|return
name|getContainerCommandResponse
argument_list|(
name|request
argument_list|,
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
comment|/**    * We found a command type that is not supported yet.    *    * @param request - Protobuf message.    * @return ContainerCommandResponseProto - UNSUPPORTED_REQUEST.    */
DECL|method|unsupportedRequest ( ContainerCommandRequestProto request)
specifier|public
specifier|static
name|ContainerCommandResponseProto
name|unsupportedRequest
parameter_list|(
name|ContainerCommandRequestProto
name|request
parameter_list|)
block|{
return|return
name|getContainerCommandResponse
argument_list|(
name|request
argument_list|,
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
comment|/**    * Logs the error and returns a response to the caller.    *    * @param log - Logger    * @param ex - Exception    * @param request - Request Object    * @return Response    */
DECL|method|logAndReturnError ( Logger log, StorageContainerException ex, ContainerCommandRequestProto request)
specifier|public
specifier|static
name|ContainerCommandResponseProto
name|logAndReturnError
parameter_list|(
name|Logger
name|log
parameter_list|,
name|StorageContainerException
name|ex
parameter_list|,
name|ContainerCommandRequestProto
name|request
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Operation: {} : Trace ID: {} : Message: {} : Result: {}"
argument_list|,
name|request
operator|.
name|getCmdType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|request
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
name|getContainerCommandResponse
argument_list|(
name|request
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
comment|/**    * Verifies that this is indeed a new container.    *    * @param containerFile - Container File to verify    * @throws IOException    */
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
name|FileAlreadyExistsException
block|{
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ContainerSet
operator|.
name|class
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerFile
argument_list|,
literal|"containerFile Should not be "
operator|+
literal|"null"
argument_list|)
expr_stmt|;
if|if
condition|(
name|containerFile
operator|.
name|getParentFile
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Container already exists on disk. File: {}"
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
comment|/**    * Verify that the checksum stored in containerData is equal to the    * computed checksum.    * @param containerData    * @throws IOException    */
DECL|method|verifyChecksum (ContainerData containerData)
specifier|public
specifier|static
name|void
name|verifyChecksum
parameter_list|(
name|ContainerData
name|containerData
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|storedChecksum
init|=
name|containerData
operator|.
name|getChecksum
argument_list|()
decl_stmt|;
name|Yaml
name|yaml
init|=
name|ContainerDataYaml
operator|.
name|getYamlForContainerType
argument_list|(
name|containerData
operator|.
name|getContainerType
argument_list|()
argument_list|)
decl_stmt|;
name|containerData
operator|.
name|computeAndSetChecksum
argument_list|(
name|yaml
argument_list|)
expr_stmt|;
name|String
name|computedChecksum
init|=
name|containerData
operator|.
name|getChecksum
argument_list|()
decl_stmt|;
if|if
condition|(
name|storedChecksum
operator|==
literal|null
operator|||
operator|!
name|storedChecksum
operator|.
name|equals
argument_list|(
name|computedChecksum
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Container checksum error for "
operator|+
literal|"ContainerID: "
operator|+
name|containerData
operator|.
name|getContainerID
argument_list|()
operator|+
literal|". "
operator|+
literal|"\nStored Checksum: "
operator|+
name|storedChecksum
operator|+
literal|"\nExpected Checksum: "
operator|+
name|computedChecksum
argument_list|,
name|CONTAINER_CHECKSUM_ERROR
argument_list|)
throw|;
block|}
block|}
comment|/**    * Return the SHA-256 chesksum of the containerData.    * @param containerDataYamlStr ContainerData as a Yaml String    * @return Checksum of the container data    * @throws StorageContainerException    */
DECL|method|getChecksum (String containerDataYamlStr)
specifier|public
specifier|static
name|String
name|getChecksum
parameter_list|(
name|String
name|containerDataYamlStr
parameter_list|)
throws|throws
name|StorageContainerException
block|{
name|MessageDigest
name|sha
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
name|sha
operator|.
name|update
argument_list|(
name|containerDataYamlStr
operator|.
name|getBytes
argument_list|(
name|CHARSET_ENCODING
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|DigestUtils
operator|.
name|sha256Hex
argument_list|(
name|sha
operator|.
name|digest
argument_list|()
argument_list|)
return|;
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
block|}
comment|/**    * Get the .container file from the containerBaseDir    * @param containerBaseDir container base directory. The name of this    *                         directory is same as the containerID    * @return the .container file    */
DECL|method|getContainerFile (File containerBaseDir)
specifier|public
specifier|static
name|File
name|getContainerFile
parameter_list|(
name|File
name|containerBaseDir
parameter_list|)
block|{
comment|// Container file layout is
comment|// .../<<containerID>>/metadata/<<containerID>>.container
name|String
name|containerFilePath
init|=
name|OzoneConsts
operator|.
name|CONTAINER_META_PATH
operator|+
name|File
operator|.
name|separator
operator|+
name|getContainerID
argument_list|(
name|containerBaseDir
argument_list|)
operator|+
name|OzoneConsts
operator|.
name|CONTAINER_EXTENSION
decl_stmt|;
return|return
operator|new
name|File
argument_list|(
name|containerBaseDir
argument_list|,
name|containerFilePath
argument_list|)
return|;
block|}
comment|/**    * ContainerID can be decoded from the container base directory name    */
DECL|method|getContainerID (File containerBaseDir)
specifier|public
specifier|static
name|long
name|getContainerID
parameter_list|(
name|File
name|containerBaseDir
parameter_list|)
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|containerBaseDir
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

