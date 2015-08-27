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
name|namenode
operator|.
name|INode
operator|.
name|BlocksMapUpdateInfo
import|;
end_import

begin_comment
comment|/**  * Feature for under-construction file.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FileUnderConstructionFeature
specifier|public
class|class
name|FileUnderConstructionFeature
implements|implements
name|INode
operator|.
name|Feature
block|{
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
DECL|method|FileUnderConstructionFeature (final String clientName, final String clientMachine)
specifier|public
name|FileUnderConstructionFeature
parameter_list|(
specifier|final
name|String
name|clientName
parameter_list|,
specifier|final
name|String
name|clientMachine
parameter_list|)
block|{
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
comment|/**    * Update the length for the last block    *    * @param lastBlockLength    *          The length of the last block reported from client    * @throws IOException    */
DECL|method|updateLengthOfLastBlock (INodeFile f, long lastBlockLength)
name|void
name|updateLengthOfLastBlock
parameter_list|(
name|INodeFile
name|f
parameter_list|,
name|long
name|lastBlockLength
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockInfo
name|lastBlock
init|=
name|f
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
name|f
operator|.
name|getFullPathName
argument_list|()
operator|+
literal|" is null when updating its length"
assert|;
assert|assert
operator|!
name|lastBlock
operator|.
name|isComplete
argument_list|()
operator|:
literal|"The last block for path "
operator|+
name|f
operator|.
name|getFullPathName
argument_list|()
operator|+
literal|" is not under-construction when updating its length"
assert|;
name|lastBlock
operator|.
name|setNumBytes
argument_list|(
name|lastBlockLength
argument_list|)
expr_stmt|;
block|}
comment|/**    * When deleting a file in the current fs directory, and the file is contained    * in a snapshot, we should delete the last block if it's under construction    * and its size is 0.    */
DECL|method|cleanZeroSizeBlock (final INodeFile f, final BlocksMapUpdateInfo collectedBlocks)
name|void
name|cleanZeroSizeBlock
parameter_list|(
specifier|final
name|INodeFile
name|f
parameter_list|,
specifier|final
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|)
block|{
specifier|final
name|BlockInfo
index|[]
name|blocks
init|=
name|f
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
if|if
condition|(
name|blocks
operator|!=
literal|null
operator|&&
name|blocks
operator|.
name|length
operator|>
literal|0
operator|&&
operator|!
name|blocks
index|[
name|blocks
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|isComplete
argument_list|()
condition|)
block|{
name|BlockInfo
name|lastUC
init|=
name|blocks
index|[
name|blocks
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|lastUC
operator|.
name|getNumBytes
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// this is a 0-sized block. do not need check its UC state here
name|collectedBlocks
operator|.
name|addDeleteBlock
argument_list|(
name|lastUC
argument_list|)
expr_stmt|;
name|f
operator|.
name|removeLastBlock
argument_list|(
name|lastUC
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

