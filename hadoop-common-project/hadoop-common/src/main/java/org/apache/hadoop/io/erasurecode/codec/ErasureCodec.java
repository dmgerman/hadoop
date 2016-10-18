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
name|CodecUtil
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
name|ECSchema
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
name|ErasureCoderOptions
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
name|grouper
operator|.
name|BlockGrouper
import|;
end_import

begin_comment
comment|/**  * Abstract Erasure Codec is defines the interface of each actual erasure  * codec classes.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ErasureCodec
specifier|public
specifier|abstract
class|class
name|ErasureCodec
block|{
DECL|field|schema
specifier|private
name|ECSchema
name|schema
decl_stmt|;
DECL|field|codecOptions
specifier|private
name|ErasureCodecOptions
name|codecOptions
decl_stmt|;
DECL|field|coderOptions
specifier|private
name|ErasureCoderOptions
name|coderOptions
decl_stmt|;
DECL|method|ErasureCodec (Configuration conf, ErasureCodecOptions options)
specifier|public
name|ErasureCodec
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ErasureCodecOptions
name|options
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|options
operator|.
name|getSchema
argument_list|()
expr_stmt|;
name|this
operator|.
name|codecOptions
operator|=
name|options
expr_stmt|;
name|boolean
name|allowChangeInputs
init|=
literal|false
decl_stmt|;
name|this
operator|.
name|coderOptions
operator|=
operator|new
name|ErasureCoderOptions
argument_list|(
name|schema
operator|.
name|getNumDataUnits
argument_list|()
argument_list|,
name|schema
operator|.
name|getNumParityUnits
argument_list|()
argument_list|,
name|allowChangeInputs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|schema
operator|.
name|getCodecName
argument_list|()
return|;
block|}
DECL|method|getSchema ()
specifier|public
name|ECSchema
name|getSchema
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
comment|/**    * Get a {@link ErasureCodecOptions}.    * @return erasure codec options    */
DECL|method|getCodecOptions ()
specifier|public
name|ErasureCodecOptions
name|getCodecOptions
parameter_list|()
block|{
return|return
name|codecOptions
return|;
block|}
DECL|method|setCodecOptions (ErasureCodecOptions options)
specifier|protected
name|void
name|setCodecOptions
parameter_list|(
name|ErasureCodecOptions
name|options
parameter_list|)
block|{
name|this
operator|.
name|codecOptions
operator|=
name|options
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|options
operator|.
name|getSchema
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get a {@link ErasureCoderOptions}.    * @return erasure coder options    */
DECL|method|getCoderOptions ()
specifier|public
name|ErasureCoderOptions
name|getCoderOptions
parameter_list|()
block|{
return|return
name|coderOptions
return|;
block|}
DECL|method|setCoderOptions (ErasureCoderOptions options)
specifier|protected
name|void
name|setCoderOptions
parameter_list|(
name|ErasureCoderOptions
name|options
parameter_list|)
block|{
name|this
operator|.
name|coderOptions
operator|=
name|options
expr_stmt|;
block|}
DECL|method|createEncoder ()
specifier|public
specifier|abstract
name|ErasureEncoder
name|createEncoder
parameter_list|()
function_decl|;
DECL|method|createDecoder ()
specifier|public
specifier|abstract
name|ErasureDecoder
name|createDecoder
parameter_list|()
function_decl|;
DECL|method|createBlockGrouper ()
specifier|public
name|BlockGrouper
name|createBlockGrouper
parameter_list|()
block|{
name|BlockGrouper
name|blockGrouper
init|=
operator|new
name|BlockGrouper
argument_list|()
decl_stmt|;
name|blockGrouper
operator|.
name|setSchema
argument_list|(
name|getSchema
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|blockGrouper
return|;
block|}
block|}
end_class

end_unit

