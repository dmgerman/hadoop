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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
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
name|net
operator|.
name|URI
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
name|Options
operator|.
name|CreateOpts
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
name|Rename
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
name|security
operator|.
name|token
operator|.
name|DelegationTokenIssuer
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
name|BeforeClass
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

begin_class
DECL|class|TestFilterFileSystem
specifier|public
class|class
name|TestFilterFileSystem
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|FileSystem
operator|.
name|LOG
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
block|{
name|conf
operator|.
name|set
argument_list|(
literal|"fs.flfs.impl"
argument_list|,
name|FilterLocalFileSystem
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"fs.flfs.impl.disable.cache"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"fs.file.impl.disable.cache"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * FileSystem methods that must not be overwritten by    * {@link FilterFileSystem}. Either because there is a default implementation    * already available or because it is not relevant.    */
DECL|interface|MustNotImplement
specifier|public
specifier|static
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
DECL|method|append (Path f)
specifier|public
name|FSDataOutputStream
name|append
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
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
throws|throws
name|IOException
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
DECL|method|createNonRecursive (Path f, boolean overwrite, int bufferSize, short replication, long blockSize, Progressable progress)
specifier|public
name|FSDataOutputStream
name|createNonRecursive
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
throws|throws
name|IOException
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
DECL|method|open (PathHandle f)
specifier|public
name|FSDataInputStream
name|open
parameter_list|(
name|PathHandle
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
DECL|method|listStatusBatch (Path f, byte[] token)
specifier|public
name|FileStatus
index|[]
name|listStatusBatch
parameter_list|(
name|Path
name|f
parameter_list|,
name|byte
index|[]
name|token
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
DECL|method|getAdditionalTokenIssuers ()
specifier|public
name|DelegationTokenIssuer
index|[]
name|getAdditionalTokenIssuers
parameter_list|()
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
DECL|method|getScheme ()
specifier|public
name|String
name|getScheme
parameter_list|()
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
DECL|method|getContentSummary (Path f)
specifier|public
name|ContentSummary
name|getContentSummary
parameter_list|(
name|Path
name|f
parameter_list|)
function_decl|;
DECL|method|getQuotaUsage (Path f)
specifier|public
name|QuotaUsage
name|getQuotaUsage
parameter_list|(
name|Path
name|f
parameter_list|)
function_decl|;
DECL|method|setQuota (Path f, long namespaceQuota, long storagespaceQuota)
name|void
name|setQuota
parameter_list|(
name|Path
name|f
parameter_list|,
name|long
name|namespaceQuota
parameter_list|,
name|long
name|storagespaceQuota
parameter_list|)
function_decl|;
DECL|method|setQuotaByStorageType (Path f, StorageType type, long quota)
name|void
name|setQuotaByStorageType
parameter_list|(
name|Path
name|f
parameter_list|,
name|StorageType
name|type
parameter_list|,
name|long
name|quota
parameter_list|)
function_decl|;
DECL|method|getStorageStatistics ()
name|StorageStatistics
name|getStorageStatistics
parameter_list|()
function_decl|;
block|}
annotation|@
name|Test
DECL|method|testFilterFileSystem ()
specifier|public
name|void
name|testFilterFileSystem
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
name|FilterFileSystem
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
literal|"FilterFileSystem MUST NOT implement "
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
name|FilterFileSystem
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
literal|"FilterFileSystem MUST implement "
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
literal|" methods were not overridden correctly - see"
operator|+
literal|" log"
operator|)
argument_list|,
name|errors
operator|<=
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFilterEmbedInit ()
specifier|public
name|void
name|testFilterEmbedInit
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|mockFs
init|=
name|createMockFs
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// no conf = need init
name|checkInit
argument_list|(
operator|new
name|FilterFileSystem
argument_list|(
name|mockFs
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFilterEmbedNoInit ()
specifier|public
name|void
name|testFilterEmbedNoInit
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|mockFs
init|=
name|createMockFs
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|// has conf = skip init
name|checkInit
argument_list|(
operator|new
name|FilterFileSystem
argument_list|(
name|mockFs
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLocalEmbedInit ()
specifier|public
name|void
name|testLocalEmbedInit
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|mockFs
init|=
name|createMockFs
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// no conf = need init
name|checkInit
argument_list|(
operator|new
name|LocalFileSystem
argument_list|(
name|mockFs
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLocalEmbedNoInit ()
specifier|public
name|void
name|testLocalEmbedNoInit
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|mockFs
init|=
name|createMockFs
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|// has conf = skip init
name|checkInit
argument_list|(
operator|new
name|LocalFileSystem
argument_list|(
name|mockFs
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|createMockFs (boolean useConf)
specifier|private
name|FileSystem
name|createMockFs
parameter_list|(
name|boolean
name|useConf
parameter_list|)
block|{
name|FileSystem
name|mockFs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockFs
operator|.
name|getUri
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"mock:/"
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockFs
operator|.
name|getConf
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|useConf
condition|?
name|conf
else|:
literal|null
argument_list|)
expr_stmt|;
return|return
name|mockFs
return|;
block|}
annotation|@
name|Test
DECL|method|testGetLocalFsSetsConfs ()
specifier|public
name|void
name|testGetLocalFsSetsConfs
parameter_list|()
throws|throws
name|Exception
block|{
name|LocalFileSystem
name|lfs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|checkFsConf
argument_list|(
name|lfs
argument_list|,
name|conf
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetFilterLocalFsSetsConfs ()
specifier|public
name|void
name|testGetFilterLocalFsSetsConfs
parameter_list|()
throws|throws
name|Exception
block|{
name|FilterFileSystem
name|flfs
init|=
operator|(
name|FilterFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"flfs:/"
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|checkFsConf
argument_list|(
name|flfs
argument_list|,
name|conf
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInitLocalFsSetsConfs ()
specifier|public
name|void
name|testInitLocalFsSetsConfs
parameter_list|()
throws|throws
name|Exception
block|{
name|LocalFileSystem
name|lfs
init|=
operator|new
name|LocalFileSystem
argument_list|()
decl_stmt|;
name|checkFsConf
argument_list|(
name|lfs
argument_list|,
literal|null
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|lfs
operator|.
name|initialize
argument_list|(
name|lfs
operator|.
name|getUri
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|checkFsConf
argument_list|(
name|lfs
argument_list|,
name|conf
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInitFilterFsSetsEmbedConf ()
specifier|public
name|void
name|testInitFilterFsSetsEmbedConf
parameter_list|()
throws|throws
name|Exception
block|{
name|LocalFileSystem
name|lfs
init|=
operator|new
name|LocalFileSystem
argument_list|()
decl_stmt|;
name|checkFsConf
argument_list|(
name|lfs
argument_list|,
literal|null
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|FilterFileSystem
name|ffs
init|=
operator|new
name|FilterFileSystem
argument_list|(
name|lfs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|lfs
argument_list|,
name|ffs
operator|.
name|getRawFileSystem
argument_list|()
argument_list|)
expr_stmt|;
name|checkFsConf
argument_list|(
name|ffs
argument_list|,
literal|null
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|ffs
operator|.
name|initialize
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"filter:/"
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|checkFsConf
argument_list|(
name|ffs
argument_list|,
name|conf
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInitFilterLocalFsSetsEmbedConf ()
specifier|public
name|void
name|testInitFilterLocalFsSetsEmbedConf
parameter_list|()
throws|throws
name|Exception
block|{
name|FilterFileSystem
name|flfs
init|=
operator|new
name|FilterLocalFileSystem
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|LocalFileSystem
operator|.
name|class
argument_list|,
name|flfs
operator|.
name|getRawFileSystem
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|checkFsConf
argument_list|(
name|flfs
argument_list|,
literal|null
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|flfs
operator|.
name|initialize
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"flfs:/"
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|checkFsConf
argument_list|(
name|flfs
argument_list|,
name|conf
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVerifyChecksumPassthru ()
specifier|public
name|void
name|testVerifyChecksumPassthru
parameter_list|()
block|{
name|FileSystem
name|mockFs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
operator|new
name|FilterFileSystem
argument_list|(
name|mockFs
argument_list|)
decl_stmt|;
name|fs
operator|.
name|setVerifyChecksum
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs
argument_list|)
operator|.
name|setVerifyChecksum
argument_list|(
name|eq
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|reset
argument_list|(
name|mockFs
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setVerifyChecksum
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs
argument_list|)
operator|.
name|setVerifyChecksum
argument_list|(
name|eq
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWriteChecksumPassthru ()
specifier|public
name|void
name|testWriteChecksumPassthru
parameter_list|()
block|{
name|FileSystem
name|mockFs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
operator|new
name|FilterFileSystem
argument_list|(
name|mockFs
argument_list|)
decl_stmt|;
name|fs
operator|.
name|setWriteChecksum
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs
argument_list|)
operator|.
name|setWriteChecksum
argument_list|(
name|eq
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|reset
argument_list|(
name|mockFs
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setWriteChecksum
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs
argument_list|)
operator|.
name|setWriteChecksum
argument_list|(
name|eq
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenameOptions ()
specifier|public
name|void
name|testRenameOptions
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|mockFs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
operator|new
name|FilterFileSystem
argument_list|(
name|mockFs
argument_list|)
decl_stmt|;
name|Path
name|src
init|=
operator|new
name|Path
argument_list|(
literal|"/src"
argument_list|)
decl_stmt|;
name|Path
name|dst
init|=
operator|new
name|Path
argument_list|(
literal|"/dest"
argument_list|)
decl_stmt|;
name|Rename
name|opt
init|=
name|Rename
operator|.
name|TO_TRASH
decl_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|src
argument_list|,
name|dst
argument_list|,
name|opt
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockFs
argument_list|)
operator|.
name|rename
argument_list|(
name|eq
argument_list|(
name|src
argument_list|)
argument_list|,
name|eq
argument_list|(
name|dst
argument_list|)
argument_list|,
name|eq
argument_list|(
name|opt
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|checkInit (FilterFileSystem fs, boolean expectInit)
specifier|private
name|void
name|checkInit
parameter_list|(
name|FilterFileSystem
name|fs
parameter_list|,
name|boolean
name|expectInit
parameter_list|)
throws|throws
name|Exception
block|{
name|URI
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
literal|"filter:/"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|FileSystem
name|embedFs
init|=
name|fs
operator|.
name|getRawFileSystem
argument_list|()
decl_stmt|;
if|if
condition|(
name|expectInit
condition|)
block|{
name|verify
argument_list|(
name|embedFs
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|initialize
argument_list|(
name|eq
argument_list|(
name|uri
argument_list|)
argument_list|,
name|eq
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|verify
argument_list|(
name|embedFs
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|initialize
argument_list|(
name|any
argument_list|(
name|URI
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// check the given fs's conf, and all its filtered filesystems
DECL|method|checkFsConf (FileSystem fs, Configuration conf, int expectDepth)
specifier|private
name|void
name|checkFsConf
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|int
name|expectDepth
parameter_list|)
block|{
name|int
name|depth
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|depth
operator|++
expr_stmt|;
name|assertFalse
argument_list|(
literal|"depth "
operator|+
name|depth
operator|+
literal|">"
operator|+
name|expectDepth
argument_list|,
name|depth
operator|>
name|expectDepth
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|conf
argument_list|,
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|fs
operator|instanceof
name|FilterFileSystem
operator|)
condition|)
block|{
break|break;
block|}
name|fs
operator|=
operator|(
operator|(
name|FilterFileSystem
operator|)
name|fs
operator|)
operator|.
name|getRawFileSystem
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectDepth
argument_list|,
name|depth
argument_list|)
expr_stmt|;
block|}
DECL|class|FilterLocalFileSystem
specifier|private
specifier|static
class|class
name|FilterLocalFileSystem
extends|extends
name|FilterFileSystem
block|{
DECL|method|FilterLocalFileSystem ()
name|FilterLocalFileSystem
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|LocalFileSystem
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

