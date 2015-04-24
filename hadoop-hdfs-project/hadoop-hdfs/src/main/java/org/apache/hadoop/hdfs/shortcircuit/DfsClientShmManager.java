begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.shortcircuit
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|shortcircuit
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
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
name|DataOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|TreeMap
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
name|Condition
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
name|ReentrantLock
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
name|mutable
operator|.
name|MutableBoolean
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
name|net
operator|.
name|DomainPeer
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
name|DatanodeInfo
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
name|datatransfer
operator|.
name|DataTransferProtocol
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
name|datatransfer
operator|.
name|Sender
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
name|proto
operator|.
name|DataTransferProtos
operator|.
name|ShortCircuitShmResponseProto
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
name|protocolPB
operator|.
name|PBHelper
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
name|ShortCircuitRegistry
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
comment|/**  * Manages short-circuit memory segments for an HDFS client.  *   * Clients are responsible for requesting and releasing shared memory segments used  * for communicating with the DataNode. The client will try to allocate new slots  * in the set of existing segments, falling back to getting a new segment from the  * DataNode via {@link DataTransferProtocol#requestShortCircuitFds}.  *   * The counterpart to this class on the DataNode is {@link ShortCircuitRegistry}.  * See {@link ShortCircuitRegistry} for more information on the communication protocol.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DfsClientShmManager
specifier|public
class|class
name|DfsClientShmManager
implements|implements
name|Closeable
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
name|DfsClientShmManager
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Manages short-circuit memory segments that pertain to a given DataNode.    */
DECL|class|EndpointShmManager
class|class
name|EndpointShmManager
block|{
comment|/**      * The datanode we're managing.      */
DECL|field|datanode
specifier|private
specifier|final
name|DatanodeInfo
name|datanode
decl_stmt|;
comment|/**      * Shared memory segments which have no empty slots.      *      * Protected by the manager lock.      */
DECL|field|full
specifier|private
specifier|final
name|TreeMap
argument_list|<
name|ShmId
argument_list|,
name|DfsClientShm
argument_list|>
name|full
init|=
operator|new
name|TreeMap
argument_list|<
name|ShmId
argument_list|,
name|DfsClientShm
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Shared memory segments which have at least one empty slot.      *      * Protected by the manager lock.      */
DECL|field|notFull
specifier|private
specifier|final
name|TreeMap
argument_list|<
name|ShmId
argument_list|,
name|DfsClientShm
argument_list|>
name|notFull
init|=
operator|new
name|TreeMap
argument_list|<
name|ShmId
argument_list|,
name|DfsClientShm
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * True if this datanode doesn't support short-circuit shared memory      * segments.      *      * Protected by the manager lock.      */
DECL|field|disabled
specifier|private
name|boolean
name|disabled
init|=
literal|false
decl_stmt|;
comment|/**      * True if we're in the process of loading a shared memory segment from      * this DataNode.      *      * Protected by the manager lock.      */
DECL|field|loading
specifier|private
name|boolean
name|loading
init|=
literal|false
decl_stmt|;
DECL|method|EndpointShmManager (DatanodeInfo datanode)
name|EndpointShmManager
parameter_list|(
name|DatanodeInfo
name|datanode
parameter_list|)
block|{
name|this
operator|.
name|datanode
operator|=
name|datanode
expr_stmt|;
block|}
comment|/**      * Pull a slot out of a preexisting shared memory segment.      *      * Must be called with the manager lock held.      *      * @param blockId     The blockId to put inside the Slot object.      *      * @return            null if none of our shared memory segments contain a      *                      free slot; the slot object otherwise.      */
DECL|method|allocSlotFromExistingShm (ExtendedBlockId blockId)
specifier|private
name|Slot
name|allocSlotFromExistingShm
parameter_list|(
name|ExtendedBlockId
name|blockId
parameter_list|)
block|{
if|if
condition|(
name|notFull
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Entry
argument_list|<
name|ShmId
argument_list|,
name|DfsClientShm
argument_list|>
name|entry
init|=
name|notFull
operator|.
name|firstEntry
argument_list|()
decl_stmt|;
name|DfsClientShm
name|shm
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|ShmId
name|shmId
init|=
name|shm
operator|.
name|getShmId
argument_list|()
decl_stmt|;
name|Slot
name|slot
init|=
name|shm
operator|.
name|allocAndRegisterSlot
argument_list|(
name|blockId
argument_list|)
decl_stmt|;
if|if
condition|(
name|shm
operator|.
name|isFull
argument_list|()
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
literal|": pulled the last slot "
operator|+
name|slot
operator|.
name|getSlotIdx
argument_list|()
operator|+
literal|" out of "
operator|+
name|shm
argument_list|)
expr_stmt|;
block|}
name|DfsClientShm
name|removedShm
init|=
name|notFull
operator|.
name|remove
argument_list|(
name|shmId
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|removedShm
operator|==
name|shm
argument_list|)
expr_stmt|;
name|full
operator|.
name|put
argument_list|(
name|shmId
argument_list|,
name|shm
argument_list|)
expr_stmt|;
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
literal|": pulled slot "
operator|+
name|slot
operator|.
name|getSlotIdx
argument_list|()
operator|+
literal|" out of "
operator|+
name|shm
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|slot
return|;
block|}
comment|/**      * Ask the DataNode for a new shared memory segment.  This function must be      * called with the manager lock held.  We will release the lock while      * communicating with the DataNode.      *      * @param clientName    The current client name.      * @param peer          The peer to use to talk to the DataNode.      *      * @return              Null if the DataNode does not support shared memory      *                        segments, or experienced an error creating the      *                        shm.  The shared memory segment itself on success.      * @throws IOException  If there was an error communicating over the socket.      *                        We will not throw an IOException unless the socket      *                        itself (or the network) is the problem.      */
DECL|method|requestNewShm (String clientName, DomainPeer peer)
specifier|private
name|DfsClientShm
name|requestNewShm
parameter_list|(
name|String
name|clientName
parameter_list|,
name|DomainPeer
name|peer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DataOutputStream
name|out
init|=
operator|new
name|DataOutputStream
argument_list|(
operator|new
name|BufferedOutputStream
argument_list|(
name|peer
operator|.
name|getOutputStream
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
operator|new
name|Sender
argument_list|(
name|out
argument_list|)
operator|.
name|requestShortCircuitShm
argument_list|(
name|clientName
argument_list|)
expr_stmt|;
name|ShortCircuitShmResponseProto
name|resp
init|=
name|ShortCircuitShmResponseProto
operator|.
name|parseFrom
argument_list|(
name|PBHelper
operator|.
name|vintPrefixed
argument_list|(
name|peer
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|error
init|=
name|resp
operator|.
name|hasError
argument_list|()
condition|?
name|resp
operator|.
name|getError
argument_list|()
else|:
literal|"(unknown)"
decl_stmt|;
switch|switch
condition|(
name|resp
operator|.
name|getStatus
argument_list|()
condition|)
block|{
case|case
name|SUCCESS
case|:
name|DomainSocket
name|sock
init|=
name|peer
operator|.
name|getDomainSocket
argument_list|()
decl_stmt|;
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
name|FileInputStream
name|fis
index|[]
init|=
operator|new
name|FileInputStream
index|[
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|sock
operator|.
name|recvFileInputStreams
argument_list|(
name|fis
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"got EOF while trying to transfer the "
operator|+
literal|"file descriptor for the shared memory segment."
argument_list|)
throw|;
block|}
if|if
condition|(
name|fis
index|[
literal|0
index|]
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"the datanode "
operator|+
name|datanode
operator|+
literal|" failed to "
operator|+
literal|"pass a file descriptor for the shared memory segment."
argument_list|)
throw|;
block|}
try|try
block|{
name|DfsClientShm
name|shm
init|=
operator|new
name|DfsClientShm
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|resp
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|fis
index|[
literal|0
index|]
argument_list|,
name|this
argument_list|,
name|peer
argument_list|)
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
literal|": createNewShm: created "
operator|+
name|shm
argument_list|)
expr_stmt|;
block|}
return|return
name|shm
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|fis
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
case|case
name|ERROR_UNSUPPORTED
case|:
comment|// The DataNode just does not support short-circuit shared memory
comment|// access, and we should stop asking.
name|LOG
operator|.
name|info
argument_list|(
name|this
operator|+
literal|": datanode does not support short-circuit "
operator|+
literal|"shared memory access: "
operator|+
name|error
argument_list|)
expr_stmt|;
name|disabled
operator|=
literal|true
expr_stmt|;
return|return
literal|null
return|;
default|default:
comment|// The datanode experienced some kind of unexpected error when trying to
comment|// create the short-circuit shared memory segment.
name|LOG
operator|.
name|warn
argument_list|(
name|this
operator|+
literal|": error requesting short-circuit shared memory "
operator|+
literal|"access: "
operator|+
name|error
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Allocate a new shared memory slot connected to this datanode.      *      * Must be called with the EndpointShmManager lock held.      *      * @param peer          The peer to use to talk to the DataNode.      * @param usedPeer      (out param) Will be set to true if we used the peer.      *                        When a peer is used      *      * @param clientName    The client name.      * @param blockId       The block ID to use.      * @return              null if the DataNode does not support shared memory      *                        segments, or experienced an error creating the      *                        shm.  The shared memory segment itself on success.      * @throws IOException  If there was an error communicating over the socket.      */
DECL|method|allocSlot (DomainPeer peer, MutableBoolean usedPeer, String clientName, ExtendedBlockId blockId)
name|Slot
name|allocSlot
parameter_list|(
name|DomainPeer
name|peer
parameter_list|,
name|MutableBoolean
name|usedPeer
parameter_list|,
name|String
name|clientName
parameter_list|,
name|ExtendedBlockId
name|blockId
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|closed
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
literal|": the DfsClientShmManager has been closed."
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
if|if
condition|(
name|disabled
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
literal|": shared memory segment access is disabled."
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|// Try to use an existing slot.
name|Slot
name|slot
init|=
name|allocSlotFromExistingShm
argument_list|(
name|blockId
argument_list|)
decl_stmt|;
if|if
condition|(
name|slot
operator|!=
literal|null
condition|)
block|{
return|return
name|slot
return|;
block|}
comment|// There are no free slots.  If someone is loading more slots, wait
comment|// for that to finish.
if|if
condition|(
name|loading
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
literal|": waiting for loading to finish..."
argument_list|)
expr_stmt|;
block|}
name|finishedLoading
operator|.
name|awaitUninterruptibly
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Otherwise, load the slot ourselves.
name|loading
operator|=
literal|true
expr_stmt|;
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
name|DfsClientShm
name|shm
decl_stmt|;
try|try
block|{
name|shm
operator|=
name|requestNewShm
argument_list|(
name|clientName
argument_list|,
name|peer
argument_list|)
expr_stmt|;
if|if
condition|(
name|shm
operator|==
literal|null
condition|)
continue|continue;
comment|// See #{DfsClientShmManager#domainSocketWatcher} for details
comment|// about why we do this before retaking the manager lock.
name|domainSocketWatcher
operator|.
name|add
argument_list|(
name|peer
operator|.
name|getDomainSocket
argument_list|()
argument_list|,
name|shm
argument_list|)
expr_stmt|;
comment|// The DomainPeer is now our responsibility, and should not be
comment|// closed by the caller.
name|usedPeer
operator|.
name|setValue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|loading
operator|=
literal|false
expr_stmt|;
name|finishedLoading
operator|.
name|signalAll
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|shm
operator|.
name|isDisconnected
argument_list|()
condition|)
block|{
comment|// If the peer closed immediately after the shared memory segment
comment|// was created, the DomainSocketWatcher callback might already have
comment|// fired and marked the shm as disconnected.  In this case, we
comment|// obviously don't want to add the SharedMemorySegment to our list
comment|// of valid not-full segments.
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
name|this
operator|+
literal|": the UNIX domain socket associated with "
operator|+
literal|"this short-circuit memory closed before we could make "
operator|+
literal|"use of the shm."
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|notFull
operator|.
name|put
argument_list|(
name|shm
operator|.
name|getShmId
argument_list|()
argument_list|,
name|shm
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Stop tracking a slot.      *      * Must be called with the EndpointShmManager lock held.      *      * @param slot          The slot to release.      */
DECL|method|freeSlot (Slot slot)
name|void
name|freeSlot
parameter_list|(
name|Slot
name|slot
parameter_list|)
block|{
name|DfsClientShm
name|shm
init|=
operator|(
name|DfsClientShm
operator|)
name|slot
operator|.
name|getShm
argument_list|()
decl_stmt|;
name|shm
operator|.
name|unregisterSlot
argument_list|(
name|slot
operator|.
name|getSlotIdx
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|shm
operator|.
name|isDisconnected
argument_list|()
condition|)
block|{
comment|// Stale shared memory segments should not be tracked here.
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|full
operator|.
name|containsKey
argument_list|(
name|shm
operator|.
name|getShmId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|notFull
operator|.
name|containsKey
argument_list|(
name|shm
operator|.
name|getShmId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|shm
operator|.
name|isEmpty
argument_list|()
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
literal|": freeing empty stale "
operator|+
name|shm
argument_list|)
expr_stmt|;
block|}
name|shm
operator|.
name|free
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|ShmId
name|shmId
init|=
name|shm
operator|.
name|getShmId
argument_list|()
decl_stmt|;
name|full
operator|.
name|remove
argument_list|(
name|shmId
argument_list|)
expr_stmt|;
comment|// The shm can't be full if we just freed a slot.
if|if
condition|(
name|shm
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|notFull
operator|.
name|remove
argument_list|(
name|shmId
argument_list|)
expr_stmt|;
comment|// If the shared memory segment is now empty, we call shutdown(2) on
comment|// the UNIX domain socket associated with it.  The DomainSocketWatcher,
comment|// which is watching this socket, will call DfsClientShm#handle,
comment|// cleaning up this shared memory segment.
comment|//
comment|// See #{DfsClientShmManager#domainSocketWatcher} for details about why
comment|// we don't want to call DomainSocketWatcher#remove directly here.
comment|//
comment|// Note that we could experience 'fragmentation' here, where the
comment|// DFSClient allocates a bunch of slots in different shared memory
comment|// segments, and then frees most of them, but never fully empties out
comment|// any segment.  We make some attempt to avoid this fragmentation by
comment|// always allocating new slots out of the shared memory segment with the
comment|// lowest ID, but it could still occur.  In most workloads,
comment|// fragmentation should not be a major concern, since it doesn't impact
comment|// peak file descriptor usage or the speed of allocation.
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
literal|": shutting down UNIX domain socket for "
operator|+
literal|"empty "
operator|+
name|shm
argument_list|)
expr_stmt|;
block|}
name|shutdown
argument_list|(
name|shm
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|notFull
operator|.
name|put
argument_list|(
name|shmId
argument_list|,
name|shm
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Unregister a shared memory segment.      *      * Once a segment is unregistered, we will not allocate any more slots      * inside that segment.      *      * The DomainSocketWatcher calls this while holding the DomainSocketWatcher      * lock.      *      * @param shmId         The ID of the shared memory segment to unregister.      */
DECL|method|unregisterShm (ShmId shmId)
name|void
name|unregisterShm
parameter_list|(
name|ShmId
name|shmId
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|full
operator|.
name|remove
argument_list|(
name|shmId
argument_list|)
expr_stmt|;
name|notFull
operator|.
name|remove
argument_list|(
name|shmId
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
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
name|String
operator|.
name|format
argument_list|(
literal|"EndpointShmManager(%s, parent=%s)"
argument_list|,
name|datanode
argument_list|,
name|DfsClientShmManager
operator|.
name|this
argument_list|)
return|;
block|}
DECL|method|getVisitorInfo ()
name|PerDatanodeVisitorInfo
name|getVisitorInfo
parameter_list|()
block|{
return|return
operator|new
name|PerDatanodeVisitorInfo
argument_list|(
name|full
argument_list|,
name|notFull
argument_list|,
name|disabled
argument_list|)
return|;
block|}
DECL|method|shutdown (DfsClientShm shm)
specifier|final
name|void
name|shutdown
parameter_list|(
name|DfsClientShm
name|shm
parameter_list|)
block|{
try|try
block|{
name|shm
operator|.
name|getPeer
argument_list|()
operator|.
name|getDomainSocket
argument_list|()
operator|.
name|shutdown
argument_list|()
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
name|this
operator|+
literal|": error shutting down shm: got IOException calling "
operator|+
literal|"shutdown(SHUT_RDWR)"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|ReentrantLock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
comment|/**    * A condition variable which is signalled when we finish loading a segment    * from the Datanode.    */
DECL|field|finishedLoading
specifier|private
specifier|final
name|Condition
name|finishedLoading
init|=
name|lock
operator|.
name|newCondition
argument_list|()
decl_stmt|;
comment|/**    * Information about each Datanode.    */
DECL|field|datanodes
specifier|private
specifier|final
name|HashMap
argument_list|<
name|DatanodeInfo
argument_list|,
name|EndpointShmManager
argument_list|>
name|datanodes
init|=
operator|new
name|HashMap
argument_list|<
name|DatanodeInfo
argument_list|,
name|EndpointShmManager
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|/**    * The DomainSocketWatcher which keeps track of the UNIX domain socket    * associated with each shared memory segment.    *    * Note: because the DomainSocketWatcher makes callbacks into this    * DfsClientShmManager object, you must MUST NOT attempt to take the    * DomainSocketWatcher lock while holding the DfsClientShmManager lock,    * or else deadlock might result.   This means that most DomainSocketWatcher    * methods are off-limits unless you release the manager lock first.    */
DECL|field|domainSocketWatcher
specifier|private
specifier|final
name|DomainSocketWatcher
name|domainSocketWatcher
decl_stmt|;
DECL|method|DfsClientShmManager (int interruptCheckPeriodMs)
name|DfsClientShmManager
parameter_list|(
name|int
name|interruptCheckPeriodMs
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|domainSocketWatcher
operator|=
operator|new
name|DomainSocketWatcher
argument_list|(
name|interruptCheckPeriodMs
argument_list|,
literal|"client"
argument_list|)
expr_stmt|;
block|}
DECL|method|allocSlot (DatanodeInfo datanode, DomainPeer peer, MutableBoolean usedPeer, ExtendedBlockId blockId, String clientName)
specifier|public
name|Slot
name|allocSlot
parameter_list|(
name|DatanodeInfo
name|datanode
parameter_list|,
name|DomainPeer
name|peer
parameter_list|,
name|MutableBoolean
name|usedPeer
parameter_list|,
name|ExtendedBlockId
name|blockId
parameter_list|,
name|String
name|clientName
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|closed
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|": the DfsClientShmManager isclosed."
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|EndpointShmManager
name|shmManager
init|=
name|datanodes
operator|.
name|get
argument_list|(
name|datanode
argument_list|)
decl_stmt|;
if|if
condition|(
name|shmManager
operator|==
literal|null
condition|)
block|{
name|shmManager
operator|=
operator|new
name|EndpointShmManager
argument_list|(
name|datanode
argument_list|)
expr_stmt|;
name|datanodes
operator|.
name|put
argument_list|(
name|datanode
argument_list|,
name|shmManager
argument_list|)
expr_stmt|;
block|}
return|return
name|shmManager
operator|.
name|allocSlot
argument_list|(
name|peer
argument_list|,
name|usedPeer
argument_list|,
name|clientName
argument_list|,
name|blockId
argument_list|)
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|freeSlot (Slot slot)
specifier|public
name|void
name|freeSlot
parameter_list|(
name|Slot
name|slot
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|DfsClientShm
name|shm
init|=
operator|(
name|DfsClientShm
operator|)
name|slot
operator|.
name|getShm
argument_list|()
decl_stmt|;
name|shm
operator|.
name|getEndpointShmManager
argument_list|()
operator|.
name|freeSlot
argument_list|(
name|slot
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|class|PerDatanodeVisitorInfo
specifier|public
specifier|static
class|class
name|PerDatanodeVisitorInfo
block|{
DECL|field|full
specifier|public
specifier|final
name|TreeMap
argument_list|<
name|ShmId
argument_list|,
name|DfsClientShm
argument_list|>
name|full
decl_stmt|;
DECL|field|notFull
specifier|public
specifier|final
name|TreeMap
argument_list|<
name|ShmId
argument_list|,
name|DfsClientShm
argument_list|>
name|notFull
decl_stmt|;
DECL|field|disabled
specifier|public
specifier|final
name|boolean
name|disabled
decl_stmt|;
DECL|method|PerDatanodeVisitorInfo (TreeMap<ShmId, DfsClientShm> full, TreeMap<ShmId, DfsClientShm> notFull, boolean disabled)
name|PerDatanodeVisitorInfo
parameter_list|(
name|TreeMap
argument_list|<
name|ShmId
argument_list|,
name|DfsClientShm
argument_list|>
name|full
parameter_list|,
name|TreeMap
argument_list|<
name|ShmId
argument_list|,
name|DfsClientShm
argument_list|>
name|notFull
parameter_list|,
name|boolean
name|disabled
parameter_list|)
block|{
name|this
operator|.
name|full
operator|=
name|full
expr_stmt|;
name|this
operator|.
name|notFull
operator|=
name|notFull
expr_stmt|;
name|this
operator|.
name|disabled
operator|=
name|disabled
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|interface|Visitor
specifier|public
interface|interface
name|Visitor
block|{
DECL|method|visit (HashMap<DatanodeInfo, PerDatanodeVisitorInfo> info)
name|void
name|visit
parameter_list|(
name|HashMap
argument_list|<
name|DatanodeInfo
argument_list|,
name|PerDatanodeVisitorInfo
argument_list|>
name|info
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|visit (Visitor visitor)
specifier|public
name|void
name|visit
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|HashMap
argument_list|<
name|DatanodeInfo
argument_list|,
name|PerDatanodeVisitorInfo
argument_list|>
name|info
init|=
operator|new
name|HashMap
argument_list|<
name|DatanodeInfo
argument_list|,
name|PerDatanodeVisitorInfo
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|DatanodeInfo
argument_list|,
name|EndpointShmManager
argument_list|>
name|entry
range|:
name|datanodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|info
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getVisitorInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|visitor
operator|.
name|visit
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Close the DfsClientShmManager.    */
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
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|closed
condition|)
return|return;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
comment|// When closed, the domainSocketWatcher will issue callbacks that mark
comment|// all the outstanding DfsClientShm segments as stale.
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|domainSocketWatcher
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
name|String
operator|.
name|format
argument_list|(
literal|"ShortCircuitShmManager(%08x)"
argument_list|,
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getDomainSocketWatcher ()
specifier|public
name|DomainSocketWatcher
name|getDomainSocketWatcher
parameter_list|()
block|{
return|return
name|domainSocketWatcher
return|;
block|}
block|}
end_class

end_unit

