begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key.kms
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
operator|.
name|kms
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
name|crypto
operator|.
name|key
operator|.
name|KeyProvider
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|Text
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|KMSUtil
import|;
end_import

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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|KMSDelegationToken
operator|.
name|TOKEN_LEGACY_KIND
import|;
end_import

begin_comment
comment|/**  * The {@link KMSTokenRenewer} that supports legacy tokens.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|Deprecated
DECL|class|KMSLegacyTokenRenewer
specifier|public
class|class
name|KMSLegacyTokenRenewer
extends|extends
name|KMSTokenRenewer
block|{
annotation|@
name|Override
DECL|method|handleKind (Text kind)
specifier|public
name|boolean
name|handleKind
parameter_list|(
name|Text
name|kind
parameter_list|)
block|{
return|return
name|kind
operator|.
name|equals
argument_list|(
name|TOKEN_LEGACY_KIND
argument_list|)
return|;
block|}
comment|/**    * Create a key provider for token renewal / cancellation.    * Caller is responsible for closing the key provider.    */
annotation|@
name|Override
DECL|method|createKeyProvider (Token<?> token, Configuration conf)
specifier|protected
name|KeyProvider
name|createKeyProvider
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|token
operator|.
name|getKind
argument_list|()
operator|.
name|equals
argument_list|(
name|TOKEN_LEGACY_KIND
argument_list|)
assert|;
comment|// Legacy tokens get service from configuration.
return|return
name|KMSUtil
operator|.
name|createKeyProvider
argument_list|(
name|conf
argument_list|,
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_KEY_PROVIDER_PATH
argument_list|)
return|;
block|}
block|}
end_class

end_unit

