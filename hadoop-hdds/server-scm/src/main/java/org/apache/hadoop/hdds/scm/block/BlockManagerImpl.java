begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.block
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
name|block
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
name|conf
operator|.
name|Configuration
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
name|conf
operator|.
name|StorageUnit
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
name|HddsUtils
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
name|client
operator|.
name|ContainerBlockID
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
name|ScmOps
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
name|ScmConfigKeys
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
name|ScmUtils
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
name|chillmode
operator|.
name|ChillModePrecheck
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
name|common
operator|.
name|helpers
operator|.
name|AllocatedBlock
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
name|exceptions
operator|.
name|SCMException
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
name|pipeline
operator|.
name|PipelineManager
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
name|PipelineNotFoundException
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
name|EventHandler
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
name|metrics2
operator|.
name|util
operator|.
name|MBeans
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
name|client
operator|.
name|BlockID
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
name|util
operator|.
name|StringUtils
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
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|HashMap
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
name|concurrent
operator|.
name|TimeUnit
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
name|exceptions
operator|.
name|SCMException
operator|.
name|ResultCodes
operator|.
name|INVALID_BLOCK_SIZE
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL_DEFAULT
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_BLOCK_DELETING_SERVICE_TIMEOUT
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_BLOCK_DELETING_SERVICE_TIMEOUT_DEFAULT
import|;
end_import

begin_comment
comment|/** Block Manager manages the block access for SCM. */
end_comment

