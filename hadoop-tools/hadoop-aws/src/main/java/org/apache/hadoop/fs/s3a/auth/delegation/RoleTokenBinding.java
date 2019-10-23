begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.auth.delegation
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|auth
operator|.
name|delegation
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
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|securitytoken
operator|.
name|model
operator|.
name|Credentials
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
name|annotations
operator|.
name|VisibleForTesting
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
name|fs
operator|.
name|s3a
operator|.
name|AWSCredentialProviderList
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
name|s3a
operator|.
name|Retries
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
name|s3a
operator|.
name|auth
operator|.
name|MarshalledCredentialProvider
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
name|s3a
operator|.
name|auth
operator|.
name|MarshalledCredentials
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
name|s3a
operator|.
name|auth
operator|.
name|RoleModel
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
name|s3a
operator|.
name|auth
operator|.
name|STSClientFactory
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|auth
operator|.
name|MarshalledCredentialBinding
operator|.
name|fromSTSCredentials
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
name|s3a
operator|.
name|auth
operator|.
name|delegation
operator|.
name|DelegationConstants
operator|.
name|DELEGATION_TOKEN_CREDENTIALS_PROVIDER
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
name|s3a
operator|.
name|auth
operator|.
name|delegation
operator|.
name|DelegationConstants
operator|.
name|DELEGATION_TOKEN_ROLE_ARN
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
name|s3a
operator|.
name|auth
operator|.
name|delegation
operator|.
name|DelegationConstants
operator|.
name|E_NO_SESSION_TOKENS_FOR_ROLE_BINDING
import|;
end_import

begin_comment
comment|/**  * Role Token support requests an explicit role and automatically restricts  * that role to the given policy of the binding.  * The session is locked down as much as possible.  */
end_comment

