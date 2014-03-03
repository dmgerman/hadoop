begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
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
name|classification
operator|.
name|InterfaceAudience
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|MappedByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
operator|.
name|MapMode
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
name|ExtendedBlockId
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
name|ShortCircuitShm
operator|.
name|Slot
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
name|BlockMetadataHeader
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
name|NativeIO
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

begin_comment
comment|/**  * A ShortCircuitReplica object contains file descriptors for a block that  * we are reading via short-circuit local reads.  *  * The file descriptors can be shared between multiple threads because  * all the operations we perform are stateless-- i.e., we use pread  * instead of read, to avoid using the shared position state.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ShortCircuitReplica
specifier|public
class|class
name|ShortCircuitReplica
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
name|ShortCircuitCache
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Identifies this ShortCircuitReplica object.    */
DECL|field|key
specifier|final
name|ExtendedBlockId
name|key
decl_stmt|;
comment|/**    * The block data input stream.    */
DECL|field|dataStream
specifier|private
specifier|final
name|FileInputStream
name|dataStream
decl_stmt|;
comment|/**    * The block metadata input stream.    *    * TODO: make this nullable if the file has no checksums on disk.    */
DECL|field|metaStream
specifier|private
specifier|final
name|FileInputStream
name|metaStream
decl_stmt|;
comment|/**    * Block metadata header.    */
DECL|field|metaHeader
specifier|private
specifier|final
name|BlockMetadataHeader
name|metaHeader
decl_stmt|;
comment|/**    * The cache we belong to.    */
DECL|field|cache
specifier|private
specifier|final
name|ShortCircuitCache
name|cache
decl_stmt|;
comment|/**    * Monotonic time at which the replica was created.    */
DECL|field|creationTimeMs
specifier|private
specifier|final
name|long
name|creationTimeMs
decl_stmt|;
comment|/**    * If non-null, the shared memory slot associated with this replica.    */
DECL|field|slot
specifier|private
specifier|final
name|Slot
name|slot
decl_stmt|;
comment|/**    * Current mmap state.    *    * Protected by the cache lock.    */
DECL|field|mmapData
name|Object
name|mmapData
decl_stmt|;
comment|/**    * True if this replica has been purged from the cache; false otherwise.    *    * Protected by the cache lock.    */
DECL|field|purged
name|boolean
name|purged
init|=
literal|false
decl_stmt|;
comment|/**    * Number of external references to this replica.  Replicas are referenced    * by the cache, BlockReaderLocal instances, and by ClientMmap instances.    * The number starts at 2 because when we create a replica, it is referenced    * by both the cache and the requester.    *    * Protected by the cache lock.    */
DECL|field|refCount
name|int
name|refCount
init|=
literal|2
decl_stmt|;
comment|/**    * The monotonic time in nanoseconds at which the replica became evictable, or    * null if it is not evictable.    *    * Protected by the cache lock.    */
DECL|field|evictableTimeNs
specifier|private
name|Long
name|evictableTimeNs
init|=
literal|null
decl_stmt|;
DECL|method|ShortCircuitReplica (ExtendedBlockId key, FileInputStream dataStream, FileInputStream metaStream, ShortCircuitCache cache, long creationTimeMs, Slot slot)
specifier|public
name|ShortCircuitReplica
parameter_list|(
name|ExtendedBlockId
name|key
parameter_list|,
name|FileInputStream
name|dataStream
parameter_list|,
name|FileInputStream
name|metaStream
parameter_list|,
name|ShortCircuitCache
name|cache
parameter_list|,
name|long
name|creationTimeMs
parameter_list|,
name|Slot
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|dataStream
operator|=
name|dataStream
expr_stmt|;
name|this
operator|.
name|metaStream
operator|=
name|metaStream
expr_stmt|;
name|this
operator|.
name|metaHeader
operator|=
name|BlockMetadataHeader
operator|.
name|preadHeader
argument_list|(
name|metaStream
operator|.
name|getChannel
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|metaHeader
operator|.
name|getVersion
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"invalid metadata header version "
operator|+
name|metaHeader
operator|.
name|getVersion
argument_list|()
operator|+
literal|".  Can only handle version 1."
argument_list|)
throw|;
block|}
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
name|this
operator|.
name|creationTimeMs
operator|=
name|creationTimeMs
expr_stmt|;
name|this
operator|.
name|slot
operator|=
name|slot
expr_stmt|;
block|}
comment|/**    * Decrement the reference count.    */
DECL|method|unref ()
specifier|public
name|void
name|unref
parameter_list|()
block|{
name|cache
operator|.
name|unref
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check if the replica is stale.    *    * Must be called with the cache lock held.    */
DECL|method|isStale ()
name|boolean
name|isStale
parameter_list|()
block|{
if|if
condition|(
name|slot
operator|!=
literal|null
condition|)
block|{
comment|// Check staleness by looking at the shared memory area we use to
comment|// communicate with the DataNode.
name|boolean
name|stale
init|=
operator|!
name|slot
operator|.
name|isValid
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|": checked shared memory segment.  isStale="
operator|+
name|stale
argument_list|)
expr_stmt|;
block|}
return|return
name|stale
return|;
block|}
else|else
block|{
comment|// Fall back to old, time-based staleness method.
name|long
name|deltaMs
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|creationTimeMs
decl_stmt|;
name|long
name|staleThresholdMs
init|=
name|cache
operator|.
name|getStaleThresholdMs
argument_list|()
decl_stmt|;
if|if
condition|(
name|deltaMs
operator|>
name|staleThresholdMs
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|" is stale because it's "
operator|+
name|deltaMs
operator|+
literal|" ms old, and staleThresholdMs = "
operator|+
name|staleThresholdMs
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|" is not stale because it's only "
operator|+
name|deltaMs
operator|+
literal|" ms old, and staleThresholdMs = "
operator|+
name|staleThresholdMs
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
block|}
comment|/**    * Try to add a no-checksum anchor to our shared memory slot.    *    * It is only possible to add this anchor when the block is mlocked on the Datanode.    * The DataNode will not munlock the block until the number of no-checksum anchors    * for the block reaches zero.    *     * This method does not require any synchronization.    *    * @return     True if we successfully added a no-checksum anchor.    */
DECL|method|addNoChecksumAnchor ()
specifier|public
name|boolean
name|addNoChecksumAnchor
parameter_list|()
block|{
if|if
condition|(
name|slot
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|slot
operator|.
name|addAnchor
argument_list|()
return|;
block|}
comment|/**    * Remove a no-checksum anchor for our shared memory slot.    *    * This method does not require any synchronization.    */
DECL|method|removeNoChecksumAnchor ()
specifier|public
name|void
name|removeNoChecksumAnchor
parameter_list|()
block|{
if|if
condition|(
name|slot
operator|!=
literal|null
condition|)
block|{
name|slot
operator|.
name|removeAnchor
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Check if the replica has an associated mmap that has been fully loaded.    *    * Must be called with the cache lock held.    */
annotation|@
name|VisibleForTesting
DECL|method|hasMmap ()
specifier|public
name|boolean
name|hasMmap
parameter_list|()
block|{
return|return
operator|(
operator|(
name|mmapData
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|mmapData
operator|instanceof
name|MappedByteBuffer
operator|)
operator|)
return|;
block|}
comment|/**    * Free the mmap associated with this replica.    *    * Must be called with the cache lock held.    */
DECL|method|munmap ()
name|void
name|munmap
parameter_list|()
block|{
name|MappedByteBuffer
name|mmap
init|=
operator|(
name|MappedByteBuffer
operator|)
name|mmapData
decl_stmt|;
name|NativeIO
operator|.
name|POSIX
operator|.
name|munmap
argument_list|(
name|mmap
argument_list|)
expr_stmt|;
name|mmapData
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Close the replica.    *    * Must be called after there are no more references to the replica in the    * cache or elsewhere.    */
DECL|method|close ()
name|void
name|close
parameter_list|()
block|{
name|String
name|suffix
init|=
literal|""
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|refCount
operator|==
literal|0
argument_list|,
literal|"tried to close replica with refCount "
operator|+
name|refCount
operator|+
literal|": "
operator|+
name|this
argument_list|)
expr_stmt|;
name|refCount
operator|=
operator|-
literal|1
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|purged
argument_list|,
literal|"tried to close unpurged replica "
operator|+
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasMmap
argument_list|()
condition|)
block|{
name|munmap
argument_list|()
expr_stmt|;
name|suffix
operator|+=
literal|"  munmapped."
expr_stmt|;
block|}
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|dataStream
argument_list|,
name|metaStream
argument_list|)
expr_stmt|;
if|if
condition|(
name|slot
operator|!=
literal|null
condition|)
block|{
name|cache
operator|.
name|scheduleSlotReleaser
argument_list|(
name|slot
argument_list|)
expr_stmt|;
name|suffix
operator|+=
literal|"  scheduling "
operator|+
name|slot
operator|+
literal|" for later release."
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"closed "
operator|+
name|this
operator|+
name|suffix
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getDataStream ()
specifier|public
name|FileInputStream
name|getDataStream
parameter_list|()
block|{
return|return
name|dataStream
return|;
block|}
DECL|method|getMetaStream ()
specifier|public
name|FileInputStream
name|getMetaStream
parameter_list|()
block|{
return|return
name|metaStream
return|;
block|}
DECL|method|getMetaHeader ()
specifier|public
name|BlockMetadataHeader
name|getMetaHeader
parameter_list|()
block|{
return|return
name|metaHeader
return|;
block|}
DECL|method|getKey ()
specifier|public
name|ExtendedBlockId
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
DECL|method|getOrCreateClientMmap (boolean anchor)
specifier|public
name|ClientMmap
name|getOrCreateClientMmap
parameter_list|(
name|boolean
name|anchor
parameter_list|)
block|{
return|return
name|cache
operator|.
name|getOrCreateClientMmap
argument_list|(
name|this
argument_list|,
name|anchor
argument_list|)
return|;
block|}
DECL|method|loadMmapInternal ()
name|MappedByteBuffer
name|loadMmapInternal
parameter_list|()
block|{
try|try
block|{
name|FileChannel
name|channel
init|=
name|dataStream
operator|.
name|getChannel
argument_list|()
decl_stmt|;
return|return
name|channel
operator|.
name|map
argument_list|(
name|MapMode
operator|.
name|READ_ONLY
argument_list|,
literal|0
argument_list|,
name|channel
operator|.
name|size
argument_list|()
argument_list|)
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
name|warn
argument_list|(
name|this
operator|+
literal|": mmap error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|this
operator|+
literal|": mmap error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Get the evictable time in nanoseconds.    *    * Note: you must hold the cache lock to call this function.    *    * @return the evictable time in nanoseconds.    */
DECL|method|getEvictableTimeNs ()
specifier|public
name|Long
name|getEvictableTimeNs
parameter_list|()
block|{
return|return
name|evictableTimeNs
return|;
block|}
comment|/**    * Set the evictable time in nanoseconds.    *    * Note: you must hold the cache lock to call this function.    *    * @param evictableTimeNs   The evictable time in nanoseconds, or null    *                          to set no evictable time.    */
DECL|method|setEvictableTimeNs (Long evictableTimeNs)
name|void
name|setEvictableTimeNs
parameter_list|(
name|Long
name|evictableTimeNs
parameter_list|)
block|{
name|this
operator|.
name|evictableTimeNs
operator|=
name|evictableTimeNs
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getSlot ()
specifier|public
name|Slot
name|getSlot
parameter_list|()
block|{
return|return
name|slot
return|;
block|}
comment|/**    * Convert the replica to a string for debugging purposes.    * Note that we can't take the lock here.    */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
literal|"ShortCircuitReplica{"
argument_list|)
operator|.
name|append
argument_list|(
literal|"key="
argument_list|)
operator|.
name|append
argument_list|(
name|key
argument_list|)
operator|.
name|append
argument_list|(
literal|", metaHeader.version="
argument_list|)
operator|.
name|append
argument_list|(
name|metaHeader
operator|.
name|getVersion
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", metaHeader.checksum="
argument_list|)
operator|.
name|append
argument_list|(
name|metaHeader
operator|.
name|getChecksum
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", ident="
argument_list|)
operator|.
name|append
argument_list|(
literal|"0x"
argument_list|)
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|", creationTimeMs="
argument_list|)
operator|.
name|append
argument_list|(
name|creationTimeMs
argument_list|)
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

