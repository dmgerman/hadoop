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
name|DFSUtilClient
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
name|HdfsConfiguration
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
name|fsdataset
operator|.
name|FsVolumeReference
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

begin_comment
comment|/**  * This class is a container of multiple thread pools, one for each non-RamDisk  * volume with a maximum thread count of 1 so that we can schedule async lazy  * persist operations easily with volume arrival and departure handled.  *  * This class and {@link org.apache.hadoop.util.AsyncDiskService} are similar.  * They should be combined.  */
end_comment

begin_class
DECL|class|RamDiskAsyncLazyPersistService
class|class
name|RamDiskAsyncLazyPersistService
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
name|RamDiskAsyncLazyPersistService
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
literal|1
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
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
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
DECL|field|EMPTY_HDFS_CONF
specifier|private
specifier|final
specifier|static
name|HdfsConfiguration
name|EMPTY_HDFS_CONF
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|/**    * Create a RamDiskAsyncLazyPersistService with a set of volumes (specified by their    * root directories).    *    * The RamDiskAsyncLazyPersistService uses one ThreadPool per volume to do the async    * disk operations.    */
DECL|method|RamDiskAsyncLazyPersistService (DataNode datanode, Configuration conf)
name|RamDiskAsyncLazyPersistService
parameter_list|(
name|DataNode
name|datanode
parameter_list|,
name|Configuration
name|conf
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
name|conf
operator|=
name|conf
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
DECL|method|addExecutorForVolume (final File volume)
specifier|private
name|void
name|addExecutorForVolume
parameter_list|(
specifier|final
name|File
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
literal|"Async RamDisk lazy persist worker for volume "
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
argument_list|,
name|executor
argument_list|)
expr_stmt|;
block|}
comment|/**    * Starts AsyncLazyPersistService for a new volume    * @param volume the root of the new data volume.    */
DECL|method|addVolume (File volume)
specifier|synchronized
name|void
name|addVolume
parameter_list|(
name|File
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
literal|"AsyncLazyPersistService is already shutdown"
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
comment|/**    * Stops AsyncLazyPersistService for a volume.    * @param volume the root of the volume.    */
DECL|method|removeVolume (File volume)
specifier|synchronized
name|void
name|removeVolume
parameter_list|(
name|File
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
name|ThreadPoolExecutor
name|executor
init|=
name|executors
operator|.
name|get
argument_list|(
name|volume
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
literal|"Can not find volume "
operator|+
name|volume
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
name|volume
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Query if the thread pool exist for the volume    * @param volume the root of a volume    * @return true if there is one thread pool for the volume    *         false otherwise    */
DECL|method|queryVolume (File volume)
specifier|synchronized
name|boolean
name|queryVolume
parameter_list|(
name|File
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
literal|"AsyncLazyPersistService is already shutdown"
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
argument_list|)
decl_stmt|;
return|return
operator|(
name|executor
operator|!=
literal|null
operator|)
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
literal|"AsyncLazyPersistService is already shutdown"
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
comment|/**    * Gracefully shut down all ThreadPool. Will wait for all lazy persist    * tasks to finish.    */
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
literal|"AsyncLazyPersistService has already shut down."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down all async lazy persist service threads"
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
literal|"All async lazy persist service threads have been shut down"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Asynchronously lazy persist the block from the RamDisk to Disk.    */
DECL|method|submitLazyPersistTask (String bpId, long blockId, long genStamp, long creationTime, File metaFile, File blockFile, FsVolumeReference target)
name|void
name|submitLazyPersistTask
parameter_list|(
name|String
name|bpId
parameter_list|,
name|long
name|blockId
parameter_list|,
name|long
name|genStamp
parameter_list|,
name|long
name|creationTime
parameter_list|,
name|File
name|metaFile
parameter_list|,
name|File
name|blockFile
parameter_list|,
name|FsVolumeReference
name|target
parameter_list|)
throws|throws
name|IOException
block|{
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
literal|"LazyWriter schedule async task to persist RamDisk block pool id: "
operator|+
name|bpId
operator|+
literal|" block id: "
operator|+
name|blockId
argument_list|)
expr_stmt|;
block|}
name|FsVolumeImpl
name|volume
init|=
operator|(
name|FsVolumeImpl
operator|)
name|target
operator|.
name|getVolume
argument_list|()
decl_stmt|;
name|File
name|lazyPersistDir
init|=
name|volume
operator|.
name|getLazyPersistDir
argument_list|(
name|bpId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|lazyPersistDir
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|lazyPersistDir
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"LazyWriter failed to create "
operator|+
name|lazyPersistDir
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"LazyWriter fail to find or create lazy persist dir: "
operator|+
name|lazyPersistDir
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|ReplicaLazyPersistTask
name|lazyPersistTask
init|=
operator|new
name|ReplicaLazyPersistTask
argument_list|(
name|bpId
argument_list|,
name|blockId
argument_list|,
name|genStamp
argument_list|,
name|creationTime
argument_list|,
name|blockFile
argument_list|,
name|metaFile
argument_list|,
name|target
argument_list|,
name|lazyPersistDir
argument_list|)
decl_stmt|;
name|execute
argument_list|(
name|volume
operator|.
name|getCurrentDir
argument_list|()
argument_list|,
name|lazyPersistTask
argument_list|)
expr_stmt|;
block|}
DECL|class|ReplicaLazyPersistTask
class|class
name|ReplicaLazyPersistTask
implements|implements
name|Runnable
block|{
DECL|field|bpId
specifier|final
name|String
name|bpId
decl_stmt|;
DECL|field|blockId
specifier|final
name|long
name|blockId
decl_stmt|;
DECL|field|genStamp
specifier|final
name|long
name|genStamp
decl_stmt|;
DECL|field|creationTime
specifier|final
name|long
name|creationTime
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
DECL|field|targetVolume
specifier|final
name|FsVolumeReference
name|targetVolume
decl_stmt|;
DECL|field|lazyPersistDir
specifier|final
name|File
name|lazyPersistDir
decl_stmt|;
DECL|method|ReplicaLazyPersistTask (String bpId, long blockId, long genStamp, long creationTime, File blockFile, File metaFile, FsVolumeReference targetVolume, File lazyPersistDir)
name|ReplicaLazyPersistTask
parameter_list|(
name|String
name|bpId
parameter_list|,
name|long
name|blockId
parameter_list|,
name|long
name|genStamp
parameter_list|,
name|long
name|creationTime
parameter_list|,
name|File
name|blockFile
parameter_list|,
name|File
name|metaFile
parameter_list|,
name|FsVolumeReference
name|targetVolume
parameter_list|,
name|File
name|lazyPersistDir
parameter_list|)
block|{
name|this
operator|.
name|bpId
operator|=
name|bpId
expr_stmt|;
name|this
operator|.
name|blockId
operator|=
name|blockId
expr_stmt|;
name|this
operator|.
name|genStamp
operator|=
name|genStamp
expr_stmt|;
name|this
operator|.
name|creationTime
operator|=
name|creationTime
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
name|targetVolume
operator|=
name|targetVolume
expr_stmt|;
name|this
operator|.
name|lazyPersistDir
operator|=
name|lazyPersistDir
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
comment|// Called in AsyncLazyPersistService.execute for displaying error messages.
return|return
literal|"LazyWriter async task of persist RamDisk block pool id:"
operator|+
name|bpId
operator|+
literal|" block pool id: "
operator|+
name|blockId
operator|+
literal|" with block file "
operator|+
name|blockFile
operator|+
literal|" and meta file "
operator|+
name|metaFile
operator|+
literal|" to target volume "
operator|+
name|targetVolume
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
name|boolean
name|succeeded
init|=
literal|false
decl_stmt|;
specifier|final
name|FsDatasetImpl
name|dataset
init|=
operator|(
name|FsDatasetImpl
operator|)
name|datanode
operator|.
name|getFSDataset
argument_list|()
decl_stmt|;
try|try
init|(
name|FsVolumeReference
name|ref
init|=
name|this
operator|.
name|targetVolume
init|)
block|{
name|int
name|smallBufferSize
init|=
name|DFSUtilClient
operator|.
name|getSmallBufferSize
argument_list|(
name|EMPTY_HDFS_CONF
argument_list|)
decl_stmt|;
comment|// No FsDatasetImpl lock for the file copy
name|File
name|targetFiles
index|[]
init|=
name|FsDatasetImpl
operator|.
name|copyBlockFiles
argument_list|(
name|blockId
argument_list|,
name|genStamp
argument_list|,
name|metaFile
argument_list|,
name|blockFile
argument_list|,
name|lazyPersistDir
argument_list|,
literal|true
argument_list|,
name|smallBufferSize
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// Lock FsDataSetImpl during onCompleteLazyPersist callback
name|dataset
operator|.
name|onCompleteLazyPersist
argument_list|(
name|bpId
argument_list|,
name|blockId
argument_list|,
name|creationTime
argument_list|,
name|targetFiles
argument_list|,
operator|(
name|FsVolumeImpl
operator|)
name|ref
operator|.
name|getVolume
argument_list|()
argument_list|)
expr_stmt|;
name|succeeded
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|FsDatasetImpl
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"LazyWriter failed to async persist RamDisk block pool id: "
operator|+
name|bpId
operator|+
literal|"block Id: "
operator|+
name|blockId
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|succeeded
condition|)
block|{
name|dataset
operator|.
name|onFailLazyPersist
argument_list|(
name|bpId
argument_list|,
name|blockId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

