begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.snapshot
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
name|snapshot
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
name|blockmanagement
operator|.
name|BlockInfo
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
operator|.
name|BlocksMapINodeUpdateEntry
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
operator|.
name|BlocksMapUpdateInfo
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
name|INodeFile
import|;
end_import

begin_comment
comment|/**  * {@link INodeFile} with a link to the next element.  * The link of all the snapshot files and the original file form a circular  * linked list so that all elements are accessible by any of the elements.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|FileWithSnapshot
specifier|public
interface|interface
name|FileWithSnapshot
block|{
comment|/**    * The difference of an {@link INodeFile} between two snapshots.    */
DECL|class|FileDiff
specifier|static
class|class
name|FileDiff
extends|extends
name|AbstractINodeDiff
argument_list|<
name|INodeFile
argument_list|,
name|FileDiff
argument_list|>
block|{
comment|/** The file size at snapshot creation time. */
DECL|field|fileSize
specifier|final
name|long
name|fileSize
decl_stmt|;
DECL|method|FileDiff (Snapshot snapshot, INodeFile file)
name|FileDiff
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|,
name|INodeFile
name|file
parameter_list|)
block|{
name|super
argument_list|(
name|snapshot
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fileSize
operator|=
name|file
operator|.
name|computeFileSize
argument_list|(
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createSnapshotCopyOfCurrentINode (INodeFile currentINode)
name|INodeFile
name|createSnapshotCopyOfCurrentINode
parameter_list|(
name|INodeFile
name|currentINode
parameter_list|)
block|{
specifier|final
name|INodeFile
name|copy
init|=
operator|new
name|INodeFile
argument_list|(
name|currentINode
argument_list|)
decl_stmt|;
name|copy
operator|.
name|setBlocks
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
name|copy
return|;
block|}
annotation|@
name|Override
DECL|method|combinePosteriorAndCollectBlocks (INodeFile currentINode, FileDiff posterior, BlocksMapUpdateInfo collectedBlocks)
name|void
name|combinePosteriorAndCollectBlocks
parameter_list|(
name|INodeFile
name|currentINode
parameter_list|,
name|FileDiff
name|posterior
parameter_list|,
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|)
block|{
name|Util
operator|.
name|collectBlocksAndClear
argument_list|(
operator|(
name|FileWithSnapshot
operator|)
name|currentINode
argument_list|,
name|collectedBlocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|" fileSize="
operator|+
name|fileSize
operator|+
literal|", rep="
operator|+
operator|(
name|snapshotINode
operator|==
literal|null
condition|?
literal|"?"
else|:
name|snapshotINode
operator|.
name|getFileReplication
argument_list|()
operator|)
return|;
block|}
block|}
comment|/** @return the {@link INodeFile} view of this object. */
DECL|method|asINodeFile ()
specifier|public
name|INodeFile
name|asINodeFile
parameter_list|()
function_decl|;
comment|/** @return the next element. */
DECL|method|getNext ()
specifier|public
name|FileWithSnapshot
name|getNext
parameter_list|()
function_decl|;
comment|/** Set the next element. */
DECL|method|setNext (FileWithSnapshot next)
specifier|public
name|void
name|setNext
parameter_list|(
name|FileWithSnapshot
name|next
parameter_list|)
function_decl|;
comment|/** Insert inode to the circular linked list, after the current node. */
DECL|method|insertAfter (FileWithSnapshot inode)
specifier|public
name|void
name|insertAfter
parameter_list|(
name|FileWithSnapshot
name|inode
parameter_list|)
function_decl|;
comment|/** Insert inode to the circular linked list, before the current node. */
DECL|method|insertBefore (FileWithSnapshot inode)
specifier|public
name|void
name|insertBefore
parameter_list|(
name|FileWithSnapshot
name|inode
parameter_list|)
function_decl|;
comment|/** Remove self from the circular list */
DECL|method|removeSelf ()
specifier|public
name|void
name|removeSelf
parameter_list|()
function_decl|;
comment|/** Is the current file deleted? */
DECL|method|isCurrentFileDeleted ()
specifier|public
name|boolean
name|isCurrentFileDeleted
parameter_list|()
function_decl|;
comment|/** Are the current file and all snapshot copies deleted? */
DECL|method|isEverythingDeleted ()
specifier|public
name|boolean
name|isEverythingDeleted
parameter_list|()
function_decl|;
comment|/** @return the max file replication in the inode and its snapshot copies. */
DECL|method|getMaxFileReplication ()
specifier|public
name|short
name|getMaxFileReplication
parameter_list|()
function_decl|;
comment|/** @return the max file size in the inode and its snapshot copies. */
DECL|method|computeMaxFileSize ()
specifier|public
name|long
name|computeMaxFileSize
parameter_list|()
function_decl|;
comment|/** Utility methods for the classes which implement the interface. */
DECL|class|Util
specifier|public
specifier|static
class|class
name|Util
block|{
comment|/** @return The previous node in the circular linked list */
DECL|method|getPrevious (FileWithSnapshot file)
specifier|static
name|FileWithSnapshot
name|getPrevious
parameter_list|(
name|FileWithSnapshot
name|file
parameter_list|)
block|{
name|FileWithSnapshot
name|previous
init|=
name|file
operator|.
name|getNext
argument_list|()
decl_stmt|;
while|while
condition|(
name|previous
operator|.
name|getNext
argument_list|()
operator|!=
name|file
condition|)
block|{
name|previous
operator|=
name|previous
operator|.
name|getNext
argument_list|()
expr_stmt|;
block|}
return|return
name|previous
return|;
block|}
comment|/** Replace the old file with the new file in the circular linked list. */
DECL|method|replace (FileWithSnapshot oldFile, FileWithSnapshot newFile)
specifier|static
name|void
name|replace
parameter_list|(
name|FileWithSnapshot
name|oldFile
parameter_list|,
name|FileWithSnapshot
name|newFile
parameter_list|)
block|{
specifier|final
name|FileWithSnapshot
name|oldNext
init|=
name|oldFile
operator|.
name|getNext
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldNext
operator|==
literal|null
condition|)
block|{
name|newFile
operator|.
name|setNext
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|oldNext
operator|!=
name|oldFile
condition|)
block|{
name|newFile
operator|.
name|setNext
argument_list|(
name|oldNext
argument_list|)
expr_stmt|;
name|getPrevious
argument_list|(
name|oldFile
argument_list|)
operator|.
name|setNext
argument_list|(
name|newFile
argument_list|)
expr_stmt|;
block|}
name|oldFile
operator|.
name|setNext
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** @return the max file replication of the file in the diff list. */
specifier|static
parameter_list|<
name|N
extends|extends
name|INodeFile
parameter_list|,
name|D
extends|extends
name|AbstractINodeDiff
argument_list|<
name|N
argument_list|,
name|D
argument_list|>
parameter_list|>
DECL|method|getMaxFileReplication (short max, final AbstractINodeDiffList<N, D> diffs)
name|short
name|getMaxFileReplication
parameter_list|(
name|short
name|max
parameter_list|,
specifier|final
name|AbstractINodeDiffList
argument_list|<
name|N
argument_list|,
name|D
argument_list|>
name|diffs
parameter_list|)
block|{
for|for
control|(
name|AbstractINodeDiff
argument_list|<
name|N
argument_list|,
name|D
argument_list|>
name|d
range|:
name|diffs
control|)
block|{
if|if
condition|(
name|d
operator|.
name|snapshotINode
operator|!=
literal|null
condition|)
block|{
specifier|final
name|short
name|replication
init|=
name|d
operator|.
name|snapshotINode
operator|.
name|getFileReplication
argument_list|()
decl_stmt|;
if|if
condition|(
name|replication
operator|>
name|max
condition|)
block|{
name|max
operator|=
name|replication
expr_stmt|;
block|}
block|}
block|}
return|return
name|max
return|;
block|}
comment|/**      * @return the max file replication of the elements      *         in the circular linked list.      */
DECL|method|getBlockReplication (final FileWithSnapshot file)
specifier|static
name|short
name|getBlockReplication
parameter_list|(
specifier|final
name|FileWithSnapshot
name|file
parameter_list|)
block|{
name|short
name|max
init|=
name|file
operator|.
name|getMaxFileReplication
argument_list|()
decl_stmt|;
comment|// i may be null since next will be set to null when the INode is deleted
for|for
control|(
name|FileWithSnapshot
name|i
init|=
name|file
operator|.
name|getNext
argument_list|()
init|;
name|i
operator|!=
name|file
operator|&&
name|i
operator|!=
literal|null
condition|;
name|i
operator|=
name|i
operator|.
name|getNext
argument_list|()
control|)
block|{
specifier|final
name|short
name|replication
init|=
name|i
operator|.
name|getMaxFileReplication
argument_list|()
decl_stmt|;
if|if
condition|(
name|replication
operator|>
name|max
condition|)
block|{
name|max
operator|=
name|replication
expr_stmt|;
block|}
block|}
return|return
name|max
return|;
block|}
comment|/**      * If some blocks at the end of the block list no longer belongs to      * any inode, collect them and update the block list.      */
DECL|method|collectBlocksAndClear (final FileWithSnapshot file, final BlocksMapUpdateInfo info)
specifier|static
name|void
name|collectBlocksAndClear
parameter_list|(
specifier|final
name|FileWithSnapshot
name|file
parameter_list|,
specifier|final
name|BlocksMapUpdateInfo
name|info
parameter_list|)
block|{
specifier|final
name|FileWithSnapshot
name|next
init|=
name|file
operator|.
name|getNext
argument_list|()
decl_stmt|;
comment|// find max file size, max replication and the last inode.
name|long
name|maxFileSize
init|=
name|file
operator|.
name|computeMaxFileSize
argument_list|()
decl_stmt|;
name|short
name|maxReplication
init|=
name|file
operator|.
name|getMaxFileReplication
argument_list|()
decl_stmt|;
name|FileWithSnapshot
name|last
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
operator|&&
name|next
operator|!=
name|file
condition|)
block|{
for|for
control|(
name|FileWithSnapshot
name|i
init|=
name|next
init|;
name|i
operator|!=
name|file
condition|;
name|i
operator|=
name|i
operator|.
name|getNext
argument_list|()
control|)
block|{
specifier|final
name|long
name|size
init|=
name|i
operator|.
name|computeMaxFileSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
name|maxFileSize
condition|)
block|{
name|maxFileSize
operator|=
name|size
expr_stmt|;
block|}
specifier|final
name|short
name|rep
init|=
name|i
operator|.
name|getMaxFileReplication
argument_list|()
decl_stmt|;
if|if
condition|(
name|rep
operator|>
name|maxReplication
condition|)
block|{
name|maxReplication
operator|=
name|rep
expr_stmt|;
block|}
name|last
operator|=
name|i
expr_stmt|;
block|}
block|}
name|collectBlocksBeyondMax
argument_list|(
name|file
argument_list|,
name|maxFileSize
argument_list|,
name|info
argument_list|)
expr_stmt|;
if|if
condition|(
name|file
operator|.
name|isEverythingDeleted
argument_list|()
condition|)
block|{
comment|// Set the replication of the current INode to the max of all the other
comment|// linked INodes, so that in case the current INode is retrieved from the
comment|// blocksMap before it is removed or updated, the correct replication
comment|// number can be retrieved.
if|if
condition|(
name|maxReplication
operator|>
literal|0
condition|)
block|{
name|file
operator|.
name|asINodeFile
argument_list|()
operator|.
name|setFileReplication
argument_list|(
name|maxReplication
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// remove the file from the circular linked list.
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
name|last
operator|.
name|setNext
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
name|file
operator|.
name|setNext
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|file
operator|.
name|asINodeFile
argument_list|()
operator|.
name|setBlocks
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|collectBlocksBeyondMax (final FileWithSnapshot file, final long max, final BlocksMapUpdateInfo collectedBlocks)
specifier|private
specifier|static
name|void
name|collectBlocksBeyondMax
parameter_list|(
specifier|final
name|FileWithSnapshot
name|file
parameter_list|,
specifier|final
name|long
name|max
parameter_list|,
specifier|final
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|)
block|{
specifier|final
name|BlockInfo
index|[]
name|oldBlocks
init|=
name|file
operator|.
name|asINodeFile
argument_list|()
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldBlocks
operator|!=
literal|null
condition|)
block|{
comment|//find the minimum n such that the size of the first n blocks> max
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|long
name|size
init|=
literal|0
init|;
name|n
argument_list|<
name|oldBlocks
operator|.
name|length
operator|&&
name|max
argument_list|>
name|size
condition|;
name|n
operator|++
control|)
block|{
name|size
operator|+=
name|oldBlocks
index|[
name|n
index|]
operator|.
name|getNumBytes
argument_list|()
expr_stmt|;
block|}
comment|// collect update blocks
specifier|final
name|FileWithSnapshot
name|next
init|=
name|file
operator|.
name|getNext
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
operator|&&
name|next
operator|!=
name|file
operator|&&
name|file
operator|.
name|isEverythingDeleted
argument_list|()
operator|&&
name|collectedBlocks
operator|!=
literal|null
condition|)
block|{
specifier|final
name|BlocksMapINodeUpdateEntry
name|entry
init|=
operator|new
name|BlocksMapINodeUpdateEntry
argument_list|(
name|file
operator|.
name|asINodeFile
argument_list|()
argument_list|,
name|next
operator|.
name|asINodeFile
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|collectedBlocks
operator|.
name|addUpdateBlock
argument_list|(
name|oldBlocks
index|[
name|i
index|]
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
comment|// starting from block n, the data is beyond max.
if|if
condition|(
name|n
operator|<
name|oldBlocks
operator|.
name|length
condition|)
block|{
comment|// resize the array.
specifier|final
name|BlockInfo
index|[]
name|newBlocks
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|0
condition|)
block|{
name|newBlocks
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|newBlocks
operator|=
operator|new
name|BlockInfo
index|[
name|n
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|oldBlocks
argument_list|,
literal|0
argument_list|,
name|newBlocks
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
comment|// set new blocks
name|file
operator|.
name|asINodeFile
argument_list|()
operator|.
name|setBlocks
argument_list|(
name|newBlocks
argument_list|)
expr_stmt|;
for|for
control|(
name|FileWithSnapshot
name|i
init|=
name|next
init|;
name|i
operator|!=
literal|null
operator|&&
name|i
operator|!=
name|file
condition|;
name|i
operator|=
name|i
operator|.
name|getNext
argument_list|()
control|)
block|{
name|i
operator|.
name|asINodeFile
argument_list|()
operator|.
name|setBlocks
argument_list|(
name|newBlocks
argument_list|)
expr_stmt|;
block|}
comment|// collect the blocks beyond max.
if|if
condition|(
name|collectedBlocks
operator|!=
literal|null
condition|)
block|{
for|for
control|(
init|;
name|n
operator|<
name|oldBlocks
operator|.
name|length
condition|;
name|n
operator|++
control|)
block|{
name|collectedBlocks
operator|.
name|addDeleteBlock
argument_list|(
name|oldBlocks
index|[
name|n
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|circularListString (final FileWithSnapshot file)
specifier|static
name|String
name|circularListString
parameter_list|(
specifier|final
name|FileWithSnapshot
name|file
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"* -> "
argument_list|)
operator|.
name|append
argument_list|(
name|file
operator|.
name|asINodeFile
argument_list|()
operator|.
name|getObjectString
argument_list|()
argument_list|)
decl_stmt|;
name|FileWithSnapshot
name|n
init|=
name|file
operator|.
name|getNext
argument_list|()
decl_stmt|;
for|for
control|(
init|;
name|n
operator|!=
literal|null
operator|&&
name|n
operator|!=
name|file
condition|;
name|n
operator|=
name|n
operator|.
name|getNext
argument_list|()
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" -> "
argument_list|)
operator|.
name|append
argument_list|(
name|n
operator|.
name|asINodeFile
argument_list|()
operator|.
name|getObjectString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|append
argument_list|(
name|n
operator|==
literal|null
condition|?
literal|" -> null"
else|:
literal|" -> *"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_interface

end_unit

