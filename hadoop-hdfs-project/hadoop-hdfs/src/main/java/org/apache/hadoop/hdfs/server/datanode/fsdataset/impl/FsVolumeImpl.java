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
name|ConcurrentHashMap
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
name|Executor
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|annotations
operator|.
name|VisibleForTesting
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
name|classification
operator|.
name|InterfaceAudience
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
name|fs
operator|.
name|DF
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
name|fs
operator|.
name|FileUtil
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
name|DFSConfigKeys
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
name|StorageType
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
name|Block
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
name|DataStorage
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
name|fsdataset
operator|.
name|FsVolumeSpi
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
name|DatanodeStorage
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
name|DiskChecker
operator|.
name|DiskErrorException
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
import|;
end_import

begin_comment
comment|/**  * The underlying volume used to store replica.  *   * It uses the {@link FsDatasetImpl} object for synchronization.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|VisibleForTesting
DECL|class|FsVolumeImpl
specifier|public
class|class
name|FsVolumeImpl
implements|implements
name|FsVolumeSpi
block|{
DECL|field|dataset
specifier|private
specifier|final
name|FsDatasetImpl
name|dataset
decl_stmt|;
DECL|field|storageID
specifier|private
specifier|final
name|String
name|storageID
decl_stmt|;
DECL|field|storageType
specifier|private
specifier|final
name|StorageType
name|storageType
decl_stmt|;
DECL|field|bpSlices
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|BlockPoolSlice
argument_list|>
name|bpSlices
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|BlockPoolSlice
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|currentDir
specifier|private
specifier|final
name|File
name|currentDir
decl_stmt|;
comment|//<StorageDirectory>/current
DECL|field|usage
specifier|private
specifier|final
name|DF
name|usage
decl_stmt|;
DECL|field|reserved
specifier|private
specifier|final
name|long
name|reserved
decl_stmt|;
comment|// Disk space reserved for open blocks.
DECL|field|reservedForRbw
specifier|private
name|AtomicLong
name|reservedForRbw
decl_stmt|;
comment|// Capacity configured. This is useful when we want to
comment|// limit the visible capacity for tests. If negative, then we just
comment|// query from the filesystem.
DECL|field|configuredCapacity
specifier|protected
specifier|volatile
name|long
name|configuredCapacity
decl_stmt|;
comment|/**    * Per-volume worker pool that processes new blocks to cache.    * The maximum number of workers per volume is bounded (configurable via    * dfs.datanode.fsdatasetcache.max.threads.per.volume) to limit resource    * contention.    */
DECL|field|cacheExecutor
specifier|protected
name|ThreadPoolExecutor
name|cacheExecutor
decl_stmt|;
DECL|method|FsVolumeImpl (FsDatasetImpl dataset, String storageID, File currentDir, Configuration conf, StorageType storageType)
name|FsVolumeImpl
parameter_list|(
name|FsDatasetImpl
name|dataset
parameter_list|,
name|String
name|storageID
parameter_list|,
name|File
name|currentDir
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|StorageType
name|storageType
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|dataset
operator|=
name|dataset
expr_stmt|;
name|this
operator|.
name|storageID
operator|=
name|storageID
expr_stmt|;
name|this
operator|.
name|reserved
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DU_RESERVED_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DU_RESERVED_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|reservedForRbw
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentDir
operator|=
name|currentDir
expr_stmt|;
name|File
name|parent
init|=
name|currentDir
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
name|this
operator|.
name|usage
operator|=
operator|new
name|DF
argument_list|(
name|parent
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|storageType
operator|=
name|storageType
expr_stmt|;
name|this
operator|.
name|configuredCapacity
operator|=
operator|-
literal|1
expr_stmt|;
name|cacheExecutor
operator|=
name|initializeCacheExecutor
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
DECL|method|initializeCacheExecutor (File parent)
specifier|protected
name|ThreadPoolExecutor
name|initializeCacheExecutor
parameter_list|(
name|File
name|parent
parameter_list|)
block|{
if|if
condition|(
name|storageType
operator|.
name|isTransient
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|int
name|maxNumThreads
init|=
name|dataset
operator|.
name|datanode
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FSDATASETCACHE_MAX_THREADS_PER_VOLUME_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FSDATASETCACHE_MAX_THREADS_PER_VOLUME_DEFAULT
argument_list|)
decl_stmt|;
name|ThreadFactory
name|workerFactory
init|=
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|setNameFormat
argument_list|(
literal|"FsVolumeImplWorker-"
operator|+
name|parent
operator|.
name|toString
argument_list|()
operator|+
literal|"-%d"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ThreadPoolExecutor
name|executor
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|1
argument_list|,
name|maxNumThreads
argument_list|,
literal|60
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
name|workerFactory
argument_list|)
decl_stmt|;
name|executor
operator|.
name|allowCoreThreadTimeOut
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|executor
return|;
block|}
DECL|method|getCurrentDir ()
name|File
name|getCurrentDir
parameter_list|()
block|{
return|return
name|currentDir
return|;
block|}
DECL|method|getRbwDir (String bpid)
name|File
name|getRbwDir
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|getRbwDir
argument_list|()
return|;
block|}
DECL|method|getLazyPersistDir (String bpid)
name|File
name|getLazyPersistDir
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|getLazypersistDir
argument_list|()
return|;
block|}
DECL|method|getTmpDir (String bpid)
name|File
name|getTmpDir
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|getTmpDir
argument_list|()
return|;
block|}
DECL|method|decDfsUsed (String bpid, long value)
name|void
name|decDfsUsed
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
name|value
parameter_list|)
block|{
synchronized|synchronized
init|(
name|dataset
init|)
block|{
name|BlockPoolSlice
name|bp
init|=
name|bpSlices
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|bp
operator|!=
literal|null
condition|)
block|{
name|bp
operator|.
name|decDfsUsed
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|incDfsUsed (String bpid, long value)
name|void
name|incDfsUsed
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
name|value
parameter_list|)
block|{
synchronized|synchronized
init|(
name|dataset
init|)
block|{
name|BlockPoolSlice
name|bp
init|=
name|bpSlices
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|bp
operator|!=
literal|null
condition|)
block|{
name|bp
operator|.
name|incDfsUsed
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getDfsUsed ()
name|long
name|getDfsUsed
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|dfsUsed
init|=
literal|0
decl_stmt|;
synchronized|synchronized
init|(
name|dataset
init|)
block|{
for|for
control|(
name|BlockPoolSlice
name|s
range|:
name|bpSlices
operator|.
name|values
argument_list|()
control|)
block|{
name|dfsUsed
operator|+=
name|s
operator|.
name|getDfsUsed
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|dfsUsed
return|;
block|}
DECL|method|getBlockPoolUsed (String bpid)
name|long
name|getBlockPoolUsed
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|getDfsUsed
argument_list|()
return|;
block|}
comment|/**    * Calculate the capacity of the filesystem, after removing any    * reserved capacity.    * @return the unreserved number of bytes left in this filesystem. May be zero.    */
annotation|@
name|VisibleForTesting
DECL|method|getCapacity ()
specifier|public
name|long
name|getCapacity
parameter_list|()
block|{
if|if
condition|(
name|configuredCapacity
operator|<
literal|0
condition|)
block|{
name|long
name|remaining
init|=
name|usage
operator|.
name|getCapacity
argument_list|()
operator|-
name|reserved
decl_stmt|;
return|return
name|remaining
operator|>
literal|0
condition|?
name|remaining
else|:
literal|0
return|;
block|}
return|return
name|configuredCapacity
return|;
block|}
comment|/**    * This function MUST NOT be used outside of tests.    *    * @param capacity    */
annotation|@
name|VisibleForTesting
DECL|method|setCapacityForTesting (long capacity)
specifier|public
name|void
name|setCapacityForTesting
parameter_list|(
name|long
name|capacity
parameter_list|)
block|{
name|this
operator|.
name|configuredCapacity
operator|=
name|capacity
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAvailable ()
specifier|public
name|long
name|getAvailable
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|remaining
init|=
name|getCapacity
argument_list|()
operator|-
name|getDfsUsed
argument_list|()
operator|-
name|reservedForRbw
operator|.
name|get
argument_list|()
decl_stmt|;
name|long
name|available
init|=
name|usage
operator|.
name|getAvailable
argument_list|()
decl_stmt|;
if|if
condition|(
name|remaining
operator|>
name|available
condition|)
block|{
name|remaining
operator|=
name|available
expr_stmt|;
block|}
return|return
operator|(
name|remaining
operator|>
literal|0
operator|)
condition|?
name|remaining
else|:
literal|0
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getReservedForRbw ()
specifier|public
name|long
name|getReservedForRbw
parameter_list|()
block|{
return|return
name|reservedForRbw
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getReserved ()
name|long
name|getReserved
parameter_list|()
block|{
return|return
name|reserved
return|;
block|}
DECL|method|getBlockPoolSlice (String bpid)
name|BlockPoolSlice
name|getBlockPoolSlice
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockPoolSlice
name|bp
init|=
name|bpSlices
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|bp
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"block pool "
operator|+
name|bpid
operator|+
literal|" is not found"
argument_list|)
throw|;
block|}
return|return
name|bp
return|;
block|}
annotation|@
name|Override
DECL|method|getBasePath ()
specifier|public
name|String
name|getBasePath
parameter_list|()
block|{
return|return
name|currentDir
operator|.
name|getParent
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isTransientStorage ()
specifier|public
name|boolean
name|isTransientStorage
parameter_list|()
block|{
return|return
name|storageType
operator|.
name|isTransient
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPath (String bpid)
specifier|public
name|String
name|getPath
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|getDirectory
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFinalizedDir (String bpid)
specifier|public
name|File
name|getFinalizedDir
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|getFinalizedDir
argument_list|()
return|;
block|}
comment|/**    * Make a deep copy of the list of currently active BPIDs    */
annotation|@
name|Override
DECL|method|getBlockPoolList ()
specifier|public
name|String
index|[]
name|getBlockPoolList
parameter_list|()
block|{
return|return
name|bpSlices
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|bpSlices
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**    * Temporary files. They get moved to the finalized block directory when    * the block is finalized.    */
DECL|method|createTmpFile (String bpid, Block b)
name|File
name|createTmpFile
parameter_list|(
name|String
name|bpid
parameter_list|,
name|Block
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|createTmpFile
argument_list|(
name|b
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|reserveSpaceForRbw (long bytesToReserve)
specifier|public
name|void
name|reserveSpaceForRbw
parameter_list|(
name|long
name|bytesToReserve
parameter_list|)
block|{
if|if
condition|(
name|bytesToReserve
operator|!=
literal|0
condition|)
block|{
name|reservedForRbw
operator|.
name|addAndGet
argument_list|(
name|bytesToReserve
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|releaseReservedSpace (long bytesToRelease)
specifier|public
name|void
name|releaseReservedSpace
parameter_list|(
name|long
name|bytesToRelease
parameter_list|)
block|{
if|if
condition|(
name|bytesToRelease
operator|!=
literal|0
condition|)
block|{
name|long
name|oldReservation
decl_stmt|,
name|newReservation
decl_stmt|;
do|do
block|{
name|oldReservation
operator|=
name|reservedForRbw
operator|.
name|get
argument_list|()
expr_stmt|;
name|newReservation
operator|=
name|oldReservation
operator|-
name|bytesToRelease
expr_stmt|;
if|if
condition|(
name|newReservation
operator|<
literal|0
condition|)
block|{
comment|// Failsafe, this should never occur in practice, but if it does we don't
comment|// want to start advertising more space than we have available.
name|newReservation
operator|=
literal|0
expr_stmt|;
block|}
block|}
do|while
condition|(
operator|!
name|reservedForRbw
operator|.
name|compareAndSet
argument_list|(
name|oldReservation
argument_list|,
name|newReservation
argument_list|)
condition|)
do|;
block|}
block|}
comment|/**    * RBW files. They get moved to the finalized block directory when    * the block is finalized.    */
DECL|method|createRbwFile (String bpid, Block b)
name|File
name|createRbwFile
parameter_list|(
name|String
name|bpid
parameter_list|,
name|Block
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|reserveSpaceForRbw
argument_list|(
name|b
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|createRbwFile
argument_list|(
name|b
argument_list|)
return|;
block|}
comment|/**    *    * @param bytesReservedForRbw Space that was reserved during    *     block creation. Now that the block is being finalized we    *     can free up this space.    * @return    * @throws IOException    */
DECL|method|addFinalizedBlock (String bpid, Block b, File f, long bytesReservedForRbw)
name|File
name|addFinalizedBlock
parameter_list|(
name|String
name|bpid
parameter_list|,
name|Block
name|b
parameter_list|,
name|File
name|f
parameter_list|,
name|long
name|bytesReservedForRbw
parameter_list|)
throws|throws
name|IOException
block|{
name|releaseReservedSpace
argument_list|(
name|bytesReservedForRbw
argument_list|)
expr_stmt|;
return|return
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|addFinalizedBlock
argument_list|(
name|b
argument_list|,
name|f
argument_list|)
return|;
block|}
DECL|method|getCacheExecutor ()
name|Executor
name|getCacheExecutor
parameter_list|()
block|{
return|return
name|cacheExecutor
return|;
block|}
DECL|method|checkDirs ()
name|void
name|checkDirs
parameter_list|()
throws|throws
name|DiskErrorException
block|{
comment|// TODO:FEDERATION valid synchronization
for|for
control|(
name|BlockPoolSlice
name|s
range|:
name|bpSlices
operator|.
name|values
argument_list|()
control|)
block|{
name|s
operator|.
name|checkDirs
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getVolumeMap (ReplicaMap volumeMap, final RamDiskReplicaTracker ramDiskReplicaMap)
name|void
name|getVolumeMap
parameter_list|(
name|ReplicaMap
name|volumeMap
parameter_list|,
specifier|final
name|RamDiskReplicaTracker
name|ramDiskReplicaMap
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|BlockPoolSlice
name|s
range|:
name|bpSlices
operator|.
name|values
argument_list|()
control|)
block|{
name|s
operator|.
name|getVolumeMap
argument_list|(
name|volumeMap
argument_list|,
name|ramDiskReplicaMap
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getVolumeMap (String bpid, ReplicaMap volumeMap, final RamDiskReplicaTracker ramDiskReplicaMap)
name|void
name|getVolumeMap
parameter_list|(
name|String
name|bpid
parameter_list|,
name|ReplicaMap
name|volumeMap
parameter_list|,
specifier|final
name|RamDiskReplicaTracker
name|ramDiskReplicaMap
parameter_list|)
throws|throws
name|IOException
block|{
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|getVolumeMap
argument_list|(
name|volumeMap
argument_list|,
name|ramDiskReplicaMap
argument_list|)
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
return|return
name|currentDir
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
DECL|method|shutdown ()
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|cacheExecutor
operator|!=
literal|null
condition|)
block|{
name|cacheExecutor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|Set
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|BlockPoolSlice
argument_list|>
argument_list|>
name|set
init|=
name|bpSlices
operator|.
name|entrySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|BlockPoolSlice
argument_list|>
name|entry
range|:
name|set
control|)
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addBlockPool (String bpid, Configuration conf)
name|void
name|addBlockPool
parameter_list|(
name|String
name|bpid
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|bpdir
init|=
operator|new
name|File
argument_list|(
name|currentDir
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
name|BlockPoolSlice
name|bp
init|=
operator|new
name|BlockPoolSlice
argument_list|(
name|bpid
argument_list|,
name|this
argument_list|,
name|bpdir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|bpSlices
operator|.
name|put
argument_list|(
name|bpid
argument_list|,
name|bp
argument_list|)
expr_stmt|;
block|}
DECL|method|shutdownBlockPool (String bpid)
name|void
name|shutdownBlockPool
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
name|BlockPoolSlice
name|bp
init|=
name|bpSlices
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|bp
operator|!=
literal|null
condition|)
block|{
name|bp
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|bpSlices
operator|.
name|remove
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
block|}
DECL|method|isBPDirEmpty (String bpid)
name|boolean
name|isBPDirEmpty
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|volumeCurrentDir
init|=
name|this
operator|.
name|getCurrentDir
argument_list|()
decl_stmt|;
name|File
name|bpDir
init|=
operator|new
name|File
argument_list|(
name|volumeCurrentDir
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
name|File
name|bpCurrentDir
init|=
operator|new
name|File
argument_list|(
name|bpDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_CURRENT
argument_list|)
decl_stmt|;
name|File
name|finalizedDir
init|=
operator|new
name|File
argument_list|(
name|bpCurrentDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_FINALIZED
argument_list|)
decl_stmt|;
name|File
name|rbwDir
init|=
operator|new
name|File
argument_list|(
name|bpCurrentDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_RBW
argument_list|)
decl_stmt|;
if|if
condition|(
name|finalizedDir
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|DatanodeUtil
operator|.
name|dirNoFilesRecursive
argument_list|(
name|finalizedDir
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|rbwDir
operator|.
name|exists
argument_list|()
operator|&&
name|FileUtil
operator|.
name|list
argument_list|(
name|rbwDir
argument_list|)
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|deleteBPDirectories (String bpid, boolean force)
name|void
name|deleteBPDirectories
parameter_list|(
name|String
name|bpid
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|volumeCurrentDir
init|=
name|this
operator|.
name|getCurrentDir
argument_list|()
decl_stmt|;
name|File
name|bpDir
init|=
operator|new
name|File
argument_list|(
name|volumeCurrentDir
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|bpDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// nothing to be deleted
return|return;
block|}
name|File
name|tmpDir
init|=
operator|new
name|File
argument_list|(
name|bpDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_TMP
argument_list|)
decl_stmt|;
name|File
name|bpCurrentDir
init|=
operator|new
name|File
argument_list|(
name|bpDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_CURRENT
argument_list|)
decl_stmt|;
name|File
name|finalizedDir
init|=
operator|new
name|File
argument_list|(
name|bpCurrentDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_FINALIZED
argument_list|)
decl_stmt|;
name|File
name|lazypersistDir
init|=
operator|new
name|File
argument_list|(
name|bpCurrentDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_LAZY_PERSIST
argument_list|)
decl_stmt|;
name|File
name|rbwDir
init|=
operator|new
name|File
argument_list|(
name|bpCurrentDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_RBW
argument_list|)
decl_stmt|;
if|if
condition|(
name|force
condition|)
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|bpDir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|rbwDir
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete "
operator|+
name|rbwDir
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|DatanodeUtil
operator|.
name|dirNoFilesRecursive
argument_list|(
name|finalizedDir
argument_list|)
operator|||
operator|!
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|finalizedDir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete "
operator|+
name|finalizedDir
argument_list|)
throw|;
block|}
if|if
condition|(
name|lazypersistDir
operator|.
name|exists
argument_list|()
operator|&&
operator|(
operator|(
operator|!
name|DatanodeUtil
operator|.
name|dirNoFilesRecursive
argument_list|(
name|lazypersistDir
argument_list|)
operator|||
operator|!
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|lazypersistDir
argument_list|)
operator|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete "
operator|+
name|lazypersistDir
argument_list|)
throw|;
block|}
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|tmpDir
argument_list|)
expr_stmt|;
for|for
control|(
name|File
name|f
range|:
name|FileUtil
operator|.
name|listFiles
argument_list|(
name|bpCurrentDir
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|f
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete "
operator|+
name|f
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|bpCurrentDir
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete "
operator|+
name|bpCurrentDir
argument_list|)
throw|;
block|}
for|for
control|(
name|File
name|f
range|:
name|FileUtil
operator|.
name|listFiles
argument_list|(
name|bpDir
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|f
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete "
operator|+
name|f
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|bpDir
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete "
operator|+
name|bpDir
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getStorageID ()
specifier|public
name|String
name|getStorageID
parameter_list|()
block|{
return|return
name|storageID
return|;
block|}
annotation|@
name|Override
DECL|method|getStorageType ()
specifier|public
name|StorageType
name|getStorageType
parameter_list|()
block|{
return|return
name|storageType
return|;
block|}
DECL|method|toDatanodeStorage ()
name|DatanodeStorage
name|toDatanodeStorage
parameter_list|()
block|{
return|return
operator|new
name|DatanodeStorage
argument_list|(
name|storageID
argument_list|,
name|DatanodeStorage
operator|.
name|State
operator|.
name|NORMAL
argument_list|,
name|storageType
argument_list|)
return|;
block|}
block|}
end_class

end_unit

