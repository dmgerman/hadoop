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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|TreeMap
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
name|DatanodeInfo
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
name|NameNode
import|;
end_import

begin_comment
comment|/**   * Keeps a Collection for every named machine containing blocks  * that have recently been invalidated and are thought to live  * on the machine in question.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|InvalidateBlocks
class|class
name|InvalidateBlocks
block|{
comment|/** Mapping: StorageID -> Collection of Blocks */
DECL|field|node2blocks
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|Block
argument_list|>
argument_list|>
name|node2blocks
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|Block
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/** The total number of blocks in the map. */
DECL|field|numBlocks
specifier|private
name|long
name|numBlocks
init|=
literal|0L
decl_stmt|;
DECL|field|datanodeManager
specifier|private
specifier|final
name|DatanodeManager
name|datanodeManager
decl_stmt|;
DECL|method|InvalidateBlocks (final DatanodeManager datanodeManager)
name|InvalidateBlocks
parameter_list|(
specifier|final
name|DatanodeManager
name|datanodeManager
parameter_list|)
block|{
name|this
operator|.
name|datanodeManager
operator|=
name|datanodeManager
expr_stmt|;
block|}
comment|/** @return the number of blocks to be invalidated . */
DECL|method|numBlocks ()
specifier|synchronized
name|long
name|numBlocks
parameter_list|()
block|{
return|return
name|numBlocks
return|;
block|}
comment|/** Does this contain the block which is associated with the storage? */
DECL|method|contains (final String storageID, final Block block)
specifier|synchronized
name|boolean
name|contains
parameter_list|(
specifier|final
name|String
name|storageID
parameter_list|,
specifier|final
name|Block
name|block
parameter_list|)
block|{
specifier|final
name|Collection
argument_list|<
name|Block
argument_list|>
name|s
init|=
name|node2blocks
operator|.
name|get
argument_list|(
name|storageID
argument_list|)
decl_stmt|;
return|return
name|s
operator|!=
literal|null
operator|&&
name|s
operator|.
name|contains
argument_list|(
name|block
argument_list|)
return|;
block|}
comment|/**    * Add a block to the block collection    * which will be invalidated on the specified datanode.    */
DECL|method|add (final Block block, final DatanodeInfo datanode, final boolean log)
specifier|synchronized
name|void
name|add
parameter_list|(
specifier|final
name|Block
name|block
parameter_list|,
specifier|final
name|DatanodeInfo
name|datanode
parameter_list|,
specifier|final
name|boolean
name|log
parameter_list|)
block|{
name|Collection
argument_list|<
name|Block
argument_list|>
name|set
init|=
name|node2blocks
operator|.
name|get
argument_list|(
name|datanode
operator|.
name|getStorageID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
name|set
operator|=
operator|new
name|HashSet
argument_list|<
name|Block
argument_list|>
argument_list|()
expr_stmt|;
name|node2blocks
operator|.
name|put
argument_list|(
name|datanode
operator|.
name|getStorageID
argument_list|()
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|set
operator|.
name|add
argument_list|(
name|block
argument_list|)
condition|)
block|{
name|numBlocks
operator|++
expr_stmt|;
if|if
condition|(
name|log
condition|)
block|{
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|info
argument_list|(
literal|"BLOCK* "
operator|+
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|": add "
operator|+
name|block
operator|+
literal|" to "
operator|+
name|datanode
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Remove a storage from the invalidatesSet */
DECL|method|remove (final String storageID)
specifier|synchronized
name|void
name|remove
parameter_list|(
specifier|final
name|String
name|storageID
parameter_list|)
block|{
specifier|final
name|Collection
argument_list|<
name|Block
argument_list|>
name|blocks
init|=
name|node2blocks
operator|.
name|remove
argument_list|(
name|storageID
argument_list|)
decl_stmt|;
if|if
condition|(
name|blocks
operator|!=
literal|null
condition|)
block|{
name|numBlocks
operator|-=
name|blocks
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Remove the block from the specified storage. */
DECL|method|remove (final String storageID, final Block block)
specifier|synchronized
name|void
name|remove
parameter_list|(
specifier|final
name|String
name|storageID
parameter_list|,
specifier|final
name|Block
name|block
parameter_list|)
block|{
specifier|final
name|Collection
argument_list|<
name|Block
argument_list|>
name|v
init|=
name|node2blocks
operator|.
name|get
argument_list|(
name|storageID
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
operator|&&
name|v
operator|.
name|remove
argument_list|(
name|block
argument_list|)
condition|)
block|{
name|numBlocks
operator|--
expr_stmt|;
if|if
condition|(
name|v
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|node2blocks
operator|.
name|remove
argument_list|(
name|storageID
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Print the contents to out. */
DECL|method|dump (final PrintWriter out)
specifier|synchronized
name|void
name|dump
parameter_list|(
specifier|final
name|PrintWriter
name|out
parameter_list|)
block|{
specifier|final
name|int
name|size
init|=
name|node2blocks
operator|.
name|values
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Metasave: Blocks "
operator|+
name|numBlocks
operator|+
literal|" waiting deletion from "
operator|+
name|size
operator|+
literal|" datanodes."
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|Block
argument_list|>
argument_list|>
name|entry
range|:
name|node2blocks
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|Collection
argument_list|<
name|Block
argument_list|>
name|blocks
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|blocks
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
name|datanodeManager
operator|.
name|getDatanode
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
operator|+
name|blocks
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** @return a list of the storage IDs. */
DECL|method|getStorageIDs ()
specifier|synchronized
name|List
argument_list|<
name|String
argument_list|>
name|getStorageIDs
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|node2blocks
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
comment|/** Invalidate work for the storage. */
DECL|method|invalidateWork (final String storageId)
name|int
name|invalidateWork
parameter_list|(
specifier|final
name|String
name|storageId
parameter_list|)
block|{
specifier|final
name|DatanodeDescriptor
name|dn
init|=
name|datanodeManager
operator|.
name|getDatanode
argument_list|(
name|storageId
argument_list|)
decl_stmt|;
if|if
condition|(
name|dn
operator|==
literal|null
condition|)
block|{
name|remove
argument_list|(
name|storageId
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
specifier|final
name|List
argument_list|<
name|Block
argument_list|>
name|toInvalidate
init|=
name|invalidateWork
argument_list|(
name|storageId
argument_list|,
name|dn
argument_list|)
decl_stmt|;
if|if
condition|(
name|toInvalidate
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|info
argument_list|(
literal|"BLOCK* "
operator|+
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|": ask "
operator|+
name|dn
operator|.
name|getName
argument_list|()
operator|+
literal|" to delete "
operator|+
name|toInvalidate
argument_list|)
expr_stmt|;
block|}
return|return
name|toInvalidate
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|invalidateWork ( final String storageId, final DatanodeDescriptor dn)
specifier|private
specifier|synchronized
name|List
argument_list|<
name|Block
argument_list|>
name|invalidateWork
parameter_list|(
specifier|final
name|String
name|storageId
parameter_list|,
specifier|final
name|DatanodeDescriptor
name|dn
parameter_list|)
block|{
specifier|final
name|Collection
argument_list|<
name|Block
argument_list|>
name|set
init|=
name|node2blocks
operator|.
name|get
argument_list|(
name|storageId
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// # blocks that can be sent in one message is limited
specifier|final
name|int
name|limit
init|=
name|datanodeManager
operator|.
name|blockInvalidateLimit
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Block
argument_list|>
name|toInvalidate
init|=
operator|new
name|ArrayList
argument_list|<
name|Block
argument_list|>
argument_list|(
name|limit
argument_list|)
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|Block
argument_list|>
name|it
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|count
init|=
literal|0
init|;
name|count
operator|<
name|limit
operator|&&
name|it
operator|.
name|hasNext
argument_list|()
condition|;
name|count
operator|++
control|)
block|{
name|toInvalidate
operator|.
name|add
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|// If we send everything in this message, remove this node entry
if|if
condition|(
operator|!
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|remove
argument_list|(
name|storageId
argument_list|)
expr_stmt|;
block|}
name|dn
operator|.
name|addBlocksToBeInvalidated
argument_list|(
name|toInvalidate
argument_list|)
expr_stmt|;
name|numBlocks
operator|-=
name|toInvalidate
operator|.
name|size
argument_list|()
expr_stmt|;
return|return
name|toInvalidate
return|;
block|}
block|}
end_class

end_unit

