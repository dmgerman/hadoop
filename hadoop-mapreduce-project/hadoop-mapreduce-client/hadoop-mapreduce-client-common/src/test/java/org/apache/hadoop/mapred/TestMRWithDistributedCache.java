begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

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
name|FileOutputStream
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
name|jar
operator|.
name|JarOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|filecache
operator|.
name|DistributedCache
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
name|LocalFileSystem
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
name|io
operator|.
name|LongWritable
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
name|NullWritable
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
name|mapreduce
operator|.
name|Job
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
name|Mapper
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
name|Reducer
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
name|TaskInputOutputContext
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
name|lib
operator|.
name|input
operator|.
name|FileInputFormat
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
name|lib
operator|.
name|output
operator|.
name|NullOutputFormat
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
name|server
operator|.
name|jobtracker
operator|.
name|JTConfig
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

begin_comment
comment|/**  * Tests the use of the  * {@link org.apache.hadoop.mapreduce.filecache.DistributedCache} within the  * full MR flow as well as the LocalJobRunner. This ought to be part of the  * filecache package, but that package is not currently in mapred, so cannot  * depend on MR for testing.  *   * We use the distributed.* namespace for temporary files.  *   * See {@link TestMiniMRLocalFS}, {@link TestMiniMRDFSCaching}, and  * {@link MRCaching} for other tests that test the distributed cache.  *   * This test is not fast: it uses MiniMRCluster.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|class|TestMRWithDistributedCache
specifier|public
class|class
name|TestMRWithDistributedCache
extends|extends
name|TestCase
block|{
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
name|Path
name|TEST_ROOT_DIR
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|symlinkFile
specifier|private
specifier|static
name|File
name|symlinkFile
init|=
operator|new
name|File
argument_list|(
literal|"distributed.first.symlink"
argument_list|)
decl_stmt|;
DECL|field|expectedAbsentSymlinkFile
specifier|private
specifier|static
name|File
name|expectedAbsentSymlinkFile
init|=
operator|new
name|File
argument_list|(
literal|"distributed.second.jar"
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|localFs
specifier|private
specifier|static
name|FileSystem
name|localFs
decl_stmt|;
static|static
block|{
try|try
block|{
name|localFs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|io
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"problem getting local fs"
argument_list|,
name|io
argument_list|)
throw|;
block|}
block|}
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
name|TestMRWithDistributedCache
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|DistributedCacheChecker
specifier|private
specifier|static
class|class
name|DistributedCacheChecker
block|{
DECL|method|setup (TaskInputOutputContext<?, ?, ?, ?> context)
specifier|public
name|void
name|setup
parameter_list|(
name|TaskInputOutputContext
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|Path
index|[]
name|localFiles
init|=
name|context
operator|.
name|getLocalCacheFiles
argument_list|()
decl_stmt|;
name|URI
index|[]
name|files
init|=
name|context
operator|.
name|getCacheFiles
argument_list|()
decl_stmt|;
name|Path
index|[]
name|localArchives
init|=
name|context
operator|.
name|getLocalCacheArchives
argument_list|()
decl_stmt|;
name|URI
index|[]
name|archives
init|=
name|context
operator|.
name|getCacheArchives
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|LocalFileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Check that 2 files and 2 archives are present
name|TestCase
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|localFiles
operator|.
name|length
argument_list|)
expr_stmt|;
name|TestCase
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|localArchives
operator|.
name|length
argument_list|)
expr_stmt|;
name|TestCase
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|files
operator|.
name|length
argument_list|)
expr_stmt|;
name|TestCase
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|archives
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Check the file name
name|TestCase
operator|.
name|assertTrue
argument_list|(
name|files
index|[
literal|0
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"distributed.first"
argument_list|)
argument_list|)
expr_stmt|;
name|TestCase
operator|.
name|assertTrue
argument_list|(
name|files
index|[
literal|1
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"distributed.second.jar"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check lengths of the files
name|TestCase
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fs
operator|.
name|getFileStatus
argument_list|(
name|localFiles
index|[
literal|0
index|]
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|TestCase
operator|.
name|assertTrue
argument_list|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|localFiles
index|[
literal|1
index|]
argument_list|)
operator|.
name|getLen
argument_list|()
operator|>
literal|1
argument_list|)
expr_stmt|;
comment|// Check extraction of the archive
name|TestCase
operator|.
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|localArchives
index|[
literal|0
index|]
argument_list|,
literal|"distributed.jar.inside3"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|TestCase
operator|.
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|localArchives
index|[
literal|1
index|]
argument_list|,
literal|"distributed.jar.inside4"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check the class loaders
name|LOG
operator|.
name|info
argument_list|(
literal|"Java Classpath: "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.class.path"
argument_list|)
argument_list|)
expr_stmt|;
name|ClassLoader
name|cl
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
comment|// Both the file and the archive were added to classpath, so both
comment|// should be reachable via the class loader.
name|TestCase
operator|.
name|assertNotNull
argument_list|(
name|cl
operator|.
name|getResource
argument_list|(
literal|"distributed.jar.inside2"
argument_list|)
argument_list|)
expr_stmt|;
name|TestCase
operator|.
name|assertNotNull
argument_list|(
name|cl
operator|.
name|getResource
argument_list|(
literal|"distributed.jar.inside3"
argument_list|)
argument_list|)
expr_stmt|;
name|TestCase
operator|.
name|assertNull
argument_list|(
name|cl
operator|.
name|getResource
argument_list|(
literal|"distributed.jar.inside4"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check that the symlink for the renaming was created in the cwd;
name|TestCase
operator|.
name|assertTrue
argument_list|(
literal|"symlink distributed.first.symlink doesn't exist"
argument_list|,
name|symlinkFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|TestCase
operator|.
name|assertEquals
argument_list|(
literal|"symlink distributed.first.symlink length not 1"
argument_list|,
literal|1
argument_list|,
name|symlinkFile
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|//This last one is a difference between MRv2 and MRv1
name|TestCase
operator|.
name|assertTrue
argument_list|(
literal|"second file should be symlinked too"
argument_list|,
name|expectedAbsentSymlinkFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|DistributedCacheCheckerMapper
specifier|public
specifier|static
class|class
name|DistributedCacheCheckerMapper
extends|extends
name|Mapper
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|NullWritable
argument_list|,
name|NullWritable
argument_list|>
block|{
annotation|@
name|Override
DECL|method|setup (Context context)
specifier|protected
name|void
name|setup
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
operator|new
name|DistributedCacheChecker
argument_list|()
operator|.
name|setup
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|DistributedCacheCheckerReducer
specifier|public
specifier|static
class|class
name|DistributedCacheCheckerReducer
extends|extends
name|Reducer
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|NullWritable
argument_list|,
name|NullWritable
argument_list|>
block|{
annotation|@
name|Override
DECL|method|setup (Context context)
specifier|public
name|void
name|setup
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
block|{
operator|new
name|DistributedCacheChecker
argument_list|()
operator|.
name|setup
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testWithConf (Configuration conf)
specifier|private
name|void
name|testWithConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|ClassNotFoundException
throws|,
name|URISyntaxException
block|{
comment|// Create a temporary file of length 1.
name|Path
name|first
init|=
name|createTempFile
argument_list|(
literal|"distributed.first"
argument_list|,
literal|"x"
argument_list|)
decl_stmt|;
comment|// Create two jars with a single file inside them.
name|Path
name|second
init|=
name|makeJar
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"distributed.second.jar"
argument_list|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|Path
name|third
init|=
name|makeJar
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"distributed.third.jar"
argument_list|)
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|Path
name|fourth
init|=
name|makeJar
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"distributed.fourth.jar"
argument_list|)
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|DistributedCacheCheckerMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|DistributedCacheCheckerReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputFormatClass
argument_list|(
name|NullOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|first
argument_list|)
expr_stmt|;
comment|// Creates the Job Configuration
name|job
operator|.
name|addCacheFile
argument_list|(
operator|new
name|URI
argument_list|(
name|first
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"#distributed.first.symlink"
argument_list|)
argument_list|)
expr_stmt|;
name|job
operator|.
name|addFileToClassPath
argument_list|(
name|second
argument_list|)
expr_stmt|;
name|job
operator|.
name|addArchiveToClassPath
argument_list|(
name|third
argument_list|)
expr_stmt|;
name|job
operator|.
name|addCacheArchive
argument_list|(
name|fourth
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMaxMapAttempts
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// speed up failures
name|job
operator|.
name|submit
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests using the local job runner. */
DECL|method|testLocalJobRunner ()
specifier|public
name|void
name|testLocalJobRunner
parameter_list|()
throws|throws
name|Exception
block|{
name|symlinkFile
operator|.
name|delete
argument_list|()
expr_stmt|;
comment|// ensure symlink is not present (e.g. if test is
comment|// killed part way through)
name|Configuration
name|c
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|c
operator|.
name|set
argument_list|(
name|JTConfig
operator|.
name|JT_IPC_ADDRESS
argument_list|,
literal|"local"
argument_list|)
expr_stmt|;
name|c
operator|.
name|set
argument_list|(
literal|"fs.defaultFS"
argument_list|,
literal|"file:///"
argument_list|)
expr_stmt|;
name|testWithConf
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Symlink not removed by local job runner"
argument_list|,
comment|// Symlink target will have gone so can't use File.exists()
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
operator|.
name|list
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|symlinkFile
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createTempFile (String filename, String contents)
specifier|private
name|Path
name|createTempFile
parameter_list|(
name|String
name|filename
parameter_list|,
name|String
name|contents
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
name|filename
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|os
init|=
name|localFs
operator|.
name|create
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|os
operator|.
name|writeBytes
argument_list|(
name|contents
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|path
return|;
block|}
DECL|method|makeJar (Path p, int index)
specifier|private
name|Path
name|makeJar
parameter_list|(
name|Path
name|p
parameter_list|,
name|int
name|index
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|IOException
block|{
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|p
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|JarOutputStream
name|jos
init|=
operator|new
name|JarOutputStream
argument_list|(
name|fos
argument_list|)
decl_stmt|;
name|ZipEntry
name|ze
init|=
operator|new
name|ZipEntry
argument_list|(
literal|"distributed.jar.inside"
operator|+
name|index
argument_list|)
decl_stmt|;
name|jos
operator|.
name|putNextEntry
argument_list|(
name|ze
argument_list|)
expr_stmt|;
name|jos
operator|.
name|write
argument_list|(
operator|(
literal|"inside the jar!"
operator|+
name|index
operator|)
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|jos
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
name|jos
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|p
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testDeprecatedFunctions ()
specifier|public
name|void
name|testDeprecatedFunctions
parameter_list|()
throws|throws
name|Exception
block|{
name|DistributedCache
operator|.
name|addLocalArchives
argument_list|(
name|conf
argument_list|,
literal|"Test Local Archives 1"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Test Local Archives 1"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|DistributedCache
operator|.
name|CACHE_LOCALARCHIVES
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DistributedCache
operator|.
name|getLocalCacheArchives
argument_list|(
name|conf
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Test Local Archives 1"
argument_list|,
name|DistributedCache
operator|.
name|getLocalCacheArchives
argument_list|(
name|conf
argument_list|)
index|[
literal|0
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|addLocalArchives
argument_list|(
name|conf
argument_list|,
literal|"Test Local Archives 2"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Test Local Archives 1,Test Local Archives 2"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|DistributedCache
operator|.
name|CACHE_LOCALARCHIVES
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|DistributedCache
operator|.
name|getLocalCacheArchives
argument_list|(
name|conf
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Test Local Archives 2"
argument_list|,
name|DistributedCache
operator|.
name|getLocalCacheArchives
argument_list|(
name|conf
argument_list|)
index|[
literal|1
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|setLocalArchives
argument_list|(
name|conf
argument_list|,
literal|"Test Local Archives 3"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Test Local Archives 3"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|DistributedCache
operator|.
name|CACHE_LOCALARCHIVES
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DistributedCache
operator|.
name|getLocalCacheArchives
argument_list|(
name|conf
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Test Local Archives 3"
argument_list|,
name|DistributedCache
operator|.
name|getLocalCacheArchives
argument_list|(
name|conf
argument_list|)
index|[
literal|0
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|addLocalFiles
argument_list|(
name|conf
argument_list|,
literal|"Test Local Files 1"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Test Local Files 1"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|DistributedCache
operator|.
name|CACHE_LOCALFILES
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DistributedCache
operator|.
name|getLocalCacheFiles
argument_list|(
name|conf
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Test Local Files 1"
argument_list|,
name|DistributedCache
operator|.
name|getLocalCacheFiles
argument_list|(
name|conf
argument_list|)
index|[
literal|0
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|addLocalFiles
argument_list|(
name|conf
argument_list|,
literal|"Test Local Files 2"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Test Local Files 1,Test Local Files 2"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|DistributedCache
operator|.
name|CACHE_LOCALFILES
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|DistributedCache
operator|.
name|getLocalCacheFiles
argument_list|(
name|conf
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Test Local Files 2"
argument_list|,
name|DistributedCache
operator|.
name|getLocalCacheFiles
argument_list|(
name|conf
argument_list|)
index|[
literal|1
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|setLocalFiles
argument_list|(
name|conf
argument_list|,
literal|"Test Local Files 3"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Test Local Files 3"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|DistributedCache
operator|.
name|CACHE_LOCALFILES
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DistributedCache
operator|.
name|getLocalCacheFiles
argument_list|(
name|conf
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Test Local Files 3"
argument_list|,
name|DistributedCache
operator|.
name|getLocalCacheFiles
argument_list|(
name|conf
argument_list|)
index|[
literal|0
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|setArchiveTimestamps
argument_list|(
name|conf
argument_list|,
literal|"1234567890"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1234567890
argument_list|,
name|conf
operator|.
name|getLong
argument_list|(
name|DistributedCache
operator|.
name|CACHE_ARCHIVES_TIMESTAMPS
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DistributedCache
operator|.
name|getArchiveTimestamps
argument_list|(
name|conf
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1234567890
argument_list|,
name|DistributedCache
operator|.
name|getArchiveTimestamps
argument_list|(
name|conf
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|setFileTimestamps
argument_list|(
name|conf
argument_list|,
literal|"1234567890"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1234567890
argument_list|,
name|conf
operator|.
name|getLong
argument_list|(
name|DistributedCache
operator|.
name|CACHE_FILES_TIMESTAMPS
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DistributedCache
operator|.
name|getFileTimestamps
argument_list|(
name|conf
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1234567890
argument_list|,
name|DistributedCache
operator|.
name|getFileTimestamps
argument_list|(
name|conf
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|createAllSymlink
argument_list|(
name|conf
argument_list|,
operator|new
name|File
argument_list|(
literal|"Test Job Cache Dir"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
literal|"Test Work Dir"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|DistributedCache
operator|.
name|CACHE_SYMLINK
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|DistributedCache
operator|.
name|getSymlink
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|symlinkFile
operator|.
name|createNewFile
argument_list|()
argument_list|)
expr_stmt|;
name|FileStatus
name|fileStatus
init|=
name|DistributedCache
operator|.
name|getFileStatus
argument_list|(
name|conf
argument_list|,
name|symlinkFile
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|fileStatus
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fileStatus
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|DistributedCache
operator|.
name|getTimestamp
argument_list|(
name|conf
argument_list|,
name|symlinkFile
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|symlinkFile
operator|.
name|delete
argument_list|()
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|addCacheArchive
argument_list|(
name|symlinkFile
operator|.
name|toURI
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|symlinkFile
operator|.
name|toURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|DistributedCache
operator|.
name|CACHE_ARCHIVES
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DistributedCache
operator|.
name|getCacheArchives
argument_list|(
name|conf
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|symlinkFile
operator|.
name|toURI
argument_list|()
argument_list|,
name|DistributedCache
operator|.
name|getCacheArchives
argument_list|(
name|conf
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|addCacheFile
argument_list|(
name|symlinkFile
operator|.
name|toURI
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|symlinkFile
operator|.
name|toURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|DistributedCache
operator|.
name|CACHE_FILES
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DistributedCache
operator|.
name|getCacheFiles
argument_list|(
name|conf
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|symlinkFile
operator|.
name|toURI
argument_list|()
argument_list|,
name|DistributedCache
operator|.
name|getCacheFiles
argument_list|(
name|conf
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

