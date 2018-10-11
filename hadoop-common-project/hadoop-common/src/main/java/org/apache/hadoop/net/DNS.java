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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|net
operator|.
name|InetAddresses
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|istack
operator|.
name|Nullable
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
name|Collections
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
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|Attributes
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|DirContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|InitialDirContext
import|;
end_import

begin_comment
comment|/**  *   * A class that provides direct and reverse lookup functionalities, allowing  * the querying of specific network interfaces or nameservers.  *   *   */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|DNS
specifier|public
class|class
name|DNS
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
name|DNS
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * The cached hostname -initially null.    */
DECL|field|cachedHostname
specifier|private
specifier|static
specifier|final
name|String
name|cachedHostname
init|=
name|resolveLocalHostname
argument_list|()
decl_stmt|;
DECL|field|cachedHostAddress
specifier|private
specifier|static
specifier|final
name|String
name|cachedHostAddress
init|=
name|resolveLocalHostIPAddress
argument_list|()
decl_stmt|;
DECL|field|LOCALHOST
specifier|private
specifier|static
specifier|final
name|String
name|LOCALHOST
init|=
literal|"localhost"
decl_stmt|;
comment|/**    * Returns the hostname associated with the specified IP address by the    * provided nameserver.    *    * Loopback addresses     * @param hostIp The address to reverse lookup    * @param ns The host name of a reachable DNS server    * @return The host name associated with the provided IP    * @throws NamingException If a NamingException is encountered    */
DECL|method|reverseDns (InetAddress hostIp, @Nullable String ns)
specifier|public
specifier|static
name|String
name|reverseDns
parameter_list|(
name|InetAddress
name|hostIp
parameter_list|,
annotation|@
name|Nullable
name|String
name|ns
parameter_list|)
throws|throws
name|NamingException
block|{
comment|//
comment|// Builds the reverse IP lookup form
comment|// This is formed by reversing the IP numbers and appending in-addr.arpa
comment|//
name|String
index|[]
name|parts
init|=
name|hostIp
operator|.
name|getHostAddress
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
decl_stmt|;
name|String
name|reverseIP
init|=
name|parts
index|[
literal|3
index|]
operator|+
literal|"."
operator|+
name|parts
index|[
literal|2
index|]
operator|+
literal|"."
operator|+
name|parts
index|[
literal|1
index|]
operator|+
literal|"."
operator|+
name|parts
index|[
literal|0
index|]
operator|+
literal|".in-addr.arpa"
decl_stmt|;
name|DirContext
name|ictx
init|=
operator|new
name|InitialDirContext
argument_list|()
decl_stmt|;
name|Attributes
name|attribute
decl_stmt|;
try|try
block|{
name|attribute
operator|=
name|ictx
operator|.
name|getAttributes
argument_list|(
literal|"dns://"
comment|// Use "dns:///" if the default
operator|+
operator|(
operator|(
name|ns
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|ns
operator|)
operator|+
comment|// nameserver is to be used
literal|"/"
operator|+
name|reverseIP
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"PTR"
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|ictx
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|String
name|hostname
init|=
name|attribute
operator|.
name|get
argument_list|(
literal|"PTR"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|hostnameLength
init|=
name|hostname
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|hostname
operator|.
name|charAt
argument_list|(
name|hostnameLength
operator|-
literal|1
argument_list|)
operator|==
literal|'.'
condition|)
block|{
name|hostname
operator|=
name|hostname
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|hostnameLength
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|hostname
return|;
block|}
comment|/**    * @return NetworkInterface for the given subinterface name (eg eth0:0)    *    or null if no interface with the given name can be found      */
DECL|method|getSubinterface (String strInterface)
specifier|private
specifier|static
name|NetworkInterface
name|getSubinterface
parameter_list|(
name|String
name|strInterface
parameter_list|)
throws|throws
name|SocketException
block|{
name|Enumeration
argument_list|<
name|NetworkInterface
argument_list|>
name|nifs
init|=
name|NetworkInterface
operator|.
name|getNetworkInterfaces
argument_list|()
decl_stmt|;
while|while
condition|(
name|nifs
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Enumeration
argument_list|<
name|NetworkInterface
argument_list|>
name|subNifs
init|=
name|nifs
operator|.
name|nextElement
argument_list|()
operator|.
name|getSubInterfaces
argument_list|()
decl_stmt|;
while|while
condition|(
name|subNifs
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|NetworkInterface
name|nif
init|=
name|subNifs
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|nif
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|strInterface
argument_list|)
condition|)
block|{
return|return
name|nif
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * @param nif network interface to get addresses for    * @return set containing addresses for each subinterface of nif,    *    see below for the rationale for using an ordered set    */
DECL|method|getSubinterfaceInetAddrs ( NetworkInterface nif)
specifier|private
specifier|static
name|LinkedHashSet
argument_list|<
name|InetAddress
argument_list|>
name|getSubinterfaceInetAddrs
parameter_list|(
name|NetworkInterface
name|nif
parameter_list|)
block|{
name|LinkedHashSet
argument_list|<
name|InetAddress
argument_list|>
name|addrs
init|=
operator|new
name|LinkedHashSet
argument_list|<
name|InetAddress
argument_list|>
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|NetworkInterface
argument_list|>
name|subNifs
init|=
name|nif
operator|.
name|getSubInterfaces
argument_list|()
decl_stmt|;
while|while
condition|(
name|subNifs
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|NetworkInterface
name|subNif
init|=
name|subNifs
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|addrs
operator|.
name|addAll
argument_list|(
name|Collections
operator|.
name|list
argument_list|(
name|subNif
operator|.
name|getInetAddresses
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|addrs
return|;
block|}
comment|/**    * Like {@link DNS#getIPs(String, boolean)}, but returns all    * IPs associated with the given interface and its subinterfaces.    */
DECL|method|getIPs (String strInterface)
specifier|public
specifier|static
name|String
index|[]
name|getIPs
parameter_list|(
name|String
name|strInterface
parameter_list|)
throws|throws
name|UnknownHostException
block|{
return|return
name|getIPs
argument_list|(
name|strInterface
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Returns all the IPs associated with the provided interface, if any, in    * textual form.    *     * @param strInterface    *            The name of the network interface or sub-interface to query    *            (eg eth0 or eth0:0) or the string "default"    * @param returnSubinterfaces    *            Whether to return IPs associated with subinterfaces of    *            the given interface    * @return A string vector of all the IPs associated with the provided    *         interface. The local host IP is returned if the interface    *         name "default" is specified or there is an I/O error looking    *         for the given interface.    * @throws UnknownHostException    *             If the given interface is invalid    *     */
DECL|method|getIPs (String strInterface, boolean returnSubinterfaces)
specifier|public
specifier|static
name|String
index|[]
name|getIPs
parameter_list|(
name|String
name|strInterface
parameter_list|,
name|boolean
name|returnSubinterfaces
parameter_list|)
throws|throws
name|UnknownHostException
block|{
if|if
condition|(
literal|"default"
operator|.
name|equals
argument_list|(
name|strInterface
argument_list|)
condition|)
block|{
return|return
operator|new
name|String
index|[]
block|{
name|cachedHostAddress
block|}
return|;
block|}
name|NetworkInterface
name|netIf
decl_stmt|;
try|try
block|{
name|netIf
operator|=
name|NetworkInterface
operator|.
name|getByName
argument_list|(
name|strInterface
argument_list|)
expr_stmt|;
if|if
condition|(
name|netIf
operator|==
literal|null
condition|)
block|{
name|netIf
operator|=
name|getSubinterface
argument_list|(
name|strInterface
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SocketException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"I/O error finding interface {}"
argument_list|,
name|strInterface
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
index|[]
block|{
name|cachedHostAddress
block|}
return|;
block|}
if|if
condition|(
name|netIf
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnknownHostException
argument_list|(
literal|"No such interface "
operator|+
name|strInterface
argument_list|)
throw|;
block|}
comment|// NB: Using a LinkedHashSet to preserve the order for callers
comment|// that depend on a particular element being 1st in the array.
comment|// For example, getDefaultIP always returns the first element.
name|LinkedHashSet
argument_list|<
name|InetAddress
argument_list|>
name|allAddrs
init|=
operator|new
name|LinkedHashSet
argument_list|<
name|InetAddress
argument_list|>
argument_list|()
decl_stmt|;
name|allAddrs
operator|.
name|addAll
argument_list|(
name|Collections
operator|.
name|list
argument_list|(
name|netIf
operator|.
name|getInetAddresses
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|returnSubinterfaces
condition|)
block|{
name|allAddrs
operator|.
name|removeAll
argument_list|(
name|getSubinterfaceInetAddrs
argument_list|(
name|netIf
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|ips
index|[]
init|=
operator|new
name|String
index|[
name|allAddrs
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|InetAddress
name|addr
range|:
name|allAddrs
control|)
block|{
name|ips
index|[
name|i
operator|++
index|]
operator|=
name|addr
operator|.
name|getHostAddress
argument_list|()
expr_stmt|;
block|}
return|return
name|ips
return|;
block|}
comment|/**    * Returns the first available IP address associated with the provided    * network interface or the local host IP if "default" is given.    *    * @param strInterface    *            The name of the network interface or subinterface to query    *             (e.g. eth0 or eth0:0) or the string "default"    * @return The IP address in text form, the local host IP is returned    *         if the interface name "default" is specified    * @throws UnknownHostException    *             If the given interface is invalid    */
DECL|method|getDefaultIP (String strInterface)
specifier|public
specifier|static
name|String
name|getDefaultIP
parameter_list|(
name|String
name|strInterface
parameter_list|)
throws|throws
name|UnknownHostException
block|{
name|String
index|[]
name|ips
init|=
name|getIPs
argument_list|(
name|strInterface
argument_list|)
decl_stmt|;
return|return
name|ips
index|[
literal|0
index|]
return|;
block|}
comment|/**    * Returns all the host names associated by the provided nameserver with the    * address bound to the specified network interface    *    * @param strInterface    *            The name of the network interface or subinterface to query    *            (e.g. eth0 or eth0:0)    * @param nameserver    *            The DNS host name    * @param tryfallbackResolution    *            if true and if reverse DNS resolution fails then attempt to    *            resolve the hostname with    *            {@link InetAddress#getCanonicalHostName()} which includes    *            hosts file resolution.    * @return A string vector of all host names associated with the IPs tied to    *         the specified interface    * @throws UnknownHostException if the given interface is invalid    */
DECL|method|getHosts (String strInterface, @Nullable String nameserver, boolean tryfallbackResolution)
specifier|public
specifier|static
name|String
index|[]
name|getHosts
parameter_list|(
name|String
name|strInterface
parameter_list|,
annotation|@
name|Nullable
name|String
name|nameserver
parameter_list|,
name|boolean
name|tryfallbackResolution
parameter_list|)
throws|throws
name|UnknownHostException
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|hosts
init|=
operator|new
name|Vector
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|InetAddress
argument_list|>
name|addresses
init|=
name|getIPsAsInetAddressList
argument_list|(
name|strInterface
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|InetAddress
name|address
range|:
name|addresses
control|)
block|{
try|try
block|{
name|hosts
operator|.
name|add
argument_list|(
name|reverseDns
argument_list|(
name|address
argument_list|,
name|nameserver
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|ignored
parameter_list|)
block|{       }
block|}
if|if
condition|(
name|hosts
operator|.
name|isEmpty
argument_list|()
operator|&&
name|tryfallbackResolution
condition|)
block|{
for|for
control|(
name|InetAddress
name|address
range|:
name|addresses
control|)
block|{
specifier|final
name|String
name|canonicalHostName
init|=
name|address
operator|.
name|getCanonicalHostName
argument_list|()
decl_stmt|;
comment|// Don't use the result if it looks like an IP address.
if|if
condition|(
operator|!
name|InetAddresses
operator|.
name|isInetAddress
argument_list|(
name|canonicalHostName
argument_list|)
condition|)
block|{
name|hosts
operator|.
name|add
argument_list|(
name|canonicalHostName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|hosts
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to determine hostname for interface {}"
argument_list|,
name|strInterface
argument_list|)
expr_stmt|;
name|hosts
operator|.
name|add
argument_list|(
name|cachedHostname
argument_list|)
expr_stmt|;
block|}
return|return
name|hosts
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|hosts
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**    * Determine the local hostname; retrieving it from cache if it is known    * If we cannot determine our host name, return "localhost"    * @return the local hostname or "localhost"    */
DECL|method|resolveLocalHostname ()
specifier|private
specifier|static
name|String
name|resolveLocalHostname
parameter_list|()
block|{
name|String
name|localhost
decl_stmt|;
try|try
block|{
name|localhost
operator|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getCanonicalHostName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to determine local hostname -falling back to '{}'"
argument_list|,
name|LOCALHOST
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|localhost
operator|=
name|LOCALHOST
expr_stmt|;
block|}
return|return
name|localhost
return|;
block|}
comment|/**    * Get the IPAddress of the local host as a string.    * This will be a loop back value if the local host address cannot be    * determined.    * If the loopback address of "localhost" does not resolve, then the system's    * network is in such a state that nothing is going to work. A message is    * logged at the error level and a null pointer returned, a pointer    * which will trigger failures later on the application    * @return the IPAddress of the local host or null for a serious problem.    */
DECL|method|resolveLocalHostIPAddress ()
specifier|private
specifier|static
name|String
name|resolveLocalHostIPAddress
parameter_list|()
block|{
name|String
name|address
decl_stmt|;
try|try
block|{
name|address
operator|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostAddress
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to determine address of the host "
operator|+
literal|"-falling back to '{}' address"
argument_list|,
name|LOCALHOST
argument_list|,
name|e
argument_list|)
expr_stmt|;
try|try
block|{
name|address
operator|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|LOCALHOST
argument_list|)
operator|.
name|getHostAddress
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|noLocalHostAddressException
parameter_list|)
block|{
comment|//at this point, deep trouble
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to determine local loopback address of '{}' "
operator|+
literal|"-this system's network configuration is unsupported"
argument_list|,
name|LOCALHOST
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|address
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
name|address
return|;
block|}
comment|/**    * Returns all the host names associated by the default nameserver with the    * address bound to the specified network interface    *     * @param strInterface    *            The name of the network interface to query (e.g. eth0)    * @return The list of host names associated with IPs bound to the network    *         interface    * @throws UnknownHostException    *             If one is encountered while querying the default interface    *     */
DECL|method|getHosts (String strInterface)
specifier|public
specifier|static
name|String
index|[]
name|getHosts
parameter_list|(
name|String
name|strInterface
parameter_list|)
throws|throws
name|UnknownHostException
block|{
return|return
name|getHosts
argument_list|(
name|strInterface
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Returns the default (first) host name associated by the provided    * nameserver with the address bound to the specified network interface    *     * @param strInterface    *            The name of the network interface to query (e.g. eth0)    * @param nameserver    *            The DNS host name    * @return The default host names associated with IPs bound to the network    *         interface    * @throws UnknownHostException    *             If one is encountered while querying the default interface    */
DECL|method|getDefaultHost (@ullable String strInterface, @Nullable String nameserver, boolean tryfallbackResolution)
specifier|public
specifier|static
name|String
name|getDefaultHost
parameter_list|(
annotation|@
name|Nullable
name|String
name|strInterface
parameter_list|,
annotation|@
name|Nullable
name|String
name|nameserver
parameter_list|,
name|boolean
name|tryfallbackResolution
parameter_list|)
throws|throws
name|UnknownHostException
block|{
if|if
condition|(
name|strInterface
operator|==
literal|null
operator|||
literal|"default"
operator|.
name|equals
argument_list|(
name|strInterface
argument_list|)
condition|)
block|{
return|return
name|cachedHostname
return|;
block|}
if|if
condition|(
name|nameserver
operator|!=
literal|null
operator|&&
literal|"default"
operator|.
name|equals
argument_list|(
name|nameserver
argument_list|)
condition|)
block|{
name|nameserver
operator|=
literal|null
expr_stmt|;
block|}
name|String
index|[]
name|hosts
init|=
name|getHosts
argument_list|(
name|strInterface
argument_list|,
name|nameserver
argument_list|,
name|tryfallbackResolution
argument_list|)
decl_stmt|;
return|return
name|hosts
index|[
literal|0
index|]
return|;
block|}
comment|/**    * Returns the default (first) host name associated by the default    * nameserver with the address bound to the specified network interface    *     * @param strInterface    *            The name of the network interface to query (e.g. eth0).    *            Must not be null.    * @return The default host name associated with IPs bound to the network    *         interface    * @throws UnknownHostException    *             If one is encountered while querying the default interface    */
DECL|method|getDefaultHost (@ullable String strInterface)
specifier|public
specifier|static
name|String
name|getDefaultHost
parameter_list|(
annotation|@
name|Nullable
name|String
name|strInterface
parameter_list|)
throws|throws
name|UnknownHostException
block|{
return|return
name|getDefaultHost
argument_list|(
name|strInterface
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Returns the default (first) host name associated by the provided    * nameserver with the address bound to the specified network interface.    *    * @param strInterface    *            The name of the network interface to query (e.g. eth0)    * @param nameserver    *            The DNS host name    * @throws UnknownHostException    *             If one is encountered while querying the default interface    */
DECL|method|getDefaultHost (@ullable String strInterface, @Nullable String nameserver)
specifier|public
specifier|static
name|String
name|getDefaultHost
parameter_list|(
annotation|@
name|Nullable
name|String
name|strInterface
parameter_list|,
annotation|@
name|Nullable
name|String
name|nameserver
parameter_list|)
throws|throws
name|UnknownHostException
block|{
return|return
name|getDefaultHost
argument_list|(
name|strInterface
argument_list|,
name|nameserver
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Returns all the IPs associated with the provided interface, if any, as    * a list of InetAddress objects.    *    * @param strInterface    *            The name of the network interface or sub-interface to query    *            (eg eth0 or eth0:0) or the string "default"    * @param returnSubinterfaces    *            Whether to return IPs associated with subinterfaces of    *            the given interface    * @return A list of all the IPs associated with the provided    *         interface. The local host IP is returned if the interface    *         name "default" is specified or there is an I/O error looking    *         for the given interface.    * @throws UnknownHostException    *             If the given interface is invalid    *    */
DECL|method|getIPsAsInetAddressList (String strInterface, boolean returnSubinterfaces)
specifier|public
specifier|static
name|List
argument_list|<
name|InetAddress
argument_list|>
name|getIPsAsInetAddressList
parameter_list|(
name|String
name|strInterface
parameter_list|,
name|boolean
name|returnSubinterfaces
parameter_list|)
throws|throws
name|UnknownHostException
block|{
if|if
condition|(
literal|"default"
operator|.
name|equals
argument_list|(
name|strInterface
argument_list|)
condition|)
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|InetAddress
operator|.
name|getByName
argument_list|(
name|cachedHostAddress
argument_list|)
argument_list|)
return|;
block|}
name|NetworkInterface
name|netIf
decl_stmt|;
try|try
block|{
name|netIf
operator|=
name|NetworkInterface
operator|.
name|getByName
argument_list|(
name|strInterface
argument_list|)
expr_stmt|;
if|if
condition|(
name|netIf
operator|==
literal|null
condition|)
block|{
name|netIf
operator|=
name|getSubinterface
argument_list|(
name|strInterface
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SocketException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"I/O error finding interface {}: {}"
argument_list|,
name|strInterface
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|InetAddress
operator|.
name|getByName
argument_list|(
name|cachedHostAddress
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|netIf
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnknownHostException
argument_list|(
literal|"No such interface "
operator|+
name|strInterface
argument_list|)
throw|;
block|}
comment|// NB: Using a LinkedHashSet to preserve the order for callers
comment|// that depend on a particular element being 1st in the array.
comment|// For example, getDefaultIP always returns the first element.
name|LinkedHashSet
argument_list|<
name|InetAddress
argument_list|>
name|allAddrs
init|=
operator|new
name|LinkedHashSet
argument_list|<
name|InetAddress
argument_list|>
argument_list|()
decl_stmt|;
name|allAddrs
operator|.
name|addAll
argument_list|(
name|Collections
operator|.
name|list
argument_list|(
name|netIf
operator|.
name|getInetAddresses
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|returnSubinterfaces
condition|)
block|{
name|allAddrs
operator|.
name|removeAll
argument_list|(
name|getSubinterfaceInetAddrs
argument_list|(
name|netIf
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Vector
argument_list|<
name|InetAddress
argument_list|>
argument_list|(
name|allAddrs
argument_list|)
return|;
block|}
block|}
end_class

end_unit

