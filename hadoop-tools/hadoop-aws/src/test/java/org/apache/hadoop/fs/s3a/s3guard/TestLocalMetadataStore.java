begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.s3guard
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
name|s3guard
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
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Ticker
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|Cache
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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
name|S3ATestUtils
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
name|Tristate
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
name|mock
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
name|when
import|;
end_import

begin_comment
comment|/**  * MetadataStore unit test for {@link LocalMetadataStore}.  */
end_comment

begin_class
DECL|class|TestLocalMetadataStore
specifier|public
class|class
name|TestLocalMetadataStore
extends|extends
name|MetadataStoreTestBase
block|{
DECL|class|LocalMSContract
specifier|private
specifier|final
specifier|static
class|class
name|LocalMSContract
extends|extends
name|AbstractMSContract
block|{
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|method|LocalMSContract ()
specifier|private
name|LocalMSContract
parameter_list|()
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|LocalMSContract (Configuration config)
specifier|private
name|LocalMSContract
parameter_list|(
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFileSystem ()
specifier|public
name|FileSystem
name|getFileSystem
parameter_list|()
block|{
return|return
name|fs
return|;
block|}
annotation|@
name|Override
DECL|method|getMetadataStore ()
specifier|public
name|MetadataStore
name|getMetadataStore
parameter_list|()
throws|throws
name|IOException
block|{
name|LocalMetadataStore
name|lms
init|=
operator|new
name|LocalMetadataStore
argument_list|()
decl_stmt|;
return|return
name|lms
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|createContract ()
specifier|public
name|AbstractMSContract
name|createContract
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|LocalMSContract
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createContract (Configuration conf)
specifier|public
name|AbstractMSContract
name|createContract
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|LocalMSContract
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|getPathStringForPrune (String path)
annotation|@
name|Override
specifier|protected
name|String
name|getPathStringForPrune
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|path
return|;
block|}
annotation|@
name|Test
DECL|method|testClearByAncestor ()
specifier|public
name|void
name|testClearByAncestor
parameter_list|()
throws|throws
name|Exception
block|{
name|Cache
argument_list|<
name|Path
argument_list|,
name|LocalMetadataEntry
argument_list|>
name|cache
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// 1. Test paths without scheme/host
name|assertClearResult
argument_list|(
name|cache
argument_list|,
literal|""
argument_list|,
literal|"/"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertClearResult
argument_list|(
name|cache
argument_list|,
literal|""
argument_list|,
literal|"/dirA/dirB"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertClearResult
argument_list|(
name|cache
argument_list|,
literal|""
argument_list|,
literal|"/invalid"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
comment|// 2. Test paths w/ scheme/host
name|String
name|p
init|=
literal|"s3a://fake-bucket-name"
decl_stmt|;
name|assertClearResult
argument_list|(
name|cache
argument_list|,
name|p
argument_list|,
literal|"/"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertClearResult
argument_list|(
name|cache
argument_list|,
name|p
argument_list|,
literal|"/dirA/dirB"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertClearResult
argument_list|(
name|cache
argument_list|,
name|p
argument_list|,
literal|"/invalid"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|class|TestTicker
specifier|static
class|class
name|TestTicker
extends|extends
name|Ticker
block|{
DECL|field|myTicker
specifier|private
name|long
name|myTicker
init|=
literal|0
decl_stmt|;
DECL|method|read ()
annotation|@
name|Override
specifier|public
name|long
name|read
parameter_list|()
block|{
return|return
name|myTicker
return|;
block|}
DECL|method|set (long val)
specifier|public
name|void
name|set
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|this
operator|.
name|myTicker
operator|=
name|val
expr_stmt|;
block|}
block|}
comment|/**    * Test that time eviction in cache used in {@link LocalMetadataStore}    * implementation working properly.    *    * The test creates a Ticker instance, which will be used to control the    * internal clock of the cache to achieve eviction without having to wait    * for the system clock.    * The test creates 3 entry: 2nd and 3rd entry will survive the eviction,    * because it will be created later than the 1st - using the ticker.    */
annotation|@
name|Test
DECL|method|testCacheTimedEvictionAfterWrite ()
specifier|public
name|void
name|testCacheTimedEvictionAfterWrite
parameter_list|()
block|{
name|TestTicker
name|testTicker
init|=
operator|new
name|TestTicker
argument_list|()
decl_stmt|;
specifier|final
name|long
name|t0
init|=
name|testTicker
operator|.
name|read
argument_list|()
decl_stmt|;
specifier|final
name|long
name|t1
init|=
name|t0
operator|+
literal|100
decl_stmt|;
specifier|final
name|long
name|t2
init|=
name|t1
operator|+
literal|100
decl_stmt|;
specifier|final
name|long
name|ttl
init|=
name|t1
operator|+
literal|50
decl_stmt|;
comment|// between t1 and t2
name|Cache
argument_list|<
name|Path
argument_list|,
name|LocalMetadataEntry
argument_list|>
name|cache
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|expireAfterWrite
argument_list|(
name|ttl
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
comment|/* nanos to avoid conversions */
argument_list|)
operator|.
name|ticker
argument_list|(
name|testTicker
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|p
init|=
literal|"s3a://fake-bucket-name"
decl_stmt|;
name|Path
name|path1
init|=
operator|new
name|Path
argument_list|(
name|p
operator|+
literal|"/dirA/dirB/file1"
argument_list|)
decl_stmt|;
name|Path
name|path2
init|=
operator|new
name|Path
argument_list|(
name|p
operator|+
literal|"/dirA/dirB/file2"
argument_list|)
decl_stmt|;
name|Path
name|path3
init|=
operator|new
name|Path
argument_list|(
name|p
operator|+
literal|"/dirA/dirB/file3"
argument_list|)
decl_stmt|;
comment|// Test time is t0
name|populateEntry
argument_list|(
name|cache
argument_list|,
name|path1
argument_list|)
expr_stmt|;
comment|// set new value on the ticker, so the next two entries will be added later
name|testTicker
operator|.
name|set
argument_list|(
name|t1
argument_list|)
expr_stmt|;
comment|// Test time is now t1
name|populateEntry
argument_list|(
name|cache
argument_list|,
name|path2
argument_list|)
expr_stmt|;
name|populateEntry
argument_list|(
name|cache
argument_list|,
name|path3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Cache should contain 3 records before eviction"
argument_list|,
literal|3
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LocalMetadataEntry
name|pm1
init|=
name|cache
operator|.
name|getIfPresent
argument_list|(
name|path1
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"PathMetadata should not be null before eviction"
argument_list|,
name|pm1
argument_list|)
expr_stmt|;
comment|// set the ticker to a time when timed eviction should occur
comment|// for the first entry
name|testTicker
operator|.
name|set
argument_list|(
name|t2
argument_list|)
expr_stmt|;
comment|// call cleanup explicitly, as timed expiration is performed with
comment|// periodic maintenance during writes and occasionally during reads only
name|cache
operator|.
name|cleanUp
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Cache size should be 2 after eviction"
argument_list|,
literal|2
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|pm1
operator|=
name|cache
operator|.
name|getIfPresent
argument_list|(
name|path1
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"PathMetadata should be null after eviction"
argument_list|,
name|pm1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpdateParentLastUpdatedOnPutNewParent ()
specifier|public
name|void
name|testUpdateParentLastUpdatedOnPutNewParent
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
literal|"This test only applies if metadatastore does not allow"
operator|+
literal|" missing values (skip for NullMS)."
argument_list|,
operator|!
name|allowMissing
argument_list|()
argument_list|)
expr_stmt|;
name|ITtlTimeProvider
name|tp
init|=
name|mock
argument_list|(
name|ITtlTimeProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|ITtlTimeProvider
name|originalTimeProvider
init|=
name|getTtlTimeProvider
argument_list|()
decl_stmt|;
name|long
name|now
init|=
literal|100L
decl_stmt|;
specifier|final
name|String
name|parent
init|=
literal|"/parentUpdated-"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
specifier|final
name|String
name|child
init|=
name|parent
operator|+
literal|"/file1"
decl_stmt|;
try|try
block|{
name|when
argument_list|(
name|tp
operator|.
name|getNow
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|now
argument_list|)
expr_stmt|;
comment|// create a file
name|ms
operator|.
name|put
argument_list|(
operator|new
name|PathMetadata
argument_list|(
name|makeFileStatus
argument_list|(
name|child
argument_list|,
literal|100
argument_list|)
argument_list|,
name|tp
operator|.
name|getNow
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|PathMetadata
name|fileMeta
init|=
name|ms
operator|.
name|get
argument_list|(
name|strToPath
argument_list|(
name|child
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"lastUpdated field of first file should be equal to the "
operator|+
literal|"mocked value"
argument_list|,
name|now
argument_list|,
name|fileMeta
operator|.
name|getLastUpdated
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|DirListingMetadata
name|listing
init|=
name|ms
operator|.
name|listChildren
argument_list|(
name|strToPath
argument_list|(
name|parent
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Listing lastUpdated field should be equal to the mocked "
operator|+
literal|"time value."
argument_list|,
name|now
argument_list|,
name|listing
operator|.
name|getLastUpdated
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|ms
operator|.
name|setTtlTimeProvider
argument_list|(
name|originalTimeProvider
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|populateMap (Cache<Path, LocalMetadataEntry> cache, String prefix)
specifier|private
specifier|static
name|void
name|populateMap
parameter_list|(
name|Cache
argument_list|<
name|Path
argument_list|,
name|LocalMetadataEntry
argument_list|>
name|cache
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|populateEntry
argument_list|(
name|cache
argument_list|,
operator|new
name|Path
argument_list|(
name|prefix
operator|+
literal|"/dirA/dirB/"
argument_list|)
argument_list|)
expr_stmt|;
name|populateEntry
argument_list|(
name|cache
argument_list|,
operator|new
name|Path
argument_list|(
name|prefix
operator|+
literal|"/dirA/dirB/dirC"
argument_list|)
argument_list|)
expr_stmt|;
name|populateEntry
argument_list|(
name|cache
argument_list|,
operator|new
name|Path
argument_list|(
name|prefix
operator|+
literal|"/dirA/dirB/dirC/file1"
argument_list|)
argument_list|)
expr_stmt|;
name|populateEntry
argument_list|(
name|cache
argument_list|,
operator|new
name|Path
argument_list|(
name|prefix
operator|+
literal|"/dirA/dirB/dirC/file2"
argument_list|)
argument_list|)
expr_stmt|;
name|populateEntry
argument_list|(
name|cache
argument_list|,
operator|new
name|Path
argument_list|(
name|prefix
operator|+
literal|"/dirA/file1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|populateEntry (Cache<Path, LocalMetadataEntry> cache, Path path)
specifier|private
specifier|static
name|void
name|populateEntry
parameter_list|(
name|Cache
argument_list|<
name|Path
argument_list|,
name|LocalMetadataEntry
argument_list|>
name|cache
parameter_list|,
name|Path
name|path
parameter_list|)
block|{
name|S3AFileStatus
name|s3aStatus
init|=
operator|new
name|S3AFileStatus
argument_list|(
name|Tristate
operator|.
name|UNKNOWN
argument_list|,
name|path
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|path
argument_list|,
operator|new
name|LocalMetadataEntry
argument_list|(
operator|new
name|PathMetadata
argument_list|(
name|s3aStatus
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|sizeOfMap (Cache<Path, LocalMetadataEntry> cache)
specifier|private
specifier|static
name|long
name|sizeOfMap
parameter_list|(
name|Cache
argument_list|<
name|Path
argument_list|,
name|LocalMetadataEntry
argument_list|>
name|cache
parameter_list|)
block|{
return|return
name|cache
operator|.
name|asMap
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|entry
lambda|->
operator|!
name|entry
operator|.
name|getFileMeta
argument_list|()
operator|.
name|isDeleted
argument_list|()
argument_list|)
operator|.
name|count
argument_list|()
return|;
block|}
DECL|method|assertClearResult (Cache<Path, LocalMetadataEntry> cache, String prefixStr, String pathStr, int leftoverSize)
specifier|private
specifier|static
name|void
name|assertClearResult
parameter_list|(
name|Cache
argument_list|<
name|Path
argument_list|,
name|LocalMetadataEntry
argument_list|>
name|cache
parameter_list|,
name|String
name|prefixStr
parameter_list|,
name|String
name|pathStr
parameter_list|,
name|int
name|leftoverSize
parameter_list|)
throws|throws
name|IOException
block|{
name|populateMap
argument_list|(
name|cache
argument_list|,
name|prefixStr
argument_list|)
expr_stmt|;
name|LocalMetadataStore
operator|.
name|deleteEntryByAncestor
argument_list|(
operator|new
name|Path
argument_list|(
name|prefixStr
operator|+
name|pathStr
argument_list|)
argument_list|,
name|cache
argument_list|,
literal|true
argument_list|,
name|getTtlTimeProvider
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cache should have %d entries"
argument_list|,
name|leftoverSize
argument_list|)
argument_list|,
name|leftoverSize
argument_list|,
name|sizeOfMap
argument_list|(
name|cache
argument_list|)
argument_list|)
expr_stmt|;
name|cache
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|verifyFileStatus (FileStatus status, long size)
specifier|protected
name|void
name|verifyFileStatus
parameter_list|(
name|FileStatus
name|status
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|S3ATestUtils
operator|.
name|verifyFileStatus
argument_list|(
name|status
argument_list|,
name|size
argument_list|,
name|REPLICATION
argument_list|,
name|getModTime
argument_list|()
argument_list|,
name|getAccessTime
argument_list|()
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|OWNER
argument_list|,
name|GROUP
argument_list|,
name|PERMISSION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|verifyDirStatus (S3AFileStatus status)
specifier|protected
name|void
name|verifyDirStatus
parameter_list|(
name|S3AFileStatus
name|status
parameter_list|)
block|{
name|S3ATestUtils
operator|.
name|verifyDirStatus
argument_list|(
name|status
argument_list|,
name|REPLICATION
argument_list|,
name|OWNER
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

