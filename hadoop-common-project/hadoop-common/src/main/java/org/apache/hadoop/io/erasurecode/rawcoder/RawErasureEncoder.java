begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode.rawcoder
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
name|rawcoder
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
name|ECChunk
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_comment
comment|/**  * RawErasureEncoder performs encoding given chunks of input data and generates  * parity outputs that corresponds to an erasure code scheme, like XOR and  * Reed-Solomon.  *  * It extends the {@link RawErasureCoder} interface.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|RawErasureEncoder
specifier|public
interface|interface
name|RawErasureEncoder
extends|extends
name|RawErasureCoder
block|{
comment|/**    * Encode with inputs and generates outputs.    *    * Note, for both inputs and outputs, no mixing of on-heap buffers and direct    * buffers are allowed.    *    * @param inputs inputs to read data from, contents may change after the call    * @param outputs    */
DECL|method|encode (ByteBuffer[] inputs, ByteBuffer[] outputs)
specifier|public
name|void
name|encode
parameter_list|(
name|ByteBuffer
index|[]
name|inputs
parameter_list|,
name|ByteBuffer
index|[]
name|outputs
parameter_list|)
function_decl|;
comment|/**    * Encode with inputs and generates outputs    * @param inputs inputs to read data from, contents may change after the call    * @param outputs outputs to write into for data generated, ready for reading    *                the result data from after the call    */
DECL|method|encode (byte[][] inputs, byte[][] outputs)
specifier|public
name|void
name|encode
parameter_list|(
name|byte
index|[]
index|[]
name|inputs
parameter_list|,
name|byte
index|[]
index|[]
name|outputs
parameter_list|)
function_decl|;
comment|/**    * Encode with inputs and generates outputs.    *    * Note, for both input and output ECChunks, no mixing of on-heap buffers and    * direct buffers are allowed.    *    * @param inputs inputs to read data from, contents may change after the call    * @param outputs outputs to write into for data generated, ready for reading    *                the result data from after the call    */
DECL|method|encode (ECChunk[] inputs, ECChunk[] outputs)
specifier|public
name|void
name|encode
parameter_list|(
name|ECChunk
index|[]
name|inputs
parameter_list|,
name|ECChunk
index|[]
name|outputs
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

