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

begin_comment
comment|/**  * This class defines constants used for HTTP protocol entities (such as  * headers, methods and their values).  */
end_comment

begin_class
DECL|class|HttpConstants
specifier|public
specifier|final
class|class
name|HttpConstants
block|{
comment|/**    * This class defines the HTTP protocol constants. Hence it is not intended    * to be instantiated.    */
DECL|method|HttpConstants ()
specifier|private
name|HttpConstants
parameter_list|()
block|{   }
comment|/**    * HTTP header used by the server endpoint during an authentication sequence.    */
DECL|field|WWW_AUTHENTICATE_HEADER
specifier|public
specifier|static
specifier|final
name|String
name|WWW_AUTHENTICATE_HEADER
init|=
literal|"WWW-Authenticate"
decl_stmt|;
comment|/**    * HTTP header used by the client endpoint during an authentication sequence.    */
DECL|field|AUTHORIZATION_HEADER
specifier|public
specifier|static
specifier|final
name|String
name|AUTHORIZATION_HEADER
init|=
literal|"Authorization"
decl_stmt|;
comment|/**    * HTTP header prefix used by the SPNEGO client/server endpoints during an    * authentication sequence.    */
DECL|field|NEGOTIATE
specifier|public
specifier|static
specifier|final
name|String
name|NEGOTIATE
init|=
literal|"Negotiate"
decl_stmt|;
comment|/**    * HTTP header prefix used during the Basic authentication sequence.    */
DECL|field|BASIC
specifier|public
specifier|static
specifier|final
name|String
name|BASIC
init|=
literal|"Basic"
decl_stmt|;
comment|/**    * HTTP header prefix used during the Basic authentication sequence.    */
DECL|field|DIGEST
specifier|public
specifier|static
specifier|final
name|String
name|DIGEST
init|=
literal|"Digest"
decl_stmt|;
block|}
end_class

end_unit

