begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
operator|.
name|security
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
name|UserGroupInformation
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
name|security
operator|.
name|token
operator|.
name|TokenRenewer
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
name|delegation
operator|.
name|AbstractDelegationTokenIdentifier
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
name|delegation
operator|.
name|web
operator|.
name|DelegationTokenAuthenticatedURL
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
name|delegation
operator|.
name|web
operator|.
name|DelegationTokenAuthenticator
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
name|delegation
operator|.
name|web
operator|.
name|KerberosDelegationTokenAuthenticator
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_comment
comment|/**  * Token Renewer for renewing WASB delegation tokens with remote service.  */
end_comment

begin_class
DECL|class|WasbTokenRenewer
specifier|public
class|class
name|WasbTokenRenewer
extends|extends
name|TokenRenewer
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|WasbTokenRenewer
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Checks if this particular object handles the Kind of token passed.    * @param kind the kind of the token    * @return true if it handles passed token kind false otherwise.    */
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
name|WasbDelegationTokenIdentifier
operator|.
name|TOKEN_KIND
operator|.
name|equals
argument_list|(
name|kind
argument_list|)
return|;
block|}
comment|/**    * Checks if passed token is managed.    * @param token the token being checked    * @return true if it is managed.    * @throws IOException thrown when evaluating if token is managed.    */
annotation|@
name|Override
DECL|method|isManaged (Token<?> token)
specifier|public
name|boolean
name|isManaged
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Renew the delegation token.    * @param token token to renew.    * @param conf configuration object.    * @return extended expiry time of the token.    * @throws IOException thrown when trying get current user.    * @throws InterruptedException thrown when thread is interrupted    */
annotation|@
name|Override
DECL|method|renew (final Token<?> token, Configuration conf)
specifier|public
name|long
name|renew
parameter_list|(
specifier|final
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
throws|,
name|InterruptedException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Renewing the delegation token"
argument_list|)
expr_stmt|;
specifier|final
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|connectUgi
init|=
name|ugi
operator|.
name|getRealUser
argument_list|()
decl_stmt|;
specifier|final
name|UserGroupInformation
name|proxyUser
init|=
name|connectUgi
decl_stmt|;
if|if
condition|(
name|connectUgi
operator|==
literal|null
condition|)
block|{
name|connectUgi
operator|=
name|ugi
expr_stmt|;
block|}
name|connectUgi
operator|.
name|checkTGTAndReloginFromKeytab
argument_list|()
expr_stmt|;
specifier|final
name|DelegationTokenAuthenticatedURL
operator|.
name|Token
name|authToken
init|=
operator|new
name|DelegationTokenAuthenticatedURL
operator|.
name|Token
argument_list|()
decl_stmt|;
name|authToken
operator|.
name|setDelegationToken
argument_list|(
operator|(
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
operator|)
name|token
argument_list|)
expr_stmt|;
specifier|final
name|String
name|credServiceUrl
init|=
name|conf
operator|.
name|get
argument_list|(
name|Constants
operator|.
name|KEY_CRED_SERVICE_URL
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"http://%s:%s"
argument_list|,
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getCanonicalHostName
argument_list|()
argument_list|,
name|Constants
operator|.
name|DEFAULT_CRED_SERVICE_PORT
argument_list|)
argument_list|)
decl_stmt|;
name|DelegationTokenAuthenticator
name|authenticator
init|=
operator|new
name|KerberosDelegationTokenAuthenticator
argument_list|()
decl_stmt|;
specifier|final
name|DelegationTokenAuthenticatedURL
name|authURL
init|=
operator|new
name|DelegationTokenAuthenticatedURL
argument_list|(
name|authenticator
argument_list|)
decl_stmt|;
return|return
name|connectUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|authURL
operator|.
name|renewDelegationToken
argument_list|(
operator|new
name|URL
argument_list|(
name|credServiceUrl
operator|+
name|Constants
operator|.
name|DEFAULT_DELEGATION_TOKEN_MANAGER_ENDPOINT
argument_list|)
argument_list|,
name|authToken
argument_list|,
operator|(
name|proxyUser
operator|!=
literal|null
operator|)
condition|?
name|ugi
operator|.
name|getShortUserName
argument_list|()
else|:
literal|null
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**    * Cancel the delegation token.    * @param token token to cancel.    * @param conf configuration object.    * @throws IOException thrown when trying get current user.    * @throws InterruptedException thrown when thread is interrupted.    */
annotation|@
name|Override
DECL|method|cancel (final Token<?> token, Configuration conf)
specifier|public
name|void
name|cancel
parameter_list|(
specifier|final
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
throws|,
name|InterruptedException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Cancelling the delegation token"
argument_list|)
expr_stmt|;
specifier|final
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|connectUgi
init|=
name|ugi
operator|.
name|getRealUser
argument_list|()
decl_stmt|;
specifier|final
name|UserGroupInformation
name|proxyUser
init|=
name|connectUgi
decl_stmt|;
if|if
condition|(
name|connectUgi
operator|==
literal|null
condition|)
block|{
name|connectUgi
operator|=
name|ugi
expr_stmt|;
block|}
name|connectUgi
operator|.
name|checkTGTAndReloginFromKeytab
argument_list|()
expr_stmt|;
specifier|final
name|DelegationTokenAuthenticatedURL
operator|.
name|Token
name|authToken
init|=
operator|new
name|DelegationTokenAuthenticatedURL
operator|.
name|Token
argument_list|()
decl_stmt|;
name|authToken
operator|.
name|setDelegationToken
argument_list|(
operator|(
name|Token
argument_list|<
name|AbstractDelegationTokenIdentifier
argument_list|>
operator|)
name|token
argument_list|)
expr_stmt|;
specifier|final
name|String
name|credServiceUrl
init|=
name|conf
operator|.
name|get
argument_list|(
name|Constants
operator|.
name|KEY_CRED_SERVICE_URL
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"http://%s:%s"
argument_list|,
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getCanonicalHostName
argument_list|()
argument_list|,
name|Constants
operator|.
name|DEFAULT_CRED_SERVICE_PORT
argument_list|)
argument_list|)
decl_stmt|;
name|DelegationTokenAuthenticator
name|authenticator
init|=
operator|new
name|KerberosDelegationTokenAuthenticator
argument_list|()
decl_stmt|;
specifier|final
name|DelegationTokenAuthenticatedURL
name|authURL
init|=
operator|new
name|DelegationTokenAuthenticatedURL
argument_list|(
name|authenticator
argument_list|)
decl_stmt|;
name|connectUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|authURL
operator|.
name|cancelDelegationToken
argument_list|(
operator|new
name|URL
argument_list|(
name|credServiceUrl
operator|+
name|Constants
operator|.
name|DEFAULT_DELEGATION_TOKEN_MANAGER_ENDPOINT
argument_list|)
argument_list|,
name|authToken
argument_list|,
operator|(
name|proxyUser
operator|!=
literal|null
operator|)
condition|?
name|ugi
operator|.
name|getShortUserName
argument_list|()
else|:
literal|null
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

