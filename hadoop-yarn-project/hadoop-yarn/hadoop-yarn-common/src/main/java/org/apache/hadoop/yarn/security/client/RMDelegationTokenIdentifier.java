begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.security.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|security
operator|.
name|client
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
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
operator|.
name|Private
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
operator|.
name|Public
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
operator|.
name|Evolving
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
operator|.
name|RPC
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
name|net
operator|.
name|NetUtils
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
name|SecurityUtil
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
name|security
operator|.
name|token
operator|.
name|TokenRenewer
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
name|AbstractDelegationTokenIdentifier
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
name|AbstractDelegationTokenSecretManager
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
name|yarn
operator|.
name|api
operator|.
name|ApplicationClientProtocol
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|CancelDelegationTokenRequest
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|RenewDelegationTokenRequest
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
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
name|yarn
operator|.
name|ipc
operator|.
name|YarnRPC
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
name|yarn
operator|.
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  * Delegation Token Identifier that identifies the delegation tokens from the   * Resource Manager.   */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|RMDelegationTokenIdentifier
specifier|public
class|class
name|RMDelegationTokenIdentifier
extends|extends
name|AbstractDelegationTokenIdentifier
block|{
DECL|field|KIND_NAME
specifier|public
specifier|static
specifier|final
name|Text
name|KIND_NAME
init|=
operator|new
name|Text
argument_list|(
literal|"RM_DELEGATION_TOKEN"
argument_list|)
decl_stmt|;
DECL|method|RMDelegationTokenIdentifier ()
specifier|public
name|RMDelegationTokenIdentifier
parameter_list|()
block|{   }
comment|/**    * Create a new delegation token identifier    * @param owner the effective username of the token owner    * @param renewer the username of the renewer    * @param realUser the real username of the token owner    */
DECL|method|RMDelegationTokenIdentifier (Text owner, Text renewer, Text realUser)
specifier|public
name|RMDelegationTokenIdentifier
parameter_list|(
name|Text
name|owner
parameter_list|,
name|Text
name|renewer
parameter_list|,
name|Text
name|realUser
parameter_list|)
block|{
name|super
argument_list|(
name|owner
argument_list|,
name|renewer
argument_list|,
name|realUser
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getKind ()
specifier|public
name|Text
name|getKind
parameter_list|()
block|{
return|return
name|KIND_NAME
return|;
block|}
DECL|class|Renewer
specifier|public
specifier|static
class|class
name|Renewer
extends|extends
name|TokenRenewer
block|{
annotation|@
name|Override
DECL|method|handleKind (Text kind)
specifier|public
name|boolean
name|handleKind
parameter_list|(
name|Text
name|kind
parameter_list|)
block|{
return|return
name|KIND_NAME
operator|.
name|equals
argument_list|(
name|kind
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isManaged (Token<?> token)
specifier|public
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
block|{
return|return
literal|true
return|;
block|}
specifier|private
specifier|static
DECL|field|localSecretManager
name|AbstractDelegationTokenSecretManager
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|>
name|localSecretManager
decl_stmt|;
DECL|field|localServiceAddress
specifier|private
specifier|static
name|InetSocketAddress
name|localServiceAddress
decl_stmt|;
annotation|@
name|Private
DECL|method|setSecretManager ( AbstractDelegationTokenSecretManager<RMDelegationTokenIdentifier> secretManager, InetSocketAddress serviceAddress)
specifier|public
specifier|static
name|void
name|setSecretManager
parameter_list|(
name|AbstractDelegationTokenSecretManager
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|>
name|secretManager
parameter_list|,
name|InetSocketAddress
name|serviceAddress
parameter_list|)
block|{
name|localSecretManager
operator|=
name|secretManager
expr_stmt|;
name|localServiceAddress
operator|=
name|serviceAddress
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|renew (Token<?> token, Configuration conf)
specifier|public
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
block|{
specifier|final
name|ApplicationClientProtocol
name|rmClient
init|=
name|getRmClient
argument_list|(
name|token
argument_list|,
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|rmClient
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|RenewDelegationTokenRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|RenewDelegationTokenRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setDelegationToken
argument_list|(
name|convertToProtoToken
argument_list|(
name|token
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|rmClient
operator|.
name|renewDelegationToken
argument_list|(
name|request
argument_list|)
operator|.
name|getNextExpirationTime
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|rmClient
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
return|return
name|localSecretManager
operator|.
name|renewToken
argument_list|(
operator|(
name|Token
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|>
operator|)
name|token
argument_list|,
name|getRenewer
argument_list|(
name|token
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|cancel (Token<?> token, Configuration conf)
specifier|public
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
block|{
specifier|final
name|ApplicationClientProtocol
name|rmClient
init|=
name|getRmClient
argument_list|(
name|token
argument_list|,
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|rmClient
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|CancelDelegationTokenRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|CancelDelegationTokenRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setDelegationToken
argument_list|(
name|convertToProtoToken
argument_list|(
name|token
argument_list|)
argument_list|)
expr_stmt|;
name|rmClient
operator|.
name|cancelDelegationToken
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|rmClient
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|localSecretManager
operator|.
name|cancelToken
argument_list|(
operator|(
name|Token
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|>
operator|)
name|token
argument_list|,
name|getRenewer
argument_list|(
name|token
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getRmClient (Token<?> token, Configuration conf)
specifier|private
specifier|static
name|ApplicationClientProtocol
name|getRmClient
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
block|{
name|InetSocketAddress
name|addr
init|=
name|SecurityUtil
operator|.
name|getTokenServiceAddr
argument_list|(
name|token
argument_list|)
decl_stmt|;
if|if
condition|(
name|localSecretManager
operator|!=
literal|null
condition|)
block|{
comment|// return null if it's our token
if|if
condition|(
name|localServiceAddress
operator|.
name|getAddress
argument_list|()
operator|.
name|isAnyLocalAddress
argument_list|()
condition|)
block|{
if|if
condition|(
name|NetUtils
operator|.
name|isLocalAddress
argument_list|(
name|addr
operator|.
name|getAddress
argument_list|()
argument_list|)
operator|&&
name|addr
operator|.
name|getPort
argument_list|()
operator|==
name|localServiceAddress
operator|.
name|getPort
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|addr
operator|.
name|equals
argument_list|(
name|localServiceAddress
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|final
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
return|return
operator|(
name|ApplicationClientProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|ApplicationClientProtocol
operator|.
name|class
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|// get renewer so we can always renew our own tokens
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getRenewer (Token<?> token)
specifier|private
specifier|static
name|String
name|getRenewer
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|Token
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|>
operator|)
name|token
operator|)
operator|.
name|decodeIdentifier
argument_list|()
operator|.
name|getRenewer
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Token
DECL|method|convertToProtoToken (Token<?> token)
name|convertToProtoToken
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|)
block|{
return|return
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Token
operator|.
name|newInstance
argument_list|(
name|token
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|token
operator|.
name|getKind
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|token
operator|.
name|getPassword
argument_list|()
argument_list|,
name|token
operator|.
name|getService
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

