begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  *<p>http://www.apache.org/licenses/LICENSE-2.0  *<p>  *<p>Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Longs
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
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|ContainerInfoProto
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
name|LifeCycleState
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
name|server
operator|.
name|ServerUtils
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
name|utils
operator|.
name|BatchOperation
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
name|utils
operator|.
name|MetadataStore
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
name|utils
operator|.
name|MetadataStoreBuilder
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
name|File
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
name|Collections
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
name|Objects
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReentrantLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE_DEFAULT
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
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE
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
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DB_CACHE_SIZE_DEFAULT
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
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DB_CACHE_SIZE_MB
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
name|FAILED_TO_CHANGE_CONTAINER_STATE
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
name|OzoneConsts
operator|.
name|SCM_CONTAINER_DB
import|;
end_import

begin_comment
comment|/**  * ContainerManager class contains the mapping from a name to a pipeline  * mapping. This is used by SCM when allocating new locations and when  * looking up a key.  */
end_comment

begin_class
DECL|class|SCMContainerManager
specifier|public
class|class
name|SCMContainerManager
implements|implements
name|ContainerManager
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
name|SCMContainerManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|Lock
name|lock
decl_stmt|;
DECL|field|containerStore
specifier|private
specifier|final
name|MetadataStore
name|containerStore
decl_stmt|;
DECL|field|pipelineManager
specifier|private
specifier|final
name|PipelineManager
name|pipelineManager
decl_stmt|;
DECL|field|containerStateManager
specifier|private
specifier|final
name|ContainerStateManager
name|containerStateManager
decl_stmt|;
DECL|field|eventPublisher
specifier|private
specifier|final
name|EventPublisher
name|eventPublisher
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|long
name|size
decl_stmt|;
comment|/**    * Constructs a mapping class that creates mapping between container names    * and pipelines.    *    * @param nodeManager - NodeManager so that we can get the nodes that are    * healthy to place new    * containers.    * passed to LevelDB and this memory is allocated in Native code space.    * CacheSize is specified    * in MB.    * @param pipelineManager - PipelineManager    * @throws IOException on Failure.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|SCMContainerManager (final Configuration conf, final NodeManager nodeManager, PipelineManager pipelineManager, final EventPublisher eventPublisher)
specifier|public
name|SCMContainerManager
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|NodeManager
name|nodeManager
parameter_list|,
name|PipelineManager
name|pipelineManager
parameter_list|,
specifier|final
name|EventPublisher
name|eventPublisher
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|File
name|metaDir
init|=
name|ServerUtils
operator|.
name|getScmDbDir
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|File
name|containerDBPath
init|=
operator|new
name|File
argument_list|(
name|metaDir
argument_list|,
name|SCM_CONTAINER_DB
argument_list|)
decl_stmt|;
specifier|final
name|int
name|cacheSize
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_SCM_DB_CACHE_SIZE_MB
argument_list|,
name|OZONE_SCM_DB_CACHE_SIZE_DEFAULT
argument_list|)
decl_stmt|;
name|this
operator|.
name|containerStore
operator|=
name|MetadataStoreBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
operator|.
name|setDbFile
argument_list|(
name|containerDBPath
argument_list|)
operator|.
name|setCacheSize
argument_list|(
name|cacheSize
operator|*
name|OzoneConsts
operator|.
name|MB
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|lock
operator|=
operator|new
name|ReentrantLock
argument_list|()
expr_stmt|;
name|this
operator|.
name|size
operator|=
operator|(
name|long
operator|)
name|conf
operator|.
name|getStorageSize
argument_list|(
name|OZONE_SCM_CONTAINER_SIZE
argument_list|,
name|OZONE_SCM_CONTAINER_SIZE_DEFAULT
argument_list|,
name|StorageUnit
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|this
operator|.
name|pipelineManager
operator|=
name|pipelineManager
expr_stmt|;
name|this
operator|.
name|containerStateManager
operator|=
operator|new
name|ContainerStateManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|eventPublisher
operator|=
name|eventPublisher
expr_stmt|;
name|loadExistingContainers
argument_list|()
expr_stmt|;
block|}
DECL|method|loadExistingContainers ()
specifier|private
name|void
name|loadExistingContainers
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|range
init|=
name|containerStore
operator|.
name|getSequentialRangeKVs
argument_list|(
literal|null
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
range|:
name|range
control|)
block|{
name|ContainerInfo
name|container
init|=
name|ContainerInfo
operator|.
name|fromProtobuf
argument_list|(
name|ContainerInfoProto
operator|.
name|PARSER
operator|.
name|parseFrom
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|containerStateManager
operator|.
name|loadContainer
argument_list|(
name|container
argument_list|)
expr_stmt|;
if|if
condition|(
name|container
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|pipelineManager
operator|.
name|addContainerToPipeline
argument_list|(
name|container
operator|.
name|getPipelineID
argument_list|()
argument_list|,
name|ContainerID
operator|.
name|valueof
argument_list|(
name|container
operator|.
name|getContainerID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|VisibleForTesting
comment|// TODO: remove this later.
DECL|method|getContainerStateManager ()
specifier|public
name|ContainerStateManager
name|getContainerStateManager
parameter_list|()
block|{
return|return
name|containerStateManager
return|;
block|}
annotation|@
name|Override
DECL|method|getContainers ()
specifier|public
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|getContainers
parameter_list|()
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|containerStateManager
operator|.
name|getAllContainerIDs
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|id
lambda|->
block|{
try|try
block|{
return|return
name|containerStateManager
operator|.
name|getContainer
argument_list|(
name|id
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ContainerNotFoundException
name|e
parameter_list|)
block|{
comment|// How can this happen?
return|return
literal|null
return|;
block|}
block|}
argument_list|)
operator|.
name|filter
argument_list|(
name|Objects
operator|::
name|nonNull
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getContainers (LifeCycleState state)
specifier|public
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|getContainers
parameter_list|(
name|LifeCycleState
name|state
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|containerStateManager
operator|.
name|getContainerIDsByState
argument_list|(
name|state
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|id
lambda|->
block|{
try|try
block|{
return|return
name|containerStateManager
operator|.
name|getContainer
argument_list|(
name|id
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ContainerNotFoundException
name|e
parameter_list|)
block|{
comment|// How can this happen?
return|return
literal|null
return|;
block|}
block|}
argument_list|)
operator|.
name|filter
argument_list|(
name|Objects
operator|::
name|nonNull
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|getContainer (final ContainerID containerID)
specifier|public
name|ContainerInfo
name|getContainer
parameter_list|(
specifier|final
name|ContainerID
name|containerID
parameter_list|)
throws|throws
name|ContainerNotFoundException
block|{
return|return
name|containerStateManager
operator|.
name|getContainer
argument_list|(
name|containerID
argument_list|)
return|;
block|}
comment|/**    * Returns the ContainerInfo and pipeline from the containerID. If container    * has no available replicas in datanodes it returns pipeline with no    * datanodes and empty leaderID . Pipeline#isEmpty can be used to check for    * an empty pipeline.    *    * @param containerID - ID of container.    * @return - ContainerWithPipeline such as creation state and the pipeline.    * @throws IOException    */
annotation|@
name|Override
DECL|method|getContainerWithPipeline (ContainerID containerID)
specifier|public
name|ContainerWithPipeline
name|getContainerWithPipeline
parameter_list|(
name|ContainerID
name|containerID
parameter_list|)
throws|throws
name|ContainerNotFoundException
throws|,
name|PipelineNotFoundException
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|ContainerInfo
name|contInfo
init|=
name|getContainer
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
decl_stmt|;
if|if
condition|(
name|contInfo
operator|.
name|isOpen
argument_list|()
condition|)
block|{
comment|// If pipeline with given pipeline Id already exist return it
name|pipeline
operator|=
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|contInfo
operator|.
name|getPipelineID
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// For close containers create pipeline from datanodes with replicas
name|Set
argument_list|<
name|ContainerReplica
argument_list|>
name|dnWithReplicas
init|=
name|containerStateManager
operator|.
name|getContainerReplicas
argument_list|(
name|contInfo
operator|.
name|containerID
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|dns
init|=
name|dnWithReplicas
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|ContainerReplica
operator|::
name|getDatanodeDetails
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|pipeline
operator|=
name|pipelineManager
operator|.
name|createPipeline
argument_list|(
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|,
name|contInfo
operator|.
name|getReplicationFactor
argument_list|()
argument_list|,
name|dns
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ContainerWithPipeline
argument_list|(
name|contInfo
argument_list|,
name|pipeline
argument_list|)
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|listContainer (ContainerID startContainerID, int count)
specifier|public
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
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|long
name|startId
init|=
name|startContainerID
operator|==
literal|null
condition|?
literal|0
else|:
name|startContainerID
operator|.
name|getId
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ContainerID
argument_list|>
name|containersIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|containerStateManager
operator|.
name|getAllContainerIDs
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|containersIds
argument_list|)
expr_stmt|;
return|return
name|containersIds
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|id
lambda|->
name|id
operator|.
name|getId
argument_list|()
operator|>
name|startId
argument_list|)
operator|.
name|limit
argument_list|(
name|count
argument_list|)
operator|.
name|map
argument_list|(
name|id
lambda|->
block|{
lambda|try
block|{
return|return
name|containerStateManager
operator|.
name|getContainer
argument_list|(
name|id
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ContainerNotFoundException
name|ex
parameter_list|)
block|{
comment|// This can never happen, as we hold lock no one else can remove
comment|// the container after we got the container ids.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Container Missing."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|)
function|.collect
parameter_list|(
function|Collectors.toList
parameter_list|()
block|)
class|;
end_class

begin_block
unit|} finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
end_block

begin_comment
unit|}
comment|/**    * Allocates a new container.    *    * @param replicationFactor - replication factor of the container.    * @param owner - The string name of the Service that owns this container.    * @return - Pipeline that makes up this container.    * @throws IOException - Exception    */
end_comment

begin_function
unit|@
name|Override
DECL|method|allocateContainer (final ReplicationType type, final ReplicationFactor replicationFactor, final String owner)
specifier|public
name|ContainerWithPipeline
name|allocateContainer
parameter_list|(
specifier|final
name|ReplicationType
name|type
parameter_list|,
specifier|final
name|ReplicationFactor
name|replicationFactor
parameter_list|,
specifier|final
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|ContainerInfo
name|containerInfo
decl_stmt|;
name|containerInfo
operator|=
name|containerStateManager
operator|.
name|allocateContainer
argument_list|(
name|pipelineManager
argument_list|,
name|type
argument_list|,
name|replicationFactor
argument_list|,
name|owner
argument_list|)
expr_stmt|;
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
try|try
block|{
specifier|final
name|byte
index|[]
name|containerIDBytes
init|=
name|Longs
operator|.
name|toByteArray
argument_list|(
name|containerInfo
operator|.
name|getContainerID
argument_list|()
argument_list|)
decl_stmt|;
name|containerStore
operator|.
name|put
argument_list|(
name|containerIDBytes
argument_list|,
name|containerInfo
operator|.
name|getProtobuf
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// If adding to containerStore fails, we should remove the container
comment|// from in-memory map.
try|try
block|{
name|containerStateManager
operator|.
name|removeContainer
argument_list|(
name|containerInfo
operator|.
name|containerID
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ContainerNotFoundException
name|cnfe
parameter_list|)
block|{
comment|// No need to worry much, everything is going as planned.
block|}
throw|throw
name|ex
throw|;
block|}
return|return
operator|new
name|ContainerWithPipeline
argument_list|(
name|containerInfo
argument_list|,
name|pipeline
argument_list|)
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
end_function

begin_comment
comment|/**    * Deletes a container from SCM.    *    * @param containerID - Container ID    * @throws IOException if container doesn't exist or container store failed    *                     to delete the    *                     specified key.    */
end_comment

begin_function
annotation|@
name|Override
DECL|method|deleteContainer (ContainerID containerID)
specifier|public
name|void
name|deleteContainer
parameter_list|(
name|ContainerID
name|containerID
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|containerStateManager
operator|.
name|removeContainer
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|dbKey
init|=
name|Longs
operator|.
name|toByteArray
argument_list|(
name|containerID
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|containerBytes
init|=
name|containerStore
operator|.
name|get
argument_list|(
name|dbKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerBytes
operator|!=
literal|null
condition|)
block|{
name|containerStore
operator|.
name|delete
argument_list|(
name|dbKey
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Where did the container go? o_O
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to remove the container {} from container store,"
operator|+
literal|" it's missing!"
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ContainerNotFoundException
name|cnfe
parameter_list|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Failed to delete container "
operator|+
name|containerID
operator|+
literal|", reason : "
operator|+
literal|"container doesn't exist."
argument_list|,
name|SCMException
operator|.
name|ResultCodes
operator|.
name|FAILED_TO_FIND_CONTAINER
argument_list|)
throw|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
end_function

begin_comment
comment|/**    * {@inheritDoc} Used by client to update container state on SCM.    */
end_comment

begin_function
annotation|@
name|Override
DECL|method|updateContainerState ( ContainerID containerID, HddsProtos.LifeCycleEvent event)
specifier|public
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
block|{
comment|// Should we return the updated ContainerInfo instead of LifeCycleState?
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ContainerInfo
name|updatedContainer
init|=
name|updateContainerStateInternal
argument_list|(
name|containerID
argument_list|,
name|event
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|updatedContainer
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|pipelineManager
operator|.
name|removeContainerFromPipeline
argument_list|(
name|updatedContainer
operator|.
name|getPipelineID
argument_list|()
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
block|}
specifier|final
name|byte
index|[]
name|dbKey
init|=
name|Longs
operator|.
name|toByteArray
argument_list|(
name|containerID
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|containerStore
operator|.
name|put
argument_list|(
name|dbKey
argument_list|,
name|updatedContainer
operator|.
name|getProtobuf
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|updatedContainer
operator|.
name|getState
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ContainerNotFoundException
name|cnfe
parameter_list|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Failed to update container state"
operator|+
name|containerID
operator|+
literal|", reason : container doesn't exist."
argument_list|,
name|SCMException
operator|.
name|ResultCodes
operator|.
name|FAILED_TO_FIND_CONTAINER
argument_list|)
throw|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
end_function

begin_function
DECL|method|updateContainerStateInternal (ContainerID containerID, HddsProtos.LifeCycleEvent event)
specifier|private
name|ContainerInfo
name|updateContainerStateInternal
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
block|{
comment|// Refactor the below code for better clarity.
switch|switch
condition|(
name|event
condition|)
block|{
case|case
name|FINALIZE
case|:
comment|// TODO: we don't need a lease manager here for closing as the
comment|// container report will include the container state after HDFS-13008
comment|// If a client failed to update the container close state, DN container
comment|// report from 3 DNs will be used to close the container eventually.
break|break;
case|case
name|CLOSE
case|:
break|break;
case|case
name|UPDATE
case|:
break|break;
case|case
name|DELETE
case|:
break|break;
case|case
name|TIMEOUT
case|:
break|break;
case|case
name|CLEANUP
case|:
break|break;
default|default:
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Unsupported container LifeCycleEvent."
argument_list|,
name|FAILED_TO_CHANGE_CONTAINER_STATE
argument_list|)
throw|;
block|}
comment|// If the below updateContainerState call fails, we should revert the
comment|// changes made in switch case.
comment|// Like releasing the lease in case of BEGIN_CREATE.
return|return
name|containerStateManager
operator|.
name|updateContainerState
argument_list|(
name|containerID
argument_list|,
name|event
argument_list|)
return|;
block|}
end_function

begin_comment
comment|/**      * Update deleteTransactionId according to deleteTransactionMap.      *      * @param deleteTransactionMap Maps the containerId to latest delete      *                             transaction id for the container.      * @throws IOException      */
end_comment

begin_function
DECL|method|updateDeleteTransactionId (Map<Long, Long> deleteTransactionMap)
specifier|public
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
block|{
if|if
condition|(
name|deleteTransactionMap
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|BatchOperation
name|batch
init|=
operator|new
name|BatchOperation
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|deleteTransactionMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|long
name|containerID
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|byte
index|[]
name|dbKey
init|=
name|Longs
operator|.
name|toByteArray
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
name|byte
index|[]
name|containerBytes
init|=
name|containerStore
operator|.
name|get
argument_list|(
name|dbKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerBytes
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Failed to increment number of deleted blocks for container "
operator|+
name|containerID
operator|+
literal|", reason : "
operator|+
literal|"container doesn't exist."
argument_list|,
name|SCMException
operator|.
name|ResultCodes
operator|.
name|FAILED_TO_FIND_CONTAINER
argument_list|)
throw|;
block|}
name|ContainerInfo
name|containerInfo
init|=
name|ContainerInfo
operator|.
name|fromProtobuf
argument_list|(
name|HddsProtos
operator|.
name|ContainerInfoProto
operator|.
name|parseFrom
argument_list|(
name|containerBytes
argument_list|)
argument_list|)
decl_stmt|;
name|containerInfo
operator|.
name|updateDeleteTransactionId
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|batch
operator|.
name|put
argument_list|(
name|dbKey
argument_list|,
name|containerInfo
operator|.
name|getProtobuf
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|containerStore
operator|.
name|writeBatch
argument_list|(
name|batch
argument_list|)
expr_stmt|;
name|containerStateManager
operator|.
name|updateDeleteTransactionId
argument_list|(
name|deleteTransactionMap
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
end_function

begin_comment
comment|/**    * Return a container matching the attributes specified.    *    * @param sizeRequired - Space needed in the Container.    * @param owner - Owner of the container - A specific nameservice.    * @param type - Replication Type {StandAlone, Ratis}    * @param factor - Replication Factor {ONE, THREE}    * @param state - State of the Container-- {Open, Allocated etc.}    * @return ContainerInfo, null if there is no match found.    */
end_comment

begin_function
DECL|method|getMatchingContainerWithPipeline ( final long sizeRequired, String owner, ReplicationType type, ReplicationFactor factor, LifeCycleState state)
specifier|public
name|ContainerWithPipeline
name|getMatchingContainerWithPipeline
parameter_list|(
specifier|final
name|long
name|sizeRequired
parameter_list|,
name|String
name|owner
parameter_list|,
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|,
name|LifeCycleState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerInfo
name|containerInfo
init|=
name|containerStateManager
operator|.
name|getMatchingContainer
argument_list|(
name|sizeRequired
argument_list|,
name|owner
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|state
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerInfo
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
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
return|return
operator|new
name|ContainerWithPipeline
argument_list|(
name|containerInfo
argument_list|,
name|pipeline
argument_list|)
return|;
block|}
end_function

begin_comment
comment|/**    * Returns the latest list of DataNodes where replica for given containerId    * exist. Throws an SCMException if no entry is found for given containerId.    *    * @param containerID    * @return Set<DatanodeDetails>    */
end_comment

begin_function
DECL|method|getContainerReplicas ( final ContainerID containerID)
specifier|public
name|Set
argument_list|<
name|ContainerReplica
argument_list|>
name|getContainerReplicas
parameter_list|(
specifier|final
name|ContainerID
name|containerID
parameter_list|)
throws|throws
name|ContainerNotFoundException
block|{
return|return
name|containerStateManager
operator|.
name|getContainerReplicas
argument_list|(
name|containerID
argument_list|)
return|;
block|}
end_function

begin_comment
comment|/**    * Add a container Replica for given DataNode.    *    * @param containerID    * @param replica    */
end_comment

begin_function
DECL|method|updateContainerReplica (final ContainerID containerID, final ContainerReplica replica)
specifier|public
name|void
name|updateContainerReplica
parameter_list|(
specifier|final
name|ContainerID
name|containerID
parameter_list|,
specifier|final
name|ContainerReplica
name|replica
parameter_list|)
throws|throws
name|ContainerNotFoundException
block|{
name|containerStateManager
operator|.
name|updateContainerReplica
argument_list|(
name|containerID
argument_list|,
name|replica
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Remove a container Replica for given DataNode.    *    * @param containerID    * @param replica    * @return True of dataNode is removed successfully else false.    */
end_comment

begin_function
DECL|method|removeContainerReplica (final ContainerID containerID, final ContainerReplica replica)
specifier|public
name|void
name|removeContainerReplica
parameter_list|(
specifier|final
name|ContainerID
name|containerID
parameter_list|,
specifier|final
name|ContainerReplica
name|replica
parameter_list|)
throws|throws
name|ContainerNotFoundException
throws|,
name|ContainerReplicaNotFoundException
block|{
name|containerStateManager
operator|.
name|removeContainerReplica
argument_list|(
name|containerID
argument_list|,
name|replica
argument_list|)
expr_stmt|;
block|}
end_function

begin_comment
comment|/**    * Closes this stream and releases any system resources associated with it.    * If the stream is    * already closed then invoking this method has no effect.    *<p>    *<p>As noted in {@link AutoCloseable#close()}, cases where the close may    * fail require careful    * attention. It is strongly advised to relinquish the underlying resources    * and to internally    *<em>mark</em> the {@code Closeable} as closed, prior to throwing the    * {@code IOException}.    *    * @throws IOException if an I/O error occurs    */
end_comment

begin_function
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
name|containerStateManager
operator|!=
literal|null
condition|)
block|{
name|containerStateManager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|containerStore
operator|!=
literal|null
condition|)
block|{
name|containerStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_function

unit|}
end_unit

