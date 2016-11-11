begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.adl.oauth2
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|adl
operator|.
name|oauth2
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
name|conf
operator|.
name|Configuration
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
name|Date
import|;
end_import

begin_comment
comment|/**  * Provide an Azure Active Directory supported  * OAuth2 access token to be used to authenticate REST calls against Azure data  * lake file system {@link org.apache.hadoop.fs.adl.AdlFileSystem}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|AzureADTokenProvider
specifier|public
specifier|abstract
class|class
name|AzureADTokenProvider
block|{
comment|/**    * Initialize with supported configuration. This method is invoked when the    * {@link org.apache.hadoop.fs.adl.AdlFileSystem#initialize    * (URI, Configuration)} method is invoked.    *    * @param configuration Configuration object    * @throws IOException if instance can not be configured.    */
DECL|method|initialize (Configuration configuration)
specifier|public
specifier|abstract
name|void
name|initialize
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Obtain the access token that should be added to https connection's header.    * Will be called depending upon {@link #getExpiryTime()} expiry time is set,    * so implementations should be performant. Implementations are responsible    * for any refreshing of the token.    *    * @return String containing the access token    * @throws IOException if there is an error fetching the token    */
DECL|method|getAccessToken ()
specifier|public
specifier|abstract
name|String
name|getAccessToken
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Obtain expiry time of the token. If implementation is performant enough to    * maintain expiry and expect {@link #getAccessToken()} call for every    * connection then safe to return current or past time.    *    * However recommended to use the token expiry time received from Azure Active    * Directory.    *    * @return Date to expire access token retrieved from AAD.    */
DECL|method|getExpiryTime ()
specifier|public
specifier|abstract
name|Date
name|getExpiryTime
parameter_list|()
function_decl|;
block|}
end_class

end_unit

