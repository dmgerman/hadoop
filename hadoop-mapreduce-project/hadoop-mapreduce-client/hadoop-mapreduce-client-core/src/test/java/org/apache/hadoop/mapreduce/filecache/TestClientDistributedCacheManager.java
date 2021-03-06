begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.filecache
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|filecache
package|;
end_package

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
name|util
operator|.
name|HashMap
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
name|Map
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
name|io
operator|.
name|SequenceFile
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
name|io
operator|.
name|SequenceFile
operator|.
name|CompressionType
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
name|MRJobConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|assertNotNull
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
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

begin_class
DECL|class|TestClientDistributedCacheManager
specifier|public
class|class
name|TestClientDistributedCacheManager
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
name|TestClientDistributedCacheManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
specifier|final
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
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
argument_list|,
name|TestClientDistributedCacheManager
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|TEST_VISIBILITY_PARENT_DIR
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_VISIBILITY_PARENT_DIR
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"TestCacheVisibility_Parent"
argument_list|)
decl_stmt|;
DECL|field|TEST_VISIBILITY_CHILD_DIR
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_VISIBILITY_CHILD_DIR
init|=
operator|new
name|Path
argument_list|(
name|TEST_VISIBILITY_PARENT_DIR
argument_list|,
literal|"TestCacheVisibility_Child"
argument_list|)
decl_stmt|;
DECL|field|FIRST_CACHE_FILE
specifier|private
specifier|static
specifier|final
name|String
name|FIRST_CACHE_FILE
init|=
literal|"firstcachefile"
decl_stmt|;
DECL|field|SECOND_CACHE_FILE
specifier|private
specifier|static
specifier|final
name|String
name|SECOND_CACHE_FILE
init|=
literal|"secondcachefile"
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|firstCacheFile
specifier|private
name|Path
name|firstCacheFile
decl_stmt|;
DECL|field|secondCacheFile
specifier|private
name|Path
name|secondCacheFile
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|firstCacheFile
operator|=
operator|new
name|Path
argument_list|(
name|TEST_VISIBILITY_PARENT_DIR
argument_list|,
name|FIRST_CACHE_FILE
argument_list|)
expr_stmt|;
name|secondCacheFile
operator|=
operator|new
name|Path
argument_list|(
name|TEST_VISIBILITY_CHILD_DIR
argument_list|,
name|SECOND_CACHE_FILE
argument_list|)
expr_stmt|;
name|createTempFile
argument_list|(
name|firstCacheFile
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|createTempFile
argument_list|(
name|secondCacheFile
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|fs
operator|.
name|delete
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to delete test root dir and its content under "
operator|+
name|TEST_ROOT_DIR
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDetermineTimestamps ()
specifier|public
name|void
name|testDetermineTimestamps
parameter_list|()
throws|throws
name|IOException
block|{
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
name|addCacheFile
argument_list|(
name|firstCacheFile
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|job
operator|.
name|addCacheFile
argument_list|(
name|secondCacheFile
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|Configuration
name|jobConf
init|=
name|job
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|URI
argument_list|,
name|FileStatus
argument_list|>
name|statCache
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|ClientDistributedCacheManager
operator|.
name|determineTimestamps
argument_list|(
name|jobConf
argument_list|,
name|statCache
argument_list|)
expr_stmt|;
name|FileStatus
name|firstStatus
init|=
name|statCache
operator|.
name|get
argument_list|(
name|firstCacheFile
operator|.
name|toUri
argument_list|()
argument_list|)
decl_stmt|;
name|FileStatus
name|secondStatus
init|=
name|statCache
operator|.
name|get
argument_list|(
name|secondCacheFile
operator|.
name|toUri
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|firstCacheFile
operator|+
literal|" was not found in the stats cache"
argument_list|,
name|firstStatus
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|secondCacheFile
operator|+
literal|" was not found in the stats cache"
argument_list|,
name|secondStatus
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Missing/extra entries found in the stats cache"
argument_list|,
literal|2
argument_list|,
name|statCache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|expected
init|=
name|firstStatus
operator|.
name|getModificationTime
argument_list|()
operator|+
literal|","
operator|+
name|secondStatus
operator|.
name|getModificationTime
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|jobConf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_TIMESTAMPS
argument_list|)
argument_list|)
expr_stmt|;
name|job
operator|=
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|job
operator|.
name|addCacheFile
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_VISIBILITY_CHILD_DIR
argument_list|,
literal|"*"
argument_list|)
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|jobConf
operator|=
name|job
operator|.
name|getConfiguration
argument_list|()
expr_stmt|;
name|statCache
operator|.
name|clear
argument_list|()
expr_stmt|;
name|ClientDistributedCacheManager
operator|.
name|determineTimestamps
argument_list|(
name|jobConf
argument_list|,
name|statCache
argument_list|)
expr_stmt|;
name|FileStatus
name|thirdStatus
init|=
name|statCache
operator|.
name|get
argument_list|(
name|TEST_VISIBILITY_CHILD_DIR
operator|.
name|toUri
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Missing/extra entries found in the stats cache"
argument_list|,
literal|1
argument_list|,
name|statCache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|TEST_VISIBILITY_CHILD_DIR
operator|+
literal|" was not found in the stats cache"
argument_list|,
name|thirdStatus
argument_list|)
expr_stmt|;
name|expected
operator|=
name|Long
operator|.
name|toString
argument_list|(
name|thirdStatus
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect timestamp for "
operator|+
name|TEST_VISIBILITY_CHILD_DIR
argument_list|,
name|expected
argument_list|,
name|jobConf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_TIMESTAMPS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDetermineCacheVisibilities ()
specifier|public
name|void
name|testDetermineCacheVisibilities
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|.
name|setPermission
argument_list|(
name|TEST_VISIBILITY_PARENT_DIR
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|00777
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setPermission
argument_list|(
name|TEST_VISIBILITY_CHILD_DIR
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|00777
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setWorkingDirectory
argument_list|(
name|TEST_VISIBILITY_CHILD_DIR
argument_list|)
expr_stmt|;
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
name|Path
name|relativePath
init|=
operator|new
name|Path
argument_list|(
name|SECOND_CACHE_FILE
argument_list|)
decl_stmt|;
name|Path
name|wildcardPath
init|=
operator|new
name|Path
argument_list|(
literal|"*"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|URI
argument_list|,
name|FileStatus
argument_list|>
name|statCache
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Configuration
name|jobConf
decl_stmt|;
name|job
operator|.
name|addCacheFile
argument_list|(
name|firstCacheFile
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|job
operator|.
name|addCacheFile
argument_list|(
name|relativePath
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|jobConf
operator|=
name|job
operator|.
name|getConfiguration
argument_list|()
expr_stmt|;
comment|// skip test if scratch dir is not PUBLIC
name|assumeTrue
argument_list|(
name|TEST_VISIBILITY_PARENT_DIR
operator|+
literal|" is not public"
argument_list|,
name|ClientDistributedCacheManager
operator|.
name|isPublic
argument_list|(
name|jobConf
argument_list|,
name|TEST_VISIBILITY_PARENT_DIR
operator|.
name|toUri
argument_list|()
argument_list|,
name|statCache
argument_list|)
argument_list|)
expr_stmt|;
name|ClientDistributedCacheManager
operator|.
name|determineCacheVisibilities
argument_list|(
name|jobConf
argument_list|,
name|statCache
argument_list|)
expr_stmt|;
comment|// We use get() instead of getBoolean() so we can tell the difference
comment|// between wrong and missing
name|assertEquals
argument_list|(
literal|"The file paths were not found to be publicly visible "
operator|+
literal|"even though the full path is publicly accessible"
argument_list|,
literal|"true,true"
argument_list|,
name|jobConf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_VISIBILITIES
argument_list|)
argument_list|)
expr_stmt|;
name|checkCacheEntries
argument_list|(
name|statCache
argument_list|,
literal|null
argument_list|,
name|firstCacheFile
argument_list|,
name|relativePath
argument_list|)
expr_stmt|;
name|job
operator|=
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|job
operator|.
name|addCacheFile
argument_list|(
name|wildcardPath
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|jobConf
operator|=
name|job
operator|.
name|getConfiguration
argument_list|()
expr_stmt|;
name|statCache
operator|.
name|clear
argument_list|()
expr_stmt|;
name|ClientDistributedCacheManager
operator|.
name|determineCacheVisibilities
argument_list|(
name|jobConf
argument_list|,
name|statCache
argument_list|)
expr_stmt|;
comment|// We use get() instead of getBoolean() so we can tell the difference
comment|// between wrong and missing
name|assertEquals
argument_list|(
literal|"The file path was not found to be publicly visible "
operator|+
literal|"even though the full path is publicly accessible"
argument_list|,
literal|"true"
argument_list|,
name|jobConf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_VISIBILITIES
argument_list|)
argument_list|)
expr_stmt|;
name|checkCacheEntries
argument_list|(
name|statCache
argument_list|,
literal|null
argument_list|,
name|wildcardPath
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|qualifiedParent
init|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|TEST_VISIBILITY_PARENT_DIR
argument_list|)
decl_stmt|;
name|fs
operator|.
name|setPermission
argument_list|(
name|TEST_VISIBILITY_PARENT_DIR
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|00700
argument_list|)
argument_list|)
expr_stmt|;
name|job
operator|=
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|job
operator|.
name|addCacheFile
argument_list|(
name|firstCacheFile
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|job
operator|.
name|addCacheFile
argument_list|(
name|relativePath
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|jobConf
operator|=
name|job
operator|.
name|getConfiguration
argument_list|()
expr_stmt|;
name|statCache
operator|.
name|clear
argument_list|()
expr_stmt|;
name|ClientDistributedCacheManager
operator|.
name|determineCacheVisibilities
argument_list|(
name|jobConf
argument_list|,
name|statCache
argument_list|)
expr_stmt|;
comment|// We use get() instead of getBoolean() so we can tell the difference
comment|// between wrong and missing
name|assertEquals
argument_list|(
literal|"The file paths were found to be publicly visible "
operator|+
literal|"even though the parent directory is not publicly accessible"
argument_list|,
literal|"false,false"
argument_list|,
name|jobConf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_VISIBILITIES
argument_list|)
argument_list|)
expr_stmt|;
name|checkCacheEntries
argument_list|(
name|statCache
argument_list|,
name|qualifiedParent
argument_list|,
name|firstCacheFile
argument_list|,
name|relativePath
argument_list|)
expr_stmt|;
name|job
operator|=
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|job
operator|.
name|addCacheFile
argument_list|(
name|wildcardPath
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|jobConf
operator|=
name|job
operator|.
name|getConfiguration
argument_list|()
expr_stmt|;
name|statCache
operator|.
name|clear
argument_list|()
expr_stmt|;
name|ClientDistributedCacheManager
operator|.
name|determineCacheVisibilities
argument_list|(
name|jobConf
argument_list|,
name|statCache
argument_list|)
expr_stmt|;
comment|// We use get() instead of getBoolean() so we can tell the difference
comment|// between wrong and missing
name|assertEquals
argument_list|(
literal|"The file path was found to be publicly visible "
operator|+
literal|"even though the parent directory is not publicly accessible"
argument_list|,
literal|"false"
argument_list|,
name|jobConf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_VISIBILITIES
argument_list|)
argument_list|)
expr_stmt|;
name|checkCacheEntries
argument_list|(
name|statCache
argument_list|,
name|qualifiedParent
argument_list|,
name|wildcardPath
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validate that the file status cache contains all and only entries for a    * given set of paths up to a common parent.    *    * @param statCache the cache    * @param top the common parent at which to stop digging    * @param paths the paths to compare against the cache    */
DECL|method|checkCacheEntries (Map<URI, FileStatus> statCache, Path top, Path... paths)
specifier|private
name|void
name|checkCacheEntries
parameter_list|(
name|Map
argument_list|<
name|URI
argument_list|,
name|FileStatus
argument_list|>
name|statCache
parameter_list|,
name|Path
name|top
parameter_list|,
name|Path
modifier|...
name|paths
parameter_list|)
block|{
name|Set
argument_list|<
name|URI
argument_list|>
name|expected
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Path
name|path
range|:
name|paths
control|)
block|{
name|Path
name|p
init|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|path
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|p
operator|.
name|isRoot
argument_list|()
operator|&&
operator|!
name|p
operator|.
name|equals
argument_list|(
name|top
argument_list|)
condition|)
block|{
name|expected
operator|.
name|add
argument_list|(
name|p
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|=
name|p
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
name|expected
operator|.
name|add
argument_list|(
name|p
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|URI
argument_list|>
name|uris
init|=
name|statCache
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|URI
argument_list|>
name|missing
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|uris
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|URI
argument_list|>
name|extra
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|expected
argument_list|)
decl_stmt|;
name|missing
operator|.
name|removeAll
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|extra
operator|.
name|removeAll
argument_list|(
name|uris
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"File status cache does not contain an entries for "
operator|+
name|missing
argument_list|,
name|missing
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"File status cache contains extra extries: "
operator|+
name|extra
argument_list|,
name|extra
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|createTempFile (Path p, Configuration conf)
name|void
name|createTempFile
parameter_list|(
name|Path
name|p
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|SequenceFile
operator|.
name|Writer
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|writer
operator|=
name|SequenceFile
operator|.
name|createWriter
argument_list|(
name|fs
argument_list|,
name|conf
argument_list|,
name|p
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|CompressionType
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
operator|new
name|Text
argument_list|(
literal|"text"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"moretext"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|writer
operator|=
literal|null
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"created: "
operator|+
name|p
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

