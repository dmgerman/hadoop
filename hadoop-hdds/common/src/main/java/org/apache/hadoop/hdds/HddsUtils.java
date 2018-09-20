begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
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
name|base
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
import|;
end_import

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
name|HostAndPort
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
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
name|DNS
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|InetSocketAddress
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
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DNS_INTERFACE_KEY
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DNS_NAMESERVER_KEY
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HOST_NAME_KEY
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_ENABLED
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_ENABLED_DEFAULT
import|;
end_import

begin_comment
comment|/**  * HDDS specific stateless utility functions.  */
end_comment

begin_class
DECL|class|HddsUtils
specifier|public
specifier|final
class|class
name|HddsUtils
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
name|HddsUtils
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * The service ID of the solitary Ozone SCM service.    */
DECL|field|OZONE_SCM_SERVICE_ID
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_SERVICE_ID
init|=
literal|"OzoneScmService"
decl_stmt|;
DECL|field|OZONE_SCM_SERVICE_INSTANCE_ID
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_SCM_SERVICE_INSTANCE_ID
init|=
literal|"OzoneScmServiceInstance"
decl_stmt|;
DECL|field|NO_PORT
specifier|private
specifier|static
specifier|final
name|int
name|NO_PORT
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|HddsUtils ()
specifier|private
name|HddsUtils
parameter_list|()
block|{   }
comment|/**    * Retrieve the socket address that should be used by clients to connect    * to the SCM.    *    * @param conf    * @return Target InetSocketAddress for the SCM client endpoint.    */
DECL|method|getScmAddressForClients (Configuration conf)
specifier|public
specifier|static
name|InetSocketAddress
name|getScmAddressForClients
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|host
init|=
name|getHostNameFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|host
operator|.
name|isPresent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_ADDRESS_KEY
operator|+
literal|" must be defined. See"
operator|+
literal|" https://wiki.apache.org/hadoop/Ozone#Configuration for "
operator|+
literal|"details"
operator|+
literal|" on configuring Ozone."
argument_list|)
throw|;
block|}
specifier|final
name|Optional
argument_list|<
name|Integer
argument_list|>
name|port
init|=
name|getPortNumberFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|)
decl_stmt|;
return|return
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|host
operator|.
name|get
argument_list|()
operator|+
literal|":"
operator|+
name|port
operator|.
name|or
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_PORT_DEFAULT
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Retrieve the socket address that should be used by clients to connect    * to the SCM for block service. If    * {@link ScmConfigKeys#OZONE_SCM_BLOCK_CLIENT_ADDRESS_KEY} is not defined    * then {@link ScmConfigKeys#OZONE_SCM_CLIENT_ADDRESS_KEY} is used.    *    * @param conf    * @return Target InetSocketAddress for the SCM block client endpoint.    * @throws IllegalArgumentException if configuration is not defined.    */
DECL|method|getScmAddressForBlockClients ( Configuration conf)
specifier|public
specifier|static
name|InetSocketAddress
name|getScmAddressForBlockClients
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Optional
argument_list|<
name|String
argument_list|>
name|host
init|=
name|getHostNameFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_BLOCK_CLIENT_ADDRESS_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|host
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|host
operator|=
name|getHostNameFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_ADDRESS_KEY
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|host
operator|.
name|isPresent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_BLOCK_CLIENT_ADDRESS_KEY
operator|+
literal|" must be defined. See"
operator|+
literal|" https://wiki.apache.org/hadoop/Ozone#Configuration"
operator|+
literal|" for details on configuring Ozone."
argument_list|)
throw|;
block|}
block|}
specifier|final
name|Optional
argument_list|<
name|Integer
argument_list|>
name|port
init|=
name|getPortNumberFromConfigKeys
argument_list|(
name|conf
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_BLOCK_CLIENT_ADDRESS_KEY
argument_list|)
decl_stmt|;
return|return
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|host
operator|.
name|get
argument_list|()
operator|+
literal|":"
operator|+
name|port
operator|.
name|or
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_BLOCK_CLIENT_PORT_DEFAULT
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Retrieve the hostname, trying the supplied config keys in order.    * Each config value may be absent, or if present in the format    * host:port (the :port part is optional).    *    * @param conf  - Conf    * @param keys a list of configuration key names.    *    * @return first hostname component found from the given keys, or absent.    * @throws IllegalArgumentException if any values are not in the 'host'    *             or host:port format.    */
DECL|method|getHostNameFromConfigKeys (Configuration conf, String... keys)
specifier|public
specifier|static
name|Optional
argument_list|<
name|String
argument_list|>
name|getHostNameFromConfigKeys
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
modifier|...
name|keys
parameter_list|)
block|{
for|for
control|(
specifier|final
name|String
name|key
range|:
name|keys
control|)
block|{
specifier|final
name|String
name|value
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|key
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|hostName
init|=
name|getHostName
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|hostName
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
name|hostName
return|;
block|}
block|}
return|return
name|Optional
operator|.
name|absent
argument_list|()
return|;
block|}
comment|/**    * Gets the hostname or Indicates that it is absent.    * @param value host or host:port    * @return hostname    */
DECL|method|getHostName (String value)
specifier|public
specifier|static
name|Optional
argument_list|<
name|String
argument_list|>
name|getHostName
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
operator|(
name|value
operator|==
literal|null
operator|)
operator|||
name|value
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Optional
operator|.
name|absent
argument_list|()
return|;
block|}
return|return
name|Optional
operator|.
name|of
argument_list|(
name|HostAndPort
operator|.
name|fromString
argument_list|(
name|value
argument_list|)
operator|.
name|getHostText
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Gets the port if there is one, throws otherwise.    * @param value  String in host:port format.    * @return Port    */
DECL|method|getHostPort (String value)
specifier|public
specifier|static
name|Optional
argument_list|<
name|Integer
argument_list|>
name|getHostPort
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
operator|(
name|value
operator|==
literal|null
operator|)
operator|||
name|value
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Optional
operator|.
name|absent
argument_list|()
return|;
block|}
name|int
name|port
init|=
name|HostAndPort
operator|.
name|fromString
argument_list|(
name|value
argument_list|)
operator|.
name|getPortOrDefault
argument_list|(
name|NO_PORT
argument_list|)
decl_stmt|;
if|if
condition|(
name|port
operator|==
name|NO_PORT
condition|)
block|{
return|return
name|Optional
operator|.
name|absent
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
name|port
argument_list|)
return|;
block|}
block|}
comment|/**    * Retrieve the port number, trying the supplied config keys in order.    * Each config value may be absent, or if present in the format    * host:port (the :port part is optional).    *    * @param conf Conf    * @param keys a list of configuration key names.    *    * @return first port number component found from the given keys, or absent.    * @throws IllegalArgumentException if any values are not in the 'host'    *             or host:port format.    */
DECL|method|getPortNumberFromConfigKeys ( Configuration conf, String... keys)
specifier|public
specifier|static
name|Optional
argument_list|<
name|Integer
argument_list|>
name|getPortNumberFromConfigKeys
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
modifier|...
name|keys
parameter_list|)
block|{
for|for
control|(
specifier|final
name|String
name|key
range|:
name|keys
control|)
block|{
specifier|final
name|String
name|value
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|key
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Integer
argument_list|>
name|hostPort
init|=
name|getHostPort
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|hostPort
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
name|hostPort
return|;
block|}
block|}
return|return
name|Optional
operator|.
name|absent
argument_list|()
return|;
block|}
comment|/**    * Retrieve the socket addresses of all storage container managers.    *    * @param conf    * @return A collection of SCM addresses    * @throws IllegalArgumentException If the configuration is invalid    */
DECL|method|getSCMAddresses ( Configuration conf)
specifier|public
specifier|static
name|Collection
argument_list|<
name|InetSocketAddress
argument_list|>
name|getSCMAddresses
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|Collection
argument_list|<
name|InetSocketAddress
argument_list|>
name|addresses
init|=
operator|new
name|HashSet
argument_list|<
name|InetSocketAddress
argument_list|>
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|names
init|=
name|conf
operator|.
name|getTrimmedStringCollection
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_NAMES
argument_list|)
decl_stmt|;
if|if
condition|(
name|names
operator|==
literal|null
operator|||
name|names
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_NAMES
operator|+
literal|" need to be a set of valid DNS names or IP addresses."
operator|+
literal|" Null or empty address list found."
argument_list|)
throw|;
block|}
specifier|final
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Optional
argument_list|<
name|Integer
argument_list|>
name|defaultPort
init|=
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Optional
operator|.
name|of
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DEFAULT_PORT
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|address
range|:
name|names
control|)
block|{
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Optional
argument_list|<
name|String
argument_list|>
name|hostname
init|=
name|getHostName
argument_list|(
name|address
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|hostname
operator|.
name|isPresent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid hostname for SCM: "
operator|+
name|hostname
argument_list|)
throw|;
block|}
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Optional
argument_list|<
name|Integer
argument_list|>
name|port
init|=
name|getHostPort
argument_list|(
name|address
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|hostname
operator|.
name|get
argument_list|()
argument_list|,
name|port
operator|.
name|or
argument_list|(
name|defaultPort
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|addresses
operator|.
name|add
argument_list|(
name|addr
argument_list|)
expr_stmt|;
block|}
return|return
name|addresses
return|;
block|}
DECL|method|isHddsEnabled (Configuration conf)
specifier|public
specifier|static
name|boolean
name|isHddsEnabled
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|securityEnabled
init|=
name|conf
operator|.
name|get
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"simple"
argument_list|)
decl_stmt|;
name|boolean
name|securityAuthorizationEnabled
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTHORIZATION
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|securityEnabled
operator|.
name|equals
argument_list|(
literal|"kerberos"
argument_list|)
operator|||
name|securityAuthorizationEnabled
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Ozone is not supported in a security enabled cluster. "
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|conf
operator|.
name|getBoolean
argument_list|(
name|OZONE_ENABLED
argument_list|,
name|OZONE_ENABLED_DEFAULT
argument_list|)
return|;
block|}
block|}
comment|/**    * Get the path for datanode id file.    *    * @param conf - Configuration    * @return the path of datanode id as string    */
DECL|method|getDatanodeIdFilePath (Configuration conf)
specifier|public
specifier|static
name|String
name|getDatanodeIdFilePath
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|dataNodeIDPath
init|=
name|conf
operator|.
name|get
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DATANODE_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|dataNodeIDPath
operator|==
literal|null
condition|)
block|{
name|String
name|metaPath
init|=
name|conf
operator|.
name|get
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|metaPath
argument_list|)
condition|)
block|{
comment|// this means meta data is not found, in theory should not happen at
comment|// this point because should've failed earlier.
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unable to locate meta data"
operator|+
literal|"directory when getting datanode id path"
argument_list|)
throw|;
block|}
name|dataNodeIDPath
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|metaPath
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DATANODE_ID_PATH_DEFAULT
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|dataNodeIDPath
return|;
block|}
comment|/**    * Returns the hostname for this datanode. If the hostname is not    * explicitly configured in the given config, then it is determined    * via the DNS class.    *    * @param conf Configuration    *    * @return the hostname (NB: may not be a FQDN)    * @throws UnknownHostException if the dfs.datanode.dns.interface    *    option is used and the hostname can not be determined    */
DECL|method|getHostName (Configuration conf)
specifier|public
specifier|static
name|String
name|getHostName
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|UnknownHostException
block|{
name|String
name|name
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFS_DATANODE_HOST_NAME_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|String
name|dnsInterface
init|=
name|conf
operator|.
name|get
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_DNS_INTERFACE_KEY
argument_list|)
decl_stmt|;
name|String
name|nameServer
init|=
name|conf
operator|.
name|get
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_DNS_NAMESERVER_KEY
argument_list|)
decl_stmt|;
name|boolean
name|fallbackToHosts
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|dnsInterface
operator|==
literal|null
condition|)
block|{
comment|// Try the legacy configuration keys.
name|dnsInterface
operator|=
name|conf
operator|.
name|get
argument_list|(
name|DFS_DATANODE_DNS_INTERFACE_KEY
argument_list|)
expr_stmt|;
name|nameServer
operator|=
name|conf
operator|.
name|get
argument_list|(
name|DFS_DATANODE_DNS_NAMESERVER_KEY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// If HADOOP_SECURITY_DNS_* is set then also attempt hosts file
comment|// resolution if DNS fails. We will not use hosts file resolution
comment|// by default to avoid breaking existing clusters.
name|fallbackToHosts
operator|=
literal|true
expr_stmt|;
block|}
name|name
operator|=
name|DNS
operator|.
name|getDefaultHost
argument_list|(
name|dnsInterface
argument_list|,
name|nameServer
argument_list|,
name|fallbackToHosts
argument_list|)
expr_stmt|;
block|}
return|return
name|name
return|;
block|}
comment|/**    * Checks if the container command is read only or not.    * @param proto ContainerCommand Request proto    * @return True if its readOnly , false otherwise.    */
DECL|method|isReadOnly ( ContainerProtos.ContainerCommandRequestProto proto)
specifier|public
specifier|static
name|boolean
name|isReadOnly
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|proto
parameter_list|)
block|{
switch|switch
condition|(
name|proto
operator|.
name|getCmdType
argument_list|()
condition|)
block|{
case|case
name|ReadContainer
case|:
case|case
name|ReadChunk
case|:
case|case
name|ListBlock
case|:
case|case
name|GetBlock
case|:
case|case
name|GetSmallFile
case|:
case|case
name|ListContainer
case|:
case|case
name|ListChunk
case|:
case|case
name|GetCommittedBlockLength
case|:
return|return
literal|true
return|;
case|case
name|CloseContainer
case|:
case|case
name|WriteChunk
case|:
case|case
name|UpdateContainer
case|:
case|case
name|CompactChunk
case|:
case|case
name|CreateContainer
case|:
case|case
name|DeleteChunk
case|:
case|case
name|DeleteContainer
case|:
case|case
name|DeleteBlock
case|:
case|case
name|PutBlock
case|:
case|case
name|PutSmallFile
case|:
default|default:
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

