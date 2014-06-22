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
name|security
operator|.
name|GeneralSecurityException
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
name|classification
operator|.
name|InterfaceStability
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
name|Configurable
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ReflectionUtils
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_CRYPTO_CODEC_CLASS_KEY
import|;
end_import

begin_comment
comment|/**  * Crypto codec class, encapsulates encryptor/decryptor pair.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|CryptoCodec
specifier|public
specifier|abstract
class|class
name|CryptoCodec
implements|implements
name|Configurable
block|{
DECL|method|getInstance (Configuration conf)
specifier|public
specifier|static
name|CryptoCodec
name|getInstance
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|CryptoCodec
argument_list|>
name|klass
init|=
name|conf
operator|.
name|getClass
argument_list|(
name|HADOOP_SECURITY_CRYPTO_CODEC_CLASS_KEY
argument_list|,
name|JCEAESCTRCryptoCodec
operator|.
name|class
argument_list|,
name|CryptoCodec
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|klass
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/**    * Get the block size of a block cipher.    * For different algorithms, the block size may be different.    * @return int the block size    */
DECL|method|getAlgorithmBlockSize ()
specifier|public
specifier|abstract
name|int
name|getAlgorithmBlockSize
parameter_list|()
function_decl|;
comment|/**    * Create a {@link org.apache.hadoop.crypto.Encryptor}.     * @return Encryptor the encryptor    */
DECL|method|createEncryptor ()
specifier|public
specifier|abstract
name|Encryptor
name|createEncryptor
parameter_list|()
throws|throws
name|GeneralSecurityException
function_decl|;
comment|/**    * Create a {@link org.apache.hadoop.crypto.Decryptor}.    * @return Decryptor the decryptor    */
DECL|method|createDecryptor ()
specifier|public
specifier|abstract
name|Decryptor
name|createDecryptor
parameter_list|()
throws|throws
name|GeneralSecurityException
function_decl|;
comment|/**    * This interface is only for Counter (CTR) mode. Generally the Encryptor    * or Decryptor calculates the IV and maintain encryption context internally.     * For example a {@link javax.crypto.Cipher} will maintain its encryption     * context internally when we do encryption/decryption using the     * Cipher#update interface.     *<p/>    * Encryption/Decryption is not always on the entire file. For example,    * in Hadoop, a node may only decrypt a portion of a file (i.e. a split).    * In these situations, the counter is derived from the file position.    *<p/>    * The IV can be calculated by combining the initial IV and the counter with     * a lossless operation (concatenation, addition, or XOR).    * @see http://en.wikipedia.org/wiki/Block_cipher_mode_of_operation#Counter_.28CTR.29    *     * @param initIV initial IV    * @param counter counter for input stream position     * @param IV the IV for input stream position    */
DECL|method|calculateIV (byte[] initIV, long counter, byte[] IV)
specifier|public
specifier|abstract
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
function_decl|;
comment|/**    * Generate a number of secure, random bytes suitable for cryptographic use.    * This method needs to be thread-safe.    *    * @param bytes byte array to populate with random data    */
DECL|method|generateSecureRandom (byte[] bytes)
specifier|public
specifier|abstract
name|void
name|generateSecureRandom
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
function_decl|;
block|}
end_class

end_unit

