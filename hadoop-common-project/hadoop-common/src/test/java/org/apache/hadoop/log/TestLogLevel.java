begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.log
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|log
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
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
name|Log
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
name|LogFactory
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
name|HadoopIllegalArgumentException
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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|fs
operator|.
name|FileUtil
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
name|http
operator|.
name|HttpServer2
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
name|log
operator|.
name|LogLevel
operator|.
name|CLI
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
name|minikdc
operator|.
name|KerberosSecurityTestcase
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
name|authentication
operator|.
name|KerberosTestUtils
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
name|authentication
operator|.
name|client
operator|.
name|KerberosAuthenticator
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
name|AccessControlList
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
name|ssl
operator|.
name|KeyStoreTestUtil
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
name|hadoop
operator|.
name|test
operator|.
name|GenericTestUtils
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
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|slf4j
operator|.
name|LoggerFactory
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
name|SSLException
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
name|assertEquals
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
name|assertFalse
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
name|assertTrue
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
name|fail
import|;
end_import

begin_comment
comment|/**  * Test LogLevel.  */
end_comment

begin_class
DECL|class|TestLogLevel
specifier|public
class|class
name|TestLogLevel
extends|extends
name|KerberosSecurityTestcase
block|{
DECL|field|BASEDIR
specifier|private
specifier|static
specifier|final
name|File
name|BASEDIR
init|=
name|GenericTestUtils
operator|.
name|getRandomizedTestDir
argument_list|()
decl_stmt|;
DECL|field|keystoresDir
specifier|private
specifier|static
name|String
name|keystoresDir
decl_stmt|;
DECL|field|sslConfDir
specifier|private
specifier|static
name|String
name|sslConfDir
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|sslConf
specifier|private
specifier|static
name|Configuration
name|sslConf
decl_stmt|;
DECL|field|logName
specifier|private
specifier|final
name|String
name|logName
init|=
name|TestLogLevel
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|field|clientPrincipal
specifier|private
name|String
name|clientPrincipal
decl_stmt|;
DECL|field|serverPrincipal
specifier|private
name|String
name|serverPrincipal
decl_stmt|;
DECL|field|testlog
specifier|private
specifier|final
name|Log
name|testlog
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|logName
argument_list|)
decl_stmt|;
DECL|field|log
specifier|private
specifier|final
name|Logger
name|log
init|=
operator|(
operator|(
name|Log4JLogger
operator|)
name|testlog
operator|)
operator|.
name|getLogger
argument_list|()
decl_stmt|;
DECL|field|PRINCIPAL
specifier|private
specifier|final
specifier|static
name|String
name|PRINCIPAL
init|=
literal|"loglevel.principal"
decl_stmt|;
DECL|field|KEYTAB
specifier|private
specifier|final
specifier|static
name|String
name|KEYTAB
init|=
literal|"loglevel.keytab"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|org
operator|.
name|slf4j
operator|.
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|KerberosAuthenticator
operator|.
name|class
argument_list|)
decl_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|logger
argument_list|,
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|BASEDIR
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|BASEDIR
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"unable to create the base directory for testing"
argument_list|)
throw|;
block|}
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|setupSSL
argument_list|(
name|BASEDIR
argument_list|)
expr_stmt|;
block|}
DECL|method|setupSSL (File base)
specifier|static
specifier|private
name|void
name|setupSSL
parameter_list|(
name|File
name|base
parameter_list|)
throws|throws
name|Exception
block|{
name|keystoresDir
operator|=
name|base
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|sslConfDir
operator|=
name|KeyStoreTestUtil
operator|.
name|getClasspathDir
argument_list|(
name|TestLogLevel
operator|.
name|class
argument_list|)
expr_stmt|;
name|KeyStoreTestUtil
operator|.
name|setupSSLConfig
argument_list|(
name|keystoresDir
argument_list|,
name|sslConfDir
argument_list|,
name|conf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sslConf
operator|=
name|KeyStoreTestUtil
operator|.
name|getSslConfig
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setupKerberos ()
specifier|public
name|void
name|setupKerberos
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|keytabFile
init|=
operator|new
name|File
argument_list|(
name|KerberosTestUtils
operator|.
name|getKeytabFile
argument_list|()
argument_list|)
decl_stmt|;
name|clientPrincipal
operator|=
name|KerberosTestUtils
operator|.
name|getClientPrincipal
argument_list|()
expr_stmt|;
name|serverPrincipal
operator|=
name|KerberosTestUtils
operator|.
name|getServerPrincipal
argument_list|()
expr_stmt|;
name|clientPrincipal
operator|=
name|clientPrincipal
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|clientPrincipal
operator|.
name|lastIndexOf
argument_list|(
literal|"@"
argument_list|)
argument_list|)
expr_stmt|;
name|serverPrincipal
operator|=
name|serverPrincipal
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|serverPrincipal
operator|.
name|lastIndexOf
argument_list|(
literal|"@"
argument_list|)
argument_list|)
expr_stmt|;
name|getKdc
argument_list|()
operator|.
name|createPrincipal
argument_list|(
name|keytabFile
argument_list|,
name|clientPrincipal
argument_list|,
name|serverPrincipal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|KeyStoreTestUtil
operator|.
name|cleanupSSLConfig
argument_list|(
name|keystoresDir
argument_list|,
name|sslConfDir
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|BASEDIR
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test client command line options. Does not validate server behavior.    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testCommandOptions ()
specifier|public
name|void
name|testCommandOptions
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|className
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|validateCommand
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-foo"
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// fail due to insufficient number of arguments
name|assertFalse
argument_list|(
name|validateCommand
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|validateCommand
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-getlevel"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|validateCommand
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-setlevel"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|validateCommand
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-getlevel"
block|,
literal|"foo.bar:8080"
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// valid command arguments
name|assertTrue
argument_list|(
name|validateCommand
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-getlevel"
block|,
literal|"foo.bar:8080"
block|,
name|className
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|validateCommand
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-setlevel"
block|,
literal|"foo.bar:8080"
block|,
name|className
block|,
literal|"DEBUG"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|validateCommand
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-getlevel"
block|,
literal|"foo.bar:8080"
block|,
name|className
block|,
literal|"-protocol"
block|,
literal|"http"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|validateCommand
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-getlevel"
block|,
literal|"foo.bar:8080"
block|,
name|className
block|,
literal|"-protocol"
block|,
literal|"https"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|validateCommand
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-setlevel"
block|,
literal|"foo.bar:8080"
block|,
name|className
block|,
literal|"DEBUG"
block|,
literal|"-protocol"
block|,
literal|"http"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|validateCommand
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-setlevel"
block|,
literal|"foo.bar:8080"
block|,
name|className
block|,
literal|"DEBUG"
block|,
literal|"-protocol"
block|,
literal|"https"
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// fail due to the extra argument
name|assertFalse
argument_list|(
name|validateCommand
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-getlevel"
block|,
literal|"foo.bar:8080"
block|,
name|className
block|,
literal|"-protocol"
block|,
literal|"https"
block|,
literal|"blah"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|validateCommand
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-setlevel"
block|,
literal|"foo.bar:8080"
block|,
name|className
block|,
literal|"DEBUG"
block|,
literal|"-protocol"
block|,
literal|"https"
block|,
literal|"blah"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|validateCommand
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-getlevel"
block|,
literal|"foo.bar:8080"
block|,
name|className
block|,
literal|"-protocol"
block|,
literal|"https"
block|,
literal|"-protocol"
block|,
literal|"https"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|validateCommand
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-getlevel"
block|,
literal|"foo.bar:8080"
block|,
name|className
block|,
literal|"-setlevel"
block|,
literal|"foo.bar:8080"
block|,
name|className
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check to see if a command can be accepted.    *    * @param args a String array of arguments    * @return true if the command can be accepted, false if not.    */
DECL|method|validateCommand (String[] args)
specifier|private
name|boolean
name|validateCommand
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|CLI
name|cli
init|=
operator|new
name|CLI
argument_list|(
name|sslConf
argument_list|)
decl_stmt|;
try|try
block|{
name|cli
operator|.
name|parseArguments
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HadoopIllegalArgumentException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// this is used to verify the command arguments only.
comment|// no HadoopIllegalArgumentException = the arguments are good.
return|return
literal|true
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Creates and starts a Jetty server binding at an ephemeral port to run    * LogLevel servlet.    * @param protocol "http" or "https"    * @param isSpnego true if SPNEGO is enabled    * @return a created HttpServer2 object    * @throws Exception if unable to create or start a Jetty server    */
DECL|method|createServer (String protocol, boolean isSpnego)
specifier|private
name|HttpServer2
name|createServer
parameter_list|(
name|String
name|protocol
parameter_list|,
name|boolean
name|isSpnego
parameter_list|)
throws|throws
name|Exception
block|{
name|HttpServer2
operator|.
name|Builder
name|builder
init|=
operator|new
name|HttpServer2
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|".."
argument_list|)
operator|.
name|addEndpoint
argument_list|(
operator|new
name|URI
argument_list|(
name|protocol
operator|+
literal|"://localhost:0"
argument_list|)
argument_list|)
operator|.
name|setFindPort
argument_list|(
literal|true
argument_list|)
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|isSpnego
condition|)
block|{
comment|// Set up server Kerberos credentials.
comment|// Since the server may fall back to simple authentication,
comment|// use ACL to make sure the connection is Kerberos/SPNEGO authenticated.
name|builder
operator|.
name|setSecurityEnabled
argument_list|(
literal|true
argument_list|)
operator|.
name|setUsernameConfKey
argument_list|(
name|PRINCIPAL
argument_list|)
operator|.
name|setKeytabConfKey
argument_list|(
name|KEYTAB
argument_list|)
operator|.
name|setACL
argument_list|(
operator|new
name|AccessControlList
argument_list|(
name|clientPrincipal
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// if using HTTPS, configure keystore/truststore properties.
if|if
condition|(
name|protocol
operator|.
name|equals
argument_list|(
name|LogLevel
operator|.
name|PROTOCOL_HTTPS
argument_list|)
condition|)
block|{
name|builder
operator|=
name|builder
operator|.
name|keyPassword
argument_list|(
name|sslConf
operator|.
name|get
argument_list|(
literal|"ssl.server.keystore.keypassword"
argument_list|)
argument_list|)
operator|.
name|keyStore
argument_list|(
name|sslConf
operator|.
name|get
argument_list|(
literal|"ssl.server.keystore.location"
argument_list|)
argument_list|,
name|sslConf
operator|.
name|get
argument_list|(
literal|"ssl.server.keystore.password"
argument_list|)
argument_list|,
name|sslConf
operator|.
name|get
argument_list|(
literal|"ssl.server.keystore.type"
argument_list|,
literal|"jks"
argument_list|)
argument_list|)
operator|.
name|trustStore
argument_list|(
name|sslConf
operator|.
name|get
argument_list|(
literal|"ssl.server.truststore.location"
argument_list|)
argument_list|,
name|sslConf
operator|.
name|get
argument_list|(
literal|"ssl.server.truststore.password"
argument_list|)
argument_list|,
name|sslConf
operator|.
name|get
argument_list|(
literal|"ssl.server.truststore.type"
argument_list|,
literal|"jks"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|HttpServer2
name|server
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Enable SPNEGO for LogLevel servlet
if|if
condition|(
name|isSpnego
condition|)
block|{
name|server
operator|.
name|addInternalServlet
argument_list|(
literal|"logLevel"
argument_list|,
literal|"/logLevel"
argument_list|,
name|LogLevel
operator|.
name|Servlet
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|server
return|;
block|}
DECL|method|testDynamicLogLevel (final String bindProtocol, final String connectProtocol, final boolean isSpnego)
specifier|private
name|void
name|testDynamicLogLevel
parameter_list|(
specifier|final
name|String
name|bindProtocol
parameter_list|,
specifier|final
name|String
name|connectProtocol
parameter_list|,
specifier|final
name|boolean
name|isSpnego
parameter_list|)
throws|throws
name|Exception
block|{
name|testDynamicLogLevel
argument_list|(
name|bindProtocol
argument_list|,
name|connectProtocol
argument_list|,
name|isSpnego
argument_list|,
name|Level
operator|.
name|DEBUG
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Run both client and server using the given protocol.    *    * @param bindProtocol specify either http or https for server    * @param connectProtocol specify either http or https for client    * @param isSpnego true if SPNEGO is enabled    * @throws Exception    */
DECL|method|testDynamicLogLevel (final String bindProtocol, final String connectProtocol, final boolean isSpnego, final String newLevel)
specifier|private
name|void
name|testDynamicLogLevel
parameter_list|(
specifier|final
name|String
name|bindProtocol
parameter_list|,
specifier|final
name|String
name|connectProtocol
parameter_list|,
specifier|final
name|boolean
name|isSpnego
parameter_list|,
specifier|final
name|String
name|newLevel
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|LogLevel
operator|.
name|isValidProtocol
argument_list|(
name|bindProtocol
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Invalid server protocol "
operator|+
name|bindProtocol
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|LogLevel
operator|.
name|isValidProtocol
argument_list|(
name|connectProtocol
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Invalid client protocol "
operator|+
name|connectProtocol
argument_list|)
throw|;
block|}
name|Level
name|oldLevel
init|=
name|log
operator|.
name|getEffectiveLevel
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"Get default Log Level which shouldn't be ERROR."
argument_list|,
name|Level
operator|.
name|ERROR
argument_list|,
name|oldLevel
argument_list|)
expr_stmt|;
comment|// configs needed for SPNEGO at server side
if|if
condition|(
name|isSpnego
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|PRINCIPAL
argument_list|,
name|KerberosTestUtils
operator|.
name|getServerPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KEYTAB
argument_list|,
name|KerberosTestUtils
operator|.
name|getKeytabFile
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_AUTHORIZATION
argument_list|,
literal|true
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
specifier|final
name|HttpServer2
name|server
init|=
name|createServer
argument_list|(
name|bindProtocol
argument_list|,
name|isSpnego
argument_list|)
decl_stmt|;
comment|// get server port
specifier|final
name|String
name|authority
init|=
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|server
operator|.
name|getConnectorAddress
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|KerberosTestUtils
operator|.
name|doAsClient
argument_list|(
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
comment|// client command line
name|getLevel
argument_list|(
name|connectProtocol
argument_list|,
name|authority
argument_list|)
expr_stmt|;
name|setLevel
argument_list|(
name|connectProtocol
argument_list|,
name|authority
argument_list|,
name|newLevel
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// restore log level
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|log
argument_list|,
name|oldLevel
argument_list|)
expr_stmt|;
block|}
comment|/**    * Run LogLevel command line to start a client to get log level of this test    * class.    *    * @param protocol specify either http or https    * @param authority daemon's web UI address    * @throws Exception if unable to connect    */
DECL|method|getLevel (String protocol, String authority)
specifier|private
name|void
name|getLevel
parameter_list|(
name|String
name|protocol
parameter_list|,
name|String
name|authority
parameter_list|)
throws|throws
name|Exception
block|{
name|String
index|[]
name|getLevelArgs
init|=
block|{
literal|"-getlevel"
block|,
name|authority
block|,
name|logName
block|,
literal|"-protocol"
block|,
name|protocol
block|}
decl_stmt|;
name|CLI
name|cli
init|=
operator|new
name|CLI
argument_list|(
name|sslConf
argument_list|)
decl_stmt|;
name|cli
operator|.
name|run
argument_list|(
name|getLevelArgs
argument_list|)
expr_stmt|;
block|}
comment|/**    * Run LogLevel command line to start a client to set log level of this test    * class to debug.    *    * @param protocol specify either http or https    * @param authority daemon's web UI address    * @throws Exception if unable to run or log level does not change as expected    */
DECL|method|setLevel (String protocol, String authority, String newLevel)
specifier|private
name|void
name|setLevel
parameter_list|(
name|String
name|protocol
parameter_list|,
name|String
name|authority
parameter_list|,
name|String
name|newLevel
parameter_list|)
throws|throws
name|Exception
block|{
name|String
index|[]
name|setLevelArgs
init|=
block|{
literal|"-setlevel"
block|,
name|authority
block|,
name|logName
block|,
name|newLevel
block|,
literal|"-protocol"
block|,
name|protocol
block|}
decl_stmt|;
name|CLI
name|cli
init|=
operator|new
name|CLI
argument_list|(
name|sslConf
argument_list|)
decl_stmt|;
name|cli
operator|.
name|run
argument_list|(
name|setLevelArgs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new level not equal to expected: "
argument_list|,
name|newLevel
operator|.
name|toUpperCase
argument_list|()
argument_list|,
name|log
operator|.
name|getEffectiveLevel
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test setting log level to "Info".    *    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testInfoLogLevel ()
specifier|public
name|void
name|testInfoLogLevel
parameter_list|()
throws|throws
name|Exception
block|{
name|testDynamicLogLevel
argument_list|(
name|LogLevel
operator|.
name|PROTOCOL_HTTP
argument_list|,
name|LogLevel
operator|.
name|PROTOCOL_HTTP
argument_list|,
literal|false
argument_list|,
literal|"Info"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test setting log level to "Error".    *    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testErrorLogLevel ()
specifier|public
name|void
name|testErrorLogLevel
parameter_list|()
throws|throws
name|Exception
block|{
name|testDynamicLogLevel
argument_list|(
name|LogLevel
operator|.
name|PROTOCOL_HTTP
argument_list|,
name|LogLevel
operator|.
name|PROTOCOL_HTTP
argument_list|,
literal|false
argument_list|,
literal|"Error"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Server runs HTTP, no SPNEGO.    *    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testLogLevelByHttp ()
specifier|public
name|void
name|testLogLevelByHttp
parameter_list|()
throws|throws
name|Exception
block|{
name|testDynamicLogLevel
argument_list|(
name|LogLevel
operator|.
name|PROTOCOL_HTTP
argument_list|,
name|LogLevel
operator|.
name|PROTOCOL_HTTP
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|testDynamicLogLevel
argument_list|(
name|LogLevel
operator|.
name|PROTOCOL_HTTP
argument_list|,
name|LogLevel
operator|.
name|PROTOCOL_HTTPS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"A HTTPS Client should not have succeeded in connecting to a "
operator|+
literal|"HTTP server"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SSLException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Error while authenticating "
operator|+
literal|"with endpoint"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Unrecognized SSL message"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Server runs HTTP + SPNEGO.    *    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testLogLevelByHttpWithSpnego ()
specifier|public
name|void
name|testLogLevelByHttpWithSpnego
parameter_list|()
throws|throws
name|Exception
block|{
name|testDynamicLogLevel
argument_list|(
name|LogLevel
operator|.
name|PROTOCOL_HTTP
argument_list|,
name|LogLevel
operator|.
name|PROTOCOL_HTTP
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|testDynamicLogLevel
argument_list|(
name|LogLevel
operator|.
name|PROTOCOL_HTTP
argument_list|,
name|LogLevel
operator|.
name|PROTOCOL_HTTPS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"A HTTPS Client should not have succeeded in connecting to a "
operator|+
literal|"HTTP server"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SSLException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Error while authenticating "
operator|+
literal|"with endpoint"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Unrecognized SSL message"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Server runs HTTPS, no SPNEGO.    *    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testLogLevelByHttps ()
specifier|public
name|void
name|testLogLevelByHttps
parameter_list|()
throws|throws
name|Exception
block|{
name|testDynamicLogLevel
argument_list|(
name|LogLevel
operator|.
name|PROTOCOL_HTTPS
argument_list|,
name|LogLevel
operator|.
name|PROTOCOL_HTTPS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|testDynamicLogLevel
argument_list|(
name|LogLevel
operator|.
name|PROTOCOL_HTTPS
argument_list|,
name|LogLevel
operator|.
name|PROTOCOL_HTTP
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"A HTTP Client should not have succeeded in connecting to a "
operator|+
literal|"HTTPS server"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Error while authenticating "
operator|+
literal|"with endpoint"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Unexpected end of file from server"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Server runs HTTPS + SPNEGO.    *    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testLogLevelByHttpsWithSpnego ()
specifier|public
name|void
name|testLogLevelByHttpsWithSpnego
parameter_list|()
throws|throws
name|Exception
block|{
name|testDynamicLogLevel
argument_list|(
name|LogLevel
operator|.
name|PROTOCOL_HTTPS
argument_list|,
name|LogLevel
operator|.
name|PROTOCOL_HTTPS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|testDynamicLogLevel
argument_list|(
name|LogLevel
operator|.
name|PROTOCOL_HTTPS
argument_list|,
name|LogLevel
operator|.
name|PROTOCOL_HTTP
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"A HTTP Client should not have succeeded in connecting to a "
operator|+
literal|"HTTPS server"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Error while authenticating "
operator|+
literal|"with endpoint"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Unexpected end of file from server"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

