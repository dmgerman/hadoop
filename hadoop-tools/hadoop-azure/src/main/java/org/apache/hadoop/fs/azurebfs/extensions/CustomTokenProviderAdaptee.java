begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.extensions
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
name|extensions
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * This interface provides an extensibility model for customizing the acquisition  * of Azure Active Directory Access Tokens.   When "fs.azure.account.auth.type" is  * set to "Custom", implementors may use the  * "fs.azure.account.oauth.provider.type.{accountName}" configuration property  * to specify a class with a custom implementation of CustomTokenProviderAdaptee.  * This class will be dynamically loaded, initialized, and invoked to provide  * AAD Access Tokens and their Expiry.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
literal|"authorization-subsystems"
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|CustomTokenProviderAdaptee
specifier|public
interface|interface
name|CustomTokenProviderAdaptee
block|{
comment|/**    * Initialize with supported configuration. This method is invoked when the    * (URI, Configuration)} method is invoked.    *    * @param configuration Configuration object    * @param accountName Account Name    * @throws IOException if instance can not be configured.    */
DECL|method|initialize (Configuration configuration, String accountName)
name|void
name|initialize
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|String
name|accountName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Obtain the access token that should be added to https connection's header.    * Will be called depending upon {@link #getExpiryTime()} expiry time is set,    * so implementations should be performant. Implementations are responsible    * for any refreshing of the token.    *    * @return String containing the access token    * @throws IOException if there is an error fetching the token    */
DECL|method|getAccessToken ()
name|String
name|getAccessToken
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Obtain expiry time of the token. If implementation is performant enough to    * maintain expiry and expect {@link #getAccessToken()} call for every    * connection then safe to return current or past time.    *    * However recommended to use the token expiry time received from Azure Active    * Directory.    *    * @return Date to expire access token retrieved from AAD.    */
DECL|method|getExpiryTime ()
name|Date
name|getExpiryTime
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

