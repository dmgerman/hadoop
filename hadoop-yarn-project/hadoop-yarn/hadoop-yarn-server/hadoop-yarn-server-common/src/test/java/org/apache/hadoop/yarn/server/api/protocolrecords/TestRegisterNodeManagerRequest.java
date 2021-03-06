begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.protocolrecords
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
name|api
operator|.
name|protocolrecords
package|;
end_package

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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|RegisterNodeManagerRequestPBImpl
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

begin_class
DECL|class|TestRegisterNodeManagerRequest
specifier|public
class|class
name|TestRegisterNodeManagerRequest
block|{
annotation|@
name|Test
DECL|method|testRegisterNodeManagerRequest ()
specifier|public
name|void
name|testRegisterNodeManagerRequest
parameter_list|()
block|{
name|RegisterNodeManagerRequest
name|request
init|=
name|RegisterNodeManagerRequest
operator|.
name|newInstance
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"host"
argument_list|,
literal|1234
argument_list|)
argument_list|,
literal|1234
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|"version"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|NMContainerStatus
operator|.
name|newInstance
argument_list|(
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1234L
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|0
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"good"
argument_list|,
operator|-
literal|1
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|1234
argument_list|)
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1234L
argument_list|,
literal|1
argument_list|)
argument_list|,
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1234L
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// serialze to proto, and get request from proto
name|RegisterNodeManagerRequest
name|request1
init|=
operator|new
name|RegisterNodeManagerRequestPBImpl
argument_list|(
operator|(
operator|(
name|RegisterNodeManagerRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
decl_stmt|;
comment|// check values
name|Assert
operator|.
name|assertEquals
argument_list|(
name|request1
operator|.
name|getNMContainerStatuses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|request
operator|.
name|getNMContainerStatuses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|request1
operator|.
name|getNMContainerStatuses
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|request
operator|.
name|getNMContainerStatuses
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|request1
operator|.
name|getRunningApplications
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|request
operator|.
name|getRunningApplications
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|request1
operator|.
name|getRunningApplications
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|request
operator|.
name|getRunningApplications
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|request1
operator|.
name|getRunningApplications
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|request
operator|.
name|getRunningApplications
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRegisterNodeManagerRequestWithNullArrays ()
specifier|public
name|void
name|testRegisterNodeManagerRequestWithNullArrays
parameter_list|()
block|{
name|RegisterNodeManagerRequest
name|request
init|=
name|RegisterNodeManagerRequest
operator|.
name|newInstance
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"host"
argument_list|,
literal|1234
argument_list|)
argument_list|,
literal|1234
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|"version"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// serialze to proto, and get request from proto
name|RegisterNodeManagerRequest
name|request1
init|=
operator|new
name|RegisterNodeManagerRequestPBImpl
argument_list|(
operator|(
operator|(
name|RegisterNodeManagerRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
decl_stmt|;
comment|// check values
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|request1
operator|.
name|getNMContainerStatuses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|request1
operator|.
name|getRunningApplications
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

