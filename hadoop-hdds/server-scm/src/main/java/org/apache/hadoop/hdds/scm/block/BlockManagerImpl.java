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
name|server
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
name|Random
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
DECL|field|containerProvisionBatchSize
specifier|private
specifier|final
name|int
name|containerProvisionBatchSize
decl_stmt|;
DECL|field|rand
specifier|private
specifier|final
name|Random
name|rand
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
comment|/**    * Constructor.    *    * @param conf - configuration.    * @param nodeManager - node manager.    * @param containerManager - container manager.    * @param eventPublisher - event publisher.    * @throws IOException    */
DECL|method|BlockManagerImpl (final Configuration conf, final NodeManager nodeManager, final ContainerManager containerManager, EventPublisher eventPublisher)
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
name|this
operator|.
name|containerProvisionBatchSize
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_PROVISION_BATCH_SIZE
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_PROVISION_BATCH_SIZE_DEFAULT
argument_list|)
expr_stmt|;
name|rand
operator|=
operator|new
name|Random
argument_list|()
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
comment|/**    * Pre allocate specified count of containers for block creation.    *    * @param count - Number of containers to allocate.    * @param type - Type of containers    * @param factor - how many copies needed for this container.    * @throws IOException    */
DECL|method|preAllocateContainers (int count, ReplicationType type, ReplicationFactor factor, String owner)
specifier|private
specifier|synchronized
name|void
name|preAllocateContainers
parameter_list|(
name|int
name|count
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|ContainerWithPipeline
name|containerWithPipeline
decl_stmt|;
try|try
block|{
comment|// TODO: Fix this later when Ratis is made the Default.
name|containerWithPipeline
operator|=
name|containerManager
operator|.
name|allocateContainer
argument_list|(
name|type
argument_list|,
name|factor
argument_list|,
name|owner
argument_list|)
expr_stmt|;
if|if
condition|(
name|containerWithPipeline
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to allocate container."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to allocate container: {}"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|/*       Here is the high level logic.        1. First we check if there are containers in ALLOCATED state, that is          SCM has allocated them in the SCM namespace but the corresponding          container has not been created in the Datanode yet. If we have any in          that state, we will return that to the client, which allows client to          finish creating those containers. This is a sort of greedy algorithm,          our primary purpose is to get as many containers as possible.        2. If there are no allocated containers -- Then we find a Open container          that matches that pattern.        3. If both of them fail, the we will pre-allocate a bunch of containers          in SCM and try again.        TODO : Support random picking of two containers from the list. So we can              use different kind of policies.     */
name|ContainerWithPipeline
name|containerWithPipeline
decl_stmt|;
comment|// This is to optimize performance, if the below condition is evaluated
comment|// to false, then we can be sure that there are no containers in
comment|// ALLOCATED state.
comment|// This can result in false positive, but it will never be false negative.
comment|// How can this result in false positive? We check if there are any
comment|// containers in ALLOCATED state, this check doesn't care about the
comment|// USER of the containers. So there might be cases where a different
comment|// USER has few containers in ALLOCATED state, which will result in
comment|// false positive.
if|if
condition|(
operator|!
name|containerManager
operator|.
name|getContainers
argument_list|(
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|ALLOCATED
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Since the above check can result in false positive, we have to do
comment|// the actual check and find out if there are containers in ALLOCATED
comment|// state matching our criteria.
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// Using containers from ALLOCATED state should be done within
comment|// synchronized block (or) write lock. Since we already hold a
comment|// read lock, we will end up in deadlock situation if we take
comment|// write lock here.
name|containerWithPipeline
operator|=
name|containerManager
operator|.
name|getMatchingContainerWithPipeline
argument_list|(
name|size
argument_list|,
name|owner
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|ALLOCATED
argument_list|)
expr_stmt|;
if|if
condition|(
name|containerWithPipeline
operator|!=
literal|null
condition|)
block|{
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|containerWithPipeline
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|containerID
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CREATE
argument_list|)
expr_stmt|;
return|return
name|newBlock
argument_list|(
name|containerWithPipeline
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|ALLOCATED
argument_list|)
return|;
block|}
block|}
block|}
comment|// Since we found no allocated containers that match our criteria, let us
comment|// look for OPEN containers that match the criteria.
name|containerWithPipeline
operator|=
name|containerManager
operator|.
name|getMatchingContainerWithPipeline
argument_list|(
name|size
argument_list|,
name|owner
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
argument_list|)
expr_stmt|;
if|if
condition|(
name|containerWithPipeline
operator|!=
literal|null
condition|)
block|{
return|return
name|newBlock
argument_list|(
name|containerWithPipeline
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
argument_list|)
return|;
block|}
comment|// We found neither ALLOCATED or OPEN Containers. This generally means
comment|// that most of our containers are full or we have not allocated
comment|// containers of the type and replication factor. So let us go and
comment|// allocate some.
comment|// Even though we have already checked the containers in ALLOCATED
comment|// state, we have to check again as we only hold a read lock.
comment|// Some other thread might have pre-allocated container in meantime.
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
operator|!
name|containerManager
operator|.
name|getContainers
argument_list|(
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|ALLOCATED
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|containerWithPipeline
operator|=
name|containerManager
operator|.
name|getMatchingContainerWithPipeline
argument_list|(
name|size
argument_list|,
name|owner
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|ALLOCATED
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|containerWithPipeline
operator|==
literal|null
condition|)
block|{
name|preAllocateContainers
argument_list|(
name|containerProvisionBatchSize
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|owner
argument_list|)
expr_stmt|;
name|containerWithPipeline
operator|=
name|containerManager
operator|.
name|getMatchingContainerWithPipeline
argument_list|(
name|size
argument_list|,
name|owner
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|ALLOCATED
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|containerWithPipeline
operator|!=
literal|null
condition|)
block|{
name|containerManager
operator|.
name|updateContainerState
argument_list|(
name|containerWithPipeline
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|containerID
argument_list|()
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleEvent
operator|.
name|CREATE
argument_list|)
expr_stmt|;
return|return
name|newBlock
argument_list|(
name|containerWithPipeline
argument_list|,
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|ALLOCATED
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
comment|/**    * newBlock - returns a new block assigned to a container.    *    * @param containerWithPipeline - Container Info.    * @param state - Current state of the container.    * @return AllocatedBlock    */
DECL|method|newBlock (ContainerWithPipeline containerWithPipeline, HddsProtos.LifeCycleState state)
specifier|private
name|AllocatedBlock
name|newBlock
parameter_list|(
name|ContainerWithPipeline
name|containerWithPipeline
parameter_list|,
name|HddsProtos
operator|.
name|LifeCycleState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerInfo
name|containerInfo
init|=
name|containerWithPipeline
operator|.
name|getContainerInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|containerWithPipeline
operator|.
name|getPipeline
argument_list|()
operator|.
name|getDatanodes
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Pipeline Machine count is zero."
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
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
name|boolean
name|createContainer
init|=
operator|(
name|state
operator|==
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|ALLOCATED
operator|)
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
name|setBlockID
argument_list|(
operator|new
name|BlockID
argument_list|(
name|containerID
argument_list|,
name|localID
argument_list|)
argument_list|)
operator|.
name|setPipeline
argument_list|(
name|containerWithPipeline
operator|.
name|getPipeline
argument_list|()
argument_list|)
operator|.
name|setShouldCreateContainer
argument_list|(
name|createContainer
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

