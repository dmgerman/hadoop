begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.client
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
name|client
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
name|classification
operator|.
name|InterfaceStability
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
name|common
operator|.
name|helpers
operator|.
name|ContainerWithPipeline
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
name|pipeline
operator|.
name|Pipeline
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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerDataProto
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

begin_comment
comment|/**  * The interface to call into underlying container layer.  *  * Written as interface to allow easy testing: implement a mock container layer  * for standalone testing of CBlock API without actually calling into remote  * containers. Actual container layer can simply re-implement this.  *  * NOTE this is temporarily needed class. When SCM containers are full-fledged,  * this interface will likely be removed.  */
end_comment

begin_interface
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|ScmClient
specifier|public
interface|interface
name|ScmClient
extends|extends
name|Closeable
block|{
comment|/**    * Creates a Container on SCM and returns the pipeline.    * @return ContainerInfo    * @throws IOException    */
DECL|method|createContainer (String owner)
name|ContainerWithPipeline
name|createContainer
parameter_list|(
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets a container by Name -- Throws if the container does not exist.    * @param containerId - Container ID    * @return Pipeline    * @throws IOException    */
DECL|method|getContainer (long containerId)
name|ContainerInfo
name|getContainer
parameter_list|(
name|long
name|containerId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets a container by Name -- Throws if the container does not exist.    * @param containerId - Container ID    * @return ContainerWithPipeline    * @throws IOException    */
DECL|method|getContainerWithPipeline (long containerId)
name|ContainerWithPipeline
name|getContainerWithPipeline
parameter_list|(
name|long
name|containerId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Close a container.    *    * @param containerId - ID of the container.    * @param pipeline - Pipeline where the container is located.    * @throws IOException    */
DECL|method|closeContainer (long containerId, Pipeline pipeline)
name|void
name|closeContainer
parameter_list|(
name|long
name|containerId
parameter_list|,
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Close a container.    *    * @param containerId - ID of the container.    * @throws IOException    */
DECL|method|closeContainer (long containerId)
name|void
name|closeContainer
parameter_list|(
name|long
name|containerId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes an existing container.    * @param containerId - ID of the container.    * @param pipeline - Pipeline that represents the container.    * @param force - true to forcibly delete the container.    * @throws IOException    */
DECL|method|deleteContainer (long containerId, Pipeline pipeline, boolean force)
name|void
name|deleteContainer
parameter_list|(
name|long
name|containerId
parameter_list|,
name|Pipeline
name|pipeline
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes an existing container.    * @param containerId - ID of the container.    * @param force - true to forcibly delete the container.    * @throws IOException    */
DECL|method|deleteContainer (long containerId, boolean force)
name|void
name|deleteContainer
parameter_list|(
name|long
name|containerId
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Lists a range of containers and get their info.    *    * @param startContainerID start containerID.    * @param count count must be> 0.    *    * @return a list of pipeline.    * @throws IOException    */
DECL|method|listContainer (long startContainerID, int count)
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|listContainer
parameter_list|(
name|long
name|startContainerID
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Read meta data from an existing container.    * @param containerID - ID of the container.    * @param pipeline - Pipeline where the container is located.    * @return ContainerInfo    * @throws IOException    */
DECL|method|readContainer (long containerID, Pipeline pipeline)
name|ContainerDataProto
name|readContainer
parameter_list|(
name|long
name|containerID
parameter_list|,
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Read meta data from an existing container.    * @param containerID - ID of the container.    * @return ContainerInfo    * @throws IOException    */
DECL|method|readContainer (long containerID)
name|ContainerDataProto
name|readContainer
parameter_list|(
name|long
name|containerID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the container size -- Computed by SCM from Container Reports.    * @param containerID - ID of the container.    * @return number of bytes used by this container.    * @throws IOException    */
DECL|method|getContainerSize (long containerID)
name|long
name|getContainerSize
parameter_list|(
name|long
name|containerID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a Container on SCM and returns the pipeline.    * @param type - Replication Type.    * @param replicationFactor - Replication Factor    * @return ContainerInfo    * @throws IOException - in case of error.    */
DECL|method|createContainer (HddsProtos.ReplicationType type, HddsProtos.ReplicationFactor replicationFactor, String owner)
name|ContainerWithPipeline
name|createContainer
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
comment|/**    * Returns a set of Nodes that meet a query criteria.    * @param nodeStatuses - Criteria that we want the node to have.    * @param queryScope - Query scope - Cluster or pool.    * @param poolName - if it is pool, a pool name is required.    * @return A set of nodes that meet the requested criteria.    * @throws IOException    */
DECL|method|queryNode (HddsProtos.NodeState nodeStatuses, HddsProtos.QueryScope queryScope, String poolName)
name|List
argument_list|<
name|HddsProtos
operator|.
name|Node
argument_list|>
name|queryNode
parameter_list|(
name|HddsProtos
operator|.
name|NodeState
name|nodeStatuses
parameter_list|,
name|HddsProtos
operator|.
name|QueryScope
name|queryScope
parameter_list|,
name|String
name|poolName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a specified replication pipeline.    * @param type - Type    * @param factor - Replication factor    * @param nodePool - Set of machines.    * @throws IOException    */
DECL|method|createReplicationPipeline (HddsProtos.ReplicationType type, HddsProtos.ReplicationFactor factor, HddsProtos.NodePool nodePool)
name|Pipeline
name|createReplicationPipeline
parameter_list|(
name|HddsProtos
operator|.
name|ReplicationType
name|type
parameter_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
parameter_list|,
name|HddsProtos
operator|.
name|NodePool
name|nodePool
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Check if SCM is in chill mode.    *    * @return Returns true if SCM is in chill mode else returns false.    * @throws IOException    */
DECL|method|inChillMode ()
name|boolean
name|inChillMode
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Force SCM out of chill mode.    *    * @return returns true if operation is successful.    * @throws IOException    */
DECL|method|forceExitChillMode ()
name|boolean
name|forceExitChillMode
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

