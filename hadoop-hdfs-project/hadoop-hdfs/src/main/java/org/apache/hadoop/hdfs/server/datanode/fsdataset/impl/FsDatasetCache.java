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
name|FileInputStream
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
name|Iterator
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
name|ConcurrentMap
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
name|org
operator|.
name|apache
operator|.
name|commons
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
name|classification
operator|.
name|InterfaceStability
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
name|ChecksumException
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
name|protocol
operator|.
name|BlockListAsLongs
import|;
end_import

begin_comment
comment|/**  * Manages caching for an FsDatasetImpl by using the mmap(2) and mlock(2)  * system calls to lock blocks into memory. Block checksums are verified upon  * entry into the cache.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|FsDatasetCache
specifier|public
class|class
name|FsDatasetCache
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FsDatasetCache
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Map of cached blocks    */
DECL|field|cachedBlocks
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|Long
argument_list|,
name|MappableBlock
argument_list|>
name|cachedBlocks
decl_stmt|;
DECL|field|dataset
specifier|private
specifier|final
name|FsDatasetImpl
name|dataset
decl_stmt|;
comment|/**    * Number of cached bytes    */
DECL|field|usedBytes
specifier|private
name|AtomicLong
name|usedBytes
decl_stmt|;
comment|/**    * Total cache capacity in bytes    */
DECL|field|maxBytes
specifier|private
specifier|final
name|long
name|maxBytes
decl_stmt|;
DECL|method|FsDatasetCache (FsDatasetImpl dataset)
specifier|public
name|FsDatasetCache
parameter_list|(
name|FsDatasetImpl
name|dataset
parameter_list|)
block|{
name|this
operator|.
name|dataset
operator|=
name|dataset
expr_stmt|;
name|this
operator|.
name|cachedBlocks
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Long
argument_list|,
name|MappableBlock
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|usedBytes
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxBytes
operator|=
name|dataset
operator|.
name|datanode
operator|.
name|getDnConf
argument_list|()
operator|.
name|getMaxLockedMemory
argument_list|()
expr_stmt|;
block|}
comment|/**    * @return if the block is cached    */
DECL|method|isCached (String bpid, long blockId)
name|boolean
name|isCached
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
name|blockId
parameter_list|)
block|{
name|MappableBlock
name|mapBlock
init|=
name|cachedBlocks
operator|.
name|get
argument_list|(
name|blockId
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapBlock
operator|!=
literal|null
condition|)
block|{
return|return
name|mapBlock
operator|.
name|getBlockPoolId
argument_list|()
operator|.
name|equals
argument_list|(
name|bpid
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * @return List of cached blocks suitable for translation into a    * {@link BlockListAsLongs} for a cache report.    */
DECL|method|getCachedBlocks (String bpid)
name|List
argument_list|<
name|Block
argument_list|>
name|getCachedBlocks
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
name|List
argument_list|<
name|Block
argument_list|>
name|blocks
init|=
operator|new
name|ArrayList
argument_list|<
name|Block
argument_list|>
argument_list|()
decl_stmt|;
comment|// ConcurrentHashMap iteration doesn't see latest updates, which is okay
name|Iterator
argument_list|<
name|MappableBlock
argument_list|>
name|it
init|=
name|cachedBlocks
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|MappableBlock
name|mapBlock
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|mapBlock
operator|.
name|getBlockPoolId
argument_list|()
operator|.
name|equals
argument_list|(
name|bpid
argument_list|)
condition|)
block|{
name|blocks
operator|.
name|add
argument_list|(
name|mapBlock
operator|.
name|getBlock
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|blocks
return|;
block|}
comment|/**    * Asynchronously attempts to cache a block. This is subject to the    * configured maximum locked memory limit.    *     * @param block block to cache    * @param volume volume of the block    * @param blockIn stream of the block's data file    * @param metaIn stream of the block's meta file    */
DECL|method|cacheBlock (String bpid, Block block, FsVolumeImpl volume, FileInputStream blockIn, FileInputStream metaIn)
name|void
name|cacheBlock
parameter_list|(
name|String
name|bpid
parameter_list|,
name|Block
name|block
parameter_list|,
name|FsVolumeImpl
name|volume
parameter_list|,
name|FileInputStream
name|blockIn
parameter_list|,
name|FileInputStream
name|metaIn
parameter_list|)
block|{
if|if
condition|(
name|isCached
argument_list|(
name|bpid
argument_list|,
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
name|MappableBlock
name|mapBlock
init|=
literal|null
decl_stmt|;
try|try
block|{
name|mapBlock
operator|=
operator|new
name|MappableBlock
argument_list|(
name|bpid
argument_list|,
name|block
argument_list|,
name|volume
argument_list|,
name|blockIn
argument_list|,
name|metaIn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to cache replica "
operator|+
name|block
operator|+
literal|": Could not instantiate"
operator|+
literal|" MappableBlock"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|blockIn
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|metaIn
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Check if there's sufficient cache capacity
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|long
name|bytes
init|=
name|mapBlock
operator|.
name|getNumBytes
argument_list|()
decl_stmt|;
name|long
name|used
init|=
name|usedBytes
operator|.
name|get
argument_list|()
decl_stmt|;
while|while
condition|(
name|used
operator|+
name|bytes
operator|<
name|maxBytes
condition|)
block|{
if|if
condition|(
name|usedBytes
operator|.
name|compareAndSet
argument_list|(
name|used
argument_list|,
name|used
operator|+
name|bytes
argument_list|)
condition|)
block|{
name|success
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|used
operator|=
name|usedBytes
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to cache replica %s: %s exceeded (%d + %d> %d)"
argument_list|,
name|mapBlock
operator|.
name|getBlock
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MAX_LOCKED_MEMORY_KEY
argument_list|,
name|used
argument_list|,
name|bytes
argument_list|,
name|maxBytes
argument_list|)
argument_list|)
expr_stmt|;
name|mapBlock
operator|.
name|close
argument_list|()
expr_stmt|;
return|return;
block|}
comment|// Submit it to the worker pool to be cached
name|volume
operator|.
name|getExecutor
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|WorkerTask
argument_list|(
name|mapBlock
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Uncaches a block if it is cached.    * @param blockId id to uncache    */
DECL|method|uncacheBlock (String bpid, long blockId)
name|void
name|uncacheBlock
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
name|blockId
parameter_list|)
block|{
name|MappableBlock
name|mapBlock
init|=
name|cachedBlocks
operator|.
name|get
argument_list|(
name|blockId
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapBlock
operator|!=
literal|null
operator|&&
name|mapBlock
operator|.
name|getBlockPoolId
argument_list|()
operator|.
name|equals
argument_list|(
name|bpid
argument_list|)
operator|&&
name|mapBlock
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
operator|==
name|blockId
condition|)
block|{
name|mapBlock
operator|.
name|close
argument_list|()
expr_stmt|;
name|cachedBlocks
operator|.
name|remove
argument_list|(
name|blockId
argument_list|)
expr_stmt|;
name|long
name|bytes
init|=
name|mapBlock
operator|.
name|getNumBytes
argument_list|()
decl_stmt|;
name|long
name|used
init|=
name|usedBytes
operator|.
name|get
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|usedBytes
operator|.
name|compareAndSet
argument_list|(
name|used
argument_list|,
name|used
operator|-
name|bytes
argument_list|)
condition|)
block|{
name|used
operator|=
name|usedBytes
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully uncached block "
operator|+
name|blockId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Could not uncache block "
operator|+
name|blockId
operator|+
literal|": unknown block."
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Background worker that mmaps, mlocks, and checksums a block    */
DECL|class|WorkerTask
specifier|private
class|class
name|WorkerTask
implements|implements
name|Runnable
block|{
DECL|field|block
specifier|private
name|MappableBlock
name|block
decl_stmt|;
DECL|method|WorkerTask (MappableBlock block)
name|WorkerTask
parameter_list|(
name|MappableBlock
name|block
parameter_list|)
block|{
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
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
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|block
operator|.
name|map
argument_list|()
expr_stmt|;
name|block
operator|.
name|lock
argument_list|()
expr_stmt|;
name|block
operator|.
name|verifyChecksum
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ChecksumException
name|e
parameter_list|)
block|{
comment|// Exception message is bogus since this wasn't caused by a file read
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to cache block "
operator|+
name|block
operator|.
name|getBlock
argument_list|()
operator|+
literal|": Checksum "
operator|+
literal|"verification failed."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to cache block "
operator|+
name|block
operator|.
name|getBlock
argument_list|()
operator|+
literal|": IOException"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// If we failed or the block became uncacheable in the meantime,
comment|// clean up and return the reserved cache allocation
if|if
condition|(
operator|!
name|success
operator|||
operator|!
name|dataset
operator|.
name|validToCache
argument_list|(
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|block
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
argument_list|)
condition|)
block|{
name|block
operator|.
name|close
argument_list|()
expr_stmt|;
name|long
name|used
init|=
name|usedBytes
operator|.
name|get
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|usedBytes
operator|.
name|compareAndSet
argument_list|(
name|used
argument_list|,
name|used
operator|-
name|block
operator|.
name|getNumBytes
argument_list|()
argument_list|)
condition|)
block|{
name|used
operator|=
name|usedBytes
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully cached block "
operator|+
name|block
operator|.
name|getBlock
argument_list|()
argument_list|)
expr_stmt|;
name|cachedBlocks
operator|.
name|put
argument_list|(
name|block
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|block
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Stats related methods for FsDatasetMBean
DECL|method|getCacheUsed ()
specifier|public
name|long
name|getCacheUsed
parameter_list|()
block|{
return|return
name|usedBytes
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getCacheCapacity ()
specifier|public
name|long
name|getCacheCapacity
parameter_list|()
block|{
return|return
name|maxBytes
return|;
block|}
DECL|method|getCacheRemaining ()
specifier|public
name|long
name|getCacheRemaining
parameter_list|()
block|{
return|return
name|maxBytes
operator|-
name|usedBytes
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

