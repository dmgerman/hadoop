begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.common.fs
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
name|common
operator|.
name|fs
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
name|yarn
operator|.
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|CliConstants
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  * Manages remote directories for staging, log, etc.  * TODO, need to properly handle permission / name validation, etc.  */
end_comment

begin_class
DECL|class|DefaultRemoteDirectoryManager
specifier|public
class|class
name|DefaultRemoteDirectoryManager
implements|implements
name|RemoteDirectoryManager
block|{
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|method|DefaultRemoteDirectoryManager (ClientContext context)
specifier|public
name|DefaultRemoteDirectoryManager
parameter_list|(
name|ClientContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|context
operator|.
name|getYarnConfig
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|context
operator|.
name|getYarnConfig
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getJobStagingArea (String jobName, boolean create)
specifier|public
name|Path
name|getJobStagingArea
parameter_list|(
name|String
name|jobName
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|staging
init|=
operator|new
name|Path
argument_list|(
name|getJobRootFolder
argument_list|(
name|jobName
argument_list|)
argument_list|,
literal|"staging"
argument_list|)
decl_stmt|;
if|if
condition|(
name|create
condition|)
block|{
name|createFolderIfNotExist
argument_list|(
name|staging
argument_list|)
expr_stmt|;
block|}
comment|// Get a file status to make sure it is a absolute path.
name|FileStatus
name|fStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|staging
argument_list|)
decl_stmt|;
return|return
name|fStatus
operator|.
name|getPath
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getJobCheckpointDir (String jobName, boolean create)
specifier|public
name|Path
name|getJobCheckpointDir
parameter_list|(
name|String
name|jobName
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|jobDir
init|=
operator|new
name|Path
argument_list|(
name|getJobStagingArea
argument_list|(
name|jobName
argument_list|,
name|create
argument_list|)
argument_list|,
name|CliConstants
operator|.
name|CHECKPOINT_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|create
condition|)
block|{
name|createFolderIfNotExist
argument_list|(
name|jobDir
argument_list|)
expr_stmt|;
block|}
return|return
name|jobDir
return|;
block|}
annotation|@
name|Override
DECL|method|getModelDir (String modelName, boolean create)
specifier|public
name|Path
name|getModelDir
parameter_list|(
name|String
name|modelName
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|modelDir
init|=
operator|new
name|Path
argument_list|(
operator|new
name|Path
argument_list|(
literal|"submarine"
argument_list|,
literal|"models"
argument_list|)
argument_list|,
name|modelName
argument_list|)
decl_stmt|;
if|if
condition|(
name|create
condition|)
block|{
name|createFolderIfNotExist
argument_list|(
name|modelDir
argument_list|)
expr_stmt|;
block|}
return|return
name|modelDir
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultFileSystem ()
specifier|public
name|FileSystem
name|getDefaultFileSystem
parameter_list|()
block|{
return|return
name|fs
return|;
block|}
annotation|@
name|Override
DECL|method|getFileSystemByUri (String uri)
specifier|public
name|FileSystem
name|getFileSystemByUri
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|FileSystem
operator|.
name|get
argument_list|(
name|URI
operator|.
name|create
argument_list|(
name|uri
argument_list|)
argument_list|,
name|conf
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getUserRootFolder ()
specifier|public
name|Path
name|getUserRootFolder
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|rootPath
init|=
operator|new
name|Path
argument_list|(
literal|"submarine"
argument_list|,
literal|"jobs"
argument_list|)
decl_stmt|;
name|createFolderIfNotExist
argument_list|(
name|rootPath
argument_list|)
expr_stmt|;
comment|// Get a file status to make sure it is a absolute path.
name|FileStatus
name|fStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|rootPath
argument_list|)
decl_stmt|;
return|return
name|fStatus
operator|.
name|getPath
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isDir (String uri)
specifier|public
name|boolean
name|isDir
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isRemote
argument_list|(
name|uri
argument_list|)
condition|)
block|{
return|return
name|getFileSystemByUri
argument_list|(
name|uri
argument_list|)
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|uri
argument_list|)
argument_list|)
operator|.
name|isDirectory
argument_list|()
return|;
block|}
return|return
operator|new
name|File
argument_list|(
name|uri
argument_list|)
operator|.
name|isDirectory
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isRemote (String uri)
specifier|public
name|boolean
name|isRemote
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|String
name|scheme
init|=
operator|new
name|Path
argument_list|(
name|uri
argument_list|)
operator|.
name|toUri
argument_list|()
operator|.
name|getScheme
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|scheme
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|!
name|scheme
operator|.
name|startsWith
argument_list|(
literal|"file://"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copyRemoteToLocal (String remoteUri, String localUri)
specifier|public
name|boolean
name|copyRemoteToLocal
parameter_list|(
name|String
name|remoteUri
parameter_list|,
name|String
name|localUri
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Delete old to avoid failure in FileUtil.copy
name|File
name|old
init|=
operator|new
name|File
argument_list|(
name|localUri
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|old
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete dir:"
operator|+
name|old
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|FileUtil
operator|.
name|copy
argument_list|(
name|getFileSystemByUri
argument_list|(
name|remoteUri
argument_list|)
argument_list|,
operator|new
name|Path
argument_list|(
name|remoteUri
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|localUri
argument_list|)
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|existsRemoteFile (Path url)
specifier|public
name|boolean
name|existsRemoteFile
parameter_list|(
name|Path
name|url
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getFileSystemByUri
argument_list|(
name|url
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|exists
argument_list|(
name|url
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRemoteFileStatus (Path url)
specifier|public
name|FileStatus
name|getRemoteFileStatus
parameter_list|(
name|Path
name|url
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getFileSystemByUri
argument_list|(
name|url
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getFileStatus
argument_list|(
name|url
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRemoteFileSize (String uri)
specifier|public
name|long
name|getRemoteFileSize
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getFileSystemByUri
argument_list|(
name|uri
argument_list|)
operator|.
name|getContentSummary
argument_list|(
operator|new
name|Path
argument_list|(
name|uri
argument_list|)
argument_list|)
operator|.
name|getSpaceConsumed
argument_list|()
return|;
block|}
DECL|method|getJobRootFolder (String jobName)
specifier|private
name|Path
name|getJobRootFolder
parameter_list|(
name|String
name|jobName
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|userRoot
init|=
name|getUserRootFolder
argument_list|()
decl_stmt|;
name|Path
name|jobRootPath
init|=
operator|new
name|Path
argument_list|(
name|userRoot
argument_list|,
name|jobName
argument_list|)
decl_stmt|;
name|createFolderIfNotExist
argument_list|(
name|jobRootPath
argument_list|)
expr_stmt|;
comment|// Get a file status to make sure it is a absolute path.
name|FileStatus
name|fStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|jobRootPath
argument_list|)
decl_stmt|;
return|return
name|fStatus
operator|.
name|getPath
argument_list|()
return|;
block|}
DECL|method|createFolderIfNotExist (Path path)
specifier|private
name|void
name|createFolderIfNotExist
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
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
if|if
condition|(
operator|!
name|fs
operator|.
name|mkdirs
argument_list|(
name|path
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to create folder="
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

