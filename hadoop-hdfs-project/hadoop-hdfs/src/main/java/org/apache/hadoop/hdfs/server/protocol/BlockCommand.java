begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
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
name|protocol
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|classification
operator|.
name|InterfaceStability
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
name|blockmanagement
operator|.
name|DatanodeDescriptor
operator|.
name|BlockTargetPair
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
name|io
operator|.
name|Text
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
name|io
operator|.
name|Writable
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
name|io
operator|.
name|WritableFactories
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
name|io
operator|.
name|WritableFactory
import|;
end_import

begin_comment
comment|/****************************************************  * A BlockCommand is an instruction to a datanode   * regarding some blocks under its control.  It tells  * the DataNode to either invalidate a set of indicated  * blocks, or to copy a set of indicated blocks to   * another DataNode.  *   ****************************************************/
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|BlockCommand
specifier|public
class|class
name|BlockCommand
extends|extends
name|DatanodeCommand
block|{
comment|/**    * This constant is used to indicate that the block deletion does not need    * explicit ACK from the datanode. When a block is put into the list of blocks    * to be deleted, it's size is set to this constant. We assume that no block    * would actually have this size. Otherwise, we would miss ACKs for blocks    * with such size. Positive number is used for compatibility reasons.    */
DECL|field|NO_ACK
specifier|public
specifier|static
specifier|final
name|long
name|NO_ACK
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|poolId
name|String
name|poolId
decl_stmt|;
DECL|field|blocks
name|Block
name|blocks
index|[]
decl_stmt|;
DECL|field|targets
name|DatanodeInfo
name|targets
index|[]
index|[]
decl_stmt|;
DECL|method|BlockCommand ()
specifier|public
name|BlockCommand
parameter_list|()
block|{}
comment|/**    * Create BlockCommand for transferring blocks to another datanode    * @param blocktargetlist    blocks to be transferred     */
DECL|method|BlockCommand (int action, String poolId, List<BlockTargetPair> blocktargetlist)
specifier|public
name|BlockCommand
parameter_list|(
name|int
name|action
parameter_list|,
name|String
name|poolId
parameter_list|,
name|List
argument_list|<
name|BlockTargetPair
argument_list|>
name|blocktargetlist
parameter_list|)
block|{
name|super
argument_list|(
name|action
argument_list|)
expr_stmt|;
name|this
operator|.
name|poolId
operator|=
name|poolId
expr_stmt|;
name|blocks
operator|=
operator|new
name|Block
index|[
name|blocktargetlist
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|targets
operator|=
operator|new
name|DatanodeInfo
index|[
name|blocks
operator|.
name|length
index|]
index|[]
expr_stmt|;
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
name|BlockTargetPair
name|p
init|=
name|blocktargetlist
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|blocks
index|[
name|i
index|]
operator|=
name|p
operator|.
name|block
expr_stmt|;
name|targets
index|[
name|i
index|]
operator|=
name|p
operator|.
name|targets
expr_stmt|;
block|}
block|}
DECL|field|EMPTY_TARGET
specifier|private
specifier|static
specifier|final
name|DatanodeInfo
index|[]
index|[]
name|EMPTY_TARGET
init|=
block|{}
decl_stmt|;
comment|/**    * Create BlockCommand for the given action    * @param blocks blocks related to the action    */
DECL|method|BlockCommand (int action, String poolId, Block blocks[])
specifier|public
name|BlockCommand
parameter_list|(
name|int
name|action
parameter_list|,
name|String
name|poolId
parameter_list|,
name|Block
name|blocks
index|[]
parameter_list|)
block|{
name|super
argument_list|(
name|action
argument_list|)
expr_stmt|;
name|this
operator|.
name|poolId
operator|=
name|poolId
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
name|this
operator|.
name|targets
operator|=
name|EMPTY_TARGET
expr_stmt|;
block|}
DECL|method|getBlockPoolId ()
specifier|public
name|String
name|getBlockPoolId
parameter_list|()
block|{
return|return
name|poolId
return|;
block|}
DECL|method|getBlocks ()
specifier|public
name|Block
index|[]
name|getBlocks
parameter_list|()
block|{
return|return
name|blocks
return|;
block|}
DECL|method|getTargets ()
specifier|public
name|DatanodeInfo
index|[]
index|[]
name|getTargets
parameter_list|()
block|{
return|return
name|targets
return|;
block|}
comment|///////////////////////////////////////////
comment|// Writable
comment|///////////////////////////////////////////
static|static
block|{
comment|// register a ctor
name|WritableFactories
operator|.
name|setFactory
argument_list|(
name|BlockCommand
operator|.
name|class
argument_list|,
operator|new
name|WritableFactory
argument_list|()
block|{
specifier|public
name|Writable
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|BlockCommand
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|poolId
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|blocks
operator|.
name|length
argument_list|)
expr_stmt|;
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
name|blocks
index|[
name|i
index|]
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeInt
argument_list|(
name|targets
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|targets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|targets
index|[
name|i
index|]
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|targets
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|targets
index|[
name|i
index|]
index|[
name|j
index|]
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|poolId
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
operator|new
name|Block
index|[
name|in
operator|.
name|readInt
argument_list|()
index|]
expr_stmt|;
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
name|blocks
index|[
name|i
index|]
operator|=
operator|new
name|Block
argument_list|()
expr_stmt|;
name|blocks
index|[
name|i
index|]
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|targets
operator|=
operator|new
name|DatanodeInfo
index|[
name|in
operator|.
name|readInt
argument_list|()
index|]
index|[]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|targets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|targets
index|[
name|i
index|]
operator|=
operator|new
name|DatanodeInfo
index|[
name|in
operator|.
name|readInt
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|targets
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|targets
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
operator|new
name|DatanodeInfo
argument_list|()
expr_stmt|;
name|targets
index|[
name|i
index|]
index|[
name|j
index|]
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

