begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cblock.jscsiHelper.cache.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
operator|.
name|jscsiHelper
operator|.
name|cache
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
name|primitives
operator|.
name|Longs
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
name|codec
operator|.
name|digest
operator|.
name|DigestUtils
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
name|cblock
operator|.
name|jscsiHelper
operator|.
name|cache
operator|.
name|LogicalBlock
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
name|scm
operator|.
name|XceiverClientManager
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
name|LevelDBStore
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
name|io
operator|.
name|FileOutputStream
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
name|ByteBuffer
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
name|file
operator|.
name|Paths
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
name|Lock
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_CACHE_BLOCK_BUFFER_SIZE
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_CACHE_BLOCK_BUFFER_SIZE_DEFAULT
import|;
end_import

begin_comment
comment|/**  * A Queue that is used to write blocks asynchronously to the container.  */
end_comment

begin_class
DECL|class|AsyncBlockWriter
specifier|public
class|class
name|AsyncBlockWriter
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
name|AsyncBlockWriter
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Right now we have a single buffer and we block when we write it to    * the file.    */
DECL|field|blockIDBuffer
specifier|private
specifier|final
name|ByteBuffer
name|blockIDBuffer
decl_stmt|;
comment|/**    * XceiverClientManager is used to get client connections to a set of    * machines.    */
DECL|field|xceiverClientManager
specifier|private
specifier|final
name|XceiverClientManager
name|xceiverClientManager
decl_stmt|;
comment|/**    * This lock is used as a signal to re-queuing thread. The requeue thread    * wakes up as soon as it is signaled some blocks are in the retry queue.    * We try really aggressively since this new block will automatically move    * to the end of the queue.    *<p>    * In the event a container is unavailable for a long time, we can either    * fail all writes or remap and let the writes succeed. The easier    * semantics is to fail the volume until the container is recovered by SCM.    */
DECL|field|lock
specifier|private
specifier|final
name|Lock
name|lock
decl_stmt|;
DECL|field|notEmpty
specifier|private
specifier|final
name|Condition
name|notEmpty
decl_stmt|;
comment|/**    * The cache this writer is operating against.    */
DECL|field|parentCache
specifier|private
specifier|final
name|CBlockLocalCache
name|parentCache
decl_stmt|;
DECL|field|blockBufferSize
specifier|private
specifier|final
name|int
name|blockBufferSize
decl_stmt|;
DECL|field|DIRTY_LOG_PREFIX
specifier|private
specifier|final
specifier|static
name|String
name|DIRTY_LOG_PREFIX
init|=
literal|"DirtyLog"
decl_stmt|;
DECL|field|localIoCount
specifier|private
name|AtomicLong
name|localIoCount
decl_stmt|;
comment|/**    * Constructs an Async Block Writer.    *    * @param config - Config    * @param cache - Parent Cache for this writer    */
DECL|method|AsyncBlockWriter (Configuration config, CBlockLocalCache cache)
specifier|public
name|AsyncBlockWriter
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|CBlockLocalCache
name|cache
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|cache
argument_list|,
literal|"Cache cannot be null."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|cache
operator|.
name|getCacheDB
argument_list|()
argument_list|,
literal|"DB cannot be null."
argument_list|)
expr_stmt|;
name|localIoCount
operator|=
operator|new
name|AtomicLong
argument_list|()
expr_stmt|;
name|blockBufferSize
operator|=
name|config
operator|.
name|getInt
argument_list|(
name|DFS_CBLOCK_CACHE_BLOCK_BUFFER_SIZE
argument_list|,
name|DFS_CBLOCK_CACHE_BLOCK_BUFFER_SIZE_DEFAULT
argument_list|)
operator|*
literal|1024
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Cache: Block Size: {}"
argument_list|,
name|blockBufferSize
argument_list|)
expr_stmt|;
name|lock
operator|=
operator|new
name|ReentrantLock
argument_list|()
expr_stmt|;
name|notEmpty
operator|=
name|lock
operator|.
name|newCondition
argument_list|()
expr_stmt|;
name|parentCache
operator|=
name|cache
expr_stmt|;
name|xceiverClientManager
operator|=
name|cache
operator|.
name|getClientManager
argument_list|()
expr_stmt|;
name|blockIDBuffer
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|blockBufferSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return the log to write to.    *    * @return Logger.    */
DECL|method|getLOG ()
specifier|public
specifier|static
name|Logger
name|getLOG
parameter_list|()
block|{
return|return
name|LOG
return|;
block|}
comment|/**    * Get the CacheDB.    *    * @return LevelDB Handle    */
DECL|method|getCacheDB ()
name|LevelDBStore
name|getCacheDB
parameter_list|()
block|{
return|return
name|parentCache
operator|.
name|getCacheDB
argument_list|()
return|;
block|}
comment|/**    * Returns the client manager.    *    * @return XceiverClientManager    */
DECL|method|getXceiverClientManager ()
name|XceiverClientManager
name|getXceiverClientManager
parameter_list|()
block|{
return|return
name|xceiverClientManager
return|;
block|}
comment|/**    * Incs the localIoPacket Count that has gone into this device.    */
DECL|method|incrementLocalIO ()
specifier|public
name|long
name|incrementLocalIO
parameter_list|()
block|{
return|return
name|localIoCount
operator|.
name|incrementAndGet
argument_list|()
return|;
block|}
comment|/**    * Return the local io counts to this device.    * @return the count of io    */
DECL|method|getLocalIOCount ()
specifier|public
name|long
name|getLocalIOCount
parameter_list|()
block|{
return|return
name|localIoCount
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Writes a block to LevelDB store and queues a work item for the system to    * sync the block to containers.    *    * @param block - Logical Block    */
DECL|method|writeBlock (LogicalBlock block)
specifier|public
name|void
name|writeBlock
parameter_list|(
name|LogicalBlock
name|block
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|keybuf
init|=
name|Longs
operator|.
name|toByteArray
argument_list|(
name|block
operator|.
name|getBlockID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentCache
operator|.
name|isShortCircuitIOEnabled
argument_list|()
condition|)
block|{
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|getCacheDB
argument_list|()
operator|.
name|put
argument_list|(
name|keybuf
argument_list|,
name|block
operator|.
name|getData
argument_list|()
operator|.
name|array
argument_list|()
argument_list|)
expr_stmt|;
name|incrementLocalIO
argument_list|()
expr_stmt|;
name|long
name|endTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|parentCache
operator|.
name|getTargetMetrics
argument_list|()
operator|.
name|updateDBWriteLatency
argument_list|(
name|endTime
operator|-
name|startTime
argument_list|)
expr_stmt|;
if|if
condition|(
name|parentCache
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|String
name|datahash
init|=
name|DigestUtils
operator|.
name|sha256Hex
argument_list|(
name|block
operator|.
name|getData
argument_list|()
operator|.
name|array
argument_list|()
argument_list|)
decl_stmt|;
name|parentCache
operator|.
name|getTracer
argument_list|()
operator|.
name|info
argument_list|(
literal|"Task=WriterTaskDBPut,BlockID={},Time={},SHA={}"
argument_list|,
name|block
operator|.
name|getBlockID
argument_list|()
argument_list|,
name|endTime
operator|-
name|startTime
argument_list|,
name|datahash
argument_list|)
expr_stmt|;
block|}
name|block
operator|.
name|clearData
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// TODO : Support Direct I/O
name|LOG
operator|.
name|error
argument_list|(
literal|"Non-Cache I/O is not supported at this point of time."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cache is required and cannot be "
operator|+
literal|"disabled now."
argument_list|)
throw|;
block|}
if|if
condition|(
name|blockIDBuffer
operator|.
name|remaining
argument_list|()
operator|<=
operator|(
name|Long
operator|.
name|SIZE
operator|/
name|Byte
operator|.
name|SIZE
operator|)
condition|)
block|{
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|blockIDBuffer
operator|.
name|flip
argument_list|()
expr_stmt|;
name|writeBlockBufferToFile
argument_list|(
name|blockIDBuffer
argument_list|)
expr_stmt|;
name|blockIDBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|long
name|endTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentCache
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|parentCache
operator|.
name|getTracer
argument_list|()
operator|.
name|info
argument_list|(
literal|"Task=DirtyBlockLogWrite,Time={}"
argument_list|,
name|endTime
operator|-
name|startTime
argument_list|)
expr_stmt|;
block|}
block|}
name|blockIDBuffer
operator|.
name|putLong
argument_list|(
name|block
operator|.
name|getBlockID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Write Block Buffer to file.    *    * @param blockID - ByteBuffer    * @throws IOException    */
DECL|method|writeBlockBufferToFile (ByteBuffer blockID)
specifier|private
name|void
name|writeBlockBufferToFile
parameter_list|(
name|ByteBuffer
name|blockID
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|append
init|=
literal|false
decl_stmt|;
name|String
name|fileName
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s.%s"
argument_list|,
name|DIRTY_LOG_PREFIX
argument_list|,
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|logDir
init|=
operator|new
name|File
argument_list|(
name|parentCache
operator|.
name|getDbPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|logDir
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|logDir
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to create the log directory, Critical error cannot "
operator|+
literal|"continue. Log Dir : {}"
argument_list|,
name|logDir
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cache Directory create failed, Cannot "
operator|+
literal|"continue. Log Dir: {}"
operator|+
name|logDir
argument_list|)
throw|;
block|}
name|String
name|log
init|=
name|Paths
operator|.
name|get
argument_list|(
name|parentCache
operator|.
name|getDbPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|fileName
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
init|(
name|FileChannel
name|channel
init|=
operator|new
name|FileOutputStream
argument_list|(
name|log
argument_list|,
name|append
argument_list|)
operator|.
name|getChannel
argument_list|()
init|)
block|{
name|channel
operator|.
name|write
argument_list|(
name|blockID
argument_list|)
expr_stmt|;
block|}
name|blockID
operator|.
name|clear
argument_list|()
expr_stmt|;
name|parentCache
operator|.
name|processDirtyMessage
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Shutdown by writing any pending I/O to dirtylog buffer.    */
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
try|try
block|{
name|writeBlockBufferToFile
argument_list|(
name|this
operator|.
name|blockIDBuffer
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
name|error
argument_list|(
literal|"Unable to sync the Block map to disk -- This might cause a "
operator|+
literal|"data loss or corruption"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns tracer.    *    * @return Tracer    */
DECL|method|getTracer ()
name|Logger
name|getTracer
parameter_list|()
block|{
return|return
name|parentCache
operator|.
name|getTracer
argument_list|()
return|;
block|}
block|}
end_class

end_unit

