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
name|java
operator|.
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Subject
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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|kerberos
operator|.
name|KerberosTicket
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
name|net
operator|.
name|NetUtils
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|security
operator|.
name|jgss
operator|.
name|krb5
operator|.
name|Krb5Util
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|security
operator|.
name|krb5
operator|.
name|Credentials
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|security
operator|.
name|krb5
operator|.
name|PrincipalName
import|;
end_import

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
name|Evolving
DECL|class|SecurityUtil
specifier|public
class|class
name|SecurityUtil
block|{
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
name|SecurityUtil
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|HOSTNAME_PATTERN
specifier|public
specifier|static
specifier|final
name|String
name|HOSTNAME_PATTERN
init|=
literal|"_HOST"
decl_stmt|;
comment|/**    * Find the original TGT within the current subject's credentials. Cross-realm    * TGT's of the form "krbtgt/TWO.COM@ONE.COM" may be present.    *     * @return The TGT from the current subject    * @throws IOException    *           if TGT can't be found    */
DECL|method|getTgtFromSubject ()
specifier|private
specifier|static
name|KerberosTicket
name|getTgtFromSubject
parameter_list|()
throws|throws
name|IOException
block|{
name|Subject
name|current
init|=
name|Subject
operator|.
name|getSubject
argument_list|(
name|AccessController
operator|.
name|getContext
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't get TGT from current Subject, because it is null"
argument_list|)
throw|;
block|}
name|Set
argument_list|<
name|KerberosTicket
argument_list|>
name|tickets
init|=
name|current
operator|.
name|getPrivateCredentials
argument_list|(
name|KerberosTicket
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|KerberosTicket
name|t
range|:
name|tickets
control|)
block|{
if|if
condition|(
name|isOriginalTGT
argument_list|(
name|t
argument_list|)
condition|)
return|return
name|t
return|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to find TGT from current Subject"
argument_list|)
throw|;
block|}
comment|/**    * TGS must have the server principal of the form "krbtgt/FOO@FOO".    * @param principal    * @return true or false    */
specifier|static
name|boolean
DECL|method|isTGSPrincipal (KerberosPrincipal principal)
name|isTGSPrincipal
parameter_list|(
name|KerberosPrincipal
name|principal
parameter_list|)
block|{
if|if
condition|(
name|principal
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|principal
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"krbtgt/"
operator|+
name|principal
operator|.
name|getRealm
argument_list|()
operator|+
literal|"@"
operator|+
name|principal
operator|.
name|getRealm
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Check whether the server principal is the TGS's principal    * @param ticket the original TGT (the ticket that is obtained when a     * kinit is done)    * @return true or false    */
DECL|method|isOriginalTGT (KerberosTicket ticket)
specifier|protected
specifier|static
name|boolean
name|isOriginalTGT
parameter_list|(
name|KerberosTicket
name|ticket
parameter_list|)
block|{
return|return
name|isTGSPrincipal
argument_list|(
name|ticket
operator|.
name|getServer
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Explicitly pull the service ticket for the specified host.  This solves a    * problem with Java's Kerberos SSL problem where the client cannot     * authenticate against a cross-realm service.  It is necessary for clients    * making kerberized https requests to call this method on the target URL    * to ensure that in a cross-realm environment the remote host will be     * successfully authenticated.      *     * This method is internal to Hadoop and should not be used by other     * applications.  This method should not be considered stable or open:     * it will be removed when the Java behavior is changed.    *     * @param remoteHost Target URL the krb-https client will access    * @throws IOException    */
DECL|method|fetchServiceTicket (URL remoteHost)
specifier|public
specifier|static
name|void
name|fetchServiceTicket
parameter_list|(
name|URL
name|remoteHost
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
return|return;
name|String
name|serviceName
init|=
literal|"host/"
operator|+
name|remoteHost
operator|.
name|getHost
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Fetching service ticket for host at: "
operator|+
name|serviceName
argument_list|)
expr_stmt|;
name|Credentials
name|serviceCred
init|=
literal|null
decl_stmt|;
try|try
block|{
name|PrincipalName
name|principal
init|=
operator|new
name|PrincipalName
argument_list|(
name|serviceName
argument_list|,
name|PrincipalName
operator|.
name|KRB_NT_SRV_HST
argument_list|)
decl_stmt|;
name|serviceCred
operator|=
name|Credentials
operator|.
name|acquireServiceCreds
argument_list|(
name|principal
operator|.
name|toString
argument_list|()
argument_list|,
name|Krb5Util
operator|.
name|ticketToCreds
argument_list|(
name|getTgtFromSubject
argument_list|()
argument_list|)
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
name|IOException
argument_list|(
literal|"Can't get service ticket for: "
operator|+
name|serviceName
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|serviceCred
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't get service ticket for "
operator|+
name|serviceName
argument_list|)
throw|;
block|}
name|Subject
operator|.
name|getSubject
argument_list|(
name|AccessController
operator|.
name|getContext
argument_list|()
argument_list|)
operator|.
name|getPrivateCredentials
argument_list|()
operator|.
name|add
argument_list|(
name|Krb5Util
operator|.
name|credsToTicket
argument_list|(
name|serviceCred
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Convert Kerberos principal name pattern to valid Kerberos principal    * names. It replaces hostname pattern with hostname, which should be    * fully-qualified domain name. If hostname is null or "0.0.0.0", it uses    * dynamically looked-up fqdn of the current host instead.    *     * @param principalConfig    *          the Kerberos principal name conf value to convert    * @param hostname    *          the fully-qualified domain name used for substitution    * @return converted Kerberos principal name    * @throws IOException    */
DECL|method|getServerPrincipal (String principalConfig, String hostname)
specifier|public
specifier|static
name|String
name|getServerPrincipal
parameter_list|(
name|String
name|principalConfig
parameter_list|,
name|String
name|hostname
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|components
init|=
name|getComponents
argument_list|(
name|principalConfig
argument_list|)
decl_stmt|;
if|if
condition|(
name|components
operator|==
literal|null
operator|||
name|components
operator|.
name|length
operator|!=
literal|3
operator|||
operator|!
name|components
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
name|HOSTNAME_PATTERN
argument_list|)
condition|)
block|{
return|return
name|principalConfig
return|;
block|}
else|else
block|{
return|return
name|replacePattern
argument_list|(
name|components
argument_list|,
name|hostname
argument_list|)
return|;
block|}
block|}
comment|/**    * Convert Kerberos principal name pattern to valid Kerberos principal names.    * This method is similar to {@link #getServerPrincipal(String, String)},    * except 1) the reverse DNS lookup from addr to hostname is done only when    * necessary, 2) param addr can't be null (no default behavior of using local    * hostname when addr is null).    *     * @param principalConfig    *          Kerberos principal name pattern to convert    * @param addr    *          InetAddress of the host used for substitution    * @return converted Kerberos principal name    * @throws IOException    */
DECL|method|getServerPrincipal (String principalConfig, InetAddress addr)
specifier|public
specifier|static
name|String
name|getServerPrincipal
parameter_list|(
name|String
name|principalConfig
parameter_list|,
name|InetAddress
name|addr
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|components
init|=
name|getComponents
argument_list|(
name|principalConfig
argument_list|)
decl_stmt|;
if|if
condition|(
name|components
operator|==
literal|null
operator|||
name|components
operator|.
name|length
operator|!=
literal|3
operator|||
operator|!
name|components
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
name|HOSTNAME_PATTERN
argument_list|)
condition|)
block|{
return|return
name|principalConfig
return|;
block|}
else|else
block|{
if|if
condition|(
name|addr
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't replace "
operator|+
name|HOSTNAME_PATTERN
operator|+
literal|" pattern since client address is null"
argument_list|)
throw|;
block|}
return|return
name|replacePattern
argument_list|(
name|components
argument_list|,
name|addr
operator|.
name|getCanonicalHostName
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|getComponents (String principalConfig)
specifier|private
specifier|static
name|String
index|[]
name|getComponents
parameter_list|(
name|String
name|principalConfig
parameter_list|)
block|{
if|if
condition|(
name|principalConfig
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|principalConfig
operator|.
name|split
argument_list|(
literal|"[/@]"
argument_list|)
return|;
block|}
DECL|method|replacePattern (String[] components, String hostname)
specifier|private
specifier|static
name|String
name|replacePattern
parameter_list|(
name|String
index|[]
name|components
parameter_list|,
name|String
name|hostname
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fqdn
init|=
name|hostname
decl_stmt|;
if|if
condition|(
name|fqdn
operator|==
literal|null
operator|||
name|fqdn
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|||
name|fqdn
operator|.
name|equals
argument_list|(
literal|"0.0.0.0"
argument_list|)
condition|)
block|{
name|fqdn
operator|=
name|getLocalHostName
argument_list|()
expr_stmt|;
block|}
return|return
name|components
index|[
literal|0
index|]
operator|+
literal|"/"
operator|+
name|fqdn
operator|+
literal|"@"
operator|+
name|components
index|[
literal|2
index|]
return|;
block|}
DECL|method|getLocalHostName ()
specifier|static
name|String
name|getLocalHostName
parameter_list|()
throws|throws
name|UnknownHostException
block|{
return|return
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getCanonicalHostName
argument_list|()
return|;
block|}
comment|/**    * Login as a principal specified in config. Substitute $host in    * user's Kerberos principal name with a dynamically looked-up fully-qualified    * domain name of the current host.    *     * @param conf    *          conf to use    * @param keytabFileKey    *          the key to look for keytab file in conf    * @param userNameKey    *          the key to look for user's Kerberos principal name in conf    * @throws IOException    */
DECL|method|login (final Configuration conf, final String keytabFileKey, final String userNameKey)
specifier|public
specifier|static
name|void
name|login
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|String
name|keytabFileKey
parameter_list|,
specifier|final
name|String
name|userNameKey
parameter_list|)
throws|throws
name|IOException
block|{
name|login
argument_list|(
name|conf
argument_list|,
name|keytabFileKey
argument_list|,
name|userNameKey
argument_list|,
name|getLocalHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Login as a principal specified in config. Substitute $host in user's Kerberos principal     * name with hostname. If non-secure mode - return. If no keytab available -    * bail out with an exception    *     * @param conf    *          conf to use    * @param keytabFileKey    *          the key to look for keytab file in conf    * @param userNameKey    *          the key to look for user's Kerberos principal name in conf    * @param hostname    *          hostname to use for substitution    * @throws IOException    */
DECL|method|login (final Configuration conf, final String keytabFileKey, final String userNameKey, String hostname)
specifier|public
specifier|static
name|void
name|login
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|String
name|keytabFileKey
parameter_list|,
specifier|final
name|String
name|userNameKey
parameter_list|,
name|String
name|hostname
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
return|return;
name|String
name|keytabFilename
init|=
name|conf
operator|.
name|get
argument_list|(
name|keytabFileKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|keytabFilename
operator|==
literal|null
operator|||
name|keytabFilename
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Running in secure mode, but config doesn't have a keytab"
argument_list|)
throw|;
block|}
name|String
name|principalConfig
init|=
name|conf
operator|.
name|get
argument_list|(
name|userNameKey
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|principalName
init|=
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
name|principalConfig
argument_list|,
name|hostname
argument_list|)
decl_stmt|;
name|UserGroupInformation
operator|.
name|loginUserFromKeytab
argument_list|(
name|principalName
argument_list|,
name|keytabFilename
argument_list|)
expr_stmt|;
block|}
comment|/**    * create service name for Delegation token ip:port    * @param uri    * @param defPort    * @return "ip:port"    */
DECL|method|buildDTServiceName (URI uri, int defPort)
specifier|public
specifier|static
name|String
name|buildDTServiceName
parameter_list|(
name|URI
name|uri
parameter_list|,
name|int
name|defPort
parameter_list|)
block|{
name|int
name|port
init|=
name|uri
operator|.
name|getPort
argument_list|()
decl_stmt|;
if|if
condition|(
name|port
operator|==
operator|-
literal|1
condition|)
name|port
operator|=
name|defPort
expr_stmt|;
comment|// build the service name string "/ip:port"
comment|// for whatever reason using NetUtils.createSocketAddr(target).toString()
comment|// returns "localhost/ip:port"
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|String
name|host
init|=
name|uri
operator|.
name|getHost
argument_list|()
decl_stmt|;
if|if
condition|(
name|host
operator|!=
literal|null
condition|)
block|{
name|host
operator|=
name|NetUtils
operator|.
name|normalizeHostName
argument_list|(
name|host
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|host
operator|=
literal|""
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|host
argument_list|)
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
operator|.
name|append
argument_list|(
name|port
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Get the host name from the principal name of format<service>/host@realm.    * @param principalName principal name of format as described above    * @return host name if the the string conforms to the above format, else null    */
DECL|method|getHostFromPrincipal (String principalName)
specifier|public
specifier|static
name|String
name|getHostFromPrincipal
parameter_list|(
name|String
name|principalName
parameter_list|)
block|{
return|return
operator|new
name|KerberosName
argument_list|(
name|principalName
argument_list|)
operator|.
name|getHostName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

