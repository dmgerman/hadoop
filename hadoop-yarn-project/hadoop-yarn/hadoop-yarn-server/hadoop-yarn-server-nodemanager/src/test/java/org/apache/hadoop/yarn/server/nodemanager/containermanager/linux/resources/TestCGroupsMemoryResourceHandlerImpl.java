begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources
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
name|linux
operator|.
name|resources
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|ExecutionType
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
name|security
operator|.
name|ContainerTokenIdentifier
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
name|container
operator|.
name|Container
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
name|linux
operator|.
name|privileged
operator|.
name|PrivilegedOperation
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
name|junit
operator|.
name|Assert
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
name|mockito
operator|.
name|Mockito
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Unit test for CGroupsMemoryResourceHandlerImpl.  */
end_comment

begin_class
DECL|class|TestCGroupsMemoryResourceHandlerImpl
specifier|public
class|class
name|TestCGroupsMemoryResourceHandlerImpl
block|{
DECL|field|mockCGroupsHandler
specifier|private
name|CGroupsHandler
name|mockCGroupsHandler
decl_stmt|;
DECL|field|cGroupsMemoryResourceHandler
specifier|private
name|CGroupsMemoryResourceHandlerImpl
name|cGroupsMemoryResourceHandler
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|mockCGroupsHandler
operator|=
name|mock
argument_list|(
name|CGroupsHandler
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockCGroupsHandler
operator|.
name|getPathForCGroup
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
literal|"."
argument_list|)
expr_stmt|;
name|cGroupsMemoryResourceHandler
operator|=
operator|new
name|CGroupsMemoryResourceHandlerImpl
argument_list|(
name|mockCGroupsHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBootstrap ()
specifier|public
name|void
name|testBootstrap
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PMEM_CHECK_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_VMEM_CHECK_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|ret
init|=
name|cGroupsMemoryResourceHandler
operator|.
name|bootstrap
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|mockCGroupsHandler
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|initializeCGroupController
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|ret
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Default swappiness value incorrect"
argument_list|,
literal|0
argument_list|,
name|cGroupsMemoryResourceHandler
operator|.
name|getSwappiness
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PMEM_CHECK_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|cGroupsMemoryResourceHandler
operator|.
name|bootstrap
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceHandlerException
name|re
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Pmem check should be allowed to run with cgroups"
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PMEM_CHECK_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_VMEM_CHECK_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|cGroupsMemoryResourceHandler
operator|.
name|bootstrap
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceHandlerException
name|re
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Vmem check should be allowed to run with cgroups"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSwappinessValues ()
specifier|public
name|void
name|testSwappinessValues
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PMEM_CHECK_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_VMEM_CHECK_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_MEMORY_RESOURCE_CGROUPS_SWAPPINESS
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|cGroupsMemoryResourceHandler
operator|.
name|bootstrap
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Negative values for swappiness should not be allowed."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceHandlerException
name|re
parameter_list|)
block|{
comment|// do nothing
block|}
try|try
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_MEMORY_RESOURCE_CGROUPS_SWAPPINESS
argument_list|,
literal|101
argument_list|)
expr_stmt|;
name|cGroupsMemoryResourceHandler
operator|.
name|bootstrap
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Values greater than 100 for swappiness"
operator|+
literal|" should not be allowed."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceHandlerException
name|re
parameter_list|)
block|{
comment|// do nothing
block|}
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_MEMORY_RESOURCE_CGROUPS_SWAPPINESS
argument_list|,
literal|60
argument_list|)
expr_stmt|;
name|cGroupsMemoryResourceHandler
operator|.
name|bootstrap
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Swappiness value incorrect"
argument_list|,
literal|60
argument_list|,
name|cGroupsMemoryResourceHandler
operator|.
name|getSwappiness
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPreStart ()
specifier|public
name|void
name|testPreStart
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PMEM_CHECK_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_VMEM_CHECK_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cGroupsMemoryResourceHandler
operator|.
name|bootstrap
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|String
name|id
init|=
literal|"container_01_01"
decl_stmt|;
name|String
name|path
init|=
literal|"test-path/"
operator|+
name|id
decl_stmt|;
name|ContainerId
name|mockContainerId
init|=
name|mock
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockContainerId
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|Container
name|mockContainer
init|=
name|mock
argument_list|(
name|Container
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockContainer
operator|.
name|getContainerId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockContainerId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockCGroupsHandler
operator|.
name|getPathForCGroupTasks
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|,
name|id
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|int
name|memory
init|=
literal|1024
decl_stmt|;
name|when
argument_list|(
name|mockContainer
operator|.
name|getResource
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
name|memory
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|ret
init|=
name|cGroupsMemoryResourceHandler
operator|.
name|preStart
argument_list|(
name|mockContainer
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|mockCGroupsHandler
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|createCGroup
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockCGroupsHandler
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|updateCGroupParam
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|,
name|id
argument_list|,
name|CGroupsHandler
operator|.
name|CGROUP_PARAM_MEMORY_HARD_LIMIT_BYTES
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|memory
argument_list|)
operator|+
literal|"M"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockCGroupsHandler
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|updateCGroupParam
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|,
name|id
argument_list|,
name|CGroupsHandler
operator|.
name|CGROUP_PARAM_MEMORY_SOFT_LIMIT_BYTES
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
call|(
name|int
call|)
argument_list|(
name|memory
operator|*
literal|0.9
argument_list|)
argument_list|)
operator|+
literal|"M"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockCGroupsHandler
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|updateCGroupParam
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|,
name|id
argument_list|,
name|CGroupsHandler
operator|.
name|CGROUP_PARAM_MEMORY_SWAPPINESS
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|ret
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ret
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|PrivilegedOperation
name|op
init|=
name|ret
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|ADD_PID_TO_CGROUP
argument_list|,
name|op
operator|.
name|getOperationType
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
name|op
operator|.
name|getArguments
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|args
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|PrivilegedOperation
operator|.
name|CGROUP_ARG_PREFIX
operator|+
name|path
argument_list|,
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPreStartNonEnforced ()
specifier|public
name|void
name|testPreStartNonEnforced
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PMEM_CHECK_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_VMEM_CHECK_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_MEMORY_RESOURCE_ENFORCED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cGroupsMemoryResourceHandler
operator|.
name|bootstrap
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|String
name|id
init|=
literal|"container_01_01"
decl_stmt|;
name|String
name|path
init|=
literal|"test-path/"
operator|+
name|id
decl_stmt|;
name|ContainerId
name|mockContainerId
init|=
name|mock
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockContainerId
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|Container
name|mockContainer
init|=
name|mock
argument_list|(
name|Container
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockContainer
operator|.
name|getContainerId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockContainerId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockCGroupsHandler
operator|.
name|getPathForCGroupTasks
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|,
name|id
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|int
name|memory
init|=
literal|1024
decl_stmt|;
name|when
argument_list|(
name|mockContainer
operator|.
name|getResource
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
name|memory
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|ret
init|=
name|cGroupsMemoryResourceHandler
operator|.
name|preStart
argument_list|(
name|mockContainer
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|mockCGroupsHandler
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|createCGroup
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockCGroupsHandler
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|updateCGroupParam
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|,
name|id
argument_list|,
name|CGroupsHandler
operator|.
name|CGROUP_PARAM_MEMORY_HARD_LIMIT_BYTES
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|memory
argument_list|)
operator|+
literal|"M"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockCGroupsHandler
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|updateCGroupParam
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|,
name|id
argument_list|,
name|CGroupsHandler
operator|.
name|CGROUP_PARAM_MEMORY_SOFT_LIMIT_BYTES
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
call|(
name|int
call|)
argument_list|(
name|memory
operator|*
literal|0.9
argument_list|)
argument_list|)
operator|+
literal|"M"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockCGroupsHandler
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|updateCGroupParam
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|,
name|id
argument_list|,
name|CGroupsHandler
operator|.
name|CGROUP_PARAM_MEMORY_SWAPPINESS
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|ret
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ret
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|PrivilegedOperation
name|op
init|=
name|ret
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|ADD_PID_TO_CGROUP
argument_list|,
name|op
operator|.
name|getOperationType
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
name|op
operator|.
name|getArguments
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|args
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|PrivilegedOperation
operator|.
name|CGROUP_ARG_PREFIX
operator|+
name|path
argument_list|,
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReacquireContainer ()
specifier|public
name|void
name|testReacquireContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|ContainerId
name|containerIdMock
init|=
name|mock
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|cGroupsMemoryResourceHandler
operator|.
name|reacquireContainer
argument_list|(
name|containerIdMock
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPostComplete ()
specifier|public
name|void
name|testPostComplete
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|id
init|=
literal|"container_01_01"
decl_stmt|;
name|ContainerId
name|mockContainerId
init|=
name|mock
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockContainerId
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|cGroupsMemoryResourceHandler
operator|.
name|postComplete
argument_list|(
name|mockContainerId
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockCGroupsHandler
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|deleteCGroup
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTeardown ()
specifier|public
name|void
name|testTeardown
parameter_list|()
throws|throws
name|Exception
block|{
name|Assert
operator|.
name|assertNull
argument_list|(
name|cGroupsMemoryResourceHandler
operator|.
name|teardown
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOpportunistic ()
specifier|public
name|void
name|testOpportunistic
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PMEM_CHECK_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_VMEM_CHECK_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cGroupsMemoryResourceHandler
operator|.
name|bootstrap
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|ContainerTokenIdentifier
name|tokenId
init|=
name|mock
argument_list|(
name|ContainerTokenIdentifier
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|tokenId
operator|.
name|getExecutionType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ExecutionType
operator|.
name|OPPORTUNISTIC
argument_list|)
expr_stmt|;
name|Container
name|container
init|=
name|mock
argument_list|(
name|Container
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|id
init|=
literal|"container_01_01"
decl_stmt|;
name|ContainerId
name|mockContainerId
init|=
name|mock
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockContainerId
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockContainerId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerTokenIdentifier
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|tokenId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|cGroupsMemoryResourceHandler
operator|.
name|preStart
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockCGroupsHandler
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|updateCGroupParam
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|,
name|id
argument_list|,
name|CGroupsHandler
operator|.
name|CGROUP_PARAM_MEMORY_SOFT_LIMIT_BYTES
argument_list|,
literal|"0M"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockCGroupsHandler
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|updateCGroupParam
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|,
name|id
argument_list|,
name|CGroupsHandler
operator|.
name|CGROUP_PARAM_MEMORY_SWAPPINESS
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockCGroupsHandler
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|updateCGroupParam
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|,
name|id
argument_list|,
name|CGroupsHandler
operator|.
name|CGROUP_PARAM_MEMORY_HARD_LIMIT_BYTES
argument_list|,
literal|"1024M"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

