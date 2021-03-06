begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.runtimes.yarnservice
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|submarine
operator|.
name|runtimes
operator|.
name|yarnservice
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
name|annotations
operator|.
name|VisibleForTesting
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
name|FileUtil
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
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Component
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
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|ConfigFile
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
name|yarn
operator|.
name|submarine
operator|.
name|common
operator|.
name|ClientContext
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
name|yarn
operator|.
name|submarine
operator|.
name|common
operator|.
name|conf
operator|.
name|SubmarineConfiguration
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
name|yarn
operator|.
name|submarine
operator|.
name|common
operator|.
name|conf
operator|.
name|SubmarineLogs
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
name|yarn
operator|.
name|submarine
operator|.
name|common
operator|.
name|fs
operator|.
name|RemoteDirectoryManager
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
name|yarn
operator|.
name|submarine
operator|.
name|utils
operator|.
name|ZipUtilities
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
name|HashSet
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Contains methods to perform file system operations. Almost all of the methods  * are regular non-static methods as the operations are performed with the help  * of a {@link RemoteDirectoryManager} instance passed in as a constructor  * dependency. Please note that some operations require to read config settings  * as well, so that we have Submarine and YARN config objects as dependencies as  * well.  */
end_comment

begin_class
DECL|class|FileSystemOperations
specifier|public
class|class
name|FileSystemOperations
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
name|FileSystemOperations
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|submarineConfig
specifier|private
specifier|final
name|Configuration
name|submarineConfig
decl_stmt|;
DECL|field|yarnConfig
specifier|private
specifier|final
name|Configuration
name|yarnConfig
decl_stmt|;
DECL|field|uploadedFiles
specifier|private
name|Set
argument_list|<
name|Path
argument_list|>
name|uploadedFiles
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|remoteDirectoryManager
specifier|private
name|RemoteDirectoryManager
name|remoteDirectoryManager
decl_stmt|;
DECL|method|FileSystemOperations (ClientContext clientContext)
specifier|public
name|FileSystemOperations
parameter_list|(
name|ClientContext
name|clientContext
parameter_list|)
block|{
name|this
operator|.
name|remoteDirectoryManager
operator|=
name|clientContext
operator|.
name|getRemoteDirectoryManager
argument_list|()
expr_stmt|;
name|this
operator|.
name|submarineConfig
operator|=
name|clientContext
operator|.
name|getSubmarineConfig
argument_list|()
expr_stmt|;
name|this
operator|.
name|yarnConfig
operator|=
name|clientContext
operator|.
name|getYarnConfig
argument_list|()
expr_stmt|;
block|}
comment|/**    * May download a remote uri(file/dir) and zip.    * Skip download if local dir    * Remote uri can be a local dir(won't download)    * or remote HDFS dir, s3 dir/file .etc    * */
DECL|method|downloadAndZip (String remoteDir, String zipFileName, boolean doZip)
specifier|public
name|String
name|downloadAndZip
parameter_list|(
name|String
name|remoteDir
parameter_list|,
name|String
name|zipFileName
parameter_list|,
name|boolean
name|doZip
parameter_list|)
throws|throws
name|IOException
block|{
comment|//Append original modification time and size to zip file name
name|String
name|suffix
decl_stmt|;
name|String
name|srcDir
init|=
name|remoteDir
decl_stmt|;
name|String
name|zipDirPath
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
operator|+
literal|"/"
operator|+
name|zipFileName
decl_stmt|;
name|boolean
name|needDeleteTempDir
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|remoteDirectoryManager
operator|.
name|isRemote
argument_list|(
name|remoteDir
argument_list|)
condition|)
block|{
comment|//Append original modification time and size to zip file name
name|FileStatus
name|status
init|=
name|remoteDirectoryManager
operator|.
name|getRemoteFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|remoteDir
argument_list|)
argument_list|)
decl_stmt|;
name|suffix
operator|=
literal|"_"
operator|+
name|status
operator|.
name|getModificationTime
argument_list|()
operator|+
literal|"-"
operator|+
name|remoteDirectoryManager
operator|.
name|getRemoteFileSize
argument_list|(
name|remoteDir
argument_list|)
expr_stmt|;
comment|// Download them to temp dir
name|boolean
name|downloaded
init|=
name|remoteDirectoryManager
operator|.
name|copyRemoteToLocal
argument_list|(
name|remoteDir
argument_list|,
name|zipDirPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|downloaded
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to download files from "
operator|+
name|remoteDir
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Downloaded remote: {} to local: {}"
argument_list|,
name|remoteDir
argument_list|,
name|zipDirPath
argument_list|)
expr_stmt|;
name|srcDir
operator|=
name|zipDirPath
expr_stmt|;
name|needDeleteTempDir
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|File
name|localDir
init|=
operator|new
name|File
argument_list|(
name|remoteDir
argument_list|)
decl_stmt|;
name|suffix
operator|=
literal|"_"
operator|+
name|localDir
operator|.
name|lastModified
argument_list|()
operator|+
literal|"-"
operator|+
name|localDir
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|doZip
condition|)
block|{
return|return
name|srcDir
return|;
block|}
comment|// zip a local dir
name|String
name|zipFileUri
init|=
name|ZipUtilities
operator|.
name|zipDir
argument_list|(
name|srcDir
argument_list|,
name|zipDirPath
operator|+
name|suffix
operator|+
literal|".zip"
argument_list|)
decl_stmt|;
comment|// delete downloaded temp dir
if|if
condition|(
name|needDeleteTempDir
condition|)
block|{
name|deleteFiles
argument_list|(
name|srcDir
argument_list|)
expr_stmt|;
block|}
return|return
name|zipFileUri
return|;
block|}
DECL|method|deleteFiles (String localUri)
specifier|public
name|void
name|deleteFiles
parameter_list|(
name|String
name|localUri
parameter_list|)
block|{
name|boolean
name|success
init|=
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|localUri
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to delete {}"
argument_list|,
name|localUri
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleted {}"
argument_list|,
name|localUri
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|uploadToRemoteFileAndLocalizeToContainerWorkDir (Path stagingDir, String fileToUpload, String destFilename, Component comp)
specifier|public
name|void
name|uploadToRemoteFileAndLocalizeToContainerWorkDir
parameter_list|(
name|Path
name|stagingDir
parameter_list|,
name|String
name|fileToUpload
parameter_list|,
name|String
name|destFilename
parameter_list|,
name|Component
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|uploadedFilePath
init|=
name|uploadToRemoteFile
argument_list|(
name|stagingDir
argument_list|,
name|fileToUpload
argument_list|)
decl_stmt|;
name|locateRemoteFileToContainerWorkDir
argument_list|(
name|destFilename
argument_list|,
name|comp
argument_list|,
name|uploadedFilePath
argument_list|)
expr_stmt|;
block|}
DECL|method|locateRemoteFileToContainerWorkDir (String destFilename, Component comp, Path uploadedFilePath)
specifier|private
name|void
name|locateRemoteFileToContainerWorkDir
parameter_list|(
name|String
name|destFilename
parameter_list|,
name|Component
name|comp
parameter_list|,
name|Path
name|uploadedFilePath
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|yarnConfig
argument_list|)
decl_stmt|;
name|FileStatus
name|fileStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|uploadedFilePath
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Uploaded file path = "
operator|+
name|fileStatus
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set it to component's files list
name|comp
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getFiles
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|ConfigFile
argument_list|()
operator|.
name|srcFile
argument_list|(
name|fileStatus
operator|.
name|getPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|destFile
argument_list|(
name|destFilename
argument_list|)
operator|.
name|type
argument_list|(
name|ConfigFile
operator|.
name|TypeEnum
operator|.
name|STATIC
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|uploadToRemoteFile (Path stagingDir, String fileToUpload)
specifier|public
name|Path
name|uploadToRemoteFile
parameter_list|(
name|Path
name|stagingDir
parameter_list|,
name|String
name|fileToUpload
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|remoteDirectoryManager
operator|.
name|getDefaultFileSystem
argument_list|()
decl_stmt|;
comment|// Upload to remote FS under staging area
name|File
name|localFile
init|=
operator|new
name|File
argument_list|(
name|fileToUpload
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|localFile
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Trying to upload file="
operator|+
name|localFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" to remote, but couldn't find local file."
argument_list|)
throw|;
block|}
name|String
name|filename
init|=
operator|new
name|File
argument_list|(
name|fileToUpload
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Path
name|uploadedFilePath
init|=
operator|new
name|Path
argument_list|(
name|stagingDir
argument_list|,
name|filename
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|uploadedFiles
operator|.
name|contains
argument_list|(
name|uploadedFilePath
argument_list|)
condition|)
block|{
if|if
condition|(
name|SubmarineLogs
operator|.
name|isVerbose
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Copying local file="
operator|+
name|fileToUpload
operator|+
literal|" to remote="
operator|+
name|uploadedFilePath
argument_list|)
expr_stmt|;
block|}
name|fs
operator|.
name|copyFromLocalFile
argument_list|(
operator|new
name|Path
argument_list|(
name|fileToUpload
argument_list|)
argument_list|,
name|uploadedFilePath
argument_list|)
expr_stmt|;
name|uploadedFiles
operator|.
name|add
argument_list|(
name|uploadedFilePath
argument_list|)
expr_stmt|;
block|}
return|return
name|uploadedFilePath
return|;
block|}
DECL|method|validFileSize (String uri)
specifier|public
name|void
name|validFileSize
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|actualSizeByte
decl_stmt|;
name|String
name|locationType
init|=
literal|"Local"
decl_stmt|;
if|if
condition|(
name|remoteDirectoryManager
operator|.
name|isRemote
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|actualSizeByte
operator|=
name|remoteDirectoryManager
operator|.
name|getRemoteFileSize
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|locationType
operator|=
literal|"Remote"
expr_stmt|;
block|}
else|else
block|{
name|actualSizeByte
operator|=
name|FileUtil
operator|.
name|getDU
argument_list|(
operator|new
name|File
argument_list|(
name|uri
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|long
name|maxFileSizeMB
init|=
name|submarineConfig
operator|.
name|getLong
argument_list|(
name|SubmarineConfiguration
operator|.
name|LOCALIZATION_MAX_ALLOWED_FILE_SIZE_MB
argument_list|,
name|SubmarineConfiguration
operator|.
name|DEFAULT_MAX_ALLOWED_REMOTE_URI_SIZE_MB
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"{} fie/dir: {}, size(Byte):{},"
operator|+
literal|" Allowed max file/dir size: {}"
argument_list|,
name|locationType
argument_list|,
name|uri
argument_list|,
name|actualSizeByte
argument_list|,
name|maxFileSizeMB
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
if|if
condition|(
name|actualSizeByte
operator|>
name|maxFileSizeMB
operator|*
literal|1024
operator|*
literal|1024
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|uri
operator|+
literal|" size(Byte): "
operator|+
name|actualSizeByte
operator|+
literal|" exceeds configured max size:"
operator|+
name|maxFileSizeMB
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
throw|;
block|}
block|}
DECL|method|setPermission (Path destPath, FsPermission permission)
specifier|public
name|void
name|setPermission
parameter_list|(
name|Path
name|destPath
parameter_list|,
name|FsPermission
name|permission
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|yarnConfig
argument_list|)
decl_stmt|;
name|fs
operator|.
name|setPermission
argument_list|(
name|destPath
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|permission
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|needHdfs (List<String> stringsToCheck)
specifier|public
specifier|static
name|boolean
name|needHdfs
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|stringsToCheck
parameter_list|)
block|{
for|for
control|(
name|String
name|content
range|:
name|stringsToCheck
control|)
block|{
if|if
condition|(
name|content
operator|!=
literal|null
operator|&&
name|content
operator|.
name|contains
argument_list|(
literal|"hdfs://"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|needHdfs (String content)
specifier|public
specifier|static
name|boolean
name|needHdfs
parameter_list|(
name|String
name|content
parameter_list|)
block|{
return|return
name|content
operator|!=
literal|null
operator|&&
name|content
operator|.
name|contains
argument_list|(
literal|"hdfs://"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

