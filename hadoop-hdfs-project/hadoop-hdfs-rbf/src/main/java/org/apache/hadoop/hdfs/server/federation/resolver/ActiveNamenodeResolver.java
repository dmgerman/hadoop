begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.resolver
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
name|federation
operator|.
name|resolver
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
name|InetSocketAddress
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
name|Set
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
comment|/**  * Locates the most active NN for a given nameservice ID or blockpool ID. This  * interface is used by the {@link org.apache.hadoop.hdfs.server.federation.  * router.RouterRpcServer RouterRpcServer} to:  *<ul>  *<li>Determine the target NN for a given subcluster.  *<li>List of all namespaces discovered/active in the federation.  *<li>Update the currently active NN empirically.  *</ul>  * The interface is also used by the {@link org.apache.hadoop.hdfs.server.  * federation.router.NamenodeHeartbeatService NamenodeHeartbeatService} to  * register a discovered NN.  */
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
DECL|interface|ActiveNamenodeResolver
specifier|public
interface|interface
name|ActiveNamenodeResolver
block|{
comment|/**    * Report a successful, active NN address for a nameservice or blockPool.    *    * @param ns Nameservice identifier.    * @param successfulAddress The address the successful responded to the    *                          command.    * @throws IOException If the state store cannot be accessed.    */
DECL|method|updateActiveNamenode ( String ns, InetSocketAddress successfulAddress)
name|void
name|updateActiveNamenode
parameter_list|(
name|String
name|ns
parameter_list|,
name|InetSocketAddress
name|successfulAddress
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a prioritized list of the most recent cached registration entries    * for a single nameservice ID.    * Returns an empty list if none are found. Returns entries in preference of:    *<ul>    *<li>The most recent ACTIVE NN    *<li>The most recent STANDBY NN    *<li>The most recent UNAVAILABLE NN    *</ul>    *    * @param nameserviceId Nameservice identifier.    * @return Prioritized list of namenode contexts.    * @throws IOException If the state store cannot be accessed.    */
name|List
argument_list|<
name|?
extends|extends
name|FederationNamenodeContext
argument_list|>
DECL|method|getNamenodesForNameserviceId (String nameserviceId)
name|getNamenodesForNameserviceId
parameter_list|(
name|String
name|nameserviceId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a prioritized list of the most recent cached registration entries    * for a single block pool ID.    * Returns an empty list if none are found. Returns entries in preference of:    *<ul>    *<li>The most recent ACTIVE NN    *<li>The most recent STANDBY NN    *<li>The most recent UNAVAILABLE NN    *</ul>    *    * @param blockPoolId Block pool identifier for the nameservice.    * @return Prioritized list of namenode contexts.    * @throws IOException If the state store cannot be accessed.    */
name|List
argument_list|<
name|?
extends|extends
name|FederationNamenodeContext
argument_list|>
DECL|method|getNamenodesForBlockPoolId (String blockPoolId)
name|getNamenodesForBlockPoolId
parameter_list|(
name|String
name|blockPoolId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Register a namenode in the State Store.    *    * @param report Namenode status report.    * @return True if the node was registered and successfully committed to the    *         data store.    * @throws IOException Throws exception if the namenode could not be    *         registered.    */
DECL|method|registerNamenode (NamenodeStatusReport report)
name|boolean
name|registerNamenode
parameter_list|(
name|NamenodeStatusReport
name|report
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a list of all namespaces that are registered and active in the    * federation.    *    * @return List of name spaces in the federation    * @throws IOException Throws exception if the namespace list is not    *         available.    */
DECL|method|getNamespaces ()
name|Set
argument_list|<
name|FederationNamespaceInfo
argument_list|>
name|getNamespaces
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Assign a unique identifier for the parent router service.    * Required to report the status to the namenode resolver.    *    * @param routerId Unique string identifier for the router.    */
DECL|method|setRouterId (String routerId)
name|void
name|setRouterId
parameter_list|(
name|String
name|routerId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

