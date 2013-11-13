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
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
import|;
end_import

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
name|FileNotFoundException
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
name|lang
operator|.
name|builder
operator|.
name|HashCodeBuilder
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
name|BlockListAsLongs
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
name|io
operator|.
name|nativeio
operator|.
name|NativeIO
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
comment|/**    * Keys which identify MappableBlocks.    */
DECL|class|Key
specifier|private
specifier|static
specifier|final
class|class
name|Key
block|{
comment|/**      * Block id.      */
DECL|field|id
specifier|final
name|long
name|id
decl_stmt|;
comment|/**      * Block pool id.      */
DECL|field|bpid
specifier|final
name|String
name|bpid
decl_stmt|;
DECL|method|Key (long id, String bpid)
name|Key
parameter_list|(
name|long
name|id
parameter_list|,
name|String
name|bpid
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|bpid
operator|=
name|bpid
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|o
operator|.
name|getClass
argument_list|()
operator|==
name|getClass
argument_list|()
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Key
name|other
init|=
operator|(
name|Key
operator|)
name|o
decl_stmt|;
return|return
operator|(
operator|(
name|other
operator|.
name|id
operator|==
name|this
operator|.
name|id
operator|)
operator|&&
operator|(
name|other
operator|.
name|bpid
operator|.
name|equals
argument_list|(
name|this
operator|.
name|bpid
argument_list|)
operator|)
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|new
name|HashCodeBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|id
argument_list|)
operator|.
name|append
argument_list|(
name|bpid
argument_list|)
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
empty_stmt|;
comment|/**    * MappableBlocks that we know about.    */
DECL|class|Value
specifier|private
specifier|static
specifier|final
class|class
name|Value
block|{
DECL|field|state
specifier|final
name|State
name|state
decl_stmt|;
DECL|field|mappableBlock
specifier|final
name|MappableBlock
name|mappableBlock
decl_stmt|;
DECL|method|Value (MappableBlock mappableBlock, State state)
name|Value
parameter_list|(
name|MappableBlock
name|mappableBlock
parameter_list|,
name|State
name|state
parameter_list|)
block|{
name|this
operator|.
name|mappableBlock
operator|=
name|mappableBlock
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
block|}
DECL|enum|State
specifier|private
enum|enum
name|State
block|{
comment|/**      * The MappableBlock is in the process of being cached.      */
DECL|enumConstant|CACHING
name|CACHING
block|,
comment|/**      * The MappableBlock was in the process of being cached, but it was      * cancelled.  Only the FsDatasetCache#WorkerTask can remove cancelled      * MappableBlock objects.      */
DECL|enumConstant|CACHING_CANCELLED
name|CACHING_CANCELLED
block|,
comment|/**      * The MappableBlock is in the cache.      */
DECL|enumConstant|CACHED
name|CACHED
block|,
comment|/**      * The MappableBlock is in the process of uncaching.      */
DECL|enumConstant|UNCACHING
name|UNCACHING
block|;
comment|/**      * Whether we should advertise this block as cached to the NameNode and      * clients.      */
DECL|method|shouldAdvertise ()
specifier|public
name|boolean
name|shouldAdvertise
parameter_list|()
block|{
return|return
operator|(
name|this
operator|==
name|CACHED
operator|)
return|;
block|}
block|}
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
comment|/**    * Stores MappableBlock objects and the states they're in.    */
DECL|field|mappableBlockMap
specifier|private
specifier|final
name|HashMap
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|mappableBlockMap
init|=
operator|new
name|HashMap
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|dataset
specifier|private
specifier|final
name|FsDatasetImpl
name|dataset
decl_stmt|;
DECL|field|uncachingExecutor
specifier|private
specifier|final
name|ThreadPoolExecutor
name|uncachingExecutor
decl_stmt|;
comment|/**    * The approximate amount of cache space in use.    *    * This number is an overestimate, counting bytes that will be used only    * if pending caching operations succeed.  It does not take into account    * pending uncaching operations.    *    * This overestimate is more useful to the NameNode than an underestimate,    * since we don't want the NameNode to assign us more replicas than    * we can cache, because of the current batch of operations.    */
DECL|field|usedBytesCount
specifier|private
specifier|final
name|UsedBytesCount
name|usedBytesCount
decl_stmt|;
DECL|class|PageRounder
specifier|public
specifier|static
class|class
name|PageRounder
block|{
DECL|field|osPageSize
specifier|private
specifier|final
name|long
name|osPageSize
init|=
name|NativeIO
operator|.
name|getOperatingSystemPageSize
argument_list|()
decl_stmt|;
comment|/**      * Round up a number to the operating system page size.      */
DECL|method|round (long count)
specifier|public
name|long
name|round
parameter_list|(
name|long
name|count
parameter_list|)
block|{
name|long
name|newCount
init|=
operator|(
name|count
operator|+
operator|(
name|osPageSize
operator|-
literal|1
operator|)
operator|)
operator|/
name|osPageSize
decl_stmt|;
return|return
name|newCount
operator|*
name|osPageSize
return|;
block|}
block|}
DECL|class|UsedBytesCount
specifier|private
class|class
name|UsedBytesCount
block|{
DECL|field|usedBytes
specifier|private
specifier|final
name|AtomicLong
name|usedBytes
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|rounder
specifier|private
name|PageRounder
name|rounder
init|=
operator|new
name|PageRounder
argument_list|()
decl_stmt|;
comment|/**      * Try to reserve more bytes.      *      * @param count    The number of bytes to add.  We will round this      *                 up to the page size.      *      * @return         The new number of usedBytes if we succeeded;      *                 -1 if we failed.      */
DECL|method|reserve (long count)
name|long
name|reserve
parameter_list|(
name|long
name|count
parameter_list|)
block|{
name|count
operator|=
name|rounder
operator|.
name|round
argument_list|(
name|count
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|long
name|cur
init|=
name|usedBytes
operator|.
name|get
argument_list|()
decl_stmt|;
name|long
name|next
init|=
name|cur
operator|+
name|count
decl_stmt|;
if|if
condition|(
name|next
operator|>
name|maxBytes
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|usedBytes
operator|.
name|compareAndSet
argument_list|(
name|cur
argument_list|,
name|next
argument_list|)
condition|)
block|{
return|return
name|next
return|;
block|}
block|}
block|}
comment|/**      * Release some bytes that we're using.      *      * @param count    The number of bytes to release.  We will round this      *                 up to the page size.      *      * @return         The new number of usedBytes.      */
DECL|method|release (long count)
name|long
name|release
parameter_list|(
name|long
name|count
parameter_list|)
block|{
name|count
operator|=
name|rounder
operator|.
name|round
argument_list|(
name|count
argument_list|)
expr_stmt|;
return|return
name|usedBytes
operator|.
name|addAndGet
argument_list|(
operator|-
name|count
argument_list|)
return|;
block|}
DECL|method|get ()
name|long
name|get
parameter_list|()
block|{
return|return
name|usedBytes
operator|.
name|get
argument_list|()
return|;
block|}
block|}
comment|/**    * The total cache capacity in bytes.    */
DECL|field|maxBytes
specifier|private
specifier|final
name|long
name|maxBytes
decl_stmt|;
comment|/**    * Number of cache commands that could not be completed successfully    */
DECL|field|numBlocksFailedToCache
name|AtomicLong
name|numBlocksFailedToCache
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/**    * Number of uncache commands that could not be completed successfully    */
DECL|field|numBlocksFailedToUncache
name|AtomicLong
name|numBlocksFailedToUncache
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
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
literal|"FsDatasetCache-%d-"
operator|+
name|dataset
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|this
operator|.
name|usedBytesCount
operator|=
operator|new
name|UsedBytesCount
argument_list|()
expr_stmt|;
name|this
operator|.
name|uncachingExecutor
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
literal|1
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
expr_stmt|;
name|this
operator|.
name|uncachingExecutor
operator|.
name|allowCoreThreadTimeOut
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return List of cached blocks suitable for translation into a    * {@link BlockListAsLongs} for a cache report.    */
DECL|method|getCachedBlocks (String bpid)
specifier|synchronized
name|List
argument_list|<
name|Long
argument_list|>
name|getCachedBlocks
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
name|List
argument_list|<
name|Long
argument_list|>
name|blocks
init|=
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
argument_list|>
name|iter
init|=
name|mappableBlockMap
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Entry
argument_list|<
name|Key
argument_list|,
name|Value
argument_list|>
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|bpid
operator|.
name|equals
argument_list|(
name|bpid
argument_list|)
condition|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|state
operator|.
name|shouldAdvertise
argument_list|()
condition|)
block|{
name|blocks
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|blocks
return|;
block|}
comment|/**    * Attempt to begin caching a block.    */
DECL|method|cacheBlock (long blockId, String bpid, String blockFileName, long length, long genstamp, Executor volumeExecutor)
specifier|synchronized
name|void
name|cacheBlock
parameter_list|(
name|long
name|blockId
parameter_list|,
name|String
name|bpid
parameter_list|,
name|String
name|blockFileName
parameter_list|,
name|long
name|length
parameter_list|,
name|long
name|genstamp
parameter_list|,
name|Executor
name|volumeExecutor
parameter_list|)
block|{
name|Key
name|key
init|=
operator|new
name|Key
argument_list|(
name|blockId
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
name|Value
name|prevValue
init|=
name|mappableBlockMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|prevValue
operator|!=
literal|null
condition|)
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
literal|"Block with id "
operator|+
name|blockId
operator|+
literal|", pool "
operator|+
name|bpid
operator|+
literal|" already exists in the FsDatasetCache with state "
operator|+
name|prevValue
operator|.
name|state
argument_list|)
expr_stmt|;
block|}
name|numBlocksFailedToCache
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return;
block|}
name|mappableBlockMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
operator|new
name|Value
argument_list|(
literal|null
argument_list|,
name|State
operator|.
name|CACHING
argument_list|)
argument_list|)
expr_stmt|;
name|volumeExecutor
operator|.
name|execute
argument_list|(
operator|new
name|CachingTask
argument_list|(
name|key
argument_list|,
name|blockFileName
argument_list|,
name|length
argument_list|,
name|genstamp
argument_list|)
argument_list|)
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
literal|"Initiating caching for Block with id "
operator|+
name|blockId
operator|+
literal|", pool "
operator|+
name|bpid
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|uncacheBlock (String bpid, long blockId)
specifier|synchronized
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
name|Key
name|key
init|=
operator|new
name|Key
argument_list|(
name|blockId
argument_list|,
name|bpid
argument_list|)
decl_stmt|;
name|Value
name|prevValue
init|=
name|mappableBlockMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|prevValue
operator|==
literal|null
condition|)
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
literal|"Block with id "
operator|+
name|blockId
operator|+
literal|", pool "
operator|+
name|bpid
operator|+
literal|" "
operator|+
literal|"does not need to be uncached, because it is not currently "
operator|+
literal|"in the mappableBlockMap."
argument_list|)
expr_stmt|;
block|}
name|numBlocksFailedToUncache
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return;
block|}
switch|switch
condition|(
name|prevValue
operator|.
name|state
condition|)
block|{
case|case
name|CACHING
case|:
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
literal|"Cancelling caching for block with id "
operator|+
name|blockId
operator|+
literal|", pool "
operator|+
name|bpid
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
name|mappableBlockMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
operator|new
name|Value
argument_list|(
name|prevValue
operator|.
name|mappableBlock
argument_list|,
name|State
operator|.
name|CACHING_CANCELLED
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|CACHED
case|:
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
literal|"Block with id "
operator|+
name|blockId
operator|+
literal|", pool "
operator|+
name|bpid
operator|+
literal|" "
operator|+
literal|"has been scheduled for uncaching."
argument_list|)
expr_stmt|;
block|}
name|mappableBlockMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
operator|new
name|Value
argument_list|(
name|prevValue
operator|.
name|mappableBlock
argument_list|,
name|State
operator|.
name|UNCACHING
argument_list|)
argument_list|)
expr_stmt|;
name|uncachingExecutor
operator|.
name|execute
argument_list|(
operator|new
name|UncachingTask
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
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
literal|"Block with id "
operator|+
name|blockId
operator|+
literal|", pool "
operator|+
name|bpid
operator|+
literal|" "
operator|+
literal|"does not need to be uncached, because it is "
operator|+
literal|"in state "
operator|+
name|prevValue
operator|.
name|state
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
name|numBlocksFailedToUncache
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
comment|/**    * Background worker that mmaps, mlocks, and checksums a block    */
DECL|class|CachingTask
specifier|private
class|class
name|CachingTask
implements|implements
name|Runnable
block|{
DECL|field|key
specifier|private
specifier|final
name|Key
name|key
decl_stmt|;
DECL|field|blockFileName
specifier|private
specifier|final
name|String
name|blockFileName
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|field|genstamp
specifier|private
specifier|final
name|long
name|genstamp
decl_stmt|;
DECL|method|CachingTask (Key key, String blockFileName, long length, long genstamp)
name|CachingTask
parameter_list|(
name|Key
name|key
parameter_list|,
name|String
name|blockFileName
parameter_list|,
name|long
name|length
parameter_list|,
name|long
name|genstamp
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|blockFileName
operator|=
name|blockFileName
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|genstamp
operator|=
name|genstamp
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
name|FileInputStream
name|blockIn
init|=
literal|null
decl_stmt|,
name|metaIn
init|=
literal|null
decl_stmt|;
name|MappableBlock
name|mappableBlock
init|=
literal|null
decl_stmt|;
name|ExtendedBlock
name|extBlk
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|key
operator|.
name|bpid
argument_list|,
name|key
operator|.
name|id
argument_list|,
name|length
argument_list|,
name|genstamp
argument_list|)
decl_stmt|;
name|long
name|newUsedBytes
init|=
name|usedBytesCount
operator|.
name|reserve
argument_list|(
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|newUsedBytes
operator|<
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to cache block id "
operator|+
name|key
operator|.
name|id
operator|+
literal|", pool "
operator|+
name|key
operator|.
name|bpid
operator|+
literal|": could not reserve "
operator|+
name|length
operator|+
literal|" more bytes in the "
operator|+
literal|"cache: "
operator|+
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MAX_LOCKED_MEMORY_KEY
operator|+
literal|" of "
operator|+
name|maxBytes
operator|+
literal|" exceeded."
argument_list|)
expr_stmt|;
name|numBlocksFailedToCache
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return;
block|}
try|try
block|{
try|try
block|{
name|blockIn
operator|=
operator|(
name|FileInputStream
operator|)
name|dataset
operator|.
name|getBlockInputStream
argument_list|(
name|extBlk
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|metaIn
operator|=
operator|(
name|FileInputStream
operator|)
name|dataset
operator|.
name|getMetaDataInputStream
argument_list|(
name|extBlk
argument_list|)
operator|.
name|getWrappedStream
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to cache block with id "
operator|+
name|key
operator|.
name|id
operator|+
literal|", pool "
operator|+
name|key
operator|.
name|bpid
operator|+
literal|": Underlying blocks are not backed by files."
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Failed to cache block with id "
operator|+
name|key
operator|.
name|id
operator|+
literal|", pool "
operator|+
name|key
operator|.
name|bpid
operator|+
literal|": failed to find backing files."
argument_list|)
expr_stmt|;
return|return;
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
literal|"Failed to cache block with id "
operator|+
name|key
operator|.
name|id
operator|+
literal|", pool "
operator|+
name|key
operator|.
name|bpid
operator|+
literal|": failed to open file"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|mappableBlock
operator|=
name|MappableBlock
operator|.
name|load
argument_list|(
name|length
argument_list|,
name|blockIn
argument_list|,
name|metaIn
argument_list|,
name|blockFileName
argument_list|)
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
name|key
operator|.
name|id
operator|+
literal|" in "
operator|+
name|key
operator|.
name|bpid
operator|+
literal|": "
operator|+
literal|"checksum verification failed."
argument_list|)
expr_stmt|;
return|return;
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
name|key
operator|.
name|id
operator|+
literal|" in "
operator|+
name|key
operator|.
name|bpid
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
synchronized|synchronized
init|(
name|FsDatasetCache
operator|.
name|this
init|)
block|{
name|Value
name|value
init|=
name|mappableBlockMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|value
operator|.
name|state
operator|==
name|State
operator|.
name|CACHING
operator|||
name|value
operator|.
name|state
operator|==
name|State
operator|.
name|CACHING_CANCELLED
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|.
name|state
operator|==
name|State
operator|.
name|CACHING_CANCELLED
condition|)
block|{
name|mappableBlockMap
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Caching of block "
operator|+
name|key
operator|.
name|id
operator|+
literal|" in "
operator|+
name|key
operator|.
name|bpid
operator|+
literal|" was cancelled."
argument_list|)
expr_stmt|;
return|return;
block|}
name|mappableBlockMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
operator|new
name|Value
argument_list|(
name|mappableBlock
argument_list|,
name|State
operator|.
name|CACHED
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"Successfully cached block "
operator|+
name|key
operator|.
name|id
operator|+
literal|" in "
operator|+
name|key
operator|.
name|bpid
operator|+
literal|".  We are now caching "
operator|+
name|newUsedBytes
operator|+
literal|" bytes in total."
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|newUsedBytes
operator|=
name|usedBytesCount
operator|.
name|release
argument_list|(
name|length
argument_list|)
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
literal|"Caching of block "
operator|+
name|key
operator|.
name|id
operator|+
literal|" in "
operator|+
name|key
operator|.
name|bpid
operator|+
literal|" was aborted.  We are now caching only "
operator|+
name|newUsedBytes
operator|+
literal|" + bytes in total."
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|mappableBlock
operator|!=
literal|null
condition|)
block|{
name|mappableBlock
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|numBlocksFailedToCache
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|FsDatasetCache
operator|.
name|this
init|)
block|{
name|mappableBlockMap
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|class|UncachingTask
specifier|private
class|class
name|UncachingTask
implements|implements
name|Runnable
block|{
DECL|field|key
specifier|private
specifier|final
name|Key
name|key
decl_stmt|;
DECL|method|UncachingTask (Key key)
name|UncachingTask
parameter_list|(
name|Key
name|key
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
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
name|Value
name|value
decl_stmt|;
synchronized|synchronized
init|(
name|FsDatasetCache
operator|.
name|this
init|)
block|{
name|value
operator|=
name|mappableBlockMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|value
operator|.
name|state
operator|==
name|State
operator|.
name|UNCACHING
argument_list|)
expr_stmt|;
comment|// TODO: we will eventually need to do revocation here if any clients
comment|// are reading via mmap with checksums enabled.  See HDFS-5182.
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|value
operator|.
name|mappableBlock
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|FsDatasetCache
operator|.
name|this
init|)
block|{
name|mappableBlockMap
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
name|long
name|newUsedBytes
init|=
name|usedBytesCount
operator|.
name|release
argument_list|(
name|value
operator|.
name|mappableBlock
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
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
literal|"Uncaching of block "
operator|+
name|key
operator|.
name|id
operator|+
literal|" in "
operator|+
name|key
operator|.
name|bpid
operator|+
literal|" completed.  usedBytes = "
operator|+
name|newUsedBytes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Stats related methods for FSDatasetMBean
comment|/**    * Get the approximate amount of cache space used.    */
DECL|method|getDnCacheUsed ()
specifier|public
name|long
name|getDnCacheUsed
parameter_list|()
block|{
return|return
name|usedBytesCount
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Get the maximum amount of bytes we can cache.  This is a constant.    */
DECL|method|getDnCacheCapacity ()
specifier|public
name|long
name|getDnCacheCapacity
parameter_list|()
block|{
return|return
name|maxBytes
return|;
block|}
DECL|method|getNumBlocksFailedToCache ()
specifier|public
name|long
name|getNumBlocksFailedToCache
parameter_list|()
block|{
return|return
name|numBlocksFailedToCache
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getNumBlocksFailedToUncache ()
specifier|public
name|long
name|getNumBlocksFailedToUncache
parameter_list|()
block|{
return|return
name|numBlocksFailedToUncache
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

