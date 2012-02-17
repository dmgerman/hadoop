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
name|Matchers
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
DECL|class|DontCheck
specifier|public
specifier|static
class|class
name|DontCheck
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
block|{
return|return
literal|null
return|;
block|}
DECL|method|getServerDefaults ()
specifier|public
name|FsServerDefaults
name|getServerDefaults
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|getLength (Path f)
specifier|public
name|long
name|getLength
parameter_list|(
name|Path
name|f
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
DECL|method|append (Path f)
specifier|public
name|FSDataOutputStream
name|append
parameter_list|(
name|Path
name|f
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
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
block|{
return|return
literal|null
return|;
block|}
DECL|method|rename (final Path src, final Path dst, final Rename... options)
specifier|public
name|void
name|rename
parameter_list|(
specifier|final
name|Path
name|src
parameter_list|,
specifier|final
name|Path
name|dst
parameter_list|,
specifier|final
name|Rename
modifier|...
name|options
parameter_list|)
block|{ }
DECL|method|exists (Path f)
specifier|public
name|boolean
name|exists
parameter_list|(
name|Path
name|f
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
DECL|method|isDirectory (Path f)
specifier|public
name|boolean
name|isDirectory
parameter_list|(
name|Path
name|f
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
DECL|method|isFile (Path f)
specifier|public
name|boolean
name|isFile
parameter_list|(
name|Path
name|f
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
DECL|method|createNewFile (Path f)
specifier|public
name|boolean
name|createNewFile
parameter_list|(
name|Path
name|f
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
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
block|{
return|return
literal|null
return|;
block|}
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
block|{
return|return
literal|null
return|;
block|}
DECL|method|mkdirs (Path f)
specifier|public
name|boolean
name|mkdirs
parameter_list|(
name|Path
name|f
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
DECL|method|open (Path f)
specifier|public
name|FSDataInputStream
name|open
parameter_list|(
name|Path
name|f
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|create (Path f)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
name|Path
name|f
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
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
block|{
return|return
literal|null
return|;
block|}
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
block|{
return|return
literal|null
return|;
block|}
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
block|{
return|return
literal|null
return|;
block|}
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
block|{
return|return
literal|null
return|;
block|}
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
block|{
return|return
literal|null
return|;
block|}
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
block|{
return|return
literal|null
return|;
block|}
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
block|{
return|return
literal|null
return|;
block|}
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
block|{
return|return
literal|null
return|;
block|}
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
block|{
return|return
literal|null
return|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|delete (Path f)
specifier|public
name|boolean
name|delete
parameter_list|(
name|Path
name|f
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
DECL|method|getReplication (Path src)
specifier|public
name|short
name|getReplication
parameter_list|(
name|Path
name|src
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
DECL|method|processDeleteOnExit ()
specifier|public
name|void
name|processDeleteOnExit
parameter_list|()
block|{ }
DECL|method|getContentSummary (Path f)
specifier|public
name|ContentSummary
name|getContentSummary
parameter_list|(
name|Path
name|f
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|getStatus ()
specifier|public
name|FsStatus
name|getStatus
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
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
block|{
return|return
literal|null
return|;
block|}
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
block|{
return|return
literal|null
return|;
block|}
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
block|{
return|return
literal|null
return|;
block|}
DECL|method|globStatus (Path pathPattern)
specifier|public
name|FileStatus
index|[]
name|globStatus
parameter_list|(
name|Path
name|pathPattern
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
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
block|{
return|return
literal|null
return|;
block|}
DECL|method|listFiles ( final Path path, final boolean isRecursive)
specifier|public
name|Iterator
argument_list|<
name|LocatedFileStatus
argument_list|>
name|listFiles
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|,
specifier|final
name|boolean
name|isRecursive
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
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
block|{
return|return
literal|null
return|;
block|}
DECL|method|listLocatedStatus (Path f, final PathFilter filter)
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
specifier|final
name|PathFilter
name|filter
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
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
block|{ }
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
block|{ }
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
block|{ }
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
block|{ }
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
block|{ }
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
block|{ }
DECL|method|getBlockSize (Path f)
specifier|public
name|long
name|getBlockSize
parameter_list|(
name|Path
name|f
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
DECL|method|primitiveCreate (final Path f, final EnumSet<CreateFlag> createFlag, CreateOpts... opts)
specifier|public
name|FSDataOutputStream
name|primitiveCreate
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|,
specifier|final
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
block|{
return|return
literal|null
return|;
block|}
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
block|{ }
DECL|method|getDefaultPort ()
specifier|public
name|int
name|getDefaultPort
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|getCanonicalServiceName ()
specifier|public
name|String
name|getCanonicalServiceName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
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
literal|null
return|;
block|}
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
condition|)
continue|continue;
if|if
condition|(
name|Modifier
operator|.
name|isPrivate
argument_list|(
name|m
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
continue|continue;
try|try
block|{
name|DontCheck
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Skipping "
operator|+
name|m
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|exc
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Testing "
operator|+
name|m
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
literal|"FilterFileSystem doesn't implement "
operator|+
name|m
argument_list|)
expr_stmt|;
throw|throw
name|exc2
throw|;
block|}
block|}
block|}
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

