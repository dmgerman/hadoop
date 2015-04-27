begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authentication.util
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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
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

begin_comment
comment|/**  * The SignerSecretProvider is an abstract way to provide a secret to be used  * by the Signer so that we can have different implementations that potentially  * do more complicated things in the backend.  * See the RolloverSignerSecretProvider class for an implementation that  * supports rolling over the secret at a regular interval.  */
end_comment

begin_class
annotation|@
name|InterfaceStability
operator|.
name|Unstable
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|SignerSecretProvider
specifier|public
specifier|abstract
class|class
name|SignerSecretProvider
block|{
comment|/**    * Initialize the SignerSecretProvider    * @param config configuration properties    * @param servletContext servlet context    * @param tokenValidity The amount of time a token is valid for    * @throws Exception thrown if an error occurred    */
DECL|method|init (Properties config, ServletContext servletContext, long tokenValidity)
specifier|public
specifier|abstract
name|void
name|init
parameter_list|(
name|Properties
name|config
parameter_list|,
name|ServletContext
name|servletContext
parameter_list|,
name|long
name|tokenValidity
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Will be called on shutdown; subclasses should perform any cleanup here.    */
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{}
comment|/**    * Returns the current secret to be used by the Signer for signing new    * cookies.  This should never return null.    *<p>    * Callers should be careful not to modify the returned value.    * @return the current secret    */
DECL|method|getCurrentSecret ()
specifier|public
specifier|abstract
name|byte
index|[]
name|getCurrentSecret
parameter_list|()
function_decl|;
comment|/**    * Returns all secrets that a cookie could have been signed with and are still    * valid; this should include the secret returned by getCurrentSecret().    *<p>    * Callers should be careful not to modify the returned value.    * @return the secrets    */
DECL|method|getAllSecrets ()
specifier|public
specifier|abstract
name|byte
index|[]
index|[]
name|getAllSecrets
parameter_list|()
function_decl|;
block|}
end_class

end_unit

