begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.scm.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|AllocatedBlock
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
name|DeleteBlockResult
import|;
end_import

begin_comment
comment|/**  * ScmBlockLocationProtocol is used by an HDFS node to find the set of nodes  * to read/write a block.  */
end_comment

begin_interface
DECL|interface|ScmBlockLocationProtocol
specifier|public
interface|interface
name|ScmBlockLocationProtocol
block|{
comment|/**    * Find the set of nodes to read/write a block, as    * identified by the block key.  This method supports batch lookup by    * passing multiple keys.    *    * @param keys batch of block keys to find    * @return allocated blocks for each block key    * @throws IOException if there is any failure    */
DECL|method|getBlockLocations (Set<String> keys)
name|Set
argument_list|<
name|AllocatedBlock
argument_list|>
name|getBlockLocations
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|keys
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Asks SCM where a block should be allocated. SCM responds with the    * set of datanodes that should be used creating this block.    * @param size - size of the block.    * @return allocated block accessing info (key, pipeline).    * @throws IOException    */
DECL|method|allocateBlock (long size)
name|AllocatedBlock
name|allocateBlock
parameter_list|(
name|long
name|size
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Delete the set of keys specified.    *    * @param keys batch of block keys to delete.    * @return list of block deletion results.    * @throws IOException if there is any failure.    *    */
DECL|method|deleteBlocks (Set<String> keys)
name|List
argument_list|<
name|DeleteBlockResult
argument_list|>
name|deleteBlocks
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|keys
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

