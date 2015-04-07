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
name|io
operator|.
name|erasurecode
operator|.
name|coder
operator|.
name|ErasureCoder
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
name|RSErasureDecoder
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
name|RSErasureEncoder
import|;
end_import

begin_comment
comment|/**  * A Reed-Solomon erasure codec.  */
end_comment

begin_class
DECL|class|RSErasureCodec
specifier|public
class|class
name|RSErasureCodec
extends|extends
name|AbstractErasureCodec
block|{
annotation|@
name|Override
DECL|method|doCreateEncoder ()
specifier|protected
name|ErasureCoder
name|doCreateEncoder
parameter_list|()
block|{
return|return
operator|new
name|RSErasureEncoder
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doCreateDecoder ()
specifier|protected
name|ErasureCoder
name|doCreateDecoder
parameter_list|()
block|{
return|return
operator|new
name|RSErasureDecoder
argument_list|()
return|;
block|}
block|}
end_class

end_unit

