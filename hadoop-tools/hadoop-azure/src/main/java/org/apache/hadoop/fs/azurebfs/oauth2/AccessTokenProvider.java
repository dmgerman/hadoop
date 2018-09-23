begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.oauth2
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|oauth2
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
name|Date
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

begin_comment
comment|/**  * Returns an Azure Active Directory token when requested. The provider can  * cache the token if it has already retrieved one. If it does, then the  * provider is responsible for checking expiry and refreshing as needed.  *  * In other words, this is is a token cache that fetches tokens when  * requested, if the cached token has expired.  *  */
end_comment

begin_class
DECL|class|AccessTokenProvider
specifier|public
specifier|abstract
class|class
name|AccessTokenProvider
block|{
DECL|field|token
specifier|private
name|AzureADToken
name|token
decl_stmt|;
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
name|AccessTokenProvider
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * returns the {@link AzureADToken} cached (or retrieved) by this instance.    *    * @return {@link AzureADToken} containing the access token    * @throws IOException if there is an error fetching the token    */
DECL|method|getToken ()
specifier|public
specifier|synchronized
name|AzureADToken
name|getToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isTokenAboutToExpire
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"AAD Token is missing or expired:"
operator|+
literal|" Calling refresh-token from abstract base class"
argument_list|)
expr_stmt|;
name|token
operator|=
name|refreshToken
argument_list|()
expr_stmt|;
block|}
return|return
name|token
return|;
block|}
comment|/**    * the method to fetch the access token. Derived classes should override    * this method to actually get the token from Azure Active Directory.    *    * This method will be called initially, and then once when the token    * is about to expire.    *    *    * @return {@link AzureADToken} containing the access token    * @throws IOException if there is an error fetching the token    */
DECL|method|refreshToken ()
specifier|protected
specifier|abstract
name|AzureADToken
name|refreshToken
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Checks if the token is about to expire in the next 5 minutes.    * The 5 minute allowance is to allow for clock skew and also to    * allow for token to be refreshed in that much time.    *    * @return true if the token is expiring in next 5 minutes    */
DECL|method|isTokenAboutToExpire ()
specifier|private
name|boolean
name|isTokenAboutToExpire
parameter_list|()
block|{
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"AADToken: no token. Returning expiring=true"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
comment|// no token should have same response as expired token
block|}
name|boolean
name|expiring
init|=
literal|false
decl_stmt|;
comment|// allow 5 minutes for clock skew
name|long
name|approximatelyNow
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|FIVE_MINUTES
decl_stmt|;
if|if
condition|(
name|token
operator|.
name|getExpiry
argument_list|()
operator|.
name|getTime
argument_list|()
operator|<
name|approximatelyNow
condition|)
block|{
name|expiring
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|expiring
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"AADToken: token expiring: "
operator|+
name|token
operator|.
name|getExpiry
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" : Five-minute window: "
operator|+
operator|new
name|Date
argument_list|(
name|approximatelyNow
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|expiring
return|;
block|}
comment|// 5 minutes in milliseconds
DECL|field|FIVE_MINUTES
specifier|private
specifier|static
specifier|final
name|long
name|FIVE_MINUTES
init|=
literal|300
operator|*
literal|1000
decl_stmt|;
block|}
end_class

end_unit

