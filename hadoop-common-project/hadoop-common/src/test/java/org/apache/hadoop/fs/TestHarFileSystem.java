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
name|permission
operator|.
name|AclEntry
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
name|AclStatus
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
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
name|Iterator
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
name|fs
operator|.
name|Options
operator|.
name|ChecksumOpt
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
name|fs
operator|.
name|Options
operator|.
name|CreateOpts
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
name|fs
operator|.
name|Options
operator|.
name|Rename
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|class|TestHarFileSystem
specifier|public
class|class
name|TestHarFileSystem
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestHarFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * FileSystem methods that must not be overwritten by    * {@link HarFileSystem}. Either because there is a default implementation    * already available or because it is not relevant.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|interface|MustNotImplement
specifier|private
interface|interface
name|MustNotImplement
block|{
DECL|method|getFileBlockLocations (Path p, long start, long len)
specifier|public
name|BlockLocation
index|[]
name|getFileBlockLocations
parameter_list|(
name|Path
name|p
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|len
parameter_list|)
function_decl|;
DECL|method|getLength (Path f)
specifier|public
name|long
name|getLength
parameter_list|(
name|Path
name|f
parameter_list|)
function_decl|;
DECL|method|append (Path f, int bufferSize)
specifier|public
name|FSDataOutputStream
name|append
parameter_list|(
name|Path
name|f
parameter_list|,
name|int
name|bufferSize
parameter_list|)
function_decl|;
DECL|method|rename (Path src, Path dst, Rename... options)
specifier|public
name|void
name|rename
parameter_list|(
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|,
name|Rename
modifier|...
name|options
parameter_list|)
function_decl|;
DECL|method|exists (Path f)
specifier|public
name|boolean
name|exists
parameter_list|(
name|Path
name|f
parameter_list|)
function_decl|;
DECL|method|isDirectory (Path f)
specifier|public
name|boolean
name|isDirectory
parameter_list|(
name|Path
name|f
parameter_list|)
function_decl|;
DECL|method|isFile (Path f)
specifier|public
name|boolean
name|isFile
parameter_list|(
name|Path
name|f
parameter_list|)
function_decl|;
DECL|method|createNewFile (Path f)
specifier|public
name|boolean
name|createNewFile
parameter_list|(
name|Path
name|f
parameter_list|)
function_decl|;
DECL|method|createNonRecursive (Path f, FsPermission permission, boolean overwrite, int bufferSize, short replication, long blockSize, Progressable progress)
specifier|public
name|FSDataOutputStream
name|createNonRecursive
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
function_decl|;
DECL|method|createNonRecursive (Path f, FsPermission permission, EnumSet<CreateFlag> flags, int bufferSize, short replication, long blockSize, Progressable progress)
specifier|public
name|FSDataOutputStream
name|createNonRecursive
parameter_list|(
name|Path
name|f
parameter_list|,
name|FsPermission
name|permission
parameter_list|,
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|flags
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
function_decl|;
DECL|method|createNonRecursive (Path f, FsPermission permission, EnumSet<CreateFlag> flags, int bufferSize, short replication, long blockSize, Progressable progress, ChecksumOpt checksumOpt)
specifier|public
name|FSDataOutputStream
name|createNonRecursive
parameter_list|(
name|Path
name|f
parameter_list|,
name|FsPermission
name|permission
parameter_list|,
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|flags
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
parameter_list|)
function_decl|;
DECL|method|mkdirs (Path f)
specifier|public
name|boolean
name|mkdirs
parameter_list|(
name|Path
name|f
parameter_list|)
function_decl|;
DECL|method|open (Path f)
specifier|public
name|FSDataInputStream
name|open
parameter_list|(
name|Path
name|f
parameter_list|)
function_decl|;
DECL|method|create (Path f)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
name|Path
name|f
parameter_list|)
function_decl|;
DECL|method|create (Path f, boolean overwrite)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
name|Path
name|f
parameter_list|,
name|boolean
name|overwrite
parameter_list|)
function_decl|;
DECL|method|create (Path f, Progressable progress)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
name|Path
name|f
parameter_list|,
name|Progressable
name|progress
parameter_list|)
function_decl|;
DECL|method|create (Path f, short replication)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
name|Path
name|f
parameter_list|,
name|short
name|replication
parameter_list|)
function_decl|;
DECL|method|create (Path f, short replication, Progressable progress)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
name|Path
name|f
parameter_list|,
name|short
name|replication
parameter_list|,
name|Progressable
name|progress
parameter_list|)
function_decl|;
DECL|method|create (Path f, boolean overwrite, int bufferSize)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
name|Path
name|f
parameter_list|,
name|boolean
name|overwrite
parameter_list|,
name|int
name|bufferSize
parameter_list|)
function_decl|;
DECL|method|create (Path f, boolean overwrite, int bufferSize, Progressable progress)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
name|Path
name|f
parameter_list|,
name|boolean
name|overwrite
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|Progressable
name|progress
parameter_list|)
function_decl|;
DECL|method|create (Path f, boolean overwrite, int bufferSize, short replication, long blockSize)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
name|Path
name|f
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
parameter_list|)
function_decl|;
DECL|method|create (Path f, boolean overwrite, int bufferSize, short replication, long blockSize, Progressable progress)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
name|Path
name|f
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
function_decl|;
DECL|method|create (Path f, FsPermission permission, EnumSet<CreateFlag> flags, int bufferSize, short replication, long blockSize, Progressable progress)
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
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|flags
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
function_decl|;
DECL|method|create (Path f, FsPermission permission, EnumSet<CreateFlag> flags, int bufferSize, short replication, long blockSize, Progressable progress, ChecksumOpt checksumOpt)
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
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|flags
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
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
DECL|method|delete (Path f)
specifier|public
name|boolean
name|delete
parameter_list|(
name|Path
name|f
parameter_list|)
function_decl|;
DECL|method|getReplication (Path src)
specifier|public
name|short
name|getReplication
parameter_list|(
name|Path
name|src
parameter_list|)
function_decl|;
DECL|method|processDeleteOnExit ()
specifier|public
name|void
name|processDeleteOnExit
parameter_list|()
function_decl|;
DECL|method|getContentSummary (Path f)
specifier|public
name|ContentSummary
name|getContentSummary
parameter_list|(
name|Path
name|f
parameter_list|)
function_decl|;
DECL|method|getStatus ()
specifier|public
name|FsStatus
name|getStatus
parameter_list|()
function_decl|;
DECL|method|listStatus (Path f, PathFilter filter)
specifier|public
name|FileStatus
index|[]
name|listStatus
parameter_list|(
name|Path
name|f
parameter_list|,
name|PathFilter
name|filter
parameter_list|)
function_decl|;
DECL|method|listStatus (Path[] files)
specifier|public
name|FileStatus
index|[]
name|listStatus
parameter_list|(
name|Path
index|[]
name|files
parameter_list|)
function_decl|;
DECL|method|listStatus (Path[] files, PathFilter filter)
specifier|public
name|FileStatus
index|[]
name|listStatus
parameter_list|(
name|Path
index|[]
name|files
parameter_list|,
name|PathFilter
name|filter
parameter_list|)
function_decl|;
DECL|method|globStatus (Path pathPattern)
specifier|public
name|FileStatus
index|[]
name|globStatus
parameter_list|(
name|Path
name|pathPattern
parameter_list|)
function_decl|;
DECL|method|globStatus (Path pathPattern, PathFilter filter)
specifier|public
name|FileStatus
index|[]
name|globStatus
parameter_list|(
name|Path
name|pathPattern
parameter_list|,
name|PathFilter
name|filter
parameter_list|)
function_decl|;
DECL|method|listFiles (Path path, boolean isRecursive)
specifier|public
name|Iterator
argument_list|<
name|LocatedFileStatus
argument_list|>
name|listFiles
parameter_list|(
name|Path
name|path
parameter_list|,
name|boolean
name|isRecursive
parameter_list|)
function_decl|;
DECL|method|listLocatedStatus (Path f)
specifier|public
name|Iterator
argument_list|<
name|LocatedFileStatus
argument_list|>
name|listLocatedStatus
parameter_list|(
name|Path
name|f
parameter_list|)
function_decl|;
DECL|method|listLocatedStatus (Path f, PathFilter filter)
specifier|public
name|Iterator
argument_list|<
name|LocatedFileStatus
argument_list|>
name|listLocatedStatus
parameter_list|(
name|Path
name|f
parameter_list|,
name|PathFilter
name|filter
parameter_list|)
function_decl|;
DECL|method|copyFromLocalFile (Path src, Path dst)
specifier|public
name|void
name|copyFromLocalFile
parameter_list|(
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|)
function_decl|;
DECL|method|moveFromLocalFile (Path[] srcs, Path dst)
specifier|public
name|void
name|moveFromLocalFile
parameter_list|(
name|Path
index|[]
name|srcs
parameter_list|,
name|Path
name|dst
parameter_list|)
function_decl|;
DECL|method|moveFromLocalFile (Path src, Path dst)
specifier|public
name|void
name|moveFromLocalFile
parameter_list|(
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|)
function_decl|;
DECL|method|copyToLocalFile (Path src, Path dst)
specifier|public
name|void
name|copyToLocalFile
parameter_list|(
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|)
function_decl|;
DECL|method|copyToLocalFile (boolean delSrc, Path src, Path dst, boolean useRawLocalFileSystem)
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
parameter_list|,
name|boolean
name|useRawLocalFileSystem
parameter_list|)
function_decl|;
DECL|method|moveToLocalFile (Path src, Path dst)
specifier|public
name|void
name|moveToLocalFile
parameter_list|(
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|)
function_decl|;
DECL|method|getBlockSize (Path f)
specifier|public
name|long
name|getBlockSize
parameter_list|(
name|Path
name|f
parameter_list|)
function_decl|;
DECL|method|primitiveCreate (Path f, EnumSet<CreateFlag> createFlag, CreateOpts... opts)
specifier|public
name|FSDataOutputStream
name|primitiveCreate
parameter_list|(
name|Path
name|f
parameter_list|,
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|createFlag
parameter_list|,
name|CreateOpts
modifier|...
name|opts
parameter_list|)
function_decl|;
DECL|method|primitiveMkdir (Path f, FsPermission absolutePermission, boolean createParent)
specifier|public
name|void
name|primitiveMkdir
parameter_list|(
name|Path
name|f
parameter_list|,
name|FsPermission
name|absolutePermission
parameter_list|,
name|boolean
name|createParent
parameter_list|)
function_decl|;
DECL|method|getDefaultPort ()
specifier|public
name|int
name|getDefaultPort
parameter_list|()
function_decl|;
DECL|method|getCanonicalServiceName ()
specifier|public
name|String
name|getCanonicalServiceName
parameter_list|()
function_decl|;
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
function_decl|;
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
function_decl|;
DECL|method|cancelDeleteOnExit (Path f)
specifier|public
name|boolean
name|cancelDeleteOnExit
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|addDelegationTokens (String renewer, Credentials creds)
specifier|public
name|Token
argument_list|<
name|?
argument_list|>
index|[]
name|addDelegationTokens
parameter_list|(
name|String
name|renewer
parameter_list|,
name|Credentials
name|creds
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|fixRelativePart (Path p)
specifier|public
name|Path
name|fixRelativePart
parameter_list|(
name|Path
name|p
parameter_list|)
function_decl|;
DECL|method|concat (Path trg, Path [] psrcs)
specifier|public
name|void
name|concat
parameter_list|(
name|Path
name|trg
parameter_list|,
name|Path
index|[]
name|psrcs
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|primitiveCreate (Path f, FsPermission absolutePermission, EnumSet<CreateFlag> flag, int bufferSize, short replication, long blockSize, Progressable progress, ChecksumOpt checksumOpt)
specifier|public
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
name|ChecksumOpt
name|checksumOpt
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|primitiveMkdir (Path f, FsPermission absolutePermission)
specifier|public
name|boolean
name|primitiveMkdir
parameter_list|(
name|Path
name|f
parameter_list|,
name|FsPermission
name|absolutePermission
parameter_list|)
throws|throws
name|IOException
function_decl|;
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
function_decl|;
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
function_decl|;
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
function_decl|;
DECL|method|getFileLinkStatus (Path f)
specifier|public
name|FileStatus
name|getFileLinkStatus
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|supportsSymlinks ()
specifier|public
name|boolean
name|supportsSymlinks
parameter_list|()
function_decl|;
DECL|method|getLinkTarget (Path f)
specifier|public
name|Path
name|getLinkTarget
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|resolveLink (Path f)
specifier|public
name|Path
name|resolveLink
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|setVerifyChecksum (boolean verifyChecksum)
specifier|public
name|void
name|setVerifyChecksum
parameter_list|(
name|boolean
name|verifyChecksum
parameter_list|)
function_decl|;
DECL|method|setWriteChecksum (boolean writeChecksum)
specifier|public
name|void
name|setWriteChecksum
parameter_list|(
name|boolean
name|writeChecksum
parameter_list|)
function_decl|;
DECL|method|createSnapshot (Path path, String snapshotName)
specifier|public
name|Path
name|createSnapshot
parameter_list|(
name|Path
name|path
parameter_list|,
name|String
name|snapshotName
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|renameSnapshot (Path path, String snapshotOldName, String snapshotNewName)
specifier|public
name|void
name|renameSnapshot
parameter_list|(
name|Path
name|path
parameter_list|,
name|String
name|snapshotOldName
parameter_list|,
name|String
name|snapshotNewName
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|deleteSnapshot (Path path, String snapshotName)
specifier|public
name|void
name|deleteSnapshot
parameter_list|(
name|Path
name|path
parameter_list|,
name|String
name|snapshotName
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|modifyAclEntries (Path path, Iterable<AclEntry> aclSpec)
specifier|public
name|void
name|modifyAclEntries
parameter_list|(
name|Path
name|path
parameter_list|,
name|Iterable
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|removeAclEntries (Path path, Iterable<AclEntry> aclSpec)
specifier|public
name|void
name|removeAclEntries
parameter_list|(
name|Path
name|path
parameter_list|,
name|Iterable
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|removeDefaultAcl (Path path)
specifier|public
name|void
name|removeDefaultAcl
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|removeAcl (Path path)
specifier|public
name|void
name|removeAcl
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|setAcl (Path path, Iterable<AclEntry> aclSpec)
specifier|public
name|void
name|setAcl
parameter_list|(
name|Path
name|path
parameter_list|,
name|Iterable
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getAclStatus (Path path)
specifier|public
name|AclStatus
name|getAclStatus
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
annotation|@
name|Test
DECL|method|testHarUri ()
specifier|public
name|void
name|testHarUri
parameter_list|()
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|checkInvalidPath
argument_list|(
literal|"har://hdfs-/foo.har"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|checkInvalidPath
argument_list|(
literal|"har://hdfs/foo.har"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|checkInvalidPath
argument_list|(
literal|"har://-hdfs/foo.har"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|checkInvalidPath
argument_list|(
literal|"har://-/foo.har"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|checkInvalidPath
argument_list|(
literal|"har://127.0.0.1-/foo.har"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|checkInvalidPath
argument_list|(
literal|"har://127.0.0.1/foo.har"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|checkInvalidPath (String s, Configuration conf)
specifier|static
name|void
name|checkInvalidPath
parameter_list|(
name|String
name|s
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\ncheckInvalidPath: "
operator|+
name|s
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|s
argument_list|)
decl_stmt|;
try|try
block|{
name|p
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
name|p
operator|+
literal|" is an invalid path."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Expected
block|}
block|}
annotation|@
name|Test
DECL|method|testFileChecksum ()
specifier|public
name|void
name|testFileChecksum
parameter_list|()
block|{
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"har://file-localhost/foo.har/file1"
argument_list|)
decl_stmt|;
specifier|final
name|HarFileSystem
name|harfs
init|=
operator|new
name|HarFileSystem
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|harfs
operator|.
name|getFileChecksum
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test how block location offsets and lengths are fixed.    */
annotation|@
name|Test
DECL|method|testFixBlockLocations ()
specifier|public
name|void
name|testFixBlockLocations
parameter_list|()
block|{
comment|// do some tests where start == 0
block|{
comment|// case 1: range starts before current har block and ends after
name|BlockLocation
index|[]
name|b
init|=
block|{
operator|new
name|BlockLocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
block|}
decl_stmt|;
name|HarFileSystem
operator|.
name|fixBlockLocations
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|0
index|]
operator|.
name|getOffset
argument_list|()
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|0
index|]
operator|.
name|getLength
argument_list|()
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
block|{
comment|// case 2: range starts in current har block and ends after
name|BlockLocation
index|[]
name|b
init|=
block|{
operator|new
name|BlockLocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
block|}
decl_stmt|;
name|HarFileSystem
operator|.
name|fixBlockLocations
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|0
index|]
operator|.
name|getOffset
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|0
index|]
operator|.
name|getLength
argument_list|()
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
block|{
comment|// case 3: range starts before current har block and ends in
comment|// current har block
name|BlockLocation
index|[]
name|b
init|=
block|{
operator|new
name|BlockLocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
block|}
decl_stmt|;
name|HarFileSystem
operator|.
name|fixBlockLocations
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|0
index|]
operator|.
name|getOffset
argument_list|()
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|0
index|]
operator|.
name|getLength
argument_list|()
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
block|{
comment|// case 4: range starts and ends in current har block
name|BlockLocation
index|[]
name|b
init|=
block|{
operator|new
name|BlockLocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
block|}
decl_stmt|;
name|HarFileSystem
operator|.
name|fixBlockLocations
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|0
index|]
operator|.
name|getOffset
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|0
index|]
operator|.
name|getLength
argument_list|()
argument_list|,
literal|6
argument_list|)
expr_stmt|;
block|}
comment|// now try a range where start == 3
block|{
comment|// case 5: range starts before current har block and ends after
name|BlockLocation
index|[]
name|b
init|=
block|{
operator|new
name|BlockLocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
block|}
decl_stmt|;
name|HarFileSystem
operator|.
name|fixBlockLocations
argument_list|(
name|b
argument_list|,
literal|3
argument_list|,
literal|20
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|0
index|]
operator|.
name|getOffset
argument_list|()
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|0
index|]
operator|.
name|getLength
argument_list|()
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
block|{
comment|// case 6: range starts in current har block and ends after
name|BlockLocation
index|[]
name|b
init|=
block|{
operator|new
name|BlockLocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
block|}
decl_stmt|;
name|HarFileSystem
operator|.
name|fixBlockLocations
argument_list|(
name|b
argument_list|,
literal|3
argument_list|,
literal|20
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|0
index|]
operator|.
name|getOffset
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|0
index|]
operator|.
name|getLength
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
block|{
comment|// case 7: range starts before current har block and ends in
comment|// current har block
name|BlockLocation
index|[]
name|b
init|=
block|{
operator|new
name|BlockLocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
block|}
decl_stmt|;
name|HarFileSystem
operator|.
name|fixBlockLocations
argument_list|(
name|b
argument_list|,
literal|3
argument_list|,
literal|7
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|0
index|]
operator|.
name|getOffset
argument_list|()
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|0
index|]
operator|.
name|getLength
argument_list|()
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
block|{
comment|// case 8: range starts and ends in current har block
name|BlockLocation
index|[]
name|b
init|=
block|{
operator|new
name|BlockLocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
block|}
decl_stmt|;
name|HarFileSystem
operator|.
name|fixBlockLocations
argument_list|(
name|b
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|0
index|]
operator|.
name|getOffset
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|0
index|]
operator|.
name|getLength
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
comment|// test case from JIRA MAPREDUCE-1752
block|{
name|BlockLocation
index|[]
name|b
init|=
block|{
operator|new
name|BlockLocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|512
argument_list|,
literal|512
argument_list|)
block|,
operator|new
name|BlockLocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|1024
argument_list|,
literal|512
argument_list|)
block|}
decl_stmt|;
name|HarFileSystem
operator|.
name|fixBlockLocations
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
literal|512
argument_list|,
literal|896
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|0
index|]
operator|.
name|getOffset
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|0
index|]
operator|.
name|getLength
argument_list|()
argument_list|,
literal|128
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|1
index|]
operator|.
name|getOffset
argument_list|()
argument_list|,
literal|128
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
index|[
literal|1
index|]
operator|.
name|getLength
argument_list|()
argument_list|,
literal|384
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInheritedMethodsImplemented ()
specifier|public
name|void
name|testInheritedMethodsImplemented
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|errors
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Method
name|m
range|:
name|FileSystem
operator|.
name|class
operator|.
name|getDeclaredMethods
argument_list|()
control|)
block|{
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|m
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|||
name|Modifier
operator|.
name|isPrivate
argument_list|(
name|m
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|||
name|Modifier
operator|.
name|isFinal
argument_list|(
name|m
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
try|try
block|{
name|MustNotImplement
operator|.
name|class
operator|.
name|getMethod
argument_list|(
name|m
operator|.
name|getName
argument_list|()
argument_list|,
name|m
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|HarFileSystem
operator|.
name|class
operator|.
name|getDeclaredMethod
argument_list|(
name|m
operator|.
name|getName
argument_list|()
argument_list|,
name|m
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"HarFileSystem MUST not implement "
operator|+
name|m
argument_list|)
expr_stmt|;
name|errors
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|exc
parameter_list|)
block|{
try|try
block|{
name|HarFileSystem
operator|.
name|class
operator|.
name|getDeclaredMethod
argument_list|(
name|m
operator|.
name|getName
argument_list|()
argument_list|,
name|m
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|exc2
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"HarFileSystem MUST implement "
operator|+
name|m
argument_list|)
expr_stmt|;
name|errors
operator|++
expr_stmt|;
block|}
block|}
block|}
name|assertTrue
argument_list|(
operator|(
name|errors
operator|+
literal|" methods were not overridden correctly - see log"
operator|)
argument_list|,
name|errors
operator|<=
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

