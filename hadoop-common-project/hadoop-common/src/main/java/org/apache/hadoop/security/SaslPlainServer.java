begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Provider
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|callback
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|sasl
operator|.
name|AuthorizeCallback
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|sasl
operator|.
name|Sasl
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|sasl
operator|.
name|SaslException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|sasl
operator|.
name|SaslServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|sasl
operator|.
name|SaslServerFactory
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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|SaslPlainServer
specifier|public
class|class
name|SaslPlainServer
implements|implements
name|SaslServer
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|SecurityProvider
specifier|public
specifier|static
class|class
name|SecurityProvider
extends|extends
name|Provider
block|{
DECL|method|SecurityProvider ()
specifier|public
name|SecurityProvider
parameter_list|()
block|{
name|super
argument_list|(
literal|"SaslPlainServer"
argument_list|,
literal|1.0
argument_list|,
literal|"SASL PLAIN Authentication Server"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"SaslServerFactory.PLAIN"
argument_list|,
name|SaslPlainServerFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SaslPlainServerFactory
specifier|public
specifier|static
class|class
name|SaslPlainServerFactory
implements|implements
name|SaslServerFactory
block|{
annotation|@
name|Override
DECL|method|createSaslServer (String mechanism, String protocol, String serverName, Map<String,?> props, CallbackHandler cbh)
specifier|public
name|SaslServer
name|createSaslServer
parameter_list|(
name|String
name|mechanism
parameter_list|,
name|String
name|protocol
parameter_list|,
name|String
name|serverName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|props
parameter_list|,
name|CallbackHandler
name|cbh
parameter_list|)
throws|throws
name|SaslException
block|{
return|return
literal|"PLAIN"
operator|.
name|equals
argument_list|(
name|mechanism
argument_list|)
condition|?
operator|new
name|SaslPlainServer
argument_list|(
name|cbh
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getMechanismNames (Map<String,?> props)
specifier|public
name|String
index|[]
name|getMechanismNames
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|props
parameter_list|)
block|{
return|return
operator|(
name|props
operator|==
literal|null
operator|)
operator|||
literal|"false"
operator|.
name|equals
argument_list|(
name|props
operator|.
name|get
argument_list|(
name|Sasl
operator|.
name|POLICY_NOPLAINTEXT
argument_list|)
argument_list|)
condition|?
operator|new
name|String
index|[]
block|{
literal|"PLAIN"
block|}
else|:
operator|new
name|String
index|[
literal|0
index|]
return|;
block|}
block|}
DECL|field|cbh
specifier|private
name|CallbackHandler
name|cbh
decl_stmt|;
DECL|field|completed
specifier|private
name|boolean
name|completed
decl_stmt|;
DECL|field|authz
specifier|private
name|String
name|authz
decl_stmt|;
DECL|method|SaslPlainServer (CallbackHandler callback)
name|SaslPlainServer
parameter_list|(
name|CallbackHandler
name|callback
parameter_list|)
block|{
name|this
operator|.
name|cbh
operator|=
name|callback
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMechanismName ()
specifier|public
name|String
name|getMechanismName
parameter_list|()
block|{
return|return
literal|"PLAIN"
return|;
block|}
annotation|@
name|Override
DECL|method|evaluateResponse (byte[] response)
specifier|public
name|byte
index|[]
name|evaluateResponse
parameter_list|(
name|byte
index|[]
name|response
parameter_list|)
throws|throws
name|SaslException
block|{
if|if
condition|(
name|completed
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"PLAIN authentication has completed"
argument_list|)
throw|;
block|}
if|if
condition|(
name|response
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Received null response"
argument_list|)
throw|;
block|}
try|try
block|{
name|String
name|payload
decl_stmt|;
try|try
block|{
name|payload
operator|=
operator|new
name|String
argument_list|(
name|response
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Received corrupt response"
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// [ authz, authn, password ]
name|String
index|[]
name|parts
init|=
name|payload
operator|.
name|split
argument_list|(
literal|"\u0000"
argument_list|,
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|!=
literal|3
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Received corrupt response"
argument_list|)
throw|;
block|}
if|if
condition|(
name|parts
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// authz = authn
name|parts
index|[
literal|0
index|]
operator|=
name|parts
index|[
literal|1
index|]
expr_stmt|;
block|}
name|NameCallback
name|nc
init|=
operator|new
name|NameCallback
argument_list|(
literal|"SASL PLAIN"
argument_list|)
decl_stmt|;
name|nc
operator|.
name|setName
argument_list|(
name|parts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|PasswordCallback
name|pc
init|=
operator|new
name|PasswordCallback
argument_list|(
literal|"SASL PLAIN"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|pc
operator|.
name|setPassword
argument_list|(
name|parts
index|[
literal|2
index|]
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|AuthorizeCallback
name|ac
init|=
operator|new
name|AuthorizeCallback
argument_list|(
name|parts
index|[
literal|1
index|]
argument_list|,
name|parts
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|cbh
operator|.
name|handle
argument_list|(
operator|new
name|Callback
index|[]
block|{
name|nc
block|,
name|pc
block|,
name|ac
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|ac
operator|.
name|isAuthorized
argument_list|()
condition|)
block|{
name|authz
operator|=
name|ac
operator|.
name|getAuthorizedID
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SaslException
argument_list|(
literal|"PLAIN auth failed: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|completed
operator|=
literal|true
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|throwIfNotComplete ()
specifier|private
name|void
name|throwIfNotComplete
parameter_list|()
block|{
if|if
condition|(
operator|!
name|completed
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"PLAIN authentication not completed"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|isComplete ()
specifier|public
name|boolean
name|isComplete
parameter_list|()
block|{
return|return
name|completed
return|;
block|}
annotation|@
name|Override
DECL|method|getAuthorizationID ()
specifier|public
name|String
name|getAuthorizationID
parameter_list|()
block|{
name|throwIfNotComplete
argument_list|()
expr_stmt|;
return|return
name|authz
return|;
block|}
annotation|@
name|Override
DECL|method|getNegotiatedProperty (String propName)
specifier|public
name|Object
name|getNegotiatedProperty
parameter_list|(
name|String
name|propName
parameter_list|)
block|{
name|throwIfNotComplete
argument_list|()
expr_stmt|;
return|return
name|Sasl
operator|.
name|QOP
operator|.
name|equals
argument_list|(
name|propName
argument_list|)
condition|?
literal|"auth"
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|wrap (byte[] outgoing, int offset, int len)
specifier|public
name|byte
index|[]
name|wrap
parameter_list|(
name|byte
index|[]
name|outgoing
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|SaslException
block|{
name|throwIfNotComplete
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"PLAIN supports neither integrity nor privacy"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|unwrap (byte[] incoming, int offset, int len)
specifier|public
name|byte
index|[]
name|unwrap
parameter_list|(
name|byte
index|[]
name|incoming
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|SaslException
block|{
name|throwIfNotComplete
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"PLAIN supports neither integrity nor privacy"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|dispose ()
specifier|public
name|void
name|dispose
parameter_list|()
throws|throws
name|SaslException
block|{
name|cbh
operator|=
literal|null
expr_stmt|;
name|authz
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

