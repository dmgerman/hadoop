begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.client.types
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
name|types
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
name|binding
operator|.
name|RegistryTypeUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|annotate
operator|.
name|JsonIgnoreProperties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|annotate
operator|.
name|JsonSerialize
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
name|ArrayList
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
name|List
import|;
end_import

begin_comment
comment|/**  * Description of a single service/component endpoint.  * It is designed to be marshalled as JSON.  *<p>  * Every endpoint can have more than one address entry, such as  * a list of URLs to a replicated service, or a (hostname, port)  * pair. Each of these address entries is represented as a string list,  * as that is the only reliably marshallable form of a tuple JSON can represent.  *  *  */
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
annotation|@
name|JsonIgnoreProperties
argument_list|(
name|ignoreUnknown
operator|=
literal|true
argument_list|)
annotation|@
name|JsonSerialize
argument_list|(
name|include
operator|=
name|JsonSerialize
operator|.
name|Inclusion
operator|.
name|NON_NULL
argument_list|)
DECL|class|Endpoint
specifier|public
specifier|final
class|class
name|Endpoint
implements|implements
name|Cloneable
block|{
comment|/**    * API implemented at the end of the binding    */
DECL|field|api
specifier|public
name|String
name|api
decl_stmt|;
comment|/**    * Type of address. The standard types are defined in    * {@link AddressTypes}    */
DECL|field|addressType
specifier|public
name|String
name|addressType
decl_stmt|;
comment|/**    * Protocol type. Some standard types are defined in    * {@link ProtocolTypes}    */
DECL|field|protocolType
specifier|public
name|String
name|protocolType
decl_stmt|;
comment|/**    * a list of address tuples âtuples whose format depends on the address type    */
DECL|field|addresses
specifier|public
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|addresses
decl_stmt|;
comment|/**    * Create an empty instance.    */
DECL|method|Endpoint ()
specifier|public
name|Endpoint
parameter_list|()
block|{   }
comment|/**    * Create an endpoint from another endpoint.    * This is a deep clone with a new list of addresses.    * @param that the endpoint to copy from    */
DECL|method|Endpoint (Endpoint that)
specifier|public
name|Endpoint
parameter_list|(
name|Endpoint
name|that
parameter_list|)
block|{
name|this
operator|.
name|api
operator|=
name|that
operator|.
name|api
expr_stmt|;
name|this
operator|.
name|addressType
operator|=
name|that
operator|.
name|addressType
expr_stmt|;
name|this
operator|.
name|protocolType
operator|=
name|that
operator|.
name|protocolType
expr_stmt|;
name|this
operator|.
name|addresses
operator|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|(
name|that
operator|.
name|addresses
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
name|List
argument_list|<
name|String
argument_list|>
name|addr2
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|address
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|copy
argument_list|(
name|address
argument_list|,
name|addr2
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Build an endpoint with a list of addresses    * @param api API name    * @param addressType address type    * @param protocolType protocol type    * @param addrs addresses    */
DECL|method|Endpoint (String api, String addressType, String protocolType, List<List<String>> addrs)
specifier|public
name|Endpoint
parameter_list|(
name|String
name|api
parameter_list|,
name|String
name|addressType
parameter_list|,
name|String
name|protocolType
parameter_list|,
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|addrs
parameter_list|)
block|{
name|this
operator|.
name|api
operator|=
name|api
expr_stmt|;
name|this
operator|.
name|addressType
operator|=
name|addressType
expr_stmt|;
name|this
operator|.
name|protocolType
operator|=
name|protocolType
expr_stmt|;
name|this
operator|.
name|addresses
operator|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|addrs
operator|!=
literal|null
condition|)
block|{
name|addresses
operator|.
name|addAll
argument_list|(
name|addrs
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Build an endpoint from a list of URIs; each URI    * is ASCII-encoded and added to the list of addresses.    * @param api API name    * @param protocolType protocol type    * @param uris URIs to convert to a list of tup;les    */
DECL|method|Endpoint (String api, String protocolType, URI... uris)
specifier|public
name|Endpoint
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
name|this
operator|.
name|api
operator|=
name|api
expr_stmt|;
name|this
operator|.
name|addressType
operator|=
name|AddressTypes
operator|.
name|ADDRESS_URI
expr_stmt|;
name|this
operator|.
name|protocolType
operator|=
name|protocolType
expr_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|addrs
init|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|(
name|uris
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|URI
name|uri
range|:
name|uris
control|)
block|{
name|addrs
operator|.
name|add
argument_list|(
name|RegistryTypeUtils
operator|.
name|tuple
argument_list|(
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|addresses
operator|=
name|addrs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Endpoint{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"api='"
argument_list|)
operator|.
name|append
argument_list|(
name|api
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", addressType='"
argument_list|)
operator|.
name|append
argument_list|(
name|addressType
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", protocolType='"
argument_list|)
operator|.
name|append
argument_list|(
name|protocolType
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", addresses="
argument_list|)
expr_stmt|;
if|if
condition|(
name|addresses
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"[ "
argument_list|)
expr_stmt|;
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
name|sb
operator|.
name|append
argument_list|(
literal|"[ "
argument_list|)
expr_stmt|;
if|if
condition|(
name|address
operator|==
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"NULL entry in address list"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|elt
range|:
name|address
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
operator|.
name|append
argument_list|(
name|elt
argument_list|)
operator|.
name|append
argument_list|(
literal|"\" "
argument_list|)
expr_stmt|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"] "
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"] "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"(null) "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Validate the record by checking for null fields and other invalid    * conditions    * @throws NullPointerException if a field is null when it    * MUST be set.    * @throws RuntimeException on invalid entries    */
DECL|method|validate ()
specifier|public
name|void
name|validate
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|api
argument_list|,
literal|"null API field"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|addressType
argument_list|,
literal|"null addressType field"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|protocolType
argument_list|,
literal|"null protocolType field"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|addresses
argument_list|,
literal|"null addresses field"
argument_list|)
expr_stmt|;
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
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|address
argument_list|,
literal|"null element in address"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Shallow clone: the lists of addresses are shared    * @return a cloned instance    * @throws CloneNotSupportedException    */
annotation|@
name|Override
DECL|method|clone ()
specifier|protected
name|Object
name|clone
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
return|return
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
block|}
end_class

end_unit

