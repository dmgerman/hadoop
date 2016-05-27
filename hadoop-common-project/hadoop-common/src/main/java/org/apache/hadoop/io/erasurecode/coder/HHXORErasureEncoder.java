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
name|RawErasureEncoder
import|;
end_import

begin_comment
comment|/**  * Hitchhiker is a new erasure coding algorithm developed as a research project  * at UC Berkeley by Rashmi Vinayak.  * It has been shown to reduce network traffic and disk I/O by 25%-45% during  * data reconstruction while retaining the same storage capacity and failure  * tolerance capability of RS codes.  * The Hitchhiker algorithm is described in K.V.Rashmi, et al.,  * "A "Hitchhiker's" Guide to Fast and Efficient Data Reconstruction in  * Erasure-coded Data Centers", in ACM SIGCOMM 2014.  * This is Hitchhiker-XOR erasure encoder that encodes a block group.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HHXORErasureEncoder
specifier|public
class|class
name|HHXORErasureEncoder
extends|extends
name|AbstractErasureEncoder
block|{
DECL|field|rsRawEncoder
specifier|private
name|RawErasureEncoder
name|rsRawEncoder
decl_stmt|;
DECL|field|xorRawEncoder
specifier|private
name|RawErasureEncoder
name|xorRawEncoder
decl_stmt|;
DECL|method|HHXORErasureEncoder (int numDataUnits, int numParityUnits)
specifier|public
name|HHXORErasureEncoder
parameter_list|(
name|int
name|numDataUnits
parameter_list|,
name|int
name|numParityUnits
parameter_list|)
block|{
name|super
argument_list|(
name|numDataUnits
argument_list|,
name|numParityUnits
argument_list|)
expr_stmt|;
block|}
DECL|method|HHXORErasureEncoder (ECSchema schema)
specifier|public
name|HHXORErasureEncoder
parameter_list|(
name|ECSchema
name|schema
parameter_list|)
block|{
name|super
argument_list|(
name|schema
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareEncodingStep ( final ECBlockGroup blockGroup)
specifier|protected
name|ErasureCodingStep
name|prepareEncodingStep
parameter_list|(
specifier|final
name|ECBlockGroup
name|blockGroup
parameter_list|)
block|{
name|RawErasureEncoder
name|rsRawEncoderTmp
init|=
name|checkCreateRSRawEncoder
argument_list|()
decl_stmt|;
name|RawErasureEncoder
name|xorRawEncoderTmp
init|=
name|checkCreateXorRawEncoder
argument_list|()
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
return|return
operator|new
name|HHXORErasureEncodingStep
argument_list|(
name|inputBlocks
argument_list|,
name|getOutputBlocks
argument_list|(
name|blockGroup
argument_list|)
argument_list|,
name|rsRawEncoderTmp
argument_list|,
name|xorRawEncoderTmp
argument_list|)
return|;
block|}
DECL|method|checkCreateRSRawEncoder ()
specifier|private
name|RawErasureEncoder
name|checkCreateRSRawEncoder
parameter_list|()
block|{
if|if
condition|(
name|rsRawEncoder
operator|==
literal|null
condition|)
block|{
name|ErasureCoderOptions
name|coderOptions
init|=
operator|new
name|ErasureCoderOptions
argument_list|(
name|getNumDataUnits
argument_list|()
argument_list|,
name|getNumParityUnits
argument_list|()
argument_list|)
decl_stmt|;
name|rsRawEncoder
operator|=
name|CodecUtil
operator|.
name|createRawEncoder
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|ErasureCodeConstants
operator|.
name|RS_DEFAULT_CODEC_NAME
argument_list|,
name|coderOptions
argument_list|)
expr_stmt|;
block|}
return|return
name|rsRawEncoder
return|;
block|}
DECL|method|checkCreateXorRawEncoder ()
specifier|private
name|RawErasureEncoder
name|checkCreateXorRawEncoder
parameter_list|()
block|{
if|if
condition|(
name|xorRawEncoder
operator|==
literal|null
condition|)
block|{
name|ErasureCoderOptions
name|erasureCoderOptions
init|=
operator|new
name|ErasureCoderOptions
argument_list|(
name|getNumDataUnits
argument_list|()
argument_list|,
name|getNumParityUnits
argument_list|()
argument_list|)
decl_stmt|;
name|xorRawEncoder
operator|=
name|CodecUtil
operator|.
name|createRawEncoder
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|ErasureCodeConstants
operator|.
name|XOR_CODEC_NAME
argument_list|,
name|erasureCoderOptions
argument_list|)
expr_stmt|;
block|}
return|return
name|xorRawEncoder
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
name|rsRawEncoder
operator|!=
literal|null
condition|)
block|{
name|rsRawEncoder
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|xorRawEncoder
operator|!=
literal|null
condition|)
block|{
name|xorRawEncoder
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

