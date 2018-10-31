begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.sps
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
operator|.
name|sps
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Queue
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
comment|/**  * A Class to track the block collection IDs (Inode's ID) for which physical  * storage movement needed as per the Namespace and StorageReports from DN.  * It scan the pending directories for which storage movement is required and  * schedule the block collection IDs for movement. It track the info of  * scheduled items and remove the SPS xAttr from the file/Directory once  * movement is success.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockStorageMovementNeeded
specifier|public
class|class
name|BlockStorageMovementNeeded
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BlockStorageMovementNeeded
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|storageMovementNeeded
specifier|private
specifier|final
name|Queue
argument_list|<
name|ItemInfo
argument_list|>
name|storageMovementNeeded
init|=
operator|new
name|LinkedList
argument_list|<
name|ItemInfo
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Map of startPath and number of child's. Number of child's indicate the    * number of files pending to satisfy the policy.    */
DECL|field|pendingWorkForDirectory
specifier|private
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|DirPendingWorkInfo
argument_list|>
name|pendingWorkForDirectory
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|ctxt
specifier|private
specifier|final
name|Context
name|ctxt
decl_stmt|;
DECL|field|pathIdCollector
specifier|private
name|Daemon
name|pathIdCollector
decl_stmt|;
DECL|field|pathIDProcessor
specifier|private
name|SPSPathIdProcessor
name|pathIDProcessor
decl_stmt|;
comment|// Amount of time to cache the SUCCESS status of path before turning it to
comment|// NOT_AVAILABLE.
DECL|field|statusClearanceElapsedTimeMs
specifier|private
specifier|static
name|long
name|statusClearanceElapsedTimeMs
init|=
literal|300000
decl_stmt|;
DECL|method|BlockStorageMovementNeeded (Context context)
specifier|public
name|BlockStorageMovementNeeded
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|this
operator|.
name|ctxt
operator|=
name|context
expr_stmt|;
name|pathIDProcessor
operator|=
operator|new
name|SPSPathIdProcessor
argument_list|()
expr_stmt|;
block|}
comment|/**    * Add the candidate to tracking list for which storage movement    * expected if necessary.    *    * @param trackInfo    *          - track info for satisfy the policy    */
DECL|method|add (ItemInfo trackInfo)
specifier|public
specifier|synchronized
name|void
name|add
parameter_list|(
name|ItemInfo
name|trackInfo
parameter_list|)
block|{
if|if
condition|(
name|trackInfo
operator|!=
literal|null
condition|)
block|{
name|storageMovementNeeded
operator|.
name|add
argument_list|(
name|trackInfo
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Add the itemInfo list to tracking list for which storage movement expected    * if necessary.    *    * @param startPath    *          - start path    * @param itemInfoList    *          - List of child in the directory    * @param scanCompleted    *          -Indicates whether the start id directory has no more elements to    *          scan.    */
annotation|@
name|VisibleForTesting
DECL|method|addAll (long startPath, List<ItemInfo> itemInfoList, boolean scanCompleted)
specifier|public
specifier|synchronized
name|void
name|addAll
parameter_list|(
name|long
name|startPath
parameter_list|,
name|List
argument_list|<
name|ItemInfo
argument_list|>
name|itemInfoList
parameter_list|,
name|boolean
name|scanCompleted
parameter_list|)
block|{
name|storageMovementNeeded
operator|.
name|addAll
argument_list|(
name|itemInfoList
argument_list|)
expr_stmt|;
name|updatePendingDirScanStats
argument_list|(
name|startPath
argument_list|,
name|itemInfoList
operator|.
name|size
argument_list|()
argument_list|,
name|scanCompleted
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add the itemInfo to tracking list for which storage movement expected if    * necessary.    *    * @param itemInfo    *          - child in the directory    * @param scanCompleted    *          -Indicates whether the ItemInfo start id directory has no more    *          elements to scan.    */
annotation|@
name|VisibleForTesting
DECL|method|add (ItemInfo itemInfo, boolean scanCompleted)
specifier|public
specifier|synchronized
name|void
name|add
parameter_list|(
name|ItemInfo
name|itemInfo
parameter_list|,
name|boolean
name|scanCompleted
parameter_list|)
block|{
name|storageMovementNeeded
operator|.
name|add
argument_list|(
name|itemInfo
argument_list|)
expr_stmt|;
comment|// This represents sps start id is file, so no need to update pending dir
comment|// stats.
if|if
condition|(
name|itemInfo
operator|.
name|getStartPath
argument_list|()
operator|==
name|itemInfo
operator|.
name|getFile
argument_list|()
condition|)
block|{
return|return;
block|}
name|updatePendingDirScanStats
argument_list|(
name|itemInfo
operator|.
name|getStartPath
argument_list|()
argument_list|,
literal|1
argument_list|,
name|scanCompleted
argument_list|)
expr_stmt|;
block|}
DECL|method|updatePendingDirScanStats (long startPath, int numScannedFiles, boolean scanCompleted)
specifier|private
name|void
name|updatePendingDirScanStats
parameter_list|(
name|long
name|startPath
parameter_list|,
name|int
name|numScannedFiles
parameter_list|,
name|boolean
name|scanCompleted
parameter_list|)
block|{
name|DirPendingWorkInfo
name|pendingWork
init|=
name|pendingWorkForDirectory
operator|.
name|get
argument_list|(
name|startPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|pendingWork
operator|==
literal|null
condition|)
block|{
name|pendingWork
operator|=
operator|new
name|DirPendingWorkInfo
argument_list|()
expr_stmt|;
name|pendingWorkForDirectory
operator|.
name|put
argument_list|(
name|startPath
argument_list|,
name|pendingWork
argument_list|)
expr_stmt|;
block|}
name|pendingWork
operator|.
name|addPendingWorkCount
argument_list|(
name|numScannedFiles
argument_list|)
expr_stmt|;
if|if
condition|(
name|scanCompleted
condition|)
block|{
name|pendingWork
operator|.
name|markScanCompleted
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Gets the satisfier files for which block storage movements check necessary    * and make the movement if required.    *    * @return satisfier files    */
DECL|method|get ()
specifier|public
specifier|synchronized
name|ItemInfo
name|get
parameter_list|()
block|{
return|return
name|storageMovementNeeded
operator|.
name|poll
argument_list|()
return|;
block|}
comment|/**    * Returns queue size.    */
DECL|method|size ()
specifier|public
specifier|synchronized
name|int
name|size
parameter_list|()
block|{
return|return
name|storageMovementNeeded
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|clearAll ()
specifier|public
specifier|synchronized
name|void
name|clearAll
parameter_list|()
block|{
name|storageMovementNeeded
operator|.
name|clear
argument_list|()
expr_stmt|;
name|pendingWorkForDirectory
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * Decrease the pending child count for directory once one file blocks moved    * successfully. Remove the SPS xAttr if pending child count is zero.    */
DECL|method|removeItemTrackInfo (ItemInfo trackInfo, boolean isSuccess)
specifier|public
specifier|synchronized
name|void
name|removeItemTrackInfo
parameter_list|(
name|ItemInfo
name|trackInfo
parameter_list|,
name|boolean
name|isSuccess
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|trackInfo
operator|.
name|isDir
argument_list|()
condition|)
block|{
comment|// If track is part of some start inode then reduce the pending
comment|// directory work count.
name|long
name|startId
init|=
name|trackInfo
operator|.
name|getStartPath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|ctxt
operator|.
name|isFileExist
argument_list|(
name|startId
argument_list|)
condition|)
block|{
comment|// directory deleted just remove it.
name|this
operator|.
name|pendingWorkForDirectory
operator|.
name|remove
argument_list|(
name|startId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DirPendingWorkInfo
name|pendingWork
init|=
name|pendingWorkForDirectory
operator|.
name|get
argument_list|(
name|startId
argument_list|)
decl_stmt|;
if|if
condition|(
name|pendingWork
operator|!=
literal|null
condition|)
block|{
name|pendingWork
operator|.
name|decrementPendingWorkCount
argument_list|()
expr_stmt|;
if|if
condition|(
name|pendingWork
operator|.
name|isDirWorkDone
argument_list|()
condition|)
block|{
name|ctxt
operator|.
name|removeSPSHint
argument_list|(
name|startId
argument_list|)
expr_stmt|;
name|pendingWorkForDirectory
operator|.
name|remove
argument_list|(
name|startId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
comment|// Remove xAttr if trackID doesn't exist in
comment|// storageMovementAttemptedItems or file policy satisfied.
name|ctxt
operator|.
name|removeSPSHint
argument_list|(
name|trackInfo
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Clean all the movements in spsDirsToBeTraveresed/storageMovementNeeded    * and notify to clean up required resources.    */
DECL|method|clearQueuesWithNotification ()
specifier|public
specifier|synchronized
name|void
name|clearQueuesWithNotification
parameter_list|()
block|{
comment|// Remove xAttr from directories
name|Long
name|trackId
decl_stmt|;
while|while
condition|(
operator|(
name|trackId
operator|=
name|ctxt
operator|.
name|getNextSPSPath
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
try|try
block|{
comment|// Remove xAttr for file
name|ctxt
operator|.
name|removeSPSHint
argument_list|(
name|trackId
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
literal|"Failed to remove SPS xattr for track id "
operator|+
name|trackId
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
block|}
comment|// File's directly added to storageMovementNeeded, So try to remove
comment|// xAttr for file
name|ItemInfo
name|itemInfo
decl_stmt|;
while|while
condition|(
operator|(
name|itemInfo
operator|=
name|get
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
try|try
block|{
comment|// Remove xAttr for file
if|if
condition|(
operator|!
name|itemInfo
operator|.
name|isDir
argument_list|()
condition|)
block|{
name|ctxt
operator|.
name|removeSPSHint
argument_list|(
name|itemInfo
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
literal|"Failed to remove SPS xattr for track id "
operator|+
name|itemInfo
operator|.
name|getFile
argument_list|()
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|clearAll
argument_list|()
expr_stmt|;
block|}
comment|/**    * Take dir tack ID from the spsDirsToBeTraveresed queue and collect child    * ID's to process for satisfy the policy.    */
DECL|class|SPSPathIdProcessor
specifier|private
class|class
name|SPSPathIdProcessor
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting SPSPathIdProcessor!."
argument_list|)
expr_stmt|;
name|Long
name|startINode
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|ctxt
operator|.
name|isRunning
argument_list|()
condition|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|ctxt
operator|.
name|isInSafeMode
argument_list|()
condition|)
block|{
if|if
condition|(
name|startINode
operator|==
literal|null
condition|)
block|{
name|startINode
operator|=
name|ctxt
operator|.
name|getNextSPSPath
argument_list|()
expr_stmt|;
block|}
comment|// else same id will be retried
if|if
condition|(
name|startINode
operator|==
literal|null
condition|)
block|{
comment|// Waiting for SPS path
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ctxt
operator|.
name|scanAndCollectFiles
argument_list|(
name|startINode
argument_list|)
expr_stmt|;
comment|// check if directory was empty and no child added to queue
name|DirPendingWorkInfo
name|dirPendingWorkInfo
init|=
name|pendingWorkForDirectory
operator|.
name|get
argument_list|(
name|startINode
argument_list|)
decl_stmt|;
if|if
condition|(
name|dirPendingWorkInfo
operator|!=
literal|null
operator|&&
name|dirPendingWorkInfo
operator|.
name|isDirWorkDone
argument_list|()
condition|)
block|{
name|ctxt
operator|.
name|removeSPSHint
argument_list|(
name|startINode
argument_list|)
expr_stmt|;
name|pendingWorkForDirectory
operator|.
name|remove
argument_list|(
name|startINode
argument_list|)
expr_stmt|;
block|}
block|}
name|startINode
operator|=
literal|null
expr_stmt|;
comment|// Current inode successfully scanned.
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|String
name|reClass
init|=
name|t
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|InterruptedException
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|reClass
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"SPSPathIdProcessor thread is interrupted. Stopping.."
argument_list|)
expr_stmt|;
break|break;
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while scanning file inodes to satisfy the policy"
argument_list|,
name|t
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Interrupted while waiting in SPSPathIdProcessor"
argument_list|,
name|t
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
comment|/**    * Info for directory recursive scan.    */
DECL|class|DirPendingWorkInfo
specifier|public
specifier|static
class|class
name|DirPendingWorkInfo
block|{
DECL|field|pendingWorkCount
specifier|private
name|int
name|pendingWorkCount
init|=
literal|0
decl_stmt|;
DECL|field|fullyScanned
specifier|private
name|boolean
name|fullyScanned
init|=
literal|false
decl_stmt|;
comment|/**      * Increment the pending work count for directory.      */
DECL|method|addPendingWorkCount (int count)
specifier|public
specifier|synchronized
name|void
name|addPendingWorkCount
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|this
operator|.
name|pendingWorkCount
operator|=
name|this
operator|.
name|pendingWorkCount
operator|+
name|count
expr_stmt|;
block|}
comment|/**      * Decrement the pending work count for directory one track info is      * completed.      */
DECL|method|decrementPendingWorkCount ()
specifier|public
specifier|synchronized
name|void
name|decrementPendingWorkCount
parameter_list|()
block|{
name|this
operator|.
name|pendingWorkCount
operator|--
expr_stmt|;
block|}
comment|/**      * Return true if all the pending work is done and directory fully      * scanned, otherwise false.      */
DECL|method|isDirWorkDone ()
specifier|public
specifier|synchronized
name|boolean
name|isDirWorkDone
parameter_list|()
block|{
return|return
operator|(
name|pendingWorkCount
operator|<=
literal|0
operator|&&
name|fullyScanned
operator|)
return|;
block|}
comment|/**      * Mark directory scan is completed.      */
DECL|method|markScanCompleted ()
specifier|public
specifier|synchronized
name|void
name|markScanCompleted
parameter_list|()
block|{
name|this
operator|.
name|fullyScanned
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|activate ()
specifier|public
name|void
name|activate
parameter_list|()
block|{
name|pathIdCollector
operator|=
operator|new
name|Daemon
argument_list|(
name|pathIDProcessor
argument_list|)
expr_stmt|;
name|pathIdCollector
operator|.
name|setName
argument_list|(
literal|"SPSPathIdProcessor"
argument_list|)
expr_stmt|;
name|pathIdCollector
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|pathIdCollector
operator|!=
literal|null
condition|)
block|{
name|pathIdCollector
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|setStatusClearanceElapsedTimeMs ( long statusClearanceElapsedTimeMs)
specifier|public
specifier|static
name|void
name|setStatusClearanceElapsedTimeMs
parameter_list|(
name|long
name|statusClearanceElapsedTimeMs
parameter_list|)
block|{
name|BlockStorageMovementNeeded
operator|.
name|statusClearanceElapsedTimeMs
operator|=
name|statusClearanceElapsedTimeMs
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getStatusClearanceElapsedTimeMs ()
specifier|public
specifier|static
name|long
name|getStatusClearanceElapsedTimeMs
parameter_list|()
block|{
return|return
name|statusClearanceElapsedTimeMs
return|;
block|}
DECL|method|markScanCompletedForDir (long inode)
specifier|public
name|void
name|markScanCompletedForDir
parameter_list|(
name|long
name|inode
parameter_list|)
block|{
name|DirPendingWorkInfo
name|pendingWork
init|=
name|pendingWorkForDirectory
operator|.
name|get
argument_list|(
name|inode
argument_list|)
decl_stmt|;
if|if
condition|(
name|pendingWork
operator|!=
literal|null
condition|)
block|{
name|pendingWork
operator|.
name|markScanCompleted
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

