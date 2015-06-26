begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|mapred
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
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
name|util
operator|.
name|EnumSet
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|CreateFlag
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
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|FileChecksum
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
name|FileStatus
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
name|FileSystem
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
name|Options
operator|.
name|ChecksumOpt
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
name|Path
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
name|permission
operator|.
name|FsPermission
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
name|mapreduce
operator|.
name|Mapper
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
name|tools
operator|.
name|DistCpConstants
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
name|tools
operator|.
name|DistCpOptions
operator|.
name|FileAttribute
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
name|tools
operator|.
name|mapred
operator|.
name|CopyMapper
operator|.
name|FileAction
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
name|tools
operator|.
name|util
operator|.
name|DistCpUtils
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
name|tools
operator|.
name|util
operator|.
name|RetriableCommand
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
name|tools
operator|.
name|util
operator|.
name|ThrottledInputStream
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
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  * This class extends RetriableCommand to implement the copy of files,  * with retries on failure.  */
end_comment

begin_class
DECL|class|RetriableFileCopyCommand
specifier|public
class|class
name|RetriableFileCopyCommand
extends|extends
name|RetriableCommand
block|{
DECL|field|LOG
specifier|private
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RetriableFileCopyCommand
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BUFFER_SIZE
specifier|private
specifier|static
name|int
name|BUFFER_SIZE
init|=
literal|8
operator|*
literal|1024
decl_stmt|;
DECL|field|skipCrc
specifier|private
name|boolean
name|skipCrc
init|=
literal|false
decl_stmt|;
DECL|field|action
specifier|private
name|FileAction
name|action
decl_stmt|;
comment|/**    * Constructor, taking a description of the action.    * @param description Verbose description of the copy operation.    */
DECL|method|RetriableFileCopyCommand (String description, FileAction action)
specifier|public
name|RetriableFileCopyCommand
parameter_list|(
name|String
name|description
parameter_list|,
name|FileAction
name|action
parameter_list|)
block|{
name|super
argument_list|(
name|description
argument_list|)
expr_stmt|;
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
block|}
comment|/**    * Create a RetriableFileCopyCommand.    *    * @param skipCrc Whether to skip the crc check.    * @param description A verbose description of the copy operation.    * @param action We should overwrite the target file or append new data to it.    */
DECL|method|RetriableFileCopyCommand (boolean skipCrc, String description, FileAction action)
specifier|public
name|RetriableFileCopyCommand
parameter_list|(
name|boolean
name|skipCrc
parameter_list|,
name|String
name|description
parameter_list|,
name|FileAction
name|action
parameter_list|)
block|{
name|this
argument_list|(
name|description
argument_list|,
name|action
argument_list|)
expr_stmt|;
name|this
operator|.
name|skipCrc
operator|=
name|skipCrc
expr_stmt|;
block|}
comment|/**    * Implementation of RetriableCommand::doExecute().    * This is the actual copy-implementation.    * @param arguments Argument-list to the command.    * @return Number of bytes copied.    * @throws Exception    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|doExecute (Object... arguments)
specifier|protected
name|Object
name|doExecute
parameter_list|(
name|Object
modifier|...
name|arguments
parameter_list|)
throws|throws
name|Exception
block|{
assert|assert
name|arguments
operator|.
name|length
operator|==
literal|4
operator|:
literal|"Unexpected argument list."
assert|;
name|FileStatus
name|source
init|=
operator|(
name|FileStatus
operator|)
name|arguments
index|[
literal|0
index|]
decl_stmt|;
assert|assert
operator|!
name|source
operator|.
name|isDirectory
argument_list|()
operator|:
literal|"Unexpected file-status. Expected file."
assert|;
name|Path
name|target
init|=
operator|(
name|Path
operator|)
name|arguments
index|[
literal|1
index|]
decl_stmt|;
name|Mapper
operator|.
name|Context
name|context
init|=
operator|(
name|Mapper
operator|.
name|Context
operator|)
name|arguments
index|[
literal|2
index|]
decl_stmt|;
name|EnumSet
argument_list|<
name|FileAttribute
argument_list|>
name|fileAttributes
init|=
operator|(
name|EnumSet
argument_list|<
name|FileAttribute
argument_list|>
operator|)
name|arguments
index|[
literal|3
index|]
decl_stmt|;
return|return
name|doCopy
argument_list|(
name|source
argument_list|,
name|target
argument_list|,
name|context
argument_list|,
name|fileAttributes
argument_list|)
return|;
block|}
DECL|method|doCopy (FileStatus sourceFileStatus, Path target, Mapper.Context context, EnumSet<FileAttribute> fileAttributes)
specifier|private
name|long
name|doCopy
parameter_list|(
name|FileStatus
name|sourceFileStatus
parameter_list|,
name|Path
name|target
parameter_list|,
name|Mapper
operator|.
name|Context
name|context
parameter_list|,
name|EnumSet
argument_list|<
name|FileAttribute
argument_list|>
name|fileAttributes
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|boolean
name|toAppend
init|=
name|action
operator|==
name|FileAction
operator|.
name|APPEND
decl_stmt|;
name|Path
name|targetPath
init|=
name|toAppend
condition|?
name|target
else|:
name|getTmpFile
argument_list|(
name|target
argument_list|,
name|context
argument_list|)
decl_stmt|;
specifier|final
name|Configuration
name|configuration
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|FileSystem
name|targetFS
init|=
name|target
operator|.
name|getFileSystem
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Copying "
operator|+
name|sourceFileStatus
operator|.
name|getPath
argument_list|()
operator|+
literal|" to "
operator|+
name|target
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Target file path: "
operator|+
name|targetPath
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Path
name|sourcePath
init|=
name|sourceFileStatus
operator|.
name|getPath
argument_list|()
decl_stmt|;
specifier|final
name|FileSystem
name|sourceFS
init|=
name|sourcePath
operator|.
name|getFileSystem
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
specifier|final
name|FileChecksum
name|sourceChecksum
init|=
name|fileAttributes
operator|.
name|contains
argument_list|(
name|FileAttribute
operator|.
name|CHECKSUMTYPE
argument_list|)
condition|?
name|sourceFS
operator|.
name|getFileChecksum
argument_list|(
name|sourcePath
argument_list|)
else|:
literal|null
decl_stmt|;
specifier|final
name|long
name|offset
init|=
name|action
operator|==
name|FileAction
operator|.
name|APPEND
condition|?
name|targetFS
operator|.
name|getFileStatus
argument_list|(
name|target
argument_list|)
operator|.
name|getLen
argument_list|()
else|:
literal|0
decl_stmt|;
name|long
name|bytesRead
init|=
name|copyToFile
argument_list|(
name|targetPath
argument_list|,
name|targetFS
argument_list|,
name|sourceFileStatus
argument_list|,
name|offset
argument_list|,
name|context
argument_list|,
name|fileAttributes
argument_list|,
name|sourceChecksum
argument_list|)
decl_stmt|;
name|compareFileLengths
argument_list|(
name|sourceFileStatus
argument_list|,
name|targetPath
argument_list|,
name|configuration
argument_list|,
name|bytesRead
operator|+
name|offset
argument_list|)
expr_stmt|;
comment|//At this point, src&dest lengths are same. if length==0, we skip checksum
if|if
condition|(
operator|(
name|bytesRead
operator|!=
literal|0
operator|)
operator|&&
operator|(
operator|!
name|skipCrc
operator|)
condition|)
block|{
name|compareCheckSums
argument_list|(
name|sourceFS
argument_list|,
name|sourceFileStatus
operator|.
name|getPath
argument_list|()
argument_list|,
name|sourceChecksum
argument_list|,
name|targetFS
argument_list|,
name|targetPath
argument_list|)
expr_stmt|;
block|}
comment|// it's not append case, thus we first write to a temporary file, rename
comment|// it to the target path.
if|if
condition|(
operator|!
name|toAppend
condition|)
block|{
name|promoteTmpToTarget
argument_list|(
name|targetPath
argument_list|,
name|target
argument_list|,
name|targetFS
argument_list|)
expr_stmt|;
block|}
return|return
name|bytesRead
return|;
block|}
finally|finally
block|{
comment|// note that for append case, it is possible that we append partial data
comment|// and then fail. In that case, for the next retry, we either reuse the
comment|// partial appended data if it is good or we overwrite the whole file
if|if
condition|(
operator|!
name|toAppend
operator|&&
name|targetFS
operator|.
name|exists
argument_list|(
name|targetPath
argument_list|)
condition|)
block|{
name|targetFS
operator|.
name|delete
argument_list|(
name|targetPath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * @return the checksum spec of the source checksum if checksum type should be    *         preserved    */
DECL|method|getChecksumOpt (EnumSet<FileAttribute> fileAttributes, FileChecksum sourceChecksum)
specifier|private
name|ChecksumOpt
name|getChecksumOpt
parameter_list|(
name|EnumSet
argument_list|<
name|FileAttribute
argument_list|>
name|fileAttributes
parameter_list|,
name|FileChecksum
name|sourceChecksum
parameter_list|)
block|{
if|if
condition|(
name|fileAttributes
operator|.
name|contains
argument_list|(
name|FileAttribute
operator|.
name|CHECKSUMTYPE
argument_list|)
operator|&&
name|sourceChecksum
operator|!=
literal|null
condition|)
block|{
return|return
name|sourceChecksum
operator|.
name|getChecksumOpt
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|copyToFile (Path targetPath, FileSystem targetFS, FileStatus sourceFileStatus, long sourceOffset, Mapper.Context context, EnumSet<FileAttribute> fileAttributes, final FileChecksum sourceChecksum)
specifier|private
name|long
name|copyToFile
parameter_list|(
name|Path
name|targetPath
parameter_list|,
name|FileSystem
name|targetFS
parameter_list|,
name|FileStatus
name|sourceFileStatus
parameter_list|,
name|long
name|sourceOffset
parameter_list|,
name|Mapper
operator|.
name|Context
name|context
parameter_list|,
name|EnumSet
argument_list|<
name|FileAttribute
argument_list|>
name|fileAttributes
parameter_list|,
specifier|final
name|FileChecksum
name|sourceChecksum
parameter_list|)
throws|throws
name|IOException
block|{
name|FsPermission
name|permission
init|=
name|FsPermission
operator|.
name|getFileDefault
argument_list|()
operator|.
name|applyUMask
argument_list|(
name|FsPermission
operator|.
name|getUMask
argument_list|(
name|targetFS
operator|.
name|getConf
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|OutputStream
name|outStream
decl_stmt|;
if|if
condition|(
name|action
operator|==
name|FileAction
operator|.
name|OVERWRITE
condition|)
block|{
specifier|final
name|short
name|repl
init|=
name|getReplicationFactor
argument_list|(
name|fileAttributes
argument_list|,
name|sourceFileStatus
argument_list|,
name|targetFS
argument_list|,
name|targetPath
argument_list|)
decl_stmt|;
specifier|final
name|long
name|blockSize
init|=
name|getBlockSize
argument_list|(
name|fileAttributes
argument_list|,
name|sourceFileStatus
argument_list|,
name|targetFS
argument_list|,
name|targetPath
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|targetFS
operator|.
name|create
argument_list|(
name|targetPath
argument_list|,
name|permission
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|,
name|CreateFlag
operator|.
name|OVERWRITE
argument_list|)
argument_list|,
name|BUFFER_SIZE
argument_list|,
name|repl
argument_list|,
name|blockSize
argument_list|,
name|context
argument_list|,
name|getChecksumOpt
argument_list|(
name|fileAttributes
argument_list|,
name|sourceChecksum
argument_list|)
argument_list|)
decl_stmt|;
name|outStream
operator|=
operator|new
name|BufferedOutputStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|outStream
operator|=
operator|new
name|BufferedOutputStream
argument_list|(
name|targetFS
operator|.
name|append
argument_list|(
name|targetPath
argument_list|,
name|BUFFER_SIZE
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|copyBytes
argument_list|(
name|sourceFileStatus
argument_list|,
name|sourceOffset
argument_list|,
name|outStream
argument_list|,
name|BUFFER_SIZE
argument_list|,
name|context
argument_list|)
return|;
block|}
DECL|method|compareFileLengths (FileStatus sourceFileStatus, Path target, Configuration configuration, long targetLen)
specifier|private
name|void
name|compareFileLengths
parameter_list|(
name|FileStatus
name|sourceFileStatus
parameter_list|,
name|Path
name|target
parameter_list|,
name|Configuration
name|configuration
parameter_list|,
name|long
name|targetLen
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|sourcePath
init|=
name|sourceFileStatus
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|sourcePath
operator|.
name|getFileSystem
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
if|if
condition|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|sourcePath
argument_list|)
operator|.
name|getLen
argument_list|()
operator|!=
name|targetLen
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mismatch in length of source:"
operator|+
name|sourcePath
operator|+
literal|" and target:"
operator|+
name|target
argument_list|)
throw|;
block|}
DECL|method|compareCheckSums (FileSystem sourceFS, Path source, FileChecksum sourceChecksum, FileSystem targetFS, Path target)
specifier|private
name|void
name|compareCheckSums
parameter_list|(
name|FileSystem
name|sourceFS
parameter_list|,
name|Path
name|source
parameter_list|,
name|FileChecksum
name|sourceChecksum
parameter_list|,
name|FileSystem
name|targetFS
parameter_list|,
name|Path
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|DistCpUtils
operator|.
name|checksumsAreEqual
argument_list|(
name|sourceFS
argument_list|,
name|source
argument_list|,
name|sourceChecksum
argument_list|,
name|targetFS
argument_list|,
name|target
argument_list|)
condition|)
block|{
name|StringBuilder
name|errorMessage
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Check-sum mismatch between "
argument_list|)
operator|.
name|append
argument_list|(
name|source
argument_list|)
operator|.
name|append
argument_list|(
literal|" and "
argument_list|)
operator|.
name|append
argument_list|(
name|target
argument_list|)
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceFS
operator|.
name|getFileStatus
argument_list|(
name|source
argument_list|)
operator|.
name|getBlockSize
argument_list|()
operator|!=
name|targetFS
operator|.
name|getFileStatus
argument_list|(
name|target
argument_list|)
operator|.
name|getBlockSize
argument_list|()
condition|)
block|{
name|errorMessage
operator|.
name|append
argument_list|(
literal|" Source and target differ in block-size."
argument_list|)
operator|.
name|append
argument_list|(
literal|" Use -pb to preserve block-sizes during copy."
argument_list|)
operator|.
name|append
argument_list|(
literal|" Alternatively, skip checksum-checks altogether, using -skipCrc."
argument_list|)
operator|.
name|append
argument_list|(
literal|" (NOTE: By skipping checksums, one runs the risk of masking data-corruption during file-transfer.)"
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|errorMessage
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|//If target file exists and unable to delete target - fail
comment|//If target doesn't exist and unable to create parent folder - fail
comment|//If target is successfully deleted and parent exists, if rename fails - fail
DECL|method|promoteTmpToTarget (Path tmpTarget, Path target, FileSystem fs)
specifier|private
name|void
name|promoteTmpToTarget
parameter_list|(
name|Path
name|tmpTarget
parameter_list|,
name|Path
name|target
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|(
name|fs
operator|.
name|exists
argument_list|(
name|target
argument_list|)
operator|&&
operator|!
name|fs
operator|.
name|delete
argument_list|(
name|target
argument_list|,
literal|false
argument_list|)
operator|)
operator|||
operator|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|target
operator|.
name|getParent
argument_list|()
argument_list|)
operator|&&
operator|!
name|fs
operator|.
name|mkdirs
argument_list|(
name|target
operator|.
name|getParent
argument_list|()
argument_list|)
operator|)
operator|||
operator|!
name|fs
operator|.
name|rename
argument_list|(
name|tmpTarget
argument_list|,
name|target
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to promote tmp-file:"
operator|+
name|tmpTarget
operator|+
literal|" to: "
operator|+
name|target
argument_list|)
throw|;
block|}
block|}
DECL|method|getTmpFile (Path target, Mapper.Context context)
specifier|private
name|Path
name|getTmpFile
parameter_list|(
name|Path
name|target
parameter_list|,
name|Mapper
operator|.
name|Context
name|context
parameter_list|)
block|{
name|Path
name|targetWorkPath
init|=
operator|new
name|Path
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|get
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_TARGET_WORK_PATH
argument_list|)
argument_list|)
decl_stmt|;
name|Path
name|root
init|=
name|target
operator|.
name|equals
argument_list|(
name|targetWorkPath
argument_list|)
condition|?
name|targetWorkPath
operator|.
name|getParent
argument_list|()
else|:
name|targetWorkPath
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating temp file: "
operator|+
operator|new
name|Path
argument_list|(
name|root
argument_list|,
literal|".distcp.tmp."
operator|+
name|context
operator|.
name|getTaskAttemptID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|Path
argument_list|(
name|root
argument_list|,
literal|".distcp.tmp."
operator|+
name|context
operator|.
name|getTaskAttemptID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|copyBytes (FileStatus sourceFileStatus, long sourceOffset, OutputStream outStream, int bufferSize, Mapper.Context context)
name|long
name|copyBytes
parameter_list|(
name|FileStatus
name|sourceFileStatus
parameter_list|,
name|long
name|sourceOffset
parameter_list|,
name|OutputStream
name|outStream
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|Mapper
operator|.
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|source
init|=
name|sourceFileStatus
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
name|bufferSize
index|]
decl_stmt|;
name|ThrottledInputStream
name|inStream
init|=
literal|null
decl_stmt|;
name|long
name|totalBytesRead
init|=
literal|0
decl_stmt|;
try|try
block|{
name|inStream
operator|=
name|getInputStream
argument_list|(
name|source
argument_list|,
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|bytesRead
init|=
name|readBytes
argument_list|(
name|inStream
argument_list|,
name|buf
argument_list|,
name|sourceOffset
argument_list|)
decl_stmt|;
while|while
condition|(
name|bytesRead
operator|>=
literal|0
condition|)
block|{
name|totalBytesRead
operator|+=
name|bytesRead
expr_stmt|;
if|if
condition|(
name|action
operator|==
name|FileAction
operator|.
name|APPEND
condition|)
block|{
name|sourceOffset
operator|+=
name|bytesRead
expr_stmt|;
block|}
name|outStream
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|bytesRead
argument_list|)
expr_stmt|;
name|updateContextStatus
argument_list|(
name|totalBytesRead
argument_list|,
name|context
argument_list|,
name|sourceFileStatus
argument_list|)
expr_stmt|;
name|bytesRead
operator|=
name|readBytes
argument_list|(
name|inStream
argument_list|,
name|buf
argument_list|,
name|sourceOffset
argument_list|)
expr_stmt|;
block|}
name|outStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|outStream
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|outStream
argument_list|,
name|inStream
argument_list|)
expr_stmt|;
block|}
return|return
name|totalBytesRead
return|;
block|}
DECL|method|updateContextStatus (long totalBytesRead, Mapper.Context context, FileStatus sourceFileStatus)
specifier|private
name|void
name|updateContextStatus
parameter_list|(
name|long
name|totalBytesRead
parameter_list|,
name|Mapper
operator|.
name|Context
name|context
parameter_list|,
name|FileStatus
name|sourceFileStatus
parameter_list|)
block|{
name|StringBuilder
name|message
init|=
operator|new
name|StringBuilder
argument_list|(
name|DistCpUtils
operator|.
name|getFormatter
argument_list|()
operator|.
name|format
argument_list|(
name|totalBytesRead
operator|*
literal|100.0f
operator|/
name|sourceFileStatus
operator|.
name|getLen
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|"% "
argument_list|)
operator|.
name|append
argument_list|(
name|description
argument_list|)
operator|.
name|append
argument_list|(
literal|" ["
argument_list|)
operator|.
name|append
argument_list|(
name|DistCpUtils
operator|.
name|getStringDescriptionFor
argument_list|(
name|totalBytesRead
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|DistCpUtils
operator|.
name|getStringDescriptionFor
argument_list|(
name|sourceFileStatus
operator|.
name|getLen
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
name|context
operator|.
name|setStatus
argument_list|(
name|message
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|readBytes (ThrottledInputStream inStream, byte buf[], long position)
specifier|private
specifier|static
name|int
name|readBytes
parameter_list|(
name|ThrottledInputStream
name|inStream
parameter_list|,
name|byte
name|buf
index|[]
parameter_list|,
name|long
name|position
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|position
operator|==
literal|0
condition|)
block|{
return|return
name|inStream
operator|.
name|read
argument_list|(
name|buf
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|inStream
operator|.
name|read
argument_list|(
name|position
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CopyReadException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getInputStream (Path path, Configuration conf)
specifier|private
specifier|static
name|ThrottledInputStream
name|getInputStream
parameter_list|(
name|Path
name|path
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|FileSystem
name|fs
init|=
name|path
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|float
name|bandwidthMB
init|=
name|conf
operator|.
name|getFloat
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_BANDWIDTH_MB
argument_list|,
name|DistCpConstants
operator|.
name|DEFAULT_BANDWIDTH_MB
argument_list|)
decl_stmt|;
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
operator|new
name|ThrottledInputStream
argument_list|(
name|in
argument_list|,
name|bandwidthMB
operator|*
literal|1024
operator|*
literal|1024
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
name|CopyReadException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getReplicationFactor ( EnumSet<FileAttribute> fileAttributes, FileStatus sourceFile, FileSystem targetFS, Path tmpTargetPath)
specifier|private
specifier|static
name|short
name|getReplicationFactor
parameter_list|(
name|EnumSet
argument_list|<
name|FileAttribute
argument_list|>
name|fileAttributes
parameter_list|,
name|FileStatus
name|sourceFile
parameter_list|,
name|FileSystem
name|targetFS
parameter_list|,
name|Path
name|tmpTargetPath
parameter_list|)
block|{
return|return
name|fileAttributes
operator|.
name|contains
argument_list|(
name|FileAttribute
operator|.
name|REPLICATION
argument_list|)
condition|?
name|sourceFile
operator|.
name|getReplication
argument_list|()
else|:
name|targetFS
operator|.
name|getDefaultReplication
argument_list|(
name|tmpTargetPath
argument_list|)
return|;
block|}
comment|/**    * @return the block size of the source file if we need to preserve either    *         the block size or the checksum type. Otherwise the default block    *         size of the target FS.    */
DECL|method|getBlockSize ( EnumSet<FileAttribute> fileAttributes, FileStatus sourceFile, FileSystem targetFS, Path tmpTargetPath)
specifier|private
specifier|static
name|long
name|getBlockSize
parameter_list|(
name|EnumSet
argument_list|<
name|FileAttribute
argument_list|>
name|fileAttributes
parameter_list|,
name|FileStatus
name|sourceFile
parameter_list|,
name|FileSystem
name|targetFS
parameter_list|,
name|Path
name|tmpTargetPath
parameter_list|)
block|{
name|boolean
name|preserve
init|=
name|fileAttributes
operator|.
name|contains
argument_list|(
name|FileAttribute
operator|.
name|BLOCKSIZE
argument_list|)
operator|||
name|fileAttributes
operator|.
name|contains
argument_list|(
name|FileAttribute
operator|.
name|CHECKSUMTYPE
argument_list|)
decl_stmt|;
return|return
name|preserve
condition|?
name|sourceFile
operator|.
name|getBlockSize
argument_list|()
else|:
name|targetFS
operator|.
name|getDefaultBlockSize
argument_list|(
name|tmpTargetPath
argument_list|)
return|;
block|}
comment|/**    * Special subclass of IOException. This is used to distinguish read-operation    * failures from other kinds of IOExceptions.    * The failure to read from source is dealt with specially, in the CopyMapper.    * Such failures may be skipped if the DistCpOptions indicate so.    * Write failures are intolerable, and amount to CopyMapper failure.    */
DECL|class|CopyReadException
specifier|public
specifier|static
class|class
name|CopyReadException
extends|extends
name|IOException
block|{
DECL|method|CopyReadException (Throwable rootCause)
specifier|public
name|CopyReadException
parameter_list|(
name|Throwable
name|rootCause
parameter_list|)
block|{
name|super
argument_list|(
name|rootCause
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

