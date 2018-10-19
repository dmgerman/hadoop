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

begin_comment
comment|/**  * Enum of address types -as integers.  * Why integers and not enums? Cross platform serialization as JSON  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|AddressTypes
specifier|public
interface|interface
name|AddressTypes
block|{
comment|/**    * hostname/FQDN and port pair: {@value}.    * The host/domain name and port are set as separate strings in the address    * list, e.g.    *<pre>    *   ["namenode.example.org", "9870"]    *</pre>    */
DECL|field|ADDRESS_HOSTNAME_AND_PORT
specifier|public
specifier|static
specifier|final
name|String
name|ADDRESS_HOSTNAME_AND_PORT
init|=
literal|"host/port"
decl_stmt|;
DECL|field|ADDRESS_HOSTNAME_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|ADDRESS_HOSTNAME_FIELD
init|=
literal|"host"
decl_stmt|;
DECL|field|ADDRESS_PORT_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|ADDRESS_PORT_FIELD
init|=
literal|"port"
decl_stmt|;
comment|/**    * Path<code>/a/b/c</code> style: {@value}.    * The entire path is encoded in a single entry    *    *<pre>    *   ["/users/example/dataset"]    *</pre>    */
DECL|field|ADDRESS_PATH
specifier|public
specifier|static
specifier|final
name|String
name|ADDRESS_PATH
init|=
literal|"path"
decl_stmt|;
comment|/**    * URI entries: {@value}.    *<pre>    *   ["http://example.org"]    *</pre>    */
DECL|field|ADDRESS_URI
specifier|public
specifier|static
specifier|final
name|String
name|ADDRESS_URI
init|=
literal|"uri"
decl_stmt|;
comment|/**    * Zookeeper addresses as a triple : {@value}.    *<p>    * These are provide as a 3 element tuple of: hostname, port    * and optionally path (depending on the application)    *<p>    *   A single element would be    *<pre>    *   ["zk1","2181","/registry"]    *</pre>    *  An endpoint with multiple elements would list them as    *<pre>    *   [    *    ["zk1","2181","/registry"]    *    ["zk2","1600","/registry"]    *   ]    *</pre>    *    * the third element in each entry , the path, MUST be the same in each entry.    * A client reading the addresses of an endpoint is free to pick any    * of the set, so they must be the same.    *    */
DECL|field|ADDRESS_ZOOKEEPER
specifier|public
specifier|static
specifier|final
name|String
name|ADDRESS_ZOOKEEPER
init|=
literal|"zktriple"
decl_stmt|;
comment|/**    * Any other address: {@value}.    */
DECL|field|ADDRESS_OTHER
specifier|public
specifier|static
specifier|final
name|String
name|ADDRESS_OTHER
init|=
literal|""
decl_stmt|;
block|}
end_interface

end_unit

