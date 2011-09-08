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

begin_comment
comment|/**  * Exception thrown by {@link Signer} when a string signature is invalid.  */
end_comment

begin_class
DECL|class|SignerException
specifier|public
class|class
name|SignerException
extends|extends
name|Exception
block|{
DECL|field|serialVersionUID
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0
decl_stmt|;
comment|/**    * Creates an exception instance.    *    * @param msg message for the exception.    */
DECL|method|SignerException (String msg)
specifier|public
name|SignerException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

