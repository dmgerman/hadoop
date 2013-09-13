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
throws|throws
name|IOException
function_decl|;
comment|/**     * Get content summary.    */
DECL|method|computeContentSummary ()
specifier|public
name|ContentSummary
name|computeContentSummary
parameter_list|()
function_decl|;
comment|/**    * @return the number of blocks    */
DECL|method|numBlocks ()
specifier|public
name|int
name|numBlocks
parameter_list|()
function_decl|;
comment|/**    * Get the blocks.    */
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
comment|/**    * Get block replication for the collection     * @return block replication value    */
DECL|method|getBlockReplication ()
specifier|public
name|short
name|getBlockReplication
parameter_list|()
function_decl|;
comment|/**    * Set cache replication factor for the collection    */
DECL|method|setCacheReplication (short cacheReplication)
specifier|public
name|void
name|setCacheReplication
parameter_list|(
name|short
name|cacheReplication
parameter_list|)
function_decl|;
comment|/**    * Get cache replication factor for the collection    * @return cache replication value    */
DECL|method|getCacheReplication ()
specifier|public
name|short
name|getCacheReplication
parameter_list|()
function_decl|;
comment|/**    * Get the name of the collection.    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

