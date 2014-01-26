begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
package|;
end_package

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
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|MethodRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|FrameworkMethod
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|Connector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|security
operator|.
name|SslSocketConnector
import|;
end_import

begin_class
DECL|class|TestJettyHelper
specifier|public
class|class
name|TestJettyHelper
implements|implements
name|MethodRule
block|{
DECL|field|ssl
specifier|private
name|boolean
name|ssl
decl_stmt|;
DECL|field|keyStoreType
specifier|private
name|String
name|keyStoreType
decl_stmt|;
DECL|field|keyStore
specifier|private
name|String
name|keyStore
decl_stmt|;
DECL|field|keyStorePassword
specifier|private
name|String
name|keyStorePassword
decl_stmt|;
DECL|field|server
specifier|private
name|Server
name|server
decl_stmt|;
DECL|method|TestJettyHelper ()
specifier|public
name|TestJettyHelper
parameter_list|()
block|{
name|this
operator|.
name|ssl
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|TestJettyHelper (String keyStoreType, String keyStore, String keyStorePassword)
specifier|public
name|TestJettyHelper
parameter_list|(
name|String
name|keyStoreType
parameter_list|,
name|String
name|keyStore
parameter_list|,
name|String
name|keyStorePassword
parameter_list|)
block|{
name|ssl
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|keyStoreType
operator|=
name|keyStoreType
expr_stmt|;
name|this
operator|.
name|keyStore
operator|=
name|keyStore
expr_stmt|;
name|this
operator|.
name|keyStorePassword
operator|=
name|keyStorePassword
expr_stmt|;
block|}
DECL|field|TEST_JETTY_TL
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|TestJettyHelper
argument_list|>
name|TEST_JETTY_TL
init|=
operator|new
name|InheritableThreadLocal
argument_list|<
name|TestJettyHelper
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|apply (final Statement statement, final FrameworkMethod frameworkMethod, final Object o)
specifier|public
name|Statement
name|apply
parameter_list|(
specifier|final
name|Statement
name|statement
parameter_list|,
specifier|final
name|FrameworkMethod
name|frameworkMethod
parameter_list|,
specifier|final
name|Object
name|o
parameter_list|)
block|{
return|return
operator|new
name|Statement
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|evaluate
parameter_list|()
throws|throws
name|Throwable
block|{
name|TestJetty
name|testJetty
init|=
name|frameworkMethod
operator|.
name|getAnnotation
argument_list|(
name|TestJetty
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|testJetty
operator|!=
literal|null
condition|)
block|{
name|server
operator|=
name|createJettyServer
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|TEST_JETTY_TL
operator|.
name|set
argument_list|(
name|TestJettyHelper
operator|.
name|this
argument_list|)
expr_stmt|;
name|statement
operator|.
name|evaluate
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|TEST_JETTY_TL
operator|.
name|remove
argument_list|()
expr_stmt|;
if|if
condition|(
name|server
operator|!=
literal|null
operator|&&
name|server
operator|.
name|isRunning
argument_list|()
condition|)
block|{
try|try
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not stop embedded servlet container, "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
return|;
block|}
DECL|method|createJettyServer ()
specifier|private
name|Server
name|createJettyServer
parameter_list|()
block|{
try|try
block|{
name|InetAddress
name|localhost
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"localhost"
argument_list|)
decl_stmt|;
name|String
name|host
init|=
literal|"localhost"
decl_stmt|;
name|ServerSocket
name|ss
init|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|,
literal|50
argument_list|,
name|localhost
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|ss
operator|.
name|getLocalPort
argument_list|()
decl_stmt|;
name|ss
operator|.
name|close
argument_list|()
expr_stmt|;
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|ssl
condition|)
block|{
name|server
operator|.
name|getConnectors
argument_list|()
index|[
literal|0
index|]
operator|.
name|setHost
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|server
operator|.
name|getConnectors
argument_list|()
index|[
literal|0
index|]
operator|.
name|setPort
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SslSocketConnector
name|c
init|=
operator|new
name|SslSocketConnector
argument_list|()
decl_stmt|;
name|c
operator|.
name|setHost
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|c
operator|.
name|setPort
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|c
operator|.
name|setNeedClientAuth
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|c
operator|.
name|setKeystore
argument_list|(
name|keyStore
argument_list|)
expr_stmt|;
name|c
operator|.
name|setKeystoreType
argument_list|(
name|keyStoreType
argument_list|)
expr_stmt|;
name|c
operator|.
name|setKeyPassword
argument_list|(
name|keyStorePassword
argument_list|)
expr_stmt|;
name|server
operator|.
name|setConnectors
argument_list|(
operator|new
name|Connector
index|[]
block|{
name|c
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|server
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not start embedded servlet container, "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns the authority (hostname& port) used by the JettyServer.    *    * @return an<code>InetSocketAddress</code> with the corresponding authority.    */
DECL|method|getAuthority ()
specifier|public
specifier|static
name|InetSocketAddress
name|getAuthority
parameter_list|()
block|{
name|Server
name|server
init|=
name|getJettyServer
argument_list|()
decl_stmt|;
try|try
block|{
name|InetAddress
name|add
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|server
operator|.
name|getConnectors
argument_list|()
index|[
literal|0
index|]
operator|.
name|getHost
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|server
operator|.
name|getConnectors
argument_list|()
index|[
literal|0
index|]
operator|.
name|getPort
argument_list|()
decl_stmt|;
return|return
operator|new
name|InetSocketAddress
argument_list|(
name|add
argument_list|,
name|port
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns a Jetty server ready to be configured and the started. This server    * is only available when the test method has been annotated with    * {@link TestJetty}. Refer to {@link HTestCase} header for details.    *<p/>    * Once configured, the Jetty server should be started. The server will be    * automatically stopped when the test method ends.    *    * @return a Jetty server ready to be configured and the started.    */
DECL|method|getJettyServer ()
specifier|public
specifier|static
name|Server
name|getJettyServer
parameter_list|()
block|{
name|TestJettyHelper
name|helper
init|=
name|TEST_JETTY_TL
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|helper
operator|==
literal|null
operator|||
name|helper
operator|.
name|server
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"This test does not use @TestJetty"
argument_list|)
throw|;
block|}
return|return
name|helper
operator|.
name|server
return|;
block|}
comment|/**    * Returns the base URL (SCHEMA://HOST:PORT) of the test Jetty server    * (see {@link #getJettyServer()}) once started.    *    * @return the base URL (SCHEMA://HOST:PORT) of the test Jetty server.    */
DECL|method|getJettyURL ()
specifier|public
specifier|static
name|URL
name|getJettyURL
parameter_list|()
block|{
name|TestJettyHelper
name|helper
init|=
name|TEST_JETTY_TL
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|helper
operator|==
literal|null
operator|||
name|helper
operator|.
name|server
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"This test does not use @TestJetty"
argument_list|)
throw|;
block|}
try|try
block|{
name|String
name|scheme
init|=
operator|(
name|helper
operator|.
name|ssl
operator|)
condition|?
literal|"https"
else|:
literal|"http"
decl_stmt|;
return|return
operator|new
name|URL
argument_list|(
name|scheme
operator|+
literal|"://"
operator|+
name|helper
operator|.
name|server
operator|.
name|getConnectors
argument_list|()
index|[
literal|0
index|]
operator|.
name|getHost
argument_list|()
operator|+
literal|":"
operator|+
name|helper
operator|.
name|server
operator|.
name|getConnectors
argument_list|()
index|[
literal|0
index|]
operator|.
name|getPort
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"It should never happen, "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

