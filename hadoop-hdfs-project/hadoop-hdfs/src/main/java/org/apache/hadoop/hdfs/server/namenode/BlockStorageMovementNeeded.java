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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
operator|.
name|XATTR_SATISFY_STORAGE_POLICY
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
name|namenode
operator|.
name|snapshot
operator|.
name|Snapshot
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
comment|/**    * Map of rootId and number of child's. Number of child's indicate the number    * of files pending to satisfy the policy.    */
DECL|field|pendingWorkForDirectory
specifier|private
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|Integer
argument_list|>
name|pendingWorkForDirectory
init|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|namesystem
specifier|private
specifier|final
name|Namesystem
name|namesystem
decl_stmt|;
comment|// List of pending dir to satisfy the policy
DECL|field|spsDirsToBeTraveresed
specifier|private
specifier|final
name|Queue
argument_list|<
name|Long
argument_list|>
name|spsDirsToBeTraveresed
init|=
operator|new
name|LinkedList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|sps
specifier|private
specifier|final
name|StoragePolicySatisfier
name|sps
decl_stmt|;
DECL|field|fileInodeIdCollector
specifier|private
name|Daemon
name|fileInodeIdCollector
decl_stmt|;
DECL|method|BlockStorageMovementNeeded (Namesystem namesystem, StoragePolicySatisfier sps)
specifier|public
name|BlockStorageMovementNeeded
parameter_list|(
name|Namesystem
name|namesystem
parameter_list|,
name|StoragePolicySatisfier
name|sps
parameter_list|)
block|{
name|this
operator|.
name|namesystem
operator|=
name|namesystem
expr_stmt|;
name|this
operator|.
name|sps
operator|=
name|sps
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
name|storageMovementNeeded
operator|.
name|add
argument_list|(
name|trackInfo
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add the itemInfo to tracking list for which storage movement    * expected if necessary.    * @param rootId    *            - root inode id    * @param itemInfoList    *            - List of child in the directory    */
DECL|method|addAll (Long rootId, List<ItemInfo> itemInfoList)
specifier|private
specifier|synchronized
name|void
name|addAll
parameter_list|(
name|Long
name|rootId
parameter_list|,
name|List
argument_list|<
name|ItemInfo
argument_list|>
name|itemInfoList
parameter_list|)
block|{
name|storageMovementNeeded
operator|.
name|addAll
argument_list|(
name|itemInfoList
argument_list|)
expr_stmt|;
name|pendingWorkForDirectory
operator|.
name|put
argument_list|(
name|rootId
argument_list|,
name|itemInfoList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gets the block collection id for which storage movements check necessary    * and make the movement if required.    *    * @return block collection ID    */
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
DECL|method|addToPendingDirQueue (long id)
specifier|public
specifier|synchronized
name|void
name|addToPendingDirQueue
parameter_list|(
name|long
name|id
parameter_list|)
block|{
name|spsDirsToBeTraveresed
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
comment|// Notify waiting FileInodeIdCollector thread about the newly
comment|// added SPS path.
synchronized|synchronized
init|(
name|spsDirsToBeTraveresed
init|)
block|{
name|spsDirsToBeTraveresed
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|clearAll ()
specifier|public
specifier|synchronized
name|void
name|clearAll
parameter_list|()
block|{
name|spsDirsToBeTraveresed
operator|.
name|clear
argument_list|()
expr_stmt|;
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
DECL|method|removeItemTrackInfo (ItemInfo trackInfo)
specifier|public
specifier|synchronized
name|void
name|removeItemTrackInfo
parameter_list|(
name|ItemInfo
name|trackInfo
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
comment|// If track is part of some root then reduce the pending directory work
comment|// count.
name|long
name|rootId
init|=
name|trackInfo
operator|.
name|getRootId
argument_list|()
decl_stmt|;
name|INode
name|inode
init|=
name|namesystem
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|getInode
argument_list|(
name|rootId
argument_list|)
decl_stmt|;
if|if
condition|(
name|inode
operator|==
literal|null
condition|)
block|{
comment|// directory deleted just remove it.
name|this
operator|.
name|pendingWorkForDirectory
operator|.
name|remove
argument_list|(
name|rootId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|pendingWorkForDirectory
operator|.
name|get
argument_list|(
name|rootId
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|Integer
name|pendingWork
init|=
name|pendingWorkForDirectory
operator|.
name|get
argument_list|(
name|rootId
argument_list|)
operator|-
literal|1
decl_stmt|;
name|pendingWorkForDirectory
operator|.
name|put
argument_list|(
name|rootId
argument_list|,
name|pendingWork
argument_list|)
expr_stmt|;
if|if
condition|(
name|pendingWork
operator|<=
literal|0
condition|)
block|{
name|namesystem
operator|.
name|removeXattr
argument_list|(
name|rootId
argument_list|,
name|XATTR_SATISFY_STORAGE_POLICY
argument_list|)
expr_stmt|;
name|pendingWorkForDirectory
operator|.
name|remove
argument_list|(
name|rootId
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
name|namesystem
operator|.
name|removeXattr
argument_list|(
name|trackInfo
operator|.
name|getTrackId
argument_list|()
argument_list|,
name|XATTR_SATISFY_STORAGE_POLICY
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|clearQueue (long trackId)
specifier|public
specifier|synchronized
name|void
name|clearQueue
parameter_list|(
name|long
name|trackId
parameter_list|)
block|{
name|spsDirsToBeTraveresed
operator|.
name|remove
argument_list|(
name|trackId
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|ItemInfo
argument_list|>
name|iterator
init|=
name|storageMovementNeeded
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ItemInfo
name|next
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|.
name|getRootId
argument_list|()
operator|==
name|trackId
condition|)
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
name|pendingWorkForDirectory
operator|.
name|remove
argument_list|(
name|trackId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Clean all the movements in spsDirsToBeTraveresed/storageMovementNeeded    * and notify to clean up required resources.    * @throws IOException    */
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
name|spsDirsToBeTraveresed
operator|.
name|poll
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
try|try
block|{
comment|// Remove xAttr for file
name|namesystem
operator|.
name|removeXattr
argument_list|(
name|trackId
argument_list|,
name|XATTR_SATISFY_STORAGE_POLICY
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
name|storageMovementNeeded
operator|.
name|poll
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
name|namesystem
operator|.
name|removeXattr
argument_list|(
name|itemInfo
operator|.
name|getTrackId
argument_list|()
argument_list|,
name|XATTR_SATISFY_STORAGE_POLICY
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
name|getTrackId
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
DECL|class|FileInodeIdCollector
specifier|private
class|class
name|FileInodeIdCollector
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
literal|"Starting FileInodeIdCollector!."
argument_list|)
expr_stmt|;
while|while
condition|(
name|namesystem
operator|.
name|isRunning
argument_list|()
operator|&&
name|sps
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
name|namesystem
operator|.
name|isInSafeMode
argument_list|()
condition|)
block|{
name|FSDirectory
name|fsd
init|=
name|namesystem
operator|.
name|getFSDirectory
argument_list|()
decl_stmt|;
name|Long
name|rootINodeId
init|=
name|spsDirsToBeTraveresed
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|rootINodeId
operator|==
literal|null
condition|)
block|{
comment|// Waiting for SPS path
synchronized|synchronized
init|(
name|spsDirsToBeTraveresed
init|)
block|{
name|spsDirsToBeTraveresed
operator|.
name|wait
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|INode
name|rootInode
init|=
name|fsd
operator|.
name|getInode
argument_list|(
name|rootINodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|rootInode
operator|!=
literal|null
condition|)
block|{
comment|// TODO : HDFS-12291
comment|// 1. Implement an efficient recursive directory iteration
comment|// mechanism and satisfies storage policy for all the files
comment|// under the given directory.
comment|// 2. Process files in batches,so datanodes workload can be
comment|// handled.
name|List
argument_list|<
name|ItemInfo
argument_list|>
name|itemInfoList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|INode
name|childInode
range|:
name|rootInode
operator|.
name|asDirectory
argument_list|()
operator|.
name|getChildrenList
argument_list|(
name|Snapshot
operator|.
name|CURRENT_STATE_ID
argument_list|)
control|)
block|{
if|if
condition|(
name|childInode
operator|.
name|isFile
argument_list|()
operator|&&
name|childInode
operator|.
name|asFile
argument_list|()
operator|.
name|numBlocks
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|itemInfoList
operator|.
name|add
argument_list|(
operator|new
name|ItemInfo
argument_list|(
name|rootINodeId
argument_list|,
name|childInode
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|itemInfoList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// satisfy track info is empty, so remove the xAttr from the
comment|// directory
name|namesystem
operator|.
name|removeXattr
argument_list|(
name|rootINodeId
argument_list|,
name|XATTR_SATISFY_STORAGE_POLICY
argument_list|)
expr_stmt|;
block|}
name|addAll
argument_list|(
name|rootINodeId
argument_list|,
name|itemInfoList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while loading inodes to satisfy the policy"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|fileInodeIdCollector
operator|=
operator|new
name|Daemon
argument_list|(
operator|new
name|FileInodeIdCollector
argument_list|()
argument_list|)
expr_stmt|;
name|fileInodeIdCollector
operator|.
name|setName
argument_list|(
literal|"FileInodeIdCollector"
argument_list|)
expr_stmt|;
name|fileInodeIdCollector
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|fileInodeIdCollector
operator|!=
literal|null
condition|)
block|{
name|fileInodeIdCollector
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

