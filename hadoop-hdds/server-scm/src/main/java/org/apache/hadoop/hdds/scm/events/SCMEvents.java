begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.events
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|events
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
name|scm
operator|.
name|block
operator|.
name|PendingDeleteStatusList
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
name|scm
operator|.
name|command
operator|.
name|CommandStatusReportHandler
operator|.
name|CloseContainerStatus
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
name|scm
operator|.
name|command
operator|.
name|CommandStatusReportHandler
operator|.
name|DeleteBlockCommandStatus
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
name|scm
operator|.
name|command
operator|.
name|CommandStatusReportHandler
operator|.
name|ReplicationStatus
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
name|scm
operator|.
name|container
operator|.
name|CloseContainerEventHandler
operator|.
name|CloseContainerRetryableReq
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
name|scm
operator|.
name|container
operator|.
name|ContainerID
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
name|scm
operator|.
name|server
operator|.
name|SCMDatanodeHeartbeatDispatcher
operator|.
name|ContainerActionsFromDatanode
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
name|scm
operator|.
name|server
operator|.
name|SCMDatanodeHeartbeatDispatcher
operator|.
name|CommandStatusReportFromDatanode
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
name|scm
operator|.
name|server
operator|.
name|SCMDatanodeHeartbeatDispatcher
operator|.
name|ContainerReportFromDatanode
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
name|scm
operator|.
name|server
operator|.
name|SCMDatanodeHeartbeatDispatcher
operator|.
name|NodeReportFromDatanode
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
name|scm
operator|.
name|container
operator|.
name|replication
operator|.
name|ReplicationManager
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
name|scm
operator|.
name|container
operator|.
name|replication
operator|.
name|ReplicationManager
operator|.
name|ReplicationCompleted
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
name|scm
operator|.
name|container
operator|.
name|replication
operator|.
name|ReplicationRequest
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
name|server
operator|.
name|events
operator|.
name|Event
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
name|server
operator|.
name|events
operator|.
name|TypedEvent
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
name|protocol
operator|.
name|commands
operator|.
name|CommandForDatanode
import|;
end_import

begin_comment
comment|/**  * Class that acts as the namespace for all SCM Events.  */
end_comment

