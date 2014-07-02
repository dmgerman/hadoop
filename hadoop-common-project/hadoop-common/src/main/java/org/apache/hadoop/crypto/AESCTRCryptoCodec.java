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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|AESCTRCryptoCodec
specifier|public
specifier|abstract
class|class
name|AESCTRCryptoCodec
extends|extends
name|CryptoCodec
block|{
DECL|field|SUITE
specifier|protected
specifier|static
specifier|final
name|CipherSuite
name|SUITE
init|=
name|CipherSuite
operator|.
name|AES_CTR_NOPADDING
decl_stmt|;
comment|/**    * For AES, the algorithm block is fixed size of 128 bits.    * @see http://en.wikipedia.org/wiki/Advanced_Encryption_Standard    */
DECL|field|AES_BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|AES_BLOCK_SIZE
init|=
name|SUITE
operator|.
name|getAlgorithmBlockSize
argument_list|()
decl_stmt|;
DECL|field|CTR_OFFSET
specifier|private
specifier|static
specifier|final
name|int
name|CTR_OFFSET
init|=
literal|8
decl_stmt|;
annotation|@
name|Override
DECL|method|getCipherSuite ()
specifier|public
name|CipherSuite
name|getCipherSuite
parameter_list|()
block|{
return|return
name|SUITE
return|;
block|}
comment|/**    * The IV is produced by adding the initial IV to the counter. IV length     * should be the same as {@link #AES_BLOCK_SIZE}    */
annotation|@
name|Override
DECL|method|calculateIV (byte[] initIV, long counter, byte[] IV)
specifier|public
name|void
name|calculateIV
parameter_list|(
name|byte
index|[]
name|initIV
parameter_list|,
name|long
name|counter
parameter_list|,
name|byte
index|[]
name|IV
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|initIV
operator|.
name|length
operator|==
name|AES_BLOCK_SIZE
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|IV
operator|.
name|length
operator|==
name|AES_BLOCK_SIZE
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|initIV
argument_list|,
literal|0
argument_list|,
name|IV
argument_list|,
literal|0
argument_list|,
name|CTR_OFFSET
argument_list|)
expr_stmt|;
name|long
name|l
init|=
operator|(
name|initIV
index|[
name|CTR_OFFSET
operator|+
literal|0
index|]
operator|<<
literal|56
operator|)
operator|+
operator|(
operator|(
name|initIV
index|[
name|CTR_OFFSET
operator|+
literal|1
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|48
operator|)
operator|+
operator|(
operator|(
name|initIV
index|[
name|CTR_OFFSET
operator|+
literal|2
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|40
operator|)
operator|+
operator|(
operator|(
name|initIV
index|[
name|CTR_OFFSET
operator|+
literal|3
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|32
operator|)
operator|+
operator|(
operator|(
name|initIV
index|[
name|CTR_OFFSET
operator|+
literal|4
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator|+
operator|(
operator|(
name|initIV
index|[
name|CTR_OFFSET
operator|+
literal|5
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator|+
operator|(
operator|(
name|initIV
index|[
name|CTR_OFFSET
operator|+
literal|6
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator|+
operator|(
name|initIV
index|[
name|CTR_OFFSET
operator|+
literal|7
index|]
operator|&
literal|0xFF
operator|)
decl_stmt|;
name|l
operator|+=
name|counter
expr_stmt|;
name|IV
index|[
name|CTR_OFFSET
operator|+
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|l
operator|>>>
literal|56
argument_list|)
expr_stmt|;
name|IV
index|[
name|CTR_OFFSET
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|l
operator|>>>
literal|48
argument_list|)
expr_stmt|;
name|IV
index|[
name|CTR_OFFSET
operator|+
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|l
operator|>>>
literal|40
argument_list|)
expr_stmt|;
name|IV
index|[
name|CTR_OFFSET
operator|+
literal|3
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|l
operator|>>>
literal|32
argument_list|)
expr_stmt|;
name|IV
index|[
name|CTR_OFFSET
operator|+
literal|4
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|l
operator|>>>
literal|24
argument_list|)
expr_stmt|;
name|IV
index|[
name|CTR_OFFSET
operator|+
literal|5
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|l
operator|>>>
literal|16
argument_list|)
expr_stmt|;
name|IV
index|[
name|CTR_OFFSET
operator|+
literal|6
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|l
operator|>>>
literal|8
argument_list|)
expr_stmt|;
name|IV
index|[
name|CTR_OFFSET
operator|+
literal|7
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

