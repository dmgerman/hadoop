begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.protocol.commands
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|commands
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|DeletedBlocksTransaction
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMDeleteBlocksCmdResponseProto
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|Type
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

begin_comment
comment|/**  * A SCM command asks a datanode to delete a number of blocks.  */
end_comment

begin_class
DECL|class|DeleteBlocksCommand
specifier|public
class|class
name|DeleteBlocksCommand
extends|extends
name|SCMCommand
argument_list|<
name|SCMDeleteBlocksCmdResponseProto
argument_list|>
block|{
DECL|field|blocksTobeDeleted
specifier|private
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|blocksTobeDeleted
decl_stmt|;
DECL|method|DeleteBlocksCommand (List<DeletedBlocksTransaction> blocks)
specifier|public
name|DeleteBlocksCommand
parameter_list|(
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|blocks
parameter_list|)
block|{
name|this
operator|.
name|blocksTobeDeleted
operator|=
name|blocks
expr_stmt|;
block|}
DECL|method|blocksTobeDeleted ()
specifier|public
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|blocksTobeDeleted
parameter_list|()
block|{
return|return
name|this
operator|.
name|blocksTobeDeleted
return|;
block|}
annotation|@
name|Override
DECL|method|getType ()
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|deleteBlocksCommand
return|;
block|}
annotation|@
name|Override
DECL|method|getProtoBufMessage ()
specifier|public
name|byte
index|[]
name|getProtoBufMessage
parameter_list|()
block|{
return|return
name|getProto
argument_list|()
operator|.
name|toByteArray
argument_list|()
return|;
block|}
DECL|method|getFromProtobuf ( SCMDeleteBlocksCmdResponseProto deleteBlocksProto)
specifier|public
specifier|static
name|DeleteBlocksCommand
name|getFromProtobuf
parameter_list|(
name|SCMDeleteBlocksCmdResponseProto
name|deleteBlocksProto
parameter_list|)
block|{
return|return
operator|new
name|DeleteBlocksCommand
argument_list|(
name|deleteBlocksProto
operator|.
name|getDeletedBlocksTransactionsList
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getProto ()
specifier|public
name|SCMDeleteBlocksCmdResponseProto
name|getProto
parameter_list|()
block|{
return|return
name|SCMDeleteBlocksCmdResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|addAllDeletedBlocksTransactions
argument_list|(
name|blocksTobeDeleted
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

