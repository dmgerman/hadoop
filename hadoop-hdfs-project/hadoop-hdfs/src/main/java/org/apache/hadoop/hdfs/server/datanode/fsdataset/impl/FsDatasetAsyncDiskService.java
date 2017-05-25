begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|impl
package|;
end_package

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
name|HashSet
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
name|Map
operator|.
name|Entry
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
name|LinkedBlockingQueue
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
name|ThreadFactory
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
name|ThreadPoolExecutor
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
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|protocol
operator|.
name|ExtendedBlock
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
name|server
operator|.
name|datanode
operator|.
name|DataNode
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
name|server
operator|.
name|datanode
operator|.
name|DatanodeUtil
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
name|server
operator|.
name|datanode
operator|.
name|ReplicaInfo
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
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|FsVolumeReference
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
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|ReplicaOutputStreams
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
name|server
operator|.
name|protocol
operator|.
name|BlockCommand
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
name|io
operator|.
name|IOUtils
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
name|io
operator|.
name|nativeio
operator|.
name|NativeIOException
import|;
end_import

begin_comment
comment|/**  * This class is a container of multiple thread pools, each for a volume,  * so that we can schedule async disk operations easily.  *   * Examples of async disk operations are deletion of block files.  * We don't want to create a new thread for each of the deletion request, and  * we don't want to do all deletions in the heartbeat thread since deletion  * can be slow, and we don't want to use a single thread pool because that  * is inefficient when we have more than 1 volume.  AsyncDiskService is the  * solution for these.  * Another example of async disk operation is requesting sync_file_range().  *   * This class and {@link org.apache.hadoop.util.AsyncDiskService} are similar.  * They should be combined.  */
end_comment

