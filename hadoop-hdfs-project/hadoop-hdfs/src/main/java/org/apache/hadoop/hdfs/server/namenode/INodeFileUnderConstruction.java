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
name|util
operator|.
name|Arrays
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
name|blockmanagement
operator|.
name|MutableBlockCollection
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|BlockUCState
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
name|INodeFileUnderConstructionWithSnapshot
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
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * I-node for file being written.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|INodeFileUnderConstruction
specifier|public
class|class
name|INodeFileUnderConstruction
extends|extends
name|INodeFile
implements|implements
name|MutableBlockCollection
block|{
comment|/** Cast INode to INodeFileUnderConstruction. */
DECL|method|valueOf (INode inode, String path )
specifier|public
specifier|static
name|INodeFileUnderConstruction
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
specifier|final
name|INodeFile
name|file
init|=
name|INodeFile
operator|.
name|valueOf
argument_list|(
name|inode
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|isUnderConstruction
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"File is not under construction: "
operator|+
name|path
argument_list|)
throw|;
block|}
return|return
operator|(
name|INodeFileUnderConstruction
operator|)
name|file
return|;
block|}
DECL|field|clientName
specifier|private
name|String
name|clientName
decl_stmt|;
comment|// lease holder
DECL|field|clientMachine
specifier|private
specifier|final
name|String
name|clientMachine
decl_stmt|;
DECL|field|clientNode
specifier|private
specifier|final
name|DatanodeDescriptor
name|clientNode
decl_stmt|;
comment|// if client is a cluster node too.
DECL|method|INodeFileUnderConstruction (long id, PermissionStatus permissions, short replication, long preferredBlockSize, long modTime, String clientName, String clientMachine, DatanodeDescriptor clientNode)
name|INodeFileUnderConstruction
parameter_list|(
name|long
name|id
parameter_list|,
name|PermissionStatus
name|permissions
parameter_list|,
name|short
name|replication
parameter_list|,
name|long
name|preferredBlockSize
parameter_list|,
name|long
name|modTime
parameter_list|,
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
name|this
argument_list|(
name|id
argument_list|,
literal|null
argument_list|,
name|replication
argument_list|,
name|modTime
argument_list|,
name|preferredBlockSize
argument_list|,
name|BlockInfo
operator|.
name|EMPTY_ARRAY
argument_list|,
name|permissions
argument_list|,
name|clientName
argument_list|,
name|clientMachine
argument_list|,
name|clientNode
argument_list|)
expr_stmt|;
block|}
DECL|method|INodeFileUnderConstruction (long id, byte[] name, short blockReplication, long modificationTime, long preferredBlockSize, BlockInfo[] blocks, PermissionStatus perm, String clientName, String clientMachine, DatanodeDescriptor clientNode)
name|INodeFileUnderConstruction
parameter_list|(
name|long
name|id
parameter_list|,
name|byte
index|[]
name|name
parameter_list|,
name|short
name|blockReplication
parameter_list|,
name|long
name|modificationTime
parameter_list|,
name|long
name|preferredBlockSize
parameter_list|,
name|BlockInfo
index|[]
name|blocks
parameter_list|,
name|PermissionStatus
name|perm
parameter_list|,
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
name|super
argument_list|(
name|id
argument_list|,
name|name
argument_list|,
name|perm
argument_list|,
name|modificationTime
argument_list|,
name|modificationTime
argument_list|,
name|blocks
argument_list|,
name|blockReplication
argument_list|,
name|preferredBlockSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|clientName
operator|=
name|clientName
expr_stmt|;
name|this
operator|.
name|clientMachine
operator|=
name|clientMachine
expr_stmt|;
name|this
operator|.
name|clientNode
operator|=
name|clientNode
expr_stmt|;
block|}
DECL|method|INodeFileUnderConstruction (final INodeFile that, final String clientName, final String clientMachine, final DatanodeDescriptor clientNode)
specifier|public
name|INodeFileUnderConstruction
parameter_list|(
specifier|final
name|INodeFile
name|that
parameter_list|,
specifier|final
name|String
name|clientName
parameter_list|,
specifier|final
name|String
name|clientMachine
parameter_list|,
specifier|final
name|DatanodeDescriptor
name|clientNode
parameter_list|)
block|{
name|super
argument_list|(
name|that
argument_list|)
expr_stmt|;
name|this
operator|.
name|clientName
operator|=
name|clientName
expr_stmt|;
name|this
operator|.
name|clientMachine
operator|=
name|clientMachine
expr_stmt|;
name|this
operator|.
name|clientNode
operator|=
name|clientNode
expr_stmt|;
block|}
DECL|method|getClientName ()
specifier|public
name|String
name|getClientName
parameter_list|()
block|{
return|return
name|clientName
return|;
block|}
DECL|method|setClientName (String clientName)
name|void
name|setClientName
parameter_list|(
name|String
name|clientName
parameter_list|)
block|{
name|this
operator|.
name|clientName
operator|=
name|clientName
expr_stmt|;
block|}
DECL|method|getClientMachine ()
specifier|public
name|String
name|getClientMachine
parameter_list|()
block|{
return|return
name|clientMachine
return|;
block|}
DECL|method|getClientNode ()
specifier|public
name|DatanodeDescriptor
name|getClientNode
parameter_list|()
block|{
return|return
name|clientNode
return|;
block|}
comment|/** @return true unconditionally. */
annotation|@
name|Override
DECL|method|isUnderConstruction ()
specifier|public
specifier|final
name|boolean
name|isUnderConstruction
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Converts an INodeFileUnderConstruction to an INodeFile.    * The original modification time is used as the access time.    * The new modification is the specified mtime.    */
DECL|method|toINodeFile (long mtime)
specifier|protected
name|INodeFile
name|toINodeFile
parameter_list|(
name|long
name|mtime
parameter_list|)
block|{
name|assertAllBlocksComplete
argument_list|()
expr_stmt|;
specifier|final
name|INodeFile
name|f
init|=
operator|new
name|INodeFile
argument_list|(
name|getId
argument_list|()
argument_list|,
name|getLocalNameBytes
argument_list|()
argument_list|,
name|getPermissionStatus
argument_list|()
argument_list|,
name|mtime
argument_list|,
name|getModificationTime
argument_list|()
argument_list|,
name|getBlocks
argument_list|()
argument_list|,
name|getFileReplication
argument_list|()
argument_list|,
name|getPreferredBlockSize
argument_list|()
argument_list|)
decl_stmt|;
name|f
operator|.
name|setParent
argument_list|(
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
annotation|@
name|Override
DECL|method|recordModification (final Snapshot latest, final INodeMap inodeMap)
specifier|public
name|INodeFileUnderConstruction
name|recordModification
parameter_list|(
specifier|final
name|Snapshot
name|latest
parameter_list|,
specifier|final
name|INodeMap
name|inodeMap
parameter_list|)
throws|throws
name|QuotaExceededException
block|{
if|if
condition|(
name|isInLatestSnapshot
argument_list|(
name|latest
argument_list|)
condition|)
block|{
name|INodeFileUnderConstructionWithSnapshot
name|newFile
init|=
name|getParent
argument_list|()
operator|.
name|replaceChild4INodeFileUcWithSnapshot
argument_list|(
name|this
argument_list|,
name|inodeMap
argument_list|)
operator|.
name|recordModification
argument_list|(
name|latest
argument_list|,
name|inodeMap
argument_list|)
decl_stmt|;
return|return
name|newFile
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
block|}
comment|/** Assert all blocks are complete. */
DECL|method|assertAllBlocksComplete ()
specifier|protected
name|void
name|assertAllBlocksComplete
parameter_list|()
block|{
specifier|final
name|BlockInfo
index|[]
name|blocks
init|=
name|getBlocks
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
name|blocks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|blocks
index|[
name|i
index|]
operator|.
name|isComplete
argument_list|()
argument_list|,
literal|"Failed to finalize"
operator|+
literal|" %s %s since blocks[%s] is non-complete, where blocks=%s."
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|this
argument_list|,
name|i
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|getBlocks
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Remove a block from the block list. This block should be    * the last one on the list.    */
DECL|method|removeLastBlock (Block oldblock)
name|void
name|removeLastBlock
parameter_list|(
name|Block
name|oldblock
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BlockInfo
index|[]
name|blocks
init|=
name|getBlocks
argument_list|()
decl_stmt|;
if|if
condition|(
name|blocks
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Trying to delete non-existant block "
operator|+
name|oldblock
argument_list|)
throw|;
block|}
name|int
name|size_1
init|=
name|blocks
operator|.
name|length
operator|-
literal|1
decl_stmt|;
if|if
condition|(
operator|!
name|blocks
index|[
name|size_1
index|]
operator|.
name|equals
argument_list|(
name|oldblock
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Trying to delete non-last block "
operator|+
name|oldblock
argument_list|)
throw|;
block|}
comment|//copy to a new list
name|BlockInfo
index|[]
name|newlist
init|=
operator|new
name|BlockInfo
index|[
name|size_1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|blocks
argument_list|,
literal|0
argument_list|,
name|newlist
argument_list|,
literal|0
argument_list|,
name|size_1
argument_list|)
expr_stmt|;
name|setBlocks
argument_list|(
name|newlist
argument_list|)
expr_stmt|;
block|}
comment|/**    * Convert the last block of the file to an under-construction block.    * Set its locations.    */
annotation|@
name|Override
DECL|method|setLastBlock (BlockInfo lastBlock, DatanodeDescriptor[] targets)
specifier|public
name|BlockInfoUnderConstruction
name|setLastBlock
parameter_list|(
name|BlockInfo
name|lastBlock
parameter_list|,
name|DatanodeDescriptor
index|[]
name|targets
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|numBlocks
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to set last block: File is empty."
argument_list|)
throw|;
block|}
name|BlockInfoUnderConstruction
name|ucBlock
init|=
name|lastBlock
operator|.
name|convertToBlockUnderConstruction
argument_list|(
name|BlockUCState
operator|.
name|UNDER_CONSTRUCTION
argument_list|,
name|targets
argument_list|)
decl_stmt|;
name|ucBlock
operator|.
name|setBlockCollection
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|setBlock
argument_list|(
name|numBlocks
argument_list|()
operator|-
literal|1
argument_list|,
name|ucBlock
argument_list|)
expr_stmt|;
return|return
name|ucBlock
return|;
block|}
comment|/**    * Update the length for the last block    *     * @param lastBlockLength    *          The length of the last block reported from client    * @throws IOException    */
DECL|method|updateLengthOfLastBlock (long lastBlockLength)
name|void
name|updateLengthOfLastBlock
parameter_list|(
name|long
name|lastBlockLength
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockInfo
name|lastBlock
init|=
name|this
operator|.
name|getLastBlock
argument_list|()
decl_stmt|;
assert|assert
operator|(
name|lastBlock
operator|!=
literal|null
operator|)
operator|:
literal|"The last block for path "
operator|+
name|this
operator|.
name|getFullPathName
argument_list|()
operator|+
literal|" is null when updating its length"
assert|;
assert|assert
operator|(
name|lastBlock
operator|instanceof
name|BlockInfoUnderConstruction
operator|)
operator|:
literal|"The last block for path "
operator|+
name|this
operator|.
name|getFullPathName
argument_list|()
operator|+
literal|" is not a BlockInfoUnderConstruction when updating its length"
assert|;
name|lastBlock
operator|.
name|setNumBytes
argument_list|(
name|lastBlockLength
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

