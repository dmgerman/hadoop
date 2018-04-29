begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
package|;
end_package

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
name|Collections
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
name|common
operator|.
name|sps
operator|.
name|BlockMovementAttemptFinished
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
name|sps
operator|.
name|BlocksMovementsStatusHandler
import|;
end_import

begin_comment
comment|/**  * Blocks movements status handler, which is used to collect details of the  * completed block movements and later these attempted finished(with success or  * failure) blocks can be accessed to notify respective listeners, if any.  */
end_comment

begin_class
DECL|class|SimpleBlocksMovementsStatusHandler
specifier|public
class|class
name|SimpleBlocksMovementsStatusHandler
implements|implements
name|BlocksMovementsStatusHandler
block|{
DECL|field|blockIdVsMovementStatus
specifier|private
specifier|final
name|List
argument_list|<
name|Block
argument_list|>
name|blockIdVsMovementStatus
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Collect all the storage movement attempt finished blocks. Later this will    * be send to namenode via heart beat.    *    * @param moveAttemptFinishedBlk    *          storage movement attempt finished block    */
DECL|method|handle (BlockMovementAttemptFinished moveAttemptFinishedBlk)
specifier|public
name|void
name|handle
parameter_list|(
name|BlockMovementAttemptFinished
name|moveAttemptFinishedBlk
parameter_list|)
block|{
comment|// Adding to the tracking report list. Later this can be accessed to know
comment|// the attempted block movements.
synchronized|synchronized
init|(
name|blockIdVsMovementStatus
init|)
block|{
name|blockIdVsMovementStatus
operator|.
name|add
argument_list|(
name|moveAttemptFinishedBlk
operator|.
name|getBlock
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @return unmodifiable list of storage movement attempt finished blocks.    */
DECL|method|getMoveAttemptFinishedBlocks ()
specifier|public
name|List
argument_list|<
name|Block
argument_list|>
name|getMoveAttemptFinishedBlocks
parameter_list|()
block|{
name|List
argument_list|<
name|Block
argument_list|>
name|moveAttemptFinishedBlks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// 1. Adding all the completed block ids.
synchronized|synchronized
init|(
name|blockIdVsMovementStatus
init|)
block|{
if|if
condition|(
name|blockIdVsMovementStatus
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|moveAttemptFinishedBlks
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|blockIdVsMovementStatus
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|moveAttemptFinishedBlks
return|;
block|}
comment|/**    * Remove the storage movement attempt finished blocks from the tracking list.    *    * @param moveAttemptFinishedBlks    *          set of storage movement attempt finished blocks    */
DECL|method|remove (List<Block> moveAttemptFinishedBlks)
specifier|public
name|void
name|remove
parameter_list|(
name|List
argument_list|<
name|Block
argument_list|>
name|moveAttemptFinishedBlks
parameter_list|)
block|{
if|if
condition|(
name|moveAttemptFinishedBlks
operator|!=
literal|null
condition|)
block|{
name|blockIdVsMovementStatus
operator|.
name|removeAll
argument_list|(
name|moveAttemptFinishedBlks
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Clear the blockID vs movement status tracking map.    */
DECL|method|removeAll ()
specifier|public
name|void
name|removeAll
parameter_list|()
block|{
synchronized|synchronized
init|(
name|blockIdVsMovementStatus
init|)
block|{
name|blockIdVsMovementStatus
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

