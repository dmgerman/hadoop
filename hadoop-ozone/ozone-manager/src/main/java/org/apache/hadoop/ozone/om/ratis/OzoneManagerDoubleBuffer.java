begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.ratis
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|ratis
package|;
end_package

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
name|Queue
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
name|ConcurrentLinkedQueue
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
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|OMMetadataManager
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
name|ozone
operator|.
name|om
operator|.
name|ratis
operator|.
name|helpers
operator|.
name|DoubleBufferEntry
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
name|ozone
operator|.
name|om
operator|.
name|response
operator|.
name|OMClientResponse
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
name|Daemon
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
name|db
operator|.
name|BatchOperation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|util
operator|.
name|ExitUtils
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

begin_comment
comment|/**  * This class implements DoubleBuffer implementation of OMClientResponse's. In  * DoubleBuffer it has 2 buffers one is currentBuffer and other is  * readyBuffer. The current OM requests will be always added to currentBuffer.  * Flush thread will be running in background, it check's if currentBuffer has  * any entries, it swaps the buffer and creates a batch and commit to DB.  * Adding OM request to doubleBuffer and swap of buffer are synchronized  * methods.  *  */
end_comment

begin_class
DECL|class|OzoneManagerDoubleBuffer
specifier|public
class|class
name|OzoneManagerDoubleBuffer
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
name|OzoneManagerDoubleBuffer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// Taken unbounded queue, if sync thread is taking too long time, we
comment|// might end up taking huge memory to add entries to the buffer.
comment|// TODO: We can avoid this using unbounded queue and use queue with
comment|// capacity, if queue is full we can wait for sync to be completed to
comment|// add entries. But in this also we might block rpc handlers, as we
comment|// clear entries after sync. Or we can come up with a good approach to
comment|// solve this.
DECL|field|currentBuffer
specifier|private
name|Queue
argument_list|<
name|DoubleBufferEntry
argument_list|<
name|OMClientResponse
argument_list|>
argument_list|>
name|currentBuffer
decl_stmt|;
DECL|field|readyBuffer
specifier|private
name|Queue
argument_list|<
name|DoubleBufferEntry
argument_list|<
name|OMClientResponse
argument_list|>
argument_list|>
name|readyBuffer
decl_stmt|;
DECL|field|daemon
specifier|private
name|Daemon
name|daemon
decl_stmt|;
DECL|field|omMetadataManager
specifier|private
specifier|final
name|OMMetadataManager
name|omMetadataManager
decl_stmt|;
DECL|field|flushedTransactionCount
specifier|private
specifier|final
name|AtomicLong
name|flushedTransactionCount
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|flushIterations
specifier|private
specifier|final
name|AtomicLong
name|flushIterations
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|isRunning
specifier|private
specifier|volatile
name|boolean
name|isRunning
decl_stmt|;
DECL|method|OzoneManagerDoubleBuffer (OMMetadataManager omMetadataManager)
specifier|public
name|OzoneManagerDoubleBuffer
parameter_list|(
name|OMMetadataManager
name|omMetadataManager
parameter_list|)
block|{
name|this
operator|.
name|currentBuffer
operator|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|readyBuffer
operator|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|omMetadataManager
operator|=
name|omMetadataManager
expr_stmt|;
name|isRunning
operator|=
literal|true
expr_stmt|;
comment|// Daemon thread which runs in back ground and flushes transactions to DB.
name|daemon
operator|=
operator|new
name|Daemon
argument_list|(
name|this
operator|::
name|flushTransactions
argument_list|)
expr_stmt|;
name|daemon
operator|.
name|setName
argument_list|(
literal|"OMDoubleBufferFlushThread"
argument_list|)
expr_stmt|;
name|daemon
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * Runs in a background thread and batches the transaction in currentBuffer    * and commit to DB.    */
DECL|method|flushTransactions ()
specifier|private
name|void
name|flushTransactions
parameter_list|()
block|{
while|while
condition|(
name|isRunning
condition|)
block|{
try|try
block|{
if|if
condition|(
name|canFlush
argument_list|()
condition|)
block|{
name|setReadyBuffer
argument_list|()
expr_stmt|;
specifier|final
name|BatchOperation
name|batchOperation
init|=
name|omMetadataManager
operator|.
name|getStore
argument_list|()
operator|.
name|initBatchOperation
argument_list|()
decl_stmt|;
name|readyBuffer
operator|.
name|iterator
argument_list|()
operator|.
name|forEachRemaining
argument_list|(
parameter_list|(
name|entry
parameter_list|)
lambda|->
block|{
try|try
block|{
name|entry
operator|.
name|getResponse
argument_list|()
operator|.
name|addToDBBatch
argument_list|(
name|omMetadataManager
argument_list|,
name|batchOperation
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// During Adding to RocksDB batch entry got an exception.
comment|// We should terminate the OM.
name|terminate
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|omMetadataManager
operator|.
name|getStore
argument_list|()
operator|.
name|commitBatchOperation
argument_list|(
name|batchOperation
argument_list|)
expr_stmt|;
name|int
name|flushedTransactionsSize
init|=
name|readyBuffer
operator|.
name|size
argument_list|()
decl_stmt|;
name|flushedTransactionCount
operator|.
name|addAndGet
argument_list|(
name|flushedTransactionsSize
argument_list|)
expr_stmt|;
name|flushIterations
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sync Iteration {} flushed transactions in this "
operator|+
literal|"iteration{}"
argument_list|,
name|flushIterations
operator|.
name|get
argument_list|()
argument_list|,
name|flushedTransactionsSize
argument_list|)
expr_stmt|;
name|readyBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// TODO: update the last updated index in OzoneManagerStateMachine.
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
if|if
condition|(
name|isRunning
condition|)
block|{
specifier|final
name|String
name|message
init|=
literal|"OMDoubleBuffer flush thread "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" encountered Interrupted "
operator|+
literal|"exception while running"
decl_stmt|;
name|ExitUtils
operator|.
name|terminate
argument_list|(
literal|1
argument_list|,
name|message
argument_list|,
name|ex
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"OMDoubleBuffer flush thread "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" is interrupted and will "
operator|+
literal|"exit. {}"
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|terminate
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
specifier|final
name|String
name|s
init|=
literal|"OMDoubleBuffer flush thread"
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"encountered Throwable error"
decl_stmt|;
name|ExitUtils
operator|.
name|terminate
argument_list|(
literal|2
argument_list|,
name|s
argument_list|,
name|t
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Stop OM DoubleBuffer flush thread.    */
DECL|method|stop ()
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|isRunning
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping OMDoubleBuffer flush thread"
argument_list|)
expr_stmt|;
name|isRunning
operator|=
literal|false
expr_stmt|;
name|daemon
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"OMDoubleBuffer flush thread is not running."
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|terminate (IOException ex)
specifier|private
name|void
name|terminate
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|String
name|message
init|=
literal|"During flush to DB encountered error in "
operator|+
literal|"OMDoubleBuffer flush thread "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|ExitUtils
operator|.
name|terminate
argument_list|(
literal|1
argument_list|,
name|message
argument_list|,
name|ex
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the flushed transaction count to OM DB.    * @return flushedTransactionCount    */
DECL|method|getFlushedTransactionCount ()
specifier|public
name|long
name|getFlushedTransactionCount
parameter_list|()
block|{
return|return
name|flushedTransactionCount
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Returns total number of flush iterations run by sync thread.    * @return flushIterations    */
DECL|method|getFlushIterations ()
specifier|public
name|long
name|getFlushIterations
parameter_list|()
block|{
return|return
name|flushIterations
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Add OmResponseBufferEntry to buffer.    * @param response    * @param transactionIndex    */
DECL|method|add (OMClientResponse response, long transactionIndex)
specifier|public
specifier|synchronized
name|void
name|add
parameter_list|(
name|OMClientResponse
name|response
parameter_list|,
name|long
name|transactionIndex
parameter_list|)
block|{
name|currentBuffer
operator|.
name|add
argument_list|(
operator|new
name|DoubleBufferEntry
argument_list|<>
argument_list|(
name|transactionIndex
argument_list|,
name|response
argument_list|)
argument_list|)
expr_stmt|;
name|notify
argument_list|()
expr_stmt|;
block|}
comment|/**    * Check can we flush transactions or not. This method wait's until    * currentBuffer size is greater than zero, once currentBuffer size is    * greater than zero it gets notify signal, and it returns true    * indicating that we are ready to flush.    *    * @return boolean    */
DECL|method|canFlush ()
specifier|private
specifier|synchronized
name|boolean
name|canFlush
parameter_list|()
throws|throws
name|InterruptedException
block|{
comment|// When transactions are added to buffer it notifies, then we check if
comment|// currentBuffer size once and return from this method.
while|while
condition|(
name|currentBuffer
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|wait
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Prepares the readyBuffer which is used by sync thread to flush    * transactions to OM DB. This method swaps the currentBuffer and readyBuffer.    */
DECL|method|setReadyBuffer ()
specifier|private
specifier|synchronized
name|void
name|setReadyBuffer
parameter_list|()
block|{
name|Queue
argument_list|<
name|DoubleBufferEntry
argument_list|<
name|OMClientResponse
argument_list|>
argument_list|>
name|temp
init|=
name|currentBuffer
decl_stmt|;
name|currentBuffer
operator|=
name|readyBuffer
expr_stmt|;
name|readyBuffer
operator|=
name|temp
expr_stmt|;
block|}
block|}
end_class

end_unit

