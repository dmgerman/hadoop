begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key.kms.server
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
operator|.
name|server
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
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
name|crypto
operator|.
name|key
operator|.
name|KeyProviderCryptoExtension
operator|.
name|EncryptedKeyVersion
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
name|kms
operator|.
name|KMSRESTConstants
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * JSON utility methods for the KMS.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|KMSServerJSONUtils
specifier|public
class|class
name|KMSServerJSONUtils
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|toJSON (KeyProvider.KeyVersion keyVersion)
specifier|public
specifier|static
name|Map
name|toJSON
parameter_list|(
name|KeyProvider
operator|.
name|KeyVersion
name|keyVersion
parameter_list|)
block|{
name|Map
name|json
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|keyVersion
operator|!=
literal|null
condition|)
block|{
name|json
operator|.
name|put
argument_list|(
name|KMSRESTConstants
operator|.
name|NAME_FIELD
argument_list|,
name|keyVersion
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|.
name|put
argument_list|(
name|KMSRESTConstants
operator|.
name|VERSION_NAME_FIELD
argument_list|,
name|keyVersion
operator|.
name|getVersionName
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|.
name|put
argument_list|(
name|KMSRESTConstants
operator|.
name|MATERIAL_FIELD
argument_list|,
name|Base64
operator|.
name|encodeBase64URLSafeString
argument_list|(
name|keyVersion
operator|.
name|getMaterial
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|json
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|toJSON (List<KeyProvider.KeyVersion> keyVersions)
specifier|public
specifier|static
name|List
name|toJSON
parameter_list|(
name|List
argument_list|<
name|KeyProvider
operator|.
name|KeyVersion
argument_list|>
name|keyVersions
parameter_list|)
block|{
name|List
name|json
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|keyVersions
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|KeyProvider
operator|.
name|KeyVersion
name|version
range|:
name|keyVersions
control|)
block|{
name|json
operator|.
name|add
argument_list|(
name|toJSON
argument_list|(
name|version
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|json
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|toJSON (EncryptedKeyVersion encryptedKeyVersion)
specifier|public
specifier|static
name|Map
name|toJSON
parameter_list|(
name|EncryptedKeyVersion
name|encryptedKeyVersion
parameter_list|)
block|{
name|Map
name|json
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|encryptedKeyVersion
operator|!=
literal|null
condition|)
block|{
name|json
operator|.
name|put
argument_list|(
name|KMSRESTConstants
operator|.
name|VERSION_NAME_FIELD
argument_list|,
name|encryptedKeyVersion
operator|.
name|getKeyVersionName
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|.
name|put
argument_list|(
name|KMSRESTConstants
operator|.
name|IV_FIELD
argument_list|,
name|Base64
operator|.
name|encodeBase64URLSafeString
argument_list|(
name|encryptedKeyVersion
operator|.
name|getIv
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|json
operator|.
name|put
argument_list|(
name|KMSRESTConstants
operator|.
name|ENCRYPTED_KEY_VERSION_FIELD
argument_list|,
name|toJSON
argument_list|(
name|encryptedKeyVersion
operator|.
name|getEncryptedKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|json
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|toJSON (String keyName, KeyProvider.Metadata meta)
specifier|public
specifier|static
name|Map
name|toJSON
parameter_list|(
name|String
name|keyName
parameter_list|,
name|KeyProvider
operator|.
name|Metadata
name|meta
parameter_list|)
block|{
name|Map
name|json
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|meta
operator|!=
literal|null
condition|)
block|{
name|json
operator|.
name|put
argument_list|(
name|KMSRESTConstants
operator|.
name|NAME_FIELD
argument_list|,
name|keyName
argument_list|)
expr_stmt|;
name|json
operator|.
name|put
argument_list|(
name|KMSRESTConstants
operator|.
name|CIPHER_FIELD
argument_list|,
name|meta
operator|.
name|getCipher
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|.
name|put
argument_list|(
name|KMSRESTConstants
operator|.
name|LENGTH_FIELD
argument_list|,
name|meta
operator|.
name|getBitLength
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|.
name|put
argument_list|(
name|KMSRESTConstants
operator|.
name|DESCRIPTION_FIELD
argument_list|,
name|meta
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|.
name|put
argument_list|(
name|KMSRESTConstants
operator|.
name|ATTRIBUTES_FIELD
argument_list|,
name|meta
operator|.
name|getAttributes
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|.
name|put
argument_list|(
name|KMSRESTConstants
operator|.
name|CREATED_FIELD
argument_list|,
name|meta
operator|.
name|getCreated
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|json
operator|.
name|put
argument_list|(
name|KMSRESTConstants
operator|.
name|VERSIONS_FIELD
argument_list|,
operator|(
name|long
operator|)
name|meta
operator|.
name|getVersions
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|json
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|toJSON (String[] keyNames, KeyProvider.Metadata[] metas)
specifier|public
specifier|static
name|List
name|toJSON
parameter_list|(
name|String
index|[]
name|keyNames
parameter_list|,
name|KeyProvider
operator|.
name|Metadata
index|[]
name|metas
parameter_list|)
block|{
name|List
name|json
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|keyNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|json
operator|.
name|add
argument_list|(
name|toJSON
argument_list|(
name|keyNames
index|[
name|i
index|]
argument_list|,
name|metas
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|json
return|;
block|}
block|}
end_class

end_unit

