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
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|XceiverClientManager
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
name|XceiverClientSpi
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
name|scm
operator|.
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
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
name|storage
operator|.
name|ContainerProtocolCalls
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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ReadContainerResponseProto
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  * This class provides the client-facing APIs of container operations.  */
end_comment

begin_class
DECL|class|ContainerOperationClient
specifier|public
class|class
name|ContainerOperationClient
implements|implements
name|ScmClient
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
name|ContainerOperationClient
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|containerSizeB
specifier|private
specifier|static
name|long
name|containerSizeB
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
specifier|final
name|StorageContainerLocationProtocolClientSideTranslatorPB
DECL|field|storageContainerLocationClient
name|storageContainerLocationClient
decl_stmt|;
DECL|field|xceiverClientManager
specifier|private
specifier|final
name|XceiverClientManager
name|xceiverClientManager
decl_stmt|;
DECL|method|ContainerOperationClient ( StorageContainerLocationProtocolClientSideTranslatorPB storageContainerLocationClient, XceiverClientManager xceiverClientManager)
specifier|public
name|ContainerOperationClient
parameter_list|(
name|StorageContainerLocationProtocolClientSideTranslatorPB
name|storageContainerLocationClient
parameter_list|,
name|XceiverClientManager
name|xceiverClientManager
parameter_list|)
block|{
name|this
operator|.
name|storageContainerLocationClient
operator|=
name|storageContainerLocationClient
expr_stmt|;
name|this
operator|.
name|xceiverClientManager
operator|=
name|xceiverClientManager
expr_stmt|;
block|}
comment|/**    * Return the capacity of containers. The current assumption is that all    * containers have the same capacity. Therefore one static is sufficient for    * any container.    * @return The capacity of one container in number of bytes.    */
DECL|method|getContainerSizeB ()
specifier|public
specifier|static
name|long
name|getContainerSizeB
parameter_list|()
block|{
return|return
name|containerSizeB
return|;
block|}
comment|/**    * Set the capacity of container. Should be exactly once on system start.    * @param size Capacity of one container in number of bytes.    */
DECL|method|setContainerSizeB (long size)
specifier|public
specifier|static
name|void
name|setContainerSizeB
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|containerSizeB
operator|=
name|size
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createContainer (String owner)
specifier|public
name|ContainerWithPipeline
name|createContainer
parameter_list|(
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
block|{
name|XceiverClientSpi
name|client
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ContainerWithPipeline
name|containerWithPipeline
init|=
name|storageContainerLocationClient
operator|.
name|allocateContainer
argument_list|(
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|xceiverClientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|owner
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|containerWithPipeline
operator|.
name|getPipeline
argument_list|()
decl_stmt|;
name|client
operator|=
name|xceiverClientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|pipeline
operator|.
name|isOpen
argument_list|()
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"Unexpected state=%s for pipeline=%s, expected state=%s"
argument_list|,
name|pipeline
operator|.
name|getPipelineState
argument_list|()
argument_list|,
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|,
name|Pipeline
operator|.
name|PipelineState
operator|.
name|OPEN
argument_list|)
argument_list|)
expr_stmt|;
name|createContainer
argument_list|(
name|client
argument_list|,
name|containerWithPipeline
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|containerWithPipeline
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|xceiverClientManager
operator|.
name|releaseClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Create a container over pipeline specified by the SCM.    *    * @param client - Client to communicate with Datanodes.    * @param containerId - Container ID.    * @throws IOException    */
DECL|method|createContainer (XceiverClientSpi client, long containerId)
specifier|public
name|void
name|createContainer
parameter_list|(
name|XceiverClientSpi
name|client
parameter_list|,
name|long
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|traceID
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ContainerProtocolCalls
operator|.
name|createContainer
argument_list|(
name|client
argument_list|,
name|containerId
argument_list|,
name|traceID
argument_list|)
expr_stmt|;
comment|// Let us log this info after we let SCM know that we have completed the
comment|// creation state.
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Created container "
operator|+
name|containerId
operator|+
literal|" machines:"
operator|+
name|client
operator|.
name|getPipeline
argument_list|()
operator|.
name|getNodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Creates a pipeline over the machines choosen by the SCM.    *    * @param client - Client    * @param pipeline - pipeline to be createdon Datanodes.    * @throws IOException    */
DECL|method|createPipeline (XceiverClientSpi client, Pipeline pipeline)
specifier|private
name|void
name|createPipeline
parameter_list|(
name|XceiverClientSpi
name|client
parameter_list|,
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|,
literal|"Pipeline "
operator|+
literal|"name cannot be null when client create flag is set."
argument_list|)
expr_stmt|;
comment|// Pipeline creation is a three step process.
comment|//
comment|// 1. Notify SCM that this client is doing a create pipeline on
comment|// datanodes.
comment|//
comment|// 2. Talk to Datanodes to create the pipeline.
comment|//
comment|// 3. update SCM that pipeline creation was successful.
comment|// TODO: this has not been fully implemented on server side
comment|// SCMClientProtocolServer#notifyObjectStageChange
comment|// TODO: when implement the pipeline state machine, change
comment|// the pipeline name (string) to pipeline id (long)
comment|//storageContainerLocationClient.notifyObjectStageChange(
comment|//    ObjectStageChangeRequestProto.Type.pipeline,
comment|//    pipeline.getPipelineName(),
comment|//    ObjectStageChangeRequestProto.Op.create,
comment|//    ObjectStageChangeRequestProto.Stage.begin);
comment|// client.createPipeline();
comment|// TODO: Use PipelineManager to createPipeline
comment|//storageContainerLocationClient.notifyObjectStageChange(
comment|//    ObjectStageChangeRequestProto.Type.pipeline,
comment|//    pipeline.getPipelineName(),
comment|//    ObjectStageChangeRequestProto.Op.create,
comment|//    ObjectStageChangeRequestProto.Stage.complete);
comment|// TODO : Should we change the state on the client side ??
comment|// That makes sense, but it is not needed for the client to work.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Pipeline creation successful. Pipeline: {}"
argument_list|,
name|pipeline
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createContainer (HddsProtos.ReplicationType type, HddsProtos.ReplicationFactor factor, String owner)
specifier|public
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
name|factor
parameter_list|,
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
block|{
name|XceiverClientSpi
name|client
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// allocate container on SCM.
name|ContainerWithPipeline
name|containerWithPipeline
init|=
name|storageContainerLocationClient
operator|.
name|allocateContainer
argument_list|(
name|type
argument_list|,
name|factor
argument_list|,
name|owner
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|containerWithPipeline
operator|.
name|getPipeline
argument_list|()
decl_stmt|;
name|client
operator|=
name|xceiverClientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
comment|// connect to pipeline leader and allocate container on leader datanode.
name|client
operator|=
name|xceiverClientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|createContainer
argument_list|(
name|client
argument_list|,
name|containerWithPipeline
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|containerWithPipeline
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|xceiverClientManager
operator|.
name|releaseClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Returns a set of Nodes that meet a query criteria.    *    * @param nodeStatuses - Criteria that we want the node to have.    * @param queryScope - Query scope - Cluster or pool.    * @param poolName - if it is pool, a pool name is required.    * @return A set of nodes that meet the requested criteria.    * @throws IOException    */
annotation|@
name|Override
DECL|method|queryNode (HddsProtos.NodeState nodeStatuses, HddsProtos.QueryScope queryScope, String poolName)
specifier|public
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
block|{
return|return
name|storageContainerLocationClient
operator|.
name|queryNode
argument_list|(
name|nodeStatuses
argument_list|,
name|queryScope
argument_list|,
name|poolName
argument_list|)
return|;
block|}
comment|/**    * Creates a specified replication pipeline.    */
annotation|@
name|Override
DECL|method|createReplicationPipeline (HddsProtos.ReplicationType type, HddsProtos.ReplicationFactor factor, HddsProtos.NodePool nodePool)
specifier|public
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
block|{
return|return
name|storageContainerLocationClient
operator|.
name|createReplicationPipeline
argument_list|(
name|type
argument_list|,
name|factor
argument_list|,
name|nodePool
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|listPipelines ()
specifier|public
name|List
argument_list|<
name|Pipeline
argument_list|>
name|listPipelines
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|storageContainerLocationClient
operator|.
name|listPipelines
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|closePipeline (HddsProtos.PipelineID pipelineID)
specifier|public
name|void
name|closePipeline
parameter_list|(
name|HddsProtos
operator|.
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
name|storageContainerLocationClient
operator|.
name|closePipeline
argument_list|(
name|pipelineID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|xceiverClientManager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Can't close "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Deletes an existing container.    *    * @param containerId - ID of the container.    * @param pipeline    - Pipeline that represents the container.    * @param force       - true to forcibly delete the container.    * @throws IOException    */
annotation|@
name|Override
DECL|method|deleteContainer (long containerId, Pipeline pipeline, boolean force)
specifier|public
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
block|{
name|XceiverClientSpi
name|client
init|=
literal|null
decl_stmt|;
try|try
block|{
name|client
operator|=
name|xceiverClientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|String
name|traceID
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ContainerProtocolCalls
operator|.
name|deleteContainer
argument_list|(
name|client
argument_list|,
name|containerId
argument_list|,
name|force
argument_list|,
name|traceID
argument_list|)
expr_stmt|;
name|storageContainerLocationClient
operator|.
name|deleteContainer
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Deleted container {}, machines: {} "
argument_list|,
name|containerId
argument_list|,
name|pipeline
operator|.
name|getNodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|xceiverClientManager
operator|.
name|releaseClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Delete the container, this will release any resource it uses.    * @param containerID - containerID.    * @param force - True to forcibly delete the container.    * @throws IOException    */
annotation|@
name|Override
DECL|method|deleteContainer (long containerID, boolean force)
specifier|public
name|void
name|deleteContainer
parameter_list|(
name|long
name|containerID
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerWithPipeline
name|info
init|=
name|getContainerWithPipeline
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
name|deleteContainer
argument_list|(
name|containerID
argument_list|,
name|info
operator|.
name|getPipeline
argument_list|()
argument_list|,
name|force
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|listContainer (long startContainerID, int count)
specifier|public
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
block|{
return|return
name|storageContainerLocationClient
operator|.
name|listContainer
argument_list|(
name|startContainerID
argument_list|,
name|count
argument_list|)
return|;
block|}
comment|/**    * Get meta data from an existing container.    *    * @param containerID - ID of the container.    * @param pipeline    - Pipeline where the container is located.    * @return ContainerInfo    * @throws IOException    */
annotation|@
name|Override
DECL|method|readContainer (long containerID, Pipeline pipeline)
specifier|public
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
block|{
name|XceiverClientSpi
name|client
init|=
literal|null
decl_stmt|;
try|try
block|{
name|client
operator|=
name|xceiverClientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|String
name|traceID
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ReadContainerResponseProto
name|response
init|=
name|ContainerProtocolCalls
operator|.
name|readContainer
argument_list|(
name|client
argument_list|,
name|containerID
argument_list|,
name|traceID
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Read container {}, machines: {} "
argument_list|,
name|containerID
argument_list|,
name|pipeline
operator|.
name|getNodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|response
operator|.
name|getContainerData
argument_list|()
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|xceiverClientManager
operator|.
name|releaseClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Get meta data from an existing container.    * @param containerID - ID of the container.    * @return ContainerInfo - a message of protobuf which has basic info    * of a container.    * @throws IOException    */
annotation|@
name|Override
DECL|method|readContainer (long containerID)
specifier|public
name|ContainerDataProto
name|readContainer
parameter_list|(
name|long
name|containerID
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerWithPipeline
name|info
init|=
name|getContainerWithPipeline
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
return|return
name|readContainer
argument_list|(
name|containerID
argument_list|,
name|info
operator|.
name|getPipeline
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Given an id, return the pipeline associated with the container.    * @param containerId - String Container ID    * @return Pipeline of the existing container, corresponding to the given id.    * @throws IOException    */
annotation|@
name|Override
DECL|method|getContainer (long containerId)
specifier|public
name|ContainerInfo
name|getContainer
parameter_list|(
name|long
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|storageContainerLocationClient
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
return|;
block|}
comment|/**    * Gets a container by Name -- Throws if the container does not exist.    *    * @param containerId - Container ID    * @return ContainerWithPipeline    * @throws IOException    */
annotation|@
name|Override
DECL|method|getContainerWithPipeline (long containerId)
specifier|public
name|ContainerWithPipeline
name|getContainerWithPipeline
parameter_list|(
name|long
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|storageContainerLocationClient
operator|.
name|getContainerWithPipeline
argument_list|(
name|containerId
argument_list|)
return|;
block|}
comment|/**    * Close a container.    *    * @param pipeline the container to be closed.    * @throws IOException    */
annotation|@
name|Override
DECL|method|closeContainer (long containerId, Pipeline pipeline)
specifier|public
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
block|{
name|XceiverClientSpi
name|client
init|=
literal|null
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Close container {}"
argument_list|,
name|pipeline
argument_list|)
expr_stmt|;
comment|/*       TODO: two orders here, revisit this later:       1. close on SCM first, then on data node       2. close on data node first, then on SCM        with 1: if client failed after closing on SCM, then there is a       container SCM thinks as closed, but is actually open. Then SCM will no       longer allocate block to it, which is fine. But SCM may later try to       replicate this "closed" container, which I'm not sure is safe.        with 2: if client failed after close on datanode, then there is a       container SCM thinks as open, but is actually closed. Then SCM will still       try to allocate block to it. Which will fail when actually doing the       write. No more data can be written, but at least the correctness and       consistency of existing data will maintain.        For now, take the #2 way.        */
comment|// Actually close the container on Datanode
name|client
operator|=
name|xceiverClientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|String
name|traceID
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|storageContainerLocationClient
operator|.
name|notifyObjectStageChange
argument_list|(
name|ObjectStageChangeRequestProto
operator|.
name|Type
operator|.
name|container
argument_list|,
name|containerId
argument_list|,
name|ObjectStageChangeRequestProto
operator|.
name|Op
operator|.
name|close
argument_list|,
name|ObjectStageChangeRequestProto
operator|.
name|Stage
operator|.
name|begin
argument_list|)
expr_stmt|;
name|ContainerProtocolCalls
operator|.
name|closeContainer
argument_list|(
name|client
argument_list|,
name|containerId
argument_list|,
name|traceID
argument_list|)
expr_stmt|;
comment|// Notify SCM to close the container
name|storageContainerLocationClient
operator|.
name|notifyObjectStageChange
argument_list|(
name|ObjectStageChangeRequestProto
operator|.
name|Type
operator|.
name|container
argument_list|,
name|containerId
argument_list|,
name|ObjectStageChangeRequestProto
operator|.
name|Op
operator|.
name|close
argument_list|,
name|ObjectStageChangeRequestProto
operator|.
name|Stage
operator|.
name|complete
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|xceiverClientManager
operator|.
name|releaseClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Close a container.    *    * @throws IOException    */
annotation|@
name|Override
DECL|method|closeContainer (long containerId)
specifier|public
name|void
name|closeContainer
parameter_list|(
name|long
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerWithPipeline
name|info
init|=
name|getContainerWithPipeline
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|info
operator|.
name|getPipeline
argument_list|()
decl_stmt|;
name|closeContainer
argument_list|(
name|containerId
argument_list|,
name|pipeline
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the the current usage information.    * @param containerID - ID of the container.    * @return the size of the given container.    * @throws IOException    */
annotation|@
name|Override
DECL|method|getContainerSize (long containerID)
specifier|public
name|long
name|getContainerSize
parameter_list|(
name|long
name|containerID
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO : Fix this, it currently returns the capacity
comment|// but not the current usage.
name|long
name|size
init|=
name|getContainerSizeB
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Container size unknown!"
argument_list|)
throw|;
block|}
return|return
name|size
return|;
block|}
comment|/**    * Check if SCM is in chill mode.    *    * @return Returns true if SCM is in chill mode else returns false.    * @throws IOException    */
DECL|method|inChillMode ()
specifier|public
name|boolean
name|inChillMode
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|storageContainerLocationClient
operator|.
name|inChillMode
argument_list|()
return|;
block|}
comment|/**    * Force SCM out of chill mode.    *    * @return returns true if operation is successful.    * @throws IOException    */
DECL|method|forceExitChillMode ()
specifier|public
name|boolean
name|forceExitChillMode
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|storageContainerLocationClient
operator|.
name|forceExitChillMode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

