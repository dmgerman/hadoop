begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|UserGroupInformation
operator|.
name|AuthenticationMethod
operator|.
name|*
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
name|URI
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
name|kerberos
operator|.
name|KerberosPrincipal
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestSecurityUtil
specifier|public
class|class
name|TestSecurityUtil
block|{
annotation|@
name|BeforeClass
DECL|method|unsetKerberosRealm ()
specifier|public
specifier|static
name|void
name|unsetKerberosRealm
parameter_list|()
block|{
comment|// prevent failures if kinit-ed or on os x with no realm
name|System
operator|.
name|setProperty
argument_list|(
literal|"java.security.krb5.kdc"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"java.security.krb5.realm"
argument_list|,
literal|"NONE"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|isOriginalTGTReturnsCorrectValues ()
specifier|public
name|void
name|isOriginalTGTReturnsCorrectValues
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|SecurityUtil
operator|.
name|isTGSPrincipal
argument_list|(
operator|new
name|KerberosPrincipal
argument_list|(
literal|"krbtgt/foo@foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|SecurityUtil
operator|.
name|isTGSPrincipal
argument_list|(
operator|new
name|KerberosPrincipal
argument_list|(
literal|"krbtgt/foo.bar.bat@foo.bar.bat"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|SecurityUtil
operator|.
name|isTGSPrincipal
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|SecurityUtil
operator|.
name|isTGSPrincipal
argument_list|(
operator|new
name|KerberosPrincipal
argument_list|(
literal|"blah"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|SecurityUtil
operator|.
name|isTGSPrincipal
argument_list|(
operator|new
name|KerberosPrincipal
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|SecurityUtil
operator|.
name|isTGSPrincipal
argument_list|(
operator|new
name|KerberosPrincipal
argument_list|(
literal|"krbtgt/hello"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|SecurityUtil
operator|.
name|isTGSPrincipal
argument_list|(
operator|new
name|KerberosPrincipal
argument_list|(
literal|"/@"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|SecurityUtil
operator|.
name|isTGSPrincipal
argument_list|(
operator|new
name|KerberosPrincipal
argument_list|(
literal|"krbtgt/foo@FOO"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|verify (String original, String hostname, String expected)
specifier|private
name|void
name|verify
parameter_list|(
name|String
name|original
parameter_list|,
name|String
name|hostname
parameter_list|,
name|String
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
name|original
argument_list|,
name|hostname
argument_list|)
argument_list|)
expr_stmt|;
name|InetAddress
name|addr
init|=
name|mockAddr
argument_list|(
name|hostname
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
name|original
argument_list|,
name|addr
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|mockAddr (String reverseTo)
specifier|private
name|InetAddress
name|mockAddr
parameter_list|(
name|String
name|reverseTo
parameter_list|)
block|{
name|InetAddress
name|mock
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|InetAddress
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|reverseTo
argument_list|)
operator|.
name|when
argument_list|(
name|mock
argument_list|)
operator|.
name|getCanonicalHostName
argument_list|()
expr_stmt|;
return|return
name|mock
return|;
block|}
annotation|@
name|Test
DECL|method|testGetServerPrincipal ()
specifier|public
name|void
name|testGetServerPrincipal
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|service
init|=
literal|"hdfs/"
decl_stmt|;
name|String
name|realm
init|=
literal|"@REALM"
decl_stmt|;
name|String
name|hostname
init|=
literal|"foohost"
decl_stmt|;
name|String
name|userPrincipal
init|=
literal|"foo@FOOREALM"
decl_stmt|;
name|String
name|shouldReplace
init|=
name|service
operator|+
name|SecurityUtil
operator|.
name|HOSTNAME_PATTERN
operator|+
name|realm
decl_stmt|;
name|String
name|replaced
init|=
name|service
operator|+
name|hostname
operator|+
name|realm
decl_stmt|;
name|verify
argument_list|(
name|shouldReplace
argument_list|,
name|hostname
argument_list|,
name|replaced
argument_list|)
expr_stmt|;
name|String
name|shouldNotReplace
init|=
name|service
operator|+
name|SecurityUtil
operator|.
name|HOSTNAME_PATTERN
operator|+
literal|"NAME"
operator|+
name|realm
decl_stmt|;
name|verify
argument_list|(
name|shouldNotReplace
argument_list|,
name|hostname
argument_list|,
name|shouldNotReplace
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|userPrincipal
argument_list|,
name|hostname
argument_list|,
name|userPrincipal
argument_list|)
expr_stmt|;
comment|// testing reverse DNS lookup doesn't happen
name|InetAddress
name|notUsed
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|InetAddress
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|shouldNotReplace
argument_list|,
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
name|shouldNotReplace
argument_list|,
name|notUsed
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|notUsed
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|getCanonicalHostName
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPrincipalsWithLowerCaseHosts ()
specifier|public
name|void
name|testPrincipalsWithLowerCaseHosts
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|service
init|=
literal|"xyz/"
decl_stmt|;
name|String
name|realm
init|=
literal|"@REALM"
decl_stmt|;
name|String
name|principalInConf
init|=
name|service
operator|+
name|SecurityUtil
operator|.
name|HOSTNAME_PATTERN
operator|+
name|realm
decl_stmt|;
name|String
name|hostname
init|=
literal|"FooHost"
decl_stmt|;
name|String
name|principal
init|=
name|service
operator|+
name|hostname
operator|.
name|toLowerCase
argument_list|()
operator|+
name|realm
decl_stmt|;
name|verify
argument_list|(
name|principalInConf
argument_list|,
name|hostname
argument_list|,
name|principal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLocalHostNameForNullOrWild ()
specifier|public
name|void
name|testLocalHostNameForNullOrWild
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|local
init|=
name|SecurityUtil
operator|.
name|getLocalHostName
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hdfs/"
operator|+
name|local
operator|+
literal|"@REALM"
argument_list|,
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
literal|"hdfs/_HOST@REALM"
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hdfs/"
operator|+
name|local
operator|+
literal|"@REALM"
argument_list|,
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
literal|"hdfs/_HOST@REALM"
argument_list|,
literal|"0.0.0.0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStartsWithIncorrectSettings ()
specifier|public
name|void
name|testStartsWithIncorrectSettings
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|SecurityUtil
operator|.
name|setAuthenticationMethod
argument_list|(
name|KERBEROS
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|String
name|keyTabKey
init|=
literal|"key"
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|keyTabKey
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|boolean
name|gotException
init|=
literal|false
decl_stmt|;
try|try
block|{
name|SecurityUtil
operator|.
name|login
argument_list|(
name|conf
argument_list|,
name|keyTabKey
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// expected
name|gotException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Exception for empty keytabfile name was expected"
argument_list|,
name|gotException
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetHostFromPrincipal ()
specifier|public
name|void
name|testGetHostFromPrincipal
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"host"
argument_list|,
name|SecurityUtil
operator|.
name|getHostFromPrincipal
argument_list|(
literal|"service/host@realm"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|SecurityUtil
operator|.
name|getHostFromPrincipal
argument_list|(
literal|"service@realm"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBuildDTServiceName ()
specifier|public
name|void
name|testBuildDTServiceName
parameter_list|()
block|{
name|SecurityUtil
operator|.
name|setTokenServiceUseIp
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"127.0.0.1:123"
argument_list|,
name|SecurityUtil
operator|.
name|buildDTServiceName
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"test://LocalHost"
argument_list|)
argument_list|,
literal|123
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"127.0.0.1:123"
argument_list|,
name|SecurityUtil
operator|.
name|buildDTServiceName
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"test://LocalHost:123"
argument_list|)
argument_list|,
literal|456
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"127.0.0.1:123"
argument_list|,
name|SecurityUtil
operator|.
name|buildDTServiceName
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"test://127.0.0.1"
argument_list|)
argument_list|,
literal|123
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"127.0.0.1:123"
argument_list|,
name|SecurityUtil
operator|.
name|buildDTServiceName
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"test://127.0.0.1:123"
argument_list|)
argument_list|,
literal|456
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBuildTokenServiceSockAddr ()
specifier|public
name|void
name|testBuildTokenServiceSockAddr
parameter_list|()
block|{
name|SecurityUtil
operator|.
name|setTokenServiceUseIp
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"127.0.0.1:123"
argument_list|,
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"LocalHost"
argument_list|,
literal|123
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"127.0.0.1:123"
argument_list|,
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|123
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// what goes in, comes out
name|assertEquals
argument_list|(
literal|"127.0.0.1:123"
argument_list|,
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|123
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGoodHostsAndPorts ()
specifier|public
name|void
name|testGoodHostsAndPorts
parameter_list|()
block|{
name|InetSocketAddress
name|compare
init|=
name|NetUtils
operator|.
name|createSocketAddrForHost
argument_list|(
literal|"localhost"
argument_list|,
literal|123
argument_list|)
decl_stmt|;
name|runGoodCases
argument_list|(
name|compare
argument_list|,
literal|"localhost"
argument_list|,
literal|123
argument_list|)
expr_stmt|;
name|runGoodCases
argument_list|(
name|compare
argument_list|,
literal|"localhost:"
argument_list|,
literal|123
argument_list|)
expr_stmt|;
name|runGoodCases
argument_list|(
name|compare
argument_list|,
literal|"localhost:123"
argument_list|,
literal|456
argument_list|)
expr_stmt|;
block|}
DECL|method|runGoodCases (InetSocketAddress addr, String host, int port)
name|void
name|runGoodCases
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|,
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|addr
argument_list|,
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|addr
argument_list|,
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
literal|"hdfs://"
operator|+
name|host
argument_list|,
name|port
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|addr
argument_list|,
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
literal|"hdfs://"
operator|+
name|host
operator|+
literal|"/path"
argument_list|,
name|port
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBadHostsAndPorts ()
specifier|public
name|void
name|testBadHostsAndPorts
parameter_list|()
block|{
name|runBadCases
argument_list|(
literal|""
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|runBadCases
argument_list|(
literal|":"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|runBadCases
argument_list|(
literal|"hdfs/"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|runBadCases
argument_list|(
literal|"hdfs:/"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|runBadCases
argument_list|(
literal|"hdfs://"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|runBadCases (String prefix, boolean validIfPosPort)
name|void
name|runBadCases
parameter_list|(
name|String
name|prefix
parameter_list|,
name|boolean
name|validIfPosPort
parameter_list|)
block|{
name|runBadPortPermutes
argument_list|(
name|prefix
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|runBadPortPermutes
argument_list|(
name|prefix
operator|+
literal|"*"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|runBadPortPermutes
argument_list|(
name|prefix
operator|+
literal|"localhost"
argument_list|,
name|validIfPosPort
argument_list|)
expr_stmt|;
name|runBadPortPermutes
argument_list|(
name|prefix
operator|+
literal|"localhost:-1"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|runBadPortPermutes
argument_list|(
name|prefix
operator|+
literal|"localhost:-123"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|runBadPortPermutes
argument_list|(
name|prefix
operator|+
literal|"localhost:xyz"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|runBadPortPermutes
argument_list|(
name|prefix
operator|+
literal|"localhost/xyz"
argument_list|,
name|validIfPosPort
argument_list|)
expr_stmt|;
name|runBadPortPermutes
argument_list|(
name|prefix
operator|+
literal|"localhost/:123"
argument_list|,
name|validIfPosPort
argument_list|)
expr_stmt|;
name|runBadPortPermutes
argument_list|(
name|prefix
operator|+
literal|":123"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|runBadPortPermutes
argument_list|(
name|prefix
operator|+
literal|":xyz"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|runBadPortPermutes (String arg, boolean validIfPosPort)
name|void
name|runBadPortPermutes
parameter_list|(
name|String
name|arg
parameter_list|,
name|boolean
name|validIfPosPort
parameter_list|)
block|{
name|int
name|ports
index|[]
init|=
block|{
operator|-
literal|123
block|,
operator|-
literal|1
block|,
literal|123
block|}
decl_stmt|;
name|boolean
name|bad
init|=
literal|false
decl_stmt|;
try|try
block|{
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|bad
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|assertTrue
argument_list|(
literal|"should be bad: '"
operator|+
name|arg
operator|+
literal|"'"
argument_list|,
name|bad
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|port
range|:
name|ports
control|)
block|{
if|if
condition|(
name|validIfPosPort
operator|&&
name|port
operator|>
literal|0
condition|)
continue|continue;
name|bad
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|arg
argument_list|,
name|port
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|bad
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|assertTrue
argument_list|(
literal|"should be bad: '"
operator|+
name|arg
operator|+
literal|"' (default port:"
operator|+
name|port
operator|+
literal|")"
argument_list|,
name|bad
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// check that the socket addr has:
comment|// 1) the InetSocketAddress has the correct hostname, ie. exact host/ip given
comment|// 2) the address is resolved, ie. has an ip
comment|// 3,4) the socket's InetAddress has the same hostname, and the correct ip
comment|// 5) the port is correct
specifier|private
name|void
DECL|method|verifyValues (InetSocketAddress addr, String host, String ip, int port)
name|verifyValues
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|,
name|String
name|host
parameter_list|,
name|String
name|ip
parameter_list|,
name|int
name|port
parameter_list|)
block|{
name|assertTrue
argument_list|(
operator|!
name|addr
operator|.
name|isUnresolved
argument_list|()
argument_list|)
expr_stmt|;
comment|// don't know what the standard resolver will return for hostname.
comment|// should be host for host; host or ip for ip is ambiguous
if|if
condition|(
operator|!
name|SecurityUtil
operator|.
name|useIpForTokenService
condition|)
block|{
name|assertEquals
argument_list|(
name|host
argument_list|,
name|addr
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|host
argument_list|,
name|addr
operator|.
name|getAddress
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|ip
argument_list|,
name|addr
operator|.
name|getAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|port
argument_list|,
name|addr
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// check:
comment|// 1) buildTokenService honors use_ip setting
comment|// 2) setTokenService& getService works
comment|// 3) getTokenServiceAddr decodes to the identical socket addr
specifier|private
name|void
DECL|method|verifyTokenService (InetSocketAddress addr, String host, String ip, int port, boolean useIp)
name|verifyTokenService
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|,
name|String
name|host
parameter_list|,
name|String
name|ip
parameter_list|,
name|int
name|port
parameter_list|,
name|boolean
name|useIp
parameter_list|)
block|{
comment|//LOG.info("address:"+addr+" host:"+host+" ip:"+ip+" port:"+port);
name|SecurityUtil
operator|.
name|setTokenServiceUseIp
argument_list|(
name|useIp
argument_list|)
expr_stmt|;
name|String
name|serviceHost
init|=
name|useIp
condition|?
name|ip
else|:
name|host
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|Token
argument_list|<
name|?
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
argument_list|()
decl_stmt|;
name|Text
name|service
init|=
operator|new
name|Text
argument_list|(
name|serviceHost
operator|+
literal|":"
operator|+
name|port
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|service
argument_list|,
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|addr
argument_list|)
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
name|assertEquals
argument_list|(
name|service
argument_list|,
name|token
operator|.
name|getService
argument_list|()
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|serviceAddr
init|=
name|SecurityUtil
operator|.
name|getTokenServiceAddr
argument_list|(
name|token
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|serviceAddr
argument_list|)
expr_stmt|;
name|verifyValues
argument_list|(
name|serviceAddr
argument_list|,
name|serviceHost
argument_list|,
name|ip
argument_list|,
name|port
argument_list|)
expr_stmt|;
block|}
comment|// check:
comment|// 1) socket addr is created with fields set as expected
comment|// 2) token service with ips
comment|// 3) token service with the given host or ip
specifier|private
name|void
DECL|method|verifyAddress (InetSocketAddress addr, String host, String ip, int port)
name|verifyAddress
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|,
name|String
name|host
parameter_list|,
name|String
name|ip
parameter_list|,
name|int
name|port
parameter_list|)
block|{
name|verifyValues
argument_list|(
name|addr
argument_list|,
name|host
argument_list|,
name|ip
argument_list|,
name|port
argument_list|)
expr_stmt|;
comment|//LOG.info("test that token service uses ip");
name|verifyTokenService
argument_list|(
name|addr
argument_list|,
name|host
argument_list|,
name|ip
argument_list|,
name|port
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//LOG.info("test that token service uses host");
name|verifyTokenService
argument_list|(
name|addr
argument_list|,
name|host
argument_list|,
name|ip
argument_list|,
name|port
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// check:
comment|// 1-4) combinations of host and port
comment|// this will construct a socket addr, verify all the fields, build the
comment|// service to verify the use_ip setting is honored, set the token service
comment|// based on addr and verify the token service is set correctly, decode
comment|// the token service and ensure all the fields of the decoded addr match
DECL|method|verifyServiceAddr (String host, String ip)
specifier|private
name|void
name|verifyServiceAddr
parameter_list|(
name|String
name|host
parameter_list|,
name|String
name|ip
parameter_list|)
block|{
name|InetSocketAddress
name|addr
decl_stmt|;
name|int
name|port
init|=
literal|123
decl_stmt|;
comment|// test host, port tuple
comment|//LOG.info("test tuple ("+host+","+port+")");
name|addr
operator|=
name|NetUtils
operator|.
name|createSocketAddrForHost
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
expr_stmt|;
name|verifyAddress
argument_list|(
name|addr
argument_list|,
name|host
argument_list|,
name|ip
argument_list|,
name|port
argument_list|)
expr_stmt|;
comment|// test authority with no default port
comment|//LOG.info("test authority '"+host+":"+port+"'");
name|addr
operator|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|host
operator|+
literal|":"
operator|+
name|port
argument_list|)
expr_stmt|;
name|verifyAddress
argument_list|(
name|addr
argument_list|,
name|host
argument_list|,
name|ip
argument_list|,
name|port
argument_list|)
expr_stmt|;
comment|// test authority with a default port, make sure default isn't used
comment|//LOG.info("test authority '"+host+":"+port+"' with ignored default port");
name|addr
operator|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|host
operator|+
literal|":"
operator|+
name|port
argument_list|,
name|port
operator|+
literal|1
argument_list|)
expr_stmt|;
name|verifyAddress
argument_list|(
name|addr
argument_list|,
name|host
argument_list|,
name|ip
argument_list|,
name|port
argument_list|)
expr_stmt|;
comment|// test host-only authority, using port as default port
comment|//LOG.info("test host:"+host+" port:"+port);
name|addr
operator|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
expr_stmt|;
name|verifyAddress
argument_list|(
name|addr
argument_list|,
name|host
argument_list|,
name|ip
argument_list|,
name|port
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSocketAddrWithName ()
specifier|public
name|void
name|testSocketAddrWithName
parameter_list|()
block|{
name|String
name|staticHost
init|=
literal|"my"
decl_stmt|;
name|NetUtils
operator|.
name|addStaticResolution
argument_list|(
name|staticHost
argument_list|,
literal|"localhost"
argument_list|)
expr_stmt|;
name|verifyServiceAddr
argument_list|(
literal|"LocalHost"
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSocketAddrWithIP ()
specifier|public
name|void
name|testSocketAddrWithIP
parameter_list|()
block|{
name|verifyServiceAddr
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSocketAddrWithNameToStaticName ()
specifier|public
name|void
name|testSocketAddrWithNameToStaticName
parameter_list|()
block|{
name|String
name|staticHost
init|=
literal|"host1"
decl_stmt|;
name|NetUtils
operator|.
name|addStaticResolution
argument_list|(
name|staticHost
argument_list|,
literal|"localhost"
argument_list|)
expr_stmt|;
name|verifyServiceAddr
argument_list|(
name|staticHost
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSocketAddrWithNameToStaticIP ()
specifier|public
name|void
name|testSocketAddrWithNameToStaticIP
parameter_list|()
block|{
name|String
name|staticHost
init|=
literal|"host3"
decl_stmt|;
name|NetUtils
operator|.
name|addStaticResolution
argument_list|(
name|staticHost
argument_list|,
literal|"255.255.255.255"
argument_list|)
expr_stmt|;
name|verifyServiceAddr
argument_list|(
name|staticHost
argument_list|,
literal|"255.255.255.255"
argument_list|)
expr_stmt|;
block|}
comment|// this is a bizarre case, but it's if a test tries to remap an ip address
annotation|@
name|Test
DECL|method|testSocketAddrWithIPToStaticIP ()
specifier|public
name|void
name|testSocketAddrWithIPToStaticIP
parameter_list|()
block|{
name|String
name|staticHost
init|=
literal|"1.2.3.4"
decl_stmt|;
name|NetUtils
operator|.
name|addStaticResolution
argument_list|(
name|staticHost
argument_list|,
literal|"255.255.255.255"
argument_list|)
expr_stmt|;
name|verifyServiceAddr
argument_list|(
name|staticHost
argument_list|,
literal|"255.255.255.255"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetAuthenticationMethod ()
specifier|public
name|void
name|testGetAuthenticationMethod
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// default is simple
name|conf
operator|.
name|unset
argument_list|(
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SIMPLE
argument_list|,
name|SecurityUtil
operator|.
name|getAuthenticationMethod
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
comment|// simple
name|conf
operator|.
name|set
argument_list|(
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"simple"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SIMPLE
argument_list|,
name|SecurityUtil
operator|.
name|getAuthenticationMethod
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
comment|// kerberos
name|conf
operator|.
name|set
argument_list|(
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|KERBEROS
argument_list|,
name|SecurityUtil
operator|.
name|getAuthenticationMethod
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
comment|// bad value
name|conf
operator|.
name|set
argument_list|(
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"kaboom"
argument_list|)
expr_stmt|;
name|String
name|error
init|=
literal|null
decl_stmt|;
try|try
block|{
name|SecurityUtil
operator|.
name|getAuthenticationMethod
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|error
operator|=
name|e
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"java.lang.IllegalArgumentException: "
operator|+
literal|"Invalid attribute value for "
operator|+
name|HADOOP_SECURITY_AUTHENTICATION
operator|+
literal|" of kaboom"
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetAuthenticationMethod ()
specifier|public
name|void
name|testSetAuthenticationMethod
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// default
name|SecurityUtil
operator|.
name|setAuthenticationMethod
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"simple"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|)
argument_list|)
expr_stmt|;
comment|// simple
name|SecurityUtil
operator|.
name|setAuthenticationMethod
argument_list|(
name|SIMPLE
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"simple"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|)
argument_list|)
expr_stmt|;
comment|// kerberos
name|SecurityUtil
operator|.
name|setAuthenticationMethod
argument_list|(
name|KERBEROS
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"kerberos"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

