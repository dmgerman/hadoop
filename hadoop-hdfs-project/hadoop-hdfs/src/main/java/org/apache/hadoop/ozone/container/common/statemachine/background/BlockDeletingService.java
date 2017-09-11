begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.statemachine.background
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|background
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
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|InvalidProtocolBufferException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|hdfs
operator|.
name|DFSUtil
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
name|container
operator|.
name|common
operator|.
name|helpers
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerUtils
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|KeyUtils
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
name|container
operator|.
name|common
operator|.
name|interfaces
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|StorageContainerException
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
name|Time
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
name|BackgroundService
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
name|BackgroundTaskResult
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
name|BackgroundTaskQueue
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
name|BackgroundTask
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
name|MetadataKeyFilters
operator|.
name|KeyPrefixFilter
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
name|LinkedList
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER
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
name|OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER_DEFAULT
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
name|OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL
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
name|OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL_DEFAULT
import|;
end_import

begin_comment
comment|/**  * A per-datanode container block deleting service takes in charge  * of deleting staled ozone blocks.  */
end_comment

begin_class
DECL|class|BlockDeletingService
specifier|public
class|class
name|BlockDeletingService
extends|extends
name|BackgroundService
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
name|BlockDeletingService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|containerManager
specifier|private
specifier|final
name|ContainerManager
name|containerManager
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
comment|// Throttle number of blocks to delete per task,
comment|// set to 1 for testing
DECL|field|blockLimitPerTask
specifier|private
specifier|final
name|int
name|blockLimitPerTask
decl_stmt|;
comment|// Throttle the number of containers to process concurrently at a time,
DECL|field|containerLimitPerInterval
specifier|private
specifier|final
name|int
name|containerLimitPerInterval
decl_stmt|;
comment|// Task priority is useful when a to-delete block has weight.
DECL|field|TASK_PRIORITY_DEFAULT
specifier|private
specifier|final
specifier|static
name|int
name|TASK_PRIORITY_DEFAULT
init|=
literal|1
decl_stmt|;
comment|// Core pool size for container tasks
DECL|field|BLOCK_DELETING_SERVICE_CORE_POOL_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|BLOCK_DELETING_SERVICE_CORE_POOL_SIZE
init|=
literal|10
decl_stmt|;
DECL|method|BlockDeletingService (ContainerManager containerManager, int serviceInterval, long serviceTimeout, Configuration conf)
specifier|public
name|BlockDeletingService
parameter_list|(
name|ContainerManager
name|containerManager
parameter_list|,
name|int
name|serviceInterval
parameter_list|,
name|long
name|serviceTimeout
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
literal|"BlockDeletingService"
argument_list|,
name|serviceInterval
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|BLOCK_DELETING_SERVICE_CORE_POOL_SIZE
argument_list|,
name|serviceTimeout
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerManager
operator|=
name|containerManager
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|blockLimitPerTask
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER
argument_list|,
name|OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerLimitPerInterval
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL
argument_list|,
name|OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL_DEFAULT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTasks ()
specifier|public
name|BackgroundTaskQueue
name|getTasks
parameter_list|()
block|{
name|BackgroundTaskQueue
name|queue
init|=
operator|new
name|BackgroundTaskQueue
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ContainerData
argument_list|>
name|containers
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
try|try
block|{
comment|// We at most list a number of containers a time,
comment|// in case there are too many containers and start too many workers.
comment|// We must ensure there is no empty container in this result.
comment|// The chosen result depends on what container deletion policy is
comment|// configured.
name|containers
operator|=
name|containerManager
operator|.
name|chooseContainerForBlockDeletion
argument_list|(
name|containerLimitPerInterval
argument_list|)
expr_stmt|;
for|for
control|(
name|ContainerData
name|container
range|:
name|containers
control|)
block|{
name|BlockDeletingTask
name|containerTask
init|=
operator|new
name|BlockDeletingTask
argument_list|(
name|container
argument_list|,
name|TASK_PRIORITY_DEFAULT
argument_list|)
decl_stmt|;
name|queue
operator|.
name|add
argument_list|(
name|containerTask
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|StorageContainerException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to initiate block deleting tasks, "
operator|+
literal|"caused by unable to get containers info. "
operator|+
literal|"Retry in next interval. "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// In case listContainer call throws any uncaught RuntimeException.
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
literal|"Unexpected error occurs during deleting blocks."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|queue
return|;
block|}
DECL|class|ContainerBackgroundTaskResult
specifier|private
specifier|static
class|class
name|ContainerBackgroundTaskResult
implements|implements
name|BackgroundTaskResult
block|{
DECL|field|deletedBlockIds
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|deletedBlockIds
decl_stmt|;
DECL|method|ContainerBackgroundTaskResult ()
name|ContainerBackgroundTaskResult
parameter_list|()
block|{
name|deletedBlockIds
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|addBlockId (String blockId)
specifier|public
name|void
name|addBlockId
parameter_list|(
name|String
name|blockId
parameter_list|)
block|{
name|deletedBlockIds
operator|.
name|add
argument_list|(
name|blockId
argument_list|)
expr_stmt|;
block|}
DECL|method|addAll (List<String> blockIds)
specifier|public
name|void
name|addAll
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|blockIds
parameter_list|)
block|{
name|deletedBlockIds
operator|.
name|addAll
argument_list|(
name|blockIds
argument_list|)
expr_stmt|;
block|}
DECL|method|getDeletedBlocks ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getDeletedBlocks
parameter_list|()
block|{
return|return
name|deletedBlockIds
return|;
block|}
annotation|@
name|Override
DECL|method|getSize ()
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|deletedBlockIds
operator|.
name|size
argument_list|()
return|;
block|}
block|}
DECL|class|BlockDeletingTask
specifier|private
class|class
name|BlockDeletingTask
implements|implements
name|BackgroundTask
argument_list|<
name|BackgroundTaskResult
argument_list|>
block|{
DECL|field|priority
specifier|private
specifier|final
name|int
name|priority
decl_stmt|;
DECL|field|containerData
specifier|private
specifier|final
name|ContainerData
name|containerData
decl_stmt|;
DECL|method|BlockDeletingTask (ContainerData containerName, int priority)
name|BlockDeletingTask
parameter_list|(
name|ContainerData
name|containerName
parameter_list|,
name|int
name|priority
parameter_list|)
block|{
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
name|this
operator|.
name|containerData
operator|=
name|containerName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|BackgroundTaskResult
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|ContainerBackgroundTaskResult
name|crr
init|=
operator|new
name|ContainerBackgroundTaskResult
argument_list|()
decl_stmt|;
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
comment|// Scan container's db and get list of under deletion blocks
name|MetadataStore
name|meta
init|=
name|KeyUtils
operator|.
name|getDB
argument_list|(
name|containerData
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// # of blocks to delete is throttled
name|KeyPrefixFilter
name|filter
init|=
operator|new
name|KeyPrefixFilter
argument_list|(
name|OzoneConsts
operator|.
name|DELETING_KEY_PREFIX
argument_list|)
decl_stmt|;
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
name|toDeleteBlocks
init|=
name|meta
operator|.
name|getRangeKVs
argument_list|(
literal|null
argument_list|,
name|blockLimitPerTask
argument_list|,
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|toDeleteBlocks
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No under deletion block found in container : {}"
argument_list|,
name|containerData
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|succeedBlocks
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Container : {}, To-Delete blocks : {}"
argument_list|,
name|containerData
operator|.
name|getContainerName
argument_list|()
argument_list|,
name|toDeleteBlocks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|dataDir
init|=
name|ContainerUtils
operator|.
name|getDataDirectory
argument_list|(
name|containerData
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|dataDir
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|dataDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Invalid container data dir {} : "
operator|+
literal|"not exist or not a directory"
argument_list|,
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|crr
return|;
block|}
name|toDeleteBlocks
operator|.
name|forEach
argument_list|(
name|entry
lambda|->
block|{
name|String
name|blockName
init|=
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Deleting block {}"
argument_list|,
name|blockName
argument_list|)
expr_stmt|;
try|try
block|{
name|ContainerProtos
operator|.
name|KeyData
name|data
init|=
name|ContainerProtos
operator|.
name|KeyData
operator|.
name|parseFrom
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ContainerProtos
operator|.
name|ChunkInfo
name|chunkInfo
range|:
name|data
operator|.
name|getChunksList
argument_list|()
control|)
block|{
name|File
name|chunkFile
init|=
name|dataDir
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
name|chunkInfo
operator|.
name|getChunkName
argument_list|()
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|chunkFile
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"block {} chunk {} deleted"
argument_list|,
name|blockName
argument_list|,
name|chunkFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|succeedBlocks
operator|.
name|add
argument_list|(
name|blockName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidProtocolBufferException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to parse block info for block {}"
argument_list|,
name|blockName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Once files are deleted ... clean up DB
name|BatchOperation
name|batch
init|=
operator|new
name|BatchOperation
argument_list|()
decl_stmt|;
name|succeedBlocks
operator|.
name|forEach
argument_list|(
name|entry
lambda|->
name|batch
operator|.
name|delete
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|entry
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeBatch
argument_list|(
name|batch
argument_list|)
expr_stmt|;
comment|// update count of pending deletion blocks in in-memory container status
name|containerManager
operator|.
name|decrPendingDeletionBlocks
argument_list|(
name|succeedBlocks
operator|.
name|size
argument_list|()
argument_list|,
name|containerData
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|succeedBlocks
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Container: {}, deleted blocks: {}, task elapsed time: {}ms"
argument_list|,
name|containerData
operator|.
name|getContainerName
argument_list|()
argument_list|,
name|succeedBlocks
operator|.
name|size
argument_list|()
argument_list|,
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|startTime
argument_list|)
expr_stmt|;
block|}
name|crr
operator|.
name|addAll
argument_list|(
name|succeedBlocks
argument_list|)
expr_stmt|;
return|return
name|crr
return|;
block|}
annotation|@
name|Override
DECL|method|getPriority ()
specifier|public
name|int
name|getPriority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
block|}
block|}
end_class

end_unit

