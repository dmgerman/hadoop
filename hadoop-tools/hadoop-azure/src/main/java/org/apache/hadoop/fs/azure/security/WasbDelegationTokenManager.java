begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
operator|.
name|security
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|web
operator|.
name|DelegationTokenIdentifier
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

begin_comment
comment|/**  * Interface for Managing the Delegation tokens.  */
end_comment

begin_interface
DECL|interface|WasbDelegationTokenManager
specifier|public
interface|interface
name|WasbDelegationTokenManager
block|{
comment|/**    * Get Delegation token    * @param renewer delegation token renewer    * @return delegation token    * @throws IOException when error in getting the delegation token    */
DECL|method|getDelegationToken (String renewer)
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|getDelegationToken
parameter_list|(
name|String
name|renewer
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Renew the delegation token    * @param token delegation token.    * @return renewed time.    * @throws IOException when error in renewing the delegation token    */
DECL|method|renewDelegationToken (Token<?> token)
name|long
name|renewDelegationToken
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
comment|/**    * Cancel the delegation token    * @param token delegation token.    * @throws IOException when error in cancelling the delegation token.    */
DECL|method|cancelDelegationToken (Token<?> token)
name|void
name|cancelDelegationToken
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
block|}
end_interface

end_unit

