begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|assertTrue
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
name|Context
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
name|containermanager
operator|.
name|localizer
operator|.
name|LocalCacheCleaner
operator|.
name|LocalCacheCleanerStats
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
name|metrics
operator|.
name|NodeManagerMetrics
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
comment|/**  * This class tests the clean up of local caches the node manager uses for the  * purpose of resource localization.  */
end_comment

begin_class
DECL|class|TestLocalCacheCleanup
specifier|public
class|class
name|TestLocalCacheCleanup
block|{
annotation|@
name|Test
DECL|method|testBasicCleanup ()
specifier|public
name|void
name|testBasicCleanup
parameter_list|()
block|{
name|ConcurrentMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|publicRsrc
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
name|addResource
argument_list|(
name|publicRsrc
argument_list|,
literal|"/pub-resource1.txt"
argument_list|,
literal|5
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|addResource
argument_list|(
name|publicRsrc
argument_list|,
literal|"/pub-resource2.txt"
argument_list|,
literal|3
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|addResource
argument_list|(
name|publicRsrc
argument_list|,
literal|"/pub-resource3.txt"
argument_list|,
literal|15
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|LocalResourcesTracker
argument_list|>
name|privateRsrc
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|LocalResourcesTracker
argument_list|>
argument_list|()
decl_stmt|;
name|ConcurrentMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|user1rsrcs
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
name|addResource
argument_list|(
name|user1rsrcs
argument_list|,
literal|"/private-u1-resource4.txt"
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|LocalResourcesTracker
name|user1Tracker
init|=
operator|new
name|StubbedLocalResourcesTrackerImpl
argument_list|(
literal|"user1"
argument_list|,
name|user1rsrcs
argument_list|)
decl_stmt|;
name|privateRsrc
operator|.
name|put
argument_list|(
literal|"user1"
argument_list|,
name|user1Tracker
argument_list|)
expr_stmt|;
name|ConcurrentMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|user2rsrcs
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
name|addResource
argument_list|(
name|user2rsrcs
argument_list|,
literal|"/private-u2-resource5.txt"
argument_list|,
literal|2
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|LocalResourcesTracker
name|user2Tracker
init|=
operator|new
name|StubbedLocalResourcesTrackerImpl
argument_list|(
literal|"user2"
argument_list|,
name|user2rsrcs
argument_list|)
decl_stmt|;
name|privateRsrc
operator|.
name|put
argument_list|(
literal|"user2"
argument_list|,
name|user2Tracker
argument_list|)
expr_stmt|;
name|ResourceLocalizationService
name|rls
init|=
name|createLocService
argument_list|(
name|publicRsrc
argument_list|,
name|privateRsrc
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|LocalCacheCleanerStats
name|stats
init|=
name|rls
operator|.
name|handleCacheCleanup
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
operator|(
name|StubbedLocalResourcesTrackerImpl
operator|)
name|rls
operator|.
name|publicRsrc
operator|)
operator|.
name|getLocalRsrc
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
operator|(
name|StubbedLocalResourcesTrackerImpl
operator|)
name|privateRsrc
operator|.
name|get
argument_list|(
literal|"user1"
argument_list|)
operator|)
operator|.
name|getLocalRsrc
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
operator|(
name|StubbedLocalResourcesTrackerImpl
operator|)
name|privateRsrc
operator|.
name|get
argument_list|(
literal|"user2"
argument_list|)
operator|)
operator|.
name|getLocalRsrc
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|stats
operator|.
name|getTotalDelSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|rls
operator|.
name|metrics
operator|.
name|getTotalBytesDeleted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|60
argument_list|,
name|stats
operator|.
name|getPublicDelSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|60
argument_list|,
name|rls
operator|.
name|metrics
operator|.
name|getPublicBytesDeleted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|40
argument_list|,
name|stats
operator|.
name|getPrivateDelSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|40
argument_list|,
name|rls
operator|.
name|metrics
operator|.
name|getPrivateBytesDeleted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|rls
operator|.
name|metrics
operator|.
name|getCacheSizeBeforeClean
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPositiveRefCount ()
specifier|public
name|void
name|testPositiveRefCount
parameter_list|()
block|{
name|ConcurrentMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|publicRsrc
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
comment|// Oldest resource with a positive ref count the other with a ref count
comment|// equal to 0.
name|LocalResourceRequest
name|survivor
init|=
name|addResource
argument_list|(
name|publicRsrc
argument_list|,
literal|"/pub-resource1.txt"
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|addResource
argument_list|(
name|publicRsrc
argument_list|,
literal|"/pub-resource2.txt"
argument_list|,
literal|5
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|LocalResourcesTracker
argument_list|>
name|privateRsrc
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|LocalResourcesTracker
argument_list|>
argument_list|()
decl_stmt|;
name|ResourceLocalizationService
name|rls
init|=
name|createLocService
argument_list|(
name|publicRsrc
argument_list|,
name|privateRsrc
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|LocalCacheCleanerStats
name|stats
init|=
name|rls
operator|.
name|handleCacheCleanup
argument_list|()
decl_stmt|;
name|StubbedLocalResourcesTrackerImpl
name|resources
init|=
operator|(
name|StubbedLocalResourcesTrackerImpl
operator|)
name|rls
operator|.
name|publicRsrc
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|resources
operator|.
name|getLocalRsrc
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|resources
operator|.
name|getLocalRsrc
argument_list|()
operator|.
name|containsKey
argument_list|(
name|survivor
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|stats
operator|.
name|getTotalDelSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|rls
operator|.
name|metrics
operator|.
name|getTotalBytesDeleted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|stats
operator|.
name|getPublicDelSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|rls
operator|.
name|metrics
operator|.
name|getPublicBytesDeleted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|getPrivateDelSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rls
operator|.
name|metrics
operator|.
name|getPrivateBytesDeleted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|40
argument_list|,
name|rls
operator|.
name|metrics
operator|.
name|getCacheSizeBeforeClean
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLRUAcrossTrackers ()
specifier|public
name|void
name|testLRUAcrossTrackers
parameter_list|()
block|{
name|ConcurrentMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|publicRsrc
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
name|LocalResourceRequest
name|pubSurviver1
init|=
name|addResource
argument_list|(
name|publicRsrc
argument_list|,
literal|"/pub-resource1.txt"
argument_list|,
literal|8
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|LocalResourceRequest
name|pubSurviver2
init|=
name|addResource
argument_list|(
name|publicRsrc
argument_list|,
literal|"/pub-resource2.txt"
argument_list|,
literal|7
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|addResource
argument_list|(
name|publicRsrc
argument_list|,
literal|"/pub-resource3.txt"
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|LocalResourcesTracker
argument_list|>
name|privateRsrc
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|LocalResourcesTracker
argument_list|>
argument_list|()
decl_stmt|;
name|ConcurrentMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|user1rsrcs
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
name|LocalResourceRequest
name|usr1Surviver1
init|=
name|addResource
argument_list|(
name|user1rsrcs
argument_list|,
literal|"/private-u1-resource1.txt"
argument_list|,
literal|6
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|addResource
argument_list|(
name|user1rsrcs
argument_list|,
literal|"/private-u1-resource2.txt"
argument_list|,
literal|2
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|LocalResourcesTracker
name|user1Tracker
init|=
operator|new
name|StubbedLocalResourcesTrackerImpl
argument_list|(
literal|"user1"
argument_list|,
name|user1rsrcs
argument_list|)
decl_stmt|;
name|privateRsrc
operator|.
name|put
argument_list|(
literal|"user1"
argument_list|,
name|user1Tracker
argument_list|)
expr_stmt|;
name|ConcurrentMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|user2rsrcs
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
name|LocalResourceRequest
name|usr2Surviver1
init|=
name|addResource
argument_list|(
name|user2rsrcs
argument_list|,
literal|"/private-u2-resource1.txt"
argument_list|,
literal|5
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|addResource
argument_list|(
name|user2rsrcs
argument_list|,
literal|"/private-u2-resource2.txt"
argument_list|,
literal|3
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|addResource
argument_list|(
name|user2rsrcs
argument_list|,
literal|"/private-u2-resource3.txt"
argument_list|,
literal|4
argument_list|,
literal|20
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|LocalResourcesTracker
name|user2Tracker
init|=
operator|new
name|StubbedLocalResourcesTrackerImpl
argument_list|(
literal|"user2"
argument_list|,
name|user2rsrcs
argument_list|)
decl_stmt|;
name|privateRsrc
operator|.
name|put
argument_list|(
literal|"user2"
argument_list|,
name|user2Tracker
argument_list|)
expr_stmt|;
name|ResourceLocalizationService
name|rls
init|=
name|createLocService
argument_list|(
name|publicRsrc
argument_list|,
name|privateRsrc
argument_list|,
literal|80
argument_list|)
decl_stmt|;
name|LocalCacheCleanerStats
name|stats
init|=
name|rls
operator|.
name|handleCacheCleanup
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|pubLocalRsrc
init|=
operator|(
operator|(
name|StubbedLocalResourcesTrackerImpl
operator|)
name|rls
operator|.
name|publicRsrc
operator|)
operator|.
name|getLocalRsrc
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|pubLocalRsrc
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pubLocalRsrc
operator|.
name|containsKey
argument_list|(
name|pubSurviver1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pubLocalRsrc
operator|.
name|containsKey
argument_list|(
name|pubSurviver2
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|usr1LocalRsrc
init|=
operator|(
operator|(
name|StubbedLocalResourcesTrackerImpl
operator|)
name|privateRsrc
operator|.
name|get
argument_list|(
literal|"user1"
argument_list|)
operator|)
operator|.
name|getLocalRsrc
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|usr1LocalRsrc
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|usr1LocalRsrc
operator|.
name|containsKey
argument_list|(
name|usr1Surviver1
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|usr2LocalRsrc
init|=
operator|(
operator|(
name|StubbedLocalResourcesTrackerImpl
operator|)
name|privateRsrc
operator|.
name|get
argument_list|(
literal|"user2"
argument_list|)
operator|)
operator|.
name|getLocalRsrc
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|usr2LocalRsrc
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|usr2LocalRsrc
operator|.
name|containsKey
argument_list|(
name|usr2Surviver1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|80
argument_list|,
name|stats
operator|.
name|getTotalDelSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|80
argument_list|,
name|rls
operator|.
name|metrics
operator|.
name|getTotalBytesDeleted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|stats
operator|.
name|getPublicDelSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|rls
operator|.
name|metrics
operator|.
name|getPublicBytesDeleted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|60
argument_list|,
name|stats
operator|.
name|getPrivateDelSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|60
argument_list|,
name|rls
operator|.
name|metrics
operator|.
name|getPrivateBytesDeleted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|160
argument_list|,
name|rls
operator|.
name|metrics
operator|.
name|getCacheSizeBeforeClean
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createLocService ( ConcurrentMap<LocalResourceRequest, LocalizedResource> publicRsrcs, ConcurrentMap<String, LocalResourcesTracker> privateRsrcs, long targetCacheSize)
specifier|private
name|ResourceLocalizationService
name|createLocService
parameter_list|(
name|ConcurrentMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|publicRsrcs
parameter_list|,
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|LocalResourcesTracker
argument_list|>
name|privateRsrcs
parameter_list|,
name|long
name|targetCacheSize
parameter_list|)
block|{
name|Context
name|mockedContext
init|=
name|mock
argument_list|(
name|Context
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockedContext
operator|.
name|getNMStateStore
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|NodeManagerMetrics
name|metrics
init|=
name|NodeManagerMetrics
operator|.
name|create
argument_list|()
decl_stmt|;
name|ResourceLocalizationService
name|rls
init|=
operator|new
name|ResourceLocalizationService
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|mockedContext
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
comment|// We set the following members directly so we don't have to deal with
comment|// mocking out the service init method.
name|rls
operator|.
name|publicRsrc
operator|=
operator|new
name|StubbedLocalResourcesTrackerImpl
argument_list|(
literal|null
argument_list|,
name|publicRsrcs
argument_list|)
expr_stmt|;
name|rls
operator|.
name|cacheTargetSize
operator|=
name|targetCacheSize
expr_stmt|;
name|rls
operator|.
name|privateRsrc
operator|.
name|putAll
argument_list|(
name|privateRsrcs
argument_list|)
expr_stmt|;
return|return
name|rls
return|;
block|}
DECL|method|addResource ( ConcurrentMap<LocalResourceRequest, LocalizedResource> resources, String path, long timestamp, long size, int refCount)
specifier|private
name|LocalResourceRequest
name|addResource
parameter_list|(
name|ConcurrentMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|resources
parameter_list|,
name|String
name|path
parameter_list|,
name|long
name|timestamp
parameter_list|,
name|long
name|size
parameter_list|,
name|int
name|refCount
parameter_list|)
block|{
name|LocalResourceRequest
name|request
init|=
name|createLocalResourceRequest
argument_list|(
name|path
argument_list|,
name|timestamp
argument_list|)
decl_stmt|;
name|LocalizedResource
name|resource
init|=
name|createLocalizedResource
argument_list|(
name|size
argument_list|,
name|refCount
argument_list|,
name|timestamp
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|resources
operator|.
name|put
argument_list|(
name|request
argument_list|,
name|resource
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
DECL|method|createLocalResourceRequest (String path, long timestamp)
specifier|private
name|LocalResourceRequest
name|createLocalResourceRequest
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
return|return
operator|new
name|LocalResourceRequest
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|,
name|timestamp
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
return|;
block|}
DECL|method|createLocalizedResource (long size, int refCount, long timestamp, LocalResourceRequest req)
specifier|private
name|LocalizedResource
name|createLocalizedResource
parameter_list|(
name|long
name|size
parameter_list|,
name|int
name|refCount
parameter_list|,
name|long
name|timestamp
parameter_list|,
name|LocalResourceRequest
name|req
parameter_list|)
block|{
name|LocalizedResource
name|lr
init|=
name|mock
argument_list|(
name|LocalizedResource
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|lr
operator|.
name|getSize
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|lr
operator|.
name|getRefCount
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|refCount
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|lr
operator|.
name|getTimestamp
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|lr
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ResourceState
operator|.
name|LOCALIZED
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|lr
operator|.
name|getRequest
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|req
argument_list|)
expr_stmt|;
return|return
name|lr
return|;
block|}
DECL|class|StubbedLocalResourcesTrackerImpl
class|class
name|StubbedLocalResourcesTrackerImpl
extends|extends
name|LocalResourcesTrackerImpl
block|{
DECL|method|StubbedLocalResourcesTrackerImpl (String user, ConcurrentMap<LocalResourceRequest, LocalizedResource> rsrcs)
name|StubbedLocalResourcesTrackerImpl
parameter_list|(
name|String
name|user
parameter_list|,
name|ConcurrentMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|rsrcs
parameter_list|)
block|{
name|super
argument_list|(
name|user
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|rsrcs
argument_list|,
literal|false
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|remove (LocalizedResource rem, DeletionService delService)
specifier|public
name|boolean
name|remove
parameter_list|(
name|LocalizedResource
name|rem
parameter_list|,
name|DeletionService
name|delService
parameter_list|)
block|{
name|LocalizedResource
name|r
init|=
name|localrsrc
operator|.
name|remove
argument_list|(
name|rem
operator|.
name|getRequest
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Removed "
operator|+
name|rem
operator|.
name|getRequest
argument_list|()
operator|.
name|getPath
argument_list|()
operator|+
literal|" from localized cache"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|getLocalRsrc ()
name|Map
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|getLocalRsrc
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|localrsrc
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

