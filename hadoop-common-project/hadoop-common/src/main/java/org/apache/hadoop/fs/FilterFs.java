begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|fs
operator|.
name|FileSystem
operator|.
name|Statistics
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
name|security
operator|.
name|AccessControlException
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
comment|/**  * A<code>FilterFs</code> contains some other file system, which it uses as its  * basic file system, possibly transforming the data along the way or providing  * additional functionality. The class<code>FilterFs</code> itself simply  * overrides all methods of<code>AbstractFileSystem</code> with versions that  * pass all requests to the contained file system. Subclasses of  *<code>FilterFs</code> may further override some of these methods and may also  * provide additional methods and fields.  *   */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
comment|/*Evolving for a release,to be changed to Stable */
DECL|class|FilterFs
specifier|public
specifier|abstract
class|class
name|FilterFs
extends|extends
name|AbstractFileSystem
block|{
DECL|field|myFs
specifier|private
specifier|final
name|AbstractFileSystem
name|myFs
decl_stmt|;
DECL|method|getMyFs ()
specifier|protected
name|AbstractFileSystem
name|getMyFs
parameter_list|()
block|{
return|return
name|myFs
return|;
block|}
DECL|method|FilterFs (AbstractFileSystem fs)
specifier|protected
name|FilterFs
parameter_list|(
name|AbstractFileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|super
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
argument_list|,
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|getScheme
argument_list|()
argument_list|,
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|getAuthority
argument_list|()
operator|!=
literal|null
argument_list|,
name|fs
operator|.
name|getUriDefaultPort
argument_list|()
argument_list|)
expr_stmt|;
name|myFs
operator|=
name|fs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStatistics ()
specifier|public
name|Statistics
name|getStatistics
parameter_list|()
block|{
return|return
name|myFs
operator|.
name|getStatistics
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|makeQualified (Path path)
specifier|public
name|Path
name|makeQualified
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
name|myFs
operator|.
name|makeQualified
argument_list|(
name|path
argument_list|)
return|;
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
name|myFs
operator|.
name|getInitialWorkingDirectory
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getHomeDirectory ()
specifier|public
name|Path
name|getHomeDirectory
parameter_list|()
block|{
return|return
name|myFs
operator|.
name|getHomeDirectory
argument_list|()
return|;
block|}
annotation|@
name|Override
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
throws|,
name|UnresolvedLinkException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|myFs
operator|.
name|createInternal
argument_list|(
name|f
argument_list|,
name|flag
argument_list|,
name|absolutePermission
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
argument_list|,
name|createParent
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
throws|,
name|UnresolvedLinkException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|myFs
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
throws|,
name|UnresolvedLinkException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|myFs
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
throws|,
name|UnresolvedLinkException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|myFs
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
throws|,
name|UnresolvedLinkException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|myFs
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
throws|,
name|UnresolvedLinkException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|myFs
operator|.
name|getFileLinkStatus
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFsStatus (final Path f)
specifier|public
name|FsStatus
name|getFsStatus
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnresolvedLinkException
throws|,
name|IOException
block|{
return|return
name|myFs
operator|.
name|getFsStatus
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
name|myFs
operator|.
name|getFsStatus
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
name|myFs
operator|.
name|getServerDefaults
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|resolvePath (final Path p)
specifier|public
name|Path
name|resolvePath
parameter_list|(
specifier|final
name|Path
name|p
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|UnresolvedLinkException
throws|,
name|AccessControlException
throws|,
name|IOException
block|{
return|return
name|myFs
operator|.
name|resolvePath
argument_list|(
name|p
argument_list|)
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
name|myFs
operator|.
name|getUriDefaultPort
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getUri ()
specifier|public
name|URI
name|getUri
parameter_list|()
block|{
return|return
name|myFs
operator|.
name|getUri
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|checkPath (Path path)
specifier|public
name|void
name|checkPath
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
name|myFs
operator|.
name|checkPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUriPath (final Path p)
specifier|public
name|String
name|getUriPath
parameter_list|(
specifier|final
name|Path
name|p
parameter_list|)
block|{
return|return
name|myFs
operator|.
name|getUriPath
argument_list|(
name|p
argument_list|)
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
throws|,
name|UnresolvedLinkException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|myFs
operator|.
name|listStatus
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|listCorruptFileBlocks (Path path)
specifier|public
name|RemoteIterator
argument_list|<
name|Path
argument_list|>
name|listCorruptFileBlocks
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|myFs
operator|.
name|listCorruptFileBlocks
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
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
throws|,
name|UnresolvedLinkException
block|{
name|checkPath
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|myFs
operator|.
name|mkdir
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
DECL|method|open (final Path f)
specifier|public
name|FSDataInputStream
name|open
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnresolvedLinkException
throws|,
name|IOException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|myFs
operator|.
name|open
argument_list|(
name|f
argument_list|)
return|;
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
throws|,
name|UnresolvedLinkException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|myFs
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
throws|,
name|UnresolvedLinkException
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
name|myFs
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
DECL|method|renameInternal (final Path src, final Path dst, boolean overwrite)
specifier|public
name|void
name|renameInternal
parameter_list|(
specifier|final
name|Path
name|src
parameter_list|,
specifier|final
name|Path
name|dst
parameter_list|,
name|boolean
name|overwrite
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileAlreadyExistsException
throws|,
name|FileNotFoundException
throws|,
name|ParentNotDirectoryException
throws|,
name|UnresolvedLinkException
throws|,
name|IOException
block|{
name|myFs
operator|.
name|renameInternal
argument_list|(
name|src
argument_list|,
name|dst
argument_list|,
name|overwrite
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
throws|,
name|UnresolvedLinkException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|myFs
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
throws|,
name|UnresolvedLinkException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|myFs
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
throws|,
name|UnresolvedLinkException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|myFs
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
throws|,
name|UnresolvedLinkException
block|{
name|checkPath
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|myFs
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
throws|,
name|UnresolvedLinkException
block|{
name|myFs
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
name|myFs
operator|.
name|supportsSymlinks
argument_list|()
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
throws|,
name|UnresolvedLinkException
block|{
name|myFs
operator|.
name|createSymlink
argument_list|(
name|target
argument_list|,
name|link
argument_list|,
name|createParent
argument_list|)
expr_stmt|;
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
return|return
name|myFs
operator|.
name|getLinkTarget
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
comment|// AbstractFileSystem
DECL|method|getCanonicalServiceName ()
specifier|public
name|String
name|getCanonicalServiceName
parameter_list|()
block|{
return|return
name|myFs
operator|.
name|getCanonicalServiceName
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|// AbstractFileSystem
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
name|myFs
operator|.
name|getDelegationTokens
argument_list|(
name|renewer
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isValidName (String src)
specifier|public
name|boolean
name|isValidName
parameter_list|(
name|String
name|src
parameter_list|)
block|{
return|return
name|myFs
operator|.
name|isValidName
argument_list|(
name|src
argument_list|)
return|;
block|}
block|}
end_class

end_unit

