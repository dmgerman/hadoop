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
name|TreeMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServlet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|hdfs
operator|.
name|protocol
operator|.
name|Block
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|FsDatasetSpi
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
name|fsdataset
operator|.
name|FsVolumeSpi
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

begin_comment
comment|/**  * DataBlockScanner manages block scanning for all the block pools. For each  * block pool a {@link BlockPoolSliceScanner} is created which runs in a separate  * thread to scan the blocks for that block pool. When a {@link BPOfferService}  * becomes alive or dies, blockPoolScannerMap in this class is updated.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DataBlockScanner
specifier|public
class|class
name|DataBlockScanner
implements|implements
name|Runnable
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
name|DataBlockScanner
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|datanode
specifier|private
specifier|final
name|DataNode
name|datanode
decl_stmt|;
DECL|field|dataset
specifier|private
specifier|final
name|FsDatasetSpi
argument_list|<
name|?
extends|extends
name|FsVolumeSpi
argument_list|>
name|dataset
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|SLEEP_PERIOD_MS
specifier|static
specifier|final
name|int
name|SLEEP_PERIOD_MS
init|=
literal|5
operator|*
literal|1000
decl_stmt|;
comment|/**    * Map to find the BlockPoolScanner for a given block pool id. This is updated    * when a BPOfferService becomes alive or dies.    */
DECL|field|blockPoolScannerMap
specifier|private
specifier|final
name|TreeMap
argument_list|<
name|String
argument_list|,
name|BlockPoolSliceScanner
argument_list|>
name|blockPoolScannerMap
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|BlockPoolSliceScanner
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|blockScannerThread
name|Thread
name|blockScannerThread
init|=
literal|null
decl_stmt|;
DECL|method|DataBlockScanner (DataNode datanode, FsDatasetSpi<? extends FsVolumeSpi> dataset, Configuration conf)
name|DataBlockScanner
parameter_list|(
name|DataNode
name|datanode
parameter_list|,
name|FsDatasetSpi
argument_list|<
name|?
extends|extends
name|FsVolumeSpi
argument_list|>
name|dataset
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|datanode
operator|=
name|datanode
expr_stmt|;
name|this
operator|.
name|dataset
operator|=
name|dataset
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
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
name|String
name|currentBpId
init|=
literal|""
decl_stmt|;
name|boolean
name|firstRun
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|datanode
operator|.
name|shouldRun
operator|&&
operator|!
name|Thread
operator|.
name|interrupted
argument_list|()
condition|)
block|{
comment|//Sleep everytime except in the first iteration.
if|if
condition|(
operator|!
name|firstRun
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|SLEEP_PERIOD_MS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
comment|// Interrupt itself again to set the interrupt status
name|blockScannerThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
continue|continue;
block|}
block|}
else|else
block|{
name|firstRun
operator|=
literal|false
expr_stmt|;
block|}
name|BlockPoolSliceScanner
name|bpScanner
init|=
name|getNextBPScanner
argument_list|(
name|currentBpId
argument_list|)
decl_stmt|;
if|if
condition|(
name|bpScanner
operator|==
literal|null
condition|)
block|{
comment|// Possible if thread is interrupted
continue|continue;
block|}
name|currentBpId
operator|=
name|bpScanner
operator|.
name|getBlockPoolId
argument_list|()
expr_stmt|;
comment|// If BPOfferService for this pool is not alive, don't process it
if|if
condition|(
operator|!
name|datanode
operator|.
name|isBPServiceAlive
argument_list|(
name|currentBpId
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Block Pool "
operator|+
name|currentBpId
operator|+
literal|" is not alive"
argument_list|)
expr_stmt|;
comment|// Remove in case BP service died abruptly without proper shutdown
name|removeBlockPool
argument_list|(
name|currentBpId
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|bpScanner
operator|.
name|scanBlockPoolSlice
argument_list|()
expr_stmt|;
block|}
comment|// Call shutdown for each allocated BlockPoolSliceScanner.
for|for
control|(
name|BlockPoolSliceScanner
name|bpss
range|:
name|blockPoolScannerMap
operator|.
name|values
argument_list|()
control|)
block|{
name|bpss
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Wait for at least one block pool to be up
DECL|method|waitForInit ()
specifier|private
name|void
name|waitForInit
parameter_list|()
block|{
while|while
condition|(
operator|(
name|getBlockPoolSetSize
argument_list|()
operator|<
name|datanode
operator|.
name|getAllBpOs
argument_list|()
operator|.
name|length
operator|)
operator|||
operator|(
name|getBlockPoolSetSize
argument_list|()
operator|<
literal|1
operator|)
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|SLEEP_PERIOD_MS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|blockScannerThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
block|}
comment|/**    * Find next block pool id to scan. There should be only one current    * verification log file. Find which block pool contains the current    * verification log file and that is used as the starting block pool id. If no    * current files are found start with first block-pool in the blockPoolSet.    * However, if more than one current files are found, the one with latest     * modification time is used to find the next block pool id.    */
DECL|method|getNextBPScanner (String currentBpId)
specifier|private
name|BlockPoolSliceScanner
name|getNextBPScanner
parameter_list|(
name|String
name|currentBpId
parameter_list|)
block|{
name|String
name|nextBpId
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|datanode
operator|.
name|shouldRun
operator|&&
operator|!
name|blockScannerThread
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
name|waitForInit
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|getBlockPoolSetSize
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// Find nextBpId by the minimum of the last scan time
name|long
name|lastScanTime
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|bpid
range|:
name|blockPoolScannerMap
operator|.
name|keySet
argument_list|()
control|)
block|{
specifier|final
name|long
name|t
init|=
name|getBPScanner
argument_list|(
name|bpid
argument_list|)
operator|.
name|getLastScanTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|0L
condition|)
block|{
if|if
condition|(
name|bpid
operator|==
literal|null
operator|||
name|t
operator|<
name|lastScanTime
condition|)
block|{
name|lastScanTime
operator|=
name|t
expr_stmt|;
name|nextBpId
operator|=
name|bpid
expr_stmt|;
block|}
block|}
block|}
comment|// nextBpId can still be null if no current log is found,
comment|// find nextBpId sequentially.
if|if
condition|(
name|nextBpId
operator|==
literal|null
condition|)
block|{
name|nextBpId
operator|=
name|blockPoolScannerMap
operator|.
name|higherKey
argument_list|(
name|currentBpId
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextBpId
operator|==
literal|null
condition|)
block|{
name|nextBpId
operator|=
name|blockPoolScannerMap
operator|.
name|firstKey
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|nextBpId
operator|!=
literal|null
condition|)
block|{
return|return
name|getBPScanner
argument_list|(
name|nextBpId
argument_list|)
return|;
block|}
block|}
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"No block pool is up, going to wait"
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Received exception: "
operator|+
name|ex
argument_list|)
expr_stmt|;
name|blockScannerThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getBlockPoolSetSize ()
specifier|private
specifier|synchronized
name|int
name|getBlockPoolSetSize
parameter_list|()
block|{
return|return
name|blockPoolScannerMap
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getBPScanner (String bpid)
specifier|synchronized
name|BlockPoolSliceScanner
name|getBPScanner
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
return|return
name|blockPoolScannerMap
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
return|;
block|}
DECL|method|getBpIdList ()
specifier|private
specifier|synchronized
name|String
index|[]
name|getBpIdList
parameter_list|()
block|{
return|return
name|blockPoolScannerMap
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|blockPoolScannerMap
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|addBlock (ExtendedBlock block, boolean scanNow)
specifier|public
name|void
name|addBlock
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|,
name|boolean
name|scanNow
parameter_list|)
block|{
name|BlockPoolSliceScanner
name|bpScanner
init|=
name|getBPScanner
argument_list|(
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|bpScanner
operator|!=
literal|null
condition|)
block|{
name|bpScanner
operator|.
name|addBlock
argument_list|(
name|block
argument_list|,
name|scanNow
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No block pool scanner found for block pool id: "
operator|+
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isInitialized (String bpid)
name|boolean
name|isInitialized
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
return|return
name|getBPScanner
argument_list|(
name|bpid
argument_list|)
operator|!=
literal|null
return|;
block|}
DECL|method|printBlockReport (StringBuilder buffer, boolean summary)
specifier|public
specifier|synchronized
name|void
name|printBlockReport
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|,
name|boolean
name|summary
parameter_list|)
block|{
name|String
index|[]
name|bpIdList
init|=
name|getBpIdList
argument_list|()
decl_stmt|;
if|if
condition|(
name|bpIdList
operator|==
literal|null
operator|||
name|bpIdList
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"Periodic block scanner is not yet initialized. "
operator|+
literal|"Please check back again after some time."
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
name|String
name|bpid
range|:
name|bpIdList
control|)
block|{
name|BlockPoolSliceScanner
name|bpScanner
init|=
name|getBPScanner
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"\n\nBlock report for block pool: "
operator|+
name|bpid
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|bpScanner
operator|.
name|printBlockReport
argument_list|(
name|buffer
argument_list|,
name|summary
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|deleteBlock (String poolId, Block toDelete)
specifier|public
name|void
name|deleteBlock
parameter_list|(
name|String
name|poolId
parameter_list|,
name|Block
name|toDelete
parameter_list|)
block|{
name|BlockPoolSliceScanner
name|bpScanner
init|=
name|getBPScanner
argument_list|(
name|poolId
argument_list|)
decl_stmt|;
if|if
condition|(
name|bpScanner
operator|!=
literal|null
condition|)
block|{
name|bpScanner
operator|.
name|deleteBlock
argument_list|(
name|toDelete
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No block pool scanner found for block pool id: "
operator|+
name|poolId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|deleteBlocks (String poolId, Block[] toDelete)
specifier|public
name|void
name|deleteBlocks
parameter_list|(
name|String
name|poolId
parameter_list|,
name|Block
index|[]
name|toDelete
parameter_list|)
block|{
name|BlockPoolSliceScanner
name|bpScanner
init|=
name|getBPScanner
argument_list|(
name|poolId
argument_list|)
decl_stmt|;
if|if
condition|(
name|bpScanner
operator|!=
literal|null
condition|)
block|{
name|bpScanner
operator|.
name|deleteBlocks
argument_list|(
name|toDelete
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No block pool scanner found for block pool id: "
operator|+
name|poolId
argument_list|)
expr_stmt|;
block|}
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
name|blockScannerThread
operator|!=
literal|null
condition|)
block|{
name|blockScannerThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
comment|// We cannot join within the synchronized block, because it would create a
comment|// deadlock situation.  blockScannerThread calls other synchronized methods.
if|if
condition|(
name|blockScannerThread
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|blockScannerThread
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
block|{
comment|// shutting down anyway
block|}
block|}
block|}
DECL|method|addBlockPool (String blockPoolId)
specifier|public
specifier|synchronized
name|void
name|addBlockPool
parameter_list|(
name|String
name|blockPoolId
parameter_list|)
block|{
if|if
condition|(
name|blockPoolScannerMap
operator|.
name|get
argument_list|(
name|blockPoolId
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|BlockPoolSliceScanner
name|bpScanner
init|=
operator|new
name|BlockPoolSliceScanner
argument_list|(
name|blockPoolId
argument_list|,
name|datanode
argument_list|,
name|dataset
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|blockPoolScannerMap
operator|.
name|put
argument_list|(
name|blockPoolId
argument_list|,
name|bpScanner
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Added bpid="
operator|+
name|blockPoolId
operator|+
literal|" to blockPoolScannerMap, new size="
operator|+
name|blockPoolScannerMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|removeBlockPool (String blockPoolId)
specifier|public
specifier|synchronized
name|void
name|removeBlockPool
parameter_list|(
name|String
name|blockPoolId
parameter_list|)
block|{
name|BlockPoolSliceScanner
name|bpss
init|=
name|blockPoolScannerMap
operator|.
name|remove
argument_list|(
name|blockPoolId
argument_list|)
decl_stmt|;
if|if
condition|(
name|bpss
operator|!=
literal|null
condition|)
block|{
name|bpss
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Removed bpid="
operator|+
name|blockPoolId
operator|+
literal|" from blockPoolScannerMap"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getBlocksScannedInLastRun (String bpid)
name|long
name|getBlocksScannedInLastRun
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockPoolSliceScanner
name|bpScanner
init|=
name|getBPScanner
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|bpScanner
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Block Pool: "
operator|+
name|bpid
operator|+
literal|" is not running"
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|bpScanner
operator|.
name|getBlocksScannedInLastRun
argument_list|()
return|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getTotalScans (String bpid)
name|long
name|getTotalScans
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockPoolSliceScanner
name|bpScanner
init|=
name|getBPScanner
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|bpScanner
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Block Pool: "
operator|+
name|bpid
operator|+
literal|" is not running"
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|bpScanner
operator|.
name|getTotalScans
argument_list|()
return|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|setLastScanTimeDifference (ExtendedBlock block, int lastScanTimeDifference)
specifier|public
name|void
name|setLastScanTimeDifference
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|,
name|int
name|lastScanTimeDifference
parameter_list|)
block|{
name|BlockPoolSliceScanner
name|bpScanner
init|=
name|getBPScanner
argument_list|(
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|bpScanner
operator|!=
literal|null
condition|)
block|{
name|bpScanner
operator|.
name|setLastScanTimeDifference
argument_list|(
name|lastScanTimeDifference
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No block pool scanner found for block pool id: "
operator|+
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|blockScannerThread
operator|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|blockScannerThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|blockScannerThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|Servlet
specifier|public
specifier|static
class|class
name|Servlet
extends|extends
name|HttpServlet
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest request, HttpServletResponse response)
specifier|public
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|DataNode
name|datanode
init|=
operator|(
name|DataNode
operator|)
name|getServletContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
literal|"datanode"
argument_list|)
decl_stmt|;
name|DataBlockScanner
name|blockScanner
init|=
name|datanode
operator|.
name|blockScanner
decl_stmt|;
name|boolean
name|summary
init|=
operator|(
name|request
operator|.
name|getParameter
argument_list|(
literal|"listblocks"
argument_list|)
operator|==
literal|null
operator|)
decl_stmt|;
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|(
literal|8
operator|*
literal|1024
argument_list|)
decl_stmt|;
if|if
condition|(
name|blockScanner
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Periodic block scanner is not running"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"Periodic block scanner is not running. "
operator|+
literal|"Please check the datanode log if this is unexpected."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|blockScanner
operator|.
name|printBlockReport
argument_list|(
name|buffer
argument_list|,
name|summary
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|getWriter
argument_list|()
operator|.
name|write
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// extra copy!
block|}
block|}
block|}
end_class

end_unit

