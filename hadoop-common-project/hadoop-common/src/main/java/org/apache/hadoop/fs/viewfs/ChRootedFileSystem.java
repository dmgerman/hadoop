begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.viewfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|viewfs
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
name|BlockLocation
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
name|FilterFileSystem
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
name|FsServerDefaults
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
name|FsStatus
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
name|util
operator|.
name|Progressable
import|;
end_import

begin_comment
comment|/**  *<code>ChRootedFileSystem</code> is a file system with its root some path  * below the root of its base file system.   *   * Example: For a base file system hdfs://nn1/ with chRoot at /usr/foo, the  * members will be setup as shown below.  *<ul>  *<li>myFs is the base file system and points to hdfs at nn1</li>  *<li>myURI is hdfs://nn1/user/foo</li>  *<li>chRootPathPart is /user/foo</li>  *<li>workingDir is a directory related to chRoot</li>  *</ul>  *   * The paths are resolved as follows by ChRootedFileSystem:  *<ul>  *<li> Absolute path /a/b/c is resolved to /user/foo/a/b/c at myFs</li>  *<li> Relative path x/y is resolved to /user/foo/<workingDir>/x/y</li>  *</ul>  */
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
DECL|class|ChRootedFileSystem
class|class
name|ChRootedFileSystem
extends|extends
name|FilterFileSystem
block|{
DECL|field|myUri
specifier|private
specifier|final
name|URI
name|myUri
decl_stmt|;
comment|// the base URI + the chRoot
DECL|field|chRootPathPart
specifier|private
specifier|final
name|Path
name|chRootPathPart
decl_stmt|;
comment|// the root below the root of the base
DECL|field|chRootPathPartString
specifier|private
specifier|final
name|String
name|chRootPathPartString
decl_stmt|;
DECL|field|workingDir
specifier|private
name|Path
name|workingDir
decl_stmt|;
DECL|method|getMyFs ()
specifier|protected
name|FileSystem
name|getMyFs
parameter_list|()
block|{
return|return
name|getRawFileSystem
argument_list|()
return|;
block|}
comment|/**    * @param path    * @return  full path including the chroot     */
DECL|method|fullPath (final Path path)
specifier|protected
name|Path
name|fullPath
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
block|{
name|super
operator|.
name|checkPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|path
operator|.
name|isAbsolute
argument_list|()
condition|?
operator|new
name|Path
argument_list|(
name|chRootPathPartString
operator|+
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
else|:
operator|new
name|Path
argument_list|(
name|chRootPathPartString
operator|+
name|workingDir
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|path
argument_list|)
return|;
block|}
comment|/**    * Constructor    * @param uri base file system    * @param conf configuration    * @throws IOException     */
DECL|method|ChRootedFileSystem (final URI uri, Configuration conf)
specifier|public
name|ChRootedFileSystem
parameter_list|(
specifier|final
name|URI
name|uri
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|FileSystem
operator|.
name|get
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|chRootPathPart
operator|=
operator|new
name|Path
argument_list|(
name|uri
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|chRootPathPartString
operator|=
name|chRootPathPart
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|myUri
operator|=
name|uri
expr_stmt|;
name|workingDir
operator|=
name|getHomeDirectory
argument_list|()
expr_stmt|;
comment|// We don't use the wd of the myFs
block|}
comment|/**     * Called after a new FileSystem instance is constructed.    * @param name a uri whose authority section names the host, port, etc.    *   for this FileSystem    * @param conf the configuration    */
DECL|method|initialize (final URI name, final Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
specifier|final
name|URI
name|name
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|initialize
argument_list|(
name|name
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
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
name|myUri
return|;
block|}
comment|/**    * Strip out the root from the path.    * @param p - fully qualified path p    * @return -  the remaining path  without the begining /    * @throws IOException if the p is not prefixed with root    */
DECL|method|stripOutRoot (final Path p)
name|String
name|stripOutRoot
parameter_list|(
specifier|final
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|checkPath
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Internal Error - path "
operator|+
name|p
operator|+
literal|" should have been with URI: "
operator|+
name|myUri
argument_list|)
throw|;
block|}
name|String
name|pathPart
init|=
name|p
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
return|return
operator|(
name|pathPart
operator|.
name|length
argument_list|()
operator|==
name|chRootPathPartString
operator|.
name|length
argument_list|()
operator|)
condition|?
literal|""
else|:
name|pathPart
operator|.
name|substring
argument_list|(
name|chRootPathPartString
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getInitialWorkingDirectory ()
specifier|protected
name|Path
name|getInitialWorkingDirectory
parameter_list|()
block|{
comment|/*      * 3 choices here:       *     null or / or /user/<uname> or strip out the root out of myFs's      *  inital wd.       * Only reasonable choice for initialWd for chrooted fds is null       * so that the default rule for wd is applied      */
return|return
literal|null
return|;
block|}
DECL|method|getResolvedQualifiedPath (final Path f)
specifier|public
name|Path
name|getResolvedQualifiedPath
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
return|return
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|chRootPathPartString
operator|+
name|f
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
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
operator|new
name|Path
argument_list|(
literal|"/user/"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|getUri
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getWorkingDirectory ()
specifier|public
name|Path
name|getWorkingDirectory
parameter_list|()
block|{
return|return
name|workingDir
return|;
block|}
annotation|@
name|Override
DECL|method|setWorkingDirectory (final Path new_dir)
specifier|public
name|void
name|setWorkingDirectory
parameter_list|(
specifier|final
name|Path
name|new_dir
parameter_list|)
block|{
name|workingDir
operator|=
name|new_dir
operator|.
name|isAbsolute
argument_list|()
condition|?
name|new_dir
else|:
operator|new
name|Path
argument_list|(
name|workingDir
argument_list|,
name|new_dir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create (final Path f, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|,
specifier|final
name|FsPermission
name|permission
parameter_list|,
specifier|final
name|boolean
name|overwrite
parameter_list|,
specifier|final
name|int
name|bufferSize
parameter_list|,
specifier|final
name|short
name|replication
parameter_list|,
specifier|final
name|long
name|blockSize
parameter_list|,
specifier|final
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|create
argument_list|(
name|fullPath
argument_list|(
name|f
argument_list|)
argument_list|,
name|permission
argument_list|,
name|overwrite
argument_list|,
name|bufferSize
argument_list|,
name|replication
argument_list|,
name|blockSize
argument_list|,
name|progress
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|delete (final Path f, final boolean recursive)
specifier|public
name|boolean
name|delete
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|,
specifier|final
name|boolean
name|recursive
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|delete
argument_list|(
name|fullPath
argument_list|(
name|f
argument_list|)
argument_list|,
name|recursive
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
DECL|method|delete (Path f)
specifier|public
name|boolean
name|delete
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delete
argument_list|(
name|f
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFileBlockLocations (final FileStatus fs, final long start, final long len)
specifier|public
name|BlockLocation
index|[]
name|getFileBlockLocations
parameter_list|(
specifier|final
name|FileStatus
name|fs
parameter_list|,
specifier|final
name|long
name|start
parameter_list|,
specifier|final
name|long
name|len
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|getFileBlockLocations
argument_list|(
operator|new
name|ViewFsFileStatus
argument_list|(
name|fs
argument_list|,
name|fullPath
argument_list|(
name|fs
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFileChecksum (final Path f)
specifier|public
name|FileChecksum
name|getFileChecksum
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|getFileChecksum
argument_list|(
name|fullPath
argument_list|(
name|f
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFileStatus (final Path f)
specifier|public
name|FileStatus
name|getFileStatus
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|getFileStatus
argument_list|(
name|fullPath
argument_list|(
name|f
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getStatus (Path p)
specifier|public
name|FsStatus
name|getStatus
parameter_list|(
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|getStatus
argument_list|(
name|fullPath
argument_list|(
name|p
argument_list|)
argument_list|)
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
name|super
operator|.
name|getServerDefaults
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|listStatus (final Path f)
specifier|public
name|FileStatus
index|[]
name|listStatus
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|listStatus
argument_list|(
name|fullPath
argument_list|(
name|f
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|mkdirs (final Path f, final FsPermission permission)
specifier|public
name|boolean
name|mkdirs
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|,
specifier|final
name|FsPermission
name|permission
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|mkdirs
argument_list|(
name|fullPath
argument_list|(
name|f
argument_list|)
argument_list|,
name|permission
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|open (final Path f, final int bufferSize)
specifier|public
name|FSDataInputStream
name|open
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|,
specifier|final
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|open
argument_list|(
name|fullPath
argument_list|(
name|f
argument_list|)
argument_list|,
name|bufferSize
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|append (final Path f, final int bufferSize, final Progressable progress)
specifier|public
name|FSDataOutputStream
name|append
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|,
specifier|final
name|int
name|bufferSize
parameter_list|,
specifier|final
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|append
argument_list|(
name|fullPath
argument_list|(
name|f
argument_list|)
argument_list|,
name|bufferSize
argument_list|,
name|progress
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|rename (final Path src, final Path dst)
specifier|public
name|boolean
name|rename
parameter_list|(
specifier|final
name|Path
name|src
parameter_list|,
specifier|final
name|Path
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
comment|// note fullPath will check that paths are relative to this FileSystem.
comment|// Hence both are in same file system and a rename is valid
return|return
name|super
operator|.
name|rename
argument_list|(
name|fullPath
argument_list|(
name|src
argument_list|)
argument_list|,
name|fullPath
argument_list|(
name|dst
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setOwner (final Path f, final String username, final String groupname)
specifier|public
name|void
name|setOwner
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|,
specifier|final
name|String
name|username
parameter_list|,
specifier|final
name|String
name|groupname
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|setOwner
argument_list|(
name|fullPath
argument_list|(
name|f
argument_list|)
argument_list|,
name|username
argument_list|,
name|groupname
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setPermission (final Path f, final FsPermission permission)
specifier|public
name|void
name|setPermission
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|,
specifier|final
name|FsPermission
name|permission
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|setPermission
argument_list|(
name|fullPath
argument_list|(
name|f
argument_list|)
argument_list|,
name|permission
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setReplication (final Path f, final short replication)
specifier|public
name|boolean
name|setReplication
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|,
specifier|final
name|short
name|replication
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|setReplication
argument_list|(
name|fullPath
argument_list|(
name|f
argument_list|)
argument_list|,
name|replication
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setTimes (final Path f, final long mtime, final long atime)
specifier|public
name|void
name|setTimes
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|,
specifier|final
name|long
name|mtime
parameter_list|,
specifier|final
name|long
name|atime
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|setTimes
argument_list|(
name|fullPath
argument_list|(
name|f
argument_list|)
argument_list|,
name|mtime
argument_list|,
name|atime
argument_list|)
expr_stmt|;
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
name|IOException
block|{
return|return
name|super
operator|.
name|resolvePath
argument_list|(
name|fullPath
argument_list|(
name|p
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

