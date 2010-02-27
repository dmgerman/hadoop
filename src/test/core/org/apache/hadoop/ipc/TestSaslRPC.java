begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|SecretManager
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
name|TokenIdentifier
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
name|TokenInfo
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
name|TokenSelector
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
name|SecretManager
operator|.
name|InvalidToken
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
name|SaslInputStream
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
name|SaslRpcClient
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
name|SaslRpcServer
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
name|UserGroupInformation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/** Unit tests for using Sasl over RPC. */
end_comment

begin_class
DECL|class|TestSaslRPC
specifier|public
class|class
name|TestSaslRPC
block|{
DECL|field|ADDRESS
specifier|private
specifier|static
specifier|final
name|String
name|ADDRESS
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestSaslRPC
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ERROR_MESSAGE
specifier|static
specifier|final
name|String
name|ERROR_MESSAGE
init|=
literal|"Token is invalid"
decl_stmt|;
DECL|field|SERVER_PRINCIPAL_KEY
specifier|static
specifier|final
name|String
name|SERVER_PRINCIPAL_KEY
init|=
literal|"test.ipc.server.principal"
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
static|static
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
static|static
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|Client
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|Server
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|SaslRpcClient
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|SaslRpcServer
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|SaslInputStream
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
DECL|class|TestTokenIdentifier
specifier|public
specifier|static
class|class
name|TestTokenIdentifier
extends|extends
name|TokenIdentifier
block|{
DECL|field|tokenid
specifier|private
name|Text
name|tokenid
decl_stmt|;
DECL|field|realUser
specifier|private
name|Text
name|realUser
decl_stmt|;
DECL|field|KIND_NAME
specifier|final
specifier|static
name|Text
name|KIND_NAME
init|=
operator|new
name|Text
argument_list|(
literal|"test.token"
argument_list|)
decl_stmt|;
DECL|method|TestTokenIdentifier ()
specifier|public
name|TestTokenIdentifier
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|Text
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|TestTokenIdentifier (Text tokenid)
specifier|public
name|TestTokenIdentifier
parameter_list|(
name|Text
name|tokenid
parameter_list|)
block|{
name|this
argument_list|(
name|tokenid
argument_list|,
operator|new
name|Text
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|TestTokenIdentifier (Text tokenid, Text realUser)
specifier|public
name|TestTokenIdentifier
parameter_list|(
name|Text
name|tokenid
parameter_list|,
name|Text
name|realUser
parameter_list|)
block|{
name|this
operator|.
name|tokenid
operator|=
name|tokenid
operator|==
literal|null
condition|?
operator|new
name|Text
argument_list|()
else|:
name|tokenid
expr_stmt|;
name|this
operator|.
name|realUser
operator|=
name|realUser
operator|==
literal|null
condition|?
operator|new
name|Text
argument_list|()
else|:
name|realUser
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
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|UserGroupInformation
name|getUser
parameter_list|()
block|{
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|realUser
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|tokenid
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
name|UserGroupInformation
name|realUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|realUser
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
name|tokenid
operator|.
name|toString
argument_list|()
argument_list|,
name|realUgi
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|tokenid
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|realUser
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|tokenid
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|realUser
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TestTokenSecretManager
specifier|public
specifier|static
class|class
name|TestTokenSecretManager
extends|extends
name|SecretManager
argument_list|<
name|TestTokenIdentifier
argument_list|>
block|{
DECL|method|createPassword (TestTokenIdentifier id)
specifier|public
name|byte
index|[]
name|createPassword
parameter_list|(
name|TestTokenIdentifier
name|id
parameter_list|)
block|{
return|return
name|id
operator|.
name|getBytes
argument_list|()
return|;
block|}
DECL|method|retrievePassword (TestTokenIdentifier id)
specifier|public
name|byte
index|[]
name|retrievePassword
parameter_list|(
name|TestTokenIdentifier
name|id
parameter_list|)
throws|throws
name|InvalidToken
block|{
return|return
name|id
operator|.
name|getBytes
argument_list|()
return|;
block|}
DECL|method|createIdentifier ()
specifier|public
name|TestTokenIdentifier
name|createIdentifier
parameter_list|()
block|{
return|return
operator|new
name|TestTokenIdentifier
argument_list|()
return|;
block|}
block|}
DECL|class|BadTokenSecretManager
specifier|public
specifier|static
class|class
name|BadTokenSecretManager
extends|extends
name|TestTokenSecretManager
block|{
DECL|method|retrievePassword (TestTokenIdentifier id)
specifier|public
name|byte
index|[]
name|retrievePassword
parameter_list|(
name|TestTokenIdentifier
name|id
parameter_list|)
throws|throws
name|InvalidToken
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
name|ERROR_MESSAGE
argument_list|)
throw|;
block|}
block|}
DECL|class|TestTokenSelector
specifier|public
specifier|static
class|class
name|TestTokenSelector
implements|implements
name|TokenSelector
argument_list|<
name|TestTokenIdentifier
argument_list|>
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|selectToken (Text service, Collection<Token<? extends TokenIdentifier>> tokens)
specifier|public
name|Token
argument_list|<
name|TestTokenIdentifier
argument_list|>
name|selectToken
parameter_list|(
name|Text
name|service
parameter_list|,
name|Collection
argument_list|<
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|tokens
parameter_list|)
block|{
if|if
condition|(
name|service
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
for|for
control|(
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|token
range|:
name|tokens
control|)
block|{
if|if
condition|(
name|TestTokenIdentifier
operator|.
name|KIND_NAME
operator|.
name|equals
argument_list|(
name|token
operator|.
name|getKind
argument_list|()
argument_list|)
operator|&&
name|service
operator|.
name|equals
argument_list|(
name|token
operator|.
name|getService
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|(
name|Token
argument_list|<
name|TestTokenIdentifier
argument_list|>
operator|)
name|token
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|KerberosInfo
argument_list|(
name|SERVER_PRINCIPAL_KEY
argument_list|)
annotation|@
name|TokenInfo
argument_list|(
name|TestTokenSelector
operator|.
name|class
argument_list|)
DECL|interface|TestSaslProtocol
specifier|public
interface|interface
name|TestSaslProtocol
extends|extends
name|TestRPC
operator|.
name|TestProtocol
block|{   }
DECL|class|TestSaslImpl
specifier|public
specifier|static
class|class
name|TestSaslImpl
extends|extends
name|TestRPC
operator|.
name|TestImpl
implements|implements
name|TestSaslProtocol
block|{   }
annotation|@
name|Test
DECL|method|testDigestRpc ()
specifier|public
name|void
name|testDigestRpc
parameter_list|()
throws|throws
name|Exception
block|{
name|TestTokenSecretManager
name|sm
init|=
operator|new
name|TestTokenSecretManager
argument_list|()
decl_stmt|;
specifier|final
name|Server
name|server
init|=
name|RPC
operator|.
name|getServer
argument_list|(
name|TestSaslProtocol
operator|.
name|class
argument_list|,
operator|new
name|TestSaslImpl
argument_list|()
argument_list|,
name|ADDRESS
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|,
literal|true
argument_list|,
name|conf
argument_list|,
name|sm
argument_list|)
decl_stmt|;
name|doDigestRpc
argument_list|(
name|server
argument_list|,
name|sm
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSecureToInsecureRpc ()
specifier|public
name|void
name|testSecureToInsecureRpc
parameter_list|()
throws|throws
name|Exception
block|{
name|Server
name|server
init|=
name|RPC
operator|.
name|getServer
argument_list|(
name|TestSaslProtocol
operator|.
name|class
argument_list|,
operator|new
name|TestSaslImpl
argument_list|()
argument_list|,
name|ADDRESS
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|,
literal|true
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|server
operator|.
name|disableSecurity
argument_list|()
expr_stmt|;
name|TestTokenSecretManager
name|sm
init|=
operator|new
name|TestTokenSecretManager
argument_list|()
decl_stmt|;
name|doDigestRpc
argument_list|(
name|server
argument_list|,
name|sm
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testErrorMessage ()
specifier|public
name|void
name|testErrorMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|BadTokenSecretManager
name|sm
init|=
operator|new
name|BadTokenSecretManager
argument_list|()
decl_stmt|;
specifier|final
name|Server
name|server
init|=
name|RPC
operator|.
name|getServer
argument_list|(
name|TestSaslProtocol
operator|.
name|class
argument_list|,
operator|new
name|TestSaslImpl
argument_list|()
argument_list|,
name|ADDRESS
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|,
literal|true
argument_list|,
name|conf
argument_list|,
name|sm
argument_list|)
decl_stmt|;
name|boolean
name|succeeded
init|=
literal|false
decl_stmt|;
try|try
block|{
name|doDigestRpc
argument_list|(
name|server
argument_list|,
name|sm
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LOGGING MESSAGE: "
operator|+
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ERROR_MESSAGE
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|unwrapRemoteException
argument_list|()
operator|instanceof
name|InvalidToken
argument_list|)
expr_stmt|;
name|succeeded
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|succeeded
argument_list|)
expr_stmt|;
block|}
DECL|method|doDigestRpc (Server server, TestTokenSecretManager sm)
specifier|private
name|void
name|doDigestRpc
parameter_list|(
name|Server
name|server
parameter_list|,
name|TestTokenSecretManager
name|sm
parameter_list|)
throws|throws
name|Exception
block|{
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|UserGroupInformation
name|current
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
specifier|final
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|TestTokenIdentifier
name|tokenId
init|=
operator|new
name|TestTokenIdentifier
argument_list|(
operator|new
name|Text
argument_list|(
name|current
operator|.
name|getUserName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|TestTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|TestTokenIdentifier
argument_list|>
argument_list|(
name|tokenId
argument_list|,
name|sm
argument_list|)
decl_stmt|;
name|Text
name|host
init|=
operator|new
name|Text
argument_list|(
name|addr
operator|.
name|getAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
operator|+
literal|":"
operator|+
name|addr
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
name|token
operator|.
name|setService
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Service IP address for token is "
operator|+
name|host
argument_list|)
expr_stmt|;
name|current
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|TestSaslProtocol
name|proxy
init|=
literal|null
decl_stmt|;
try|try
block|{
name|proxy
operator|=
operator|(
name|TestSaslProtocol
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|TestSaslProtocol
operator|.
name|class
argument_list|,
name|TestSaslProtocol
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|ping
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|proxy
operator|!=
literal|null
condition|)
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testKerberosRpc (String principal, String keytab)
specifier|static
name|void
name|testKerberosRpc
parameter_list|(
name|String
name|principal
parameter_list|,
name|String
name|keytab
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|newConf
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|newConf
operator|.
name|set
argument_list|(
name|SERVER_PRINCIPAL_KEY
argument_list|,
name|principal
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|loginUserFromKeytab
argument_list|(
name|principal
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|current
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"UGI: "
operator|+
name|current
argument_list|)
expr_stmt|;
name|Server
name|server
init|=
name|RPC
operator|.
name|getServer
argument_list|(
name|TestSaslProtocol
operator|.
name|class
argument_list|,
operator|new
name|TestSaslImpl
argument_list|()
argument_list|,
name|ADDRESS
argument_list|,
literal|0
argument_list|,
literal|5
argument_list|,
literal|true
argument_list|,
name|newConf
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|TestSaslProtocol
name|proxy
init|=
literal|null
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
decl_stmt|;
try|try
block|{
name|proxy
operator|=
operator|(
name|TestSaslProtocol
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|TestSaslProtocol
operator|.
name|class
argument_list|,
name|TestSaslProtocol
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|newConf
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|ping
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|proxy
operator|!=
literal|null
condition|)
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing Kerberos authentication over RPC"
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: java<options> org.apache.hadoop.ipc.TestSaslRPC "
operator|+
literal|"<serverPrincipal><keytabFile>"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|String
name|principal
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
name|String
name|keytab
init|=
name|args
index|[
literal|1
index|]
decl_stmt|;
name|testKerberosRpc
argument_list|(
name|principal
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

