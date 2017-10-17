begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode.coder
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
operator|.
name|coder
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
name|io
operator|.
name|erasurecode
operator|.
name|ECBlock
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
name|erasurecode
operator|.
name|ECChunk
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

begin_comment
comment|/**  * Erasure coding step that's involved in encoding/decoding of a block group.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|ErasureCodingStep
specifier|public
interface|interface
name|ErasureCodingStep
block|{
comment|/**    * Input blocks of readable data involved in this step, may be data blocks    * or parity blocks.    * @return input blocks    */
DECL|method|getInputBlocks ()
name|ECBlock
index|[]
name|getInputBlocks
parameter_list|()
function_decl|;
comment|/**    * Output blocks of writable buffers involved in this step, may be data    * blocks or parity blocks.    * @return output blocks    */
DECL|method|getOutputBlocks ()
name|ECBlock
index|[]
name|getOutputBlocks
parameter_list|()
function_decl|;
comment|/**    * Perform encoding or decoding given the input chunks, and generated results    * will be written to the output chunks.    * @param inputChunks    * @param outputChunks    */
DECL|method|performCoding (ECChunk[] inputChunks, ECChunk[] outputChunks)
name|void
name|performCoding
parameter_list|(
name|ECChunk
index|[]
name|inputChunks
parameter_list|,
name|ECChunk
index|[]
name|outputChunks
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Notify erasure coder that all the chunks of input blocks are processed so    * the coder can be able to update internal states, considering next step.    */
DECL|method|finish ()
name|void
name|finish
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

