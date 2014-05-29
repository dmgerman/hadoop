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
name|InetAddress
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
name|net
operator|.
name|NetworkInterface
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|authorize
operator|.
name|DefaultImpersonationProvider
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
name|authorize
operator|.
name|ProxyUsers
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
name|delegation
operator|.
name|AbstractDelegationTokenSelector
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
name|TestDelegationToken
operator|.
name|TestDelegationTokenIdentifier
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
name|TestDelegationToken
operator|.
name|TestDelegationTokenSecretManager
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
name|util
operator|.
name|Time
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
name|apache
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_comment
comment|/**  * MiniRPCBenchmark measures time to establish an RPC connection   * to a secure RPC server.  * It sequentially establishes connections the specified number of times,   * and calculates the average time taken to connect.  * The time to connect includes the server side authentication time.  * The benchmark supports three authentication methods:  *<ol>  *<li>simple - no authentication. In order to enter this mode   * the configuration file<tt>core-site.xml</tt> should specify  *<tt>hadoop.security.authentication = simple</tt>.  * This is the default mode.</li>  *<li>kerberos - kerberos authentication. In order to enter this mode   * the configuration file<tt>core-site.xml</tt> should specify  *<tt>hadoop.security.authentication = kerberos</tt> and   * the argument string should provide qualifying  *<tt>keytabFile</tt> and<tt>userName</tt> parameters.  *<li>delegation token - authentication using delegation token.  * In order to enter this mode the benchmark should provide all the  * mentioned parameters for kerberos authentication plus the  *<tt>useToken</tt> argument option.  *</ol>  * Input arguments:  *<ul>  *<li>numIterations - number of connections to establish</li>  *<li>keytabFile - keytab file for kerberos authentication</li>  *<li>userName - principal name for kerberos authentication</li>  *<li>useToken - should be specified for delegation token authentication</li>  *<li>logLevel - logging level, see {@link Level}</li>  *</ul>  */
end_comment

