begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.protocol
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
name|protocol
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
name|scm
operator|.
name|ScmInfo
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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerLocationProtocolProtos
operator|.
name|ObjectStageChangeRequestProto
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
comment|/**  * ContainerLocationProtocol is used by an HDFS node to find the set of nodes  * that currently host a container.  */
end_comment

begin_interface
DECL|interface|StorageContainerLocationProtocol
specifier|public
interface|interface
name|StorageContainerLocationProtocol
block|{
comment|/**    * Asks SCM where a container should be allocated. SCM responds with the    * set of datanodes that should be used creating this container.    *    */
DECL|method|allocateContainer ( HddsProtos.ReplicationType replicationType, HddsProtos.ReplicationFactor factor, String owner)
name|ContainerWithPipeline
name|allocateContainer
parameter_list|(
name|HddsProtos
operator|.
name|ReplicationType
name|replicationType
parameter_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
parameter_list|,
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Ask SCM the location of the container. SCM responds with a group of    * nodes where this container and its replicas are located.    *    * @param containerID - ID of the container.    * @return ContainerInfo - the container info such as where the pipeline    *                         is located.    * @throws IOException    */
DECL|method|getContainer (long containerID)
name|ContainerInfo
name|getContainer
parameter_list|(
name|long
name|containerID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Ask SCM the location of the container. SCM responds with a group of    * nodes where this container and its replicas are located.    *    * @param containerID - ID of the container.    * @return ContainerWithPipeline - the container info with the pipeline.    * @throws IOException    */
DECL|method|getContainerWithPipeline (long containerID)
name|ContainerWithPipeline
name|getContainerWithPipeline
parameter_list|(
name|long
name|containerID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Ask SCM a list of containers with a range of container names    * and the limit of count.    * Search container names between start name(exclusive), and    * use prefix name to filter the result. the max size of the    * searching range cannot exceed the value of count.    *    * @param startContainerID start container ID.    * @param count count, if count< 0, the max size is unlimited.(    *              Usually the count will be replace with a very big    *              value instead of being unlimited in case the db is very big)    *    * @return a list of container.    * @throws IOException    */
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
comment|/**    * Deletes a container in SCM.    *    * @param containerID    * @throws IOException    *   if failed to delete the container mapping from db store    *   or container doesn't exist.    */
DECL|method|deleteContainer (long containerID)
name|void
name|deleteContainer
parameter_list|(
name|long
name|containerID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    *  Queries a list of Node Statuses.    * @param state    * @return List of Datanodes.    */
DECL|method|queryNode (HddsProtos.NodeState state, HddsProtos.QueryScope queryScope, String poolName)
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
name|state
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
comment|/**    * Notify from client when begin or finish creating objects like pipeline    * or containers on datanodes.    * Container will be in Operational state after that.    * @param type object type    * @param id object id    * @param op operation type (e.g., create, close, delete)    * @param stage creation stage    */
DECL|method|notifyObjectStageChange ( ObjectStageChangeRequestProto.Type type, long id, ObjectStageChangeRequestProto.Op op, ObjectStageChangeRequestProto.Stage stage)
name|void
name|notifyObjectStageChange
parameter_list|(
name|ObjectStageChangeRequestProto
operator|.
name|Type
name|type
parameter_list|,
name|long
name|id
parameter_list|,
name|ObjectStageChangeRequestProto
operator|.
name|Op
name|op
parameter_list|,
name|ObjectStageChangeRequestProto
operator|.
name|Stage
name|stage
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a replication pipeline of a specified type.    * @param type - replication type    * @param factor - factor 1 or 3    * @param nodePool - optional machine list to build a pipeline.    * @throws IOException    */
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
comment|/**    * Returns information about SCM.    *    * @return {@link ScmInfo}    * @throws IOException    */
DECL|method|getScmInfo ()
name|ScmInfo
name|getScmInfo
parameter_list|()
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
comment|/**    * Force SCM out of Chill mode.    *    * @return returns true if operation is successful.    * @throws IOException    */
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

