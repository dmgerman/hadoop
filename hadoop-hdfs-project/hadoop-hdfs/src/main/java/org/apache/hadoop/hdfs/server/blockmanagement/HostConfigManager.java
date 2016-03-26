begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
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
name|Configurable
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
name|protocol
operator|.
name|DatanodeID
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
name|InetSocketAddress
import|;
end_import

begin_comment
comment|/**  * This interface abstracts how datanode configuration is managed.  *  * Each implementation defines its own way to persist the configuration.  * For example, it can use one JSON file to store the configs for all  * datanodes; or it can use one file to store in-service datanodes and another  * file to store decommission-requested datanodes.  *  * These files control which DataNodes the NameNode expects to see in the  * cluster.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|HostConfigManager
specifier|public
specifier|abstract
class|class
name|HostConfigManager
implements|implements
name|Configurable
block|{
comment|/**    * Return all the datanodes that are allowed to connect to the namenode.    * @return Iterable of all datanodes    */
DECL|method|getIncludes ()
specifier|public
specifier|abstract
name|Iterable
argument_list|<
name|InetSocketAddress
argument_list|>
name|getIncludes
parameter_list|()
function_decl|;
comment|/**    * Return all datanodes that should be in decommissioned state.    * @return Iterable of those datanodes    */
DECL|method|getExcludes ()
specifier|public
specifier|abstract
name|Iterable
argument_list|<
name|InetSocketAddress
argument_list|>
name|getExcludes
parameter_list|()
function_decl|;
comment|/**    * Check if a datanode is allowed to connect the namenode.    * @param dn the DatanodeID of the datanode    * @return boolean if dn is allowed to connect the namenode.    */
DECL|method|isIncluded (DatanodeID dn)
specifier|public
specifier|abstract
name|boolean
name|isIncluded
parameter_list|(
name|DatanodeID
name|dn
parameter_list|)
function_decl|;
comment|/**    * Check if a datanode needs to be decommissioned.    * @param dn the DatanodeID of the datanode    * @return boolean if dn needs to be decommissioned.    */
DECL|method|isExcluded (DatanodeID dn)
specifier|public
specifier|abstract
name|boolean
name|isExcluded
parameter_list|(
name|DatanodeID
name|dn
parameter_list|)
function_decl|;
comment|/**    * Reload the configuration.    */
DECL|method|refresh ()
specifier|public
specifier|abstract
name|void
name|refresh
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the upgrade domain of a datanode.    * @param dn the DatanodeID of the datanode    * @return the upgrade domain of dn.    */
DECL|method|getUpgradeDomain (DatanodeID dn)
specifier|public
specifier|abstract
name|String
name|getUpgradeDomain
parameter_list|(
name|DatanodeID
name|dn
parameter_list|)
function_decl|;
block|}
end_class

end_unit

