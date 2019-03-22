begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|proto
operator|.
name|HddsProtos
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
name|Pipeline
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
comment|// TODO: Write extensive java doc.
end_comment

begin_comment
comment|// This is the main interface of ContainerManager.
end_comment

begin_comment
comment|/**  * ContainerManager class contains the mapping from a name to a pipeline  * mapping. This is used by SCM when allocating new locations and when  * looking up a key.  */
end_comment

begin_interface
DECL|interface|ContainerManager
specifier|public
interface|interface
name|ContainerManager
extends|extends
name|Closeable
block|{
comment|/**    * Returns all the container Ids managed by ContainerManager.    *    * @return Set of ContainerID    */
DECL|method|getContainerIDs ()
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|getContainerIDs
parameter_list|()
function_decl|;
comment|/**    * Returns all the containers managed by ContainerManager.    *    * @return List of ContainerInfo    */
DECL|method|getContainers ()
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|getContainers
parameter_list|()
function_decl|;
comment|/**    * Returns all the containers which are in the specified state.    *    * @return List of ContainerInfo    */
DECL|method|getContainers (HddsProtos.LifeCycleState state)
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|getContainers
parameter_list|(
name|HddsProtos
operator|.
name|LifeCycleState
name|state
parameter_list|)
function_decl|;
comment|/**    * Returns number of containers in the given,    *  {@link org.apache.hadoop.hdds.protocol.proto.HddsProtos.LifeCycleState}.    *    * @return Number of containers    */
DECL|method|getContainerCountByState (HddsProtos.LifeCycleState state)
name|Integer
name|getContainerCountByState
parameter_list|(
name|HddsProtos
operator|.
name|LifeCycleState
name|state
parameter_list|)
function_decl|;
comment|/**    * Returns the ContainerInfo from the container ID.    *    * @param containerID - ID of container.    * @return - ContainerInfo such as creation state and the pipeline.    * @throws IOException    */
DECL|method|getContainer (ContainerID containerID)
name|ContainerInfo
name|getContainer
parameter_list|(
name|ContainerID
name|containerID
parameter_list|)
throws|throws
name|ContainerNotFoundException
function_decl|;
comment|/**    * Returns containers under certain conditions.    * Search container IDs from start ID(exclusive),    * The max size of the searching range cannot exceed the    * value of count.    *    * @param startContainerID start containerID,>=0,    * start searching at the head if 0.    * @param count count must be>= 0    *              Usually the count will be replace with a very big    *              value instead of being unlimited in case the db is very big.    *    * @return a list of container.    * @throws IOException    */
DECL|method|listContainer (ContainerID startContainerID, int count)
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|listContainer
parameter_list|(
name|ContainerID
name|startContainerID
parameter_list|,
name|int
name|count
parameter_list|)
function_decl|;
comment|/**    * Allocates a new container for a given keyName and replication factor.    *    * @param replicationFactor - replication factor of the container.    * @param owner    * @return - ContainerInfo.    * @throws IOException    */
DECL|method|allocateContainer (HddsProtos.ReplicationType type, HddsProtos.ReplicationFactor replicationFactor, String owner)
name|ContainerInfo
name|allocateContainer
parameter_list|(
name|HddsProtos
operator|.
name|ReplicationType
name|type
parameter_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
name|replicationFactor
parameter_list|,
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes a container from SCM.    *    * @param containerID - Container ID    * @throws IOException    */
DECL|method|deleteContainer (ContainerID containerID)
name|void
name|deleteContainer
parameter_list|(
name|ContainerID
name|containerID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Update container state.    * @param containerID - Container ID    * @param event - container life cycle event    * @return - new container state    * @throws IOException    */
DECL|method|updateContainerState (ContainerID containerID, HddsProtos.LifeCycleEvent event)
name|HddsProtos
operator|.
name|LifeCycleState
name|updateContainerState
parameter_list|(
name|ContainerID
name|containerID
parameter_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
name|event
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the latest list of replicas for given containerId.    *    * @param containerID Container ID    * @return Set of ContainerReplica    */
DECL|method|getContainerReplicas (ContainerID containerID)
name|Set
argument_list|<
name|ContainerReplica
argument_list|>
name|getContainerReplicas
parameter_list|(
name|ContainerID
name|containerID
parameter_list|)
throws|throws
name|ContainerNotFoundException
function_decl|;
comment|/**    * Adds a container Replica for the given Container.    *    * @param containerID Container ID    * @param replica ContainerReplica    */
DECL|method|updateContainerReplica (ContainerID containerID, ContainerReplica replica)
name|void
name|updateContainerReplica
parameter_list|(
name|ContainerID
name|containerID
parameter_list|,
name|ContainerReplica
name|replica
parameter_list|)
throws|throws
name|ContainerNotFoundException
function_decl|;
comment|/**    * Remove a container Replica form a given Container.    *    * @param containerID Container ID    * @param replica ContainerReplica    * @return True of dataNode is removed successfully else false.    */
DECL|method|removeContainerReplica (ContainerID containerID, ContainerReplica replica)
name|void
name|removeContainerReplica
parameter_list|(
name|ContainerID
name|containerID
parameter_list|,
name|ContainerReplica
name|replica
parameter_list|)
throws|throws
name|ContainerNotFoundException
throws|,
name|ContainerReplicaNotFoundException
function_decl|;
comment|/**    * Update deleteTransactionId according to deleteTransactionMap.    *    * @param deleteTransactionMap Maps the containerId to latest delete    *                             transaction id for the container.    * @throws IOException    */
DECL|method|updateDeleteTransactionId (Map<Long, Long> deleteTransactionMap)
name|void
name|updateDeleteTransactionId
parameter_list|(
name|Map
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|deleteTransactionMap
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns ContainerInfo which matches the requirements.    * @param size - the amount of space required in the container    * @param owner - the user which requires space in its owned container    * @param pipeline - pipeline to which the container should belong    * @return ContainerInfo for the matching container.    */
DECL|method|getMatchingContainer (long size, String owner, Pipeline pipeline)
name|ContainerInfo
name|getMatchingContainer
parameter_list|(
name|long
name|size
parameter_list|,
name|String
name|owner
parameter_list|,
name|Pipeline
name|pipeline
parameter_list|)
function_decl|;
comment|/**    * Returns ContainerInfo which matches the requirements.    * @param size - the amount of space required in the container    * @param owner - the user which requires space in its owned container    * @param pipeline - pipeline to which the container should belong.    * @param excludedContainerIDS - containerIds to be excluded.    * @return ContainerInfo for the matching container.    */
DECL|method|getMatchingContainer (long size, String owner, Pipeline pipeline, List<ContainerID> excludedContainerIDS)
name|ContainerInfo
name|getMatchingContainer
parameter_list|(
name|long
name|size
parameter_list|,
name|String
name|owner
parameter_list|,
name|Pipeline
name|pipeline
parameter_list|,
name|List
argument_list|<
name|ContainerID
argument_list|>
name|excludedContainerIDS
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

