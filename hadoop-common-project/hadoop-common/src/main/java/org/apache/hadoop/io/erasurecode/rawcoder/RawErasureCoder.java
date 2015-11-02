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
name|conf
operator|.
name|Configurable
import|;
end_import

begin_comment
comment|/**  * RawErasureCoder is a common interface for {@link RawErasureEncoder} and  * {@link RawErasureDecoder} as both encoder and decoder share some properties.  *  * RawErasureCoder is part of ErasureCodec framework, where ErasureCoder is  * used to encode/decode a group of blocks (BlockGroup) according to the codec  * specific BlockGroup layout and logic. An ErasureCoder extracts chunks of  * data from the blocks and can employ various low level RawErasureCoders to  * perform encoding/decoding against the chunks.  *  * To distinguish from ErasureCoder, here RawErasureCoder is used to mean the  * low level constructs, since it only takes care of the math calculation with  * a group of byte buffers.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|RawErasureCoder
specifier|public
interface|interface
name|RawErasureCoder
extends|extends
name|Configurable
block|{
comment|/**    * Get a coder option value.    * @param option    * @return    */
DECL|method|getCoderOption (CoderOption option)
specifier|public
name|Object
name|getCoderOption
parameter_list|(
name|CoderOption
name|option
parameter_list|)
function_decl|;
comment|/**    * Set a coder option value.    * @param option    * @param value    */
DECL|method|setCoderOption (CoderOption option, Object value)
specifier|public
name|void
name|setCoderOption
parameter_list|(
name|CoderOption
name|option
parameter_list|,
name|Object
name|value
parameter_list|)
function_decl|;
comment|/**    * The number of data input units for the coding. A unit can be a byte,    * chunk or buffer or even a block.    * @return count of data input units    */
DECL|method|getNumDataUnits ()
specifier|public
name|int
name|getNumDataUnits
parameter_list|()
function_decl|;
comment|/**    * The number of parity output units for the coding. A unit can be a byte,    * chunk, buffer or even a block.    * @return count of parity output units    */
DECL|method|getNumParityUnits ()
specifier|public
name|int
name|getNumParityUnits
parameter_list|()
function_decl|;
comment|/**    * Should be called when release this coder. Good chance to release encoding    * or decoding buffers    */
DECL|method|release ()
specifier|public
name|void
name|release
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