begin_class
DECL|class|SCMEvents
specifier|public
specifier|final
class|class
name|SCMEvents
block|{
comment|/**    * NodeReports are  sent out by Datanodes. This report is received by    * SCMDatanodeHeartbeatDispatcher and NodeReport Event is generated.    */
DECL|field|NODE_REPORT
specifier|public
specifier|static
specifier|final
name|TypedEvent
argument_list|<
name|NodeReportFromDatanode
argument_list|>
name|NODE_REPORT
init|=
operator|new
name|TypedEvent
argument_list|<>
argument_list|(
name|NodeReportFromDatanode
operator|.
name|class
argument_list|,
literal|"Node_Report"
argument_list|)
decl_stmt|;
comment|/**    * ContainerReports are send out by Datanodes. This report is received by    * SCMDatanodeHeartbeatDispatcher and Container_Report Event    * isTestSCMDatanodeHeartbeatDispatcher generated.    */
DECL|field|CONTAINER_REPORT
specifier|public
specifier|static
specifier|final
name|TypedEvent
argument_list|<
name|ContainerReportFromDatanode
argument_list|>
name|CONTAINER_REPORT
init|=
operator|new
name|TypedEvent
argument_list|<>
argument_list|(
name|ContainerReportFromDatanode
operator|.
name|class
argument_list|,
literal|"Container_Report"
argument_list|)
decl_stmt|;
comment|/**    * ContainerActions are sent by Datanode. This event is received by    * SCMDatanodeHeartbeatDispatcher and CONTAINER_ACTIONS event is generated.    */
specifier|public
specifier|static
specifier|final
name|TypedEvent
argument_list|<
name|ContainerActionsFromDatanode
argument_list|>
DECL|field|CONTAINER_ACTIONS
name|CONTAINER_ACTIONS
init|=
operator|new
name|TypedEvent
argument_list|<>
argument_list|(
name|ContainerActionsFromDatanode
operator|.
name|class
argument_list|,
literal|"Container_Actions"
argument_list|)
decl_stmt|;
comment|/**    * A Command status report will be sent by datanodes. This repoort is received    * by SCMDatanodeHeartbeatDispatcher and CommandReport event is generated.    */
specifier|public
specifier|static
specifier|final
name|TypedEvent
argument_list|<
name|CommandStatusReportFromDatanode
argument_list|>
DECL|field|CMD_STATUS_REPORT
name|CMD_STATUS_REPORT
init|=
operator|new
name|TypedEvent
argument_list|<>
argument_list|(
name|CommandStatusReportFromDatanode
operator|.
name|class
argument_list|,
literal|"Cmd_Status_Report"
argument_list|)
decl_stmt|;
comment|/**    * When ever a command for the Datanode needs to be issued by any component    * inside SCM, a Datanode_Command event is generated. NodeManager listens to    * these events and dispatches them to Datanode for further processing.    */
DECL|field|DATANODE_COMMAND
specifier|public
specifier|static
specifier|final
name|Event
argument_list|<
name|CommandForDatanode
argument_list|>
name|DATANODE_COMMAND
init|=
operator|new
name|TypedEvent
argument_list|<>
argument_list|(
name|CommandForDatanode
operator|.
name|class
argument_list|,
literal|"Datanode_Command"
argument_list|)
decl_stmt|;
comment|/**    * A Close Container Event can be triggered under many condition. Some of them    * are: 1. A Container is full, then we stop writing further information to    * that container. DN's let SCM know that current state and sends a    * informational message that allows SCM to close the container.    *<p>    * 2. If a pipeline is open; for example Ratis; if a single node fails, we    * will proactively close these containers.    *<p>    * Once a command is dispatched to DN, we will also listen to updates from the    * datanode which lets us know that this command completed or timed out.    */
DECL|field|CLOSE_CONTAINER
specifier|public
specifier|static
specifier|final
name|TypedEvent
argument_list|<
name|ContainerID
argument_list|>
name|CLOSE_CONTAINER
init|=
operator|new
name|TypedEvent
argument_list|<>
argument_list|(
name|ContainerID
operator|.
name|class
argument_list|,
literal|"Close_Container"
argument_list|)
decl_stmt|;
comment|/**    * A CLOSE_CONTAINER_RETRYABLE_REQ will be triggered by    * CloseContainerEventHandler after sending a SCMCommand to DataNode.    * CloseContainerWatcher will track this event. Watcher will be responsible    * for retrying it in event of failure or timeout.    */
specifier|public
specifier|static
specifier|final
name|TypedEvent
argument_list|<
name|CloseContainerRetryableReq
argument_list|>
DECL|field|CLOSE_CONTAINER_RETRYABLE_REQ
name|CLOSE_CONTAINER_RETRYABLE_REQ
init|=
operator|new
name|TypedEvent
argument_list|<>
argument_list|(
name|CloseContainerRetryableReq
operator|.
name|class
argument_list|,
literal|"Close_Container_Retryable"
argument_list|)
decl_stmt|;
comment|/**    * This event will be triggered whenever a new datanode is registered with    * SCM.    */
DECL|field|NEW_NODE
specifier|public
specifier|static
specifier|final
name|TypedEvent
argument_list|<
name|DatanodeDetails
argument_list|>
name|NEW_NODE
init|=
operator|new
name|TypedEvent
argument_list|<>
argument_list|(
name|DatanodeDetails
operator|.
name|class
argument_list|,
literal|"New_Node"
argument_list|)
decl_stmt|;
comment|/**    * This event will be triggered whenever a datanode is moved from healthy to    * stale state.    */
DECL|field|STALE_NODE
specifier|public
specifier|static
specifier|final
name|TypedEvent
argument_list|<
name|DatanodeDetails
argument_list|>
name|STALE_NODE
init|=
operator|new
name|TypedEvent
argument_list|<>
argument_list|(
name|DatanodeDetails
operator|.
name|class
argument_list|,
literal|"Stale_Node"
argument_list|)
decl_stmt|;
comment|/**    * This event will be triggered whenever a datanode is moved from stale to    * dead state.    */
DECL|field|DEAD_NODE
specifier|public
specifier|static
specifier|final
name|TypedEvent
argument_list|<
name|DatanodeDetails
argument_list|>
name|DEAD_NODE
init|=
operator|new
name|TypedEvent
argument_list|<>
argument_list|(
name|DatanodeDetails
operator|.
name|class
argument_list|,
literal|"Dead_Node"
argument_list|)
decl_stmt|;
comment|/**    * This event will be triggered by CommandStatusReportHandler whenever a    * status for Replication SCMCommand is received.    */
DECL|field|REPLICATION_STATUS
specifier|public
specifier|static
specifier|final
name|Event
argument_list|<
name|ReplicationStatus
argument_list|>
name|REPLICATION_STATUS
init|=
operator|new
name|TypedEvent
argument_list|<>
argument_list|(
name|ReplicationStatus
operator|.
name|class
argument_list|,
literal|"ReplicateCommandStatus"
argument_list|)
decl_stmt|;
comment|/**    * This event will be triggered by CommandStatusReportHandler whenever a    * status for CloseContainer SCMCommand is received.    */
specifier|public
specifier|static
specifier|final
name|Event
argument_list|<
name|CloseContainerStatus
argument_list|>
DECL|field|CLOSE_CONTAINER_STATUS
name|CLOSE_CONTAINER_STATUS
init|=
operator|new
name|TypedEvent
argument_list|<>
argument_list|(
name|CloseContainerStatus
operator|.
name|class
argument_list|,
literal|"CloseContainerCommandStatus"
argument_list|)
decl_stmt|;
comment|/**    * This event will be triggered by CommandStatusReportHandler whenever a    * status for DeleteBlock SCMCommand is received.    */
specifier|public
specifier|static
specifier|final
name|Event
argument_list|<
name|DeleteBlockCommandStatus
argument_list|>
DECL|field|DELETE_BLOCK_STATUS
name|DELETE_BLOCK_STATUS
init|=
operator|new
name|TypedEvent
argument_list|(
name|DeleteBlockCommandStatus
operator|.
name|class
argument_list|,
literal|"DeleteBlockCommandStatus"
argument_list|)
decl_stmt|;
comment|/**    * This event will be triggered while processing container reports from DN    * when deleteTransactionID of container in report mismatches with the    * deleteTransactionID on SCM.    */
DECL|field|PENDING_DELETE_STATUS
specifier|public
specifier|static
specifier|final
name|Event
argument_list|<
name|PendingDeleteStatusList
argument_list|>
name|PENDING_DELETE_STATUS
init|=
operator|new
name|TypedEvent
argument_list|(
name|PendingDeleteStatusList
operator|.
name|class
argument_list|,
literal|"PendingDeleteStatus"
argument_list|)
decl_stmt|;
comment|/**    * This is the command for ReplicationManager to handle under/over    * replication. Sent by the ContainerReportHandler after processing the    * heartbeat.    */
DECL|field|REPLICATE_CONTAINER
specifier|public
specifier|static
specifier|final
name|TypedEvent
argument_list|<
name|ReplicationRequest
argument_list|>
name|REPLICATE_CONTAINER
init|=
operator|new
name|TypedEvent
argument_list|<>
argument_list|(
name|ReplicationRequest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * This event is sent by the ReplicaManager to the    * ReplicationCommandWatcher to track the in-progress replication.    */
specifier|public
specifier|static
specifier|final
name|TypedEvent
argument_list|<
name|ReplicationManager
operator|.
name|ReplicationRequestToRepeat
argument_list|>
DECL|field|TRACK_REPLICATE_COMMAND
name|TRACK_REPLICATE_COMMAND
init|=
operator|new
name|TypedEvent
argument_list|<>
argument_list|(
name|ReplicationManager
operator|.
name|ReplicationRequestToRepeat
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * This event comes from the Heartbeat dispatcher (in fact from the    * datanode) to notify the scm that the replication is done. This is    * received by the replicate command watcher to mark in-progress task as    * finished.<p>    * TODO: Temporary event, should be replaced by specific Heartbeat    * ActionRequred event.    */
DECL|field|REPLICATION_COMPLETE
specifier|public
specifier|static
specifier|final
name|TypedEvent
argument_list|<
name|ReplicationCompleted
argument_list|>
name|REPLICATION_COMPLETE
init|=
operator|new
name|TypedEvent
argument_list|<>
argument_list|(
name|ReplicationCompleted
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Signal for all the components (but especially for the replication    * manager and container report handler) that the replication could be    * started. Should be send only if (almost) all the container state are    * available from the datanodes.    */
DECL|field|START_REPLICATION
specifier|public
specifier|static
specifier|final
name|TypedEvent
argument_list|<
name|Boolean
argument_list|>
name|START_REPLICATION
init|=
operator|new
name|TypedEvent
argument_list|<>
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Private Ctor. Never Constructed.    */
DECL|method|SCMEvents ()
specifier|private
name|SCMEvents
parameter_list|()
block|{   }
block|}
end_class

end_unit

