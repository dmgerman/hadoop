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
name|FileNotFoundException
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
name|lang
operator|.
name|exception
operator|.
name|ExceptionUtils
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
name|io
operator|.
name|Text
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
name|CopyListingFileStatus
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
name|DistCpOptionSwitch
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
name|RetriableFileCopyCommand
operator|.
name|CopyReadException
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * Mapper class that executes the DistCp copy operation.  * Implements the o.a.h.mapreduce.Mapper interface.  */
end_comment

begin_class
DECL|class|CopyMapper
specifier|public
class|class
name|CopyMapper
extends|extends
name|Mapper
argument_list|<
name|Text
argument_list|,
name|CopyListingFileStatus
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|>
block|{
comment|/**    * Hadoop counters for the DistCp CopyMapper.    * (These have been kept identical to the old DistCp,    * for backward compatibility.)    */
DECL|enum|Counter
specifier|public
specifier|static
enum|enum
name|Counter
block|{
DECL|enumConstant|COPY
name|COPY
block|,
comment|// Number of files received by the mapper for copy.
DECL|enumConstant|SKIP
name|SKIP
block|,
comment|// Number of files skipped.
DECL|enumConstant|FAIL
name|FAIL
block|,
comment|// Number of files that failed to be copied.
DECL|enumConstant|BYTESCOPIED
name|BYTESCOPIED
block|,
comment|// Number of bytes actually copied by the copy-mapper, total.
DECL|enumConstant|BYTESEXPECTED
name|BYTESEXPECTED
block|,
comment|// Number of bytes expected to be copied.
DECL|enumConstant|BYTESFAILED
name|BYTESFAILED
block|,
comment|// Number of bytes that failed to be copied.
DECL|enumConstant|BYTESSKIPPED
name|BYTESSKIPPED
block|,
comment|// Number of bytes that were skipped from copy.
DECL|enumConstant|SLEEP_TIME_MS
name|SLEEP_TIME_MS
block|,
comment|// Time map slept while trying to honor bandwidth cap.
DECL|enumConstant|BANDWIDTH_IN_BYTES
name|BANDWIDTH_IN_BYTES
block|,
comment|// Effective transfer rate in B/s.
block|}
comment|/**    * Indicate the action for each file    */
DECL|enum|FileAction
specifier|static
enum|enum
name|FileAction
block|{
DECL|enumConstant|SKIP
name|SKIP
block|,
comment|// Skip copying the file since it's already in the target FS
DECL|enumConstant|APPEND
name|APPEND
block|,
comment|// Only need to append new data to the file in the target FS
DECL|enumConstant|OVERWRITE
name|OVERWRITE
block|,
comment|// Overwrite the whole file
block|}
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
name|CopyMapper
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|syncFolders
specifier|private
name|boolean
name|syncFolders
init|=
literal|false
decl_stmt|;
DECL|field|ignoreFailures
specifier|private
name|boolean
name|ignoreFailures
init|=
literal|false
decl_stmt|;
DECL|field|skipCrc
specifier|private
name|boolean
name|skipCrc
init|=
literal|false
decl_stmt|;
DECL|field|overWrite
specifier|private
name|boolean
name|overWrite
init|=
literal|false
decl_stmt|;
DECL|field|append
specifier|private
name|boolean
name|append
init|=
literal|false
decl_stmt|;
DECL|field|preserve
specifier|private
name|EnumSet
argument_list|<
name|FileAttribute
argument_list|>
name|preserve
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|FileAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|targetFS
specifier|private
name|FileSystem
name|targetFS
init|=
literal|null
decl_stmt|;
DECL|field|targetWorkPath
specifier|private
name|Path
name|targetWorkPath
init|=
literal|null
decl_stmt|;
DECL|field|startEpoch
specifier|private
name|long
name|startEpoch
decl_stmt|;
DECL|field|totalBytesCopied
specifier|private
name|long
name|totalBytesCopied
init|=
literal|0
decl_stmt|;
comment|/**    * Implementation of the Mapper::setup() method. This extracts the DistCp-    * options specified in the Job's configuration, to set up the Job.    * @param context Mapper's context.    * @throws IOException On IO failure.    * @throws InterruptedException If the job is interrupted.    */
annotation|@
name|Override
DECL|method|setup (Context context)
specifier|public
name|void
name|setup
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|conf
operator|=
name|context
operator|.
name|getConfiguration
argument_list|()
expr_stmt|;
name|syncFolders
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|DistCpOptionSwitch
operator|.
name|SYNC_FOLDERS
operator|.
name|getConfigLabel
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ignoreFailures
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|DistCpOptionSwitch
operator|.
name|IGNORE_FAILURES
operator|.
name|getConfigLabel
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|skipCrc
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|DistCpOptionSwitch
operator|.
name|SKIP_CRC
operator|.
name|getConfigLabel
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|overWrite
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|DistCpOptionSwitch
operator|.
name|OVERWRITE
operator|.
name|getConfigLabel
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|append
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|DistCpOptionSwitch
operator|.
name|APPEND
operator|.
name|getConfigLabel
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|preserve
operator|=
name|DistCpUtils
operator|.
name|unpackAttributes
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|DistCpOptionSwitch
operator|.
name|PRESERVE_STATUS
operator|.
name|getConfigLabel
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|targetWorkPath
operator|=
operator|new
name|Path
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_TARGET_WORK_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|targetFinalPath
init|=
operator|new
name|Path
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_TARGET_FINAL_PATH
argument_list|)
argument_list|)
decl_stmt|;
name|targetFS
operator|=
name|targetFinalPath
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|targetFS
operator|.
name|exists
argument_list|(
name|targetFinalPath
argument_list|)
operator|&&
name|targetFS
operator|.
name|isFile
argument_list|(
name|targetFinalPath
argument_list|)
condition|)
block|{
name|overWrite
operator|=
literal|true
expr_stmt|;
comment|// When target is an existing file, overwrite it.
block|}
name|startEpoch
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
comment|/**    * Implementation of the Mapper::map(). Does the copy.    * @param relPath The target path.    * @param sourceFileStatus The source path.    * @throws IOException    * @throws InterruptedException    */
annotation|@
name|Override
DECL|method|map (Text relPath, CopyListingFileStatus sourceFileStatus, Context context)
specifier|public
name|void
name|map
parameter_list|(
name|Text
name|relPath
parameter_list|,
name|CopyListingFileStatus
name|sourceFileStatus
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Path
name|sourcePath
init|=
name|sourceFileStatus
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"DistCpMapper::map(): Received "
operator|+
name|sourcePath
operator|+
literal|", "
operator|+
name|relPath
argument_list|)
expr_stmt|;
name|Path
name|target
init|=
operator|new
name|Path
argument_list|(
name|targetWorkPath
operator|.
name|makeQualified
argument_list|(
name|targetFS
operator|.
name|getUri
argument_list|()
argument_list|,
name|targetFS
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
operator|+
name|relPath
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|EnumSet
argument_list|<
name|DistCpOptions
operator|.
name|FileAttribute
argument_list|>
name|fileAttributes
init|=
name|getFileAttributeSettings
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|preserveRawXattrs
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_PRESERVE_RAWXATTRS
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|String
name|description
init|=
literal|"Copying "
operator|+
name|sourcePath
operator|+
literal|" to "
operator|+
name|target
decl_stmt|;
name|context
operator|.
name|setStatus
argument_list|(
name|description
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|description
argument_list|)
expr_stmt|;
try|try
block|{
name|CopyListingFileStatus
name|sourceCurrStatus
decl_stmt|;
name|FileSystem
name|sourceFS
decl_stmt|;
try|try
block|{
name|sourceFS
operator|=
name|sourcePath
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|preserveXAttrs
init|=
name|fileAttributes
operator|.
name|contains
argument_list|(
name|FileAttribute
operator|.
name|XATTR
argument_list|)
decl_stmt|;
name|sourceCurrStatus
operator|=
name|DistCpUtils
operator|.
name|toCopyListingFileStatus
argument_list|(
name|sourceFS
argument_list|,
name|sourceFS
operator|.
name|getFileStatus
argument_list|(
name|sourcePath
argument_list|)
argument_list|,
name|fileAttributes
operator|.
name|contains
argument_list|(
name|FileAttribute
operator|.
name|ACL
argument_list|)
argument_list|,
name|preserveXAttrs
argument_list|,
name|preserveRawXattrs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
operator|new
name|RetriableFileCopyCommand
operator|.
name|CopyReadException
argument_list|(
name|e
argument_list|)
argument_list|)
throw|;
block|}
name|FileStatus
name|targetStatus
init|=
literal|null
decl_stmt|;
try|try
block|{
name|targetStatus
operator|=
name|targetFS
operator|.
name|getFileStatus
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|ignore
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Path could not be found: "
operator|+
name|target
argument_list|,
name|ignore
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|targetStatus
operator|!=
literal|null
operator|&&
operator|(
name|targetStatus
operator|.
name|isDirectory
argument_list|()
operator|!=
name|sourceCurrStatus
operator|.
name|isDirectory
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't replace "
operator|+
name|target
operator|+
literal|". Target is "
operator|+
name|getFileType
argument_list|(
name|targetStatus
argument_list|)
operator|+
literal|", Source is "
operator|+
name|getFileType
argument_list|(
name|sourceCurrStatus
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|sourceCurrStatus
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|createTargetDirsWithRetry
argument_list|(
name|description
argument_list|,
name|target
argument_list|,
name|context
argument_list|)
expr_stmt|;
return|return;
block|}
name|FileAction
name|action
init|=
name|checkUpdate
argument_list|(
name|sourceFS
argument_list|,
name|sourceCurrStatus
argument_list|,
name|target
argument_list|,
name|targetStatus
argument_list|)
decl_stmt|;
if|if
condition|(
name|action
operator|==
name|FileAction
operator|.
name|SKIP
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Skipping copy of "
operator|+
name|sourceCurrStatus
operator|.
name|getPath
argument_list|()
operator|+
literal|" to "
operator|+
name|target
argument_list|)
expr_stmt|;
name|updateSkipCounters
argument_list|(
name|context
argument_list|,
name|sourceCurrStatus
argument_list|)
expr_stmt|;
name|context
operator|.
name|write
argument_list|(
literal|null
argument_list|,
operator|new
name|Text
argument_list|(
literal|"SKIP: "
operator|+
name|sourceCurrStatus
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|copyFileWithRetry
argument_list|(
name|description
argument_list|,
name|sourceCurrStatus
argument_list|,
name|target
argument_list|,
name|context
argument_list|,
name|action
argument_list|,
name|fileAttributes
argument_list|)
expr_stmt|;
block|}
name|DistCpUtils
operator|.
name|preserve
argument_list|(
name|target
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
argument_list|,
name|target
argument_list|,
name|sourceCurrStatus
argument_list|,
name|fileAttributes
argument_list|,
name|preserveRawXattrs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|exception
parameter_list|)
block|{
name|handleFailures
argument_list|(
name|exception
argument_list|,
name|sourceFileStatus
argument_list|,
name|target
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getFileType (CopyListingFileStatus fileStatus)
specifier|private
name|String
name|getFileType
parameter_list|(
name|CopyListingFileStatus
name|fileStatus
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|fileStatus
condition|)
block|{
return|return
literal|"N/A"
return|;
block|}
return|return
name|fileStatus
operator|.
name|isDirectory
argument_list|()
condition|?
literal|"dir"
else|:
literal|"file"
return|;
block|}
DECL|method|getFileType (FileStatus fileStatus)
specifier|private
name|String
name|getFileType
parameter_list|(
name|FileStatus
name|fileStatus
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|fileStatus
condition|)
block|{
return|return
literal|"N/A"
return|;
block|}
return|return
name|fileStatus
operator|.
name|isDirectory
argument_list|()
condition|?
literal|"dir"
else|:
literal|"file"
return|;
block|}
specifier|private
specifier|static
name|EnumSet
argument_list|<
name|DistCpOptions
operator|.
name|FileAttribute
argument_list|>
DECL|method|getFileAttributeSettings (Mapper.Context context)
name|getFileAttributeSettings
parameter_list|(
name|Mapper
operator|.
name|Context
name|context
parameter_list|)
block|{
name|String
name|attributeString
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|get
argument_list|(
name|DistCpOptionSwitch
operator|.
name|PRESERVE_STATUS
operator|.
name|getConfigLabel
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|DistCpUtils
operator|.
name|unpackAttributes
argument_list|(
name|attributeString
argument_list|)
return|;
block|}
DECL|method|copyFileWithRetry (String description, CopyListingFileStatus sourceFileStatus, Path target, Context context, FileAction action, EnumSet<DistCpOptions.FileAttribute> fileAttributes)
specifier|private
name|void
name|copyFileWithRetry
parameter_list|(
name|String
name|description
parameter_list|,
name|CopyListingFileStatus
name|sourceFileStatus
parameter_list|,
name|Path
name|target
parameter_list|,
name|Context
name|context
parameter_list|,
name|FileAction
name|action
parameter_list|,
name|EnumSet
argument_list|<
name|DistCpOptions
operator|.
name|FileAttribute
argument_list|>
name|fileAttributes
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|bytesCopied
decl_stmt|;
try|try
block|{
name|bytesCopied
operator|=
operator|(
name|Long
operator|)
operator|new
name|RetriableFileCopyCommand
argument_list|(
name|skipCrc
argument_list|,
name|description
argument_list|,
name|action
argument_list|)
operator|.
name|execute
argument_list|(
name|sourceFileStatus
argument_list|,
name|target
argument_list|,
name|context
argument_list|,
name|fileAttributes
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|context
operator|.
name|setStatus
argument_list|(
literal|"Copy Failure: "
operator|+
name|sourceFileStatus
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File copy failed: "
operator|+
name|sourceFileStatus
operator|.
name|getPath
argument_list|()
operator|+
literal|" --> "
operator|+
name|target
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|incrementCounter
argument_list|(
name|context
argument_list|,
name|Counter
operator|.
name|BYTESEXPECTED
argument_list|,
name|sourceFileStatus
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|incrementCounter
argument_list|(
name|context
argument_list|,
name|Counter
operator|.
name|BYTESCOPIED
argument_list|,
name|bytesCopied
argument_list|)
expr_stmt|;
name|incrementCounter
argument_list|(
name|context
argument_list|,
name|Counter
operator|.
name|COPY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|totalBytesCopied
operator|+=
name|bytesCopied
expr_stmt|;
block|}
DECL|method|createTargetDirsWithRetry (String description, Path target, Context context)
specifier|private
name|void
name|createTargetDirsWithRetry
parameter_list|(
name|String
name|description
parameter_list|,
name|Path
name|target
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
operator|new
name|RetriableDirectoryCreateCommand
argument_list|(
name|description
argument_list|)
operator|.
name|execute
argument_list|(
name|target
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"mkdir failed for "
operator|+
name|target
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|incrementCounter
argument_list|(
name|context
argument_list|,
name|Counter
operator|.
name|COPY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|updateSkipCounters (Context context, CopyListingFileStatus sourceFile)
specifier|private
specifier|static
name|void
name|updateSkipCounters
parameter_list|(
name|Context
name|context
parameter_list|,
name|CopyListingFileStatus
name|sourceFile
parameter_list|)
block|{
name|incrementCounter
argument_list|(
name|context
argument_list|,
name|Counter
operator|.
name|SKIP
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|incrementCounter
argument_list|(
name|context
argument_list|,
name|Counter
operator|.
name|BYTESSKIPPED
argument_list|,
name|sourceFile
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|handleFailures (IOException exception, CopyListingFileStatus sourceFileStatus, Path target, Context context)
specifier|private
name|void
name|handleFailures
parameter_list|(
name|IOException
name|exception
parameter_list|,
name|CopyListingFileStatus
name|sourceFileStatus
parameter_list|,
name|Path
name|target
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failure in copying "
operator|+
name|sourceFileStatus
operator|.
name|getPath
argument_list|()
operator|+
literal|" to "
operator|+
name|target
argument_list|,
name|exception
argument_list|)
expr_stmt|;
if|if
condition|(
name|ignoreFailures
operator|&&
name|ExceptionUtils
operator|.
name|indexOfType
argument_list|(
name|exception
argument_list|,
name|CopyReadException
operator|.
name|class
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|incrementCounter
argument_list|(
name|context
argument_list|,
name|Counter
operator|.
name|FAIL
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|incrementCounter
argument_list|(
name|context
argument_list|,
name|Counter
operator|.
name|BYTESFAILED
argument_list|,
name|sourceFileStatus
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|write
argument_list|(
literal|null
argument_list|,
operator|new
name|Text
argument_list|(
literal|"FAIL: "
operator|+
name|sourceFileStatus
operator|.
name|getPath
argument_list|()
operator|+
literal|" - "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|exception
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
throw|throw
name|exception
throw|;
block|}
DECL|method|incrementCounter (Context context, Counter counter, long value)
specifier|private
specifier|static
name|void
name|incrementCounter
parameter_list|(
name|Context
name|context
parameter_list|,
name|Counter
name|counter
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|context
operator|.
name|getCounter
argument_list|(
name|counter
argument_list|)
operator|.
name|increment
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|checkUpdate (FileSystem sourceFS, CopyListingFileStatus source, Path target, FileStatus targetFileStatus)
specifier|private
name|FileAction
name|checkUpdate
parameter_list|(
name|FileSystem
name|sourceFS
parameter_list|,
name|CopyListingFileStatus
name|source
parameter_list|,
name|Path
name|target
parameter_list|,
name|FileStatus
name|targetFileStatus
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|targetFileStatus
operator|!=
literal|null
operator|&&
operator|!
name|overWrite
condition|)
block|{
if|if
condition|(
name|canSkip
argument_list|(
name|sourceFS
argument_list|,
name|source
argument_list|,
name|targetFileStatus
argument_list|)
condition|)
block|{
return|return
name|FileAction
operator|.
name|SKIP
return|;
block|}
elseif|else
if|if
condition|(
name|append
condition|)
block|{
name|long
name|targetLen
init|=
name|targetFileStatus
operator|.
name|getLen
argument_list|()
decl_stmt|;
if|if
condition|(
name|targetLen
operator|<
name|source
operator|.
name|getLen
argument_list|()
condition|)
block|{
name|FileChecksum
name|sourceChecksum
init|=
name|sourceFS
operator|.
name|getFileChecksum
argument_list|(
name|source
operator|.
name|getPath
argument_list|()
argument_list|,
name|targetLen
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceChecksum
operator|!=
literal|null
operator|&&
name|sourceChecksum
operator|.
name|equals
argument_list|(
name|targetFS
operator|.
name|getFileChecksum
argument_list|(
name|target
argument_list|)
argument_list|)
condition|)
block|{
comment|// We require that the checksum is not null. Thus currently only
comment|// DistributedFileSystem is supported
return|return
name|FileAction
operator|.
name|APPEND
return|;
block|}
block|}
block|}
block|}
return|return
name|FileAction
operator|.
name|OVERWRITE
return|;
block|}
DECL|method|canSkip (FileSystem sourceFS, CopyListingFileStatus source, FileStatus target)
specifier|private
name|boolean
name|canSkip
parameter_list|(
name|FileSystem
name|sourceFS
parameter_list|,
name|CopyListingFileStatus
name|source
parameter_list|,
name|FileStatus
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|syncFolders
condition|)
block|{
return|return
literal|true
return|;
block|}
name|boolean
name|sameLength
init|=
name|target
operator|.
name|getLen
argument_list|()
operator|==
name|source
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|boolean
name|sameBlockSize
init|=
name|source
operator|.
name|getBlockSize
argument_list|()
operator|==
name|target
operator|.
name|getBlockSize
argument_list|()
operator|||
operator|!
name|preserve
operator|.
name|contains
argument_list|(
name|FileAttribute
operator|.
name|BLOCKSIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|sameLength
operator|&&
name|sameBlockSize
condition|)
block|{
return|return
name|skipCrc
operator|||
name|DistCpUtils
operator|.
name|checksumsAreEqual
argument_list|(
name|sourceFS
argument_list|,
name|source
operator|.
name|getPath
argument_list|()
argument_list|,
literal|null
argument_list|,
name|targetFS
argument_list|,
name|target
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|cleanup (Context context)
specifier|protected
name|void
name|cleanup
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|super
operator|.
name|cleanup
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|long
name|secs
init|=
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startEpoch
operator|)
operator|/
literal|1000
decl_stmt|;
name|incrementCounter
argument_list|(
name|context
argument_list|,
name|Counter
operator|.
name|BANDWIDTH_IN_BYTES
argument_list|,
name|totalBytesCopied
operator|/
operator|(
operator|(
name|secs
operator|==
literal|0
condition|?
literal|1
else|:
name|secs
operator|)
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

