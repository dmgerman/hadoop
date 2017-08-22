begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|event
operator|.
name|Dispatcher
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
name|protocolrecords
operator|.
name|NodeHeartbeatRequest
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
name|RegisterNodeManagerRequest
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
name|UnRegisterNodeManagerRequest
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
name|UnRegisterNodeManagerResponse
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
name|records
operator|.
name|MasterKey
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
name|records
operator|.
name|NodeStatus
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
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|MasterKeyPBImpl
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
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|utils
operator|.
name|YarnServerBuilderUtils
import|;
end_import

begin_comment
comment|/**  * This class allows a node manager to run without without communicating with a  * real RM.  */
end_comment

begin_class
DECL|class|MockNodeStatusUpdater
specifier|public
class|class
name|MockNodeStatusUpdater
extends|extends
name|NodeStatusUpdaterImpl
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MockNodeStatusUpdater
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|static
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|resourceTracker
specifier|private
name|ResourceTracker
name|resourceTracker
decl_stmt|;
DECL|method|MockNodeStatusUpdater (Context context, Dispatcher dispatcher, NodeHealthCheckerService healthChecker, NodeManagerMetrics metrics)
specifier|public
name|MockNodeStatusUpdater
parameter_list|(
name|Context
name|context
parameter_list|,
name|Dispatcher
name|dispatcher
parameter_list|,
name|NodeHealthCheckerService
name|healthChecker
parameter_list|,
name|NodeManagerMetrics
name|metrics
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|dispatcher
argument_list|,
name|healthChecker
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
name|resourceTracker
operator|=
name|createResourceTracker
argument_list|()
expr_stmt|;
block|}
DECL|method|createResourceTracker ()
specifier|protected
name|ResourceTracker
name|createResourceTracker
parameter_list|()
block|{
return|return
operator|new
name|MockResourceTracker
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getRMClient ()
specifier|protected
name|ResourceTracker
name|getRMClient
parameter_list|()
block|{
return|return
name|resourceTracker
return|;
block|}
annotation|@
name|Override
DECL|method|stopRMProxy ()
specifier|protected
name|void
name|stopRMProxy
parameter_list|()
block|{
return|return;
block|}
DECL|class|MockResourceTracker
specifier|protected
specifier|static
class|class
name|MockResourceTracker
implements|implements
name|ResourceTracker
block|{
DECL|field|heartBeatID
specifier|private
name|int
name|heartBeatID
decl_stmt|;
annotation|@
name|Override
DECL|method|registerNodeManager ( RegisterNodeManagerRequest request)
specifier|public
name|RegisterNodeManagerResponse
name|registerNodeManager
parameter_list|(
name|RegisterNodeManagerRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|RegisterNodeManagerResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RegisterNodeManagerResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|MasterKey
name|masterKey
init|=
operator|new
name|MasterKeyPBImpl
argument_list|()
decl_stmt|;
name|masterKey
operator|.
name|setKeyId
argument_list|(
literal|123
argument_list|)
expr_stmt|;
name|masterKey
operator|.
name|setBytes
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|new
name|Integer
argument_list|(
literal|123
argument_list|)
operator|.
name|byteValue
argument_list|()
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContainerTokenMasterKey
argument_list|(
name|masterKey
argument_list|)
expr_stmt|;
name|response
operator|.
name|setNMTokenMasterKey
argument_list|(
name|masterKey
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|nodeHeartbeat (NodeHeartbeatRequest request)
specifier|public
name|NodeHeartbeatResponse
name|nodeHeartbeat
parameter_list|(
name|NodeHeartbeatRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|NodeStatus
name|nodeStatus
init|=
name|request
operator|.
name|getNodeStatus
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got heartbeat number "
operator|+
name|heartBeatID
argument_list|)
expr_stmt|;
name|nodeStatus
operator|.
name|setResponseId
argument_list|(
name|heartBeatID
operator|++
argument_list|)
expr_stmt|;
name|NodeHeartbeatResponse
name|nhResponse
init|=
name|YarnServerBuilderUtils
operator|.
name|newNodeHeartbeatResponse
argument_list|(
name|heartBeatID
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|1000L
argument_list|)
decl_stmt|;
return|return
name|nhResponse
return|;
block|}
annotation|@
name|Override
DECL|method|unRegisterNodeManager ( UnRegisterNodeManagerRequest request)
specifier|public
name|UnRegisterNodeManagerResponse
name|unRegisterNodeManager
parameter_list|(
name|UnRegisterNodeManagerRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|UnRegisterNodeManagerResponse
operator|.
name|class
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

