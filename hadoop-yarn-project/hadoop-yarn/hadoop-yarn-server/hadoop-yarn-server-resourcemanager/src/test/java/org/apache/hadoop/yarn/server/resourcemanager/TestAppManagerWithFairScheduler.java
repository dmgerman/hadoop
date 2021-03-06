begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
name|resourcemanager
package|;
end_package

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|TestCase
operator|.
name|assertTrue
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
name|yarn
operator|.
name|exceptions
operator|.
name|InvalidResourceRequestException
operator|.
name|InvalidResourceType
operator|.
name|GREATER_THEN_MAX_ALLOCATION
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|any
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
name|util
operator|.
name|Collections
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
name|AccessControlException
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
name|test
operator|.
name|GenericTestUtils
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
name|MockApps
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
name|api
operator|.
name|records
operator|.
name|ApplicationSubmissionContext
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
name|ContainerLaunchContext
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
name|Priority
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
name|Resource
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
name|ResourceRequest
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
name|exceptions
operator|.
name|InvalidResourceRequestException
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
name|resourcemanager
operator|.
name|placement
operator|.
name|ApplicationPlacementContext
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
name|resourcemanager
operator|.
name|placement
operator|.
name|PlacementManager
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|ResourceScheduler
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|FairScheduler
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|FairSchedulerConfiguration
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|allocationfile
operator|.
name|AllocationFileQueue
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|allocationfile
operator|.
name|AllocationFileWriter
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
name|resourcemanager
operator|.
name|security
operator|.
name|ClientToAMTokenSecretManagerInRM
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
name|security
operator|.
name|ApplicationACLsManager
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
name|util
operator|.
name|Records
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
name|util
operator|.
name|resource
operator|.
name|Resources
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
name|Assert
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

begin_comment
comment|/**  * Testing RMAppManager application submission with fair scheduler.  */
end_comment

