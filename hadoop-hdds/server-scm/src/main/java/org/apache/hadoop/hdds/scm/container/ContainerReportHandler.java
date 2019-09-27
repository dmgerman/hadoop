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

begin_comment
comment|/**  * Handles container reports from datanode.  */
end_comment

begin_class
DECL|class|ContainerReportHandler
specifier|public
class|class
name|ContainerReportHandler
extends|extends
name|AbstractContainerReportHandler
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
DECL|field|containerManager
specifier|private
specifier|final
name|ContainerManager
name|containerManager
decl_stmt|;
comment|/**    * Constructs ContainerReportHandler instance with the    * given NodeManager and ContainerManager instance.    *    * @param nodeManager NodeManager instance    * @param containerManager ContainerManager instance    */
DECL|method|ContainerReportHandler (final NodeManager nodeManager, final ContainerManager containerManager)
specifier|public
name|ContainerReportHandler
parameter_list|(
specifier|final
name|NodeManager
name|nodeManager
parameter_list|,
specifier|final
name|ContainerManager
name|containerManager
parameter_list|)
block|{
name|super
argument_list|(
name|containerManager
argument_list|,
name|LOG
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
name|containerManager
operator|=
name|containerManager
expr_stmt|;
block|}
comment|/**    * Process the container reports from datanodes.    *    * @param reportFromDatanode Container Report    * @param publisher EventPublisher reference    */
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
specifier|final
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|containersInSCM
init|=
name|nodeManager
operator|.
name|getContainers
argument_list|(
name|datanodeDetails
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|containersInDn
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
name|containersInSCM
argument_list|)
decl_stmt|;
name|missingReplicas
operator|.
name|removeAll
argument_list|(
name|containersInDn
argument_list|)
expr_stmt|;
name|processContainerReplicas
argument_list|(
name|datanodeDetails
argument_list|,
name|replicas
argument_list|)
expr_stmt|;
name|processMissingReplicas
argument_list|(
name|datanodeDetails
argument_list|,
name|missingReplicas
argument_list|)
expr_stmt|;
name|updateDeleteTransaction
argument_list|(
name|datanodeDetails
argument_list|,
name|replicas
argument_list|,
name|publisher
argument_list|)
expr_stmt|;
comment|/*        * Update the latest set of containers for this datanode in        * NodeManager        */
name|nodeManager
operator|.
name|setContainers
argument_list|(
name|datanodeDetails
argument_list|,
name|containersInDn
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|notifyContainerReportProcessing
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NodeNotFoundException
name|ex
parameter_list|)
block|{
name|containerManager
operator|.
name|notifyContainerReportProcessing
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Received container report from unknown datanode {} {}"
argument_list|,
name|datanodeDetails
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Processes the ContainerReport.    *    * @param datanodeDetails Datanode from which this report was received    * @param replicas list of ContainerReplicaProto    */
DECL|method|processContainerReplicas (final DatanodeDetails datanodeDetails, final List<ContainerReplicaProto> replicas)
specifier|private
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
parameter_list|)
block|{
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
name|processContainerReplica
argument_list|(
name|datanodeDetails
argument_list|,
name|replicaProto
argument_list|)
expr_stmt|;
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
literal|"Received container report for an unknown container"
operator|+
literal|" {} from datanode {}."
argument_list|,
name|replicaProto
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|datanodeDetails
argument_list|,
name|e
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
literal|" {} from datanode {}."
argument_list|,
name|replicaProto
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|datanodeDetails
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Process the missing replica on the given datanode.    *    * @param datanodeDetails DatanodeDetails    * @param missingReplicas ContainerID which are missing on the given datanode    */
DECL|method|processMissingReplicas (final DatanodeDetails datanodeDetails, final Set<ContainerID> missingReplicas)
specifier|private
name|void
name|processMissingReplicas
parameter_list|(
specifier|final
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
specifier|final
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|missingReplicas
parameter_list|)
block|{
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
name|ignored
parameter_list|)
block|{
comment|// This should not happen, but even if it happens, not an issue
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
literal|"Cannot remove container replica, container {} not found."
argument_list|,
name|id
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
unit|}
comment|/**    * Updates the Delete Transaction Id for the given datanode.    *    * @param datanodeDetails DatanodeDetails    * @param replicas List of ContainerReplicaProto    * @param publisher EventPublisher reference    */
end_comment

begin_function
DECL|method|updateDeleteTransaction (final DatanodeDetails datanodeDetails, final List<ContainerReplicaProto> replicas, final EventPublisher publisher)
unit|private
name|void
name|updateDeleteTransaction
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
name|replica
range|:
name|replicas
control|)
block|{
try|try
block|{
specifier|final
name|ContainerInfo
name|containerInfo
init|=
name|containerManager
operator|.
name|getContainer
argument_list|(
name|ContainerID
operator|.
name|valueof
argument_list|(
name|replica
operator|.
name|getContainerID
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerInfo
operator|.
name|getDeleteTransactionId
argument_list|()
operator|>
name|replica
operator|.
name|getDeleteTransactionId
argument_list|()
condition|)
block|{
name|pendingDeleteStatusList
operator|.
name|addPendingDeleteStatus
argument_list|(
name|replica
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
name|cnfe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot update pending delete transaction for "
operator|+
literal|"container #{}. Reason: container missing."
argument_list|,
name|replica
operator|.
name|getContainerID
argument_list|()
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

unit|}
end_unit

