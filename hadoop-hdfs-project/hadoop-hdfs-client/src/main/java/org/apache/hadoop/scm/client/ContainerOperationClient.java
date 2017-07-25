begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.scm.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
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
comment|/**    * Create a container with the given ID as its name.    * @param containerId - String container ID    * @return A Pipeline object to actually write/read from the container.    * @throws IOException    */
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
name|XceiverClientSpi
name|client
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Pipeline
name|pipeline
init|=
name|storageContainerLocationClient
operator|.
name|allocateContainer
argument_list|(
name|containerId
argument_list|)
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
name|traceID
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
literal|"Created container "
operator|+
name|containerId
operator|+
literal|" leader:"
operator|+
name|pipeline
operator|.
name|getLeader
argument_list|()
operator|+
literal|" machines:"
operator|+
name|pipeline
operator|.
name|getMachines
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|pipeline
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
comment|/**    * Creates a Container on SCM with specified replication factor.    * @param containerId - String container ID    * @param replicationFactor - replication factor    * @return Pipeline    * @throws IOException    */
annotation|@
name|Override
DECL|method|createContainer (String containerId, ScmClient.ReplicationFactor replicationFactor)
specifier|public
name|Pipeline
name|createContainer
parameter_list|(
name|String
name|containerId
parameter_list|,
name|ScmClient
operator|.
name|ReplicationFactor
name|replicationFactor
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
name|Pipeline
name|pipeline
init|=
name|storageContainerLocationClient
operator|.
name|allocateContainer
argument_list|(
name|containerId
argument_list|,
name|replicationFactor
argument_list|)
decl_stmt|;
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
name|traceID
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created container "
operator|+
name|containerId
operator|+
literal|" leader:"
operator|+
name|pipeline
operator|.
name|getLeader
argument_list|()
operator|+
literal|" machines:"
operator|+
name|pipeline
operator|.
name|getMachines
argument_list|()
operator|+
literal|" replication factor:"
operator|+
name|replicationFactor
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|pipeline
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
comment|/**    * Delete the container, this will release any resource it uses.    * @param pipeline - Pipeline that represents the container.    * @param force - True to forcibly delete the container.    * @throws IOException    */
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
name|force
argument_list|,
name|traceID
argument_list|)
expr_stmt|;
name|storageContainerLocationClient
operator|.
name|deleteContainer
argument_list|(
name|pipeline
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleted container {}, leader: {}, machines: {} "
argument_list|,
name|pipeline
operator|.
name|getContainerName
argument_list|()
argument_list|,
name|pipeline
operator|.
name|getLeader
argument_list|()
argument_list|,
name|pipeline
operator|.
name|getMachines
argument_list|()
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
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|listContainer (String startName, String prefixName, int count)
specifier|public
name|List
argument_list|<
name|Pipeline
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
return|return
name|storageContainerLocationClient
operator|.
name|listContainer
argument_list|(
name|startName
argument_list|,
name|prefixName
argument_list|,
name|count
argument_list|)
return|;
block|}
comment|/**    * Get meta data from an existing container.    *    * @param pipeline - pipeline that represents the container.    * @return ContainerInfo - a message of protobuf which has basic info    * of a container.    * @throws IOException    */
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
name|pipeline
operator|.
name|getContainerName
argument_list|()
argument_list|,
name|traceID
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Read container {}, leader: {}, machines: {} "
argument_list|,
name|pipeline
operator|.
name|getContainerName
argument_list|()
argument_list|,
name|pipeline
operator|.
name|getLeader
argument_list|()
argument_list|,
name|pipeline
operator|.
name|getMachines
argument_list|()
argument_list|)
expr_stmt|;
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
comment|/**    * Given an id, return the pipeline associated with the container.    * @param containerId - String Container ID    * @return Pipeline of the existing container, corresponding to the given id.    * @throws IOException    */
annotation|@
name|Override
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
name|storageContainerLocationClient
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
return|;
block|}
comment|/**    * Get the the current usage information.    * @param pipeline - Pipeline    * @return the size of the given container.    * @throws IOException    */
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
comment|// TODO : Pipeline can be null, handle it correctly.
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
block|}
end_class

end_unit

