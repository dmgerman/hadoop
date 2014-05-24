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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|GeneralSecurityException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|Cipher
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|spec
operator|.
name|IvParameterSpec
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|spec
operator|.
name|SecretKeySpec
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
DECL|class|JCEAESCTRDecryptor
specifier|public
class|class
name|JCEAESCTRDecryptor
implements|implements
name|Decryptor
block|{
DECL|field|cipher
specifier|private
specifier|final
name|Cipher
name|cipher
decl_stmt|;
DECL|field|contextReset
specifier|private
name|boolean
name|contextReset
init|=
literal|false
decl_stmt|;
DECL|method|JCEAESCTRDecryptor (String provider)
specifier|public
name|JCEAESCTRDecryptor
parameter_list|(
name|String
name|provider
parameter_list|)
throws|throws
name|GeneralSecurityException
block|{
if|if
condition|(
name|provider
operator|==
literal|null
operator|||
name|provider
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|cipher
operator|=
name|Cipher
operator|.
name|getInstance
argument_list|(
literal|"AES/CTR/NoPadding"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cipher
operator|=
name|Cipher
operator|.
name|getInstance
argument_list|(
literal|"AES/CTR/NoPadding"
argument_list|,
name|provider
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|init (byte[] key, byte[] iv)
specifier|public
name|void
name|init
parameter_list|(
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|iv
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|iv
argument_list|)
expr_stmt|;
name|contextReset
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|cipher
operator|.
name|init
argument_list|(
name|Cipher
operator|.
name|DECRYPT_MODE
argument_list|,
operator|new
name|SecretKeySpec
argument_list|(
name|key
argument_list|,
literal|"AES"
argument_list|)
argument_list|,
operator|new
name|IvParameterSpec
argument_list|(
name|iv
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * For AES-CTR, will consume all input data and needs enough space in the     * destination buffer to decrypt entire input data.    */
annotation|@
name|Override
DECL|method|decrypt (ByteBuffer inBuffer, ByteBuffer outBuffer)
specifier|public
name|void
name|decrypt
parameter_list|(
name|ByteBuffer
name|inBuffer
parameter_list|,
name|ByteBuffer
name|outBuffer
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|int
name|inputSize
init|=
name|inBuffer
operator|.
name|remaining
argument_list|()
decl_stmt|;
comment|// Cipher#update will maintain decryption context.
name|int
name|n
init|=
name|cipher
operator|.
name|update
argument_list|(
name|inBuffer
argument_list|,
name|outBuffer
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|<
name|inputSize
condition|)
block|{
comment|/**          * Typically code will not get here. Cipher#update will decrypt all           * input data and put result in outBuffer.           * Cipher#doFinal will reset the decryption context.          */
name|contextReset
operator|=
literal|true
expr_stmt|;
name|cipher
operator|.
name|doFinal
argument_list|(
name|inBuffer
argument_list|,
name|outBuffer
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|isContextReset ()
specifier|public
name|boolean
name|isContextReset
parameter_list|()
block|{
return|return
name|contextReset
return|;
block|}
block|}
end_class

end_unit