begin_class
DECL|class|FsDatasetAsyncDiskService
class|class
name|FsDatasetAsyncDiskService
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FsDatasetAsyncDiskService
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// ThreadPool core pool size
DECL|field|CORE_THREADS_PER_VOLUME
specifier|private
specifier|static
specifier|final
name|int
name|CORE_THREADS_PER_VOLUME
init|=
literal|1
decl_stmt|;
comment|// ThreadPool maximum pool size
DECL|field|MAXIMUM_THREADS_PER_VOLUME
specifier|private
specifier|static
specifier|final
name|int
name|MAXIMUM_THREADS_PER_VOLUME
init|=
literal|4
decl_stmt|;
comment|// ThreadPool keep-alive time for threads over core pool size
DECL|field|THREADS_KEEP_ALIVE_SECONDS
specifier|private
specifier|static
specifier|final
name|long
name|THREADS_KEEP_ALIVE_SECONDS
init|=
literal|60
decl_stmt|;
DECL|field|datanode
specifier|private
specifier|final
name|DataNode
name|datanode
decl_stmt|;
DECL|field|fsdatasetImpl
specifier|private
specifier|final
name|FsDatasetImpl
name|fsdatasetImpl
decl_stmt|;
DECL|field|threadGroup
specifier|private
specifier|final
name|ThreadGroup
name|threadGroup
decl_stmt|;
DECL|field|executors
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ThreadPoolExecutor
argument_list|>
name|executors
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ThreadPoolExecutor
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|deletedBlockIds
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Long
argument_list|>
argument_list|>
name|deletedBlockIds
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Long
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|MAX_DELETED_BLOCKS
specifier|private
specifier|static
specifier|final
name|int
name|MAX_DELETED_BLOCKS
init|=
literal|64
decl_stmt|;
DECL|field|numDeletedBlocks
specifier|private
name|int
name|numDeletedBlocks
init|=
literal|0
decl_stmt|;
comment|/**    * Create a AsyncDiskServices with a set of volumes (specified by their    * root directories).    *     * The AsyncDiskServices uses one ThreadPool per volume to do the async    * disk operations.    */
DECL|method|FsDatasetAsyncDiskService (DataNode datanode, FsDatasetImpl fsdatasetImpl)
name|FsDatasetAsyncDiskService
parameter_list|(
name|DataNode
name|datanode
parameter_list|,
name|FsDatasetImpl
name|fsdatasetImpl
parameter_list|)
block|{
name|this
operator|.
name|datanode
operator|=
name|datanode
expr_stmt|;
name|this
operator|.
name|fsdatasetImpl
operator|=
name|fsdatasetImpl
expr_stmt|;
name|this
operator|.
name|threadGroup
operator|=
operator|new
name|ThreadGroup
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|addExecutorForVolume (final FsVolumeImpl volume)
specifier|private
name|void
name|addExecutorForVolume
parameter_list|(
specifier|final
name|FsVolumeImpl
name|volume
parameter_list|)
block|{
name|ThreadFactory
name|threadFactory
init|=
operator|new
name|ThreadFactory
argument_list|()
block|{
name|int
name|counter
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|r
parameter_list|)
block|{
name|int
name|thisIndex
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|thisIndex
operator|=
name|counter
operator|++
expr_stmt|;
block|}
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
name|threadGroup
argument_list|,
name|r
argument_list|)
decl_stmt|;
name|t
operator|.
name|setName
argument_list|(
literal|"Async disk worker #"
operator|+
name|thisIndex
operator|+
literal|" for volume "
operator|+
name|volume
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
decl_stmt|;
name|ThreadPoolExecutor
name|executor
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|CORE_THREADS_PER_VOLUME
argument_list|,
name|MAXIMUM_THREADS_PER_VOLUME
argument_list|,
name|THREADS_KEEP_ALIVE_SECONDS
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
name|threadFactory
argument_list|)
decl_stmt|;
comment|// This can reduce the number of running threads
name|executor
operator|.
name|allowCoreThreadTimeOut
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|executors
operator|.
name|put
argument_list|(
name|volume
operator|.
name|getStorageID
argument_list|()
argument_list|,
name|executor
argument_list|)
expr_stmt|;
block|}
comment|/**    * Starts AsyncDiskService for a new volume    * @param volume the root of the new data volume.    */
DECL|method|addVolume (FsVolumeImpl volume)
specifier|synchronized
name|void
name|addVolume
parameter_list|(
name|FsVolumeImpl
name|volume
parameter_list|)
block|{
if|if
condition|(
name|executors
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"AsyncDiskService is already shutdown"
argument_list|)
throw|;
block|}
if|if
condition|(
name|volume
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Attempt to add a null volume"
argument_list|)
throw|;
block|}
name|ThreadPoolExecutor
name|executor
init|=
name|executors
operator|.
name|get
argument_list|(
name|volume
operator|.
name|getStorageID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|executor
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Volume "
operator|+
name|volume
operator|+
literal|" is already existed."
argument_list|)
throw|;
block|}
name|addExecutorForVolume
argument_list|(
name|volume
argument_list|)
expr_stmt|;
block|}
comment|/**    * Stops AsyncDiskService for a volume.    * @param volume the root of the volume.    */
DECL|method|removeVolume (String storageId)
specifier|synchronized
name|void
name|removeVolume
parameter_list|(
name|String
name|storageId
parameter_list|)
block|{
if|if
condition|(
name|executors
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"AsyncDiskService is already shutdown"
argument_list|)
throw|;
block|}
name|ThreadPoolExecutor
name|executor
init|=
name|executors
operator|.
name|get
argument_list|(
name|storageId
argument_list|)
decl_stmt|;
if|if
condition|(
name|executor
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Can not find volume with storageId "
operator|+
name|storageId
operator|+
literal|" to remove."
argument_list|)
throw|;
block|}
else|else
block|{
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executors
operator|.
name|remove
argument_list|(
name|storageId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|countPendingDeletions ()
specifier|synchronized
name|long
name|countPendingDeletions
parameter_list|()
block|{
name|long
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ThreadPoolExecutor
name|exec
range|:
name|executors
operator|.
name|values
argument_list|()
control|)
block|{
name|count
operator|+=
name|exec
operator|.
name|getTaskCount
argument_list|()
operator|-
name|exec
operator|.
name|getCompletedTaskCount
argument_list|()
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
comment|/**    * Execute the task sometime in the future, using ThreadPools.    */
DECL|method|execute (FsVolumeImpl volume, Runnable task)
specifier|synchronized
name|void
name|execute
parameter_list|(
name|FsVolumeImpl
name|volume
parameter_list|,
name|Runnable
name|task
parameter_list|)
block|{
if|if
condition|(
name|executors
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"AsyncDiskService is already shutdown"
argument_list|)
throw|;
block|}
if|if
condition|(
name|volume
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"A null volume does not have a executor"
argument_list|)
throw|;
block|}
name|ThreadPoolExecutor
name|executor
init|=
name|executors
operator|.
name|get
argument_list|(
name|volume
operator|.
name|getStorageID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|executor
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot find volume "
operator|+
name|volume
operator|+
literal|" for execution of task "
operator|+
name|task
argument_list|)
throw|;
block|}
else|else
block|{
name|executor
operator|.
name|execute
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Gracefully shut down all ThreadPool. Will wait for all deletion    * tasks to finish.    */
DECL|method|shutdown ()
specifier|synchronized
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|executors
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"AsyncDiskService has already shut down."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down all async disk service threads"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ThreadPoolExecutor
argument_list|>
name|e
range|:
name|executors
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|// clear the executor map so that calling execute again will fail.
name|executors
operator|=
literal|null
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"All async disk service threads have been shut down"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|submitSyncFileRangeRequest (FsVolumeImpl volume, final ReplicaOutputStreams streams, final long offset, final long nbytes, final int flags)
specifier|public
name|void
name|submitSyncFileRangeRequest
parameter_list|(
name|FsVolumeImpl
name|volume
parameter_list|,
specifier|final
name|ReplicaOutputStreams
name|streams
parameter_list|,
specifier|final
name|long
name|offset
parameter_list|,
specifier|final
name|long
name|nbytes
parameter_list|,
specifier|final
name|int
name|flags
parameter_list|)
block|{
name|execute
argument_list|(
name|volume
argument_list|,
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|streams
operator|.
name|syncFileRangeIfPossible
argument_list|(
name|offset
argument_list|,
name|nbytes
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NativeIOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"sync_file_range error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Delete the block file and meta file from the disk asynchronously, adjust    * dfsUsed statistics accordingly.    */
DECL|method|deleteAsync (FsVolumeReference volumeRef, ReplicaInfo replicaToDelete, ExtendedBlock block, String trashDirectory)
name|void
name|deleteAsync
parameter_list|(
name|FsVolumeReference
name|volumeRef
parameter_list|,
name|ReplicaInfo
name|replicaToDelete
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|,
name|String
name|trashDirectory
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Scheduling "
operator|+
name|block
operator|.
name|getLocalBlock
argument_list|()
operator|+
literal|" replica "
operator|+
name|replicaToDelete
operator|+
literal|" for deletion"
argument_list|)
expr_stmt|;
name|ReplicaFileDeleteTask
name|deletionTask
init|=
operator|new
name|ReplicaFileDeleteTask
argument_list|(
name|volumeRef
argument_list|,
name|replicaToDelete
argument_list|,
name|block
argument_list|,
name|trashDirectory
argument_list|)
decl_stmt|;
name|execute
argument_list|(
operator|(
operator|(
name|FsVolumeImpl
operator|)
name|volumeRef
operator|.
name|getVolume
argument_list|()
operator|)
argument_list|,
name|deletionTask
argument_list|)
expr_stmt|;
block|}
comment|/**    * Delete the block file and meta file from the disk synchronously, adjust    * dfsUsed statistics accordingly.    */
DECL|method|deleteSync (FsVolumeReference volumeRef, ReplicaInfo replicaToDelete, ExtendedBlock block, String trashDirectory)
name|void
name|deleteSync
parameter_list|(
name|FsVolumeReference
name|volumeRef
parameter_list|,
name|ReplicaInfo
name|replicaToDelete
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|,
name|String
name|trashDirectory
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting "
operator|+
name|block
operator|.
name|getLocalBlock
argument_list|()
operator|+
literal|" replica "
operator|+
name|replicaToDelete
argument_list|)
expr_stmt|;
name|ReplicaFileDeleteTask
name|deletionTask
init|=
operator|new
name|ReplicaFileDeleteTask
argument_list|(
name|volumeRef
argument_list|,
name|replicaToDelete
argument_list|,
name|block
argument_list|,
name|trashDirectory
argument_list|)
decl_stmt|;
name|deletionTask
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
comment|/** A task for deleting a block file and its associated meta file, as well    *  as decrement the dfs usage of the volume.    *  Optionally accepts a trash directory. If one is specified then the files    *  are moved to trash instead of being deleted. If none is specified then the    *  files are deleted immediately.    */
DECL|class|ReplicaFileDeleteTask
class|class
name|ReplicaFileDeleteTask
implements|implements
name|Runnable
block|{
DECL|field|volumeRef
specifier|private
specifier|final
name|FsVolumeReference
name|volumeRef
decl_stmt|;
DECL|field|volume
specifier|private
specifier|final
name|FsVolumeImpl
name|volume
decl_stmt|;
DECL|field|replicaToDelete
specifier|private
specifier|final
name|ReplicaInfo
name|replicaToDelete
decl_stmt|;
DECL|field|block
specifier|private
specifier|final
name|ExtendedBlock
name|block
decl_stmt|;
DECL|field|trashDirectory
specifier|private
specifier|final
name|String
name|trashDirectory
decl_stmt|;
DECL|method|ReplicaFileDeleteTask (FsVolumeReference volumeRef, ReplicaInfo replicaToDelete, ExtendedBlock block, String trashDirectory)
name|ReplicaFileDeleteTask
parameter_list|(
name|FsVolumeReference
name|volumeRef
parameter_list|,
name|ReplicaInfo
name|replicaToDelete
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|,
name|String
name|trashDirectory
parameter_list|)
block|{
name|this
operator|.
name|volumeRef
operator|=
name|volumeRef
expr_stmt|;
name|this
operator|.
name|volume
operator|=
operator|(
name|FsVolumeImpl
operator|)
name|volumeRef
operator|.
name|getVolume
argument_list|()
expr_stmt|;
name|this
operator|.
name|replicaToDelete
operator|=
name|replicaToDelete
expr_stmt|;
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
name|this
operator|.
name|trashDirectory
operator|=
name|trashDirectory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|// Called in AsyncDiskService.execute for displaying error messages.
return|return
literal|"deletion of block "
operator|+
name|block
operator|.
name|getBlockPoolId
argument_list|()
operator|+
literal|" "
operator|+
name|block
operator|.
name|getLocalBlock
argument_list|()
operator|+
literal|" with block file "
operator|+
name|replicaToDelete
operator|.
name|getBlockURI
argument_list|()
operator|+
literal|" and meta file "
operator|+
name|replicaToDelete
operator|.
name|getMetadataURI
argument_list|()
operator|+
literal|" from volume "
operator|+
name|volume
return|;
block|}
DECL|method|deleteFiles ()
specifier|private
name|boolean
name|deleteFiles
parameter_list|()
block|{
return|return
name|replicaToDelete
operator|.
name|deleteBlockData
argument_list|()
operator|&&
operator|(
name|replicaToDelete
operator|.
name|deleteMetadata
argument_list|()
operator|||
operator|!
name|replicaToDelete
operator|.
name|metadataExists
argument_list|()
operator|)
return|;
block|}
DECL|method|moveFiles ()
specifier|private
name|boolean
name|moveFiles
parameter_list|()
block|{
if|if
condition|(
name|trashDirectory
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Trash dir for replica "
operator|+
name|replicaToDelete
operator|+
literal|" is null"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|File
name|trashDirFile
init|=
operator|new
name|File
argument_list|(
name|trashDirectory
argument_list|)
decl_stmt|;
try|try
block|{
name|volume
operator|.
name|getFileIoProvider
argument_list|()
operator|.
name|mkdirsWithExistsCheck
argument_list|(
name|volume
argument_list|,
name|trashDirFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
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
literal|"Moving files "
operator|+
name|replicaToDelete
operator|.
name|getBlockURI
argument_list|()
operator|+
literal|" and "
operator|+
name|replicaToDelete
operator|.
name|getMetadataURI
argument_list|()
operator|+
literal|" to trash."
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|blockName
init|=
name|replicaToDelete
operator|.
name|getBlockName
argument_list|()
decl_stmt|;
specifier|final
name|long
name|genstamp
init|=
name|replicaToDelete
operator|.
name|getGenerationStamp
argument_list|()
decl_stmt|;
name|File
name|newBlockFile
init|=
operator|new
name|File
argument_list|(
name|trashDirectory
argument_list|,
name|blockName
argument_list|)
decl_stmt|;
name|File
name|newMetaFile
init|=
operator|new
name|File
argument_list|(
name|trashDirectory
argument_list|,
name|DatanodeUtil
operator|.
name|getMetaName
argument_list|(
name|blockName
argument_list|,
name|genstamp
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
return|return
operator|(
name|replicaToDelete
operator|.
name|renameData
argument_list|(
name|newBlockFile
operator|.
name|toURI
argument_list|()
argument_list|)
operator|&&
name|replicaToDelete
operator|.
name|renameMeta
argument_list|(
name|newMetaFile
operator|.
name|toURI
argument_list|()
argument_list|)
operator|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error moving files to trash: "
operator|+
name|replicaToDelete
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
specifier|final
name|long
name|blockLength
init|=
name|replicaToDelete
operator|.
name|getBlockDataLength
argument_list|()
decl_stmt|;
specifier|final
name|long
name|metaLength
init|=
name|replicaToDelete
operator|.
name|getMetadataLength
argument_list|()
decl_stmt|;
name|boolean
name|result
decl_stmt|;
name|result
operator|=
operator|(
name|trashDirectory
operator|==
literal|null
operator|)
condition|?
name|deleteFiles
argument_list|()
else|:
name|moveFiles
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|result
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unexpected error trying to "
operator|+
operator|(
name|trashDirectory
operator|==
literal|null
condition|?
literal|"delete"
else|:
literal|"move"
operator|)
operator|+
literal|" block "
operator|+
name|block
operator|.
name|getBlockPoolId
argument_list|()
operator|+
literal|" "
operator|+
name|block
operator|.
name|getLocalBlock
argument_list|()
operator|+
literal|" at file "
operator|+
name|replicaToDelete
operator|.
name|getBlockURI
argument_list|()
operator|+
literal|". Ignored."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|block
operator|.
name|getLocalBlock
argument_list|()
operator|.
name|getNumBytes
argument_list|()
operator|!=
name|BlockCommand
operator|.
name|NO_ACK
condition|)
block|{
name|datanode
operator|.
name|notifyNamenodeDeletedBlock
argument_list|(
name|block
argument_list|,
name|volume
operator|.
name|getStorageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|volume
operator|.
name|onBlockFileDeletion
argument_list|(
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|blockLength
argument_list|)
expr_stmt|;
name|volume
operator|.
name|onMetaFileDeletion
argument_list|(
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|metaLength
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleted "
operator|+
name|block
operator|.
name|getBlockPoolId
argument_list|()
operator|+
literal|" "
operator|+
name|block
operator|.
name|getLocalBlock
argument_list|()
operator|+
literal|" URI "
operator|+
name|replicaToDelete
operator|.
name|getBlockURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|updateDeletedBlockId
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|volumeRef
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|updateDeletedBlockId (ExtendedBlock block)
specifier|private
specifier|synchronized
name|void
name|updateDeletedBlockId
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
block|{
name|Set
argument_list|<
name|Long
argument_list|>
name|blockIds
init|=
name|deletedBlockIds
operator|.
name|get
argument_list|(
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|blockIds
operator|==
literal|null
condition|)
block|{
name|blockIds
operator|=
operator|new
name|HashSet
argument_list|<
name|Long
argument_list|>
argument_list|()
expr_stmt|;
name|deletedBlockIds
operator|.
name|put
argument_list|(
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|blockIds
argument_list|)
expr_stmt|;
block|}
name|blockIds
operator|.
name|add
argument_list|(
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|)
expr_stmt|;
name|numDeletedBlocks
operator|++
expr_stmt|;
if|if
condition|(
name|numDeletedBlocks
operator|==
name|MAX_DELETED_BLOCKS
condition|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Long
argument_list|>
argument_list|>
name|e
range|:
name|deletedBlockIds
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|bpid
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Long
argument_list|>
name|bs
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|fsdatasetImpl
operator|.
name|removeDeletedBlocks
argument_list|(
name|bpid
argument_list|,
name|bs
argument_list|)
expr_stmt|;
name|bs
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|numDeletedBlocks
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

