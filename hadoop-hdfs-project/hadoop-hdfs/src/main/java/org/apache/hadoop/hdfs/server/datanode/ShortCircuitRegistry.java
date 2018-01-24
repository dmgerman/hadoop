begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SHARED_FILE_DESCRIPTOR_PATHS
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SHARED_FILE_DESCRIPTOR_PATHS_DEFAULT
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_SHORT_CIRCUIT_SHARED_MEMORY_WATCHER_INTERRUPT_CHECK_MS
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_SHORT_CIRCUIT_SHARED_MEMORY_WATCHER_INTERRUPT_CHECK_MS_DEFAULT
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Iterator
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
name|InvalidRequestException
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
name|shortcircuit
operator|.
name|ShortCircuitShm
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
name|shortcircuit
operator|.
name|ShortCircuitShm
operator|.
name|ShmId
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
name|shortcircuit
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
name|shortcircuit
operator|.
name|ShortCircuitShm
operator|.
name|SlotId
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
name|SharedFileDescriptorFactory
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
name|net
operator|.
name|unix
operator|.
name|DomainSocket
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
name|net
operator|.
name|unix
operator|.
name|DomainSocketWatcher
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
name|shortcircuit
operator|.
name|DfsClientShmManager
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
name|Joiner
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
name|collect
operator|.
name|HashMultimap
import|;
end_import

begin_comment
comment|/**  * Manages client short-circuit memory segments on the DataNode.  *  * DFSClients request shared memory segments from the DataNode.  The   * ShortCircuitRegistry generates and manages these segments.  Each segment  * has a randomly generated 128-bit ID which uniquely identifies it.  The  * segments each contain several "slots."  *  * Before performing a short-circuit read, DFSClients must request a pair of  * file descriptors from the DataNode via the REQUEST_SHORT_CIRCUIT_FDS  * operation.  As part of this operation, DFSClients pass the ID of the shared  * memory segment they would like to use to communicate information about this  * replica, as well as the slot number within that segment they would like to  * use.  Slot allocation is always done by the client.  *  * Slots are used to track the state of the block on the both the client and  * datanode. When this DataNode mlocks a block, the corresponding slots for the  * replicas are marked as "anchorable".  Anchorable blocks can be safely read  * without verifying the checksum.  This means that BlockReaderLocal objects  * using these replicas can skip checksumming.  It also means that we can do  * zero-copy reads on these replicas (the ZCR interface has no way of  * verifying checksums.)  *   * When a DN needs to munlock a block, it needs to first wait for the block to  * be unanchored by clients doing a no-checksum read or a zero-copy read. The   * DN also marks the block's slots as "unanchorable" to prevent additional   * clients from initiating these operations in the future.  *   * The counterpart of this class on the client is {@link DfsClientShmManager}.  */
end_comment

