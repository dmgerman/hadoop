begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

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
name|hdfs
operator|.
name|protocol
operator|.
name|RollingUpgradeInfo
import|;
end_import

begin_comment
comment|/**  * This is the JMX management interface for namenode information.  * End users shouldn't be implementing these interfaces, and instead  * access this information through the JMX APIs.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|NameNodeMXBean
specifier|public
interface|interface
name|NameNodeMXBean
block|{
comment|/**    * Gets the version of Hadoop.    *     * @return the version.    */
DECL|method|getVersion ()
name|String
name|getVersion
parameter_list|()
function_decl|;
comment|/**    * Get the version of software running on the Namenode.    *    * @return a string representing the version.    */
DECL|method|getSoftwareVersion ()
name|String
name|getSoftwareVersion
parameter_list|()
function_decl|;
comment|/**    * Gets the used space by data nodes.    *     * @return the used space by data nodes.    */
DECL|method|getUsed ()
name|long
name|getUsed
parameter_list|()
function_decl|;
comment|/**    * Gets total non-used raw bytes.    *     * @return total non-used raw bytes.    */
DECL|method|getFree ()
name|long
name|getFree
parameter_list|()
function_decl|;
comment|/**    * Gets total raw bytes including non-dfs used space.    *     * @return the total raw bytes including non-dfs used space.    */
DECL|method|getTotal ()
name|long
name|getTotal
parameter_list|()
function_decl|;
comment|/**    * Gets capacity of the provided storage mounted, in bytes.    *    * @return the total raw bytes present in the provided storage.    */
DECL|method|getProvidedCapacity ()
name|long
name|getProvidedCapacity
parameter_list|()
function_decl|;
comment|/**    * Gets the safemode status.    *     * @return the safemode status.    */
DECL|method|getSafemode ()
name|String
name|getSafemode
parameter_list|()
function_decl|;
comment|/**    * Checks if upgrade is finalized.    *     * @return true, if upgrade is finalized.    */
DECL|method|isUpgradeFinalized ()
name|boolean
name|isUpgradeFinalized
parameter_list|()
function_decl|;
comment|/**    * Gets the RollingUpgrade information.    *    * @return Rolling upgrade information if an upgrade is in progress. Else    * (e.g. if there is no upgrade or the upgrade is finalized), returns null.    */
DECL|method|getRollingUpgradeStatus ()
name|RollingUpgradeInfo
operator|.
name|Bean
name|getRollingUpgradeStatus
parameter_list|()
function_decl|;
comment|/**    * Gets total used space by data nodes for non DFS purposes such as storing    * temporary files on the local file system.    *     * @return the non dfs space of the cluster.    */
DECL|method|getNonDfsUsedSpace ()
name|long
name|getNonDfsUsedSpace
parameter_list|()
function_decl|;
comment|/**    * Gets the total used space by data nodes as percentage of total capacity.    *     * @return the percentage of used space on the cluster.    */
DECL|method|getPercentUsed ()
name|float
name|getPercentUsed
parameter_list|()
function_decl|;
comment|/**    * Gets the total remaining space by data nodes as percentage of total     * capacity.    *     * @return the percentage of the remaining space on the cluster.    */
DECL|method|getPercentRemaining ()
name|float
name|getPercentRemaining
parameter_list|()
function_decl|;
comment|/**    * Gets the amount of cache used by the datanode (in bytes).    *    * @return the amount of cache used by the datanode (in bytes).    */
DECL|method|getCacheUsed ()
name|long
name|getCacheUsed
parameter_list|()
function_decl|;
comment|/**    * Gets the total cache capacity of the datanode (in bytes).    *    * @return the total cache capacity of the datanode (in bytes).    */
DECL|method|getCacheCapacity ()
name|long
name|getCacheCapacity
parameter_list|()
function_decl|;
comment|/**    * Get the total space used by the block pools of this namenode.    *    * @return the total space used by the block pools of this namenode.    */
DECL|method|getBlockPoolUsedSpace ()
name|long
name|getBlockPoolUsedSpace
parameter_list|()
function_decl|;
comment|/**    * Get the total space used by the block pool as percentage of total capacity.    *    * @return the total space used by the block pool as percentage of total    * capacity.    */
DECL|method|getPercentBlockPoolUsed ()
name|float
name|getPercentBlockPoolUsed
parameter_list|()
function_decl|;
comment|/**    * Gets the total numbers of blocks on the cluster.    *     * @return the total number of blocks of the cluster.    */
DECL|method|getTotalBlocks ()
name|long
name|getTotalBlocks
parameter_list|()
function_decl|;
comment|/**    * Gets the total number of missing blocks on the cluster.    *     * @return the total number of missing blocks on the cluster.    */
DECL|method|getNumberOfMissingBlocks ()
name|long
name|getNumberOfMissingBlocks
parameter_list|()
function_decl|;
comment|/**    * Gets the total number of missing blocks on the cluster with    * replication factor 1.    *    * @return the total number of missing blocks on the cluster with    * replication factor 1.    */
DECL|method|getNumberOfMissingBlocksWithReplicationFactorOne ()
name|long
name|getNumberOfMissingBlocksWithReplicationFactorOne
parameter_list|()
function_decl|;
comment|/**    * Gets the total number of replicated low redundancy blocks on the cluster    * with the highest risk of loss.    *    * @return the total number of low redundancy blocks on the cluster    * with the highest risk of loss.    */
DECL|method|getHighestPriorityLowRedundancyReplicatedBlocks ()
name|long
name|getHighestPriorityLowRedundancyReplicatedBlocks
parameter_list|()
function_decl|;
comment|/**    * Gets the total number of erasure coded low redundancy blocks on the cluster    * with the highest risk of loss.    *    * @return the total number of low redundancy blocks on the cluster    * with the highest risk of loss.    */
DECL|method|getHighestPriorityLowRedundancyECBlocks ()
name|long
name|getHighestPriorityLowRedundancyECBlocks
parameter_list|()
function_decl|;
comment|/**    * Gets the total number of snapshottable dirs in the system.    *    * @return the total number of snapshottable dirs in the system.    */
DECL|method|getNumberOfSnapshottableDirs ()
name|long
name|getNumberOfSnapshottableDirs
parameter_list|()
function_decl|;
comment|/**    * Gets the number of threads.    *     * @return the number of threads.    */
DECL|method|getThreads ()
name|int
name|getThreads
parameter_list|()
function_decl|;
comment|/**    * Gets the live node information of the cluster.    *     * @return the live node information.    */
DECL|method|getLiveNodes ()
name|String
name|getLiveNodes
parameter_list|()
function_decl|;
comment|/**    * Gets the dead node information of the cluster.    *     * @return the dead node information.    */
DECL|method|getDeadNodes ()
name|String
name|getDeadNodes
parameter_list|()
function_decl|;
comment|/**    * Gets the decommissioning node information of the cluster.    *     * @return the decommissioning node information.    */
DECL|method|getDecomNodes ()
name|String
name|getDecomNodes
parameter_list|()
function_decl|;
comment|/**    * Gets the information on nodes entering maintenance.    *    * @return the information on nodes entering maintenance.    */
DECL|method|getEnteringMaintenanceNodes ()
name|String
name|getEnteringMaintenanceNodes
parameter_list|()
function_decl|;
comment|/**    * Gets the cluster id.    *     * @return the cluster id.    */
DECL|method|getClusterId ()
name|String
name|getClusterId
parameter_list|()
function_decl|;
comment|/**    * Gets the block pool id.    *     * @return the block pool id.    */
DECL|method|getBlockPoolId ()
name|String
name|getBlockPoolId
parameter_list|()
function_decl|;
comment|/**    * Get status information about the directories storing image and edits logs    * of the NN.    *     * @return the name dir status information, as a JSON string.    */
DECL|method|getNameDirStatuses ()
name|String
name|getNameDirStatuses
parameter_list|()
function_decl|;
comment|/**    * Get Max, Median, Min and Standard Deviation of DataNodes usage.    *    * @return the DataNode usage information, as a JSON string.    */
DECL|method|getNodeUsage ()
name|String
name|getNodeUsage
parameter_list|()
function_decl|;
comment|/**    * Get status information about the journals of the NN.    *    * @return the name journal status information, as a JSON string.    */
DECL|method|getNameJournalStatus ()
name|String
name|getNameJournalStatus
parameter_list|()
function_decl|;
comment|/**    * Get information about the transaction ID, including the last applied     * transaction ID and the most recent checkpoint's transaction ID.    *    * @return information about the transaction ID.    */
DECL|method|getJournalTransactionInfo ()
name|String
name|getJournalTransactionInfo
parameter_list|()
function_decl|;
comment|/**    * Gets the NN start time in milliseconds.    *    * @return the NN start time in msec.    */
DECL|method|getNNStartedTimeInMillis ()
name|long
name|getNNStartedTimeInMillis
parameter_list|()
function_decl|;
comment|/**    * Get the compilation information which contains date, user and branch.    *    * @return the compilation information, as a JSON string.    */
DECL|method|getCompileInfo ()
name|String
name|getCompileInfo
parameter_list|()
function_decl|;
comment|/**    * Get the list of corrupt files.    *    * @return the list of corrupt files, as a JSON string.    */
DECL|method|getCorruptFiles ()
name|String
name|getCorruptFiles
parameter_list|()
function_decl|;
comment|/**    * Get the length of the list of corrupt files.    *    * @return the length of the list of corrupt files.    */
DECL|method|getCorruptFilesCount ()
name|int
name|getCorruptFilesCount
parameter_list|()
function_decl|;
comment|/**    * Get the number of distinct versions of live datanodes.    *     * @return the number of distinct versions of live datanodes.    */
DECL|method|getDistinctVersionCount ()
name|int
name|getDistinctVersionCount
parameter_list|()
function_decl|;
comment|/**    * Get the number of live datanodes for each distinct versions.    *     * @return the number of live datanodes for each distinct versions.    */
DECL|method|getDistinctVersions ()
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getDistinctVersions
parameter_list|()
function_decl|;
comment|/**    * Get namenode directory size.    *    * @return namenode directory size.    */
DECL|method|getNameDirSize ()
name|String
name|getNameDirSize
parameter_list|()
function_decl|;
comment|/**    * Verifies whether the cluster setup can support all enabled EC policies.    *    * @return the result of the verification.    */
DECL|method|getVerifyECWithTopologyResult ()
name|String
name|getVerifyECWithTopologyResult
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

