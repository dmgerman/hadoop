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
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
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
name|Method
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
name|util
operator|.
name|Calendar
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
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
name|protocolPB
operator|.
name|SCMSecurityProtocolClientSideTranslatorPB
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
name|protocolPB
operator|.
name|SCMSecurityProtocolPB
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|SCMSecurityProtocol
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
name|protocolPB
operator|.
name|ScmBlockLocationProtocolPB
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|ipc
operator|.
name|Client
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
name|ipc
operator|.
name|ProtobufRpcEngine
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
name|ipc
operator|.
name|RPC
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
name|metrics2
operator|.
name|MetricsSystem
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
name|metrics2
operator|.
name|lib
operator|.
name|DefaultMetricsSystem
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
name|metrics2
operator|.
name|source
operator|.
name|JvmMetrics
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
name|metrics2
operator|.
name|util
operator|.
name|MBeans
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
comment|/**  * HDDS specific stateless utility functions.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Stable
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
DECL|field|UTC_ZONE
specifier|private
specifier|static
specifier|final
name|TimeZone
name|UTC_ZONE
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"UTC"
argument_list|)
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
comment|// Fallback to Ozone SCM names.
name|Collection
argument_list|<
name|InetSocketAddress
argument_list|>
name|scmAddresses
init|=
name|getSCMAddresses
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|scmAddresses
operator|.
name|size
argument_list|()
operator|>
literal|1
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
literal|" must contain a single hostname. Multiple SCM hosts are "
operator|+
literal|"currently unsupported"
argument_list|)
throw|;
block|}
name|host
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|scmAddresses
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|orElse
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CLIENT_PORT_DEFAULT
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Retrieve the socket address that should be used by clients to connect    * to the SCM for block service. If    * {@link ScmConfigKeys#OZONE_SCM_BLOCK_CLIENT_ADDRESS_KEY} is not defined    * then {@link ScmConfigKeys#OZONE_SCM_CLIENT_ADDRESS_KEY} is used. If neither    * is defined then {@link ScmConfigKeys#OZONE_SCM_NAMES} is used.    *    * @param conf    * @return Target InetSocketAddress for the SCM block client endpoint.    * @throws IllegalArgumentException if configuration is not defined.    */
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
block|}
if|if
condition|(
operator|!
name|host
operator|.
name|isPresent
argument_list|()
condition|)
block|{
comment|// Fallback to Ozone SCM names.
name|Collection
argument_list|<
name|InetSocketAddress
argument_list|>
name|scmAddresses
init|=
name|getSCMAddresses
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|scmAddresses
operator|.
name|size
argument_list|()
operator|>
literal|1
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
literal|" must contain a single hostname. Multiple SCM hosts are "
operator|+
literal|"currently unsupported"
argument_list|)
throw|;
block|}
name|host
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|scmAddresses
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|orElse
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_BLOCK_CLIENT_PORT_DEFAULT
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Create a scm security client.    * @param conf    - Ozone configuration.    * @param address - inet socket address of scm.    *    * @return {@link SCMSecurityProtocol}    * @throws IOException    */
DECL|method|getScmSecurityClient ( OzoneConfiguration conf, InetSocketAddress address)
specifier|public
specifier|static
name|SCMSecurityProtocolClientSideTranslatorPB
name|getScmSecurityClient
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|InetSocketAddress
name|address
parameter_list|)
throws|throws
name|IOException
block|{
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|SCMSecurityProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|long
name|scmVersion
init|=
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|ScmBlockLocationProtocolPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|SCMSecurityProtocolClientSideTranslatorPB
name|scmSecurityClient
init|=
operator|new
name|SCMSecurityProtocolClientSideTranslatorPB
argument_list|(
name|RPC
operator|.
name|getProxy
argument_list|(
name|SCMSecurityProtocolPB
operator|.
name|class
argument_list|,
name|scmVersion
argument_list|,
name|address
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
name|conf
argument_list|,
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|conf
argument_list|)
argument_list|,
name|Client
operator|.
name|getRpcTimeout
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|scmSecurityClient
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
name|empty
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
name|empty
argument_list|()
return|;
block|}
name|String
name|hostname
init|=
name|value
operator|.
name|replaceAll
argument_list|(
literal|"\\:[0-9]+$"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|hostname
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|Optional
operator|.
name|empty
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
name|hostname
argument_list|)
return|;
block|}
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
name|empty
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
name|empty
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
name|empty
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
name|Optional
argument_list|<
name|Integer
argument_list|>
name|defaultPort
init|=
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
name|orElse
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
comment|/**    * Register the provided MBean with additional JMX ObjectName properties.    * If additional properties are not supported then fallback to registering    * without properties.    *    * @param serviceName - see {@link MBeans#register}    * @param mBeanName - see {@link MBeans#register}    * @param jmxProperties - additional JMX ObjectName properties.    * @param mBean - the MBean to register.    * @return the named used to register the MBean.    */
DECL|method|registerWithJmxProperties ( String serviceName, String mBeanName, Map<String, String> jmxProperties, Object mBean)
specifier|public
specifier|static
name|ObjectName
name|registerWithJmxProperties
parameter_list|(
name|String
name|serviceName
parameter_list|,
name|String
name|mBeanName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|jmxProperties
parameter_list|,
name|Object
name|mBean
parameter_list|)
block|{
try|try
block|{
comment|// Check support for registering with additional properties.
specifier|final
name|Method
name|registerMethod
init|=
name|MBeans
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"register"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|Map
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
operator|(
name|ObjectName
operator|)
name|registerMethod
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
name|serviceName
argument_list|,
name|mBeanName
argument_list|,
name|jmxProperties
argument_list|,
name|mBean
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
decl||
name|IllegalAccessException
decl||
name|InvocationTargetException
name|e
parameter_list|)
block|{
comment|// Fallback
name|LOG
operator|.
name|trace
argument_list|(
literal|"Registering MBean {} without additional properties {}"
argument_list|,
name|mBeanName
argument_list|,
name|jmxProperties
argument_list|)
expr_stmt|;
return|return
name|MBeans
operator|.
name|register
argument_list|(
name|serviceName
argument_list|,
name|mBeanName
argument_list|,
name|mBean
argument_list|)
return|;
block|}
block|}
comment|/**    * Get the current UTC time in milliseconds.    * @return the current UTC time in milliseconds.    */
DECL|method|getUtcTime ()
specifier|public
specifier|static
name|long
name|getUtcTime
parameter_list|()
block|{
return|return
name|Calendar
operator|.
name|getInstance
argument_list|(
name|UTC_ZONE
argument_list|)
operator|.
name|getTimeInMillis
argument_list|()
return|;
block|}
comment|/**    * Retrieve the socket address that should be used by clients to connect    * to the SCM for    * {@link org.apache.hadoop.hdds.protocol.SCMSecurityProtocol}. If    * {@link ScmConfigKeys#OZONE_SCM_SECURITY_SERVICE_ADDRESS_KEY} is not defined    * then {@link ScmConfigKeys#OZONE_SCM_CLIENT_ADDRESS_KEY} is used. If neither    * is defined then {@link ScmConfigKeys#OZONE_SCM_NAMES} is used.    *    * @param conf    * @return Target InetSocketAddress for the SCM block client endpoint.    * @throws IllegalArgumentException if configuration is not defined.    */
DECL|method|getScmAddressForSecurityProtocol ( Configuration conf)
specifier|public
specifier|static
name|InetSocketAddress
name|getScmAddressForSecurityProtocol
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
name|OZONE_SCM_SECURITY_SERVICE_ADDRESS_KEY
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
block|}
if|if
condition|(
operator|!
name|host
operator|.
name|isPresent
argument_list|()
condition|)
block|{
comment|// Fallback to Ozone SCM names.
name|Collection
argument_list|<
name|InetSocketAddress
argument_list|>
name|scmAddresses
init|=
name|getSCMAddresses
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|scmAddresses
operator|.
name|size
argument_list|()
operator|>
literal|1
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
literal|" must contain a single hostname. Multiple SCM hosts are "
operator|+
literal|"currently unsupported"
argument_list|)
throw|;
block|}
name|host
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|scmAddresses
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|OZONE_SCM_SECURITY_SERVICE_ADDRESS_KEY
operator|+
literal|" must be defined. See"
operator|+
literal|" https://wiki.apache.org/hadoop/Ozone#Configuration"
operator|+
literal|" for details on configuring Ozone."
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
name|OZONE_SCM_SECURITY_SERVICE_PORT_KEY
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
name|orElse
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_SECURITY_SERVICE_PORT_DEFAULT
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Initialize hadoop metrics systen for Ozone servers.    *  @param configuration OzoneConfiguration to use.    * @param serverName    The logical name of the server components. (eg.    * @return    */
DECL|method|initializeMetrics (OzoneConfiguration configuration, String serverName)
specifier|public
specifier|static
name|MetricsSystem
name|initializeMetrics
parameter_list|(
name|OzoneConfiguration
name|configuration
parameter_list|,
name|String
name|serverName
parameter_list|)
block|{
name|MetricsSystem
name|metricsSystem
init|=
name|DefaultMetricsSystem
operator|.
name|initialize
argument_list|(
name|serverName
argument_list|)
decl_stmt|;
name|JvmMetrics
operator|.
name|create
argument_list|(
name|serverName
argument_list|,
name|configuration
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_METRICS_SESSION_ID_KEY
argument_list|)
argument_list|,
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|metricsSystem
return|;
block|}
block|}
end_class

end_unit

