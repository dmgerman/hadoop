begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.server.dns
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|server
operator|.
name|dns
package|;
end_package

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
name|PathNotFoundException
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
name|registry
operator|.
name|client
operator|.
name|binding
operator|.
name|RegistryPathUtils
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|AddressTypes
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|Endpoint
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|ServiceRecord
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xbill
operator|.
name|DNS
operator|.
name|Name
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xbill
operator|.
name|DNS
operator|.
name|ReverseMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xbill
operator|.
name|DNS
operator|.
name|TextParseException
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
name|Inet6Address
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
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Provides common service record processing logic.  */
end_comment

begin_class
DECL|class|BaseServiceRecordProcessor
specifier|public
specifier|abstract
class|class
name|BaseServiceRecordProcessor
implements|implements
name|ServiceRecordProcessor
block|{
DECL|field|zoneSelctor
specifier|private
specifier|final
name|ZoneSelector
name|zoneSelctor
decl_stmt|;
DECL|field|typeToDescriptorMap
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|RecordDescriptor
argument_list|>
argument_list|>
name|typeToDescriptorMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|domain
specifier|private
name|String
name|domain
decl_stmt|;
DECL|field|YARN_SERVICE_API_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|YARN_SERVICE_API_PREFIX
init|=
literal|"classpath:org.apache.hadoop.yarn.service."
decl_stmt|;
DECL|field|HTTP_API_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|HTTP_API_TYPE
init|=
literal|"http://"
decl_stmt|;
comment|/**    * Creates a service record processor.    *    * @param record       the service record.    * @param path         the node path for the record in the registry.    * @param domain       the target DNS domain for the service record    *                     associated DNS records.    * @param zoneSelector A selector of the best zone for a given DNS name.    * @throws Exception if an issue is generated during instantiation.    */
DECL|method|BaseServiceRecordProcessor (ServiceRecord record, String path, String domain, ZoneSelector zoneSelector)
specifier|public
name|BaseServiceRecordProcessor
parameter_list|(
name|ServiceRecord
name|record
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|domain
parameter_list|,
name|ZoneSelector
name|zoneSelector
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|setPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|domain
operator|=
name|domain
expr_stmt|;
name|this
operator|.
name|zoneSelctor
operator|=
name|zoneSelector
expr_stmt|;
name|initTypeToInfoMapping
argument_list|(
name|record
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return the IPv6 mapped address for the provided IPv4 address. Utilized    * to create corresponding AAAA records.    *    * @param address the IPv4 address.    * @return the mapped IPv6 address.    * @throws UnknownHostException    */
DECL|method|getIpv6Address (InetAddress address)
specifier|static
name|InetAddress
name|getIpv6Address
parameter_list|(
name|InetAddress
name|address
parameter_list|)
throws|throws
name|UnknownHostException
block|{
name|String
index|[]
name|octets
init|=
name|address
operator|.
name|getHostAddress
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
decl_stmt|;
name|byte
index|[]
name|octetBytes
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
operator|++
name|i
control|)
block|{
name|octetBytes
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|octets
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|ipv4asIpV6addr
init|=
operator|new
name|byte
index|[
literal|16
index|]
decl_stmt|;
name|ipv4asIpV6addr
index|[
literal|10
index|]
operator|=
operator|(
name|byte
operator|)
literal|0xff
expr_stmt|;
name|ipv4asIpV6addr
index|[
literal|11
index|]
operator|=
operator|(
name|byte
operator|)
literal|0xff
expr_stmt|;
name|ipv4asIpV6addr
index|[
literal|12
index|]
operator|=
name|octetBytes
index|[
literal|0
index|]
expr_stmt|;
name|ipv4asIpV6addr
index|[
literal|13
index|]
operator|=
name|octetBytes
index|[
literal|1
index|]
expr_stmt|;
name|ipv4asIpV6addr
index|[
literal|14
index|]
operator|=
name|octetBytes
index|[
literal|2
index|]
expr_stmt|;
name|ipv4asIpV6addr
index|[
literal|15
index|]
operator|=
name|octetBytes
index|[
literal|3
index|]
expr_stmt|;
return|return
name|Inet6Address
operator|.
name|getByAddress
argument_list|(
literal|null
argument_list|,
name|ipv4asIpV6addr
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * Reverse the string representation of the input IP address.    *    * @param ip the string representation of the IP address.    * @return the reversed IP address.    * @throws UnknownHostException if the ip is unknown.    */
DECL|method|reverseIP (String ip)
specifier|protected
name|Name
name|reverseIP
parameter_list|(
name|String
name|ip
parameter_list|)
throws|throws
name|UnknownHostException
block|{
return|return
name|ReverseMap
operator|.
name|fromAddress
argument_list|(
name|ip
argument_list|)
return|;
block|}
comment|/**    * Manages the creation and registration of service record generated DNS    * records.    *    * @param command the DNS registration command object (e.g. add_record,    *                remove record)    * @throws IOException if the creation or registration generates an issue.    */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|method|manageDNSRecords (RegistryDNS.RegistryCommand command)
specifier|public
name|void
name|manageDNSRecords
parameter_list|(
name|RegistryDNS
operator|.
name|RegistryCommand
name|command
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|RecordDescriptor
argument_list|>
argument_list|>
name|entry
range|:
name|typeToDescriptorMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|RecordDescriptor
name|recordDescriptor
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
for|for
control|(
name|Name
name|name
range|:
name|recordDescriptor
operator|.
name|getNames
argument_list|()
control|)
block|{
name|RecordCreatorFactory
operator|.
name|RecordCreator
name|recordCreator
init|=
name|RecordCreatorFactory
operator|.
name|getRecordCreator
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|exec
argument_list|(
name|zoneSelctor
operator|.
name|findBestZone
argument_list|(
name|name
argument_list|)
argument_list|,
name|recordCreator
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|recordDescriptor
operator|.
name|getTarget
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Add the DNS record descriptor object to the record type to descriptor    * mapping.    *    * @param type             the DNS record type.    * @param recordDescriptor the DNS record descriptor    */
DECL|method|registerRecordDescriptor (int type, RecordDescriptor recordDescriptor)
specifier|protected
name|void
name|registerRecordDescriptor
parameter_list|(
name|int
name|type
parameter_list|,
name|RecordDescriptor
name|recordDescriptor
parameter_list|)
block|{
name|List
argument_list|<
name|RecordDescriptor
argument_list|>
name|infos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|infos
operator|.
name|add
argument_list|(
name|recordDescriptor
argument_list|)
expr_stmt|;
name|typeToDescriptorMap
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|infos
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add the DNS record descriptor objects to the record type to descriptor    * mapping.    *    * @param type              the DNS record type.    * @param recordDescriptors the DNS record descriptors    */
DECL|method|registerRecordDescriptor (int type, List<RecordDescriptor> recordDescriptors)
specifier|protected
name|void
name|registerRecordDescriptor
parameter_list|(
name|int
name|type
parameter_list|,
name|List
argument_list|<
name|RecordDescriptor
argument_list|>
name|recordDescriptors
parameter_list|)
block|{
name|typeToDescriptorMap
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|recordDescriptors
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return the path associated with the record.    * @return the path.    */
DECL|method|getPath ()
specifier|protected
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
comment|/**    * Set the path associated with the record.    * @param path the path.    */
DECL|method|setPath (String path)
specifier|protected
name|void
name|setPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
comment|/**    * A descriptor container the information to be populated into a DNS record.    *    * @param<T> the DNS record type/class.    */
DECL|class|RecordDescriptor
specifier|abstract
class|class
name|RecordDescriptor
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|record
specifier|private
specifier|final
name|ServiceRecord
name|record
decl_stmt|;
DECL|field|names
specifier|private
name|Name
index|[]
name|names
decl_stmt|;
DECL|field|target
specifier|private
name|T
name|target
decl_stmt|;
comment|/**      * Creates a DNS record descriptor.      *      * @param record the associated service record.      */
DECL|method|RecordDescriptor (ServiceRecord record)
specifier|public
name|RecordDescriptor
parameter_list|(
name|ServiceRecord
name|record
parameter_list|)
block|{
name|this
operator|.
name|record
operator|=
name|record
expr_stmt|;
block|}
comment|/**      * Returns the DNS names associated with the record type and information.      *      * @return the array of names.      */
DECL|method|getNames ()
specifier|public
name|Name
index|[]
name|getNames
parameter_list|()
block|{
return|return
name|names
return|;
block|}
comment|/**      * Return the target object for the DNS record.      *      * @return the DNS record target.      */
DECL|method|getTarget ()
specifier|public
name|T
name|getTarget
parameter_list|()
block|{
return|return
name|target
return|;
block|}
comment|/**      * Initializes the names and information for this DNS record descriptor.      *      * @param serviceRecord the service record.      * @throws Exception      */
DECL|method|init (ServiceRecord serviceRecord)
specifier|protected
specifier|abstract
name|void
name|init
parameter_list|(
name|ServiceRecord
name|serviceRecord
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Returns the service record.      * @return the service record.      */
DECL|method|getRecord ()
specifier|public
name|ServiceRecord
name|getRecord
parameter_list|()
block|{
return|return
name|record
return|;
block|}
comment|/**      * Sets the names associated with the record type and information.      * @param names the names.      */
DECL|method|setNames (Name[] names)
specifier|public
name|void
name|setNames
parameter_list|(
name|Name
index|[]
name|names
parameter_list|)
block|{
name|this
operator|.
name|names
operator|=
name|names
expr_stmt|;
block|}
comment|/**      * Sets the target object associated with the record.      * @param target the target.      */
DECL|method|setTarget (T target)
specifier|public
name|void
name|setTarget
parameter_list|(
name|T
name|target
parameter_list|)
block|{
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
block|}
block|}
comment|/**    * A container-based DNS record descriptor.    *    * @param<T> the DNS record type/class.    */
DECL|class|ContainerRecordDescriptor
specifier|abstract
class|class
name|ContainerRecordDescriptor
parameter_list|<
name|T
parameter_list|>
extends|extends
name|RecordDescriptor
argument_list|<
name|T
argument_list|>
block|{
DECL|method|ContainerRecordDescriptor (String path, ServiceRecord record)
specifier|public
name|ContainerRecordDescriptor
parameter_list|(
name|String
name|path
parameter_list|,
name|ServiceRecord
name|record
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|record
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|record
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the DNS name constructed from the YARN container ID.      *      * @return the container ID name.      * @throws TextParseException      */
DECL|method|getContainerIDName ()
specifier|protected
name|Name
name|getContainerIDName
parameter_list|()
throws|throws
name|TextParseException
block|{
name|String
name|containerID
init|=
name|RegistryPathUtils
operator|.
name|lastPathEntry
argument_list|(
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Name
operator|.
name|fromString
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s.%s"
argument_list|,
name|containerID
argument_list|,
name|domain
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns the DNS name constructed from the container role/component name.      *      * @return the DNS naem.      * @throws PathNotFoundException      * @throws TextParseException      */
DECL|method|getContainerName ()
specifier|protected
name|Name
name|getContainerName
parameter_list|()
throws|throws
name|PathNotFoundException
throws|,
name|TextParseException
block|{
name|String
name|service
init|=
name|RegistryPathUtils
operator|.
name|lastPathEntry
argument_list|(
name|RegistryPathUtils
operator|.
name|parentOf
argument_list|(
name|RegistryPathUtils
operator|.
name|parentOf
argument_list|(
name|getPath
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|description
init|=
name|getRecord
argument_list|()
operator|.
name|description
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|String
name|user
init|=
name|RegistryPathUtils
operator|.
name|getUsername
argument_list|(
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Name
operator|.
name|fromString
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"{0}.{1}.{2}.{3}"
argument_list|,
name|description
argument_list|,
name|service
argument_list|,
name|user
argument_list|,
name|domain
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Return the DNS name constructed from the component name.      *      * @return the DNS naem.      * @throws PathNotFoundException      * @throws TextParseException      */
DECL|method|getComponentName ()
specifier|protected
name|Name
name|getComponentName
parameter_list|()
throws|throws
name|PathNotFoundException
throws|,
name|TextParseException
block|{
name|String
name|service
init|=
name|RegistryPathUtils
operator|.
name|lastPathEntry
argument_list|(
name|RegistryPathUtils
operator|.
name|parentOf
argument_list|(
name|RegistryPathUtils
operator|.
name|parentOf
argument_list|(
name|getPath
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|component
init|=
name|getRecord
argument_list|()
operator|.
name|get
argument_list|(
literal|"yarn:component"
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|String
name|user
init|=
name|RegistryPathUtils
operator|.
name|getUsername
argument_list|(
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Name
operator|.
name|fromString
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"{0}.{1}.{2}.{3}"
argument_list|,
name|component
argument_list|,
name|service
argument_list|,
name|user
argument_list|,
name|domain
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**    * An application-based DNS record descriptor.    *    * @param<T> the DNS record type/class.    */
DECL|class|ApplicationRecordDescriptor
specifier|abstract
class|class
name|ApplicationRecordDescriptor
parameter_list|<
name|T
parameter_list|>
extends|extends
name|RecordDescriptor
argument_list|<
name|T
argument_list|>
block|{
DECL|field|srEndpoint
specifier|private
name|Endpoint
name|srEndpoint
decl_stmt|;
comment|/**      * Creates an application associated DNS record descriptor.      *      * @param record the service record.      * @throws Exception      */
DECL|method|ApplicationRecordDescriptor (ServiceRecord record)
specifier|public
name|ApplicationRecordDescriptor
parameter_list|(
name|ServiceRecord
name|record
parameter_list|)
throws|throws
name|Exception
block|{
name|this
argument_list|(
name|record
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates an application associated DNS record descriptor.  The endpoint      * is leverated to create an associated application API record.      *      * @param record   the service record.      * @param endpoint an API endpoint.      * @throws Exception      */
DECL|method|ApplicationRecordDescriptor (ServiceRecord record, Endpoint endpoint)
specifier|public
name|ApplicationRecordDescriptor
parameter_list|(
name|ServiceRecord
name|record
parameter_list|,
name|Endpoint
name|endpoint
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|record
argument_list|)
expr_stmt|;
name|this
operator|.
name|setEndpoint
argument_list|(
name|endpoint
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|record
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the service's DNS name for registration.      *      * @return the service DNS name.      * @throws TextParseException      */
DECL|method|getServiceName ()
specifier|protected
name|Name
name|getServiceName
parameter_list|()
throws|throws
name|TextParseException
block|{
name|String
name|user
init|=
name|RegistryPathUtils
operator|.
name|getUsername
argument_list|(
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|service
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s.%s.%s"
argument_list|,
name|RegistryPathUtils
operator|.
name|lastPathEntry
argument_list|(
name|getPath
argument_list|()
argument_list|)
argument_list|,
name|user
argument_list|,
name|domain
argument_list|)
decl_stmt|;
return|return
name|Name
operator|.
name|fromString
argument_list|(
name|service
argument_list|)
return|;
block|}
comment|/**      * Get the host from the provided endpoint record.      *      * @param endpoint the endpoint info.      * @return the host name.      */
DECL|method|getHost (Endpoint endpoint)
specifier|protected
name|String
name|getHost
parameter_list|(
name|Endpoint
name|endpoint
parameter_list|)
block|{
name|String
name|host
init|=
literal|null
decl_stmt|;
comment|// assume one address for now
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|address
init|=
name|endpoint
operator|.
name|addresses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|endpoint
operator|.
name|addressType
operator|.
name|equals
argument_list|(
name|AddressTypes
operator|.
name|ADDRESS_HOSTNAME_AND_PORT
argument_list|)
condition|)
block|{
name|host
operator|=
name|address
operator|.
name|get
argument_list|(
name|AddressTypes
operator|.
name|ADDRESS_HOSTNAME_FIELD
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|endpoint
operator|.
name|addressType
operator|.
name|equals
argument_list|(
name|AddressTypes
operator|.
name|ADDRESS_URI
argument_list|)
condition|)
block|{
name|URI
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
name|address
operator|.
name|get
argument_list|(
literal|"uri"
argument_list|)
argument_list|)
decl_stmt|;
name|host
operator|=
name|uri
operator|.
name|getHost
argument_list|()
expr_stmt|;
block|}
return|return
name|host
return|;
block|}
comment|/**      * Get the post from the provided endpoint record.      *      * @param endpoint the endpoint info.      * @return the port.      */
DECL|method|getPort (Endpoint endpoint)
specifier|protected
name|int
name|getPort
parameter_list|(
name|Endpoint
name|endpoint
parameter_list|)
block|{
name|int
name|port
init|=
operator|-
literal|1
decl_stmt|;
comment|// assume one address for now
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|address
init|=
name|endpoint
operator|.
name|addresses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|endpoint
operator|.
name|addressType
operator|.
name|equals
argument_list|(
name|AddressTypes
operator|.
name|ADDRESS_HOSTNAME_AND_PORT
argument_list|)
condition|)
block|{
name|port
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|address
operator|.
name|get
argument_list|(
name|AddressTypes
operator|.
name|ADDRESS_PORT_FIELD
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|endpoint
operator|.
name|addressType
operator|.
name|equals
argument_list|(
name|AddressTypes
operator|.
name|ADDRESS_URI
argument_list|)
condition|)
block|{
name|URI
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
name|address
operator|.
name|get
argument_list|(
literal|"uri"
argument_list|)
argument_list|)
decl_stmt|;
name|port
operator|=
name|uri
operator|.
name|getPort
argument_list|()
expr_stmt|;
block|}
return|return
name|port
return|;
block|}
comment|/**      * Get the list of strings that can be related in a TXT record for the given      * endpoint.      *      * @param endpoint the endpoint information.      * @return the list of strings relating endpoint info.      */
DECL|method|getTextRecords (Endpoint endpoint)
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|getTextRecords
parameter_list|(
name|Endpoint
name|endpoint
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|address
init|=
name|endpoint
operator|.
name|addresses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|txtRecs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|txtRecs
operator|.
name|add
argument_list|(
literal|"api="
operator|+
name|getDNSApiFragment
argument_list|(
name|endpoint
operator|.
name|api
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|endpoint
operator|.
name|addressType
operator|.
name|equals
argument_list|(
name|AddressTypes
operator|.
name|ADDRESS_URI
argument_list|)
condition|)
block|{
name|URI
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
name|address
operator|.
name|get
argument_list|(
literal|"uri"
argument_list|)
argument_list|)
decl_stmt|;
name|txtRecs
operator|.
name|add
argument_list|(
literal|"path="
operator|+
name|uri
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|txtRecs
return|;
block|}
comment|/**      * Get an API name that is compatible with DNS standards (and shortened).      *      * @param api the api indicator.      * @return the shortened and compatible api name.      */
DECL|method|getDNSApiFragment (String api)
specifier|protected
name|String
name|getDNSApiFragment
parameter_list|(
name|String
name|api
parameter_list|)
block|{
name|String
name|dnsApi
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|api
operator|.
name|startsWith
argument_list|(
name|YARN_SERVICE_API_PREFIX
argument_list|)
condition|)
block|{
name|dnsApi
operator|=
name|api
operator|.
name|substring
argument_list|(
name|YARN_SERVICE_API_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|api
operator|.
name|startsWith
argument_list|(
name|HTTP_API_TYPE
argument_list|)
condition|)
block|{
name|dnsApi
operator|=
literal|"http"
expr_stmt|;
block|}
assert|assert
name|dnsApi
operator|!=
literal|null
assert|;
name|dnsApi
operator|=
name|dnsApi
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'-'
argument_list|)
expr_stmt|;
return|return
name|dnsApi
return|;
block|}
comment|/**      * Return the DNS name associated with the API endpoint.      *      * @return the name.      * @throws TextParseException      */
DECL|method|getEndpointName ()
specifier|protected
name|Name
name|getEndpointName
parameter_list|()
throws|throws
name|TextParseException
block|{
return|return
name|Name
operator|.
name|fromString
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s-api.%s"
argument_list|,
name|getDNSApiFragment
argument_list|(
name|getEndpoint
argument_list|()
operator|.
name|api
argument_list|)
argument_list|,
name|getServiceName
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns the endpoint.      * @return the endpoint.      */
DECL|method|getEndpoint ()
specifier|public
name|Endpoint
name|getEndpoint
parameter_list|()
block|{
return|return
name|srEndpoint
return|;
block|}
comment|/**      * Sets the endpoint.      * @param endpoint the endpoint.      */
DECL|method|setEndpoint ( Endpoint endpoint)
specifier|public
name|void
name|setEndpoint
parameter_list|(
name|Endpoint
name|endpoint
parameter_list|)
block|{
name|this
operator|.
name|srEndpoint
operator|=
name|endpoint
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

