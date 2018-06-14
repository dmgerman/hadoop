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
name|node
operator|.
name|NodeManager
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

begin_comment
comment|/**  * Mapping class contains the mapping from a name to a pipeline mapping. This is  * used by SCM when allocating new locations and when looking up a key.  */
end_comment

begin_interface
DECL|interface|Mapping
specifier|public
interface|interface
name|Mapping
extends|extends
name|Closeable
block|{
comment|/**    * Returns the ContainerInfo from the container ID.    *    * @param containerID - ID of container.    * @return - ContainerInfo such as creation state and the pipeline.    * @throws IOException    */
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
comment|/**    * Returns containers under certain conditions.    * Search container IDs from start ID(exclusive),    * The max size of the searching range cannot exceed the    * value of count.    *    * @param startContainerID start containerID,>=0,    * start searching at the head if 0.    * @param count count must be>= 0    *              Usually the count will be replace with a very big    *              value instead of being unlimited in case the db is very big.    *    * @return a list of container.    * @throws IOException    */
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
comment|/**    * Allocates a new container for a given keyName and replication factor.    *    * @param replicationFactor - replication factor of the container.    * @param owner    * @return - Container Info.    * @throws IOException    */
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
comment|/**    * Update container state.    * @param containerID - Container ID    * @param event - container life cycle event    * @return - new container state    * @throws IOException    */
DECL|method|updateContainerState (long containerID, HddsProtos.LifeCycleEvent event)
name|HddsProtos
operator|.
name|LifeCycleState
name|updateContainerState
parameter_list|(
name|long
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
comment|/**    * Returns the container State Manager.    * @return ContainerStateManager    */
DECL|method|getStateManager ()
name|ContainerStateManager
name|getStateManager
parameter_list|()
function_decl|;
comment|/**    * Process container report from Datanode.    *    * @param reports Container report    */
DECL|method|processContainerReports (DatanodeDetails datanodeDetails, ContainerReportsProto reports)
name|void
name|processContainerReports
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|ContainerReportsProto
name|reports
parameter_list|)
throws|throws
name|IOException
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
comment|/**    * Returns the nodeManager.    * @return NodeManager    */
DECL|method|getNodeManager ()
name|NodeManager
name|getNodeManager
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