begin_class
DECL|class|ShortCircuitRegistry
specifier|public
class|class
name|ShortCircuitRegistry
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
name|ShortCircuitRegistry
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SHM_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|SHM_LENGTH
init|=
literal|8192
decl_stmt|;
DECL|class|RegisteredShm
specifier|public
specifier|static
class|class
name|RegisteredShm
extends|extends
name|ShortCircuitShm
implements|implements
name|DomainSocketWatcher
operator|.
name|Handler
block|{
DECL|field|clientName
specifier|private
specifier|final
name|String
name|clientName
decl_stmt|;
DECL|field|registry
specifier|private
specifier|final
name|ShortCircuitRegistry
name|registry
decl_stmt|;
DECL|method|RegisteredShm (String clientName, ShmId shmId, FileInputStream stream, ShortCircuitRegistry registry)
name|RegisteredShm
parameter_list|(
name|String
name|clientName
parameter_list|,
name|ShmId
name|shmId
parameter_list|,
name|FileInputStream
name|stream
parameter_list|,
name|ShortCircuitRegistry
name|registry
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|shmId
argument_list|,
name|stream
argument_list|)
expr_stmt|;
name|this
operator|.
name|clientName
operator|=
name|clientName
expr_stmt|;
name|this
operator|.
name|registry
operator|=
name|registry
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (DomainSocket sock)
specifier|public
name|boolean
name|handle
parameter_list|(
name|DomainSocket
name|sock
parameter_list|)
block|{
synchronized|synchronized
init|(
name|registry
init|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|registry
operator|.
name|removeShm
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|getClientName ()
name|String
name|getClientName
parameter_list|()
block|{
return|return
name|clientName
return|;
block|}
block|}
DECL|method|removeShm (ShortCircuitShm shm)
specifier|public
specifier|synchronized
name|void
name|removeShm
parameter_list|(
name|ShortCircuitShm
name|shm
parameter_list|)
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
literal|"removing shm "
operator|+
name|shm
argument_list|)
expr_stmt|;
block|}
comment|// Stop tracking the shmId.
name|RegisteredShm
name|removedShm
init|=
name|segments
operator|.
name|remove
argument_list|(
name|shm
operator|.
name|getShmId
argument_list|()
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|removedShm
operator|==
name|shm
argument_list|,
literal|"failed to remove "
operator|+
name|shm
operator|.
name|getShmId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Stop tracking the slots.
for|for
control|(
name|Iterator
argument_list|<
name|Slot
argument_list|>
name|iter
init|=
name|shm
operator|.
name|slotIterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Slot
name|slot
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|boolean
name|removed
init|=
name|slots
operator|.
name|remove
argument_list|(
name|slot
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|slot
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|removed
argument_list|)
expr_stmt|;
name|slot
operator|.
name|makeInvalid
argument_list|()
expr_stmt|;
block|}
comment|// De-allocate the memory map and close the shared file.
name|shm
operator|.
name|free
argument_list|()
expr_stmt|;
block|}
comment|/**    * Whether or not the registry is enabled.    */
DECL|field|enabled
specifier|private
name|boolean
name|enabled
decl_stmt|;
comment|/**    * The factory which creates shared file descriptors.    */
DECL|field|shmFactory
specifier|private
specifier|final
name|SharedFileDescriptorFactory
name|shmFactory
decl_stmt|;
comment|/**    * A watcher which sends out callbacks when the UNIX domain socket    * associated with a shared memory segment closes.    */
DECL|field|watcher
specifier|private
specifier|final
name|DomainSocketWatcher
name|watcher
decl_stmt|;
DECL|field|segments
specifier|private
specifier|final
name|HashMap
argument_list|<
name|ShmId
argument_list|,
name|RegisteredShm
argument_list|>
name|segments
init|=
operator|new
name|HashMap
argument_list|<
name|ShmId
argument_list|,
name|RegisteredShm
argument_list|>
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|slots
specifier|private
specifier|final
name|HashMultimap
argument_list|<
name|ExtendedBlockId
argument_list|,
name|Slot
argument_list|>
name|slots
init|=
name|HashMultimap
operator|.
name|create
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
DECL|method|ShortCircuitRegistry (Configuration conf)
specifier|public
name|ShortCircuitRegistry
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|enabled
init|=
literal|false
decl_stmt|;
name|SharedFileDescriptorFactory
name|shmFactory
init|=
literal|null
decl_stmt|;
name|DomainSocketWatcher
name|watcher
init|=
literal|null
decl_stmt|;
try|try
block|{
name|int
name|interruptCheck
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFS_SHORT_CIRCUIT_SHARED_MEMORY_WATCHER_INTERRUPT_CHECK_MS
argument_list|,
name|DFS_SHORT_CIRCUIT_SHARED_MEMORY_WATCHER_INTERRUPT_CHECK_MS_DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
name|interruptCheck
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|DFS_SHORT_CIRCUIT_SHARED_MEMORY_WATCHER_INTERRUPT_CHECK_MS
operator|+
literal|" was set to "
operator|+
name|interruptCheck
argument_list|)
throw|;
block|}
name|String
index|[]
name|shmPaths
init|=
name|conf
operator|.
name|getTrimmedStrings
argument_list|(
name|DFS_DATANODE_SHARED_FILE_DESCRIPTOR_PATHS
argument_list|)
decl_stmt|;
if|if
condition|(
name|shmPaths
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|shmPaths
operator|=
name|DFS_DATANODE_SHARED_FILE_DESCRIPTOR_PATHS_DEFAULT
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|shmFactory
operator|=
name|SharedFileDescriptorFactory
operator|.
name|create
argument_list|(
literal|"HadoopShortCircuitShm_"
argument_list|,
name|shmPaths
argument_list|)
expr_stmt|;
name|String
name|dswLoadingFailure
init|=
name|DomainSocketWatcher
operator|.
name|getLoadingFailureReason
argument_list|()
decl_stmt|;
if|if
condition|(
name|dswLoadingFailure
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|dswLoadingFailure
argument_list|)
throw|;
block|}
name|watcher
operator|=
operator|new
name|DomainSocketWatcher
argument_list|(
name|interruptCheck
argument_list|,
literal|"datanode"
argument_list|)
expr_stmt|;
name|enabled
operator|=
literal|true
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
literal|"created new ShortCircuitRegistry with interruptCheck="
operator|+
name|interruptCheck
operator|+
literal|", shmPath="
operator|+
name|shmFactory
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
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
literal|"Disabling ShortCircuitRegistry"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
name|this
operator|.
name|shmFactory
operator|=
name|shmFactory
expr_stmt|;
name|this
operator|.
name|watcher
operator|=
name|watcher
expr_stmt|;
block|}
block|}
comment|/**    * Process a block mlock event from the FsDatasetCache.    *    * @param blockId    The block that was mlocked.    */
DECL|method|processBlockMlockEvent (ExtendedBlockId blockId)
specifier|public
specifier|synchronized
name|void
name|processBlockMlockEvent
parameter_list|(
name|ExtendedBlockId
name|blockId
parameter_list|)
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
return|return;
name|Set
argument_list|<
name|Slot
argument_list|>
name|affectedSlots
init|=
name|slots
operator|.
name|get
argument_list|(
name|blockId
argument_list|)
decl_stmt|;
for|for
control|(
name|Slot
name|slot
range|:
name|affectedSlots
control|)
block|{
name|slot
operator|.
name|makeAnchorable
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Mark any slots associated with this blockId as unanchorable.    *    * @param blockId        The block ID.    * @return               True if we should allow the munlock request.    */
DECL|method|processBlockMunlockRequest ( ExtendedBlockId blockId)
specifier|public
specifier|synchronized
name|boolean
name|processBlockMunlockRequest
parameter_list|(
name|ExtendedBlockId
name|blockId
parameter_list|)
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
return|return
literal|true
return|;
name|boolean
name|allowMunlock
init|=
literal|true
decl_stmt|;
name|Set
argument_list|<
name|Slot
argument_list|>
name|affectedSlots
init|=
name|slots
operator|.
name|get
argument_list|(
name|blockId
argument_list|)
decl_stmt|;
for|for
control|(
name|Slot
name|slot
range|:
name|affectedSlots
control|)
block|{
name|slot
operator|.
name|makeUnanchorable
argument_list|()
expr_stmt|;
if|if
condition|(
name|slot
operator|.
name|isAnchored
argument_list|()
condition|)
block|{
name|allowMunlock
operator|=
literal|false
expr_stmt|;
block|}
block|}
return|return
name|allowMunlock
return|;
block|}
comment|/**    * Invalidate any slot associated with a blockId that we are invalidating    * (deleting) from this DataNode.  When a slot is invalid, the DFSClient will    * not use the corresponding replica for new read or mmap operations (although    * existing, ongoing read or mmap operations will complete.)    *    * @param blockId        The block ID.    */
DECL|method|processBlockInvalidation (ExtendedBlockId blockId)
specifier|public
specifier|synchronized
name|void
name|processBlockInvalidation
parameter_list|(
name|ExtendedBlockId
name|blockId
parameter_list|)
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
return|return;
specifier|final
name|Set
argument_list|<
name|Slot
argument_list|>
name|affectedSlots
init|=
name|slots
operator|.
name|get
argument_list|(
name|blockId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|affectedSlots
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|StringBuilder
name|bld
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|prefix
init|=
literal|""
decl_stmt|;
name|bld
operator|.
name|append
argument_list|(
literal|"Block "
argument_list|)
operator|.
name|append
argument_list|(
name|blockId
argument_list|)
operator|.
name|append
argument_list|(
literal|" has been invalidated.  "
argument_list|)
operator|.
name|append
argument_list|(
literal|"Marking short-circuit slots as invalid: "
argument_list|)
expr_stmt|;
for|for
control|(
name|Slot
name|slot
range|:
name|affectedSlots
control|)
block|{
name|slot
operator|.
name|makeInvalid
argument_list|()
expr_stmt|;
name|bld
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
operator|.
name|append
argument_list|(
name|slot
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|", "
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|bld
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getClientNames (ExtendedBlockId blockId)
specifier|public
specifier|synchronized
name|String
name|getClientNames
parameter_list|(
name|ExtendedBlockId
name|blockId
parameter_list|)
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
return|return
literal|""
return|;
specifier|final
name|HashSet
argument_list|<
name|String
argument_list|>
name|clientNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Slot
argument_list|>
name|affectedSlots
init|=
name|slots
operator|.
name|get
argument_list|(
name|blockId
argument_list|)
decl_stmt|;
for|for
control|(
name|Slot
name|slot
range|:
name|affectedSlots
control|)
block|{
name|clientNames
operator|.
name|add
argument_list|(
operator|(
operator|(
name|RegisteredShm
operator|)
name|slot
operator|.
name|getShm
argument_list|()
operator|)
operator|.
name|getClientName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|join
argument_list|(
name|clientNames
argument_list|)
return|;
block|}
DECL|class|NewShmInfo
specifier|public
specifier|static
class|class
name|NewShmInfo
implements|implements
name|Closeable
block|{
DECL|field|shmId
specifier|private
specifier|final
name|ShmId
name|shmId
decl_stmt|;
DECL|field|stream
specifier|private
specifier|final
name|FileInputStream
name|stream
decl_stmt|;
DECL|method|NewShmInfo (ShmId shmId, FileInputStream stream)
name|NewShmInfo
parameter_list|(
name|ShmId
name|shmId
parameter_list|,
name|FileInputStream
name|stream
parameter_list|)
block|{
name|this
operator|.
name|shmId
operator|=
name|shmId
expr_stmt|;
name|this
operator|.
name|stream
operator|=
name|stream
expr_stmt|;
block|}
DECL|method|getShmId ()
specifier|public
name|ShmId
name|getShmId
parameter_list|()
block|{
return|return
name|shmId
return|;
block|}
DECL|method|getFileStream ()
specifier|public
name|FileInputStream
name|getFileStream
parameter_list|()
block|{
return|return
name|stream
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
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Handle a DFSClient request to create a new memory segment.    *    * @param clientName    Client name as reported by the client.    * @param sock          The DomainSocket to associate with this memory    *                        segment.  When this socket is closed, or the    *                        other side writes anything to the socket, the    *                        segment will be closed.  This can happen at any    *                        time, including right after this function returns.    * @return              A NewShmInfo object.  The caller must close the    *                        NewShmInfo object once they are done with it.    * @throws IOException  If the new memory segment could not be created.    */
DECL|method|createNewMemorySegment (String clientName, DomainSocket sock)
specifier|public
name|NewShmInfo
name|createNewMemorySegment
parameter_list|(
name|String
name|clientName
parameter_list|,
name|DomainSocket
name|sock
parameter_list|)
throws|throws
name|IOException
block|{
name|NewShmInfo
name|info
init|=
literal|null
decl_stmt|;
name|RegisteredShm
name|shm
init|=
literal|null
decl_stmt|;
name|ShmId
name|shmId
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
operator|!
name|enabled
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
literal|"createNewMemorySegment: ShortCircuitRegistry is "
operator|+
literal|"not enabled."
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
name|FileInputStream
name|fis
init|=
literal|null
decl_stmt|;
try|try
block|{
do|do
block|{
name|shmId
operator|=
name|ShmId
operator|.
name|createRandom
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|segments
operator|.
name|containsKey
argument_list|(
name|shmId
argument_list|)
condition|)
do|;
name|fis
operator|=
name|shmFactory
operator|.
name|createDescriptor
argument_list|(
name|clientName
argument_list|,
name|SHM_LENGTH
argument_list|)
expr_stmt|;
name|shm
operator|=
operator|new
name|RegisteredShm
argument_list|(
name|clientName
argument_list|,
name|shmId
argument_list|,
name|fis
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|shm
operator|==
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|fis
argument_list|)
expr_stmt|;
block|}
block|}
name|info
operator|=
operator|new
name|NewShmInfo
argument_list|(
name|shmId
argument_list|,
name|fis
argument_list|)
expr_stmt|;
name|segments
operator|.
name|put
argument_list|(
name|shmId
argument_list|,
name|shm
argument_list|)
expr_stmt|;
block|}
comment|// Drop the registry lock to prevent deadlock.
comment|// After this point, RegisteredShm#handle may be called at any time.
name|watcher
operator|.
name|add
argument_list|(
name|sock
argument_list|,
name|shm
argument_list|)
expr_stmt|;
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
literal|"createNewMemorySegment: created "
operator|+
name|info
operator|.
name|shmId
argument_list|)
expr_stmt|;
block|}
return|return
name|info
return|;
block|}
DECL|method|registerSlot (ExtendedBlockId blockId, SlotId slotId, boolean isCached)
specifier|public
specifier|synchronized
name|void
name|registerSlot
parameter_list|(
name|ExtendedBlockId
name|blockId
parameter_list|,
name|SlotId
name|slotId
parameter_list|,
name|boolean
name|isCached
parameter_list|)
throws|throws
name|InvalidRequestException
block|{
if|if
condition|(
operator|!
name|enabled
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
literal|" can't register a slot because the "
operator|+
literal|"ShortCircuitRegistry is not enabled."
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
name|ShmId
name|shmId
init|=
name|slotId
operator|.
name|getShmId
argument_list|()
decl_stmt|;
name|RegisteredShm
name|shm
init|=
name|segments
operator|.
name|get
argument_list|(
name|shmId
argument_list|)
decl_stmt|;
if|if
condition|(
name|shm
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidRequestException
argument_list|(
literal|"there is no shared memory segment "
operator|+
literal|"registered with shmId "
operator|+
name|shmId
argument_list|)
throw|;
block|}
name|Slot
name|slot
init|=
name|shm
operator|.
name|registerSlot
argument_list|(
name|slotId
operator|.
name|getSlotIdx
argument_list|()
argument_list|,
name|blockId
argument_list|)
decl_stmt|;
if|if
condition|(
name|isCached
condition|)
block|{
name|slot
operator|.
name|makeAnchorable
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|slot
operator|.
name|makeUnanchorable
argument_list|()
expr_stmt|;
block|}
name|boolean
name|added
init|=
name|slots
operator|.
name|put
argument_list|(
name|blockId
argument_list|,
name|slot
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|added
argument_list|)
expr_stmt|;
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
literal|": registered "
operator|+
name|blockId
operator|+
literal|" with slot "
operator|+
name|slotId
operator|+
literal|" (isCached="
operator|+
name|isCached
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|unregisterSlot (SlotId slotId)
specifier|public
specifier|synchronized
name|void
name|unregisterSlot
parameter_list|(
name|SlotId
name|slotId
parameter_list|)
throws|throws
name|InvalidRequestException
block|{
if|if
condition|(
operator|!
name|enabled
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
literal|"unregisterSlot: ShortCircuitRegistry is "
operator|+
literal|"not enabled."
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
name|ShmId
name|shmId
init|=
name|slotId
operator|.
name|getShmId
argument_list|()
decl_stmt|;
name|RegisteredShm
name|shm
init|=
name|segments
operator|.
name|get
argument_list|(
name|shmId
argument_list|)
decl_stmt|;
if|if
condition|(
name|shm
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidRequestException
argument_list|(
literal|"there is no shared memory segment "
operator|+
literal|"registered with shmId "
operator|+
name|shmId
argument_list|)
throw|;
block|}
name|Slot
name|slot
init|=
name|shm
operator|.
name|getSlot
argument_list|(
name|slotId
operator|.
name|getSlotIdx
argument_list|()
argument_list|)
decl_stmt|;
name|slot
operator|.
name|makeInvalid
argument_list|()
expr_stmt|;
name|shm
operator|.
name|unregisterSlot
argument_list|(
name|slotId
operator|.
name|getSlotIdx
argument_list|()
argument_list|)
expr_stmt|;
name|slots
operator|.
name|remove
argument_list|(
name|slot
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|slot
argument_list|)
expr_stmt|;
block|}
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
return|return;
name|enabled
operator|=
literal|false
expr_stmt|;
block|}
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|watcher
argument_list|)
expr_stmt|;
block|}
DECL|interface|Visitor
specifier|public
specifier|static
interface|interface
name|Visitor
block|{
DECL|method|accept (HashMap<ShmId, RegisteredShm> segments, HashMultimap<ExtendedBlockId, Slot> slots)
name|boolean
name|accept
parameter_list|(
name|HashMap
argument_list|<
name|ShmId
argument_list|,
name|RegisteredShm
argument_list|>
name|segments
parameter_list|,
name|HashMultimap
argument_list|<
name|ExtendedBlockId
argument_list|,
name|Slot
argument_list|>
name|slots
parameter_list|)
function_decl|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|visit (Visitor visitor)
specifier|public
specifier|synchronized
name|boolean
name|visit
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|accept
argument_list|(
name|segments
argument_list|,
name|slots
argument_list|)
return|;
block|}
block|}
end_class

end_unit

