begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|ConcurrentMap
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
name|LocalResourceType
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
name|LocalResourceVisibility
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
name|nodemanager
operator|.
name|DeletionService
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
name|nodemanager
operator|.
name|recovery
operator|.
name|NMNullStateStoreService
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentCaptor
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

begin_class
DECL|class|TestResourceRetention
specifier|public
class|class
name|TestResourceRetention
block|{
annotation|@
name|Test
DECL|method|testRsrcUnused ()
specifier|public
name|void
name|testRsrcUnused
parameter_list|()
block|{
name|DeletionService
name|delService
init|=
name|mock
argument_list|(
name|DeletionService
operator|.
name|class
argument_list|)
decl_stmt|;
name|long
name|TARGET_MB
init|=
literal|10
operator|<<
literal|20
decl_stmt|;
name|ResourceRetentionSet
name|rss
init|=
operator|new
name|ResourceRetentionSet
argument_list|(
name|delService
argument_list|,
name|TARGET_MB
argument_list|)
decl_stmt|;
comment|// 3MB files @{10, 15}
name|LocalResourcesTracker
name|pubTracker
init|=
name|createMockTracker
argument_list|(
literal|null
argument_list|,
literal|3
operator|*
literal|1024
operator|*
literal|1024
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|,
literal|5
argument_list|)
decl_stmt|;
comment|// 1MB files @{3, 6, 9, 12}
name|LocalResourcesTracker
name|trackerA
init|=
name|createMockTracker
argument_list|(
literal|"A"
argument_list|,
literal|1
operator|*
literal|1024
operator|*
literal|1024
argument_list|,
literal|4
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|)
decl_stmt|;
comment|// 4MB file @{1}
name|LocalResourcesTracker
name|trackerB
init|=
name|createMockTracker
argument_list|(
literal|"B"
argument_list|,
literal|4
operator|*
literal|1024
operator|*
literal|1024
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|,
literal|5
argument_list|)
decl_stmt|;
comment|// 2MB files @{7, 9, 11}
name|LocalResourcesTracker
name|trackerC
init|=
name|createMockTracker
argument_list|(
literal|"C"
argument_list|,
literal|2
operator|*
literal|1024
operator|*
literal|1024
argument_list|,
literal|3
argument_list|,
literal|7
argument_list|,
literal|2
argument_list|)
decl_stmt|;
comment|// Total cache: 20MB; verify removed at least 10MB
name|rss
operator|.
name|addResources
argument_list|(
name|pubTracker
argument_list|)
expr_stmt|;
name|rss
operator|.
name|addResources
argument_list|(
name|trackerA
argument_list|)
expr_stmt|;
name|rss
operator|.
name|addResources
argument_list|(
name|trackerB
argument_list|)
expr_stmt|;
name|rss
operator|.
name|addResources
argument_list|(
name|trackerC
argument_list|)
expr_stmt|;
name|long
name|deleted
init|=
literal|0L
decl_stmt|;
name|ArgumentCaptor
argument_list|<
name|LocalizedResource
argument_list|>
name|captor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|LocalizedResource
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|pubTracker
argument_list|,
name|atMost
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|remove
argument_list|(
name|captor
operator|.
name|capture
argument_list|()
argument_list|,
name|isA
argument_list|(
name|DeletionService
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|trackerA
argument_list|,
name|atMost
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|remove
argument_list|(
name|captor
operator|.
name|capture
argument_list|()
argument_list|,
name|isA
argument_list|(
name|DeletionService
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|trackerB
argument_list|,
name|atMost
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|remove
argument_list|(
name|captor
operator|.
name|capture
argument_list|()
argument_list|,
name|isA
argument_list|(
name|DeletionService
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|trackerC
argument_list|,
name|atMost
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|remove
argument_list|(
name|captor
operator|.
name|capture
argument_list|()
argument_list|,
name|isA
argument_list|(
name|DeletionService
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|LocalizedResource
name|rem
range|:
name|captor
operator|.
name|getAllValues
argument_list|()
control|)
block|{
name|deleted
operator|+=
name|rem
operator|.
name|getSize
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|deleted
operator|>=
literal|10
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|deleted
operator|<
literal|15
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
DECL|method|createMockTracker (String user, final long rsrcSize, long nRsrcs, long timestamp, long tsstep)
name|LocalResourcesTracker
name|createMockTracker
parameter_list|(
name|String
name|user
parameter_list|,
specifier|final
name|long
name|rsrcSize
parameter_list|,
name|long
name|nRsrcs
parameter_list|,
name|long
name|timestamp
parameter_list|,
name|long
name|tsstep
parameter_list|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|ConcurrentMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|trackerResources
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
argument_list|()
decl_stmt|;
name|LocalResourcesTracker
name|ret
init|=
name|spy
argument_list|(
operator|new
name|LocalResourcesTrackerImpl
argument_list|(
name|user
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|trackerResources
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|,
operator|new
name|NMNullStateStoreService
argument_list|()
argument_list|,
literal|null
argument_list|)
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
name|nRsrcs
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|LocalResourceRequest
name|req
init|=
operator|new
name|LocalResourceRequest
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///"
operator|+
name|user
operator|+
literal|"/rsrc"
operator|+
name|i
argument_list|)
argument_list|,
name|timestamp
operator|+
name|i
operator|*
name|tsstep
argument_list|,
name|LocalResourceType
operator|.
name|FILE
argument_list|,
name|LocalResourceVisibility
operator|.
name|PUBLIC
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|long
name|ts
init|=
name|timestamp
operator|+
name|i
operator|*
name|tsstep
decl_stmt|;
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"file:///local/"
operator|+
name|user
operator|+
literal|"/rsrc"
operator|+
name|i
argument_list|)
decl_stmt|;
name|LocalizedResource
name|rsrc
init|=
operator|new
name|LocalizedResource
argument_list|(
name|req
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|getRefCount
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|rsrcSize
return|;
block|}
annotation|@
name|Override
specifier|public
name|Path
name|getLocalPath
parameter_list|()
block|{
return|return
name|p
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|ts
return|;
block|}
annotation|@
name|Override
specifier|public
name|ResourceState
name|getState
parameter_list|()
block|{
return|return
name|ResourceState
operator|.
name|LOCALIZED
return|;
block|}
block|}
decl_stmt|;
name|trackerResources
operator|.
name|put
argument_list|(
name|req
argument_list|,
name|rsrc
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
end_class

end_unit

