begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.pipeline
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
name|pipeline
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
name|container
operator|.
name|ContainerID
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
name|events
operator|.
name|SCMEvents
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
name|MetadataKeyFilters
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
name|NavigableSet
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
name|Collection
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
name|ReadWriteLock
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
name|ReentrantReadWriteLock
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
name|ozone
operator|.
name|OzoneConsts
operator|.
name|SCM_PIPELINE_DB
import|;
end_import

begin_comment
comment|/**  * Implements api needed for management of pipelines. All the write operations  * for pipelines must come via PipelineManager. It synchronises all write  * and read operations via a ReadWriteLock.  */
end_comment

begin_class
DECL|class|SCMPipelineManager
specifier|public
class|class
name|SCMPipelineManager
implements|implements
name|PipelineManager
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
name|SCMPipelineManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|ReadWriteLock
name|lock
decl_stmt|;
DECL|field|pipelineFactory
specifier|private
specifier|final
name|PipelineFactory
name|pipelineFactory
decl_stmt|;
DECL|field|stateManager
specifier|private
specifier|final
name|PipelineStateManager
name|stateManager
decl_stmt|;
DECL|field|pipelineStore
specifier|private
specifier|final
name|MetadataStore
name|pipelineStore
decl_stmt|;
DECL|field|eventPublisher
specifier|private
specifier|final
name|EventPublisher
name|eventPublisher
decl_stmt|;
DECL|field|nodeManager
specifier|private
specifier|final
name|NodeManager
name|nodeManager
decl_stmt|;
DECL|field|metrics
specifier|private
specifier|final
name|SCMPipelineMetrics
name|metrics
decl_stmt|;
comment|// Pipeline Manager MXBean
DECL|field|pmInfoBean
specifier|private
name|ObjectName
name|pmInfoBean
decl_stmt|;
DECL|method|SCMPipelineManager (Configuration conf, NodeManager nodeManager, EventPublisher eventPublisher)
specifier|public
name|SCMPipelineManager
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|NodeManager
name|nodeManager
parameter_list|,
name|EventPublisher
name|eventPublisher
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|lock
operator|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
expr_stmt|;
name|this
operator|.
name|stateManager
operator|=
operator|new
name|PipelineStateManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|pipelineFactory
operator|=
operator|new
name|PipelineFactory
argument_list|(
name|nodeManager
argument_list|,
name|stateManager
argument_list|,
name|conf
argument_list|)
expr_stmt|;
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
name|pipelineDBPath
init|=
operator|new
name|File
argument_list|(
name|metaDir
argument_list|,
name|SCM_PIPELINE_DB
argument_list|)
decl_stmt|;
name|this
operator|.
name|pipelineStore
operator|=
name|MetadataStoreBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCreateIfMissing
argument_list|(
literal|true
argument_list|)
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
operator|.
name|setDbFile
argument_list|(
name|pipelineDBPath
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
name|eventPublisher
operator|=
name|eventPublisher
expr_stmt|;
name|this
operator|.
name|nodeManager
operator|=
name|nodeManager
expr_stmt|;
name|this
operator|.
name|metrics
operator|=
name|SCMPipelineMetrics
operator|.
name|create
argument_list|()
expr_stmt|;
name|this
operator|.
name|pmInfoBean
operator|=
name|MBeans
operator|.
name|register
argument_list|(
literal|"SCMPipelineManager"
argument_list|,
literal|"SCMPipelineManagerInfo"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|initializePipelineState
argument_list|()
expr_stmt|;
block|}
DECL|method|getStateManager ()
specifier|public
name|PipelineStateManager
name|getStateManager
parameter_list|()
block|{
return|return
name|stateManager
return|;
block|}
DECL|method|setPipelineProvider (ReplicationType replicationType, PipelineProvider provider)
specifier|public
name|void
name|setPipelineProvider
parameter_list|(
name|ReplicationType
name|replicationType
parameter_list|,
name|PipelineProvider
name|provider
parameter_list|)
block|{
name|pipelineFactory
operator|.
name|setProvider
argument_list|(
name|replicationType
argument_list|,
name|provider
argument_list|)
expr_stmt|;
block|}
DECL|method|initializePipelineState ()
specifier|private
name|void
name|initializePipelineState
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|pipelineStore
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"No pipeline exists in current db"
argument_list|)
expr_stmt|;
return|return;
block|}
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
name|pipelines
init|=
name|pipelineStore
operator|.
name|getSequentialRangeKVs
argument_list|(
literal|null
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
operator|(
name|MetadataKeyFilters
operator|.
name|MetadataKeyFilter
index|[]
operator|)
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
name|pipelines
control|)
block|{
name|HddsProtos
operator|.
name|Pipeline
operator|.
name|Builder
name|pipelineBuilder
init|=
name|HddsProtos
operator|.
name|Pipeline
operator|.
name|newBuilder
argument_list|(
name|HddsProtos
operator|.
name|Pipeline
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
name|Pipeline
name|pipeline
init|=
name|Pipeline
operator|.
name|getFromProtobuf
argument_list|(
name|pipelineBuilder
operator|.
name|setState
argument_list|(
name|HddsProtos
operator|.
name|PipelineState
operator|.
name|PIPELINE_ALLOCATED
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|nodeManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|createPipeline ( ReplicationType type, ReplicationFactor factor)
specifier|public
specifier|synchronized
name|Pipeline
name|createPipeline
parameter_list|(
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|Pipeline
name|pipeline
init|=
name|pipelineFactory
operator|.
name|create
argument_list|(
name|type
argument_list|,
name|factor
argument_list|)
decl_stmt|;
name|pipelineStore
operator|.
name|put
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
operator|.
name|getProtobuf
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|pipeline
operator|.
name|getProtobufMessage
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|stateManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|nodeManager
operator|.
name|addPipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|incNumPipelineCreated
argument_list|()
expr_stmt|;
return|return
name|pipeline
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|metrics
operator|.
name|incNumPipelineCreationFailed
argument_list|()
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|createPipeline (ReplicationType type, ReplicationFactor factor, List<DatanodeDetails> nodes)
specifier|public
name|Pipeline
name|createPipeline
parameter_list|(
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|,
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|nodes
parameter_list|)
block|{
comment|// This will mostly be used to create dummy pipeline for SimplePipelines.
comment|// We don't update the metrics for SimplePipelines.
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|pipelineFactory
operator|.
name|create
argument_list|(
name|type
argument_list|,
name|factor
argument_list|,
name|nodes
argument_list|)
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getPipeline (PipelineID pipelineID)
specifier|public
name|Pipeline
name|getPipeline
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|PipelineNotFoundException
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|stateManager
operator|.
name|getPipeline
argument_list|(
name|pipelineID
argument_list|)
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getPipelines ()
specifier|public
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getPipelines
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|stateManager
operator|.
name|getPipelines
argument_list|()
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getPipelines (ReplicationType type)
specifier|public
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getPipelines
parameter_list|(
name|ReplicationType
name|type
parameter_list|)
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|stateManager
operator|.
name|getPipelines
argument_list|(
name|type
argument_list|)
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getPipelines (ReplicationType type, ReplicationFactor factor)
specifier|public
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getPipelines
parameter_list|(
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|)
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|stateManager
operator|.
name|getPipelines
argument_list|(
name|type
argument_list|,
name|factor
argument_list|)
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getPipelines (ReplicationType type, ReplicationFactor factor, Pipeline.PipelineState state)
specifier|public
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getPipelines
parameter_list|(
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|,
name|Pipeline
operator|.
name|PipelineState
name|state
parameter_list|)
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|stateManager
operator|.
name|getPipelines
argument_list|(
name|type
argument_list|,
name|factor
argument_list|,
name|state
argument_list|)
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getPipelines (ReplicationType type, ReplicationFactor factor, Pipeline.PipelineState state, Collection<DatanodeDetails> excludeDns, Collection<PipelineID> excludePipelines)
specifier|public
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getPipelines
parameter_list|(
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|,
name|Pipeline
operator|.
name|PipelineState
name|state
parameter_list|,
name|Collection
argument_list|<
name|DatanodeDetails
argument_list|>
name|excludeDns
parameter_list|,
name|Collection
argument_list|<
name|PipelineID
argument_list|>
name|excludePipelines
parameter_list|)
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|stateManager
operator|.
name|getPipelines
argument_list|(
name|type
argument_list|,
name|factor
argument_list|,
name|state
argument_list|,
name|excludeDns
argument_list|,
name|excludePipelines
argument_list|)
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addContainerToPipeline (PipelineID pipelineID, ContainerID containerID)
specifier|public
name|void
name|addContainerToPipeline
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|,
name|ContainerID
name|containerID
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|stateManager
operator|.
name|addContainerToPipeline
argument_list|(
name|pipelineID
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|removeContainerFromPipeline (PipelineID pipelineID, ContainerID containerID)
specifier|public
name|void
name|removeContainerFromPipeline
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|,
name|ContainerID
name|containerID
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|stateManager
operator|.
name|removeContainerFromPipeline
argument_list|(
name|pipelineID
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getContainersInPipeline ( PipelineID pipelineID)
specifier|public
name|NavigableSet
argument_list|<
name|ContainerID
argument_list|>
name|getContainersInPipeline
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|stateManager
operator|.
name|getContainers
argument_list|(
name|pipelineID
argument_list|)
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNumberOfContainers (PipelineID pipelineID)
specifier|public
name|int
name|getNumberOfContainers
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|stateManager
operator|.
name|getNumberOfContainers
argument_list|(
name|pipelineID
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|finalizePipeline (PipelineID pipelineId)
specifier|public
name|void
name|finalizePipeline
parameter_list|(
name|PipelineID
name|pipelineId
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|stateManager
operator|.
name|finalizePipeline
argument_list|(
name|pipelineId
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|containerIDs
init|=
name|stateManager
operator|.
name|getContainers
argument_list|(
name|pipelineId
argument_list|)
decl_stmt|;
for|for
control|(
name|ContainerID
name|containerID
range|:
name|containerIDs
control|)
block|{
name|eventPublisher
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|CLOSE_CONTAINER
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|openPipeline (PipelineID pipelineId)
specifier|public
name|void
name|openPipeline
parameter_list|(
name|PipelineID
name|pipelineId
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|stateManager
operator|.
name|openPipeline
argument_list|(
name|pipelineId
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|removePipeline (PipelineID pipelineID)
specifier|public
name|void
name|removePipeline
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|pipelineStore
operator|.
name|delete
argument_list|(
name|pipelineID
operator|.
name|getProtobuf
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|Pipeline
name|pipeline
init|=
name|stateManager
operator|.
name|removePipeline
argument_list|(
name|pipelineID
argument_list|)
decl_stmt|;
name|nodeManager
operator|.
name|removePipeline
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|incNumPipelineDestroyed
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|metrics
operator|.
name|incNumPipelineDestroyFailed
argument_list|()
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getPipelineInfo ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getPipelineInfo
parameter_list|()
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|pipelineInfo
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Pipeline
operator|.
name|PipelineState
name|state
range|:
name|Pipeline
operator|.
name|PipelineState
operator|.
name|values
argument_list|()
control|)
block|{
name|pipelineInfo
operator|.
name|put
argument_list|(
name|state
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|stateManager
operator|.
name|getPipelines
argument_list|()
operator|.
name|forEach
argument_list|(
name|pipeline
lambda|->
name|pipelineInfo
operator|.
name|computeIfPresent
argument_list|(
name|pipeline
operator|.
name|getPipelineState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
parameter_list|(
name|k
parameter_list|,
name|v
parameter_list|)
lambda|->
name|v
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|pipelineInfo
return|;
block|}
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
name|pipelineFactory
operator|!=
literal|null
condition|)
block|{
name|pipelineFactory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|pipelineStore
operator|!=
literal|null
condition|)
block|{
name|pipelineStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|pmInfoBean
operator|!=
literal|null
condition|)
block|{
name|MBeans
operator|.
name|unregister
argument_list|(
name|this
operator|.
name|pmInfoBean
argument_list|)
expr_stmt|;
name|pmInfoBean
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|metrics
operator|!=
literal|null
condition|)
block|{
name|metrics
operator|.
name|unRegister
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

