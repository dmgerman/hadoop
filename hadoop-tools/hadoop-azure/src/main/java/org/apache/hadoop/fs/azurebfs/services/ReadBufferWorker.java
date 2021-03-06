begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.services
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|services
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|azurebfs
operator|.
name|contracts
operator|.
name|services
operator|.
name|ReadBufferStatus
import|;
end_import

begin_class
DECL|class|ReadBufferWorker
class|class
name|ReadBufferWorker
implements|implements
name|Runnable
block|{
DECL|field|UNLEASH_WORKERS
specifier|protected
specifier|static
specifier|final
name|CountDownLatch
name|UNLEASH_WORKERS
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|id
specifier|private
name|int
name|id
decl_stmt|;
DECL|method|ReadBufferWorker (final int id)
name|ReadBufferWorker
parameter_list|(
specifier|final
name|int
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
comment|/**    * return the ID of ReadBufferWorker.    */
DECL|method|getId ()
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
comment|/**    * Waits until a buffer becomes available in ReadAheadQueue.    * Once a buffer becomes available, reads the file specified in it and then posts results back to buffer manager.    * Rinse and repeat. Forever.    */
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|UNLEASH_WORKERS
operator|.
name|await
argument_list|()
expr_stmt|;
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
block|}
name|ReadBufferManager
name|bufferManager
init|=
name|ReadBufferManager
operator|.
name|getBufferManager
argument_list|()
decl_stmt|;
name|ReadBuffer
name|buffer
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|buffer
operator|=
name|bufferManager
operator|.
name|getNextBlockToRead
argument_list|()
expr_stmt|;
comment|// blocks, until a buffer is available for this thread
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
return|return;
block|}
if|if
condition|(
name|buffer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
comment|// do the actual read, from the file.
name|int
name|bytesRead
init|=
name|buffer
operator|.
name|getStream
argument_list|()
operator|.
name|readRemote
argument_list|(
name|buffer
operator|.
name|getOffset
argument_list|()
argument_list|,
name|buffer
operator|.
name|getBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|getRequestedLength
argument_list|()
argument_list|)
decl_stmt|;
name|bufferManager
operator|.
name|doneReading
argument_list|(
name|buffer
argument_list|,
name|ReadBufferStatus
operator|.
name|AVAILABLE
argument_list|,
name|bytesRead
argument_list|)
expr_stmt|;
comment|// post result back to ReadBufferManager
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|bufferManager
operator|.
name|doneReading
argument_list|(
name|buffer
argument_list|,
name|ReadBufferStatus
operator|.
name|READ_FAILED
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

