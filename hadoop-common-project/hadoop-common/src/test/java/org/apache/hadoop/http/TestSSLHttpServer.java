begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.http
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|http
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|URI
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
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|HttpsURLConnection
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
name|SSLHandshakeException
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
name|io
operator|.
name|IOUtils
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
name|ssl
operator|.
name|KeyStoreTestUtil
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
name|SSLFactory
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
name|hadoop
operator|.
name|util
operator|.
name|StringUtils
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

begin_comment
comment|/**  * This testcase issues SSL certificates configures the HttpServer to serve  * HTTPS using the created certficates and calls an echo servlet using the  * corresponding HTTPS URL.  */
end_comment

begin_class
DECL|class|TestSSLHttpServer
specifier|public
class|class
name|TestSSLHttpServer
extends|extends
name|HttpServerFunctionalTest
block|{
DECL|field|BASEDIR
specifier|private
specifier|static
specifier|final
name|String
name|BASEDIR
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestSSLHttpServer
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
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
name|TestSSLHttpServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|server
specifier|private
specifier|static
name|HttpServer2
name|server
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
DECL|field|clientSslFactory
specifier|private
specifier|static
name|SSLFactory
name|clientSslFactory
decl_stmt|;
DECL|field|excludeCiphers
specifier|private
specifier|static
specifier|final
name|String
name|excludeCiphers
init|=
literal|"TLS_ECDHE_RSA_WITH_RC4_128_SHA,"
operator|+
literal|"SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA, \n"
operator|+
literal|"SSL_RSA_WITH_DES_CBC_SHA,"
operator|+
literal|"SSL_DHE_RSA_WITH_DES_CBC_SHA,  "
operator|+
literal|"SSL_RSA_EXPORT_WITH_RC4_40_MD5,\t \n"
operator|+
literal|"SSL_RSA_EXPORT_WITH_DES40_CBC_SHA,"
operator|+
literal|"SSL_RSA_WITH_RC4_128_MD5 \t"
decl_stmt|;
DECL|field|oneEnabledCiphers
specifier|private
specifier|static
specifier|final
name|String
name|oneEnabledCiphers
init|=
name|excludeCiphers
operator|+
literal|",TLS_RSA_WITH_AES_128_CBC_SHA"
decl_stmt|;
DECL|field|exclusiveEnabledCiphers
specifier|private
specifier|static
specifier|final
name|String
name|exclusiveEnabledCiphers
init|=
literal|"\tTLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA, \n"
operator|+
literal|"TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,"
operator|+
literal|"TLS_RSA_WITH_AES_128_CBC_SHA,"
operator|+
literal|"TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA,  "
operator|+
literal|"TLS_ECDH_RSA_WITH_AES_128_CBC_SHA,"
operator|+
literal|"TLS_DHE_RSA_WITH_AES_128_CBC_SHA,\t\n "
operator|+
literal|"TLS_DHE_DSS_WITH_AES_128_CBC_SHA"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|HttpServer2
operator|.
name|HTTP_MAX_THREADS_KEY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|File
name|base
init|=
operator|new
name|File
argument_list|(
name|BASEDIR
argument_list|)
decl_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|base
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|keystoresDir
operator|=
operator|new
name|File
argument_list|(
name|BASEDIR
argument_list|)
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
name|TestSSLHttpServer
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
argument_list|,
literal|true
argument_list|,
name|excludeCiphers
argument_list|)
expr_stmt|;
name|Configuration
name|sslConf
init|=
name|KeyStoreTestUtil
operator|.
name|getSslConfig
argument_list|()
decl_stmt|;
name|clientSslFactory
operator|=
operator|new
name|SSLFactory
argument_list|(
name|SSLFactory
operator|.
name|Mode
operator|.
name|CLIENT
argument_list|,
name|sslConf
argument_list|)
expr_stmt|;
name|clientSslFactory
operator|.
name|init
argument_list|()
expr_stmt|;
name|server
operator|=
operator|new
name|HttpServer2
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addEndpoint
argument_list|(
operator|new
name|URI
argument_list|(
literal|"https://localhost"
argument_list|)
argument_list|)
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
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
operator|.
name|excludeCiphers
argument_list|(
name|sslConf
operator|.
name|get
argument_list|(
literal|"ssl.server.exclude.cipher.list"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|server
operator|.
name|addServlet
argument_list|(
literal|"echo"
argument_list|,
literal|"/echo"
argument_list|,
name|TestHttpServer
operator|.
name|EchoServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|server
operator|.
name|addServlet
argument_list|(
literal|"longheader"
argument_list|,
literal|"/longheader"
argument_list|,
name|LongHeaderServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|baseUrl
operator|=
operator|new
name|URL
argument_list|(
literal|"https://"
operator|+
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
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"HTTP server started: "
operator|+
name|baseUrl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|cleanup ()
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|BASEDIR
argument_list|)
argument_list|)
expr_stmt|;
name|KeyStoreTestUtil
operator|.
name|cleanupSSLConfig
argument_list|(
name|keystoresDir
argument_list|,
name|sslConfDir
argument_list|)
expr_stmt|;
name|clientSslFactory
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEcho ()
specifier|public
name|void
name|testEcho
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"a:b\nc:d\n"
argument_list|,
name|readOut
argument_list|(
operator|new
name|URL
argument_list|(
name|baseUrl
argument_list|,
literal|"/echo?a=b&c=d"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a:b\nc&lt;:d\ne:&gt;\n"
argument_list|,
name|readOut
argument_list|(
operator|new
name|URL
argument_list|(
name|baseUrl
argument_list|,
literal|"/echo?a=b&c<=d&e=>"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that verifies headers can be up to 64K long. The test adds a 63K    * header leaving 1K for other headers. This is because the header buffer    * setting is for ALL headers, names and values included.    */
annotation|@
name|Test
DECL|method|testLongHeader ()
specifier|public
name|void
name|testLongHeader
parameter_list|()
throws|throws
name|Exception
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|baseUrl
argument_list|,
literal|"/longheader"
argument_list|)
decl_stmt|;
name|HttpsURLConnection
name|conn
init|=
operator|(
name|HttpsURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setSSLSocketFactory
argument_list|(
name|clientSslFactory
operator|.
name|createSSLSocketFactory
argument_list|()
argument_list|)
expr_stmt|;
name|testLongHeader
argument_list|(
name|conn
argument_list|)
expr_stmt|;
block|}
DECL|method|readOut (URL url)
specifier|private
specifier|static
name|String
name|readOut
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|Exception
block|{
name|HttpsURLConnection
name|conn
init|=
operator|(
name|HttpsURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setSSLSocketFactory
argument_list|(
name|clientSslFactory
operator|.
name|createSSLSocketFactory
argument_list|()
argument_list|)
expr_stmt|;
name|InputStream
name|in
init|=
name|conn
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Test that verifies that excluded ciphers (SSL_RSA_WITH_RC4_128_SHA,    * TLS_ECDH_ECDSA_WITH_RC4_128_SHA,TLS_ECDH_RSA_WITH_RC4_128_SHA,    * TLS_ECDHE_ECDSA_WITH_RC4_128_SHA,TLS_ECDHE_RSA_WITH_RC4_128_SHA) are not    * available for negotiation during SSL connection.    */
annotation|@
name|Test
DECL|method|testExcludedCiphers ()
specifier|public
name|void
name|testExcludedCiphers
parameter_list|()
throws|throws
name|Exception
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|baseUrl
argument_list|,
literal|"/echo?a=b&c=d"
argument_list|)
decl_stmt|;
name|HttpsURLConnection
name|conn
init|=
operator|(
name|HttpsURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|SSLSocketFactory
name|sslSocketF
init|=
name|clientSslFactory
operator|.
name|createSSLSocketFactory
argument_list|()
decl_stmt|;
name|PrefferedCipherSSLSocketFactory
name|testPreferredCipherSSLSocketF
init|=
operator|new
name|PrefferedCipherSSLSocketFactory
argument_list|(
name|sslSocketF
argument_list|,
name|StringUtils
operator|.
name|getTrimmedStrings
argument_list|(
name|excludeCiphers
argument_list|)
argument_list|)
decl_stmt|;
name|conn
operator|.
name|setSSLSocketFactory
argument_list|(
name|testPreferredCipherSSLSocketF
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"excludedCipher list is empty"
argument_list|,
name|excludeCiphers
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|InputStream
name|in
init|=
name|conn
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"No Ciphers in common, SSLHandshake must fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SSLHandshakeException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"No Ciphers in common, expected succesful test result."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test that verified that additionally included cipher    * TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA is only available cipher for working    * TLS connection from client to server disabled for all other common ciphers.    */
annotation|@
name|Test
DECL|method|testOneEnabledCiphers ()
specifier|public
name|void
name|testOneEnabledCiphers
parameter_list|()
throws|throws
name|Exception
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|baseUrl
argument_list|,
literal|"/echo?a=b&c=d"
argument_list|)
decl_stmt|;
name|HttpsURLConnection
name|conn
init|=
operator|(
name|HttpsURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|SSLSocketFactory
name|sslSocketF
init|=
name|clientSslFactory
operator|.
name|createSSLSocketFactory
argument_list|()
decl_stmt|;
name|PrefferedCipherSSLSocketFactory
name|testPreferredCipherSSLSocketF
init|=
operator|new
name|PrefferedCipherSSLSocketFactory
argument_list|(
name|sslSocketF
argument_list|,
name|StringUtils
operator|.
name|getTrimmedStrings
argument_list|(
name|oneEnabledCiphers
argument_list|)
argument_list|)
decl_stmt|;
name|conn
operator|.
name|setSSLSocketFactory
argument_list|(
name|testPreferredCipherSSLSocketF
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"excludedCipher list is empty"
argument_list|,
name|oneEnabledCiphers
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|InputStream
name|in
init|=
name|conn
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|out
operator|.
name|toString
argument_list|()
argument_list|,
literal|"a:b\nc:d\n"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Atleast one additional enabled cipher than excluded ciphers,"
operator|+
literal|" expected successful test result."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SSLHandshakeException
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Atleast one additional cipher available for successful handshake."
operator|+
literal|" Unexpected test failure: "
operator|+
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test verifies that mutually exclusive server's disabled cipher suites and    * client's enabled cipher suites can successfully establish TLS connection.    */
annotation|@
name|Test
DECL|method|testExclusiveEnabledCiphers ()
specifier|public
name|void
name|testExclusiveEnabledCiphers
parameter_list|()
throws|throws
name|Exception
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|baseUrl
argument_list|,
literal|"/echo?a=b&c=d"
argument_list|)
decl_stmt|;
name|HttpsURLConnection
name|conn
init|=
operator|(
name|HttpsURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|SSLSocketFactory
name|sslSocketF
init|=
name|clientSslFactory
operator|.
name|createSSLSocketFactory
argument_list|()
decl_stmt|;
name|PrefferedCipherSSLSocketFactory
name|testPreferredCipherSSLSocketF
init|=
operator|new
name|PrefferedCipherSSLSocketFactory
argument_list|(
name|sslSocketF
argument_list|,
name|StringUtils
operator|.
name|getTrimmedStrings
argument_list|(
name|exclusiveEnabledCiphers
argument_list|)
argument_list|)
decl_stmt|;
name|conn
operator|.
name|setSSLSocketFactory
argument_list|(
name|testPreferredCipherSSLSocketF
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"excludedCipher list is empty"
argument_list|,
name|exclusiveEnabledCiphers
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|InputStream
name|in
init|=
name|conn
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|out
operator|.
name|toString
argument_list|()
argument_list|,
literal|"a:b\nc:d\n"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Atleast one additional enabled cipher than excluded ciphers,"
operator|+
literal|" expected successful test result."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SSLHandshakeException
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Atleast one additional cipher available for successful handshake."
operator|+
literal|" Unexpected test failure: "
operator|+
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|PrefferedCipherSSLSocketFactory
specifier|private
class|class
name|PrefferedCipherSSLSocketFactory
extends|extends
name|SSLSocketFactory
block|{
DECL|field|delegateSocketFactory
specifier|private
specifier|final
name|SSLSocketFactory
name|delegateSocketFactory
decl_stmt|;
DECL|field|enabledCipherSuites
specifier|private
specifier|final
name|String
index|[]
name|enabledCipherSuites
decl_stmt|;
DECL|method|PrefferedCipherSSLSocketFactory (SSLSocketFactory sslSocketFactory, String[] pEnabledCipherSuites)
specifier|public
name|PrefferedCipherSSLSocketFactory
parameter_list|(
name|SSLSocketFactory
name|sslSocketFactory
parameter_list|,
name|String
index|[]
name|pEnabledCipherSuites
parameter_list|)
block|{
name|delegateSocketFactory
operator|=
name|sslSocketFactory
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|pEnabledCipherSuites
operator|&&
name|pEnabledCipherSuites
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|enabledCipherSuites
operator|=
name|pEnabledCipherSuites
expr_stmt|;
block|}
else|else
block|{
name|enabledCipherSuites
operator|=
literal|null
expr_stmt|;
block|}
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
name|delegateSocketFactory
operator|.
name|getDefaultCipherSuites
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
name|delegateSocketFactory
operator|.
name|getSupportedCipherSuites
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket (Socket socket, String string, int i, boolean bln)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|Socket
name|socket
parameter_list|,
name|String
name|string
parameter_list|,
name|int
name|i
parameter_list|,
name|boolean
name|bln
parameter_list|)
throws|throws
name|IOException
block|{
name|SSLSocket
name|sslSocket
init|=
operator|(
name|SSLSocket
operator|)
name|delegateSocketFactory
operator|.
name|createSocket
argument_list|(
name|socket
argument_list|,
name|string
argument_list|,
name|i
argument_list|,
name|bln
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|enabledCipherSuites
condition|)
block|{
name|sslSocket
operator|.
name|setEnabledCipherSuites
argument_list|(
name|enabledCipherSuites
argument_list|)
expr_stmt|;
block|}
return|return
name|sslSocket
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket (String string, int i)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|String
name|string
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnknownHostException
block|{
name|SSLSocket
name|sslSocket
init|=
operator|(
name|SSLSocket
operator|)
name|delegateSocketFactory
operator|.
name|createSocket
argument_list|(
name|string
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|enabledCipherSuites
condition|)
block|{
name|sslSocket
operator|.
name|setEnabledCipherSuites
argument_list|(
name|enabledCipherSuites
argument_list|)
expr_stmt|;
block|}
return|return
name|sslSocket
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket (String string, int i, InetAddress ia, int i1)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|String
name|string
parameter_list|,
name|int
name|i
parameter_list|,
name|InetAddress
name|ia
parameter_list|,
name|int
name|i1
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnknownHostException
block|{
name|SSLSocket
name|sslSocket
init|=
operator|(
name|SSLSocket
operator|)
name|delegateSocketFactory
operator|.
name|createSocket
argument_list|(
name|string
argument_list|,
name|i
argument_list|,
name|ia
argument_list|,
name|i1
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|enabledCipherSuites
condition|)
block|{
name|sslSocket
operator|.
name|setEnabledCipherSuites
argument_list|(
name|enabledCipherSuites
argument_list|)
expr_stmt|;
block|}
return|return
name|sslSocket
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket (InetAddress ia, int i)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|InetAddress
name|ia
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|SSLSocket
name|sslSocket
init|=
operator|(
name|SSLSocket
operator|)
name|delegateSocketFactory
operator|.
name|createSocket
argument_list|(
name|ia
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|enabledCipherSuites
condition|)
block|{
name|sslSocket
operator|.
name|setEnabledCipherSuites
argument_list|(
name|enabledCipherSuites
argument_list|)
expr_stmt|;
block|}
return|return
name|sslSocket
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket (InetAddress ia, int i, InetAddress ia1, int i1)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|InetAddress
name|ia
parameter_list|,
name|int
name|i
parameter_list|,
name|InetAddress
name|ia1
parameter_list|,
name|int
name|i1
parameter_list|)
throws|throws
name|IOException
block|{
name|SSLSocket
name|sslSocket
init|=
operator|(
name|SSLSocket
operator|)
name|delegateSocketFactory
operator|.
name|createSocket
argument_list|(
name|ia
argument_list|,
name|i
argument_list|,
name|ia1
argument_list|,
name|i1
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|enabledCipherSuites
condition|)
block|{
name|sslSocket
operator|.
name|setEnabledCipherSuites
argument_list|(
name|enabledCipherSuites
argument_list|)
expr_stmt|;
block|}
return|return
name|sslSocket
return|;
block|}
block|}
block|}
end_class

end_unit

