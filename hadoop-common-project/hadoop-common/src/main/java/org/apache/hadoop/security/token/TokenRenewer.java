begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.token
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|token
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  * This is the interface for plugins that handle tokens.  */
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
DECL|class|TokenRenewer
specifier|public
specifier|abstract
class|class
name|TokenRenewer
block|{
comment|/**    * Does this renewer handle this kind of token?    * @param kind the kind of the token    * @return true if this renewer can renew it    */
DECL|method|handleKind (Text kind)
specifier|public
specifier|abstract
name|boolean
name|handleKind
parameter_list|(
name|Text
name|kind
parameter_list|)
function_decl|;
comment|/**    * Is the given token managed? Only managed tokens may be renewed or    * cancelled.    * @param token the token being checked    * @return true if the token may be renewed or cancelled    * @throws IOException    */
DECL|method|isManaged (Token<?> token)
specifier|public
specifier|abstract
name|boolean
name|isManaged
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Renew the given token.    * @return the new expiration time    * @throws IOException    * @throws InterruptedException     */
DECL|method|renew (Token<?> token, Configuration conf )
specifier|public
specifier|abstract
name|long
name|renew
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Cancel the given token    * @throws IOException    * @throws InterruptedException     */
DECL|method|cancel (Token<?> token, Configuration conf )
specifier|public
specifier|abstract
name|void
name|cancel
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
block|}
end_class

end_unit

