begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cblock.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
operator|.
name|util
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
name|cblock
operator|.
name|meta
operator|.
name|ContainerDescriptor
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
name|hdfs
operator|.
name|ozone
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
name|ozone
operator|.
name|OzoneConsts
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
name|proto
operator|.
name|OzoneProtos
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
name|scm
operator|.
name|client
operator|.
name|ScmClient
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * This class is the one that directly talks to SCM server.  *  * NOTE : this is only a mock class, only to allow testing volume  * creation without actually creating containers. In real world, need to be  * replaced with actual container look up calls.  *  */
end_comment

begin_class
DECL|class|MockStorageClient
specifier|public
class|class
name|MockStorageClient
implements|implements
name|ScmClient
block|{
DECL|field|currentContainerId
specifier|private
specifier|static
name|AtomicInteger
name|currentContainerId
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/**    * Ask SCM to get a exclusive container.    *    * @return A container descriptor object to locate this container    * @throws Exception    */
annotation|@
name|Override
DECL|method|createContainer (String containerId)
specifier|public
name|Pipeline
name|createContainer
parameter_list|(
name|String
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|contId
init|=
name|currentContainerId
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
name|ContainerLookUpService
operator|.
name|addContainer
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|contId
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|ContainerLookUpService
operator|.
name|lookUp
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|contId
argument_list|)
argument_list|)
operator|.
name|getPipeline
argument_list|()
return|;
block|}
comment|/**    * As this is only a testing class, with all "container" maintained in    * memory, no need to really delete anything for now.    * @throws IOException    */
annotation|@
name|Override
DECL|method|deleteContainer (Pipeline pipeline, boolean force)
specifier|public
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
block|{    }
comment|/**    * This is a mock class, so returns the container infos of start container    * and end container.    *    * @param startName start container name.    * @param prefixName prefix container name.    * @param count count.    * @return a list of pipeline.    * @throws IOException    */
annotation|@
name|Override
DECL|method|listContainer (String startName, String prefixName, int count)
specifier|public
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
block|{
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|containerList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ContainerDescriptor
name|containerDescriptor
init|=
name|ContainerLookUpService
operator|.
name|lookUp
argument_list|(
name|startName
argument_list|)
decl_stmt|;
name|ContainerInfo
name|container
init|=
operator|new
name|ContainerInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setContainerName
argument_list|(
name|containerDescriptor
operator|.
name|getContainerID
argument_list|()
argument_list|)
operator|.
name|setPipeline
argument_list|(
name|containerDescriptor
operator|.
name|getPipeline
argument_list|()
argument_list|)
operator|.
name|setState
argument_list|(
name|OzoneProtos
operator|.
name|LifeCycleState
operator|.
name|ALLOCATED
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|containerList
operator|.
name|add
argument_list|(
name|container
argument_list|)
expr_stmt|;
return|return
name|containerList
return|;
block|}
comment|/**    * Create a instance of ContainerData by a given container id,    * since this is a testing class, there is no need set up the hold    * env to get the meta data of the container.    * @param pipeline    * @return    * @throws IOException    */
annotation|@
name|Override
DECL|method|readContainer (Pipeline pipeline)
specifier|public
name|ContainerData
name|readContainer
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ContainerData
operator|.
name|newBuilder
argument_list|()
operator|.
name|setName
argument_list|(
name|pipeline
operator|.
name|getContainerName
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Return reference to an *existing* container with given ID.    *    * @param containerId    * @return    * @throws IOException    */
DECL|method|getContainer (String containerId)
specifier|public
name|Pipeline
name|getContainer
parameter_list|(
name|String
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ContainerLookUpService
operator|.
name|lookUp
argument_list|(
name|containerId
argument_list|)
operator|.
name|getPipeline
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|closeContainer (Pipeline container)
specifier|public
name|void
name|closeContainer
parameter_list|(
name|Pipeline
name|container
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Do nothing, because the mock container does not have the notion of
comment|// "open" and "close".
block|}
annotation|@
name|Override
DECL|method|getContainerSize (Pipeline pipeline)
specifier|public
name|long
name|getContainerSize
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
block|{
comment|// just return a constant value for now
return|return
literal|5L
operator|*
name|OzoneConsts
operator|.
name|GB
return|;
comment|// 5GB
block|}
annotation|@
name|Override
DECL|method|createContainer (OzoneProtos.ReplicationType type, OzoneProtos.ReplicationFactor replicationFactor, String containerId)
specifier|public
name|Pipeline
name|createContainer
parameter_list|(
name|OzoneProtos
operator|.
name|ReplicationType
name|type
parameter_list|,
name|OzoneProtos
operator|.
name|ReplicationFactor
name|replicationFactor
parameter_list|,
name|String
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|contId
init|=
name|currentContainerId
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
name|ContainerLookUpService
operator|.
name|addContainer
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|contId
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|ContainerLookUpService
operator|.
name|lookUp
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|contId
argument_list|)
argument_list|)
operator|.
name|getPipeline
argument_list|()
return|;
block|}
comment|/**    * Returns a set of Nodes that meet a query criteria.    *    * @param nodeStatuses - A set of criteria that we want the node to have.    * @param queryScope - Query scope - Cluster or pool.    * @param poolName - if it is pool, a pool name is required.    * @return A set of nodes that meet the requested criteria.    * @throws IOException    */
annotation|@
name|Override
DECL|method|queryNode (EnumSet<OzoneProtos.NodeState> nodeStatuses, OzoneProtos.QueryScope queryScope, String poolName)
specifier|public
name|OzoneProtos
operator|.
name|NodePool
name|queryNode
parameter_list|(
name|EnumSet
argument_list|<
name|OzoneProtos
operator|.
name|NodeState
argument_list|>
name|nodeStatuses
parameter_list|,
name|OzoneProtos
operator|.
name|QueryScope
name|queryScope
parameter_list|,
name|String
name|poolName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Creates a specified replication pipeline.    *    * @param type - Type    * @param factor - Replication factor    * @param nodePool - Set of machines.    * @throws IOException    */
annotation|@
name|Override
DECL|method|createReplicationPipeline (OzoneProtos.ReplicationType type, OzoneProtos.ReplicationFactor factor, OzoneProtos.NodePool nodePool)
specifier|public
name|Pipeline
name|createReplicationPipeline
parameter_list|(
name|OzoneProtos
operator|.
name|ReplicationType
name|type
parameter_list|,
name|OzoneProtos
operator|.
name|ReplicationFactor
name|factor
parameter_list|,
name|OzoneProtos
operator|.
name|NodePool
name|nodePool
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

