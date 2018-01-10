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
name|ozone
operator|.
name|common
operator|.
name|DeleteBlockGroupResult
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
name|common
operator|.
name|BlockGroup
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
operator|.
name|ReplicationType
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
name|OzoneProtos
operator|.
name|ReplicationFactor
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
name|ScmInfo
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
DECL|method|allocateBlock (long size, ReplicationType type, ReplicationFactor factor, String owner)
name|AllocatedBlock
name|allocateBlock
parameter_list|(
name|long
name|size
parameter_list|,
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|,
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Delete blocks for a set of object keys.    *    * @param keyBlocksInfoList Map of object key and its blocks.    * @return list of block deletion results.    * @throws IOException if there is any failure.    */
name|List
argument_list|<
name|DeleteBlockGroupResult
argument_list|>
DECL|method|deleteKeyBlocks (List<BlockGroup> keyBlocksInfoList)
name|deleteKeyBlocks
parameter_list|(
name|List
argument_list|<
name|BlockGroup
argument_list|>
name|keyBlocksInfoList
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the Clusterid and SCM Id from SCM.    */
DECL|method|getScmInfo ()
name|ScmInfo
name|getScmInfo
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

