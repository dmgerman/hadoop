begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
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
name|IOException
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
name|namenode
operator|.
name|StoragePolicySatisfier
operator|.
name|ItemInfo
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
name|protocol
operator|.
name|BlocksStorageMovementResult
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
name|protocol
operator|.
name|BlocksStorageMovementResult
operator|.
name|Status
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
comment|/**  * A monitor class for checking whether block storage movements finished or not.  * If block storage movement results from datanode indicates about the movement  * success, then it will just remove the entries from tracking. If it reports  * failure, then it will add back to needed block storage movements list. If it  * reports in_progress, that means the blocks movement is in progress and the  * coordinator is still tracking the movement. If no DN reports about movement  * for longer time, then such items will be retries automatically after timeout.  * The default timeout would be 30mins.  */
end_comment

begin_class
DECL|class|BlockStorageMovementAttemptedItems
specifier|public
class|class
name|BlockStorageMovementAttemptedItems
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
name|BlockStorageMovementAttemptedItems
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * A map holds the items which are already taken for blocks movements    * processing and sent to DNs.    */
DECL|field|storageMovementAttemptedItems
specifier|private
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|AttemptedItemInfo
argument_list|>
name|storageMovementAttemptedItems
decl_stmt|;
DECL|field|storageMovementAttemptedResults
specifier|private
specifier|final
name|List
argument_list|<
name|BlocksStorageMovementResult
argument_list|>
name|storageMovementAttemptedResults
decl_stmt|;
DECL|field|monitorRunning
specifier|private
specifier|volatile
name|boolean
name|monitorRunning
init|=
literal|true
decl_stmt|;
DECL|field|timerThread
specifier|private
name|Daemon
name|timerThread
init|=
literal|null
decl_stmt|;
DECL|field|sps
specifier|private
specifier|final
name|StoragePolicySatisfier
name|sps
decl_stmt|;
comment|//
comment|// It might take anywhere between 20 to 60 minutes before
comment|// a request is timed out.
comment|//
DECL|field|selfRetryTimeout
specifier|private
name|long
name|selfRetryTimeout
init|=
literal|20
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|//
comment|// It might take anywhere between 5 to 10 minutes before
comment|// a request is timed out.
comment|//
DECL|field|minCheckTimeout
specifier|private
name|long
name|minCheckTimeout
init|=
literal|5
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// minimum value
DECL|field|blockStorageMovementNeeded
specifier|private
name|BlockStorageMovementNeeded
name|blockStorageMovementNeeded
decl_stmt|;
DECL|method|BlockStorageMovementAttemptedItems (long recheckTimeout, long selfRetryTimeout, BlockStorageMovementNeeded unsatisfiedStorageMovementFiles, StoragePolicySatisfier sps)
specifier|public
name|BlockStorageMovementAttemptedItems
parameter_list|(
name|long
name|recheckTimeout
parameter_list|,
name|long
name|selfRetryTimeout
parameter_list|,
name|BlockStorageMovementNeeded
name|unsatisfiedStorageMovementFiles
parameter_list|,
name|StoragePolicySatisfier
name|sps
parameter_list|)
block|{
if|if
condition|(
name|recheckTimeout
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|minCheckTimeout
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minCheckTimeout
argument_list|,
name|recheckTimeout
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|selfRetryTimeout
operator|=
name|selfRetryTimeout
expr_stmt|;
name|this
operator|.
name|blockStorageMovementNeeded
operator|=
name|unsatisfiedStorageMovementFiles
expr_stmt|;
name|storageMovementAttemptedItems
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|storageMovementAttemptedResults
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|sps
operator|=
name|sps
expr_stmt|;
block|}
comment|/**    * Add item to block storage movement attempted items map which holds the    * tracking/blockCollection id versus time stamp.    *    * @param itemInfo    *          - tracking info    * @param allBlockLocsAttemptedToSatisfy    *          - failed to find matching target nodes to satisfy storage type    *          for all the block locations of the given blockCollectionID    */
DECL|method|add (ItemInfo itemInfo, boolean allBlockLocsAttemptedToSatisfy)
specifier|public
name|void
name|add
parameter_list|(
name|ItemInfo
name|itemInfo
parameter_list|,
name|boolean
name|allBlockLocsAttemptedToSatisfy
parameter_list|)
block|{
synchronized|synchronized
init|(
name|storageMovementAttemptedItems
init|)
block|{
name|AttemptedItemInfo
name|attemptedItemInfo
init|=
operator|new
name|AttemptedItemInfo
argument_list|(
name|itemInfo
operator|.
name|getRootId
argument_list|()
argument_list|,
name|itemInfo
operator|.
name|getTrackId
argument_list|()
argument_list|,
name|monotonicNow
argument_list|()
argument_list|,
name|allBlockLocsAttemptedToSatisfy
argument_list|)
decl_stmt|;
name|storageMovementAttemptedItems
operator|.
name|put
argument_list|(
name|itemInfo
operator|.
name|getTrackId
argument_list|()
argument_list|,
name|attemptedItemInfo
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Add the trackIDBlocksStorageMovementResults to    * storageMovementAttemptedResults.    *    * @param blksMovementResults    */
DECL|method|addResults (BlocksStorageMovementResult[] blksMovementResults)
specifier|public
name|void
name|addResults
parameter_list|(
name|BlocksStorageMovementResult
index|[]
name|blksMovementResults
parameter_list|)
block|{
if|if
condition|(
name|blksMovementResults
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return;
block|}
synchronized|synchronized
init|(
name|storageMovementAttemptedResults
init|)
block|{
name|storageMovementAttemptedResults
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|blksMovementResults
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Starts the monitor thread.    */
DECL|method|start ()
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
block|{
name|monitorRunning
operator|=
literal|true
expr_stmt|;
name|timerThread
operator|=
operator|new
name|Daemon
argument_list|(
operator|new
name|BlocksStorageMovementAttemptResultMonitor
argument_list|()
argument_list|)
expr_stmt|;
name|timerThread
operator|.
name|setName
argument_list|(
literal|"BlocksStorageMovementAttemptResultMonitor"
argument_list|)
expr_stmt|;
name|timerThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * Sets running flag to false. Also, this will interrupt monitor thread and    * clear all the queued up tasks.    */
DECL|method|stop ()
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
block|{
name|monitorRunning
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|timerThread
operator|!=
literal|null
condition|)
block|{
name|timerThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|clearQueues
argument_list|()
expr_stmt|;
block|}
comment|/**    * Timed wait to stop monitor thread.    */
DECL|method|stopGracefully ()
specifier|synchronized
name|void
name|stopGracefully
parameter_list|()
block|{
if|if
condition|(
name|timerThread
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|monitorRunning
condition|)
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
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
comment|/**    * This class contains information of an attempted trackID. Information such    * as, (a)last attempted or reported time stamp, (b)whether all the blocks in    * the trackID were attempted and blocks movement has been scheduled to    * satisfy storage policy. This is used by    * {@link BlockStorageMovementAttemptedItems#storageMovementAttemptedItems}.    */
DECL|class|AttemptedItemInfo
specifier|private
specifier|final
specifier|static
class|class
name|AttemptedItemInfo
extends|extends
name|ItemInfo
block|{
DECL|field|lastAttemptedOrReportedTime
specifier|private
name|long
name|lastAttemptedOrReportedTime
decl_stmt|;
DECL|field|allBlockLocsAttemptedToSatisfy
specifier|private
specifier|final
name|boolean
name|allBlockLocsAttemptedToSatisfy
decl_stmt|;
comment|/**      * AttemptedItemInfo constructor.      *      * @param rootId      *          rootId for trackId      * @param trackId      *          trackId for file.      * @param lastAttemptedOrReportedTime      *          last attempted or reported time      * @param allBlockLocsAttemptedToSatisfy      *          whether all the blocks in the trackID were attempted and blocks      *          movement has been scheduled to satisfy storage policy      */
DECL|method|AttemptedItemInfo (long rootId, long trackId, long lastAttemptedOrReportedTime, boolean allBlockLocsAttemptedToSatisfy)
specifier|private
name|AttemptedItemInfo
parameter_list|(
name|long
name|rootId
parameter_list|,
name|long
name|trackId
parameter_list|,
name|long
name|lastAttemptedOrReportedTime
parameter_list|,
name|boolean
name|allBlockLocsAttemptedToSatisfy
parameter_list|)
block|{
name|super
argument_list|(
name|rootId
argument_list|,
name|trackId
argument_list|)
expr_stmt|;
name|this
operator|.
name|lastAttemptedOrReportedTime
operator|=
name|lastAttemptedOrReportedTime
expr_stmt|;
name|this
operator|.
name|allBlockLocsAttemptedToSatisfy
operator|=
name|allBlockLocsAttemptedToSatisfy
expr_stmt|;
block|}
comment|/**      * @return last attempted or reported time stamp.      */
DECL|method|getLastAttemptedOrReportedTime ()
specifier|private
name|long
name|getLastAttemptedOrReportedTime
parameter_list|()
block|{
return|return
name|lastAttemptedOrReportedTime
return|;
block|}
comment|/**      * @return true/false. True value represents that, all the block locations      *         under the trackID has found matching target nodes to satisfy      *         storage policy. False value represents that, trackID needed      *         retries to satisfy the storage policy for some of the block      *         locations.      */
DECL|method|isAllBlockLocsAttemptedToSatisfy ()
specifier|private
name|boolean
name|isAllBlockLocsAttemptedToSatisfy
parameter_list|()
block|{
return|return
name|allBlockLocsAttemptedToSatisfy
return|;
block|}
comment|/**      * Update lastAttemptedOrReportedTime, so that the expiration time will be      * postponed to future.      */
DECL|method|touchLastReportedTimeStamp ()
specifier|private
name|void
name|touchLastReportedTimeStamp
parameter_list|()
block|{
name|this
operator|.
name|lastAttemptedOrReportedTime
operator|=
name|monotonicNow
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * A monitor class for checking block storage movement result and long waiting    * items periodically.    */
DECL|class|BlocksStorageMovementAttemptResultMonitor
specifier|private
class|class
name|BlocksStorageMovementAttemptResultMonitor
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
name|monitorRunning
condition|)
block|{
try|try
block|{
name|blockStorageMovementResultCheck
argument_list|()
expr_stmt|;
name|blocksStorageMovementUnReportedItemsCheck
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|minCheckTimeout
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
name|info
argument_list|(
literal|"BlocksStorageMovementAttemptResultMonitor thread "
operator|+
literal|"is interrupted."
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"BlocksStorageMovementAttemptResultMonitor thread "
operator|+
literal|"received exception and exiting."
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|blocksStorageMovementUnReportedItemsCheck ()
name|void
name|blocksStorageMovementUnReportedItemsCheck
parameter_list|()
block|{
synchronized|synchronized
init|(
name|storageMovementAttemptedItems
init|)
block|{
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|Long
argument_list|,
name|AttemptedItemInfo
argument_list|>
argument_list|>
name|iter
init|=
name|storageMovementAttemptedItems
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
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Entry
argument_list|<
name|Long
argument_list|,
name|AttemptedItemInfo
argument_list|>
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|AttemptedItemInfo
name|itemInfo
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
name|itemInfo
operator|.
name|getLastAttemptedOrReportedTime
argument_list|()
operator|+
name|selfRetryTimeout
condition|)
block|{
name|Long
name|blockCollectionID
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|storageMovementAttemptedResults
init|)
block|{
if|if
condition|(
operator|!
name|isExistInResult
argument_list|(
name|blockCollectionID
argument_list|)
condition|)
block|{
name|ItemInfo
name|candidate
init|=
operator|new
name|ItemInfo
argument_list|(
name|itemInfo
operator|.
name|getRootId
argument_list|()
argument_list|,
name|blockCollectionID
argument_list|)
decl_stmt|;
name|blockStorageMovementNeeded
operator|.
name|add
argument_list|(
name|candidate
argument_list|)
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"TrackID: {} becomes timed out and moved to needed "
operator|+
literal|"retries queue for next iteration."
argument_list|,
name|blockCollectionID
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Blocks storage movement results for the"
operator|+
literal|" tracking id : "
operator|+
name|blockCollectionID
operator|+
literal|" is reported from one of the co-ordinating datanode."
operator|+
literal|" So, the result will be processed soon."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
DECL|method|isExistInResult (Long blockCollectionID)
specifier|private
name|boolean
name|isExistInResult
parameter_list|(
name|Long
name|blockCollectionID
parameter_list|)
block|{
name|Iterator
argument_list|<
name|BlocksStorageMovementResult
argument_list|>
name|iter
init|=
name|storageMovementAttemptedResults
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|BlocksStorageMovementResult
name|storageMovementAttemptedResult
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|storageMovementAttemptedResult
operator|.
name|getTrackId
argument_list|()
operator|==
name|blockCollectionID
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|blockStorageMovementResultCheck ()
name|void
name|blockStorageMovementResultCheck
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|storageMovementAttemptedResults
init|)
block|{
name|Iterator
argument_list|<
name|BlocksStorageMovementResult
argument_list|>
name|resultsIter
init|=
name|storageMovementAttemptedResults
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|resultsIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|boolean
name|isInprogress
init|=
literal|false
decl_stmt|;
comment|// TrackID need to be retried in the following cases:
comment|// 1) All or few scheduled block(s) movement has been failed.
comment|// 2) All the scheduled block(s) movement has been succeeded but there
comment|// are unscheduled block(s) movement in this trackID. Say, some of
comment|// the blocks in the trackID couldn't finding any matching target node
comment|// for scheduling block movement in previous SPS iteration.
name|BlocksStorageMovementResult
name|storageMovementAttemptedResult
init|=
name|resultsIter
operator|.
name|next
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|storageMovementAttemptedItems
init|)
block|{
name|Status
name|status
init|=
name|storageMovementAttemptedResult
operator|.
name|getStatus
argument_list|()
decl_stmt|;
name|long
name|trackId
init|=
name|storageMovementAttemptedResult
operator|.
name|getTrackId
argument_list|()
decl_stmt|;
name|AttemptedItemInfo
name|attemptedItemInfo
init|=
name|storageMovementAttemptedItems
operator|.
name|get
argument_list|(
name|trackId
argument_list|)
decl_stmt|;
comment|// itemInfo is null means no root for trackId, using trackId only as
comment|// root and handling it in
comment|// blockStorageMovementNeeded#removeIteamTrackInfo() for cleaning
comment|// the xAttr
name|ItemInfo
name|itemInfo
init|=
operator|new
name|ItemInfo
argument_list|(
operator|(
name|attemptedItemInfo
operator|!=
literal|null
operator|)
condition|?
name|attemptedItemInfo
operator|.
name|getRootId
argument_list|()
else|:
name|trackId
argument_list|,
name|trackId
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|status
condition|)
block|{
case|case
name|FAILURE
case|:
if|if
condition|(
name|attemptedItemInfo
operator|!=
literal|null
condition|)
block|{
name|blockStorageMovementNeeded
operator|.
name|add
argument_list|(
name|itemInfo
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Blocks storage movement results for the tracking id:"
operator|+
literal|"{} is reported from co-ordinating datanode, but result"
operator|+
literal|" status is FAILURE. So, added for retry"
argument_list|,
name|trackId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Blocks storage movement is FAILURE for the track"
operator|+
literal|" id {}. But the trackID doesn't exists in"
operator|+
literal|" storageMovementAttemptedItems list."
argument_list|,
name|trackId
argument_list|)
expr_stmt|;
name|blockStorageMovementNeeded
operator|.
name|removeItemTrackInfo
argument_list|(
name|itemInfo
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|SUCCESS
case|:
comment|// ItemInfo could be null. One case is, before the blocks movements
comment|// result arrives the attempted trackID became timed out and then
comment|// removed the trackID from the storageMovementAttemptedItems list.
comment|// TODO: Need to ensure that trackID is added to the
comment|// 'blockStorageMovementNeeded' queue for retries to handle the
comment|// following condition. If all the block locations under the trackID
comment|// are attempted and failed to find matching target nodes to satisfy
comment|// storage policy in previous SPS iteration.
name|String
name|msg
init|=
literal|"Blocks storage movement is SUCCESS for the track id: "
operator|+
name|trackId
operator|+
literal|" reported from co-ordinating datanode."
decl_stmt|;
if|if
condition|(
name|attemptedItemInfo
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|attemptedItemInfo
operator|.
name|isAllBlockLocsAttemptedToSatisfy
argument_list|()
condition|)
block|{
name|blockStorageMovementNeeded
operator|.
name|add
argument_list|(
operator|new
name|ItemInfo
argument_list|(
name|attemptedItemInfo
operator|.
name|getRootId
argument_list|()
argument_list|,
name|trackId
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"{} But adding trackID back to retry queue as some of"
operator|+
literal|" the blocks couldn't find matching target nodes in"
operator|+
literal|" previous SPS iteration."
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|blockStorageMovementNeeded
operator|.
name|removeItemTrackInfo
argument_list|(
name|itemInfo
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"{} But the trackID doesn't exists in "
operator|+
literal|"storageMovementAttemptedItems list"
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|blockStorageMovementNeeded
operator|.
name|removeItemTrackInfo
argument_list|(
name|itemInfo
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|IN_PROGRESS
case|:
name|isInprogress
operator|=
literal|true
expr_stmt|;
name|attemptedItemInfo
operator|=
name|storageMovementAttemptedItems
operator|.
name|get
argument_list|(
name|storageMovementAttemptedResult
operator|.
name|getTrackId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|attemptedItemInfo
operator|!=
literal|null
condition|)
block|{
comment|// update the attempted expiration time to next cycle.
name|attemptedItemInfo
operator|.
name|touchLastReportedTimeStamp
argument_list|()
expr_stmt|;
block|}
break|break;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Unknown status: {}"
argument_list|,
name|status
argument_list|)
expr_stmt|;
break|break;
block|}
comment|// Remove trackID from the attempted list if the attempt has been
comment|// completed(success or failure), if any.
if|if
condition|(
operator|!
name|isInprogress
condition|)
block|{
name|storageMovementAttemptedItems
operator|.
name|remove
argument_list|(
name|storageMovementAttemptedResult
operator|.
name|getTrackId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Remove trackID from results as processed above.
name|resultsIter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|resultsCount ()
specifier|public
name|int
name|resultsCount
parameter_list|()
block|{
return|return
name|storageMovementAttemptedResults
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getAttemptedItemsCount ()
specifier|public
name|int
name|getAttemptedItemsCount
parameter_list|()
block|{
return|return
name|storageMovementAttemptedItems
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|clearQueues ()
specifier|public
name|void
name|clearQueues
parameter_list|()
block|{
name|storageMovementAttemptedResults
operator|.
name|clear
argument_list|()
expr_stmt|;
name|storageMovementAttemptedItems
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