begin_class
DECL|class|TestAppManagerWithFairScheduler
specifier|public
class|class
name|TestAppManagerWithFairScheduler
extends|extends
name|AppManagerTestBase
block|{
DECL|field|TEST_FOLDER
specifier|private
specifier|static
specifier|final
name|String
name|TEST_FOLDER
init|=
literal|"test-queues"
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
DECL|field|placementMgr
specifier|private
name|PlacementManager
name|placementMgr
decl_stmt|;
DECL|field|rmAppManager
specifier|private
name|TestRMAppManager
name|rmAppManager
decl_stmt|;
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|allocFileName
specifier|private
specifier|static
name|String
name|allocFileName
init|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
name|TEST_FOLDER
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
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
comment|// Basic config with one queue (override in test if needed)
name|AllocationFileWriter
operator|.
name|create
argument_list|()
operator|.
name|addQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"test"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|writeToFile
argument_list|(
name|allocFileName
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|FairScheduler
operator|.
name|class
argument_list|,
name|ResourceScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FairSchedulerConfiguration
operator|.
name|ALLOCATION_FILE
argument_list|,
name|allocFileName
argument_list|)
expr_stmt|;
name|placementMgr
operator|=
name|mock
argument_list|(
name|PlacementManager
operator|.
name|class
argument_list|)
expr_stmt|;
name|MockRM
name|mockRM
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|rmContext
operator|=
name|mockRM
operator|.
name|getRMContext
argument_list|()
expr_stmt|;
name|rmContext
operator|.
name|setQueuePlacementManager
argument_list|(
name|placementMgr
argument_list|)
expr_stmt|;
name|ApplicationMasterService
name|masterService
init|=
operator|new
name|ApplicationMasterService
argument_list|(
name|rmContext
argument_list|,
name|rmContext
operator|.
name|getScheduler
argument_list|()
argument_list|)
decl_stmt|;
name|rmAppManager
operator|=
operator|new
name|TestRMAppManager
argument_list|(
name|rmContext
argument_list|,
operator|new
name|ClientToAMTokenSecretManagerInRM
argument_list|()
argument_list|,
name|rmContext
operator|.
name|getScheduler
argument_list|()
argument_list|,
name|masterService
argument_list|,
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|File
name|allocFile
init|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
name|TEST_FOLDER
argument_list|)
decl_stmt|;
name|allocFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueueSubmitWithHighQueueContainerSize ()
specifier|public
name|void
name|testQueueSubmitWithHighQueueContainerSize
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|int
name|maxAlloc
init|=
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB
decl_stmt|;
comment|// scheduler config with a limited queue
name|AllocationFileWriter
operator|.
name|create
argument_list|()
operator|.
name|addQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"root"
argument_list|)
operator|.
name|subQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"limited"
argument_list|)
operator|.
name|maxContainerAllocation
argument_list|(
name|maxAlloc
operator|+
literal|" mb 1 vcores"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|subQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"unlimited"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|writeToFile
argument_list|(
name|allocFileName
argument_list|)
expr_stmt|;
name|rmContext
operator|.
name|getScheduler
argument_list|()
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|MockApps
operator|.
name|newAppID
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Resource
name|res
init|=
name|Resources
operator|.
name|createResource
argument_list|(
name|maxAlloc
operator|+
literal|1
argument_list|)
decl_stmt|;
name|ApplicationSubmissionContext
name|asContext
init|=
name|createAppSubmitCtx
argument_list|(
name|appId
argument_list|,
name|res
argument_list|)
decl_stmt|;
comment|// Submit to limited queue
name|when
argument_list|(
name|placementMgr
operator|.
name|placeApplication
argument_list|(
name|any
argument_list|()
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ApplicationPlacementContext
argument_list|(
literal|"limited"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|rmAppManager
operator|.
name|submitApplication
argument_list|(
name|asContext
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Test should fail on too high allocation!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidResourceRequestException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|GREATER_THEN_MAX_ALLOCATION
argument_list|,
name|e
operator|.
name|getInvalidResourceType
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// submit same app but now place it in the unlimited queue
name|when
argument_list|(
name|placementMgr
operator|.
name|placeApplication
argument_list|(
name|any
argument_list|()
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ApplicationPlacementContext
argument_list|(
literal|"root.unlimited"
argument_list|)
argument_list|)
expr_stmt|;
name|rmAppManager
operator|.
name|submitApplication
argument_list|(
name|asContext
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueueSubmitWithPermissionLimits ()
specifier|public
name|void
name|testQueueSubmitWithPermissionLimits
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ACL_ENABLE
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|AllocationFileWriter
operator|.
name|create
argument_list|()
operator|.
name|addQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"root"
argument_list|)
operator|.
name|aclSubmitApps
argument_list|(
literal|" "
argument_list|)
operator|.
name|aclAdministerApps
argument_list|(
literal|" "
argument_list|)
operator|.
name|subQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"noaccess"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|subQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"submitonly"
argument_list|)
operator|.
name|aclSubmitApps
argument_list|(
literal|"test "
argument_list|)
operator|.
name|aclAdministerApps
argument_list|(
literal|" "
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|subQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"adminonly"
argument_list|)
operator|.
name|aclSubmitApps
argument_list|(
literal|" "
argument_list|)
operator|.
name|aclAdministerApps
argument_list|(
literal|"test "
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|writeToFile
argument_list|(
name|allocFileName
argument_list|)
expr_stmt|;
name|rmContext
operator|.
name|getScheduler
argument_list|()
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|MockApps
operator|.
name|newAppID
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Resource
name|res
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationSubmissionContext
name|asContext
init|=
name|createAppSubmitCtx
argument_list|(
name|appId
argument_list|,
name|res
argument_list|)
decl_stmt|;
comment|// Submit to no access queue
name|when
argument_list|(
name|placementMgr
operator|.
name|placeApplication
argument_list|(
name|any
argument_list|()
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ApplicationPlacementContext
argument_list|(
literal|"noaccess"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|rmAppManager
operator|.
name|submitApplication
argument_list|(
name|asContext
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Test should have failed with access denied"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Access exception not found"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AccessControlException
argument_list|)
expr_stmt|;
block|}
comment|// Submit to submit access queue
name|when
argument_list|(
name|placementMgr
operator|.
name|placeApplication
argument_list|(
name|any
argument_list|()
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ApplicationPlacementContext
argument_list|(
literal|"submitonly"
argument_list|)
argument_list|)
expr_stmt|;
name|rmAppManager
operator|.
name|submitApplication
argument_list|(
name|asContext
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
comment|// Submit second app to admin access queue
name|appId
operator|=
name|MockApps
operator|.
name|newAppID
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|asContext
operator|=
name|createAppSubmitCtx
argument_list|(
name|appId
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|placementMgr
operator|.
name|placeApplication
argument_list|(
name|any
argument_list|()
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ApplicationPlacementContext
argument_list|(
literal|"adminonly"
argument_list|)
argument_list|)
expr_stmt|;
name|rmAppManager
operator|.
name|submitApplication
argument_list|(
name|asContext
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueueSubmitWithRootPermission ()
specifier|public
name|void
name|testQueueSubmitWithRootPermission
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ACL_ENABLE
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|AllocationFileWriter
operator|.
name|create
argument_list|()
operator|.
name|addQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"root"
argument_list|)
operator|.
name|subQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"noaccess"
argument_list|)
operator|.
name|aclSubmitApps
argument_list|(
literal|" "
argument_list|)
operator|.
name|aclAdministerApps
argument_list|(
literal|" "
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|writeToFile
argument_list|(
name|allocFileName
argument_list|)
expr_stmt|;
name|rmContext
operator|.
name|getScheduler
argument_list|()
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|MockApps
operator|.
name|newAppID
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Resource
name|res
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationSubmissionContext
name|asContext
init|=
name|createAppSubmitCtx
argument_list|(
name|appId
argument_list|,
name|res
argument_list|)
decl_stmt|;
comment|// Submit to noaccess queue should be allowed by root ACL
name|when
argument_list|(
name|placementMgr
operator|.
name|placeApplication
argument_list|(
name|any
argument_list|()
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ApplicationPlacementContext
argument_list|(
literal|"noaccess"
argument_list|)
argument_list|)
expr_stmt|;
name|rmAppManager
operator|.
name|submitApplication
argument_list|(
name|asContext
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueueSubmitWithAutoCreateQueue ()
specifier|public
name|void
name|testQueueSubmitWithAutoCreateQueue
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ACL_ENABLE
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|AllocationFileWriter
operator|.
name|create
argument_list|()
operator|.
name|addQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"root"
argument_list|)
operator|.
name|aclSubmitApps
argument_list|(
literal|" "
argument_list|)
operator|.
name|aclAdministerApps
argument_list|(
literal|" "
argument_list|)
operator|.
name|subQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"noaccess"
argument_list|)
operator|.
name|parent
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|subQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"submitonly"
argument_list|)
operator|.
name|parent
argument_list|(
literal|true
argument_list|)
operator|.
name|aclSubmitApps
argument_list|(
literal|"test "
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|writeToFile
argument_list|(
name|allocFileName
argument_list|)
expr_stmt|;
name|rmContext
operator|.
name|getScheduler
argument_list|()
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|MockApps
operator|.
name|newAppID
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Resource
name|res
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationSubmissionContext
name|asContext
init|=
name|createAppSubmitCtx
argument_list|(
name|appId
argument_list|,
name|res
argument_list|)
decl_stmt|;
comment|// Submit to noaccess parent with non existent child queue
name|when
argument_list|(
name|placementMgr
operator|.
name|placeApplication
argument_list|(
name|any
argument_list|()
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ApplicationPlacementContext
argument_list|(
literal|"root.noaccess.child"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|rmAppManager
operator|.
name|submitApplication
argument_list|(
name|asContext
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Test should have failed with access denied"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Access exception not found"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AccessControlException
argument_list|)
expr_stmt|;
block|}
comment|// Submit to submitonly parent with non existent child queue
name|when
argument_list|(
name|placementMgr
operator|.
name|placeApplication
argument_list|(
name|any
argument_list|()
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|ApplicationPlacementContext
argument_list|(
literal|"root.submitonly.child"
argument_list|)
argument_list|)
expr_stmt|;
name|rmAppManager
operator|.
name|submitApplication
argument_list|(
name|asContext
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
DECL|method|createAppSubmitCtx (ApplicationId appId, Resource res)
specifier|private
name|ApplicationSubmissionContext
name|createAppSubmitCtx
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|ApplicationSubmissionContext
name|asContext
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ApplicationSubmissionContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|asContext
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|ResourceRequest
name|resReg
init|=
name|ResourceRequest
operator|.
name|newInstance
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|,
name|ResourceRequest
operator|.
name|ANY
argument_list|,
name|res
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|asContext
operator|.
name|setAMContainerResourceRequests
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|resReg
argument_list|)
argument_list|)
expr_stmt|;
name|asContext
operator|.
name|setAMContainerSpec
argument_list|(
name|mock
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|asContext
operator|.
name|setQueue
argument_list|(
literal|"default"
argument_list|)
expr_stmt|;
return|return
name|asContext
return|;
block|}
block|}
end_class

end_unit

