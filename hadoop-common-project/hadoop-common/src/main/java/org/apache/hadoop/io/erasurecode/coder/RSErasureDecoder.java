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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|ECBlock
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
name|rawcoder
operator|.
name|RSRawDecoder
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
name|rawcoder
operator|.
name|RawErasureDecoder
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
name|rawcoder
operator|.
name|XORRawDecoder
import|;
end_import

begin_comment
comment|/**  * Reed-Solomon erasure decoder that decodes a block group.  *  * It implements {@link ErasureCoder}.  */
end_comment

begin_class
DECL|class|RSErasureDecoder
specifier|public
class|class
name|RSErasureDecoder
extends|extends
name|AbstractErasureDecoder
block|{
DECL|field|rsRawDecoder
specifier|private
name|RawErasureDecoder
name|rsRawDecoder
decl_stmt|;
DECL|field|xorRawDecoder
specifier|private
name|RawErasureDecoder
name|xorRawDecoder
decl_stmt|;
DECL|field|useXorWhenPossible
specifier|private
name|boolean
name|useXorWhenPossible
init|=
literal|true
decl_stmt|;
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|useXorWhenPossible
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_ERASURECODE_CODEC_RS_USEXOR_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|prepareDecodingStep (final ECBlockGroup blockGroup)
specifier|protected
name|ErasureCodingStep
name|prepareDecodingStep
parameter_list|(
specifier|final
name|ECBlockGroup
name|blockGroup
parameter_list|)
block|{
name|RawErasureDecoder
name|rawDecoder
decl_stmt|;
name|ECBlock
index|[]
name|inputBlocks
init|=
name|getInputBlocks
argument_list|(
name|blockGroup
argument_list|)
decl_stmt|;
name|ECBlock
index|[]
name|outputBlocks
init|=
name|getOutputBlocks
argument_list|(
name|blockGroup
argument_list|)
decl_stmt|;
comment|/**      * Optimization: according to some benchmark, when only one block is erased      * and to be recovering, the most simple XOR scheme can be much efficient.      * We will have benchmark tests to verify this opt is effect or not.      */
if|if
condition|(
name|outputBlocks
operator|.
name|length
operator|==
literal|1
operator|&&
name|useXorWhenPossible
condition|)
block|{
name|rawDecoder
operator|=
name|checkCreateXorRawDecoder
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|rawDecoder
operator|=
name|checkCreateRSRawDecoder
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|ErasureDecodingStep
argument_list|(
name|inputBlocks
argument_list|,
name|getErasedIndexes
argument_list|(
name|inputBlocks
argument_list|)
argument_list|,
name|outputBlocks
argument_list|,
name|rawDecoder
argument_list|)
return|;
block|}
DECL|method|checkCreateRSRawDecoder ()
specifier|private
name|RawErasureDecoder
name|checkCreateRSRawDecoder
parameter_list|()
block|{
if|if
condition|(
name|rsRawDecoder
operator|==
literal|null
condition|)
block|{
name|rsRawDecoder
operator|=
name|createRawDecoder
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_ERASURECODE_CODEC_RS_RAWCODER_KEY
argument_list|)
expr_stmt|;
if|if
condition|(
name|rsRawDecoder
operator|==
literal|null
condition|)
block|{
name|rsRawDecoder
operator|=
operator|new
name|RSRawDecoder
argument_list|()
expr_stmt|;
block|}
name|rsRawDecoder
operator|.
name|initialize
argument_list|(
name|getNumDataUnits
argument_list|()
argument_list|,
name|getNumParityUnits
argument_list|()
argument_list|,
name|getChunkSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|rsRawDecoder
return|;
block|}
DECL|method|checkCreateXorRawDecoder ()
specifier|private
name|RawErasureDecoder
name|checkCreateXorRawDecoder
parameter_list|()
block|{
if|if
condition|(
name|xorRawDecoder
operator|==
literal|null
condition|)
block|{
name|xorRawDecoder
operator|=
operator|new
name|XORRawDecoder
argument_list|()
expr_stmt|;
name|xorRawDecoder
operator|.
name|initialize
argument_list|(
name|getNumDataUnits
argument_list|()
argument_list|,
literal|1
argument_list|,
name|getChunkSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|xorRawDecoder
return|;
block|}
annotation|@
name|Override
DECL|method|release ()
specifier|public
name|void
name|release
parameter_list|()
block|{
if|if
condition|(
name|xorRawDecoder
operator|!=
literal|null
condition|)
block|{
name|xorRawDecoder
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rsRawDecoder
operator|!=
literal|null
condition|)
block|{
name|rsRawDecoder
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

