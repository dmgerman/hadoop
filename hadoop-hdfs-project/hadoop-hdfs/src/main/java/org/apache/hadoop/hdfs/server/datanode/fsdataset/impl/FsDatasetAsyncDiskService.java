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
name|protocol
operator|.
name|BlockCommand
import|;
end_import

begin_comment
comment|/**  * This class is a container of multiple thread pools, each for a volume,  * so that we can schedule async disk operations easily.  *   * Examples of async disk operations are deletion of block files.  * We don't want to create a new thread for each of the deletion request, and  * we don't want to do all deletions in the heartbeat thread since deletion  * can be slow, and we don't want to use a single thread pool because that  * is inefficient when we have more than 1 volume.  AsyncDiskService is the  * solution for these.  *   * This class and {@link org.apache.hadoop.util.AsyncDiskService} are similar.  * They should be combined.  */
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
DECL|field|executors
specifier|private
name|Map
argument_list|<
name|File
argument_list|,
name|ThreadPoolExecutor
argument_list|>
name|executors
init|=
operator|new
name|HashMap
argument_list|<
name|File
argument_list|,
name|ThreadPoolExecutor
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Create a AsyncDiskServices with a set of volumes (specified by their    * root directories).    *     * The AsyncDiskServices uses one ThreadPool per volume to do the async    * disk operations.    *     * @param volumes The roots of the data volumes.    */
DECL|method|FsDatasetAsyncDiskService (DataNode datanode, File[] volumes)
name|FsDatasetAsyncDiskService
parameter_list|(
name|DataNode
name|datanode
parameter_list|,
name|File
index|[]
name|volumes
parameter_list|)
block|{
name|this
operator|.
name|datanode
operator|=
name|datanode
expr_stmt|;
specifier|final
name|ThreadGroup
name|threadGroup
init|=
operator|new
name|ThreadGroup
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
comment|// Create one ThreadPool per volume
for|for
control|(
name|int
name|v
init|=
literal|0
init|;
name|v
operator|<
name|volumes
operator|.
name|length
condition|;
name|v
operator|++
control|)
block|{
specifier|final
name|File
name|vol
init|=
name|volumes
index|[
name|v
index|]
decl_stmt|;
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
name|vol
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
name|vol
argument_list|,
name|executor
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
DECL|method|execute (File root, Runnable task)
specifier|synchronized
name|void
name|execute
parameter_list|(
name|File
name|root
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
name|ThreadPoolExecutor
name|executor
init|=
name|executors
operator|.
name|get
argument_list|(
name|root
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
literal|"Cannot find root "
operator|+
name|root
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
name|File
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
comment|/**    * Delete the block file and meta file from the disk asynchronously, adjust    * dfsUsed statistics accordingly.    */
DECL|method|deleteAsync (FsVolumeImpl volume, File blockFile, File metaFile, ExtendedBlock block, String trashDirectory)
name|void
name|deleteAsync
parameter_list|(
name|FsVolumeImpl
name|volume
parameter_list|,
name|File
name|blockFile
parameter_list|,
name|File
name|metaFile
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
literal|" file "
operator|+
name|blockFile
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
name|volume
argument_list|,
name|blockFile
argument_list|,
name|metaFile
argument_list|,
name|block
argument_list|,
name|trashDirectory
argument_list|)
decl_stmt|;
name|execute
argument_list|(
name|volume
operator|.
name|getCurrentDir
argument_list|()
argument_list|,
name|deletionTask
argument_list|)
expr_stmt|;
block|}
comment|/** A task for deleting a block file and its associated meta file, as well    *  as decrement the dfs usage of the volume.    *  Optionally accepts a trash directory. If one is specified then the files    *  are moved to trash instead of being deleted. If none is specified then the    *  files are deleted immediately.    */
DECL|class|ReplicaFileDeleteTask
class|class
name|ReplicaFileDeleteTask
implements|implements
name|Runnable
block|{
DECL|field|volume
specifier|final
name|FsVolumeImpl
name|volume
decl_stmt|;
DECL|field|blockFile
specifier|final
name|File
name|blockFile
decl_stmt|;
DECL|field|metaFile
specifier|final
name|File
name|metaFile
decl_stmt|;
DECL|field|block
specifier|final
name|ExtendedBlock
name|block
decl_stmt|;
DECL|field|trashDirectory
specifier|final
name|String
name|trashDirectory
decl_stmt|;
DECL|method|ReplicaFileDeleteTask (FsVolumeImpl volume, File blockFile, File metaFile, ExtendedBlock block, String trashDirectory)
name|ReplicaFileDeleteTask
parameter_list|(
name|FsVolumeImpl
name|volume
parameter_list|,
name|File
name|blockFile
parameter_list|,
name|File
name|metaFile
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
name|volume
operator|=
name|volume
expr_stmt|;
name|this
operator|.
name|blockFile
operator|=
name|blockFile
expr_stmt|;
name|this
operator|.
name|metaFile
operator|=
name|metaFile
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
name|blockFile
operator|+
literal|" and meta file "
operator|+
name|metaFile
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
name|blockFile
operator|.
name|delete
argument_list|()
operator|&&
operator|(
name|metaFile
operator|.
name|delete
argument_list|()
operator|||
operator|!
name|metaFile
operator|.
name|exists
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
name|File
name|newBlockFile
init|=
operator|new
name|File
argument_list|(
name|trashDirectory
argument_list|,
name|blockFile
operator|.
name|getName
argument_list|()
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
name|metaFile
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
operator|(
operator|new
name|File
argument_list|(
name|trashDirectory
argument_list|)
operator|)
operator|.
name|mkdirs
argument_list|()
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
literal|"Moving files "
operator|+
name|blockFile
operator|.
name|getName
argument_list|()
operator|+
literal|" and "
operator|+
name|metaFile
operator|.
name|getName
argument_list|()
operator|+
literal|" to trash."
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|blockFile
operator|.
name|renameTo
argument_list|(
name|newBlockFile
argument_list|)
operator|&&
name|metaFile
operator|.
name|renameTo
argument_list|(
name|newMetaFile
argument_list|)
operator|)
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
name|long
name|dfsBytes
init|=
name|blockFile
operator|.
name|length
argument_list|()
operator|+
name|metaFile
operator|.
name|length
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
name|blockFile
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
name|decDfsUsed
argument_list|(
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|dfsBytes
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
literal|" file "
operator|+
name|blockFile
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

