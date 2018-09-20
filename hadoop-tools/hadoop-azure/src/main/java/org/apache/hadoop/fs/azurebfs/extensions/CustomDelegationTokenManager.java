begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.extensions
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|extensions
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
comment|/**  * Interface for Managing the Delegation tokens.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
literal|"authorization-subsystems"
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|CustomDelegationTokenManager
specifier|public
interface|interface
name|CustomDelegationTokenManager
block|{
comment|/**    * Initialize with supported configuration. This method is invoked when the    * (URI, Configuration)} method is invoked.    *    * @param configuration Configuration object    * @throws IOException if instance can not be configured.    */
DECL|method|initialize (Configuration configuration)
name|void
name|initialize
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get Delegation token.    * @param renewer delegation token renewer    * @return delegation token    * @throws IOException when error in getting the delegation token    */
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
comment|/**    * Renew the delegation token.    * @param token delegation token.    * @return renewed time.    * @throws IOException when error in renewing the delegation token    */
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
comment|/**    * Cancel the delegation token.    * @param token delegation token.    * @throws IOException when error in cancelling the delegation token.    */
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

