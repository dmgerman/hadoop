begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.client.binding
package|package
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
name|Preconditions
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
name|registry
operator|.
name|client
operator|.
name|exceptions
operator|.
name|InvalidRecordException
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
name|ProtocolTypes
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
name|Arrays
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

begin_comment
comment|/**  * Static methods to work with registry types âprimarily endpoints and the  * list representation of addresses.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|RegistryTypeUtils
specifier|public
class|class
name|RegistryTypeUtils
block|{
comment|/**    * Create a URL endpoint from a list of URIs    * @param api implemented API    * @param protocolType protocol type    * @param uris URIs    * @return a new endpoint    */
DECL|method|urlEndpoint (String api, String protocolType, URI... uris)
specifier|public
specifier|static
name|Endpoint
name|urlEndpoint
parameter_list|(
name|String
name|api
parameter_list|,
name|String
name|protocolType
parameter_list|,
name|URI
modifier|...
name|uris
parameter_list|)
block|{
return|return
operator|new
name|Endpoint
argument_list|(
name|api
argument_list|,
name|protocolType
argument_list|,
name|uris
argument_list|)
return|;
block|}
comment|/**    * Create a REST endpoint from a list of URIs    * @param api implemented API    * @param uris URIs    * @return a new endpoint    */
DECL|method|restEndpoint (String api, URI... uris)
specifier|public
specifier|static
name|Endpoint
name|restEndpoint
parameter_list|(
name|String
name|api
parameter_list|,
name|URI
modifier|...
name|uris
parameter_list|)
block|{
return|return
name|urlEndpoint
argument_list|(
name|api
argument_list|,
name|ProtocolTypes
operator|.
name|PROTOCOL_REST
argument_list|,
name|uris
argument_list|)
return|;
block|}
comment|/**    * Create a Web UI endpoint from a list of URIs    * @param api implemented API    * @param uris URIs    * @return a new endpoint    */
DECL|method|webEndpoint (String api, URI... uris)
specifier|public
specifier|static
name|Endpoint
name|webEndpoint
parameter_list|(
name|String
name|api
parameter_list|,
name|URI
modifier|...
name|uris
parameter_list|)
block|{
return|return
name|urlEndpoint
argument_list|(
name|api
argument_list|,
name|ProtocolTypes
operator|.
name|PROTOCOL_WEBUI
argument_list|,
name|uris
argument_list|)
return|;
block|}
comment|/**    * Create an internet address endpoint from a list of URIs    * @param api implemented API    * @param protocolType protocol type    * @param hostname hostname/FQDN    * @param port port    * @return a new endpoint    */
DECL|method|inetAddrEndpoint (String api, String protocolType, String hostname, int port)
specifier|public
specifier|static
name|Endpoint
name|inetAddrEndpoint
parameter_list|(
name|String
name|api
parameter_list|,
name|String
name|protocolType
parameter_list|,
name|String
name|hostname
parameter_list|,
name|int
name|port
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|api
operator|!=
literal|null
argument_list|,
literal|"null API"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|protocolType
operator|!=
literal|null
argument_list|,
literal|"null protocolType"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|hostname
operator|!=
literal|null
argument_list|,
literal|"null hostname"
argument_list|)
expr_stmt|;
return|return
operator|new
name|Endpoint
argument_list|(
name|api
argument_list|,
name|AddressTypes
operator|.
name|ADDRESS_HOSTNAME_AND_PORT
argument_list|,
name|protocolType
argument_list|,
name|tuplelist
argument_list|(
name|hostname
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|port
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Create an IPC endpoint    * @param api API    * @param protobuf flag to indicate whether or not the IPC uses protocol    * buffers    * @param address the address as a tuple of (hostname, port)    * @return the new endpoint    */
DECL|method|ipcEndpoint (String api, boolean protobuf, List<String> address)
specifier|public
specifier|static
name|Endpoint
name|ipcEndpoint
parameter_list|(
name|String
name|api
parameter_list|,
name|boolean
name|protobuf
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|address
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|addressList
init|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|address
operator|!=
literal|null
condition|)
block|{
name|addressList
operator|.
name|add
argument_list|(
name|address
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Endpoint
argument_list|(
name|api
argument_list|,
name|AddressTypes
operator|.
name|ADDRESS_HOSTNAME_AND_PORT
argument_list|,
name|protobuf
condition|?
name|ProtocolTypes
operator|.
name|PROTOCOL_HADOOP_IPC_PROTOBUF
else|:
name|ProtocolTypes
operator|.
name|PROTOCOL_HADOOP_IPC
argument_list|,
name|addressList
argument_list|)
return|;
block|}
comment|/**    * Create a single-element list of tuples from the input.    * that is, an input ("a","b","c") is converted into a list    * in the form [["a","b","c"]]    * @param t1 tuple elements    * @return a list containing a single tuple    */
DECL|method|tuplelist (String... t1)
specifier|public
specifier|static
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|tuplelist
parameter_list|(
name|String
modifier|...
name|t1
parameter_list|)
block|{
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|outer
init|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|outer
operator|.
name|add
argument_list|(
name|tuple
argument_list|(
name|t1
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|outer
return|;
block|}
comment|/**    * Create a tuples from the input.    * that is, an input ("a","b","c") is converted into a list    * in the form ["a","b","c"]    * @param t1 tuple elements    * @return a single tuple as a list    */
DECL|method|tuple (String... t1)
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|tuple
parameter_list|(
name|String
modifier|...
name|t1
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|t1
argument_list|)
return|;
block|}
comment|/**    * Create a tuples from the input, converting all to Strings in the process    * that is, an input ("a", 7, true) is converted into a list    * in the form ["a","7,"true"]    * @param t1 tuple elements    * @return a single tuple as a list    */
DECL|method|tuple (Object... t1)
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|tuple
parameter_list|(
name|Object
modifier|...
name|t1
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|t1
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|t
range|:
name|t1
control|)
block|{
name|l
operator|.
name|add
argument_list|(
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|l
return|;
block|}
comment|/**    * Convert a socket address pair into a string tuple, (host, port).    * TODO JDK7: move to InetAddress.getHostString() to avoid DNS lookups.    * @param address an address    * @return an element for the address list    */
DECL|method|marshall (InetSocketAddress address)
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|marshall
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|)
block|{
return|return
name|tuple
argument_list|(
name|address
operator|.
name|getHostName
argument_list|()
argument_list|,
name|address
operator|.
name|getPort
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Require a specific address type on an endpoint    * @param required required type    * @param epr endpoint    * @throws InvalidRecordException if the type is wrong    */
DECL|method|requireAddressType (String required, Endpoint epr)
specifier|public
specifier|static
name|void
name|requireAddressType
parameter_list|(
name|String
name|required
parameter_list|,
name|Endpoint
name|epr
parameter_list|)
throws|throws
name|InvalidRecordException
block|{
if|if
condition|(
operator|!
name|required
operator|.
name|equals
argument_list|(
name|epr
operator|.
name|addressType
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidRecordException
argument_list|(
name|epr
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Address type of "
operator|+
name|epr
operator|.
name|addressType
operator|+
literal|" does not match required type of "
operator|+
name|required
argument_list|)
throw|;
block|}
block|}
comment|/**    * Get a single URI endpoint    * @param epr endpoint    * @return the uri of the first entry in the address list. Null if the endpoint    * itself is null    * @throws InvalidRecordException if the type is wrong, there are no addresses    * or the payload ill-formatted    */
DECL|method|retrieveAddressesUriType (Endpoint epr)
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|retrieveAddressesUriType
parameter_list|(
name|Endpoint
name|epr
parameter_list|)
throws|throws
name|InvalidRecordException
block|{
if|if
condition|(
name|epr
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|requireAddressType
argument_list|(
name|AddressTypes
operator|.
name|ADDRESS_URI
argument_list|,
name|epr
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|addresses
init|=
name|epr
operator|.
name|addresses
decl_stmt|;
if|if
condition|(
name|addresses
operator|.
name|size
argument_list|()
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|InvalidRecordException
argument_list|(
name|epr
operator|.
name|toString
argument_list|()
argument_list|,
literal|"No addresses in endpoint"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|addresses
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|String
argument_list|>
name|address
range|:
name|addresses
control|)
block|{
if|if
condition|(
name|address
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|InvalidRecordException
argument_list|(
name|epr
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Address payload invalid: wrong element count: "
operator|+
name|address
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
name|results
operator|.
name|add
argument_list|(
name|address
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
comment|/**    * Get the address URLs. Guranteed to return at least one address.    * @param epr endpoint    * @return the address as a URL    * @throws InvalidRecordException if the type is wrong, there are no addresses    * or the payload ill-formatted    * @throws MalformedURLException address can't be turned into a URL    */
DECL|method|retrieveAddressURLs (Endpoint epr)
specifier|public
specifier|static
name|List
argument_list|<
name|URL
argument_list|>
name|retrieveAddressURLs
parameter_list|(
name|Endpoint
name|epr
parameter_list|)
throws|throws
name|InvalidRecordException
throws|,
name|MalformedURLException
block|{
if|if
condition|(
name|epr
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidRecordException
argument_list|(
literal|""
argument_list|,
literal|"Null endpoint"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|addresses
init|=
name|retrieveAddressesUriType
argument_list|(
name|epr
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|URL
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|URL
argument_list|>
argument_list|(
name|addresses
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|address
range|:
name|addresses
control|)
block|{
name|results
operator|.
name|add
argument_list|(
operator|new
name|URL
argument_list|(
name|address
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
block|}
end_class

end_unit

