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
name|IOException
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
name|Block
import|;
end_import

begin_class
DECL|class|INodeFile
class|class
name|INodeFile
extends|extends
name|INode
block|{
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
comment|//Number of bits for Block size
DECL|field|BLOCKBITS
specifier|static
specifier|final
name|short
name|BLOCKBITS
init|=
literal|48
decl_stmt|;
comment|//Header mask 64-bit representation
comment|//Format: [16 bits for replication][48 bits for PreferredBlockSize]
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
DECL|field|header
specifier|protected
name|long
name|header
decl_stmt|;
DECL|field|blocks
specifier|protected
name|BlockInfo
name|blocks
index|[]
init|=
literal|null
decl_stmt|;
DECL|method|INodeFile (PermissionStatus permissions, int nrBlocks, short replication, long modificationTime, long atime, long preferredBlockSize)
name|INodeFile
parameter_list|(
name|PermissionStatus
name|permissions
parameter_list|,
name|int
name|nrBlocks
parameter_list|,
name|short
name|replication
parameter_list|,
name|long
name|modificationTime
parameter_list|,
name|long
name|atime
parameter_list|,
name|long
name|preferredBlockSize
parameter_list|)
block|{
name|this
argument_list|(
name|permissions
argument_list|,
operator|new
name|BlockInfo
index|[
name|nrBlocks
index|]
argument_list|,
name|replication
argument_list|,
name|modificationTime
argument_list|,
name|atime
argument_list|,
name|preferredBlockSize
argument_list|)
expr_stmt|;
block|}
DECL|method|INodeFile ()
specifier|protected
name|INodeFile
parameter_list|()
block|{
name|blocks
operator|=
literal|null
expr_stmt|;
name|header
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|INodeFile (PermissionStatus permissions, BlockInfo[] blklist, short replication, long modificationTime, long atime, long preferredBlockSize)
specifier|protected
name|INodeFile
parameter_list|(
name|PermissionStatus
name|permissions
parameter_list|,
name|BlockInfo
index|[]
name|blklist
parameter_list|,
name|short
name|replication
parameter_list|,
name|long
name|modificationTime
parameter_list|,
name|long
name|atime
parameter_list|,
name|long
name|preferredBlockSize
parameter_list|)
block|{
name|super
argument_list|(
name|permissions
argument_list|,
name|modificationTime
argument_list|,
name|atime
argument_list|)
expr_stmt|;
name|this
operator|.
name|setReplication
argument_list|(
name|replication
argument_list|)
expr_stmt|;
name|this
operator|.
name|setPreferredBlockSize
argument_list|(
name|preferredBlockSize
argument_list|)
expr_stmt|;
name|blocks
operator|=
name|blklist
expr_stmt|;
block|}
comment|/**    * Set the {@link FsPermission} of this {@link INodeFile}.    * Since this is a file,    * the {@link FsAction#EXECUTE} action, if any, is ignored.    */
DECL|method|setPermission (FsPermission permission)
specifier|protected
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
DECL|method|isDirectory ()
specifier|public
name|boolean
name|isDirectory
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Get block replication for the file     * @return block replication value    */
DECL|method|getReplication ()
specifier|public
name|short
name|getReplication
parameter_list|()
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
DECL|method|setReplication (short replication)
specifier|public
name|void
name|setReplication
parameter_list|(
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
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected value for the replication"
argument_list|)
throw|;
name|header
operator|=
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
operator|~
name|HEADERMASK
operator|)
expr_stmt|;
block|}
comment|/**    * Get preferred block size for the file    * @return preferred block size in bytes    */
DECL|method|getPreferredBlockSize ()
specifier|public
name|long
name|getPreferredBlockSize
parameter_list|()
block|{
return|return
name|header
operator|&
operator|~
name|HEADERMASK
return|;
block|}
DECL|method|setPreferredBlockSize (long preferredBlkSize)
specifier|public
name|void
name|setPreferredBlockSize
parameter_list|(
name|long
name|preferredBlkSize
parameter_list|)
block|{
if|if
condition|(
operator|(
name|preferredBlkSize
operator|<
literal|0
operator|)
operator|||
operator|(
name|preferredBlkSize
operator|>
operator|~
name|HEADERMASK
operator|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected value for the block size"
argument_list|)
throw|;
name|header
operator|=
operator|(
name|header
operator|&
name|HEADERMASK
operator|)
operator||
operator|(
name|preferredBlkSize
operator|&
operator|~
name|HEADERMASK
operator|)
expr_stmt|;
block|}
comment|/**    * Get file blocks     * @return file blocks    */
DECL|method|getBlocks ()
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
comment|/**    * append array of blocks to this.blocks    */
DECL|method|appendBlocks (INodeFile [] inodes, int totalAddedBlocks)
name|void
name|appendBlocks
parameter_list|(
name|INodeFile
index|[]
name|inodes
parameter_list|,
name|int
name|totalAddedBlocks
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
for|for
control|(
name|BlockInfo
name|bi
range|:
name|this
operator|.
name|blocks
control|)
block|{
name|bi
operator|.
name|setINode
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|blocks
operator|=
name|newlist
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
name|blocks
operator|=
operator|new
name|BlockInfo
index|[
literal|1
index|]
expr_stmt|;
name|this
operator|.
name|blocks
index|[
literal|0
index|]
operator|=
name|newblock
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
name|blocks
operator|=
name|newlist
expr_stmt|;
block|}
block|}
comment|/**    * Set file block    */
DECL|method|setBlock (int idx, BlockInfo blk)
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
DECL|method|collectSubtreeBlocksAndClear (List<Block> v)
name|int
name|collectSubtreeBlocksAndClear
parameter_list|(
name|List
argument_list|<
name|Block
argument_list|>
name|v
parameter_list|)
block|{
name|parent
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|blocks
operator|!=
literal|null
operator|&&
name|v
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
name|v
operator|.
name|add
argument_list|(
name|blk
argument_list|)
expr_stmt|;
name|blk
operator|.
name|setINode
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
name|blocks
operator|=
literal|null
expr_stmt|;
return|return
literal|1
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|computeContentSummary (long[] summary)
name|long
index|[]
name|computeContentSummary
parameter_list|(
name|long
index|[]
name|summary
parameter_list|)
block|{
name|summary
index|[
literal|0
index|]
operator|+=
name|computeFileSize
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|summary
index|[
literal|1
index|]
operator|++
expr_stmt|;
name|summary
index|[
literal|3
index|]
operator|+=
name|diskspaceConsumed
argument_list|()
expr_stmt|;
return|return
name|summary
return|;
block|}
comment|/** Compute file size.    * May or may not include BlockInfoUnderConstruction.    */
DECL|method|computeFileSize (boolean includesBlockInfoUnderConstruction)
name|long
name|computeFileSize
parameter_list|(
name|boolean
name|includesBlockInfoUnderConstruction
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
name|bytes
init|=
name|blocks
index|[
name|last
index|]
operator|instanceof
name|BlockInfoUnderConstruction
operator|&&
operator|!
name|includesBlockInfoUnderConstruction
condition|?
literal|0
else|:
name|blocks
index|[
name|last
index|]
operator|.
name|getNumBytes
argument_list|()
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
name|last
condition|;
name|i
operator|++
control|)
block|{
name|bytes
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
name|bytes
return|;
block|}
annotation|@
name|Override
DECL|method|spaceConsumedInTree (DirCounts counts)
name|DirCounts
name|spaceConsumedInTree
parameter_list|(
name|DirCounts
name|counts
parameter_list|)
block|{
name|counts
operator|.
name|nsCount
operator|+=
literal|1
expr_stmt|;
name|counts
operator|.
name|dsCount
operator|+=
name|diskspaceConsumed
argument_list|()
expr_stmt|;
return|return
name|counts
return|;
block|}
DECL|method|diskspaceConsumed ()
name|long
name|diskspaceConsumed
parameter_list|()
block|{
return|return
name|diskspaceConsumed
argument_list|(
name|blocks
argument_list|)
return|;
block|}
DECL|method|diskspaceConsumed (Block[] blkArr)
name|long
name|diskspaceConsumed
parameter_list|(
name|Block
index|[]
name|blkArr
parameter_list|)
block|{
name|long
name|size
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|blkArr
operator|==
literal|null
condition|)
return|return
literal|0
return|;
for|for
control|(
name|Block
name|blk
range|:
name|blkArr
control|)
block|{
if|if
condition|(
name|blk
operator|!=
literal|null
condition|)
block|{
name|size
operator|+=
name|blk
operator|.
name|getNumBytes
argument_list|()
expr_stmt|;
block|}
block|}
comment|/* If the last block is being written to, use prefferedBlockSize      * rather than the actual block size.      */
if|if
condition|(
name|blkArr
operator|.
name|length
operator|>
literal|0
operator|&&
name|blkArr
index|[
name|blkArr
operator|.
name|length
operator|-
literal|1
index|]
operator|!=
literal|null
operator|&&
name|isUnderConstruction
argument_list|()
condition|)
block|{
name|size
operator|+=
name|getPreferredBlockSize
argument_list|()
operator|-
name|blkArr
index|[
name|blkArr
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|getNumBytes
argument_list|()
expr_stmt|;
block|}
return|return
name|size
operator|*
name|getReplication
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
comment|/**    * Get the last block of the file.    * Make sure it has the right type.    */
DECL|method|getLastBlock ()
parameter_list|<
name|T
extends|extends
name|BlockInfo
parameter_list|>
name|T
name|getLastBlock
parameter_list|()
throws|throws
name|IOException
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
return|return
literal|null
return|;
name|T
name|returnBlock
init|=
literal|null
decl_stmt|;
try|try
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// ClassCastException is caught below
name|T
name|tBlock
init|=
operator|(
name|T
operator|)
name|blocks
index|[
name|blocks
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|returnBlock
operator|=
name|tBlock
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|cce
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected last block type: "
operator|+
name|blocks
index|[
name|blocks
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|returnBlock
return|;
block|}
DECL|method|numBlocks ()
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
block|}
end_class

end_unit

