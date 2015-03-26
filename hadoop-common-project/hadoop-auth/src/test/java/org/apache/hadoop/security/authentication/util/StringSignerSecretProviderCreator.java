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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
comment|/**  * Helper class for creating StringSignerSecretProviders in unit tests  */
end_comment

begin_class
annotation|@
name|InterfaceStability
operator|.
name|Unstable
annotation|@
name|VisibleForTesting
DECL|class|StringSignerSecretProviderCreator
specifier|public
class|class
name|StringSignerSecretProviderCreator
block|{
comment|/**    * @return a new StringSignerSecretProvider    * @throws Exception    */
DECL|method|newStringSignerSecretProvider ()
specifier|public
specifier|static
name|StringSignerSecretProvider
name|newStringSignerSecretProvider
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|StringSignerSecretProvider
argument_list|()
return|;
block|}
block|}
end_class

end_unit

