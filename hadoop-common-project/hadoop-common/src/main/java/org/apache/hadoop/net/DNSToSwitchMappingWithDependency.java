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
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|DNSToSwitchMappingWithDependency
specifier|public
interface|interface
name|DNSToSwitchMappingWithDependency
extends|extends
name|DNSToSwitchMapping
block|{
comment|/**    * Get a list of dependent DNS-names for a given DNS-name/IP-address.    * Dependent DNS-names fall into the same fault domain which must be    * taken into account when placing replicas. This is intended to be used for    * cross node group dependencies when node groups are not sufficient to     * distinguish data nodes by fault domains. In practice, this is needed when    * a compute server runs VMs which use shared storage (as opposite to     * directly attached storage). In this case data nodes fall in two different    * fault domains. One fault domain is defined by a compute server and     * the other is defined by storage. With node groups we can group data nodes    * either by server fault domain or by storage fault domain. However one of    * the fault domains cannot be handled and there we need to define cross node    * group dependencies. These dependencies are applied in block placement     * polices which ensure that no two replicas will be on two dependent nodes.     * @param name - host name or IP address of a data node. Input host name     * parameter must take a value of dfs.datanode.hostname config value if this    * config property is set. Otherwise FQDN of the data node is used.    * @return list of dependent host names. If dfs.datanode.hostname config    * property is set, then its value must be returned.    * Otherwise, FQDN is returned.     */
DECL|method|getDependency (String name)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getDependency
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

