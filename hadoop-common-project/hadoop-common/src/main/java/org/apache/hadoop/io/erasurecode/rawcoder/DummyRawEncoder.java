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
name|ErasureCoderOptions
import|;
end_import

begin_comment
comment|/**  * A dummy raw encoder that does no real computation.  * Instead, it just returns zero bytes.  * This encoder can be used to isolate the performance issue to HDFS side logic  * instead of codec, and is intended for test only.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DummyRawEncoder
specifier|public
class|class
name|DummyRawEncoder
extends|extends
name|RawErasureEncoder
block|{
DECL|method|DummyRawEncoder (ErasureCoderOptions coderOptions)
specifier|public
name|DummyRawEncoder
parameter_list|(
name|ErasureCoderOptions
name|coderOptions
parameter_list|)
block|{
name|super
argument_list|(
name|coderOptions
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doEncode (ByteArrayEncodingState encodingState)
specifier|protected
name|void
name|doEncode
parameter_list|(
name|ByteArrayEncodingState
name|encodingState
parameter_list|)
block|{
comment|// Nothing to do. Output buffers have already been reset
block|}
annotation|@
name|Override
DECL|method|doEncode (ByteBufferEncodingState encodingState)
specifier|protected
name|void
name|doEncode
parameter_list|(
name|ByteBufferEncodingState
name|encodingState
parameter_list|)
block|{
comment|// Nothing to do. Output buffers have already been reset
block|}
block|}
end_class

end_unit

