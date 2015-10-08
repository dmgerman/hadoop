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
name|conf
operator|.
name|Configured
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

begin_comment
comment|/**  * A common class of basic facilities to be shared by encoder and decoder  *  * It implements the {@link ErasureCoder} interface.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|AbstractErasureCoder
specifier|public
specifier|abstract
class|class
name|AbstractErasureCoder
extends|extends
name|Configured
implements|implements
name|ErasureCoder
block|{
DECL|field|numDataUnits
specifier|private
specifier|final
name|int
name|numDataUnits
decl_stmt|;
DECL|field|numParityUnits
specifier|private
specifier|final
name|int
name|numParityUnits
decl_stmt|;
DECL|method|AbstractErasureCoder (int numDataUnits, int numParityUnits)
specifier|public
name|AbstractErasureCoder
parameter_list|(
name|int
name|numDataUnits
parameter_list|,
name|int
name|numParityUnits
parameter_list|)
block|{
name|this
operator|.
name|numDataUnits
operator|=
name|numDataUnits
expr_stmt|;
name|this
operator|.
name|numParityUnits
operator|=
name|numParityUnits
expr_stmt|;
block|}
DECL|method|AbstractErasureCoder (ECSchema schema)
specifier|public
name|AbstractErasureCoder
parameter_list|(
name|ECSchema
name|schema
parameter_list|)
block|{
name|this
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumDataUnits ()
specifier|public
name|int
name|getNumDataUnits
parameter_list|()
block|{
return|return
name|numDataUnits
return|;
block|}
annotation|@
name|Override
DECL|method|getNumParityUnits ()
specifier|public
name|int
name|getNumParityUnits
parameter_list|()
block|{
return|return
name|numParityUnits
return|;
block|}
annotation|@
name|Override
DECL|method|preferDirectBuffer ()
specifier|public
name|boolean
name|preferDirectBuffer
parameter_list|()
block|{
return|return
literal|false
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
comment|// Nothing to do by default
block|}
block|}
end_class

end_unit

