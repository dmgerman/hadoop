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
name|java
operator|.
name|security
operator|.
name|SecureRandom
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
name|Configuration
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

begin_comment
comment|/**  * Implement the AES-CTR crypto codec using JNI into OpenSSL.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|OpensslAesCtrCryptoCodec
specifier|public
class|class
name|OpensslAesCtrCryptoCodec
extends|extends
name|AesCtrCryptoCodec
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|random
specifier|private
name|SecureRandom
name|random
init|=
operator|new
name|SecureRandom
argument_list|()
decl_stmt|;
DECL|method|OpensslAesCtrCryptoCodec ()
specifier|public
name|OpensslAesCtrCryptoCodec
parameter_list|()
block|{   }
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
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|createEncryptor ()
specifier|public
name|Encryptor
name|createEncryptor
parameter_list|()
throws|throws
name|GeneralSecurityException
block|{
return|return
operator|new
name|OpensslAesCtrCipher
argument_list|(
name|OpensslCipher
operator|.
name|ENCRYPT_MODE
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createDecryptor ()
specifier|public
name|Decryptor
name|createDecryptor
parameter_list|()
throws|throws
name|GeneralSecurityException
block|{
return|return
operator|new
name|OpensslAesCtrCipher
argument_list|(
name|OpensslCipher
operator|.
name|DECRYPT_MODE
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|generateSecureRandom (byte[] bytes)
specifier|public
name|void
name|generateSecureRandom
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|random
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
DECL|class|OpensslAesCtrCipher
specifier|private
specifier|static
class|class
name|OpensslAesCtrCipher
implements|implements
name|Encryptor
implements|,
name|Decryptor
block|{
DECL|field|cipher
specifier|private
specifier|final
name|OpensslCipher
name|cipher
decl_stmt|;
DECL|field|mode
specifier|private
specifier|final
name|int
name|mode
decl_stmt|;
DECL|field|contextReset
specifier|private
name|boolean
name|contextReset
init|=
literal|false
decl_stmt|;
DECL|method|OpensslAesCtrCipher (int mode)
specifier|public
name|OpensslAesCtrCipher
parameter_list|(
name|int
name|mode
parameter_list|)
throws|throws
name|GeneralSecurityException
block|{
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
name|cipher
operator|=
name|OpensslCipher
operator|.
name|getInstance
argument_list|(
name|SUITE
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
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
name|cipher
operator|.
name|init
argument_list|(
name|mode
argument_list|,
name|key
argument_list|,
name|iv
argument_list|)
expr_stmt|;
block|}
comment|/**      * AES-CTR will consume all of the input data. It requires enough space in       * the destination buffer to encrypt entire input buffer.      */
annotation|@
name|Override
DECL|method|encrypt (ByteBuffer inBuffer, ByteBuffer outBuffer)
specifier|public
name|void
name|encrypt
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
name|process
argument_list|(
name|inBuffer
argument_list|,
name|outBuffer
argument_list|)
expr_stmt|;
block|}
comment|/**      * AES-CTR will consume all of the input data. It requires enough space in      * the destination buffer to decrypt entire input buffer.      */
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
name|process
argument_list|(
name|inBuffer
argument_list|,
name|outBuffer
argument_list|)
expr_stmt|;
block|}
DECL|method|process (ByteBuffer inBuffer, ByteBuffer outBuffer)
specifier|private
name|void
name|process
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
comment|// OpensslCipher#update will maintain crypto context.
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
comment|/**            * Typically code will not get here. OpensslCipher#update will             * consume all input data and put result in outBuffer.             * OpensslCipher#doFinal will reset the crypto context.            */
name|contextReset
operator|=
literal|true
expr_stmt|;
name|cipher
operator|.
name|doFinal
argument_list|(
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
block|}
end_class

end_unit

