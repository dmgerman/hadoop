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
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|io
operator|.
name|PrintWriter
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
name|fs
operator|.
name|permission
operator|.
name|FsAction
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
name|permission
operator|.
name|FsPermission
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
name|permission
operator|.
name|PermissionStatus
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
name|QuotaExceededException
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
name|BlockCollection
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
name|blockmanagement
operator|.
name|BlockInfoUnderConstruction
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
name|DatanodeDescriptor
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
name|Content
operator|.
name|CountsMap
operator|.
name|Key
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
name|FileWithSnapshot
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
name|FileWithSnapshot
operator|.
name|FileDiff
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
name|FileWithSnapshot
operator|.
name|FileDiffList
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
name|FileWithSnapshot
operator|.
name|Util
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/** I-node for closed file. */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|INodeFile
specifier|public
class|class
name|INodeFile
extends|extends
name|INodeWithAdditionalFields
implements|implements
name|BlockCollection
block|{
comment|/** The same as valueOf(inode, path, false). */
DECL|method|valueOf (INode inode, String path )
specifier|public
specifier|static
name|INodeFile
name|valueOf
parameter_list|(
name|INode
name|inode
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
return|return
name|valueOf
argument_list|(
name|inode
argument_list|,
name|path
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Cast INode to INodeFile. */
DECL|method|valueOf (INode inode, String path, boolean acceptNull)
specifier|public
specifier|static
name|INodeFile
name|valueOf
parameter_list|(
name|INode
name|inode
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|acceptNull
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
if|if
condition|(
name|inode
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|acceptNull
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"File does not exist: "
operator|+
name|path
argument_list|)
throw|;
block|}
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
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Path is not a file: "
operator|+
name|path
argument_list|)
throw|;
block|}
return|return
name|inode
operator|.
name|asFile
argument_list|()
return|;
block|}
DECL|field|UMASK
specifier|static
specifier|final
name|FsPermission
name|UMASK
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0111
argument_list|)
decl_stmt|;
comment|/** Format: [16 bits for replication][48 bits for PreferredBlockSize] */
DECL|class|HeaderFormat
specifier|private
specifier|static
class|class
name|HeaderFormat
block|{
comment|/** Number of bits for Block size */
DECL|field|BLOCKBITS
specifier|static
specifier|final
name|int
name|BLOCKBITS
init|=
literal|48
decl_stmt|;
comment|/** Header mask 64-bit representation */
DECL|field|HEADERMASK
specifier|static
specifier|final
name|long
name|HEADERMASK
init|=
literal|0xffffL
operator|<<
name|BLOCKBITS
decl_stmt|;
DECL|field|MAX_BLOCK_SIZE
specifier|static
specifier|final
name|long
name|MAX_BLOCK_SIZE
init|=
operator|~
name|HEADERMASK
decl_stmt|;
DECL|method|getReplication (long header)
specifier|static
name|short
name|getReplication
parameter_list|(
name|long
name|header
parameter_list|)
block|{
return|return
call|(
name|short
call|)
argument_list|(
operator|(
name|header
operator|&
name|HEADERMASK
operator|)
operator|>>
name|BLOCKBITS
argument_list|)
return|;
block|}
DECL|method|combineReplication (long header, short replication)
specifier|static
name|long
name|combineReplication
parameter_list|(
name|long
name|header
parameter_list|,
name|short
name|replication
parameter_list|)
block|{
if|if
condition|(
name|replication
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected value for the replication: "
operator|+
name|replication
argument_list|)
throw|;
block|}
return|return
operator|(
operator|(
name|long
operator|)
name|replication
operator|<<
name|BLOCKBITS
operator|)
operator||
operator|(
name|header
operator|&
name|MAX_BLOCK_SIZE
operator|)
return|;
block|}
DECL|method|getPreferredBlockSize (long header)
specifier|static
name|long
name|getPreferredBlockSize
parameter_list|(
name|long
name|header
parameter_list|)
block|{
return|return
name|header
operator|&
name|MAX_BLOCK_SIZE
return|;
block|}
DECL|method|combinePreferredBlockSize (long header, long blockSize)
specifier|static
name|long
name|combinePreferredBlockSize
parameter_list|(
name|long
name|header
parameter_list|,
name|long
name|blockSize
parameter_list|)
block|{
if|if
condition|(
name|blockSize
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Block size< 0: "
operator|+
name|blockSize
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|blockSize
operator|>
name|MAX_BLOCK_SIZE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Block size = "
operator|+
name|blockSize
operator|+
literal|"> MAX_BLOCK_SIZE = "
operator|+
name|MAX_BLOCK_SIZE
argument_list|)
throw|;
block|}
return|return
operator|(
name|header
operator|&
name|HEADERMASK
operator|)
operator||
operator|(
name|blockSize
operator|&
name|MAX_BLOCK_SIZE
operator|)
return|;
block|}
block|}
DECL|field|header
specifier|private
name|long
name|header
init|=
literal|0L
decl_stmt|;
DECL|field|blocks
specifier|private
name|BlockInfo
index|[]
name|blocks
decl_stmt|;
DECL|method|INodeFile (long id, byte[] name, PermissionStatus permissions, long mtime, long atime, BlockInfo[] blklist, short replication, long preferredBlockSize)
name|INodeFile
parameter_list|(
name|long
name|id
parameter_list|,
name|byte
index|[]
name|name
parameter_list|,
name|PermissionStatus
name|permissions
parameter_list|,
name|long
name|mtime
parameter_list|,
name|long
name|atime
parameter_list|,
name|BlockInfo
index|[]
name|blklist
parameter_list|,
name|short
name|replication
parameter_list|,
name|long
name|preferredBlockSize
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|,
name|name
argument_list|,
name|permissions
argument_list|,
name|mtime
argument_list|,
name|atime
argument_list|)
expr_stmt|;
name|header
operator|=
name|HeaderFormat
operator|.
name|combineReplication
argument_list|(
name|header
argument_list|,
name|replication
argument_list|)
expr_stmt|;
name|header
operator|=
name|HeaderFormat
operator|.
name|combinePreferredBlockSize
argument_list|(
name|header
argument_list|,
name|preferredBlockSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
name|blklist
expr_stmt|;
block|}
DECL|method|INodeFile (INodeFile that)
specifier|public
name|INodeFile
parameter_list|(
name|INodeFile
name|that
parameter_list|)
block|{
name|super
argument_list|(
name|that
argument_list|)
expr_stmt|;
name|this
operator|.
name|header
operator|=
name|that
operator|.
name|header
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
name|that
operator|.
name|blocks
expr_stmt|;
block|}
comment|/** @return true unconditionally. */
annotation|@
name|Override
DECL|method|isFile ()
specifier|public
specifier|final
name|boolean
name|isFile
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/** @return this object. */
annotation|@
name|Override
DECL|method|asFile ()
specifier|public
specifier|final
name|INodeFile
name|asFile
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/** Is this file under construction? */
DECL|method|isUnderConstruction ()
specifier|public
name|boolean
name|isUnderConstruction
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/** Convert this file to an {@link INodeFileUnderConstruction}. */
DECL|method|toUnderConstruction ( String clientName, String clientMachine, DatanodeDescriptor clientNode)
specifier|public
name|INodeFileUnderConstruction
name|toUnderConstruction
parameter_list|(
name|String
name|clientName
parameter_list|,
name|String
name|clientMachine
parameter_list|,
name|DatanodeDescriptor
name|clientNode
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|isUnderConstruction
argument_list|()
argument_list|,
literal|"file is already an INodeFileUnderConstruction"
argument_list|)
expr_stmt|;
return|return
operator|new
name|INodeFileUnderConstruction
argument_list|(
name|this
argument_list|,
name|clientName
argument_list|,
name|clientMachine
argument_list|,
name|clientNode
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSnapshotINode (final Snapshot snapshot)
specifier|public
name|INodeFile
name|getSnapshotINode
parameter_list|(
specifier|final
name|Snapshot
name|snapshot
parameter_list|)
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|recordModification (final Snapshot latest)
specifier|public
name|INodeFile
name|recordModification
parameter_list|(
specifier|final
name|Snapshot
name|latest
parameter_list|)
throws|throws
name|QuotaExceededException
block|{
return|return
name|isInLatestSnapshot
argument_list|(
name|latest
argument_list|)
condition|?
name|getParent
argument_list|()
operator|.
name|replaceChild4INodeFileWithSnapshot
argument_list|(
name|this
argument_list|)
operator|.
name|recordModification
argument_list|(
name|latest
argument_list|)
else|:
name|this
return|;
block|}
comment|/**    * Set the {@link FsPermission} of this {@link INodeFile}.    * Since this is a file,    * the {@link FsAction#EXECUTE} action, if any, is ignored.    */
annotation|@
name|Override
DECL|method|setPermission (FsPermission permission)
specifier|final
name|void
name|setPermission
parameter_list|(
name|FsPermission
name|permission
parameter_list|)
block|{
name|super
operator|.
name|setPermission
argument_list|(
name|permission
operator|.
name|applyUMask
argument_list|(
name|UMASK
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the {@link FsPermission} of this {@link INodeFile}.    * Since this is a file,    * the {@link FsAction#EXECUTE} action, if any, is ignored.    */
annotation|@
name|Override
DECL|method|setPermission (FsPermission permission, Snapshot latest)
specifier|final
name|INode
name|setPermission
parameter_list|(
name|FsPermission
name|permission
parameter_list|,
name|Snapshot
name|latest
parameter_list|)
throws|throws
name|QuotaExceededException
block|{
return|return
name|super
operator|.
name|setPermission
argument_list|(
name|permission
operator|.
name|applyUMask
argument_list|(
name|UMASK
argument_list|)
argument_list|,
name|latest
argument_list|)
return|;
block|}
comment|/** @return the replication factor of the file. */
DECL|method|getFileReplication (Snapshot snapshot)
specifier|public
specifier|final
name|short
name|getFileReplication
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
if|if
condition|(
name|snapshot
operator|!=
literal|null
condition|)
block|{
return|return
name|getSnapshotINode
argument_list|(
name|snapshot
argument_list|)
operator|.
name|getFileReplication
argument_list|()
return|;
block|}
return|return
name|HeaderFormat
operator|.
name|getReplication
argument_list|(
name|header
argument_list|)
return|;
block|}
comment|/** The same as getFileReplication(null). */
DECL|method|getFileReplication ()
specifier|public
specifier|final
name|short
name|getFileReplication
parameter_list|()
block|{
return|return
name|getFileReplication
argument_list|(
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBlockReplication ()
specifier|public
specifier|final
name|short
name|getBlockReplication
parameter_list|()
block|{
return|return
name|this
operator|instanceof
name|FileWithSnapshot
condition|?
name|Util
operator|.
name|getBlockReplication
argument_list|(
operator|(
name|FileWithSnapshot
operator|)
name|this
argument_list|)
else|:
name|getFileReplication
argument_list|(
literal|null
argument_list|)
return|;
block|}
comment|/** Set the replication factor of this file. */
DECL|method|setFileReplication (short replication)
specifier|public
specifier|final
name|void
name|setFileReplication
parameter_list|(
name|short
name|replication
parameter_list|)
block|{
name|header
operator|=
name|HeaderFormat
operator|.
name|combineReplication
argument_list|(
name|header
argument_list|,
name|replication
argument_list|)
expr_stmt|;
block|}
comment|/** Set the replication factor of this file. */
DECL|method|setFileReplication (short replication, Snapshot latest)
specifier|public
specifier|final
name|INodeFile
name|setFileReplication
parameter_list|(
name|short
name|replication
parameter_list|,
name|Snapshot
name|latest
parameter_list|)
throws|throws
name|QuotaExceededException
block|{
specifier|final
name|INodeFile
name|nodeToUpdate
init|=
name|recordModification
argument_list|(
name|latest
argument_list|)
decl_stmt|;
name|nodeToUpdate
operator|.
name|setFileReplication
argument_list|(
name|replication
argument_list|)
expr_stmt|;
return|return
name|nodeToUpdate
return|;
block|}
comment|/** @return preferred block size (in bytes) of the file. */
annotation|@
name|Override
DECL|method|getPreferredBlockSize ()
specifier|public
name|long
name|getPreferredBlockSize
parameter_list|()
block|{
return|return
name|HeaderFormat
operator|.
name|getPreferredBlockSize
argument_list|(
name|header
argument_list|)
return|;
block|}
comment|/** @return the diskspace required for a full block. */
DECL|method|getBlockDiskspace ()
specifier|final
name|long
name|getBlockDiskspace
parameter_list|()
block|{
return|return
name|getPreferredBlockSize
argument_list|()
operator|*
name|getBlockReplication
argument_list|()
return|;
block|}
comment|/** @return the blocks of the file. */
annotation|@
name|Override
DECL|method|getBlocks ()
specifier|public
name|BlockInfo
index|[]
name|getBlocks
parameter_list|()
block|{
return|return
name|this
operator|.
name|blocks
return|;
block|}
DECL|method|updateBlockCollection ()
name|void
name|updateBlockCollection
parameter_list|()
block|{
if|if
condition|(
name|blocks
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|BlockInfo
name|b
range|:
name|blocks
control|)
block|{
name|b
operator|.
name|setBlockCollection
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * append array of blocks to this.blocks    */
DECL|method|concatBlocks (INodeFile[] inodes)
name|void
name|concatBlocks
parameter_list|(
name|INodeFile
index|[]
name|inodes
parameter_list|)
block|{
name|int
name|size
init|=
name|this
operator|.
name|blocks
operator|.
name|length
decl_stmt|;
name|int
name|totalAddedBlocks
init|=
literal|0
decl_stmt|;
for|for
control|(
name|INodeFile
name|f
range|:
name|inodes
control|)
block|{
name|totalAddedBlocks
operator|+=
name|f
operator|.
name|blocks
operator|.
name|length
expr_stmt|;
block|}
name|BlockInfo
index|[]
name|newlist
init|=
operator|new
name|BlockInfo
index|[
name|size
operator|+
name|totalAddedBlocks
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|this
operator|.
name|blocks
argument_list|,
literal|0
argument_list|,
name|newlist
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
for|for
control|(
name|INodeFile
name|in
range|:
name|inodes
control|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|in
operator|.
name|blocks
argument_list|,
literal|0
argument_list|,
name|newlist
argument_list|,
name|size
argument_list|,
name|in
operator|.
name|blocks
operator|.
name|length
argument_list|)
expr_stmt|;
name|size
operator|+=
name|in
operator|.
name|blocks
operator|.
name|length
expr_stmt|;
block|}
name|setBlocks
argument_list|(
name|newlist
argument_list|)
expr_stmt|;
name|updateBlockCollection
argument_list|()
expr_stmt|;
block|}
comment|/**    * add a block to the block list    */
DECL|method|addBlock (BlockInfo newblock)
name|void
name|addBlock
parameter_list|(
name|BlockInfo
name|newblock
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|blocks
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|setBlocks
argument_list|(
operator|new
name|BlockInfo
index|[]
block|{
name|newblock
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|size
init|=
name|this
operator|.
name|blocks
operator|.
name|length
decl_stmt|;
name|BlockInfo
index|[]
name|newlist
init|=
operator|new
name|BlockInfo
index|[
name|size
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|this
operator|.
name|blocks
argument_list|,
literal|0
argument_list|,
name|newlist
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|newlist
index|[
name|size
index|]
operator|=
name|newblock
expr_stmt|;
name|this
operator|.
name|setBlocks
argument_list|(
name|newlist
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Set the block of the file at the given index. */
DECL|method|setBlock (int idx, BlockInfo blk)
specifier|public
name|void
name|setBlock
parameter_list|(
name|int
name|idx
parameter_list|,
name|BlockInfo
name|blk
parameter_list|)
block|{
name|this
operator|.
name|blocks
index|[
name|idx
index|]
operator|=
name|blk
expr_stmt|;
block|}
comment|/** Set the blocks. */
DECL|method|setBlocks (BlockInfo[] blocks)
specifier|public
name|void
name|setBlocks
parameter_list|(
name|BlockInfo
index|[]
name|blocks
parameter_list|)
block|{
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cleanSubtree (final Snapshot snapshot, Snapshot prior, final BlocksMapUpdateInfo collectedBlocks, final List<INode> removedINodes)
specifier|public
name|Quota
operator|.
name|Counts
name|cleanSubtree
parameter_list|(
specifier|final
name|Snapshot
name|snapshot
parameter_list|,
name|Snapshot
name|prior
parameter_list|,
specifier|final
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|,
specifier|final
name|List
argument_list|<
name|INode
argument_list|>
name|removedINodes
parameter_list|)
throws|throws
name|QuotaExceededException
block|{
name|Quota
operator|.
name|Counts
name|counts
init|=
name|Quota
operator|.
name|Counts
operator|.
name|newInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|snapshot
operator|==
literal|null
operator|&&
name|prior
operator|==
literal|null
condition|)
block|{
comment|// this only happens when deleting the current file
name|computeQuotaUsage
argument_list|(
name|counts
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|destroyAndCollectBlocks
argument_list|(
name|collectedBlocks
argument_list|,
name|removedINodes
argument_list|)
expr_stmt|;
block|}
return|return
name|counts
return|;
block|}
annotation|@
name|Override
DECL|method|destroyAndCollectBlocks (BlocksMapUpdateInfo collectedBlocks, final List<INode> removedINodes)
specifier|public
name|void
name|destroyAndCollectBlocks
parameter_list|(
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|,
specifier|final
name|List
argument_list|<
name|INode
argument_list|>
name|removedINodes
parameter_list|)
block|{
if|if
condition|(
name|blocks
operator|!=
literal|null
operator|&&
name|collectedBlocks
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|BlockInfo
name|blk
range|:
name|blocks
control|)
block|{
name|collectedBlocks
operator|.
name|addDeleteBlock
argument_list|(
name|blk
argument_list|)
expr_stmt|;
name|blk
operator|.
name|setBlockCollection
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
name|setBlocks
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|clear
argument_list|()
expr_stmt|;
name|removedINodes
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|instanceof
name|FileWithSnapshot
condition|)
block|{
operator|(
operator|(
name|FileWithSnapshot
operator|)
name|this
operator|)
operator|.
name|getDiffs
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
comment|// Get the full path name of this inode.
return|return
name|getFullPathName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|computeQuotaUsage (Quota.Counts counts, boolean useCache)
specifier|public
specifier|final
name|Quota
operator|.
name|Counts
name|computeQuotaUsage
parameter_list|(
name|Quota
operator|.
name|Counts
name|counts
parameter_list|,
name|boolean
name|useCache
parameter_list|)
block|{
name|counts
operator|.
name|add
argument_list|(
name|Quota
operator|.
name|NAMESPACE
argument_list|,
name|this
operator|instanceof
name|FileWithSnapshot
condition|?
operator|(
operator|(
name|FileWithSnapshot
operator|)
name|this
operator|)
operator|.
name|getDiffs
argument_list|()
operator|.
name|asList
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|1
else|:
literal|1
argument_list|)
expr_stmt|;
name|counts
operator|.
name|add
argument_list|(
name|Quota
operator|.
name|DISKSPACE
argument_list|,
name|diskspaceConsumed
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|counts
return|;
block|}
annotation|@
name|Override
DECL|method|computeContentSummary ( final Content.CountsMap countsMap)
specifier|public
specifier|final
name|Content
operator|.
name|CountsMap
name|computeContentSummary
parameter_list|(
specifier|final
name|Content
operator|.
name|CountsMap
name|countsMap
parameter_list|)
block|{
name|computeContentSummary4Snapshot
argument_list|(
name|countsMap
operator|.
name|getCounts
argument_list|(
name|Key
operator|.
name|SNAPSHOT
argument_list|)
argument_list|)
expr_stmt|;
name|computeContentSummary4Current
argument_list|(
name|countsMap
operator|.
name|getCounts
argument_list|(
name|Key
operator|.
name|CURRENT
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|countsMap
return|;
block|}
annotation|@
name|Override
DECL|method|computeContentSummary ( final Content.Counts counts)
specifier|public
specifier|final
name|Content
operator|.
name|Counts
name|computeContentSummary
parameter_list|(
specifier|final
name|Content
operator|.
name|Counts
name|counts
parameter_list|)
block|{
name|computeContentSummary4Snapshot
argument_list|(
name|counts
argument_list|)
expr_stmt|;
name|computeContentSummary4Current
argument_list|(
name|counts
argument_list|)
expr_stmt|;
return|return
name|counts
return|;
block|}
DECL|method|computeContentSummary4Snapshot (final Content.Counts counts)
specifier|private
name|void
name|computeContentSummary4Snapshot
parameter_list|(
specifier|final
name|Content
operator|.
name|Counts
name|counts
parameter_list|)
block|{
comment|// file length and diskspace only counted for the latest state of the file
comment|// i.e. either the current state or the last snapshot
if|if
condition|(
name|this
operator|instanceof
name|FileWithSnapshot
condition|)
block|{
specifier|final
name|FileWithSnapshot
name|withSnapshot
init|=
operator|(
name|FileWithSnapshot
operator|)
name|this
decl_stmt|;
specifier|final
name|FileDiffList
name|diffs
init|=
name|withSnapshot
operator|.
name|getDiffs
argument_list|()
decl_stmt|;
specifier|final
name|int
name|n
init|=
name|diffs
operator|.
name|asList
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|counts
operator|.
name|add
argument_list|(
name|Content
operator|.
name|FILE
argument_list|,
name|n
argument_list|)
expr_stmt|;
if|if
condition|(
name|n
operator|>
literal|0
operator|&&
name|withSnapshot
operator|.
name|isCurrentFileDeleted
argument_list|()
condition|)
block|{
name|counts
operator|.
name|add
argument_list|(
name|Content
operator|.
name|LENGTH
argument_list|,
name|diffs
operator|.
name|getLast
argument_list|()
operator|.
name|getFileSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|withSnapshot
operator|.
name|isCurrentFileDeleted
argument_list|()
condition|)
block|{
specifier|final
name|long
name|lastFileSize
init|=
name|diffs
operator|.
name|getLast
argument_list|()
operator|.
name|getFileSize
argument_list|()
decl_stmt|;
name|counts
operator|.
name|add
argument_list|(
name|Content
operator|.
name|DISKSPACE
argument_list|,
name|lastFileSize
operator|*
name|getBlockReplication
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|computeContentSummary4Current (final Content.Counts counts)
specifier|private
name|void
name|computeContentSummary4Current
parameter_list|(
specifier|final
name|Content
operator|.
name|Counts
name|counts
parameter_list|)
block|{
if|if
condition|(
name|this
operator|instanceof
name|FileWithSnapshot
operator|&&
operator|(
operator|(
name|FileWithSnapshot
operator|)
name|this
operator|)
operator|.
name|isCurrentFileDeleted
argument_list|()
condition|)
block|{
return|return;
block|}
name|counts
operator|.
name|add
argument_list|(
name|Content
operator|.
name|LENGTH
argument_list|,
name|computeFileSize
argument_list|()
argument_list|)
expr_stmt|;
name|counts
operator|.
name|add
argument_list|(
name|Content
operator|.
name|FILE
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|counts
operator|.
name|add
argument_list|(
name|Content
operator|.
name|DISKSPACE
argument_list|,
name|diskspaceConsumed
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** The same as computeFileSize(null). */
DECL|method|computeFileSize ()
specifier|public
specifier|final
name|long
name|computeFileSize
parameter_list|()
block|{
return|return
name|computeFileSize
argument_list|(
literal|null
argument_list|)
return|;
block|}
comment|/**    * Compute file size of the current file if the given snapshot is null;    * otherwise, get the file size from the given snapshot.    */
DECL|method|computeFileSize (Snapshot snapshot)
specifier|public
specifier|final
name|long
name|computeFileSize
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
if|if
condition|(
name|snapshot
operator|!=
literal|null
operator|&&
name|this
operator|instanceof
name|FileWithSnapshot
condition|)
block|{
specifier|final
name|FileDiff
name|d
init|=
operator|(
operator|(
name|FileWithSnapshot
operator|)
name|this
operator|)
operator|.
name|getDiffs
argument_list|()
operator|.
name|getDiff
argument_list|(
name|snapshot
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
block|{
return|return
name|d
operator|.
name|getFileSize
argument_list|()
return|;
block|}
block|}
return|return
name|computeFileSize
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Compute file size of the current file size    * but not including the last block if it is under construction.    */
DECL|method|computeFileSizeNotIncludingLastUcBlock ()
specifier|public
specifier|final
name|long
name|computeFileSizeNotIncludingLastUcBlock
parameter_list|()
block|{
return|return
name|computeFileSize
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Compute file size of the current file.    *     * @param includesLastUcBlock    *          If the last block is under construction, should it be included?    * @param usePreferredBlockSize4LastUcBlock    *          If the last block is under construction, should we use actual    *          block size or preferred block size?    *          Note that usePreferredBlockSize4LastUcBlock is ignored    *          if includesLastUcBlock == false.    * @return file size    */
DECL|method|computeFileSize (boolean includesLastUcBlock, boolean usePreferredBlockSize4LastUcBlock)
specifier|private
specifier|final
name|long
name|computeFileSize
parameter_list|(
name|boolean
name|includesLastUcBlock
parameter_list|,
name|boolean
name|usePreferredBlockSize4LastUcBlock
parameter_list|)
block|{
if|if
condition|(
name|blocks
operator|==
literal|null
operator|||
name|blocks
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
specifier|final
name|int
name|last
init|=
name|blocks
operator|.
name|length
operator|-
literal|1
decl_stmt|;
comment|//check if the last block is BlockInfoUnderConstruction
name|long
name|size
init|=
name|blocks
index|[
name|last
index|]
operator|.
name|getNumBytes
argument_list|()
decl_stmt|;
if|if
condition|(
name|blocks
index|[
name|last
index|]
operator|instanceof
name|BlockInfoUnderConstruction
condition|)
block|{
if|if
condition|(
operator|!
name|includesLastUcBlock
condition|)
block|{
name|size
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|usePreferredBlockSize4LastUcBlock
condition|)
block|{
name|size
operator|=
name|getPreferredBlockSize
argument_list|()
expr_stmt|;
block|}
block|}
comment|//sum other blocks
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|last
condition|;
name|i
operator|++
control|)
block|{
name|size
operator|+=
name|blocks
index|[
name|i
index|]
operator|.
name|getNumBytes
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
DECL|method|diskspaceConsumed ()
specifier|public
specifier|final
name|long
name|diskspaceConsumed
parameter_list|()
block|{
comment|// use preferred block size for the last block if it is under construction
return|return
name|computeFileSize
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
operator|*
name|getBlockReplication
argument_list|()
return|;
block|}
comment|/**    * Return the penultimate allocated block for this file.    */
DECL|method|getPenultimateBlock ()
name|BlockInfo
name|getPenultimateBlock
parameter_list|()
block|{
if|if
condition|(
name|blocks
operator|==
literal|null
operator|||
name|blocks
operator|.
name|length
operator|<=
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|blocks
index|[
name|blocks
operator|.
name|length
operator|-
literal|2
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getLastBlock ()
specifier|public
name|BlockInfo
name|getLastBlock
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|blocks
operator|==
literal|null
operator|||
name|blocks
operator|.
name|length
operator|==
literal|0
condition|?
literal|null
else|:
name|blocks
index|[
name|blocks
operator|.
name|length
operator|-
literal|1
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|numBlocks ()
specifier|public
name|int
name|numBlocks
parameter_list|()
block|{
return|return
name|blocks
operator|==
literal|null
condition|?
literal|0
else|:
name|blocks
operator|.
name|length
return|;
block|}
annotation|@
name|VisibleForTesting
annotation|@
name|Override
DECL|method|dumpTreeRecursively (PrintWriter out, StringBuilder prefix, final Snapshot snapshot)
specifier|public
name|void
name|dumpTreeRecursively
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|StringBuilder
name|prefix
parameter_list|,
specifier|final
name|Snapshot
name|snapshot
parameter_list|)
block|{
name|super
operator|.
name|dumpTreeRecursively
argument_list|(
name|out
argument_list|,
name|prefix
argument_list|,
name|snapshot
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|", fileSize="
operator|+
name|computeFileSize
argument_list|(
name|snapshot
argument_list|)
argument_list|)
expr_stmt|;
comment|// only compare the first block
name|out
operator|.
name|print
argument_list|(
literal|", blocks="
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|blocks
operator|==
literal|null
operator|||
name|blocks
operator|.
name|length
operator|==
literal|0
condition|?
literal|null
else|:
name|blocks
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

