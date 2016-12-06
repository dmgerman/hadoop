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

begin_comment
comment|/**  * KMS REST and JSON constants and utility methods for the KMSServer.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|KMSRESTConstants
specifier|public
class|class
name|KMSRESTConstants
block|{
DECL|field|SERVICE_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|SERVICE_VERSION
init|=
literal|"/v1"
decl_stmt|;
DECL|field|KEY_RESOURCE
specifier|public
specifier|static
specifier|final
name|String
name|KEY_RESOURCE
init|=
literal|"key"
decl_stmt|;
DECL|field|KEYS_RESOURCE
specifier|public
specifier|static
specifier|final
name|String
name|KEYS_RESOURCE
init|=
literal|"keys"
decl_stmt|;
DECL|field|KEYS_METADATA_RESOURCE
specifier|public
specifier|static
specifier|final
name|String
name|KEYS_METADATA_RESOURCE
init|=
name|KEYS_RESOURCE
operator|+
literal|"/metadata"
decl_stmt|;
DECL|field|KEYS_NAMES_RESOURCE
specifier|public
specifier|static
specifier|final
name|String
name|KEYS_NAMES_RESOURCE
init|=
name|KEYS_RESOURCE
operator|+
literal|"/names"
decl_stmt|;
DECL|field|KEY_VERSION_RESOURCE
specifier|public
specifier|static
specifier|final
name|String
name|KEY_VERSION_RESOURCE
init|=
literal|"keyversion"
decl_stmt|;
DECL|field|METADATA_SUB_RESOURCE
specifier|public
specifier|static
specifier|final
name|String
name|METADATA_SUB_RESOURCE
init|=
literal|"_metadata"
decl_stmt|;
DECL|field|VERSIONS_SUB_RESOURCE
specifier|public
specifier|static
specifier|final
name|String
name|VERSIONS_SUB_RESOURCE
init|=
literal|"_versions"
decl_stmt|;
DECL|field|EEK_SUB_RESOURCE
specifier|public
specifier|static
specifier|final
name|String
name|EEK_SUB_RESOURCE
init|=
literal|"_eek"
decl_stmt|;
DECL|field|CURRENT_VERSION_SUB_RESOURCE
specifier|public
specifier|static
specifier|final
name|String
name|CURRENT_VERSION_SUB_RESOURCE
init|=
literal|"_currentversion"
decl_stmt|;
DECL|field|KEY
specifier|public
specifier|static
specifier|final
name|String
name|KEY
init|=
literal|"key"
decl_stmt|;
DECL|field|EEK_OP
specifier|public
specifier|static
specifier|final
name|String
name|EEK_OP
init|=
literal|"eek_op"
decl_stmt|;
DECL|field|EEK_GENERATE
specifier|public
specifier|static
specifier|final
name|String
name|EEK_GENERATE
init|=
literal|"generate"
decl_stmt|;
DECL|field|EEK_DECRYPT
specifier|public
specifier|static
specifier|final
name|String
name|EEK_DECRYPT
init|=
literal|"decrypt"
decl_stmt|;
DECL|field|EEK_NUM_KEYS
specifier|public
specifier|static
specifier|final
name|String
name|EEK_NUM_KEYS
init|=
literal|"num_keys"
decl_stmt|;
DECL|field|EEK_REENCRYPT
specifier|public
specifier|static
specifier|final
name|String
name|EEK_REENCRYPT
init|=
literal|"reencrypt"
decl_stmt|;
DECL|field|IV_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|IV_FIELD
init|=
literal|"iv"
decl_stmt|;
DECL|field|NAME_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|NAME_FIELD
init|=
literal|"name"
decl_stmt|;
DECL|field|CIPHER_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|CIPHER_FIELD
init|=
literal|"cipher"
decl_stmt|;
DECL|field|LENGTH_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|LENGTH_FIELD
init|=
literal|"length"
decl_stmt|;
DECL|field|DESCRIPTION_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION_FIELD
init|=
literal|"description"
decl_stmt|;
DECL|field|ATTRIBUTES_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|ATTRIBUTES_FIELD
init|=
literal|"attributes"
decl_stmt|;
DECL|field|CREATED_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|CREATED_FIELD
init|=
literal|"created"
decl_stmt|;
DECL|field|VERSIONS_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|VERSIONS_FIELD
init|=
literal|"versions"
decl_stmt|;
DECL|field|MATERIAL_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|MATERIAL_FIELD
init|=
literal|"material"
decl_stmt|;
DECL|field|VERSION_NAME_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|VERSION_NAME_FIELD
init|=
literal|"versionName"
decl_stmt|;
DECL|field|ENCRYPTED_KEY_VERSION_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|ENCRYPTED_KEY_VERSION_FIELD
init|=
literal|"encryptedKeyVersion"
decl_stmt|;
DECL|field|ERROR_EXCEPTION_JSON
specifier|public
specifier|static
specifier|final
name|String
name|ERROR_EXCEPTION_JSON
init|=
literal|"exception"
decl_stmt|;
DECL|field|ERROR_MESSAGE_JSON
specifier|public
specifier|static
specifier|final
name|String
name|ERROR_MESSAGE_JSON
init|=
literal|"message"
decl_stmt|;
block|}
end_class

end_unit

