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
comment|/**    * Total PROVIDED storage capacity.    * @return -  total PROVIDED storage capacity in bytes    */
DECL|method|getProvidedCapacityTotal ()
specifier|public
name|long
name|getProvidedCapacityTotal
parameter_list|()
function_decl|;
comment|/**    * Total number of files and directories    * @return -  num of files and directories    */
DECL|method|getFilesTotal ()
specifier|public
name|long
name|getFilesTotal
parameter_list|()
function_decl|;
comment|/**    * Get aggregated count of all blocks pending to be reconstructed.    * @deprecated Use {@link #getPendingReconstructionBlocks()} instead.    */
annotation|@
name|Deprecated
DECL|method|getPendingReplicationBlocks ()
specifier|public
name|long
name|getPendingReplicationBlocks
parameter_list|()
function_decl|;
comment|/**    * Get aggregated count of all blocks pending to be reconstructed.    * @return Number of blocks to be replicated.    */
DECL|method|getPendingReconstructionBlocks ()
specifier|public
name|long
name|getPendingReconstructionBlocks
parameter_list|()
function_decl|;
comment|/**    * Get aggregated count of all blocks with low redundancy.    * @deprecated Use {@link #getLowRedundancyBlocks()} instead.    */
annotation|@
name|Deprecated
DECL|method|getUnderReplicatedBlocks ()
specifier|public
name|long
name|getUnderReplicatedBlocks
parameter_list|()
function_decl|;
comment|/**    * Get aggregated count of all blocks with low redundancy.    * @return Number of blocks with low redundancy.    */
DECL|method|getLowRedundancyBlocks ()
specifier|public
name|long
name|getLowRedundancyBlocks
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
comment|/**    * Number of stale data nodes    * @return number of stale data nodes    */
DECL|method|getNumStaleDataNodes ()
specifier|public
name|int
name|getNumStaleDataNodes
parameter_list|()
function_decl|;
comment|/**    * Number of decommissioned Live data nodes    * @return number of decommissioned live data nodes    */
DECL|method|getNumDecomLiveDataNodes ()
specifier|public
name|int
name|getNumDecomLiveDataNodes
parameter_list|()
function_decl|;
comment|/**    * Number of decommissioned dead data nodes    * @return number of decommissioned dead data nodes    */
DECL|method|getNumDecomDeadDataNodes ()
specifier|public
name|int
name|getNumDecomDeadDataNodes
parameter_list|()
function_decl|;
comment|/**    * Number of failed data volumes across all live data nodes.    * @return number of failed data volumes across all live data nodes    */
DECL|method|getVolumeFailuresTotal ()
name|int
name|getVolumeFailuresTotal
parameter_list|()
function_decl|;
comment|/**    * Returns an estimate of total capacity lost due to volume failures in bytes    * across all live data nodes.    * @return estimate of total capacity lost in bytes    */
DECL|method|getEstimatedCapacityLostTotal ()
name|long
name|getEstimatedCapacityLostTotal
parameter_list|()
function_decl|;
comment|/**    * Number of data nodes that are in the decommissioning state    */
DECL|method|getNumDecommissioningDataNodes ()
specifier|public
name|int
name|getNumDecommissioningDataNodes
parameter_list|()
function_decl|;
comment|/**    * The statistics of snapshots    */
DECL|method|getSnapshotStats ()
specifier|public
name|String
name|getSnapshotStats
parameter_list|()
function_decl|;
comment|/**    * Return the maximum number of inodes in the file system    */
DECL|method|getMaxObjects ()
specifier|public
name|long
name|getMaxObjects
parameter_list|()
function_decl|;
comment|/**    * Number of blocks pending deletion    * @return number of blocks pending deletion    */
DECL|method|getPendingDeletionBlocks ()
name|long
name|getPendingDeletionBlocks
parameter_list|()
function_decl|;
comment|/**    * Time when block deletions will begin    * @return time when block deletions will begin    */
DECL|method|getBlockDeletionStartTime ()
name|long
name|getBlockDeletionStartTime
parameter_list|()
function_decl|;
comment|/**    * Number of content stale storages.    * @return number of content stale storages    */
DECL|method|getNumStaleStorages ()
specifier|public
name|int
name|getNumStaleStorages
parameter_list|()
function_decl|;
comment|/**    * Returns a nested JSON object listing the top users for different RPC     * operations over tracked time windows.    *     * @return JSON string    */
DECL|method|getTopUserOpCounts ()
specifier|public
name|String
name|getTopUserOpCounts
parameter_list|()
function_decl|;
comment|/**    * Return the number of encryption zones in the system.    */
DECL|method|getNumEncryptionZones ()
name|int
name|getNumEncryptionZones
parameter_list|()
function_decl|;
comment|/**    * Returns the length of the wait Queue for the FSNameSystemLock.    *    * A larger number here indicates lots of threads are waiting for    * FSNameSystemLock.    * @return int - Number of Threads waiting to acquire FSNameSystemLock    */
DECL|method|getFsLockQueueLength ()
name|int
name|getFsLockQueueLength
parameter_list|()
function_decl|;
comment|/**    * Return total number of Sync Operations on FSEditLog.    */
DECL|method|getTotalSyncCount ()
name|long
name|getTotalSyncCount
parameter_list|()
function_decl|;
comment|/**    * Return total time spent doing sync operations on FSEditLog.    */
DECL|method|getTotalSyncTimes ()
name|String
name|getTotalSyncTimes
parameter_list|()
function_decl|;
comment|/**    * @return Number of IN_MAINTENANCE live data nodes    */
DECL|method|getNumInMaintenanceLiveDataNodes ()
name|int
name|getNumInMaintenanceLiveDataNodes
parameter_list|()
function_decl|;
comment|/**    * @return Number of IN_MAINTENANCE dead data nodes    */
DECL|method|getNumInMaintenanceDeadDataNodes ()
name|int
name|getNumInMaintenanceDeadDataNodes
parameter_list|()
function_decl|;
comment|/**    * @return Number of ENTERING_MAINTENANCE data nodes    */
DECL|method|getNumEnteringMaintenanceDataNodes ()
name|int
name|getNumEnteringMaintenanceDataNodes
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

