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

begin_comment
comment|/**  * Raw erasure coder factory that can be used to create raw encoder and decoder.  * It helps in configuration since only one factory class is needed to be  * configured.  */
end_comment

begin_interface
DECL|interface|RawErasureCoderFactory
specifier|public
interface|interface
name|RawErasureCoderFactory
block|{
comment|/**    * Create raw erasure encoder.    * @param numDataUnits    * @param numParityUnits    * @return raw erasure encoder    */
DECL|method|createEncoder (int numDataUnits, int numParityUnits)
specifier|public
name|RawErasureEncoder
name|createEncoder
parameter_list|(
name|int
name|numDataUnits
parameter_list|,
name|int
name|numParityUnits
parameter_list|)
function_decl|;
comment|/**    * Create raw erasure decoder.    * @param numDataUnits    * @param numParityUnits    * @return raw erasure decoder    */
DECL|method|createDecoder (int numDataUnits, int numParityUnits)
specifier|public
name|RawErasureDecoder
name|createDecoder
parameter_list|(
name|int
name|numDataUnits
parameter_list|,
name|int
name|numParityUnits
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

