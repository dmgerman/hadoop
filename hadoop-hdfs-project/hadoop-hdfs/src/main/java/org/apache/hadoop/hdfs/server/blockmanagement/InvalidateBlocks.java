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
name|text
operator|.
name|SimpleDateFormat
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
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|GregorianCalendar
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
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|util
operator|.
name|LightWeightHashSet
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
name|Time
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
name|DFSUtil
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
comment|/**  * Keeps a Collection for every named machine containing blocks  * that have recently been invalidated and are thought to live  * on the machine in question.  */
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
comment|/** Mapping: DatanodeInfo -> Collection of Blocks */
DECL|field|node2blocks
specifier|private
specifier|final
name|Map
argument_list|<
name|DatanodeInfo
argument_list|,
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
argument_list|>
name|node2blocks
init|=
operator|new
name|TreeMap
argument_list|<
name|DatanodeInfo
argument_list|,
name|LightWeightHashSet
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
DECL|field|blockInvalidateLimit
specifier|private
specifier|final
name|int
name|blockInvalidateLimit
decl_stmt|;
comment|/**    * The period of pending time for block invalidation since the NameNode    * startup    */
DECL|field|pendingPeriodInMs
specifier|private
specifier|final
name|long
name|pendingPeriodInMs
decl_stmt|;
comment|/** the startup time */
DECL|field|startupTime
specifier|private
specifier|final
name|long
name|startupTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
DECL|method|InvalidateBlocks (final int blockInvalidateLimit, long pendingPeriodInMs)
name|InvalidateBlocks
parameter_list|(
specifier|final
name|int
name|blockInvalidateLimit
parameter_list|,
name|long
name|pendingPeriodInMs
parameter_list|)
block|{
name|this
operator|.
name|blockInvalidateLimit
operator|=
name|blockInvalidateLimit
expr_stmt|;
name|this
operator|.
name|pendingPeriodInMs
operator|=
name|pendingPeriodInMs
expr_stmt|;
name|printBlockDeletionTime
argument_list|(
name|BlockManager
operator|.
name|LOG
argument_list|)
expr_stmt|;
block|}
DECL|method|printBlockDeletionTime (final Log log)
specifier|private
name|void
name|printBlockDeletionTime
parameter_list|(
specifier|final
name|Log
name|log
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_STARTUP_DELAY_BLOCK_DELETION_SEC_KEY
operator|+
literal|" is set to "
operator|+
name|DFSUtil
operator|.
name|durationToString
argument_list|(
name|pendingPeriodInMs
argument_list|)
argument_list|)
expr_stmt|;
name|SimpleDateFormat
name|sdf
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy MMM dd HH:mm:ss"
argument_list|)
decl_stmt|;
name|Calendar
name|calendar
init|=
operator|new
name|GregorianCalendar
argument_list|()
decl_stmt|;
name|calendar
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
call|(
name|int
call|)
argument_list|(
name|this
operator|.
name|pendingPeriodInMs
operator|/
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"The block deletion will start around "
operator|+
name|sdf
operator|.
name|format
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
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
comment|/**    * @return true if the given storage has the given block listed for    * invalidation. Blocks are compared including their generation stamps:    * if a block is pending invalidation but with a different generation stamp,    * returns false.    */
DECL|method|contains (final DatanodeInfo dn, final Block block)
specifier|synchronized
name|boolean
name|contains
parameter_list|(
specifier|final
name|DatanodeInfo
name|dn
parameter_list|,
specifier|final
name|Block
name|block
parameter_list|)
block|{
specifier|final
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
name|s
init|=
name|node2blocks
operator|.
name|get
argument_list|(
name|dn
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
comment|// no invalidate blocks for this storage ID
block|}
name|Block
name|blockInSet
init|=
name|s
operator|.
name|getElement
argument_list|(
name|block
argument_list|)
decl_stmt|;
return|return
name|blockInSet
operator|!=
literal|null
operator|&&
name|block
operator|.
name|getGenerationStamp
argument_list|()
operator|==
name|blockInSet
operator|.
name|getGenerationStamp
argument_list|()
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
name|LightWeightHashSet
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
name|LightWeightHashSet
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
name|blockStateChangeLog
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
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Remove a storage from the invalidatesSet */
DECL|method|remove (final DatanodeInfo dn)
specifier|synchronized
name|void
name|remove
parameter_list|(
specifier|final
name|DatanodeInfo
name|dn
parameter_list|)
block|{
specifier|final
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
name|blocks
init|=
name|node2blocks
operator|.
name|remove
argument_list|(
name|dn
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
DECL|method|remove (final DatanodeInfo dn, final Block block)
specifier|synchronized
name|void
name|remove
parameter_list|(
specifier|final
name|DatanodeInfo
name|dn
parameter_list|,
specifier|final
name|Block
name|block
parameter_list|)
block|{
specifier|final
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
name|v
init|=
name|node2blocks
operator|.
name|get
argument_list|(
name|dn
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
name|dn
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
name|DatanodeInfo
argument_list|,
name|LightWeightHashSet
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
name|LightWeightHashSet
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
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|blocks
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** @return a list of the storage IDs. */
DECL|method|getDatanodes ()
specifier|synchronized
name|List
argument_list|<
name|DatanodeInfo
argument_list|>
name|getDatanodes
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|DatanodeInfo
argument_list|>
argument_list|(
name|node2blocks
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * @return the remianing pending time    */
annotation|@
name|VisibleForTesting
DECL|method|getInvalidationDelay ()
name|long
name|getInvalidationDelay
parameter_list|()
block|{
return|return
name|pendingPeriodInMs
operator|-
operator|(
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|startupTime
operator|)
return|;
block|}
DECL|method|invalidateWork (final DatanodeDescriptor dn)
specifier|synchronized
name|List
argument_list|<
name|Block
argument_list|>
name|invalidateWork
parameter_list|(
specifier|final
name|DatanodeDescriptor
name|dn
parameter_list|)
block|{
specifier|final
name|long
name|delay
init|=
name|getInvalidationDelay
argument_list|()
decl_stmt|;
if|if
condition|(
name|delay
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|BlockManager
operator|.
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|BlockManager
operator|.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Block deletion is delayed during NameNode startup. "
operator|+
literal|"The deletion will start after "
operator|+
name|delay
operator|+
literal|" ms."
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|final
name|LightWeightHashSet
argument_list|<
name|Block
argument_list|>
name|set
init|=
name|node2blocks
operator|.
name|get
argument_list|(
name|dn
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
name|blockInvalidateLimit
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Block
argument_list|>
name|toInvalidate
init|=
name|set
operator|.
name|pollN
argument_list|(
name|limit
argument_list|)
decl_stmt|;
comment|// If we send everything in this message, remove this node entry
if|if
condition|(
name|set
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|remove
argument_list|(
name|dn
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
DECL|method|clear ()
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|node2blocks
operator|.
name|clear
argument_list|()
expr_stmt|;
name|numBlocks
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class

end_unit

