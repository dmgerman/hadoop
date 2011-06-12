begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.metrics
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

begin_comment
comment|/**  *   * This Interface defines the methods to get the status of a the FSNamesystem of  * a name node.  * It is also used for publishing via JMX (hence we follow the JMX naming  * convention.)  *   * Note we have not used the MetricsDynamicMBeanBase to implement this  * because the interface for the NameNodeStateMBean is stable and should  * be published as an interface.  *   *<p>  * Name Node runtime activity statistic  info is reported in  * @see org.apache.hadoop.hdfs.server.namenode.metrics.NameNodeMetrics  *  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|FSNamesystemMBean
specifier|public
interface|interface
name|FSNamesystemMBean
block|{
comment|/**    * The state of the file system: Safemode or Operational    * @return the state    */
DECL|method|getFSState ()
specifier|public
name|String
name|getFSState
parameter_list|()
function_decl|;
comment|/**    * Number of allocated blocks in the system    * @return -  number of allocated blocks    */
DECL|method|getBlocksTotal ()
specifier|public
name|long
name|getBlocksTotal
parameter_list|()
function_decl|;
comment|/**    * Total storage capacity    * @return -  total capacity in bytes    */
DECL|method|getCapacityTotal ()
specifier|public
name|long
name|getCapacityTotal
parameter_list|()
function_decl|;
comment|/**    * Free (unused) storage capacity    * @return -  free capacity in bytes    */
DECL|method|getCapacityRemaining ()
specifier|public
name|long
name|getCapacityRemaining
parameter_list|()
function_decl|;
comment|/**    * Used storage capacity    * @return -  used capacity in bytes    */
DECL|method|getCapacityUsed ()
specifier|public
name|long
name|getCapacityUsed
parameter_list|()
function_decl|;
comment|/**    * Total number of files and directories    * @return -  num of files and directories    */
DECL|method|getFilesTotal ()
specifier|public
name|long
name|getFilesTotal
parameter_list|()
function_decl|;
comment|/**    * Blocks pending to be replicated    * @return -  num of blocks to be replicated    */
DECL|method|getPendingReplicationBlocks ()
specifier|public
name|long
name|getPendingReplicationBlocks
parameter_list|()
function_decl|;
comment|/**    * Blocks under replicated     * @return -  num of blocks under replicated    */
DECL|method|getUnderReplicatedBlocks ()
specifier|public
name|long
name|getUnderReplicatedBlocks
parameter_list|()
function_decl|;
comment|/**    * Blocks scheduled for replication    * @return -  num of blocks scheduled for replication    */
DECL|method|getScheduledReplicationBlocks ()
specifier|public
name|long
name|getScheduledReplicationBlocks
parameter_list|()
function_decl|;
comment|/**    * Total Load on the FSNamesystem    * @return -  total load of FSNamesystem    */
DECL|method|getTotalLoad ()
specifier|public
name|int
name|getTotalLoad
parameter_list|()
function_decl|;
comment|/**    * Number of Live data nodes    * @return number of live data nodes    */
DECL|method|getNumLiveDataNodes ()
specifier|public
name|int
name|getNumLiveDataNodes
parameter_list|()
function_decl|;
comment|/**    * Number of dead data nodes    * @return number of dead data nodes    */
DECL|method|getNumDeadDataNodes ()
specifier|public
name|int
name|getNumDeadDataNodes
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

