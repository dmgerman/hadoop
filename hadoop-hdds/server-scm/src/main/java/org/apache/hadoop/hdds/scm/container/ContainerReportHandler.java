begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container
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
name|container
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
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|ContainerReplicaProto
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
name|container
operator|.
name|replication
operator|.
name|ReplicationActivityStatus
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
name|scm
operator|.
name|events
operator|.
name|SCMEvents
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
name|node
operator|.
name|NodeManager
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
name|node
operator|.
name|states
operator|.
name|NodeNotFoundException
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
name|pipeline
operator|.
name|PipelineManager
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
name|server
operator|.
name|events
operator|.
name|EventHandler
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
name|EventPublisher
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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

begin_comment
comment|/**  * Handles container reports from datanode.  */
end_comment

begin_class
DECL|class|ContainerReportHandler
specifier|public
class|class
name|ContainerReportHandler
implements|implements
name|EventHandler
argument_list|<
name|ContainerReportFromDatanode
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ContainerReportHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|nodeManager
specifier|private
specifier|final
name|NodeManager
name|nodeManager
decl_stmt|;
DECL|field|pipelineManager
specifier|private
specifier|final
name|PipelineManager
name|pipelineManager
decl_stmt|;
DECL|field|containerManager
specifier|private
specifier|final
name|ContainerManager
name|containerManager
decl_stmt|;
DECL|field|replicationStatus
specifier|private
specifier|final
name|ReplicationActivityStatus
name|replicationStatus
decl_stmt|;
DECL|method|ContainerReportHandler (final NodeManager nodeManager, final PipelineManager pipelineManager, final ContainerManager containerManager, final ReplicationActivityStatus replicationActivityStatus)
specifier|public
name|ContainerReportHandler
parameter_list|(
specifier|final
name|NodeManager
name|nodeManager
parameter_list|,
specifier|final
name|PipelineManager
name|pipelineManager
parameter_list|,
specifier|final
name|ContainerManager
name|containerManager
parameter_list|,
specifier|final
name|ReplicationActivityStatus
name|replicationActivityStatus
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|nodeManager
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipelineManager
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerManager
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|replicationActivityStatus
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeManager
operator|=
name|nodeManager
expr_stmt|;
name|this
operator|.
name|pipelineManager
operator|=
name|pipelineManager
expr_stmt|;
name|this
operator|.
name|containerManager
operator|=
name|containerManager
expr_stmt|;
name|this
operator|.
name|replicationStatus
operator|=
name|replicationActivityStatus
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onMessage (final ContainerReportFromDatanode reportFromDatanode, final EventPublisher publisher)
specifier|public
name|void
name|onMessage
parameter_list|(
specifier|final
name|ContainerReportFromDatanode
name|reportFromDatanode
parameter_list|,
specifier|final
name|EventPublisher
name|publisher
parameter_list|)
block|{
specifier|final
name|DatanodeDetails
name|datanodeDetails
init|=
name|reportFromDatanode
operator|.
name|getDatanodeDetails
argument_list|()
decl_stmt|;
specifier|final
name|ContainerReportsProto
name|containerReport
init|=
name|reportFromDatanode
operator|.
name|getReport
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|List
argument_list|<
name|ContainerReplicaProto
argument_list|>
name|replicas
init|=
name|containerReport
operator|.
name|getReportsList
argument_list|()
decl_stmt|;
comment|// ContainerIDs which SCM expects this datanode to have.
specifier|final
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|expectedContainerIDs
init|=
name|nodeManager
operator|.
name|getContainers
argument_list|(
name|datanodeDetails
argument_list|)
decl_stmt|;
comment|// ContainerIDs that this datanode actually has.
specifier|final
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|actualContainerIDs
init|=
name|replicas
operator|.
name|parallelStream
argument_list|()
operator|.
name|map
argument_list|(
name|ContainerReplicaProto
operator|::
name|getContainerID
argument_list|)
operator|.
name|map
argument_list|(
name|ContainerID
operator|::
name|valueof
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
comment|// Container replicas which SCM is not aware of.
specifier|final
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|newReplicas
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|actualContainerIDs
argument_list|)
decl_stmt|;
name|newReplicas
operator|.
name|removeAll
argument_list|(
name|expectedContainerIDs
argument_list|)
expr_stmt|;
comment|// Container replicas which are missing from datanode.
specifier|final
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|missingReplicas
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|expectedContainerIDs
argument_list|)
decl_stmt|;
name|missingReplicas
operator|.
name|removeAll
argument_list|(
name|actualContainerIDs
argument_list|)
expr_stmt|;
name|processContainerReplicas
argument_list|(
name|datanodeDetails
argument_list|,
name|replicas
argument_list|,
name|publisher
argument_list|)
expr_stmt|;
comment|// Remove missing replica from ContainerManager
for|for
control|(
name|ContainerID
name|id
range|:
name|missingReplicas
control|)
block|{
try|try
block|{
name|containerManager
operator|.
name|getContainerReplicas
argument_list|(
name|id
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|replica
lambda|->
name|replica
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|equals
argument_list|(
name|datanodeDetails
argument_list|)
argument_list|)
operator|.
name|findFirst
argument_list|()
operator|.
name|ifPresent
argument_list|(
name|replica
lambda|->
block|{
lambda|try
block|{
name|containerManager
operator|.
name|removeContainerReplica
argument_list|(
name|id
argument_list|,
name|replica
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ContainerNotFoundException
decl||
name|ContainerReplicaNotFoundException
name|e
parameter_list|)
block|{
comment|// This should not happen, but even if it happens, not an
comment|// issue
block|}
block|}
block|)
empty_stmt|;
block|}
catch|catch
parameter_list|(
name|ContainerNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot remove container replica, container {} not found"
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Update the latest set of containers for this datanode in NodeManager.
name|nodeManager
operator|.
name|setContainers
parameter_list|(
name|datanodeDetails
parameter_list|,
name|actualContainerIDs
parameter_list|)
constructor_decl|;
comment|// Replicate if needed.
name|newReplicas
operator|.
name|forEach
argument_list|(
name|id
lambda|->
name|checkReplicationState
argument_list|(
name|id
argument_list|,
name|publisher
argument_list|)
argument_list|)
expr_stmt|;
name|missingReplicas
operator|.
name|forEach
argument_list|(
name|id
lambda|->
name|checkReplicationState
argument_list|(
name|id
argument_list|,
name|publisher
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_class

begin_catch
catch|catch
parameter_list|(
name|NodeNotFoundException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Received container report from unknown datanode {}"
argument_list|,
name|datanodeDetails
argument_list|)
expr_stmt|;
block|}
end_catch

begin_function
unit|}    private
DECL|method|processContainerReplicas (final DatanodeDetails datanodeDetails, final List<ContainerReplicaProto> replicas, final EventPublisher publisher)
name|void
name|processContainerReplicas
parameter_list|(
specifier|final
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
specifier|final
name|List
argument_list|<
name|ContainerReplicaProto
argument_list|>
name|replicas
parameter_list|,
specifier|final
name|EventPublisher
name|publisher
parameter_list|)
block|{
specifier|final
name|PendingDeleteStatusList
name|pendingDeleteStatusList
init|=
operator|new
name|PendingDeleteStatusList
argument_list|(
name|datanodeDetails
argument_list|)
decl_stmt|;
for|for
control|(
name|ContainerReplicaProto
name|replicaProto
range|:
name|replicas
control|)
block|{
try|try
block|{
specifier|final
name|ContainerID
name|containerID
init|=
name|ContainerID
operator|.
name|valueof
argument_list|(
name|replicaProto
operator|.
name|getContainerID
argument_list|()
argument_list|)
decl_stmt|;
name|ReportHandlerHelper
operator|.
name|processContainerReplica
argument_list|(
name|containerManager
argument_list|,
name|containerID
argument_list|,
name|replicaProto
argument_list|,
name|datanodeDetails
argument_list|,
name|publisher
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
specifier|final
name|ContainerInfo
name|containerInfo
init|=
name|containerManager
operator|.
name|getContainer
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerInfo
operator|.
name|getDeleteTransactionId
argument_list|()
operator|>
name|replicaProto
operator|.
name|getDeleteTransactionId
argument_list|()
condition|)
block|{
name|pendingDeleteStatusList
operator|.
name|addPendingDeleteStatus
argument_list|(
name|replicaProto
operator|.
name|getDeleteTransactionId
argument_list|()
argument_list|,
name|containerInfo
operator|.
name|getDeleteTransactionId
argument_list|()
argument_list|,
name|containerInfo
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ContainerNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Received container report for an unknown container {} from"
operator|+
literal|" datanode {}"
argument_list|,
name|replicaProto
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|datanodeDetails
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while processing container report for container"
operator|+
literal|" {} from datanode {}"
argument_list|,
name|replicaProto
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|datanodeDetails
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|pendingDeleteStatusList
operator|.
name|getNumPendingDeletes
argument_list|()
operator|>
literal|0
condition|)
block|{
name|publisher
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|PENDING_DELETE_STATUS
argument_list|,
name|pendingDeleteStatusList
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
DECL|method|checkReplicationState (ContainerID containerID, EventPublisher publisher)
specifier|private
name|void
name|checkReplicationState
parameter_list|(
name|ContainerID
name|containerID
parameter_list|,
name|EventPublisher
name|publisher
parameter_list|)
block|{
try|try
block|{
name|ContainerInfo
name|container
init|=
name|containerManager
operator|.
name|getContainer
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
name|replicateIfNeeded
argument_list|(
name|container
argument_list|,
name|publisher
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ContainerNotFoundException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Container is missing from containerStateManager. Can't request "
operator|+
literal|"replication. {}"
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
DECL|method|replicateIfNeeded (ContainerInfo container, EventPublisher publisher)
specifier|private
name|void
name|replicateIfNeeded
parameter_list|(
name|ContainerInfo
name|container
parameter_list|,
name|EventPublisher
name|publisher
parameter_list|)
throws|throws
name|ContainerNotFoundException
block|{
if|if
condition|(
operator|!
name|container
operator|.
name|isOpen
argument_list|()
operator|&&
name|replicationStatus
operator|.
name|isReplicationEnabled
argument_list|()
condition|)
block|{
specifier|final
name|int
name|existingReplicas
init|=
name|containerManager
operator|.
name|getContainerReplicas
argument_list|(
name|container
operator|.
name|containerID
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
name|expectedReplicas
init|=
name|container
operator|.
name|getReplicationFactor
argument_list|()
operator|.
name|getNumber
argument_list|()
decl_stmt|;
if|if
condition|(
name|existingReplicas
operator|!=
name|expectedReplicas
condition|)
block|{
name|publisher
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|REPLICATE_CONTAINER
argument_list|,
operator|new
name|ReplicationRequest
argument_list|(
name|container
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|existingReplicas
argument_list|,
name|expectedReplicas
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_function

unit|}
end_unit

