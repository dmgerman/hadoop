begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.impl.pb.service
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
name|impl
operator|.
name|pb
operator|.
name|service
package|;
end_package

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
name|proto
operator|.
name|YarnServerCommonServiceProtos
operator|.
name|NodeHeartbeatRequestProto
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
name|proto
operator|.
name|YarnServerCommonServiceProtos
operator|.
name|NodeHeartbeatResponseProto
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
name|proto
operator|.
name|YarnServerCommonServiceProtos
operator|.
name|RegisterNodeManagerRequestProto
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
name|proto
operator|.
name|YarnServerCommonServiceProtos
operator|.
name|RegisterNodeManagerResponseProto
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
name|ResourceTracker
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
name|ResourceTrackerPB
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
name|NodeHeartbeatResponse
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
name|RegisterNodeManagerResponse
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
name|NodeHeartbeatRequestPBImpl
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
name|NodeHeartbeatResponsePBImpl
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
name|RegisterNodeManagerResponsePBImpl
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|RpcController
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ServiceException
import|;
end_import

begin_class
DECL|class|ResourceTrackerPBServiceImpl
specifier|public
class|class
name|ResourceTrackerPBServiceImpl
implements|implements
name|ResourceTrackerPB
block|{
DECL|field|real
specifier|private
name|ResourceTracker
name|real
decl_stmt|;
DECL|method|ResourceTrackerPBServiceImpl (ResourceTracker impl)
specifier|public
name|ResourceTrackerPBServiceImpl
parameter_list|(
name|ResourceTracker
name|impl
parameter_list|)
block|{
name|this
operator|.
name|real
operator|=
name|impl
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|registerNodeManager ( RpcController controller, RegisterNodeManagerRequestProto proto)
specifier|public
name|RegisterNodeManagerResponseProto
name|registerNodeManager
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|RegisterNodeManagerRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|RegisterNodeManagerRequestPBImpl
name|request
init|=
operator|new
name|RegisterNodeManagerRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|RegisterNodeManagerResponse
name|response
init|=
name|real
operator|.
name|registerNodeManager
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|RegisterNodeManagerResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|nodeHeartbeat (RpcController controller, NodeHeartbeatRequestProto proto)
specifier|public
name|NodeHeartbeatResponseProto
name|nodeHeartbeat
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|NodeHeartbeatRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|NodeHeartbeatRequestPBImpl
name|request
init|=
operator|new
name|NodeHeartbeatRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|NodeHeartbeatResponse
name|response
init|=
name|real
operator|.
name|nodeHeartbeat
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|NodeHeartbeatResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

