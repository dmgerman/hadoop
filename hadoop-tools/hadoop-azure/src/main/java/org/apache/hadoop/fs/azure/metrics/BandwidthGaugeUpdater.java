begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
operator|.
name|metrics
package|;
end_package

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
name|Date
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

begin_comment
comment|/**  * Internal implementation class to help calculate the current bytes  * uploaded/downloaded and the maximum bandwidth gauges.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BandwidthGaugeUpdater
specifier|public
specifier|final
class|class
name|BandwidthGaugeUpdater
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
name|BandwidthGaugeUpdater
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|THREAD_NAME
specifier|public
specifier|static
specifier|final
name|String
name|THREAD_NAME
init|=
literal|"AzureNativeFilesystemStore-UploadBandwidthUpdater"
decl_stmt|;
DECL|field|DEFAULT_WINDOW_SIZE_MS
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_WINDOW_SIZE_MS
init|=
literal|1000
decl_stmt|;
DECL|field|PROCESS_QUEUE_INITIAL_CAPACITY
specifier|private
specifier|static
specifier|final
name|int
name|PROCESS_QUEUE_INITIAL_CAPACITY
init|=
literal|1000
decl_stmt|;
DECL|field|windowSizeMs
specifier|private
name|int
name|windowSizeMs
decl_stmt|;
DECL|field|allBlocksWritten
specifier|private
name|ArrayList
argument_list|<
name|BlockTransferWindow
argument_list|>
name|allBlocksWritten
init|=
name|createNewToProcessQueue
argument_list|()
decl_stmt|;
DECL|field|allBlocksRead
specifier|private
name|ArrayList
argument_list|<
name|BlockTransferWindow
argument_list|>
name|allBlocksRead
init|=
name|createNewToProcessQueue
argument_list|()
decl_stmt|;
DECL|field|blocksWrittenLock
specifier|private
specifier|final
name|Object
name|blocksWrittenLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|blocksReadLock
specifier|private
specifier|final
name|Object
name|blocksReadLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|instrumentation
specifier|private
specifier|final
name|AzureFileSystemInstrumentation
name|instrumentation
decl_stmt|;
DECL|field|uploadBandwidthUpdater
specifier|private
name|Thread
name|uploadBandwidthUpdater
decl_stmt|;
DECL|field|suppressAutoUpdate
specifier|private
specifier|volatile
name|boolean
name|suppressAutoUpdate
init|=
literal|false
decl_stmt|;
comment|/**    * Create a new updater object with default values.    * @param instrumentation The metrics source to update.    */
DECL|method|BandwidthGaugeUpdater (AzureFileSystemInstrumentation instrumentation)
specifier|public
name|BandwidthGaugeUpdater
parameter_list|(
name|AzureFileSystemInstrumentation
name|instrumentation
parameter_list|)
block|{
name|this
argument_list|(
name|instrumentation
argument_list|,
name|DEFAULT_WINDOW_SIZE_MS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new updater object with some overrides (used in unit tests).    * @param instrumentation The metrics source to update.    * @param windowSizeMs The window size to use for calculating bandwidth    *                    (in milliseconds).    * @param manualUpdateTrigger If true, then this object won't create the    *                            auto-update threads, and will wait for manual    *                            calls to triggerUpdate to occur.    */
DECL|method|BandwidthGaugeUpdater (AzureFileSystemInstrumentation instrumentation, int windowSizeMs, boolean manualUpdateTrigger)
specifier|public
name|BandwidthGaugeUpdater
parameter_list|(
name|AzureFileSystemInstrumentation
name|instrumentation
parameter_list|,
name|int
name|windowSizeMs
parameter_list|,
name|boolean
name|manualUpdateTrigger
parameter_list|)
block|{
name|this
operator|.
name|windowSizeMs
operator|=
name|windowSizeMs
expr_stmt|;
name|this
operator|.
name|instrumentation
operator|=
name|instrumentation
expr_stmt|;
if|if
condition|(
operator|!
name|manualUpdateTrigger
condition|)
block|{
name|uploadBandwidthUpdater
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|UploadBandwidthUpdater
argument_list|()
argument_list|,
name|THREAD_NAME
argument_list|)
expr_stmt|;
name|uploadBandwidthUpdater
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|uploadBandwidthUpdater
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Indicate that a block has been uploaded.    * @param startDate The exact time the upload started.    * @param endDate The exact time the upload ended.    * @param length The number of bytes uploaded in the block.    */
DECL|method|blockUploaded (Date startDate, Date endDate, long length)
specifier|public
name|void
name|blockUploaded
parameter_list|(
name|Date
name|startDate
parameter_list|,
name|Date
name|endDate
parameter_list|,
name|long
name|length
parameter_list|)
block|{
synchronized|synchronized
init|(
name|blocksWrittenLock
init|)
block|{
name|allBlocksWritten
operator|.
name|add
argument_list|(
operator|new
name|BlockTransferWindow
argument_list|(
name|startDate
argument_list|,
name|endDate
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Indicate that a block has been downloaded.    * @param startDate The exact time the download started.    * @param endDate The exact time the download ended.    * @param length The number of bytes downloaded in the block.    */
DECL|method|blockDownloaded (Date startDate, Date endDate, long length)
specifier|public
name|void
name|blockDownloaded
parameter_list|(
name|Date
name|startDate
parameter_list|,
name|Date
name|endDate
parameter_list|,
name|long
name|length
parameter_list|)
block|{
synchronized|synchronized
init|(
name|blocksReadLock
init|)
block|{
name|allBlocksRead
operator|.
name|add
argument_list|(
operator|new
name|BlockTransferWindow
argument_list|(
name|startDate
argument_list|,
name|endDate
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Creates a new ArrayList to hold incoming block transfer notifications    * before they're processed.    * @return The newly created ArrayList.    */
DECL|method|createNewToProcessQueue ()
specifier|private
specifier|static
name|ArrayList
argument_list|<
name|BlockTransferWindow
argument_list|>
name|createNewToProcessQueue
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|BlockTransferWindow
argument_list|>
argument_list|(
name|PROCESS_QUEUE_INITIAL_CAPACITY
argument_list|)
return|;
block|}
comment|/**    * Update the metrics source gauge for how many bytes were transferred    * during the last time window.    * @param updateWrite If true, update the write (upload) counter.    *                    Otherwise update the read (download) counter.    * @param bytes The number of bytes transferred.    */
DECL|method|updateBytesTransferred (boolean updateWrite, long bytes)
specifier|private
name|void
name|updateBytesTransferred
parameter_list|(
name|boolean
name|updateWrite
parameter_list|,
name|long
name|bytes
parameter_list|)
block|{
if|if
condition|(
name|updateWrite
condition|)
block|{
name|instrumentation
operator|.
name|updateBytesWrittenInLastSecond
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|instrumentation
operator|.
name|updateBytesReadInLastSecond
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Update the metrics source gauge for what the current transfer rate    * is.    * @param updateWrite If true, update the write (upload) counter.    *                    Otherwise update the read (download) counter.    * @param bytesPerSecond The number of bytes per second we're seeing.    */
DECL|method|updateBytesTransferRate (boolean updateWrite, long bytesPerSecond)
specifier|private
name|void
name|updateBytesTransferRate
parameter_list|(
name|boolean
name|updateWrite
parameter_list|,
name|long
name|bytesPerSecond
parameter_list|)
block|{
if|if
condition|(
name|updateWrite
condition|)
block|{
name|instrumentation
operator|.
name|currentUploadBytesPerSecond
argument_list|(
name|bytesPerSecond
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|instrumentation
operator|.
name|currentDownloadBytesPerSecond
argument_list|(
name|bytesPerSecond
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * For unit test purposes, suppresses auto-update of the metrics    * from the dedicated thread.    */
DECL|method|suppressAutoUpdate ()
specifier|public
name|void
name|suppressAutoUpdate
parameter_list|()
block|{
name|suppressAutoUpdate
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Resumes auto-update (undo suppressAutoUpdate).    */
DECL|method|resumeAutoUpdate ()
specifier|public
name|void
name|resumeAutoUpdate
parameter_list|()
block|{
name|suppressAutoUpdate
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * Triggers the update of the metrics gauge based on all the blocks    * uploaded/downloaded so far. This is typically done periodically in a    * dedicated update thread, but exposing as public for unit test purposes.    *     * @param updateWrite If true, we'll update the write (upload) metrics.    *                    Otherwise we'll update the read (download) ones.    */
DECL|method|triggerUpdate (boolean updateWrite)
specifier|public
name|void
name|triggerUpdate
parameter_list|(
name|boolean
name|updateWrite
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|BlockTransferWindow
argument_list|>
name|toProcess
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|updateWrite
condition|?
name|blocksWrittenLock
else|:
name|blocksReadLock
init|)
block|{
if|if
condition|(
name|updateWrite
operator|&&
operator|!
name|allBlocksWritten
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|toProcess
operator|=
name|allBlocksWritten
expr_stmt|;
name|allBlocksWritten
operator|=
name|createNewToProcessQueue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|updateWrite
operator|&&
operator|!
name|allBlocksRead
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|toProcess
operator|=
name|allBlocksRead
expr_stmt|;
name|allBlocksRead
operator|=
name|createNewToProcessQueue
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Check to see if we have any blocks to process.
if|if
condition|(
name|toProcess
operator|==
literal|null
condition|)
block|{
comment|// Nothing to process, set the current bytes and rate to zero.
name|updateBytesTransferred
argument_list|(
name|updateWrite
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|updateBytesTransferRate
argument_list|(
name|updateWrite
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// The cut-off time for when we want to calculate rates is one
comment|// window size ago from now.
name|long
name|cutoffTime
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|-
name|windowSizeMs
decl_stmt|;
comment|// Go through all the blocks we're processing, and calculate the
comment|// total number of bytes processed as well as the maximum transfer
comment|// rate we experienced for any single block during our time window.
name|long
name|maxSingleBlockTransferRate
init|=
literal|0
decl_stmt|;
name|long
name|bytesInLastSecond
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BlockTransferWindow
name|currentWindow
range|:
name|toProcess
control|)
block|{
name|long
name|windowDuration
init|=
name|currentWindow
operator|.
name|getEndDate
argument_list|()
operator|.
name|getTime
argument_list|()
operator|-
name|currentWindow
operator|.
name|getStartDate
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|windowDuration
operator|==
literal|0
condition|)
block|{
comment|// Edge case, assume it took 1 ms but we were too fast
name|windowDuration
operator|=
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|currentWindow
operator|.
name|getStartDate
argument_list|()
operator|.
name|getTime
argument_list|()
operator|>
name|cutoffTime
condition|)
block|{
comment|// This block was transferred fully within our time window,
comment|// just add its bytes to the total.
name|bytesInLastSecond
operator|+=
name|currentWindow
operator|.
name|bytesTransferred
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentWindow
operator|.
name|getEndDate
argument_list|()
operator|.
name|getTime
argument_list|()
operator|>
name|cutoffTime
condition|)
block|{
comment|// This block started its transfer before our time window,
comment|// interpolate to estimate how many bytes from that block
comment|// were actually transferred during our time window.
name|long
name|adjustedBytes
init|=
operator|(
name|currentWindow
operator|.
name|getBytesTransferred
argument_list|()
operator|*
operator|(
name|currentWindow
operator|.
name|getEndDate
argument_list|()
operator|.
name|getTime
argument_list|()
operator|-
name|cutoffTime
operator|)
operator|)
operator|/
name|windowDuration
decl_stmt|;
name|bytesInLastSecond
operator|+=
name|adjustedBytes
expr_stmt|;
block|}
comment|// Calculate the transfer rate for this block.
name|long
name|currentBlockTransferRate
init|=
operator|(
name|currentWindow
operator|.
name|getBytesTransferred
argument_list|()
operator|*
literal|1000
operator|)
operator|/
name|windowDuration
decl_stmt|;
name|maxSingleBlockTransferRate
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxSingleBlockTransferRate
argument_list|,
name|currentBlockTransferRate
argument_list|)
expr_stmt|;
block|}
name|updateBytesTransferred
argument_list|(
name|updateWrite
argument_list|,
name|bytesInLastSecond
argument_list|)
expr_stmt|;
comment|// The transfer rate we saw in the last second is a tricky concept to
comment|// define: If we saw two blocks, one 2 MB block transferred in 0.2 seconds,
comment|// and one 4 MB block transferred in 0.2 seconds, then the maximum rate
comment|// is 20 MB/s (the 4 MB block), the average of the two blocks is 15 MB/s,
comment|// and the aggregate rate is 6 MB/s (total of 6 MB transferred in one
comment|// second). As a first cut, I'm taking the definition to be the maximum
comment|// of aggregate or of any single block's rate (so in the example case it's
comment|// 6 MB/s).
name|long
name|aggregateTransferRate
init|=
name|bytesInLastSecond
decl_stmt|;
name|long
name|maxObservedTransferRate
init|=
name|Math
operator|.
name|max
argument_list|(
name|aggregateTransferRate
argument_list|,
name|maxSingleBlockTransferRate
argument_list|)
decl_stmt|;
name|updateBytesTransferRate
argument_list|(
name|updateWrite
argument_list|,
name|maxObservedTransferRate
argument_list|)
expr_stmt|;
block|}
comment|/**    * A single block transfer.    */
DECL|class|BlockTransferWindow
specifier|private
specifier|static
specifier|final
class|class
name|BlockTransferWindow
block|{
DECL|field|startDate
specifier|private
specifier|final
name|Date
name|startDate
decl_stmt|;
DECL|field|endDate
specifier|private
specifier|final
name|Date
name|endDate
decl_stmt|;
DECL|field|bytesTransferred
specifier|private
specifier|final
name|long
name|bytesTransferred
decl_stmt|;
DECL|method|BlockTransferWindow (Date startDate, Date endDate, long bytesTransferred)
specifier|public
name|BlockTransferWindow
parameter_list|(
name|Date
name|startDate
parameter_list|,
name|Date
name|endDate
parameter_list|,
name|long
name|bytesTransferred
parameter_list|)
block|{
name|this
operator|.
name|startDate
operator|=
name|startDate
expr_stmt|;
name|this
operator|.
name|endDate
operator|=
name|endDate
expr_stmt|;
name|this
operator|.
name|bytesTransferred
operator|=
name|bytesTransferred
expr_stmt|;
block|}
DECL|method|getStartDate ()
specifier|public
name|Date
name|getStartDate
parameter_list|()
block|{
return|return
name|startDate
return|;
block|}
DECL|method|getEndDate ()
specifier|public
name|Date
name|getEndDate
parameter_list|()
block|{
return|return
name|endDate
return|;
block|}
DECL|method|getBytesTransferred ()
specifier|public
name|long
name|getBytesTransferred
parameter_list|()
block|{
return|return
name|bytesTransferred
return|;
block|}
block|}
comment|/**    * The auto-update thread.    */
DECL|class|UploadBandwidthUpdater
specifier|private
specifier|final
class|class
name|UploadBandwidthUpdater
implements|implements
name|Runnable
block|{
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
while|while
condition|(
literal|true
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|windowSizeMs
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|suppressAutoUpdate
condition|)
block|{
name|triggerUpdate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|triggerUpdate
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{       }
block|}
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|uploadBandwidthUpdater
operator|!=
literal|null
condition|)
block|{
comment|// Interrupt and join the updater thread in death.
name|uploadBandwidthUpdater
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|uploadBandwidthUpdater
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{       }
name|uploadBandwidthUpdater
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

