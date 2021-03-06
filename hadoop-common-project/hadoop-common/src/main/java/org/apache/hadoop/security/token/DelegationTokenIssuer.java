begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.token
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|token
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
name|Credentials
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
name|List
import|;
end_import

begin_comment
comment|/**  * Class for issuing delegation tokens.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|,
literal|"Yarn"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|DelegationTokenIssuer
specifier|public
interface|interface
name|DelegationTokenIssuer
block|{
comment|/**    * The service name used as the alias for the  token in the credential    * token map.  addDelegationTokens will use this to determine if    * a token exists, and if not, add a new token with this alias.    */
DECL|method|getCanonicalServiceName ()
name|String
name|getCanonicalServiceName
parameter_list|()
function_decl|;
comment|/**    * Unconditionally get a new token with the optional renewer.  Returning    * null indicates the service does not issue tokens.    */
DECL|method|getDelegationToken (String renewer)
name|Token
argument_list|<
name|?
argument_list|>
name|getDelegationToken
parameter_list|(
name|String
name|renewer
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Issuers may need tokens from additional services.    */
DECL|method|getAdditionalTokenIssuers ()
specifier|default
name|DelegationTokenIssuer
index|[]
name|getAdditionalTokenIssuers
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Given a renewer, add delegation tokens for issuer and it's child issuers    * to the<code>Credentials</code> object if it is not already present.    *<p>    * Note: This method is not intended to be overridden.  Issuers should    * implement getCanonicalService and getDelegationToken to ensure    * consistent token acquisition behavior.    *    * @param renewer the user allowed to renew the delegation tokens    * @param credentials cache in which to add new delegation tokens    * @return list of new delegation tokens    * @throws IOException thrown if IOException if an IO error occurs.    */
DECL|method|addDelegationTokens ( final String renewer, Credentials credentials)
specifier|default
name|Token
argument_list|<
name|?
argument_list|>
index|[]
name|addDelegationTokens
parameter_list|(
specifier|final
name|String
name|renewer
parameter_list|,
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|credentials
operator|==
literal|null
condition|)
block|{
name|credentials
operator|=
operator|new
name|Credentials
argument_list|()
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|Token
argument_list|<
name|?
argument_list|>
argument_list|>
name|tokens
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|collectDelegationTokens
argument_list|(
name|this
argument_list|,
name|renewer
argument_list|,
name|credentials
argument_list|,
name|tokens
argument_list|)
expr_stmt|;
return|return
name|tokens
operator|.
name|toArray
argument_list|(
operator|new
name|Token
argument_list|<
name|?
argument_list|>
index|[
name|tokens
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**    * NEVER call this method directly.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|collectDelegationTokens ( final DelegationTokenIssuer issuer, final String renewer, final Credentials credentials, final List<Token<?>> tokens)
specifier|static
name|void
name|collectDelegationTokens
parameter_list|(
specifier|final
name|DelegationTokenIssuer
name|issuer
parameter_list|,
specifier|final
name|String
name|renewer
parameter_list|,
specifier|final
name|Credentials
name|credentials
parameter_list|,
specifier|final
name|List
argument_list|<
name|Token
argument_list|<
name|?
argument_list|>
argument_list|>
name|tokens
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|serviceName
init|=
name|issuer
operator|.
name|getCanonicalServiceName
argument_list|()
decl_stmt|;
comment|// Collect token of the this issuer and then of its embedded children
if|if
condition|(
name|serviceName
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Text
name|service
init|=
operator|new
name|Text
argument_list|(
name|serviceName
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|?
argument_list|>
name|token
init|=
name|credentials
operator|.
name|getToken
argument_list|(
name|service
argument_list|)
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
name|token
operator|=
name|issuer
operator|.
name|getDelegationToken
argument_list|(
name|renewer
argument_list|)
expr_stmt|;
if|if
condition|(
name|token
operator|!=
literal|null
condition|)
block|{
name|tokens
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|credentials
operator|.
name|addToken
argument_list|(
name|service
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Now collect the tokens from the children.
specifier|final
name|DelegationTokenIssuer
index|[]
name|ancillary
init|=
name|issuer
operator|.
name|getAdditionalTokenIssuers
argument_list|()
decl_stmt|;
if|if
condition|(
name|ancillary
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|DelegationTokenIssuer
name|subIssuer
range|:
name|ancillary
control|)
block|{
name|collectDelegationTokens
argument_list|(
name|subIssuer
argument_list|,
name|renewer
argument_list|,
name|credentials
argument_list|,
name|tokens
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_interface

end_unit

