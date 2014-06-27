begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Defines properties of a CipherSuite. Modeled after the ciphers in  * {@link javax.crypto.Cipher}.  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|enum|CipherSuite
specifier|public
enum|enum
name|CipherSuite
block|{
DECL|enumConstant|AES_CTR_NOPADDING
name|AES_CTR_NOPADDING
argument_list|(
literal|"AES/CTR/NoPadding"
argument_list|,
literal|128
argument_list|)
block|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|blockBits
specifier|private
specifier|final
name|int
name|blockBits
decl_stmt|;
DECL|method|CipherSuite (String name, int blockBits)
name|CipherSuite
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|blockBits
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|blockBits
operator|=
name|blockBits
expr_stmt|;
block|}
comment|/**    * @return name of cipher suite, as in {@link javax.crypto.Cipher}    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**    * @return size of an algorithm block in bits    */
DECL|method|getNumberBlockBits ()
specifier|public
name|int
name|getNumberBlockBits
parameter_list|()
block|{
return|return
name|blockBits
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"{"
argument_list|)
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"name: "
operator|+
name|getName
argument_list|()
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"numBlockBits: "
operator|+
name|getNumberBlockBits
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_enum

end_unit

