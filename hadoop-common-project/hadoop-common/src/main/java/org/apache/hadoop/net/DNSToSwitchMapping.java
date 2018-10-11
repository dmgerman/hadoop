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
name|util
operator|.
name|List
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

begin_comment
comment|/**  * An interface that must be implemented to allow pluggable  * DNS-name/IP-address to RackID resolvers.  *  */
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
DECL|interface|DNSToSwitchMapping
specifier|public
interface|interface
name|DNSToSwitchMapping
block|{
comment|/**    * Resolves a list of DNS-names/IP-addresses and returns back a list of    * switch information (network paths). One-to-one correspondence must be     * maintained between the elements in the lists.     * Consider an element in the argument list - x.y.com. The switch information    * that is returned must be a network path of the form /foo/rack,     * where / is the root, and 'foo' is the switch where 'rack' is connected.    * Note the hostname/ip-address is not part of the returned path.    * The network topology of the cluster would determine the number of    * components in the network path.    *<p>    *    * If a name cannot be resolved to a rack, the implementation    * should return {@link NetworkTopology#DEFAULT_RACK}. This    * is what the bundled implementations do, though it is not a formal requirement    *    * @param names the list of hosts to resolve (can be empty)    * @return list of resolved network paths.    * If<i>names</i> is empty, the returned list is also empty    */
DECL|method|resolve (List<String> names)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|resolve
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
function_decl|;
comment|/**    * Reload all of the cached mappings.    *    * If there is a cache, this method will clear it, so that future accesses    * will get a chance to see the new data.    */
DECL|method|reloadCachedMappings ()
specifier|public
name|void
name|reloadCachedMappings
parameter_list|()
function_decl|;
comment|/**    * Reload cached mappings on specific nodes.    *    * If there is a cache on these nodes, this method will clear it, so that     * future accesses will see updated data.    */
DECL|method|reloadCachedMappings (List<String> names)
specifier|public
name|void
name|reloadCachedMappings
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

