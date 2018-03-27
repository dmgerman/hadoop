begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.block
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|block
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|hdsl
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|hdsl
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
name|scm
operator|.
name|container
operator|.
name|Mapping
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerInfo
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
name|collect
operator|.
name|ArrayListMultimap
import|;
end_import

begin_comment
comment|/**  * A wrapper class to hold info about datanode and all deleted block  * transactions that will be sent to this datanode.  */
end_comment

begin_class
DECL|class|DatanodeDeletedBlockTransactions
specifier|public
class|class
name|DatanodeDeletedBlockTransactions
block|{
DECL|field|nodeNum
specifier|private
name|int
name|nodeNum
decl_stmt|;
comment|// The throttle size for each datanode.
DECL|field|maximumAllowedTXNum
specifier|private
name|int
name|maximumAllowedTXNum
decl_stmt|;
comment|// Current counter of inserted TX.
DECL|field|currentTXNum
specifier|private
name|int
name|currentTXNum
decl_stmt|;
DECL|field|mappingService
specifier|private
name|Mapping
name|mappingService
decl_stmt|;
comment|// A list of TXs mapped to a certain datanode ID.
specifier|private
specifier|final
name|ArrayListMultimap
argument_list|<
name|UUID
argument_list|,
name|DeletedBlocksTransaction
argument_list|>
DECL|field|transactions
name|transactions
decl_stmt|;
DECL|method|DatanodeDeletedBlockTransactions (Mapping mappingService, int maximumAllowedTXNum, int nodeNum)
name|DatanodeDeletedBlockTransactions
parameter_list|(
name|Mapping
name|mappingService
parameter_list|,
name|int
name|maximumAllowedTXNum
parameter_list|,
name|int
name|nodeNum
parameter_list|)
block|{
name|this
operator|.
name|transactions
operator|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
expr_stmt|;
name|this
operator|.
name|mappingService
operator|=
name|mappingService
expr_stmt|;
name|this
operator|.
name|maximumAllowedTXNum
operator|=
name|maximumAllowedTXNum
expr_stmt|;
name|this
operator|.
name|nodeNum
operator|=
name|nodeNum
expr_stmt|;
block|}
DECL|method|addTransaction (DeletedBlocksTransaction tx)
specifier|public
name|void
name|addTransaction
parameter_list|(
name|DeletedBlocksTransaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerInfo
name|info
init|=
literal|null
decl_stmt|;
try|try
block|{
name|info
operator|=
name|mappingService
operator|.
name|getContainer
argument_list|(
name|tx
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|SCMBlockDeletingService
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Got container info error."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
name|SCMBlockDeletingService
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Container {} not found, continue to process next"
argument_list|,
name|tx
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
name|DatanodeDetails
name|dd
range|:
name|info
operator|.
name|getPipeline
argument_list|()
operator|.
name|getMachines
argument_list|()
control|)
block|{
name|UUID
name|dnID
init|=
name|dd
operator|.
name|getUuid
argument_list|()
decl_stmt|;
if|if
condition|(
name|transactions
operator|.
name|containsKey
argument_list|(
name|dnID
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|txs
init|=
name|transactions
operator|.
name|get
argument_list|(
name|dnID
argument_list|)
decl_stmt|;
if|if
condition|(
name|txs
operator|!=
literal|null
operator|&&
name|txs
operator|.
name|size
argument_list|()
operator|<
name|maximumAllowedTXNum
condition|)
block|{
name|boolean
name|hasContained
init|=
literal|false
decl_stmt|;
for|for
control|(
name|DeletedBlocksTransaction
name|t
range|:
name|txs
control|)
block|{
if|if
condition|(
name|t
operator|.
name|getContainerName
argument_list|()
operator|.
name|equals
argument_list|(
name|tx
operator|.
name|getContainerName
argument_list|()
argument_list|)
condition|)
block|{
name|hasContained
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|hasContained
condition|)
block|{
name|txs
operator|.
name|add
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|currentTXNum
operator|++
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|currentTXNum
operator|++
expr_stmt|;
name|transactions
operator|.
name|put
argument_list|(
name|dnID
argument_list|,
name|tx
argument_list|)
expr_stmt|;
block|}
name|SCMBlockDeletingService
operator|.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Transaction added: {}<- TX({})"
argument_list|,
name|dnID
argument_list|,
name|tx
operator|.
name|getTxID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getDatanodeIDs ()
name|Set
argument_list|<
name|UUID
argument_list|>
name|getDatanodeIDs
parameter_list|()
block|{
return|return
name|transactions
operator|.
name|keySet
argument_list|()
return|;
block|}
DECL|method|isEmpty ()
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|transactions
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|hasTransactions (UUID dnId)
name|boolean
name|hasTransactions
parameter_list|(
name|UUID
name|dnId
parameter_list|)
block|{
return|return
name|transactions
operator|.
name|containsKey
argument_list|(
name|dnId
argument_list|)
operator|&&
operator|!
name|transactions
operator|.
name|get
argument_list|(
name|dnId
argument_list|)
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|getDatanodeTransactions (UUID dnId)
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|getDatanodeTransactions
parameter_list|(
name|UUID
name|dnId
parameter_list|)
block|{
return|return
name|transactions
operator|.
name|get
argument_list|(
name|dnId
argument_list|)
return|;
block|}
DECL|method|getTransactionIDList (UUID dnId)
name|List
argument_list|<
name|String
argument_list|>
name|getTransactionIDList
parameter_list|(
name|UUID
name|dnId
parameter_list|)
block|{
if|if
condition|(
name|hasTransactions
argument_list|(
name|dnId
argument_list|)
condition|)
block|{
return|return
name|transactions
operator|.
name|get
argument_list|(
name|dnId
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|DeletedBlocksTransaction
operator|::
name|getTxID
argument_list|)
operator|.
name|map
argument_list|(
name|String
operator|::
name|valueOf
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
DECL|method|isFull ()
name|boolean
name|isFull
parameter_list|()
block|{
return|return
name|currentTXNum
operator|>=
name|maximumAllowedTXNum
operator|*
name|nodeNum
return|;
block|}
DECL|method|getTXNum ()
name|int
name|getTXNum
parameter_list|()
block|{
return|return
name|currentTXNum
return|;
block|}
block|}
end_class

end_unit

