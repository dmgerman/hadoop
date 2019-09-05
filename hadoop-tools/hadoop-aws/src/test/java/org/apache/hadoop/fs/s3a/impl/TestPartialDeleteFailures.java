begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.impl
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
name|impl
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

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
name|ArrayList
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|DeleteObjectsRequest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|MultiObjectDeleteException
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
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|assertj
operator|.
name|core
operator|.
name|api
operator|.
name|Assertions
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
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|tuple
operator|.
name|Pair
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
name|lang3
operator|.
name|tuple
operator|.
name|Triple
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
name|Constants
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
name|Invoker
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
name|S3AInputPolicy
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
name|S3AInstrumentation
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
name|S3AStorageStatistics
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
name|BulkOperationState
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
name|DirListingMetadata
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
name|RenameTracker
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
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|retry
operator|.
name|RetryPolicies
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
name|UserGroupInformation
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
name|BlockingThreadPoolExecutorService
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
name|s3a
operator|.
name|impl
operator|.
name|MultiObjectDeleteSupport
operator|.
name|ACCESS_DENIED
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
name|s3a
operator|.
name|impl
operator|.
name|MultiObjectDeleteSupport
operator|.
name|removeUndeletedPaths
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

begin_comment
comment|/**  * Unit test suite covering translation of AWS SDK exceptions to S3A exceptions,  * and retry/recovery policies.  */
end_comment