begin_class
DECL|class|RoleTokenBinding
specifier|public
class|class
name|RoleTokenBinding
extends|extends
name|SessionTokenBinding
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
name|RoleTokenBinding
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MODEL
specifier|private
specifier|static
specifier|final
name|RoleModel
name|MODEL
init|=
operator|new
name|RoleModel
argument_list|()
decl_stmt|;
comment|/**    * Wire name of this binding includes a version marker: {@value}.    */
DECL|field|NAME
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"RoleCredentials/001"
decl_stmt|;
comment|/**    * Error message when there is no Role ARN.    */
annotation|@
name|VisibleForTesting
DECL|field|E_NO_ARN
specifier|public
specifier|static
specifier|final
name|String
name|E_NO_ARN
init|=
literal|"No role ARN defined in "
operator|+
name|DELEGATION_TOKEN_ROLE_ARN
decl_stmt|;
DECL|field|COMPONENT
specifier|public
specifier|static
specifier|final
name|String
name|COMPONENT
init|=
literal|"Role Delegation Token"
decl_stmt|;
comment|/**    * Role ARN to use when requesting new tokens.    */
DECL|field|roleArn
specifier|private
name|String
name|roleArn
decl_stmt|;
comment|/**    * Constructor.    * Name is {@link #NAME}; token kind is    * {@link DelegationConstants#ROLE_TOKEN_KIND}.    */
DECL|method|RoleTokenBinding ()
specifier|public
name|RoleTokenBinding
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|DelegationConstants
operator|.
name|ROLE_TOKEN_KIND
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (final Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|roleArn
operator|=
name|getConfig
argument_list|()
operator|.
name|getTrimmed
argument_list|(
name|DELEGATION_TOKEN_ROLE_ARN
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a (wrapped) {@link MarshalledCredentialProvider} which    * requires the marshalled credentials to contain session secrets.    * @param retrievedIdentifier the incoming identifier.    * @return the provider chain.    * @throws IOException on failure    */
annotation|@
name|Override
DECL|method|bindToTokenIdentifier ( final AbstractS3ATokenIdentifier retrievedIdentifier)
specifier|public
name|AWSCredentialProviderList
name|bindToTokenIdentifier
parameter_list|(
specifier|final
name|AbstractS3ATokenIdentifier
name|retrievedIdentifier
parameter_list|)
throws|throws
name|IOException
block|{
name|RoleTokenIdentifier
name|tokenIdentifier
init|=
name|convertTokenIdentifier
argument_list|(
name|retrievedIdentifier
argument_list|,
name|RoleTokenIdentifier
operator|.
name|class
argument_list|)
decl_stmt|;
name|setTokenIdentifier
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|tokenIdentifier
argument_list|)
argument_list|)
expr_stmt|;
name|MarshalledCredentials
name|marshalledCredentials
init|=
name|tokenIdentifier
operator|.
name|getMarshalledCredentials
argument_list|()
decl_stmt|;
name|setExpirationDateTime
argument_list|(
name|marshalledCredentials
operator|.
name|getExpirationDateTime
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|AWSCredentialProviderList
argument_list|(
literal|"Role Token Binding"
argument_list|,
operator|new
name|MarshalledCredentialProvider
argument_list|(
name|COMPONENT
argument_list|,
name|getFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
argument_list|,
name|getConfig
argument_list|()
argument_list|,
name|marshalledCredentials
argument_list|,
name|MarshalledCredentials
operator|.
name|CredentialTypeRequired
operator|.
name|SessionOnly
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Create the Token Identifier.    * Looks for the option {@link DelegationConstants#DELEGATION_TOKEN_ROLE_ARN}    * in the config and fail if it is not set.    * @param policy the policy which will be used for the requested token.    * @param encryptionSecrets encryption secrets.    * @return the token.    * @throws IllegalArgumentException if there is no role defined.    * @throws IOException any problem acquiring the role.    */
annotation|@
name|Override
annotation|@
name|Retries
operator|.
name|RetryTranslated
DECL|method|createTokenIdentifier ( final Optional<RoleModel.Policy> policy, final EncryptionSecrets encryptionSecrets, final Text renewer)
specifier|public
name|RoleTokenIdentifier
name|createTokenIdentifier
parameter_list|(
specifier|final
name|Optional
argument_list|<
name|RoleModel
operator|.
name|Policy
argument_list|>
name|policy
parameter_list|,
specifier|final
name|EncryptionSecrets
name|encryptionSecrets
parameter_list|,
specifier|final
name|Text
name|renewer
parameter_list|)
throws|throws
name|IOException
block|{
name|requireServiceStarted
argument_list|()
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|roleArn
operator|.
name|isEmpty
argument_list|()
argument_list|,
name|E_NO_ARN
argument_list|)
expr_stmt|;
name|String
name|policyJson
init|=
name|policy
operator|.
name|isPresent
argument_list|()
condition|?
name|MODEL
operator|.
name|toJson
argument_list|(
name|policy
operator|.
name|get
argument_list|()
argument_list|)
else|:
literal|""
decl_stmt|;
specifier|final
name|STSClientFactory
operator|.
name|STSClient
name|client
init|=
name|prepareSTSClient
argument_list|()
operator|.
name|orElseThrow
argument_list|(
parameter_list|()
lambda|->
block|{
comment|// we've come in on a parent binding, so fail fast
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot issue delegation tokens because the credential"
operator|+
literal|" providers listed in "
operator|+
name|DELEGATION_TOKEN_CREDENTIALS_PROVIDER
operator|+
literal|" are returning session tokens"
argument_list|)
expr_stmt|;
return|return
operator|new
name|DelegationTokenIOException
argument_list|(
name|E_NO_SESSION_TOKENS_FOR_ROLE_BINDING
argument_list|)
return|;
block|}
argument_list|)
decl_stmt|;
name|Credentials
name|credentials
init|=
name|client
operator|.
name|requestRole
argument_list|(
name|roleArn
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|policyJson
argument_list|,
name|getDuration
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
return|return
operator|new
name|RoleTokenIdentifier
argument_list|(
name|getCanonicalUri
argument_list|()
argument_list|,
name|getOwnerText
argument_list|()
argument_list|,
name|renewer
argument_list|,
name|fromSTSCredentials
argument_list|(
name|credentials
argument_list|)
argument_list|,
name|encryptionSecrets
argument_list|,
name|AbstractS3ATokenIdentifier
operator|.
name|createDefaultOriginMessage
argument_list|()
operator|+
literal|" Role ARN="
operator|+
name|roleArn
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createEmptyIdentifier ()
specifier|public
name|RoleTokenIdentifier
name|createEmptyIdentifier
parameter_list|()
block|{
return|return
operator|new
name|RoleTokenIdentifier
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|super
operator|.
name|getDescription
argument_list|()
operator|+
literal|" Role ARN="
operator|+
operator|(
name|roleArn
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"(none)"
else|:
operator|(
literal|'"'
operator|+
name|roleArn
operator|+
literal|'"'
operator|)
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|bindingName ()
specifier|protected
name|String
name|bindingName
parameter_list|()
block|{
return|return
literal|"Role"
return|;
block|}
block|}
end_class

end_unit

