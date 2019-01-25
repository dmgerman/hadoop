begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|protocol
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
name|io
operator|.
name|Text
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
name|retry
operator|.
name|Idempotent
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
name|ozone
operator|.
name|om
operator|.
name|OMConfigKeys
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
name|ozone
operator|.
name|security
operator|.
name|OzoneTokenIdentifier
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
name|KerberosInfo
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|exceptions
operator|.
name|OMException
import|;
end_import

begin_comment
comment|/**  * Security protocol for a secure OzoneManager.  */
end_comment

begin_interface
annotation|@
name|KerberosInfo
argument_list|(
name|serverPrincipal
operator|=
name|OMConfigKeys
operator|.
name|OZONE_OM_KERBEROS_PRINCIPAL_KEY
argument_list|)
DECL|interface|OzoneManagerSecurityProtocol
specifier|public
interface|interface
name|OzoneManagerSecurityProtocol
block|{
comment|/**    * Get a valid Delegation Token.    *    * @param renewer the designated renewer for the token    * @return Token<OzoneDelegationTokenSelector>    * @throws OMException    */
annotation|@
name|Idempotent
DECL|method|getDelegationToken (Text renewer)
name|Token
argument_list|<
name|OzoneTokenIdentifier
argument_list|>
name|getDelegationToken
parameter_list|(
name|Text
name|renewer
parameter_list|)
throws|throws
name|OMException
function_decl|;
comment|/**    * Renew an existing delegation token.    *    * @param token delegation token obtained earlier    * @return the new expiration time    * @throws OMException    */
annotation|@
name|Idempotent
DECL|method|renewDelegationToken (Token<OzoneTokenIdentifier> token)
name|long
name|renewDelegationToken
parameter_list|(
name|Token
argument_list|<
name|OzoneTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|OMException
function_decl|;
comment|/**    * Cancel an existing delegation token.    *    * @param token delegation token    * @throws OMException    */
annotation|@
name|Idempotent
DECL|method|cancelDelegationToken (Token<OzoneTokenIdentifier> token)
name|void
name|cancelDelegationToken
parameter_list|(
name|Token
argument_list|<
name|OzoneTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|OMException
function_decl|;
block|}
end_interface

end_unit