begin_class
DECL|class|MiniRPCBenchmark
specifier|public
class|class
name|MiniRPCBenchmark
block|{
DECL|field|KEYTAB_FILE_KEY
specifier|private
specifier|static
specifier|final
name|String
name|KEYTAB_FILE_KEY
init|=
literal|"test.keytab.file"
decl_stmt|;
DECL|field|USER_NAME_KEY
specifier|private
specifier|static
specifier|final
name|String
name|USER_NAME_KEY
init|=
literal|"test.user.name"
decl_stmt|;
DECL|field|MINI_USER
specifier|private
specifier|static
specifier|final
name|String
name|MINI_USER
init|=
literal|"miniUser"
decl_stmt|;
DECL|field|RENEWER
specifier|private
specifier|static
specifier|final
name|String
name|RENEWER
init|=
literal|"renewer"
decl_stmt|;
DECL|field|GROUP_NAME_1
specifier|private
specifier|static
specifier|final
name|String
name|GROUP_NAME_1
init|=
literal|"MiniGroup1"
decl_stmt|;
DECL|field|GROUP_NAME_2
specifier|private
specifier|static
specifier|final
name|String
name|GROUP_NAME_2
init|=
literal|"MiniGroup2"
decl_stmt|;
DECL|field|GROUP_NAMES
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|GROUP_NAMES
init|=
operator|new
name|String
index|[]
block|{
name|GROUP_NAME_1
block|,
name|GROUP_NAME_2
block|}
decl_stmt|;
DECL|field|currentUgi
specifier|private
name|UserGroupInformation
name|currentUgi
decl_stmt|;
DECL|field|logLevel
specifier|private
name|Level
name|logLevel
decl_stmt|;
DECL|method|MiniRPCBenchmark (Level l)
name|MiniRPCBenchmark
parameter_list|(
name|Level
name|l
parameter_list|)
block|{
name|currentUgi
operator|=
literal|null
expr_stmt|;
name|logLevel
operator|=
name|l
expr_stmt|;
block|}
DECL|class|TestDelegationTokenSelector
specifier|public
specifier|static
class|class
name|TestDelegationTokenSelector
extends|extends
name|AbstractDelegationTokenSelector
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
block|{
DECL|method|TestDelegationTokenSelector ()
specifier|protected
name|TestDelegationTokenSelector
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|Text
argument_list|(
literal|"MY KIND"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|KerberosInfo
argument_list|(
name|serverPrincipal
operator|=
name|USER_NAME_KEY
argument_list|)
annotation|@
name|TokenInfo
argument_list|(
name|TestDelegationTokenSelector
operator|.
name|class
argument_list|)
DECL|interface|MiniProtocol
specifier|public
specifier|static
interface|interface
name|MiniProtocol
extends|extends
name|VersionedProtocol
block|{
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|1L
decl_stmt|;
comment|/**      * Get a Delegation Token.      */
DECL|method|getDelegationToken (Text renewer)
specifier|public
name|Token
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
name|getDelegationToken
parameter_list|(
name|Text
name|renewer
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * Primitive RPC server, which    * allows clients to connect to it.    */
DECL|class|MiniServer
specifier|static
class|class
name|MiniServer
implements|implements
name|MiniProtocol
block|{
DECL|field|DEFAULT_SERVER_ADDRESS
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_SERVER_ADDRESS
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|secretManager
specifier|private
name|TestDelegationTokenSecretManager
name|secretManager
decl_stmt|;
DECL|field|rpcServer
specifier|private
name|Server
name|rpcServer
decl_stmt|;
annotation|@
name|Override
comment|// VersionedProtocol
DECL|method|getProtocolVersion (String protocol, long clientVersion)
specifier|public
name|long
name|getProtocolVersion
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|protocol
operator|.
name|equals
argument_list|(
name|MiniProtocol
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
return|return
name|versionID
return|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown protocol: "
operator|+
name|protocol
argument_list|)
throw|;
block|}
annotation|@
name|Override
comment|// VersionedProtocol
DECL|method|getProtocolSignature (String protocol, long clientVersion, int clientMethodsHashCode)
specifier|public
name|ProtocolSignature
name|getProtocolSignature
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|,
name|int
name|clientMethodsHashCode
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|protocol
operator|.
name|equals
argument_list|(
name|MiniProtocol
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
return|return
operator|new
name|ProtocolSignature
argument_list|(
name|versionID
argument_list|,
literal|null
argument_list|)
return|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown protocol: "
operator|+
name|protocol
argument_list|)
throw|;
block|}
annotation|@
name|Override
comment|// MiniProtocol
DECL|method|getDelegationToken (Text renewer)
specifier|public
name|Token
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
name|getDelegationToken
parameter_list|(
name|Text
name|renewer
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|owner
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getUserName
argument_list|()
decl_stmt|;
name|String
name|realUser
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getRealUser
argument_list|()
operator|==
literal|null
condition|?
literal|""
else|:
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getRealUser
argument_list|()
operator|.
name|getUserName
argument_list|()
decl_stmt|;
name|TestDelegationTokenIdentifier
name|tokenId
init|=
operator|new
name|TestDelegationTokenIdentifier
argument_list|(
operator|new
name|Text
argument_list|(
name|owner
argument_list|)
argument_list|,
name|renewer
argument_list|,
operator|new
name|Text
argument_list|(
name|realUser
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|Token
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
argument_list|(
name|tokenId
argument_list|,
name|secretManager
argument_list|)
return|;
block|}
comment|/** Start RPC server */
DECL|method|MiniServer (Configuration conf, String user, String keytabFile)
name|MiniServer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|keytabFile
parameter_list|)
throws|throws
name|IOException
block|{
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|loginUserFromKeytab
argument_list|(
name|user
argument_list|,
name|keytabFile
argument_list|)
expr_stmt|;
name|secretManager
operator|=
operator|new
name|TestDelegationTokenSecretManager
argument_list|(
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|,
literal|7
operator|*
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|,
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|,
literal|3600000
argument_list|)
expr_stmt|;
name|secretManager
operator|.
name|startThreads
argument_list|()
expr_stmt|;
name|rpcServer
operator|=
operator|new
name|RPC
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setProtocol
argument_list|(
name|MiniProtocol
operator|.
name|class
argument_list|)
operator|.
name|setInstance
argument_list|(
name|this
argument_list|)
operator|.
name|setBindAddress
argument_list|(
name|DEFAULT_SERVER_ADDRESS
argument_list|)
operator|.
name|setPort
argument_list|(
literal|0
argument_list|)
operator|.
name|setNumHandlers
argument_list|(
literal|1
argument_list|)
operator|.
name|setVerbose
argument_list|(
literal|false
argument_list|)
operator|.
name|setSecretManager
argument_list|(
name|secretManager
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|rpcServer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/** Stop RPC server */
DECL|method|stop ()
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|rpcServer
operator|!=
literal|null
condition|)
name|rpcServer
operator|.
name|stop
argument_list|()
expr_stmt|;
name|rpcServer
operator|=
literal|null
expr_stmt|;
block|}
comment|/** Get RPC server address */
DECL|method|getAddress ()
name|InetSocketAddress
name|getAddress
parameter_list|()
block|{
if|if
condition|(
name|rpcServer
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|rpcServer
argument_list|)
return|;
block|}
block|}
DECL|method|connectToServer (Configuration conf, InetSocketAddress addr)
name|long
name|connectToServer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|)
throws|throws
name|IOException
block|{
name|MiniProtocol
name|client
init|=
literal|null
decl_stmt|;
try|try
block|{
name|long
name|start
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|client
operator|=
name|RPC
operator|.
name|getProxy
argument_list|(
name|MiniProtocol
operator|.
name|class
argument_list|,
name|MiniProtocol
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|long
name|end
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
return|return
name|end
operator|-
name|start
return|;
block|}
finally|finally
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|connectToServerAndGetDelegationToken ( final Configuration conf, final InetSocketAddress addr)
name|void
name|connectToServerAndGetDelegationToken
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|InetSocketAddress
name|addr
parameter_list|)
throws|throws
name|IOException
block|{
name|MiniProtocol
name|client
init|=
literal|null
decl_stmt|;
try|try
block|{
name|UserGroupInformation
name|current
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|proxyUserUgi
init|=
name|UserGroupInformation
operator|.
name|createProxyUserForTesting
argument_list|(
name|MINI_USER
argument_list|,
name|current
argument_list|,
name|GROUP_NAMES
argument_list|)
decl_stmt|;
try|try
block|{
name|client
operator|=
name|proxyUserUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|MiniProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|MiniProtocol
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|MiniProtocol
name|p
init|=
name|RPC
operator|.
name|getProxy
argument_list|(
name|MiniProtocol
operator|.
name|class
argument_list|,
name|MiniProtocol
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|TestDelegationTokenIdentifier
argument_list|>
name|token
decl_stmt|;
name|token
operator|=
name|p
operator|.
name|getDelegationToken
argument_list|(
operator|new
name|Text
argument_list|(
name|RENEWER
argument_list|)
argument_list|)
expr_stmt|;
name|currentUgi
operator|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|MINI_USER
argument_list|,
name|GROUP_NAMES
argument_list|)
expr_stmt|;
name|SecurityUtil
operator|.
name|setTokenService
argument_list|(
name|token
argument_list|,
name|addr
argument_list|)
expr_stmt|;
name|currentUgi
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|e
operator|.
name|getStackTrace
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|connectToServerUsingDelegationToken ( final Configuration conf, final InetSocketAddress addr)
name|long
name|connectToServerUsingDelegationToken
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|InetSocketAddress
name|addr
parameter_list|)
throws|throws
name|IOException
block|{
name|MiniProtocol
name|client
init|=
literal|null
decl_stmt|;
try|try
block|{
name|long
name|start
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
try|try
block|{
name|client
operator|=
name|currentUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|MiniProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|MiniProtocol
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|RPC
operator|.
name|getProxy
argument_list|(
name|MiniProtocol
operator|.
name|class
argument_list|,
name|MiniProtocol
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|long
name|end
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
return|return
name|end
operator|-
name|start
return|;
block|}
finally|finally
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setLoggingLevel (Level level)
specifier|static
name|void
name|setLoggingLevel
parameter_list|(
name|Level
name|level
parameter_list|)
block|{
name|LogManager
operator|.
name|getLogger
argument_list|(
name|Server
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|setLevel
argument_list|(
name|level
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|Server
operator|.
name|AUDITLOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|LogManager
operator|.
name|getLogger
argument_list|(
name|Client
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|setLevel
argument_list|(
name|level
argument_list|)
expr_stmt|;
block|}
comment|/**    * Run MiniBenchmark with MiniServer as the RPC server.    *     * @param conf - configuration    * @param count - connect this many times    * @param keytabKey - key for keytab file in the configuration    * @param userNameKey - key for user name in the configuration    * @return average time to connect    * @throws IOException    */
DECL|method|runMiniBenchmark (Configuration conf, int count, String keytabKey, String userNameKey)
name|long
name|runMiniBenchmark
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|count
parameter_list|,
name|String
name|keytabKey
parameter_list|,
name|String
name|userNameKey
parameter_list|)
throws|throws
name|IOException
block|{
comment|// get login information
name|String
name|user
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|userNameKey
operator|!=
literal|null
condition|)
name|user
operator|=
name|conf
operator|.
name|get
argument_list|(
name|userNameKey
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|String
name|keytabFile
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|keytabKey
operator|!=
literal|null
condition|)
name|keytabFile
operator|=
name|conf
operator|.
name|get
argument_list|(
name|keytabKey
argument_list|,
name|keytabFile
argument_list|)
expr_stmt|;
name|MiniServer
name|miniServer
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// start the server
name|miniServer
operator|=
operator|new
name|MiniServer
argument_list|(
name|conf
argument_list|,
name|user
argument_list|,
name|keytabFile
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
name|miniServer
operator|.
name|getAddress
argument_list|()
decl_stmt|;
name|connectToServer
argument_list|(
name|conf
argument_list|,
name|addr
argument_list|)
expr_stmt|;
comment|// connect to the server count times
name|setLoggingLevel
argument_list|(
name|logLevel
argument_list|)
expr_stmt|;
name|long
name|elapsed
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|count
condition|;
name|idx
operator|++
control|)
block|{
name|elapsed
operator|+=
name|connectToServer
argument_list|(
name|conf
argument_list|,
name|addr
argument_list|)
expr_stmt|;
block|}
return|return
name|elapsed
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|miniServer
operator|!=
literal|null
condition|)
name|miniServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Run MiniBenchmark using delegation token authentication.    *     * @param conf - configuration    * @param count - connect this many times    * @param keytabKey - key for keytab file in the configuration    * @param userNameKey - key for user name in the configuration    * @return average time to connect    * @throws IOException    */
DECL|method|runMiniBenchmarkWithDelegationToken (Configuration conf, int count, String keytabKey, String userNameKey)
name|long
name|runMiniBenchmarkWithDelegationToken
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|count
parameter_list|,
name|String
name|keytabKey
parameter_list|,
name|String
name|userNameKey
parameter_list|)
throws|throws
name|IOException
block|{
comment|// get login information
name|String
name|user
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|userNameKey
operator|!=
literal|null
condition|)
name|user
operator|=
name|conf
operator|.
name|get
argument_list|(
name|userNameKey
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|String
name|keytabFile
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|keytabKey
operator|!=
literal|null
condition|)
name|keytabFile
operator|=
name|conf
operator|.
name|get
argument_list|(
name|keytabKey
argument_list|,
name|keytabFile
argument_list|)
expr_stmt|;
name|MiniServer
name|miniServer
init|=
literal|null
decl_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|String
name|shortUserName
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|user
argument_list|)
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
try|try
block|{
name|conf
operator|.
name|setStrings
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserGroupConfKey
argument_list|(
name|shortUserName
argument_list|)
argument_list|,
name|GROUP_NAME_1
argument_list|)
expr_stmt|;
name|configureSuperUserIPAddresses
argument_list|(
name|conf
argument_list|,
name|shortUserName
argument_list|)
expr_stmt|;
comment|// start the server
name|miniServer
operator|=
operator|new
name|MiniServer
argument_list|(
name|conf
argument_list|,
name|user
argument_list|,
name|keytabFile
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
name|miniServer
operator|.
name|getAddress
argument_list|()
decl_stmt|;
name|connectToServerAndGetDelegationToken
argument_list|(
name|conf
argument_list|,
name|addr
argument_list|)
expr_stmt|;
comment|// connect to the server count times
name|setLoggingLevel
argument_list|(
name|logLevel
argument_list|)
expr_stmt|;
name|long
name|elapsed
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|count
condition|;
name|idx
operator|++
control|)
block|{
name|elapsed
operator|+=
name|connectToServerUsingDelegationToken
argument_list|(
name|conf
argument_list|,
name|addr
argument_list|)
expr_stmt|;
block|}
return|return
name|elapsed
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|miniServer
operator|!=
literal|null
condition|)
name|miniServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|printUsage ()
specifier|static
name|void
name|printUsage
parameter_list|()
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: MiniRPCBenchmark<numIterations> [<keytabFile> [<userName> "
operator|+
literal|"[useToken|useKerberos [<logLevel>]]]]"
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
literal|"Benchmark: RPC session establishment."
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|1
condition|)
name|printUsage
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|int
name|count
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|1
condition|)
name|conf
operator|.
name|set
argument_list|(
name|KEYTAB_FILE_KEY
argument_list|,
name|args
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|2
condition|)
name|conf
operator|.
name|set
argument_list|(
name|USER_NAME_KEY
argument_list|,
name|args
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|boolean
name|useDelegationToken
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|3
condition|)
name|useDelegationToken
operator|=
name|args
index|[
literal|3
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"useToken"
argument_list|)
expr_stmt|;
name|Level
name|l
init|=
name|Level
operator|.
name|ERROR
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|4
condition|)
name|l
operator|=
name|Level
operator|.
name|toLevel
argument_list|(
name|args
index|[
literal|4
index|]
argument_list|)
expr_stmt|;
name|MiniRPCBenchmark
name|mb
init|=
operator|new
name|MiniRPCBenchmark
argument_list|(
name|l
argument_list|)
decl_stmt|;
name|long
name|elapsedTime
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|useDelegationToken
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Running MiniRPCBenchmark with delegation token authentication."
argument_list|)
expr_stmt|;
name|elapsedTime
operator|=
name|mb
operator|.
name|runMiniBenchmarkWithDelegationToken
argument_list|(
name|conf
argument_list|,
name|count
argument_list|,
name|KEYTAB_FILE_KEY
argument_list|,
name|USER_NAME_KEY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|auth
init|=
name|SecurityUtil
operator|.
name|getAuthenticationMethod
argument_list|(
name|conf
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Running MiniRPCBenchmark with "
operator|+
name|auth
operator|+
literal|" authentication."
argument_list|)
expr_stmt|;
name|elapsedTime
operator|=
name|mb
operator|.
name|runMiniBenchmark
argument_list|(
name|conf
argument_list|,
name|count
argument_list|,
name|KEYTAB_FILE_KEY
argument_list|,
name|USER_NAME_KEY
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|VersionInfo
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Number  of  connects: "
operator|+
name|count
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Average connect time: "
operator|+
operator|(
operator|(
name|double
operator|)
name|elapsedTime
operator|/
name|count
operator|)
argument_list|)
expr_stmt|;
block|}
DECL|method|configureSuperUserIPAddresses (Configuration conf, String superUserShortName)
specifier|private
name|void
name|configureSuperUserIPAddresses
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|superUserShortName
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|ipList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|NetworkInterface
argument_list|>
name|netInterfaceList
init|=
name|NetworkInterface
operator|.
name|getNetworkInterfaces
argument_list|()
decl_stmt|;
while|while
condition|(
name|netInterfaceList
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|NetworkInterface
name|inf
init|=
name|netInterfaceList
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|InetAddress
argument_list|>
name|addrList
init|=
name|inf
operator|.
name|getInetAddresses
argument_list|()
decl_stmt|;
while|while
condition|(
name|addrList
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|InetAddress
name|addr
init|=
name|addrList
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|ipList
operator|.
name|add
argument_list|(
name|addr
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|ip
range|:
name|ipList
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|ip
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"127.0.1.1,"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getCanonicalHostName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setStrings
argument_list|(
name|DefaultImpersonationProvider
operator|.
name|getProxySuperuserIpConfKey
argument_list|(
name|superUserShortName
argument_list|)
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

