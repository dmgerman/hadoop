begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.metrics
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
name|metrics
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
comment|/**  * JMX interface for the federation statistics.  */
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
DECL|interface|FederationMBean
specifier|public
interface|interface
name|FederationMBean
block|{
comment|/**    * Get information about all the namenodes in the federation or null if    * failure.    * @return JSON with all the Namenodes.    */
DECL|method|getNamenodes ()
name|String
name|getNamenodes
parameter_list|()
function_decl|;
comment|/**    * Get the latest info for each registered nameservice.    * @return JSON with all the nameservices.    */
DECL|method|getNameservices ()
name|String
name|getNameservices
parameter_list|()
function_decl|;
comment|/**    * Get the mount table for the federated filesystem or null if failure.    * @return JSON with the mount table.    */
DECL|method|getMountTable ()
name|String
name|getMountTable
parameter_list|()
function_decl|;
comment|/**    * Get the latest state of all routers.    * @return JSON with all of the known routers or null if failure.    */
DECL|method|getRouters ()
name|String
name|getRouters
parameter_list|()
function_decl|;
comment|/**    * Get the total capacity of the federated cluster.    * @return Total capacity of the federated cluster.    */
DECL|method|getTotalCapacity ()
name|long
name|getTotalCapacity
parameter_list|()
function_decl|;
comment|/**    * Get the used capacity of the federated cluster.    * @return Used capacity of the federated cluster.    */
DECL|method|getUsedCapacity ()
name|long
name|getUsedCapacity
parameter_list|()
function_decl|;
comment|/**    * Get the remaining capacity of the federated cluster.    * @return Remaining capacity of the federated cluster.    */
DECL|method|getRemainingCapacity ()
name|long
name|getRemainingCapacity
parameter_list|()
function_decl|;
comment|/**    * Get the total remote storage capacity mounted in the federated cluster.    * @return Remote capacity of the federated cluster.    */
DECL|method|getProvidedSpace ()
name|long
name|getProvidedSpace
parameter_list|()
function_decl|;
comment|/**    * Get the number of nameservices in the federation.    * @return Number of nameservices in the federation.    */
DECL|method|getNumNameservices ()
name|int
name|getNumNameservices
parameter_list|()
function_decl|;
comment|/**    * Get the number of namenodes.    * @return Number of namenodes.    */
DECL|method|getNumNamenodes ()
name|int
name|getNumNamenodes
parameter_list|()
function_decl|;
comment|/**    * Get the number of expired namenodes.    * @return Number of expired namenodes.    */
DECL|method|getNumExpiredNamenodes ()
name|int
name|getNumExpiredNamenodes
parameter_list|()
function_decl|;
comment|/**    * Get the number of live datanodes.    * @return Number of live datanodes.    */
DECL|method|getNumLiveNodes ()
name|int
name|getNumLiveNodes
parameter_list|()
function_decl|;
comment|/**    * Get the number of dead datanodes.    * @return Number of dead datanodes.    */
DECL|method|getNumDeadNodes ()
name|int
name|getNumDeadNodes
parameter_list|()
function_decl|;
comment|/**    * Get the number of stale datanodes.    * @return Number of stale datanodes.    */
DECL|method|getNumStaleNodes ()
name|int
name|getNumStaleNodes
parameter_list|()
function_decl|;
comment|/**    * Get the number of decommissioning datanodes.    * @return Number of decommissioning datanodes.    */
DECL|method|getNumDecommissioningNodes ()
name|int
name|getNumDecommissioningNodes
parameter_list|()
function_decl|;
comment|/**    * Get the number of live decommissioned datanodes.    * @return Number of live decommissioned datanodes.    */
DECL|method|getNumDecomLiveNodes ()
name|int
name|getNumDecomLiveNodes
parameter_list|()
function_decl|;
comment|/**    * Get the number of dead decommissioned datanodes.    * @return Number of dead decommissioned datanodes.    */
DECL|method|getNumDecomDeadNodes ()
name|int
name|getNumDecomDeadNodes
parameter_list|()
function_decl|;
comment|/**    * Get Max, Median, Min and Standard Deviation of DataNodes usage.    * @return the DataNode usage information, as a JSON string.    */
DECL|method|getNodeUsage ()
name|String
name|getNodeUsage
parameter_list|()
function_decl|;
comment|/**    * Get the number of blocks in the federation.    * @return Number of blocks in the federation.    */
DECL|method|getNumBlocks ()
name|long
name|getNumBlocks
parameter_list|()
function_decl|;
comment|/**    * Get the number of missing blocks in the federation.    * @return Number of missing blocks in the federation.    */
DECL|method|getNumOfMissingBlocks ()
name|long
name|getNumOfMissingBlocks
parameter_list|()
function_decl|;
comment|/**    * Get the number of pending replication blocks in the federation.    * @return Number of pending replication blocks in the federation.    */
DECL|method|getNumOfBlocksPendingReplication ()
name|long
name|getNumOfBlocksPendingReplication
parameter_list|()
function_decl|;
comment|/**    * Get the number of under replicated blocks in the federation.    * @return Number of under replicated blocks in the federation.    */
DECL|method|getNumOfBlocksUnderReplicated ()
name|long
name|getNumOfBlocksUnderReplicated
parameter_list|()
function_decl|;
comment|/**    * Get the number of pending deletion blocks in the federation.    * @return Number of pending deletion blocks in the federation.    */
DECL|method|getNumOfBlocksPendingDeletion ()
name|long
name|getNumOfBlocksPendingDeletion
parameter_list|()
function_decl|;
comment|/**    * Get the number of files in the federation.    * @return Number of files in the federation.    */
DECL|method|getNumFiles ()
name|long
name|getNumFiles
parameter_list|()
function_decl|;
comment|/**    * When the router started.    * @return Date as a string the router started.    */
DECL|method|getRouterStarted ()
name|String
name|getRouterStarted
parameter_list|()
function_decl|;
comment|/**    * Get the version of the router.    * @return Version of the router.    */
DECL|method|getVersion ()
name|String
name|getVersion
parameter_list|()
function_decl|;
comment|/**    * Get the compilation date of the router.    * @return Compilation date of the router.    */
DECL|method|getCompiledDate ()
name|String
name|getCompiledDate
parameter_list|()
function_decl|;
comment|/**    * Get the compilation info of the router.    * @return Compilation info of the router.    */
DECL|method|getCompileInfo ()
name|String
name|getCompileInfo
parameter_list|()
function_decl|;
comment|/**    * Get the host and port of the router.    * @return Host and port of the router.    */
DECL|method|getHostAndPort ()
name|String
name|getHostAndPort
parameter_list|()
function_decl|;
comment|/**    * Get the identifier of the router.    * @return Identifier of the router.    */
DECL|method|getRouterId ()
name|String
name|getRouterId
parameter_list|()
function_decl|;
comment|/**    * Get the host and port of the router.    * @return Host and port of the router.    */
DECL|method|getClusterId ()
name|String
name|getClusterId
parameter_list|()
function_decl|;
comment|/**    * Get the host and port of the router.    * @return Host and port of the router.    */
DECL|method|getBlockPoolId ()
name|String
name|getBlockPoolId
parameter_list|()
function_decl|;
comment|/**    * Get the current state of the router.    *    * @return String label for the current router state.    */
DECL|method|getRouterStatus ()
name|String
name|getRouterStatus
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

