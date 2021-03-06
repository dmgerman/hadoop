begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
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
name|UnknownHostException
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
name|javax
operator|.
name|naming
operator|.
name|CommunicationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NameNotFoundException
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|PlatformAssumptions
operator|.
name|assumeNotWindows
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|not
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
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

begin_comment
comment|/**  * Test host name and IP resolution and caching.  */
end_comment

begin_class
DECL|class|TestDNS
specifier|public
class|class
name|TestDNS
block|{
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
name|TestDNS
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DEFAULT
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT
init|=
literal|"default"
decl_stmt|;
comment|// This is not a legal hostname (starts with a hyphen). It will never
comment|// be returned on any test machine.
DECL|field|DUMMY_HOSTNAME
specifier|private
specifier|static
specifier|final
name|String
name|DUMMY_HOSTNAME
init|=
literal|"-DUMMY_HOSTNAME"
decl_stmt|;
DECL|field|INVALID_DNS_SERVER
specifier|private
specifier|static
specifier|final
name|String
name|INVALID_DNS_SERVER
init|=
literal|"0.0.0.0"
decl_stmt|;
comment|/**    * Test that asking for the default hostname works    * @throws Exception if hostname lookups fail    */
annotation|@
name|Test
DECL|method|testGetLocalHost ()
specifier|public
name|void
name|testGetLocalHost
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|hostname
init|=
name|DNS
operator|.
name|getDefaultHost
argument_list|(
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|hostname
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that repeated calls to getting the local host are fairly fast, and    * hence that caching is being used    * @throws Exception if hostname lookups fail    */
annotation|@
name|Test
DECL|method|testGetLocalHostIsFast ()
specifier|public
name|void
name|testGetLocalHostIsFast
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|hostname1
init|=
name|DNS
operator|.
name|getDefaultHost
argument_list|(
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|hostname1
argument_list|)
expr_stmt|;
name|String
name|hostname2
init|=
name|DNS
operator|.
name|getDefaultHost
argument_list|(
name|DEFAULT
argument_list|)
decl_stmt|;
name|long
name|t1
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|String
name|hostname3
init|=
name|DNS
operator|.
name|getDefaultHost
argument_list|(
name|DEFAULT
argument_list|)
decl_stmt|;
name|long
name|t2
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|hostname3
argument_list|,
name|hostname2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hostname2
argument_list|,
name|hostname1
argument_list|)
expr_stmt|;
name|long
name|interval
init|=
name|t2
operator|-
name|t1
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Took too long to determine local host - caching is not working"
argument_list|,
name|interval
operator|<
literal|20000
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that our local IP address is not null    * @throws Exception if something went wrong    */
annotation|@
name|Test
DECL|method|testLocalHostHasAnAddress ()
specifier|public
name|void
name|testLocalHostHasAnAddress
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
name|getLocalIPAddr
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getLocalIPAddr ()
specifier|private
name|InetAddress
name|getLocalIPAddr
parameter_list|()
throws|throws
name|UnknownHostException
block|{
name|String
name|hostname
init|=
name|DNS
operator|.
name|getDefaultHost
argument_list|(
name|DEFAULT
argument_list|)
decl_stmt|;
name|InetAddress
name|localhost
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|hostname
argument_list|)
decl_stmt|;
return|return
name|localhost
return|;
block|}
comment|/**    * Test null interface name    */
annotation|@
name|Test
DECL|method|testNullInterface ()
specifier|public
name|void
name|testNullInterface
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|host
init|=
name|DNS
operator|.
name|getDefaultHost
argument_list|(
literal|null
argument_list|)
decl_stmt|;
comment|// should work.
name|assertThat
argument_list|(
name|host
argument_list|,
name|is
argument_list|(
name|DNS
operator|.
name|getDefaultHost
argument_list|(
name|DEFAULT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|String
name|ip
init|=
name|DNS
operator|.
name|getDefaultIP
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Expected a NullPointerException, got "
operator|+
name|ip
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
comment|// Expected
block|}
block|}
comment|/**    * Test that 'null' DNS server gives the same result as if no DNS    * server was passed.    */
annotation|@
name|Test
DECL|method|testNullDnsServer ()
specifier|public
name|void
name|testNullDnsServer
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|host
init|=
name|DNS
operator|.
name|getDefaultHost
argument_list|(
name|getLoopbackInterface
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|host
argument_list|,
name|is
argument_list|(
name|DNS
operator|.
name|getDefaultHost
argument_list|(
name|getLoopbackInterface
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that "default" DNS server gives the same result as if no DNS    * server was passed.    */
annotation|@
name|Test
DECL|method|testDefaultDnsServer ()
specifier|public
name|void
name|testDefaultDnsServer
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|host
init|=
name|DNS
operator|.
name|getDefaultHost
argument_list|(
name|getLoopbackInterface
argument_list|()
argument_list|,
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|host
argument_list|,
name|is
argument_list|(
name|DNS
operator|.
name|getDefaultHost
argument_list|(
name|getLoopbackInterface
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the IP addresses of an unknown interface    */
annotation|@
name|Test
DECL|method|testIPsOfUnknownInterface ()
specifier|public
name|void
name|testIPsOfUnknownInterface
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|DNS
operator|.
name|getIPs
argument_list|(
literal|"name-of-an-unknown-interface"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Got an IP for a bogus interface"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"No such interface name-of-an-unknown-interface"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test the "default" IP addresses is the local IP addr    */
annotation|@
name|Test
DECL|method|testGetIPWithDefault ()
specifier|public
name|void
name|testGetIPWithDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|ips
init|=
name|DNS
operator|.
name|getIPs
argument_list|(
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should only return 1 default IP"
argument_list|,
literal|1
argument_list|,
name|ips
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getLocalIPAddr
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|,
name|ips
index|[
literal|0
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|ip
init|=
name|DNS
operator|.
name|getDefaultIP
argument_list|(
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ip
argument_list|,
name|ips
index|[
literal|0
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * TestCase: get our local address and reverse look it up    */
annotation|@
name|Test
DECL|method|testRDNS ()
specifier|public
name|void
name|testRDNS
parameter_list|()
throws|throws
name|Exception
block|{
name|InetAddress
name|localhost
init|=
name|getLocalIPAddr
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|s
init|=
name|DNS
operator|.
name|reverseDns
argument_list|(
name|localhost
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Local reverse DNS hostname is "
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NameNotFoundException
decl||
name|CommunicationException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|localhost
operator|.
name|isLinkLocalAddress
argument_list|()
operator|||
name|localhost
operator|.
name|isLoopbackAddress
argument_list|()
condition|)
block|{
comment|//these addresses probably won't work with rDNS anyway, unless someone
comment|//has unusual entries in their DNS server mapping 1.0.0.127 to localhost
name|LOG
operator|.
name|info
argument_list|(
literal|"Reverse DNS failing as due to incomplete networking"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Address is "
operator|+
name|localhost
operator|+
literal|" Loopback="
operator|+
name|localhost
operator|.
name|isLoopbackAddress
argument_list|()
operator|+
literal|" Linklocal="
operator|+
name|localhost
operator|.
name|isLinkLocalAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Test that when using an invalid DNS server with hosts file fallback,    * we are able to get the hostname from the hosts file.    *    * This test may fail on some misconfigured test machines that don't have    * an entry for "localhost" in their hosts file. This entry is correctly    * configured out of the box on common Linux distributions and OS X.    *    * Windows refuses to resolve 127.0.0.1 to "localhost" despite the presence of    * this entry in the hosts file.  We skip the test on Windows to avoid    * reporting a spurious failure.    *    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testLookupWithHostsFallback ()
specifier|public
name|void
name|testLookupWithHostsFallback
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeNotWindows
argument_list|()
expr_stmt|;
specifier|final
name|String
name|oldHostname
init|=
name|changeDnsCachedHostname
argument_list|(
name|DUMMY_HOSTNAME
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|hostname
init|=
name|DNS
operator|.
name|getDefaultHost
argument_list|(
name|getLoopbackInterface
argument_list|()
argument_list|,
name|INVALID_DNS_SERVER
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// Expect to get back something other than the cached host name.
name|assertThat
argument_list|(
name|hostname
argument_list|,
name|not
argument_list|(
name|DUMMY_HOSTNAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// Restore DNS#cachedHostname for subsequent tests.
name|changeDnsCachedHostname
argument_list|(
name|oldHostname
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that when using an invalid DNS server without hosts file    * fallback, we get back the cached host name.    *    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testLookupWithoutHostsFallback ()
specifier|public
name|void
name|testLookupWithoutHostsFallback
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|oldHostname
init|=
name|changeDnsCachedHostname
argument_list|(
name|DUMMY_HOSTNAME
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|hostname
init|=
name|DNS
operator|.
name|getDefaultHost
argument_list|(
name|getLoopbackInterface
argument_list|()
argument_list|,
name|INVALID_DNS_SERVER
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// Expect to get back the cached host name since there was no hosts
comment|// file lookup.
name|assertThat
argument_list|(
name|hostname
argument_list|,
name|is
argument_list|(
name|DUMMY_HOSTNAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// Restore DNS#cachedHostname for subsequent tests.
name|changeDnsCachedHostname
argument_list|(
name|oldHostname
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getLoopbackInterface ()
specifier|private
name|String
name|getLoopbackInterface
parameter_list|()
throws|throws
name|SocketException
block|{
return|return
name|NetworkInterface
operator|.
name|getByInetAddress
argument_list|(
name|InetAddress
operator|.
name|getLoopbackAddress
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/**    * Change DNS#cachedHostName to something which cannot be a real    * host name. Uses reflection since it is a 'private final' field.    */
DECL|method|changeDnsCachedHostname (final String newHostname)
specifier|private
name|String
name|changeDnsCachedHostname
parameter_list|(
specifier|final
name|String
name|newHostname
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|String
name|oldCachedHostname
init|=
name|DNS
operator|.
name|getDefaultHost
argument_list|(
name|DEFAULT
argument_list|)
decl_stmt|;
name|Field
name|field
init|=
name|DNS
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"cachedHostname"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Field
name|modifiersField
init|=
name|Field
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"modifiers"
argument_list|)
decl_stmt|;
name|modifiersField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|modifiersField
operator|.
name|set
argument_list|(
name|field
argument_list|,
name|field
operator|.
name|getModifiers
argument_list|()
operator|&
operator|~
name|Modifier
operator|.
name|FINAL
argument_list|)
expr_stmt|;
name|field
operator|.
name|set
argument_list|(
literal|null
argument_list|,
name|newHostname
argument_list|)
expr_stmt|;
return|return
name|oldCachedHostname
return|;
block|}
comment|/**    * Test that the name "localhost" resolves to something.    *    * If this fails, your machine's network is in a mess, go edit /etc/hosts    */
annotation|@
name|Test
DECL|method|testLocalhostResolves ()
specifier|public
name|void
name|testLocalhostResolves
parameter_list|()
throws|throws
name|Exception
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
name|assertNotNull
argument_list|(
literal|"localhost is null"
argument_list|,
name|localhost
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Localhost IPAddr is "
operator|+
name|localhost
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

