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
name|*
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
name|ContentSummary
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
comment|/****************************************************************  * A<code>FilterFileSystem</code> contains  * some other file system, which it uses as  * its  basic file system, possibly transforming  * the data along the way or providing  additional  * functionality. The class<code>FilterFileSystem</code>  * itself simply overrides all  methods of  *<code>FileSystem</code> with versions that  * pass all requests to the contained  file  * system. Subclasses of<code>FilterFileSystem</code>  * may further override some of  these methods  * and may also provide additional methods  * and fields.  *  *****************************************************************/
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|FilterFileSystem
specifier|public
class|class
name|FilterFileSystem
extends|extends
name|FileSystem
block|{
DECL|field|fs
specifier|protected
name|FileSystem
name|fs
decl_stmt|;
DECL|field|swapScheme
specifier|private
name|String
name|swapScheme
decl_stmt|;
comment|/*    * so that extending classes can define it    */
DECL|method|FilterFileSystem ()
specifier|public
name|FilterFileSystem
parameter_list|()
block|{   }
DECL|method|FilterFileSystem (FileSystem fs)
specifier|public
name|FilterFileSystem
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
block|{
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|statistics
operator|=
name|fs
operator|.
name|statistics
expr_stmt|;
block|}
comment|/**    * Get the raw file system     * @return FileSystem being filtered    */
DECL|method|getRawFileSystem ()
specifier|public
name|FileSystem
name|getRawFileSystem
parameter_list|()
block|{
return|return
name|fs
return|;
block|}
comment|/** Called after a new FileSystem instance is constructed.    * @param name a uri whose authority section names the host, port, etc.    *   for this FileSystem    * @param conf the configuration    */
DECL|method|initialize (URI name, Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
name|URI
name|name
parameter_list|,
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
comment|// this is less than ideal, but existing filesystems sometimes neglect
comment|// to initialize the embedded filesystem
if|if
condition|(
name|fs
operator|.
name|getConf
argument_list|()
operator|==
literal|null
condition|)
block|{
name|fs
operator|.
name|initialize
argument_list|(
name|name
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
name|String
name|scheme
init|=
name|name
operator|.
name|getScheme
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|scheme
operator|.
name|equals
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|getScheme
argument_list|()
argument_list|)
condition|)
block|{
name|swapScheme
operator|=
name|scheme
expr_stmt|;
block|}
block|}
comment|/** Returns a URI whose scheme and authority identify this FileSystem.*/
DECL|method|getUri ()
specifier|public
name|URI
name|getUri
parameter_list|()
block|{
return|return
name|fs
operator|.
name|getUri
argument_list|()
return|;
block|}
comment|/**    * Returns a qualified URI whose scheme and authority identify this    * FileSystem.    */
annotation|@
name|Override
DECL|method|getCanonicalUri ()
specifier|protected
name|URI
name|getCanonicalUri
parameter_list|()
block|{
return|return
name|fs
operator|.
name|getCanonicalUri
argument_list|()
return|;
block|}
comment|/** Make sure that a path specifies a FileSystem. */
DECL|method|makeQualified (Path path)
specifier|public
name|Path
name|makeQualified
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
name|Path
name|fqPath
init|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|path
argument_list|)
decl_stmt|;
comment|// swap in our scheme if the filtered fs is using a different scheme
if|if
condition|(
name|swapScheme
operator|!=
literal|null
condition|)
block|{
try|try
block|{
comment|// NOTE: should deal with authority, but too much other stuff is broken
name|fqPath
operator|=
operator|new
name|Path
argument_list|(
operator|new
name|URI
argument_list|(
name|swapScheme
argument_list|,
name|fqPath
operator|.
name|toUri
argument_list|()
operator|.
name|getSchemeSpecificPart
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|fqPath
return|;
block|}
comment|///////////////////////////////////////////////////////////////
comment|// FileSystem
comment|///////////////////////////////////////////////////////////////
comment|/** Check that a Path belongs to this FileSystem. */
DECL|method|checkPath (Path path)
specifier|protected
name|void
name|checkPath
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
name|fs
operator|.
name|checkPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|getFileBlockLocations (FileStatus file, long start, long len)
specifier|public
name|BlockLocation
index|[]
name|getFileBlockLocations
parameter_list|(
name|FileStatus
name|file
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
return|return
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|file
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
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
name|IOException
block|{
return|return
name|fs
operator|.
name|resolvePath
argument_list|(
name|p
argument_list|)
return|;
block|}
comment|/**    * Opens an FSDataInputStream at the indicated Path.    * @param f the file name to open    * @param bufferSize the size of the buffer to be used.    */
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
return|return
name|fs
operator|.
name|open
argument_list|(
name|f
argument_list|,
name|bufferSize
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|append (Path f, int bufferSize, Progressable progress)
specifier|public
name|FSDataOutputStream
name|append
parameter_list|(
name|Path
name|f
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|append
argument_list|(
name|f
argument_list|,
name|bufferSize
argument_list|,
name|progress
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|create (Path f, FsPermission permission, boolean overwrite, int bufferSize, short replication, long blockSize, Progressable progress)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
name|Path
name|f
parameter_list|,
name|FsPermission
name|permission
parameter_list|,
name|boolean
name|overwrite
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
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|create
argument_list|(
name|f
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
comment|/**    * Set replication for an existing file.    *     * @param src file name    * @param replication new replication    * @throws IOException    * @return true if successful;    *         false if file does not exist or is a directory    */
DECL|method|setReplication (Path src, short replication)
specifier|public
name|boolean
name|setReplication
parameter_list|(
name|Path
name|src
parameter_list|,
name|short
name|replication
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|setReplication
argument_list|(
name|src
argument_list|,
name|replication
argument_list|)
return|;
block|}
comment|/**    * Renames Path src to Path dst.  Can take place on local fs    * or remote DFS.    */
DECL|method|rename (Path src, Path dst)
specifier|public
name|boolean
name|rename
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
return|return
name|fs
operator|.
name|rename
argument_list|(
name|src
argument_list|,
name|dst
argument_list|)
return|;
block|}
comment|/** Delete a file */
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
return|return
name|fs
operator|.
name|delete
argument_list|(
name|f
argument_list|,
name|recursive
argument_list|)
return|;
block|}
comment|/**    * Mark a path to be deleted when FileSystem is closed.    * When the JVM shuts down,    * all FileSystem objects will be closed automatically.    * Then,    * the marked path will be deleted as a result of closing the FileSystem.    *    * The path has to exist in the file system.    *     * @param f the path to delete.    * @return  true if deleteOnExit is successful, otherwise false.    * @throws IOException    */
DECL|method|deleteOnExit (Path f)
specifier|public
name|boolean
name|deleteOnExit
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|deleteOnExit
argument_list|(
name|f
argument_list|)
return|;
block|}
comment|/** List files in a directory. */
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
return|return
name|fs
operator|.
name|listStatus
argument_list|(
name|f
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    */
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
name|fs
operator|.
name|listCorruptFileBlocks
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/** List files and its block locations in a directory. */
DECL|method|listLocatedStatus (Path f)
specifier|public
name|RemoteIterator
argument_list|<
name|LocatedFileStatus
argument_list|>
name|listLocatedStatus
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|listLocatedStatus
argument_list|(
name|f
argument_list|)
return|;
block|}
DECL|method|getHomeDirectory ()
specifier|public
name|Path
name|getHomeDirectory
parameter_list|()
block|{
return|return
name|fs
operator|.
name|getHomeDirectory
argument_list|()
return|;
block|}
comment|/**    * Set the current working directory for the given file system. All relative    * paths will be resolved relative to it.    *     * @param newDir    */
DECL|method|setWorkingDirectory (Path newDir)
specifier|public
name|void
name|setWorkingDirectory
parameter_list|(
name|Path
name|newDir
parameter_list|)
block|{
name|fs
operator|.
name|setWorkingDirectory
argument_list|(
name|newDir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the current working directory for the given file system    *     * @return the directory pathname    */
DECL|method|getWorkingDirectory ()
specifier|public
name|Path
name|getWorkingDirectory
parameter_list|()
block|{
return|return
name|fs
operator|.
name|getWorkingDirectory
argument_list|()
return|;
block|}
DECL|method|getInitialWorkingDirectory ()
specifier|protected
name|Path
name|getInitialWorkingDirectory
parameter_list|()
block|{
return|return
name|fs
operator|.
name|getInitialWorkingDirectory
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
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
name|fs
operator|.
name|getStatus
argument_list|(
name|p
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|mkdirs (Path f, FsPermission permission)
specifier|public
name|boolean
name|mkdirs
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
return|return
name|fs
operator|.
name|mkdirs
argument_list|(
name|f
argument_list|,
name|permission
argument_list|)
return|;
block|}
comment|/**    * The src file is on the local disk.  Add it to FS at    * the given dst name.    * delSrc indicates if the source should be removed    */
DECL|method|copyFromLocalFile (boolean delSrc, Path src, Path dst)
specifier|public
name|void
name|copyFromLocalFile
parameter_list|(
name|boolean
name|delSrc
parameter_list|,
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|copyFromLocalFile
argument_list|(
name|delSrc
argument_list|,
name|src
argument_list|,
name|dst
argument_list|)
expr_stmt|;
block|}
comment|/**    * The src files are on the local disk.  Add it to FS at    * the given dst name.    * delSrc indicates if the source should be removed    */
DECL|method|copyFromLocalFile (boolean delSrc, boolean overwrite, Path[] srcs, Path dst)
specifier|public
name|void
name|copyFromLocalFile
parameter_list|(
name|boolean
name|delSrc
parameter_list|,
name|boolean
name|overwrite
parameter_list|,
name|Path
index|[]
name|srcs
parameter_list|,
name|Path
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|copyFromLocalFile
argument_list|(
name|delSrc
argument_list|,
name|overwrite
argument_list|,
name|srcs
argument_list|,
name|dst
argument_list|)
expr_stmt|;
block|}
comment|/**    * The src file is on the local disk.  Add it to FS at    * the given dst name.    * delSrc indicates if the source should be removed    */
DECL|method|copyFromLocalFile (boolean delSrc, boolean overwrite, Path src, Path dst)
specifier|public
name|void
name|copyFromLocalFile
parameter_list|(
name|boolean
name|delSrc
parameter_list|,
name|boolean
name|overwrite
parameter_list|,
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|copyFromLocalFile
argument_list|(
name|delSrc
argument_list|,
name|overwrite
argument_list|,
name|src
argument_list|,
name|dst
argument_list|)
expr_stmt|;
block|}
comment|/**    * The src file is under FS, and the dst is on the local disk.    * Copy it from FS control to the local dst name.    * delSrc indicates if the src will be removed or not.    */
DECL|method|copyToLocalFile (boolean delSrc, Path src, Path dst)
specifier|public
name|void
name|copyToLocalFile
parameter_list|(
name|boolean
name|delSrc
parameter_list|,
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|copyToLocalFile
argument_list|(
name|delSrc
argument_list|,
name|src
argument_list|,
name|dst
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a local File that the user can write output to.  The caller    * provides both the eventual FS target name and the local working    * file.  If the FS is local, we write directly into the target.  If    * the FS is remote, we write into the tmp local area.    */
DECL|method|startLocalOutput (Path fsOutputFile, Path tmpLocalFile)
specifier|public
name|Path
name|startLocalOutput
parameter_list|(
name|Path
name|fsOutputFile
parameter_list|,
name|Path
name|tmpLocalFile
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|startLocalOutput
argument_list|(
name|fsOutputFile
argument_list|,
name|tmpLocalFile
argument_list|)
return|;
block|}
comment|/**    * Called when we're all done writing to the target.  A local FS will    * do nothing, because we've written to exactly the right place.  A remote    * FS will copy the contents of tmpLocalFile to the correct target at    * fsOutputFile.    */
DECL|method|completeLocalOutput (Path fsOutputFile, Path tmpLocalFile)
specifier|public
name|void
name|completeLocalOutput
parameter_list|(
name|Path
name|fsOutputFile
parameter_list|,
name|Path
name|tmpLocalFile
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|completeLocalOutput
argument_list|(
name|fsOutputFile
argument_list|,
name|tmpLocalFile
argument_list|)
expr_stmt|;
block|}
comment|/** Return the total size of all files in the filesystem.*/
DECL|method|getUsed ()
specifier|public
name|long
name|getUsed
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|getUsed
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultBlockSize ()
specifier|public
name|long
name|getDefaultBlockSize
parameter_list|()
block|{
return|return
name|fs
operator|.
name|getDefaultBlockSize
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultReplication ()
specifier|public
name|short
name|getDefaultReplication
parameter_list|()
block|{
return|return
name|fs
operator|.
name|getDefaultReplication
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
name|fs
operator|.
name|getServerDefaults
argument_list|()
return|;
block|}
comment|// path variants delegate to underlying filesystem
annotation|@
name|Override
DECL|method|getContentSummary (Path f)
specifier|public
name|ContentSummary
name|getContentSummary
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|getContentSummary
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultBlockSize (Path f)
specifier|public
name|long
name|getDefaultBlockSize
parameter_list|(
name|Path
name|f
parameter_list|)
block|{
return|return
name|fs
operator|.
name|getDefaultBlockSize
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultReplication (Path f)
specifier|public
name|short
name|getDefaultReplication
parameter_list|(
name|Path
name|f
parameter_list|)
block|{
return|return
name|fs
operator|.
name|getDefaultReplication
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getServerDefaults (Path f)
specifier|public
name|FsServerDefaults
name|getServerDefaults
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|getServerDefaults
argument_list|(
name|f
argument_list|)
return|;
block|}
comment|/**    * Get file status.    */
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
return|return
name|fs
operator|.
name|getFileStatus
argument_list|(
name|f
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
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
return|return
name|fs
operator|.
name|getFileChecksum
argument_list|(
name|f
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|setVerifyChecksum (boolean verifyChecksum)
specifier|public
name|void
name|setVerifyChecksum
parameter_list|(
name|boolean
name|verifyChecksum
parameter_list|)
block|{
name|fs
operator|.
name|setVerifyChecksum
argument_list|(
name|verifyChecksum
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setWriteChecksum (boolean writeChecksum)
specifier|public
name|void
name|setWriteChecksum
parameter_list|(
name|boolean
name|writeChecksum
parameter_list|)
block|{
name|fs
operator|.
name|setVerifyChecksum
argument_list|(
name|writeChecksum
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|fs
operator|.
name|getConf
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|setOwner (Path p, String username, String groupname )
specifier|public
name|void
name|setOwner
parameter_list|(
name|Path
name|p
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
name|fs
operator|.
name|setOwner
argument_list|(
name|p
argument_list|,
name|username
argument_list|,
name|groupname
argument_list|)
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|setTimes (Path p, long mtime, long atime )
specifier|public
name|void
name|setTimes
parameter_list|(
name|Path
name|p
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
name|fs
operator|.
name|setTimes
argument_list|(
name|p
argument_list|,
name|mtime
argument_list|,
name|atime
argument_list|)
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|setPermission (Path p, FsPermission permission )
specifier|public
name|void
name|setPermission
parameter_list|(
name|Path
name|p
parameter_list|,
name|FsPermission
name|permission
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|setPermission
argument_list|(
name|p
argument_list|,
name|permission
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|primitiveCreate (Path f, FsPermission absolutePermission, EnumSet<CreateFlag> flag, int bufferSize, short replication, long blockSize, Progressable progress, int bytesPerChecksum)
specifier|protected
name|FSDataOutputStream
name|primitiveCreate
parameter_list|(
name|Path
name|f
parameter_list|,
name|FsPermission
name|absolutePermission
parameter_list|,
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|flag
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
name|int
name|bytesPerChecksum
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
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
name|bytesPerChecksum
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
DECL|method|primitiveMkdir (Path f, FsPermission abdolutePermission)
specifier|protected
name|boolean
name|primitiveMkdir
parameter_list|(
name|Path
name|f
parameter_list|,
name|FsPermission
name|abdolutePermission
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|primitiveMkdir
argument_list|(
name|f
argument_list|,
name|abdolutePermission
argument_list|)
return|;
block|}
annotation|@
name|Override
comment|// FileSystem
DECL|method|getCanonicalServiceName ()
specifier|public
name|String
name|getCanonicalServiceName
parameter_list|()
block|{
return|return
name|fs
operator|.
name|getCanonicalServiceName
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|// FileSystem
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|getDelegationToken (String renewer)
specifier|public
name|Token
argument_list|<
name|?
argument_list|>
name|getDelegationToken
parameter_list|(
name|String
name|renewer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|getDelegationToken
argument_list|(
name|renewer
argument_list|)
return|;
block|}
annotation|@
name|Override
comment|// FileSystem
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
name|fs
operator|.
name|getDelegationTokens
argument_list|(
name|renewer
argument_list|)
return|;
block|}
annotation|@
name|Override
comment|// FileSystem
DECL|method|getDelegationTokens (String renewer, Credentials credentials)
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
parameter_list|,
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|getDelegationTokens
argument_list|(
name|renewer
argument_list|,
name|credentials
argument_list|)
return|;
block|}
block|}
end_class

end_unit

