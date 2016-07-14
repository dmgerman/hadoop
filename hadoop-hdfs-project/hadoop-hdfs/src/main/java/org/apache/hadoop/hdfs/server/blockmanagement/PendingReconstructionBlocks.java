begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
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
name|blockmanagement
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
name|util
operator|.
name|Time
operator|.
name|monotonicNow
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Time
import|;
end_import

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
name|Arrays
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|util
operator|.
name|Daemon
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

begin_comment
comment|/***************************************************  * PendingReconstructionBlocks does the bookkeeping of all  * blocks that gains stronger redundancy.  *  * It does the following:  * 1)  record blocks that gains stronger redundancy at this instant.  * 2)  a coarse grain timer to track age of reconstruction request  * 3)  a thread that periodically identifies reconstruction-requests  *     that never made it.  *  ***************************************************/
end_comment

begin_class
DECL|class|PendingReconstructionBlocks
class|class
name|PendingReconstructionBlocks
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|BlockManager
operator|.
name|LOG
decl_stmt|;
DECL|field|pendingReconstructions
specifier|private
specifier|final
name|Map
argument_list|<
name|BlockInfo
argument_list|,
name|PendingBlockInfo
argument_list|>
name|pendingReconstructions
decl_stmt|;
DECL|field|timedOutItems
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|BlockInfo
argument_list|>
name|timedOutItems
decl_stmt|;
DECL|field|timerThread
name|Daemon
name|timerThread
init|=
literal|null
decl_stmt|;
DECL|field|fsRunning
specifier|private
specifier|volatile
name|boolean
name|fsRunning
init|=
literal|true
decl_stmt|;
DECL|field|timedOutCount
specifier|private
name|long
name|timedOutCount
init|=
literal|0L
decl_stmt|;
comment|//
comment|// It might take anywhere between 5 to 10 minutes before
comment|// a request is timed out.
comment|//
DECL|field|timeout
specifier|private
name|long
name|timeout
init|=
literal|5
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
DECL|field|DEFAULT_RECHECK_INTERVAL
specifier|private
specifier|final
specifier|static
name|long
name|DEFAULT_RECHECK_INTERVAL
init|=
literal|5
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
DECL|method|PendingReconstructionBlocks (long timeoutPeriod)
name|PendingReconstructionBlocks
parameter_list|(
name|long
name|timeoutPeriod
parameter_list|)
block|{
if|if
condition|(
name|timeoutPeriod
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeoutPeriod
expr_stmt|;
block|}
name|pendingReconstructions
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|timedOutItems
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|start ()
name|void
name|start
parameter_list|()
block|{
name|timerThread
operator|=
operator|new
name|Daemon
argument_list|(
operator|new
name|PendingReconstructionMonitor
argument_list|()
argument_list|)
expr_stmt|;
name|timerThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * Add a block to the list of pending reconstructions    * @param block The corresponding block    * @param targets The DataNodes where replicas of the block should be placed    */
DECL|method|increment (BlockInfo block, DatanodeDescriptor... targets)
name|void
name|increment
parameter_list|(
name|BlockInfo
name|block
parameter_list|,
name|DatanodeDescriptor
modifier|...
name|targets
parameter_list|)
block|{
synchronized|synchronized
init|(
name|pendingReconstructions
init|)
block|{
name|PendingBlockInfo
name|found
init|=
name|pendingReconstructions
operator|.
name|get
argument_list|(
name|block
argument_list|)
decl_stmt|;
if|if
condition|(
name|found
operator|==
literal|null
condition|)
block|{
name|pendingReconstructions
operator|.
name|put
argument_list|(
name|block
argument_list|,
operator|new
name|PendingBlockInfo
argument_list|(
name|targets
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|found
operator|.
name|incrementReplicas
argument_list|(
name|targets
argument_list|)
expr_stmt|;
name|found
operator|.
name|setTimeStamp
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * One reconstruction request for this block has finished.    * Decrement the number of pending reconstruction requests    * for this block.    *    * @param dn The DataNode that finishes the reconstruction    */
DECL|method|decrement (BlockInfo block, DatanodeDescriptor dn)
name|void
name|decrement
parameter_list|(
name|BlockInfo
name|block
parameter_list|,
name|DatanodeDescriptor
name|dn
parameter_list|)
block|{
synchronized|synchronized
init|(
name|pendingReconstructions
init|)
block|{
name|PendingBlockInfo
name|found
init|=
name|pendingReconstructions
operator|.
name|get
argument_list|(
name|block
argument_list|)
decl_stmt|;
if|if
condition|(
name|found
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Removing pending reconstruction for {}"
argument_list|,
name|block
argument_list|)
expr_stmt|;
name|found
operator|.
name|decrementReplicas
argument_list|(
name|dn
argument_list|)
expr_stmt|;
if|if
condition|(
name|found
operator|.
name|getNumReplicas
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|pendingReconstructions
operator|.
name|remove
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Remove the record about the given block from pending reconstructions.    *    * @param block    *          The given block whose pending reconstruction requests need to be    *          removed    */
DECL|method|remove (BlockInfo block)
name|void
name|remove
parameter_list|(
name|BlockInfo
name|block
parameter_list|)
block|{
synchronized|synchronized
init|(
name|pendingReconstructions
init|)
block|{
name|pendingReconstructions
operator|.
name|remove
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|clear ()
specifier|public
name|void
name|clear
parameter_list|()
block|{
synchronized|synchronized
init|(
name|pendingReconstructions
init|)
block|{
name|pendingReconstructions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|timedOutItems
operator|.
name|clear
argument_list|()
expr_stmt|;
name|timedOutCount
operator|=
literal|0L
expr_stmt|;
block|}
block|}
comment|/**    * The total number of blocks that are undergoing reconstruction.    */
DECL|method|size ()
name|int
name|size
parameter_list|()
block|{
synchronized|synchronized
init|(
name|pendingReconstructions
init|)
block|{
return|return
name|pendingReconstructions
operator|.
name|size
argument_list|()
return|;
block|}
block|}
comment|/**    * How many copies of this block is pending reconstruction?.    */
DECL|method|getNumReplicas (BlockInfo block)
name|int
name|getNumReplicas
parameter_list|(
name|BlockInfo
name|block
parameter_list|)
block|{
synchronized|synchronized
init|(
name|pendingReconstructions
init|)
block|{
name|PendingBlockInfo
name|found
init|=
name|pendingReconstructions
operator|.
name|get
argument_list|(
name|block
argument_list|)
decl_stmt|;
if|if
condition|(
name|found
operator|!=
literal|null
condition|)
block|{
return|return
name|found
operator|.
name|getNumReplicas
argument_list|()
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
comment|/**    * Used for metrics.    * @return The number of timeouts    */
DECL|method|getNumTimedOuts ()
name|long
name|getNumTimedOuts
parameter_list|()
block|{
synchronized|synchronized
init|(
name|timedOutItems
init|)
block|{
return|return
name|timedOutCount
operator|+
name|timedOutItems
operator|.
name|size
argument_list|()
return|;
block|}
block|}
comment|/**    * Returns a list of blocks that have timed out their    * reconstruction requests. Returns null if no blocks have    * timed out.    */
DECL|method|getTimedOutBlocks ()
name|BlockInfo
index|[]
name|getTimedOutBlocks
parameter_list|()
block|{
synchronized|synchronized
init|(
name|timedOutItems
init|)
block|{
if|if
condition|(
name|timedOutItems
operator|.
name|size
argument_list|()
operator|<=
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|size
init|=
name|timedOutItems
operator|.
name|size
argument_list|()
decl_stmt|;
name|BlockInfo
index|[]
name|blockList
init|=
name|timedOutItems
operator|.
name|toArray
argument_list|(
operator|new
name|BlockInfo
index|[
name|size
index|]
argument_list|)
decl_stmt|;
name|timedOutItems
operator|.
name|clear
argument_list|()
expr_stmt|;
name|timedOutCount
operator|+=
name|size
expr_stmt|;
return|return
name|blockList
return|;
block|}
block|}
comment|/**    * An object that contains information about a block that    * is being reconstructed. It records the timestamp when the    * system started reconstructing the most recent copy of this    * block. It also records the list of Datanodes where the    * reconstruction requests are in progress.    */
DECL|class|PendingBlockInfo
specifier|static
class|class
name|PendingBlockInfo
block|{
DECL|field|timeStamp
specifier|private
name|long
name|timeStamp
decl_stmt|;
DECL|field|targets
specifier|private
specifier|final
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|targets
decl_stmt|;
DECL|method|PendingBlockInfo (DatanodeDescriptor[] targets)
name|PendingBlockInfo
parameter_list|(
name|DatanodeDescriptor
index|[]
name|targets
parameter_list|)
block|{
name|this
operator|.
name|timeStamp
operator|=
name|monotonicNow
argument_list|()
expr_stmt|;
name|this
operator|.
name|targets
operator|=
name|targets
operator|==
literal|null
condition|?
operator|new
name|ArrayList
argument_list|<
name|DatanodeDescriptor
argument_list|>
argument_list|()
else|:
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|targets
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getTimeStamp ()
name|long
name|getTimeStamp
parameter_list|()
block|{
return|return
name|timeStamp
return|;
block|}
DECL|method|setTimeStamp ()
name|void
name|setTimeStamp
parameter_list|()
block|{
name|timeStamp
operator|=
name|monotonicNow
argument_list|()
expr_stmt|;
block|}
DECL|method|incrementReplicas (DatanodeDescriptor... newTargets)
name|void
name|incrementReplicas
parameter_list|(
name|DatanodeDescriptor
modifier|...
name|newTargets
parameter_list|)
block|{
if|if
condition|(
name|newTargets
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|DatanodeDescriptor
name|newTarget
range|:
name|newTargets
control|)
block|{
if|if
condition|(
operator|!
name|targets
operator|.
name|contains
argument_list|(
name|newTarget
argument_list|)
condition|)
block|{
name|targets
operator|.
name|add
argument_list|(
name|newTarget
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|decrementReplicas (DatanodeDescriptor dn)
name|void
name|decrementReplicas
parameter_list|(
name|DatanodeDescriptor
name|dn
parameter_list|)
block|{
name|targets
operator|.
name|remove
argument_list|(
name|dn
argument_list|)
expr_stmt|;
block|}
DECL|method|getNumReplicas ()
name|int
name|getNumReplicas
parameter_list|()
block|{
return|return
name|targets
operator|.
name|size
argument_list|()
return|;
block|}
block|}
comment|/*    * A periodic thread that scans for blocks that never finished    * their reconstruction request.    */
DECL|class|PendingReconstructionMonitor
class|class
name|PendingReconstructionMonitor
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
while|while
condition|(
name|fsRunning
condition|)
block|{
name|long
name|period
init|=
name|Math
operator|.
name|min
argument_list|(
name|DEFAULT_RECHECK_INTERVAL
argument_list|,
name|timeout
argument_list|)
decl_stmt|;
try|try
block|{
name|pendingReconstructionCheck
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|period
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"PendingReconstructionMonitor thread is interrupted."
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Iterate through all items and detect timed-out items      */
DECL|method|pendingReconstructionCheck ()
name|void
name|pendingReconstructionCheck
parameter_list|()
block|{
synchronized|synchronized
init|(
name|pendingReconstructions
init|)
block|{
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|BlockInfo
argument_list|,
name|PendingBlockInfo
argument_list|>
argument_list|>
name|iter
init|=
name|pendingReconstructions
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|long
name|now
init|=
name|monotonicNow
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"PendingReconstructionMonitor checking Q"
argument_list|)
expr_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|BlockInfo
argument_list|,
name|PendingBlockInfo
argument_list|>
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|PendingBlockInfo
name|pendingBlock
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|>
name|pendingBlock
operator|.
name|getTimeStamp
argument_list|()
operator|+
name|timeout
condition|)
block|{
name|BlockInfo
name|block
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|timedOutItems
init|)
block|{
name|timedOutItems
operator|.
name|add
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"PendingReconstructionMonitor timed out "
operator|+
name|block
argument_list|)
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/*    * Shuts down the pending reconstruction monitor thread.    * Waits for the thread to exit.    */
DECL|method|stop ()
name|void
name|stop
parameter_list|()
block|{
name|fsRunning
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|timerThread
operator|==
literal|null
condition|)
return|return;
name|timerThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|timerThread
operator|.
name|join
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{     }
block|}
comment|/**    * Iterate through all items and print them.    */
DECL|method|metaSave (PrintWriter out)
name|void
name|metaSave
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
block|{
synchronized|synchronized
init|(
name|pendingReconstructions
init|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"Metasave: Blocks being reconstructed: "
operator|+
name|pendingReconstructions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|BlockInfo
argument_list|,
name|PendingBlockInfo
argument_list|>
name|entry
range|:
name|pendingReconstructions
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|PendingBlockInfo
name|pendingBlock
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Block
name|block
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
name|block
operator|+
literal|" StartTime: "
operator|+
operator|new
name|Time
argument_list|(
name|pendingBlock
operator|.
name|timeStamp
argument_list|)
operator|+
literal|" NumReconstructInProgress: "
operator|+
name|pendingBlock
operator|.
name|getNumReplicas
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

