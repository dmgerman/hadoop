begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authentication.server
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
name|server
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/**  * Interface to support multiple authentication mechanisms simultaneously.  *  */
end_comment

begin_interface
DECL|interface|CompositeAuthenticationHandler
specifier|public
interface|interface
name|CompositeAuthenticationHandler
extends|extends
name|AuthenticationHandler
block|{
comment|/**    * This method returns the token types supported by this authentication    * handler.    *    * @return the token types supported by this authentication handler.    */
DECL|method|getTokenTypes ()
name|Collection
argument_list|<
name|String
argument_list|>
name|getTokenTypes
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

