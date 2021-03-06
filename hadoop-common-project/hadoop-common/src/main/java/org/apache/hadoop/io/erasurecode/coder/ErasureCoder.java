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
name|conf
operator|.
name|Configurable
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
name|ECBlockGroup
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
name|ErasureCoderOptions
import|;
end_import

begin_comment
comment|/**  * An erasure coder to perform encoding or decoding given a group. Generally it  * involves calculating necessary internal steps according to codec logic. For  * each step,it calculates necessary input blocks to read chunks from and output  * parity blocks to write parity chunks into from the group. It also takes care  * of appropriate raw coder to use for the step. And encapsulates all the  * necessary info (input blocks, output blocks and raw coder) into a step  * represented by {@link ErasureCodingStep}. ErasureCoder callers can use the  * step to do the real work with retrieved input and output chunks.  *  * Note, currently only one coding step is supported. Will support complex cases  * of multiple coding steps.  *  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|ErasureCoder
specifier|public
interface|interface
name|ErasureCoder
extends|extends
name|Configurable
block|{
comment|/**    * The number of data input units for the coding. A unit can be a byte, chunk    * or buffer or even a block.    * @return count of data input units    */
DECL|method|getNumDataUnits ()
name|int
name|getNumDataUnits
parameter_list|()
function_decl|;
comment|/**    * The number of parity output units for the coding. A unit can be a byte,    * chunk, buffer or even a block.    * @return count of parity output units    */
DECL|method|getNumParityUnits ()
name|int
name|getNumParityUnits
parameter_list|()
function_decl|;
comment|/**    * The options of erasure coder. This option is passed to    * raw erasure coder as it is.    * @return erasure coder options    */
DECL|method|getOptions ()
name|ErasureCoderOptions
name|getOptions
parameter_list|()
function_decl|;
comment|/**    * Calculate the encoding or decoding steps given a block blockGroup.    *    * Note, currently only one coding step is supported. Will support complex    * cases of multiple coding steps.    *    * @param blockGroup the erasure coding block group containing all necessary    *                   information for codec calculation    */
DECL|method|calculateCoding (ECBlockGroup blockGroup)
name|ErasureCodingStep
name|calculateCoding
parameter_list|(
name|ECBlockGroup
name|blockGroup
parameter_list|)
function_decl|;
comment|/**    * Tell if direct or off-heap buffer is preferred or not. It's for callers to    * decide how to allocate coding chunk buffers, either on heap or off heap.    * It will return false by default.    * @return true if direct buffer is preferred for performance consideration,    * otherwise false.    */
DECL|method|preferDirectBuffer ()
name|boolean
name|preferDirectBuffer
parameter_list|()
function_decl|;
comment|/**    * Release the resources if any. Good chance to invoke    * RawErasureCoder#release.    */
DECL|method|release ()
name|void
name|release
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

