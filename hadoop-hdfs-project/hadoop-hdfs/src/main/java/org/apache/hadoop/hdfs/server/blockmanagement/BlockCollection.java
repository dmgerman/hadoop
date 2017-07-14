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
name|fs
operator|.
name|ContentSummary
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
name|security
operator|.
name|AccessControlException
import|;
end_import

begin_comment
comment|/**   * This interface is used by the block manager to expose a  * few characteristics of a collection of Block/BlockUnderConstruction.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|BlockCollection
specifier|public
interface|interface
name|BlockCollection
block|{
comment|/**    * Get the last block of the collection.    */
DECL|method|getLastBlock ()
specifier|public
name|BlockInfo
name|getLastBlock
parameter_list|()
function_decl|;
comment|/**     * Get content summary.    */
DECL|method|computeContentSummary (BlockStoragePolicySuite bsps)
specifier|public
name|ContentSummary
name|computeContentSummary
parameter_list|(
name|BlockStoragePolicySuite
name|bsps
parameter_list|)
throws|throws
name|AccessControlException
function_decl|;
comment|/**    * @return the number of blocks or block groups    */
DECL|method|numBlocks ()
specifier|public
name|int
name|numBlocks
parameter_list|()
function_decl|;
comment|/**    * Get the blocks (striped or contiguous).    */
DECL|method|getBlocks ()
specifier|public
name|BlockInfo
index|[]
name|getBlocks
parameter_list|()
function_decl|;
comment|/**    * Get preferred block size for the collection     * @return preferred block size in bytes    */
DECL|method|getPreferredBlockSize ()
specifier|public
name|long
name|getPreferredBlockSize
parameter_list|()
function_decl|;
comment|/**    * Get block replication for the collection.    * @return block replication value. Return 0 if the file is erasure coded.    */
DECL|method|getPreferredBlockReplication ()
specifier|public
name|short
name|getPreferredBlockReplication
parameter_list|()
function_decl|;
comment|/**    * @return the storage policy ID.    */
DECL|method|getStoragePolicyID ()
specifier|public
name|byte
name|getStoragePolicyID
parameter_list|()
function_decl|;
comment|/**    * Get the name of the collection.    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * Set the block (contiguous or striped) at the given index.    */
DECL|method|setBlock (int index, BlockInfo blk)
specifier|public
name|void
name|setBlock
parameter_list|(
name|int
name|index
parameter_list|,
name|BlockInfo
name|blk
parameter_list|)
function_decl|;
comment|/**    * Convert the last block of the collection to an under-construction block    * and set the locations.    */
DECL|method|convertLastBlockToUC (BlockInfo lastBlock, DatanodeStorageInfo[] targets)
specifier|public
name|void
name|convertLastBlockToUC
parameter_list|(
name|BlockInfo
name|lastBlock
parameter_list|,
name|DatanodeStorageInfo
index|[]
name|targets
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @return whether the block collection is under construction.    */
DECL|method|isUnderConstruction ()
specifier|public
name|boolean
name|isUnderConstruction
parameter_list|()
function_decl|;
comment|/**    * @return whether the block collection is in striping format    */
DECL|method|isStriped ()
name|boolean
name|isStriped
parameter_list|()
function_decl|;
comment|/**    * @return the id for the block collection    */
DECL|method|getId ()
name|long
name|getId
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

