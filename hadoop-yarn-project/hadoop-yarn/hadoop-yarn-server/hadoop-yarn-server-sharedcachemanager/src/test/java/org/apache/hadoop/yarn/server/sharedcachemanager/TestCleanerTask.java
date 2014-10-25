begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.sharedcachemanager
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|sharedcachemanager
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyBoolean
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
name|eq
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
name|isA
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
name|never
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
name|spy
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
name|verify
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReentrantLock
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|server
operator|.
name|sharedcachemanager
operator|.
name|metrics
operator|.
name|CleanerMetrics
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
name|yarn
operator|.
name|server
operator|.
name|sharedcachemanager
operator|.
name|store
operator|.
name|SCMStore
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
DECL|class|TestCleanerTask
specifier|public
class|class
name|TestCleanerTask
block|{
DECL|field|ROOT
specifier|private
specifier|static
specifier|final
name|String
name|ROOT
init|=
name|YarnConfiguration
operator|.
name|DEFAULT_SHARED_CACHE_ROOT
decl_stmt|;
DECL|field|SLEEP_TIME
specifier|private
specifier|static
specifier|final
name|long
name|SLEEP_TIME
init|=
name|YarnConfiguration
operator|.
name|DEFAULT_SCM_CLEANER_RESOURCE_SLEEP_MS
decl_stmt|;
DECL|field|NESTED_LEVEL
specifier|private
specifier|static
specifier|final
name|int
name|NESTED_LEVEL
init|=
name|YarnConfiguration
operator|.
name|DEFAULT_SHARED_CACHE_NESTED_LEVEL
decl_stmt|;
annotation|@
name|Test
DECL|method|testNonExistentRoot ()
specifier|public
name|void
name|testNonExistentRoot
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|CleanerMetrics
name|metrics
init|=
name|mock
argument_list|(
name|CleanerMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
name|SCMStore
name|store
init|=
name|mock
argument_list|(
name|SCMStore
operator|.
name|class
argument_list|)
decl_stmt|;
name|CleanerTask
name|task
init|=
name|createSpiedTask
argument_list|(
name|fs
argument_list|,
name|store
argument_list|,
name|metrics
argument_list|,
operator|new
name|ReentrantLock
argument_list|()
argument_list|)
decl_stmt|;
comment|// the shared cache root does not exist
name|when
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|task
operator|.
name|getRootPath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|task
operator|.
name|run
argument_list|()
expr_stmt|;
comment|// process() should not be called
name|verify
argument_list|(
name|task
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|process
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProcessFreshResource ()
specifier|public
name|void
name|testProcessFreshResource
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|CleanerMetrics
name|metrics
init|=
name|mock
argument_list|(
name|CleanerMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
name|SCMStore
name|store
init|=
name|mock
argument_list|(
name|SCMStore
operator|.
name|class
argument_list|)
decl_stmt|;
name|CleanerTask
name|task
init|=
name|createSpiedTask
argument_list|(
name|fs
argument_list|,
name|store
argument_list|,
name|metrics
argument_list|,
operator|new
name|ReentrantLock
argument_list|()
argument_list|)
decl_stmt|;
comment|// mock a resource that is not evictable
name|when
argument_list|(
name|store
operator|.
name|isResourceEvictable
argument_list|(
name|isA
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|isA
argument_list|(
name|FileStatus
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|FileStatus
name|status
init|=
name|mock
argument_list|(
name|FileStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|status
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Path
argument_list|(
name|ROOT
operator|+
literal|"/a/b/c/abc"
argument_list|)
argument_list|)
expr_stmt|;
comment|// process the resource
name|task
operator|.
name|processSingleResource
argument_list|(
name|status
argument_list|)
expr_stmt|;
comment|// the directory should not be renamed
name|verify
argument_list|(
name|fs
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|rename
argument_list|(
name|eq
argument_list|(
name|status
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
name|isA
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// metrics should record a processed file (but not delete)
name|verify
argument_list|(
name|metrics
argument_list|)
operator|.
name|reportAFileProcess
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|metrics
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|reportAFileDelete
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProcessEvictableResource ()
specifier|public
name|void
name|testProcessEvictableResource
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|CleanerMetrics
name|metrics
init|=
name|mock
argument_list|(
name|CleanerMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
name|SCMStore
name|store
init|=
name|mock
argument_list|(
name|SCMStore
operator|.
name|class
argument_list|)
decl_stmt|;
name|CleanerTask
name|task
init|=
name|createSpiedTask
argument_list|(
name|fs
argument_list|,
name|store
argument_list|,
name|metrics
argument_list|,
operator|new
name|ReentrantLock
argument_list|()
argument_list|)
decl_stmt|;
comment|// mock an evictable resource
name|when
argument_list|(
name|store
operator|.
name|isResourceEvictable
argument_list|(
name|isA
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|isA
argument_list|(
name|FileStatus
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FileStatus
name|status
init|=
name|mock
argument_list|(
name|FileStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|status
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Path
argument_list|(
name|ROOT
operator|+
literal|"/a/b/c/abc"
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|store
operator|.
name|removeResource
argument_list|(
name|isA
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// rename succeeds
name|when
argument_list|(
name|fs
operator|.
name|rename
argument_list|(
name|isA
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|isA
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// delete returns true
name|when
argument_list|(
name|fs
operator|.
name|delete
argument_list|(
name|isA
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// process the resource
name|task
operator|.
name|processSingleResource
argument_list|(
name|status
argument_list|)
expr_stmt|;
comment|// the directory should be renamed
name|verify
argument_list|(
name|fs
argument_list|)
operator|.
name|rename
argument_list|(
name|eq
argument_list|(
name|status
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
name|isA
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// metrics should record a deleted file
name|verify
argument_list|(
name|metrics
argument_list|)
operator|.
name|reportAFileDelete
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|metrics
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|reportAFileProcess
argument_list|()
expr_stmt|;
block|}
DECL|method|createSpiedTask (FileSystem fs, SCMStore store, CleanerMetrics metrics, Lock isCleanerRunning)
specifier|private
name|CleanerTask
name|createSpiedTask
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|SCMStore
name|store
parameter_list|,
name|CleanerMetrics
name|metrics
parameter_list|,
name|Lock
name|isCleanerRunning
parameter_list|)
block|{
return|return
name|spy
argument_list|(
operator|new
name|CleanerTask
argument_list|(
name|ROOT
argument_list|,
name|SLEEP_TIME
argument_list|,
name|NESTED_LEVEL
argument_list|,
name|fs
argument_list|,
name|store
argument_list|,
name|metrics
argument_list|,
name|isCleanerRunning
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testResourceIsInUseHasAnActiveApp ()
specifier|public
name|void
name|testResourceIsInUseHasAnActiveApp
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|CleanerMetrics
name|metrics
init|=
name|mock
argument_list|(
name|CleanerMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
name|SCMStore
name|store
init|=
name|mock
argument_list|(
name|SCMStore
operator|.
name|class
argument_list|)
decl_stmt|;
name|FileStatus
name|resource
init|=
name|mock
argument_list|(
name|FileStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|resource
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Path
argument_list|(
name|ROOT
operator|+
literal|"/a/b/c/abc"
argument_list|)
argument_list|)
expr_stmt|;
comment|// resource is stale
name|when
argument_list|(
name|store
operator|.
name|isResourceEvictable
argument_list|(
name|isA
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|isA
argument_list|(
name|FileStatus
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// but still has appIds
name|when
argument_list|(
name|store
operator|.
name|removeResource
argument_list|(
name|isA
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|CleanerTask
name|task
init|=
name|createSpiedTask
argument_list|(
name|fs
argument_list|,
name|store
argument_list|,
name|metrics
argument_list|,
operator|new
name|ReentrantLock
argument_list|()
argument_list|)
decl_stmt|;
comment|// process the resource
name|task
operator|.
name|processSingleResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
comment|// metrics should record a processed file (but not delete)
name|verify
argument_list|(
name|metrics
argument_list|)
operator|.
name|reportAFileProcess
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|metrics
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|reportAFileDelete
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

