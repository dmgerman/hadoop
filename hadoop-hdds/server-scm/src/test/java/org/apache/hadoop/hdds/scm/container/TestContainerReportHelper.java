begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|commons
operator|.
name|lang3
operator|.
name|RandomUtils
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
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|LifeCycleEvent
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
name|ReplicationType
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
name|ReplicationFactor
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Helper methods for testing ContainerReportHandler and  * IncrementalContainerReportHandler.  */
end_comment

begin_class
DECL|class|TestContainerReportHelper
specifier|public
specifier|final
class|class
name|TestContainerReportHelper
block|{
DECL|method|TestContainerReportHelper ()
specifier|private
name|TestContainerReportHelper
parameter_list|()
block|{}
DECL|method|addContainerToContainerManager ( final ContainerManager containerManager, final ContainerInfo container, final Set<ContainerReplica> replicas)
specifier|static
name|void
name|addContainerToContainerManager
parameter_list|(
specifier|final
name|ContainerManager
name|containerManager
parameter_list|,
specifier|final
name|ContainerInfo
name|container
parameter_list|,
specifier|final
name|Set
argument_list|<
name|ContainerReplica
argument_list|>
name|replicas
parameter_list|)
throws|throws
name|ContainerNotFoundException
block|{
name|Mockito
operator|.
name|when
argument_list|(
name|containerManager
operator|.
name|getContainer
argument_list|(
name|container
operator|.
name|containerID
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|containerManager
operator|.
name|getContainerReplicas
argument_list|(
name|container
operator|.
name|containerID
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|replicas
argument_list|)
expr_stmt|;
block|}
DECL|method|mockUpdateContainerReplica ( final ContainerManager containerManager, final ContainerInfo containerInfo, final Set<ContainerReplica> replicas)
specifier|static
name|void
name|mockUpdateContainerReplica
parameter_list|(
specifier|final
name|ContainerManager
name|containerManager
parameter_list|,
specifier|final
name|ContainerInfo
name|containerInfo
parameter_list|,
specifier|final
name|Set
argument_list|<
name|ContainerReplica
argument_list|>
name|replicas
parameter_list|)
throws|throws
name|ContainerNotFoundException
block|{
name|Mockito
operator|.
name|doAnswer
argument_list|(
operator|(
name|Answer
argument_list|<
name|Void
argument_list|>
operator|)
name|invocation
lambda|->
block|{
name|Object
index|[]
name|args
init|=
name|invocation
operator|.
name|getArguments
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|containerInfo
operator|.
name|containerID
argument_list|()
argument_list|)
condition|)
block|{
name|ContainerReplica
name|replica
init|=
operator|(
name|ContainerReplica
operator|)
name|args
index|[
literal|1
index|]
decl_stmt|;
name|replicas
operator|.
name|remove
argument_list|(
name|replica
argument_list|)
expr_stmt|;
name|replicas
operator|.
name|add
argument_list|(
name|replica
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|containerManager
argument_list|)
operator|.
name|updateContainerReplica
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|ContainerID
operator|.
name|class
argument_list|)
argument_list|,
name|Mockito
operator|.
name|any
argument_list|(
name|ContainerReplica
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|mockUpdateContainerState ( final ContainerManager containerManager, final ContainerInfo containerInfo, final LifeCycleEvent event, final LifeCycleState state)
specifier|static
name|void
name|mockUpdateContainerState
parameter_list|(
specifier|final
name|ContainerManager
name|containerManager
parameter_list|,
specifier|final
name|ContainerInfo
name|containerInfo
parameter_list|,
specifier|final
name|LifeCycleEvent
name|event
parameter_list|,
specifier|final
name|LifeCycleState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|Mockito
operator|.
name|doAnswer
argument_list|(
operator|(
name|Answer
argument_list|<
name|LifeCycleState
argument_list|>
operator|)
name|invocation
lambda|->
block|{
name|containerInfo
operator|.
name|setState
argument_list|(
name|state
argument_list|)
expr_stmt|;
return|return
name|containerInfo
operator|.
name|getState
argument_list|()
return|;
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|containerManager
argument_list|)
operator|.
name|updateContainerState
argument_list|(
name|containerInfo
operator|.
name|containerID
argument_list|()
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
DECL|method|getContainer (final LifeCycleState state)
specifier|public
specifier|static
name|ContainerInfo
name|getContainer
parameter_list|(
specifier|final
name|LifeCycleState
name|state
parameter_list|)
block|{
return|return
operator|new
name|ContainerInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setContainerID
argument_list|(
name|RandomUtils
operator|.
name|nextLong
argument_list|()
argument_list|)
operator|.
name|setReplicationType
argument_list|(
name|ReplicationType
operator|.
name|RATIS
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
name|ReplicationFactor
operator|.
name|THREE
argument_list|)
operator|.
name|setState
argument_list|(
name|state
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getReplicas ( final ContainerID containerId, final ContainerReplicaProto.State state, final DatanodeDetails... datanodeDetails)
specifier|static
name|Set
argument_list|<
name|ContainerReplica
argument_list|>
name|getReplicas
parameter_list|(
specifier|final
name|ContainerID
name|containerId
parameter_list|,
specifier|final
name|ContainerReplicaProto
operator|.
name|State
name|state
parameter_list|,
specifier|final
name|DatanodeDetails
modifier|...
name|datanodeDetails
parameter_list|)
block|{
return|return
name|getReplicas
argument_list|(
name|containerId
argument_list|,
name|state
argument_list|,
literal|10000L
argument_list|,
name|datanodeDetails
argument_list|)
return|;
block|}
DECL|method|getReplicas ( final ContainerID containerId, final ContainerReplicaProto.State state, final long sequenceId, final DatanodeDetails... datanodeDetails)
specifier|static
name|Set
argument_list|<
name|ContainerReplica
argument_list|>
name|getReplicas
parameter_list|(
specifier|final
name|ContainerID
name|containerId
parameter_list|,
specifier|final
name|ContainerReplicaProto
operator|.
name|State
name|state
parameter_list|,
specifier|final
name|long
name|sequenceId
parameter_list|,
specifier|final
name|DatanodeDetails
modifier|...
name|datanodeDetails
parameter_list|)
block|{
name|Set
argument_list|<
name|ContainerReplica
argument_list|>
name|replicas
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeDetails
name|datanode
range|:
name|datanodeDetails
control|)
block|{
name|replicas
operator|.
name|add
argument_list|(
name|ContainerReplica
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerID
argument_list|(
name|containerId
argument_list|)
operator|.
name|setContainerState
argument_list|(
name|state
argument_list|)
operator|.
name|setDatanodeDetails
argument_list|(
name|datanode
argument_list|)
operator|.
name|setOriginNodeId
argument_list|(
name|datanode
operator|.
name|getUuid
argument_list|()
argument_list|)
operator|.
name|setSequenceId
argument_list|(
name|sequenceId
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|replicas
return|;
block|}
block|}
end_class

end_unit

