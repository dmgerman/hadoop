begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode.codec
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
name|codec
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
name|Configuration
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
name|ErasureCodecOptions
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
name|coder
operator|.
name|ErasureDecoder
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
name|coder
operator|.
name|ErasureEncoder
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
name|coder
operator|.
name|HHXORErasureDecoder
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
name|coder
operator|.
name|HHXORErasureEncoder
import|;
end_import

begin_comment
comment|/**  * A Hitchhiker-XOR erasure codec.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HHXORErasureCodec
specifier|public
class|class
name|HHXORErasureCodec
extends|extends
name|ErasureCodec
block|{
DECL|method|HHXORErasureCodec (Configuration conf, ErasureCodecOptions options)
specifier|public
name|HHXORErasureCodec
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ErasureCodecOptions
name|options
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createEncoder ()
specifier|public
name|ErasureEncoder
name|createEncoder
parameter_list|()
block|{
return|return
operator|new
name|HHXORErasureEncoder
argument_list|(
name|getCoderOptions
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createDecoder ()
specifier|public
name|ErasureDecoder
name|createDecoder
parameter_list|()
block|{
return|return
operator|new
name|HHXORErasureDecoder
argument_list|(
name|getCoderOptions
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

