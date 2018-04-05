begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cblock.jscsiHelper
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
name|cblock
operator|.
name|jscsiHelper
operator|.
name|cache
operator|.
name|impl
operator|.
name|AsyncBlockWriter
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
name|hdds
operator|.
name|scm
operator|.
name|XceiverClientSpi
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
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
name|hdds
operator|.
name|scm
operator|.
name|storage
operator|.
name|ContainerProtocolCalls
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

begin_comment
comment|/**  * The blockWriter task.  */
end_comment

begin_class
DECL|class|BlockWriterTask
specifier|public
class|class
name|BlockWriterTask
implements|implements
name|Runnable
block|{
DECL|field|block
specifier|private
specifier|final
name|LogicalBlock
name|block
decl_stmt|;
DECL|field|tryCount
specifier|private
name|int
name|tryCount
decl_stmt|;
DECL|field|flusher
specifier|private
specifier|final
name|ContainerCacheFlusher
name|flusher
decl_stmt|;
DECL|field|dbPath
specifier|private
specifier|final
name|String
name|dbPath
decl_stmt|;
DECL|field|fileName
specifier|private
specifier|final
name|String
name|fileName
decl_stmt|;
DECL|field|maxRetryCount
specifier|private
specifier|final
name|int
name|maxRetryCount
decl_stmt|;
comment|/**    * Constructs a BlockWriterTask.    *    * @param block - Block Information.    * @param flusher - ContainerCacheFlusher.    */
DECL|method|BlockWriterTask (LogicalBlock block, ContainerCacheFlusher flusher, String dbPath, int tryCount, String fileName, int maxRetryCount)
specifier|public
name|BlockWriterTask
parameter_list|(
name|LogicalBlock
name|block
parameter_list|,
name|ContainerCacheFlusher
name|flusher
parameter_list|,
name|String
name|dbPath
parameter_list|,
name|int
name|tryCount
parameter_list|,
name|String
name|fileName
parameter_list|,
name|int
name|maxRetryCount
parameter_list|)
block|{
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
name|this
operator|.
name|flusher
operator|=
name|flusher
expr_stmt|;
name|this
operator|.
name|dbPath
operator|=
name|dbPath
expr_stmt|;
name|this
operator|.
name|tryCount
operator|=
name|tryCount
expr_stmt|;
name|this
operator|.
name|fileName
operator|=
name|fileName
expr_stmt|;
name|this
operator|.
name|maxRetryCount
operator|=
name|maxRetryCount
expr_stmt|;
block|}
comment|/**    * When an object implementing interface<code>Runnable</code> is used    * to create a thread, starting the thread causes the object's    *<code>run</code> method to be called in that separately executing    * thread.    *<p>    * The general contract of the method<code>run</code> is that it may    * take any action whatsoever.    *    * @see Thread#run()    */
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|String
name|containerName
init|=
literal|null
decl_stmt|;
name|XceiverClientSpi
name|client
init|=
literal|null
decl_stmt|;
name|LevelDBStore
name|levelDBStore
init|=
literal|null
decl_stmt|;
name|String
name|traceID
init|=
name|flusher
operator|.
name|getTraceID
argument_list|(
operator|new
name|File
argument_list|(
name|dbPath
argument_list|)
argument_list|,
name|block
operator|.
name|getBlockID
argument_list|()
argument_list|)
decl_stmt|;
name|flusher
operator|.
name|getLOG
argument_list|()
operator|.
name|debug
argument_list|(
literal|"Writing block to remote. block ID: {}"
argument_list|,
name|block
operator|.
name|getBlockID
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|incTryCount
argument_list|()
expr_stmt|;
name|Pipeline
name|pipeline
init|=
name|flusher
operator|.
name|getPipeline
argument_list|(
name|this
operator|.
name|dbPath
argument_list|,
name|block
operator|.
name|getBlockID
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|=
name|flusher
operator|.
name|getXceiverClientManager
argument_list|()
operator|.
name|acquireClient
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
name|containerName
operator|=
name|pipeline
operator|.
name|getContainerName
argument_list|()
expr_stmt|;
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
name|byte
index|[]
name|data
decl_stmt|;
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|levelDBStore
operator|=
name|flusher
operator|.
name|getCacheDB
argument_list|(
name|this
operator|.
name|dbPath
argument_list|)
expr_stmt|;
name|data
operator|=
name|levelDBStore
operator|.
name|get
argument_list|(
name|keybuf
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|long
name|endTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|data
operator|.
name|length
operator|>
literal|0
argument_list|,
literal|"Block data is zero length"
argument_list|)
expr_stmt|;
name|startTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
name|ContainerProtocolCalls
operator|.
name|writeSmallFile
argument_list|(
name|client
argument_list|,
name|containerName
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|block
operator|.
name|getBlockID
argument_list|()
argument_list|)
argument_list|,
name|data
argument_list|,
name|traceID
argument_list|)
expr_stmt|;
name|endTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
name|flusher
operator|.
name|getTargetMetrics
argument_list|()
operator|.
name|updateContainerWriteLatency
argument_list|(
name|endTime
operator|-
name|startTime
argument_list|)
expr_stmt|;
name|flusher
operator|.
name|getLOG
argument_list|()
operator|.
name|debug
argument_list|(
literal|"Time taken for Write Small File : {} ms"
argument_list|,
name|endTime
operator|-
name|startTime
argument_list|)
expr_stmt|;
name|flusher
operator|.
name|incrementRemoteIO
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|flusher
operator|.
name|getLOG
argument_list|()
operator|.
name|error
argument_list|(
literal|"Writing of block:{} failed, We have attempted "
operator|+
literal|"to write this block {} times to the container {}.Trace ID:{}"
argument_list|,
name|block
operator|.
name|getBlockID
argument_list|()
argument_list|,
name|this
operator|.
name|getTryCount
argument_list|()
argument_list|,
name|containerName
argument_list|,
name|traceID
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|writeRetryBlock
argument_list|(
name|block
argument_list|)
expr_stmt|;
if|if
condition|(
name|ex
operator|instanceof
name|IOException
condition|)
block|{
name|flusher
operator|.
name|getTargetMetrics
argument_list|()
operator|.
name|incNumWriteIOExceptionRetryBlocks
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|flusher
operator|.
name|getTargetMetrics
argument_list|()
operator|.
name|incNumWriteGenericExceptionRetryBlocks
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|getTryCount
argument_list|()
operator|>=
name|maxRetryCount
condition|)
block|{
name|flusher
operator|.
name|getTargetMetrics
argument_list|()
operator|.
name|incNumWriteMaxRetryBlocks
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|flusher
operator|.
name|incFinishCount
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
if|if
condition|(
name|levelDBStore
operator|!=
literal|null
condition|)
block|{
name|flusher
operator|.
name|releaseCacheDB
argument_list|(
name|dbPath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|flusher
operator|.
name|getXceiverClientManager
argument_list|()
operator|.
name|releaseClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|writeRetryBlock (LogicalBlock currentBlock)
specifier|private
name|void
name|writeRetryBlock
parameter_list|(
name|LogicalBlock
name|currentBlock
parameter_list|)
block|{
name|boolean
name|append
init|=
literal|false
decl_stmt|;
name|String
name|retryFileName
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s.%d.%s.%s"
argument_list|,
name|AsyncBlockWriter
operator|.
name|RETRY_LOG_PREFIX
argument_list|,
name|currentBlock
operator|.
name|getBlockID
argument_list|()
argument_list|,
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|,
name|tryCount
argument_list|)
decl_stmt|;
name|File
name|logDir
init|=
operator|new
name|File
argument_list|(
name|this
operator|.
name|dbPath
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
name|flusher
operator|.
name|getLOG
argument_list|()
operator|.
name|error
argument_list|(
literal|"Unable to create the log directory, Critical error cannot continue"
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|log
init|=
name|Paths
operator|.
name|get
argument_list|(
name|this
operator|.
name|dbPath
argument_list|,
name|retryFileName
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|Long
operator|.
name|SIZE
operator|/
name|Byte
operator|.
name|SIZE
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|putLong
argument_list|(
name|currentBlock
operator|.
name|getBlockID
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|flip
argument_list|()
expr_stmt|;
try|try
block|{
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
decl_stmt|;
name|channel
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
name|flusher
operator|.
name|processDirtyBlocks
argument_list|(
name|this
operator|.
name|dbPath
argument_list|,
name|retryFileName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|flusher
operator|.
name|getTargetMetrics
argument_list|()
operator|.
name|incNumFailedRetryLogFileWrites
argument_list|()
expr_stmt|;
name|flusher
operator|.
name|getLOG
argument_list|()
operator|.
name|error
argument_list|(
literal|"Unable to write the retry block. Block ID: {}"
argument_list|,
name|currentBlock
operator|.
name|getBlockID
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Increments the try count. This is done each time we try this block    * write to the container.    */
DECL|method|incTryCount ()
specifier|private
name|void
name|incTryCount
parameter_list|()
block|{
name|tryCount
operator|++
expr_stmt|;
block|}
comment|/**    * Get the retry count.    *    * @return int    */
DECL|method|getTryCount ()
specifier|public
name|int
name|getTryCount
parameter_list|()
block|{
return|return
name|tryCount
return|;
block|}
block|}
end_class

end_unit

