begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.webapp
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
name|webapp
package|;
end_package

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
name|ImmutableMap
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
name|ApplicationBaseProtocol
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
name|ApplicationAttemptId
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
name|ContainerReport
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
name|ContainerState
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
name|NodeId
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
name|ResourceInformation
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
name|resourcetypes
operator|.
name|ResourceTypesTestHelper
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
name|webapp
operator|.
name|dao
operator|.
name|ContainerInfo
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
name|CustomResourceTypesConfigurationProvider
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
name|webapp
operator|.
name|View
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
name|Test
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
name|HashMap
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
import|import static
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
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB
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
name|conf
operator|.
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES
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

begin_comment
comment|/**  * Tests for ContainerBlock.  */
end_comment

begin_class
DECL|class|ContainerBlockTest
specifier|public
class|class
name|ContainerBlockTest
block|{
DECL|method|createContainerReport ()
specifier|private
name|ContainerReport
name|createContainerReport
parameter_list|()
block|{
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1234
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|attemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|applicationId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|attemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|>
name|ports
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"abc"
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|ports
operator|.
name|put
argument_list|(
literal|"192.168.0.1"
argument_list|,
name|list
argument_list|)
expr_stmt|;
name|ContainerReport
name|container
init|=
name|ContainerReport
operator|.
name|newInstance
argument_list|(
name|containerId
argument_list|,
literal|null
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"host"
argument_list|,
literal|1234
argument_list|)
argument_list|,
name|Priority
operator|.
name|UNDEFINED
argument_list|,
literal|1234
argument_list|,
literal|5678
argument_list|,
literal|"diagnosticInfo"
argument_list|,
literal|"logURL"
argument_list|,
literal|0
argument_list|,
name|ContainerState
operator|.
name|COMPLETE
argument_list|,
literal|"http://"
operator|+
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"host"
argument_list|,
literal|2345
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|container
operator|.
name|setExposedPorts
argument_list|(
name|ports
argument_list|)
expr_stmt|;
return|return
name|container
return|;
block|}
annotation|@
name|Test
DECL|method|testRenderResourcesString ()
specifier|public
name|void
name|testRenderResourcesString
parameter_list|()
block|{
name|CustomResourceTypesConfigurationProvider
operator|.
name|initResourceTypes
argument_list|(
name|ResourceInformation
operator|.
name|GPU_URI
argument_list|)
expr_stmt|;
name|Resource
name|resource
init|=
name|ResourceTypesTestHelper
operator|.
name|newResource
argument_list|(
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB
argument_list|,
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|ResourceInformation
operator|.
name|GPU_URI
argument_list|,
literal|"5"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerBlock
name|block
init|=
operator|new
name|ContainerBlock
argument_list|(
name|mock
argument_list|(
name|ApplicationBaseProtocol
operator|.
name|class
argument_list|)
argument_list|,
name|mock
argument_list|(
name|View
operator|.
name|ViewContext
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|ContainerReport
name|containerReport
init|=
name|createContainerReport
argument_list|()
decl_stmt|;
name|containerReport
operator|.
name|setAllocatedResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|ContainerInfo
name|containerInfo
init|=
operator|new
name|ContainerInfo
argument_list|(
name|containerReport
argument_list|)
decl_stmt|;
name|String
name|resources
init|=
name|block
operator|.
name|getResources
argument_list|(
name|containerInfo
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"8192 Memory, 4 VCores, 5 yarn.io/gpu"
argument_list|,
name|resources
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

