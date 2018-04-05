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
name|common
operator|.
name|helpers
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
name|ContainerProtos
operator|.
name|ContainerData
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
block|{
comment|/**    * Creates a Container on SCM and returns the pipeline.    * @param containerId - String container ID    * @return Pipeline    * @throws IOException    */
DECL|method|createContainer (String containerId, String owner)
name|Pipeline
name|createContainer
parameter_list|(
name|String
name|containerId
parameter_list|,
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets a container by Name -- Throws if the container does not exist.    * @param containerId - String Container ID    * @return Pipeline    * @throws IOException    */
DECL|method|getContainer (String containerId)
name|Pipeline
name|getContainer
parameter_list|(
name|String
name|containerId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Close a container by name.    *    * @param pipeline the container to be closed.    * @throws IOException    */
DECL|method|closeContainer (Pipeline pipeline)
name|void
name|closeContainer
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes an existing container.    * @param pipeline - Pipeline that represents the container.    * @param force - true to forcibly delete the container.    * @throws IOException    */
DECL|method|deleteContainer (Pipeline pipeline, boolean force)
name|void
name|deleteContainer
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Lists a range of containers and get their info.    *    * @param startName start name, if null, start searching at the head.    * @param prefixName prefix name, if null, then filter is disabled.    * @param count count, if count< 0, the max size is unlimited.(    *              Usually the count will be replace with a very big    *              value instead of being unlimited in case the db is very big)    *    * @return a list of pipeline.    * @throws IOException    */
DECL|method|listContainer (String startName, String prefixName, int count)
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|listContainer
parameter_list|(
name|String
name|startName
parameter_list|,
name|String
name|prefixName
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Read meta data from an existing container.    * @param pipeline - Pipeline that represents the container.    * @return ContainerInfo    * @throws IOException    */
DECL|method|readContainer (Pipeline pipeline)
name|ContainerData
name|readContainer
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the container size -- Computed by SCM from Container Reports.    * @param pipeline - Pipeline    * @return number of bytes used by this container.    * @throws IOException    */
DECL|method|getContainerSize (Pipeline pipeline)
name|long
name|getContainerSize
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a Container on SCM and returns the pipeline.    * @param type - Replication Type.    * @param replicationFactor - Replication Factor    * @param containerId - Container ID    * @return Pipeline    * @throws IOException - in case of error.    */
DECL|method|createContainer (HddsProtos.ReplicationType type, HddsProtos.ReplicationFactor replicationFactor, String containerId, String owner)
name|Pipeline
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
name|containerId
parameter_list|,
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a set of Nodes that meet a query criteria.    * @param nodeStatuses - A set of criteria that we want the node to have.    * @param queryScope - Query scope - Cluster or pool.    * @param poolName - if it is pool, a pool name is required.    * @return A set of nodes that meet the requested criteria.    * @throws IOException    */
DECL|method|queryNode (EnumSet<HddsProtos.NodeState> nodeStatuses, HddsProtos.QueryScope queryScope, String poolName)
name|HddsProtos
operator|.
name|NodePool
name|queryNode
parameter_list|(
name|EnumSet
argument_list|<
name|HddsProtos
operator|.
name|NodeState
argument_list|>
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
block|}
end_interface

end_unit

