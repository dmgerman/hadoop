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
name|fs
operator|.
name|StorageType
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
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|DatanodeStorageInfo
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
specifier|final
name|String
name|poolId
decl_stmt|;
DECL|field|blocks
specifier|final
name|Block
index|[]
name|blocks
decl_stmt|;
DECL|field|targets
specifier|final
name|DatanodeInfo
index|[]
index|[]
name|targets
decl_stmt|;
DECL|field|targetStorageTypes
specifier|final
name|StorageType
index|[]
index|[]
name|targetStorageTypes
decl_stmt|;
DECL|field|targetStorageIDs
specifier|final
name|String
index|[]
index|[]
name|targetStorageIDs
decl_stmt|;
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
name|targetStorageTypes
operator|=
operator|new
name|StorageType
index|[
name|blocks
operator|.
name|length
index|]
index|[]
expr_stmt|;
name|targetStorageIDs
operator|=
operator|new
name|String
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
name|DatanodeStorageInfo
operator|.
name|toDatanodeInfos
argument_list|(
name|p
operator|.
name|targets
argument_list|)
expr_stmt|;
name|targetStorageTypes
index|[
name|i
index|]
operator|=
name|DatanodeStorageInfo
operator|.
name|toStorageTypes
argument_list|(
name|p
operator|.
name|targets
argument_list|)
expr_stmt|;
name|targetStorageIDs
index|[
name|i
index|]
operator|=
name|DatanodeStorageInfo
operator|.
name|toStorageIDs
argument_list|(
name|p
operator|.
name|targets
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|EMPTY_TARGET_DATANODES
specifier|private
specifier|static
specifier|final
name|DatanodeInfo
index|[]
index|[]
name|EMPTY_TARGET_DATANODES
init|=
block|{}
decl_stmt|;
DECL|field|EMPTY_TARGET_STORAGE_TYPES
specifier|private
specifier|static
specifier|final
name|StorageType
index|[]
index|[]
name|EMPTY_TARGET_STORAGE_TYPES
init|=
block|{}
decl_stmt|;
DECL|field|EMPTY_TARGET_STORAGEIDS
specifier|private
specifier|static
specifier|final
name|String
index|[]
index|[]
name|EMPTY_TARGET_STORAGEIDS
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
name|this
argument_list|(
name|action
argument_list|,
name|poolId
argument_list|,
name|blocks
argument_list|,
name|EMPTY_TARGET_DATANODES
argument_list|,
name|EMPTY_TARGET_STORAGE_TYPES
argument_list|,
name|EMPTY_TARGET_STORAGEIDS
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create BlockCommand for the given action    * @param blocks blocks related to the action    */
DECL|method|BlockCommand (int action, String poolId, Block[] blocks, DatanodeInfo[][] targets, StorageType[][] targetStorageTypes, String[][] targetStorageIDs)
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
index|[]
name|blocks
parameter_list|,
name|DatanodeInfo
index|[]
index|[]
name|targets
parameter_list|,
name|StorageType
index|[]
index|[]
name|targetStorageTypes
parameter_list|,
name|String
index|[]
index|[]
name|targetStorageIDs
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
name|targets
expr_stmt|;
name|this
operator|.
name|targetStorageTypes
operator|=
name|targetStorageTypes
expr_stmt|;
name|this
operator|.
name|targetStorageIDs
operator|=
name|targetStorageIDs
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
DECL|method|getTargetStorageTypes ()
specifier|public
name|StorageType
index|[]
index|[]
name|getTargetStorageTypes
parameter_list|()
block|{
return|return
name|targetStorageTypes
return|;
block|}
DECL|method|getTargetStorageIDs ()
specifier|public
name|String
index|[]
index|[]
name|getTargetStorageIDs
parameter_list|()
block|{
return|return
name|targetStorageIDs
return|;
block|}
block|}
end_class

end_unit

