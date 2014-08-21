begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
package|;
end_package

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
name|io
operator|.
name|SequenceFile
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
name|mapreduce
operator|.
name|security
operator|.
name|TokenCache
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
name|Credentials
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
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
name|tools
operator|.
name|DistCpConstants
operator|.
name|HDFS_RESERVED_RAW_DIRECTORY_NAME
import|;
end_import

begin_comment
comment|/**  * The SimpleCopyListing is responsible for making the exhaustive list of  * all files/directories under its specified list of input-paths.  * These are written into the specified copy-listing file.  * Note: The SimpleCopyListing doesn't handle wild-cards in the input-paths.  */
end_comment

begin_class
DECL|class|SimpleCopyListing
specifier|public
class|class
name|SimpleCopyListing
extends|extends
name|CopyListing
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SimpleCopyListing
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|totalPaths
specifier|private
name|long
name|totalPaths
init|=
literal|0
decl_stmt|;
DECL|field|totalBytesToCopy
specifier|private
name|long
name|totalBytesToCopy
init|=
literal|0
decl_stmt|;
comment|/**    * Protected constructor, to initialize configuration.    *    * @param configuration The input configuration, with which the source/target FileSystems may be accessed.    * @param credentials - Credentials object on which the FS delegation tokens are cached. If null    * delegation token caching is skipped    */
DECL|method|SimpleCopyListing (Configuration configuration, Credentials credentials)
specifier|protected
name|SimpleCopyListing
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|Credentials
name|credentials
parameter_list|)
block|{
name|super
argument_list|(
name|configuration
argument_list|,
name|credentials
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|validatePaths (DistCpOptions options)
specifier|protected
name|void
name|validatePaths
parameter_list|(
name|DistCpOptions
name|options
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidInputException
block|{
name|Path
name|targetPath
init|=
name|options
operator|.
name|getTargetPath
argument_list|()
decl_stmt|;
name|FileSystem
name|targetFS
init|=
name|targetPath
operator|.
name|getFileSystem
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|targetIsFile
init|=
name|targetFS
operator|.
name|isFile
argument_list|(
name|targetPath
argument_list|)
decl_stmt|;
name|targetPath
operator|=
name|targetFS
operator|.
name|makeQualified
argument_list|(
name|targetPath
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|targetIsReservedRaw
init|=
name|Path
operator|.
name|getPathWithoutSchemeAndAuthority
argument_list|(
name|targetPath
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
name|HDFS_RESERVED_RAW_DIRECTORY_NAME
argument_list|)
decl_stmt|;
comment|//If target is a file, then source has to be single file
if|if
condition|(
name|targetIsFile
condition|)
block|{
if|if
condition|(
name|options
operator|.
name|getSourcePaths
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|InvalidInputException
argument_list|(
literal|"Multiple source being copied to a file: "
operator|+
name|targetPath
argument_list|)
throw|;
block|}
name|Path
name|srcPath
init|=
name|options
operator|.
name|getSourcePaths
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|FileSystem
name|sourceFS
init|=
name|srcPath
operator|.
name|getFileSystem
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|sourceFS
operator|.
name|isFile
argument_list|(
name|srcPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidInputException
argument_list|(
literal|"Cannot copy "
operator|+
name|srcPath
operator|+
literal|", which is not a file to "
operator|+
name|targetPath
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|options
operator|.
name|shouldAtomicCommit
argument_list|()
operator|&&
name|targetFS
operator|.
name|exists
argument_list|(
name|targetPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidInputException
argument_list|(
literal|"Target path for atomic-commit already exists: "
operator|+
name|targetPath
operator|+
literal|". Cannot atomic-commit to pre-existing target-path."
argument_list|)
throw|;
block|}
for|for
control|(
name|Path
name|path
range|:
name|options
operator|.
name|getSourcePaths
argument_list|()
control|)
block|{
name|FileSystem
name|fs
init|=
name|path
operator|.
name|getFileSystem
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidInputException
argument_list|(
name|path
operator|+
literal|" doesn't exist"
argument_list|)
throw|;
block|}
if|if
condition|(
name|Path
operator|.
name|getPathWithoutSchemeAndAuthority
argument_list|(
name|path
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
name|HDFS_RESERVED_RAW_DIRECTORY_NAME
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|targetIsReservedRaw
condition|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"The source path '"
operator|+
name|path
operator|+
literal|"' starts with "
operator|+
name|HDFS_RESERVED_RAW_DIRECTORY_NAME
operator|+
literal|" but the target path '"
operator|+
name|targetPath
operator|+
literal|"' does not. Either all or none of the paths must "
operator|+
literal|"have this prefix."
decl_stmt|;
throw|throw
operator|new
name|InvalidInputException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|targetIsReservedRaw
condition|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"The target path '"
operator|+
name|targetPath
operator|+
literal|"' starts with "
operator|+
name|HDFS_RESERVED_RAW_DIRECTORY_NAME
operator|+
literal|" but the source path '"
operator|+
name|path
operator|+
literal|"' does not. Either all or none of the paths must "
operator|+
literal|"have this prefix."
decl_stmt|;
throw|throw
operator|new
name|InvalidInputException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|targetIsReservedRaw
condition|)
block|{
name|options
operator|.
name|preserveRawXattrs
argument_list|()
expr_stmt|;
name|getConf
argument_list|()
operator|.
name|setBoolean
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_PRESERVE_RAWXATTRS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/* This is requires to allow map tasks to access each of the source        clusters. This would retrieve the delegation token for each unique        file system and add them to job's private credential store      */
name|Credentials
name|credentials
init|=
name|getCredentials
argument_list|()
decl_stmt|;
if|if
condition|(
name|credentials
operator|!=
literal|null
condition|)
block|{
name|Path
index|[]
name|inputPaths
init|=
name|options
operator|.
name|getSourcePaths
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|Path
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|TokenCache
operator|.
name|obtainTokensForNamenodes
argument_list|(
name|credentials
argument_list|,
name|inputPaths
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|doBuildListing (Path pathToListingFile, DistCpOptions options)
specifier|public
name|void
name|doBuildListing
parameter_list|(
name|Path
name|pathToListingFile
parameter_list|,
name|DistCpOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|doBuildListing
argument_list|(
name|getWriter
argument_list|(
name|pathToListingFile
argument_list|)
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
comment|/**    * Collect the list of     *<sourceRelativePath, sourceFileStatus>    * to be copied and write to the sequence file. In essence, any file or    * directory that need to be copied or sync-ed is written as an entry to the    * sequence file, with the possible exception of the source root:    *     when either -update (sync) or -overwrite switch is specified, and if    *     the the source root is a directory, then the source root entry is not     *     written to the sequence file, because only the contents of the source    *     directory need to be copied in this case.    * See {@link org.apache.hadoop.tools.util.DistCpUtils#getRelativePath} for    *     how relative path is computed.    * See computeSourceRootPath method for how the root path of the source is    *     computed.    * @param fileListWriter    * @param options    * @throws IOException    */
annotation|@
name|VisibleForTesting
DECL|method|doBuildListing (SequenceFile.Writer fileListWriter, DistCpOptions options)
specifier|public
name|void
name|doBuildListing
parameter_list|(
name|SequenceFile
operator|.
name|Writer
name|fileListWriter
parameter_list|,
name|DistCpOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
for|for
control|(
name|Path
name|path
range|:
name|options
operator|.
name|getSourcePaths
argument_list|()
control|)
block|{
name|FileSystem
name|sourceFS
init|=
name|path
operator|.
name|getFileSystem
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|preserveAcls
init|=
name|options
operator|.
name|shouldPreserve
argument_list|(
name|FileAttribute
operator|.
name|ACL
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|preserveXAttrs
init|=
name|options
operator|.
name|shouldPreserve
argument_list|(
name|FileAttribute
operator|.
name|XATTR
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|preserveRawXAttrs
init|=
name|options
operator|.
name|shouldPreserveRawXattrs
argument_list|()
decl_stmt|;
name|path
operator|=
name|makeQualified
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|FileStatus
name|rootStatus
init|=
name|sourceFS
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Path
name|sourcePathRoot
init|=
name|computeSourceRootPath
argument_list|(
name|rootStatus
argument_list|,
name|options
argument_list|)
decl_stmt|;
name|FileStatus
index|[]
name|sourceFiles
init|=
name|sourceFS
operator|.
name|listStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|boolean
name|explore
init|=
operator|(
name|sourceFiles
operator|!=
literal|null
operator|&&
name|sourceFiles
operator|.
name|length
operator|>
literal|0
operator|)
decl_stmt|;
if|if
condition|(
operator|!
name|explore
operator|||
name|rootStatus
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|CopyListingFileStatus
name|rootCopyListingStatus
init|=
name|DistCpUtils
operator|.
name|toCopyListingFileStatus
argument_list|(
name|sourceFS
argument_list|,
name|rootStatus
argument_list|,
name|preserveAcls
argument_list|,
name|preserveXAttrs
argument_list|,
name|preserveRawXAttrs
argument_list|)
decl_stmt|;
name|writeToFileListingRoot
argument_list|(
name|fileListWriter
argument_list|,
name|rootCopyListingStatus
argument_list|,
name|sourcePathRoot
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|explore
condition|)
block|{
for|for
control|(
name|FileStatus
name|sourceStatus
range|:
name|sourceFiles
control|)
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
literal|"Recording source-path: "
operator|+
name|sourceStatus
operator|.
name|getPath
argument_list|()
operator|+
literal|" for copy."
argument_list|)
expr_stmt|;
block|}
name|CopyListingFileStatus
name|sourceCopyListingStatus
init|=
name|DistCpUtils
operator|.
name|toCopyListingFileStatus
argument_list|(
name|sourceFS
argument_list|,
name|sourceStatus
argument_list|,
name|preserveAcls
operator|&&
name|sourceStatus
operator|.
name|isDirectory
argument_list|()
argument_list|,
name|preserveXAttrs
operator|&&
name|sourceStatus
operator|.
name|isDirectory
argument_list|()
argument_list|,
name|preserveRawXAttrs
operator|&&
name|sourceStatus
operator|.
name|isDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|writeToFileListing
argument_list|(
name|fileListWriter
argument_list|,
name|sourceCopyListingStatus
argument_list|,
name|sourcePathRoot
argument_list|,
name|options
argument_list|)
expr_stmt|;
if|if
condition|(
name|isDirectoryAndNotEmpty
argument_list|(
name|sourceFS
argument_list|,
name|sourceStatus
argument_list|)
condition|)
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
literal|"Traversing non-empty source dir: "
operator|+
name|sourceStatus
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|traverseNonEmptyDirectory
argument_list|(
name|fileListWriter
argument_list|,
name|sourceStatus
argument_list|,
name|sourcePathRoot
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|fileListWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|fileListWriter
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
name|fileListWriter
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|computeSourceRootPath (FileStatus sourceStatus, DistCpOptions options)
specifier|private
name|Path
name|computeSourceRootPath
parameter_list|(
name|FileStatus
name|sourceStatus
parameter_list|,
name|DistCpOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|target
init|=
name|options
operator|.
name|getTargetPath
argument_list|()
decl_stmt|;
name|FileSystem
name|targetFS
init|=
name|target
operator|.
name|getFileSystem
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|targetPathExists
init|=
name|options
operator|.
name|getTargetPathExists
argument_list|()
decl_stmt|;
name|boolean
name|solitaryFile
init|=
name|options
operator|.
name|getSourcePaths
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
operator|!
name|sourceStatus
operator|.
name|isDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|solitaryFile
condition|)
block|{
if|if
condition|(
name|targetFS
operator|.
name|isFile
argument_list|(
name|target
argument_list|)
operator|||
operator|!
name|targetPathExists
condition|)
block|{
return|return
name|sourceStatus
operator|.
name|getPath
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|sourceStatus
operator|.
name|getPath
argument_list|()
operator|.
name|getParent
argument_list|()
return|;
block|}
block|}
else|else
block|{
name|boolean
name|specialHandling
init|=
operator|(
name|options
operator|.
name|getSourcePaths
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
operator|!
name|targetPathExists
operator|)
operator|||
name|options
operator|.
name|shouldSyncFolder
argument_list|()
operator|||
name|options
operator|.
name|shouldOverwrite
argument_list|()
decl_stmt|;
return|return
name|specialHandling
operator|&&
name|sourceStatus
operator|.
name|isDirectory
argument_list|()
condition|?
name|sourceStatus
operator|.
name|getPath
argument_list|()
else|:
name|sourceStatus
operator|.
name|getPath
argument_list|()
operator|.
name|getParent
argument_list|()
return|;
block|}
block|}
comment|/**    * Provide an option to skip copy of a path, Allows for exclusion    * of files such as {@link org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter#SUCCEEDED_FILE_NAME}    * @param path - Path being considered for copy while building the file listing    * @param options - Input options passed during DistCp invocation    * @return - True if the path should be considered for copy, false otherwise    */
DECL|method|shouldCopy (Path path, DistCpOptions options)
specifier|protected
name|boolean
name|shouldCopy
parameter_list|(
name|Path
name|path
parameter_list|,
name|DistCpOptions
name|options
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getBytesToCopy ()
specifier|protected
name|long
name|getBytesToCopy
parameter_list|()
block|{
return|return
name|totalBytesToCopy
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getNumberOfPaths ()
specifier|protected
name|long
name|getNumberOfPaths
parameter_list|()
block|{
return|return
name|totalPaths
return|;
block|}
DECL|method|makeQualified (Path path)
specifier|private
name|Path
name|makeQualified
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FileSystem
name|fs
init|=
name|path
operator|.
name|getFileSystem
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|path
operator|.
name|makeQualified
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
argument_list|,
name|fs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getWriter (Path pathToListFile)
specifier|private
name|SequenceFile
operator|.
name|Writer
name|getWriter
parameter_list|(
name|Path
name|pathToListFile
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|pathToListFile
operator|.
name|getFileSystem
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|pathToListFile
argument_list|)
condition|)
block|{
name|fs
operator|.
name|delete
argument_list|(
name|pathToListFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|SequenceFile
operator|.
name|createWriter
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|SequenceFile
operator|.
name|Writer
operator|.
name|file
argument_list|(
name|pathToListFile
argument_list|)
argument_list|,
name|SequenceFile
operator|.
name|Writer
operator|.
name|keyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
argument_list|,
name|SequenceFile
operator|.
name|Writer
operator|.
name|valueClass
argument_list|(
name|CopyListingFileStatus
operator|.
name|class
argument_list|)
argument_list|,
name|SequenceFile
operator|.
name|Writer
operator|.
name|compression
argument_list|(
name|SequenceFile
operator|.
name|CompressionType
operator|.
name|NONE
argument_list|)
argument_list|)
return|;
block|}
DECL|method|isDirectoryAndNotEmpty (FileSystem fileSystem, FileStatus fileStatus)
specifier|private
specifier|static
name|boolean
name|isDirectoryAndNotEmpty
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|,
name|FileStatus
name|fileStatus
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fileStatus
operator|.
name|isDirectory
argument_list|()
operator|&&
name|getChildren
argument_list|(
name|fileSystem
argument_list|,
name|fileStatus
argument_list|)
operator|.
name|length
operator|>
literal|0
return|;
block|}
DECL|method|getChildren (FileSystem fileSystem, FileStatus parent)
specifier|private
specifier|static
name|FileStatus
index|[]
name|getChildren
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|,
name|FileStatus
name|parent
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fileSystem
operator|.
name|listStatus
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
DECL|method|traverseNonEmptyDirectory (SequenceFile.Writer fileListWriter, FileStatus sourceStatus, Path sourcePathRoot, DistCpOptions options)
specifier|private
name|void
name|traverseNonEmptyDirectory
parameter_list|(
name|SequenceFile
operator|.
name|Writer
name|fileListWriter
parameter_list|,
name|FileStatus
name|sourceStatus
parameter_list|,
name|Path
name|sourcePathRoot
parameter_list|,
name|DistCpOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|sourceFS
init|=
name|sourcePathRoot
operator|.
name|getFileSystem
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|preserveAcls
init|=
name|options
operator|.
name|shouldPreserve
argument_list|(
name|FileAttribute
operator|.
name|ACL
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|preserveXAttrs
init|=
name|options
operator|.
name|shouldPreserve
argument_list|(
name|FileAttribute
operator|.
name|XATTR
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|preserveRawXattrs
init|=
name|options
operator|.
name|shouldPreserveRawXattrs
argument_list|()
decl_stmt|;
name|Stack
argument_list|<
name|FileStatus
argument_list|>
name|pathStack
init|=
operator|new
name|Stack
argument_list|<
name|FileStatus
argument_list|>
argument_list|()
decl_stmt|;
name|pathStack
operator|.
name|push
argument_list|(
name|sourceStatus
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|pathStack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|FileStatus
name|child
range|:
name|getChildren
argument_list|(
name|sourceFS
argument_list|,
name|pathStack
operator|.
name|pop
argument_list|()
argument_list|)
control|)
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
literal|"Recording source-path: "
operator|+
name|sourceStatus
operator|.
name|getPath
argument_list|()
operator|+
literal|" for copy."
argument_list|)
expr_stmt|;
name|CopyListingFileStatus
name|childCopyListingStatus
init|=
name|DistCpUtils
operator|.
name|toCopyListingFileStatus
argument_list|(
name|sourceFS
argument_list|,
name|child
argument_list|,
name|preserveAcls
operator|&&
name|child
operator|.
name|isDirectory
argument_list|()
argument_list|,
name|preserveXAttrs
operator|&&
name|child
operator|.
name|isDirectory
argument_list|()
argument_list|,
name|preserveRawXattrs
operator|&&
name|child
operator|.
name|isDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|writeToFileListing
argument_list|(
name|fileListWriter
argument_list|,
name|childCopyListingStatus
argument_list|,
name|sourcePathRoot
argument_list|,
name|options
argument_list|)
expr_stmt|;
if|if
condition|(
name|isDirectoryAndNotEmpty
argument_list|(
name|sourceFS
argument_list|,
name|child
argument_list|)
condition|)
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
literal|"Traversing non-empty source dir: "
operator|+
name|sourceStatus
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|pathStack
operator|.
name|push
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|writeToFileListingRoot (SequenceFile.Writer fileListWriter, CopyListingFileStatus fileStatus, Path sourcePathRoot, DistCpOptions options)
specifier|private
name|void
name|writeToFileListingRoot
parameter_list|(
name|SequenceFile
operator|.
name|Writer
name|fileListWriter
parameter_list|,
name|CopyListingFileStatus
name|fileStatus
parameter_list|,
name|Path
name|sourcePathRoot
parameter_list|,
name|DistCpOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|syncOrOverwrite
init|=
name|options
operator|.
name|shouldSyncFolder
argument_list|()
operator|||
name|options
operator|.
name|shouldOverwrite
argument_list|()
decl_stmt|;
if|if
condition|(
name|fileStatus
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|sourcePathRoot
argument_list|)
operator|&&
name|fileStatus
operator|.
name|isDirectory
argument_list|()
operator|&&
name|syncOrOverwrite
condition|)
block|{
comment|// Skip the root-paths when syncOrOverwrite
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
literal|"Skip "
operator|+
name|fileStatus
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|writeToFileListing
argument_list|(
name|fileListWriter
argument_list|,
name|fileStatus
argument_list|,
name|sourcePathRoot
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
DECL|method|writeToFileListing (SequenceFile.Writer fileListWriter, CopyListingFileStatus fileStatus, Path sourcePathRoot, DistCpOptions options)
specifier|private
name|void
name|writeToFileListing
parameter_list|(
name|SequenceFile
operator|.
name|Writer
name|fileListWriter
parameter_list|,
name|CopyListingFileStatus
name|fileStatus
parameter_list|,
name|Path
name|sourcePathRoot
parameter_list|,
name|DistCpOptions
name|options
parameter_list|)
throws|throws
name|IOException
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
literal|"REL PATH: "
operator|+
name|DistCpUtils
operator|.
name|getRelativePath
argument_list|(
name|sourcePathRoot
argument_list|,
name|fileStatus
operator|.
name|getPath
argument_list|()
argument_list|)
operator|+
literal|", FULL PATH: "
operator|+
name|fileStatus
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|FileStatus
name|status
init|=
name|fileStatus
decl_stmt|;
if|if
condition|(
operator|!
name|shouldCopy
argument_list|(
name|fileStatus
operator|.
name|getPath
argument_list|()
argument_list|,
name|options
argument_list|)
condition|)
block|{
return|return;
block|}
name|fileListWriter
operator|.
name|append
argument_list|(
operator|new
name|Text
argument_list|(
name|DistCpUtils
operator|.
name|getRelativePath
argument_list|(
name|sourcePathRoot
argument_list|,
name|fileStatus
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|fileListWriter
operator|.
name|sync
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|fileStatus
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|totalBytesToCopy
operator|+=
name|fileStatus
operator|.
name|getLen
argument_list|()
expr_stmt|;
block|}
name|totalPaths
operator|++
expr_stmt|;
block|}
block|}
end_class

end_unit

