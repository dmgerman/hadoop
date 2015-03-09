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

begin_comment
comment|/**  * An abstract erasure encoder that's to be inherited by new encoders.  *  * It implements the {@link ErasureEncoder} interface.  */
end_comment

begin_class
DECL|class|AbstractErasureEncoder
specifier|public
specifier|abstract
class|class
name|AbstractErasureEncoder
extends|extends
name|AbstractErasureCoder
implements|implements
name|ErasureEncoder
block|{
annotation|@
name|Override
DECL|method|encode (ECBlockGroup blockGroup)
specifier|public
name|ErasureCodingStep
name|encode
parameter_list|(
name|ECBlockGroup
name|blockGroup
parameter_list|)
block|{
return|return
name|performEncoding
argument_list|(
name|blockGroup
argument_list|)
return|;
block|}
comment|/**    * Perform encoding against a block group.    * @param blockGroup    * @return encoding step for caller to do the real work    */
DECL|method|performEncoding (ECBlockGroup blockGroup)
specifier|protected
specifier|abstract
name|ErasureCodingStep
name|performEncoding
parameter_list|(
name|ECBlockGroup
name|blockGroup
parameter_list|)
function_decl|;
DECL|method|getInputBlocks (ECBlockGroup blockGroup)
specifier|protected
name|ECBlock
index|[]
name|getInputBlocks
parameter_list|(
name|ECBlockGroup
name|blockGroup
parameter_list|)
block|{
return|return
name|blockGroup
operator|.
name|getDataBlocks
argument_list|()
return|;
block|}
DECL|method|getOutputBlocks (ECBlockGroup blockGroup)
specifier|protected
name|ECBlock
index|[]
name|getOutputBlocks
parameter_list|(
name|ECBlockGroup
name|blockGroup
parameter_list|)
block|{
return|return
name|blockGroup
operator|.
name|getParityBlocks
argument_list|()
return|;
block|}
block|}
end_class

end_unit

