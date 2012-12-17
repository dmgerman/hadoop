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
DECL|method|INodeFileUnderConstruction (PermissionStatus permissions, short replication, long preferredBlockSize, long modTime, String clientName, String clientMachine, DatanodeDescriptor clientNode)
name|INodeFileUnderConstruction
parameter_list|(
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
name|super
argument_list|(
name|permissions
operator|.
name|applyUMask
argument_list|(
name|UMASK
argument_list|)
argument_list|,
name|BlockInfo
operator|.
name|EMPTY_ARRAY
argument_list|,
name|replication
argument_list|,
name|modTime
argument_list|,
name|modTime
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
DECL|method|INodeFileUnderConstruction (byte[] name, short blockReplication, long modificationTime, long preferredBlockSize, BlockInfo[] blocks, PermissionStatus perm, String clientName, String clientMachine, DatanodeDescriptor clientNode)
name|INodeFileUnderConstruction
parameter_list|(
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
DECL|method|getClientName ()
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
name|String
name|getClientMachine
parameter_list|()
block|{
return|return
name|clientMachine
return|;
block|}
DECL|method|getClientNode ()
name|DatanodeDescriptor
name|getClientNode
parameter_list|()
block|{
return|return
name|clientNode
return|;
block|}
comment|/**    * Is this inode being constructed?    */
annotation|@
name|Override
DECL|method|isUnderConstruction ()
specifier|public
name|boolean
name|isUnderConstruction
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|//
comment|// converts a INodeFileUnderConstruction into a INodeFile
comment|// use the modification time as the access time
comment|//
DECL|method|convertToInodeFile (long mtime)
name|INodeFile
name|convertToInodeFile
parameter_list|(
name|long
name|mtime
parameter_list|)
block|{
assert|assert
name|allBlocksComplete
argument_list|()
operator|:
literal|"Can't finalize inode "
operator|+
name|this
operator|+
literal|" since it contains non-complete blocks! Blocks are "
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|getBlocks
argument_list|()
argument_list|)
assert|;
comment|//TODO SNAPSHOT: may convert to INodeFileWithLink
return|return
operator|new
name|INodeFile
argument_list|(
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
return|;
block|}
comment|/**    * @return true if all of the blocks in this file are marked as completed.    */
DECL|method|allBlocksComplete ()
specifier|private
name|boolean
name|allBlocksComplete
parameter_list|()
block|{
for|for
control|(
name|BlockInfo
name|b
range|:
name|getBlocks
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|b
operator|.
name|isComplete
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
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

