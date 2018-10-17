begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container.replication
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
operator|.
name|replication
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
name|ArrayList
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
name|Objects
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
name|concurrent
operator|.
name|ThreadFactory
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
name|HddsProtos
operator|.
name|LifeCycleState
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
name|container
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
name|scm
operator|.
name|container
operator|.
name|ContainerManager
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
name|ContainerReplica
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
name|placement
operator|.
name|algorithms
operator|.
name|ContainerPlacementPolicy
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
name|EventQueue
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
name|IdentifiableEventPayload
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
name|lease
operator|.
name|LeaseManager
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
name|ReplicateContainerCommand
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
name|annotations
operator|.
name|VisibleForTesting
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
import|;
end_import

begin_import
import|import static
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
operator|.
name|TRACK_REPLICATE_COMMAND
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
comment|/**  * Replication Manager manages the replication of the closed container.  */
end_comment

begin_class
DECL|class|ReplicationManager
specifier|public
class|class
name|ReplicationManager
implements|implements
name|Runnable
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
name|ReplicationManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|replicationQueue
specifier|private
name|ReplicationQueue
name|replicationQueue
decl_stmt|;
DECL|field|containerPlacement
specifier|private
name|ContainerPlacementPolicy
name|containerPlacement
decl_stmt|;
DECL|field|eventPublisher
specifier|private
name|EventPublisher
name|eventPublisher
decl_stmt|;
DECL|field|replicationCommandWatcher
specifier|private
name|ReplicationCommandWatcher
name|replicationCommandWatcher
decl_stmt|;
DECL|field|running
specifier|private
name|boolean
name|running
init|=
literal|true
decl_stmt|;
DECL|field|containerManager
specifier|private
name|ContainerManager
name|containerManager
decl_stmt|;
DECL|method|ReplicationManager (ContainerPlacementPolicy containerPlacement, ContainerManager containerManager, EventQueue eventQueue, LeaseManager<Long> commandWatcherLeaseManager)
specifier|public
name|ReplicationManager
parameter_list|(
name|ContainerPlacementPolicy
name|containerPlacement
parameter_list|,
name|ContainerManager
name|containerManager
parameter_list|,
name|EventQueue
name|eventQueue
parameter_list|,
name|LeaseManager
argument_list|<
name|Long
argument_list|>
name|commandWatcherLeaseManager
parameter_list|)
block|{
name|this
operator|.
name|containerPlacement
operator|=
name|containerPlacement
expr_stmt|;
name|this
operator|.
name|containerManager
operator|=
name|containerManager
expr_stmt|;
name|this
operator|.
name|eventPublisher
operator|=
name|eventQueue
expr_stmt|;
name|this
operator|.
name|replicationCommandWatcher
operator|=
operator|new
name|ReplicationCommandWatcher
argument_list|(
name|TRACK_REPLICATE_COMMAND
argument_list|,
name|SCMEvents
operator|.
name|REPLICATION_COMPLETE
argument_list|,
name|commandWatcherLeaseManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|replicationQueue
operator|=
operator|new
name|ReplicationQueue
argument_list|()
expr_stmt|;
name|eventQueue
operator|.
name|addHandler
argument_list|(
name|SCMEvents
operator|.
name|REPLICATE_CONTAINER
argument_list|,
parameter_list|(
name|replicationRequest
parameter_list|,
name|publisher
parameter_list|)
lambda|->
name|replicationQueue
operator|.
name|add
argument_list|(
name|replicationRequest
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|replicationCommandWatcher
operator|.
name|start
argument_list|(
name|eventQueue
argument_list|)
expr_stmt|;
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|ThreadFactory
name|threadFactory
init|=
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|setNameFormat
argument_list|(
literal|"Replication Manager"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|threadFactory
operator|.
name|newThread
argument_list|(
name|this
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|running
condition|)
block|{
name|ReplicationRequest
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|//TODO: add throttling here
name|request
operator|=
name|replicationQueue
operator|.
name|take
argument_list|()
expr_stmt|;
name|ContainerID
name|containerID
init|=
operator|new
name|ContainerID
argument_list|(
name|request
operator|.
name|getContainerId
argument_list|()
argument_list|)
decl_stmt|;
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
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerInfo
argument_list|,
literal|"No information about the container "
operator|+
name|request
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|containerInfo
operator|.
name|getState
argument_list|()
operator|==
name|LifeCycleState
operator|.
name|CLOSED
argument_list|,
literal|"Container should be in closed state"
argument_list|)
expr_stmt|;
comment|//check the current replication
name|List
argument_list|<
name|ContainerReplica
argument_list|>
name|containerReplicas
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|getCurrentReplicas
argument_list|(
name|request
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerReplicas
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Container {} should be replicated but can't find any existing "
operator|+
literal|"replicas"
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
return|return;
block|}
name|ReplicationRequest
name|finalRequest
init|=
name|request
decl_stmt|;
name|int
name|inFlightReplications
init|=
name|replicationCommandWatcher
operator|.
name|getTimeoutEvents
argument_list|(
name|e
lambda|->
name|e
operator|.
name|request
operator|.
name|getContainerId
argument_list|()
operator|==
name|finalRequest
operator|.
name|getContainerId
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|deficit
init|=
name|request
operator|.
name|getExpecReplicationCount
argument_list|()
operator|-
name|containerReplicas
operator|.
name|size
argument_list|()
operator|-
name|inFlightReplications
decl_stmt|;
if|if
condition|(
name|deficit
operator|>
literal|0
condition|)
block|{
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|datanodes
init|=
name|containerReplicas
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|ContainerReplica
operator|::
name|getDatanodeDetails
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|selectedDatanodes
init|=
name|containerPlacement
operator|.
name|chooseDatanodes
argument_list|(
name|datanodes
argument_list|,
name|deficit
argument_list|,
name|containerInfo
operator|.
name|getUsedBytes
argument_list|()
argument_list|)
decl_stmt|;
comment|//send the command
for|for
control|(
name|DatanodeDetails
name|datanode
range|:
name|selectedDatanodes
control|)
block|{
name|ReplicateContainerCommand
name|replicateCommand
init|=
operator|new
name|ReplicateContainerCommand
argument_list|(
name|containerID
operator|.
name|getId
argument_list|()
argument_list|,
name|datanodes
argument_list|)
decl_stmt|;
name|eventPublisher
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|DATANODE_COMMAND
argument_list|,
operator|new
name|CommandForDatanode
argument_list|<>
argument_list|(
name|datanode
operator|.
name|getUuid
argument_list|()
argument_list|,
name|replicateCommand
argument_list|)
argument_list|)
expr_stmt|;
name|ReplicationRequestToRepeat
name|timeoutEvent
init|=
operator|new
name|ReplicationRequestToRepeat
argument_list|(
name|replicateCommand
operator|.
name|getId
argument_list|()
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|eventPublisher
operator|.
name|fireEvent
argument_list|(
name|TRACK_REPLICATE_COMMAND
argument_list|,
name|timeoutEvent
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|deficit
operator|<
literal|0
condition|)
block|{
comment|//TODO: too many replicas. Not handled yet.
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Can't replicate container {}"
argument_list|,
name|request
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getCurrentReplicas (ReplicationRequest request)
specifier|protected
name|Set
argument_list|<
name|ContainerReplica
argument_list|>
name|getCurrentReplicas
parameter_list|(
name|ReplicationRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|containerManager
operator|.
name|getContainerReplicas
argument_list|(
operator|new
name|ContainerID
argument_list|(
name|request
operator|.
name|getContainerId
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getReplicationQueue ()
specifier|public
name|ReplicationQueue
name|getReplicationQueue
parameter_list|()
block|{
return|return
name|replicationQueue
return|;
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|running
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * Event for the ReplicationCommandWatcher to repeate the embedded request.    * in case fof timeout.    */
DECL|class|ReplicationRequestToRepeat
specifier|public
specifier|static
class|class
name|ReplicationRequestToRepeat
implements|implements
name|IdentifiableEventPayload
block|{
DECL|field|commandId
specifier|private
specifier|final
name|long
name|commandId
decl_stmt|;
DECL|field|request
specifier|private
specifier|final
name|ReplicationRequest
name|request
decl_stmt|;
DECL|method|ReplicationRequestToRepeat (long commandId, ReplicationRequest request)
specifier|public
name|ReplicationRequestToRepeat
parameter_list|(
name|long
name|commandId
parameter_list|,
name|ReplicationRequest
name|request
parameter_list|)
block|{
name|this
operator|.
name|commandId
operator|=
name|commandId
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
block|}
DECL|method|getRequest ()
specifier|public
name|ReplicationRequest
name|getRequest
parameter_list|()
block|{
return|return
name|request
return|;
block|}
annotation|@
name|Override
DECL|method|getId ()
specifier|public
name|long
name|getId
parameter_list|()
block|{
return|return
name|commandId
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ReplicationRequestToRepeat
name|that
init|=
operator|(
name|ReplicationRequestToRepeat
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|request
argument_list|,
name|that
operator|.
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|request
argument_list|)
return|;
block|}
block|}
comment|/**    * Add javadoc.    */
DECL|class|ReplicationCompleted
specifier|public
specifier|static
class|class
name|ReplicationCompleted
implements|implements
name|IdentifiableEventPayload
block|{
DECL|field|uuid
specifier|private
specifier|final
name|long
name|uuid
decl_stmt|;
DECL|method|ReplicationCompleted (long uuid)
specifier|public
name|ReplicationCompleted
parameter_list|(
name|long
name|uuid
parameter_list|)
block|{
name|this
operator|.
name|uuid
operator|=
name|uuid
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getId ()
specifier|public
name|long
name|getId
parameter_list|()
block|{
return|return
name|uuid
return|;
block|}
block|}
block|}
end_class

end_unit

