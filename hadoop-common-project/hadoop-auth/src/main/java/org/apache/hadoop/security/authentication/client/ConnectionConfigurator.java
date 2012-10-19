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
name|HttpURLConnection
import|;
end_import

begin_comment
comment|/**  * Interface to configure  {@link HttpURLConnection} created by  * {@link AuthenticatedURL} instances.  */
end_comment

begin_interface
DECL|interface|ConnectionConfigurator
specifier|public
interface|interface
name|ConnectionConfigurator
block|{
comment|/**    * Configures the given {@link HttpURLConnection} instance.    *    * @param conn the {@link HttpURLConnection} instance to configure.    * @return the configured {@link HttpURLConnection} instance.    *     * @throws IOException if an IO error occurred.    */
DECL|method|configure (HttpURLConnection conn)
specifier|public
name|HttpURLConnection
name|configure
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

