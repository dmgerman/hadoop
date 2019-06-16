begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.scale
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|scale
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
name|s3a
operator|.
name|S3AFileStatus
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
name|s3a
operator|.
name|s3guard
operator|.
name|ITtlTimeProvider
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
name|s3a
operator|.
name|s3guard
operator|.
name|MetadataStore
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
name|s3a
operator|.
name|s3guard
operator|.
name|PathMetadata
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
name|s3a
operator|.
name|s3guard
operator|.
name|S3Guard
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
name|FixMethodOrder
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
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|MethodSorters
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|contract
operator|.
name|ContractTestUtils
operator|.
name|NanoTimer
import|;
end_import

begin_comment
comment|/**  * Test the performance of a MetadataStore.  Useful for load testing.  * Could be separated from S3A code, but we're using the S3A scale test  * framework for convenience.  */
end_comment

begin_class
annotation|@
name|FixMethodOrder
argument_list|(
name|MethodSorters
operator|.
name|NAME_ASCENDING
argument_list|)
DECL|class|AbstractITestS3AMetadataStoreScale
specifier|public
specifier|abstract
class|class
name|AbstractITestS3AMetadataStoreScale
extends|extends
name|S3AScaleTestBase
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
name|AbstractITestS3AMetadataStoreScale
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Some dummy values for FileStatus contents. */
DECL|field|BLOCK_SIZE
specifier|static
specifier|final
name|long
name|BLOCK_SIZE
init|=
literal|32
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|SIZE
specifier|static
specifier|final
name|long
name|SIZE
init|=
name|BLOCK_SIZE
operator|*
literal|2
decl_stmt|;
DECL|field|OWNER
specifier|static
specifier|final
name|String
name|OWNER
init|=
literal|"bob"
decl_stmt|;
DECL|field|ACCESS_TIME
specifier|static
specifier|final
name|long
name|ACCESS_TIME
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|field|BUCKET_ROOT
specifier|static
specifier|final
name|Path
name|BUCKET_ROOT
init|=
operator|new
name|Path
argument_list|(
literal|"s3a://fake-bucket/"
argument_list|)
decl_stmt|;
DECL|field|ttlTimeProvider
specifier|private
name|ITtlTimeProvider
name|ttlTimeProvider
decl_stmt|;
annotation|@
name|Before
DECL|method|initialize ()
specifier|public
name|void
name|initialize
parameter_list|()
block|{
name|ttlTimeProvider
operator|=
operator|new
name|S3Guard
operator|.
name|TtlTimeProvider
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Subclasses should override this to provide the MetadataStore they which    * to test.    * @return MetadataStore to test against    * @throws IOException    */
DECL|method|createMetadataStore ()
specifier|public
specifier|abstract
name|MetadataStore
name|createMetadataStore
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Test
DECL|method|test_010_Put ()
specifier|public
name|void
name|test_010_Put
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Test workload of put() operations"
argument_list|)
expr_stmt|;
comment|// As described in hadoop-aws site docs, count parameter is used for
comment|// width and depth of directory tree
name|int
name|width
init|=
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|KEY_DIRECTORY_COUNT
argument_list|,
name|DEFAULT_DIRECTORY_COUNT
argument_list|)
decl_stmt|;
name|int
name|depth
init|=
name|width
decl_stmt|;
name|List
argument_list|<
name|PathMetadata
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|createDirTree
argument_list|(
name|BUCKET_ROOT
argument_list|,
name|depth
argument_list|,
name|width
argument_list|,
name|paths
argument_list|)
expr_stmt|;
name|long
name|count
init|=
literal|1
decl_stmt|;
comment|// Some value in case we throw an exception below
try|try
init|(
name|MetadataStore
name|ms
init|=
name|createMetadataStore
argument_list|()
init|)
block|{
try|try
block|{
name|count
operator|=
name|populateMetadataStore
argument_list|(
name|paths
argument_list|,
name|ms
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|clearMetadataStore
argument_list|(
name|ms
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|test_020_Moves ()
specifier|public
name|void
name|test_020_Moves
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Test workload of batched move() operations"
argument_list|)
expr_stmt|;
comment|// As described in hadoop-aws site docs, count parameter is used for
comment|// width and depth of directory tree
name|int
name|width
init|=
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|KEY_DIRECTORY_COUNT
argument_list|,
name|DEFAULT_DIRECTORY_COUNT
argument_list|)
decl_stmt|;
name|int
name|depth
init|=
name|width
decl_stmt|;
name|long
name|operations
init|=
name|getConf
argument_list|()
operator|.
name|getLong
argument_list|(
name|KEY_OPERATION_COUNT
argument_list|,
name|DEFAULT_OPERATION_COUNT
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|PathMetadata
argument_list|>
name|origMetas
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|createDirTree
argument_list|(
name|BUCKET_ROOT
argument_list|,
name|depth
argument_list|,
name|width
argument_list|,
name|origMetas
argument_list|)
expr_stmt|;
comment|// Pre-compute source and destination paths for move() loop below
name|List
argument_list|<
name|Path
argument_list|>
name|origPaths
init|=
name|metasToPaths
argument_list|(
name|origMetas
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|PathMetadata
argument_list|>
name|movedMetas
init|=
name|moveMetas
argument_list|(
name|origMetas
argument_list|,
name|BUCKET_ROOT
argument_list|,
operator|new
name|Path
argument_list|(
name|BUCKET_ROOT
argument_list|,
literal|"moved-here"
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|movedPaths
init|=
name|metasToPaths
argument_list|(
name|movedMetas
argument_list|)
decl_stmt|;
name|long
name|count
init|=
literal|1
decl_stmt|;
comment|// Some value in case we throw an exception below
try|try
init|(
name|MetadataStore
name|ms
init|=
name|createMetadataStore
argument_list|()
init|)
block|{
try|try
block|{
comment|// Setup
name|count
operator|=
name|populateMetadataStore
argument_list|(
name|origMetas
argument_list|,
name|ms
argument_list|)
expr_stmt|;
comment|// Main loop: move things back and forth
name|describe
argument_list|(
literal|"Running move workload"
argument_list|)
expr_stmt|;
name|NanoTimer
name|moveTimer
init|=
operator|new
name|NanoTimer
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Running {} moves of {} paths each"
argument_list|,
name|operations
argument_list|,
name|origMetas
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|operations
condition|;
name|i
operator|++
control|)
block|{
name|Collection
argument_list|<
name|Path
argument_list|>
name|toDelete
decl_stmt|;
name|Collection
argument_list|<
name|PathMetadata
argument_list|>
name|toCreate
decl_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|toDelete
operator|=
name|origPaths
expr_stmt|;
name|toCreate
operator|=
name|movedMetas
expr_stmt|;
block|}
else|else
block|{
name|toDelete
operator|=
name|movedPaths
expr_stmt|;
name|toCreate
operator|=
name|origMetas
expr_stmt|;
block|}
name|ms
operator|.
name|move
argument_list|(
name|toDelete
argument_list|,
name|toCreate
argument_list|,
name|ttlTimeProvider
argument_list|)
expr_stmt|;
block|}
name|moveTimer
operator|.
name|end
argument_list|()
expr_stmt|;
name|printTiming
argument_list|(
name|LOG
argument_list|,
literal|"move"
argument_list|,
name|moveTimer
argument_list|,
name|operations
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// Cleanup
name|clearMetadataStore
argument_list|(
name|ms
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Create a copy of given list of PathMetadatas with the paths moved from    * src to dest.    */
DECL|method|moveMetas (List<PathMetadata> metas, Path src, Path dest)
specifier|protected
name|List
argument_list|<
name|PathMetadata
argument_list|>
name|moveMetas
parameter_list|(
name|List
argument_list|<
name|PathMetadata
argument_list|>
name|metas
parameter_list|,
name|Path
name|src
parameter_list|,
name|Path
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|PathMetadata
argument_list|>
name|moved
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|metas
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|PathMetadata
name|srcMeta
range|:
name|metas
control|)
block|{
name|S3AFileStatus
name|status
init|=
name|copyStatus
argument_list|(
operator|(
name|S3AFileStatus
operator|)
name|srcMeta
operator|.
name|getFileStatus
argument_list|()
argument_list|)
decl_stmt|;
name|status
operator|.
name|setPath
argument_list|(
name|movePath
argument_list|(
name|status
operator|.
name|getPath
argument_list|()
argument_list|,
name|src
argument_list|,
name|dest
argument_list|)
argument_list|)
expr_stmt|;
name|moved
operator|.
name|add
argument_list|(
operator|new
name|PathMetadata
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|moved
return|;
block|}
DECL|method|movePath (Path p, Path src, Path dest)
specifier|protected
name|Path
name|movePath
parameter_list|(
name|Path
name|p
parameter_list|,
name|Path
name|src
parameter_list|,
name|Path
name|dest
parameter_list|)
block|{
name|String
name|srcStr
init|=
name|src
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|pathStr
init|=
name|p
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
comment|// Strip off src dir
name|pathStr
operator|=
name|pathStr
operator|.
name|substring
argument_list|(
name|srcStr
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// Prepend new dest
return|return
operator|new
name|Path
argument_list|(
name|dest
argument_list|,
name|pathStr
argument_list|)
return|;
block|}
DECL|method|copyStatus (S3AFileStatus status)
specifier|protected
name|S3AFileStatus
name|copyStatus
parameter_list|(
name|S3AFileStatus
name|status
parameter_list|)
block|{
if|if
condition|(
name|status
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
return|return
operator|new
name|S3AFileStatus
argument_list|(
name|status
operator|.
name|isEmptyDirectory
argument_list|()
argument_list|,
name|status
operator|.
name|getPath
argument_list|()
argument_list|,
name|status
operator|.
name|getOwner
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|S3AFileStatus
argument_list|(
name|status
operator|.
name|getLen
argument_list|()
argument_list|,
name|status
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|status
operator|.
name|getPath
argument_list|()
argument_list|,
name|status
operator|.
name|getBlockSize
argument_list|()
argument_list|,
name|status
operator|.
name|getOwner
argument_list|()
argument_list|,
name|status
operator|.
name|getETag
argument_list|()
argument_list|,
name|status
operator|.
name|getVersionId
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/** @return number of PathMetadatas put() into MetadataStore */
DECL|method|populateMetadataStore (Collection<PathMetadata> paths, MetadataStore ms)
specifier|private
name|long
name|populateMetadataStore
parameter_list|(
name|Collection
argument_list|<
name|PathMetadata
argument_list|>
name|paths
parameter_list|,
name|MetadataStore
name|ms
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|count
init|=
literal|0
decl_stmt|;
name|NanoTimer
name|putTimer
init|=
operator|new
name|NanoTimer
argument_list|()
decl_stmt|;
name|describe
argument_list|(
literal|"Inserting into MetadataStore"
argument_list|)
expr_stmt|;
for|for
control|(
name|PathMetadata
name|p
range|:
name|paths
control|)
block|{
name|ms
operator|.
name|put
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|putTimer
operator|.
name|end
argument_list|()
expr_stmt|;
name|printTiming
argument_list|(
name|LOG
argument_list|,
literal|"put"
argument_list|,
name|putTimer
argument_list|,
name|count
argument_list|)
expr_stmt|;
return|return
name|count
return|;
block|}
DECL|method|clearMetadataStore (MetadataStore ms, long count)
specifier|protected
name|void
name|clearMetadataStore
parameter_list|(
name|MetadataStore
name|ms
parameter_list|,
name|long
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|describe
argument_list|(
literal|"Recursive deletion"
argument_list|)
expr_stmt|;
name|NanoTimer
name|deleteTimer
init|=
operator|new
name|NanoTimer
argument_list|()
decl_stmt|;
name|ms
operator|.
name|deleteSubtree
argument_list|(
name|BUCKET_ROOT
argument_list|,
name|ttlTimeProvider
argument_list|)
expr_stmt|;
name|deleteTimer
operator|.
name|end
argument_list|()
expr_stmt|;
name|printTiming
argument_list|(
name|LOG
argument_list|,
literal|"delete"
argument_list|,
name|deleteTimer
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
DECL|method|printTiming (Logger log, String op, NanoTimer timer, long count)
specifier|private
specifier|static
name|void
name|printTiming
parameter_list|(
name|Logger
name|log
parameter_list|,
name|String
name|op
parameter_list|,
name|NanoTimer
name|timer
parameter_list|,
name|long
name|count
parameter_list|)
block|{
name|double
name|msec
init|=
operator|(
name|double
operator|)
name|timer
operator|.
name|duration
argument_list|()
operator|/
literal|1000
decl_stmt|;
name|double
name|msecPerOp
init|=
name|msec
operator|/
name|count
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Elapsed %.2f msec. %.3f msec / %s (%d ops)"
argument_list|,
name|msec
argument_list|,
name|msecPerOp
argument_list|,
name|op
argument_list|,
name|count
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|makeFileStatus (Path path)
specifier|protected
specifier|static
name|S3AFileStatus
name|makeFileStatus
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|S3AFileStatus
argument_list|(
name|SIZE
argument_list|,
name|ACCESS_TIME
argument_list|,
name|path
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|OWNER
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|makeDirStatus (Path p)
specifier|protected
specifier|static
name|S3AFileStatus
name|makeDirStatus
parameter_list|(
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|S3AFileStatus
argument_list|(
literal|false
argument_list|,
name|p
argument_list|,
name|OWNER
argument_list|)
return|;
block|}
DECL|method|metasToPaths (List<PathMetadata> metas)
specifier|protected
name|List
argument_list|<
name|Path
argument_list|>
name|metasToPaths
parameter_list|(
name|List
argument_list|<
name|PathMetadata
argument_list|>
name|metas
parameter_list|)
block|{
name|List
argument_list|<
name|Path
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|metas
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|PathMetadata
name|meta
range|:
name|metas
control|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|meta
operator|.
name|getFileStatus
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|paths
return|;
block|}
comment|/**    * Recursively create a directory tree.    * @param parent Parent dir of the paths to create.    * @param depth How many more levels deep past parent to create.    * @param width Number of files (and directories, if depth> 0) per directory.    * @param paths List to add generated paths to.    */
DECL|method|createDirTree (Path parent, int depth, int width, Collection<PathMetadata> paths)
specifier|protected
specifier|static
name|void
name|createDirTree
parameter_list|(
name|Path
name|parent
parameter_list|,
name|int
name|depth
parameter_list|,
name|int
name|width
parameter_list|,
name|Collection
argument_list|<
name|PathMetadata
argument_list|>
name|paths
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Create files
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|width
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|parent
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"file-%d"
argument_list|,
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|PathMetadata
name|meta
init|=
operator|new
name|PathMetadata
argument_list|(
name|makeFileStatus
argument_list|(
name|p
argument_list|)
argument_list|)
decl_stmt|;
name|paths
operator|.
name|add
argument_list|(
name|meta
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|depth
operator|==
literal|0
condition|)
block|{
return|return;
block|}
comment|// Create directories if there is depth remaining
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|width
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
name|parent
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"dir-%d"
argument_list|,
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|PathMetadata
name|meta
init|=
operator|new
name|PathMetadata
argument_list|(
name|makeDirStatus
argument_list|(
name|dir
argument_list|)
argument_list|)
decl_stmt|;
name|paths
operator|.
name|add
argument_list|(
name|meta
argument_list|)
expr_stmt|;
name|createDirTree
argument_list|(
name|dir
argument_list|,
name|depth
operator|-
literal|1
argument_list|,
name|width
argument_list|,
name|paths
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

