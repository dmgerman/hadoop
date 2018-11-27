begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.security.x509.keys
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|security
operator|.
name|x509
operator|.
name|keys
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
name|hdds
operator|.
name|security
operator|.
name|x509
operator|.
name|SecurityConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyPair
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyPairGenerator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchProviderException
import|;
end_import

begin_comment
comment|/**  * A class to generate Key Pair for use with Certificates.  */
end_comment

begin_class
DECL|class|HDDSKeyGenerator
specifier|public
class|class
name|HDDSKeyGenerator
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|HDDSKeyGenerator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|securityConfig
specifier|private
specifier|final
name|SecurityConfig
name|securityConfig
decl_stmt|;
comment|/**    * Constructor for HDDSKeyGenerator.    *    * @param configuration - config    */
DECL|method|HDDSKeyGenerator (Configuration configuration)
specifier|public
name|HDDSKeyGenerator
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
block|{
name|this
operator|.
name|securityConfig
operator|=
operator|new
name|SecurityConfig
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor that takes a SecurityConfig as the Argument.    *    * @param config - SecurityConfig    */
DECL|method|HDDSKeyGenerator (SecurityConfig config)
specifier|public
name|HDDSKeyGenerator
parameter_list|(
name|SecurityConfig
name|config
parameter_list|)
block|{
name|this
operator|.
name|securityConfig
operator|=
name|config
expr_stmt|;
block|}
comment|/**    * Returns the Security config used for this object.    *    * @return SecurityConfig    */
DECL|method|getSecurityConfig ()
specifier|public
name|SecurityConfig
name|getSecurityConfig
parameter_list|()
block|{
return|return
name|securityConfig
return|;
block|}
comment|/**    * Use Config to generate key.    *    * @return KeyPair    * @throws NoSuchProviderException  - On Error, due to missing Java    *                                  dependencies.    * @throws NoSuchAlgorithmException - On Error,  due to missing Java    *                                  dependencies.    */
DECL|method|generateKey ()
specifier|public
name|KeyPair
name|generateKey
parameter_list|()
throws|throws
name|NoSuchProviderException
throws|,
name|NoSuchAlgorithmException
block|{
return|return
name|generateKey
argument_list|(
name|securityConfig
operator|.
name|getSize
argument_list|()
argument_list|,
name|securityConfig
operator|.
name|getKeyAlgo
argument_list|()
argument_list|,
name|securityConfig
operator|.
name|getProvider
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Specify the size -- all other parameters are used from config.    *    * @param size - int, valid key sizes.    * @return KeyPair    * @throws NoSuchProviderException  - On Error, due to missing Java    *                                  dependencies.    * @throws NoSuchAlgorithmException - On Error,  due to missing Java    *                                  dependencies.    */
DECL|method|generateKey (int size)
specifier|public
name|KeyPair
name|generateKey
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|NoSuchProviderException
throws|,
name|NoSuchAlgorithmException
block|{
return|return
name|generateKey
argument_list|(
name|size
argument_list|,
name|securityConfig
operator|.
name|getKeyAlgo
argument_list|()
argument_list|,
name|securityConfig
operator|.
name|getProvider
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Custom Key Generation, all values are user provided.    *    * @param size - Key Size    * @param algorithm - Algorithm to use    * @param provider - Security provider.    * @return KeyPair.    * @throws NoSuchProviderException  - On Error, due to missing Java    *                                  dependencies.    * @throws NoSuchAlgorithmException - On Error,  due to missing Java    *                                  dependencies.    */
DECL|method|generateKey (int size, String algorithm, String provider)
specifier|public
name|KeyPair
name|generateKey
parameter_list|(
name|int
name|size
parameter_list|,
name|String
name|algorithm
parameter_list|,
name|String
name|provider
parameter_list|)
throws|throws
name|NoSuchProviderException
throws|,
name|NoSuchAlgorithmException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Generating key pair using size:{}, Algorithm:{}, Provider:{}"
argument_list|,
name|size
argument_list|,
name|algorithm
argument_list|,
name|provider
argument_list|)
expr_stmt|;
name|KeyPairGenerator
name|generator
init|=
name|KeyPairGenerator
operator|.
name|getInstance
argument_list|(
name|algorithm
argument_list|,
name|provider
argument_list|)
decl_stmt|;
name|generator
operator|.
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
return|return
name|generator
operator|.
name|generateKeyPair
argument_list|()
return|;
block|}
block|}
end_class

end_unit