begin_class
DECL|class|TestPartialDeleteFailures
specifier|public
class|class
name|TestPartialDeleteFailures
block|{
DECL|field|CONTEXT_ACCESSORS
specifier|private
specifier|static
specifier|final
name|ContextAccessors
name|CONTEXT_ACCESSORS
init|=
operator|new
name|MinimalContextAccessor
argument_list|()
decl_stmt|;
DECL|field|context
specifier|private
name|StoreContext
name|context
decl_stmt|;
DECL|method|qualifyKey (String k)
specifier|private
specifier|static
name|Path
name|qualifyKey
parameter_list|(
name|String
name|k
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
literal|"s3a://bucket/"
operator|+
name|k
argument_list|)
return|;
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|context
operator|=
name|createMockStoreContext
argument_list|(
literal|true
argument_list|,
operator|new
name|OperationTrackingStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteExtraction ()
specifier|public
name|void
name|testDeleteExtraction
parameter_list|()
block|{
name|List
argument_list|<
name|Path
argument_list|>
name|src
init|=
name|pathList
argument_list|(
literal|"a"
argument_list|,
literal|"a/b"
argument_list|,
literal|"a/c"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|rejected
init|=
name|pathList
argument_list|(
literal|"a/b"
argument_list|)
decl_stmt|;
name|MultiObjectDeleteException
name|ex
init|=
name|createDeleteException
argument_list|(
name|ACCESS_DENIED
argument_list|,
name|rejected
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|undeleted
init|=
name|removeUndeletedPaths
argument_list|(
name|ex
argument_list|,
name|src
argument_list|,
name|TestPartialDeleteFailures
operator|::
name|qualifyKey
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"mismatch of rejected and undeleted entries"
argument_list|,
name|rejected
argument_list|,
name|undeleted
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSplitKeysFromResults ()
specifier|public
name|void
name|testSplitKeysFromResults
parameter_list|()
throws|throws
name|Throwable
block|{
name|List
argument_list|<
name|Path
argument_list|>
name|src
init|=
name|pathList
argument_list|(
literal|"a"
argument_list|,
literal|"a/b"
argument_list|,
literal|"a/c"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|rejected
init|=
name|pathList
argument_list|(
literal|"a/b"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DeleteObjectsRequest
operator|.
name|KeyVersion
argument_list|>
name|keys
init|=
name|keysToDelete
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|MultiObjectDeleteException
name|ex
init|=
name|createDeleteException
argument_list|(
name|ACCESS_DENIED
argument_list|,
name|rejected
argument_list|)
decl_stmt|;
name|Pair
argument_list|<
name|List
argument_list|<
name|Path
argument_list|>
argument_list|,
name|List
argument_list|<
name|Path
argument_list|>
argument_list|>
name|pair
init|=
operator|new
name|MultiObjectDeleteSupport
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
operator|.
name|splitUndeletedKeys
argument_list|(
name|ex
argument_list|,
name|keys
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|undeleted
init|=
name|pair
operator|.
name|getLeft
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|deleted
init|=
name|pair
operator|.
name|getRight
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|rejected
argument_list|,
name|undeleted
argument_list|)
expr_stmt|;
comment|// now check the deleted list to verify that it is valid
name|src
operator|.
name|remove
argument_list|(
name|rejected
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|src
argument_list|,
name|deleted
argument_list|)
expr_stmt|;
block|}
comment|/**    * Build a list of qualified paths from vararg parameters.    * @param paths paths to qualify and then convert to a lst.    * @return same paths as a list.    */
DECL|method|pathList (String... paths)
specifier|private
name|List
argument_list|<
name|Path
argument_list|>
name|pathList
parameter_list|(
name|String
modifier|...
name|paths
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|stream
argument_list|(
name|paths
argument_list|)
operator|.
name|map
argument_list|(
name|TestPartialDeleteFailures
operator|::
name|qualifyKey
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Build a delete exception containing all the rejected paths.    * The list of successful entries is empty.    * @param rejected the rejected paths.    * @return a new exception    */
DECL|method|createDeleteException ( final String code, final List<Path> rejected)
specifier|private
name|MultiObjectDeleteException
name|createDeleteException
parameter_list|(
specifier|final
name|String
name|code
parameter_list|,
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|rejected
parameter_list|)
block|{
name|List
argument_list|<
name|MultiObjectDeleteException
operator|.
name|DeleteError
argument_list|>
name|errors
init|=
name|rejected
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
parameter_list|(
name|p
parameter_list|)
lambda|->
block|{
name|MultiObjectDeleteException
operator|.
name|DeleteError
name|e
init|=
operator|new
name|MultiObjectDeleteException
operator|.
name|DeleteError
argument_list|()
decl_stmt|;
name|e
operator|.
name|setKey
argument_list|(
name|p
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|setCode
argument_list|(
name|code
argument_list|)
expr_stmt|;
name|e
operator|.
name|setMessage
argument_list|(
literal|"forbidden"
argument_list|)
expr_stmt|;
return|return
name|e
return|;
block|}
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|MultiObjectDeleteException
argument_list|(
name|errors
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * From a list of paths, build up the list of keys for a delete request.    * @param paths path list    * @return a key list suitable for a delete request.    */
DECL|method|keysToDelete ( List<Path> paths)
specifier|public
specifier|static
name|List
argument_list|<
name|DeleteObjectsRequest
operator|.
name|KeyVersion
argument_list|>
name|keysToDelete
parameter_list|(
name|List
argument_list|<
name|Path
argument_list|>
name|paths
parameter_list|)
block|{
return|return
name|paths
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
parameter_list|(
name|p
parameter_list|)
lambda|->
name|p
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|map
argument_list|(
name|DeleteObjectsRequest
operator|.
name|KeyVersion
operator|::
operator|new
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Verify that on a partial delete, the S3Guard tables are updated    * with deleted items. And only them.    */
annotation|@
name|Test
DECL|method|testProcessDeleteFailure ()
specifier|public
name|void
name|testProcessDeleteFailure
parameter_list|()
throws|throws
name|Throwable
block|{
name|Path
name|pathA
init|=
name|qualifyKey
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
name|Path
name|pathAB
init|=
name|qualifyKey
argument_list|(
literal|"/a/b"
argument_list|)
decl_stmt|;
name|Path
name|pathAC
init|=
name|qualifyKey
argument_list|(
literal|"/a/c"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|src
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|pathA
argument_list|,
name|pathAB
argument_list|,
name|pathAC
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DeleteObjectsRequest
operator|.
name|KeyVersion
argument_list|>
name|keyList
init|=
name|keysToDelete
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|deleteForbidden
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|pathAB
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|deleteAllowed
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|pathA
argument_list|,
name|pathAC
argument_list|)
decl_stmt|;
name|MultiObjectDeleteException
name|ex
init|=
name|createDeleteException
argument_list|(
name|ACCESS_DENIED
argument_list|,
name|deleteForbidden
argument_list|)
decl_stmt|;
name|OperationTrackingStore
name|store
init|=
operator|new
name|OperationTrackingStore
argument_list|()
decl_stmt|;
name|StoreContext
name|storeContext
init|=
name|createMockStoreContext
argument_list|(
literal|true
argument_list|,
name|store
argument_list|)
decl_stmt|;
name|MultiObjectDeleteSupport
name|deleteSupport
init|=
operator|new
name|MultiObjectDeleteSupport
argument_list|(
name|storeContext
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Triple
argument_list|<
name|List
argument_list|<
name|Path
argument_list|>
argument_list|,
name|List
argument_list|<
name|Path
argument_list|>
argument_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|Path
argument_list|,
name|IOException
argument_list|>
argument_list|>
argument_list|>
name|triple
init|=
name|deleteSupport
operator|.
name|processDeleteFailure
argument_list|(
name|ex
argument_list|,
name|keyList
argument_list|)
decl_stmt|;
name|Assertions
operator|.
name|assertThat
argument_list|(
name|triple
operator|.
name|getRight
argument_list|()
argument_list|)
operator|.
name|as
argument_list|(
literal|"failure list"
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|undeleted
init|=
name|triple
operator|.
name|getLeft
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|deleted
init|=
name|triple
operator|.
name|getMiddle
argument_list|()
decl_stmt|;
name|Assertions
operator|.
name|assertThat
argument_list|(
name|deleted
argument_list|)
operator|.
name|as
argument_list|(
literal|"deleted files"
argument_list|)
operator|.
name|containsAll
argument_list|(
name|deleteAllowed
argument_list|)
operator|.
name|doesNotContainAnyElementsOf
argument_list|(
name|deleteForbidden
argument_list|)
expr_stmt|;
name|Assertions
operator|.
name|assertThat
argument_list|(
name|undeleted
argument_list|)
operator|.
name|as
argument_list|(
literal|"undeleted store entries"
argument_list|)
operator|.
name|containsAll
argument_list|(
name|deleteForbidden
argument_list|)
operator|.
name|doesNotContainAnyElementsOf
argument_list|(
name|deleteAllowed
argument_list|)
expr_stmt|;
block|}
DECL|method|createMockStoreContext (boolean multiDelete, OperationTrackingStore store)
specifier|private
name|StoreContext
name|createMockStoreContext
parameter_list|(
name|boolean
name|multiDelete
parameter_list|,
name|OperationTrackingStore
name|store
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
name|URI
name|name
init|=
operator|new
name|URI
argument_list|(
literal|"s3a://bucket"
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
return|return
operator|new
name|StoreContext
argument_list|(
name|name
argument_list|,
literal|"bucket"
argument_list|,
name|conf
argument_list|,
literal|"alice"
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
name|BlockingThreadPoolExecutorService
operator|.
name|newInstance
argument_list|(
literal|4
argument_list|,
literal|4
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
literal|"s3a-transfer-shared"
argument_list|)
argument_list|,
name|Constants
operator|.
name|DEFAULT_EXECUTOR_CAPACITY
argument_list|,
operator|new
name|Invoker
argument_list|(
name|RetryPolicies
operator|.
name|TRY_ONCE_THEN_FAIL
argument_list|,
name|Invoker
operator|.
name|LOG_EVENT
argument_list|)
argument_list|,
operator|new
name|S3AInstrumentation
argument_list|(
name|name
argument_list|)
argument_list|,
operator|new
name|S3AStorageStatistics
argument_list|()
argument_list|,
name|S3AInputPolicy
operator|.
name|Normal
argument_list|,
name|ChangeDetectionPolicy
operator|.
name|createPolicy
argument_list|(
name|ChangeDetectionPolicy
operator|.
name|Mode
operator|.
name|None
argument_list|,
name|ChangeDetectionPolicy
operator|.
name|Source
operator|.
name|ETag
argument_list|,
literal|false
argument_list|)
argument_list|,
name|multiDelete
argument_list|,
name|store
argument_list|,
literal|false
argument_list|,
name|CONTEXT_ACCESSORS
argument_list|,
operator|new
name|S3Guard
operator|.
name|TtlTimeProvider
argument_list|(
name|conf
argument_list|)
argument_list|)
return|;
block|}
DECL|class|MinimalContextAccessor
specifier|private
specifier|static
class|class
name|MinimalContextAccessor
implements|implements
name|ContextAccessors
block|{
annotation|@
name|Override
DECL|method|keyToPath (final String key)
specifier|public
name|Path
name|keyToPath
parameter_list|(
specifier|final
name|String
name|key
parameter_list|)
block|{
return|return
name|qualifyKey
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|pathToKey (final Path path)
specifier|public
name|String
name|pathToKey
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|createTempFile (final String prefix, final long size)
specifier|public
name|File
name|createTempFile
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|,
specifier|final
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"unsppported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getBucketLocation ()
specifier|public
name|String
name|getBucketLocation
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**    * MetadataStore which tracks what is deleted and added.    */
DECL|class|OperationTrackingStore
specifier|private
specifier|static
class|class
name|OperationTrackingStore
implements|implements
name|MetadataStore
block|{
DECL|field|deleted
specifier|private
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|deleted
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|created
specifier|private
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|created
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|initialize (final FileSystem fs, ITtlTimeProvider ttlTimeProvider)
specifier|public
name|void
name|initialize
parameter_list|(
specifier|final
name|FileSystem
name|fs
parameter_list|,
name|ITtlTimeProvider
name|ttlTimeProvider
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|initialize (final Configuration conf, ITtlTimeProvider ttlTimeProvider)
specifier|public
name|void
name|initialize
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
name|ITtlTimeProvider
name|ttlTimeProvider
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|forgetMetadata (final Path path)
specifier|public
name|void
name|forgetMetadata
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|get (final Path path)
specifier|public
name|PathMetadata
name|get
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|get (final Path path, final boolean wantEmptyDirectoryFlag)
specifier|public
name|PathMetadata
name|get
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|,
specifier|final
name|boolean
name|wantEmptyDirectoryFlag
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|listChildren (final Path path)
specifier|public
name|DirListingMetadata
name|listChildren
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|put (final PathMetadata meta)
specifier|public
name|void
name|put
parameter_list|(
specifier|final
name|PathMetadata
name|meta
parameter_list|)
block|{
name|put
argument_list|(
name|meta
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|put (final PathMetadata meta, final BulkOperationState operationState)
specifier|public
name|void
name|put
parameter_list|(
specifier|final
name|PathMetadata
name|meta
parameter_list|,
specifier|final
name|BulkOperationState
name|operationState
parameter_list|)
block|{
name|created
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
annotation|@
name|Override
DECL|method|put (final Collection<? extends PathMetadata> metas, final BulkOperationState operationState)
specifier|public
name|void
name|put
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|?
extends|extends
name|PathMetadata
argument_list|>
name|metas
parameter_list|,
specifier|final
name|BulkOperationState
name|operationState
parameter_list|)
block|{
name|metas
operator|.
name|stream
argument_list|()
operator|.
name|forEach
argument_list|(
name|meta
lambda|->
name|put
argument_list|(
name|meta
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|put (final DirListingMetadata meta, final BulkOperationState operationState)
specifier|public
name|void
name|put
parameter_list|(
specifier|final
name|DirListingMetadata
name|meta
parameter_list|,
specifier|final
name|BulkOperationState
name|operationState
parameter_list|)
block|{
name|created
operator|.
name|add
argument_list|(
name|meta
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|delete (final Path path, final BulkOperationState operationState)
specifier|public
name|void
name|delete
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|,
specifier|final
name|BulkOperationState
name|operationState
parameter_list|)
block|{
name|deleted
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deletePaths (final Collection<Path> paths, @Nullable final BulkOperationState operationState)
specifier|public
name|void
name|deletePaths
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|Path
argument_list|>
name|paths
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|BulkOperationState
name|operationState
parameter_list|)
throws|throws
name|IOException
block|{
name|deleted
operator|.
name|addAll
argument_list|(
name|paths
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteSubtree (final Path path, final BulkOperationState operationState)
specifier|public
name|void
name|deleteSubtree
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|,
specifier|final
name|BulkOperationState
name|operationState
parameter_list|)
block|{      }
annotation|@
name|Override
DECL|method|move (@ullable final Collection<Path> pathsToDelete, @Nullable final Collection<PathMetadata> pathsToCreate, @Nullable final BulkOperationState operationState)
specifier|public
name|void
name|move
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|Collection
argument_list|<
name|Path
argument_list|>
name|pathsToDelete
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Collection
argument_list|<
name|PathMetadata
argument_list|>
name|pathsToCreate
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|BulkOperationState
name|operationState
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|prune (final PruneMode pruneMode, final long cutoff)
specifier|public
name|void
name|prune
parameter_list|(
specifier|final
name|PruneMode
name|pruneMode
parameter_list|,
specifier|final
name|long
name|cutoff
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|prune (final PruneMode pruneMode, final long cutoff, final String keyPrefix)
specifier|public
name|void
name|prune
parameter_list|(
specifier|final
name|PruneMode
name|pruneMode
parameter_list|,
specifier|final
name|long
name|cutoff
parameter_list|,
specifier|final
name|String
name|keyPrefix
parameter_list|)
block|{      }
annotation|@
name|Override
DECL|method|initiateBulkWrite ( final BulkOperationState.OperationType operation, final Path dest)
specifier|public
name|BulkOperationState
name|initiateBulkWrite
parameter_list|(
specifier|final
name|BulkOperationState
operator|.
name|OperationType
name|operation
parameter_list|,
specifier|final
name|Path
name|dest
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setTtlTimeProvider (ITtlTimeProvider ttlTimeProvider)
specifier|public
name|void
name|setTtlTimeProvider
parameter_list|(
name|ITtlTimeProvider
name|ttlTimeProvider
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|getDiagnostics ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getDiagnostics
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|updateParameters (final Map<String, String> parameters)
specifier|public
name|void
name|updateParameters
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{     }
DECL|method|getDeleted ()
specifier|public
name|List
argument_list|<
name|Path
argument_list|>
name|getDeleted
parameter_list|()
block|{
return|return
name|deleted
return|;
block|}
DECL|method|getCreated ()
specifier|public
name|List
argument_list|<
name|Path
argument_list|>
name|getCreated
parameter_list|()
block|{
return|return
name|created
return|;
block|}
annotation|@
name|Override
DECL|method|initiateRenameOperation ( final StoreContext storeContext, final Path source, final S3AFileStatus sourceStatus, final Path dest)
specifier|public
name|RenameTracker
name|initiateRenameOperation
parameter_list|(
specifier|final
name|StoreContext
name|storeContext
parameter_list|,
specifier|final
name|Path
name|source
parameter_list|,
specifier|final
name|S3AFileStatus
name|sourceStatus
parameter_list|,
specifier|final
name|Path
name|dest
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"unsupported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|addAncestors (final Path qualifiedPath, @Nullable final BulkOperationState operationState)
specifier|public
name|void
name|addAncestors
parameter_list|(
specifier|final
name|Path
name|qualifiedPath
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|BulkOperationState
name|operationState
parameter_list|)
block|{      }
block|}
block|}
end_class

end_unit

