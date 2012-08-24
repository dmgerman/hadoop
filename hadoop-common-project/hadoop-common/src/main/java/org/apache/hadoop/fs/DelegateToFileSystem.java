begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|EnumSet
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|Progressable
import|;
end_import

begin_comment
comment|/**  * Implementation of AbstractFileSystem based on the existing implementation of   * {@link FileSystem}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|DelegateToFileSystem
specifier|public
specifier|abstract
class|class
name|DelegateToFileSystem
extends|extends
name|AbstractFileSystem
block|{
DECL|field|fsImpl
specifier|protected
specifier|final
name|FileSystem
name|fsImpl
decl_stmt|;
DECL|method|DelegateToFileSystem (URI theUri, FileSystem theFsImpl, Configuration conf, String supportedScheme, boolean authorityRequired)
specifier|protected
name|DelegateToFileSystem
parameter_list|(
name|URI
name|theUri
parameter_list|,
name|FileSystem
name|theFsImpl
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|String
name|supportedScheme
parameter_list|,
name|boolean
name|authorityRequired
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|super
argument_list|(
name|theUri
argument_list|,
name|supportedScheme
argument_list|,
name|authorityRequired
argument_list|,
name|FileSystem
operator|.
name|getDefaultUri
argument_list|(
name|conf
argument_list|)
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|fsImpl
operator|=
name|theFsImpl
expr_stmt|;
name|fsImpl
operator|.
name|initialize
argument_list|(
name|theUri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|fsImpl
operator|.
name|statistics
operator|=
name|getStatistics
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getInitialWorkingDirectory ()
specifier|public
name|Path
name|getInitialWorkingDirectory
parameter_list|()
block|{
return|return
name|fsImpl
operator|.
name|getInitialWorkingDirectory
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
comment|// call to primitiveCreate
DECL|method|createInternal (Path f, EnumSet<CreateFlag> flag, FsPermission absolutePermission, int bufferSize, short replication, long blockSize, Progressable progress, ChecksumOpt checksumOpt, boolean createParent)
specifier|public
name|FSDataOutputStream
name|createInternal
parameter_list|(
name|Path
name|f
parameter_list|,
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|flag
parameter_list|,
name|FsPermission
name|absolutePermission
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|short
name|replication
parameter_list|,
name|long
name|blockSize
parameter_list|,
name|Progressable
name|progress
parameter_list|,
name|ChecksumOpt
name|checksumOpt
parameter_list|,
name|boolean
name|createParent
parameter_list|)
throws|throws
name|IOException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
comment|// Default impl assumes that permissions do not matter
comment|// calling the regular create is good enough.
comment|// FSs that implement permissions should override this.
if|if
condition|(
operator|!
name|createParent
condition|)
block|{
comment|// parent must exist.
comment|// since this.create makes parent dirs automatically
comment|// we must throw exception if parent does not exist.
specifier|final
name|FileStatus
name|stat
init|=
name|getFileStatus
argument_list|(
name|f
operator|.
name|getParent
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|stat
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Missing parent:"
operator|+
name|f
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|stat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ParentNotDirectoryException
argument_list|(
literal|"parent is not a dir:"
operator|+
name|f
argument_list|)
throw|;
block|}
comment|// parent does exist - go ahead with create of file.
block|}
return|return
name|fsImpl
operator|.
name|primitiveCreate
argument_list|(
name|f
argument_list|,
name|absolutePermission
argument_list|,
name|flag
argument_list|,
name|bufferSize
argument_list|,
name|replication
argument_list|,
name|blockSize
argument_list|,
name|progress
argument_list|,
name|checksumOpt
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|delete (Path f, boolean recursive)
specifier|public
name|boolean
name|delete
parameter_list|(
name|Path
name|f
parameter_list|,
name|boolean
name|recursive
parameter_list|)
throws|throws
name|IOException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|fsImpl
operator|.
name|delete
argument_list|(
name|f
argument_list|,
name|recursive
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFileBlockLocations (Path f, long start, long len)
specifier|public
name|BlockLocation
index|[]
name|getFileBlockLocations
parameter_list|(
name|Path
name|f
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|fsImpl
operator|.
name|getFileBlockLocations
argument_list|(
name|f
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFileChecksum (Path f)
specifier|public
name|FileChecksum
name|getFileChecksum
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|fsImpl
operator|.
name|getFileChecksum
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFileStatus (Path f)
specifier|public
name|FileStatus
name|getFileStatus
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|fsImpl
operator|.
name|getFileStatus
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFileLinkStatus (final Path f)
specifier|public
name|FileStatus
name|getFileLinkStatus
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getFileStatus
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFsStatus ()
specifier|public
name|FsStatus
name|getFsStatus
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|fsImpl
operator|.
name|getStatus
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getServerDefaults ()
specifier|public
name|FsServerDefaults
name|getServerDefaults
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|fsImpl
operator|.
name|getServerDefaults
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getUriDefaultPort ()
specifier|public
name|int
name|getUriDefaultPort
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|listStatus (Path f)
specifier|public
name|FileStatus
index|[]
name|listStatus
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|fsImpl
operator|.
name|listStatus
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
comment|// call to primitiveMkdir
DECL|method|mkdir (Path dir, FsPermission permission, boolean createParent)
specifier|public
name|void
name|mkdir
parameter_list|(
name|Path
name|dir
parameter_list|,
name|FsPermission
name|permission
parameter_list|,
name|boolean
name|createParent
parameter_list|)
throws|throws
name|IOException
block|{
name|checkPath
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|fsImpl
operator|.
name|primitiveMkdir
argument_list|(
name|dir
argument_list|,
name|permission
argument_list|,
name|createParent
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|open (Path f, int bufferSize)
specifier|public
name|FSDataInputStream
name|open
parameter_list|(
name|Path
name|f
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|fsImpl
operator|.
name|open
argument_list|(
name|f
argument_list|,
name|bufferSize
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
comment|// call to rename
DECL|method|renameInternal (Path src, Path dst)
specifier|public
name|void
name|renameInternal
parameter_list|(
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
name|checkPath
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|checkPath
argument_list|(
name|dst
argument_list|)
expr_stmt|;
name|fsImpl
operator|.
name|rename
argument_list|(
name|src
argument_list|,
name|dst
argument_list|,
name|Options
operator|.
name|Rename
operator|.
name|NONE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setOwner (Path f, String username, String groupname)
specifier|public
name|void
name|setOwner
parameter_list|(
name|Path
name|f
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|groupname
parameter_list|)
throws|throws
name|IOException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|fsImpl
operator|.
name|setOwner
argument_list|(
name|f
argument_list|,
name|username
argument_list|,
name|groupname
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setPermission (Path f, FsPermission permission)
specifier|public
name|void
name|setPermission
parameter_list|(
name|Path
name|f
parameter_list|,
name|FsPermission
name|permission
parameter_list|)
throws|throws
name|IOException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|fsImpl
operator|.
name|setPermission
argument_list|(
name|f
argument_list|,
name|permission
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setReplication (Path f, short replication)
specifier|public
name|boolean
name|setReplication
parameter_list|(
name|Path
name|f
parameter_list|,
name|short
name|replication
parameter_list|)
throws|throws
name|IOException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|fsImpl
operator|.
name|setReplication
argument_list|(
name|f
argument_list|,
name|replication
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setTimes (Path f, long mtime, long atime)
specifier|public
name|void
name|setTimes
parameter_list|(
name|Path
name|f
parameter_list|,
name|long
name|mtime
parameter_list|,
name|long
name|atime
parameter_list|)
throws|throws
name|IOException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|fsImpl
operator|.
name|setTimes
argument_list|(
name|f
argument_list|,
name|mtime
argument_list|,
name|atime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setVerifyChecksum (boolean verifyChecksum)
specifier|public
name|void
name|setVerifyChecksum
parameter_list|(
name|boolean
name|verifyChecksum
parameter_list|)
throws|throws
name|IOException
block|{
name|fsImpl
operator|.
name|setVerifyChecksum
argument_list|(
name|verifyChecksum
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|supportsSymlinks ()
specifier|public
name|boolean
name|supportsSymlinks
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|createSymlink (Path target, Path link, boolean createParent)
specifier|public
name|void
name|createSymlink
parameter_list|(
name|Path
name|target
parameter_list|,
name|Path
name|link
parameter_list|,
name|boolean
name|createParent
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"File system does not support symlinks"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getLinkTarget (final Path f)
specifier|public
name|Path
name|getLinkTarget
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* We should never get here. Any file system that threw an       * UnresolvedLinkException, causing this function to be called,      * should override getLinkTarget.       */
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
annotation|@
name|Override
comment|//AbstractFileSystem
DECL|method|getCanonicalServiceName ()
specifier|public
name|String
name|getCanonicalServiceName
parameter_list|()
block|{
return|return
name|fsImpl
operator|.
name|getCanonicalServiceName
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|//AbstractFileSystem
DECL|method|getDelegationTokens (String renewer)
specifier|public
name|List
argument_list|<
name|Token
argument_list|<
name|?
argument_list|>
argument_list|>
name|getDelegationTokens
parameter_list|(
name|String
name|renewer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|fsImpl
operator|.
name|addDelegationTokens
argument_list|(
name|renewer
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

