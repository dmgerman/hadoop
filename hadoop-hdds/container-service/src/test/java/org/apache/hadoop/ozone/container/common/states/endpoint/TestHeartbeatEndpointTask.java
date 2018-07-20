begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.states.endpoint
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|states
operator|.
name|endpoint
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerAction
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|CommandStatusReportsProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|NodeReportProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMHeartbeatResponseProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMHeartbeatRequestProto
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|DatanodeStateMachine
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|DatanodeStateMachine
operator|.
name|DatanodeStates
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|EndpointStateMachine
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|StateContext
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
name|ozone
operator|.
name|protocolPB
operator|.
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
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
name|org
operator|.
name|mockito
operator|.
name|ArgumentCaptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  * This class tests the functionality of HeartbeatEndpointTask.  */
end_comment

begin_class
DECL|class|TestHeartbeatEndpointTask
specifier|public
class|class
name|TestHeartbeatEndpointTask
block|{
annotation|@
name|Test
DECL|method|testheartbeatWithoutReports ()
specifier|public
name|void
name|testheartbeatWithoutReports
parameter_list|()
throws|throws
name|Exception
block|{
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
name|scm
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|ArgumentCaptor
argument_list|<
name|SCMHeartbeatRequestProto
argument_list|>
name|argument
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|SCMHeartbeatRequestProto
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|scm
operator|.
name|sendHeartbeat
argument_list|(
name|argument
operator|.
name|capture
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
name|invocation
lambda|->
name|SCMHeartbeatResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDatanodeUUID
argument_list|(
operator|(
operator|(
name|SCMHeartbeatRequestProto
operator|)
name|invocation
operator|.
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|getUuid
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|HeartbeatEndpointTask
name|endpointTask
init|=
name|getHeartbeatEndpointTask
argument_list|(
name|scm
argument_list|)
decl_stmt|;
name|endpointTask
operator|.
name|call
argument_list|()
expr_stmt|;
name|SCMHeartbeatRequestProto
name|heartbeat
init|=
name|argument
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|heartbeat
operator|.
name|hasDatanodeDetails
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|heartbeat
operator|.
name|hasNodeReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|heartbeat
operator|.
name|hasContainerReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|heartbeat
operator|.
name|hasCommandStatusReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|heartbeat
operator|.
name|hasContainerActions
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testheartbeatWithNodeReports ()
specifier|public
name|void
name|testheartbeatWithNodeReports
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|StateContext
name|context
init|=
operator|new
name|StateContext
argument_list|(
name|conf
argument_list|,
name|DatanodeStates
operator|.
name|RUNNING
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|DatanodeStateMachine
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
name|scm
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|ArgumentCaptor
argument_list|<
name|SCMHeartbeatRequestProto
argument_list|>
name|argument
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|SCMHeartbeatRequestProto
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|scm
operator|.
name|sendHeartbeat
argument_list|(
name|argument
operator|.
name|capture
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
name|invocation
lambda|->
name|SCMHeartbeatResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDatanodeUUID
argument_list|(
operator|(
operator|(
name|SCMHeartbeatRequestProto
operator|)
name|invocation
operator|.
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|getUuid
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|HeartbeatEndpointTask
name|endpointTask
init|=
name|getHeartbeatEndpointTask
argument_list|(
name|conf
argument_list|,
name|context
argument_list|,
name|scm
argument_list|)
decl_stmt|;
name|context
operator|.
name|addReport
argument_list|(
name|NodeReportProto
operator|.
name|getDefaultInstance
argument_list|()
argument_list|)
expr_stmt|;
name|endpointTask
operator|.
name|call
argument_list|()
expr_stmt|;
name|SCMHeartbeatRequestProto
name|heartbeat
init|=
name|argument
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|heartbeat
operator|.
name|hasDatanodeDetails
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|heartbeat
operator|.
name|hasNodeReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|heartbeat
operator|.
name|hasContainerReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|heartbeat
operator|.
name|hasCommandStatusReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|heartbeat
operator|.
name|hasContainerActions
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testheartbeatWithContainerReports ()
specifier|public
name|void
name|testheartbeatWithContainerReports
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|StateContext
name|context
init|=
operator|new
name|StateContext
argument_list|(
name|conf
argument_list|,
name|DatanodeStates
operator|.
name|RUNNING
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|DatanodeStateMachine
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
name|scm
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|ArgumentCaptor
argument_list|<
name|SCMHeartbeatRequestProto
argument_list|>
name|argument
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|SCMHeartbeatRequestProto
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|scm
operator|.
name|sendHeartbeat
argument_list|(
name|argument
operator|.
name|capture
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
name|invocation
lambda|->
name|SCMHeartbeatResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDatanodeUUID
argument_list|(
operator|(
operator|(
name|SCMHeartbeatRequestProto
operator|)
name|invocation
operator|.
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|getUuid
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|HeartbeatEndpointTask
name|endpointTask
init|=
name|getHeartbeatEndpointTask
argument_list|(
name|conf
argument_list|,
name|context
argument_list|,
name|scm
argument_list|)
decl_stmt|;
name|context
operator|.
name|addReport
argument_list|(
name|ContainerReportsProto
operator|.
name|getDefaultInstance
argument_list|()
argument_list|)
expr_stmt|;
name|endpointTask
operator|.
name|call
argument_list|()
expr_stmt|;
name|SCMHeartbeatRequestProto
name|heartbeat
init|=
name|argument
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|heartbeat
operator|.
name|hasDatanodeDetails
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|heartbeat
operator|.
name|hasNodeReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|heartbeat
operator|.
name|hasContainerReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|heartbeat
operator|.
name|hasCommandStatusReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|heartbeat
operator|.
name|hasContainerActions
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testheartbeatWithCommandStatusReports ()
specifier|public
name|void
name|testheartbeatWithCommandStatusReports
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|StateContext
name|context
init|=
operator|new
name|StateContext
argument_list|(
name|conf
argument_list|,
name|DatanodeStates
operator|.
name|RUNNING
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|DatanodeStateMachine
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
name|scm
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|ArgumentCaptor
argument_list|<
name|SCMHeartbeatRequestProto
argument_list|>
name|argument
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|SCMHeartbeatRequestProto
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|scm
operator|.
name|sendHeartbeat
argument_list|(
name|argument
operator|.
name|capture
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
name|invocation
lambda|->
name|SCMHeartbeatResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDatanodeUUID
argument_list|(
operator|(
operator|(
name|SCMHeartbeatRequestProto
operator|)
name|invocation
operator|.
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|getUuid
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|HeartbeatEndpointTask
name|endpointTask
init|=
name|getHeartbeatEndpointTask
argument_list|(
name|conf
argument_list|,
name|context
argument_list|,
name|scm
argument_list|)
decl_stmt|;
name|context
operator|.
name|addReport
argument_list|(
name|CommandStatusReportsProto
operator|.
name|getDefaultInstance
argument_list|()
argument_list|)
expr_stmt|;
name|endpointTask
operator|.
name|call
argument_list|()
expr_stmt|;
name|SCMHeartbeatRequestProto
name|heartbeat
init|=
name|argument
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|heartbeat
operator|.
name|hasDatanodeDetails
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|heartbeat
operator|.
name|hasNodeReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|heartbeat
operator|.
name|hasContainerReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|heartbeat
operator|.
name|hasCommandStatusReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|heartbeat
operator|.
name|hasContainerActions
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testheartbeatWithContainerActions ()
specifier|public
name|void
name|testheartbeatWithContainerActions
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|StateContext
name|context
init|=
operator|new
name|StateContext
argument_list|(
name|conf
argument_list|,
name|DatanodeStates
operator|.
name|RUNNING
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|DatanodeStateMachine
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
name|scm
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|ArgumentCaptor
argument_list|<
name|SCMHeartbeatRequestProto
argument_list|>
name|argument
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|SCMHeartbeatRequestProto
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|scm
operator|.
name|sendHeartbeat
argument_list|(
name|argument
operator|.
name|capture
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
name|invocation
lambda|->
name|SCMHeartbeatResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDatanodeUUID
argument_list|(
operator|(
operator|(
name|SCMHeartbeatRequestProto
operator|)
name|invocation
operator|.
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|getUuid
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|HeartbeatEndpointTask
name|endpointTask
init|=
name|getHeartbeatEndpointTask
argument_list|(
name|conf
argument_list|,
name|context
argument_list|,
name|scm
argument_list|)
decl_stmt|;
name|context
operator|.
name|addContainerAction
argument_list|(
name|getContainerAction
argument_list|()
argument_list|)
expr_stmt|;
name|endpointTask
operator|.
name|call
argument_list|()
expr_stmt|;
name|SCMHeartbeatRequestProto
name|heartbeat
init|=
name|argument
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|heartbeat
operator|.
name|hasDatanodeDetails
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|heartbeat
operator|.
name|hasNodeReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|heartbeat
operator|.
name|hasContainerReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|heartbeat
operator|.
name|hasCommandStatusReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|heartbeat
operator|.
name|hasContainerActions
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testheartbeatWithAllReports ()
specifier|public
name|void
name|testheartbeatWithAllReports
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|StateContext
name|context
init|=
operator|new
name|StateContext
argument_list|(
name|conf
argument_list|,
name|DatanodeStates
operator|.
name|RUNNING
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|DatanodeStateMachine
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
name|scm
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|ArgumentCaptor
argument_list|<
name|SCMHeartbeatRequestProto
argument_list|>
name|argument
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|SCMHeartbeatRequestProto
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|scm
operator|.
name|sendHeartbeat
argument_list|(
name|argument
operator|.
name|capture
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
name|invocation
lambda|->
name|SCMHeartbeatResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDatanodeUUID
argument_list|(
operator|(
operator|(
name|SCMHeartbeatRequestProto
operator|)
name|invocation
operator|.
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|getUuid
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|HeartbeatEndpointTask
name|endpointTask
init|=
name|getHeartbeatEndpointTask
argument_list|(
name|conf
argument_list|,
name|context
argument_list|,
name|scm
argument_list|)
decl_stmt|;
name|context
operator|.
name|addReport
argument_list|(
name|NodeReportProto
operator|.
name|getDefaultInstance
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|addReport
argument_list|(
name|ContainerReportsProto
operator|.
name|getDefaultInstance
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|addReport
argument_list|(
name|CommandStatusReportsProto
operator|.
name|getDefaultInstance
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|addContainerAction
argument_list|(
name|getContainerAction
argument_list|()
argument_list|)
expr_stmt|;
name|endpointTask
operator|.
name|call
argument_list|()
expr_stmt|;
name|SCMHeartbeatRequestProto
name|heartbeat
init|=
name|argument
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|heartbeat
operator|.
name|hasDatanodeDetails
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|heartbeat
operator|.
name|hasNodeReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|heartbeat
operator|.
name|hasContainerReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|heartbeat
operator|.
name|hasCommandStatusReport
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|heartbeat
operator|.
name|hasContainerActions
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates HeartbeatEndpointTask for the given StorageContainerManager proxy.    *    * @param proxy StorageContainerDatanodeProtocolClientSideTranslatorPB    *    * @return HeartbeatEndpointTask    */
DECL|method|getHeartbeatEndpointTask ( StorageContainerDatanodeProtocolClientSideTranslatorPB proxy)
specifier|private
name|HeartbeatEndpointTask
name|getHeartbeatEndpointTask
parameter_list|(
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
name|proxy
parameter_list|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|StateContext
name|context
init|=
operator|new
name|StateContext
argument_list|(
name|conf
argument_list|,
name|DatanodeStates
operator|.
name|RUNNING
argument_list|,
name|Mockito
operator|.
name|mock
argument_list|(
name|DatanodeStateMachine
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|getHeartbeatEndpointTask
argument_list|(
name|conf
argument_list|,
name|context
argument_list|,
name|proxy
argument_list|)
return|;
block|}
comment|/**    * Creates HeartbeatEndpointTask with the given conf, context and    * StorageContainerManager client side proxy.    *    * @param conf Configuration    * @param context StateContext    * @param proxy StorageContainerDatanodeProtocolClientSideTranslatorPB    *    * @return HeartbeatEndpointTask    */
DECL|method|getHeartbeatEndpointTask ( Configuration conf, StateContext context, StorageContainerDatanodeProtocolClientSideTranslatorPB proxy)
specifier|private
name|HeartbeatEndpointTask
name|getHeartbeatEndpointTask
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|StateContext
name|context
parameter_list|,
name|StorageContainerDatanodeProtocolClientSideTranslatorPB
name|proxy
parameter_list|)
block|{
name|DatanodeDetails
name|datanodeDetails
init|=
name|DatanodeDetails
operator|.
name|newBuilder
argument_list|()
operator|.
name|setUuid
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setHostName
argument_list|(
literal|"localhost"
argument_list|)
operator|.
name|setIpAddress
argument_list|(
literal|"127.0.0.1"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|EndpointStateMachine
name|endpointStateMachine
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|EndpointStateMachine
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|endpointStateMachine
operator|.
name|getEndPoint
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
return|return
name|HeartbeatEndpointTask
operator|.
name|newBuilder
argument_list|()
operator|.
name|setConfig
argument_list|(
name|conf
argument_list|)
operator|.
name|setDatanodeDetails
argument_list|(
name|datanodeDetails
argument_list|)
operator|.
name|setContext
argument_list|(
name|context
argument_list|)
operator|.
name|setEndpointStateMachine
argument_list|(
name|endpointStateMachine
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getContainerAction ()
specifier|private
name|ContainerAction
name|getContainerAction
parameter_list|()
block|{
name|ContainerAction
operator|.
name|Builder
name|builder
init|=
name|ContainerAction
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|ContainerInfo
name|containerInfo
init|=
name|ContainerInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerID
argument_list|(
literal|1L
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setContainer
argument_list|(
name|containerInfo
argument_list|)
operator|.
name|setAction
argument_list|(
name|ContainerAction
operator|.
name|Action
operator|.
name|CLOSE
argument_list|)
operator|.
name|setReason
argument_list|(
name|ContainerAction
operator|.
name|Reason
operator|.
name|CONTAINER_FULL
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

