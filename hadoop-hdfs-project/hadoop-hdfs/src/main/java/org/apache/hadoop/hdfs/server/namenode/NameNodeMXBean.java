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
comment|/**  * This is the JMX management interface for namenode information  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|NameNodeMXBean
specifier|public
interface|interface
name|NameNodeMXBean
block|{
comment|/**    * Gets the version of Hadoop.    *     * @return the version    */
DECL|method|getVersion ()
specifier|public
name|String
name|getVersion
parameter_list|()
function_decl|;
comment|/**    * Gets the used space by data nodes.    *     * @return the used space by data nodes    */
DECL|method|getUsed ()
specifier|public
name|long
name|getUsed
parameter_list|()
function_decl|;
comment|/**    * Gets total non-used raw bytes.    *     * @return total non-used raw bytes    */
DECL|method|getFree ()
specifier|public
name|long
name|getFree
parameter_list|()
function_decl|;
comment|/**    * Gets total raw bytes including non-dfs used space.    *     * @return the total raw bytes including non-dfs used space    */
DECL|method|getTotal ()
specifier|public
name|long
name|getTotal
parameter_list|()
function_decl|;
comment|/**    * Gets the safemode status    *     * @return the safemode status    *     */
DECL|method|getSafemode ()
specifier|public
name|String
name|getSafemode
parameter_list|()
function_decl|;
comment|/**    * Checks if upgrade is finalized.    *     * @return true, if upgrade is finalized    */
DECL|method|isUpgradeFinalized ()
specifier|public
name|boolean
name|isUpgradeFinalized
parameter_list|()
function_decl|;
comment|/**    * Gets total used space by data nodes for non DFS purposes such as storing    * temporary files on the local file system    *     * @return the non dfs space of the cluster    */
DECL|method|getNonDfsUsedSpace ()
specifier|public
name|long
name|getNonDfsUsedSpace
parameter_list|()
function_decl|;
comment|/**    * Gets the total used space by data nodes as percentage of total capacity    *     * @return the percentage of used space on the cluster.    */
DECL|method|getPercentUsed ()
specifier|public
name|float
name|getPercentUsed
parameter_list|()
function_decl|;
comment|/**    * Gets the total remaining space by data nodes as percentage of total     * capacity    *     * @return the percentage of the remaining space on the cluster    */
DECL|method|getPercentRemaining ()
specifier|public
name|float
name|getPercentRemaining
parameter_list|()
function_decl|;
comment|/**    * Get the total space used by the block pools of this namenode    */
DECL|method|getBlockPoolUsedSpace ()
specifier|public
name|long
name|getBlockPoolUsedSpace
parameter_list|()
function_decl|;
comment|/**    * Get the total space used by the block pool as percentage of total capacity    */
DECL|method|getPercentBlockPoolUsed ()
specifier|public
name|float
name|getPercentBlockPoolUsed
parameter_list|()
function_decl|;
comment|/**    * Gets the total numbers of blocks on the cluster.    *     * @return the total number of blocks of the cluster    */
DECL|method|getTotalBlocks ()
specifier|public
name|long
name|getTotalBlocks
parameter_list|()
function_decl|;
comment|/**    * Gets the total number of files on the cluster    *     * @return the total number of files on the cluster    */
DECL|method|getTotalFiles ()
specifier|public
name|long
name|getTotalFiles
parameter_list|()
function_decl|;
comment|/**    * Gets the total number of missing blocks on the cluster    *     * @return the total number of files and blocks on the cluster    */
DECL|method|getNumberOfMissingBlocks ()
specifier|public
name|long
name|getNumberOfMissingBlocks
parameter_list|()
function_decl|;
comment|/**    * Gets the number of threads.    *     * @return the number of threads    */
DECL|method|getThreads ()
specifier|public
name|int
name|getThreads
parameter_list|()
function_decl|;
comment|/**    * Gets the live node information of the cluster.    *     * @return the live node information    */
DECL|method|getLiveNodes ()
specifier|public
name|String
name|getLiveNodes
parameter_list|()
function_decl|;
comment|/**    * Gets the dead node information of the cluster.    *     * @return the dead node information    */
DECL|method|getDeadNodes ()
specifier|public
name|String
name|getDeadNodes
parameter_list|()
function_decl|;
comment|/**    * Gets the decommissioning node information of the cluster.    *     * @return the decommissioning node information    */
DECL|method|getDecomNodes ()
specifier|public
name|String
name|getDecomNodes
parameter_list|()
function_decl|;
comment|/**    * Gets the cluster id.    *     * @return the cluster id    */
DECL|method|getClusterId ()
specifier|public
name|String
name|getClusterId
parameter_list|()
function_decl|;
comment|/**    * Gets the block pool id.    *     * @return the block pool id    */
DECL|method|getBlockPoolId ()
specifier|public
name|String
name|getBlockPoolId
parameter_list|()
function_decl|;
comment|/**    * Get status information about the directories storing image and edits logs    * of the NN.    *     * @return the name dir status information, as a JSON string.    */
DECL|method|getNameDirStatuses ()
specifier|public
name|String
name|getNameDirStatuses
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

