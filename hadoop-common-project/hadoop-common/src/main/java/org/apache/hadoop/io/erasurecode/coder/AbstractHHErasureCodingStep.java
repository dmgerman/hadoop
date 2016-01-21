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
comment|/**  * Abstract class for Hitchhiker common facilities shared by  * {@link HHXORErasureEncodingStep}and {@link HHXORErasureDecodingStep}.  *  * It implements {@link AbstractErasureCodingStep}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|AbstractHHErasureCodingStep
specifier|public
specifier|abstract
class|class
name|AbstractHHErasureCodingStep
extends|extends
name|AbstractErasureCodingStep
block|{
DECL|field|SUB_PACKET_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|SUB_PACKET_SIZE
init|=
literal|2
decl_stmt|;
comment|/**    * Constructor given input blocks and output blocks.    *    * @param inputBlocks    * @param outputBlocks    */
DECL|method|AbstractHHErasureCodingStep (ECBlock[] inputBlocks, ECBlock[] outputBlocks)
specifier|public
name|AbstractHHErasureCodingStep
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
name|super
argument_list|(
name|inputBlocks
argument_list|,
name|outputBlocks
argument_list|)
expr_stmt|;
block|}
DECL|method|getSubPacketSize ()
specifier|protected
name|int
name|getSubPacketSize
parameter_list|()
block|{
return|return
name|SUB_PACKET_SIZE
return|;
block|}
block|}
end_class

end_unit

