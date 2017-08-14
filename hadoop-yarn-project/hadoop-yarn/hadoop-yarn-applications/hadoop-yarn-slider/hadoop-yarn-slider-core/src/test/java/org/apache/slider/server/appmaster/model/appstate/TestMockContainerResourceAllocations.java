begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.model.appstate
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|model
operator|.
name|appstate
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
name|slider
operator|.
name|api
operator|.
name|ResourceKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|resource
operator|.
name|Application
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|resource
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|model
operator|.
name|mock
operator|.
name|BaseMockAppStateTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|model
operator|.
name|mock
operator|.
name|MockAppState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|model
operator|.
name|mock
operator|.
name|MockRoles
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|operations
operator|.
name|AbstractRMOperation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|operations
operator|.
name|ContainerRequestOperation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|RoleStatus
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

begin_comment
comment|/**  * Test the container resource allocation logic.  */
end_comment

begin_class
DECL|class|TestMockContainerResourceAllocations
specifier|public
class|class
name|TestMockContainerResourceAllocations
extends|extends
name|BaseMockAppStateTest
block|{
annotation|@
name|Override
DECL|method|buildApplication ()
specifier|public
name|Application
name|buildApplication
parameter_list|()
block|{
return|return
name|factory
operator|.
name|newApplication
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
operator|.
name|name
argument_list|(
name|getValidTestName
argument_list|()
argument_list|)
return|;
block|}
comment|//@Test
DECL|method|testNormalAllocations ()
specifier|public
name|void
name|testNormalAllocations
parameter_list|()
throws|throws
name|Throwable
block|{
name|Component
name|role0
init|=
name|appState
operator|.
name|getClusterStatus
argument_list|()
operator|.
name|getComponent
argument_list|(
name|MockRoles
operator|.
name|ROLE0
argument_list|)
decl_stmt|;
name|role0
operator|.
name|resource
argument_list|(
operator|new
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|resource
operator|.
name|Resource
argument_list|()
operator|.
name|memory
argument_list|(
literal|"512"
argument_list|)
operator|.
name|cpus
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// hack - because role0 is created before the test run
name|RoleStatus
name|role0Status
init|=
name|appState
operator|.
name|getRoleStatusMap
argument_list|()
operator|.
name|get
argument_list|(
name|appState
operator|.
name|getRoleMap
argument_list|()
operator|.
name|get
argument_list|(
name|ROLE0
argument_list|)
operator|.
name|id
argument_list|)
decl_stmt|;
name|role0Status
operator|.
name|setResourceRequirements
argument_list|(
name|appState
operator|.
name|buildResourceRequirements
argument_list|(
name|role0Status
argument_list|)
argument_list|)
expr_stmt|;
name|appState
operator|.
name|updateComponents
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
name|role0
operator|.
name|getName
argument_list|()
argument_list|,
name|role0
operator|.
name|getNumberOfContainers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AbstractRMOperation
argument_list|>
name|ops
init|=
name|appState
operator|.
name|reviewRequestAndReleaseNodes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ops
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerRequestOperation
name|operation
init|=
operator|(
name|ContainerRequestOperation
operator|)
name|ops
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Resource
name|requirements
init|=
name|operation
operator|.
name|getRequest
argument_list|()
operator|.
name|getCapability
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|512L
argument_list|,
name|requirements
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|requirements
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//TODO replace with resource profile feature in yarn
comment|//@Test
DECL|method|testMaxMemAllocations ()
specifier|public
name|void
name|testMaxMemAllocations
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// max core allocations no longer supported
name|Component
name|role0
init|=
name|appState
operator|.
name|getClusterStatus
argument_list|()
operator|.
name|getComponent
argument_list|(
name|MockRoles
operator|.
name|ROLE0
argument_list|)
decl_stmt|;
name|role0
operator|.
name|resource
argument_list|(
operator|new
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|resource
operator|.
name|Resource
argument_list|()
operator|.
name|memory
argument_list|(
name|ResourceKeys
operator|.
name|YARN_RESOURCE_MAX
argument_list|)
operator|.
name|cpus
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|RoleStatus
name|role0Status
init|=
name|appState
operator|.
name|getRoleStatusMap
argument_list|()
operator|.
name|get
argument_list|(
name|appState
operator|.
name|getRoleMap
argument_list|()
operator|.
name|get
argument_list|(
name|ROLE0
argument_list|)
operator|.
name|id
argument_list|)
decl_stmt|;
name|role0Status
operator|.
name|setResourceRequirements
argument_list|(
name|appState
operator|.
name|buildResourceRequirements
argument_list|(
name|role0Status
argument_list|)
argument_list|)
expr_stmt|;
name|appState
operator|.
name|updateComponents
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
name|role0
operator|.
name|getName
argument_list|()
argument_list|,
name|role0
operator|.
name|getNumberOfContainers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AbstractRMOperation
argument_list|>
name|ops
init|=
name|appState
operator|.
name|reviewRequestAndReleaseNodes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ops
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerRequestOperation
name|operation
init|=
operator|(
name|ContainerRequestOperation
operator|)
name|ops
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Resource
name|requirements
init|=
name|operation
operator|.
name|getRequest
argument_list|()
operator|.
name|getCapability
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|MockAppState
operator|.
name|RM_MAX_RAM
argument_list|,
name|requirements
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|requirements
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testMaxDefaultAllocations ()
specifier|public
name|void
name|testMaxDefaultAllocations
parameter_list|()
throws|throws
name|Throwable
block|{
name|List
argument_list|<
name|AbstractRMOperation
argument_list|>
name|ops
init|=
name|appState
operator|.
name|reviewRequestAndReleaseNodes
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|ops
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ContainerRequestOperation
name|operation
init|=
operator|(
name|ContainerRequestOperation
operator|)
name|ops
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Resource
name|requirements
init|=
name|operation
operator|.
name|getRequest
argument_list|()
operator|.
name|getCapability
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|ResourceKeys
operator|.
name|DEF_YARN_MEMORY
argument_list|,
name|requirements
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ResourceKeys
operator|.
name|DEF_YARN_CORES
argument_list|,
name|requirements
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

