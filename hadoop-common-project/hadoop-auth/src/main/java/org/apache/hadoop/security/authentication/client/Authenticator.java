begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authentication.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|authentication
operator|.
name|client
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
name|net
operator|.
name|URL
import|;
end_import

begin_comment
comment|/**  * Interface for client authentication mechanisms.  *<p>  * Implementations are use-once instances, they don't need to be thread safe.  */
end_comment

begin_interface
DECL|interface|Authenticator
specifier|public
interface|interface
name|Authenticator
block|{
comment|/**    * Sets a {@link ConnectionConfigurator} instance to use for    * configuring connections.    *    * @param configurator the {@link ConnectionConfigurator} instance.    */
DECL|method|setConnectionConfigurator (ConnectionConfigurator configurator)
specifier|public
name|void
name|setConnectionConfigurator
parameter_list|(
name|ConnectionConfigurator
name|configurator
parameter_list|)
function_decl|;
comment|/**    * Authenticates against a URL and returns a {@link AuthenticatedURL.Token} to be    * used by subsequent requests.    *    * @param url the URl to authenticate against.    * @param token the authentication token being used for the user.    *    * @throws IOException if an IO error occurred.    * @throws AuthenticationException if an authentication error occurred.    */
DECL|method|authenticate (URL url, AuthenticatedURL.Token token)
specifier|public
name|void
name|authenticate
parameter_list|(
name|URL
name|url
parameter_list|,
name|AuthenticatedURL
operator|.
name|Token
name|token
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
function_decl|;
block|}
end_interface

end_unit

