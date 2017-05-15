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
comment|/**  * This task is responsible for flushing the BlockIDBuffer  * to Dirty Log File. This Dirty Log file is used later by  * ContainerCacheFlusher when the data is written to container  */
end_comment

begin_class
DECL|class|BlockBufferFlushTask
specifier|public
class|class
name|BlockBufferFlushTask
implements|implements
name|Runnable
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
name|BlockBufferFlushTask
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|parentCache
specifier|private
specifier|final
name|CBlockLocalCache
name|parentCache
decl_stmt|;
DECL|field|bufferManager
specifier|private
specifier|final
name|BlockBufferManager
name|bufferManager
decl_stmt|;
DECL|field|blockIDBuffer
specifier|private
specifier|final
name|ByteBuffer
name|blockIDBuffer
decl_stmt|;
DECL|method|BlockBufferFlushTask (ByteBuffer blockIDBuffer, CBlockLocalCache parentCache, BlockBufferManager manager)
name|BlockBufferFlushTask
parameter_list|(
name|ByteBuffer
name|blockIDBuffer
parameter_list|,
name|CBlockLocalCache
name|parentCache
parameter_list|,
name|BlockBufferManager
name|manager
parameter_list|)
block|{
name|this
operator|.
name|parentCache
operator|=
name|parentCache
expr_stmt|;
name|this
operator|.
name|bufferManager
operator|=
name|manager
expr_stmt|;
name|this
operator|.
name|blockIDBuffer
operator|=
name|blockIDBuffer
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
try|try
block|{
name|writeBlockBufferToFile
argument_list|(
name|blockIDBuffer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|parentCache
operator|.
name|getTargetMetrics
argument_list|()
operator|.
name|incNumFailedBlockBufferFlushes
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to sync the Block map to disk with "
operator|+
operator|(
name|blockIDBuffer
operator|.
name|position
argument_list|()
operator|/
name|Long
operator|.
name|SIZE
operator|)
operator|+
literal|"entries "
operator|+
literal|"-- NOTE: This might cause a data loss or corruption"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|bufferManager
operator|.
name|releaseBuffer
argument_list|(
name|blockIDBuffer
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Write Block Buffer to file.    *    * @param buffer - ByteBuffer    * @throws IOException    */
DECL|method|writeBlockBufferToFile (ByteBuffer buffer)
specifier|private
name|void
name|writeBlockBufferToFile
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|boolean
name|append
init|=
literal|false
decl_stmt|;
comment|// If there is nothing written to blockId buffer,
comment|// then skip flushing of blockId buffer
if|if
condition|(
name|buffer
operator|.
name|position
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|buffer
operator|.
name|flip
argument_list|()
expr_stmt|;
name|String
name|fileName
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s.%s"
argument_list|,
name|AsyncBlockWriter
operator|.
name|DIRTY_LOG_PREFIX
argument_list|,
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|)
decl_stmt|;
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
name|int
name|bytesWritten
init|=
name|channel
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
name|buffer
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
literal|"Task=DirtyBlockLogWrite,Time={} bytesWritten={}"
argument_list|,
name|endTime
operator|-
name|startTime
argument_list|,
name|bytesWritten
argument_list|)
expr_stmt|;
block|}
name|parentCache
operator|.
name|getTargetMetrics
argument_list|()
operator|.
name|incNumBlockBufferFlushCompleted
argument_list|()
expr_stmt|;
name|parentCache
operator|.
name|getTargetMetrics
argument_list|()
operator|.
name|incNumBytesDirtyLogWritten
argument_list|(
name|bytesWritten
argument_list|)
expr_stmt|;
name|parentCache
operator|.
name|getTargetMetrics
argument_list|()
operator|.
name|updateBlockBufferFlushLatency
argument_list|(
name|endTime
operator|-
name|startTime
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Block buffer writer bytesWritten:{} Time:{}"
argument_list|,
name|bytesWritten
argument_list|,
name|endTime
operator|-
name|startTime
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

