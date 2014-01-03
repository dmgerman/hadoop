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

begin_comment
comment|/**   * This interface is used by the block manager to expose a  * few characteristics of a collection of Block/BlockUnderConstruction.  */
end_comment

begin_interface
DECL|interface|MutableBlockCollection
specifier|public
interface|interface
name|MutableBlockCollection
extends|extends
name|BlockCollection
block|{
comment|/**    * Set the block at the given index.    */
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
DECL|method|setLastBlock (BlockInfo lastBlock, DatanodeStorageInfo[] storages)
specifier|public
name|BlockInfoUnderConstruction
name|setLastBlock
parameter_list|(
name|BlockInfo
name|lastBlock
parameter_list|,
name|DatanodeStorageInfo
index|[]
name|storages
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

