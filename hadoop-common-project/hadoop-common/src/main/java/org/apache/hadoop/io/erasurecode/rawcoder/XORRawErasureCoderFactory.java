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
name|ErasureCodeConstants
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
comment|/**  * A raw coder factory for raw XOR coder.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|XORRawErasureCoderFactory
specifier|public
class|class
name|XORRawErasureCoderFactory
implements|implements
name|RawErasureCoderFactory
block|{
DECL|field|CODER_NAME
specifier|public
specifier|static
specifier|final
name|String
name|CODER_NAME
init|=
literal|"xor_java"
decl_stmt|;
annotation|@
name|Override
DECL|method|createEncoder (ErasureCoderOptions coderOptions)
specifier|public
name|RawErasureEncoder
name|createEncoder
parameter_list|(
name|ErasureCoderOptions
name|coderOptions
parameter_list|)
block|{
return|return
operator|new
name|XORRawEncoder
argument_list|(
name|coderOptions
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createDecoder (ErasureCoderOptions coderOptions)
specifier|public
name|RawErasureDecoder
name|createDecoder
parameter_list|(
name|ErasureCoderOptions
name|coderOptions
parameter_list|)
block|{
return|return
operator|new
name|XORRawDecoder
argument_list|(
name|coderOptions
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCoderName ()
specifier|public
name|String
name|getCoderName
parameter_list|()
block|{
return|return
name|CODER_NAME
return|;
block|}
annotation|@
name|Override
DECL|method|getCodecName ()
specifier|public
name|String
name|getCodecName
parameter_list|()
block|{
return|return
name|ErasureCodeConstants
operator|.
name|XOR_CODEC_NAME
return|;
block|}
block|}
end_class

end_unit

