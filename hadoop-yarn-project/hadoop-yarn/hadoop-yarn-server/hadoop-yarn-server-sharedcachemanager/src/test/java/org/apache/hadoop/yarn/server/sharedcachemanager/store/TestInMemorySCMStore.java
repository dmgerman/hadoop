begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.sharedcachemanager.store
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
operator|.
name|store
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
name|assertFalse
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
name|assertNull
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
name|assertSame
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
name|doReturn
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
name|spy
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
name|Collections
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
name|Set
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
name|Callable
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
name|CountDownLatch
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
name|ExecutorService
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
name|Executors
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
name|Future
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
operator|.
name|Private
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
name|exceptions
operator|.
name|YarnException
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
name|AppChecker
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

begin_class
DECL|class|TestInMemorySCMStore
specifier|public
class|class
name|TestInMemorySCMStore
block|{
DECL|field|store
specifier|private
name|InMemorySCMStore
name|store
decl_stmt|;
DECL|field|checker
specifier|private
name|AppChecker
name|checker
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|this
operator|.
name|store
operator|=
name|spy
argument_list|(
operator|new
name|InMemorySCMStore
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|checker
operator|=
name|spy
argument_list|(
operator|new
name|DummyAppChecker
argument_list|()
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
name|checker
argument_list|)
operator|.
name|when
argument_list|(
name|store
argument_list|)
operator|.
name|createAppCheckerService
argument_list|(
name|isA
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|store
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|startEmptyStore ()
specifier|private
name|void
name|startEmptyStore
parameter_list|()
throws|throws
name|Exception
block|{
name|doReturn
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ApplicationId
argument_list|>
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|checker
argument_list|)
operator|.
name|getActiveApplications
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|store
argument_list|)
operator|.
name|getInitialCachedResources
argument_list|(
name|isA
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
argument_list|,
name|isA
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|startStoreWithResources ()
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|startStoreWithResources
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|initialCachedResources
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|10
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|String
name|key
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|fileName
init|=
name|key
operator|+
literal|".jar"
decl_stmt|;
name|initialCachedResources
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
name|doReturn
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ApplicationId
argument_list|>
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|checker
argument_list|)
operator|.
name|getActiveApplications
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|initialCachedResources
argument_list|)
operator|.
name|when
argument_list|(
name|store
argument_list|)
operator|.
name|getInitialCachedResources
argument_list|(
name|isA
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
argument_list|,
name|isA
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|initialCachedResources
return|;
block|}
DECL|method|startStoreWithApps ()
specifier|private
name|void
name|startStoreWithApps
parameter_list|()
throws|throws
name|Exception
block|{
name|ArrayList
argument_list|<
name|ApplicationId
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|ApplicationId
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|5
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|createAppId
argument_list|(
name|i
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|doReturn
argument_list|(
name|list
argument_list|)
operator|.
name|when
argument_list|(
name|checker
argument_list|)
operator|.
name|getActiveApplications
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|store
argument_list|)
operator|.
name|getInitialCachedResources
argument_list|(
name|isA
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
argument_list|,
name|isA
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddResourceConcurrency ()
specifier|public
name|void
name|testAddResourceConcurrency
parameter_list|()
throws|throws
name|Exception
block|{
name|startEmptyStore
argument_list|()
expr_stmt|;
specifier|final
name|String
name|key
init|=
literal|"key1"
decl_stmt|;
name|int
name|count
init|=
literal|5
decl_stmt|;
name|ExecutorService
name|exec
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|count
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|String
argument_list|>
argument_list|>
name|futures
init|=
operator|new
name|ArrayList
argument_list|<
name|Future
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|(
name|count
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|start
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|fileName
init|=
literal|"foo-"
operator|+
name|i
operator|+
literal|".jar"
decl_stmt|;
name|Callable
argument_list|<
name|String
argument_list|>
name|task
init|=
operator|new
name|Callable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|start
operator|.
name|await
argument_list|()
expr_stmt|;
name|String
name|result
init|=
name|store
operator|.
name|addResource
argument_list|(
name|key
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"fileName: "
operator|+
name|fileName
operator|+
literal|", result: "
operator|+
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
decl_stmt|;
name|futures
operator|.
name|add
argument_list|(
name|exec
operator|.
name|submit
argument_list|(
name|task
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// start them all at the same time
name|start
operator|.
name|countDown
argument_list|()
expr_stmt|;
comment|// check the result; they should all agree with the value
name|Set
argument_list|<
name|String
argument_list|>
name|results
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Future
argument_list|<
name|String
argument_list|>
name|future
range|:
name|futures
control|)
block|{
name|results
operator|.
name|add
argument_list|(
name|future
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertSame
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|exec
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddResourceRefNonExistentResource ()
specifier|public
name|void
name|testAddResourceRefNonExistentResource
parameter_list|()
throws|throws
name|Exception
block|{
name|startEmptyStore
argument_list|()
expr_stmt|;
name|String
name|key
init|=
literal|"key1"
decl_stmt|;
name|ApplicationId
name|id
init|=
name|createAppId
argument_list|(
literal|1
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
comment|// try adding an app id without adding the key first
name|assertNull
argument_list|(
name|store
operator|.
name|addResourceReference
argument_list|(
name|key
argument_list|,
operator|new
name|SharedCacheResourceReference
argument_list|(
name|id
argument_list|,
literal|"user"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveResourceEmptyRefs ()
specifier|public
name|void
name|testRemoveResourceEmptyRefs
parameter_list|()
throws|throws
name|Exception
block|{
name|startEmptyStore
argument_list|()
expr_stmt|;
name|String
name|key
init|=
literal|"key1"
decl_stmt|;
name|String
name|fileName
init|=
literal|"foo.jar"
decl_stmt|;
comment|// first add resource
name|store
operator|.
name|addResource
argument_list|(
name|key
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
comment|// try removing the resource; it should return true
name|assertTrue
argument_list|(
name|store
operator|.
name|removeResource
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddResourceRefRemoveResource ()
specifier|public
name|void
name|testAddResourceRefRemoveResource
parameter_list|()
throws|throws
name|Exception
block|{
name|startEmptyStore
argument_list|()
expr_stmt|;
name|String
name|key
init|=
literal|"key1"
decl_stmt|;
name|ApplicationId
name|id
init|=
name|createAppId
argument_list|(
literal|1
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
name|String
name|user
init|=
literal|"user"
decl_stmt|;
comment|// add the resource, and then add a resource ref
name|store
operator|.
name|addResource
argument_list|(
name|key
argument_list|,
literal|"foo.jar"
argument_list|)
expr_stmt|;
name|store
operator|.
name|addResourceReference
argument_list|(
name|key
argument_list|,
operator|new
name|SharedCacheResourceReference
argument_list|(
name|id
argument_list|,
name|user
argument_list|)
argument_list|)
expr_stmt|;
comment|// removeResource should return false
name|assertTrue
argument_list|(
operator|!
name|store
operator|.
name|removeResource
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
comment|// the resource and the ref should be intact
name|Collection
argument_list|<
name|SharedCacheResourceReference
argument_list|>
name|refs
init|=
name|store
operator|.
name|getResourceReferences
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|refs
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|SharedCacheResourceReference
argument_list|(
name|id
argument_list|,
name|user
argument_list|)
argument_list|)
argument_list|,
name|refs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddResourceRefConcurrency ()
specifier|public
name|void
name|testAddResourceRefConcurrency
parameter_list|()
throws|throws
name|Exception
block|{
name|startEmptyStore
argument_list|()
expr_stmt|;
specifier|final
name|String
name|key
init|=
literal|"key1"
decl_stmt|;
specifier|final
name|String
name|user
init|=
literal|"user"
decl_stmt|;
name|String
name|fileName
init|=
literal|"foo.jar"
decl_stmt|;
comment|// first add the resource
name|store
operator|.
name|addResource
argument_list|(
name|key
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
comment|// make concurrent addResourceRef calls (clients)
name|int
name|count
init|=
literal|5
decl_stmt|;
name|ExecutorService
name|exec
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|count
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|String
argument_list|>
argument_list|>
name|futures
init|=
operator|new
name|ArrayList
argument_list|<
name|Future
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|(
name|count
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|start
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|ApplicationId
name|id
init|=
name|createAppId
argument_list|(
name|i
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|Callable
argument_list|<
name|String
argument_list|>
name|task
init|=
operator|new
name|Callable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|start
operator|.
name|await
argument_list|()
expr_stmt|;
return|return
name|store
operator|.
name|addResourceReference
argument_list|(
name|key
argument_list|,
operator|new
name|SharedCacheResourceReference
argument_list|(
name|id
argument_list|,
name|user
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|futures
operator|.
name|add
argument_list|(
name|exec
operator|.
name|submit
argument_list|(
name|task
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// start them all at the same time
name|start
operator|.
name|countDown
argument_list|()
expr_stmt|;
comment|// check the result
name|Set
argument_list|<
name|String
argument_list|>
name|results
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Future
argument_list|<
name|String
argument_list|>
name|future
range|:
name|futures
control|)
block|{
name|results
operator|.
name|add
argument_list|(
name|future
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// they should all have the same file name
name|assertSame
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|fileName
argument_list|)
argument_list|,
name|results
argument_list|)
expr_stmt|;
comment|// there should be 5 refs as a result
name|Collection
argument_list|<
name|SharedCacheResourceReference
argument_list|>
name|refs
init|=
name|store
operator|.
name|getResourceReferences
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|count
argument_list|,
name|refs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|exec
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddResourceRefAddResourceConcurrency ()
specifier|public
name|void
name|testAddResourceRefAddResourceConcurrency
parameter_list|()
throws|throws
name|Exception
block|{
name|startEmptyStore
argument_list|()
expr_stmt|;
specifier|final
name|String
name|key
init|=
literal|"key1"
decl_stmt|;
specifier|final
name|String
name|fileName
init|=
literal|"foo.jar"
decl_stmt|;
specifier|final
name|String
name|user
init|=
literal|"user"
decl_stmt|;
specifier|final
name|ApplicationId
name|id
init|=
name|createAppId
argument_list|(
literal|1
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
comment|// add the resource and add the resource ref at the same time
name|ExecutorService
name|exec
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|start
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Callable
argument_list|<
name|String
argument_list|>
name|addKeyTask
init|=
operator|new
name|Callable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|start
operator|.
name|await
argument_list|()
expr_stmt|;
return|return
name|store
operator|.
name|addResource
argument_list|(
name|key
argument_list|,
name|fileName
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|Callable
argument_list|<
name|String
argument_list|>
name|addAppIdTask
init|=
operator|new
name|Callable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|start
operator|.
name|await
argument_list|()
expr_stmt|;
return|return
name|store
operator|.
name|addResourceReference
argument_list|(
name|key
argument_list|,
operator|new
name|SharedCacheResourceReference
argument_list|(
name|id
argument_list|,
name|user
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|Future
argument_list|<
name|String
argument_list|>
name|addAppIdFuture
init|=
name|exec
operator|.
name|submit
argument_list|(
name|addAppIdTask
argument_list|)
decl_stmt|;
name|Future
argument_list|<
name|String
argument_list|>
name|addKeyFuture
init|=
name|exec
operator|.
name|submit
argument_list|(
name|addKeyTask
argument_list|)
decl_stmt|;
comment|// start them at the same time
name|start
operator|.
name|countDown
argument_list|()
expr_stmt|;
comment|// get the results
name|String
name|addKeyResult
init|=
name|addKeyFuture
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|addAppIdResult
init|=
name|addAppIdFuture
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|fileName
argument_list|,
name|addKeyResult
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"addAppId() result: "
operator|+
name|addAppIdResult
argument_list|)
expr_stmt|;
comment|// it may be null or the fileName depending on the timing
name|assertTrue
argument_list|(
name|addAppIdResult
operator|==
literal|null
operator|||
name|addAppIdResult
operator|.
name|equals
argument_list|(
name|fileName
argument_list|)
argument_list|)
expr_stmt|;
name|exec
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveRef ()
specifier|public
name|void
name|testRemoveRef
parameter_list|()
throws|throws
name|Exception
block|{
name|startEmptyStore
argument_list|()
expr_stmt|;
name|String
name|key
init|=
literal|"key1"
decl_stmt|;
name|String
name|fileName
init|=
literal|"foo.jar"
decl_stmt|;
name|String
name|user
init|=
literal|"user"
decl_stmt|;
comment|// first add the resource
name|store
operator|.
name|addResource
argument_list|(
name|key
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
comment|// add a ref
name|ApplicationId
name|id
init|=
name|createAppId
argument_list|(
literal|1
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
name|SharedCacheResourceReference
name|myRef
init|=
operator|new
name|SharedCacheResourceReference
argument_list|(
name|id
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|String
name|result
init|=
name|store
operator|.
name|addResourceReference
argument_list|(
name|key
argument_list|,
name|myRef
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|fileName
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|SharedCacheResourceReference
argument_list|>
name|refs
init|=
name|store
operator|.
name|getResourceReferences
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|1
argument_list|,
name|refs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|myRef
argument_list|)
argument_list|,
name|refs
argument_list|)
expr_stmt|;
comment|// remove the same ref
name|store
operator|.
name|removeResourceReferences
argument_list|(
name|key
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|myRef
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|SharedCacheResourceReference
argument_list|>
name|newRefs
init|=
name|store
operator|.
name|getResourceReferences
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|newRefs
operator|==
literal|null
operator|||
name|newRefs
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBootstrapping ()
specifier|public
name|void
name|testBootstrapping
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|initialCachedResources
init|=
name|startStoreWithResources
argument_list|()
decl_stmt|;
name|int
name|count
init|=
name|initialCachedResources
operator|.
name|size
argument_list|()
decl_stmt|;
name|ApplicationId
name|id
init|=
name|createAppId
argument_list|(
literal|1
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
comment|// the entries from the cached entries should now exist
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|String
name|key
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|fileName
init|=
name|key
operator|+
literal|".jar"
decl_stmt|;
name|String
name|result
init|=
name|store
operator|.
name|addResourceReference
argument_list|(
name|key
argument_list|,
operator|new
name|SharedCacheResourceReference
argument_list|(
name|id
argument_list|,
literal|"user"
argument_list|)
argument_list|)
decl_stmt|;
comment|// the value should not be null (i.e. it has the key) and the filename should match
name|assertEquals
argument_list|(
name|fileName
argument_list|,
name|result
argument_list|)
expr_stmt|;
comment|// the initial input should be emptied
name|assertTrue
argument_list|(
name|initialCachedResources
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testEvictableWithInitialApps ()
specifier|public
name|void
name|testEvictableWithInitialApps
parameter_list|()
throws|throws
name|Exception
block|{
name|startStoreWithApps
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|store
operator|.
name|isResourceEvictable
argument_list|(
literal|"key"
argument_list|,
name|mock
argument_list|(
name|FileStatus
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createAppId (int id, long timestamp)
specifier|private
name|ApplicationId
name|createAppId
parameter_list|(
name|int
name|id
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
return|return
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|timestamp
argument_list|,
name|id
argument_list|)
return|;
block|}
DECL|class|DummyAppChecker
class|class
name|DummyAppChecker
extends|extends
name|AppChecker
block|{
annotation|@
name|Override
annotation|@
name|Private
DECL|method|isApplicationActive (ApplicationId id)
specifier|public
name|boolean
name|isApplicationActive
parameter_list|(
name|ApplicationId
name|id
parameter_list|)
throws|throws
name|YarnException
block|{
comment|// stub
return|return
literal|false
return|;
block|}
annotation|@
name|Override
annotation|@
name|Private
DECL|method|getActiveApplications ()
specifier|public
name|Collection
argument_list|<
name|ApplicationId
argument_list|>
name|getActiveApplications
parameter_list|()
throws|throws
name|YarnException
block|{
comment|// stub
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

