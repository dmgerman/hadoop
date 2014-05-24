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
name|conf
operator|.
name|Configuration
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
name|HADOOP_SECURITY_CRYPTO_JCE_PROVIDER_KEY
import|;
end_import

begin_comment
comment|/**  * Implement the AES-CTR crypto codec using JCE provider.  */
end_comment

begin_class
DECL|class|JCEAESCTRCryptoCodec
specifier|public
class|class
name|JCEAESCTRCryptoCodec
extends|extends
name|AESCTRCryptoCodec
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|provider
specifier|private
name|String
name|provider
decl_stmt|;
DECL|method|JCEAESCTRCryptoCodec ()
specifier|public
name|JCEAESCTRCryptoCodec
parameter_list|()
block|{   }
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
name|provider
operator|=
name|conf
operator|.
name|get
argument_list|(
name|HADOOP_SECURITY_CRYPTO_JCE_PROVIDER_KEY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEncryptor ()
specifier|public
name|Encryptor
name|getEncryptor
parameter_list|()
throws|throws
name|GeneralSecurityException
block|{
return|return
operator|new
name|JCEAESCTREncryptor
argument_list|(
name|provider
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDecryptor ()
specifier|public
name|Decryptor
name|getDecryptor
parameter_list|()
throws|throws
name|GeneralSecurityException
block|{
return|return
operator|new
name|JCEAESCTRDecryptor
argument_list|(
name|provider
argument_list|)
return|;
block|}
block|}
end_class

end_unit

