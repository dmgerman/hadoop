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
name|fs
operator|.
name|s3a
operator|.
name|Constants
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

begin_comment
comment|/**  * All the constants related to delegation tokens.  * Not in the normal S3 constants while unstable.  *  * Where possible, the existing assumed role properties are used to configure  * STS binding, default ARN, etc. This makes documenting everything that  * much easier and avoids trying to debug precisely which sts endpoint  * property should be set.  *  * Most settings here are replicated in {@code core-default.xml}; the  * values MUST be kept in sync.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|DelegationConstants
specifier|public
specifier|final
class|class
name|DelegationConstants
block|{
comment|/**    * Endpoint for session tokens, used when building delegation tokens:    * {@value}.    * @see<a href="https://docs.aws.amazon.com/general/latest/gr/rande.html#sts_region">STS regions</a>    */
DECL|field|DELEGATION_TOKEN_ENDPOINT
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_ENDPOINT
init|=
name|Constants
operator|.
name|ASSUMED_ROLE_STS_ENDPOINT
decl_stmt|;
comment|/**    * Default endpoint for session tokens: {@value}.    */
DECL|field|DEFAULT_DELEGATION_TOKEN_ENDPOINT
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_DELEGATION_TOKEN_ENDPOINT
init|=
name|Constants
operator|.
name|DEFAULT_ASSUMED_ROLE_STS_ENDPOINT
decl_stmt|;
comment|/**    * Region for DT issuing; must be non-empty if the endpoint is set: {@value}.    */
DECL|field|DELEGATION_TOKEN_REGION
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_REGION
init|=
name|Constants
operator|.
name|ASSUMED_ROLE_STS_ENDPOINT_REGION
decl_stmt|;
comment|/**    * Region default: {@value}.    */
DECL|field|DEFAULT_DELEGATION_TOKEN_REGION
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_DELEGATION_TOKEN_REGION
init|=
name|Constants
operator|.
name|ASSUMED_ROLE_STS_ENDPOINT_REGION_DEFAULT
decl_stmt|;
comment|/**    * Duration of tokens in time: {@value}.    */
DECL|field|DELEGATION_TOKEN_DURATION
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_DURATION
init|=
name|Constants
operator|.
name|ASSUMED_ROLE_SESSION_DURATION
decl_stmt|;
comment|/**    * Default duration of a delegation token: {@value}.    * Must be in the range supported by STS.    */
DECL|field|DEFAULT_DELEGATION_TOKEN_DURATION
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_DELEGATION_TOKEN_DURATION
init|=
name|Constants
operator|.
name|ASSUMED_ROLE_SESSION_DURATION_DEFAULT
decl_stmt|;
comment|/**    * Key to list AWS credential providers for Session/role    * credentials: {@value}.    */
DECL|field|DELEGATION_TOKEN_CREDENTIALS_PROVIDER
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_CREDENTIALS_PROVIDER
init|=
name|Constants
operator|.
name|AWS_CREDENTIALS_PROVIDER
decl_stmt|;
comment|/**    * ARN of the delegation token: {@value}.    * Required for the role token.    */
DECL|field|DELEGATION_TOKEN_ROLE_ARN
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_ROLE_ARN
init|=
name|Constants
operator|.
name|ASSUMED_ROLE_ARN
decl_stmt|;
comment|/**    * Property containing classname for token binding: {@value}.    */
DECL|field|DELEGATION_TOKEN_BINDING
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_BINDING
init|=
literal|"fs.s3a.delegation.token.binding"
decl_stmt|;
comment|/**    * Session Token binding classname: {@value}.    */
DECL|field|DELEGATION_TOKEN_SESSION_BINDING
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_SESSION_BINDING
init|=
literal|"org.apache.hadoop.fs.s3a.auth.delegation.SessionTokenBinding"
decl_stmt|;
comment|/**    * Default token binding {@value}.    */
DECL|field|DEFAULT_DELEGATION_TOKEN_BINDING
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_DELEGATION_TOKEN_BINDING
init|=
literal|""
decl_stmt|;
comment|/**    * Token binding to pass full credentials: {@value}.    */
DECL|field|DELEGATION_TOKEN_FULL_CREDENTIALS_BINDING
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_FULL_CREDENTIALS_BINDING
init|=
literal|"org.apache.hadoop.fs.s3a.auth.delegation.FullCredentialsTokenBinding"
decl_stmt|;
comment|/**    * Role DTs: {@value}.    */
DECL|field|DELEGATION_TOKEN_ROLE_BINDING
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_ROLE_BINDING
init|=
literal|"org.apache.hadoop.fs.s3a.auth.delegation.RoleTokenBinding"
decl_stmt|;
comment|/** Prefix for token names: {@value}. */
DECL|field|TOKEN_NAME_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_NAME_PREFIX
init|=
literal|"S3ADelegationToken/"
decl_stmt|;
comment|/** Name of session token: {@value}. */
DECL|field|SESSION_TOKEN_NAME
specifier|public
specifier|static
specifier|final
name|String
name|SESSION_TOKEN_NAME
init|=
name|TOKEN_NAME_PREFIX
operator|+
literal|"Session"
decl_stmt|;
comment|/** Kind of the session token; value is {@link #SESSION_TOKEN_NAME}. */
DECL|field|SESSION_TOKEN_KIND
specifier|public
specifier|static
specifier|final
name|Text
name|SESSION_TOKEN_KIND
init|=
operator|new
name|Text
argument_list|(
name|SESSION_TOKEN_NAME
argument_list|)
decl_stmt|;
comment|/** Name of full token: {@value}. */
DECL|field|FULL_TOKEN_NAME
specifier|public
specifier|static
specifier|final
name|String
name|FULL_TOKEN_NAME
init|=
name|TOKEN_NAME_PREFIX
operator|+
literal|"Full"
decl_stmt|;
comment|/** Kind of the full token; value is {@link #FULL_TOKEN_NAME}. */
DECL|field|FULL_TOKEN_KIND
specifier|public
specifier|static
specifier|final
name|Text
name|FULL_TOKEN_KIND
init|=
operator|new
name|Text
argument_list|(
name|FULL_TOKEN_NAME
argument_list|)
decl_stmt|;
comment|/** Name of role token: {@value}. */
DECL|field|ROLE_TOKEN_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ROLE_TOKEN_NAME
init|=
name|TOKEN_NAME_PREFIX
operator|+
literal|"Role"
decl_stmt|;
comment|/** Kind of the role token; value is {@link #ROLE_TOKEN_NAME}. */
DECL|field|ROLE_TOKEN_KIND
specifier|public
specifier|static
specifier|final
name|Text
name|ROLE_TOKEN_KIND
init|=
operator|new
name|Text
argument_list|(
name|ROLE_TOKEN_NAME
argument_list|)
decl_stmt|;
comment|/**    * Package-scoped option to control level that duration info on token    * binding operations are logged at.    * Value: {@value}.    */
DECL|field|DURATION_LOG_AT_INFO
specifier|static
specifier|final
name|boolean
name|DURATION_LOG_AT_INFO
init|=
literal|true
decl_stmt|;
comment|/**    * If the token binding auth chain is only session-level auth, you    * can't use the role binding: {@value}.    */
DECL|field|E_NO_SESSION_TOKENS_FOR_ROLE_BINDING
specifier|public
specifier|static
specifier|final
name|String
name|E_NO_SESSION_TOKENS_FOR_ROLE_BINDING
init|=
literal|"Cannot issue S3A Role Delegation Tokens without full AWS credentials"
decl_stmt|;
comment|/**    * The standard STS server.    */
DECL|field|STS_STANDARD
specifier|public
specifier|static
specifier|final
name|String
name|STS_STANDARD
init|=
literal|"sts.amazonaws.com"
decl_stmt|;
DECL|method|DelegationConstants ()
specifier|private
name|DelegationConstants
parameter_list|()
block|{   }
block|}
end_class

end_unit

