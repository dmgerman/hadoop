begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|service
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
name|UserGroupInformation
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
name|Token
import|;
end_import

begin_comment
comment|/**  * Service interface to manage HttpFS delegation tokens.  */
end_comment

begin_interface
DECL|interface|DelegationTokenManager
specifier|public
interface|interface
name|DelegationTokenManager
block|{
comment|/**    * Creates a delegation token.    *    * @param ugi UGI creating the token.    * @param renewer token renewer.    * @return new delegation token.    * @throws DelegationTokenManagerException thrown if the token could not be    * created.    */
DECL|method|createToken (UserGroupInformation ugi, String renewer)
specifier|public
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|createToken
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|,
name|String
name|renewer
parameter_list|)
throws|throws
name|DelegationTokenManagerException
function_decl|;
comment|/**    * Renews a delegation token.    *    * @param token delegation token to renew.    * @param renewer token renewer.    * @return epoc expiration time.    * @throws DelegationTokenManagerException thrown if the token could not be    * renewed.    */
DECL|method|renewToken (Token<DelegationTokenIdentifier> token, String renewer)
specifier|public
name|long
name|renewToken
parameter_list|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
parameter_list|,
name|String
name|renewer
parameter_list|)
throws|throws
name|DelegationTokenManagerException
function_decl|;
comment|/**    * Cancels a delegation token.    *    * @param token delegation token to cancel.    * @param canceler token canceler.    * @throws DelegationTokenManagerException thrown if the token could not be    * canceled.    */
DECL|method|cancelToken (Token<DelegationTokenIdentifier> token, String canceler)
specifier|public
name|void
name|cancelToken
parameter_list|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
parameter_list|,
name|String
name|canceler
parameter_list|)
throws|throws
name|DelegationTokenManagerException
function_decl|;
comment|/**    * Verifies a delegation token.    *    * @param token delegation token to verify.    * @return the UGI for the token.    * @throws DelegationTokenManagerException thrown if the token could not be    * verified.    */
DECL|method|verifyToken (Token<DelegationTokenIdentifier> token)
specifier|public
name|UserGroupInformation
name|verifyToken
parameter_list|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|DelegationTokenManagerException
function_decl|;
block|}
end_interface

end_unit

