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
comment|/**  * RawErasureDecoder performs decoding given chunks of input data and generates  * missing data that corresponds to an erasure code scheme, like XOR and  * Reed-Solomon.  *  * It extends the {@link RawErasureCoder} interface.  */
end_comment

begin_interface
DECL|interface|RawErasureDecoder
specifier|public
interface|interface
name|RawErasureDecoder
extends|extends
name|RawErasureCoder
block|{
comment|/**    * Decode with inputs and erasedIndexes, generates outputs.    * How to prepare for inputs:    * 1. Create an array containing parity units + data units. Please note the    *    parity units should be first or before the data units.    * 2. Set null in the array locations specified via erasedIndexes to indicate    *    they're erased and no data are to read from;    * 3. Set null in the array locations for extra redundant items, as they're    *    not necessary to read when decoding. For example in RS-6-3, if only 1    *    unit is really erased, then we have 2 extra items as redundant. They can    *    be set as null to indicate no data will be used from them.    *    * For an example using RS (6, 3), assuming sources (d0, d1, d2, d3, d4, d5)    * and parities (p0, p1, p2), d2 being erased. We can and may want to use only    * 6 units like (d1, d3, d4, d5, p0, p2) to recover d2. We will have:    *     inputs = [p0, null(p1), p2, null(d0), d1, null(d2), d3, d4, d5]    *     erasedIndexes = [5] // index of d2 into inputs array    *     outputs = [a-writable-buffer]    *    * Note, for both inputs and outputs, no mixing of on-heap buffers and direct    * buffers are allowed.    *    * @param inputs inputs to read data from, contents may change after the call    * @param erasedIndexes indexes of erased units in the inputs array    * @param outputs outputs to write into for data generated according to    *                erasedIndexes, ready for reading the result data from after    *                the call    */
DECL|method|decode (ByteBuffer[] inputs, int[] erasedIndexes, ByteBuffer[] outputs)
specifier|public
name|void
name|decode
parameter_list|(
name|ByteBuffer
index|[]
name|inputs
parameter_list|,
name|int
index|[]
name|erasedIndexes
parameter_list|,
name|ByteBuffer
index|[]
name|outputs
parameter_list|)
function_decl|;
comment|/**    * Decode with inputs and erasedIndexes, generates outputs. More see above.    * @param inputs inputs to read data from, contents may change after the call    * @param erasedIndexes indexes of erased units in the inputs array    * @param outputs outputs to write into for data generated according to    *                erasedIndexes, ready for reading the result data from after    *                the call    */
DECL|method|decode (byte[][] inputs, int[] erasedIndexes, byte[][] outputs)
specifier|public
name|void
name|decode
parameter_list|(
name|byte
index|[]
index|[]
name|inputs
parameter_list|,
name|int
index|[]
name|erasedIndexes
parameter_list|,
name|byte
index|[]
index|[]
name|outputs
parameter_list|)
function_decl|;
comment|/**    * Decode with inputs and erasedIndexes, generates outputs. More see above.    *    * Note, for both input and output ECChunks, no mixing of on-heap buffers and    * direct buffers are allowed.    *    * @param inputs inputs to read data from, contents may change after the call    * @param erasedIndexes indexes of erased units in the inputs array    * @param outputs outputs to write into for data generated according to    *                erasedIndexes, ready for reading the result data from after    *                the call    */
DECL|method|decode (ECChunk[] inputs, int[] erasedIndexes, ECChunk[] outputs)
specifier|public
name|void
name|decode
parameter_list|(
name|ECChunk
index|[]
name|inputs
parameter_list|,
name|int
index|[]
name|erasedIndexes
parameter_list|,
name|ECChunk
index|[]
name|outputs
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

