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
name|ipc
operator|.
name|ProtobufRpcEngine
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
name|ipc
operator|.
name|RPC
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
name|ipc
operator|.
name|Server
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
name|net
operator|.
name|NetUtils
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
name|ApplicationMasterProtocolPB
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|AllocateRequestPBImpl
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|AllocateResponsePBImpl
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|FinishApplicationMasterRequestPBImpl
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|FinishApplicationMasterResponsePBImpl
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|RegisterApplicationMasterRequestPBImpl
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|RegisterApplicationMasterResponsePBImpl
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
name|DistributedSchedulerProtocolPB
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
name|protocolrecords
operator|.
name|AllocateRequest
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
name|protocolrecords
operator|.
name|AllocateResponse
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
name|DistSchedAllocateResponse
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
name|DistSchedRegisterResponse
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
name|protocolrecords
operator|.
name|FinishApplicationMasterRequest
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
name|protocolrecords
operator|.
name|FinishApplicationMasterResponse
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
name|protocolrecords
operator|.
name|RegisterApplicationMasterRequest
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
name|protocolrecords
operator|.
name|RegisterApplicationMasterResponse
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|ipc
operator|.
name|HadoopYarnProtoRPC
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
name|ipc
operator|.
name|YarnRPC
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
name|DistSchedAllocateResponsePBImpl
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
name|DistSchedRegisterResponsePBImpl
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
name|rmapp
operator|.
name|attempt
operator|.
name|AMLivelinessMonitor
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_class
DECL|class|TestDistributedSchedulingService
specifier|public
class|class
name|TestDistributedSchedulingService
block|{
comment|// Test if the DistributedSchedulingService can handle both DSProtocol as
comment|// well as AMProtocol clients
annotation|@
name|Test
DECL|method|testRPCWrapping ()
specifier|public
name|void
name|testRPCWrapping
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
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|IPC_RPC_IMPL
argument_list|,
name|HadoopYarnProtoRPC
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|bindAddr
init|=
literal|"localhost:0"
decl_stmt|;
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|bindAddr
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_ADDRESS
argument_list|,
name|addr
argument_list|)
expr_stmt|;
specifier|final
name|RecordFactory
name|factory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|RMContext
name|rmContext
init|=
operator|new
name|RMContextImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AMLivelinessMonitor
name|getAMLivelinessMonitor
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Configuration
name|getYarnConfiguration
parameter_list|()
block|{
return|return
operator|new
name|YarnConfiguration
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|DistributedSchedulingService
name|service
init|=
operator|new
name|DistributedSchedulingService
argument_list|(
name|rmContext
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|RegisterApplicationMasterResponse
name|registerApplicationMaster
parameter_list|(
name|RegisterApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|RegisterApplicationMasterResponse
name|resp
init|=
name|factory
operator|.
name|newRecordInstance
argument_list|(
name|RegisterApplicationMasterResponse
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Dummy Entry to Assert that we get this object back
name|resp
operator|.
name|setQueue
argument_list|(
literal|"dummyQueue"
argument_list|)
expr_stmt|;
return|return
name|resp
return|;
block|}
annotation|@
name|Override
specifier|public
name|FinishApplicationMasterResponse
name|finishApplicationMaster
parameter_list|(
name|FinishApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|FinishApplicationMasterResponse
name|resp
init|=
name|factory
operator|.
name|newRecordInstance
argument_list|(
name|FinishApplicationMasterResponse
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Dummy Entry to Assert that we get this object back
name|resp
operator|.
name|setIsUnregistered
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|resp
return|;
block|}
annotation|@
name|Override
specifier|public
name|AllocateResponse
name|allocate
parameter_list|(
name|AllocateRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|AllocateResponse
name|response
init|=
name|factory
operator|.
name|newRecordInstance
argument_list|(
name|AllocateResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setNumClusterNodes
argument_list|(
literal|12345
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
specifier|public
name|DistSchedRegisterResponse
name|registerApplicationMasterForDistributedScheduling
parameter_list|(
name|RegisterApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|DistSchedRegisterResponse
name|resp
init|=
name|factory
operator|.
name|newRecordInstance
argument_list|(
name|DistSchedRegisterResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|resp
operator|.
name|setContainerIdStart
argument_list|(
literal|54321l
argument_list|)
expr_stmt|;
return|return
name|resp
return|;
block|}
annotation|@
name|Override
specifier|public
name|DistSchedAllocateResponse
name|allocateForDistributedScheduling
parameter_list|(
name|AllocateRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|DistSchedAllocateResponse
name|resp
init|=
name|factory
operator|.
name|newRecordInstance
argument_list|(
name|DistSchedAllocateResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|resp
operator|.
name|setNodesForScheduling
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"h1"
argument_list|,
literal|1234
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|resp
return|;
block|}
block|}
decl_stmt|;
name|Server
name|server
init|=
name|service
operator|.
name|getServer
argument_list|(
name|rpc
argument_list|,
name|conf
argument_list|,
name|addr
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Verify that the DistrubutedSchedulingService can handle vanilla
comment|// ApplicationMasterProtocol clients
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|ApplicationMasterProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|ApplicationMasterProtocolPB
name|ampProxy
init|=
name|RPC
operator|.
name|getProxy
argument_list|(
name|ApplicationMasterProtocolPB
operator|.
name|class
argument_list|,
literal|1
argument_list|,
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|RegisterApplicationMasterResponse
name|regResp
init|=
operator|new
name|RegisterApplicationMasterResponsePBImpl
argument_list|(
name|ampProxy
operator|.
name|registerApplicationMaster
argument_list|(
literal|null
argument_list|,
operator|(
operator|(
name|RegisterApplicationMasterRequestPBImpl
operator|)
name|factory
operator|.
name|newRecordInstance
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|class
argument_list|)
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"dummyQueue"
argument_list|,
name|regResp
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
name|FinishApplicationMasterResponse
name|finishResp
init|=
operator|new
name|FinishApplicationMasterResponsePBImpl
argument_list|(
name|ampProxy
operator|.
name|finishApplicationMaster
argument_list|(
literal|null
argument_list|,
operator|(
operator|(
name|FinishApplicationMasterRequestPBImpl
operator|)
name|factory
operator|.
name|newRecordInstance
argument_list|(
name|FinishApplicationMasterRequest
operator|.
name|class
argument_list|)
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|finishResp
operator|.
name|getIsUnregistered
argument_list|()
argument_list|)
expr_stmt|;
name|AllocateResponse
name|allocResp
init|=
operator|new
name|AllocateResponsePBImpl
argument_list|(
name|ampProxy
operator|.
name|allocate
argument_list|(
literal|null
argument_list|,
operator|(
operator|(
name|AllocateRequestPBImpl
operator|)
name|factory
operator|.
name|newRecordInstance
argument_list|(
name|AllocateRequest
operator|.
name|class
argument_list|)
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|12345
argument_list|,
name|allocResp
operator|.
name|getNumClusterNodes
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify that the DistrubutedSchedulingService can handle the
comment|// DistributedSchedulerProtocol clients as well
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|DistributedSchedulerProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|DistributedSchedulerProtocolPB
name|dsProxy
init|=
name|RPC
operator|.
name|getProxy
argument_list|(
name|DistributedSchedulerProtocolPB
operator|.
name|class
argument_list|,
literal|1
argument_list|,
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|DistSchedRegisterResponse
name|dsRegResp
init|=
operator|new
name|DistSchedRegisterResponsePBImpl
argument_list|(
name|dsProxy
operator|.
name|registerApplicationMasterForDistributedScheduling
argument_list|(
literal|null
argument_list|,
operator|(
operator|(
name|RegisterApplicationMasterRequestPBImpl
operator|)
name|factory
operator|.
name|newRecordInstance
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|class
argument_list|)
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|54321l
argument_list|,
name|dsRegResp
operator|.
name|getContainerIdStart
argument_list|()
argument_list|)
expr_stmt|;
name|DistSchedAllocateResponse
name|dsAllocResp
init|=
operator|new
name|DistSchedAllocateResponsePBImpl
argument_list|(
name|dsProxy
operator|.
name|allocateForDistributedScheduling
argument_list|(
literal|null
argument_list|,
operator|(
operator|(
name|AllocateRequestPBImpl
operator|)
name|factory
operator|.
name|newRecordInstance
argument_list|(
name|AllocateRequest
operator|.
name|class
argument_list|)
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"h1"
argument_list|,
name|dsAllocResp
operator|.
name|getNodesForScheduling
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

