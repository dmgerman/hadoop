begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|ServerRMProxy
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
name|util
operator|.
name|YarnVersionInfo
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
DECL|class|TestResourceTrackerOnHA
specifier|public
class|class
name|TestResourceTrackerOnHA
extends|extends
name|ProtocolHATestBase
block|{
DECL|field|resourceTracker
specifier|private
name|ResourceTracker
name|resourceTracker
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
DECL|method|initiate ()
specifier|public
name|void
name|initiate
parameter_list|()
throws|throws
name|Exception
block|{
name|startHACluster
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|resourceTracker
operator|=
name|getRMClient
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutDown ()
specifier|public
name|void
name|shutDown
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|resourceTracker
operator|!=
literal|null
condition|)
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|this
operator|.
name|resourceTracker
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
DECL|method|testResourceTrackerOnHA ()
specifier|public
name|void
name|testResourceTrackerOnHA
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeId
name|nodeId
init|=
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"localhost"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|2048
argument_list|,
literal|4
argument_list|)
decl_stmt|;
comment|// make sure registerNodeManager works when failover happens
name|RegisterNodeManagerRequest
name|request
init|=
name|RegisterNodeManagerRequest
operator|.
name|newInstance
argument_list|(
name|nodeId
argument_list|,
literal|0
argument_list|,
name|resource
argument_list|,
name|YarnVersionInfo
operator|.
name|getVersion
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|resourceTracker
operator|.
name|registerNodeManager
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|waitForNodeManagerToConnect
argument_list|(
literal|10000
argument_list|,
name|nodeId
argument_list|)
argument_list|)
expr_stmt|;
comment|// restart the failover thread, and make sure nodeHeartbeat works
name|failoverThread
operator|=
name|createAndStartFailoverThread
argument_list|()
expr_stmt|;
name|NodeStatus
name|status
init|=
name|NodeStatus
operator|.
name|newInstance
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"localhost"
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|NodeHeartbeatRequest
name|request2
init|=
name|NodeHeartbeatRequest
operator|.
name|newInstance
argument_list|(
name|status
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|resourceTracker
operator|.
name|nodeHeartbeat
argument_list|(
name|request2
argument_list|)
expr_stmt|;
block|}
DECL|method|getRMClient ()
specifier|private
name|ResourceTracker
name|getRMClient
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|ServerRMProxy
operator|.
name|createRMProxy
argument_list|(
name|this
operator|.
name|conf
argument_list|,
name|ResourceTracker
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|waitForNodeManagerToConnect (int timeout, NodeId nodeId)
specifier|private
name|boolean
name|waitForNodeManagerToConnect
parameter_list|(
name|int
name|timeout
parameter_list|,
name|NodeId
name|nodeId
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|timeout
operator|/
literal|100
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|getActiveRM
argument_list|()
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|containsKey
argument_list|(
name|nodeId
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

