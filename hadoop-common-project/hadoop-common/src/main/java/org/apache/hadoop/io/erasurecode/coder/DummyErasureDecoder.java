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
name|DummyRawDecoder
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

begin_comment
comment|/**  * Dummy erasure decoder does no real computation. Instead, it just returns  * zero bytes. This decoder can be used to isolate the performance issue to  * HDFS side logic instead of codec, and is intended for test only.  */
end_comment

begin_class
DECL|class|DummyErasureDecoder
specifier|public
class|class
name|DummyErasureDecoder
extends|extends
name|ErasureDecoder
block|{
DECL|method|DummyErasureDecoder (ErasureCoderOptions options)
specifier|public
name|DummyErasureDecoder
parameter_list|(
name|ErasureCoderOptions
name|options
parameter_list|)
block|{
name|super
argument_list|(
name|options
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareDecodingStep (ECBlockGroup blockGroup)
specifier|protected
name|ErasureCodingStep
name|prepareDecodingStep
parameter_list|(
name|ECBlockGroup
name|blockGroup
parameter_list|)
block|{
name|RawErasureDecoder
name|rawDecoder
init|=
operator|new
name|DummyRawDecoder
argument_list|(
name|getOptions
argument_list|()
argument_list|)
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
name|ErasureDecodingStep
argument_list|(
name|inputBlocks
argument_list|,
name|getErasedIndexes
argument_list|(
name|inputBlocks
argument_list|)
argument_list|,
name|getOutputBlocks
argument_list|(
name|blockGroup
argument_list|)
argument_list|,
name|rawDecoder
argument_list|)
return|;
block|}
block|}
end_class

end_unit

