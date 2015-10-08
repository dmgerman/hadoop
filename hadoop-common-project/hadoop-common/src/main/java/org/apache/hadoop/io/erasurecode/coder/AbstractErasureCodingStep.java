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
name|ECBlock
import|;
end_import

begin_comment
comment|/**  * Abstract class for common facilities shared by {@link ErasureEncodingStep}  * and {@link ErasureDecodingStep}.  *  * It implements {@link ErasureEncodingStep}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|AbstractErasureCodingStep
specifier|public
specifier|abstract
class|class
name|AbstractErasureCodingStep
implements|implements
name|ErasureCodingStep
block|{
DECL|field|inputBlocks
specifier|private
name|ECBlock
index|[]
name|inputBlocks
decl_stmt|;
DECL|field|outputBlocks
specifier|private
name|ECBlock
index|[]
name|outputBlocks
decl_stmt|;
comment|/**    * Constructor given input blocks and output blocks.    * @param inputBlocks    * @param outputBlocks    */
DECL|method|AbstractErasureCodingStep (ECBlock[] inputBlocks, ECBlock[] outputBlocks)
specifier|public
name|AbstractErasureCodingStep
parameter_list|(
name|ECBlock
index|[]
name|inputBlocks
parameter_list|,
name|ECBlock
index|[]
name|outputBlocks
parameter_list|)
block|{
name|this
operator|.
name|inputBlocks
operator|=
name|inputBlocks
expr_stmt|;
name|this
operator|.
name|outputBlocks
operator|=
name|outputBlocks
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getInputBlocks ()
specifier|public
name|ECBlock
index|[]
name|getInputBlocks
parameter_list|()
block|{
return|return
name|inputBlocks
return|;
block|}
annotation|@
name|Override
DECL|method|getOutputBlocks ()
specifier|public
name|ECBlock
index|[]
name|getOutputBlocks
parameter_list|()
block|{
return|return
name|outputBlocks
return|;
block|}
annotation|@
name|Override
DECL|method|finish ()
specifier|public
name|void
name|finish
parameter_list|()
block|{
comment|// NOOP by default
block|}
block|}
end_class

end_unit

