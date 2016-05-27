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
name|HadoopIllegalArgumentException
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

begin_comment
comment|/**  * A utility class that maintains encoding state during an encode call.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|EncodingState
specifier|abstract
class|class
name|EncodingState
block|{
DECL|field|encoder
name|RawErasureEncoder
name|encoder
decl_stmt|;
DECL|field|encodeLength
name|int
name|encodeLength
decl_stmt|;
comment|/**    * Check and validate decoding parameters, throw exception accordingly.    * @param inputs input buffers to check    * @param outputs output buffers to check    */
DECL|method|checkParameters (T[] inputs, T[] outputs)
parameter_list|<
name|T
parameter_list|>
name|void
name|checkParameters
parameter_list|(
name|T
index|[]
name|inputs
parameter_list|,
name|T
index|[]
name|outputs
parameter_list|)
block|{
if|if
condition|(
name|inputs
operator|.
name|length
operator|!=
name|encoder
operator|.
name|getNumDataUnits
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid inputs length"
argument_list|)
throw|;
block|}
if|if
condition|(
name|outputs
operator|.
name|length
operator|!=
name|encoder
operator|.
name|getNumParityUnits
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid outputs length"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

