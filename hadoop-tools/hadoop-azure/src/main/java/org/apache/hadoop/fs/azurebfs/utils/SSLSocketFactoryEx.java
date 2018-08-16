begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.utils
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
name|utils
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
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyManagementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
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
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLSocket
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLSocketFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|wildfly
operator|.
name|openssl
operator|.
name|OpenSSLProvider
import|;
end_import

begin_comment
comment|/**  * Extension to use native OpenSSL library instead of JSSE for better  * performance.  *  */
end_comment

begin_class
DECL|class|SSLSocketFactoryEx
specifier|public
class|class
name|SSLSocketFactoryEx
extends|extends
name|SSLSocketFactory
block|{
comment|/**    * Default indicates Ordered, preferred OpenSSL, if failed to load then fall    * back to Default_JSSE    */
DECL|enum|SSLChannelMode
specifier|public
enum|enum
name|SSLChannelMode
block|{
DECL|enumConstant|OpenSSL
name|OpenSSL
block|,
DECL|enumConstant|Default
name|Default
block|,
DECL|enumConstant|Default_JSSE
name|Default_JSSE
block|}
DECL|field|instance
specifier|private
specifier|static
name|SSLSocketFactoryEx
name|instance
init|=
literal|null
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SSLSocketFactoryEx
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|providerName
specifier|private
name|String
name|providerName
decl_stmt|;
DECL|field|ctx
specifier|private
name|SSLContext
name|ctx
decl_stmt|;
DECL|field|ciphers
specifier|private
name|String
index|[]
name|ciphers
decl_stmt|;
DECL|field|channelMode
specifier|private
name|SSLChannelMode
name|channelMode
decl_stmt|;
comment|/**    * Initialize a singleton SSL socket factory.    *    * @param preferredMode applicable only if the instance is not initialized.    * @throws IOException    */
DECL|method|initializeDefaultFactory ( SSLChannelMode preferredMode)
specifier|public
specifier|synchronized
specifier|static
name|void
name|initializeDefaultFactory
parameter_list|(
name|SSLChannelMode
name|preferredMode
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|instance
operator|=
operator|new
name|SSLSocketFactoryEx
argument_list|(
name|preferredMode
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Singletone instance of the SSLSocketFactory.    *    * SSLSocketFactory must be initialized with appropriate SSLChannelMode    * using initializeDefaultFactory method.    *    * @return instance of the SSLSocketFactory, instance must be initialized by    * initializeDefaultFactory.    */
DECL|method|getDefaultFactory ()
specifier|public
specifier|static
name|SSLSocketFactoryEx
name|getDefaultFactory
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
static|static
block|{
name|OpenSSLProvider
operator|.
name|register
argument_list|()
expr_stmt|;
block|}
DECL|method|SSLSocketFactoryEx (SSLChannelMode preferredChannelMode)
specifier|private
name|SSLSocketFactoryEx
parameter_list|(
name|SSLChannelMode
name|preferredChannelMode
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|initializeSSLContext
argument_list|(
name|preferredChannelMode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
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
catch|catch
parameter_list|(
name|KeyManagementException
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
comment|// Get list of supported cipher suits from the SSL factory.
name|SSLSocketFactory
name|factory
init|=
name|ctx
operator|.
name|getSocketFactory
argument_list|()
decl_stmt|;
name|String
index|[]
name|defaultCiphers
init|=
name|factory
operator|.
name|getSupportedCipherSuites
argument_list|()
decl_stmt|;
name|String
name|version
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|)
decl_stmt|;
name|ciphers
operator|=
operator|(
name|channelMode
operator|==
name|SSLChannelMode
operator|.
name|Default_JSSE
operator|&&
name|version
operator|.
name|startsWith
argument_list|(
literal|"1.8"
argument_list|)
operator|)
condition|?
name|alterCipherList
argument_list|(
name|defaultCiphers
argument_list|)
else|:
name|defaultCiphers
expr_stmt|;
name|providerName
operator|=
name|ctx
operator|.
name|getProvider
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"-"
operator|+
name|ctx
operator|.
name|getProvider
argument_list|()
operator|.
name|getVersion
argument_list|()
expr_stmt|;
block|}
DECL|method|initializeSSLContext (SSLChannelMode preferredChannelMode)
specifier|private
name|void
name|initializeSSLContext
parameter_list|(
name|SSLChannelMode
name|preferredChannelMode
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|KeyManagementException
block|{
switch|switch
condition|(
name|preferredChannelMode
condition|)
block|{
case|case
name|Default
case|:
try|try
block|{
name|ctx
operator|=
name|SSLContext
operator|.
name|getInstance
argument_list|(
literal|"openssl.TLS"
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|init
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|channelMode
operator|=
name|SSLChannelMode
operator|.
name|OpenSSL
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to load OpenSSL. Falling back to the JSSE default."
argument_list|)
expr_stmt|;
name|ctx
operator|=
name|SSLContext
operator|.
name|getDefault
argument_list|()
expr_stmt|;
name|channelMode
operator|=
name|SSLChannelMode
operator|.
name|Default_JSSE
expr_stmt|;
block|}
break|break;
case|case
name|OpenSSL
case|:
name|ctx
operator|=
name|SSLContext
operator|.
name|getInstance
argument_list|(
literal|"openssl.TLS"
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|init
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|channelMode
operator|=
name|SSLChannelMode
operator|.
name|OpenSSL
expr_stmt|;
break|break;
case|case
name|Default_JSSE
case|:
name|ctx
operator|=
name|SSLContext
operator|.
name|getDefault
argument_list|()
expr_stmt|;
name|channelMode
operator|=
name|SSLChannelMode
operator|.
name|Default_JSSE
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unknown channel mode: "
operator|+
name|preferredChannelMode
argument_list|)
throw|;
block|}
block|}
DECL|method|getProviderName ()
specifier|public
name|String
name|getProviderName
parameter_list|()
block|{
return|return
name|providerName
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultCipherSuites ()
specifier|public
name|String
index|[]
name|getDefaultCipherSuites
parameter_list|()
block|{
return|return
name|ciphers
operator|.
name|clone
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSupportedCipherSuites ()
specifier|public
name|String
index|[]
name|getSupportedCipherSuites
parameter_list|()
block|{
return|return
name|ciphers
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|method|createSocket ()
specifier|public
name|Socket
name|createSocket
parameter_list|()
throws|throws
name|IOException
block|{
name|SSLSocketFactory
name|factory
init|=
name|ctx
operator|.
name|getSocketFactory
argument_list|()
decl_stmt|;
name|SSLSocket
name|ss
init|=
operator|(
name|SSLSocket
operator|)
name|factory
operator|.
name|createSocket
argument_list|()
decl_stmt|;
name|configureSocket
argument_list|(
name|ss
argument_list|)
expr_stmt|;
return|return
name|ss
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket (Socket s, String host, int port, boolean autoClose)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|Socket
name|s
parameter_list|,
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|boolean
name|autoClose
parameter_list|)
throws|throws
name|IOException
block|{
name|SSLSocketFactory
name|factory
init|=
name|ctx
operator|.
name|getSocketFactory
argument_list|()
decl_stmt|;
name|SSLSocket
name|ss
init|=
operator|(
name|SSLSocket
operator|)
name|factory
operator|.
name|createSocket
argument_list|(
name|s
argument_list|,
name|host
argument_list|,
name|port
argument_list|,
name|autoClose
argument_list|)
decl_stmt|;
name|configureSocket
argument_list|(
name|ss
argument_list|)
expr_stmt|;
return|return
name|ss
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket (InetAddress address, int port, InetAddress localAddress, int localPort)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|InetAddress
name|address
parameter_list|,
name|int
name|port
parameter_list|,
name|InetAddress
name|localAddress
parameter_list|,
name|int
name|localPort
parameter_list|)
throws|throws
name|IOException
block|{
name|SSLSocketFactory
name|factory
init|=
name|ctx
operator|.
name|getSocketFactory
argument_list|()
decl_stmt|;
name|SSLSocket
name|ss
init|=
operator|(
name|SSLSocket
operator|)
name|factory
operator|.
name|createSocket
argument_list|(
name|address
argument_list|,
name|port
argument_list|,
name|localAddress
argument_list|,
name|localPort
argument_list|)
decl_stmt|;
name|configureSocket
argument_list|(
name|ss
argument_list|)
expr_stmt|;
return|return
name|ss
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket (String host, int port, InetAddress localHost, int localPort)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|InetAddress
name|localHost
parameter_list|,
name|int
name|localPort
parameter_list|)
throws|throws
name|IOException
block|{
name|SSLSocketFactory
name|factory
init|=
name|ctx
operator|.
name|getSocketFactory
argument_list|()
decl_stmt|;
name|SSLSocket
name|ss
init|=
operator|(
name|SSLSocket
operator|)
name|factory
operator|.
name|createSocket
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|localHost
argument_list|,
name|localPort
argument_list|)
decl_stmt|;
name|configureSocket
argument_list|(
name|ss
argument_list|)
expr_stmt|;
return|return
name|ss
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket (InetAddress host, int port)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|InetAddress
name|host
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
name|SSLSocketFactory
name|factory
init|=
name|ctx
operator|.
name|getSocketFactory
argument_list|()
decl_stmt|;
name|SSLSocket
name|ss
init|=
operator|(
name|SSLSocket
operator|)
name|factory
operator|.
name|createSocket
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
decl_stmt|;
name|configureSocket
argument_list|(
name|ss
argument_list|)
expr_stmt|;
return|return
name|ss
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket (String host, int port)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
name|SSLSocketFactory
name|factory
init|=
name|ctx
operator|.
name|getSocketFactory
argument_list|()
decl_stmt|;
name|SSLSocket
name|ss
init|=
operator|(
name|SSLSocket
operator|)
name|factory
operator|.
name|createSocket
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
decl_stmt|;
name|configureSocket
argument_list|(
name|ss
argument_list|)
expr_stmt|;
return|return
name|ss
return|;
block|}
DECL|method|configureSocket (SSLSocket ss)
specifier|private
name|void
name|configureSocket
parameter_list|(
name|SSLSocket
name|ss
parameter_list|)
throws|throws
name|SocketException
block|{
name|ss
operator|.
name|setEnabledCipherSuites
argument_list|(
name|ciphers
argument_list|)
expr_stmt|;
block|}
DECL|method|alterCipherList (String[] defaultCiphers)
specifier|private
name|String
index|[]
name|alterCipherList
parameter_list|(
name|String
index|[]
name|defaultCiphers
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|preferredSuits
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Remove GCM mode based ciphers from the supported list.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|defaultCiphers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|defaultCiphers
index|[
name|i
index|]
operator|.
name|contains
argument_list|(
literal|"_GCM_"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Removed Cipher - "
operator|+
name|defaultCiphers
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|preferredSuits
operator|.
name|add
argument_list|(
name|defaultCiphers
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|ciphers
operator|=
name|preferredSuits
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
return|return
name|ciphers
return|;
block|}
block|}
end_class

end_unit

