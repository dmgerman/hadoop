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
name|java
operator|.
name|util
operator|.
name|Random
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
comment|/**  * A SignerSecretProvider that uses a random number as its secret.  It rolls  * the secret at a regular interval.  */
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
DECL|class|RandomSignerSecretProvider
specifier|public
class|class
name|RandomSignerSecretProvider
extends|extends
name|RolloverSignerSecretProvider
block|{
DECL|field|rand
specifier|private
specifier|final
name|Random
name|rand
decl_stmt|;
DECL|method|RandomSignerSecretProvider ()
specifier|public
name|RandomSignerSecretProvider
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|rand
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
block|}
comment|/**    * This constructor lets you set the seed of the Random Number Generator and    * is meant for testing.    * @param seed the seed for the random number generator    */
annotation|@
name|VisibleForTesting
DECL|method|RandomSignerSecretProvider (long seed)
specifier|public
name|RandomSignerSecretProvider
parameter_list|(
name|long
name|seed
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|rand
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|generateNewSecret ()
specifier|protected
name|byte
index|[]
name|generateNewSecret
parameter_list|()
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|)
operator|.
name|getBytes
argument_list|()
return|;
block|}
block|}
end_class

end_unit

