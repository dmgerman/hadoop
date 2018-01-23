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
name|ArrayList
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
name|DFSConfigKeys
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
name|HdfsFileStatus
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
name|FSDirectory
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
name|FSTreeTraverser
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
name|INode
import|;
end_import

begin_comment
comment|/**  * A specific implementation for scanning the directory with Namenode internal  * Inode structure and collects the file ids under the given directory ID.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|IntraSPSNameNodeFileIdCollector
specifier|public
class|class
name|IntraSPSNameNodeFileIdCollector
extends|extends
name|FSTreeTraverser
implements|implements
name|FileIdCollector
block|{
DECL|field|maxQueueLimitToScan
specifier|private
name|int
name|maxQueueLimitToScan
decl_stmt|;
DECL|field|service
specifier|private
specifier|final
name|SPSService
name|service
decl_stmt|;
DECL|field|remainingCapacity
specifier|private
name|int
name|remainingCapacity
init|=
literal|0
decl_stmt|;
DECL|field|currentBatch
specifier|private
name|List
argument_list|<
name|ItemInfo
argument_list|>
name|currentBatch
decl_stmt|;
DECL|method|IntraSPSNameNodeFileIdCollector (FSDirectory dir, SPSService service)
specifier|public
name|IntraSPSNameNodeFileIdCollector
parameter_list|(
name|FSDirectory
name|dir
parameter_list|,
name|SPSService
name|service
parameter_list|)
block|{
name|super
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|this
operator|.
name|service
operator|=
name|service
expr_stmt|;
name|this
operator|.
name|maxQueueLimitToScan
operator|=
name|service
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_STORAGE_POLICY_SATISFIER_QUEUE_LIMIT_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_STORAGE_POLICY_SATISFIER_QUEUE_LIMIT_DEFAULT
argument_list|)
expr_stmt|;
name|currentBatch
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|maxQueueLimitToScan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processFileInode (INode inode, TraverseInfo traverseInfo)
specifier|protected
name|boolean
name|processFileInode
parameter_list|(
name|INode
name|inode
parameter_list|,
name|TraverseInfo
name|traverseInfo
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Processing {} for statisy the policy"
argument_list|,
name|inode
operator|.
name|getFullPathName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|inode
operator|.
name|isFile
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|inode
operator|.
name|isFile
argument_list|()
operator|&&
name|inode
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
name|currentBatch
operator|.
name|add
argument_list|(
operator|new
name|ItemInfo
argument_list|(
operator|(
operator|(
name|SPSTraverseInfo
operator|)
name|traverseInfo
operator|)
operator|.
name|getStartId
argument_list|()
argument_list|,
name|inode
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|remainingCapacity
operator|--
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|canSubmitCurrentBatch ()
specifier|protected
name|boolean
name|canSubmitCurrentBatch
parameter_list|()
block|{
return|return
name|remainingCapacity
operator|<=
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|checkINodeReady (long startId)
specifier|protected
name|void
name|checkINodeReady
parameter_list|(
name|long
name|startId
parameter_list|)
throws|throws
name|IOException
block|{
comment|// SPS work won't be scheduled if NN is in standby. So, skipping NN
comment|// standby check.
return|return;
block|}
annotation|@
name|Override
DECL|method|submitCurrentBatch (long startId)
specifier|protected
name|void
name|submitCurrentBatch
parameter_list|(
name|long
name|startId
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// Add current child's to queue
name|service
operator|.
name|addAllFileIdsToProcess
argument_list|(
name|startId
argument_list|,
name|currentBatch
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|currentBatch
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|throttle ()
specifier|protected
name|void
name|throttle
parameter_list|()
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"StorageMovementNeeded queue remaining capacity is zero,"
operator|+
literal|" waiting for some free slots."
argument_list|)
expr_stmt|;
block|}
name|remainingCapacity
operator|=
name|remainingCapacity
argument_list|()
expr_stmt|;
comment|// wait for queue to be free
while|while
condition|(
name|remainingCapacity
operator|<=
literal|0
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Waiting for storageMovementNeeded queue to be free!"
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|remainingCapacity
operator|=
name|remainingCapacity
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|canTraverseDir (INode inode)
specifier|protected
name|boolean
name|canTraverseDir
parameter_list|(
name|INode
name|inode
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|checkPauseForTesting ()
specifier|protected
name|void
name|checkPauseForTesting
parameter_list|()
throws|throws
name|InterruptedException
block|{
comment|// Nothing to do
block|}
annotation|@
name|Override
DECL|method|scanAndCollectFileIds (final Long startINodeId)
specifier|public
name|void
name|scanAndCollectFileIds
parameter_list|(
specifier|final
name|Long
name|startINodeId
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|FSDirectory
name|fsd
init|=
name|getFSDirectory
argument_list|()
decl_stmt|;
name|INode
name|startInode
init|=
name|fsd
operator|.
name|getInode
argument_list|(
name|startINodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|startInode
operator|!=
literal|null
condition|)
block|{
name|remainingCapacity
operator|=
name|remainingCapacity
argument_list|()
expr_stmt|;
if|if
condition|(
name|remainingCapacity
operator|==
literal|0
condition|)
block|{
name|throttle
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|startInode
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|currentBatch
operator|.
name|add
argument_list|(
operator|new
name|ItemInfo
argument_list|(
name|startInode
operator|.
name|getId
argument_list|()
argument_list|,
name|startInode
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|readLock
argument_list|()
expr_stmt|;
comment|// NOTE: this lock will not be held for full directory scanning. It is
comment|// basically a sliced locking. Once it collects a batch size( at max the
comment|// size of maxQueueLimitToScan (default 1000)) file ids, then it will
comment|// unlock and submits the current batch to SPSService. Once
comment|// service.processingQueueSize() shows empty slots, then lock will be
comment|// re-acquired and scan will be resumed. This logic was re-used from
comment|// EDEK feature.
try|try
block|{
name|traverseDir
argument_list|(
name|startInode
operator|.
name|asDirectory
argument_list|()
argument_list|,
name|startINodeId
argument_list|,
name|HdfsFileStatus
operator|.
name|EMPTY_NAME
argument_list|,
operator|new
name|SPSTraverseInfo
argument_list|(
name|startINodeId
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|readUnlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Mark startInode traverse is done, this is last-batch
name|service
operator|.
name|addAllFileIdsToProcess
argument_list|(
name|startInode
operator|.
name|getId
argument_list|()
argument_list|,
name|currentBatch
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|currentBatch
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns queue remaining capacity.    */
DECL|method|remainingCapacity ()
specifier|public
specifier|synchronized
name|int
name|remainingCapacity
parameter_list|()
block|{
name|int
name|size
init|=
name|service
operator|.
name|processingQueueSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>=
name|maxQueueLimitToScan
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
operator|(
name|maxQueueLimitToScan
operator|-
name|size
operator|)
return|;
block|}
block|}
DECL|class|SPSTraverseInfo
class|class
name|SPSTraverseInfo
extends|extends
name|TraverseInfo
block|{
DECL|field|startId
specifier|private
name|long
name|startId
decl_stmt|;
DECL|method|SPSTraverseInfo (long startId)
name|SPSTraverseInfo
parameter_list|(
name|long
name|startId
parameter_list|)
block|{
name|this
operator|.
name|startId
operator|=
name|startId
expr_stmt|;
block|}
DECL|method|getStartId ()
specifier|public
name|long
name|getStartId
parameter_list|()
block|{
return|return
name|startId
return|;
block|}
block|}
block|}
end_class

end_unit