begin_class
DECL|class|BlockManagerImpl
specifier|public
class|class
name|BlockManagerImpl
implements|implements
name|EventHandler
argument_list|<
name|Boolean
argument_list|>
implements|,
name|BlockManager
implements|,
name|BlockmanagerMXBean
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
name|BlockManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// TODO : FIX ME : Hard coding the owner.
comment|// Currently only user of the block service is Ozone, CBlock manages blocks
comment|// by itself and does not rely on the Block service offered by SCM.
DECL|field|pipelineManager
specifier|private
specifier|final
name|PipelineManager
name|pipelineManager
decl_stmt|;
DECL|field|containerManager
specifier|private
specifier|final
name|ContainerManager
name|containerManager
decl_stmt|;
DECL|field|containerSize
specifier|private
specifier|final
name|long
name|containerSize
decl_stmt|;
DECL|field|deletedBlockLog
specifier|private
specifier|final
name|DeletedBlockLog
name|deletedBlockLog
decl_stmt|;
DECL|field|blockDeletingService
specifier|private
specifier|final
name|SCMBlockDeletingService
name|blockDeletingService
decl_stmt|;
DECL|field|mxBean
specifier|private
name|ObjectName
name|mxBean
decl_stmt|;
DECL|field|chillModePrecheck
specifier|private
name|ChillModePrecheck
name|chillModePrecheck
decl_stmt|;
comment|/**    * Constructor.    *    * @param conf - configuration.    * @param nodeManager - node manager.    * @param pipelineManager - pipeline manager.    * @param containerManager - container manager.    * @param eventPublisher - event publisher.    * @throws IOException    */
DECL|method|BlockManagerImpl (final Configuration conf, final NodeManager nodeManager, final PipelineManager pipelineManager, final ContainerManager containerManager, EventPublisher eventPublisher)
specifier|public
name|BlockManagerImpl
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|NodeManager
name|nodeManager
parameter_list|,
specifier|final
name|PipelineManager
name|pipelineManager
parameter_list|,
specifier|final
name|ContainerManager
name|containerManager
parameter_list|,
name|EventPublisher
name|eventPublisher
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|pipelineManager
operator|=
name|pipelineManager
expr_stmt|;
name|this
operator|.
name|containerManager
operator|=
name|containerManager
expr_stmt|;
name|this
operator|.
name|containerSize
operator|=
operator|(
name|long
operator|)
name|conf
operator|.
name|getStorageSize
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE_DEFAULT
argument_list|,
name|StorageUnit
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|mxBean
operator|=
name|MBeans
operator|.
name|register
argument_list|(
literal|"BlockManager"
argument_list|,
literal|"BlockManagerImpl"
argument_list|,
name|this
argument_list|)
expr_stmt|;
comment|// SCM block deleting transaction log and deleting service.
name|deletedBlockLog
operator|=
operator|new
name|DeletedBlockLogImpl
argument_list|(
name|conf
argument_list|,
name|containerManager
argument_list|)
expr_stmt|;
name|long
name|svcInterval
init|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL
argument_list|,
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|long
name|serviceTimeout
init|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|OZONE_BLOCK_DELETING_SERVICE_TIMEOUT
argument_list|,
name|OZONE_BLOCK_DELETING_SERVICE_TIMEOUT_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|blockDeletingService
operator|=
operator|new
name|SCMBlockDeletingService
argument_list|(
name|deletedBlockLog
argument_list|,
name|containerManager
argument_list|,
name|nodeManager
argument_list|,
name|eventPublisher
argument_list|,
name|svcInterval
argument_list|,
name|serviceTimeout
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|chillModePrecheck
operator|=
operator|new
name|ChillModePrecheck
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Start block manager services.    *    * @throws IOException    */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|blockDeletingService
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * Shutdown block manager services.    *    * @throws IOException    */
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|blockDeletingService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|this
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Allocates a block in a container and returns that info.    *    * @param size - Block Size    * @param type Replication Type    * @param factor - Replication Factor    * @return Allocated block    * @throws IOException on failure.    */
annotation|@
name|Override
DECL|method|allocateBlock (final long size, ReplicationType type, ReplicationFactor factor, String owner)
specifier|public
name|AllocatedBlock
name|allocateBlock
parameter_list|(
specifier|final
name|long
name|size
parameter_list|,
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|,
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Size;{} , type : {}, factor : {} "
argument_list|,
name|size
argument_list|,
name|type
argument_list|,
name|factor
argument_list|)
expr_stmt|;
name|ScmUtils
operator|.
name|preCheck
argument_list|(
name|ScmOps
operator|.
name|allocateBlock
argument_list|,
name|chillModePrecheck
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
argument_list|<
literal|0
operator|||
name|size
argument_list|>
name|containerSize
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid block size requested : {}"
argument_list|,
name|size
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Unsupported block size: "
operator|+
name|size
argument_list|,
name|INVALID_BLOCK_SIZE
argument_list|)
throw|;
block|}
comment|/*       Here is the high level logic.        1. We try to find pipelines in open state.        2. If there are no pipelines in OPEN state, then we try to create one.        3. We allocate a block from the available containers in the selected       pipeline.        TODO : #CLUTIL Support random picking of two containers from the list.       So we can use different kind of policies.     */
name|ContainerInfo
name|containerInfo
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|List
argument_list|<
name|Pipeline
argument_list|>
name|availablePipelines
init|=
name|pipelineManager
operator|.
name|getPipelines
argument_list|(
name|type
argument_list|,
name|factor
argument_list|,
name|Pipeline
operator|.
name|PipelineState
operator|.
name|OPEN
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
decl_stmt|;
if|if
condition|(
name|availablePipelines
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
try|try
block|{
comment|// TODO: #CLUTIL Remove creation logic when all replication types and
comment|// factors are handled by pipeline creator
name|pipeline
operator|=
name|pipelineManager
operator|.
name|createPipeline
argument_list|(
name|type
argument_list|,
name|factor
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
break|break;
block|}
block|}
else|else
block|{
comment|// TODO: #CLUTIL Make the selection policy driven.
name|pipeline
operator|=
name|availablePipelines
operator|.
name|get
argument_list|(
call|(
name|int
call|)
argument_list|(
name|Math
operator|.
name|random
argument_list|()
operator|*
name|availablePipelines
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// look for OPEN containers that match the criteria.
name|containerInfo
operator|=
name|containerManager
operator|.
name|getMatchingContainer
argument_list|(
name|size
argument_list|,
name|owner
argument_list|,
name|pipeline
argument_list|)
expr_stmt|;
if|if
condition|(
name|containerInfo
operator|!=
literal|null
condition|)
block|{
return|return
name|newBlock
argument_list|(
name|containerInfo
argument_list|)
return|;
block|}
block|}
comment|// we have tried all strategies we know and but somehow we are not able
comment|// to get a container for this block. Log that info and return a null.
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to allocate a block for the size: {}, type: {}, factor: {}"
argument_list|,
name|size
argument_list|,
name|type
argument_list|,
name|factor
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|/**    * newBlock - returns a new block assigned to a container.    *    * @param containerInfo - Container Info.    * @return AllocatedBlock    */
DECL|method|newBlock (ContainerInfo containerInfo)
specifier|private
name|AllocatedBlock
name|newBlock
parameter_list|(
name|ContainerInfo
name|containerInfo
parameter_list|)
block|{
try|try
block|{
specifier|final
name|Pipeline
name|pipeline
init|=
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|containerInfo
operator|.
name|getPipelineID
argument_list|()
argument_list|)
decl_stmt|;
comment|// TODO : Revisit this local ID allocation when HA is added.
name|long
name|localID
init|=
name|UniqueId
operator|.
name|next
argument_list|()
decl_stmt|;
name|long
name|containerID
init|=
name|containerInfo
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|AllocatedBlock
operator|.
name|Builder
name|abb
init|=
operator|new
name|AllocatedBlock
operator|.
name|Builder
argument_list|()
operator|.
name|setContainerBlockID
argument_list|(
operator|new
name|ContainerBlockID
argument_list|(
name|containerID
argument_list|,
name|localID
argument_list|)
argument_list|)
operator|.
name|setPipeline
argument_list|(
name|pipeline
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"New block allocated : {} Container ID: {}"
argument_list|,
name|localID
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
return|return
name|abb
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|PipelineNotFoundException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Pipeline Machine count is zero."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Deletes a list of blocks in an atomic operation. Internally, SCM writes    * these blocks into a    * {@link DeletedBlockLog} and deletes them from SCM DB. If this is    * successful, given blocks are    * entering pending deletion state and becomes invisible from SCM namespace.    *    * @param blockIDs block IDs. This is often the list of blocks of a    * particular object key.    * @throws IOException if exception happens, non of the blocks is deleted.    */
annotation|@
name|Override
DECL|method|deleteBlocks (List<BlockID> blockIDs)
specifier|public
name|void
name|deleteBlocks
parameter_list|(
name|List
argument_list|<
name|BlockID
argument_list|>
name|blockIDs
parameter_list|)
throws|throws
name|IOException
block|{
name|ScmUtils
operator|.
name|preCheck
argument_list|(
name|ScmOps
operator|.
name|deleteBlock
argument_list|,
name|chillModePrecheck
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting blocks {}"
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|blockIDs
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|Long
argument_list|>
argument_list|>
name|containerBlocks
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// TODO: track the block size info so that we can reclaim the container
comment|// TODO: used space when the block is deleted.
for|for
control|(
name|BlockID
name|block
range|:
name|blockIDs
control|)
block|{
comment|// Merge blocks to a container to blocks mapping,
comment|// prepare to persist this info to the deletedBlocksLog.
name|long
name|containerID
init|=
name|block
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
if|if
condition|(
name|containerBlocks
operator|.
name|containsKey
argument_list|(
name|containerID
argument_list|)
condition|)
block|{
name|containerBlocks
operator|.
name|get
argument_list|(
name|containerID
argument_list|)
operator|.
name|add
argument_list|(
name|block
operator|.
name|getLocalID
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|Long
argument_list|>
name|item
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|item
operator|.
name|add
argument_list|(
name|block
operator|.
name|getLocalID
argument_list|()
argument_list|)
expr_stmt|;
name|containerBlocks
operator|.
name|put
argument_list|(
name|containerID
argument_list|,
name|item
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|deletedBlockLog
operator|.
name|addTransactions
argument_list|(
name|containerBlocks
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Skip writing the deleted blocks info to"
operator|+
literal|" the delLog because addTransaction fails. Batch skipped: "
operator|+
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|blockIDs
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// TODO: Container report handling of the deleted blocks:
comment|// Remove tombstone and update open container usage.
comment|// We will revisit this when the closed container replication is done.
block|}
annotation|@
name|Override
DECL|method|getDeletedBlockLog ()
specifier|public
name|DeletedBlockLog
name|getDeletedBlockLog
parameter_list|()
block|{
return|return
name|this
operator|.
name|deletedBlockLog
return|;
block|}
comment|/**    * Close the resources for BlockManager.    *    * @throws IOException    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|deletedBlockLog
operator|!=
literal|null
condition|)
block|{
name|deletedBlockLog
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|blockDeletingService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
name|mxBean
operator|!=
literal|null
condition|)
block|{
name|MBeans
operator|.
name|unregister
argument_list|(
name|mxBean
argument_list|)
expr_stmt|;
name|mxBean
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getOpenContainersNo ()
specifier|public
name|int
name|getOpenContainersNo
parameter_list|()
block|{
return|return
literal|0
return|;
comment|// TODO : FIX ME : The open container being a single number does not make
comment|// sense.
comment|// We have to get open containers by Replication Type and Replication
comment|// factor. Hence returning 0 for now.
comment|// containers.get(HddsProtos.LifeCycleState.OPEN).size();
block|}
annotation|@
name|Override
DECL|method|getSCMBlockDeletingService ()
specifier|public
name|SCMBlockDeletingService
name|getSCMBlockDeletingService
parameter_list|()
block|{
return|return
name|this
operator|.
name|blockDeletingService
return|;
block|}
annotation|@
name|Override
DECL|method|onMessage (Boolean inChillMode, EventPublisher publisher)
specifier|public
name|void
name|onMessage
parameter_list|(
name|Boolean
name|inChillMode
parameter_list|,
name|EventPublisher
name|publisher
parameter_list|)
block|{
name|this
operator|.
name|chillModePrecheck
operator|.
name|setInChillMode
argument_list|(
name|inChillMode
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns status of scm chill mode determined by CHILL_MODE_STATUS event.    * */
DECL|method|isScmInChillMode ()
specifier|public
name|boolean
name|isScmInChillMode
parameter_list|()
block|{
return|return
name|this
operator|.
name|chillModePrecheck
operator|.
name|isInChillMode
argument_list|()
return|;
block|}
comment|/**    * Get class logger.    * */
DECL|method|getLogger ()
specifier|public
specifier|static
name|Logger
name|getLogger
parameter_list|()
block|{
return|return
name|LOG
return|;
block|}
comment|/**    * This class uses system current time milliseconds to generate unique id.    */
DECL|class|UniqueId
specifier|public
specifier|static
specifier|final
class|class
name|UniqueId
block|{
comment|/*      * When we represent time in milliseconds using 'long' data type,      * the LSB bits are used. Currently we are only using 44 bits (LSB),      * 20 bits (MSB) are not used.      * We will exhaust this 44 bits only when we are in year 2525,      * until then we can safely use this 20 bits (MSB) for offset to generate      * unique id within millisecond.      *      * Year        : Mon Dec 31 18:49:04 IST 2525      * TimeInMillis: 17545641544247      * Binary Representation:      *   MSB (20 bits): 0000 0000 0000 0000 0000      *   LSB (44 bits): 1111 1111 0101 0010 1001 1011 1011 0100 1010 0011 0111      *      * We have 20 bits to run counter, we should exclude the first bit (MSB)      * as we don't want to deal with negative values.      * To be on safer side we will use 'short' data type which is of length      * 16 bits and will give us 65,536 values for offset.      *      */
DECL|field|offset
specifier|private
specifier|static
specifier|volatile
name|short
name|offset
init|=
literal|0
decl_stmt|;
comment|/**      * Private constructor so that no one can instantiate this class.      */
DECL|method|UniqueId ()
specifier|private
name|UniqueId
parameter_list|()
block|{}
comment|/**      * Calculate and returns next unique id based on System#currentTimeMillis.      *      * @return unique long value      */
DECL|method|next ()
specifier|public
specifier|static
specifier|synchronized
name|long
name|next
parameter_list|()
block|{
name|long
name|utcTime
init|=
name|HddsUtils
operator|.
name|getUtcTime
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|utcTime
operator|&
literal|0xFFFF000000000000L
operator|)
operator|==
literal|0
condition|)
block|{
return|return
name|utcTime
operator|<<
name|Short
operator|.
name|SIZE
operator||
operator|(
name|offset
operator|++
operator|&
literal|0x0000FFFF
operator|)
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Got invalid UTC time,"
operator|+
literal|" cannot generate unique Id. UTC Time: "
operator|+
name|utcTime
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

