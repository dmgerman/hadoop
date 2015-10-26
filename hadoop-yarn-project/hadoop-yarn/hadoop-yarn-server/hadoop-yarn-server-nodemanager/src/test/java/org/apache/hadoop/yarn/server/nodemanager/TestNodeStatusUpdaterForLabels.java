begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|assertNull
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|service
operator|.
name|ServiceOperations
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
name|NodeLabel
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
name|nodelabels
operator|.
name|CommonNodeLabelsManager
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
name|nodelabels
operator|.
name|NodeLabelTestBase
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
name|NodeAction
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
name|nodelabels
operator|.
name|NodeLabelsProvider
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

begin_class
DECL|class|TestNodeStatusUpdaterForLabels
specifier|public
class|class
name|TestNodeStatusUpdaterForLabels
extends|extends
name|NodeLabelTestBase
block|{
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
DECL|field|nm
specifier|private
name|NodeManager
name|nm
decl_stmt|;
DECL|field|dummyLabelsProviderRef
specifier|protected
name|DummyNodeLabelsProvider
name|dummyLabelsProviderRef
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|dummyLabelsProviderRef
operator|=
operator|new
name|DummyNodeLabelsProvider
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|!=
name|nm
condition|)
block|{
name|ServiceOperations
operator|.
name|stop
argument_list|(
name|nm
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ResourceTrackerForLabels
specifier|private
class|class
name|ResourceTrackerForLabels
implements|implements
name|ResourceTracker
block|{
DECL|field|heartbeatID
name|int
name|heartbeatID
init|=
literal|0
decl_stmt|;
DECL|field|labels
name|Set
argument_list|<
name|NodeLabel
argument_list|>
name|labels
decl_stmt|;
DECL|field|receivedNMHeartbeat
specifier|private
name|boolean
name|receivedNMHeartbeat
init|=
literal|false
decl_stmt|;
DECL|field|receivedNMRegister
specifier|private
name|boolean
name|receivedNMRegister
init|=
literal|false
decl_stmt|;
DECL|method|createMasterKey ()
specifier|private
name|MasterKey
name|createMasterKey
parameter_list|()
block|{
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
return|return
name|masterKey
return|;
block|}
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
name|labels
operator|=
name|request
operator|.
name|getNodeLabels
argument_list|()
expr_stmt|;
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
name|response
operator|.
name|setNodeAction
argument_list|(
name|NodeAction
operator|.
name|NORMAL
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContainerTokenMasterKey
argument_list|(
name|createMasterKey
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setNMTokenMasterKey
argument_list|(
name|createMasterKey
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setAreNodeLabelsAcceptedByRM
argument_list|(
name|labels
operator|!=
literal|null
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|ResourceTrackerForLabels
operator|.
name|class
init|)
block|{
name|receivedNMRegister
operator|=
literal|true
expr_stmt|;
name|ResourceTrackerForLabels
operator|.
name|class
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
DECL|method|waitTillHeartbeat ()
specifier|public
name|void
name|waitTillHeartbeat
parameter_list|()
block|{
if|if
condition|(
name|receivedNMHeartbeat
condition|)
block|{
return|return;
block|}
name|int
name|i
init|=
literal|10
decl_stmt|;
while|while
condition|(
operator|!
name|receivedNMHeartbeat
operator|&&
name|i
operator|>
literal|0
condition|)
block|{
synchronized|synchronized
init|(
name|ResourceTrackerForLabels
operator|.
name|class
init|)
block|{
if|if
condition|(
operator|!
name|receivedNMHeartbeat
condition|)
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"In ResourceTrackerForLabels waiting for heartbeat : "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|ResourceTrackerForLabels
operator|.
name|class
operator|.
name|wait
argument_list|(
literal|500l
argument_list|)
expr_stmt|;
comment|// to avoid race condition, i.e. sendOutofBandHeartBeat can be
comment|// sent before NSU thread has gone to sleep, hence we wait and try
comment|// to resend heartbeat again
name|nm
operator|.
name|getNodeStatusUpdater
argument_list|()
operator|.
name|sendOutofBandHeartBeat
argument_list|()
expr_stmt|;
name|ResourceTrackerForLabels
operator|.
name|class
operator|.
name|wait
argument_list|(
literal|500l
argument_list|)
expr_stmt|;
name|i
operator|--
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception caught while waiting for Heartbeat"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
operator|!
name|receivedNMHeartbeat
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Heartbeat dint receive even after waiting"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|waitTillRegister ()
specifier|public
name|void
name|waitTillRegister
parameter_list|()
block|{
if|if
condition|(
name|receivedNMRegister
condition|)
block|{
return|return;
block|}
while|while
condition|(
operator|!
name|receivedNMRegister
condition|)
block|{
synchronized|synchronized
init|(
name|ResourceTrackerForLabels
operator|.
name|class
init|)
block|{
try|try
block|{
name|ResourceTrackerForLabels
operator|.
name|class
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception caught while waiting for register"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Flag to indicate received any      */
DECL|method|resetNMHeartbeatReceiveFlag ()
specifier|public
name|void
name|resetNMHeartbeatReceiveFlag
parameter_list|()
block|{
synchronized|synchronized
init|(
name|ResourceTrackerForLabels
operator|.
name|class
init|)
block|{
name|receivedNMHeartbeat
operator|=
literal|false
expr_stmt|;
block|}
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"RTS receive heartbeat : "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|labels
operator|=
name|request
operator|.
name|getNodeLabels
argument_list|()
expr_stmt|;
name|NodeStatus
name|nodeStatus
init|=
name|request
operator|.
name|getNodeStatus
argument_list|()
decl_stmt|;
name|nodeStatus
operator|.
name|setResponseId
argument_list|(
name|heartbeatID
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
name|heartbeatID
argument_list|,
name|NodeAction
operator|.
name|NORMAL
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
comment|// to ensure that heartbeats are sent only when required.
name|nhResponse
operator|.
name|setNextHeartBeatInterval
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|nhResponse
operator|.
name|setAreNodeLabelsAcceptedByRM
argument_list|(
name|labels
operator|!=
literal|null
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|ResourceTrackerForLabels
operator|.
name|class
init|)
block|{
name|receivedNMHeartbeat
operator|=
literal|true
expr_stmt|;
name|ResourceTrackerForLabels
operator|.
name|class
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
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
literal|null
return|;
block|}
block|}
DECL|class|DummyNodeLabelsProvider
specifier|public
specifier|static
class|class
name|DummyNodeLabelsProvider
implements|implements
name|NodeLabelsProvider
block|{
DECL|field|nodeLabels
specifier|private
name|Set
argument_list|<
name|NodeLabel
argument_list|>
name|nodeLabels
init|=
name|CommonNodeLabelsManager
operator|.
name|EMPTY_NODELABEL_SET
decl_stmt|;
annotation|@
name|Override
DECL|method|getNodeLabels ()
specifier|public
specifier|synchronized
name|Set
argument_list|<
name|NodeLabel
argument_list|>
name|getNodeLabels
parameter_list|()
block|{
return|return
name|nodeLabels
return|;
block|}
DECL|method|setNodeLabels (Set<NodeLabel> nodeLabels)
specifier|synchronized
name|void
name|setNodeLabels
parameter_list|(
name|Set
argument_list|<
name|NodeLabel
argument_list|>
name|nodeLabels
parameter_list|)
block|{
name|this
operator|.
name|nodeLabels
operator|=
name|nodeLabels
expr_stmt|;
block|}
block|}
DECL|method|createNMConfigForDistributeNodeLabels ()
specifier|private
name|YarnConfiguration
name|createNMConfigForDistributeNodeLabels
parameter_list|()
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NODELABEL_CONFIGURATION_TYPE
argument_list|,
name|YarnConfiguration
operator|.
name|DISTRIBUTED_NODELABEL_CONFIGURATION_TYPE
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Test
DECL|method|testNodeStatusUpdaterForNodeLabels ()
specifier|public
name|void
name|testNodeStatusUpdaterForNodeLabels
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
specifier|final
name|ResourceTrackerForLabels
name|resourceTracker
init|=
operator|new
name|ResourceTrackerForLabels
argument_list|()
decl_stmt|;
name|nm
operator|=
operator|new
name|NodeManager
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|NodeLabelsProvider
name|createNodeLabelsProvider
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dummyLabelsProviderRef
return|;
block|}
annotation|@
name|Override
specifier|protected
name|NodeStatusUpdater
name|createNodeStatusUpdater
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
name|NodeLabelsProvider
name|labelsProvider
parameter_list|)
block|{
return|return
operator|new
name|NodeStatusUpdaterImpl
argument_list|(
name|context
argument_list|,
name|dispatcher
argument_list|,
name|healthChecker
argument_list|,
name|metrics
argument_list|,
name|labelsProvider
argument_list|)
block|{
annotation|@
name|Override
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
specifier|protected
name|void
name|stopRMProxy
parameter_list|()
block|{
return|return;
block|}
block|}
return|;
block|}
block|}
expr_stmt|;
name|YarnConfiguration
name|conf
init|=
name|createNMConfigForDistributeNodeLabels
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NODE_LABELS_RESYNC_INTERVAL
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
name|nm
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|resourceTracker
operator|.
name|resetNMHeartbeatReceiveFlag
argument_list|()
expr_stmt|;
name|nm
operator|.
name|start
argument_list|()
expr_stmt|;
name|resourceTracker
operator|.
name|waitTillRegister
argument_list|()
expr_stmt|;
name|assertNLCollectionEquals
argument_list|(
name|resourceTracker
operator|.
name|labels
argument_list|,
name|dummyLabelsProviderRef
operator|.
name|getNodeLabels
argument_list|()
argument_list|)
expr_stmt|;
name|resourceTracker
operator|.
name|waitTillHeartbeat
argument_list|()
expr_stmt|;
comment|// wait till the first heartbeat
name|resourceTracker
operator|.
name|resetNMHeartbeatReceiveFlag
argument_list|()
expr_stmt|;
comment|// heartbeat with updated labels
name|dummyLabelsProviderRef
operator|.
name|setNodeLabels
argument_list|(
name|toNodeLabelSet
argument_list|(
literal|"P"
argument_list|)
argument_list|)
expr_stmt|;
name|nm
operator|.
name|getNodeStatusUpdater
argument_list|()
operator|.
name|sendOutofBandHeartBeat
argument_list|()
expr_stmt|;
name|resourceTracker
operator|.
name|waitTillHeartbeat
argument_list|()
expr_stmt|;
name|assertNLCollectionEquals
argument_list|(
name|resourceTracker
operator|.
name|labels
argument_list|,
name|dummyLabelsProviderRef
operator|.
name|getNodeLabels
argument_list|()
argument_list|)
expr_stmt|;
name|resourceTracker
operator|.
name|resetNMHeartbeatReceiveFlag
argument_list|()
expr_stmt|;
comment|// heartbeat without updating labels
name|nm
operator|.
name|getNodeStatusUpdater
argument_list|()
operator|.
name|sendOutofBandHeartBeat
argument_list|()
expr_stmt|;
name|resourceTracker
operator|.
name|waitTillHeartbeat
argument_list|()
expr_stmt|;
name|resourceTracker
operator|.
name|resetNMHeartbeatReceiveFlag
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"If no change in labels then null should be sent as part of request"
argument_list|,
name|resourceTracker
operator|.
name|labels
argument_list|)
expr_stmt|;
comment|// provider return with null labels
name|dummyLabelsProviderRef
operator|.
name|setNodeLabels
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|nm
operator|.
name|getNodeStatusUpdater
argument_list|()
operator|.
name|sendOutofBandHeartBeat
argument_list|()
expr_stmt|;
name|resourceTracker
operator|.
name|waitTillHeartbeat
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"If provider sends null then empty label set should be sent and not null"
argument_list|,
name|resourceTracker
operator|.
name|labels
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"If provider sends null then empty labels should be sent"
argument_list|,
name|resourceTracker
operator|.
name|labels
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|resourceTracker
operator|.
name|resetNMHeartbeatReceiveFlag
argument_list|()
expr_stmt|;
comment|// Since the resync interval is set to 2 sec in every alternate heartbeat
comment|// the labels will be send along with heartbeat.In loop we sleep for 1 sec
comment|// so that every sec 1 heartbeat is send.
name|int
name|nullLabels
init|=
literal|0
decl_stmt|;
name|int
name|nonNullLabels
init|=
literal|0
decl_stmt|;
name|dummyLabelsProviderRef
operator|.
name|setNodeLabels
argument_list|(
name|toNodeLabelSet
argument_list|(
literal|"P1"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|nm
operator|.
name|getNodeStatusUpdater
argument_list|()
operator|.
name|sendOutofBandHeartBeat
argument_list|()
expr_stmt|;
name|resourceTracker
operator|.
name|waitTillHeartbeat
argument_list|()
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|resourceTracker
operator|.
name|labels
condition|)
block|{
name|nullLabels
operator|++
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"In heartbeat PI labels should be send"
argument_list|,
name|toNodeLabelSet
argument_list|(
literal|"P1"
argument_list|)
argument_list|,
name|resourceTracker
operator|.
name|labels
argument_list|)
expr_stmt|;
name|nonNullLabels
operator|++
expr_stmt|;
block|}
name|resourceTracker
operator|.
name|resetNMHeartbeatReceiveFlag
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"More than one heartbeat with empty labels expected"
argument_list|,
name|nullLabels
operator|>
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"More than one heartbeat with labels expected"
argument_list|,
name|nonNullLabels
operator|>
literal|1
argument_list|)
expr_stmt|;
name|nm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidNodeLabelsFromProvider ()
specifier|public
name|void
name|testInvalidNodeLabelsFromProvider
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
specifier|final
name|ResourceTrackerForLabels
name|resourceTracker
init|=
operator|new
name|ResourceTrackerForLabels
argument_list|()
decl_stmt|;
name|nm
operator|=
operator|new
name|NodeManager
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|NodeLabelsProvider
name|createNodeLabelsProvider
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dummyLabelsProviderRef
return|;
block|}
annotation|@
name|Override
specifier|protected
name|NodeStatusUpdater
name|createNodeStatusUpdater
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
name|NodeLabelsProvider
name|labelsProvider
parameter_list|)
block|{
return|return
operator|new
name|NodeStatusUpdaterImpl
argument_list|(
name|context
argument_list|,
name|dispatcher
argument_list|,
name|healthChecker
argument_list|,
name|metrics
argument_list|,
name|labelsProvider
argument_list|)
block|{
annotation|@
name|Override
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
specifier|protected
name|void
name|stopRMProxy
parameter_list|()
block|{
return|return;
block|}
block|}
return|;
block|}
block|}
expr_stmt|;
name|dummyLabelsProviderRef
operator|.
name|setNodeLabels
argument_list|(
name|toNodeLabelSet
argument_list|(
literal|"P"
argument_list|)
argument_list|)
expr_stmt|;
name|YarnConfiguration
name|conf
init|=
name|createNMConfigForDistributeNodeLabels
argument_list|()
decl_stmt|;
name|nm
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|resourceTracker
operator|.
name|resetNMHeartbeatReceiveFlag
argument_list|()
expr_stmt|;
name|nm
operator|.
name|start
argument_list|()
expr_stmt|;
name|resourceTracker
operator|.
name|waitTillHeartbeat
argument_list|()
expr_stmt|;
comment|// wait till the first heartbeat
name|resourceTracker
operator|.
name|resetNMHeartbeatReceiveFlag
argument_list|()
expr_stmt|;
comment|// heartbeat with invalid labels
name|dummyLabelsProviderRef
operator|.
name|setNodeLabels
argument_list|(
name|toNodeLabelSet
argument_list|(
literal|"_.P"
argument_list|)
argument_list|)
expr_stmt|;
name|nm
operator|.
name|getNodeStatusUpdater
argument_list|()
operator|.
name|sendOutofBandHeartBeat
argument_list|()
expr_stmt|;
name|resourceTracker
operator|.
name|waitTillHeartbeat
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"On Invalid Labels we need to retain earlier labels, HB "
operator|+
literal|"needs to send null"
argument_list|,
name|resourceTracker
operator|.
name|labels
argument_list|)
expr_stmt|;
name|resourceTracker
operator|.
name|resetNMHeartbeatReceiveFlag
argument_list|()
expr_stmt|;
comment|// on next heartbeat same invalid labels will be given by the provider, but
comment|// again label validation check and reset RM with empty labels set should
comment|// not happen
name|nm
operator|.
name|getNodeStatusUpdater
argument_list|()
operator|.
name|sendOutofBandHeartBeat
argument_list|()
expr_stmt|;
name|resourceTracker
operator|.
name|waitTillHeartbeat
argument_list|()
expr_stmt|;
name|resourceTracker
operator|.
name|resetNMHeartbeatReceiveFlag
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"NodeStatusUpdater need not send repeatedly empty labels on "
operator|+
literal|"invalid labels from provider "
argument_list|,
name|resourceTracker
operator|.
name|labels
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

